/*     */ package com.facebook.presto.jdbc.internal.jetty.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Result;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class HttpChannel
/*     */ {
/*  27 */   protected static final Logger LOG = Log.getLogger(HttpChannel.class);
/*     */   
/*     */   private final HttpDestination _destination;
/*     */   private HttpExchange _exchange;
/*     */   
/*     */   protected HttpChannel(HttpDestination destination)
/*     */   {
/*  34 */     this._destination = destination;
/*     */   }
/*     */   
/*     */   public HttpDestination getHttpDestination()
/*     */   {
/*  39 */     return this._destination;
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
/*     */   public boolean associate(HttpExchange exchange)
/*     */   {
/*  52 */     boolean result = false;
/*  53 */     boolean abort = true;
/*  54 */     synchronized (this)
/*     */     {
/*  56 */       if (this._exchange == null)
/*     */       {
/*  58 */         abort = false;
/*  59 */         result = exchange.associate(this);
/*  60 */         if (result) {
/*  61 */           this._exchange = exchange;
/*     */         }
/*     */       }
/*     */     }
/*  65 */     if (abort) {
/*  66 */       exchange.getRequest().abort(new UnsupportedOperationException("Pipelined requests not supported"));
/*     */     }
/*  68 */     if (LOG.isDebugEnabled()) {
/*  69 */       LOG.debug("{} associated {} to {}", new Object[] { exchange, Boolean.valueOf(result), this });
/*     */     }
/*  71 */     return result;
/*     */   }
/*     */   
/*     */   public boolean disassociate(HttpExchange exchange)
/*     */   {
/*  76 */     boolean result = false;
/*  77 */     synchronized (this)
/*     */     {
/*  79 */       HttpExchange existing = this._exchange;
/*  80 */       this._exchange = null;
/*  81 */       if (existing == exchange)
/*     */       {
/*  83 */         existing.disassociate(this);
/*  84 */         result = true;
/*     */       }
/*     */     }
/*     */     
/*  88 */     if (LOG.isDebugEnabled())
/*  89 */       LOG.debug("{} disassociated {} from {}", new Object[] { exchange, Boolean.valueOf(result), this });
/*  90 */     return result;
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public HttpExchange getHttpExchange()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_1
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 27	com/facebook/presto/jdbc/internal/jetty/client/HttpChannel:_exchange	Lcom/facebook/presto/jdbc/internal/jetty/client/HttpExchange;
/*     */     //   8: aload_1
/*     */     //   9: monitorexit
/*     */     //   10: areturn
/*     */     //   11: astore_2
/*     */     //   12: aload_1
/*     */     //   13: monitorexit
/*     */     //   14: aload_2
/*     */     //   15: athrow
/*     */     // Line number table:
/*     */     //   Java source line #95	-> byte code offset #0
/*     */     //   Java source line #97	-> byte code offset #4
/*     */     //   Java source line #98	-> byte code offset #11
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	16	0	this	HttpChannel
/*     */     //   2	11	1	Ljava/lang/Object;	Object
/*     */     //   11	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	10	11	finally
/*     */     //   11	14	11	finally
/*     */   }
/*     */   
/*     */   protected abstract HttpSender getHttpSender();
/*     */   
/*     */   protected abstract HttpReceiver getHttpReceiver();
/*     */   
/*     */   public abstract void send();
/*     */   
/*     */   public abstract void release();
/*     */   
/*     */   public void proceed(HttpExchange exchange, Throwable failure)
/*     */   {
/* 111 */     getHttpSender().proceed(exchange, failure);
/*     */   }
/*     */   
/*     */   public boolean abort(HttpExchange exchange, Throwable requestFailure, Throwable responseFailure)
/*     */   {
/* 116 */     boolean requestAborted = false;
/* 117 */     if (requestFailure != null) {
/* 118 */       requestAborted = getHttpSender().abort(exchange, requestFailure);
/*     */     }
/* 120 */     boolean responseAborted = false;
/* 121 */     if (responseFailure != null) {
/* 122 */       responseAborted = abortResponse(exchange, responseFailure);
/*     */     }
/* 124 */     return (requestAborted) || (responseAborted);
/*     */   }
/*     */   
/*     */   public boolean abortResponse(HttpExchange exchange, Throwable failure)
/*     */   {
/* 129 */     return getHttpReceiver().abort(exchange, failure);
/*     */   }
/*     */   
/*     */   public void exchangeTerminated(HttpExchange exchange, Result result)
/*     */   {
/* 134 */     disassociate(exchange);
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 140 */     return String.format("%s@%x(exchange=%s)", new Object[] { getClass().getSimpleName(), Integer.valueOf(hashCode()), getHttpExchange() });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\HttpChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */