/*      */ package com.mchange.v2.c3p0.impl;
/*      */ 
/*      */ import com.mchange.v2.c3p0.C3P0ProxyConnection;
/*      */ import com.mchange.v2.c3p0.C3P0ProxyStatement;
/*      */ import com.mchange.v2.c3p0.ConnectionCustomizer;
/*      */ import com.mchange.v2.c3p0.ConnectionTester;
/*      */ import com.mchange.v2.c3p0.stmt.GooGooStatementCache;
/*      */ import com.mchange.v2.c3p0.util.ConnectionEventSupport;
/*      */ import com.mchange.v2.lang.ObjectUtils;
/*      */ import com.mchange.v2.log.MLevel;
/*      */ import com.mchange.v2.log.MLog;
/*      */ import com.mchange.v2.log.MLogger;
/*      */ import com.mchange.v2.sql.SqlUtils;
/*      */ import com.mchange.v2.sql.filter.FilterCallableStatement;
/*      */ import com.mchange.v2.sql.filter.FilterPreparedStatement;
/*      */ import com.mchange.v2.sql.filter.FilterStatement;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.InvocationHandler;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Proxy;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.Connection;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Statement;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import javax.sql.ConnectionEventListener;
/*      */ 
/*      */ 
/*      */ public final class C3P0PooledConnection
/*      */   extends AbstractC3P0PooledConnection
/*      */ {
/*   41 */   static final MLogger logger = MLog.getLogger(C3P0PooledConnection.class);
/*      */   
/*   43 */   static final Class[] PROXY_CTOR_ARGS = { InvocationHandler.class };
/*      */   
/*      */   static final Constructor CON_PROXY_CTOR;
/*      */   
/*      */   static final Method RS_CLOSE_METHOD;
/*      */   static final Method STMT_CLOSE_METHOD;
/*      */   static final Object[] CLOSE_ARGS;
/*      */   static final Set OBJECT_METHODS;
/*      */   final ConnectionTester connectionTester;
/*      */   
/*      */   /**
/*      */    * @deprecated
/*      */    */
/*      */   private static Constructor createProxyConstructor(Class intfc)
/*      */     throws NoSuchMethodException
/*      */   {
/*   59 */     Class[] proxyInterfaces = { intfc };
/*   60 */     Class proxyCl = Proxy.getProxyClass(C3P0PooledConnection.class.getClassLoader(), proxyInterfaces);
/*   61 */     return proxyCl.getConstructor(PROXY_CTOR_ARGS);
/*      */   }
/*      */   
/*      */   static
/*      */   {
/*      */     try
/*      */     {
/*   68 */       CON_PROXY_CTOR = createProxyConstructor(ProxyConnection.class);
/*      */       
/*   70 */       Class[] argClasses = new Class[0];
/*   71 */       RS_CLOSE_METHOD = ResultSet.class.getMethod("close", argClasses);
/*   72 */       STMT_CLOSE_METHOD = Statement.class.getMethod("close", argClasses);
/*      */       
/*   74 */       CLOSE_ARGS = new Object[0];
/*      */       
/*   76 */       OBJECT_METHODS = Collections.unmodifiableSet(new HashSet(Arrays.asList(Object.class.getMethods())));
/*      */ 
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*   81 */       logger.log(MLevel.SEVERE, "An Exception occurred in static initializer of" + C3P0PooledConnection.class.getName(), e);
/*   82 */       throw new InternalError("Something is very wrong, or this is a pre 1.3 JVM.We cannot set up dynamic proxies and/or methods!");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   final boolean autoCommitOnClose;
/*      */   
/*      */   final boolean forceIgnoreUnresolvedTransactions;
/*      */   
/*      */   final boolean supports_setTypeMap;
/*      */   
/*      */   final boolean supports_setHoldability;
/*      */   
/*      */   final int dflt_txn_isolation;
/*      */   final String dflt_catalog;
/*      */   final int dflt_holdability;
/*   98 */   final ConnectionEventSupport ces = new ConnectionEventSupport(this);
/*      */   
/*      */   volatile Connection physicalConnection;
/*      */   
/*  102 */   volatile Exception invalidatingException = null;
/*      */   
/*      */ 
/*      */ 
/*      */   ProxyConnection exposedProxy;
/*      */   
/*      */ 
/*  109 */   int connection_status = 0;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  117 */   final Set uncachedActiveStatements = Collections.synchronizedSet(new HashSet());
/*      */   
/*      */   volatile GooGooStatementCache scache;
/*      */   
/*  121 */   volatile boolean isolation_lvl_nondefault = false;
/*  122 */   volatile boolean catalog_nondefault = false;
/*  123 */   volatile boolean holdability_nondefault = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public C3P0PooledConnection(Connection con, ConnectionTester connectionTester, boolean autoCommitOnClose, boolean forceIgnoreUnresolvedTransactions, ConnectionCustomizer cc, String pdsIdt)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  134 */       if (cc != null) {
/*  135 */         cc.onAcquire(con, pdsIdt);
/*      */       }
/*      */     } catch (Exception e) {
/*  138 */       throw SqlUtils.toSQLException(e);
/*      */     }
/*  140 */     this.physicalConnection = con;
/*  141 */     this.connectionTester = connectionTester;
/*  142 */     this.autoCommitOnClose = autoCommitOnClose;
/*  143 */     this.forceIgnoreUnresolvedTransactions = forceIgnoreUnresolvedTransactions;
/*  144 */     this.supports_setTypeMap = C3P0ImplUtils.supportsMethod(con, "setTypeMap", new Class[] { Map.class });
/*  145 */     this.supports_setHoldability = C3P0ImplUtils.supportsMethod(con, "setHoldability", new Class[] { Integer.TYPE });
/*  146 */     this.dflt_txn_isolation = con.getTransactionIsolation();
/*  147 */     this.dflt_catalog = con.getCatalog();
/*  148 */     this.dflt_holdability = (this.supports_setHoldability ? con.getHoldability() : 2);
/*      */   }
/*      */   
/*      */   Connection getPhysicalConnection()
/*      */   {
/*  153 */     return this.physicalConnection;
/*      */   }
/*      */   
/*  156 */   boolean isClosed() throws SQLException { return this.physicalConnection == null; }
/*      */   
/*      */   void initStatementCache(GooGooStatementCache scache) {
/*  159 */     this.scache = scache;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public synchronized Connection getConnection()
/*      */     throws SQLException
/*      */   {
/*  169 */     if (this.exposedProxy != null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  180 */       logger.warning("c3p0 -- Uh oh... getConnection() was called on a PooledConnection when it had already provided a client with a Connection that has not yet been closed. This probably indicates a bug in the connection pool!!!");
/*      */       
/*      */ 
/*      */ 
/*  184 */       return this.exposedProxy;
/*      */     }
/*      */     
/*  187 */     return getCreateNewConnection();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Connection getCreateNewConnection()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  200 */       ensureOkay();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  206 */       return this.exposedProxy = createProxyConnection();
/*      */     }
/*      */     catch (SQLException e) {
/*  209 */       throw e;
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*  213 */       logger.log(MLevel.WARNING, "Failed to acquire connection!", e);
/*  214 */       throw new SQLException("Failed to acquire connection!");
/*      */     }
/*      */   }
/*      */   
/*      */   public void closeAll() throws SQLException
/*      */   {
/*  220 */     if (this.scache != null)
/*  221 */       this.scache.closeAll(this.physicalConnection);
/*      */   }
/*      */   
/*      */   public void close() throws SQLException {
/*  225 */     close(false);
/*      */   }
/*      */   
/*      */   private synchronized void close(boolean known_invalid)
/*      */     throws SQLException
/*      */   {
/*  231 */     if (this.physicalConnection != null)
/*      */     {
/*      */       try
/*      */       {
/*  235 */         StringBuffer debugOnlyLog = null;
/*  236 */         if (known_invalid)
/*      */         {
/*  238 */           debugOnlyLog = new StringBuffer();
/*  239 */           debugOnlyLog.append("[ exceptions: ");
/*      */         }
/*      */         
/*  242 */         Exception exc = cleanupUncachedActiveStatements();
/*  243 */         if (exc != null)
/*      */         {
/*  245 */           if (known_invalid) {
/*  246 */             debugOnlyLog.append(exc.toString() + ' ');
/*      */           } else {
/*  248 */             logger.log(MLevel.WARNING, "An exception occurred while cleaning up uncached active Statements.", exc);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/*  262 */           if (this.exposedProxy != null) {
/*  263 */             this.exposedProxy.silentClose(known_invalid);
/*      */           }
/*      */           
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/*  269 */           if (known_invalid) {
/*  270 */             debugOnlyLog.append(e.toString() + ' ');
/*      */           } else {
/*  272 */             logger.log(MLevel.WARNING, "An exception occurred.", exc);
/*      */           }
/*      */           
/*  275 */           exc = e;
/*      */         }
/*      */         try {
/*  278 */           closeAll();
/*      */ 
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/*  283 */           if (known_invalid) {
/*  284 */             debugOnlyLog.append(e.toString() + ' ');
/*      */           } else {
/*  286 */             logger.log(MLevel.WARNING, "An exception occurred.", exc);
/*      */           }
/*      */           
/*  289 */           exc = e;
/*      */         }
/*      */         try {
/*  292 */           this.physicalConnection.close();
/*      */ 
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/*  297 */           if (known_invalid) {
/*  298 */             debugOnlyLog.append(e.toString() + ' ');
/*      */           } else
/*  300 */             logger.log(MLevel.WARNING, "An exception occurred.", exc);
/*  301 */           e.printStackTrace();
/*      */           
/*  303 */           exc = e;
/*      */         }
/*      */         
/*  306 */         if (exc != null)
/*      */         {
/*  308 */           if (known_invalid)
/*      */           {
/*  310 */             debugOnlyLog.append(" ]");
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  318 */             logger.fine(this + ": while closing a PooledConnection known to be invalid, " + "  some exceptions occurred. This is probably not a problem: " + debugOnlyLog.toString());
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/*  324 */             throw new SQLException("At least one error occurred while attempting to close() the PooledConnection: " + exc);
/*      */           }
/*      */         }
/*      */         
/*  328 */         logger.fine("C3P0PooledConnection closed. [" + this + ']');
/*      */       }
/*      */       finally
/*      */       {
/*  332 */         this.physicalConnection = null;
/*      */       } }
/*      */   }
/*      */   
/*      */   public void addConnectionEventListener(ConnectionEventListener listener) {
/*  337 */     this.ces.addConnectionEventListener(listener);
/*      */   }
/*      */   
/*  340 */   public void removeConnectionEventListener(ConnectionEventListener listener) { this.ces.removeConnectionEventListener(listener); }
/*      */   
/*      */   private void reset() throws SQLException {
/*  343 */     reset(false);
/*      */   }
/*      */   
/*      */   private void reset(boolean known_resolved_txn) throws SQLException {
/*  347 */     ensureOkay();
/*  348 */     C3P0ImplUtils.resetTxnState(this.physicalConnection, this.forceIgnoreUnresolvedTransactions, this.autoCommitOnClose, known_resolved_txn);
/*  349 */     if (this.isolation_lvl_nondefault)
/*      */     {
/*  351 */       this.physicalConnection.setTransactionIsolation(this.dflt_txn_isolation);
/*  352 */       this.isolation_lvl_nondefault = false;
/*      */     }
/*  354 */     if (this.catalog_nondefault)
/*      */     {
/*  356 */       this.physicalConnection.setCatalog(this.dflt_catalog);
/*  357 */       this.catalog_nondefault = false;
/*      */     }
/*  359 */     if (this.holdability_nondefault)
/*      */     {
/*  361 */       this.physicalConnection.setHoldability(this.dflt_holdability);
/*  362 */       this.holdability_nondefault = false;
/*      */     }
/*      */     try
/*      */     {
/*  366 */       this.physicalConnection.setReadOnly(false);
/*      */     }
/*      */     catch (Throwable t) {
/*  369 */       if (logger.isLoggable(MLevel.FINE)) {
/*  370 */         logger.log(MLevel.FINE, "A Throwable occurred while trying to reset the readOnly property of our Connection to false!", t);
/*      */       }
/*      */     }
/*      */     try {
/*  374 */       if (this.supports_setTypeMap) this.physicalConnection.setTypeMap(Collections.EMPTY_MAP);
/*      */     }
/*      */     catch (Throwable t) {
/*  377 */       if (logger.isLoggable(MLevel.FINE)) {
/*  378 */         logger.log(MLevel.FINE, "A Throwable occurred while trying to reset the typeMap property of our Connection to Collections.EMPTY_MAP!", t);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   boolean closeAndRemoveResultSets(Set rsSet) {
/*  384 */     boolean okay = true;
/*  385 */     Iterator ii; synchronized (rsSet)
/*      */     {
/*  387 */       for (ii = rsSet.iterator(); ii.hasNext();)
/*      */       {
/*  389 */         ResultSet rs = (ResultSet)ii.next();
/*      */         try {
/*  391 */           rs.close();
/*      */         }
/*      */         catch (SQLException e)
/*      */         {
/*  395 */           logger.log(MLevel.WARNING, "An exception occurred while cleaning up a ResultSet.", e);
/*      */           
/*  397 */           okay = false;
/*      */         }
/*      */         finally {
/*  400 */           ii.remove();
/*      */         }
/*      */       } }
/*  403 */     return okay;
/*      */   }
/*      */   
/*      */   void ensureOkay() throws SQLException
/*      */   {
/*  408 */     if (this.physicalConnection == null) {
/*  409 */       throw new SQLException("Connection is broken. Invalidating Exception: " + this.invalidatingException.toString());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   boolean closeAndRemoveResourcesInSet(Set s, Method closeMethod)
/*      */   {
/*  416 */     boolean okay = true;
/*      */     
/*      */     Set temp;
/*  419 */     synchronized (s) {
/*  420 */       temp = new HashSet(s);
/*      */     }
/*  422 */     for (Iterator ii = temp.iterator(); ii.hasNext();)
/*      */     {
/*  424 */       Object rsrc = ii.next();
/*      */       try {
/*  426 */         closeMethod.invoke(rsrc, CLOSE_ARGS);
/*      */       }
/*      */       catch (Exception e) {
/*  429 */         Throwable t = e;
/*  430 */         if ((t instanceof InvocationTargetException))
/*  431 */           t = ((InvocationTargetException)e).getTargetException();
/*  432 */         logger.log(MLevel.WARNING, "An exception occurred while cleaning up a resource.", t);
/*      */         
/*  434 */         okay = false;
/*      */       }
/*      */       finally {
/*  437 */         s.remove(rsrc);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  472 */     return okay;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private SQLException cleanupUncachedActiveStatements()
/*      */   {
/*  479 */     boolean okay = closeAndRemoveResourcesInSet(this.uncachedActiveStatements, STMT_CLOSE_METHOD);
/*      */     
/*      */ 
/*      */ 
/*  483 */     if (okay) {
/*  484 */       return null;
/*      */     }
/*  486 */     return new SQLException("An exception occurred while trying to clean up orphaned resources.");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   ProxyConnection createProxyConnection()
/*      */     throws Exception
/*      */   {
/*  495 */     InvocationHandler handler = new ProxyConnectionInvocationHandler();
/*  496 */     return (ProxyConnection)CON_PROXY_CTOR.newInstance(new Object[] { handler });
/*      */   }
/*      */   
/*      */   Statement createProxyStatement(Statement innerStmt) throws Exception {
/*  500 */     return createProxyStatement(false, innerStmt);
/*      */   }
/*      */   
/*      */   private static class StatementProxyingSetManagedResultSet extends SetManagedResultSet
/*      */   {
/*      */     private Statement proxyStatement;
/*      */     
/*      */     StatementProxyingSetManagedResultSet(Set activeResultSets) {
/*  508 */       super();
/*      */     }
/*      */     
/*  511 */     public void setProxyStatement(Statement proxyStatement) { this.proxyStatement = proxyStatement; }
/*      */     
/*      */     public Statement getStatement() throws SQLException {
/*  514 */       return this.proxyStatement == null ? super.getStatement() : this.proxyStatement;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   Statement createProxyStatement(final boolean inner_is_cached, final Statement innerStmt)
/*      */     throws Exception
/*      */   {
/*  526 */     final Set activeResultSets = Collections.synchronizedSet(new HashSet());
/*  527 */     final Connection parentConnection = this.exposedProxy;
/*      */     
/*  529 */     if (parentConnection == null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  534 */       logger.warning("PROBABLE C3P0 BUG -- " + this + ": created a proxy Statement when there is no active, exposed proxy Connection???");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  543 */     final StatementProxyingSetManagedResultSet mainResultSet = new StatementProxyingSetManagedResultSet(activeResultSets);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  616 */     if ((innerStmt instanceof CallableStatement))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  651 */       new FilterCallableStatement((CallableStatement)innerStmt)
/*      */       {
/*      */         C3P0PooledConnection.1WrapperStatementHelper wsh;
/*      */         
/*      */         public Connection getConnection()
/*      */         {
/*  629 */           return parentConnection;
/*      */         }
/*      */         
/*  632 */         public ResultSet getResultSet() throws SQLException { return this.wsh.wrap(super.getResultSet()); }
/*      */         
/*      */         public ResultSet getGeneratedKeys() throws SQLException {
/*  635 */           return this.wsh.wrap(super.getGeneratedKeys());
/*      */         }
/*      */         
/*  638 */         public ResultSet executeQuery(String sql) throws SQLException { return this.wsh.wrap(super.executeQuery(sql)); }
/*      */         
/*      */         public ResultSet executeQuery() throws SQLException {
/*  641 */           return this.wsh.wrap(super.executeQuery());
/*      */         }
/*      */         
/*      */         public Object rawStatementOperation(Method m, Object target, Object[] args) throws IllegalAccessException, InvocationTargetException, SQLException {
/*  645 */           return this.wsh.doRawStatementOperation(m, target, args);
/*      */         }
/*      */         
/*  648 */         public void close() throws SQLException { this.wsh.doClose(); }
/*      */       };
/*      */     }
/*      */     
/*      */ 
/*  653 */     if ((innerStmt instanceof PreparedStatement))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  688 */       new FilterPreparedStatement((PreparedStatement)innerStmt)
/*      */       {
/*      */         C3P0PooledConnection.1WrapperStatementHelper wsh;
/*      */         
/*      */         public Connection getConnection()
/*      */         {
/*  666 */           return parentConnection;
/*      */         }
/*      */         
/*  669 */         public ResultSet getResultSet() throws SQLException { return this.wsh.wrap(super.getResultSet()); }
/*      */         
/*      */         public ResultSet getGeneratedKeys() throws SQLException {
/*  672 */           return this.wsh.wrap(super.getGeneratedKeys());
/*      */         }
/*      */         
/*  675 */         public ResultSet executeQuery(String sql) throws SQLException { return this.wsh.wrap(super.executeQuery(sql)); }
/*      */         
/*      */         public ResultSet executeQuery() throws SQLException {
/*  678 */           return this.wsh.wrap(super.executeQuery());
/*      */         }
/*      */         
/*      */         public Object rawStatementOperation(Method m, Object target, Object[] args) throws IllegalAccessException, InvocationTargetException, SQLException {
/*  682 */           return this.wsh.doRawStatementOperation(m, target, args);
/*      */         }
/*      */         
/*  685 */         public void close() throws SQLException { this.wsh.doClose(); }
/*      */       };
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  722 */     new FilterStatement(innerStmt)
/*      */     {
/*      */       C3P0PooledConnection.1WrapperStatementHelper wsh;
/*      */       
/*      */       public Connection getConnection()
/*      */       {
/*  703 */         return parentConnection;
/*      */       }
/*      */       
/*  706 */       public ResultSet getResultSet() throws SQLException { return this.wsh.wrap(super.getResultSet()); }
/*      */       
/*      */       public ResultSet getGeneratedKeys() throws SQLException {
/*  709 */         return this.wsh.wrap(super.getGeneratedKeys());
/*      */       }
/*      */       
/*  712 */       public ResultSet executeQuery(String sql) throws SQLException { return this.wsh.wrap(super.executeQuery(sql)); }
/*      */       
/*      */       public Object rawStatementOperation(Method m, Object target, Object[] args) throws IllegalAccessException, InvocationTargetException, SQLException
/*      */       {
/*  716 */         return this.wsh.doRawStatementOperation(m, target, args);
/*      */       }
/*      */       
/*  719 */       public void close() throws SQLException { this.wsh.doClose(); }
/*      */     };
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   final class ProxyConnectionInvocationHandler
/*      */     implements InvocationHandler
/*      */   {
/*  729 */     Connection activeConnection = C3P0PooledConnection.this.physicalConnection;
/*  730 */     DatabaseMetaData metaData = null;
/*  731 */     boolean connection_error_signaled = false;
/*      */     
/*      */ 
/*      */ 
/*      */     ProxyConnectionInvocationHandler() {}
/*      */     
/*      */ 
/*      */ 
/*  739 */     final Set activeMetaDataResultSets = new HashSet();
/*      */     
/*      */ 
/*      */ 
/*  743 */     Set doRawResultSets = null;
/*      */     
/*  745 */     boolean txn_known_resolved = true;
/*      */     
/*      */     public String toString() {
/*  748 */       return "C3P0ProxyConnection [Invocation Handler: " + super.toString() + ']'; }
/*      */     
/*      */     private Object doRawConnectionOperation(Method m, Object target, Object[] args)
/*      */       throws IllegalAccessException, InvocationTargetException, SQLException, Exception
/*      */     {
/*  753 */       if (this.activeConnection == null) {
/*  754 */         throw new SQLException("Connection previously closed. You cannot operate on a closed Connection.");
/*      */       }
/*  756 */       if (target == C3P0ProxyConnection.RAW_CONNECTION)
/*  757 */         target = this.activeConnection;
/*  758 */       int i = 0; for (int len = args.length; i < len; i++) {
/*  759 */         if (args[i] == C3P0ProxyConnection.RAW_CONNECTION)
/*  760 */           args[i] = this.activeConnection;
/*      */       }
/*  762 */       Object out = m.invoke(target, args);
/*      */       
/*      */ 
/*  765 */       if ((out instanceof Statement)) {
/*  766 */         out = C3P0PooledConnection.this.createProxyStatement(false, (Statement)out);
/*  767 */       } else if ((out instanceof ResultSet))
/*      */       {
/*  769 */         if (this.doRawResultSets == null)
/*  770 */           this.doRawResultSets = new HashSet();
/*  771 */         out = new NullStatementSetManagedResultSet((ResultSet)out, this.doRawResultSets);
/*      */       }
/*  773 */       return out;
/*      */     }
/*      */     
/*      */     public synchronized Object invoke(Object proxy, Method m, Object[] args)
/*      */       throws Throwable
/*      */     {
/*  779 */       if (C3P0PooledConnection.OBJECT_METHODS.contains(m)) {
/*  780 */         return m.invoke(this, args);
/*      */       }
/*      */       try
/*      */       {
/*  784 */         String mname = m.getName();
/*  785 */         if (this.activeConnection != null)
/*      */         {
/*  787 */           if (mname.equals("rawConnectionOperation"))
/*      */           {
/*  789 */             C3P0PooledConnection.this.ensureOkay();
/*  790 */             this.txn_known_resolved = false;
/*      */             
/*  792 */             return doRawConnectionOperation((Method)args[0], args[1], (Object[])args[2]);
/*      */           }
/*  794 */           if (mname.equals("setTransactionIsolation"))
/*      */           {
/*  796 */             C3P0PooledConnection.this.ensureOkay();
/*      */             
/*      */ 
/*      */ 
/*  800 */             m.invoke(this.activeConnection, args);
/*      */             
/*  802 */             int lvl = ((Integer)args[0]).intValue();
/*  803 */             C3P0PooledConnection.this.isolation_lvl_nondefault = (lvl != C3P0PooledConnection.this.dflt_txn_isolation);
/*      */             
/*      */ 
/*      */ 
/*  807 */             return null;
/*      */           }
/*  809 */           if (mname.equals("setCatalog"))
/*      */           {
/*  811 */             C3P0PooledConnection.this.ensureOkay();
/*      */             
/*      */ 
/*      */ 
/*  815 */             m.invoke(this.activeConnection, args);
/*      */             
/*  817 */             String catalog = (String)args[0];
/*  818 */             C3P0PooledConnection.this.catalog_nondefault = ObjectUtils.eqOrBothNull(catalog, C3P0PooledConnection.this.dflt_catalog);
/*      */             
/*  820 */             return null;
/*      */           }
/*  822 */           if (mname.equals("setHoldability"))
/*      */           {
/*  824 */             C3P0PooledConnection.this.ensureOkay();
/*      */             
/*      */ 
/*      */ 
/*  828 */             m.invoke(this.activeConnection, args);
/*      */             
/*  830 */             int holdability = ((Integer)args[0]).intValue();
/*  831 */             C3P0PooledConnection.this.holdability_nondefault = (holdability != C3P0PooledConnection.this.dflt_holdability);
/*      */             
/*  833 */             return null;
/*      */           }
/*  835 */           if (mname.equals("createStatement"))
/*      */           {
/*  837 */             C3P0PooledConnection.this.ensureOkay();
/*  838 */             this.txn_known_resolved = false;
/*      */             
/*  840 */             Object stmt = m.invoke(this.activeConnection, args);
/*  841 */             return C3P0PooledConnection.this.createProxyStatement((Statement)stmt);
/*      */           }
/*  843 */           if (mname.equals("prepareStatement"))
/*      */           {
/*  845 */             C3P0PooledConnection.this.ensureOkay();
/*  846 */             this.txn_known_resolved = false;
/*      */             
/*      */ 
/*  849 */             if (C3P0PooledConnection.this.scache == null)
/*      */             {
/*  851 */               Object pstmt = m.invoke(this.activeConnection, args);
/*  852 */               return C3P0PooledConnection.this.createProxyStatement((Statement)pstmt);
/*      */             }
/*      */             
/*      */ 
/*  856 */             Object pstmt = C3P0PooledConnection.this.scache.checkoutStatement(C3P0PooledConnection.this.physicalConnection, m, args);
/*      */             
/*      */ 
/*  859 */             return C3P0PooledConnection.this.createProxyStatement(true, (Statement)pstmt);
/*      */           }
/*      */           
/*      */ 
/*  863 */           if (mname.equals("prepareCall"))
/*      */           {
/*  865 */             C3P0PooledConnection.this.ensureOkay();
/*  866 */             this.txn_known_resolved = false;
/*      */             
/*      */ 
/*  869 */             if (C3P0PooledConnection.this.scache == null)
/*      */             {
/*  871 */               Object cstmt = m.invoke(this.activeConnection, args);
/*  872 */               return C3P0PooledConnection.this.createProxyStatement((Statement)cstmt);
/*      */             }
/*      */             
/*      */ 
/*  876 */             Object cstmt = C3P0PooledConnection.this.scache.checkoutStatement(C3P0PooledConnection.this.physicalConnection, m, args);
/*  877 */             return C3P0PooledConnection.this.createProxyStatement(true, (Statement)cstmt);
/*      */           }
/*      */           
/*      */ 
/*  881 */           if (mname.equals("getMetaData"))
/*      */           {
/*  883 */             C3P0PooledConnection.this.ensureOkay();
/*  884 */             this.txn_known_resolved = false;
/*      */             
/*  886 */             DatabaseMetaData innerMd = this.activeConnection.getMetaData();
/*  887 */             if (this.metaData == null)
/*      */             {
/*      */ 
/*  890 */               synchronized (C3P0PooledConnection.this) {
/*  891 */                 this.metaData = new SetManagedDatabaseMetaData(innerMd, this.activeMetaDataResultSets, C3P0PooledConnection.this.exposedProxy);
/*      */               } }
/*  893 */             return this.metaData;
/*      */           }
/*  895 */           if (mname.equals("silentClose"))
/*      */           {
/*      */ 
/*      */ 
/*  899 */             doSilentClose(proxy, ((Boolean)args[0]).booleanValue(), this.txn_known_resolved);
/*  900 */             return null;
/*      */           }
/*  902 */           if (mname.equals("close"))
/*      */           {
/*      */ 
/*      */ 
/*  906 */             Exception e = doSilentClose(proxy, false, this.txn_known_resolved);
/*  907 */             if (!this.connection_error_signaled) {
/*  908 */               C3P0PooledConnection.this.ces.fireConnectionClosed();
/*      */             }
/*  910 */             if (e != null)
/*      */             {
/*      */ 
/*      */ 
/*  914 */               throw e;
/*      */             }
/*      */             
/*  917 */             return null;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  926 */           C3P0PooledConnection.this.ensureOkay();
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  938 */           this.txn_known_resolved = false;
/*      */           
/*  940 */           return m.invoke(this.activeConnection, args);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*  945 */         if ((mname.equals("close")) || (mname.equals("silentClose")))
/*      */         {
/*  947 */           return null; }
/*  948 */         if (mname.equals("isClosed")) {
/*  949 */           return Boolean.TRUE;
/*      */         }
/*      */         
/*  952 */         throw new SQLException("You can't operate on a closed connection!!!");
/*      */ 
/*      */ 
/*      */       }
/*      */       catch (InvocationTargetException e)
/*      */       {
/*      */ 
/*  959 */         Throwable convertMe = e.getTargetException();
/*  960 */         SQLException sqle = handleMaybeFatalToPooledConnection(convertMe, proxy, false);
/*  961 */         sqle.fillInStackTrace();
/*  962 */         throw sqle;
/*      */       }
/*      */     }
/*      */     
/*      */     private Exception doSilentClose(Object proxyConnection, boolean pooled_connection_is_dead) {
/*  967 */       return doSilentClose(proxyConnection, pooled_connection_is_dead, false);
/*      */     }
/*      */     
/*      */     private Exception doSilentClose(Object proxyConnection, boolean pooled_connection_is_dead, boolean known_resolved_txn) {
/*  971 */       if (this.activeConnection != null)
/*      */       {
/*  973 */         synchronized (C3P0PooledConnection.this)
/*      */         {
/*  975 */           if (C3P0PooledConnection.this.exposedProxy == proxyConnection)
/*      */           {
/*  977 */             C3P0PooledConnection.this.exposedProxy = null;
/*      */ 
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/*  984 */             C3P0PooledConnection.logger.warning("(c3p0 issue) doSilentClose( ... ) called on a proxyConnection other than the current exposed proxy for its PooledConnection. [exposedProxy: " + C3P0PooledConnection.this.exposedProxy + ", proxyConnection: " + proxyConnection);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  992 */         Exception out = null;
/*      */         
/*  994 */         Exception exc1 = null;Exception exc2 = null;Exception exc3 = null;Exception exc4 = null;
/*      */         try
/*      */         {
/*  997 */           if (!pooled_connection_is_dead) {
/*  998 */             C3P0PooledConnection.this.reset(known_resolved_txn);
/*      */           }
/*      */         }
/*      */         catch (Exception e) {
/* 1002 */           exc1 = e;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1010 */         exc2 = C3P0PooledConnection.this.cleanupUncachedActiveStatements();
/*      */         
/*      */ 
/*      */         String errSource;
/*      */         
/*      */         String errSource;
/*      */         
/* 1017 */         if (this.doRawResultSets != null)
/*      */         {
/* 1019 */           this.activeMetaDataResultSets.addAll(this.doRawResultSets);
/* 1020 */           errSource = "DataBaseMetaData or raw Connection operation";
/*      */         }
/*      */         else {
/* 1023 */           errSource = "DataBaseMetaData";
/*      */         }
/* 1025 */         if (!C3P0PooledConnection.this.closeAndRemoveResultSets(this.activeMetaDataResultSets)) {
/* 1026 */           exc3 = new SQLException("Failed to close some " + errSource + " Result Sets.");
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1032 */         if (C3P0PooledConnection.this.scache != null) {
/*      */           try
/*      */           {
/* 1035 */             C3P0PooledConnection.this.scache.checkinAll(C3P0PooledConnection.this.physicalConnection);
/*      */           } catch (Exception e) {
/* 1037 */             exc4 = e;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1045 */         if (exc1 != null)
/*      */         {
/* 1047 */           handleMaybeFatalToPooledConnection(exc1, proxyConnection, true);
/* 1048 */           out = exc1;
/*      */         }
/* 1050 */         else if (exc2 != null)
/*      */         {
/* 1052 */           handleMaybeFatalToPooledConnection(exc2, proxyConnection, true);
/* 1053 */           out = exc2;
/*      */         }
/* 1055 */         else if (exc3 != null)
/*      */         {
/* 1057 */           handleMaybeFatalToPooledConnection(exc3, proxyConnection, true);
/* 1058 */           out = exc3;
/*      */         }
/* 1060 */         else if (exc4 != null)
/*      */         {
/* 1062 */           handleMaybeFatalToPooledConnection(exc4, proxyConnection, true);
/* 1063 */           out = exc4;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1072 */         this.activeConnection = null;
/* 1073 */         return out;
/*      */       }
/*      */       
/* 1076 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private SQLException handleMaybeFatalToPooledConnection(Throwable t, Object proxyConnection, boolean already_closed)
/*      */     {
/* 1083 */       SQLException sqle = SqlUtils.toSQLException(t);
/* 1084 */       int status = C3P0PooledConnection.this.connectionTester.statusOnException(C3P0PooledConnection.this.physicalConnection, sqle);
/* 1085 */       C3P0PooledConnection.this.updateConnectionStatus(status);
/* 1086 */       if (status != 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1094 */         C3P0PooledConnection.logger.log(MLevel.INFO, C3P0PooledConnection.this + " will no longer be pooled because it has been marked invalid by an Exception.", t);
/*      */         
/*      */ 
/*      */ 
/*      */ 
/* 1099 */         C3P0PooledConnection.this.invalidatingException = sqle;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1121 */         if (!this.connection_error_signaled)
/*      */         {
/* 1123 */           C3P0PooledConnection.this.ces.fireConnectionErrorOccurred(sqle);
/* 1124 */           this.connection_error_signaled = true;
/*      */         }
/*      */       }
/* 1127 */       return sqle;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public synchronized int getConnectionStatus()
/*      */   {
/* 1137 */     return this.connection_status;
/*      */   }
/*      */   
/*      */   private synchronized void updateConnectionStatus(int status) {
/* 1141 */     switch (this.connection_status)
/*      */     {
/*      */     case -8: 
/*      */       break;
/*      */     
/*      */     case -1: 
/* 1147 */       if (status == -8)
/* 1148 */         doBadUpdate(status);
/*      */       break;
/*      */     case 0: 
/* 1151 */       if (status != 0)
/* 1152 */         doBadUpdate(status);
/*      */       break;
/*      */     default: 
/* 1155 */       throw new InternalError(this + " -- Illegal Connection Status: " + this.connection_status);
/*      */     }
/*      */     
/*      */   }
/*      */   
/*      */   private void doBadUpdate(int new_status)
/*      */   {
/* 1162 */     this.connection_status = new_status;
/* 1163 */     try { close(true);
/*      */ 
/*      */     }
/*      */     catch (SQLException e)
/*      */     {
/*      */ 
/* 1169 */       logger.log(MLevel.WARNING, "Broken Connection Close Error. ", e);
/*      */     }
/*      */   }
/*      */   
/*      */   static abstract interface ProxyConnection
/*      */     extends C3P0ProxyConnection
/*      */   {
/*      */     public abstract void silentClose(boolean paramBoolean)
/*      */       throws SQLException;
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\C3P0PooledConnection.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */