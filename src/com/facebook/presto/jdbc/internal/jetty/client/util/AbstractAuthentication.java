/*    */ package com.facebook.presto.jdbc.internal.jetty.client.util;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.HttpClient;
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.Authentication;
/*    */ import java.net.URI;
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
/*    */ 
/*    */ public abstract class AbstractAuthentication
/*    */   implements Authentication
/*    */ {
/*    */   private final URI uri;
/*    */   private final String realm;
/*    */   
/*    */   public AbstractAuthentication(URI uri, String realm)
/*    */   {
/* 33 */     this.uri = uri;
/* 34 */     this.realm = realm;
/*    */   }
/*    */   
/*    */   public abstract String getType();
/*    */   
/*    */   public URI getURI()
/*    */   {
/* 41 */     return this.uri;
/*    */   }
/*    */   
/*    */   public String getRealm()
/*    */   {
/* 46 */     return this.realm;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean matches(String type, URI uri, String realm)
/*    */   {
/* 52 */     if (!getType().equalsIgnoreCase(type)) {
/* 53 */       return false;
/*    */     }
/* 55 */     if (!this.realm.equals(realm)) {
/* 56 */       return false;
/*    */     }
/* 58 */     return matchesURI(this.uri, uri);
/*    */   }
/*    */   
/*    */   public static boolean matchesURI(URI uri1, URI uri2)
/*    */   {
/* 63 */     String scheme = uri1.getScheme();
/* 64 */     if (uri1.getScheme().equalsIgnoreCase(scheme))
/*    */     {
/* 66 */       if (uri1.getHost().equalsIgnoreCase(uri2.getHost()))
/*    */       {
/*    */ 
/* 69 */         int thisPort = HttpClient.normalizePort(scheme, uri1.getPort());
/* 70 */         int thatPort = HttpClient.normalizePort(scheme, uri2.getPort());
/* 71 */         if (thisPort == thatPort)
/*    */         {
/*    */ 
/* 74 */           return uri2.getPath().startsWith(uri1.getPath());
/*    */         }
/*    */       }
/*    */     }
/* 78 */     return false;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\util\AbstractAuthentication.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */