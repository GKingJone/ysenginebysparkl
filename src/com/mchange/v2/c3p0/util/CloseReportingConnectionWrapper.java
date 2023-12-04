/*    */ package com.mchange.v2.c3p0.util;
/*    */ 
/*    */ import com.mchange.v2.sql.filter.FilterConnection;
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
/*    */ public class CloseReportingConnectionWrapper
/*    */   extends FilterConnection
/*    */ {
/*    */   public CloseReportingConnectionWrapper(Connection conn)
/*    */   {
/* 32 */     super(conn);
/*    */   }
/*    */   
/*    */   public void close() throws SQLException
/*    */   {
/* 37 */     new SQLWarning("Connection.close() called!").printStackTrace();
/* 38 */     super.close();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\util\CloseReportingConnectionWrapper.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */