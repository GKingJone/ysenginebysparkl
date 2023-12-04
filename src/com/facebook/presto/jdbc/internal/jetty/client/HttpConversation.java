/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.ResponseListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.AttributesMap;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Deque;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.ConcurrentLinkedDeque;
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
/*     */ public class HttpConversation
/*     */   extends AttributesMap
/*     */ {
/*  31 */   private final Deque<HttpExchange> exchanges = new ConcurrentLinkedDeque();
/*     */   private volatile List<Response.ResponseListener> listeners;
/*     */   
/*     */   public Deque<HttpExchange> getExchanges()
/*     */   {
/*  36 */     return this.exchanges;
/*     */   }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public List<Response.ResponseListener> getResponseListeners()
/*     */   {
/*  99 */     return this.listeners;
/*     */   }
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
/*     */   public void updateResponseListeners(Response.ResponseListener overrideListener)
/*     */   {
/* 116 */     List<Response.ResponseListener> listeners = new ArrayList();
/* 117 */     HttpExchange firstExchange = (HttpExchange)this.exchanges.peekFirst();
/* 118 */     HttpExchange lastExchange = (HttpExchange)this.exchanges.peekLast();
/* 119 */     if (firstExchange == lastExchange)
/*     */     {
/* 121 */       if (overrideListener != null) {
/* 122 */         listeners.add(overrideListener);
/*     */       } else {
/* 124 */         listeners.addAll(firstExchange.getResponseListeners());
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 129 */       listeners.addAll(lastExchange.getResponseListeners());
/* 130 */       if (overrideListener != null) {
/* 131 */         listeners.add(overrideListener);
/*     */       } else
/* 133 */         listeners.addAll(firstExchange.getResponseListeners());
/*     */     }
/* 135 */     this.listeners = listeners;
/*     */   }
/*     */   
/*     */   public boolean abort(Throwable cause)
/*     */   {
/* 140 */     HttpExchange exchange = (HttpExchange)this.exchanges.peekLast();
/* 141 */     return (exchange != null) && (exchange.abort(cause));
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 147 */     return String.format("%s[%x]", new Object[] { HttpConversation.class.getSimpleName(), Integer.valueOf(hashCode()) });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\HttpConversation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */