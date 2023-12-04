/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.profiler.ProfilerEventHandler;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.math.BigDecimal;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.Date;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.TreeMap;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class UpdatableResultSet
/*      */   extends ResultSetImpl
/*      */ {
/*   42 */   static final byte[] STREAM_DATA_MARKER = StringUtils.getBytes("** STREAM DATA **");
/*      */   
/*      */ 
/*      */   protected SingleByteCharsetConverter charConverter;
/*      */   
/*      */ 
/*      */   private String charEncoding;
/*      */   
/*      */   private byte[][] defaultColumnValue;
/*      */   
/*   52 */   private PreparedStatement deleter = null;
/*      */   
/*   54 */   private String deleteSQL = null;
/*      */   
/*   56 */   private boolean initializedCharConverter = false;
/*      */   
/*      */ 
/*   59 */   protected PreparedStatement inserter = null;
/*      */   
/*   61 */   private String insertSQL = null;
/*      */   
/*      */ 
/*   64 */   private boolean isUpdatable = false;
/*      */   
/*      */ 
/*   67 */   private String notUpdatableReason = null;
/*      */   
/*      */ 
/*   70 */   private List<Integer> primaryKeyIndicies = null;
/*      */   
/*      */   private String qualifiedAndQuotedTableName;
/*      */   
/*   74 */   private String quotedIdChar = null;
/*      */   
/*      */ 
/*      */   private PreparedStatement refresher;
/*      */   
/*   79 */   private String refreshSQL = null;
/*      */   
/*      */ 
/*      */   private ResultSetRow savedCurrentRow;
/*      */   
/*      */ 
/*   85 */   protected PreparedStatement updater = null;
/*      */   
/*      */ 
/*   88 */   private String updateSQL = null;
/*      */   
/*   90 */   private boolean populateInserterWithDefaultValues = false;
/*      */   
/*   92 */   private Map<String, Map<String, Map<String, Integer>>> databasesUsedToTablesUsed = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected UpdatableResultSet(String catalog, Field[] fields, RowData tuples, MySQLConnection conn, StatementImpl creatorStmt)
/*      */     throws SQLException
/*      */   {
/*  110 */     super(catalog, fields, tuples, conn, creatorStmt);
/*  111 */     checkUpdatability();
/*  112 */     this.populateInserterWithDefaultValues = this.connection.getPopulateInsertRowWithDefaultValues();
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
/*      */   public synchronized boolean absolute(int row)
/*      */     throws SQLException
/*      */   {
/*  149 */     return super.absolute(row);
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
/*      */   public synchronized void afterLast()
/*      */     throws SQLException
/*      */   {
/*  165 */     super.afterLast();
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
/*      */   public synchronized void beforeFirst()
/*      */     throws SQLException
/*      */   {
/*  181 */     super.beforeFirst();
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
/*      */   public synchronized void cancelRowUpdates()
/*      */     throws SQLException
/*      */   {
/*  196 */     checkClosed();
/*      */     
/*  198 */     if (this.doingUpdates) {
/*  199 */       this.doingUpdates = false;
/*  200 */       this.updater.clearParameters();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected synchronized void checkRowPos()
/*      */     throws SQLException
/*      */   {
/*  211 */     checkClosed();
/*      */     
/*  213 */     if (!this.onInsertRow) {
/*  214 */       super.checkRowPos();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void checkUpdatability()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/*  225 */       if (this.fields == null)
/*      */       {
/*      */ 
/*      */ 
/*  229 */         return;
/*      */       }
/*      */       
/*  232 */       String singleTableName = null;
/*  233 */       String catalogName = null;
/*      */       
/*  235 */       int primaryKeyCount = 0;
/*      */       
/*      */ 
/*      */ 
/*  239 */       if ((this.catalog == null) || (this.catalog.length() == 0)) {
/*  240 */         this.catalog = this.fields[0].getDatabaseName();
/*      */         
/*  242 */         if ((this.catalog == null) || (this.catalog.length() == 0)) {
/*  243 */           throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.43"), "S1009", getExceptionInterceptor());
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  248 */       if (this.fields.length > 0) {
/*  249 */         singleTableName = this.fields[0].getOriginalTableName();
/*  250 */         catalogName = this.fields[0].getDatabaseName();
/*      */         
/*  252 */         if (singleTableName == null) {
/*  253 */           singleTableName = this.fields[0].getTableName();
/*  254 */           catalogName = this.catalog;
/*      */         }
/*      */         
/*  257 */         if ((singleTableName != null) && (singleTableName.length() == 0)) {
/*  258 */           this.isUpdatable = false;
/*  259 */           this.notUpdatableReason = Messages.getString("NotUpdatableReason.3");
/*      */           
/*  261 */           return;
/*      */         }
/*      */         
/*  264 */         if (this.fields[0].isPrimaryKey()) {
/*  265 */           primaryKeyCount++;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  271 */         for (int i = 1; i < this.fields.length; i++) {
/*  272 */           String otherTableName = this.fields[i].getOriginalTableName();
/*  273 */           String otherCatalogName = this.fields[i].getDatabaseName();
/*      */           
/*  275 */           if (otherTableName == null) {
/*  276 */             otherTableName = this.fields[i].getTableName();
/*  277 */             otherCatalogName = this.catalog;
/*      */           }
/*      */           
/*  280 */           if ((otherTableName != null) && (otherTableName.length() == 0)) {
/*  281 */             this.isUpdatable = false;
/*  282 */             this.notUpdatableReason = Messages.getString("NotUpdatableReason.3");
/*      */             
/*  284 */             return;
/*      */           }
/*      */           
/*  287 */           if ((singleTableName == null) || (!otherTableName.equals(singleTableName))) {
/*  288 */             this.isUpdatable = false;
/*  289 */             this.notUpdatableReason = Messages.getString("NotUpdatableReason.0");
/*      */             
/*  291 */             return;
/*      */           }
/*      */           
/*      */ 
/*  295 */           if ((catalogName == null) || (!otherCatalogName.equals(catalogName))) {
/*  296 */             this.isUpdatable = false;
/*  297 */             this.notUpdatableReason = Messages.getString("NotUpdatableReason.1");
/*      */             
/*  299 */             return;
/*      */           }
/*      */           
/*  302 */           if (this.fields[i].isPrimaryKey()) {
/*  303 */             primaryKeyCount++;
/*      */           }
/*      */         }
/*      */         
/*  307 */         if ((singleTableName == null) || (singleTableName.length() == 0)) {
/*  308 */           this.isUpdatable = false;
/*  309 */           this.notUpdatableReason = Messages.getString("NotUpdatableReason.2");
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  314 */         this.isUpdatable = false;
/*  315 */         this.notUpdatableReason = Messages.getString("NotUpdatableReason.3");
/*      */         
/*  317 */         return;
/*      */       }
/*      */       
/*  320 */       if (this.connection.getStrictUpdates()) {
/*  321 */         DatabaseMetaData dbmd = this.connection.getMetaData();
/*      */         
/*  323 */         ResultSet rs = null;
/*  324 */         HashMap<String, String> primaryKeyNames = new HashMap();
/*      */         try
/*      */         {
/*  327 */           rs = dbmd.getPrimaryKeys(catalogName, null, singleTableName);
/*      */           
/*  329 */           while (rs.next()) {
/*  330 */             String keyName = rs.getString(4);
/*  331 */             keyName = keyName.toUpperCase();
/*  332 */             primaryKeyNames.put(keyName, keyName);
/*      */           }
/*      */         } finally {
/*  335 */           if (rs != null) {
/*      */             try {
/*  337 */               rs.close();
/*      */             } catch (Exception ex) {
/*  339 */               AssertionFailedException.shouldNotHappen(ex);
/*      */             }
/*      */             
/*  342 */             rs = null;
/*      */           }
/*      */         }
/*      */         
/*  346 */         int existingPrimaryKeysCount = primaryKeyNames.size();
/*      */         
/*  348 */         if (existingPrimaryKeysCount == 0) {
/*  349 */           this.isUpdatable = false;
/*  350 */           this.notUpdatableReason = Messages.getString("NotUpdatableReason.5");
/*      */           
/*  352 */           return;
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  358 */         for (int i = 0; i < this.fields.length; i++) {
/*  359 */           if (this.fields[i].isPrimaryKey()) {
/*  360 */             String columnNameUC = this.fields[i].getName().toUpperCase();
/*      */             
/*  362 */             if (primaryKeyNames.remove(columnNameUC) == null)
/*      */             {
/*  364 */               String originalName = this.fields[i].getOriginalName();
/*      */               
/*  366 */               if ((originalName != null) && 
/*  367 */                 (primaryKeyNames.remove(originalName.toUpperCase()) == null))
/*      */               {
/*  369 */                 this.isUpdatable = false;
/*  370 */                 this.notUpdatableReason = Messages.getString("NotUpdatableReason.6", new Object[] { originalName });
/*      */                 
/*  372 */                 return;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  379 */         this.isUpdatable = primaryKeyNames.isEmpty();
/*      */         
/*  381 */         if (!this.isUpdatable) {
/*  382 */           if (existingPrimaryKeysCount > 1) {
/*  383 */             this.notUpdatableReason = Messages.getString("NotUpdatableReason.7");
/*      */           } else {
/*  385 */             this.notUpdatableReason = Messages.getString("NotUpdatableReason.4");
/*      */           }
/*      */           
/*  388 */           return;
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  395 */       if (primaryKeyCount == 0) {
/*  396 */         this.isUpdatable = false;
/*  397 */         this.notUpdatableReason = Messages.getString("NotUpdatableReason.4");
/*      */         
/*  399 */         return;
/*      */       }
/*      */       
/*  402 */       this.isUpdatable = true;
/*  403 */       this.notUpdatableReason = null;
/*      */       
/*  405 */       return;
/*      */     } catch (SQLException sqlEx) {
/*  407 */       this.isUpdatable = false;
/*  408 */       this.notUpdatableReason = sqlEx.getMessage();
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
/*      */   public synchronized void deleteRow()
/*      */     throws SQLException
/*      */   {
/*  424 */     checkClosed();
/*      */     
/*  426 */     if (!this.isUpdatable) {
/*  427 */       throw new NotUpdatable(this.notUpdatableReason);
/*      */     }
/*      */     
/*  430 */     if (this.onInsertRow)
/*  431 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.1"), getExceptionInterceptor());
/*  432 */     if (this.rowData.size() == 0)
/*  433 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.2"), getExceptionInterceptor());
/*  434 */     if (isBeforeFirst())
/*  435 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.3"), getExceptionInterceptor());
/*  436 */     if (isAfterLast()) {
/*  437 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.4"), getExceptionInterceptor());
/*      */     }
/*      */     
/*  440 */     if (this.deleter == null) {
/*  441 */       if (this.deleteSQL == null) {
/*  442 */         generateStatements();
/*      */       }
/*      */       
/*  445 */       this.deleter = ((PreparedStatement)this.connection.clientPrepareStatement(this.deleteSQL));
/*      */     }
/*      */     
/*  448 */     this.deleter.clearParameters();
/*      */     
/*  450 */     int numKeys = this.primaryKeyIndicies.size();
/*      */     
/*  452 */     if (numKeys == 1) {
/*  453 */       int index = ((Integer)this.primaryKeyIndicies.get(0)).intValue();
/*  454 */       setParamValue(this.deleter, 1, this.thisRow, index, this.fields[index].getSQLType());
/*      */     } else {
/*  456 */       for (int i = 0; i < numKeys; i++) {
/*  457 */         int index = ((Integer)this.primaryKeyIndicies.get(i)).intValue();
/*  458 */         setParamValue(this.deleter, i + 1, this.thisRow, index, this.fields[index].getSQLType());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  463 */     this.deleter.executeUpdate();
/*  464 */     this.rowData.removeRow(this.rowData.getCurrentRowNumber());
/*      */     
/*      */ 
/*  467 */     previous();
/*      */   }
/*      */   
/*      */   private synchronized void setParamValue(PreparedStatement ps, int psIdx, ResultSetRow row, int rsIdx, int sqlType)
/*      */     throws SQLException
/*      */   {
/*  473 */     byte[] val = row.getColumnValue(rsIdx);
/*  474 */     if (val == null) {
/*  475 */       ps.setNull(psIdx, 0);
/*  476 */       return;
/*      */     }
/*  478 */     switch (sqlType) {
/*      */     case 0: 
/*  480 */       ps.setNull(psIdx, 0);
/*  481 */       break;
/*      */     case -6: 
/*      */     case 4: 
/*      */     case 5: 
/*  485 */       ps.setInt(psIdx, row.getInt(rsIdx));
/*  486 */       break;
/*      */     case -5: 
/*  488 */       ps.setLong(psIdx, row.getLong(rsIdx));
/*  489 */       break;
/*      */     case -1: 
/*      */     case 1: 
/*      */     case 2: 
/*      */     case 3: 
/*      */     case 12: 
/*  495 */       ps.setString(psIdx, row.getString(rsIdx, this.charEncoding, this.connection));
/*  496 */       break;
/*      */     case 91: 
/*  498 */       ps.setDate(psIdx, row.getDateFast(rsIdx, this.connection, this, this.fastDefaultCal), this.fastDefaultCal);
/*  499 */       break;
/*      */     case 93: 
/*  501 */       ps.setTimestamp(psIdx, row.getTimestampFast(rsIdx, this.fastDefaultCal, this.connection.getDefaultTimeZone(), false, this.connection, this));
/*  502 */       break;
/*      */     case 92: 
/*  504 */       ps.setTime(psIdx, row.getTimeFast(rsIdx, this.fastDefaultCal, this.connection.getDefaultTimeZone(), false, this.connection, this));
/*  505 */       break;
/*      */     case 6: 
/*      */     case 7: 
/*      */     case 8: 
/*      */     case 16: 
/*  510 */       ps.setBytesNoEscapeNoQuotes(psIdx, val);
/*  511 */       break;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     default: 
/*  518 */       ps.setBytes(psIdx, val);
/*      */     }
/*      */   }
/*      */   
/*      */   private synchronized void extractDefaultValues()
/*      */     throws SQLException
/*      */   {
/*  525 */     DatabaseMetaData dbmd = this.connection.getMetaData();
/*  526 */     this.defaultColumnValue = new byte[this.fields.length][];
/*      */     
/*  528 */     ResultSet columnsResultSet = null;
/*      */     
/*  530 */     for (Entry<String, Map<String, Map<String, Integer>>> dbEntry : this.databasesUsedToTablesUsed.entrySet())
/*      */     {
/*  532 */       for (Entry<String, Map<String, Integer>> tableEntry : ((Map)dbEntry.getValue()).entrySet()) {
/*  533 */         String tableName = (String)tableEntry.getKey();
/*  534 */         Map<String, Integer> columnNamesToIndices = (Map)tableEntry.getValue();
/*      */         try
/*      */         {
/*  537 */           columnsResultSet = dbmd.getColumns(this.catalog, null, tableName, "%");
/*      */           
/*  539 */           while (columnsResultSet.next()) {
/*  540 */             String columnName = columnsResultSet.getString("COLUMN_NAME");
/*  541 */             byte[] defaultValue = columnsResultSet.getBytes("COLUMN_DEF");
/*      */             
/*  543 */             if (columnNamesToIndices.containsKey(columnName)) {
/*  544 */               int localColumnIndex = ((Integer)columnNamesToIndices.get(columnName)).intValue();
/*      */               
/*  546 */               this.defaultColumnValue[localColumnIndex] = defaultValue;
/*      */             }
/*      */           }
/*      */         } finally {
/*  550 */           if (columnsResultSet != null) {
/*  551 */             columnsResultSet.close();
/*      */             
/*  553 */             columnsResultSet = null;
/*      */           }
/*      */         }
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
/*      */   public synchronized boolean first()
/*      */     throws SQLException
/*      */   {
/*  575 */     return super.first();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected synchronized void generateStatements()
/*      */     throws SQLException
/*      */   {
/*  586 */     if (!this.isUpdatable) {
/*  587 */       this.doingUpdates = false;
/*  588 */       this.onInsertRow = false;
/*      */       
/*  590 */       throw new NotUpdatable(this.notUpdatableReason);
/*      */     }
/*      */     
/*  593 */     String quotedId = getQuotedIdChar();
/*      */     
/*  595 */     Map<String, String> tableNamesSoFar = null;
/*      */     
/*  597 */     if (this.connection.lowerCaseTableNames()) {
/*  598 */       tableNamesSoFar = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*  599 */       this.databasesUsedToTablesUsed = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*      */     } else {
/*  601 */       tableNamesSoFar = new TreeMap();
/*  602 */       this.databasesUsedToTablesUsed = new TreeMap();
/*      */     }
/*      */     
/*  605 */     this.primaryKeyIndicies = new ArrayList();
/*      */     
/*  607 */     StringBuilder fieldValues = new StringBuilder();
/*  608 */     StringBuilder keyValues = new StringBuilder();
/*  609 */     StringBuilder columnNames = new StringBuilder();
/*  610 */     StringBuilder insertPlaceHolders = new StringBuilder();
/*  611 */     StringBuilder allTablesBuf = new StringBuilder();
/*  612 */     Map<Integer, String> columnIndicesToTable = new HashMap();
/*      */     
/*  614 */     boolean firstTime = true;
/*  615 */     boolean keysFirstTime = true;
/*      */     
/*  617 */     String equalsStr = this.connection.versionMeetsMinimum(3, 23, 0) ? "<=>" : "=";
/*      */     
/*  619 */     for (int i = 0; i < this.fields.length; i++) {
/*  620 */       StringBuilder tableNameBuffer = new StringBuilder();
/*  621 */       Map<String, Integer> updColumnNameToIndex = null;
/*      */       
/*      */ 
/*  624 */       if (this.fields[i].getOriginalTableName() != null)
/*      */       {
/*  626 */         String databaseName = this.fields[i].getDatabaseName();
/*      */         
/*  628 */         if ((databaseName != null) && (databaseName.length() > 0)) {
/*  629 */           tableNameBuffer.append(quotedId);
/*  630 */           tableNameBuffer.append(databaseName);
/*  631 */           tableNameBuffer.append(quotedId);
/*  632 */           tableNameBuffer.append('.');
/*      */         }
/*      */         
/*  635 */         String tableOnlyName = this.fields[i].getOriginalTableName();
/*      */         
/*  637 */         tableNameBuffer.append(quotedId);
/*  638 */         tableNameBuffer.append(tableOnlyName);
/*  639 */         tableNameBuffer.append(quotedId);
/*      */         
/*  641 */         String fqTableName = tableNameBuffer.toString();
/*      */         
/*  643 */         if (!tableNamesSoFar.containsKey(fqTableName)) {
/*  644 */           if (!tableNamesSoFar.isEmpty()) {
/*  645 */             allTablesBuf.append(',');
/*      */           }
/*      */           
/*  648 */           allTablesBuf.append(fqTableName);
/*  649 */           tableNamesSoFar.put(fqTableName, fqTableName);
/*      */         }
/*      */         
/*  652 */         columnIndicesToTable.put(Integer.valueOf(i), fqTableName);
/*      */         
/*  654 */         updColumnNameToIndex = getColumnsToIndexMapForTableAndDB(databaseName, tableOnlyName);
/*      */       } else {
/*  656 */         String tableOnlyName = this.fields[i].getTableName();
/*      */         
/*  658 */         if (tableOnlyName != null) {
/*  659 */           tableNameBuffer.append(quotedId);
/*  660 */           tableNameBuffer.append(tableOnlyName);
/*  661 */           tableNameBuffer.append(quotedId);
/*      */           
/*  663 */           String fqTableName = tableNameBuffer.toString();
/*      */           
/*  665 */           if (!tableNamesSoFar.containsKey(fqTableName)) {
/*  666 */             if (!tableNamesSoFar.isEmpty()) {
/*  667 */               allTablesBuf.append(',');
/*      */             }
/*      */             
/*  670 */             allTablesBuf.append(fqTableName);
/*  671 */             tableNamesSoFar.put(fqTableName, fqTableName);
/*      */           }
/*      */           
/*  674 */           columnIndicesToTable.put(Integer.valueOf(i), fqTableName);
/*      */           
/*  676 */           updColumnNameToIndex = getColumnsToIndexMapForTableAndDB(this.catalog, tableOnlyName);
/*      */         }
/*      */       }
/*      */       
/*  680 */       String originalColumnName = this.fields[i].getOriginalName();
/*  681 */       String columnName = null;
/*      */       
/*  683 */       if ((this.connection.getIO().hasLongColumnInfo()) && (originalColumnName != null) && (originalColumnName.length() > 0)) {
/*  684 */         columnName = originalColumnName;
/*      */       } else {
/*  686 */         columnName = this.fields[i].getName();
/*      */       }
/*      */       
/*  689 */       if ((updColumnNameToIndex != null) && (columnName != null)) {
/*  690 */         updColumnNameToIndex.put(columnName, Integer.valueOf(i));
/*      */       }
/*      */       
/*  693 */       String originalTableName = this.fields[i].getOriginalTableName();
/*  694 */       String tableName = null;
/*      */       
/*  696 */       if ((this.connection.getIO().hasLongColumnInfo()) && (originalTableName != null) && (originalTableName.length() > 0)) {
/*  697 */         tableName = originalTableName;
/*      */       } else {
/*  699 */         tableName = this.fields[i].getTableName();
/*      */       }
/*      */       
/*  702 */       StringBuilder fqcnBuf = new StringBuilder();
/*  703 */       String databaseName = this.fields[i].getDatabaseName();
/*      */       
/*  705 */       if ((databaseName != null) && (databaseName.length() > 0)) {
/*  706 */         fqcnBuf.append(quotedId);
/*  707 */         fqcnBuf.append(databaseName);
/*  708 */         fqcnBuf.append(quotedId);
/*  709 */         fqcnBuf.append('.');
/*      */       }
/*      */       
/*  712 */       fqcnBuf.append(quotedId);
/*  713 */       fqcnBuf.append(tableName);
/*  714 */       fqcnBuf.append(quotedId);
/*  715 */       fqcnBuf.append('.');
/*  716 */       fqcnBuf.append(quotedId);
/*  717 */       fqcnBuf.append(columnName);
/*  718 */       fqcnBuf.append(quotedId);
/*      */       
/*  720 */       String qualifiedColumnName = fqcnBuf.toString();
/*      */       
/*  722 */       if (this.fields[i].isPrimaryKey()) {
/*  723 */         this.primaryKeyIndicies.add(Integer.valueOf(i));
/*      */         
/*  725 */         if (!keysFirstTime) {
/*  726 */           keyValues.append(" AND ");
/*      */         } else {
/*  728 */           keysFirstTime = false;
/*      */         }
/*      */         
/*  731 */         keyValues.append(qualifiedColumnName);
/*  732 */         keyValues.append(equalsStr);
/*  733 */         keyValues.append("?");
/*      */       }
/*      */       
/*  736 */       if (firstTime) {
/*  737 */         firstTime = false;
/*  738 */         fieldValues.append("SET ");
/*      */       } else {
/*  740 */         fieldValues.append(",");
/*  741 */         columnNames.append(",");
/*  742 */         insertPlaceHolders.append(",");
/*      */       }
/*      */       
/*  745 */       insertPlaceHolders.append("?");
/*      */       
/*  747 */       columnNames.append(qualifiedColumnName);
/*      */       
/*  749 */       fieldValues.append(qualifiedColumnName);
/*  750 */       fieldValues.append("=?");
/*      */     }
/*      */     
/*  753 */     this.qualifiedAndQuotedTableName = allTablesBuf.toString();
/*      */     
/*  755 */     this.updateSQL = ("UPDATE " + this.qualifiedAndQuotedTableName + " " + fieldValues.toString() + " WHERE " + keyValues.toString());
/*  756 */     this.insertSQL = ("INSERT INTO " + this.qualifiedAndQuotedTableName + " (" + columnNames.toString() + ") VALUES (" + insertPlaceHolders.toString() + ")");
/*  757 */     this.refreshSQL = ("SELECT " + columnNames.toString() + " FROM " + this.qualifiedAndQuotedTableName + " WHERE " + keyValues.toString());
/*  758 */     this.deleteSQL = ("DELETE FROM " + this.qualifiedAndQuotedTableName + " WHERE " + keyValues.toString());
/*      */   }
/*      */   
/*      */   private Map<String, Integer> getColumnsToIndexMapForTableAndDB(String databaseName, String tableName)
/*      */   {
/*  763 */     Map<String, Map<String, Integer>> tablesUsedToColumnsMap = (Map)this.databasesUsedToTablesUsed.get(databaseName);
/*      */     
/*  765 */     if (tablesUsedToColumnsMap == null) {
/*  766 */       if (this.connection.lowerCaseTableNames()) {
/*  767 */         tablesUsedToColumnsMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*      */       } else {
/*  769 */         tablesUsedToColumnsMap = new TreeMap();
/*      */       }
/*      */       
/*  772 */       this.databasesUsedToTablesUsed.put(databaseName, tablesUsedToColumnsMap);
/*      */     }
/*      */     
/*  775 */     Map<String, Integer> nameToIndex = (Map)tablesUsedToColumnsMap.get(tableName);
/*      */     
/*  777 */     if (nameToIndex == null) {
/*  778 */       nameToIndex = new HashMap();
/*  779 */       tablesUsedToColumnsMap.put(tableName, nameToIndex);
/*      */     }
/*      */     
/*  782 */     return nameToIndex;
/*      */   }
/*      */   
/*      */   private synchronized SingleByteCharsetConverter getCharConverter() throws SQLException {
/*  786 */     if (!this.initializedCharConverter) {
/*  787 */       this.initializedCharConverter = true;
/*      */       
/*  789 */       if (this.connection.getUseUnicode()) {
/*  790 */         this.charEncoding = this.connection.getEncoding();
/*  791 */         this.charConverter = this.connection.getCharsetConverter(this.charEncoding);
/*      */       }
/*      */     }
/*      */     
/*  795 */     return this.charConverter;
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
/*      */   public int getConcurrency()
/*      */     throws SQLException
/*      */   {
/*  809 */     return this.isUpdatable ? 1008 : 1007;
/*      */   }
/*      */   
/*      */   private synchronized String getQuotedIdChar() throws SQLException {
/*  813 */     if (this.quotedIdChar == null) {
/*  814 */       boolean useQuotedIdentifiers = this.connection.supportsQuotedIdentifiers();
/*      */       
/*  816 */       if (useQuotedIdentifiers) {
/*  817 */         DatabaseMetaData dbmd = this.connection.getMetaData();
/*  818 */         this.quotedIdChar = dbmd.getIdentifierQuoteString();
/*      */       } else {
/*  820 */         this.quotedIdChar = "";
/*      */       }
/*      */     }
/*      */     
/*  824 */     return this.quotedIdChar;
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
/*      */   public synchronized void insertRow()
/*      */     throws SQLException
/*      */   {
/*  838 */     checkClosed();
/*      */     
/*  840 */     if (!this.onInsertRow) {
/*  841 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.7"), getExceptionInterceptor());
/*      */     }
/*      */     
/*  844 */     this.inserter.executeUpdate();
/*      */     
/*  846 */     long autoIncrementId = this.inserter.getLastInsertID();
/*  847 */     int numFields = this.fields.length;
/*  848 */     byte[][] newRow = new byte[numFields][];
/*      */     
/*  850 */     for (int i = 0; i < numFields; i++) {
/*  851 */       if (this.inserter.isNull(i)) {
/*  852 */         newRow[i] = null;
/*      */       } else {
/*  854 */         newRow[i] = this.inserter.getBytesRepresentation(i);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  860 */       if ((this.fields[i].isAutoIncrement()) && (autoIncrementId > 0L)) {
/*  861 */         newRow[i] = StringUtils.getBytes(String.valueOf(autoIncrementId));
/*  862 */         this.inserter.setBytesNoEscapeNoQuotes(i + 1, newRow[i]);
/*      */       }
/*      */     }
/*      */     
/*  866 */     ResultSetRow resultSetRow = new ByteArrayRow(newRow, getExceptionInterceptor());
/*      */     
/*  868 */     refreshRow(this.inserter, resultSetRow);
/*      */     
/*  870 */     this.rowData.addRow(resultSetRow);
/*  871 */     resetInserter();
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
/*      */   public synchronized boolean isAfterLast()
/*      */     throws SQLException
/*      */   {
/*  889 */     return super.isAfterLast();
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
/*      */   public synchronized boolean isBeforeFirst()
/*      */     throws SQLException
/*      */   {
/*  907 */     return super.isBeforeFirst();
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
/*      */   public synchronized boolean isFirst()
/*      */     throws SQLException
/*      */   {
/*  924 */     return super.isFirst();
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
/*      */   public synchronized boolean isLast()
/*      */     throws SQLException
/*      */   {
/*  942 */     return super.isLast();
/*      */   }
/*      */   
/*      */   boolean isUpdatable() {
/*  946 */     return this.isUpdatable;
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
/*      */   public synchronized boolean last()
/*      */     throws SQLException
/*      */   {
/*  964 */     return super.last();
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
/*      */   public synchronized void moveToCurrentRow()
/*      */     throws SQLException
/*      */   {
/*  979 */     checkClosed();
/*      */     
/*  981 */     if (!this.isUpdatable) {
/*  982 */       throw new NotUpdatable(this.notUpdatableReason);
/*      */     }
/*      */     
/*  985 */     if (this.onInsertRow) {
/*  986 */       this.onInsertRow = false;
/*  987 */       this.thisRow = this.savedCurrentRow;
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
/*      */   public synchronized void moveToInsertRow()
/*      */     throws SQLException
/*      */   {
/* 1009 */     checkClosed();
/*      */     
/* 1011 */     if (!this.isUpdatable) {
/* 1012 */       throw new NotUpdatable(this.notUpdatableReason);
/*      */     }
/*      */     
/* 1015 */     if (this.inserter == null) {
/* 1016 */       if (this.insertSQL == null) {
/* 1017 */         generateStatements();
/*      */       }
/*      */       
/* 1020 */       this.inserter = ((PreparedStatement)this.connection.clientPrepareStatement(this.insertSQL));
/* 1021 */       if (this.populateInserterWithDefaultValues) {
/* 1022 */         extractDefaultValues();
/*      */       }
/*      */       
/* 1025 */       resetInserter();
/*      */     } else {
/* 1027 */       resetInserter();
/*      */     }
/*      */     
/* 1030 */     int numFields = this.fields.length;
/*      */     
/* 1032 */     this.onInsertRow = true;
/* 1033 */     this.doingUpdates = false;
/* 1034 */     this.savedCurrentRow = this.thisRow;
/* 1035 */     byte[][] newRowData = new byte[numFields][];
/* 1036 */     this.thisRow = new ByteArrayRow(newRowData, getExceptionInterceptor());
/* 1037 */     this.thisRow.setMetadata(this.fields);
/*      */     
/* 1039 */     for (int i = 0; i < numFields; i++) {
/* 1040 */       if (!this.populateInserterWithDefaultValues) {
/* 1041 */         this.inserter.setBytesNoEscapeNoQuotes(i + 1, StringUtils.getBytes("DEFAULT"));
/* 1042 */         newRowData = (byte[][])null;
/*      */       }
/* 1044 */       else if (this.defaultColumnValue[i] != null) {
/* 1045 */         Field f = this.fields[i];
/*      */         
/* 1047 */         switch (f.getMysqlType())
/*      */         {
/*      */         case 7: 
/*      */         case 10: 
/*      */         case 11: 
/*      */         case 12: 
/*      */         case 14: 
/* 1054 */           if ((this.defaultColumnValue[i].length > 7) && (this.defaultColumnValue[i][0] == 67) && (this.defaultColumnValue[i][1] == 85) && (this.defaultColumnValue[i][2] == 82) && (this.defaultColumnValue[i][3] == 82) && (this.defaultColumnValue[i][4] == 69) && (this.defaultColumnValue[i][5] == 78) && (this.defaultColumnValue[i][6] == 84) && (this.defaultColumnValue[i][7] == 95))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1059 */             this.inserter.setBytesNoEscapeNoQuotes(i + 1, this.defaultColumnValue[i]);
/*      */           }
/*      */           else
/*      */           {
/* 1063 */             this.inserter.setBytes(i + 1, this.defaultColumnValue[i], false, false); }
/* 1064 */           break;
/*      */         case 8: case 9: 
/*      */         case 13: default: 
/* 1067 */           this.inserter.setBytes(i + 1, this.defaultColumnValue[i], false, false);
/*      */         }
/*      */         
/*      */         
/* 1071 */         byte[] defaultValueCopy = new byte[this.defaultColumnValue[i].length];
/* 1072 */         System.arraycopy(this.defaultColumnValue[i], 0, defaultValueCopy, 0, defaultValueCopy.length);
/* 1073 */         newRowData[i] = defaultValueCopy;
/*      */       } else {
/* 1075 */         this.inserter.setNull(i + 1, 0);
/* 1076 */         newRowData[i] = null;
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
/*      */   public synchronized boolean next()
/*      */     throws SQLException
/*      */   {
/* 1102 */     return super.next();
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
/*      */   public synchronized boolean prev()
/*      */     throws SQLException
/*      */   {
/* 1121 */     return super.prev();
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
/*      */   public synchronized boolean previous()
/*      */     throws SQLException
/*      */   {
/* 1143 */     return super.previous();
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
/*      */   public synchronized void realClose(boolean calledExplicitly)
/*      */     throws SQLException
/*      */   {
/* 1158 */     if (this.isClosed) {
/* 1159 */       return;
/*      */     }
/*      */     
/* 1162 */     SQLException sqlEx = null;
/*      */     
/* 1164 */     if ((this.useUsageAdvisor) && 
/* 1165 */       (this.deleter == null) && (this.inserter == null) && (this.refresher == null) && (this.updater == null)) {
/* 1166 */       this.eventSink = ProfilerEventHandlerFactory.getInstance(this.connection);
/*      */       
/* 1168 */       String message = Messages.getString("UpdatableResultSet.34");
/*      */       
/* 1170 */       this.eventSink.consumeEvent(new ProfilerEvent((byte)0, "", this.owningStatement == null ? "N/A" : this.owningStatement.currentCatalog, this.connectionId, this.owningStatement == null ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0L, Constants.MILLIS_I18N, null, this.pointOfOrigin, message));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/* 1178 */       if (this.deleter != null) {
/* 1179 */         this.deleter.close();
/*      */       }
/*      */     } catch (SQLException ex) {
/* 1182 */       sqlEx = ex;
/*      */     }
/*      */     try
/*      */     {
/* 1186 */       if (this.inserter != null) {
/* 1187 */         this.inserter.close();
/*      */       }
/*      */     } catch (SQLException ex) {
/* 1190 */       sqlEx = ex;
/*      */     }
/*      */     try
/*      */     {
/* 1194 */       if (this.refresher != null) {
/* 1195 */         this.refresher.close();
/*      */       }
/*      */     } catch (SQLException ex) {
/* 1198 */       sqlEx = ex;
/*      */     }
/*      */     try
/*      */     {
/* 1202 */       if (this.updater != null) {
/* 1203 */         this.updater.close();
/*      */       }
/*      */     } catch (SQLException ex) {
/* 1206 */       sqlEx = ex;
/*      */     }
/*      */     
/* 1209 */     super.realClose(calledExplicitly);
/*      */     
/* 1211 */     if (sqlEx != null) {
/* 1212 */       throw sqlEx;
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
/*      */   public synchronized void refreshRow()
/*      */     throws SQLException
/*      */   {
/* 1237 */     checkClosed();
/*      */     
/* 1239 */     if (!this.isUpdatable) {
/* 1240 */       throw new NotUpdatable();
/*      */     }
/*      */     
/* 1243 */     if (this.onInsertRow)
/* 1244 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.8"), getExceptionInterceptor());
/* 1245 */     if (this.rowData.size() == 0)
/* 1246 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.9"), getExceptionInterceptor());
/* 1247 */     if (isBeforeFirst())
/* 1248 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.10"), getExceptionInterceptor());
/* 1249 */     if (isAfterLast()) {
/* 1250 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.11"), getExceptionInterceptor());
/*      */     }
/*      */     
/* 1253 */     refreshRow(this.updater, this.thisRow);
/*      */   }
/*      */   
/*      */   private synchronized void refreshRow(PreparedStatement updateInsertStmt, ResultSetRow rowToRefresh) throws SQLException {
/* 1257 */     if (this.refresher == null) {
/* 1258 */       if (this.refreshSQL == null) {
/* 1259 */         generateStatements();
/*      */       }
/*      */       
/* 1262 */       this.refresher = ((PreparedStatement)this.connection.clientPrepareStatement(this.refreshSQL));
/*      */     }
/*      */     
/* 1265 */     this.refresher.clearParameters();
/*      */     
/* 1267 */     int numKeys = this.primaryKeyIndicies.size();
/*      */     
/* 1269 */     if (numKeys == 1) {
/* 1270 */       byte[] dataFrom = null;
/* 1271 */       int index = ((Integer)this.primaryKeyIndicies.get(0)).intValue();
/*      */       
/* 1273 */       if ((!this.doingUpdates) && (!this.onInsertRow)) {
/* 1274 */         dataFrom = rowToRefresh.getColumnValue(index);
/*      */       } else {
/* 1276 */         dataFrom = updateInsertStmt.getBytesRepresentation(index);
/*      */         
/*      */ 
/* 1279 */         if ((updateInsertStmt.isNull(index)) || (dataFrom.length == 0)) {
/* 1280 */           dataFrom = rowToRefresh.getColumnValue(index);
/*      */         } else {
/* 1282 */           dataFrom = stripBinaryPrefix(dataFrom);
/*      */         }
/*      */       }
/*      */       
/* 1286 */       if (this.fields[index].getvalueNeedsQuoting()) {
/* 1287 */         this.refresher.setBytesNoEscape(1, dataFrom);
/*      */       } else {
/* 1289 */         this.refresher.setBytesNoEscapeNoQuotes(1, dataFrom);
/*      */       }
/*      */     }
/*      */     else {
/* 1293 */       for (int i = 0; i < numKeys; i++) {
/* 1294 */         byte[] dataFrom = null;
/* 1295 */         int index = ((Integer)this.primaryKeyIndicies.get(i)).intValue();
/*      */         
/* 1297 */         if ((!this.doingUpdates) && (!this.onInsertRow)) {
/* 1298 */           dataFrom = rowToRefresh.getColumnValue(index);
/*      */         } else {
/* 1300 */           dataFrom = updateInsertStmt.getBytesRepresentation(index);
/*      */           
/*      */ 
/* 1303 */           if ((updateInsertStmt.isNull(index)) || (dataFrom.length == 0)) {
/* 1304 */             dataFrom = rowToRefresh.getColumnValue(index);
/*      */           } else {
/* 1306 */             dataFrom = stripBinaryPrefix(dataFrom);
/*      */           }
/*      */         }
/*      */         
/* 1310 */         this.refresher.setBytesNoEscape(i + 1, dataFrom);
/*      */       }
/*      */     }
/*      */     
/* 1314 */     ResultSet rs = null;
/*      */     try
/*      */     {
/* 1317 */       rs = this.refresher.executeQuery();
/*      */       
/* 1319 */       int numCols = rs.getMetaData().getColumnCount();
/*      */       
/* 1321 */       if (rs.next()) {
/* 1322 */         for (int i = 0; i < numCols; i++) {
/* 1323 */           byte[] val = rs.getBytes(i + 1);
/*      */           
/* 1325 */           if ((val == null) || (rs.wasNull())) {
/* 1326 */             rowToRefresh.setColumnValue(i, null);
/*      */           } else {
/* 1328 */             rowToRefresh.setColumnValue(i, rs.getBytes(i + 1));
/*      */           }
/*      */         }
/*      */       } else {
/* 1332 */         throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.12"), "S1000", getExceptionInterceptor());
/*      */       }
/*      */     } finally {
/* 1335 */       if (rs != null) {
/*      */         try {
/* 1337 */           rs.close();
/*      */         }
/*      */         catch (SQLException ex) {}
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
/*      */   public synchronized boolean relative(int rows)
/*      */     throws SQLException
/*      */   {
/* 1368 */     return super.relative(rows);
/*      */   }
/*      */   
/*      */   private void resetInserter() throws SQLException {
/* 1372 */     this.inserter.clearParameters();
/*      */     
/* 1374 */     for (int i = 0; i < this.fields.length; i++) {
/* 1375 */       this.inserter.setNull(i + 1, 0);
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
/*      */   public synchronized boolean rowDeleted()
/*      */     throws SQLException
/*      */   {
/* 1395 */     throw SQLError.createSQLFeatureNotSupportedException();
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
/*      */   public synchronized boolean rowInserted()
/*      */     throws SQLException
/*      */   {
/* 1413 */     throw SQLError.createSQLFeatureNotSupportedException();
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
/*      */   public synchronized boolean rowUpdated()
/*      */     throws SQLException
/*      */   {
/* 1431 */     throw SQLError.createSQLFeatureNotSupportedException();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setResultSetConcurrency(int concurrencyFlag)
/*      */   {
/* 1442 */     super.setResultSetConcurrency(concurrencyFlag);
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
/*      */   private byte[] stripBinaryPrefix(byte[] dataFrom)
/*      */   {
/* 1456 */     return StringUtils.stripEnclosure(dataFrom, "_binary'", "'");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected synchronized void syncUpdate()
/*      */     throws SQLException
/*      */   {
/* 1466 */     if (this.updater == null) {
/* 1467 */       if (this.updateSQL == null) {
/* 1468 */         generateStatements();
/*      */       }
/*      */       
/* 1471 */       this.updater = ((PreparedStatement)this.connection.clientPrepareStatement(this.updateSQL));
/*      */     }
/*      */     
/* 1474 */     int numFields = this.fields.length;
/* 1475 */     this.updater.clearParameters();
/*      */     
/* 1477 */     for (int i = 0; i < numFields; i++) {
/* 1478 */       if (this.thisRow.getColumnValue(i) != null)
/*      */       {
/* 1480 */         if (this.fields[i].getvalueNeedsQuoting()) {
/* 1481 */           this.updater.setBytes(i + 1, this.thisRow.getColumnValue(i), this.fields[i].isBinary(), false);
/*      */         } else {
/* 1483 */           this.updater.setBytesNoEscapeNoQuotes(i + 1, this.thisRow.getColumnValue(i));
/*      */         }
/*      */       } else {
/* 1486 */         this.updater.setNull(i + 1, 0);
/*      */       }
/*      */     }
/*      */     
/* 1490 */     int numKeys = this.primaryKeyIndicies.size();
/*      */     
/* 1492 */     if (numKeys == 1) {
/* 1493 */       int index = ((Integer)this.primaryKeyIndicies.get(0)).intValue();
/* 1494 */       setParamValue(this.updater, numFields + 1, this.thisRow, index, this.fields[index].getSQLType());
/*      */     } else {
/* 1496 */       for (int i = 0; i < numKeys; i++) {
/* 1497 */         int idx = ((Integer)this.primaryKeyIndicies.get(i)).intValue();
/* 1498 */         setParamValue(this.updater, numFields + i + 1, this.thisRow, idx, this.fields[idx].getSQLType());
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
/*      */   public synchronized void updateAsciiStream(int columnIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1522 */     if (!this.onInsertRow) {
/* 1523 */       if (!this.doingUpdates) {
/* 1524 */         this.doingUpdates = true;
/* 1525 */         syncUpdate();
/*      */       }
/*      */       
/* 1528 */       this.updater.setAsciiStream(columnIndex, x, length);
/*      */     } else {
/* 1530 */       this.inserter.setAsciiStream(columnIndex, x, length);
/* 1531 */       this.thisRow.setColumnValue(columnIndex - 1, STREAM_DATA_MARKER);
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
/*      */   public synchronized void updateAsciiStream(String columnName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1554 */     updateAsciiStream(findColumn(columnName), x, length);
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
/*      */   public synchronized void updateBigDecimal(int columnIndex, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 1573 */     if (!this.onInsertRow) {
/* 1574 */       if (!this.doingUpdates) {
/* 1575 */         this.doingUpdates = true;
/* 1576 */         syncUpdate();
/*      */       }
/*      */       
/* 1579 */       this.updater.setBigDecimal(columnIndex, x);
/*      */     } else {
/* 1581 */       this.inserter.setBigDecimal(columnIndex, x);
/*      */       
/* 1583 */       if (x == null) {
/* 1584 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*      */       } else {
/* 1586 */         this.thisRow.setColumnValue(columnIndex - 1, StringUtils.getBytes(x.toString()));
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
/*      */   public synchronized void updateBigDecimal(String columnName, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 1607 */     updateBigDecimal(findColumn(columnName), x);
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
/*      */   public synchronized void updateBinaryStream(int columnIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1629 */     if (!this.onInsertRow) {
/* 1630 */       if (!this.doingUpdates) {
/* 1631 */         this.doingUpdates = true;
/* 1632 */         syncUpdate();
/*      */       }
/*      */       
/* 1635 */       this.updater.setBinaryStream(columnIndex, x, length);
/*      */     } else {
/* 1637 */       this.inserter.setBinaryStream(columnIndex, x, length);
/*      */       
/* 1639 */       if (x == null) {
/* 1640 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*      */       } else {
/* 1642 */         this.thisRow.setColumnValue(columnIndex - 1, STREAM_DATA_MARKER);
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
/*      */   public synchronized void updateBinaryStream(String columnName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1666 */     updateBinaryStream(findColumn(columnName), x, length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public synchronized void updateBlob(int columnIndex, Blob blob)
/*      */     throws SQLException
/*      */   {
/* 1674 */     if (!this.onInsertRow) {
/* 1675 */       if (!this.doingUpdates) {
/* 1676 */         this.doingUpdates = true;
/* 1677 */         syncUpdate();
/*      */       }
/*      */       
/* 1680 */       this.updater.setBlob(columnIndex, blob);
/*      */     } else {
/* 1682 */       this.inserter.setBlob(columnIndex, blob);
/*      */       
/* 1684 */       if (blob == null) {
/* 1685 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*      */       } else {
/* 1687 */         this.thisRow.setColumnValue(columnIndex - 1, STREAM_DATA_MARKER);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public synchronized void updateBlob(String columnName, Blob blob)
/*      */     throws SQLException
/*      */   {
/* 1697 */     updateBlob(findColumn(columnName), blob);
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
/*      */   public synchronized void updateBoolean(int columnIndex, boolean x)
/*      */     throws SQLException
/*      */   {
/* 1716 */     if (!this.onInsertRow) {
/* 1717 */       if (!this.doingUpdates) {
/* 1718 */         this.doingUpdates = true;
/* 1719 */         syncUpdate();
/*      */       }
/*      */       
/* 1722 */       this.updater.setBoolean(columnIndex, x);
/*      */     } else {
/* 1724 */       this.inserter.setBoolean(columnIndex, x);
/*      */       
/* 1726 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
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
/*      */   public synchronized void updateBoolean(String columnName, boolean x)
/*      */     throws SQLException
/*      */   {
/* 1746 */     updateBoolean(findColumn(columnName), x);
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
/*      */   public synchronized void updateByte(int columnIndex, byte x)
/*      */     throws SQLException
/*      */   {
/* 1765 */     if (!this.onInsertRow) {
/* 1766 */       if (!this.doingUpdates) {
/* 1767 */         this.doingUpdates = true;
/* 1768 */         syncUpdate();
/*      */       }
/*      */       
/* 1771 */       this.updater.setByte(columnIndex, x);
/*      */     } else {
/* 1773 */       this.inserter.setByte(columnIndex, x);
/*      */       
/* 1775 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
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
/*      */   public synchronized void updateByte(String columnName, byte x)
/*      */     throws SQLException
/*      */   {
/* 1795 */     updateByte(findColumn(columnName), x);
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
/*      */   public synchronized void updateBytes(int columnIndex, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 1814 */     if (!this.onInsertRow) {
/* 1815 */       if (!this.doingUpdates) {
/* 1816 */         this.doingUpdates = true;
/* 1817 */         syncUpdate();
/*      */       }
/*      */       
/* 1820 */       this.updater.setBytes(columnIndex, x);
/*      */     } else {
/* 1822 */       this.inserter.setBytes(columnIndex, x);
/*      */       
/* 1824 */       this.thisRow.setColumnValue(columnIndex - 1, x);
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
/*      */   public synchronized void updateBytes(String columnName, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 1844 */     updateBytes(findColumn(columnName), x);
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
/*      */   public synchronized void updateCharacterStream(int columnIndex, Reader x, int length)
/*      */     throws SQLException
/*      */   {
/* 1866 */     if (!this.onInsertRow) {
/* 1867 */       if (!this.doingUpdates) {
/* 1868 */         this.doingUpdates = true;
/* 1869 */         syncUpdate();
/*      */       }
/*      */       
/* 1872 */       this.updater.setCharacterStream(columnIndex, x, length);
/*      */     } else {
/* 1874 */       this.inserter.setCharacterStream(columnIndex, x, length);
/*      */       
/* 1876 */       if (x == null) {
/* 1877 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*      */       } else {
/* 1879 */         this.thisRow.setColumnValue(columnIndex - 1, STREAM_DATA_MARKER);
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
/*      */   public synchronized void updateCharacterStream(String columnName, Reader reader, int length)
/*      */     throws SQLException
/*      */   {
/* 1903 */     updateCharacterStream(findColumn(columnName), reader, length);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void updateClob(int columnIndex, Clob clob)
/*      */     throws SQLException
/*      */   {
/* 1911 */     if (clob == null) {
/* 1912 */       updateNull(columnIndex);
/*      */     } else {
/* 1914 */       updateCharacterStream(columnIndex, clob.getCharacterStream(), (int)clob.length());
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
/*      */   public synchronized void updateDate(int columnIndex, Date x)
/*      */     throws SQLException
/*      */   {
/* 1934 */     if (!this.onInsertRow) {
/* 1935 */       if (!this.doingUpdates) {
/* 1936 */         this.doingUpdates = true;
/* 1937 */         syncUpdate();
/*      */       }
/*      */       
/* 1940 */       this.updater.setDate(columnIndex, x);
/*      */     } else {
/* 1942 */       this.inserter.setDate(columnIndex, x);
/*      */       
/* 1944 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
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
/*      */   public synchronized void updateDate(String columnName, Date x)
/*      */     throws SQLException
/*      */   {
/* 1964 */     updateDate(findColumn(columnName), x);
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
/*      */   public synchronized void updateDouble(int columnIndex, double x)
/*      */     throws SQLException
/*      */   {
/* 1983 */     if (!this.onInsertRow) {
/* 1984 */       if (!this.doingUpdates) {
/* 1985 */         this.doingUpdates = true;
/* 1986 */         syncUpdate();
/*      */       }
/*      */       
/* 1989 */       this.updater.setDouble(columnIndex, x);
/*      */     } else {
/* 1991 */       this.inserter.setDouble(columnIndex, x);
/*      */       
/* 1993 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
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
/*      */   public synchronized void updateDouble(String columnName, double x)
/*      */     throws SQLException
/*      */   {
/* 2013 */     updateDouble(findColumn(columnName), x);
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
/*      */   public synchronized void updateFloat(int columnIndex, float x)
/*      */     throws SQLException
/*      */   {
/* 2032 */     if (!this.onInsertRow) {
/* 2033 */       if (!this.doingUpdates) {
/* 2034 */         this.doingUpdates = true;
/* 2035 */         syncUpdate();
/*      */       }
/*      */       
/* 2038 */       this.updater.setFloat(columnIndex, x);
/*      */     } else {
/* 2040 */       this.inserter.setFloat(columnIndex, x);
/*      */       
/* 2042 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
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
/*      */   public synchronized void updateFloat(String columnName, float x)
/*      */     throws SQLException
/*      */   {
/* 2062 */     updateFloat(findColumn(columnName), x);
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
/*      */   public synchronized void updateInt(int columnIndex, int x)
/*      */     throws SQLException
/*      */   {
/* 2081 */     if (!this.onInsertRow) {
/* 2082 */       if (!this.doingUpdates) {
/* 2083 */         this.doingUpdates = true;
/* 2084 */         syncUpdate();
/*      */       }
/*      */       
/* 2087 */       this.updater.setInt(columnIndex, x);
/*      */     } else {
/* 2089 */       this.inserter.setInt(columnIndex, x);
/*      */       
/* 2091 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
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
/*      */   public synchronized void updateInt(String columnName, int x)
/*      */     throws SQLException
/*      */   {
/* 2111 */     updateInt(findColumn(columnName), x);
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
/*      */   public synchronized void updateLong(int columnIndex, long x)
/*      */     throws SQLException
/*      */   {
/* 2130 */     if (!this.onInsertRow) {
/* 2131 */       if (!this.doingUpdates) {
/* 2132 */         this.doingUpdates = true;
/* 2133 */         syncUpdate();
/*      */       }
/*      */       
/* 2136 */       this.updater.setLong(columnIndex, x);
/*      */     } else {
/* 2138 */       this.inserter.setLong(columnIndex, x);
/*      */       
/* 2140 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
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
/*      */   public synchronized void updateLong(String columnName, long x)
/*      */     throws SQLException
/*      */   {
/* 2160 */     updateLong(findColumn(columnName), x);
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
/*      */   public synchronized void updateNull(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2177 */     if (!this.onInsertRow) {
/* 2178 */       if (!this.doingUpdates) {
/* 2179 */         this.doingUpdates = true;
/* 2180 */         syncUpdate();
/*      */       }
/*      */       
/* 2183 */       this.updater.setNull(columnIndex, 0);
/*      */     } else {
/* 2185 */       this.inserter.setNull(columnIndex, 0);
/*      */       
/* 2187 */       this.thisRow.setColumnValue(columnIndex - 1, null);
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
/*      */   public synchronized void updateNull(String columnName)
/*      */     throws SQLException
/*      */   {
/* 2205 */     updateNull(findColumn(columnName));
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
/*      */   public synchronized void updateObject(int columnIndex, Object x)
/*      */     throws SQLException
/*      */   {
/* 2224 */     updateObjectInternal(columnIndex, x, null, 0);
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
/*      */   public synchronized void updateObject(int columnIndex, Object x, int scale)
/*      */     throws SQLException
/*      */   {
/* 2247 */     updateObjectInternal(columnIndex, x, null, scale);
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
/*      */   protected synchronized void updateObjectInternal(int columnIndex, Object x, Integer targetType, int scaleOrLength)
/*      */     throws SQLException
/*      */   {
/* 2261 */     if (!this.onInsertRow) {
/* 2262 */       if (!this.doingUpdates) {
/* 2263 */         this.doingUpdates = true;
/* 2264 */         syncUpdate();
/*      */       }
/*      */       
/* 2267 */       if (targetType == null) {
/* 2268 */         this.updater.setObject(columnIndex, x);
/*      */       } else {
/* 2270 */         this.updater.setObject(columnIndex, x, targetType.intValue());
/*      */       }
/*      */     } else {
/* 2273 */       if (targetType == null) {
/* 2274 */         this.inserter.setObject(columnIndex, x);
/*      */       } else {
/* 2276 */         this.inserter.setObject(columnIndex, x, targetType.intValue());
/*      */       }
/*      */       
/* 2279 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
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
/*      */   public synchronized void updateObject(String columnName, Object x)
/*      */     throws SQLException
/*      */   {
/* 2299 */     updateObject(findColumn(columnName), x);
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
/*      */   public synchronized void updateObject(String columnName, Object x, int scale)
/*      */     throws SQLException
/*      */   {
/* 2322 */     updateObject(findColumn(columnName), x);
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
/*      */   public synchronized void updateRow()
/*      */     throws SQLException
/*      */   {
/* 2336 */     if (!this.isUpdatable) {
/* 2337 */       throw new NotUpdatable(this.notUpdatableReason);
/*      */     }
/*      */     
/* 2340 */     if (this.doingUpdates) {
/* 2341 */       this.updater.executeUpdate();
/* 2342 */       refreshRow();
/* 2343 */       this.doingUpdates = false;
/* 2344 */     } else if (this.onInsertRow) {
/* 2345 */       throw SQLError.createSQLException(Messages.getString("UpdatableResultSet.44"), getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 2351 */     syncUpdate();
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
/*      */   public synchronized void updateShort(int columnIndex, short x)
/*      */     throws SQLException
/*      */   {
/* 2370 */     if (!this.onInsertRow) {
/* 2371 */       if (!this.doingUpdates) {
/* 2372 */         this.doingUpdates = true;
/* 2373 */         syncUpdate();
/*      */       }
/*      */       
/* 2376 */       this.updater.setShort(columnIndex, x);
/*      */     } else {
/* 2378 */       this.inserter.setShort(columnIndex, x);
/*      */       
/* 2380 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
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
/*      */   public synchronized void updateShort(String columnName, short x)
/*      */     throws SQLException
/*      */   {
/* 2400 */     updateShort(findColumn(columnName), x);
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
/*      */   public synchronized void updateString(int columnIndex, String x)
/*      */     throws SQLException
/*      */   {
/* 2419 */     checkClosed();
/*      */     
/* 2421 */     if (!this.onInsertRow) {
/* 2422 */       if (!this.doingUpdates) {
/* 2423 */         this.doingUpdates = true;
/* 2424 */         syncUpdate();
/*      */       }
/*      */       
/* 2427 */       this.updater.setString(columnIndex, x);
/*      */     } else {
/* 2429 */       this.inserter.setString(columnIndex, x);
/*      */       
/* 2431 */       if (x == null) {
/* 2432 */         this.thisRow.setColumnValue(columnIndex - 1, null);
/*      */       }
/* 2434 */       else if (getCharConverter() != null) {
/* 2435 */         this.thisRow.setColumnValue(columnIndex - 1, StringUtils.getBytes(x, this.charConverter, this.charEncoding, this.connection.getServerCharset(), this.connection.parserKnowsUnicode(), getExceptionInterceptor()));
/*      */       }
/*      */       else {
/* 2438 */         this.thisRow.setColumnValue(columnIndex - 1, StringUtils.getBytes(x));
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
/*      */   public synchronized void updateString(String columnName, String x)
/*      */     throws SQLException
/*      */   {
/* 2460 */     updateString(findColumn(columnName), x);
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
/*      */   public synchronized void updateTime(int columnIndex, Time x)
/*      */     throws SQLException
/*      */   {
/* 2479 */     if (!this.onInsertRow) {
/* 2480 */       if (!this.doingUpdates) {
/* 2481 */         this.doingUpdates = true;
/* 2482 */         syncUpdate();
/*      */       }
/*      */       
/* 2485 */       this.updater.setTime(columnIndex, x);
/*      */     } else {
/* 2487 */       this.inserter.setTime(columnIndex, x);
/*      */       
/* 2489 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
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
/*      */   public synchronized void updateTime(String columnName, Time x)
/*      */     throws SQLException
/*      */   {
/* 2509 */     updateTime(findColumn(columnName), x);
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
/*      */   public synchronized void updateTimestamp(int columnIndex, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 2528 */     if (!this.onInsertRow) {
/* 2529 */       if (!this.doingUpdates) {
/* 2530 */         this.doingUpdates = true;
/* 2531 */         syncUpdate();
/*      */       }
/*      */       
/* 2534 */       this.updater.setTimestamp(columnIndex, x);
/*      */     } else {
/* 2536 */       this.inserter.setTimestamp(columnIndex, x);
/*      */       
/* 2538 */       this.thisRow.setColumnValue(columnIndex - 1, this.inserter.getBytesRepresentation(columnIndex - 1));
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
/*      */   public synchronized void updateTimestamp(String columnName, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 2558 */     updateTimestamp(findColumn(columnName), x);
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\UpdatableResultSet.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */