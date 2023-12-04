/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.util.List;
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
/*      */ public class DatabaseMetaDataUsingInfoSchema
/*      */   extends DatabaseMetaData
/*      */ {
/*      */   private boolean hasReferentialConstraintsView;
/*      */   private final boolean hasParametersView;
/*      */   
/*      */   protected static enum JDBC4FunctionConstant
/*      */   {
/*   38 */     FUNCTION_COLUMN_UNKNOWN,  FUNCTION_COLUMN_IN,  FUNCTION_COLUMN_INOUT,  FUNCTION_COLUMN_OUT,  FUNCTION_COLUMN_RETURN,  FUNCTION_COLUMN_RESULT, 
/*      */     
/*   40 */     FUNCTION_NO_NULLS,  FUNCTION_NULLABLE,  FUNCTION_NULLABLE_UNKNOWN;
/*      */     
/*      */     private JDBC4FunctionConstant() {}
/*      */   }
/*      */   
/*      */   protected DatabaseMetaDataUsingInfoSchema(MySQLConnection connToSet, String databaseToSet) throws SQLException
/*      */   {
/*   47 */     super(connToSet, databaseToSet);
/*      */     
/*   49 */     this.hasReferentialConstraintsView = this.conn.versionMeetsMinimum(5, 1, 10);
/*      */     
/*   51 */     ResultSet rs = null;
/*      */     try
/*      */     {
/*   54 */       rs = super.getTables("INFORMATION_SCHEMA", null, "PARAMETERS", new String[0]);
/*      */       
/*   56 */       this.hasParametersView = rs.next();
/*      */     } finally {
/*   58 */       if (rs != null) {
/*   59 */         rs.close();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected ResultSet executeMetadataQuery(PreparedStatement pStmt) throws SQLException {
/*   65 */     ResultSet rs = pStmt.executeQuery();
/*   66 */     ((ResultSetInternalMethods)rs).setOwningStatement(null);
/*      */     
/*   68 */     return rs;
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
/*      */   public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/*  105 */     if (columnNamePattern == null) {
/*  106 */       if (this.conn.getNullNamePatternMatchesAll()) {
/*  107 */         columnNamePattern = "%";
/*      */       } else {
/*  109 */         throw SQLError.createSQLException("Column name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  114 */     if ((catalog == null) && 
/*  115 */       (this.conn.getNullCatalogMeansCurrent())) {
/*  116 */       catalog = this.database;
/*      */     }
/*      */     
/*      */ 
/*  120 */     String sql = "SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM, TABLE_NAME,COLUMN_NAME, NULL AS GRANTOR, GRANTEE, PRIVILEGE_TYPE AS PRIVILEGE, IS_GRANTABLE FROM INFORMATION_SCHEMA.COLUMN_PRIVILEGES WHERE TABLE_SCHEMA LIKE ? AND TABLE_NAME =? AND COLUMN_NAME LIKE ? ORDER BY COLUMN_NAME, PRIVILEGE_TYPE";
/*      */     
/*      */ 
/*      */ 
/*  124 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/*  127 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */       
/*  129 */       if (catalog != null) {
/*  130 */         pStmt.setString(1, catalog);
/*      */       } else {
/*  132 */         pStmt.setString(1, "%");
/*      */       }
/*      */       
/*  135 */       pStmt.setString(2, table);
/*  136 */       pStmt.setString(3, columnNamePattern);
/*      */       
/*  138 */       ResultSet rs = executeMetadataQuery(pStmt);
/*  139 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(new Field[] { new Field("", "TABLE_CAT", 1, 64), new Field("", "TABLE_SCHEM", 1, 1), new Field("", "TABLE_NAME", 1, 64), new Field("", "COLUMN_NAME", 1, 64), new Field("", "GRANTOR", 1, 77), new Field("", "GRANTEE", 1, 77), new Field("", "PRIVILEGE", 1, 64), new Field("", "IS_GRANTABLE", 1, 3) });
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  144 */       return rs;
/*      */     } finally {
/*  146 */       if (pStmt != null) {
/*  147 */         pStmt.close();
/*      */       }
/*      */     }
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
/*      */   public ResultSet getColumns(String catalog, String schemaPattern, String tableName, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/*  192 */     if (columnNamePattern == null) {
/*  193 */       if (this.conn.getNullNamePatternMatchesAll()) {
/*  194 */         columnNamePattern = "%";
/*      */       } else {
/*  196 */         throw SQLError.createSQLException("Column name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  201 */     if ((catalog == null) && 
/*  202 */       (this.conn.getNullCatalogMeansCurrent())) {
/*  203 */       catalog = this.database;
/*      */     }
/*      */     
/*      */ 
/*  207 */     StringBuilder sqlBuf = new StringBuilder("SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM, TABLE_NAME, COLUMN_NAME,");
/*  208 */     MysqlDefs.appendJdbcTypeMappingQuery(sqlBuf, "DATA_TYPE");
/*      */     
/*  210 */     sqlBuf.append(" AS DATA_TYPE, ");
/*      */     
/*  212 */     if (this.conn.getCapitalizeTypeNames()) {
/*  213 */       sqlBuf.append("UPPER(CASE WHEN LOCATE('unsigned', COLUMN_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 AND LOCATE('set', DATA_TYPE) <> 1 AND LOCATE('enum', DATA_TYPE) <> 1 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END) AS TYPE_NAME,");
/*      */     }
/*      */     else {
/*  216 */       sqlBuf.append("CASE WHEN LOCATE('unsigned', COLUMN_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 AND LOCATE('set', DATA_TYPE) <> 1 AND LOCATE('enum', DATA_TYPE) <> 1 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END AS TYPE_NAME,");
/*      */     }
/*      */     
/*      */ 
/*  220 */     sqlBuf.append("CASE WHEN LCASE(DATA_TYPE)='date' THEN 10 WHEN LCASE(DATA_TYPE)='time' THEN 8 WHEN LCASE(DATA_TYPE)='datetime' THEN 19 WHEN LCASE(DATA_TYPE)='timestamp' THEN 19 WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION WHEN CHARACTER_MAXIMUM_LENGTH > 2147483647 THEN 2147483647 ELSE CHARACTER_MAXIMUM_LENGTH END AS COLUMN_SIZE, " + MysqlIO.getMaxBuf() + " AS BUFFER_LENGTH," + "NUMERIC_SCALE AS DECIMAL_DIGITS," + "10 AS NUM_PREC_RADIX," + "CASE WHEN IS_NULLABLE='NO' THEN " + 0 + " ELSE CASE WHEN IS_NULLABLE='YES' THEN " + 1 + " ELSE " + 2 + " END END AS NULLABLE," + "COLUMN_COMMENT AS REMARKS," + "COLUMN_DEFAULT AS COLUMN_DEF," + "0 AS SQL_DATA_TYPE," + "0 AS SQL_DATETIME_SUB," + "CASE WHEN CHARACTER_OCTET_LENGTH > " + Integer.MAX_VALUE + " THEN " + Integer.MAX_VALUE + " ELSE CHARACTER_OCTET_LENGTH END AS CHAR_OCTET_LENGTH," + "ORDINAL_POSITION," + "IS_NULLABLE," + "NULL AS SCOPE_CATALOG," + "NULL AS SCOPE_SCHEMA," + "NULL AS SCOPE_TABLE," + "NULL AS SOURCE_DATA_TYPE," + "IF (EXTRA LIKE '%auto_increment%','YES','NO') AS IS_AUTOINCREMENT, " + "IF (EXTRA LIKE '%GENERATED%','YES','NO') AS IS_GENERATEDCOLUMN FROM INFORMATION_SCHEMA.COLUMNS WHERE ");
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
/*  232 */     boolean operatingOnInformationSchema = "information_schema".equalsIgnoreCase(catalog);
/*      */     
/*  234 */     if (catalog != null) {
/*  235 */       if ((operatingOnInformationSchema) || ((StringUtils.indexOfIgnoreCase(0, catalog, "%") == -1) && (StringUtils.indexOfIgnoreCase(0, catalog, "_") == -1)))
/*      */       {
/*  237 */         sqlBuf.append("TABLE_SCHEMA = ? AND ");
/*      */       } else {
/*  239 */         sqlBuf.append("TABLE_SCHEMA LIKE ? AND ");
/*      */       }
/*      */     }
/*      */     else {
/*  243 */       sqlBuf.append("TABLE_SCHEMA LIKE ? AND ");
/*      */     }
/*      */     
/*  246 */     if (tableName != null) {
/*  247 */       if ((StringUtils.indexOfIgnoreCase(0, tableName, "%") == -1) && (StringUtils.indexOfIgnoreCase(0, tableName, "_") == -1)) {
/*  248 */         sqlBuf.append("TABLE_NAME = ? AND ");
/*      */       } else {
/*  250 */         sqlBuf.append("TABLE_NAME LIKE ? AND ");
/*      */       }
/*      */     }
/*      */     else {
/*  254 */       sqlBuf.append("TABLE_NAME LIKE ? AND ");
/*      */     }
/*      */     
/*  257 */     if ((StringUtils.indexOfIgnoreCase(0, columnNamePattern, "%") == -1) && (StringUtils.indexOfIgnoreCase(0, columnNamePattern, "_") == -1)) {
/*  258 */       sqlBuf.append("COLUMN_NAME = ? ");
/*      */     } else {
/*  260 */       sqlBuf.append("COLUMN_NAME LIKE ? ");
/*      */     }
/*  262 */     sqlBuf.append("ORDER BY TABLE_SCHEMA, TABLE_NAME, ORDINAL_POSITION");
/*      */     
/*  264 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/*  267 */       pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*      */       
/*  269 */       if (catalog != null) {
/*  270 */         pStmt.setString(1, catalog);
/*      */       } else {
/*  272 */         pStmt.setString(1, "%");
/*      */       }
/*      */       
/*  275 */       pStmt.setString(2, tableName);
/*  276 */       pStmt.setString(3, columnNamePattern);
/*      */       
/*  278 */       ResultSet rs = executeMetadataQuery(pStmt);
/*      */       
/*  280 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(createColumnsFields());
/*  281 */       return rs;
/*      */     } finally {
/*  283 */       if (pStmt != null) {
/*  284 */         pStmt.close();
/*      */       }
/*      */     }
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
/*      */   public ResultSet getCrossReference(String primaryCatalog, String primarySchema, String primaryTable, String foreignCatalog, String foreignSchema, String foreignTable)
/*      */     throws SQLException
/*      */   {
/*  346 */     if (primaryTable == null) {
/*  347 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/*  350 */     if ((primaryCatalog == null) && 
/*  351 */       (this.conn.getNullCatalogMeansCurrent())) {
/*  352 */       primaryCatalog = this.database;
/*      */     }
/*      */     
/*      */ 
/*  356 */     if ((foreignCatalog == null) && 
/*  357 */       (this.conn.getNullCatalogMeansCurrent())) {
/*  358 */       foreignCatalog = this.database;
/*      */     }
/*      */     
/*      */ 
/*  362 */     String sql = "SELECT A.REFERENCED_TABLE_SCHEMA AS PKTABLE_CAT,NULL AS PKTABLE_SCHEM, A.REFERENCED_TABLE_NAME AS PKTABLE_NAME,A.REFERENCED_COLUMN_NAME AS PKCOLUMN_NAME, A.TABLE_SCHEMA AS FKTABLE_CAT, NULL AS FKTABLE_SCHEM, A.TABLE_NAME AS FKTABLE_NAME, A.COLUMN_NAME AS FKCOLUMN_NAME, A.ORDINAL_POSITION AS KEY_SEQ," + generateUpdateRuleClause() + " AS UPDATE_RULE," + generateDeleteRuleClause() + " AS DELETE_RULE," + "A.CONSTRAINT_NAME AS FK_NAME," + "(SELECT CONSTRAINT_NAME FROM" + " INFORMATION_SCHEMA.TABLE_CONSTRAINTS" + " WHERE TABLE_SCHEMA = A.REFERENCED_TABLE_SCHEMA AND" + " TABLE_NAME = A.REFERENCED_TABLE_NAME AND" + " CONSTRAINT_TYPE IN ('UNIQUE','PRIMARY KEY') LIMIT 1)" + " AS PK_NAME," + 7 + " AS DEFERRABILITY " + "FROM " + "INFORMATION_SCHEMA.KEY_COLUMN_USAGE A JOIN " + "INFORMATION_SCHEMA.TABLE_CONSTRAINTS B " + "USING (TABLE_SCHEMA, TABLE_NAME, CONSTRAINT_NAME) " + generateOptionalRefContraintsJoin() + "WHERE " + "B.CONSTRAINT_TYPE = 'FOREIGN KEY' " + "AND A.REFERENCED_TABLE_SCHEMA LIKE ? AND A.REFERENCED_TABLE_NAME=? " + "AND A.TABLE_SCHEMA LIKE ? AND A.TABLE_NAME=? ORDER BY A.TABLE_SCHEMA, A.TABLE_NAME, A.ORDINAL_POSITION";
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
/*  373 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/*  376 */       pStmt = prepareMetaDataSafeStatement(sql);
/*  377 */       if (primaryCatalog != null) {
/*  378 */         pStmt.setString(1, primaryCatalog);
/*      */       } else {
/*  380 */         pStmt.setString(1, "%");
/*      */       }
/*      */       
/*  383 */       pStmt.setString(2, primaryTable);
/*      */       
/*  385 */       if (foreignCatalog != null) {
/*  386 */         pStmt.setString(3, foreignCatalog);
/*      */       } else {
/*  388 */         pStmt.setString(3, "%");
/*      */       }
/*      */       
/*  391 */       pStmt.setString(4, foreignTable);
/*      */       
/*  393 */       ResultSet rs = executeMetadataQuery(pStmt);
/*  394 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(createFkMetadataFields());
/*      */       
/*  396 */       return rs;
/*      */     } finally {
/*  398 */       if (pStmt != null) {
/*  399 */         pStmt.close();
/*      */       }
/*      */     }
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
/*      */   public ResultSet getExportedKeys(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/*  454 */     if (table == null) {
/*  455 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/*  458 */     if ((catalog == null) && 
/*  459 */       (this.conn.getNullCatalogMeansCurrent())) {
/*  460 */       catalog = this.database;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  466 */     String sql = "SELECT A.REFERENCED_TABLE_SCHEMA AS PKTABLE_CAT, NULL AS PKTABLE_SCHEM, A.REFERENCED_TABLE_NAME AS PKTABLE_NAME, A.REFERENCED_COLUMN_NAME AS PKCOLUMN_NAME, A.TABLE_SCHEMA AS FKTABLE_CAT, NULL AS FKTABLE_SCHEM, A.TABLE_NAME AS FKTABLE_NAME,A.COLUMN_NAME AS FKCOLUMN_NAME, A.ORDINAL_POSITION AS KEY_SEQ," + generateUpdateRuleClause() + " AS UPDATE_RULE," + generateDeleteRuleClause() + " AS DELETE_RULE," + "A.CONSTRAINT_NAME AS FK_NAME," + "(SELECT CONSTRAINT_NAME FROM" + " INFORMATION_SCHEMA.TABLE_CONSTRAINTS" + " WHERE TABLE_SCHEMA = A.REFERENCED_TABLE_SCHEMA AND" + " TABLE_NAME = A.REFERENCED_TABLE_NAME AND" + " CONSTRAINT_TYPE IN ('UNIQUE','PRIMARY KEY') LIMIT 1)" + " AS PK_NAME," + 7 + " AS DEFERRABILITY " + "FROM " + "INFORMATION_SCHEMA.KEY_COLUMN_USAGE A JOIN " + "INFORMATION_SCHEMA.TABLE_CONSTRAINTS B " + "USING (TABLE_SCHEMA, TABLE_NAME, CONSTRAINT_NAME) " + generateOptionalRefContraintsJoin() + "WHERE " + "B.CONSTRAINT_TYPE = 'FOREIGN KEY' " + "AND A.REFERENCED_TABLE_SCHEMA LIKE ? AND A.REFERENCED_TABLE_NAME=? " + "ORDER BY A.TABLE_SCHEMA, A.TABLE_NAME, A.ORDINAL_POSITION";
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  476 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/*  479 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */       
/*  481 */       if (catalog != null) {
/*  482 */         pStmt.setString(1, catalog);
/*      */       } else {
/*  484 */         pStmt.setString(1, "%");
/*      */       }
/*      */       
/*  487 */       pStmt.setString(2, table);
/*      */       
/*  489 */       ResultSet rs = executeMetadataQuery(pStmt);
/*      */       
/*  491 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(createFkMetadataFields());
/*      */       
/*  493 */       return rs;
/*      */     } finally {
/*  495 */       if (pStmt != null) {
/*  496 */         pStmt.close();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private String generateOptionalRefContraintsJoin()
/*      */   {
/*  503 */     return this.hasReferentialConstraintsView ? "JOIN INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS R ON (R.CONSTRAINT_NAME = B.CONSTRAINT_NAME AND R.TABLE_NAME = B.TABLE_NAME AND R.CONSTRAINT_SCHEMA = B.TABLE_SCHEMA) " : "";
/*      */   }
/*      */   
/*      */   private String generateDeleteRuleClause()
/*      */   {
/*  508 */     return this.hasReferentialConstraintsView ? "CASE WHEN R.DELETE_RULE='CASCADE' THEN " + String.valueOf(0) + " WHEN R.DELETE_RULE='SET NULL' THEN " + String.valueOf(2) + " WHEN R.DELETE_RULE='SET DEFAULT' THEN " + String.valueOf(4) + " WHEN R.DELETE_RULE='RESTRICT' THEN " + String.valueOf(1) + " WHEN R.DELETE_RULE='NO ACTION' THEN " + String.valueOf(3) + " ELSE " + String.valueOf(3) + " END " : String.valueOf(1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private String generateUpdateRuleClause()
/*      */   {
/*  516 */     return this.hasReferentialConstraintsView ? "CASE WHEN R.UPDATE_RULE='CASCADE' THEN " + String.valueOf(0) + " WHEN R.UPDATE_RULE='SET NULL' THEN " + String.valueOf(2) + " WHEN R.UPDATE_RULE='SET DEFAULT' THEN " + String.valueOf(4) + " WHEN R.UPDATE_RULE='RESTRICT' THEN " + String.valueOf(1) + " WHEN R.UPDATE_RULE='NO ACTION' THEN " + String.valueOf(3) + " ELSE " + String.valueOf(3) + " END " : String.valueOf(1);
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
/*      */   public ResultSet getImportedKeys(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/*  571 */     if (table == null) {
/*  572 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/*  575 */     if ((catalog == null) && 
/*  576 */       (this.conn.getNullCatalogMeansCurrent())) {
/*  577 */       catalog = this.database;
/*      */     }
/*      */     
/*      */ 
/*  581 */     String sql = "SELECT A.REFERENCED_TABLE_SCHEMA AS PKTABLE_CAT, NULL AS PKTABLE_SCHEM, A.REFERENCED_TABLE_NAME AS PKTABLE_NAME,A.REFERENCED_COLUMN_NAME AS PKCOLUMN_NAME, A.TABLE_SCHEMA AS FKTABLE_CAT, NULL AS FKTABLE_SCHEM, A.TABLE_NAME AS FKTABLE_NAME, A.COLUMN_NAME AS FKCOLUMN_NAME, A.ORDINAL_POSITION AS KEY_SEQ," + generateUpdateRuleClause() + " AS UPDATE_RULE," + generateDeleteRuleClause() + " AS DELETE_RULE," + "A.CONSTRAINT_NAME AS FK_NAME," + "(SELECT CONSTRAINT_NAME FROM" + " INFORMATION_SCHEMA.TABLE_CONSTRAINTS" + " WHERE TABLE_SCHEMA = A.REFERENCED_TABLE_SCHEMA AND" + " TABLE_NAME = A.REFERENCED_TABLE_NAME AND" + " CONSTRAINT_TYPE IN ('UNIQUE','PRIMARY KEY') LIMIT 1)" + " AS PK_NAME," + 7 + " AS DEFERRABILITY " + "FROM " + "INFORMATION_SCHEMA.KEY_COLUMN_USAGE A " + "JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS B USING " + "(CONSTRAINT_NAME, TABLE_NAME) " + generateOptionalRefContraintsJoin() + "WHERE " + "B.CONSTRAINT_TYPE = 'FOREIGN KEY' " + "AND A.TABLE_SCHEMA LIKE ? " + "AND A.TABLE_NAME=? " + "AND A.REFERENCED_TABLE_SCHEMA IS NOT NULL " + "ORDER BY A.REFERENCED_TABLE_SCHEMA, A.REFERENCED_TABLE_NAME, A.ORDINAL_POSITION";
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  591 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/*  594 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */       
/*  596 */       if (catalog != null) {
/*  597 */         pStmt.setString(1, catalog);
/*      */       } else {
/*  599 */         pStmt.setString(1, "%");
/*      */       }
/*      */       
/*  602 */       pStmt.setString(2, table);
/*      */       
/*  604 */       ResultSet rs = executeMetadataQuery(pStmt);
/*      */       
/*  606 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(createFkMetadataFields());
/*      */       
/*  608 */       return rs;
/*      */     } finally {
/*  610 */       if (pStmt != null) {
/*  611 */         pStmt.close();
/*      */       }
/*      */     }
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
/*      */   public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate)
/*      */     throws SQLException
/*      */   {
/*  665 */     StringBuilder sqlBuf = new StringBuilder("SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM, TABLE_NAME, NON_UNIQUE,");
/*  666 */     sqlBuf.append("TABLE_SCHEMA AS INDEX_QUALIFIER, INDEX_NAME,3 AS TYPE, SEQ_IN_INDEX AS ORDINAL_POSITION, COLUMN_NAME,");
/*  667 */     sqlBuf.append("COLLATION AS ASC_OR_DESC, CARDINALITY, NULL AS PAGES, NULL AS FILTER_CONDITION FROM INFORMATION_SCHEMA.STATISTICS WHERE ");
/*  668 */     sqlBuf.append("TABLE_SCHEMA LIKE ? AND TABLE_NAME LIKE ?");
/*      */     
/*  670 */     if (unique) {
/*  671 */       sqlBuf.append(" AND NON_UNIQUE=0 ");
/*      */     }
/*      */     
/*  674 */     sqlBuf.append("ORDER BY NON_UNIQUE, INDEX_NAME, SEQ_IN_INDEX");
/*      */     
/*  676 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/*  679 */       if ((catalog == null) && 
/*  680 */         (this.conn.getNullCatalogMeansCurrent())) {
/*  681 */         catalog = this.database;
/*      */       }
/*      */       
/*      */ 
/*  685 */       pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*      */       
/*  687 */       if (catalog != null) {
/*  688 */         pStmt.setString(1, catalog);
/*      */       } else {
/*  690 */         pStmt.setString(1, "%");
/*      */       }
/*      */       
/*  693 */       pStmt.setString(2, table);
/*      */       
/*  695 */       ResultSet rs = executeMetadataQuery(pStmt);
/*      */       
/*  697 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(createIndexInfoFields());
/*      */       
/*  699 */       return rs;
/*      */     } finally {
/*  701 */       if (pStmt != null) {
/*  702 */         pStmt.close();
/*      */       }
/*      */     }
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
/*      */   public ResultSet getPrimaryKeys(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/*  734 */     if ((catalog == null) && 
/*  735 */       (this.conn.getNullCatalogMeansCurrent())) {
/*  736 */       catalog = this.database;
/*      */     }
/*      */     
/*      */ 
/*  740 */     if (table == null) {
/*  741 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/*  744 */     String sql = "SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, SEQ_IN_INDEX AS KEY_SEQ, 'PRIMARY' AS PK_NAME FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA LIKE ? AND TABLE_NAME LIKE ? AND INDEX_NAME='PRIMARY' ORDER BY TABLE_SCHEMA, TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX";
/*      */     
/*      */ 
/*      */ 
/*  748 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/*  751 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */       
/*  753 */       if (catalog != null) {
/*  754 */         pStmt.setString(1, catalog);
/*      */       } else {
/*  756 */         pStmt.setString(1, "%");
/*      */       }
/*      */       
/*  759 */       pStmt.setString(2, table);
/*      */       
/*  761 */       ResultSet rs = executeMetadataQuery(pStmt);
/*  762 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(new Field[] { new Field("", "TABLE_CAT", 1, 255), new Field("", "TABLE_SCHEM", 1, 0), new Field("", "TABLE_NAME", 1, 255), new Field("", "COLUMN_NAME", 1, 32), new Field("", "KEY_SEQ", 5, 5), new Field("", "PK_NAME", 1, 32) });
/*      */       
/*      */ 
/*      */ 
/*  766 */       return rs;
/*      */     } finally {
/*  768 */       if (pStmt != null) {
/*  769 */         pStmt.close();
/*      */       }
/*      */     }
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
/*      */   public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern)
/*      */     throws SQLException
/*      */   {
/*  813 */     if ((procedureNamePattern == null) || (procedureNamePattern.length() == 0)) {
/*  814 */       if (this.conn.getNullNamePatternMatchesAll()) {
/*  815 */         procedureNamePattern = "%";
/*      */       } else {
/*  817 */         throw SQLError.createSQLException("Procedure name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  822 */     String db = null;
/*      */     
/*  824 */     if (catalog == null) {
/*  825 */       if (this.conn.getNullCatalogMeansCurrent()) {
/*  826 */         db = this.database;
/*      */       }
/*      */     } else {
/*  829 */       db = catalog;
/*      */     }
/*      */     
/*  832 */     String sql = "SELECT ROUTINE_SCHEMA AS PROCEDURE_CAT, NULL AS PROCEDURE_SCHEM, ROUTINE_NAME AS PROCEDURE_NAME, NULL AS RESERVED_1, NULL AS RESERVED_2, NULL AS RESERVED_3, ROUTINE_COMMENT AS REMARKS, CASE WHEN ROUTINE_TYPE = 'PROCEDURE' THEN 1 WHEN ROUTINE_TYPE='FUNCTION' THEN 2 ELSE 0 END AS PROCEDURE_TYPE, ROUTINE_NAME AS SPECIFIC_NAME FROM INFORMATION_SCHEMA.ROUTINES WHERE " + getRoutineTypeConditionForGetProcedures() + "ROUTINE_SCHEMA LIKE ? AND ROUTINE_NAME LIKE ? ORDER BY ROUTINE_SCHEMA, ROUTINE_NAME, ROUTINE_TYPE";
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  838 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/*  841 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */       
/*  843 */       if (db != null) {
/*  844 */         pStmt.setString(1, db);
/*      */       } else {
/*  846 */         pStmt.setString(1, "%");
/*      */       }
/*      */       
/*  849 */       pStmt.setString(2, procedureNamePattern);
/*      */       
/*  851 */       ResultSet rs = executeMetadataQuery(pStmt);
/*  852 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(createFieldMetadataForGetProcedures());
/*      */       
/*  854 */       return rs;
/*      */     } finally {
/*  856 */       if (pStmt != null) {
/*  857 */         pStmt.close();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String getRoutineTypeConditionForGetProcedures()
/*      */   {
/*  869 */     return "";
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
/*      */   public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/*  937 */     if (!this.hasParametersView) {
/*  938 */       return getProcedureColumnsNoISParametersView(catalog, schemaPattern, procedureNamePattern, columnNamePattern);
/*      */     }
/*      */     
/*  941 */     if ((procedureNamePattern == null) || (procedureNamePattern.length() == 0)) {
/*  942 */       if (this.conn.getNullNamePatternMatchesAll()) {
/*  943 */         procedureNamePattern = "%";
/*      */       } else {
/*  945 */         throw SQLError.createSQLException("Procedure name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  950 */     String db = null;
/*      */     
/*  952 */     if (catalog == null) {
/*  953 */       if (this.conn.getNullCatalogMeansCurrent()) {
/*  954 */         db = this.database;
/*      */       }
/*      */     } else {
/*  957 */       db = catalog;
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
/*  976 */     StringBuilder sqlBuf = new StringBuilder("SELECT SPECIFIC_SCHEMA AS PROCEDURE_CAT, NULL AS `PROCEDURE_SCHEM`, SPECIFIC_NAME AS `PROCEDURE_NAME`, IFNULL(PARAMETER_NAME, '') AS `COLUMN_NAME`, CASE WHEN PARAMETER_MODE = 'IN' THEN 1 WHEN PARAMETER_MODE = 'OUT' THEN 4 WHEN PARAMETER_MODE = 'INOUT' THEN 2 WHEN ORDINAL_POSITION = 0 THEN 5 ELSE 0 END AS `COLUMN_TYPE`, ");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  982 */     MysqlDefs.appendJdbcTypeMappingQuery(sqlBuf, "DATA_TYPE");
/*      */     
/*  984 */     sqlBuf.append(" AS `DATA_TYPE`, ");
/*      */     
/*      */ 
/*  987 */     if (this.conn.getCapitalizeTypeNames()) {
/*  988 */       sqlBuf.append("UPPER(CASE WHEN LOCATE('unsigned', DATA_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END) AS `TYPE_NAME`,");
/*      */     }
/*      */     else {
/*  991 */       sqlBuf.append("CASE WHEN LOCATE('unsigned', DATA_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END AS `TYPE_NAME`,");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  996 */     sqlBuf.append("NUMERIC_PRECISION AS `PRECISION`, ");
/*      */     
/*  998 */     sqlBuf.append("CASE WHEN LCASE(DATA_TYPE)='date' THEN 10 WHEN LCASE(DATA_TYPE)='time' THEN 8 WHEN LCASE(DATA_TYPE)='datetime' THEN 19 WHEN LCASE(DATA_TYPE)='timestamp' THEN 19 WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION WHEN CHARACTER_MAXIMUM_LENGTH > 2147483647 THEN 2147483647 ELSE CHARACTER_MAXIMUM_LENGTH END AS LENGTH, ");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1003 */     sqlBuf.append("NUMERIC_SCALE AS `SCALE`, ");
/*      */     
/* 1005 */     sqlBuf.append("10 AS RADIX,");
/* 1006 */     sqlBuf.append("1 AS `NULLABLE`, NULL AS `REMARKS`, NULL AS `COLUMN_DEF`, NULL AS `SQL_DATA_TYPE`, NULL AS `SQL_DATETIME_SUB`, CHARACTER_OCTET_LENGTH AS `CHAR_OCTET_LENGTH`, ORDINAL_POSITION, 'YES' AS `IS_NULLABLE`, SPECIFIC_NAME FROM INFORMATION_SCHEMA.PARAMETERS WHERE " + getRoutineTypeConditionForGetProcedureColumns() + "SPECIFIC_SCHEMA LIKE ? AND SPECIFIC_NAME LIKE ? AND (PARAMETER_NAME LIKE ? OR PARAMETER_NAME IS NULL) " + "ORDER BY SPECIFIC_SCHEMA, SPECIFIC_NAME, ROUTINE_TYPE, ORDINAL_POSITION");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1012 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/* 1015 */       pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*      */       
/* 1017 */       if (db != null) {
/* 1018 */         pStmt.setString(1, db);
/*      */       } else {
/* 1020 */         pStmt.setString(1, "%");
/*      */       }
/*      */       
/* 1023 */       pStmt.setString(2, procedureNamePattern);
/* 1024 */       pStmt.setString(3, columnNamePattern);
/*      */       
/* 1026 */       ResultSet rs = executeMetadataQuery(pStmt);
/* 1027 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(createProcedureColumnsFields());
/*      */       
/* 1029 */       return rs;
/*      */     } finally {
/* 1031 */       if (pStmt != null) {
/* 1032 */         pStmt.close();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ResultSet getProcedureColumnsNoISParametersView(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 1044 */     return super.getProcedureColumns(catalog, schemaPattern, procedureNamePattern, columnNamePattern);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String getRoutineTypeConditionForGetProcedureColumns()
/*      */   {
/* 1054 */     return "";
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
/*      */   public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types)
/*      */     throws SQLException
/*      */   {
/* 1092 */     if ((catalog == null) && 
/* 1093 */       (this.conn.getNullCatalogMeansCurrent())) {
/* 1094 */       catalog = this.database;
/*      */     }
/*      */     
/*      */ 
/* 1098 */     if (tableNamePattern == null) {
/* 1099 */       if (this.conn.getNullNamePatternMatchesAll()) {
/* 1100 */         tableNamePattern = "%";
/*      */       } else {
/* 1102 */         throw SQLError.createSQLException("Table name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1108 */     String tmpCat = "";
/*      */     
/* 1110 */     if ((catalog == null) || (catalog.length() == 0)) {
/* 1111 */       if (this.conn.getNullCatalogMeansCurrent()) {
/* 1112 */         tmpCat = this.database;
/*      */       }
/*      */     } else {
/* 1115 */       tmpCat = catalog;
/*      */     }
/*      */     
/* 1118 */     List<String> parseList = StringUtils.splitDBdotName(tableNamePattern, tmpCat, this.quotedId, this.conn.isNoBackslashEscapesSet());
/*      */     String tableNamePat;
/* 1120 */     String tableNamePat; if (parseList.size() == 2) {
/* 1121 */       tableNamePat = (String)parseList.get(1);
/*      */     } else {
/* 1123 */       tableNamePat = tableNamePattern;
/*      */     }
/*      */     
/* 1126 */     PreparedStatement pStmt = null;
/*      */     
/* 1128 */     String sql = "SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM, TABLE_NAME, CASE WHEN TABLE_TYPE='BASE TABLE' THEN CASE WHEN TABLE_SCHEMA = 'mysql' OR TABLE_SCHEMA = 'performance_schema' THEN 'SYSTEM TABLE' ELSE 'TABLE' END WHEN TABLE_TYPE='TEMPORARY' THEN 'LOCAL_TEMPORARY' ELSE TABLE_TYPE END AS TABLE_TYPE, TABLE_COMMENT AS REMARKS, NULL AS TYPE_CAT, NULL AS TYPE_SCHEM, NULL AS TYPE_NAME, NULL AS SELF_REFERENCING_COL_NAME, NULL AS REF_GENERATION FROM INFORMATION_SCHEMA.TABLES WHERE ";
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1134 */     boolean operatingOnInformationSchema = "information_schema".equalsIgnoreCase(catalog);
/* 1135 */     if (catalog != null) {
/* 1136 */       if ((operatingOnInformationSchema) || ((StringUtils.indexOfIgnoreCase(0, catalog, "%") == -1) && (StringUtils.indexOfIgnoreCase(0, catalog, "_") == -1)))
/*      */       {
/* 1138 */         sql = sql + "TABLE_SCHEMA = ? ";
/*      */       } else {
/* 1140 */         sql = sql + "TABLE_SCHEMA LIKE ? ";
/*      */       }
/*      */     }
/*      */     else {
/* 1144 */       sql = sql + "TABLE_SCHEMA LIKE ? ";
/*      */     }
/*      */     
/* 1147 */     if (tableNamePat != null) {
/* 1148 */       if ((StringUtils.indexOfIgnoreCase(0, tableNamePat, "%") == -1) && (StringUtils.indexOfIgnoreCase(0, tableNamePat, "_") == -1)) {
/* 1149 */         sql = sql + "AND TABLE_NAME = ? ";
/*      */       } else {
/* 1151 */         sql = sql + "AND TABLE_NAME LIKE ? ";
/*      */       }
/*      */     }
/*      */     else {
/* 1155 */       sql = sql + "AND TABLE_NAME LIKE ? ";
/*      */     }
/* 1157 */     sql = sql + "HAVING TABLE_TYPE IN (?,?,?,?,?) ";
/* 1158 */     sql = sql + "ORDER BY TABLE_TYPE, TABLE_SCHEMA, TABLE_NAME";
/*      */     try {
/* 1160 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */       
/* 1162 */       if (catalog != null) {
/* 1163 */         pStmt.setString(1, catalog);
/*      */       } else {
/* 1165 */         pStmt.setString(1, "%");
/*      */       }
/*      */       
/* 1168 */       pStmt.setString(2, tableNamePat);
/*      */       
/*      */       int i;
/* 1171 */       if ((types == null) || (types.length == 0)) {
/* 1172 */         DatabaseMetaData.TableType[] tableTypes = DatabaseMetaData.TableType.values();
/* 1173 */         for (int i = 0; i < 5; i++) {
/* 1174 */           pStmt.setString(3 + i, tableTypes[i].getName());
/*      */         }
/*      */       } else {
/* 1177 */         for (int i = 0; i < 5; i++) {
/* 1178 */           pStmt.setNull(3 + i, 12);
/*      */         }
/*      */         
/* 1181 */         int idx = 3;
/* 1182 */         for (i = 0; i < types.length; i++) {
/* 1183 */           DatabaseMetaData.TableType tableType = DatabaseMetaData.TableType.getTableTypeEqualTo(types[i]);
/* 1184 */           if (tableType != DatabaseMetaData.TableType.UNKNOWN) {
/* 1185 */             pStmt.setString(idx++, tableType.getName());
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1190 */       ResultSet rs = executeMetadataQuery(pStmt);
/*      */       
/* 1192 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(createTablesFields());
/*      */       
/* 1194 */       return rs;
/*      */     } finally {
/* 1196 */       if (pStmt != null) {
/* 1197 */         pStmt.close();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean gethasParametersView() {
/* 1203 */     return this.hasParametersView;
/*      */   }
/*      */   
/*      */   public ResultSet getVersionColumns(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/* 1209 */     if ((catalog == null) && 
/* 1210 */       (this.conn.getNullCatalogMeansCurrent())) {
/* 1211 */       catalog = this.database;
/*      */     }
/*      */     
/*      */ 
/* 1215 */     if (table == null) {
/* 1216 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/* 1219 */     StringBuilder sqlBuf = new StringBuilder("SELECT NULL AS SCOPE, COLUMN_NAME, ");
/*      */     
/* 1221 */     MysqlDefs.appendJdbcTypeMappingQuery(sqlBuf, "DATA_TYPE");
/* 1222 */     sqlBuf.append(" AS DATA_TYPE, ");
/*      */     
/* 1224 */     sqlBuf.append("COLUMN_TYPE AS TYPE_NAME, ");
/* 1225 */     sqlBuf.append("CASE WHEN LCASE(DATA_TYPE)='date' THEN 10 WHEN LCASE(DATA_TYPE)='time' THEN 8 WHEN LCASE(DATA_TYPE)='datetime' THEN 19 WHEN LCASE(DATA_TYPE)='timestamp' THEN 19 WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION WHEN CHARACTER_MAXIMUM_LENGTH > 2147483647 THEN 2147483647 ELSE CHARACTER_MAXIMUM_LENGTH END AS COLUMN_SIZE, ");
/*      */     
/*      */ 
/*      */ 
/* 1229 */     sqlBuf.append(MysqlIO.getMaxBuf() + " AS BUFFER_LENGTH,NUMERIC_SCALE AS DECIMAL_DIGITS, " + Integer.toString(1) + " AS PSEUDO_COLUMN FROM INFORMATION_SCHEMA.COLUMNS " + "WHERE TABLE_SCHEMA LIKE ? AND TABLE_NAME LIKE ? AND EXTRA LIKE '%on update CURRENT_TIMESTAMP%'");
/*      */     
/*      */ 
/*      */ 
/* 1233 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/* 1236 */       pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*      */       
/* 1238 */       if (catalog != null) {
/* 1239 */         pStmt.setString(1, catalog);
/*      */       } else {
/* 1241 */         pStmt.setString(1, "%");
/*      */       }
/*      */       
/* 1244 */       pStmt.setString(2, table);
/*      */       
/* 1246 */       ResultSet rs = executeMetadataQuery(pStmt);
/* 1247 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(new Field[] { new Field("", "SCOPE", 5, 5), new Field("", "COLUMN_NAME", 1, 32), new Field("", "DATA_TYPE", 4, 5), new Field("", "TYPE_NAME", 1, 16), new Field("", "COLUMN_SIZE", 4, 16), new Field("", "BUFFER_LENGTH", 4, 16), new Field("", "DECIMAL_DIGITS", 5, 16), new Field("", "PSEUDO_COLUMN", 5, 5) });
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1252 */       return rs;
/*      */     } finally {
/* 1254 */       if (pStmt != null) {
/* 1255 */         pStmt.close();
/*      */       }
/*      */     }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 1347 */     if (!this.hasParametersView) {
/* 1348 */       return super.getFunctionColumns(catalog, schemaPattern, functionNamePattern, columnNamePattern);
/*      */     }
/*      */     
/* 1351 */     if ((functionNamePattern == null) || (functionNamePattern.length() == 0)) {
/* 1352 */       if (this.conn.getNullNamePatternMatchesAll()) {
/* 1353 */         functionNamePattern = "%";
/*      */       } else {
/* 1355 */         throw SQLError.createSQLException("Procedure name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1360 */     String db = null;
/*      */     
/* 1362 */     if (catalog == null) {
/* 1363 */       if (this.conn.getNullCatalogMeansCurrent()) {
/* 1364 */         db = this.database;
/*      */       }
/*      */     } else {
/* 1367 */       db = catalog;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1375 */     StringBuilder sqlBuf = new StringBuilder("SELECT SPECIFIC_SCHEMA AS FUNCTION_CAT, NULL AS `FUNCTION_SCHEM`, SPECIFIC_NAME AS `FUNCTION_NAME`, ");
/* 1376 */     sqlBuf.append("IFNULL(PARAMETER_NAME, '') AS `COLUMN_NAME`, CASE WHEN PARAMETER_MODE = 'IN' THEN ");
/* 1377 */     sqlBuf.append(getJDBC4FunctionConstant(JDBC4FunctionConstant.FUNCTION_COLUMN_IN));
/* 1378 */     sqlBuf.append(" WHEN PARAMETER_MODE = 'OUT' THEN ");
/* 1379 */     sqlBuf.append(getJDBC4FunctionConstant(JDBC4FunctionConstant.FUNCTION_COLUMN_OUT));
/* 1380 */     sqlBuf.append(" WHEN PARAMETER_MODE = 'INOUT' THEN ");
/* 1381 */     sqlBuf.append(getJDBC4FunctionConstant(JDBC4FunctionConstant.FUNCTION_COLUMN_INOUT));
/* 1382 */     sqlBuf.append(" WHEN ORDINAL_POSITION = 0 THEN ");
/* 1383 */     sqlBuf.append(getJDBC4FunctionConstant(JDBC4FunctionConstant.FUNCTION_COLUMN_RETURN));
/* 1384 */     sqlBuf.append(" ELSE ");
/* 1385 */     sqlBuf.append(getJDBC4FunctionConstant(JDBC4FunctionConstant.FUNCTION_COLUMN_UNKNOWN));
/* 1386 */     sqlBuf.append(" END AS `COLUMN_TYPE`, ");
/*      */     
/*      */ 
/* 1389 */     MysqlDefs.appendJdbcTypeMappingQuery(sqlBuf, "DATA_TYPE");
/*      */     
/* 1391 */     sqlBuf.append(" AS `DATA_TYPE`, ");
/*      */     
/*      */ 
/* 1394 */     if (this.conn.getCapitalizeTypeNames()) {
/* 1395 */       sqlBuf.append("UPPER(CASE WHEN LOCATE('unsigned', DATA_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END) AS `TYPE_NAME`,");
/*      */     }
/*      */     else {
/* 1398 */       sqlBuf.append("CASE WHEN LOCATE('unsigned', DATA_TYPE) != 0 AND LOCATE('unsigned', DATA_TYPE) = 0 THEN CONCAT(DATA_TYPE, ' unsigned') ELSE DATA_TYPE END AS `TYPE_NAME`,");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1403 */     sqlBuf.append("NUMERIC_PRECISION AS `PRECISION`, ");
/*      */     
/* 1405 */     sqlBuf.append("CASE WHEN LCASE(DATA_TYPE)='date' THEN 10 WHEN LCASE(DATA_TYPE)='time' THEN 8 WHEN LCASE(DATA_TYPE)='datetime' THEN 19 WHEN LCASE(DATA_TYPE)='timestamp' THEN 19 WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION WHEN CHARACTER_MAXIMUM_LENGTH > 2147483647 THEN 2147483647 ELSE CHARACTER_MAXIMUM_LENGTH END AS LENGTH, ");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1410 */     sqlBuf.append("NUMERIC_SCALE AS `SCALE`, ");
/*      */     
/* 1412 */     sqlBuf.append("10 AS RADIX,");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1419 */     sqlBuf.append(getJDBC4FunctionConstant(JDBC4FunctionConstant.FUNCTION_NULLABLE) + " AS `NULLABLE`,  NULL AS `REMARKS`, " + "CHARACTER_OCTET_LENGTH AS `CHAR_OCTET_LENGTH`,  ORDINAL_POSITION, 'YES' AS `IS_NULLABLE`, SPECIFIC_NAME " + "FROM INFORMATION_SCHEMA.PARAMETERS WHERE " + "SPECIFIC_SCHEMA LIKE ? AND SPECIFIC_NAME LIKE ? AND (PARAMETER_NAME LIKE ? OR PARAMETER_NAME IS NULL) " + "AND ROUTINE_TYPE='FUNCTION' ORDER BY SPECIFIC_SCHEMA, SPECIFIC_NAME, ORDINAL_POSITION");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1425 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/* 1428 */       pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*      */       
/* 1430 */       if (db != null) {
/* 1431 */         pStmt.setString(1, db);
/*      */       } else {
/* 1433 */         pStmt.setString(1, "%");
/*      */       }
/*      */       
/* 1436 */       pStmt.setString(2, functionNamePattern);
/* 1437 */       pStmt.setString(3, columnNamePattern);
/*      */       
/* 1439 */       ResultSet rs = executeMetadataQuery(pStmt);
/* 1440 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(createFunctionColumnsFields());
/*      */       
/* 1442 */       return rs;
/*      */     } finally {
/* 1444 */       if (pStmt != null) {
/* 1445 */         pStmt.close();
/*      */       }
/*      */     }
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
/*      */   protected int getJDBC4FunctionConstant(JDBC4FunctionConstant constant)
/*      */   {
/* 1460 */     return 0;
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
/*      */   public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern)
/*      */     throws SQLException
/*      */   {
/* 1510 */     if ((functionNamePattern == null) || (functionNamePattern.length() == 0)) {
/* 1511 */       if (this.conn.getNullNamePatternMatchesAll()) {
/* 1512 */         functionNamePattern = "%";
/*      */       } else {
/* 1514 */         throw SQLError.createSQLException("Function name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1519 */     String db = null;
/*      */     
/* 1521 */     if (catalog == null) {
/* 1522 */       if (this.conn.getNullCatalogMeansCurrent()) {
/* 1523 */         db = this.database;
/*      */       }
/*      */     } else {
/* 1526 */       db = catalog;
/*      */     }
/*      */     
/* 1529 */     String sql = "SELECT ROUTINE_SCHEMA AS FUNCTION_CAT, NULL AS FUNCTION_SCHEM, ROUTINE_NAME AS FUNCTION_NAME, ROUTINE_COMMENT AS REMARKS, " + getJDBC4FunctionNoTableConstant() + " AS FUNCTION_TYPE, ROUTINE_NAME AS SPECIFIC_NAME FROM INFORMATION_SCHEMA.ROUTINES " + "WHERE ROUTINE_TYPE LIKE 'FUNCTION' AND ROUTINE_SCHEMA LIKE ? AND " + "ROUTINE_NAME LIKE ? ORDER BY FUNCTION_CAT, FUNCTION_SCHEM, FUNCTION_NAME, SPECIFIC_NAME";
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1534 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/* 1537 */       pStmt = prepareMetaDataSafeStatement(sql);
/*      */       
/* 1539 */       pStmt.setString(1, db != null ? db : "%");
/* 1540 */       pStmt.setString(2, functionNamePattern);
/*      */       
/* 1542 */       ResultSet rs = executeMetadataQuery(pStmt);
/* 1543 */       ((ResultSetInternalMethods)rs).redefineFieldsForDBMD(new Field[] { new Field("", "FUNCTION_CAT", 1, 255), new Field("", "FUNCTION_SCHEM", 1, 255), new Field("", "FUNCTION_NAME", 1, 255), new Field("", "REMARKS", 1, 255), new Field("", "FUNCTION_TYPE", 5, 6), new Field("", "SPECIFIC_NAME", 1, 255) });
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1548 */       return rs;
/*      */     } finally {
/* 1550 */       if (pStmt != null) {
/* 1551 */         pStmt.close();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int getJDBC4FunctionNoTableConstant()
/*      */   {
/* 1564 */     return 0;
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\DatabaseMetaDataUsingInfoSchema.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */