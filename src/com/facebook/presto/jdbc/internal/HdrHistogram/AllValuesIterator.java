/*    */ package com.facebook.presto.jdbc.internal.HdrHistogram;
/*    */ 
/*    */ import java.util.ConcurrentModificationException;
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
/*    */ public class AllValuesIterator
/*    */   extends AbstractHistogramIterator
/*    */   implements Iterator<HistogramIterationValue>
/*    */ {
/*    */   int visitedIndex;
/*    */   
/*    */   public void reset()
/*    */   {
/* 26 */     reset(this.histogram);
/*    */   }
/*    */   
/*    */   private void reset(AbstractHistogram histogram) {
/* 30 */     super.resetIterator(histogram);
/* 31 */     this.visitedIndex = -1;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public AllValuesIterator(AbstractHistogram histogram)
/*    */   {
/* 38 */     reset(histogram);
/*    */   }
/*    */   
/*    */   void incrementIterationLevel()
/*    */   {
/* 43 */     this.visitedIndex = this.currentIndex;
/*    */   }
/*    */   
/*    */   boolean reachedIterationLevel()
/*    */   {
/* 48 */     return this.visitedIndex != this.currentIndex;
/*    */   }
/*    */   
/*    */   public boolean hasNext()
/*    */   {
/* 53 */     if (this.histogram.getTotalCount() != this.savedHistogramTotalRawCount) {
/* 54 */       throw new ConcurrentModificationException();
/*    */     }
/*    */     
/* 57 */     return this.currentIndex < this.histogram.countsArrayLength - 1;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\AllValuesIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */