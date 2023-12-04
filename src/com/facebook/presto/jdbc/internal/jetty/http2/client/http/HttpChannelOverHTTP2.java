/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.client.http;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpChannel;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpDestination;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpExchange;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpReceiver;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpRequest;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpSender;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Result;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.ErrorCode;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.HTTP2Stream;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.api.Session;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.api.Stream;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.api.Stream.Listener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.ResetFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
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
/*     */ public class HttpChannelOverHTTP2
/*     */   extends HttpChannel
/*     */ {
/*     */   private final HttpConnectionOverHTTP2 connection;
/*     */   private final Session session;
/*     */   private final HttpSenderOverHTTP2 sender;
/*     */   private final HttpReceiverOverHTTP2 receiver;
/*     */   private Stream stream;
/*     */   
/*     */   public HttpChannelOverHTTP2(HttpDestination destination, HttpConnectionOverHTTP2 connection, Session session)
/*     */   {
/*  44 */     super(destination);
/*  45 */     this.connection = connection;
/*  46 */     this.session = session;
/*  47 */     this.sender = new HttpSenderOverHTTP2(this);
/*  48 */     this.receiver = new HttpReceiverOverHTTP2(this);
/*     */   }
/*     */   
/*     */   public Session getSession()
/*     */   {
/*  53 */     return this.session;
/*     */   }
/*     */   
/*     */   public Listener getStreamListener()
/*     */   {
/*  58 */     return this.receiver;
/*     */   }
/*     */   
/*     */ 
/*     */   protected HttpSender getHttpSender()
/*     */   {
/*  64 */     return this.sender;
/*     */   }
/*     */   
/*     */ 
/*     */   protected HttpReceiver getHttpReceiver()
/*     */   {
/*  70 */     return this.receiver;
/*     */   }
/*     */   
/*     */   public Stream getStream()
/*     */   {
/*  75 */     return this.stream;
/*     */   }
/*     */   
/*     */   public void setStream(Stream stream)
/*     */   {
/*  80 */     this.stream = stream;
/*  81 */     getHttpExchange().getRequest().attribute(HTTP2Stream.class.getName(), stream);
/*     */   }
/*     */   
/*     */ 
/*     */   public void send()
/*     */   {
/*  87 */     HttpExchange exchange = getHttpExchange();
/*  88 */     if (exchange != null) {
/*  89 */       this.sender.send(exchange);
/*     */     }
/*     */   }
/*     */   
/*     */   public void release()
/*     */   {
/*  95 */     this.connection.release(this);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean abort(HttpExchange exchange, Throwable requestFailure, Throwable responseFailure)
/*     */   {
/* 101 */     boolean aborted = super.abort(exchange, requestFailure, responseFailure);
/* 102 */     if (aborted)
/*     */     {
/* 104 */       Stream stream = getStream();
/* 105 */       if (stream != null)
/* 106 */         stream.reset(new ResetFrame(stream.getId(), ErrorCode.CANCEL_STREAM_ERROR.code), Callback.NOOP);
/*     */     }
/* 108 */     return aborted;
/*     */   }
/*     */   
/*     */ 
/*     */   public void exchangeTerminated(HttpExchange exchange, Result result)
/*     */   {
/* 114 */     super.exchangeTerminated(exchange, result);
/* 115 */     release();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\client\http\HttpChannelOverHTTP2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */