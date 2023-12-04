/*    */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects.ToStringHelper;
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
/*    */ @Beta
/*    */ public class UnexpectedResponseException
/*    */   extends RuntimeException
/*    */ {
/*    */   private final Request request;
/*    */   private final int statusCode;
/*    */   private final String statusMessage;
/*    */   private final ListMultimap<HeaderName, String> headers;
/*    */   
/*    */   public UnexpectedResponseException(Request request, Response response)
/*    */   {
/* 37 */     this(String.format("%d: %s", new Object[] { Integer.valueOf(response.getStatusCode()), response.getStatusMessage() }), request, response
/*    */     
/* 39 */       .getStatusCode(), response
/* 40 */       .getStatusMessage(), 
/* 41 */       ImmutableListMultimap.copyOf(response.getHeaders()));
/*    */   }
/*    */   
/*    */   public UnexpectedResponseException(String message, Request request, Response response)
/*    */   {
/* 46 */     this(message, request, response
/*    */     
/* 48 */       .getStatusCode(), response
/* 49 */       .getStatusMessage(), 
/* 50 */       ImmutableListMultimap.copyOf(response.getHeaders()));
/*    */   }
/*    */   
/*    */   public UnexpectedResponseException(String message, Request request, int statusCode, String statusMessage, ListMultimap<HeaderName, String> headers)
/*    */   {
/* 55 */     super(message);
/* 56 */     this.request = request;
/* 57 */     this.statusCode = statusCode;
/* 58 */     this.statusMessage = statusMessage;
/* 59 */     this.headers = ImmutableListMultimap.copyOf(headers);
/*    */   }
/*    */   
/*    */   public int getStatusCode()
/*    */   {
/* 64 */     return this.statusCode;
/*    */   }
/*    */   
/*    */   public String getStatusMessage()
/*    */   {
/* 69 */     return this.statusMessage;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public String getHeader(String name)
/*    */   {
/* 75 */     List<String> values = getHeaders().get(HeaderName.of(name));
/* 76 */     return values.isEmpty() ? null : (String)values.get(0);
/*    */   }
/*    */   
/*    */   public List<String> getHeaders(String name)
/*    */   {
/* 81 */     return this.headers.get(HeaderName.of(name));
/*    */   }
/*    */   
/*    */   public ListMultimap<HeaderName, String> getHeaders()
/*    */   {
/* 86 */     return this.headers;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 92 */     return 
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 97 */       MoreObjects.toStringHelper(this).add("request", this.request).add("statusCode", this.statusCode).add("statusMessage", this.statusMessage).add("headers", this.headers).toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\UnexpectedResponseException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */