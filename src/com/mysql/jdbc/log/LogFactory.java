/*     */ package com.mysql.jdbc.log;
/*     */ 
/*     */ import com.mysql.jdbc.ExceptionInterceptor;
/*     */ import com.mysql.jdbc.SQLError;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LogFactory
/*     */ {
/*     */   public static Log getLogger(String className, String instanceName, ExceptionInterceptor exceptionInterceptor)
/*     */     throws SQLException
/*     */   {
/*  52 */     if (className == null) {
/*  53 */       throw SQLError.createSQLException("Logger class can not be NULL", "S1009", exceptionInterceptor);
/*     */     }
/*     */     
/*  56 */     if (instanceName == null) {
/*  57 */       throw SQLError.createSQLException("Logger instance name can not be NULL", "S1009", exceptionInterceptor);
/*     */     }
/*     */     try
/*     */     {
/*  61 */       Class<?> loggerClass = null;
/*     */       try
/*     */       {
/*  64 */         loggerClass = Class.forName(className);
/*     */       } catch (ClassNotFoundException nfe) {
/*  66 */         loggerClass = Class.forName(Log.class.getPackage().getName() + "." + className);
/*     */       }
/*     */       
/*  69 */       Constructor<?> constructor = loggerClass.getConstructor(new Class[] { String.class });
/*     */       
/*  71 */       return (Log)constructor.newInstance(new Object[] { instanceName });
/*     */     } catch (ClassNotFoundException cnfe) {
/*  73 */       SQLException sqlEx = SQLError.createSQLException("Unable to load class for logger '" + className + "'", "S1009", exceptionInterceptor);
/*     */       
/*  75 */       sqlEx.initCause(cnfe);
/*     */       
/*  77 */       throw sqlEx;
/*     */     } catch (NoSuchMethodException nsme) {
/*  79 */       SQLException sqlEx = SQLError.createSQLException("Logger class does not have a single-arg constructor that takes an instance name", "S1009", exceptionInterceptor);
/*     */       
/*  81 */       sqlEx.initCause(nsme);
/*     */       
/*  83 */       throw sqlEx;
/*     */     } catch (InstantiationException inse) {
/*  85 */       SQLException sqlEx = SQLError.createSQLException("Unable to instantiate logger class '" + className + "', exception in constructor?", "S1009", exceptionInterceptor);
/*     */       
/*  87 */       sqlEx.initCause(inse);
/*     */       
/*  89 */       throw sqlEx;
/*     */     } catch (InvocationTargetException ite) {
/*  91 */       SQLException sqlEx = SQLError.createSQLException("Unable to instantiate logger class '" + className + "', exception in constructor?", "S1009", exceptionInterceptor);
/*     */       
/*  93 */       sqlEx.initCause(ite);
/*     */       
/*  95 */       throw sqlEx;
/*     */     } catch (IllegalAccessException iae) {
/*  97 */       SQLException sqlEx = SQLError.createSQLException("Unable to instantiate logger class '" + className + "', constructor not public", "S1009", exceptionInterceptor);
/*     */       
/*  99 */       sqlEx.initCause(iae);
/*     */       
/* 101 */       throw sqlEx;
/*     */     } catch (ClassCastException cce) {
/* 103 */       SQLException sqlEx = SQLError.createSQLException("Logger class '" + className + "' does not implement the '" + Log.class.getName() + "' interface", "S1009", exceptionInterceptor);
/*     */       
/* 105 */       sqlEx.initCause(cce);
/*     */       
/* 107 */       throw sqlEx;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\log\LogFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */