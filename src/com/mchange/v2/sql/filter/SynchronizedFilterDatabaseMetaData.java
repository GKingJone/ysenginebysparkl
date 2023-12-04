/*     */ package com.mchange.v2.sql.filter;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class SynchronizedFilterDatabaseMetaData
/*     */   implements DatabaseMetaData
/*     */ {
/*     */   protected DatabaseMetaData inner;
/*     */   
/*     */   public SynchronizedFilterDatabaseMetaData(DatabaseMetaData inner)
/*     */   {
/*  37 */     this.inner = inner;
/*     */   }
/*     */   
/*     */   public SynchronizedFilterDatabaseMetaData() {}
/*     */   
/*     */   public synchronized void setInner(DatabaseMetaData inner) {
/*  43 */     this.inner = inner;
/*     */   }
/*     */   
/*  46 */   public synchronized DatabaseMetaData getInner() { return this.inner; }
/*     */   
/*     */   public synchronized boolean allProceduresAreCallable() throws SQLException {
/*  49 */     return this.inner.allProceduresAreCallable();
/*     */   }
/*     */   
/*  52 */   public synchronized boolean allTablesAreSelectable() throws SQLException { return this.inner.allTablesAreSelectable(); }
/*     */   
/*     */   public synchronized boolean nullsAreSortedHigh() throws SQLException {
/*  55 */     return this.inner.nullsAreSortedHigh();
/*     */   }
/*     */   
/*  58 */   public synchronized boolean nullsAreSortedLow() throws SQLException { return this.inner.nullsAreSortedLow(); }
/*     */   
/*     */   public synchronized boolean nullsAreSortedAtStart() throws SQLException {
/*  61 */     return this.inner.nullsAreSortedAtStart();
/*     */   }
/*     */   
/*  64 */   public synchronized boolean nullsAreSortedAtEnd() throws SQLException { return this.inner.nullsAreSortedAtEnd(); }
/*     */   
/*     */   public synchronized String getDatabaseProductName() throws SQLException {
/*  67 */     return this.inner.getDatabaseProductName();
/*     */   }
/*     */   
/*  70 */   public synchronized String getDatabaseProductVersion() throws SQLException { return this.inner.getDatabaseProductVersion(); }
/*     */   
/*     */   public synchronized String getDriverName() throws SQLException {
/*  73 */     return this.inner.getDriverName();
/*     */   }
/*     */   
/*  76 */   public synchronized String getDriverVersion() throws SQLException { return this.inner.getDriverVersion(); }
/*     */   
/*     */   public synchronized int getDriverMajorVersion() {
/*  79 */     return this.inner.getDriverMajorVersion();
/*     */   }
/*     */   
/*  82 */   public synchronized int getDriverMinorVersion() { return this.inner.getDriverMinorVersion(); }
/*     */   
/*     */   public synchronized boolean usesLocalFiles() throws SQLException {
/*  85 */     return this.inner.usesLocalFiles();
/*     */   }
/*     */   
/*  88 */   public synchronized boolean usesLocalFilePerTable() throws SQLException { return this.inner.usesLocalFilePerTable(); }
/*     */   
/*     */   public synchronized boolean supportsMixedCaseIdentifiers() throws SQLException {
/*  91 */     return this.inner.supportsMixedCaseIdentifiers();
/*     */   }
/*     */   
/*  94 */   public synchronized boolean storesUpperCaseIdentifiers() throws SQLException { return this.inner.storesUpperCaseIdentifiers(); }
/*     */   
/*     */   public synchronized boolean storesLowerCaseIdentifiers() throws SQLException {
/*  97 */     return this.inner.storesLowerCaseIdentifiers();
/*     */   }
/*     */   
/* 100 */   public synchronized boolean storesMixedCaseIdentifiers() throws SQLException { return this.inner.storesMixedCaseIdentifiers(); }
/*     */   
/*     */   public synchronized boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
/* 103 */     return this.inner.supportsMixedCaseQuotedIdentifiers();
/*     */   }
/*     */   
/* 106 */   public synchronized boolean storesUpperCaseQuotedIdentifiers() throws SQLException { return this.inner.storesUpperCaseQuotedIdentifiers(); }
/*     */   
/*     */   public synchronized boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
/* 109 */     return this.inner.storesLowerCaseQuotedIdentifiers();
/*     */   }
/*     */   
/* 112 */   public synchronized boolean storesMixedCaseQuotedIdentifiers() throws SQLException { return this.inner.storesMixedCaseQuotedIdentifiers(); }
/*     */   
/*     */   public synchronized String getIdentifierQuoteString() throws SQLException {
/* 115 */     return this.inner.getIdentifierQuoteString();
/*     */   }
/*     */   
/* 118 */   public synchronized String getSQLKeywords() throws SQLException { return this.inner.getSQLKeywords(); }
/*     */   
/*     */   public synchronized String getNumericFunctions() throws SQLException {
/* 121 */     return this.inner.getNumericFunctions();
/*     */   }
/*     */   
/* 124 */   public synchronized String getStringFunctions() throws SQLException { return this.inner.getStringFunctions(); }
/*     */   
/*     */   public synchronized String getSystemFunctions() throws SQLException {
/* 127 */     return this.inner.getSystemFunctions();
/*     */   }
/*     */   
/* 130 */   public synchronized String getTimeDateFunctions() throws SQLException { return this.inner.getTimeDateFunctions(); }
/*     */   
/*     */   public synchronized String getSearchStringEscape() throws SQLException {
/* 133 */     return this.inner.getSearchStringEscape();
/*     */   }
/*     */   
/* 136 */   public synchronized String getExtraNameCharacters() throws SQLException { return this.inner.getExtraNameCharacters(); }
/*     */   
/*     */   public synchronized boolean supportsAlterTableWithAddColumn() throws SQLException {
/* 139 */     return this.inner.supportsAlterTableWithAddColumn();
/*     */   }
/*     */   
/* 142 */   public synchronized boolean supportsAlterTableWithDropColumn() throws SQLException { return this.inner.supportsAlterTableWithDropColumn(); }
/*     */   
/*     */   public synchronized boolean supportsColumnAliasing() throws SQLException {
/* 145 */     return this.inner.supportsColumnAliasing();
/*     */   }
/*     */   
/* 148 */   public synchronized boolean nullPlusNonNullIsNull() throws SQLException { return this.inner.nullPlusNonNullIsNull(); }
/*     */   
/*     */   public synchronized boolean supportsConvert() throws SQLException {
/* 151 */     return this.inner.supportsConvert();
/*     */   }
/*     */   
/* 154 */   public synchronized boolean supportsConvert(int a, int b) throws SQLException { return this.inner.supportsConvert(a, b); }
/*     */   
/*     */   public synchronized boolean supportsTableCorrelationNames() throws SQLException {
/* 157 */     return this.inner.supportsTableCorrelationNames();
/*     */   }
/*     */   
/* 160 */   public synchronized boolean supportsDifferentTableCorrelationNames() throws SQLException { return this.inner.supportsDifferentTableCorrelationNames(); }
/*     */   
/*     */   public synchronized boolean supportsExpressionsInOrderBy() throws SQLException {
/* 163 */     return this.inner.supportsExpressionsInOrderBy();
/*     */   }
/*     */   
/* 166 */   public synchronized boolean supportsOrderByUnrelated() throws SQLException { return this.inner.supportsOrderByUnrelated(); }
/*     */   
/*     */   public synchronized boolean supportsGroupBy() throws SQLException {
/* 169 */     return this.inner.supportsGroupBy();
/*     */   }
/*     */   
/* 172 */   public synchronized boolean supportsGroupByUnrelated() throws SQLException { return this.inner.supportsGroupByUnrelated(); }
/*     */   
/*     */   public synchronized boolean supportsGroupByBeyondSelect() throws SQLException {
/* 175 */     return this.inner.supportsGroupByBeyondSelect();
/*     */   }
/*     */   
/* 178 */   public synchronized boolean supportsLikeEscapeClause() throws SQLException { return this.inner.supportsLikeEscapeClause(); }
/*     */   
/*     */   public synchronized boolean supportsMultipleResultSets() throws SQLException {
/* 181 */     return this.inner.supportsMultipleResultSets();
/*     */   }
/*     */   
/* 184 */   public synchronized boolean supportsMultipleTransactions() throws SQLException { return this.inner.supportsMultipleTransactions(); }
/*     */   
/*     */   public synchronized boolean supportsNonNullableColumns() throws SQLException {
/* 187 */     return this.inner.supportsNonNullableColumns();
/*     */   }
/*     */   
/* 190 */   public synchronized boolean supportsMinimumSQLGrammar() throws SQLException { return this.inner.supportsMinimumSQLGrammar(); }
/*     */   
/*     */   public synchronized boolean supportsCoreSQLGrammar() throws SQLException {
/* 193 */     return this.inner.supportsCoreSQLGrammar();
/*     */   }
/*     */   
/* 196 */   public synchronized boolean supportsExtendedSQLGrammar() throws SQLException { return this.inner.supportsExtendedSQLGrammar(); }
/*     */   
/*     */   public synchronized boolean supportsANSI92EntryLevelSQL() throws SQLException {
/* 199 */     return this.inner.supportsANSI92EntryLevelSQL();
/*     */   }
/*     */   
/* 202 */   public synchronized boolean supportsANSI92IntermediateSQL() throws SQLException { return this.inner.supportsANSI92IntermediateSQL(); }
/*     */   
/*     */   public synchronized boolean supportsANSI92FullSQL() throws SQLException {
/* 205 */     return this.inner.supportsANSI92FullSQL();
/*     */   }
/*     */   
/* 208 */   public synchronized boolean supportsIntegrityEnhancementFacility() throws SQLException { return this.inner.supportsIntegrityEnhancementFacility(); }
/*     */   
/*     */   public synchronized boolean supportsOuterJoins() throws SQLException {
/* 211 */     return this.inner.supportsOuterJoins();
/*     */   }
/*     */   
/* 214 */   public synchronized boolean supportsFullOuterJoins() throws SQLException { return this.inner.supportsFullOuterJoins(); }
/*     */   
/*     */   public synchronized boolean supportsLimitedOuterJoins() throws SQLException {
/* 217 */     return this.inner.supportsLimitedOuterJoins();
/*     */   }
/*     */   
/* 220 */   public synchronized String getSchemaTerm() throws SQLException { return this.inner.getSchemaTerm(); }
/*     */   
/*     */   public synchronized String getProcedureTerm() throws SQLException {
/* 223 */     return this.inner.getProcedureTerm();
/*     */   }
/*     */   
/* 226 */   public synchronized String getCatalogTerm() throws SQLException { return this.inner.getCatalogTerm(); }
/*     */   
/*     */   public synchronized boolean isCatalogAtStart() throws SQLException {
/* 229 */     return this.inner.isCatalogAtStart();
/*     */   }
/*     */   
/* 232 */   public synchronized String getCatalogSeparator() throws SQLException { return this.inner.getCatalogSeparator(); }
/*     */   
/*     */   public synchronized boolean supportsSchemasInDataManipulation() throws SQLException {
/* 235 */     return this.inner.supportsSchemasInDataManipulation();
/*     */   }
/*     */   
/* 238 */   public synchronized boolean supportsSchemasInProcedureCalls() throws SQLException { return this.inner.supportsSchemasInProcedureCalls(); }
/*     */   
/*     */   public synchronized boolean supportsSchemasInTableDefinitions() throws SQLException {
/* 241 */     return this.inner.supportsSchemasInTableDefinitions();
/*     */   }
/*     */   
/* 244 */   public synchronized boolean supportsSchemasInIndexDefinitions() throws SQLException { return this.inner.supportsSchemasInIndexDefinitions(); }
/*     */   
/*     */   public synchronized boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
/* 247 */     return this.inner.supportsSchemasInPrivilegeDefinitions();
/*     */   }
/*     */   
/* 250 */   public synchronized boolean supportsCatalogsInDataManipulation() throws SQLException { return this.inner.supportsCatalogsInDataManipulation(); }
/*     */   
/*     */   public synchronized boolean supportsCatalogsInProcedureCalls() throws SQLException {
/* 253 */     return this.inner.supportsCatalogsInProcedureCalls();
/*     */   }
/*     */   
/* 256 */   public synchronized boolean supportsCatalogsInTableDefinitions() throws SQLException { return this.inner.supportsCatalogsInTableDefinitions(); }
/*     */   
/*     */   public synchronized boolean supportsCatalogsInIndexDefinitions() throws SQLException {
/* 259 */     return this.inner.supportsCatalogsInIndexDefinitions();
/*     */   }
/*     */   
/* 262 */   public synchronized boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException { return this.inner.supportsCatalogsInPrivilegeDefinitions(); }
/*     */   
/*     */   public synchronized boolean supportsPositionedDelete() throws SQLException {
/* 265 */     return this.inner.supportsPositionedDelete();
/*     */   }
/*     */   
/* 268 */   public synchronized boolean supportsPositionedUpdate() throws SQLException { return this.inner.supportsPositionedUpdate(); }
/*     */   
/*     */   public synchronized boolean supportsSelectForUpdate() throws SQLException {
/* 271 */     return this.inner.supportsSelectForUpdate();
/*     */   }
/*     */   
/* 274 */   public synchronized boolean supportsStoredProcedures() throws SQLException { return this.inner.supportsStoredProcedures(); }
/*     */   
/*     */   public synchronized boolean supportsSubqueriesInComparisons() throws SQLException {
/* 277 */     return this.inner.supportsSubqueriesInComparisons();
/*     */   }
/*     */   
/* 280 */   public synchronized boolean supportsSubqueriesInExists() throws SQLException { return this.inner.supportsSubqueriesInExists(); }
/*     */   
/*     */   public synchronized boolean supportsSubqueriesInIns() throws SQLException {
/* 283 */     return this.inner.supportsSubqueriesInIns();
/*     */   }
/*     */   
/* 286 */   public synchronized boolean supportsSubqueriesInQuantifieds() throws SQLException { return this.inner.supportsSubqueriesInQuantifieds(); }
/*     */   
/*     */   public synchronized boolean supportsCorrelatedSubqueries() throws SQLException {
/* 289 */     return this.inner.supportsCorrelatedSubqueries();
/*     */   }
/*     */   
/* 292 */   public synchronized boolean supportsUnion() throws SQLException { return this.inner.supportsUnion(); }
/*     */   
/*     */   public synchronized boolean supportsUnionAll() throws SQLException {
/* 295 */     return this.inner.supportsUnionAll();
/*     */   }
/*     */   
/* 298 */   public synchronized boolean supportsOpenCursorsAcrossCommit() throws SQLException { return this.inner.supportsOpenCursorsAcrossCommit(); }
/*     */   
/*     */   public synchronized boolean supportsOpenCursorsAcrossRollback() throws SQLException {
/* 301 */     return this.inner.supportsOpenCursorsAcrossRollback();
/*     */   }
/*     */   
/* 304 */   public synchronized boolean supportsOpenStatementsAcrossCommit() throws SQLException { return this.inner.supportsOpenStatementsAcrossCommit(); }
/*     */   
/*     */   public synchronized boolean supportsOpenStatementsAcrossRollback() throws SQLException {
/* 307 */     return this.inner.supportsOpenStatementsAcrossRollback();
/*     */   }
/*     */   
/* 310 */   public synchronized int getMaxBinaryLiteralLength() throws SQLException { return this.inner.getMaxBinaryLiteralLength(); }
/*     */   
/*     */   public synchronized int getMaxCharLiteralLength() throws SQLException {
/* 313 */     return this.inner.getMaxCharLiteralLength();
/*     */   }
/*     */   
/* 316 */   public synchronized int getMaxColumnNameLength() throws SQLException { return this.inner.getMaxColumnNameLength(); }
/*     */   
/*     */   public synchronized int getMaxColumnsInGroupBy() throws SQLException {
/* 319 */     return this.inner.getMaxColumnsInGroupBy();
/*     */   }
/*     */   
/* 322 */   public synchronized int getMaxColumnsInIndex() throws SQLException { return this.inner.getMaxColumnsInIndex(); }
/*     */   
/*     */   public synchronized int getMaxColumnsInOrderBy() throws SQLException {
/* 325 */     return this.inner.getMaxColumnsInOrderBy();
/*     */   }
/*     */   
/* 328 */   public synchronized int getMaxColumnsInSelect() throws SQLException { return this.inner.getMaxColumnsInSelect(); }
/*     */   
/*     */   public synchronized int getMaxColumnsInTable() throws SQLException {
/* 331 */     return this.inner.getMaxColumnsInTable();
/*     */   }
/*     */   
/* 334 */   public synchronized int getMaxConnections() throws SQLException { return this.inner.getMaxConnections(); }
/*     */   
/*     */   public synchronized int getMaxCursorNameLength() throws SQLException {
/* 337 */     return this.inner.getMaxCursorNameLength();
/*     */   }
/*     */   
/* 340 */   public synchronized int getMaxIndexLength() throws SQLException { return this.inner.getMaxIndexLength(); }
/*     */   
/*     */   public synchronized int getMaxSchemaNameLength() throws SQLException {
/* 343 */     return this.inner.getMaxSchemaNameLength();
/*     */   }
/*     */   
/* 346 */   public synchronized int getMaxProcedureNameLength() throws SQLException { return this.inner.getMaxProcedureNameLength(); }
/*     */   
/*     */   public synchronized int getMaxCatalogNameLength() throws SQLException {
/* 349 */     return this.inner.getMaxCatalogNameLength();
/*     */   }
/*     */   
/* 352 */   public synchronized int getMaxRowSize() throws SQLException { return this.inner.getMaxRowSize(); }
/*     */   
/*     */   public synchronized boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
/* 355 */     return this.inner.doesMaxRowSizeIncludeBlobs();
/*     */   }
/*     */   
/* 358 */   public synchronized int getMaxStatementLength() throws SQLException { return this.inner.getMaxStatementLength(); }
/*     */   
/*     */   public synchronized int getMaxStatements() throws SQLException {
/* 361 */     return this.inner.getMaxStatements();
/*     */   }
/*     */   
/* 364 */   public synchronized int getMaxTableNameLength() throws SQLException { return this.inner.getMaxTableNameLength(); }
/*     */   
/*     */   public synchronized int getMaxTablesInSelect() throws SQLException {
/* 367 */     return this.inner.getMaxTablesInSelect();
/*     */   }
/*     */   
/* 370 */   public synchronized int getMaxUserNameLength() throws SQLException { return this.inner.getMaxUserNameLength(); }
/*     */   
/*     */   public synchronized int getDefaultTransactionIsolation() throws SQLException {
/* 373 */     return this.inner.getDefaultTransactionIsolation();
/*     */   }
/*     */   
/* 376 */   public synchronized boolean supportsTransactions() throws SQLException { return this.inner.supportsTransactions(); }
/*     */   
/*     */   public synchronized boolean supportsTransactionIsolationLevel(int a) throws SQLException {
/* 379 */     return this.inner.supportsTransactionIsolationLevel(a);
/*     */   }
/*     */   
/* 382 */   public synchronized boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException { return this.inner.supportsDataDefinitionAndDataManipulationTransactions(); }
/*     */   
/*     */   public synchronized boolean supportsDataManipulationTransactionsOnly() throws SQLException {
/* 385 */     return this.inner.supportsDataManipulationTransactionsOnly();
/*     */   }
/*     */   
/* 388 */   public synchronized boolean dataDefinitionCausesTransactionCommit() throws SQLException { return this.inner.dataDefinitionCausesTransactionCommit(); }
/*     */   
/*     */   public synchronized boolean dataDefinitionIgnoredInTransactions() throws SQLException {
/* 391 */     return this.inner.dataDefinitionIgnoredInTransactions();
/*     */   }
/*     */   
/* 394 */   public synchronized ResultSet getProcedures(String a, String b, String c) throws SQLException { return this.inner.getProcedures(a, b, c); }
/*     */   
/*     */   public synchronized ResultSet getProcedureColumns(String a, String b, String c, String d) throws SQLException {
/* 397 */     return this.inner.getProcedureColumns(a, b, c, d);
/*     */   }
/*     */   
/* 400 */   public synchronized ResultSet getTables(String a, String b, String c, String[] d) throws SQLException { return this.inner.getTables(a, b, c, d); }
/*     */   
/*     */   public synchronized ResultSet getSchemas() throws SQLException {
/* 403 */     return this.inner.getSchemas();
/*     */   }
/*     */   
/* 406 */   public synchronized ResultSet getCatalogs() throws SQLException { return this.inner.getCatalogs(); }
/*     */   
/*     */   public synchronized ResultSet getTableTypes() throws SQLException {
/* 409 */     return this.inner.getTableTypes();
/*     */   }
/*     */   
/* 412 */   public synchronized ResultSet getColumnPrivileges(String a, String b, String c, String d) throws SQLException { return this.inner.getColumnPrivileges(a, b, c, d); }
/*     */   
/*     */   public synchronized ResultSet getTablePrivileges(String a, String b, String c) throws SQLException {
/* 415 */     return this.inner.getTablePrivileges(a, b, c);
/*     */   }
/*     */   
/* 418 */   public synchronized ResultSet getBestRowIdentifier(String a, String b, String c, int d, boolean e) throws SQLException { return this.inner.getBestRowIdentifier(a, b, c, d, e); }
/*     */   
/*     */   public synchronized ResultSet getVersionColumns(String a, String b, String c) throws SQLException {
/* 421 */     return this.inner.getVersionColumns(a, b, c);
/*     */   }
/*     */   
/* 424 */   public synchronized ResultSet getPrimaryKeys(String a, String b, String c) throws SQLException { return this.inner.getPrimaryKeys(a, b, c); }
/*     */   
/*     */   public synchronized ResultSet getImportedKeys(String a, String b, String c) throws SQLException {
/* 427 */     return this.inner.getImportedKeys(a, b, c);
/*     */   }
/*     */   
/* 430 */   public synchronized ResultSet getExportedKeys(String a, String b, String c) throws SQLException { return this.inner.getExportedKeys(a, b, c); }
/*     */   
/*     */   public synchronized ResultSet getCrossReference(String a, String b, String c, String d, String e, String f) throws SQLException {
/* 433 */     return this.inner.getCrossReference(a, b, c, d, e, f);
/*     */   }
/*     */   
/* 436 */   public synchronized ResultSet getTypeInfo() throws SQLException { return this.inner.getTypeInfo(); }
/*     */   
/*     */   public synchronized ResultSet getIndexInfo(String a, String b, String c, boolean d, boolean e) throws SQLException {
/* 439 */     return this.inner.getIndexInfo(a, b, c, d, e);
/*     */   }
/*     */   
/* 442 */   public synchronized boolean supportsResultSetType(int a) throws SQLException { return this.inner.supportsResultSetType(a); }
/*     */   
/*     */   public synchronized boolean supportsResultSetConcurrency(int a, int b) throws SQLException {
/* 445 */     return this.inner.supportsResultSetConcurrency(a, b);
/*     */   }
/*     */   
/* 448 */   public synchronized boolean ownUpdatesAreVisible(int a) throws SQLException { return this.inner.ownUpdatesAreVisible(a); }
/*     */   
/*     */   public synchronized boolean ownDeletesAreVisible(int a) throws SQLException {
/* 451 */     return this.inner.ownDeletesAreVisible(a);
/*     */   }
/*     */   
/* 454 */   public synchronized boolean ownInsertsAreVisible(int a) throws SQLException { return this.inner.ownInsertsAreVisible(a); }
/*     */   
/*     */   public synchronized boolean othersUpdatesAreVisible(int a) throws SQLException {
/* 457 */     return this.inner.othersUpdatesAreVisible(a);
/*     */   }
/*     */   
/* 460 */   public synchronized boolean othersDeletesAreVisible(int a) throws SQLException { return this.inner.othersDeletesAreVisible(a); }
/*     */   
/*     */   public synchronized boolean othersInsertsAreVisible(int a) throws SQLException {
/* 463 */     return this.inner.othersInsertsAreVisible(a);
/*     */   }
/*     */   
/* 466 */   public synchronized boolean updatesAreDetected(int a) throws SQLException { return this.inner.updatesAreDetected(a); }
/*     */   
/*     */   public synchronized boolean deletesAreDetected(int a) throws SQLException {
/* 469 */     return this.inner.deletesAreDetected(a);
/*     */   }
/*     */   
/* 472 */   public synchronized boolean insertsAreDetected(int a) throws SQLException { return this.inner.insertsAreDetected(a); }
/*     */   
/*     */   public synchronized boolean supportsBatchUpdates() throws SQLException {
/* 475 */     return this.inner.supportsBatchUpdates();
/*     */   }
/*     */   
/* 478 */   public synchronized ResultSet getUDTs(String a, String b, String c, int[] d) throws SQLException { return this.inner.getUDTs(a, b, c, d); }
/*     */   
/*     */   public synchronized boolean supportsSavepoints() throws SQLException {
/* 481 */     return this.inner.supportsSavepoints();
/*     */   }
/*     */   
/* 484 */   public synchronized boolean supportsNamedParameters() throws SQLException { return this.inner.supportsNamedParameters(); }
/*     */   
/*     */   public synchronized boolean supportsMultipleOpenResults() throws SQLException {
/* 487 */     return this.inner.supportsMultipleOpenResults();
/*     */   }
/*     */   
/* 490 */   public synchronized boolean supportsGetGeneratedKeys() throws SQLException { return this.inner.supportsGetGeneratedKeys(); }
/*     */   
/*     */   public synchronized ResultSet getSuperTypes(String a, String b, String c) throws SQLException {
/* 493 */     return this.inner.getSuperTypes(a, b, c);
/*     */   }
/*     */   
/* 496 */   public synchronized ResultSet getSuperTables(String a, String b, String c) throws SQLException { return this.inner.getSuperTables(a, b, c); }
/*     */   
/*     */   public synchronized boolean supportsResultSetHoldability(int a) throws SQLException {
/* 499 */     return this.inner.supportsResultSetHoldability(a);
/*     */   }
/*     */   
/* 502 */   public synchronized int getResultSetHoldability() throws SQLException { return this.inner.getResultSetHoldability(); }
/*     */   
/*     */   public synchronized int getDatabaseMajorVersion() throws SQLException {
/* 505 */     return this.inner.getDatabaseMajorVersion();
/*     */   }
/*     */   
/* 508 */   public synchronized int getDatabaseMinorVersion() throws SQLException { return this.inner.getDatabaseMinorVersion(); }
/*     */   
/*     */   public synchronized int getJDBCMajorVersion() throws SQLException {
/* 511 */     return this.inner.getJDBCMajorVersion();
/*     */   }
/*     */   
/* 514 */   public synchronized int getJDBCMinorVersion() throws SQLException { return this.inner.getJDBCMinorVersion(); }
/*     */   
/*     */   public synchronized int getSQLStateType() throws SQLException {
/* 517 */     return this.inner.getSQLStateType();
/*     */   }
/*     */   
/* 520 */   public synchronized boolean locatorsUpdateCopy() throws SQLException { return this.inner.locatorsUpdateCopy(); }
/*     */   
/*     */   public synchronized boolean supportsStatementPooling() throws SQLException {
/* 523 */     return this.inner.supportsStatementPooling();
/*     */   }
/*     */   
/* 526 */   public synchronized String getURL() throws SQLException { return this.inner.getURL(); }
/*     */   
/*     */   public synchronized boolean isReadOnly() throws SQLException {
/* 529 */     return this.inner.isReadOnly();
/*     */   }
/*     */   
/* 532 */   public synchronized ResultSet getAttributes(String a, String b, String c, String d) throws SQLException { return this.inner.getAttributes(a, b, c, d); }
/*     */   
/*     */   public synchronized Connection getConnection() throws SQLException {
/* 535 */     return this.inner.getConnection();
/*     */   }
/*     */   
/* 538 */   public synchronized ResultSet getColumns(String a, String b, String c, String d) throws SQLException { return this.inner.getColumns(a, b, c, d); }
/*     */   
/*     */   public synchronized String getUserName() throws SQLException {
/* 541 */     return this.inner.getUserName();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\sql\filter\SynchronizedFilterDatabaseMetaData.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */