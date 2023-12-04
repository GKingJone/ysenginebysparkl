/*     */ package com.facebook.presto.jdbc.internal.jetty.client.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.HttpContentResponse;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.ContentResponse;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request;
/*     */ import com.facebook.presto.jdbc.internal.jetty.client.api.Result;
/*     */ import java.util.concurrent.CancellationException;
/*     */ import java.util.concurrent.CountDownLatch;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
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
/*     */ public class FutureResponseListener
/*     */   extends BufferingResponseListener
/*     */   implements Future<ContentResponse>
/*     */ {
/*  48 */   private final CountDownLatch latch = new CountDownLatch(1);
/*     */   private final Request request;
/*     */   private ContentResponse response;
/*     */   private Throwable failure;
/*     */   private volatile boolean cancelled;
/*     */   
/*     */   public FutureResponseListener(Request request)
/*     */   {
/*  56 */     this(request, 2097152);
/*     */   }
/*     */   
/*     */   public FutureResponseListener(Request request, int maxLength)
/*     */   {
/*  61 */     super(maxLength);
/*  62 */     this.request = request;
/*     */   }
/*     */   
/*     */   public Request getRequest()
/*     */   {
/*  67 */     return this.request;
/*     */   }
/*     */   
/*     */ 
/*     */   public void onComplete(Result result)
/*     */   {
/*  73 */     this.response = new HttpContentResponse(result.getResponse(), getContent(), getMediaType(), getEncoding());
/*  74 */     this.failure = result.getFailure();
/*  75 */     this.latch.countDown();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean cancel(boolean mayInterruptIfRunning)
/*     */   {
/*  81 */     this.cancelled = true;
/*  82 */     return this.request.abort(new CancellationException());
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCancelled()
/*     */   {
/*  88 */     return this.cancelled;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDone()
/*     */   {
/*  94 */     return (this.latch.getCount() == 0L) || (isCancelled());
/*     */   }
/*     */   
/*     */   public ContentResponse get()
/*     */     throws InterruptedException, ExecutionException
/*     */   {
/* 100 */     this.latch.await();
/* 101 */     return getResult();
/*     */   }
/*     */   
/*     */   public ContentResponse get(long timeout, TimeUnit unit)
/*     */     throws InterruptedException, ExecutionException, TimeoutException
/*     */   {
/* 107 */     boolean expired = !this.latch.await(timeout, unit);
/* 108 */     if (expired)
/* 109 */       throw new TimeoutException();
/* 110 */     return getResult();
/*     */   }
/*     */   
/*     */   private ContentResponse getResult() throws ExecutionException
/*     */   {
/* 115 */     if (isCancelled())
/* 116 */       throw ((CancellationException)new CancellationException().initCause(this.failure));
/* 117 */     if (this.failure != null)
/* 118 */       throw new ExecutionException(this.failure);
/* 119 */     return this.response;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\util\FutureResponseListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */