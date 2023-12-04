/*     */ package com.yammer.metrics.core;
/*     */ 
/*     */ import com.yammer.metrics.stats.Snapshot;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Timer
/*     */   implements Metered, Stoppable, Sampling, Summarizable
/*     */ {
/*     */   private final TimeUnit durationUnit;
/*     */   private final TimeUnit rateUnit;
/*     */   private final Meter meter;
/*  18 */   private final Histogram histogram = new Histogram(Histogram.SampleType.BIASED);
/*     */   
/*     */ 
/*     */ 
/*     */   private final Clock clock;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   Timer(ScheduledExecutorService tickThread, TimeUnit durationUnit, TimeUnit rateUnit)
/*     */   {
/*  29 */     this(tickThread, durationUnit, rateUnit, Clock.defaultClock());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   Timer(ScheduledExecutorService tickThread, TimeUnit durationUnit, TimeUnit rateUnit, Clock clock)
/*     */   {
/*  41 */     this.durationUnit = durationUnit;
/*  42 */     this.rateUnit = rateUnit;
/*  43 */     this.meter = new Meter(tickThread, "calls", rateUnit, clock);
/*  44 */     this.clock = clock;
/*  45 */     clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TimeUnit durationUnit()
/*     */   {
/*  54 */     return this.durationUnit;
/*     */   }
/*     */   
/*     */   public TimeUnit rateUnit()
/*     */   {
/*  59 */     return this.rateUnit;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void clear()
/*     */   {
/*  66 */     this.histogram.clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void update(long duration, TimeUnit unit)
/*     */   {
/*  76 */     update(unit.toNanos(duration));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public <T> T time(Callable<T> event)
/*     */     throws Exception
/*     */   {
/*  89 */     long startTime = this.clock.tick();
/*     */     try {
/*  91 */       return (T)event.call();
/*     */     } finally {
/*  93 */       update(this.clock.tick() - startTime);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TimerContext time()
/*     */   {
/* 103 */     return new TimerContext(this, this.clock);
/*     */   }
/*     */   
/*     */   public long count()
/*     */   {
/* 108 */     return this.histogram.count();
/*     */   }
/*     */   
/*     */   public double fifteenMinuteRate()
/*     */   {
/* 113 */     return this.meter.fifteenMinuteRate();
/*     */   }
/*     */   
/*     */   public double fiveMinuteRate()
/*     */   {
/* 118 */     return this.meter.fiveMinuteRate();
/*     */   }
/*     */   
/*     */   public double meanRate()
/*     */   {
/* 123 */     return this.meter.meanRate();
/*     */   }
/*     */   
/*     */   public double oneMinuteRate()
/*     */   {
/* 128 */     return this.meter.oneMinuteRate();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double max()
/*     */   {
/* 138 */     return convertFromNS(this.histogram.max());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double min()
/*     */   {
/* 148 */     return convertFromNS(this.histogram.min());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double mean()
/*     */   {
/* 158 */     return convertFromNS(this.histogram.mean());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double stdDev()
/*     */   {
/* 168 */     return convertFromNS(this.histogram.stdDev());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public double sum()
/*     */   {
/* 178 */     return convertFromNS(this.histogram.sum());
/*     */   }
/*     */   
/*     */   public Snapshot getSnapshot()
/*     */   {
/* 183 */     double[] values = this.histogram.getSnapshot().getValues();
/* 184 */     double[] converted = new double[values.length];
/* 185 */     for (int i = 0; i < values.length; i++) {
/* 186 */       converted[i] = convertFromNS(values[i]);
/*     */     }
/* 188 */     return new Snapshot(converted);
/*     */   }
/*     */   
/*     */   public String eventType()
/*     */   {
/* 193 */     return this.meter.eventType();
/*     */   }
/*     */   
/*     */   private void update(long duration) {
/* 197 */     if (duration >= 0L) {
/* 198 */       this.histogram.update(duration);
/* 199 */       this.meter.mark();
/*     */     }
/*     */   }
/*     */   
/*     */   private double convertFromNS(double ns) {
/* 204 */     return ns / TimeUnit.NANOSECONDS.convert(1L, this.durationUnit);
/*     */   }
/*     */   
/*     */   public void stop()
/*     */   {
/* 209 */     this.meter.stop();
/*     */   }
/*     */   
/*     */   public <T> void processWith(MetricProcessor<T> processor, MetricName name, T context) throws Exception
/*     */   {
/* 214 */     processor.processTimer(name, this, context);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\core\Timer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */