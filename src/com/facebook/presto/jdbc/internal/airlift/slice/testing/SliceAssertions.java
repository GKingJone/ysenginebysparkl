/*    */ package com.facebook.presto.jdbc.internal.airlift.slice.testing;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.slice.Slice;
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
/*    */ public class SliceAssertions
/*    */ {
/*    */   public static void assertSlicesEqual(Slice actual, Slice expected)
/*    */   {
/* 22 */     if ((actual == null) && (expected == null)) {
/* 23 */       return;
/*    */     }
/* 25 */     if (actual == null) {
/* 26 */       throw new AssertionError("Actual is null");
/*    */     }
/* 28 */     if (expected == null) {
/* 29 */       throw new AssertionError("Expected actual to be null");
/*    */     }
/*    */     
/* 32 */     if (actual.length() != expected.length()) {
/* 33 */       throw new AssertionError(String.format("Slices differ in size. Actual: %s, expected: %s", new Object[] { Integer.valueOf(actual.length()), Integer.valueOf(expected.length()) }));
/*    */     }
/*    */     
/* 36 */     for (int i = 0; i < actual.length(); i++) {
/* 37 */       if (actual.getByte(i) != expected.getByte(i)) {
/* 38 */         throw new AssertionError(String.format("Slices differ at index %s. Actual: 0x%02x, expected: 0x%02x", new Object[] { Integer.valueOf(i), Short.valueOf(actual.getUnsignedByte(i)), Short.valueOf(expected.getUnsignedByte(i)) }));
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\testing\SliceAssertions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */