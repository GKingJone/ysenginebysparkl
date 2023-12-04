/*     */ package com.facebook.presto.jdbc.internal.jetty.util.thread.strategy;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.ExecutionStrategy;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.ExecutionStrategy.Producer;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.ExecutionStrategy.Rejectable;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Locker;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Locker.Lock;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.thread.ThreadPool;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.RejectedExecutionException;
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
/*     */ public class ExecuteProduceConsume
/*     */   implements ExecutionStrategy, Runnable
/*     */ {
/*  48 */   private static final Logger LOG = Log.getLogger(ExecuteProduceConsume.class);
/*  49 */   private final Locker _locker = new Locker();
/*  50 */   private final Runnable _runExecute = new RunExecute(null);
/*     */   private final ExecutionStrategy.Producer _producer;
/*     */   private final Executor _executor;
/*  53 */   private boolean _idle = true;
/*     */   private boolean _execute;
/*     */   private boolean _producing;
/*     */   private boolean _pending;
/*     */   private final ThreadPool _threadpool;
/*     */   private final ExecutionStrategy _lowresources;
/*     */   
/*     */   public ExecuteProduceConsume(ExecutionStrategy.Producer producer, Executor executor)
/*     */   {
/*  62 */     this(producer, executor, (executor instanceof ThreadPool) ? new ProduceExecuteConsume(producer, executor) : null);
/*     */   }
/*     */   
/*     */   public ExecuteProduceConsume(ExecutionStrategy.Producer producer, Executor executor, ExecutionStrategy lowResourceStrategy)
/*     */   {
/*  67 */     this._producer = producer;
/*  68 */     this._executor = executor;
/*  69 */     this._threadpool = ((executor instanceof ThreadPool) ? (ThreadPool)executor : null);
/*  70 */     this._lowresources = (this._threadpool == null ? null : lowResourceStrategy);
/*     */   }
/*     */   
/*     */ 
/*     */   public void execute()
/*     */   {
/*  76 */     if (LOG.isDebugEnabled()) {
/*  77 */       LOG.debug("{} execute", new Object[] { this });
/*     */     }
/*  79 */     boolean produce = false;
/*  80 */     Locker.Lock locked = this._locker.lock();Throwable localThrowable3 = null;
/*     */     try
/*     */     {
/*  83 */       if (this._idle)
/*     */       {
/*  85 */         if (this._producing) {
/*  86 */           throw new IllegalStateException();
/*     */         }
/*     */         
/*  89 */         produce = this._producing = 1;
/*     */         
/*  91 */         this._idle = false;
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/*  97 */         this._execute = true;
/*     */       }
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/*  80 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  99 */       if (locked != null) if (localThrowable3 != null) try { locked.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else locked.close();
/*     */     }
/* 101 */     if (produce) {
/* 102 */       produceAndRun();
/*     */     }
/*     */   }
/*     */   
/*     */   public void dispatch()
/*     */   {
/* 108 */     if (LOG.isDebugEnabled())
/* 109 */       LOG.debug("{} spawning", new Object[] { this });
/* 110 */     boolean dispatch = false;
/* 111 */     Locker.Lock locked = this._locker.lock();Throwable localThrowable3 = null;
/*     */     try {
/* 113 */       if (this._idle) {
/* 114 */         dispatch = true;
/*     */       } else {
/* 116 */         this._execute = true;
/*     */       }
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 111 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/*     */ 
/* 117 */       if (locked != null) if (localThrowable3 != null) try { locked.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else locked.close(); }
/* 118 */     if (dispatch) {
/* 119 */       this._executor.execute(this._runExecute);
/*     */     }
/*     */   }
/*     */   
/*     */   public void run()
/*     */   {
/* 125 */     if (LOG.isDebugEnabled())
/* 126 */       LOG.debug("{} run", new Object[] { this });
/* 127 */     boolean produce = false;
/* 128 */     Locker.Lock locked = this._locker.lock();Throwable localThrowable4 = null;
/*     */     try {
/* 130 */       this._pending = false;
/* 131 */       if ((!this._idle) && (!this._producing))
/*     */       {
/* 133 */         produce = this._producing = 1;
/*     */       }
/*     */     }
/*     */     catch (Throwable localThrowable2)
/*     */     {
/* 128 */       localThrowable4 = localThrowable2;throw localThrowable2;
/*     */ 
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/*     */ 
/* 135 */       if (locked != null) if (localThrowable4 != null) try { locked.close(); } catch (Throwable localThrowable3) { localThrowable4.addSuppressed(localThrowable3); } else locked.close();
/*     */     }
/* 137 */     if (produce)
/*     */     {
/*     */ 
/*     */ 
/* 141 */       while ((this._threadpool != null) && (this._threadpool.isLowOnThreads()))
/*     */       {
/* 143 */         LOG.debug("EWYK low resources {}", new Object[] { this });
/*     */         try
/*     */         {
/* 146 */           this._lowresources.execute();
/*     */ 
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 151 */           LOG.warn(e);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 156 */       produceAndRun();
/*     */     }
/*     */   }
/*     */   
/*     */   private void produceAndRun()
/*     */   {
/* 162 */     if (LOG.isDebugEnabled()) {
/* 163 */       LOG.debug("{} produce enter", new Object[] { this });
/*     */     }
/*     */     
/*     */     for (;;)
/*     */     {
/* 168 */       if (LOG.isDebugEnabled()) {
/* 169 */         LOG.debug("{} producing", new Object[] { this });
/*     */       }
/* 171 */       Runnable task = this._producer.produce();
/*     */       
/* 173 */       if (LOG.isDebugEnabled()) {
/* 174 */         LOG.debug("{} produced {}", new Object[] { this, task });
/*     */       }
/* 176 */       boolean dispatch = false;
/* 177 */       Locker.Lock locked = this._locker.lock();Throwable localThrowable9 = null;
/*     */       try
/*     */       {
/* 180 */         this._producing = false;
/*     */         
/*     */ 
/* 183 */         if (task == null)
/*     */         {
/*     */ 
/* 186 */           if (this._execute)
/*     */           {
/* 188 */             this._idle = false;
/* 189 */             this._producing = true;
/* 190 */             this._execute = false;
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
/* 208 */             if (locked == null) continue; if (localThrowable9 != null) { try { locked.close(); } catch (Throwable localThrowable) {} localThrowable9.addSuppressed(localThrowable); continue; } locked.close(); continue;
/*     */           }
/* 195 */           this._idle = true;
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
/* 208 */           if (locked == null) break; if (localThrowable9 != null) try { locked.close(); } catch (Throwable localThrowable1) { localThrowable9.addSuppressed(localThrowable1); } locked.close(); break;
/*     */         }
/* 201 */         if (!this._pending)
/*     */         {
/*     */ 
/* 204 */           dispatch = this._pending = 1;
/*     */         }
/*     */         
/* 207 */         this._execute = false;
/*     */       }
/*     */       catch (Throwable localThrowable3)
/*     */       {
/* 177 */         localThrowable9 = localThrowable3;throw localThrowable3;
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
/*     */       }
/*     */       finally
/*     */       {
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
/* 208 */         if (locked != null) if (localThrowable9 != null) try { locked.close(); } catch (Throwable localThrowable4) { localThrowable9.addSuppressed(localThrowable4); } else { locked.close();
/*     */           }
/*     */       }
/* 211 */       if (dispatch)
/*     */       {
/*     */ 
/* 214 */         if (LOG.isDebugEnabled()) {
/* 215 */           LOG.debug("{} dispatch", new Object[] { this });
/*     */         }
/*     */         try {
/* 218 */           this._executor.execute(this);
/*     */ 
/*     */         }
/*     */         catch (RejectedExecutionException e)
/*     */         {
/* 223 */           LOG.debug(e);
/* 224 */           LOG.warn("RejectedExecution {}", new Object[] { task });
/*     */           try
/*     */           {
/* 227 */             if ((task instanceof ExecutionStrategy.Rejectable)) {
/* 228 */               ((ExecutionStrategy.Rejectable)task).reject();
/*     */             }
/*     */           }
/*     */           catch (Exception x) {
/* 232 */             e.addSuppressed((Throwable)x);
/* 233 */             LOG.warn(e);
/*     */           }
/*     */           finally
/*     */           {
/* 237 */             task = null;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 243 */       if (LOG.isDebugEnabled())
/* 244 */         LOG.debug("{} run {}", new Object[] { this, task });
/* 245 */       if (task != null)
/* 246 */         task.run();
/* 247 */       if (LOG.isDebugEnabled()) {
/* 248 */         LOG.debug("{} ran {}", new Object[] { this, task });
/*     */       }
/*     */       
/* 251 */       Locker.Lock locked = this._locker.lock();x = null;
/*     */       try
/*     */       {
/* 254 */         if ((this._producing) || (this._idle))
/*     */         {
/*     */ 
/* 257 */           if (locked == null) break; if (x != null) try { locked.close(); } catch (Throwable localThrowable5) { ((Throwable)x).addSuppressed(localThrowable5); } locked.close(); break;
/*     */         }
/* 256 */         this._producing = true;
/*     */       }
/*     */       catch (Throwable localThrowable7)
/*     */       {
/* 251 */         x = localThrowable7;throw localThrowable7;
/*     */ 
/*     */       }
/*     */       finally
/*     */       {
/*     */ 
/* 257 */         if (locked != null) if (x != null) try { locked.close(); } catch (Throwable localThrowable8) { ((Throwable)x).addSuppressed(localThrowable8); } else locked.close();
/*     */       }
/*     */     }
/* 260 */     if (LOG.isDebugEnabled()) {
/* 261 */       LOG.debug("{} produce exit", new Object[] { this });
/*     */     }
/*     */   }
/*     */   
/*     */   public Boolean isIdle() {
/* 266 */     Locker.Lock locked = this._locker.lock();Throwable localThrowable3 = null;
/*     */     try {
/* 268 */       return Boolean.valueOf(this._idle);
/*     */     }
/*     */     catch (Throwable localThrowable4)
/*     */     {
/* 266 */       localThrowable3 = localThrowable4;throw localThrowable4;
/*     */     }
/*     */     finally {
/* 269 */       if (locked != null) if (localThrowable3 != null) try { locked.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else locked.close();
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString() {
/* 274 */     StringBuilder builder = new StringBuilder();
/* 275 */     builder.append("EPR ");
/* 276 */     Locker.Lock locked = this._locker.lock();Throwable localThrowable3 = null;
/*     */     try {
/* 278 */       builder.append(this._idle ? "Idle/" : "");
/* 279 */       builder.append(this._producing ? "Prod/" : "");
/* 280 */       builder.append(this._pending ? "Pend/" : "");
/* 281 */       builder.append(this._execute ? "Exec/" : "");
/*     */     }
/*     */     catch (Throwable localThrowable1)
/*     */     {
/* 276 */       localThrowable3 = localThrowable1;throw localThrowable1;
/*     */ 
/*     */     }
/*     */     finally
/*     */     {
/*     */ 
/* 282 */       if (locked != null) if (localThrowable3 != null) try { locked.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else locked.close(); }
/* 283 */     builder.append(this._producer);
/* 284 */     return builder.toString();
/*     */   }
/*     */   
/*     */   private class RunExecute implements Runnable
/*     */   {
/*     */     private RunExecute() {}
/*     */     
/*     */     public void run() {
/* 292 */       ExecuteProduceConsume.this.execute();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\thread\strategy\ExecuteProduceConsume.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */