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
/*    */ public class DoubleLinearIterator
/*    */   implements Iterator<DoubleHistogramIterationValue>
/*    */ {
/*    */   private final LinearIterator integerLinearIterator;
/*    */   private final DoubleHistogramIterationValue iterationValue;
/*    */   DoubleHistogram histogram;
/*    */   
/*    */   public void reset(double valueUnitsPerBucket)
/*    */   {
/* 28 */     this.integerLinearIterator.reset((valueUnitsPerBucket * this.histogram.doubleToIntegerValueConversionRatio));
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public DoubleLinearIterator(DoubleHistogram histogram, double valueUnitsPerBucket)
/*    */   {
/* 36 */     this.histogram = histogram;
/* 37 */     this.integerLinearIterator = new LinearIterator(histogram.integerValuesHistogram, (valueUnitsPerBucket * histogram.doubleToIntegerValueConversionRatio));
/*    */     
/*    */ 
/*    */ 
/* 41 */     this.iterationValue = new DoubleHistogramIterationValue(this.integerLinearIterator.currentIterationValue);
/*    */   }
/*    */   
/*    */   public boolean hasNext()
/*    */   {
/* 46 */     return this.integerLinearIterator.hasNext();
/*    */   }
/*    */   
/*    */   public DoubleHistogramIterationValue next()
/*    */   {
/* 51 */     this.integerLinearIterator.next();
/* 52 */     return this.iterationValue;
/*    */   }
/*    */   
/*    */   public void remove()
/*    */   {
/* 57 */     this.integerLinearIterator.remove();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\DoubleLinearIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */