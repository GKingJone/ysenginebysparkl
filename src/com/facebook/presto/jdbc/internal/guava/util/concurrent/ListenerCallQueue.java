/*     */ package com.facebook.presto.jdbc.internal.guava.util.concurrent;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Queues;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.annotation.concurrent.GuardedBy;
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
/*     */ final class ListenerCallQueue<L>
/*     */   implements Runnable
/*     */ {
/*  40 */   private static final Logger logger = Logger.getLogger(ListenerCallQueue.class.getName());
/*     */   private final L listener;
/*     */   private final Executor executor;
/*     */   
/*     */   static abstract class Callback<L> { private final String methodCall;
/*     */     
/*  46 */     Callback(String methodCall) { this.methodCall = methodCall; }
/*     */     
/*     */ 
/*     */     abstract void call(L paramL);
/*     */     
/*     */     void enqueueOn(Iterable<ListenerCallQueue<L>> queues)
/*     */     {
/*  53 */       for (ListenerCallQueue<L> queue : queues) {
/*  54 */         queue.add(this);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @GuardedBy("this")
/*  62 */   private final Queue<Callback<L>> waitQueue = Queues.newArrayDeque();
/*     */   @GuardedBy("this")
/*     */   private boolean isThreadScheduled;
/*     */   
/*  66 */   ListenerCallQueue(L listener, Executor executor) { this.listener = Preconditions.checkNotNull(listener);
/*  67 */     this.executor = ((Executor)Preconditions.checkNotNull(executor));
/*     */   }
/*     */   
/*     */   synchronized void add(Callback<L> callback)
/*     */   {
/*  72 */     this.waitQueue.add(callback);
/*     */   }
/*     */   
/*     */   void execute()
/*     */   {
/*  77 */     boolean scheduleTaskRunner = false;
/*  78 */     synchronized (this) {
/*  79 */       if (!this.isThreadScheduled) {
/*  80 */         this.isThreadScheduled = true;
/*  81 */         scheduleTaskRunner = true;
/*     */       }
/*     */     }
/*  84 */     if (scheduleTaskRunner) {
/*     */       try {
/*  86 */         this.executor.execute(this);
/*     */       }
/*     */       catch (RuntimeException e) {
/*  89 */         synchronized (this) {
/*  90 */           this.isThreadScheduled = false;
/*     */         }
/*     */         
/*  93 */         ??? = String.valueOf(String.valueOf(this.listener));String str = String.valueOf(String.valueOf(this.executor));logger.log(Level.SEVERE, 42 + ((String)???).length() + str.length() + "Exception while running callbacks for " + (String)??? + " on " + str, e);
/*     */         
/*     */ 
/*  96 */         throw e;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void run() {
/* 102 */     boolean stillRunning = true;
/*     */     try {
/*     */       for (;;) {
/*     */         Callback<L> nextToRun;
/* 106 */         synchronized (this) {
/* 107 */           Preconditions.checkState(this.isThreadScheduled);
/* 108 */           nextToRun = (Callback)this.waitQueue.poll();
/* 109 */           if (nextToRun == null) {
/* 110 */             this.isThreadScheduled = false;
/* 111 */             stillRunning = false;
/* 112 */             break;
/*     */           }
/*     */         }
/*     */         
/*     */         try
/*     */         {
/* 118 */           nextToRun.call(this.listener);
/*     */         }
/*     */         catch (RuntimeException e) {
/* 121 */           String str1 = String.valueOf(String.valueOf(this.listener));String str2 = String.valueOf(String.valueOf(nextToRun.methodCall));logger.log(Level.SEVERE, 37 + str1.length() + str2.length() + "Exception while executing callback: " + str1 + "." + str2, e);
/*     */         }
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 127 */       if (stillRunning)
/*     */       {
/*     */ 
/*     */ 
/* 131 */         synchronized (this) {
/* 132 */           this.isThreadScheduled = false;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\util\concurrent\ListenerCallQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */