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
/*    */ public class RecordedValuesIterator
/*    */   extends AbstractHistogramIterator
/*    */   implements Iterator<HistogramIterationValue>
/*    */ {
/*    */   int visitedIndex;
/*    */   
/*    */   public void reset()
/*    */   {
/* 25 */     reset(this.histogram);
/*    */   }
/*    */   
/*    */   private void reset(AbstractHistogram histogram) {
/* 29 */     super.resetIterator(histogram);
/* 30 */     this.visitedIndex = -1;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public RecordedValuesIterator(AbstractHistogram histogram)
/*    */   {
/* 37 */     reset(histogram);
/*    */   }
/*    */   
/*    */   void incrementIterationLevel()
/*    */   {
/* 42 */     this.visitedIndex = this.currentIndex;
/*    */   }
/*    */   
/*    */   boolean reachedIterationLevel()
/*    */   {
/* 47 */     long currentCount = this.histogram.getCountAtIndex(this.currentIndex);
/* 48 */     return (currentCount != 0L) && (this.visitedIndex != this.currentIndex);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\RecordedValuesIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */