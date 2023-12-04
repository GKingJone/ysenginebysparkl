/*     */ package com.facebook.presto.jdbc;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Strings;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Throwables;
/*     */ import java.io.Closeable;
/*     */ import java.sql.Connection;
/*     */ import java.sql.Driver;
/*     */ import java.sql.DriverManager;
/*     */ import java.sql.DriverPropertyInfo;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLFeatureNotSupportedException;
/*     */ import java.util.Properties;
/*     */ import java.util.logging.Logger;
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
/*     */ public class PrestoDriver
/*     */   implements Driver, Closeable
/*     */ {
/*     */   static final int VERSION_MAJOR = 1;
/*     */   static final int VERSION_MINOR = 0;
/*     */   static final int JDBC_VERSION_MAJOR = 4;
/*     */   static final int JDBC_VERSION_MINOR = 1;
/*     */   static final String DRIVER_NAME = "Presto JDBC Driver";
/*     */   static final String DRIVER_VERSION = "1.0";
/*  43 */   private static final DriverPropertyInfo[] DRIVER_PROPERTY_INFOS = new DriverPropertyInfo[0];
/*     */   
/*     */   private static final String DRIVER_URL_START = "jdbc:presto:";
/*     */   private static final String USER_PROPERTY = "user";
/*     */   private final QueryExecutor queryExecutor;
/*     */   
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  53 */       DriverManager.registerDriver(new PrestoDriver());
/*     */     }
/*     */     catch (SQLException e) {
/*  56 */       throw Throwables.propagate(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public PrestoDriver()
/*     */   {
/*  62 */     this.queryExecutor = QueryExecutor.create("Presto JDBC Driver/1.0");
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */   {
/*  68 */     this.queryExecutor.close();
/*     */   }
/*     */   
/*     */ 
/*     */   public Connection connect(String url, Properties info)
/*     */     throws SQLException
/*     */   {
/*  75 */     if (!acceptsURL(url)) {
/*  76 */       return null;
/*     */     }
/*     */     
/*  79 */     String user = info.getProperty("user");
/*  80 */     if (Strings.isNullOrEmpty(user)) {
/*  81 */       throw new SQLException(String.format("Username property (%s) must be set", new Object[] { "user" }));
/*     */     }
/*     */     
/*  84 */     return new PrestoConnection(new PrestoDriverUri(url), user, this.queryExecutor);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean acceptsURL(String url)
/*     */     throws SQLException
/*     */   {
/*  91 */     return url.startsWith("jdbc:presto:");
/*     */   }
/*     */   
/*     */ 
/*     */   public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
/*     */     throws SQLException
/*     */   {
/*  98 */     return DRIVER_PROPERTY_INFOS;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMajorVersion()
/*     */   {
/* 104 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMinorVersion()
/*     */   {
/* 110 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean jdbcCompliant()
/*     */   {
/* 117 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Logger getParentLogger()
/*     */     throws SQLFeatureNotSupportedException
/*     */   {
/* 125 */     throw new SQLFeatureNotSupportedException();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\PrestoDriver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */