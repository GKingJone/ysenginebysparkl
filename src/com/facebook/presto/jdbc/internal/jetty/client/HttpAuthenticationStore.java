/*    */ package com.facebook.presto.jdbc.internal.jetty.client;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.Authentication;
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.Authentication.Result;
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.AuthenticationStore;
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.util.AbstractAuthentication;
/*    */ import java.net.URI;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ import java.util.concurrent.CopyOnWriteArrayList;
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
/*    */ public class HttpAuthenticationStore
/*    */   implements AuthenticationStore
/*    */ {
/* 33 */   private final List<Authentication> authentications = new CopyOnWriteArrayList();
/* 34 */   private final Map<URI, Result> results = new ConcurrentHashMap();
/*    */   
/*    */ 
/*    */   public void addAuthentication(Authentication authentication)
/*    */   {
/* 39 */     this.authentications.add(authentication);
/*    */   }
/*    */   
/*    */ 
/*    */   public void removeAuthentication(Authentication authentication)
/*    */   {
/* 45 */     this.authentications.remove(authentication);
/*    */   }
/*    */   
/*    */ 
/*    */   public void clearAuthentications()
/*    */   {
/* 51 */     this.authentications.clear();
/*    */   }
/*    */   
/*    */ 
/*    */   public Authentication findAuthentication(String type, URI uri, String realm)
/*    */   {
/* 57 */     for (Authentication authentication : this.authentications)
/*    */     {
/* 59 */       if (authentication.matches(type, uri, realm))
/* 60 */         return authentication;
/*    */     }
/* 62 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */   public void addAuthenticationResult(Result result)
/*    */   {
/* 68 */     this.results.put(result.getURI(), result);
/*    */   }
/*    */   
/*    */ 
/*    */   public void removeAuthenticationResult(Result result)
/*    */   {
/* 74 */     this.results.remove(result.getURI());
/*    */   }
/*    */   
/*    */ 
/*    */   public void clearAuthenticationResults()
/*    */   {
/* 80 */     this.results.clear();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public Result findAuthenticationResult(URI uri)
/*    */   {
/* 87 */     for (Entry<URI, Result> entry : this.results.entrySet())
/*    */     {
/* 89 */       if (AbstractAuthentication.matchesURI((URI)entry.getKey(), uri))
/* 90 */         return (Result)entry.getValue();
/*    */     }
/* 92 */     return null;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\HttpAuthenticationStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */