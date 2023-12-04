/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationHandler;
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
/*     */ public abstract class MultiHostConnectionProxy
/*     */   implements InvocationHandler
/*     */ {
/*     */   private static final String METHOD_GET_MULTI_HOST_SAFE_PROXY = "getMultiHostSafeProxy";
/*     */   private static final String METHOD_EQUALS = "equals";
/*     */   private static final String METHOD_HASH_CODE = "hashCode";
/*     */   private static final String METHOD_CLOSE = "close";
/*     */   private static final String METHOD_ABORT_INTERNAL = "abortInternal";
/*     */   private static final String METHOD_ABORT = "abort";
/*     */   private static final String METHOD_IS_CLOSED = "isClosed";
/*     */   private static final String METHOD_GET_AUTO_COMMIT = "getAutoCommit";
/*     */   private static final String METHOD_GET_CATALOG = "getCatalog";
/*     */   private static final String METHOD_GET_TRANSACTION_ISOLATION = "getTransactionIsolation";
/*     */   private static final String METHOD_GET_SESSION_MAX_ROWS = "getSessionMaxRows";
/*     */   List<String> hostList;
/*     */   Properties localProps;
/*  56 */   boolean autoReconnect = false;
/*     */   
/*  58 */   MySQLConnection thisAsConnection = null;
/*  59 */   MySQLConnection proxyConnection = null;
/*     */   
/*  61 */   MySQLConnection currentConnection = null;
/*     */   
/*  63 */   boolean isClosed = false;
/*  64 */   boolean closedExplicitly = false;
/*  65 */   String closedReason = null;
/*     */   
/*     */ 
/*     */ 
/*  69 */   protected Throwable lastExceptionDealtWith = null;
/*     */   private static Constructor<?> JDBC_4_MS_CONNECTION_CTOR;
/*     */   
/*     */   static
/*     */   {
/*  74 */     if (Util.isJdbc4()) {
/*     */       try {
/*  76 */         JDBC_4_MS_CONNECTION_CTOR = Class.forName("com.mysql.jdbc.JDBC4MultiHostMySQLConnection").getConstructor(new Class[] { MultiHostConnectionProxy.class });
/*     */       }
/*     */       catch (SecurityException e) {
/*  79 */         throw new RuntimeException(e);
/*     */       } catch (NoSuchMethodException e) {
/*  81 */         throw new RuntimeException(e);
/*     */       } catch (ClassNotFoundException e) {
/*  83 */         throw new RuntimeException(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   class JdbcInterfaceProxy
/*     */     implements InvocationHandler
/*     */   {
/*  92 */     Object invokeOn = null;
/*     */     
/*     */     JdbcInterfaceProxy(Object toInvokeOn) {
/*  95 */       this.invokeOn = toInvokeOn;
/*     */     }
/*     */     
/*     */     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/*  99 */       synchronized (MultiHostConnectionProxy.this) {
/* 100 */         Object result = null;
/*     */         try
/*     */         {
/* 103 */           result = method.invoke(this.invokeOn, args);
/* 104 */           result = MultiHostConnectionProxy.this.proxyIfReturnTypeIsJdbcInterface(method.getReturnType(), result);
/*     */         } catch (InvocationTargetException e) {
/* 106 */           MultiHostConnectionProxy.this.dealWithInvocationException(e);
/*     */         }
/*     */         
/* 109 */         return result;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   MultiHostConnectionProxy()
/*     */     throws SQLException
/*     */   {
/* 121 */     this.thisAsConnection = getNewWrapperForThisAsConnection();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   MultiHostConnectionProxy(List<String> hosts, Properties props)
/*     */     throws SQLException
/*     */   {
/* 133 */     this();
/* 134 */     initializeHostsSpecs(hosts, props);
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
/*     */   int initializeHostsSpecs(List<String> hosts, Properties props)
/*     */   {
/* 149 */     this.autoReconnect = (("true".equalsIgnoreCase(props.getProperty("autoReconnect"))) || ("true".equalsIgnoreCase(props.getProperty("autoReconnectForPools"))));
/*     */     
/* 151 */     this.hostList = hosts;
/* 152 */     int numHosts = this.hostList.size();
/*     */     
/* 154 */     this.localProps = ((Properties)props.clone());
/* 155 */     this.localProps.remove("HOST");
/* 156 */     this.localProps.remove("PORT");
/*     */     
/* 158 */     for (int i = 0; i < numHosts; i++) {
/* 159 */       this.localProps.remove("HOST." + (i + 1));
/* 160 */       this.localProps.remove("PORT." + (i + 1));
/*     */     }
/*     */     
/* 163 */     this.localProps.remove("NUM_HOSTS");
/* 164 */     this.localProps.setProperty("useLocalSessionState", "true");
/*     */     
/* 166 */     return numHosts;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   MySQLConnection getNewWrapperForThisAsConnection()
/*     */     throws SQLException
/*     */   {
/* 176 */     if ((Util.isJdbc4()) || (JDBC_4_MS_CONNECTION_CTOR != null)) {
/* 177 */       return (MySQLConnection)Util.handleNewInstance(JDBC_4_MS_CONNECTION_CTOR, new Object[] { this }, null);
/*     */     }
/*     */     
/* 180 */     return new MultiHostMySQLConnection(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected MySQLConnection getProxy()
/*     */   {
/* 192 */     return this.proxyConnection != null ? this.proxyConnection : this.thisAsConnection;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final void setProxy(MySQLConnection proxyConn)
/*     */   {
/* 203 */     this.proxyConnection = proxyConn;
/* 204 */     propagateProxyDown(proxyConn);
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
/* 215 */     this.currentConnection.setProxy(proxyConn);
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
/*     */   Object proxyIfReturnTypeIsJdbcInterface(Class<?> returnType, Object toProxy)
/*     */   {
/* 229 */     if ((toProxy != null) && 
/* 230 */       (Util.isJdbcInterface(returnType))) {
/* 231 */       Class<?> toProxyClass = toProxy.getClass();
/* 232 */       return Proxy.newProxyInstance(toProxyClass.getClassLoader(), Util.getImplementedInterfaces(toProxyClass), getNewJdbcInterfaceProxy(toProxy));
/*     */     }
/*     */     
/* 235 */     return toProxy;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   InvocationHandler getNewJdbcInterfaceProxy(Object toProxy)
/*     */   {
/* 247 */     return new JdbcInterfaceProxy(toProxy);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void dealWithInvocationException(InvocationTargetException e)
/*     */     throws SQLException, Throwable, InvocationTargetException
/*     */   {
/* 257 */     Throwable t = e.getTargetException();
/*     */     
/* 259 */     if (t != null) {
/* 260 */       if ((this.lastExceptionDealtWith != t) && (shouldExceptionTriggerConnectionSwitch(t))) {
/* 261 */         invalidateCurrentConnection();
/* 262 */         pickNewConnection();
/* 263 */         this.lastExceptionDealtWith = t;
/*     */       }
/* 265 */       throw t;
/*     */     }
/* 267 */     throw e;
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
/*     */   synchronized void invalidateCurrentConnection()
/*     */     throws SQLException
/*     */   {
/* 287 */     invalidateConnection(this.currentConnection);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   synchronized void invalidateConnection(MySQLConnection conn)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 298 */       if ((conn != null) && (!conn.isClosed())) {
/* 299 */         conn.realClose(true, !conn.getAutoCommit(), true, null);
/*     */       }
/*     */     }
/*     */     catch (SQLException e) {}
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
/*     */   synchronized ConnectionImpl createConnectionForHost(String hostPortSpec)
/*     */     throws SQLException
/*     */   {
/* 320 */     Properties connProps = (Properties)this.localProps.clone();
/*     */     
/* 322 */     String[] hostPortPair = NonRegisteringDriver.parseHostPortPair(hostPortSpec);
/* 323 */     String hostName = hostPortPair[0];
/* 324 */     String portNumber = hostPortPair[1];
/* 325 */     String dbName = connProps.getProperty("DBNAME");
/*     */     
/* 327 */     if (hostName == null) {
/* 328 */       throw new SQLException("Could not find a hostname to start a connection to");
/*     */     }
/* 330 */     if (portNumber == null) {
/* 331 */       portNumber = "3306";
/*     */     }
/*     */     
/* 334 */     connProps.setProperty("HOST", hostName);
/* 335 */     connProps.setProperty("PORT", portNumber);
/* 336 */     connProps.setProperty("HOST.1", hostName);
/* 337 */     connProps.setProperty("PORT.1", portNumber);
/* 338 */     connProps.setProperty("NUM_HOSTS", "1");
/* 339 */     connProps.setProperty("roundRobinLoadBalance", "false");
/*     */     
/* 341 */     ConnectionImpl conn = (ConnectionImpl)ConnectionImpl.getInstance(hostName, Integer.parseInt(portNumber), connProps, dbName, "jdbc:mysql://" + hostName + ":" + portNumber + "/");
/*     */     
/*     */ 
/* 344 */     conn.setProxy(getProxy());
/*     */     
/* 346 */     return conn;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static void syncSessionState(Connection source, Connection target)
/*     */     throws SQLException
/*     */   {
/* 358 */     if ((source == null) || (target == null)) {
/* 359 */       return;
/*     */     }
/* 361 */     syncSessionState(source, target, source.isReadOnly());
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
/*     */   static void syncSessionState(Connection source, Connection target, boolean readOnly)
/*     */     throws SQLException
/*     */   {
/* 375 */     if (target != null) {
/* 376 */       target.setReadOnly(readOnly);
/*     */     }
/*     */     
/* 379 */     if ((source == null) || (target == null)) {
/* 380 */       return;
/*     */     }
/* 382 */     target.setAutoCommit(source.getAutoCommit());
/* 383 */     target.setCatalog(source.getCatalog());
/* 384 */     target.setTransactionIsolation(source.getTransactionIsolation());
/* 385 */     target.setSessionMaxRows(source.getSessionMaxRows());
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized Object invoke(Object proxy, Method method, Object[] args)
/*     */     throws Throwable
/*     */   {
/* 409 */     String methodName = method.getName();
/*     */     
/* 411 */     if ("getMultiHostSafeProxy".equals(methodName)) {
/* 412 */       return this.thisAsConnection;
/*     */     }
/*     */     
/* 415 */     if ("equals".equals(methodName))
/*     */     {
/* 417 */       return Boolean.valueOf(args[0].equals(this));
/*     */     }
/*     */     
/* 420 */     if ("hashCode".equals(methodName)) {
/* 421 */       return Integer.valueOf(hashCode());
/*     */     }
/*     */     
/* 424 */     if ("close".equals(methodName)) {
/* 425 */       doClose();
/* 426 */       this.isClosed = true;
/* 427 */       this.closedReason = "Connection explicitly closed.";
/* 428 */       this.closedExplicitly = true;
/* 429 */       return null;
/*     */     }
/*     */     
/* 432 */     if ("abortInternal".equals(methodName)) {
/* 433 */       doAbortInternal();
/* 434 */       this.currentConnection.abortInternal();
/* 435 */       this.isClosed = true;
/* 436 */       this.closedReason = "Connection explicitly closed.";
/* 437 */       return null;
/*     */     }
/*     */     
/* 440 */     if (("abort".equals(methodName)) && (args.length == 1)) {
/* 441 */       doAbort((Executor)args[0]);
/* 442 */       this.isClosed = true;
/* 443 */       this.closedReason = "Connection explicitly closed.";
/* 444 */       return null;
/*     */     }
/*     */     
/* 447 */     if ("isClosed".equals(methodName)) {
/* 448 */       return Boolean.valueOf(this.isClosed);
/*     */     }
/*     */     try
/*     */     {
/* 452 */       return invokeMore(proxy, method, args);
/*     */     } catch (InvocationTargetException e) {
/* 454 */       throw (e.getCause() != null ? e.getCause() : e);
/*     */     }
/*     */     catch (Exception e) {
/* 457 */       Class<?>[] declaredException = method.getExceptionTypes();
/* 458 */       for (Class<?> declEx : declaredException) {
/* 459 */         if (declEx.isAssignableFrom(e.getClass())) {
/* 460 */           throw e;
/*     */         }
/*     */       }
/* 463 */       throw new IllegalStateException(e.getMessage(), e);
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
/*     */   protected boolean allowedOnClosedConnection(Method method)
/*     */   {
/* 476 */     String methodName = method.getName();
/*     */     
/* 478 */     return (methodName.equals("getAutoCommit")) || (methodName.equals("getCatalog")) || (methodName.equals("getTransactionIsolation")) || (methodName.equals("getSessionMaxRows"));
/*     */   }
/*     */   
/*     */   abstract boolean shouldExceptionTriggerConnectionSwitch(Throwable paramThrowable);
/*     */   
/*     */   abstract boolean isMasterConnection();
/*     */   
/*     */   abstract void pickNewConnection()
/*     */     throws SQLException;
/*     */   
/*     */   abstract void doClose()
/*     */     throws SQLException;
/*     */   
/*     */   abstract void doAbortInternal()
/*     */     throws SQLException;
/*     */   
/*     */   abstract void doAbort(Executor paramExecutor)
/*     */     throws SQLException;
/*     */   
/*     */   abstract Object invokeMore(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
/*     */     throws Throwable;
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\MultiHostConnectionProxy.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */