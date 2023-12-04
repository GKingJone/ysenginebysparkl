/*    */ package com.mchange.v2.resourcepool;
/*    */ 
/*    */ import com.mchange.lang.PotentiallySecondaryException;
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
/*    */ public class ResourcePoolException
/*    */   extends PotentiallySecondaryException
/*    */ {
/*    */   public ResourcePoolException(String msg, Throwable t)
/*    */   {
/* 31 */     super(msg, t);
/*    */   }
/*    */   
/* 34 */   public ResourcePoolException(Throwable t) { super(t); }
/*    */   
/*    */   public ResourcePoolException(String msg) {
/* 37 */     super(msg);
/*    */   }
/*    */   
/*    */   public ResourcePoolException() {}
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\resourcepool\ResourcePoolException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */