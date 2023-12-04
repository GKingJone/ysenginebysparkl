/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.client.http;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpChannel;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpClient;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpDestination;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpExchange;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpReceiver;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpRequest;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpResponse;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpField;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpFields;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.MetaData.Response;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.ErrorCode;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.api.Stream;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.api.Stream.Listener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.DataFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.HeadersFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.PushPromiseFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.ResetFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.io.ByteBufferPool;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.CompletableCallback;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Locale;
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
/*     */ public class HttpReceiverOverHTTP2
/*     */   extends HttpReceiver
/*     */   implements Listener
/*     */ {
/*     */   public HttpReceiverOverHTTP2(HttpChannel channel)
/*     */   {
/*  47 */     super(channel);
/*     */   }
/*     */   
/*     */ 
/*     */   protected HttpChannelOverHTTP2 getHttpChannel()
/*     */   {
/*  53 */     return (HttpChannelOverHTTP2)super.getHttpChannel();
/*     */   }
/*     */   
/*     */ 
/*     */   public void onHeaders(Stream stream, HeadersFrame frame)
/*     */   {
/*  59 */     HttpExchange exchange = getHttpExchange();
/*  60 */     if (exchange == null) {
/*  61 */       return;
/*     */     }
/*  63 */     HttpResponse response = exchange.getResponse();
/*  64 */     MetaData.Response metaData = (MetaData.Response)frame.getMetaData();
/*  65 */     response.version(metaData.getVersion()).status(metaData.getStatus()).reason(metaData.getReason());
/*     */     
/*  67 */     if (responseBegin(exchange))
/*     */     {
/*  69 */       HttpFields headers = metaData.getFields();
/*  70 */       for (HttpField header : headers)
/*     */       {
/*  72 */         if (!responseHeader(exchange, header)) {
/*  73 */           return;
/*     */         }
/*     */       }
/*  76 */       if (responseHeaders(exchange))
/*     */       {
/*  78 */         if (frame.isEndStream()) {
/*  79 */           responseSuccess(exchange);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Listener onPush(Stream stream, PushPromiseFrame frame)
/*     */   {
/*  88 */     stream.reset(new ResetFrame(stream.getId(), ErrorCode.REFUSED_STREAM_ERROR.code), Callback.NOOP);
/*  89 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void onData(Stream stream, final DataFrame frame, final Callback callback)
/*     */   {
/*  95 */     final HttpExchange exchange = getHttpExchange();
/*  96 */     if (exchange == null)
/*     */     {
/*  98 */       callback.failed(new IOException("terminated"));
/*  99 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 106 */     final ByteBufferPool byteBufferPool = getHttpDestination().getHttpClient().getByteBufferPool();
/* 107 */     ByteBuffer original = frame.getData();
/* 108 */     int length = original.remaining();
/* 109 */     final ByteBuffer copy = byteBufferPool.acquire(length, original.isDirect());
/* 110 */     BufferUtil.clearToFill(copy);
/* 111 */     copy.put(original);
/* 112 */     BufferUtil.flipToFlush(copy, 0);
/*     */     
/* 114 */     CompletableCallback delegate = new CompletableCallback()
/*     */     {
/*     */ 
/*     */       public void succeeded()
/*     */       {
/* 119 */         byteBufferPool.release(copy);
/* 120 */         callback.succeeded();
/* 121 */         super.succeeded();
/*     */       }
/*     */       
/*     */ 
/*     */       public void failed(Throwable x)
/*     */       {
/* 127 */         byteBufferPool.release(copy);
/* 128 */         callback.failed(x);
/* 129 */         super.failed(x);
/*     */       }
/*     */       
/*     */ 
/*     */       public void resume()
/*     */       {
/* 135 */         if (frame.isEndStream()) {
/* 136 */           HttpReceiverOverHTTP2.this.responseSuccess(exchange);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void abort(Throwable failure) {}
/* 144 */     };
/* 145 */     responseContent(exchange, copy, delegate);
/* 146 */     if (!delegate.tryComplete()) {
/* 147 */       delegate.resume();
/*     */     }
/*     */   }
/*     */   
/*     */   public void onReset(Stream stream, ResetFrame frame)
/*     */   {
/* 153 */     HttpExchange exchange = getHttpExchange();
/* 154 */     if (exchange == null) {
/* 155 */       return;
/*     */     }
/* 157 */     ErrorCode error = ErrorCode.from(frame.getError());
/* 158 */     String reason = error == null ? "reset" : error.name().toLowerCase(Locale.ENGLISH);
/* 159 */     exchange.getRequest().abort(new IOException(reason));
/*     */   }
/*     */   
/*     */ 
/*     */   public void onTimeout(Stream stream, Throwable failure)
/*     */   {
/* 165 */     responseFailure(failure);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\client\http\HttpReceiverOverHTTP2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */