/*    */ package com.mchange.v2.c3p0.filter;
/*    */ 
/*    */ import java.io.PrintWriter;
/*    */ import java.sql.Connection;
/*    */ import java.sql.SQLException;
/*    */ import javax.sql.DataSource;
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
/*    */ public abstract class FilterDataSource
/*    */   implements DataSource
/*    */ {
/*    */   protected DataSource inner;
/*    */   
/*    */   public FilterDataSource(DataSource inner)
/*    */   {
/* 39 */     this.inner = inner;
/*    */   }
/*    */   
/*    */   public Connection getConnection() throws SQLException
/*    */   {
/* 44 */     return this.inner.getConnection();
/*    */   }
/*    */   
/*    */   public Connection getConnection(String a, String b) throws SQLException
/*    */   {
/* 49 */     return this.inner.getConnection(a, b);
/*    */   }
/*    */   
/*    */   public PrintWriter getLogWriter() throws SQLException
/*    */   {
/* 54 */     return this.inner.getLogWriter();
/*    */   }
/*    */   
/*    */   public int getLoginTimeout() throws SQLException
/*    */   {
/* 59 */     return this.inner.getLoginTimeout();
/*    */   }
/*    */   
/*    */   public void setLogWriter(PrintWriter a) throws SQLException
/*    */   {
/* 64 */     this.inner.setLogWriter(a);
/*    */   }
/*    */   
/*    */   public void setLoginTimeout(int a) throws SQLException
/*    */   {
/* 69 */     this.inner.setLoginTimeout(a);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\filter\FilterDataSource.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */