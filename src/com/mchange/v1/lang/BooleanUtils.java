/*    */ package com.mchange.v1.lang;
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
/*    */ public final class BooleanUtils
/*    */ {
/*    */   public static boolean parseBoolean(String str)
/*    */     throws IllegalArgumentException
/*    */   {
/* 30 */     if (str.equals("true"))
/* 31 */       return true;
/* 32 */     if (str.equals("false")) {
/* 33 */       return false;
/*    */     }
/* 35 */     throw new IllegalArgumentException("\"str\" is neither \"true\" nor \"false\".");
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\lang\BooleanUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */