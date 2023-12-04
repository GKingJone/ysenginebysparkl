/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ import java.util.Properties;
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
/*    */ public class NoSubInterceptorWrapper
/*    */   implements StatementInterceptorV2
/*    */ {
/*    */   private final StatementInterceptorV2 underlyingInterceptor;
/*    */   
/*    */   public NoSubInterceptorWrapper(StatementInterceptorV2 underlyingInterceptor)
/*    */   {
/* 37 */     if (underlyingInterceptor == null) {
/* 38 */       throw new RuntimeException("Interceptor to be wrapped can not be NULL");
/*    */     }
/*    */     
/* 41 */     this.underlyingInterceptor = underlyingInterceptor;
/*    */   }
/*    */   
/*    */   public void destroy() {
/* 45 */     this.underlyingInterceptor.destroy();
/*    */   }
/*    */   
/*    */   public boolean executeTopLevelOnly() {
/* 49 */     return this.underlyingInterceptor.executeTopLevelOnly();
/*    */   }
/*    */   
/*    */   public void init(Connection conn, Properties props) throws SQLException {
/* 53 */     this.underlyingInterceptor.init(conn, props);
/*    */   }
/*    */   
/*    */   public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection, int warningCount, boolean noIndexUsed, boolean noGoodIndexUsed, SQLException statementException) throws SQLException
/*    */   {
/* 58 */     this.underlyingInterceptor.postProcess(sql, interceptedStatement, originalResultSet, connection, warningCount, noIndexUsed, noGoodIndexUsed, statementException);
/*    */     
/*    */ 
/* 61 */     return null;
/*    */   }
/*    */   
/*    */   public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement, Connection connection) throws SQLException {
/* 65 */     this.underlyingInterceptor.preProcess(sql, interceptedStatement, connection);
/*    */     
/* 67 */     return null;
/*    */   }
/*    */   
/*    */   public StatementInterceptorV2 getUnderlyingInterceptor() {
/* 71 */     return this.underlyingInterceptor;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\NoSubInterceptorWrapper.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */