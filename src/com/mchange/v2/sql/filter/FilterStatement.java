/*     */ package com.mchange.v2.sql.filter;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLWarning;
/*     */ import java.sql.Statement;
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
/*     */ public abstract class FilterStatement
/*     */   implements Statement
/*     */ {
/*     */   protected Statement inner;
/*     */   
/*     */   public FilterStatement(Statement inner)
/*     */   {
/*  38 */     this.inner = inner;
/*     */   }
/*     */   
/*     */   public FilterStatement() {}
/*     */   
/*     */   public void setInner(Statement inner) {
/*  44 */     this.inner = inner;
/*     */   }
/*     */   
/*  47 */   public Statement getInner() { return this.inner; }
/*     */   
/*     */   public SQLWarning getWarnings() throws SQLException {
/*  50 */     return this.inner.getWarnings();
/*     */   }
/*     */   
/*  53 */   public void clearWarnings() throws SQLException { this.inner.clearWarnings(); }
/*     */   
/*     */   public void setFetchDirection(int a) throws SQLException {
/*  56 */     this.inner.setFetchDirection(a);
/*     */   }
/*     */   
/*  59 */   public int getFetchDirection() throws SQLException { return this.inner.getFetchDirection(); }
/*     */   
/*     */   public void setFetchSize(int a) throws SQLException {
/*  62 */     this.inner.setFetchSize(a);
/*     */   }
/*     */   
/*  65 */   public int getFetchSize() throws SQLException { return this.inner.getFetchSize(); }
/*     */   
/*     */   public int getResultSetHoldability() throws SQLException {
/*  68 */     return this.inner.getResultSetHoldability();
/*     */   }
/*     */   
/*  71 */   public ResultSet executeQuery(String a) throws SQLException { return this.inner.executeQuery(a); }
/*     */   
/*     */   public int executeUpdate(String a, int b) throws SQLException {
/*  74 */     return this.inner.executeUpdate(a, b);
/*     */   }
/*     */   
/*  77 */   public int executeUpdate(String a, String[] b) throws SQLException { return this.inner.executeUpdate(a, b); }
/*     */   
/*     */   public int executeUpdate(String a, int[] b) throws SQLException {
/*  80 */     return this.inner.executeUpdate(a, b);
/*     */   }
/*     */   
/*  83 */   public int executeUpdate(String a) throws SQLException { return this.inner.executeUpdate(a); }
/*     */   
/*     */   public int getMaxFieldSize() throws SQLException {
/*  86 */     return this.inner.getMaxFieldSize();
/*     */   }
/*     */   
/*  89 */   public void setMaxFieldSize(int a) throws SQLException { this.inner.setMaxFieldSize(a); }
/*     */   
/*     */   public int getMaxRows() throws SQLException {
/*  92 */     return this.inner.getMaxRows();
/*     */   }
/*     */   
/*  95 */   public void setMaxRows(int a) throws SQLException { this.inner.setMaxRows(a); }
/*     */   
/*     */   public void setEscapeProcessing(boolean a) throws SQLException {
/*  98 */     this.inner.setEscapeProcessing(a);
/*     */   }
/*     */   
/* 101 */   public int getQueryTimeout() throws SQLException { return this.inner.getQueryTimeout(); }
/*     */   
/*     */   public void setQueryTimeout(int a) throws SQLException {
/* 104 */     this.inner.setQueryTimeout(a);
/*     */   }
/*     */   
/* 107 */   public void setCursorName(String a) throws SQLException { this.inner.setCursorName(a); }
/*     */   
/*     */   public ResultSet getResultSet() throws SQLException {
/* 110 */     return this.inner.getResultSet();
/*     */   }
/*     */   
/* 113 */   public int getUpdateCount() throws SQLException { return this.inner.getUpdateCount(); }
/*     */   
/*     */   public boolean getMoreResults() throws SQLException {
/* 116 */     return this.inner.getMoreResults();
/*     */   }
/*     */   
/* 119 */   public boolean getMoreResults(int a) throws SQLException { return this.inner.getMoreResults(a); }
/*     */   
/*     */   public int getResultSetConcurrency() throws SQLException {
/* 122 */     return this.inner.getResultSetConcurrency();
/*     */   }
/*     */   
/* 125 */   public int getResultSetType() throws SQLException { return this.inner.getResultSetType(); }
/*     */   
/*     */   public void addBatch(String a) throws SQLException {
/* 128 */     this.inner.addBatch(a);
/*     */   }
/*     */   
/* 131 */   public void clearBatch() throws SQLException { this.inner.clearBatch(); }
/*     */   
/*     */   public int[] executeBatch() throws SQLException {
/* 134 */     return this.inner.executeBatch();
/*     */   }
/*     */   
/* 137 */   public ResultSet getGeneratedKeys() throws SQLException { return this.inner.getGeneratedKeys(); }
/*     */   
/*     */   public void close() throws SQLException {
/* 140 */     this.inner.close();
/*     */   }
/*     */   
/* 143 */   public boolean execute(String a, int b) throws SQLException { return this.inner.execute(a, b); }
/*     */   
/*     */   public boolean execute(String a) throws SQLException {
/* 146 */     return this.inner.execute(a);
/*     */   }
/*     */   
/* 149 */   public boolean execute(String a, int[] b) throws SQLException { return this.inner.execute(a, b); }
/*     */   
/*     */   public boolean execute(String a, String[] b) throws SQLException {
/* 152 */     return this.inner.execute(a, b);
/*     */   }
/*     */   
/* 155 */   public Connection getConnection() throws SQLException { return this.inner.getConnection(); }
/*     */   
/*     */   public void cancel() throws SQLException {
/* 158 */     this.inner.cancel();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\sql\filter\FilterStatement.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */