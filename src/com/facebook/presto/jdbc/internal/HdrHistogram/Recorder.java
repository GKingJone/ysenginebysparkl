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
/*     */ public class Recorder
/*     */ {
/*  29 */   private static AtomicLong instanceIdSequencer = new AtomicLong(1L);
/*  30 */   private final long instanceId = instanceIdSequencer.getAndIncrement();
/*     */   
/*  32 */   private final WriterReaderPhaser recordingPhaser = new WriterReaderPhaser();
/*     */   
/*     */ 
/*     */ 
/*     */   private volatile Histogram activeHistogram;
/*     */   
/*     */ 
/*     */ 
/*     */   private Histogram inactiveHistogram;
/*     */   
/*     */ 
/*     */ 
/*     */   public Recorder(int numberOfSignificantValueDigits)
/*     */   {
/*  46 */     this.activeHistogram = new InternalConcurrentHistogram(this.instanceId, numberOfSignificantValueDigits, null);
/*  47 */     this.inactiveHistogram = new InternalConcurrentHistogram(this.instanceId, numberOfSignificantValueDigits, null);
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
/*     */ 
/*     */   public Recorder(long highestTrackableValue, int numberOfSignificantValueDigits)
/*     */   {
/*  63 */     this(1L, highestTrackableValue, numberOfSignificantValueDigits);
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
/*     */   public Recorder(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits)
/*     */   {
/*  85 */     this.activeHistogram = new InternalAtomicHistogram(this.instanceId, lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits, null);
/*     */     
/*  87 */     this.inactiveHistogram = new InternalAtomicHistogram(this.instanceId, lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits, null);
/*     */     
/*  89 */     this.activeHistogram.setStartTimeStamp(System.currentTimeMillis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void recordValue(long value)
/*     */     throws ArrayIndexOutOfBoundsException
/*     */   {
/*  98 */     long criticalValueAtEnter = this.recordingPhaser.writerCriticalSectionEnter();
/*     */     try {
/* 100 */       this.activeHistogram.recordValue(value);
/*     */     } finally {
/* 102 */       this.recordingPhaser.writerCriticalSectionExit(criticalValueAtEnter);
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
/* 114 */     long criticalValueAtEnter = this.recordingPhaser.writerCriticalSectionEnter();
/*     */     try {
/* 116 */       this.activeHistogram.recordValueWithCount(value, count);
/*     */     } finally {
/* 118 */       this.recordingPhaser.writerCriticalSectionExit(criticalValueAtEnter);
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
/* 140 */     long criticalValueAtEnter = this.recordingPhaser.writerCriticalSectionEnter();
/*     */     try {
/* 142 */       this.activeHistogram.recordValueWithExpectedInterval(value, expectedIntervalBetweenValueSamples);
/*     */     } finally {
/* 144 */       this.recordingPhaser.writerCriticalSectionExit(criticalValueAtEnter);
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
/* 158 */     return getIntervalHistogram(null);
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
/* 190 */     validateFitAsReplacementHistogram(histogramToRecycle);
/* 191 */     this.inactiveHistogram = histogramToRecycle;
/* 192 */     performIntervalSample();
/* 193 */     Histogram sampledHistogram = this.inactiveHistogram;
/* 194 */     this.inactiveHistogram = null;
/* 195 */     return sampledHistogram;
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
/* 208 */     performIntervalSample();
/* 209 */     this.inactiveHistogram.copyInto(targetHistogram);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void reset()
/*     */   {
/* 217 */     performIntervalSample();
/* 218 */     performIntervalSample();
/*     */   }
/*     */   
/*     */   private void performIntervalSample() {
/*     */     try {
/* 223 */       this.recordingPhaser.readerLock();
/*     */       
/*     */ 
/* 226 */       if (this.inactiveHistogram == null) {
/* 227 */         if ((this.activeHistogram instanceof InternalAtomicHistogram))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 232 */           this.inactiveHistogram = new InternalAtomicHistogram(this.instanceId, this.activeHistogram.getLowestDiscernibleValue(), this.activeHistogram.getHighestTrackableValue(), this.activeHistogram.getNumberOfSignificantValueDigits(), null);
/*     */         }
/*     */         else
/*     */         {
/* 236 */           this.inactiveHistogram = new InternalConcurrentHistogram(this.instanceId, this.activeHistogram.getNumberOfSignificantValueDigits(), null);
/*     */         }
/*     */       }
/*     */       
/* 240 */       this.inactiveHistogram.reset();
/*     */       
/*     */ 
/* 243 */       Histogram tempHistogram = this.inactiveHistogram;
/* 244 */       this.inactiveHistogram = this.activeHistogram;
/* 245 */       this.activeHistogram = tempHistogram;
/*     */       
/*     */ 
/* 248 */       long now = System.currentTimeMillis();
/* 249 */       this.activeHistogram.setStartTimeStamp(now);
/* 250 */       this.inactiveHistogram.setEndTimeStamp(now);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 255 */       this.recordingPhaser.flipPhase(500000L);
/*     */     } finally {
/* 257 */       this.recordingPhaser.readerUnlock();
/*     */     }
/*     */   }
/*     */   
/*     */   private class InternalAtomicHistogram
/*     */     extends AtomicHistogram
/*     */   {
/*     */     private final long containingInstanceId;
/*     */     
/*     */     private InternalAtomicHistogram(long id, long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits)
/*     */     {
/* 268 */       super(highestTrackableValue, numberOfSignificantValueDigits);
/* 269 */       this.containingInstanceId = id;
/*     */     }
/*     */   }
/*     */   
/*     */   private class InternalConcurrentHistogram extends ConcurrentHistogram {
/*     */     private final long containingInstanceId;
/*     */     
/*     */     private InternalConcurrentHistogram(long id, int numberOfSignificantValueDigits) {
/* 277 */       super();
/* 278 */       this.containingInstanceId = id;
/*     */     }
/*     */   }
/*     */   
/*     */   void validateFitAsReplacementHistogram(Histogram replacementHistogram) {
/* 283 */     boolean bad = true;
/* 284 */     if (replacementHistogram == null) {
/* 285 */       bad = false;
/* 286 */     } else if ((replacementHistogram instanceof InternalAtomicHistogram)) {
/* 287 */       if ((this.activeHistogram instanceof InternalAtomicHistogram))
/*     */       {
/*     */ 
/* 290 */         if (((InternalAtomicHistogram)replacementHistogram).containingInstanceId == ((InternalAtomicHistogram)this.activeHistogram).containingInstanceId)
/*     */         {
/* 292 */           bad = false; }
/*     */       }
/* 294 */     } else if (((replacementHistogram instanceof InternalConcurrentHistogram)) && 
/* 295 */       ((this.activeHistogram instanceof InternalConcurrentHistogram)))
/*     */     {
/*     */ 
/* 298 */       if (((InternalConcurrentHistogram)replacementHistogram).containingInstanceId == ((InternalConcurrentHistogram)this.activeHistogram).containingInstanceId)
/*     */       {
/* 300 */         bad = false;
/*     */       }
/*     */     }
/* 303 */     if (bad)
/*     */     {
/* 305 */       throw new IllegalArgumentException("replacement histogram must have been obtained via a previous getIntervalHistogram() call from this " + getClass().getName() + " instance");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\Recorder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */