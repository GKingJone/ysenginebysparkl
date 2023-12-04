/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import com.mysql.jdbc.ExceptionInterceptor;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.sql.SQLException;
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
/*     */ abstract class WrapperBase
/*     */ {
/*     */   protected MysqlPooledConnection pooledConnection;
/*     */   
/*     */   protected void checkAndFireConnectionError(SQLException sqlEx)
/*     */     throws SQLException
/*     */   {
/*  51 */     if ((this.pooledConnection != null) && 
/*  52 */       ("08S01".equals(sqlEx.getSQLState()))) {
/*  53 */       this.pooledConnection.callConnectionEventListeners(1, sqlEx);
/*     */     }
/*     */     
/*     */ 
/*  57 */     throw sqlEx;
/*     */   }
/*     */   
/*  60 */   protected Map<Class<?>, Object> unwrappedInterfaces = null;
/*     */   protected ExceptionInterceptor exceptionInterceptor;
/*     */   
/*     */   protected WrapperBase(MysqlPooledConnection pooledConnection) {
/*  64 */     this.pooledConnection = pooledConnection;
/*  65 */     this.exceptionInterceptor = this.pooledConnection.getExceptionInterceptor();
/*     */   }
/*     */   
/*     */   protected class ConnectionErrorFiringInvocationHandler implements InvocationHandler {
/*  69 */     Object invokeOn = null;
/*     */     
/*     */     public ConnectionErrorFiringInvocationHandler(Object toInvokeOn) {
/*  72 */       this.invokeOn = toInvokeOn;
/*     */     }
/*     */     
/*     */     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/*  76 */       Object result = null;
/*     */       try
/*     */       {
/*  79 */         result = method.invoke(this.invokeOn, args);
/*     */         
/*  81 */         if (result != null) {
/*  82 */           result = proxyIfInterfaceIsJdbc(result, result.getClass());
/*     */         }
/*     */       } catch (InvocationTargetException e) {
/*  85 */         if ((e.getTargetException() instanceof SQLException)) {
/*  86 */           WrapperBase.this.checkAndFireConnectionError((SQLException)e.getTargetException());
/*     */         } else {
/*  88 */           throw e;
/*     */         }
/*     */       }
/*     */       
/*  92 */       return result;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private Object proxyIfInterfaceIsJdbc(Object toProxy, Class<?> clazz)
/*     */     {
/* 104 */       Class<?>[] interfaces = clazz.getInterfaces();
/*     */       
/* 106 */       Class[] arr$ = interfaces;int len$ = arr$.length;int i$ = 0; if (i$ < len$) { Class<?> iclass = arr$[i$];
/* 107 */         String packageName = iclass.getPackage().getName();
/*     */         
/* 109 */         if (("java.sql".equals(packageName)) || ("javax.sql".equals(packageName))) {
/* 110 */           return Proxy.newProxyInstance(toProxy.getClass().getClassLoader(), interfaces, new ConnectionErrorFiringInvocationHandler(WrapperBase.this, toProxy));
/*     */         }
/*     */         
/* 113 */         return proxyIfInterfaceIsJdbc(toProxy, iclass);
/*     */       }
/*     */       
/* 116 */       return toProxy;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\jdbc2\optional\WrapperBase.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */