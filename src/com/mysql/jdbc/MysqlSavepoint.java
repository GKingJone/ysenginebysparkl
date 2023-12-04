/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.rmi.server.UID;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Savepoint;
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
/*     */ public class MysqlSavepoint
/*     */   implements Savepoint
/*     */ {
/*     */   private String savepointName;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */   
/*     */   private static String getUniqueId()
/*     */   {
/*  36 */     String uidStr = new UID().toString();
/*     */     
/*  38 */     int uidLength = uidStr.length();
/*     */     
/*  40 */     StringBuilder safeString = new StringBuilder(uidLength + 1);
/*  41 */     safeString.append('_');
/*     */     
/*  43 */     for (int i = 0; i < uidLength; i++) {
/*  44 */       char c = uidStr.charAt(i);
/*     */       
/*  46 */       if ((Character.isLetter(c)) || (Character.isDigit(c))) {
/*  47 */         safeString.append(c);
/*     */       } else {
/*  49 */         safeString.append('_');
/*     */       }
/*     */     }
/*     */     
/*  53 */     return safeString.toString();
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
/*     */   MysqlSavepoint(ExceptionInterceptor exceptionInterceptor)
/*     */     throws SQLException
/*     */   {
/*  69 */     this(getUniqueId(), exceptionInterceptor);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   MysqlSavepoint(String name, ExceptionInterceptor exceptionInterceptor)
/*     */     throws SQLException
/*     */   {
/*  82 */     if ((name == null) || (name.length() == 0)) {
/*  83 */       throw SQLError.createSQLException("Savepoint name can not be NULL or empty", "S1009", exceptionInterceptor);
/*     */     }
/*     */     
/*  86 */     this.savepointName = name;
/*     */     
/*  88 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSavepointId()
/*     */     throws SQLException
/*     */   {
/*  95 */     throw SQLError.createSQLException("Only named savepoints are supported.", "S1C00", this.exceptionInterceptor);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getSavepointName()
/*     */     throws SQLException
/*     */   {
/* 102 */     return this.savepointName;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\MysqlSavepoint.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */