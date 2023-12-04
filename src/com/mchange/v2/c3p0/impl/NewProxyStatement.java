/*      */ package com.mchange.v2.c3p0.impl;
/*      */ 
/*      */ import com.mchange.v2.c3p0.C3P0ProxyStatement;
/*      */ import com.mchange.v2.log.MLevel;
/*      */ import com.mchange.v2.log.MLog;
/*      */ import com.mchange.v2.log.MLogger;
/*      */ import com.mchange.v2.sql.SqlUtils;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.sql.Connection;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Statement;
/*      */ import javax.sql.ConnectionEvent;
/*      */ import javax.sql.ConnectionEventListener;
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class NewProxyStatement
/*      */   implements Statement, C3P0ProxyStatement
/*      */ {
/*      */   protected Statement inner;
/*      */   
/*      */   public NewProxyStatement(Statement inner)
/*      */   {
/*   27 */     this.inner = inner;
/*      */   }
/*      */   
/*      */   public final ResultSet executeQuery(String a) throws SQLException
/*      */   {
/*      */     try {
/*   33 */       maybeDirtyTransaction();
/*      */       
/*   35 */       ResultSet innerResultSet = this.inner.executeQuery(a);
/*   36 */       if (innerResultSet == null) return null;
/*   37 */       this.parentPooledConnection.markActiveResultSetForStatement(this.inner, innerResultSet);
/*   38 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*   42 */       if (isDetached())
/*      */       {
/*   44 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*   46 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*   50 */       if (!isDetached())
/*      */       {
/*   52 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*   54 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int executeUpdate(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*   62 */       maybeDirtyTransaction();
/*      */       
/*   64 */       return this.inner.executeUpdate(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*   68 */       if (isDetached())
/*      */       {
/*   70 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*   72 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*   76 */       if (!isDetached())
/*      */       {
/*   78 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*   80 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int executeUpdate(String a, String[] b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*   88 */       maybeDirtyTransaction();
/*      */       
/*   90 */       return this.inner.executeUpdate(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*   94 */       if (isDetached())
/*      */       {
/*   96 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*   98 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  102 */       if (!isDetached())
/*      */       {
/*  104 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  106 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int executeUpdate(String a, int b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  114 */       maybeDirtyTransaction();
/*      */       
/*  116 */       return this.inner.executeUpdate(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  120 */       if (isDetached())
/*      */       {
/*  122 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  124 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  128 */       if (!isDetached())
/*      */       {
/*  130 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  132 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int executeUpdate(String a, int[] b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  140 */       maybeDirtyTransaction();
/*      */       
/*  142 */       return this.inner.executeUpdate(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  146 */       if (isDetached())
/*      */       {
/*  148 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  150 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  154 */       if (!isDetached())
/*      */       {
/*  156 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  158 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxFieldSize() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  166 */       maybeDirtyTransaction();
/*      */       
/*  168 */       return this.inner.getMaxFieldSize();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  172 */       if (isDetached())
/*      */       {
/*  174 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  176 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  180 */       if (!isDetached())
/*      */       {
/*  182 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  184 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setMaxFieldSize(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  192 */       maybeDirtyTransaction();
/*      */       
/*  194 */       this.inner.setMaxFieldSize(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  198 */       if (isDetached())
/*      */       {
/*  200 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  202 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  206 */       if (!isDetached())
/*      */       {
/*  208 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  210 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxRows() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  218 */       maybeDirtyTransaction();
/*      */       
/*  220 */       return this.inner.getMaxRows();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  224 */       if (isDetached())
/*      */       {
/*  226 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  228 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  232 */       if (!isDetached())
/*      */       {
/*  234 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  236 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setMaxRows(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  244 */       maybeDirtyTransaction();
/*      */       
/*  246 */       this.inner.setMaxRows(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  250 */       if (isDetached())
/*      */       {
/*  252 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  254 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  258 */       if (!isDetached())
/*      */       {
/*  260 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  262 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setEscapeProcessing(boolean a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  270 */       maybeDirtyTransaction();
/*      */       
/*  272 */       this.inner.setEscapeProcessing(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  276 */       if (isDetached())
/*      */       {
/*  278 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  280 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  284 */       if (!isDetached())
/*      */       {
/*  286 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  288 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getQueryTimeout() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  296 */       maybeDirtyTransaction();
/*      */       
/*  298 */       return this.inner.getQueryTimeout();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  302 */       if (isDetached())
/*      */       {
/*  304 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  306 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  310 */       if (!isDetached())
/*      */       {
/*  312 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  314 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setQueryTimeout(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  322 */       maybeDirtyTransaction();
/*      */       
/*  324 */       this.inner.setQueryTimeout(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  328 */       if (isDetached())
/*      */       {
/*  330 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  332 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  336 */       if (!isDetached())
/*      */       {
/*  338 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  340 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final SQLWarning getWarnings() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  348 */       maybeDirtyTransaction();
/*      */       
/*  350 */       return this.inner.getWarnings();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  354 */       if (isDetached())
/*      */       {
/*  356 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  358 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  362 */       if (!isDetached())
/*      */       {
/*  364 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  366 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void clearWarnings() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  374 */       maybeDirtyTransaction();
/*      */       
/*  376 */       this.inner.clearWarnings();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  380 */       if (isDetached())
/*      */       {
/*  382 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  384 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  388 */       if (!isDetached())
/*      */       {
/*  390 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  392 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setCursorName(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  400 */       maybeDirtyTransaction();
/*      */       
/*  402 */       this.inner.setCursorName(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  406 */       if (isDetached())
/*      */       {
/*  408 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final ResultSet getResultSet() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  426 */       maybeDirtyTransaction();
/*      */       
/*  428 */       ResultSet innerResultSet = this.inner.getResultSet();
/*  429 */       if (innerResultSet == null) return null;
/*  430 */       this.parentPooledConnection.markActiveResultSetForStatement(this.inner, innerResultSet);
/*  431 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  435 */       if (isDetached())
/*      */       {
/*  437 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  439 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  443 */       if (!isDetached())
/*      */       {
/*  445 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  447 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getUpdateCount() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  455 */       maybeDirtyTransaction();
/*      */       
/*  457 */       return this.inner.getUpdateCount();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  461 */       if (isDetached())
/*      */       {
/*  463 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  465 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  469 */       if (!isDetached())
/*      */       {
/*  471 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  473 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean getMoreResults() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  481 */       maybeDirtyTransaction();
/*      */       
/*  483 */       return this.inner.getMoreResults();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  487 */       if (isDetached())
/*      */       {
/*  489 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  491 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  495 */       if (!isDetached())
/*      */       {
/*  497 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  499 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean getMoreResults(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  507 */       maybeDirtyTransaction();
/*      */       
/*  509 */       return this.inner.getMoreResults(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  513 */       if (isDetached())
/*      */       {
/*  515 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  517 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  521 */       if (!isDetached())
/*      */       {
/*  523 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  525 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setFetchDirection(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  533 */       maybeDirtyTransaction();
/*      */       
/*  535 */       this.inner.setFetchDirection(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  539 */       if (isDetached())
/*      */       {
/*  541 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  543 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  547 */       if (!isDetached())
/*      */       {
/*  549 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  551 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getFetchDirection() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  559 */       maybeDirtyTransaction();
/*      */       
/*  561 */       return this.inner.getFetchDirection();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  565 */       if (isDetached())
/*      */       {
/*  567 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  569 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  573 */       if (!isDetached())
/*      */       {
/*  575 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  577 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setFetchSize(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  585 */       maybeDirtyTransaction();
/*      */       
/*  587 */       this.inner.setFetchSize(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  591 */       if (isDetached())
/*      */       {
/*  593 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  595 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  599 */       if (!isDetached())
/*      */       {
/*  601 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  603 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getFetchSize() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  611 */       maybeDirtyTransaction();
/*      */       
/*  613 */       return this.inner.getFetchSize();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  617 */       if (isDetached())
/*      */       {
/*  619 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  621 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  625 */       if (!isDetached())
/*      */       {
/*  627 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  629 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getResultSetConcurrency() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  637 */       maybeDirtyTransaction();
/*      */       
/*  639 */       return this.inner.getResultSetConcurrency();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  643 */       if (isDetached())
/*      */       {
/*  645 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  647 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  651 */       if (!isDetached())
/*      */       {
/*  653 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  655 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getResultSetType() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  663 */       maybeDirtyTransaction();
/*      */       
/*  665 */       return this.inner.getResultSetType();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  669 */       if (isDetached())
/*      */       {
/*  671 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  673 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  677 */       if (!isDetached())
/*      */       {
/*  679 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  681 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void addBatch(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  689 */       maybeDirtyTransaction();
/*      */       
/*  691 */       this.inner.addBatch(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  695 */       if (isDetached())
/*      */       {
/*  697 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  699 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  703 */       if (!isDetached())
/*      */       {
/*  705 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  707 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void clearBatch() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  715 */       maybeDirtyTransaction();
/*      */       
/*  717 */       this.inner.clearBatch();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  721 */       if (isDetached())
/*      */       {
/*  723 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  725 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  729 */       if (!isDetached())
/*      */       {
/*  731 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  733 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int[] executeBatch() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  741 */       maybeDirtyTransaction();
/*      */       
/*  743 */       return this.inner.executeBatch();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  747 */       if (isDetached())
/*      */       {
/*  749 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  751 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  755 */       if (!isDetached())
/*      */       {
/*  757 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  759 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getGeneratedKeys() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  767 */       maybeDirtyTransaction();
/*      */       
/*  769 */       ResultSet innerResultSet = this.inner.getGeneratedKeys();
/*  770 */       if (innerResultSet == null) return null;
/*  771 */       this.parentPooledConnection.markActiveResultSetForStatement(this.inner, innerResultSet);
/*  772 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  776 */       if (isDetached())
/*      */       {
/*  778 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  780 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  784 */       if (!isDetached())
/*      */       {
/*  786 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  788 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getResultSetHoldability() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  796 */       maybeDirtyTransaction();
/*      */       
/*  798 */       return this.inner.getResultSetHoldability();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  802 */       if (isDetached())
/*      */       {
/*  804 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  806 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  810 */       if (!isDetached())
/*      */       {
/*  812 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  814 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void close() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  822 */       maybeDirtyTransaction();
/*      */       
/*  824 */       if (!isDetached())
/*      */       {
/*  826 */         if (this.is_cached) {
/*  827 */           this.parentPooledConnection.checkinStatement(this.inner);
/*      */         }
/*      */         else {
/*  830 */           this.parentPooledConnection.markInactiveUncachedStatement(this.inner);
/*  831 */           try { this.inner.close();
/*      */           }
/*      */           catch (Exception e) {
/*  834 */             if (logger.isLoggable(MLevel.WARNING))
/*  835 */               logger.log(MLevel.WARNING, "Exception on close of inner statement.", e);
/*  836 */             SQLException sqle = SqlUtils.toSQLException(e);
/*  837 */             throw sqle;
/*      */           }
/*      */         }
/*      */         
/*  841 */         detach();
/*  842 */         this.inner = null;
/*  843 */         this.creatorProxy = null;
/*      */       }
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  848 */       if (isDetached())
/*      */       {
/*  850 */         if (logger.isLoggable(MLevel.FINE))
/*      */         {
/*  852 */           logger.log(MLevel.FINE, this + ": close() called more than once.");
/*      */         }
/*      */       } else {
/*  855 */         throw exc;
/*      */       }
/*      */     }
/*      */     catch (Exception exc) {
/*  859 */       if (!isDetached())
/*      */       {
/*  861 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  863 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void cancel() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  871 */       maybeDirtyTransaction();
/*      */       
/*  873 */       this.inner.cancel();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  877 */       if (isDetached())
/*      */       {
/*  879 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  881 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  885 */       if (!isDetached())
/*      */       {
/*  887 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  889 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Connection getConnection() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  897 */       maybeDirtyTransaction();
/*      */       
/*  899 */       if (!isDetached()) {
/*  900 */         return this.creatorProxy;
/*      */       }
/*  902 */       throw new SQLException("You cannot operate on a closed Statement!");
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  906 */       if (isDetached())
/*      */       {
/*  908 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  910 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  914 */       if (!isDetached())
/*      */       {
/*  916 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  918 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean execute(String a, int b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  926 */       maybeDirtyTransaction();
/*      */       
/*  928 */       return this.inner.execute(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  932 */       if (isDetached())
/*      */       {
/*  934 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  936 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  940 */       if (!isDetached())
/*      */       {
/*  942 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  944 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean execute(String a, int[] b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  952 */       maybeDirtyTransaction();
/*      */       
/*  954 */       return this.inner.execute(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  958 */       if (isDetached())
/*      */       {
/*  960 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  962 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  966 */       if (!isDetached())
/*      */       {
/*  968 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  970 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean execute(String a, String[] b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  978 */       maybeDirtyTransaction();
/*      */       
/*  980 */       return this.inner.execute(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  984 */       if (isDetached())
/*      */       {
/*  986 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  988 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  992 */       if (!isDetached())
/*      */       {
/*  994 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  996 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean execute(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1004 */       maybeDirtyTransaction();
/*      */       
/* 1006 */       return this.inner.execute(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1010 */       if (isDetached())
/*      */       {
/* 1012 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1014 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1018 */       if (!isDetached())
/*      */       {
/* 1020 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1022 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/* 1026 */   private static final MLogger logger = MLog.getLogger("com.mchange.v2.c3p0.impl.NewProxyStatement");
/*      */   
/*      */   volatile NewPooledConnection parentPooledConnection;
/*      */   
/* 1030 */   ConnectionEventListener cel = new ConnectionEventListener()
/*      */   {
/*      */     public void connectionErrorOccurred(ConnectionEvent evt) {}
/*      */     
/*      */ 
/*      */ 
/* 1036 */     public void connectionClosed(ConnectionEvent evt) { NewProxyStatement.this.detach(); }
/*      */   };
/*      */   boolean is_cached;
/*      */   NewProxyConnection creatorProxy;
/*      */   
/* 1041 */   void attach(NewPooledConnection parentPooledConnection) { this.parentPooledConnection = parentPooledConnection;
/* 1042 */     parentPooledConnection.addConnectionEventListener(this.cel);
/*      */   }
/*      */   
/*      */   private void detach()
/*      */   {
/* 1047 */     this.parentPooledConnection.removeConnectionEventListener(this.cel);
/* 1048 */     this.parentPooledConnection = null;
/*      */   }
/*      */   
/*      */   NewProxyStatement(Statement inner, NewPooledConnection parentPooledConnection)
/*      */   {
/* 1053 */     this(inner);
/* 1054 */     attach(parentPooledConnection);
/*      */   }
/*      */   
/*      */   boolean isDetached() {
/* 1058 */     return this.parentPooledConnection == null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   NewProxyStatement(Statement inner, NewPooledConnection parentPooledConnection, boolean cached, NewProxyConnection cProxy)
/*      */   {
/* 1065 */     this(inner, parentPooledConnection);
/* 1066 */     this.is_cached = cached;
/* 1067 */     this.creatorProxy = cProxy;
/*      */   }
/*      */   
/*      */   public Object rawStatementOperation(Method m, Object target, Object[] args) throws IllegalAccessException, InvocationTargetException, SQLException
/*      */   {
/* 1072 */     maybeDirtyTransaction();
/*      */     
/* 1074 */     if (target == C3P0ProxyStatement.RAW_STATEMENT) target = this.inner;
/* 1075 */     int i = 0; for (int len = args.length; i < len; i++)
/* 1076 */       if (args[i] == C3P0ProxyStatement.RAW_STATEMENT) args[i] = this.inner;
/* 1077 */     Object out = m.invoke(target, args);
/* 1078 */     if ((out instanceof ResultSet))
/*      */     {
/* 1080 */       ResultSet innerResultSet = (ResultSet)out;
/* 1081 */       this.parentPooledConnection.markActiveResultSetForStatement(this.inner, innerResultSet);
/* 1082 */       out = new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     
/* 1085 */     return out;
/*      */   }
/*      */   
/*      */   void maybeDirtyTransaction() {
/* 1089 */     this.creatorProxy.maybeDirtyTransaction();
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\NewProxyStatement.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */