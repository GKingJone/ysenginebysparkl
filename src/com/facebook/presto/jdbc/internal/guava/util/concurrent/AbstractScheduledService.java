/*     */ package com.facebook.presto.jdbc.internal.guava.util.concurrent;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Supplier;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Throwables;
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
/*     */ 
/*     */ 
/*     */ @Beta
/*     */ public abstract class AbstractScheduledService
/*     */   implements Service
/*     */ {
/*  97 */   private static final Logger logger = Logger.getLogger(AbstractScheduledService.class.getName());
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
/* 123 */       new Scheduler(initialDelay)
/*     */       {
/*     */         public Future<?> schedule(AbstractService service, ScheduledExecutorService executor, Runnable task)
/*     */         {
/* 127 */           return executor.scheduleWithFixedDelay(task, this.val$initialDelay, unit, this.val$unit);
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
/* 142 */       new Scheduler(initialDelay)
/*     */       {
/*     */         public Future<?> schedule(AbstractService service, ScheduledExecutorService executor, Runnable task)
/*     */         {
/* 146 */           return executor.scheduleAtFixedRate(task, this.val$initialDelay, unit, this.val$unit);
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
/* 159 */   private final AbstractService delegate = new AbstractService()
/*     */   {
/*     */     private volatile Future<?> runningTask;
/*     */     
/*     */ 
/*     */ 
/*     */     private volatile ScheduledExecutorService executorService;
/*     */     
/*     */ 
/* 168 */     private final ReentrantLock lock = new ReentrantLock();
/*     */     
/* 170 */     private final Runnable task = new Runnable() {
/*     */       public void run() {
/* 172 */         AbstractScheduledService.1.this.lock.lock();
/*     */         try {
/* 174 */           AbstractScheduledService.this.runOneIteration();
/*     */         } catch (Throwable t) {
/*     */           try {
/* 177 */             AbstractScheduledService.this.shutDown();
/*     */           } catch (Exception ignored) {
/* 179 */             AbstractScheduledService.logger.log(Level.WARNING, "Error while attempting to shut down the service after failure.", ignored);
/*     */           }
/*     */           
/* 182 */           AbstractScheduledService.1.this.notifyFailed(t);
/* 183 */           throw Throwables.propagate(t);
/*     */         } finally {
/* 185 */           AbstractScheduledService.1.this.lock.unlock();
/*     */         }
/*     */       }
/*     */     };
/*     */     
/*     */     protected final void doStart() {
/* 191 */       this.executorService = MoreExecutors.renamingDecorator(AbstractScheduledService.this.executor(), new Supplier() {
/*     */         public String get() {
/* 193 */           String str1 = String.valueOf(String.valueOf(AbstractScheduledService.this.serviceName()));String str2 = String.valueOf(String.valueOf(AbstractScheduledService.1.this.state()));return 1 + str1.length() + str2.length() + str1 + " " + str2;
/*     */         }
/* 195 */       });
/* 196 */       this.executorService.execute(new Runnable() {
/*     */         public void run() {
/* 198 */           AbstractScheduledService.1.this.lock.lock();
/*     */           try {
/* 200 */             AbstractScheduledService.this.startUp();
/* 201 */             AbstractScheduledService.1.this.runningTask = AbstractScheduledService.this.scheduler().schedule(AbstractScheduledService.this.delegate, AbstractScheduledService.1.this.executorService, AbstractScheduledService.1.this.task);
/* 202 */             AbstractScheduledService.1.this.notifyStarted();
/*     */           } catch (Throwable t) {
/* 204 */             AbstractScheduledService.1.this.notifyFailed(t);
/* 205 */             throw Throwables.propagate(t);
/*     */           } finally {
/* 207 */             AbstractScheduledService.1.this.lock.unlock();
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */     protected final void doStop() {
/* 214 */       this.runningTask.cancel(false);
/* 215 */       this.executorService.execute(new Runnable() {
/*     */         public void run() {
/*     */           try {
/* 218 */             AbstractScheduledService.1.this.lock.lock();
/*     */             try {
/* 220 */               if (AbstractScheduledService.1.this.state() != State.STOPPING) {
/*     */                 return;
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 227 */               AbstractScheduledService.this.shutDown();
/*     */             } finally {
/* 229 */               AbstractScheduledService.1.this.lock.unlock();
/*     */             }
/* 231 */             AbstractScheduledService.1.this.notifyStopped();
/*     */           } catch (Throwable t) {
/* 233 */             AbstractScheduledService.1.this.notifyFailed(t);
/* 234 */             throw Throwables.propagate(t);
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
/* 287 */     final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory()
/*     */     {
/*     */       public Thread newThread(Runnable runnable) {
/* 290 */         return MoreExecutors.newThread(AbstractScheduledService.this.serviceName(), runnable);
/*     */ 
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 297 */     });
/* 298 */     addListener(new Listener() {
/*     */       public void terminated(State from) {
/* 300 */         executor.shutdown();
/*     */       }
/*     */       
/* 303 */       public void failed(State from, Throwable failure) { executor.shutdown(); } }, MoreExecutors.directExecutor());
/*     */     
/*     */ 
/* 306 */     return executor;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String serviceName()
/*     */   {
/* 316 */     return getClass().getSimpleName();
/*     */   }
/*     */   
/*     */   public String toString() {
/* 320 */     String str1 = String.valueOf(String.valueOf(serviceName()));String str2 = String.valueOf(String.valueOf(state()));return 3 + str1.length() + str2.length() + str1 + " [" + str2 + "]";
/*     */   }
/*     */   
/*     */   public final boolean isRunning() {
/* 324 */     return this.delegate.isRunning();
/*     */   }
/*     */   
/*     */   public final State state() {
/* 328 */     return this.delegate.state();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final void addListener(Listener listener, Executor executor)
/*     */   {
/* 335 */     this.delegate.addListener(listener, executor);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final Throwable failureCause()
/*     */   {
/* 342 */     return this.delegate.failureCause();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final Service startAsync()
/*     */   {
/* 349 */     this.delegate.startAsync();
/* 350 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final Service stopAsync()
/*     */   {
/* 357 */     this.delegate.stopAsync();
/* 358 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final void awaitRunning()
/*     */   {
/* 365 */     this.delegate.awaitRunning();
/*     */   }
/*     */   
/*     */ 
/*     */   public final void awaitRunning(long timeout, TimeUnit unit)
/*     */     throws TimeoutException
/*     */   {
/* 372 */     this.delegate.awaitRunning(timeout, unit);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final void awaitTerminated()
/*     */   {
/* 379 */     this.delegate.awaitTerminated();
/*     */   }
/*     */   
/*     */ 
/*     */   public final void awaitTerminated(long timeout, TimeUnit unit)
/*     */     throws TimeoutException
/*     */   {
/* 386 */     this.delegate.awaitTerminated(timeout, unit);
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
/* 398 */       super();
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
/* 422 */       private final ReentrantLock lock = new ReentrantLock();
/*     */       
/*     */       @GuardedBy("lock")
/*     */       private Future<Void> currentFuture;
/*     */       
/*     */ 
/*     */       ReschedulableCallable(AbstractService service, ScheduledExecutorService executor, Runnable runnable)
/*     */       {
/* 430 */         this.wrappedRunnable = runnable;
/* 431 */         this.executor = executor;
/* 432 */         this.service = service;
/*     */       }
/*     */       
/*     */       public Void call() throws Exception
/*     */       {
/* 437 */         this.wrappedRunnable.run();
/* 438 */         reschedule();
/* 439 */         return null;
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
/* 450 */         this.lock.lock();
/*     */         try {
/* 452 */           if ((this.currentFuture == null) || (!this.currentFuture.isCancelled())) {
/* 453 */             Schedule schedule = CustomScheduler.this.getNextSchedule();
/* 454 */             this.currentFuture = this.executor.schedule(this, schedule.delay, schedule.unit);
/*     */           }
/*     */           
/*     */ 
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/*     */ 
/* 462 */           this.service.notifyFailed(e);
/*     */         } finally {
/* 464 */           this.lock.unlock();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public boolean cancel(boolean mayInterruptIfRunning)
/*     */       {
/* 473 */         this.lock.lock();
/*     */         try {
/* 475 */           return this.currentFuture.cancel(mayInterruptIfRunning);
/*     */         } finally {
/* 477 */           this.lock.unlock();
/*     */         }
/*     */       }
/*     */       
/*     */       protected Future<Void> delegate()
/*     */       {
/* 483 */         throw new UnsupportedOperationException("Only cancel is supported by this future");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     final Future<?> schedule(AbstractService service, ScheduledExecutorService executor, Runnable runnable)
/*     */     {
/* 490 */       ReschedulableCallable task = new ReschedulableCallable(service, executor, runnable);
/* 491 */       task.reschedule();
/* 492 */       return task;
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
/* 512 */         this.delay = delay;
/* 513 */         this.unit = ((TimeUnit)Preconditions.checkNotNull(unit));
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\util\concurrent\AbstractScheduledService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */