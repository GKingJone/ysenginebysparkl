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
/*    */ 
/*    */ /**
/*    */  * @deprecated
/*    */  */
/*    */ public final class NullUtils
/*    */ {
/*    */   public static boolean equalsOrBothNull(Object a, Object b)
/*    */   {
/* 33 */     if (a == b)
/* 34 */       return true;
/* 35 */     if (a == null) {
/* 36 */       return false;
/*    */     }
/* 38 */     return a.equals(b);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\lang\NullUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */