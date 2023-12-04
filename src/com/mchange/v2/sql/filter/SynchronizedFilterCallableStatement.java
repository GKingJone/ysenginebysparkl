/*     */ package com.mchange.v2.sql.filter;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.math.BigDecimal;
/*     */ import java.net.URL;
/*     */ import java.sql.Array;
/*     */ import java.sql.Blob;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Clob;
/*     */ import java.sql.Connection;
/*     */ import java.sql.Date;
/*     */ import java.sql.ParameterMetaData;
/*     */ import java.sql.Ref;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLWarning;
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
/*     */ public abstract class SynchronizedFilterCallableStatement
/*     */   implements CallableStatement
/*     */ {
/*     */   protected CallableStatement inner;
/*     */   
/*     */   public SynchronizedFilterCallableStatement(CallableStatement inner)
/*     */   {
/*  54 */     this.inner = inner;
/*     */   }
/*     */   
/*     */   public SynchronizedFilterCallableStatement() {}
/*     */   
/*     */   public synchronized void setInner(CallableStatement inner) {
/*  60 */     this.inner = inner;
/*     */   }
/*     */   
/*  63 */   public synchronized CallableStatement getInner() { return this.inner; }
/*     */   
/*     */   public synchronized boolean wasNull() throws SQLException {
/*  66 */     return this.inner.wasNull();
/*     */   }
/*     */   
/*  69 */   public synchronized BigDecimal getBigDecimal(int a, int b) throws SQLException { return this.inner.getBigDecimal(a, b); }
/*     */   
/*     */   public synchronized BigDecimal getBigDecimal(int a) throws SQLException {
/*  72 */     return this.inner.getBigDecimal(a);
/*     */   }
/*     */   
/*  75 */   public synchronized BigDecimal getBigDecimal(String a) throws SQLException { return this.inner.getBigDecimal(a); }
/*     */   
/*     */   public synchronized Timestamp getTimestamp(String a) throws SQLException {
/*  78 */     return this.inner.getTimestamp(a);
/*     */   }
/*     */   
/*  81 */   public synchronized Timestamp getTimestamp(String a, Calendar b) throws SQLException { return this.inner.getTimestamp(a, b); }
/*     */   
/*     */   public synchronized Timestamp getTimestamp(int a, Calendar b) throws SQLException {
/*  84 */     return this.inner.getTimestamp(a, b);
/*     */   }
/*     */   
/*  87 */   public synchronized Timestamp getTimestamp(int a) throws SQLException { return this.inner.getTimestamp(a); }
/*     */   
/*     */   public synchronized Blob getBlob(String a) throws SQLException {
/*  90 */     return this.inner.getBlob(a);
/*     */   }
/*     */   
/*  93 */   public synchronized Blob getBlob(int a) throws SQLException { return this.inner.getBlob(a); }
/*     */   
/*     */   public synchronized Clob getClob(String a) throws SQLException {
/*  96 */     return this.inner.getClob(a);
/*     */   }
/*     */   
/*  99 */   public synchronized Clob getClob(int a) throws SQLException { return this.inner.getClob(a); }
/*     */   
/*     */   public synchronized void setNull(String a, int b, String c) throws SQLException {
/* 102 */     this.inner.setNull(a, b, c);
/*     */   }
/*     */   
/* 105 */   public synchronized void setNull(String a, int b) throws SQLException { this.inner.setNull(a, b); }
/*     */   
/*     */   public synchronized void setBigDecimal(String a, BigDecimal b) throws SQLException {
/* 108 */     this.inner.setBigDecimal(a, b);
/*     */   }
/*     */   
/* 111 */   public synchronized void setBytes(String a, byte[] b) throws SQLException { this.inner.setBytes(a, b); }
/*     */   
/*     */   public synchronized void setTimestamp(String a, Timestamp b, Calendar c) throws SQLException {
/* 114 */     this.inner.setTimestamp(a, b, c);
/*     */   }
/*     */   
/* 117 */   public synchronized void setTimestamp(String a, Timestamp b) throws SQLException { this.inner.setTimestamp(a, b); }
/*     */   
/*     */   public synchronized void setAsciiStream(String a, InputStream b, int c) throws SQLException {
/* 120 */     this.inner.setAsciiStream(a, b, c);
/*     */   }
/*     */   
/* 123 */   public synchronized void setBinaryStream(String a, InputStream b, int c) throws SQLException { this.inner.setBinaryStream(a, b, c); }
/*     */   
/*     */   public synchronized void setObject(String a, Object b) throws SQLException {
/* 126 */     this.inner.setObject(a, b);
/*     */   }
/*     */   
/* 129 */   public synchronized void setObject(String a, Object b, int c, int d) throws SQLException { this.inner.setObject(a, b, c, d); }
/*     */   
/*     */   public synchronized void setObject(String a, Object b, int c) throws SQLException {
/* 132 */     this.inner.setObject(a, b, c);
/*     */   }
/*     */   
/* 135 */   public synchronized void setCharacterStream(String a, Reader b, int c) throws SQLException { this.inner.setCharacterStream(a, b, c); }
/*     */   
/*     */   public synchronized void registerOutParameter(String a, int b) throws SQLException {
/* 138 */     this.inner.registerOutParameter(a, b);
/*     */   }
/*     */   
/* 141 */   public synchronized void registerOutParameter(int a, int b) throws SQLException { this.inner.registerOutParameter(a, b); }
/*     */   
/*     */   public synchronized void registerOutParameter(int a, int b, int c) throws SQLException {
/* 144 */     this.inner.registerOutParameter(a, b, c);
/*     */   }
/*     */   
/* 147 */   public synchronized void registerOutParameter(int a, int b, String c) throws SQLException { this.inner.registerOutParameter(a, b, c); }
/*     */   
/*     */   public synchronized void registerOutParameter(String a, int b, int c) throws SQLException {
/* 150 */     this.inner.registerOutParameter(a, b, c);
/*     */   }
/*     */   
/* 153 */   public synchronized void registerOutParameter(String a, int b, String c) throws SQLException { this.inner.registerOutParameter(a, b, c); }
/*     */   
/*     */   public synchronized Object getObject(String a, Map b) throws SQLException {
/* 156 */     return this.inner.getObject(a, b);
/*     */   }
/*     */   
/* 159 */   public synchronized Object getObject(int a, Map b) throws SQLException { return this.inner.getObject(a, b); }
/*     */   
/*     */   public synchronized Object getObject(int a) throws SQLException {
/* 162 */     return this.inner.getObject(a);
/*     */   }
/*     */   
/* 165 */   public synchronized Object getObject(String a) throws SQLException { return this.inner.getObject(a); }
/*     */   
/*     */   public synchronized boolean getBoolean(int a) throws SQLException {
/* 168 */     return this.inner.getBoolean(a);
/*     */   }
/*     */   
/* 171 */   public synchronized boolean getBoolean(String a) throws SQLException { return this.inner.getBoolean(a); }
/*     */   
/*     */   public synchronized byte getByte(String a) throws SQLException {
/* 174 */     return this.inner.getByte(a);
/*     */   }
/*     */   
/* 177 */   public synchronized byte getByte(int a) throws SQLException { return this.inner.getByte(a); }
/*     */   
/*     */   public synchronized short getShort(int a) throws SQLException {
/* 180 */     return this.inner.getShort(a);
/*     */   }
/*     */   
/* 183 */   public synchronized short getShort(String a) throws SQLException { return this.inner.getShort(a); }
/*     */   
/*     */   public synchronized int getInt(String a) throws SQLException {
/* 186 */     return this.inner.getInt(a);
/*     */   }
/*     */   
/* 189 */   public synchronized int getInt(int a) throws SQLException { return this.inner.getInt(a); }
/*     */   
/*     */   public synchronized long getLong(int a) throws SQLException {
/* 192 */     return this.inner.getLong(a);
/*     */   }
/*     */   
/* 195 */   public synchronized long getLong(String a) throws SQLException { return this.inner.getLong(a); }
/*     */   
/*     */   public synchronized float getFloat(String a) throws SQLException {
/* 198 */     return this.inner.getFloat(a);
/*     */   }
/*     */   
/* 201 */   public synchronized float getFloat(int a) throws SQLException { return this.inner.getFloat(a); }
/*     */   
/*     */   public synchronized double getDouble(String a) throws SQLException {
/* 204 */     return this.inner.getDouble(a);
/*     */   }
/*     */   
/* 207 */   public synchronized double getDouble(int a) throws SQLException { return this.inner.getDouble(a); }
/*     */   
/*     */   public synchronized byte[] getBytes(int a) throws SQLException {
/* 210 */     return this.inner.getBytes(a);
/*     */   }
/*     */   
/* 213 */   public synchronized byte[] getBytes(String a) throws SQLException { return this.inner.getBytes(a); }
/*     */   
/*     */   public synchronized URL getURL(String a) throws SQLException {
/* 216 */     return this.inner.getURL(a);
/*     */   }
/*     */   
/* 219 */   public synchronized URL getURL(int a) throws SQLException { return this.inner.getURL(a); }
/*     */   
/*     */   public synchronized void setBoolean(String a, boolean b) throws SQLException {
/* 222 */     this.inner.setBoolean(a, b);
/*     */   }
/*     */   
/* 225 */   public synchronized void setByte(String a, byte b) throws SQLException { this.inner.setByte(a, b); }
/*     */   
/*     */   public synchronized void setShort(String a, short b) throws SQLException {
/* 228 */     this.inner.setShort(a, b);
/*     */   }
/*     */   
/* 231 */   public synchronized void setInt(String a, int b) throws SQLException { this.inner.setInt(a, b); }
/*     */   
/*     */   public synchronized void setLong(String a, long b) throws SQLException {
/* 234 */     this.inner.setLong(a, b);
/*     */   }
/*     */   
/* 237 */   public synchronized void setFloat(String a, float b) throws SQLException { this.inner.setFloat(a, b); }
/*     */   
/*     */   public synchronized void setDouble(String a, double b) throws SQLException {
/* 240 */     this.inner.setDouble(a, b);
/*     */   }
/*     */   
/* 243 */   public synchronized String getString(String a) throws SQLException { return this.inner.getString(a); }
/*     */   
/*     */   public synchronized String getString(int a) throws SQLException {
/* 246 */     return this.inner.getString(a);
/*     */   }
/*     */   
/* 249 */   public synchronized Ref getRef(int a) throws SQLException { return this.inner.getRef(a); }
/*     */   
/*     */   public synchronized Ref getRef(String a) throws SQLException {
/* 252 */     return this.inner.getRef(a);
/*     */   }
/*     */   
/* 255 */   public synchronized void setURL(String a, URL b) throws SQLException { this.inner.setURL(a, b); }
/*     */   
/*     */   public synchronized void setTime(String a, Time b) throws SQLException {
/* 258 */     this.inner.setTime(a, b);
/*     */   }
/*     */   
/* 261 */   public synchronized void setTime(String a, Time b, Calendar c) throws SQLException { this.inner.setTime(a, b, c); }
/*     */   
/*     */   public synchronized Time getTime(int a, Calendar b) throws SQLException {
/* 264 */     return this.inner.getTime(a, b);
/*     */   }
/*     */   
/* 267 */   public synchronized Time getTime(String a) throws SQLException { return this.inner.getTime(a); }
/*     */   
/*     */   public synchronized Time getTime(int a) throws SQLException {
/* 270 */     return this.inner.getTime(a);
/*     */   }
/*     */   
/* 273 */   public synchronized Time getTime(String a, Calendar b) throws SQLException { return this.inner.getTime(a, b); }
/*     */   
/*     */   public synchronized Date getDate(int a, Calendar b) throws SQLException {
/* 276 */     return this.inner.getDate(a, b);
/*     */   }
/*     */   
/* 279 */   public synchronized Date getDate(String a) throws SQLException { return this.inner.getDate(a); }
/*     */   
/*     */   public synchronized Date getDate(int a) throws SQLException {
/* 282 */     return this.inner.getDate(a);
/*     */   }
/*     */   
/* 285 */   public synchronized Date getDate(String a, Calendar b) throws SQLException { return this.inner.getDate(a, b); }
/*     */   
/*     */   public synchronized void setString(String a, String b) throws SQLException {
/* 288 */     this.inner.setString(a, b);
/*     */   }
/*     */   
/* 291 */   public synchronized Array getArray(int a) throws SQLException { return this.inner.getArray(a); }
/*     */   
/*     */   public synchronized Array getArray(String a) throws SQLException {
/* 294 */     return this.inner.getArray(a);
/*     */   }
/*     */   
/* 297 */   public synchronized void setDate(String a, Date b, Calendar c) throws SQLException { this.inner.setDate(a, b, c); }
/*     */   
/*     */   public synchronized void setDate(String a, Date b) throws SQLException {
/* 300 */     this.inner.setDate(a, b);
/*     */   }
/*     */   
/* 303 */   public synchronized ResultSetMetaData getMetaData() throws SQLException { return this.inner.getMetaData(); }
/*     */   
/*     */   public synchronized ResultSet executeQuery() throws SQLException {
/* 306 */     return this.inner.executeQuery();
/*     */   }
/*     */   
/* 309 */   public synchronized int executeUpdate() throws SQLException { return this.inner.executeUpdate(); }
/*     */   
/*     */   public synchronized void addBatch() throws SQLException {
/* 312 */     this.inner.addBatch();
/*     */   }
/*     */   
/* 315 */   public synchronized void setNull(int a, int b, String c) throws SQLException { this.inner.setNull(a, b, c); }
/*     */   
/*     */   public synchronized void setNull(int a, int b) throws SQLException {
/* 318 */     this.inner.setNull(a, b);
/*     */   }
/*     */   
/* 321 */   public synchronized void setBigDecimal(int a, BigDecimal b) throws SQLException { this.inner.setBigDecimal(a, b); }
/*     */   
/*     */   public synchronized void setBytes(int a, byte[] b) throws SQLException {
/* 324 */     this.inner.setBytes(a, b);
/*     */   }
/*     */   
/* 327 */   public synchronized void setTimestamp(int a, Timestamp b, Calendar c) throws SQLException { this.inner.setTimestamp(a, b, c); }
/*     */   
/*     */   public synchronized void setTimestamp(int a, Timestamp b) throws SQLException {
/* 330 */     this.inner.setTimestamp(a, b);
/*     */   }
/*     */   
/* 333 */   public synchronized void setAsciiStream(int a, InputStream b, int c) throws SQLException { this.inner.setAsciiStream(a, b, c); }
/*     */   
/*     */   public synchronized void setUnicodeStream(int a, InputStream b, int c) throws SQLException {
/* 336 */     this.inner.setUnicodeStream(a, b, c);
/*     */   }
/*     */   
/* 339 */   public synchronized void setBinaryStream(int a, InputStream b, int c) throws SQLException { this.inner.setBinaryStream(a, b, c); }
/*     */   
/*     */   public synchronized void clearParameters() throws SQLException {
/* 342 */     this.inner.clearParameters();
/*     */   }
/*     */   
/* 345 */   public synchronized void setObject(int a, Object b) throws SQLException { this.inner.setObject(a, b); }
/*     */   
/*     */   public synchronized void setObject(int a, Object b, int c, int d) throws SQLException {
/* 348 */     this.inner.setObject(a, b, c, d);
/*     */   }
/*     */   
/* 351 */   public synchronized void setObject(int a, Object b, int c) throws SQLException { this.inner.setObject(a, b, c); }
/*     */   
/*     */   public synchronized void setCharacterStream(int a, Reader b, int c) throws SQLException {
/* 354 */     this.inner.setCharacterStream(a, b, c);
/*     */   }
/*     */   
/* 357 */   public synchronized void setRef(int a, Ref b) throws SQLException { this.inner.setRef(a, b); }
/*     */   
/*     */   public synchronized void setBlob(int a, Blob b) throws SQLException {
/* 360 */     this.inner.setBlob(a, b);
/*     */   }
/*     */   
/* 363 */   public synchronized void setClob(int a, Clob b) throws SQLException { this.inner.setClob(a, b); }
/*     */   
/*     */   public synchronized void setArray(int a, Array b) throws SQLException {
/* 366 */     this.inner.setArray(a, b);
/*     */   }
/*     */   
/* 369 */   public synchronized ParameterMetaData getParameterMetaData() throws SQLException { return this.inner.getParameterMetaData(); }
/*     */   
/*     */   public synchronized void setBoolean(int a, boolean b) throws SQLException {
/* 372 */     this.inner.setBoolean(a, b);
/*     */   }
/*     */   
/* 375 */   public synchronized void setByte(int a, byte b) throws SQLException { this.inner.setByte(a, b); }
/*     */   
/*     */   public synchronized void setShort(int a, short b) throws SQLException {
/* 378 */     this.inner.setShort(a, b);
/*     */   }
/*     */   
/* 381 */   public synchronized void setInt(int a, int b) throws SQLException { this.inner.setInt(a, b); }
/*     */   
/*     */   public synchronized void setLong(int a, long b) throws SQLException {
/* 384 */     this.inner.setLong(a, b);
/*     */   }
/*     */   
/* 387 */   public synchronized void setFloat(int a, float b) throws SQLException { this.inner.setFloat(a, b); }
/*     */   
/*     */   public synchronized void setDouble(int a, double b) throws SQLException {
/* 390 */     this.inner.setDouble(a, b);
/*     */   }
/*     */   
/* 393 */   public synchronized void setURL(int a, URL b) throws SQLException { this.inner.setURL(a, b); }
/*     */   
/*     */   public synchronized void setTime(int a, Time b) throws SQLException {
/* 396 */     this.inner.setTime(a, b);
/*     */   }
/*     */   
/* 399 */   public synchronized void setTime(int a, Time b, Calendar c) throws SQLException { this.inner.setTime(a, b, c); }
/*     */   
/*     */   public synchronized boolean execute() throws SQLException {
/* 402 */     return this.inner.execute();
/*     */   }
/*     */   
/* 405 */   public synchronized void setString(int a, String b) throws SQLException { this.inner.setString(a, b); }
/*     */   
/*     */   public synchronized void setDate(int a, Date b, Calendar c) throws SQLException {
/* 408 */     this.inner.setDate(a, b, c);
/*     */   }
/*     */   
/* 411 */   public synchronized void setDate(int a, Date b) throws SQLException { this.inner.setDate(a, b); }
/*     */   
/*     */   public synchronized SQLWarning getWarnings() throws SQLException {
/* 414 */     return this.inner.getWarnings();
/*     */   }
/*     */   
/* 417 */   public synchronized void clearWarnings() throws SQLException { this.inner.clearWarnings(); }
/*     */   
/*     */   public synchronized void setFetchDirection(int a) throws SQLException {
/* 420 */     this.inner.setFetchDirection(a);
/*     */   }
/*     */   
/* 423 */   public synchronized int getFetchDirection() throws SQLException { return this.inner.getFetchDirection(); }
/*     */   
/*     */   public synchronized void setFetchSize(int a) throws SQLException {
/* 426 */     this.inner.setFetchSize(a);
/*     */   }
/*     */   
/* 429 */   public synchronized int getFetchSize() throws SQLException { return this.inner.getFetchSize(); }
/*     */   
/*     */   public synchronized int getResultSetHoldability() throws SQLException {
/* 432 */     return this.inner.getResultSetHoldability();
/*     */   }
/*     */   
/* 435 */   public synchronized ResultSet executeQuery(String a) throws SQLException { return this.inner.executeQuery(a); }
/*     */   
/*     */   public synchronized int executeUpdate(String a, int b) throws SQLException {
/* 438 */     return this.inner.executeUpdate(a, b);
/*     */   }
/*     */   
/* 441 */   public synchronized int executeUpdate(String a, String[] b) throws SQLException { return this.inner.executeUpdate(a, b); }
/*     */   
/*     */   public synchronized int executeUpdate(String a, int[] b) throws SQLException {
/* 444 */     return this.inner.executeUpdate(a, b);
/*     */   }
/*     */   
/* 447 */   public synchronized int executeUpdate(String a) throws SQLException { return this.inner.executeUpdate(a); }
/*     */   
/*     */   public synchronized int getMaxFieldSize() throws SQLException {
/* 450 */     return this.inner.getMaxFieldSize();
/*     */   }
/*     */   
/* 453 */   public synchronized void setMaxFieldSize(int a) throws SQLException { this.inner.setMaxFieldSize(a); }
/*     */   
/*     */   public synchronized int getMaxRows() throws SQLException {
/* 456 */     return this.inner.getMaxRows();
/*     */   }
/*     */   
/* 459 */   public synchronized void setMaxRows(int a) throws SQLException { this.inner.setMaxRows(a); }
/*     */   
/*     */   public synchronized void setEscapeProcessing(boolean a) throws SQLException {
/* 462 */     this.inner.setEscapeProcessing(a);
/*     */   }
/*     */   
/* 465 */   public synchronized int getQueryTimeout() throws SQLException { return this.inner.getQueryTimeout(); }
/*     */   
/*     */   public synchronized void setQueryTimeout(int a) throws SQLException {
/* 468 */     this.inner.setQueryTimeout(a);
/*     */   }
/*     */   
/* 471 */   public synchronized void setCursorName(String a) throws SQLException { this.inner.setCursorName(a); }
/*     */   
/*     */   public synchronized ResultSet getResultSet() throws SQLException {
/* 474 */     return this.inner.getResultSet();
/*     */   }
/*     */   
/* 477 */   public synchronized int getUpdateCount() throws SQLException { return this.inner.getUpdateCount(); }
/*     */   
/*     */   public synchronized boolean getMoreResults() throws SQLException {
/* 480 */     return this.inner.getMoreResults();
/*     */   }
/*     */   
/* 483 */   public synchronized boolean getMoreResults(int a) throws SQLException { return this.inner.getMoreResults(a); }
/*     */   
/*     */   public synchronized int getResultSetConcurrency() throws SQLException {
/* 486 */     return this.inner.getResultSetConcurrency();
/*     */   }
/*     */   
/* 489 */   public synchronized int getResultSetType() throws SQLException { return this.inner.getResultSetType(); }
/*     */   
/*     */   public synchronized void addBatch(String a) throws SQLException {
/* 492 */     this.inner.addBatch(a);
/*     */   }
/*     */   
/* 495 */   public synchronized void clearBatch() throws SQLException { this.inner.clearBatch(); }
/*     */   
/*     */   public synchronized int[] executeBatch() throws SQLException {
/* 498 */     return this.inner.executeBatch();
/*     */   }
/*     */   
/* 501 */   public synchronized ResultSet getGeneratedKeys() throws SQLException { return this.inner.getGeneratedKeys(); }
/*     */   
/*     */   public synchronized void close() throws SQLException {
/* 504 */     this.inner.close();
/*     */   }
/*     */   
/* 507 */   public synchronized boolean execute(String a, int b) throws SQLException { return this.inner.execute(a, b); }
/*     */   
/*     */   public synchronized boolean execute(String a) throws SQLException {
/* 510 */     return this.inner.execute(a);
/*     */   }
/*     */   
/* 513 */   public synchronized boolean execute(String a, int[] b) throws SQLException { return this.inner.execute(a, b); }
/*     */   
/*     */   public synchronized boolean execute(String a, String[] b) throws SQLException {
/* 516 */     return this.inner.execute(a, b);
/*     */   }
/*     */   
/* 519 */   public synchronized Connection getConnection() throws SQLException { return this.inner.getConnection(); }
/*     */   
/*     */   public synchronized void cancel() throws SQLException {
/* 522 */     this.inner.cancel();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\sql\filter\SynchronizedFilterCallableStatement.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */