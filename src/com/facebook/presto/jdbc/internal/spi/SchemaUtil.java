/*    */ package com.facebook.presto.jdbc.internal.spi;
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
/*    */ final class SchemaUtil
/*    */ {
/*    */   static String checkNotEmpty(String value, String name)
/*    */   {
/* 24 */     if (value == null) {
/* 25 */       throw new NullPointerException(name + " is null");
/*    */     }
/* 27 */     if (value.isEmpty()) {
/* 28 */       throw new IllegalArgumentException(name + " is empty");
/*    */     }
/* 30 */     return value;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\SchemaUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */