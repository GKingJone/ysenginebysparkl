/*    */ package com.facebook.presto.jdbc.internal.HdrHistogram;
/*    */ 
/*    */ import java.nio.ByteBuffer;
/*    */ import java.util.concurrent.atomic.AtomicLong;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ abstract class AbstractHistogramBase
/*    */   extends EncodableHistogram
/*    */ {
/* 33 */   static AtomicLong constructionIdentityCount = new AtomicLong(0L);
/*    */   
/*    */   long identity;
/*    */   
/* 37 */   volatile boolean autoResize = false;
/*    */   
/*    */   long highestTrackableValue;
/*    */   
/*    */   long lowestDiscernibleValue;
/*    */   
/*    */   int numberOfSignificantValueDigits;
/*    */   
/*    */   int bucketCount;
/*    */   
/*    */   int subBucketCount;
/*    */   
/*    */   int countsArrayLength;
/*    */   
/*    */   int wordSizeInBytes;
/* 52 */   long startTimeStampMsec = Long.MAX_VALUE;
/* 53 */   long endTimeStampMsec = 0L;
/* 54 */   String tag = null;
/*    */   
/* 56 */   double integerToDoubleValueConversionRatio = 1.0D;
/*    */   
/*    */   PercentileIterator percentileIterator;
/*    */   
/*    */   RecordedValuesIterator recordedValuesIterator;
/* 61 */   ByteBuffer intermediateUncompressedByteBuffer = null;
/* 62 */   byte[] intermediateUncompressedByteArray = null;
/*    */   
/*    */   double getIntegerToDoubleValueConversionRatio() {
/* 65 */     return this.integerToDoubleValueConversionRatio;
/*    */   }
/*    */   
/*    */   void setIntegerToDoubleValueConversionRatio(double integerToDoubleValueConversionRatio) {
/* 69 */     this.integerToDoubleValueConversionRatio = integerToDoubleValueConversionRatio;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\AbstractHistogramBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */