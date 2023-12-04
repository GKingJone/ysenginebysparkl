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
/*    */ public class DoubleRecordedValuesIterator
/*    */   implements Iterator<DoubleHistogramIterationValue>
/*    */ {
/*    */   private final RecordedValuesIterator integerRecordedValuesIterator;
/*    */   private final DoubleHistogramIterationValue iterationValue;
/*    */   DoubleHistogram histogram;
/*    */   
/*    */   public void reset()
/*    */   {
/* 27 */     this.integerRecordedValuesIterator.reset();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public DoubleRecordedValuesIterator(DoubleHistogram histogram)
/*    */   {
/* 34 */     this.histogram = histogram;
/* 35 */     this.integerRecordedValuesIterator = new RecordedValuesIterator(histogram.integerValuesHistogram);
/* 36 */     this.iterationValue = new DoubleHistogramIterationValue(this.integerRecordedValuesIterator.currentIterationValue);
/*    */   }
/*    */   
/*    */   public boolean hasNext()
/*    */   {
/* 41 */     return this.integerRecordedValuesIterator.hasNext();
/*    */   }
/*    */   
/*    */   public DoubleHistogramIterationValue next()
/*    */   {
/* 46 */     this.integerRecordedValuesIterator.next();
/* 47 */     return this.iterationValue;
/*    */   }
/*    */   
/*    */   public void remove()
/*    */   {
/* 52 */     this.integerRecordedValuesIterator.remove();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\DoubleRecordedValuesIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */