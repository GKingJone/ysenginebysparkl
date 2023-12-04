/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.Array;
/*     */ import java.sql.Blob;
/*     */ import java.sql.Clob;
/*     */ import java.sql.NClob;
/*     */ import java.sql.SQLClientInfoException;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLXML;
/*     */ import java.sql.Struct;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JDBC4ConnectionWrapper
/*     */   extends ConnectionWrapper
/*     */ {
/*     */   public JDBC4ConnectionWrapper(MysqlPooledConnection mysqlPooledConnection, com.mysql.jdbc.Connection mysqlConnection, boolean forXa)
/*     */     throws SQLException
/*     */   {
/*  64 */     super(mysqlPooledConnection, mysqlConnection, forXa);
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public void close()
/*     */     throws SQLException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokespecial 2	com/mysql/jdbc/jdbc2/optional/ConnectionWrapper:close	()V
/*     */     //   4: aload_0
/*     */     //   5: aconst_null
/*     */     //   6: putfield 3	com/mysql/jdbc/jdbc2/optional/JDBC4ConnectionWrapper:unwrappedInterfaces	Ljava/util/Map;
/*     */     //   9: goto +11 -> 20
/*     */     //   12: astore_1
/*     */     //   13: aload_0
/*     */     //   14: aconst_null
/*     */     //   15: putfield 3	com/mysql/jdbc/jdbc2/optional/JDBC4ConnectionWrapper:unwrappedInterfaces	Ljava/util/Map;
/*     */     //   18: aload_1
/*     */     //   19: athrow
/*     */     //   20: return
/*     */     // Line number table:
/*     */     //   Java source line #69	-> byte code offset #0
/*     */     //   Java source line #71	-> byte code offset #4
/*     */     //   Java source line #72	-> byte code offset #9
/*     */     //   Java source line #71	-> byte code offset #12
/*     */     //   Java source line #73	-> byte code offset #20
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	21	0	this	JDBC4ConnectionWrapper
/*     */     //   12	7	1	localObject	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   0	4	12	finally
/*     */   }
/*     */   
/*     */   public SQLXML createSQLXML()
/*     */     throws SQLException
/*     */   {
/*  76 */     checkClosed();
/*     */     try
/*     */     {
/*  79 */       return this.mc.createSQLXML();
/*     */     } catch (SQLException sqlException) {
/*  81 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */     
/*  84 */     return null;
/*     */   }
/*     */   
/*     */   public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
/*  88 */     checkClosed();
/*     */     try
/*     */     {
/*  91 */       return this.mc.createArrayOf(typeName, elements);
/*     */     } catch (SQLException sqlException) {
/*  93 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */     
/*  96 */     return null;
/*     */   }
/*     */   
/*     */   public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
/* 100 */     checkClosed();
/*     */     try
/*     */     {
/* 103 */       return this.mc.createStruct(typeName, attributes);
/*     */     } catch (SQLException sqlException) {
/* 105 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */     
/* 108 */     return null;
/*     */   }
/*     */   
/*     */   public Properties getClientInfo() throws SQLException {
/* 112 */     checkClosed();
/*     */     try
/*     */     {
/* 115 */       return this.mc.getClientInfo();
/*     */     } catch (SQLException sqlException) {
/* 117 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */     
/* 120 */     return null;
/*     */   }
/*     */   
/*     */   public String getClientInfo(String name) throws SQLException {
/* 124 */     checkClosed();
/*     */     try
/*     */     {
/* 127 */       return this.mc.getClientInfo(name);
/*     */     } catch (SQLException sqlException) {
/* 129 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */     
/* 132 */     return null;
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
/*     */   public synchronized boolean isValid(int timeout)
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 159 */       return this.mc.isValid(timeout);
/*     */     } catch (SQLException sqlException) {
/* 161 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */     
/* 164 */     return false;
/*     */   }
/*     */   
/*     */   public void setClientInfo(Properties properties) throws SQLClientInfoException {
/*     */     try {
/* 169 */       checkClosed();
/*     */       
/* 171 */       this.mc.setClientInfo(properties);
/*     */     } catch (SQLException sqlException) {
/*     */       try {
/* 174 */         checkAndFireConnectionError(sqlException);
/*     */       } catch (SQLException sqlEx2) {
/* 176 */         SQLClientInfoException clientEx = new SQLClientInfoException();
/* 177 */         clientEx.initCause(sqlEx2);
/*     */         
/* 179 */         throw clientEx;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void setClientInfo(String name, String value) throws SQLClientInfoException {
/*     */     try {
/* 186 */       checkClosed();
/*     */       
/* 188 */       this.mc.setClientInfo(name, value);
/*     */     } catch (SQLException sqlException) {
/*     */       try {
/* 191 */         checkAndFireConnectionError(sqlException);
/*     */       } catch (SQLException sqlEx2) {
/* 193 */         SQLClientInfoException clientEx = new SQLClientInfoException();
/* 194 */         clientEx.initCause(sqlEx2);
/*     */         
/* 196 */         throw clientEx;
/*     */       }
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
/*     */   public boolean isWrapperFor(Class<?> iface)
/*     */     throws SQLException
/*     */   {
/* 222 */     checkClosed();
/*     */     
/* 224 */     boolean isInstance = iface.isInstance(this);
/*     */     
/* 226 */     if (isInstance) {
/* 227 */       return true;
/*     */     }
/*     */     
/* 230 */     return (iface.getName().equals("com.mysql.jdbc.Connection")) || (iface.getName().equals("com.mysql.jdbc.ConnectionProperties"));
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
/* 254 */       if (("java.sql.Connection".equals(iface.getName())) || ("java.sql.Wrapper.class".equals(iface.getName()))) {
/* 255 */         return (T)iface.cast(this);
/*     */       }
/*     */       
/* 258 */       if (this.unwrappedInterfaces == null) {
/* 259 */         this.unwrappedInterfaces = new HashMap();
/*     */       }
/*     */       
/* 262 */       Object cachedUnwrapped = this.unwrappedInterfaces.get(iface);
/*     */       
/* 264 */       if (cachedUnwrapped == null) {
/* 265 */         cachedUnwrapped = Proxy.newProxyInstance(this.mc.getClass().getClassLoader(), new Class[] { iface }, new ConnectionErrorFiringInvocationHandler(this, this.mc));
/*     */         
/* 267 */         this.unwrappedInterfaces.put(iface, cachedUnwrapped);
/*     */       }
/*     */       
/* 270 */       return (T)iface.cast(cachedUnwrapped);
/*     */     } catch (ClassCastException cce) {
/* 272 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Blob createBlob()
/*     */     throws SQLException
/*     */   {
/* 280 */     checkClosed();
/*     */     try
/*     */     {
/* 283 */       return this.mc.createBlob();
/*     */     } catch (SQLException sqlException) {
/* 285 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */     
/* 288 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public Clob createClob()
/*     */     throws SQLException
/*     */   {
/* 295 */     checkClosed();
/*     */     try
/*     */     {
/* 298 */       return this.mc.createClob();
/*     */     } catch (SQLException sqlException) {
/* 300 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */     
/* 303 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public NClob createNClob()
/*     */     throws SQLException
/*     */   {
/* 310 */     checkClosed();
/*     */     try
/*     */     {
/* 313 */       return this.mc.createNClob();
/*     */     } catch (SQLException sqlException) {
/* 315 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */     
/* 318 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\jdbc2\optional\JDBC4ConnectionWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */