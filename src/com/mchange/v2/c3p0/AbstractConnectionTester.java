/*    */ package com.mchange.v2.c3p0;
/*    */ 
/*    */ import java.sql.Connection;
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
/*    */ public abstract class AbstractConnectionTester
/*    */   implements UnifiedConnectionTester
/*    */ {
/*    */   public abstract int activeCheckConnection(Connection paramConnection, String paramString, Throwable[] paramArrayOfThrowable);
/*    */   
/*    */   public abstract int statusOnException(Connection paramConnection, Throwable paramThrowable, String paramString, Throwable[] paramArrayOfThrowable);
/*    */   
/*    */   public int activeCheckConnection(Connection c)
/*    */   {
/* 67 */     return activeCheckConnection(c, null, null);
/*    */   }
/*    */   
/* 70 */   public int activeCheckConnection(Connection c, Throwable[] rootCauseOutParamHolder) { return activeCheckConnection(c, null, rootCauseOutParamHolder); }
/*    */   
/*    */   public int activeCheckConnection(Connection c, String preferredTestQuery) {
/* 73 */     return activeCheckConnection(c, preferredTestQuery, null);
/*    */   }
/*    */   
/* 76 */   public int statusOnException(Connection c, Throwable t) { return statusOnException(c, t, null, null); }
/*    */   
/*    */   public int statusOnException(Connection c, Throwable t, Throwable[] rootCauseOutParamHolder) {
/* 79 */     return statusOnException(c, t, null, rootCauseOutParamHolder);
/*    */   }
/*    */   
/* 82 */   public int statusOnException(Connection c, Throwable t, String preferredTestQuery) { return statusOnException(c, t, preferredTestQuery, null); }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\AbstractConnectionTester.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */