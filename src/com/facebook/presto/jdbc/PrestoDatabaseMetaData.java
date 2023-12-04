/*      */ package com.facebook.presto.jdbc;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.client.NodeVersion;
/*      */ import com.facebook.presto.jdbc.internal.client.ServerInfo;
/*      */ import com.facebook.presto.jdbc.internal.guava.base.Joiner;
/*      */ import java.net.URI;
/*      */ import java.sql.Connection;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.RowIdLifetime;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLFeatureNotSupportedException;
/*      */ import java.sql.Statement;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import java.util.Objects;
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
/*      */ public class PrestoDatabaseMetaData
/*      */   implements DatabaseMetaData
/*      */ {
/*      */   private final PrestoConnection connection;
/*      */   
/*      */   PrestoDatabaseMetaData(PrestoConnection connection)
/*      */   {
/*   37 */     this.connection = ((PrestoConnection)Objects.requireNonNull(connection, "connection is null"));
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean allProceduresAreCallable()
/*      */     throws SQLException
/*      */   {
/*   44 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean allTablesAreSelectable()
/*      */     throws SQLException
/*      */   {
/*   51 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getURL()
/*      */     throws SQLException
/*      */   {
/*   58 */     return this.connection.getURI().toString();
/*      */   }
/*      */   
/*      */ 
/*      */   public String getUserName()
/*      */     throws SQLException
/*      */   {
/*   65 */     return this.connection.getUser();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isReadOnly()
/*      */     throws SQLException
/*      */   {
/*   72 */     return this.connection.isReadOnly();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean nullsAreSortedHigh()
/*      */     throws SQLException
/*      */   {
/*   79 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean nullsAreSortedLow()
/*      */     throws SQLException
/*      */   {
/*   86 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean nullsAreSortedAtStart()
/*      */     throws SQLException
/*      */   {
/*   93 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean nullsAreSortedAtEnd()
/*      */     throws SQLException
/*      */   {
/*  100 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getDatabaseProductName()
/*      */     throws SQLException
/*      */   {
/*  107 */     return "Presto";
/*      */   }
/*      */   
/*      */   public String getDatabaseProductVersion()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  115 */       return this.connection.getServerInfo().getNodeVersion().getVersion();
/*      */     }
/*      */     catch (RuntimeException e) {
/*  118 */       throw new SQLException("Error fetching version from server", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getDriverName()
/*      */     throws SQLException
/*      */   {
/*  126 */     return "Presto JDBC Driver";
/*      */   }
/*      */   
/*      */ 
/*      */   public String getDriverVersion()
/*      */     throws SQLException
/*      */   {
/*  133 */     return "1.0";
/*      */   }
/*      */   
/*      */ 
/*      */   public int getDriverMajorVersion()
/*      */   {
/*  139 */     return 1;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getDriverMinorVersion()
/*      */   {
/*  145 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean usesLocalFiles()
/*      */     throws SQLException
/*      */   {
/*  152 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean usesLocalFilePerTable()
/*      */     throws SQLException
/*      */   {
/*  159 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsMixedCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/*  166 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean storesUpperCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/*  173 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean storesLowerCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/*  180 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean storesMixedCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/*  187 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsMixedCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/*  195 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean storesUpperCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/*  202 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean storesLowerCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/*  210 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean storesMixedCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/*  218 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getIdentifierQuoteString()
/*      */     throws SQLException
/*      */   {
/*  225 */     return "\"";
/*      */   }
/*      */   
/*      */ 
/*      */   public String getSQLKeywords()
/*      */     throws SQLException
/*      */   {
/*  232 */     return "LIMIT";
/*      */   }
/*      */   
/*      */ 
/*      */   public String getNumericFunctions()
/*      */     throws SQLException
/*      */   {
/*  239 */     return "";
/*      */   }
/*      */   
/*      */ 
/*      */   public String getStringFunctions()
/*      */     throws SQLException
/*      */   {
/*  246 */     return "";
/*      */   }
/*      */   
/*      */ 
/*      */   public String getSystemFunctions()
/*      */     throws SQLException
/*      */   {
/*  253 */     return "";
/*      */   }
/*      */   
/*      */ 
/*      */   public String getTimeDateFunctions()
/*      */     throws SQLException
/*      */   {
/*  260 */     return "";
/*      */   }
/*      */   
/*      */ 
/*      */   public String getSearchStringEscape()
/*      */     throws SQLException
/*      */   {
/*  267 */     return "\\";
/*      */   }
/*      */   
/*      */ 
/*      */   public String getExtraNameCharacters()
/*      */     throws SQLException
/*      */   {
/*  274 */     return "";
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsAlterTableWithAddColumn()
/*      */     throws SQLException
/*      */   {
/*  281 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsAlterTableWithDropColumn()
/*      */     throws SQLException
/*      */   {
/*  288 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsColumnAliasing()
/*      */     throws SQLException
/*      */   {
/*  295 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean nullPlusNonNullIsNull()
/*      */     throws SQLException
/*      */   {
/*  302 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsConvert()
/*      */     throws SQLException
/*      */   {
/*  310 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsConvert(int fromType, int toType)
/*      */     throws SQLException
/*      */   {
/*  318 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsTableCorrelationNames()
/*      */     throws SQLException
/*      */   {
/*  325 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsDifferentTableCorrelationNames()
/*      */     throws SQLException
/*      */   {
/*  332 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsExpressionsInOrderBy()
/*      */     throws SQLException
/*      */   {
/*  339 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsOrderByUnrelated()
/*      */     throws SQLException
/*      */   {
/*  346 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsGroupBy()
/*      */     throws SQLException
/*      */   {
/*  353 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsGroupByUnrelated()
/*      */     throws SQLException
/*      */   {
/*  360 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsGroupByBeyondSelect()
/*      */     throws SQLException
/*      */   {
/*  367 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsLikeEscapeClause()
/*      */     throws SQLException
/*      */   {
/*  374 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsMultipleResultSets()
/*      */     throws SQLException
/*      */   {
/*  381 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsMultipleTransactions()
/*      */     throws SQLException
/*      */   {
/*  388 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsNonNullableColumns()
/*      */     throws SQLException
/*      */   {
/*  395 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsMinimumSQLGrammar()
/*      */     throws SQLException
/*      */   {
/*  402 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsCoreSQLGrammar()
/*      */     throws SQLException
/*      */   {
/*  410 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsExtendedSQLGrammar()
/*      */     throws SQLException
/*      */   {
/*  418 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsANSI92EntryLevelSQL()
/*      */     throws SQLException
/*      */   {
/*  426 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsANSI92IntermediateSQL()
/*      */     throws SQLException
/*      */   {
/*  434 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsANSI92FullSQL()
/*      */     throws SQLException
/*      */   {
/*  442 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsIntegrityEnhancementFacility()
/*      */     throws SQLException
/*      */   {
/*  449 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsOuterJoins()
/*      */     throws SQLException
/*      */   {
/*  456 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsFullOuterJoins()
/*      */     throws SQLException
/*      */   {
/*  463 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsLimitedOuterJoins()
/*      */     throws SQLException
/*      */   {
/*  470 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getSchemaTerm()
/*      */     throws SQLException
/*      */   {
/*  477 */     return "schema";
/*      */   }
/*      */   
/*      */ 
/*      */   public String getProcedureTerm()
/*      */     throws SQLException
/*      */   {
/*  484 */     return "procedure";
/*      */   }
/*      */   
/*      */ 
/*      */   public String getCatalogTerm()
/*      */     throws SQLException
/*      */   {
/*  491 */     return "catalog";
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isCatalogAtStart()
/*      */     throws SQLException
/*      */   {
/*  498 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getCatalogSeparator()
/*      */     throws SQLException
/*      */   {
/*  505 */     return ".";
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsSchemasInDataManipulation()
/*      */     throws SQLException
/*      */   {
/*  512 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsSchemasInProcedureCalls()
/*      */     throws SQLException
/*      */   {
/*  519 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsSchemasInTableDefinitions()
/*      */     throws SQLException
/*      */   {
/*  526 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsSchemasInIndexDefinitions()
/*      */     throws SQLException
/*      */   {
/*  533 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsSchemasInPrivilegeDefinitions()
/*      */     throws SQLException
/*      */   {
/*  540 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsCatalogsInDataManipulation()
/*      */     throws SQLException
/*      */   {
/*  547 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsCatalogsInProcedureCalls()
/*      */     throws SQLException
/*      */   {
/*  554 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsCatalogsInTableDefinitions()
/*      */     throws SQLException
/*      */   {
/*  561 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsCatalogsInIndexDefinitions()
/*      */     throws SQLException
/*      */   {
/*  568 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsCatalogsInPrivilegeDefinitions()
/*      */     throws SQLException
/*      */   {
/*  575 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsPositionedDelete()
/*      */     throws SQLException
/*      */   {
/*  582 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsPositionedUpdate()
/*      */     throws SQLException
/*      */   {
/*  589 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsSelectForUpdate()
/*      */     throws SQLException
/*      */   {
/*  596 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsStoredProcedures()
/*      */     throws SQLException
/*      */   {
/*  604 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsSubqueriesInComparisons()
/*      */     throws SQLException
/*      */   {
/*  612 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsSubqueriesInExists()
/*      */     throws SQLException
/*      */   {
/*  620 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsSubqueriesInIns()
/*      */     throws SQLException
/*      */   {
/*  627 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsSubqueriesInQuantifieds()
/*      */     throws SQLException
/*      */   {
/*  635 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsCorrelatedSubqueries()
/*      */     throws SQLException
/*      */   {
/*  643 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsUnion()
/*      */     throws SQLException
/*      */   {
/*  650 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsUnionAll()
/*      */     throws SQLException
/*      */   {
/*  657 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsOpenCursorsAcrossCommit()
/*      */     throws SQLException
/*      */   {
/*  664 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsOpenCursorsAcrossRollback()
/*      */     throws SQLException
/*      */   {
/*  671 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsOpenStatementsAcrossCommit()
/*      */     throws SQLException
/*      */   {
/*  678 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsOpenStatementsAcrossRollback()
/*      */     throws SQLException
/*      */   {
/*  685 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxBinaryLiteralLength()
/*      */     throws SQLException
/*      */   {
/*  692 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxCharLiteralLength()
/*      */     throws SQLException
/*      */   {
/*  699 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getMaxColumnNameLength()
/*      */     throws SQLException
/*      */   {
/*  707 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxColumnsInGroupBy()
/*      */     throws SQLException
/*      */   {
/*  714 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxColumnsInIndex()
/*      */     throws SQLException
/*      */   {
/*  721 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxColumnsInOrderBy()
/*      */     throws SQLException
/*      */   {
/*  728 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxColumnsInSelect()
/*      */     throws SQLException
/*      */   {
/*  735 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxColumnsInTable()
/*      */     throws SQLException
/*      */   {
/*  742 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxConnections()
/*      */     throws SQLException
/*      */   {
/*  749 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxCursorNameLength()
/*      */     throws SQLException
/*      */   {
/*  756 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxIndexLength()
/*      */     throws SQLException
/*      */   {
/*  763 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getMaxSchemaNameLength()
/*      */     throws SQLException
/*      */   {
/*  771 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getMaxProcedureNameLength()
/*      */     throws SQLException
/*      */   {
/*  779 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getMaxCatalogNameLength()
/*      */     throws SQLException
/*      */   {
/*  787 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxRowSize()
/*      */     throws SQLException
/*      */   {
/*  794 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean doesMaxRowSizeIncludeBlobs()
/*      */     throws SQLException
/*      */   {
/*  801 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxStatementLength()
/*      */     throws SQLException
/*      */   {
/*  808 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxStatements()
/*      */     throws SQLException
/*      */   {
/*  815 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getMaxTableNameLength()
/*      */     throws SQLException
/*      */   {
/*  823 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getMaxTablesInSelect()
/*      */     throws SQLException
/*      */   {
/*  830 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getMaxUserNameLength()
/*      */     throws SQLException
/*      */   {
/*  838 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getDefaultTransactionIsolation()
/*      */     throws SQLException
/*      */   {
/*  846 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsTransactions()
/*      */     throws SQLException
/*      */   {
/*  854 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsTransactionIsolationLevel(int level)
/*      */     throws SQLException
/*      */   {
/*  861 */     return level == 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsDataDefinitionAndDataManipulationTransactions()
/*      */     throws SQLException
/*      */   {
/*  868 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsDataManipulationTransactionsOnly()
/*      */     throws SQLException
/*      */   {
/*  875 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean dataDefinitionCausesTransactionCommit()
/*      */     throws SQLException
/*      */   {
/*  882 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean dataDefinitionIgnoredInTransactions()
/*      */     throws SQLException
/*      */   {
/*  889 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern)
/*      */     throws SQLException
/*      */   {
/*  896 */     return selectEmpty("SELECT PROCEDURE_CAT, PROCEDURE_SCHEM, PROCEDURE_NAME,\n   null, null, null, REMARKS, PROCEDURE_TYPE, SPECIFIC_NAME\nFROM system.jdbc.procedures\nORDER BY PROCEDURE_CAT, PROCEDURE_SCHEM, PROCEDURE_NAME, SPECIFIC_NAME");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/*  907 */     return selectEmpty("SELECT PROCEDURE_CAT, PROCEDURE_SCHEM, PROCEDURE_NAME,   COLUMN_NAME, COLUMN_TYPE, DATA_TYPE, TYPE_NAME,\n  PRECISION, LENGTH, SCALE, RADIX,\n  NULLABLE, REMARKS, COLUMN_DEF, SQL_DATA_TYPE, SQL_DATETIME_SUB,\n  CHAR_OCTET_LENGTH, ORDINAL_POSITION, IS_NULLABLE, SPECIFIC_NAME\nFROM system.jdbc.procedure_columns\nORDER BY PROCEDURE_CAT, PROCEDURE_SCHEM, PROCEDURE_NAME, SPECIFIC_NAME, COLUMN_NAME");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types)
/*      */     throws SQLException
/*      */   {
/*  921 */     StringBuilder query = new StringBuilder("SELECT TABLE_CAT, TABLE_SCHEM, TABLE_NAME, TABLE_TYPE, REMARKS,\n  TYPE_CAT, TYPE_SCHEM, TYPE_NAME,   SELF_REFERENCING_COL_NAME, REF_GENERATION\nFROM system.jdbc.tables");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  927 */     List<String> filters = new ArrayList();
/*  928 */     emptyStringEqualsFilter(filters, "TABLE_CAT", catalog);
/*  929 */     emptyStringLikeFilter(filters, "TABLE_SCHEM", schemaPattern);
/*  930 */     optionalStringLikeFilter(filters, "TABLE_NAME", tableNamePattern);
/*  931 */     optionalStringInFilter(filters, "TABLE_TYPE", types);
/*  932 */     buildFilters(query, filters);
/*      */     
/*  934 */     query.append("\nORDER BY TABLE_TYPE, TABLE_CAT, TABLE_SCHEM, TABLE_NAME");
/*      */     
/*  936 */     return select(query.toString());
/*      */   }
/*      */   
/*      */ 
/*      */   public ResultSet getSchemas()
/*      */     throws SQLException
/*      */   {
/*  943 */     return select("SELECT TABLE_SCHEM, TABLE_CATALOG\nFROM system.jdbc.schemas\nORDER BY TABLE_CATALOG, TABLE_SCHEM");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ResultSet getCatalogs()
/*      */     throws SQLException
/*      */   {
/*  953 */     return select("SELECT TABLE_CAT\nFROM system.jdbc.catalogs\nORDER BY TABLE_CAT");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ResultSet getTableTypes()
/*      */     throws SQLException
/*      */   {
/*  963 */     return select("SELECT TABLE_TYPE\nFROM system.jdbc.table_types\nORDER BY TABLE_TYPE");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/*  973 */     StringBuilder query = new StringBuilder("SELECT TABLE_CAT, TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, DATA_TYPE,\n  TYPE_NAME, COLUMN_SIZE, BUFFER_LENGTH, DECIMAL_DIGITS, NUM_PREC_RADIX,\n  NULLABLE, REMARKS, COLUMN_DEF, SQL_DATA_TYPE, SQL_DATETIME_SUB,\n  CHAR_OCTET_LENGTH, ORDINAL_POSITION, IS_NULLABLE,\n  SCOPE_CATALOG, SCOPE_SCHEMA, SCOPE_TABLE,\n  SOURCE_DATA_TYPE, IS_AUTOINCREMENT, IS_GENERATEDCOLUMN\nFROM system.jdbc.columns\n");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  982 */     List<String> filters = new ArrayList();
/*  983 */     emptyStringEqualsFilter(filters, "TABLE_CAT", catalog);
/*  984 */     emptyStringLikeFilter(filters, "TABLE_SCHEM", schemaPattern);
/*  985 */     optionalStringLikeFilter(filters, "TABLE_NAME", tableNamePattern);
/*  986 */     optionalStringLikeFilter(filters, "COLUMN_NAME", columnNamePattern);
/*  987 */     buildFilters(query, filters);
/*      */     
/*  989 */     query.append("\nORDER BY TABLE_CAT, TABLE_SCHEM, TABLE_NAME, ORDINAL_POSITION");
/*      */     
/*  991 */     return select(query.toString());
/*      */   }
/*      */   
/*      */ 
/*      */   public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/*  998 */     throw new SQLFeatureNotSupportedException("privileges not supported");
/*      */   }
/*      */   
/*      */ 
/*      */   public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern)
/*      */     throws SQLException
/*      */   {
/* 1005 */     throw new SQLFeatureNotSupportedException("privileges not supported");
/*      */   }
/*      */   
/*      */ 
/*      */   public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable)
/*      */     throws SQLException
/*      */   {
/* 1012 */     throw new SQLFeatureNotSupportedException("row identifiers not supported");
/*      */   }
/*      */   
/*      */ 
/*      */   public ResultSet getVersionColumns(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/* 1019 */     throw new SQLFeatureNotSupportedException("version columns not supported");
/*      */   }
/*      */   
/*      */ 
/*      */   public ResultSet getPrimaryKeys(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/* 1026 */     throw new SQLFeatureNotSupportedException("primary keys not supported");
/*      */   }
/*      */   
/*      */ 
/*      */   public ResultSet getImportedKeys(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/* 1033 */     throw new SQLFeatureNotSupportedException("imported keys not supported");
/*      */   }
/*      */   
/*      */ 
/*      */   public ResultSet getExportedKeys(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/* 1040 */     throw new SQLFeatureNotSupportedException("exported keys not supported");
/*      */   }
/*      */   
/*      */ 
/*      */   public ResultSet getCrossReference(String parentCatalog, String parentSchema, String parentTable, String foreignCatalog, String foreignSchema, String foreignTable)
/*      */     throws SQLException
/*      */   {
/* 1047 */     throw new SQLFeatureNotSupportedException("cross reference not supported");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ResultSet getTypeInfo()
/*      */     throws SQLException
/*      */   {
/* 1055 */     throw new NotImplementedException("DatabaseMetaData", "getTypeInfo");
/*      */   }
/*      */   
/*      */ 
/*      */   public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate)
/*      */     throws SQLException
/*      */   {
/* 1062 */     throw new SQLFeatureNotSupportedException("indexes not supported");
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsResultSetType(int type)
/*      */     throws SQLException
/*      */   {
/* 1069 */     return type == 1003;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsResultSetConcurrency(int type, int concurrency)
/*      */     throws SQLException
/*      */   {
/* 1076 */     return (type == 1003) && (concurrency == 1007);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean ownUpdatesAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 1084 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean ownDeletesAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 1091 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean ownInsertsAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 1098 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean othersUpdatesAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 1105 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean othersDeletesAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 1112 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean othersInsertsAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 1119 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean updatesAreDetected(int type)
/*      */     throws SQLException
/*      */   {
/* 1126 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean deletesAreDetected(int type)
/*      */     throws SQLException
/*      */   {
/* 1133 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean insertsAreDetected(int type)
/*      */     throws SQLException
/*      */   {
/* 1140 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsBatchUpdates()
/*      */     throws SQLException
/*      */   {
/* 1148 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types)
/*      */     throws SQLException
/*      */   {
/* 1155 */     return selectEmpty("SELECT TYPE_CAT, TYPE_SCHEM, TYPE_NAME,\n  CLASS_NAME, DATA_TYPE, REMARKS, BASE_TYPE\nFROM system.jdbc.udts\nORDER BY DATA_TYPE, TYPE_CAT, TYPE_SCHEM, TYPE_NAME");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Connection getConnection()
/*      */     throws SQLException
/*      */   {
/* 1166 */     return this.connection;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsSavepoints()
/*      */     throws SQLException
/*      */   {
/* 1173 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsNamedParameters()
/*      */     throws SQLException
/*      */   {
/* 1180 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsMultipleOpenResults()
/*      */     throws SQLException
/*      */   {
/* 1187 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsGetGeneratedKeys()
/*      */     throws SQLException
/*      */   {
/* 1194 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern)
/*      */     throws SQLException
/*      */   {
/* 1201 */     return selectEmpty("SELECT TYPE_CAT, TYPE_SCHEM, TYPE_NAME,\n  SUPERTYPE_CAT, SUPERTYPE_SCHEM, SUPERTYPE_NAME\nFROM system.jdbc.super_types\nORDER BY TYPE_CAT, TYPE_SCHEM, TYPE_NAME");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern)
/*      */     throws SQLException
/*      */   {
/* 1212 */     return selectEmpty("SELECT TABLE_CAT, TABLE_SCHEM, TABLE_NAME, SUPERTABLE_NAME\nFROM system.jdbc.super_tables\nORDER BY TABLE_CAT, TABLE_SCHEM, TABLE_NAME");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern)
/*      */     throws SQLException
/*      */   {
/* 1222 */     return selectEmpty("SELECT TYPE_CAT, TYPE_SCHEM, TYPE_NAME, ATTR_NAME, DATA_TYPE,\n  ATTR_TYPE_NAME, ATTR_SIZE, DECIMAL_DIGITS, NUM_PREC_RADIX, NULLABLE,\n  REMARKS, ATTR_DEF, SQL_DATA_TYPE, SQL_DATETIME_SUB, CHAR_OCTET_LENGTH,\n  ORDINAL_POSITION, IS_NULLABLE, SCOPE_CATALOG, SCOPE_SCHEMA, SCOPE_TABLE,\nSOURCE_DATA_TYPE\nFROM system.jdbc.attributes\nORDER BY TYPE_CAT, TYPE_SCHEM, TYPE_NAME, ORDINAL_POSITION");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsResultSetHoldability(int holdability)
/*      */     throws SQLException
/*      */   {
/* 1236 */     return holdability == 1;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getResultSetHoldability()
/*      */     throws SQLException
/*      */   {
/* 1243 */     return 1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getDatabaseMajorVersion()
/*      */     throws SQLException
/*      */   {
/* 1251 */     return 1;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getDatabaseMinorVersion()
/*      */     throws SQLException
/*      */   {
/* 1258 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getJDBCMajorVersion()
/*      */     throws SQLException
/*      */   {
/* 1265 */     return 4;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getJDBCMinorVersion()
/*      */     throws SQLException
/*      */   {
/* 1272 */     return 1;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getSQLStateType()
/*      */     throws SQLException
/*      */   {
/* 1279 */     return 2;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean locatorsUpdateCopy()
/*      */     throws SQLException
/*      */   {
/* 1286 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsStatementPooling()
/*      */     throws SQLException
/*      */   {
/* 1293 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public RowIdLifetime getRowIdLifetime()
/*      */     throws SQLException
/*      */   {
/* 1300 */     return RowIdLifetime.ROWID_UNSUPPORTED;
/*      */   }
/*      */   
/*      */ 
/*      */   public ResultSet getSchemas(String catalog, String schemaPattern)
/*      */     throws SQLException
/*      */   {
/* 1307 */     StringBuilder query = new StringBuilder("SELECT TABLE_SCHEM, TABLE_CATALOG\nFROM system.jdbc.schemas");
/*      */     
/*      */ 
/*      */ 
/* 1311 */     List<String> filters = new ArrayList();
/* 1312 */     emptyStringEqualsFilter(filters, "TABLE_CATALOG", catalog);
/* 1313 */     optionalStringLikeFilter(filters, "TABLE_SCHEM", schemaPattern);
/* 1314 */     buildFilters(query, filters);
/*      */     
/* 1316 */     query.append("\nORDER BY TABLE_CATALOG, TABLE_SCHEM");
/*      */     
/* 1318 */     return select(query.toString());
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsStoredFunctionsUsingCallSyntax()
/*      */     throws SQLException
/*      */   {
/* 1325 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean autoCommitFailureClosesAllResultSets()
/*      */     throws SQLException
/*      */   {
/* 1332 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ResultSet getClientInfoProperties()
/*      */     throws SQLException
/*      */   {
/* 1340 */     throw new NotImplementedException("DatabaseMetaData", "getClientInfoProperties");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern)
/*      */     throws SQLException
/*      */   {
/* 1348 */     throw new NotImplementedException("DatabaseMetaData", "getFunctions");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 1356 */     throw new NotImplementedException("DatabaseMetaData", "getFunctionColumns");
/*      */   }
/*      */   
/*      */ 
/*      */   public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 1363 */     return selectEmpty("SELECT TABLE_CAT, TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, DATA_TYPE,\n  COLUMN_SIZE, DECIMAL_DIGITS, NUM_PREC_RADIX, COLUMN_USAGE, REMARKS,\n  CHAR_OCTET_LENGTH, IS_NULLABLE\nFROM system.jdbc.pseudo_columns\nORDER BY TABLE_CAT, table_SCHEM, TABLE_NAME, COLUMN_NAME");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean generatedKeyAlwaysReturned()
/*      */     throws SQLException
/*      */   {
/* 1375 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public <T> T unwrap(Class<T> iface)
/*      */     throws SQLException
/*      */   {
/* 1383 */     if (isWrapperFor(iface)) {
/* 1384 */       return this;
/*      */     }
/* 1386 */     throw new SQLException("No wrapper for " + iface);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isWrapperFor(Class<?> iface)
/*      */     throws SQLException
/*      */   {
/* 1393 */     return iface.isInstance(this);
/*      */   }
/*      */   
/*      */   private ResultSet selectEmpty(String sql)
/*      */     throws SQLException
/*      */   {
/* 1399 */     return select(sql + " LIMIT 0");
/*      */   }
/*      */   
/*      */   private ResultSet select(String sql)
/*      */     throws SQLException
/*      */   {
/* 1405 */     Statement statement = getConnection().createStatement();Throwable localThrowable3 = null;
/* 1406 */     try { return statement.executeQuery(sql);
/*      */     }
/*      */     catch (Throwable localThrowable4)
/*      */     {
/* 1405 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*      */     } finally {
/* 1407 */       if (statement != null) if (localThrowable3 != null) try { statement.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else statement.close();
/*      */     }
/*      */   }
/*      */   
/*      */   private static void buildFilters(StringBuilder out, List<String> filters) {
/* 1412 */     if (!filters.isEmpty()) {
/* 1413 */       out.append("\nWHERE ");
/* 1414 */       Joiner.on(" AND ").appendTo(out, filters);
/*      */     }
/*      */   }
/*      */   
/*      */   private static void optionalStringInFilter(List<String> filters, String columnName, String[] values)
/*      */   {
/* 1420 */     if (values == null) {
/* 1421 */       return;
/*      */     }
/*      */     
/* 1424 */     if (values.length == 0) {
/* 1425 */       filters.add("false");
/* 1426 */       return;
/*      */     }
/*      */     
/* 1429 */     StringBuilder filter = new StringBuilder();
/* 1430 */     filter.append(columnName).append(" IN (");
/*      */     
/* 1432 */     for (int i = 0; i < values.length; i++) {
/* 1433 */       if (i > 0) {
/* 1434 */         filter.append(", ");
/*      */       }
/* 1436 */       quoteStringLiteral(filter, values[i]);
/*      */     }
/*      */     
/* 1439 */     filter.append(")");
/* 1440 */     filters.add(filter.toString());
/*      */   }
/*      */   
/*      */   private static void optionalStringLikeFilter(List<String> filters, String columnName, String value)
/*      */   {
/* 1445 */     if (value != null) {
/* 1446 */       filters.add(stringColumnLike(columnName, value));
/*      */     }
/*      */   }
/*      */   
/*      */   private static void emptyStringEqualsFilter(List<String> filters, String columnName, String value)
/*      */   {
/* 1452 */     if (value != null) {
/* 1453 */       if (value.isEmpty()) {
/* 1454 */         filters.add(columnName + " IS NULL");
/*      */       }
/*      */       else {
/* 1457 */         filters.add(stringColumnEquals(columnName, value));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private static void emptyStringLikeFilter(List<String> filters, String columnName, String value)
/*      */   {
/* 1464 */     if (value != null) {
/* 1465 */       if (value.isEmpty()) {
/* 1466 */         filters.add(columnName + " IS NULL");
/*      */       }
/*      */       else {
/* 1469 */         filters.add(stringColumnLike(columnName, value));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private static String stringColumnEquals(String columnName, String value)
/*      */   {
/* 1476 */     StringBuilder filter = new StringBuilder();
/* 1477 */     filter.append(columnName).append(" = ");
/* 1478 */     quoteStringLiteral(filter, value);
/* 1479 */     return filter.toString();
/*      */   }
/*      */   
/*      */   private static String stringColumnLike(String columnName, String pattern)
/*      */   {
/* 1484 */     StringBuilder filter = new StringBuilder();
/* 1485 */     filter.append(columnName).append(" LIKE ");
/* 1486 */     quoteStringLiteral(filter, pattern);
/* 1487 */     return filter.toString();
/*      */   }
/*      */   
/*      */   private static void quoteStringLiteral(StringBuilder out, String value)
/*      */   {
/* 1492 */     out.append('\'');
/* 1493 */     for (int i = 0; i < value.length(); i++) {
/* 1494 */       char c = value.charAt(i);
/* 1495 */       out.append(c);
/* 1496 */       if (c == '\'') {
/* 1497 */         out.append('\'');
/*      */       }
/*      */     }
/* 1500 */     out.append('\'');
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\PrestoDatabaseMetaData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */