/*     */ package com.mysql.jdbc.util;
/*     */ 
/*     */ import com.mysql.jdbc.Driver;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
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
/*     */ public abstract class BaseBugReport
/*     */ {
/*     */   private Connection conn;
/*     */   private Driver driver;
/*     */   
/*     */   public BaseBugReport()
/*     */   {
/*     */     try
/*     */     {
/*  94 */       this.driver = new Driver();
/*     */     } catch (SQLException ex) {
/*  96 */       throw new RuntimeException(ex.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void setUp()
/*     */     throws Exception;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void tearDown()
/*     */     throws Exception;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract void runTest()
/*     */     throws Exception;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void run()
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 137 */       setUp();
/* 138 */       runTest();
/*     */     }
/*     */     finally {
/* 141 */       tearDown();
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
/*     */ 
/*     */   protected final void assertTrue(String message, boolean condition)
/*     */     throws Exception
/*     */   {
/* 157 */     if (!condition) {
/* 158 */       throw new Exception("Assertion failed: " + message);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final void assertTrue(boolean condition)
/*     */     throws Exception
/*     */   {
/* 171 */     assertTrue("(no message given)", condition);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getUrl()
/*     */   {
/* 182 */     return "jdbc:mysql:///test";
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
/*     */   public final synchronized Connection getConnection()
/*     */     throws SQLException
/*     */   {
/* 197 */     if ((this.conn == null) || (this.conn.isClosed())) {
/* 198 */       this.conn = getNewConnection();
/*     */     }
/*     */     
/* 201 */     return this.conn;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final synchronized Connection getNewConnection()
/*     */     throws SQLException
/*     */   {
/* 214 */     return getConnection(getUrl());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final synchronized Connection getConnection(String url)
/*     */     throws SQLException
/*     */   {
/* 227 */     return getConnection(url, null);
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
/*     */   public final synchronized Connection getConnection(String url, Properties props)
/*     */     throws SQLException
/*     */   {
/* 246 */     return this.driver.connect(url, props);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\util\BaseBugReport.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */