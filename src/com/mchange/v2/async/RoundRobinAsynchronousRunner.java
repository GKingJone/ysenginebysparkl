/*     */ package com.mchange.v2.async;
/*     */ 
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import com.mchange.v2.util.ResourceClosedException;
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
/*     */ public class RoundRobinAsynchronousRunner
/*     */   implements AsynchronousRunner, Queuable
/*     */ {
/*  37 */   private static final MLogger logger = MLog.getLogger(RoundRobinAsynchronousRunner.class);
/*     */   
/*     */ 
/*     */   final RunnableQueue[] rqs;
/*     */   
/*     */ 
/*  43 */   int task_turn = 0;
/*     */   
/*     */ 
/*  46 */   int view_turn = 0;
/*     */   
/*     */   public RoundRobinAsynchronousRunner(int num_threads, boolean daemon)
/*     */   {
/*  50 */     this.rqs = new RunnableQueue[num_threads];
/*  51 */     for (int i = 0; i < num_threads; i++) {
/*  52 */       this.rqs[i] = new CarefulRunnableQueue(daemon, false);
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void postRunnable(Runnable r)
/*     */   {
/*     */     try {
/*  59 */       int index = this.task_turn;
/*  60 */       this.task_turn = ((this.task_turn + 1) % this.rqs.length);
/*  61 */       this.rqs[index].postRunnable(r);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (NullPointerException e)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  80 */       if (logger.isLoggable(MLevel.FINE)) {
/*  81 */         logger.log(MLevel.FINE, "NullPointerException while posting Runnable -- Probably we're closed.", e);
/*     */       }
/*  83 */       close(true);
/*  84 */       throw new ResourceClosedException("Attempted to use a RoundRobinAsynchronousRunner in a closed or broken state.");
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized RunnableQueue asRunnableQueue()
/*     */   {
/*     */     try
/*     */     {
/*  92 */       int index = this.view_turn;
/*  93 */       this.view_turn = ((this.view_turn + 1) % this.rqs.length);
/*  94 */       return new RunnableQueueView(index);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (NullPointerException e)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 107 */       if (logger.isLoggable(MLevel.FINE)) {
/* 108 */         logger.log(MLevel.FINE, "NullPointerException in asRunnableQueue() -- Probably we're closed.", e);
/*     */       }
/* 110 */       close(true);
/* 111 */       throw new ResourceClosedException("Attempted to use a RoundRobinAsynchronousRunner in a closed or broken state.");
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void close(boolean skip_remaining_tasks)
/*     */   {
/* 117 */     int i = 0; for (int len = this.rqs.length; i < len; i++)
/*     */     {
/* 119 */       attemptClose(this.rqs[i], skip_remaining_tasks);
/* 120 */       this.rqs[i] = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/* 125 */   public void close() { close(true); }
/*     */   
/*     */   static void attemptClose(RunnableQueue rq, boolean skip_remaining_tasks) {
/*     */     try {
/* 129 */       rq.close(skip_remaining_tasks);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 133 */       if (logger.isLoggable(MLevel.WARNING)) {
/* 134 */         logger.log(MLevel.WARNING, "RunnableQueue close FAILED.", e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   class RunnableQueueView implements RunnableQueue {
/*     */     final int rq_num;
/*     */     
/*     */     RunnableQueueView(int rq_num) {
/* 143 */       this.rq_num = rq_num;
/*     */     }
/*     */     
/* 146 */     public void postRunnable(Runnable r) { RoundRobinAsynchronousRunner.this.rqs[this.rq_num].postRunnable(r); }
/*     */     
/*     */     public void close(boolean skip_remaining_tasks) {}
/*     */     
/*     */     public void close() {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\async\RoundRobinAsynchronousRunner.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */