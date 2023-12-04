/*    */ package com.facebook.presto.jdbc.internal.jetty.client;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request;
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http.HttpHeader;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WWWAuthenticationProtocolHandler
/*    */   extends AuthenticationProtocolHandler
/*    */ {
/*    */   public static final String NAME = "www-authenticate";
/* 37 */   private static final String ATTRIBUTE = WWWAuthenticationProtocolHandler.class.getName() + ".attribute";
/*    */   
/*    */   public WWWAuthenticationProtocolHandler(HttpClient client)
/*    */   {
/* 41 */     this(client, 16384);
/*    */   }
/*    */   
/*    */   public WWWAuthenticationProtocolHandler(HttpClient client, int maxContentLength)
/*    */   {
/* 46 */     super(client, maxContentLength);
/*    */   }
/*    */   
/*    */ 
/*    */   public String getName()
/*    */   {
/* 52 */     return "www-authenticate";
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean accept(Request request, Response response)
/*    */   {
/* 58 */     return response.getStatus() == 401;
/*    */   }
/*    */   
/*    */ 
/*    */   protected HttpHeader getAuthenticateHeader()
/*    */   {
/* 64 */     return HttpHeader.WWW_AUTHENTICATE;
/*    */   }
/*    */   
/*    */ 
/*    */   protected HttpHeader getAuthorizationHeader()
/*    */   {
/* 70 */     return HttpHeader.AUTHORIZATION;
/*    */   }
/*    */   
/*    */ 
/*    */   protected URI getAuthenticationURI(Request request)
/*    */   {
/* 76 */     return request.getURI();
/*    */   }
/*    */   
/*    */ 
/*    */   protected String getAuthenticationAttribute()
/*    */   {
/* 82 */     return ATTRIBUTE;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\WWWAuthenticationProtocolHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */