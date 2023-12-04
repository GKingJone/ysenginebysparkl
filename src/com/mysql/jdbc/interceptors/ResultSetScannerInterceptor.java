/*    */ package com.mysql.jdbc.interceptors;
/*    */ 
/*    */ import com.mysql.jdbc.Connection;
/*    */ import com.mysql.jdbc.ResultSetInternalMethods;
/*    */ import com.mysql.jdbc.Statement;
/*    */ import com.mysql.jdbc.StatementInterceptor;
/*    */ import java.lang.reflect.InvocationHandler;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ import java.lang.reflect.Proxy;
/*    */ import java.sql.SQLException;
/*    */ import java.util.Properties;
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
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
/*    */ public class ResultSetScannerInterceptor
/*    */   implements StatementInterceptor
/*    */ {
/*    */   protected Pattern regexP;
/*    */   
/*    */   public void init(Connection conn, Properties props)
/*    */     throws SQLException
/*    */   {
/* 44 */     String regexFromUser = props.getProperty("resultSetScannerRegex");
/*    */     
/* 46 */     if ((regexFromUser == null) || (regexFromUser.length() == 0)) {
/* 47 */       throw new SQLException("resultSetScannerRegex must be configured, and must be > 0 characters");
/*    */     }
/*    */     try
/*    */     {
/* 51 */       this.regexP = Pattern.compile(regexFromUser);
/*    */     } catch (Throwable t) {
/* 53 */       SQLException sqlEx = new SQLException("Can't use configured regex due to underlying exception.");
/* 54 */       sqlEx.initCause(t);
/*    */       
/* 56 */       throw sqlEx;
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection)
/*    */     throws SQLException
/*    */   {
/* 65 */     final ResultSetInternalMethods finalResultSet = originalResultSet;
/*    */     
/* 67 */     (ResultSetInternalMethods)Proxy.newProxyInstance(originalResultSet.getClass().getClassLoader(), new Class[] { ResultSetInternalMethods.class }, new InvocationHandler()
/*    */     {
/*    */       public Object invoke(Object proxy, Method method, Object[] args)
/*    */         throws Throwable
/*    */       {
/* 72 */         Object invocationResult = method.invoke(finalResultSet, args);
/*    */         
/* 74 */         String methodName = method.getName();
/*    */         
/* 76 */         if (((invocationResult != null) && ((invocationResult instanceof String))) || ("getString".equals(methodName)) || ("getObject".equals(methodName)) || ("getObjectStoredProc".equals(methodName)))
/*    */         {
/* 78 */           Matcher matcher = ResultSetScannerInterceptor.this.regexP.matcher(invocationResult.toString());
/*    */           
/* 80 */           if (matcher.matches()) {
/* 81 */             throw new SQLException("value disallowed by filter");
/*    */           }
/*    */         }
/*    */         
/* 85 */         return invocationResult;
/*    */       }
/*    */     });
/*    */   }
/*    */   
/*    */ 
/*    */   public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement, Connection connection)
/*    */     throws SQLException
/*    */   {
/* 94 */     return null;
/*    */   }
/*    */   
/*    */   public boolean executeTopLevelOnly()
/*    */   {
/* 99 */     return false;
/*    */   }
/*    */   
/*    */   public void destroy() {}
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\interceptors\ResultSetScannerInterceptor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */