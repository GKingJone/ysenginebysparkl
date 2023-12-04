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
/*    */ 
/*    */ public class LogarithmicIterator
/*    */   extends AbstractHistogramIterator
/*    */   implements Iterator<HistogramIterationValue>
/*    */ {
/*    */   long valueUnitsInFirstBucket;
/*    */   double logBase;
/*    */   double nextValueReportingLevel;
/*    */   long currentStepHighestValueReportingLevel;
/*    */   long currentStepLowestValueReportingLevel;
/*    */   
/*    */   public void reset(long valueUnitsInFirstBucket, double logBase)
/*    */   {
/* 31 */     reset(this.histogram, valueUnitsInFirstBucket, logBase);
/*    */   }
/*    */   
/*    */   private void reset(AbstractHistogram histogram, long valueUnitsInFirstBucket, double logBase) {
/* 35 */     super.resetIterator(histogram);
/* 36 */     this.logBase = logBase;
/* 37 */     this.valueUnitsInFirstBucket = valueUnitsInFirstBucket;
/* 38 */     this.nextValueReportingLevel = valueUnitsInFirstBucket;
/* 39 */     this.currentStepHighestValueReportingLevel = (this.nextValueReportingLevel - 1L);
/* 40 */     this.currentStepLowestValueReportingLevel = histogram.lowestEquivalentValue(this.currentStepHighestValueReportingLevel);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public LogarithmicIterator(AbstractHistogram histogram, long valueUnitsInFirstBucket, double logBase)
/*    */   {
/* 49 */     reset(histogram, valueUnitsInFirstBucket, logBase);
/*    */   }
/*    */   
/*    */   public boolean hasNext()
/*    */   {
/* 54 */     if (super.hasNext()) {
/* 55 */       return true;
/*    */     }
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 61 */     return this.histogram.lowestEquivalentValue(this.nextValueReportingLevel) < this.nextValueAtIndex;
/*    */   }
/*    */   
/*    */   void incrementIterationLevel()
/*    */   {
/* 66 */     this.nextValueReportingLevel *= this.logBase;
/* 67 */     this.currentStepHighestValueReportingLevel = (this.nextValueReportingLevel - 1L);
/* 68 */     this.currentStepLowestValueReportingLevel = this.histogram.lowestEquivalentValue(this.currentStepHighestValueReportingLevel);
/*    */   }
/*    */   
/*    */   long getValueIteratedTo()
/*    */   {
/* 73 */     return this.currentStepHighestValueReportingLevel;
/*    */   }
/*    */   
/*    */   boolean reachedIterationLevel()
/*    */   {
/* 78 */     return (this.currentValueAtIndex >= this.currentStepLowestValueReportingLevel) || (this.currentIndex >= this.histogram.countsArrayLength - 1);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\LogarithmicIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */