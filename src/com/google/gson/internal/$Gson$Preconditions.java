/*    */ package com.google.gson.internal;
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
/*    */ 
/*    */ public final class $Gson$Preconditions
/*    */ {
/*    */   public static <T> T checkNotNull(T obj)
/*    */   {
/* 34 */     if (obj == null) {
/* 35 */       throw new NullPointerException();
/*    */     }
/* 37 */     return obj;
/*    */   }
/*    */   
/*    */   public static void checkArgument(boolean condition) {
/* 41 */     if (!condition) {
/* 42 */       throw new IllegalArgumentException();
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\gson\internal\$Gson$Preconditions.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */