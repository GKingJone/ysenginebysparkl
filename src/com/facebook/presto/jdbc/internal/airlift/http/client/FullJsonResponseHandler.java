/*     */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.json.JsonCodec;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects.ToStringHelper;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Optional;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableListMultimap;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ListMultimap;
/*     */ import com.facebook.presto.jdbc.internal.guava.io.ByteStreams;
/*     */ import com.facebook.presto.jdbc.internal.guava.net.MediaType;
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
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
/*     */ 
/*     */ 
/*     */ public class FullJsonResponseHandler<T>
/*     */   implements ResponseHandler<JsonResponse<T>, RuntimeException>
/*     */ {
/*  40 */   private static final MediaType MEDIA_TYPE_JSON = MediaType.create("application", "json");
/*     */   private final JsonCodec<T> jsonCodec;
/*     */   
/*     */   public static <T> FullJsonResponseHandler<T> createFullJsonResponseHandler(JsonCodec<T> jsonCodec) {
/*  44 */     return new FullJsonResponseHandler(jsonCodec);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private FullJsonResponseHandler(JsonCodec<T> jsonCodec)
/*     */   {
/*  51 */     this.jsonCodec = jsonCodec;
/*     */   }
/*     */   
/*     */ 
/*     */   public JsonResponse<T> handleException(Request request, Exception exception)
/*     */   {
/*  57 */     throw ResponseHandlerUtils.propagate(request, exception);
/*     */   }
/*     */   
/*     */ 
/*     */   public JsonResponse<T> handle(Request request, Response response)
/*     */   {
/*  63 */     byte[] bytes = readResponseBytes(response);
/*  64 */     String contentType = response.getHeader("Content-Type");
/*  65 */     if ((contentType == null) || (!MediaType.parse(contentType).is(MEDIA_TYPE_JSON))) {
/*  66 */       return new JsonResponse(response.getStatusCode(), response.getStatusMessage(), response.getHeaders(), bytes);
/*     */     }
/*  68 */     return new JsonResponse(response.getStatusCode(), response.getStatusMessage(), response.getHeaders(), this.jsonCodec, bytes);
/*     */   }
/*     */   
/*     */   private static byte[] readResponseBytes(Response response)
/*     */   {
/*     */     try {
/*  74 */       return ByteStreams.toByteArray(response.getInputStream());
/*     */     }
/*     */     catch (IOException e) {
/*  77 */       throw new RuntimeException("Error reading response from server", e);
/*     */     }
/*     */   }
/*     */   
/*     */   public static class JsonResponse<T>
/*     */   {
/*     */     private final int statusCode;
/*     */     private final String statusMessage;
/*     */     private final ListMultimap<HeaderName, String> headers;
/*     */     private final boolean hasValue;
/*     */     private final byte[] jsonBytes;
/*     */     private final byte[] responseBytes;
/*     */     private final T value;
/*     */     private final IllegalArgumentException exception;
/*     */     
/*     */     public JsonResponse(int statusCode, String statusMessage, ListMultimap<HeaderName, String> headers, byte[] responseBytes)
/*     */     {
/*  94 */       this.statusCode = statusCode;
/*  95 */       this.statusMessage = statusMessage;
/*  96 */       this.headers = ImmutableListMultimap.copyOf(headers);
/*     */       
/*  98 */       this.hasValue = false;
/*  99 */       this.jsonBytes = null;
/* 100 */       this.responseBytes = ((byte[])Preconditions.checkNotNull(responseBytes, "responseBytes is null"));
/* 101 */       this.value = null;
/* 102 */       this.exception = null;
/*     */     }
/*     */     
/*     */ 
/*     */     public JsonResponse(int statusCode, String statusMessage, ListMultimap<HeaderName, String> headers, JsonCodec<T> jsonCodec, byte[] jsonBytes)
/*     */     {
/* 108 */       this.statusCode = statusCode;
/* 109 */       this.statusMessage = statusMessage;
/* 110 */       this.headers = ImmutableListMultimap.copyOf(headers);
/*     */       
/* 112 */       this.jsonBytes = ((byte[])Preconditions.checkNotNull(jsonBytes, "jsonBytes is null"));
/* 113 */       this.responseBytes = ((byte[])Preconditions.checkNotNull(jsonBytes, "responseBytes is null"));
/*     */       
/* 115 */       T value = null;
/* 116 */       IllegalArgumentException exception = null;
/*     */       try {
/* 118 */         value = jsonCodec.fromJson(jsonBytes);
/*     */       }
/*     */       catch (IllegalArgumentException e) {
/* 121 */         exception = new IllegalArgumentException(String.format("Unable to create %s from JSON response:\n[%s]", new Object[] { jsonCodec.getType(), getJson() }), e);
/*     */       }
/* 123 */       this.hasValue = (exception == null);
/* 124 */       this.value = value;
/* 125 */       this.exception = exception;
/*     */     }
/*     */     
/*     */     public int getStatusCode()
/*     */     {
/* 130 */       return this.statusCode;
/*     */     }
/*     */     
/*     */     public String getStatusMessage()
/*     */     {
/* 135 */       return this.statusMessage;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     public String getHeader(String name)
/*     */     {
/* 141 */       List<String> values = getHeaders().get(HeaderName.of(name));
/* 142 */       return values.isEmpty() ? null : (String)values.get(0);
/*     */     }
/*     */     
/*     */     public List<String> getHeaders(String name)
/*     */     {
/* 147 */       return this.headers.get(HeaderName.of(name));
/*     */     }
/*     */     
/*     */     public ListMultimap<HeaderName, String> getHeaders()
/*     */     {
/* 152 */       return this.headers;
/*     */     }
/*     */     
/*     */     public boolean hasValue()
/*     */     {
/* 157 */       return this.hasValue;
/*     */     }
/*     */     
/*     */     public T getValue()
/*     */     {
/* 162 */       if (!this.hasValue) {
/* 163 */         throw new IllegalStateException("Response does not contain a JSON value", this.exception);
/*     */       }
/* 165 */       return (T)this.value;
/*     */     }
/*     */     
/*     */     public int getResponseSize()
/*     */     {
/* 170 */       return this.responseBytes.length;
/*     */     }
/*     */     
/*     */     public byte[] getResponseBytes()
/*     */     {
/* 175 */       return (byte[])this.responseBytes.clone();
/*     */     }
/*     */     
/*     */     public String getResponseBody()
/*     */     {
/* 180 */       return new String(this.responseBytes, getCharset());
/*     */     }
/*     */     
/*     */     public byte[] getJsonBytes()
/*     */     {
/* 185 */       return this.jsonBytes == null ? null : (byte[])this.jsonBytes.clone();
/*     */     }
/*     */     
/*     */     public String getJson()
/*     */     {
/* 190 */       return this.jsonBytes == null ? null : new String(this.jsonBytes, StandardCharsets.UTF_8);
/*     */     }
/*     */     
/*     */     public IllegalArgumentException getException()
/*     */     {
/* 195 */       return this.exception;
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 201 */       return 
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 207 */         MoreObjects.toStringHelper(this).add("statusCode", this.statusCode).add("statusMessage", this.statusMessage).add("headers", this.headers).add("hasValue", this.hasValue).add("value", this.value).toString();
/*     */     }
/*     */     
/*     */     private Charset getCharset()
/*     */     {
/* 212 */       String value = getHeader("Content-Type");
/* 213 */       if (value != null) {
/*     */         try {
/* 215 */           return (Charset)MediaType.parse(value).charset().or(StandardCharsets.UTF_8);
/*     */         }
/*     */         catch (RuntimeException localRuntimeException) {}
/*     */       }
/*     */       
/* 220 */       return StandardCharsets.UTF_8;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\FullJsonResponseHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */