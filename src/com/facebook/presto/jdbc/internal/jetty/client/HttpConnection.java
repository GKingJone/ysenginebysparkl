/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Authentication.Result;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.AuthenticationStore;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Connection;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.ContentProvider;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.ContentProvider.Typed;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.CompleteListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.ResponseListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpField;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpFields;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpHeader;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpHeaderValue;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpScheme;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpVersion;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.net.CookieStore;
/*     */ import java.net.HttpCookie;
/*     */ import java.net.URI;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
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
/*     */ public abstract class HttpConnection
/*     */   implements Connection
/*     */ {
/*  45 */   private static final Logger LOG = Log.getLogger(HttpConnection.class);
/*  46 */   private static final HttpField CHUNKED_FIELD = new HttpField(HttpHeader.TRANSFER_ENCODING, HttpHeaderValue.CHUNKED);
/*     */   
/*     */   private final HttpDestination destination;
/*     */   private int idleTimeoutGuard;
/*     */   private long idleTimeoutStamp;
/*     */   
/*     */   protected HttpConnection(HttpDestination destination)
/*     */   {
/*  54 */     this.destination = destination;
/*  55 */     this.idleTimeoutStamp = System.nanoTime();
/*     */   }
/*     */   
/*     */   public HttpClient getHttpClient()
/*     */   {
/*  60 */     return this.destination.getHttpClient();
/*     */   }
/*     */   
/*     */   public HttpDestination getHttpDestination()
/*     */   {
/*  65 */     return this.destination;
/*     */   }
/*     */   
/*     */ 
/*     */   public void send(Request request, Response.CompleteListener listener)
/*     */   {
/*  71 */     ArrayList<Response.ResponseListener> listeners = new ArrayList(2);
/*  72 */     if (request.getTimeout() > 0L)
/*     */     {
/*  74 */       TimeoutCompleteListener timeoutListener = new TimeoutCompleteListener(request);
/*  75 */       timeoutListener.schedule(getHttpClient().getScheduler());
/*  76 */       listeners.add(timeoutListener);
/*     */     }
/*  78 */     if (listener != null) {
/*  79 */       listeners.add(listener);
/*     */     }
/*  81 */     HttpExchange exchange = new HttpExchange(getHttpDestination(), (HttpRequest)request, listeners);
/*     */     
/*  83 */     SendFailure result = send(exchange);
/*  84 */     if (result != null) {
/*  85 */       request.abort(result.failure);
/*     */     }
/*     */   }
/*     */   
/*     */   protected abstract SendFailure send(HttpExchange paramHttpExchange);
/*     */   
/*     */   protected void normalizeRequest(Request request) {
/*  92 */     HttpVersion version = request.getVersion();
/*  93 */     HttpFields headers = request.getHeaders();
/*  94 */     ContentProvider content = request.getContent();
/*  95 */     ProxyConfiguration.Proxy proxy = this.destination.getProxy();
/*     */     
/*     */ 
/*  98 */     String path = request.getPath();
/*  99 */     if (path.trim().length() == 0)
/*     */     {
/* 101 */       path = "/";
/* 102 */       request.path(path);
/*     */     }
/*     */     
/* 105 */     URI uri = request.getURI();
/*     */     
/* 107 */     if (((proxy instanceof HttpProxy)) && (!HttpScheme.HTTPS.is(request.getScheme())) && (uri != null))
/*     */     {
/* 109 */       path = uri.toString();
/* 110 */       request.path(path);
/*     */     }
/*     */     
/*     */ 
/* 114 */     if (version.getVersion() == 11)
/*     */     {
/* 116 */       if (!headers.containsKey(HttpHeader.HOST.asString())) {
/* 117 */         headers.put(getHttpDestination().getHostField());
/*     */       }
/*     */     }
/*     */     
/* 121 */     if (content != null)
/*     */     {
/* 123 */       if ((content instanceof Typed))
/*     */       {
/* 125 */         if (!headers.containsKey(HttpHeader.CONTENT_TYPE.asString()))
/*     */         {
/* 127 */           String contentType = ((Typed)content).getContentType();
/* 128 */           if (contentType != null)
/* 129 */             headers.put(HttpHeader.CONTENT_TYPE, contentType);
/*     */         }
/*     */       }
/* 132 */       long contentLength = content.getLength();
/* 133 */       if (contentLength >= 0L)
/*     */       {
/* 135 */         if (!headers.containsKey(HttpHeader.CONTENT_LENGTH.asString())) {
/* 136 */           headers.put(HttpHeader.CONTENT_LENGTH, String.valueOf(contentLength));
/*     */         }
/*     */         
/*     */       }
/* 140 */       else if (!headers.containsKey(HttpHeader.TRANSFER_ENCODING.asString())) {
/* 141 */         headers.put(CHUNKED_FIELD);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 146 */     CookieStore cookieStore = getHttpClient().getCookieStore();
/* 147 */     if (cookieStore != null)
/*     */     {
/* 149 */       StringBuilder cookies = null;
/* 150 */       if (uri != null)
/* 151 */         cookies = convertCookies(cookieStore.get(uri), null);
/* 152 */       cookies = convertCookies(request.getCookies(), cookies);
/* 153 */       if (cookies != null) {
/* 154 */         request.header(HttpHeader.COOKIE.asString(), cookies.toString());
/*     */       }
/*     */     }
/*     */     
/* 158 */     applyAuthentication(request, proxy != null ? proxy.getURI() : null);
/* 159 */     applyAuthentication(request, uri);
/*     */   }
/*     */   
/*     */   private StringBuilder convertCookies(List<HttpCookie> cookies, StringBuilder builder)
/*     */   {
/* 164 */     for (int i = 0; i < cookies.size(); i++)
/*     */     {
/* 166 */       if (builder == null)
/* 167 */         builder = new StringBuilder();
/* 168 */       if (builder.length() > 0)
/* 169 */         builder.append("; ");
/* 170 */       HttpCookie cookie = (HttpCookie)cookies.get(i);
/* 171 */       builder.append(cookie.getName()).append("=").append(cookie.getValue());
/*     */     }
/* 173 */     return builder;
/*     */   }
/*     */   
/*     */   private void applyAuthentication(Request request, URI uri)
/*     */   {
/* 178 */     if (uri != null)
/*     */     {
/* 180 */       Authentication.Result result = getHttpClient().getAuthenticationStore().findAuthenticationResult(uri);
/* 181 */       if (result != null) {
/* 182 */         result.apply(request);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected SendFailure send(HttpChannel channel, HttpExchange exchange)
/*     */   {
/* 192 */     synchronized (this)
/*     */     {
/* 194 */       boolean send = this.idleTimeoutGuard >= 0;
/* 195 */       if (send)
/* 196 */         this.idleTimeoutGuard += 1;
/*     */     }
/*     */     boolean send;
/* 199 */     if (send)
/*     */     {
/* 201 */       HttpRequest request = exchange.getRequest();
/*     */       SendFailure result;
/* 203 */       SendFailure result; if (channel.associate(exchange))
/*     */       {
/* 205 */         channel.send();
/* 206 */         result = null;
/*     */       }
/*     */       else
/*     */       {
/* 210 */         channel.release();
/* 211 */         result = new SendFailure(new HttpRequestException("Could not associate request to connection", request), false);
/*     */       }
/*     */       
/* 214 */       synchronized (this)
/*     */       {
/* 216 */         this.idleTimeoutGuard -= 1;
/* 217 */         this.idleTimeoutStamp = System.nanoTime();
/*     */       }
/*     */       
/* 220 */       return result;
/*     */     }
/*     */     
/*     */ 
/* 224 */     return new SendFailure(new TimeoutException(), true);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean onIdleTimeout(long idleTimeout)
/*     */   {
/* 230 */     synchronized (this)
/*     */     {
/* 232 */       if (this.idleTimeoutGuard == 0)
/*     */       {
/* 234 */         long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - this.idleTimeoutStamp);
/* 235 */         boolean idle = elapsed > idleTimeout / 2L;
/* 236 */         if (idle)
/* 237 */           this.idleTimeoutGuard = -1;
/* 238 */         if (LOG.isDebugEnabled())
/* 239 */           LOG.debug("Idle timeout {}/{}ms - {}", new Object[] { Long.valueOf(elapsed), Long.valueOf(idleTimeout), this });
/* 240 */         return idle;
/*     */       }
/*     */       
/*     */ 
/* 244 */       if (LOG.isDebugEnabled())
/* 245 */         LOG.debug("Idle timeout skipped - {}", new Object[] { this });
/* 246 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 254 */     return String.format("%s@%h", new Object[] { getClass().getSimpleName(), this });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\HttpConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */