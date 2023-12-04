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
/*    */ public class CannotAcquireResourceException
/*    */   extends ResourcePoolException
/*    */ {
/*    */   public CannotAcquireResourceException(String msg, Throwable t)
/*    */   {
/* 29 */     super(msg, t);
/*    */   }
/*    */   
/* 32 */   public CannotAcquireResourceException(Throwable t) { super(t); }
/*    */   
/*    */   public CannotAcquireResourceException(String msg) {
/* 35 */     super(msg);
/*    */   }
/*    */   
/*    */   public CannotAcquireResourceException() {}
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\resourcepool\CannotAcquireResourceException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */