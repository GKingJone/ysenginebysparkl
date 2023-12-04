/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Executor;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LoadBalancedConnectionProxy
/*     */   extends MultiHostConnectionProxy
/*     */   implements PingTarget
/*     */ {
/*  56 */   private ConnectionGroup connectionGroup = null;
/*  57 */   private long connectionGroupProxyID = 0L;
/*     */   
/*     */   protected Map<String, ConnectionImpl> liveConnections;
/*     */   private Map<String, Integer> hostsToListIndexMap;
/*     */   private Map<ConnectionImpl, String> connectionsToHostsMap;
/*  62 */   private long totalPhysicalConnections = 0L;
/*     */   
/*     */   private long[] responseTimes;
/*     */   private int retriesAllDown;
/*     */   private BalanceStrategy balancer;
/*  67 */   private int autoCommitSwapThreshold = 0;
/*     */   
/*     */   public static final String BLACKLIST_TIMEOUT_PROPERTY_KEY = "loadBalanceBlacklistTimeout";
/*  70 */   private int globalBlacklistTimeout = 0;
/*  71 */   private static Map<String, Long> globalBlacklist = new HashMap();
/*     */   public static final String HOST_REMOVAL_GRACE_PERIOD_PROPERTY_KEY = "loadBalanceHostRemovalGracePeriod";
/*  73 */   private int hostRemovalGracePeriod = 0;
/*     */   
/*  75 */   private Set<String> hostsToRemove = new HashSet();
/*     */   
/*  77 */   private boolean inTransaction = false;
/*  78 */   private long transactionStartTime = 0L;
/*  79 */   private long transactionCount = 0L;
/*     */   
/*     */   private LoadBalanceExceptionChecker exceptionChecker;
/*     */   private static Constructor<?> JDBC_4_LB_CONNECTION_CTOR;
/*     */   private static Class<?>[] INTERFACES_TO_PROXY;
/*     */   
/*     */   static
/*     */   {
/*  87 */     if (Util.isJdbc4()) {
/*     */       try {
/*  89 */         JDBC_4_LB_CONNECTION_CTOR = Class.forName("com.mysql.jdbc.JDBC4LoadBalancedMySQLConnection").getConstructor(new Class[] { LoadBalancedConnectionProxy.class });
/*     */         
/*  91 */         INTERFACES_TO_PROXY = new Class[] { LoadBalancedConnection.class, Class.forName("com.mysql.jdbc.JDBC4MySQLConnection") };
/*     */       } catch (SecurityException e) {
/*  93 */         throw new RuntimeException(e);
/*     */       } catch (NoSuchMethodException e) {
/*  95 */         throw new RuntimeException(e);
/*     */       } catch (ClassNotFoundException e) {
/*  97 */         throw new RuntimeException(e);
/*     */       }
/*     */     } else {
/* 100 */       INTERFACES_TO_PROXY = new Class[] { LoadBalancedConnection.class };
/*     */     }
/*     */   }
/*     */   
/*     */   public static LoadBalancedConnection createProxyInstance(List<String> hosts, Properties props) throws SQLException {
/* 105 */     LoadBalancedConnectionProxy connProxy = new LoadBalancedConnectionProxy(hosts, props);
/*     */     
/* 107 */     return (LoadBalancedConnection)Proxy.newProxyInstance(LoadBalancedConnection.class.getClassLoader(), INTERFACES_TO_PROXY, connProxy);
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
/*     */   private LoadBalancedConnectionProxy(List<String> hosts, Properties props)
/*     */     throws SQLException
/*     */   {
/* 122 */     String group = props.getProperty("loadBalanceConnectionGroup", null);
/* 123 */     boolean enableJMX = false;
/* 124 */     String enableJMXAsString = props.getProperty("loadBalanceEnableJMX", "false");
/*     */     try {
/* 126 */       enableJMX = Boolean.parseBoolean(enableJMXAsString);
/*     */     } catch (Exception e) {
/* 128 */       throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.badValueForLoadBalanceEnableJMX", new Object[] { enableJMXAsString }), "S1009", null);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 133 */     if (group != null) {
/* 134 */       this.connectionGroup = ConnectionGroupManager.getConnectionGroupInstance(group);
/* 135 */       if (enableJMX) {
/* 136 */         ConnectionGroupManager.registerJmx();
/*     */       }
/* 138 */       this.connectionGroupProxyID = this.connectionGroup.registerConnectionProxy(this, hosts);
/* 139 */       hosts = new ArrayList(this.connectionGroup.getInitialHosts());
/*     */     }
/*     */     
/*     */ 
/* 143 */     int numHosts = initializeHostsSpecs(hosts, props);
/*     */     
/* 145 */     this.liveConnections = new HashMap(numHosts);
/* 146 */     this.hostsToListIndexMap = new HashMap(numHosts);
/* 147 */     for (int i = 0; i < numHosts; i++) {
/* 148 */       this.hostsToListIndexMap.put(this.hostList.get(i), Integer.valueOf(i));
/*     */     }
/* 150 */     this.connectionsToHostsMap = new HashMap(numHosts);
/* 151 */     this.responseTimes = new long[numHosts];
/*     */     
/* 153 */     String retriesAllDownAsString = this.localProps.getProperty("retriesAllDown", "120");
/*     */     try {
/* 155 */       this.retriesAllDown = Integer.parseInt(retriesAllDownAsString);
/*     */     } catch (NumberFormatException nfe) {
/* 157 */       throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.badValueForRetriesAllDown", new Object[] { retriesAllDownAsString }), "S1009", null);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 162 */     String blacklistTimeoutAsString = this.localProps.getProperty("loadBalanceBlacklistTimeout", "0");
/*     */     try {
/* 164 */       this.globalBlacklistTimeout = Integer.parseInt(blacklistTimeoutAsString);
/*     */     } catch (NumberFormatException nfe) {
/* 166 */       throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.badValueForLoadBalanceBlacklistTimeout", new Object[] { blacklistTimeoutAsString }), "S1009", null);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 171 */     String hostRemovalGracePeriodAsString = this.localProps.getProperty("loadBalanceHostRemovalGracePeriod", "15000");
/*     */     try {
/* 173 */       this.hostRemovalGracePeriod = Integer.parseInt(hostRemovalGracePeriodAsString);
/*     */     } catch (NumberFormatException nfe) {
/* 175 */       throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.badValueForLoadBalanceHostRemovalGracePeriod", new Object[] { hostRemovalGracePeriodAsString }), "S1009", null);
/*     */     }
/*     */     
/*     */ 
/* 179 */     String strategy = this.localProps.getProperty("loadBalanceStrategy", "random");
/* 180 */     if ("random".equals(strategy)) {
/* 181 */       this.balancer = ((BalanceStrategy)Util.loadExtensions(null, props, "com.mysql.jdbc.RandomBalanceStrategy", "InvalidLoadBalanceStrategy", null).get(0));
/*     */     }
/* 183 */     else if ("bestResponseTime".equals(strategy)) {
/* 184 */       this.balancer = ((BalanceStrategy)Util.loadExtensions(null, props, "com.mysql.jdbc.BestResponseTimeBalanceStrategy", "InvalidLoadBalanceStrategy", null).get(0));
/*     */     }
/*     */     else {
/* 187 */       this.balancer = ((BalanceStrategy)Util.loadExtensions(null, props, strategy, "InvalidLoadBalanceStrategy", null).get(0));
/*     */     }
/*     */     
/* 190 */     String autoCommitSwapThresholdAsString = props.getProperty("loadBalanceAutoCommitStatementThreshold", "0");
/*     */     try {
/* 192 */       this.autoCommitSwapThreshold = Integer.parseInt(autoCommitSwapThresholdAsString);
/*     */     } catch (NumberFormatException nfe) {
/* 194 */       throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.badValueForLoadBalanceAutoCommitStatementThreshold", new Object[] { autoCommitSwapThresholdAsString }), "S1009", null);
/*     */     }
/*     */     
/*     */ 
/* 198 */     String autoCommitSwapRegex = props.getProperty("loadBalanceAutoCommitStatementRegex", "");
/* 199 */     if (!"".equals(autoCommitSwapRegex)) {
/*     */       try {
/* 201 */         "".matches(autoCommitSwapRegex);
/*     */       } catch (Exception e) {
/* 203 */         throw SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.badValueForLoadBalanceAutoCommitStatementRegex", new Object[] { autoCommitSwapRegex }), "S1009", null);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 209 */     if (this.autoCommitSwapThreshold > 0) {
/* 210 */       String statementInterceptors = this.localProps.getProperty("statementInterceptors");
/* 211 */       if (statementInterceptors == null) {
/* 212 */         this.localProps.setProperty("statementInterceptors", "com.mysql.jdbc.LoadBalancedAutoCommitInterceptor");
/* 213 */       } else if (statementInterceptors.length() > 0) {
/* 214 */         this.localProps.setProperty("statementInterceptors", statementInterceptors + ",com.mysql.jdbc.LoadBalancedAutoCommitInterceptor");
/*     */       }
/* 216 */       props.setProperty("statementInterceptors", this.localProps.getProperty("statementInterceptors"));
/*     */     }
/*     */     
/*     */ 
/* 220 */     this.balancer.init(null, props);
/*     */     
/* 222 */     String lbExceptionChecker = this.localProps.getProperty("loadBalanceExceptionChecker", "com.mysql.jdbc.StandardLoadBalanceExceptionChecker");
/* 223 */     this.exceptionChecker = ((LoadBalanceExceptionChecker)Util.loadExtensions(null, props, lbExceptionChecker, "InvalidLoadBalanceExceptionChecker", null).get(0));
/*     */     
/*     */ 
/* 226 */     pickNewConnection();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   MySQLConnection getNewWrapperForThisAsConnection()
/*     */     throws SQLException
/*     */   {
/* 237 */     if ((Util.isJdbc4()) || (JDBC_4_LB_CONNECTION_CTOR != null)) {
/* 238 */       return (MySQLConnection)Util.handleNewInstance(JDBC_4_LB_CONNECTION_CTOR, new Object[] { this }, null);
/*     */     }
/* 240 */     return new LoadBalancedMySQLConnection(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void propagateProxyDown(MySQLConnection proxyConn)
/*     */   {
/* 251 */     for (MySQLConnection c : this.liveConnections.values()) {
/* 252 */       c.setProxy(proxyConn);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   boolean shouldExceptionTriggerConnectionSwitch(Throwable t)
/*     */   {
/* 264 */     return ((t instanceof SQLException)) && (this.exceptionChecker.shouldExceptionTriggerFailover((SQLException)t));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   boolean isMasterConnection()
/*     */   {
/* 272 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   synchronized void invalidateConnection(MySQLConnection conn)
/*     */     throws SQLException
/*     */   {
/* 283 */     super.invalidateConnection(conn);
/*     */     
/*     */ 
/* 286 */     if (isGlobalBlacklistEnabled()) {
/* 287 */       addToGlobalBlacklist((String)this.connectionsToHostsMap.get(conn));
/*     */     }
/*     */     
/*     */ 
/* 291 */     this.liveConnections.remove(this.connectionsToHostsMap.get(conn));
/* 292 */     Object mappedHost = this.connectionsToHostsMap.remove(conn);
/* 293 */     if ((mappedHost != null) && (this.hostsToListIndexMap.containsKey(mappedHost))) {
/* 294 */       int hostIndex = ((Integer)this.hostsToListIndexMap.get(mappedHost)).intValue();
/*     */       
/* 296 */       synchronized (this.responseTimes) {
/* 297 */         this.responseTimes[hostIndex] = 0L;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   synchronized void pickNewConnection()
/*     */     throws SQLException
/*     */   {
/* 309 */     if ((this.isClosed) && (this.closedExplicitly)) {
/* 310 */       return;
/*     */     }
/*     */     
/* 313 */     if (this.currentConnection == null) {
/* 314 */       this.currentConnection = this.balancer.pickConnection(this, Collections.unmodifiableList(this.hostList), Collections.unmodifiableMap(this.liveConnections), (long[])this.responseTimes.clone(), this.retriesAllDown);
/*     */       
/* 316 */       return;
/*     */     }
/*     */     
/* 319 */     if (this.currentConnection.isClosed()) {
/* 320 */       invalidateCurrentConnection();
/*     */     }
/*     */     
/* 323 */     int pingTimeout = this.currentConnection.getLoadBalancePingTimeout();
/* 324 */     boolean pingBeforeReturn = this.currentConnection.getLoadBalanceValidateConnectionOnSwapServer();
/*     */     
/* 326 */     int hostsTried = 0; for (int hostsToTry = this.hostList.size(); hostsTried < hostsToTry; hostsTried++) {
/* 327 */       ConnectionImpl newConn = null;
/*     */       try {
/* 329 */         newConn = this.balancer.pickConnection(this, Collections.unmodifiableList(this.hostList), Collections.unmodifiableMap(this.liveConnections), (long[])this.responseTimes.clone(), this.retriesAllDown);
/*     */         
/*     */ 
/* 332 */         if (this.currentConnection != null) {
/* 333 */           if (pingBeforeReturn) {
/* 334 */             if (pingTimeout == 0) {
/* 335 */               newConn.ping();
/*     */             } else {
/* 337 */               newConn.pingInternal(true, pingTimeout);
/*     */             }
/*     */           }
/*     */           
/* 341 */           syncSessionState(this.currentConnection, newConn);
/*     */         }
/*     */         
/* 344 */         this.currentConnection = newConn;
/* 345 */         return;
/*     */       }
/*     */       catch (SQLException e) {
/* 348 */         if ((shouldExceptionTriggerConnectionSwitch(e)) && (newConn != null))
/*     */         {
/* 350 */           invalidateConnection(newConn);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 356 */     this.isClosed = true;
/* 357 */     this.closedReason = "Connection closed after inability to pick valid new connection during load-balance.";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized ConnectionImpl createConnectionForHost(String hostPortSpec)
/*     */     throws SQLException
/*     */   {
/* 369 */     ConnectionImpl conn = super.createConnectionForHost(hostPortSpec);
/*     */     
/* 371 */     this.liveConnections.put(hostPortSpec, conn);
/* 372 */     this.connectionsToHostsMap.put(conn, hostPortSpec);
/*     */     
/* 374 */     this.totalPhysicalConnections += 1L;
/*     */     
/* 376 */     return conn;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private synchronized void closeAllConnections()
/*     */   {
/* 384 */     for (MySQLConnection c : this.liveConnections.values()) {
/*     */       try {
/* 386 */         c.close();
/*     */       }
/*     */       catch (SQLException e) {}
/*     */     }
/*     */     
/* 391 */     if (!this.isClosed) {
/* 392 */       this.balancer.destroy();
/* 393 */       if (this.connectionGroup != null) {
/* 394 */         this.connectionGroup.closeConnectionProxy(this);
/*     */       }
/*     */     }
/*     */     
/* 398 */     this.liveConnections.clear();
/* 399 */     this.connectionsToHostsMap.clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   synchronized void doClose()
/*     */   {
/* 407 */     closeAllConnections();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   synchronized void doAbortInternal()
/*     */   {
/* 416 */     for (MySQLConnection c : this.liveConnections.values()) {
/*     */       try {
/* 418 */         c.abortInternal();
/*     */       }
/*     */       catch (SQLException e) {}
/*     */     }
/*     */     
/* 423 */     if (!this.isClosed) {
/* 424 */       this.balancer.destroy();
/* 425 */       if (this.connectionGroup != null) {
/* 426 */         this.connectionGroup.closeConnectionProxy(this);
/*     */       }
/*     */     }
/*     */     
/* 430 */     this.liveConnections.clear();
/* 431 */     this.connectionsToHostsMap.clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   synchronized void doAbort(Executor executor)
/*     */   {
/* 440 */     for (MySQLConnection c : this.liveConnections.values()) {
/*     */       try {
/* 442 */         c.abort(executor);
/*     */       }
/*     */       catch (SQLException e) {}
/*     */     }
/*     */     
/* 447 */     if (!this.isClosed) {
/* 448 */       this.balancer.destroy();
/* 449 */       if (this.connectionGroup != null) {
/* 450 */         this.connectionGroup.closeConnectionProxy(this);
/*     */       }
/*     */     }
/*     */     
/* 454 */     this.liveConnections.clear();
/* 455 */     this.connectionsToHostsMap.clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized Object invokeMore(Object proxy, Method method, Object[] args)
/*     */     throws Throwable
/*     */   {
/* 465 */     String methodName = method.getName();
/*     */     
/* 467 */     if ((this.isClosed) && (!allowedOnClosedConnection(method)) && (method.getExceptionTypes().length > 0)) {
/* 468 */       if ((this.autoReconnect) && (!this.closedExplicitly))
/*     */       {
/* 470 */         this.currentConnection = null;
/* 471 */         pickNewConnection();
/* 472 */         this.isClosed = false;
/* 473 */         this.closedReason = null;
/*     */       } else {
/* 475 */         String reason = "No operations allowed after connection closed.";
/* 476 */         if (this.closedReason != null) {
/* 477 */           reason = reason + " " + this.closedReason;
/*     */         }
/* 479 */         throw SQLError.createSQLException(reason, "08003", null);
/*     */       }
/*     */     }
/*     */     
/* 483 */     if (!this.inTransaction) {
/* 484 */       this.inTransaction = true;
/* 485 */       this.transactionStartTime = System.nanoTime();
/* 486 */       this.transactionCount += 1L;
/*     */     }
/*     */     
/* 489 */     Object result = null;
/*     */     try
/*     */     {
/* 492 */       result = method.invoke(this.thisAsConnection, args);
/*     */       
/* 494 */       if (result != null) {
/* 495 */         if ((result instanceof Statement)) {
/* 496 */           ((Statement)result).setPingTarget(this);
/*     */         }
/* 498 */         result = proxyIfReturnTypeIsJdbcInterface(method.getReturnType(), result);
/*     */       }
/*     */     }
/*     */     catch (InvocationTargetException e) {
/* 502 */       dealWithInvocationException(e);
/*     */     }
/*     */     finally {
/* 505 */       if (("commit".equals(methodName)) || ("rollback".equals(methodName))) {
/* 506 */         this.inTransaction = false;
/*     */         
/*     */ 
/* 509 */         String host = (String)this.connectionsToHostsMap.get(this.currentConnection);
/*     */         
/* 511 */         if (host != null) {
/* 512 */           synchronized (this.responseTimes) {
/* 513 */             Integer hostIndex = (Integer)this.hostsToListIndexMap.get(host);
/*     */             
/* 515 */             if ((hostIndex != null) && (hostIndex.intValue() < this.responseTimes.length)) {
/* 516 */               this.responseTimes[hostIndex.intValue()] = (System.nanoTime() - this.transactionStartTime);
/*     */             }
/*     */           }
/*     */         }
/* 520 */         pickNewConnection();
/*     */       }
/*     */     }
/*     */     
/* 524 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void doPing()
/*     */     throws SQLException
/*     */   {
/* 531 */     SQLException se = null;
/* 532 */     boolean foundHost = false;
/* 533 */     int pingTimeout = this.currentConnection.getLoadBalancePingTimeout();
/*     */     
/* 535 */     for (Iterator<String> i = this.hostList.iterator(); i.hasNext();) {
/* 536 */       String host = (String)i.next();
/* 537 */       ConnectionImpl conn = (ConnectionImpl)this.liveConnections.get(host);
/* 538 */       if (conn != null)
/*     */       {
/*     */         try
/*     */         {
/* 542 */           if (pingTimeout == 0) {
/* 543 */             conn.ping();
/*     */           } else {
/* 545 */             conn.pingInternal(true, pingTimeout);
/*     */           }
/* 547 */           foundHost = true;
/*     */         }
/*     */         catch (SQLException e) {
/* 550 */           if (host.equals(this.connectionsToHostsMap.get(this.currentConnection)))
/*     */           {
/* 552 */             closeAllConnections();
/* 553 */             this.isClosed = true;
/* 554 */             this.closedReason = "Connection closed because ping of current connection failed.";
/* 555 */             throw e;
/*     */           }
/*     */           
/*     */ 
/* 559 */           if (e.getMessage().equals(Messages.getString("Connection.exceededConnectionLifetime")))
/*     */           {
/* 561 */             if (se == null) {
/* 562 */               se = e;
/*     */             }
/*     */           }
/*     */           else {
/* 566 */             se = e;
/* 567 */             if (isGlobalBlacklistEnabled()) {
/* 568 */               addToGlobalBlacklist(host);
/*     */             }
/*     */           }
/*     */           
/* 572 */           this.liveConnections.remove(this.connectionsToHostsMap.get(conn));
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 577 */     if (!foundHost) {
/* 578 */       closeAllConnections();
/* 579 */       this.isClosed = true;
/* 580 */       this.closedReason = "Connection closed due to inability to ping any active connections.";
/*     */       
/* 582 */       if (se != null) {
/* 583 */         throw se;
/*     */       }
/*     */       
/* 586 */       ((ConnectionImpl)this.currentConnection).throwConnectionClosedException();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addToGlobalBlacklist(String host, long timeout)
/*     */   {
/* 599 */     if (isGlobalBlacklistEnabled()) {
/* 600 */       synchronized (globalBlacklist) {
/* 601 */         globalBlacklist.put(host, Long.valueOf(timeout));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addToGlobalBlacklist(String host)
/*     */   {
/* 613 */     addToGlobalBlacklist(host, System.currentTimeMillis() + this.globalBlacklistTimeout);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isGlobalBlacklistEnabled()
/*     */   {
/* 620 */     return this.globalBlacklistTimeout > 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized Map<String, Long> getGlobalBlacklist()
/*     */   {
/* 630 */     if (!isGlobalBlacklistEnabled()) {
/* 631 */       if (this.hostsToRemove.isEmpty()) {
/* 632 */         return new HashMap(1);
/*     */       }
/* 634 */       HashMap<String, Long> fakedBlacklist = new HashMap();
/* 635 */       for (String h : this.hostsToRemove) {
/* 636 */         fakedBlacklist.put(h, Long.valueOf(System.currentTimeMillis() + 5000L));
/*     */       }
/* 638 */       return fakedBlacklist;
/*     */     }
/*     */     
/*     */ 
/* 642 */     Map<String, Long> blacklistClone = new HashMap(globalBlacklist.size());
/*     */     
/* 644 */     synchronized (globalBlacklist) {
/* 645 */       blacklistClone.putAll(globalBlacklist);
/*     */     }
/* 647 */     Set<String> keys = blacklistClone.keySet();
/*     */     
/*     */ 
/* 650 */     keys.retainAll(this.hostList);
/*     */     
/*     */ 
/* 653 */     for (Iterator<String> i = keys.iterator(); i.hasNext();) {
/* 654 */       String host = (String)i.next();
/*     */       
/* 656 */       Long timeout = (Long)globalBlacklist.get(host);
/* 657 */       if ((timeout != null) && (timeout.longValue() < System.currentTimeMillis()))
/*     */       {
/* 659 */         synchronized (globalBlacklist) {
/* 660 */           globalBlacklist.remove(host);
/*     */         }
/* 662 */         i.remove();
/*     */       }
/*     */     }
/*     */     
/* 666 */     if (keys.size() == this.hostList.size())
/*     */     {
/*     */ 
/* 669 */       return new HashMap(1);
/*     */     }
/*     */     
/* 672 */     return blacklistClone;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeHostWhenNotInUse(String hostPortPair)
/*     */     throws SQLException
/*     */   {
/* 683 */     if (this.hostRemovalGracePeriod <= 0) {
/* 684 */       removeHost(hostPortPair);
/* 685 */       return;
/*     */     }
/*     */     
/* 688 */     int timeBetweenChecks = this.hostRemovalGracePeriod > 1000 ? 1000 : this.hostRemovalGracePeriod;
/*     */     
/* 690 */     synchronized (this) {
/* 691 */       addToGlobalBlacklist(hostPortPair, System.currentTimeMillis() + this.hostRemovalGracePeriod + timeBetweenChecks);
/*     */       
/* 693 */       long cur = System.currentTimeMillis();
/*     */       
/* 695 */       while (System.currentTimeMillis() < cur + this.hostRemovalGracePeriod) {
/* 696 */         this.hostsToRemove.add(hostPortPair);
/*     */         
/* 698 */         if (!hostPortPair.equals(this.currentConnection.getHostPortPair())) {
/* 699 */           removeHost(hostPortPair);
/* 700 */           return;
/*     */         }
/*     */         try
/*     */         {
/* 704 */           Thread.sleep(timeBetweenChecks);
/*     */         }
/*     */         catch (InterruptedException e) {}
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 711 */     removeHost(hostPortPair);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void removeHost(String hostPortPair)
/*     */     throws SQLException
/*     */   {
/* 722 */     if ((this.connectionGroup != null) && 
/* 723 */       (this.connectionGroup.getInitialHosts().size() == 1) && (this.connectionGroup.getInitialHosts().contains(hostPortPair))) {
/* 724 */       throw SQLError.createSQLException("Cannot remove only configured host.", null);
/*     */     }
/*     */     
/*     */ 
/* 728 */     this.hostsToRemove.add(hostPortPair);
/*     */     
/* 730 */     this.connectionsToHostsMap.remove(this.liveConnections.remove(hostPortPair));
/* 731 */     if (this.hostsToListIndexMap.remove(hostPortPair) != null) {
/* 732 */       long[] newResponseTimes = new long[this.responseTimes.length - 1];
/* 733 */       int newIdx = 0;
/* 734 */       for (String h : this.hostList) {
/* 735 */         if (!this.hostsToRemove.contains(h)) {
/* 736 */           Integer idx = (Integer)this.hostsToListIndexMap.get(h);
/* 737 */           if ((idx != null) && (idx.intValue() < this.responseTimes.length)) {
/* 738 */             newResponseTimes[newIdx] = this.responseTimes[idx.intValue()];
/*     */           }
/* 740 */           this.hostsToListIndexMap.put(h, Integer.valueOf(newIdx++));
/*     */         }
/*     */       }
/* 743 */       this.responseTimes = newResponseTimes;
/*     */     }
/*     */     
/* 746 */     if (hostPortPair.equals(this.currentConnection.getHostPortPair())) {
/* 747 */       invalidateConnection(this.currentConnection);
/* 748 */       pickNewConnection();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized boolean addHost(String hostPortPair)
/*     */   {
/* 759 */     if (this.hostsToListIndexMap.containsKey(hostPortPair)) {
/* 760 */       return false;
/*     */     }
/*     */     
/* 763 */     long[] newResponseTimes = new long[this.responseTimes.length + 1];
/* 764 */     System.arraycopy(this.responseTimes, 0, newResponseTimes, 0, this.responseTimes.length);
/*     */     
/* 766 */     this.responseTimes = newResponseTimes;
/* 767 */     if (!this.hostList.contains(hostPortPair)) {
/* 768 */       this.hostList.add(hostPortPair);
/*     */     }
/* 770 */     this.hostsToListIndexMap.put(hostPortPair, Integer.valueOf(this.responseTimes.length - 1));
/* 771 */     this.hostsToRemove.remove(hostPortPair);
/*     */     
/* 773 */     return true;
/*     */   }
/*     */   
/*     */   public synchronized boolean inTransaction() {
/* 777 */     return this.inTransaction;
/*     */   }
/*     */   
/*     */   public synchronized long getTransactionCount() {
/* 781 */     return this.transactionCount;
/*     */   }
/*     */   
/*     */   public synchronized long getActivePhysicalConnectionCount() {
/* 785 */     return this.liveConnections.size();
/*     */   }
/*     */   
/*     */   public synchronized long getTotalPhysicalConnectionCount() {
/* 789 */     return this.totalPhysicalConnections;
/*     */   }
/*     */   
/*     */   public synchronized long getConnectionGroupProxyID() {
/* 793 */     return this.connectionGroupProxyID;
/*     */   }
/*     */   
/*     */   public synchronized String getCurrentActiveHost() {
/* 797 */     MySQLConnection c = this.currentConnection;
/* 798 */     if (c != null) {
/* 799 */       Object o = this.connectionsToHostsMap.get(c);
/* 800 */       if (o != null) {
/* 801 */         return o.toString();
/*     */       }
/*     */     }
/* 804 */     return null;
/*     */   }
/*     */   
/*     */   public synchronized long getCurrentTransactionDuration() {
/* 808 */     if ((this.inTransaction) && (this.transactionStartTime > 0L)) {
/* 809 */       return System.nanoTime() - this.transactionStartTime;
/*     */     }
/* 811 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class NullLoadBalancedConnectionProxy
/*     */     implements InvocationHandler
/*     */   {
/*     */     public Object invoke(Object proxy, Method method, Object[] args)
/*     */       throws Throwable
/*     */     {
/* 823 */       SQLException exceptionToThrow = SQLError.createSQLException(Messages.getString("LoadBalancedConnectionProxy.unusableConnection"), "25000", 1000001, true, null);
/*     */       
/* 825 */       Class<?>[] declaredException = method.getExceptionTypes();
/* 826 */       for (Class<?> declEx : declaredException) {
/* 827 */         if (declEx.isAssignableFrom(exceptionToThrow.getClass())) {
/* 828 */           throw exceptionToThrow;
/*     */         }
/*     */       }
/* 831 */       throw new IllegalStateException(exceptionToThrow.getMessage(), exceptionToThrow);
/*     */     }
/*     */   }
/*     */   
/* 835 */   private static LoadBalancedConnection nullLBConnectionInstance = null;
/*     */   
/*     */   static synchronized LoadBalancedConnection getNullLoadBalancedConnectionInstance() {
/* 838 */     if (nullLBConnectionInstance == null) {
/* 839 */       nullLBConnectionInstance = (LoadBalancedConnection)Proxy.newProxyInstance(LoadBalancedConnection.class.getClassLoader(), INTERFACES_TO_PROXY, new NullLoadBalancedConnectionProxy());
/*     */     }
/*     */     
/* 842 */     return nullLBConnectionInstance;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\LoadBalancedConnectionProxy.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */