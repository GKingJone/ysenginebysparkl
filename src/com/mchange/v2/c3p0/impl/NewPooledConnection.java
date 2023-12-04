/*     */ package com.mchange.v2.c3p0.impl;
/*     */ 
/*     */ import com.mchange.v2.c3p0.ConnectionCustomizer;
/*     */ import com.mchange.v2.c3p0.ConnectionTester;
/*     */ import com.mchange.v2.c3p0.FullQueryConnectionTester;
/*     */ import com.mchange.v2.c3p0.stmt.GooGooStatementCache;
/*     */ import com.mchange.v2.c3p0.util.ConnectionEventSupport;
/*     */ import com.mchange.v2.lang.ObjectUtils;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import com.mchange.v2.sql.SqlUtils;
/*     */ import java.lang.reflect.Method;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.sql.ConnectionEventListener;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class NewPooledConnection
/*     */   extends AbstractC3P0PooledConnection
/*     */ {
/*  40 */   private static final MLogger logger = MLog.getLogger(NewPooledConnection.class);
/*     */   
/*  42 */   private static final SQLException NORMAL_CLOSE_PLACEHOLDER = new SQLException("This pooled Connection was explicitly close()ed by a client, not invalidated due to an error.");
/*     */   
/*     */ 
/*     */ 
/*  46 */   static Set holdabilityBugKeys = null;
/*     */   
/*     */   final Connection physicalConnection;
/*     */   
/*     */   final ConnectionTester connectionTester;
/*     */   
/*     */   final boolean autoCommitOnClose;
/*     */   
/*     */   final boolean forceIgnoreUnresolvedTransactions;
/*     */   final String preferredTestQuery;
/*     */   final boolean supports_setHoldability;
/*     */   final boolean supports_setReadOnly;
/*     */   final boolean supports_setTypeMap;
/*     */   final int dflt_txn_isolation;
/*     */   final String dflt_catalog;
/*     */   final int dflt_holdability;
/*     */   final boolean dflt_readOnly;
/*     */   final Map dflt_typeMap;
/*     */   final ConnectionEventSupport ces;
/*  65 */   GooGooStatementCache scache = null;
/*  66 */   Throwable invalidatingException = null;
/*  67 */   int connection_status = 0;
/*  68 */   Set uncachedActiveStatements = new HashSet();
/*  69 */   Map resultSetsForStatements = new HashMap();
/*  70 */   Set metaDataResultSets = new HashSet();
/*  71 */   Set rawConnectionResultSets = null;
/*  72 */   boolean connection_error_signaled = false;
/*     */   
/*     */ 
/*  75 */   volatile NewProxyConnection exposedProxy = null;
/*  76 */   volatile boolean isolation_lvl_nondefault = false;
/*  77 */   volatile boolean catalog_nondefault = false;
/*  78 */   volatile boolean holdability_nondefault = false;
/*  79 */   volatile boolean readOnly_nondefault = false;
/*  80 */   volatile boolean typeMap_nondefault = false;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public NewPooledConnection(Connection con, ConnectionTester connectionTester, boolean autoCommitOnClose, boolean forceIgnoreUnresolvedTransactions, String preferredTestQuery, ConnectionCustomizer cc, String pdsIdt)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/*  93 */       if (cc != null) {
/*  94 */         cc.onAcquire(con, pdsIdt);
/*     */       }
/*     */     } catch (Exception e) {
/*  97 */       throw SqlUtils.toSQLException(e);
/*     */     }
/*  99 */     this.physicalConnection = con;
/* 100 */     this.connectionTester = connectionTester;
/* 101 */     this.autoCommitOnClose = autoCommitOnClose;
/* 102 */     this.forceIgnoreUnresolvedTransactions = forceIgnoreUnresolvedTransactions;
/* 103 */     this.preferredTestQuery = preferredTestQuery;
/* 104 */     this.supports_setHoldability = C3P0ImplUtils.supportsMethod(con, "setHoldability", new Class[] { Integer.TYPE });
/* 105 */     this.supports_setReadOnly = C3P0ImplUtils.supportsMethod(con, "setReadOnly", new Class[] { Boolean.TYPE });
/* 106 */     this.supports_setTypeMap = C3P0ImplUtils.supportsMethod(con, "setTypeMap", new Class[] { Map.class });
/* 107 */     this.dflt_txn_isolation = con.getTransactionIsolation();
/* 108 */     this.dflt_catalog = con.getCatalog();
/* 109 */     this.dflt_holdability = (this.supports_setHoldability ? carefulCheckHoldability(con) : 2);
/* 110 */     this.dflt_readOnly = (this.supports_setReadOnly ? carefulCheckReadOnly(con) : false);
/* 111 */     this.dflt_typeMap = ((this.supports_setTypeMap) && (carefulCheckTypeMap(con) == null) ? null : Collections.EMPTY_MAP);
/* 112 */     this.ces = new ConnectionEventSupport(this);
/*     */   }
/*     */   
/*     */   private static int carefulCheckHoldability(Connection con) {
/*     */     try {
/* 117 */       return con.getHoldability();
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 127 */       return 2;
/*     */     }
/*     */     catch (Error e)
/*     */     {
/* 131 */       synchronized (NewPooledConnection.class)
/*     */       {
/* 133 */         if (holdabilityBugKeys == null)
/* 134 */           holdabilityBugKeys = new HashSet();
/* 135 */         String hbk = holdabilityBugKey(con, e);
/* 136 */         if (!holdabilityBugKeys.contains(hbk))
/*     */         {
/* 138 */           if (logger.isLoggable(MLevel.WARNING)) {
/* 139 */             logger.log(MLevel.WARNING, con + " threw an Error when we tried to check its default " + "holdability. This is probably due to a bug in your JDBC driver that c3p0 can harmlessly " + "work around (reported for some DB2 drivers). Please verify that the error stack trace is consistent" + "with the getHoldability() method not being properly implemented, and is not due to some deeper problem. " + "This message will not be repeated for Connections of type " + con.getClass().getName() + " that " + "provoke errors of type " + e.getClass().getName() + " when getHoldability() is called.", e);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 145 */           holdabilityBugKeys.add(hbk);
/*     */         }
/*     */       } }
/* 148 */     return 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 153 */   private static String holdabilityBugKey(Connection con, Error err) { return con.getClass().getName() + '|' + err.getClass().getName(); }
/*     */   
/*     */   private static boolean carefulCheckReadOnly(Connection con) {
/*     */     try {
/* 157 */       return con.isReadOnly();
/*     */     }
/*     */     catch (Exception e) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 167 */     return false;
/*     */   }
/*     */   
/*     */   private static Map carefulCheckTypeMap(Connection con)
/*     */   {
/*     */     try {
/* 173 */       return con.getTypeMap();
/*     */     }
/*     */     catch (Exception e) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 183 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized Connection getConnection()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 192 */       if (this.exposedProxy == null)
/*     */       {
/* 194 */         this.exposedProxy = new NewProxyConnection(this.physicalConnection, this);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/* 202 */       else if (logger.isLoggable(MLevel.WARNING)) {
/* 203 */         logger.warning("c3p0 -- Uh oh... getConnection() was called on a PooledConnection when it had already provided a client with a Connection that has not yet been closed. This probably indicates a bug in the connection pool!!!");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 208 */       return this.exposedProxy;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 212 */       SQLException sqle = handleThrowable(e);
/* 213 */       throw sqle;
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized int getConnectionStatus() {
/* 218 */     return this.connection_status;
/*     */   }
/*     */   
/*     */   public synchronized void closeAll() throws SQLException
/*     */   {
/*     */     try {
/* 224 */       closeAllCachedStatements();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 228 */       SQLException sqle = handleThrowable(e);
/* 229 */       throw sqle;
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void close() throws SQLException {
/* 234 */     close(null);
/*     */   }
/*     */   
/* 237 */   public void addConnectionEventListener(ConnectionEventListener cel) { this.ces.addConnectionEventListener(cel); }
/*     */   
/*     */   public void removeConnectionEventListener(ConnectionEventListener cel) {
/* 240 */     this.ces.removeConnectionEventListener(cel);
/*     */   }
/*     */   
/*     */   public synchronized void initStatementCache(GooGooStatementCache scache) {
/* 244 */     this.scache = scache;
/*     */   }
/*     */   
/* 247 */   public synchronized GooGooStatementCache getStatementCache() { return this.scache; }
/*     */   
/*     */ 
/*     */   void markNewTxnIsolation(int lvl)
/*     */   {
/* 252 */     this.isolation_lvl_nondefault = (lvl != this.dflt_txn_isolation);
/*     */   }
/*     */   
/*     */ 
/*     */   void markNewCatalog(String catalog)
/*     */   {
/* 258 */     this.catalog_nondefault = ObjectUtils.eqOrBothNull(catalog, this.dflt_catalog);
/*     */   }
/*     */   
/*     */   void markNewHoldability(int holdability)
/*     */   {
/* 263 */     this.holdability_nondefault = (holdability != this.dflt_holdability);
/*     */   }
/*     */   
/*     */   void markNewReadOnly(boolean readOnly)
/*     */   {
/* 268 */     this.readOnly_nondefault = (readOnly != this.dflt_readOnly);
/*     */   }
/*     */   
/*     */   void markNewTypeMap(Map typeMap)
/*     */   {
/* 273 */     this.typeMap_nondefault = (typeMap != this.dflt_typeMap);
/*     */   }
/*     */   
/*     */   synchronized Object checkoutStatement(Method stmtProducingMethod, Object[] args) throws SQLException {
/* 277 */     return this.scache.checkoutStatement(this.physicalConnection, stmtProducingMethod, args);
/*     */   }
/*     */   
/*     */   synchronized void checkinStatement(Statement stmt) throws SQLException {
/* 281 */     cleanupStatementResultSets(stmt);
/* 282 */     this.scache.checkinStatement(stmt);
/*     */   }
/*     */   
/*     */   synchronized void markActiveUncachedStatement(Statement stmt) {
/* 286 */     this.uncachedActiveStatements.add(stmt);
/*     */   }
/*     */   
/*     */   synchronized void markInactiveUncachedStatement(Statement stmt) {
/* 290 */     cleanupStatementResultSets(stmt);
/* 291 */     this.uncachedActiveStatements.remove(stmt);
/*     */   }
/*     */   
/*     */   synchronized void markActiveResultSetForStatement(Statement stmt, ResultSet rs)
/*     */   {
/* 296 */     Set rss = resultSets(stmt, true);
/* 297 */     rss.add(rs);
/*     */   }
/*     */   
/*     */   synchronized void markInactiveResultSetForStatement(Statement stmt, ResultSet rs)
/*     */   {
/* 302 */     Set rss = resultSets(stmt, false);
/* 303 */     if (rss == null)
/*     */     {
/* 305 */       if (logger.isLoggable(MLevel.FINE)) {
/* 306 */         logger.fine("ResultSet " + rs + " was apparently closed after the Statement that created it had already been closed.");
/*     */       }
/* 308 */     } else if (!rss.remove(rs)) {
/* 309 */       throw new InternalError("Marking a ResultSet inactive that we did not know was opened!");
/*     */     }
/*     */   }
/*     */   
/*     */   synchronized void markActiveRawConnectionResultSet(ResultSet rs) {
/* 314 */     if (this.rawConnectionResultSets == null)
/* 315 */       this.rawConnectionResultSets = new HashSet();
/* 316 */     this.rawConnectionResultSets.add(rs);
/*     */   }
/*     */   
/*     */   synchronized void markInactiveRawConnectionResultSet(ResultSet rs)
/*     */   {
/* 321 */     if (!this.rawConnectionResultSets.remove(rs))
/* 322 */       throw new InternalError("Marking a raw Connection ResultSet inactive that we did not know was opened!");
/*     */   }
/*     */   
/*     */   synchronized void markActiveMetaDataResultSet(ResultSet rs) {
/* 326 */     this.metaDataResultSets.add(rs);
/*     */   }
/*     */   
/* 329 */   synchronized void markInactiveMetaDataResultSet(ResultSet rs) { this.metaDataResultSets.remove(rs); }
/*     */   
/*     */ 
/*     */   void markClosedProxyConnection(NewProxyConnection npc, boolean txn_known_resolved)
/*     */   {
/* 334 */     SQLException trouble = null;
/*     */     try
/*     */     {
/* 337 */       synchronized (this)
/*     */       {
/*     */         try
/*     */         {
/* 341 */           if (npc != this.exposedProxy) {
/* 342 */             throw new InternalError("C3P0 Error: An exposed proxy asked a PooledConnection that was not its parents to clean up its resources!");
/*     */           }
/* 344 */           List closeExceptions = new LinkedList();
/* 345 */           cleanupResultSets(closeExceptions);
/* 346 */           cleanupUncachedStatements(closeExceptions);
/* 347 */           checkinAllCachedStatements(closeExceptions);
/* 348 */           Iterator ii; if (closeExceptions.size() > 0)
/*     */           {
/*     */ 
/* 351 */             if (logger.isLoggable(MLevel.INFO))
/* 352 */               logger.info("[c3p0] The following Exceptions occurred while trying to clean up a Connection's stranded resources:");
/* 353 */             for (ii = closeExceptions.iterator(); ii.hasNext();)
/*     */             {
/* 355 */               Throwable t = (Throwable)ii.next();
/*     */               
/*     */ 
/* 358 */               if (logger.isLoggable(MLevel.INFO))
/* 359 */                 logger.log(MLevel.INFO, "[c3p0 -- conection resource close Exception]", t);
/*     */             }
/*     */           }
/* 362 */           reset(txn_known_resolved);
/*     */ 
/*     */         }
/*     */         catch (SQLException e)
/*     */         {
/* 367 */           if (logger.isLoggable(MLevel.FINE)) {
/* 368 */             logger.log(MLevel.FINE, "An exception occurred while reseting a closed Connection. Invalidating Connection.", e);
/*     */           }
/* 370 */           updateConnectionStatus(-1);
/*     */         }
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 376 */       if (trouble != null) {
/* 377 */         fireConnectionErrorOccurred(trouble);
/*     */       }
/*     */       else {
/* 380 */         this.exposedProxy = null;
/* 381 */         fireConnectionClosed();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void reset(boolean txn_known_resolved) throws SQLException
/*     */   {
/* 388 */     C3P0ImplUtils.resetTxnState(this.physicalConnection, this.forceIgnoreUnresolvedTransactions, this.autoCommitOnClose, txn_known_resolved);
/* 389 */     if (this.isolation_lvl_nondefault)
/*     */     {
/* 391 */       this.physicalConnection.setTransactionIsolation(this.dflt_txn_isolation);
/* 392 */       this.isolation_lvl_nondefault = false;
/*     */     }
/*     */     
/* 395 */     if (this.catalog_nondefault)
/*     */     {
/* 397 */       this.physicalConnection.setCatalog(this.dflt_catalog);
/* 398 */       this.catalog_nondefault = false;
/*     */     }
/* 400 */     if (this.holdability_nondefault)
/*     */     {
/* 402 */       this.physicalConnection.setHoldability(this.dflt_holdability);
/* 403 */       this.holdability_nondefault = false;
/*     */     }
/* 405 */     if (this.readOnly_nondefault)
/*     */     {
/* 407 */       this.physicalConnection.setReadOnly(this.dflt_readOnly);
/* 408 */       this.readOnly_nondefault = false;
/*     */     }
/* 410 */     if (this.typeMap_nondefault)
/*     */     {
/* 412 */       this.physicalConnection.setTypeMap(this.dflt_typeMap);
/* 413 */       this.typeMap_nondefault = false;
/*     */     }
/*     */   }
/*     */   
/*     */   synchronized boolean isStatementCaching() {
/* 418 */     return this.scache != null;
/*     */   }
/*     */   
/*     */   SQLException handleThrowable(Throwable t)
/*     */   {
/* 423 */     boolean fire_cxn_error = false;
/* 424 */     SQLException sqle = null;
/*     */     try
/*     */     {
/* 427 */       synchronized (this)
/*     */       {
/* 429 */         if (logger.isLoggable(MLevel.FINER)) {
/* 430 */           logger.log(MLevel.FINER, this + " handling a throwable.", t);
/*     */         }
/* 432 */         sqle = SqlUtils.toSQLException(t);
/*     */         
/*     */         int status;
/*     */         int status;
/* 436 */         if ((this.connectionTester instanceof FullQueryConnectionTester)) {
/* 437 */           status = ((FullQueryConnectionTester)this.connectionTester).statusOnException(this.physicalConnection, sqle, this.preferredTestQuery);
/*     */         } else {
/* 439 */           status = this.connectionTester.statusOnException(this.physicalConnection, sqle);
/*     */         }
/* 441 */         updateConnectionStatus(status);
/* 442 */         if (status != 0)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 448 */           if (logger.isLoggable(MLevel.FINE)) {
/* 449 */             logger.log(MLevel.FINE, this + " invalidated by Exception.", t);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 478 */           if (!this.connection_error_signaled) {
/* 479 */             fire_cxn_error = true;
/*     */ 
/*     */ 
/*     */ 
/*     */           }
/* 484 */           else if (logger.isLoggable(MLevel.WARNING))
/*     */           {
/* 486 */             logger.log(MLevel.WARNING, "[c3p0] A PooledConnection that has already signalled a Connection error is still in use!");
/* 487 */             logger.log(MLevel.WARNING, "[c3p0] Another error has occurred [ " + t + " ] which will not be reported to listeners!", t);
/*     */           }
/*     */           
/*     */         }
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 495 */       if (fire_cxn_error)
/*     */       {
/* 497 */         fireConnectionErrorOccurred(sqle);
/* 498 */         this.connection_error_signaled = true;
/*     */       }
/*     */     }
/* 501 */     return sqle;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void fireConnectionClosed()
/*     */   {
/* 509 */     assert (!Thread.holdsLock(this));
/* 510 */     this.ces.fireConnectionClosed();
/*     */   }
/*     */   
/*     */ 
/*     */   private void fireConnectionErrorOccurred(SQLException error)
/*     */   {
/* 516 */     assert (!Thread.holdsLock(this));
/* 517 */     this.ces.fireConnectionErrorOccurred(error);
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
/*     */   private void close(Throwable cause)
/*     */     throws SQLException
/*     */   {
/* 531 */     if (this.invalidatingException == null)
/*     */     {
/* 533 */       List closeExceptions = new LinkedList();
/*     */       
/*     */ 
/* 536 */       cleanupResultSets(closeExceptions);
/*     */       
/*     */ 
/* 539 */       cleanupUncachedStatements(closeExceptions);
/*     */       
/*     */       try
/*     */       {
/* 543 */         closeAllCachedStatements();
/*     */       } catch (SQLException e) {
/* 545 */         closeExceptions.add(e);
/*     */       }
/*     */       try
/*     */       {
/* 549 */         this.physicalConnection.close();
/*     */       }
/*     */       catch (SQLException e) {
/* 552 */         if (logger.isLoggable(MLevel.FINER)) {
/* 553 */           logger.log(MLevel.FINER, "Failed to close physical Connection: " + this.physicalConnection, e);
/*     */         }
/* 555 */         closeExceptions.add(e);
/*     */       }
/*     */       
/*     */ 
/* 559 */       if (this.connection_status == 0)
/* 560 */         this.connection_status = -1;
/* 561 */       if (cause == null)
/*     */       {
/* 563 */         this.invalidatingException = NORMAL_CLOSE_PLACEHOLDER;
/*     */         
/* 565 */         if (logger.isLoggable(MLevel.FINEST)) {
/* 566 */           logger.log(MLevel.FINEST, this + " closed by a client.", new Exception("DEBUG -- CLOSE BY CLIENT STACK TRACE"));
/*     */         }
/* 568 */         logCloseExceptions(null, closeExceptions);
/*     */         
/* 570 */         if (closeExceptions.size() > 0) {
/* 571 */           throw new SQLException("Some resources failed to close properly while closing " + this);
/*     */         }
/*     */       }
/*     */       else {
/* 575 */         this.invalidatingException = cause;
/*     */         
/* 577 */         logCloseExceptions(cause, closeExceptions);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void cleanupResultSets(List closeExceptions)
/*     */   {
/* 586 */     cleanupAllStatementResultSets(closeExceptions);
/* 587 */     cleanupUnclosedResultSetsSet(this.metaDataResultSets, closeExceptions);
/* 588 */     if (this.rawConnectionResultSets != null) {
/* 589 */       cleanupUnclosedResultSetsSet(this.rawConnectionResultSets, closeExceptions);
/*     */     }
/*     */   }
/*     */   
/*     */   private void cleanupUnclosedResultSetsSet(Set rsSet, List closeExceptions) {
/* 594 */     for (Iterator ii = rsSet.iterator(); ii.hasNext();)
/*     */     {
/* 596 */       ResultSet rs = (ResultSet)ii.next();
/*     */       try {
/* 598 */         rs.close();
/*     */       } catch (SQLException e) {
/* 600 */         closeExceptions.add(e);
/*     */       }
/* 602 */       ii.remove();
/*     */     }
/*     */   }
/*     */   
/*     */   private void cleanupStatementResultSets(Statement stmt)
/*     */   {
/* 608 */     Set rss = resultSets(stmt, false);
/* 609 */     Iterator ii; if (rss != null)
/*     */     {
/* 611 */       for (ii = rss.iterator(); ii.hasNext();) {
/*     */         try
/*     */         {
/* 614 */           ((ResultSet)ii.next()).close();
/*     */ 
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 619 */           if (logger.isLoggable(MLevel.INFO))
/* 620 */             logger.log(MLevel.INFO, "ResultSet close() failed.", e);
/*     */         }
/*     */       }
/*     */     }
/* 624 */     this.resultSetsForStatements.remove(stmt);
/*     */   }
/*     */   
/*     */   private void cleanupAllStatementResultSets(List closeExceptions)
/*     */   {
/* 629 */     for (Iterator ii = this.resultSetsForStatements.keySet().iterator(); ii.hasNext();)
/*     */     {
/* 631 */       Object stmt = ii.next();
/* 632 */       Set rss = (Set)this.resultSetsForStatements.get(stmt);
/* 633 */       for (jj = rss.iterator(); jj.hasNext();)
/*     */       {
/* 635 */         ResultSet rs = (ResultSet)jj.next();
/*     */         try {
/* 637 */           rs.close();
/*     */         } catch (SQLException e) {
/* 639 */           closeExceptions.add(e);
/*     */         } } }
/*     */     Iterator jj;
/* 642 */     this.resultSetsForStatements.clear();
/*     */   }
/*     */   
/*     */   private void cleanupUncachedStatements(List closeExceptions)
/*     */   {
/* 647 */     for (Iterator ii = this.uncachedActiveStatements.iterator(); ii.hasNext();)
/*     */     {
/* 649 */       Statement stmt = (Statement)ii.next();
/*     */       try {
/* 651 */         stmt.close();
/*     */       } catch (SQLException e) {
/* 653 */         closeExceptions.add(e);
/*     */       }
/* 655 */       ii.remove();
/*     */     }
/*     */   }
/*     */   
/*     */   private void checkinAllCachedStatements(List closeExceptions)
/*     */   {
/*     */     try
/*     */     {
/* 663 */       if (this.scache != null) {
/* 664 */         this.scache.checkinAll(this.physicalConnection);
/*     */       }
/*     */     } catch (SQLException e) {
/* 667 */       closeExceptions.add(e);
/*     */     }
/*     */   }
/*     */   
/*     */   private void closeAllCachedStatements() throws SQLException {
/* 672 */     if (this.scache != null) {
/* 673 */       this.scache.closeAll(this.physicalConnection);
/*     */     }
/*     */   }
/*     */   
/*     */   private void updateConnectionStatus(int status) {
/* 678 */     switch (this.connection_status)
/*     */     {
/*     */     case -8: 
/*     */       break;
/*     */     
/*     */     case -1: 
/* 684 */       if (status == -8)
/* 685 */         this.connection_status = status;
/*     */       break;
/*     */     case 0: 
/* 688 */       if (status != 0)
/* 689 */         this.connection_status = status;
/*     */       break;
/*     */     default: 
/* 692 */       throw new InternalError(this + " -- Illegal Connection Status: " + this.connection_status);
/*     */     }
/*     */   }
/*     */   
/*     */   private Set resultSets(Statement stmt, boolean create)
/*     */   {
/* 698 */     Set out = (Set)this.resultSetsForStatements.get(stmt);
/* 699 */     if ((out == null) && (create))
/*     */     {
/* 701 */       out = new HashSet();
/* 702 */       this.resultSetsForStatements.put(stmt, out);
/*     */     }
/* 704 */     return out;
/*     */   }
/*     */   
/*     */   Connection getPhysicalConnection()
/*     */   {
/* 709 */     return this.physicalConnection;
/*     */   }
/*     */   
/*     */   private static void logCloseExceptions(Throwable cause, Collection exceptions) {
/*     */     Iterator ii;
/* 714 */     if (logger.isLoggable(MLevel.INFO))
/*     */     {
/* 716 */       if (cause != null)
/*     */       {
/*     */ 
/*     */ 
/* 720 */         logger.log(MLevel.INFO, "[c3p0] A PooledConnection died due to the following error!", cause);
/*     */       }
/* 722 */       if ((exceptions != null) && (exceptions.size() > 0))
/*     */       {
/* 724 */         if (cause == null) {
/* 725 */           logger.info("[c3p0] Exceptions occurred while trying to close a PooledConnection's resources normally.");
/*     */         }
/*     */         else {
/* 728 */           logger.info("[c3p0] Exceptions occurred while trying to close a Broken PooledConnection.");
/*     */         }
/* 730 */         for (ii = exceptions.iterator(); ii.hasNext();)
/*     */         {
/* 732 */           Throwable t = (Throwable)ii.next();
/*     */           
/*     */ 
/* 735 */           logger.log(MLevel.INFO, "[c3p0] NewPooledConnection close Exception.", t);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\NewPooledConnection.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */