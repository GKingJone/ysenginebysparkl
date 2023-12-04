/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Connection;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Destination;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.ResponseListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpField;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpHeader;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpScheme;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.ClientConnectionFactory;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.ssl.SslClientConnectionFactory;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BlockingArrayQueue;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Promise;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedAttribute;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedObject;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.ContainerLifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.Dumpable;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.AsynchronousCloseException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.RejectedExecutionException;
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
/*     */ @ManagedObject
/*     */ public abstract class HttpDestination
/*     */   extends ContainerLifeCycle
/*     */   implements Destination, Closeable, Dumpable
/*     */ {
/*  49 */   protected static final Logger LOG = Log.getLogger(HttpDestination.class);
/*     */   
/*     */   private final HttpClient client;
/*     */   private final Origin origin;
/*     */   private final Queue<HttpExchange> exchanges;
/*     */   private final RequestNotifier requestNotifier;
/*     */   private final ResponseNotifier responseNotifier;
/*     */   private final ProxyConfiguration.Proxy proxy;
/*     */   private final ClientConnectionFactory connectionFactory;
/*     */   private final HttpField hostField;
/*     */   
/*     */   public HttpDestination(HttpClient client, Origin origin)
/*     */   {
/*  62 */     this.client = client;
/*  63 */     this.origin = origin;
/*     */     
/*  65 */     this.exchanges = newExchangeQueue(client);
/*     */     
/*  67 */     this.requestNotifier = new RequestNotifier(client);
/*  68 */     this.responseNotifier = new ResponseNotifier();
/*     */     
/*  70 */     ProxyConfiguration proxyConfig = client.getProxyConfiguration();
/*  71 */     this.proxy = proxyConfig.match(origin);
/*  72 */     ClientConnectionFactory connectionFactory = client.getTransport();
/*  73 */     if (this.proxy != null)
/*     */     {
/*  75 */       connectionFactory = this.proxy.newClientConnectionFactory(connectionFactory);
/*  76 */       if (this.proxy.isSecure()) {
/*  77 */         connectionFactory = newSslClientConnectionFactory(connectionFactory);
/*     */       }
/*     */       
/*     */     }
/*  81 */     else if (HttpScheme.HTTPS.is(getScheme())) {
/*  82 */       connectionFactory = newSslClientConnectionFactory(connectionFactory);
/*     */     }
/*  84 */     this.connectionFactory = connectionFactory;
/*     */     
/*  86 */     String host = getHost();
/*  87 */     if (!client.isDefaultPort(getScheme(), getPort()))
/*  88 */       host = host + ":" + getPort();
/*  89 */     this.hostField = new HttpField(HttpHeader.HOST, host);
/*     */   }
/*     */   
/*     */   protected Queue<HttpExchange> newExchangeQueue(HttpClient client)
/*     */   {
/*  94 */     return new BlockingArrayQueue(client.getMaxRequestsQueuedPerDestination());
/*     */   }
/*     */   
/*     */   protected ClientConnectionFactory newSslClientConnectionFactory(ClientConnectionFactory connectionFactory)
/*     */   {
/*  99 */     return new SslClientConnectionFactory(this.client.getSslContextFactory(), this.client.getByteBufferPool(), this.client.getExecutor(), connectionFactory);
/*     */   }
/*     */   
/*     */   public HttpClient getHttpClient()
/*     */   {
/* 104 */     return this.client;
/*     */   }
/*     */   
/*     */   public Origin getOrigin()
/*     */   {
/* 109 */     return this.origin;
/*     */   }
/*     */   
/*     */   public Queue<HttpExchange> getHttpExchanges()
/*     */   {
/* 114 */     return this.exchanges;
/*     */   }
/*     */   
/*     */   public RequestNotifier getRequestNotifier()
/*     */   {
/* 119 */     return this.requestNotifier;
/*     */   }
/*     */   
/*     */   public ResponseNotifier getResponseNotifier()
/*     */   {
/* 124 */     return this.responseNotifier;
/*     */   }
/*     */   
/*     */   public ProxyConfiguration.Proxy getProxy()
/*     */   {
/* 129 */     return this.proxy;
/*     */   }
/*     */   
/*     */   public ClientConnectionFactory getClientConnectionFactory()
/*     */   {
/* 134 */     return this.connectionFactory;
/*     */   }
/*     */   
/*     */ 
/*     */   @ManagedAttribute(value="The destination scheme", readonly=true)
/*     */   public String getScheme()
/*     */   {
/* 141 */     return this.origin.getScheme();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @ManagedAttribute(value="The destination host", readonly=true)
/*     */   public String getHost()
/*     */   {
/* 150 */     return this.origin.getAddress().getHost();
/*     */   }
/*     */   
/*     */ 
/*     */   @ManagedAttribute(value="The destination port", readonly=true)
/*     */   public int getPort()
/*     */   {
/* 157 */     return this.origin.getAddress().getPort();
/*     */   }
/*     */   
/*     */   @ManagedAttribute(value="The number of queued requests", readonly=true)
/*     */   public int getQueuedRequestCount()
/*     */   {
/* 163 */     return this.exchanges.size();
/*     */   }
/*     */   
/*     */   public Origin.Address getConnectAddress()
/*     */   {
/* 168 */     return this.proxy == null ? this.origin.getAddress() : this.proxy.getAddress();
/*     */   }
/*     */   
/*     */   public HttpField getHostField()
/*     */   {
/* 173 */     return this.hostField;
/*     */   }
/*     */   
/*     */   protected void send(HttpRequest request, List<Response.ResponseListener> listeners)
/*     */   {
/* 178 */     if (!getScheme().equalsIgnoreCase(request.getScheme()))
/* 179 */       throw new IllegalArgumentException("Invalid request scheme " + request.getScheme() + " for destination " + this);
/* 180 */     if (!getHost().equalsIgnoreCase(request.getHost()))
/* 181 */       throw new IllegalArgumentException("Invalid request host " + request.getHost() + " for destination " + this);
/* 182 */     int port = request.getPort();
/* 183 */     if ((port >= 0) && (getPort() != port)) {
/* 184 */       throw new IllegalArgumentException("Invalid request port " + port + " for destination " + this);
/*     */     }
/* 186 */     HttpExchange exchange = new HttpExchange(this, request, listeners);
/*     */     
/* 188 */     if (this.client.isRunning())
/*     */     {
/* 190 */       if (enqueue(this.exchanges, exchange))
/*     */       {
/* 192 */         if ((!this.client.isRunning()) && (this.exchanges.remove(exchange)))
/*     */         {
/* 194 */           request.abort(new RejectedExecutionException(this.client + " is stopping"));
/*     */         }
/*     */         else
/*     */         {
/* 198 */           if (LOG.isDebugEnabled())
/* 199 */             LOG.debug("Queued {} for {}", new Object[] { request, this });
/* 200 */           this.requestNotifier.notifyQueued(request);
/* 201 */           send();
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 206 */         if (LOG.isDebugEnabled())
/* 207 */           LOG.debug("Max queue size {} exceeded by {} for {}", new Object[] { Integer.valueOf(this.client.getMaxRequestsQueuedPerDestination()), request, this });
/* 208 */         request.abort(new RejectedExecutionException("Max requests per destination " + this.client.getMaxRequestsQueuedPerDestination() + " exceeded for " + this));
/*     */       }
/*     */       
/*     */     }
/*     */     else {
/* 213 */       request.abort(new RejectedExecutionException(this.client + " is stopped"));
/*     */     }
/*     */   }
/*     */   
/*     */   protected boolean enqueue(Queue<HttpExchange> queue, HttpExchange exchange)
/*     */   {
/* 219 */     return queue.offer(exchange);
/*     */   }
/*     */   
/*     */   public abstract void send();
/*     */   
/*     */   public void newConnection(Promise<Connection> promise)
/*     */   {
/* 226 */     createConnection(promise);
/*     */   }
/*     */   
/*     */   protected void createConnection(Promise<Connection> promise)
/*     */   {
/* 231 */     this.client.newConnection(this, promise);
/*     */   }
/*     */   
/*     */   public boolean remove(HttpExchange exchange)
/*     */   {
/* 236 */     return this.exchanges.remove(exchange);
/*     */   }
/*     */   
/*     */   public void close()
/*     */   {
/* 241 */     abort(new AsynchronousCloseException());
/* 242 */     if (LOG.isDebugEnabled()) {
/* 243 */       LOG.debug("Closed {}", new Object[] { this });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void release(Connection connection) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void close(Connection connection) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void abort(Throwable cause)
/*     */   {
/* 265 */     for (HttpExchange exchange : new ArrayList(this.exchanges)) {
/* 266 */       exchange.getRequest().abort(cause);
/*     */     }
/*     */   }
/*     */   
/*     */   public String dump()
/*     */   {
/* 272 */     return ContainerLifeCycle.dump(this);
/*     */   }
/*     */   
/*     */   public void dump(Appendable out, String indent)
/*     */     throws IOException
/*     */   {
/* 278 */     ContainerLifeCycle.dumpObject(out, toString());
/*     */   }
/*     */   
/*     */   public String asString()
/*     */   {
/* 283 */     return this.origin.asString();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 289 */     return String.format("%s[%s]@%x%s,queue=%d", new Object[] {HttpDestination.class
/* 290 */       .getSimpleName(), 
/* 291 */       asString(), 
/* 292 */       Integer.valueOf(hashCode()), "(via " + this.proxy + ")", 
/*     */       
/* 294 */       Integer.valueOf(this.exchanges.size()) });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\HttpDestination.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */