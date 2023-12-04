/*      */ package com.mchange.v2.c3p0.impl;
/*      */ 
/*      */ import com.mchange.v2.c3p0.C3P0ProxyStatement;
/*      */ import com.mchange.v2.log.MLevel;
/*      */ import com.mchange.v2.log.MLog;
/*      */ import com.mchange.v2.log.MLogger;
/*      */ import com.mchange.v2.sql.SqlUtils;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Connection;
/*      */ import java.sql.Date;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import javax.sql.ConnectionEvent;
/*      */ import javax.sql.ConnectionEventListener;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class NewProxyPreparedStatement
/*      */   implements PreparedStatement, C3P0ProxyStatement
/*      */ {
/*      */   protected PreparedStatement inner;
/*      */   
/*      */   public NewProxyPreparedStatement(PreparedStatement inner)
/*      */   {
/*   42 */     this.inner = inner;
/*      */   }
/*      */   
/*      */   public final ResultSetMetaData getMetaData() throws SQLException
/*      */   {
/*      */     try {
/*   48 */       maybeDirtyTransaction();
/*      */       
/*   50 */       return this.inner.getMetaData();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*   54 */       if (isDetached())
/*      */       {
/*   56 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*   58 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*   62 */       if (!isDetached())
/*      */       {
/*   64 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*   66 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet executeQuery() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*   74 */       maybeDirtyTransaction();
/*      */       
/*   76 */       ResultSet innerResultSet = this.inner.executeQuery();
/*   77 */       if (innerResultSet == null) return null;
/*   78 */       this.parentPooledConnection.markActiveResultSetForStatement(this.inner, innerResultSet);
/*   79 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*   83 */       if (isDetached())
/*      */       {
/*   85 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*   87 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*   91 */       if (!isDetached())
/*      */       {
/*   93 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*   95 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int executeUpdate() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  103 */       maybeDirtyTransaction();
/*      */       
/*  105 */       return this.inner.executeUpdate();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  109 */       if (isDetached())
/*      */       {
/*  111 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  113 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  117 */       if (!isDetached())
/*      */       {
/*  119 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  121 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void addBatch() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  129 */       maybeDirtyTransaction();
/*      */       
/*  131 */       this.inner.addBatch();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  135 */       if (isDetached())
/*      */       {
/*  137 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  139 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  143 */       if (!isDetached())
/*      */       {
/*  145 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  147 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setNull(int a, int b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  155 */       maybeDirtyTransaction();
/*      */       
/*  157 */       this.inner.setNull(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  161 */       if (isDetached())
/*      */       {
/*  163 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  165 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  169 */       if (!isDetached())
/*      */       {
/*  171 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  173 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setNull(int a, int b, String c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  181 */       maybeDirtyTransaction();
/*      */       
/*  183 */       this.inner.setNull(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  187 */       if (isDetached())
/*      */       {
/*  189 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  191 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  195 */       if (!isDetached())
/*      */       {
/*  197 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  199 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setBigDecimal(int a, BigDecimal b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  207 */       maybeDirtyTransaction();
/*      */       
/*  209 */       this.inner.setBigDecimal(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  213 */       if (isDetached())
/*      */       {
/*  215 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  217 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  221 */       if (!isDetached())
/*      */       {
/*  223 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  225 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setBytes(int a, byte[] b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  233 */       maybeDirtyTransaction();
/*      */       
/*  235 */       this.inner.setBytes(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  239 */       if (isDetached())
/*      */       {
/*  241 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  243 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  247 */       if (!isDetached())
/*      */       {
/*  249 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  251 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setAsciiStream(int a, InputStream b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  259 */       maybeDirtyTransaction();
/*      */       
/*  261 */       this.inner.setAsciiStream(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  265 */       if (isDetached())
/*      */       {
/*  267 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  269 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  273 */       if (!isDetached())
/*      */       {
/*  275 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  277 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setUnicodeStream(int a, InputStream b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  285 */       maybeDirtyTransaction();
/*      */       
/*  287 */       this.inner.setUnicodeStream(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  291 */       if (isDetached())
/*      */       {
/*  293 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  295 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  299 */       if (!isDetached())
/*      */       {
/*  301 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  303 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setBinaryStream(int a, InputStream b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  311 */       maybeDirtyTransaction();
/*      */       
/*  313 */       this.inner.setBinaryStream(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  317 */       if (isDetached())
/*      */       {
/*  319 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  321 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  325 */       if (!isDetached())
/*      */       {
/*  327 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  329 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void clearParameters() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  337 */       maybeDirtyTransaction();
/*      */       
/*  339 */       this.inner.clearParameters();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  343 */       if (isDetached())
/*      */       {
/*  345 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  347 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  351 */       if (!isDetached())
/*      */       {
/*  353 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  355 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setObject(int a, Object b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  363 */       maybeDirtyTransaction();
/*      */       
/*  365 */       this.inner.setObject(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  369 */       if (isDetached())
/*      */       {
/*  371 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  373 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  377 */       if (!isDetached())
/*      */       {
/*  379 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  381 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setObject(int a, Object b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  389 */       maybeDirtyTransaction();
/*      */       
/*  391 */       this.inner.setObject(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  395 */       if (isDetached())
/*      */       {
/*  397 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  399 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  403 */       if (!isDetached())
/*      */       {
/*  405 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  407 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setObject(int a, Object b, int c, int d) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  415 */       maybeDirtyTransaction();
/*      */       
/*  417 */       this.inner.setObject(a, b, c, d);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  421 */       if (isDetached())
/*      */       {
/*  423 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  425 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  429 */       if (!isDetached())
/*      */       {
/*  431 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  433 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setCharacterStream(int a, Reader b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  441 */       maybeDirtyTransaction();
/*      */       
/*  443 */       this.inner.setCharacterStream(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  447 */       if (isDetached())
/*      */       {
/*  449 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  451 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  455 */       if (!isDetached())
/*      */       {
/*  457 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  459 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setRef(int a, Ref b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  467 */       maybeDirtyTransaction();
/*      */       
/*  469 */       this.inner.setRef(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  473 */       if (isDetached())
/*      */       {
/*  475 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  477 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  481 */       if (!isDetached())
/*      */       {
/*  483 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  485 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setBlob(int a, Blob b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  493 */       maybeDirtyTransaction();
/*      */       
/*  495 */       this.inner.setBlob(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  499 */       if (isDetached())
/*      */       {
/*  501 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  503 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  507 */       if (!isDetached())
/*      */       {
/*  509 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  511 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setClob(int a, Clob b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  519 */       maybeDirtyTransaction();
/*      */       
/*  521 */       this.inner.setClob(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  525 */       if (isDetached())
/*      */       {
/*  527 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  529 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  533 */       if (!isDetached())
/*      */       {
/*  535 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  537 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setArray(int a, Array b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  545 */       maybeDirtyTransaction();
/*      */       
/*  547 */       this.inner.setArray(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  551 */       if (isDetached())
/*      */       {
/*  553 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  555 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  559 */       if (!isDetached())
/*      */       {
/*  561 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  563 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ParameterMetaData getParameterMetaData() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  571 */       maybeDirtyTransaction();
/*      */       
/*  573 */       return this.inner.getParameterMetaData();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  577 */       if (isDetached())
/*      */       {
/*  579 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  581 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  585 */       if (!isDetached())
/*      */       {
/*  587 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  589 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setBoolean(int a, boolean b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  597 */       maybeDirtyTransaction();
/*      */       
/*  599 */       this.inner.setBoolean(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  603 */       if (isDetached())
/*      */       {
/*  605 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  607 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  611 */       if (!isDetached())
/*      */       {
/*  613 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  615 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setByte(int a, byte b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  623 */       maybeDirtyTransaction();
/*      */       
/*  625 */       this.inner.setByte(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  629 */       if (isDetached())
/*      */       {
/*  631 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  633 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  637 */       if (!isDetached())
/*      */       {
/*  639 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  641 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setShort(int a, short b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  649 */       maybeDirtyTransaction();
/*      */       
/*  651 */       this.inner.setShort(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  655 */       if (isDetached())
/*      */       {
/*  657 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  659 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  663 */       if (!isDetached())
/*      */       {
/*  665 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  667 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setInt(int a, int b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  675 */       maybeDirtyTransaction();
/*      */       
/*  677 */       this.inner.setInt(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  681 */       if (isDetached())
/*      */       {
/*  683 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setLong(int a, long b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  701 */       maybeDirtyTransaction();
/*      */       
/*  703 */       this.inner.setLong(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  707 */       if (isDetached())
/*      */       {
/*  709 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  711 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  715 */       if (!isDetached())
/*      */       {
/*  717 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  719 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setFloat(int a, float b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  727 */       maybeDirtyTransaction();
/*      */       
/*  729 */       this.inner.setFloat(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  733 */       if (isDetached())
/*      */       {
/*  735 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  737 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  741 */       if (!isDetached())
/*      */       {
/*  743 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  745 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setDouble(int a, double b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  753 */       maybeDirtyTransaction();
/*      */       
/*  755 */       this.inner.setDouble(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  759 */       if (isDetached())
/*      */       {
/*  761 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  763 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  767 */       if (!isDetached())
/*      */       {
/*  769 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  771 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setTimestamp(int a, Timestamp b, Calendar c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  779 */       maybeDirtyTransaction();
/*      */       
/*  781 */       this.inner.setTimestamp(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  785 */       if (isDetached())
/*      */       {
/*  787 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  789 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  793 */       if (!isDetached())
/*      */       {
/*  795 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  797 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setTimestamp(int a, Timestamp b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  805 */       maybeDirtyTransaction();
/*      */       
/*  807 */       this.inner.setTimestamp(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  811 */       if (isDetached())
/*      */       {
/*  813 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  815 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  819 */       if (!isDetached())
/*      */       {
/*  821 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  823 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setURL(int a, URL b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  831 */       maybeDirtyTransaction();
/*      */       
/*  833 */       this.inner.setURL(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  837 */       if (isDetached())
/*      */       {
/*  839 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  841 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  845 */       if (!isDetached())
/*      */       {
/*  847 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  849 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setTime(int a, Time b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  857 */       maybeDirtyTransaction();
/*      */       
/*  859 */       this.inner.setTime(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  863 */       if (isDetached())
/*      */       {
/*  865 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  867 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  871 */       if (!isDetached())
/*      */       {
/*  873 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  875 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setTime(int a, Time b, Calendar c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  883 */       maybeDirtyTransaction();
/*      */       
/*  885 */       this.inner.setTime(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  889 */       if (isDetached())
/*      */       {
/*  891 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  893 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  897 */       if (!isDetached())
/*      */       {
/*  899 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  901 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setDate(int a, Date b, Calendar c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  909 */       maybeDirtyTransaction();
/*      */       
/*  911 */       this.inner.setDate(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  915 */       if (isDetached())
/*      */       {
/*  917 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  919 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  923 */       if (!isDetached())
/*      */       {
/*  925 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  927 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setDate(int a, Date b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  935 */       maybeDirtyTransaction();
/*      */       
/*  937 */       this.inner.setDate(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  941 */       if (isDetached())
/*      */       {
/*  943 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  945 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  949 */       if (!isDetached())
/*      */       {
/*  951 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  953 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setString(int a, String b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  961 */       maybeDirtyTransaction();
/*      */       
/*  963 */       this.inner.setString(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  967 */       if (isDetached())
/*      */       {
/*  969 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  971 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  975 */       if (!isDetached())
/*      */       {
/*  977 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  979 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean execute() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  987 */       maybeDirtyTransaction();
/*      */       
/*  989 */       return this.inner.execute();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  993 */       if (isDetached())
/*      */       {
/*  995 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*  997 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1001 */       if (!isDetached())
/*      */       {
/* 1003 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1005 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet executeQuery(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1013 */       maybeDirtyTransaction();
/*      */       
/* 1015 */       ResultSet innerResultSet = this.inner.executeQuery(a);
/* 1016 */       if (innerResultSet == null) return null;
/* 1017 */       this.parentPooledConnection.markActiveResultSetForStatement(this.inner, innerResultSet);
/* 1018 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1022 */       if (isDetached())
/*      */       {
/* 1024 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1026 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1030 */       if (!isDetached())
/*      */       {
/* 1032 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1034 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int executeUpdate(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1042 */       maybeDirtyTransaction();
/*      */       
/* 1044 */       return this.inner.executeUpdate(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1048 */       if (isDetached())
/*      */       {
/* 1050 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1052 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1056 */       if (!isDetached())
/*      */       {
/* 1058 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1060 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int executeUpdate(String a, String[] b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1068 */       maybeDirtyTransaction();
/*      */       
/* 1070 */       return this.inner.executeUpdate(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1074 */       if (isDetached())
/*      */       {
/* 1076 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1078 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1082 */       if (!isDetached())
/*      */       {
/* 1084 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1086 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int executeUpdate(String a, int b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1094 */       maybeDirtyTransaction();
/*      */       
/* 1096 */       return this.inner.executeUpdate(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1100 */       if (isDetached())
/*      */       {
/* 1102 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1104 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1108 */       if (!isDetached())
/*      */       {
/* 1110 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1112 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int executeUpdate(String a, int[] b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1120 */       maybeDirtyTransaction();
/*      */       
/* 1122 */       return this.inner.executeUpdate(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1126 */       if (isDetached())
/*      */       {
/* 1128 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1130 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1134 */       if (!isDetached())
/*      */       {
/* 1136 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1138 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxFieldSize() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1146 */       maybeDirtyTransaction();
/*      */       
/* 1148 */       return this.inner.getMaxFieldSize();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1152 */       if (isDetached())
/*      */       {
/* 1154 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1156 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1160 */       if (!isDetached())
/*      */       {
/* 1162 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1164 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setMaxFieldSize(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1172 */       maybeDirtyTransaction();
/*      */       
/* 1174 */       this.inner.setMaxFieldSize(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1178 */       if (isDetached())
/*      */       {
/* 1180 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1182 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1186 */       if (!isDetached())
/*      */       {
/* 1188 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1190 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxRows() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1198 */       maybeDirtyTransaction();
/*      */       
/* 1200 */       return this.inner.getMaxRows();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1204 */       if (isDetached())
/*      */       {
/* 1206 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1208 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1212 */       if (!isDetached())
/*      */       {
/* 1214 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1216 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setMaxRows(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1224 */       maybeDirtyTransaction();
/*      */       
/* 1226 */       this.inner.setMaxRows(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1230 */       if (isDetached())
/*      */       {
/* 1232 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1234 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1238 */       if (!isDetached())
/*      */       {
/* 1240 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1242 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setEscapeProcessing(boolean a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1250 */       maybeDirtyTransaction();
/*      */       
/* 1252 */       this.inner.setEscapeProcessing(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1256 */       if (isDetached())
/*      */       {
/* 1258 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1260 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1264 */       if (!isDetached())
/*      */       {
/* 1266 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1268 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getQueryTimeout() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1276 */       maybeDirtyTransaction();
/*      */       
/* 1278 */       return this.inner.getQueryTimeout();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1282 */       if (isDetached())
/*      */       {
/* 1284 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1286 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1290 */       if (!isDetached())
/*      */       {
/* 1292 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1294 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setQueryTimeout(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1302 */       maybeDirtyTransaction();
/*      */       
/* 1304 */       this.inner.setQueryTimeout(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1308 */       if (isDetached())
/*      */       {
/* 1310 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1312 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1316 */       if (!isDetached())
/*      */       {
/* 1318 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1320 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final SQLWarning getWarnings() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1328 */       maybeDirtyTransaction();
/*      */       
/* 1330 */       return this.inner.getWarnings();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1334 */       if (isDetached())
/*      */       {
/* 1336 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1338 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1342 */       if (!isDetached())
/*      */       {
/* 1344 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1346 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void clearWarnings() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1354 */       maybeDirtyTransaction();
/*      */       
/* 1356 */       this.inner.clearWarnings();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1360 */       if (isDetached())
/*      */       {
/* 1362 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1364 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1368 */       if (!isDetached())
/*      */       {
/* 1370 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1372 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setCursorName(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1380 */       maybeDirtyTransaction();
/*      */       
/* 1382 */       this.inner.setCursorName(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1386 */       if (isDetached())
/*      */       {
/* 1388 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1390 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1394 */       if (!isDetached())
/*      */       {
/* 1396 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1398 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getResultSet() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1406 */       maybeDirtyTransaction();
/*      */       
/* 1408 */       ResultSet innerResultSet = this.inner.getResultSet();
/* 1409 */       if (innerResultSet == null) return null;
/* 1410 */       this.parentPooledConnection.markActiveResultSetForStatement(this.inner, innerResultSet);
/* 1411 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1415 */       if (isDetached())
/*      */       {
/* 1417 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1419 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1423 */       if (!isDetached())
/*      */       {
/* 1425 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1427 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getUpdateCount() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1435 */       maybeDirtyTransaction();
/*      */       
/* 1437 */       return this.inner.getUpdateCount();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1441 */       if (isDetached())
/*      */       {
/* 1443 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1445 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1449 */       if (!isDetached())
/*      */       {
/* 1451 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1453 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean getMoreResults() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1461 */       maybeDirtyTransaction();
/*      */       
/* 1463 */       return this.inner.getMoreResults();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1467 */       if (isDetached())
/*      */       {
/* 1469 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1471 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1475 */       if (!isDetached())
/*      */       {
/* 1477 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1479 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean getMoreResults(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1487 */       maybeDirtyTransaction();
/*      */       
/* 1489 */       return this.inner.getMoreResults(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1493 */       if (isDetached())
/*      */       {
/* 1495 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1497 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1501 */       if (!isDetached())
/*      */       {
/* 1503 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1505 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setFetchDirection(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1513 */       maybeDirtyTransaction();
/*      */       
/* 1515 */       this.inner.setFetchDirection(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1519 */       if (isDetached())
/*      */       {
/* 1521 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1523 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1527 */       if (!isDetached())
/*      */       {
/* 1529 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1531 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getFetchDirection() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1539 */       maybeDirtyTransaction();
/*      */       
/* 1541 */       return this.inner.getFetchDirection();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1545 */       if (isDetached())
/*      */       {
/* 1547 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1549 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1553 */       if (!isDetached())
/*      */       {
/* 1555 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1557 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setFetchSize(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1565 */       maybeDirtyTransaction();
/*      */       
/* 1567 */       this.inner.setFetchSize(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1571 */       if (isDetached())
/*      */       {
/* 1573 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1575 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1579 */       if (!isDetached())
/*      */       {
/* 1581 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1583 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getFetchSize() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1591 */       maybeDirtyTransaction();
/*      */       
/* 1593 */       return this.inner.getFetchSize();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1597 */       if (isDetached())
/*      */       {
/* 1599 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1601 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1605 */       if (!isDetached())
/*      */       {
/* 1607 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1609 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getResultSetConcurrency() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1617 */       maybeDirtyTransaction();
/*      */       
/* 1619 */       return this.inner.getResultSetConcurrency();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1623 */       if (isDetached())
/*      */       {
/* 1625 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1627 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1631 */       if (!isDetached())
/*      */       {
/* 1633 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1635 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getResultSetType() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1643 */       maybeDirtyTransaction();
/*      */       
/* 1645 */       return this.inner.getResultSetType();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1649 */       if (isDetached())
/*      */       {
/* 1651 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1653 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1657 */       if (!isDetached())
/*      */       {
/* 1659 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1661 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void addBatch(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1669 */       maybeDirtyTransaction();
/*      */       
/* 1671 */       this.inner.addBatch(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1675 */       if (isDetached())
/*      */       {
/* 1677 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1679 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1683 */       if (!isDetached())
/*      */       {
/* 1685 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1687 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void clearBatch() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1695 */       maybeDirtyTransaction();
/*      */       
/* 1697 */       this.inner.clearBatch();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1701 */       if (isDetached())
/*      */       {
/* 1703 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1705 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1709 */       if (!isDetached())
/*      */       {
/* 1711 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1713 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int[] executeBatch() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1721 */       maybeDirtyTransaction();
/*      */       
/* 1723 */       return this.inner.executeBatch();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1727 */       if (isDetached())
/*      */       {
/* 1729 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1731 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1735 */       if (!isDetached())
/*      */       {
/* 1737 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1739 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getGeneratedKeys() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1747 */       maybeDirtyTransaction();
/*      */       
/* 1749 */       ResultSet innerResultSet = this.inner.getGeneratedKeys();
/* 1750 */       if (innerResultSet == null) return null;
/* 1751 */       this.parentPooledConnection.markActiveResultSetForStatement(this.inner, innerResultSet);
/* 1752 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1756 */       if (isDetached())
/*      */       {
/* 1758 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1760 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1764 */       if (!isDetached())
/*      */       {
/* 1766 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1768 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getResultSetHoldability() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1776 */       maybeDirtyTransaction();
/*      */       
/* 1778 */       return this.inner.getResultSetHoldability();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1782 */       if (isDetached())
/*      */       {
/* 1784 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1786 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1790 */       if (!isDetached())
/*      */       {
/* 1792 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1794 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void close() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1802 */       maybeDirtyTransaction();
/*      */       
/* 1804 */       if (!isDetached())
/*      */       {
/* 1806 */         if (this.is_cached) {
/* 1807 */           this.parentPooledConnection.checkinStatement(this.inner);
/*      */         }
/*      */         else {
/* 1810 */           this.parentPooledConnection.markInactiveUncachedStatement(this.inner);
/* 1811 */           try { this.inner.close();
/*      */           }
/*      */           catch (Exception e) {
/* 1814 */             if (logger.isLoggable(MLevel.WARNING))
/* 1815 */               logger.log(MLevel.WARNING, "Exception on close of inner statement.", e);
/* 1816 */             SQLException sqle = SqlUtils.toSQLException(e);
/* 1817 */             throw sqle;
/*      */           }
/*      */         }
/*      */         
/* 1821 */         detach();
/* 1822 */         this.inner = null;
/* 1823 */         this.creatorProxy = null;
/*      */       }
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1828 */       if (isDetached())
/*      */       {
/* 1830 */         if (logger.isLoggable(MLevel.FINE))
/*      */         {
/* 1832 */           logger.log(MLevel.FINE, this + ": close() called more than once.");
/*      */         }
/*      */       } else {
/* 1835 */         throw exc;
/*      */       }
/*      */     }
/*      */     catch (Exception exc) {
/* 1839 */       if (!isDetached())
/*      */       {
/* 1841 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1843 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void cancel() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1851 */       maybeDirtyTransaction();
/*      */       
/* 1853 */       this.inner.cancel();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1857 */       if (isDetached())
/*      */       {
/* 1859 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1861 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1865 */       if (!isDetached())
/*      */       {
/* 1867 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1869 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Connection getConnection() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1877 */       maybeDirtyTransaction();
/*      */       
/* 1879 */       if (!isDetached()) {
/* 1880 */         return this.creatorProxy;
/*      */       }
/* 1882 */       throw new SQLException("You cannot operate on a closed Statement!");
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1886 */       if (isDetached())
/*      */       {
/* 1888 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1890 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1894 */       if (!isDetached())
/*      */       {
/* 1896 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1898 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean execute(String a, int b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1906 */       maybeDirtyTransaction();
/*      */       
/* 1908 */       return this.inner.execute(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1912 */       if (isDetached())
/*      */       {
/* 1914 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1916 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1920 */       if (!isDetached())
/*      */       {
/* 1922 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1924 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean execute(String a, int[] b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1932 */       maybeDirtyTransaction();
/*      */       
/* 1934 */       return this.inner.execute(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1938 */       if (isDetached())
/*      */       {
/* 1940 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1942 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1946 */       if (!isDetached())
/*      */       {
/* 1948 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1950 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean execute(String a, String[] b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1958 */       maybeDirtyTransaction();
/*      */       
/* 1960 */       return this.inner.execute(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1964 */       if (isDetached())
/*      */       {
/* 1966 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1968 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1972 */       if (!isDetached())
/*      */       {
/* 1974 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1976 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean execute(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1984 */       maybeDirtyTransaction();
/*      */       
/* 1986 */       return this.inner.execute(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1990 */       if (isDetached())
/*      */       {
/* 1992 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 1994 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1998 */       if (!isDetached())
/*      */       {
/* 2000 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2002 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/* 2006 */   private static final MLogger logger = MLog.getLogger("com.mchange.v2.c3p0.impl.NewProxyPreparedStatement");
/*      */   
/*      */   volatile NewPooledConnection parentPooledConnection;
/*      */   
/* 2010 */   ConnectionEventListener cel = new ConnectionEventListener()
/*      */   {
/*      */     public void connectionErrorOccurred(ConnectionEvent evt) {}
/*      */     
/*      */ 
/*      */ 
/* 2016 */     public void connectionClosed(ConnectionEvent evt) { NewProxyPreparedStatement.this.detach(); }
/*      */   };
/*      */   boolean is_cached;
/*      */   NewProxyConnection creatorProxy;
/*      */   
/* 2021 */   void attach(NewPooledConnection parentPooledConnection) { this.parentPooledConnection = parentPooledConnection;
/* 2022 */     parentPooledConnection.addConnectionEventListener(this.cel);
/*      */   }
/*      */   
/*      */   private void detach()
/*      */   {
/* 2027 */     this.parentPooledConnection.removeConnectionEventListener(this.cel);
/* 2028 */     this.parentPooledConnection = null;
/*      */   }
/*      */   
/*      */   NewProxyPreparedStatement(PreparedStatement inner, NewPooledConnection parentPooledConnection)
/*      */   {
/* 2033 */     this(inner);
/* 2034 */     attach(parentPooledConnection);
/*      */   }
/*      */   
/*      */   boolean isDetached() {
/* 2038 */     return this.parentPooledConnection == null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   NewProxyPreparedStatement(PreparedStatement inner, NewPooledConnection parentPooledConnection, boolean cached, NewProxyConnection cProxy)
/*      */   {
/* 2045 */     this(inner, parentPooledConnection);
/* 2046 */     this.is_cached = cached;
/* 2047 */     this.creatorProxy = cProxy;
/*      */   }
/*      */   
/*      */   public Object rawStatementOperation(Method m, Object target, Object[] args) throws IllegalAccessException, InvocationTargetException, SQLException
/*      */   {
/* 2052 */     maybeDirtyTransaction();
/*      */     
/* 2054 */     if (target == C3P0ProxyStatement.RAW_STATEMENT) target = this.inner;
/* 2055 */     int i = 0; for (int len = args.length; i < len; i++)
/* 2056 */       if (args[i] == C3P0ProxyStatement.RAW_STATEMENT) args[i] = this.inner;
/* 2057 */     Object out = m.invoke(target, args);
/* 2058 */     if ((out instanceof ResultSet))
/*      */     {
/* 2060 */       ResultSet innerResultSet = (ResultSet)out;
/* 2061 */       this.parentPooledConnection.markActiveResultSetForStatement(this.inner, innerResultSet);
/* 2062 */       out = new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     
/* 2065 */     return out;
/*      */   }
/*      */   
/*      */   void maybeDirtyTransaction() {
/* 2069 */     this.creatorProxy.maybeDirtyTransaction();
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\NewProxyPreparedStatement.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */