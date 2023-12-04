/*     */ package com.facebook.presto.jdbc.internal.jetty.client.api;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpHeader;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Attributes;
/*     */ import java.net.URI;
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
/*     */ public abstract interface Authentication
/*     */ {
/*     */   public abstract boolean matches(String paramString1, URI paramURI, String paramString2);
/*     */   
/*     */   public abstract Result authenticate(Request paramRequest, ContentResponse paramContentResponse, HeaderInfo paramHeaderInfo, Attributes paramAttributes);
/*     */   
/*     */   public static abstract interface Result
/*     */   {
/*     */     public abstract URI getURI();
/*     */     
/*     */     public abstract void apply(Request paramRequest);
/*     */   }
/*     */   
/*     */   public static class HeaderInfo
/*     */   {
/*     */     private final String type;
/*     */     private final String realm;
/*     */     private final String params;
/*     */     private final HttpHeader header;
/*     */     
/*     */     public HeaderInfo(String type, String realm, String params, HttpHeader header)
/*     */     {
/*  80 */       this.type = type;
/*  81 */       this.realm = realm;
/*  82 */       this.params = params;
/*  83 */       this.header = header;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getType()
/*     */     {
/*  91 */       return this.type;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getRealm()
/*     */     {
/*  99 */       return this.realm;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getParameters()
/*     */     {
/* 107 */       return this.params;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public HttpHeader getHeader()
/*     */     {
/* 115 */       return this.header;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\api\Authentication.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */