/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.sql.Connection;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.SortedMap;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.TreeMap;
/*      */ import java.util.TreeSet;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DatabaseMetaData
/*      */   implements java.sql.DatabaseMetaData
/*      */ {
/*      */   protected static final int MAX_IDENTIFIER_LENGTH = 64;
/*      */   private static final int DEFERRABILITY = 13;
/*      */   private static final int DELETE_RULE = 10;
/*      */   private static final int FK_NAME = 11;
/*      */   private static final int FKCOLUMN_NAME = 7;
/*      */   private static final int FKTABLE_CAT = 4;
/*      */   private static final int FKTABLE_NAME = 6;
/*      */   private static final int FKTABLE_SCHEM = 5;
/*      */   private static final int KEY_SEQ = 8;
/*      */   private static final int PK_NAME = 12;
/*      */   private static final int PKCOLUMN_NAME = 3;
/*      */   private static final int PKTABLE_CAT = 0;
/*      */   private static final int PKTABLE_NAME = 2;
/*      */   private static final int PKTABLE_SCHEM = 1;
/*      */   private static final String SUPPORTS_FK = "SUPPORTS_FK";
/*      */   
/*      */   protected abstract class IteratorWithCleanup<T>
/*      */   {
/*      */     protected IteratorWithCleanup() {}
/*      */     
/*      */     abstract void close()
/*      */       throws SQLException;
/*      */     
/*      */     abstract boolean hasNext()
/*      */       throws SQLException;
/*      */     
/*      */     abstract T next()
/*      */       throws SQLException;
/*      */   }
/*      */   
/*      */   class LocalAndReferencedColumns
/*      */   {
/*      */     String constraintName;
/*      */     List<String> localColumnsList;
/*      */     String referencedCatalog;
/*      */     List<String> referencedColumnsList;
/*      */     String referencedTable;
/*      */     
/*      */     LocalAndReferencedColumns(List<String> localColumns, String refColumns, String constName, String refCatalog)
/*      */     {
/*   86 */       this.localColumnsList = localColumns;
/*   87 */       this.referencedColumnsList = refColumns;
/*   88 */       this.constraintName = constName;
/*   89 */       this.referencedTable = refTable;
/*   90 */       this.referencedCatalog = refCatalog;
/*      */     }
/*      */   }
/*      */   
/*      */   protected class ResultSetIterator extends IteratorWithCleanup<String> {
/*      */     int colIndex;
/*      */     ResultSet resultSet;
/*      */     
/*      */     ResultSetIterator(ResultSet rs, int index) {
/*   99 */       super();
/*  100 */       this.resultSet = rs;
/*  101 */       this.colIndex = index;
/*      */     }
/*      */     
/*      */     void close() throws SQLException
/*      */     {
/*  106 */       this.resultSet.close();
/*      */     }
/*      */     
/*      */     boolean hasNext() throws SQLException
/*      */     {
/*  111 */       return this.resultSet.next();
/*      */     }
/*      */     
/*      */     String next() throws SQLException
/*      */     {
/*  116 */       return this.resultSet.getObject(this.colIndex).toString();
/*      */     }
/*      */   }
/*      */   
/*      */   protected class SingleStringIterator extends IteratorWithCleanup<String> {
/*  121 */     boolean onFirst = true;
/*      */     String value;
/*      */     
/*      */     SingleStringIterator(String s) {
/*  125 */       super();
/*  126 */       this.value = s;
/*      */     }
/*      */     
/*      */ 
/*      */     void close()
/*      */       throws SQLException
/*      */     {}
/*      */     
/*      */     boolean hasNext()
/*      */       throws SQLException
/*      */     {
/*  137 */       return this.onFirst;
/*      */     }
/*      */     
/*      */     String next() throws SQLException
/*      */     {
/*  142 */       this.onFirst = false;
/*  143 */       return this.value;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   class TypeDescriptor
/*      */   {
/*      */     int bufferLength;
/*      */     
/*      */ 
/*      */     int charOctetLength;
/*      */     
/*      */     Integer columnSize;
/*      */     
/*      */     short dataType;
/*      */     
/*      */     Integer decimalDigits;
/*      */     
/*      */     String isNullable;
/*      */     
/*      */     int nullability;
/*      */     
/*  166 */     int numPrecRadix = 10;
/*      */     String typeName;
/*      */     
/*      */     TypeDescriptor(String typeInfo, String nullabilityInfo) throws SQLException
/*      */     {
/*  171 */       if (typeInfo == null) {
/*  172 */         throw SQLError.createSQLException("NULL typeinfo not supported.", "S1009", DatabaseMetaData.this.getExceptionInterceptor());
/*      */       }
/*      */       
/*  175 */       String mysqlType = "";
/*  176 */       String fullMysqlType = null;
/*      */       
/*  178 */       if (typeInfo.indexOf("(") != -1) {
/*  179 */         mysqlType = typeInfo.substring(0, typeInfo.indexOf("(")).trim();
/*      */       } else {
/*  181 */         mysqlType = typeInfo;
/*      */       }
/*      */       
/*  184 */       int indexOfUnsignedInMysqlType = StringUtils.indexOfIgnoreCase(mysqlType, "unsigned");
/*      */       
/*  186 */       if (indexOfUnsignedInMysqlType != -1) {
/*  187 */         mysqlType = mysqlType.substring(0, indexOfUnsignedInMysqlType - 1);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  192 */       boolean isUnsigned = false;
/*      */       
/*  194 */       if ((StringUtils.indexOfIgnoreCase(typeInfo, "unsigned") != -1) && (StringUtils.indexOfIgnoreCase(typeInfo, "set") != 0) && (StringUtils.indexOfIgnoreCase(typeInfo, "enum") != 0))
/*      */       {
/*  196 */         fullMysqlType = mysqlType + " unsigned";
/*  197 */         isUnsigned = true;
/*      */       } else {
/*  199 */         fullMysqlType = mysqlType;
/*      */       }
/*      */       
/*  202 */       if (DatabaseMetaData.this.conn.getCapitalizeTypeNames()) {
/*  203 */         fullMysqlType = fullMysqlType.toUpperCase(Locale.ENGLISH);
/*      */       }
/*      */       
/*  206 */       this.dataType = ((short)MysqlDefs.mysqlToJavaType(mysqlType));
/*      */       
/*  208 */       this.typeName = fullMysqlType;
/*      */       
/*      */ 
/*      */ 
/*  212 */       if (StringUtils.startsWithIgnoreCase(typeInfo, "enum")) {
/*  213 */         String temp = typeInfo.substring(typeInfo.indexOf("("), typeInfo.lastIndexOf(")"));
/*  214 */         StringTokenizer tokenizer = new StringTokenizer(temp, ",");
/*  215 */         int maxLength = 0;
/*      */         
/*  217 */         while (tokenizer.hasMoreTokens()) {
/*  218 */           maxLength = Math.max(maxLength, tokenizer.nextToken().length() - 2);
/*      */         }
/*      */         
/*  221 */         this.columnSize = Integer.valueOf(maxLength);
/*  222 */         this.decimalDigits = null;
/*  223 */       } else if (StringUtils.startsWithIgnoreCase(typeInfo, "set")) {
/*  224 */         String temp = typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.lastIndexOf(")"));
/*  225 */         StringTokenizer tokenizer = new StringTokenizer(temp, ",");
/*  226 */         int maxLength = 0;
/*      */         
/*  228 */         int numElements = tokenizer.countTokens();
/*      */         
/*  230 */         if (numElements > 0) {
/*  231 */           maxLength += numElements - 1;
/*      */         }
/*      */         
/*  234 */         while (tokenizer.hasMoreTokens()) {
/*  235 */           String setMember = tokenizer.nextToken().trim();
/*      */           
/*  237 */           if ((setMember.startsWith("'")) && (setMember.endsWith("'"))) {
/*  238 */             maxLength += setMember.length() - 2;
/*      */           } else {
/*  240 */             maxLength += setMember.length();
/*      */           }
/*      */         }
/*      */         
/*  244 */         this.columnSize = Integer.valueOf(maxLength);
/*  245 */         this.decimalDigits = null;
/*  246 */       } else if (typeInfo.indexOf(",") != -1)
/*      */       {
/*  248 */         this.columnSize = Integer.valueOf(typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.indexOf(",")).trim());
/*  249 */         this.decimalDigits = Integer.valueOf(typeInfo.substring(typeInfo.indexOf(",") + 1, typeInfo.indexOf(")")).trim());
/*      */       } else {
/*  251 */         this.columnSize = null;
/*  252 */         this.decimalDigits = null;
/*      */         
/*      */ 
/*  255 */         if (((StringUtils.indexOfIgnoreCase(typeInfo, "char") != -1) || (StringUtils.indexOfIgnoreCase(typeInfo, "text") != -1) || (StringUtils.indexOfIgnoreCase(typeInfo, "blob") != -1) || (StringUtils.indexOfIgnoreCase(typeInfo, "binary") != -1) || (StringUtils.indexOfIgnoreCase(typeInfo, "bit") != -1)) && (typeInfo.indexOf("(") != -1))
/*      */         {
/*      */ 
/*  258 */           int endParenIndex = typeInfo.indexOf(")");
/*      */           
/*  260 */           if (endParenIndex == -1) {
/*  261 */             endParenIndex = typeInfo.length();
/*      */           }
/*      */           
/*  264 */           this.columnSize = Integer.valueOf(typeInfo.substring(typeInfo.indexOf("(") + 1, endParenIndex).trim());
/*      */           
/*      */ 
/*  267 */           if ((DatabaseMetaData.this.conn.getTinyInt1isBit()) && (this.columnSize.intValue() == 1) && (StringUtils.startsWithIgnoreCase(typeInfo, 0, "tinyint")))
/*      */           {
/*  269 */             if (DatabaseMetaData.this.conn.getTransformedBitIsBoolean()) {
/*  270 */               this.dataType = 16;
/*  271 */               this.typeName = "BOOLEAN";
/*      */             } else {
/*  273 */               this.dataType = -7;
/*  274 */               this.typeName = "BIT";
/*      */             }
/*      */           }
/*  277 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "tinyint")) {
/*  278 */           if ((DatabaseMetaData.this.conn.getTinyInt1isBit()) && (typeInfo.indexOf("(1)") != -1)) {
/*  279 */             if (DatabaseMetaData.this.conn.getTransformedBitIsBoolean()) {
/*  280 */               this.dataType = 16;
/*  281 */               this.typeName = "BOOLEAN";
/*      */             } else {
/*  283 */               this.dataType = -7;
/*  284 */               this.typeName = "BIT";
/*      */             }
/*      */           } else {
/*  287 */             this.columnSize = Integer.valueOf(3);
/*  288 */             this.decimalDigits = Integer.valueOf(0);
/*      */           }
/*  290 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "smallint")) {
/*  291 */           this.columnSize = Integer.valueOf(5);
/*  292 */           this.decimalDigits = Integer.valueOf(0);
/*  293 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "mediumint")) {
/*  294 */           this.columnSize = Integer.valueOf(isUnsigned ? 8 : 7);
/*  295 */           this.decimalDigits = Integer.valueOf(0);
/*  296 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "int")) {
/*  297 */           this.columnSize = Integer.valueOf(10);
/*  298 */           this.decimalDigits = Integer.valueOf(0);
/*  299 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "integer")) {
/*  300 */           this.columnSize = Integer.valueOf(10);
/*  301 */           this.decimalDigits = Integer.valueOf(0);
/*  302 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "bigint")) {
/*  303 */           this.columnSize = Integer.valueOf(isUnsigned ? 20 : 19);
/*  304 */           this.decimalDigits = Integer.valueOf(0);
/*  305 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "int24")) {
/*  306 */           this.columnSize = Integer.valueOf(19);
/*  307 */           this.decimalDigits = Integer.valueOf(0);
/*  308 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "real")) {
/*  309 */           this.columnSize = Integer.valueOf(12);
/*  310 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "float")) {
/*  311 */           this.columnSize = Integer.valueOf(12);
/*  312 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "decimal")) {
/*  313 */           this.columnSize = Integer.valueOf(12);
/*  314 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "numeric")) {
/*  315 */           this.columnSize = Integer.valueOf(12);
/*  316 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "double")) {
/*  317 */           this.columnSize = Integer.valueOf(22);
/*  318 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "char")) {
/*  319 */           this.columnSize = Integer.valueOf(1);
/*  320 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "varchar")) {
/*  321 */           this.columnSize = Integer.valueOf(255);
/*  322 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "timestamp")) {
/*  323 */           this.columnSize = Integer.valueOf(19);
/*  324 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "datetime")) {
/*  325 */           this.columnSize = Integer.valueOf(19);
/*  326 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "date")) {
/*  327 */           this.columnSize = Integer.valueOf(10);
/*  328 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "time")) {
/*  329 */           this.columnSize = Integer.valueOf(8);
/*      */         }
/*  331 */         else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "tinyblob")) {
/*  332 */           this.columnSize = Integer.valueOf(255);
/*  333 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "blob")) {
/*  334 */           this.columnSize = Integer.valueOf(65535);
/*  335 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "mediumblob")) {
/*  336 */           this.columnSize = Integer.valueOf(16777215);
/*  337 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "longblob")) {
/*  338 */           this.columnSize = Integer.valueOf(Integer.MAX_VALUE);
/*  339 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "tinytext")) {
/*  340 */           this.columnSize = Integer.valueOf(255);
/*  341 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "text")) {
/*  342 */           this.columnSize = Integer.valueOf(65535);
/*  343 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "mediumtext")) {
/*  344 */           this.columnSize = Integer.valueOf(16777215);
/*  345 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "longtext")) {
/*  346 */           this.columnSize = Integer.valueOf(Integer.MAX_VALUE);
/*  347 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "enum")) {
/*  348 */           this.columnSize = Integer.valueOf(255);
/*  349 */         } else if (StringUtils.startsWithIgnoreCaseAndWs(typeInfo, "set")) {
/*  350 */           this.columnSize = Integer.valueOf(255);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  356 */       this.bufferLength = MysqlIO.getMaxBuf();
/*      */       
/*      */ 
/*  359 */       this.numPrecRadix = 10;
/*      */       
/*      */ 
/*  362 */       if (nullabilityInfo != null) {
/*  363 */         if (nullabilityInfo.equals("YES")) {
/*  364 */           this.nullability = 1;
/*  365 */           this.isNullable = "YES";
/*      */         }
/*  367 */         else if (nullabilityInfo.equals("UNKNOWN")) {
/*  368 */           this.nullability = 2;
/*  369 */           this.isNullable = "";
/*      */         }
/*      */         else
/*      */         {
/*  373 */           this.nullability = 0;
/*  374 */           this.isNullable = "NO";
/*      */         }
/*      */       } else {
/*  377 */         this.nullability = 0;
/*  378 */         this.isNullable = "NO";
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected class IndexMetaDataKey
/*      */     implements Comparable<IndexMetaDataKey>
/*      */   {
/*      */     Boolean columnNonUnique;
/*      */     Short columnType;
/*      */     String columnIndexName;
/*      */     Short columnOrdinalPosition;
/*      */     
/*      */     IndexMetaDataKey(boolean columnNonUnique, short columnType, String columnIndexName, short columnOrdinalPosition)
/*      */     {
/*  393 */       this.columnNonUnique = Boolean.valueOf(columnNonUnique);
/*  394 */       this.columnType = Short.valueOf(columnType);
/*  395 */       this.columnIndexName = columnIndexName;
/*  396 */       this.columnOrdinalPosition = Short.valueOf(columnOrdinalPosition);
/*      */     }
/*      */     
/*      */     public int compareTo(IndexMetaDataKey indexInfoKey)
/*      */     {
/*      */       int compareResult;
/*  402 */       if ((compareResult = this.columnNonUnique.compareTo(indexInfoKey.columnNonUnique)) != 0) {
/*  403 */         return compareResult;
/*      */       }
/*  405 */       if ((compareResult = this.columnType.compareTo(indexInfoKey.columnType)) != 0) {
/*  406 */         return compareResult;
/*      */       }
/*  408 */       if ((compareResult = this.columnIndexName.compareTo(indexInfoKey.columnIndexName)) != 0) {
/*  409 */         return compareResult;
/*      */       }
/*  411 */       return this.columnOrdinalPosition.compareTo(indexInfoKey.columnOrdinalPosition);
/*      */     }
/*      */     
/*      */     public boolean equals(Object obj)
/*      */     {
/*  416 */       if (obj == null) {
/*  417 */         return false;
/*      */       }
/*      */       
/*  420 */       if (obj == this) {
/*  421 */         return true;
/*      */       }
/*      */       
/*  424 */       if (!(obj instanceof IndexMetaDataKey)) {
/*  425 */         return false;
/*      */       }
/*  427 */       return compareTo((IndexMetaDataKey)obj) == 0;
/*      */     }
/*      */     
/*      */     public int hashCode()
/*      */     {
/*  432 */       if (!$assertionsDisabled) throw new AssertionError("hashCode not designed");
/*  433 */       return 0;
/*      */     }
/*      */   }
/*      */   
/*      */   protected class TableMetaDataKey
/*      */     implements Comparable<TableMetaDataKey>
/*      */   {
/*      */     String tableType;
/*      */     String tableCat;
/*      */     String tableSchem;
/*      */     String tableName;
/*      */     
/*      */     TableMetaDataKey(String tableType, String tableCat, String tableSchem, String tableName)
/*      */     {
/*  447 */       this.tableType = (tableType == null ? "" : tableType);
/*  448 */       this.tableCat = (tableCat == null ? "" : tableCat);
/*  449 */       this.tableSchem = (tableSchem == null ? "" : tableSchem);
/*  450 */       this.tableName = (tableName == null ? "" : tableName);
/*      */     }
/*      */     
/*      */     public int compareTo(TableMetaDataKey tablesKey)
/*      */     {
/*      */       int compareResult;
/*  456 */       if ((compareResult = this.tableType.compareTo(tablesKey.tableType)) != 0) {
/*  457 */         return compareResult;
/*      */       }
/*  459 */       if ((compareResult = this.tableCat.compareTo(tablesKey.tableCat)) != 0) {
/*  460 */         return compareResult;
/*      */       }
/*  462 */       if ((compareResult = this.tableSchem.compareTo(tablesKey.tableSchem)) != 0) {
/*  463 */         return compareResult;
/*      */       }
/*  465 */       return this.tableName.compareTo(tablesKey.tableName);
/*      */     }
/*      */     
/*      */     public boolean equals(Object obj)
/*      */     {
/*  470 */       if (obj == null) {
/*  471 */         return false;
/*      */       }
/*      */       
/*  474 */       if (obj == this) {
/*  475 */         return true;
/*      */       }
/*      */       
/*  478 */       if (!(obj instanceof TableMetaDataKey)) {
/*  479 */         return false;
/*      */       }
/*  481 */       return compareTo((TableMetaDataKey)obj) == 0;
/*      */     }
/*      */     
/*      */     public int hashCode()
/*      */     {
/*  486 */       if (!$assertionsDisabled) throw new AssertionError("hashCode not designed");
/*  487 */       return 0;
/*      */     }
/*      */   }
/*      */   
/*      */   protected class ComparableWrapper<K,  extends Comparable<? super K>, V>
/*      */     implements Comparable<ComparableWrapper<K, V>>
/*      */   {
/*      */     K key;
/*      */     V value;
/*      */     
/*      */     public ComparableWrapper(V key)
/*      */     {
/*  499 */       this.key = key;
/*  500 */       this.value = value;
/*      */     }
/*      */     
/*      */     public K getKey() {
/*  504 */       return (K)this.key;
/*      */     }
/*      */     
/*      */     public V getValue() {
/*  508 */       return (V)this.value;
/*      */     }
/*      */     
/*      */     public int compareTo(ComparableWrapper<K, V> other) {
/*  512 */       return ((Comparable)getKey()).compareTo(other.getKey());
/*      */     }
/*      */     
/*      */     public boolean equals(Object obj)
/*      */     {
/*  517 */       if (obj == null) {
/*  518 */         return false;
/*      */       }
/*      */       
/*  521 */       if (obj == this) {
/*  522 */         return true;
/*      */       }
/*      */       
/*  525 */       if (!(obj instanceof ComparableWrapper)) {
/*  526 */         return false;
/*      */       }
/*      */       
/*  529 */       Object otherKey = ((ComparableWrapper)obj).getKey();
/*  530 */       return this.key.equals(otherKey);
/*      */     }
/*      */     
/*      */     public int hashCode()
/*      */     {
/*  535 */       if (!$assertionsDisabled) throw new AssertionError("hashCode not designed");
/*  536 */       return 0;
/*      */     }
/*      */     
/*      */     public String toString()
/*      */     {
/*  541 */       return "{KEY:" + this.key + "; VALUE:" + this.value + "}";
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static enum TableType
/*      */   {
/*  549 */     LOCAL_TEMPORARY("LOCAL TEMPORARY"),  SYSTEM_TABLE("SYSTEM TABLE"),  SYSTEM_VIEW("SYSTEM VIEW"),  TABLE("TABLE", new String[] { "BASE TABLE" }), 
/*  550 */     VIEW("VIEW"),  UNKNOWN("UNKNOWN");
/*      */     
/*      */     private String name;
/*      */     private byte[] nameAsBytes;
/*      */     private String[] synonyms;
/*      */     
/*      */     private TableType(String tableTypeName) {
/*  557 */       this(tableTypeName, null);
/*      */     }
/*      */     
/*      */     private TableType(String tableTypeName, String[] tableTypeSynonyms) {
/*  561 */       this.name = tableTypeName;
/*  562 */       this.nameAsBytes = tableTypeName.getBytes();
/*  563 */       this.synonyms = tableTypeSynonyms;
/*      */     }
/*      */     
/*      */     String getName() {
/*  567 */       return this.name;
/*      */     }
/*      */     
/*      */     byte[] asBytes() {
/*  571 */       return this.nameAsBytes;
/*      */     }
/*      */     
/*      */     boolean equalsTo(String tableTypeName) {
/*  575 */       return this.name.equalsIgnoreCase(tableTypeName);
/*      */     }
/*      */     
/*      */     static TableType getTableTypeEqualTo(String tableTypeName) {
/*  579 */       for (TableType tableType : ) {
/*  580 */         if (tableType.equalsTo(tableTypeName)) {
/*  581 */           return tableType;
/*      */         }
/*      */       }
/*  584 */       return UNKNOWN;
/*      */     }
/*      */     
/*      */     boolean compliesWith(String tableTypeName) {
/*  588 */       if (equalsTo(tableTypeName)) {
/*  589 */         return true;
/*      */       }
/*  591 */       if (this.synonyms != null) {
/*  592 */         for (String synonym : this.synonyms) {
/*  593 */           if (synonym.equalsIgnoreCase(tableTypeName)) {
/*  594 */             return true;
/*      */           }
/*      */         }
/*      */       }
/*  598 */       return false;
/*      */     }
/*      */     
/*      */     static TableType getTableTypeCompliantWith(String tableTypeName) {
/*  602 */       for (TableType tableType : ) {
/*  603 */         if (tableType.compliesWith(tableTypeName)) {
/*  604 */           return tableType;
/*      */         }
/*      */       }
/*  607 */       return UNKNOWN;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected static enum ProcedureType
/*      */   {
/*  615 */     PROCEDURE,  FUNCTION;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private ProcedureType() {}
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
/*  652 */   protected static final byte[] TABLE_AS_BYTES = "TABLE".getBytes();
/*      */   
/*  654 */   protected static final byte[] SYSTEM_TABLE_AS_BYTES = "SYSTEM TABLE".getBytes();
/*      */   
/*      */   private static final int UPDATE_RULE = 9;
/*      */   
/*  658 */   protected static final byte[] VIEW_AS_BYTES = "VIEW".getBytes();
/*      */   
/*      */   private static final Constructor<?> JDBC_4_DBMD_SHOW_CTOR;
/*      */   private static final Constructor<?> JDBC_4_DBMD_IS_CTOR;
/*      */   
/*      */   static
/*      */   {
/*  665 */     if (Util.isJdbc4()) {
/*      */       try {
/*  667 */         JDBC_4_DBMD_SHOW_CTOR = Class.forName("com.mysql.jdbc.JDBC4DatabaseMetaData").getConstructor(new Class[] { MySQLConnection.class, String.class });
/*      */         
/*  669 */         JDBC_4_DBMD_IS_CTOR = Class.forName("com.mysql.jdbc.JDBC4DatabaseMetaDataUsingInfoSchema").getConstructor(new Class[] { MySQLConnection.class, String.class });
/*      */       }
/*      */       catch (SecurityException e) {
/*  672 */         throw new RuntimeException(e);
/*      */       } catch (NoSuchMethodException e) {
/*  674 */         throw new RuntimeException(e);
/*      */       } catch (ClassNotFoundException e) {
/*  676 */         throw new RuntimeException(e);
/*      */       }
/*      */     } else {
/*  679 */       JDBC_4_DBMD_IS_CTOR = null;
/*  680 */       JDBC_4_DBMD_SHOW_CTOR = null;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*  685 */   private static final String[] MYSQL_KEYWORDS = { "ACCESSIBLE", "ADD", "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC", "ASENSITIVE", "BEFORE", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOTH", "BY", "CALL", "CASCADE", "CASE", "CHANGE", "CHAR", "CHARACTER", "CHECK", "COLLATE", "COLUMN", "CONDITION", "CONSTRAINT", "CONTINUE", "CONVERT", "CREATE", "CROSS", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DATABASE", "DATABASES", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELAYED", "DELETE", "DESC", "DESCRIBE", "DETERMINISTIC", "DISTINCT", "DISTINCTROW", "DIV", "DOUBLE", "DROP", "DUAL", "EACH", "ELSE", "ELSEIF", "ENCLOSED", "ESCAPED", "EXISTS", "EXIT", "EXPLAIN", "FALSE", "FETCH", "FLOAT", "FLOAT4", "FLOAT8", "FOR", "FORCE", "FOREIGN", "FROM", "FULLTEXT", "GENERATED", "GET", "GRANT", "GROUP", "HAVING", "HIGH_PRIORITY", "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF", "IGNORE", "IN", "INDEX", "INFILE", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT", "INT1", "INT2", "INT3", "INT4", "INT8", "INTEGER", "INTERVAL", "INTO", "IO_AFTER_GTIDS", "IO_BEFORE_GTIDS", "IS", "ITERATE", "JOIN", "KEY", "KEYS", "KILL", "LEADING", "LEAVE", "LEFT", "LIKE", "LIMIT", "LINEAR", "LINES", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG", "LONGBLOB", "LONGTEXT", "LOOP", "LOW_PRIORITY", "MASTER_BIND", "MASTER_SSL_VERIFY_SERVER_CERT", "MATCH", "MAXVALUE", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MIDDLEINT", "MINUTE_MICROSECOND", "MINUTE_SECOND", "MOD", "MODIFIES", "NATURAL", "NOT", "NO_WRITE_TO_BINLOG", "NULL", "NUMERIC", "ON", "OPTIMIZE", "OPTIMIZER_COSTS", "OPTION", "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER", "OUTFILE", "PARTITION", "PRECISION", "PRIMARY", "PROCEDURE", "PURGE", "RANGE", "READ", "READS", "READ_WRITE", "REAL", "REFERENCES", "REGEXP", "RELEASE", "RENAME", "REPEAT", "REPLACE", "REQUIRE", "RESIGNAL", "RESTRICT", "RETURN", "REVOKE", "RIGHT", "RLIKE", "SCHEMA", "SCHEMAS", "SECOND_MICROSECOND", "SELECT", "SENSITIVE", "SEPARATOR", "SET", "SHOW", "SIGNAL", "SMALLINT", "SPATIAL", "SPECIFIC", "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQL_BIG_RESULT", "SQL_CALC_FOUND_ROWS", "SQL_SMALL_RESULT", "SSL", "STARTING", "STORED", "STRAIGHT_JOIN", "TABLE", "TERMINATED", "THEN", "TINYBLOB", "TINYINT", "TINYTEXT", "TO", "TRAILING", "TRIGGER", "TRUE", "UNDO", "UNION", "UNIQUE", "UNLOCK", "UNSIGNED", "UPDATE", "USAGE", "USE", "USING", "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "VALUES", "VARBINARY", "VARCHAR", "VARCHARACTER", "VARYING", "VIRTUAL", "WHEN", "WHERE", "WHILE", "WITH", "WRITE", "XOR", "YEAR_MONTH", "ZEROFILL" };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  706 */   private static final String[] SQL92_KEYWORDS = { "ABSOLUTE", "ACTION", "ADD", "ALL", "ALLOCATE", "ALTER", "AND", "ANY", "ARE", "AS", "ASC", "ASSERTION", "AT", "AUTHORIZATION", "AVG", "BEGIN", "BETWEEN", "BIT", "BIT_LENGTH", "BOTH", "BY", "CASCADE", "CASCADED", "CASE", "CAST", "CATALOG", "CHAR", "CHARACTER", "CHARACTER_LENGTH", "CHAR_LENGTH", "CHECK", "CLOSE", "COALESCE", "COLLATE", "COLLATION", "COLUMN", "COMMIT", "CONNECT", "CONNECTION", "CONSTRAINT", "CONSTRAINTS", "CONTINUE", "CONVERT", "CORRESPONDING", "COUNT", "CREATE", "CROSS", "CURRENT", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "DATE", "DAY", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DEFERRABLE", "DEFERRED", "DELETE", "DESC", "DESCRIBE", "DESCRIPTOR", "DIAGNOSTICS", "DISCONNECT", "DISTINCT", "DOMAIN", "DOUBLE", "DROP", "ELSE", "END", "END-EXEC", "ESCAPE", "EXCEPT", "EXCEPTION", "EXEC", "EXECUTE", "EXISTS", "EXTERNAL", "EXTRACT", "FALSE", "FETCH", "FIRST", "FLOAT", "FOR", "FOREIGN", "FOUND", "FROM", "FULL", "GET", "GLOBAL", "GO", "GOTO", "GRANT", "GROUP", "HAVING", "HOUR", "IDENTITY", "IMMEDIATE", "IN", "INDICATOR", "INITIALLY", "INNER", "INPUT", "INSENSITIVE", "INSERT", "INT", "INTEGER", "INTERSECT", "INTERVAL", "INTO", "IS", "ISOLATION", "JOIN", "KEY", "LANGUAGE", "LAST", "LEADING", "LEFT", "LEVEL", "LIKE", "LOCAL", "LOWER", "MATCH", "MAX", "MIN", "MINUTE", "MODULE", "MONTH", "NAMES", "NATIONAL", "NATURAL", "NCHAR", "NEXT", "NO", "NOT", "NULL", "NULLIF", "NUMERIC", "OCTET_LENGTH", "OF", "ON", "ONLY", "OPEN", "OPTION", "OR", "ORDER", "OUTER", "OUTPUT", "OVERLAPS", "PAD", "PARTIAL", "POSITION", "PRECISION", "PREPARE", "PRESERVE", "PRIMARY", "PRIOR", "PRIVILEGES", "PROCEDURE", "PUBLIC", "READ", "REAL", "REFERENCES", "RELATIVE", "RESTRICT", "REVOKE", "RIGHT", "ROLLBACK", "ROWS", "SCHEMA", "SCROLL", "SECOND", "SECTION", "SELECT", "SESSION", "SESSION_USER", "SET", "SIZE", "SMALLINT", "SOME", "SPACE", "SQL", "SQLCODE", "SQLERROR", "SQLSTATE", "SUBSTRING", "SUM", "SYSTEM_USER", "TABLE", "TEMPORARY", "THEN", "TIME", "TIMESTAMP", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TO", "TRAILING", "TRANSACTION", "TRANSLATE", "TRANSLATION", "TRIM", "TRUE", "UNION", "UNIQUE", "UNKNOWN", "UPDATE", "UPPER", "USAGE", "USER", "USING", "VALUE", "VALUES", "VARCHAR", "VARYING", "VIEW", "WHEN", "WHENEVER", "WHERE", "WITH", "WORK", "WRITE", "YEAR", "ZONE" };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  725 */   private static final String[] SQL2003_KEYWORDS = { "ABS", "ALL", "ALLOCATE", "ALTER", "AND", "ANY", "ARE", "ARRAY", "AS", "ASENSITIVE", "ASYMMETRIC", "AT", "ATOMIC", "AUTHORIZATION", "AVG", "BEGIN", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOOLEAN", "BOTH", "BY", "CALL", "CALLED", "CARDINALITY", "CASCADED", "CASE", "CAST", "CEIL", "CEILING", "CHAR", "CHARACTER", "CHARACTER_LENGTH", "CHAR_LENGTH", "CHECK", "CLOB", "CLOSE", "COALESCE", "COLLATE", "COLLECT", "COLUMN", "COMMIT", "CONDITION", "CONNECT", "CONSTRAINT", "CONVERT", "CORR", "CORRESPONDING", "COUNT", "COVAR_POP", "COVAR_SAMP", "CREATE", "CROSS", "CUBE", "CUME_DIST", "CURRENT", "CURRENT_DATE", "CURRENT_DEFAULT_TRANSFORM_GROUP", "CURRENT_PATH", "CURRENT_ROLE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_TRANSFORM_GROUP_FOR_TYPE", "CURRENT_USER", "CURSOR", "CYCLE", "DATE", "DAY", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELETE", "DENSE_RANK", "DEREF", "DESCRIBE", "DETERMINISTIC", "DISCONNECT", "DISTINCT", "DOUBLE", "DROP", "DYNAMIC", "EACH", "ELEMENT", "ELSE", "END", "END-EXEC", "ESCAPE", "EVERY", "EXCEPT", "EXEC", "EXECUTE", "EXISTS", "EXP", "EXTERNAL", "EXTRACT", "FALSE", "FETCH", "FILTER", "FLOAT", "FLOOR", "FOR", "FOREIGN", "FREE", "FROM", "FULL", "FUNCTION", "FUSION", "GET", "GLOBAL", "GRANT", "GROUP", "GROUPING", "HAVING", "HOLD", "HOUR", "IDENTITY", "IN", "INDICATOR", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT", "INTEGER", "INTERSECT", "INTERSECTION", "INTERVAL", "INTO", "IS", "JOIN", "LANGUAGE", "LARGE", "LATERAL", "LEADING", "LEFT", "LIKE", "LN", "LOCAL", "LOCALTIME", "LOCALTIMESTAMP", "LOWER", "MATCH", "MAX", "MEMBER", "MERGE", "METHOD", "MIN", "MINUTE", "MOD", "MODIFIES", "MODULE", "MONTH", "MULTISET", "NATIONAL", "NATURAL", "NCHAR", "NCLOB", "NEW", "NO", "NONE", "NORMALIZE", "NOT", "NULL", "NULLIF", "NUMERIC", "OCTET_LENGTH", "OF", "OLD", "ON", "ONLY", "OPEN", "OR", "ORDER", "OUT", "OUTER", "OVER", "OVERLAPS", "OVERLAY", "PARAMETER", "PARTITION", "PERCENTILE_CONT", "PERCENTILE_DISC", "PERCENT_RANK", "POSITION", "POWER", "PRECISION", "PREPARE", "PRIMARY", "PROCEDURE", "RANGE", "RANK", "READS", "REAL", "RECURSIVE", "REF", "REFERENCES", "REFERENCING", "REGR_AVGX", "REGR_AVGY", "REGR_COUNT", "REGR_INTERCEPT", "REGR_R2", "REGR_SLOPE", "REGR_SXX", "REGR_SXY", "REGR_SYY", "RELEASE", "RESULT", "RETURN", "RETURNS", "REVOKE", "RIGHT", "ROLLBACK", "ROLLUP", "ROW", "ROWS", "ROW_NUMBER", "SAVEPOINT", "SCOPE", "SCROLL", "SEARCH", "SECOND", "SELECT", "SENSITIVE", "SESSION_USER", "SET", "SIMILAR", "SMALLINT", "SOME", "SPECIFIC", "SPECIFICTYPE", "SQL", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SQRT", "START", "STATIC", "STDDEV_POP", "STDDEV_SAMP", "SUBMULTISET", "SUBSTRING", "SUM", "SYMMETRIC", "SYSTEM", "SYSTEM_USER", "TABLE", "TABLESAMPLE", "THEN", "TIME", "TIMESTAMP", "TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TO", "TRAILING", "TRANSLATE", "TRANSLATION", "TREAT", "TRIGGER", "TRIM", "TRUE", "UESCAPE", "UNION", "UNIQUE", "UNKNOWN", "UNNEST", "UPDATE", "UPPER", "USER", "USING", "VALUE", "VALUES", "VARCHAR", "VARYING", "VAR_POP", "VAR_SAMP", "WHEN", "WHENEVER", "WHERE", "WIDTH_BUCKET", "WINDOW", "WITH", "WITHIN", "WITHOUT", "YEAR" };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  749 */   private static volatile String mysqlKeywords = null;
/*      */   
/*      */ 
/*      */   protected MySQLConnection conn;
/*      */   
/*      */ 
/*  755 */   protected String database = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final String quotedId;
/*      */   
/*      */ 
/*      */ 
/*      */   private ExceptionInterceptor exceptionInterceptor;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static DatabaseMetaData getInstance(MySQLConnection connToSet, String databaseToSet, boolean checkForInfoSchema)
/*      */     throws SQLException
/*      */   {
/*  772 */     if (!Util.isJdbc4()) {
/*  773 */       if ((checkForInfoSchema) && (connToSet.getUseInformationSchema()) && (connToSet.versionMeetsMinimum(5, 0, 7))) {
/*  774 */         return new DatabaseMetaDataUsingInfoSchema(connToSet, databaseToSet);
/*      */       }
/*      */       
/*  777 */       return new DatabaseMetaData(connToSet, databaseToSet);
/*      */     }
/*      */     
/*  780 */     if ((checkForInfoSchema) && (connToSet.getUseInformationSchema()) && (connToSet.versionMeetsMinimum(5, 0, 7)))
/*      */     {
/*  782 */       return (DatabaseMetaData)Util.handleNewInstance(JDBC_4_DBMD_IS_CTOR, new Object[] { connToSet, databaseToSet }, connToSet.getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/*  786 */     return (DatabaseMetaData)Util.handleNewInstance(JDBC_4_DBMD_SHOW_CTOR, new Object[] { connToSet, databaseToSet }, connToSet.getExceptionInterceptor());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected DatabaseMetaData(MySQLConnection connToSet, String databaseToSet)
/*      */   {
/*  796 */     this.conn = connToSet;
/*  797 */     this.database = databaseToSet;
/*  798 */     this.exceptionInterceptor = this.conn.getExceptionInterceptor();
/*      */     
/*  800 */     String identifierQuote = null;
/*      */     try {
/*  802 */       identifierQuote = getIdentifierQuoteString();
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  805 */       AssertionFailedException.shouldNotHappen(sqlEx);
/*      */     } finally {
/*  807 */       this.quotedId = identifierQuote;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean allProceduresAreCallable()
/*      */     throws SQLException
/*      */   {
/*  819 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean allTablesAreSelectable()
/*      */     throws SQLException
/*      */   {
/*  829 */     return false;
/*      */   }
/*      */   
/*      */   private ResultSet buildResultSet(Field[] fields, ArrayList<ResultSetRow> rows) throws SQLException {
/*  833 */     return buildResultSet(fields, rows, this.conn);
/*      */   }
/*      */   
/*      */   static ResultSet buildResultSet(Field[] fields, ArrayList<ResultSetRow> rows, MySQLConnection c) throws SQLException {
/*  837 */     int fieldsLength = fields.length;
/*      */     
/*  839 */     for (int i = 0; i < fieldsLength; i++) {
/*  840 */       int jdbcType = fields[i].getSQLType();
/*      */       
/*  842 */       switch (jdbcType) {
/*      */       case -1: 
/*      */       case 1: 
/*      */       case 12: 
/*  846 */         fields[i].setEncoding(c.getCharacterSetMetadata(), c);
/*  847 */         break;
/*      */       }
/*      */       
/*      */       
/*      */ 
/*  852 */       fields[i].setConnection(c);
/*  853 */       fields[i].setUseOldNameMetadata(true);
/*      */     }
/*      */     
/*  856 */     return ResultSetImpl.getInstance(c.getCatalog(), fields, new RowDataStatic(rows), c, null, false);
/*      */   }
/*      */   
/*      */   protected void convertToJdbcFunctionList(String catalog, ResultSet proceduresRs, boolean needsClientFiltering, String db, List<ComparableWrapper<String, ResultSetRow>> procedureRows, int nameIndex, Field[] fields) throws SQLException
/*      */   {
/*  861 */     while (proceduresRs.next()) {
/*  862 */       boolean shouldAdd = true;
/*      */       
/*  864 */       if (needsClientFiltering) {
/*  865 */         shouldAdd = false;
/*      */         
/*  867 */         String procDb = proceduresRs.getString(1);
/*      */         
/*  869 */         if ((db == null) && (procDb == null)) {
/*  870 */           shouldAdd = true;
/*  871 */         } else if ((db != null) && (db.equals(procDb))) {
/*  872 */           shouldAdd = true;
/*      */         }
/*      */       }
/*      */       
/*  876 */       if (shouldAdd) {
/*  877 */         String functionName = proceduresRs.getString(nameIndex);
/*      */         
/*  879 */         byte[][] rowData = (byte[][])null;
/*      */         
/*  881 */         if ((fields != null) && (fields.length == 9))
/*      */         {
/*  883 */           rowData = new byte[9][];
/*  884 */           rowData[0] = (catalog == null ? null : s2b(catalog));
/*  885 */           rowData[1] = null;
/*  886 */           rowData[2] = s2b(functionName);
/*  887 */           rowData[3] = null;
/*  888 */           rowData[4] = null;
/*  889 */           rowData[5] = null;
/*  890 */           rowData[6] = s2b(proceduresRs.getString("comment"));
/*  891 */           rowData[7] = s2b(Integer.toString(2));
/*  892 */           rowData[8] = s2b(functionName);
/*      */         }
/*      */         else {
/*  895 */           rowData = new byte[6][];
/*      */           
/*  897 */           rowData[0] = (catalog == null ? null : s2b(catalog));
/*  898 */           rowData[1] = null;
/*  899 */           rowData[2] = s2b(functionName);
/*  900 */           rowData[3] = s2b(proceduresRs.getString("comment"));
/*  901 */           rowData[4] = s2b(Integer.toString(getJDBC4FunctionNoTableConstant()));
/*  902 */           rowData[5] = s2b(functionName);
/*      */         }
/*      */         
/*  905 */         procedureRows.add(new ComparableWrapper(getFullyQualifiedName(catalog, functionName), new ByteArrayRow(rowData, getExceptionInterceptor())));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected String getFullyQualifiedName(String catalog, String entity)
/*      */   {
/*  915 */     StringBuilder fullyQualifiedName = new StringBuilder(StringUtils.quoteIdentifier(catalog == null ? "" : catalog, this.quotedId, this.conn.getPedantic()));
/*      */     
/*  917 */     fullyQualifiedName.append('.');
/*  918 */     fullyQualifiedName.append(StringUtils.quoteIdentifier(entity, this.quotedId, this.conn.getPedantic()));
/*  919 */     return fullyQualifiedName.toString();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected int getJDBC4FunctionNoTableConstant()
/*      */   {
/*  929 */     return 0;
/*      */   }
/*      */   
/*      */   protected void convertToJdbcProcedureList(boolean fromSelect, String catalog, ResultSet proceduresRs, boolean needsClientFiltering, String db, List<ComparableWrapper<String, ResultSetRow>> procedureRows, int nameIndex) throws SQLException
/*      */   {
/*  934 */     while (proceduresRs.next()) {
/*  935 */       boolean shouldAdd = true;
/*      */       
/*  937 */       if (needsClientFiltering) {
/*  938 */         shouldAdd = false;
/*      */         
/*  940 */         String procDb = proceduresRs.getString(1);
/*      */         
/*  942 */         if ((db == null) && (procDb == null)) {
/*  943 */           shouldAdd = true;
/*  944 */         } else if ((db != null) && (db.equals(procDb))) {
/*  945 */           shouldAdd = true;
/*      */         }
/*      */       }
/*      */       
/*  949 */       if (shouldAdd) {
/*  950 */         String procedureName = proceduresRs.getString(nameIndex);
/*  951 */         byte[][] rowData = new byte[9][];
/*  952 */         rowData[0] = (catalog == null ? null : s2b(catalog));
/*  953 */         rowData[1] = null;
/*  954 */         rowData[2] = s2b(procedureName);
/*  955 */         rowData[3] = null;
/*  956 */         rowData[4] = null;
/*  957 */         rowData[5] = null;
/*  958 */         rowData[6] = s2b(proceduresRs.getString("comment"));
/*      */         
/*  960 */         boolean isFunction = fromSelect ? "FUNCTION".equalsIgnoreCase(proceduresRs.getString("type")) : false;
/*  961 */         rowData[7] = s2b(isFunction ? Integer.toString(2) : Integer.toString(1));
/*      */         
/*  963 */         rowData[8] = s2b(procedureName);
/*      */         
/*  965 */         procedureRows.add(new ComparableWrapper(getFullyQualifiedName(catalog, procedureName), new ByteArrayRow(rowData, getExceptionInterceptor())));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private ResultSetRow convertTypeDescriptorToProcedureRow(byte[] procNameAsBytes, byte[] procCatAsBytes, String paramName, boolean isOutParam, boolean isInParam, boolean isReturnParam, TypeDescriptor typeDesc, boolean forGetFunctionColumns, int ordinal)
/*      */     throws SQLException
/*      */   {
/*  973 */     byte[][] row = forGetFunctionColumns ? new byte[17][] : new byte[20][];
/*  974 */     row[0] = procCatAsBytes;
/*  975 */     row[1] = null;
/*  976 */     row[2] = procNameAsBytes;
/*  977 */     row[3] = s2b(paramName);
/*  978 */     row[4] = s2b(String.valueOf(getColumnType(isOutParam, isInParam, isReturnParam, forGetFunctionColumns)));
/*  979 */     row[5] = s2b(Short.toString(typeDesc.dataType));
/*  980 */     row[6] = s2b(typeDesc.typeName);
/*  981 */     row[7] = (typeDesc.columnSize == null ? null : s2b(typeDesc.columnSize.toString()));
/*  982 */     row[8] = row[7];
/*  983 */     row[9] = (typeDesc.decimalDigits == null ? null : s2b(typeDesc.decimalDigits.toString()));
/*  984 */     row[10] = s2b(Integer.toString(typeDesc.numPrecRadix));
/*      */     
/*  986 */     switch (typeDesc.nullability) {
/*      */     case 0: 
/*  988 */       row[11] = s2b(String.valueOf(0));
/*  989 */       break;
/*      */     
/*      */     case 1: 
/*  992 */       row[11] = s2b(String.valueOf(1));
/*  993 */       break;
/*      */     
/*      */     case 2: 
/*  996 */       row[11] = s2b(String.valueOf(2));
/*  997 */       break;
/*      */     
/*      */     default: 
/* 1000 */       throw SQLError.createSQLException("Internal error while parsing callable statement metadata (unknown nullability value fount)", "S1000", getExceptionInterceptor());
/*      */     }
/*      */     
/*      */     
/* 1004 */     row[12] = null;
/*      */     
/* 1006 */     if (forGetFunctionColumns)
/*      */     {
/* 1008 */       row[13] = null;
/*      */       
/*      */ 
/* 1011 */       row[14] = s2b(String.valueOf(ordinal));
/*      */       
/*      */ 
/* 1014 */       row[15] = s2b(typeDesc.isNullable);
/*      */       
/*      */ 
/* 1017 */       row[16] = procNameAsBytes;
/*      */     }
/*      */     else {
/* 1020 */       row[13] = null;
/*      */       
/*      */ 
/* 1023 */       row[14] = null;
/*      */       
/*      */ 
/* 1026 */       row[15] = null;
/*      */       
/*      */ 
/* 1029 */       row[16] = null;
/*      */       
/*      */ 
/* 1032 */       row[17] = s2b(String.valueOf(ordinal));
/*      */       
/*      */ 
/* 1035 */       row[18] = s2b(typeDesc.isNullable);
/*      */       
/*      */ 
/* 1038 */       row[19] = procNameAsBytes;
/*      */     }
/*      */     
/* 1041 */     return new ByteArrayRow(row, getExceptionInterceptor());
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
/*      */   protected int getColumnType(boolean isOutParam, boolean isInParam, boolean isReturnParam, boolean forGetFunctionColumns)
/*      */   {
/* 1060 */     if ((isInParam) && (isOutParam))
/* 1061 */       return 2;
/* 1062 */     if (isInParam)
/* 1063 */       return 1;
/* 1064 */     if (isOutParam)
/* 1065 */       return 4;
/* 1066 */     if (isReturnParam) {
/* 1067 */       return 5;
/*      */     }
/* 1069 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected ExceptionInterceptor getExceptionInterceptor()
/*      */   {
/* 1076 */     return this.exceptionInterceptor;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean dataDefinitionCausesTransactionCommit()
/*      */     throws SQLException
/*      */   {
/* 1087 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean dataDefinitionIgnoredInTransactions()
/*      */     throws SQLException
/*      */   {
/* 1097 */     return false;
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
/*      */   public boolean deletesAreDetected(int type)
/*      */     throws SQLException
/*      */   {
/* 1112 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean doesMaxRowSizeIncludeBlobs()
/*      */     throws SQLException
/*      */   {
/* 1124 */     return true;
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
/*      */   public List<ResultSetRow> extractForeignKeyForTable(ArrayList<ResultSetRow> rows, ResultSet rs, String catalog)
/*      */     throws SQLException
/*      */   {
/* 1141 */     byte[][] row = new byte[3][];
/* 1142 */     row[0] = rs.getBytes(1);
/* 1143 */     row[1] = s2b("SUPPORTS_FK");
/*      */     
/* 1145 */     String createTableString = rs.getString(2);
/* 1146 */     StringTokenizer lineTokenizer = new StringTokenizer(createTableString, "\n");
/* 1147 */     StringBuilder commentBuf = new StringBuilder("comment; ");
/* 1148 */     boolean firstTime = true;
/*      */     
/* 1150 */     while (lineTokenizer.hasMoreTokens()) {
/* 1151 */       String line = lineTokenizer.nextToken().trim();
/*      */       
/* 1153 */       String constraintName = null;
/*      */       
/* 1155 */       if (StringUtils.startsWithIgnoreCase(line, "CONSTRAINT")) {
/* 1156 */         boolean usingBackTicks = true;
/* 1157 */         int beginPos = StringUtils.indexOfQuoteDoubleAware(line, this.quotedId, 0);
/*      */         
/* 1159 */         if (beginPos == -1) {
/* 1160 */           beginPos = line.indexOf("\"");
/* 1161 */           usingBackTicks = false;
/*      */         }
/*      */         
/* 1164 */         if (beginPos != -1) {
/* 1165 */           int endPos = -1;
/*      */           
/* 1167 */           if (usingBackTicks) {
/* 1168 */             endPos = StringUtils.indexOfQuoteDoubleAware(line, this.quotedId, beginPos + 1);
/*      */           } else {
/* 1170 */             endPos = StringUtils.indexOfQuoteDoubleAware(line, "\"", beginPos + 1);
/*      */           }
/*      */           
/* 1173 */           if (endPos != -1) {
/* 1174 */             constraintName = line.substring(beginPos + 1, endPos);
/* 1175 */             line = line.substring(endPos + 1, line.length()).trim();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1180 */       if (line.startsWith("FOREIGN KEY")) {
/* 1181 */         if (line.endsWith(",")) {
/* 1182 */           line = line.substring(0, line.length() - 1);
/*      */         }
/*      */         
/* 1185 */         int indexOfFK = line.indexOf("FOREIGN KEY");
/*      */         
/* 1187 */         String localColumnName = null;
/* 1188 */         String referencedCatalogName = StringUtils.quoteIdentifier(catalog, this.quotedId, this.conn.getPedantic());
/* 1189 */         String referencedTableName = null;
/* 1190 */         String referencedColumnName = null;
/*      */         
/* 1192 */         if (indexOfFK != -1) {
/* 1193 */           int afterFk = indexOfFK + "FOREIGN KEY".length();
/*      */           
/* 1195 */           int indexOfRef = StringUtils.indexOfIgnoreCase(afterFk, line, "REFERENCES", this.quotedId, this.quotedId, StringUtils.SEARCH_MODE__ALL);
/*      */           
/* 1197 */           if (indexOfRef != -1)
/*      */           {
/* 1199 */             int indexOfParenOpen = line.indexOf('(', afterFk);
/* 1200 */             int indexOfParenClose = StringUtils.indexOfIgnoreCase(indexOfParenOpen, line, ")", this.quotedId, this.quotedId, StringUtils.SEARCH_MODE__ALL);
/*      */             
/*      */ 
/* 1203 */             if ((indexOfParenOpen != -1) && (indexOfParenClose == -1)) {}
/*      */             
/*      */ 
/*      */ 
/* 1207 */             localColumnName = line.substring(indexOfParenOpen + 1, indexOfParenClose);
/*      */             
/* 1209 */             int afterRef = indexOfRef + "REFERENCES".length();
/*      */             
/* 1211 */             int referencedColumnBegin = StringUtils.indexOfIgnoreCase(afterRef, line, "(", this.quotedId, this.quotedId, StringUtils.SEARCH_MODE__ALL);
/*      */             
/*      */ 
/* 1214 */             if (referencedColumnBegin != -1) {
/* 1215 */               referencedTableName = line.substring(afterRef, referencedColumnBegin);
/*      */               
/* 1217 */               int referencedColumnEnd = StringUtils.indexOfIgnoreCase(referencedColumnBegin + 1, line, ")", this.quotedId, this.quotedId, StringUtils.SEARCH_MODE__ALL);
/*      */               
/*      */ 
/* 1220 */               if (referencedColumnEnd != -1) {
/* 1221 */                 referencedColumnName = line.substring(referencedColumnBegin + 1, referencedColumnEnd);
/*      */               }
/*      */               
/* 1224 */               int indexOfCatalogSep = StringUtils.indexOfIgnoreCase(0, referencedTableName, ".", this.quotedId, this.quotedId, StringUtils.SEARCH_MODE__ALL);
/*      */               
/*      */ 
/* 1227 */               if (indexOfCatalogSep != -1) {
/* 1228 */                 referencedCatalogName = referencedTableName.substring(0, indexOfCatalogSep);
/* 1229 */                 referencedTableName = referencedTableName.substring(indexOfCatalogSep + 1);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         
/* 1235 */         if (!firstTime) {
/* 1236 */           commentBuf.append("; ");
/*      */         } else {
/* 1238 */           firstTime = false;
/*      */         }
/*      */         
/* 1241 */         if (constraintName != null) {
/* 1242 */           commentBuf.append(constraintName);
/*      */         } else {
/* 1244 */           commentBuf.append("not_available");
/*      */         }
/*      */         
/* 1247 */         commentBuf.append("(");
/* 1248 */         commentBuf.append(localColumnName);
/* 1249 */         commentBuf.append(") REFER ");
/* 1250 */         commentBuf.append(referencedCatalogName);
/* 1251 */         commentBuf.append("/");
/* 1252 */         commentBuf.append(referencedTableName);
/* 1253 */         commentBuf.append("(");
/* 1254 */         commentBuf.append(referencedColumnName);
/* 1255 */         commentBuf.append(")");
/*      */         
/* 1257 */         int lastParenIndex = line.lastIndexOf(")");
/*      */         
/* 1259 */         if (lastParenIndex != line.length() - 1) {
/* 1260 */           String cascadeOptions = line.substring(lastParenIndex + 1);
/* 1261 */           commentBuf.append(" ");
/* 1262 */           commentBuf.append(cascadeOptions);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1267 */     row[2] = s2b(commentBuf.toString());
/* 1268 */     rows.add(new ByteArrayRow(row, getExceptionInterceptor()));
/*      */     
/* 1270 */     return rows;
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
/*      */   public ResultSet extractForeignKeyFromCreateTable(String catalog, String tableName)
/*      */     throws SQLException
/*      */   {
/* 1290 */     ArrayList<String> tableList = new ArrayList();
/* 1291 */     ResultSet rs = null;
/* 1292 */     java.sql.Statement stmt = null;
/*      */     
/* 1294 */     if (tableName != null) {
/* 1295 */       tableList.add(tableName);
/*      */     } else {
/*      */       try {
/* 1298 */         rs = getTables(catalog, "", "%", new String[] { "TABLE" });
/*      */         
/* 1300 */         while (rs.next()) {
/* 1301 */           tableList.add(rs.getString("TABLE_NAME"));
/*      */         }
/*      */       } finally {
/* 1304 */         if (rs != null) {
/* 1305 */           rs.close();
/*      */         }
/*      */         
/* 1308 */         rs = null;
/*      */       }
/*      */     }
/*      */     
/* 1312 */     Object rows = new ArrayList();
/* 1313 */     Field[] fields = new Field[3];
/* 1314 */     fields[0] = new Field("", "Name", 1, Integer.MAX_VALUE);
/* 1315 */     fields[1] = new Field("", "Type", 1, 255);
/* 1316 */     fields[2] = new Field("", "Comment", 1, Integer.MAX_VALUE);
/*      */     
/* 1318 */     int numTables = tableList.size();
/* 1319 */     stmt = this.conn.getMetadataSafeStatement();
/*      */     try
/*      */     {
/* 1322 */       for (int i = 0; i < numTables; i++) {
/* 1323 */         String tableToExtract = (String)tableList.get(i);
/*      */         
/* 1325 */         String query = "SHOW CREATE TABLE " + getFullyQualifiedName(catalog, tableToExtract);
/*      */         try
/*      */         {
/* 1328 */           rs = stmt.executeQuery(query);
/*      */         }
/*      */         catch (SQLException sqlEx) {
/* 1331 */           String sqlState = sqlEx.getSQLState();
/*      */           
/* 1333 */           if ((!"42S02".equals(sqlState)) && (sqlEx.getErrorCode() != 1146)) {
/* 1334 */             throw sqlEx;
/*      */           }
/*      */           
/* 1337 */           continue;
/*      */         }
/*      */         
/* 1340 */         while (rs.next()) {
/* 1341 */           extractForeignKeyForTable((ArrayList)rows, rs, catalog);
/*      */         }
/*      */       }
/*      */     } finally {
/* 1345 */       if (rs != null) {
/* 1346 */         rs.close();
/*      */       }
/*      */       
/* 1349 */       rs = null;
/*      */       
/* 1351 */       if (stmt != null) {
/* 1352 */         stmt.close();
/*      */       }
/*      */       
/* 1355 */       stmt = null;
/*      */     }
/*      */     
/* 1358 */     return buildResultSet(fields, (ArrayList)rows);
/*      */   }
/*      */   
/*      */ 
/*      */   public ResultSet getAttributes(String arg0, String arg1, String arg2, String arg3)
/*      */     throws SQLException
/*      */   {
/* 1365 */     Field[] fields = new Field[21];
/* 1366 */     fields[0] = new Field("", "TYPE_CAT", 1, 32);
/* 1367 */     fields[1] = new Field("", "TYPE_SCHEM", 1, 32);
/* 1368 */     fields[2] = new Field("", "TYPE_NAME", 1, 32);
/* 1369 */     fields[3] = new Field("", "ATTR_NAME", 1, 32);
/* 1370 */     fields[4] = new Field("", "DATA_TYPE", 5, 32);
/* 1371 */     fields[5] = new Field("", "ATTR_TYPE_NAME", 1, 32);
/* 1372 */     fields[6] = new Field("", "ATTR_SIZE", 4, 32);
/* 1373 */     fields[7] = new Field("", "DECIMAL_DIGITS", 4, 32);
/* 1374 */     fields[8] = new Field("", "NUM_PREC_RADIX", 4, 32);
/* 1375 */     fields[9] = new Field("", "NULLABLE ", 4, 32);
/* 1376 */     fields[10] = new Field("", "REMARKS", 1, 32);
/* 1377 */     fields[11] = new Field("", "ATTR_DEF", 1, 32);
/* 1378 */     fields[12] = new Field("", "SQL_DATA_TYPE", 4, 32);
/* 1379 */     fields[13] = new Field("", "SQL_DATETIME_SUB", 4, 32);
/* 1380 */     fields[14] = new Field("", "CHAR_OCTET_LENGTH", 4, 32);
/* 1381 */     fields[15] = new Field("", "ORDINAL_POSITION", 4, 32);
/* 1382 */     fields[16] = new Field("", "IS_NULLABLE", 1, 32);
/* 1383 */     fields[17] = new Field("", "SCOPE_CATALOG", 1, 32);
/* 1384 */     fields[18] = new Field("", "SCOPE_SCHEMA", 1, 32);
/* 1385 */     fields[19] = new Field("", "SCOPE_TABLE", 1, 32);
/* 1386 */     fields[20] = new Field("", "SOURCE_DATA_TYPE", 5, 32);
/*      */     
/* 1388 */     return buildResultSet(fields, new ArrayList());
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
/*      */   public ResultSet getBestRowIdentifier(String catalog, String schema, final String table, int scope, boolean nullable)
/*      */     throws SQLException
/*      */   {
/* 1434 */     if (table == null) {
/* 1435 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/* 1438 */     Field[] fields = new Field[8];
/* 1439 */     fields[0] = new Field("", "SCOPE", 5, 5);
/* 1440 */     fields[1] = new Field("", "COLUMN_NAME", 1, 32);
/* 1441 */     fields[2] = new Field("", "DATA_TYPE", 4, 32);
/* 1442 */     fields[3] = new Field("", "TYPE_NAME", 1, 32);
/* 1443 */     fields[4] = new Field("", "COLUMN_SIZE", 4, 10);
/* 1444 */     fields[5] = new Field("", "BUFFER_LENGTH", 4, 10);
/* 1445 */     fields[6] = new Field("", "DECIMAL_DIGITS", 5, 10);
/* 1446 */     fields[7] = new Field("", "PSEUDO_COLUMN", 5, 5);
/*      */     
/* 1448 */     final ArrayList<ResultSetRow> rows = new ArrayList();
/* 1449 */     final java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
/*      */     
/*      */     try
/*      */     {
/* 1453 */       new IterateBlock(getCatalogIterator(catalog))
/*      */       {
/*      */         void forEach(String catalogStr) throws SQLException {
/* 1456 */           ResultSet results = null;
/*      */           try
/*      */           {
/* 1459 */             StringBuilder queryBuf = new StringBuilder("SHOW COLUMNS FROM ");
/* 1460 */             queryBuf.append(StringUtils.quoteIdentifier(table, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()));
/* 1461 */             queryBuf.append(" FROM ");
/* 1462 */             queryBuf.append(StringUtils.quoteIdentifier(catalogStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()));
/*      */             
/* 1464 */             results = stmt.executeQuery(queryBuf.toString());
/*      */             
/* 1466 */             while (results.next()) {
/* 1467 */               String keyType = results.getString("Key");
/*      */               
/* 1469 */               if ((keyType != null) && 
/* 1470 */                 (StringUtils.startsWithIgnoreCase(keyType, "PRI"))) {
/* 1471 */                 byte[][] rowVal = new byte[8][];
/* 1472 */                 rowVal[0] = Integer.toString(2).getBytes();
/* 1473 */                 rowVal[1] = results.getBytes("Field");
/*      */                 
/* 1475 */                 String type = results.getString("Type");
/* 1476 */                 int size = MysqlIO.getMaxBuf();
/* 1477 */                 int decimals = 0;
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/* 1482 */                 if (type.indexOf("enum") != -1) {
/* 1483 */                   String temp = type.substring(type.indexOf("("), type.indexOf(")"));
/* 1484 */                   StringTokenizer tokenizer = new StringTokenizer(temp, ",");
/* 1485 */                   int maxLength = 0;
/*      */                   
/* 1487 */                   while (tokenizer.hasMoreTokens()) {
/* 1488 */                     maxLength = Math.max(maxLength, tokenizer.nextToken().length() - 2);
/*      */                   }
/*      */                   
/* 1491 */                   size = maxLength;
/* 1492 */                   decimals = 0;
/* 1493 */                   type = "enum";
/* 1494 */                 } else if (type.indexOf("(") != -1) {
/* 1495 */                   if (type.indexOf(",") != -1) {
/* 1496 */                     size = Integer.parseInt(type.substring(type.indexOf("(") + 1, type.indexOf(",")));
/* 1497 */                     decimals = Integer.parseInt(type.substring(type.indexOf(",") + 1, type.indexOf(")")));
/*      */                   } else {
/* 1499 */                     size = Integer.parseInt(type.substring(type.indexOf("(") + 1, type.indexOf(")")));
/*      */                   }
/*      */                   
/* 1502 */                   type = type.substring(0, type.indexOf("("));
/*      */                 }
/*      */                 
/* 1505 */                 rowVal[2] = DatabaseMetaData.this.s2b(String.valueOf(MysqlDefs.mysqlToJavaType(type)));
/* 1506 */                 rowVal[3] = DatabaseMetaData.this.s2b(type);
/* 1507 */                 rowVal[4] = Integer.toString(size + decimals).getBytes();
/* 1508 */                 rowVal[5] = Integer.toString(size + decimals).getBytes();
/* 1509 */                 rowVal[6] = Integer.toString(decimals).getBytes();
/* 1510 */                 rowVal[7] = Integer.toString(1).getBytes();
/*      */                 
/* 1512 */                 rows.add(new ByteArrayRow(rowVal, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (SQLException sqlEx) {
/* 1517 */             if (!"42S02".equals(sqlEx.getSQLState())) {
/* 1518 */               throw sqlEx;
/*      */             }
/*      */           } finally {
/* 1521 */             if (results != null) {
/*      */               try {
/* 1523 */                 results.close();
/*      */               }
/*      */               catch (Exception ex) {}
/*      */               
/* 1527 */               results = null;
/*      */             }
/*      */           }
/*      */         }
/*      */       }.doForAll();
/*      */     } finally {
/* 1533 */       if (stmt != null) {
/* 1534 */         stmt.close();
/*      */       }
/*      */     }
/*      */     
/* 1538 */     ResultSet results = buildResultSet(fields, rows);
/*      */     
/* 1540 */     return results;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void getCallStmtParameterTypes(String catalog, String quotedProcName, ProcedureType procType, String parameterNamePattern, List<ResultSetRow> resultRows, boolean forGetFunctionColumns)
/*      */     throws SQLException
/*      */   {
/* 1552 */     java.sql.Statement paramRetrievalStmt = null;
/* 1553 */     ResultSet paramRetrievalRs = null;
/*      */     
/* 1555 */     if (parameterNamePattern == null) {
/* 1556 */       if (this.conn.getNullNamePatternMatchesAll()) {
/* 1557 */         parameterNamePattern = "%";
/*      */       } else {
/* 1559 */         throw SQLError.createSQLException("Parameter/Column name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1564 */     String parameterDef = null;
/*      */     
/* 1566 */     byte[] procNameAsBytes = null;
/* 1567 */     byte[] procCatAsBytes = null;
/*      */     
/* 1569 */     boolean isProcedureInAnsiMode = false;
/* 1570 */     String storageDefnDelims = null;
/* 1571 */     String storageDefnClosures = null;
/*      */     try
/*      */     {
/* 1574 */       paramRetrievalStmt = this.conn.getMetadataSafeStatement();
/*      */       
/* 1576 */       String oldCatalog = this.conn.getCatalog();
/* 1577 */       if ((this.conn.lowerCaseTableNames()) && (catalog != null) && (catalog.length() != 0) && (oldCatalog != null) && (oldCatalog.length() != 0))
/*      */       {
/*      */ 
/* 1580 */         ResultSet rs = null;
/*      */         try
/*      */         {
/* 1583 */           this.conn.setCatalog(StringUtils.unQuoteIdentifier(catalog, this.quotedId));
/* 1584 */           rs = paramRetrievalStmt.executeQuery("SELECT DATABASE()");
/* 1585 */           rs.next();
/*      */           
/* 1587 */           catalog = rs.getString(1);
/*      */         }
/*      */         finally
/*      */         {
/* 1591 */           this.conn.setCatalog(oldCatalog);
/*      */           
/* 1593 */           if (rs != null) {
/* 1594 */             rs.close();
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1599 */       if (paramRetrievalStmt.getMaxRows() != 0) {
/* 1600 */         paramRetrievalStmt.setMaxRows(0);
/*      */       }
/*      */       
/* 1603 */       int dotIndex = -1;
/*      */       
/* 1605 */       if (!" ".equals(this.quotedId)) {
/* 1606 */         dotIndex = StringUtils.indexOfIgnoreCase(0, quotedProcName, ".", this.quotedId, this.quotedId, this.conn.isNoBackslashEscapesSet() ? StringUtils.SEARCH_MODE__MRK_COM_WS : StringUtils.SEARCH_MODE__ALL);
/*      */       }
/*      */       else {
/* 1609 */         dotIndex = quotedProcName.indexOf(".");
/*      */       }
/*      */       
/* 1612 */       String dbName = null;
/*      */       
/* 1614 */       if ((dotIndex != -1) && (dotIndex + 1 < quotedProcName.length())) {
/* 1615 */         dbName = quotedProcName.substring(0, dotIndex);
/* 1616 */         quotedProcName = quotedProcName.substring(dotIndex + 1);
/*      */       } else {
/* 1618 */         dbName = StringUtils.quoteIdentifier(catalog, this.quotedId, this.conn.getPedantic());
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1623 */       String tmpProcName = StringUtils.unQuoteIdentifier(quotedProcName, this.quotedId);
/*      */       try {
/* 1625 */         procNameAsBytes = StringUtils.getBytes(tmpProcName, "UTF-8");
/*      */       } catch (UnsupportedEncodingException ueEx) {
/* 1627 */         procNameAsBytes = s2b(tmpProcName);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1632 */       tmpProcName = StringUtils.unQuoteIdentifier(dbName, this.quotedId);
/*      */       try {
/* 1634 */         procCatAsBytes = StringUtils.getBytes(tmpProcName, "UTF-8");
/*      */       } catch (UnsupportedEncodingException ueEx) {
/* 1636 */         procCatAsBytes = s2b(tmpProcName);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1642 */       StringBuilder procNameBuf = new StringBuilder();
/* 1643 */       procNameBuf.append(dbName);
/* 1644 */       procNameBuf.append('.');
/* 1645 */       procNameBuf.append(quotedProcName);
/*      */       
/* 1647 */       String fieldName = null;
/* 1648 */       if (procType == ProcedureType.PROCEDURE) {
/* 1649 */         paramRetrievalRs = paramRetrievalStmt.executeQuery("SHOW CREATE PROCEDURE " + procNameBuf.toString());
/* 1650 */         fieldName = "Create Procedure";
/*      */       } else {
/* 1652 */         paramRetrievalRs = paramRetrievalStmt.executeQuery("SHOW CREATE FUNCTION " + procNameBuf.toString());
/* 1653 */         fieldName = "Create Function";
/*      */       }
/*      */       
/* 1656 */       if (paramRetrievalRs.next()) {
/* 1657 */         String procedureDef = paramRetrievalRs.getString(fieldName);
/*      */         
/* 1659 */         if ((!this.conn.getNoAccessToProcedureBodies()) && ((procedureDef == null) || (procedureDef.length() == 0))) {
/* 1660 */           throw SQLError.createSQLException("User does not have access to metadata required to determine stored procedure parameter types. If rights can not be granted, configure connection with \"noAccessToProcedureBodies=true\" to have driver generate parameters that represent INOUT strings irregardless of actual parameter types.", "S1000", getExceptionInterceptor());
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         try
/*      */         {
/* 1668 */           String sqlMode = paramRetrievalRs.getString("sql_mode");
/*      */           
/* 1670 */           if (StringUtils.indexOfIgnoreCase(sqlMode, "ANSI") != -1) {
/* 1671 */             isProcedureInAnsiMode = true;
/*      */           }
/*      */         }
/*      */         catch (SQLException sqlEx) {}
/*      */         
/*      */ 
/* 1677 */         String identifierMarkers = isProcedureInAnsiMode ? "`\"" : "`";
/* 1678 */         String identifierAndStringMarkers = "'" + identifierMarkers;
/* 1679 */         storageDefnDelims = "(" + identifierMarkers;
/* 1680 */         storageDefnClosures = ")" + identifierMarkers;
/*      */         
/* 1682 */         if ((procedureDef != null) && (procedureDef.length() != 0))
/*      */         {
/* 1684 */           procedureDef = StringUtils.stripComments(procedureDef, identifierAndStringMarkers, identifierAndStringMarkers, true, false, true, true);
/*      */           
/* 1686 */           int openParenIndex = StringUtils.indexOfIgnoreCase(0, procedureDef, "(", this.quotedId, this.quotedId, this.conn.isNoBackslashEscapesSet() ? StringUtils.SEARCH_MODE__MRK_COM_WS : StringUtils.SEARCH_MODE__ALL);
/*      */           
/* 1688 */           int endOfParamDeclarationIndex = 0;
/*      */           
/* 1690 */           endOfParamDeclarationIndex = endPositionOfParameterDeclaration(openParenIndex, procedureDef, this.quotedId);
/*      */           
/* 1692 */           if (procType == ProcedureType.FUNCTION)
/*      */           {
/*      */ 
/*      */ 
/* 1696 */             int returnsIndex = StringUtils.indexOfIgnoreCase(0, procedureDef, " RETURNS ", this.quotedId, this.quotedId, this.conn.isNoBackslashEscapesSet() ? StringUtils.SEARCH_MODE__MRK_COM_WS : StringUtils.SEARCH_MODE__ALL);
/*      */             
/*      */ 
/* 1699 */             int endReturnsDef = findEndOfReturnsClause(procedureDef, returnsIndex);
/*      */             
/*      */ 
/*      */ 
/* 1703 */             int declarationStart = returnsIndex + "RETURNS ".length();
/*      */             
/* 1705 */             while ((declarationStart < procedureDef.length()) && 
/* 1706 */               (Character.isWhitespace(procedureDef.charAt(declarationStart)))) {
/* 1707 */               declarationStart++;
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */ 
/* 1713 */             String returnsDefn = procedureDef.substring(declarationStart, endReturnsDef).trim();
/* 1714 */             TypeDescriptor returnDescriptor = new TypeDescriptor(returnsDefn, "YES");
/*      */             
/* 1716 */             resultRows.add(convertTypeDescriptorToProcedureRow(procNameAsBytes, procCatAsBytes, "", false, false, true, returnDescriptor, forGetFunctionColumns, 0));
/*      */           }
/*      */           
/*      */ 
/* 1720 */           if ((openParenIndex == -1) || (endOfParamDeclarationIndex == -1))
/*      */           {
/* 1722 */             throw SQLError.createSQLException("Internal error when parsing callable statement metadata", "S1000", getExceptionInterceptor());
/*      */           }
/*      */           
/*      */ 
/* 1726 */           parameterDef = procedureDef.substring(openParenIndex + 1, endOfParamDeclarationIndex);
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1731 */       SQLException sqlExRethrow = null;
/*      */       
/* 1733 */       if (paramRetrievalRs != null) {
/*      */         try {
/* 1735 */           paramRetrievalRs.close();
/*      */         } catch (SQLException sqlEx) {
/* 1737 */           sqlExRethrow = sqlEx;
/*      */         }
/*      */         
/* 1740 */         paramRetrievalRs = null;
/*      */       }
/*      */       
/* 1743 */       if (paramRetrievalStmt != null) {
/*      */         try {
/* 1745 */           paramRetrievalStmt.close();
/*      */         } catch (SQLException sqlEx) {
/* 1747 */           sqlExRethrow = sqlEx;
/*      */         }
/*      */         
/* 1750 */         paramRetrievalStmt = null;
/*      */       }
/*      */       
/* 1753 */       if (sqlExRethrow != null) {
/* 1754 */         throw sqlExRethrow;
/*      */       }
/*      */     }
/*      */     
/* 1758 */     if (parameterDef != null) {
/* 1759 */       int ordinal = 1;
/*      */       
/* 1761 */       List<String> parseList = StringUtils.split(parameterDef, ",", storageDefnDelims, storageDefnClosures, true);
/*      */       
/* 1763 */       int parseListLen = parseList.size();
/*      */       
/* 1765 */       for (int i = 0; i < parseListLen; i++) {
/* 1766 */         String declaration = (String)parseList.get(i);
/*      */         
/* 1768 */         if (declaration.trim().length() == 0) {
/*      */           break;
/*      */         }
/*      */         
/*      */ 
/* 1773 */         declaration = declaration.replaceAll("[\\t\\n\\x0B\\f\\r]", " ");
/* 1774 */         StringTokenizer declarationTok = new StringTokenizer(declaration, " \t");
/*      */         
/* 1776 */         String paramName = null;
/* 1777 */         boolean isOutParam = false;
/* 1778 */         boolean isInParam = false;
/*      */         
/* 1780 */         if (declarationTok.hasMoreTokens()) {
/* 1781 */           String possibleParamName = declarationTok.nextToken();
/*      */           
/* 1783 */           if (possibleParamName.equalsIgnoreCase("OUT")) {
/* 1784 */             isOutParam = true;
/*      */             
/* 1786 */             if (declarationTok.hasMoreTokens()) {
/* 1787 */               paramName = declarationTok.nextToken();
/*      */             } else {
/* 1789 */               throw SQLError.createSQLException("Internal error when parsing callable statement metadata (missing parameter name)", "S1000", getExceptionInterceptor());
/*      */             }
/*      */           }
/* 1792 */           else if (possibleParamName.equalsIgnoreCase("INOUT")) {
/* 1793 */             isOutParam = true;
/* 1794 */             isInParam = true;
/*      */             
/* 1796 */             if (declarationTok.hasMoreTokens()) {
/* 1797 */               paramName = declarationTok.nextToken();
/*      */             } else {
/* 1799 */               throw SQLError.createSQLException("Internal error when parsing callable statement metadata (missing parameter name)", "S1000", getExceptionInterceptor());
/*      */             }
/*      */           }
/* 1802 */           else if (possibleParamName.equalsIgnoreCase("IN")) {
/* 1803 */             isOutParam = false;
/* 1804 */             isInParam = true;
/*      */             
/* 1806 */             if (declarationTok.hasMoreTokens()) {
/* 1807 */               paramName = declarationTok.nextToken();
/*      */             } else {
/* 1809 */               throw SQLError.createSQLException("Internal error when parsing callable statement metadata (missing parameter name)", "S1000", getExceptionInterceptor());
/*      */             }
/*      */           }
/*      */           else {
/* 1813 */             isOutParam = false;
/* 1814 */             isInParam = true;
/*      */             
/* 1816 */             paramName = possibleParamName;
/*      */           }
/*      */           
/* 1819 */           TypeDescriptor typeDesc = null;
/*      */           
/* 1821 */           if (declarationTok.hasMoreTokens()) {
/* 1822 */             StringBuilder typeInfoBuf = new StringBuilder(declarationTok.nextToken());
/*      */             
/* 1824 */             while (declarationTok.hasMoreTokens()) {
/* 1825 */               typeInfoBuf.append(" ");
/* 1826 */               typeInfoBuf.append(declarationTok.nextToken());
/*      */             }
/*      */             
/* 1829 */             String typeInfo = typeInfoBuf.toString();
/*      */             
/* 1831 */             typeDesc = new TypeDescriptor(typeInfo, "YES");
/*      */           } else {
/* 1833 */             throw SQLError.createSQLException("Internal error when parsing callable statement metadata (missing parameter type)", "S1000", getExceptionInterceptor());
/*      */           }
/*      */           
/*      */ 
/* 1837 */           if (((paramName.startsWith("`")) && (paramName.endsWith("`"))) || ((isProcedureInAnsiMode) && (paramName.startsWith("\"")) && (paramName.endsWith("\""))))
/*      */           {
/* 1839 */             paramName = paramName.substring(1, paramName.length() - 1);
/*      */           }
/*      */           
/* 1842 */           int wildCompareRes = StringUtils.wildCompare(paramName, parameterNamePattern);
/*      */           
/* 1844 */           if (wildCompareRes != -1) {
/* 1845 */             ResultSetRow row = convertTypeDescriptorToProcedureRow(procNameAsBytes, procCatAsBytes, paramName, isOutParam, isInParam, false, typeDesc, forGetFunctionColumns, ordinal++);
/*      */             
/*      */ 
/* 1848 */             resultRows.add(row);
/*      */           }
/*      */         } else {
/* 1851 */           throw SQLError.createSQLException("Internal error when parsing callable statement metadata (unknown output from 'SHOW CREATE PROCEDURE')", "S1000", getExceptionInterceptor());
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private int endPositionOfParameterDeclaration(int beginIndex, String procedureDef, String quoteChar)
/*      */     throws SQLException
/*      */   {
/* 1877 */     int currentPos = beginIndex + 1;
/* 1878 */     int parenDepth = 1;
/*      */     
/* 1880 */     while ((parenDepth > 0) && (currentPos < procedureDef.length())) {
/* 1881 */       int closedParenIndex = StringUtils.indexOfIgnoreCase(currentPos, procedureDef, ")", quoteChar, quoteChar, this.conn.isNoBackslashEscapesSet() ? StringUtils.SEARCH_MODE__MRK_COM_WS : StringUtils.SEARCH_MODE__ALL);
/*      */       
/*      */ 
/* 1884 */       if (closedParenIndex != -1) {
/* 1885 */         int nextOpenParenIndex = StringUtils.indexOfIgnoreCase(currentPos, procedureDef, "(", quoteChar, quoteChar, this.conn.isNoBackslashEscapesSet() ? StringUtils.SEARCH_MODE__MRK_COM_WS : StringUtils.SEARCH_MODE__ALL);
/*      */         
/*      */ 
/* 1888 */         if ((nextOpenParenIndex != -1) && (nextOpenParenIndex < closedParenIndex)) {
/* 1889 */           parenDepth++;
/* 1890 */           currentPos = closedParenIndex + 1;
/*      */         } else {
/* 1892 */           parenDepth--;
/* 1893 */           currentPos = closedParenIndex;
/*      */         }
/*      */       }
/*      */       else {
/* 1897 */         throw SQLError.createSQLException("Internal error when parsing callable statement metadata", "S1000", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1902 */     return currentPos;
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
/*      */   private int findEndOfReturnsClause(String procedureDefn, int positionOfReturnKeyword)
/*      */     throws SQLException
/*      */   {
/* 1925 */     String openingMarkers = this.quotedId + "(";
/* 1926 */     String closingMarkers = this.quotedId + ")";
/*      */     
/* 1928 */     String[] tokens = { "LANGUAGE", "NOT", "DETERMINISTIC", "CONTAINS", "NO", "READ", "MODIFIES", "SQL", "COMMENT", "BEGIN", "RETURN" };
/*      */     
/* 1930 */     int startLookingAt = positionOfReturnKeyword + "RETURNS".length() + 1;
/*      */     
/* 1932 */     int endOfReturn = -1;
/*      */     
/* 1934 */     for (int i = 0; i < tokens.length; i++) {
/* 1935 */       int nextEndOfReturn = StringUtils.indexOfIgnoreCase(startLookingAt, procedureDefn, tokens[i], openingMarkers, closingMarkers, this.conn.isNoBackslashEscapesSet() ? StringUtils.SEARCH_MODE__MRK_COM_WS : StringUtils.SEARCH_MODE__ALL);
/*      */       
/*      */ 
/* 1938 */       if ((nextEndOfReturn != -1) && (
/* 1939 */         (endOfReturn == -1) || (nextEndOfReturn < endOfReturn))) {
/* 1940 */         endOfReturn = nextEndOfReturn;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1945 */     if (endOfReturn != -1) {
/* 1946 */       return endOfReturn;
/*      */     }
/*      */     
/*      */ 
/* 1950 */     endOfReturn = StringUtils.indexOfIgnoreCase(startLookingAt, procedureDefn, ":", openingMarkers, closingMarkers, this.conn.isNoBackslashEscapesSet() ? StringUtils.SEARCH_MODE__MRK_COM_WS : StringUtils.SEARCH_MODE__ALL);
/*      */     
/*      */ 
/* 1953 */     if (endOfReturn != -1)
/*      */     {
/* 1955 */       for (int i = endOfReturn; i > 0; i--) {
/* 1956 */         if (Character.isWhitespace(procedureDefn.charAt(i))) {
/* 1957 */           return i;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1964 */     throw SQLError.createSQLException("Internal error when parsing callable statement metadata", "S1000", getExceptionInterceptor());
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
/*      */   private int getCascadeDeleteOption(String cascadeOptions)
/*      */   {
/* 1977 */     int onDeletePos = cascadeOptions.indexOf("ON DELETE");
/*      */     
/* 1979 */     if (onDeletePos != -1) {
/* 1980 */       String deleteOptions = cascadeOptions.substring(onDeletePos, cascadeOptions.length());
/*      */       
/* 1982 */       if (deleteOptions.startsWith("ON DELETE CASCADE"))
/* 1983 */         return 0;
/* 1984 */       if (deleteOptions.startsWith("ON DELETE SET NULL"))
/* 1985 */         return 2;
/* 1986 */       if (deleteOptions.startsWith("ON DELETE RESTRICT"))
/* 1987 */         return 1;
/* 1988 */       if (deleteOptions.startsWith("ON DELETE NO ACTION")) {
/* 1989 */         return 3;
/*      */       }
/*      */     }
/*      */     
/* 1993 */     return 3;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private int getCascadeUpdateOption(String cascadeOptions)
/*      */   {
/* 2005 */     int onUpdatePos = cascadeOptions.indexOf("ON UPDATE");
/*      */     
/* 2007 */     if (onUpdatePos != -1) {
/* 2008 */       String updateOptions = cascadeOptions.substring(onUpdatePos, cascadeOptions.length());
/*      */       
/* 2010 */       if (updateOptions.startsWith("ON UPDATE CASCADE"))
/* 2011 */         return 0;
/* 2012 */       if (updateOptions.startsWith("ON UPDATE SET NULL"))
/* 2013 */         return 2;
/* 2014 */       if (updateOptions.startsWith("ON UPDATE RESTRICT"))
/* 2015 */         return 1;
/* 2016 */       if (updateOptions.startsWith("ON UPDATE NO ACTION")) {
/* 2017 */         return 3;
/*      */       }
/*      */     }
/*      */     
/* 2021 */     return 3;
/*      */   }
/*      */   
/*      */   protected IteratorWithCleanup<String> getCatalogIterator(String catalogSpec) throws SQLException { IteratorWithCleanup<String> allCatalogsIter;
/*      */     IteratorWithCleanup<String> allCatalogsIter;
/* 2026 */     if (catalogSpec != null) { IteratorWithCleanup<String> allCatalogsIter;
/* 2027 */       if (!catalogSpec.equals("")) { IteratorWithCleanup<String> allCatalogsIter;
/* 2028 */         if (this.conn.getPedantic()) {
/* 2029 */           allCatalogsIter = new SingleStringIterator(catalogSpec);
/*      */         } else {
/* 2031 */           allCatalogsIter = new SingleStringIterator(StringUtils.unQuoteIdentifier(catalogSpec, this.quotedId));
/*      */         }
/*      */       }
/*      */       else {
/* 2035 */         allCatalogsIter = new SingleStringIterator(this.database);
/*      */       } } else { IteratorWithCleanup<String> allCatalogsIter;
/* 2037 */       if (this.conn.getNullCatalogMeansCurrent())
/*      */       {
/* 2039 */         allCatalogsIter = new SingleStringIterator(this.database);
/*      */       } else {
/* 2041 */         allCatalogsIter = new ResultSetIterator(getCatalogs(), 1);
/*      */       }
/*      */     }
/* 2044 */     return allCatalogsIter;
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
/*      */   public ResultSet getCatalogs()
/*      */     throws SQLException
/*      */   {
/* 2062 */     ResultSet results = null;
/* 2063 */     java.sql.Statement stmt = null;
/*      */     try
/*      */     {
/* 2066 */       stmt = this.conn.getMetadataSafeStatement();
/* 2067 */       results = stmt.executeQuery("SHOW DATABASES");
/*      */       
/* 2069 */       int catalogsCount = 0;
/* 2070 */       if (results.last()) {
/* 2071 */         catalogsCount = results.getRow();
/* 2072 */         results.beforeFirst();
/*      */       }
/*      */       
/* 2075 */       List<String> resultsAsList = new ArrayList(catalogsCount);
/* 2076 */       while (results.next()) {
/* 2077 */         resultsAsList.add(results.getString(1));
/*      */       }
/* 2079 */       Collections.sort(resultsAsList);
/*      */       
/* 2081 */       Field[] fields = new Field[1];
/* 2082 */       fields[0] = new Field("", "TABLE_CAT", 12, results.getMetaData().getColumnDisplaySize(1));
/*      */       
/* 2084 */       ArrayList<ResultSetRow> tuples = new ArrayList(catalogsCount);
/* 2085 */       for (String cat : resultsAsList) {
/* 2086 */         byte[][] rowVal = new byte[1][];
/* 2087 */         rowVal[0] = s2b(cat);
/* 2088 */         tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */       }
/*      */       
/* 2091 */       return buildResultSet(fields, tuples);
/*      */     } finally {
/* 2093 */       if (results != null) {
/*      */         try {
/* 2095 */           results.close();
/*      */         } catch (SQLException sqlEx) {
/* 2097 */           AssertionFailedException.shouldNotHappen(sqlEx);
/*      */         }
/*      */         
/* 2100 */         results = null;
/*      */       }
/*      */       
/* 2103 */       if (stmt != null) {
/*      */         try {
/* 2105 */           stmt.close();
/*      */         } catch (SQLException sqlEx) {
/* 2107 */           AssertionFailedException.shouldNotHappen(sqlEx);
/*      */         }
/*      */         
/* 2110 */         stmt = null;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getCatalogSeparator()
/*      */     throws SQLException
/*      */   {
/* 2122 */     return ".";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getCatalogTerm()
/*      */     throws SQLException
/*      */   {
/* 2135 */     return "database";
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
/*      */   public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 2171 */     Field[] fields = new Field[8];
/* 2172 */     fields[0] = new Field("", "TABLE_CAT", 1, 64);
/* 2173 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 1);
/* 2174 */     fields[2] = new Field("", "TABLE_NAME", 1, 64);
/* 2175 */     fields[3] = new Field("", "COLUMN_NAME", 1, 64);
/* 2176 */     fields[4] = new Field("", "GRANTOR", 1, 77);
/* 2177 */     fields[5] = new Field("", "GRANTEE", 1, 77);
/* 2178 */     fields[6] = new Field("", "PRIVILEGE", 1, 64);
/* 2179 */     fields[7] = new Field("", "IS_GRANTABLE", 1, 3);
/*      */     
/* 2181 */     String grantQuery = "SELECT c.host, c.db, t.grantor, c.user, c.table_name, c.column_name, c.column_priv FROM mysql.columns_priv c, mysql.tables_priv t WHERE c.host = t.host AND c.db = t.db AND c.table_name = t.table_name AND c.db LIKE ? AND c.table_name = ? AND c.column_name LIKE ?";
/*      */     
/*      */ 
/*      */ 
/* 2185 */     PreparedStatement pStmt = null;
/* 2186 */     ResultSet results = null;
/* 2187 */     ArrayList<ResultSetRow> grantRows = new ArrayList();
/*      */     try
/*      */     {
/* 2190 */       pStmt = prepareMetaDataSafeStatement(grantQuery);
/*      */       
/* 2192 */       pStmt.setString(1, (catalog != null) && (catalog.length() != 0) ? catalog : "%");
/* 2193 */       pStmt.setString(2, table);
/* 2194 */       pStmt.setString(3, columnNamePattern);
/*      */       
/* 2196 */       results = pStmt.executeQuery();
/*      */       
/* 2198 */       while (results.next()) {
/* 2199 */         String host = results.getString(1);
/* 2200 */         String db = results.getString(2);
/* 2201 */         String grantor = results.getString(3);
/* 2202 */         String user = results.getString(4);
/*      */         
/* 2204 */         if ((user == null) || (user.length() == 0)) {
/* 2205 */           user = "%";
/*      */         }
/*      */         
/* 2208 */         StringBuilder fullUser = new StringBuilder(user);
/*      */         
/* 2210 */         if ((host != null) && (this.conn.getUseHostsInPrivileges())) {
/* 2211 */           fullUser.append("@");
/* 2212 */           fullUser.append(host);
/*      */         }
/*      */         
/* 2215 */         String columnName = results.getString(6);
/* 2216 */         String allPrivileges = results.getString(7);
/*      */         
/* 2218 */         if (allPrivileges != null) {
/* 2219 */           allPrivileges = allPrivileges.toUpperCase(Locale.ENGLISH);
/*      */           
/* 2221 */           StringTokenizer st = new StringTokenizer(allPrivileges, ",");
/*      */           
/* 2223 */           while (st.hasMoreTokens()) {
/* 2224 */             String privilege = st.nextToken().trim();
/* 2225 */             byte[][] tuple = new byte[8][];
/* 2226 */             tuple[0] = s2b(db);
/* 2227 */             tuple[1] = null;
/* 2228 */             tuple[2] = s2b(table);
/* 2229 */             tuple[3] = s2b(columnName);
/*      */             
/* 2231 */             if (grantor != null) {
/* 2232 */               tuple[4] = s2b(grantor);
/*      */             } else {
/* 2234 */               tuple[4] = null;
/*      */             }
/*      */             
/* 2237 */             tuple[5] = s2b(fullUser.toString());
/* 2238 */             tuple[6] = s2b(privilege);
/* 2239 */             tuple[7] = null;
/* 2240 */             grantRows.add(new ByteArrayRow(tuple, getExceptionInterceptor()));
/*      */           }
/*      */         }
/*      */       }
/*      */     } finally {
/* 2245 */       if (results != null) {
/*      */         try {
/* 2247 */           results.close();
/*      */         }
/*      */         catch (Exception ex) {}
/*      */         
/* 2251 */         results = null;
/*      */       }
/*      */       
/* 2254 */       if (pStmt != null) {
/*      */         try {
/* 2256 */           pStmt.close();
/*      */         }
/*      */         catch (Exception ex) {}
/*      */         
/* 2260 */         pStmt = null;
/*      */       }
/*      */     }
/*      */     
/* 2264 */     return buildResultSet(fields, grantRows);
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
/*      */   public ResultSet getColumns(String catalog, final String schemaPattern, final String tableNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 2321 */     if (columnNamePattern == null) {
/* 2322 */       if (this.conn.getNullNamePatternMatchesAll()) {
/* 2323 */         columnNamePattern = "%";
/*      */       } else {
/* 2325 */         throw SQLError.createSQLException("Column name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 2330 */     final String colPattern = columnNamePattern;
/*      */     
/* 2332 */     Field[] fields = createColumnsFields();
/*      */     
/* 2334 */     final ArrayList<ResultSetRow> rows = new ArrayList();
/* 2335 */     final java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
/*      */     
/*      */     try
/*      */     {
/* 2339 */       new IterateBlock(getCatalogIterator(catalog))
/*      */       {
/*      */         void forEach(String catalogStr) throws SQLException
/*      */         {
/* 2343 */           ArrayList<String> tableNameList = new ArrayList();
/*      */           
/* 2345 */           if (tableNamePattern == null)
/*      */           {
/* 2347 */             ResultSet tables = null;
/*      */             try
/*      */             {
/* 2350 */               tables = DatabaseMetaData.this.getTables(catalogStr, schemaPattern, "%", new String[0]);
/*      */               
/* 2352 */               while (tables.next()) {
/* 2353 */                 String tableNameFromList = tables.getString("TABLE_NAME");
/* 2354 */                 tableNameList.add(tableNameFromList);
/*      */               }
/*      */             } finally {
/* 2357 */               if (tables != null) {
/*      */                 try {
/* 2359 */                   tables.close();
/*      */                 } catch (Exception sqlEx) {
/* 2361 */                   AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                 }
/*      */                 
/* 2364 */                 tables = null;
/*      */               }
/*      */             }
/*      */           } else {
/* 2368 */             ResultSet tables = null;
/*      */             try
/*      */             {
/* 2371 */               tables = DatabaseMetaData.this.getTables(catalogStr, schemaPattern, tableNamePattern, new String[0]);
/*      */               
/* 2373 */               while (tables.next()) {
/* 2374 */                 String tableNameFromList = tables.getString("TABLE_NAME");
/* 2375 */                 tableNameList.add(tableNameFromList);
/*      */               }
/*      */             } finally {
/* 2378 */               if (tables != null) {
/*      */                 try {
/* 2380 */                   tables.close();
/*      */                 } catch (SQLException sqlEx) {
/* 2382 */                   AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                 }
/*      */                 
/* 2385 */                 tables = null;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 2390 */           for (String tableName : tableNameList)
/*      */           {
/* 2392 */             ResultSet results = null;
/*      */             try
/*      */             {
/* 2395 */               StringBuilder queryBuf = new StringBuilder("SHOW ");
/*      */               
/* 2397 */               if (DatabaseMetaData.this.conn.versionMeetsMinimum(4, 1, 0)) {
/* 2398 */                 queryBuf.append("FULL ");
/*      */               }
/*      */               
/* 2401 */               queryBuf.append("COLUMNS FROM ");
/* 2402 */               queryBuf.append(StringUtils.quoteIdentifier(tableName, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()));
/* 2403 */               queryBuf.append(" FROM ");
/* 2404 */               queryBuf.append(StringUtils.quoteIdentifier(catalogStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()));
/* 2405 */               queryBuf.append(" LIKE ");
/* 2406 */               queryBuf.append(StringUtils.quoteIdentifier(colPattern, "'", true));
/*      */               
/*      */ 
/*      */ 
/*      */ 
/* 2411 */               boolean fixUpOrdinalsRequired = false;
/* 2412 */               Object ordinalFixUpMap = null;
/*      */               
/* 2414 */               if (!colPattern.equals("%")) {
/* 2415 */                 fixUpOrdinalsRequired = true;
/*      */                 
/* 2417 */                 StringBuilder fullColumnQueryBuf = new StringBuilder("SHOW ");
/*      */                 
/* 2419 */                 if (DatabaseMetaData.this.conn.versionMeetsMinimum(4, 1, 0)) {
/* 2420 */                   fullColumnQueryBuf.append("FULL ");
/*      */                 }
/*      */                 
/* 2423 */                 fullColumnQueryBuf.append("COLUMNS FROM ");
/* 2424 */                 fullColumnQueryBuf.append(StringUtils.quoteIdentifier(tableName, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()));
/*      */                 
/* 2426 */                 fullColumnQueryBuf.append(" FROM ");
/* 2427 */                 fullColumnQueryBuf.append(StringUtils.quoteIdentifier(catalogStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()));
/*      */                 
/*      */ 
/* 2430 */                 results = stmt.executeQuery(fullColumnQueryBuf.toString());
/*      */                 
/* 2432 */                 ordinalFixUpMap = new HashMap();
/*      */                 
/* 2434 */                 int fullOrdinalPos = 1;
/*      */                 
/* 2436 */                 while (results.next()) {
/* 2437 */                   String fullOrdColName = results.getString("Field");
/*      */                   
/* 2439 */                   ((Map)ordinalFixUpMap).put(fullOrdColName, Integer.valueOf(fullOrdinalPos++));
/*      */                 }
/*      */               }
/*      */               
/* 2443 */               results = stmt.executeQuery(queryBuf.toString());
/*      */               
/* 2445 */               int ordPos = 1;
/*      */               
/* 2447 */               while (results.next()) {
/* 2448 */                 byte[][] rowVal = new byte[24][];
/* 2449 */                 rowVal[0] = DatabaseMetaData.this.s2b(catalogStr);
/* 2450 */                 rowVal[1] = null;
/*      */                 
/*      */ 
/* 2453 */                 rowVal[2] = DatabaseMetaData.this.s2b(tableName);
/* 2454 */                 rowVal[3] = results.getBytes("Field");
/*      */                 
/* 2456 */                 DatabaseMetaData.TypeDescriptor typeDesc = new DatabaseMetaData.TypeDescriptor(DatabaseMetaData.this, results.getString("Type"), results.getString("Null"));
/*      */                 
/* 2458 */                 rowVal[4] = Short.toString(typeDesc.dataType).getBytes();
/*      */                 
/*      */ 
/* 2461 */                 rowVal[5] = DatabaseMetaData.this.s2b(typeDesc.typeName);
/*      */                 
/* 2463 */                 if (typeDesc.columnSize == null) {
/* 2464 */                   rowVal[6] = null;
/*      */                 } else {
/* 2466 */                   String collation = results.getString("Collation");
/* 2467 */                   int mbminlen = 1;
/* 2468 */                   if ((collation != null) && (("TEXT".equals(typeDesc.typeName)) || ("TINYTEXT".equals(typeDesc.typeName)) || ("MEDIUMTEXT".equals(typeDesc.typeName))))
/*      */                   {
/* 2470 */                     if ((collation.indexOf("ucs2") > -1) || (collation.indexOf("utf16") > -1)) {
/* 2471 */                       mbminlen = 2;
/* 2472 */                     } else if (collation.indexOf("utf32") > -1) {
/* 2473 */                       mbminlen = 4;
/*      */                     }
/*      */                   }
/* 2476 */                   rowVal[6] = (mbminlen == 1 ? DatabaseMetaData.this.s2b(typeDesc.columnSize.toString()) : DatabaseMetaData.this.s2b(Integer.valueOf(typeDesc.columnSize.intValue() / mbminlen).toString()));
/*      */                 }
/*      */                 
/* 2479 */                 rowVal[7] = DatabaseMetaData.this.s2b(Integer.toString(typeDesc.bufferLength));
/* 2480 */                 rowVal[8] = (typeDesc.decimalDigits == null ? null : DatabaseMetaData.this.s2b(typeDesc.decimalDigits.toString()));
/* 2481 */                 rowVal[9] = DatabaseMetaData.this.s2b(Integer.toString(typeDesc.numPrecRadix));
/* 2482 */                 rowVal[10] = DatabaseMetaData.this.s2b(Integer.toString(typeDesc.nullability));
/*      */                 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 try
/*      */                 {
/* 2491 */                   if (DatabaseMetaData.this.conn.versionMeetsMinimum(4, 1, 0)) {
/* 2492 */                     rowVal[11] = results.getBytes("Comment");
/*      */                   } else {
/* 2494 */                     rowVal[11] = results.getBytes("Extra");
/*      */                   }
/*      */                 } catch (Exception E) {
/* 2497 */                   rowVal[11] = new byte[0];
/*      */                 }
/*      */                 
/*      */ 
/* 2501 */                 rowVal[12] = results.getBytes("Default");
/*      */                 
/* 2503 */                 rowVal[13] = { 48 };
/* 2504 */                 rowVal[14] = { 48 };
/*      */                 
/* 2506 */                 if ((StringUtils.indexOfIgnoreCase(typeDesc.typeName, "CHAR") != -1) || (StringUtils.indexOfIgnoreCase(typeDesc.typeName, "BLOB") != -1) || (StringUtils.indexOfIgnoreCase(typeDesc.typeName, "TEXT") != -1) || (StringUtils.indexOfIgnoreCase(typeDesc.typeName, "BINARY") != -1))
/*      */                 {
/*      */ 
/*      */ 
/* 2510 */                   rowVal[15] = rowVal[6];
/*      */                 } else {
/* 2512 */                   rowVal[15] = null;
/*      */                 }
/*      */                 
/*      */ 
/* 2516 */                 if (!fixUpOrdinalsRequired) {
/* 2517 */                   rowVal[16] = Integer.toString(ordPos++).getBytes();
/*      */                 } else {
/* 2519 */                   String origColName = results.getString("Field");
/* 2520 */                   Integer realOrdinal = (Integer)((Map)ordinalFixUpMap).get(origColName);
/*      */                   
/* 2522 */                   if (realOrdinal != null) {
/* 2523 */                     rowVal[16] = realOrdinal.toString().getBytes();
/*      */                   } else {
/* 2525 */                     throw SQLError.createSQLException("Can not find column in full column list to determine true ordinal position.", "S1000", DatabaseMetaData.this.getExceptionInterceptor());
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/* 2530 */                 rowVal[17] = DatabaseMetaData.this.s2b(typeDesc.isNullable);
/*      */                 
/*      */ 
/* 2533 */                 rowVal[18] = null;
/* 2534 */                 rowVal[19] = null;
/* 2535 */                 rowVal[20] = null;
/* 2536 */                 rowVal[21] = null;
/*      */                 
/* 2538 */                 rowVal[22] = DatabaseMetaData.this.s2b("");
/*      */                 
/* 2540 */                 String extra = results.getString("Extra");
/*      */                 
/* 2542 */                 if (extra != null) {
/* 2543 */                   rowVal[22] = DatabaseMetaData.this.s2b(StringUtils.indexOfIgnoreCase(extra, "auto_increment") != -1 ? "YES" : "NO");
/* 2544 */                   rowVal[23] = DatabaseMetaData.this.s2b(StringUtils.indexOfIgnoreCase(extra, "generated") != -1 ? "YES" : "NO");
/*      */                 }
/*      */                 
/* 2547 */                 rows.add(new ByteArrayRow(rowVal, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */               }
/*      */             } finally {
/* 2550 */               if (results != null) {
/*      */                 try {
/* 2552 */                   results.close();
/*      */                 }
/*      */                 catch (Exception ex) {}
/*      */                 
/* 2556 */                 results = null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }.doForAll();
/*      */     } finally {
/* 2563 */       if (stmt != null) {
/* 2564 */         stmt.close();
/*      */       }
/*      */     }
/*      */     
/* 2568 */     ResultSet results = buildResultSet(fields, rows);
/*      */     
/* 2570 */     return results;
/*      */   }
/*      */   
/*      */   protected Field[] createColumnsFields() {
/* 2574 */     Field[] fields = new Field[24];
/* 2575 */     fields[0] = new Field("", "TABLE_CAT", 1, 255);
/* 2576 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 0);
/* 2577 */     fields[2] = new Field("", "TABLE_NAME", 1, 255);
/* 2578 */     fields[3] = new Field("", "COLUMN_NAME", 1, 32);
/* 2579 */     fields[4] = new Field("", "DATA_TYPE", 4, 5);
/* 2580 */     fields[5] = new Field("", "TYPE_NAME", 1, 16);
/* 2581 */     fields[6] = new Field("", "COLUMN_SIZE", 4, Integer.toString(Integer.MAX_VALUE).length());
/* 2582 */     fields[7] = new Field("", "BUFFER_LENGTH", 4, 10);
/* 2583 */     fields[8] = new Field("", "DECIMAL_DIGITS", 4, 10);
/* 2584 */     fields[9] = new Field("", "NUM_PREC_RADIX", 4, 10);
/* 2585 */     fields[10] = new Field("", "NULLABLE", 4, 10);
/* 2586 */     fields[11] = new Field("", "REMARKS", 1, 0);
/* 2587 */     fields[12] = new Field("", "COLUMN_DEF", 1, 0);
/* 2588 */     fields[13] = new Field("", "SQL_DATA_TYPE", 4, 10);
/* 2589 */     fields[14] = new Field("", "SQL_DATETIME_SUB", 4, 10);
/* 2590 */     fields[15] = new Field("", "CHAR_OCTET_LENGTH", 4, Integer.toString(Integer.MAX_VALUE).length());
/* 2591 */     fields[16] = new Field("", "ORDINAL_POSITION", 4, 10);
/* 2592 */     fields[17] = new Field("", "IS_NULLABLE", 1, 3);
/* 2593 */     fields[18] = new Field("", "SCOPE_CATALOG", 1, 255);
/* 2594 */     fields[19] = new Field("", "SCOPE_SCHEMA", 1, 255);
/* 2595 */     fields[20] = new Field("", "SCOPE_TABLE", 1, 255);
/* 2596 */     fields[21] = new Field("", "SOURCE_DATA_TYPE", 5, 10);
/* 2597 */     fields[22] = new Field("", "IS_AUTOINCREMENT", 1, 3);
/* 2598 */     fields[23] = new Field("", "IS_GENERATEDCOLUMN", 1, 3);
/* 2599 */     return fields;
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
/* 2610 */     return this.conn;
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
/*      */   public ResultSet getCrossReference(final String primaryCatalog, final String primarySchema, final String primaryTable, final String foreignCatalog, final String foreignSchema, final String foreignTable)
/*      */     throws SQLException
/*      */   {
/* 2669 */     if (primaryTable == null) {
/* 2670 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/* 2673 */     Field[] fields = createFkMetadataFields();
/*      */     
/* 2675 */     final ArrayList<ResultSetRow> tuples = new ArrayList();
/*      */     
/* 2677 */     if (this.conn.versionMeetsMinimum(3, 23, 0))
/*      */     {
/* 2679 */       final java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
/*      */       
/*      */       try
/*      */       {
/* 2683 */         new IterateBlock(getCatalogIterator(foreignCatalog))
/*      */         {
/*      */           void forEach(String catalogStr) throws SQLException
/*      */           {
/* 2687 */             ResultSet fkresults = null;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             try
/*      */             {
/* 2694 */               if (DatabaseMetaData.this.conn.versionMeetsMinimum(3, 23, 50)) {
/* 2695 */                 fkresults = DatabaseMetaData.this.extractForeignKeyFromCreateTable(catalogStr, null);
/*      */               } else {
/* 2697 */                 StringBuilder queryBuf = new StringBuilder("SHOW TABLE STATUS FROM ");
/* 2698 */                 queryBuf.append(StringUtils.quoteIdentifier(catalogStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()));
/*      */                 
/*      */ 
/* 2701 */                 fkresults = stmt.executeQuery(queryBuf.toString());
/*      */               }
/*      */               
/* 2704 */               String foreignTableWithCase = DatabaseMetaData.this.getTableNameWithCase(foreignTable);
/* 2705 */               String primaryTableWithCase = DatabaseMetaData.this.getTableNameWithCase(primaryTable);
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2713 */               while (fkresults.next()) {
/* 2714 */                 String tableType = fkresults.getString("Type");
/*      */                 
/* 2716 */                 if ((tableType != null) && ((tableType.equalsIgnoreCase("innodb")) || (tableType.equalsIgnoreCase("SUPPORTS_FK")))) {
/* 2717 */                   String comment = fkresults.getString("Comment").trim();
/*      */                   
/* 2719 */                   if (comment != null) {
/* 2720 */                     StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
/*      */                     
/* 2722 */                     if (commentTokens.hasMoreTokens()) {
/* 2723 */                       String str1 = commentTokens.nextToken();
/*      */                     }
/*      */                     
/*      */ 
/*      */ 
/* 2728 */                     while (commentTokens.hasMoreTokens()) {
/* 2729 */                       String keys = commentTokens.nextToken();
/* 2730 */                       DatabaseMetaData.LocalAndReferencedColumns parsedInfo = DatabaseMetaData.this.parseTableStatusIntoLocalAndReferencedColumns(keys);
/*      */                       
/* 2732 */                       int keySeq = 0;
/*      */                       
/* 2734 */                       Iterator<String> referencingColumns = parsedInfo.localColumnsList.iterator();
/* 2735 */                       Iterator<String> referencedColumns = parsedInfo.referencedColumnsList.iterator();
/*      */                       
/* 2737 */                       while (referencingColumns.hasNext()) {
/* 2738 */                         String referencingColumn = StringUtils.unQuoteIdentifier((String)referencingColumns.next(), DatabaseMetaData.this.quotedId);
/*      */                         
/*      */ 
/*      */ 
/* 2742 */                         byte[][] tuple = new byte[14][];
/* 2743 */                         tuple[4] = (foreignCatalog == null ? null : DatabaseMetaData.this.s2b(foreignCatalog));
/* 2744 */                         tuple[5] = (foreignSchema == null ? null : DatabaseMetaData.this.s2b(foreignSchema));
/* 2745 */                         String dummy = fkresults.getString("Name");
/*      */                         
/* 2747 */                         if (dummy.compareTo(foreignTableWithCase) == 0)
/*      */                         {
/*      */ 
/*      */ 
/* 2751 */                           tuple[6] = DatabaseMetaData.this.s2b(dummy);
/*      */                           
/* 2753 */                           tuple[7] = DatabaseMetaData.this.s2b(referencingColumn);
/* 2754 */                           tuple[0] = (primaryCatalog == null ? null : DatabaseMetaData.this.s2b(primaryCatalog));
/* 2755 */                           tuple[1] = (primarySchema == null ? null : DatabaseMetaData.this.s2b(primarySchema));
/*      */                           
/*      */ 
/* 2758 */                           if (parsedInfo.referencedTable.compareTo(primaryTableWithCase) == 0)
/*      */                           {
/*      */ 
/*      */ 
/* 2762 */                             tuple[2] = DatabaseMetaData.this.s2b(parsedInfo.referencedTable);
/* 2763 */                             tuple[3] = DatabaseMetaData.this.s2b(StringUtils.unQuoteIdentifier((String)referencedColumns.next(), DatabaseMetaData.this.quotedId));
/* 2764 */                             tuple[8] = Integer.toString(keySeq).getBytes();
/*      */                             
/* 2766 */                             int[] actions = DatabaseMetaData.this.getForeignKeyActions(keys);
/*      */                             
/* 2768 */                             tuple[9] = Integer.toString(actions[1]).getBytes();
/* 2769 */                             tuple[10] = Integer.toString(actions[0]).getBytes();
/* 2770 */                             tuple[11] = null;
/* 2771 */                             tuple[12] = null;
/* 2772 */                             tuple[13] = Integer.toString(7).getBytes();
/* 2773 */                             tuples.add(new ByteArrayRow(tuple, DatabaseMetaData.this.getExceptionInterceptor()));
/* 2774 */                             keySeq++;
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/* 2782 */             } finally { if (fkresults != null) {
/*      */                 try {
/* 2784 */                   fkresults.close();
/*      */                 } catch (Exception sqlEx) {
/* 2786 */                   AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                 }
/*      */                 
/* 2789 */                 fkresults = null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }.doForAll();
/*      */       } finally {
/* 2795 */         if (stmt != null) {
/* 2796 */           stmt.close();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 2801 */     ResultSet results = buildResultSet(fields, tuples);
/*      */     
/* 2803 */     return results;
/*      */   }
/*      */   
/*      */   protected Field[] createFkMetadataFields() {
/* 2807 */     Field[] fields = new Field[14];
/* 2808 */     fields[0] = new Field("", "PKTABLE_CAT", 1, 255);
/* 2809 */     fields[1] = new Field("", "PKTABLE_SCHEM", 1, 0);
/* 2810 */     fields[2] = new Field("", "PKTABLE_NAME", 1, 255);
/* 2811 */     fields[3] = new Field("", "PKCOLUMN_NAME", 1, 32);
/* 2812 */     fields[4] = new Field("", "FKTABLE_CAT", 1, 255);
/* 2813 */     fields[5] = new Field("", "FKTABLE_SCHEM", 1, 0);
/* 2814 */     fields[6] = new Field("", "FKTABLE_NAME", 1, 255);
/* 2815 */     fields[7] = new Field("", "FKCOLUMN_NAME", 1, 32);
/* 2816 */     fields[8] = new Field("", "KEY_SEQ", 5, 2);
/* 2817 */     fields[9] = new Field("", "UPDATE_RULE", 5, 2);
/* 2818 */     fields[10] = new Field("", "DELETE_RULE", 5, 2);
/* 2819 */     fields[11] = new Field("", "FK_NAME", 1, 0);
/* 2820 */     fields[12] = new Field("", "PK_NAME", 1, 0);
/* 2821 */     fields[13] = new Field("", "DEFERRABILITY", 5, 2);
/* 2822 */     return fields;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getDatabaseMajorVersion()
/*      */     throws SQLException
/*      */   {
/* 2829 */     return this.conn.getServerMajorVersion();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getDatabaseMinorVersion()
/*      */     throws SQLException
/*      */   {
/* 2836 */     return this.conn.getServerMinorVersion();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getDatabaseProductName()
/*      */     throws SQLException
/*      */   {
/* 2846 */     return "MySQL";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getDatabaseProductVersion()
/*      */     throws SQLException
/*      */   {
/* 2856 */     return this.conn.getServerVersion();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getDefaultTransactionIsolation()
/*      */     throws SQLException
/*      */   {
/* 2869 */     if (this.conn.supportsIsolationLevel()) {
/* 2870 */       return 2;
/*      */     }
/*      */     
/* 2873 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getDriverMajorVersion()
/*      */   {
/* 2882 */     return NonRegisteringDriver.getMajorVersionInternal();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getDriverMinorVersion()
/*      */   {
/* 2891 */     return NonRegisteringDriver.getMinorVersionInternal();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getDriverName()
/*      */     throws SQLException
/*      */   {
/* 2901 */     return "MySQL Connector Java";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getDriverVersion()
/*      */     throws SQLException
/*      */   {
/* 2911 */     return "mysql-connector-java-5.1.39 ( Revision: 3289a357af6d09ecc1a10fd3c26e95183e5790ad )";
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
/*      */   public ResultSet getExportedKeys(String catalog, String schema, final String table)
/*      */     throws SQLException
/*      */   {
/* 2961 */     if (table == null) {
/* 2962 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/* 2965 */     Field[] fields = createFkMetadataFields();
/*      */     
/* 2967 */     final ArrayList<ResultSetRow> rows = new ArrayList();
/*      */     
/* 2969 */     if (this.conn.versionMeetsMinimum(3, 23, 0))
/*      */     {
/* 2971 */       final java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
/*      */       
/*      */       try
/*      */       {
/* 2975 */         new IterateBlock(getCatalogIterator(catalog))
/*      */         {
/*      */           void forEach(String catalogStr) throws SQLException {
/* 2978 */             ResultSet fkresults = null;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             try
/*      */             {
/* 2985 */               if (DatabaseMetaData.this.conn.versionMeetsMinimum(3, 23, 50))
/*      */               {
/*      */ 
/* 2988 */                 fkresults = DatabaseMetaData.this.extractForeignKeyFromCreateTable(catalogStr, null);
/*      */               } else {
/* 2990 */                 StringBuilder queryBuf = new StringBuilder("SHOW TABLE STATUS FROM ");
/* 2991 */                 queryBuf.append(StringUtils.quoteIdentifier(catalogStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()));
/*      */                 
/*      */ 
/* 2994 */                 fkresults = stmt.executeQuery(queryBuf.toString());
/*      */               }
/*      */               
/*      */ 
/* 2998 */               String tableNameWithCase = DatabaseMetaData.this.getTableNameWithCase(table);
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3004 */               while (fkresults.next()) {
/* 3005 */                 String tableType = fkresults.getString("Type");
/*      */                 
/* 3007 */                 if ((tableType != null) && ((tableType.equalsIgnoreCase("innodb")) || (tableType.equalsIgnoreCase("SUPPORTS_FK")))) {
/* 3008 */                   String comment = fkresults.getString("Comment").trim();
/*      */                   
/* 3010 */                   if (comment != null) {
/* 3011 */                     StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
/*      */                     
/* 3013 */                     if (commentTokens.hasMoreTokens()) {
/* 3014 */                       commentTokens.nextToken();
/*      */                       
/*      */ 
/*      */ 
/* 3018 */                       while (commentTokens.hasMoreTokens()) {
/* 3019 */                         String keys = commentTokens.nextToken();
/* 3020 */                         DatabaseMetaData.this.getExportKeyResults(catalogStr, tableNameWithCase, keys, rows, fkresults.getString("Name"));
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             finally {
/* 3028 */               if (fkresults != null) {
/*      */                 try {
/* 3030 */                   fkresults.close();
/*      */                 } catch (SQLException sqlEx) {
/* 3032 */                   AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                 }
/*      */                 
/* 3035 */                 fkresults = null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }.doForAll();
/*      */       } finally {
/* 3041 */         if (stmt != null) {
/* 3042 */           stmt.close();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 3047 */     ResultSet results = buildResultSet(fields, rows);
/*      */     
/* 3049 */     return results;
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
/*      */   protected void getExportKeyResults(String catalog, String exportingTable, String keysComment, List<ResultSetRow> tuples, String fkTableName)
/*      */     throws SQLException
/*      */   {
/* 3072 */     getResultsImpl(catalog, exportingTable, keysComment, tuples, fkTableName, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getExtraNameCharacters()
/*      */     throws SQLException
/*      */   {
/* 3083 */     return "#@";
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
/*      */   protected int[] getForeignKeyActions(String commentString)
/*      */   {
/* 3096 */     int[] actions = { 3, 3 };
/*      */     
/* 3098 */     int lastParenIndex = commentString.lastIndexOf(")");
/*      */     
/* 3100 */     if (lastParenIndex != commentString.length() - 1) {
/* 3101 */       String cascadeOptions = commentString.substring(lastParenIndex + 1).trim().toUpperCase(Locale.ENGLISH);
/*      */       
/* 3103 */       actions[0] = getCascadeDeleteOption(cascadeOptions);
/* 3104 */       actions[1] = getCascadeUpdateOption(cascadeOptions);
/*      */     }
/*      */     
/* 3107 */     return actions;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getIdentifierQuoteString()
/*      */     throws SQLException
/*      */   {
/* 3119 */     if (this.conn.supportsQuotedIdentifiers()) {
/* 3120 */       return this.conn.useAnsiQuotedIdentifiers() ? "\"" : "`";
/*      */     }
/*      */     
/*      */ 
/* 3124 */     return " ";
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
/*      */   public ResultSet getImportedKeys(String catalog, String schema, final String table)
/*      */     throws SQLException
/*      */   {
/* 3174 */     if (table == null) {
/* 3175 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/* 3178 */     Field[] fields = createFkMetadataFields();
/*      */     
/* 3180 */     final ArrayList<ResultSetRow> rows = new ArrayList();
/*      */     
/* 3182 */     if (this.conn.versionMeetsMinimum(3, 23, 0))
/*      */     {
/* 3184 */       final java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
/*      */       
/*      */       try
/*      */       {
/* 3188 */         new IterateBlock(getCatalogIterator(catalog))
/*      */         {
/*      */           void forEach(String catalogStr) throws SQLException {
/* 3191 */             ResultSet fkresults = null;
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */             try
/*      */             {
/* 3198 */               if (DatabaseMetaData.this.conn.versionMeetsMinimum(3, 23, 50))
/*      */               {
/*      */ 
/* 3201 */                 fkresults = DatabaseMetaData.this.extractForeignKeyFromCreateTable(catalogStr, table);
/*      */               } else {
/* 3203 */                 StringBuilder queryBuf = new StringBuilder("SHOW TABLE STATUS ");
/* 3204 */                 queryBuf.append(" FROM ");
/* 3205 */                 queryBuf.append(StringUtils.quoteIdentifier(catalogStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()));
/*      */                 
/* 3207 */                 queryBuf.append(" LIKE ");
/* 3208 */                 queryBuf.append(StringUtils.quoteIdentifier(table, "'", true));
/*      */                 
/* 3210 */                 fkresults = stmt.executeQuery(queryBuf.toString());
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3217 */               while (fkresults.next()) {
/* 3218 */                 String tableType = fkresults.getString("Type");
/*      */                 
/* 3220 */                 if ((tableType != null) && ((tableType.equalsIgnoreCase("innodb")) || (tableType.equalsIgnoreCase("SUPPORTS_FK")))) {
/* 3221 */                   String comment = fkresults.getString("Comment").trim();
/*      */                   
/* 3223 */                   if (comment != null) {
/* 3224 */                     StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
/*      */                     
/* 3226 */                     if (commentTokens.hasMoreTokens()) {
/* 3227 */                       commentTokens.nextToken();
/*      */                       
/* 3229 */                       while (commentTokens.hasMoreTokens()) {
/* 3230 */                         String keys = commentTokens.nextToken();
/* 3231 */                         DatabaseMetaData.this.getImportKeyResults(catalogStr, table, keys, rows);
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             } finally {
/* 3238 */               if (fkresults != null) {
/*      */                 try {
/* 3240 */                   fkresults.close();
/*      */                 } catch (SQLException sqlEx) {
/* 3242 */                   AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                 }
/*      */                 
/* 3245 */                 fkresults = null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }.doForAll();
/*      */       } finally {
/* 3251 */         if (stmt != null) {
/* 3252 */           stmt.close();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 3257 */     ResultSet results = buildResultSet(fields, rows);
/*      */     
/* 3259 */     return results;
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
/*      */   protected void getImportKeyResults(String catalog, String importingTable, String keysComment, List<ResultSetRow> tuples)
/*      */     throws SQLException
/*      */   {
/* 3280 */     getResultsImpl(catalog, importingTable, keysComment, tuples, null, false);
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
/*      */   public ResultSet getIndexInfo(String catalog, String schema, final String table, final boolean unique, boolean approximate)
/*      */     throws SQLException
/*      */   {
/* 3335 */     Field[] fields = createIndexInfoFields();
/*      */     
/* 3337 */     final SortedMap<IndexMetaDataKey, ResultSetRow> sortedRows = new TreeMap();
/* 3338 */     ArrayList<ResultSetRow> rows = new ArrayList();
/* 3339 */     final java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
/*      */     
/*      */     try
/*      */     {
/* 3343 */       new IterateBlock(getCatalogIterator(catalog))
/*      */       {
/*      */         void forEach(String catalogStr) throws SQLException
/*      */         {
/* 3347 */           ResultSet results = null;
/*      */           try
/*      */           {
/* 3350 */             StringBuilder queryBuf = new StringBuilder("SHOW INDEX FROM ");
/* 3351 */             queryBuf.append(StringUtils.quoteIdentifier(table, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()));
/* 3352 */             queryBuf.append(" FROM ");
/* 3353 */             queryBuf.append(StringUtils.quoteIdentifier(catalogStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()));
/*      */             try
/*      */             {
/* 3356 */               results = stmt.executeQuery(queryBuf.toString());
/*      */             } catch (SQLException sqlEx) {
/* 3358 */               int errorCode = sqlEx.getErrorCode();
/*      */               
/*      */ 
/* 3361 */               if (!"42S02".equals(sqlEx.getSQLState()))
/*      */               {
/* 3363 */                 if (errorCode != 1146) {
/* 3364 */                   throw sqlEx;
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 3369 */             while ((results != null) && (results.next())) {
/* 3370 */               byte[][] row = new byte[14][];
/* 3371 */               row[0] = (catalogStr == null ? new byte[0] : DatabaseMetaData.this.s2b(catalogStr));
/* 3372 */               row[1] = null;
/* 3373 */               row[2] = results.getBytes("Table");
/*      */               
/* 3375 */               boolean indexIsUnique = results.getInt("Non_unique") == 0;
/*      */               
/* 3377 */               row[3] = (!indexIsUnique ? DatabaseMetaData.this.s2b("true") : DatabaseMetaData.this.s2b("false"));
/* 3378 */               row[4] = new byte[0];
/* 3379 */               row[5] = results.getBytes("Key_name");
/* 3380 */               short indexType = 3;
/* 3381 */               row[6] = Integer.toString(indexType).getBytes();
/* 3382 */               row[7] = results.getBytes("Seq_in_index");
/* 3383 */               row[8] = results.getBytes("Column_name");
/* 3384 */               row[9] = results.getBytes("Collation");
/*      */               
/* 3386 */               long cardinality = results.getLong("Cardinality");
/*      */               
/*      */ 
/* 3389 */               if ((!Util.isJdbc42()) && (cardinality > 2147483647L)) {
/* 3390 */                 cardinality = 2147483647L;
/*      */               }
/*      */               
/* 3393 */               row[10] = DatabaseMetaData.this.s2b(String.valueOf(cardinality));
/* 3394 */               row[11] = DatabaseMetaData.this.s2b("0");
/* 3395 */               row[12] = null;
/*      */               
/* 3397 */               DatabaseMetaData.IndexMetaDataKey indexInfoKey = new DatabaseMetaData.IndexMetaDataKey(DatabaseMetaData.this, !indexIsUnique, indexType, results.getString("Key_name").toLowerCase(), results.getShort("Seq_in_index"));
/*      */               
/*      */ 
/* 3400 */               if (unique) {
/* 3401 */                 if (indexIsUnique) {
/* 3402 */                   sortedRows.put(indexInfoKey, new ByteArrayRow(row, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */                 }
/*      */               }
/*      */               else {
/* 3406 */                 sortedRows.put(indexInfoKey, new ByteArrayRow(row, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */               }
/*      */             }
/*      */           } finally {
/* 3410 */             if (results != null) {
/*      */               try {
/* 3412 */                 results.close();
/*      */               }
/*      */               catch (Exception ex) {}
/*      */               
/* 3416 */               results = null;
/*      */             }
/*      */             
/*      */           }
/*      */         }
/* 3421 */       }.doForAll();
/* 3422 */       Iterator<ResultSetRow> sortedRowsIterator = sortedRows.values().iterator();
/* 3423 */       while (sortedRowsIterator.hasNext()) {
/* 3424 */         rows.add(sortedRowsIterator.next());
/*      */       }
/*      */       
/* 3427 */       ResultSet indexInfo = buildResultSet(fields, rows);
/*      */       
/* 3429 */       return indexInfo;
/*      */     } finally {
/* 3431 */       if (stmt != null) {
/* 3432 */         stmt.close();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected Field[] createIndexInfoFields() {
/* 3438 */     Field[] fields = new Field[13];
/* 3439 */     fields[0] = new Field("", "TABLE_CAT", 1, 255);
/* 3440 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 0);
/* 3441 */     fields[2] = new Field("", "TABLE_NAME", 1, 255);
/* 3442 */     fields[3] = new Field("", "NON_UNIQUE", 16, 4);
/* 3443 */     fields[4] = new Field("", "INDEX_QUALIFIER", 1, 1);
/* 3444 */     fields[5] = new Field("", "INDEX_NAME", 1, 32);
/* 3445 */     fields[6] = new Field("", "TYPE", 5, 32);
/* 3446 */     fields[7] = new Field("", "ORDINAL_POSITION", 5, 5);
/* 3447 */     fields[8] = new Field("", "COLUMN_NAME", 1, 32);
/* 3448 */     fields[9] = new Field("", "ASC_OR_DESC", 1, 1);
/* 3449 */     if (Util.isJdbc42()) {
/* 3450 */       fields[10] = new Field("", "CARDINALITY", -5, 20);
/* 3451 */       fields[11] = new Field("", "PAGES", -5, 20);
/*      */     } else {
/* 3453 */       fields[10] = new Field("", "CARDINALITY", 4, 20);
/* 3454 */       fields[11] = new Field("", "PAGES", 4, 10);
/*      */     }
/* 3456 */     fields[12] = new Field("", "FILTER_CONDITION", 1, 32);
/* 3457 */     return fields;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getJDBCMajorVersion()
/*      */     throws SQLException
/*      */   {
/* 3464 */     return 4;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getJDBCMinorVersion()
/*      */     throws SQLException
/*      */   {
/* 3471 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxBinaryLiteralLength()
/*      */     throws SQLException
/*      */   {
/* 3481 */     return 16777208;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxCatalogNameLength()
/*      */     throws SQLException
/*      */   {
/* 3491 */     return 32;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxCharLiteralLength()
/*      */     throws SQLException
/*      */   {
/* 3501 */     return 16777208;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxColumnNameLength()
/*      */     throws SQLException
/*      */   {
/* 3511 */     return 64;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxColumnsInGroupBy()
/*      */     throws SQLException
/*      */   {
/* 3521 */     return 64;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxColumnsInIndex()
/*      */     throws SQLException
/*      */   {
/* 3531 */     return 16;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxColumnsInOrderBy()
/*      */     throws SQLException
/*      */   {
/* 3541 */     return 64;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxColumnsInSelect()
/*      */     throws SQLException
/*      */   {
/* 3551 */     return 256;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxColumnsInTable()
/*      */     throws SQLException
/*      */   {
/* 3561 */     return 512;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxConnections()
/*      */     throws SQLException
/*      */   {
/* 3571 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxCursorNameLength()
/*      */     throws SQLException
/*      */   {
/* 3581 */     return 64;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxIndexLength()
/*      */     throws SQLException
/*      */   {
/* 3591 */     return 256;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxProcedureNameLength()
/*      */     throws SQLException
/*      */   {
/* 3601 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxRowSize()
/*      */     throws SQLException
/*      */   {
/* 3611 */     return 2147483639;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxSchemaNameLength()
/*      */     throws SQLException
/*      */   {
/* 3621 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxStatementLength()
/*      */     throws SQLException
/*      */   {
/* 3631 */     return MysqlIO.getMaxBuf() - 4;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxStatements()
/*      */     throws SQLException
/*      */   {
/* 3641 */     return 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxTableNameLength()
/*      */     throws SQLException
/*      */   {
/* 3651 */     return 64;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxTablesInSelect()
/*      */     throws SQLException
/*      */   {
/* 3661 */     return 256;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMaxUserNameLength()
/*      */     throws SQLException
/*      */   {
/* 3671 */     return 16;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getNumericFunctions()
/*      */     throws SQLException
/*      */   {
/* 3681 */     return "ABS,ACOS,ASIN,ATAN,ATAN2,BIT_COUNT,CEILING,COS,COT,DEGREES,EXP,FLOOR,LOG,LOG10,MAX,MIN,MOD,PI,POW,POWER,RADIANS,RAND,ROUND,SIN,SQRT,TAN,TRUNCATE";
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
/*      */   public ResultSet getPrimaryKeys(String catalog, String schema, final String table)
/*      */     throws SQLException
/*      */   {
/* 3710 */     Field[] fields = new Field[6];
/* 3711 */     fields[0] = new Field("", "TABLE_CAT", 1, 255);
/* 3712 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 0);
/* 3713 */     fields[2] = new Field("", "TABLE_NAME", 1, 255);
/* 3714 */     fields[3] = new Field("", "COLUMN_NAME", 1, 32);
/* 3715 */     fields[4] = new Field("", "KEY_SEQ", 5, 5);
/* 3716 */     fields[5] = new Field("", "PK_NAME", 1, 32);
/*      */     
/* 3718 */     if (table == null) {
/* 3719 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/* 3722 */     final ArrayList<ResultSetRow> rows = new ArrayList();
/* 3723 */     final java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
/*      */     
/*      */     try
/*      */     {
/* 3727 */       new IterateBlock(getCatalogIterator(catalog))
/*      */       {
/*      */         void forEach(String catalogStr) throws SQLException {
/* 3730 */           ResultSet rs = null;
/*      */           
/*      */           try
/*      */           {
/* 3734 */             StringBuilder queryBuf = new StringBuilder("SHOW KEYS FROM ");
/* 3735 */             queryBuf.append(StringUtils.quoteIdentifier(table, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()));
/* 3736 */             queryBuf.append(" FROM ");
/* 3737 */             queryBuf.append(StringUtils.quoteIdentifier(catalogStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()));
/*      */             
/* 3739 */             rs = stmt.executeQuery(queryBuf.toString());
/*      */             
/* 3741 */             TreeMap<String, byte[][]> sortMap = new TreeMap();
/*      */             
/* 3743 */             while (rs.next()) {
/* 3744 */               String keyType = rs.getString("Key_name");
/*      */               
/* 3746 */               if ((keyType != null) && (
/* 3747 */                 (keyType.equalsIgnoreCase("PRIMARY")) || (keyType.equalsIgnoreCase("PRI")))) {
/* 3748 */                 byte[][] tuple = new byte[6][];
/* 3749 */                 tuple[0] = (catalogStr == null ? new byte[0] : DatabaseMetaData.this.s2b(catalogStr));
/* 3750 */                 tuple[1] = null;
/* 3751 */                 tuple[2] = DatabaseMetaData.this.s2b(table);
/*      */                 
/* 3753 */                 String columnName = rs.getString("Column_name");
/* 3754 */                 tuple[3] = DatabaseMetaData.this.s2b(columnName);
/* 3755 */                 tuple[4] = DatabaseMetaData.this.s2b(rs.getString("Seq_in_index"));
/* 3756 */                 tuple[5] = DatabaseMetaData.this.s2b(keyType);
/* 3757 */                 sortMap.put(columnName, tuple);
/*      */               }
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 3763 */             Iterator<byte[][]> sortedIterator = sortMap.values().iterator();
/*      */             
/* 3765 */             while (sortedIterator.hasNext()) {
/* 3766 */               rows.add(new ByteArrayRow((byte[][])sortedIterator.next(), DatabaseMetaData.this.getExceptionInterceptor()));
/*      */             }
/*      */           }
/*      */           finally {
/* 3770 */             if (rs != null) {
/*      */               try {
/* 3772 */                 rs.close();
/*      */               }
/*      */               catch (Exception ex) {}
/*      */               
/* 3776 */               rs = null;
/*      */             }
/*      */           }
/*      */         }
/*      */       }.doForAll();
/*      */     } finally {
/* 3782 */       if (stmt != null) {
/* 3783 */         stmt.close();
/*      */       }
/*      */     }
/*      */     
/* 3787 */     ResultSet results = buildResultSet(fields, rows);
/*      */     
/* 3789 */     return results;
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
/*      */   public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 3866 */     Field[] fields = createProcedureColumnsFields();
/*      */     
/* 3868 */     return getProcedureOrFunctionColumns(fields, catalog, schemaPattern, procedureNamePattern, columnNamePattern, true, true);
/*      */   }
/*      */   
/*      */   protected Field[] createProcedureColumnsFields() {
/* 3872 */     Field[] fields = new Field[20];
/*      */     
/* 3874 */     fields[0] = new Field("", "PROCEDURE_CAT", 1, 512);
/* 3875 */     fields[1] = new Field("", "PROCEDURE_SCHEM", 1, 512);
/* 3876 */     fields[2] = new Field("", "PROCEDURE_NAME", 1, 512);
/* 3877 */     fields[3] = new Field("", "COLUMN_NAME", 1, 512);
/* 3878 */     fields[4] = new Field("", "COLUMN_TYPE", 1, 64);
/* 3879 */     fields[5] = new Field("", "DATA_TYPE", 5, 6);
/* 3880 */     fields[6] = new Field("", "TYPE_NAME", 1, 64);
/* 3881 */     fields[7] = new Field("", "PRECISION", 4, 12);
/* 3882 */     fields[8] = new Field("", "LENGTH", 4, 12);
/* 3883 */     fields[9] = new Field("", "SCALE", 5, 12);
/* 3884 */     fields[10] = new Field("", "RADIX", 5, 6);
/* 3885 */     fields[11] = new Field("", "NULLABLE", 5, 6);
/* 3886 */     fields[12] = new Field("", "REMARKS", 1, 512);
/* 3887 */     fields[13] = new Field("", "COLUMN_DEF", 1, 512);
/* 3888 */     fields[14] = new Field("", "SQL_DATA_TYPE", 4, 12);
/* 3889 */     fields[15] = new Field("", "SQL_DATETIME_SUB", 4, 12);
/* 3890 */     fields[16] = new Field("", "CHAR_OCTET_LENGTH", 4, 12);
/* 3891 */     fields[17] = new Field("", "ORDINAL_POSITION", 4, 12);
/* 3892 */     fields[18] = new Field("", "IS_NULLABLE", 1, 512);
/* 3893 */     fields[19] = new Field("", "SPECIFIC_NAME", 1, 512);
/* 3894 */     return fields;
/*      */   }
/*      */   
/*      */   protected ResultSet getProcedureOrFunctionColumns(Field[] fields, String catalog, String schemaPattern, String procedureOrFunctionNamePattern, String columnNamePattern, boolean returnProcedures, boolean returnFunctions)
/*      */     throws SQLException
/*      */   {
/* 3900 */     List<ComparableWrapper<String, ProcedureType>> procsOrFuncsToExtractList = new ArrayList();
/*      */     
/* 3902 */     ResultSet procsAndOrFuncsRs = null;
/*      */     
/* 3904 */     if (supportsStoredProcedures()) {
/*      */       try
/*      */       {
/* 3907 */         String tmpProcedureOrFunctionNamePattern = null;
/*      */         
/* 3909 */         if ((procedureOrFunctionNamePattern != null) && (!procedureOrFunctionNamePattern.equals("%"))) {
/* 3910 */           tmpProcedureOrFunctionNamePattern = StringUtils.sanitizeProcOrFuncName(procedureOrFunctionNamePattern);
/*      */         }
/*      */         
/*      */ 
/* 3914 */         if (tmpProcedureOrFunctionNamePattern == null) {
/* 3915 */           tmpProcedureOrFunctionNamePattern = procedureOrFunctionNamePattern;
/*      */         }
/*      */         else
/*      */         {
/* 3919 */           String tmpCatalog = catalog;
/* 3920 */           List<String> parseList = StringUtils.splitDBdotName(tmpProcedureOrFunctionNamePattern, tmpCatalog, this.quotedId, this.conn.isNoBackslashEscapesSet());
/*      */           
/*      */ 
/*      */ 
/* 3924 */           if (parseList.size() == 2) {
/* 3925 */             tmpCatalog = (String)parseList.get(0);
/* 3926 */             tmpProcedureOrFunctionNamePattern = (String)parseList.get(1);
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 3932 */         procsAndOrFuncsRs = getProceduresAndOrFunctions(createFieldMetadataForGetProcedures(), catalog, schemaPattern, tmpProcedureOrFunctionNamePattern, returnProcedures, returnFunctions);
/*      */         
/*      */ 
/* 3935 */         boolean hasResults = false;
/* 3936 */         while (procsAndOrFuncsRs.next()) {
/* 3937 */           procsOrFuncsToExtractList.add(new ComparableWrapper(getFullyQualifiedName(procsAndOrFuncsRs.getString(1), procsAndOrFuncsRs.getString(3)), procsAndOrFuncsRs.getShort(8) == 1 ? ProcedureType.PROCEDURE : ProcedureType.FUNCTION));
/*      */           
/*      */ 
/* 3940 */           hasResults = true;
/*      */         }
/*      */         
/*      */ 
/* 3944 */         if (hasResults)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 3951 */           Collections.sort(procsOrFuncsToExtractList);
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       finally
/*      */       {
/* 3958 */         SQLException rethrowSqlEx = null;
/*      */         
/* 3960 */         if (procsAndOrFuncsRs != null) {
/*      */           try {
/* 3962 */             procsAndOrFuncsRs.close();
/*      */           } catch (SQLException sqlEx) {
/* 3964 */             rethrowSqlEx = sqlEx;
/*      */           }
/*      */         }
/*      */         
/* 3968 */         if (rethrowSqlEx != null) {
/* 3969 */           throw rethrowSqlEx;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 3974 */     ArrayList<ResultSetRow> resultRows = new ArrayList();
/* 3975 */     int idx = 0;
/* 3976 */     String procNameToCall = "";
/*      */     
/* 3978 */     for (Object procOrFunc : procsOrFuncsToExtractList) {
/* 3979 */       String procName = (String)((ComparableWrapper)procOrFunc).getKey();
/* 3980 */       ProcedureType procType = (ProcedureType)((ComparableWrapper)procOrFunc).getValue();
/*      */       
/*      */ 
/* 3983 */       if (!" ".equals(this.quotedId)) {
/* 3984 */         idx = StringUtils.indexOfIgnoreCase(0, procName, ".", this.quotedId, this.quotedId, this.conn.isNoBackslashEscapesSet() ? StringUtils.SEARCH_MODE__MRK_COM_WS : StringUtils.SEARCH_MODE__ALL);
/*      */       }
/*      */       else {
/* 3987 */         idx = procName.indexOf(".");
/*      */       }
/*      */       
/* 3990 */       if (idx > 0) {
/* 3991 */         catalog = StringUtils.unQuoteIdentifier(procName.substring(0, idx), this.quotedId);
/* 3992 */         procNameToCall = procName;
/*      */       }
/*      */       else {
/* 3995 */         procNameToCall = procName;
/*      */       }
/* 3997 */       getCallStmtParameterTypes(catalog, procNameToCall, procType, columnNamePattern, resultRows, fields.length == 17);
/*      */     }
/*      */     
/* 4000 */     return buildResultSet(fields, resultRows);
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
/*      */   public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern)
/*      */     throws SQLException
/*      */   {
/* 4040 */     Field[] fields = createFieldMetadataForGetProcedures();
/*      */     
/* 4042 */     return getProceduresAndOrFunctions(fields, catalog, schemaPattern, procedureNamePattern, true, true);
/*      */   }
/*      */   
/*      */   protected Field[] createFieldMetadataForGetProcedures() {
/* 4046 */     Field[] fields = new Field[9];
/* 4047 */     fields[0] = new Field("", "PROCEDURE_CAT", 1, 255);
/* 4048 */     fields[1] = new Field("", "PROCEDURE_SCHEM", 1, 255);
/* 4049 */     fields[2] = new Field("", "PROCEDURE_NAME", 1, 255);
/* 4050 */     fields[3] = new Field("", "reserved1", 1, 0);
/* 4051 */     fields[4] = new Field("", "reserved2", 1, 0);
/* 4052 */     fields[5] = new Field("", "reserved3", 1, 0);
/* 4053 */     fields[6] = new Field("", "REMARKS", 1, 255);
/* 4054 */     fields[7] = new Field("", "PROCEDURE_TYPE", 5, 6);
/* 4055 */     fields[8] = new Field("", "SPECIFIC_NAME", 1, 255);
/*      */     
/* 4057 */     return fields;
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
/*      */   protected ResultSet getProceduresAndOrFunctions(final Field[] fields, String catalog, String schemaPattern, String procedureNamePattern, final boolean returnProcedures, final boolean returnFunctions)
/*      */     throws SQLException
/*      */   {
/* 4071 */     if ((procedureNamePattern == null) || (procedureNamePattern.length() == 0)) {
/* 4072 */       if (this.conn.getNullNamePatternMatchesAll()) {
/* 4073 */         procedureNamePattern = "%";
/*      */       } else {
/* 4075 */         throw SQLError.createSQLException("Procedure name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 4080 */     ArrayList<ResultSetRow> procedureRows = new ArrayList();
/*      */     
/* 4082 */     if (supportsStoredProcedures()) {
/* 4083 */       final String procNamePattern = procedureNamePattern;
/*      */       
/* 4085 */       final List<ComparableWrapper<String, ResultSetRow>> procedureRowsToSort = new ArrayList();
/*      */       
/* 4087 */       new IterateBlock(getCatalogIterator(catalog))
/*      */       {
/*      */         void forEach(String catalogStr) throws SQLException {
/* 4090 */           String db = catalogStr;
/*      */           
/* 4092 */           boolean fromSelect = false;
/* 4093 */           ResultSet proceduresRs = null;
/* 4094 */           boolean needsClientFiltering = true;
/*      */           
/* 4096 */           StringBuilder selectFromMySQLProcSQL = new StringBuilder();
/*      */           
/* 4098 */           selectFromMySQLProcSQL.append("SELECT name, type, comment FROM mysql.proc WHERE ");
/* 4099 */           if ((returnProcedures) && (!returnFunctions)) {
/* 4100 */             selectFromMySQLProcSQL.append("type = 'PROCEDURE' and ");
/* 4101 */           } else if ((!returnProcedures) && (returnFunctions)) {
/* 4102 */             selectFromMySQLProcSQL.append("type = 'FUNCTION' and ");
/*      */           }
/* 4104 */           selectFromMySQLProcSQL.append("name like ? and db <=> ? ORDER BY name, type");
/*      */           
/* 4106 */           PreparedStatement proceduresStmt = DatabaseMetaData.this.prepareMetaDataSafeStatement(selectFromMySQLProcSQL.toString());
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */           try
/*      */           {
/* 4113 */             boolean hasTypeColumn = false;
/*      */             
/* 4115 */             if (db != null) {
/* 4116 */               if (DatabaseMetaData.this.conn.lowerCaseTableNames()) {
/* 4117 */                 db = db.toLowerCase();
/*      */               }
/* 4119 */               proceduresStmt.setString(2, db);
/*      */             } else {
/* 4121 */               proceduresStmt.setNull(2, 12);
/*      */             }
/*      */             
/* 4124 */             int nameIndex = 1;
/*      */             
/* 4126 */             proceduresStmt.setString(1, procNamePattern);
/*      */             try
/*      */             {
/* 4129 */               proceduresRs = proceduresStmt.executeQuery();
/* 4130 */               fromSelect = true;
/* 4131 */               needsClientFiltering = false;
/* 4132 */               hasTypeColumn = true;
/*      */ 
/*      */             }
/*      */             catch (SQLException sqlEx)
/*      */             {
/*      */ 
/* 4138 */               proceduresStmt.close();
/*      */               
/* 4140 */               fromSelect = false;
/*      */               
/* 4142 */               if (DatabaseMetaData.this.conn.versionMeetsMinimum(5, 0, 1)) {
/* 4143 */                 nameIndex = 2;
/*      */               } else {
/* 4145 */                 nameIndex = 1;
/*      */               }
/*      */               
/* 4148 */               proceduresStmt = DatabaseMetaData.this.prepareMetaDataSafeStatement("SHOW PROCEDURE STATUS LIKE ?");
/*      */               
/* 4150 */               proceduresStmt.setString(1, procNamePattern);
/*      */               
/* 4152 */               proceduresRs = proceduresStmt.executeQuery();
/*      */             }
/*      */             
/* 4155 */             if (returnProcedures) {
/* 4156 */               DatabaseMetaData.this.convertToJdbcProcedureList(fromSelect, db, proceduresRs, needsClientFiltering, db, procedureRowsToSort, nameIndex);
/*      */             }
/*      */             
/* 4159 */             if (!hasTypeColumn)
/*      */             {
/* 4161 */               proceduresStmt.close();
/*      */               
/* 4163 */               proceduresStmt = DatabaseMetaData.this.prepareMetaDataSafeStatement("SHOW FUNCTION STATUS LIKE ?");
/*      */               
/* 4165 */               proceduresStmt.setString(1, procNamePattern);
/*      */               
/* 4167 */               proceduresRs = proceduresStmt.executeQuery();
/*      */             }
/*      */             
/*      */ 
/* 4171 */             if (returnFunctions) {
/* 4172 */               DatabaseMetaData.this.convertToJdbcFunctionList(db, proceduresRs, needsClientFiltering, db, procedureRowsToSort, nameIndex, fields);
/*      */             }
/*      */           }
/*      */           finally {
/* 4176 */             SQLException rethrowSqlEx = null;
/*      */             
/* 4178 */             if (proceduresRs != null) {
/*      */               try {
/* 4180 */                 proceduresRs.close();
/*      */               } catch (SQLException sqlEx) {
/* 4182 */                 rethrowSqlEx = sqlEx;
/*      */               }
/*      */             }
/*      */             
/* 4186 */             if (proceduresStmt != null) {
/*      */               try {
/* 4188 */                 proceduresStmt.close();
/*      */               } catch (SQLException sqlEx) {
/* 4190 */                 rethrowSqlEx = sqlEx;
/*      */               }
/*      */             }
/*      */             
/* 4194 */             if (rethrowSqlEx != null) {
/* 4195 */               throw rethrowSqlEx;
/*      */             }
/*      */             
/*      */           }
/*      */         }
/* 4200 */       }.doForAll();
/* 4201 */       Collections.sort(procedureRowsToSort);
/* 4202 */       for (ComparableWrapper<String, ResultSetRow> procRow : procedureRowsToSort) {
/* 4203 */         procedureRows.add(procRow.getValue());
/*      */       }
/*      */     }
/*      */     
/* 4207 */     return buildResultSet(fields, procedureRows);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getProcedureTerm()
/*      */     throws SQLException
/*      */   {
/* 4218 */     return "PROCEDURE";
/*      */   }
/*      */   
/*      */ 
/*      */   public int getResultSetHoldability()
/*      */     throws SQLException
/*      */   {
/* 4225 */     return 1;
/*      */   }
/*      */   
/*      */   private void getResultsImpl(String catalog, String table, String keysComment, List<ResultSetRow> tuples, String fkTableName, boolean isExport)
/*      */     throws SQLException
/*      */   {
/* 4231 */     LocalAndReferencedColumns parsedInfo = parseTableStatusIntoLocalAndReferencedColumns(keysComment);
/*      */     
/* 4233 */     if ((isExport) && (!parsedInfo.referencedTable.equals(table))) {
/* 4234 */       return;
/*      */     }
/*      */     
/* 4237 */     if (parsedInfo.localColumnsList.size() != parsedInfo.referencedColumnsList.size()) {
/* 4238 */       throw SQLError.createSQLException("Error parsing foreign keys definition, number of local and referenced columns is not the same.", "S1000", getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/* 4242 */     Iterator<String> localColumnNames = parsedInfo.localColumnsList.iterator();
/* 4243 */     Iterator<String> referColumnNames = parsedInfo.referencedColumnsList.iterator();
/*      */     
/* 4245 */     int keySeqIndex = 1;
/*      */     
/* 4247 */     while (localColumnNames.hasNext()) {
/* 4248 */       byte[][] tuple = new byte[14][];
/* 4249 */       String lColumnName = StringUtils.unQuoteIdentifier((String)localColumnNames.next(), this.quotedId);
/* 4250 */       String rColumnName = StringUtils.unQuoteIdentifier((String)referColumnNames.next(), this.quotedId);
/* 4251 */       tuple[4] = (catalog == null ? new byte[0] : s2b(catalog));
/* 4252 */       tuple[5] = null;
/* 4253 */       tuple[6] = s2b(isExport ? fkTableName : table);
/* 4254 */       tuple[7] = s2b(lColumnName);
/* 4255 */       tuple[0] = s2b(parsedInfo.referencedCatalog);
/* 4256 */       tuple[1] = null;
/* 4257 */       tuple[2] = s2b(isExport ? table : parsedInfo.referencedTable);
/* 4258 */       tuple[3] = s2b(rColumnName);
/* 4259 */       tuple[8] = s2b(Integer.toString(keySeqIndex++));
/*      */       
/* 4261 */       int[] actions = getForeignKeyActions(keysComment);
/*      */       
/* 4263 */       tuple[9] = s2b(Integer.toString(actions[1]));
/* 4264 */       tuple[10] = s2b(Integer.toString(actions[0]));
/* 4265 */       tuple[11] = s2b(parsedInfo.constraintName);
/* 4266 */       tuple[12] = null;
/* 4267 */       tuple[13] = s2b(Integer.toString(7));
/* 4268 */       tuples.add(new ByteArrayRow(tuple, getExceptionInterceptor()));
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
/*      */   public ResultSet getSchemas()
/*      */     throws SQLException
/*      */   {
/* 4287 */     Field[] fields = new Field[2];
/* 4288 */     fields[0] = new Field("", "TABLE_SCHEM", 1, 0);
/* 4289 */     fields[1] = new Field("", "TABLE_CATALOG", 1, 0);
/*      */     
/* 4291 */     ArrayList<ResultSetRow> tuples = new ArrayList();
/* 4292 */     ResultSet results = buildResultSet(fields, tuples);
/*      */     
/* 4294 */     return results;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getSchemaTerm()
/*      */     throws SQLException
/*      */   {
/* 4304 */     return "";
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
/*      */   public String getSearchStringEscape()
/*      */     throws SQLException
/*      */   {
/* 4321 */     return "\\";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getSQLKeywords()
/*      */     throws SQLException
/*      */   {
/* 4331 */     if (mysqlKeywords != null) {
/* 4332 */       return mysqlKeywords;
/*      */     }
/*      */     
/* 4335 */     synchronized (DatabaseMetaData.class)
/*      */     {
/* 4337 */       if (mysqlKeywords != null) {
/* 4338 */         return mysqlKeywords;
/*      */       }
/*      */       
/* 4341 */       Set<String> mysqlKeywordSet = new TreeSet();
/* 4342 */       StringBuilder mysqlKeywordsBuffer = new StringBuilder();
/*      */       
/* 4344 */       Collections.addAll(mysqlKeywordSet, MYSQL_KEYWORDS);
/* 4345 */       mysqlKeywordSet.removeAll(Arrays.asList(Util.isJdbc4() ? SQL2003_KEYWORDS : SQL92_KEYWORDS));
/*      */       
/* 4347 */       for (String keyword : mysqlKeywordSet) {
/* 4348 */         mysqlKeywordsBuffer.append(",").append(keyword);
/*      */       }
/*      */       
/* 4351 */       mysqlKeywords = mysqlKeywordsBuffer.substring(1);
/* 4352 */       return mysqlKeywords;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getSQLStateType()
/*      */     throws SQLException
/*      */   {
/* 4360 */     if (this.conn.versionMeetsMinimum(4, 1, 0)) {
/* 4361 */       return 2;
/*      */     }
/*      */     
/* 4364 */     if (this.conn.getUseSqlStateCodes()) {
/* 4365 */       return 2;
/*      */     }
/*      */     
/* 4368 */     return 1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getStringFunctions()
/*      */     throws SQLException
/*      */   {
/* 4378 */     return "ASCII,BIN,BIT_LENGTH,CHAR,CHARACTER_LENGTH,CHAR_LENGTH,CONCAT,CONCAT_WS,CONV,ELT,EXPORT_SET,FIELD,FIND_IN_SET,HEX,INSERT,INSTR,LCASE,LEFT,LENGTH,LOAD_FILE,LOCATE,LOCATE,LOWER,LPAD,LTRIM,MAKE_SET,MATCH,MID,OCT,OCTET_LENGTH,ORD,POSITION,QUOTE,REPEAT,REPLACE,REVERSE,RIGHT,RPAD,RTRIM,SOUNDEX,SPACE,STRCMP,SUBSTRING,SUBSTRING,SUBSTRING,SUBSTRING,SUBSTRING_INDEX,TRIM,UCASE,UPPER";
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ResultSet getSuperTables(String arg0, String arg1, String arg2)
/*      */     throws SQLException
/*      */   {
/* 4388 */     Field[] fields = new Field[4];
/* 4389 */     fields[0] = new Field("", "TABLE_CAT", 1, 32);
/* 4390 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 32);
/* 4391 */     fields[2] = new Field("", "TABLE_NAME", 1, 32);
/* 4392 */     fields[3] = new Field("", "SUPERTABLE_NAME", 1, 32);
/*      */     
/* 4394 */     return buildResultSet(fields, new ArrayList());
/*      */   }
/*      */   
/*      */ 
/*      */   public ResultSet getSuperTypes(String arg0, String arg1, String arg2)
/*      */     throws SQLException
/*      */   {
/* 4401 */     Field[] fields = new Field[6];
/* 4402 */     fields[0] = new Field("", "TYPE_CAT", 1, 32);
/* 4403 */     fields[1] = new Field("", "TYPE_SCHEM", 1, 32);
/* 4404 */     fields[2] = new Field("", "TYPE_NAME", 1, 32);
/* 4405 */     fields[3] = new Field("", "SUPERTYPE_CAT", 1, 32);
/* 4406 */     fields[4] = new Field("", "SUPERTYPE_SCHEM", 1, 32);
/* 4407 */     fields[5] = new Field("", "SUPERTYPE_NAME", 1, 32);
/*      */     
/* 4409 */     return buildResultSet(fields, new ArrayList());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getSystemFunctions()
/*      */     throws SQLException
/*      */   {
/* 4419 */     return "DATABASE,USER,SYSTEM_USER,SESSION_USER,PASSWORD,ENCRYPT,LAST_INSERT_ID,VERSION";
/*      */   }
/*      */   
/*      */   protected String getTableNameWithCase(String table) {
/* 4423 */     String tableNameWithCase = this.conn.lowerCaseTableNames() ? table.toLowerCase() : table;
/*      */     
/* 4425 */     return tableNameWithCase;
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
/*      */   public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern)
/*      */     throws SQLException
/*      */   {
/* 4461 */     if (tableNamePattern == null) {
/* 4462 */       if (this.conn.getNullNamePatternMatchesAll()) {
/* 4463 */         tableNamePattern = "%";
/*      */       } else {
/* 4465 */         throw SQLError.createSQLException("Table name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 4470 */     Field[] fields = new Field[7];
/* 4471 */     fields[0] = new Field("", "TABLE_CAT", 1, 64);
/* 4472 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 1);
/* 4473 */     fields[2] = new Field("", "TABLE_NAME", 1, 64);
/* 4474 */     fields[3] = new Field("", "GRANTOR", 1, 77);
/* 4475 */     fields[4] = new Field("", "GRANTEE", 1, 77);
/* 4476 */     fields[5] = new Field("", "PRIVILEGE", 1, 64);
/* 4477 */     fields[6] = new Field("", "IS_GRANTABLE", 1, 3);
/*      */     
/* 4479 */     String grantQuery = "SELECT host,db,table_name,grantor,user,table_priv FROM mysql.tables_priv WHERE db LIKE ? AND table_name LIKE ?";
/*      */     
/* 4481 */     ResultSet results = null;
/* 4482 */     ArrayList<ResultSetRow> grantRows = new ArrayList();
/* 4483 */     PreparedStatement pStmt = null;
/*      */     try
/*      */     {
/* 4486 */       pStmt = prepareMetaDataSafeStatement(grantQuery);
/*      */       
/* 4488 */       pStmt.setString(1, (catalog != null) && (catalog.length() != 0) ? catalog : "%");
/* 4489 */       pStmt.setString(2, tableNamePattern);
/*      */       
/* 4491 */       results = pStmt.executeQuery();
/*      */       
/* 4493 */       while (results.next()) {
/* 4494 */         String host = results.getString(1);
/* 4495 */         String db = results.getString(2);
/* 4496 */         String table = results.getString(3);
/* 4497 */         String grantor = results.getString(4);
/* 4498 */         String user = results.getString(5);
/*      */         
/* 4500 */         if ((user == null) || (user.length() == 0)) {
/* 4501 */           user = "%";
/*      */         }
/*      */         
/* 4504 */         StringBuilder fullUser = new StringBuilder(user);
/*      */         
/* 4506 */         if ((host != null) && (this.conn.getUseHostsInPrivileges())) {
/* 4507 */           fullUser.append("@");
/* 4508 */           fullUser.append(host);
/*      */         }
/*      */         
/* 4511 */         String allPrivileges = results.getString(6);
/*      */         
/* 4513 */         if (allPrivileges != null) {
/* 4514 */           allPrivileges = allPrivileges.toUpperCase(Locale.ENGLISH);
/*      */           
/* 4516 */           StringTokenizer st = new StringTokenizer(allPrivileges, ",");
/*      */           
/* 4518 */           while (st.hasMoreTokens()) {
/* 4519 */             String privilege = st.nextToken().trim();
/*      */             
/*      */ 
/* 4522 */             ResultSet columnResults = null;
/*      */             try
/*      */             {
/* 4525 */               columnResults = getColumns(catalog, schemaPattern, table, "%");
/*      */               
/* 4527 */               while (columnResults.next()) {
/* 4528 */                 byte[][] tuple = new byte[8][];
/* 4529 */                 tuple[0] = s2b(db);
/* 4530 */                 tuple[1] = null;
/* 4531 */                 tuple[2] = s2b(table);
/*      */                 
/* 4533 */                 if (grantor != null) {
/* 4534 */                   tuple[3] = s2b(grantor);
/*      */                 } else {
/* 4536 */                   tuple[3] = null;
/*      */                 }
/*      */                 
/* 4539 */                 tuple[4] = s2b(fullUser.toString());
/* 4540 */                 tuple[5] = s2b(privilege);
/* 4541 */                 tuple[6] = null;
/* 4542 */                 grantRows.add(new ByteArrayRow(tuple, getExceptionInterceptor()));
/*      */               }
/*      */             } finally {
/* 4545 */               if (columnResults != null) {
/*      */                 try {
/* 4547 */                   columnResults.close();
/*      */                 }
/*      */                 catch (Exception ex) {}
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     } finally {
/* 4556 */       if (results != null) {
/*      */         try {
/* 4558 */           results.close();
/*      */         }
/*      */         catch (Exception ex) {}
/*      */         
/* 4562 */         results = null;
/*      */       }
/*      */       
/* 4565 */       if (pStmt != null) {
/*      */         try {
/* 4567 */           pStmt.close();
/*      */         }
/*      */         catch (Exception ex) {}
/*      */         
/* 4571 */         pStmt = null;
/*      */       }
/*      */     }
/*      */     
/* 4575 */     return buildResultSet(fields, grantRows);
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
/*      */   public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, final String[] types)
/*      */     throws SQLException
/*      */   {
/* 4613 */     if (tableNamePattern == null) {
/* 4614 */       if (this.conn.getNullNamePatternMatchesAll()) {
/* 4615 */         tableNamePattern = "%";
/*      */       } else {
/* 4617 */         throw SQLError.createSQLException("Table name pattern can not be NULL or empty.", "S1009", getExceptionInterceptor());
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 4622 */     final SortedMap<TableMetaDataKey, ResultSetRow> sortedRows = new TreeMap();
/* 4623 */     ArrayList<ResultSetRow> tuples = new ArrayList();
/*      */     
/* 4625 */     final java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
/*      */     
/*      */ 
/* 4628 */     String tmpCat = "";
/*      */     
/* 4630 */     if ((catalog == null) || (catalog.length() == 0)) {
/* 4631 */       if (this.conn.getNullCatalogMeansCurrent()) {
/* 4632 */         tmpCat = this.database;
/*      */       }
/*      */     } else {
/* 4635 */       tmpCat = catalog;
/*      */     }
/*      */     
/* 4638 */     List<String> parseList = StringUtils.splitDBdotName(tableNamePattern, tmpCat, this.quotedId, this.conn.isNoBackslashEscapesSet());
/*      */     String tableNamePat;
/* 4640 */     final String tableNamePat; if (parseList.size() == 2) {
/* 4641 */       tableNamePat = (String)parseList.get(1);
/*      */     } else {
/* 4643 */       tableNamePat = tableNamePattern;
/*      */     }
/*      */     try
/*      */     {
/* 4647 */       new IterateBlock(getCatalogIterator(catalog))
/*      */       {
/*      */         void forEach(String catalogStr) throws SQLException {
/* 4650 */           boolean operatingOnSystemDB = ("information_schema".equalsIgnoreCase(catalogStr)) || ("mysql".equalsIgnoreCase(catalogStr)) || ("performance_schema".equalsIgnoreCase(catalogStr));
/*      */           
/*      */ 
/* 4653 */           ResultSet results = null;
/*      */           try
/*      */           {
/*      */             try
/*      */             {
/* 4658 */               results = stmt.executeQuery((!DatabaseMetaData.this.conn.versionMeetsMinimum(5, 0, 2) ? "SHOW TABLES FROM " : "SHOW FULL TABLES FROM ") + StringUtils.quoteIdentifier(catalogStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()) + " LIKE " + StringUtils.quoteIdentifier(tableNamePat, "'", true));
/*      */ 
/*      */             }
/*      */             catch (SQLException sqlEx)
/*      */             {
/* 4663 */               if ("08S01".equals(sqlEx.getSQLState())) {
/* 4664 */                 throw sqlEx;
/*      */               }
/*      */               
/* 4667 */               return;
/*      */             }
/*      */             
/* 4670 */             boolean shouldReportTables = false;
/* 4671 */             boolean shouldReportViews = false;
/* 4672 */             boolean shouldReportSystemTables = false;
/* 4673 */             boolean shouldReportSystemViews = false;
/* 4674 */             boolean shouldReportLocalTemporaries = false;
/*      */             
/* 4676 */             if ((types == null) || (types.length == 0)) {
/* 4677 */               shouldReportTables = true;
/* 4678 */               shouldReportViews = true;
/* 4679 */               shouldReportSystemTables = true;
/* 4680 */               shouldReportSystemViews = true;
/* 4681 */               shouldReportLocalTemporaries = true;
/*      */             } else {
/* 4683 */               for (int i = 0; i < types.length; i++) {
/* 4684 */                 if (DatabaseMetaData.TableType.TABLE.equalsTo(types[i])) {
/* 4685 */                   shouldReportTables = true;
/*      */                 }
/* 4687 */                 else if (DatabaseMetaData.TableType.VIEW.equalsTo(types[i])) {
/* 4688 */                   shouldReportViews = true;
/*      */                 }
/* 4690 */                 else if (DatabaseMetaData.TableType.SYSTEM_TABLE.equalsTo(types[i])) {
/* 4691 */                   shouldReportSystemTables = true;
/*      */                 }
/* 4693 */                 else if (DatabaseMetaData.TableType.SYSTEM_VIEW.equalsTo(types[i])) {
/* 4694 */                   shouldReportSystemViews = true;
/*      */                 }
/* 4696 */                 else if (DatabaseMetaData.TableType.LOCAL_TEMPORARY.equalsTo(types[i])) {
/* 4697 */                   shouldReportLocalTemporaries = true;
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 4702 */             int typeColumnIndex = 1;
/* 4703 */             boolean hasTableTypes = false;
/*      */             
/* 4705 */             if (DatabaseMetaData.this.conn.versionMeetsMinimum(5, 0, 2)) {
/*      */               try
/*      */               {
/* 4708 */                 typeColumnIndex = results.findColumn("table_type");
/* 4709 */                 hasTableTypes = true;
/*      */ 
/*      */               }
/*      */               catch (SQLException sqlEx)
/*      */               {
/*      */                 try
/*      */                 {
/* 4716 */                   typeColumnIndex = results.findColumn("Type");
/* 4717 */                   hasTableTypes = true;
/*      */                 } catch (SQLException sqlEx2) {
/* 4719 */                   hasTableTypes = false;
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 4724 */             while (results.next()) {
/* 4725 */               byte[][] row = new byte[10][];
/* 4726 */               row[0] = (catalogStr == null ? null : DatabaseMetaData.this.s2b(catalogStr));
/* 4727 */               row[1] = null;
/* 4728 */               row[2] = results.getBytes(1);
/* 4729 */               row[4] = new byte[0];
/* 4730 */               row[5] = null;
/* 4731 */               row[6] = null;
/* 4732 */               row[7] = null;
/* 4733 */               row[8] = null;
/* 4734 */               row[9] = null;
/*      */               
/* 4736 */               if (hasTableTypes) {
/* 4737 */                 String tableType = results.getString(typeColumnIndex);
/*      */                 
/* 4739 */                 switch (DatabaseMetaData.11.$SwitchMap$com$mysql$jdbc$DatabaseMetaData$TableType[DatabaseMetaData.TableType.getTableTypeCompliantWith(tableType).ordinal()]) {
/*      */                 case 1: 
/* 4741 */                   boolean reportTable = false;
/* 4742 */                   DatabaseMetaData.TableMetaDataKey tablesKey = null;
/*      */                   
/* 4744 */                   if ((operatingOnSystemDB) && (shouldReportSystemTables)) {
/* 4745 */                     row[3] = DatabaseMetaData.TableType.SYSTEM_TABLE.asBytes();
/* 4746 */                     tablesKey = new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.this, DatabaseMetaData.TableType.SYSTEM_TABLE.getName(), catalogStr, null, results.getString(1));
/* 4747 */                     reportTable = true;
/*      */                   }
/* 4749 */                   else if ((!operatingOnSystemDB) && (shouldReportTables)) {
/* 4750 */                     row[3] = DatabaseMetaData.TableType.TABLE.asBytes();
/* 4751 */                     tablesKey = new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.this, DatabaseMetaData.TableType.TABLE.getName(), catalogStr, null, results.getString(1));
/* 4752 */                     reportTable = true;
/*      */                   }
/*      */                   
/* 4755 */                   if (reportTable) {
/* 4756 */                     sortedRows.put(tablesKey, new ByteArrayRow(row, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */                   }
/*      */                   
/*      */                   break;
/*      */                 case 2: 
/* 4761 */                   if (shouldReportViews) {
/* 4762 */                     row[3] = DatabaseMetaData.TableType.VIEW.asBytes();
/* 4763 */                     sortedRows.put(new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.this, DatabaseMetaData.TableType.VIEW.getName(), catalogStr, null, results.getString(1)), new ByteArrayRow(row, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */                   }
/*      */                   
/*      */ 
/*      */                   break;
/*      */                 case 3: 
/* 4769 */                   if (shouldReportSystemTables) {
/* 4770 */                     row[3] = DatabaseMetaData.TableType.SYSTEM_TABLE.asBytes();
/* 4771 */                     sortedRows.put(new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.this, DatabaseMetaData.TableType.SYSTEM_TABLE.getName(), catalogStr, null, results.getString(1)), new ByteArrayRow(row, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */                   }
/*      */                   
/*      */ 
/*      */                   break;
/*      */                 case 4: 
/* 4777 */                   if (shouldReportSystemViews) {
/* 4778 */                     row[3] = DatabaseMetaData.TableType.SYSTEM_VIEW.asBytes();
/* 4779 */                     sortedRows.put(new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.this, DatabaseMetaData.TableType.SYSTEM_VIEW.getName(), catalogStr, null, results.getString(1)), new ByteArrayRow(row, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */                   }
/*      */                   
/*      */ 
/*      */                   break;
/*      */                 case 5: 
/* 4785 */                   if (shouldReportLocalTemporaries) {
/* 4786 */                     row[3] = DatabaseMetaData.TableType.LOCAL_TEMPORARY.asBytes();
/* 4787 */                     sortedRows.put(new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.this, DatabaseMetaData.TableType.LOCAL_TEMPORARY.getName(), catalogStr, null, results.getString(1)), new ByteArrayRow(row, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */                   }
/*      */                   
/*      */ 
/*      */                   break;
/*      */                 default: 
/* 4793 */                   row[3] = DatabaseMetaData.TableType.TABLE.asBytes();
/* 4794 */                   sortedRows.put(new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.this, DatabaseMetaData.TableType.TABLE.getName(), catalogStr, null, results.getString(1)), new ByteArrayRow(row, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */                 
/*      */                 }
/*      */                 
/*      */               }
/* 4799 */               else if (shouldReportTables)
/*      */               {
/* 4801 */                 row[3] = DatabaseMetaData.TableType.TABLE.asBytes();
/* 4802 */                 sortedRows.put(new DatabaseMetaData.TableMetaDataKey(DatabaseMetaData.this, DatabaseMetaData.TableType.TABLE.getName(), catalogStr, null, results.getString(1)), new ByteArrayRow(row, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */               }
/*      */               
/*      */             }
/*      */           }
/*      */           finally
/*      */           {
/* 4809 */             if (results != null) {
/*      */               try {
/* 4811 */                 results.close();
/*      */               }
/*      */               catch (Exception ex) {}
/*      */               
/* 4815 */               results = null;
/*      */             }
/*      */           }
/*      */         }
/*      */       }.doForAll();
/*      */     } finally {
/* 4821 */       if (stmt != null) {
/* 4822 */         stmt.close();
/*      */       }
/*      */     }
/*      */     
/* 4826 */     tuples.addAll(sortedRows.values());
/* 4827 */     ResultSet tables = buildResultSet(createTablesFields(), tuples);
/*      */     
/* 4829 */     return tables;
/*      */   }
/*      */   
/*      */   protected Field[] createTablesFields() {
/* 4833 */     Field[] fields = new Field[10];
/* 4834 */     fields[0] = new Field("", "TABLE_CAT", 12, 255);
/* 4835 */     fields[1] = new Field("", "TABLE_SCHEM", 12, 0);
/* 4836 */     fields[2] = new Field("", "TABLE_NAME", 12, 255);
/* 4837 */     fields[3] = new Field("", "TABLE_TYPE", 12, 5);
/* 4838 */     fields[4] = new Field("", "REMARKS", 12, 0);
/* 4839 */     fields[5] = new Field("", "TYPE_CAT", 12, 0);
/* 4840 */     fields[6] = new Field("", "TYPE_SCHEM", 12, 0);
/* 4841 */     fields[7] = new Field("", "TYPE_NAME", 12, 0);
/* 4842 */     fields[8] = new Field("", "SELF_REFERENCING_COL_NAME", 12, 0);
/* 4843 */     fields[9] = new Field("", "REF_GENERATION", 12, 0);
/* 4844 */     return fields;
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
/*      */   public ResultSet getTableTypes()
/*      */     throws SQLException
/*      */   {
/* 4863 */     ArrayList<ResultSetRow> tuples = new ArrayList();
/* 4864 */     Field[] fields = { new Field("", "TABLE_TYPE", 12, 256) };
/*      */     
/* 4866 */     boolean minVersion5_0_1 = this.conn.versionMeetsMinimum(5, 0, 1);
/*      */     
/* 4868 */     tuples.add(new ByteArrayRow(new byte[][] { TableType.LOCAL_TEMPORARY.asBytes() }, getExceptionInterceptor()));
/* 4869 */     tuples.add(new ByteArrayRow(new byte[][] { TableType.SYSTEM_TABLE.asBytes() }, getExceptionInterceptor()));
/* 4870 */     if (minVersion5_0_1) {
/* 4871 */       tuples.add(new ByteArrayRow(new byte[][] { TableType.SYSTEM_VIEW.asBytes() }, getExceptionInterceptor()));
/*      */     }
/* 4873 */     tuples.add(new ByteArrayRow(new byte[][] { TableType.TABLE.asBytes() }, getExceptionInterceptor()));
/* 4874 */     if (minVersion5_0_1) {
/* 4875 */       tuples.add(new ByteArrayRow(new byte[][] { TableType.VIEW.asBytes() }, getExceptionInterceptor()));
/*      */     }
/*      */     
/* 4878 */     return buildResultSet(fields, tuples);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getTimeDateFunctions()
/*      */     throws SQLException
/*      */   {
/* 4888 */     return "DAYOFWEEK,WEEKDAY,DAYOFMONTH,DAYOFYEAR,MONTH,DAYNAME,MONTHNAME,QUARTER,WEEK,YEAR,HOUR,MINUTE,SECOND,PERIOD_ADD,PERIOD_DIFF,TO_DAYS,FROM_DAYS,DATE_FORMAT,TIME_FORMAT,CURDATE,CURRENT_DATE,CURTIME,CURRENT_TIME,NOW,SYSDATE,CURRENT_TIMESTAMP,UNIX_TIMESTAMP,FROM_UNIXTIME,SEC_TO_TIME,TIME_TO_SEC";
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ResultSet getTypeInfo()
/*      */     throws SQLException
/*      */   {
/* 4982 */     Field[] fields = new Field[18];
/* 4983 */     fields[0] = new Field("", "TYPE_NAME", 1, 32);
/* 4984 */     fields[1] = new Field("", "DATA_TYPE", 4, 5);
/* 4985 */     fields[2] = new Field("", "PRECISION", 4, 10);
/* 4986 */     fields[3] = new Field("", "LITERAL_PREFIX", 1, 4);
/* 4987 */     fields[4] = new Field("", "LITERAL_SUFFIX", 1, 4);
/* 4988 */     fields[5] = new Field("", "CREATE_PARAMS", 1, 32);
/* 4989 */     fields[6] = new Field("", "NULLABLE", 5, 5);
/* 4990 */     fields[7] = new Field("", "CASE_SENSITIVE", 16, 3);
/* 4991 */     fields[8] = new Field("", "SEARCHABLE", 5, 3);
/* 4992 */     fields[9] = new Field("", "UNSIGNED_ATTRIBUTE", 16, 3);
/* 4993 */     fields[10] = new Field("", "FIXED_PREC_SCALE", 16, 3);
/* 4994 */     fields[11] = new Field("", "AUTO_INCREMENT", 16, 3);
/* 4995 */     fields[12] = new Field("", "LOCAL_TYPE_NAME", 1, 32);
/* 4996 */     fields[13] = new Field("", "MINIMUM_SCALE", 5, 5);
/* 4997 */     fields[14] = new Field("", "MAXIMUM_SCALE", 5, 5);
/* 4998 */     fields[15] = new Field("", "SQL_DATA_TYPE", 4, 10);
/* 4999 */     fields[16] = new Field("", "SQL_DATETIME_SUB", 4, 10);
/* 5000 */     fields[17] = new Field("", "NUM_PREC_RADIX", 4, 10);
/*      */     
/* 5002 */     byte[][] rowVal = (byte[][])null;
/* 5003 */     ArrayList<ResultSetRow> tuples = new ArrayList();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 5011 */     rowVal = new byte[18][];
/* 5012 */     rowVal[0] = s2b("BIT");
/* 5013 */     rowVal[1] = Integer.toString(-7).getBytes();
/*      */     
/*      */ 
/* 5016 */     rowVal[2] = s2b("1");
/* 5017 */     rowVal[3] = s2b("");
/* 5018 */     rowVal[4] = s2b("");
/* 5019 */     rowVal[5] = s2b("");
/* 5020 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5023 */     rowVal[7] = s2b("true");
/* 5024 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5027 */     rowVal[9] = s2b("false");
/* 5028 */     rowVal[10] = s2b("false");
/* 5029 */     rowVal[11] = s2b("false");
/* 5030 */     rowVal[12] = s2b("BIT");
/* 5031 */     rowVal[13] = s2b("0");
/* 5032 */     rowVal[14] = s2b("0");
/* 5033 */     rowVal[15] = s2b("0");
/* 5034 */     rowVal[16] = s2b("0");
/* 5035 */     rowVal[17] = s2b("10");
/* 5036 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5041 */     rowVal = new byte[18][];
/* 5042 */     rowVal[0] = s2b("BOOL");
/* 5043 */     rowVal[1] = Integer.toString(-7).getBytes();
/*      */     
/*      */ 
/* 5046 */     rowVal[2] = s2b("1");
/* 5047 */     rowVal[3] = s2b("");
/* 5048 */     rowVal[4] = s2b("");
/* 5049 */     rowVal[5] = s2b("");
/* 5050 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5053 */     rowVal[7] = s2b("true");
/* 5054 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5057 */     rowVal[9] = s2b("false");
/* 5058 */     rowVal[10] = s2b("false");
/* 5059 */     rowVal[11] = s2b("false");
/* 5060 */     rowVal[12] = s2b("BOOL");
/* 5061 */     rowVal[13] = s2b("0");
/* 5062 */     rowVal[14] = s2b("0");
/* 5063 */     rowVal[15] = s2b("0");
/* 5064 */     rowVal[16] = s2b("0");
/* 5065 */     rowVal[17] = s2b("10");
/* 5066 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5071 */     rowVal = new byte[18][];
/* 5072 */     rowVal[0] = s2b("TINYINT");
/* 5073 */     rowVal[1] = Integer.toString(-6).getBytes();
/*      */     
/*      */ 
/* 5076 */     rowVal[2] = s2b("3");
/* 5077 */     rowVal[3] = s2b("");
/* 5078 */     rowVal[4] = s2b("");
/* 5079 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5080 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5083 */     rowVal[7] = s2b("false");
/* 5084 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5087 */     rowVal[9] = s2b("true");
/* 5088 */     rowVal[10] = s2b("false");
/* 5089 */     rowVal[11] = s2b("true");
/* 5090 */     rowVal[12] = s2b("TINYINT");
/* 5091 */     rowVal[13] = s2b("0");
/* 5092 */     rowVal[14] = s2b("0");
/* 5093 */     rowVal[15] = s2b("0");
/* 5094 */     rowVal[16] = s2b("0");
/* 5095 */     rowVal[17] = s2b("10");
/* 5096 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/* 5098 */     rowVal = new byte[18][];
/* 5099 */     rowVal[0] = s2b("TINYINT UNSIGNED");
/* 5100 */     rowVal[1] = Integer.toString(-6).getBytes();
/*      */     
/*      */ 
/* 5103 */     rowVal[2] = s2b("3");
/* 5104 */     rowVal[3] = s2b("");
/* 5105 */     rowVal[4] = s2b("");
/* 5106 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5107 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5110 */     rowVal[7] = s2b("false");
/* 5111 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5114 */     rowVal[9] = s2b("true");
/* 5115 */     rowVal[10] = s2b("false");
/* 5116 */     rowVal[11] = s2b("true");
/* 5117 */     rowVal[12] = s2b("TINYINT UNSIGNED");
/* 5118 */     rowVal[13] = s2b("0");
/* 5119 */     rowVal[14] = s2b("0");
/* 5120 */     rowVal[15] = s2b("0");
/* 5121 */     rowVal[16] = s2b("0");
/* 5122 */     rowVal[17] = s2b("10");
/* 5123 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5128 */     rowVal = new byte[18][];
/* 5129 */     rowVal[0] = s2b("BIGINT");
/* 5130 */     rowVal[1] = Integer.toString(-5).getBytes();
/*      */     
/*      */ 
/* 5133 */     rowVal[2] = s2b("19");
/* 5134 */     rowVal[3] = s2b("");
/* 5135 */     rowVal[4] = s2b("");
/* 5136 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5137 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5140 */     rowVal[7] = s2b("false");
/* 5141 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5144 */     rowVal[9] = s2b("true");
/* 5145 */     rowVal[10] = s2b("false");
/* 5146 */     rowVal[11] = s2b("true");
/* 5147 */     rowVal[12] = s2b("BIGINT");
/* 5148 */     rowVal[13] = s2b("0");
/* 5149 */     rowVal[14] = s2b("0");
/* 5150 */     rowVal[15] = s2b("0");
/* 5151 */     rowVal[16] = s2b("0");
/* 5152 */     rowVal[17] = s2b("10");
/* 5153 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/* 5155 */     rowVal = new byte[18][];
/* 5156 */     rowVal[0] = s2b("BIGINT UNSIGNED");
/* 5157 */     rowVal[1] = Integer.toString(-5).getBytes();
/*      */     
/*      */ 
/* 5160 */     rowVal[2] = s2b("20");
/* 5161 */     rowVal[3] = s2b("");
/* 5162 */     rowVal[4] = s2b("");
/* 5163 */     rowVal[5] = s2b("[(M)] [ZEROFILL]");
/* 5164 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5167 */     rowVal[7] = s2b("false");
/* 5168 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5171 */     rowVal[9] = s2b("true");
/* 5172 */     rowVal[10] = s2b("false");
/* 5173 */     rowVal[11] = s2b("true");
/* 5174 */     rowVal[12] = s2b("BIGINT UNSIGNED");
/* 5175 */     rowVal[13] = s2b("0");
/* 5176 */     rowVal[14] = s2b("0");
/* 5177 */     rowVal[15] = s2b("0");
/* 5178 */     rowVal[16] = s2b("0");
/* 5179 */     rowVal[17] = s2b("10");
/* 5180 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5185 */     rowVal = new byte[18][];
/* 5186 */     rowVal[0] = s2b("LONG VARBINARY");
/* 5187 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */     
/*      */ 
/* 5190 */     rowVal[2] = s2b("16777215");
/* 5191 */     rowVal[3] = s2b("'");
/* 5192 */     rowVal[4] = s2b("'");
/* 5193 */     rowVal[5] = s2b("");
/* 5194 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5197 */     rowVal[7] = s2b("true");
/* 5198 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5201 */     rowVal[9] = s2b("false");
/* 5202 */     rowVal[10] = s2b("false");
/* 5203 */     rowVal[11] = s2b("false");
/* 5204 */     rowVal[12] = s2b("LONG VARBINARY");
/* 5205 */     rowVal[13] = s2b("0");
/* 5206 */     rowVal[14] = s2b("0");
/* 5207 */     rowVal[15] = s2b("0");
/* 5208 */     rowVal[16] = s2b("0");
/* 5209 */     rowVal[17] = s2b("10");
/* 5210 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5215 */     rowVal = new byte[18][];
/* 5216 */     rowVal[0] = s2b("MEDIUMBLOB");
/* 5217 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */     
/*      */ 
/* 5220 */     rowVal[2] = s2b("16777215");
/* 5221 */     rowVal[3] = s2b("'");
/* 5222 */     rowVal[4] = s2b("'");
/* 5223 */     rowVal[5] = s2b("");
/* 5224 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5227 */     rowVal[7] = s2b("true");
/* 5228 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5231 */     rowVal[9] = s2b("false");
/* 5232 */     rowVal[10] = s2b("false");
/* 5233 */     rowVal[11] = s2b("false");
/* 5234 */     rowVal[12] = s2b("MEDIUMBLOB");
/* 5235 */     rowVal[13] = s2b("0");
/* 5236 */     rowVal[14] = s2b("0");
/* 5237 */     rowVal[15] = s2b("0");
/* 5238 */     rowVal[16] = s2b("0");
/* 5239 */     rowVal[17] = s2b("10");
/* 5240 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5245 */     rowVal = new byte[18][];
/* 5246 */     rowVal[0] = s2b("LONGBLOB");
/* 5247 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */     
/*      */ 
/* 5250 */     rowVal[2] = Integer.toString(Integer.MAX_VALUE).getBytes();
/*      */     
/*      */ 
/* 5253 */     rowVal[3] = s2b("'");
/* 5254 */     rowVal[4] = s2b("'");
/* 5255 */     rowVal[5] = s2b("");
/* 5256 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5259 */     rowVal[7] = s2b("true");
/* 5260 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5263 */     rowVal[9] = s2b("false");
/* 5264 */     rowVal[10] = s2b("false");
/* 5265 */     rowVal[11] = s2b("false");
/* 5266 */     rowVal[12] = s2b("LONGBLOB");
/* 5267 */     rowVal[13] = s2b("0");
/* 5268 */     rowVal[14] = s2b("0");
/* 5269 */     rowVal[15] = s2b("0");
/* 5270 */     rowVal[16] = s2b("0");
/* 5271 */     rowVal[17] = s2b("10");
/* 5272 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5277 */     rowVal = new byte[18][];
/* 5278 */     rowVal[0] = s2b("BLOB");
/* 5279 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */     
/*      */ 
/* 5282 */     rowVal[2] = s2b("65535");
/* 5283 */     rowVal[3] = s2b("'");
/* 5284 */     rowVal[4] = s2b("'");
/* 5285 */     rowVal[5] = s2b("");
/* 5286 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5289 */     rowVal[7] = s2b("true");
/* 5290 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5293 */     rowVal[9] = s2b("false");
/* 5294 */     rowVal[10] = s2b("false");
/* 5295 */     rowVal[11] = s2b("false");
/* 5296 */     rowVal[12] = s2b("BLOB");
/* 5297 */     rowVal[13] = s2b("0");
/* 5298 */     rowVal[14] = s2b("0");
/* 5299 */     rowVal[15] = s2b("0");
/* 5300 */     rowVal[16] = s2b("0");
/* 5301 */     rowVal[17] = s2b("10");
/* 5302 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5307 */     rowVal = new byte[18][];
/* 5308 */     rowVal[0] = s2b("TINYBLOB");
/* 5309 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */     
/*      */ 
/* 5312 */     rowVal[2] = s2b("255");
/* 5313 */     rowVal[3] = s2b("'");
/* 5314 */     rowVal[4] = s2b("'");
/* 5315 */     rowVal[5] = s2b("");
/* 5316 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5319 */     rowVal[7] = s2b("true");
/* 5320 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5323 */     rowVal[9] = s2b("false");
/* 5324 */     rowVal[10] = s2b("false");
/* 5325 */     rowVal[11] = s2b("false");
/* 5326 */     rowVal[12] = s2b("TINYBLOB");
/* 5327 */     rowVal[13] = s2b("0");
/* 5328 */     rowVal[14] = s2b("0");
/* 5329 */     rowVal[15] = s2b("0");
/* 5330 */     rowVal[16] = s2b("0");
/* 5331 */     rowVal[17] = s2b("10");
/* 5332 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 5338 */     rowVal = new byte[18][];
/* 5339 */     rowVal[0] = s2b("VARBINARY");
/* 5340 */     rowVal[1] = Integer.toString(-3).getBytes();
/*      */     
/*      */ 
/* 5343 */     rowVal[2] = s2b(this.conn.versionMeetsMinimum(5, 0, 3) ? "65535" : "255");
/* 5344 */     rowVal[3] = s2b("'");
/* 5345 */     rowVal[4] = s2b("'");
/* 5346 */     rowVal[5] = s2b("(M)");
/* 5347 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5350 */     rowVal[7] = s2b("true");
/* 5351 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5354 */     rowVal[9] = s2b("false");
/* 5355 */     rowVal[10] = s2b("false");
/* 5356 */     rowVal[11] = s2b("false");
/* 5357 */     rowVal[12] = s2b("VARBINARY");
/* 5358 */     rowVal[13] = s2b("0");
/* 5359 */     rowVal[14] = s2b("0");
/* 5360 */     rowVal[15] = s2b("0");
/* 5361 */     rowVal[16] = s2b("0");
/* 5362 */     rowVal[17] = s2b("10");
/* 5363 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 5369 */     rowVal = new byte[18][];
/* 5370 */     rowVal[0] = s2b("BINARY");
/* 5371 */     rowVal[1] = Integer.toString(-2).getBytes();
/*      */     
/*      */ 
/* 5374 */     rowVal[2] = s2b("255");
/* 5375 */     rowVal[3] = s2b("'");
/* 5376 */     rowVal[4] = s2b("'");
/* 5377 */     rowVal[5] = s2b("(M)");
/* 5378 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5381 */     rowVal[7] = s2b("true");
/* 5382 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5385 */     rowVal[9] = s2b("false");
/* 5386 */     rowVal[10] = s2b("false");
/* 5387 */     rowVal[11] = s2b("false");
/* 5388 */     rowVal[12] = s2b("BINARY");
/* 5389 */     rowVal[13] = s2b("0");
/* 5390 */     rowVal[14] = s2b("0");
/* 5391 */     rowVal[15] = s2b("0");
/* 5392 */     rowVal[16] = s2b("0");
/* 5393 */     rowVal[17] = s2b("10");
/* 5394 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5399 */     rowVal = new byte[18][];
/* 5400 */     rowVal[0] = s2b("LONG VARCHAR");
/* 5401 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */     
/*      */ 
/* 5404 */     rowVal[2] = s2b("16777215");
/* 5405 */     rowVal[3] = s2b("'");
/* 5406 */     rowVal[4] = s2b("'");
/* 5407 */     rowVal[5] = s2b("");
/* 5408 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5411 */     rowVal[7] = s2b("false");
/* 5412 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5415 */     rowVal[9] = s2b("false");
/* 5416 */     rowVal[10] = s2b("false");
/* 5417 */     rowVal[11] = s2b("false");
/* 5418 */     rowVal[12] = s2b("LONG VARCHAR");
/* 5419 */     rowVal[13] = s2b("0");
/* 5420 */     rowVal[14] = s2b("0");
/* 5421 */     rowVal[15] = s2b("0");
/* 5422 */     rowVal[16] = s2b("0");
/* 5423 */     rowVal[17] = s2b("10");
/* 5424 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5429 */     rowVal = new byte[18][];
/* 5430 */     rowVal[0] = s2b("MEDIUMTEXT");
/* 5431 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */     
/*      */ 
/* 5434 */     rowVal[2] = s2b("16777215");
/* 5435 */     rowVal[3] = s2b("'");
/* 5436 */     rowVal[4] = s2b("'");
/* 5437 */     rowVal[5] = s2b("");
/* 5438 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5441 */     rowVal[7] = s2b("false");
/* 5442 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5445 */     rowVal[9] = s2b("false");
/* 5446 */     rowVal[10] = s2b("false");
/* 5447 */     rowVal[11] = s2b("false");
/* 5448 */     rowVal[12] = s2b("MEDIUMTEXT");
/* 5449 */     rowVal[13] = s2b("0");
/* 5450 */     rowVal[14] = s2b("0");
/* 5451 */     rowVal[15] = s2b("0");
/* 5452 */     rowVal[16] = s2b("0");
/* 5453 */     rowVal[17] = s2b("10");
/* 5454 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5459 */     rowVal = new byte[18][];
/* 5460 */     rowVal[0] = s2b("LONGTEXT");
/* 5461 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */     
/*      */ 
/* 5464 */     rowVal[2] = Integer.toString(Integer.MAX_VALUE).getBytes();
/*      */     
/*      */ 
/* 5467 */     rowVal[3] = s2b("'");
/* 5468 */     rowVal[4] = s2b("'");
/* 5469 */     rowVal[5] = s2b("");
/* 5470 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5473 */     rowVal[7] = s2b("false");
/* 5474 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5477 */     rowVal[9] = s2b("false");
/* 5478 */     rowVal[10] = s2b("false");
/* 5479 */     rowVal[11] = s2b("false");
/* 5480 */     rowVal[12] = s2b("LONGTEXT");
/* 5481 */     rowVal[13] = s2b("0");
/* 5482 */     rowVal[14] = s2b("0");
/* 5483 */     rowVal[15] = s2b("0");
/* 5484 */     rowVal[16] = s2b("0");
/* 5485 */     rowVal[17] = s2b("10");
/* 5486 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5491 */     rowVal = new byte[18][];
/* 5492 */     rowVal[0] = s2b("TEXT");
/* 5493 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */     
/*      */ 
/* 5496 */     rowVal[2] = s2b("65535");
/* 5497 */     rowVal[3] = s2b("'");
/* 5498 */     rowVal[4] = s2b("'");
/* 5499 */     rowVal[5] = s2b("");
/* 5500 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5503 */     rowVal[7] = s2b("false");
/* 5504 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5507 */     rowVal[9] = s2b("false");
/* 5508 */     rowVal[10] = s2b("false");
/* 5509 */     rowVal[11] = s2b("false");
/* 5510 */     rowVal[12] = s2b("TEXT");
/* 5511 */     rowVal[13] = s2b("0");
/* 5512 */     rowVal[14] = s2b("0");
/* 5513 */     rowVal[15] = s2b("0");
/* 5514 */     rowVal[16] = s2b("0");
/* 5515 */     rowVal[17] = s2b("10");
/* 5516 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5521 */     rowVal = new byte[18][];
/* 5522 */     rowVal[0] = s2b("TINYTEXT");
/* 5523 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */     
/*      */ 
/* 5526 */     rowVal[2] = s2b("255");
/* 5527 */     rowVal[3] = s2b("'");
/* 5528 */     rowVal[4] = s2b("'");
/* 5529 */     rowVal[5] = s2b("");
/* 5530 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5533 */     rowVal[7] = s2b("false");
/* 5534 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5537 */     rowVal[9] = s2b("false");
/* 5538 */     rowVal[10] = s2b("false");
/* 5539 */     rowVal[11] = s2b("false");
/* 5540 */     rowVal[12] = s2b("TINYTEXT");
/* 5541 */     rowVal[13] = s2b("0");
/* 5542 */     rowVal[14] = s2b("0");
/* 5543 */     rowVal[15] = s2b("0");
/* 5544 */     rowVal[16] = s2b("0");
/* 5545 */     rowVal[17] = s2b("10");
/* 5546 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5551 */     rowVal = new byte[18][];
/* 5552 */     rowVal[0] = s2b("CHAR");
/* 5553 */     rowVal[1] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5556 */     rowVal[2] = s2b("255");
/* 5557 */     rowVal[3] = s2b("'");
/* 5558 */     rowVal[4] = s2b("'");
/* 5559 */     rowVal[5] = s2b("(M)");
/* 5560 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5563 */     rowVal[7] = s2b("false");
/* 5564 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5567 */     rowVal[9] = s2b("false");
/* 5568 */     rowVal[10] = s2b("false");
/* 5569 */     rowVal[11] = s2b("false");
/* 5570 */     rowVal[12] = s2b("CHAR");
/* 5571 */     rowVal[13] = s2b("0");
/* 5572 */     rowVal[14] = s2b("0");
/* 5573 */     rowVal[15] = s2b("0");
/* 5574 */     rowVal[16] = s2b("0");
/* 5575 */     rowVal[17] = s2b("10");
/* 5576 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/* 5580 */     int decimalPrecision = 254;
/*      */     
/* 5582 */     if (this.conn.versionMeetsMinimum(5, 0, 3)) {
/* 5583 */       if (this.conn.versionMeetsMinimum(5, 0, 6)) {
/* 5584 */         decimalPrecision = 65;
/*      */       } else {
/* 5586 */         decimalPrecision = 64;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5593 */     rowVal = new byte[18][];
/* 5594 */     rowVal[0] = s2b("NUMERIC");
/* 5595 */     rowVal[1] = Integer.toString(2).getBytes();
/*      */     
/*      */ 
/* 5598 */     rowVal[2] = s2b(String.valueOf(decimalPrecision));
/* 5599 */     rowVal[3] = s2b("");
/* 5600 */     rowVal[4] = s2b("");
/* 5601 */     rowVal[5] = s2b("[(M[,D])] [ZEROFILL]");
/* 5602 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5605 */     rowVal[7] = s2b("false");
/* 5606 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5609 */     rowVal[9] = s2b("false");
/* 5610 */     rowVal[10] = s2b("false");
/* 5611 */     rowVal[11] = s2b("true");
/* 5612 */     rowVal[12] = s2b("NUMERIC");
/* 5613 */     rowVal[13] = s2b("-308");
/* 5614 */     rowVal[14] = s2b("308");
/* 5615 */     rowVal[15] = s2b("0");
/* 5616 */     rowVal[16] = s2b("0");
/* 5617 */     rowVal[17] = s2b("10");
/* 5618 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5623 */     rowVal = new byte[18][];
/* 5624 */     rowVal[0] = s2b("DECIMAL");
/* 5625 */     rowVal[1] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5628 */     rowVal[2] = s2b(String.valueOf(decimalPrecision));
/* 5629 */     rowVal[3] = s2b("");
/* 5630 */     rowVal[4] = s2b("");
/* 5631 */     rowVal[5] = s2b("[(M[,D])] [ZEROFILL]");
/* 5632 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5635 */     rowVal[7] = s2b("false");
/* 5636 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5639 */     rowVal[9] = s2b("false");
/* 5640 */     rowVal[10] = s2b("false");
/* 5641 */     rowVal[11] = s2b("true");
/* 5642 */     rowVal[12] = s2b("DECIMAL");
/* 5643 */     rowVal[13] = s2b("-308");
/* 5644 */     rowVal[14] = s2b("308");
/* 5645 */     rowVal[15] = s2b("0");
/* 5646 */     rowVal[16] = s2b("0");
/* 5647 */     rowVal[17] = s2b("10");
/* 5648 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5653 */     rowVal = new byte[18][];
/* 5654 */     rowVal[0] = s2b("INTEGER");
/* 5655 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */     
/*      */ 
/* 5658 */     rowVal[2] = s2b("10");
/* 5659 */     rowVal[3] = s2b("");
/* 5660 */     rowVal[4] = s2b("");
/* 5661 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5662 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5665 */     rowVal[7] = s2b("false");
/* 5666 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5669 */     rowVal[9] = s2b("true");
/* 5670 */     rowVal[10] = s2b("false");
/* 5671 */     rowVal[11] = s2b("true");
/* 5672 */     rowVal[12] = s2b("INTEGER");
/* 5673 */     rowVal[13] = s2b("0");
/* 5674 */     rowVal[14] = s2b("0");
/* 5675 */     rowVal[15] = s2b("0");
/* 5676 */     rowVal[16] = s2b("0");
/* 5677 */     rowVal[17] = s2b("10");
/* 5678 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/* 5680 */     rowVal = new byte[18][];
/* 5681 */     rowVal[0] = s2b("INTEGER UNSIGNED");
/* 5682 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */     
/*      */ 
/* 5685 */     rowVal[2] = s2b("10");
/* 5686 */     rowVal[3] = s2b("");
/* 5687 */     rowVal[4] = s2b("");
/* 5688 */     rowVal[5] = s2b("[(M)] [ZEROFILL]");
/* 5689 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5692 */     rowVal[7] = s2b("false");
/* 5693 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5696 */     rowVal[9] = s2b("true");
/* 5697 */     rowVal[10] = s2b("false");
/* 5698 */     rowVal[11] = s2b("true");
/* 5699 */     rowVal[12] = s2b("INTEGER UNSIGNED");
/* 5700 */     rowVal[13] = s2b("0");
/* 5701 */     rowVal[14] = s2b("0");
/* 5702 */     rowVal[15] = s2b("0");
/* 5703 */     rowVal[16] = s2b("0");
/* 5704 */     rowVal[17] = s2b("10");
/* 5705 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5710 */     rowVal = new byte[18][];
/* 5711 */     rowVal[0] = s2b("INT");
/* 5712 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */     
/*      */ 
/* 5715 */     rowVal[2] = s2b("10");
/* 5716 */     rowVal[3] = s2b("");
/* 5717 */     rowVal[4] = s2b("");
/* 5718 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5719 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5722 */     rowVal[7] = s2b("false");
/* 5723 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5726 */     rowVal[9] = s2b("true");
/* 5727 */     rowVal[10] = s2b("false");
/* 5728 */     rowVal[11] = s2b("true");
/* 5729 */     rowVal[12] = s2b("INT");
/* 5730 */     rowVal[13] = s2b("0");
/* 5731 */     rowVal[14] = s2b("0");
/* 5732 */     rowVal[15] = s2b("0");
/* 5733 */     rowVal[16] = s2b("0");
/* 5734 */     rowVal[17] = s2b("10");
/* 5735 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/* 5737 */     rowVal = new byte[18][];
/* 5738 */     rowVal[0] = s2b("INT UNSIGNED");
/* 5739 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */     
/*      */ 
/* 5742 */     rowVal[2] = s2b("10");
/* 5743 */     rowVal[3] = s2b("");
/* 5744 */     rowVal[4] = s2b("");
/* 5745 */     rowVal[5] = s2b("[(M)] [ZEROFILL]");
/* 5746 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5749 */     rowVal[7] = s2b("false");
/* 5750 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5753 */     rowVal[9] = s2b("true");
/* 5754 */     rowVal[10] = s2b("false");
/* 5755 */     rowVal[11] = s2b("true");
/* 5756 */     rowVal[12] = s2b("INT UNSIGNED");
/* 5757 */     rowVal[13] = s2b("0");
/* 5758 */     rowVal[14] = s2b("0");
/* 5759 */     rowVal[15] = s2b("0");
/* 5760 */     rowVal[16] = s2b("0");
/* 5761 */     rowVal[17] = s2b("10");
/* 5762 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5767 */     rowVal = new byte[18][];
/* 5768 */     rowVal[0] = s2b("MEDIUMINT");
/* 5769 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */     
/*      */ 
/* 5772 */     rowVal[2] = s2b("7");
/* 5773 */     rowVal[3] = s2b("");
/* 5774 */     rowVal[4] = s2b("");
/* 5775 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5776 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5779 */     rowVal[7] = s2b("false");
/* 5780 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5783 */     rowVal[9] = s2b("true");
/* 5784 */     rowVal[10] = s2b("false");
/* 5785 */     rowVal[11] = s2b("true");
/* 5786 */     rowVal[12] = s2b("MEDIUMINT");
/* 5787 */     rowVal[13] = s2b("0");
/* 5788 */     rowVal[14] = s2b("0");
/* 5789 */     rowVal[15] = s2b("0");
/* 5790 */     rowVal[16] = s2b("0");
/* 5791 */     rowVal[17] = s2b("10");
/* 5792 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/* 5794 */     rowVal = new byte[18][];
/* 5795 */     rowVal[0] = s2b("MEDIUMINT UNSIGNED");
/* 5796 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */     
/*      */ 
/* 5799 */     rowVal[2] = s2b("8");
/* 5800 */     rowVal[3] = s2b("");
/* 5801 */     rowVal[4] = s2b("");
/* 5802 */     rowVal[5] = s2b("[(M)] [ZEROFILL]");
/* 5803 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5806 */     rowVal[7] = s2b("false");
/* 5807 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5810 */     rowVal[9] = s2b("true");
/* 5811 */     rowVal[10] = s2b("false");
/* 5812 */     rowVal[11] = s2b("true");
/* 5813 */     rowVal[12] = s2b("MEDIUMINT UNSIGNED");
/* 5814 */     rowVal[13] = s2b("0");
/* 5815 */     rowVal[14] = s2b("0");
/* 5816 */     rowVal[15] = s2b("0");
/* 5817 */     rowVal[16] = s2b("0");
/* 5818 */     rowVal[17] = s2b("10");
/* 5819 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5824 */     rowVal = new byte[18][];
/* 5825 */     rowVal[0] = s2b("SMALLINT");
/* 5826 */     rowVal[1] = Integer.toString(5).getBytes();
/*      */     
/*      */ 
/* 5829 */     rowVal[2] = s2b("5");
/* 5830 */     rowVal[3] = s2b("");
/* 5831 */     rowVal[4] = s2b("");
/* 5832 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5833 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5836 */     rowVal[7] = s2b("false");
/* 5837 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5840 */     rowVal[9] = s2b("true");
/* 5841 */     rowVal[10] = s2b("false");
/* 5842 */     rowVal[11] = s2b("true");
/* 5843 */     rowVal[12] = s2b("SMALLINT");
/* 5844 */     rowVal[13] = s2b("0");
/* 5845 */     rowVal[14] = s2b("0");
/* 5846 */     rowVal[15] = s2b("0");
/* 5847 */     rowVal[16] = s2b("0");
/* 5848 */     rowVal[17] = s2b("10");
/* 5849 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/* 5851 */     rowVal = new byte[18][];
/* 5852 */     rowVal[0] = s2b("SMALLINT UNSIGNED");
/* 5853 */     rowVal[1] = Integer.toString(5).getBytes();
/*      */     
/*      */ 
/* 5856 */     rowVal[2] = s2b("5");
/* 5857 */     rowVal[3] = s2b("");
/* 5858 */     rowVal[4] = s2b("");
/* 5859 */     rowVal[5] = s2b("[(M)] [ZEROFILL]");
/* 5860 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5863 */     rowVal[7] = s2b("false");
/* 5864 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5867 */     rowVal[9] = s2b("true");
/* 5868 */     rowVal[10] = s2b("false");
/* 5869 */     rowVal[11] = s2b("true");
/* 5870 */     rowVal[12] = s2b("SMALLINT UNSIGNED");
/* 5871 */     rowVal[13] = s2b("0");
/* 5872 */     rowVal[14] = s2b("0");
/* 5873 */     rowVal[15] = s2b("0");
/* 5874 */     rowVal[16] = s2b("0");
/* 5875 */     rowVal[17] = s2b("10");
/* 5876 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 5882 */     rowVal = new byte[18][];
/* 5883 */     rowVal[0] = s2b("FLOAT");
/* 5884 */     rowVal[1] = Integer.toString(7).getBytes();
/*      */     
/*      */ 
/* 5887 */     rowVal[2] = s2b("10");
/* 5888 */     rowVal[3] = s2b("");
/* 5889 */     rowVal[4] = s2b("");
/* 5890 */     rowVal[5] = s2b("[(M,D)] [ZEROFILL]");
/* 5891 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5894 */     rowVal[7] = s2b("false");
/* 5895 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5898 */     rowVal[9] = s2b("false");
/* 5899 */     rowVal[10] = s2b("false");
/* 5900 */     rowVal[11] = s2b("true");
/* 5901 */     rowVal[12] = s2b("FLOAT");
/* 5902 */     rowVal[13] = s2b("-38");
/* 5903 */     rowVal[14] = s2b("38");
/* 5904 */     rowVal[15] = s2b("0");
/* 5905 */     rowVal[16] = s2b("0");
/* 5906 */     rowVal[17] = s2b("10");
/* 5907 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5912 */     rowVal = new byte[18][];
/* 5913 */     rowVal[0] = s2b("DOUBLE");
/* 5914 */     rowVal[1] = Integer.toString(8).getBytes();
/*      */     
/*      */ 
/* 5917 */     rowVal[2] = s2b("17");
/* 5918 */     rowVal[3] = s2b("");
/* 5919 */     rowVal[4] = s2b("");
/* 5920 */     rowVal[5] = s2b("[(M,D)] [ZEROFILL]");
/* 5921 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5924 */     rowVal[7] = s2b("false");
/* 5925 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5928 */     rowVal[9] = s2b("false");
/* 5929 */     rowVal[10] = s2b("false");
/* 5930 */     rowVal[11] = s2b("true");
/* 5931 */     rowVal[12] = s2b("DOUBLE");
/* 5932 */     rowVal[13] = s2b("-308");
/* 5933 */     rowVal[14] = s2b("308");
/* 5934 */     rowVal[15] = s2b("0");
/* 5935 */     rowVal[16] = s2b("0");
/* 5936 */     rowVal[17] = s2b("10");
/* 5937 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5942 */     rowVal = new byte[18][];
/* 5943 */     rowVal[0] = s2b("DOUBLE PRECISION");
/* 5944 */     rowVal[1] = Integer.toString(8).getBytes();
/*      */     
/*      */ 
/* 5947 */     rowVal[2] = s2b("17");
/* 5948 */     rowVal[3] = s2b("");
/* 5949 */     rowVal[4] = s2b("");
/* 5950 */     rowVal[5] = s2b("[(M,D)] [ZEROFILL]");
/* 5951 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5954 */     rowVal[7] = s2b("false");
/* 5955 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5958 */     rowVal[9] = s2b("false");
/* 5959 */     rowVal[10] = s2b("false");
/* 5960 */     rowVal[11] = s2b("true");
/* 5961 */     rowVal[12] = s2b("DOUBLE PRECISION");
/* 5962 */     rowVal[13] = s2b("-308");
/* 5963 */     rowVal[14] = s2b("308");
/* 5964 */     rowVal[15] = s2b("0");
/* 5965 */     rowVal[16] = s2b("0");
/* 5966 */     rowVal[17] = s2b("10");
/* 5967 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 5972 */     rowVal = new byte[18][];
/* 5973 */     rowVal[0] = s2b("REAL");
/* 5974 */     rowVal[1] = Integer.toString(8).getBytes();
/*      */     
/*      */ 
/* 5977 */     rowVal[2] = s2b("17");
/* 5978 */     rowVal[3] = s2b("");
/* 5979 */     rowVal[4] = s2b("");
/* 5980 */     rowVal[5] = s2b("[(M,D)] [ZEROFILL]");
/* 5981 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 5984 */     rowVal[7] = s2b("false");
/* 5985 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 5988 */     rowVal[9] = s2b("false");
/* 5989 */     rowVal[10] = s2b("false");
/* 5990 */     rowVal[11] = s2b("true");
/* 5991 */     rowVal[12] = s2b("REAL");
/* 5992 */     rowVal[13] = s2b("-308");
/* 5993 */     rowVal[14] = s2b("308");
/* 5994 */     rowVal[15] = s2b("0");
/* 5995 */     rowVal[16] = s2b("0");
/* 5996 */     rowVal[17] = s2b("10");
/* 5997 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 6002 */     rowVal = new byte[18][];
/* 6003 */     rowVal[0] = s2b("VARCHAR");
/* 6004 */     rowVal[1] = Integer.toString(12).getBytes();
/*      */     
/*      */ 
/* 6007 */     rowVal[2] = s2b(this.conn.versionMeetsMinimum(5, 0, 3) ? "65535" : "255");
/* 6008 */     rowVal[3] = s2b("'");
/* 6009 */     rowVal[4] = s2b("'");
/* 6010 */     rowVal[5] = s2b("(M)");
/* 6011 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 6014 */     rowVal[7] = s2b("false");
/* 6015 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 6018 */     rowVal[9] = s2b("false");
/* 6019 */     rowVal[10] = s2b("false");
/* 6020 */     rowVal[11] = s2b("false");
/* 6021 */     rowVal[12] = s2b("VARCHAR");
/* 6022 */     rowVal[13] = s2b("0");
/* 6023 */     rowVal[14] = s2b("0");
/* 6024 */     rowVal[15] = s2b("0");
/* 6025 */     rowVal[16] = s2b("0");
/* 6026 */     rowVal[17] = s2b("10");
/* 6027 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 6032 */     rowVal = new byte[18][];
/* 6033 */     rowVal[0] = s2b("ENUM");
/* 6034 */     rowVal[1] = Integer.toString(12).getBytes();
/*      */     
/*      */ 
/* 6037 */     rowVal[2] = s2b("65535");
/* 6038 */     rowVal[3] = s2b("'");
/* 6039 */     rowVal[4] = s2b("'");
/* 6040 */     rowVal[5] = s2b("");
/* 6041 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 6044 */     rowVal[7] = s2b("false");
/* 6045 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 6048 */     rowVal[9] = s2b("false");
/* 6049 */     rowVal[10] = s2b("false");
/* 6050 */     rowVal[11] = s2b("false");
/* 6051 */     rowVal[12] = s2b("ENUM");
/* 6052 */     rowVal[13] = s2b("0");
/* 6053 */     rowVal[14] = s2b("0");
/* 6054 */     rowVal[15] = s2b("0");
/* 6055 */     rowVal[16] = s2b("0");
/* 6056 */     rowVal[17] = s2b("10");
/* 6057 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 6062 */     rowVal = new byte[18][];
/* 6063 */     rowVal[0] = s2b("SET");
/* 6064 */     rowVal[1] = Integer.toString(12).getBytes();
/*      */     
/*      */ 
/* 6067 */     rowVal[2] = s2b("64");
/* 6068 */     rowVal[3] = s2b("'");
/* 6069 */     rowVal[4] = s2b("'");
/* 6070 */     rowVal[5] = s2b("");
/* 6071 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 6074 */     rowVal[7] = s2b("false");
/* 6075 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 6078 */     rowVal[9] = s2b("false");
/* 6079 */     rowVal[10] = s2b("false");
/* 6080 */     rowVal[11] = s2b("false");
/* 6081 */     rowVal[12] = s2b("SET");
/* 6082 */     rowVal[13] = s2b("0");
/* 6083 */     rowVal[14] = s2b("0");
/* 6084 */     rowVal[15] = s2b("0");
/* 6085 */     rowVal[16] = s2b("0");
/* 6086 */     rowVal[17] = s2b("10");
/* 6087 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 6092 */     rowVal = new byte[18][];
/* 6093 */     rowVal[0] = s2b("DATE");
/* 6094 */     rowVal[1] = Integer.toString(91).getBytes();
/*      */     
/*      */ 
/* 6097 */     rowVal[2] = s2b("0");
/* 6098 */     rowVal[3] = s2b("'");
/* 6099 */     rowVal[4] = s2b("'");
/* 6100 */     rowVal[5] = s2b("");
/* 6101 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 6104 */     rowVal[7] = s2b("false");
/* 6105 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 6108 */     rowVal[9] = s2b("false");
/* 6109 */     rowVal[10] = s2b("false");
/* 6110 */     rowVal[11] = s2b("false");
/* 6111 */     rowVal[12] = s2b("DATE");
/* 6112 */     rowVal[13] = s2b("0");
/* 6113 */     rowVal[14] = s2b("0");
/* 6114 */     rowVal[15] = s2b("0");
/* 6115 */     rowVal[16] = s2b("0");
/* 6116 */     rowVal[17] = s2b("10");
/* 6117 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 6122 */     rowVal = new byte[18][];
/* 6123 */     rowVal[0] = s2b("TIME");
/* 6124 */     rowVal[1] = Integer.toString(92).getBytes();
/*      */     
/*      */ 
/* 6127 */     rowVal[2] = s2b("0");
/* 6128 */     rowVal[3] = s2b("'");
/* 6129 */     rowVal[4] = s2b("'");
/* 6130 */     rowVal[5] = s2b("");
/* 6131 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 6134 */     rowVal[7] = s2b("false");
/* 6135 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 6138 */     rowVal[9] = s2b("false");
/* 6139 */     rowVal[10] = s2b("false");
/* 6140 */     rowVal[11] = s2b("false");
/* 6141 */     rowVal[12] = s2b("TIME");
/* 6142 */     rowVal[13] = s2b("0");
/* 6143 */     rowVal[14] = s2b("0");
/* 6144 */     rowVal[15] = s2b("0");
/* 6145 */     rowVal[16] = s2b("0");
/* 6146 */     rowVal[17] = s2b("10");
/* 6147 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 6152 */     rowVal = new byte[18][];
/* 6153 */     rowVal[0] = s2b("DATETIME");
/* 6154 */     rowVal[1] = Integer.toString(93).getBytes();
/*      */     
/*      */ 
/* 6157 */     rowVal[2] = s2b("0");
/* 6158 */     rowVal[3] = s2b("'");
/* 6159 */     rowVal[4] = s2b("'");
/* 6160 */     rowVal[5] = s2b("");
/* 6161 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 6164 */     rowVal[7] = s2b("false");
/* 6165 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 6168 */     rowVal[9] = s2b("false");
/* 6169 */     rowVal[10] = s2b("false");
/* 6170 */     rowVal[11] = s2b("false");
/* 6171 */     rowVal[12] = s2b("DATETIME");
/* 6172 */     rowVal[13] = s2b("0");
/* 6173 */     rowVal[14] = s2b("0");
/* 6174 */     rowVal[15] = s2b("0");
/* 6175 */     rowVal[16] = s2b("0");
/* 6176 */     rowVal[17] = s2b("10");
/* 6177 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 6182 */     rowVal = new byte[18][];
/* 6183 */     rowVal[0] = s2b("TIMESTAMP");
/* 6184 */     rowVal[1] = Integer.toString(93).getBytes();
/*      */     
/*      */ 
/* 6187 */     rowVal[2] = s2b("0");
/* 6188 */     rowVal[3] = s2b("'");
/* 6189 */     rowVal[4] = s2b("'");
/* 6190 */     rowVal[5] = s2b("[(M)]");
/* 6191 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */     
/*      */ 
/* 6194 */     rowVal[7] = s2b("false");
/* 6195 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */     
/*      */ 
/* 6198 */     rowVal[9] = s2b("false");
/* 6199 */     rowVal[10] = s2b("false");
/* 6200 */     rowVal[11] = s2b("false");
/* 6201 */     rowVal[12] = s2b("TIMESTAMP");
/* 6202 */     rowVal[13] = s2b("0");
/* 6203 */     rowVal[14] = s2b("0");
/* 6204 */     rowVal[15] = s2b("0");
/* 6205 */     rowVal[16] = s2b("0");
/* 6206 */     rowVal[17] = s2b("10");
/* 6207 */     tuples.add(new ByteArrayRow(rowVal, getExceptionInterceptor()));
/*      */     
/* 6209 */     return buildResultSet(fields, tuples);
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
/*      */   public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types)
/*      */     throws SQLException
/*      */   {
/* 6250 */     Field[] fields = new Field[7];
/* 6251 */     fields[0] = new Field("", "TYPE_CAT", 12, 32);
/* 6252 */     fields[1] = new Field("", "TYPE_SCHEM", 12, 32);
/* 6253 */     fields[2] = new Field("", "TYPE_NAME", 12, 32);
/* 6254 */     fields[3] = new Field("", "CLASS_NAME", 12, 32);
/* 6255 */     fields[4] = new Field("", "DATA_TYPE", 4, 10);
/* 6256 */     fields[5] = new Field("", "REMARKS", 12, 32);
/* 6257 */     fields[6] = new Field("", "BASE_TYPE", 5, 10);
/*      */     
/* 6259 */     ArrayList<ResultSetRow> tuples = new ArrayList();
/*      */     
/* 6261 */     return buildResultSet(fields, tuples);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getURL()
/*      */     throws SQLException
/*      */   {
/* 6271 */     return this.conn.getURL();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String getUserName()
/*      */     throws SQLException
/*      */   {
/* 6281 */     if (this.conn.getUseHostsInPrivileges()) {
/* 6282 */       java.sql.Statement stmt = null;
/* 6283 */       ResultSet rs = null;
/*      */       try
/*      */       {
/* 6286 */         stmt = this.conn.getMetadataSafeStatement();
/*      */         
/* 6288 */         rs = stmt.executeQuery("SELECT USER()");
/* 6289 */         rs.next();
/*      */         
/* 6291 */         return rs.getString(1);
/*      */       } finally {
/* 6293 */         if (rs != null) {
/*      */           try {
/* 6295 */             rs.close();
/*      */           } catch (Exception ex) {
/* 6297 */             AssertionFailedException.shouldNotHappen(ex);
/*      */           }
/*      */           
/* 6300 */           rs = null;
/*      */         }
/*      */         
/* 6303 */         if (stmt != null) {
/*      */           try {
/* 6305 */             stmt.close();
/*      */           } catch (Exception ex) {
/* 6307 */             AssertionFailedException.shouldNotHappen(ex);
/*      */           }
/*      */           
/* 6310 */           stmt = null;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 6315 */     return this.conn.getUser();
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
/*      */   public ResultSet getVersionColumns(String catalog, String schema, final String table)
/*      */     throws SQLException
/*      */   {
/* 6352 */     if (table == null) {
/* 6353 */       throw SQLError.createSQLException("Table not specified.", "S1009", getExceptionInterceptor());
/*      */     }
/*      */     
/* 6356 */     Field[] fields = new Field[8];
/* 6357 */     fields[0] = new Field("", "SCOPE", 5, 5);
/* 6358 */     fields[1] = new Field("", "COLUMN_NAME", 1, 32);
/* 6359 */     fields[2] = new Field("", "DATA_TYPE", 4, 5);
/* 6360 */     fields[3] = new Field("", "TYPE_NAME", 1, 16);
/* 6361 */     fields[4] = new Field("", "COLUMN_SIZE", 4, 16);
/* 6362 */     fields[5] = new Field("", "BUFFER_LENGTH", 4, 16);
/* 6363 */     fields[6] = new Field("", "DECIMAL_DIGITS", 5, 16);
/* 6364 */     fields[7] = new Field("", "PSEUDO_COLUMN", 5, 5);
/*      */     
/* 6366 */     final ArrayList<ResultSetRow> rows = new ArrayList();
/*      */     
/* 6368 */     final java.sql.Statement stmt = this.conn.getMetadataSafeStatement();
/*      */     
/*      */     try
/*      */     {
/* 6372 */       new IterateBlock(getCatalogIterator(catalog))
/*      */       {
/*      */         void forEach(String catalogStr) throws SQLException
/*      */         {
/* 6376 */           ResultSet results = null;
/* 6377 */           boolean with_where = DatabaseMetaData.this.conn.versionMeetsMinimum(5, 0, 0);
/*      */           try
/*      */           {
/* 6380 */             StringBuilder whereBuf = new StringBuilder(" Extra LIKE '%on update CURRENT_TIMESTAMP%'");
/* 6381 */             List<String> rsFields = new ArrayList();
/*      */             
/*      */ 
/*      */ 
/* 6385 */             if (!DatabaseMetaData.this.conn.versionMeetsMinimum(5, 1, 23))
/*      */             {
/* 6387 */               whereBuf = new StringBuilder();
/* 6388 */               boolean firstTime = true;
/*      */               
/* 6390 */               String query = "SHOW CREATE TABLE " + DatabaseMetaData.this.getFullyQualifiedName(catalogStr, table);
/*      */               
/* 6392 */               results = stmt.executeQuery(query);
/* 6393 */               while (results.next()) {
/* 6394 */                 String createTableString = results.getString(2);
/* 6395 */                 StringTokenizer lineTokenizer = new StringTokenizer(createTableString, "\n");
/*      */                 
/* 6397 */                 while (lineTokenizer.hasMoreTokens()) {
/* 6398 */                   String line = lineTokenizer.nextToken().trim();
/* 6399 */                   if (StringUtils.indexOfIgnoreCase(line, "on update CURRENT_TIMESTAMP") > -1) {
/* 6400 */                     boolean usingBackTicks = true;
/* 6401 */                     int beginPos = line.indexOf(DatabaseMetaData.this.quotedId);
/*      */                     
/* 6403 */                     if (beginPos == -1) {
/* 6404 */                       beginPos = line.indexOf("\"");
/* 6405 */                       usingBackTicks = false;
/*      */                     }
/*      */                     
/* 6408 */                     if (beginPos != -1) {
/* 6409 */                       int endPos = -1;
/*      */                       
/* 6411 */                       if (usingBackTicks) {
/* 6412 */                         endPos = line.indexOf(DatabaseMetaData.this.quotedId, beginPos + 1);
/*      */                       } else {
/* 6414 */                         endPos = line.indexOf("\"", beginPos + 1);
/*      */                       }
/*      */                       
/* 6417 */                       if (endPos != -1) {
/* 6418 */                         if (with_where) {
/* 6419 */                           if (!firstTime) {
/* 6420 */                             whereBuf.append(" or");
/*      */                           } else {
/* 6422 */                             firstTime = false;
/*      */                           }
/* 6424 */                           whereBuf.append(" Field='");
/* 6425 */                           whereBuf.append(line.substring(beginPos + 1, endPos));
/* 6426 */                           whereBuf.append("'");
/*      */                         } else {
/* 6428 */                           rsFields.add(line.substring(beginPos + 1, endPos));
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             
/* 6437 */             if ((whereBuf.length() > 0) || (rsFields.size() > 0)) {
/* 6438 */               StringBuilder queryBuf = new StringBuilder("SHOW COLUMNS FROM ");
/* 6439 */               queryBuf.append(StringUtils.quoteIdentifier(table, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()));
/* 6440 */               queryBuf.append(" FROM ");
/* 6441 */               queryBuf.append(StringUtils.quoteIdentifier(catalogStr, DatabaseMetaData.this.quotedId, DatabaseMetaData.this.conn.getPedantic()));
/* 6442 */               if (with_where) {
/* 6443 */                 queryBuf.append(" WHERE");
/* 6444 */                 queryBuf.append(whereBuf.toString());
/*      */               }
/*      */               
/* 6447 */               results = stmt.executeQuery(queryBuf.toString());
/*      */               
/* 6449 */               while (results.next()) {
/* 6450 */                 if ((with_where) || (rsFields.contains(results.getString("Field")))) {
/* 6451 */                   DatabaseMetaData.TypeDescriptor typeDesc = new DatabaseMetaData.TypeDescriptor(DatabaseMetaData.this, results.getString("Type"), results.getString("Null"));
/* 6452 */                   byte[][] rowVal = new byte[8][];
/*      */                   
/* 6454 */                   rowVal[0] = null;
/*      */                   
/* 6456 */                   rowVal[1] = results.getBytes("Field");
/*      */                   
/* 6458 */                   rowVal[2] = Short.toString(typeDesc.dataType).getBytes();
/*      */                   
/* 6460 */                   rowVal[3] = DatabaseMetaData.this.s2b(typeDesc.typeName);
/*      */                   
/* 6462 */                   rowVal[4] = (typeDesc.columnSize == null ? null : DatabaseMetaData.this.s2b(typeDesc.columnSize.toString()));
/*      */                   
/* 6464 */                   rowVal[5] = DatabaseMetaData.this.s2b(Integer.toString(typeDesc.bufferLength));
/*      */                   
/* 6466 */                   rowVal[6] = (typeDesc.decimalDigits == null ? null : DatabaseMetaData.this.s2b(typeDesc.decimalDigits.toString()));
/*      */                   
/* 6468 */                   rowVal[7] = Integer.toString(1).getBytes();
/*      */                   
/* 6470 */                   rows.add(new ByteArrayRow(rowVal, DatabaseMetaData.this.getExceptionInterceptor()));
/*      */                 }
/*      */               }
/*      */             }
/*      */           } catch (SQLException sqlEx) {
/* 6475 */             if (!"42S02".equals(sqlEx.getSQLState())) {
/* 6476 */               throw sqlEx;
/*      */             }
/*      */           } finally {
/* 6479 */             if (results != null) {
/*      */               try {
/* 6481 */                 results.close();
/*      */               }
/*      */               catch (Exception ex) {}
/*      */               
/* 6485 */               results = null;
/*      */             }
/*      */           }
/*      */         }
/*      */       }.doForAll();
/*      */     }
/*      */     finally {
/* 6492 */       if (stmt != null) {
/* 6493 */         stmt.close();
/*      */       }
/*      */     }
/*      */     
/* 6497 */     return buildResultSet(fields, rows);
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
/*      */   public boolean insertsAreDetected(int type)
/*      */     throws SQLException
/*      */   {
/* 6511 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isCatalogAtStart()
/*      */     throws SQLException
/*      */   {
/* 6522 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isReadOnly()
/*      */     throws SQLException
/*      */   {
/* 6532 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean locatorsUpdateCopy()
/*      */     throws SQLException
/*      */   {
/* 6539 */     return !this.conn.getEmulateLocators();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean nullPlusNonNullIsNull()
/*      */     throws SQLException
/*      */   {
/* 6550 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean nullsAreSortedAtEnd()
/*      */     throws SQLException
/*      */   {
/* 6560 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean nullsAreSortedAtStart()
/*      */     throws SQLException
/*      */   {
/* 6570 */     return (this.conn.versionMeetsMinimum(4, 0, 2)) && (!this.conn.versionMeetsMinimum(4, 0, 11));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean nullsAreSortedHigh()
/*      */     throws SQLException
/*      */   {
/* 6580 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean nullsAreSortedLow()
/*      */     throws SQLException
/*      */   {
/* 6590 */     return !nullsAreSortedHigh();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean othersDeletesAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 6598 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean othersInsertsAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 6606 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean othersUpdatesAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 6619 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean ownDeletesAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 6627 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean ownInsertsAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 6635 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean ownUpdatesAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 6648 */     return false;
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
/*      */   protected LocalAndReferencedColumns parseTableStatusIntoLocalAndReferencedColumns(String keysComment)
/*      */     throws SQLException
/*      */   {
/* 6663 */     String columnsDelimitter = ",";
/*      */     
/* 6665 */     int indexOfOpenParenLocalColumns = StringUtils.indexOfIgnoreCase(0, keysComment, "(", this.quotedId, this.quotedId, StringUtils.SEARCH_MODE__ALL);
/*      */     
/* 6667 */     if (indexOfOpenParenLocalColumns == -1) {
/* 6668 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find start of local columns list.", "S1000", getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/* 6672 */     String constraintName = StringUtils.unQuoteIdentifier(keysComment.substring(0, indexOfOpenParenLocalColumns).trim(), this.quotedId);
/* 6673 */     keysComment = keysComment.substring(indexOfOpenParenLocalColumns, keysComment.length());
/*      */     
/* 6675 */     String keysCommentTrimmed = keysComment.trim();
/*      */     
/* 6677 */     int indexOfCloseParenLocalColumns = StringUtils.indexOfIgnoreCase(0, keysCommentTrimmed, ")", this.quotedId, this.quotedId, StringUtils.SEARCH_MODE__ALL);
/*      */     
/*      */ 
/* 6680 */     if (indexOfCloseParenLocalColumns == -1) {
/* 6681 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find end of local columns list.", "S1000", getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/* 6685 */     String localColumnNamesString = keysCommentTrimmed.substring(1, indexOfCloseParenLocalColumns);
/*      */     
/* 6687 */     int indexOfRefer = StringUtils.indexOfIgnoreCase(0, keysCommentTrimmed, "REFER ", this.quotedId, this.quotedId, StringUtils.SEARCH_MODE__ALL);
/*      */     
/* 6689 */     if (indexOfRefer == -1) {
/* 6690 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find start of referenced tables list.", "S1000", getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/* 6694 */     int indexOfOpenParenReferCol = StringUtils.indexOfIgnoreCase(indexOfRefer, keysCommentTrimmed, "(", this.quotedId, this.quotedId, StringUtils.SEARCH_MODE__MRK_COM_WS);
/*      */     
/*      */ 
/* 6697 */     if (indexOfOpenParenReferCol == -1) {
/* 6698 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find start of referenced columns list.", "S1000", getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/* 6702 */     String referCatalogTableString = keysCommentTrimmed.substring(indexOfRefer + "REFER ".length(), indexOfOpenParenReferCol);
/*      */     
/* 6704 */     int indexOfSlash = StringUtils.indexOfIgnoreCase(0, referCatalogTableString, "/", this.quotedId, this.quotedId, StringUtils.SEARCH_MODE__MRK_COM_WS);
/*      */     
/* 6706 */     if (indexOfSlash == -1) {
/* 6707 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find name of referenced catalog.", "S1000", getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/* 6711 */     String referCatalog = StringUtils.unQuoteIdentifier(referCatalogTableString.substring(0, indexOfSlash), this.quotedId);
/* 6712 */     String referTable = StringUtils.unQuoteIdentifier(referCatalogTableString.substring(indexOfSlash + 1).trim(), this.quotedId);
/*      */     
/* 6714 */     int indexOfCloseParenRefer = StringUtils.indexOfIgnoreCase(indexOfOpenParenReferCol, keysCommentTrimmed, ")", this.quotedId, this.quotedId, StringUtils.SEARCH_MODE__ALL);
/*      */     
/*      */ 
/* 6717 */     if (indexOfCloseParenRefer == -1) {
/* 6718 */       throw SQLError.createSQLException("Error parsing foreign keys definition, couldn't find end of referenced columns list.", "S1000", getExceptionInterceptor());
/*      */     }
/*      */     
/*      */ 
/* 6722 */     String referColumnNamesString = keysCommentTrimmed.substring(indexOfOpenParenReferCol + 1, indexOfCloseParenRefer);
/*      */     
/* 6724 */     List<String> referColumnsList = StringUtils.split(referColumnNamesString, columnsDelimitter, this.quotedId, this.quotedId, false);
/* 6725 */     List<String> localColumnsList = StringUtils.split(localColumnNamesString, columnsDelimitter, this.quotedId, this.quotedId, false);
/*      */     
/* 6727 */     return new LocalAndReferencedColumns(localColumnsList, referColumnsList, constraintName, referCatalog, referTable);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected byte[] s2b(String s)
/*      */     throws SQLException
/*      */   {
/* 6737 */     if (s == null) {
/* 6738 */       return null;
/*      */     }
/*      */     
/* 6741 */     return StringUtils.getBytes(s, this.conn.getCharacterSetMetadata(), this.conn.getServerCharset(), this.conn.parserKnowsUnicode(), this.conn, getExceptionInterceptor());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean storesLowerCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 6753 */     return this.conn.storesLowerCaseTableName();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean storesLowerCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 6764 */     return this.conn.storesLowerCaseTableName();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean storesMixedCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 6775 */     return !this.conn.storesLowerCaseTableName();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean storesMixedCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 6786 */     return !this.conn.storesLowerCaseTableName();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean storesUpperCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 6797 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean storesUpperCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 6808 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsAlterTableWithAddColumn()
/*      */     throws SQLException
/*      */   {
/* 6818 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsAlterTableWithDropColumn()
/*      */     throws SQLException
/*      */   {
/* 6828 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsANSI92EntryLevelSQL()
/*      */     throws SQLException
/*      */   {
/* 6839 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsANSI92FullSQL()
/*      */     throws SQLException
/*      */   {
/* 6849 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsANSI92IntermediateSQL()
/*      */     throws SQLException
/*      */   {
/* 6859 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsBatchUpdates()
/*      */     throws SQLException
/*      */   {
/* 6869 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsCatalogsInDataManipulation()
/*      */     throws SQLException
/*      */   {
/* 6880 */     return this.conn.versionMeetsMinimum(3, 22, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsCatalogsInIndexDefinitions()
/*      */     throws SQLException
/*      */   {
/* 6891 */     return this.conn.versionMeetsMinimum(3, 22, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsCatalogsInPrivilegeDefinitions()
/*      */     throws SQLException
/*      */   {
/* 6902 */     return this.conn.versionMeetsMinimum(3, 22, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsCatalogsInProcedureCalls()
/*      */     throws SQLException
/*      */   {
/* 6913 */     return this.conn.versionMeetsMinimum(3, 22, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsCatalogsInTableDefinitions()
/*      */     throws SQLException
/*      */   {
/* 6924 */     return this.conn.versionMeetsMinimum(3, 22, 0);
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
/*      */   public boolean supportsColumnAliasing()
/*      */     throws SQLException
/*      */   {
/* 6938 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsConvert()
/*      */     throws SQLException
/*      */   {
/* 6948 */     return false;
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
/*      */   public boolean supportsConvert(int fromType, int toType)
/*      */     throws SQLException
/*      */   {
/* 6964 */     switch (fromType)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */     case -4: 
/*      */     case -3: 
/*      */     case -2: 
/*      */     case -1: 
/*      */     case 1: 
/*      */     case 12: 
/* 6975 */       switch (toType) {
/*      */       case -6: 
/*      */       case -5: 
/*      */       case -4: 
/*      */       case -3: 
/*      */       case -2: 
/*      */       case -1: 
/*      */       case 1: 
/*      */       case 2: 
/*      */       case 3: 
/*      */       case 4: 
/*      */       case 5: 
/*      */       case 6: 
/*      */       case 7: 
/*      */       case 8: 
/*      */       case 12: 
/*      */       case 91: 
/*      */       case 92: 
/*      */       case 93: 
/*      */       case 1111: 
/* 6995 */         return true;
/*      */       }
/*      */       
/* 6998 */       return false;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     case -7: 
/* 7005 */       return false;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     case -6: 
/*      */     case -5: 
/*      */     case 2: 
/*      */     case 3: 
/*      */     case 4: 
/*      */     case 5: 
/*      */     case 6: 
/*      */     case 7: 
/*      */     case 8: 
/* 7020 */       switch (toType) {
/*      */       case -6: 
/*      */       case -5: 
/*      */       case -4: 
/*      */       case -3: 
/*      */       case -2: 
/*      */       case -1: 
/*      */       case 1: 
/*      */       case 2: 
/*      */       case 3: 
/*      */       case 4: 
/*      */       case 5: 
/*      */       case 6: 
/*      */       case 7: 
/*      */       case 8: 
/*      */       case 12: 
/* 7036 */         return true;
/*      */       }
/*      */       
/* 7039 */       return false;
/*      */     
/*      */ 
/*      */ 
/*      */     case 0: 
/* 7044 */       return false;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     case 1111: 
/* 7051 */       switch (toType) {
/*      */       case -4: 
/*      */       case -3: 
/*      */       case -2: 
/*      */       case -1: 
/*      */       case 1: 
/*      */       case 12: 
/* 7058 */         return true;
/*      */       }
/*      */       
/* 7061 */       return false;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     case 91: 
/* 7067 */       switch (toType) {
/*      */       case -4: 
/*      */       case -3: 
/*      */       case -2: 
/*      */       case -1: 
/*      */       case 1: 
/*      */       case 12: 
/* 7074 */         return true;
/*      */       }
/*      */       
/* 7077 */       return false;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     case 92: 
/* 7083 */       switch (toType) {
/*      */       case -4: 
/*      */       case -3: 
/*      */       case -2: 
/*      */       case -1: 
/*      */       case 1: 
/*      */       case 12: 
/* 7090 */         return true;
/*      */       }
/*      */       
/* 7093 */       return false;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     case 93: 
/* 7101 */       switch (toType) {
/*      */       case -4: 
/*      */       case -3: 
/*      */       case -2: 
/*      */       case -1: 
/*      */       case 1: 
/*      */       case 12: 
/*      */       case 91: 
/*      */       case 92: 
/* 7110 */         return true;
/*      */       }
/*      */       
/* 7113 */       return false;
/*      */     }
/*      */     
/*      */     
/*      */ 
/* 7118 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsCoreSQLGrammar()
/*      */     throws SQLException
/*      */   {
/* 7129 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsCorrelatedSubqueries()
/*      */     throws SQLException
/*      */   {
/* 7140 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsDataDefinitionAndDataManipulationTransactions()
/*      */     throws SQLException
/*      */   {
/* 7151 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsDataManipulationTransactionsOnly()
/*      */     throws SQLException
/*      */   {
/* 7161 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsDifferentTableCorrelationNames()
/*      */     throws SQLException
/*      */   {
/* 7173 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsExpressionsInOrderBy()
/*      */     throws SQLException
/*      */   {
/* 7183 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsExtendedSQLGrammar()
/*      */     throws SQLException
/*      */   {
/* 7193 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsFullOuterJoins()
/*      */     throws SQLException
/*      */   {
/* 7203 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsGetGeneratedKeys()
/*      */   {
/* 7210 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsGroupBy()
/*      */     throws SQLException
/*      */   {
/* 7220 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsGroupByBeyondSelect()
/*      */     throws SQLException
/*      */   {
/* 7231 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsGroupByUnrelated()
/*      */     throws SQLException
/*      */   {
/* 7241 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsIntegrityEnhancementFacility()
/*      */     throws SQLException
/*      */   {
/* 7251 */     if (!this.conn.getOverrideSupportsIntegrityEnhancementFacility()) {
/* 7252 */       return false;
/*      */     }
/*      */     
/* 7255 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsLikeEscapeClause()
/*      */     throws SQLException
/*      */   {
/* 7266 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsLimitedOuterJoins()
/*      */     throws SQLException
/*      */   {
/* 7277 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsMinimumSQLGrammar()
/*      */     throws SQLException
/*      */   {
/* 7288 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsMixedCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 7298 */     return !this.conn.lowerCaseTableNames();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsMixedCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 7309 */     return !this.conn.lowerCaseTableNames();
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsMultipleOpenResults()
/*      */     throws SQLException
/*      */   {
/* 7316 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsMultipleResultSets()
/*      */     throws SQLException
/*      */   {
/* 7326 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsMultipleTransactions()
/*      */     throws SQLException
/*      */   {
/* 7337 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsNamedParameters()
/*      */     throws SQLException
/*      */   {
/* 7344 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsNonNullableColumns()
/*      */     throws SQLException
/*      */   {
/* 7355 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsOpenCursorsAcrossCommit()
/*      */     throws SQLException
/*      */   {
/* 7367 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsOpenCursorsAcrossRollback()
/*      */     throws SQLException
/*      */   {
/* 7379 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsOpenStatementsAcrossCommit()
/*      */     throws SQLException
/*      */   {
/* 7391 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsOpenStatementsAcrossRollback()
/*      */     throws SQLException
/*      */   {
/* 7403 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsOrderByUnrelated()
/*      */     throws SQLException
/*      */   {
/* 7413 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsOuterJoins()
/*      */     throws SQLException
/*      */   {
/* 7423 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsPositionedDelete()
/*      */     throws SQLException
/*      */   {
/* 7433 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsPositionedUpdate()
/*      */     throws SQLException
/*      */   {
/* 7443 */     return false;
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
/*      */   public boolean supportsResultSetConcurrency(int type, int concurrency)
/*      */     throws SQLException
/*      */   {
/* 7460 */     switch (type) {
/*      */     case 1004: 
/* 7462 */       if ((concurrency == 1007) || (concurrency == 1008)) {
/* 7463 */         return true;
/*      */       }
/* 7465 */       throw SQLError.createSQLException("Illegal arguments to supportsResultSetConcurrency()", "S1009", getExceptionInterceptor());
/*      */     
/*      */ 
/*      */     case 1003: 
/* 7469 */       if ((concurrency == 1007) || (concurrency == 1008)) {
/* 7470 */         return true;
/*      */       }
/* 7472 */       throw SQLError.createSQLException("Illegal arguments to supportsResultSetConcurrency()", "S1009", getExceptionInterceptor());
/*      */     
/*      */ 
/*      */     case 1005: 
/* 7476 */       return false;
/*      */     }
/* 7478 */     throw SQLError.createSQLException("Illegal arguments to supportsResultSetConcurrency()", "S1009", getExceptionInterceptor());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsResultSetHoldability(int holdability)
/*      */     throws SQLException
/*      */   {
/* 7488 */     return holdability == 1;
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
/*      */   public boolean supportsResultSetType(int type)
/*      */     throws SQLException
/*      */   {
/* 7502 */     return type == 1004;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean supportsSavepoints()
/*      */     throws SQLException
/*      */   {
/* 7510 */     return (this.conn.versionMeetsMinimum(4, 0, 14)) || (this.conn.versionMeetsMinimum(4, 1, 1));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsSchemasInDataManipulation()
/*      */     throws SQLException
/*      */   {
/* 7520 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsSchemasInIndexDefinitions()
/*      */     throws SQLException
/*      */   {
/* 7530 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsSchemasInPrivilegeDefinitions()
/*      */     throws SQLException
/*      */   {
/* 7540 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsSchemasInProcedureCalls()
/*      */     throws SQLException
/*      */   {
/* 7550 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsSchemasInTableDefinitions()
/*      */     throws SQLException
/*      */   {
/* 7560 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsSelectForUpdate()
/*      */     throws SQLException
/*      */   {
/* 7570 */     return this.conn.versionMeetsMinimum(4, 0, 0);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean supportsStatementPooling()
/*      */     throws SQLException
/*      */   {
/* 7577 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsStoredProcedures()
/*      */     throws SQLException
/*      */   {
/* 7588 */     return this.conn.versionMeetsMinimum(5, 0, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsSubqueriesInComparisons()
/*      */     throws SQLException
/*      */   {
/* 7599 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsSubqueriesInExists()
/*      */     throws SQLException
/*      */   {
/* 7610 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsSubqueriesInIns()
/*      */     throws SQLException
/*      */   {
/* 7621 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsSubqueriesInQuantifieds()
/*      */     throws SQLException
/*      */   {
/* 7632 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsTableCorrelationNames()
/*      */     throws SQLException
/*      */   {
/* 7643 */     return true;
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
/*      */   public boolean supportsTransactionIsolationLevel(int level)
/*      */     throws SQLException
/*      */   {
/* 7657 */     if (this.conn.supportsIsolationLevel()) {
/* 7658 */       switch (level) {
/*      */       case 1: 
/*      */       case 2: 
/*      */       case 4: 
/*      */       case 8: 
/* 7663 */         return true;
/*      */       }
/*      */       
/* 7666 */       return false;
/*      */     }
/*      */     
/*      */ 
/* 7670 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsTransactions()
/*      */     throws SQLException
/*      */   {
/* 7681 */     return this.conn.supportsTransactions();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsUnion()
/*      */     throws SQLException
/*      */   {
/* 7691 */     return this.conn.versionMeetsMinimum(4, 0, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean supportsUnionAll()
/*      */     throws SQLException
/*      */   {
/* 7701 */     return this.conn.versionMeetsMinimum(4, 0, 0);
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
/*      */   public boolean updatesAreDetected(int type)
/*      */     throws SQLException
/*      */   {
/* 7715 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean usesLocalFilePerTable()
/*      */     throws SQLException
/*      */   {
/* 7725 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean usesLocalFiles()
/*      */     throws SQLException
/*      */   {
/* 7735 */     return false;
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
/*      */   public ResultSet getClientInfoProperties()
/*      */     throws SQLException
/*      */   {
/* 7767 */     Field[] fields = new Field[4];
/* 7768 */     fields[0] = new Field("", "NAME", 12, 255);
/* 7769 */     fields[1] = new Field("", "MAX_LEN", 4, 10);
/* 7770 */     fields[2] = new Field("", "DEFAULT_VALUE", 12, 255);
/* 7771 */     fields[3] = new Field("", "DESCRIPTION", 12, 255);
/*      */     
/* 7773 */     return buildResultSet(fields, new ArrayList(), this.conn);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 7784 */     Field[] fields = createFunctionColumnsFields();
/*      */     
/* 7786 */     return getProcedureOrFunctionColumns(fields, catalog, schemaPattern, functionNamePattern, columnNamePattern, false, true);
/*      */   }
/*      */   
/*      */   protected Field[] createFunctionColumnsFields() {
/* 7790 */     Field[] fields = { new Field("", "FUNCTION_CAT", 12, 512), new Field("", "FUNCTION_SCHEM", 12, 512), new Field("", "FUNCTION_NAME", 12, 512), new Field("", "COLUMN_NAME", 12, 512), new Field("", "COLUMN_TYPE", 12, 64), new Field("", "DATA_TYPE", 5, 6), new Field("", "TYPE_NAME", 12, 64), new Field("", "PRECISION", 4, 12), new Field("", "LENGTH", 4, 12), new Field("", "SCALE", 5, 12), new Field("", "RADIX", 5, 6), new Field("", "NULLABLE", 5, 6), new Field("", "REMARKS", 12, 512), new Field("", "CHAR_OCTET_LENGTH", 4, 32), new Field("", "ORDINAL_POSITION", 4, 32), new Field("", "IS_NULLABLE", 12, 12), new Field("", "SPECIFIC_NAME", 12, 64) };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 7797 */     return fields;
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
/*      */   public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern)
/*      */     throws SQLException
/*      */   {
/* 7845 */     Field[] fields = new Field[6];
/*      */     
/* 7847 */     fields[0] = new Field("", "FUNCTION_CAT", 1, 255);
/* 7848 */     fields[1] = new Field("", "FUNCTION_SCHEM", 1, 255);
/* 7849 */     fields[2] = new Field("", "FUNCTION_NAME", 1, 255);
/* 7850 */     fields[3] = new Field("", "REMARKS", 1, 255);
/* 7851 */     fields[4] = new Field("", "FUNCTION_TYPE", 5, 6);
/* 7852 */     fields[5] = new Field("", "SPECIFIC_NAME", 1, 255);
/*      */     
/* 7854 */     return getProceduresAndOrFunctions(fields, catalog, schemaPattern, functionNamePattern, false, true);
/*      */   }
/*      */   
/*      */   public boolean providesQueryObjectGenerator() throws SQLException {
/* 7858 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public ResultSet getSchemas(String catalog, String schemaPattern)
/*      */     throws SQLException
/*      */   {
/* 7867 */     Field[] fields = { new Field("", "TABLE_SCHEM", 12, 255), new Field("", "TABLE_CATALOG", 12, 255) };
/*      */     
/* 7869 */     return buildResultSet(fields, new ArrayList());
/*      */   }
/*      */   
/*      */   public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
/* 7873 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected PreparedStatement prepareMetaDataSafeStatement(String sql)
/*      */     throws SQLException
/*      */   {
/* 7884 */     PreparedStatement pStmt = this.conn.clientPrepareStatement(sql);
/*      */     
/* 7886 */     if (pStmt.getMaxRows() != 0) {
/* 7887 */       pStmt.setMaxRows(0);
/*      */     }
/*      */     
/* 7890 */     ((Statement)pStmt).setHoldResultsOpenOverClose(true);
/*      */     
/* 7892 */     return pStmt;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 7905 */     Field[] fields = { new Field("", "TABLE_CAT", 12, 512), new Field("", "TABLE_SCHEM", 12, 512), new Field("", "TABLE_NAME", 12, 512), new Field("", "COLUMN_NAME", 12, 512), new Field("", "DATA_TYPE", 4, 12), new Field("", "COLUMN_SIZE", 4, 12), new Field("", "DECIMAL_DIGITS", 4, 12), new Field("", "NUM_PREC_RADIX", 4, 12), new Field("", "COLUMN_USAGE", 12, 512), new Field("", "REMARKS", 12, 512), new Field("", "CHAR_OCTET_LENGTH", 4, 12), new Field("", "IS_NULLABLE", 12, 512) };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 7912 */     return buildResultSet(fields, new ArrayList());
/*      */   }
/*      */   
/*      */   public boolean generatedKeyAlwaysReturned() throws SQLException
/*      */   {
/* 7917 */     return true;
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\DatabaseMetaData.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */