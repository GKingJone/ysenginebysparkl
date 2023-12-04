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
/*     */ public class JDBC42ServerPreparedStatement
/*     */   extends JDBC4ServerPreparedStatement
/*     */ {
/*     */   public JDBC42ServerPreparedStatement(MySQLConnection conn, String sql, String catalog, int resultSetType, int resultSetConcurrency)
/*     */     throws SQLException
/*     */   {
/*  31 */     super(conn, sql, catalog, resultSetType, resultSetConcurrency);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private int checkSqlType(int sqlType)
/*     */     throws SQLException
/*     */   {
/*  39 */     return JDBC42Helper.checkSqlType(sqlType, getExceptionInterceptor());
/*     */   }
/*     */   
/*     */   private int translateAndCheckSqlType(SQLType sqlType) throws SQLException {
/*  43 */     return JDBC42Helper.translateAndCheckSqlType(sqlType, getExceptionInterceptor());
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
/*  55 */     synchronized (checkClosed().getConnectionMutex()) {
/*  56 */       super.setObject(parameterIndex, JDBC42Helper.convertJavaTimeToJavaSql(x));
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
/*  70 */     synchronized (checkClosed().getConnectionMutex()) {
/*  71 */       super.setObject(parameterIndex, JDBC42Helper.convertJavaTimeToJavaSql(x), checkSqlType(targetSqlType));
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
/*  86 */     synchronized (checkClosed().getConnectionMutex()) {
/*  87 */       super.setObject(parameterIndex, JDBC42Helper.convertJavaTimeToJavaSql(x), checkSqlType(targetSqlType), scaleOrLength);
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
/* 101 */     synchronized (checkClosed().getConnectionMutex()) {
/* 102 */       super.setObject(parameterIndex, JDBC42Helper.convertJavaTimeToJavaSql(x), translateAndCheckSqlType(targetSqlType));
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
/* 117 */     synchronized (checkClosed().getConnectionMutex()) {
/* 118 */       super.setObject(parameterIndex, JDBC42Helper.convertJavaTimeToJavaSql(x), translateAndCheckSqlType(targetSqlType), scaleOrLength);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\JDBC42ServerPreparedStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */