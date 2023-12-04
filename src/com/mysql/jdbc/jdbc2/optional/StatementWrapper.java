/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.ResultSetInternalMethods;
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import com.mysql.jdbc.StatementImpl;
/*     */ import com.mysql.jdbc.Util;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLWarning;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StatementWrapper
/*     */   extends WrapperBase
/*     */   implements java.sql.Statement
/*     */ {
/*     */   private static final Constructor<?> JDBC_4_STATEMENT_WRAPPER_CTOR;
/*     */   protected java.sql.Statement wrappedStmt;
/*     */   protected ConnectionWrapper wrappedConn;
/*     */   
/*     */   static
/*     */   {
/*  44 */     if (Util.isJdbc4()) {
/*     */       try {
/*  46 */         JDBC_4_STATEMENT_WRAPPER_CTOR = Class.forName("com.mysql.jdbc.jdbc2.optional.JDBC4StatementWrapper").getConstructor(new Class[] { ConnectionWrapper.class, MysqlPooledConnection.class, java.sql.Statement.class });
/*     */       }
/*     */       catch (SecurityException e) {
/*  49 */         throw new RuntimeException(e);
/*     */       } catch (NoSuchMethodException e) {
/*  51 */         throw new RuntimeException(e);
/*     */       } catch (ClassNotFoundException e) {
/*  53 */         throw new RuntimeException(e);
/*     */       }
/*     */     } else {
/*  56 */       JDBC_4_STATEMENT_WRAPPER_CTOR = null;
/*     */     }
/*     */   }
/*     */   
/*     */   protected static StatementWrapper getInstance(ConnectionWrapper c, MysqlPooledConnection conn, java.sql.Statement toWrap) throws SQLException {
/*  61 */     if (!Util.isJdbc4()) {
/*  62 */       return new StatementWrapper(c, conn, toWrap);
/*     */     }
/*     */     
/*  65 */     return (StatementWrapper)Util.handleNewInstance(JDBC_4_STATEMENT_WRAPPER_CTOR, new Object[] { c, conn, toWrap }, conn.getExceptionInterceptor());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public StatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, java.sql.Statement toWrap)
/*     */   {
/*  73 */     super(conn);
/*  74 */     this.wrappedStmt = toWrap;
/*  75 */     this.wrappedConn = c;
/*     */   }
/*     */   
/*     */   public Connection getConnection() throws SQLException {
/*     */     try {
/*  80 */       if (this.wrappedStmt != null) {
/*  81 */         return this.wrappedConn;
/*     */       }
/*     */       
/*  84 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/*  86 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/*  89 */     return null;
/*     */   }
/*     */   
/*     */   public void setCursorName(String name) throws SQLException {
/*     */     try {
/*  94 */       if (this.wrappedStmt != null) {
/*  95 */         this.wrappedStmt.setCursorName(name);
/*     */       } else {
/*  97 */         throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 100 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setEscapeProcessing(boolean enable) throws SQLException {
/*     */     try {
/* 106 */       if (this.wrappedStmt != null) {
/* 107 */         this.wrappedStmt.setEscapeProcessing(enable);
/*     */       } else {
/* 109 */         throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 112 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setFetchDirection(int direction) throws SQLException {
/*     */     try {
/* 118 */       if (this.wrappedStmt != null) {
/* 119 */         this.wrappedStmt.setFetchDirection(direction);
/*     */       } else {
/* 121 */         throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 124 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public int getFetchDirection() throws SQLException {
/*     */     try {
/* 130 */       if (this.wrappedStmt != null) {
/* 131 */         return this.wrappedStmt.getFetchDirection();
/*     */       }
/*     */       
/* 134 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 136 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 139 */     return 1000;
/*     */   }
/*     */   
/*     */   public void setFetchSize(int rows) throws SQLException {
/*     */     try {
/* 144 */       if (this.wrappedStmt != null) {
/* 145 */         this.wrappedStmt.setFetchSize(rows);
/*     */       } else {
/* 147 */         throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 150 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public int getFetchSize() throws SQLException {
/*     */     try {
/* 156 */       if (this.wrappedStmt != null) {
/* 157 */         return this.wrappedStmt.getFetchSize();
/*     */       }
/*     */       
/* 160 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 162 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 165 */     return 0;
/*     */   }
/*     */   
/*     */   public ResultSet getGeneratedKeys() throws SQLException {
/*     */     try {
/* 170 */       if (this.wrappedStmt != null) {
/* 171 */         return this.wrappedStmt.getGeneratedKeys();
/*     */       }
/*     */       
/* 174 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 176 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 179 */     return null;
/*     */   }
/*     */   
/*     */   public void setMaxFieldSize(int max) throws SQLException {
/*     */     try {
/* 184 */       if (this.wrappedStmt != null) {
/* 185 */         this.wrappedStmt.setMaxFieldSize(max);
/*     */       } else {
/* 187 */         throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 190 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public int getMaxFieldSize() throws SQLException {
/*     */     try {
/* 196 */       if (this.wrappedStmt != null) {
/* 197 */         return this.wrappedStmt.getMaxFieldSize();
/*     */       }
/*     */       
/* 200 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 202 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 205 */     return 0;
/*     */   }
/*     */   
/*     */   public void setMaxRows(int max) throws SQLException {
/*     */     try {
/* 210 */       if (this.wrappedStmt != null) {
/* 211 */         this.wrappedStmt.setMaxRows(max);
/*     */       } else {
/* 213 */         throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 216 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public int getMaxRows() throws SQLException {
/*     */     try {
/* 222 */       if (this.wrappedStmt != null) {
/* 223 */         return this.wrappedStmt.getMaxRows();
/*     */       }
/*     */       
/* 226 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 228 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 231 */     return 0;
/*     */   }
/*     */   
/*     */   public boolean getMoreResults() throws SQLException {
/*     */     try {
/* 236 */       if (this.wrappedStmt != null) {
/* 237 */         return this.wrappedStmt.getMoreResults();
/*     */       }
/*     */       
/* 240 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 242 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 245 */     return false;
/*     */   }
/*     */   
/*     */   public boolean getMoreResults(int current) throws SQLException {
/*     */     try {
/* 250 */       if (this.wrappedStmt != null) {
/* 251 */         return this.wrappedStmt.getMoreResults(current);
/*     */       }
/*     */       
/* 254 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 256 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 259 */     return false;
/*     */   }
/*     */   
/*     */   public void setQueryTimeout(int seconds) throws SQLException {
/*     */     try {
/* 264 */       if (this.wrappedStmt != null) {
/* 265 */         this.wrappedStmt.setQueryTimeout(seconds);
/*     */       } else {
/* 267 */         throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 270 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public int getQueryTimeout() throws SQLException {
/*     */     try {
/* 276 */       if (this.wrappedStmt != null) {
/* 277 */         return this.wrappedStmt.getQueryTimeout();
/*     */       }
/*     */       
/* 280 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 282 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 285 */     return 0;
/*     */   }
/*     */   
/*     */   public ResultSet getResultSet() throws SQLException {
/*     */     try {
/* 290 */       if (this.wrappedStmt != null) {
/* 291 */         ResultSet rs = this.wrappedStmt.getResultSet();
/*     */         
/* 293 */         if (rs != null) {
/* 294 */           ((ResultSetInternalMethods)rs).setWrapperStatement(this);
/*     */         }
/* 296 */         return rs;
/*     */       }
/*     */       
/* 299 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 301 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 304 */     return null;
/*     */   }
/*     */   
/*     */   public int getResultSetConcurrency() throws SQLException {
/*     */     try {
/* 309 */       if (this.wrappedStmt != null) {
/* 310 */         return this.wrappedStmt.getResultSetConcurrency();
/*     */       }
/*     */       
/* 313 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 315 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 318 */     return 0;
/*     */   }
/*     */   
/*     */   public int getResultSetHoldability() throws SQLException {
/*     */     try {
/* 323 */       if (this.wrappedStmt != null) {
/* 324 */         return this.wrappedStmt.getResultSetHoldability();
/*     */       }
/*     */       
/* 327 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 329 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 332 */     return 1;
/*     */   }
/*     */   
/*     */   public int getResultSetType() throws SQLException {
/*     */     try {
/* 337 */       if (this.wrappedStmt != null) {
/* 338 */         return this.wrappedStmt.getResultSetType();
/*     */       }
/*     */       
/* 341 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 343 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 346 */     return 1003;
/*     */   }
/*     */   
/*     */   public int getUpdateCount() throws SQLException {
/*     */     try {
/* 351 */       if (this.wrappedStmt != null) {
/* 352 */         return this.wrappedStmt.getUpdateCount();
/*     */       }
/*     */       
/* 355 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 357 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 360 */     return -1;
/*     */   }
/*     */   
/*     */   public SQLWarning getWarnings() throws SQLException {
/*     */     try {
/* 365 */       if (this.wrappedStmt != null) {
/* 366 */         return this.wrappedStmt.getWarnings();
/*     */       }
/*     */       
/* 369 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 371 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 374 */     return null;
/*     */   }
/*     */   
/*     */   public void addBatch(String sql) throws SQLException {
/*     */     try {
/* 379 */       if (this.wrappedStmt != null) {
/* 380 */         this.wrappedStmt.addBatch(sql);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 383 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void cancel() throws SQLException {
/*     */     try {
/* 389 */       if (this.wrappedStmt != null) {
/* 390 */         this.wrappedStmt.cancel();
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 393 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void clearBatch() throws SQLException {
/*     */     try {
/* 399 */       if (this.wrappedStmt != null) {
/* 400 */         this.wrappedStmt.clearBatch();
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 403 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void clearWarnings() throws SQLException {
/*     */     try {
/* 409 */       if (this.wrappedStmt != null) {
/* 410 */         this.wrappedStmt.clearWarnings();
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 413 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void close() throws SQLException {
/*     */     try {
/* 419 */       if (this.wrappedStmt != null) {
/* 420 */         this.wrappedStmt.close();
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 423 */       checkAndFireConnectionError(sqlEx);
/*     */     } finally {
/* 425 */       this.wrappedStmt = null;
/* 426 */       this.pooledConnection = null;
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
/*     */     try {
/* 432 */       if (this.wrappedStmt != null) {
/* 433 */         return this.wrappedStmt.execute(sql, autoGeneratedKeys);
/*     */       }
/*     */       
/* 436 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 438 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 441 */     return false;
/*     */   }
/*     */   
/*     */   public boolean execute(String sql, int[] columnIndexes) throws SQLException {
/*     */     try {
/* 446 */       if (this.wrappedStmt != null) {
/* 447 */         return this.wrappedStmt.execute(sql, columnIndexes);
/*     */       }
/*     */       
/* 450 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 452 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 455 */     return false;
/*     */   }
/*     */   
/*     */   public boolean execute(String sql, String[] columnNames) throws SQLException {
/*     */     try {
/* 460 */       if (this.wrappedStmt != null) {
/* 461 */         return this.wrappedStmt.execute(sql, columnNames);
/*     */       }
/*     */       
/* 464 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 466 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 469 */     return false;
/*     */   }
/*     */   
/*     */   public boolean execute(String sql) throws SQLException {
/*     */     try {
/* 474 */       if (this.wrappedStmt != null) {
/* 475 */         return this.wrappedStmt.execute(sql);
/*     */       }
/*     */       
/* 478 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 480 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 483 */     return false;
/*     */   }
/*     */   
/*     */   public int[] executeBatch() throws SQLException {
/*     */     try {
/* 488 */       if (this.wrappedStmt != null) {
/* 489 */         return this.wrappedStmt.executeBatch();
/*     */       }
/*     */       
/* 492 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 494 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 497 */     return null;
/*     */   }
/*     */   
/*     */   public ResultSet executeQuery(String sql) throws SQLException {
/* 501 */     ResultSet rs = null;
/*     */     try {
/* 503 */       if (this.wrappedStmt == null) {
/* 504 */         throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */       }
/* 506 */       rs = this.wrappedStmt.executeQuery(sql);
/* 507 */       ((ResultSetInternalMethods)rs).setWrapperStatement(this);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 510 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 513 */     return rs;
/*     */   }
/*     */   
/*     */   public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
/*     */     try {
/* 518 */       if (this.wrappedStmt != null) {
/* 519 */         return this.wrappedStmt.executeUpdate(sql, autoGeneratedKeys);
/*     */       }
/*     */       
/* 522 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 524 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 527 */     return -1;
/*     */   }
/*     */   
/*     */   public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
/*     */     try {
/* 532 */       if (this.wrappedStmt != null) {
/* 533 */         return this.wrappedStmt.executeUpdate(sql, columnIndexes);
/*     */       }
/*     */       
/* 536 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 538 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 541 */     return -1;
/*     */   }
/*     */   
/*     */   public int executeUpdate(String sql, String[] columnNames) throws SQLException {
/*     */     try {
/* 546 */       if (this.wrappedStmt != null) {
/* 547 */         return this.wrappedStmt.executeUpdate(sql, columnNames);
/*     */       }
/*     */       
/* 550 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 552 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 555 */     return -1;
/*     */   }
/*     */   
/*     */   public int executeUpdate(String sql) throws SQLException {
/*     */     try {
/* 560 */       if (this.wrappedStmt != null) {
/* 561 */         return this.wrappedStmt.executeUpdate(sql);
/*     */       }
/*     */       
/* 564 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 566 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 569 */     return -1;
/*     */   }
/*     */   
/*     */   public void enableStreamingResults() throws SQLException {
/*     */     try {
/* 574 */       if (this.wrappedStmt != null) {
/* 575 */         ((com.mysql.jdbc.Statement)this.wrappedStmt).enableStreamingResults();
/*     */       } else {
/* 577 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 580 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public long[] executeLargeBatch()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 590 */       if (this.wrappedStmt != null) {
/* 591 */         return ((StatementImpl)this.wrappedStmt).executeLargeBatch();
/*     */       }
/*     */       
/* 594 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 596 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 599 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public long executeLargeUpdate(String sql)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 608 */       if (this.wrappedStmt != null) {
/* 609 */         return ((StatementImpl)this.wrappedStmt).executeLargeUpdate(sql);
/*     */       }
/*     */       
/* 612 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 614 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 617 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long executeLargeUpdate(String sql, int autoGeneratedKeys)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 626 */       if (this.wrappedStmt != null) {
/* 627 */         return ((StatementImpl)this.wrappedStmt).executeLargeUpdate(sql, autoGeneratedKeys);
/*     */       }
/*     */       
/* 630 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 632 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 635 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long executeLargeUpdate(String sql, int[] columnIndexes)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 644 */       if (this.wrappedStmt != null) {
/* 645 */         return ((StatementImpl)this.wrappedStmt).executeLargeUpdate(sql, columnIndexes);
/*     */       }
/*     */       
/* 648 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 650 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 653 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long executeLargeUpdate(String sql, String[] columnNames)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 662 */       if (this.wrappedStmt != null) {
/* 663 */         return ((StatementImpl)this.wrappedStmt).executeLargeUpdate(sql, columnNames);
/*     */       }
/*     */       
/* 666 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 668 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 671 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLargeMaxRows()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 680 */       if (this.wrappedStmt != null) {
/* 681 */         return ((StatementImpl)this.wrappedStmt).getLargeMaxRows();
/*     */       }
/*     */       
/* 684 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 686 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 689 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLargeUpdateCount()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 698 */       if (this.wrappedStmt != null) {
/* 699 */         return ((StatementImpl)this.wrappedStmt).getLargeUpdateCount();
/*     */       }
/*     */       
/* 702 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     } catch (SQLException sqlEx) {
/* 704 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 707 */     return -1L;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setLargeMaxRows(long max)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 716 */       if (this.wrappedStmt != null) {
/* 717 */         ((StatementImpl)this.wrappedStmt).setLargeMaxRows(max);
/*     */       } else {
/* 719 */         throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 722 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\jdbc2\optional\StatementWrapper.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */