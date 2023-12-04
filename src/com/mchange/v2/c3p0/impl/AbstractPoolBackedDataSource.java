/*     */ package com.mchange.v2.c3p0.impl;
/*     */ 
/*     */ import com.mchange.lang.ThrowableUtils;
/*     */ import com.mchange.v2.c3p0.C3P0Registry;
/*     */ import com.mchange.v2.c3p0.PooledDataSource;
/*     */ import com.mchange.v2.c3p0.cfg.C3P0Config;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import java.beans.PropertyChangeEvent;
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Set;
/*     */ import javax.sql.ConnectionPoolDataSource;
/*     */ import javax.sql.PooledConnection;
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
/*     */ public abstract class AbstractPoolBackedDataSource
/*     */   extends PoolBackedDataSourceBase
/*     */   implements PooledDataSource
/*     */ {
/*  49 */   static final MLogger logger = MLog.getLogger(AbstractPoolBackedDataSource.class);
/*     */   
/*     */ 
/*     */   static final String NO_CPDS_ERR_MSG = "Attempted to use an uninitialized PoolBackedDataSource. Please call setConnectionPoolDataSource( ... ) to initialize.";
/*     */   
/*     */ 
/*     */   transient C3P0PooledConnectionPoolManager poolManager;
/*     */   
/*  57 */   transient boolean is_closed = false;
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final short VERSION = 1;
/*     */   
/*     */   protected AbstractPoolBackedDataSource(boolean autoregister) {
/*  62 */     super(autoregister);
/*  63 */     setUpPropertyEvents();
/*     */   }
/*     */   
/*     */   protected AbstractPoolBackedDataSource(String configName)
/*     */   {
/*  68 */     this(true);
/*  69 */     initializeNamedConfig(configName);
/*     */   }
/*     */   
/*     */   private void setUpPropertyEvents()
/*     */   {
/*  74 */     PropertyChangeListener l = new PropertyChangeListener()
/*     */     {
/*     */ 
/*  77 */       public void propertyChange(PropertyChangeEvent evt) { AbstractPoolBackedDataSource.this.resetPoolManager(); }
/*  78 */     };
/*  79 */     addPropertyChangeListener(l);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void initializeNamedConfig(String configName)
/*     */   {
/*     */     try
/*     */     {
/*  87 */       if (configName != null)
/*     */       {
/*  89 */         C3P0Config.bindNamedConfigToBean(this, configName);
/*  90 */         if (getDataSourceName().equals(getIdentityToken())) {
/*  91 */           setDataSourceName(configName);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/*  96 */       if (logger.isLoggable(MLevel.WARNING)) {
/*  97 */         logger.log(MLevel.WARNING, "Error binding PoolBackedDataSource to named-config '" + configName + "'. Some default-config values may be used.", e);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getDataSourceName()
/*     */   {
/* 119 */     String out = super.getDataSourceName();
/* 120 */     if (out == null)
/* 121 */       out = getIdentityToken();
/* 122 */     return out;
/*     */   }
/*     */   
/*     */   public Connection getConnection()
/*     */     throws SQLException
/*     */   {
/* 128 */     PooledConnection pc = getPoolManager().getPool().checkoutPooledConnection();
/* 129 */     return pc.getConnection();
/*     */   }
/*     */   
/*     */   public Connection getConnection(String username, String password) throws SQLException
/*     */   {
/* 134 */     PooledConnection pc = getPoolManager().getPool(username, password).checkoutPooledConnection();
/* 135 */     return pc.getConnection();
/*     */   }
/*     */   
/*     */   public PrintWriter getLogWriter() throws SQLException {
/* 139 */     return assertCpds().getLogWriter();
/*     */   }
/*     */   
/* 142 */   public void setLogWriter(PrintWriter out) throws SQLException { assertCpds().setLogWriter(out); }
/*     */   
/*     */   public int getLoginTimeout() throws SQLException {
/* 145 */     return assertCpds().getLoginTimeout();
/*     */   }
/*     */   
/* 148 */   public void setLoginTimeout(int seconds) throws SQLException { assertCpds().setLoginTimeout(seconds); }
/*     */   
/*     */   public int getNumConnections() throws SQLException
/*     */   {
/* 152 */     return getPoolManager().getPool().getNumConnections();
/*     */   }
/*     */   
/* 155 */   public int getNumIdleConnections() throws SQLException { return getPoolManager().getPool().getNumIdleConnections(); }
/*     */   
/*     */   public int getNumBusyConnections() throws SQLException {
/* 158 */     return getPoolManager().getPool().getNumBusyConnections();
/*     */   }
/*     */   
/* 161 */   public int getNumUnclosedOrphanedConnections() throws SQLException { return getPoolManager().getPool().getNumUnclosedOrphanedConnections(); }
/*     */   
/*     */   public int getNumConnectionsDefaultUser() throws SQLException {
/* 164 */     return getNumConnections();
/*     */   }
/*     */   
/* 167 */   public int getNumIdleConnectionsDefaultUser() throws SQLException { return getNumIdleConnections(); }
/*     */   
/*     */   public int getNumBusyConnectionsDefaultUser() throws SQLException {
/* 170 */     return getNumBusyConnections();
/*     */   }
/*     */   
/* 173 */   public int getNumUnclosedOrphanedConnectionsDefaultUser() throws SQLException { return getNumUnclosedOrphanedConnections(); }
/*     */   
/*     */   public int getStatementCacheNumStatementsDefaultUser() throws SQLException {
/* 176 */     return getPoolManager().getPool().getStatementCacheNumStatements();
/*     */   }
/*     */   
/* 179 */   public int getStatementCacheNumCheckedOutDefaultUser() throws SQLException { return getPoolManager().getPool().getStatementCacheNumCheckedOut(); }
/*     */   
/*     */   public int getStatementCacheNumConnectionsWithCachedStatementsDefaultUser() throws SQLException {
/* 182 */     return getPoolManager().getPool().getStatementCacheNumConnectionsWithCachedStatements();
/*     */   }
/*     */   
/* 185 */   public float getEffectivePropertyCycleDefaultUser() throws SQLException { return getPoolManager().getPool().getEffectivePropertyCycle(); }
/*     */   
/*     */   public long getStartTimeMillisDefaultUser() throws SQLException {
/* 188 */     return getPoolManager().getPool().getStartTime();
/*     */   }
/*     */   
/* 191 */   public long getUpTimeMillisDefaultUser() throws SQLException { return getPoolManager().getPool().getUpTime(); }
/*     */   
/*     */   public long getNumFailedCheckinsDefaultUser() throws SQLException {
/* 194 */     return getPoolManager().getPool().getNumFailedCheckins();
/*     */   }
/*     */   
/* 197 */   public long getNumFailedCheckoutsDefaultUser() throws SQLException { return getPoolManager().getPool().getNumFailedCheckouts(); }
/*     */   
/*     */   public long getNumFailedIdleTestsDefaultUser() throws SQLException {
/* 200 */     return getPoolManager().getPool().getNumFailedIdleTests();
/*     */   }
/*     */   
/* 203 */   public int getNumThreadsAwaitingCheckoutDefaultUser() throws SQLException { return getPoolManager().getPool().getNumThreadsAwaitingCheckout(); }
/*     */   
/*     */   public int getThreadPoolSize() throws SQLException {
/* 206 */     return getPoolManager().getThreadPoolSize();
/*     */   }
/*     */   
/* 209 */   public int getThreadPoolNumActiveThreads() throws SQLException { return getPoolManager().getThreadPoolNumActiveThreads(); }
/*     */   
/*     */   public int getThreadPoolNumIdleThreads() throws SQLException {
/* 212 */     return getPoolManager().getThreadPoolNumIdleThreads();
/*     */   }
/*     */   
/* 215 */   public int getThreadPoolNumTasksPending() throws SQLException { return getPoolManager().getThreadPoolNumTasksPending(); }
/*     */   
/*     */   public String sampleThreadPoolStackTraces() throws SQLException {
/* 218 */     return getPoolManager().getThreadPoolStackTraces();
/*     */   }
/*     */   
/* 221 */   public String sampleThreadPoolStatus() throws SQLException { return getPoolManager().getThreadPoolStatus(); }
/*     */   
/*     */   public String sampleStatementCacheStatusDefaultUser() throws SQLException {
/* 224 */     return getPoolManager().getPool().dumpStatementCacheStatus();
/*     */   }
/*     */   
/* 227 */   public String sampleStatementCacheStatus(String username, String password) throws SQLException { return assertAuthPool(username, password).dumpStatementCacheStatus(); }
/*     */   
/*     */   public Throwable getLastAcquisitionFailureDefaultUser() throws SQLException {
/* 230 */     return getPoolManager().getPool().getLastAcquisitionFailure();
/*     */   }
/*     */   
/* 233 */   public Throwable getLastCheckinFailureDefaultUser() throws SQLException { return getPoolManager().getPool().getLastCheckinFailure(); }
/*     */   
/*     */   public Throwable getLastCheckoutFailureDefaultUser() throws SQLException {
/* 236 */     return getPoolManager().getPool().getLastCheckoutFailure();
/*     */   }
/*     */   
/* 239 */   public Throwable getLastIdleTestFailureDefaultUser() throws SQLException { return getPoolManager().getPool().getLastIdleTestFailure(); }
/*     */   
/*     */   public Throwable getLastConnectionTestFailureDefaultUser() throws SQLException {
/* 242 */     return getPoolManager().getPool().getLastConnectionTestFailure();
/*     */   }
/*     */   
/* 245 */   public Throwable getLastAcquisitionFailure(String username, String password) throws SQLException { return assertAuthPool(username, password).getLastAcquisitionFailure(); }
/*     */   
/*     */   public Throwable getLastCheckinFailure(String username, String password) throws SQLException {
/* 248 */     return assertAuthPool(username, password).getLastCheckinFailure();
/*     */   }
/*     */   
/* 251 */   public Throwable getLastCheckoutFailure(String username, String password) throws SQLException { return assertAuthPool(username, password).getLastCheckoutFailure(); }
/*     */   
/*     */   public Throwable getLastIdleTestFailure(String username, String password) throws SQLException {
/* 254 */     return assertAuthPool(username, password).getLastIdleTestFailure();
/*     */   }
/*     */   
/* 257 */   public Throwable getLastConnectionTestFailure(String username, String password) throws SQLException { return assertAuthPool(username, password).getLastConnectionTestFailure(); }
/*     */   
/*     */   public int getNumThreadsAwaitingCheckout(String username, String password) throws SQLException {
/* 260 */     return assertAuthPool(username, password).getNumThreadsAwaitingCheckout();
/*     */   }
/*     */   
/*     */   public String sampleLastAcquisitionFailureStackTraceDefaultUser() throws SQLException {
/* 264 */     Throwable t = getLastAcquisitionFailureDefaultUser();
/* 265 */     return t == null ? null : ThrowableUtils.extractStackTrace(t);
/*     */   }
/*     */   
/*     */   public String sampleLastCheckinFailureStackTraceDefaultUser() throws SQLException
/*     */   {
/* 270 */     Throwable t = getLastCheckinFailureDefaultUser();
/* 271 */     return t == null ? null : ThrowableUtils.extractStackTrace(t);
/*     */   }
/*     */   
/*     */   public String sampleLastCheckoutFailureStackTraceDefaultUser() throws SQLException
/*     */   {
/* 276 */     Throwable t = getLastCheckoutFailureDefaultUser();
/* 277 */     return t == null ? null : ThrowableUtils.extractStackTrace(t);
/*     */   }
/*     */   
/*     */   public String sampleLastIdleTestFailureStackTraceDefaultUser() throws SQLException
/*     */   {
/* 282 */     Throwable t = getLastIdleTestFailureDefaultUser();
/* 283 */     return t == null ? null : ThrowableUtils.extractStackTrace(t);
/*     */   }
/*     */   
/*     */   public String sampleLastConnectionTestFailureStackTraceDefaultUser() throws SQLException
/*     */   {
/* 288 */     Throwable t = getLastConnectionTestFailureDefaultUser();
/* 289 */     return t == null ? null : ThrowableUtils.extractStackTrace(t);
/*     */   }
/*     */   
/*     */   public String sampleLastAcquisitionFailureStackTrace(String username, String password) throws SQLException
/*     */   {
/* 294 */     Throwable t = getLastAcquisitionFailure(username, password);
/* 295 */     return t == null ? null : ThrowableUtils.extractStackTrace(t);
/*     */   }
/*     */   
/*     */   public String sampleLastCheckinFailureStackTrace(String username, String password) throws SQLException
/*     */   {
/* 300 */     Throwable t = getLastCheckinFailure(username, password);
/* 301 */     return t == null ? null : ThrowableUtils.extractStackTrace(t);
/*     */   }
/*     */   
/*     */   public String sampleLastCheckoutFailureStackTrace(String username, String password) throws SQLException
/*     */   {
/* 306 */     Throwable t = getLastCheckoutFailure(username, password);
/* 307 */     return t == null ? null : ThrowableUtils.extractStackTrace(t);
/*     */   }
/*     */   
/*     */   public String sampleLastIdleTestFailureStackTrace(String username, String password) throws SQLException
/*     */   {
/* 312 */     Throwable t = getLastIdleTestFailure(username, password);
/* 313 */     return t == null ? null : ThrowableUtils.extractStackTrace(t);
/*     */   }
/*     */   
/*     */   public String sampleLastConnectionTestFailureStackTrace(String username, String password) throws SQLException
/*     */   {
/* 318 */     Throwable t = getLastConnectionTestFailure(username, password);
/* 319 */     return t == null ? null : ThrowableUtils.extractStackTrace(t);
/*     */   }
/*     */   
/*     */   public void softResetDefaultUser() throws SQLException {
/* 323 */     getPoolManager().getPool().reset();
/*     */   }
/*     */   
/* 326 */   public int getNumConnections(String username, String password) throws SQLException { return assertAuthPool(username, password).getNumConnections(); }
/*     */   
/*     */   public int getNumIdleConnections(String username, String password) throws SQLException {
/* 329 */     return assertAuthPool(username, password).getNumIdleConnections();
/*     */   }
/*     */   
/* 332 */   public int getNumBusyConnections(String username, String password) throws SQLException { return assertAuthPool(username, password).getNumBusyConnections(); }
/*     */   
/*     */   public int getNumUnclosedOrphanedConnections(String username, String password) throws SQLException {
/* 335 */     return assertAuthPool(username, password).getNumUnclosedOrphanedConnections();
/*     */   }
/*     */   
/* 338 */   public int getStatementCacheNumStatements(String username, String password) throws SQLException { return assertAuthPool(username, password).getStatementCacheNumStatements(); }
/*     */   
/*     */   public int getStatementCacheNumCheckedOut(String username, String password) throws SQLException {
/* 341 */     return assertAuthPool(username, password).getStatementCacheNumCheckedOut();
/*     */   }
/*     */   
/* 344 */   public int getStatementCacheNumConnectionsWithCachedStatements(String username, String password) throws SQLException { return assertAuthPool(username, password).getStatementCacheNumConnectionsWithCachedStatements(); }
/*     */   
/*     */   public float getEffectivePropertyCycle(String username, String password) throws SQLException {
/* 347 */     return assertAuthPool(username, password).getEffectivePropertyCycle();
/*     */   }
/*     */   
/* 350 */   public long getStartTimeMillis(String username, String password) throws SQLException { return assertAuthPool(username, password).getStartTime(); }
/*     */   
/*     */   public long getUpTimeMillis(String username, String password) throws SQLException {
/* 353 */     return assertAuthPool(username, password).getUpTime();
/*     */   }
/*     */   
/* 356 */   public long getNumFailedCheckins(String username, String password) throws SQLException { return assertAuthPool(username, password).getNumFailedCheckins(); }
/*     */   
/*     */   public long getNumFailedCheckouts(String username, String password) throws SQLException {
/* 359 */     return assertAuthPool(username, password).getNumFailedCheckouts();
/*     */   }
/*     */   
/* 362 */   public long getNumFailedIdleTests(String username, String password) throws SQLException { return assertAuthPool(username, password).getNumFailedIdleTests(); }
/*     */   
/*     */   public void softReset(String username, String password) throws SQLException {
/* 365 */     assertAuthPool(username, password).reset();
/*     */   }
/*     */   
/* 368 */   public int getNumBusyConnectionsAllUsers() throws SQLException { return getPoolManager().getNumBusyConnectionsAllAuths(); }
/*     */   
/*     */   public int getNumIdleConnectionsAllUsers() throws SQLException {
/* 371 */     return getPoolManager().getNumIdleConnectionsAllAuths();
/*     */   }
/*     */   
/* 374 */   public int getNumConnectionsAllUsers() throws SQLException { return getPoolManager().getNumConnectionsAllAuths(); }
/*     */   
/*     */   public int getNumUnclosedOrphanedConnectionsAllUsers() throws SQLException {
/* 377 */     return getPoolManager().getNumUnclosedOrphanedConnectionsAllAuths();
/*     */   }
/*     */   
/* 380 */   public int getStatementCacheNumStatementsAllUsers() throws SQLException { return getPoolManager().getStatementCacheNumStatementsAllUsers(); }
/*     */   
/*     */   public int getStatementCacheNumCheckedOutStatementsAllUsers() throws SQLException {
/* 383 */     return getPoolManager().getStatementCacheNumCheckedOutStatementsAllUsers();
/*     */   }
/*     */   
/* 386 */   public synchronized int getStatementCacheNumConnectionsWithCachedStatementsAllUsers() throws SQLException { return getPoolManager().getStatementCacheNumConnectionsWithCachedStatementsAllUsers(); }
/*     */   
/*     */   public void softResetAllUsers() throws SQLException {
/* 389 */     getPoolManager().softResetAllAuths();
/*     */   }
/*     */   
/* 392 */   public int getNumUserPools() throws SQLException { return getPoolManager().getNumManagedAuths(); }
/*     */   
/*     */   public Collection getAllUsers() throws SQLException
/*     */   {
/* 396 */     LinkedList out = new LinkedList();
/* 397 */     Set auths = getPoolManager().getManagedAuths();
/* 398 */     for (Iterator ii = auths.iterator(); ii.hasNext();)
/* 399 */       out.add(((DbAuth)ii.next()).getUser());
/* 400 */     return Collections.unmodifiableList(out);
/*     */   }
/*     */   
/*     */   public synchronized void hardReset()
/*     */   {
/* 405 */     resetPoolManager();
/*     */   }
/*     */   
/*     */   public synchronized void close()
/*     */   {
/* 410 */     resetPoolManager();
/* 411 */     this.is_closed = true;
/*     */     
/* 413 */     C3P0Registry.markClosed(this);
/*     */     
/* 415 */     if (logger.isLoggable(MLevel.FINEST))
/*     */     {
/* 417 */       logger.log(MLevel.FINEST, getClass().getName() + '@' + Integer.toHexString(System.identityHashCode(this)) + " has been closed. ", new Exception("DEBUG STACK TRACE for PoolBackedDataSource.close()."));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public void close(boolean force_destroy)
/*     */   {
/* 429 */     close();
/*     */   }
/*     */   
/*     */   public synchronized void resetPoolManager() {
/* 433 */     resetPoolManager(true);
/*     */   }
/*     */   
/*     */   public synchronized void resetPoolManager(boolean close_checked_out_connections) {
/* 437 */     if (this.poolManager != null)
/*     */     {
/* 439 */       this.poolManager.close(close_checked_out_connections);
/* 440 */       this.poolManager = null;
/*     */     }
/*     */   }
/*     */   
/*     */   private synchronized ConnectionPoolDataSource assertCpds() throws SQLException
/*     */   {
/* 446 */     if (this.is_closed) {
/* 447 */       throw new SQLException(this + " has been closed() -- you can no longer use it.");
/*     */     }
/* 449 */     ConnectionPoolDataSource out = getConnectionPoolDataSource();
/* 450 */     if (out == null)
/* 451 */       throw new SQLException("Attempted to use an uninitialized PoolBackedDataSource. Please call setConnectionPoolDataSource( ... ) to initialize.");
/* 452 */     return out;
/*     */   }
/*     */   
/*     */   private synchronized C3P0PooledConnectionPoolManager getPoolManager() throws SQLException
/*     */   {
/* 457 */     if (this.poolManager == null)
/*     */     {
/* 459 */       ConnectionPoolDataSource cpds = assertCpds();
/* 460 */       this.poolManager = new C3P0PooledConnectionPoolManager(cpds, null, null, getNumHelperThreads(), getIdentityToken());
/* 461 */       if (logger.isLoggable(MLevel.INFO))
/* 462 */         logger.info("Initializing c3p0 pool... " + toString());
/*     */     }
/* 464 */     return this.poolManager;
/*     */   }
/*     */   
/*     */   private C3P0PooledConnectionPool assertAuthPool(String username, String password) throws SQLException
/*     */   {
/* 469 */     C3P0PooledConnectionPool authPool = getPoolManager().getPool(username, password, false);
/* 470 */     if (authPool == null) {
/* 471 */       throw new SQLException("No pool has been yet been established for Connections authenticated by user '" + username + "' with the password provided. [Use getConnection( username, password ) " + "to initialize such a pool.]");
/*     */     }
/*     */     
/*     */ 
/* 475 */     return authPool;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void writeObject(ObjectOutputStream oos)
/*     */     throws IOException
/*     */   {
/* 484 */     oos.writeShort(1);
/*     */   }
/*     */   
/*     */   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
/*     */   {
/* 489 */     short version = ois.readShort();
/* 490 */     switch (version)
/*     */     {
/*     */     case 1: 
/* 493 */       setUpPropertyEvents();
/* 494 */       break;
/*     */     default: 
/* 496 */       throw new IOException("Unsupported Serialized Version: " + version);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\AbstractPoolBackedDataSource.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */