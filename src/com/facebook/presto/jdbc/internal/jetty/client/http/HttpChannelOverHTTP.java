/*     */ package com.facebook.presto.jdbc.internal.jetty.client.http;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpChannel;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpExchange;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpReceiver;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpRequest;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpSender;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Result;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpFields;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpHeader;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpHeaderValue;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpMethod;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpVersion;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
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
/*     */ public class HttpChannelOverHTTP
/*     */   extends HttpChannel
/*     */ {
/*     */   private final HttpConnectionOverHTTP connection;
/*     */   private final HttpSenderOverHTTP sender;
/*     */   private final HttpReceiverOverHTTP receiver;
/*     */   
/*     */   public HttpChannelOverHTTP(HttpConnectionOverHTTP connection)
/*     */   {
/*  41 */     super(connection.getHttpDestination());
/*  42 */     this.connection = connection;
/*  43 */     this.sender = newHttpSender();
/*  44 */     this.receiver = newHttpReceiver();
/*     */   }
/*     */   
/*     */   protected HttpSenderOverHTTP newHttpSender()
/*     */   {
/*  49 */     return new HttpSenderOverHTTP(this);
/*     */   }
/*     */   
/*     */   protected HttpReceiverOverHTTP newHttpReceiver()
/*     */   {
/*  54 */     return new HttpReceiverOverHTTP(this);
/*     */   }
/*     */   
/*     */ 
/*     */   protected HttpSender getHttpSender()
/*     */   {
/*  60 */     return this.sender;
/*     */   }
/*     */   
/*     */ 
/*     */   protected HttpReceiver getHttpReceiver()
/*     */   {
/*  66 */     return this.receiver;
/*     */   }
/*     */   
/*     */   public HttpConnectionOverHTTP getHttpConnection()
/*     */   {
/*  71 */     return this.connection;
/*     */   }
/*     */   
/*     */ 
/*     */   public void send()
/*     */   {
/*  77 */     HttpExchange exchange = getHttpExchange();
/*  78 */     if (exchange != null) {
/*  79 */       this.sender.send(exchange);
/*     */     }
/*     */   }
/*     */   
/*     */   public void release()
/*     */   {
/*  85 */     this.connection.release();
/*     */   }
/*     */   
/*     */   public void receive()
/*     */   {
/*  90 */     this.receiver.receive();
/*     */   }
/*     */   
/*     */ 
/*     */   public void exchangeTerminated(HttpExchange exchange, Result result)
/*     */   {
/*  96 */     super.exchangeTerminated(exchange, result);
/*     */     
/*  98 */     Response response = result.getResponse();
/*  99 */     HttpFields responseHeaders = response.getHeaders();
/*     */     
/* 101 */     String closeReason = null;
/* 102 */     if (result.isFailed()) {
/* 103 */       closeReason = "failure";
/* 104 */     } else if (this.receiver.isShutdown()) {
/* 105 */       closeReason = "server close";
/*     */     }
/* 107 */     if (closeReason == null)
/*     */     {
/* 109 */       if (response.getVersion().compareTo(HttpVersion.HTTP_1_1) < 0)
/*     */       {
/*     */ 
/*     */ 
/* 113 */         boolean keepAlive = responseHeaders.contains(HttpHeader.CONNECTION, HttpHeaderValue.KEEP_ALIVE.asString());
/* 114 */         boolean connect = HttpMethod.CONNECT.is(exchange.getRequest().getMethod());
/* 115 */         if ((!keepAlive) && (!connect)) {
/* 116 */           closeReason = "http/1.0";
/*     */         }
/*     */         
/*     */ 
/*     */       }
/* 121 */       else if (responseHeaders.contains(HttpHeader.CONNECTION, HttpHeaderValue.CLOSE.asString())) {
/* 122 */         closeReason = "http/1.1";
/*     */       }
/*     */     }
/*     */     
/* 126 */     if (closeReason != null)
/*     */     {
/* 128 */       if (LOG.isDebugEnabled())
/* 129 */         LOG.debug("Closing, reason: {} - {}", new Object[] { closeReason, this.connection });
/* 130 */       this.connection.close();
/*     */     }
/*     */     else
/*     */     {
/* 134 */       release();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 141 */     return String.format("%s[send=%s,recv=%s]", new Object[] {
/* 142 */       super.toString(), this.sender, this.receiver });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\http\HttpChannelOverHTTP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */