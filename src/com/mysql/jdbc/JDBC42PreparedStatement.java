/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLType;
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
/*     */ public class JDBC42PreparedStatement
/*     */   extends JDBC4PreparedStatement
/*     */ {
/*     */   public JDBC42PreparedStatement(MySQLConnection conn, String catalog)
/*     */     throws SQLException
/*     */   {
/*  35 */     super(conn, catalog);
/*     */   }
/*     */   
/*     */   public JDBC42PreparedStatement(MySQLConnection conn, String sql, String catalog) throws SQLException {
/*  39 */     super(conn, sql, catalog);
/*     */   }
/*     */   
/*     */   public JDBC42PreparedStatement(MySQLConnection conn, String sql, String catalog, ParseInfo cachedParseInfo) throws SQLException {
/*  43 */     super(conn, sql, catalog, cachedParseInfo);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private int checkSqlType(int sqlType)
/*     */     throws SQLException
/*     */   {
/*  51 */     return JDBC42Helper.checkSqlType(sqlType, getExceptionInterceptor());
/*     */   }
/*     */   
/*     */   private int translateAndCheckSqlType(SQLType sqlType) throws SQLException {
/*  55 */     return JDBC42Helper.translateAndCheckSqlType(sqlType, getExceptionInterceptor());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setObject(int parameterIndex, Object x)
/*     */     throws SQLException
/*     */   {
/*  67 */     synchronized (checkClosed().getConnectionMutex()) {
/*  68 */       super.setObject(parameterIndex, JDBC42Helper.convertJavaTimeToJavaSql(x));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setObject(int parameterIndex, Object x, int targetSqlType)
/*     */     throws SQLException
/*     */   {
/*  82 */     synchronized (checkClosed().getConnectionMutex()) {
/*  83 */       super.setObject(parameterIndex, JDBC42Helper.convertJavaTimeToJavaSql(x), checkSqlType(targetSqlType));
/*     */     }
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
/*     */   public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength)
/*     */     throws SQLException
/*     */   {
/*  98 */     synchronized (checkClosed().getConnectionMutex()) {
/*  99 */       super.setObject(parameterIndex, JDBC42Helper.convertJavaTimeToJavaSql(x), checkSqlType(targetSqlType), scaleOrLength);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setObject(int parameterIndex, Object x, SQLType targetSqlType)
/*     */     throws SQLException
/*     */   {
/* 113 */     synchronized (checkClosed().getConnectionMutex()) {
/* 114 */       super.setObject(parameterIndex, JDBC42Helper.convertJavaTimeToJavaSql(x), translateAndCheckSqlType(targetSqlType));
/*     */     }
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
/*     */   public void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength)
/*     */     throws SQLException
/*     */   {
/* 129 */     synchronized (checkClosed().getConnectionMutex()) {
/* 130 */       super.setObject(parameterIndex, JDBC42Helper.convertJavaTimeToJavaSql(x), translateAndCheckSqlType(targetSqlType), scaleOrLength);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\JDBC42PreparedStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */