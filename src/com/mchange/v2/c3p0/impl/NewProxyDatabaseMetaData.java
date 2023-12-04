/*      */ package com.mchange.v2.c3p0.impl;
/*      */ 
/*      */ import com.mchange.v2.log.MLog;
/*      */ import com.mchange.v2.log.MLogger;
/*      */ import com.mchange.v2.sql.SqlUtils;
/*      */ import java.sql.Connection;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import javax.sql.ConnectionEvent;
/*      */ import javax.sql.ConnectionEventListener;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class NewProxyDatabaseMetaData
/*      */   implements DatabaseMetaData
/*      */ {
/*      */   protected DatabaseMetaData inner;
/*      */   
/*      */   public NewProxyDatabaseMetaData(DatabaseMetaData inner)
/*      */   {
/*   24 */     this.inner = inner;
/*      */   }
/*      */   
/*      */   public final int getResultSetHoldability() throws SQLException
/*      */   {
/*      */     try {
/*   30 */       return this.inner.getResultSetHoldability();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*   34 */       if (isDetached())
/*      */       {
/*   36 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*   38 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*   42 */       if (!isDetached())
/*      */       {
/*   44 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*   46 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean allProceduresAreCallable() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*   54 */       return this.inner.allProceduresAreCallable();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*   58 */       if (isDetached())
/*      */       {
/*   60 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*   62 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*   66 */       if (!isDetached())
/*      */       {
/*   68 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*   70 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean allTablesAreSelectable() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*   78 */       return this.inner.allTablesAreSelectable();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*   82 */       if (isDetached())
/*      */       {
/*   84 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*   86 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*   90 */       if (!isDetached())
/*      */       {
/*   92 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*   94 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean nullsAreSortedHigh() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  102 */       return this.inner.nullsAreSortedHigh();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  106 */       if (isDetached())
/*      */       {
/*  108 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  110 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  114 */       if (!isDetached())
/*      */       {
/*  116 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  118 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean nullsAreSortedLow() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  126 */       return this.inner.nullsAreSortedLow();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  130 */       if (isDetached())
/*      */       {
/*  132 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  134 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  138 */       if (!isDetached())
/*      */       {
/*  140 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  142 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean nullsAreSortedAtStart() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  150 */       return this.inner.nullsAreSortedAtStart();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  154 */       if (isDetached())
/*      */       {
/*  156 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  158 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  162 */       if (!isDetached())
/*      */       {
/*  164 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  166 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean nullsAreSortedAtEnd() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  174 */       return this.inner.nullsAreSortedAtEnd();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  178 */       if (isDetached())
/*      */       {
/*  180 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  182 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  186 */       if (!isDetached())
/*      */       {
/*  188 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  190 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final String getDatabaseProductName() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  198 */       return this.inner.getDatabaseProductName();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  202 */       if (isDetached())
/*      */       {
/*  204 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  206 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  210 */       if (!isDetached())
/*      */       {
/*  212 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  214 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final String getDatabaseProductVersion() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  222 */       return this.inner.getDatabaseProductVersion();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  226 */       if (isDetached())
/*      */       {
/*  228 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  230 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  234 */       if (!isDetached())
/*      */       {
/*  236 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  238 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final String getDriverName() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  246 */       return this.inner.getDriverName();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  250 */       if (isDetached())
/*      */       {
/*  252 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
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
/*      */   public final String getDriverVersion() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  270 */       return this.inner.getDriverVersion();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  274 */       if (isDetached())
/*      */       {
/*  276 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  278 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  282 */       if (!isDetached())
/*      */       {
/*  284 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  286 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getDriverMajorVersion()
/*      */   {
/*  292 */     return this.inner.getDriverMajorVersion();
/*      */   }
/*      */   
/*      */   public final int getDriverMinorVersion()
/*      */   {
/*  297 */     return this.inner.getDriverMinorVersion();
/*      */   }
/*      */   
/*      */   public final boolean usesLocalFiles() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  304 */       return this.inner.usesLocalFiles();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  308 */       if (isDetached())
/*      */       {
/*  310 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  312 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  316 */       if (!isDetached())
/*      */       {
/*  318 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  320 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean usesLocalFilePerTable() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  328 */       return this.inner.usesLocalFilePerTable();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  332 */       if (isDetached())
/*      */       {
/*  334 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  336 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  340 */       if (!isDetached())
/*      */       {
/*  342 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  344 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsMixedCaseIdentifiers() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  352 */       return this.inner.supportsMixedCaseIdentifiers();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  356 */       if (isDetached())
/*      */       {
/*  358 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  360 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  364 */       if (!isDetached())
/*      */       {
/*  366 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  368 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean storesUpperCaseIdentifiers() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  376 */       return this.inner.storesUpperCaseIdentifiers();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  380 */       if (isDetached())
/*      */       {
/*  382 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
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
/*      */   public final boolean storesLowerCaseIdentifiers() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  400 */       return this.inner.storesLowerCaseIdentifiers();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  404 */       if (isDetached())
/*      */       {
/*  406 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  408 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  412 */       if (!isDetached())
/*      */       {
/*  414 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  416 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean storesMixedCaseIdentifiers() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  424 */       return this.inner.storesMixedCaseIdentifiers();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  428 */       if (isDetached())
/*      */       {
/*  430 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  432 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  436 */       if (!isDetached())
/*      */       {
/*  438 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  440 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsMixedCaseQuotedIdentifiers() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  448 */       return this.inner.supportsMixedCaseQuotedIdentifiers();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  452 */       if (isDetached())
/*      */       {
/*  454 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  456 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  460 */       if (!isDetached())
/*      */       {
/*  462 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  464 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean storesUpperCaseQuotedIdentifiers() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  472 */       return this.inner.storesUpperCaseQuotedIdentifiers();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  476 */       if (isDetached())
/*      */       {
/*  478 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  480 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  484 */       if (!isDetached())
/*      */       {
/*  486 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  488 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean storesLowerCaseQuotedIdentifiers() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  496 */       return this.inner.storesLowerCaseQuotedIdentifiers();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  500 */       if (isDetached())
/*      */       {
/*  502 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  504 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  508 */       if (!isDetached())
/*      */       {
/*  510 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  512 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean storesMixedCaseQuotedIdentifiers() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  520 */       return this.inner.storesMixedCaseQuotedIdentifiers();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  524 */       if (isDetached())
/*      */       {
/*  526 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  528 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  532 */       if (!isDetached())
/*      */       {
/*  534 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  536 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final String getIdentifierQuoteString() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  544 */       return this.inner.getIdentifierQuoteString();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  548 */       if (isDetached())
/*      */       {
/*  550 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  552 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  556 */       if (!isDetached())
/*      */       {
/*  558 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  560 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final String getSQLKeywords() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  568 */       return this.inner.getSQLKeywords();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  572 */       if (isDetached())
/*      */       {
/*  574 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  576 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  580 */       if (!isDetached())
/*      */       {
/*  582 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  584 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final String getNumericFunctions() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  592 */       return this.inner.getNumericFunctions();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  596 */       if (isDetached())
/*      */       {
/*  598 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  600 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  604 */       if (!isDetached())
/*      */       {
/*  606 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  608 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final String getStringFunctions() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  616 */       return this.inner.getStringFunctions();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  620 */       if (isDetached())
/*      */       {
/*  622 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  624 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  628 */       if (!isDetached())
/*      */       {
/*  630 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  632 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final String getSystemFunctions() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  640 */       return this.inner.getSystemFunctions();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  644 */       if (isDetached())
/*      */       {
/*  646 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  648 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  652 */       if (!isDetached())
/*      */       {
/*  654 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  656 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final String getTimeDateFunctions() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  664 */       return this.inner.getTimeDateFunctions();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  668 */       if (isDetached())
/*      */       {
/*  670 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  672 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  676 */       if (!isDetached())
/*      */       {
/*  678 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  680 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final String getSearchStringEscape() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  688 */       return this.inner.getSearchStringEscape();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  692 */       if (isDetached())
/*      */       {
/*  694 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  696 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  700 */       if (!isDetached())
/*      */       {
/*  702 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  704 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final String getExtraNameCharacters() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  712 */       return this.inner.getExtraNameCharacters();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  716 */       if (isDetached())
/*      */       {
/*  718 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  720 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  724 */       if (!isDetached())
/*      */       {
/*  726 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  728 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsAlterTableWithAddColumn() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  736 */       return this.inner.supportsAlterTableWithAddColumn();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  740 */       if (isDetached())
/*      */       {
/*  742 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  744 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  748 */       if (!isDetached())
/*      */       {
/*  750 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  752 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsAlterTableWithDropColumn() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  760 */       return this.inner.supportsAlterTableWithDropColumn();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  764 */       if (isDetached())
/*      */       {
/*  766 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  768 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  772 */       if (!isDetached())
/*      */       {
/*  774 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  776 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsColumnAliasing() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  784 */       return this.inner.supportsColumnAliasing();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  788 */       if (isDetached())
/*      */       {
/*  790 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  792 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  796 */       if (!isDetached())
/*      */       {
/*  798 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  800 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean nullPlusNonNullIsNull() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  808 */       return this.inner.nullPlusNonNullIsNull();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  812 */       if (isDetached())
/*      */       {
/*  814 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  816 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  820 */       if (!isDetached())
/*      */       {
/*  822 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  824 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsConvert() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  832 */       return this.inner.supportsConvert();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  836 */       if (isDetached())
/*      */       {
/*  838 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  840 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  844 */       if (!isDetached())
/*      */       {
/*  846 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  848 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsConvert(int a, int b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  856 */       return this.inner.supportsConvert(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  860 */       if (isDetached())
/*      */       {
/*  862 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  864 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  868 */       if (!isDetached())
/*      */       {
/*  870 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  872 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsTableCorrelationNames() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  880 */       return this.inner.supportsTableCorrelationNames();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  884 */       if (isDetached())
/*      */       {
/*  886 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  888 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  892 */       if (!isDetached())
/*      */       {
/*  894 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  896 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsDifferentTableCorrelationNames() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  904 */       return this.inner.supportsDifferentTableCorrelationNames();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  908 */       if (isDetached())
/*      */       {
/*  910 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  912 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  916 */       if (!isDetached())
/*      */       {
/*  918 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  920 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsExpressionsInOrderBy() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  928 */       return this.inner.supportsExpressionsInOrderBy();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  932 */       if (isDetached())
/*      */       {
/*  934 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
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
/*      */   public final boolean supportsOrderByUnrelated() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  952 */       return this.inner.supportsOrderByUnrelated();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  956 */       if (isDetached())
/*      */       {
/*  958 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  960 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  964 */       if (!isDetached())
/*      */       {
/*  966 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  968 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsGroupBy() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  976 */       return this.inner.supportsGroupBy();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/*  980 */       if (isDetached())
/*      */       {
/*  982 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/*  984 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/*  988 */       if (!isDetached())
/*      */       {
/*  990 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/*  992 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsGroupByUnrelated() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1000 */       return this.inner.supportsGroupByUnrelated();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1004 */       if (isDetached())
/*      */       {
/* 1006 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1008 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1012 */       if (!isDetached())
/*      */       {
/* 1014 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1016 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsGroupByBeyondSelect() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1024 */       return this.inner.supportsGroupByBeyondSelect();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1028 */       if (isDetached())
/*      */       {
/* 1030 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1032 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1036 */       if (!isDetached())
/*      */       {
/* 1038 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1040 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsLikeEscapeClause() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1048 */       return this.inner.supportsLikeEscapeClause();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1052 */       if (isDetached())
/*      */       {
/* 1054 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1056 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1060 */       if (!isDetached())
/*      */       {
/* 1062 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1064 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsMultipleResultSets() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1072 */       return this.inner.supportsMultipleResultSets();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1076 */       if (isDetached())
/*      */       {
/* 1078 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1080 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1084 */       if (!isDetached())
/*      */       {
/* 1086 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1088 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsMultipleTransactions() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1096 */       return this.inner.supportsMultipleTransactions();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1100 */       if (isDetached())
/*      */       {
/* 1102 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
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
/*      */   public final boolean supportsNonNullableColumns() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1120 */       return this.inner.supportsNonNullableColumns();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1124 */       if (isDetached())
/*      */       {
/* 1126 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1128 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1132 */       if (!isDetached())
/*      */       {
/* 1134 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1136 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsMinimumSQLGrammar() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1144 */       return this.inner.supportsMinimumSQLGrammar();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1148 */       if (isDetached())
/*      */       {
/* 1150 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1152 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1156 */       if (!isDetached())
/*      */       {
/* 1158 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1160 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsCoreSQLGrammar() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1168 */       return this.inner.supportsCoreSQLGrammar();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1172 */       if (isDetached())
/*      */       {
/* 1174 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1176 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1180 */       if (!isDetached())
/*      */       {
/* 1182 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1184 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsExtendedSQLGrammar() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1192 */       return this.inner.supportsExtendedSQLGrammar();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1196 */       if (isDetached())
/*      */       {
/* 1198 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
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
/*      */   public final boolean supportsANSI92EntryLevelSQL() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1216 */       return this.inner.supportsANSI92EntryLevelSQL();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1220 */       if (isDetached())
/*      */       {
/* 1222 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1224 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1228 */       if (!isDetached())
/*      */       {
/* 1230 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1232 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsANSI92IntermediateSQL() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1240 */       return this.inner.supportsANSI92IntermediateSQL();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1244 */       if (isDetached())
/*      */       {
/* 1246 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1248 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1252 */       if (!isDetached())
/*      */       {
/* 1254 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1256 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsANSI92FullSQL() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1264 */       return this.inner.supportsANSI92FullSQL();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1268 */       if (isDetached())
/*      */       {
/* 1270 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1272 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1276 */       if (!isDetached())
/*      */       {
/* 1278 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1280 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsIntegrityEnhancementFacility() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1288 */       return this.inner.supportsIntegrityEnhancementFacility();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1292 */       if (isDetached())
/*      */       {
/* 1294 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1296 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1300 */       if (!isDetached())
/*      */       {
/* 1302 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1304 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsOuterJoins() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1312 */       return this.inner.supportsOuterJoins();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1316 */       if (isDetached())
/*      */       {
/* 1318 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1320 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1324 */       if (!isDetached())
/*      */       {
/* 1326 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1328 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsFullOuterJoins() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1336 */       return this.inner.supportsFullOuterJoins();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1340 */       if (isDetached())
/*      */       {
/* 1342 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1344 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1348 */       if (!isDetached())
/*      */       {
/* 1350 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1352 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsLimitedOuterJoins() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1360 */       return this.inner.supportsLimitedOuterJoins();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1364 */       if (isDetached())
/*      */       {
/* 1366 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1368 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1372 */       if (!isDetached())
/*      */       {
/* 1374 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1376 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final String getSchemaTerm() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1384 */       return this.inner.getSchemaTerm();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1388 */       if (isDetached())
/*      */       {
/* 1390 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1392 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1396 */       if (!isDetached())
/*      */       {
/* 1398 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1400 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final String getProcedureTerm() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1408 */       return this.inner.getProcedureTerm();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1412 */       if (isDetached())
/*      */       {
/* 1414 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1416 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1420 */       if (!isDetached())
/*      */       {
/* 1422 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1424 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final String getCatalogTerm() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1432 */       return this.inner.getCatalogTerm();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1436 */       if (isDetached())
/*      */       {
/* 1438 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1440 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1444 */       if (!isDetached())
/*      */       {
/* 1446 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1448 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean isCatalogAtStart() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1456 */       return this.inner.isCatalogAtStart();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1460 */       if (isDetached())
/*      */       {
/* 1462 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1464 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1468 */       if (!isDetached())
/*      */       {
/* 1470 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1472 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final String getCatalogSeparator() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1480 */       return this.inner.getCatalogSeparator();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1484 */       if (isDetached())
/*      */       {
/* 1486 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1488 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1492 */       if (!isDetached())
/*      */       {
/* 1494 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1496 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsSchemasInDataManipulation() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1504 */       return this.inner.supportsSchemasInDataManipulation();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1508 */       if (isDetached())
/*      */       {
/* 1510 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1512 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1516 */       if (!isDetached())
/*      */       {
/* 1518 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1520 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsSchemasInProcedureCalls() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1528 */       return this.inner.supportsSchemasInProcedureCalls();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1532 */       if (isDetached())
/*      */       {
/* 1534 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1536 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1540 */       if (!isDetached())
/*      */       {
/* 1542 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1544 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsSchemasInTableDefinitions() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1552 */       return this.inner.supportsSchemasInTableDefinitions();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1556 */       if (isDetached())
/*      */       {
/* 1558 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1560 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1564 */       if (!isDetached())
/*      */       {
/* 1566 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1568 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsSchemasInIndexDefinitions() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1576 */       return this.inner.supportsSchemasInIndexDefinitions();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1580 */       if (isDetached())
/*      */       {
/* 1582 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1584 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1588 */       if (!isDetached())
/*      */       {
/* 1590 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1592 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsSchemasInPrivilegeDefinitions() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1600 */       return this.inner.supportsSchemasInPrivilegeDefinitions();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1604 */       if (isDetached())
/*      */       {
/* 1606 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1608 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1612 */       if (!isDetached())
/*      */       {
/* 1614 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1616 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsCatalogsInDataManipulation() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1624 */       return this.inner.supportsCatalogsInDataManipulation();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1628 */       if (isDetached())
/*      */       {
/* 1630 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1632 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1636 */       if (!isDetached())
/*      */       {
/* 1638 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1640 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsCatalogsInProcedureCalls() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1648 */       return this.inner.supportsCatalogsInProcedureCalls();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1652 */       if (isDetached())
/*      */       {
/* 1654 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1656 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1660 */       if (!isDetached())
/*      */       {
/* 1662 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1664 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsCatalogsInTableDefinitions() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1672 */       return this.inner.supportsCatalogsInTableDefinitions();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1676 */       if (isDetached())
/*      */       {
/* 1678 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1680 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1684 */       if (!isDetached())
/*      */       {
/* 1686 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1688 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsCatalogsInIndexDefinitions() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1696 */       return this.inner.supportsCatalogsInIndexDefinitions();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1700 */       if (isDetached())
/*      */       {
/* 1702 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1704 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1708 */       if (!isDetached())
/*      */       {
/* 1710 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1712 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1720 */       return this.inner.supportsCatalogsInPrivilegeDefinitions();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1724 */       if (isDetached())
/*      */       {
/* 1726 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1728 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1732 */       if (!isDetached())
/*      */       {
/* 1734 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1736 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsPositionedDelete() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1744 */       return this.inner.supportsPositionedDelete();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1748 */       if (isDetached())
/*      */       {
/* 1750 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1752 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1756 */       if (!isDetached())
/*      */       {
/* 1758 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1760 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsPositionedUpdate() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1768 */       return this.inner.supportsPositionedUpdate();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1772 */       if (isDetached())
/*      */       {
/* 1774 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1776 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1780 */       if (!isDetached())
/*      */       {
/* 1782 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1784 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsSelectForUpdate() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1792 */       return this.inner.supportsSelectForUpdate();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1796 */       if (isDetached())
/*      */       {
/* 1798 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1800 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1804 */       if (!isDetached())
/*      */       {
/* 1806 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1808 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsStoredProcedures() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1816 */       return this.inner.supportsStoredProcedures();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1820 */       if (isDetached())
/*      */       {
/* 1822 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1824 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1828 */       if (!isDetached())
/*      */       {
/* 1830 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1832 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsSubqueriesInComparisons() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1840 */       return this.inner.supportsSubqueriesInComparisons();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1844 */       if (isDetached())
/*      */       {
/* 1846 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1848 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1852 */       if (!isDetached())
/*      */       {
/* 1854 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1856 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsSubqueriesInExists() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1864 */       return this.inner.supportsSubqueriesInExists();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1868 */       if (isDetached())
/*      */       {
/* 1870 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1872 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1876 */       if (!isDetached())
/*      */       {
/* 1878 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1880 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsSubqueriesInIns() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1888 */       return this.inner.supportsSubqueriesInIns();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1892 */       if (isDetached())
/*      */       {
/* 1894 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1896 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1900 */       if (!isDetached())
/*      */       {
/* 1902 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1904 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsSubqueriesInQuantifieds() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1912 */       return this.inner.supportsSubqueriesInQuantifieds();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1916 */       if (isDetached())
/*      */       {
/* 1918 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1920 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1924 */       if (!isDetached())
/*      */       {
/* 1926 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1928 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsCorrelatedSubqueries() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1936 */       return this.inner.supportsCorrelatedSubqueries();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1940 */       if (isDetached())
/*      */       {
/* 1942 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1944 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1948 */       if (!isDetached())
/*      */       {
/* 1950 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 1952 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsUnion() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1960 */       return this.inner.supportsUnion();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1964 */       if (isDetached())
/*      */       {
/* 1966 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
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
/*      */   public final boolean supportsUnionAll() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1984 */       return this.inner.supportsUnionAll();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 1988 */       if (isDetached())
/*      */       {
/* 1990 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 1992 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 1996 */       if (!isDetached())
/*      */       {
/* 1998 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2000 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsOpenCursorsAcrossCommit() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2008 */       return this.inner.supportsOpenCursorsAcrossCommit();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2012 */       if (isDetached())
/*      */       {
/* 2014 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2016 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2020 */       if (!isDetached())
/*      */       {
/* 2022 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2024 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsOpenCursorsAcrossRollback() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2032 */       return this.inner.supportsOpenCursorsAcrossRollback();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2036 */       if (isDetached())
/*      */       {
/* 2038 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2040 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2044 */       if (!isDetached())
/*      */       {
/* 2046 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2048 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsOpenStatementsAcrossCommit() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2056 */       return this.inner.supportsOpenStatementsAcrossCommit();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2060 */       if (isDetached())
/*      */       {
/* 2062 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2064 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2068 */       if (!isDetached())
/*      */       {
/* 2070 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2072 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsOpenStatementsAcrossRollback() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2080 */       return this.inner.supportsOpenStatementsAcrossRollback();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2084 */       if (isDetached())
/*      */       {
/* 2086 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2088 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2092 */       if (!isDetached())
/*      */       {
/* 2094 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2096 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxBinaryLiteralLength() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2104 */       return this.inner.getMaxBinaryLiteralLength();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2108 */       if (isDetached())
/*      */       {
/* 2110 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2112 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2116 */       if (!isDetached())
/*      */       {
/* 2118 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2120 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxCharLiteralLength() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2128 */       return this.inner.getMaxCharLiteralLength();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2132 */       if (isDetached())
/*      */       {
/* 2134 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2136 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2140 */       if (!isDetached())
/*      */       {
/* 2142 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2144 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxColumnNameLength() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2152 */       return this.inner.getMaxColumnNameLength();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2156 */       if (isDetached())
/*      */       {
/* 2158 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2160 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2164 */       if (!isDetached())
/*      */       {
/* 2166 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2168 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxColumnsInGroupBy() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2176 */       return this.inner.getMaxColumnsInGroupBy();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2180 */       if (isDetached())
/*      */       {
/* 2182 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2184 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2188 */       if (!isDetached())
/*      */       {
/* 2190 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2192 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxColumnsInIndex() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2200 */       return this.inner.getMaxColumnsInIndex();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2204 */       if (isDetached())
/*      */       {
/* 2206 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2208 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2212 */       if (!isDetached())
/*      */       {
/* 2214 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2216 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxColumnsInOrderBy() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2224 */       return this.inner.getMaxColumnsInOrderBy();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2228 */       if (isDetached())
/*      */       {
/* 2230 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2232 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2236 */       if (!isDetached())
/*      */       {
/* 2238 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2240 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxColumnsInSelect() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2248 */       return this.inner.getMaxColumnsInSelect();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2252 */       if (isDetached())
/*      */       {
/* 2254 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2256 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2260 */       if (!isDetached())
/*      */       {
/* 2262 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2264 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxColumnsInTable() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2272 */       return this.inner.getMaxColumnsInTable();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2276 */       if (isDetached())
/*      */       {
/* 2278 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2280 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2284 */       if (!isDetached())
/*      */       {
/* 2286 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2288 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxConnections() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2296 */       return this.inner.getMaxConnections();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2300 */       if (isDetached())
/*      */       {
/* 2302 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2304 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2308 */       if (!isDetached())
/*      */       {
/* 2310 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2312 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxCursorNameLength() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2320 */       return this.inner.getMaxCursorNameLength();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2324 */       if (isDetached())
/*      */       {
/* 2326 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2328 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2332 */       if (!isDetached())
/*      */       {
/* 2334 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2336 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxIndexLength() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2344 */       return this.inner.getMaxIndexLength();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2348 */       if (isDetached())
/*      */       {
/* 2350 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2352 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2356 */       if (!isDetached())
/*      */       {
/* 2358 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2360 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxSchemaNameLength() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2368 */       return this.inner.getMaxSchemaNameLength();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2372 */       if (isDetached())
/*      */       {
/* 2374 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
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
/*      */   public final int getMaxProcedureNameLength() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2392 */       return this.inner.getMaxProcedureNameLength();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2396 */       if (isDetached())
/*      */       {
/* 2398 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2400 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2404 */       if (!isDetached())
/*      */       {
/* 2406 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2408 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxCatalogNameLength() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2416 */       return this.inner.getMaxCatalogNameLength();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2420 */       if (isDetached())
/*      */       {
/* 2422 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2424 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2428 */       if (!isDetached())
/*      */       {
/* 2430 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2432 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxRowSize() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2440 */       return this.inner.getMaxRowSize();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2444 */       if (isDetached())
/*      */       {
/* 2446 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2448 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2452 */       if (!isDetached())
/*      */       {
/* 2454 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2456 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean doesMaxRowSizeIncludeBlobs() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2464 */       return this.inner.doesMaxRowSizeIncludeBlobs();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2468 */       if (isDetached())
/*      */       {
/* 2470 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2472 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2476 */       if (!isDetached())
/*      */       {
/* 2478 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2480 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxStatementLength() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2488 */       return this.inner.getMaxStatementLength();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2492 */       if (isDetached())
/*      */       {
/* 2494 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2496 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2500 */       if (!isDetached())
/*      */       {
/* 2502 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2504 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxStatements() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2512 */       return this.inner.getMaxStatements();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2516 */       if (isDetached())
/*      */       {
/* 2518 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2520 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2524 */       if (!isDetached())
/*      */       {
/* 2526 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2528 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxTableNameLength() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2536 */       return this.inner.getMaxTableNameLength();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2540 */       if (isDetached())
/*      */       {
/* 2542 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2544 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2548 */       if (!isDetached())
/*      */       {
/* 2550 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2552 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxTablesInSelect() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2560 */       return this.inner.getMaxTablesInSelect();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2564 */       if (isDetached())
/*      */       {
/* 2566 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2568 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2572 */       if (!isDetached())
/*      */       {
/* 2574 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2576 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getMaxUserNameLength() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2584 */       return this.inner.getMaxUserNameLength();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2588 */       if (isDetached())
/*      */       {
/* 2590 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2592 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2596 */       if (!isDetached())
/*      */       {
/* 2598 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2600 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getDefaultTransactionIsolation() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2608 */       return this.inner.getDefaultTransactionIsolation();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2612 */       if (isDetached())
/*      */       {
/* 2614 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2616 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2620 */       if (!isDetached())
/*      */       {
/* 2622 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2624 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsTransactions() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2632 */       return this.inner.supportsTransactions();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2636 */       if (isDetached())
/*      */       {
/* 2638 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2640 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2644 */       if (!isDetached())
/*      */       {
/* 2646 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2648 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsTransactionIsolationLevel(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2656 */       return this.inner.supportsTransactionIsolationLevel(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2660 */       if (isDetached())
/*      */       {
/* 2662 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2664 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2668 */       if (!isDetached())
/*      */       {
/* 2670 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2672 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2680 */       return this.inner.supportsDataDefinitionAndDataManipulationTransactions();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2684 */       if (isDetached())
/*      */       {
/* 2686 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
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
/*      */   public final boolean supportsDataManipulationTransactionsOnly() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2704 */       return this.inner.supportsDataManipulationTransactionsOnly();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2708 */       if (isDetached())
/*      */       {
/* 2710 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2712 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2716 */       if (!isDetached())
/*      */       {
/* 2718 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2720 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean dataDefinitionCausesTransactionCommit() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2728 */       return this.inner.dataDefinitionCausesTransactionCommit();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2732 */       if (isDetached())
/*      */       {
/* 2734 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2736 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2740 */       if (!isDetached())
/*      */       {
/* 2742 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2744 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean dataDefinitionIgnoredInTransactions() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2752 */       return this.inner.dataDefinitionIgnoredInTransactions();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2756 */       if (isDetached())
/*      */       {
/* 2758 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2760 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2764 */       if (!isDetached())
/*      */       {
/* 2766 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2768 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getProcedures(String a, String b, String c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2776 */       ResultSet innerResultSet = this.inner.getProcedures(a, b, c);
/* 2777 */       if (innerResultSet == null) return null;
/* 2778 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2782 */       if (isDetached())
/*      */       {
/* 2784 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2786 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2790 */       if (!isDetached())
/*      */       {
/* 2792 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2794 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getProcedureColumns(String a, String b, String c, String d) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2802 */       ResultSet innerResultSet = this.inner.getProcedureColumns(a, b, c, d);
/* 2803 */       if (innerResultSet == null) return null;
/* 2804 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2808 */       if (isDetached())
/*      */       {
/* 2810 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2812 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2816 */       if (!isDetached())
/*      */       {
/* 2818 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2820 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getTables(String a, String b, String c, String[] d) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2828 */       ResultSet innerResultSet = this.inner.getTables(a, b, c, d);
/* 2829 */       if (innerResultSet == null) return null;
/* 2830 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2834 */       if (isDetached())
/*      */       {
/* 2836 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2838 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2842 */       if (!isDetached())
/*      */       {
/* 2844 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2846 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getSchemas() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2854 */       ResultSet innerResultSet = this.inner.getSchemas();
/* 2855 */       if (innerResultSet == null) return null;
/* 2856 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2860 */       if (isDetached())
/*      */       {
/* 2862 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2864 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2868 */       if (!isDetached())
/*      */       {
/* 2870 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2872 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getCatalogs() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2880 */       ResultSet innerResultSet = this.inner.getCatalogs();
/* 2881 */       if (innerResultSet == null) return null;
/* 2882 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2886 */       if (isDetached())
/*      */       {
/* 2888 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2890 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2894 */       if (!isDetached())
/*      */       {
/* 2896 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2898 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getTableTypes() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2906 */       ResultSet innerResultSet = this.inner.getTableTypes();
/* 2907 */       if (innerResultSet == null) return null;
/* 2908 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2912 */       if (isDetached())
/*      */       {
/* 2914 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2916 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2920 */       if (!isDetached())
/*      */       {
/* 2922 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2924 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getColumnPrivileges(String a, String b, String c, String d) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2932 */       ResultSet innerResultSet = this.inner.getColumnPrivileges(a, b, c, d);
/* 2933 */       if (innerResultSet == null) return null;
/* 2934 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2938 */       if (isDetached())
/*      */       {
/* 2940 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2942 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2946 */       if (!isDetached())
/*      */       {
/* 2948 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2950 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getTablePrivileges(String a, String b, String c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2958 */       ResultSet innerResultSet = this.inner.getTablePrivileges(a, b, c);
/* 2959 */       if (innerResultSet == null) return null;
/* 2960 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2964 */       if (isDetached())
/*      */       {
/* 2966 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2968 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2972 */       if (!isDetached())
/*      */       {
/* 2974 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 2976 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getBestRowIdentifier(String a, String b, String c, int d, boolean e) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2984 */       ResultSet innerResultSet = this.inner.getBestRowIdentifier(a, b, c, d, e);
/* 2985 */       if (innerResultSet == null) return null;
/* 2986 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 2990 */       if (isDetached())
/*      */       {
/* 2992 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 2994 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 2998 */       if (!isDetached())
/*      */       {
/* 3000 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3002 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getVersionColumns(String a, String b, String c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3010 */       ResultSet innerResultSet = this.inner.getVersionColumns(a, b, c);
/* 3011 */       if (innerResultSet == null) return null;
/* 3012 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3016 */       if (isDetached())
/*      */       {
/* 3018 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3020 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3024 */       if (!isDetached())
/*      */       {
/* 3026 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3028 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getPrimaryKeys(String a, String b, String c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3036 */       ResultSet innerResultSet = this.inner.getPrimaryKeys(a, b, c);
/* 3037 */       if (innerResultSet == null) return null;
/* 3038 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3042 */       if (isDetached())
/*      */       {
/* 3044 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3046 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3050 */       if (!isDetached())
/*      */       {
/* 3052 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3054 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getImportedKeys(String a, String b, String c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3062 */       ResultSet innerResultSet = this.inner.getImportedKeys(a, b, c);
/* 3063 */       if (innerResultSet == null) return null;
/* 3064 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3068 */       if (isDetached())
/*      */       {
/* 3070 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3072 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3076 */       if (!isDetached())
/*      */       {
/* 3078 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3080 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getExportedKeys(String a, String b, String c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3088 */       ResultSet innerResultSet = this.inner.getExportedKeys(a, b, c);
/* 3089 */       if (innerResultSet == null) return null;
/* 3090 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3094 */       if (isDetached())
/*      */       {
/* 3096 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3098 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3102 */       if (!isDetached())
/*      */       {
/* 3104 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3106 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getCrossReference(String a, String b, String c, String d, String e, String f) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3114 */       ResultSet innerResultSet = this.inner.getCrossReference(a, b, c, d, e, f);
/* 3115 */       if (innerResultSet == null) return null;
/* 3116 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3120 */       if (isDetached())
/*      */       {
/* 3122 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3124 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3128 */       if (!isDetached())
/*      */       {
/* 3130 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3132 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getTypeInfo() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3140 */       ResultSet innerResultSet = this.inner.getTypeInfo();
/* 3141 */       if (innerResultSet == null) return null;
/* 3142 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3146 */       if (isDetached())
/*      */       {
/* 3148 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3150 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3154 */       if (!isDetached())
/*      */       {
/* 3156 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3158 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getIndexInfo(String a, String b, String c, boolean d, boolean e) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3166 */       ResultSet innerResultSet = this.inner.getIndexInfo(a, b, c, d, e);
/* 3167 */       if (innerResultSet == null) return null;
/* 3168 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3172 */       if (isDetached())
/*      */       {
/* 3174 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3176 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3180 */       if (!isDetached())
/*      */       {
/* 3182 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3184 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsResultSetType(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3192 */       return this.inner.supportsResultSetType(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3196 */       if (isDetached())
/*      */       {
/* 3198 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3200 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3204 */       if (!isDetached())
/*      */       {
/* 3206 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3208 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsResultSetConcurrency(int a, int b) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3216 */       return this.inner.supportsResultSetConcurrency(a, b);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3220 */       if (isDetached())
/*      */       {
/* 3222 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3224 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3228 */       if (!isDetached())
/*      */       {
/* 3230 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3232 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean ownUpdatesAreVisible(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3240 */       return this.inner.ownUpdatesAreVisible(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3244 */       if (isDetached())
/*      */       {
/* 3246 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3248 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3252 */       if (!isDetached())
/*      */       {
/* 3254 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3256 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean ownDeletesAreVisible(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3264 */       return this.inner.ownDeletesAreVisible(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3268 */       if (isDetached())
/*      */       {
/* 3270 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
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
/*      */   public final boolean ownInsertsAreVisible(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3288 */       return this.inner.ownInsertsAreVisible(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3292 */       if (isDetached())
/*      */       {
/* 3294 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3296 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3300 */       if (!isDetached())
/*      */       {
/* 3302 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3304 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean othersUpdatesAreVisible(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3312 */       return this.inner.othersUpdatesAreVisible(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3316 */       if (isDetached())
/*      */       {
/* 3318 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3320 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3324 */       if (!isDetached())
/*      */       {
/* 3326 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3328 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean othersDeletesAreVisible(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3336 */       return this.inner.othersDeletesAreVisible(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3340 */       if (isDetached())
/*      */       {
/* 3342 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3344 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3348 */       if (!isDetached())
/*      */       {
/* 3350 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3352 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean othersInsertsAreVisible(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3360 */       return this.inner.othersInsertsAreVisible(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3364 */       if (isDetached())
/*      */       {
/* 3366 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3368 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3372 */       if (!isDetached())
/*      */       {
/* 3374 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3376 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean updatesAreDetected(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3384 */       return this.inner.updatesAreDetected(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3388 */       if (isDetached())
/*      */       {
/* 3390 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3392 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3396 */       if (!isDetached())
/*      */       {
/* 3398 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3400 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean deletesAreDetected(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3408 */       return this.inner.deletesAreDetected(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3412 */       if (isDetached())
/*      */       {
/* 3414 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3416 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3420 */       if (!isDetached())
/*      */       {
/* 3422 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3424 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean insertsAreDetected(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3432 */       return this.inner.insertsAreDetected(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3436 */       if (isDetached())
/*      */       {
/* 3438 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3440 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3444 */       if (!isDetached())
/*      */       {
/* 3446 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3448 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsBatchUpdates() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3456 */       return this.inner.supportsBatchUpdates();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3460 */       if (isDetached())
/*      */       {
/* 3462 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3464 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3468 */       if (!isDetached())
/*      */       {
/* 3470 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3472 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getUDTs(String a, String b, String c, int[] d) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3480 */       ResultSet innerResultSet = this.inner.getUDTs(a, b, c, d);
/* 3481 */       if (innerResultSet == null) return null;
/* 3482 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3486 */       if (isDetached())
/*      */       {
/* 3488 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3490 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3494 */       if (!isDetached())
/*      */       {
/* 3496 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3498 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsSavepoints() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3506 */       return this.inner.supportsSavepoints();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3510 */       if (isDetached())
/*      */       {
/* 3512 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3514 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3518 */       if (!isDetached())
/*      */       {
/* 3520 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3522 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsNamedParameters() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3530 */       return this.inner.supportsNamedParameters();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3534 */       if (isDetached())
/*      */       {
/* 3536 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3538 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3542 */       if (!isDetached())
/*      */       {
/* 3544 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3546 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsMultipleOpenResults() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3554 */       return this.inner.supportsMultipleOpenResults();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3558 */       if (isDetached())
/*      */       {
/* 3560 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3562 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3566 */       if (!isDetached())
/*      */       {
/* 3568 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3570 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsGetGeneratedKeys() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3578 */       return this.inner.supportsGetGeneratedKeys();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3582 */       if (isDetached())
/*      */       {
/* 3584 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3586 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3590 */       if (!isDetached())
/*      */       {
/* 3592 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3594 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getSuperTypes(String a, String b, String c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3602 */       ResultSet innerResultSet = this.inner.getSuperTypes(a, b, c);
/* 3603 */       if (innerResultSet == null) return null;
/* 3604 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3608 */       if (isDetached())
/*      */       {
/* 3610 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3612 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3616 */       if (!isDetached())
/*      */       {
/* 3618 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3620 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getSuperTables(String a, String b, String c) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3628 */       ResultSet innerResultSet = this.inner.getSuperTables(a, b, c);
/* 3629 */       if (innerResultSet == null) return null;
/* 3630 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3634 */       if (isDetached())
/*      */       {
/* 3636 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3638 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3642 */       if (!isDetached())
/*      */       {
/* 3644 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3646 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsResultSetHoldability(int a) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3654 */       return this.inner.supportsResultSetHoldability(a);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3658 */       if (isDetached())
/*      */       {
/* 3660 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
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
/*      */   public final int getDatabaseMajorVersion() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3678 */       return this.inner.getDatabaseMajorVersion();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3682 */       if (isDetached())
/*      */       {
/* 3684 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3686 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3690 */       if (!isDetached())
/*      */       {
/* 3692 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3694 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getDatabaseMinorVersion() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3702 */       return this.inner.getDatabaseMinorVersion();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3706 */       if (isDetached())
/*      */       {
/* 3708 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3710 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3714 */       if (!isDetached())
/*      */       {
/* 3716 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3718 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getJDBCMajorVersion() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3726 */       return this.inner.getJDBCMajorVersion();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3730 */       if (isDetached())
/*      */       {
/* 3732 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
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
/*      */   public final int getJDBCMinorVersion() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3750 */       return this.inner.getJDBCMinorVersion();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3754 */       if (isDetached())
/*      */       {
/* 3756 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3758 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3762 */       if (!isDetached())
/*      */       {
/* 3764 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3766 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final int getSQLStateType() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3774 */       return this.inner.getSQLStateType();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3778 */       if (isDetached())
/*      */       {
/* 3780 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3782 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3786 */       if (!isDetached())
/*      */       {
/* 3788 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3790 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean locatorsUpdateCopy() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3798 */       return this.inner.locatorsUpdateCopy();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3802 */       if (isDetached())
/*      */       {
/* 3804 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3806 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3810 */       if (!isDetached())
/*      */       {
/* 3812 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3814 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean supportsStatementPooling() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3822 */       return this.inner.supportsStatementPooling();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3826 */       if (isDetached())
/*      */       {
/* 3828 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3830 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3834 */       if (!isDetached())
/*      */       {
/* 3836 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3838 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final String getURL() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3846 */       return this.inner.getURL();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3850 */       if (isDetached())
/*      */       {
/* 3852 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3854 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3858 */       if (!isDetached())
/*      */       {
/* 3860 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3862 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final boolean isReadOnly() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3870 */       return this.inner.isReadOnly();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3874 */       if (isDetached())
/*      */       {
/* 3876 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3878 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3882 */       if (!isDetached())
/*      */       {
/* 3884 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3886 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getAttributes(String a, String b, String c, String d) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3894 */       ResultSet innerResultSet = this.inner.getAttributes(a, b, c, d);
/* 3895 */       if (innerResultSet == null) return null;
/* 3896 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3900 */       if (isDetached())
/*      */       {
/* 3902 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3904 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3908 */       if (!isDetached())
/*      */       {
/* 3910 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3912 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final Connection getConnection() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3920 */       return this.proxyCon;
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3924 */       if (isDetached())
/*      */       {
/* 3926 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3928 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3932 */       if (!isDetached())
/*      */       {
/* 3934 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3936 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final String getUserName() throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3944 */       return this.inner.getUserName();
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3948 */       if (isDetached())
/*      */       {
/* 3950 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3952 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3956 */       if (!isDetached())
/*      */       {
/* 3958 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3960 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/*      */   public final ResultSet getColumns(String a, String b, String c, String d) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3968 */       ResultSet innerResultSet = this.inner.getColumns(a, b, c, d);
/* 3969 */       if (innerResultSet == null) return null;
/* 3970 */       return new NewProxyResultSet(innerResultSet, this.parentPooledConnection, this.inner, this);
/*      */     }
/*      */     catch (NullPointerException exc)
/*      */     {
/* 3974 */       if (isDetached())
/*      */       {
/* 3976 */         throw SqlUtils.toSQLException("You can't operate on a closed DatabaseMetaData!!!", exc);
/*      */       }
/* 3978 */       throw exc;
/*      */     }
/*      */     catch (Exception exc)
/*      */     {
/* 3982 */       if (!isDetached())
/*      */       {
/* 3984 */         throw this.parentPooledConnection.handleThrowable(exc);
/*      */       }
/* 3986 */       throw SqlUtils.toSQLException(exc);
/*      */     }
/*      */   }
/*      */   
/* 3990 */   private static final MLogger logger = MLog.getLogger("com.mchange.v2.c3p0.impl.NewProxyDatabaseMetaData");
/*      */   
/*      */   volatile NewPooledConnection parentPooledConnection;
/*      */   
/* 3994 */   ConnectionEventListener cel = new ConnectionEventListener()
/*      */   {
/*      */     public void connectionErrorOccurred(ConnectionEvent evt) {}
/*      */     
/*      */ 
/*      */ 
/* 4000 */     public void connectionClosed(ConnectionEvent evt) { NewProxyDatabaseMetaData.this.detach(); }
/*      */   };
/*      */   NewProxyConnection proxyCon;
/*      */   
/*      */   void attach(NewPooledConnection parentPooledConnection) {
/* 4005 */     this.parentPooledConnection = parentPooledConnection;
/* 4006 */     parentPooledConnection.addConnectionEventListener(this.cel);
/*      */   }
/*      */   
/*      */   private void detach()
/*      */   {
/* 4011 */     this.parentPooledConnection.removeConnectionEventListener(this.cel);
/* 4012 */     this.parentPooledConnection = null;
/*      */   }
/*      */   
/*      */   NewProxyDatabaseMetaData(DatabaseMetaData inner, NewPooledConnection parentPooledConnection)
/*      */   {
/* 4017 */     this(inner);
/* 4018 */     attach(parentPooledConnection);
/*      */   }
/*      */   
/*      */   boolean isDetached() {
/* 4022 */     return this.parentPooledConnection == null;
/*      */   }
/*      */   
/*      */ 
/*      */   NewProxyDatabaseMetaData(DatabaseMetaData inner, NewPooledConnection parentPooledConnection, NewProxyConnection proxyCon)
/*      */   {
/* 4028 */     this(inner, parentPooledConnection);
/* 4029 */     this.proxyCon = proxyCon;
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\NewProxyDatabaseMetaData.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */