/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Supplier;
/*     */ import com.google.common.base.Throwables;
/*     */ import com.google.common.collect.Lists;
/*     */ import com.google.common.collect.Queues;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.Delayed;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.RejectedExecutionException;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import java.util.concurrent.ScheduledThreadPoolExecutor;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import java.util.concurrent.locks.Condition;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class MoreExecutors
/*     */ {
/*     */   @Beta
/*     */   public static ExecutorService getExitingExecutorService(ThreadPoolExecutor executor, long terminationTimeout, TimeUnit timeUnit)
/*     */   {
/*  86 */     return new Application().getExitingExecutorService(executor, terminationTimeout, timeUnit);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Beta
/*     */   public static ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor executor, long terminationTimeout, TimeUnit timeUnit)
/*     */   {
/* 109 */     return new Application().getExitingScheduledExecutorService(executor, terminationTimeout, timeUnit);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Beta
/*     */   public static void addDelayedShutdownHook(ExecutorService service, long terminationTimeout, TimeUnit timeUnit)
/*     */   {
/* 127 */     new Application().addDelayedShutdownHook(service, terminationTimeout, timeUnit);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Beta
/*     */   public static ExecutorService getExitingExecutorService(ThreadPoolExecutor executor)
/*     */   {
/* 148 */     return new Application().getExitingExecutorService(executor);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Beta
/*     */   public static ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor executor)
/*     */   {
/* 169 */     return new Application().getExitingScheduledExecutorService(executor);
/*     */   }
/*     */   
/*     */   @VisibleForTesting
/*     */   static class Application
/*     */   {
/*     */     final ExecutorService getExitingExecutorService(ThreadPoolExecutor executor, long terminationTimeout, TimeUnit timeUnit)
/*     */     {
/* 177 */       MoreExecutors.useDaemonThreadFactory(executor);
/* 178 */       ExecutorService service = Executors.unconfigurableExecutorService(executor);
/* 179 */       addDelayedShutdownHook(service, terminationTimeout, timeUnit);
/* 180 */       return service;
/*     */     }
/*     */     
/*     */     final ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor executor, long terminationTimeout, TimeUnit timeUnit)
/*     */     {
/* 185 */       MoreExecutors.useDaemonThreadFactory(executor);
/* 186 */       ScheduledExecutorService service = Executors.unconfigurableScheduledExecutorService(executor);
/* 187 */       addDelayedShutdownHook(service, terminationTimeout, timeUnit);
/* 188 */       return service;
/*     */     }
/*     */     
/*     */     final void addDelayedShutdownHook(final ExecutorService service, final long terminationTimeout, TimeUnit timeUnit)
/*     */     {
/* 193 */       Preconditions.checkNotNull(service);
/* 194 */       Preconditions.checkNotNull(timeUnit);
/* 195 */       addShutdownHook(MoreExecutors.newThread("DelayedShutdownHook-for-" + service, new Runnable()
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/*     */ 
/*     */           try
/*     */           {
/*     */ 
/* 204 */             service.shutdown();
/* 205 */             service.awaitTermination(terminationTimeout, this.val$timeUnit);
/*     */           }
/*     */           catch (InterruptedException ignored) {}
/*     */         }
/*     */       }));
/*     */     }
/*     */     
/*     */     final ExecutorService getExitingExecutorService(ThreadPoolExecutor executor)
/*     */     {
/* 214 */       return getExitingExecutorService(executor, 120L, TimeUnit.SECONDS);
/*     */     }
/*     */     
/*     */     final ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor executor)
/*     */     {
/* 219 */       return getExitingScheduledExecutorService(executor, 120L, TimeUnit.SECONDS);
/*     */     }
/*     */     
/*     */     @VisibleForTesting
/* 223 */     void addShutdownHook(Thread hook) { Runtime.getRuntime().addShutdownHook(hook); }
/*     */   }
/*     */   
/*     */   private static void useDaemonThreadFactory(ThreadPoolExecutor executor)
/*     */   {
/* 228 */     executor.setThreadFactory(new ThreadFactoryBuilder().setDaemon(true).setThreadFactory(executor.getThreadFactory()).build());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ListeningExecutorService sameThreadExecutor()
/*     */   {
/* 268 */     return new SameThreadExecutorService(null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class SameThreadExecutorService
/*     */     extends AbstractListeningExecutorService
/*     */   {
/* 278 */     private final Lock lock = new ReentrantLock();
/*     */     
/*     */ 
/* 281 */     private final Condition termination = this.lock.newCondition();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 290 */     private int runningTasks = 0;
/* 291 */     private boolean shutdown = false;
/*     */     
/*     */     public void execute(Runnable command)
/*     */     {
/* 295 */       startTask();
/*     */       try {
/* 297 */         command.run();
/*     */       } finally {
/* 299 */         endTask();
/*     */       }
/*     */     }
/*     */     
/*     */     public boolean isShutdown()
/*     */     {
/* 305 */       this.lock.lock();
/*     */       try {
/* 307 */         return this.shutdown;
/*     */       } finally {
/* 309 */         this.lock.unlock();
/*     */       }
/*     */     }
/*     */     
/*     */     public void shutdown()
/*     */     {
/* 315 */       this.lock.lock();
/*     */       try {
/* 317 */         this.shutdown = true;
/*     */       } finally {
/* 319 */         this.lock.unlock();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public List<Runnable> shutdownNow()
/*     */     {
/* 326 */       shutdown();
/* 327 */       return Collections.emptyList();
/*     */     }
/*     */     
/*     */     public boolean isTerminated()
/*     */     {
/* 332 */       this.lock.lock();
/*     */       try {
/* 334 */         return (this.shutdown) && (this.runningTasks == 0);
/*     */       } finally {
/* 336 */         this.lock.unlock();
/*     */       }
/*     */     }
/*     */     
/*     */     /* Error */
/*     */     public boolean awaitTermination(long timeout, TimeUnit unit)
/*     */       throws InterruptedException
/*     */     {
/*     */       // Byte code:
/*     */       //   0: aload_3
/*     */       //   1: lload_1
/*     */       //   2: invokevirtual 17	java/util/concurrent/TimeUnit:toNanos	(J)J
/*     */       //   5: lstore 4
/*     */       //   7: aload_0
/*     */       //   8: getfield 5	com/google/common/util/concurrent/MoreExecutors$SameThreadExecutorService:lock	Ljava/util/concurrent/locks/Lock;
/*     */       //   11: invokeinterface 13 1 0
/*     */       //   16: aload_0
/*     */       //   17: invokevirtual 18	com/google/common/util/concurrent/MoreExecutors$SameThreadExecutorService:isTerminated	()Z
/*     */       //   20: ifeq +18 -> 38
/*     */       //   23: iconst_1
/*     */       //   24: istore 6
/*     */       //   26: aload_0
/*     */       //   27: getfield 5	com/google/common/util/concurrent/MoreExecutors$SameThreadExecutorService:lock	Ljava/util/concurrent/locks/Lock;
/*     */       //   30: invokeinterface 14 1 0
/*     */       //   35: iload 6
/*     */       //   37: ireturn
/*     */       //   38: lload 4
/*     */       //   40: lconst_0
/*     */       //   41: lcmp
/*     */       //   42: ifgt +18 -> 60
/*     */       //   45: iconst_0
/*     */       //   46: istore 6
/*     */       //   48: aload_0
/*     */       //   49: getfield 5	com/google/common/util/concurrent/MoreExecutors$SameThreadExecutorService:lock	Ljava/util/concurrent/locks/Lock;
/*     */       //   52: invokeinterface 14 1 0
/*     */       //   57: iload 6
/*     */       //   59: ireturn
/*     */       //   60: aload_0
/*     */       //   61: getfield 7	com/google/common/util/concurrent/MoreExecutors$SameThreadExecutorService:termination	Ljava/util/concurrent/locks/Condition;
/*     */       //   64: lload 4
/*     */       //   66: invokeinterface 19 3 0
/*     */       //   71: lstore 4
/*     */       //   73: goto -57 -> 16
/*     */       //   76: astore 7
/*     */       //   78: aload_0
/*     */       //   79: getfield 5	com/google/common/util/concurrent/MoreExecutors$SameThreadExecutorService:lock	Ljava/util/concurrent/locks/Lock;
/*     */       //   82: invokeinterface 14 1 0
/*     */       //   87: aload 7
/*     */       //   89: athrow
/*     */       // Line number table:
/*     */       //   Java source line #343	-> byte code offset #0
/*     */       //   Java source line #344	-> byte code offset #7
/*     */       //   Java source line #347	-> byte code offset #16
/*     */       //   Java source line #348	-> byte code offset #23
/*     */       //   Java source line #356	-> byte code offset #26
/*     */       //   Java source line #349	-> byte code offset #38
/*     */       //   Java source line #350	-> byte code offset #45
/*     */       //   Java source line #356	-> byte code offset #48
/*     */       //   Java source line #352	-> byte code offset #60
/*     */       //   Java source line #356	-> byte code offset #76
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	90	0	this	SameThreadExecutorService
/*     */       //   0	90	1	timeout	long
/*     */       //   0	90	3	unit	TimeUnit
/*     */       //   5	67	4	nanos	long
/*     */       //   24	34	6	bool	boolean
/*     */       //   76	12	7	localObject	Object
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   16	26	76	finally
/*     */       //   38	48	76	finally
/*     */       //   60	78	76	finally
/*     */     }
/*     */     
/*     */     private void startTask()
/*     */     {
/* 368 */       this.lock.lock();
/*     */       try {
/* 370 */         if (isShutdown()) {
/* 371 */           throw new RejectedExecutionException("Executor already shutdown");
/*     */         }
/* 373 */         this.runningTasks += 1;
/*     */       } finally {
/* 375 */         this.lock.unlock();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private void endTask()
/*     */     {
/* 383 */       this.lock.lock();
/*     */       try {
/* 385 */         this.runningTasks -= 1;
/* 386 */         if (isTerminated()) {
/* 387 */           this.termination.signalAll();
/*     */         }
/*     */       } finally {
/* 390 */         this.lock.unlock();
/*     */       }
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ListeningExecutorService listeningDecorator(ExecutorService delegate)
/*     */   {
/* 415 */     return (delegate instanceof ScheduledExecutorService) ? new ScheduledListeningDecorator((ScheduledExecutorService)delegate) : (delegate instanceof ListeningExecutorService) ? (ListeningExecutorService)delegate : new ListeningDecorator(delegate);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ListeningScheduledExecutorService listeningDecorator(ScheduledExecutorService delegate)
/*     */   {
/* 443 */     return (delegate instanceof ListeningScheduledExecutorService) ? (ListeningScheduledExecutorService)delegate : new ScheduledListeningDecorator(delegate);
/*     */   }
/*     */   
/*     */   private static class ListeningDecorator
/*     */     extends AbstractListeningExecutorService
/*     */   {
/*     */     private final ExecutorService delegate;
/*     */     
/*     */     ListeningDecorator(ExecutorService delegate)
/*     */     {
/* 453 */       this.delegate = ((ExecutorService)Preconditions.checkNotNull(delegate));
/*     */     }
/*     */     
/*     */     public boolean awaitTermination(long timeout, TimeUnit unit)
/*     */       throws InterruptedException
/*     */     {
/* 459 */       return this.delegate.awaitTermination(timeout, unit);
/*     */     }
/*     */     
/*     */     public boolean isShutdown()
/*     */     {
/* 464 */       return this.delegate.isShutdown();
/*     */     }
/*     */     
/*     */     public boolean isTerminated()
/*     */     {
/* 469 */       return this.delegate.isTerminated();
/*     */     }
/*     */     
/*     */     public void shutdown()
/*     */     {
/* 474 */       this.delegate.shutdown();
/*     */     }
/*     */     
/*     */     public List<Runnable> shutdownNow()
/*     */     {
/* 479 */       return this.delegate.shutdownNow();
/*     */     }
/*     */     
/*     */     public void execute(Runnable command)
/*     */     {
/* 484 */       this.delegate.execute(command);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ScheduledListeningDecorator extends ListeningDecorator implements ListeningScheduledExecutorService
/*     */   {
/*     */     final ScheduledExecutorService delegate;
/*     */     
/*     */     ScheduledListeningDecorator(ScheduledExecutorService delegate)
/*     */     {
/* 494 */       super();
/* 495 */       this.delegate = ((ScheduledExecutorService)Preconditions.checkNotNull(delegate));
/*     */     }
/*     */     
/*     */ 
/*     */     public ListenableScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit)
/*     */     {
/* 501 */       ListenableFutureTask<Void> task = ListenableFutureTask.create(command, null);
/*     */       
/* 503 */       ScheduledFuture<?> scheduled = this.delegate.schedule(task, delay, unit);
/* 504 */       return new ListenableScheduledTask(task, scheduled);
/*     */     }
/*     */     
/*     */ 
/*     */     public <V> ListenableScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit)
/*     */     {
/* 510 */       ListenableFutureTask<V> task = ListenableFutureTask.create(callable);
/* 511 */       ScheduledFuture<?> scheduled = this.delegate.schedule(task, delay, unit);
/* 512 */       return new ListenableScheduledTask(task, scheduled);
/*     */     }
/*     */     
/*     */ 
/*     */     public ListenableScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
/*     */     {
/* 518 */       NeverSuccessfulListenableFutureTask task = new NeverSuccessfulListenableFutureTask(command);
/*     */       
/* 520 */       ScheduledFuture<?> scheduled = this.delegate.scheduleAtFixedRate(task, initialDelay, period, unit);
/*     */       
/* 522 */       return new ListenableScheduledTask(task, scheduled);
/*     */     }
/*     */     
/*     */ 
/*     */     public ListenableScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)
/*     */     {
/* 528 */       NeverSuccessfulListenableFutureTask task = new NeverSuccessfulListenableFutureTask(command);
/*     */       
/* 530 */       ScheduledFuture<?> scheduled = this.delegate.scheduleWithFixedDelay(task, initialDelay, delay, unit);
/*     */       
/* 532 */       return new ListenableScheduledTask(task, scheduled);
/*     */     }
/*     */     
/*     */ 
/*     */     private static final class ListenableScheduledTask<V>
/*     */       extends ForwardingListenableFuture.SimpleForwardingListenableFuture<V>
/*     */       implements ListenableScheduledFuture<V>
/*     */     {
/*     */       private final ScheduledFuture<?> scheduledDelegate;
/*     */       
/*     */       public ListenableScheduledTask(ListenableFuture<V> listenableDelegate, ScheduledFuture<?> scheduledDelegate)
/*     */       {
/* 544 */         super();
/* 545 */         this.scheduledDelegate = scheduledDelegate;
/*     */       }
/*     */       
/*     */       public boolean cancel(boolean mayInterruptIfRunning)
/*     */       {
/* 550 */         boolean cancelled = super.cancel(mayInterruptIfRunning);
/* 551 */         if (cancelled)
/*     */         {
/* 553 */           this.scheduledDelegate.cancel(mayInterruptIfRunning);
/*     */         }
/*     */         
/*     */ 
/* 557 */         return cancelled;
/*     */       }
/*     */       
/*     */       public long getDelay(TimeUnit unit)
/*     */       {
/* 562 */         return this.scheduledDelegate.getDelay(unit);
/*     */       }
/*     */       
/*     */       public int compareTo(Delayed other)
/*     */       {
/* 567 */         return this.scheduledDelegate.compareTo(other);
/*     */       }
/*     */     }
/*     */     
/*     */     private static final class NeverSuccessfulListenableFutureTask extends AbstractFuture<Void> implements Runnable
/*     */     {
/*     */       private final Runnable delegate;
/*     */       
/*     */       public NeverSuccessfulListenableFutureTask(Runnable delegate)
/*     */       {
/* 577 */         this.delegate = ((Runnable)Preconditions.checkNotNull(delegate));
/*     */       }
/*     */       
/*     */       public void run() {
/*     */         try {
/* 582 */           this.delegate.run();
/*     */         } catch (Throwable t) {
/* 584 */           setException(t);
/* 585 */           throw Throwables.propagate(t);
/*     */         }
/*     */       }
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static <T> T invokeAnyImpl(ListeningExecutorService executorService, Collection<? extends Callable<T>> tasks, boolean timed, long nanos)
/*     */     throws InterruptedException, ExecutionException, TimeoutException
/*     */   {
/* 609 */     Preconditions.checkNotNull(executorService);
/* 610 */     int ntasks = tasks.size();
/* 611 */     Preconditions.checkArgument(ntasks > 0);
/* 612 */     List<Future<T>> futures = Lists.newArrayListWithCapacity(ntasks);
/* 613 */     BlockingQueue<Future<T>> futureQueue = Queues.newLinkedBlockingQueue();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 624 */       ExecutionException ee = null;
/* 625 */       long lastTime = timed ? System.nanoTime() : 0L;
/* 626 */       Iterator<? extends Callable<T>> it = tasks.iterator();
/*     */       
/* 628 */       futures.add(submitAndAddQueueListener(executorService, (Callable)it.next(), futureQueue));
/* 629 */       ntasks--;
/* 630 */       int active = 1;
/*     */       for (;;)
/*     */       {
/* 633 */         Future<T> f = (Future)futureQueue.poll();
/* 634 */         long now; if (f == null)
/* 635 */           if (ntasks > 0) {
/* 636 */             ntasks--;
/* 637 */             futures.add(submitAndAddQueueListener(executorService, (Callable)it.next(), futureQueue));
/* 638 */             active++;
/* 639 */           } else { if (active == 0)
/*     */               break;
/* 641 */             if (timed) {
/* 642 */               f = (Future)futureQueue.poll(nanos, TimeUnit.NANOSECONDS);
/* 643 */               if (f == null) {
/* 644 */                 throw new TimeoutException();
/*     */               }
/* 646 */               now = System.nanoTime();
/* 647 */               nanos -= now - lastTime;
/* 648 */               lastTime = now;
/*     */             } else {
/* 650 */               f = (Future)futureQueue.take();
/*     */             }
/*     */           }
/* 653 */         if (f != null) {
/* 654 */           active--;
/*     */           try { Iterator i$;
/* 656 */             Future<T> f; return (T)f.get();
/*     */           } catch (ExecutionException eex) {
/* 658 */             ee = eex;
/*     */           } catch (RuntimeException rex) {
/* 660 */             ee = new ExecutionException(rex);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 665 */       if (ee == null) {
/* 666 */         ee = new ExecutionException(null);
/*     */       }
/* 668 */       throw ee;
/*     */     } finally {
/* 670 */       for (Future<T> f : futures) {
/* 671 */         f.cancel(true);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static <T> ListenableFuture<T> submitAndAddQueueListener(ListeningExecutorService executorService, Callable<T> task, BlockingQueue<Future<T>> queue)
/*     */   {
/* 682 */     final ListenableFuture<T> future = executorService.submit(task);
/* 683 */     future.addListener(new Runnable()
/*     */     {
/* 685 */       public void run() { this.val$queue.add(future); } }, sameThreadExecutor());
/*     */     
/*     */ 
/* 688 */     return future;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Beta
/*     */   public static ThreadFactory platformThreadFactory()
/*     */   {
/* 701 */     if (!isAppEngine()) {
/* 702 */       return Executors.defaultThreadFactory();
/*     */     }
/*     */     try {
/* 705 */       return (ThreadFactory)Class.forName("com.google.appengine.api.ThreadManager").getMethod("currentRequestThreadFactory", new Class[0]).invoke(null, new Object[0]);
/*     */     }
/*     */     catch (IllegalAccessException e)
/*     */     {
/* 709 */       throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", e);
/*     */     } catch (ClassNotFoundException e) {
/* 711 */       throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", e);
/*     */     } catch (NoSuchMethodException e) {
/* 713 */       throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", e);
/*     */     } catch (InvocationTargetException e) {
/* 715 */       throw Throwables.propagate(e.getCause());
/*     */     }
/*     */   }
/*     */   
/*     */   private static boolean isAppEngine() {
/* 720 */     if (System.getProperty("com.google.appengine.runtime.environment") == null) {
/* 721 */       return false;
/*     */     }
/*     */     try
/*     */     {
/* 725 */       return Class.forName("com.google.apphosting.api.ApiProxy").getMethod("getCurrentEnvironment", new Class[0]).invoke(null, new Object[0]) != null;
/*     */ 
/*     */     }
/*     */     catch (ClassNotFoundException e)
/*     */     {
/* 730 */       return false;
/*     */     }
/*     */     catch (InvocationTargetException e) {
/* 733 */       return false;
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 736 */       return false;
/*     */     }
/*     */     catch (NoSuchMethodException e) {}
/* 739 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static Thread newThread(String name, Runnable runnable)
/*     */   {
/* 748 */     Preconditions.checkNotNull(name);
/* 749 */     Preconditions.checkNotNull(runnable);
/* 750 */     Thread result = platformThreadFactory().newThread(runnable);
/*     */     try {
/* 752 */       result.setName(name);
/*     */     }
/*     */     catch (SecurityException e) {}
/*     */     
/* 756 */     return result;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static Executor renamingDecorator(Executor executor, final Supplier<String> nameSupplier)
/*     */   {
/* 774 */     Preconditions.checkNotNull(executor);
/* 775 */     Preconditions.checkNotNull(nameSupplier);
/* 776 */     if (isAppEngine())
/*     */     {
/* 778 */       return executor;
/*     */     }
/* 780 */     new Executor() {
/*     */       public void execute(Runnable command) {
/* 782 */         this.val$executor.execute(Callables.threadRenaming(command, nameSupplier));
/*     */       }
/*     */     };
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
/*     */ 
/*     */ 
/*     */ 
/*     */   static ExecutorService renamingDecorator(ExecutorService service, final Supplier<String> nameSupplier)
/*     */   {
/* 800 */     Preconditions.checkNotNull(service);
/* 801 */     Preconditions.checkNotNull(nameSupplier);
/* 802 */     if (isAppEngine())
/*     */     {
/* 804 */       return service;
/*     */     }
/* 806 */     new WrappingExecutorService(service) {
/*     */       protected <T> Callable<T> wrapTask(Callable<T> callable) {
/* 808 */         return Callables.threadRenaming(callable, nameSupplier);
/*     */       }
/*     */       
/* 811 */       protected Runnable wrapTask(Runnable command) { return Callables.threadRenaming(command, nameSupplier); }
/*     */     };
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static ScheduledExecutorService renamingDecorator(ScheduledExecutorService service, final Supplier<String> nameSupplier)
/*     */   {
/* 829 */     Preconditions.checkNotNull(service);
/* 830 */     Preconditions.checkNotNull(nameSupplier);
/* 831 */     if (isAppEngine())
/*     */     {
/* 833 */       return service;
/*     */     }
/* 835 */     new WrappingScheduledExecutorService(service) {
/*     */       protected <T> Callable<T> wrapTask(Callable<T> callable) {
/* 837 */         return Callables.threadRenaming(callable, nameSupplier);
/*     */       }
/*     */       
/* 840 */       protected Runnable wrapTask(Runnable command) { return Callables.threadRenaming(command, nameSupplier); }
/*     */     };
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\util\concurrent\MoreExecutors.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */