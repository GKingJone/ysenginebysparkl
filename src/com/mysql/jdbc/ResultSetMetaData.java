/*     */ package com.mysql.jdbc;
/*     */ 
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
/*     */ public class ResultSetMetaData
/*     */   implements java.sql.ResultSetMetaData
/*     */ {
/*     */   Field[] fields;
/*     */   
/*     */   private static int clampedGetLength(Field f)
/*     */   {
/*  34 */     long fieldLength = f.getLength();
/*     */     
/*  36 */     if (fieldLength > 2147483647L) {
/*  37 */       fieldLength = 2147483647L;
/*     */     }
/*     */     
/*  40 */     return (int)fieldLength;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final boolean isDecimalType(int type)
/*     */   {
/*  50 */     switch (type) {
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
/*  61 */       return true;
/*     */     }
/*     */     
/*  64 */     return false;
/*     */   }
/*     */   
/*     */ 
/*  68 */   boolean useOldAliasBehavior = false;
/*  69 */   boolean treatYearAsDate = true;
/*     */   
/*     */ 
/*     */ 
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ResultSetMetaData(Field[] fields, boolean useOldAliasBehavior, boolean treatYearAsDate, ExceptionInterceptor exceptionInterceptor)
/*     */   {
/*  80 */     this.fields = fields;
/*  81 */     this.useOldAliasBehavior = useOldAliasBehavior;
/*  82 */     this.treatYearAsDate = treatYearAsDate;
/*  83 */     this.exceptionInterceptor = exceptionInterceptor;
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
/*     */   public String getCatalogName(int column)
/*     */     throws SQLException
/*     */   {
/*  98 */     Field f = getField(column);
/*     */     
/* 100 */     String database = f.getDatabaseName();
/*     */     
/* 102 */     return database == null ? "" : database;
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
/*     */   public String getColumnCharacterEncoding(int column)
/*     */     throws SQLException
/*     */   {
/* 119 */     String mysqlName = getColumnCharacterSet(column);
/*     */     
/* 121 */     String javaName = null;
/*     */     
/* 123 */     if (mysqlName != null) {
/*     */       try {
/* 125 */         javaName = CharsetMapping.getJavaEncodingForMysqlCharset(mysqlName);
/*     */       } catch (RuntimeException ex) {
/* 127 */         SQLException sqlEx = SQLError.createSQLException(ex.toString(), "S1009", null);
/* 128 */         sqlEx.initCause(ex);
/* 129 */         throw sqlEx;
/*     */       }
/*     */     }
/*     */     
/* 133 */     return javaName;
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
/*     */   public String getColumnCharacterSet(int column)
/*     */     throws SQLException
/*     */   {
/* 148 */     return getField(column).getEncoding();
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getColumnClassName(int column)
/*     */     throws SQLException
/*     */   {
/* 172 */     Field f = getField(column);
/*     */     
/* 174 */     return getClassNameForJavaType(f.getSQLType(), f.isUnsigned(), f.getMysqlType(), (f.isBinary()) || (f.isBlob()), f.isOpaqueBinary(), this.treatYearAsDate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getColumnCount()
/*     */     throws SQLException
/*     */   {
/* 186 */     return this.fields.length;
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
/*     */   public int getColumnDisplaySize(int column)
/*     */     throws SQLException
/*     */   {
/* 201 */     Field f = getField(column);
/*     */     
/* 203 */     int lengthInBytes = clampedGetLength(f);
/*     */     
/* 205 */     return lengthInBytes / f.getMaxBytesPerCharacter();
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
/*     */   public String getColumnLabel(int column)
/*     */     throws SQLException
/*     */   {
/* 220 */     if (this.useOldAliasBehavior) {
/* 221 */       return getColumnName(column);
/*     */     }
/*     */     
/* 224 */     return getField(column).getColumnLabel();
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
/*     */   public String getColumnName(int column)
/*     */     throws SQLException
/*     */   {
/* 239 */     if (this.useOldAliasBehavior) {
/* 240 */       return getField(column).getName();
/*     */     }
/*     */     
/* 243 */     String name = getField(column).getNameNoAliases();
/*     */     
/* 245 */     if ((name != null) && (name.length() == 0)) {
/* 246 */       return getField(column).getName();
/*     */     }
/*     */     
/* 249 */     return name;
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
/*     */   public int getColumnType(int column)
/*     */     throws SQLException
/*     */   {
/* 266 */     return getField(column).getSQLType();
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
/*     */   public String getColumnTypeName(int column)
/*     */     throws SQLException
/*     */   {
/* 281 */     Field field = getField(column);
/*     */     
/* 283 */     int mysqlType = field.getMysqlType();
/* 284 */     int jdbcType = field.getSQLType();
/*     */     
/* 286 */     switch (mysqlType) {
/*     */     case 16: 
/* 288 */       return "BIT";
/*     */     case 0: 
/*     */     case 246: 
/* 291 */       return field.isUnsigned() ? "DECIMAL UNSIGNED" : "DECIMAL";
/*     */     
/*     */     case 1: 
/* 294 */       return field.isUnsigned() ? "TINYINT UNSIGNED" : "TINYINT";
/*     */     
/*     */     case 2: 
/* 297 */       return field.isUnsigned() ? "SMALLINT UNSIGNED" : "SMALLINT";
/*     */     
/*     */     case 3: 
/* 300 */       return field.isUnsigned() ? "INT UNSIGNED" : "INT";
/*     */     
/*     */     case 4: 
/* 303 */       return field.isUnsigned() ? "FLOAT UNSIGNED" : "FLOAT";
/*     */     
/*     */     case 5: 
/* 306 */       return field.isUnsigned() ? "DOUBLE UNSIGNED" : "DOUBLE";
/*     */     
/*     */     case 6: 
/* 309 */       return "NULL";
/*     */     
/*     */     case 7: 
/* 312 */       return "TIMESTAMP";
/*     */     
/*     */     case 8: 
/* 315 */       return field.isUnsigned() ? "BIGINT UNSIGNED" : "BIGINT";
/*     */     
/*     */     case 9: 
/* 318 */       return field.isUnsigned() ? "MEDIUMINT UNSIGNED" : "MEDIUMINT";
/*     */     
/*     */     case 10: 
/* 321 */       return "DATE";
/*     */     
/*     */     case 11: 
/* 324 */       return "TIME";
/*     */     
/*     */     case 12: 
/* 327 */       return "DATETIME";
/*     */     
/*     */     case 249: 
/* 330 */       return "TINYBLOB";
/*     */     
/*     */     case 250: 
/* 333 */       return "MEDIUMBLOB";
/*     */     
/*     */     case 251: 
/* 336 */       return "LONGBLOB";
/*     */     
/*     */     case 252: 
/* 339 */       if (getField(column).isBinary()) {
/* 340 */         return "BLOB";
/*     */       }
/*     */       
/* 343 */       return "TEXT";
/*     */     
/*     */     case 15: 
/* 346 */       return "VARCHAR";
/*     */     
/*     */     case 253: 
/* 349 */       if (jdbcType == -3) {
/* 350 */         return "VARBINARY";
/*     */       }
/*     */       
/* 353 */       return "VARCHAR";
/*     */     
/*     */     case 254: 
/* 356 */       if (jdbcType == -2) {
/* 357 */         return "BINARY";
/*     */       }
/*     */       
/* 360 */       return "CHAR";
/*     */     
/*     */     case 247: 
/* 363 */       return "ENUM";
/*     */     
/*     */     case 13: 
/* 366 */       return "YEAR";
/*     */     
/*     */     case 248: 
/* 369 */       return "SET";
/*     */     
/*     */     case 255: 
/* 372 */       return "GEOMETRY";
/*     */     
/*     */     case 245: 
/* 375 */       return "JSON";
/*     */     }
/*     */     
/* 378 */     return "UNKNOWN";
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
/*     */   protected Field getField(int columnIndex)
/*     */     throws SQLException
/*     */   {
/* 394 */     if ((columnIndex < 1) || (columnIndex > this.fields.length)) {
/* 395 */       throw SQLError.createSQLException(Messages.getString("ResultSetMetaData.46"), "S1002", this.exceptionInterceptor);
/*     */     }
/*     */     
/* 398 */     return this.fields[(columnIndex - 1)];
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
/*     */   public int getPrecision(int column)
/*     */     throws SQLException
/*     */   {
/* 413 */     Field f = getField(column);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 419 */     if (isDecimalType(f.getSQLType())) {
/* 420 */       if (f.getDecimals() > 0) {
/* 421 */         return clampedGetLength(f) - 1 + f.getPrecisionAdjustFactor();
/*     */       }
/*     */       
/* 424 */       return clampedGetLength(f) + f.getPrecisionAdjustFactor();
/*     */     }
/*     */     
/* 427 */     switch (f.getMysqlType()) {
/*     */     case 249: 
/*     */     case 250: 
/*     */     case 251: 
/*     */     case 252: 
/* 432 */       return clampedGetLength(f);
/*     */     }
/*     */     
/* 435 */     return clampedGetLength(f) / f.getMaxBytesPerCharacter();
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
/*     */   public int getScale(int column)
/*     */     throws SQLException
/*     */   {
/* 452 */     Field f = getField(column);
/*     */     
/* 454 */     if (isDecimalType(f.getSQLType())) {
/* 455 */       return f.getDecimals();
/*     */     }
/*     */     
/* 458 */     return 0;
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
/*     */   public String getSchemaName(int column)
/*     */     throws SQLException
/*     */   {
/* 475 */     return "";
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
/*     */   public String getTableName(int column)
/*     */     throws SQLException
/*     */   {
/* 490 */     if (this.useOldAliasBehavior) {
/* 491 */       return getField(column).getTableName();
/*     */     }
/*     */     
/* 494 */     return getField(column).getTableNameNoAliases();
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
/*     */   public boolean isAutoIncrement(int column)
/*     */     throws SQLException
/*     */   {
/* 509 */     Field f = getField(column);
/*     */     
/* 511 */     return f.isAutoIncrement();
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
/*     */   public boolean isCaseSensitive(int column)
/*     */     throws SQLException
/*     */   {
/* 526 */     Field field = getField(column);
/*     */     
/* 528 */     int sqlType = field.getSQLType();
/*     */     
/* 530 */     switch (sqlType) {
/*     */     case -7: 
/*     */     case -6: 
/*     */     case -5: 
/*     */     case 4: 
/*     */     case 5: 
/*     */     case 6: 
/*     */     case 7: 
/*     */     case 8: 
/*     */     case 91: 
/*     */     case 92: 
/*     */     case 93: 
/* 542 */       return false;
/*     */     
/*     */ 
/*     */     case -1: 
/*     */     case 1: 
/*     */     case 12: 
/* 548 */       if (field.isBinary()) {
/* 549 */         return true;
/*     */       }
/*     */       
/* 552 */       String collationName = field.getCollation();
/*     */       
/* 554 */       return (collationName != null) && (!collationName.endsWith("_ci"));
/*     */     }
/*     */     
/* 557 */     return true;
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
/*     */   public boolean isCurrency(int column)
/*     */     throws SQLException
/*     */   {
/* 573 */     return false;
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
/*     */   public boolean isDefinitelyWritable(int column)
/*     */     throws SQLException
/*     */   {
/* 588 */     return isWritable(column);
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
/*     */   public int isNullable(int column)
/*     */     throws SQLException
/*     */   {
/* 603 */     if (!getField(column).isNotNull()) {
/* 604 */       return 1;
/*     */     }
/*     */     
/* 607 */     return 0;
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
/*     */   public boolean isReadOnly(int column)
/*     */     throws SQLException
/*     */   {
/* 622 */     return getField(column).isReadOnly();
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
/*     */   public boolean isSearchable(int column)
/*     */     throws SQLException
/*     */   {
/* 641 */     return true;
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
/*     */   public boolean isSigned(int column)
/*     */     throws SQLException
/*     */   {
/* 656 */     Field f = getField(column);
/* 657 */     int sqlType = f.getSQLType();
/*     */     
/* 659 */     switch (sqlType) {
/*     */     case -6: 
/*     */     case -5: 
/*     */     case 2: 
/*     */     case 3: 
/*     */     case 4: 
/*     */     case 5: 
/*     */     case 6: 
/*     */     case 7: 
/*     */     case 8: 
/* 669 */       return !f.isUnsigned();
/*     */     
/*     */     case 91: 
/*     */     case 92: 
/*     */     case 93: 
/* 674 */       return false;
/*     */     }
/*     */     
/* 677 */     return false;
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
/*     */   public boolean isWritable(int column)
/*     */     throws SQLException
/*     */   {
/* 693 */     return !isReadOnly(column);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 703 */     StringBuilder toStringBuf = new StringBuilder();
/* 704 */     toStringBuf.append(super.toString());
/* 705 */     toStringBuf.append(" - Field level information: ");
/*     */     
/* 707 */     for (int i = 0; i < this.fields.length; i++) {
/* 708 */       toStringBuf.append("\n\t");
/* 709 */       toStringBuf.append(this.fields[i].toString());
/*     */     }
/*     */     
/* 712 */     return toStringBuf.toString();
/*     */   }
/*     */   
/*     */   static String getClassNameForJavaType(int javaType, boolean isUnsigned, int mysqlTypeIfKnown, boolean isBinaryOrBlob, boolean isOpaqueBinary, boolean treatYearAsDate)
/*     */   {
/* 717 */     switch (javaType) {
/*     */     case -7: 
/*     */     case 16: 
/* 720 */       return "java.lang.Boolean";
/*     */     
/*     */ 
/*     */     case -6: 
/* 724 */       if (isUnsigned) {
/* 725 */         return "java.lang.Integer";
/*     */       }
/*     */       
/* 728 */       return "java.lang.Integer";
/*     */     
/*     */ 
/*     */     case 5: 
/* 732 */       if (isUnsigned) {
/* 733 */         return "java.lang.Integer";
/*     */       }
/*     */       
/* 736 */       return "java.lang.Integer";
/*     */     
/*     */ 
/*     */     case 4: 
/* 740 */       if ((!isUnsigned) || (mysqlTypeIfKnown == 9)) {
/* 741 */         return "java.lang.Integer";
/*     */       }
/*     */       
/* 744 */       return "java.lang.Long";
/*     */     
/*     */ 
/*     */     case -5: 
/* 748 */       if (!isUnsigned) {
/* 749 */         return "java.lang.Long";
/*     */       }
/*     */       
/* 752 */       return "java.math.BigInteger";
/*     */     
/*     */     case 2: 
/*     */     case 3: 
/* 756 */       return "java.math.BigDecimal";
/*     */     
/*     */     case 7: 
/* 759 */       return "java.lang.Float";
/*     */     
/*     */     case 6: 
/*     */     case 8: 
/* 763 */       return "java.lang.Double";
/*     */     
/*     */     case -1: 
/*     */     case 1: 
/*     */     case 12: 
/* 768 */       if (!isOpaqueBinary) {
/* 769 */         return "java.lang.String";
/*     */       }
/*     */       
/* 772 */       return "[B";
/*     */     
/*     */ 
/*     */     case -4: 
/*     */     case -3: 
/*     */     case -2: 
/* 778 */       if (mysqlTypeIfKnown == 255)
/* 779 */         return "[B";
/* 780 */       if (isBinaryOrBlob) {
/* 781 */         return "[B";
/*     */       }
/* 783 */       return "java.lang.String";
/*     */     
/*     */ 
/*     */     case 91: 
/* 787 */       return (treatYearAsDate) || (mysqlTypeIfKnown != 13) ? "java.sql.Date" : "java.lang.Short";
/*     */     
/*     */     case 92: 
/* 790 */       return "java.sql.Time";
/*     */     
/*     */     case 93: 
/* 793 */       return "java.sql.Timestamp";
/*     */     }
/*     */     
/* 796 */     return "java.lang.Object";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isWrapperFor(Class<?> iface)
/*     */     throws SQLException
/*     */   {
/* 805 */     return iface.isInstance(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public <T> T unwrap(Class<T> iface)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 814 */       return (T)iface.cast(this);
/*     */     } catch (ClassCastException cce) {
/* 816 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\ResultSetMetaData.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */