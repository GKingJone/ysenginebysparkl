/*     */ package com.mchange.v2.c3p0;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
/*     */ import javax.sql.DataSource;
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
/*     */ /**
/*     */  * @deprecated
/*     */  */
/*     */ public final class DriverManagerDataSourceFactory
/*     */ {
/*     */   public static DataSource create(String driverClass, String jdbcUrl, String dfltUser, String dfltPassword, String refFactoryLoc)
/*     */     throws SQLException
/*     */   {
/*  61 */     DriverManagerDataSource out = new DriverManagerDataSource();
/*  62 */     out.setDriverClass(driverClass);
/*  63 */     out.setJdbcUrl(jdbcUrl);
/*  64 */     out.setUser(dfltUser);
/*  65 */     out.setPassword(dfltPassword);
/*  66 */     out.setFactoryClassLocation(refFactoryLoc);
/*  67 */     return out;
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
/*     */ 
/*     */ 
/*     */   public static DataSource create(String driverClass, String jdbcUrl, Properties props, String refFactoryLoc)
/*     */     throws SQLException
/*     */   {
/*  87 */     DriverManagerDataSource out = new DriverManagerDataSource();
/*  88 */     out.setDriverClass(driverClass);
/*  89 */     out.setJdbcUrl(jdbcUrl);
/*  90 */     out.setProperties(props);
/*  91 */     out.setFactoryClassLocation(refFactoryLoc);
/*  92 */     return out;
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
/*     */   public static DataSource create(String driverClass, String jdbcUrl, String dfltUser, String dfltPassword)
/*     */     throws SQLException
/*     */   {
/* 109 */     return create(driverClass, jdbcUrl, dfltUser, dfltPassword, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DataSource create(String driverClass, String jdbcUrl)
/*     */     throws SQLException
/*     */   {
/* 120 */     return create(driverClass, jdbcUrl, (String)null, null);
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
/*     */   public static DataSource create(String jdbcUrl, String dfltUser, String dfltPassword)
/*     */     throws SQLException
/*     */   {
/* 136 */     return create(null, jdbcUrl, dfltUser, dfltPassword);
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
/*     */   public static DataSource create(String jdbcUrl)
/*     */     throws SQLException
/*     */   {
/* 150 */     return create(null, jdbcUrl, (String)null, null);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\DriverManagerDataSourceFactory.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */