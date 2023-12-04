/*     */ package com.facebook.presto.jdbc.internal.airlift.stats;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.units.Duration;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import org.weakref.jmx.Managed;
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
/*     */ @Deprecated
/*     */ public class TimedStat
/*     */ {
/*  33 */   private final AtomicLong sum = new AtomicLong(0L);
/*  34 */   private final AtomicLong count = new AtomicLong(0L);
/*  35 */   private final ExponentiallyDecayingSample sample = new ExponentiallyDecayingSample(1028, 0.015D);
/*     */   
/*     */   @Managed
/*     */   public long getCount()
/*     */   {
/*  40 */     return this.count.get();
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public double getSum()
/*     */   {
/*  46 */     return this.sum.get();
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public double getMin()
/*     */   {
/*  52 */     List<Long> values = this.sample.values();
/*  53 */     if (!values.isEmpty()) {
/*  54 */       return ((Long)Collections.min(values)).longValue();
/*     */     }
/*     */     
/*  57 */     return NaN.0D;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public double getMax()
/*     */   {
/*  63 */     List<Long> values = this.sample.values();
/*  64 */     if (!values.isEmpty()) {
/*  65 */       return ((Long)Collections.max(values)).longValue();
/*     */     }
/*     */     
/*  68 */     return NaN.0D;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public double getMean()
/*     */   {
/*  74 */     List<Long> values = this.sample.values();
/*     */     
/*  76 */     if (!values.isEmpty()) {
/*  77 */       long sum = 0L;
/*  78 */       for (Iterator localIterator = values.iterator(); localIterator.hasNext();) { long value = ((Long)localIterator.next()).longValue();
/*  79 */         sum += value;
/*     */       }
/*     */       
/*  82 */       return sum * 1.0D / values.size();
/*     */     }
/*     */     
/*  85 */     return NaN.0D;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public double getPercentile(double percentile)
/*     */   {
/*  91 */     if ((percentile < 0.0D) || (percentile > 1.0D)) {
/*  92 */       throw new IllegalArgumentException("percentile must be between 0 and 1");
/*     */     }
/*  94 */     return this.sample.percentiles(new double[] { percentile })[0];
/*     */   }
/*     */   
/*     */   @Managed(description="50th Percentile Measurement")
/*     */   public double getTP50()
/*     */   {
/* 100 */     return this.sample.percentiles(new double[] { 0.5D })[0];
/*     */   }
/*     */   
/*     */   @Managed(description="90th Percentile Measurement")
/*     */   public double getTP90()
/*     */   {
/* 106 */     return this.sample.percentiles(new double[] { 0.9D })[0];
/*     */   }
/*     */   
/*     */   @Managed(description="99th Percentile Measurement")
/*     */   public double getTP99()
/*     */   {
/* 112 */     return this.sample.percentiles(new double[] { 0.99D })[0];
/*     */   }
/*     */   
/*     */   @Managed(description="99.9th Percentile Measurement")
/*     */   public double getTP999()
/*     */   {
/* 118 */     return this.sample.percentiles(new double[] { 0.999D })[0];
/*     */   }
/*     */   
/*     */   public void addValue(double value, TimeUnit timeUnit)
/*     */   {
/* 123 */     addValue(new Duration(value, timeUnit));
/*     */   }
/*     */   
/*     */   public void addValue(Duration duration)
/*     */   {
/* 128 */     this.sample.update(duration.toMillis());
/* 129 */     this.sum.addAndGet(duration.toMillis());
/* 130 */     this.count.incrementAndGet();
/*     */   }
/*     */   
/*     */   public <T> T time(Callable<T> callable)
/*     */     throws Exception
/*     */   {
/* 136 */     long start = System.nanoTime();
/* 137 */     T result = callable.call();
/* 138 */     addValue(Duration.nanosSince(start));
/* 139 */     return result;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\stats\TimedStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */