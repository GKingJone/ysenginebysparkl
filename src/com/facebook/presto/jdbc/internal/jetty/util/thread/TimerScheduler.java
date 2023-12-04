/*     */ package com.facebook.presto.jdbc.internal.jetty.util.thread;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.AbstractLifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.util.Timer;
/*     */ import java.util.TimerTask;
/*     */ import java.util.concurrent.RejectedExecutionException;
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
/*     */ public class TimerScheduler
/*     */   extends AbstractLifeCycle
/*     */   implements Scheduler, Runnable
/*     */ {
/*  36 */   private static final Logger LOG = Log.getLogger(TimerScheduler.class);
/*     */   
/*     */ 
/*     */   private final String _name;
/*     */   
/*     */ 
/*     */   private final boolean _daemon;
/*     */   
/*     */ 
/*     */   private Timer _timer;
/*     */   
/*     */ 
/*     */ 
/*     */   public TimerScheduler()
/*     */   {
/*  51 */     this(null, false);
/*     */   }
/*     */   
/*     */   public TimerScheduler(String name, boolean daemon)
/*     */   {
/*  56 */     this._name = name;
/*  57 */     this._daemon = daemon;
/*     */   }
/*     */   
/*     */   protected void doStart()
/*     */     throws Exception
/*     */   {
/*  63 */     this._timer = (this._name == null ? new Timer() : new Timer(this._name, this._daemon));
/*  64 */     run();
/*  65 */     super.doStart();
/*     */   }
/*     */   
/*     */   protected void doStop()
/*     */     throws Exception
/*     */   {
/*  71 */     this._timer.cancel();
/*  72 */     super.doStop();
/*  73 */     this._timer = null;
/*     */   }
/*     */   
/*     */ 
/*     */   public Task schedule(Runnable task, long delay, TimeUnit units)
/*     */   {
/*  79 */     Timer timer = this._timer;
/*  80 */     if (timer == null)
/*  81 */       throw new RejectedExecutionException("STOPPED: " + this);
/*  82 */     SimpleTask t = new SimpleTask(task, null);
/*  83 */     timer.schedule(t, units.toMillis(delay));
/*  84 */     return t;
/*     */   }
/*     */   
/*     */ 
/*     */   public void run()
/*     */   {
/*  90 */     Timer timer = this._timer;
/*  91 */     if (timer != null)
/*     */     {
/*  93 */       timer.purge();
/*  94 */       schedule(this, 1L, TimeUnit.SECONDS);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class SimpleTask extends TimerTask implements Task
/*     */   {
/*     */     private final Runnable _task;
/*     */     
/*     */     private SimpleTask(Runnable runnable)
/*     */     {
/* 104 */       this._task = runnable;
/*     */     }
/*     */     
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       try
/*     */       {
/* 112 */         this._task.run();
/*     */       }
/*     */       catch (Throwable x)
/*     */       {
/* 116 */         TimerScheduler.LOG.warn("Exception while executing task " + this._task, x);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 123 */       return String.format("%s.%s@%x", new Object[] {TimerScheduler.class
/* 124 */         .getSimpleName(), SimpleTask.class
/* 125 */         .getSimpleName(), 
/* 126 */         Integer.valueOf(hashCode()) });
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\thread\TimerScheduler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */