/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.Queues;
/*     */ import java.util.Iterator;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.annotation.concurrent.GuardedBy;
/*     */ import javax.annotation.concurrent.ThreadSafe;
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
/*     */ @ThreadSafe
/*     */ final class ExecutionQueue
/*     */ {
/*  55 */   private static final Logger logger = Logger.getLogger(ExecutionQueue.class.getName());
/*     */   private final ConcurrentLinkedQueue<RunnableExecutorPair> queuedListeners;
/*     */   
/*  58 */   ExecutionQueue() { this.queuedListeners = Queues.newConcurrentLinkedQueue();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  64 */     this.lock = new ReentrantLock();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   void add(Runnable runnable, Executor executor)
/*     */   {
/*  71 */     this.queuedListeners.add(new RunnableExecutorPair(runnable, executor));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final ReentrantLock lock;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   void execute()
/*     */   {
/*  88 */     Iterator<RunnableExecutorPair> iterator = this.queuedListeners.iterator();
/*  89 */     while (iterator.hasNext()) {
/*  90 */       ((RunnableExecutorPair)iterator.next()).submit();
/*  91 */       iterator.remove();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final class RunnableExecutorPair
/*     */     implements Runnable
/*     */   {
/*     */     private final Executor executor;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private final Runnable runnable;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     @GuardedBy("lock")
/* 116 */     private boolean hasBeenExecuted = false;
/*     */     
/*     */     RunnableExecutorPair(Runnable runnable, Executor executor)
/*     */     {
/* 120 */       this.runnable = ((Runnable)Preconditions.checkNotNull(runnable));
/* 121 */       this.executor = ((Executor)Preconditions.checkNotNull(executor));
/*     */     }
/*     */     
/*     */     private void submit()
/*     */     {
/* 126 */       ExecutionQueue.this.lock.lock();
/*     */       try {
/* 128 */         if (!this.hasBeenExecuted) {
/*     */           try {
/* 130 */             this.executor.execute(this);
/*     */           } catch (Exception e) {
/* 132 */             ExecutionQueue.logger.log(Level.SEVERE, "Exception while executing listener " + this.runnable + " with executor " + this.executor, e);
/*     */           }
/*     */           
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 139 */         if (ExecutionQueue.this.lock.isHeldByCurrentThread()) {
/* 140 */           this.hasBeenExecuted = true;
/* 141 */           ExecutionQueue.this.lock.unlock();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public final void run()
/*     */     {
/* 150 */       if (ExecutionQueue.this.lock.isHeldByCurrentThread()) {
/* 151 */         this.hasBeenExecuted = true;
/* 152 */         ExecutionQueue.this.lock.unlock();
/*     */       }
/* 154 */       this.runnable.run();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\util\concurrent\ExecutionQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */