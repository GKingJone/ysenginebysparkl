/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Supplier;
/*     */ import java.util.concurrent.Callable;
/*     */ import javax.annotation.Nullable;
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
/*     */ public final class Callables
/*     */ {
/*     */   public static <T> Callable<T> returning(@Nullable T value)
/*     */   {
/*  41 */     new Callable() {
/*     */       public T call() {
/*  43 */         return (T)this.val$value;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static <T> Callable<T> threadRenaming(final Callable<T> callable, Supplier<String> nameSupplier)
/*     */   {
/*  58 */     Preconditions.checkNotNull(nameSupplier);
/*  59 */     Preconditions.checkNotNull(callable);
/*  60 */     new Callable() {
/*     */       public T call() throws Exception {
/*  62 */         Thread currentThread = Thread.currentThread();
/*  63 */         String oldName = currentThread.getName();
/*  64 */         boolean restoreName = Callables.trySetName((String)this.val$nameSupplier.get(), currentThread);
/*     */         try {
/*  66 */           return (T)callable.call();
/*     */         } finally {
/*  68 */           if (restoreName) {
/*  69 */             Callables.trySetName(oldName, currentThread);
/*     */           }
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
/*     */ 
/*     */ 
/*     */   static Runnable threadRenaming(final Runnable task, Supplier<String> nameSupplier)
/*     */   {
/*  85 */     Preconditions.checkNotNull(nameSupplier);
/*  86 */     Preconditions.checkNotNull(task);
/*  87 */     new Runnable() {
/*     */       public void run() {
/*  89 */         Thread currentThread = Thread.currentThread();
/*  90 */         String oldName = currentThread.getName();
/*  91 */         boolean restoreName = Callables.trySetName((String)this.val$nameSupplier.get(), currentThread);
/*     */         try {
/*  93 */           task.run();
/*     */         } finally {
/*  95 */           if (restoreName) {
/*  96 */             Callables.trySetName(oldName, currentThread);
/*     */           }
/*     */         }
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static boolean trySetName(String threadName, Thread currentThread)
/*     */   {
/*     */     try
/*     */     {
/* 109 */       currentThread.setName(threadName);
/* 110 */       return true;
/*     */     } catch (SecurityException e) {}
/* 112 */     return false;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\util\concurrent\Callables.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */