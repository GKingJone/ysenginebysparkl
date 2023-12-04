/*     */ package com.mchange.v2.sql.filter;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.math.BigDecimal;
/*     */ import java.net.URL;
/*     */ import java.sql.Array;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.Date;
/*     */ import java.sql.Ref;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLWarning;
/*     */ import java.sql.Statement;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Calendar;
/*     */ import java.util.Map;
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
/*     */ public abstract class SynchronizedFilterResultSet
/*     */   implements ResultSet
/*     */ {
/*     */   protected ResultSet inner;
/*     */   
/*     */   public SynchronizedFilterResultSet(ResultSet inner)
/*     */   {
/*  52 */     this.inner = inner;
/*     */   }
/*     */   
/*     */   public SynchronizedFilterResultSet() {}
/*     */   
/*     */   public synchronized void setInner(ResultSet inner) {
/*  58 */     this.inner = inner;
/*     */   }
/*     */   
/*  61 */   public synchronized ResultSet getInner() { return this.inner; }
/*     */   
/*     */   public synchronized ResultSetMetaData getMetaData() throws SQLException {
/*  64 */     return this.inner.getMetaData();
/*     */   }
/*     */   
/*  67 */   public synchronized SQLWarning getWarnings() throws SQLException { return this.inner.getWarnings(); }
/*     */   
/*     */   public synchronized void clearWarnings() throws SQLException {
/*  70 */     this.inner.clearWarnings();
/*     */   }
/*     */   
/*  73 */   public synchronized boolean wasNull() throws SQLException { return this.inner.wasNull(); }
/*     */   
/*     */   public synchronized BigDecimal getBigDecimal(int a) throws SQLException {
/*  76 */     return this.inner.getBigDecimal(a);
/*     */   }
/*     */   
/*  79 */   public synchronized BigDecimal getBigDecimal(String a, int b) throws SQLException { return this.inner.getBigDecimal(a, b); }
/*     */   
/*     */   public synchronized BigDecimal getBigDecimal(int a, int b) throws SQLException {
/*  82 */     return this.inner.getBigDecimal(a, b);
/*     */   }
/*     */   
/*  85 */   public synchronized BigDecimal getBigDecimal(String a) throws SQLException { return this.inner.getBigDecimal(a); }
/*     */   
/*     */   public synchronized Timestamp getTimestamp(int a) throws SQLException {
/*  88 */     return this.inner.getTimestamp(a);
/*     */   }
/*     */   
/*  91 */   public synchronized Timestamp getTimestamp(String a) throws SQLException { return this.inner.getTimestamp(a); }
/*     */   
/*     */   public synchronized Timestamp getTimestamp(int a, Calendar b) throws SQLException {
/*  94 */     return this.inner.getTimestamp(a, b);
/*     */   }
/*     */   
/*  97 */   public synchronized Timestamp getTimestamp(String a, Calendar b) throws SQLException { return this.inner.getTimestamp(a, b); }
/*     */   
/*     */   public synchronized InputStream getAsciiStream(String a) throws SQLException {
/* 100 */     return this.inner.getAsciiStream(a);
/*     */   }
/*     */   
/* 103 */   public synchronized InputStream getAsciiStream(int a) throws SQLException { return this.inner.getAsciiStream(a); }
/*     */   
/*     */   public synchronized InputStream getUnicodeStream(String a) throws SQLException {
/* 106 */     return this.inner.getUnicodeStream(a);
/*     */   }
/*     */   
/* 109 */   public synchronized InputStream getUnicodeStream(int a) throws SQLException { return this.inner.getUnicodeStream(a); }
/*     */   
/*     */   public synchronized InputStream getBinaryStream(int a) throws SQLException {
/* 112 */     return this.inner.getBinaryStream(a);
/*     */   }
/*     */   
/* 115 */   public synchronized InputStream getBinaryStream(String a) throws SQLException { return this.inner.getBinaryStream(a); }
/*     */   
/*     */   public synchronized String getCursorName() throws SQLException {
/* 118 */     return this.inner.getCursorName();
/*     */   }
/*     */   
/* 121 */   public synchronized Reader getCharacterStream(int a) throws SQLException { return this.inner.getCharacterStream(a); }
/*     */   
/*     */   public synchronized Reader getCharacterStream(String a) throws SQLException {
/* 124 */     return this.inner.getCharacterStream(a);
/*     */   }
/*     */   
/* 127 */   public synchronized boolean isBeforeFirst() throws SQLException { return this.inner.isBeforeFirst(); }
/*     */   
/*     */   public synchronized boolean isAfterLast() throws SQLException {
/* 130 */     return this.inner.isAfterLast();
/*     */   }
/*     */   
/* 133 */   public synchronized boolean isFirst() throws SQLException { return this.inner.isFirst(); }
/*     */   
/*     */   public synchronized boolean isLast() throws SQLException {
/* 136 */     return this.inner.isLast();
/*     */   }
/*     */   
/* 139 */   public synchronized void beforeFirst() throws SQLException { this.inner.beforeFirst(); }
/*     */   
/*     */   public synchronized void afterLast() throws SQLException {
/* 142 */     this.inner.afterLast();
/*     */   }
/*     */   
/* 145 */   public synchronized boolean absolute(int a) throws SQLException { return this.inner.absolute(a); }
/*     */   
/*     */   public synchronized void setFetchDirection(int a) throws SQLException {
/* 148 */     this.inner.setFetchDirection(a);
/*     */   }
/*     */   
/* 151 */   public synchronized int getFetchDirection() throws SQLException { return this.inner.getFetchDirection(); }
/*     */   
/*     */   public synchronized void setFetchSize(int a) throws SQLException {
/* 154 */     this.inner.setFetchSize(a);
/*     */   }
/*     */   
/* 157 */   public synchronized int getFetchSize() throws SQLException { return this.inner.getFetchSize(); }
/*     */   
/*     */   public synchronized int getConcurrency() throws SQLException {
/* 160 */     return this.inner.getConcurrency();
/*     */   }
/*     */   
/* 163 */   public synchronized boolean rowUpdated() throws SQLException { return this.inner.rowUpdated(); }
/*     */   
/*     */   public synchronized boolean rowInserted() throws SQLException {
/* 166 */     return this.inner.rowInserted();
/*     */   }
/*     */   
/* 169 */   public synchronized boolean rowDeleted() throws SQLException { return this.inner.rowDeleted(); }
/*     */   
/*     */   public synchronized void updateNull(int a) throws SQLException {
/* 172 */     this.inner.updateNull(a);
/*     */   }
/*     */   
/* 175 */   public synchronized void updateNull(String a) throws SQLException { this.inner.updateNull(a); }
/*     */   
/*     */   public synchronized void updateBoolean(int a, boolean b) throws SQLException {
/* 178 */     this.inner.updateBoolean(a, b);
/*     */   }
/*     */   
/* 181 */   public synchronized void updateBoolean(String a, boolean b) throws SQLException { this.inner.updateBoolean(a, b); }
/*     */   
/*     */   public synchronized void updateByte(int a, byte b) throws SQLException {
/* 184 */     this.inner.updateByte(a, b);
/*     */   }
/*     */   
/* 187 */   public synchronized void updateByte(String a, byte b) throws SQLException { this.inner.updateByte(a, b); }
/*     */   
/*     */   public synchronized void updateShort(int a, short b) throws SQLException {
/* 190 */     this.inner.updateShort(a, b);
/*     */   }
/*     */   
/* 193 */   public synchronized void updateShort(String a, short b) throws SQLException { this.inner.updateShort(a, b); }
/*     */   
/*     */   public synchronized void updateInt(String a, int b) throws SQLException {
/* 196 */     this.inner.updateInt(a, b);
/*     */   }
/*     */   
/* 199 */   public synchronized void updateInt(int a, int b) throws SQLException { this.inner.updateInt(a, b); }
/*     */   
/*     */   public synchronized void updateLong(int a, long b) throws SQLException {
/* 202 */     this.inner.updateLong(a, b);
/*     */   }
/*     */   
/* 205 */   public synchronized void updateLong(String a, long b) throws SQLException { this.inner.updateLong(a, b); }
/*     */   
/*     */   public synchronized void updateFloat(String a, float b) throws SQLException {
/* 208 */     this.inner.updateFloat(a, b);
/*     */   }
/*     */   
/* 211 */   public synchronized void updateFloat(int a, float b) throws SQLException { this.inner.updateFloat(a, b); }
/*     */   
/*     */   public synchronized void updateDouble(String a, double b) throws SQLException {
/* 214 */     this.inner.updateDouble(a, b);
/*     */   }
/*     */   
/* 217 */   public synchronized void updateDouble(int a, double b) throws SQLException { this.inner.updateDouble(a, b); }
/*     */   
/*     */   public synchronized void updateBigDecimal(int a, BigDecimal b) throws SQLException {
/* 220 */     this.inner.updateBigDecimal(a, b);
/*     */   }
/*     */   
/* 223 */   public synchronized void updateBigDecimal(String a, BigDecimal b) throws SQLException { this.inner.updateBigDecimal(a, b); }
/*     */   
/*     */   public synchronized void updateString(String a, String b) throws SQLException {
/* 226 */     this.inner.updateString(a, b);
/*     */   }
/*     */   
/* 229 */   public synchronized void updateString(int a, String b) throws SQLException { this.inner.updateString(a, b); }
/*     */   
/*     */   public synchronized void updateBytes(int a, byte[] b) throws SQLException {
/* 232 */     this.inner.updateBytes(a, b);
/*     */   }
/*     */   
/* 235 */   public synchronized void updateBytes(String a, byte[] b) throws SQLException { this.inner.updateBytes(a, b); }
/*     */   
/*     */   public synchronized void updateDate(String a, Date b) throws SQLException {
/* 238 */     this.inner.updateDate(a, b);
/*     */   }
/*     */   
/* 241 */   public synchronized void updateDate(int a, Date b) throws SQLException { this.inner.updateDate(a, b); }
/*     */   
/*     */   public synchronized void updateTimestamp(int a, Timestamp b) throws SQLException {
/* 244 */     this.inner.updateTimestamp(a, b);
/*     */   }
/*     */   
/* 247 */   public synchronized void updateTimestamp(String a, Timestamp b) throws SQLException { this.inner.updateTimestamp(a, b); }
/*     */   
/*     */   public synchronized void updateAsciiStream(String a, InputStream b, int c) throws SQLException {
/* 250 */     this.inner.updateAsciiStream(a, b, c);
/*     */   }
/*     */   
/* 253 */   public synchronized void updateAsciiStream(int a, InputStream b, int c) throws SQLException { this.inner.updateAsciiStream(a, b, c); }
/*     */   
/*     */   public synchronized void updateBinaryStream(int a, InputStream b, int c) throws SQLException {
/* 256 */     this.inner.updateBinaryStream(a, b, c);
/*     */   }
/*     */   
/* 259 */   public synchronized void updateBinaryStream(String a, InputStream b, int c) throws SQLException { this.inner.updateBinaryStream(a, b, c); }
/*     */   
/*     */   public synchronized void updateCharacterStream(int a, Reader b, int c) throws SQLException {
/* 262 */     this.inner.updateCharacterStream(a, b, c);
/*     */   }
/*     */   
/* 265 */   public synchronized void updateCharacterStream(String a, Reader b, int c) throws SQLException { this.inner.updateCharacterStream(a, b, c); }
/*     */   
/*     */   public synchronized void updateObject(String a, Object b) throws SQLException {
/* 268 */     this.inner.updateObject(a, b);
/*     */   }
/*     */   
/* 271 */   public synchronized void updateObject(int a, Object b) throws SQLException { this.inner.updateObject(a, b); }
/*     */   
/*     */   public synchronized void updateObject(int a, Object b, int c) throws SQLException {
/* 274 */     this.inner.updateObject(a, b, c);
/*     */   }
/*     */   
/* 277 */   public synchronized void updateObject(String a, Object b, int c) throws SQLException { this.inner.updateObject(a, b, c); }
/*     */   
/*     */   public synchronized void insertRow() throws SQLException {
/* 280 */     this.inner.insertRow();
/*     */   }
/*     */   
/* 283 */   public synchronized void updateRow() throws SQLException { this.inner.updateRow(); }
/*     */   
/*     */   public synchronized void deleteRow() throws SQLException {
/* 286 */     this.inner.deleteRow();
/*     */   }
/*     */   
/* 289 */   public synchronized void refreshRow() throws SQLException { this.inner.refreshRow(); }
/*     */   
/*     */   public synchronized void cancelRowUpdates() throws SQLException {
/* 292 */     this.inner.cancelRowUpdates();
/*     */   }
/*     */   
/* 295 */   public synchronized void moveToInsertRow() throws SQLException { this.inner.moveToInsertRow(); }
/*     */   
/*     */   public synchronized void moveToCurrentRow() throws SQLException {
/* 298 */     this.inner.moveToCurrentRow();
/*     */   }
/*     */   
/* 301 */   public synchronized Statement getStatement() throws SQLException { return this.inner.getStatement(); }
/*     */   
/*     */   public synchronized Blob getBlob(String a) throws SQLException {
/* 304 */     return this.inner.getBlob(a);
/*     */   }
/*     */   
/* 307 */   public synchronized Blob getBlob(int a) throws SQLException { return this.inner.getBlob(a); }
/*     */   
/*     */   public synchronized Clob getClob(String a) throws SQLException {
/* 310 */     return this.inner.getClob(a);
/*     */   }
/*     */   
/* 313 */   public synchronized Clob getClob(int a) throws SQLException { return this.inner.getClob(a); }
/*     */   
/*     */   public synchronized void updateRef(String a, Ref b) throws SQLException {
/* 316 */     this.inner.updateRef(a, b);
/*     */   }
/*     */   
/* 319 */   public synchronized void updateRef(int a, Ref b) throws SQLException { this.inner.updateRef(a, b); }
/*     */   
/*     */   public synchronized void updateBlob(String a, Blob b) throws SQLException {
/* 322 */     this.inner.updateBlob(a, b);
/*     */   }
/*     */   
/* 325 */   public synchronized void updateBlob(int a, Blob b) throws SQLException { this.inner.updateBlob(a, b); }
/*     */   
/*     */   public synchronized void updateClob(int a, Clob b) throws SQLException {
/* 328 */     this.inner.updateClob(a, b);
/*     */   }
/*     */   
/* 331 */   public synchronized void updateClob(String a, Clob b) throws SQLException { this.inner.updateClob(a, b); }
/*     */   
/*     */   public synchronized void updateArray(String a, Array b) throws SQLException {
/* 334 */     this.inner.updateArray(a, b);
/*     */   }
/*     */   
/* 337 */   public synchronized void updateArray(int a, Array b) throws SQLException { this.inner.updateArray(a, b); }
/*     */   
/*     */   public synchronized Object getObject(int a) throws SQLException {
/* 340 */     return this.inner.getObject(a);
/*     */   }
/*     */   
/* 343 */   public synchronized Object getObject(String a, Map b) throws SQLException { return this.inner.getObject(a, b); }
/*     */   
/*     */   public synchronized Object getObject(String a) throws SQLException {
/* 346 */     return this.inner.getObject(a);
/*     */   }
/*     */   
/* 349 */   public synchronized Object getObject(int a, Map b) throws SQLException { return this.inner.getObject(a, b); }
/*     */   
/*     */   public synchronized boolean getBoolean(int a) throws SQLException {
/* 352 */     return this.inner.getBoolean(a);
/*     */   }
/*     */   
/* 355 */   public synchronized boolean getBoolean(String a) throws SQLException { return this.inner.getBoolean(a); }
/*     */   
/*     */   public synchronized byte getByte(String a) throws SQLException {
/* 358 */     return this.inner.getByte(a);
/*     */   }
/*     */   
/* 361 */   public synchronized byte getByte(int a) throws SQLException { return this.inner.getByte(a); }
/*     */   
/*     */   public synchronized short getShort(String a) throws SQLException {
/* 364 */     return this.inner.getShort(a);
/*     */   }
/*     */   
/* 367 */   public synchronized short getShort(int a) throws SQLException { return this.inner.getShort(a); }
/*     */   
/*     */   public synchronized int getInt(String a) throws SQLException {
/* 370 */     return this.inner.getInt(a);
/*     */   }
/*     */   
/* 373 */   public synchronized int getInt(int a) throws SQLException { return this.inner.getInt(a); }
/*     */   
/*     */   public synchronized long getLong(int a) throws SQLException {
/* 376 */     return this.inner.getLong(a);
/*     */   }
/*     */   
/* 379 */   public synchronized long getLong(String a) throws SQLException { return this.inner.getLong(a); }
/*     */   
/*     */   public synchronized float getFloat(String a) throws SQLException {
/* 382 */     return this.inner.getFloat(a);
/*     */   }
/*     */   
/* 385 */   public synchronized float getFloat(int a) throws SQLException { return this.inner.getFloat(a); }
/*     */   
/*     */   public synchronized double getDouble(int a) throws SQLException {
/* 388 */     return this.inner.getDouble(a);
/*     */   }
/*     */   
/* 391 */   public synchronized double getDouble(String a) throws SQLException { return this.inner.getDouble(a); }
/*     */   
/*     */   public synchronized byte[] getBytes(String a) throws SQLException {
/* 394 */     return this.inner.getBytes(a);
/*     */   }
/*     */   
/* 397 */   public synchronized byte[] getBytes(int a) throws SQLException { return this.inner.getBytes(a); }
/*     */   
/*     */   public synchronized boolean next() throws SQLException {
/* 400 */     return this.inner.next();
/*     */   }
/*     */   
/* 403 */   public synchronized URL getURL(int a) throws SQLException { return this.inner.getURL(a); }
/*     */   
/*     */   public synchronized URL getURL(String a) throws SQLException {
/* 406 */     return this.inner.getURL(a);
/*     */   }
/*     */   
/* 409 */   public synchronized int getType() throws SQLException { return this.inner.getType(); }
/*     */   
/*     */   public synchronized boolean previous() throws SQLException {
/* 412 */     return this.inner.previous();
/*     */   }
/*     */   
/* 415 */   public synchronized void close() throws SQLException { this.inner.close(); }
/*     */   
/*     */   public synchronized String getString(String a) throws SQLException {
/* 418 */     return this.inner.getString(a);
/*     */   }
/*     */   
/* 421 */   public synchronized String getString(int a) throws SQLException { return this.inner.getString(a); }
/*     */   
/*     */   public synchronized Ref getRef(String a) throws SQLException {
/* 424 */     return this.inner.getRef(a);
/*     */   }
/*     */   
/* 427 */   public synchronized Ref getRef(int a) throws SQLException { return this.inner.getRef(a); }
/*     */   
/*     */   public synchronized Time getTime(int a, Calendar b) throws SQLException {
/* 430 */     return this.inner.getTime(a, b);
/*     */   }
/*     */   
/* 433 */   public synchronized Time getTime(String a) throws SQLException { return this.inner.getTime(a); }
/*     */   
/*     */   public synchronized Time getTime(int a) throws SQLException {
/* 436 */     return this.inner.getTime(a);
/*     */   }
/*     */   
/* 439 */   public synchronized Time getTime(String a, Calendar b) throws SQLException { return this.inner.getTime(a, b); }
/*     */   
/*     */   public synchronized Date getDate(String a) throws SQLException {
/* 442 */     return this.inner.getDate(a);
/*     */   }
/*     */   
/* 445 */   public synchronized Date getDate(int a) throws SQLException { return this.inner.getDate(a); }
/*     */   
/*     */   public synchronized Date getDate(int a, Calendar b) throws SQLException {
/* 448 */     return this.inner.getDate(a, b);
/*     */   }
/*     */   
/* 451 */   public synchronized Date getDate(String a, Calendar b) throws SQLException { return this.inner.getDate(a, b); }
/*     */   
/*     */   public synchronized boolean first() throws SQLException {
/* 454 */     return this.inner.first();
/*     */   }
/*     */   
/* 457 */   public synchronized boolean last() throws SQLException { return this.inner.last(); }
/*     */   
/*     */   public synchronized Array getArray(String a) throws SQLException {
/* 460 */     return this.inner.getArray(a);
/*     */   }
/*     */   
/* 463 */   public synchronized Array getArray(int a) throws SQLException { return this.inner.getArray(a); }
/*     */   
/*     */   public synchronized boolean relative(int a) throws SQLException {
/* 466 */     return this.inner.relative(a);
/*     */   }
/*     */   
/* 469 */   public synchronized void updateTime(String a, Time b) throws SQLException { this.inner.updateTime(a, b); }
/*     */   
/*     */   public synchronized void updateTime(int a, Time b) throws SQLException {
/* 472 */     this.inner.updateTime(a, b);
/*     */   }
/*     */   
/* 475 */   public synchronized int findColumn(String a) throws SQLException { return this.inner.findColumn(a); }
/*     */   
/*     */   public synchronized int getRow() throws SQLException {
/* 478 */     return this.inner.getRow();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\sql\filter\SynchronizedFilterResultSet.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */