/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Supplier;
/*     */ import com.google.common.base.Throwables;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public abstract class AbstractExecutionThreadService
/*     */   implements Service
/*     */ {
/*  40 */   private static final Logger logger = Logger.getLogger(AbstractExecutionThreadService.class.getName());
/*     */   
/*     */ 
/*     */ 
/*  44 */   private final Service delegate = new AbstractService() {
/*     */     protected final void doStart() {
/*  46 */       Executor executor = MoreExecutors.renamingDecorator(AbstractExecutionThreadService.this.executor(), new Supplier() {
/*     */         public String get() {
/*  48 */           return AbstractExecutionThreadService.this.serviceName();
/*     */         }
/*  50 */       });
/*  51 */       executor.execute(new Runnable()
/*     */       {
/*     */         public void run() {
/*     */           try {
/*  55 */             AbstractExecutionThreadService.this.startUp();
/*  56 */             AbstractExecutionThreadService.1.this.notifyStarted();
/*     */             
/*  58 */             if (AbstractExecutionThreadService.1.this.isRunning()) {
/*     */               try {
/*  60 */                 AbstractExecutionThreadService.this.run();
/*     */               } catch (Throwable t) {
/*     */                 try {
/*  63 */                   AbstractExecutionThreadService.this.shutDown();
/*     */                 } catch (Exception ignored) {
/*  65 */                   AbstractExecutionThreadService.logger.log(Level.WARNING, "Error while attempting to shut down the service after failure.", ignored);
/*     */                 }
/*     */                 
/*     */ 
/*  69 */                 throw t;
/*     */               }
/*     */             }
/*     */             
/*  73 */             AbstractExecutionThreadService.this.shutDown();
/*  74 */             AbstractExecutionThreadService.1.this.notifyStopped();
/*     */           } catch (Throwable t) {
/*  76 */             AbstractExecutionThreadService.1.this.notifyFailed(t);
/*  77 */             throw Throwables.propagate(t);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */     protected void doStop() {
/*  84 */       AbstractExecutionThreadService.this.triggerShutdown();
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void startUp()
/*     */     throws Exception
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract void run()
/*     */     throws Exception;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void shutDown()
/*     */     throws Exception
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void triggerShutdown() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Executor executor()
/*     */   {
/* 143 */     new Executor()
/*     */     {
/*     */       public void execute(Runnable command) {
/* 146 */         MoreExecutors.newThread(AbstractExecutionThreadService.this.serviceName(), command).start();
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   public String toString() {
/* 152 */     return serviceName() + " [" + state() + "]";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public final ListenableFuture<State> start()
/*     */   {
/* 160 */     return this.delegate.start();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public final State startAndWait()
/*     */   {
/* 166 */     return this.delegate.startAndWait();
/*     */   }
/*     */   
/*     */   public final boolean isRunning() {
/* 170 */     return this.delegate.isRunning();
/*     */   }
/*     */   
/*     */   public final State state() {
/* 174 */     return this.delegate.state();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public final ListenableFuture<State> stop()
/*     */   {
/* 180 */     return this.delegate.stop();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public final State stopAndWait()
/*     */   {
/* 186 */     return this.delegate.stopAndWait();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final void addListener(Listener listener, Executor executor)
/*     */   {
/* 193 */     this.delegate.addListener(listener, executor);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final Throwable failureCause()
/*     */   {
/* 200 */     return this.delegate.failureCause();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final Service startAsync()
/*     */   {
/* 207 */     this.delegate.startAsync();
/* 208 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final Service stopAsync()
/*     */   {
/* 215 */     this.delegate.stopAsync();
/* 216 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final void awaitRunning()
/*     */   {
/* 223 */     this.delegate.awaitRunning();
/*     */   }
/*     */   
/*     */ 
/*     */   public final void awaitRunning(long timeout, TimeUnit unit)
/*     */     throws TimeoutException
/*     */   {
/* 230 */     this.delegate.awaitRunning(timeout, unit);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final void awaitTerminated()
/*     */   {
/* 237 */     this.delegate.awaitTerminated();
/*     */   }
/*     */   
/*     */ 
/*     */   public final void awaitTerminated(long timeout, TimeUnit unit)
/*     */     throws TimeoutException
/*     */   {
/* 244 */     this.delegate.awaitTerminated(timeout, unit);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String serviceName()
/*     */   {
/* 256 */     return getClass().getSimpleName();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\util\concurrent\AbstractExecutionThreadService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */