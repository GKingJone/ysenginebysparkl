/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.lang.reflect.Proxy;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JDBC4PreparedStatementWrapper
/*     */   extends PreparedStatementWrapper
/*     */ {
/*     */   public JDBC4PreparedStatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, PreparedStatement toWrap)
/*     */   {
/*  58 */     super(c, conn, toWrap);
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public synchronized void close()
/*     */     throws SQLException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield 2	com/mysql/jdbc/jdbc2/optional/JDBC4PreparedStatementWrapper:pooledConnection	Lcom/mysql/jdbc/jdbc2/optional/MysqlPooledConnection;
/*     */     //   4: ifnonnull +4 -> 8
/*     */     //   7: return
/*     */     //   8: aload_0
/*     */     //   9: getfield 2	com/mysql/jdbc/jdbc2/optional/JDBC4PreparedStatementWrapper:pooledConnection	Lcom/mysql/jdbc/jdbc2/optional/MysqlPooledConnection;
/*     */     //   12: astore_1
/*     */     //   13: aload_0
/*     */     //   14: invokespecial 3	com/mysql/jdbc/jdbc2/optional/PreparedStatementWrapper:close	()V
/*     */     //   17: new 4	javax/sql/StatementEvent
/*     */     //   20: dup
/*     */     //   21: aload_1
/*     */     //   22: aload_0
/*     */     //   23: invokespecial 5	javax/sql/StatementEvent:<init>	(Ljavax/sql/PooledConnection;Ljava/sql/PreparedStatement;)V
/*     */     //   26: astore_2
/*     */     //   27: aload_1
/*     */     //   28: instanceof 6
/*     */     //   31: ifeq +14 -> 45
/*     */     //   34: aload_1
/*     */     //   35: checkcast 6	com/mysql/jdbc/jdbc2/optional/JDBC4MysqlPooledConnection
/*     */     //   38: aload_2
/*     */     //   39: invokevirtual 7	com/mysql/jdbc/jdbc2/optional/JDBC4MysqlPooledConnection:fireStatementEvent	(Ljavax/sql/StatementEvent;)V
/*     */     //   42: goto +36 -> 78
/*     */     //   45: aload_1
/*     */     //   46: instanceof 8
/*     */     //   49: ifeq +14 -> 63
/*     */     //   52: aload_1
/*     */     //   53: checkcast 8	com/mysql/jdbc/jdbc2/optional/JDBC4MysqlXAConnection
/*     */     //   56: aload_2
/*     */     //   57: invokevirtual 9	com/mysql/jdbc/jdbc2/optional/JDBC4MysqlXAConnection:fireStatementEvent	(Ljavax/sql/StatementEvent;)V
/*     */     //   60: goto +18 -> 78
/*     */     //   63: aload_1
/*     */     //   64: instanceof 10
/*     */     //   67: ifeq +11 -> 78
/*     */     //   70: aload_1
/*     */     //   71: checkcast 10	com/mysql/jdbc/jdbc2/optional/JDBC4SuspendableXAConnection
/*     */     //   74: aload_2
/*     */     //   75: invokevirtual 11	com/mysql/jdbc/jdbc2/optional/JDBC4SuspendableXAConnection:fireStatementEvent	(Ljavax/sql/StatementEvent;)V
/*     */     //   78: aload_0
/*     */     //   79: aconst_null
/*     */     //   80: putfield 12	com/mysql/jdbc/jdbc2/optional/JDBC4PreparedStatementWrapper:unwrappedInterfaces	Ljava/util/Map;
/*     */     //   83: goto +11 -> 94
/*     */     //   86: astore_3
/*     */     //   87: aload_0
/*     */     //   88: aconst_null
/*     */     //   89: putfield 12	com/mysql/jdbc/jdbc2/optional/JDBC4PreparedStatementWrapper:unwrappedInterfaces	Ljava/util/Map;
/*     */     //   92: aload_3
/*     */     //   93: athrow
/*     */     //   94: goto +91 -> 185
/*     */     //   97: astore 4
/*     */     //   99: new 4	javax/sql/StatementEvent
/*     */     //   102: dup
/*     */     //   103: aload_1
/*     */     //   104: aload_0
/*     */     //   105: invokespecial 5	javax/sql/StatementEvent:<init>	(Ljavax/sql/PooledConnection;Ljava/sql/PreparedStatement;)V
/*     */     //   108: astore 5
/*     */     //   110: aload_1
/*     */     //   111: instanceof 6
/*     */     //   114: ifeq +15 -> 129
/*     */     //   117: aload_1
/*     */     //   118: checkcast 6	com/mysql/jdbc/jdbc2/optional/JDBC4MysqlPooledConnection
/*     */     //   121: aload 5
/*     */     //   123: invokevirtual 7	com/mysql/jdbc/jdbc2/optional/JDBC4MysqlPooledConnection:fireStatementEvent	(Ljavax/sql/StatementEvent;)V
/*     */     //   126: goto +38 -> 164
/*     */     //   129: aload_1
/*     */     //   130: instanceof 8
/*     */     //   133: ifeq +15 -> 148
/*     */     //   136: aload_1
/*     */     //   137: checkcast 8	com/mysql/jdbc/jdbc2/optional/JDBC4MysqlXAConnection
/*     */     //   140: aload 5
/*     */     //   142: invokevirtual 9	com/mysql/jdbc/jdbc2/optional/JDBC4MysqlXAConnection:fireStatementEvent	(Ljavax/sql/StatementEvent;)V
/*     */     //   145: goto +19 -> 164
/*     */     //   148: aload_1
/*     */     //   149: instanceof 10
/*     */     //   152: ifeq +12 -> 164
/*     */     //   155: aload_1
/*     */     //   156: checkcast 10	com/mysql/jdbc/jdbc2/optional/JDBC4SuspendableXAConnection
/*     */     //   159: aload 5
/*     */     //   161: invokevirtual 11	com/mysql/jdbc/jdbc2/optional/JDBC4SuspendableXAConnection:fireStatementEvent	(Ljavax/sql/StatementEvent;)V
/*     */     //   164: aload_0
/*     */     //   165: aconst_null
/*     */     //   166: putfield 12	com/mysql/jdbc/jdbc2/optional/JDBC4PreparedStatementWrapper:unwrappedInterfaces	Ljava/util/Map;
/*     */     //   169: goto +13 -> 182
/*     */     //   172: astore 6
/*     */     //   174: aload_0
/*     */     //   175: aconst_null
/*     */     //   176: putfield 12	com/mysql/jdbc/jdbc2/optional/JDBC4PreparedStatementWrapper:unwrappedInterfaces	Ljava/util/Map;
/*     */     //   179: aload 6
/*     */     //   181: athrow
/*     */     //   182: aload 4
/*     */     //   184: athrow
/*     */     //   185: return
/*     */     // Line number table:
/*     */     //   Java source line #62	-> byte code offset #0
/*     */     //   Java source line #64	-> byte code offset #7
/*     */     //   Java source line #67	-> byte code offset #8
/*     */     //   Java source line #70	-> byte code offset #13
/*     */     //   Java source line #73	-> byte code offset #17
/*     */     //   Java source line #75	-> byte code offset #27
/*     */     //   Java source line #76	-> byte code offset #34
/*     */     //   Java source line #77	-> byte code offset #45
/*     */     //   Java source line #78	-> byte code offset #52
/*     */     //   Java source line #79	-> byte code offset #63
/*     */     //   Java source line #80	-> byte code offset #70
/*     */     //   Java source line #83	-> byte code offset #78
/*     */     //   Java source line #84	-> byte code offset #83
/*     */     //   Java source line #83	-> byte code offset #86
/*     */     //   Java source line #85	-> byte code offset #94
/*     */     //   Java source line #72	-> byte code offset #97
/*     */     //   Java source line #73	-> byte code offset #99
/*     */     //   Java source line #75	-> byte code offset #110
/*     */     //   Java source line #76	-> byte code offset #117
/*     */     //   Java source line #77	-> byte code offset #129
/*     */     //   Java source line #78	-> byte code offset #136
/*     */     //   Java source line #79	-> byte code offset #148
/*     */     //   Java source line #80	-> byte code offset #155
/*     */     //   Java source line #83	-> byte code offset #164
/*     */     //   Java source line #84	-> byte code offset #169
/*     */     //   Java source line #83	-> byte code offset #172
/*     */     //   Java source line #86	-> byte code offset #185
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	186	0	this	JDBC4PreparedStatementWrapper
/*     */     //   12	144	1	con	MysqlPooledConnection
/*     */     //   26	49	2	e	javax.sql.StatementEvent
/*     */     //   86	7	3	localObject1	Object
/*     */     //   97	86	4	localObject2	Object
/*     */     //   108	52	5	e	javax.sql.StatementEvent
/*     */     //   172	8	6	localObject3	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   17	78	86	finally
/*     */     //   13	17	97	finally
/*     */     //   97	99	97	finally
/*     */     //   99	164	172	finally
/*     */     //   172	174	172	finally
/*     */   }
/*     */   
/*     */   public boolean isClosed()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/*  90 */       if (this.wrappedStmt != null) {
/*  91 */         return this.wrappedStmt.isClosed();
/*     */       }
/*  93 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/*  96 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/*  99 */     return false;
/*     */   }
/*     */   
/*     */   public void setPoolable(boolean poolable) throws SQLException {
/*     */     try {
/* 104 */       if (this.wrappedStmt != null) {
/* 105 */         this.wrappedStmt.setPoolable(poolable);
/*     */       } else {
/* 107 */         throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 110 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isPoolable() throws SQLException {
/*     */     try {
/* 116 */       if (this.wrappedStmt != null) {
/* 117 */         return this.wrappedStmt.isPoolable();
/*     */       }
/* 119 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 122 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 125 */     return false;
/*     */   }
/*     */   
/*     */   public void setRowId(int parameterIndex, RowId x) throws SQLException {
/*     */     try {
/* 130 */       if (this.wrappedStmt != null) {
/* 131 */         ((PreparedStatement)this.wrappedStmt).setRowId(parameterIndex, x);
/*     */       } else {
/* 133 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 136 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setNClob(int parameterIndex, NClob value) throws SQLException {
/*     */     try {
/* 142 */       if (this.wrappedStmt != null) {
/* 143 */         ((PreparedStatement)this.wrappedStmt).setNClob(parameterIndex, value);
/*     */       } else {
/* 145 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 148 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
/*     */     try {
/* 154 */       if (this.wrappedStmt != null) {
/* 155 */         ((PreparedStatement)this.wrappedStmt).setSQLXML(parameterIndex, xmlObject);
/*     */       } else {
/* 157 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 160 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setNString(int parameterIndex, String value) throws SQLException {
/*     */     try {
/* 166 */       if (this.wrappedStmt != null) {
/* 167 */         ((PreparedStatement)this.wrappedStmt).setNString(parameterIndex, value);
/*     */       } else {
/* 169 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 172 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
/*     */     try {
/* 178 */       if (this.wrappedStmt != null) {
/* 179 */         ((PreparedStatement)this.wrappedStmt).setNCharacterStream(parameterIndex, value, length);
/*     */       } else {
/* 181 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 184 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
/*     */     try {
/* 190 */       if (this.wrappedStmt != null) {
/* 191 */         ((PreparedStatement)this.wrappedStmt).setClob(parameterIndex, reader, length);
/*     */       } else {
/* 193 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 196 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
/*     */     try {
/* 202 */       if (this.wrappedStmt != null) {
/* 203 */         ((PreparedStatement)this.wrappedStmt).setBlob(parameterIndex, inputStream, length);
/*     */       } else {
/* 205 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 208 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
/*     */     try {
/* 214 */       if (this.wrappedStmt != null) {
/* 215 */         ((PreparedStatement)this.wrappedStmt).setNClob(parameterIndex, reader, length);
/*     */       } else {
/* 217 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 220 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
/*     */     try {
/* 226 */       if (this.wrappedStmt != null) {
/* 227 */         ((PreparedStatement)this.wrappedStmt).setAsciiStream(parameterIndex, x, length);
/*     */       } else {
/* 229 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 232 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
/*     */     try {
/* 238 */       if (this.wrappedStmt != null) {
/* 239 */         ((PreparedStatement)this.wrappedStmt).setBinaryStream(parameterIndex, x, length);
/*     */       } else {
/* 241 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 244 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
/*     */     try {
/* 250 */       if (this.wrappedStmt != null) {
/* 251 */         ((PreparedStatement)this.wrappedStmt).setCharacterStream(parameterIndex, reader, length);
/*     */       } else {
/* 253 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 256 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
/*     */     try {
/* 262 */       if (this.wrappedStmt != null) {
/* 263 */         ((PreparedStatement)this.wrappedStmt).setAsciiStream(parameterIndex, x);
/*     */       } else {
/* 265 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 268 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
/*     */     try {
/* 274 */       if (this.wrappedStmt != null) {
/* 275 */         ((PreparedStatement)this.wrappedStmt).setBinaryStream(parameterIndex, x);
/*     */       } else {
/* 277 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 280 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
/*     */     try {
/* 286 */       if (this.wrappedStmt != null) {
/* 287 */         ((PreparedStatement)this.wrappedStmt).setCharacterStream(parameterIndex, reader);
/*     */       } else {
/* 289 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 292 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException
/*     */   {
/*     */     try {
/* 299 */       if (this.wrappedStmt != null) {
/* 300 */         ((PreparedStatement)this.wrappedStmt).setNCharacterStream(parameterIndex, value);
/*     */       } else {
/* 302 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 305 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setClob(int parameterIndex, Reader reader) throws SQLException
/*     */   {
/*     */     try {
/* 312 */       if (this.wrappedStmt != null) {
/* 313 */         ((PreparedStatement)this.wrappedStmt).setClob(parameterIndex, reader);
/*     */       } else {
/* 315 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 318 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException
/*     */   {
/*     */     try {
/* 325 */       if (this.wrappedStmt != null) {
/* 326 */         ((PreparedStatement)this.wrappedStmt).setBlob(parameterIndex, inputStream);
/*     */       } else {
/* 328 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 331 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setNClob(int parameterIndex, Reader reader) throws SQLException {
/*     */     try {
/* 337 */       if (this.wrappedStmt != null) {
/* 338 */         ((PreparedStatement)this.wrappedStmt).setNClob(parameterIndex, reader);
/*     */       } else {
/* 340 */         throw SQLError.createSQLException("No operations allowed after statement closed", "S1000", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/* 343 */       checkAndFireConnectionError(sqlEx);
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
/* 369 */     boolean isInstance = iface.isInstance(this);
/*     */     
/* 371 */     if (isInstance) {
/* 372 */       return true;
/*     */     }
/*     */     
/* 375 */     String interfaceClassName = iface.getName();
/*     */     
/*     */ 
/* 378 */     return (interfaceClassName.equals("com.mysql.jdbc.Statement")) || (interfaceClassName.equals("java.sql.Statement")) || (interfaceClassName.equals("java.sql.PreparedStatement")) || (interfaceClassName.equals("java.sql.Wrapper"));
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
/* 402 */       if (("java.sql.Statement".equals(iface.getName())) || ("java.sql.PreparedStatement".equals(iface.getName())) || 
/* 403 */         ("java.sql.Wrapper.class".equals(iface.getName()))) {
/* 404 */         return (T)iface.cast(this);
/*     */       }
/*     */       
/* 407 */       if (this.unwrappedInterfaces == null) {
/* 408 */         this.unwrappedInterfaces = new HashMap();
/*     */       }
/*     */       
/* 411 */       Object cachedUnwrapped = this.unwrappedInterfaces.get(iface);
/*     */       
/* 413 */       if (cachedUnwrapped == null) {
/* 414 */         if (cachedUnwrapped == null) {
/* 415 */           cachedUnwrapped = Proxy.newProxyInstance(this.wrappedStmt.getClass().getClassLoader(), new Class[] { iface }, new ConnectionErrorFiringInvocationHandler(this, this.wrappedStmt));
/*     */           
/* 417 */           this.unwrappedInterfaces.put(iface, cachedUnwrapped);
/*     */         }
/* 419 */         this.unwrappedInterfaces.put(iface, cachedUnwrapped);
/*     */       }
/*     */       
/* 422 */       return (T)iface.cast(cachedUnwrapped);
/*     */     } catch (ClassCastException cce) {
/* 424 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\jdbc2\optional\JDBC4PreparedStatementWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */