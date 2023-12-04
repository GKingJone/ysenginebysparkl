/*     */ package com.facebook.presto.jdbc.internal.airlift.stats;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
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
/*     */ @Deprecated
/*     */ public class MeterStat
/*     */ {
/*  27 */   private final AtomicLong sum = new AtomicLong(0L);
/*  28 */   private final ExponentiallyDecayingSample sample = new ExponentiallyDecayingSample(1028, 0.015D);
/*  29 */   private final DecayCounter oneMinute = new DecayCounter(ExponentialDecay.oneMinute());
/*  30 */   private final DecayCounter fiveMinute = new DecayCounter(ExponentialDecay.fiveMinutes());
/*  31 */   private final DecayCounter fifteenMinute = new DecayCounter(ExponentialDecay.fifteenMinutes());
/*     */   
/*     */   public void update(long value)
/*     */   {
/*  35 */     this.sample.update(value);
/*  36 */     this.oneMinute.add(value);
/*  37 */     this.fiveMinute.add(value);
/*  38 */     this.fifteenMinute.add(value);
/*  39 */     this.sum.addAndGet(value);
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public long getSum()
/*     */   {
/*  45 */     return this.sum.get();
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public long getMin()
/*     */   {
/*  51 */     return ((Long)Collections.min(this.sample.values())).longValue();
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public long getMax()
/*     */   {
/*  57 */     return ((Long)Collections.max(this.sample.values())).longValue();
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public double getMean()
/*     */   {
/*  63 */     List<Long> values = this.sample.values();
/*     */     
/*  65 */     long sum = 0L;
/*  66 */     for (Iterator localIterator = values.iterator(); localIterator.hasNext();) { long value = ((Long)localIterator.next()).longValue();
/*  67 */       sum += value;
/*     */     }
/*     */     
/*  70 */     return sum * 1.0D / values.size();
/*     */   }
/*     */   
/*     */   public double getStdDev()
/*     */   {
/*  75 */     throw new UnsupportedOperationException("not yet implemented");
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public double getOneMinuteRate()
/*     */   {
/*  81 */     return this.oneMinute.getCount();
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public double getFiveMinuteRate()
/*     */   {
/*  87 */     return this.fiveMinute.getCount();
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public double getFifteenMinuteRate()
/*     */   {
/*  93 */     return this.fifteenMinute.getCount();
/*     */   }
/*     */   
/*     */   @Managed(description="50th Percentile Measurement")
/*     */   public double getTP50()
/*     */   {
/*  99 */     return this.sample.percentiles(new double[] { 0.5D })[0];
/*     */   }
/*     */   
/*     */   @Managed(description="90th Percentile Measurement")
/*     */   public double getTP90()
/*     */   {
/* 105 */     return this.sample.percentiles(new double[] { 0.9D })[0];
/*     */   }
/*     */   
/*     */   @Managed(description="99th Percentile Measurement")
/*     */   public double getTP99()
/*     */   {
/* 111 */     return this.sample.percentiles(new double[] { 0.99D })[0];
/*     */   }
/*     */   
/*     */   @Managed(description="99.9th Percentile Measurement")
/*     */   public double getTP999()
/*     */   {
/* 117 */     return this.sample.percentiles(new double[] { 0.999D })[0];
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\stats\MeterStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */