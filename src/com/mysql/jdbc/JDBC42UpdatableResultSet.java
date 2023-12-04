/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.Date;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLType;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.time.LocalDate;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.LocalTime;
/*     */ import java.time.OffsetDateTime;
/*     */ import java.time.OffsetTime;
/*     */ import java.time.format.DateTimeParseException;
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
/*     */ public class JDBC42UpdatableResultSet
/*     */   extends JDBC4UpdatableResultSet
/*     */ {
/*     */   public JDBC42UpdatableResultSet(String catalog, Field[] fields, RowData tuples, MySQLConnection conn, StatementImpl creatorStmt)
/*     */     throws SQLException
/*     */   {
/*  44 */     super(catalog, fields, tuples, conn, creatorStmt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private int translateAndCheckSqlType(SQLType sqlType)
/*     */     throws SQLException
/*     */   {
/*  52 */     return JDBC42Helper.translateAndCheckSqlType(sqlType, getExceptionInterceptor());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public <T> T getObject(int columnIndex, Class<T> type)
/*     */     throws SQLException
/*     */   {
/*  64 */     if (type == null) {
/*  65 */       throw SQLError.createSQLException("Type parameter can not be null", "S1009", getExceptionInterceptor());
/*     */     }
/*     */     
/*  68 */     if (type.equals(LocalDate.class))
/*  69 */       return (T)type.cast(getDate(columnIndex).toLocalDate());
/*  70 */     if (type.equals(LocalDateTime.class))
/*  71 */       return (T)type.cast(getTimestamp(columnIndex).toLocalDateTime());
/*  72 */     if (type.equals(LocalTime.class))
/*  73 */       return (T)type.cast(getTime(columnIndex).toLocalTime());
/*  74 */     if (type.equals(OffsetDateTime.class)) {
/*     */       try {
/*  76 */         return (T)type.cast(OffsetDateTime.parse(getString(columnIndex)));
/*     */ 
/*     */       }
/*     */       catch (DateTimeParseException localDateTimeParseException) {}
/*  80 */     } else if (type.equals(OffsetTime.class)) {
/*     */       try {
/*  82 */         return (T)type.cast(OffsetTime.parse(getString(columnIndex)));
/*     */       }
/*     */       catch (DateTimeParseException localDateTimeParseException1) {}
/*     */     }
/*     */     
/*     */ 
/*  88 */     return (T)super.getObject(columnIndex, type);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void updateObject(int columnIndex, Object x)
/*     */     throws SQLException
/*     */   {
/* 100 */     super.updateObject(columnIndex, JDBC42Helper.convertJavaTimeToJavaSql(x));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void updateObject(int columnIndex, Object x, int scaleOrLength)
/*     */     throws SQLException
/*     */   {
/* 113 */     super.updateObject(columnIndex, JDBC42Helper.convertJavaTimeToJavaSql(x), scaleOrLength);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void updateObject(String columnLabel, Object x)
/*     */     throws SQLException
/*     */   {
/* 125 */     super.updateObject(columnLabel, JDBC42Helper.convertJavaTimeToJavaSql(x));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void updateObject(String columnLabel, Object x, int scaleOrLength)
/*     */     throws SQLException
/*     */   {
/* 138 */     super.updateObject(columnLabel, JDBC42Helper.convertJavaTimeToJavaSql(x), scaleOrLength);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void updateObject(int columnIndex, Object x, SQLType targetSqlType)
/*     */     throws SQLException
/*     */   {
/* 151 */     super.updateObjectInternal(columnIndex, JDBC42Helper.convertJavaTimeToJavaSql(x), Integer.valueOf(translateAndCheckSqlType(targetSqlType)), 0);
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
/*     */   public synchronized void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength)
/*     */     throws SQLException
/*     */   {
/* 165 */     super.updateObjectInternal(columnIndex, JDBC42Helper.convertJavaTimeToJavaSql(x), Integer.valueOf(translateAndCheckSqlType(targetSqlType)), scaleOrLength);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void updateObject(String columnLabel, Object x, SQLType targetSqlType)
/*     */     throws SQLException
/*     */   {
/* 178 */     super.updateObjectInternal(findColumn(columnLabel), JDBC42Helper.convertJavaTimeToJavaSql(x), Integer.valueOf(translateAndCheckSqlType(targetSqlType)), 0);
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
/*     */   public synchronized void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength)
/*     */     throws SQLException
/*     */   {
/* 192 */     super.updateObjectInternal(findColumn(columnLabel), JDBC42Helper.convertJavaTimeToJavaSql(x), Integer.valueOf(translateAndCheckSqlType(targetSqlType)), scaleOrLength);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\JDBC42UpdatableResultSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */