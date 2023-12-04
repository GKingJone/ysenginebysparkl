/*     */ package com.facebook.presto.jdbc.internal.jetty.util.thread;
/*     */ 
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import java.util.concurrent.locks.ReentrantLock;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Locker
/*     */ {
/*  39 */   private static final boolean SPIN = Boolean.getBoolean(Locker.class.getName() + ".spin");
/*     */   
/*     */   private final boolean _spin;
/*  42 */   private final ReentrantLock _lock = new ReentrantLock();
/*  43 */   private final AtomicReference<Thread> _spinLockState = new AtomicReference(null);
/*  44 */   private final Lock _unlock = new Lock();
/*     */   
/*     */   public Locker()
/*     */   {
/*  48 */     this(SPIN);
/*     */   }
/*     */   
/*     */   public Locker(boolean spin)
/*     */   {
/*  53 */     this._spin = spin;
/*     */   }
/*     */   
/*     */   public Lock lock()
/*     */   {
/*  58 */     if (this._spin) {
/*  59 */       spinLock();
/*     */     } else
/*  61 */       concLock();
/*  62 */     return this._unlock;
/*     */   }
/*     */   
/*     */   private void spinLock()
/*     */   {
/*  67 */     Thread current = Thread.currentThread();
/*     */     Thread locker;
/*     */     do
/*     */     {
/*  71 */       locker = (Thread)this._spinLockState.get();
/*  72 */       if ((locker == null) && (this._spinLockState.compareAndSet(null, current)))
/*     */         break;
/*  74 */     } while (locker != current);
/*  75 */     throw new IllegalStateException("Locker is not reentrant");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void concLock()
/*     */   {
/*  84 */     if (this._lock.isHeldByCurrentThread())
/*  85 */       throw new IllegalStateException("Locker is not reentrant");
/*  86 */     this._lock.lock();
/*     */   }
/*     */   
/*     */   public boolean isLocked()
/*     */   {
/*  91 */     if (this._spin) {
/*  92 */       return this._spinLockState.get() != null;
/*     */     }
/*  94 */     return this._lock.isLocked();
/*     */   }
/*     */   
/*     */   public class Lock implements AutoCloseable
/*     */   {
/*     */     public Lock() {}
/*     */     
/*     */     public void close() {
/* 102 */       if (Locker.this._spin) {
/* 103 */         Locker.this._spinLockState.set(null);
/*     */       } else {
/* 105 */         Locker.this._lock.unlock();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\thread\Locker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */