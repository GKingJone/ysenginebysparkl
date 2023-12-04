/*    */ package com.yammer.metrics.core;
/*    */ 
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ import java.util.concurrent.ConcurrentMap;
/*    */ import java.util.concurrent.ExecutorService;
/*    */ import java.util.concurrent.Executors;
/*    */ import java.util.concurrent.ScheduledExecutorService;
/*    */ import java.util.concurrent.atomic.AtomicInteger;
/*    */ 
/*    */ class ThreadPools
/*    */ {
/*    */   private static class NamedThreadFactory implements java.util.concurrent.ThreadFactory
/*    */   {
/*    */     private final ThreadGroup group;
/* 15 */     private final AtomicInteger threadNumber = new AtomicInteger(1);
/*    */     
/*    */ 
/*    */ 
/*    */     private final String namePrefix;
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */     NamedThreadFactory(String name)
/*    */     {
/* 26 */       SecurityManager s = System.getSecurityManager();
/* 27 */       this.group = (s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup());
/* 28 */       this.namePrefix = ("metrics-" + name + "-thread-");
/*    */     }
/*    */     
/*    */     public Thread newThread(Runnable r)
/*    */     {
/* 33 */       Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
/* 34 */       t.setDaemon(true);
/* 35 */       if (t.getPriority() != 5) {
/* 36 */         t.setPriority(5);
/*    */       }
/* 38 */       return t;
/*    */     }
/*    */   }
/*    */   
/* 42 */   private final ConcurrentMap<String, ScheduledExecutorService> threadPools = new ConcurrentHashMap(100);
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   ScheduledExecutorService newScheduledThreadPool(int poolSize, String name)
/*    */   {
/* 54 */     ScheduledExecutorService existing = (ScheduledExecutorService)this.threadPools.get(name);
/* 55 */     if (isValidExecutor(existing)) {
/* 56 */       return existing;
/*    */     }
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 62 */     synchronized (this) {
/* 63 */       ScheduledExecutorService lastChance = (ScheduledExecutorService)this.threadPools.get(name);
/* 64 */       if (isValidExecutor(lastChance)) {
/* 65 */         return lastChance;
/*    */       }
/* 67 */       ScheduledExecutorService service = Executors.newScheduledThreadPool(poolSize, new NamedThreadFactory(name));
/*    */       
/* 69 */       this.threadPools.put(name, service);
/* 70 */       return service;
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   private static boolean isValidExecutor(ExecutorService executor)
/*    */   {
/* 77 */     return (executor != null) && (!executor.isShutdown()) && (!executor.isTerminated());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   void shutdown()
/*    */   {
/* 84 */     synchronized (this) {
/* 85 */       for (ExecutorService executor : this.threadPools.values()) {
/* 86 */         executor.shutdown();
/*    */       }
/* 88 */       this.threadPools.clear();
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\core\ThreadPools.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */