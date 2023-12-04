/*    */ package com.facebook.presto.jdbc.internal.jetty.client;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SendFailure
/*    */ {
/*    */   public final Throwable failure;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public final boolean retry;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public SendFailure(Throwable failure, boolean retry)
/*    */   {
/* 28 */     this.failure = failure;
/* 29 */     this.retry = retry;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 35 */     return String.format("%s[failure=%s,retry=%b]", new Object[] { super.toString(), this.failure, Boolean.valueOf(this.retry) });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\SendFailure.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */