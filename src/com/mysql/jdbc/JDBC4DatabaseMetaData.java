/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.RowIdLifetime;
/*     */ import java.sql.SQLException;
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
/*     */ public class JDBC4DatabaseMetaData
/*     */   extends DatabaseMetaData
/*     */ {
/*     */   public JDBC4DatabaseMetaData(MySQLConnection connToSet, String databaseToSet)
/*     */   {
/*  38 */     super(connToSet, databaseToSet);
/*     */   }
/*     */   
/*     */   public RowIdLifetime getRowIdLifetime() throws SQLException {
/*  42 */     return RowIdLifetime.ROWID_UNSUPPORTED;
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
/*     */ 
/*     */ 
/*     */   public boolean isWrapperFor(Class<?> iface)
/*     */     throws SQLException
/*     */   {
/*  64 */     return iface.isInstance(this);
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
/*     */   public <T> T unwrap(Class<T> iface)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/*  86 */       return (T)iface.cast(this);
/*     */     } catch (ClassCastException cce) {
/*  88 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", this.conn
/*  89 */         .getExceptionInterceptor());
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
/*  94 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern)
/*     */     throws SQLException
/*     */   {
/* 106 */     Field[] fields = createProcedureColumnsFields();
/*     */     
/* 108 */     return getProcedureOrFunctionColumns(fields, catalog, schemaPattern, procedureNamePattern, columnNamePattern, true, this.conn
/* 109 */       .getGetProceduresReturnsFunctions());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern)
/*     */     throws SQLException
/*     */   {
/* 120 */     Field[] fields = createFieldMetadataForGetProcedures();
/*     */     
/* 122 */     return getProceduresAndOrFunctions(fields, catalog, schemaPattern, procedureNamePattern, true, this.conn.getGetProceduresReturnsFunctions());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int getJDBC4FunctionNoTableConstant()
/*     */   {
/* 131 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int getColumnType(boolean isOutParam, boolean isInParam, boolean isReturnParam, boolean forGetFunctionColumns)
/*     */   {
/* 140 */     return getProcedureOrFunctionColumnType(isOutParam, isInParam, isReturnParam, forGetFunctionColumns);
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
/*     */   protected static int getProcedureOrFunctionColumnType(boolean isOutParam, boolean isInParam, boolean isReturnParam, boolean forGetFunctionColumns)
/*     */   {
/* 159 */     if ((isInParam) && (isOutParam))
/* 160 */       return forGetFunctionColumns ? 2 : 2;
/* 161 */     if (isInParam)
/* 162 */       return forGetFunctionColumns ? 1 : 1;
/* 163 */     if (isOutParam)
/* 164 */       return forGetFunctionColumns ? 3 : 4;
/* 165 */     if (isReturnParam) {
/* 166 */       return forGetFunctionColumns ? 4 : 5;
/*     */     }
/* 168 */     return forGetFunctionColumns ? 0 : 0;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\JDBC4DatabaseMetaData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */