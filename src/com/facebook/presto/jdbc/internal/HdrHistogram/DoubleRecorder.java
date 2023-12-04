/*     */ package com.facebook.presto.jdbc.internal.HdrHistogram;
/*     */ 
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
/*     */ public class DoubleRecorder
/*     */ {
/*  29 */   private static AtomicLong instanceIdSequencer = new AtomicLong(1L);
/*  30 */   private final long instanceId = instanceIdSequencer.getAndIncrement();
/*     */   
/*  32 */   private final WriterReaderPhaser recordingPhaser = new WriterReaderPhaser();
/*     */   
/*     */ 
/*     */ 
/*     */   private volatile InternalConcurrentDoubleHistogram activeHistogram;
/*     */   
/*     */ 
/*     */ 
/*     */   private InternalConcurrentDoubleHistogram inactiveHistogram;
/*     */   
/*     */ 
/*     */ 
/*     */   public DoubleRecorder(int numberOfSignificantValueDigits)
/*     */   {
/*  46 */     this.activeHistogram = new InternalConcurrentDoubleHistogram(this.instanceId, numberOfSignificantValueDigits, null);
/*  47 */     this.inactiveHistogram = new InternalConcurrentDoubleHistogram(this.instanceId, numberOfSignificantValueDigits, null);
/*  48 */     this.activeHistogram.setStartTimeStamp(System.currentTimeMillis());
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
/*     */   public DoubleRecorder(long highestToLowestValueRatio, int numberOfSignificantValueDigits)
/*     */   {
/*  62 */     this.activeHistogram = new InternalConcurrentDoubleHistogram(this.instanceId, highestToLowestValueRatio, numberOfSignificantValueDigits, null);
/*     */     
/*  64 */     this.inactiveHistogram = new InternalConcurrentDoubleHistogram(this.instanceId, highestToLowestValueRatio, numberOfSignificantValueDigits, null);
/*     */     
/*  66 */     this.activeHistogram.setStartTimeStamp(System.currentTimeMillis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void recordValue(double value)
/*     */   {
/*  75 */     long criticalValueAtEnter = this.recordingPhaser.writerCriticalSectionEnter();
/*     */     try {
/*  77 */       this.activeHistogram.recordValue(value);
/*     */     } finally {
/*  79 */       this.recordingPhaser.writerCriticalSectionExit(criticalValueAtEnter);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void recordValueWithCount(double value, long count)
/*     */     throws ArrayIndexOutOfBoundsException
/*     */   {
/*  91 */     long criticalValueAtEnter = this.recordingPhaser.writerCriticalSectionEnter();
/*     */     try {
/*  93 */       this.activeHistogram.recordValueWithCount(value, count);
/*     */     } finally {
/*  95 */       this.recordingPhaser.writerCriticalSectionExit(criticalValueAtEnter);
/*     */     }
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
/*     */   public void recordValueWithExpectedInterval(double value, double expectedIntervalBetweenValueSamples)
/*     */     throws ArrayIndexOutOfBoundsException
/*     */   {
/* 117 */     long criticalValueAtEnter = this.recordingPhaser.writerCriticalSectionEnter();
/*     */     try {
/* 119 */       this.activeHistogram.recordValueWithExpectedInterval(value, expectedIntervalBetweenValueSamples);
/*     */     } finally {
/* 121 */       this.recordingPhaser.writerCriticalSectionExit(criticalValueAtEnter);
/*     */     }
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
/*     */   public synchronized DoubleHistogram getIntervalHistogram()
/*     */   {
/* 135 */     return getIntervalHistogram(null);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized DoubleHistogram getIntervalHistogram(DoubleHistogram histogramToRecycle)
/*     */   {
/* 167 */     validateFitAsReplacementHistogram(histogramToRecycle);
/* 168 */     this.inactiveHistogram = ((InternalConcurrentDoubleHistogram)histogramToRecycle);
/* 169 */     performIntervalSample();
/* 170 */     DoubleHistogram sampledHistogram = this.inactiveHistogram;
/* 171 */     this.inactiveHistogram = null;
/* 172 */     return sampledHistogram;
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
/*     */   public synchronized void getIntervalHistogramInto(DoubleHistogram targetHistogram)
/*     */   {
/* 185 */     performIntervalSample();
/* 186 */     this.inactiveHistogram.copyInto(targetHistogram);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void reset()
/*     */   {
/* 194 */     performIntervalSample();
/* 195 */     performIntervalSample();
/*     */   }
/*     */   
/*     */   private void performIntervalSample() {
/*     */     try {
/* 200 */       this.recordingPhaser.readerLock();
/*     */       
/*     */ 
/* 203 */       if (this.inactiveHistogram == null) {
/* 204 */         this.inactiveHistogram = new InternalConcurrentDoubleHistogram(this.activeHistogram, null);
/*     */       }
/*     */       
/* 207 */       this.inactiveHistogram.reset();
/*     */       
/*     */ 
/* 210 */       InternalConcurrentDoubleHistogram tempHistogram = this.inactiveHistogram;
/* 211 */       this.inactiveHistogram = this.activeHistogram;
/* 212 */       this.activeHistogram = tempHistogram;
/*     */       
/*     */ 
/* 215 */       long now = System.currentTimeMillis();
/* 216 */       this.activeHistogram.setStartTimeStamp(now);
/* 217 */       this.inactiveHistogram.setEndTimeStamp(now);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 222 */       this.recordingPhaser.flipPhase(500000L);
/*     */     } finally {
/* 224 */       this.recordingPhaser.readerUnlock();
/*     */     }
/*     */   }
/*     */   
/*     */   private class InternalConcurrentDoubleHistogram extends ConcurrentDoubleHistogram {
/*     */     private final long containingInstanceId;
/*     */     
/*     */     private InternalConcurrentDoubleHistogram(long id, int numberOfSignificantValueDigits) {
/* 232 */       super();
/* 233 */       this.containingInstanceId = id;
/*     */     }
/*     */     
/*     */ 
/*     */     private InternalConcurrentDoubleHistogram(long id, long highestToLowestValueRatio, int numberOfSignificantValueDigits)
/*     */     {
/* 239 */       super(numberOfSignificantValueDigits);
/* 240 */       this.containingInstanceId = id;
/*     */     }
/*     */     
/*     */     private InternalConcurrentDoubleHistogram(InternalConcurrentDoubleHistogram source) {
/* 244 */       super();
/* 245 */       this.containingInstanceId = source.containingInstanceId;
/*     */     }
/*     */   }
/*     */   
/*     */   void validateFitAsReplacementHistogram(DoubleHistogram replacementHistogram) {
/* 250 */     boolean bad = true;
/* 251 */     if (replacementHistogram == null) {
/* 252 */       bad = false;
/* 253 */     } else if ((replacementHistogram instanceof InternalConcurrentDoubleHistogram))
/*     */     {
/*     */ 
/* 256 */       if (((InternalConcurrentDoubleHistogram)replacementHistogram).containingInstanceId == this.activeHistogram.containingInstanceId)
/*     */       {
/* 258 */         bad = false;
/*     */       }
/*     */     }
/* 261 */     if (bad)
/*     */     {
/* 263 */       throw new IllegalArgumentException("replacement histogram must have been obtained via a previous getIntervalHistogram() call from this " + getClass().getName() + " instance");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\DoubleRecorder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */