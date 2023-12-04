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
/*    */ public class InvalidCodePointException
/*    */   extends IllegalArgumentException
/*    */ {
/*    */   private final int codePoint;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public InvalidCodePointException(int codePoint)
/*    */   {
/* 25 */     super("Invalid code point 0x" + Integer.toHexString(codePoint).toUpperCase());
/* 26 */     this.codePoint = codePoint;
/*    */   }
/*    */   
/*    */   public int getCodePoint()
/*    */   {
/* 31 */     return this.codePoint;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\slice\InvalidCodePointException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */