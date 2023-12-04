/*    */ package com.mchange.v1.util;
/*    */ 
/*    */ import com.mchange.v2.log.MLevel;
/*    */ import com.mchange.v2.log.MLog;
/*    */ import com.mchange.v2.log.MLogger;
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
/*    */ public final class ClosableResourceUtils
/*    */ {
/* 30 */   private static final MLogger logger = MLog.getLogger(ClosableResourceUtils.class);
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static Exception attemptClose(ClosableResource cr)
/*    */   {
/*    */     try
/*    */     {
/* 41 */       if (cr != null) cr.close();
/* 42 */       return null;
/*    */ 
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 47 */       if (logger.isLoggable(MLevel.WARNING))
/* 48 */         logger.log(MLevel.WARNING, "CloseableResource close FAILED.", e);
/* 49 */       return e;
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\util\ClosableResourceUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */