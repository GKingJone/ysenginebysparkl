/*    */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableListMultimap;
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ListMultimap;
/*    */ import java.util.List;
/*    */ import javax.annotation.Nullable;
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
/*    */ public class StatusResponseHandler
/*    */   implements ResponseHandler<StatusResponse, RuntimeException>
/*    */ {
/* 29 */   private static final StatusResponseHandler statusResponseHandler = new StatusResponseHandler();
/*    */   
/*    */   public static StatusResponseHandler createStatusResponseHandler()
/*    */   {
/* 33 */     return statusResponseHandler;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public StatusResponse handleException(Request request, Exception exception)
/*    */   {
/* 43 */     throw ResponseHandlerUtils.propagate(request, exception);
/*    */   }
/*    */   
/*    */ 
/*    */   public StatusResponse handle(Request request, Response response)
/*    */   {
/* 49 */     return new StatusResponse(response.getStatusCode(), response.getStatusMessage(), response.getHeaders());
/*    */   }
/*    */   
/*    */   public static class StatusResponse
/*    */   {
/*    */     private final int statusCode;
/*    */     private final String statusMessage;
/*    */     private final ListMultimap<HeaderName, String> headers;
/*    */     
/*    */     public StatusResponse(int statusCode, String statusMessage, ListMultimap<HeaderName, String> headers)
/*    */     {
/* 60 */       this.statusCode = statusCode;
/* 61 */       this.statusMessage = statusMessage;
/* 62 */       this.headers = ImmutableListMultimap.copyOf(headers);
/*    */     }
/*    */     
/*    */     public int getStatusCode()
/*    */     {
/* 67 */       return this.statusCode;
/*    */     }
/*    */     
/*    */     public String getStatusMessage()
/*    */     {
/* 72 */       return this.statusMessage;
/*    */     }
/*    */     
/*    */     @Nullable
/*    */     public String getHeader(String name)
/*    */     {
/* 78 */       List<String> values = getHeaders().get(HeaderName.of(name));
/* 79 */       return values.isEmpty() ? null : (String)values.get(0);
/*    */     }
/*    */     
/*    */     public List<String> getHeaders(String name)
/*    */     {
/* 84 */       return this.headers.get(HeaderName.of(name));
/*    */     }
/*    */     
/*    */     public ListMultimap<HeaderName, String> getHeaders()
/*    */     {
/* 89 */       return this.headers;
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\StatusResponseHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */