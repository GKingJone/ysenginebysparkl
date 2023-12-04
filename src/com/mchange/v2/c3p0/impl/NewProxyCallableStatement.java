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
/*      */ import java.sql.CallableStatement;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Connection;
/*      */ import java.sql.Date;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import java.util.Map;
/*      */ import javax.sql.ConnectionEvent;
/*      */ import javax.sql.ConnectionEventListener;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class NewProxyCallableStatement
/*      */   implements CallableStatement, C3P0ProxyStatement
/*      */ {
/*      */   protected CallableStatement inner;
/*      */   
/*      */   public NewProxyCallableStatement(CallableStatement inner)
/*      */   {
/*   43 */     this.inner = inner;
/*      */   }
/*      */   
/*      */   public final void setNull(String a, int b) throws SQLException
/*      */   {
/*      */     try {
/*   49 */       maybeDirtyTransaction();
/*      */       
/*   51 */       this.inner.setNull(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*   55 */       if (isDetached())
/*      */       {
/*   57 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/*   59 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*   63 */       if (!isDetached())
/*      */       {
/*   65 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*   67 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setNull(String a, int b, String c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*   75 */       maybeDirtyTransaction();
/*      */       
/*   77 */       this.inner.setNull(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*   81 */       if (isDetached())
/*      */       {
/*   83 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setBigDecimal(String a, BigDecimal b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  101 */       maybeDirtyTransaction();
/*      */       
/*  103 */       this.inner.setBigDecimal(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  107 */       if (isDetached())
/*      */       {
/*  109 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setBytes(String a, byte[] b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  127 */       maybeDirtyTransaction();
/*      */       
/*  129 */       this.inner.setBytes(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  133 */       if (isDetached())
/*      */       {
/*  135 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setAsciiStream(String a, InputStream b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  153 */       maybeDirtyTransaction();
/*      */       
/*  155 */       this.inner.setAsciiStream(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  159 */       if (isDetached())
/*      */       {
/*  161 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setBinaryStream(String a, InputStream b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  179 */       maybeDirtyTransaction();
/*      */       
/*  181 */       this.inner.setBinaryStream(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  185 */       if (isDetached())
/*      */       {
/*  187 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setObject(String a, Object b, int c, int d) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  205 */       maybeDirtyTransaction();
/*      */       
/*  207 */       this.inner.setObject(a, b, c, d);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  211 */       if (isDetached())
/*      */       {
/*  213 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setObject(String a, Object b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  231 */       maybeDirtyTransaction();
/*      */       
/*  233 */       this.inner.setObject(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  237 */       if (isDetached())
/*      */       {
/*  239 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setObject(String a, Object b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  257 */       maybeDirtyTransaction();
/*      */       
/*  259 */       this.inner.setObject(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  263 */       if (isDetached())
/*      */       {
/*  265 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setCharacterStream(String a, Reader b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  283 */       maybeDirtyTransaction();
/*      */       
/*  285 */       this.inner.setCharacterStream(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  289 */       if (isDetached())
/*      */       {
/*  291 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void registerOutParameter(int a, int b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  309 */       maybeDirtyTransaction();
/*      */       
/*  311 */       this.inner.registerOutParameter(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  315 */       if (isDetached())
/*      */       {
/*  317 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void registerOutParameter(String a, int b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  335 */       maybeDirtyTransaction();
/*      */       
/*  337 */       this.inner.registerOutParameter(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  341 */       if (isDetached())
/*      */       {
/*  343 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void registerOutParameter(String a, int b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  361 */       maybeDirtyTransaction();
/*      */       
/*  363 */       this.inner.registerOutParameter(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  367 */       if (isDetached())
/*      */       {
/*  369 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void registerOutParameter(int a, int b, String c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  387 */       maybeDirtyTransaction();
/*      */       
/*  389 */       this.inner.registerOutParameter(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  393 */       if (isDetached())
/*      */       {
/*  395 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void registerOutParameter(int a, int b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  413 */       maybeDirtyTransaction();
/*      */       
/*  415 */       this.inner.registerOutParameter(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  419 */       if (isDetached())
/*      */       {
/*  421 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void registerOutParameter(String a, int b, String c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  439 */       maybeDirtyTransaction();
/*      */       
/*  441 */       this.inner.registerOutParameter(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  445 */       if (isDetached())
/*      */       {
/*  447 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final boolean wasNull() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  465 */       maybeDirtyTransaction();
/*      */       
/*  467 */       return this.inner.wasNull();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  471 */       if (isDetached())
/*      */       {
/*  473 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Blob getBlob(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  491 */       maybeDirtyTransaction();
/*      */       
/*  493 */       return this.inner.getBlob(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  497 */       if (isDetached())
/*      */       {
/*  499 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Blob getBlob(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  517 */       maybeDirtyTransaction();
/*      */       
/*  519 */       return this.inner.getBlob(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  523 */       if (isDetached())
/*      */       {
/*  525 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Clob getClob(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  543 */       maybeDirtyTransaction();
/*      */       
/*  545 */       return this.inner.getClob(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  549 */       if (isDetached())
/*      */       {
/*  551 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Clob getClob(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  569 */       maybeDirtyTransaction();
/*      */       
/*  571 */       return this.inner.getClob(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  575 */       if (isDetached())
/*      */       {
/*  577 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Object getObject(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  595 */       maybeDirtyTransaction();
/*      */       
/*  597 */       return this.inner.getObject(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  601 */       if (isDetached())
/*      */       {
/*  603 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Object getObject(int a, Map b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  621 */       maybeDirtyTransaction();
/*      */       
/*  623 */       return this.inner.getObject(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  627 */       if (isDetached())
/*      */       {
/*  629 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Object getObject(String a, Map b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  647 */       maybeDirtyTransaction();
/*      */       
/*  649 */       return this.inner.getObject(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  653 */       if (isDetached())
/*      */       {
/*  655 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Object getObject(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  673 */       maybeDirtyTransaction();
/*      */       
/*  675 */       return this.inner.getObject(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  679 */       if (isDetached())
/*      */       {
/*  681 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final boolean getBoolean(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  699 */       maybeDirtyTransaction();
/*      */       
/*  701 */       return this.inner.getBoolean(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  705 */       if (isDetached())
/*      */       {
/*  707 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final boolean getBoolean(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  725 */       maybeDirtyTransaction();
/*      */       
/*  727 */       return this.inner.getBoolean(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  731 */       if (isDetached())
/*      */       {
/*  733 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final byte getByte(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  751 */       maybeDirtyTransaction();
/*      */       
/*  753 */       return this.inner.getByte(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  757 */       if (isDetached())
/*      */       {
/*  759 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final byte getByte(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  777 */       maybeDirtyTransaction();
/*      */       
/*  779 */       return this.inner.getByte(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  783 */       if (isDetached())
/*      */       {
/*  785 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final short getShort(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  803 */       maybeDirtyTransaction();
/*      */       
/*  805 */       return this.inner.getShort(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  809 */       if (isDetached())
/*      */       {
/*  811 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final short getShort(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  829 */       maybeDirtyTransaction();
/*      */       
/*  831 */       return this.inner.getShort(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  835 */       if (isDetached())
/*      */       {
/*  837 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final int getInt(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  855 */       maybeDirtyTransaction();
/*      */       
/*  857 */       return this.inner.getInt(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  861 */       if (isDetached())
/*      */       {
/*  863 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final int getInt(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  881 */       maybeDirtyTransaction();
/*      */       
/*  883 */       return this.inner.getInt(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  887 */       if (isDetached())
/*      */       {
/*  889 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final long getLong(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  907 */       maybeDirtyTransaction();
/*      */       
/*  909 */       return this.inner.getLong(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  913 */       if (isDetached())
/*      */       {
/*  915 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final long getLong(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  933 */       maybeDirtyTransaction();
/*      */       
/*  935 */       return this.inner.getLong(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  939 */       if (isDetached())
/*      */       {
/*  941 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final float getFloat(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  959 */       maybeDirtyTransaction();
/*      */       
/*  961 */       return this.inner.getFloat(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  965 */       if (isDetached())
/*      */       {
/*  967 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final float getFloat(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  985 */       maybeDirtyTransaction();
/*      */       
/*  987 */       return this.inner.getFloat(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  991 */       if (isDetached())
/*      */       {
/*  993 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final double getDouble(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1011 */       maybeDirtyTransaction();
/*      */       
/* 1013 */       return this.inner.getDouble(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1017 */       if (isDetached())
/*      */       {
/* 1019 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final double getDouble(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1037 */       maybeDirtyTransaction();
/*      */       
/* 1039 */       return this.inner.getDouble(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1043 */       if (isDetached())
/*      */       {
/* 1045 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final byte[] getBytes(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1063 */       maybeDirtyTransaction();
/*      */       
/* 1065 */       return this.inner.getBytes(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1069 */       if (isDetached())
/*      */       {
/* 1071 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final byte[] getBytes(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1089 */       maybeDirtyTransaction();
/*      */       
/* 1091 */       return this.inner.getBytes(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1095 */       if (isDetached())
/*      */       {
/* 1097 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Array getArray(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1115 */       maybeDirtyTransaction();
/*      */       
/* 1117 */       return this.inner.getArray(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1121 */       if (isDetached())
/*      */       {
/* 1123 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Array getArray(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1141 */       maybeDirtyTransaction();
/*      */       
/* 1143 */       return this.inner.getArray(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1147 */       if (isDetached())
/*      */       {
/* 1149 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final URL getURL(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1167 */       maybeDirtyTransaction();
/*      */       
/* 1169 */       return this.inner.getURL(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1173 */       if (isDetached())
/*      */       {
/* 1175 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final URL getURL(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1193 */       maybeDirtyTransaction();
/*      */       
/* 1195 */       return this.inner.getURL(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1199 */       if (isDetached())
/*      */       {
/* 1201 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setBoolean(String a, boolean b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1219 */       maybeDirtyTransaction();
/*      */       
/* 1221 */       this.inner.setBoolean(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1225 */       if (isDetached())
/*      */       {
/* 1227 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setByte(String a, byte b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1245 */       maybeDirtyTransaction();
/*      */       
/* 1247 */       this.inner.setByte(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1251 */       if (isDetached())
/*      */       {
/* 1253 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setShort(String a, short b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1271 */       maybeDirtyTransaction();
/*      */       
/* 1273 */       this.inner.setShort(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1277 */       if (isDetached())
/*      */       {
/* 1279 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setInt(String a, int b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1297 */       maybeDirtyTransaction();
/*      */       
/* 1299 */       this.inner.setInt(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1303 */       if (isDetached())
/*      */       {
/* 1305 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setLong(String a, long b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1323 */       maybeDirtyTransaction();
/*      */       
/* 1325 */       this.inner.setLong(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1329 */       if (isDetached())
/*      */       {
/* 1331 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setFloat(String a, float b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1349 */       maybeDirtyTransaction();
/*      */       
/* 1351 */       this.inner.setFloat(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1355 */       if (isDetached())
/*      */       {
/* 1357 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setDouble(String a, double b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1375 */       maybeDirtyTransaction();
/*      */       
/* 1377 */       this.inner.setDouble(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1381 */       if (isDetached())
/*      */       {
/* 1383 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setTimestamp(String a, Timestamp b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1401 */       maybeDirtyTransaction();
/*      */       
/* 1403 */       this.inner.setTimestamp(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1407 */       if (isDetached())
/*      */       {
/* 1409 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setTimestamp(String a, Timestamp b, Calendar c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1427 */       maybeDirtyTransaction();
/*      */       
/* 1429 */       this.inner.setTimestamp(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1433 */       if (isDetached())
/*      */       {
/* 1435 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Ref getRef(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1453 */       maybeDirtyTransaction();
/*      */       
/* 1455 */       return this.inner.getRef(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1459 */       if (isDetached())
/*      */       {
/* 1461 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Ref getRef(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1479 */       maybeDirtyTransaction();
/*      */       
/* 1481 */       return this.inner.getRef(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1485 */       if (isDetached())
/*      */       {
/* 1487 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setURL(String a, URL b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1505 */       maybeDirtyTransaction();
/*      */       
/* 1507 */       this.inner.setURL(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1511 */       if (isDetached())
/*      */       {
/* 1513 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setTime(String a, Time b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1531 */       maybeDirtyTransaction();
/*      */       
/* 1533 */       this.inner.setTime(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1537 */       if (isDetached())
/*      */       {
/* 1539 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setTime(String a, Time b, Calendar c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1557 */       maybeDirtyTransaction();
/*      */       
/* 1559 */       this.inner.setTime(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1563 */       if (isDetached())
/*      */       {
/* 1565 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Time getTime(int a, Calendar b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1583 */       maybeDirtyTransaction();
/*      */       
/* 1585 */       return this.inner.getTime(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1589 */       if (isDetached())
/*      */       {
/* 1591 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Time getTime(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1609 */       maybeDirtyTransaction();
/*      */       
/* 1611 */       return this.inner.getTime(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1615 */       if (isDetached())
/*      */       {
/* 1617 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Time getTime(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1635 */       maybeDirtyTransaction();
/*      */       
/* 1637 */       return this.inner.getTime(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1641 */       if (isDetached())
/*      */       {
/* 1643 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Time getTime(String a, Calendar b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1661 */       maybeDirtyTransaction();
/*      */       
/* 1663 */       return this.inner.getTime(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1667 */       if (isDetached())
/*      */       {
/* 1669 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Date getDate(int a, Calendar b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1687 */       maybeDirtyTransaction();
/*      */       
/* 1689 */       return this.inner.getDate(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1693 */       if (isDetached())
/*      */       {
/* 1695 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Date getDate(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1713 */       maybeDirtyTransaction();
/*      */       
/* 1715 */       return this.inner.getDate(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1719 */       if (isDetached())
/*      */       {
/* 1721 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Date getDate(String a, Calendar b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1739 */       maybeDirtyTransaction();
/*      */       
/* 1741 */       return this.inner.getDate(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1745 */       if (isDetached())
/*      */       {
/* 1747 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Date getDate(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1765 */       maybeDirtyTransaction();
/*      */       
/* 1767 */       return this.inner.getDate(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1771 */       if (isDetached())
/*      */       {
/* 1773 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final String getString(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1791 */       maybeDirtyTransaction();
/*      */       
/* 1793 */       return this.inner.getString(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1797 */       if (isDetached())
/*      */       {
/* 1799 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final String getString(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1817 */       maybeDirtyTransaction();
/*      */       
/* 1819 */       return this.inner.getString(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1823 */       if (isDetached())
/*      */       {
/* 1825 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Timestamp getTimestamp(String a, Calendar b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1843 */       maybeDirtyTransaction();
/*      */       
/* 1845 */       return this.inner.getTimestamp(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1849 */       if (isDetached())
/*      */       {
/* 1851 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Timestamp getTimestamp(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1869 */       maybeDirtyTransaction();
/*      */       
/* 1871 */       return this.inner.getTimestamp(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1875 */       if (isDetached())
/*      */       {
/* 1877 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Timestamp getTimestamp(int a, Calendar b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1895 */       maybeDirtyTransaction();
/*      */       
/* 1897 */       return this.inner.getTimestamp(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1901 */       if (isDetached())
/*      */       {
/* 1903 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final Timestamp getTimestamp(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1921 */       maybeDirtyTransaction();
/*      */       
/* 1923 */       return this.inner.getTimestamp(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1927 */       if (isDetached())
/*      */       {
/* 1929 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setDate(String a, Date b, Calendar c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1947 */       maybeDirtyTransaction();
/*      */       
/* 1949 */       this.inner.setDate(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1953 */       if (isDetached())
/*      */       {
/* 1955 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setDate(String a, Date b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1973 */       maybeDirtyTransaction();
/*      */       
/* 1975 */       this.inner.setDate(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1979 */       if (isDetached())
/*      */       {
/* 1981 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final void setString(String a, String b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1999 */       maybeDirtyTransaction();
/*      */       
/* 2001 */       this.inner.setString(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2005 */       if (isDetached())
/*      */       {
/* 2007 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final BigDecimal getBigDecimal(int a, int b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2025 */       maybeDirtyTransaction();
/*      */       
/* 2027 */       return this.inner.getBigDecimal(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2031 */       if (isDetached())
/*      */       {
/* 2033 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final BigDecimal getBigDecimal(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2051 */       maybeDirtyTransaction();
/*      */       
/* 2053 */       return this.inner.getBigDecimal(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2057 */       if (isDetached())
/*      */       {
/* 2059 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final BigDecimal getBigDecimal(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2077 */       maybeDirtyTransaction();
/*      */       
/* 2079 */       return this.inner.getBigDecimal(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2083 */       if (isDetached())
/*      */       {
/* 2085 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final ResultSetMetaData getMetaData() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2103 */       maybeDirtyTransaction();
/*      */       
/* 2105 */       return this.inner.getMetaData();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2109 */       if (isDetached())
/*      */       {
/* 2111 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
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
/*      */   public final ResultSet executeQuery() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2129 */       maybeDirtyTransaction();
/*      */       
/* 2131 */       ResultSet innerResultSet = this.inner.executeQuery();
/* 2132 */       if (innerResultSet == null) return null;
/* 2133 */       this.parentPooledConnection.markActiveResultSetForStatement(this.inner, innerResultSet);
/* 2134 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2138 */       if (isDetached())
/*      */       {
/* 2140 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2142 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2146 */       if (!isDetached())
/*      */       {
/* 2148 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2150 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int executeUpdate() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2158 */       maybeDirtyTransaction();
/*      */       
/* 2160 */       return this.inner.executeUpdate();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2164 */       if (isDetached())
/*      */       {
/* 2166 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2168 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2172 */       if (!isDetached())
/*      */       {
/* 2174 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2176 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void addBatch() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2184 */       maybeDirtyTransaction();
/*      */       
/* 2186 */       this.inner.addBatch();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2190 */       if (isDetached())
/*      */       {
/* 2192 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2194 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2198 */       if (!isDetached())
/*      */       {
/* 2200 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2202 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setNull(int a, int b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2210 */       maybeDirtyTransaction();
/*      */       
/* 2212 */       this.inner.setNull(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2216 */       if (isDetached())
/*      */       {
/* 2218 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2220 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2224 */       if (!isDetached())
/*      */       {
/* 2226 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2228 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setNull(int a, int b, String c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2236 */       maybeDirtyTransaction();
/*      */       
/* 2238 */       this.inner.setNull(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2242 */       if (isDetached())
/*      */       {
/* 2244 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2246 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2250 */       if (!isDetached())
/*      */       {
/* 2252 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2254 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setBigDecimal(int a, BigDecimal b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2262 */       maybeDirtyTransaction();
/*      */       
/* 2264 */       this.inner.setBigDecimal(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2268 */       if (isDetached())
/*      */       {
/* 2270 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2272 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2276 */       if (!isDetached())
/*      */       {
/* 2278 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2280 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setBytes(int a, byte[] b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2288 */       maybeDirtyTransaction();
/*      */       
/* 2290 */       this.inner.setBytes(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2294 */       if (isDetached())
/*      */       {
/* 2296 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2298 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2302 */       if (!isDetached())
/*      */       {
/* 2304 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2306 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setAsciiStream(int a, InputStream b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2314 */       maybeDirtyTransaction();
/*      */       
/* 2316 */       this.inner.setAsciiStream(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2320 */       if (isDetached())
/*      */       {
/* 2322 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2324 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2328 */       if (!isDetached())
/*      */       {
/* 2330 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2332 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setUnicodeStream(int a, InputStream b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2340 */       maybeDirtyTransaction();
/*      */       
/* 2342 */       this.inner.setUnicodeStream(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2346 */       if (isDetached())
/*      */       {
/* 2348 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2350 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2354 */       if (!isDetached())
/*      */       {
/* 2356 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2358 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setBinaryStream(int a, InputStream b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2366 */       maybeDirtyTransaction();
/*      */       
/* 2368 */       this.inner.setBinaryStream(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2372 */       if (isDetached())
/*      */       {
/* 2374 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2376 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2380 */       if (!isDetached())
/*      */       {
/* 2382 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2384 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void clearParameters() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2392 */       maybeDirtyTransaction();
/*      */       
/* 2394 */       this.inner.clearParameters();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2398 */       if (isDetached())
/*      */       {
/* 2400 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2402 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2406 */       if (!isDetached())
/*      */       {
/* 2408 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2410 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setObject(int a, Object b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2418 */       maybeDirtyTransaction();
/*      */       
/* 2420 */       this.inner.setObject(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2424 */       if (isDetached())
/*      */       {
/* 2426 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2428 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2432 */       if (!isDetached())
/*      */       {
/* 2434 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2436 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setObject(int a, Object b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2444 */       maybeDirtyTransaction();
/*      */       
/* 2446 */       this.inner.setObject(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2450 */       if (isDetached())
/*      */       {
/* 2452 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2454 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2458 */       if (!isDetached())
/*      */       {
/* 2460 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2462 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setObject(int a, Object b, int c, int d) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2470 */       maybeDirtyTransaction();
/*      */       
/* 2472 */       this.inner.setObject(a, b, c, d);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2476 */       if (isDetached())
/*      */       {
/* 2478 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2480 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2484 */       if (!isDetached())
/*      */       {
/* 2486 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2488 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setCharacterStream(int a, Reader b, int c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2496 */       maybeDirtyTransaction();
/*      */       
/* 2498 */       this.inner.setCharacterStream(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2502 */       if (isDetached())
/*      */       {
/* 2504 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2506 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2510 */       if (!isDetached())
/*      */       {
/* 2512 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2514 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setRef(int a, Ref b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2522 */       maybeDirtyTransaction();
/*      */       
/* 2524 */       this.inner.setRef(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2528 */       if (isDetached())
/*      */       {
/* 2530 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2532 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2536 */       if (!isDetached())
/*      */       {
/* 2538 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2540 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setBlob(int a, Blob b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2548 */       maybeDirtyTransaction();
/*      */       
/* 2550 */       this.inner.setBlob(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2554 */       if (isDetached())
/*      */       {
/* 2556 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2558 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2562 */       if (!isDetached())
/*      */       {
/* 2564 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2566 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setClob(int a, Clob b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2574 */       maybeDirtyTransaction();
/*      */       
/* 2576 */       this.inner.setClob(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2580 */       if (isDetached())
/*      */       {
/* 2582 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2584 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2588 */       if (!isDetached())
/*      */       {
/* 2590 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2592 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setArray(int a, Array b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2600 */       maybeDirtyTransaction();
/*      */       
/* 2602 */       this.inner.setArray(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2606 */       if (isDetached())
/*      */       {
/* 2608 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2610 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2614 */       if (!isDetached())
/*      */       {
/* 2616 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2618 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ParameterMetaData getParameterMetaData() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2626 */       maybeDirtyTransaction();
/*      */       
/* 2628 */       return this.inner.getParameterMetaData();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2632 */       if (isDetached())
/*      */       {
/* 2634 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2636 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2640 */       if (!isDetached())
/*      */       {
/* 2642 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2644 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setBoolean(int a, boolean b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2652 */       maybeDirtyTransaction();
/*      */       
/* 2654 */       this.inner.setBoolean(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2658 */       if (isDetached())
/*      */       {
/* 2660 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2662 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2666 */       if (!isDetached())
/*      */       {
/* 2668 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2670 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setByte(int a, byte b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2678 */       maybeDirtyTransaction();
/*      */       
/* 2680 */       this.inner.setByte(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2684 */       if (isDetached())
/*      */       {
/* 2686 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2688 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2692 */       if (!isDetached())
/*      */       {
/* 2694 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2696 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setShort(int a, short b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2704 */       maybeDirtyTransaction();
/*      */       
/* 2706 */       this.inner.setShort(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2710 */       if (isDetached())
/*      */       {
/* 2712 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2714 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2718 */       if (!isDetached())
/*      */       {
/* 2720 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2722 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setInt(int a, int b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2730 */       maybeDirtyTransaction();
/*      */       
/* 2732 */       this.inner.setInt(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2736 */       if (isDetached())
/*      */       {
/* 2738 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2740 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2744 */       if (!isDetached())
/*      */       {
/* 2746 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2748 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setLong(int a, long b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2756 */       maybeDirtyTransaction();
/*      */       
/* 2758 */       this.inner.setLong(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2762 */       if (isDetached())
/*      */       {
/* 2764 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2766 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2770 */       if (!isDetached())
/*      */       {
/* 2772 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2774 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setFloat(int a, float b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2782 */       maybeDirtyTransaction();
/*      */       
/* 2784 */       this.inner.setFloat(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2788 */       if (isDetached())
/*      */       {
/* 2790 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2792 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2796 */       if (!isDetached())
/*      */       {
/* 2798 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2800 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setDouble(int a, double b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2808 */       maybeDirtyTransaction();
/*      */       
/* 2810 */       this.inner.setDouble(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2814 */       if (isDetached())
/*      */       {
/* 2816 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2818 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2822 */       if (!isDetached())
/*      */       {
/* 2824 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2826 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setTimestamp(int a, Timestamp b, Calendar c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2834 */       maybeDirtyTransaction();
/*      */       
/* 2836 */       this.inner.setTimestamp(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2840 */       if (isDetached())
/*      */       {
/* 2842 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2844 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2848 */       if (!isDetached())
/*      */       {
/* 2850 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2852 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setTimestamp(int a, Timestamp b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2860 */       maybeDirtyTransaction();
/*      */       
/* 2862 */       this.inner.setTimestamp(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2866 */       if (isDetached())
/*      */       {
/* 2868 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2870 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2874 */       if (!isDetached())
/*      */       {
/* 2876 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2878 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setURL(int a, URL b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2886 */       maybeDirtyTransaction();
/*      */       
/* 2888 */       this.inner.setURL(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2892 */       if (isDetached())
/*      */       {
/* 2894 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2896 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2900 */       if (!isDetached())
/*      */       {
/* 2902 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2904 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setTime(int a, Time b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2912 */       maybeDirtyTransaction();
/*      */       
/* 2914 */       this.inner.setTime(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2918 */       if (isDetached())
/*      */       {
/* 2920 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2922 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2926 */       if (!isDetached())
/*      */       {
/* 2928 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2930 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setTime(int a, Time b, Calendar c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2938 */       maybeDirtyTransaction();
/*      */       
/* 2940 */       this.inner.setTime(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2944 */       if (isDetached())
/*      */       {
/* 2946 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2948 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2952 */       if (!isDetached())
/*      */       {
/* 2954 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2956 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setDate(int a, Date b, Calendar c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2964 */       maybeDirtyTransaction();
/*      */       
/* 2966 */       this.inner.setDate(a, b, c);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2970 */       if (isDetached())
/*      */       {
/* 2972 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 2974 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2978 */       if (!isDetached())
/*      */       {
/* 2980 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2982 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setDate(int a, Date b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2990 */       maybeDirtyTransaction();
/*      */       
/* 2992 */       this.inner.setDate(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2996 */       if (isDetached())
/*      */       {
/* 2998 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3000 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3004 */       if (!isDetached())
/*      */       {
/* 3006 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3008 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setString(int a, String b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3016 */       maybeDirtyTransaction();
/*      */       
/* 3018 */       this.inner.setString(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3022 */       if (isDetached())
/*      */       {
/* 3024 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3026 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3030 */       if (!isDetached())
/*      */       {
/* 3032 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3034 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean execute() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3042 */       maybeDirtyTransaction();
/*      */       
/* 3044 */       return this.inner.execute();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3048 */       if (isDetached())
/*      */       {
/* 3050 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3052 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3056 */       if (!isDetached())
/*      */       {
/* 3058 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3060 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet executeQuery(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3068 */       maybeDirtyTransaction();
/*      */       
/* 3070 */       ResultSet innerResultSet = this.inner.executeQuery(a);
/* 3071 */       if (innerResultSet == null) return null;
/* 3072 */       this.parentPooledConnection.markActiveResultSetForStatement(this.inner, innerResultSet);
/* 3073 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3077 */       if (isDetached())
/*      */       {
/* 3079 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3081 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3085 */       if (!isDetached())
/*      */       {
/* 3087 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3089 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int executeUpdate(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3097 */       maybeDirtyTransaction();
/*      */       
/* 3099 */       return this.inner.executeUpdate(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3103 */       if (isDetached())
/*      */       {
/* 3105 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3107 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3111 */       if (!isDetached())
/*      */       {
/* 3113 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3115 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int executeUpdate(String a, String[] b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3123 */       maybeDirtyTransaction();
/*      */       
/* 3125 */       return this.inner.executeUpdate(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3129 */       if (isDetached())
/*      */       {
/* 3131 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3133 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3137 */       if (!isDetached())
/*      */       {
/* 3139 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3141 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int executeUpdate(String a, int b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3149 */       maybeDirtyTransaction();
/*      */       
/* 3151 */       return this.inner.executeUpdate(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3155 */       if (isDetached())
/*      */       {
/* 3157 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3159 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3163 */       if (!isDetached())
/*      */       {
/* 3165 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3167 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int executeUpdate(String a, int[] b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3175 */       maybeDirtyTransaction();
/*      */       
/* 3177 */       return this.inner.executeUpdate(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3181 */       if (isDetached())
/*      */       {
/* 3183 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3185 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3189 */       if (!isDetached())
/*      */       {
/* 3191 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3193 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxFieldSize() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3201 */       maybeDirtyTransaction();
/*      */       
/* 3203 */       return this.inner.getMaxFieldSize();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3207 */       if (isDetached())
/*      */       {
/* 3209 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3211 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3215 */       if (!isDetached())
/*      */       {
/* 3217 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3219 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setMaxFieldSize(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3227 */       maybeDirtyTransaction();
/*      */       
/* 3229 */       this.inner.setMaxFieldSize(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3233 */       if (isDetached())
/*      */       {
/* 3235 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3237 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3241 */       if (!isDetached())
/*      */       {
/* 3243 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3245 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxRows() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3253 */       maybeDirtyTransaction();
/*      */       
/* 3255 */       return this.inner.getMaxRows();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3259 */       if (isDetached())
/*      */       {
/* 3261 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3263 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3267 */       if (!isDetached())
/*      */       {
/* 3269 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3271 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setMaxRows(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3279 */       maybeDirtyTransaction();
/*      */       
/* 3281 */       this.inner.setMaxRows(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3285 */       if (isDetached())
/*      */       {
/* 3287 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3289 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3293 */       if (!isDetached())
/*      */       {
/* 3295 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3297 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setEscapeProcessing(boolean a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3305 */       maybeDirtyTransaction();
/*      */       
/* 3307 */       this.inner.setEscapeProcessing(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3311 */       if (isDetached())
/*      */       {
/* 3313 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3315 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3319 */       if (!isDetached())
/*      */       {
/* 3321 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3323 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getQueryTimeout() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3331 */       maybeDirtyTransaction();
/*      */       
/* 3333 */       return this.inner.getQueryTimeout();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3337 */       if (isDetached())
/*      */       {
/* 3339 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3341 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3345 */       if (!isDetached())
/*      */       {
/* 3347 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3349 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setQueryTimeout(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3357 */       maybeDirtyTransaction();
/*      */       
/* 3359 */       this.inner.setQueryTimeout(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3363 */       if (isDetached())
/*      */       {
/* 3365 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3367 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3371 */       if (!isDetached())
/*      */       {
/* 3373 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3375 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final SQLWarning getWarnings() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3383 */       maybeDirtyTransaction();
/*      */       
/* 3385 */       return this.inner.getWarnings();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3389 */       if (isDetached())
/*      */       {
/* 3391 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3393 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3397 */       if (!isDetached())
/*      */       {
/* 3399 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3401 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void clearWarnings() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3409 */       maybeDirtyTransaction();
/*      */       
/* 3411 */       this.inner.clearWarnings();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3415 */       if (isDetached())
/*      */       {
/* 3417 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3419 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3423 */       if (!isDetached())
/*      */       {
/* 3425 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3427 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setCursorName(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3435 */       maybeDirtyTransaction();
/*      */       
/* 3437 */       this.inner.setCursorName(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3441 */       if (isDetached())
/*      */       {
/* 3443 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3445 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3449 */       if (!isDetached())
/*      */       {
/* 3451 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3453 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getResultSet() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3461 */       maybeDirtyTransaction();
/*      */       
/* 3463 */       ResultSet innerResultSet = this.inner.getResultSet();
/* 3464 */       if (innerResultSet == null) return null;
/* 3465 */       this.parentPooledConnection.markActiveResultSetForStatement(this.inner, innerResultSet);
/* 3466 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3470 */       if (isDetached())
/*      */       {
/* 3472 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3474 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3478 */       if (!isDetached())
/*      */       {
/* 3480 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3482 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getUpdateCount() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3490 */       maybeDirtyTransaction();
/*      */       
/* 3492 */       return this.inner.getUpdateCount();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3496 */       if (isDetached())
/*      */       {
/* 3498 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3500 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3504 */       if (!isDetached())
/*      */       {
/* 3506 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3508 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean getMoreResults() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3516 */       maybeDirtyTransaction();
/*      */       
/* 3518 */       return this.inner.getMoreResults();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3522 */       if (isDetached())
/*      */       {
/* 3524 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3526 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3530 */       if (!isDetached())
/*      */       {
/* 3532 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3534 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean getMoreResults(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3542 */       maybeDirtyTransaction();
/*      */       
/* 3544 */       return this.inner.getMoreResults(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3548 */       if (isDetached())
/*      */       {
/* 3550 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3552 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3556 */       if (!isDetached())
/*      */       {
/* 3558 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3560 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setFetchDirection(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3568 */       maybeDirtyTransaction();
/*      */       
/* 3570 */       this.inner.setFetchDirection(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3574 */       if (isDetached())
/*      */       {
/* 3576 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3578 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3582 */       if (!isDetached())
/*      */       {
/* 3584 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3586 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getFetchDirection() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3594 */       maybeDirtyTransaction();
/*      */       
/* 3596 */       return this.inner.getFetchDirection();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3600 */       if (isDetached())
/*      */       {
/* 3602 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3604 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3608 */       if (!isDetached())
/*      */       {
/* 3610 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3612 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void setFetchSize(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3620 */       maybeDirtyTransaction();
/*      */       
/* 3622 */       this.inner.setFetchSize(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3626 */       if (isDetached())
/*      */       {
/* 3628 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3630 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3634 */       if (!isDetached())
/*      */       {
/* 3636 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3638 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getFetchSize() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3646 */       maybeDirtyTransaction();
/*      */       
/* 3648 */       return this.inner.getFetchSize();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3652 */       if (isDetached())
/*      */       {
/* 3654 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3656 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3660 */       if (!isDetached())
/*      */       {
/* 3662 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3664 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getResultSetConcurrency() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3672 */       maybeDirtyTransaction();
/*      */       
/* 3674 */       return this.inner.getResultSetConcurrency();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3678 */       if (isDetached())
/*      */       {
/* 3680 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3682 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3686 */       if (!isDetached())
/*      */       {
/* 3688 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3690 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getResultSetType() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3698 */       maybeDirtyTransaction();
/*      */       
/* 3700 */       return this.inner.getResultSetType();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3704 */       if (isDetached())
/*      */       {
/* 3706 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3708 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3712 */       if (!isDetached())
/*      */       {
/* 3714 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3716 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void addBatch(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3724 */       maybeDirtyTransaction();
/*      */       
/* 3726 */       this.inner.addBatch(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3730 */       if (isDetached())
/*      */       {
/* 3732 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3734 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3738 */       if (!isDetached())
/*      */       {
/* 3740 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3742 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void clearBatch() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3750 */       maybeDirtyTransaction();
/*      */       
/* 3752 */       this.inner.clearBatch();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3756 */       if (isDetached())
/*      */       {
/* 3758 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3760 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3764 */       if (!isDetached())
/*      */       {
/* 3766 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3768 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int[] executeBatch() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3776 */       maybeDirtyTransaction();
/*      */       
/* 3778 */       return this.inner.executeBatch();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3782 */       if (isDetached())
/*      */       {
/* 3784 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3786 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3790 */       if (!isDetached())
/*      */       {
/* 3792 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3794 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getGeneratedKeys() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3802 */       maybeDirtyTransaction();
/*      */       
/* 3804 */       ResultSet innerResultSet = this.inner.getGeneratedKeys();
/* 3805 */       if (innerResultSet == null) return null;
/* 3806 */       this.parentPooledConnection.markActiveResultSetForStatement(this.inner, innerResultSet);
/* 3807 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3811 */       if (isDetached())
/*      */       {
/* 3813 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3815 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3819 */       if (!isDetached())
/*      */       {
/* 3821 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3823 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getResultSetHoldability() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3831 */       maybeDirtyTransaction();
/*      */       
/* 3833 */       return this.inner.getResultSetHoldability();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3837 */       if (isDetached())
/*      */       {
/* 3839 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3841 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3845 */       if (!isDetached())
/*      */       {
/* 3847 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3849 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void close() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3857 */       maybeDirtyTransaction();
/*      */       
/* 3859 */       if (!isDetached())
/*      */       {
/* 3861 */         if (this.is_cached) {
/* 3862 */           this.parentPooledConnection.checkinStatement(this.inner);
/*      */         }
/*      */         else {
/* 3865 */           this.parentPooledConnection.markInactiveUncachedStatement(this.inner);
/* 3866 */           try { this.inner.close();
/*      */           }
/*      */           catch (Exception e) {
/* 3869 */             if (logger.isLoggable(MLevel.WARNING))
/* 3870 */               logger.log(MLevel.WARNING, "Exception on close of inner statement.", e);
/* 3871 */             SQLException sqle = SqlUtils.toSQLException(e);
/* 3872 */             throw sqle;
/*      */           }
/*      */         }
/*      */         
/* 3876 */         detach();
/* 3877 */         this.inner = null;
/* 3878 */         this.creatorProxy = null;
/*      */       }
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3883 */       if (isDetached())
/*      */       {
/* 3885 */         if (logger.isLoggable(MLevel.FINE))
/*      */         {
/* 3887 */           logger.log(MLevel.FINE, this + ": close() called more than once.");
/*      */         }
/*      */       } else {
/* 3890 */         throw exc;
/*      */       }
/*      */     }
/*      */     catch (Exception exc) {
/* 3894 */       if (!isDetached())
/*      */       {
/* 3896 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3898 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final void cancel() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3906 */       maybeDirtyTransaction();
/*      */       
/* 3908 */       this.inner.cancel();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3912 */       if (isDetached())
/*      */       {
/* 3914 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3916 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3920 */       if (!isDetached())
/*      */       {
/* 3922 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3924 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Connection getConnection() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3932 */       maybeDirtyTransaction();
/*      */       
/* 3934 */       if (!isDetached()) {
/* 3935 */         return this.creatorProxy;
/*      */       }
/* 3937 */       throw new SQLException("You cannot operate on a closed Statement!");
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3941 */       if (isDetached())
/*      */       {
/* 3943 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3945 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3949 */       if (!isDetached())
/*      */       {
/* 3951 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3953 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean execute(String a, int b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3961 */       maybeDirtyTransaction();
/*      */       
/* 3963 */       return this.inner.execute(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3967 */       if (isDetached())
/*      */       {
/* 3969 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3971 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3975 */       if (!isDetached())
/*      */       {
/* 3977 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3979 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean execute(String a, int[] b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3987 */       maybeDirtyTransaction();
/*      */       
/* 3989 */       return this.inner.execute(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3993 */       if (isDetached())
/*      */       {
/* 3995 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 3997 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 4001 */       if (!isDetached())
/*      */       {
/* 4003 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 4005 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean execute(String a, String[] b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 4013 */       maybeDirtyTransaction();
/*      */       
/* 4015 */       return this.inner.execute(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 4019 */       if (isDetached())
/*      */       {
/* 4021 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 4023 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 4027 */       if (!isDetached())
/*      */       {
/* 4029 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 4031 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean execute(String a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 4039 */       maybeDirtyTransaction();
/*      */       
/* 4041 */       return this.inner.execute(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 4045 */       if (isDetached())
/*      */       {
/* 4047 */         throw SqlUtils.toSQLException("You can't operate on a closed Statement!!!", exc);
/*      */       }
/* 4049 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 4053 */       if (!isDetached())
/*      */       {
/* 4055 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 4057 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/* 4061 */   private static final MLogger logger = MLog.getLogger("com.mchange.v2.c3p0.impl.NewProxyCallableStatement");
/*      */   
/*      */   volatile NewPooledConnection parentPooledConnection;
/*      */   
/* 4065 */   ConnectionEventListener cel = new ConnectionEventListener()
/*      */   {
/*      */     public void connectionErrorOccurred(ConnectionEvent evt) {}
/*      */     
/*      */ 
/*      */ 
/* 4071 */     public void connectionClosed(ConnectionEvent evt) { NewProxyCallableStatement.this.detach(); }
/*      */   };
/*      */   boolean is_cached;
/*      */   NewProxyConnection creatorProxy;
/*      */   
/* 4076 */   void attach(NewPooledConnection parentPooledConnection) { this.parentPooledConnection = parentPooledConnection;
/* 4077 */     parentPooledConnection.addConnectionEventListener(this.cel);
/*      */   }
/*      */   
/*      */   private void detach()
/*      */   {
/* 4082 */     this.parentPooledConnection.removeConnectionEventListener(this.cel);
/* 4083 */     this.parentPooledConnection = null;
/*      */   }
/*      */   
/*      */   NewProxyCallableStatement(CallableStatement inner, NewPooledConnection parentPooledConnection)
/*      */   {
/* 4088 */     this(inner);
/* 4089 */     attach(parentPooledConnection);
/*      */   }
/*      */   
/*      */   boolean isDetached() {
/* 4093 */     return this.parentPooledConnection == null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   NewProxyCallableStatement(CallableStatement inner, NewPooledConnection parentPooledConnection, boolean cached, NewProxyConnection cProxy)
/*      */   {
/* 4100 */     this(inner, parentPooledConnection);
/* 4101 */     this.is_cached = cached;
/* 4102 */     this.creatorProxy = cProxy;
/*      */   }
/*      */   
/*      */   public Object rawStatementOperation(Method m, Object target, Object[] args) throws IllegalAccessException, InvocationTargetException, SQLException
/*      */   {
/* 4107 */     maybeDirtyTransaction();
/*      */     
/* 4109 */     if (target == C3P0ProxyStatement.RAW_STATEMENT) target = this.inner;
/* 4110 */     int i = 0; for (int len = args.length; i < len; i++)
/* 4111 */       if (args[i] == C3P0ProxyStatement.RAW_STATEMENT) args[i] = this.inner;
/* 4112 */     Object out = m.invoke(target, args);
/* 4113 */     if ((out instanceof ResultSet))
/*      */     {
/* 4115 */       ResultSet innerResultSet = (ResultSet)out;
/* 4116 */       this.parentPooledConnection.markActiveResultSetForStatement(this.inner, innerResultSet);
/* 4117 */       out = new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     
/* 4120 */     return out;
/*      */   }
/*      */   
/*      */   void maybeDirtyTransaction() {
/* 4124 */     this.creatorProxy.maybeDirtyTransaction();
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\NewProxyCallableStatement.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */