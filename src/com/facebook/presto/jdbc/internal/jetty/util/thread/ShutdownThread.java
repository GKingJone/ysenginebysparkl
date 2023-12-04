/*     */ package com.facebook.presto.jdbc.internal.jetty.util.thread;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.Destroyable;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.LifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
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
/*     */ public class ShutdownThread
/*     */   extends Thread
/*     */ {
/*  40 */   private static final Logger LOG = Log.getLogger(ShutdownThread.class);
/*  41 */   private static final ShutdownThread _thread = new ShutdownThread();
/*     */   
/*     */   private boolean _hooked;
/*  44 */   private final List<LifeCycle> _lifeCycles = new CopyOnWriteArrayList();
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
/*     */   private synchronized void hook()
/*     */   {
/*     */     try
/*     */     {
/*  61 */       if (!this._hooked)
/*  62 */         Runtime.getRuntime().addShutdownHook(this);
/*  63 */       this._hooked = true;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  67 */       LOG.ignore(e);
/*  68 */       LOG.info("shutdown already commenced", new Object[0]);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private synchronized void unhook()
/*     */   {
/*     */     try
/*     */     {
/*  77 */       this._hooked = false;
/*  78 */       Runtime.getRuntime().removeShutdownHook(this);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  82 */       LOG.ignore(e);
/*  83 */       LOG.debug("shutdown already commenced", new Object[0]);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ShutdownThread getInstance()
/*     */   {
/*  95 */     return _thread;
/*     */   }
/*     */   
/*     */ 
/*     */   public static synchronized void register(LifeCycle... lifeCycles)
/*     */   {
/* 101 */     _thread._lifeCycles.addAll(Arrays.asList(lifeCycles));
/* 102 */     if (_thread._lifeCycles.size() > 0) {
/* 103 */       _thread.hook();
/*     */     }
/*     */   }
/*     */   
/*     */   public static synchronized void register(int index, LifeCycle... lifeCycles)
/*     */   {
/* 109 */     _thread._lifeCycles.addAll(index, Arrays.asList(lifeCycles));
/* 110 */     if (_thread._lifeCycles.size() > 0) {
/* 111 */       _thread.hook();
/*     */     }
/*     */   }
/*     */   
/*     */   public static synchronized void deregister(LifeCycle lifeCycle)
/*     */   {
/* 117 */     _thread._lifeCycles.remove(lifeCycle);
/* 118 */     if (_thread._lifeCycles.size() == 0) {
/* 119 */       _thread.unhook();
/*     */     }
/*     */   }
/*     */   
/*     */   public static synchronized boolean isRegistered(LifeCycle lifeCycle)
/*     */   {
/* 125 */     return _thread._lifeCycles.contains(lifeCycle);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void run()
/*     */   {
/* 132 */     for (LifeCycle lifeCycle : _thread._lifeCycles)
/*     */     {
/*     */       try
/*     */       {
/* 136 */         if (lifeCycle.isStarted())
/*     */         {
/* 138 */           lifeCycle.stop();
/* 139 */           LOG.debug("Stopped {}", new Object[] { lifeCycle });
/*     */         }
/*     */         
/* 142 */         if ((lifeCycle instanceof Destroyable))
/*     */         {
/* 144 */           ((Destroyable)lifeCycle).destroy();
/* 145 */           LOG.debug("Destroyed {}", new Object[] { lifeCycle });
/*     */         }
/*     */       }
/*     */       catch (Exception ex)
/*     */       {
/* 150 */         LOG.debug(ex);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\thread\ShutdownThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */