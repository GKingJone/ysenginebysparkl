/*     */ package com.mchange.v2.c3p0.impl;
/*     */ 
/*     */ import com.mchange.v2.sql.filter.FilterDatabaseMetaData;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Set;
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
/*     */ final class SetManagedDatabaseMetaData
/*     */   extends FilterDatabaseMetaData
/*     */ {
/*     */   Set activeResultSets;
/*     */   Connection returnableProxy;
/*     */   
/*     */   SetManagedDatabaseMetaData(DatabaseMetaData inner, Set activeResultSets, Connection returnableProxy)
/*     */   {
/*  37 */     super(inner);
/*  38 */     this.activeResultSets = activeResultSets;
/*  39 */     this.returnableProxy = returnableProxy;
/*     */   }
/*     */   
/*     */   public Connection getConnection() throws SQLException {
/*  43 */     return this.returnableProxy;
/*     */   }
/*     */   
/*     */   public ResultSet getProcedures(String a, String b, String c) throws SQLException {
/*  47 */     return new NullStatementSetManagedResultSet(this.inner.getProcedures(a, b, c), this.activeResultSets);
/*     */   }
/*     */   
/*     */   public ResultSet getProcedureColumns(String a, String b, String c, String d) throws SQLException
/*     */   {
/*  52 */     return new NullStatementSetManagedResultSet(this.inner.getProcedureColumns(a, b, c, d), this.activeResultSets);
/*     */   }
/*     */   
/*     */   public ResultSet getTables(String a, String b, String c, String[] d) throws SQLException
/*     */   {
/*  57 */     return new NullStatementSetManagedResultSet(this.inner.getTables(a, b, c, d), this.activeResultSets);
/*     */   }
/*     */   
/*     */   public ResultSet getSchemas() throws SQLException
/*     */   {
/*  62 */     return new NullStatementSetManagedResultSet(this.inner.getSchemas(), this.activeResultSets);
/*     */   }
/*     */   
/*     */   public ResultSet getCatalogs() throws SQLException
/*     */   {
/*  67 */     return new NullStatementSetManagedResultSet(this.inner.getCatalogs(), this.activeResultSets);
/*     */   }
/*     */   
/*     */   public ResultSet getTableTypes() throws SQLException
/*     */   {
/*  72 */     return new NullStatementSetManagedResultSet(this.inner.getTableTypes(), this.activeResultSets);
/*     */   }
/*     */   
/*     */   public ResultSet getColumns(String a, String b, String c, String d) throws SQLException
/*     */   {
/*  77 */     return new NullStatementSetManagedResultSet(this.inner.getColumns(a, b, c, d), this.activeResultSets);
/*     */   }
/*     */   
/*     */   public ResultSet getColumnPrivileges(String a, String b, String c, String d) throws SQLException
/*     */   {
/*  82 */     return new NullStatementSetManagedResultSet(this.inner.getColumnPrivileges(a, b, c, d), this.activeResultSets);
/*     */   }
/*     */   
/*     */   public ResultSet getTablePrivileges(String a, String b, String c) throws SQLException
/*     */   {
/*  87 */     return new NullStatementSetManagedResultSet(this.inner.getTablePrivileges(a, b, c), this.activeResultSets);
/*     */   }
/*     */   
/*     */   public ResultSet getBestRowIdentifier(String a, String b, String c, int d, boolean e) throws SQLException
/*     */   {
/*  92 */     return new NullStatementSetManagedResultSet(this.inner.getBestRowIdentifier(a, b, c, d, e), this.activeResultSets);
/*     */   }
/*     */   
/*     */   public ResultSet getVersionColumns(String a, String b, String c) throws SQLException
/*     */   {
/*  97 */     return new NullStatementSetManagedResultSet(this.inner.getVersionColumns(a, b, c), this.activeResultSets);
/*     */   }
/*     */   
/*     */   public ResultSet getPrimaryKeys(String a, String b, String c) throws SQLException
/*     */   {
/* 102 */     return new NullStatementSetManagedResultSet(this.inner.getPrimaryKeys(a, b, c), this.activeResultSets);
/*     */   }
/*     */   
/*     */   public ResultSet getImportedKeys(String a, String b, String c) throws SQLException
/*     */   {
/* 107 */     return new NullStatementSetManagedResultSet(this.inner.getImportedKeys(a, b, c), this.activeResultSets);
/*     */   }
/*     */   
/*     */   public ResultSet getExportedKeys(String a, String b, String c) throws SQLException
/*     */   {
/* 112 */     return new NullStatementSetManagedResultSet(this.inner.getExportedKeys(a, b, c), this.activeResultSets);
/*     */   }
/*     */   
/*     */   public ResultSet getCrossReference(String a, String b, String c, String d, String e, String f) throws SQLException
/*     */   {
/* 117 */     return new NullStatementSetManagedResultSet(this.inner.getCrossReference(a, b, c, d, e, f), this.activeResultSets);
/*     */   }
/*     */   
/*     */   public ResultSet getTypeInfo() throws SQLException
/*     */   {
/* 122 */     return new NullStatementSetManagedResultSet(this.inner.getTypeInfo(), this.activeResultSets);
/*     */   }
/*     */   
/*     */   public ResultSet getIndexInfo(String a, String b, String c, boolean d, boolean e) throws SQLException
/*     */   {
/* 127 */     return new NullStatementSetManagedResultSet(this.inner.getIndexInfo(a, b, c, d, e), this.activeResultSets);
/*     */   }
/*     */   
/*     */   public ResultSet getUDTs(String a, String b, String c, int[] d) throws SQLException
/*     */   {
/* 132 */     return new NullStatementSetManagedResultSet(this.inner.getUDTs(a, b, c, d), this.activeResultSets);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\SetManagedDatabaseMetaData.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */