/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Supplier;
/*     */ import com.google.common.base.Throwables;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Beta
/*     */ public abstract class AbstractIdleService
/*     */   implements Service
/*     */ {
/*  41 */   private final Supplier<String> threadNameSupplier = new Supplier() {
/*     */     public String get() {
/*  43 */       return AbstractIdleService.this.serviceName() + " " + AbstractIdleService.this.state();
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*  48 */   private final Service delegate = new AbstractService() {
/*     */     protected final void doStart() {
/*  50 */       MoreExecutors.renamingDecorator(AbstractIdleService.this.executor(), AbstractIdleService.this.threadNameSupplier).execute(new Runnable()
/*     */       {
/*     */         public void run() {
/*     */           try {
/*  54 */             AbstractIdleService.this.startUp();
/*  55 */             AbstractIdleService.2.this.notifyStarted();
/*     */           } catch (Throwable t) {
/*  57 */             AbstractIdleService.2.this.notifyFailed(t);
/*  58 */             throw Throwables.propagate(t);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */     protected final void doStop() {
/*  65 */       MoreExecutors.renamingDecorator(AbstractIdleService.this.executor(), AbstractIdleService.this.threadNameSupplier).execute(new Runnable()
/*     */       {
/*     */         public void run() {
/*     */           try {
/*  69 */             AbstractIdleService.this.shutDown();
/*  70 */             AbstractIdleService.2.this.notifyStopped();
/*     */           } catch (Throwable t) {
/*  72 */             AbstractIdleService.2.this.notifyFailed(t);
/*  73 */             throw Throwables.propagate(t);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract void startUp()
/*     */     throws Exception;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract void shutDown()
/*     */     throws Exception;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Executor executor()
/*     */   {
/*  98 */     new Executor() {
/*     */       public void execute(Runnable command) {
/* 100 */         MoreExecutors.newThread((String)AbstractIdleService.this.threadNameSupplier.get(), command).start();
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public String toString() {
/* 106 */     return serviceName() + " [" + state() + "]";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public final ListenableFuture<State> start()
/*     */   {
/* 114 */     return this.delegate.start();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public final State startAndWait()
/*     */   {
/* 120 */     return this.delegate.startAndWait();
/*     */   }
/*     */   
/*     */   public final boolean isRunning() {
/* 124 */     return this.delegate.isRunning();
/*     */   }
/*     */   
/*     */   public final State state() {
/* 128 */     return this.delegate.state();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public final ListenableFuture<State> stop()
/*     */   {
/* 134 */     return this.delegate.stop();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public final State stopAndWait()
/*     */   {
/* 140 */     return this.delegate.stopAndWait();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final void addListener(Listener listener, Executor executor)
/*     */   {
/* 147 */     this.delegate.addListener(listener, executor);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final Throwable failureCause()
/*     */   {
/* 154 */     return this.delegate.failureCause();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final Service startAsync()
/*     */   {
/* 161 */     this.delegate.startAsync();
/* 162 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final Service stopAsync()
/*     */   {
/* 169 */     this.delegate.stopAsync();
/* 170 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final void awaitRunning()
/*     */   {
/* 177 */     this.delegate.awaitRunning();
/*     */   }
/*     */   
/*     */ 
/*     */   public final void awaitRunning(long timeout, TimeUnit unit)
/*     */     throws TimeoutException
/*     */   {
/* 184 */     this.delegate.awaitRunning(timeout, unit);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final void awaitTerminated()
/*     */   {
/* 191 */     this.delegate.awaitTerminated();
/*     */   }
/*     */   
/*     */ 
/*     */   public final void awaitTerminated(long timeout, TimeUnit unit)
/*     */     throws TimeoutException
/*     */   {
/* 198 */     this.delegate.awaitTerminated(timeout, unit);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String serviceName()
/*     */   {
/* 208 */     return getClass().getSimpleName();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\util\concurrent\AbstractIdleService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */