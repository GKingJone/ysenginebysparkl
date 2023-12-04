/*    */ package com.mchange.v2.resourcepool;
/*    */ 
/*    */ import com.mchange.v2.log.MLevel;
/*    */ import com.mchange.v2.log.MLog;
/*    */ import com.mchange.v2.log.MLogger;
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
/*    */ final class ResourcePoolUtils
/*    */ {
/* 30 */   static final MLogger logger = MLog.getLogger(ResourcePoolUtils.class);
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   static final ResourcePoolException convertThrowable(String msg, Throwable t)
/*    */   {
/* 37 */     if (logger.isLoggable(MLevel.FINE)) {
/* 38 */       logger.log(MLevel.FINE, "Converting throwable to ResourcePoolException...", t);
/*    */     }
/* 40 */     if ((t instanceof ResourcePoolException)) {
/* 41 */       return (ResourcePoolException)t;
/*    */     }
/* 43 */     return new ResourcePoolException(msg, t);
/*    */   }
/*    */   
/*    */   static final ResourcePoolException convertThrowable(Throwable t) {
/* 47 */     return convertThrowable("Ouch! " + t.toString(), t);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\resourcepool\ResourcePoolUtils.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */