/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.client.http;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpChannel;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpConnection;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpDestination;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpExchange;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpRequest;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.SendFailure;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpVersion;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.ErrorCode;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.api.Session;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.ConcurrentHashSet;
/*     */ import java.nio.channels.AsynchronousCloseException;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
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
/*     */ public class HttpConnectionOverHTTP2
/*     */   extends HttpConnection
/*     */ {
/*  39 */   private final Set<HttpChannel> channels = new ConcurrentHashSet();
/*  40 */   private final AtomicBoolean closed = new AtomicBoolean();
/*     */   private final Session session;
/*     */   
/*     */   public HttpConnectionOverHTTP2(HttpDestination destination, Session session)
/*     */   {
/*  45 */     super(destination);
/*  46 */     this.session = session;
/*     */   }
/*     */   
/*     */   public Session getSession()
/*     */   {
/*  51 */     return this.session;
/*     */   }
/*     */   
/*     */ 
/*     */   protected SendFailure send(HttpExchange exchange)
/*     */   {
/*  57 */     exchange.getRequest().version(HttpVersion.HTTP_2);
/*  58 */     normalizeRequest(exchange.getRequest());
/*     */     
/*     */ 
/*  61 */     HttpChannel channel = newHttpChannel();
/*  62 */     this.channels.add(channel);
/*     */     
/*  64 */     return send(channel, exchange);
/*     */   }
/*     */   
/*     */   protected HttpChannelOverHTTP2 newHttpChannel()
/*     */   {
/*  69 */     return new HttpChannelOverHTTP2(getHttpDestination(), this, getSession());
/*     */   }
/*     */   
/*     */   protected void release(HttpChannel channel)
/*     */   {
/*  74 */     this.channels.remove(channel);
/*  75 */     getHttpDestination().release(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean onIdleTimeout(long idleTimeout)
/*     */   {
/*  81 */     boolean close = super.onIdleTimeout(idleTimeout);
/*  82 */     if (close)
/*  83 */       close(new TimeoutException("idle_timeout"));
/*  84 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */   {
/*  90 */     close(new AsynchronousCloseException());
/*     */   }
/*     */   
/*     */   protected void close(Throwable failure)
/*     */   {
/*  95 */     if (this.closed.compareAndSet(false, true))
/*     */     {
/*  97 */       getHttpDestination().close(this);
/*     */       
/*  99 */       abort(failure);
/*     */       
/* 101 */       this.session.close(ErrorCode.NO_ERROR.code, failure.getMessage(), Callback.NOOP);
/*     */     }
/*     */   }
/*     */   
/*     */   private void abort(Throwable failure)
/*     */   {
/* 107 */     for (HttpChannel channel : this.channels)
/*     */     {
/* 109 */       HttpExchange exchange = channel.getHttpExchange();
/* 110 */       if (exchange != null)
/* 111 */         exchange.getRequest().abort(failure);
/*     */     }
/* 113 */     this.channels.clear();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 119 */     return String.format("%s@%h[%s]", new Object[] {
/* 120 */       getClass().getSimpleName(), this, this.session });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\client\http\HttpConnectionOverHTTP2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */