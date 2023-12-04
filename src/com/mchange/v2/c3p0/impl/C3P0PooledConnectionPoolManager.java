/*     */ package com.mchange.v2.c3p0.impl;
/*     */ 
/*     */ import com.mchange.v1.db.sql.ConnectionUtils;
/*     */ import com.mchange.v1.db.sql.ResultSetUtils;
/*     */ import com.mchange.v1.db.sql.StatementUtils;
/*     */ import com.mchange.v1.lang.BooleanUtils;
/*     */ import com.mchange.v2.async.ThreadPoolAsynchronousRunner;
/*     */ import com.mchange.v2.c3p0.C3P0Registry;
/*     */ import com.mchange.v2.c3p0.ConnectionCustomizer;
/*     */ import com.mchange.v2.c3p0.ConnectionTester;
/*     */ import com.mchange.v2.c3p0.cfg.C3P0Config;
/*     */ import com.mchange.v2.coalesce.CoalesceChecker;
/*     */ import com.mchange.v2.coalesce.Coalescer;
/*     */ import com.mchange.v2.coalesce.CoalescerFactory;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import com.mchange.v2.resourcepool.BasicResourcePoolFactory;
/*     */ import com.mchange.v2.resourcepool.ResourcePoolFactory;
/*     */ import com.mchange.v2.sql.SqlUtils;
/*     */ import java.beans.BeanInfo;
/*     */ import java.beans.Introspector;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.Timer;
/*     */ import javax.sql.ConnectionPoolDataSource;
/*     */ import javax.sql.PooledConnection;
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class C3P0PooledConnectionPoolManager
/*     */ {
/*  44 */   private static final MLogger logger = MLog.getLogger(C3P0PooledConnectionPoolManager.class);
/*     */   
/*     */   private static final boolean POOL_EVENT_SUPPORT = false;
/*     */   
/*  48 */   private static final CoalesceChecker COALESCE_CHECKER = IdentityTokenizedCoalesceChecker.INSTANCE;
/*     */   
/*     */ 
/*  51 */   static final Coalescer COALESCER = CoalescerFactory.createCoalescer(COALESCE_CHECKER, true, false);
/*     */   
/*     */   static final int DFLT_NUM_TASK_THREADS_PER_DATA_SOURCE = 3;
/*     */   
/*     */   ThreadPoolAsynchronousRunner taskRunner;
/*     */   
/*     */   Timer timer;
/*     */   
/*     */   ResourcePoolFactory rpfact;
/*     */   
/*     */   Map authsToPools;
/*     */   
/*     */   final ConnectionPoolDataSource cpds;
/*     */   
/*     */   final Map propNamesToReadMethods;
/*     */   
/*     */   final Map flatPropertyOverrides;
/*     */   final Map userOverrides;
/*     */   final DbAuth defaultAuth;
/*     */   final String parentDataSourceIdentityToken;
/*  71 */   int num_task_threads = 3;
/*     */   
/*     */ 
/*     */   public int getThreadPoolSize()
/*     */   {
/*  76 */     return this.taskRunner.getThreadCount();
/*     */   }
/*     */   
/*  79 */   public int getThreadPoolNumActiveThreads() { return this.taskRunner.getActiveCount(); }
/*     */   
/*     */   public int getThreadPoolNumIdleThreads() {
/*  82 */     return this.taskRunner.getIdleCount();
/*     */   }
/*     */   
/*  85 */   public int getThreadPoolNumTasksPending() { return this.taskRunner.getPendingTaskCount(); }
/*     */   
/*     */   public String getThreadPoolStackTraces() {
/*  88 */     return this.taskRunner.getStackTraces();
/*     */   }
/*     */   
/*  91 */   public String getThreadPoolStatus() { return this.taskRunner.getStatus(); }
/*     */   
/*     */   private synchronized void poolsInit()
/*     */   {
/*  95 */     this.timer = new Timer(true);
/*     */     
/*  97 */     int matt = getMaxAdministrativeTaskTime(null);
/*  98 */     if (matt > 0)
/*     */     {
/* 100 */       int matt_ms = matt * 1000;
/* 101 */       this.taskRunner = new ThreadPoolAsynchronousRunner(this.num_task_threads, true, matt_ms, matt_ms * 3, matt_ms * 6, this.timer);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 117 */       this.taskRunner = new ThreadPoolAsynchronousRunner(this.num_task_threads, true, this.timer);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 123 */     this.rpfact = BasicResourcePoolFactory.createNoEventSupportInstance(this.taskRunner, this.timer);
/* 124 */     this.authsToPools = new HashMap();
/*     */   }
/*     */   
/*     */   private void poolsDestroy() {
/* 128 */     poolsDestroy(true);
/*     */   }
/*     */   
/*     */   private synchronized void poolsDestroy(boolean close_outstanding_connections)
/*     */   {
/* 133 */     for (Iterator ii = this.authsToPools.values().iterator(); ii.hasNext();) {
/*     */       try
/*     */       {
/* 136 */         ((C3P0PooledConnectionPool)ii.next()).close(close_outstanding_connections);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 140 */         logger.log(MLevel.WARNING, "An Exception occurred while trying to clean up a pool!", e);
/*     */       }
/*     */     }
/*     */     
/* 144 */     this.taskRunner.close(true);
/* 145 */     this.timer.cancel();
/*     */     
/* 147 */     this.taskRunner = null;
/* 148 */     this.timer = null;
/* 149 */     this.rpfact = null;
/* 150 */     this.authsToPools = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public C3P0PooledConnectionPoolManager(ConnectionPoolDataSource cpds, Map flatPropertyOverrides, Map forceUserOverrides, int num_task_threads, String parentDataSourceIdentityToken)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 162 */       this.cpds = cpds;
/* 163 */       this.flatPropertyOverrides = flatPropertyOverrides;
/* 164 */       this.num_task_threads = num_task_threads;
/* 165 */       this.parentDataSourceIdentityToken = parentDataSourceIdentityToken;
/*     */       
/* 167 */       DbAuth auth = null;
/*     */       
/* 169 */       if (flatPropertyOverrides != null)
/*     */       {
/* 171 */         String overrideUser = (String)flatPropertyOverrides.get("overrideDefaultUser");
/* 172 */         String overridePassword = (String)flatPropertyOverrides.get("overrideDefaultPassword");
/*     */         
/* 174 */         if (overrideUser == null)
/*     */         {
/* 176 */           overrideUser = (String)flatPropertyOverrides.get("user");
/* 177 */           overridePassword = (String)flatPropertyOverrides.get("password");
/*     */         }
/*     */         
/* 180 */         if (overrideUser != null) {
/* 181 */           auth = new DbAuth(overrideUser, overridePassword);
/*     */         }
/*     */       }
/* 184 */       if (auth == null) {
/* 185 */         auth = C3P0ImplUtils.findAuth(cpds);
/*     */       }
/* 187 */       this.defaultAuth = auth;
/*     */       
/* 189 */       Map tmp = new HashMap();
/* 190 */       BeanInfo bi = Introspector.getBeanInfo(cpds.getClass());
/* 191 */       PropertyDescriptor[] pds = bi.getPropertyDescriptors();
/* 192 */       PropertyDescriptor pd = null;
/* 193 */       int i = 0; for (int len = pds.length; i < len; i++)
/*     */       {
/* 195 */         pd = pds[i];
/*     */         
/* 197 */         String name = pd.getName();
/* 198 */         Method m = pd.getReadMethod();
/*     */         
/* 200 */         if (m != null)
/* 201 */           tmp.put(name, m);
/*     */       }
/* 203 */       this.propNamesToReadMethods = tmp;
/*     */       
/* 205 */       if (forceUserOverrides == null)
/*     */       {
/* 207 */         Method uom = (Method)this.propNamesToReadMethods.get("userOverridesAsString");
/* 208 */         if (uom != null)
/*     */         {
/* 210 */           String uoas = (String)uom.invoke(cpds, null);
/*     */           
/* 212 */           Map uo = C3P0ImplUtils.parseUserOverridesAsString(uoas);
/* 213 */           this.userOverrides = uo;
/*     */         }
/*     */         else {
/* 216 */           this.userOverrides = Collections.EMPTY_MAP;
/*     */         }
/*     */       } else {
/* 219 */         this.userOverrides = forceUserOverrides;
/*     */       }
/* 221 */       poolsInit();
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 226 */       logger.log(MLevel.FINE, null, e);
/*     */       
/* 228 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized C3P0PooledConnectionPool getPool(String username, String password, boolean create) throws SQLException
/*     */   {
/* 234 */     if (create) {
/* 235 */       return getPool(username, password);
/*     */     }
/*     */     
/* 238 */     DbAuth checkAuth = new DbAuth(username, password);
/* 239 */     C3P0PooledConnectionPool out = (C3P0PooledConnectionPool)this.authsToPools.get(checkAuth);
/* 240 */     if (out == null) {
/* 241 */       throw new SQLException("No pool has been initialized for databse user '" + username + "' with the specified password.");
/*     */     }
/* 243 */     return out;
/*     */   }
/*     */   
/*     */   public C3P0PooledConnectionPool getPool(String username, String password)
/*     */     throws SQLException
/*     */   {
/* 249 */     return getPool(new DbAuth(username, password));
/*     */   }
/*     */   
/*     */   public synchronized C3P0PooledConnectionPool getPool(DbAuth auth) throws SQLException
/*     */   {
/* 254 */     C3P0PooledConnectionPool out = (C3P0PooledConnectionPool)this.authsToPools.get(auth);
/* 255 */     if (out == null)
/*     */     {
/* 257 */       out = createPooledConnectionPool(auth);
/* 258 */       this.authsToPools.put(auth, out);
/*     */     }
/* 260 */     return out;
/*     */   }
/*     */   
/*     */   public synchronized Set getManagedAuths() {
/* 264 */     return Collections.unmodifiableSet(this.authsToPools.keySet());
/*     */   }
/*     */   
/* 267 */   public synchronized int getNumManagedAuths() { return this.authsToPools.size(); }
/*     */   
/*     */   public C3P0PooledConnectionPool getPool() throws SQLException
/*     */   {
/* 271 */     return getPool(this.defaultAuth);
/*     */   }
/*     */   
/*     */   public synchronized int getNumIdleConnectionsAllAuths() throws SQLException {
/* 275 */     int out = 0;
/* 276 */     for (Iterator ii = this.authsToPools.values().iterator(); ii.hasNext();)
/* 277 */       out += ((C3P0PooledConnectionPool)ii.next()).getNumIdleConnections();
/* 278 */     return out;
/*     */   }
/*     */   
/*     */   public synchronized int getNumBusyConnectionsAllAuths() throws SQLException
/*     */   {
/* 283 */     int out = 0;
/* 284 */     for (Iterator ii = this.authsToPools.values().iterator(); ii.hasNext();)
/* 285 */       out += ((C3P0PooledConnectionPool)ii.next()).getNumBusyConnections();
/* 286 */     return out;
/*     */   }
/*     */   
/*     */   public synchronized int getNumConnectionsAllAuths() throws SQLException
/*     */   {
/* 291 */     int out = 0;
/* 292 */     for (Iterator ii = this.authsToPools.values().iterator(); ii.hasNext();)
/* 293 */       out += ((C3P0PooledConnectionPool)ii.next()).getNumConnections();
/* 294 */     return out;
/*     */   }
/*     */   
/*     */   public synchronized int getNumUnclosedOrphanedConnectionsAllAuths() throws SQLException
/*     */   {
/* 299 */     int out = 0;
/* 300 */     for (Iterator ii = this.authsToPools.values().iterator(); ii.hasNext();)
/* 301 */       out += ((C3P0PooledConnectionPool)ii.next()).getNumUnclosedOrphanedConnections();
/* 302 */     return out;
/*     */   }
/*     */   
/*     */   public synchronized int getStatementCacheNumStatementsAllUsers() throws SQLException
/*     */   {
/* 307 */     int out = 0;
/* 308 */     for (Iterator ii = this.authsToPools.values().iterator(); ii.hasNext();)
/* 309 */       out += ((C3P0PooledConnectionPool)ii.next()).getStatementCacheNumStatements();
/* 310 */     return out;
/*     */   }
/*     */   
/*     */   public synchronized int getStatementCacheNumCheckedOutStatementsAllUsers() throws SQLException
/*     */   {
/* 315 */     int out = 0;
/* 316 */     for (Iterator ii = this.authsToPools.values().iterator(); ii.hasNext();)
/* 317 */       out += ((C3P0PooledConnectionPool)ii.next()).getStatementCacheNumCheckedOut();
/* 318 */     return out;
/*     */   }
/*     */   
/*     */   public synchronized int getStatementCacheNumConnectionsWithCachedStatementsAllUsers() throws SQLException
/*     */   {
/* 323 */     int out = 0;
/* 324 */     for (Iterator ii = this.authsToPools.values().iterator(); ii.hasNext();)
/* 325 */       out += ((C3P0PooledConnectionPool)ii.next()).getStatementCacheNumConnectionsWithCachedStatements();
/* 326 */     return out;
/*     */   }
/*     */   
/*     */   public synchronized void softResetAllAuths() throws SQLException
/*     */   {
/* 331 */     for (Iterator ii = this.authsToPools.values().iterator(); ii.hasNext();)
/* 332 */       ((C3P0PooledConnectionPool)ii.next()).reset();
/*     */   }
/*     */   
/*     */   public void close() {
/* 336 */     close(true);
/*     */   }
/*     */   
/*     */   public synchronized void close(boolean close_outstanding_connections)
/*     */   {
/* 341 */     if (this.authsToPools != null) {
/* 342 */       poolsDestroy(close_outstanding_connections);
/*     */     }
/*     */   }
/*     */   
/*     */   protected synchronized void finalize()
/*     */   {
/* 348 */     close();
/*     */   }
/*     */   
/*     */   private Object getObject(String propName, String userName)
/*     */   {
/* 353 */     Object out = null;
/*     */     
/* 355 */     if (userName != null)
/*     */     {
/*     */ 
/* 358 */       Map specificUserOverrides = (Map)this.userOverrides.get(userName);
/* 359 */       if (specificUserOverrides != null) {
/* 360 */         out = specificUserOverrides.get(propName);
/*     */       }
/*     */     }
/* 363 */     if ((out == null) && (this.flatPropertyOverrides != null)) {
/* 364 */       out = this.flatPropertyOverrides.get(propName);
/*     */     }
/*     */     
/*     */ 
/* 368 */     if (out == null)
/*     */     {
/*     */       try
/*     */       {
/* 372 */         Method m = (Method)this.propNamesToReadMethods.get(propName);
/* 373 */         if (m != null)
/*     */         {
/* 375 */           Object readProp = m.invoke(this.cpds, null);
/* 376 */           if (readProp != null) {
/* 377 */             out = readProp.toString();
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Exception e) {
/* 382 */         if (logger.isLoggable(MLevel.WARNING)) {
/* 383 */           logger.log(MLevel.WARNING, "An exception occurred while trying to read property '" + propName + "' from ConnectionPoolDataSource: " + this.cpds + ". Default config value will be used.", e);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 394 */     if (out == null) {
/* 395 */       out = C3P0Config.getUnspecifiedUserProperty(propName, null);
/*     */     }
/* 397 */     return out;
/*     */   }
/*     */   
/*     */   private String getString(String propName, String userName)
/*     */   {
/* 402 */     Object o = getObject(propName, userName);
/* 403 */     return o == null ? null : o.toString();
/*     */   }
/*     */   
/*     */   private int getInt(String propName, String userName) throws Exception
/*     */   {
/* 408 */     Object o = getObject(propName, userName);
/* 409 */     if ((o instanceof Integer))
/* 410 */       return ((Integer)o).intValue();
/* 411 */     if ((o instanceof String)) {
/* 412 */       return Integer.parseInt((String)o);
/*     */     }
/* 414 */     throw new Exception("Unexpected object found for putative int property '" + propName + "': " + o);
/*     */   }
/*     */   
/*     */   private boolean getBoolean(String propName, String userName) throws Exception
/*     */   {
/* 419 */     Object o = getObject(propName, userName);
/* 420 */     if ((o instanceof Boolean))
/* 421 */       return ((Boolean)o).booleanValue();
/* 422 */     if ((o instanceof String)) {
/* 423 */       return BooleanUtils.parseBoolean((String)o);
/*     */     }
/* 425 */     throw new Exception("Unexpected object found for putative boolean property '" + propName + "': " + o);
/*     */   }
/*     */   
/*     */   public String getAutomaticTestTable(String userName) {
/* 429 */     return getString("automaticTestTable", userName);
/*     */   }
/*     */   
/* 432 */   public String getPreferredTestQuery(String userName) { return getString("preferredTestQuery", userName); }
/*     */   
/*     */   private int getInitialPoolSize(String userName)
/*     */   {
/*     */     try {
/* 437 */       return getInt("initialPoolSize", userName);
/*     */     }
/*     */     catch (Exception e) {
/* 440 */       if (logger.isLoggable(MLevel.FINE))
/* 441 */         logger.log(MLevel.FINE, "Could not fetch int property", e); }
/* 442 */     return C3P0Defaults.initialPoolSize();
/*     */   }
/*     */   
/*     */   public int getMinPoolSize(String userName)
/*     */   {
/*     */     try
/*     */     {
/* 449 */       return getInt("minPoolSize", userName);
/*     */     }
/*     */     catch (Exception e) {
/* 452 */       if (logger.isLoggable(MLevel.FINE))
/* 453 */         logger.log(MLevel.FINE, "Could not fetch int property", e); }
/* 454 */     return C3P0Defaults.minPoolSize();
/*     */   }
/*     */   
/*     */   private int getMaxPoolSize(String userName)
/*     */   {
/*     */     try
/*     */     {
/* 461 */       return getInt("maxPoolSize", userName);
/*     */     }
/*     */     catch (Exception e) {
/* 464 */       if (logger.isLoggable(MLevel.FINE))
/* 465 */         logger.log(MLevel.FINE, "Could not fetch int property", e); }
/* 466 */     return C3P0Defaults.maxPoolSize();
/*     */   }
/*     */   
/*     */   private int getMaxStatements(String userName)
/*     */   {
/*     */     try
/*     */     {
/* 473 */       return getInt("maxStatements", userName);
/*     */     }
/*     */     catch (Exception e) {
/* 476 */       if (logger.isLoggable(MLevel.FINE))
/* 477 */         logger.log(MLevel.FINE, "Could not fetch int property", e); }
/* 478 */     return C3P0Defaults.maxStatements();
/*     */   }
/*     */   
/*     */   private int getMaxStatementsPerConnection(String userName)
/*     */   {
/*     */     try
/*     */     {
/* 485 */       return getInt("maxStatementsPerConnection", userName);
/*     */     }
/*     */     catch (Exception e) {
/* 488 */       if (logger.isLoggable(MLevel.FINE))
/* 489 */         logger.log(MLevel.FINE, "Could not fetch int property", e); }
/* 490 */     return C3P0Defaults.maxStatementsPerConnection();
/*     */   }
/*     */   
/*     */   private int getAcquireIncrement(String userName)
/*     */   {
/*     */     try
/*     */     {
/* 497 */       return getInt("acquireIncrement", userName);
/*     */     }
/*     */     catch (Exception e) {
/* 500 */       if (logger.isLoggable(MLevel.FINE))
/* 501 */         logger.log(MLevel.FINE, "Could not fetch int property", e); }
/* 502 */     return C3P0Defaults.acquireIncrement();
/*     */   }
/*     */   
/*     */   private int getAcquireRetryAttempts(String userName)
/*     */   {
/*     */     try
/*     */     {
/* 509 */       return getInt("acquireRetryAttempts", userName);
/*     */     }
/*     */     catch (Exception e) {
/* 512 */       if (logger.isLoggable(MLevel.FINE))
/* 513 */         logger.log(MLevel.FINE, "Could not fetch int property", e); }
/* 514 */     return C3P0Defaults.acquireRetryAttempts();
/*     */   }
/*     */   
/*     */   private int getAcquireRetryDelay(String userName)
/*     */   {
/*     */     try
/*     */     {
/* 521 */       return getInt("acquireRetryDelay", userName);
/*     */     }
/*     */     catch (Exception e) {
/* 524 */       if (logger.isLoggable(MLevel.FINE))
/* 525 */         logger.log(MLevel.FINE, "Could not fetch int property", e); }
/* 526 */     return C3P0Defaults.acquireRetryDelay();
/*     */   }
/*     */   
/*     */   private boolean getBreakAfterAcquireFailure(String userName)
/*     */   {
/*     */     try
/*     */     {
/* 533 */       return getBoolean("breakAfterAcquireFailure", userName);
/*     */     }
/*     */     catch (Exception e) {
/* 536 */       if (logger.isLoggable(MLevel.FINE))
/* 537 */         logger.log(MLevel.FINE, "Could not fetch boolean property", e); }
/* 538 */     return C3P0Defaults.breakAfterAcquireFailure();
/*     */   }
/*     */   
/*     */   private int getCheckoutTimeout(String userName)
/*     */   {
/*     */     try
/*     */     {
/* 545 */       return getInt("checkoutTimeout", userName);
/*     */     }
/*     */     catch (Exception e) {
/* 548 */       if (logger.isLoggable(MLevel.FINE))
/* 549 */         logger.log(MLevel.FINE, "Could not fetch int property", e); }
/* 550 */     return C3P0Defaults.checkoutTimeout();
/*     */   }
/*     */   
/*     */   private int getIdleConnectionTestPeriod(String userName)
/*     */   {
/*     */     try
/*     */     {
/* 557 */       return getInt("idleConnectionTestPeriod", userName);
/*     */     }
/*     */     catch (Exception e) {
/* 560 */       if (logger.isLoggable(MLevel.FINE))
/* 561 */         logger.log(MLevel.FINE, "Could not fetch int property", e); }
/* 562 */     return C3P0Defaults.idleConnectionTestPeriod();
/*     */   }
/*     */   
/*     */   private int getMaxIdleTime(String userName)
/*     */   {
/*     */     try
/*     */     {
/* 569 */       return getInt("maxIdleTime", userName);
/*     */     }
/*     */     catch (Exception e) {
/* 572 */       if (logger.isLoggable(MLevel.FINE))
/* 573 */         logger.log(MLevel.FINE, "Could not fetch int property", e); }
/* 574 */     return C3P0Defaults.maxIdleTime();
/*     */   }
/*     */   
/*     */   private int getUnreturnedConnectionTimeout(String userName)
/*     */   {
/*     */     try
/*     */     {
/* 581 */       return getInt("unreturnedConnectionTimeout", userName);
/*     */     }
/*     */     catch (Exception e) {
/* 584 */       if (logger.isLoggable(MLevel.FINE))
/* 585 */         logger.log(MLevel.FINE, "Could not fetch int property", e); }
/* 586 */     return C3P0Defaults.unreturnedConnectionTimeout();
/*     */   }
/*     */   
/*     */   private boolean getTestConnectionOnCheckout(String userName)
/*     */   {
/*     */     try
/*     */     {
/* 593 */       return getBoolean("testConnectionOnCheckout", userName);
/*     */     }
/*     */     catch (Exception e) {
/* 596 */       if (logger.isLoggable(MLevel.FINE))
/* 597 */         logger.log(MLevel.FINE, "Could not fetch boolean property", e); }
/* 598 */     return C3P0Defaults.testConnectionOnCheckout();
/*     */   }
/*     */   
/*     */   private boolean getTestConnectionOnCheckin(String userName)
/*     */   {
/*     */     try
/*     */     {
/* 605 */       return getBoolean("testConnectionOnCheckin", userName);
/*     */     }
/*     */     catch (Exception e) {
/* 608 */       if (logger.isLoggable(MLevel.FINE))
/* 609 */         logger.log(MLevel.FINE, "Could not fetch boolean property", e); }
/* 610 */     return C3P0Defaults.testConnectionOnCheckin();
/*     */   }
/*     */   
/*     */   private boolean getDebugUnreturnedConnectionStackTraces(String userName)
/*     */   {
/*     */     try
/*     */     {
/* 617 */       return getBoolean("debugUnreturnedConnectionStackTraces", userName);
/*     */     }
/*     */     catch (Exception e) {
/* 620 */       if (logger.isLoggable(MLevel.FINE))
/* 621 */         logger.log(MLevel.FINE, "Could not fetch boolean property", e); }
/* 622 */     return C3P0Defaults.debugUnreturnedConnectionStackTraces();
/*     */   }
/*     */   
/*     */   private String getConnectionTesterClassName(String userName)
/*     */   {
/* 627 */     return getString("connectionTesterClassName", userName);
/*     */   }
/*     */   
/* 630 */   private ConnectionTester getConnectionTester(String userName) { return C3P0Registry.getConnectionTester(getConnectionTesterClassName(userName)); }
/*     */   
/*     */   private String getConnectionCustomizerClassName(String userName) {
/* 633 */     return getString("connectionCustomizerClassName", userName);
/*     */   }
/*     */   
/* 636 */   private ConnectionCustomizer getConnectionCustomizer(String userName) throws SQLException { return C3P0Registry.getConnectionCustomizer(getConnectionCustomizerClassName(userName)); }
/*     */   
/*     */   private int getMaxIdleTimeExcessConnections(String userName)
/*     */   {
/*     */     try {
/* 641 */       return getInt("maxIdleTimeExcessConnections", userName);
/*     */     }
/*     */     catch (Exception e) {
/* 644 */       if (logger.isLoggable(MLevel.FINE))
/* 645 */         logger.log(MLevel.FINE, "Could not fetch int property", e); }
/* 646 */     return C3P0Defaults.maxIdleTimeExcessConnections();
/*     */   }
/*     */   
/*     */   private int getMaxAdministrativeTaskTime(String userName)
/*     */   {
/*     */     try
/*     */     {
/* 653 */       return getInt("maxAdministrativeTaskTime", userName);
/*     */     }
/*     */     catch (Exception e) {
/* 656 */       if (logger.isLoggable(MLevel.FINE))
/* 657 */         logger.log(MLevel.FINE, "Could not fetch int property", e); }
/* 658 */     return C3P0Defaults.maxAdministrativeTaskTime();
/*     */   }
/*     */   
/*     */   private int getMaxConnectionAge(String userName)
/*     */   {
/*     */     try
/*     */     {
/* 665 */       return getInt("maxConnectionAge", userName);
/*     */     }
/*     */     catch (Exception e) {
/* 668 */       if (logger.isLoggable(MLevel.FINE))
/* 669 */         logger.log(MLevel.FINE, "Could not fetch int property", e); }
/* 670 */     return C3P0Defaults.maxConnectionAge();
/*     */   }
/*     */   
/*     */   private int getPropertyCycle(String userName)
/*     */   {
/*     */     try
/*     */     {
/* 677 */       return getInt("propertyCycle", userName);
/*     */     }
/*     */     catch (Exception e) {
/* 680 */       if (logger.isLoggable(MLevel.FINE))
/* 681 */         logger.log(MLevel.FINE, "Could not fetch int property", e); }
/* 682 */     return C3P0Defaults.propertyCycle();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private C3P0PooledConnectionPool createPooledConnectionPool(DbAuth auth)
/*     */     throws SQLException
/*     */   {
/* 690 */     String userName = auth.getUser();
/* 691 */     String automaticTestTable = getAutomaticTestTable(userName);
/*     */     
/*     */     String realTestQuery;
/* 694 */     if (automaticTestTable != null)
/*     */     {
/* 696 */       String realTestQuery = initializeAutomaticTestTable(automaticTestTable, auth);
/* 697 */       if (getPreferredTestQuery(userName) != null)
/*     */       {
/* 699 */         if (logger.isLoggable(MLevel.WARNING))
/*     */         {
/* 701 */           logger.logp(MLevel.WARNING, C3P0PooledConnectionPoolManager.class.getName(), "createPooledConnectionPool", "[c3p0] Both automaticTestTable and preferredTestQuery have been set! Using automaticTestTable, and ignoring preferredTestQuery. Real test query is ''{0}''.", realTestQuery);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 732 */       if (!this.defaultAuth.equals(auth)) {
/* 733 */         ensureFirstConnectionAcquisition(auth);
/*     */       }
/* 735 */       realTestQuery = getPreferredTestQuery(userName);
/*     */     }
/*     */     
/* 738 */     C3P0PooledConnectionPool out = new C3P0PooledConnectionPool(this.cpds, auth, getMinPoolSize(userName), getMaxPoolSize(userName), getInitialPoolSize(userName), getAcquireIncrement(userName), getAcquireRetryAttempts(userName), getAcquireRetryDelay(userName), getBreakAfterAcquireFailure(userName), getCheckoutTimeout(userName), getIdleConnectionTestPeriod(userName), getMaxIdleTime(userName), getMaxIdleTimeExcessConnections(userName), getMaxConnectionAge(userName), getPropertyCycle(userName), getUnreturnedConnectionTimeout(userName), getDebugUnreturnedConnectionStackTraces(userName), getTestConnectionOnCheckout(userName), getTestConnectionOnCheckin(userName), getMaxStatements(userName), getMaxStatementsPerConnection(userName), getConnectionTester(userName), getConnectionCustomizer(userName), realTestQuery, this.rpfact, this.taskRunner, this.parentDataSourceIdentityToken);
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
/* 765 */     return out;
/*     */   }
/*     */   
/*     */ 
/*     */   private String initializeAutomaticTestTable(String automaticTestTable, DbAuth auth)
/*     */     throws SQLException
/*     */   {
/* 772 */     PooledConnection throwawayPooledConnection = auth.equals(this.defaultAuth) ? this.cpds.getPooledConnection() : this.cpds.getPooledConnection(auth.getUser(), auth.getPassword());
/*     */     
/*     */ 
/* 775 */     Connection c = null;
/* 776 */     PreparedStatement testStmt = null;
/* 777 */     PreparedStatement createStmt = null;
/* 778 */     ResultSet mdrs = null;
/* 779 */     ResultSet rs = null;
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 785 */       c = throwawayPooledConnection.getConnection();
/*     */       
/* 787 */       DatabaseMetaData dmd = c.getMetaData();
/* 788 */       String q = dmd.getIdentifierQuoteString();
/* 789 */       String quotedTableName = q + automaticTestTable + q;
/* 790 */       String out = "SELECT * FROM " + quotedTableName;
/* 791 */       mdrs = dmd.getTables(null, null, automaticTestTable, new String[] { "TABLE" });
/* 792 */       boolean exists = mdrs.next();
/*     */       
/*     */       String createSql;
/*     */       
/* 796 */       if (exists)
/*     */       {
/* 798 */         testStmt = c.prepareStatement(out);
/* 799 */         rs = testStmt.executeQuery();
/* 800 */         boolean has_rows = rs.next();
/* 801 */         if (has_rows) {
/* 802 */           throw new SQLException("automatic test table '" + automaticTestTable + "' contains rows, and it should not! Please set this " + "parameter to the name of a table c3p0 can create on its own, " + "that is not used elsewhere in the database!");
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 809 */         createSql = "CREATE TABLE " + quotedTableName + " ( a CHAR(1) )";
/*     */         try
/*     */         {
/* 812 */           createStmt = c.prepareStatement(createSql);
/* 813 */           createStmt.executeUpdate();
/*     */         }
/*     */         catch (SQLException e)
/*     */         {
/* 817 */           if (logger.isLoggable(MLevel.WARNING)) {
/* 818 */             logger.log(MLevel.WARNING, "An attempt to create an automatic test table failed. Create SQL: " + createSql, e);
/*     */           }
/*     */           
/*     */ 
/* 822 */           throw e;
/*     */         }
/*     */       }
/* 825 */       return out;
/*     */     }
/*     */     finally
/*     */     {
/* 829 */       ResultSetUtils.attemptClose(mdrs);
/* 830 */       ResultSetUtils.attemptClose(rs);
/* 831 */       StatementUtils.attemptClose(testStmt);
/* 832 */       StatementUtils.attemptClose(createStmt);
/* 833 */       ConnectionUtils.attemptClose(c);
/* 834 */       try { if (throwawayPooledConnection != null) throwawayPooledConnection.close();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 838 */         logger.log(MLevel.WARNING, "A PooledConnection failed to close.", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void ensureFirstConnectionAcquisition(DbAuth auth) throws SQLException
/*     */   {
/* 845 */     PooledConnection throwawayPooledConnection = auth.equals(this.defaultAuth) ? this.cpds.getPooledConnection() : this.cpds.getPooledConnection(auth.getUser(), auth.getPassword());
/*     */     
/*     */ 
/* 848 */     Connection c = null;
/*     */     try
/*     */     {
/* 851 */       c = throwawayPooledConnection.getConnection(); return;
/*     */     }
/*     */     finally
/*     */     {
/* 855 */       ConnectionUtils.attemptClose(c);
/* 856 */       try { if (throwawayPooledConnection != null) throwawayPooledConnection.close();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 860 */         logger.log(MLevel.WARNING, "A PooledConnection failed to close.", e);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\C3P0PooledConnectionPoolManager.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */