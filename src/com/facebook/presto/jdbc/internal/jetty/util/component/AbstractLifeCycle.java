/*     */ package com.facebook.presto.jdbc.internal.jetty.util.component;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.Uptime;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedAttribute;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedObject;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
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
/*     */ @ManagedObject("Abstract Implementation of LifeCycle")
/*     */ public abstract class AbstractLifeCycle
/*     */   implements LifeCycle
/*     */ {
/*  35 */   private static final Logger LOG = Log.getLogger(AbstractLifeCycle.class);
/*     */   
/*     */   public static final String STOPPED = "STOPPED";
/*     */   
/*     */   public static final String FAILED = "FAILED";
/*     */   public static final String STARTING = "STARTING";
/*     */   public static final String STARTED = "STARTED";
/*     */   public static final String STOPPING = "STOPPING";
/*     */   public static final String RUNNING = "RUNNING";
/*  44 */   private final CopyOnWriteArrayList<Listener> _listeners = new CopyOnWriteArrayList();
/*  45 */   private final Object _lock = new Object();
/*  46 */   private final int __FAILED = -1; private final int __STOPPED = 0; private final int __STARTING = 1; private final int __STARTED = 2; private final int __STOPPING = 3;
/*  47 */   private volatile int _state = 0;
/*  48 */   private long _stopTimeout = 30000L;
/*     */   
/*     */   protected void doStart()
/*     */     throws Exception
/*     */   {}
/*     */   
/*     */   protected void doStop()
/*     */     throws Exception
/*     */   {}
/*     */   
/*     */   public final void start()
/*     */     throws Exception
/*     */   {
/*  61 */     synchronized (this._lock)
/*     */     {
/*     */       try
/*     */       {
/*  65 */         if ((this._state == 2) || (this._state == 1))
/*  66 */           return;
/*  67 */         setStarting();
/*  68 */         doStart();
/*  69 */         setStarted();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*  73 */         setFailed(e);
/*  74 */         throw e;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public final void stop()
/*     */     throws Exception
/*     */   {
/*  82 */     synchronized (this._lock)
/*     */     {
/*     */       try
/*     */       {
/*  86 */         if ((this._state == 3) || (this._state == 0))
/*  87 */           return;
/*  88 */         setStopping();
/*  89 */         doStop();
/*  90 */         setStopped();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*  94 */         setFailed(e);
/*  95 */         throw e;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isRunning()
/*     */   {
/* 103 */     int state = this._state;
/*     */     
/* 105 */     return (state == 2) || (state == 1);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isStarted()
/*     */   {
/* 111 */     return this._state == 2;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isStarting()
/*     */   {
/* 117 */     return this._state == 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isStopping()
/*     */   {
/* 123 */     return this._state == 3;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isStopped()
/*     */   {
/* 129 */     return this._state == 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isFailed()
/*     */   {
/* 135 */     return this._state == -1;
/*     */   }
/*     */   
/*     */ 
/*     */   public void addLifeCycleListener(Listener listener)
/*     */   {
/* 141 */     this._listeners.add(listener);
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeLifeCycleListener(Listener listener)
/*     */   {
/* 147 */     this._listeners.remove(listener);
/*     */   }
/*     */   
/*     */   @ManagedAttribute(value="Lifecycle State for this instance", readonly=true)
/*     */   public String getState()
/*     */   {
/* 153 */     switch (this._state) {
/*     */     case -1: 
/* 155 */       return "FAILED";
/* 156 */     case 1:  return "STARTING";
/* 157 */     case 2:  return "STARTED";
/* 158 */     case 3:  return "STOPPING";
/* 159 */     case 0:  return "STOPPED";
/*     */     }
/* 161 */     return null;
/*     */   }
/*     */   
/*     */   public static String getState(LifeCycle lc)
/*     */   {
/* 166 */     if (lc.isStarting()) return "STARTING";
/* 167 */     if (lc.isStarted()) return "STARTED";
/* 168 */     if (lc.isStopping()) return "STOPPING";
/* 169 */     if (lc.isStopped()) return "STOPPED";
/* 170 */     return "FAILED";
/*     */   }
/*     */   
/*     */   private void setStarted()
/*     */   {
/* 175 */     this._state = 2;
/* 176 */     if (LOG.isDebugEnabled())
/* 177 */       LOG.debug("STARTED @{}ms {}", new Object[] { Long.valueOf(Uptime.getUptime()), this });
/* 178 */     for (Listener listener : this._listeners) {
/* 179 */       listener.lifeCycleStarted(this);
/*     */     }
/*     */   }
/*     */   
/*     */   private void setStarting() {
/* 184 */     if (LOG.isDebugEnabled())
/* 185 */       LOG.debug("starting {}", new Object[] { this });
/* 186 */     this._state = 1;
/* 187 */     for (Listener listener : this._listeners) {
/* 188 */       listener.lifeCycleStarting(this);
/*     */     }
/*     */   }
/*     */   
/*     */   private void setStopping() {
/* 193 */     if (LOG.isDebugEnabled())
/* 194 */       LOG.debug("stopping {}", new Object[] { this });
/* 195 */     this._state = 3;
/* 196 */     for (Listener listener : this._listeners) {
/* 197 */       listener.lifeCycleStopping(this);
/*     */     }
/*     */   }
/*     */   
/*     */   private void setStopped() {
/* 202 */     this._state = 0;
/* 203 */     if (LOG.isDebugEnabled())
/* 204 */       LOG.debug("{} {}", new Object[] { "STOPPED", this });
/* 205 */     for (Listener listener : this._listeners) {
/* 206 */       listener.lifeCycleStopped(this);
/*     */     }
/*     */   }
/*     */   
/*     */   private void setFailed(Throwable th) {
/* 211 */     this._state = -1;
/* 212 */     if (LOG.isDebugEnabled())
/* 213 */       LOG.warn("FAILED " + this + ": " + th, th);
/* 214 */     for (Listener listener : this._listeners) {
/* 215 */       listener.lifeCycleFailure(this, th);
/*     */     }
/*     */   }
/*     */   
/*     */   @ManagedAttribute("The stop timeout in milliseconds")
/*     */   public long getStopTimeout() {
/* 221 */     return this._stopTimeout;
/*     */   }
/*     */   
/*     */   public void setStopTimeout(long stopTimeout)
/*     */   {
/* 226 */     this._stopTimeout = stopTimeout;
/*     */   }
/*     */   
/*     */   public static abstract class AbstractLifeCycleListener
/*     */     implements Listener
/*     */   {
/*     */     public void lifeCycleFailure(LifeCycle event, Throwable cause) {}
/*     */     
/*     */     public void lifeCycleStarted(LifeCycle event) {}
/*     */     
/*     */     public void lifeCycleStarting(LifeCycle event) {}
/*     */     
/*     */     public void lifeCycleStopped(LifeCycle event) {}
/*     */     
/*     */     public void lifeCycleStopping(LifeCycle event) {}
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\component\AbstractLifeCycle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */