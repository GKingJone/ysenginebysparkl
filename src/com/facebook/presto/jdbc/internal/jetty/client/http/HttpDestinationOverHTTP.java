/*    */ package com.facebook.presto.jdbc.internal.jetty.client.http;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.HttpClient;
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.HttpExchange;
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.Origin;
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.PoolingHttpDestination;
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.SendFailure;
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
/*    */ public class HttpDestinationOverHTTP
/*    */   extends PoolingHttpDestination<HttpConnectionOverHTTP>
/*    */ {
/*    */   public HttpDestinationOverHTTP(HttpClient client, Origin origin)
/*    */   {
/* 31 */     super(client, origin);
/*    */   }
/*    */   
/*    */ 
/*    */   protected SendFailure send(HttpConnectionOverHTTP connection, HttpExchange exchange)
/*    */   {
/* 37 */     return connection.send(exchange);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\http\HttpDestinationOverHTTP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */