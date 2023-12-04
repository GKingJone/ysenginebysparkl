/*    */ package com.mchange.v2.lang;
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
/*    */ public final class ObjectUtils
/*    */ {
/*    */   public static boolean eqOrBothNull(Object a, Object b)
/*    */   {
/* 30 */     if (a == b)
/* 31 */       return true;
/* 32 */     if (a == null) {
/* 33 */       return false;
/*    */     }
/* 35 */     return a.equals(b);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static int hashOrZero(Object o)
/*    */   {
/* 44 */     return o == null ? 0 : o.hashCode();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\lang\ObjectUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */