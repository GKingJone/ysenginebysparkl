/*     */ package com.facebook.presto.jdbc.internal.jetty.io;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.ReadPendingException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.concurrent.atomic.AtomicReference;
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
/*     */ public abstract class FillInterest
/*     */ {
/*  38 */   private static final Logger LOG = Log.getLogger(FillInterest.class);
/*  39 */   private final AtomicReference<Callback> _interested = new AtomicReference(null);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Throwable _lastSet;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void register(Callback callback)
/*     */     throws ReadPendingException
/*     */   {
/*  56 */     if (callback == null) {
/*  57 */       throw new IllegalArgumentException();
/*     */     }
/*  59 */     if (this._interested.compareAndSet(null, callback))
/*     */     {
/*  61 */       if (LOG.isDebugEnabled())
/*     */       {
/*  63 */         LOG.debug("{} register {}", new Object[] { this, callback });
/*  64 */         this._lastSet = new Throwable(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + ":" + Thread.currentThread().getName());
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*  69 */       LOG.warn("Read pending for {} prevented {}", new Object[] { this._interested, callback });
/*  70 */       if (LOG.isDebugEnabled())
/*  71 */         LOG.warn("callback set at ", this._lastSet);
/*  72 */       throw new ReadPendingException();
/*     */     }
/*     */     try
/*     */     {
/*  76 */       if (LOG.isDebugEnabled())
/*  77 */         LOG.debug("{} register {}", new Object[] { this, callback });
/*  78 */       needsFillInterest();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  82 */       onFail(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void fillable()
/*     */   {
/*  91 */     Callback callback = (Callback)this._interested.get();
/*  92 */     if (LOG.isDebugEnabled())
/*  93 */       LOG.debug("{} fillable {}", new Object[] { this, callback });
/*  94 */     if ((callback != null) && (this._interested.compareAndSet(callback, null))) {
/*  95 */       callback.succeeded();
/*  96 */     } else if (LOG.isDebugEnabled()) {
/*  97 */       LOG.debug("{} lost race {}", new Object[] { this, callback });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isInterested()
/*     */   {
/* 105 */     return this._interested.get() != null;
/*     */   }
/*     */   
/*     */   public boolean isCallbackNonBlocking()
/*     */   {
/* 110 */     Callback callback = (Callback)this._interested.get();
/* 111 */     return (callback != null) && (callback.isNonBlocking());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean onFail(Throwable cause)
/*     */   {
/* 122 */     Callback callback = (Callback)this._interested.get();
/* 123 */     if ((callback != null) && (this._interested.compareAndSet(callback, null)))
/*     */     {
/* 125 */       callback.failed(cause);
/* 126 */       return true;
/*     */     }
/* 128 */     return false;
/*     */   }
/*     */   
/*     */   public void onClose()
/*     */   {
/* 133 */     Callback callback = (Callback)this._interested.get();
/* 134 */     if ((callback != null) && (this._interested.compareAndSet(callback, null))) {
/* 135 */       callback.failed(new ClosedChannelException());
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 141 */     return String.format("FillInterest@%x{%b,%s}", new Object[] { Integer.valueOf(hashCode()), Boolean.valueOf(this._interested.get() != null ? 1 : false), this._interested.get() });
/*     */   }
/*     */   
/*     */ 
/*     */   public String toStateString()
/*     */   {
/* 147 */     return this._interested.get() == null ? "-" : "FI";
/*     */   }
/*     */   
/*     */   protected abstract void needsFillInterest()
/*     */     throws IOException;
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\FillInterest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */