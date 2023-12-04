/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.CancellationException;
/*     */ import java.util.concurrent.CountDownLatch;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
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
/*     */ public class FuturePromise<C>
/*     */   implements Future<C>, Promise<C>
/*     */ {
/*  32 */   private static Throwable COMPLETED = new Throwable();
/*  33 */   private final AtomicBoolean _done = new AtomicBoolean(false);
/*  34 */   private final CountDownLatch _latch = new CountDownLatch(1);
/*     */   
/*     */   private Throwable _cause;
/*     */   private C _result;
/*     */   
/*     */   public FuturePromise() {}
/*     */   
/*     */   public FuturePromise(C result)
/*     */   {
/*  43 */     this._cause = COMPLETED;
/*  44 */     this._result = result;
/*  45 */     this._done.set(true);
/*  46 */     this._latch.countDown();
/*     */   }
/*     */   
/*     */   public FuturePromise(C ctx, Throwable failed)
/*     */   {
/*  51 */     this._result = ctx;
/*  52 */     this._cause = failed;
/*  53 */     this._done.set(true);
/*  54 */     this._latch.countDown();
/*     */   }
/*     */   
/*     */ 
/*     */   public void succeeded(C result)
/*     */   {
/*  60 */     if (this._done.compareAndSet(false, true))
/*     */     {
/*  62 */       this._result = result;
/*  63 */       this._cause = COMPLETED;
/*  64 */       this._latch.countDown();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void failed(Throwable cause)
/*     */   {
/*  71 */     if (this._done.compareAndSet(false, true))
/*     */     {
/*  73 */       this._cause = cause;
/*  74 */       this._latch.countDown();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean cancel(boolean mayInterruptIfRunning)
/*     */   {
/*  81 */     if (this._done.compareAndSet(false, true))
/*     */     {
/*  83 */       this._result = null;
/*  84 */       this._cause = new CancellationException();
/*  85 */       this._latch.countDown();
/*  86 */       return true;
/*     */     }
/*  88 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCancelled()
/*     */   {
/*  94 */     if (this._done.get())
/*     */     {
/*     */       try
/*     */       {
/*  98 */         this._latch.await();
/*     */       }
/*     */       catch (InterruptedException e)
/*     */       {
/* 102 */         throw new RuntimeException(e);
/*     */       }
/* 104 */       return this._cause instanceof CancellationException;
/*     */     }
/* 106 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDone()
/*     */   {
/* 112 */     return (this._done.get()) && (this._latch.getCount() == 0L);
/*     */   }
/*     */   
/*     */   public C get()
/*     */     throws InterruptedException, ExecutionException
/*     */   {
/* 118 */     this._latch.await();
/* 119 */     if (this._cause == COMPLETED)
/* 120 */       return (C)this._result;
/* 121 */     if ((this._cause instanceof CancellationException))
/* 122 */       throw ((CancellationException)new CancellationException().initCause(this._cause));
/* 123 */     throw new ExecutionException(this._cause);
/*     */   }
/*     */   
/*     */   public C get(long timeout, TimeUnit unit)
/*     */     throws InterruptedException, ExecutionException, TimeoutException
/*     */   {
/* 129 */     if (!this._latch.await(timeout, unit)) {
/* 130 */       throw new TimeoutException();
/*     */     }
/* 132 */     if (this._cause == COMPLETED)
/* 133 */       return (C)this._result;
/* 134 */     if ((this._cause instanceof TimeoutException))
/* 135 */       throw ((TimeoutException)this._cause);
/* 136 */     if ((this._cause instanceof CancellationException))
/* 137 */       throw ((CancellationException)new CancellationException().initCause(this._cause));
/* 138 */     throw new ExecutionException(this._cause);
/*     */   }
/*     */   
/*     */   public static void rethrow(ExecutionException e) throws IOException
/*     */   {
/* 143 */     Throwable cause = e.getCause();
/* 144 */     if ((cause instanceof IOException))
/* 145 */       throw ((IOException)cause);
/* 146 */     if ((cause instanceof Error))
/* 147 */       throw ((Error)cause);
/* 148 */     if ((cause instanceof RuntimeException))
/* 149 */       throw ((RuntimeException)cause);
/* 150 */     throw new RuntimeException(cause);
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 156 */     return String.format("FutureCallback@%x{%b,%b,%s}", new Object[] { Integer.valueOf(hashCode()), Boolean.valueOf(this._done.get()), Boolean.valueOf(this._cause == COMPLETED ? 1 : false), this._result });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\FuturePromise.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */