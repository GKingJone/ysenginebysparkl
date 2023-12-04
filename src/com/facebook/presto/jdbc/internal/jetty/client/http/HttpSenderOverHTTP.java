/*     */ package com.facebook.presto.jdbc.internal.jetty.client.http;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpClient;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpContent;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpDestination;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpExchange;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpRequestException;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpSender;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.ContentProvider;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpGenerator.Result;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpURI;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.MetaData.Request;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.ByteBufferPool;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.EndPoint;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback.Nested;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.IteratingCallback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.IteratingCallback.Action;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
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
/*     */ public class HttpSenderOverHTTP
/*     */   extends HttpSender
/*     */ {
/*  40 */   private final HttpGenerator generator = new HttpGenerator();
/*     */   
/*     */   public HttpSenderOverHTTP(HttpChannelOverHTTP channel)
/*     */   {
/*  44 */     super(channel);
/*     */   }
/*     */   
/*     */ 
/*     */   public HttpChannelOverHTTP getHttpChannel()
/*     */   {
/*  50 */     return (HttpChannelOverHTTP)super.getHttpChannel();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void sendHeaders(HttpExchange exchange, HttpContent content, Callback callback)
/*     */   {
/*     */     try
/*     */     {
/*  58 */       new HeadersCallback(exchange, content, callback).iterate();
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/*  62 */       if (LOG.isDebugEnabled())
/*  63 */         LOG.debug(x);
/*  64 */       callback.failed(x);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void sendContent(HttpExchange exchange, HttpContent content, Callback callback)
/*     */   {
/*     */     try
/*     */     {
/*  73 */       HttpClient client = getHttpChannel().getHttpDestination().getHttpClient();
/*  74 */       ByteBufferPool bufferPool = client.getByteBufferPool();
/*  75 */       ByteBuffer chunk = null;
/*     */       for (;;)
/*     */       {
/*  78 */         ByteBuffer contentBuffer = content.getByteBuffer();
/*  79 */         boolean lastContent = content.isLast();
/*  80 */         Result result = this.generator.generateRequest(null, null, chunk, contentBuffer, lastContent);
/*  81 */         if (LOG.isDebugEnabled()) {
/*  82 */           LOG.debug("Generated content ({} bytes) - {}/{}", new Object[] {
/*  83 */             Integer.valueOf(contentBuffer == null ? -1 : contentBuffer.remaining()), result, this.generator });
/*     */         }
/*  85 */         switch (result)
/*     */         {
/*     */ 
/*     */         case NEED_CHUNK: 
/*  89 */           chunk = bufferPool.acquire(12, false);
/*  90 */           break;
/*     */         
/*     */ 
/*     */         case FLUSH: 
/*  94 */           EndPoint endPoint = getHttpChannel().getHttpConnection().getEndPoint();
/*  95 */           if (chunk != null) {
/*  96 */             endPoint.write(new ByteBufferRecyclerCallback(callback, bufferPool, new ByteBuffer[] { chunk }, null), new ByteBuffer[] { chunk, contentBuffer });
/*     */           } else
/*  98 */             endPoint.write(callback, new ByteBuffer[] { contentBuffer });
/*  99 */           return;
/*     */         
/*     */ 
/*     */         case SHUTDOWN_OUT: 
/* 103 */           shutdownOutput();
/* 104 */           break;
/*     */         
/*     */ 
/*     */         case CONTINUE: 
/* 108 */           if (!lastContent)
/*     */           {
/* 110 */             callback.succeeded();
/* 111 */             return;
/*     */           }
/*     */           break;
/*     */         case DONE: 
/* 115 */           callback.succeeded();
/* 116 */           return;
/*     */         
/*     */ 
/*     */         default: 
/* 120 */           throw new IllegalStateException(result.toString());
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 131 */       return;
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 127 */       if (LOG.isDebugEnabled())
/* 128 */         LOG.debug(x);
/* 129 */       callback.failed(x);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void reset()
/*     */   {
/* 136 */     this.generator.reset();
/* 137 */     super.reset();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void dispose()
/*     */   {
/* 143 */     this.generator.abort();
/* 144 */     super.dispose();
/* 145 */     shutdownOutput();
/*     */   }
/*     */   
/*     */   private void shutdownOutput()
/*     */   {
/* 150 */     if (LOG.isDebugEnabled())
/* 151 */       LOG.debug("Request shutdown output {}", new Object[] { getHttpExchange().getRequest() });
/* 152 */     getHttpChannel().getHttpConnection().getEndPoint().shutdownOutput();
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 158 */     return String.format("%s[%s]", new Object[] { super.toString(), this.generator });
/*     */   }
/*     */   
/*     */   private class HeadersCallback extends IteratingCallback
/*     */   {
/*     */     private final HttpExchange exchange;
/*     */     private final Callback callback;
/*     */     private final MetaData.Request metaData;
/*     */     private ByteBuffer headerBuffer;
/*     */     private ByteBuffer chunkBuffer;
/*     */     private ByteBuffer contentBuffer;
/*     */     private boolean lastContent;
/*     */     private boolean generated;
/*     */     
/*     */     public HeadersCallback(HttpExchange exchange, HttpContent content, Callback callback)
/*     */     {
/* 174 */       super();
/* 175 */       this.exchange = exchange;
/* 176 */       this.callback = callback;
/*     */       
/* 178 */       Request request = exchange.getRequest();
/* 179 */       ContentProvider requestContent = request.getContent();
/* 180 */       long contentLength = requestContent == null ? -1L : requestContent.getLength();
/* 181 */       String path = request.getPath();
/* 182 */       String query = request.getQuery();
/* 183 */       if (query != null)
/* 184 */         path = path + "?" + query;
/* 185 */       this.metaData = new MetaData.Request(request.getMethod(), new HttpURI(path), request.getVersion(), request.getHeaders(), contentLength);
/*     */       
/* 187 */       if (!HttpSenderOverHTTP.this.expects100Continue(request))
/*     */       {
/* 189 */         content.advance();
/* 190 */         this.contentBuffer = content.getByteBuffer();
/* 191 */         this.lastContent = content.isLast();
/*     */       }
/*     */     }
/*     */     
/*     */     protected Action process()
/*     */       throws Exception
/*     */     {
/* 198 */       HttpClient client = HttpSenderOverHTTP.this.getHttpChannel().getHttpDestination().getHttpClient();
/* 199 */       ByteBufferPool bufferPool = client.getByteBufferPool();
/*     */       
/*     */       for (;;)
/*     */       {
/* 203 */         Result result = HttpSenderOverHTTP.this.generator.generateRequest(this.metaData, this.headerBuffer, this.chunkBuffer, this.contentBuffer, this.lastContent);
/* 204 */         if (HttpSenderOverHTTP.LOG.isDebugEnabled())
/* 205 */           HttpSenderOverHTTP.LOG.debug("Generated headers ({} bytes), chunk ({} bytes), content ({} bytes) - {}/{}", new Object[] {
/* 206 */             Integer.valueOf(this.headerBuffer == null ? -1 : this.headerBuffer.remaining()), 
/* 207 */             Integer.valueOf(this.chunkBuffer == null ? -1 : this.chunkBuffer.remaining()), 
/* 208 */             Integer.valueOf(this.contentBuffer == null ? -1 : this.contentBuffer.remaining()), result, 
/* 209 */             HttpSenderOverHTTP.this.generator });
/* 210 */         switch (HttpSenderOverHTTP.1.$SwitchMap$org$eclipse$jetty$http$HttpGenerator$Result[result.ordinal()])
/*     */         {
/*     */ 
/*     */         case 6: 
/* 214 */           this.headerBuffer = bufferPool.acquire(client.getRequestBufferSize(), false);
/* 215 */           break;
/*     */         
/*     */ 
/*     */         case 1: 
/* 219 */           this.chunkBuffer = bufferPool.acquire(12, false);
/* 220 */           break;
/*     */         
/*     */ 
/*     */         case 2: 
/* 224 */           EndPoint endPoint = HttpSenderOverHTTP.this.getHttpChannel().getHttpConnection().getEndPoint();
/* 225 */           if (this.chunkBuffer == null)
/*     */           {
/* 227 */             if (this.contentBuffer == null) {
/* 228 */               endPoint.write(this, new ByteBuffer[] { this.headerBuffer });
/*     */             } else {
/* 230 */               endPoint.write(this, new ByteBuffer[] { this.headerBuffer, this.contentBuffer });
/*     */             }
/*     */             
/*     */           }
/* 234 */           else if (this.contentBuffer == null) {
/* 235 */             endPoint.write(this, new ByteBuffer[] { this.headerBuffer, this.chunkBuffer });
/*     */           } else {
/* 237 */             endPoint.write(this, new ByteBuffer[] { this.headerBuffer, this.chunkBuffer, this.contentBuffer });
/*     */           }
/* 239 */           this.generated = true;
/* 240 */           return Action.SCHEDULED;
/*     */         
/*     */ 
/*     */         case 3: 
/* 244 */           HttpSenderOverHTTP.this.shutdownOutput();
/* 245 */           return Action.SUCCEEDED;
/*     */         
/*     */ 
/*     */         case 4: 
/* 249 */           if (this.generated) {
/* 250 */             return Action.SUCCEEDED;
/*     */           }
/*     */           
/*     */           break;
/*     */         case 5: 
/* 255 */           if (this.generated) {
/* 256 */             return Action.SUCCEEDED;
/*     */           }
/*     */           
/* 259 */           throw new HttpRequestException("Could not generate headers", this.exchange.getRequest());
/*     */         
/*     */ 
/*     */         default: 
/* 263 */           throw new IllegalStateException(result.toString());
/*     */         }
/*     */         
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void succeeded()
/*     */     {
/* 272 */       release();
/* 273 */       super.succeeded();
/*     */     }
/*     */     
/*     */ 
/*     */     public void failed(Throwable x)
/*     */     {
/* 279 */       release();
/* 280 */       this.callback.failed(x);
/* 281 */       super.failed(x);
/*     */     }
/*     */     
/*     */ 
/*     */     protected void onCompleteSuccess()
/*     */     {
/* 287 */       super.onCompleteSuccess();
/* 288 */       this.callback.succeeded();
/*     */     }
/*     */     
/*     */     private void release()
/*     */     {
/* 293 */       HttpClient client = HttpSenderOverHTTP.this.getHttpChannel().getHttpDestination().getHttpClient();
/* 294 */       ByteBufferPool bufferPool = client.getByteBufferPool();
/* 295 */       bufferPool.release(this.headerBuffer);
/* 296 */       this.headerBuffer = null;
/* 297 */       if (this.chunkBuffer != null)
/* 298 */         bufferPool.release(this.chunkBuffer);
/* 299 */       this.chunkBuffer = null;
/*     */     }
/*     */   }
/*     */   
/*     */   private class ByteBufferRecyclerCallback extends Nested
/*     */   {
/*     */     private final ByteBufferPool pool;
/*     */     private final ByteBuffer[] buffers;
/*     */     
/*     */     private ByteBufferRecyclerCallback(Callback callback, ByteBufferPool pool, ByteBuffer... buffers)
/*     */     {
/* 310 */       super();
/* 311 */       this.pool = pool;
/* 312 */       this.buffers = buffers;
/*     */     }
/*     */     
/*     */ 
/*     */     public void succeeded()
/*     */     {
/* 318 */       for (ByteBuffer buffer : this.buffers)
/*     */       {
/* 320 */         assert (!buffer.hasRemaining());
/* 321 */         this.pool.release(buffer);
/*     */       }
/* 323 */       super.succeeded();
/*     */     }
/*     */     
/*     */ 
/*     */     public void failed(Throwable x)
/*     */     {
/* 329 */       for (ByteBuffer buffer : this.buffers)
/* 330 */         this.pool.release(buffer);
/* 331 */       super.failed(x);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\http\HttpSenderOverHTTP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */