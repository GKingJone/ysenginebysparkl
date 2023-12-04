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
/*     */ public class JDBC4DatabaseMetaDataUsingInfoSchema
/*     */   extends DatabaseMetaDataUsingInfoSchema
/*     */ {
/*     */   public JDBC4DatabaseMetaDataUsingInfoSchema(MySQLConnection connToSet, String databaseToSet)
/*     */     throws SQLException
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ResultSet getProcedureColumnsNoISParametersView(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern)
/*     */     throws SQLException
/*     */   {
/* 102 */     Field[] fields = createProcedureColumnsFields();
/*     */     
/* 104 */     return getProcedureOrFunctionColumns(fields, catalog, schemaPattern, procedureNamePattern, columnNamePattern, true, this.conn
/* 105 */       .getGetProceduresReturnsFunctions());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String getRoutineTypeConditionForGetProcedures()
/*     */   {
/* 115 */     return this.conn.getGetProceduresReturnsFunctions() ? "" : "ROUTINE_TYPE = 'PROCEDURE' AND ";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String getRoutineTypeConditionForGetProcedureColumns()
/*     */   {
/* 126 */     return this.conn.getGetProceduresReturnsFunctions() ? "" : "ROUTINE_TYPE = 'PROCEDURE' AND ";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int getJDBC4FunctionConstant(JDBC4FunctionConstant constant)
/*     */   {
/* 138 */     switch (constant) {
/*     */     case FUNCTION_COLUMN_IN: 
/* 140 */       return 1;
/*     */     case FUNCTION_COLUMN_INOUT: 
/* 142 */       return 2;
/*     */     case FUNCTION_COLUMN_OUT: 
/* 144 */       return 3;
/*     */     case FUNCTION_COLUMN_RETURN: 
/* 146 */       return 4;
/*     */     case FUNCTION_COLUMN_RESULT: 
/* 148 */       return 5;
/*     */     case FUNCTION_COLUMN_UNKNOWN: 
/* 150 */       return 0;
/*     */     case FUNCTION_NO_NULLS: 
/* 152 */       return 0;
/*     */     case FUNCTION_NULLABLE: 
/* 154 */       return 1;
/*     */     case FUNCTION_NULLABLE_UNKNOWN: 
/* 156 */       return 2;
/*     */     }
/* 158 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int getJDBC4FunctionNoTableConstant()
/*     */   {
/* 168 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int getColumnType(boolean isOutParam, boolean isInParam, boolean isReturnParam, boolean forGetFunctionColumns)
/*     */   {
/* 177 */     return JDBC4DatabaseMetaData.getProcedureOrFunctionColumnType(isOutParam, isInParam, isReturnParam, forGetFunctionColumns);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\JDBC4DatabaseMetaDataUsingInfoSchema.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */