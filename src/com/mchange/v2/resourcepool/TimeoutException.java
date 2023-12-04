/*    */ package com.mchange.v2.resourcepool;
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
/*    */ public class TimeoutException
/*    */   extends ResourcePoolException
/*    */ {
/*    */   public TimeoutException(String msg, Throwable t)
/*    */   {
/* 29 */     super(msg, t);
/*    */   }
/*    */   
/* 32 */   public TimeoutException(Throwable t) { super(t); }
/*    */   
/*    */   public TimeoutException(String msg) {
/* 35 */     super(msg);
/*    */   }
/*    */   
/*    */   public TimeoutException() {}
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\resourcepool\TimeoutException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */