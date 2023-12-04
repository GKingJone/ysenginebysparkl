/*    */ package com.mysql.jdbc.interceptors;
/*    */ 
/*    */ import com.mysql.jdbc.Connection;
/*    */ import com.mysql.jdbc.ResultSetInternalMethods;
/*    */ import com.mysql.jdbc.Statement;
/*    */ import com.mysql.jdbc.StatementInterceptor;
/*    */ import java.sql.PreparedStatement;
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
/*    */ public class SessionAssociationInterceptor
/*    */   implements StatementInterceptor
/*    */ {
/*    */   protected String currentSessionKey;
/* 38 */   protected static final ThreadLocal<String> sessionLocal = new ThreadLocal();
/*    */   
/*    */   public static final void setSessionKey(String key) {
/* 41 */     sessionLocal.set(key);
/*    */   }
/*    */   
/*    */   public static final void resetSessionKey() {
/* 45 */     sessionLocal.set(null);
/*    */   }
/*    */   
/*    */   public static final String getSessionKey() {
/* 49 */     return (String)sessionLocal.get();
/*    */   }
/*    */   
/*    */   public boolean executeTopLevelOnly() {
/* 53 */     return true;
/*    */   }
/*    */   
/*    */   public void init(Connection conn, Properties props)
/*    */     throws SQLException
/*    */   {}
/*    */   
/*    */   public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection) throws SQLException
/*    */   {
/* 62 */     return null;
/*    */   }
/*    */   
/*    */   public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement, Connection connection) throws SQLException {
/* 66 */     String key = getSessionKey();
/*    */     
/* 68 */     if ((key != null) && (!key.equals(this.currentSessionKey))) {
/* 69 */       PreparedStatement pstmt = connection.clientPrepareStatement("SET @mysql_proxy_session=?");
/*    */       try
/*    */       {
/* 72 */         pstmt.setString(1, key);
/* 73 */         pstmt.execute();
/*    */       } finally {
/* 75 */         pstmt.close();
/*    */       }
/*    */       
/* 78 */       this.currentSessionKey = key;
/*    */     }
/*    */     
/* 81 */     return null;
/*    */   }
/*    */   
/*    */   public void destroy() {}
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\interceptors\SessionAssociationInterceptor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */