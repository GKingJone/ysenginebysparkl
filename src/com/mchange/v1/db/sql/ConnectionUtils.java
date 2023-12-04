/*    */ package com.mchange.v1.db.sql;
/*    */ 
/*    */ import com.mchange.v2.log.MLevel;
/*    */ import com.mchange.v2.log.MLog;
/*    */ import com.mchange.v2.log.MLogger;
/*    */ import java.sql.Connection;
/*    */ import java.sql.SQLException;
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
/*    */ public final class ConnectionUtils
/*    */ {
/* 31 */   private static final MLogger logger = MLog.getLogger(ConnectionUtils.class);
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static boolean attemptClose(Connection con)
/*    */   {
/*    */     try
/*    */     {
/* 41 */       if (con != null) { con.close();
/*    */       }
/* 43 */       return true;
/*    */ 
/*    */ 
/*    */     }
/*    */     catch (SQLException e)
/*    */     {
/*    */ 
/* 50 */       if (logger.isLoggable(MLevel.WARNING))
/* 51 */         logger.log(MLevel.WARNING, "Connection close FAILED.", e); }
/* 52 */     return false;
/*    */   }
/*    */   
/*    */ 
/*    */   public static boolean attemptRollback(Connection con)
/*    */   {
/*    */     try
/*    */     {
/* 60 */       if (con != null) con.rollback();
/* 61 */       return true;
/*    */ 
/*    */     }
/*    */     catch (SQLException e)
/*    */     {
/*    */ 
/* 67 */       if (logger.isLoggable(MLevel.WARNING))
/* 68 */         logger.log(MLevel.WARNING, "Rollback FAILED.", e); }
/* 69 */     return false;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\db\sql\ConnectionUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */