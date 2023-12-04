/*     */ package com.yammer.metrics.stats;
/*     */ 
/*     */ import com.yammer.metrics.core.Clock;
/*     */ import java.util.ArrayList;
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
/*     */ public class ExponentiallyDecayingSample
/*     */   implements Sample
/*     */ {
/*  24 */   private static final long RESCALE_THRESHOLD = TimeUnit.HOURS.toNanos(1L);
/*     */   private final ConcurrentSkipListMap<Double, Long> values;
/*     */   private final ReentrantReadWriteLock lock;
/*     */   private final double alpha;
/*     */   private final int reservoirSize;
/*  29 */   private final AtomicLong count = new AtomicLong(0L);
/*     */   private volatile long startTime;
/*  31 */   private final AtomicLong nextScaleTime = new AtomicLong(0L);
/*     */   
/*     */ 
/*     */ 
/*     */   private final Clock clock;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ExponentiallyDecayingSample(int reservoirSize, double alpha)
/*     */   {
/*  42 */     this(reservoirSize, alpha, Clock.defaultClock());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ExponentiallyDecayingSample(int reservoirSize, double alpha, Clock clock)
/*     */   {
/*  53 */     this.values = new ConcurrentSkipListMap();
/*  54 */     this.lock = new ReentrantReadWriteLock();
/*  55 */     this.alpha = alpha;
/*  56 */     this.reservoirSize = reservoirSize;
/*  57 */     this.clock = clock;
/*  58 */     clear();
/*     */   }
/*     */   
/*     */   public void clear()
/*     */   {
/*  63 */     lockForRescale();
/*     */     try {
/*  65 */       this.values.clear();
/*  66 */       this.count.set(0L);
/*  67 */       this.startTime = currentTimeInSeconds();
/*  68 */       this.nextScaleTime.set(this.clock.tick() + RESCALE_THRESHOLD);
/*     */     } finally {
/*  70 */       unlockForRescale();
/*     */     }
/*     */   }
/*     */   
/*     */   public int size()
/*     */   {
/*  76 */     return (int)Math.min(this.reservoirSize, this.count.get());
/*     */   }
/*     */   
/*     */   public void update(long value)
/*     */   {
/*  81 */     update(value, currentTimeInSeconds());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void update(long value, long timestamp)
/*     */   {
/*  92 */     rescaleIfNeeded();
/*     */     
/*  94 */     lockForRegularUsage();
/*     */     try {
/*  96 */       double priority = weight(timestamp - this.startTime) / ThreadLocalRandom.current().nextDouble();
/*     */       
/*  98 */       long newCount = this.count.incrementAndGet();
/*  99 */       if (newCount <= this.reservoirSize) {
/* 100 */         this.values.put(Double.valueOf(priority), Long.valueOf(value));
/*     */       } else {
/* 102 */         Double first = (Double)this.values.firstKey();
/* 103 */         if ((first.doubleValue() < priority) && 
/* 104 */           (this.values.putIfAbsent(Double.valueOf(priority), Long.valueOf(value)) == null))
/*     */         {
/* 106 */           while (this.values.remove(first) == null) {
/* 107 */             first = (Double)this.values.firstKey();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 113 */       unlockForRegularUsage();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void rescaleIfNeeded()
/*     */   {
/* 120 */     long now = this.clock.tick();
/* 121 */     long next = this.nextScaleTime.get();
/* 122 */     if (now >= next) {
/* 123 */       rescale(now, next);
/*     */     }
/*     */   }
/*     */   
/*     */   public Snapshot getSnapshot()
/*     */   {
/* 129 */     lockForRegularUsage();
/*     */     try {
/* 131 */       return new Snapshot(this.values.values());
/*     */     } finally {
/* 133 */       unlockForRegularUsage();
/*     */     }
/*     */   }
/*     */   
/*     */   private long currentTimeInSeconds() {
/* 138 */     return TimeUnit.MILLISECONDS.toSeconds(this.clock.time());
/*     */   }
/*     */   
/*     */   private double weight(long t) {
/* 142 */     return Math.exp(this.alpha * t);
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
/* 164 */     if (this.nextScaleTime.compareAndSet(next, now + RESCALE_THRESHOLD)) {
/* 165 */       lockForRescale();
/*     */       try {
/* 167 */         long oldStartTime = this.startTime;
/* 168 */         this.startTime = currentTimeInSeconds();
/* 169 */         ArrayList<Double> keys = new ArrayList(this.values.keySet());
/* 170 */         for (Double key : keys) {
/* 171 */           Long value = (Long)this.values.remove(key);
/* 172 */           this.values.put(Double.valueOf(key.doubleValue() * Math.exp(-this.alpha * (this.startTime - oldStartTime))), value);
/*     */         }
/*     */         
/*     */ 
/* 176 */         this.count.set(this.values.size());
/*     */       } finally {
/* 178 */         unlockForRescale();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void unlockForRescale() {
/* 184 */     this.lock.writeLock().unlock();
/*     */   }
/*     */   
/*     */   private void lockForRescale() {
/* 188 */     this.lock.writeLock().lock();
/*     */   }
/*     */   
/*     */   private void lockForRegularUsage() {
/* 192 */     this.lock.readLock().lock();
/*     */   }
/*     */   
/*     */   private void unlockForRegularUsage() {
/* 196 */     this.lock.readLock().unlock();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\stats\ExponentiallyDecayingSample.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */