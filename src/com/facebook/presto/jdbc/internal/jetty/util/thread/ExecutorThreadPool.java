/*     */ package com.facebook.presto.jdbc.internal.jetty.util.thread;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.AbstractLifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.LifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.util.concurrent.ArrayBlockingQueue;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.RejectedExecutionException;
/*     */ import java.util.concurrent.SynchronousQueue;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
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
/*     */ public class ExecutorThreadPool
/*     */   extends AbstractLifeCycle
/*     */   implements ThreadPool, LifeCycle
/*     */ {
/*  43 */   private static final Logger LOG = Log.getLogger(ExecutorThreadPool.class);
/*     */   
/*     */   private final ExecutorService _executor;
/*     */   
/*     */   public ExecutorThreadPool(ExecutorService executor)
/*     */   {
/*  49 */     this._executor = executor;
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
/*     */   public ExecutorThreadPool()
/*     */   {
/*  62 */     this(new ThreadPoolExecutor(256, 256, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ExecutorThreadPool(int queueSize)
/*     */   {
/*  74 */     this(queueSize == 0 ? new ThreadPoolExecutor(32, 256, 60L, TimeUnit.SECONDS, new SynchronousQueue()) : queueSize < 0 ? new ThreadPoolExecutor(256, 256, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue()) : new ThreadPoolExecutor(32, 256, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue(queueSize)));
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
/*     */ 
/*     */ 
/*     */   public ExecutorThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime)
/*     */   {
/*  89 */     this(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS);
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
/*     */ 
/*     */   public ExecutorThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit)
/*     */   {
/* 103 */     this(corePoolSize, maximumPoolSize, keepAliveTime, unit, new LinkedBlockingQueue());
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
/*     */ 
/*     */ 
/*     */   public ExecutorThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue)
/*     */   {
/* 118 */     this(new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void execute(Runnable job)
/*     */   {
/* 126 */     this._executor.execute(job);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean dispatch(Runnable job)
/*     */   {
/*     */     try
/*     */     {
/* 134 */       this._executor.execute(job);
/* 135 */       return true;
/*     */     }
/*     */     catch (RejectedExecutionException e)
/*     */     {
/* 139 */       LOG.warn(e); }
/* 140 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getIdleThreads()
/*     */   {
/* 147 */     if ((this._executor instanceof ThreadPoolExecutor))
/*     */     {
/* 149 */       ThreadPoolExecutor tpe = (ThreadPoolExecutor)this._executor;
/* 150 */       return tpe.getPoolSize() - tpe.getActiveCount();
/*     */     }
/* 152 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getThreads()
/*     */   {
/* 158 */     if ((this._executor instanceof ThreadPoolExecutor))
/*     */     {
/* 160 */       ThreadPoolExecutor tpe = (ThreadPoolExecutor)this._executor;
/* 161 */       return tpe.getPoolSize();
/*     */     }
/* 163 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isLowOnThreads()
/*     */   {
/* 169 */     if ((this._executor instanceof ThreadPoolExecutor))
/*     */     {
/* 171 */       ThreadPoolExecutor tpe = (ThreadPoolExecutor)this._executor;
/*     */       
/*     */ 
/* 174 */       return (tpe.getPoolSize() == tpe.getMaximumPoolSize()) && (tpe.getQueue().size() >= tpe.getPoolSize() - tpe.getActiveCount());
/*     */     }
/* 176 */     return false;
/*     */   }
/*     */   
/*     */   public void join()
/*     */     throws InterruptedException
/*     */   {
/* 182 */     this._executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void doStop()
/*     */     throws Exception
/*     */   {
/* 189 */     super.doStop();
/* 190 */     this._executor.shutdownNow();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\thread\ExecutorThreadPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */