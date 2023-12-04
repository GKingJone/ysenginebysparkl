/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.NClob;
/*    */ import java.sql.RowId;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.SQLXML;
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
/*    */ 
/*    */ public class JDBC4PreparedStatement
/*    */   extends PreparedStatement
/*    */ {
/*    */   public JDBC4PreparedStatement(MySQLConnection conn, String catalog)
/*    */     throws SQLException
/*    */   {
/* 41 */     super(conn, catalog);
/*    */   }
/*    */   
/*    */   public JDBC4PreparedStatement(MySQLConnection conn, String sql, String catalog) throws SQLException {
/* 45 */     super(conn, sql, catalog);
/*    */   }
/*    */   
/*    */   public JDBC4PreparedStatement(MySQLConnection conn, String sql, String catalog, ParseInfo cachedParseInfo) throws SQLException {
/* 49 */     super(conn, sql, catalog, cachedParseInfo);
/*    */   }
/*    */   
/*    */   public void setRowId(int parameterIndex, RowId x) throws SQLException {
/* 53 */     JDBC4PreparedStatementHelper.setRowId(this, parameterIndex, x);
/*    */   }
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
/*    */   public void setNClob(int parameterIndex, NClob value)
/*    */     throws SQLException
/*    */   {
/* 68 */     JDBC4PreparedStatementHelper.setNClob(this, parameterIndex, value);
/*    */   }
/*    */   
/*    */   public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
/* 72 */     JDBC4PreparedStatementHelper.setSQLXML(this, parameterIndex, xmlObject);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\JDBC4PreparedStatement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */