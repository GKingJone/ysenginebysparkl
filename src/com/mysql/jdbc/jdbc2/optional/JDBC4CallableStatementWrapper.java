/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.Blob;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Clob;
/*     */ import java.sql.NClob;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.RowId;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLXML;
/*     */ import java.sql.Statement;
/*     */ import java.util.HashMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JDBC4CallableStatementWrapper
/*     */   extends CallableStatementWrapper
/*     */ {
/*     */   public JDBC4CallableStatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, CallableStatement toWrap)
/*     */   {
/*  58 */     super(c, conn, toWrap);
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public void close()
/*     */     throws SQLException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokespecial 2	com/mysql/jdbc/jdbc2/optional/CallableStatementWrapper:close	()V
/*     */     //   4: aload_0
/*     */     //   5: aconst_null
/*     */     //   6: putfield 3	com/mysql/jdbc/jdbc2/optional/JDBC4CallableStatementWrapper:unwrappedInterfaces	Ljava/util/Map;
/*     */     //   9: goto +11 -> 20
/*     */     //   12: astore_1
/*     */     //   13: aload_0
/*     */     //   14: aconst_null
/*     */     //   15: putfield 3	com/mysql/jdbc/jdbc2/optional/JDBC4CallableStatementWrapper:unwrappedInterfaces	Ljava/util/Map;
/*     */     //   18: aload_1
/*     */     //   19: athrow
/*     */     //   20: return
/*     */     // Line number table:
/*     */     //   Java source line #63	-> byte code offset #0
/*     */     //   Java source line #65	-> byte code offset #4
/*     */     //   Java source line #66	-> byte code offset #9
/*     */     //   Java source line #65	-> byte code offset #12
/*     */     //   Java source line #67	-> byte code offset #20
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	21	0	this	JDBC4CallableStatementWrapper
/*     */     //   12	7	1	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   0	4	12	finally
/*     */   }
/*     */   
/*     */   public boolean isClosed()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/*  71 */       if (this.wrappedStmt != null) {
/*  72 */         return this.wrappedStmt.isClosed();
/*     */       }
/*  74 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/*  77 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/*  80 */     return false;
/*     */   }
/*     */   
/*     */   public void setPoolable(boolean poolable) throws SQLException {
/*     */     try {
/*  85 */       if (this.wrappedStmt != null) {
/*  86 */         this.wrappedStmt.setPoolable(poolable);
/*     */       } else {
/*  88 */         throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/*  91 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isPoolable() throws SQLException {
/*     */     try {
/*  97 */       if (this.wrappedStmt != null) {
/*  98 */         return this.wrappedStmt.isPoolable();
/*     */       }
/* 100 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 103 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 106 */     return false;
/*     */   }
/*     */   
/*     */   public void setRowId(int parameterIndex, RowId x) throws SQLException {
/*     */     try {
/* 111 */       if (this.wrappedStmt != null) {
/* 112 */         ((PreparedStatement)this.wrappedStmt).setRowId(parameterIndex, x);
/*     */       } else {
/* 114 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 117 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setNClob(int parameterIndex, NClob value) throws SQLException {
/*     */     try {
/* 123 */       if (this.wrappedStmt != null) {
/* 124 */         ((PreparedStatement)this.wrappedStmt).setNClob(parameterIndex, value);
/*     */       } else {
/* 126 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 129 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
/*     */     try {
/* 135 */       if (this.wrappedStmt != null) {
/* 136 */         ((PreparedStatement)this.wrappedStmt).setSQLXML(parameterIndex, xmlObject);
/*     */       } else {
/* 138 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 141 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setNString(int parameterIndex, String value) throws SQLException {
/*     */     try {
/* 147 */       if (this.wrappedStmt != null) {
/* 148 */         ((PreparedStatement)this.wrappedStmt).setNString(parameterIndex, value);
/*     */       } else {
/* 150 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 153 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
/*     */     try {
/* 159 */       if (this.wrappedStmt != null) {
/* 160 */         ((PreparedStatement)this.wrappedStmt).setNCharacterStream(parameterIndex, value, length);
/*     */       } else {
/* 162 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 165 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
/*     */     try {
/* 171 */       if (this.wrappedStmt != null) {
/* 172 */         ((PreparedStatement)this.wrappedStmt).setClob(parameterIndex, reader, length);
/*     */       } else {
/* 174 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 177 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
/*     */     try {
/* 183 */       if (this.wrappedStmt != null) {
/* 184 */         ((PreparedStatement)this.wrappedStmt).setBlob(parameterIndex, inputStream, length);
/*     */       } else {
/* 186 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 189 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
/*     */     try {
/* 195 */       if (this.wrappedStmt != null) {
/* 196 */         ((PreparedStatement)this.wrappedStmt).setNClob(parameterIndex, reader, length);
/*     */       } else {
/* 198 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 201 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
/*     */     try {
/* 207 */       if (this.wrappedStmt != null) {
/* 208 */         ((PreparedStatement)this.wrappedStmt).setAsciiStream(parameterIndex, x, length);
/*     */       } else {
/* 210 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 213 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
/*     */     try {
/* 219 */       if (this.wrappedStmt != null) {
/* 220 */         ((PreparedStatement)this.wrappedStmt).setBinaryStream(parameterIndex, x, length);
/*     */       } else {
/* 222 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 225 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
/*     */     try {
/* 231 */       if (this.wrappedStmt != null) {
/* 232 */         ((PreparedStatement)this.wrappedStmt).setCharacterStream(parameterIndex, reader, length);
/*     */       } else {
/* 234 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 237 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
/*     */     try {
/* 243 */       if (this.wrappedStmt != null) {
/* 244 */         ((PreparedStatement)this.wrappedStmt).setAsciiStream(parameterIndex, x);
/*     */       } else {
/* 246 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 249 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
/*     */     try {
/* 255 */       if (this.wrappedStmt != null) {
/* 256 */         ((PreparedStatement)this.wrappedStmt).setBinaryStream(parameterIndex, x);
/*     */       } else {
/* 258 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 261 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
/*     */     try {
/* 267 */       if (this.wrappedStmt != null) {
/* 268 */         ((PreparedStatement)this.wrappedStmt).setCharacterStream(parameterIndex, reader);
/*     */       } else {
/* 270 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 273 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException
/*     */   {
/*     */     try {
/* 280 */       if (this.wrappedStmt != null) {
/* 281 */         ((PreparedStatement)this.wrappedStmt).setNCharacterStream(parameterIndex, value);
/*     */       } else {
/* 283 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 286 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setClob(int parameterIndex, Reader reader) throws SQLException
/*     */   {
/*     */     try {
/* 293 */       if (this.wrappedStmt != null) {
/* 294 */         ((PreparedStatement)this.wrappedStmt).setClob(parameterIndex, reader);
/*     */       } else {
/* 296 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 299 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException
/*     */   {
/*     */     try {
/* 306 */       if (this.wrappedStmt != null) {
/* 307 */         ((PreparedStatement)this.wrappedStmt).setBlob(parameterIndex, inputStream);
/*     */       } else {
/* 309 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 312 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setNClob(int parameterIndex, Reader reader) throws SQLException {
/*     */     try {
/* 318 */       if (this.wrappedStmt != null) {
/* 319 */         ((PreparedStatement)this.wrappedStmt).setNClob(parameterIndex, reader);
/*     */       } else {
/* 321 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 324 */       checkAndFireConnectionError(sqlEx);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isWrapperFor(Class<?> iface)
/*     */     throws SQLException
/*     */   {
/* 350 */     boolean isInstance = iface.isInstance(this);
/*     */     
/* 352 */     if (isInstance) {
/* 353 */       return true;
/*     */     }
/*     */     
/* 356 */     String interfaceClassName = iface.getName();
/*     */     
/*     */ 
/* 359 */     return (interfaceClassName.equals("com.mysql.jdbc.Statement")) || (interfaceClassName.equals("java.sql.Statement")) || (interfaceClassName.equals("java.sql.PreparedStatement")) || (interfaceClassName.equals("java.sql.Wrapper"));
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
/*     */   public synchronized <T> T unwrap(Class<T> iface)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 383 */       if (("java.sql.Statement".equals(iface.getName())) || ("java.sql.PreparedStatement".equals(iface.getName())) || 
/* 384 */         ("java.sql.Wrapper.class".equals(iface.getName()))) {
/* 385 */         return (T)iface.cast(this);
/*     */       }
/*     */       
/* 388 */       if (this.unwrappedInterfaces == null) {
/* 389 */         this.unwrappedInterfaces = new HashMap();
/*     */       }
/*     */       
/* 392 */       Object cachedUnwrapped = this.unwrappedInterfaces.get(iface);
/*     */       
/* 394 */       if (cachedUnwrapped == null) {
/* 395 */         if (cachedUnwrapped == null) {
/* 396 */           cachedUnwrapped = Proxy.newProxyInstance(this.wrappedStmt.getClass().getClassLoader(), new Class[] { iface }, new ConnectionErrorFiringInvocationHandler(this, this.wrappedStmt));
/*     */           
/* 398 */           this.unwrappedInterfaces.put(iface, cachedUnwrapped);
/*     */         }
/* 400 */         this.unwrappedInterfaces.put(iface, cachedUnwrapped);
/*     */       }
/*     */       
/* 403 */       return (T)iface.cast(cachedUnwrapped);
/*     */     } catch (ClassCastException cce) {
/* 405 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setRowId(String parameterName, RowId x) throws SQLException {
/*     */     try {
/* 411 */       if (this.wrappedStmt != null) {
/* 412 */         ((CallableStatement)this.wrappedStmt).setRowId(parameterName, x);
/*     */       } else {
/* 414 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 417 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
/*     */     try {
/* 423 */       if (this.wrappedStmt != null) {
/* 424 */         ((CallableStatement)this.wrappedStmt).setSQLXML(parameterName, xmlObject);
/*     */       } else {
/* 426 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 429 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public SQLXML getSQLXML(int parameterIndex) throws SQLException {
/*     */     try {
/* 435 */       if (this.wrappedStmt != null) {
/* 436 */         return ((CallableStatement)this.wrappedStmt).getSQLXML(parameterIndex);
/*     */       }
/* 438 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 441 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 444 */     return null;
/*     */   }
/*     */   
/*     */   public SQLXML getSQLXML(String parameterName) throws SQLException
/*     */   {
/*     */     try {
/* 450 */       if (this.wrappedStmt != null) {
/* 451 */         return ((CallableStatement)this.wrappedStmt).getSQLXML(parameterName);
/*     */       }
/* 453 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 456 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 459 */     return null;
/*     */   }
/*     */   
/*     */   public RowId getRowId(String parameterName) throws SQLException {
/*     */     try {
/* 464 */       if (this.wrappedStmt != null) {
/* 465 */         return ((CallableStatement)this.wrappedStmt).getRowId(parameterName);
/*     */       }
/* 467 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 470 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 473 */     return null;
/*     */   }
/*     */   
/*     */   public void setNClob(String parameterName, NClob value) throws SQLException {
/*     */     try {
/* 478 */       if (this.wrappedStmt != null) {
/* 479 */         ((CallableStatement)this.wrappedStmt).setNClob(parameterName, value);
/*     */       } else {
/* 481 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 484 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setNClob(String parameterName, Reader reader) throws SQLException {
/*     */     try {
/* 490 */       if (this.wrappedStmt != null) {
/* 491 */         ((CallableStatement)this.wrappedStmt).setNClob(parameterName, reader);
/*     */       } else {
/* 493 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 496 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
/*     */     try {
/* 502 */       if (this.wrappedStmt != null) {
/* 503 */         ((CallableStatement)this.wrappedStmt).setNClob(parameterName, reader, length);
/*     */       } else {
/* 505 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 508 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setNString(String parameterName, String value) throws SQLException {
/*     */     try {
/* 514 */       if (this.wrappedStmt != null) {
/* 515 */         ((CallableStatement)this.wrappedStmt).setNString(parameterName, value);
/*     */       } else {
/* 517 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 520 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public Reader getCharacterStream(int parameterIndex)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 529 */       if (this.wrappedStmt != null) {
/* 530 */         return ((CallableStatement)this.wrappedStmt).getCharacterStream(parameterIndex);
/*     */       }
/* 532 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 535 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 538 */     return null;
/*     */   }
/*     */   
/*     */   public Reader getCharacterStream(String parameterName)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 546 */       if (this.wrappedStmt != null) {
/* 547 */         return ((CallableStatement)this.wrappedStmt).getCharacterStream(parameterName);
/*     */       }
/* 549 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 552 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 555 */     return null;
/*     */   }
/*     */   
/*     */   public Reader getNCharacterStream(int parameterIndex)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 563 */       if (this.wrappedStmt != null) {
/* 564 */         return ((CallableStatement)this.wrappedStmt).getNCharacterStream(parameterIndex);
/*     */       }
/* 566 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 569 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 572 */     return null;
/*     */   }
/*     */   
/*     */   public Reader getNCharacterStream(String parameterName)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 580 */       if (this.wrappedStmt != null) {
/* 581 */         return ((CallableStatement)this.wrappedStmt).getNCharacterStream(parameterName);
/*     */       }
/* 583 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 586 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 589 */     return null;
/*     */   }
/*     */   
/*     */   public NClob getNClob(String parameterName)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 597 */       if (this.wrappedStmt != null) {
/* 598 */         return ((CallableStatement)this.wrappedStmt).getNClob(parameterName);
/*     */       }
/* 600 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 603 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 606 */     return null;
/*     */   }
/*     */   
/*     */   public String getNString(String parameterName)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 614 */       if (this.wrappedStmt != null) {
/* 615 */         return ((CallableStatement)this.wrappedStmt).getNString(parameterName);
/*     */       }
/* 617 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 620 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 623 */     return null;
/*     */   }
/*     */   
/*     */   public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
/*     */     try {
/* 628 */       if (this.wrappedStmt != null) {
/* 629 */         ((CallableStatement)this.wrappedStmt).setAsciiStream(parameterName, x);
/*     */       } else {
/* 631 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 634 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
/*     */     try {
/* 640 */       if (this.wrappedStmt != null) {
/* 641 */         ((CallableStatement)this.wrappedStmt).setAsciiStream(parameterName, x, length);
/*     */       } else {
/* 643 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 646 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
/*     */     try {
/* 652 */       if (this.wrappedStmt != null) {
/* 653 */         ((CallableStatement)this.wrappedStmt).setBinaryStream(parameterName, x);
/*     */       } else {
/* 655 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 658 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
/*     */     try {
/* 664 */       if (this.wrappedStmt != null) {
/* 665 */         ((CallableStatement)this.wrappedStmt).setBinaryStream(parameterName, x, length);
/*     */       } else {
/* 667 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 670 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setBlob(String parameterName, InputStream x) throws SQLException {
/*     */     try {
/* 676 */       if (this.wrappedStmt != null) {
/* 677 */         ((CallableStatement)this.wrappedStmt).setBlob(parameterName, x);
/*     */       } else {
/* 679 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 682 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setBlob(String parameterName, InputStream x, long length) throws SQLException {
/*     */     try {
/* 688 */       if (this.wrappedStmt != null) {
/* 689 */         ((CallableStatement)this.wrappedStmt).setBlob(parameterName, x, length);
/*     */       } else {
/* 691 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 694 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setBlob(String parameterName, Blob x) throws SQLException {
/*     */     try {
/* 700 */       if (this.wrappedStmt != null) {
/* 701 */         ((CallableStatement)this.wrappedStmt).setBlob(parameterName, x);
/*     */       } else {
/* 703 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 706 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
/*     */     try {
/* 712 */       if (this.wrappedStmt != null) {
/* 713 */         ((CallableStatement)this.wrappedStmt).setCharacterStream(parameterName, reader);
/*     */       } else {
/* 715 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 718 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
/*     */     try {
/* 724 */       if (this.wrappedStmt != null) {
/* 725 */         ((CallableStatement)this.wrappedStmt).setCharacterStream(parameterName, reader, length);
/*     */       } else {
/* 727 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 730 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setClob(String parameterName, Clob x) throws SQLException {
/*     */     try {
/* 736 */       if (this.wrappedStmt != null) {
/* 737 */         ((CallableStatement)this.wrappedStmt).setClob(parameterName, x);
/*     */       } else {
/* 739 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 742 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setClob(String parameterName, Reader reader) throws SQLException {
/*     */     try {
/* 748 */       if (this.wrappedStmt != null) {
/* 749 */         ((CallableStatement)this.wrappedStmt).setClob(parameterName, reader);
/*     */       } else {
/* 751 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 754 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setClob(String parameterName, Reader reader, long length) throws SQLException {
/*     */     try {
/* 760 */       if (this.wrappedStmt != null) {
/* 761 */         ((CallableStatement)this.wrappedStmt).setClob(parameterName, reader, length);
/*     */       } else {
/* 763 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 766 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setNCharacterStream(String parameterName, Reader reader) throws SQLException {
/*     */     try {
/* 772 */       if (this.wrappedStmt != null) {
/* 773 */         ((CallableStatement)this.wrappedStmt).setNCharacterStream(parameterName, reader);
/*     */       } else {
/* 775 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 778 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setNCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
/*     */     try {
/* 784 */       if (this.wrappedStmt != null) {
/* 785 */         ((CallableStatement)this.wrappedStmt).setNCharacterStream(parameterName, reader, length);
/*     */       } else {
/* 787 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 790 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public NClob getNClob(int parameterIndex) throws SQLException {
/*     */     try {
/* 796 */       if (this.wrappedStmt != null) {
/* 797 */         return ((CallableStatement)this.wrappedStmt).getNClob(parameterIndex);
/*     */       }
/* 799 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 802 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 805 */     return null;
/*     */   }
/*     */   
/*     */   public String getNString(int parameterIndex) throws SQLException {
/*     */     try {
/* 810 */       if (this.wrappedStmt != null) {
/* 811 */         return ((CallableStatement)this.wrappedStmt).getNString(parameterIndex);
/*     */       }
/* 813 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 816 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 819 */     return null;
/*     */   }
/*     */   
/*     */   public RowId getRowId(int parameterIndex) throws SQLException {
/*     */     try {
/* 824 */       if (this.wrappedStmt != null) {
/* 825 */         return ((CallableStatement)this.wrappedStmt).getRowId(parameterIndex);
/*     */       }
/* 827 */       throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 830 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 833 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\jdbc2\optional\JDBC4CallableStatementWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */