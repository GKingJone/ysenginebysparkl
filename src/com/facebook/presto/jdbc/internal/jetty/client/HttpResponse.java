/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.ResponseListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpFields;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpVersion;
/*     */ import java.util.ArrayList;
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
/*     */ public class HttpResponse
/*     */   implements Response
/*     */ {
/*  31 */   private final HttpFields headers = new HttpFields();
/*     */   private final Request request;
/*     */   private final List<ResponseListener> listeners;
/*     */   private HttpVersion version;
/*     */   private int status;
/*     */   private String reason;
/*     */   
/*     */   public HttpResponse(Request request, List<ResponseListener> listeners)
/*     */   {
/*  40 */     this.request = request;
/*  41 */     this.listeners = listeners;
/*     */   }
/*     */   
/*     */ 
/*     */   public Request getRequest()
/*     */   {
/*  47 */     return this.request;
/*     */   }
/*     */   
/*     */   public HttpVersion getVersion()
/*     */   {
/*  52 */     return this.version;
/*     */   }
/*     */   
/*     */   public HttpResponse version(HttpVersion version)
/*     */   {
/*  57 */     this.version = version;
/*  58 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getStatus()
/*     */   {
/*  64 */     return this.status;
/*     */   }
/*     */   
/*     */   public HttpResponse status(int status)
/*     */   {
/*  69 */     this.status = status;
/*  70 */     return this;
/*     */   }
/*     */   
/*     */   public String getReason()
/*     */   {
/*  75 */     return this.reason;
/*     */   }
/*     */   
/*     */   public HttpResponse reason(String reason)
/*     */   {
/*  80 */     this.reason = reason;
/*  81 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public HttpFields getHeaders()
/*     */   {
/*  87 */     return this.headers;
/*     */   }
/*     */   
/*     */ 
/*     */   public <T extends ResponseListener> List<T> getListeners(Class<T> type)
/*     */   {
/*  93 */     ArrayList<T> result = new ArrayList();
/*  94 */     for (ResponseListener listener : this.listeners)
/*  95 */       if ((type == null) || (type.isInstance(listener)))
/*  96 */         result.add(listener);
/*  97 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean abort(Throwable cause)
/*     */   {
/* 103 */     return this.request.abort(cause);
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 109 */     return String.format("%s[%s %d %s]@%x", new Object[] { HttpResponse.class.getSimpleName(), getVersion(), Integer.valueOf(getStatus()), getReason(), Integer.valueOf(hashCode()) });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\HttpResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */