/*     */ package com.facebook.presto.jdbc.internal.guava.util.concurrent;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import java.util.ArrayDeque;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class SerializingExecutor
/*     */   implements Executor
/*     */ {
/*  47 */   private static final Logger log = Logger.getLogger(SerializingExecutor.class.getName());
/*     */   
/*     */ 
/*     */   private final Executor executor;
/*     */   
/*     */ 
/*     */   @GuardedBy("internalLock")
/*  54 */   private final Queue<Runnable> waitQueue = new ArrayDeque();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @GuardedBy("internalLock")
/*  65 */   private boolean isThreadScheduled = false;
/*     */   
/*     */ 
/*     */ 
/*  69 */   private final TaskRunner taskRunner = new TaskRunner(null);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SerializingExecutor(Executor executor)
/*     */   {
/*  77 */     Preconditions.checkNotNull(executor, "'executor' must not be null.");
/*  78 */     this.executor = executor;
/*     */   }
/*     */   
/*  81 */   private final Object internalLock = new Object()
/*     */   {
/*     */     /* Error */
/*     */     public String toString()
/*     */     {
/*     */       // Byte code:
/*     */       //   0: ldc 22
/*     */       //   2: aload_0
/*     */       //   3: invokespecial 24	java/lang/Object:toString	()Ljava/lang/String;
/*     */       //   6: invokestatic 30	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
/*     */       //   9: dup
/*     */       //   10: invokevirtual 34	java/lang/String:length	()I
/*     */       //   13: ifeq +9 -> 22
/*     */       //   16: invokevirtual 38	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
/*     */       //   19: goto +12 -> 31
/*     */       //   22: pop
/*     */       //   23: new 26	java/lang/String
/*     */       //   26: dup_x1
/*     */       //   27: swap
/*     */       //   28: invokespecial 41	java/lang/String:<init>	(Ljava/lang/String;)V
/*     */       //   31: areturn
/*     */       // Line number table:
/*     */       //   Java source line #83	-> byte code offset #0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	32	0	this	1
/*     */     }
/*     */   };
/*     */   
/*     */   public void execute(Runnable r)
/*     */   {
/*  93 */     Preconditions.checkNotNull(r, "'r' must not be null.");
/*  94 */     boolean scheduleTaskRunner = false;
/*  95 */     synchronized (this.internalLock) {
/*  96 */       this.waitQueue.add(r);
/*     */       
/*  98 */       if (!this.isThreadScheduled) {
/*  99 */         this.isThreadScheduled = true;
/* 100 */         scheduleTaskRunner = true;
/*     */       }
/*     */     }
/* 103 */     if (scheduleTaskRunner) {
/* 104 */       boolean threw = true;
/*     */       try {
/* 106 */         this.executor.execute(this.taskRunner);
/* 107 */         threw = false;
/*     */       } finally {
/* 109 */         if (threw) {
/* 110 */           synchronized (this.internalLock)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 115 */             this.isThreadScheduled = false;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private class TaskRunner
/*     */     implements Runnable
/*     */   {
/*     */     private TaskRunner() {}
/*     */     
/*     */ 
/*     */     public void run()
/*     */     {
/* 132 */       boolean stillRunning = true;
/*     */       try {
/*     */         for (;;) {
/* 135 */           Preconditions.checkState(SerializingExecutor.this.isThreadScheduled);
/*     */           Runnable nextToRun;
/* 137 */           synchronized (SerializingExecutor.this.internalLock) {
/* 138 */             nextToRun = (Runnable)SerializingExecutor.this.waitQueue.poll();
/* 139 */             if (nextToRun == null) {
/* 140 */               SerializingExecutor.this.isThreadScheduled = false;
/* 141 */               stillRunning = false;
/* 142 */               break;
/*     */             }
/*     */           }
/*     */           
/*     */           try
/*     */           {
/* 148 */             nextToRun.run();
/*     */           }
/*     */           catch (RuntimeException e) {
/* 151 */             String str = String.valueOf(String.valueOf(nextToRun));SerializingExecutor.log.log(Level.SEVERE, 35 + str.length() + "Exception while executing runnable " + str, e);
/*     */           }
/*     */         }
/*     */       }
/*     */       finally {
/* 156 */         if (stillRunning)
/*     */         {
/*     */ 
/*     */ 
/* 160 */           synchronized (SerializingExecutor.this.internalLock) {
/* 161 */             SerializingExecutor.this.isThreadScheduled = false;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\util\concurrent\SerializingExecutor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */