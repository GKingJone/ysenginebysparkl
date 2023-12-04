/*     */ package com.facebook.presto.jdbc.internal.guava.util.concurrent;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.VisibleForTesting;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Stopwatch;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.annotation.concurrent.ThreadSafe;
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
/*     */ @ThreadSafe
/*     */ @Beta
/*     */ public abstract class RateLimiter
/*     */ {
/*     */   private final SleepingStopwatch stopwatch;
/*     */   private volatile Object mutexDoNotUseDirectly;
/*     */   
/*     */   public static RateLimiter create(double permitsPerSecond)
/*     */   {
/* 129 */     return create(SleepingStopwatch.createFromSystemTimer(), permitsPerSecond);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @VisibleForTesting
/*     */   static RateLimiter create(SleepingStopwatch stopwatch, double permitsPerSecond)
/*     */   {
/* 138 */     RateLimiter rateLimiter = new SmoothRateLimiter.SmoothBursty(stopwatch, 1.0D);
/* 139 */     rateLimiter.setRate(permitsPerSecond);
/* 140 */     return rateLimiter;
/*     */   }
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
/*     */   public static RateLimiter create(double permitsPerSecond, long warmupPeriod, TimeUnit unit)
/*     */   {
/* 168 */     Preconditions.checkArgument(warmupPeriod >= 0L, "warmupPeriod must not be negative: %s", new Object[] { Long.valueOf(warmupPeriod) });
/* 169 */     return create(SleepingStopwatch.createFromSystemTimer(), permitsPerSecond, warmupPeriod, unit);
/*     */   }
/*     */   
/*     */   @VisibleForTesting
/*     */   static RateLimiter create(SleepingStopwatch stopwatch, double permitsPerSecond, long warmupPeriod, TimeUnit unit)
/*     */   {
/* 175 */     RateLimiter rateLimiter = new SmoothRateLimiter.SmoothWarmingUp(stopwatch, warmupPeriod, unit);
/* 176 */     rateLimiter.setRate(permitsPerSecond);
/* 177 */     return rateLimiter;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Object mutex()
/*     */   {
/* 190 */     Object mutex = this.mutexDoNotUseDirectly;
/* 191 */     if (mutex == null) {
/* 192 */       synchronized (this) {
/* 193 */         mutex = this.mutexDoNotUseDirectly;
/* 194 */         if (mutex == null) {
/* 195 */           this.mutexDoNotUseDirectly = (mutex = new Object());
/*     */         }
/*     */       }
/*     */     }
/* 199 */     return mutex;
/*     */   }
/*     */   
/*     */   RateLimiter(SleepingStopwatch stopwatch) {
/* 203 */     this.stopwatch = ((SleepingStopwatch)Preconditions.checkNotNull(stopwatch));
/*     */   }
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
/*     */   public final void setRate(double permitsPerSecond)
/*     */   {
/* 226 */     Preconditions.checkArgument((permitsPerSecond > 0.0D) && (!Double.isNaN(permitsPerSecond)), "rate must be positive");
/*     */     
/* 228 */     synchronized (mutex()) {
/* 229 */       doSetRate(permitsPerSecond, this.stopwatch.readMicros());
/*     */     }
/*     */   }
/*     */   
/*     */   abstract void doSetRate(double paramDouble, long paramLong);
/*     */   
/*     */   /* Error */
/*     */   public final double getRate()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokespecial 99	com/facebook/presto/jdbc/internal/guava/util/concurrent/RateLimiter:mutex	()Ljava/lang/Object;
/*     */     //   4: dup
/*     */     //   5: astore_1
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: invokevirtual 112	com/facebook/presto/jdbc/internal/guava/util/concurrent/RateLimiter:doGetRate	()D
/*     */     //   11: aload_1
/*     */     //   12: monitorexit
/*     */     //   13: dreturn
/*     */     //   14: astore_2
/*     */     //   15: aload_1
/*     */     //   16: monitorexit
/*     */     //   17: aload_2
/*     */     //   18: athrow
/*     */     // Line number table:
/*     */     //   Java source line #243	-> byte code offset #0
/*     */     //   Java source line #244	-> byte code offset #7
/*     */     //   Java source line #245	-> byte code offset #14
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	19	0	this	RateLimiter
/*     */     //   5	11	1	Ljava/lang/Object;	Object
/*     */     //   14	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	13	14	finally
/*     */     //   14	17	14	finally
/*     */   }
/*     */   
/*     */   abstract double doGetRate();
/*     */   
/*     */   public double acquire()
/*     */   {
/* 260 */     return acquire(1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double acquire(int permits)
/*     */   {
/* 273 */     long microsToWait = reserve(permits);
/* 274 */     this.stopwatch.sleepMicrosUninterruptibly(microsToWait);
/* 275 */     return 1.0D * microsToWait / TimeUnit.SECONDS.toMicros(1L);
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   final long reserve(int permits)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: iload_1
/*     */     //   1: invokestatic 140	com/facebook/presto/jdbc/internal/guava/util/concurrent/RateLimiter:checkPermits	(I)I
/*     */     //   4: pop
/*     */     //   5: aload_0
/*     */     //   6: invokespecial 99	com/facebook/presto/jdbc/internal/guava/util/concurrent/RateLimiter:mutex	()Ljava/lang/Object;
/*     */     //   9: dup
/*     */     //   10: astore_2
/*     */     //   11: monitorenter
/*     */     //   12: aload_0
/*     */     //   13: iload_1
/*     */     //   14: aload_0
/*     */     //   15: getfield 86	com/facebook/presto/jdbc/internal/guava/util/concurrent/RateLimiter:stopwatch	Lcom/facebook/presto/jdbc/internal/guava/util/concurrent/RateLimiter$SleepingStopwatch;
/*     */     //   18: invokevirtual 103	com/facebook/presto/jdbc/internal/guava/util/concurrent/RateLimiter$SleepingStopwatch:readMicros	()J
/*     */     //   21: invokevirtual 144	com/facebook/presto/jdbc/internal/guava/util/concurrent/RateLimiter:reserveAndGetWaitLength	(IJ)J
/*     */     //   24: aload_2
/*     */     //   25: monitorexit
/*     */     //   26: lreturn
/*     */     //   27: astore_3
/*     */     //   28: aload_2
/*     */     //   29: monitorexit
/*     */     //   30: aload_3
/*     */     //   31: athrow
/*     */     // Line number table:
/*     */     //   Java source line #285	-> byte code offset #0
/*     */     //   Java source line #286	-> byte code offset #5
/*     */     //   Java source line #287	-> byte code offset #12
/*     */     //   Java source line #288	-> byte code offset #27
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	32	0	this	RateLimiter
/*     */     //   0	32	1	permits	int
/*     */     //   10	19	2	Ljava/lang/Object;	Object
/*     */     //   27	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   12	26	27	finally
/*     */     //   27	30	27	finally
/*     */   }
/*     */   
/*     */   public boolean tryAcquire(long timeout, TimeUnit unit)
/*     */   {
/* 305 */     return tryAcquire(1, timeout, unit);
/*     */   }
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
/*     */   public boolean tryAcquire(int permits)
/*     */   {
/* 320 */     return tryAcquire(permits, 0L, TimeUnit.MICROSECONDS);
/*     */   }
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
/*     */   public boolean tryAcquire()
/*     */   {
/* 334 */     return tryAcquire(1, 0L, TimeUnit.MICROSECONDS);
/*     */   }
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
/*     */   public boolean tryAcquire(int permits, long timeout, TimeUnit unit)
/*     */   {
/* 350 */     long timeoutMicros = Math.max(unit.toMicros(timeout), 0L);
/* 351 */     checkPermits(permits);
/*     */     long microsToWait;
/* 353 */     synchronized (mutex()) {
/* 354 */       long nowMicros = this.stopwatch.readMicros();
/* 355 */       if (!canAcquire(nowMicros, timeoutMicros)) {
/* 356 */         return false;
/*     */       }
/* 358 */       microsToWait = reserveAndGetWaitLength(permits, nowMicros);
/*     */     }
/*     */     
/* 361 */     this.stopwatch.sleepMicrosUninterruptibly(microsToWait);
/* 362 */     return true;
/*     */   }
/*     */   
/*     */   private boolean canAcquire(long nowMicros, long timeoutMicros) {
/* 366 */     return queryEarliestAvailable(nowMicros) - timeoutMicros <= nowMicros;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   final long reserveAndGetWaitLength(int permits, long nowMicros)
/*     */   {
/* 375 */     long momentAvailable = reserveEarliestAvailable(permits, nowMicros);
/* 376 */     return Math.max(momentAvailable - nowMicros, 0L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract long queryEarliestAvailable(long paramLong);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract long reserveEarliestAvailable(int paramInt, long paramLong);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 398 */     return String.format("RateLimiter[stableRate=%3.1fqps]", new Object[] { Double.valueOf(getRate()) });
/*     */   }
/*     */   
/*     */ 
/*     */   @VisibleForTesting
/*     */   static abstract class SleepingStopwatch
/*     */   {
/*     */     abstract long readMicros();
/*     */     
/*     */ 
/*     */     abstract void sleepMicrosUninterruptibly(long paramLong);
/*     */     
/*     */ 
/*     */     static final SleepingStopwatch createFromSystemTimer()
/*     */     {
/* 413 */       new SleepingStopwatch() {
/* 414 */         final Stopwatch stopwatch = Stopwatch.createStarted();
/*     */         
/*     */         long readMicros()
/*     */         {
/* 418 */           return this.stopwatch.elapsed(TimeUnit.MICROSECONDS);
/*     */         }
/*     */         
/*     */         void sleepMicrosUninterruptibly(long micros)
/*     */         {
/* 423 */           if (micros > 0L) {
/* 424 */             Uninterruptibles.sleepUninterruptibly(micros, TimeUnit.MICROSECONDS);
/*     */           }
/*     */         }
/*     */       };
/*     */     }
/*     */   }
/*     */   
/*     */   private static int checkPermits(int permits) {
/* 432 */     Preconditions.checkArgument(permits > 0, "Requested permits (%s) must be positive", new Object[] { Integer.valueOf(permits) });
/* 433 */     return permits;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\util\concurrent\RateLimiter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */