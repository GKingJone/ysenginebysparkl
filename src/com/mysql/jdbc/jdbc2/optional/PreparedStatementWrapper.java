/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.ResultSetInternalMethods;
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import com.mysql.jdbc.Util;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.math.BigDecimal;
/*     */ import java.net.URL;
/*     */ import java.sql.Array;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.Date;
/*     */ import java.sql.ParameterMetaData;
/*     */ import java.sql.Ref;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
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
/*     */ public class PreparedStatementWrapper
/*     */   extends StatementWrapper
/*     */   implements java.sql.PreparedStatement
/*     */ {
/*     */   private static final Constructor<?> JDBC_4_PREPARED_STATEMENT_WRAPPER_CTOR;
/*     */   
/*     */   static
/*     */   {
/*  55 */     if (Util.isJdbc4()) {
/*     */       try {
/*  57 */         String jdbc4ClassName = Util.isJdbc42() ? "com.mysql.jdbc.jdbc2.optional.JDBC42PreparedStatementWrapper" : "com.mysql.jdbc.jdbc2.optional.JDBC4PreparedStatementWrapper";
/*     */         
/*  59 */         JDBC_4_PREPARED_STATEMENT_WRAPPER_CTOR = Class.forName(jdbc4ClassName).getConstructor(new Class[] { ConnectionWrapper.class, MysqlPooledConnection.class, java.sql.PreparedStatement.class });
/*     */       }
/*     */       catch (SecurityException e) {
/*  62 */         throw new RuntimeException(e);
/*     */       } catch (NoSuchMethodException e) {
/*  64 */         throw new RuntimeException(e);
/*     */       } catch (ClassNotFoundException e) {
/*  66 */         throw new RuntimeException(e);
/*     */       }
/*     */     } else {
/*  69 */       JDBC_4_PREPARED_STATEMENT_WRAPPER_CTOR = null;
/*     */     }
/*     */   }
/*     */   
/*     */   protected static PreparedStatementWrapper getInstance(ConnectionWrapper c, MysqlPooledConnection conn, java.sql.PreparedStatement toWrap) throws SQLException {
/*  74 */     if (!Util.isJdbc4()) {
/*  75 */       return new PreparedStatementWrapper(c, conn, toWrap);
/*     */     }
/*     */     
/*  78 */     return (PreparedStatementWrapper)Util.handleNewInstance(JDBC_4_PREPARED_STATEMENT_WRAPPER_CTOR, new Object[] { c, conn, toWrap }, conn.getExceptionInterceptor());
/*     */   }
/*     */   
/*     */   PreparedStatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, java.sql.PreparedStatement toWrap)
/*     */   {
/*  83 */     super(c, conn, toWrap);
/*     */   }
/*     */   
/*     */   public void setArray(int parameterIndex, Array x) throws SQLException {
/*     */     try {
/*  88 */       if (this.wrappedStmt != null) {
/*  89 */         ((java.sql.PreparedStatement)this.wrappedStmt).setArray(parameterIndex, x);
/*     */       } else {
/*  91 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/*  94 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
/*     */     try {
/* 100 */       if (this.wrappedStmt != null) {
/* 101 */         ((java.sql.PreparedStatement)this.wrappedStmt).setAsciiStream(parameterIndex, x, length);
/*     */       } else {
/* 103 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 106 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
/*     */     try {
/* 112 */       if (this.wrappedStmt != null) {
/* 113 */         ((java.sql.PreparedStatement)this.wrappedStmt).setBigDecimal(parameterIndex, x);
/*     */       } else {
/* 115 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 118 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
/*     */     try {
/* 124 */       if (this.wrappedStmt != null) {
/* 125 */         ((java.sql.PreparedStatement)this.wrappedStmt).setBinaryStream(parameterIndex, x, length);
/*     */       } else {
/* 127 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 130 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setBlob(int parameterIndex, Blob x) throws SQLException {
/*     */     try {
/* 136 */       if (this.wrappedStmt != null) {
/* 137 */         ((java.sql.PreparedStatement)this.wrappedStmt).setBlob(parameterIndex, x);
/*     */       } else {
/* 139 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 142 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setBoolean(int parameterIndex, boolean x) throws SQLException {
/*     */     try {
/* 148 */       if (this.wrappedStmt != null) {
/* 149 */         ((java.sql.PreparedStatement)this.wrappedStmt).setBoolean(parameterIndex, x);
/*     */       } else {
/* 151 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 154 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setByte(int parameterIndex, byte x) throws SQLException {
/*     */     try {
/* 160 */       if (this.wrappedStmt != null) {
/* 161 */         ((java.sql.PreparedStatement)this.wrappedStmt).setByte(parameterIndex, x);
/*     */       } else {
/* 163 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 166 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setBytes(int parameterIndex, byte[] x) throws SQLException {
/*     */     try {
/* 172 */       if (this.wrappedStmt != null) {
/* 173 */         ((java.sql.PreparedStatement)this.wrappedStmt).setBytes(parameterIndex, x);
/*     */       } else {
/* 175 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 178 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
/*     */     try {
/* 184 */       if (this.wrappedStmt != null) {
/* 185 */         ((java.sql.PreparedStatement)this.wrappedStmt).setCharacterStream(parameterIndex, reader, length);
/*     */       } else {
/* 187 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 190 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setClob(int parameterIndex, Clob x) throws SQLException {
/*     */     try {
/* 196 */       if (this.wrappedStmt != null) {
/* 197 */         ((java.sql.PreparedStatement)this.wrappedStmt).setClob(parameterIndex, x);
/*     */       } else {
/* 199 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 202 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setDate(int parameterIndex, Date x) throws SQLException {
/*     */     try {
/* 208 */       if (this.wrappedStmt != null) {
/* 209 */         ((java.sql.PreparedStatement)this.wrappedStmt).setDate(parameterIndex, x);
/*     */       } else {
/* 211 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 214 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
/*     */     try {
/* 220 */       if (this.wrappedStmt != null) {
/* 221 */         ((java.sql.PreparedStatement)this.wrappedStmt).setDate(parameterIndex, x, cal);
/*     */       } else {
/* 223 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 226 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setDouble(int parameterIndex, double x) throws SQLException {
/*     */     try {
/* 232 */       if (this.wrappedStmt != null) {
/* 233 */         ((java.sql.PreparedStatement)this.wrappedStmt).setDouble(parameterIndex, x);
/*     */       } else {
/* 235 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 238 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setFloat(int parameterIndex, float x) throws SQLException {
/*     */     try {
/* 244 */       if (this.wrappedStmt != null) {
/* 245 */         ((java.sql.PreparedStatement)this.wrappedStmt).setFloat(parameterIndex, x);
/*     */       } else {
/* 247 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 250 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setInt(int parameterIndex, int x) throws SQLException {
/*     */     try {
/* 256 */       if (this.wrappedStmt != null) {
/* 257 */         ((java.sql.PreparedStatement)this.wrappedStmt).setInt(parameterIndex, x);
/*     */       } else {
/* 259 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 262 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setLong(int parameterIndex, long x) throws SQLException {
/*     */     try {
/* 268 */       if (this.wrappedStmt != null) {
/* 269 */         ((java.sql.PreparedStatement)this.wrappedStmt).setLong(parameterIndex, x);
/*     */       } else {
/* 271 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 274 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public ResultSetMetaData getMetaData() throws SQLException {
/*     */     try {
/* 280 */       if (this.wrappedStmt != null) {
/* 281 */         return ((java.sql.PreparedStatement)this.wrappedStmt).getMetaData();
/*     */       }
/*     */       
/* 284 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 286 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 289 */     return null;
/*     */   }
/*     */   
/*     */   public void setNull(int parameterIndex, int sqlType) throws SQLException {
/*     */     try {
/* 294 */       if (this.wrappedStmt != null) {
/* 295 */         ((java.sql.PreparedStatement)this.wrappedStmt).setNull(parameterIndex, sqlType);
/*     */       } else {
/* 297 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 300 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
/*     */     try {
/* 306 */       if (this.wrappedStmt != null) {
/* 307 */         ((java.sql.PreparedStatement)this.wrappedStmt).setNull(parameterIndex, sqlType, typeName);
/*     */       } else {
/* 309 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 312 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setObject(int parameterIndex, Object x) throws SQLException {
/*     */     try {
/* 318 */       if (this.wrappedStmt != null) {
/* 319 */         ((java.sql.PreparedStatement)this.wrappedStmt).setObject(parameterIndex, x);
/*     */       } else {
/* 321 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 324 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
/*     */     try {
/* 330 */       if (this.wrappedStmt != null) {
/* 331 */         ((java.sql.PreparedStatement)this.wrappedStmt).setObject(parameterIndex, x, targetSqlType);
/*     */       } else {
/* 333 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 336 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
/*     */     try {
/* 342 */       if (this.wrappedStmt != null) {
/* 343 */         ((java.sql.PreparedStatement)this.wrappedStmt).setObject(parameterIndex, x, targetSqlType, scale);
/*     */       } else {
/* 345 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 348 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public ParameterMetaData getParameterMetaData() throws SQLException {
/*     */     try {
/* 354 */       if (this.wrappedStmt != null) {
/* 355 */         return ((java.sql.PreparedStatement)this.wrappedStmt).getParameterMetaData();
/*     */       }
/*     */       
/* 358 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 360 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 363 */     return null;
/*     */   }
/*     */   
/*     */   public void setRef(int parameterIndex, Ref x) throws SQLException {
/*     */     try {
/* 368 */       if (this.wrappedStmt != null) {
/* 369 */         ((java.sql.PreparedStatement)this.wrappedStmt).setRef(parameterIndex, x);
/*     */       } else {
/* 371 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 374 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setShort(int parameterIndex, short x) throws SQLException {
/*     */     try {
/* 380 */       if (this.wrappedStmt != null) {
/* 381 */         ((java.sql.PreparedStatement)this.wrappedStmt).setShort(parameterIndex, x);
/*     */       } else {
/* 383 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 386 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setString(int parameterIndex, String x) throws SQLException {
/*     */     try {
/* 392 */       if (this.wrappedStmt != null) {
/* 393 */         ((java.sql.PreparedStatement)this.wrappedStmt).setString(parameterIndex, x);
/*     */       } else {
/* 395 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 398 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setTime(int parameterIndex, Time x) throws SQLException {
/*     */     try {
/* 404 */       if (this.wrappedStmt != null) {
/* 405 */         ((java.sql.PreparedStatement)this.wrappedStmt).setTime(parameterIndex, x);
/*     */       } else {
/* 407 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 410 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
/*     */     try {
/* 416 */       if (this.wrappedStmt != null) {
/* 417 */         ((java.sql.PreparedStatement)this.wrappedStmt).setTime(parameterIndex, x, cal);
/*     */       } else {
/* 419 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 422 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
/*     */     try {
/* 428 */       if (this.wrappedStmt != null) {
/* 429 */         ((java.sql.PreparedStatement)this.wrappedStmt).setTimestamp(parameterIndex, x);
/*     */       } else {
/* 431 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 434 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
/*     */     try {
/* 440 */       if (this.wrappedStmt != null) {
/* 441 */         ((java.sql.PreparedStatement)this.wrappedStmt).setTimestamp(parameterIndex, x, cal);
/*     */       } else {
/* 443 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 446 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setURL(int parameterIndex, URL x) throws SQLException {
/*     */     try {
/* 452 */       if (this.wrappedStmt != null) {
/* 453 */         ((java.sql.PreparedStatement)this.wrappedStmt).setURL(parameterIndex, x);
/*     */       } else {
/* 455 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 458 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public void setUnicodeStream(int parameterIndex, InputStream x, int length)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 475 */       if (this.wrappedStmt != null) {
/* 476 */         ((java.sql.PreparedStatement)this.wrappedStmt).setUnicodeStream(parameterIndex, x, length);
/*     */       } else {
/* 478 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 481 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addBatch() throws SQLException {
/*     */     try {
/* 487 */       if (this.wrappedStmt != null) {
/* 488 */         ((java.sql.PreparedStatement)this.wrappedStmt).addBatch();
/*     */       } else {
/* 490 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 493 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void clearParameters() throws SQLException {
/*     */     try {
/* 499 */       if (this.wrappedStmt != null) {
/* 500 */         ((java.sql.PreparedStatement)this.wrappedStmt).clearParameters();
/*     */       } else {
/* 502 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 505 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean execute() throws SQLException {
/*     */     try {
/* 511 */       if (this.wrappedStmt != null) {
/* 512 */         return ((java.sql.PreparedStatement)this.wrappedStmt).execute();
/*     */       }
/*     */       
/* 515 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 517 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 520 */     return false;
/*     */   }
/*     */   
/*     */   public ResultSet executeQuery() throws SQLException
/*     */   {
/* 525 */     ResultSet rs = null;
/*     */     try {
/* 527 */       if (this.wrappedStmt == null) {
/* 528 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/* 530 */       rs = ((java.sql.PreparedStatement)this.wrappedStmt).executeQuery();
/* 531 */       ((ResultSetInternalMethods)rs).setWrapperStatement(this);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 534 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 537 */     return rs;
/*     */   }
/*     */   
/*     */   public int executeUpdate() throws SQLException {
/*     */     try {
/* 542 */       if (this.wrappedStmt != null) {
/* 543 */         return ((java.sql.PreparedStatement)this.wrappedStmt).executeUpdate();
/*     */       }
/*     */       
/* 546 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 548 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 551 */     return -1;
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 556 */     StringBuilder buf = new StringBuilder(super.toString());
/*     */     
/* 558 */     if (this.wrappedStmt != null) {
/* 559 */       buf.append(": ");
/*     */       try {
/* 561 */         buf.append(((com.mysql.jdbc.PreparedStatement)this.wrappedStmt).asSql());
/*     */       } catch (SQLException sqlEx) {
/* 563 */         buf.append("EXCEPTION: " + sqlEx.toString());
/*     */       }
/*     */     }
/*     */     
/* 567 */     return buf.toString();
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
/*     */   public long executeLargeUpdate()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 914 */       if (this.wrappedStmt != null) {
/* 915 */         return ((com.mysql.jdbc.PreparedStatement)this.wrappedStmt).executeLargeUpdate();
/*     */       }
/*     */       
/* 918 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 920 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 923 */     return -1L;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\jdbc2\optional\PreparedStatementWrapper.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */