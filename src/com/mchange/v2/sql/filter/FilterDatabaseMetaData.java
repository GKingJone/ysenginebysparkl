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
/*     */ public abstract class FilterDatabaseMetaData
/*     */   implements DatabaseMetaData
/*     */ {
/*     */   protected DatabaseMetaData inner;
/*     */   
/*     */   public FilterDatabaseMetaData(DatabaseMetaData inner)
/*     */   {
/*  37 */     this.inner = inner;
/*     */   }
/*     */   
/*     */   public FilterDatabaseMetaData() {}
/*     */   
/*     */   public void setInner(DatabaseMetaData inner) {
/*  43 */     this.inner = inner;
/*     */   }
/*     */   
/*  46 */   public DatabaseMetaData getInner() { return this.inner; }
/*     */   
/*     */   public boolean allProceduresAreCallable() throws SQLException {
/*  49 */     return this.inner.allProceduresAreCallable();
/*     */   }
/*     */   
/*  52 */   public boolean allTablesAreSelectable() throws SQLException { return this.inner.allTablesAreSelectable(); }
/*     */   
/*     */   public boolean nullsAreSortedHigh() throws SQLException {
/*  55 */     return this.inner.nullsAreSortedHigh();
/*     */   }
/*     */   
/*  58 */   public boolean nullsAreSortedLow() throws SQLException { return this.inner.nullsAreSortedLow(); }
/*     */   
/*     */   public boolean nullsAreSortedAtStart() throws SQLException {
/*  61 */     return this.inner.nullsAreSortedAtStart();
/*     */   }
/*     */   
/*  64 */   public boolean nullsAreSortedAtEnd() throws SQLException { return this.inner.nullsAreSortedAtEnd(); }
/*     */   
/*     */   public String getDatabaseProductName() throws SQLException {
/*  67 */     return this.inner.getDatabaseProductName();
/*     */   }
/*     */   
/*  70 */   public String getDatabaseProductVersion() throws SQLException { return this.inner.getDatabaseProductVersion(); }
/*     */   
/*     */   public String getDriverName() throws SQLException {
/*  73 */     return this.inner.getDriverName();
/*     */   }
/*     */   
/*  76 */   public String getDriverVersion() throws SQLException { return this.inner.getDriverVersion(); }
/*     */   
/*     */   public int getDriverMajorVersion() {
/*  79 */     return this.inner.getDriverMajorVersion();
/*     */   }
/*     */   
/*  82 */   public int getDriverMinorVersion() { return this.inner.getDriverMinorVersion(); }
/*     */   
/*     */   public boolean usesLocalFiles() throws SQLException {
/*  85 */     return this.inner.usesLocalFiles();
/*     */   }
/*     */   
/*  88 */   public boolean usesLocalFilePerTable() throws SQLException { return this.inner.usesLocalFilePerTable(); }
/*     */   
/*     */   public boolean supportsMixedCaseIdentifiers() throws SQLException {
/*  91 */     return this.inner.supportsMixedCaseIdentifiers();
/*     */   }
/*     */   
/*  94 */   public boolean storesUpperCaseIdentifiers() throws SQLException { return this.inner.storesUpperCaseIdentifiers(); }
/*     */   
/*     */   public boolean storesLowerCaseIdentifiers() throws SQLException {
/*  97 */     return this.inner.storesLowerCaseIdentifiers();
/*     */   }
/*     */   
/* 100 */   public boolean storesMixedCaseIdentifiers() throws SQLException { return this.inner.storesMixedCaseIdentifiers(); }
/*     */   
/*     */   public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
/* 103 */     return this.inner.supportsMixedCaseQuotedIdentifiers();
/*     */   }
/*     */   
/* 106 */   public boolean storesUpperCaseQuotedIdentifiers() throws SQLException { return this.inner.storesUpperCaseQuotedIdentifiers(); }
/*     */   
/*     */   public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
/* 109 */     return this.inner.storesLowerCaseQuotedIdentifiers();
/*     */   }
/*     */   
/* 112 */   public boolean storesMixedCaseQuotedIdentifiers() throws SQLException { return this.inner.storesMixedCaseQuotedIdentifiers(); }
/*     */   
/*     */   public String getIdentifierQuoteString() throws SQLException {
/* 115 */     return this.inner.getIdentifierQuoteString();
/*     */   }
/*     */   
/* 118 */   public String getSQLKeywords() throws SQLException { return this.inner.getSQLKeywords(); }
/*     */   
/*     */   public String getNumericFunctions() throws SQLException {
/* 121 */     return this.inner.getNumericFunctions();
/*     */   }
/*     */   
/* 124 */   public String getStringFunctions() throws SQLException { return this.inner.getStringFunctions(); }
/*     */   
/*     */   public String getSystemFunctions() throws SQLException {
/* 127 */     return this.inner.getSystemFunctions();
/*     */   }
/*     */   
/* 130 */   public String getTimeDateFunctions() throws SQLException { return this.inner.getTimeDateFunctions(); }
/*     */   
/*     */   public String getSearchStringEscape() throws SQLException {
/* 133 */     return this.inner.getSearchStringEscape();
/*     */   }
/*     */   
/* 136 */   public String getExtraNameCharacters() throws SQLException { return this.inner.getExtraNameCharacters(); }
/*     */   
/*     */   public boolean supportsAlterTableWithAddColumn() throws SQLException {
/* 139 */     return this.inner.supportsAlterTableWithAddColumn();
/*     */   }
/*     */   
/* 142 */   public boolean supportsAlterTableWithDropColumn() throws SQLException { return this.inner.supportsAlterTableWithDropColumn(); }
/*     */   
/*     */   public boolean supportsColumnAliasing() throws SQLException {
/* 145 */     return this.inner.supportsColumnAliasing();
/*     */   }
/*     */   
/* 148 */   public boolean nullPlusNonNullIsNull() throws SQLException { return this.inner.nullPlusNonNullIsNull(); }
/*     */   
/*     */   public boolean supportsConvert() throws SQLException {
/* 151 */     return this.inner.supportsConvert();
/*     */   }
/*     */   
/* 154 */   public boolean supportsConvert(int a, int b) throws SQLException { return this.inner.supportsConvert(a, b); }
/*     */   
/*     */   public boolean supportsTableCorrelationNames() throws SQLException {
/* 157 */     return this.inner.supportsTableCorrelationNames();
/*     */   }
/*     */   
/* 160 */   public boolean supportsDifferentTableCorrelationNames() throws SQLException { return this.inner.supportsDifferentTableCorrelationNames(); }
/*     */   
/*     */   public boolean supportsExpressionsInOrderBy() throws SQLException {
/* 163 */     return this.inner.supportsExpressionsInOrderBy();
/*     */   }
/*     */   
/* 166 */   public boolean supportsOrderByUnrelated() throws SQLException { return this.inner.supportsOrderByUnrelated(); }
/*     */   
/*     */   public boolean supportsGroupBy() throws SQLException {
/* 169 */     return this.inner.supportsGroupBy();
/*     */   }
/*     */   
/* 172 */   public boolean supportsGroupByUnrelated() throws SQLException { return this.inner.supportsGroupByUnrelated(); }
/*     */   
/*     */   public boolean supportsGroupByBeyondSelect() throws SQLException {
/* 175 */     return this.inner.supportsGroupByBeyondSelect();
/*     */   }
/*     */   
/* 178 */   public boolean supportsLikeEscapeClause() throws SQLException { return this.inner.supportsLikeEscapeClause(); }
/*     */   
/*     */   public boolean supportsMultipleResultSets() throws SQLException {
/* 181 */     return this.inner.supportsMultipleResultSets();
/*     */   }
/*     */   
/* 184 */   public boolean supportsMultipleTransactions() throws SQLException { return this.inner.supportsMultipleTransactions(); }
/*     */   
/*     */   public boolean supportsNonNullableColumns() throws SQLException {
/* 187 */     return this.inner.supportsNonNullableColumns();
/*     */   }
/*     */   
/* 190 */   public boolean supportsMinimumSQLGrammar() throws SQLException { return this.inner.supportsMinimumSQLGrammar(); }
/*     */   
/*     */   public boolean supportsCoreSQLGrammar() throws SQLException {
/* 193 */     return this.inner.supportsCoreSQLGrammar();
/*     */   }
/*     */   
/* 196 */   public boolean supportsExtendedSQLGrammar() throws SQLException { return this.inner.supportsExtendedSQLGrammar(); }
/*     */   
/*     */   public boolean supportsANSI92EntryLevelSQL() throws SQLException {
/* 199 */     return this.inner.supportsANSI92EntryLevelSQL();
/*     */   }
/*     */   
/* 202 */   public boolean supportsANSI92IntermediateSQL() throws SQLException { return this.inner.supportsANSI92IntermediateSQL(); }
/*     */   
/*     */   public boolean supportsANSI92FullSQL() throws SQLException {
/* 205 */     return this.inner.supportsANSI92FullSQL();
/*     */   }
/*     */   
/* 208 */   public boolean supportsIntegrityEnhancementFacility() throws SQLException { return this.inner.supportsIntegrityEnhancementFacility(); }
/*     */   
/*     */   public boolean supportsOuterJoins() throws SQLException {
/* 211 */     return this.inner.supportsOuterJoins();
/*     */   }
/*     */   
/* 214 */   public boolean supportsFullOuterJoins() throws SQLException { return this.inner.supportsFullOuterJoins(); }
/*     */   
/*     */   public boolean supportsLimitedOuterJoins() throws SQLException {
/* 217 */     return this.inner.supportsLimitedOuterJoins();
/*     */   }
/*     */   
/* 220 */   public String getSchemaTerm() throws SQLException { return this.inner.getSchemaTerm(); }
/*     */   
/*     */   public String getProcedureTerm() throws SQLException {
/* 223 */     return this.inner.getProcedureTerm();
/*     */   }
/*     */   
/* 226 */   public String getCatalogTerm() throws SQLException { return this.inner.getCatalogTerm(); }
/*     */   
/*     */   public boolean isCatalogAtStart() throws SQLException {
/* 229 */     return this.inner.isCatalogAtStart();
/*     */   }
/*     */   
/* 232 */   public String getCatalogSeparator() throws SQLException { return this.inner.getCatalogSeparator(); }
/*     */   
/*     */   public boolean supportsSchemasInDataManipulation() throws SQLException {
/* 235 */     return this.inner.supportsSchemasInDataManipulation();
/*     */   }
/*     */   
/* 238 */   public boolean supportsSchemasInProcedureCalls() throws SQLException { return this.inner.supportsSchemasInProcedureCalls(); }
/*     */   
/*     */   public boolean supportsSchemasInTableDefinitions() throws SQLException {
/* 241 */     return this.inner.supportsSchemasInTableDefinitions();
/*     */   }
/*     */   
/* 244 */   public boolean supportsSchemasInIndexDefinitions() throws SQLException { return this.inner.supportsSchemasInIndexDefinitions(); }
/*     */   
/*     */   public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
/* 247 */     return this.inner.supportsSchemasInPrivilegeDefinitions();
/*     */   }
/*     */   
/* 250 */   public boolean supportsCatalogsInDataManipulation() throws SQLException { return this.inner.supportsCatalogsInDataManipulation(); }
/*     */   
/*     */   public boolean supportsCatalogsInProcedureCalls() throws SQLException {
/* 253 */     return this.inner.supportsCatalogsInProcedureCalls();
/*     */   }
/*     */   
/* 256 */   public boolean supportsCatalogsInTableDefinitions() throws SQLException { return this.inner.supportsCatalogsInTableDefinitions(); }
/*     */   
/*     */   public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
/* 259 */     return this.inner.supportsCatalogsInIndexDefinitions();
/*     */   }
/*     */   
/* 262 */   public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException { return this.inner.supportsCatalogsInPrivilegeDefinitions(); }
/*     */   
/*     */   public boolean supportsPositionedDelete() throws SQLException {
/* 265 */     return this.inner.supportsPositionedDelete();
/*     */   }
/*     */   
/* 268 */   public boolean supportsPositionedUpdate() throws SQLException { return this.inner.supportsPositionedUpdate(); }
/*     */   
/*     */   public boolean supportsSelectForUpdate() throws SQLException {
/* 271 */     return this.inner.supportsSelectForUpdate();
/*     */   }
/*     */   
/* 274 */   public boolean supportsStoredProcedures() throws SQLException { return this.inner.supportsStoredProcedures(); }
/*     */   
/*     */   public boolean supportsSubqueriesInComparisons() throws SQLException {
/* 277 */     return this.inner.supportsSubqueriesInComparisons();
/*     */   }
/*     */   
/* 280 */   public boolean supportsSubqueriesInExists() throws SQLException { return this.inner.supportsSubqueriesInExists(); }
/*     */   
/*     */   public boolean supportsSubqueriesInIns() throws SQLException {
/* 283 */     return this.inner.supportsSubqueriesInIns();
/*     */   }
/*     */   
/* 286 */   public boolean supportsSubqueriesInQuantifieds() throws SQLException { return this.inner.supportsSubqueriesInQuantifieds(); }
/*     */   
/*     */   public boolean supportsCorrelatedSubqueries() throws SQLException {
/* 289 */     return this.inner.supportsCorrelatedSubqueries();
/*     */   }
/*     */   
/* 292 */   public boolean supportsUnion() throws SQLException { return this.inner.supportsUnion(); }
/*     */   
/*     */   public boolean supportsUnionAll() throws SQLException {
/* 295 */     return this.inner.supportsUnionAll();
/*     */   }
/*     */   
/* 298 */   public boolean supportsOpenCursorsAcrossCommit() throws SQLException { return this.inner.supportsOpenCursorsAcrossCommit(); }
/*     */   
/*     */   public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
/* 301 */     return this.inner.supportsOpenCursorsAcrossRollback();
/*     */   }
/*     */   
/* 304 */   public boolean supportsOpenStatementsAcrossCommit() throws SQLException { return this.inner.supportsOpenStatementsAcrossCommit(); }
/*     */   
/*     */   public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
/* 307 */     return this.inner.supportsOpenStatementsAcrossRollback();
/*     */   }
/*     */   
/* 310 */   public int getMaxBinaryLiteralLength() throws SQLException { return this.inner.getMaxBinaryLiteralLength(); }
/*     */   
/*     */   public int getMaxCharLiteralLength() throws SQLException {
/* 313 */     return this.inner.getMaxCharLiteralLength();
/*     */   }
/*     */   
/* 316 */   public int getMaxColumnNameLength() throws SQLException { return this.inner.getMaxColumnNameLength(); }
/*     */   
/*     */   public int getMaxColumnsInGroupBy() throws SQLException {
/* 319 */     return this.inner.getMaxColumnsInGroupBy();
/*     */   }
/*     */   
/* 322 */   public int getMaxColumnsInIndex() throws SQLException { return this.inner.getMaxColumnsInIndex(); }
/*     */   
/*     */   public int getMaxColumnsInOrderBy() throws SQLException {
/* 325 */     return this.inner.getMaxColumnsInOrderBy();
/*     */   }
/*     */   
/* 328 */   public int getMaxColumnsInSelect() throws SQLException { return this.inner.getMaxColumnsInSelect(); }
/*     */   
/*     */   public int getMaxColumnsInTable() throws SQLException {
/* 331 */     return this.inner.getMaxColumnsInTable();
/*     */   }
/*     */   
/* 334 */   public int getMaxConnections() throws SQLException { return this.inner.getMaxConnections(); }
/*     */   
/*     */   public int getMaxCursorNameLength() throws SQLException {
/* 337 */     return this.inner.getMaxCursorNameLength();
/*     */   }
/*     */   
/* 340 */   public int getMaxIndexLength() throws SQLException { return this.inner.getMaxIndexLength(); }
/*     */   
/*     */   public int getMaxSchemaNameLength() throws SQLException {
/* 343 */     return this.inner.getMaxSchemaNameLength();
/*     */   }
/*     */   
/* 346 */   public int getMaxProcedureNameLength() throws SQLException { return this.inner.getMaxProcedureNameLength(); }
/*     */   
/*     */   public int getMaxCatalogNameLength() throws SQLException {
/* 349 */     return this.inner.getMaxCatalogNameLength();
/*     */   }
/*     */   
/* 352 */   public int getMaxRowSize() throws SQLException { return this.inner.getMaxRowSize(); }
/*     */   
/*     */   public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
/* 355 */     return this.inner.doesMaxRowSizeIncludeBlobs();
/*     */   }
/*     */   
/* 358 */   public int getMaxStatementLength() throws SQLException { return this.inner.getMaxStatementLength(); }
/*     */   
/*     */   public int getMaxStatements() throws SQLException {
/* 361 */     return this.inner.getMaxStatements();
/*     */   }
/*     */   
/* 364 */   public int getMaxTableNameLength() throws SQLException { return this.inner.getMaxTableNameLength(); }
/*     */   
/*     */   public int getMaxTablesInSelect() throws SQLException {
/* 367 */     return this.inner.getMaxTablesInSelect();
/*     */   }
/*     */   
/* 370 */   public int getMaxUserNameLength() throws SQLException { return this.inner.getMaxUserNameLength(); }
/*     */   
/*     */   public int getDefaultTransactionIsolation() throws SQLException {
/* 373 */     return this.inner.getDefaultTransactionIsolation();
/*     */   }
/*     */   
/* 376 */   public boolean supportsTransactions() throws SQLException { return this.inner.supportsTransactions(); }
/*     */   
/*     */   public boolean supportsTransactionIsolationLevel(int a) throws SQLException {
/* 379 */     return this.inner.supportsTransactionIsolationLevel(a);
/*     */   }
/*     */   
/* 382 */   public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException { return this.inner.supportsDataDefinitionAndDataManipulationTransactions(); }
/*     */   
/*     */   public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
/* 385 */     return this.inner.supportsDataManipulationTransactionsOnly();
/*     */   }
/*     */   
/* 388 */   public boolean dataDefinitionCausesTransactionCommit() throws SQLException { return this.inner.dataDefinitionCausesTransactionCommit(); }
/*     */   
/*     */   public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
/* 391 */     return this.inner.dataDefinitionIgnoredInTransactions();
/*     */   }
/*     */   
/* 394 */   public ResultSet getProcedures(String a, String b, String c) throws SQLException { return this.inner.getProcedures(a, b, c); }
/*     */   
/*     */   public ResultSet getProcedureColumns(String a, String b, String c, String d) throws SQLException {
/* 397 */     return this.inner.getProcedureColumns(a, b, c, d);
/*     */   }
/*     */   
/* 400 */   public ResultSet getTables(String a, String b, String c, String[] d) throws SQLException { return this.inner.getTables(a, b, c, d); }
/*     */   
/*     */   public ResultSet getSchemas() throws SQLException {
/* 403 */     return this.inner.getSchemas();
/*     */   }
/*     */   
/* 406 */   public ResultSet getCatalogs() throws SQLException { return this.inner.getCatalogs(); }
/*     */   
/*     */   public ResultSet getTableTypes() throws SQLException {
/* 409 */     return this.inner.getTableTypes();
/*     */   }
/*     */   
/* 412 */   public ResultSet getColumnPrivileges(String a, String b, String c, String d) throws SQLException { return this.inner.getColumnPrivileges(a, b, c, d); }
/*     */   
/*     */   public ResultSet getTablePrivileges(String a, String b, String c) throws SQLException {
/* 415 */     return this.inner.getTablePrivileges(a, b, c);
/*     */   }
/*     */   
/* 418 */   public ResultSet getBestRowIdentifier(String a, String b, String c, int d, boolean e) throws SQLException { return this.inner.getBestRowIdentifier(a, b, c, d, e); }
/*     */   
/*     */   public ResultSet getVersionColumns(String a, String b, String c) throws SQLException {
/* 421 */     return this.inner.getVersionColumns(a, b, c);
/*     */   }
/*     */   
/* 424 */   public ResultSet getPrimaryKeys(String a, String b, String c) throws SQLException { return this.inner.getPrimaryKeys(a, b, c); }
/*     */   
/*     */   public ResultSet getImportedKeys(String a, String b, String c) throws SQLException {
/* 427 */     return this.inner.getImportedKeys(a, b, c);
/*     */   }
/*     */   
/* 430 */   public ResultSet getExportedKeys(String a, String b, String c) throws SQLException { return this.inner.getExportedKeys(a, b, c); }
/*     */   
/*     */   public ResultSet getCrossReference(String a, String b, String c, String d, String e, String f) throws SQLException {
/* 433 */     return this.inner.getCrossReference(a, b, c, d, e, f);
/*     */   }
/*     */   
/* 436 */   public ResultSet getTypeInfo() throws SQLException { return this.inner.getTypeInfo(); }
/*     */   
/*     */   public ResultSet getIndexInfo(String a, String b, String c, boolean d, boolean e) throws SQLException {
/* 439 */     return this.inner.getIndexInfo(a, b, c, d, e);
/*     */   }
/*     */   
/* 442 */   public boolean supportsResultSetType(int a) throws SQLException { return this.inner.supportsResultSetType(a); }
/*     */   
/*     */   public boolean supportsResultSetConcurrency(int a, int b) throws SQLException {
/* 445 */     return this.inner.supportsResultSetConcurrency(a, b);
/*     */   }
/*     */   
/* 448 */   public boolean ownUpdatesAreVisible(int a) throws SQLException { return this.inner.ownUpdatesAreVisible(a); }
/*     */   
/*     */   public boolean ownDeletesAreVisible(int a) throws SQLException {
/* 451 */     return this.inner.ownDeletesAreVisible(a);
/*     */   }
/*     */   
/* 454 */   public boolean ownInsertsAreVisible(int a) throws SQLException { return this.inner.ownInsertsAreVisible(a); }
/*     */   
/*     */   public boolean othersUpdatesAreVisible(int a) throws SQLException {
/* 457 */     return this.inner.othersUpdatesAreVisible(a);
/*     */   }
/*     */   
/* 460 */   public boolean othersDeletesAreVisible(int a) throws SQLException { return this.inner.othersDeletesAreVisible(a); }
/*     */   
/*     */   public boolean othersInsertsAreVisible(int a) throws SQLException {
/* 463 */     return this.inner.othersInsertsAreVisible(a);
/*     */   }
/*     */   
/* 466 */   public boolean updatesAreDetected(int a) throws SQLException { return this.inner.updatesAreDetected(a); }
/*     */   
/*     */   public boolean deletesAreDetected(int a) throws SQLException {
/* 469 */     return this.inner.deletesAreDetected(a);
/*     */   }
/*     */   
/* 472 */   public boolean insertsAreDetected(int a) throws SQLException { return this.inner.insertsAreDetected(a); }
/*     */   
/*     */   public boolean supportsBatchUpdates() throws SQLException {
/* 475 */     return this.inner.supportsBatchUpdates();
/*     */   }
/*     */   
/* 478 */   public ResultSet getUDTs(String a, String b, String c, int[] d) throws SQLException { return this.inner.getUDTs(a, b, c, d); }
/*     */   
/*     */   public boolean supportsSavepoints() throws SQLException {
/* 481 */     return this.inner.supportsSavepoints();
/*     */   }
/*     */   
/* 484 */   public boolean supportsNamedParameters() throws SQLException { return this.inner.supportsNamedParameters(); }
/*     */   
/*     */   public boolean supportsMultipleOpenResults() throws SQLException {
/* 487 */     return this.inner.supportsMultipleOpenResults();
/*     */   }
/*     */   
/* 490 */   public boolean supportsGetGeneratedKeys() throws SQLException { return this.inner.supportsGetGeneratedKeys(); }
/*     */   
/*     */   public ResultSet getSuperTypes(String a, String b, String c) throws SQLException {
/* 493 */     return this.inner.getSuperTypes(a, b, c);
/*     */   }
/*     */   
/* 496 */   public ResultSet getSuperTables(String a, String b, String c) throws SQLException { return this.inner.getSuperTables(a, b, c); }
/*     */   
/*     */   public boolean supportsResultSetHoldability(int a) throws SQLException {
/* 499 */     return this.inner.supportsResultSetHoldability(a);
/*     */   }
/*     */   
/* 502 */   public int getResultSetHoldability() throws SQLException { return this.inner.getResultSetHoldability(); }
/*     */   
/*     */   public int getDatabaseMajorVersion() throws SQLException {
/* 505 */     return this.inner.getDatabaseMajorVersion();
/*     */   }
/*     */   
/* 508 */   public int getDatabaseMinorVersion() throws SQLException { return this.inner.getDatabaseMinorVersion(); }
/*     */   
/*     */   public int getJDBCMajorVersion() throws SQLException {
/* 511 */     return this.inner.getJDBCMajorVersion();
/*     */   }
/*     */   
/* 514 */   public int getJDBCMinorVersion() throws SQLException { return this.inner.getJDBCMinorVersion(); }
/*     */   
/*     */   public int getSQLStateType() throws SQLException {
/* 517 */     return this.inner.getSQLStateType();
/*     */   }
/*     */   
/* 520 */   public boolean locatorsUpdateCopy() throws SQLException { return this.inner.locatorsUpdateCopy(); }
/*     */   
/*     */   public boolean supportsStatementPooling() throws SQLException {
/* 523 */     return this.inner.supportsStatementPooling();
/*     */   }
/*     */   
/* 526 */   public String getURL() throws SQLException { return this.inner.getURL(); }
/*     */   
/*     */   public boolean isReadOnly() throws SQLException {
/* 529 */     return this.inner.isReadOnly();
/*     */   }
/*     */   
/* 532 */   public ResultSet getAttributes(String a, String b, String c, String d) throws SQLException { return this.inner.getAttributes(a, b, c, d); }
/*     */   
/*     */   public Connection getConnection() throws SQLException {
/* 535 */     return this.inner.getConnection();
/*     */   }
/*     */   
/* 538 */   public ResultSet getColumns(String a, String b, String c, String d) throws SQLException { return this.inner.getColumns(a, b, c, d); }
/*     */   
/*     */   public String getUserName() throws SQLException {
/* 541 */     return this.inner.getUserName();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\sql\filter\FilterDatabaseMetaData.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */