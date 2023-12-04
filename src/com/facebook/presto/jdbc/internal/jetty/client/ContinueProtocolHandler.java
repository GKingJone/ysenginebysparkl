/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.Listener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.ResponseListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Result;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.util.BufferingResponseListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpFields;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpHeader;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpHeaderValue;
/*     */ import java.util.Deque;
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
/*     */ public class ContinueProtocolHandler
/*     */   implements ProtocolHandler
/*     */ {
/*     */   public static final String NAME = "continue";
/*  36 */   private static final String ATTRIBUTE = ContinueProtocolHandler.class.getName() + ".100continue";
/*     */   
/*     */   private final ResponseNotifier notifier;
/*     */   
/*     */   public ContinueProtocolHandler()
/*     */   {
/*  42 */     this.notifier = new ResponseNotifier();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  48 */     return "continue";
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean accept(Request request, Response response)
/*     */   {
/*  54 */     boolean expect100 = request.getHeaders().contains(HttpHeader.EXPECT, HttpHeaderValue.CONTINUE.asString());
/*  55 */     HttpConversation conversation = ((HttpRequest)request).getConversation();
/*  56 */     boolean handled100 = conversation.getAttribute(ATTRIBUTE) != null;
/*  57 */     return (expect100) && (!handled100);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Listener getResponseListener()
/*     */   {
/*  64 */     return new ContinueListener();
/*     */   }
/*     */   
/*     */ 
/*     */   protected class ContinueListener
/*     */     extends BufferingResponseListener
/*     */   {
/*     */     protected ContinueListener() {}
/*     */     
/*     */     public void onSuccess(Response response)
/*     */     {
/*  75 */       HttpConversation conversation = ((HttpRequest)response.getRequest()).getConversation();
/*     */       
/*  77 */       conversation.setAttribute(ContinueProtocolHandler.ATTRIBUTE, Boolean.TRUE);
/*     */       
/*     */ 
/*  80 */       conversation.updateResponseListeners(null);
/*     */       
/*  82 */       HttpExchange exchange = (HttpExchange)conversation.getExchanges().peekLast();
/*  83 */       assert (exchange.getResponse() == response);
/*  84 */       switch (response.getStatus())
/*     */       {
/*     */ 
/*     */ 
/*     */       case 100: 
/*  89 */         exchange.resetResponse();
/*  90 */         exchange.proceed(null);
/*  91 */         break;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       default: 
/*  98 */         List<ResponseListener> listeners = exchange.getResponseListeners();
/*  99 */         HttpContentResponse contentResponse = new HttpContentResponse(response, getContent(), getMediaType(), getEncoding());
/* 100 */         ContinueProtocolHandler.this.notifier.forwardSuccess(listeners, contentResponse);
/* 101 */         exchange.proceed(new HttpRequestException("Expectation failed", exchange.getRequest()));
/* 102 */         break;
/*     */       }
/*     */       
/*     */     }
/*     */     
/*     */ 
/*     */     public void onFailure(Response response, Throwable failure)
/*     */     {
/* 110 */       HttpConversation conversation = ((HttpRequest)response.getRequest()).getConversation();
/*     */       
/* 112 */       conversation.setAttribute(ContinueProtocolHandler.ATTRIBUTE, Boolean.TRUE);
/*     */       
/* 114 */       conversation.updateResponseListeners(null);
/*     */       
/* 116 */       HttpExchange exchange = (HttpExchange)conversation.getExchanges().peekLast();
/* 117 */       assert (exchange.getResponse() == response);
/* 118 */       List<ResponseListener> listeners = exchange.getResponseListeners();
/* 119 */       HttpContentResponse contentResponse = new HttpContentResponse(response, getContent(), getMediaType(), getEncoding());
/* 120 */       ContinueProtocolHandler.this.notifier.forwardFailureComplete(listeners, exchange.getRequest(), exchange.getRequestFailure(), contentResponse, failure);
/*     */     }
/*     */     
/*     */     public void onComplete(Result result) {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\ContinueProtocolHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */