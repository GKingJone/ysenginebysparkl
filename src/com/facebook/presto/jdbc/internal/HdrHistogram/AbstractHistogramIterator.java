/*     */ package com.facebook.presto.jdbc.internal.HdrHistogram;
/*     */ 
/*     */ import java.util.ConcurrentModificationException;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class AbstractHistogramIterator
/*     */   implements Iterator<HistogramIterationValue>
/*     */ {
/*     */   AbstractHistogram histogram;
/*     */   long savedHistogramTotalRawCount;
/*     */   int currentIndex;
/*     */   long currentValueAtIndex;
/*     */   long nextValueAtIndex;
/*     */   long prevValueIteratedTo;
/*     */   long totalCountToPrevIndex;
/*     */   long totalCountToCurrentIndex;
/*     */   long totalValueToCurrentIndex;
/*     */   long arrayTotalCount;
/*     */   long countAtThisValue;
/*     */   private boolean freshSubBucket;
/*  35 */   final HistogramIterationValue currentIterationValue = new HistogramIterationValue();
/*     */   private double integerToDoubleValueConversionRatio;
/*     */   
/*     */   void resetIterator(AbstractHistogram histogram)
/*     */   {
/*  40 */     this.histogram = histogram;
/*  41 */     this.savedHistogramTotalRawCount = histogram.getTotalCount();
/*  42 */     this.arrayTotalCount = histogram.getTotalCount();
/*  43 */     this.integerToDoubleValueConversionRatio = histogram.getIntegerToDoubleValueConversionRatio();
/*  44 */     this.currentIndex = 0;
/*  45 */     this.currentValueAtIndex = 0L;
/*  46 */     this.nextValueAtIndex = (1 << histogram.unitMagnitude);
/*  47 */     this.prevValueIteratedTo = 0L;
/*  48 */     this.totalCountToPrevIndex = 0L;
/*  49 */     this.totalCountToCurrentIndex = 0L;
/*  50 */     this.totalValueToCurrentIndex = 0L;
/*  51 */     this.countAtThisValue = 0L;
/*  52 */     this.freshSubBucket = true;
/*  53 */     this.currentIterationValue.reset();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasNext()
/*     */   {
/*  64 */     if (this.histogram.getTotalCount() != this.savedHistogramTotalRawCount) {
/*  65 */       throw new ConcurrentModificationException();
/*     */     }
/*  67 */     return this.totalCountToCurrentIndex < this.arrayTotalCount;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public HistogramIterationValue next()
/*     */   {
/*  78 */     while (!exhaustedSubBuckets()) {
/*  79 */       this.countAtThisValue = this.histogram.getCountAtIndex(this.currentIndex);
/*  80 */       if (this.freshSubBucket) {
/*  81 */         this.totalCountToCurrentIndex += this.countAtThisValue;
/*  82 */         this.totalValueToCurrentIndex += this.countAtThisValue * this.histogram.highestEquivalentValue(this.currentValueAtIndex);
/*  83 */         this.freshSubBucket = false;
/*     */       }
/*  85 */       if (reachedIterationLevel()) {
/*  86 */         long valueIteratedTo = getValueIteratedTo();
/*  87 */         this.currentIterationValue.set(valueIteratedTo, this.prevValueIteratedTo, this.countAtThisValue, this.totalCountToCurrentIndex - this.totalCountToPrevIndex, this.totalCountToCurrentIndex, this.totalValueToCurrentIndex, 100.0D * this.totalCountToCurrentIndex / this.arrayTotalCount, 
/*     */         
/*     */ 
/*  90 */           getPercentileIteratedTo(), this.integerToDoubleValueConversionRatio);
/*  91 */         this.prevValueIteratedTo = valueIteratedTo;
/*  92 */         this.totalCountToPrevIndex = this.totalCountToCurrentIndex;
/*     */         
/*  94 */         incrementIterationLevel();
/*  95 */         if (this.histogram.getTotalCount() != this.savedHistogramTotalRawCount) {
/*  96 */           throw new ConcurrentModificationException();
/*     */         }
/*  98 */         return this.currentIterationValue;
/*     */       }
/* 100 */       incrementSubBucket();
/*     */     }
/*     */     
/* 103 */     throw new ArrayIndexOutOfBoundsException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void remove()
/*     */   {
/* 111 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */   abstract void incrementIterationLevel();
/*     */   
/*     */ 
/*     */   abstract boolean reachedIterationLevel();
/*     */   
/*     */   double getPercentileIteratedTo()
/*     */   {
/* 122 */     return 100.0D * this.totalCountToCurrentIndex / this.arrayTotalCount;
/*     */   }
/*     */   
/*     */   double getPercentileIteratedFrom() {
/* 126 */     return 100.0D * this.totalCountToPrevIndex / this.arrayTotalCount;
/*     */   }
/*     */   
/*     */   long getValueIteratedTo() {
/* 130 */     return this.histogram.highestEquivalentValue(this.currentValueAtIndex);
/*     */   }
/*     */   
/*     */   private boolean exhaustedSubBuckets() {
/* 134 */     return this.currentIndex >= this.histogram.countsArrayLength;
/*     */   }
/*     */   
/*     */   void incrementSubBucket() {
/* 138 */     this.freshSubBucket = true;
/*     */     
/* 140 */     this.currentIndex += 1;
/* 141 */     this.currentValueAtIndex = this.histogram.valueFromIndex(this.currentIndex);
/*     */     
/* 143 */     this.nextValueAtIndex = this.histogram.valueFromIndex(this.currentIndex + 1);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\AbstractHistogramIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */