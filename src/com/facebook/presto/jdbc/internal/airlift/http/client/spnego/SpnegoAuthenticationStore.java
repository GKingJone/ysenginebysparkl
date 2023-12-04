/*    */ package com.facebook.presto.jdbc.internal.airlift.http.client.spnego;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.airlift.units.Duration;
/*    */ import com.facebook.presto.jdbc.internal.guava.cache.Cache;
/*    */ import com.facebook.presto.jdbc.internal.guava.cache.CacheBuilder;
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.Authentication;
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.Authentication.Result;
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.AuthenticationStore;
/*    */ import java.net.URI;
/*    */ import java.util.Objects;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ 
/*    */ 
/*    */ public class SpnegoAuthenticationStore
/*    */   implements AuthenticationStore
/*    */ {
/*    */   private static final int CACHE_SIZE = 10000;
/* 18 */   private static final Duration CACHE_EXPIRE_TIME = new Duration(5.0D, TimeUnit.MINUTES);
/*    */   
/*    */   private static final int CONCURRENCY_LEVEL = 16;
/*    */   private final Cache<URI, Result> results;
/*    */   private final SpnegoAuthentication authentication;
/*    */   
/*    */   public SpnegoAuthenticationStore(SpnegoAuthentication authentication)
/*    */   {
/* 26 */     Objects.requireNonNull(authentication, "authentication is null");
/* 27 */     this.authentication = authentication;
/*    */     
/*    */ 
/*    */ 
/* 31 */     this.results = CacheBuilder.newBuilder().concurrencyLevel(16).maximumSize(10000L).expireAfterWrite(CACHE_EXPIRE_TIME.roundTo(TimeUnit.MINUTES), TimeUnit.MINUTES).build();
/*    */   }
/*    */   
/*    */ 
/*    */   public void addAuthentication(Authentication authentication)
/*    */   {
/* 37 */     throw new UnsupportedOperationException("addAuthentication is not supported");
/*    */   }
/*    */   
/*    */ 
/*    */   public void removeAuthentication(Authentication authentication)
/*    */   {
/* 43 */     throw new UnsupportedOperationException("removeAuthentication is not supported");
/*    */   }
/*    */   
/*    */ 
/*    */   public void clearAuthentications()
/*    */   {
/* 49 */     throw new UnsupportedOperationException("clearAuthentications is not supported");
/*    */   }
/*    */   
/*    */ 
/*    */   public Authentication findAuthentication(String type, URI uri, String realm)
/*    */   {
/* 55 */     if (this.authentication.matches(type, uri, realm)) {
/* 56 */       return this.authentication;
/*    */     }
/* 58 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */   public void addAuthenticationResult(Result result)
/*    */   {
/* 64 */     this.results.put(UriUtil.normalizedUri(result.getURI()), result);
/*    */   }
/*    */   
/*    */ 
/*    */   public void removeAuthenticationResult(Result result)
/*    */   {
/* 70 */     this.results.invalidate(UriUtil.normalizedUri(result.getURI()));
/*    */   }
/*    */   
/*    */ 
/*    */   public void clearAuthenticationResults()
/*    */   {
/* 76 */     this.results.invalidateAll();
/*    */   }
/*    */   
/*    */ 
/*    */   public Result findAuthenticationResult(URI uri)
/*    */   {
/* 82 */     Objects.requireNonNull(uri, "uri is null");
/* 83 */     if ("https".equalsIgnoreCase(uri.getScheme()))
/*    */     {
/* 85 */       return (Result)this.results.getIfPresent(UriUtil.normalizedUri(uri));
/*    */     }
/* 87 */     return null;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\spnego\SpnegoAuthenticationStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */