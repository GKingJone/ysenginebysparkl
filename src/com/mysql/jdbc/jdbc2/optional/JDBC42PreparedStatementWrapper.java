/*    */ package com.mysql.jdbc.jdbc2.optional;
/*    */ 
/*    */ import com.mysql.jdbc.SQLError;
/*    */ import java.sql.PreparedStatement;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.SQLType;
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
/*    */ public class JDBC42PreparedStatementWrapper
/*    */   extends JDBC4PreparedStatementWrapper
/*    */ {
/*    */   public JDBC42PreparedStatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, PreparedStatement toWrap)
/*    */   {
/* 36 */     super(c, conn, toWrap);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setObject(int parameterIndex, Object x, SQLType targetSqlType)
/*    */     throws SQLException
/*    */   {
/*    */     try
/*    */     {
/* 49 */       if (this.wrappedStmt != null) {
/* 50 */         ((PreparedStatement)this.wrappedStmt).setObject(parameterIndex, x, targetSqlType);
/*    */       } else {
/* 52 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*    */       }
/*    */     } catch (SQLException sqlEx) {
/* 55 */       checkAndFireConnectionError(sqlEx);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength)
/*    */     throws SQLException
/*    */   {
/*    */     try
/*    */     {
/* 70 */       if (this.wrappedStmt != null) {
/* 71 */         ((PreparedStatement)this.wrappedStmt).setObject(parameterIndex, x, targetSqlType, scaleOrLength);
/*    */       } else {
/* 73 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*    */       }
/*    */     } catch (SQLException sqlEx) {
/* 76 */       checkAndFireConnectionError(sqlEx);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\jdbc2\optional\JDBC42PreparedStatementWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */