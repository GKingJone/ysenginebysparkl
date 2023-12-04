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
/*     */ public class SingleWriterDoubleRecorder
/*     */ {
/*  27 */   private static AtomicLong instanceIdSequencer = new AtomicLong(1L);
/*  28 */   private final long instanceId = instanceIdSequencer.getAndIncrement();
/*     */   
/*  30 */   private final WriterReaderPhaser recordingPhaser = new WriterReaderPhaser();
/*     */   
/*     */ 
/*     */ 
/*     */   private volatile InternalDoubleHistogram activeHistogram;
/*     */   
/*     */ 
/*     */ 
/*     */   private InternalDoubleHistogram inactiveHistogram;
/*     */   
/*     */ 
/*     */ 
/*     */   public SingleWriterDoubleRecorder(int numberOfSignificantValueDigits)
/*     */   {
/*  44 */     this.activeHistogram = new InternalDoubleHistogram(this.instanceId, numberOfSignificantValueDigits, null);
/*  45 */     this.inactiveHistogram = new InternalDoubleHistogram(this.instanceId, numberOfSignificantValueDigits, null);
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
/*     */   public SingleWriterDoubleRecorder(long highestToLowestValueRatio, int numberOfSignificantValueDigits)
/*     */   {
/*  60 */     this.activeHistogram = new InternalDoubleHistogram(this.instanceId, highestToLowestValueRatio, numberOfSignificantValueDigits, null);
/*     */     
/*  62 */     this.inactiveHistogram = new InternalDoubleHistogram(this.instanceId, highestToLowestValueRatio, numberOfSignificantValueDigits, null);
/*     */     
/*  64 */     this.activeHistogram.setStartTimeStamp(System.currentTimeMillis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void recordValue(double value)
/*     */   {
/*  73 */     long criticalValueAtEnter = this.recordingPhaser.writerCriticalSectionEnter();
/*     */     try {
/*  75 */       this.activeHistogram.recordValue(value);
/*     */     } finally {
/*  77 */       this.recordingPhaser.writerCriticalSectionExit(criticalValueAtEnter);
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
/*  89 */     long criticalValueAtEnter = this.recordingPhaser.writerCriticalSectionEnter();
/*     */     try {
/*  91 */       this.activeHistogram.recordValueWithCount(value, count);
/*     */     } finally {
/*  93 */       this.recordingPhaser.writerCriticalSectionExit(criticalValueAtEnter);
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
/* 115 */     long criticalValueAtEnter = this.recordingPhaser.writerCriticalSectionEnter();
/*     */     try {
/* 117 */       this.activeHistogram.recordValueWithExpectedInterval(value, expectedIntervalBetweenValueSamples);
/*     */     } finally {
/* 119 */       this.recordingPhaser.writerCriticalSectionExit(criticalValueAtEnter);
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
/* 133 */     return getIntervalHistogram(null);
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
/*     */ 
/*     */   public synchronized DoubleHistogram getIntervalHistogram(DoubleHistogram histogramToRecycle)
/*     */   {
/* 166 */     validateFitAsReplacementHistogram(histogramToRecycle);
/* 167 */     this.inactiveHistogram = ((InternalDoubleHistogram)histogramToRecycle);
/* 168 */     performIntervalSample();
/* 169 */     DoubleHistogram sampledHistogram = this.inactiveHistogram;
/* 170 */     this.inactiveHistogram = null;
/* 171 */     return sampledHistogram;
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
/* 184 */     performIntervalSample();
/* 185 */     this.inactiveHistogram.copyInto(targetHistogram);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void reset()
/*     */   {
/* 193 */     performIntervalSample();
/* 194 */     performIntervalSample();
/*     */   }
/*     */   
/*     */   private void performIntervalSample() {
/*     */     try {
/* 199 */       this.recordingPhaser.readerLock();
/*     */       
/*     */ 
/* 202 */       if (this.inactiveHistogram == null) {
/* 203 */         this.inactiveHistogram = new InternalDoubleHistogram(this.activeHistogram, null);
/*     */       }
/*     */       
/* 206 */       this.inactiveHistogram.reset();
/*     */       
/*     */ 
/* 209 */       InternalDoubleHistogram tempHistogram = this.inactiveHistogram;
/* 210 */       this.inactiveHistogram = this.activeHistogram;
/* 211 */       this.activeHistogram = tempHistogram;
/*     */       
/*     */ 
/* 214 */       long now = System.currentTimeMillis();
/* 215 */       this.activeHistogram.setStartTimeStamp(now);
/* 216 */       this.inactiveHistogram.setEndTimeStamp(now);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 221 */       this.recordingPhaser.flipPhase(500000L);
/*     */     } finally {
/* 223 */       this.recordingPhaser.readerUnlock();
/*     */     }
/*     */   }
/*     */   
/*     */   private class InternalDoubleHistogram extends DoubleHistogram {
/*     */     private final long containingInstanceId;
/*     */     
/*     */     private InternalDoubleHistogram(long id, int numberOfSignificantValueDigits) {
/* 231 */       super();
/* 232 */       this.containingInstanceId = id;
/*     */     }
/*     */     
/*     */ 
/*     */     private InternalDoubleHistogram(long id, long highestToLowestValueRatio, int numberOfSignificantValueDigits)
/*     */     {
/* 238 */       super(numberOfSignificantValueDigits);
/* 239 */       this.containingInstanceId = id;
/*     */     }
/*     */     
/*     */     private InternalDoubleHistogram(InternalDoubleHistogram source) {
/* 243 */       super();
/* 244 */       this.containingInstanceId = source.containingInstanceId;
/*     */     }
/*     */   }
/*     */   
/*     */   void validateFitAsReplacementHistogram(DoubleHistogram replacementHistogram) {
/* 249 */     boolean bad = true;
/* 250 */     if (replacementHistogram == null) {
/* 251 */       bad = false;
/* 252 */     } else if ((replacementHistogram instanceof InternalDoubleHistogram))
/*     */     {
/*     */ 
/* 255 */       if (((InternalDoubleHistogram)replacementHistogram).containingInstanceId == this.activeHistogram.containingInstanceId)
/*     */       {
/* 257 */         bad = false;
/*     */       }
/*     */     }
/* 260 */     if (bad)
/*     */     {
/* 262 */       throw new IllegalArgumentException("replacement histogram must have been obtained via a previousgetIntervalHistogram() call from this " + getClass().getName() + " instance");
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\SingleWriterDoubleRecorder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */