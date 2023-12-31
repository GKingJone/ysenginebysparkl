/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Supplier;
/*     */ import com.google.common.base.Throwables;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.annotation.concurrent.GuardedBy;
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
/*     */ public abstract class AbstractScheduledService
/*     */   implements Service
/*     */ {
/*  95 */   private static final Logger logger = Logger.getLogger(AbstractScheduledService.class.getName());
/*     */   
/*     */ 
/*     */   protected abstract void runOneIteration()
/*     */     throws Exception;
/*     */   
/*     */ 
/*     */ 
/*     */   protected void startUp()
/*     */     throws Exception
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */   protected void shutDown()
/*     */     throws Exception
/*     */   {}
/*     */   
/*     */ 
/*     */   protected abstract Scheduler scheduler();
/*     */   
/*     */ 
/*     */   public static abstract class Scheduler
/*     */   {
/*     */     public static Scheduler newFixedDelaySchedule(long initialDelay, long delay, final TimeUnit unit)
/*     */     {
/* 121 */       new Scheduler(initialDelay)
/*     */       {
/*     */         public Future<?> schedule(AbstractService service, ScheduledExecutorService executor, Runnable task)
/*     */         {
/* 125 */           return executor.scheduleWithFixedDelay(task, this.val$initialDelay, unit, this.val$unit);
/*     */         }
/*     */       };
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public static Scheduler newFixedRateSchedule(long initialDelay, long period, final TimeUnit unit)
/*     */     {
/* 140 */       new Scheduler(initialDelay)
/*     */       {
/*     */         public Future<?> schedule(AbstractService service, ScheduledExecutorService executor, Runnable task)
/*     */         {
/* 144 */           return executor.scheduleAtFixedRate(task, this.val$initialDelay, unit, this.val$unit);
/*     */         }
/*     */       };
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     abstract Future<?> schedule(AbstractService paramAbstractService, ScheduledExecutorService paramScheduledExecutorService, Runnable paramRunnable);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/* 157 */   private final AbstractService delegate = new AbstractService()
/*     */   {
/*     */     private volatile Future<?> runningTask;
/*     */     
/*     */ 
/*     */ 
/*     */     private volatile ScheduledExecutorService executorService;
/*     */     
/*     */ 
/* 166 */     private final ReentrantLock lock = new ReentrantLock();
/*     */     
/* 168 */     private final Runnable task = new Runnable() {
/*     */       public void run() {
/* 170 */         AbstractScheduledService.1.this.lock.lock();
/*     */         try {
/* 172 */           AbstractScheduledService.this.runOneIteration();
/*     */         } catch (Throwable t) {
/*     */           try {
/* 175 */             AbstractScheduledService.this.shutDown();
/*     */           } catch (Exception ignored) {
/* 177 */             AbstractScheduledService.logger.log(Level.WARNING, "Error while attempting to shut down the service after failure.", ignored);
/*     */           }
/*     */           
/* 180 */           AbstractScheduledService.1.this.notifyFailed(t);
/* 181 */           throw Throwables.propagate(t);
/*     */         } finally {
/* 183 */           AbstractScheduledService.1.this.lock.unlock();
/*     */         }
/*     */       }
/*     */     };
/*     */     
/*     */     protected final void doStart() {
/* 189 */       this.executorService = MoreExecutors.renamingDecorator(AbstractScheduledService.this.executor(), new Supplier() {
/*     */         public String get() {
/* 191 */           return AbstractScheduledService.this.serviceName() + " " + AbstractScheduledService.1.this.state();
/*     */         }
/* 193 */       });
/* 194 */       this.executorService.execute(new Runnable() {
/*     */         public void run() {
/* 196 */           AbstractScheduledService.1.this.lock.lock();
/*     */           try {
/* 198 */             AbstractScheduledService.this.startUp();
/* 199 */             AbstractScheduledService.1.this.runningTask = AbstractScheduledService.this.scheduler().schedule(AbstractScheduledService.this.delegate, AbstractScheduledService.1.this.executorService, AbstractScheduledService.1.this.task);
/* 200 */             AbstractScheduledService.1.this.notifyStarted();
/*     */           } catch (Throwable t) {
/* 202 */             AbstractScheduledService.1.this.notifyFailed(t);
/* 203 */             throw Throwables.propagate(t);
/*     */           } finally {
/* 205 */             AbstractScheduledService.1.this.lock.unlock();
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */     protected final void doStop() {
/* 212 */       this.runningTask.cancel(false);
/* 213 */       this.executorService.execute(new Runnable() {
/*     */         public void run() {
/*     */           try {
/* 216 */             AbstractScheduledService.1.this.lock.lock();
/*     */             try {
/* 218 */               if (AbstractScheduledService.1.this.state() != State.STOPPING) {
/*     */                 return;
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 225 */               AbstractScheduledService.this.shutDown();
/*     */             } finally {
/* 227 */               AbstractScheduledService.1.this.lock.unlock();
/*     */             }
/* 229 */             AbstractScheduledService.1.this.notifyStopped();
/*     */           } catch (Throwable t) {
/* 231 */             AbstractScheduledService.1.this.notifyFailed(t);
/* 232 */             throw Throwables.propagate(t);
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
/*     */   protected ScheduledExecutorService executor()
/*     */   {
/* 285 */     final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory()
/*     */     {
/*     */       public Thread newThread(Runnable runnable) {
/* 288 */         return MoreExecutors.newThread(AbstractScheduledService.this.serviceName(), runnable);
/*     */ 
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 295 */     });
/* 296 */     addListener(new Listener() {
/*     */       public void terminated(State from) {
/* 298 */         executor.shutdown();
/*     */       }
/*     */       
/* 301 */       public void failed(State from, Throwable failure) { executor.shutdown(); } }, MoreExecutors.sameThreadExecutor());
/*     */     
/* 303 */     return executor;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String serviceName()
/*     */   {
/* 313 */     return getClass().getSimpleName();
/*     */   }
/*     */   
/*     */   public String toString() {
/* 317 */     return serviceName() + " [" + state() + "]";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public final ListenableFuture<State> start()
/*     */   {
/* 325 */     return this.delegate.start();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public final State startAndWait()
/*     */   {
/* 331 */     return this.delegate.startAndWait();
/*     */   }
/*     */   
/*     */   public final boolean isRunning() {
/* 335 */     return this.delegate.isRunning();
/*     */   }
/*     */   
/*     */   public final State state() {
/* 339 */     return this.delegate.state();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public final ListenableFuture<State> stop()
/*     */   {
/* 345 */     return this.delegate.stop();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public final State stopAndWait()
/*     */   {
/* 351 */     return this.delegate.stopAndWait();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final void addListener(Listener listener, Executor executor)
/*     */   {
/* 358 */     this.delegate.addListener(listener, executor);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final Throwable failureCause()
/*     */   {
/* 365 */     return this.delegate.failureCause();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final Service startAsync()
/*     */   {
/* 372 */     this.delegate.startAsync();
/* 373 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final Service stopAsync()
/*     */   {
/* 380 */     this.delegate.stopAsync();
/* 381 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final void awaitRunning()
/*     */   {
/* 388 */     this.delegate.awaitRunning();
/*     */   }
/*     */   
/*     */ 
/*     */   public final void awaitRunning(long timeout, TimeUnit unit)
/*     */     throws TimeoutException
/*     */   {
/* 395 */     this.delegate.awaitRunning(timeout, unit);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final void awaitTerminated()
/*     */   {
/* 402 */     this.delegate.awaitTerminated();
/*     */   }
/*     */   
/*     */ 
/*     */   public final void awaitTerminated(long timeout, TimeUnit unit)
/*     */     throws TimeoutException
/*     */   {
/* 409 */     this.delegate.awaitTerminated(timeout, unit);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @Beta
/*     */   public static abstract class CustomScheduler
/*     */     extends Scheduler
/*     */   {
/*     */     public CustomScheduler()
/*     */     {
/* 421 */       super();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     private class ReschedulableCallable
/*     */       extends ForwardingFuture<Void>
/*     */       implements Callable<Void>
/*     */     {
/*     */       private final Runnable wrappedRunnable;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       private final ScheduledExecutorService executor;
/*     */       
/*     */ 
/*     */ 
/*     */       private final AbstractService service;
/*     */       
/*     */ 
/*     */ 
/* 445 */       private final ReentrantLock lock = new ReentrantLock();
/*     */       
/*     */       @GuardedBy("lock")
/*     */       private Future<Void> currentFuture;
/*     */       
/*     */ 
/*     */       ReschedulableCallable(AbstractService service, ScheduledExecutorService executor, Runnable runnable)
/*     */       {
/* 453 */         this.wrappedRunnable = runnable;
/* 454 */         this.executor = executor;
/* 455 */         this.service = service;
/*     */       }
/*     */       
/*     */       public Void call() throws Exception
/*     */       {
/* 460 */         this.wrappedRunnable.run();
/* 461 */         reschedule();
/* 462 */         return null;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void reschedule()
/*     */       {
/* 473 */         this.lock.lock();
/*     */         try {
/* 475 */           if ((this.currentFuture == null) || (!this.currentFuture.isCancelled())) {
/* 476 */             Schedule schedule = CustomScheduler.this.getNextSchedule();
/* 477 */             this.currentFuture = this.executor.schedule(this, schedule.delay, schedule.unit);
/*     */           }
/*     */           
/*     */ 
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/*     */ 
/* 485 */           this.service.notifyFailed(e);
/*     */         } finally {
/* 487 */           this.lock.unlock();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public boolean cancel(boolean mayInterruptIfRunning)
/*     */       {
/* 496 */         this.lock.lock();
/*     */         try {
/* 498 */           return this.currentFuture.cancel(mayInterruptIfRunning);
/*     */         } finally {
/* 500 */           this.lock.unlock();
/*     */         }
/*     */       }
/*     */       
/*     */       protected Future<Void> delegate()
/*     */       {
/* 506 */         throw new UnsupportedOperationException("Only cancel is supported by this future");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     final Future<?> schedule(AbstractService service, ScheduledExecutorService executor, Runnable runnable)
/*     */     {
/* 513 */       ReschedulableCallable task = new ReschedulableCallable(service, executor, runnable);
/* 514 */       task.reschedule();
/* 515 */       return task;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected abstract Schedule getNextSchedule()
/*     */       throws Exception;
/*     */     
/*     */ 
/*     */ 
/*     */     @Beta
/*     */     protected static final class Schedule
/*     */     {
/*     */       private final long delay;
/*     */       
/*     */       private final TimeUnit unit;
/*     */       
/*     */ 
/*     */       public Schedule(long delay, TimeUnit unit)
/*     */       {
/* 535 */         this.delay = delay;
/* 536 */         this.unit = ((TimeUnit)Preconditions.checkNotNull(unit));
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\util\concurrent\AbstractScheduledService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */