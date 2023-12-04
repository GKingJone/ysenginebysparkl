/*    */ package com.facebook.presto.jdbc.internal.HdrHistogram;
/*    */ 
/*    */ import java.nio.ByteBuffer;
/*    */ import java.util.zip.DataFormatException;
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
/*    */ 
/*    */ 
/*    */ public abstract class EncodableHistogram
/*    */ {
/*    */   public abstract int getNeededByteBufferCapacity();
/*    */   
/*    */   public abstract int encodeIntoCompressedByteBuffer(ByteBuffer paramByteBuffer, int paramInt);
/*    */   
/*    */   public abstract long getStartTimeStamp();
/*    */   
/*    */   public abstract void setStartTimeStamp(long paramLong);
/*    */   
/*    */   public abstract long getEndTimeStamp();
/*    */   
/*    */   public abstract void setEndTimeStamp(long paramLong);
/*    */   
/*    */   public abstract String getTag();
/*    */   
/*    */   public abstract void setTag(String paramString);
/*    */   
/*    */   public abstract double getMaxValueAsDouble();
/*    */   
/*    */   static EncodableHistogram decodeFromCompressedByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue)
/*    */     throws DataFormatException
/*    */   {
/* 55 */     int cookie = buffer.getInt(buffer.position());
/* 56 */     if (DoubleHistogram.isDoubleHistogramCookie(cookie)) {
/* 57 */       return DoubleHistogram.decodeFromCompressedByteBuffer(buffer, minBarForHighestTrackableValue);
/*    */     }
/* 59 */     return Histogram.decodeFromCompressedByteBuffer(buffer, minBarForHighestTrackableValue);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\HdrHistogram\EncodableHistogram.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */