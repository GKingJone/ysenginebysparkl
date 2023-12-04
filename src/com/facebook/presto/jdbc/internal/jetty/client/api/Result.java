/*     */ package com.facebook.presto.jdbc.internal.jetty.client.api;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Result
/*     */ {
/*     */   private final Request request;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final Throwable requestFailure;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private final Response response;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private final Throwable responseFailure;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Result(Request request, Response response)
/*     */   {
/*  34 */     this(request, null, response, null);
/*     */   }
/*     */   
/*     */   public Result(Request request, Response response, Throwable responseFailure)
/*     */   {
/*  39 */     this(request, null, response, responseFailure);
/*     */   }
/*     */   
/*     */   public Result(Request request, Throwable requestFailure, Response response)
/*     */   {
/*  44 */     this(request, requestFailure, response, null);
/*     */   }
/*     */   
/*     */   public Result(Request request, Throwable requestFailure, Response response, Throwable responseFailure)
/*     */   {
/*  49 */     this.request = request;
/*  50 */     this.requestFailure = requestFailure;
/*  51 */     this.response = response;
/*  52 */     this.responseFailure = responseFailure;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Request getRequest()
/*     */   {
/*  60 */     return this.request;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Throwable getRequestFailure()
/*     */   {
/*  68 */     return this.requestFailure;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Response getResponse()
/*     */   {
/*  76 */     return this.response;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Throwable getResponseFailure()
/*     */   {
/*  84 */     return this.responseFailure;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isSucceeded()
/*     */   {
/*  92 */     return getFailure() == null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isFailed()
/*     */   {
/* 100 */     return !isSucceeded();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Throwable getFailure()
/*     */   {
/* 108 */     return this.responseFailure != null ? this.responseFailure : this.requestFailure;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 114 */     return String.format("%s[%s > %s] %s", new Object[] {Result.class
/* 115 */       .getSimpleName(), this.request, this.response, 
/*     */       
/*     */ 
/* 118 */       getFailure() });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\api\Result.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */