/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.ContentProvider;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Result;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpFields;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpHeader;
/*     */ import com.facebook.presto.jdbc.internal.jetty.http.HttpHeaderValue;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BufferUtil;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Callback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.IteratingCallback;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.IteratingCallback.Action;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.nio.ByteBuffer;
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
/*     */ public abstract class HttpSender
/*     */   implements AsyncContentProvider.Listener
/*     */ {
/*  60 */   protected static final Logger LOG = Log.getLogger(HttpSender.class);
/*     */   
/*  62 */   private final AtomicReference<RequestState> requestState = new AtomicReference(RequestState.QUEUED);
/*  63 */   private final AtomicReference<SenderState> senderState = new AtomicReference(SenderState.IDLE);
/*  64 */   private final Callback commitCallback = new CommitCallback(null);
/*  65 */   private final IteratingCallback contentCallback = new ContentCallback(null);
/*  66 */   private final Callback lastCallback = new LastContentCallback(null);
/*     */   private final HttpChannel channel;
/*     */   private HttpContent content;
/*     */   private Throwable failure;
/*     */   
/*     */   protected HttpSender(HttpChannel channel)
/*     */   {
/*  73 */     this.channel = channel;
/*     */   }
/*     */   
/*     */   protected HttpChannel getHttpChannel()
/*     */   {
/*  78 */     return this.channel;
/*     */   }
/*     */   
/*     */   protected HttpExchange getHttpExchange()
/*     */   {
/*  83 */     return this.channel.getHttpExchange();
/*     */   }
/*     */   
/*     */ 
/*     */   public void onContent()
/*     */   {
/*  89 */     HttpExchange exchange = getHttpExchange();
/*  90 */     if (exchange == null) {
/*  91 */       return;
/*     */     }
/*     */     for (;;)
/*     */     {
/*  95 */       SenderState current = (SenderState)this.senderState.get();
/*  96 */       switch (current)
/*     */       {
/*     */ 
/*     */       case IDLE: 
/* 100 */         SenderState newSenderState = SenderState.SENDING;
/* 101 */         if (updateSenderState(current, newSenderState))
/*     */         {
/* 103 */           if (LOG.isDebugEnabled())
/* 104 */             LOG.debug("Deferred content available, {} -> {}", new Object[] { current, newSenderState });
/* 105 */           this.contentCallback.iterate();
/* 106 */           return;
/*     */         }
/*     */         
/*     */ 
/*     */         break;
/*     */       case SENDING: 
/* 112 */         SenderState newSenderState = SenderState.SENDING_WITH_CONTENT;
/* 113 */         if (updateSenderState(current, newSenderState))
/*     */         {
/* 115 */           if (LOG.isDebugEnabled())
/* 116 */             LOG.debug("Deferred content available, {} -> {}", new Object[] { current, newSenderState });
/* 117 */           return;
/*     */         }
/*     */         
/*     */ 
/*     */         break;
/*     */       case EXPECTING: 
/* 123 */         SenderState newSenderState = SenderState.EXPECTING_WITH_CONTENT;
/* 124 */         if (updateSenderState(current, newSenderState))
/*     */         {
/* 126 */           if (LOG.isDebugEnabled())
/* 127 */             LOG.debug("Deferred content available, {} -> {}", new Object[] { current, newSenderState });
/* 128 */           return;
/*     */         }
/*     */         
/*     */ 
/*     */         break;
/*     */       case PROCEEDING: 
/* 134 */         SenderState newSenderState = SenderState.PROCEEDING_WITH_CONTENT;
/* 135 */         if (updateSenderState(current, newSenderState))
/*     */         {
/* 137 */           if (LOG.isDebugEnabled())
/* 138 */             LOG.debug("Deferred content available, {} -> {}", new Object[] { current, newSenderState });
/* 139 */           return;
/*     */         }
/*     */         
/*     */ 
/*     */         break;
/*     */       case SENDING_WITH_CONTENT: 
/*     */       case EXPECTING_WITH_CONTENT: 
/*     */       case PROCEEDING_WITH_CONTENT: 
/*     */       case WAITING: 
/*     */       case COMPLETED: 
/*     */       case FAILED: 
/* 150 */         if (LOG.isDebugEnabled())
/* 151 */           LOG.debug("Deferred content available, {}", new Object[] { current });
/* 152 */         return;
/*     */       
/*     */ 
/*     */       default: 
/* 156 */         illegalSenderState(current);
/* 157 */         return;
/*     */       }
/*     */       
/*     */     }
/*     */   }
/*     */   
/*     */   public void send(HttpExchange exchange)
/*     */   {
/* 165 */     if (!queuedToBegin(exchange)) {
/* 166 */       return;
/*     */     }
/* 168 */     Request request = exchange.getRequest();
/* 169 */     ContentProvider contentProvider = request.getContent();
/* 170 */     HttpContent content = this.content = new HttpContent(contentProvider);
/*     */     
/* 172 */     SenderState newSenderState = SenderState.SENDING;
/* 173 */     if (expects100Continue(request)) {
/* 174 */       newSenderState = content.hasContent() ? SenderState.EXPECTING_WITH_CONTENT : SenderState.EXPECTING;
/*     */     }
/*     */     for (;;)
/*     */     {
/* 178 */       SenderState current = (SenderState)this.senderState.get();
/* 179 */       switch (current)
/*     */       {
/*     */ 
/*     */       case IDLE: 
/*     */       case COMPLETED: 
/* 184 */         if (!updateSenderState(current, newSenderState)) break;
/* 185 */         break;
/*     */       
/*     */ 
/*     */ 
/*     */       default: 
/* 190 */         illegalSenderState(current);
/* 191 */         return;
/*     */       }
/*     */       
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 198 */     if ((contentProvider instanceof AsyncContentProvider)) {
/* 199 */       ((AsyncContentProvider)contentProvider).setListener(this);
/*     */     }
/* 201 */     if (!beginToHeaders(exchange)) {
/* 202 */       return;
/*     */     }
/* 204 */     sendHeaders(exchange, content, this.commitCallback);
/*     */   }
/*     */   
/*     */   protected boolean expects100Continue(Request request)
/*     */   {
/* 209 */     return request.getHeaders().contains(HttpHeader.EXPECT, HttpHeaderValue.CONTINUE.asString());
/*     */   }
/*     */   
/*     */   protected boolean queuedToBegin(HttpExchange exchange)
/*     */   {
/* 214 */     if (!updateRequestState(RequestState.QUEUED, RequestState.TRANSIENT)) {
/* 215 */       return false;
/*     */     }
/* 217 */     Request request = exchange.getRequest();
/* 218 */     if (LOG.isDebugEnabled())
/* 219 */       LOG.debug("Request begin {}", new Object[] { request });
/* 220 */     RequestNotifier notifier = getHttpChannel().getHttpDestination().getRequestNotifier();
/* 221 */     notifier.notifyBegin(request);
/*     */     
/* 223 */     if (updateRequestState(RequestState.TRANSIENT, RequestState.BEGIN)) {
/* 224 */       return true;
/*     */     }
/* 226 */     terminateRequest(exchange);
/* 227 */     return false;
/*     */   }
/*     */   
/*     */   protected boolean beginToHeaders(HttpExchange exchange)
/*     */   {
/* 232 */     if (!updateRequestState(RequestState.BEGIN, RequestState.TRANSIENT)) {
/* 233 */       return false;
/*     */     }
/* 235 */     Request request = exchange.getRequest();
/* 236 */     if (LOG.isDebugEnabled())
/* 237 */       LOG.debug("Request headers {}{}{}", new Object[] { request, System.lineSeparator(), request.getHeaders().toString().trim() });
/* 238 */     RequestNotifier notifier = getHttpChannel().getHttpDestination().getRequestNotifier();
/* 239 */     notifier.notifyHeaders(request);
/*     */     
/* 241 */     if (updateRequestState(RequestState.TRANSIENT, RequestState.HEADERS)) {
/* 242 */       return true;
/*     */     }
/* 244 */     terminateRequest(exchange);
/* 245 */     return false;
/*     */   }
/*     */   
/*     */   protected boolean headersToCommit(HttpExchange exchange)
/*     */   {
/* 250 */     if (!updateRequestState(RequestState.HEADERS, RequestState.TRANSIENT)) {
/* 251 */       return false;
/*     */     }
/* 253 */     Request request = exchange.getRequest();
/* 254 */     if (LOG.isDebugEnabled())
/* 255 */       LOG.debug("Request committed {}", new Object[] { request });
/* 256 */     RequestNotifier notifier = getHttpChannel().getHttpDestination().getRequestNotifier();
/* 257 */     notifier.notifyCommit(request);
/*     */     
/* 259 */     if (updateRequestState(RequestState.TRANSIENT, RequestState.COMMIT)) {
/* 260 */       return true;
/*     */     }
/* 262 */     terminateRequest(exchange);
/* 263 */     return false;
/*     */   }
/*     */   
/*     */   protected boolean someToContent(HttpExchange exchange, ByteBuffer content)
/*     */   {
/* 268 */     RequestState current = (RequestState)this.requestState.get();
/* 269 */     switch (current)
/*     */     {
/*     */ 
/*     */     case COMMIT: 
/*     */     case CONTENT: 
/* 274 */       if (!updateRequestState(current, RequestState.TRANSIENT)) {
/* 275 */         return false;
/*     */       }
/* 277 */       Request request = exchange.getRequest();
/* 278 */       if (LOG.isDebugEnabled())
/* 279 */         LOG.debug("Request content {}{}{}", new Object[] { request, System.lineSeparator(), BufferUtil.toDetailString(content) });
/* 280 */       RequestNotifier notifier = getHttpChannel().getHttpDestination().getRequestNotifier();
/* 281 */       notifier.notifyContent(request, content);
/*     */       
/* 283 */       if (updateRequestState(RequestState.TRANSIENT, RequestState.CONTENT)) {
/* 284 */         return true;
/*     */       }
/* 286 */       terminateRequest(exchange);
/* 287 */       return false;
/*     */     }
/*     */     
/*     */     
/* 291 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean someToSuccess(HttpExchange exchange)
/*     */   {
/* 298 */     RequestState current = (RequestState)this.requestState.get();
/* 299 */     switch (current)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */     case COMMIT: 
/*     */     case CONTENT: 
/* 306 */       if (!exchange.requestComplete(null)) {
/* 307 */         return false;
/*     */       }
/* 309 */       this.requestState.set(RequestState.QUEUED);
/*     */       
/*     */ 
/* 312 */       reset();
/*     */       
/* 314 */       Request request = exchange.getRequest();
/* 315 */       if (LOG.isDebugEnabled())
/* 316 */         LOG.debug("Request success {}", new Object[] { request });
/* 317 */       HttpDestination destination = getHttpChannel().getHttpDestination();
/* 318 */       destination.getRequestNotifier().notifySuccess(exchange.getRequest());
/*     */       
/*     */ 
/*     */ 
/* 322 */       Result result = exchange.terminateRequest();
/* 323 */       terminateRequest(exchange, null, result);
/* 324 */       return true;
/*     */     }
/*     */     
/*     */     
/* 328 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean anyToFailure(Throwable failure)
/*     */   {
/* 335 */     HttpExchange exchange = getHttpExchange();
/* 336 */     if (exchange == null) {
/* 337 */       return false;
/*     */     }
/*     */     
/*     */ 
/* 341 */     if (exchange.requestComplete(failure)) {
/* 342 */       return abort(exchange, failure);
/*     */     }
/* 344 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void terminateRequest(HttpExchange exchange)
/*     */   {
/* 351 */     Throwable failure = this.failure;
/* 352 */     if (failure == null)
/* 353 */       failure = new HttpRequestException("Concurrent failure", exchange.getRequest());
/* 354 */     Result result = exchange.terminateRequest();
/* 355 */     terminateRequest(exchange, failure, result);
/*     */   }
/*     */   
/*     */   private void terminateRequest(HttpExchange exchange, Throwable failure, Result result)
/*     */   {
/* 360 */     Request request = exchange.getRequest();
/*     */     
/* 362 */     if (LOG.isDebugEnabled()) {
/* 363 */       LOG.debug("Terminating request {}", new Object[] { request });
/*     */     }
/* 365 */     if (result == null)
/*     */     {
/* 367 */       if (failure != null)
/*     */       {
/* 369 */         if (exchange.responseComplete(failure))
/*     */         {
/* 371 */           if (LOG.isDebugEnabled())
/* 372 */             LOG.debug("Response failure from request {} {}", new Object[] { request, exchange });
/* 373 */           getHttpChannel().abortResponse(exchange, failure);
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 379 */       HttpDestination destination = getHttpChannel().getHttpDestination();
/* 380 */       boolean ordered = destination.getHttpClient().isStrictEventOrdering();
/* 381 */       if (!ordered)
/* 382 */         this.channel.exchangeTerminated(exchange, result);
/* 383 */       if (LOG.isDebugEnabled())
/* 384 */         LOG.debug("Request/Response {}: {}", new Object[] { failure == null ? "succeeded" : "failed", result });
/* 385 */       HttpConversation conversation = exchange.getConversation();
/* 386 */       destination.getResponseNotifier().notifyComplete(conversation.getResponseListeners(), result);
/* 387 */       if (ordered) {
/* 388 */         this.channel.exchangeTerminated(exchange, result);
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
/*     */ 
/*     */ 
/*     */   protected abstract void sendHeaders(HttpExchange paramHttpExchange, HttpContent paramHttpContent, Callback paramCallback);
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
/*     */   protected abstract void sendContent(HttpExchange paramHttpExchange, HttpContent paramHttpContent, Callback paramCallback);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void reset()
/*     */   {
/* 425 */     HttpContent content = this.content;
/* 426 */     this.content = null;
/* 427 */     content.close();
/* 428 */     this.senderState.set(SenderState.COMPLETED);
/*     */   }
/*     */   
/*     */   protected void dispose()
/*     */   {
/* 433 */     HttpContent content = this.content;
/* 434 */     this.content = null;
/* 435 */     if (content != null)
/* 436 */       content.close();
/* 437 */     this.senderState.set(SenderState.FAILED);
/*     */   }
/*     */   
/*     */   public void proceed(HttpExchange exchange, Throwable failure)
/*     */   {
/* 442 */     if (!expects100Continue(exchange.getRequest())) {
/* 443 */       return;
/*     */     }
/* 445 */     if (failure != null)
/*     */     {
/* 447 */       anyToFailure(failure);
/*     */     }
/*     */     else
/*     */     {
/*     */       for (;;)
/*     */       {
/* 453 */         SenderState current = (SenderState)this.senderState.get();
/* 454 */         switch (current)
/*     */         {
/*     */ 
/*     */ 
/*     */         case EXPECTING: 
/* 459 */           if (updateSenderState(current, SenderState.PROCEEDING))
/*     */           {
/* 461 */             if (LOG.isDebugEnabled()) {
/* 462 */               LOG.debug("Proceeding while expecting", new Object[0]);
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */             return;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */           break;
/*     */         case EXPECTING_WITH_CONTENT: 
/* 475 */           if (updateSenderState(current, SenderState.PROCEEDING_WITH_CONTENT))
/*     */           {
/* 477 */             if (LOG.isDebugEnabled()) {
/* 478 */               LOG.debug("Proceeding while scheduled", new Object[0]);
/*     */             }
/*     */             
/*     */             return;
/*     */           }
/*     */           
/*     */           break;
/*     */         case WAITING: 
/* 486 */           if (updateSenderState(current, SenderState.SENDING))
/*     */           {
/* 488 */             if (LOG.isDebugEnabled())
/* 489 */               LOG.debug("Proceeding while waiting", new Object[0]);
/* 490 */             this.contentCallback.iterate(); return;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */           break;
/*     */         case FAILED: 
/* 497 */           return;
/*     */         case PROCEEDING: case SENDING_WITH_CONTENT: 
/*     */         case PROCEEDING_WITH_CONTENT: case COMPLETED: 
/*     */         default: 
/* 501 */           illegalSenderState(current); return;
/*     */         }
/*     */         
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean abort(HttpExchange exchange, Throwable failure)
/*     */   {
/*     */     for (;;)
/*     */     {
/* 514 */       RequestState current = (RequestState)this.requestState.get();
/* 515 */       switch (current)
/*     */       {
/*     */ 
/*     */       case FAILURE: 
/* 519 */         return false;
/*     */       }
/*     */       
/*     */       
/* 523 */       if (updateRequestState(current, RequestState.FAILURE))
/*     */       {
/* 525 */         boolean terminate = current != RequestState.TRANSIENT;
/* 526 */         break;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     boolean terminate;
/*     */     
/* 533 */     this.failure = failure;
/*     */     
/* 535 */     dispose();
/*     */     
/* 537 */     Request request = exchange.getRequest();
/* 538 */     if (LOG.isDebugEnabled())
/* 539 */       LOG.debug("Request failure {} {} on {}: {}", new Object[] { request, exchange, getHttpChannel(), failure });
/* 540 */     HttpDestination destination = getHttpChannel().getHttpDestination();
/* 541 */     destination.getRequestNotifier().notifyFailure(request, failure);
/*     */     
/* 543 */     if (terminate)
/*     */     {
/*     */ 
/*     */ 
/* 547 */       Result result = exchange.terminateRequest();
/* 548 */       terminateRequest(exchange, failure, result);
/*     */ 
/*     */ 
/*     */     }
/* 552 */     else if (LOG.isDebugEnabled()) {
/* 553 */       LOG.debug("Concurrent failure: request termination skipped, performed by helpers", new Object[0]);
/*     */     }
/*     */     
/* 556 */     return true;
/*     */   }
/*     */   
/*     */   private boolean updateRequestState(RequestState from, RequestState to)
/*     */   {
/* 561 */     boolean updated = this.requestState.compareAndSet(from, to);
/* 562 */     if ((!updated) && (LOG.isDebugEnabled()))
/* 563 */       LOG.debug("RequestState update failed: {} -> {}: {}", new Object[] { from, to, this.requestState.get() });
/* 564 */     return updated;
/*     */   }
/*     */   
/*     */   private boolean updateSenderState(SenderState from, SenderState to)
/*     */   {
/* 569 */     boolean updated = this.senderState.compareAndSet(from, to);
/* 570 */     if ((!updated) && (LOG.isDebugEnabled()))
/* 571 */       LOG.debug("SenderState update failed: {} -> {}: {}", new Object[] { from, to, this.senderState.get() });
/* 572 */     return updated;
/*     */   }
/*     */   
/*     */   private void illegalSenderState(SenderState current)
/*     */   {
/* 577 */     anyToFailure(new IllegalStateException("Expected " + current + " found " + this.senderState.get() + " instead"));
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 583 */     return String.format("%s@%x(req=%s,snd=%s,failure=%s)", new Object[] {
/* 584 */       getClass().getSimpleName(), 
/* 585 */       Integer.valueOf(hashCode()), this.requestState, this.senderState, this.failure });
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
/*     */   private static enum RequestState
/*     */   {
/* 599 */     TRANSIENT, 
/*     */     
/*     */ 
/*     */ 
/* 603 */     QUEUED, 
/*     */     
/*     */ 
/*     */ 
/* 607 */     BEGIN, 
/*     */     
/*     */ 
/*     */ 
/* 611 */     HEADERS, 
/*     */     
/*     */ 
/*     */ 
/* 615 */     COMMIT, 
/*     */     
/*     */ 
/*     */ 
/* 619 */     CONTENT, 
/*     */     
/*     */ 
/*     */ 
/* 623 */     FAILURE;
/*     */     
/*     */ 
/*     */ 
/*     */     private RequestState() {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static enum SenderState
/*     */   {
/* 634 */     IDLE, 
/*     */     
/*     */ 
/*     */ 
/* 638 */     SENDING, 
/*     */     
/*     */ 
/*     */ 
/* 642 */     SENDING_WITH_CONTENT, 
/*     */     
/*     */ 
/*     */ 
/* 646 */     EXPECTING, 
/*     */     
/*     */ 
/*     */ 
/* 650 */     EXPECTING_WITH_CONTENT, 
/*     */     
/*     */ 
/*     */ 
/* 654 */     WAITING, 
/*     */     
/*     */ 
/*     */ 
/* 658 */     PROCEEDING, 
/*     */     
/*     */ 
/*     */ 
/* 662 */     PROCEEDING_WITH_CONTENT, 
/*     */     
/*     */ 
/*     */ 
/* 666 */     COMPLETED, 
/*     */     
/*     */ 
/*     */ 
/* 670 */     FAILED;
/*     */     
/*     */     private SenderState() {}
/*     */   }
/*     */   
/*     */   private class CommitCallback implements Callback {
/*     */     private CommitCallback() {}
/*     */     
/*     */     public boolean isNonBlocking() {
/* 679 */       return HttpSender.this.content.isNonBlocking();
/*     */     }
/*     */     
/*     */ 
/*     */     public void succeeded()
/*     */     {
/*     */       try
/*     */       {
/* 687 */         HttpContent content = HttpSender.this.content;
/* 688 */         if (content == null)
/* 689 */           return;
/* 690 */         content.succeeded();
/* 691 */         process();
/*     */       }
/*     */       catch (Throwable x)
/*     */       {
/* 695 */         HttpSender.this.anyToFailure(x);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void failed(Throwable failure)
/*     */     {
/* 702 */       HttpContent content = HttpSender.this.content;
/* 703 */       if (content == null)
/* 704 */         return;
/* 705 */       content.failed(failure);
/* 706 */       HttpSender.this.anyToFailure(failure);
/*     */     }
/*     */     
/*     */     private void process() throws Exception
/*     */     {
/* 711 */       HttpExchange exchange = HttpSender.this.getHttpExchange();
/* 712 */       if (exchange == null) {
/* 713 */         return;
/*     */       }
/* 715 */       if (!HttpSender.this.headersToCommit(exchange)) {
/* 716 */         return;
/*     */       }
/* 718 */       HttpContent content = HttpSender.this.content;
/* 719 */       if (content == null) {
/* 720 */         return;
/*     */       }
/* 722 */       if (!content.hasContent())
/*     */       {
/*     */ 
/* 725 */         HttpSender.this.someToSuccess(exchange);
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 730 */         ByteBuffer contentBuffer = content.getContent();
/* 731 */         if (contentBuffer != null)
/*     */         {
/* 733 */           if (!HttpSender.this.someToContent(exchange, contentBuffer)) {
/*     */             return;
/*     */           }
/*     */         }
/*     */         for (;;)
/*     */         {
/* 739 */           SenderState current = (SenderState)HttpSender.this.senderState.get();
/* 740 */           switch (HttpSender.1.$SwitchMap$org$eclipse$jetty$client$HttpSender$SenderState[current.ordinal()])
/*     */           {
/*     */ 
/*     */           case 2: 
/* 744 */             HttpSender.this.contentCallback.iterate();
/* 745 */             return;
/*     */           
/*     */ 
/*     */ 
/*     */           case 5: 
/* 750 */             HttpSender.this.updateSenderState(current, SenderState.SENDING);
/* 751 */             break;
/*     */           
/*     */ 
/*     */ 
/*     */           case 3: 
/* 756 */             if (HttpSender.this.updateSenderState(current, SenderState.WAITING)) {
/*     */               return;
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */             break;
/*     */           case 6: 
/* 764 */             if (HttpSender.this.updateSenderState(current, SenderState.WAITING)) {
/*     */               return;
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */             break;
/*     */           case 4: 
/* 772 */             if (HttpSender.this.updateSenderState(current, SenderState.IDLE)) {
/*     */               return;
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */             break;
/*     */           case 7: 
/* 780 */             HttpSender.this.updateSenderState(current, SenderState.SENDING);
/* 781 */             break;
/*     */           
/*     */ 
/*     */           case 10: 
/* 785 */             return;
/*     */           case 8: 
/*     */           case 9: 
/*     */           default: 
/* 789 */             HttpSender.this.illegalSenderState(current);
/* 790 */             return;
/*     */           }
/*     */           
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private class ContentCallback extends IteratingCallback
/*     */   {
/*     */     private ContentCallback() {}
/*     */     
/*     */     protected Action process() throws Exception {
/* 803 */       HttpExchange exchange = HttpSender.this.getHttpExchange();
/* 804 */       if (exchange == null) {
/* 805 */         return Action.IDLE;
/*     */       }
/* 807 */       HttpContent content = HttpSender.this.content;
/* 808 */       if (content == null) {
/* 809 */         return Action.IDLE;
/*     */       }
/*     */       for (;;)
/*     */       {
/* 813 */         boolean advanced = content.advance();
/* 814 */         boolean lastContent = content.isLast();
/* 815 */         if (HttpSender.LOG.isDebugEnabled()) {
/* 816 */           HttpSender.LOG.debug("Content present {}, last {}, consumed {} for {}", new Object[] { Boolean.valueOf(advanced), Boolean.valueOf(lastContent), Boolean.valueOf(content.isConsumed()), exchange.getRequest() });
/*     */         }
/* 818 */         if (advanced)
/*     */         {
/* 820 */           HttpSender.this.sendContent(exchange, content, this);
/* 821 */           return Action.SCHEDULED;
/*     */         }
/*     */         
/* 824 */         if (lastContent)
/*     */         {
/* 826 */           HttpSender.this.sendContent(exchange, content, HttpSender.this.lastCallback);
/* 827 */           return Action.IDLE;
/*     */         }
/*     */         
/* 830 */         SenderState current = (SenderState)HttpSender.this.senderState.get();
/* 831 */         switch (HttpSender.1.$SwitchMap$org$eclipse$jetty$client$HttpSender$SenderState[current.ordinal()])
/*     */         {
/*     */ 
/*     */         case 2: 
/* 835 */           if (HttpSender.this.updateSenderState(current, SenderState.IDLE))
/*     */           {
/* 837 */             if (HttpSender.LOG.isDebugEnabled())
/* 838 */               HttpSender.LOG.debug("Content is deferred for {}", new Object[] { exchange.getRequest() });
/* 839 */             return Action.IDLE;
/*     */           }
/*     */           
/*     */ 
/*     */           break;
/*     */         case 5: 
/* 845 */           HttpSender.this.updateSenderState(current, SenderState.SENDING);
/* 846 */           break;
/*     */         
/*     */ 
/*     */         default: 
/* 850 */           HttpSender.this.illegalSenderState(current);
/* 851 */           return Action.IDLE;
/*     */         }
/*     */         
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public void succeeded()
/*     */     {
/* 860 */       HttpExchange exchange = HttpSender.this.getHttpExchange();
/* 861 */       if (exchange == null)
/* 862 */         return;
/* 863 */       HttpContent content = HttpSender.this.content;
/* 864 */       if (content == null)
/* 865 */         return;
/* 866 */       content.succeeded();
/* 867 */       ByteBuffer buffer = content.getContent();
/* 868 */       HttpSender.this.someToContent(exchange, buffer);
/* 869 */       super.succeeded();
/*     */     }
/*     */     
/*     */ 
/*     */     public void onCompleteFailure(Throwable failure)
/*     */     {
/* 875 */       HttpContent content = HttpSender.this.content;
/* 876 */       if (content == null)
/* 877 */         return;
/* 878 */       content.failed(failure);
/* 879 */       HttpSender.this.anyToFailure(failure);
/*     */     }
/*     */     
/*     */ 
/*     */     protected void onCompleteSuccess() {}
/*     */   }
/*     */   
/*     */ 
/*     */   private class LastContentCallback
/*     */     implements Callback
/*     */   {
/*     */     private LastContentCallback() {}
/*     */     
/*     */ 
/*     */     public boolean isNonBlocking()
/*     */     {
/* 895 */       return HttpSender.this.content.isNonBlocking();
/*     */     }
/*     */     
/*     */ 
/*     */     public void succeeded()
/*     */     {
/* 901 */       HttpExchange exchange = HttpSender.this.getHttpExchange();
/* 902 */       if (exchange == null)
/* 903 */         return;
/* 904 */       HttpContent content = HttpSender.this.content;
/* 905 */       if (content == null)
/* 906 */         return;
/* 907 */       content.succeeded();
/* 908 */       HttpSender.this.someToSuccess(exchange);
/*     */     }
/*     */     
/*     */ 
/*     */     public void failed(Throwable failure)
/*     */     {
/* 914 */       HttpContent content = HttpSender.this.content;
/* 915 */       if (content == null)
/* 916 */         return;
/* 917 */       content.failed(failure);
/* 918 */       HttpSender.this.anyToFailure(failure);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\HttpSender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */