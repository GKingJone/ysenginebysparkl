/*     */ package com.mchange.v2.c3p0.impl;
/*     */ 
/*     */ import com.mchange.v1.db.sql.ConnectionUtils;
/*     */ import com.mchange.v2.async.AsynchronousRunner;
/*     */ import com.mchange.v2.async.ThreadPoolAsynchronousRunner;
/*     */ import com.mchange.v2.c3p0.ConnectionCustomizer;
/*     */ import com.mchange.v2.c3p0.ConnectionTester;
/*     */ import com.mchange.v2.c3p0.QueryConnectionTester;
/*     */ import com.mchange.v2.c3p0.SQLWarnings;
/*     */ import com.mchange.v2.c3p0.UnifiedConnectionTester;
/*     */ import com.mchange.v2.c3p0.WrapperConnectionPoolDataSource;
/*     */ import com.mchange.v2.c3p0.stmt.DoubleMaxStatementCache;
/*     */ import com.mchange.v2.c3p0.stmt.GlobalMaxOnlyStatementCache;
/*     */ import com.mchange.v2.c3p0.stmt.GooGooStatementCache;
/*     */ import com.mchange.v2.c3p0.stmt.PerConnectionMaxOnlyStatementCache;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import com.mchange.v2.resourcepool.CannotAcquireResourceException;
/*     */ import com.mchange.v2.resourcepool.ResourcePool;
/*     */ import com.mchange.v2.resourcepool.ResourcePool.Manager;
/*     */ import com.mchange.v2.resourcepool.ResourcePoolException;
/*     */ import com.mchange.v2.resourcepool.ResourcePoolFactory;
/*     */ import com.mchange.v2.resourcepool.TimeoutException;
/*     */ import com.mchange.v2.sql.SqlUtils;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.LinkedList;
/*     */ import javax.sql.ConnectionEvent;
/*     */ import javax.sql.ConnectionEventListener;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class C3P0PooledConnectionPool
/*     */ {
/*     */   private static final boolean ASYNCHRONOUS_CONNECTION_EVENT_LISTENER = false;
/*  61 */   private static final Throwable[] EMPTY_THROWABLE_HOLDER = new Throwable[1];
/*     */   
/*  63 */   static final MLogger logger = MLog.getLogger(C3P0PooledConnectionPool.class);
/*     */   
/*     */   final ResourcePool rp;
/*  66 */   final ConnectionEventListener cl = new ConnectionEventListenerImpl();
/*     */   
/*     */   final ConnectionTester connectionTester;
/*     */   
/*     */   final GooGooStatementCache scache;
/*     */   
/*     */   final int checkoutTimeout;
/*     */   
/*     */   final AsynchronousRunner sharedTaskRunner;
/*  75 */   final ThrowableHolderPool thp = new ThrowableHolderPool();
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
/*     */   C3P0PooledConnectionPool(final ConnectionPoolDataSource cpds, final DbAuth auth, int min, int max, int start, int inc, int acq_retry_attempts, int acq_retry_delay, boolean break_after_acq_failure, int checkoutTimeout, int idleConnectionTestPeriod, int maxIdleTime, int maxIdleTimeExcessConnections, int maxConnectionAge, int propertyCycle, int unreturnedConnectionTimeout, boolean debugUnreturnedConnectionStackTraces, final boolean testConnectionOnCheckout, final boolean testConnectionOnCheckin, int maxStatements, int maxStatementsPerConnection, final ConnectionTester connectionTester, final ConnectionCustomizer connectionCustomizer, final String testQuery, ResourcePoolFactory fact, ThreadPoolAsynchronousRunner taskRunner, final String parentDataSourceIdentityToken)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 107 */       if ((maxStatements > 0) && (maxStatementsPerConnection > 0)) {
/* 108 */         this.scache = new DoubleMaxStatementCache(taskRunner, maxStatements, maxStatementsPerConnection);
/* 109 */       } else if (maxStatementsPerConnection > 0) {
/* 110 */         this.scache = new PerConnectionMaxOnlyStatementCache(taskRunner, maxStatementsPerConnection);
/* 111 */       } else if (maxStatements > 0) {
/* 112 */         this.scache = new GlobalMaxOnlyStatementCache(taskRunner, maxStatements);
/*     */       } else {
/* 114 */         this.scache = null;
/*     */       }
/* 116 */       this.connectionTester = connectionTester;
/*     */       
/* 118 */       this.checkoutTimeout = checkoutTimeout;
/*     */       
/* 120 */       this.sharedTaskRunner = taskRunner;
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
/* 497 */       ResourcePool.Manager manager = new ResourcePool.Manager()
/*     */       {
/* 128 */         final boolean connectionTesterIsDefault = connectionTester instanceof DefaultConnectionTester;
/* 129 */         final boolean c3p0PooledConnections = cpds instanceof WrapperConnectionPoolDataSource;
/*     */         
/*     */         public Object acquireResource() throws Exception
/*     */         {
/*     */           PooledConnection out;
/*     */           PooledConnection out;
/* 135 */           if (connectionCustomizer == null)
/*     */           {
/* 137 */             out = auth.equals(C3P0ImplUtils.NULL_AUTH) ? cpds.getPooledConnection() : cpds.getPooledConnection(auth.getUser(), auth.getPassword());
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/*     */ 
/*     */             try
/*     */             {
/*     */ 
/* 146 */               WrapperConnectionPoolDataSourceBase wcpds = (WrapperConnectionPoolDataSourceBase)cpds;
/*     */               
/* 148 */               out = auth.equals(C3P0ImplUtils.NULL_AUTH) ? wcpds.getPooledConnection(connectionCustomizer, parentDataSourceIdentityToken) : wcpds.getPooledConnection(auth.getUser(), auth.getPassword(), connectionCustomizer, parentDataSourceIdentityToken);
/*     */ 
/*     */ 
/*     */             }
/*     */             catch (ClassCastException e)
/*     */             {
/*     */ 
/*     */ 
/* 156 */               throw SqlUtils.toSQLException("Cannot use a ConnectionCustomizer with a non-c3p0 ConnectionPoolDataSource. ConnectionPoolDataSource: " + cpds.getClass().getName(), e);
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           try
/*     */           {
/* 166 */             if (C3P0PooledConnectionPool.this.scache != null)
/*     */             {
/* 168 */               if (this.c3p0PooledConnections) {
/* 169 */                 ((AbstractC3P0PooledConnection)out).initStatementCache(C3P0PooledConnectionPool.this.scache);
/*     */ 
/*     */ 
/*     */               }
/*     */               else
/*     */               {
/*     */ 
/* 176 */                 C3P0PooledConnectionPool.logger.warning("StatementPooling not implemented for external (non-c3p0) ConnectionPoolDataSources.");
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 183 */             Connection con = null;
/*     */             try
/*     */             {
/* 186 */               con = out.getConnection();
/* 187 */               SQLWarnings.logAndClearWarnings(con);
/*     */ 
/*     */             }
/*     */             finally
/*     */             {
/* 192 */               ConnectionUtils.attemptClose(con);
/*     */             }
/*     */             
/* 195 */             out.addConnectionEventListener(C3P0PooledConnectionPool.this.cl);
/* 196 */             return out;
/*     */           }
/*     */           catch (Exception e)
/*     */           {
/* 200 */             if (C3P0PooledConnectionPool.logger.isLoggable(MLevel.WARNING))
/* 201 */               C3P0PooledConnectionPool.logger.warning("A PooledConnection was acquired, but an Exception occurred while preparing it for use. Attempting to destroy.");
/*     */             try {
/* 203 */               destroyResource(out);
/*     */             }
/*     */             catch (Exception e2) {
/* 206 */               if (C3P0PooledConnectionPool.logger.isLoggable(MLevel.WARNING)) {
/* 207 */                 C3P0PooledConnectionPool.logger.log(MLevel.WARNING, "An Exception occurred while trying to close partially acquired PooledConnection.", e2);
/*     */               }
/*     */             }
/*     */             
/*     */ 
/* 212 */             throw e;
/*     */           }
/*     */           finally
/*     */           {
/* 216 */             if (C3P0PooledConnectionPool.logger.isLoggable(MLevel.FINEST)) {
/* 217 */               C3P0PooledConnectionPool.logger.finest(this + ".acquireResource() returning. ");
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void refurbishResourceOnCheckout(Object resc)
/*     */           throws Exception
/*     */         {
/* 232 */           if (testConnectionOnCheckout)
/*     */           {
/* 234 */             if (C3P0PooledConnectionPool.logger.isLoggable(MLevel.FINER)) {
/* 235 */               finerLoggingTestPooledConnection(resc, "CHECKOUT");
/*     */             } else
/* 237 */               testPooledConnection(resc);
/*     */           }
/* 239 */           if (connectionCustomizer != null)
/*     */           {
/* 241 */             Connection physicalConnection = null;
/*     */             try
/*     */             {
/* 244 */               physicalConnection = ((AbstractC3P0PooledConnection)resc).getPhysicalConnection();
/* 245 */               connectionCustomizer.onCheckOut(physicalConnection, parentDataSourceIdentityToken);
/*     */             }
/*     */             catch (ClassCastException e)
/*     */             {
/* 249 */               throw SqlUtils.toSQLException("Cannot use a ConnectionCustomizer with a non-c3p0 PooledConnection. PooledConnection: " + resc + "; ConnectionPoolDataSource: " + cpds.getClass().getName(), e);
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */         public void refurbishResourceOnCheckin(Object resc)
/*     */           throws Exception
/*     */         {
/* 258 */           if (connectionCustomizer != null)
/*     */           {
/* 260 */             Connection physicalConnection = null;
/*     */             try
/*     */             {
/* 263 */               physicalConnection = ((AbstractC3P0PooledConnection)resc).getPhysicalConnection();
/* 264 */               connectionCustomizer.onCheckIn(physicalConnection, parentDataSourceIdentityToken);
/* 265 */               SQLWarnings.logAndClearWarnings(physicalConnection);
/*     */             }
/*     */             catch (ClassCastException e)
/*     */             {
/* 269 */               throw SqlUtils.toSQLException("Cannot use a ConnectionCustomizer with a non-c3p0 PooledConnection. PooledConnection: " + resc + "; ConnectionPoolDataSource: " + cpds.getClass().getName(), e);
/*     */             }
/*     */             
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/* 276 */             PooledConnection pc = (PooledConnection)resc;
/* 277 */             Connection con = null;
/*     */             
/*     */ 
/*     */             try
/*     */             {
/* 282 */               pc.removeConnectionEventListener(C3P0PooledConnectionPool.this.cl);
/*     */               
/* 284 */               con = pc.getConnection();
/* 285 */               SQLWarnings.logAndClearWarnings(con);
/*     */ 
/*     */             }
/*     */             finally
/*     */             {
/* 290 */               ConnectionUtils.attemptClose(con);
/*     */               
/* 292 */               pc.addConnectionEventListener(C3P0PooledConnectionPool.this.cl);
/*     */             }
/*     */           }
/*     */           
/* 296 */           if (testConnectionOnCheckin)
/*     */           {
/* 298 */             if (C3P0PooledConnectionPool.logger.isLoggable(MLevel.FINER)) {
/* 299 */               finerLoggingTestPooledConnection(resc, "CHECKIN");
/*     */             } else {
/* 301 */               testPooledConnection(resc);
/*     */             }
/*     */           }
/*     */         }
/*     */         
/*     */         public void refurbishIdleResource(Object resc) throws Exception {
/* 307 */           if (C3P0PooledConnectionPool.logger.isLoggable(MLevel.FINER)) {
/* 308 */             finerLoggingTestPooledConnection(resc, "IDLE CHECK");
/*     */           } else {
/* 310 */             testPooledConnection(resc);
/*     */           }
/*     */         }
/*     */         
/*     */         private void finerLoggingTestPooledConnection(Object resc, String testImpetus) throws Exception {
/* 315 */           C3P0PooledConnectionPool.logger.finer("Testing PooledConnection [" + resc + "] on " + testImpetus + ".");
/*     */           try
/*     */           {
/* 318 */             testPooledConnection(resc);
/* 319 */             C3P0PooledConnectionPool.logger.finer("Test of PooledConnection [" + resc + "] on " + testImpetus + " has SUCCEEDED.");
/*     */           }
/*     */           catch (Exception e)
/*     */           {
/* 323 */             C3P0PooledConnectionPool.logger.log(MLevel.FINER, "Test of PooledConnection [" + resc + "] on " + testImpetus + " has FAILED.", e);
/* 324 */             e.fillInStackTrace();
/* 325 */             throw e;
/*     */           }
/*     */         }
/*     */         
/*     */         private void testPooledConnection(Object resc) throws Exception
/*     */         {
/* 331 */           PooledConnection pc = (PooledConnection)resc;
/*     */           
/* 333 */           Throwable[] throwableHolder = C3P0PooledConnectionPool.EMPTY_THROWABLE_HOLDER;
/*     */           
/* 335 */           Connection conn = null;
/* 336 */           Throwable rootCause = null;
/*     */           int status;
/*     */           try
/*     */           {
/* 340 */             pc.removeConnectionEventListener(C3P0PooledConnectionPool.this.cl);
/*     */             
/* 342 */             conn = pc.getConnection();
/*     */             
/*     */ 
/*     */             Connection testConn;
/*     */             
/*     */ 
/*     */             Connection testConn;
/*     */             
/*     */ 
/* 351 */             if (C3P0PooledConnectionPool.this.scache != null)
/*     */             {
/*     */               Connection testConn;
/* 354 */               if ((testQuery == null) && (this.connectionTesterIsDefault) && (this.c3p0PooledConnections)) {
/* 355 */                 testConn = ((AbstractC3P0PooledConnection)pc).getPhysicalConnection();
/*     */               } else {
/* 357 */                 testConn = conn;
/*     */               }
/*     */             } else {
/*     */               Connection testConn;
/* 361 */               if (this.c3p0PooledConnections) {
/* 362 */                 testConn = ((AbstractC3P0PooledConnection)pc).getPhysicalConnection();
/*     */               } else
/* 364 */                 testConn = conn;
/*     */             }
/*     */             int status;
/* 367 */             if (testQuery == null) {
/* 368 */               status = connectionTester.activeCheckConnection(testConn);
/*     */             } else {
/*     */               int status;
/* 371 */               if ((connectionTester instanceof UnifiedConnectionTester))
/*     */               {
/* 373 */                 throwableHolder = C3P0PooledConnectionPool.this.thp.getThrowableHolder();
/* 374 */                 status = ((UnifiedConnectionTester)connectionTester).activeCheckConnection(testConn, testQuery, throwableHolder);
/*     */               } else { int status;
/* 376 */                 if ((connectionTester instanceof QueryConnectionTester)) {
/* 377 */                   status = ((QueryConnectionTester)connectionTester).activeCheckConnection(testConn, testQuery);
/*     */ 
/*     */ 
/*     */                 }
/*     */                 else
/*     */                 {
/*     */ 
/*     */ 
/* 385 */                   C3P0PooledConnectionPool.logger.warning("[c3p0] testQuery '" + testQuery + "' ignored. Please set a ConnectionTester that implements " + "com.mchange.v2.c3p0.QueryConnectionTester, or use the " + "DefaultConnectionTester, to test with the testQuery.");
/*     */                   
/*     */ 
/*     */ 
/* 389 */                   status = connectionTester.activeCheckConnection(testConn);
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (Exception e)
/*     */           {
/* 396 */             C3P0PooledConnectionPool.logger.log(MLevel.FINE, "A Connection test failed with an Exception.", e);
/*     */             
/* 398 */             status = -1;
/*     */             
/*     */ 
/* 401 */             rootCause = e;
/*     */           }
/*     */           finally
/*     */           {
/* 405 */             if (rootCause == null) {
/* 406 */               rootCause = throwableHolder[0];
/* 407 */             } else if ((throwableHolder[0] != null) && (C3P0PooledConnectionPool.logger.isLoggable(MLevel.FINE))) {
/* 408 */               C3P0PooledConnectionPool.logger.log(MLevel.FINE, "Internal Connection Test Exception", throwableHolder[0]);
/*     */             }
/* 410 */             if (throwableHolder != C3P0PooledConnectionPool.EMPTY_THROWABLE_HOLDER) {
/* 411 */               C3P0PooledConnectionPool.this.thp.returnThrowableHolder(throwableHolder);
/*     */             }
/* 413 */             ConnectionUtils.attemptClose(conn);
/* 414 */             pc.addConnectionEventListener(C3P0PooledConnectionPool.this.cl);
/*     */           }
/*     */           
/* 417 */           switch (status)
/*     */           {
/*     */           case 0: 
/*     */             break;
/*     */           case -8: 
/* 422 */             C3P0PooledConnectionPool.this.rp.resetPool();
/*     */           case -1: 
/*     */             Exception throwMe;
/*     */             Exception throwMe;
/* 426 */             if (rootCause == null) {
/* 427 */               throwMe = new SQLException("Connection is invalid");
/*     */             } else
/* 429 */               throwMe = SqlUtils.toSQLException("Connection is invalid", rootCause);
/* 430 */             throw throwMe;
/*     */           default: 
/* 432 */             throw new Error("Bad Connection Tester (" + connectionTester + ") " + "returned invalid status (" + status + ").");
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */         public void destroyResource(Object resc)
/*     */           throws Exception
/*     */         {
/*     */           try
/*     */           {
/* 442 */             if (connectionCustomizer != null)
/*     */             {
/* 444 */               Connection physicalConnection = null;
/*     */               try
/*     */               {
/* 447 */                 physicalConnection = ((AbstractC3P0PooledConnection)resc).getPhysicalConnection();
/* 448 */                 connectionCustomizer.onDestroy(physicalConnection, parentDataSourceIdentityToken);
/*     */               }
/*     */               catch (ClassCastException e)
/*     */               {
/* 452 */                 throw SqlUtils.toSQLException("Cannot use a ConnectionCustomizer with a non-c3p0 PooledConnection. PooledConnection: " + resc + "; ConnectionPoolDataSource: " + cpds.getClass().getName(), e);
/*     */ 
/*     */               }
/*     */               catch (Exception e)
/*     */               {
/*     */ 
/* 458 */                 if (C3P0PooledConnectionPool.logger.isLoggable(MLevel.WARNING)) {
/* 459 */                   C3P0PooledConnectionPool.logger.log(MLevel.WARNING, "An exception occurred while executing the onDestroy() method of " + connectionCustomizer + ". c3p0 will attempt to destroy the target Connection regardless, but this issue " + " should be investigated and fixed.", e);
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 467 */             if (C3P0PooledConnectionPool.logger.isLoggable(MLevel.FINER)) {
/* 468 */               C3P0PooledConnectionPool.logger.log(MLevel.FINER, "Preparing to destroy PooledConnection: " + resc);
/*     */             }
/* 470 */             ((PooledConnection)resc).close();
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 475 */             if (C3P0PooledConnectionPool.logger.isLoggable(MLevel.FINER)) {
/* 476 */               C3P0PooledConnectionPool.logger.log(MLevel.FINER, "Successfully destroyed PooledConnection: " + resc);
/*     */ 
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */           }
/*     */           catch (Exception e)
/*     */           {
/*     */ 
/* 486 */             if (C3P0PooledConnectionPool.logger.isLoggable(MLevel.FINER)) {
/* 487 */               C3P0PooledConnectionPool.logger.log(MLevel.FINER, "Failed to destroy PooledConnection: " + resc);
/*     */             }
/*     */             
/*     */ 
/*     */ 
/* 492 */             throw e;
/*     */           }
/*     */         }
/*     */       };
/*     */       
/*     */ 
/*     */ 
/* 499 */       synchronized (fact)
/*     */       {
/* 501 */         fact.setMin(min);
/* 502 */         fact.setMax(max);
/* 503 */         fact.setStart(start);
/* 504 */         fact.setIncrement(inc);
/* 505 */         fact.setIdleResourceTestPeriod(idleConnectionTestPeriod * 1000);
/* 506 */         fact.setResourceMaxIdleTime(maxIdleTime * 1000);
/* 507 */         fact.setExcessResourceMaxIdleTime(maxIdleTimeExcessConnections * 1000);
/* 508 */         fact.setResourceMaxAge(maxConnectionAge * 1000);
/* 509 */         fact.setExpirationEnforcementDelay(propertyCycle * 1000);
/* 510 */         fact.setDestroyOverdueResourceTime(unreturnedConnectionTimeout * 1000);
/* 511 */         fact.setDebugStoreCheckoutStackTrace(debugUnreturnedConnectionStackTraces);
/* 512 */         fact.setAcquisitionRetryAttempts(acq_retry_attempts);
/* 513 */         fact.setAcquisitionRetryDelay(acq_retry_delay);
/* 514 */         fact.setBreakOnAcquisitionFailure(break_after_acq_failure);
/* 515 */         this.rp = fact.createPool(manager);
/*     */       }
/*     */     }
/*     */     catch (ResourcePoolException e) {
/* 519 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public PooledConnection checkoutPooledConnection() throws SQLException {
/*     */     try {
/* 525 */       return (PooledConnection)this.rp.checkoutResource(this.checkoutTimeout);
/*     */     } catch (TimeoutException e) {
/* 527 */       throw SqlUtils.toSQLException("An attempt by a client to checkout a Connection has timed out.", e);
/*     */     } catch (CannotAcquireResourceException e) {
/* 529 */       throw SqlUtils.toSQLException("Connections could not be acquired from the underlying database!", "08001", e);
/*     */     } catch (Exception e) {
/* 531 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public void checkinPooledConnection(PooledConnection pcon) throws SQLException {
/*     */     try {
/* 537 */       this.rp.checkinResource(pcon);
/*     */     } catch (ResourcePoolException e) {
/* 539 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public float getEffectivePropertyCycle() throws SQLException {
/*     */     try {
/* 545 */       return (float)this.rp.getEffectiveExpirationEnforcementDelay() / 1000.0F;
/*     */     } catch (ResourcePoolException e) {
/* 547 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public int getNumThreadsAwaitingCheckout() throws SQLException {
/*     */     try {
/* 553 */       return this.rp.getNumCheckoutWaiters();
/*     */     } catch (ResourcePoolException e) {
/* 555 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/* 559 */   public int getStatementCacheNumStatements() { return this.scache == null ? 0 : this.scache.getNumStatements(); }
/*     */   
/*     */   public int getStatementCacheNumCheckedOut() {
/* 562 */     return this.scache == null ? 0 : this.scache.getNumStatementsCheckedOut();
/*     */   }
/*     */   
/* 565 */   public int getStatementCacheNumConnectionsWithCachedStatements() { return this.scache == null ? 0 : this.scache.getNumConnectionsWithCachedStatements(); }
/*     */   
/*     */   public String dumpStatementCacheStatus() {
/* 568 */     return this.scache == null ? "Statement caching disabled." : this.scache.dumpStatementCacheStatus();
/*     */   }
/*     */   
/* 571 */   public void close() throws SQLException { close(true); }
/*     */   
/*     */   public void close(boolean close_outstanding_connections)
/*     */     throws SQLException
/*     */   {
/* 576 */     Exception throwMe = null;
/*     */     try {
/* 578 */       if (this.scache != null) this.scache.close();
/*     */     } catch (SQLException e) {
/* 580 */       throwMe = e;
/*     */     }
/*     */     try {
/* 583 */       this.rp.close(close_outstanding_connections);
/*     */     }
/*     */     catch (ResourcePoolException e) {
/* 586 */       if ((throwMe != null) && (logger.isLoggable(MLevel.WARNING)))
/* 587 */         logger.log(MLevel.WARNING, "An Exception occurred while closing the StatementCache.", throwMe);
/* 588 */       throwMe = e;
/*     */     }
/*     */     
/* 591 */     if (throwMe != null) {
/* 592 */       throw SqlUtils.toSQLException(throwMe);
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
/*     */   class ConnectionEventListenerImpl
/*     */     implements ConnectionEventListener
/*     */   {
/*     */     ConnectionEventListenerImpl() {}
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
/*     */     public void connectionClosed(ConnectionEvent evt)
/*     */     {
/* 630 */       doCheckinResource(evt);
/*     */     }
/*     */     
/*     */     private void doCheckinResource(ConnectionEvent evt)
/*     */     {
/*     */       try {
/* 636 */         C3P0PooledConnectionPool.this.rp.checkinResource(evt.getSource());
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 640 */         C3P0PooledConnectionPool.logger.log(MLevel.WARNING, "An Exception occurred while trying to check a PooledConection into a ResourcePool.", e);
/*     */       }
/*     */     }
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
/*     */     public void connectionErrorOccurred(ConnectionEvent evt)
/*     */     {
/* 667 */       if (C3P0PooledConnectionPool.logger.isLoggable(MLevel.FINE)) {
/* 668 */         C3P0PooledConnectionPool.logger.fine("CONNECTION ERROR OCCURRED!");
/*     */       }
/* 670 */       PooledConnection pc = (PooledConnection)evt.getSource();
/*     */       int status;
/* 672 */       int status; if ((pc instanceof C3P0PooledConnection)) {
/* 673 */         status = ((C3P0PooledConnection)pc).getConnectionStatus(); } else { int status;
/* 674 */         if ((pc instanceof NewPooledConnection)) {
/* 675 */           status = ((NewPooledConnection)pc).getConnectionStatus();
/*     */         } else
/* 677 */           status = -1;
/*     */       }
/* 679 */       int final_status = status;
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
/* 691 */       doMarkPoolStatus(pc, final_status);
/*     */     }
/*     */     
/*     */     private void doMarkPoolStatus(PooledConnection pc, int status)
/*     */     {
/*     */       try
/*     */       {
/* 698 */         switch (status)
/*     */         {
/*     */         case 0: 
/* 701 */           throw new RuntimeException("connectionErrorOcccurred() should only be called for errors fatal to the Connection.");
/*     */         
/*     */         case -1: 
/* 704 */           C3P0PooledConnectionPool.this.rp.markBroken(pc);
/* 705 */           break;
/*     */         case -8: 
/* 707 */           if (C3P0PooledConnectionPool.logger.isLoggable(MLevel.WARNING)) {
/* 708 */             C3P0PooledConnectionPool.logger.warning("A ConnectionTest has failed, reporting that all previously acquired Connections are likely invalid. The pool will be reset.");
/*     */           }
/* 710 */           C3P0PooledConnectionPool.this.rp.resetPool();
/* 711 */           break;
/*     */         default: 
/* 713 */           throw new RuntimeException("Bad Connection Tester (" + C3P0PooledConnectionPool.this.connectionTester + ") " + "returned invalid status (" + status + ").");
/*     */         
/*     */ 
/*     */         }
/*     */         
/*     */       }
/*     */       catch (ResourcePoolException e)
/*     */       {
/* 721 */         C3P0PooledConnectionPool.logger.log(MLevel.WARNING, "Uh oh... our resource pool is probably broken!", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public int getNumConnections() throws SQLException {
/*     */     try {
/* 728 */       return this.rp.getPoolSize();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 732 */       logger.log(MLevel.WARNING, null, e);
/* 733 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public int getNumIdleConnections() throws SQLException {
/*     */     try {
/* 739 */       return this.rp.getAvailableCount();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 743 */       logger.log(MLevel.WARNING, null, e);
/* 744 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public int getNumBusyConnections()
/*     */     throws SQLException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 41	com/mchange/v2/c3p0/impl/C3P0PooledConnectionPool:rp	Lcom/mchange/v2/resourcepool/ResourcePool;
/*     */     //   4: dup
/*     */     //   5: astore_1
/*     */     //   6: monitorenter
/*     */     //   7: aload_0
/*     */     //   8: getfield 41	com/mchange/v2/c3p0/impl/C3P0PooledConnectionPool:rp	Lcom/mchange/v2/resourcepool/ResourcePool;
/*     */     //   11: invokeinterface 74 1 0
/*     */     //   16: aload_0
/*     */     //   17: getfield 41	com/mchange/v2/c3p0/impl/C3P0PooledConnectionPool:rp	Lcom/mchange/v2/resourcepool/ResourcePool;
/*     */     //   20: invokeinterface 75 1 0
/*     */     //   25: isub
/*     */     //   26: aload_1
/*     */     //   27: monitorexit
/*     */     //   28: ireturn
/*     */     //   29: astore_2
/*     */     //   30: aload_1
/*     */     //   31: monitorexit
/*     */     //   32: aload_2
/*     */     //   33: athrow
/*     */     //   34: astore_1
/*     */     //   35: getstatic 67	com/mchange/v2/c3p0/impl/C3P0PooledConnectionPool:logger	Lcom/mchange/v2/log/MLogger;
/*     */     //   38: getstatic 68	com/mchange/v2/log/MLevel:WARNING	Lcom/mchange/v2/log/MLevel;
/*     */     //   41: aconst_null
/*     */     //   42: aload_1
/*     */     //   43: invokeinterface 71 4 0
/*     */     //   48: aload_1
/*     */     //   49: invokestatic 43	com/mchange/v2/sql/SqlUtils:toSQLException	(Ljava/lang/Throwable;)Ljava/sql/SQLException;
/*     */     //   52: athrow
/*     */     // Line number table:
/*     */     //   Java source line #752	-> byte code offset #0
/*     */     //   Java source line #753	-> byte code offset #7
/*     */     //   Java source line #755	-> byte code offset #34
/*     */     //   Java source line #758	-> byte code offset #35
/*     */     //   Java source line #759	-> byte code offset #48
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	53	0	this	C3P0PooledConnectionPool
/*     */     //   5	26	1	Ljava/lang/Object;	Object
/*     */     //   34	15	1	e	Exception
/*     */     //   29	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   7	28	29	finally
/*     */     //   29	32	29	finally
/*     */     //   0	28	34	java/lang/Exception
/*     */     //   29	34	34	java/lang/Exception
/*     */   }
/*     */   
/*     */   public int getNumUnclosedOrphanedConnections()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 765 */       return this.rp.getExcludedCount();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 769 */       logger.log(MLevel.WARNING, null, e);
/* 770 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public long getStartTime() throws SQLException {
/*     */     try {
/* 776 */       return this.rp.getStartTime();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 780 */       logger.log(MLevel.WARNING, null, e);
/* 781 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public long getUpTime() throws SQLException {
/*     */     try {
/* 787 */       return this.rp.getUpTime();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 791 */       logger.log(MLevel.WARNING, null, e);
/* 792 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public long getNumFailedCheckins() throws SQLException {
/*     */     try {
/* 798 */       return this.rp.getNumFailedCheckins();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 802 */       logger.log(MLevel.WARNING, null, e);
/* 803 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public long getNumFailedCheckouts() throws SQLException {
/*     */     try {
/* 809 */       return this.rp.getNumFailedCheckouts();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 813 */       logger.log(MLevel.WARNING, null, e);
/* 814 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public long getNumFailedIdleTests() throws SQLException {
/*     */     try {
/* 820 */       return this.rp.getNumFailedIdleTests();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 824 */       logger.log(MLevel.WARNING, null, e);
/* 825 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public Throwable getLastCheckinFailure() throws SQLException {
/*     */     try {
/* 831 */       return this.rp.getLastCheckinFailure();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 835 */       logger.log(MLevel.WARNING, null, e);
/* 836 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public Throwable getLastCheckoutFailure() throws SQLException {
/*     */     try {
/* 842 */       return this.rp.getLastCheckoutFailure();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 846 */       logger.log(MLevel.WARNING, null, e);
/* 847 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public Throwable getLastIdleTestFailure() throws SQLException {
/*     */     try {
/* 853 */       return this.rp.getLastIdleCheckFailure();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 857 */       logger.log(MLevel.WARNING, null, e);
/* 858 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public Throwable getLastConnectionTestFailure() throws SQLException {
/*     */     try {
/* 864 */       return this.rp.getLastResourceTestFailure();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 868 */       logger.log(MLevel.WARNING, null, e);
/* 869 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public Throwable getLastAcquisitionFailure() throws SQLException {
/*     */     try {
/* 875 */       return this.rp.getLastAcquisitionFailure();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 879 */       logger.log(MLevel.WARNING, null, e);
/* 880 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 893 */       this.rp.resetPool();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 897 */       logger.log(MLevel.WARNING, null, e);
/* 898 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*     */   }
/*     */   
/*     */   static final class ThrowableHolderPool
/*     */   {
/* 904 */     LinkedList l = new LinkedList();
/*     */     
/*     */     synchronized Throwable[] getThrowableHolder()
/*     */     {
/* 908 */       if (this.l.size() == 0) {
/* 909 */         return new Throwable[1];
/*     */       }
/* 911 */       return (Throwable[])this.l.remove(0);
/*     */     }
/*     */     
/*     */     synchronized void returnThrowableHolder(Throwable[] th)
/*     */     {
/* 916 */       th[0] = null;
/* 917 */       this.l.add(th);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\C3P0PooledConnectionPool.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */