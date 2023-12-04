/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.ContentResponse;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.ResponseListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpFields;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpVersion;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.charset.UnsupportedCharsetException;
/*     */ import java.util.List;
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
/*     */ public class HttpContentResponse
/*     */   implements ContentResponse
/*     */ {
/*     */   private final Response response;
/*     */   private final byte[] content;
/*     */   private final String mediaType;
/*     */   private final String encoding;
/*     */   
/*     */   public HttpContentResponse(Response response, byte[] content, String mediaType, String encoding)
/*     */   {
/*  41 */     this.response = response;
/*  42 */     this.content = content;
/*  43 */     this.mediaType = mediaType;
/*  44 */     this.encoding = encoding;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request getRequest()
/*     */   {
/*  50 */     return this.response.getRequest();
/*     */   }
/*     */   
/*     */ 
/*     */   public <T extends ResponseListener> List<T> getListeners(Class<T> listenerClass)
/*     */   {
/*  56 */     return this.response.getListeners(listenerClass);
/*     */   }
/*     */   
/*     */ 
/*     */   public HttpVersion getVersion()
/*     */   {
/*  62 */     return this.response.getVersion();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getStatus()
/*     */   {
/*  68 */     return this.response.getStatus();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getReason()
/*     */   {
/*  74 */     return this.response.getReason();
/*     */   }
/*     */   
/*     */ 
/*     */   public HttpFields getHeaders()
/*     */   {
/*  80 */     return this.response.getHeaders();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean abort(Throwable cause)
/*     */   {
/*  86 */     return this.response.abort(cause);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getMediaType()
/*     */   {
/*  92 */     return this.mediaType;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getEncoding()
/*     */   {
/*  98 */     return this.encoding;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getContent()
/*     */   {
/* 104 */     return this.content;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getContentAsString()
/*     */   {
/* 110 */     String encoding = this.encoding;
/* 111 */     if (encoding == null)
/*     */     {
/* 113 */       return new String(getContent(), StandardCharsets.UTF_8);
/*     */     }
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 119 */       return new String(getContent(), encoding);
/*     */     }
/*     */     catch (UnsupportedEncodingException e)
/*     */     {
/* 123 */       throw new UnsupportedCharsetException(encoding);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 131 */     return String.format("%s[%s %d %s - %d bytes]", new Object[] {HttpContentResponse.class
/* 132 */       .getSimpleName(), 
/* 133 */       getVersion(), 
/* 134 */       Integer.valueOf(getStatus()), 
/* 135 */       getReason(), 
/* 136 */       Integer.valueOf(getContent().length) });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\HttpContentResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */