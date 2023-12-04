/*    */ package com.facebook.presto.jdbc.internal.jetty.client;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request;
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
/*    */ public class HttpRequestException
/*    */   extends RuntimeException
/*    */ {
/*    */   private final Request request;
/*    */   
/*    */   public HttpRequestException(String message, Request request)
/*    */   {
/* 29 */     super(message);
/* 30 */     this.request = request;
/*    */   }
/*    */   
/*    */   public Request getRequest()
/*    */   {
/* 35 */     return this.request;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\HttpRequestException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */