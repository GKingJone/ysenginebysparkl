/*      */ package com.mchange.v2.c3p0.impl;
/*      */ 
/*      */ import com.mchange.v2.log.MLevel;
/*      */ import com.mchange.v2.log.MLog;
/*      */ import com.mchange.v2.log.MLogger;
/*      */ import com.mchange.v2.sql.SqlUtils;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Connection;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.Date;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Statement;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import java.util.Map;
/*      */ import javax.sql.ConnectionEvent;
/*      */ import javax.sql.ConnectionEventListener;
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class NewProxyResultSet
/*      */   implements ResultSet
/*      */ {
/*      */   protected ResultSet inner;
/*      */   
/*      */   public NewProxyResultSet(ResultSet inner)
/*      */   {
/*   39 */     this.inner = inner;
/*      */   }
/*      */   
/*      */   public final ResultSetMetaData getMetaData() throws SQLException
/*      */   {
/*      */     try {
/*   45 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*   47 */       return this.inner.getMetaData();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*   51 */       if (isDetached())
/*      */       {
/*   53 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*   55 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*   59 */       if (!isDetached())
/*      */       {
/*   61 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*   63 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Statement getStatement() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*   71 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*   73 */       if ((this.creator instanceof Statement))
/*   74 */         return (Statement)this.creatorProxy;
/*   75 */       if ((this.creator instanceof DatabaseMetaData))
/*   76 */         return null;
/*   77 */       throw new InternalError("Must be Statement or DatabaseMetaData -- Bad Creator: " + this.creator);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*   81 */       if (isDetached())
/*      */       {
/*   83 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*   85 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*   89 */       if (!isDetached())
/*      */       {
/*   91 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*   93 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final SQLWarning getWarnings() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  101 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  103 */       return this.inner.getWarnings();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  107 */       if (isDetached())
/*      */       {
/*  109 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  111 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  115 */       if (!isDetached())
/*      */       {
/*  117 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  119 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void clearWarnings() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  127 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  129 */       this.inner.clearWarnings();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  133 */       if (isDetached())
/*      */       {
/*  135 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  137 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  141 */       if (!isDetached())
/*      */       {
/*  143 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  145 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setFetchDirection(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  153 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  155 */       this.inner.setFetchDirection(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  159 */       if (isDetached())
/*      */       {
/*  161 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  163 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  167 */       if (!isDetached())
/*      */       {
/*  169 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  171 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getFetchDirection() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  179 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  181 */       return this.inner.getFetchDirection();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  185 */       if (isDetached())
/*      */       {
/*  187 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  189 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  193 */       if (!isDetached())
/*      */       {
/*  195 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  197 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setFetchSize(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  205 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  207 */       this.inner.setFetchSize(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  211 */       if (isDetached())
/*      */       {
/*  213 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  215 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  219 */       if (!isDetached())
/*      */       {
/*  221 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  223 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getFetchSize() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  231 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  233 */       return this.inner.getFetchSize();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  237 */       if (isDetached())
/*      */       {
/*  239 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  241 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  245 */       if (!isDetached())
/*      */       {
/*  247 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  249 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean wasNull() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  257 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  259 */       return this.inner.wasNull();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  263 */       if (isDetached())
/*      */       {
/*  265 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  267 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  271 */       if (!isDetached())
/*      */       {
/*  273 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  275 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Blob getBlob(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  283 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  285 */       return this.inner.getBlob(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  289 */       if (isDetached())
/*      */       {
/*  291 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  293 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  297 */       if (!isDetached())
/*      */       {
/*  299 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  301 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Blob getBlob(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  309 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  311 */       return this.inner.getBlob(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  315 */       if (isDetached())
/*      */       {
/*  317 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  319 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  323 */       if (!isDetached())
/*      */       {
/*  325 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  327 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Clob getClob(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  335 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  337 */       return this.inner.getClob(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  341 */       if (isDetached())
/*      */       {
/*  343 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  345 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  349 */       if (!isDetached())
/*      */       {
/*  351 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  353 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Clob getClob(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  361 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  363 */       return this.inner.getClob(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  367 */       if (isDetached())
/*      */       {
/*  369 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  371 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  375 */       if (!isDetached())
/*      */       {
/*  377 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  379 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final InputStream getAsciiStream(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  387 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  389 */       return this.inner.getAsciiStream(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  393 */       if (isDetached())
/*      */       {
/*  395 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  397 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  401 */       if (!isDetached())
/*      */       {
/*  403 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  405 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final InputStream getAsciiStream(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  413 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  415 */       return this.inner.getAsciiStream(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  419 */       if (isDetached())
/*      */       {
/*  421 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  423 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  427 */       if (!isDetached())
/*      */       {
/*  429 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  431 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final InputStream getUnicodeStream(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  439 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  441 */       return this.inner.getUnicodeStream(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  445 */       if (isDetached())
/*      */       {
/*  447 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  449 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  453 */       if (!isDetached())
/*      */       {
/*  455 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  457 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final InputStream getUnicodeStream(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  465 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  467 */       return this.inner.getUnicodeStream(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  471 */       if (isDetached())
/*      */       {
/*  473 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  475 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  479 */       if (!isDetached())
/*      */       {
/*  481 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  483 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final InputStream getBinaryStream(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  491 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  493 */       return this.inner.getBinaryStream(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  497 */       if (isDetached())
/*      */       {
/*  499 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  501 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  505 */       if (!isDetached())
/*      */       {
/*  507 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  509 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final InputStream getBinaryStream(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  517 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  519 */       return this.inner.getBinaryStream(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  523 */       if (isDetached())
/*      */       {
/*  525 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  527 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  531 */       if (!isDetached())
/*      */       {
/*  533 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  535 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final String getCursorName() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  543 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  545 */       return this.inner.getCursorName();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  549 */       if (isDetached())
/*      */       {
/*  551 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  553 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  557 */       if (!isDetached())
/*      */       {
/*  559 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  561 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int findColumn(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  569 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  571 */       return this.inner.findColumn(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  575 */       if (isDetached())
/*      */       {
/*  577 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  579 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  583 */       if (!isDetached())
/*      */       {
/*  585 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  587 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Reader getCharacterStream(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  595 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  597 */       return this.inner.getCharacterStream(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  601 */       if (isDetached())
/*      */       {
/*  603 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  605 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  609 */       if (!isDetached())
/*      */       {
/*  611 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  613 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Reader getCharacterStream(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  621 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  623 */       return this.inner.getCharacterStream(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  627 */       if (isDetached())
/*      */       {
/*  629 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  631 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  635 */       if (!isDetached())
/*      */       {
/*  637 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  639 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean isBeforeFirst() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  647 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  649 */       return this.inner.isBeforeFirst();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  653 */       if (isDetached())
/*      */       {
/*  655 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  657 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  661 */       if (!isDetached())
/*      */       {
/*  663 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  665 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean isAfterLast() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  673 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  675 */       return this.inner.isAfterLast();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  679 */       if (isDetached())
/*      */       {
/*  681 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  683 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  687 */       if (!isDetached())
/*      */       {
/*  689 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  691 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean isLast() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  699 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  701 */       return this.inner.isLast();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  705 */       if (isDetached())
/*      */       {
/*  707 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  709 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  713 */       if (!isDetached())
/*      */       {
/*  715 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  717 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void beforeFirst() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  725 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  727 */       this.inner.beforeFirst();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  731 */       if (isDetached())
/*      */       {
/*  733 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
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
/*      */   public final void afterLast() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  751 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  753 */       this.inner.afterLast();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  757 */       if (isDetached())
/*      */       {
/*  759 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  761 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  765 */       if (!isDetached())
/*      */       {
/*  767 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  769 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getRow() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  777 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  779 */       return this.inner.getRow();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  783 */       if (isDetached())
/*      */       {
/*  785 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  787 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  791 */       if (!isDetached())
/*      */       {
/*  793 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  795 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean absolute(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  803 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  805 */       return this.inner.absolute(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  809 */       if (isDetached())
/*      */       {
/*  811 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  813 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  817 */       if (!isDetached())
/*      */       {
/*  819 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  821 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean relative(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  829 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  831 */       return this.inner.relative(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  835 */       if (isDetached())
/*      */       {
/*  837 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  839 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  843 */       if (!isDetached())
/*      */       {
/*  845 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  847 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getConcurrency() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  855 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  857 */       return this.inner.getConcurrency();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  861 */       if (isDetached())
/*      */       {
/*  863 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  865 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  869 */       if (!isDetached())
/*      */       {
/*  871 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  873 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean rowUpdated() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  881 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  883 */       return this.inner.rowUpdated();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  887 */       if (isDetached())
/*      */       {
/*  889 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  891 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  895 */       if (!isDetached())
/*      */       {
/*  897 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  899 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean rowInserted() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  907 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  909 */       return this.inner.rowInserted();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  913 */       if (isDetached())
/*      */       {
/*  915 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  917 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  921 */       if (!isDetached())
/*      */       {
/*  923 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  925 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean rowDeleted() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  933 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  935 */       return this.inner.rowDeleted();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  939 */       if (isDetached())
/*      */       {
/*  941 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  943 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  947 */       if (!isDetached())
/*      */       {
/*  949 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  951 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateNull(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  959 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  961 */       this.inner.updateNull(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  965 */       if (isDetached())
/*      */       {
/*  967 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  969 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  973 */       if (!isDetached())
/*      */       {
/*  975 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  977 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateNull(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  985 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/*  987 */       this.inner.updateNull(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  991 */       if (isDetached())
/*      */       {
/*  993 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/*  995 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  999 */       if (!isDetached())
/*      */       {
/* 1001 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1003 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateBoolean(int a, boolean b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1011 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1013 */       this.inner.updateBoolean(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1017 */       if (isDetached())
/*      */       {
/* 1019 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1021 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1025 */       if (!isDetached())
/*      */       {
/* 1027 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1029 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateBoolean(String a, boolean b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1037 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1039 */       this.inner.updateBoolean(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1043 */       if (isDetached())
/*      */       {
/* 1045 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1047 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1051 */       if (!isDetached())
/*      */       {
/* 1053 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1055 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateByte(int a, byte b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1063 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1065 */       this.inner.updateByte(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1069 */       if (isDetached())
/*      */       {
/* 1071 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1073 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1077 */       if (!isDetached())
/*      */       {
/* 1079 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1081 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateByte(String a, byte b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1089 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1091 */       this.inner.updateByte(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1095 */       if (isDetached())
/*      */       {
/* 1097 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1099 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1103 */       if (!isDetached())
/*      */       {
/* 1105 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1107 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateShort(int a, short b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1115 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1117 */       this.inner.updateShort(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1121 */       if (isDetached())
/*      */       {
/* 1123 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1125 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1129 */       if (!isDetached())
/*      */       {
/* 1131 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1133 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateShort(String a, short b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1141 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1143 */       this.inner.updateShort(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1147 */       if (isDetached())
/*      */       {
/* 1149 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1151 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1155 */       if (!isDetached())
/*      */       {
/* 1157 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1159 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateInt(String a, int b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1167 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1169 */       this.inner.updateInt(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1173 */       if (isDetached())
/*      */       {
/* 1175 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1177 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1181 */       if (!isDetached())
/*      */       {
/* 1183 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1185 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateInt(int a, int b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1193 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1195 */       this.inner.updateInt(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1199 */       if (isDetached())
/*      */       {
/* 1201 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1203 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1207 */       if (!isDetached())
/*      */       {
/* 1209 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1211 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateLong(int a, long b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1219 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1221 */       this.inner.updateLong(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1225 */       if (isDetached())
/*      */       {
/* 1227 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1229 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1233 */       if (!isDetached())
/*      */       {
/* 1235 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1237 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateLong(String a, long b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1245 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1247 */       this.inner.updateLong(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1251 */       if (isDetached())
/*      */       {
/* 1253 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1255 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1259 */       if (!isDetached())
/*      */       {
/* 1261 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1263 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateFloat(int a, float b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1271 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1273 */       this.inner.updateFloat(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1277 */       if (isDetached())
/*      */       {
/* 1279 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1281 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1285 */       if (!isDetached())
/*      */       {
/* 1287 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1289 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateFloat(String a, float b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1297 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1299 */       this.inner.updateFloat(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1303 */       if (isDetached())
/*      */       {
/* 1305 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1307 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1311 */       if (!isDetached())
/*      */       {
/* 1313 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1315 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateDouble(String a, double b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1323 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1325 */       this.inner.updateDouble(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1329 */       if (isDetached())
/*      */       {
/* 1331 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1333 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1337 */       if (!isDetached())
/*      */       {
/* 1339 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1341 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateDouble(int a, double b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1349 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1351 */       this.inner.updateDouble(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1355 */       if (isDetached())
/*      */       {
/* 1357 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1359 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1363 */       if (!isDetached())
/*      */       {
/* 1365 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1367 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateBigDecimal(String a, BigDecimal b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1375 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1377 */       this.inner.updateBigDecimal(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1381 */       if (isDetached())
/*      */       {
/* 1383 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1385 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1389 */       if (!isDetached())
/*      */       {
/* 1391 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1393 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateBigDecimal(int a, BigDecimal b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1401 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1403 */       this.inner.updateBigDecimal(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1407 */       if (isDetached())
/*      */       {
/* 1409 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1411 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1415 */       if (!isDetached())
/*      */       {
/* 1417 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1419 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateString(String a, String b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1427 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1429 */       this.inner.updateString(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1433 */       if (isDetached())
/*      */       {
/* 1435 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1437 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1441 */       if (!isDetached())
/*      */       {
/* 1443 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1445 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateString(int a, String b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1453 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1455 */       this.inner.updateString(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1459 */       if (isDetached())
/*      */       {
/* 1461 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1463 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1467 */       if (!isDetached())
/*      */       {
/* 1469 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1471 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateBytes(String a, byte[] b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1479 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1481 */       this.inner.updateBytes(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1485 */       if (isDetached())
/*      */       {
/* 1487 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1489 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1493 */       if (!isDetached())
/*      */       {
/* 1495 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1497 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateBytes(int a, byte[] b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1505 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1507 */       this.inner.updateBytes(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1511 */       if (isDetached())
/*      */       {
/* 1513 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1515 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1519 */       if (!isDetached())
/*      */       {
/* 1521 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1523 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateDate(int a, Date b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1531 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1533 */       this.inner.updateDate(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1537 */       if (isDetached())
/*      */       {
/* 1539 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1541 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1545 */       if (!isDetached())
/*      */       {
/* 1547 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1549 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateDate(String a, Date b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1557 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1559 */       this.inner.updateDate(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1563 */       if (isDetached())
/*      */       {
/* 1565 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1567 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1571 */       if (!isDetached())
/*      */       {
/* 1573 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1575 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateTimestamp(String a, Timestamp b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1583 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1585 */       this.inner.updateTimestamp(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1589 */       if (isDetached())
/*      */       {
/* 1591 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1593 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1597 */       if (!isDetached())
/*      */       {
/* 1599 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1601 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateTimestamp(int a, Timestamp b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1609 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1611 */       this.inner.updateTimestamp(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1615 */       if (isDetached())
/*      */       {
/* 1617 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1619 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1623 */       if (!isDetached())
/*      */       {
/* 1625 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1627 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateAsciiStream(String a, InputStream b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1635 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1637 */       this.inner.updateAsciiStream(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1641 */       if (isDetached())
/*      */       {
/* 1643 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1645 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1649 */       if (!isDetached())
/*      */       {
/* 1651 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1653 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateAsciiStream(int a, InputStream b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1661 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1663 */       this.inner.updateAsciiStream(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1667 */       if (isDetached())
/*      */       {
/* 1669 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1671 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1675 */       if (!isDetached())
/*      */       {
/* 1677 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1679 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateBinaryStream(String a, InputStream b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1687 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1689 */       this.inner.updateBinaryStream(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1693 */       if (isDetached())
/*      */       {
/* 1695 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1697 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1701 */       if (!isDetached())
/*      */       {
/* 1703 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1705 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateBinaryStream(int a, InputStream b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1713 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1715 */       this.inner.updateBinaryStream(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1719 */       if (isDetached())
/*      */       {
/* 1721 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1723 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1727 */       if (!isDetached())
/*      */       {
/* 1729 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1731 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateCharacterStream(String a, Reader b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1739 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1741 */       this.inner.updateCharacterStream(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1745 */       if (isDetached())
/*      */       {
/* 1747 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1749 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1753 */       if (!isDetached())
/*      */       {
/* 1755 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1757 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateCharacterStream(int a, Reader b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1765 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1767 */       this.inner.updateCharacterStream(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1771 */       if (isDetached())
/*      */       {
/* 1773 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1775 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1779 */       if (!isDetached())
/*      */       {
/* 1781 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1783 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateObject(int a, Object b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1791 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1793 */       this.inner.updateObject(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1797 */       if (isDetached())
/*      */       {
/* 1799 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1801 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1805 */       if (!isDetached())
/*      */       {
/* 1807 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1809 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateObject(int a, Object b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1817 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1819 */       this.inner.updateObject(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1823 */       if (isDetached())
/*      */       {
/* 1825 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1827 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1831 */       if (!isDetached())
/*      */       {
/* 1833 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1835 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateObject(String a, Object b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1843 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1845 */       this.inner.updateObject(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1849 */       if (isDetached())
/*      */       {
/* 1851 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1853 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1857 */       if (!isDetached())
/*      */       {
/* 1859 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1861 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateObject(String a, Object b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1869 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1871 */       this.inner.updateObject(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1875 */       if (isDetached())
/*      */       {
/* 1877 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1879 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1883 */       if (!isDetached())
/*      */       {
/* 1885 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1887 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void insertRow() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1895 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1897 */       this.inner.insertRow();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1901 */       if (isDetached())
/*      */       {
/* 1903 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1905 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1909 */       if (!isDetached())
/*      */       {
/* 1911 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1913 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateRow() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1921 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1923 */       this.inner.updateRow();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1927 */       if (isDetached())
/*      */       {
/* 1929 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1931 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1935 */       if (!isDetached())
/*      */       {
/* 1937 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1939 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void deleteRow() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1947 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1949 */       this.inner.deleteRow();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1953 */       if (isDetached())
/*      */       {
/* 1955 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1957 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1961 */       if (!isDetached())
/*      */       {
/* 1963 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1965 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void refreshRow() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1973 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 1975 */       this.inner.refreshRow();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1979 */       if (isDetached())
/*      */       {
/* 1981 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 1983 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1987 */       if (!isDetached())
/*      */       {
/* 1989 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1991 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void cancelRowUpdates() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1999 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2001 */       this.inner.cancelRowUpdates();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2005 */       if (isDetached())
/*      */       {
/* 2007 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2009 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2013 */       if (!isDetached())
/*      */       {
/* 2015 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2017 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void moveToInsertRow() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2025 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2027 */       this.inner.moveToInsertRow();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2031 */       if (isDetached())
/*      */       {
/* 2033 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2035 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2039 */       if (!isDetached())
/*      */       {
/* 2041 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2043 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void moveToCurrentRow() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2051 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2053 */       this.inner.moveToCurrentRow();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2057 */       if (isDetached())
/*      */       {
/* 2059 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2061 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2065 */       if (!isDetached())
/*      */       {
/* 2067 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2069 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateRef(String a, Ref b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2077 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2079 */       this.inner.updateRef(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2083 */       if (isDetached())
/*      */       {
/* 2085 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2087 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2091 */       if (!isDetached())
/*      */       {
/* 2093 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2095 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateRef(int a, Ref b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2103 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2105 */       this.inner.updateRef(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2109 */       if (isDetached())
/*      */       {
/* 2111 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2113 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2117 */       if (!isDetached())
/*      */       {
/* 2119 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2121 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateBlob(String a, Blob b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2129 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2131 */       this.inner.updateBlob(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2135 */       if (isDetached())
/*      */       {
/* 2137 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2139 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2143 */       if (!isDetached())
/*      */       {
/* 2145 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2147 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateBlob(int a, Blob b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2155 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2157 */       this.inner.updateBlob(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2161 */       if (isDetached())
/*      */       {
/* 2163 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2165 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2169 */       if (!isDetached())
/*      */       {
/* 2171 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2173 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateClob(String a, Clob b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2181 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2183 */       this.inner.updateClob(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2187 */       if (isDetached())
/*      */       {
/* 2189 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2191 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2195 */       if (!isDetached())
/*      */       {
/* 2197 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2199 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateClob(int a, Clob b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2207 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2209 */       this.inner.updateClob(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2213 */       if (isDetached())
/*      */       {
/* 2215 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2217 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2221 */       if (!isDetached())
/*      */       {
/* 2223 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2225 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateArray(int a, Array b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2233 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2235 */       this.inner.updateArray(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2239 */       if (isDetached())
/*      */       {
/* 2241 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2243 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2247 */       if (!isDetached())
/*      */       {
/* 2249 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2251 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateArray(String a, Array b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2259 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2261 */       this.inner.updateArray(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2265 */       if (isDetached())
/*      */       {
/* 2267 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2269 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2273 */       if (!isDetached())
/*      */       {
/* 2275 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2277 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Object getObject(String a, Map b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2285 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2287 */       return this.inner.getObject(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2291 */       if (isDetached())
/*      */       {
/* 2293 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2295 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2299 */       if (!isDetached())
/*      */       {
/* 2301 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2303 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Object getObject(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2311 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2313 */       return this.inner.getObject(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2317 */       if (isDetached())
/*      */       {
/* 2319 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2321 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2325 */       if (!isDetached())
/*      */       {
/* 2327 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2329 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Object getObject(int a, Map b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2337 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2339 */       return this.inner.getObject(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2343 */       if (isDetached())
/*      */       {
/* 2345 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2347 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2351 */       if (!isDetached())
/*      */       {
/* 2353 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2355 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Object getObject(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2363 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2365 */       return this.inner.getObject(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2369 */       if (isDetached())
/*      */       {
/* 2371 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2373 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2377 */       if (!isDetached())
/*      */       {
/* 2379 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2381 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean getBoolean(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2389 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2391 */       return this.inner.getBoolean(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2395 */       if (isDetached())
/*      */       {
/* 2397 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2399 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2403 */       if (!isDetached())
/*      */       {
/* 2405 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2407 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean getBoolean(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2415 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2417 */       return this.inner.getBoolean(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2421 */       if (isDetached())
/*      */       {
/* 2423 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2425 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2429 */       if (!isDetached())
/*      */       {
/* 2431 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2433 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final byte getByte(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2441 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2443 */       return this.inner.getByte(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2447 */       if (isDetached())
/*      */       {
/* 2449 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2451 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2455 */       if (!isDetached())
/*      */       {
/* 2457 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2459 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final byte getByte(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2467 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2469 */       return this.inner.getByte(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2473 */       if (isDetached())
/*      */       {
/* 2475 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2477 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2481 */       if (!isDetached())
/*      */       {
/* 2483 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2485 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final short getShort(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2493 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2495 */       return this.inner.getShort(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2499 */       if (isDetached())
/*      */       {
/* 2501 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2503 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2507 */       if (!isDetached())
/*      */       {
/* 2509 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2511 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final short getShort(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2519 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2521 */       return this.inner.getShort(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2525 */       if (isDetached())
/*      */       {
/* 2527 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2529 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2533 */       if (!isDetached())
/*      */       {
/* 2535 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2537 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getInt(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2545 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2547 */       return this.inner.getInt(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2551 */       if (isDetached())
/*      */       {
/* 2553 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2555 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2559 */       if (!isDetached())
/*      */       {
/* 2561 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2563 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getInt(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2571 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2573 */       return this.inner.getInt(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2577 */       if (isDetached())
/*      */       {
/* 2579 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2581 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2585 */       if (!isDetached())
/*      */       {
/* 2587 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2589 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final long getLong(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2597 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2599 */       return this.inner.getLong(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2603 */       if (isDetached())
/*      */       {
/* 2605 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2607 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2611 */       if (!isDetached())
/*      */       {
/* 2613 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2615 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final long getLong(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2623 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2625 */       return this.inner.getLong(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2629 */       if (isDetached())
/*      */       {
/* 2631 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2633 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2637 */       if (!isDetached())
/*      */       {
/* 2639 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2641 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final float getFloat(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2649 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2651 */       return this.inner.getFloat(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2655 */       if (isDetached())
/*      */       {
/* 2657 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2659 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2663 */       if (!isDetached())
/*      */       {
/* 2665 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2667 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final float getFloat(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2675 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2677 */       return this.inner.getFloat(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2681 */       if (isDetached())
/*      */       {
/* 2683 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2685 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2689 */       if (!isDetached())
/*      */       {
/* 2691 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2693 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final double getDouble(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2701 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2703 */       return this.inner.getDouble(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2707 */       if (isDetached())
/*      */       {
/* 2709 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2711 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2715 */       if (!isDetached())
/*      */       {
/* 2717 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2719 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final double getDouble(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2727 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2729 */       return this.inner.getDouble(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2733 */       if (isDetached())
/*      */       {
/* 2735 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2737 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2741 */       if (!isDetached())
/*      */       {
/* 2743 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2745 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final byte[] getBytes(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2753 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2755 */       return this.inner.getBytes(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2759 */       if (isDetached())
/*      */       {
/* 2761 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2763 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2767 */       if (!isDetached())
/*      */       {
/* 2769 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2771 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final byte[] getBytes(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2779 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2781 */       return this.inner.getBytes(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2785 */       if (isDetached())
/*      */       {
/* 2787 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2789 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2793 */       if (!isDetached())
/*      */       {
/* 2795 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2797 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Array getArray(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2805 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2807 */       return this.inner.getArray(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2811 */       if (isDetached())
/*      */       {
/* 2813 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2815 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2819 */       if (!isDetached())
/*      */       {
/* 2821 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2823 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Array getArray(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2831 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2833 */       return this.inner.getArray(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2837 */       if (isDetached())
/*      */       {
/* 2839 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2841 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2845 */       if (!isDetached())
/*      */       {
/* 2847 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2849 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean next() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2857 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2859 */       return this.inner.next();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2863 */       if (isDetached())
/*      */       {
/* 2865 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2867 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2871 */       if (!isDetached())
/*      */       {
/* 2873 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2875 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final URL getURL(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2883 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2885 */       return this.inner.getURL(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2889 */       if (isDetached())
/*      */       {
/* 2891 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2893 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2897 */       if (!isDetached())
/*      */       {
/* 2899 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2901 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final URL getURL(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2909 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2911 */       return this.inner.getURL(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2915 */       if (isDetached())
/*      */       {
/* 2917 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2919 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2923 */       if (!isDetached())
/*      */       {
/* 2925 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2927 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getType() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2935 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2937 */       return this.inner.getType();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2941 */       if (isDetached())
/*      */       {
/* 2943 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2945 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2949 */       if (!isDetached())
/*      */       {
/* 2951 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2953 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean previous() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2961 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2963 */       return this.inner.previous();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2967 */       if (isDetached())
/*      */       {
/* 2969 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 2971 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2975 */       if (!isDetached())
/*      */       {
/* 2977 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2979 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void close() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2987 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 2989 */       if (!isDetached())
/*      */       {
/* 2991 */         if ((this.creator instanceof Statement)) {
/* 2992 */           this.parentPooledConnection.markInactiveResultSetForStatement((Statement)this.creator, this.inner);
/* 2993 */         } else if ((this.creator instanceof DatabaseMetaData)) {
/* 2994 */           this.parentPooledConnection.markInactiveMetaDataResultSet(this.inner);
/* 2995 */         } else if ((this.creator instanceof Connection))
/* 2996 */           this.parentPooledConnection.markInactiveRawConnectionResultSet(this.inner); else
/* 2997 */           throw new InternalError("Must be Statement or DatabaseMetaData -- Bad Creator: " + this.creator);
/* 2998 */         detach();
/* 2999 */         this.inner.close();
/* 3000 */         this.inner = null;
/*      */       }
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3005 */       if (isDetached())
/*      */       {
/* 3007 */         if (logger.isLoggable(MLevel.FINE))
/*      */         {
/* 3009 */           logger.log(MLevel.FINE, this + ": close() called more than once.");
/*      */         }
/*      */       } else {
/* 3012 */         throw exc;
/*      */       }
/*      */     }
/*      */     catch (Exception exc) {
/* 3016 */       if (!isDetached())
/*      */       {
/* 3018 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3020 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Ref getRef(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3028 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3030 */       return this.inner.getRef(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3034 */       if (isDetached())
/*      */       {
/* 3036 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3038 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3042 */       if (!isDetached())
/*      */       {
/* 3044 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3046 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Ref getRef(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3054 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3056 */       return this.inner.getRef(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3060 */       if (isDetached())
/*      */       {
/* 3062 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3064 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3068 */       if (!isDetached())
/*      */       {
/* 3070 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3072 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean first() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3080 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3082 */       return this.inner.first();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3086 */       if (isDetached())
/*      */       {
/* 3088 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3090 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3094 */       if (!isDetached())
/*      */       {
/* 3096 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3098 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Time getTime(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3106 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3108 */       return this.inner.getTime(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3112 */       if (isDetached())
/*      */       {
/* 3114 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3116 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3120 */       if (!isDetached())
/*      */       {
/* 3122 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3124 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Time getTime(String a, Calendar b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3132 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3134 */       return this.inner.getTime(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3138 */       if (isDetached())
/*      */       {
/* 3140 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3142 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3146 */       if (!isDetached())
/*      */       {
/* 3148 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3150 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Time getTime(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3158 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3160 */       return this.inner.getTime(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3164 */       if (isDetached())
/*      */       {
/* 3166 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3168 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3172 */       if (!isDetached())
/*      */       {
/* 3174 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3176 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Time getTime(int a, Calendar b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3184 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3186 */       return this.inner.getTime(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3190 */       if (isDetached())
/*      */       {
/* 3192 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3194 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3198 */       if (!isDetached())
/*      */       {
/* 3200 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3202 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Date getDate(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3210 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3212 */       return this.inner.getDate(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3216 */       if (isDetached())
/*      */       {
/* 3218 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3220 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3224 */       if (!isDetached())
/*      */       {
/* 3226 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3228 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Date getDate(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3236 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3238 */       return this.inner.getDate(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3242 */       if (isDetached())
/*      */       {
/* 3244 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3246 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3250 */       if (!isDetached())
/*      */       {
/* 3252 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3254 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Date getDate(int a, Calendar b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3262 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3264 */       return this.inner.getDate(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3268 */       if (isDetached())
/*      */       {
/* 3270 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3272 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3276 */       if (!isDetached())
/*      */       {
/* 3278 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3280 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Date getDate(String a, Calendar b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3288 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3290 */       return this.inner.getDate(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3294 */       if (isDetached())
/*      */       {
/* 3296 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3298 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3302 */       if (!isDetached())
/*      */       {
/* 3304 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3306 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final String getString(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3314 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3316 */       return this.inner.getString(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3320 */       if (isDetached())
/*      */       {
/* 3322 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3324 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3328 */       if (!isDetached())
/*      */       {
/* 3330 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3332 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final String getString(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3340 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3342 */       return this.inner.getString(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3346 */       if (isDetached())
/*      */       {
/* 3348 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3350 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3354 */       if (!isDetached())
/*      */       {
/* 3356 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3358 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean last() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3366 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3368 */       return this.inner.last();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3372 */       if (isDetached())
/*      */       {
/* 3374 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3376 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3380 */       if (!isDetached())
/*      */       {
/* 3382 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3384 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Timestamp getTimestamp(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3392 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3394 */       return this.inner.getTimestamp(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3398 */       if (isDetached())
/*      */       {
/* 3400 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3402 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3406 */       if (!isDetached())
/*      */       {
/* 3408 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3410 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Timestamp getTimestamp(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3418 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3420 */       return this.inner.getTimestamp(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3424 */       if (isDetached())
/*      */       {
/* 3426 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3428 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3432 */       if (!isDetached())
/*      */       {
/* 3434 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3436 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Timestamp getTimestamp(int a, Calendar b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3444 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3446 */       return this.inner.getTimestamp(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3450 */       if (isDetached())
/*      */       {
/* 3452 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3454 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3458 */       if (!isDetached())
/*      */       {
/* 3460 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3462 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Timestamp getTimestamp(String a, Calendar b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3470 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3472 */       return this.inner.getTimestamp(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3476 */       if (isDetached())
/*      */       {
/* 3478 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3480 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3484 */       if (!isDetached())
/*      */       {
/* 3486 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3488 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean isFirst() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3496 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3498 */       return this.inner.isFirst();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3502 */       if (isDetached())
/*      */       {
/* 3504 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3506 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3510 */       if (!isDetached())
/*      */       {
/* 3512 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3514 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final BigDecimal getBigDecimal(int a, int b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3522 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3524 */       return this.inner.getBigDecimal(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3528 */       if (isDetached())
/*      */       {
/* 3530 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3532 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3536 */       if (!isDetached())
/*      */       {
/* 3538 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3540 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final BigDecimal getBigDecimal(String a, int b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3548 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3550 */       return this.inner.getBigDecimal(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3554 */       if (isDetached())
/*      */       {
/* 3556 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3558 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3562 */       if (!isDetached())
/*      */       {
/* 3564 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3566 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final BigDecimal getBigDecimal(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3574 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3576 */       return this.inner.getBigDecimal(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3580 */       if (isDetached())
/*      */       {
/* 3582 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3584 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3588 */       if (!isDetached())
/*      */       {
/* 3590 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3592 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final BigDecimal getBigDecimal(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3600 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3602 */       return this.inner.getBigDecimal(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3606 */       if (isDetached())
/*      */       {
/* 3608 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3610 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3614 */       if (!isDetached())
/*      */       {
/* 3616 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3618 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateTime(int a, Time b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3626 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3628 */       this.inner.updateTime(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3632 */       if (isDetached())
/*      */       {
/* 3634 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3636 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3640 */       if (!isDetached())
/*      */       {
/* 3642 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3644 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void updateTime(String a, Time b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3652 */       if (this.proxyConn != null) { this.proxyConn.maybeDirtyTransaction();
/*      */       }
/* 3654 */       this.inner.updateTime(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3658 */       if (isDetached())
/*      */       {
/* 3660 */         throw SqlUtils.toSQLException("You can't operate on a closed ResultSet!!!", exc);
/*      */       }
/* 3662 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3666 */       if (!isDetached())
/*      */       {
/* 3668 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3670 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/* 3674 */   private static final MLogger logger = MLog.getLogger("com.mchange.v2.c3p0.impl.NewProxyResultSet");
/*      */   
/*      */   volatile NewPooledConnection parentPooledConnection;
/*      */   
/* 3678 */   ConnectionEventListener cel = new ConnectionEventListener()
/*      */   {
/*      */     public void connectionErrorOccurred(ConnectionEvent evt) {}
/*      */     
/*      */ 
/*      */ 
/* 3684 */     public void connectionClosed(ConnectionEvent evt) { NewProxyResultSet.this.detach(); } };
/*      */   Object creator;
/*      */   Object creatorProxy;
/*      */   NewProxyConnection proxyConn;
/*      */   
/* 3689 */   void attach(NewPooledConnection parentPooledConnection) { this.parentPooledConnection = parentPooledConnection;
/* 3690 */     parentPooledConnection.addConnectionEventListener(this.cel);
/*      */   }
/*      */   
/*      */   private void detach()
/*      */   {
/* 3695 */     this.parentPooledConnection.removeConnectionEventListener(this.cel);
/* 3696 */     this.parentPooledConnection = null;
/*      */   }
/*      */   
/*      */   NewProxyResultSet(ResultSet inner, NewPooledConnection parentPooledConnection)
/*      */   {
/* 3701 */     this(inner);
/* 3702 */     attach(parentPooledConnection);
/*      */   }
/*      */   
/*      */   boolean isDetached() {
/* 3706 */     return this.parentPooledConnection == null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   NewProxyResultSet(ResultSet inner, NewPooledConnection parentPooledConnection, Object c, Object cProxy)
/*      */   {
/* 3714 */     this(inner, parentPooledConnection);
/* 3715 */     this.creator = c;
/* 3716 */     this.creatorProxy = cProxy;
/* 3717 */     if ((this.creatorProxy instanceof NewProxyConnection)) this.proxyConn = ((NewProxyConnection)cProxy);
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\NewProxyResultSet.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */