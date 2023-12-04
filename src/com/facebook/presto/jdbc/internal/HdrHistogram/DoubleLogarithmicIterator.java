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
/*    */ public class DoubleLogarithmicIterator
/*    */   implements Iterator<DoubleHistogramIterationValue>
/*    */ {
/*    */   private final LogarithmicIterator integerLogarithmicIterator;
/*    */   private final DoubleHistogramIterationValue iterationValue;
/*    */   DoubleHistogram histogram;
/*    */   
/*    */   public void reset(double valueUnitsInFirstBucket, double logBase)
/*    */   {
/* 29 */     this.integerLogarithmicIterator.reset((valueUnitsInFirstBucket * this.histogram.doubleToIntegerValueConversionRatio), logBase);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public DoubleLogarithmicIterator(DoubleHistogram histogram, double valueUnitsInFirstBucket, double logBase)
/*    */   {
/* 42 */     this.histogram = histogram;
/* 43 */     this.integerLogarithmicIterator = new LogarithmicIterator(histogram.integerValuesHistogram, (valueUnitsInFirstBucket * histogram.doubleToIntegerValueConversionRatio), logBase);
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 48 */     this.iterationValue = new DoubleHistogramIterationValue(this.integerLogarithmicIterator.currentIterationValue);
/*    */   }
/*    */   
/*    */   public boolean hasNext()
/*    */   {
/* 53 */     return this.integerLogarithmicIterator.hasNext();
/*    */   }
/*    */   
/*    */   public DoubleHistogramIterationValue next()
/*    */   {
/* 58 */     this.integerLogarithmicIterator.next();
/* 59 */     return this.iterationValue;
/*    */   }
/*    */   
/*    */   public void remove()
/*    */   {
/* 64 */     this.integerLogarithmicIterator.remove();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\DoubleLogarithmicIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */