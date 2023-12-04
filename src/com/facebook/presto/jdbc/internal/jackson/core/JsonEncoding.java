/*    */ package com.facebook.presto.jdbc.internal.jackson.core;
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
/*    */ public enum JsonEncoding
/*    */ {
/* 19 */   UTF8("UTF-8", false, 8), 
/* 20 */   UTF16_BE("UTF-16BE", true, 16), 
/* 21 */   UTF16_LE("UTF-16LE", false, 16), 
/* 22 */   UTF32_BE("UTF-32BE", true, 32), 
/* 23 */   UTF32_LE("UTF-32LE", false, 32);
/*    */   
/*    */ 
/*    */   protected final String _javaName;
/*    */   
/*    */   protected final boolean _bigEndian;
/*    */   
/*    */   protected final int _bits;
/*    */   
/*    */   private JsonEncoding(String javaName, boolean bigEndian, int bits)
/*    */   {
/* 34 */     this._javaName = javaName;
/* 35 */     this._bigEndian = bigEndian;
/* 36 */     this._bits = bits;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getJavaName()
/*    */   {
/* 44 */     return this._javaName;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 54 */   public boolean isBigEndian() { return this._bigEndian; }
/*    */   
/* 56 */   public int bits() { return this._bits; }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\core\JsonEncoding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */