/*    */ package com.facebook.presto.jdbc.internal.jetty.client.http;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.AbstractHttpClientTransport;
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.HttpDestination;
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.Origin;
/*    */ import com.facebook.presto.jdbc.internal.jetty.io.EndPoint;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.Promise;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedObject;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*    */ import java.io.IOException;
/*    */ import java.util.Map;
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
/*    */ @ManagedObject("The HTTP/1.1 client transport")
/*    */ public class HttpClientTransportOverHTTP
/*    */   extends AbstractHttpClientTransport
/*    */ {
/*    */   public HttpClientTransportOverHTTP()
/*    */   {
/* 37 */     this(Math.max(1, Runtime.getRuntime().availableProcessors() / 2));
/*    */   }
/*    */   
/*    */   public HttpClientTransportOverHTTP(int selectors)
/*    */   {
/* 42 */     super(selectors);
/*    */   }
/*    */   
/*    */ 
/*    */   public HttpDestination newHttpDestination(Origin origin)
/*    */   {
/* 48 */     return new HttpDestinationOverHTTP(getHttpClient(), origin);
/*    */   }
/*    */   
/*    */   public com.facebook.presto.jdbc.internal.jetty.io.Connection newConnection(EndPoint endPoint, Map<String, Object> context)
/*    */     throws IOException
/*    */   {
/* 54 */     HttpDestination destination = (HttpDestination)context.get("http.destination");
/*    */     
/* 56 */     Promise<com.facebook.presto.jdbc.internal.jetty.client.api.Connection> promise = (Promise)context.get("http.connection.promise");
/* 57 */     HttpConnectionOverHTTP connection = newHttpConnection(endPoint, destination, promise);
/* 58 */     if (LOG.isDebugEnabled())
/* 59 */       LOG.debug("Created {}", new Object[] { connection });
/* 60 */     return connection;
/*    */   }
/*    */   
/*    */   protected HttpConnectionOverHTTP newHttpConnection(EndPoint endPoint, HttpDestination destination, Promise<com.facebook.presto.jdbc.internal.jetty.client.api.Connection> promise)
/*    */   {
/* 65 */     return new HttpConnectionOverHTTP(endPoint, destination, promise);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\http\HttpClientTransportOverHTTP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */