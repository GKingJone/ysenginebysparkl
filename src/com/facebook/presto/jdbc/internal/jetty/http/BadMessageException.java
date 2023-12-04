/*    */ package com.facebook.presto.jdbc.internal.jetty.http;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BadMessageException
/*    */   extends RuntimeException
/*    */ {
/*    */   final int _code;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   final String _reason;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public BadMessageException()
/*    */   {
/* 35 */     this(400, null);
/*    */   }
/*    */   
/*    */   public BadMessageException(int code)
/*    */   {
/* 40 */     this(code, null);
/*    */   }
/*    */   
/*    */   public BadMessageException(String reason)
/*    */   {
/* 45 */     this(400, reason);
/*    */   }
/*    */   
/*    */   public BadMessageException(int code, String reason)
/*    */   {
/* 50 */     super(code + ": " + reason);
/* 51 */     this._code = code;
/* 52 */     this._reason = reason;
/*    */   }
/*    */   
/*    */   public BadMessageException(int code, String reason, Throwable cause)
/*    */   {
/* 57 */     super(code + ": " + reason, cause);
/* 58 */     this._code = code;
/* 59 */     this._reason = reason;
/*    */   }
/*    */   
/*    */   public int getCode()
/*    */   {
/* 64 */     return this._code;
/*    */   }
/*    */   
/*    */   public String getReason()
/*    */   {
/* 69 */     return this._reason;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\BadMessageException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */