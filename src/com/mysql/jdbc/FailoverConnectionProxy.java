/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import com.mysql.jdbc.log.Log;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.SQLException;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
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
/*     */ public class FailoverConnectionProxy
/*     */   extends MultiHostConnectionProxy
/*     */ {
/*     */   private static final String METHOD_SET_READ_ONLY = "setReadOnly";
/*     */   private static final String METHOD_SET_AUTO_COMMIT = "setAutoCommit";
/*     */   private static final String METHOD_COMMIT = "commit";
/*     */   private static final String METHOD_ROLLBACK = "rollback";
/*     */   private static final int NO_CONNECTION_INDEX = -1;
/*     */   private static final int DEFAULT_PRIMARY_HOST_INDEX = 0;
/*     */   private int secondsBeforeRetryPrimaryHost;
/*     */   private long queriesBeforeRetryPrimaryHost;
/*     */   private boolean failoverReadOnly;
/*     */   private int retriesAllDown;
/*  52 */   private int currentHostIndex = -1;
/*  53 */   private int primaryHostIndex = 0;
/*  54 */   private Boolean explicitlyReadOnly = null;
/*  55 */   private boolean explicitlyAutoCommit = true;
/*     */   
/*  57 */   private boolean enableFallBackToPrimaryHost = true;
/*  58 */   private long primaryHostFailTimeMillis = 0L;
/*  59 */   private long queriesIssuedSinceFailover = 0L;
/*     */   private static Class<?>[] INTERFACES_TO_PROXY;
/*     */   
/*     */   static
/*     */   {
/*  64 */     if (Util.isJdbc4()) {
/*     */       try {
/*  66 */         INTERFACES_TO_PROXY = new Class[] { Class.forName("com.mysql.jdbc.JDBC4MySQLConnection") };
/*     */       } catch (ClassNotFoundException e) {
/*  68 */         throw new RuntimeException(e);
/*     */       }
/*     */     } else {
/*  71 */       INTERFACES_TO_PROXY = new Class[] { MySQLConnection.class };
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   class FailoverJdbcInterfaceProxy
/*     */     extends MultiHostConnectionProxy.JdbcInterfaceProxy
/*     */   {
/*     */     FailoverJdbcInterfaceProxy(Object toInvokeOn)
/*     */     {
/*  81 */       super(toInvokeOn);
/*     */     }
/*     */     
/*     */     public Object invoke(Object proxy, Method method, Object[] args)
/*     */       throws Throwable
/*     */     {
/*  87 */       String methodName = method.getName();
/*     */       
/*  89 */       boolean isExecute = methodName.startsWith("execute");
/*     */       
/*  91 */       if ((FailoverConnectionProxy.this.connectedToSecondaryHost()) && (isExecute)) {
/*  92 */         FailoverConnectionProxy.this.incrementQueriesIssuedSinceFailover();
/*     */       }
/*     */       
/*  95 */       Object result = super.invoke(proxy, method, args);
/*     */       
/*  97 */       if ((FailoverConnectionProxy.this.explicitlyAutoCommit) && (isExecute) && (FailoverConnectionProxy.this.readyToFallBackToPrimaryHost()))
/*     */       {
/*  99 */         FailoverConnectionProxy.this.fallBackToPrimaryIfAvailable();
/*     */       }
/*     */       
/* 102 */       return result;
/*     */     }
/*     */   }
/*     */   
/*     */   public static Connection createProxyInstance(List<String> hosts, Properties props) throws SQLException {
/* 107 */     FailoverConnectionProxy connProxy = new FailoverConnectionProxy(hosts, props);
/*     */     
/* 109 */     return (Connection)Proxy.newProxyInstance(Connection.class.getClassLoader(), INTERFACES_TO_PROXY, connProxy);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private FailoverConnectionProxy(List<String> hosts, Properties props)
/*     */     throws SQLException
/*     */   {
/* 121 */     super(hosts, props);
/*     */     
/* 123 */     ConnectionPropertiesImpl connProps = new ConnectionPropertiesImpl();
/* 124 */     connProps.initializeProperties(props);
/*     */     
/* 126 */     this.secondsBeforeRetryPrimaryHost = connProps.getSecondsBeforeRetryMaster();
/* 127 */     this.queriesBeforeRetryPrimaryHost = connProps.getQueriesBeforeRetryMaster();
/* 128 */     this.failoverReadOnly = connProps.getFailOverReadOnly();
/* 129 */     this.retriesAllDown = connProps.getRetriesAllDown();
/*     */     
/* 131 */     this.enableFallBackToPrimaryHost = ((this.secondsBeforeRetryPrimaryHost > 0) || (this.queriesBeforeRetryPrimaryHost > 0L));
/*     */     
/* 133 */     pickNewConnection();
/*     */     
/* 135 */     this.explicitlyAutoCommit = this.currentConnection.getAutoCommit();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   MultiHostConnectionProxy.JdbcInterfaceProxy getNewJdbcInterfaceProxy(Object toProxy)
/*     */   {
/* 145 */     return new FailoverJdbcInterfaceProxy(toProxy);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   boolean shouldExceptionTriggerConnectionSwitch(Throwable t)
/*     */   {
/* 155 */     if (!(t instanceof SQLException)) {
/* 156 */       return false;
/*     */     }
/*     */     
/* 159 */     String sqlState = ((SQLException)t).getSQLState();
/* 160 */     if ((sqlState != null) && 
/* 161 */       (sqlState.startsWith("08")))
/*     */     {
/* 163 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 168 */     if ((t instanceof CommunicationsException)) {
/* 169 */       return true;
/*     */     }
/*     */     
/* 172 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   boolean isMasterConnection()
/*     */   {
/* 180 */     return connectedToPrimaryHost();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   synchronized void pickNewConnection()
/*     */     throws SQLException
/*     */   {
/* 190 */     if ((this.isClosed) && (this.closedExplicitly)) {
/* 191 */       return;
/*     */     }
/*     */     
/* 194 */     if ((!isConnected()) || (readyToFallBackToPrimaryHost())) {
/*     */       try {
/* 196 */         connectTo(this.primaryHostIndex);
/*     */       } catch (SQLException e) {
/* 198 */         resetAutoFallBackCounters();
/* 199 */         failOver(this.primaryHostIndex);
/*     */       }
/*     */     } else {
/* 202 */       failOver();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   synchronized ConnectionImpl createConnectionForHostIndex(int hostIndex)
/*     */     throws SQLException
/*     */   {
/* 215 */     return createConnectionForHost((String)this.hostList.get(hostIndex));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private synchronized void connectTo(int hostIndex)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 226 */       switchCurrentConnectionTo(hostIndex, createConnectionForHostIndex(hostIndex));
/*     */     } catch (SQLException e) {
/* 228 */       if (this.currentConnection != null) {
/* 229 */         StringBuilder msg = new StringBuilder("Connection to ").append(isPrimaryHostIndex(hostIndex) ? "primary" : "secondary").append(" host '").append((String)this.hostList.get(hostIndex)).append("' failed");
/*     */         
/* 231 */         this.currentConnection.getLog().logWarn(msg.toString(), e);
/*     */       }
/* 233 */       throw e;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private synchronized void switchCurrentConnectionTo(int hostIndex, MySQLConnection connection)
/*     */     throws SQLException
/*     */   {
/* 246 */     invalidateCurrentConnection();
/*     */     boolean readOnly;
/*     */     boolean readOnly;
/* 249 */     if (isPrimaryHostIndex(hostIndex)) {
/* 250 */       readOnly = this.explicitlyReadOnly == null ? false : this.explicitlyReadOnly.booleanValue(); } else { boolean readOnly;
/* 251 */       if (this.failoverReadOnly) {
/* 252 */         readOnly = true; } else { boolean readOnly;
/* 253 */         if (this.explicitlyReadOnly != null) {
/* 254 */           readOnly = this.explicitlyReadOnly.booleanValue(); } else { boolean readOnly;
/* 255 */           if (this.currentConnection != null) {
/* 256 */             readOnly = this.currentConnection.isReadOnly();
/*     */           } else
/* 258 */             readOnly = false;
/*     */         } } }
/* 260 */     syncSessionState(this.currentConnection, connection, readOnly);
/* 261 */     this.currentConnection = connection;
/* 262 */     this.currentHostIndex = hostIndex;
/*     */   }
/*     */   
/*     */ 
/*     */   private synchronized void failOver()
/*     */     throws SQLException
/*     */   {
/* 269 */     failOver(this.currentHostIndex);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private synchronized void failOver(int failedHostIdx)
/*     */     throws SQLException
/*     */   {
/* 280 */     int prevHostIndex = this.currentHostIndex;
/* 281 */     int nextHostIndex = nextHost(failedHostIdx, false);
/* 282 */     int firstHostIndexTried = nextHostIndex;
/*     */     
/* 284 */     SQLException lastExceptionCaught = null;
/* 285 */     int attempts = 0;
/* 286 */     boolean gotConnection = false;
/* 287 */     boolean firstConnOrPassedByPrimaryHost = (prevHostIndex == -1) || (isPrimaryHostIndex(prevHostIndex));
/*     */     do {
/*     */       try {
/* 290 */         firstConnOrPassedByPrimaryHost = (firstConnOrPassedByPrimaryHost) || (isPrimaryHostIndex(nextHostIndex));
/*     */         
/* 292 */         connectTo(nextHostIndex);
/*     */         
/* 294 */         if ((firstConnOrPassedByPrimaryHost) && (connectedToSecondaryHost())) {
/* 295 */           resetAutoFallBackCounters();
/*     */         }
/* 297 */         gotConnection = true;
/*     */       }
/*     */       catch (SQLException e) {
/* 300 */         lastExceptionCaught = e;
/*     */         
/* 302 */         if (shouldExceptionTriggerConnectionSwitch(e)) {
/* 303 */           int newNextHostIndex = nextHost(nextHostIndex, attempts > 0);
/*     */           
/* 305 */           if ((newNextHostIndex == firstHostIndexTried) && (newNextHostIndex == (newNextHostIndex = nextHost(nextHostIndex, true)))) {
/* 306 */             attempts++;
/*     */             try
/*     */             {
/* 309 */               Thread.sleep(250L);
/*     */             }
/*     */             catch (InterruptedException ie) {}
/*     */           }
/*     */           
/* 314 */           nextHostIndex = newNextHostIndex;
/*     */         }
/*     */         else {
/* 317 */           throw e;
/*     */         }
/*     */       }
/* 320 */     } while ((attempts < this.retriesAllDown) && (!gotConnection));
/*     */     
/* 322 */     if (!gotConnection) {
/* 323 */       throw lastExceptionCaught;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   synchronized void fallBackToPrimaryIfAvailable()
/*     */   {
/* 331 */     MySQLConnection connection = null;
/*     */     try {
/* 333 */       connection = createConnectionForHostIndex(this.primaryHostIndex);
/* 334 */       switchCurrentConnectionTo(this.primaryHostIndex, connection);
/*     */     } catch (SQLException e1) {
/* 336 */       if (connection != null) {
/*     */         try {
/* 338 */           connection.close();
/*     */         }
/*     */         catch (SQLException e2) {}
/*     */       }
/*     */       
/* 343 */       resetAutoFallBackCounters();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int nextHost(int currHostIdx, boolean vouchForPrimaryHost)
/*     */   {
/* 360 */     int nextHostIdx = (currHostIdx + 1) % this.hostList.size();
/* 361 */     if ((isPrimaryHostIndex(nextHostIdx)) && (isConnected()) && (!vouchForPrimaryHost) && (this.enableFallBackToPrimaryHost) && (!readyToFallBackToPrimaryHost()))
/*     */     {
/* 363 */       nextHostIdx = nextHost(nextHostIdx, vouchForPrimaryHost);
/*     */     }
/* 365 */     return nextHostIdx;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   synchronized void incrementQueriesIssuedSinceFailover()
/*     */   {
/* 372 */     this.queriesIssuedSinceFailover += 1L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   synchronized boolean readyToFallBackToPrimaryHost()
/*     */   {
/* 380 */     return (this.enableFallBackToPrimaryHost) && (connectedToSecondaryHost()) && ((secondsBeforeRetryPrimaryHostIsMet()) || (queriesBeforeRetryPrimaryHostIsMet()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   synchronized boolean isConnected()
/*     */   {
/* 387 */     return this.currentHostIndex != -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   synchronized boolean isPrimaryHostIndex(int hostIndex)
/*     */   {
/* 397 */     return hostIndex == this.primaryHostIndex;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   synchronized boolean connectedToPrimaryHost()
/*     */   {
/* 404 */     return isPrimaryHostIndex(this.currentHostIndex);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   synchronized boolean connectedToSecondaryHost()
/*     */   {
/* 411 */     return (this.currentHostIndex >= 0) && (!isPrimaryHostIndex(this.currentHostIndex));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private synchronized boolean secondsBeforeRetryPrimaryHostIsMet()
/*     */   {
/* 418 */     return (this.secondsBeforeRetryPrimaryHost > 0) && (Util.secondsSinceMillis(this.primaryHostFailTimeMillis) >= this.secondsBeforeRetryPrimaryHost);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private synchronized boolean queriesBeforeRetryPrimaryHostIsMet()
/*     */   {
/* 425 */     return (this.queriesBeforeRetryPrimaryHost > 0L) && (this.queriesIssuedSinceFailover >= this.queriesBeforeRetryPrimaryHost);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private synchronized void resetAutoFallBackCounters()
/*     */   {
/* 432 */     this.primaryHostFailTimeMillis = System.currentTimeMillis();
/* 433 */     this.queriesIssuedSinceFailover = 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   synchronized void doClose()
/*     */     throws SQLException
/*     */   {
/* 441 */     this.currentConnection.close();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   synchronized void doAbortInternal()
/*     */     throws SQLException
/*     */   {
/* 449 */     this.currentConnection.abortInternal();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   synchronized void doAbort(Executor executor)
/*     */     throws SQLException
/*     */   {
/* 457 */     this.currentConnection.abort(executor);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized Object invokeMore(Object proxy, Method method, Object[] args)
/*     */     throws Throwable
/*     */   {
/* 466 */     String methodName = method.getName();
/*     */     
/* 468 */     if ("setReadOnly".equals(methodName)) {
/* 469 */       this.explicitlyReadOnly = ((Boolean)args[0]);
/* 470 */       if ((this.failoverReadOnly) && (connectedToSecondaryHost())) {
/* 471 */         return null;
/*     */       }
/*     */     }
/*     */     
/* 475 */     if ((this.isClosed) && (!allowedOnClosedConnection(method))) {
/* 476 */       if ((this.autoReconnect) && (!this.closedExplicitly)) {
/* 477 */         this.currentHostIndex = -1;
/* 478 */         pickNewConnection();
/* 479 */         this.isClosed = false;
/* 480 */         this.closedReason = null;
/*     */       } else {
/* 482 */         String reason = "No operations allowed after connection closed.";
/* 483 */         if (this.closedReason != null) {
/* 484 */           reason = reason + "  " + this.closedReason;
/*     */         }
/* 486 */         throw SQLError.createSQLException(reason, "08003", null);
/*     */       }
/*     */     }
/*     */     
/* 490 */     Object result = null;
/*     */     try
/*     */     {
/* 493 */       result = method.invoke(this.thisAsConnection, args);
/* 494 */       result = proxyIfReturnTypeIsJdbcInterface(method.getReturnType(), result);
/*     */     } catch (InvocationTargetException e) {
/* 496 */       dealWithInvocationException(e);
/*     */     }
/*     */     
/* 499 */     if ("setAutoCommit".equals(methodName)) {
/* 500 */       this.explicitlyAutoCommit = ((Boolean)args[0]).booleanValue();
/*     */     }
/*     */     
/* 503 */     if (((this.explicitlyAutoCommit) || ("commit".equals(methodName)) || ("rollback".equals(methodName))) && (readyToFallBackToPrimaryHost()))
/*     */     {
/* 505 */       fallBackToPrimaryIfAvailable();
/*     */     }
/*     */     
/* 508 */     return result;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\FailoverConnectionProxy.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */