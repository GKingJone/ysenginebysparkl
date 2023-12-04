/*     */ package com.facebook.presto.jdbc.internal.guava.util.concurrent;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.VisibleForTesting;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.annotation.Nullable;
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
/*     */ 
/*     */ 
/*     */ public final class ExecutionList
/*     */ {
/*     */   @VisibleForTesting
/*  49 */   static final Logger log = Logger.getLogger(ExecutionList.class.getName());
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
/*     */   @GuardedBy("this")
/*     */   private RunnableExecutorPair runnables;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @GuardedBy("this")
/*     */   private boolean executed;
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
/*     */   public void add(Runnable runnable, Executor executor)
/*     */   {
/*  85 */     Preconditions.checkNotNull(runnable, "Runnable was null.");
/*  86 */     Preconditions.checkNotNull(executor, "Executor was null.");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  91 */     synchronized (this) {
/*  92 */       if (!this.executed) {
/*  93 */         this.runnables = new RunnableExecutorPair(runnable, executor, this.runnables);
/*  94 */         return;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 101 */     executeListener(runnable, executor);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void execute()
/*     */   {
/*     */     RunnableExecutorPair list;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 120 */     synchronized (this) {
/* 121 */       if (this.executed) {
/* 122 */         return;
/*     */       }
/* 124 */       this.executed = true;
/* 125 */       list = this.runnables;
/* 126 */       this.runnables = null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 137 */     RunnableExecutorPair reversedList = null;
/* 138 */     while (list != null) {
/* 139 */       RunnableExecutorPair tmp = list;
/* 140 */       list = list.next;
/* 141 */       tmp.next = reversedList;
/* 142 */       reversedList = tmp;
/*     */     }
/* 144 */     while (reversedList != null) {
/* 145 */       executeListener(reversedList.runnable, reversedList.executor);
/* 146 */       reversedList = reversedList.next;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static void executeListener(Runnable runnable, Executor executor)
/*     */   {
/*     */     try
/*     */     {
/* 156 */       executor.execute(runnable);
/*     */ 
/*     */     }
/*     */     catch (RuntimeException e)
/*     */     {
/* 161 */       String str1 = String.valueOf(String.valueOf(runnable));String str2 = String.valueOf(String.valueOf(executor));log.log(Level.SEVERE, 57 + str1.length() + str2.length() + "RuntimeException while executing runnable " + str1 + " with executor " + str2, e);
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class RunnableExecutorPair {
/*     */     final Runnable runnable;
/*     */     final Executor executor;
/*     */     @Nullable
/*     */     RunnableExecutorPair next;
/*     */     
/*     */     RunnableExecutorPair(Runnable runnable, Executor executor, RunnableExecutorPair next) {
/* 172 */       this.runnable = runnable;
/* 173 */       this.executor = executor;
/* 174 */       this.next = next;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\util\concurrent\ExecutionList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */