/*      */ package com.mchange.v2.c3p0.impl;
/*      */ 
/*      */ import com.mchange.v2.c3p0.C3P0ProxyConnection;
/*      */ import com.mchange.v2.log.MLevel;
/*      */ import com.mchange.v2.log.MLog;
/*      */ import com.mchange.v2.log.MLogger;
/*      */ import com.mchange.v2.sql.SqlUtils;
/*      */ import com.mchange.v2.util.ResourceClosedException;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.Connection;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Savepoint;
/*      */ import java.sql.Statement;
/*      */ import java.util.Map;
/*      */ import javax.sql.ConnectionEvent;
/*      */ import javax.sql.ConnectionEventListener;
/*      */ 
/*      */ 
/*      */ public final class NewProxyConnection
/*      */   implements Connection, C3P0ProxyConnection
/*      */ {
/*      */   protected Connection inner;
/*      */   
/*      */   public NewProxyConnection(Connection inner)
/*      */   {
/*   32 */     this.inner = inner;
/*      */   }
/*      */   
/*      */   public synchronized Statement createStatement(int a, int b, int c) throws SQLException
/*      */   {
/*      */     try {
/*   38 */       this.txn_known_resolved = false;
/*      */       
/*   40 */       Statement innerStmt = this.inner.createStatement(a, b, c);
/*   41 */       this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/*   42 */       return new NewProxyStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*   46 */       if (isDetached())
/*      */       {
/*   48 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*   50 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*   54 */       if (!isDetached())
/*      */       {
/*   56 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*   58 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized Statement createStatement(int a, int b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*   66 */       this.txn_known_resolved = false;
/*      */       
/*   68 */       Statement innerStmt = this.inner.createStatement(a, b);
/*   69 */       this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/*   70 */       return new NewProxyStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*   74 */       if (isDetached())
/*      */       {
/*   76 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*   78 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*   82 */       if (!isDetached())
/*      */       {
/*   84 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*   86 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized Statement createStatement() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*   94 */       this.txn_known_resolved = false;
/*      */       
/*   96 */       Statement innerStmt = this.inner.createStatement();
/*   97 */       this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/*   98 */       return new NewProxyStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  102 */       if (isDetached())
/*      */       {
/*  104 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*  106 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  110 */       if (!isDetached())
/*      */       {
/*  112 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  114 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized PreparedStatement prepareStatement(String a, String[] b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  122 */       this.txn_known_resolved = false;
/*      */       
/*      */ 
/*      */ 
/*  126 */       if (this.parentPooledConnection.isStatementCaching())
/*      */       {
/*      */         try
/*      */         {
/*  130 */           Class[] argTypes = { String.class, new String[0].getClass() };
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  135 */           Method method = Connection.class.getMethod("prepareStatement", argTypes);
/*      */           
/*  137 */           Object[] args = { a, b };
/*      */           
/*      */ 
/*      */ 
/*  141 */           innerStmt = (PreparedStatement)this.parentPooledConnection.checkoutStatement(method, args);
/*  142 */           return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, true, this);
/*      */         }
/*      */         catch (ResourceClosedException e)
/*      */         {
/*  146 */           if (logger.isLoggable(MLevel.FINE))
/*  147 */             logger.log(MLevel.FINE, "A Connection tried to prepare a Statement via a Statement cache that is already closed. This can happen -- rarely -- if a DataSource is closed or reset() while Connections are checked-out and in use.", e);
/*  148 */           PreparedStatement innerStmt = this.inner.prepareStatement(a, b);
/*  149 */           this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/*  150 */           return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  155 */       PreparedStatement innerStmt = this.inner.prepareStatement(a, b);
/*  156 */       this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/*  157 */       return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */ 
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  162 */       if (isDetached())
/*      */       {
/*  164 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*  166 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  170 */       if (!isDetached())
/*      */       {
/*  172 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  174 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized PreparedStatement prepareStatement(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  182 */       this.txn_known_resolved = false;
/*      */       
/*      */ 
/*      */ 
/*  186 */       if (this.parentPooledConnection.isStatementCaching())
/*      */       {
/*      */         try
/*      */         {
/*  190 */           Class[] argTypes = { String.class };
/*      */           
/*      */ 
/*      */ 
/*  194 */           Method method = Connection.class.getMethod("prepareStatement", argTypes);
/*      */           
/*  196 */           Object[] args = { a };
/*      */           
/*      */ 
/*  199 */           innerStmt = (PreparedStatement)this.parentPooledConnection.checkoutStatement(method, args);
/*  200 */           return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, true, this);
/*      */         }
/*      */         catch (ResourceClosedException e)
/*      */         {
/*  204 */           if (logger.isLoggable(MLevel.FINE))
/*  205 */             logger.log(MLevel.FINE, "A Connection tried to prepare a Statement via a Statement cache that is already closed. This can happen -- rarely -- if a DataSource is closed or reset() while Connections are checked-out and in use.", e);
/*  206 */           PreparedStatement innerStmt = this.inner.prepareStatement(a);
/*  207 */           this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/*  208 */           return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  213 */       PreparedStatement innerStmt = this.inner.prepareStatement(a);
/*  214 */       this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/*  215 */       return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */ 
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  220 */       if (isDetached())
/*      */       {
/*  222 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*  224 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  228 */       if (!isDetached())
/*      */       {
/*  230 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  232 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized PreparedStatement prepareStatement(String a, int b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  240 */       this.txn_known_resolved = false;
/*      */       
/*      */ 
/*      */ 
/*  244 */       if (this.parentPooledConnection.isStatementCaching())
/*      */       {
/*      */         try
/*      */         {
/*  248 */           Class[] argTypes = { String.class, Integer.TYPE, Integer.TYPE };
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  254 */           Method method = Connection.class.getMethod("prepareStatement", argTypes);
/*      */           
/*  256 */           Object[] args = { a, new Integer(b), new Integer(c) };
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  261 */           innerStmt = (PreparedStatement)this.parentPooledConnection.checkoutStatement(method, args);
/*  262 */           return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, true, this);
/*      */         }
/*      */         catch (ResourceClosedException e)
/*      */         {
/*  266 */           if (logger.isLoggable(MLevel.FINE))
/*  267 */             logger.log(MLevel.FINE, "A Connection tried to prepare a Statement via a Statement cache that is already closed. This can happen -- rarely -- if a DataSource is closed or reset() while Connections are checked-out and in use.", e);
/*  268 */           PreparedStatement innerStmt = this.inner.prepareStatement(a, b, c);
/*  269 */           this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/*  270 */           return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  275 */       PreparedStatement innerStmt = this.inner.prepareStatement(a, b, c);
/*  276 */       this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/*  277 */       return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */ 
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  282 */       if (isDetached())
/*      */       {
/*  284 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*  286 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  290 */       if (!isDetached())
/*      */       {
/*  292 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  294 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized PreparedStatement prepareStatement(String a, int b, int c, int d) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  302 */       this.txn_known_resolved = false;
/*      */       
/*      */ 
/*      */ 
/*  306 */       if (this.parentPooledConnection.isStatementCaching())
/*      */       {
/*      */         try
/*      */         {
/*  310 */           Class[] argTypes = { String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE };
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  317 */           Method method = Connection.class.getMethod("prepareStatement", argTypes);
/*      */           
/*  319 */           Object[] args = { a, new Integer(b), new Integer(c), new Integer(d) };
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  325 */           innerStmt = (PreparedStatement)this.parentPooledConnection.checkoutStatement(method, args);
/*  326 */           return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, true, this);
/*      */         }
/*      */         catch (ResourceClosedException e)
/*      */         {
/*  330 */           if (logger.isLoggable(MLevel.FINE))
/*  331 */             logger.log(MLevel.FINE, "A Connection tried to prepare a Statement via a Statement cache that is already closed. This can happen -- rarely -- if a DataSource is closed or reset() while Connections are checked-out and in use.", e);
/*  332 */           PreparedStatement innerStmt = this.inner.prepareStatement(a, b, c, d);
/*  333 */           this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/*  334 */           return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  339 */       PreparedStatement innerStmt = this.inner.prepareStatement(a, b, c, d);
/*  340 */       this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/*  341 */       return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */ 
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  346 */       if (isDetached())
/*      */       {
/*  348 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*  350 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  354 */       if (!isDetached())
/*      */       {
/*  356 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  358 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized PreparedStatement prepareStatement(String a, int b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  366 */       this.txn_known_resolved = false;
/*      */       
/*      */ 
/*      */ 
/*  370 */       if (this.parentPooledConnection.isStatementCaching())
/*      */       {
/*      */         try
/*      */         {
/*  374 */           Class[] argTypes = { String.class, Integer.TYPE };
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  379 */           Method method = Connection.class.getMethod("prepareStatement", argTypes);
/*      */           
/*  381 */           Object[] args = { a, new Integer(b) };
/*      */           
/*      */ 
/*      */ 
/*  385 */           innerStmt = (PreparedStatement)this.parentPooledConnection.checkoutStatement(method, args);
/*  386 */           return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, true, this);
/*      */         }
/*      */         catch (ResourceClosedException e)
/*      */         {
/*  390 */           if (logger.isLoggable(MLevel.FINE))
/*  391 */             logger.log(MLevel.FINE, "A Connection tried to prepare a Statement via a Statement cache that is already closed. This can happen -- rarely -- if a DataSource is closed or reset() while Connections are checked-out and in use.", e);
/*  392 */           PreparedStatement innerStmt = this.inner.prepareStatement(a, b);
/*  393 */           this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/*  394 */           return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  399 */       PreparedStatement innerStmt = this.inner.prepareStatement(a, b);
/*  400 */       this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/*  401 */       return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */ 
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  406 */       if (isDetached())
/*      */       {
/*  408 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*  410 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  414 */       if (!isDetached())
/*      */       {
/*  416 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  418 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized PreparedStatement prepareStatement(String a, int[] b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  426 */       this.txn_known_resolved = false;
/*      */       
/*      */ 
/*      */ 
/*  430 */       if (this.parentPooledConnection.isStatementCaching())
/*      */       {
/*      */         try
/*      */         {
/*  434 */           Class[] argTypes = { String.class, new int[0].getClass() };
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  439 */           Method method = Connection.class.getMethod("prepareStatement", argTypes);
/*      */           
/*  441 */           Object[] args = { a, b };
/*      */           
/*      */ 
/*      */ 
/*  445 */           innerStmt = (PreparedStatement)this.parentPooledConnection.checkoutStatement(method, args);
/*  446 */           return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, true, this);
/*      */         }
/*      */         catch (ResourceClosedException e)
/*      */         {
/*  450 */           if (logger.isLoggable(MLevel.FINE))
/*  451 */             logger.log(MLevel.FINE, "A Connection tried to prepare a Statement via a Statement cache that is already closed. This can happen -- rarely -- if a DataSource is closed or reset() while Connections are checked-out and in use.", e);
/*  452 */           PreparedStatement innerStmt = this.inner.prepareStatement(a, b);
/*  453 */           this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/*  454 */           return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  459 */       PreparedStatement innerStmt = this.inner.prepareStatement(a, b);
/*  460 */       this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/*  461 */       return new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */ 
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  466 */       if (isDetached())
/*      */       {
/*  468 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*  470 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  474 */       if (!isDetached())
/*      */       {
/*  476 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  478 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized CallableStatement prepareCall(String a, int b, int c, int d) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  486 */       this.txn_known_resolved = false;
/*      */       
/*      */ 
/*      */ 
/*  490 */       if (this.parentPooledConnection.isStatementCaching())
/*      */       {
/*      */         try
/*      */         {
/*  494 */           Class[] argTypes = { String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE };
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  501 */           Method method = Connection.class.getMethod("prepareCall", argTypes);
/*      */           
/*  503 */           Object[] args = { a, new Integer(b), new Integer(c), new Integer(d) };
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  509 */           innerStmt = (CallableStatement)this.parentPooledConnection.checkoutStatement(method, args);
/*  510 */           return new NewProxyCallableStatement(innerStmt, this.parentPooledConnection, true, this);
/*      */         }
/*      */         catch (ResourceClosedException e)
/*      */         {
/*  514 */           if (logger.isLoggable(MLevel.FINE))
/*  515 */             logger.log(MLevel.FINE, "A Connection tried to prepare a CallableStatement via a Statement cache that is already closed. This can happen -- rarely -- if a DataSource is closed or reset() while Connections are checked-out and in use.", e);
/*  516 */           CallableStatement innerStmt = this.inner.prepareCall(a, b, c, d);
/*  517 */           this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/*  518 */           return new NewProxyCallableStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  523 */       CallableStatement innerStmt = this.inner.prepareCall(a, b, c, d);
/*  524 */       this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/*  525 */       return new NewProxyCallableStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */ 
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  530 */       if (isDetached())
/*      */       {
/*  532 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*  534 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  538 */       if (!isDetached())
/*      */       {
/*  540 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  542 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized CallableStatement prepareCall(String a, int b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  550 */       this.txn_known_resolved = false;
/*      */       
/*      */ 
/*      */ 
/*  554 */       if (this.parentPooledConnection.isStatementCaching())
/*      */       {
/*      */         try
/*      */         {
/*  558 */           Class[] argTypes = { String.class, Integer.TYPE, Integer.TYPE };
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  564 */           Method method = Connection.class.getMethod("prepareCall", argTypes);
/*      */           
/*  566 */           Object[] args = { a, new Integer(b), new Integer(c) };
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  571 */           innerStmt = (CallableStatement)this.parentPooledConnection.checkoutStatement(method, args);
/*  572 */           return new NewProxyCallableStatement(innerStmt, this.parentPooledConnection, true, this);
/*      */         }
/*      */         catch (ResourceClosedException e)
/*      */         {
/*  576 */           if (logger.isLoggable(MLevel.FINE))
/*  577 */             logger.log(MLevel.FINE, "A Connection tried to prepare a CallableStatement via a Statement cache that is already closed. This can happen -- rarely -- if a DataSource is closed or reset() while Connections are checked-out and in use.", e);
/*  578 */           CallableStatement innerStmt = this.inner.prepareCall(a, b, c);
/*  579 */           this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/*  580 */           return new NewProxyCallableStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  585 */       CallableStatement innerStmt = this.inner.prepareCall(a, b, c);
/*  586 */       this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/*  587 */       return new NewProxyCallableStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */ 
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  592 */       if (isDetached())
/*      */       {
/*  594 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*  596 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  600 */       if (!isDetached())
/*      */       {
/*  602 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  604 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized CallableStatement prepareCall(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  612 */       this.txn_known_resolved = false;
/*      */       
/*      */ 
/*      */ 
/*  616 */       if (this.parentPooledConnection.isStatementCaching())
/*      */       {
/*      */         try
/*      */         {
/*  620 */           Class[] argTypes = { String.class };
/*      */           
/*      */ 
/*      */ 
/*  624 */           Method method = Connection.class.getMethod("prepareCall", argTypes);
/*      */           
/*  626 */           Object[] args = { a };
/*      */           
/*      */ 
/*  629 */           innerStmt = (CallableStatement)this.parentPooledConnection.checkoutStatement(method, args);
/*  630 */           return new NewProxyCallableStatement(innerStmt, this.parentPooledConnection, true, this);
/*      */         }
/*      */         catch (ResourceClosedException e)
/*      */         {
/*  634 */           if (logger.isLoggable(MLevel.FINE))
/*  635 */             logger.log(MLevel.FINE, "A Connection tried to prepare a CallableStatement via a Statement cache that is already closed. This can happen -- rarely -- if a DataSource is closed or reset() while Connections are checked-out and in use.", e);
/*  636 */           CallableStatement innerStmt = this.inner.prepareCall(a);
/*  637 */           this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/*  638 */           return new NewProxyCallableStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  643 */       CallableStatement innerStmt = this.inner.prepareCall(a);
/*  644 */       this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/*  645 */       return new NewProxyCallableStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */ 
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  650 */       if (isDetached())
/*      */       {
/*  652 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*  654 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  658 */       if (!isDetached())
/*      */       {
/*  660 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  662 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized DatabaseMetaData getMetaData() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  670 */       this.txn_known_resolved = false;
/*      */       
/*  672 */       if (this.metaData == null)
/*      */       {
/*  674 */         DatabaseMetaData innerMetaData = this.inner.getMetaData();
/*  675 */         this.metaData = new NewProxyDatabaseMetaData(innerMetaData, this.parentPooledConnection, this);
/*      */       }
/*  677 */       return this.metaData;
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  681 */       if (isDetached())
/*      */       {
/*  683 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*  685 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  689 */       if (!isDetached())
/*      */       {
/*  691 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  693 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized void setTransactionIsolation(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  701 */       this.inner.setTransactionIsolation(a);
/*  702 */       this.parentPooledConnection.markNewTxnIsolation(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  706 */       if (isDetached())
/*      */       {
/*  708 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*  710 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  714 */       if (!isDetached())
/*      */       {
/*  716 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  718 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized void setCatalog(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  726 */       this.inner.setCatalog(a);
/*  727 */       this.parentPooledConnection.markNewCatalog(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  731 */       if (isDetached())
/*      */       {
/*  733 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*  735 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  739 */       if (!isDetached())
/*      */       {
/*  741 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  743 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized void setHoldability(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  751 */       this.inner.setHoldability(a);
/*  752 */       this.parentPooledConnection.markNewHoldability(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  756 */       if (isDetached())
/*      */       {
/*  758 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*  760 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  764 */       if (!isDetached())
/*      */       {
/*  766 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  768 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized void setTypeMap(Map a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  776 */       this.inner.setTypeMap(a);
/*  777 */       this.parentPooledConnection.markNewTypeMap(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  781 */       if (isDetached())
/*      */       {
/*  783 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*  785 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  789 */       if (!isDetached())
/*      */       {
/*  791 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  793 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized void commit() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  801 */       this.txn_known_resolved = true;
/*      */       
/*  803 */       this.inner.commit();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  807 */       if (isDetached())
/*      */       {
/*  809 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*  811 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  815 */       if (!isDetached())
/*      */       {
/*  817 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  819 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized void rollback(Savepoint a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  827 */       this.txn_known_resolved = true;
/*      */       
/*  829 */       this.inner.rollback(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  833 */       if (isDetached())
/*      */       {
/*  835 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*  837 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  841 */       if (!isDetached())
/*      */       {
/*  843 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  845 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized void rollback() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  853 */       this.txn_known_resolved = true;
/*      */       
/*  855 */       this.inner.rollback();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  859 */       if (isDetached())
/*      */       {
/*  861 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*  863 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  867 */       if (!isDetached())
/*      */       {
/*  869 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  871 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized void setAutoCommit(boolean a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  879 */       this.txn_known_resolved = true;
/*      */       
/*  881 */       this.inner.setAutoCommit(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  885 */       if (isDetached())
/*      */       {
/*  887 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*  889 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  893 */       if (!isDetached())
/*      */       {
/*  895 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  897 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized SQLWarning getWarnings() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  905 */       this.txn_known_resolved = false;
/*      */       
/*  907 */       return this.inner.getWarnings();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  911 */       if (isDetached())
/*      */       {
/*  913 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*  915 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  919 */       if (!isDetached())
/*      */       {
/*  921 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  923 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized void clearWarnings() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  931 */       this.txn_known_resolved = false;
/*      */       
/*  933 */       this.inner.clearWarnings();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  937 */       if (isDetached())
/*      */       {
/*  939 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*  941 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  945 */       if (!isDetached())
/*      */       {
/*  947 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  949 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized String nativeSQL(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  957 */       this.txn_known_resolved = false;
/*      */       
/*  959 */       return this.inner.nativeSQL(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  963 */       if (isDetached())
/*      */       {
/*  965 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*  967 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  971 */       if (!isDetached())
/*      */       {
/*  973 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  975 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized boolean getAutoCommit() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  983 */       this.txn_known_resolved = false;
/*      */       
/*  985 */       return this.inner.getAutoCommit();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  989 */       if (isDetached())
/*      */       {
/*  991 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/*  993 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  997 */       if (!isDetached())
/*      */       {
/*  999 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1001 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized String getCatalog() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1009 */       this.txn_known_resolved = false;
/*      */       
/* 1011 */       return this.inner.getCatalog();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1015 */       if (isDetached())
/*      */       {
/* 1017 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/* 1019 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1023 */       if (!isDetached())
/*      */       {
/* 1025 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1027 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized int getTransactionIsolation() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1035 */       this.txn_known_resolved = false;
/*      */       
/* 1037 */       return this.inner.getTransactionIsolation();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1041 */       if (isDetached())
/*      */       {
/* 1043 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/* 1045 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1049 */       if (!isDetached())
/*      */       {
/* 1051 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1053 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized Map getTypeMap() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1061 */       this.txn_known_resolved = false;
/*      */       
/* 1063 */       return this.inner.getTypeMap();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1067 */       if (isDetached())
/*      */       {
/* 1069 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/* 1071 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1075 */       if (!isDetached())
/*      */       {
/* 1077 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1079 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized int getHoldability() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1087 */       this.txn_known_resolved = false;
/*      */       
/* 1089 */       return this.inner.getHoldability();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1093 */       if (isDetached())
/*      */       {
/* 1095 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/* 1097 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1101 */       if (!isDetached())
/*      */       {
/* 1103 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1105 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized Savepoint setSavepoint() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1113 */       this.txn_known_resolved = false;
/*      */       
/* 1115 */       return this.inner.setSavepoint();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1119 */       if (isDetached())
/*      */       {
/* 1121 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/* 1123 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1127 */       if (!isDetached())
/*      */       {
/* 1129 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1131 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized Savepoint setSavepoint(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1139 */       this.txn_known_resolved = false;
/*      */       
/* 1141 */       return this.inner.setSavepoint(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1145 */       if (isDetached())
/*      */       {
/* 1147 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/* 1149 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1153 */       if (!isDetached())
/*      */       {
/* 1155 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1157 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized void releaseSavepoint(Savepoint a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1165 */       this.txn_known_resolved = false;
/*      */       
/* 1167 */       this.inner.releaseSavepoint(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1171 */       if (isDetached())
/*      */       {
/* 1173 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/* 1175 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1179 */       if (!isDetached())
/*      */       {
/* 1181 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1183 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized void setReadOnly(boolean a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1191 */       this.inner.setReadOnly(a);
/* 1192 */       this.parentPooledConnection.markNewReadOnly(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1196 */       if (isDetached())
/*      */       {
/* 1198 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/* 1200 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1204 */       if (!isDetached())
/*      */       {
/* 1206 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1208 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized boolean isReadOnly() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1216 */       this.txn_known_resolved = false;
/*      */       
/* 1218 */       return this.inner.isReadOnly();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1222 */       if (isDetached())
/*      */       {
/* 1224 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/* 1226 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1230 */       if (!isDetached())
/*      */       {
/* 1232 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1234 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized void close() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1242 */       if (!isDetached())
/*      */       {
/* 1244 */         NewPooledConnection npc = this.parentPooledConnection;
/* 1245 */         detach();
/* 1246 */         npc.markClosedProxyConnection(this, this.txn_known_resolved);
/* 1247 */         this.inner = null;
/*      */       }
/* 1249 */       else if (logger.isLoggable(MLevel.FINE))
/*      */       {
/* 1251 */         logger.log(MLevel.FINE, this + ": close() called more than once.");
/*      */       }
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1256 */       if (isDetached())
/*      */       {
/* 1258 */         if (logger.isLoggable(MLevel.FINE))
/*      */         {
/* 1260 */           logger.log(MLevel.FINE, this + ": close() called more than once.");
/*      */         }
/*      */       } else {
/* 1263 */         throw exc;
/*      */       }
/*      */     }
/*      */     catch (Exception exc) {
/* 1267 */       if (!isDetached())
/*      */       {
/* 1269 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1271 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized boolean isClosed() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1279 */       return isDetached();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1283 */       if (isDetached())
/*      */       {
/* 1285 */         throw SqlUtils.toSQLException("You can't operate on a closed Connection!!!", exc);
/*      */       }
/* 1287 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1291 */       if (!isDetached())
/*      */       {
/* 1293 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1295 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/* 1299 */   boolean txn_known_resolved = true;
/*      */   
/* 1301 */   DatabaseMetaData metaData = null;
/*      */   
/*      */   public Object rawConnectionOperation(Method m, Object target, Object[] args)
/*      */     throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException
/*      */   {
/* 1306 */     maybeDirtyTransaction();
/*      */     
/* 1308 */     if (this.inner == null)
/* 1309 */       throw new SQLException("You cannot operate on a closed Connection!");
/* 1310 */     if (target == C3P0ProxyConnection.RAW_CONNECTION)
/* 1311 */       target = this.inner;
/* 1312 */     int i = 0; for (int len = args.length; i < len; i++)
/* 1313 */       if (args[i] == C3P0ProxyConnection.RAW_CONNECTION)
/* 1314 */         args[i] = this.inner;
/* 1315 */     Object out = m.invoke(target, args);
/*      */     
/*      */ 
/* 1318 */     if ((out instanceof CallableStatement))
/*      */     {
/* 1320 */       CallableStatement innerStmt = (CallableStatement)out;
/* 1321 */       this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/* 1322 */       out = new NewProxyCallableStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */     }
/* 1324 */     else if ((out instanceof PreparedStatement))
/*      */     {
/* 1326 */       PreparedStatement innerStmt = (PreparedStatement)out;
/* 1327 */       this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/* 1328 */       out = new NewProxyPreparedStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */     }
/* 1330 */     else if ((out instanceof Statement))
/*      */     {
/* 1332 */       Statement innerStmt = (Statement)out;
/* 1333 */       this.parentPooledConnection.markActiveUncachedStatement(innerStmt);
/* 1334 */       out = new NewProxyStatement(innerStmt, this.parentPooledConnection, false, this);
/*      */     }
/* 1336 */     else if ((out instanceof ResultSet))
/*      */     {
/* 1338 */       ResultSet innerRs = (ResultSet)out;
/* 1339 */       this.parentPooledConnection.markActiveRawConnectionResultSet(innerRs);
/* 1340 */       out = new NewProxyResultSet(innerRs, this.parentPooledConnection, this.inner, this);
/*      */     }
/* 1342 */     else if ((out instanceof DatabaseMetaData)) {
/* 1343 */       out = new NewProxyDatabaseMetaData((DatabaseMetaData)out, this.parentPooledConnection); }
/* 1344 */     return out;
/*      */   }
/*      */   
/*      */ 
/* 1348 */   synchronized void maybeDirtyTransaction() { this.txn_known_resolved = false; }
/* 1349 */   private static final MLogger logger = MLog.getLogger("com.mchange.v2.c3p0.impl.NewProxyConnection");
/*      */   
/*      */   volatile NewPooledConnection parentPooledConnection;
/*      */   
/* 1353 */   ConnectionEventListener cel = new ConnectionEventListener()
/*      */   {
/*      */     public void connectionErrorOccurred(ConnectionEvent evt) {}
/*      */     
/*      */     public void connectionClosed(ConnectionEvent evt)
/*      */     {
/* 1359 */       NewProxyConnection.this.detach();
/*      */     }
/*      */   };
/*      */   
/*      */   void attach(NewPooledConnection parentPooledConnection) {
/* 1364 */     this.parentPooledConnection = parentPooledConnection;
/* 1365 */     parentPooledConnection.addConnectionEventListener(this.cel);
/*      */   }
/*      */   
/*      */   private void detach()
/*      */   {
/* 1370 */     this.parentPooledConnection.removeConnectionEventListener(this.cel);
/* 1371 */     this.parentPooledConnection = null;
/*      */   }
/*      */   
/*      */   NewProxyConnection(Connection inner, NewPooledConnection parentPooledConnection)
/*      */   {
/* 1376 */     this(inner);
/* 1377 */     attach(parentPooledConnection);
/*      */   }
/*      */   
/*      */   boolean isDetached() {
/* 1381 */     return this.parentPooledConnection == null;
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\NewProxyConnection.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */