/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.net.CookieManager;
/*     */ import java.net.CookieStore;
/*     */ import java.net.HttpCookie;
/*     */ import java.net.URI;
/*     */ import java.util.Collections;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpCookieStore
/*     */   implements CookieStore
/*     */ {
/*     */   private final CookieStore delegate;
/*     */   
/*     */   public HttpCookieStore()
/*     */   {
/*  38 */     this.delegate = new CookieManager().getCookieStore();
/*     */   }
/*     */   
/*     */ 
/*     */   public void add(URI uri, HttpCookie cookie)
/*     */   {
/*  44 */     this.delegate.add(uri, cookie);
/*     */   }
/*     */   
/*     */ 
/*     */   public List<HttpCookie> get(URI uri)
/*     */   {
/*  50 */     return this.delegate.get(uri);
/*     */   }
/*     */   
/*     */ 
/*     */   public List<HttpCookie> getCookies()
/*     */   {
/*  56 */     return this.delegate.getCookies();
/*     */   }
/*     */   
/*     */ 
/*     */   public List<URI> getURIs()
/*     */   {
/*  62 */     return this.delegate.getURIs();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean remove(URI uri, HttpCookie cookie)
/*     */   {
/*  68 */     return this.delegate.remove(uri, cookie);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean removeAll()
/*     */   {
/*  74 */     return this.delegate.removeAll();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class Empty
/*     */     implements CookieStore
/*     */   {
/*     */     public void add(URI uri, HttpCookie cookie) {}
/*     */     
/*     */ 
/*     */     public List<HttpCookie> get(URI uri)
/*     */     {
/*  87 */       return Collections.emptyList();
/*     */     }
/*     */     
/*     */ 
/*     */     public List<HttpCookie> getCookies()
/*     */     {
/*  93 */       return Collections.emptyList();
/*     */     }
/*     */     
/*     */ 
/*     */     public List<URI> getURIs()
/*     */     {
/*  99 */       return Collections.emptyList();
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean remove(URI uri, HttpCookie cookie)
/*     */     {
/* 105 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean removeAll()
/*     */     {
/* 111 */       return false;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\HttpCookieStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */