/*     */ package com.facebook.presto.jdbc.internal.jetty.io;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Scheduler;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Scheduler.Task;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class IdleTimeout
/*     */ {
/*  39 */   private static final Logger LOG = Log.getLogger(IdleTimeout.class);
/*     */   private final Scheduler _scheduler;
/*  41 */   private final AtomicReference<Scheduler.Task> _timeout = new AtomicReference();
/*     */   private volatile long _idleTimeout;
/*  43 */   private volatile long _idleTimestamp = System.currentTimeMillis();
/*     */   
/*  45 */   private final Runnable _idleTask = new Runnable()
/*     */   {
/*     */ 
/*     */     public void run()
/*     */     {
/*  50 */       long idleLeft = IdleTimeout.this.checkIdleTimeout();
/*  51 */       if (idleLeft >= 0L) {
/*  52 */         IdleTimeout.this.scheduleIdleTimeout(idleLeft > 0L ? idleLeft : IdleTimeout.this.getIdleTimeout());
/*     */       }
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */   public IdleTimeout(Scheduler scheduler)
/*     */   {
/*  61 */     this._scheduler = scheduler;
/*     */   }
/*     */   
/*     */   public Scheduler getScheduler()
/*     */   {
/*  66 */     return this._scheduler;
/*     */   }
/*     */   
/*     */   public long getIdleTimestamp()
/*     */   {
/*  71 */     return this._idleTimestamp;
/*     */   }
/*     */   
/*     */   public long getIdleFor()
/*     */   {
/*  76 */     return System.currentTimeMillis() - getIdleTimestamp();
/*     */   }
/*     */   
/*     */   public long getIdleTimeout()
/*     */   {
/*  81 */     return this._idleTimeout;
/*     */   }
/*     */   
/*     */   public void setIdleTimeout(long idleTimeout)
/*     */   {
/*  86 */     long old = this._idleTimeout;
/*  87 */     this._idleTimeout = idleTimeout;
/*     */     
/*     */ 
/*  90 */     if (old > 0L)
/*     */     {
/*     */ 
/*  93 */       if (old <= idleTimeout) {
/*  94 */         return;
/*     */       }
/*     */       
/*  97 */       deactivate();
/*     */     }
/*     */     
/*     */ 
/* 101 */     if (isOpen()) {
/* 102 */       activate();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void notIdle()
/*     */   {
/* 110 */     this._idleTimestamp = System.currentTimeMillis();
/*     */   }
/*     */   
/*     */   private void scheduleIdleTimeout(long delay)
/*     */   {
/* 115 */     Scheduler.Task newTimeout = null;
/* 116 */     if ((isOpen()) && (delay > 0L) && (this._scheduler != null))
/* 117 */       newTimeout = this._scheduler.schedule(this._idleTask, delay, TimeUnit.MILLISECONDS);
/* 118 */     Scheduler.Task oldTimeout = (Scheduler.Task)this._timeout.getAndSet(newTimeout);
/* 119 */     if (oldTimeout != null) {
/* 120 */       oldTimeout.cancel();
/*     */     }
/*     */   }
/*     */   
/*     */   public void onOpen() {
/* 125 */     activate();
/*     */   }
/*     */   
/*     */   private void activate()
/*     */   {
/* 130 */     if (this._idleTimeout > 0L) {
/* 131 */       this._idleTask.run();
/*     */     }
/*     */   }
/*     */   
/*     */   public void onClose() {
/* 136 */     deactivate();
/*     */   }
/*     */   
/*     */   private void deactivate()
/*     */   {
/* 141 */     Scheduler.Task oldTimeout = (Scheduler.Task)this._timeout.getAndSet(null);
/* 142 */     if (oldTimeout != null) {
/* 143 */       oldTimeout.cancel();
/*     */     }
/*     */   }
/*     */   
/*     */   protected long checkIdleTimeout() {
/* 148 */     if (isOpen())
/*     */     {
/* 150 */       long idleTimestamp = getIdleTimestamp();
/* 151 */       long idleTimeout = getIdleTimeout();
/* 152 */       long idleElapsed = System.currentTimeMillis() - idleTimestamp;
/* 153 */       long idleLeft = idleTimeout - idleElapsed;
/*     */       
/* 155 */       if (LOG.isDebugEnabled()) {
/* 156 */         LOG.debug("{} idle timeout check, elapsed: {} ms, remaining: {} ms", new Object[] { this, Long.valueOf(idleElapsed), Long.valueOf(idleLeft) });
/*     */       }
/* 158 */       if ((idleTimestamp != 0L) && (idleTimeout > 0L))
/*     */       {
/* 160 */         if (idleLeft <= 0L)
/*     */         {
/* 162 */           if (LOG.isDebugEnabled()) {
/* 163 */             LOG.debug("{} idle timeout expired", new Object[] { this });
/*     */           }
/*     */           try {
/* 166 */             onIdleExpired(new TimeoutException("Idle timeout expired: " + idleElapsed + "/" + idleTimeout + " ms"));
/*     */           }
/*     */           finally
/*     */           {
/* 170 */             notIdle();
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 175 */       return idleLeft >= 0L ? idleLeft : 0L;
/*     */     }
/* 177 */     return -1L;
/*     */   }
/*     */   
/*     */   protected abstract void onIdleExpired(TimeoutException paramTimeoutException);
/*     */   
/*     */   public abstract boolean isOpen();
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\IdleTimeout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */