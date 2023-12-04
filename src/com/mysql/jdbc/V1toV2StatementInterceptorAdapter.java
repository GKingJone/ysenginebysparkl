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
/*    */ public class V1toV2StatementInterceptorAdapter
/*    */   implements StatementInterceptorV2
/*    */ {
/*    */   private final StatementInterceptor toProxy;
/*    */   
/*    */   public V1toV2StatementInterceptorAdapter(StatementInterceptor toProxy)
/*    */   {
/* 33 */     this.toProxy = toProxy;
/*    */   }
/*    */   
/*    */   public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection, int warningCount, boolean noIndexUsed, boolean noGoodIndexUsed, SQLException statementException) throws SQLException
/*    */   {
/* 38 */     return this.toProxy.postProcess(sql, interceptedStatement, originalResultSet, connection);
/*    */   }
/*    */   
/*    */   public void destroy() {
/* 42 */     this.toProxy.destroy();
/*    */   }
/*    */   
/*    */   public boolean executeTopLevelOnly() {
/* 46 */     return this.toProxy.executeTopLevelOnly();
/*    */   }
/*    */   
/*    */   public void init(Connection conn, Properties props) throws SQLException {
/* 50 */     this.toProxy.init(conn, props);
/*    */   }
/*    */   
/*    */   public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement, Connection connection) throws SQLException {
/* 54 */     return this.toProxy.preProcess(sql, interceptedStatement, connection);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\V1toV2StatementInterceptorAdapter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */