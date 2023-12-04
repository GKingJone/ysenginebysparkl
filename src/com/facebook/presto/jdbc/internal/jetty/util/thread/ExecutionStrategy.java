/*     */ package com.facebook.presto.jdbc.internal.jetty.util.thread;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Loader;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.strategy.ExecuteProduceConsume;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.concurrent.Executor;
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
/*     */ public abstract interface ExecutionStrategy
/*     */ {
/*     */   public abstract void dispatch();
/*     */   
/*     */   public abstract void execute();
/*     */   
/*     */   public static class Factory
/*     */   {
/*  86 */     private static final Logger LOG = Log.getLogger(Factory.class);
/*     */     
/*     */ 
/*     */     public static ExecutionStrategy instanceFor(Producer producer, Executor executor)
/*     */     {
/*  91 */       String strategy = System.getProperty(producer.getClass().getName() + ".ExecutionStrategy");
/*  92 */       if (strategy != null)
/*     */       {
/*     */         try
/*     */         {
/*  96 */           Class<? extends ExecutionStrategy> c = Loader.loadClass(producer.getClass(), strategy);
/*  97 */           Constructor<? extends ExecutionStrategy> m = c.getConstructor(new Class[] { Producer.class, Executor.class });
/*  98 */           LOG.info("Use {} for {}", new Object[] { c.getSimpleName(), producer.getClass().getName() });
/*  99 */           return (ExecutionStrategy)m.newInstance(new Object[] { producer, executor });
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 103 */           LOG.warn(e);
/*     */         }
/*     */       }
/*     */       
/* 107 */       return new ExecuteProduceConsume(producer, executor);
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface Producer
/*     */   {
/*     */     public abstract Runnable produce();
/*     */   }
/*     */   
/*     */   public static abstract interface Rejectable
/*     */   {
/*     */     public abstract void reject();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\thread\ExecutionStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */