/*    */ package com.mchange.v2.c3p0;
/*    */ 
/*    */ import com.mchange.v2.log.MLevel;
/*    */ import com.mchange.v2.log.MLog;
/*    */ import com.mchange.v2.log.MLogger;
/*    */ import java.sql.Connection;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.SQLWarning;
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
/*    */ public final class SQLWarnings
/*    */ {
/* 36 */   static final MLogger logger = MLog.getLogger(SQLWarnings.class);
/*    */   
/*    */   public static void logAndClearWarnings(Connection con) throws SQLException
/*    */   {
/* 40 */     if (logger.isLoggable(MLevel.INFO))
/*    */     {
/* 42 */       for (SQLWarning w = con.getWarnings(); w != null; w = w.getNextWarning())
/* 43 */         logger.log(MLevel.INFO, w.getMessage(), w);
/*    */     }
/* 45 */     con.clearWarnings();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\SQLWarnings.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */