/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PrestoException
/*    */   extends RuntimeException
/*    */ {
/*    */   private final ErrorCode errorCode;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public PrestoException(ErrorCodeSupplier errorCode, String message)
/*    */   {
/* 23 */     this(errorCode, message, null);
/*    */   }
/*    */   
/*    */   public PrestoException(ErrorCodeSupplier errorCode, Throwable throwable)
/*    */   {
/* 28 */     this(errorCode, null, throwable);
/*    */   }
/*    */   
/*    */   public PrestoException(ErrorCodeSupplier errorCodeSupplier, String message, Throwable cause)
/*    */   {
/* 33 */     super(message, cause);
/* 34 */     this.errorCode = errorCodeSupplier.toErrorCode();
/*    */   }
/*    */   
/*    */   public PrestoException(ErrorCodeSupplier errorCodeSupplier, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
/*    */   {
/* 39 */     super(message, cause, enableSuppression, writableStackTrace);
/* 40 */     this.errorCode = errorCodeSupplier.toErrorCode();
/*    */   }
/*    */   
/*    */   public ErrorCode getErrorCode()
/*    */   {
/* 45 */     return this.errorCode;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 51 */     String message = super.getMessage();
/* 52 */     if ((message == null) && (getCause() != null)) {
/* 53 */       message = getCause().getMessage();
/*    */     }
/* 55 */     if (message == null) {
/* 56 */       message = this.errorCode.getName();
/*    */     }
/* 58 */     return message;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\PrestoException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */