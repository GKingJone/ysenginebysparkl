/*     */ package com.facebook.presto.jdbc.internal.jetty.io;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Scheduler;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.ByteBuffer;
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
/*     */ public abstract class AbstractEndPoint
/*     */   extends IdleTimeout
/*     */   implements EndPoint
/*     */ {
/*  34 */   private static final Logger LOG = Log.getLogger(AbstractEndPoint.class);
/*  35 */   private final long _created = System.currentTimeMillis();
/*     */   
/*     */   private final InetSocketAddress _local;
/*     */   private final InetSocketAddress _remote;
/*     */   private volatile Connection _connection;
/*  40 */   private final FillInterest _fillInterest = new FillInterest()
/*     */   {
/*     */     protected void needsFillInterest()
/*     */       throws IOException
/*     */     {
/*  45 */       AbstractEndPoint.this.needsFillInterest();
/*     */     }
/*     */   };
/*     */   
/*  49 */   private final WriteFlusher _writeFlusher = new WriteFlusher(this)
/*     */   {
/*     */ 
/*     */     protected void onIncompleteFlush()
/*     */     {
/*  54 */       AbstractEndPoint.this.onIncompleteFlush();
/*     */     }
/*     */   };
/*     */   
/*     */   protected AbstractEndPoint(Scheduler scheduler, InetSocketAddress local, InetSocketAddress remote)
/*     */   {
/*  60 */     super(scheduler);
/*  61 */     this._local = local;
/*  62 */     this._remote = remote;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getCreatedTimeStamp()
/*     */   {
/*  68 */     return this._created;
/*     */   }
/*     */   
/*     */ 
/*     */   public InetSocketAddress getLocalAddress()
/*     */   {
/*  74 */     return this._local;
/*     */   }
/*     */   
/*     */ 
/*     */   public InetSocketAddress getRemoteAddress()
/*     */   {
/*  80 */     return this._remote;
/*     */   }
/*     */   
/*     */ 
/*     */   public Connection getConnection()
/*     */   {
/*  86 */     return this._connection;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setConnection(Connection connection)
/*     */   {
/*  92 */     this._connection = connection;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isOptimizedForDirectBuffers()
/*     */   {
/*  98 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public void onOpen()
/*     */   {
/* 104 */     if (LOG.isDebugEnabled())
/* 105 */       LOG.debug("onOpen {}", new Object[] { this });
/* 106 */     super.onOpen();
/*     */   }
/*     */   
/*     */ 
/*     */   public void onClose()
/*     */   {
/* 112 */     super.onClose();
/* 113 */     if (LOG.isDebugEnabled())
/* 114 */       LOG.debug("onClose {}", new Object[] { this });
/* 115 */     this._writeFlusher.onClose();
/* 116 */     this._fillInterest.onClose();
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */   {
/* 122 */     onClose();
/*     */   }
/*     */   
/*     */   public void fillInterested(Callback callback)
/*     */     throws IllegalStateException
/*     */   {
/* 128 */     notIdle();
/* 129 */     this._fillInterest.register(callback);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isFillInterested()
/*     */   {
/* 135 */     return this._fillInterest.isInterested();
/*     */   }
/*     */   
/*     */   public void write(Callback callback, ByteBuffer... buffers)
/*     */     throws IllegalStateException
/*     */   {
/* 141 */     this._writeFlusher.write(callback, buffers);
/*     */   }
/*     */   
/*     */   protected abstract void onIncompleteFlush();
/*     */   
/*     */   protected abstract void needsFillInterest() throws IOException;
/*     */   
/*     */   public FillInterest getFillInterest()
/*     */   {
/* 150 */     return this._fillInterest;
/*     */   }
/*     */   
/*     */   protected WriteFlusher getWriteFlusher()
/*     */   {
/* 155 */     return this._writeFlusher;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void onIdleExpired(TimeoutException timeout)
/*     */   {
/* 161 */     Connection connection = this._connection;
/* 162 */     if ((connection != null) && (!connection.onIdleExpired())) {
/* 163 */       return;
/*     */     }
/* 165 */     boolean output_shutdown = isOutputShutdown();
/* 166 */     boolean input_shutdown = isInputShutdown();
/* 167 */     boolean fillFailed = this._fillInterest.onFail(timeout);
/* 168 */     boolean writeFailed = this._writeFlusher.onFail(timeout);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 177 */     if ((isOpen()) && ((output_shutdown) || (input_shutdown)) && (!fillFailed) && (!writeFailed)) {
/* 178 */       close();
/*     */     } else {
/* 180 */       LOG.debug("Ignored idle endpoint {}", new Object[] { this });
/*     */     }
/*     */   }
/*     */   
/*     */   public void upgrade(Connection newConnection)
/*     */   {
/* 186 */     Connection old_connection = getConnection();
/*     */     
/* 188 */     if (LOG.isDebugEnabled()) {
/* 189 */       LOG.debug("{} upgrading from {} to {}", new Object[] { this, old_connection, newConnection });
/*     */     }
/*     */     
/* 192 */     ByteBuffer prefilled = (old_connection instanceof Connection.UpgradeFrom) ? ((Connection.UpgradeFrom)old_connection).onUpgradeFrom() : null;
/* 193 */     old_connection.onClose();
/* 194 */     old_connection.getEndPoint().setConnection(newConnection);
/*     */     
/* 196 */     if ((newConnection instanceof Connection.UpgradeTo)) {
/* 197 */       ((Connection.UpgradeTo)newConnection).onUpgradeTo(prefilled);
/* 198 */     } else if (BufferUtil.hasContent(prefilled)) {
/* 199 */       throw new IllegalStateException();
/*     */     }
/* 201 */     newConnection.onOpen();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 207 */     Class<?> c = getClass();
/* 208 */     String name = c.getSimpleName();
/* 209 */     while ((name.length() == 0) && (c.getSuperclass() != null))
/*     */     {
/* 211 */       c = c.getSuperclass();
/* 212 */       name = c.getSimpleName();
/*     */     }
/*     */     
/* 215 */     return String.format("%s@%x{%s<->%d,%s,%s,%s,%s,%s,%d/%d,%s}", new Object[] { name, 
/*     */     
/* 217 */       Integer.valueOf(hashCode()), 
/* 218 */       getRemoteAddress(), 
/* 219 */       Integer.valueOf(getLocalAddress().getPort()), 
/* 220 */       isOpen() ? "Open" : "CLOSED", 
/* 221 */       isInputShutdown() ? "ISHUT" : "in", 
/* 222 */       isOutputShutdown() ? "OSHUT" : "out", this._fillInterest
/* 223 */       .toStateString(), this._writeFlusher
/* 224 */       .toStateString(), 
/* 225 */       Long.valueOf(getIdleFor()), 
/* 226 */       Long.valueOf(getIdleTimeout()), 
/* 227 */       getConnection() == null ? null : getConnection().getClass().getSimpleName() });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\AbstractEndPoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */