/*     */ package com.yammer.metrics.core;
/*     */ 
/*     */ import com.yammer.metrics.stats.ExponentiallyDecayingSample;
/*     */ import com.yammer.metrics.stats.Sample;
/*     */ import com.yammer.metrics.stats.Snapshot;
/*     */ import com.yammer.metrics.stats.UniformSample;
/*     */ import java.util.concurrent.atomic.AtomicLong;
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
/*     */ public class Histogram
/*     */   implements Metric, Sampling, Summarizable
/*     */ {
/*     */   private static final int DEFAULT_SAMPLE_SIZE = 1028;
/*     */   private static final double DEFAULT_ALPHA = 0.015D;
/*     */   private final Sample sample;
/*     */   
/*     */   static abstract enum SampleType
/*     */   {
/*  31 */     UNIFORM, 
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
/*  43 */     BIASED;
/*     */     
/*     */ 
/*     */ 
/*     */     private SampleType() {}
/*     */     
/*     */ 
/*     */     public abstract Sample newSample();
/*     */   }
/*     */   
/*     */ 
/*  54 */   private final AtomicLong min = new AtomicLong();
/*  55 */   private final AtomicLong max = new AtomicLong();
/*  56 */   private final AtomicLong sum = new AtomicLong();
/*     */   
/*     */ 
/*  59 */   private final AtomicReference<double[]> variance = new AtomicReference(new double[] { -1.0D, 0.0D });
/*     */   
/*  61 */   private final AtomicLong count = new AtomicLong();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   Histogram(SampleType type)
/*     */   {
/*  69 */     this(type.newSample());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   Histogram(Sample sample)
/*     */   {
/*  78 */     this.sample = sample;
/*  79 */     clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void clear()
/*     */   {
/*  86 */     this.sample.clear();
/*  87 */     this.count.set(0L);
/*  88 */     this.max.set(Long.MIN_VALUE);
/*  89 */     this.min.set(Long.MAX_VALUE);
/*  90 */     this.sum.set(0L);
/*  91 */     this.variance.set(new double[] { -1.0D, 0.0D });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void update(int value)
/*     */   {
/* 100 */     update(value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void update(long value)
/*     */   {
/* 109 */     this.count.incrementAndGet();
/* 110 */     this.sample.update(value);
/* 111 */     setMax(value);
/* 112 */     setMin(value);
/* 113 */     this.sum.getAndAdd(value);
/* 114 */     updateVariance(value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long count()
/*     */   {
/* 123 */     return this.count.get();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public double max()
/*     */   {
/* 131 */     if (count() > 0L) {
/* 132 */       return this.max.get();
/*     */     }
/* 134 */     return 0.0D;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public double min()
/*     */   {
/* 142 */     if (count() > 0L) {
/* 143 */       return this.min.get();
/*     */     }
/* 145 */     return 0.0D;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public double mean()
/*     */   {
/* 153 */     if (count() > 0L) {
/* 154 */       return this.sum.get() / count();
/*     */     }
/* 156 */     return 0.0D;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public double stdDev()
/*     */   {
/* 164 */     if (count() > 0L) {
/* 165 */       return Math.sqrt(variance());
/*     */     }
/* 167 */     return 0.0D;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public double sum()
/*     */   {
/* 175 */     return this.sum.get();
/*     */   }
/*     */   
/*     */   public Snapshot getSnapshot()
/*     */   {
/* 180 */     return this.sample.getSnapshot();
/*     */   }
/*     */   
/*     */   private double variance() {
/* 184 */     if (count() <= 1L) {
/* 185 */       return 0.0D;
/*     */     }
/* 187 */     return ((double[])this.variance.get())[1] / (count() - 1L);
/*     */   }
/*     */   
/*     */   private void setMax(long potentialMax) {
/* 191 */     boolean done = false;
/* 192 */     while (!done) {
/* 193 */       long currentMax = this.max.get();
/* 194 */       done = (currentMax >= potentialMax) || (this.max.compareAndSet(currentMax, potentialMax));
/*     */     }
/*     */   }
/*     */   
/*     */   private void setMin(long potentialMin) {
/* 199 */     boolean done = false;
/* 200 */     while (!done) {
/* 201 */       long currentMin = this.min.get();
/* 202 */       done = (currentMin <= potentialMin) || (this.min.compareAndSet(currentMin, potentialMin));
/*     */     }
/*     */   }
/*     */   
/*     */   private void updateVariance(long value) {
/*     */     for (;;) {
/* 208 */       double[] oldValues = (double[])this.variance.get();
/* 209 */       double[] newValues = new double[2];
/* 210 */       if (oldValues[0] == -1.0D) {
/* 211 */         newValues[0] = value;
/* 212 */         newValues[1] = 0.0D;
/*     */       } else {
/* 214 */         double oldM = oldValues[0];
/* 215 */         double oldS = oldValues[1];
/*     */         
/* 217 */         double newM = oldM + (value - oldM) / count();
/* 218 */         double newS = oldS + (value - oldM) * (value - newM);
/*     */         
/* 220 */         newValues[0] = newM;
/* 221 */         newValues[1] = newS;
/*     */       }
/* 223 */       if (this.variance.compareAndSet(oldValues, newValues)) {
/* 224 */         return;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public <T> void processWith(MetricProcessor<T> processor, MetricName name, T context) throws Exception
/*     */   {
/* 231 */     processor.processHistogram(name, this, context);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\core\Histogram.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */