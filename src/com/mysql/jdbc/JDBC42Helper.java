/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.Date;
/*    */ import java.sql.JDBCType;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.SQLType;
/*    */ import java.sql.Time;
/*    */ import java.sql.Timestamp;
/*    */ import java.time.LocalDate;
/*    */ import java.time.LocalDateTime;
/*    */ import java.time.LocalTime;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JDBC42Helper
/*    */ {
/*    */   static Object convertJavaTimeToJavaSql(Object x)
/*    */   {
/* 49 */     if ((x instanceof LocalDate))
/* 50 */       return Date.valueOf((LocalDate)x);
/* 51 */     if ((x instanceof LocalDateTime))
/* 52 */       return Timestamp.valueOf((LocalDateTime)x);
/* 53 */     if ((x instanceof LocalTime)) {
/* 54 */       return Time.valueOf((LocalTime)x);
/*    */     }
/* 56 */     return x;
/*    */   }
/*    */   
/*    */   static boolean isSqlTypeSupported(int sqlType) {
/* 60 */     return (sqlType != 2012) && (sqlType != 2013) && (sqlType != 2014);
/*    */   }
/*    */   
/*    */   static int checkSqlType(int sqlType, ExceptionInterceptor exceptionInterceptor) throws SQLException {
/* 64 */     if (isSqlTypeSupported(sqlType)) {
/* 65 */       return sqlType;
/*    */     }
/* 67 */     throw SQLError.createSQLFeatureNotSupportedException(Messages.getString("UnsupportedSQLType.0") + JDBCType.valueOf(sqlType), "S1C00", exceptionInterceptor);
/*    */   }
/*    */   
/*    */   static int translateAndCheckSqlType(SQLType sqlType, ExceptionInterceptor exceptionInterceptor) throws SQLException
/*    */   {
/* 72 */     return checkSqlType(sqlType.getVendorTypeNumber().intValue(), exceptionInterceptor);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\JDBC42Helper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */