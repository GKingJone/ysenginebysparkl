/*    */ package com.mchange.v2.c3p0;
/*    */ 
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.Statement;
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
/*    */ public abstract interface C3P0ProxyStatement
/*    */   extends Statement
/*    */ {
/* 46 */   public static final Object RAW_STATEMENT = new Object();
/*    */   
/*    */   public abstract Object rawStatementOperation(Method paramMethod, Object paramObject, Object[] paramArrayOfObject)
/*    */     throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException;
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\C3P0ProxyStatement.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */