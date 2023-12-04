/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.regex.PatternSyntaxException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Field
/*     */ {
/*     */   private static final int AUTO_INCREMENT_FLAG = 512;
/*     */   private static final int NO_CHARSET_INFO = -1;
/*     */   private byte[] buffer;
/*  42 */   private int collationIndex = 0;
/*     */   
/*  44 */   private String encoding = null;
/*     */   
/*     */   private int colDecimals;
/*     */   
/*     */   private short colFlag;
/*     */   
/*  50 */   private String collationName = null;
/*     */   
/*  52 */   private MySQLConnection connection = null;
/*     */   
/*  54 */   private String databaseName = null;
/*     */   
/*  56 */   private int databaseNameLength = -1;
/*     */   
/*     */ 
/*  59 */   private int databaseNameStart = -1;
/*     */   
/*  61 */   protected int defaultValueLength = -1;
/*     */   
/*     */ 
/*  64 */   protected int defaultValueStart = -1;
/*     */   
/*  66 */   private String fullName = null;
/*     */   
/*  68 */   private String fullOriginalName = null;
/*     */   
/*  70 */   private boolean isImplicitTempTable = false;
/*     */   
/*     */   private long length;
/*     */   
/*  74 */   private int mysqlType = -1;
/*     */   
/*     */   private String name;
/*     */   
/*     */   private int nameLength;
/*     */   
/*     */   private int nameStart;
/*     */   
/*  82 */   private String originalColumnName = null;
/*     */   
/*  84 */   private int originalColumnNameLength = -1;
/*     */   
/*     */ 
/*  87 */   private int originalColumnNameStart = -1;
/*     */   
/*  89 */   private String originalTableName = null;
/*     */   
/*  91 */   private int originalTableNameLength = -1;
/*     */   
/*     */ 
/*  94 */   private int originalTableNameStart = -1;
/*     */   
/*  96 */   private int precisionAdjustFactor = 0;
/*     */   
/*  98 */   private int sqlType = -1;
/*     */   
/*     */   private String tableName;
/*     */   
/*     */   private int tableNameLength;
/*     */   
/*     */   private int tableNameStart;
/*     */   
/* 106 */   private boolean useOldNameMetadata = false;
/*     */   
/*     */ 
/*     */   private boolean isSingleBit;
/*     */   
/*     */ 
/*     */   private int maxBytesPerChar;
/*     */   
/*     */   private final boolean valueNeedsQuoting;
/*     */   
/*     */ 
/*     */   Field(MySQLConnection conn, byte[] buffer, int databaseNameStart, int databaseNameLength, int tableNameStart, int tableNameLength, int originalTableNameStart, int originalTableNameLength, int nameStart, int nameLength, int originalColumnNameStart, int originalColumnNameLength, long length, int mysqlType, short colFlag, int colDecimals, int defaultValueStart, int defaultValueLength, int charsetIndex)
/*     */     throws SQLException
/*     */   {
/* 120 */     this.connection = conn;
/* 121 */     this.buffer = buffer;
/* 122 */     this.nameStart = nameStart;
/* 123 */     this.nameLength = nameLength;
/* 124 */     this.tableNameStart = tableNameStart;
/* 125 */     this.tableNameLength = tableNameLength;
/* 126 */     this.length = length;
/* 127 */     this.colFlag = colFlag;
/* 128 */     this.colDecimals = colDecimals;
/* 129 */     this.mysqlType = mysqlType;
/*     */     
/*     */ 
/* 132 */     this.databaseNameStart = databaseNameStart;
/* 133 */     this.databaseNameLength = databaseNameLength;
/*     */     
/* 135 */     this.originalTableNameStart = originalTableNameStart;
/* 136 */     this.originalTableNameLength = originalTableNameLength;
/*     */     
/* 138 */     this.originalColumnNameStart = originalColumnNameStart;
/* 139 */     this.originalColumnNameLength = originalColumnNameLength;
/*     */     
/* 141 */     this.defaultValueStart = defaultValueStart;
/* 142 */     this.defaultValueLength = defaultValueLength;
/*     */     
/*     */ 
/* 145 */     this.collationIndex = charsetIndex;
/*     */     
/*     */ 
/* 148 */     this.sqlType = MysqlDefs.mysqlToJavaType(this.mysqlType);
/*     */     
/* 150 */     checkForImplicitTemporaryTable();
/*     */     
/* 152 */     boolean isFromFunction = this.originalTableNameLength == 0;
/*     */     
/* 154 */     if (this.mysqlType == 252) {
/* 155 */       if ((this.connection.getBlobsAreStrings()) || ((this.connection.getFunctionsNeverReturnBlobs()) && (isFromFunction))) {
/* 156 */         this.sqlType = 12;
/* 157 */         this.mysqlType = 15;
/* 158 */       } else if ((this.collationIndex == 63) || (!this.connection.versionMeetsMinimum(4, 1, 0))) {
/* 159 */         if ((this.connection.getUseBlobToStoreUTF8OutsideBMP()) && (shouldSetupForUtf8StringInBlob())) {
/* 160 */           setupForUtf8StringInBlob();
/*     */         } else {
/* 162 */           setBlobTypeBasedOnLength();
/* 163 */           this.sqlType = MysqlDefs.mysqlToJavaType(this.mysqlType);
/*     */         }
/*     */       }
/*     */       else {
/* 167 */         this.mysqlType = 253;
/* 168 */         this.sqlType = -1;
/*     */       }
/*     */     }
/*     */     
/* 172 */     if ((this.sqlType == -6) && (this.length == 1L) && (this.connection.getTinyInt1isBit()))
/*     */     {
/* 174 */       if (conn.getTinyInt1isBit()) {
/* 175 */         if (conn.getTransformedBitIsBoolean()) {
/* 176 */           this.sqlType = 16;
/*     */         } else {
/* 178 */           this.sqlType = -7;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 184 */     if ((!isNativeNumericType()) && (!isNativeDateTimeType())) {
/* 185 */       this.encoding = this.connection.getEncodingForIndex(this.collationIndex);
/*     */       
/*     */ 
/*     */ 
/* 189 */       if ("UnicodeBig".equals(this.encoding)) {
/* 190 */         this.encoding = "UTF-16";
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 195 */       boolean isBinary = isBinary();
/*     */       
/* 197 */       if ((this.connection.versionMeetsMinimum(4, 1, 0)) && (this.mysqlType == 253) && (isBinary) && (this.collationIndex == 63))
/*     */       {
/* 199 */         if ((this.connection.getFunctionsNeverReturnBlobs()) && (isFromFunction)) {
/* 200 */           this.sqlType = 12;
/* 201 */           this.mysqlType = 15;
/* 202 */         } else if (isOpaqueBinary()) {
/* 203 */           this.sqlType = -3;
/*     */         }
/*     */       }
/*     */       
/* 207 */       if ((this.connection.versionMeetsMinimum(4, 1, 0)) && (this.mysqlType == 254) && (isBinary) && (this.collationIndex == 63))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 214 */         if ((isOpaqueBinary()) && (!this.connection.getBlobsAreStrings())) {
/* 215 */           this.sqlType = -2;
/*     */         }
/*     */       }
/*     */       
/* 219 */       if (this.mysqlType == 16) {
/* 220 */         this.isSingleBit = (this.length == 0L);
/*     */         
/* 222 */         if ((this.connection != null) && ((this.connection.versionMeetsMinimum(5, 0, 21)) || (this.connection.versionMeetsMinimum(5, 1, 10))) && (this.length == 1L))
/*     */         {
/* 224 */           this.isSingleBit = true;
/*     */         }
/*     */         
/* 227 */         if (this.isSingleBit) {
/* 228 */           this.sqlType = -7;
/*     */         } else {
/* 230 */           this.sqlType = -3;
/* 231 */           this.colFlag = ((short)(this.colFlag | 0x80));
/* 232 */           this.colFlag = ((short)(this.colFlag | 0x10));
/* 233 */           isBinary = true;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 240 */       if ((this.sqlType == -4) && (!isBinary)) {
/* 241 */         this.sqlType = -1;
/* 242 */       } else if ((this.sqlType == -3) && (!isBinary)) {
/* 243 */         this.sqlType = 12;
/*     */       }
/*     */     } else {
/* 246 */       this.encoding = "US-ASCII";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 252 */     if (!isUnsigned()) {
/* 253 */       switch (this.mysqlType) {
/*     */       case 0: 
/*     */       case 246: 
/* 256 */         this.precisionAdjustFactor = -1;
/*     */         
/* 258 */         break;
/*     */       case 4: 
/*     */       case 5: 
/* 261 */         this.precisionAdjustFactor = 1;
/*     */       
/*     */       }
/*     */       
/*     */     } else {
/* 266 */       switch (this.mysqlType) {
/*     */       case 4: 
/*     */       case 5: 
/* 269 */         this.precisionAdjustFactor = 1;
/*     */       }
/*     */       
/*     */     }
/*     */     
/* 274 */     this.valueNeedsQuoting = determineNeedsQuoting();
/*     */   }
/*     */   
/*     */   private boolean shouldSetupForUtf8StringInBlob() throws SQLException {
/* 278 */     String includePattern = this.connection.getUtf8OutsideBmpIncludedColumnNamePattern();
/* 279 */     String excludePattern = this.connection.getUtf8OutsideBmpExcludedColumnNamePattern();
/*     */     
/* 281 */     if ((excludePattern != null) && (!StringUtils.isEmptyOrWhitespaceOnly(excludePattern))) {
/*     */       try {
/* 283 */         if (getOriginalName().matches(excludePattern)) {
/* 284 */           if ((includePattern != null) && (!StringUtils.isEmptyOrWhitespaceOnly(includePattern))) {
/*     */             try {
/* 286 */               if (getOriginalName().matches(includePattern)) {
/* 287 */                 return true;
/*     */               }
/*     */             } catch (PatternSyntaxException pse) {
/* 290 */               SQLException sqlEx = SQLError.createSQLException("Illegal regex specified for \"utf8OutsideBmpIncludedColumnNamePattern\"", "S1009", this.connection.getExceptionInterceptor());
/*     */               
/*     */ 
/* 293 */               if (!this.connection.getParanoid()) {
/* 294 */                 sqlEx.initCause(pse);
/*     */               }
/*     */               
/* 297 */               throw sqlEx;
/*     */             }
/*     */           }
/*     */           
/* 301 */           return false;
/*     */         }
/*     */       } catch (PatternSyntaxException pse) {
/* 304 */         SQLException sqlEx = SQLError.createSQLException("Illegal regex specified for \"utf8OutsideBmpExcludedColumnNamePattern\"", "S1009", this.connection.getExceptionInterceptor());
/*     */         
/*     */ 
/* 307 */         if (!this.connection.getParanoid()) {
/* 308 */           sqlEx.initCause(pse);
/*     */         }
/*     */         
/* 311 */         throw sqlEx;
/*     */       }
/*     */     }
/*     */     
/* 315 */     return true;
/*     */   }
/*     */   
/*     */   private void setupForUtf8StringInBlob() {
/* 319 */     if ((this.length == 255L) || (this.length == 65535L)) {
/* 320 */       this.mysqlType = 15;
/* 321 */       this.sqlType = 12;
/*     */     } else {
/* 323 */       this.mysqlType = 253;
/* 324 */       this.sqlType = -1;
/*     */     }
/*     */     
/* 327 */     this.collationIndex = 33;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   Field(MySQLConnection conn, byte[] buffer, int nameStart, int nameLength, int tableNameStart, int tableNameLength, int length, int mysqlType, short colFlag, int colDecimals)
/*     */     throws SQLException
/*     */   {
/* 335 */     this(conn, buffer, -1, -1, tableNameStart, tableNameLength, -1, -1, nameStart, nameLength, -1, -1, length, mysqlType, colFlag, colDecimals, -1, -1, -1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   Field(String tableName, String columnName, int jdbcType, int length)
/*     */   {
/* 343 */     this.tableName = tableName;
/* 344 */     this.name = columnName;
/* 345 */     this.length = length;
/* 346 */     this.sqlType = jdbcType;
/* 347 */     this.colFlag = 0;
/* 348 */     this.colDecimals = 0;
/* 349 */     this.valueNeedsQuoting = determineNeedsQuoting();
/*     */   }
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
/*     */   Field(String tableName, String columnName, int charsetIndex, int jdbcType, int length)
/*     */   {
/* 369 */     this.tableName = tableName;
/* 370 */     this.name = columnName;
/* 371 */     this.length = length;
/* 372 */     this.sqlType = jdbcType;
/* 373 */     this.colFlag = 0;
/* 374 */     this.colDecimals = 0;
/* 375 */     this.collationIndex = charsetIndex;
/* 376 */     this.valueNeedsQuoting = determineNeedsQuoting();
/*     */     
/* 378 */     switch (this.sqlType) {
/*     */     case -3: 
/*     */     case -2: 
/* 381 */       this.colFlag = ((short)(this.colFlag | 0x80));
/* 382 */       this.colFlag = ((short)(this.colFlag | 0x10));
/*     */     }
/*     */   }
/*     */   
/*     */   private void checkForImplicitTemporaryTable()
/*     */   {
/* 388 */     this.isImplicitTempTable = ((this.tableNameLength > 5) && (this.buffer[this.tableNameStart] == 35) && (this.buffer[(this.tableNameStart + 1)] == 115) && (this.buffer[(this.tableNameStart + 2)] == 113) && (this.buffer[(this.tableNameStart + 3)] == 108) && (this.buffer[(this.tableNameStart + 4)] == 95));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getEncoding()
/*     */     throws SQLException
/*     */   {
/* 399 */     return this.encoding;
/*     */   }
/*     */   
/*     */   public void setEncoding(String javaEncodingName, Connection conn) throws SQLException {
/* 403 */     this.encoding = javaEncodingName;
/*     */     try {
/* 405 */       this.collationIndex = CharsetMapping.getCollationIndexForJavaEncoding(javaEncodingName, conn);
/*     */     } catch (RuntimeException ex) {
/* 407 */       SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 408 */       sqlEx.initCause(ex);
/* 409 */       throw sqlEx;
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized String getCollation() throws SQLException {
/* 414 */     if ((this.collationName == null) && 
/* 415 */       (this.connection != null) && 
/* 416 */       (this.connection.versionMeetsMinimum(4, 1, 0))) {
/* 417 */       if (this.connection.getUseDynamicCharsetInfo()) {
/* 418 */         DatabaseMetaData dbmd = this.connection.getMetaData();
/*     */         
/* 420 */         String quotedIdStr = dbmd.getIdentifierQuoteString();
/*     */         
/* 422 */         if (" ".equals(quotedIdStr)) {
/* 423 */           quotedIdStr = "";
/*     */         }
/*     */         
/* 426 */         String csCatalogName = getDatabaseName();
/* 427 */         String csTableName = getOriginalTableName();
/* 428 */         String csColumnName = getOriginalName();
/*     */         
/* 430 */         if ((csCatalogName != null) && (csCatalogName.length() != 0) && (csTableName != null) && (csTableName.length() != 0) && (csColumnName != null) && (csColumnName.length() != 0))
/*     */         {
/* 432 */           StringBuilder queryBuf = new StringBuilder(csCatalogName.length() + csTableName.length() + 28);
/* 433 */           queryBuf.append("SHOW FULL COLUMNS FROM ");
/* 434 */           queryBuf.append(quotedIdStr);
/* 435 */           queryBuf.append(csCatalogName);
/* 436 */           queryBuf.append(quotedIdStr);
/* 437 */           queryBuf.append(".");
/* 438 */           queryBuf.append(quotedIdStr);
/* 439 */           queryBuf.append(csTableName);
/* 440 */           queryBuf.append(quotedIdStr);
/*     */           
/* 442 */           Statement collationStmt = null;
/* 443 */           ResultSet collationRs = null;
/*     */           try
/*     */           {
/* 446 */             collationStmt = this.connection.createStatement();
/*     */             
/* 448 */             collationRs = collationStmt.executeQuery(queryBuf.toString());
/*     */             
/* 450 */             while (collationRs.next()) {
/* 451 */               if (csColumnName.equals(collationRs.getString("Field"))) {
/* 452 */                 this.collationName = collationRs.getString("Collation");
/*     */               }
/*     */             }
/*     */           }
/*     */           finally
/*     */           {
/* 458 */             if (collationRs != null) {
/* 459 */               collationRs.close();
/* 460 */               collationRs = null;
/*     */             }
/*     */             
/* 463 */             if (collationStmt != null) {
/* 464 */               collationStmt.close();
/* 465 */               collationStmt = null;
/*     */             }
/*     */           }
/*     */         }
/*     */       } else {
/*     */         try {
/* 471 */           this.collationName = CharsetMapping.COLLATION_INDEX_TO_COLLATION_NAME[this.collationIndex];
/*     */         } catch (RuntimeException ex) {
/* 473 */           SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 474 */           sqlEx.initCause(ex);
/* 475 */           throw sqlEx;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 482 */     return this.collationName;
/*     */   }
/*     */   
/*     */   public String getColumnLabel() throws SQLException {
/* 486 */     return getName();
/*     */   }
/*     */   
/*     */   public String getDatabaseName() throws SQLException {
/* 490 */     if ((this.databaseName == null) && (this.databaseNameStart != -1) && (this.databaseNameLength != -1)) {
/* 491 */       this.databaseName = getStringFromBytes(this.databaseNameStart, this.databaseNameLength);
/*     */     }
/*     */     
/* 494 */     return this.databaseName;
/*     */   }
/*     */   
/*     */   int getDecimals() {
/* 498 */     return this.colDecimals;
/*     */   }
/*     */   
/*     */   public String getFullName() throws SQLException {
/* 502 */     if (this.fullName == null) {
/* 503 */       StringBuilder fullNameBuf = new StringBuilder(getTableName().length() + 1 + getName().length());
/* 504 */       fullNameBuf.append(this.tableName);
/*     */       
/*     */ 
/* 507 */       fullNameBuf.append('.');
/* 508 */       fullNameBuf.append(this.name);
/* 509 */       this.fullName = fullNameBuf.toString();
/* 510 */       fullNameBuf = null;
/*     */     }
/*     */     
/* 513 */     return this.fullName;
/*     */   }
/*     */   
/*     */   public String getFullOriginalName() throws SQLException {
/* 517 */     getOriginalName();
/*     */     
/* 519 */     if (this.originalColumnName == null) {
/* 520 */       return null;
/*     */     }
/*     */     
/* 523 */     if (this.fullName == null) {
/* 524 */       StringBuilder fullOriginalNameBuf = new StringBuilder(getOriginalTableName().length() + 1 + getOriginalName().length());
/* 525 */       fullOriginalNameBuf.append(this.originalTableName);
/*     */       
/*     */ 
/* 528 */       fullOriginalNameBuf.append('.');
/* 529 */       fullOriginalNameBuf.append(this.originalColumnName);
/* 530 */       this.fullOriginalName = fullOriginalNameBuf.toString();
/* 531 */       fullOriginalNameBuf = null;
/*     */     }
/*     */     
/* 534 */     return this.fullOriginalName;
/*     */   }
/*     */   
/*     */   public long getLength() {
/* 538 */     return this.length;
/*     */   }
/*     */   
/*     */   public synchronized int getMaxBytesPerCharacter() throws SQLException {
/* 542 */     if (this.maxBytesPerChar == 0) {
/* 543 */       this.maxBytesPerChar = this.connection.getMaxBytesPerChar(Integer.valueOf(this.collationIndex), getEncoding());
/*     */     }
/* 545 */     return this.maxBytesPerChar;
/*     */   }
/*     */   
/*     */   public int getMysqlType() {
/* 549 */     return this.mysqlType;
/*     */   }
/*     */   
/*     */   public String getName() throws SQLException {
/* 553 */     if (this.name == null) {
/* 554 */       this.name = getStringFromBytes(this.nameStart, this.nameLength);
/*     */     }
/*     */     
/* 557 */     return this.name;
/*     */   }
/*     */   
/*     */   public String getNameNoAliases() throws SQLException {
/* 561 */     if (this.useOldNameMetadata) {
/* 562 */       return getName();
/*     */     }
/*     */     
/* 565 */     if ((this.connection != null) && (this.connection.versionMeetsMinimum(4, 1, 0))) {
/* 566 */       return getOriginalName();
/*     */     }
/*     */     
/* 569 */     return getName();
/*     */   }
/*     */   
/*     */   public String getOriginalName() throws SQLException {
/* 573 */     if ((this.originalColumnName == null) && (this.originalColumnNameStart != -1) && (this.originalColumnNameLength != -1)) {
/* 574 */       this.originalColumnName = getStringFromBytes(this.originalColumnNameStart, this.originalColumnNameLength);
/*     */     }
/*     */     
/* 577 */     return this.originalColumnName;
/*     */   }
/*     */   
/*     */   public String getOriginalTableName() throws SQLException {
/* 581 */     if ((this.originalTableName == null) && (this.originalTableNameStart != -1) && (this.originalTableNameLength != -1)) {
/* 582 */       this.originalTableName = getStringFromBytes(this.originalTableNameStart, this.originalTableNameLength);
/*     */     }
/*     */     
/* 585 */     return this.originalTableName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getPrecisionAdjustFactor()
/*     */   {
/* 597 */     return this.precisionAdjustFactor;
/*     */   }
/*     */   
/*     */   public int getSQLType() {
/* 601 */     return this.sqlType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private String getStringFromBytes(int stringStart, int stringLength)
/*     */     throws SQLException
/*     */   {
/* 609 */     if ((stringStart == -1) || (stringLength == -1)) {
/* 610 */       return null;
/*     */     }
/*     */     
/* 613 */     String stringVal = null;
/*     */     
/* 615 */     if (this.connection != null) {
/* 616 */       if (this.connection.getUseUnicode()) {
/* 617 */         String javaEncoding = this.connection.getCharacterSetMetadata();
/*     */         
/* 619 */         if (javaEncoding == null) {
/* 620 */           javaEncoding = this.connection.getEncoding();
/*     */         }
/*     */         
/* 623 */         if (javaEncoding != null) {
/* 624 */           SingleByteCharsetConverter converter = null;
/*     */           
/* 626 */           if (this.connection != null) {
/* 627 */             converter = this.connection.getCharsetConverter(javaEncoding);
/*     */           }
/*     */           
/* 630 */           if (converter != null) {
/* 631 */             stringVal = converter.toString(this.buffer, stringStart, stringLength);
/*     */           } else {
/*     */             try
/*     */             {
/* 635 */               stringVal = StringUtils.toString(this.buffer, stringStart, stringLength, javaEncoding);
/*     */             } catch (UnsupportedEncodingException ue) {
/* 637 */               throw new RuntimeException(Messages.getString("Field.12") + javaEncoding + Messages.getString("Field.13"));
/*     */             }
/*     */           }
/*     */         }
/*     */         else {
/* 642 */           stringVal = StringUtils.toAsciiString(this.buffer, stringStart, stringLength);
/*     */         }
/*     */       }
/*     */       else {
/* 646 */         stringVal = StringUtils.toAsciiString(this.buffer, stringStart, stringLength);
/*     */       }
/*     */     }
/*     */     else {
/* 650 */       stringVal = StringUtils.toAsciiString(this.buffer, stringStart, stringLength);
/*     */     }
/*     */     
/* 653 */     return stringVal;
/*     */   }
/*     */   
/*     */   public String getTable() throws SQLException {
/* 657 */     return getTableName();
/*     */   }
/*     */   
/*     */   public String getTableName() throws SQLException {
/* 661 */     if (this.tableName == null) {
/* 662 */       this.tableName = getStringFromBytes(this.tableNameStart, this.tableNameLength);
/*     */     }
/*     */     
/* 665 */     return this.tableName;
/*     */   }
/*     */   
/*     */   public String getTableNameNoAliases() throws SQLException {
/* 669 */     if (this.connection.versionMeetsMinimum(4, 1, 0)) {
/* 670 */       return getOriginalTableName();
/*     */     }
/*     */     
/* 673 */     return getTableName();
/*     */   }
/*     */   
/*     */   public boolean isAutoIncrement() {
/* 677 */     return (this.colFlag & 0x200) > 0;
/*     */   }
/*     */   
/*     */   public boolean isBinary() {
/* 681 */     return (this.colFlag & 0x80) > 0;
/*     */   }
/*     */   
/*     */   public boolean isBlob() {
/* 685 */     return (this.colFlag & 0x10) > 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean isImplicitTemporaryTable()
/*     */   {
/* 692 */     return this.isImplicitTempTable;
/*     */   }
/*     */   
/*     */   public boolean isMultipleKey() {
/* 696 */     return (this.colFlag & 0x8) > 0;
/*     */   }
/*     */   
/*     */   boolean isNotNull() {
/* 700 */     return (this.colFlag & 0x1) > 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   boolean isOpaqueBinary()
/*     */     throws SQLException
/*     */   {
/* 709 */     if ((this.collationIndex == 63) && (isBinary()) && ((getMysqlType() == 254) || (getMysqlType() == 253)))
/*     */     {
/*     */ 
/* 712 */       if ((this.originalTableNameLength == 0) && (this.connection != null) && (!this.connection.versionMeetsMinimum(5, 0, 25))) {
/* 713 */         return false;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 718 */       return !isImplicitTemporaryTable();
/*     */     }
/*     */     
/* 721 */     return (this.connection.versionMeetsMinimum(4, 1, 0)) && ("binary".equalsIgnoreCase(getEncoding()));
/*     */   }
/*     */   
/*     */   public boolean isPrimaryKey()
/*     */   {
/* 726 */     return (this.colFlag & 0x2) > 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   boolean isReadOnly()
/*     */     throws SQLException
/*     */   {
/* 736 */     if (this.connection.versionMeetsMinimum(4, 1, 0)) {
/* 737 */       String orgColumnName = getOriginalName();
/* 738 */       String orgTableName = getOriginalTableName();
/*     */       
/* 740 */       return (orgColumnName == null) || (orgColumnName.length() <= 0) || (orgTableName == null) || (orgTableName.length() <= 0);
/*     */     }
/*     */     
/* 743 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isUniqueKey() {
/* 747 */     return (this.colFlag & 0x4) > 0;
/*     */   }
/*     */   
/*     */   public boolean isUnsigned() {
/* 751 */     return (this.colFlag & 0x20) > 0;
/*     */   }
/*     */   
/*     */   public void setUnsigned() {
/* 755 */     this.colFlag = ((short)(this.colFlag | 0x20));
/*     */   }
/*     */   
/*     */   public boolean isZeroFill() {
/* 759 */     return (this.colFlag & 0x40) > 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setBlobTypeBasedOnLength()
/*     */   {
/* 767 */     if (this.length == 255L) {
/* 768 */       this.mysqlType = 249;
/* 769 */     } else if (this.length == 65535L) {
/* 770 */       this.mysqlType = 252;
/* 771 */     } else if (this.length == 16777215L) {
/* 772 */       this.mysqlType = 250;
/* 773 */     } else if (this.length == 4294967295L) {
/* 774 */       this.mysqlType = 251;
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean isNativeNumericType() {
/* 779 */     return ((this.mysqlType >= 1) && (this.mysqlType <= 5)) || (this.mysqlType == 8) || (this.mysqlType == 13);
/*     */   }
/*     */   
/*     */   private boolean isNativeDateTimeType()
/*     */   {
/* 784 */     return (this.mysqlType == 10) || (this.mysqlType == 14) || (this.mysqlType == 12) || (this.mysqlType == 11) || (this.mysqlType == 7);
/*     */   }
/*     */   
/*     */   public void setConnection(MySQLConnection conn)
/*     */   {
/* 789 */     this.connection = conn;
/*     */     
/* 791 */     if ((this.encoding == null) || (this.collationIndex == 0)) {
/* 792 */       this.encoding = this.connection.getEncoding();
/*     */     }
/*     */   }
/*     */   
/*     */   void setMysqlType(int type) {
/* 797 */     this.mysqlType = type;
/* 798 */     this.sqlType = MysqlDefs.mysqlToJavaType(this.mysqlType);
/*     */   }
/*     */   
/*     */   protected void setUseOldNameMetadata(boolean useOldNameMetadata) {
/* 802 */     this.useOldNameMetadata = useOldNameMetadata;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/*     */     try {
/* 808 */       StringBuilder asString = new StringBuilder();
/* 809 */       asString.append(super.toString());
/* 810 */       asString.append("[");
/* 811 */       asString.append("catalog=");
/* 812 */       asString.append(getDatabaseName());
/* 813 */       asString.append(",tableName=");
/* 814 */       asString.append(getTableName());
/* 815 */       asString.append(",originalTableName=");
/* 816 */       asString.append(getOriginalTableName());
/* 817 */       asString.append(",columnName=");
/* 818 */       asString.append(getName());
/* 819 */       asString.append(",originalColumnName=");
/* 820 */       asString.append(getOriginalName());
/* 821 */       asString.append(",mysqlType=");
/* 822 */       asString.append(getMysqlType());
/* 823 */       asString.append("(");
/* 824 */       asString.append(MysqlDefs.typeToName(getMysqlType()));
/* 825 */       asString.append(")");
/* 826 */       asString.append(",flags=");
/*     */       
/* 828 */       if (isAutoIncrement()) {
/* 829 */         asString.append(" AUTO_INCREMENT");
/*     */       }
/*     */       
/* 832 */       if (isPrimaryKey()) {
/* 833 */         asString.append(" PRIMARY_KEY");
/*     */       }
/*     */       
/* 836 */       if (isUniqueKey()) {
/* 837 */         asString.append(" UNIQUE_KEY");
/*     */       }
/*     */       
/* 840 */       if (isBinary()) {
/* 841 */         asString.append(" BINARY");
/*     */       }
/*     */       
/* 844 */       if (isBlob()) {
/* 845 */         asString.append(" BLOB");
/*     */       }
/*     */       
/* 848 */       if (isMultipleKey()) {
/* 849 */         asString.append(" MULTI_KEY");
/*     */       }
/*     */       
/* 852 */       if (isUnsigned()) {
/* 853 */         asString.append(" UNSIGNED");
/*     */       }
/*     */       
/* 856 */       if (isZeroFill()) {
/* 857 */         asString.append(" ZEROFILL");
/*     */       }
/*     */       
/* 860 */       asString.append(", charsetIndex=");
/* 861 */       asString.append(this.collationIndex);
/* 862 */       asString.append(", charsetName=");
/* 863 */       asString.append(this.encoding);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 871 */       asString.append("]");
/*     */       
/* 873 */       return asString.toString();
/*     */     } catch (Throwable t) {}
/* 875 */     return super.toString();
/*     */   }
/*     */   
/*     */   protected boolean isSingleBit()
/*     */   {
/* 880 */     return this.isSingleBit;
/*     */   }
/*     */   
/*     */   protected boolean getvalueNeedsQuoting() {
/* 884 */     return this.valueNeedsQuoting;
/*     */   }
/*     */   
/*     */   private boolean determineNeedsQuoting() {
/* 888 */     boolean retVal = false;
/*     */     
/* 890 */     switch (this.sqlType) {
/*     */     case -7: 
/*     */     case -6: 
/*     */     case -5: 
/*     */     case 2: 
/*     */     case 3: 
/*     */     case 4: 
/*     */     case 5: 
/*     */     case 6: 
/*     */     case 7: 
/*     */     case 8: 
/* 901 */       retVal = false;
/* 902 */       break;
/*     */     case -4: case -3: case -2: case -1: case 0: case 1: default: 
/* 904 */       retVal = true;
/*     */     }
/* 906 */     return retVal;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\Field.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */