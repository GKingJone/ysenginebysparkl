/*    */ package com.mchange.v2.util;
/*    */ 
/*    */ import com.mchange.v2.lang.VersionUtils;
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
/*    */ public class ResourceClosedException
/*    */   extends RuntimeException
/*    */ {
/*    */   Throwable rootCause;
/*    */   
/*    */   public ResourceClosedException(String msg, Throwable t)
/*    */   {
/* 42 */     super(msg);
/* 43 */     setRootCause(t);
/*    */   }
/*    */   
/*    */ 
/*    */   public ResourceClosedException(Throwable t)
/*    */   {
/* 49 */     setRootCause(t);
/*    */   }
/*    */   
/*    */   public ResourceClosedException(String msg) {
/* 53 */     super(msg);
/*    */   }
/*    */   
/*    */   public ResourceClosedException() {}
/*    */   
/*    */   public Throwable getCause() {
/* 59 */     return this.rootCause;
/*    */   }
/*    */   
/*    */   private void setRootCause(Throwable t) {
/* 63 */     this.rootCause = t;
/* 64 */     if (VersionUtils.isAtLeastJavaVersion14()) {
/* 65 */       initCause(t);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\util\ResourceClosedException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */