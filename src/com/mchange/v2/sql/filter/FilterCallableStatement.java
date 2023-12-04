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
/*     */ public abstract class FilterCallableStatement
/*     */   implements CallableStatement
/*     */ {
/*     */   protected CallableStatement inner;
/*     */   
/*     */   public FilterCallableStatement(CallableStatement inner)
/*     */   {
/*  54 */     this.inner = inner;
/*     */   }
/*     */   
/*     */   public FilterCallableStatement() {}
/*     */   
/*     */   public void setInner(CallableStatement inner) {
/*  60 */     this.inner = inner;
/*     */   }
/*     */   
/*  63 */   public CallableStatement getInner() { return this.inner; }
/*     */   
/*     */   public boolean wasNull() throws SQLException {
/*  66 */     return this.inner.wasNull();
/*     */   }
/*     */   
/*  69 */   public BigDecimal getBigDecimal(int a, int b) throws SQLException { return this.inner.getBigDecimal(a, b); }
/*     */   
/*     */   public BigDecimal getBigDecimal(int a) throws SQLException {
/*  72 */     return this.inner.getBigDecimal(a);
/*     */   }
/*     */   
/*  75 */   public BigDecimal getBigDecimal(String a) throws SQLException { return this.inner.getBigDecimal(a); }
/*     */   
/*     */   public Timestamp getTimestamp(String a) throws SQLException {
/*  78 */     return this.inner.getTimestamp(a);
/*     */   }
/*     */   
/*  81 */   public Timestamp getTimestamp(String a, Calendar b) throws SQLException { return this.inner.getTimestamp(a, b); }
/*     */   
/*     */   public Timestamp getTimestamp(int a, Calendar b) throws SQLException {
/*  84 */     return this.inner.getTimestamp(a, b);
/*     */   }
/*     */   
/*  87 */   public Timestamp getTimestamp(int a) throws SQLException { return this.inner.getTimestamp(a); }
/*     */   
/*     */   public Blob getBlob(String a) throws SQLException {
/*  90 */     return this.inner.getBlob(a);
/*     */   }
/*     */   
/*  93 */   public Blob getBlob(int a) throws SQLException { return this.inner.getBlob(a); }
/*     */   
/*     */   public Clob getClob(String a) throws SQLException {
/*  96 */     return this.inner.getClob(a);
/*     */   }
/*     */   
/*  99 */   public Clob getClob(int a) throws SQLException { return this.inner.getClob(a); }
/*     */   
/*     */   public void setNull(String a, int b, String c) throws SQLException {
/* 102 */     this.inner.setNull(a, b, c);
/*     */   }
/*     */   
/* 105 */   public void setNull(String a, int b) throws SQLException { this.inner.setNull(a, b); }
/*     */   
/*     */   public void setBigDecimal(String a, BigDecimal b) throws SQLException {
/* 108 */     this.inner.setBigDecimal(a, b);
/*     */   }
/*     */   
/* 111 */   public void setBytes(String a, byte[] b) throws SQLException { this.inner.setBytes(a, b); }
/*     */   
/*     */   public void setTimestamp(String a, Timestamp b, Calendar c) throws SQLException {
/* 114 */     this.inner.setTimestamp(a, b, c);
/*     */   }
/*     */   
/* 117 */   public void setTimestamp(String a, Timestamp b) throws SQLException { this.inner.setTimestamp(a, b); }
/*     */   
/*     */   public void setAsciiStream(String a, InputStream b, int c) throws SQLException {
/* 120 */     this.inner.setAsciiStream(a, b, c);
/*     */   }
/*     */   
/* 123 */   public void setBinaryStream(String a, InputStream b, int c) throws SQLException { this.inner.setBinaryStream(a, b, c); }
/*     */   
/*     */   public void setObject(String a, Object b) throws SQLException {
/* 126 */     this.inner.setObject(a, b);
/*     */   }
/*     */   
/* 129 */   public void setObject(String a, Object b, int c, int d) throws SQLException { this.inner.setObject(a, b, c, d); }
/*     */   
/*     */   public void setObject(String a, Object b, int c) throws SQLException {
/* 132 */     this.inner.setObject(a, b, c);
/*     */   }
/*     */   
/* 135 */   public void setCharacterStream(String a, Reader b, int c) throws SQLException { this.inner.setCharacterStream(a, b, c); }
/*     */   
/*     */   public void registerOutParameter(String a, int b) throws SQLException {
/* 138 */     this.inner.registerOutParameter(a, b);
/*     */   }
/*     */   
/* 141 */   public void registerOutParameter(int a, int b) throws SQLException { this.inner.registerOutParameter(a, b); }
/*     */   
/*     */   public void registerOutParameter(int a, int b, int c) throws SQLException {
/* 144 */     this.inner.registerOutParameter(a, b, c);
/*     */   }
/*     */   
/* 147 */   public void registerOutParameter(int a, int b, String c) throws SQLException { this.inner.registerOutParameter(a, b, c); }
/*     */   
/*     */   public void registerOutParameter(String a, int b, int c) throws SQLException {
/* 150 */     this.inner.registerOutParameter(a, b, c);
/*     */   }
/*     */   
/* 153 */   public void registerOutParameter(String a, int b, String c) throws SQLException { this.inner.registerOutParameter(a, b, c); }
/*     */   
/*     */   public Object getObject(String a, Map b) throws SQLException {
/* 156 */     return this.inner.getObject(a, b);
/*     */   }
/*     */   
/* 159 */   public Object getObject(int a, Map b) throws SQLException { return this.inner.getObject(a, b); }
/*     */   
/*     */   public Object getObject(int a) throws SQLException {
/* 162 */     return this.inner.getObject(a);
/*     */   }
/*     */   
/* 165 */   public Object getObject(String a) throws SQLException { return this.inner.getObject(a); }
/*     */   
/*     */   public boolean getBoolean(int a) throws SQLException {
/* 168 */     return this.inner.getBoolean(a);
/*     */   }
/*     */   
/* 171 */   public boolean getBoolean(String a) throws SQLException { return this.inner.getBoolean(a); }
/*     */   
/*     */   public byte getByte(String a) throws SQLException {
/* 174 */     return this.inner.getByte(a);
/*     */   }
/*     */   
/* 177 */   public byte getByte(int a) throws SQLException { return this.inner.getByte(a); }
/*     */   
/*     */   public short getShort(int a) throws SQLException {
/* 180 */     return this.inner.getShort(a);
/*     */   }
/*     */   
/* 183 */   public short getShort(String a) throws SQLException { return this.inner.getShort(a); }
/*     */   
/*     */   public int getInt(String a) throws SQLException {
/* 186 */     return this.inner.getInt(a);
/*     */   }
/*     */   
/* 189 */   public int getInt(int a) throws SQLException { return this.inner.getInt(a); }
/*     */   
/*     */   public long getLong(int a) throws SQLException {
/* 192 */     return this.inner.getLong(a);
/*     */   }
/*     */   
/* 195 */   public long getLong(String a) throws SQLException { return this.inner.getLong(a); }
/*     */   
/*     */   public float getFloat(String a) throws SQLException {
/* 198 */     return this.inner.getFloat(a);
/*     */   }
/*     */   
/* 201 */   public float getFloat(int a) throws SQLException { return this.inner.getFloat(a); }
/*     */   
/*     */   public double getDouble(String a) throws SQLException {
/* 204 */     return this.inner.getDouble(a);
/*     */   }
/*     */   
/* 207 */   public double getDouble(int a) throws SQLException { return this.inner.getDouble(a); }
/*     */   
/*     */   public byte[] getBytes(int a) throws SQLException {
/* 210 */     return this.inner.getBytes(a);
/*     */   }
/*     */   
/* 213 */   public byte[] getBytes(String a) throws SQLException { return this.inner.getBytes(a); }
/*     */   
/*     */   public URL getURL(String a) throws SQLException {
/* 216 */     return this.inner.getURL(a);
/*     */   }
/*     */   
/* 219 */   public URL getURL(int a) throws SQLException { return this.inner.getURL(a); }
/*     */   
/*     */   public void setBoolean(String a, boolean b) throws SQLException {
/* 222 */     this.inner.setBoolean(a, b);
/*     */   }
/*     */   
/* 225 */   public void setByte(String a, byte b) throws SQLException { this.inner.setByte(a, b); }
/*     */   
/*     */   public void setShort(String a, short b) throws SQLException {
/* 228 */     this.inner.setShort(a, b);
/*     */   }
/*     */   
/* 231 */   public void setInt(String a, int b) throws SQLException { this.inner.setInt(a, b); }
/*     */   
/*     */   public void setLong(String a, long b) throws SQLException {
/* 234 */     this.inner.setLong(a, b);
/*     */   }
/*     */   
/* 237 */   public void setFloat(String a, float b) throws SQLException { this.inner.setFloat(a, b); }
/*     */   
/*     */   public void setDouble(String a, double b) throws SQLException {
/* 240 */     this.inner.setDouble(a, b);
/*     */   }
/*     */   
/* 243 */   public String getString(String a) throws SQLException { return this.inner.getString(a); }
/*     */   
/*     */   public String getString(int a) throws SQLException {
/* 246 */     return this.inner.getString(a);
/*     */   }
/*     */   
/* 249 */   public Ref getRef(int a) throws SQLException { return this.inner.getRef(a); }
/*     */   
/*     */   public Ref getRef(String a) throws SQLException {
/* 252 */     return this.inner.getRef(a);
/*     */   }
/*     */   
/* 255 */   public void setURL(String a, URL b) throws SQLException { this.inner.setURL(a, b); }
/*     */   
/*     */   public void setTime(String a, Time b) throws SQLException {
/* 258 */     this.inner.setTime(a, b);
/*     */   }
/*     */   
/* 261 */   public void setTime(String a, Time b, Calendar c) throws SQLException { this.inner.setTime(a, b, c); }
/*     */   
/*     */   public Time getTime(int a, Calendar b) throws SQLException {
/* 264 */     return this.inner.getTime(a, b);
/*     */   }
/*     */   
/* 267 */   public Time getTime(String a) throws SQLException { return this.inner.getTime(a); }
/*     */   
/*     */   public Time getTime(int a) throws SQLException {
/* 270 */     return this.inner.getTime(a);
/*     */   }
/*     */   
/* 273 */   public Time getTime(String a, Calendar b) throws SQLException { return this.inner.getTime(a, b); }
/*     */   
/*     */   public Date getDate(int a, Calendar b) throws SQLException {
/* 276 */     return this.inner.getDate(a, b);
/*     */   }
/*     */   
/* 279 */   public Date getDate(String a) throws SQLException { return this.inner.getDate(a); }
/*     */   
/*     */   public Date getDate(int a) throws SQLException {
/* 282 */     return this.inner.getDate(a);
/*     */   }
/*     */   
/* 285 */   public Date getDate(String a, Calendar b) throws SQLException { return this.inner.getDate(a, b); }
/*     */   
/*     */   public void setString(String a, String b) throws SQLException {
/* 288 */     this.inner.setString(a, b);
/*     */   }
/*     */   
/* 291 */   public Array getArray(int a) throws SQLException { return this.inner.getArray(a); }
/*     */   
/*     */   public Array getArray(String a) throws SQLException {
/* 294 */     return this.inner.getArray(a);
/*     */   }
/*     */   
/* 297 */   public void setDate(String a, Date b, Calendar c) throws SQLException { this.inner.setDate(a, b, c); }
/*     */   
/*     */   public void setDate(String a, Date b) throws SQLException {
/* 300 */     this.inner.setDate(a, b);
/*     */   }
/*     */   
/* 303 */   public ResultSetMetaData getMetaData() throws SQLException { return this.inner.getMetaData(); }
/*     */   
/*     */   public ResultSet executeQuery() throws SQLException {
/* 306 */     return this.inner.executeQuery();
/*     */   }
/*     */   
/* 309 */   public int executeUpdate() throws SQLException { return this.inner.executeUpdate(); }
/*     */   
/*     */   public void addBatch() throws SQLException {
/* 312 */     this.inner.addBatch();
/*     */   }
/*     */   
/* 315 */   public void setNull(int a, int b, String c) throws SQLException { this.inner.setNull(a, b, c); }
/*     */   
/*     */   public void setNull(int a, int b) throws SQLException {
/* 318 */     this.inner.setNull(a, b);
/*     */   }
/*     */   
/* 321 */   public void setBigDecimal(int a, BigDecimal b) throws SQLException { this.inner.setBigDecimal(a, b); }
/*     */   
/*     */   public void setBytes(int a, byte[] b) throws SQLException {
/* 324 */     this.inner.setBytes(a, b);
/*     */   }
/*     */   
/* 327 */   public void setTimestamp(int a, Timestamp b, Calendar c) throws SQLException { this.inner.setTimestamp(a, b, c); }
/*     */   
/*     */   public void setTimestamp(int a, Timestamp b) throws SQLException {
/* 330 */     this.inner.setTimestamp(a, b);
/*     */   }
/*     */   
/* 333 */   public void setAsciiStream(int a, InputStream b, int c) throws SQLException { this.inner.setAsciiStream(a, b, c); }
/*     */   
/*     */   public void setUnicodeStream(int a, InputStream b, int c) throws SQLException {
/* 336 */     this.inner.setUnicodeStream(a, b, c);
/*     */   }
/*     */   
/* 339 */   public void setBinaryStream(int a, InputStream b, int c) throws SQLException { this.inner.setBinaryStream(a, b, c); }
/*     */   
/*     */   public void clearParameters() throws SQLException {
/* 342 */     this.inner.clearParameters();
/*     */   }
/*     */   
/* 345 */   public void setObject(int a, Object b) throws SQLException { this.inner.setObject(a, b); }
/*     */   
/*     */   public void setObject(int a, Object b, int c, int d) throws SQLException {
/* 348 */     this.inner.setObject(a, b, c, d);
/*     */   }
/*     */   
/* 351 */   public void setObject(int a, Object b, int c) throws SQLException { this.inner.setObject(a, b, c); }
/*     */   
/*     */   public void setCharacterStream(int a, Reader b, int c) throws SQLException {
/* 354 */     this.inner.setCharacterStream(a, b, c);
/*     */   }
/*     */   
/* 357 */   public void setRef(int a, Ref b) throws SQLException { this.inner.setRef(a, b); }
/*     */   
/*     */   public void setBlob(int a, Blob b) throws SQLException {
/* 360 */     this.inner.setBlob(a, b);
/*     */   }
/*     */   
/* 363 */   public void setClob(int a, Clob b) throws SQLException { this.inner.setClob(a, b); }
/*     */   
/*     */   public void setArray(int a, Array b) throws SQLException {
/* 366 */     this.inner.setArray(a, b);
/*     */   }
/*     */   
/* 369 */   public ParameterMetaData getParameterMetaData() throws SQLException { return this.inner.getParameterMetaData(); }
/*     */   
/*     */   public void setBoolean(int a, boolean b) throws SQLException {
/* 372 */     this.inner.setBoolean(a, b);
/*     */   }
/*     */   
/* 375 */   public void setByte(int a, byte b) throws SQLException { this.inner.setByte(a, b); }
/*     */   
/*     */   public void setShort(int a, short b) throws SQLException {
/* 378 */     this.inner.setShort(a, b);
/*     */   }
/*     */   
/* 381 */   public void setInt(int a, int b) throws SQLException { this.inner.setInt(a, b); }
/*     */   
/*     */   public void setLong(int a, long b) throws SQLException {
/* 384 */     this.inner.setLong(a, b);
/*     */   }
/*     */   
/* 387 */   public void setFloat(int a, float b) throws SQLException { this.inner.setFloat(a, b); }
/*     */   
/*     */   public void setDouble(int a, double b) throws SQLException {
/* 390 */     this.inner.setDouble(a, b);
/*     */   }
/*     */   
/* 393 */   public void setURL(int a, URL b) throws SQLException { this.inner.setURL(a, b); }
/*     */   
/*     */   public void setTime(int a, Time b) throws SQLException {
/* 396 */     this.inner.setTime(a, b);
/*     */   }
/*     */   
/* 399 */   public void setTime(int a, Time b, Calendar c) throws SQLException { this.inner.setTime(a, b, c); }
/*     */   
/*     */   public boolean execute() throws SQLException {
/* 402 */     return this.inner.execute();
/*     */   }
/*     */   
/* 405 */   public void setString(int a, String b) throws SQLException { this.inner.setString(a, b); }
/*     */   
/*     */   public void setDate(int a, Date b, Calendar c) throws SQLException {
/* 408 */     this.inner.setDate(a, b, c);
/*     */   }
/*     */   
/* 411 */   public void setDate(int a, Date b) throws SQLException { this.inner.setDate(a, b); }
/*     */   
/*     */   public SQLWarning getWarnings() throws SQLException {
/* 414 */     return this.inner.getWarnings();
/*     */   }
/*     */   
/* 417 */   public void clearWarnings() throws SQLException { this.inner.clearWarnings(); }
/*     */   
/*     */   public void setFetchDirection(int a) throws SQLException {
/* 420 */     this.inner.setFetchDirection(a);
/*     */   }
/*     */   
/* 423 */   public int getFetchDirection() throws SQLException { return this.inner.getFetchDirection(); }
/*     */   
/*     */   public void setFetchSize(int a) throws SQLException {
/* 426 */     this.inner.setFetchSize(a);
/*     */   }
/*     */   
/* 429 */   public int getFetchSize() throws SQLException { return this.inner.getFetchSize(); }
/*     */   
/*     */   public int getResultSetHoldability() throws SQLException {
/* 432 */     return this.inner.getResultSetHoldability();
/*     */   }
/*     */   
/* 435 */   public ResultSet executeQuery(String a) throws SQLException { return this.inner.executeQuery(a); }
/*     */   
/*     */   public int executeUpdate(String a, int b) throws SQLException {
/* 438 */     return this.inner.executeUpdate(a, b);
/*     */   }
/*     */   
/* 441 */   public int executeUpdate(String a, String[] b) throws SQLException { return this.inner.executeUpdate(a, b); }
/*     */   
/*     */   public int executeUpdate(String a, int[] b) throws SQLException {
/* 444 */     return this.inner.executeUpdate(a, b);
/*     */   }
/*     */   
/* 447 */   public int executeUpdate(String a) throws SQLException { return this.inner.executeUpdate(a); }
/*     */   
/*     */   public int getMaxFieldSize() throws SQLException {
/* 450 */     return this.inner.getMaxFieldSize();
/*     */   }
/*     */   
/* 453 */   public void setMaxFieldSize(int a) throws SQLException { this.inner.setMaxFieldSize(a); }
/*     */   
/*     */   public int getMaxRows() throws SQLException {
/* 456 */     return this.inner.getMaxRows();
/*     */   }
/*     */   
/* 459 */   public void setMaxRows(int a) throws SQLException { this.inner.setMaxRows(a); }
/*     */   
/*     */   public void setEscapeProcessing(boolean a) throws SQLException {
/* 462 */     this.inner.setEscapeProcessing(a);
/*     */   }
/*     */   
/* 465 */   public int getQueryTimeout() throws SQLException { return this.inner.getQueryTimeout(); }
/*     */   
/*     */   public void setQueryTimeout(int a) throws SQLException {
/* 468 */     this.inner.setQueryTimeout(a);
/*     */   }
/*     */   
/* 471 */   public void setCursorName(String a) throws SQLException { this.inner.setCursorName(a); }
/*     */   
/*     */   public ResultSet getResultSet() throws SQLException {
/* 474 */     return this.inner.getResultSet();
/*     */   }
/*     */   
/* 477 */   public int getUpdateCount() throws SQLException { return this.inner.getUpdateCount(); }
/*     */   
/*     */   public boolean getMoreResults() throws SQLException {
/* 480 */     return this.inner.getMoreResults();
/*     */   }
/*     */   
/* 483 */   public boolean getMoreResults(int a) throws SQLException { return this.inner.getMoreResults(a); }
/*     */   
/*     */   public int getResultSetConcurrency() throws SQLException {
/* 486 */     return this.inner.getResultSetConcurrency();
/*     */   }
/*     */   
/* 489 */   public int getResultSetType() throws SQLException { return this.inner.getResultSetType(); }
/*     */   
/*     */   public void addBatch(String a) throws SQLException {
/* 492 */     this.inner.addBatch(a);
/*     */   }
/*     */   
/* 495 */   public void clearBatch() throws SQLException { this.inner.clearBatch(); }
/*     */   
/*     */   public int[] executeBatch() throws SQLException {
/* 498 */     return this.inner.executeBatch();
/*     */   }
/*     */   
/* 501 */   public ResultSet getGeneratedKeys() throws SQLException { return this.inner.getGeneratedKeys(); }
/*     */   
/*     */   public void close() throws SQLException {
/* 504 */     this.inner.close();
/*     */   }
/*     */   
/* 507 */   public boolean execute(String a, int b) throws SQLException { return this.inner.execute(a, b); }
/*     */   
/*     */   public boolean execute(String a) throws SQLException {
/* 510 */     return this.inner.execute(a);
/*     */   }
/*     */   
/* 513 */   public boolean execute(String a, int[] b) throws SQLException { return this.inner.execute(a, b); }
/*     */   
/*     */   public boolean execute(String a, String[] b) throws SQLException {
/* 516 */     return this.inner.execute(a, b);
/*     */   }
/*     */   
/* 519 */   public Connection getConnection() throws SQLException { return this.inner.getConnection(); }
/*     */   
/*     */   public void cancel() throws SQLException {
/* 522 */     this.inner.cancel();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\sql\filter\FilterCallableStatement.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */