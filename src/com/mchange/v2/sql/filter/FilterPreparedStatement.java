/*     */ package com.mchange.v2.sql.filter;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.math.BigDecimal;
/*     */ import java.net.URL;
/*     */ import java.sql.Array;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.Connection;
/*     */ import java.sql.Date;
/*     */ import java.sql.ParameterMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.Ref;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLWarning;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Calendar;
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
/*     */ public abstract class FilterPreparedStatement
/*     */   implements PreparedStatement
/*     */ {
/*     */   protected PreparedStatement inner;
/*     */   
/*     */   public FilterPreparedStatement(PreparedStatement inner)
/*     */   {
/*  53 */     this.inner = inner;
/*     */   }
/*     */   
/*     */   public FilterPreparedStatement() {}
/*     */   
/*     */   public void setInner(PreparedStatement inner) {
/*  59 */     this.inner = inner;
/*     */   }
/*     */   
/*  62 */   public PreparedStatement getInner() { return this.inner; }
/*     */   
/*     */   public ResultSetMetaData getMetaData() throws SQLException {
/*  65 */     return this.inner.getMetaData();
/*     */   }
/*     */   
/*  68 */   public ResultSet executeQuery() throws SQLException { return this.inner.executeQuery(); }
/*     */   
/*     */   public int executeUpdate() throws SQLException {
/*  71 */     return this.inner.executeUpdate();
/*     */   }
/*     */   
/*  74 */   public void addBatch() throws SQLException { this.inner.addBatch(); }
/*     */   
/*     */   public void setNull(int a, int b, String c) throws SQLException {
/*  77 */     this.inner.setNull(a, b, c);
/*     */   }
/*     */   
/*  80 */   public void setNull(int a, int b) throws SQLException { this.inner.setNull(a, b); }
/*     */   
/*     */   public void setBigDecimal(int a, BigDecimal b) throws SQLException {
/*  83 */     this.inner.setBigDecimal(a, b);
/*     */   }
/*     */   
/*  86 */   public void setBytes(int a, byte[] b) throws SQLException { this.inner.setBytes(a, b); }
/*     */   
/*     */   public void setTimestamp(int a, Timestamp b, Calendar c) throws SQLException {
/*  89 */     this.inner.setTimestamp(a, b, c);
/*     */   }
/*     */   
/*  92 */   public void setTimestamp(int a, Timestamp b) throws SQLException { this.inner.setTimestamp(a, b); }
/*     */   
/*     */   public void setAsciiStream(int a, InputStream b, int c) throws SQLException {
/*  95 */     this.inner.setAsciiStream(a, b, c);
/*     */   }
/*     */   
/*  98 */   public void setUnicodeStream(int a, InputStream b, int c) throws SQLException { this.inner.setUnicodeStream(a, b, c); }
/*     */   
/*     */   public void setBinaryStream(int a, InputStream b, int c) throws SQLException {
/* 101 */     this.inner.setBinaryStream(a, b, c);
/*     */   }
/*     */   
/* 104 */   public void clearParameters() throws SQLException { this.inner.clearParameters(); }
/*     */   
/*     */   public void setObject(int a, Object b) throws SQLException {
/* 107 */     this.inner.setObject(a, b);
/*     */   }
/*     */   
/* 110 */   public void setObject(int a, Object b, int c, int d) throws SQLException { this.inner.setObject(a, b, c, d); }
/*     */   
/*     */   public void setObject(int a, Object b, int c) throws SQLException {
/* 113 */     this.inner.setObject(a, b, c);
/*     */   }
/*     */   
/* 116 */   public void setCharacterStream(int a, Reader b, int c) throws SQLException { this.inner.setCharacterStream(a, b, c); }
/*     */   
/*     */   public void setRef(int a, Ref b) throws SQLException {
/* 119 */     this.inner.setRef(a, b);
/*     */   }
/*     */   
/* 122 */   public void setBlob(int a, Blob b) throws SQLException { this.inner.setBlob(a, b); }
/*     */   
/*     */   public void setClob(int a, Clob b) throws SQLException {
/* 125 */     this.inner.setClob(a, b);
/*     */   }
/*     */   
/* 128 */   public void setArray(int a, Array b) throws SQLException { this.inner.setArray(a, b); }
/*     */   
/*     */   public ParameterMetaData getParameterMetaData() throws SQLException {
/* 131 */     return this.inner.getParameterMetaData();
/*     */   }
/*     */   
/* 134 */   public void setBoolean(int a, boolean b) throws SQLException { this.inner.setBoolean(a, b); }
/*     */   
/*     */   public void setByte(int a, byte b) throws SQLException {
/* 137 */     this.inner.setByte(a, b);
/*     */   }
/*     */   
/* 140 */   public void setShort(int a, short b) throws SQLException { this.inner.setShort(a, b); }
/*     */   
/*     */   public void setInt(int a, int b) throws SQLException {
/* 143 */     this.inner.setInt(a, b);
/*     */   }
/*     */   
/* 146 */   public void setLong(int a, long b) throws SQLException { this.inner.setLong(a, b); }
/*     */   
/*     */   public void setFloat(int a, float b) throws SQLException {
/* 149 */     this.inner.setFloat(a, b);
/*     */   }
/*     */   
/* 152 */   public void setDouble(int a, double b) throws SQLException { this.inner.setDouble(a, b); }
/*     */   
/*     */   public void setURL(int a, URL b) throws SQLException {
/* 155 */     this.inner.setURL(a, b);
/*     */   }
/*     */   
/* 158 */   public void setTime(int a, Time b) throws SQLException { this.inner.setTime(a, b); }
/*     */   
/*     */   public void setTime(int a, Time b, Calendar c) throws SQLException {
/* 161 */     this.inner.setTime(a, b, c);
/*     */   }
/*     */   
/* 164 */   public boolean execute() throws SQLException { return this.inner.execute(); }
/*     */   
/*     */   public void setString(int a, String b) throws SQLException {
/* 167 */     this.inner.setString(a, b);
/*     */   }
/*     */   
/* 170 */   public void setDate(int a, Date b, Calendar c) throws SQLException { this.inner.setDate(a, b, c); }
/*     */   
/*     */   public void setDate(int a, Date b) throws SQLException {
/* 173 */     this.inner.setDate(a, b);
/*     */   }
/*     */   
/* 176 */   public SQLWarning getWarnings() throws SQLException { return this.inner.getWarnings(); }
/*     */   
/*     */   public void clearWarnings() throws SQLException {
/* 179 */     this.inner.clearWarnings();
/*     */   }
/*     */   
/* 182 */   public void setFetchDirection(int a) throws SQLException { this.inner.setFetchDirection(a); }
/*     */   
/*     */   public int getFetchDirection() throws SQLException {
/* 185 */     return this.inner.getFetchDirection();
/*     */   }
/*     */   
/* 188 */   public void setFetchSize(int a) throws SQLException { this.inner.setFetchSize(a); }
/*     */   
/*     */   public int getFetchSize() throws SQLException {
/* 191 */     return this.inner.getFetchSize();
/*     */   }
/*     */   
/* 194 */   public int getResultSetHoldability() throws SQLException { return this.inner.getResultSetHoldability(); }
/*     */   
/*     */   public ResultSet executeQuery(String a) throws SQLException {
/* 197 */     return this.inner.executeQuery(a);
/*     */   }
/*     */   
/* 200 */   public int executeUpdate(String a, int b) throws SQLException { return this.inner.executeUpdate(a, b); }
/*     */   
/*     */   public int executeUpdate(String a, String[] b) throws SQLException {
/* 203 */     return this.inner.executeUpdate(a, b);
/*     */   }
/*     */   
/* 206 */   public int executeUpdate(String a, int[] b) throws SQLException { return this.inner.executeUpdate(a, b); }
/*     */   
/*     */   public int executeUpdate(String a) throws SQLException {
/* 209 */     return this.inner.executeUpdate(a);
/*     */   }
/*     */   
/* 212 */   public int getMaxFieldSize() throws SQLException { return this.inner.getMaxFieldSize(); }
/*     */   
/*     */   public void setMaxFieldSize(int a) throws SQLException {
/* 215 */     this.inner.setMaxFieldSize(a);
/*     */   }
/*     */   
/* 218 */   public int getMaxRows() throws SQLException { return this.inner.getMaxRows(); }
/*     */   
/*     */   public void setMaxRows(int a) throws SQLException {
/* 221 */     this.inner.setMaxRows(a);
/*     */   }
/*     */   
/* 224 */   public void setEscapeProcessing(boolean a) throws SQLException { this.inner.setEscapeProcessing(a); }
/*     */   
/*     */   public int getQueryTimeout() throws SQLException {
/* 227 */     return this.inner.getQueryTimeout();
/*     */   }
/*     */   
/* 230 */   public void setQueryTimeout(int a) throws SQLException { this.inner.setQueryTimeout(a); }
/*     */   
/*     */   public void setCursorName(String a) throws SQLException {
/* 233 */     this.inner.setCursorName(a);
/*     */   }
/*     */   
/* 236 */   public ResultSet getResultSet() throws SQLException { return this.inner.getResultSet(); }
/*     */   
/*     */   public int getUpdateCount() throws SQLException {
/* 239 */     return this.inner.getUpdateCount();
/*     */   }
/*     */   
/* 242 */   public boolean getMoreResults() throws SQLException { return this.inner.getMoreResults(); }
/*     */   
/*     */   public boolean getMoreResults(int a) throws SQLException {
/* 245 */     return this.inner.getMoreResults(a);
/*     */   }
/*     */   
/* 248 */   public int getResultSetConcurrency() throws SQLException { return this.inner.getResultSetConcurrency(); }
/*     */   
/*     */   public int getResultSetType() throws SQLException {
/* 251 */     return this.inner.getResultSetType();
/*     */   }
/*     */   
/* 254 */   public void addBatch(String a) throws SQLException { this.inner.addBatch(a); }
/*     */   
/*     */   public void clearBatch() throws SQLException {
/* 257 */     this.inner.clearBatch();
/*     */   }
/*     */   
/* 260 */   public int[] executeBatch() throws SQLException { return this.inner.executeBatch(); }
/*     */   
/*     */   public ResultSet getGeneratedKeys() throws SQLException {
/* 263 */     return this.inner.getGeneratedKeys();
/*     */   }
/*     */   
/* 266 */   public void close() throws SQLException { this.inner.close(); }
/*     */   
/*     */   public boolean execute(String a, int b) throws SQLException {
/* 269 */     return this.inner.execute(a, b);
/*     */   }
/*     */   
/* 272 */   public boolean execute(String a) throws SQLException { return this.inner.execute(a); }
/*     */   
/*     */   public boolean execute(String a, int[] b) throws SQLException {
/* 275 */     return this.inner.execute(a, b);
/*     */   }
/*     */   
/* 278 */   public boolean execute(String a, String[] b) throws SQLException { return this.inner.execute(a, b); }
/*     */   
/*     */   public Connection getConnection() throws SQLException {
/* 281 */     return this.inner.getConnection();
/*     */   }
/*     */   
/* 284 */   public void cancel() throws SQLException { this.inner.cancel(); }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\sql\filter\FilterPreparedStatement.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */