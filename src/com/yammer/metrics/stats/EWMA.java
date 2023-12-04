/*     */ package com.yammer.metrics.stats;
/*     */ 
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicLong;
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
/*     */ public class EWMA
/*     */ {
/*     */   private static final int INTERVAL = 5;
/*     */   private static final double SECONDS_PER_MINUTE = 60.0D;
/*     */   private static final int ONE_MINUTE = 1;
/*     */   private static final int FIVE_MINUTES = 5;
/*     */   private static final int FIFTEEN_MINUTES = 15;
/*  22 */   private static final double M1_ALPHA = 1.0D - Math.exp(-0.08333333333333333D);
/*  23 */   private static final double M5_ALPHA = 1.0D - Math.exp(-0.016666666666666666D);
/*  24 */   private static final double M15_ALPHA = 1.0D - Math.exp(-0.005555555555555555D);
/*     */   
/*  26 */   private volatile boolean initialized = false;
/*  27 */   private volatile double rate = 0.0D;
/*     */   
/*  29 */   private final AtomicLong uncounted = new AtomicLong();
/*     */   
/*     */ 
/*     */   private final double alpha;
/*     */   
/*     */   private final double interval;
/*     */   
/*     */ 
/*     */   public static EWMA oneMinuteEWMA()
/*     */   {
/*  39 */     return new EWMA(M1_ALPHA, 5L, TimeUnit.SECONDS);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static EWMA fiveMinuteEWMA()
/*     */   {
/*  49 */     return new EWMA(M5_ALPHA, 5L, TimeUnit.SECONDS);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static EWMA fifteenMinuteEWMA()
/*     */   {
/*  59 */     return new EWMA(M15_ALPHA, 5L, TimeUnit.SECONDS);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public EWMA(double alpha, long interval, TimeUnit intervalUnit)
/*     */   {
/*  70 */     this.interval = intervalUnit.toNanos(interval);
/*  71 */     this.alpha = alpha;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void update(long n)
/*     */   {
/*  80 */     this.uncounted.addAndGet(n);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void tick()
/*     */   {
/*  87 */     long count = this.uncounted.getAndSet(0L);
/*  88 */     double instantRate = count / this.interval;
/*  89 */     if (this.initialized) {
/*  90 */       this.rate += this.alpha * (instantRate - this.rate);
/*     */     } else {
/*  92 */       this.rate = instantRate;
/*  93 */       this.initialized = true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double rate(TimeUnit rateUnit)
/*     */   {
/* 104 */     return this.rate * rateUnit.toNanos(1L);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\stats\EWMA.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */