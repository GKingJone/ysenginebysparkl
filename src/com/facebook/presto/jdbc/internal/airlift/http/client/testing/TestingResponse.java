/*    */ package com.facebook.presto.jdbc.internal.airlift.http.client.testing;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.http.client.HeaderName;
/*    */ import com.facebook.presto.jdbc.internal.airlift.http.client.HttpStatus;
/*    */ import com.facebook.presto.jdbc.internal.airlift.http.client.Response;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Charsets;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects.ToStringHelper;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableListMultimap;
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableListMultimap.Builder;
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ListMultimap;
/*    */ import com.facebook.presto.jdbc.internal.guava.io.CountingInputStream;
/*    */ import com.facebook.presto.jdbc.internal.guava.net.MediaType;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.util.Map.Entry;
/*    */ 
/*    */ 
/*    */ public class TestingResponse
/*    */   implements Response
/*    */ {
/*    */   private final HttpStatus status;
/*    */   private final ListMultimap<HeaderName, String> headers;
/*    */   private final CountingInputStream countingInputStream;
/*    */   
/*    */   public TestingResponse(HttpStatus status, ListMultimap<String, String> headers, byte[] bytes)
/*    */   {
/* 30 */     this(status, headers, new ByteArrayInputStream((byte[])Preconditions.checkNotNull(bytes, "bytes is null")));
/*    */   }
/*    */   
/*    */   public TestingResponse(HttpStatus status, ListMultimap<String, String> headers, InputStream input)
/*    */   {
/* 35 */     this.status = ((HttpStatus)Preconditions.checkNotNull(status, "status is null"));
/* 36 */     this.headers = ImmutableListMultimap.copyOf(toHeaderMap((ListMultimap)Preconditions.checkNotNull(headers, "headers is null")));
/* 37 */     this.countingInputStream = new CountingInputStream((InputStream)Preconditions.checkNotNull(input, "input is null"));
/*    */   }
/*    */   
/*    */ 
/*    */   public int getStatusCode()
/*    */   {
/* 43 */     return this.status.code();
/*    */   }
/*    */   
/*    */ 
/*    */   public String getStatusMessage()
/*    */   {
/* 49 */     return this.status.reason();
/*    */   }
/*    */   
/*    */ 
/*    */   public ListMultimap<HeaderName, String> getHeaders()
/*    */   {
/* 55 */     return this.headers;
/*    */   }
/*    */   
/*    */ 
/*    */   public long getBytesRead()
/*    */   {
/* 61 */     return this.countingInputStream.getCount();
/*    */   }
/*    */   
/*    */ 
/*    */   public InputStream getInputStream()
/*    */     throws IOException
/*    */   {
/* 68 */     return this.countingInputStream;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 74 */     return 
/*    */     
/*    */ 
/*    */ 
/* 78 */       MoreObjects.toStringHelper(this).add("statusCode", getStatusCode()).add("statusMessage", getStatusMessage()).add("headers", getHeaders()).toString();
/*    */   }
/*    */   
/*    */   public static ListMultimap<String, String> contentType(MediaType type)
/*    */   {
/* 83 */     return ImmutableListMultimap.of("Content-Type", type.toString());
/*    */   }
/*    */   
/*    */   public static Response mockResponse(HttpStatus status, MediaType type, String content)
/*    */   {
/* 88 */     return new TestingResponse(status, contentType(type), content.getBytes(Charsets.UTF_8));
/*    */   }
/*    */   
/*    */   private static ListMultimap<HeaderName, String> toHeaderMap(ListMultimap<String, String> headers)
/*    */   {
/* 93 */     Builder<HeaderName, String> builder = ImmutableListMultimap.builder();
/* 94 */     for (Map.Entry<String, String> entry : headers.entries()) {
/* 95 */       builder.put(HeaderName.of((String)entry.getKey()), entry.getValue());
/*    */     }
/* 97 */     return builder.build();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\testing\TestingResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */