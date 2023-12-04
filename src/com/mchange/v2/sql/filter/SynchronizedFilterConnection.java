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
/*     */ public abstract class SynchronizedFilterConnection
/*     */   implements Connection
/*     */ {
/*     */   protected Connection inner;
/*     */   
/*     */   public SynchronizedFilterConnection(Connection inner)
/*     */   {
/*  42 */     this.inner = inner;
/*     */   }
/*     */   
/*     */   public SynchronizedFilterConnection() {}
/*     */   
/*     */   public synchronized void setInner(Connection inner) {
/*  48 */     this.inner = inner;
/*     */   }
/*     */   
/*  51 */   public synchronized Connection getInner() { return this.inner; }
/*     */   
/*     */   public synchronized Statement createStatement(int a, int b, int c) throws SQLException {
/*  54 */     return this.inner.createStatement(a, b, c);
/*     */   }
/*     */   
/*  57 */   public synchronized Statement createStatement(int a, int b) throws SQLException { return this.inner.createStatement(a, b); }
/*     */   
/*     */   public synchronized Statement createStatement() throws SQLException {
/*  60 */     return this.inner.createStatement();
/*     */   }
/*     */   
/*  63 */   public synchronized PreparedStatement prepareStatement(String a, String[] b) throws SQLException { return this.inner.prepareStatement(a, b); }
/*     */   
/*     */   public synchronized PreparedStatement prepareStatement(String a) throws SQLException {
/*  66 */     return this.inner.prepareStatement(a);
/*     */   }
/*     */   
/*  69 */   public synchronized PreparedStatement prepareStatement(String a, int b, int c) throws SQLException { return this.inner.prepareStatement(a, b, c); }
/*     */   
/*     */   public synchronized PreparedStatement prepareStatement(String a, int b, int c, int d) throws SQLException {
/*  72 */     return this.inner.prepareStatement(a, b, c, d);
/*     */   }
/*     */   
/*  75 */   public synchronized PreparedStatement prepareStatement(String a, int b) throws SQLException { return this.inner.prepareStatement(a, b); }
/*     */   
/*     */   public synchronized PreparedStatement prepareStatement(String a, int[] b) throws SQLException {
/*  78 */     return this.inner.prepareStatement(a, b);
/*     */   }
/*     */   
/*  81 */   public synchronized CallableStatement prepareCall(String a, int b, int c, int d) throws SQLException { return this.inner.prepareCall(a, b, c, d); }
/*     */   
/*     */   public synchronized CallableStatement prepareCall(String a, int b, int c) throws SQLException {
/*  84 */     return this.inner.prepareCall(a, b, c);
/*     */   }
/*     */   
/*  87 */   public synchronized CallableStatement prepareCall(String a) throws SQLException { return this.inner.prepareCall(a); }
/*     */   
/*     */   public synchronized String nativeSQL(String a) throws SQLException {
/*  90 */     return this.inner.nativeSQL(a);
/*     */   }
/*     */   
/*  93 */   public synchronized void setAutoCommit(boolean a) throws SQLException { this.inner.setAutoCommit(a); }
/*     */   
/*     */   public synchronized boolean getAutoCommit() throws SQLException {
/*  96 */     return this.inner.getAutoCommit();
/*     */   }
/*     */   
/*  99 */   public synchronized void commit() throws SQLException { this.inner.commit(); }
/*     */   
/*     */   public synchronized void rollback(Savepoint a) throws SQLException {
/* 102 */     this.inner.rollback(a);
/*     */   }
/*     */   
/* 105 */   public synchronized void rollback() throws SQLException { this.inner.rollback(); }
/*     */   
/*     */   public synchronized DatabaseMetaData getMetaData() throws SQLException {
/* 108 */     return this.inner.getMetaData();
/*     */   }
/*     */   
/* 111 */   public synchronized void setCatalog(String a) throws SQLException { this.inner.setCatalog(a); }
/*     */   
/*     */   public synchronized String getCatalog() throws SQLException {
/* 114 */     return this.inner.getCatalog();
/*     */   }
/*     */   
/* 117 */   public synchronized void setTransactionIsolation(int a) throws SQLException { this.inner.setTransactionIsolation(a); }
/*     */   
/*     */   public synchronized int getTransactionIsolation() throws SQLException {
/* 120 */     return this.inner.getTransactionIsolation();
/*     */   }
/*     */   
/* 123 */   public synchronized SQLWarning getWarnings() throws SQLException { return this.inner.getWarnings(); }
/*     */   
/*     */   public synchronized void clearWarnings() throws SQLException {
/* 126 */     this.inner.clearWarnings();
/*     */   }
/*     */   
/* 129 */   public synchronized Map getTypeMap() throws SQLException { return this.inner.getTypeMap(); }
/*     */   
/*     */   public synchronized void setTypeMap(Map a) throws SQLException {
/* 132 */     this.inner.setTypeMap(a);
/*     */   }
/*     */   
/* 135 */   public synchronized void setHoldability(int a) throws SQLException { this.inner.setHoldability(a); }
/*     */   
/*     */   public synchronized int getHoldability() throws SQLException {
/* 138 */     return this.inner.getHoldability();
/*     */   }
/*     */   
/* 141 */   public synchronized Savepoint setSavepoint() throws SQLException { return this.inner.setSavepoint(); }
/*     */   
/*     */   public synchronized Savepoint setSavepoint(String a) throws SQLException {
/* 144 */     return this.inner.setSavepoint(a);
/*     */   }
/*     */   
/* 147 */   public synchronized void releaseSavepoint(Savepoint a) throws SQLException { this.inner.releaseSavepoint(a); }
/*     */   
/*     */   public synchronized void setReadOnly(boolean a) throws SQLException {
/* 150 */     this.inner.setReadOnly(a);
/*     */   }
/*     */   
/* 153 */   public synchronized boolean isReadOnly() throws SQLException { return this.inner.isReadOnly(); }
/*     */   
/*     */   public synchronized void close() throws SQLException {
/* 156 */     this.inner.close();
/*     */   }
/*     */   
/* 159 */   public synchronized boolean isClosed() throws SQLException { return this.inner.isClosed(); }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\sql\filter\SynchronizedFilterConnection.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */