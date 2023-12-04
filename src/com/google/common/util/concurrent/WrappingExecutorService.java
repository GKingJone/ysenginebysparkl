/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Throwables;
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import com.google.common.collect.ImmutableList.Builder;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
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
/*     */ abstract class WrappingExecutorService
/*     */   implements ExecutorService
/*     */ {
/*     */   private final ExecutorService delegate;
/*     */   
/*     */   protected WrappingExecutorService(ExecutorService delegate)
/*     */   {
/*  50 */     this.delegate = ((ExecutorService)Preconditions.checkNotNull(delegate));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract <T> Callable<T> wrapTask(Callable<T> paramCallable);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Runnable wrapTask(Runnable command)
/*     */   {
/*  67 */     final Callable<Object> wrapped = wrapTask(Executors.callable(command, null));
/*     */     
/*  69 */     new Runnable() {
/*     */       public void run() {
/*     */         try {
/*  72 */           wrapped.call();
/*     */         } catch (Exception e) {
/*  74 */           Throwables.propagate(e);
/*     */         }
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final <T> ImmutableList<Callable<T>> wrapTasks(Collection<? extends Callable<T>> tasks)
/*     */   {
/*  87 */     ImmutableList.Builder<Callable<T>> builder = ImmutableList.builder();
/*  88 */     for (Callable<T> task : tasks) {
/*  89 */       builder.add(wrapTask(task));
/*     */     }
/*  91 */     return builder.build();
/*     */   }
/*     */   
/*     */ 
/*     */   public final void execute(Runnable command)
/*     */   {
/*  97 */     this.delegate.execute(wrapTask(command));
/*     */   }
/*     */   
/*     */   public final <T> Future<T> submit(Callable<T> task)
/*     */   {
/* 102 */     return this.delegate.submit(wrapTask((Callable)Preconditions.checkNotNull(task)));
/*     */   }
/*     */   
/*     */   public final Future<?> submit(Runnable task)
/*     */   {
/* 107 */     return this.delegate.submit(wrapTask(task));
/*     */   }
/*     */   
/*     */   public final <T> Future<T> submit(Runnable task, T result)
/*     */   {
/* 112 */     return this.delegate.submit(wrapTask(task), result);
/*     */   }
/*     */   
/*     */   public final <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
/*     */     throws InterruptedException
/*     */   {
/* 118 */     return this.delegate.invokeAll(wrapTasks(tasks));
/*     */   }
/*     */   
/*     */ 
/*     */   public final <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
/*     */     throws InterruptedException
/*     */   {
/* 125 */     return this.delegate.invokeAll(wrapTasks(tasks), timeout, unit);
/*     */   }
/*     */   
/*     */   public final <T> T invokeAny(Collection<? extends Callable<T>> tasks)
/*     */     throws InterruptedException, ExecutionException
/*     */   {
/* 131 */     return (T)this.delegate.invokeAny(wrapTasks(tasks));
/*     */   }
/*     */   
/*     */ 
/*     */   public final <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
/*     */     throws InterruptedException, ExecutionException, TimeoutException
/*     */   {
/* 138 */     return (T)this.delegate.invokeAny(wrapTasks(tasks), timeout, unit);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final void shutdown()
/*     */   {
/* 145 */     this.delegate.shutdown();
/*     */   }
/*     */   
/*     */   public final List<Runnable> shutdownNow()
/*     */   {
/* 150 */     return this.delegate.shutdownNow();
/*     */   }
/*     */   
/*     */   public final boolean isShutdown()
/*     */   {
/* 155 */     return this.delegate.isShutdown();
/*     */   }
/*     */   
/*     */   public final boolean isTerminated()
/*     */   {
/* 160 */     return this.delegate.isTerminated();
/*     */   }
/*     */   
/*     */   public final boolean awaitTermination(long timeout, TimeUnit unit)
/*     */     throws InterruptedException
/*     */   {
/* 166 */     return this.delegate.awaitTermination(timeout, unit);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\util\concurrent\WrappingExecutorService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */