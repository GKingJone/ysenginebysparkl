/*    */ package com.mysql.jdbc.interceptors;
/*    */ 
/*    */ import com.mysql.jdbc.Connection;
/*    */ import com.mysql.jdbc.ResultSetInternalMethods;
/*    */ import com.mysql.jdbc.StatementInterceptor;
/*    */ import com.mysql.jdbc.Util;
/*    */ import com.mysql.jdbc.log.Log;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Properties;
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
/*    */ public class ServerStatusDiffInterceptor
/*    */   implements StatementInterceptor
/*    */ {
/* 39 */   private Map<String, String> preExecuteValues = new HashMap();
/*    */   
/* 41 */   private Map<String, String> postExecuteValues = new HashMap();
/*    */   
/*    */   public void init(Connection conn, Properties props)
/*    */     throws SQLException
/*    */   {}
/*    */   
/*    */   public ResultSetInternalMethods postProcess(String sql, com.mysql.jdbc.Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection)
/*    */     throws SQLException
/*    */   {
/* 50 */     if (connection.versionMeetsMinimum(5, 0, 2)) {
/* 51 */       populateMapWithSessionStatusValues(connection, this.postExecuteValues);
/*    */       
/* 53 */       connection.getLog().logInfo("Server status change for statement:\n" + Util.calculateDifferences(this.preExecuteValues, this.postExecuteValues));
/*    */     }
/*    */     
/* 56 */     return null;
/*    */   }
/*    */   
/*    */   private void populateMapWithSessionStatusValues(Connection connection, Map<String, String> toPopulate) throws SQLException
/*    */   {
/* 61 */     java.sql.Statement stmt = null;
/* 62 */     ResultSet rs = null;
/*    */     try
/*    */     {
/* 65 */       toPopulate.clear();
/*    */       
/* 67 */       stmt = connection.createStatement();
/* 68 */       rs = stmt.executeQuery("SHOW SESSION STATUS");
/* 69 */       Util.resultSetToMap(toPopulate, rs);
/*    */     } finally {
/* 71 */       if (rs != null) {
/* 72 */         rs.close();
/*    */       }
/*    */       
/* 75 */       if (stmt != null) {
/* 76 */         stmt.close();
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */   public ResultSetInternalMethods preProcess(String sql, com.mysql.jdbc.Statement interceptedStatement, Connection connection) throws SQLException
/*    */   {
/* 83 */     if (connection.versionMeetsMinimum(5, 0, 2)) {
/* 84 */       populateMapWithSessionStatusValues(connection, this.preExecuteValues);
/*    */     }
/*    */     
/* 87 */     return null;
/*    */   }
/*    */   
/*    */   public boolean executeTopLevelOnly() {
/* 91 */     return true;
/*    */   }
/*    */   
/*    */   public void destroy() {}
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\interceptors\ServerStatusDiffInterceptor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */