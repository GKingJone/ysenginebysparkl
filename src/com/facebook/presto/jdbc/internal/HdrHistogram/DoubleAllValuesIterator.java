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
/*    */ public class DoubleAllValuesIterator
/*    */   implements Iterator<DoubleHistogramIterationValue>
/*    */ {
/*    */   private final AllValuesIterator integerAllValuesIterator;
/*    */   private final DoubleHistogramIterationValue iterationValue;
/*    */   DoubleHistogram histogram;
/*    */   
/*    */   public void reset()
/*    */   {
/* 26 */     this.integerAllValuesIterator.reset();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public DoubleAllValuesIterator(DoubleHistogram histogram)
/*    */   {
/* 33 */     this.histogram = histogram;
/* 34 */     this.integerAllValuesIterator = new AllValuesIterator(histogram.integerValuesHistogram);
/* 35 */     this.iterationValue = new DoubleHistogramIterationValue(this.integerAllValuesIterator.currentIterationValue);
/*    */   }
/*    */   
/*    */   public boolean hasNext()
/*    */   {
/* 40 */     return this.integerAllValuesIterator.hasNext();
/*    */   }
/*    */   
/*    */   public DoubleHistogramIterationValue next()
/*    */   {
/* 45 */     this.integerAllValuesIterator.next();
/* 46 */     return this.iterationValue;
/*    */   }
/*    */   
/*    */   public void remove()
/*    */   {
/* 51 */     this.integerAllValuesIterator.remove();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\DoubleAllValuesIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */