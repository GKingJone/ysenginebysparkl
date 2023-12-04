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
/*     */ public class SingleWriterRecorder
/*     */ {
/*  27 */   private static AtomicLong instanceIdSequencer = new AtomicLong(1L);
/*  28 */   private final long instanceId = instanceIdSequencer.getAndIncrement();
/*     */   
/*  30 */   private final WriterReaderPhaser recordingPhaser = new WriterReaderPhaser();
/*     */   
/*     */ 
/*     */ 
/*     */   private volatile InternalHistogram activeHistogram;
/*     */   
/*     */ 
/*     */ 
/*     */   private InternalHistogram inactiveHistogram;
/*     */   
/*     */ 
/*     */ 
/*     */   public SingleWriterRecorder(int numberOfSignificantValueDigits)
/*     */   {
/*  44 */     this.activeHistogram = new InternalHistogram(this.instanceId, numberOfSignificantValueDigits, null);
/*  45 */     this.inactiveHistogram = new InternalHistogram(this.instanceId, numberOfSignificantValueDigits, null);
/*  46 */     this.activeHistogram.setStartTimeStamp(System.currentTimeMillis());
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
/*     */   public SingleWriterRecorder(long highestTrackableValue, int numberOfSignificantValueDigits)
/*     */   {
/*  62 */     this(1L, highestTrackableValue, numberOfSignificantValueDigits);
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
/*     */   public SingleWriterRecorder(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits)
/*     */   {
/*  84 */     this.activeHistogram = new InternalHistogram(this.instanceId, lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits, null);
/*     */     
/*  86 */     this.inactiveHistogram = new InternalHistogram(this.instanceId, lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits, null);
/*     */     
/*  88 */     this.activeHistogram.setStartTimeStamp(System.currentTimeMillis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void recordValue(long value)
/*     */   {
/*  97 */     long criticalValueAtEnter = this.recordingPhaser.writerCriticalSectionEnter();
/*     */     try {
/*  99 */       this.activeHistogram.recordValue(value);
/*     */     } finally {
/* 101 */       this.recordingPhaser.writerCriticalSectionExit(criticalValueAtEnter);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void recordValueWithCount(long value, long count)
/*     */     throws ArrayIndexOutOfBoundsException
/*     */   {
/* 113 */     long criticalValueAtEnter = this.recordingPhaser.writerCriticalSectionEnter();
/*     */     try {
/* 115 */       this.activeHistogram.recordValueWithCount(value, count);
/*     */     } finally {
/* 117 */       this.recordingPhaser.writerCriticalSectionExit(criticalValueAtEnter);
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
/*     */   public void recordValueWithExpectedInterval(long value, long expectedIntervalBetweenValueSamples)
/*     */     throws ArrayIndexOutOfBoundsException
/*     */   {
/* 139 */     long criticalValueAtEnter = this.recordingPhaser.writerCriticalSectionEnter();
/*     */     try {
/* 141 */       this.activeHistogram.recordValueWithExpectedInterval(value, expectedIntervalBetweenValueSamples);
/*     */     } finally {
/* 143 */       this.recordingPhaser.writerCriticalSectionExit(criticalValueAtEnter);
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
/*     */   public synchronized Histogram getIntervalHistogram()
/*     */   {
/* 157 */     return getIntervalHistogram(null);
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
/*     */   public synchronized Histogram getIntervalHistogram(Histogram histogramToRecycle)
/*     */   {
/* 189 */     validateFitAsReplacementHistogram(histogramToRecycle);
/* 190 */     this.inactiveHistogram = ((InternalHistogram)histogramToRecycle);
/* 191 */     performIntervalSample();
/* 192 */     Histogram sampledHistogram = this.inactiveHistogram;
/* 193 */     this.inactiveHistogram = null;
/* 194 */     return sampledHistogram;
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
/*     */   public synchronized void getIntervalHistogramInto(Histogram targetHistogram)
/*     */   {
/* 207 */     performIntervalSample();
/* 208 */     this.inactiveHistogram.copyInto(targetHistogram);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void reset()
/*     */   {
/* 216 */     performIntervalSample();
/* 217 */     performIntervalSample();
/*     */   }
/*     */   
/*     */   private void performIntervalSample() {
/*     */     try {
/* 222 */       this.recordingPhaser.readerLock();
/*     */       
/*     */ 
/* 225 */       if (this.inactiveHistogram == null) {
/* 226 */         this.inactiveHistogram = new InternalHistogram(this.activeHistogram, null);
/*     */       }
/*     */       
/* 229 */       this.inactiveHistogram.reset();
/*     */       
/*     */ 
/* 232 */       InternalHistogram tempHistogram = this.inactiveHistogram;
/* 233 */       this.inactiveHistogram = this.activeHistogram;
/* 234 */       this.activeHistogram = tempHistogram;
/*     */       
/*     */ 
/* 237 */       long now = System.currentTimeMillis();
/* 238 */       this.activeHistogram.setStartTimeStamp(now);
/* 239 */       this.inactiveHistogram.setEndTimeStamp(now);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 244 */       this.recordingPhaser.flipPhase(500000L);
/*     */     } finally {
/* 246 */       this.recordingPhaser.readerUnlock();
/*     */     }
/*     */   }
/*     */   
/*     */   private class InternalHistogram extends Histogram {
/*     */     private final long containingInstanceId;
/*     */     
/*     */     private InternalHistogram(long id, int numberOfSignificantValueDigits) {
/* 254 */       super();
/* 255 */       this.containingInstanceId = id;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private InternalHistogram(long id, long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits)
/*     */     {
/* 262 */       super(highestTrackableValue, numberOfSignificantValueDigits);
/* 263 */       this.containingInstanceId = id;
/*     */     }
/*     */     
/*     */     private InternalHistogram(InternalHistogram source) {
/* 267 */       super();
/* 268 */       this.containingInstanceId = source.containingInstanceId;
/*     */     }
/*     */   }
/*     */   
/*     */   void validateFitAsReplacementHistogram(Histogram replacementHistogram) {
/* 273 */     boolean bad = true;
/* 274 */     if (replacementHistogram == null) {
/* 275 */       bad = false;
/* 276 */     } else if ((replacementHistogram instanceof InternalHistogram))
/*     */     {
/*     */ 
/* 279 */       if (((InternalHistogram)replacementHistogram).containingInstanceId == this.activeHistogram.containingInstanceId)
/*     */       {
/* 281 */         bad = false;
/*     */       }
/*     */     }
/* 284 */     if (bad)
/*     */     {
/* 286 */       throw new IllegalArgumentException("replacement histogram must have been obtained via a previousgetIntervalHistogram() call from this " + getClass().getName() + " instance");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\SingleWriterRecorder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */