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
/*    */ public final class ThreadGroupUtils
/*    */ {
/*    */   public static ThreadGroup rootThreadGroup()
/*    */   {
/* 30 */     ThreadGroup tg = Thread.currentThread().getThreadGroup();
/* 31 */     ThreadGroup ptg = tg.getParent();
/* 32 */     while (ptg != null)
/*    */     {
/* 34 */       tg = ptg;
/* 35 */       ptg = tg.getParent();
/*    */     }
/* 37 */     return tg;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\lang\ThreadGroupUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */