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
/*     */ public abstract class FilterResultSet
/*     */   implements ResultSet
/*     */ {
/*     */   protected ResultSet inner;
/*     */   
/*     */   public FilterResultSet(ResultSet inner)
/*     */   {
/*  52 */     this.inner = inner;
/*     */   }
/*     */   
/*     */   public FilterResultSet() {}
/*     */   
/*     */   public void setInner(ResultSet inner) {
/*  58 */     this.inner = inner;
/*     */   }
/*     */   
/*  61 */   public ResultSet getInner() { return this.inner; }
/*     */   
/*     */   public ResultSetMetaData getMetaData() throws SQLException {
/*  64 */     return this.inner.getMetaData();
/*     */   }
/*     */   
/*  67 */   public SQLWarning getWarnings() throws SQLException { return this.inner.getWarnings(); }
/*     */   
/*     */   public void clearWarnings() throws SQLException {
/*  70 */     this.inner.clearWarnings();
/*     */   }
/*     */   
/*  73 */   public boolean wasNull() throws SQLException { return this.inner.wasNull(); }
/*     */   
/*     */   public BigDecimal getBigDecimal(int a) throws SQLException {
/*  76 */     return this.inner.getBigDecimal(a);
/*     */   }
/*     */   
/*  79 */   public BigDecimal getBigDecimal(String a, int b) throws SQLException { return this.inner.getBigDecimal(a, b); }
/*     */   
/*     */   public BigDecimal getBigDecimal(int a, int b) throws SQLException {
/*  82 */     return this.inner.getBigDecimal(a, b);
/*     */   }
/*     */   
/*  85 */   public BigDecimal getBigDecimal(String a) throws SQLException { return this.inner.getBigDecimal(a); }
/*     */   
/*     */   public Timestamp getTimestamp(int a) throws SQLException {
/*  88 */     return this.inner.getTimestamp(a);
/*     */   }
/*     */   
/*  91 */   public Timestamp getTimestamp(String a) throws SQLException { return this.inner.getTimestamp(a); }
/*     */   
/*     */   public Timestamp getTimestamp(int a, Calendar b) throws SQLException {
/*  94 */     return this.inner.getTimestamp(a, b);
/*     */   }
/*     */   
/*  97 */   public Timestamp getTimestamp(String a, Calendar b) throws SQLException { return this.inner.getTimestamp(a, b); }
/*     */   
/*     */   public InputStream getAsciiStream(String a) throws SQLException {
/* 100 */     return this.inner.getAsciiStream(a);
/*     */   }
/*     */   
/* 103 */   public InputStream getAsciiStream(int a) throws SQLException { return this.inner.getAsciiStream(a); }
/*     */   
/*     */   public InputStream getUnicodeStream(String a) throws SQLException {
/* 106 */     return this.inner.getUnicodeStream(a);
/*     */   }
/*     */   
/* 109 */   public InputStream getUnicodeStream(int a) throws SQLException { return this.inner.getUnicodeStream(a); }
/*     */   
/*     */   public InputStream getBinaryStream(int a) throws SQLException {
/* 112 */     return this.inner.getBinaryStream(a);
/*     */   }
/*     */   
/* 115 */   public InputStream getBinaryStream(String a) throws SQLException { return this.inner.getBinaryStream(a); }
/*     */   
/*     */   public String getCursorName() throws SQLException {
/* 118 */     return this.inner.getCursorName();
/*     */   }
/*     */   
/* 121 */   public Reader getCharacterStream(int a) throws SQLException { return this.inner.getCharacterStream(a); }
/*     */   
/*     */   public Reader getCharacterStream(String a) throws SQLException {
/* 124 */     return this.inner.getCharacterStream(a);
/*     */   }
/*     */   
/* 127 */   public boolean isBeforeFirst() throws SQLException { return this.inner.isBeforeFirst(); }
/*     */   
/*     */   public boolean isAfterLast() throws SQLException {
/* 130 */     return this.inner.isAfterLast();
/*     */   }
/*     */   
/* 133 */   public boolean isFirst() throws SQLException { return this.inner.isFirst(); }
/*     */   
/*     */   public boolean isLast() throws SQLException {
/* 136 */     return this.inner.isLast();
/*     */   }
/*     */   
/* 139 */   public void beforeFirst() throws SQLException { this.inner.beforeFirst(); }
/*     */   
/*     */   public void afterLast() throws SQLException {
/* 142 */     this.inner.afterLast();
/*     */   }
/*     */   
/* 145 */   public boolean absolute(int a) throws SQLException { return this.inner.absolute(a); }
/*     */   
/*     */   public void setFetchDirection(int a) throws SQLException {
/* 148 */     this.inner.setFetchDirection(a);
/*     */   }
/*     */   
/* 151 */   public int getFetchDirection() throws SQLException { return this.inner.getFetchDirection(); }
/*     */   
/*     */   public void setFetchSize(int a) throws SQLException {
/* 154 */     this.inner.setFetchSize(a);
/*     */   }
/*     */   
/* 157 */   public int getFetchSize() throws SQLException { return this.inner.getFetchSize(); }
/*     */   
/*     */   public int getConcurrency() throws SQLException {
/* 160 */     return this.inner.getConcurrency();
/*     */   }
/*     */   
/* 163 */   public boolean rowUpdated() throws SQLException { return this.inner.rowUpdated(); }
/*     */   
/*     */   public boolean rowInserted() throws SQLException {
/* 166 */     return this.inner.rowInserted();
/*     */   }
/*     */   
/* 169 */   public boolean rowDeleted() throws SQLException { return this.inner.rowDeleted(); }
/*     */   
/*     */   public void updateNull(int a) throws SQLException {
/* 172 */     this.inner.updateNull(a);
/*     */   }
/*     */   
/* 175 */   public void updateNull(String a) throws SQLException { this.inner.updateNull(a); }
/*     */   
/*     */   public void updateBoolean(int a, boolean b) throws SQLException {
/* 178 */     this.inner.updateBoolean(a, b);
/*     */   }
/*     */   
/* 181 */   public void updateBoolean(String a, boolean b) throws SQLException { this.inner.updateBoolean(a, b); }
/*     */   
/*     */   public void updateByte(int a, byte b) throws SQLException {
/* 184 */     this.inner.updateByte(a, b);
/*     */   }
/*     */   
/* 187 */   public void updateByte(String a, byte b) throws SQLException { this.inner.updateByte(a, b); }
/*     */   
/*     */   public void updateShort(int a, short b) throws SQLException {
/* 190 */     this.inner.updateShort(a, b);
/*     */   }
/*     */   
/* 193 */   public void updateShort(String a, short b) throws SQLException { this.inner.updateShort(a, b); }
/*     */   
/*     */   public void updateInt(String a, int b) throws SQLException {
/* 196 */     this.inner.updateInt(a, b);
/*     */   }
/*     */   
/* 199 */   public void updateInt(int a, int b) throws SQLException { this.inner.updateInt(a, b); }
/*     */   
/*     */   public void updateLong(int a, long b) throws SQLException {
/* 202 */     this.inner.updateLong(a, b);
/*     */   }
/*     */   
/* 205 */   public void updateLong(String a, long b) throws SQLException { this.inner.updateLong(a, b); }
/*     */   
/*     */   public void updateFloat(String a, float b) throws SQLException {
/* 208 */     this.inner.updateFloat(a, b);
/*     */   }
/*     */   
/* 211 */   public void updateFloat(int a, float b) throws SQLException { this.inner.updateFloat(a, b); }
/*     */   
/*     */   public void updateDouble(String a, double b) throws SQLException {
/* 214 */     this.inner.updateDouble(a, b);
/*     */   }
/*     */   
/* 217 */   public void updateDouble(int a, double b) throws SQLException { this.inner.updateDouble(a, b); }
/*     */   
/*     */   public void updateBigDecimal(int a, BigDecimal b) throws SQLException {
/* 220 */     this.inner.updateBigDecimal(a, b);
/*     */   }
/*     */   
/* 223 */   public void updateBigDecimal(String a, BigDecimal b) throws SQLException { this.inner.updateBigDecimal(a, b); }
/*     */   
/*     */   public void updateString(String a, String b) throws SQLException {
/* 226 */     this.inner.updateString(a, b);
/*     */   }
/*     */   
/* 229 */   public void updateString(int a, String b) throws SQLException { this.inner.updateString(a, b); }
/*     */   
/*     */   public void updateBytes(int a, byte[] b) throws SQLException {
/* 232 */     this.inner.updateBytes(a, b);
/*     */   }
/*     */   
/* 235 */   public void updateBytes(String a, byte[] b) throws SQLException { this.inner.updateBytes(a, b); }
/*     */   
/*     */   public void updateDate(String a, Date b) throws SQLException {
/* 238 */     this.inner.updateDate(a, b);
/*     */   }
/*     */   
/* 241 */   public void updateDate(int a, Date b) throws SQLException { this.inner.updateDate(a, b); }
/*     */   
/*     */   public void updateTimestamp(int a, Timestamp b) throws SQLException {
/* 244 */     this.inner.updateTimestamp(a, b);
/*     */   }
/*     */   
/* 247 */   public void updateTimestamp(String a, Timestamp b) throws SQLException { this.inner.updateTimestamp(a, b); }
/*     */   
/*     */   public void updateAsciiStream(String a, InputStream b, int c) throws SQLException {
/* 250 */     this.inner.updateAsciiStream(a, b, c);
/*     */   }
/*     */   
/* 253 */   public void updateAsciiStream(int a, InputStream b, int c) throws SQLException { this.inner.updateAsciiStream(a, b, c); }
/*     */   
/*     */   public void updateBinaryStream(int a, InputStream b, int c) throws SQLException {
/* 256 */     this.inner.updateBinaryStream(a, b, c);
/*     */   }
/*     */   
/* 259 */   public void updateBinaryStream(String a, InputStream b, int c) throws SQLException { this.inner.updateBinaryStream(a, b, c); }
/*     */   
/*     */   public void updateCharacterStream(int a, Reader b, int c) throws SQLException {
/* 262 */     this.inner.updateCharacterStream(a, b, c);
/*     */   }
/*     */   
/* 265 */   public void updateCharacterStream(String a, Reader b, int c) throws SQLException { this.inner.updateCharacterStream(a, b, c); }
/*     */   
/*     */   public void updateObject(String a, Object b) throws SQLException {
/* 268 */     this.inner.updateObject(a, b);
/*     */   }
/*     */   
/* 271 */   public void updateObject(int a, Object b) throws SQLException { this.inner.updateObject(a, b); }
/*     */   
/*     */   public void updateObject(int a, Object b, int c) throws SQLException {
/* 274 */     this.inner.updateObject(a, b, c);
/*     */   }
/*     */   
/* 277 */   public void updateObject(String a, Object b, int c) throws SQLException { this.inner.updateObject(a, b, c); }
/*     */   
/*     */   public void insertRow() throws SQLException {
/* 280 */     this.inner.insertRow();
/*     */   }
/*     */   
/* 283 */   public void updateRow() throws SQLException { this.inner.updateRow(); }
/*     */   
/*     */   public void deleteRow() throws SQLException {
/* 286 */     this.inner.deleteRow();
/*     */   }
/*     */   
/* 289 */   public void refreshRow() throws SQLException { this.inner.refreshRow(); }
/*     */   
/*     */   public void cancelRowUpdates() throws SQLException {
/* 292 */     this.inner.cancelRowUpdates();
/*     */   }
/*     */   
/* 295 */   public void moveToInsertRow() throws SQLException { this.inner.moveToInsertRow(); }
/*     */   
/*     */   public void moveToCurrentRow() throws SQLException {
/* 298 */     this.inner.moveToCurrentRow();
/*     */   }
/*     */   
/* 301 */   public Statement getStatement() throws SQLException { return this.inner.getStatement(); }
/*     */   
/*     */   public Blob getBlob(String a) throws SQLException {
/* 304 */     return this.inner.getBlob(a);
/*     */   }
/*     */   
/* 307 */   public Blob getBlob(int a) throws SQLException { return this.inner.getBlob(a); }
/*     */   
/*     */   public Clob getClob(String a) throws SQLException {
/* 310 */     return this.inner.getClob(a);
/*     */   }
/*     */   
/* 313 */   public Clob getClob(int a) throws SQLException { return this.inner.getClob(a); }
/*     */   
/*     */   public void updateRef(String a, Ref b) throws SQLException {
/* 316 */     this.inner.updateRef(a, b);
/*     */   }
/*     */   
/* 319 */   public void updateRef(int a, Ref b) throws SQLException { this.inner.updateRef(a, b); }
/*     */   
/*     */   public void updateBlob(String a, Blob b) throws SQLException {
/* 322 */     this.inner.updateBlob(a, b);
/*     */   }
/*     */   
/* 325 */   public void updateBlob(int a, Blob b) throws SQLException { this.inner.updateBlob(a, b); }
/*     */   
/*     */   public void updateClob(int a, Clob b) throws SQLException {
/* 328 */     this.inner.updateClob(a, b);
/*     */   }
/*     */   
/* 331 */   public void updateClob(String a, Clob b) throws SQLException { this.inner.updateClob(a, b); }
/*     */   
/*     */   public void updateArray(String a, Array b) throws SQLException {
/* 334 */     this.inner.updateArray(a, b);
/*     */   }
/*     */   
/* 337 */   public void updateArray(int a, Array b) throws SQLException { this.inner.updateArray(a, b); }
/*     */   
/*     */   public Object getObject(int a) throws SQLException {
/* 340 */     return this.inner.getObject(a);
/*     */   }
/*     */   
/* 343 */   public Object getObject(String a, Map b) throws SQLException { return this.inner.getObject(a, b); }
/*     */   
/*     */   public Object getObject(String a) throws SQLException {
/* 346 */     return this.inner.getObject(a);
/*     */   }
/*     */   
/* 349 */   public Object getObject(int a, Map b) throws SQLException { return this.inner.getObject(a, b); }
/*     */   
/*     */   public boolean getBoolean(int a) throws SQLException {
/* 352 */     return this.inner.getBoolean(a);
/*     */   }
/*     */   
/* 355 */   public boolean getBoolean(String a) throws SQLException { return this.inner.getBoolean(a); }
/*     */   
/*     */   public byte getByte(String a) throws SQLException {
/* 358 */     return this.inner.getByte(a);
/*     */   }
/*     */   
/* 361 */   public byte getByte(int a) throws SQLException { return this.inner.getByte(a); }
/*     */   
/*     */   public short getShort(String a) throws SQLException {
/* 364 */     return this.inner.getShort(a);
/*     */   }
/*     */   
/* 367 */   public short getShort(int a) throws SQLException { return this.inner.getShort(a); }
/*     */   
/*     */   public int getInt(String a) throws SQLException {
/* 370 */     return this.inner.getInt(a);
/*     */   }
/*     */   
/* 373 */   public int getInt(int a) throws SQLException { return this.inner.getInt(a); }
/*     */   
/*     */   public long getLong(int a) throws SQLException {
/* 376 */     return this.inner.getLong(a);
/*     */   }
/*     */   
/* 379 */   public long getLong(String a) throws SQLException { return this.inner.getLong(a); }
/*     */   
/*     */   public float getFloat(String a) throws SQLException {
/* 382 */     return this.inner.getFloat(a);
/*     */   }
/*     */   
/* 385 */   public float getFloat(int a) throws SQLException { return this.inner.getFloat(a); }
/*     */   
/*     */   public double getDouble(int a) throws SQLException {
/* 388 */     return this.inner.getDouble(a);
/*     */   }
/*     */   
/* 391 */   public double getDouble(String a) throws SQLException { return this.inner.getDouble(a); }
/*     */   
/*     */   public byte[] getBytes(String a) throws SQLException {
/* 394 */     return this.inner.getBytes(a);
/*     */   }
/*     */   
/* 397 */   public byte[] getBytes(int a) throws SQLException { return this.inner.getBytes(a); }
/*     */   
/*     */   public boolean next() throws SQLException {
/* 400 */     return this.inner.next();
/*     */   }
/*     */   
/* 403 */   public URL getURL(int a) throws SQLException { return this.inner.getURL(a); }
/*     */   
/*     */   public URL getURL(String a) throws SQLException {
/* 406 */     return this.inner.getURL(a);
/*     */   }
/*     */   
/* 409 */   public int getType() throws SQLException { return this.inner.getType(); }
/*     */   
/*     */   public boolean previous() throws SQLException {
/* 412 */     return this.inner.previous();
/*     */   }
/*     */   
/* 415 */   public void close() throws SQLException { this.inner.close(); }
/*     */   
/*     */   public String getString(String a) throws SQLException {
/* 418 */     return this.inner.getString(a);
/*     */   }
/*     */   
/* 421 */   public String getString(int a) throws SQLException { return this.inner.getString(a); }
/*     */   
/*     */   public Ref getRef(String a) throws SQLException {
/* 424 */     return this.inner.getRef(a);
/*     */   }
/*     */   
/* 427 */   public Ref getRef(int a) throws SQLException { return this.inner.getRef(a); }
/*     */   
/*     */   public Time getTime(int a, Calendar b) throws SQLException {
/* 430 */     return this.inner.getTime(a, b);
/*     */   }
/*     */   
/* 433 */   public Time getTime(String a) throws SQLException { return this.inner.getTime(a); }
/*     */   
/*     */   public Time getTime(int a) throws SQLException {
/* 436 */     return this.inner.getTime(a);
/*     */   }
/*     */   
/* 439 */   public Time getTime(String a, Calendar b) throws SQLException { return this.inner.getTime(a, b); }
/*     */   
/*     */   public Date getDate(String a) throws SQLException {
/* 442 */     return this.inner.getDate(a);
/*     */   }
/*     */   
/* 445 */   public Date getDate(int a) throws SQLException { return this.inner.getDate(a); }
/*     */   
/*     */   public Date getDate(int a, Calendar b) throws SQLException {
/* 448 */     return this.inner.getDate(a, b);
/*     */   }
/*     */   
/* 451 */   public Date getDate(String a, Calendar b) throws SQLException { return this.inner.getDate(a, b); }
/*     */   
/*     */   public boolean first() throws SQLException {
/* 454 */     return this.inner.first();
/*     */   }
/*     */   
/* 457 */   public boolean last() throws SQLException { return this.inner.last(); }
/*     */   
/*     */   public Array getArray(String a) throws SQLException {
/* 460 */     return this.inner.getArray(a);
/*     */   }
/*     */   
/* 463 */   public Array getArray(int a) throws SQLException { return this.inner.getArray(a); }
/*     */   
/*     */   public boolean relative(int a) throws SQLException {
/* 466 */     return this.inner.relative(a);
/*     */   }
/*     */   
/* 469 */   public void updateTime(String a, Time b) throws SQLException { this.inner.updateTime(a, b); }
/*     */   
/*     */   public void updateTime(int a, Time b) throws SQLException {
/* 472 */     this.inner.updateTime(a, b);
/*     */   }
/*     */   
/* 475 */   public int findColumn(String a) throws SQLException { return this.inner.findColumn(a); }
/*     */   
/*     */   public int getRow() throws SQLException {
/* 478 */     return this.inner.getRow();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\sql\filter\FilterResultSet.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */