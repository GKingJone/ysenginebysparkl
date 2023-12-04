/*    */ package com.facebook.presto.jdbc.internal.guava.util.concurrent;
/*    */ 
/*    */ import java.util.concurrent.Callable;
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
/*    */ public abstract class ForwardingListeningExecutorService
/*    */   extends ForwardingExecutorService
/*    */   implements ListeningExecutorService
/*    */ {
/*    */   protected abstract ListeningExecutorService delegate();
/*    */   
/*    */   public <T> ListenableFuture<T> submit(Callable<T> task)
/*    */   {
/* 40 */     return delegate().submit(task);
/*    */   }
/*    */   
/*    */   public ListenableFuture<?> submit(Runnable task)
/*    */   {
/* 45 */     return delegate().submit(task);
/*    */   }
/*    */   
/*    */   public <T> ListenableFuture<T> submit(Runnable task, T result)
/*    */   {
/* 50 */     return delegate().submit(task, result);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\util\concurrent\ForwardingListeningExecutorService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */