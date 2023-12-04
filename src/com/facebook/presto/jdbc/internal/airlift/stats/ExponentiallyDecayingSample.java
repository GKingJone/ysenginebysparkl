/*     */ package com.facebook.presto.jdbc.internal.airlift.stats;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.ConcurrentSkipListMap;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
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
/*     */ final class ExponentiallyDecayingSample
/*     */ {
/*  66 */   private static final long RESCALE_THRESHOLD = TimeUnit.HOURS.toNanos(1L);
/*     */   private final ConcurrentSkipListMap<Double, Long> values;
/*     */   private final ReentrantReadWriteLock lock;
/*     */   private final double alpha;
/*     */   private final int reservoirSize;
/*  71 */   private final AtomicLong count = new AtomicLong(0L);
/*     */   private volatile long startTime;
/*  73 */   private final AtomicLong nextScaleTime = new AtomicLong(0L);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ExponentiallyDecayingSample(int reservoirSize, double alpha)
/*     */   {
/*  84 */     this.values = new ConcurrentSkipListMap();
/*  85 */     this.lock = new ReentrantReadWriteLock();
/*  86 */     this.alpha = alpha;
/*  87 */     this.reservoirSize = reservoirSize;
/*  88 */     clear();
/*     */   }
/*     */   
/*     */   public void clear() {
/*  92 */     this.values.clear();
/*  93 */     this.count.set(0L);
/*  94 */     this.startTime = tick();
/*  95 */     this.nextScaleTime.set(System.nanoTime() + RESCALE_THRESHOLD);
/*     */   }
/*     */   
/*     */   public int size() {
/*  99 */     return (int)Math.min(this.reservoirSize, this.count.get());
/*     */   }
/*     */   
/*     */   public void update(long value) {
/* 103 */     update(value, tick());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void update(long value, long timestamp)
/*     */   {
/* 113 */     lockForRegularUsage();
/*     */     try {
/* 115 */       double priority = weight(timestamp - this.startTime) / Math.random();
/* 116 */       long newCount = this.count.incrementAndGet();
/* 117 */       if (newCount <= this.reservoirSize) {
/* 118 */         this.values.put(Double.valueOf(priority), Long.valueOf(value));
/*     */       } else {
/* 120 */         Double first = (Double)this.values.firstKey();
/* 121 */         if ((first.doubleValue() < priority) && 
/* 122 */           (this.values.putIfAbsent(Double.valueOf(priority), Long.valueOf(value)) == null))
/*     */         {
/* 124 */           while (this.values.remove(first) == null) {
/* 125 */             first = (Double)this.values.firstKey();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 131 */       unlockForRegularUsage();
/*     */     }
/*     */     
/* 134 */     long now = System.nanoTime();
/* 135 */     long next = this.nextScaleTime.get();
/* 136 */     if (now >= next) {
/* 137 */       rescale(now, next);
/*     */     }
/*     */   }
/*     */   
/*     */   public List<Long> values() {
/* 142 */     lockForRegularUsage();
/*     */     try {
/* 144 */       return new ArrayList(this.values.values());
/*     */     } finally {
/* 146 */       unlockForRegularUsage();
/*     */     }
/*     */   }
/*     */   
/* 150 */   private static long tick() { return TimeUnit.NANOSECONDS.toSeconds(System.nanoTime()); }
/*     */   
/*     */   private double weight(long t) {
/* 153 */     return Math.exp(this.alpha * t);
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
/*     */   private void rescale(long now, long next)
/*     */   {
/* 175 */     if (this.nextScaleTime.compareAndSet(next, now + RESCALE_THRESHOLD)) {
/* 176 */       lockForRescale();
/*     */       try {
/* 178 */         oldStartTime = this.startTime;
/* 179 */         this.startTime = tick();
/* 180 */         ArrayList<Double> keys = new ArrayList(this.values.keySet());
/* 181 */         for (Double key : keys) {
/* 182 */           Long value = (Long)this.values.remove(key);
/* 183 */           this.values.put(Double.valueOf(key.doubleValue() * Math.exp(-this.alpha * (this.startTime - oldStartTime))), value);
/*     */         }
/*     */       } finally { long oldStartTime;
/* 186 */         unlockForRescale();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void unlockForRescale() {
/* 192 */     this.lock.writeLock().unlock();
/*     */   }
/*     */   
/*     */   private void lockForRescale() {
/* 196 */     this.lock.writeLock().lock();
/*     */   }
/*     */   
/*     */   private void lockForRegularUsage() {
/* 200 */     this.lock.readLock().lock();
/*     */   }
/*     */   
/*     */   private void unlockForRegularUsage() {
/* 204 */     this.lock.readLock().unlock();
/*     */   }
/*     */   
/*     */   public double[] percentiles(double... percentiles)
/*     */   {
/* 209 */     double[] scores = new double[percentiles.length];
/* 210 */     Arrays.fill(scores, NaN.0D);
/*     */     
/* 212 */     List<Long> values = values();
/* 213 */     if (!values.isEmpty()) {
/* 214 */       Collections.sort(values);
/*     */       
/* 216 */       for (int i = 0; i < percentiles.length; i++) {
/* 217 */         double p = percentiles[i];
/* 218 */         double pos = p * (values.size() + 1);
/* 219 */         if (pos < 1.0D) {
/* 220 */           scores[i] = ((Long)values.get(0)).longValue();
/*     */         }
/* 222 */         else if (pos >= values.size()) {
/* 223 */           scores[i] = ((Long)values.get(values.size() - 1)).longValue();
/*     */         }
/*     */         else {
/* 226 */           double lower = ((Long)values.get((int)pos - 1)).longValue();
/* 227 */           double upper = ((Long)values.get((int)pos)).longValue();
/* 228 */           scores[i] = (lower + (pos - Math.floor(pos)) * (upper - lower));
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 233 */     return scores;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\stats\ExponentiallyDecayingSample.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */