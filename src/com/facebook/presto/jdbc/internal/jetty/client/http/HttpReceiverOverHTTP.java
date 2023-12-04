/*     */ package com.facebook.presto.jdbc.internal.jetty.client.http;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpClient;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpDestination;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpExchange;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpReceiver;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpRequest;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpResponse;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpResponseException;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpField;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpMethod;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpParser;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpParser.ResponseHandler;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpVersion;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.ByteBufferPool;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.EndPoint;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.CompletableCallback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.EOFException;
/*     */ import java.nio.ByteBuffer;
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
/*     */ public class HttpReceiverOverHTTP
/*     */   extends HttpReceiver
/*     */   implements ResponseHandler
/*     */ {
/*  40 */   private final HttpParser parser = new HttpParser(this);
/*     */   private ByteBuffer buffer;
/*     */   private boolean shutdown;
/*     */   
/*     */   public HttpReceiverOverHTTP(HttpChannelOverHTTP channel)
/*     */   {
/*  46 */     super(channel);
/*     */   }
/*     */   
/*     */ 
/*     */   public HttpChannelOverHTTP getHttpChannel()
/*     */   {
/*  52 */     return (HttpChannelOverHTTP)super.getHttpChannel();
/*     */   }
/*     */   
/*     */   private HttpConnectionOverHTTP getHttpConnection()
/*     */   {
/*  57 */     return getHttpChannel().getHttpConnection();
/*     */   }
/*     */   
/*     */   protected ByteBuffer getResponseBuffer()
/*     */   {
/*  62 */     return this.buffer;
/*     */   }
/*     */   
/*     */   public void receive()
/*     */   {
/*  67 */     if (this.buffer == null)
/*  68 */       acquireBuffer();
/*  69 */     process();
/*     */   }
/*     */   
/*     */   private void acquireBuffer()
/*     */   {
/*  74 */     HttpClient client = getHttpDestination().getHttpClient();
/*  75 */     ByteBufferPool bufferPool = client.getByteBufferPool();
/*  76 */     this.buffer = bufferPool.acquire(client.getResponseBufferSize(), true);
/*     */   }
/*     */   
/*     */   private void releaseBuffer()
/*     */   {
/*  81 */     if (this.buffer == null)
/*  82 */       throw new IllegalStateException();
/*  83 */     if (BufferUtil.hasContent(this.buffer))
/*  84 */       throw new IllegalStateException();
/*  85 */     HttpClient client = getHttpDestination().getHttpClient();
/*  86 */     ByteBufferPool bufferPool = client.getByteBufferPool();
/*  87 */     bufferPool.release(this.buffer);
/*  88 */     this.buffer = null;
/*     */   }
/*     */   
/*     */   private void process()
/*     */   {
/*     */     try
/*     */     {
/*  95 */       HttpConnectionOverHTTP connection = getHttpConnection();
/*  96 */       EndPoint endPoint = connection.getEndPoint();
/*     */       for (;;)
/*     */       {
/*  99 */         boolean upgraded = connection != endPoint.getConnection();
/*     */         
/*     */ 
/* 102 */         if ((connection.isClosed()) || (upgraded))
/*     */         {
/* 104 */           if (LOG.isDebugEnabled())
/* 105 */             LOG.debug("{} {}", new Object[] { connection, upgraded ? "upgraded" : "closed" });
/* 106 */           releaseBuffer();
/* 107 */           return;
/*     */         }
/*     */         
/* 110 */         if (parse()) {
/* 111 */           return;
/*     */         }
/* 113 */         int read = endPoint.fill(this.buffer);
/* 114 */         if (LOG.isDebugEnabled()) {
/* 115 */           LOG.debug("Read {} bytes {} from {}", new Object[] { Integer.valueOf(read), BufferUtil.toDetailString(this.buffer), endPoint });
/*     */         }
/* 117 */         if (read > 0)
/*     */         {
/* 119 */           if (!parse()) {}
/*     */         }
/*     */         else {
/* 122 */           if (read == 0)
/*     */           {
/* 124 */             releaseBuffer();
/* 125 */             fillInterested();
/* 126 */             return;
/*     */           }
/*     */           
/*     */ 
/* 130 */           releaseBuffer();
/* 131 */           shutdown();
/* 132 */           return;
/*     */         }
/*     */       }
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
/* 145 */       return;
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 138 */       if (LOG.isDebugEnabled())
/* 139 */         LOG.debug(x);
/* 140 */       BufferUtil.clear(this.buffer);
/* 141 */       if (this.buffer != null)
/* 142 */         releaseBuffer();
/* 143 */       failAndClose(x);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private boolean parse()
/*     */   {
/*     */     for (;;)
/*     */     {
/* 158 */       boolean handle = this.parser.parseNext(this.buffer);
/* 159 */       if (LOG.isDebugEnabled())
/* 160 */         LOG.debug("Parsed {}, remaining {} {}", new Object[] { Boolean.valueOf(handle), Integer.valueOf(this.buffer.remaining()), this.parser });
/* 161 */       if ((handle) || (!this.buffer.hasRemaining())) {
/* 162 */         return handle;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected void fillInterested() {
/* 168 */     getHttpChannel().getHttpConnection().fillInterested();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void shutdown()
/*     */   {
/* 177 */     this.shutdown = true;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 183 */     this.parser.atEOF();
/* 184 */     this.parser.parseNext(BufferUtil.EMPTY_BUFFER);
/*     */   }
/*     */   
/*     */   protected boolean isShutdown()
/*     */   {
/* 189 */     return this.shutdown;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getHeaderCacheSize()
/*     */   {
/* 196 */     return 256;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean startResponse(HttpVersion version, int status, String reason)
/*     */   {
/* 202 */     HttpExchange exchange = getHttpExchange();
/* 203 */     if (exchange == null) {
/* 204 */       return false;
/*     */     }
/* 206 */     String method = exchange.getRequest().getMethod();
/* 207 */     this.parser.setHeadResponse((HttpMethod.HEAD.is(method)) || (HttpMethod.CONNECT.is(method)));
/* 208 */     exchange.getResponse().version(version).status(status).reason(reason);
/*     */     
/* 210 */     return !responseBegin(exchange);
/*     */   }
/*     */   
/*     */ 
/*     */   public void parsedHeader(HttpField field)
/*     */   {
/* 216 */     HttpExchange exchange = getHttpExchange();
/* 217 */     if (exchange == null) {
/* 218 */       return;
/*     */     }
/* 220 */     responseHeader(exchange, field);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean headerComplete()
/*     */   {
/* 226 */     HttpExchange exchange = getHttpExchange();
/* 227 */     if (exchange == null) {
/* 228 */       return false;
/*     */     }
/* 230 */     return !responseHeaders(exchange);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean content(ByteBuffer buffer)
/*     */   {
/* 236 */     HttpExchange exchange = getHttpExchange();
/* 237 */     if (exchange == null) {
/* 238 */       return false;
/*     */     }
/* 240 */     CompletableCallback callback = new CompletableCallback()
/*     */     {
/*     */ 
/*     */       public void resume()
/*     */       {
/* 245 */         if (HttpReceiverOverHTTP.LOG.isDebugEnabled())
/* 246 */           HttpReceiverOverHTTP.LOG.debug("Content consumed asynchronously, resuming processing", new Object[0]);
/* 247 */         HttpReceiverOverHTTP.this.process();
/*     */       }
/*     */       
/*     */       public void abort(Throwable x)
/*     */       {
/* 252 */         HttpReceiverOverHTTP.this.failAndClose(x);
/*     */       }
/*     */       
/* 255 */     };
/* 256 */     boolean proceed = responseContent(exchange, buffer, callback);
/* 257 */     boolean async = callback.tryComplete();
/* 258 */     return (!proceed) || (async);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean messageComplete()
/*     */   {
/* 264 */     HttpExchange exchange = getHttpExchange();
/* 265 */     if (exchange == null) {
/* 266 */       return false;
/*     */     }
/* 268 */     return !responseSuccess(exchange);
/*     */   }
/*     */   
/*     */ 
/*     */   public void earlyEOF()
/*     */   {
/* 274 */     HttpExchange exchange = getHttpExchange();
/* 275 */     HttpConnectionOverHTTP connection = getHttpConnection();
/* 276 */     if (exchange == null) {
/* 277 */       connection.close();
/*     */     } else {
/* 279 */       failAndClose(new EOFException(String.valueOf(connection)));
/*     */     }
/*     */   }
/*     */   
/*     */   public void badMessage(int status, String reason)
/*     */   {
/* 285 */     HttpExchange exchange = getHttpExchange();
/* 286 */     if (exchange != null)
/*     */     {
/* 288 */       HttpResponse response = exchange.getResponse();
/* 289 */       response.status(status).reason(reason);
/* 290 */       failAndClose(new HttpResponseException("HTTP protocol violation: bad response on " + getHttpConnection(), response));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void reset()
/*     */   {
/* 297 */     super.reset();
/* 298 */     this.parser.reset();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void dispose()
/*     */   {
/* 304 */     super.dispose();
/* 305 */     this.parser.close();
/*     */   }
/*     */   
/*     */   private void failAndClose(Throwable failure)
/*     */   {
/* 310 */     if (responseFailure(failure)) {
/* 311 */       getHttpConnection().close(failure);
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 317 */     return String.format("%s[%s]", new Object[] { super.toString(), this.parser });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\http\HttpReceiverOverHTTP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */