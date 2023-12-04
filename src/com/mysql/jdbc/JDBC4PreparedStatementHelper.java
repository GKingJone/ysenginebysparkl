/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.io.Reader;
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
/*    */ public class JDBC4PreparedStatementHelper
/*    */ {
/*    */   static void setRowId(PreparedStatement pstmt, int parameterIndex, RowId x)
/*    */     throws SQLException
/*    */   {
/* 41 */     throw SQLError.createSQLFeatureNotSupportedException();
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
/*    */   static void setNClob(PreparedStatement pstmt, int parameterIndex, NClob value)
/*    */     throws SQLException
/*    */   {
/* 56 */     if (value == null) {
/* 57 */       pstmt.setNull(parameterIndex, 2011);
/*    */     } else {
/* 59 */       pstmt.setNCharacterStream(parameterIndex, value.getCharacterStream(), value.length());
/*    */     }
/*    */   }
/*    */   
/*    */   static void setNClob(PreparedStatement pstmt, int parameterIndex, Reader reader) throws SQLException {
/* 64 */     pstmt.setNCharacterStream(parameterIndex, reader);
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
/*    */ 
/*    */ 
/*    */   static void setNClob(PreparedStatement pstmt, int parameterIndex, Reader reader, long length)
/*    */     throws SQLException
/*    */   {
/* 81 */     if (reader == null) {
/* 82 */       pstmt.setNull(parameterIndex, 2011);
/*    */     } else {
/* 84 */       pstmt.setNCharacterStream(parameterIndex, reader, length);
/*    */     }
/*    */   }
/*    */   
/*    */   static void setSQLXML(PreparedStatement pstmt, int parameterIndex, SQLXML xmlObject) throws SQLException {
/* 89 */     if (xmlObject == null) {
/* 90 */       pstmt.setNull(parameterIndex, 2009);
/*    */     }
/*    */     else {
/* 93 */       pstmt.setCharacterStream(parameterIndex, ((JDBC4MysqlSQLXML)xmlObject).serializeAsCharacterStream());
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\JDBC4PreparedStatementHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */