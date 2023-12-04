/*    */ package com.facebook.presto.jdbc.internal.spi;
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
/*    */ public abstract class NotFoundException
/*    */   extends PrestoException
/*    */ {
/*    */   protected NotFoundException()
/*    */   {
/* 23 */     this(null, null);
/*    */   }
/*    */   
/*    */   protected NotFoundException(String message)
/*    */   {
/* 28 */     this(message, null);
/*    */   }
/*    */   
/*    */   protected NotFoundException(Throwable cause)
/*    */   {
/* 33 */     this(null, cause);
/*    */   }
/*    */   
/*    */   protected NotFoundException(String message, Throwable cause)
/*    */   {
/* 38 */     super(StandardErrorCode.NOT_FOUND, message, cause);
/*    */   }
/*    */   
/*    */   protected NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
/*    */   {
/* 43 */     super(StandardErrorCode.NOT_FOUND, message, cause, enableSuppression, writableStackTrace);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\NotFoundException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */