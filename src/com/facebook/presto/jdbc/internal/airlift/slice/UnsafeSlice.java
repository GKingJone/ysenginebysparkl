/*    */ package com.facebook.presto.jdbc.internal.airlift.slice;
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
/*    */ public final class UnsafeSlice
/*    */ {
/*    */   public static byte getByteUnchecked(Slice slice, int index)
/*    */   {
/* 22 */     return slice.getByteUnchecked(index);
/*    */   }
/*    */   
/*    */   public static short getShortUnchecked(Slice slice, int index)
/*    */   {
/* 27 */     return slice.getShortUnchecked(index);
/*    */   }
/*    */   
/*    */   public static int getIntUnchecked(Slice slice, int index)
/*    */   {
/* 32 */     return slice.getIntUnchecked(index);
/*    */   }
/*    */   
/*    */   public static long getLongUnchecked(Slice slice, int index)
/*    */   {
/* 37 */     return slice.getLongUnchecked(index);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\UnsafeSlice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */