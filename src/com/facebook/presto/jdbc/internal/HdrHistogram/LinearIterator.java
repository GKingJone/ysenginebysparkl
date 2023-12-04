/*    */ package com.facebook.presto.jdbc.internal.HdrHistogram;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LinearIterator
/*    */   extends AbstractHistogramIterator
/*    */   implements Iterator<HistogramIterationValue>
/*    */ {
/*    */   private long valueUnitsPerBucket;
/*    */   private long currentStepHighestValueReportingLevel;
/*    */   private long currentStepLowestValueReportingLevel;
/*    */   
/*    */   public void reset(long valueUnitsPerBucket)
/*    */   {
/* 28 */     reset(this.histogram, valueUnitsPerBucket);
/*    */   }
/*    */   
/*    */   private void reset(AbstractHistogram histogram, long valueUnitsPerBucket) {
/* 32 */     super.resetIterator(histogram);
/* 33 */     this.valueUnitsPerBucket = valueUnitsPerBucket;
/* 34 */     this.currentStepHighestValueReportingLevel = (valueUnitsPerBucket - 1L);
/* 35 */     this.currentStepLowestValueReportingLevel = histogram.lowestEquivalentValue(this.currentStepHighestValueReportingLevel);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public LinearIterator(AbstractHistogram histogram, long valueUnitsPerBucket)
/*    */   {
/* 43 */     reset(histogram, valueUnitsPerBucket);
/*    */   }
/*    */   
/*    */   public boolean hasNext()
/*    */   {
/* 48 */     if (super.hasNext()) {
/* 49 */       return true;
/*    */     }
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 55 */     return this.currentStepHighestValueReportingLevel + 1L < this.nextValueAtIndex;
/*    */   }
/*    */   
/*    */   void incrementIterationLevel()
/*    */   {
/* 60 */     this.currentStepHighestValueReportingLevel += this.valueUnitsPerBucket;
/* 61 */     this.currentStepLowestValueReportingLevel = this.histogram.lowestEquivalentValue(this.currentStepHighestValueReportingLevel);
/*    */   }
/*    */   
/*    */   long getValueIteratedTo()
/*    */   {
/* 66 */     return this.currentStepHighestValueReportingLevel;
/*    */   }
/*    */   
/*    */   boolean reachedIterationLevel()
/*    */   {
/* 71 */     return (this.currentValueAtIndex >= this.currentStepLowestValueReportingLevel) || (this.currentIndex >= this.histogram.countsArrayLength - 1);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\LinearIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */