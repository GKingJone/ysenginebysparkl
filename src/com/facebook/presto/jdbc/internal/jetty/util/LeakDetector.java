/*     */ package com.facebook.presto.jdbc.internal.jetty.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.AbstractLifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.lang.ref.PhantomReference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LeakDetector<T>
/*     */   extends AbstractLifeCycle
/*     */   implements Runnable
/*     */ {
/*  62 */   private static final Logger LOG = Log.getLogger(LeakDetector.class);
/*     */   
/*  64 */   private final ReferenceQueue<T> queue = new ReferenceQueue();
/*  65 */   private final ConcurrentMap<String, LeakInfo> resources = new ConcurrentHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private Thread thread;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean acquired(T resource)
/*     */   {
/*  78 */     String id = id(resource);
/*  79 */     LeakInfo info = (LeakInfo)this.resources.putIfAbsent(id, new LeakInfo(resource, id, null));
/*  80 */     if (info != null)
/*     */     {
/*     */ 
/*  83 */       return false;
/*     */     }
/*     */     
/*  86 */     return true;
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
/*     */   public boolean released(T resource)
/*     */   {
/*  99 */     String id = id(resource);
/* 100 */     LeakInfo info = (LeakInfo)this.resources.remove(id);
/* 101 */     if (info != null)
/*     */     {
/*     */ 
/* 104 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 108 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String id(T resource)
/*     */   {
/* 119 */     return String.valueOf(System.identityHashCode(resource));
/*     */   }
/*     */   
/*     */   protected void doStart()
/*     */     throws Exception
/*     */   {
/* 125 */     super.doStart();
/* 126 */     this.thread = new Thread(this, getClass().getSimpleName());
/* 127 */     this.thread.setDaemon(true);
/* 128 */     this.thread.start();
/*     */   }
/*     */   
/*     */   protected void doStop()
/*     */     throws Exception
/*     */   {
/* 134 */     super.doStop();
/* 135 */     this.thread.interrupt();
/*     */   }
/*     */   
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     try
/*     */     {
/* 143 */       while (isRunning())
/*     */       {
/*     */ 
/* 146 */         LeakInfo leakInfo = (LeakInfo)this.queue.remove();
/* 147 */         if (LOG.isDebugEnabled())
/* 148 */           LOG.debug("Resource GC'ed: {}", new Object[] { leakInfo });
/* 149 */         if (this.resources.remove(leakInfo.id) != null) {
/* 150 */           leaked(leakInfo);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (InterruptedException localInterruptedException) {}
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void leaked(LeakInfo leakInfo)
/*     */   {
/* 166 */     LOG.warn("Resource leaked: " + leakInfo.description, leakInfo.stackFrames);
/*     */   }
/*     */   
/*     */ 
/*     */   public class LeakInfo
/*     */     extends PhantomReference<T>
/*     */   {
/*     */     private final String id;
/*     */     
/*     */     private final String description;
/*     */     private final Throwable stackFrames;
/*     */     
/*     */     private LeakInfo(String referent)
/*     */     {
/* 180 */       super(LeakDetector.this.queue);
/* 181 */       this.id = id;
/* 182 */       this.description = referent.toString();
/* 183 */       this.stackFrames = new Throwable();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getResourceDescription()
/*     */     {
/* 191 */       return this.description;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public Throwable getStackFrames()
/*     */     {
/* 199 */       return this.stackFrames;
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 205 */       return this.description;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\LeakDetector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */