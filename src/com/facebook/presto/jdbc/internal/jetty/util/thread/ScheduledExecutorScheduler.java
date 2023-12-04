/*     */ package com.facebook.presto.jdbc.internal.jetty.util.thread;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.AbstractLifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.ContainerLifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.Dumpable;
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import java.util.concurrent.ScheduledThreadPoolExecutor;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.TimeUnit;
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
/*     */ public class ScheduledExecutorScheduler
/*     */   extends AbstractLifeCycle
/*     */   implements Scheduler, Dumpable
/*     */ {
/*     */   private final String name;
/*     */   private final boolean daemon;
/*     */   private final ClassLoader classloader;
/*     */   private final ThreadGroup threadGroup;
/*     */   private volatile ScheduledThreadPoolExecutor scheduler;
/*     */   private volatile Thread thread;
/*     */   
/*     */   public ScheduledExecutorScheduler()
/*     */   {
/*  52 */     this(null, false);
/*     */   }
/*     */   
/*     */   public ScheduledExecutorScheduler(String name, boolean daemon)
/*     */   {
/*  57 */     this(name, daemon, Thread.currentThread().getContextClassLoader());
/*     */   }
/*     */   
/*     */   public ScheduledExecutorScheduler(String name, boolean daemon, ClassLoader threadFactoryClassLoader)
/*     */   {
/*  62 */     this(name, daemon, threadFactoryClassLoader, null);
/*     */   }
/*     */   
/*     */   public ScheduledExecutorScheduler(String name, boolean daemon, ClassLoader threadFactoryClassLoader, ThreadGroup threadGroup)
/*     */   {
/*  67 */     this.name = (name == null ? "Scheduler-" + hashCode() : name);
/*  68 */     this.daemon = daemon;
/*  69 */     this.classloader = (threadFactoryClassLoader == null ? Thread.currentThread().getContextClassLoader() : threadFactoryClassLoader);
/*  70 */     this.threadGroup = threadGroup;
/*     */   }
/*     */   
/*     */   protected void doStart()
/*     */     throws Exception
/*     */   {
/*  76 */     this.scheduler = new ScheduledThreadPoolExecutor(1, new ThreadFactory()
/*     */     {
/*     */ 
/*     */       public Thread newThread(Runnable r)
/*     */       {
/*  81 */         Thread thread = ScheduledExecutorScheduler.this.thread = new Thread(ScheduledExecutorScheduler.this.threadGroup, r, ScheduledExecutorScheduler.this.name);
/*  82 */         thread.setDaemon(ScheduledExecutorScheduler.this.daemon);
/*  83 */         thread.setContextClassLoader(ScheduledExecutorScheduler.this.classloader);
/*  84 */         return thread;
/*     */       }
/*  86 */     });
/*  87 */     this.scheduler.setRemoveOnCancelPolicy(true);
/*  88 */     super.doStart();
/*     */   }
/*     */   
/*     */   protected void doStop()
/*     */     throws Exception
/*     */   {
/*  94 */     this.scheduler.shutdownNow();
/*  95 */     super.doStop();
/*  96 */     this.scheduler = null;
/*     */   }
/*     */   
/*     */ 
/*     */   public Task schedule(Runnable task, long delay, TimeUnit unit)
/*     */   {
/* 102 */     ScheduledThreadPoolExecutor s = this.scheduler;
/* 103 */     if (s == null)
/* 104 */       new Task()
/*     */       {
/*     */         public boolean cancel()
/*     */         {
/* 108 */           return false;
/*     */         }
/*     */       };
/* 111 */     ScheduledFuture<?> result = s.schedule(task, delay, unit);
/* 112 */     return new ScheduledFutureTask(result);
/*     */   }
/*     */   
/*     */ 
/*     */   public String dump()
/*     */   {
/* 118 */     return ContainerLifeCycle.dump(this);
/*     */   }
/*     */   
/*     */   public void dump(Appendable out, String indent)
/*     */     throws IOException
/*     */   {
/* 124 */     ContainerLifeCycle.dumpObject(out, this);
/* 125 */     Thread thread = this.thread;
/* 126 */     if (thread != null)
/*     */     {
/* 128 */       List<StackTraceElement> frames = Arrays.asList(thread.getStackTrace());
/* 129 */       ContainerLifeCycle.dump(out, indent, new Collection[] { frames });
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ScheduledFutureTask implements Task
/*     */   {
/*     */     private final ScheduledFuture<?> scheduledFuture;
/*     */     
/*     */     ScheduledFutureTask(ScheduledFuture<?> scheduledFuture)
/*     */     {
/* 139 */       this.scheduledFuture = scheduledFuture;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean cancel()
/*     */     {
/* 145 */       return this.scheduledFuture.cancel(false);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\thread\ScheduledExecutorScheduler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */