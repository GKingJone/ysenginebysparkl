/*     */ package com.facebook.presto.jdbc.internal.jetty.client.http;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpClient;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpConnection;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpDestination;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpExchange;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpRequest;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.SendFailure;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Connection;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.CompleteListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.AbstractConnection;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.EndPoint;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Promise;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Sweeper.Sweepable;
/*     */ import java.nio.channels.AsynchronousCloseException;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
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
/*     */ public class HttpConnectionOverHTTP
/*     */   extends AbstractConnection
/*     */   implements Connection, Sweeper.Sweepable
/*     */ {
/*  42 */   private static final Logger LOG = Log.getLogger(HttpConnectionOverHTTP.class);
/*     */   
/*  44 */   private final AtomicBoolean closed = new AtomicBoolean();
/*  45 */   private final AtomicInteger sweeps = new AtomicInteger();
/*     */   private final Promise<Connection> promise;
/*     */   private final Delegate delegate;
/*     */   private final HttpChannelOverHTTP channel;
/*     */   private long idleTimeout;
/*     */   
/*     */   public HttpConnectionOverHTTP(EndPoint endPoint, HttpDestination destination, Promise<Connection> promise)
/*     */   {
/*  53 */     super(endPoint, destination.getHttpClient().getExecutor());
/*  54 */     this.promise = promise;
/*  55 */     this.delegate = new Delegate(destination, null);
/*  56 */     this.channel = newHttpChannel();
/*     */   }
/*     */   
/*     */   protected HttpChannelOverHTTP newHttpChannel()
/*     */   {
/*  61 */     return new HttpChannelOverHTTP(this);
/*     */   }
/*     */   
/*     */   public HttpChannelOverHTTP getHttpChannel()
/*     */   {
/*  66 */     return this.channel;
/*     */   }
/*     */   
/*     */   public HttpDestinationOverHTTP getHttpDestination()
/*     */   {
/*  71 */     return (HttpDestinationOverHTTP)this.delegate.getHttpDestination();
/*     */   }
/*     */   
/*     */ 
/*     */   public void send(Request request, Response.CompleteListener listener)
/*     */   {
/*  77 */     this.delegate.send(request, listener);
/*     */   }
/*     */   
/*     */   protected SendFailure send(HttpExchange exchange)
/*     */   {
/*  82 */     return this.delegate.send(exchange);
/*     */   }
/*     */   
/*     */ 
/*     */   public void onOpen()
/*     */   {
/*  88 */     super.onOpen();
/*  89 */     fillInterested();
/*  90 */     this.promise.succeeded(this);
/*     */   }
/*     */   
/*     */   public boolean isClosed()
/*     */   {
/*  95 */     return this.closed.get();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean onIdleExpired()
/*     */   {
/* 101 */     long idleTimeout = getEndPoint().getIdleTimeout();
/* 102 */     boolean close = this.delegate.onIdleTimeout(idleTimeout);
/* 103 */     if (close)
/* 104 */       close(new TimeoutException("Idle timeout " + idleTimeout + " ms"));
/* 105 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public void onFillable()
/*     */   {
/* 111 */     HttpExchange exchange = this.channel.getHttpExchange();
/* 112 */     if (exchange != null)
/*     */     {
/* 114 */       this.channel.receive();
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/* 120 */       close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void release()
/*     */   {
/* 127 */     getEndPoint().setIdleTimeout(this.idleTimeout);
/* 128 */     getHttpDestination().release(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */   {
/* 134 */     close(new AsynchronousCloseException());
/*     */   }
/*     */   
/*     */   protected void close(Throwable failure)
/*     */   {
/* 139 */     if (this.closed.compareAndSet(false, true))
/*     */     {
/* 141 */       getHttpDestination().close(this);
/*     */       
/* 143 */       abort(failure);
/*     */       
/* 145 */       getEndPoint().shutdownOutput();
/* 146 */       if (LOG.isDebugEnabled())
/* 147 */         LOG.debug("Shutdown {}", new Object[] { this });
/* 148 */       getEndPoint().close();
/* 149 */       if (LOG.isDebugEnabled()) {
/* 150 */         LOG.debug("Closed {}", new Object[] { this });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected boolean abort(Throwable failure) {
/* 156 */     HttpExchange exchange = this.channel.getHttpExchange();
/* 157 */     return (exchange != null) && (exchange.getRequest().abort(failure));
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean sweep()
/*     */   {
/* 163 */     if (!this.closed.get())
/* 164 */       return false;
/* 165 */     if (this.sweeps.incrementAndGet() < 4)
/* 166 */       return false;
/* 167 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 173 */     return String.format("%s@%h(l:%s <-> r:%s,closed=%b)[%s]", new Object[] {
/* 174 */       getClass().getSimpleName(), this, 
/*     */       
/* 176 */       getEndPoint().getLocalAddress(), 
/* 177 */       getEndPoint().getRemoteAddress(), 
/* 178 */       Boolean.valueOf(this.closed.get()), this.channel });
/*     */   }
/*     */   
/*     */   private class Delegate
/*     */     extends HttpConnection
/*     */   {
/*     */     private Delegate(HttpDestination destination)
/*     */     {
/* 186 */       super();
/*     */     }
/*     */     
/*     */ 
/*     */     protected SendFailure send(HttpExchange exchange)
/*     */     {
/* 192 */       Request request = exchange.getRequest();
/* 193 */       normalizeRequest(request);
/*     */       
/*     */ 
/* 196 */       EndPoint endPoint = HttpConnectionOverHTTP.this.getEndPoint();
/* 197 */       HttpConnectionOverHTTP.this.idleTimeout = endPoint.getIdleTimeout();
/* 198 */       endPoint.setIdleTimeout(request.getIdleTimeout());
/*     */       
/*     */ 
/* 201 */       return send(HttpConnectionOverHTTP.this.channel, exchange);
/*     */     }
/*     */     
/*     */ 
/*     */     public void close()
/*     */     {
/* 207 */       HttpConnectionOverHTTP.this.close();
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 213 */       return HttpConnectionOverHTTP.this.toString();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\http\HttpConnectionOverHTTP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */