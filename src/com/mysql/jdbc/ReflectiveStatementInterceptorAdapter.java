/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
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
/*    */ public class ReflectiveStatementInterceptorAdapter
/*    */   implements StatementInterceptorV2
/*    */ {
/*    */   private final StatementInterceptor toProxy;
/*    */   final Method v2PostProcessMethod;
/*    */   
/*    */   public ReflectiveStatementInterceptorAdapter(StatementInterceptor toProxy)
/*    */   {
/* 38 */     this.toProxy = toProxy;
/* 39 */     this.v2PostProcessMethod = getV2PostProcessMethod(toProxy.getClass());
/*    */   }
/*    */   
/*    */   public void destroy() {
/* 43 */     this.toProxy.destroy();
/*    */   }
/*    */   
/*    */   public boolean executeTopLevelOnly() {
/* 47 */     return this.toProxy.executeTopLevelOnly();
/*    */   }
/*    */   
/*    */   public void init(Connection conn, Properties props) throws SQLException {
/* 51 */     this.toProxy.init(conn, props);
/*    */   }
/*    */   
/*    */   public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection, int warningCount, boolean noIndexUsed, boolean noGoodIndexUsed, SQLException statementException) throws SQLException
/*    */   {
/*    */     try {
/* 57 */       return (ResultSetInternalMethods)this.v2PostProcessMethod.invoke(this.toProxy, new Object[] { sql, interceptedStatement, originalResultSet, connection, Integer.valueOf(warningCount), noIndexUsed ? Boolean.TRUE : Boolean.FALSE, noGoodIndexUsed ? Boolean.TRUE : Boolean.FALSE, statementException });
/*    */     }
/*    */     catch (IllegalArgumentException e)
/*    */     {
/* 61 */       SQLException sqlEx = new SQLException("Unable to reflectively invoke interceptor");
/* 62 */       sqlEx.initCause(e);
/*    */       
/* 64 */       throw sqlEx;
/*    */     } catch (IllegalAccessException e) {
/* 66 */       SQLException sqlEx = new SQLException("Unable to reflectively invoke interceptor");
/* 67 */       sqlEx.initCause(e);
/*    */       
/* 69 */       throw sqlEx;
/*    */     } catch (InvocationTargetException e) {
/* 71 */       SQLException sqlEx = new SQLException("Unable to reflectively invoke interceptor");
/* 72 */       sqlEx.initCause(e);
/*    */       
/* 74 */       throw sqlEx;
/*    */     }
/*    */   }
/*    */   
/*    */   public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement, Connection connection) throws SQLException {
/* 79 */     return this.toProxy.preProcess(sql, interceptedStatement, connection);
/*    */   }
/*    */   
/*    */   public static final Method getV2PostProcessMethod(Class<?> toProxyClass) {
/*    */     try {
/* 84 */       return toProxyClass.getMethod("postProcess", new Class[] { String.class, Statement.class, ResultSetInternalMethods.class, Connection.class, Integer.TYPE, Boolean.TYPE, Boolean.TYPE, SQLException.class });
/*    */ 
/*    */     }
/*    */     catch (SecurityException e)
/*    */     {
/* 89 */       return null;
/*    */     } catch (NoSuchMethodException e) {}
/* 91 */     return null;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\ReflectiveStatementInterceptorAdapter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */