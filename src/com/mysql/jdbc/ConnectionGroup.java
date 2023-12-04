/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public class ConnectionGroup
/*     */ {
/*     */   private String groupName;
/*  36 */   private long connections = 0L;
/*  37 */   private long activeConnections = 0L;
/*  38 */   private HashMap<Long, LoadBalancedConnectionProxy> connectionProxies = new HashMap();
/*  39 */   private Set<String> hostList = new HashSet();
/*  40 */   private boolean isInitialized = false;
/*  41 */   private long closedProxyTotalPhysicalConnections = 0L;
/*  42 */   private long closedProxyTotalTransactions = 0L;
/*  43 */   private int activeHosts = 0;
/*  44 */   private Set<String> closedHosts = new HashSet();
/*     */   
/*     */   ConnectionGroup(String groupName) {
/*  47 */     this.groupName = groupName;
/*     */   }
/*     */   
/*     */   public long registerConnectionProxy(LoadBalancedConnectionProxy proxy, List<String> localHostList)
/*     */   {
/*     */     long currentConnectionId;
/*  53 */     synchronized (this) {
/*  54 */       if (!this.isInitialized) {
/*  55 */         this.hostList.addAll(localHostList);
/*  56 */         this.isInitialized = true;
/*  57 */         this.activeHosts = localHostList.size();
/*     */       }
/*  59 */       currentConnectionId = ++this.connections;
/*  60 */       this.connectionProxies.put(Long.valueOf(currentConnectionId), proxy);
/*     */     }
/*  62 */     this.activeConnections += 1L;
/*     */     
/*  64 */     return currentConnectionId;
/*     */   }
/*     */   
/*     */   public String getGroupName() {
/*  68 */     return this.groupName;
/*     */   }
/*     */   
/*     */   public Collection<String> getInitialHosts() {
/*  72 */     return this.hostList;
/*     */   }
/*     */   
/*     */   public int getActiveHostCount() {
/*  76 */     return this.activeHosts;
/*     */   }
/*     */   
/*     */   public Collection<String> getClosedHosts() {
/*  80 */     return this.closedHosts;
/*     */   }
/*     */   
/*     */   public long getTotalLogicalConnectionCount() {
/*  84 */     return this.connections;
/*     */   }
/*     */   
/*     */   public long getActiveLogicalConnectionCount() {
/*  88 */     return this.activeConnections;
/*     */   }
/*     */   
/*     */   public long getActivePhysicalConnectionCount() {
/*  92 */     long result = 0L;
/*  93 */     Map<Long, LoadBalancedConnectionProxy> proxyMap = new HashMap();
/*  94 */     synchronized (this.connectionProxies) {
/*  95 */       proxyMap.putAll(this.connectionProxies);
/*     */     }
/*  97 */     for (LoadBalancedConnectionProxy proxy : proxyMap.values()) {
/*  98 */       result += proxy.getActivePhysicalConnectionCount();
/*     */     }
/* 100 */     return result;
/*     */   }
/*     */   
/*     */   public long getTotalPhysicalConnectionCount() {
/* 104 */     long allConnections = this.closedProxyTotalPhysicalConnections;
/* 105 */     Map<Long, LoadBalancedConnectionProxy> proxyMap = new HashMap();
/* 106 */     synchronized (this.connectionProxies) {
/* 107 */       proxyMap.putAll(this.connectionProxies);
/*     */     }
/* 109 */     for (LoadBalancedConnectionProxy proxy : proxyMap.values()) {
/* 110 */       allConnections += proxy.getTotalPhysicalConnectionCount();
/*     */     }
/* 112 */     return allConnections;
/*     */   }
/*     */   
/*     */   public long getTotalTransactionCount()
/*     */   {
/* 117 */     long transactions = this.closedProxyTotalTransactions;
/* 118 */     Map<Long, LoadBalancedConnectionProxy> proxyMap = new HashMap();
/* 119 */     synchronized (this.connectionProxies) {
/* 120 */       proxyMap.putAll(this.connectionProxies);
/*     */     }
/* 122 */     for (LoadBalancedConnectionProxy proxy : proxyMap.values()) {
/* 123 */       transactions += proxy.getTransactionCount();
/*     */     }
/* 125 */     return transactions;
/*     */   }
/*     */   
/*     */   public void closeConnectionProxy(LoadBalancedConnectionProxy proxy) {
/* 129 */     this.activeConnections -= 1L;
/* 130 */     this.connectionProxies.remove(Long.valueOf(proxy.getConnectionGroupProxyID()));
/* 131 */     this.closedProxyTotalPhysicalConnections += proxy.getTotalPhysicalConnectionCount();
/* 132 */     this.closedProxyTotalTransactions += proxy.getTransactionCount();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeHost(String hostPortPair)
/*     */     throws SQLException
/*     */   {
/* 143 */     removeHost(hostPortPair, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeHost(String hostPortPair, boolean removeExisting)
/*     */     throws SQLException
/*     */   {
/* 156 */     removeHost(hostPortPair, removeExisting, true);
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
/*     */ 
/*     */ 
/*     */   public synchronized void removeHost(String hostPortPair, boolean removeExisting, boolean waitForGracefulFailover)
/*     */     throws SQLException
/*     */   {
/* 172 */     if (this.activeHosts == 1) {
/* 173 */       throw SQLError.createSQLException("Cannot remove host, only one configured host active.", null);
/*     */     }
/*     */     
/* 176 */     if (this.hostList.remove(hostPortPair)) {
/* 177 */       this.activeHosts -= 1;
/*     */     } else {
/* 179 */       throw SQLError.createSQLException("Host is not configured: " + hostPortPair, null);
/*     */     }
/*     */     
/* 182 */     if (removeExisting)
/*     */     {
/* 184 */       Map<Long, LoadBalancedConnectionProxy> proxyMap = new HashMap();
/* 185 */       synchronized (this.connectionProxies) {
/* 186 */         proxyMap.putAll(this.connectionProxies);
/*     */       }
/*     */       
/* 189 */       for (LoadBalancedConnectionProxy proxy : proxyMap.values()) {
/* 190 */         if (waitForGracefulFailover) {
/* 191 */           proxy.removeHostWhenNotInUse(hostPortPair);
/*     */         } else {
/* 193 */           proxy.removeHost(hostPortPair);
/*     */         }
/*     */       }
/*     */     }
/* 197 */     this.closedHosts.add(hostPortPair);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addHost(String hostPortPair)
/*     */   {
/* 208 */     addHost(hostPortPair, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addHost(String hostPortPair, boolean forExisting)
/*     */   {
/* 220 */     synchronized (this) {
/* 221 */       if (this.hostList.add(hostPortPair)) {
/* 222 */         this.activeHosts += 1;
/*     */       }
/*     */     }
/*     */     
/* 226 */     if (!forExisting) {
/* 227 */       return;
/*     */     }
/*     */     
/*     */ 
/* 231 */     Map<Long, LoadBalancedConnectionProxy> proxyMap = new HashMap();
/* 232 */     synchronized (this.connectionProxies) {
/* 233 */       proxyMap.putAll(this.connectionProxies);
/*     */     }
/*     */     
/* 236 */     for (LoadBalancedConnectionProxy proxy : proxyMap.values()) {
/* 237 */       proxy.addHost(hostPortPair);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\ConnectionGroup.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */