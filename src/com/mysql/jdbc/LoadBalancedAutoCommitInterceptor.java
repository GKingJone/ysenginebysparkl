/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.Properties;
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
/*     */ public class LoadBalancedAutoCommitInterceptor
/*     */   implements StatementInterceptorV2
/*     */ {
/*  30 */   private int matchingAfterStatementCount = 0;
/*  31 */   private int matchingAfterStatementThreshold = 0;
/*     */   private String matchingAfterStatementRegex;
/*     */   private ConnectionImpl conn;
/*  34 */   private LoadBalancedConnectionProxy proxy = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void destroy() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean executeTopLevelOnly()
/*     */   {
/*  47 */     return false;
/*     */   }
/*     */   
/*     */   public void init(Connection connection, Properties props) throws SQLException {
/*  51 */     this.conn = ((ConnectionImpl)connection);
/*     */     
/*  53 */     String autoCommitSwapThresholdAsString = props.getProperty("loadBalanceAutoCommitStatementThreshold", "0");
/*     */     try {
/*  55 */       this.matchingAfterStatementThreshold = Integer.parseInt(autoCommitSwapThresholdAsString);
/*     */     }
/*     */     catch (NumberFormatException nfe) {}
/*     */     
/*  59 */     String autoCommitSwapRegex = props.getProperty("loadBalanceAutoCommitStatementRegex", "");
/*  60 */     if ("".equals(autoCommitSwapRegex)) {
/*  61 */       return;
/*     */     }
/*  63 */     this.matchingAfterStatementRegex = autoCommitSwapRegex;
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
/*     */   public ResultSetInternalMethods postProcess(String sql, Statement interceptedStatement, ResultSetInternalMethods originalResultSet, Connection connection, int warningCount, boolean noIndexUsed, boolean noGoodIndexUsed, SQLException statementException)
/*     */     throws SQLException
/*     */   {
/*  77 */     if (!this.conn.getAutoCommit()) {
/*  78 */       this.matchingAfterStatementCount = 0;
/*     */     }
/*     */     else
/*     */     {
/*  82 */       if ((this.proxy == null) && (this.conn.isProxySet())) {
/*  83 */         MySQLConnection lcl_proxy = this.conn.getMultiHostSafeProxy();
/*  84 */         while ((lcl_proxy != null) && (!(lcl_proxy instanceof LoadBalancedMySQLConnection))) {
/*  85 */           lcl_proxy = lcl_proxy.getMultiHostSafeProxy();
/*     */         }
/*  87 */         if (lcl_proxy != null) {
/*  88 */           this.proxy = ((LoadBalancedMySQLConnection)lcl_proxy).getThisAsProxy();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*  93 */       if (this.proxy != null)
/*     */       {
/*  95 */         if ((this.matchingAfterStatementRegex == null) || (sql.matches(this.matchingAfterStatementRegex))) {
/*  96 */           this.matchingAfterStatementCount += 1;
/*     */         }
/*     */       }
/*     */       
/* 100 */       if (this.matchingAfterStatementCount >= this.matchingAfterStatementThreshold) {
/* 101 */         this.matchingAfterStatementCount = 0;
/*     */         try {
/* 103 */           if (this.proxy != null) {
/* 104 */             this.proxy.pickNewConnection();
/*     */           }
/*     */         }
/*     */         catch (SQLException e) {}
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 114 */     return originalResultSet;
/*     */   }
/*     */   
/*     */   public ResultSetInternalMethods preProcess(String sql, Statement interceptedStatement, Connection connection) throws SQLException
/*     */   {
/* 119 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\LoadBalancedAutoCommitInterceptor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */