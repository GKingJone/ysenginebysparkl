/*    */ package com.facebook.presto.jdbc.internal.HdrHistogram;
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
/*    */ 
/*    */ public class DoubleHistogramIterationValue
/*    */ {
/*    */   private final HistogramIterationValue integerHistogramIterationValue;
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
/*    */   void reset()
/*    */   {
/* 40 */     this.integerHistogramIterationValue.reset();
/*    */   }
/*    */   
/*    */   DoubleHistogramIterationValue(HistogramIterationValue integerHistogramIterationValue) {
/* 44 */     this.integerHistogramIterationValue = integerHistogramIterationValue;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public String toString()
/*    */   {
/* 55 */     return "valueIteratedTo:" + getValueIteratedTo() + ", prevValueIteratedTo:" + getValueIteratedFrom() + ", countAtValueIteratedTo:" + getCountAtValueIteratedTo() + ", countAddedInThisIterationStep:" + getCountAddedInThisIterationStep() + ", totalCountToThisValue:" + getTotalCountToThisValue() + ", totalValueToThisValue:" + getTotalValueToThisValue() + ", percentile:" + getPercentile() + ", percentileLevelIteratedTo:" + getPercentileLevelIteratedTo();
/*    */   }
/*    */   
/*    */   public double getValueIteratedTo()
/*    */   {
/* 60 */     return this.integerHistogramIterationValue.getValueIteratedTo() * this.integerHistogramIterationValue.getIntegerToDoubleValueConversionRatio();
/*    */   }
/*    */   
/*    */   public double getValueIteratedFrom()
/*    */   {
/* 65 */     return this.integerHistogramIterationValue.getValueIteratedFrom() * this.integerHistogramIterationValue.getIntegerToDoubleValueConversionRatio();
/*    */   }
/*    */   
/*    */   public long getCountAtValueIteratedTo() {
/* 69 */     return this.integerHistogramIterationValue.getCountAtValueIteratedTo();
/*    */   }
/*    */   
/*    */   public long getCountAddedInThisIterationStep() {
/* 73 */     return this.integerHistogramIterationValue.getCountAddedInThisIterationStep();
/*    */   }
/*    */   
/*    */   public long getTotalCountToThisValue() {
/* 77 */     return this.integerHistogramIterationValue.getTotalCountToThisValue();
/*    */   }
/*    */   
/*    */   public double getTotalValueToThisValue()
/*    */   {
/* 82 */     return this.integerHistogramIterationValue.getTotalValueToThisValue() * this.integerHistogramIterationValue.getIntegerToDoubleValueConversionRatio();
/*    */   }
/*    */   
/*    */   public double getPercentile() {
/* 86 */     return this.integerHistogramIterationValue.getPercentile();
/*    */   }
/*    */   
/*    */   public double getPercentileLevelIteratedTo() {
/* 90 */     return this.integerHistogramIterationValue.getPercentileLevelIteratedTo();
/*    */   }
/*    */   
/*    */   public HistogramIterationValue getIntegerHistogramIterationValue() {
/* 94 */     return this.integerHistogramIterationValue;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\DoubleHistogramIterationValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */