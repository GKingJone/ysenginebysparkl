/*    */ package com.facebook.presto.jdbc.internal.jetty.client;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request;
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response;
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.Listener;
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.Listener.Adapter;
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.Result;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http.HttpField;
/*    */ import com.facebook.presto.jdbc.internal.jetty.http.HttpHeader;
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
/*    */ public class RedirectProtocolHandler
/*    */   extends Adapter
/*    */   implements ProtocolHandler
/*    */ {
/*    */   public static final String NAME = "redirect";
/*    */   private final HttpRedirector redirector;
/*    */   
/*    */   public RedirectProtocolHandler(HttpClient client)
/*    */   {
/* 38 */     this.redirector = new HttpRedirector(client);
/*    */   }
/*    */   
/*    */ 
/*    */   public String getName()
/*    */   {
/* 44 */     return "redirect";
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean accept(Request request, Response response)
/*    */   {
/* 50 */     return (this.redirector.isRedirect(response)) && (request.isFollowRedirects());
/*    */   }
/*    */   
/*    */ 
/*    */   public Listener getResponseListener()
/*    */   {
/* 56 */     return this;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public boolean onHeader(Response response, HttpField field)
/*    */   {
/* 64 */     return field.getHeader() != HttpHeader.CONTENT_ENCODING;
/*    */   }
/*    */   
/*    */ 
/*    */   public void onComplete(Result result)
/*    */   {
/* 70 */     Request request = result.getRequest();
/* 71 */     Response response = result.getResponse();
/* 72 */     if (result.isSucceeded()) {
/* 73 */       this.redirector.redirect(request, response, null);
/*    */     } else {
/* 75 */       this.redirector.fail(request, response, result.getFailure());
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\RedirectProtocolHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */