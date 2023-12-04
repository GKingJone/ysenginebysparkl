/*    */ package com.mchange.v1.db.sql;
/*    */ 
/*    */ import com.mchange.v2.log.MLevel;
/*    */ import com.mchange.v2.log.MLog;
/*    */ import com.mchange.v2.log.MLogger;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.Statement;
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
/*    */ public final class StatementUtils
/*    */ {
/* 31 */   private static final MLogger logger = MLog.getLogger(StatementUtils.class);
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static boolean attemptClose(Statement stmt)
/*    */   {
/*    */     try
/*    */     {
/* 41 */       if (stmt != null) stmt.close();
/* 42 */       return true;
/*    */ 
/*    */     }
/*    */     catch (SQLException e)
/*    */     {
/* 47 */       if (logger.isLoggable(MLevel.WARNING))
/* 48 */         logger.log(MLevel.WARNING, "Statement close FAILED.", e); }
/* 49 */     return false;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\db\sql\StatementUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */