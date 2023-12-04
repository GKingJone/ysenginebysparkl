/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.Reader;
/*     */ import java.sql.NClob;
/*     */ import java.sql.RowId;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLXML;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JDBC4ServerPreparedStatement
/*     */   extends ServerPreparedStatement
/*     */ {
/*     */   public JDBC4ServerPreparedStatement(MySQLConnection conn, String sql, String catalog, int resultSetType, int resultSetConcurrency)
/*     */     throws SQLException
/*     */   {
/*  41 */     super(conn, sql, catalog, resultSetType, resultSetConcurrency);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setNCharacterStream(int parameterIndex, Reader reader, long length)
/*     */     throws SQLException
/*     */   {
/*  49 */     if ((!this.charEncoding.equalsIgnoreCase("UTF-8")) && (!this.charEncoding.equalsIgnoreCase("utf8"))) {
/*  50 */       throw SQLError.createSQLException("Can not call setNCharacterStream() when connection character set isn't UTF-8", getExceptionInterceptor());
/*     */     }
/*     */     
/*  53 */     checkClosed();
/*     */     
/*  55 */     if (reader == null) {
/*  56 */       setNull(parameterIndex, -2);
/*     */     } else {
/*  58 */       BindValue binding = getBinding(parameterIndex, true);
/*  59 */       resetToType(binding, 252);
/*     */       
/*  61 */       binding.value = reader;
/*  62 */       binding.isLongData = true;
/*     */       
/*  64 */       if (this.connection.getUseStreamLengthsInPrepStmts()) {
/*  65 */         binding.bindLength = length;
/*     */       } else {
/*  67 */         binding.bindLength = -1L;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void setNClob(int parameterIndex, NClob x)
/*     */     throws SQLException
/*     */   {
/*  76 */     setNClob(parameterIndex, x.getCharacterStream(), this.connection.getUseStreamLengthsInPrepStmts() ? x.length() : -1L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setNClob(int parameterIndex, Reader reader, long length)
/*     */     throws SQLException
/*     */   {
/*  94 */     if ((!this.charEncoding.equalsIgnoreCase("UTF-8")) && (!this.charEncoding.equalsIgnoreCase("utf8"))) {
/*  95 */       throw SQLError.createSQLException("Can not call setNClob() when connection character set isn't UTF-8", getExceptionInterceptor());
/*     */     }
/*     */     
/*  98 */     checkClosed();
/*     */     
/* 100 */     if (reader == null) {
/* 101 */       setNull(parameterIndex, 2011);
/*     */     } else {
/* 103 */       BindValue binding = getBinding(parameterIndex, true);
/* 104 */       resetToType(binding, 252);
/*     */       
/* 106 */       binding.value = reader;
/* 107 */       binding.isLongData = true;
/*     */       
/* 109 */       if (this.connection.getUseStreamLengthsInPrepStmts()) {
/* 110 */         binding.bindLength = length;
/*     */       } else {
/* 112 */         binding.bindLength = -1L;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void setNString(int parameterIndex, String x)
/*     */     throws SQLException
/*     */   {
/* 121 */     if ((this.charEncoding.equalsIgnoreCase("UTF-8")) || (this.charEncoding.equalsIgnoreCase("utf8"))) {
/* 122 */       setString(parameterIndex, x);
/*     */     } else {
/* 124 */       throw SQLError.createSQLException("Can not call setNString() when connection character set isn't UTF-8", getExceptionInterceptor());
/*     */     }
/*     */   }
/*     */   
/*     */   public void setRowId(int parameterIndex, RowId x) throws SQLException {
/* 129 */     JDBC4PreparedStatementHelper.setRowId(this, parameterIndex, x);
/*     */   }
/*     */   
/*     */   public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
/* 133 */     JDBC4PreparedStatementHelper.setSQLXML(this, parameterIndex, xmlObject);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\JDBC4ServerPreparedStatement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */