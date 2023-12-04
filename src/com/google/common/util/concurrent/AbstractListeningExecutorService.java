/*    */ package com.google.common.util.concurrent;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import java.util.concurrent.AbstractExecutorService;
/*    */ import java.util.concurrent.Callable;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Beta
/*    */ public abstract class AbstractListeningExecutorService
/*    */   extends AbstractExecutorService
/*    */   implements ListeningExecutorService
/*    */ {
/*    */   protected final <T> ListenableFutureTask<T> newTaskFor(Runnable runnable, T value)
/*    */   {
/* 42 */     return ListenableFutureTask.create(runnable, value);
/*    */   }
/*    */   
/*    */   protected final <T> ListenableFutureTask<T> newTaskFor(Callable<T> callable) {
/* 46 */     return ListenableFutureTask.create(callable);
/*    */   }
/*    */   
/*    */   public ListenableFuture<?> submit(Runnable task) {
/* 50 */     return (ListenableFuture)super.submit(task);
/*    */   }
/*    */   
/*    */   public <T> ListenableFuture<T> submit(Runnable task, @Nullable T result) {
/* 54 */     return (ListenableFuture)super.submit(task, result);
/*    */   }
/*    */   
/*    */   public <T> ListenableFuture<T> submit(Callable<T> task) {
/* 58 */     return (ListenableFuture)super.submit(task);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\util\concurrent\AbstractListeningExecutorService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */