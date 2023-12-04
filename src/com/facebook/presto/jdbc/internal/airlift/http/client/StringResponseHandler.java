/*     */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Charsets;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Optional;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Throwables;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableListMultimap;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ListMultimap;
/*     */ import com.facebook.presto.jdbc.internal.guava.io.ByteStreams;
/*     */ import com.facebook.presto.jdbc.internal.guava.net.MediaType;
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.List;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StringResponseHandler
/*     */   implements ResponseHandler<StringResponse, RuntimeException>
/*     */ {
/*  35 */   private static final StringResponseHandler STRING_RESPONSE_HANDLER = new StringResponseHandler();
/*     */   
/*     */   public static StringResponseHandler createStringResponseHandler()
/*     */   {
/*  39 */     return STRING_RESPONSE_HANDLER;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public StringResponse handleException(Request request, Exception exception)
/*     */   {
/*  49 */     throw ResponseHandlerUtils.propagate(request, exception);
/*     */   }
/*     */   
/*     */   public StringResponse handle(Request request, Response response)
/*     */   {
/*     */     try
/*     */     {
/*  56 */       String contentType = response.getHeader("Content-Type");
/*     */       
/*  58 */       if (contentType != null) {
/*  59 */         MediaType mediaType = MediaType.parse(contentType);
/*  60 */         return new StringResponse(response
/*  61 */           .getStatusCode(), response
/*  62 */           .getStatusMessage(), response
/*  63 */           .getHeaders(), new String(
/*  64 */           ByteStreams.toByteArray(response.getInputStream()), (Charset)mediaType.charset().or(Charsets.UTF_8)));
/*     */       }
/*     */       
/*  67 */       return new StringResponse(response
/*  68 */         .getStatusCode(), response
/*  69 */         .getStatusMessage(), response
/*  70 */         .getHeaders(), new String(
/*  71 */         ByteStreams.toByteArray(response.getInputStream()), Charsets.UTF_8));
/*     */     }
/*     */     catch (IOException e) {
/*  74 */       throw Throwables.propagate(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public static class StringResponse
/*     */   {
/*     */     private final int statusCode;
/*     */     private final String statusMessage;
/*     */     private final ListMultimap<HeaderName, String> headers;
/*     */     private final String body;
/*     */     
/*     */     public StringResponse(int statusCode, String statusMessage, ListMultimap<HeaderName, String> headers, String body)
/*     */     {
/*  87 */       this.statusCode = statusCode;
/*  88 */       this.statusMessage = statusMessage;
/*  89 */       this.headers = ImmutableListMultimap.copyOf(headers);
/*  90 */       this.body = body;
/*     */     }
/*     */     
/*     */     public int getStatusCode()
/*     */     {
/*  95 */       return this.statusCode;
/*     */     }
/*     */     
/*     */     public String getStatusMessage()
/*     */     {
/* 100 */       return this.statusMessage;
/*     */     }
/*     */     
/*     */     public String getBody()
/*     */     {
/* 105 */       return this.body;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     public String getHeader(String name)
/*     */     {
/* 111 */       List<String> values = getHeaders().get(HeaderName.of(name));
/* 112 */       return values.isEmpty() ? null : (String)values.get(0);
/*     */     }
/*     */     
/*     */     public List<String> getHeaders(String name)
/*     */     {
/* 117 */       return this.headers.get(HeaderName.of(name));
/*     */     }
/*     */     
/*     */     public ListMultimap<HeaderName, String> getHeaders()
/*     */     {
/* 122 */       return this.headers;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\StringResponseHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */