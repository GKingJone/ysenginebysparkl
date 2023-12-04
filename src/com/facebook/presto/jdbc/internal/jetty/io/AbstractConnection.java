/*     */ package com.facebook.presto.jdbc.internal.jetty.io;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.RejectedExecutionException;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractConnection
/*     */   implements Connection
/*     */ {
/*  40 */   private static final Logger LOG = Log.getLogger(AbstractConnection.class);
/*     */   
/*  42 */   private final List<Listener> listeners = new CopyOnWriteArrayList();
/*  43 */   private final long _created = System.currentTimeMillis();
/*     */   private final EndPoint _endPoint;
/*     */   private final Executor _executor;
/*     */   private final Callback _readCallback;
/*  47 */   private int _inputBufferSize = 2048;
/*     */   
/*     */   protected AbstractConnection(EndPoint endp, Executor executor)
/*     */   {
/*  51 */     if (executor == null)
/*  52 */       throw new IllegalArgumentException("Executor must not be null!");
/*  53 */     this._endPoint = endp;
/*  54 */     this._executor = executor;
/*  55 */     this._readCallback = new ReadCallback(null);
/*     */   }
/*     */   
/*     */ 
/*     */   public void addListener(Listener listener)
/*     */   {
/*  61 */     this.listeners.add(listener);
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeListener(Listener listener)
/*     */   {
/*  67 */     this.listeners.remove(listener);
/*     */   }
/*     */   
/*     */   public int getInputBufferSize()
/*     */   {
/*  72 */     return this._inputBufferSize;
/*     */   }
/*     */   
/*     */   public void setInputBufferSize(int inputBufferSize)
/*     */   {
/*  77 */     this._inputBufferSize = inputBufferSize;
/*     */   }
/*     */   
/*     */   protected Executor getExecutor()
/*     */   {
/*  82 */     return this._executor;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public boolean isDispatchIO()
/*     */   {
/*  88 */     return false;
/*     */   }
/*     */   
/*     */   protected void failedCallback(final Callback callback, final Throwable x)
/*     */   {
/*  93 */     if (callback.isNonBlocking())
/*     */     {
/*     */       try
/*     */       {
/*  97 */         callback.failed(x);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 101 */         LOG.warn(e);
/*     */       }
/*     */       
/*     */     }
/*     */     else {
/*     */       try
/*     */       {
/* 108 */         getExecutor().execute(new Runnable()
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/*     */             try
/*     */             {
/* 115 */               callback.failed(x);
/*     */             }
/*     */             catch (Exception e)
/*     */             {
/* 119 */               AbstractConnection.LOG.warn(e);
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */       catch (RejectedExecutionException e)
/*     */       {
/* 126 */         LOG.debug(e);
/* 127 */         callback.failed(x);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void fillInterested()
/*     */   {
/* 140 */     if (LOG.isDebugEnabled())
/* 141 */       LOG.debug("fillInterested {}", new Object[] { this });
/* 142 */     getEndPoint().fillInterested(this._readCallback);
/*     */   }
/*     */   
/*     */   public boolean isFillInterested()
/*     */   {
/* 147 */     return getEndPoint().isFillInterested();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void onFillable();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void onFillInterestedFailed(Throwable cause)
/*     */   {
/* 162 */     if (LOG.isDebugEnabled())
/* 163 */       LOG.debug("{} onFillInterestedFailed {}", new Object[] { this, cause });
/* 164 */     if (this._endPoint.isOpen())
/*     */     {
/* 166 */       boolean close = true;
/* 167 */       if ((cause instanceof TimeoutException))
/* 168 */         close = onReadTimeout();
/* 169 */       if (close)
/*     */       {
/* 171 */         if (this._endPoint.isOutputShutdown()) {
/* 172 */           this._endPoint.close();
/*     */         }
/*     */         else {
/* 175 */           this._endPoint.shutdownOutput();
/* 176 */           fillInterested();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean onReadTimeout()
/*     */   {
/* 188 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public void onOpen()
/*     */   {
/* 194 */     if (LOG.isDebugEnabled()) {
/* 195 */       LOG.debug("onOpen {}", new Object[] { this });
/*     */     }
/* 197 */     for (Listener listener : this.listeners) {
/* 198 */       listener.onOpened(this);
/*     */     }
/*     */   }
/*     */   
/*     */   public void onClose()
/*     */   {
/* 204 */     if (LOG.isDebugEnabled()) {
/* 205 */       LOG.debug("onClose {}", new Object[] { this });
/*     */     }
/* 207 */     for (Listener listener : this.listeners) {
/* 208 */       listener.onClosed(this);
/*     */     }
/*     */   }
/*     */   
/*     */   public EndPoint getEndPoint()
/*     */   {
/* 214 */     return this._endPoint;
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */   {
/* 220 */     getEndPoint().close();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean onIdleExpired()
/*     */   {
/* 226 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMessagesIn()
/*     */   {
/* 232 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMessagesOut()
/*     */   {
/* 238 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getBytesIn()
/*     */   {
/* 244 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getBytesOut()
/*     */   {
/* 250 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getCreatedTimeStamp()
/*     */   {
/* 256 */     return this._created;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 262 */     return String.format("%s@%x[%s]", new Object[] {
/* 263 */       getClass().getSimpleName(), 
/* 264 */       Integer.valueOf(hashCode()), this._endPoint });
/*     */   }
/*     */   
/*     */   private class ReadCallback implements Callback
/*     */   {
/*     */     private ReadCallback() {}
/*     */     
/*     */     public void succeeded()
/*     */     {
/* 273 */       AbstractConnection.this.onFillable();
/*     */     }
/*     */     
/*     */ 
/*     */     public void failed(Throwable x)
/*     */     {
/* 279 */       AbstractConnection.this.onFillInterestedFailed(x);
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 285 */       return String.format("AC.ReadCB@%x{%s}", new Object[] { Integer.valueOf(AbstractConnection.this.hashCode()), AbstractConnection.this });
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\AbstractConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */