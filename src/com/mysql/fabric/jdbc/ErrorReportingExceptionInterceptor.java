/*    */ package com.mysql.fabric.jdbc;
/*    */ 
/*    */ import com.mysql.fabric.FabricCommunicationException;
/*    */ import com.mysql.jdbc.Connection;
/*    */ import com.mysql.jdbc.ConnectionImpl;
/*    */ import com.mysql.jdbc.ExceptionInterceptor;
/*    */ import com.mysql.jdbc.MySQLConnection;
/*    */ import com.mysql.jdbc.SQLError;
/*    */ import java.sql.SQLException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ErrorReportingExceptionInterceptor
/*    */   implements ExceptionInterceptor
/*    */ {
/*    */   private String hostname;
/*    */   private String port;
/*    */   private String fabricHaGroup;
/*    */   
/*    */   public SQLException interceptException(SQLException sqlEx, Connection conn)
/*    */   {
/* 47 */     MySQLConnection mysqlConn = (MySQLConnection)conn;
/*    */     
/*    */ 
/* 50 */     if (ConnectionImpl.class.isAssignableFrom(mysqlConn.getMultiHostSafeProxy().getClass())) {
/* 51 */       return null;
/*    */     }
/*    */     
/* 54 */     FabricMySQLConnectionProxy fabricProxy = (FabricMySQLConnectionProxy)mysqlConn.getMultiHostSafeProxy();
/*    */     try {
/* 56 */       return fabricProxy.interceptException(sqlEx, conn, this.fabricHaGroup, this.hostname, this.port);
/*    */     } catch (FabricCommunicationException ex) {
/* 58 */       return SQLError.createSQLException("Failed to report error to Fabric.", "08S01", ex, null);
/*    */     }
/*    */   }
/*    */   
/*    */   public void init(Connection conn, Properties props) throws SQLException {
/* 63 */     this.hostname = props.getProperty("HOST");
/* 64 */     this.port = props.getProperty("PORT");
/* 65 */     String connectionAttributes = props.getProperty("connectionAttributes");
/* 66 */     this.fabricHaGroup = connectionAttributes.replaceAll("^.*\\bfabricHaGroup:(.+)\\b.*$", "$1");
/*    */   }
/*    */   
/*    */   public void destroy() {}
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\jdbc\ErrorReportingExceptionInterceptor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */