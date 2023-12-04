/*     */ package com.mchange.v2.async;
/*     */ 
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import com.mchange.v2.util.ResourceClosedException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Timer;
/*     */ import java.util.TimerTask;
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
/*     */ public class ThreadPerTaskAsynchronousRunner
/*     */   implements AsynchronousRunner
/*     */ {
/*     */   static final int PRESUME_DEADLOCKED_MULTIPLE = 3;
/*  34 */   static final MLogger logger = MLog.getLogger(ThreadPerTaskAsynchronousRunner.class);
/*     */   
/*     */ 
/*     */   final int max_task_threads;
/*     */   
/*     */   final long interrupt_task_delay;
/*     */   
/*  41 */   LinkedList queue = new LinkedList();
/*  42 */   ArrayList running = new ArrayList();
/*  43 */   ArrayList deadlockSnapshot = null;
/*  44 */   boolean still_open = true;
/*     */   
/*     */ 
/*  47 */   Thread dispatchThread = new DispatchThread();
/*     */   Timer interruptAndDeadlockTimer;
/*     */   
/*     */   public ThreadPerTaskAsynchronousRunner(int max_task_threads) {
/*  51 */     this(max_task_threads, 0L);
/*     */   }
/*     */   
/*     */   public ThreadPerTaskAsynchronousRunner(int max_task_threads, long interrupt_task_delay) {
/*  55 */     this.max_task_threads = max_task_threads;
/*  56 */     this.interrupt_task_delay = interrupt_task_delay;
/*  57 */     if (hasIdTimer())
/*     */     {
/*  59 */       this.interruptAndDeadlockTimer = new Timer(true);
/*  60 */       TimerTask deadlockChecker = new TimerTask()
/*     */       {
/*     */ 
/*  63 */         public void run() { ThreadPerTaskAsynchronousRunner.this.checkForDeadlock(); }
/*  64 */       };
/*  65 */       long delay = interrupt_task_delay * 3L;
/*  66 */       this.interruptAndDeadlockTimer.schedule(deadlockChecker, delay, delay);
/*     */     }
/*     */     
/*  69 */     this.dispatchThread.start();
/*     */   }
/*     */   
/*     */   private boolean hasIdTimer() {
/*  73 */     return this.interrupt_task_delay > 0L;
/*     */   }
/*     */   
/*     */   public synchronized void postRunnable(Runnable r) {
/*  77 */     if (this.still_open)
/*     */     {
/*  79 */       this.queue.add(r);
/*  80 */       notifyAll();
/*     */     }
/*     */     else {
/*  83 */       throw new ResourceClosedException("Attempted to use a ThreadPerTaskAsynchronousRunner in a closed or broken state.");
/*     */     }
/*     */   }
/*     */   
/*     */   public void close() {
/*  88 */     close(true);
/*     */   }
/*     */   
/*     */   public synchronized void close(boolean skip_remaining_tasks) {
/*  92 */     if (this.still_open)
/*     */     {
/*  94 */       this.still_open = false;
/*  95 */       if (skip_remaining_tasks)
/*     */       {
/*  97 */         this.queue.clear();
/*  98 */         for (Iterator ii = this.running.iterator(); ii.hasNext();)
/*  99 */           ((Thread)ii.next()).interrupt();
/* 100 */         closeThreadResources();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized int getRunningCount() {
/* 106 */     return this.running.size();
/*     */   }
/*     */   
/* 109 */   public synchronized Collection getRunningTasks() { return (Collection)this.running.clone(); }
/*     */   
/*     */   public synchronized int getWaitingCount() {
/* 112 */     return this.queue.size();
/*     */   }
/*     */   
/* 115 */   public synchronized Collection getWaitingTasks() { return (Collection)this.queue.clone(); }
/*     */   
/*     */   public synchronized boolean isClosed() {
/* 118 */     return !this.still_open;
/*     */   }
/*     */   
/* 121 */   public synchronized boolean isDoneAndGone() { return (!this.dispatchThread.isAlive()) && (this.running.isEmpty()) && (this.interruptAndDeadlockTimer == null); }
/*     */   
/*     */   private synchronized void acknowledgeComplete(TaskThread tt)
/*     */   {
/* 125 */     if (!tt.isCompleted())
/*     */     {
/* 127 */       this.running.remove(tt);
/* 128 */       tt.markCompleted();
/* 129 */       notifyAll();
/*     */       
/* 131 */       if ((!this.still_open) && (this.queue.isEmpty()) && (this.running.isEmpty())) {
/* 132 */         closeThreadResources();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private synchronized void checkForDeadlock() {
/* 138 */     if (this.deadlockSnapshot == null)
/*     */     {
/* 140 */       if (this.running.size() == this.max_task_threads) {
/* 141 */         this.deadlockSnapshot = ((ArrayList)this.running.clone());
/*     */       }
/* 143 */     } else if (this.running.size() < this.max_task_threads) {
/* 144 */       this.deadlockSnapshot = null;
/* 145 */     } else if (this.deadlockSnapshot.equals(this.running))
/*     */     {
/* 147 */       if (logger.isLoggable(MLevel.WARNING))
/*     */       {
/* 149 */         StringBuffer warningMsg = new StringBuffer(1024);
/* 150 */         warningMsg.append("APPARENT DEADLOCK! (");
/* 151 */         warningMsg.append(this);
/* 152 */         warningMsg.append(") Deadlocked threads (unresponsive to interrupt()) are being set aside as hopeless and up to ");
/* 153 */         warningMsg.append(this.max_task_threads);
/* 154 */         warningMsg.append(" may now be spawned for new tasks. If tasks continue to deadlock, you may run out of memory. Deadlocked task list: ");
/* 155 */         int i = 0; for (int len = this.deadlockSnapshot.size(); i < len; i++)
/*     */         {
/* 157 */           if (i != 0) warningMsg.append(", ");
/* 158 */           warningMsg.append(((TaskThread)this.deadlockSnapshot.get(i)).getTask());
/*     */         }
/*     */         
/* 161 */         logger.log(MLevel.WARNING, warningMsg.toString());
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 166 */       int i = 0; for (int len = this.deadlockSnapshot.size(); i < len; i++)
/* 167 */         acknowledgeComplete((TaskThread)this.deadlockSnapshot.get(i));
/* 168 */       this.deadlockSnapshot = null;
/*     */     }
/*     */     else {
/* 171 */       this.deadlockSnapshot = ((ArrayList)this.running.clone());
/*     */     }
/*     */   }
/*     */   
/*     */   private void closeThreadResources() {
/* 176 */     if (this.interruptAndDeadlockTimer != null)
/*     */     {
/* 178 */       this.interruptAndDeadlockTimer.cancel();
/* 179 */       this.interruptAndDeadlockTimer = null;
/*     */     }
/* 181 */     this.dispatchThread.interrupt();
/*     */   }
/*     */   
/*     */   class DispatchThread extends Thread
/*     */   {
/*     */     DispatchThread() {
/* 187 */       super();
/*     */     }
/*     */     
/*     */     public void run() {
/* 191 */       synchronized (ThreadPerTaskAsynchronousRunner.this)
/*     */       {
/*     */         try
/*     */         {
/*     */           for (;;)
/*     */           {
/* 197 */             if ((ThreadPerTaskAsynchronousRunner.this.queue.isEmpty()) || (ThreadPerTaskAsynchronousRunner.this.running.size() == ThreadPerTaskAsynchronousRunner.this.max_task_threads)) {
/* 198 */               ThreadPerTaskAsynchronousRunner.this.wait();
/*     */             } else {
/* 200 */               Runnable next = (Runnable)ThreadPerTaskAsynchronousRunner.this.queue.remove(0);
/* 201 */               TaskThread doer = new TaskThread(ThreadPerTaskAsynchronousRunner.this, next);
/* 202 */               doer.start();
/* 203 */               ThreadPerTaskAsynchronousRunner.this.running.add(doer);
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (InterruptedException e) {
/* 208 */           if (ThreadPerTaskAsynchronousRunner.this.still_open)
/*     */           {
/* 210 */             if (ThreadPerTaskAsynchronousRunner.logger.isLoggable(MLevel.WARNING))
/* 211 */               ThreadPerTaskAsynchronousRunner.logger.log(MLevel.WARNING, getName() + " unexpectedly interrupted! Shutting down!");
/* 212 */             ThreadPerTaskAsynchronousRunner.this.close(false);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   class TaskThread
/*     */     extends Thread
/*     */   {
/*     */     Runnable r;
/*     */     
/* 225 */     boolean completed = false;
/*     */     
/*     */     TaskThread(Runnable r)
/*     */     {
/* 229 */       super();
/* 230 */       this.r = r;
/*     */     }
/*     */     
/*     */     Runnable getTask() {
/* 234 */       return this.r;
/*     */     }
/*     */     
/* 237 */     synchronized void markCompleted() { this.completed = true; }
/*     */     
/*     */     synchronized boolean isCompleted() {
/* 240 */       return this.completed;
/*     */     }
/*     */     
/*     */     public void run()
/*     */     {
/*     */       try {
/* 246 */         if (ThreadPerTaskAsynchronousRunner.this.hasIdTimer())
/*     */         {
/* 248 */           TimerTask interruptTask = new TimerTask()
/*     */           {
/*     */ 
/* 251 */             public void run() { TaskThread.this.interrupt(); }
/* 252 */           };
/* 253 */           ThreadPerTaskAsynchronousRunner.this.interruptAndDeadlockTimer.schedule(interruptTask, ThreadPerTaskAsynchronousRunner.this.interrupt_task_delay);
/*     */         }
/* 255 */         this.r.run();
/*     */       }
/*     */       finally {
/* 258 */         ThreadPerTaskAsynchronousRunner.this.acknowledgeComplete(this);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\async\ThreadPerTaskAsynchronousRunner.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */