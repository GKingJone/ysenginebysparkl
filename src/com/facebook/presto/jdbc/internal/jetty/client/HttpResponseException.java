/*    */ package com.facebook.presto.jdbc.internal.jetty.client;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response;
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
/*    */ public class HttpResponseException
/*    */   extends RuntimeException
/*    */ {
/*    */   private final Response response;
/*    */   
/*    */   public HttpResponseException(String message, Response response)
/*    */   {
/* 29 */     super(message);
/* 30 */     this.response = response;
/*    */   }
/*    */   
/*    */   public Response getResponse()
/*    */   {
/* 35 */     return this.response;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\HttpResponseException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */