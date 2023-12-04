/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BestResponseTimeBalanceStrategy
/*     */   implements BalanceStrategy
/*     */ {
/*     */   public void destroy() {}
/*     */   
/*     */   public void init(Connection conn, Properties props)
/*     */     throws SQLException
/*     */   {}
/*     */   
/*     */   public ConnectionImpl pickConnection(LoadBalancedConnectionProxy proxy, List<String> configuredHosts, Map<String, ConnectionImpl> liveConnections, long[] responseTimes, int numRetries)
/*     */     throws SQLException
/*     */   {
/*  47 */     Map<String, Long> blackList = proxy.getGlobalBlacklist();
/*     */     
/*  49 */     SQLException ex = null;
/*     */     
/*  51 */     int attempts = 0; ConnectionImpl conn; for (;;) { if (attempts >= numRetries) break label240;
/*  52 */       long minResponseTime = Long.MAX_VALUE;
/*     */       
/*  54 */       int bestHostIndex = 0;
/*     */       
/*     */ 
/*  57 */       if (blackList.size() == configuredHosts.size()) {
/*  58 */         blackList = proxy.getGlobalBlacklist();
/*     */       }
/*     */       
/*  61 */       for (int i = 0; i < responseTimes.length; i++) {
/*  62 */         long candidateResponseTime = responseTimes[i];
/*     */         
/*  64 */         if ((candidateResponseTime < minResponseTime) && (!blackList.containsKey(configuredHosts.get(i)))) {
/*  65 */           if (candidateResponseTime == 0L) {
/*  66 */             bestHostIndex = i;
/*     */             
/*  68 */             break;
/*     */           }
/*     */           
/*  71 */           bestHostIndex = i;
/*  72 */           minResponseTime = candidateResponseTime;
/*     */         }
/*     */       }
/*     */       
/*  76 */       String bestHost = (String)configuredHosts.get(bestHostIndex);
/*     */       
/*  78 */       conn = (ConnectionImpl)liveConnections.get(bestHost);
/*     */       
/*  80 */       if (conn == null)
/*     */         try {
/*  82 */           conn = proxy.createConnectionForHost(bestHost);
/*     */         } catch (SQLException sqlEx) {
/*  84 */           ex = sqlEx;
/*     */           
/*  86 */           if (proxy.shouldExceptionTriggerConnectionSwitch(sqlEx)) {
/*  87 */             proxy.addToGlobalBlacklist(bestHost);
/*  88 */             blackList.put(bestHost, null);
/*     */             
/*  90 */             if (blackList.size() == configuredHosts.size()) {
/*  91 */               attempts++;
/*     */               try {
/*  93 */                 Thread.sleep(250L);
/*     */               }
/*     */               catch (InterruptedException e) {}
/*  96 */               blackList = proxy.getGlobalBlacklist();
/*     */             }
/*     */             
/*     */           }
/*     */           else
/*     */           {
/* 102 */             throw sqlEx;
/*     */           }
/*     */         }
/*     */     }
/* 106 */     return conn;
/*     */     
/*     */     label240:
/* 109 */     if (ex != null) {
/* 110 */       throw ex;
/*     */     }
/*     */     
/* 113 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\BestResponseTimeBalanceStrategy.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */