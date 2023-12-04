/*     */ package com.facebook.presto.jdbc.internal.HdrHistogram;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HistogramIterationValue
/*     */ {
/*     */   private long valueIteratedTo;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private long valueIteratedFrom;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private long countAtValueIteratedTo;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private long countAddedInThisIterationStep;
/*     */   
/*     */ 
/*     */ 
/*     */   private long totalCountToThisValue;
/*     */   
/*     */ 
/*     */ 
/*     */   private long totalValueToThisValue;
/*     */   
/*     */ 
/*     */ 
/*     */   private double percentile;
/*     */   
/*     */ 
/*     */ 
/*     */   private double percentileLevelIteratedTo;
/*     */   
/*     */ 
/*     */ 
/*     */   private double integerToDoubleValueConversionRatio;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   void set(long valueIteratedTo, long valueIteratedFrom, long countAtValueIteratedTo, long countInThisIterationStep, long totalCountToThisValue, long totalValueToThisValue, double percentile, double percentileLevelIteratedTo, double integerToDoubleValueConversionRatio)
/*     */   {
/*  51 */     this.valueIteratedTo = valueIteratedTo;
/*  52 */     this.valueIteratedFrom = valueIteratedFrom;
/*  53 */     this.countAtValueIteratedTo = countAtValueIteratedTo;
/*  54 */     this.countAddedInThisIterationStep = countInThisIterationStep;
/*  55 */     this.totalCountToThisValue = totalCountToThisValue;
/*  56 */     this.totalValueToThisValue = totalValueToThisValue;
/*  57 */     this.percentile = percentile;
/*  58 */     this.percentileLevelIteratedTo = percentileLevelIteratedTo;
/*  59 */     this.integerToDoubleValueConversionRatio = integerToDoubleValueConversionRatio;
/*     */   }
/*     */   
/*     */   void reset() {
/*  63 */     this.valueIteratedTo = 0L;
/*  64 */     this.valueIteratedFrom = 0L;
/*  65 */     this.countAtValueIteratedTo = 0L;
/*  66 */     this.countAddedInThisIterationStep = 0L;
/*  67 */     this.totalCountToThisValue = 0L;
/*  68 */     this.totalValueToThisValue = 0L;
/*  69 */     this.percentile = 0.0D;
/*  70 */     this.percentileLevelIteratedTo = 0.0D;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/*  77 */     return "valueIteratedTo:" + this.valueIteratedTo + ", prevValueIteratedTo:" + this.valueIteratedFrom + ", countAtValueIteratedTo:" + this.countAtValueIteratedTo + ", countAddedInThisIterationStep:" + this.countAddedInThisIterationStep + ", totalCountToThisValue:" + this.totalCountToThisValue + ", totalValueToThisValue:" + this.totalValueToThisValue + ", percentile:" + this.percentile + ", percentileLevelIteratedTo:" + this.percentileLevelIteratedTo;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getValueIteratedTo()
/*     */   {
/*  88 */     return this.valueIteratedTo;
/*     */   }
/*     */   
/*     */   public double getDoubleValueIteratedTo() {
/*  92 */     return this.valueIteratedTo * this.integerToDoubleValueConversionRatio;
/*     */   }
/*     */   
/*     */   public long getValueIteratedFrom() {
/*  96 */     return this.valueIteratedFrom;
/*     */   }
/*     */   
/*     */   public double getDoubleValueIteratedFrom() {
/* 100 */     return this.valueIteratedFrom * this.integerToDoubleValueConversionRatio;
/*     */   }
/*     */   
/*     */   public long getCountAtValueIteratedTo() {
/* 104 */     return this.countAtValueIteratedTo;
/*     */   }
/*     */   
/*     */   public long getCountAddedInThisIterationStep() {
/* 108 */     return this.countAddedInThisIterationStep;
/*     */   }
/*     */   
/*     */   public long getTotalCountToThisValue() {
/* 112 */     return this.totalCountToThisValue;
/*     */   }
/*     */   
/*     */   public long getTotalValueToThisValue() {
/* 116 */     return this.totalValueToThisValue;
/*     */   }
/*     */   
/*     */   public double getPercentile() {
/* 120 */     return this.percentile;
/*     */   }
/*     */   
/*     */   public double getPercentileLevelIteratedTo() {
/* 124 */     return this.percentileLevelIteratedTo;
/*     */   }
/*     */   
/* 127 */   public double getIntegerToDoubleValueConversionRatio() { return this.integerToDoubleValueConversionRatio; }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\HistogramIterationValue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */