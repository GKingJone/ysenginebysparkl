/*     */ package com.mchange.v2.sql.filter;
/*     */ 
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLWarning;
/*     */ import java.sql.Savepoint;
/*     */ import java.sql.Statement;
/*     */ import java.util.Map;
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
/*     */ public abstract class FilterConnection
/*     */   implements Connection
/*     */ {
/*     */   protected Connection inner;
/*     */   
/*     */   public FilterConnection(Connection inner)
/*     */   {
/*  42 */     this.inner = inner;
/*     */   }
/*     */   
/*     */   public FilterConnection() {}
/*     */   
/*     */   public void setInner(Connection inner) {
/*  48 */     this.inner = inner;
/*     */   }
/*     */   
/*  51 */   public Connection getInner() { return this.inner; }
/*     */   
/*     */   public Statement createStatement(int a, int b, int c) throws SQLException {
/*  54 */     return this.inner.createStatement(a, b, c);
/*     */   }
/*     */   
/*  57 */   public Statement createStatement(int a, int b) throws SQLException { return this.inner.createStatement(a, b); }
/*     */   
/*     */   public Statement createStatement() throws SQLException {
/*  60 */     return this.inner.createStatement();
/*     */   }
/*     */   
/*  63 */   public PreparedStatement prepareStatement(String a, String[] b) throws SQLException { return this.inner.prepareStatement(a, b); }
/*     */   
/*     */   public PreparedStatement prepareStatement(String a) throws SQLException {
/*  66 */     return this.inner.prepareStatement(a);
/*     */   }
/*     */   
/*  69 */   public PreparedStatement prepareStatement(String a, int b, int c) throws SQLException { return this.inner.prepareStatement(a, b, c); }
/*     */   
/*     */   public PreparedStatement prepareStatement(String a, int b, int c, int d) throws SQLException {
/*  72 */     return this.inner.prepareStatement(a, b, c, d);
/*     */   }
/*     */   
/*  75 */   public PreparedStatement prepareStatement(String a, int b) throws SQLException { return this.inner.prepareStatement(a, b); }
/*     */   
/*     */   public PreparedStatement prepareStatement(String a, int[] b) throws SQLException {
/*  78 */     return this.inner.prepareStatement(a, b);
/*     */   }
/*     */   
/*  81 */   public CallableStatement prepareCall(String a, int b, int c, int d) throws SQLException { return this.inner.prepareCall(a, b, c, d); }
/*     */   
/*     */   public CallableStatement prepareCall(String a, int b, int c) throws SQLException {
/*  84 */     return this.inner.prepareCall(a, b, c);
/*     */   }
/*     */   
/*  87 */   public CallableStatement prepareCall(String a) throws SQLException { return this.inner.prepareCall(a); }
/*     */   
/*     */   public String nativeSQL(String a) throws SQLException {
/*  90 */     return this.inner.nativeSQL(a);
/*     */   }
/*     */   
/*  93 */   public void setAutoCommit(boolean a) throws SQLException { this.inner.setAutoCommit(a); }
/*     */   
/*     */   public boolean getAutoCommit() throws SQLException {
/*  96 */     return this.inner.getAutoCommit();
/*     */   }
/*     */   
/*  99 */   public void commit() throws SQLException { this.inner.commit(); }
/*     */   
/*     */   public void rollback(Savepoint a) throws SQLException {
/* 102 */     this.inner.rollback(a);
/*     */   }
/*     */   
/* 105 */   public void rollback() throws SQLException { this.inner.rollback(); }
/*     */   
/*     */   public DatabaseMetaData getMetaData() throws SQLException {
/* 108 */     return this.inner.getMetaData();
/*     */   }
/*     */   
/* 111 */   public void setCatalog(String a) throws SQLException { this.inner.setCatalog(a); }
/*     */   
/*     */   public String getCatalog() throws SQLException {
/* 114 */     return this.inner.getCatalog();
/*     */   }
/*     */   
/* 117 */   public void setTransactionIsolation(int a) throws SQLException { this.inner.setTransactionIsolation(a); }
/*     */   
/*     */   public int getTransactionIsolation() throws SQLException {
/* 120 */     return this.inner.getTransactionIsolation();
/*     */   }
/*     */   
/* 123 */   public SQLWarning getWarnings() throws SQLException { return this.inner.getWarnings(); }
/*     */   
/*     */   public void clearWarnings() throws SQLException {
/* 126 */     this.inner.clearWarnings();
/*     */   }
/*     */   
/* 129 */   public Map getTypeMap() throws SQLException { return this.inner.getTypeMap(); }
/*     */   
/*     */   public void setTypeMap(Map a) throws SQLException {
/* 132 */     this.inner.setTypeMap(a);
/*     */   }
/*     */   
/* 135 */   public void setHoldability(int a) throws SQLException { this.inner.setHoldability(a); }
/*     */   
/*     */   public int getHoldability() throws SQLException {
/* 138 */     return this.inner.getHoldability();
/*     */   }
/*     */   
/* 141 */   public Savepoint setSavepoint() throws SQLException { return this.inner.setSavepoint(); }
/*     */   
/*     */   public Savepoint setSavepoint(String a) throws SQLException {
/* 144 */     return this.inner.setSavepoint(a);
/*     */   }
/*     */   
/* 147 */   public void releaseSavepoint(Savepoint a) throws SQLException { this.inner.releaseSavepoint(a); }
/*     */   
/*     */   public void setReadOnly(boolean a) throws SQLException {
/* 150 */     this.inner.setReadOnly(a);
/*     */   }
/*     */   
/* 153 */   public boolean isReadOnly() throws SQLException { return this.inner.isReadOnly(); }
/*     */   
/*     */   public void close() throws SQLException {
/* 156 */     this.inner.close();
/*     */   }
/*     */   
/* 159 */   public boolean isClosed() throws SQLException { return this.inner.isClosed(); }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\sql\filter\FilterConnection.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */