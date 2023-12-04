/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLType;
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
/*     */ public class JDBC42CallableStatementWrapper
/*     */   extends JDBC4CallableStatementWrapper
/*     */ {
/*     */   public JDBC42CallableStatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, CallableStatement toWrap)
/*     */   {
/*  36 */     super(c, conn, toWrap);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void registerOutParameter(int parameterIndex, SQLType sqlType)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/*  48 */       if (this.wrappedStmt != null) {
/*  49 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterIndex, sqlType);
/*     */       } else {
/*  51 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/*  54 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void registerOutParameter(int parameterIndex, SQLType sqlType, int scale)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/*  68 */       if (this.wrappedStmt != null) {
/*  69 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterIndex, sqlType, scale);
/*     */       } else {
/*  71 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/*  74 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void registerOutParameter(int parameterIndex, SQLType sqlType, String typeName)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/*  88 */       if (this.wrappedStmt != null) {
/*  89 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterIndex, sqlType, typeName);
/*     */       } else {
/*  91 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/*  94 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void registerOutParameter(String parameterName, SQLType sqlType)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 107 */       if (this.wrappedStmt != null) {
/* 108 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType);
/*     */       } else {
/* 110 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 113 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void registerOutParameter(String parameterName, SQLType sqlType, int scale)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 127 */       if (this.wrappedStmt != null) {
/* 128 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType, scale);
/*     */       } else {
/* 130 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 133 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void registerOutParameter(String parameterName, SQLType sqlType, String typeName)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 147 */       if (this.wrappedStmt != null) {
/* 148 */         ((CallableStatement)this.wrappedStmt).registerOutParameter(parameterName, sqlType, typeName);
/*     */       } else {
/* 150 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 153 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setObject(int parameterIndex, Object x, SQLType targetSqlType)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 167 */       if (this.wrappedStmt != null) {
/* 168 */         ((CallableStatement)this.wrappedStmt).setObject(parameterIndex, x, targetSqlType);
/*     */       } else {
/* 170 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 173 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 188 */       if (this.wrappedStmt != null) {
/* 189 */         ((CallableStatement)this.wrappedStmt).setObject(parameterIndex, x, targetSqlType, scaleOrLength);
/*     */       } else {
/* 191 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 194 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setObject(String parameterName, Object x, SQLType targetSqlType)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 208 */       if (this.wrappedStmt != null) {
/* 209 */         ((CallableStatement)this.wrappedStmt).setObject(parameterName, x, targetSqlType);
/*     */       } else {
/* 211 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 214 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setObject(String parameterName, Object x, SQLType targetSqlType, int scaleOrLength)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 229 */       if (this.wrappedStmt != null) {
/* 230 */         ((CallableStatement)this.wrappedStmt).setObject(parameterName, x, targetSqlType, scaleOrLength);
/*     */       } else {
/* 232 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 235 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\jdbc2\optional\JDBC42CallableStatementWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */