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
/*     */ 
/*     */ public class JDBC42ResultSet
/*     */   extends JDBC4ResultSet
/*     */ {
/*     */   public JDBC42ResultSet(long updateCount, long updateID, MySQLConnection conn, StatementImpl creatorStmt)
/*     */   {
/*  44 */     super(updateCount, updateID, conn, creatorStmt);
/*     */   }
/*     */   
/*     */   public JDBC42ResultSet(String catalog, Field[] fields, RowData tuples, MySQLConnection conn, StatementImpl creatorStmt) throws SQLException {
/*  48 */     super(catalog, fields, tuples, conn, creatorStmt);
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
/*  60 */     if (type == null) {
/*  61 */       throw SQLError.createSQLException("Type parameter can not be null", "S1009", getExceptionInterceptor());
/*     */     }
/*     */     
/*  64 */     if (type.equals(LocalDate.class))
/*  65 */       return (T)type.cast(getDate(columnIndex).toLocalDate());
/*  66 */     if (type.equals(LocalDateTime.class))
/*  67 */       return (T)type.cast(getTimestamp(columnIndex).toLocalDateTime());
/*  68 */     if (type.equals(LocalTime.class))
/*  69 */       return (T)type.cast(getTime(columnIndex).toLocalTime());
/*  70 */     if (type.equals(OffsetDateTime.class)) {
/*     */       try {
/*  72 */         return (T)type.cast(OffsetDateTime.parse(getString(columnIndex)));
/*     */ 
/*     */       }
/*     */       catch (DateTimeParseException localDateTimeParseException) {}
/*  76 */     } else if (type.equals(OffsetTime.class)) {
/*     */       try {
/*  78 */         return (T)type.cast(OffsetTime.parse(getString(columnIndex)));
/*     */       }
/*     */       catch (DateTimeParseException localDateTimeParseException1) {}
/*     */     }
/*     */     
/*     */ 
/*  84 */     return (T)super.getObject(columnIndex, type);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void updateObject(int columnIndex, Object x, SQLType targetSqlType)
/*     */     throws SQLException
/*     */   {
/*  96 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength)
/*     */     throws SQLException
/*     */   {
/* 109 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void updateObject(String columnLabel, Object x, SQLType targetSqlType)
/*     */     throws SQLException
/*     */   {
/* 121 */     throw new NotUpdatable();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength)
/*     */     throws SQLException
/*     */   {
/* 134 */     throw new NotUpdatable();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\JDBC42ResultSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */