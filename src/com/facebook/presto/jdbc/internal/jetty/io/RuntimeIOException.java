/*    */ package com.facebook.presto.jdbc.internal.jetty.io;
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
/*    */ public class RuntimeIOException
/*    */   extends RuntimeException
/*    */ {
/*    */   public RuntimeIOException() {}
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
/*    */   public RuntimeIOException(String message)
/*    */   {
/* 36 */     super(message);
/*    */   }
/*    */   
/*    */   public RuntimeIOException(Throwable cause)
/*    */   {
/* 41 */     super(cause);
/*    */   }
/*    */   
/*    */   public RuntimeIOException(String message, Throwable cause)
/*    */   {
/* 46 */     super(message, cause);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\RuntimeIOException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */