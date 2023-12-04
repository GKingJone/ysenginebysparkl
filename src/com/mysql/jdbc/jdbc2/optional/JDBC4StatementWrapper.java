/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.SQLException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JDBC4StatementWrapper
/*     */   extends StatementWrapper
/*     */ {
/*     */   public JDBC4StatementWrapper(ConnectionWrapper c, MysqlPooledConnection conn, Statement toWrap)
/*     */   {
/*  55 */     super(c, conn, toWrap);
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public void close()
/*     */     throws SQLException
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: invokespecial 2	com/mysql/jdbc/jdbc2/optional/StatementWrapper:close	()V
/*     */     //   4: aload_0
/*     */     //   5: aconst_null
/*     */     //   6: putfield 3	com/mysql/jdbc/jdbc2/optional/JDBC4StatementWrapper:unwrappedInterfaces	Ljava/util/Map;
/*     */     //   9: goto +11 -> 20
/*     */     //   12: astore_1
/*     */     //   13: aload_0
/*     */     //   14: aconst_null
/*     */     //   15: putfield 3	com/mysql/jdbc/jdbc2/optional/JDBC4StatementWrapper:unwrappedInterfaces	Ljava/util/Map;
/*     */     //   18: aload_1
/*     */     //   19: athrow
/*     */     //   20: return
/*     */     // Line number table:
/*     */     //   Java source line #60	-> byte code offset #0
/*     */     //   Java source line #62	-> byte code offset #4
/*     */     //   Java source line #63	-> byte code offset #9
/*     */     //   Java source line #62	-> byte code offset #12
/*     */     //   Java source line #64	-> byte code offset #20
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	21	0	this	JDBC4StatementWrapper
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
/*  68 */       if (this.wrappedStmt != null) {
/*  69 */         return this.wrappedStmt.isClosed();
/*     */       }
/*  71 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/*  74 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/*  77 */     return false;
/*     */   }
/*     */   
/*     */   public void setPoolable(boolean poolable) throws SQLException {
/*     */     try {
/*  82 */       if (this.wrappedStmt != null) {
/*  83 */         this.wrappedStmt.setPoolable(poolable);
/*     */       } else {
/*  85 */         throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */       }
/*     */     } catch (SQLException sqlEx) {
/*  88 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isPoolable() throws SQLException {
/*     */     try {
/*  94 */       if (this.wrappedStmt != null) {
/*  95 */         return this.wrappedStmt.isPoolable();
/*     */       }
/*  97 */       throw SQLError.createSQLException("Statement already closed", "S1009", this.exceptionInterceptor);
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 100 */       checkAndFireConnectionError(sqlEx);
/*     */     }
/*     */     
/* 103 */     return false;
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
/* 128 */     boolean isInstance = iface.isInstance(this);
/*     */     
/* 130 */     if (isInstance) {
/* 131 */       return true;
/*     */     }
/*     */     
/* 134 */     String interfaceClassName = iface.getName();
/*     */     
/*     */ 
/* 137 */     return (interfaceClassName.equals("com.mysql.jdbc.Statement")) || (interfaceClassName.equals("java.sql.Statement")) || (interfaceClassName.equals("java.sql.Wrapper"));
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
/* 161 */       if (("java.sql.Statement".equals(iface.getName())) || ("java.sql.Wrapper.class".equals(iface.getName()))) {
/* 162 */         return (T)iface.cast(this);
/*     */       }
/*     */       
/* 165 */       if (this.unwrappedInterfaces == null) {
/* 166 */         this.unwrappedInterfaces = new HashMap();
/*     */       }
/*     */       
/* 169 */       Object cachedUnwrapped = this.unwrappedInterfaces.get(iface);
/*     */       
/* 171 */       if (cachedUnwrapped == null) {
/* 172 */         cachedUnwrapped = Proxy.newProxyInstance(this.wrappedStmt.getClass().getClassLoader(), new Class[] { iface }, new ConnectionErrorFiringInvocationHandler(this, this.wrappedStmt));
/*     */         
/* 174 */         this.unwrappedInterfaces.put(iface, cachedUnwrapped);
/*     */       }
/*     */       
/* 177 */       return (T)iface.cast(cachedUnwrapped);
/*     */     } catch (ClassCastException cce) {
/* 179 */       throw SQLError.createSQLException("Unable to unwrap to " + iface.toString(), "S1009", this.exceptionInterceptor);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\jdbc2\optional\JDBC4StatementWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */