/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.Listener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.ResponseListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Result;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpField;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpFields;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpHeader;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.CountingCallback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.IOException;
/*     */ import java.net.CookieManager;
/*     */ import java.net.URI;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicReference;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class HttpReceiver
/*     */ {
/*  70 */   protected static final Logger LOG = Log.getLogger(HttpReceiver.class);
/*     */   
/*  72 */   private final AtomicReference<ResponseState> responseState = new AtomicReference(ResponseState.IDLE);
/*     */   private final HttpChannel channel;
/*     */   private ContentDecoder decoder;
/*     */   private Throwable failure;
/*     */   
/*     */   protected HttpReceiver(HttpChannel channel)
/*     */   {
/*  79 */     this.channel = channel;
/*     */   }
/*     */   
/*     */   protected HttpChannel getHttpChannel()
/*     */   {
/*  84 */     return this.channel;
/*     */   }
/*     */   
/*     */   protected HttpExchange getHttpExchange()
/*     */   {
/*  89 */     return this.channel.getHttpExchange();
/*     */   }
/*     */   
/*     */   protected HttpDestination getHttpDestination()
/*     */   {
/*  94 */     return this.channel.getHttpDestination();
/*     */   }
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
/*     */   protected boolean responseBegin(HttpExchange exchange)
/*     */   {
/* 110 */     if (!updateResponseState(ResponseState.IDLE, ResponseState.TRANSIENT)) {
/* 111 */       return false;
/*     */     }
/* 113 */     HttpConversation conversation = exchange.getConversation();
/* 114 */     HttpResponse response = exchange.getResponse();
/*     */     
/* 116 */     HttpDestination destination = getHttpDestination();
/* 117 */     HttpClient client = destination.getHttpClient();
/* 118 */     ProtocolHandler protocolHandler = client.findProtocolHandler(exchange.getRequest(), response);
/* 119 */     Response.Listener handlerListener = null;
/* 120 */     if (protocolHandler != null)
/*     */     {
/* 122 */       handlerListener = protocolHandler.getResponseListener();
/* 123 */       if (LOG.isDebugEnabled())
/* 124 */         LOG.debug("Found protocol handler {}", new Object[] { protocolHandler });
/*     */     }
/* 126 */     exchange.getConversation().updateResponseListeners(handlerListener);
/*     */     
/* 128 */     if (LOG.isDebugEnabled())
/* 129 */       LOG.debug("Response begin {}", new Object[] { response });
/* 130 */     ResponseNotifier notifier = destination.getResponseNotifier();
/* 131 */     notifier.notifyBegin(conversation.getResponseListeners(), response);
/*     */     
/* 133 */     if (updateResponseState(ResponseState.TRANSIENT, ResponseState.BEGIN)) {
/* 134 */       return true;
/*     */     }
/* 136 */     terminateResponse(exchange);
/* 137 */     return false;
/*     */   }
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
/*     */   protected boolean responseHeader(HttpExchange exchange, HttpField field)
/*     */   {
/*     */     for (;;)
/*     */     {
/* 156 */       ResponseState current = (ResponseState)this.responseState.get();
/* 157 */       switch (current)
/*     */       {
/*     */ 
/*     */       case BEGIN: 
/*     */       case HEADER: 
/* 162 */         if (!updateResponseState(current, ResponseState.TRANSIENT)) break;
/* 163 */         break;
/*     */       
/*     */ 
/*     */ 
/*     */       default: 
/* 168 */         return false;
/*     */       }
/*     */       
/*     */     }
/*     */     
/* 173 */     HttpResponse response = exchange.getResponse();
/* 174 */     ResponseNotifier notifier = getHttpDestination().getResponseNotifier();
/* 175 */     boolean process = notifier.notifyHeader(exchange.getConversation().getResponseListeners(), response, field);
/* 176 */     if (process)
/*     */     {
/* 178 */       response.getHeaders().add(field);
/* 179 */       HttpHeader fieldHeader = field.getHeader();
/* 180 */       if (fieldHeader != null)
/*     */       {
/* 182 */         switch (fieldHeader)
/*     */         {
/*     */ 
/*     */         case SET_COOKIE: 
/*     */         case SET_COOKIE2: 
/* 187 */           URI uri = exchange.getRequest().getURI();
/* 188 */           if (uri != null) {
/* 189 */             storeCookie(uri, field);
/*     */           }
/*     */           
/*     */ 
/*     */           break;
/*     */         }
/*     */         
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 200 */     if (updateResponseState(ResponseState.TRANSIENT, ResponseState.HEADER)) {
/* 201 */       return true;
/*     */     }
/* 203 */     terminateResponse(exchange);
/* 204 */     return false;
/*     */   }
/*     */   
/*     */   protected void storeCookie(URI uri, HttpField field)
/*     */   {
/*     */     try
/*     */     {
/* 211 */       String value = field.getValue();
/* 212 */       if (value != null)
/*     */       {
/* 214 */         Map<String, List<String>> header = new HashMap(1);
/* 215 */         header.put(field.getHeader().asString(), Collections.singletonList(value));
/* 216 */         getHttpDestination().getHttpClient().getCookieManager().put(uri, header);
/*     */       }
/*     */     }
/*     */     catch (IOException x)
/*     */     {
/* 221 */       if (LOG.isDebugEnabled()) {
/* 222 */         LOG.debug(x);
/*     */       }
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
/*     */   protected boolean responseHeaders(HttpExchange exchange)
/*     */   {
/*     */     for (;;)
/*     */     {
/* 238 */       ResponseState current = (ResponseState)this.responseState.get();
/* 239 */       switch (current)
/*     */       {
/*     */ 
/*     */       case BEGIN: 
/*     */       case HEADER: 
/* 244 */         if (!updateResponseState(current, ResponseState.TRANSIENT)) break;
/* 245 */         break;
/*     */       
/*     */ 
/*     */ 
/*     */       default: 
/* 250 */         return false;
/*     */       }
/*     */       
/*     */     }
/*     */     
/* 255 */     HttpResponse response = exchange.getResponse();
/* 256 */     if (LOG.isDebugEnabled())
/* 257 */       LOG.debug("Response headers {}{}{}", new Object[] { response, System.lineSeparator(), response.getHeaders().toString().trim() });
/* 258 */     ResponseNotifier notifier = getHttpDestination().getResponseNotifier();
/* 259 */     notifier.notifyHeaders(exchange.getConversation().getResponseListeners(), response);
/*     */     
/* 261 */     Enumeration<String> contentEncodings = response.getHeaders().getValues(HttpHeader.CONTENT_ENCODING.asString(), ",");
/* 262 */     if (contentEncodings != null)
/*     */     {
/* 264 */       for (ContentDecoder.Factory factory : getHttpDestination().getHttpClient().getContentDecoderFactories())
/*     */       {
/* 266 */         while (contentEncodings.hasMoreElements())
/*     */         {
/* 268 */           if (factory.getEncoding().equalsIgnoreCase((String)contentEncodings.nextElement()))
/*     */           {
/* 270 */             this.decoder = factory.newContentDecoder();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 277 */     if (updateResponseState(ResponseState.TRANSIENT, ResponseState.HEADERS)) {
/* 278 */       return true;
/*     */     }
/* 280 */     terminateResponse(exchange);
/* 281 */     return false;
/*     */   }
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
/*     */   protected boolean responseContent(HttpExchange exchange, ByteBuffer buffer, Callback callback)
/*     */   {
/*     */     for (;;)
/*     */     {
/* 298 */       ResponseState current = (ResponseState)this.responseState.get();
/* 299 */       switch (current)
/*     */       {
/*     */ 
/*     */       case HEADERS: 
/*     */       case CONTENT: 
/* 304 */         if (!updateResponseState(current, ResponseState.TRANSIENT)) break;
/* 305 */         break;
/*     */       
/*     */ 
/*     */ 
/*     */       default: 
/* 310 */         callback.failed(new IllegalStateException("Invalid response state " + current));
/* 311 */         return false;
/*     */       }
/*     */       
/*     */     }
/*     */     
/* 316 */     HttpResponse response = exchange.getResponse();
/* 317 */     if (LOG.isDebugEnabled()) {
/* 318 */       LOG.debug("Response content {}{}{}", new Object[] { response, System.lineSeparator(), BufferUtil.toDetailString(buffer) });
/*     */     }
/* 320 */     ResponseNotifier notifier = getHttpDestination().getResponseNotifier();
/* 321 */     List<Response.ResponseListener> listeners = exchange.getConversation().getResponseListeners();
/*     */     
/* 323 */     ContentDecoder decoder = this.decoder;
/* 324 */     if (decoder == null)
/*     */     {
/* 326 */       notifier.notifyContent(listeners, response, buffer, callback);
/*     */     }
/*     */     else
/*     */     {
/*     */       try
/*     */       {
/* 332 */         List<ByteBuffer> decodeds = new ArrayList(2);
/* 333 */         while (buffer.hasRemaining())
/*     */         {
/* 335 */           ByteBuffer decoded = decoder.decode(buffer);
/* 336 */           if (decoded.hasRemaining())
/*     */           {
/* 338 */             decodeds.add(decoded);
/* 339 */             if (LOG.isDebugEnabled())
/* 340 */               LOG.debug("Response content decoded ({}) {}{}{}", new Object[] { decoder, response, System.lineSeparator(), BufferUtil.toDetailString(decoded) });
/*     */           }
/*     */         }
/* 343 */         if (decodeds.isEmpty())
/*     */         {
/* 345 */           callback.succeeded();
/*     */         }
/*     */         else
/*     */         {
/* 349 */           int size = decodeds.size();
/* 350 */           CountingCallback counter = new CountingCallback(callback, size);
/* 351 */           for (int i = 0; i < size; i++) {
/* 352 */             notifier.notifyContent(listeners, response, (ByteBuffer)decodeds.get(i), counter);
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable x) {
/* 357 */         callback.failed(x);
/*     */       }
/*     */     }
/*     */     
/* 361 */     if (updateResponseState(ResponseState.TRANSIENT, ResponseState.CONTENT)) {
/* 362 */       return true;
/*     */     }
/* 364 */     terminateResponse(exchange);
/* 365 */     return false;
/*     */   }
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
/*     */   protected boolean responseSuccess(HttpExchange exchange)
/*     */   {
/* 381 */     if (!exchange.responseComplete(null)) {
/* 382 */       return false;
/*     */     }
/* 384 */     this.responseState.set(ResponseState.IDLE);
/*     */     
/*     */ 
/* 387 */     reset();
/*     */     
/* 389 */     HttpResponse response = exchange.getResponse();
/* 390 */     if (LOG.isDebugEnabled())
/* 391 */       LOG.debug("Response success {}", new Object[] { response });
/* 392 */     List<Response.ResponseListener> listeners = exchange.getConversation().getResponseListeners();
/* 393 */     ResponseNotifier notifier = getHttpDestination().getResponseNotifier();
/* 394 */     notifier.notifySuccess(listeners, response);
/*     */     
/*     */ 
/*     */ 
/* 398 */     if (exchange.getResponse().getStatus() == 100) {
/* 399 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 403 */     Result result = exchange.terminateResponse();
/* 404 */     terminateResponse(exchange, result);
/*     */     
/* 406 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean responseFailure(Throwable failure)
/*     */   {
/* 419 */     HttpExchange exchange = getHttpExchange();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 424 */     if (exchange == null) {
/* 425 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 429 */     if (exchange.responseComplete(failure)) {
/* 430 */       return abort(exchange, failure);
/*     */     }
/* 432 */     return false;
/*     */   }
/*     */   
/*     */   private void terminateResponse(HttpExchange exchange)
/*     */   {
/* 437 */     Result result = exchange.terminateResponse();
/* 438 */     terminateResponse(exchange, result);
/*     */   }
/*     */   
/*     */   private void terminateResponse(HttpExchange exchange, Result result)
/*     */   {
/* 443 */     HttpResponse response = exchange.getResponse();
/*     */     
/* 445 */     if (LOG.isDebugEnabled()) {
/* 446 */       LOG.debug("Response complete {}", new Object[] { response });
/*     */     }
/* 448 */     if (result != null)
/*     */     {
/* 450 */       boolean ordered = getHttpDestination().getHttpClient().isStrictEventOrdering();
/* 451 */       if (!ordered)
/* 452 */         this.channel.exchangeTerminated(exchange, result);
/* 453 */       if (LOG.isDebugEnabled())
/* 454 */         LOG.debug("Request/Response {}: {}", new Object[] { this.failure == null ? "succeeded" : "failed", result });
/* 455 */       List<Response.ResponseListener> listeners = exchange.getConversation().getResponseListeners();
/* 456 */       ResponseNotifier notifier = getHttpDestination().getResponseNotifier();
/* 457 */       notifier.notifyComplete(listeners, result);
/* 458 */       if (ordered) {
/* 459 */         this.channel.exchangeTerminated(exchange, result);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void reset()
/*     */   {
/* 472 */     this.decoder = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void dispose()
/*     */   {
/* 484 */     this.decoder = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean abort(HttpExchange exchange, Throwable failure)
/*     */   {
/*     */     for (;;)
/*     */     {
/* 493 */       ResponseState current = (ResponseState)this.responseState.get();
/* 494 */       switch (current)
/*     */       {
/*     */ 
/*     */       case FAILURE: 
/* 498 */         return false;
/*     */       }
/*     */       
/*     */       
/* 502 */       if (updateResponseState(current, ResponseState.FAILURE))
/*     */       {
/* 504 */         boolean terminate = current != ResponseState.TRANSIENT;
/* 505 */         break;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     boolean terminate;
/*     */     
/* 512 */     this.failure = failure;
/*     */     
/* 514 */     dispose();
/*     */     
/* 516 */     HttpResponse response = exchange.getResponse();
/* 517 */     if (LOG.isDebugEnabled())
/* 518 */       LOG.debug("Response failure {} {} on {}: {}", new Object[] { response, exchange, getHttpChannel(), failure });
/* 519 */     List<Response.ResponseListener> listeners = exchange.getConversation().getResponseListeners();
/* 520 */     ResponseNotifier notifier = getHttpDestination().getResponseNotifier();
/* 521 */     notifier.notifyFailure(listeners, response, failure);
/*     */     
/* 523 */     if (terminate)
/*     */     {
/*     */ 
/*     */ 
/* 527 */       Result result = exchange.terminateResponse();
/* 528 */       terminateResponse(exchange, result);
/*     */ 
/*     */ 
/*     */     }
/* 532 */     else if (LOG.isDebugEnabled()) {
/* 533 */       LOG.debug("Concurrent failure: response termination skipped, performed by helpers", new Object[0]);
/*     */     }
/*     */     
/* 536 */     return true;
/*     */   }
/*     */   
/*     */   private boolean updateResponseState(ResponseState from, ResponseState to)
/*     */   {
/* 541 */     boolean updated = this.responseState.compareAndSet(from, to);
/* 542 */     if (!updated)
/*     */     {
/* 544 */       if (LOG.isDebugEnabled())
/* 545 */         LOG.debug("State update failed: {} -> {}: {}", new Object[] { from, to, this.responseState.get() });
/*     */     }
/* 547 */     return updated;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 553 */     return String.format("%s@%x(rsp=%s,failure=%s)", new Object[] {
/* 554 */       getClass().getSimpleName(), 
/* 555 */       Integer.valueOf(hashCode()), this.responseState, this.failure });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static enum ResponseState
/*     */   {
/* 568 */     TRANSIENT, 
/*     */     
/*     */ 
/*     */ 
/* 572 */     IDLE, 
/*     */     
/*     */ 
/*     */ 
/* 576 */     BEGIN, 
/*     */     
/*     */ 
/*     */ 
/* 580 */     HEADER, 
/*     */     
/*     */ 
/*     */ 
/* 584 */     HEADERS, 
/*     */     
/*     */ 
/*     */ 
/* 588 */     CONTENT, 
/*     */     
/*     */ 
/*     */ 
/* 592 */     FAILURE;
/*     */     
/*     */     private ResponseState() {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\HttpReceiver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */