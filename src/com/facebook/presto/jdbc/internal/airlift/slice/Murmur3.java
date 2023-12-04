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
/*    */ @Deprecated
/*    */ public class Murmur3
/*    */ {
/*    */   public static Slice hash(Slice data)
/*    */   {
/* 21 */     return Murmur3Hash128.hash(data);
/*    */   }
/*    */   
/*    */   public static Slice hash(Slice data, int offset, int length)
/*    */   {
/* 26 */     return Murmur3Hash128.hash(data, offset, length);
/*    */   }
/*    */   
/*    */   public static Slice hash(long seed, Slice data, int offset, int length)
/*    */   {
/* 31 */     return Murmur3Hash128.hash(seed, data, offset, length);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static long hash64(Slice data)
/*    */   {
/* 39 */     return Murmur3Hash128.hash64(data);
/*    */   }
/*    */   
/*    */   public static long hash64(Slice data, int offset, int length)
/*    */   {
/* 44 */     return Murmur3Hash128.hash64(data, offset, length);
/*    */   }
/*    */   
/*    */   public static long hash64(long seed, Slice data, int offset, int length)
/*    */   {
/* 49 */     return Murmur3Hash128.hash64(seed, data, offset, length);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static long hash64(long value)
/*    */   {
/* 57 */     return Murmur3Hash128.hash64(value);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\Murmur3.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */