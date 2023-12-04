/*    */ package com.facebook.presto.jdbc.internal.airlift.slice;
/*    */ 
/*    */ import sun.misc.Unsafe;
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
/*    */ public final class ByteArrays
/*    */ {
/*    */   public static short getShort(byte[] bytes, int index)
/*    */   {
/* 31 */     checkIndexLength(bytes.length, index, 2);
/* 32 */     return JvmUtils.unsafe.getShort(bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET + index);
/*    */   }
/*    */   
/*    */   public static int getInt(byte[] bytes, int index)
/*    */   {
/* 37 */     checkIndexLength(bytes.length, index, 4);
/* 38 */     return JvmUtils.unsafe.getInt(bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET + index);
/*    */   }
/*    */   
/*    */   public static long getLong(byte[] bytes, int index)
/*    */   {
/* 43 */     checkIndexLength(bytes.length, index, 8);
/* 44 */     return JvmUtils.unsafe.getLong(bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET + index);
/*    */   }
/*    */   
/*    */   public static float getFloat(byte[] bytes, int index)
/*    */   {
/* 49 */     checkIndexLength(bytes.length, index, 4);
/* 50 */     return JvmUtils.unsafe.getFloat(bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET + index);
/*    */   }
/*    */   
/*    */   public static double getDouble(byte[] bytes, int index)
/*    */   {
/* 55 */     checkIndexLength(bytes.length, index, 8);
/* 56 */     return JvmUtils.unsafe.getDouble(bytes, Unsafe.ARRAY_BYTE_BASE_OFFSET + index);
/*    */   }
/*    */   
/*    */   private static void checkIndexLength(int arrayLength, int index, int typeLength)
/*    */   {
/* 61 */     Preconditions.checkPositionIndexes(index, index + typeLength, arrayLength);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\ByteArrays.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */