/*     */ package com.facebook.presto.jdbc.internal.jetty.http2.client.http;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpContent;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpExchange;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpSender;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpURI;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpVersion;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.MetaData.Request;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.api.Session;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.api.Stream;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.DataFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http2.frames.HeadersFrame;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Promise;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.net.URI;
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
/*     */ public class HttpSenderOverHTTP2
/*     */   extends HttpSender
/*     */ {
/*     */   public HttpSenderOverHTTP2(HttpChannelOverHTTP2 channel)
/*     */   {
/*  40 */     super(channel);
/*     */   }
/*     */   
/*     */ 
/*     */   protected HttpChannelOverHTTP2 getHttpChannel()
/*     */   {
/*  46 */     return (HttpChannelOverHTTP2)super.getHttpChannel();
/*     */   }
/*     */   
/*     */ 
/*     */   protected void sendHeaders(HttpExchange exchange, final HttpContent content, final Callback callback)
/*     */   {
/*  52 */     final Request request = exchange.getRequest();
/*  53 */     String path = relativize(request.getPath());
/*  54 */     HttpURI uri = new HttpURI(request.getScheme(), request.getHost(), request.getPort(), path, null, request.getQuery(), null);
/*  55 */     MetaData.Request metaData = new MetaData.Request(request.getMethod(), uri, HttpVersion.HTTP_2, request.getHeaders());
/*  56 */     HeadersFrame headersFrame = new HeadersFrame(metaData, null, !content.hasContent());
/*  57 */     HttpChannelOverHTTP2 channel = getHttpChannel();
/*  58 */     Promise<Stream> promise = new Promise()
/*     */     {
/*     */ 
/*     */       public void succeeded(Stream stream)
/*     */       {
/*  63 */         HttpSenderOverHTTP2.this.getHttpChannel().setStream(stream);
/*  64 */         stream.setIdleTimeout(request.getIdleTimeout());
/*     */         
/*  66 */         if ((content.hasContent()) && (!HttpSenderOverHTTP2.this.expects100Continue(request)))
/*     */         {
/*  68 */           boolean advanced = content.advance();
/*  69 */           boolean lastContent = content.isLast();
/*  70 */           if ((advanced) || (lastContent))
/*     */           {
/*  72 */             DataFrame dataFrame = new DataFrame(stream.getId(), content.getByteBuffer(), lastContent);
/*  73 */             stream.data(dataFrame, callback);
/*  74 */             return;
/*     */           }
/*     */         }
/*  77 */         callback.succeeded();
/*     */       }
/*     */       
/*     */ 
/*     */       public void failed(Throwable failure)
/*     */       {
/*  83 */         callback.failed(failure);
/*     */       }
/*     */       
/*  86 */     };
/*  87 */     channel.getSession().newStream(headersFrame, promise, channel.getStreamListener());
/*     */   }
/*     */   
/*     */   private String relativize(String path)
/*     */   {
/*     */     try
/*     */     {
/*  94 */       String result = path;
/*  95 */       URI uri = URI.create(result);
/*  96 */       if (uri.isAbsolute())
/*  97 */         result = uri.getPath();
/*  98 */       return result.isEmpty() ? "/" : result;
/*     */     }
/*     */     catch (Throwable x)
/*     */     {
/* 102 */       if (LOG.isDebugEnabled())
/* 103 */         LOG.debug("Could not relativize " + path, new Object[0]); }
/* 104 */     return path;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void sendContent(HttpExchange exchange, HttpContent content, Callback callback)
/*     */   {
/* 111 */     if (content.isConsumed())
/*     */     {
/* 113 */       callback.succeeded();
/*     */     }
/*     */     else
/*     */     {
/* 117 */       Stream stream = getHttpChannel().getStream();
/* 118 */       DataFrame frame = new DataFrame(stream.getId(), content.getByteBuffer(), content.isLast());
/* 119 */       stream.data(frame, callback);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\client\http\HttpSenderOverHTTP2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */