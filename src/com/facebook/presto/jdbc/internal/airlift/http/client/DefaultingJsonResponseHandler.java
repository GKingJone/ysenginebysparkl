/*    */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.json.JsonCodec;
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableSet;
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableSet.Builder;
/*    */ import com.facebook.presto.jdbc.internal.guava.io.ByteStreams;
/*    */ import com.facebook.presto.jdbc.internal.guava.net.MediaType;
/*    */ import com.facebook.presto.jdbc.internal.guava.primitives.Ints;
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
/*    */ public class DefaultingJsonResponseHandler<T>
/*    */   implements ResponseHandler<T, RuntimeException>
/*    */ {
/* 31 */   private static final MediaType MEDIA_TYPE_JSON = MediaType.create("application", "json");
/*    */   private final JsonCodec<T> jsonCodec;
/*    */   
/*    */   public static <T> DefaultingJsonResponseHandler<T> createDefaultingJsonResponseHandler(JsonCodec<T> jsonCodec, T defaultValue) {
/* 35 */     return new DefaultingJsonResponseHandler(jsonCodec, defaultValue);
/*    */   }
/*    */   
/*    */   public static <T> DefaultingJsonResponseHandler<T> createDefaultingJsonResponseHandler(JsonCodec<T> jsonCodec, T defaultValue, int firstSuccessfulResponseCode, int... otherSuccessfulResponseCodes)
/*    */   {
/* 40 */     return new DefaultingJsonResponseHandler(jsonCodec, defaultValue, firstSuccessfulResponseCode, otherSuccessfulResponseCodes);
/*    */   }
/*    */   
/*    */ 
/*    */   private final T defaultValue;
/*    */   
/*    */   private final Set<Integer> successfulResponseCodes;
/*    */   private DefaultingJsonResponseHandler(JsonCodec<T> jsonCodec, T defaultValue)
/*    */   {
/* 49 */     this(jsonCodec, defaultValue, 200, new int[] { 201, 202, 203, 204, 205, 206 });
/*    */   }
/*    */   
/*    */   private DefaultingJsonResponseHandler(JsonCodec<T> jsonCodec, T defaultValue, int firstSuccessfulResponseCode, int... otherSuccessfulResponseCodes)
/*    */   {
/* 54 */     this.jsonCodec = jsonCodec;
/* 55 */     this.defaultValue = defaultValue;
/* 56 */     this.successfulResponseCodes = ImmutableSet.builder().add(Integer.valueOf(firstSuccessfulResponseCode)).addAll(Ints.asList(otherSuccessfulResponseCodes)).build();
/*    */   }
/*    */   
/*    */ 
/*    */   public T handleException(Request request, Exception exception)
/*    */   {
/* 62 */     return (T)this.defaultValue;
/*    */   }
/*    */   
/*    */ 
/*    */   public T handle(Request request, Response response)
/*    */   {
/* 68 */     if (!this.successfulResponseCodes.contains(Integer.valueOf(response.getStatusCode()))) {
/* 69 */       return (T)this.defaultValue;
/*    */     }
/* 71 */     String contentType = response.getHeader("Content-Type");
/* 72 */     if (!MediaType.parse(contentType).is(MEDIA_TYPE_JSON)) {
/* 73 */       return (T)this.defaultValue;
/*    */     }
/*    */     try {
/* 76 */       return (T)this.jsonCodec.fromJson(ByteStreams.toByteArray(response.getInputStream()));
/*    */     }
/*    */     catch (Exception e) {}
/* 79 */     return (T)this.defaultValue;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\DefaultingJsonResponseHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */