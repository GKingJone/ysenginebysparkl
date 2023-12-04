/*    */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.json.JsonCodec;
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableSet;
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableSet.Builder;
/*    */ import com.facebook.presto.jdbc.internal.guava.io.ByteStreams;
/*    */ import com.facebook.presto.jdbc.internal.guava.net.MediaType;
/*    */ import com.facebook.presto.jdbc.internal.guava.primitives.Ints;
/*    */ import java.io.IOException;
/*    */ import java.nio.charset.StandardCharsets;
/*    */ import java.util.Set;
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
/*    */ public class JsonResponseHandler<T>
/*    */   implements ResponseHandler<T, RuntimeException>
/*    */ {
/* 33 */   private static final MediaType MEDIA_TYPE_JSON = MediaType.create("application", "json");
/*    */   private final JsonCodec<T> jsonCodec;
/*    */   private final Set<Integer> successfulResponseCodes;
/*    */   
/* 37 */   public static <T> JsonResponseHandler<T> createJsonResponseHandler(JsonCodec<T> jsonCodec) { return new JsonResponseHandler(jsonCodec); }
/*    */   
/*    */ 
/*    */   public static <T> JsonResponseHandler<T> createJsonResponseHandler(JsonCodec<T> jsonCodec, int firstSuccessfulResponseCode, int... otherSuccessfulResponseCodes)
/*    */   {
/* 42 */     return new JsonResponseHandler(jsonCodec, firstSuccessfulResponseCode, otherSuccessfulResponseCodes);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   private JsonResponseHandler(JsonCodec<T> jsonCodec)
/*    */   {
/* 50 */     this(jsonCodec, 200, new int[] { 201, 202, 203, 204, 205, 206 });
/*    */   }
/*    */   
/*    */   private JsonResponseHandler(JsonCodec<T> jsonCodec, int firstSuccessfulResponseCode, int... otherSuccessfulResponseCodes)
/*    */   {
/* 55 */     this.jsonCodec = jsonCodec;
/* 56 */     this.successfulResponseCodes = ImmutableSet.builder().add(Integer.valueOf(firstSuccessfulResponseCode)).addAll(Ints.asList(otherSuccessfulResponseCodes)).build();
/*    */   }
/*    */   
/*    */ 
/*    */   public T handleException(Request request, Exception exception)
/*    */   {
/* 62 */     throw ResponseHandlerUtils.propagate(request, exception);
/*    */   }
/*    */   
/*    */ 
/*    */   public T handle(Request request, Response response)
/*    */   {
/* 68 */     if (!this.successfulResponseCodes.contains(Integer.valueOf(response.getStatusCode())))
/*    */     {
/* 70 */       throw new UnexpectedResponseException(String.format("Expected response code to be %s, but was %d: %s", new Object[] { this.successfulResponseCodes, Integer.valueOf(response.getStatusCode()), response.getStatusMessage() }), request, response);
/*    */     }
/*    */     
/*    */ 
/* 74 */     String contentType = response.getHeader("Content-Type");
/* 75 */     if (contentType == null) {
/* 76 */       throw new UnexpectedResponseException("Content-Type is not set for response", request, response);
/*    */     }
/* 78 */     if (!MediaType.parse(contentType).is(MEDIA_TYPE_JSON)) {
/* 79 */       throw new UnexpectedResponseException("Expected application/json response from server but got " + contentType, request, response);
/*    */     }
/*    */     try
/*    */     {
/* 83 */       bytes = ByteStreams.toByteArray(response.getInputStream());
/*    */     } catch (IOException e) {
/*    */       byte[] bytes;
/* 86 */       throw new RuntimeException("Error reading response from server");
/*    */     }
/*    */     try {
/* 89 */       return (T)this.jsonCodec.fromJson(bytes);
/*    */     } catch (IllegalArgumentException e) {
/*    */       byte[] bytes;
/* 92 */       String json = new String(bytes, StandardCharsets.UTF_8);
/* 93 */       throw new IllegalArgumentException(String.format("Unable to create %s from JSON response:\n[%s]", new Object[] { this.jsonCodec.getType(), json }), e);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\JsonResponseHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */