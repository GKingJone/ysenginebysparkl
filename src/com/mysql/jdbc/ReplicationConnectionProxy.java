/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
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
/*     */ public class ReplicationConnectionProxy
/*     */   extends MultiHostConnectionProxy
/*     */   implements PingTarget
/*     */ {
/*     */   private ReplicationConnection thisAsReplicationConnection;
/*     */   private NonRegisteringDriver driver;
/*  44 */   protected boolean enableJMX = false;
/*  45 */   protected boolean allowMasterDownConnections = false;
/*  46 */   protected boolean allowSlaveDownConnections = false;
/*  47 */   protected boolean readFromMasterWhenNoSlaves = false;
/*  48 */   protected boolean readFromMasterWhenNoSlavesOriginal = false;
/*  49 */   protected boolean readOnly = false;
/*     */   
/*     */   ReplicationConnectionGroup connectionGroup;
/*  52 */   private long connectionGroupID = -1L;
/*     */   
/*     */   private List<String> masterHosts;
/*     */   
/*     */   private Properties masterProperties;
/*     */   protected LoadBalancedConnection masterConnection;
/*     */   private List<String> slaveHosts;
/*     */   private Properties slaveProperties;
/*     */   protected LoadBalancedConnection slavesConnection;
/*     */   private static Constructor<?> JDBC_4_REPL_CONNECTION_CTOR;
/*     */   private static Class<?>[] INTERFACES_TO_PROXY;
/*     */   
/*     */   static
/*     */   {
/*  66 */     if (Util.isJdbc4()) {
/*     */       try {
/*  68 */         JDBC_4_REPL_CONNECTION_CTOR = Class.forName("com.mysql.jdbc.JDBC4ReplicationMySQLConnection").getConstructor(new Class[] { ReplicationConnectionProxy.class });
/*     */         
/*  70 */         INTERFACES_TO_PROXY = new Class[] { ReplicationConnection.class, Class.forName("com.mysql.jdbc.JDBC4MySQLConnection") };
/*     */       } catch (SecurityException e) {
/*  72 */         throw new RuntimeException(e);
/*     */       } catch (NoSuchMethodException e) {
/*  74 */         throw new RuntimeException(e);
/*     */       } catch (ClassNotFoundException e) {
/*  76 */         throw new RuntimeException(e);
/*     */       }
/*     */     } else {
/*  79 */       INTERFACES_TO_PROXY = new Class[] { ReplicationConnection.class };
/*     */     }
/*     */   }
/*     */   
/*     */   public static ReplicationConnection createProxyInstance(List<String> masterHostList, Properties masterProperties, List<String> slaveHostList, Properties slaveProperties) throws SQLException
/*     */   {
/*  85 */     ReplicationConnectionProxy connProxy = new ReplicationConnectionProxy(masterHostList, masterProperties, slaveHostList, slaveProperties);
/*     */     
/*  87 */     return (ReplicationConnection)Proxy.newProxyInstance(ReplicationConnection.class.getClassLoader(), INTERFACES_TO_PROXY, connProxy);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private ReplicationConnectionProxy(List<String> masterHostList, Properties masterProperties, List<String> slaveHostList, Properties slaveProperties)
/*     */     throws SQLException
/*     */   {
/* 108 */     this.thisAsReplicationConnection = ((ReplicationConnection)this.thisAsConnection);
/*     */     
/* 110 */     String enableJMXAsString = masterProperties.getProperty("replicationEnableJMX", "false");
/*     */     try {
/* 112 */       this.enableJMX = Boolean.parseBoolean(enableJMXAsString);
/*     */     } catch (Exception e) {
/* 114 */       throw SQLError.createSQLException(Messages.getString("ReplicationConnectionProxy.badValueForReplicationEnableJMX", new Object[] { enableJMXAsString }), "S1009", null);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 119 */     String allowMasterDownConnectionsAsString = masterProperties.getProperty("allowMasterDownConnections", "false");
/*     */     try {
/* 121 */       this.allowMasterDownConnections = Boolean.parseBoolean(allowMasterDownConnectionsAsString);
/*     */     } catch (Exception e) {
/* 123 */       throw SQLError.createSQLException(Messages.getString("ReplicationConnectionProxy.badValueForAllowMasterDownConnections", new Object[] { allowMasterDownConnectionsAsString }), "S1009", null);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 128 */     String allowSlaveDownConnectionsAsString = masterProperties.getProperty("allowSlaveDownConnections", "false");
/*     */     try {
/* 130 */       this.allowSlaveDownConnections = Boolean.parseBoolean(allowSlaveDownConnectionsAsString);
/*     */     } catch (Exception e) {
/* 132 */       throw SQLError.createSQLException(Messages.getString("ReplicationConnectionProxy.badValueForAllowSlaveDownConnections", new Object[] { allowSlaveDownConnectionsAsString }), "S1009", null);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 137 */     String readFromMasterWhenNoSlavesAsString = masterProperties.getProperty("readFromMasterWhenNoSlaves");
/*     */     try {
/* 139 */       this.readFromMasterWhenNoSlavesOriginal = Boolean.parseBoolean(readFromMasterWhenNoSlavesAsString);
/*     */     }
/*     */     catch (Exception e) {
/* 142 */       throw SQLError.createSQLException(Messages.getString("ReplicationConnectionProxy.badValueForReadFromMasterWhenNoSlaves", new Object[] { readFromMasterWhenNoSlavesAsString }), "S1009", null);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 147 */     String group = masterProperties.getProperty("replicationConnectionGroup", null);
/* 148 */     if (group != null) {
/* 149 */       this.connectionGroup = ReplicationConnectionGroupManager.getConnectionGroupInstance(group);
/* 150 */       if (this.enableJMX) {
/* 151 */         ReplicationConnectionGroupManager.registerJmx();
/*     */       }
/* 153 */       this.connectionGroupID = this.connectionGroup.registerReplicationConnection(this.thisAsReplicationConnection, masterHostList, slaveHostList);
/*     */       
/* 155 */       this.slaveHosts = new ArrayList(this.connectionGroup.getSlaveHosts());
/* 156 */       this.masterHosts = new ArrayList(this.connectionGroup.getMasterHosts());
/*     */     } else {
/* 158 */       this.slaveHosts = new ArrayList(slaveHostList);
/* 159 */       this.masterHosts = new ArrayList(masterHostList);
/*     */     }
/*     */     
/* 162 */     this.driver = new NonRegisteringDriver();
/* 163 */     this.slaveProperties = slaveProperties;
/* 164 */     this.masterProperties = masterProperties;
/*     */     
/* 166 */     resetReadFromMasterWhenNoSlaves();
/*     */     
/*     */     try
/*     */     {
/* 170 */       initializeSlavesConnection();
/*     */     } catch (SQLException e) {
/* 172 */       if (!this.allowSlaveDownConnections) {
/* 173 */         if (this.connectionGroup != null) {
/* 174 */           this.connectionGroup.handleCloseConnection(this.thisAsReplicationConnection);
/*     */         }
/* 176 */         throw e;
/*     */       }
/*     */     }
/*     */     
/* 180 */     SQLException exCaught = null;
/*     */     try {
/* 182 */       this.currentConnection = initializeMasterConnection();
/*     */     } catch (SQLException e) {
/* 184 */       exCaught = e;
/*     */     }
/*     */     
/* 187 */     if (this.currentConnection == null) {
/* 188 */       if ((this.allowMasterDownConnections) && (this.slavesConnection != null))
/*     */       {
/* 190 */         this.readOnly = true;
/* 191 */         this.currentConnection = this.slavesConnection;
/*     */       } else {
/* 193 */         if (this.connectionGroup != null) {
/* 194 */           this.connectionGroup.handleCloseConnection(this.thisAsReplicationConnection);
/*     */         }
/* 196 */         if (exCaught != null) {
/* 197 */           throw exCaught;
/*     */         }
/* 199 */         throw SQLError.createSQLException(Messages.getString("ReplicationConnectionProxy.initializationWithEmptyHostsLists"), "S1009", null);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   MySQLConnection getNewWrapperForThisAsConnection()
/*     */     throws SQLException
/*     */   {
/* 213 */     if ((Util.isJdbc4()) || (JDBC_4_REPL_CONNECTION_CTOR != null)) {
/* 214 */       return (MySQLConnection)Util.handleNewInstance(JDBC_4_REPL_CONNECTION_CTOR, new Object[] { this }, null);
/*     */     }
/* 216 */     return new ReplicationMySQLConnection(this);
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
/* 227 */     if (this.masterConnection != null) {
/* 228 */       this.masterConnection.setProxy(proxyConn);
/*     */     }
/* 230 */     if (this.slavesConnection != null) {
/* 231 */       this.slavesConnection.setProxy(proxyConn);
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
/* 243 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isMasterConnection()
/*     */   {
/* 251 */     return (this.currentConnection != null) && (this.currentConnection == this.masterConnection);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isSlavesConnection()
/*     */   {
/* 258 */     return (this.currentConnection != null) && (this.currentConnection == this.slavesConnection);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void doClose()
/*     */     throws SQLException
/*     */   {
/* 268 */     if (this.masterConnection != null) {
/* 269 */       this.masterConnection.close();
/*     */     }
/* 271 */     if (this.slavesConnection != null) {
/* 272 */       this.slavesConnection.close();
/*     */     }
/*     */     
/* 275 */     if (this.connectionGroup != null) {
/* 276 */       this.connectionGroup.handleCloseConnection(this.thisAsReplicationConnection);
/*     */     }
/*     */   }
/*     */   
/*     */   void doAbortInternal() throws SQLException
/*     */   {
/* 282 */     this.masterConnection.abortInternal();
/* 283 */     this.slavesConnection.abortInternal();
/* 284 */     if (this.connectionGroup != null) {
/* 285 */       this.connectionGroup.handleCloseConnection(this.thisAsReplicationConnection);
/*     */     }
/*     */   }
/*     */   
/*     */   void doAbort(Executor executor) throws SQLException
/*     */   {
/* 291 */     this.masterConnection.abort(executor);
/* 292 */     this.slavesConnection.abort(executor);
/* 293 */     if (this.connectionGroup != null) {
/* 294 */       this.connectionGroup.handleCloseConnection(this.thisAsReplicationConnection);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   Object invokeMore(Object proxy, Method method, Object[] args)
/*     */     throws Throwable
/*     */   {
/* 304 */     checkConnectionCapabilityForMethod(method);
/*     */     
/* 306 */     boolean invokeAgain = false;
/*     */     for (;;) {
/*     */       try {
/* 309 */         Object result = method.invoke(this.thisAsConnection, args);
/* 310 */         if ((result != null) && ((result instanceof Statement))) {
/* 311 */           ((Statement)result).setPingTarget(this);
/*     */         }
/* 313 */         return result;
/*     */       } catch (InvocationTargetException e) {
/* 315 */         if (invokeAgain) {
/* 316 */           invokeAgain = false;
/* 317 */         } else if ((e.getCause() != null) && ((e.getCause() instanceof SQLException)) && (((SQLException)e.getCause()).getSQLState() == "25000") && (((SQLException)e.getCause()).getErrorCode() == 1000001))
/*     */         {
/*     */           try
/*     */           {
/*     */ 
/* 322 */             setReadOnly(this.readOnly);
/* 323 */             invokeAgain = true;
/*     */           }
/*     */           catch (SQLException sqlEx) {}
/*     */         }
/*     */         
/* 328 */         if (!invokeAgain) {
/* 329 */           throw e;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void checkConnectionCapabilityForMethod(Method method)
/*     */     throws Throwable
/*     */   {
/* 341 */     if ((this.masterHosts.isEmpty()) && (this.slaveHosts.isEmpty()) && (!ReplicationConnection.class.isAssignableFrom(method.getDeclaringClass()))) {
/* 342 */       throw SQLError.createSQLException(Messages.getString("ReplicationConnectionProxy.noHostsInconsistentState"), "25000", 1000002, true, null);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void doPing()
/*     */     throws SQLException
/*     */   {
/* 351 */     boolean isMasterConn = isMasterConnection();
/*     */     
/* 353 */     SQLException mastersPingException = null;
/* 354 */     SQLException slavesPingException = null;
/*     */     
/* 356 */     if (this.masterConnection != null) {
/*     */       try {
/* 358 */         this.masterConnection.ping();
/*     */       } catch (SQLException e) {
/* 360 */         mastersPingException = e;
/*     */       }
/*     */     } else {
/* 363 */       initializeMasterConnection();
/*     */     }
/*     */     
/* 366 */     if (this.slavesConnection != null) {
/*     */       try {
/* 368 */         this.slavesConnection.ping();
/*     */       } catch (SQLException e) {
/* 370 */         slavesPingException = e;
/*     */       }
/*     */     } else {
/*     */       try {
/* 374 */         initializeSlavesConnection();
/* 375 */         if (switchToSlavesConnectionIfNecessary()) {
/* 376 */           isMasterConn = false;
/*     */         }
/*     */       } catch (SQLException e) {
/* 379 */         if ((this.masterConnection == null) || (!this.readFromMasterWhenNoSlaves)) {
/* 380 */           throw e;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 385 */     if ((isMasterConn) && (mastersPingException != null))
/*     */     {
/* 387 */       if ((this.slavesConnection != null) && (slavesPingException == null)) {
/* 388 */         this.masterConnection = null;
/* 389 */         this.currentConnection = this.slavesConnection;
/* 390 */         this.readOnly = true;
/*     */       }
/* 392 */       throw mastersPingException;
/*     */     }
/* 394 */     if ((!isMasterConn) && ((slavesPingException != null) || (this.slavesConnection == null)))
/*     */     {
/* 396 */       if ((this.masterConnection != null) && (this.readFromMasterWhenNoSlaves) && (mastersPingException == null)) {
/* 397 */         this.slavesConnection = null;
/* 398 */         this.currentConnection = this.masterConnection;
/* 399 */         this.readOnly = true;
/* 400 */         this.currentConnection.setReadOnly(true);
/*     */       }
/* 402 */       if (slavesPingException != null) {
/* 403 */         throw slavesPingException;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private MySQLConnection initializeMasterConnection() throws SQLException {
/* 409 */     this.masterConnection = null;
/*     */     
/* 411 */     if (this.masterHosts.size() == 0) {
/* 412 */       return null;
/*     */     }
/*     */     
/* 415 */     LoadBalancedConnection newMasterConn = (LoadBalancedConnection)this.driver.connect(buildURL(this.masterHosts, this.masterProperties), this.masterProperties);
/*     */     
/* 417 */     newMasterConn.setProxy(getProxy());
/*     */     
/* 419 */     this.masterConnection = newMasterConn;
/* 420 */     return this.masterConnection;
/*     */   }
/*     */   
/*     */   private MySQLConnection initializeSlavesConnection() throws SQLException {
/* 424 */     this.slavesConnection = null;
/*     */     
/* 426 */     if (this.slaveHosts.size() == 0) {
/* 427 */       return null;
/*     */     }
/*     */     
/* 430 */     LoadBalancedConnection newSlavesConn = (LoadBalancedConnection)this.driver.connect(buildURL(this.slaveHosts, this.slaveProperties), this.slaveProperties);
/*     */     
/* 432 */     newSlavesConn.setProxy(getProxy());
/* 433 */     newSlavesConn.setReadOnly(true);
/*     */     
/* 435 */     this.slavesConnection = newSlavesConn;
/* 436 */     return this.slavesConnection;
/*     */   }
/*     */   
/*     */   private String buildURL(List<String> hosts, Properties props) {
/* 440 */     StringBuilder url = new StringBuilder("jdbc:mysql:loadbalance://");
/*     */     
/* 442 */     boolean firstHost = true;
/* 443 */     for (String host : hosts) {
/* 444 */       if (!firstHost) {
/* 445 */         url.append(',');
/*     */       }
/* 447 */       url.append(host);
/* 448 */       firstHost = false;
/*     */     }
/* 450 */     url.append("/");
/* 451 */     String masterDb = props.getProperty("DBNAME");
/* 452 */     if (masterDb != null) {
/* 453 */       url.append(masterDb);
/*     */     }
/*     */     
/* 456 */     return url.toString();
/*     */   }
/*     */   
/*     */   private synchronized boolean switchToMasterConnection() throws SQLException {
/* 460 */     if ((this.masterConnection == null) || (this.masterConnection.isClosed())) {
/*     */       try {
/* 462 */         if (initializeMasterConnection() == null) {
/* 463 */           return false;
/*     */         }
/*     */       } catch (SQLException e) {
/* 466 */         this.currentConnection = null;
/* 467 */         throw e;
/*     */       }
/*     */     }
/* 470 */     if ((!isMasterConnection()) && (this.masterConnection != null)) {
/* 471 */       syncSessionState(this.currentConnection, this.masterConnection, false);
/* 472 */       this.currentConnection = this.masterConnection;
/*     */     }
/* 474 */     return true;
/*     */   }
/*     */   
/*     */   private synchronized boolean switchToSlavesConnection() throws SQLException {
/* 478 */     if ((this.slavesConnection == null) || (this.slavesConnection.isClosed())) {
/*     */       try {
/* 480 */         if (initializeSlavesConnection() == null) {
/* 481 */           return false;
/*     */         }
/*     */       } catch (SQLException e) {
/* 484 */         this.currentConnection = null;
/* 485 */         throw e;
/*     */       }
/*     */     }
/* 488 */     if ((!isSlavesConnection()) && (this.slavesConnection != null)) {
/* 489 */       syncSessionState(this.currentConnection, this.slavesConnection, true);
/* 490 */       this.currentConnection = this.slavesConnection;
/*     */     }
/* 492 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean switchToSlavesConnectionIfNecessary()
/*     */     throws SQLException
/*     */   {
/* 501 */     if ((this.currentConnection == null) || ((isMasterConnection()) && ((this.readOnly) || ((this.masterHosts.isEmpty()) && (this.currentConnection.isClosed())))) || ((!isMasterConnection()) && (this.currentConnection.isClosed())))
/*     */     {
/* 503 */       return switchToSlavesConnection();
/*     */     }
/* 505 */     return false;
/*     */   }
/*     */   
/*     */   public synchronized Connection getCurrentConnection() {
/* 509 */     return this.currentConnection == null ? LoadBalancedConnectionProxy.getNullLoadBalancedConnectionInstance() : this.currentConnection;
/*     */   }
/*     */   
/*     */   public long getConnectionGroupId() {
/* 513 */     return this.connectionGroupID;
/*     */   }
/*     */   
/*     */   public synchronized Connection getMasterConnection() {
/* 517 */     return this.masterConnection;
/*     */   }
/*     */   
/*     */   public synchronized void promoteSlaveToMaster(String hostPortPair) throws SQLException {
/* 521 */     this.masterHosts.add(hostPortPair);
/* 522 */     removeSlave(hostPortPair);
/* 523 */     if (this.masterConnection != null) {
/* 524 */       this.masterConnection.addHost(hostPortPair);
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void removeMasterHost(String hostPortPair) throws SQLException {
/* 529 */     removeMasterHost(hostPortPair, true);
/*     */   }
/*     */   
/*     */   public synchronized void removeMasterHost(String hostPortPair, boolean waitUntilNotInUse) throws SQLException {
/* 533 */     removeMasterHost(hostPortPair, waitUntilNotInUse, false);
/*     */   }
/*     */   
/*     */   public synchronized void removeMasterHost(String hostPortPair, boolean waitUntilNotInUse, boolean isNowSlave) throws SQLException {
/* 537 */     if (isNowSlave) {
/* 538 */       this.slaveHosts.add(hostPortPair);
/* 539 */       resetReadFromMasterWhenNoSlaves();
/*     */     }
/* 541 */     this.masterHosts.remove(hostPortPair);
/*     */     
/*     */ 
/* 544 */     if ((this.masterConnection == null) || (this.masterConnection.isClosed())) {
/* 545 */       this.masterConnection = null;
/* 546 */       return;
/*     */     }
/*     */     
/* 549 */     if (waitUntilNotInUse) {
/* 550 */       this.masterConnection.removeHostWhenNotInUse(hostPortPair);
/*     */     } else {
/* 552 */       this.masterConnection.removeHost(hostPortPair);
/*     */     }
/*     */     
/*     */ 
/* 556 */     if (this.masterHosts.isEmpty()) {
/* 557 */       this.masterConnection.close();
/* 558 */       this.masterConnection = null;
/*     */       
/*     */ 
/* 561 */       switchToSlavesConnectionIfNecessary();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isHostMaster(String hostPortPair) {
/* 566 */     if (hostPortPair == null) {
/* 567 */       return false;
/*     */     }
/* 569 */     for (String masterHost : this.masterHosts) {
/* 570 */       if (masterHost.equalsIgnoreCase(hostPortPair)) {
/* 571 */         return true;
/*     */       }
/*     */     }
/* 574 */     return false;
/*     */   }
/*     */   
/*     */   public synchronized Connection getSlavesConnection() {
/* 578 */     return this.slavesConnection;
/*     */   }
/*     */   
/*     */   public synchronized void addSlaveHost(String hostPortPair) throws SQLException {
/* 582 */     if (isHostSlave(hostPortPair)) {
/* 583 */       return;
/*     */     }
/* 585 */     this.slaveHosts.add(hostPortPair);
/* 586 */     resetReadFromMasterWhenNoSlaves();
/* 587 */     if (this.slavesConnection == null) {
/* 588 */       initializeSlavesConnection();
/* 589 */       switchToSlavesConnectionIfNecessary();
/*     */     } else {
/* 591 */       this.slavesConnection.addHost(hostPortPair);
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void removeSlave(String hostPortPair) throws SQLException {
/* 596 */     removeSlave(hostPortPair, true);
/*     */   }
/*     */   
/*     */   public synchronized void removeSlave(String hostPortPair, boolean closeGently) throws SQLException {
/* 600 */     this.slaveHosts.remove(hostPortPair);
/* 601 */     resetReadFromMasterWhenNoSlaves();
/*     */     
/* 603 */     if ((this.slavesConnection == null) || (this.slavesConnection.isClosed())) {
/* 604 */       this.slavesConnection = null;
/* 605 */       return;
/*     */     }
/*     */     
/* 608 */     if (closeGently) {
/* 609 */       this.slavesConnection.removeHostWhenNotInUse(hostPortPair);
/*     */     } else {
/* 611 */       this.slavesConnection.removeHost(hostPortPair);
/*     */     }
/*     */     
/*     */ 
/* 615 */     if (this.slaveHosts.isEmpty()) {
/* 616 */       this.slavesConnection.close();
/* 617 */       this.slavesConnection = null;
/*     */       
/*     */ 
/* 620 */       switchToMasterConnection();
/* 621 */       if (isMasterConnection()) {
/* 622 */         this.currentConnection.setReadOnly(this.readOnly);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isHostSlave(String hostPortPair) {
/* 628 */     if (hostPortPair == null) {
/* 629 */       return false;
/*     */     }
/* 631 */     for (String test : this.slaveHosts) {
/* 632 */       if (test.equalsIgnoreCase(hostPortPair)) {
/* 633 */         return true;
/*     */       }
/*     */     }
/* 636 */     return false;
/*     */   }
/*     */   
/*     */   public synchronized void setReadOnly(boolean readOnly) throws SQLException
/*     */   {
/* 641 */     if (readOnly) {
/* 642 */       if ((!isSlavesConnection()) || (this.currentConnection.isClosed())) {
/* 643 */         boolean switched = true;
/* 644 */         SQLException exceptionCaught = null;
/*     */         try {
/* 646 */           switched = switchToSlavesConnection();
/*     */         } catch (SQLException e) {
/* 648 */           switched = false;
/* 649 */           exceptionCaught = e;
/*     */         }
/* 651 */         if ((!switched) && (this.readFromMasterWhenNoSlaves) && (switchToMasterConnection())) {
/* 652 */           exceptionCaught = null;
/*     */         }
/* 654 */         if (exceptionCaught != null) {
/* 655 */           throw exceptionCaught;
/*     */         }
/*     */       }
/*     */     }
/* 659 */     else if ((!isMasterConnection()) || (this.currentConnection.isClosed())) {
/* 660 */       boolean switched = true;
/* 661 */       SQLException exceptionCaught = null;
/*     */       try {
/* 663 */         switched = switchToMasterConnection();
/*     */       } catch (SQLException e) {
/* 665 */         switched = false;
/* 666 */         exceptionCaught = e;
/*     */       }
/* 668 */       if ((!switched) && (switchToSlavesConnectionIfNecessary())) {
/* 669 */         exceptionCaught = null;
/*     */       }
/* 671 */       if (exceptionCaught != null) {
/* 672 */         throw exceptionCaught;
/*     */       }
/*     */     }
/*     */     
/* 676 */     this.readOnly = readOnly;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 682 */     if ((this.readFromMasterWhenNoSlaves) && (isMasterConnection())) {
/* 683 */       this.currentConnection.setReadOnly(this.readOnly);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isReadOnly() throws SQLException {
/* 688 */     return (!isMasterConnection()) || (this.readOnly);
/*     */   }
/*     */   
/*     */   private void resetReadFromMasterWhenNoSlaves() {
/* 692 */     this.readFromMasterWhenNoSlaves = ((this.slaveHosts.isEmpty()) || (this.readFromMasterWhenNoSlavesOriginal));
/*     */   }
/*     */   
/*     */   void pickNewConnection()
/*     */     throws SQLException
/*     */   {}
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\ReplicationConnectionProxy.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */