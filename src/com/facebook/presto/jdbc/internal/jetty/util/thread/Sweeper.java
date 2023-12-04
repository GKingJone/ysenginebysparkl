/*     */ package com.facebook.presto.jdbc.internal.jetty.util.thread;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.AbstractLifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicReference;
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
/*     */ public class Sweeper
/*     */   extends AbstractLifeCycle
/*     */   implements Runnable
/*     */ {
/*  75 */   private static final Logger LOG = Log.getLogger(Sweeper.class);
/*     */   
/*  77 */   private final AtomicReference<List<Sweepable>> items = new AtomicReference();
/*  78 */   private final AtomicReference<Scheduler.Task> task = new AtomicReference();
/*     */   private final Scheduler scheduler;
/*     */   private final long period;
/*     */   
/*     */   public Sweeper(Scheduler scheduler, long period)
/*     */   {
/*  84 */     this.scheduler = scheduler;
/*  85 */     this.period = period;
/*     */   }
/*     */   
/*     */   protected void doStart()
/*     */     throws Exception
/*     */   {
/*  91 */     super.doStart();
/*  92 */     this.items.set(new CopyOnWriteArrayList());
/*  93 */     activate();
/*     */   }
/*     */   
/*     */   protected void doStop()
/*     */     throws Exception
/*     */   {
/*  99 */     deactivate();
/* 100 */     this.items.set(null);
/* 101 */     super.doStop();
/*     */   }
/*     */   
/*     */   public int getSize()
/*     */   {
/* 106 */     List<Sweepable> refs = (List)this.items.get();
/* 107 */     return refs == null ? 0 : refs.size();
/*     */   }
/*     */   
/*     */   public boolean offer(Sweepable sweepable)
/*     */   {
/* 112 */     List<Sweepable> refs = (List)this.items.get();
/* 113 */     if (refs == null)
/* 114 */       return false;
/* 115 */     refs.add(sweepable);
/* 116 */     if (LOG.isDebugEnabled())
/* 117 */       LOG.debug("Resource offered {}", new Object[] { sweepable });
/* 118 */     return true;
/*     */   }
/*     */   
/*     */   public boolean remove(Sweepable sweepable)
/*     */   {
/* 123 */     List<Sweepable> refs = (List)this.items.get();
/* 124 */     return (refs != null) && (refs.remove(sweepable));
/*     */   }
/*     */   
/*     */ 
/*     */   public void run()
/*     */   {
/* 130 */     List<Sweepable> refs = (List)this.items.get();
/* 131 */     if (refs == null)
/* 132 */       return;
/* 133 */     for (Sweepable sweepable : refs)
/*     */     {
/*     */       try
/*     */       {
/* 137 */         if (sweepable.sweep())
/*     */         {
/* 139 */           refs.remove(sweepable);
/* 140 */           if (LOG.isDebugEnabled()) {
/* 141 */             LOG.debug("Resource swept {}", new Object[] { sweepable });
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable x) {
/* 146 */         LOG.info("Exception while sweeping " + sweepable, x);
/*     */       }
/*     */     }
/* 149 */     activate();
/*     */   }
/*     */   
/*     */   private void activate()
/*     */   {
/* 154 */     if (isRunning())
/*     */     {
/* 156 */       Scheduler.Task t = this.scheduler.schedule(this, this.period, TimeUnit.MILLISECONDS);
/* 157 */       if (LOG.isDebugEnabled())
/* 158 */         LOG.debug("Scheduled in {} ms sweep task {}", new Object[] { Long.valueOf(this.period), t });
/* 159 */       this.task.set(t);
/*     */ 
/*     */ 
/*     */     }
/* 163 */     else if (LOG.isDebugEnabled()) {
/* 164 */       LOG.debug("Skipping sweep task scheduling", new Object[0]);
/*     */     }
/*     */   }
/*     */   
/*     */   private void deactivate()
/*     */   {
/* 170 */     Scheduler.Task t = (Scheduler.Task)this.task.getAndSet(null);
/* 171 */     if (t != null)
/*     */     {
/* 173 */       boolean cancelled = t.cancel();
/* 174 */       if (LOG.isDebugEnabled()) {
/* 175 */         LOG.debug("Cancelled ({}) sweep task {}", new Object[] { Boolean.valueOf(cancelled), t });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface Sweepable
/*     */   {
/*     */     public abstract boolean sweep();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\thread\Sweeper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */