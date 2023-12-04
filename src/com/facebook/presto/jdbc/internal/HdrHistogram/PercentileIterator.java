/*     */ package com.facebook.presto.jdbc.internal.HdrHistogram;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PercentileIterator
/*     */   extends AbstractHistogramIterator
/*     */   implements Iterator<HistogramIterationValue>
/*     */ {
/*     */   int percentileTicksPerHalfDistance;
/*     */   double percentileLevelToIterateTo;
/*     */   double percentileLevelToIterateFrom;
/*     */   boolean reachedLastRecordedValue;
/*     */   
/*     */   public void reset(int percentileTicksPerHalfDistance)
/*     */   {
/*  30 */     reset(this.histogram, percentileTicksPerHalfDistance);
/*     */   }
/*     */   
/*     */   private void reset(AbstractHistogram histogram, int percentileTicksPerHalfDistance) {
/*  34 */     super.resetIterator(histogram);
/*  35 */     this.percentileTicksPerHalfDistance = percentileTicksPerHalfDistance;
/*  36 */     this.percentileLevelToIterateTo = 0.0D;
/*  37 */     this.percentileLevelToIterateFrom = 0.0D;
/*  38 */     this.reachedLastRecordedValue = false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public PercentileIterator(AbstractHistogram histogram, int percentileTicksPerHalfDistance)
/*     */   {
/*  46 */     reset(histogram, percentileTicksPerHalfDistance);
/*     */   }
/*     */   
/*     */   public boolean hasNext()
/*     */   {
/*  51 */     if (super.hasNext()) {
/*  52 */       return true;
/*     */     }
/*  54 */     if ((!this.reachedLastRecordedValue) && (this.arrayTotalCount > 0L)) {
/*  55 */       this.percentileLevelToIterateTo = 100.0D;
/*  56 */       this.reachedLastRecordedValue = true;
/*  57 */       return true;
/*     */     }
/*  59 */     return false;
/*     */   }
/*     */   
/*     */   void incrementIterationLevel()
/*     */   {
/*  64 */     this.percentileLevelToIterateFrom = this.percentileLevelToIterateTo;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  80 */     long percentileReportingTicks = this.percentileTicksPerHalfDistance * Math.pow(2.0D, 
/*  81 */       (Math.log(100.0D / (100.0D - this.percentileLevelToIterateTo)) / Math.log(2.0D)) + 1L);
/*  82 */     this.percentileLevelToIterateTo += 100.0D / percentileReportingTicks;
/*     */   }
/*     */   
/*     */   boolean reachedIterationLevel()
/*     */   {
/*  87 */     if (this.countAtThisValue == 0L)
/*  88 */       return false;
/*  89 */     double currentPercentile = 100.0D * this.totalCountToCurrentIndex / this.arrayTotalCount;
/*  90 */     return currentPercentile >= this.percentileLevelToIterateTo;
/*     */   }
/*     */   
/*     */   double getPercentileIteratedTo()
/*     */   {
/*  95 */     return this.percentileLevelToIterateTo;
/*     */   }
/*     */   
/*     */   double getPercentileIteratedFrom()
/*     */   {
/* 100 */     return this.percentileLevelToIterateFrom;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\PercentileIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */