/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
/*    */ import com.facebook.presto.jdbc.internal.spi.type.Type;
/*    */ import java.io.Closeable;
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
/*    */ public abstract interface RecordCursor
/*    */   extends Closeable
/*    */ {
/*    */   public abstract long getTotalBytes();
/*    */   
/*    */   public abstract long getCompletedBytes();
/*    */   
/*    */   public abstract long getReadTimeNanos();
/*    */   
/*    */   public abstract Type getType(int paramInt);
/*    */   
/*    */   public abstract boolean advanceNextPosition();
/*    */   
/*    */   public abstract boolean getBoolean(int paramInt);
/*    */   
/*    */   public abstract long getLong(int paramInt);
/*    */   
/*    */   public abstract double getDouble(int paramInt);
/*    */   
/*    */   public abstract Slice getSlice(int paramInt);
/*    */   
/*    */   public abstract Object getObject(int paramInt);
/*    */   
/*    */   public abstract boolean isNull(int paramInt);
/*    */   
/*    */   public long getSystemMemoryUsage()
/*    */   {
/* 49 */     return 0L;
/*    */   }
/*    */   
/*    */   public abstract void close();
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\RecordCursor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */