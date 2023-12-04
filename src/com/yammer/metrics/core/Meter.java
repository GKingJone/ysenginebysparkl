/*     */ package com.yammer.metrics.core;
/*     */ 
/*     */ import com.yammer.metrics.stats.EWMA;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Meter
/*     */   implements Metered, Stoppable
/*     */ {
/*     */   private static final long INTERVAL = 5L;
/*  19 */   private final EWMA m1Rate = EWMA.oneMinuteEWMA();
/*  20 */   private final EWMA m5Rate = EWMA.fiveMinuteEWMA();
/*  21 */   private final EWMA m15Rate = EWMA.fifteenMinuteEWMA();
/*     */   
/*  23 */   private final AtomicLong count = new AtomicLong();
/*     */   
/*     */ 
/*     */   private final long startTime;
/*     */   
/*     */ 
/*     */   private final TimeUnit rateUnit;
/*     */   
/*     */   private final String eventType;
/*     */   
/*     */   private final ScheduledFuture<?> future;
/*     */   
/*     */   private final Clock clock;
/*     */   
/*     */ 
/*     */   Meter(ScheduledExecutorService tickThread, String eventType, TimeUnit rateUnit, Clock clock)
/*     */   {
/*  40 */     this.rateUnit = rateUnit;
/*  41 */     this.eventType = eventType;
/*  42 */     this.future = tickThread.scheduleAtFixedRate(new Runnable()
/*     */     {
/*     */ 
/*  45 */       public void run() { Meter.this.tick(); } }, 5L, 5L, TimeUnit.SECONDS);
/*     */     
/*     */ 
/*  48 */     this.clock = clock;
/*  49 */     this.startTime = this.clock.tick();
/*     */   }
/*     */   
/*     */   public TimeUnit rateUnit()
/*     */   {
/*  54 */     return this.rateUnit;
/*     */   }
/*     */   
/*     */   public String eventType()
/*     */   {
/*  59 */     return this.eventType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   void tick()
/*     */   {
/*  66 */     this.m1Rate.tick();
/*  67 */     this.m5Rate.tick();
/*  68 */     this.m15Rate.tick();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void mark()
/*     */   {
/*  75 */     mark(1L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void mark(long n)
/*     */   {
/*  84 */     this.count.addAndGet(n);
/*  85 */     this.m1Rate.update(n);
/*  86 */     this.m5Rate.update(n);
/*  87 */     this.m15Rate.update(n);
/*     */   }
/*     */   
/*     */   public long count()
/*     */   {
/*  92 */     return this.count.get();
/*     */   }
/*     */   
/*     */   public double fifteenMinuteRate()
/*     */   {
/*  97 */     return this.m15Rate.rate(this.rateUnit);
/*     */   }
/*     */   
/*     */   public double fiveMinuteRate()
/*     */   {
/* 102 */     return this.m5Rate.rate(this.rateUnit);
/*     */   }
/*     */   
/*     */   public double meanRate()
/*     */   {
/* 107 */     if (count() == 0L) {
/* 108 */       return 0.0D;
/*     */     }
/* 110 */     long elapsed = this.clock.tick() - this.startTime;
/* 111 */     return convertNsRate(count() / elapsed);
/*     */   }
/*     */   
/*     */ 
/*     */   public double oneMinuteRate()
/*     */   {
/* 117 */     return this.m1Rate.rate(this.rateUnit);
/*     */   }
/*     */   
/*     */   private double convertNsRate(double ratePerNs) {
/* 121 */     return ratePerNs * this.rateUnit.toNanos(1L);
/*     */   }
/*     */   
/*     */   public void stop()
/*     */   {
/* 126 */     this.future.cancel(false);
/*     */   }
/*     */   
/*     */   public <T> void processWith(MetricProcessor<T> processor, MetricName name, T context) throws Exception
/*     */   {
/* 131 */     processor.processMeter(name, this, context);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\core\Meter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */