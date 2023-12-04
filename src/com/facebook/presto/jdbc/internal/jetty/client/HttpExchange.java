/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.ResponseListener;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Result;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.util.Deque;
/*     */ import java.util.List;
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
/*     */ public class HttpExchange
/*     */ {
/*  30 */   private static final Logger LOG = Log.getLogger(HttpExchange.class);
/*     */   
/*     */   private final HttpDestination destination;
/*     */   private final HttpRequest request;
/*     */   private final List<Response.ResponseListener> listeners;
/*     */   private final HttpResponse response;
/*  36 */   private State requestState = State.PENDING;
/*  37 */   private State responseState = State.PENDING;
/*     */   private HttpChannel _channel;
/*     */   private Throwable requestFailure;
/*     */   private Throwable responseFailure;
/*     */   
/*     */   public HttpExchange(HttpDestination destination, HttpRequest request, List<Response.ResponseListener> listeners)
/*     */   {
/*  44 */     this.destination = destination;
/*  45 */     this.request = request;
/*  46 */     this.listeners = listeners;
/*  47 */     this.response = new HttpResponse(request, listeners);
/*  48 */     HttpConversation conversation = request.getConversation();
/*  49 */     conversation.getExchanges().offer(this);
/*  50 */     conversation.updateResponseListeners(null);
/*     */   }
/*     */   
/*     */   public HttpConversation getConversation()
/*     */   {
/*  55 */     return this.request.getConversation();
/*     */   }
/*     */   
/*     */   public HttpRequest getRequest()
/*     */   {
/*  60 */     return this.request;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public Throwable getRequestFailure()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_1
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 89	com/facebook/presto/jdbc/internal/jetty/client/HttpExchange:requestFailure	Ljava/lang/Throwable;
/*     */     //   8: aload_1
/*     */     //   9: monitorexit
/*     */     //   10: areturn
/*     */     //   11: astore_2
/*     */     //   12: aload_1
/*     */     //   13: monitorexit
/*     */     //   14: aload_2
/*     */     //   15: athrow
/*     */     // Line number table:
/*     */     //   Java source line #65	-> byte code offset #0
/*     */     //   Java source line #67	-> byte code offset #4
/*     */     //   Java source line #68	-> byte code offset #11
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	16	0	this	HttpExchange
/*     */     //   2	11	1	Ljava/lang/Object;	Object
/*     */     //   11	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	10	11	finally
/*     */     //   11	14	11	finally
/*     */   }
/*     */   
/*     */   public List<Response.ResponseListener> getResponseListeners()
/*     */   {
/*  73 */     return this.listeners;
/*     */   }
/*     */   
/*     */   public HttpResponse getResponse()
/*     */   {
/*  78 */     return this.response;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public Throwable getResponseFailure()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_1
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 98	com/facebook/presto/jdbc/internal/jetty/client/HttpExchange:responseFailure	Ljava/lang/Throwable;
/*     */     //   8: aload_1
/*     */     //   9: monitorexit
/*     */     //   10: areturn
/*     */     //   11: astore_2
/*     */     //   12: aload_1
/*     */     //   13: monitorexit
/*     */     //   14: aload_2
/*     */     //   15: athrow
/*     */     // Line number table:
/*     */     //   Java source line #83	-> byte code offset #0
/*     */     //   Java source line #85	-> byte code offset #4
/*     */     //   Java source line #86	-> byte code offset #11
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	16	0	this	HttpExchange
/*     */     //   2	11	1	Ljava/lang/Object;	Object
/*     */     //   11	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	10	11	finally
/*     */     //   11	14	11	finally
/*     */   }
/*     */   
/*     */   boolean associate(HttpChannel channel)
/*     */   {
/*  98 */     boolean result = false;
/*  99 */     boolean abort = false;
/* 100 */     synchronized (this)
/*     */     {
/*     */ 
/*     */ 
/* 104 */       if ((this.requestState == State.PENDING) && (this.responseState == State.PENDING))
/*     */       {
/* 106 */         abort = this._channel != null;
/* 107 */         if (!abort)
/*     */         {
/* 109 */           this._channel = channel;
/* 110 */           result = true;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 115 */     if (abort) {
/* 116 */       this.request.abort(new IllegalStateException(toString()));
/*     */     }
/* 118 */     return result;
/*     */   }
/*     */   
/*     */   void disassociate(HttpChannel channel)
/*     */   {
/* 123 */     boolean abort = false;
/* 124 */     synchronized (this)
/*     */     {
/* 126 */       if ((this._channel != channel) || (this.requestState != State.TERMINATED) || (this.responseState != State.TERMINATED))
/* 127 */         abort = true;
/* 128 */       this._channel = null;
/*     */     }
/*     */     
/* 131 */     if (abort) {
/* 132 */       this.request.abort(new IllegalStateException(toString()));
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   private HttpChannel getHttpChannel()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_1
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 102	com/facebook/presto/jdbc/internal/jetty/client/HttpExchange:_channel	Lcom/facebook/presto/jdbc/internal/jetty/client/HttpChannel;
/*     */     //   8: aload_1
/*     */     //   9: monitorexit
/*     */     //   10: areturn
/*     */     //   11: astore_2
/*     */     //   12: aload_1
/*     */     //   13: monitorexit
/*     */     //   14: aload_2
/*     */     //   15: athrow
/*     */     // Line number table:
/*     */     //   Java source line #137	-> byte code offset #0
/*     */     //   Java source line #139	-> byte code offset #4
/*     */     //   Java source line #140	-> byte code offset #11
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	16	0	this	HttpExchange
/*     */     //   2	11	1	Ljava/lang/Object;	Object
/*     */     //   11	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	10	11	finally
/*     */     //   11	14	11	finally
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public boolean requestComplete(Throwable failure)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_2
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: aload_1
/*     */     //   6: invokespecial 129	com/facebook/presto/jdbc/internal/jetty/client/HttpExchange:completeRequest	(Ljava/lang/Throwable;)Z
/*     */     //   9: aload_2
/*     */     //   10: monitorexit
/*     */     //   11: ireturn
/*     */     //   12: astore_3
/*     */     //   13: aload_2
/*     */     //   14: monitorexit
/*     */     //   15: aload_3
/*     */     //   16: athrow
/*     */     // Line number table:
/*     */     //   Java source line #145	-> byte code offset #0
/*     */     //   Java source line #147	-> byte code offset #4
/*     */     //   Java source line #148	-> byte code offset #12
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	17	0	this	HttpExchange
/*     */     //   0	17	1	failure	Throwable
/*     */     //   2	12	2	Ljava/lang/Object;	Object
/*     */     //   12	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	11	12	finally
/*     */     //   12	15	12	finally
/*     */   }
/*     */   
/*     */   private boolean completeRequest(Throwable failure)
/*     */   {
/* 153 */     if (this.requestState == State.PENDING)
/*     */     {
/* 155 */       this.requestState = State.COMPLETED;
/* 156 */       this.requestFailure = failure;
/* 157 */       return true;
/*     */     }
/* 159 */     return false;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public boolean responseComplete(Throwable failure)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_2
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: aload_1
/*     */     //   6: invokespecial 137	com/facebook/presto/jdbc/internal/jetty/client/HttpExchange:completeResponse	(Ljava/lang/Throwable;)Z
/*     */     //   9: aload_2
/*     */     //   10: monitorexit
/*     */     //   11: ireturn
/*     */     //   12: astore_3
/*     */     //   13: aload_2
/*     */     //   14: monitorexit
/*     */     //   15: aload_3
/*     */     //   16: athrow
/*     */     // Line number table:
/*     */     //   Java source line #164	-> byte code offset #0
/*     */     //   Java source line #166	-> byte code offset #4
/*     */     //   Java source line #167	-> byte code offset #12
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	17	0	this	HttpExchange
/*     */     //   0	17	1	failure	Throwable
/*     */     //   2	12	2	Ljava/lang/Object;	Object
/*     */     //   12	4	3	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	11	12	finally
/*     */     //   12	15	12	finally
/*     */   }
/*     */   
/*     */   private boolean completeResponse(Throwable failure)
/*     */   {
/* 172 */     if (this.responseState == State.PENDING)
/*     */     {
/* 174 */       this.responseState = State.COMPLETED;
/* 175 */       this.responseFailure = failure;
/* 176 */       return true;
/*     */     }
/* 178 */     return false;
/*     */   }
/*     */   
/*     */   public Result terminateRequest()
/*     */   {
/* 183 */     Result result = null;
/* 184 */     synchronized (this)
/*     */     {
/* 186 */       if (this.requestState == State.COMPLETED)
/* 187 */         this.requestState = State.TERMINATED;
/* 188 */       if ((this.requestState == State.TERMINATED) && (this.responseState == State.TERMINATED)) {
/* 189 */         result = new Result(getRequest(), this.requestFailure, getResponse(), this.responseFailure);
/*     */       }
/*     */     }
/* 192 */     if (LOG.isDebugEnabled()) {
/* 193 */       LOG.debug("Terminated request for {}, result: {}", new Object[] { this, result });
/*     */     }
/* 195 */     return result;
/*     */   }
/*     */   
/*     */   public Result terminateResponse()
/*     */   {
/* 200 */     Result result = null;
/* 201 */     synchronized (this)
/*     */     {
/* 203 */       if (this.responseState == State.COMPLETED)
/* 204 */         this.responseState = State.TERMINATED;
/* 205 */       if ((this.requestState == State.TERMINATED) && (this.responseState == State.TERMINATED)) {
/* 206 */         result = new Result(getRequest(), this.requestFailure, getResponse(), this.responseFailure);
/*     */       }
/*     */     }
/* 209 */     if (LOG.isDebugEnabled()) {
/* 210 */       LOG.debug("Terminated response for {}, result: {}", new Object[] { this, result });
/*     */     }
/* 212 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean abort(Throwable failure)
/*     */   {
/*     */     boolean abortResponse;
/*     */     
/* 221 */     synchronized (this)
/*     */     {
/* 223 */       boolean abortRequest = completeRequest(failure);
/* 224 */       abortResponse = completeResponse(failure); }
/*     */     boolean abortResponse;
/*     */     boolean abortRequest;
/* 227 */     if (LOG.isDebugEnabled()) {
/* 228 */       LOG.debug("Failed {}: req={}/rsp={} {}", new Object[] { this, Boolean.valueOf(abortRequest), Boolean.valueOf(abortResponse), failure });
/*     */     }
/* 230 */     if ((!abortRequest) && (!abortResponse)) {
/* 231 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 236 */     if (this.destination.remove(this))
/*     */     {
/* 238 */       if (LOG.isDebugEnabled())
/* 239 */         LOG.debug("Aborting while queued {}: {}", new Object[] { this, failure });
/* 240 */       notifyFailureComplete(failure);
/* 241 */       return true;
/*     */     }
/*     */     
/* 244 */     HttpChannel channel = getHttpChannel();
/* 245 */     if (channel == null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 250 */       if (LOG.isDebugEnabled())
/* 251 */         LOG.debug("Aborted before association {}: {}", new Object[] { this, failure });
/* 252 */       notifyFailureComplete(failure);
/* 253 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 257 */     boolean aborted = channel.abort(this, abortRequest ? failure : null, abortResponse ? failure : null);
/* 258 */     if (LOG.isDebugEnabled())
/* 259 */       LOG.debug("Aborted ({}) while active {}: {}", new Object[] { Boolean.valueOf(aborted), this, failure });
/* 260 */     return aborted;
/*     */   }
/*     */   
/*     */   private void notifyFailureComplete(Throwable failure)
/*     */   {
/* 265 */     this.destination.getRequestNotifier().notifyFailure(this.request, failure);
/* 266 */     List<Response.ResponseListener> listeners = getConversation().getResponseListeners();
/* 267 */     ResponseNotifier responseNotifier = this.destination.getResponseNotifier();
/* 268 */     responseNotifier.notifyFailure(listeners, this.response, failure);
/* 269 */     responseNotifier.notifyComplete(listeners, new Result(this.request, failure, this.response, failure));
/*     */   }
/*     */   
/*     */   public void resetResponse()
/*     */   {
/* 274 */     synchronized (this)
/*     */     {
/* 276 */       this.responseState = State.PENDING;
/* 277 */       this.responseFailure = null;
/*     */     }
/*     */   }
/*     */   
/*     */   public void proceed(Throwable failure)
/*     */   {
/* 283 */     HttpChannel channel = getHttpChannel();
/* 284 */     if (channel != null) {
/* 285 */       channel.proceed(this, failure);
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString()
/*     */   {
/* 291 */     synchronized (this)
/*     */     {
/* 293 */       return String.format("%s@%x req=%s/%s@%h res=%s/%s@%h", new Object[] {HttpExchange.class
/* 294 */         .getSimpleName(), 
/* 295 */         Integer.valueOf(hashCode()), this.requestState, this.requestFailure, this.requestFailure, this.responseState, this.responseFailure, this.responseFailure });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static enum State
/*     */   {
/* 303 */     PENDING,  COMPLETED,  TERMINATED;
/*     */     
/*     */     private State() {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\HttpExchange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */