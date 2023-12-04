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
/*     */ public class FutureCallback
/*     */   implements Future<Void>, Callback
/*     */ {
/*  32 */   private static Throwable COMPLETED = new Throwable();
/*  33 */   private final AtomicBoolean _done = new AtomicBoolean(false);
/*  34 */   private final CountDownLatch _latch = new CountDownLatch(1);
/*     */   
/*     */   private Throwable _cause;
/*     */   
/*     */   public FutureCallback() {}
/*     */   
/*     */   public FutureCallback(boolean completed)
/*     */   {
/*  42 */     if (completed)
/*     */     {
/*  44 */       this._cause = COMPLETED;
/*  45 */       this._done.set(true);
/*  46 */       this._latch.countDown();
/*     */     }
/*     */   }
/*     */   
/*     */   public FutureCallback(Throwable failed)
/*     */   {
/*  52 */     this._cause = failed;
/*  53 */     this._done.set(true);
/*  54 */     this._latch.countDown();
/*     */   }
/*     */   
/*     */ 
/*     */   public void succeeded()
/*     */   {
/*  60 */     if (this._done.compareAndSet(false, true))
/*     */     {
/*  62 */       this._cause = COMPLETED;
/*  63 */       this._latch.countDown();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void failed(Throwable cause)
/*     */   {
/*  70 */     if (this._done.compareAndSet(false, true))
/*     */     {
/*  72 */       this._cause = cause;
/*  73 */       this._latch.countDown();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean cancel(boolean mayInterruptIfRunning)
/*     */   {
/*  80 */     if (this._done.compareAndSet(false, true))
/*     */     {
/*  82 */       this._cause = new CancellationException();
/*  83 */       this._latch.countDown();
/*  84 */       return true;
/*     */     }
/*  86 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCancelled()
/*     */   {
/*  92 */     if (this._done.get())
/*     */     {
/*     */       try
/*     */       {
/*  96 */         this._latch.await();
/*     */       }
/*     */       catch (InterruptedException e)
/*     */       {
/* 100 */         throw new RuntimeException(e);
/*     */       }
/* 102 */       return this._cause instanceof CancellationException;
/*     */     }
/* 104 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDone()
/*     */   {
/* 110 */     return (this._done.get()) && (this._latch.getCount() == 0L);
/*     */   }
/*     */   
/*     */   public Void get()
/*     */     throws InterruptedException, ExecutionException
/*     */   {
/* 116 */     this._latch.await();
/* 117 */     if (this._cause == COMPLETED)
/* 118 */       return null;
/* 119 */     if ((this._cause instanceof CancellationException))
/* 120 */       throw ((CancellationException)new CancellationException().initCause(this._cause));
/* 121 */     throw new ExecutionException(this._cause);
/*     */   }
/*     */   
/*     */   public Void get(long timeout, TimeUnit unit)
/*     */     throws InterruptedException, ExecutionException, TimeoutException
/*     */   {
/* 127 */     if (!this._latch.await(timeout, unit)) {
/* 128 */       throw new TimeoutException();
/*     */     }
/* 130 */     if (this._cause == COMPLETED)
/* 131 */       return null;
/* 132 */     if ((this._cause instanceof TimeoutException))
/* 133 */       throw ((TimeoutException)this._cause);
/* 134 */     if ((this._cause instanceof CancellationException))
/* 135 */       throw ((CancellationException)new CancellationException().initCause(this._cause));
/* 136 */     throw new ExecutionException(this._cause);
/*     */   }
/*     */   
/*     */   public static void rethrow(ExecutionException e) throws IOException
/*     */   {
/* 141 */     Throwable cause = e.getCause();
/* 142 */     if ((cause instanceof IOException))
/* 143 */       throw ((IOException)cause);
/* 144 */     if ((cause instanceof Error))
/* 145 */       throw ((Error)cause);
/* 146 */     if ((cause instanceof RuntimeException))
/* 147 */       throw ((RuntimeException)cause);
/* 148 */     throw new RuntimeException(cause);
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 154 */     return String.format("FutureCallback@%x{%b,%b}", new Object[] { Integer.valueOf(hashCode()), Boolean.valueOf(this._done.get()), Boolean.valueOf(this._cause == COMPLETED ? 1 : false) });
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\FutureCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */