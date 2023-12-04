/*     */ package com.mchange.v2.async;
/*     */ 
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import com.mchange.v2.util.ResourceClosedException;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedList;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CarefulRunnableQueue
/*     */   implements RunnableQueue, Queuable, StrandedTaskReporting
/*     */ {
/*  36 */   private static final MLogger logger = MLog.getLogger(CarefulRunnableQueue.class);
/*     */   
/*  38 */   private List taskList = new LinkedList();
/*  39 */   private TaskThread t = new TaskThread();
/*     */   
/*     */   private boolean shutdown_on_interrupt;
/*     */   
/*  43 */   private boolean gentle_close_requested = false;
/*     */   
/*  45 */   private List strandedTasks = null;
/*     */   
/*     */   public CarefulRunnableQueue(boolean daemon, boolean shutdown_on_interrupt)
/*     */   {
/*  49 */     this.shutdown_on_interrupt = shutdown_on_interrupt;
/*  50 */     this.t.setDaemon(daemon);
/*  51 */     this.t.start();
/*     */   }
/*     */   
/*     */   public RunnableQueue asRunnableQueue() {
/*  55 */     return this;
/*     */   }
/*     */   
/*     */   public synchronized void postRunnable(Runnable r)
/*     */   {
/*     */     try {
/*  61 */       if (this.gentle_close_requested) {
/*  62 */         throw new ResourceClosedException("Attempted to post a task to a closing CarefulRunnableQueue.");
/*     */       }
/*     */       
/*  65 */       this.taskList.add(r);
/*  66 */       notifyAll();
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (NullPointerException e)
/*     */     {
/*     */ 
/*  73 */       if (logger.isLoggable(MLevel.FINE)) {
/*  74 */         logger.log(MLevel.FINE, "NullPointerException while posting Runnable.", e);
/*     */       }
/*  76 */       if (this.taskList == null) {
/*  77 */         throw new ResourceClosedException("Attempted to post a task to a CarefulRunnableQueue which has been closed, or whose TaskThread has been interrupted.");
/*     */       }
/*     */       
/*     */ 
/*  81 */       throw e;
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void close(boolean skip_remaining_tasks)
/*     */   {
/*  87 */     if (skip_remaining_tasks)
/*     */     {
/*  89 */       this.t.safeStop();
/*  90 */       this.t.interrupt();
/*     */     }
/*     */     else {
/*  93 */       this.gentle_close_requested = true;
/*     */     }
/*     */   }
/*     */   
/*  97 */   public synchronized void close() { close(true); }
/*     */   
/*     */   public synchronized List getStrandedTasks()
/*     */   {
/*     */     try
/*     */     {
/* 103 */       while ((this.gentle_close_requested) && (this.taskList != null))
/* 104 */         wait();
/* 105 */       return this.strandedTasks;
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (InterruptedException e)
/*     */     {
/*     */ 
/*     */ 
/* 113 */       if (logger.isLoggable(MLevel.WARNING)) {
/* 114 */         logger.log(MLevel.WARNING, Thread.currentThread() + " interrupted while waiting for stranded tasks from CarefulRunnableQueue.", e);
/*     */       }
/*     */       
/*     */ 
/* 118 */       throw new RuntimeException(Thread.currentThread() + " interrupted while waiting for stranded tasks from CarefulRunnableQueue.");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private synchronized Runnable dequeueRunnable()
/*     */   {
/* 125 */     Runnable r = (Runnable)this.taskList.get(0);
/* 126 */     this.taskList.remove(0);
/* 127 */     return r;
/*     */   }
/*     */   
/*     */   private synchronized void awaitTask() throws InterruptedException
/*     */   {
/* 132 */     while (this.taskList.size() == 0)
/*     */     {
/* 134 */       if (this.gentle_close_requested)
/*     */       {
/* 136 */         this.t.safeStop();
/* 137 */         this.t.interrupt();
/*     */       }
/* 139 */       wait();
/*     */     }
/*     */   }
/*     */   
/*     */   class TaskThread extends Thread
/*     */   {
/* 145 */     boolean should_stop = false;
/*     */     
/*     */     TaskThread() {
/* 148 */       super();
/*     */     }
/*     */     
/* 151 */     public synchronized void safeStop() { this.should_stop = true; }
/*     */     
/*     */     private synchronized boolean shouldStop() {
/* 154 */       return this.should_stop;
/*     */     }
/*     */     
/*     */     public void run()
/*     */     {
/*     */       try {
/* 160 */         while (!shouldStop())
/*     */         {
/*     */           try
/*     */           {
/* 164 */             CarefulRunnableQueue.this.awaitTask();
/* 165 */             Runnable r = CarefulRunnableQueue.this.dequeueRunnable();
/*     */             try {
/* 167 */               r.run();
/*     */ 
/*     */             }
/*     */             catch (Exception e)
/*     */             {
/*     */ 
/* 173 */               if (CarefulRunnableQueue.logger.isLoggable(MLevel.WARNING)) {
/* 174 */                 CarefulRunnableQueue.logger.log(MLevel.WARNING, getClass().getName() + " -- Unexpected exception in task!", e);
/*     */               }
/*     */             }
/*     */           }
/*     */           catch (InterruptedException e) {
/* 179 */             if (CarefulRunnableQueue.this.shutdown_on_interrupt)
/*     */             {
/* 181 */               CarefulRunnableQueue.this.close(false);
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 186 */               if (CarefulRunnableQueue.logger.isLoggable(MLevel.INFO)) {
/* 187 */                 CarefulRunnableQueue.logger.info(toString() + " interrupted. Shutting down after current tasks" + " have completed.");
/*     */ 
/*     */               }
/*     */               
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/*     */ 
/* 196 */               CarefulRunnableQueue.logger.info(toString() + " received interrupt. IGNORING.");
/*     */ 
/*     */ 
/*     */ 
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */       finally
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 222 */         synchronized (CarefulRunnableQueue.this)
/*     */         {
/* 224 */           CarefulRunnableQueue.this.strandedTasks = Collections.unmodifiableList(CarefulRunnableQueue.this.taskList);
/* 225 */           CarefulRunnableQueue.this.taskList = null;
/* 226 */           CarefulRunnableQueue.this.t = null;
/* 227 */           CarefulRunnableQueue.this.notifyAll();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\async\CarefulRunnableQueue.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */