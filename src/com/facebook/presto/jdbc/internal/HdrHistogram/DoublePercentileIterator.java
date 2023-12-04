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
/*    */ 
/*    */ public class DoublePercentileIterator
/*    */   implements Iterator<DoubleHistogramIterationValue>
/*    */ {
/*    */   private final PercentileIterator integerPercentileIterator;
/*    */   private final DoubleHistogramIterationValue iterationValue;
/*    */   DoubleHistogram histogram;
/*    */   
/*    */   public void reset(int percentileTicksPerHalfDistance)
/*    */   {
/* 29 */     this.integerPercentileIterator.reset(percentileTicksPerHalfDistance);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public DoublePercentileIterator(DoubleHistogram histogram, int percentileTicksPerHalfDistance)
/*    */   {
/* 37 */     this.histogram = histogram;
/* 38 */     this.integerPercentileIterator = new PercentileIterator(histogram.integerValuesHistogram, percentileTicksPerHalfDistance);
/*    */     
/*    */ 
/*    */ 
/* 42 */     this.iterationValue = new DoubleHistogramIterationValue(this.integerPercentileIterator.currentIterationValue);
/*    */   }
/*    */   
/*    */   public boolean hasNext()
/*    */   {
/* 47 */     return this.integerPercentileIterator.hasNext();
/*    */   }
/*    */   
/*    */   public DoubleHistogramIterationValue next()
/*    */   {
/* 52 */     this.integerPercentileIterator.next();
/* 53 */     return this.iterationValue;
/*    */   }
/*    */   
/*    */   public void remove()
/*    */   {
/* 58 */     this.integerPercentileIterator.remove();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\DoublePercentileIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */