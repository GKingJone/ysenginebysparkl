/*    */ package com.mchange.v2.c3p0;
/*    */ 
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ import java.sql.Connection;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract interface C3P0ProxyConnection
/*    */   extends Connection
/*    */ {
/* 46 */   public static final Object RAW_CONNECTION = new Object();
/*    */   
/*    */   public abstract Object rawConnectionOperation(Method paramMethod, Object paramObject, Object[] paramArrayOfObject)
/*    */     throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException;
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\C3P0ProxyConnection.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */