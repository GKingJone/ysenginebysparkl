/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.ParameterMetaData;
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
/*     */ public class MysqlParameterMetadata
/*     */   implements ParameterMetaData
/*     */ {
/*  31 */   boolean returnSimpleMetadata = false;
/*     */   
/*  33 */   ResultSetMetaData metadata = null;
/*     */   
/*  35 */   int parameterCount = 0;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */   
/*     */   MysqlParameterMetadata(Field[] fieldInfo, int parameterCount, ExceptionInterceptor exceptionInterceptor)
/*     */   {
/*  40 */     this.metadata = new ResultSetMetaData(fieldInfo, false, true, exceptionInterceptor);
/*     */     
/*  42 */     this.parameterCount = parameterCount;
/*  43 */     this.exceptionInterceptor = exceptionInterceptor;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   MysqlParameterMetadata(int count)
/*     */   {
/*  53 */     this.parameterCount = count;
/*  54 */     this.returnSimpleMetadata = true;
/*     */   }
/*     */   
/*     */   public int getParameterCount() throws SQLException {
/*  58 */     return this.parameterCount;
/*     */   }
/*     */   
/*     */   public int isNullable(int arg0) throws SQLException {
/*  62 */     checkAvailable();
/*     */     
/*  64 */     return this.metadata.isNullable(arg0);
/*     */   }
/*     */   
/*     */   private void checkAvailable() throws SQLException {
/*  68 */     if ((this.metadata == null) || (this.metadata.fields == null)) {
/*  69 */       throw SQLError.createSQLException("Parameter metadata not available for the given statement", "S1C00", this.exceptionInterceptor);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isSigned(int arg0) throws SQLException
/*     */   {
/*  75 */     if (this.returnSimpleMetadata) {
/*  76 */       checkBounds(arg0);
/*     */       
/*  78 */       return false;
/*     */     }
/*     */     
/*  81 */     checkAvailable();
/*     */     
/*  83 */     return this.metadata.isSigned(arg0);
/*     */   }
/*     */   
/*     */   public int getPrecision(int arg0) throws SQLException {
/*  87 */     if (this.returnSimpleMetadata) {
/*  88 */       checkBounds(arg0);
/*     */       
/*  90 */       return 0;
/*     */     }
/*     */     
/*  93 */     checkAvailable();
/*     */     
/*  95 */     return this.metadata.getPrecision(arg0);
/*     */   }
/*     */   
/*     */   public int getScale(int arg0) throws SQLException {
/*  99 */     if (this.returnSimpleMetadata) {
/* 100 */       checkBounds(arg0);
/*     */       
/* 102 */       return 0;
/*     */     }
/*     */     
/* 105 */     checkAvailable();
/*     */     
/* 107 */     return this.metadata.getScale(arg0);
/*     */   }
/*     */   
/*     */   public int getParameterType(int arg0) throws SQLException {
/* 111 */     if (this.returnSimpleMetadata) {
/* 112 */       checkBounds(arg0);
/*     */       
/* 114 */       return 12;
/*     */     }
/*     */     
/* 117 */     checkAvailable();
/*     */     
/* 119 */     return this.metadata.getColumnType(arg0);
/*     */   }
/*     */   
/*     */   public String getParameterTypeName(int arg0) throws SQLException {
/* 123 */     if (this.returnSimpleMetadata) {
/* 124 */       checkBounds(arg0);
/*     */       
/* 126 */       return "VARCHAR";
/*     */     }
/*     */     
/* 129 */     checkAvailable();
/*     */     
/* 131 */     return this.metadata.getColumnTypeName(arg0);
/*     */   }
/*     */   
/*     */   public String getParameterClassName(int arg0) throws SQLException {
/* 135 */     if (this.returnSimpleMetadata) {
/* 136 */       checkBounds(arg0);
/*     */       
/* 138 */       return "java.lang.String";
/*     */     }
/*     */     
/* 141 */     checkAvailable();
/*     */     
/* 143 */     return this.metadata.getColumnClassName(arg0);
/*     */   }
/*     */   
/*     */   public int getParameterMode(int arg0) throws SQLException {
/* 147 */     return 1;
/*     */   }
/*     */   
/*     */   private void checkBounds(int paramNumber) throws SQLException {
/* 151 */     if (paramNumber < 1) {
/* 152 */       throw SQLError.createSQLException("Parameter index of '" + paramNumber + "' is invalid.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     
/*     */ 
/* 156 */     if (paramNumber > this.parameterCount) {
/* 157 */       throw SQLError.createSQLException("Parameter index of '" + paramNumber + "' is greater than number of parameters, which is '" + this.parameterCount + "'.", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isWrapperFor(Class<?> iface)
/*     */     throws SQLException
/*     */   {
/* 169 */     return iface.isInstance(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public <T> T unwrap(Class<T> iface)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 178 */       return (T)iface.cast(this);
/*     */     } catch (ClassCastException cce) {
/* 180 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\MysqlParameterMetadata.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */