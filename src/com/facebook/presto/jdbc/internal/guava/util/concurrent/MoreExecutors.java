/*     */ package com.facebook.presto.jdbc.internal.guava.util.concurrent;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.VisibleForTesting;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Supplier;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Throwables;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Lists;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Queues;
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
/* 195 */       String str = String.valueOf(String.valueOf(service));addShutdownHook(MoreExecutors.newThread(24 + str.length() + "DelayedShutdownHook-for-" + str, new Runnable()
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
/*     */ 
/*     */   @Deprecated
/*     */   public static ListeningExecutorService sameThreadExecutor()
/*     */   {
/* 270 */     return new DirectExecutorService(null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class DirectExecutorService
/*     */     extends AbstractListeningExecutorService
/*     */   {
/* 280 */     private final Lock lock = new ReentrantLock();
/*     */     
/*     */ 
/* 283 */     private final Condition termination = this.lock.newCondition();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 292 */     private int runningTasks = 0;
/* 293 */     private boolean shutdown = false;
/*     */     
/*     */     public void execute(Runnable command)
/*     */     {
/* 297 */       startTask();
/*     */       try {
/* 299 */         command.run();
/*     */       } finally {
/* 301 */         endTask();
/*     */       }
/*     */     }
/*     */     
/*     */     public boolean isShutdown()
/*     */     {
/* 307 */       this.lock.lock();
/*     */       try {
/* 309 */         return this.shutdown;
/*     */       } finally {
/* 311 */         this.lock.unlock();
/*     */       }
/*     */     }
/*     */     
/*     */     public void shutdown()
/*     */     {
/* 317 */       this.lock.lock();
/*     */       try {
/* 319 */         this.shutdown = true;
/*     */       } finally {
/* 321 */         this.lock.unlock();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public List<Runnable> shutdownNow()
/*     */     {
/* 328 */       shutdown();
/* 329 */       return Collections.emptyList();
/*     */     }
/*     */     
/*     */     public boolean isTerminated()
/*     */     {
/* 334 */       this.lock.lock();
/*     */       try {
/* 336 */         return (this.shutdown) && (this.runningTasks == 0);
/*     */       } finally {
/* 338 */         this.lock.unlock();
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
/*     */       //   2: invokevirtual 85	java/util/concurrent/TimeUnit:toNanos	(J)J
/*     */       //   5: lstore 4
/*     */       //   7: aload_0
/*     */       //   8: getfield 27	com/facebook/presto/jdbc/internal/guava/util/concurrent/MoreExecutors$DirectExecutorService:lock	Ljava/util/concurrent/locks/Lock;
/*     */       //   11: invokeinterface 62 1 0
/*     */       //   16: aload_0
/*     */       //   17: invokevirtual 87	com/facebook/presto/jdbc/internal/guava/util/concurrent/MoreExecutors$DirectExecutorService:isTerminated	()Z
/*     */       //   20: ifeq +18 -> 38
/*     */       //   23: iconst_1
/*     */       //   24: istore 6
/*     */       //   26: aload_0
/*     */       //   27: getfield 27	com/facebook/presto/jdbc/internal/guava/util/concurrent/MoreExecutors$DirectExecutorService:lock	Ljava/util/concurrent/locks/Lock;
/*     */       //   30: invokeinterface 65 1 0
/*     */       //   35: iload 6
/*     */       //   37: ireturn
/*     */       //   38: lload 4
/*     */       //   40: lconst_0
/*     */       //   41: lcmp
/*     */       //   42: ifgt +18 -> 60
/*     */       //   45: iconst_0
/*     */       //   46: istore 6
/*     */       //   48: aload_0
/*     */       //   49: getfield 27	com/facebook/presto/jdbc/internal/guava/util/concurrent/MoreExecutors$DirectExecutorService:lock	Ljava/util/concurrent/locks/Lock;
/*     */       //   52: invokeinterface 65 1 0
/*     */       //   57: iload 6
/*     */       //   59: ireturn
/*     */       //   60: aload_0
/*     */       //   61: getfield 35	com/facebook/presto/jdbc/internal/guava/util/concurrent/MoreExecutors$DirectExecutorService:termination	Ljava/util/concurrent/locks/Condition;
/*     */       //   64: lload 4
/*     */       //   66: invokeinterface 92 3 0
/*     */       //   71: lstore 4
/*     */       //   73: goto -57 -> 16
/*     */       //   76: astore 7
/*     */       //   78: aload_0
/*     */       //   79: getfield 27	com/facebook/presto/jdbc/internal/guava/util/concurrent/MoreExecutors$DirectExecutorService:lock	Ljava/util/concurrent/locks/Lock;
/*     */       //   82: invokeinterface 65 1 0
/*     */       //   87: aload 7
/*     */       //   89: athrow
/*     */       // Line number table:
/*     */       //   Java source line #345	-> byte code offset #0
/*     */       //   Java source line #346	-> byte code offset #7
/*     */       //   Java source line #349	-> byte code offset #16
/*     */       //   Java source line #350	-> byte code offset #23
/*     */       //   Java source line #358	-> byte code offset #26
/*     */       //   Java source line #351	-> byte code offset #38
/*     */       //   Java source line #352	-> byte code offset #45
/*     */       //   Java source line #358	-> byte code offset #48
/*     */       //   Java source line #354	-> byte code offset #60
/*     */       //   Java source line #358	-> byte code offset #76
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	90	0	this	DirectExecutorService
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
/* 370 */       this.lock.lock();
/*     */       try {
/* 372 */         if (isShutdown()) {
/* 373 */           throw new RejectedExecutionException("Executor already shutdown");
/*     */         }
/* 375 */         this.runningTasks += 1;
/*     */       } finally {
/* 377 */         this.lock.unlock();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private void endTask()
/*     */     {
/* 385 */       this.lock.lock();
/*     */       try {
/* 387 */         this.runningTasks -= 1;
/* 388 */         if (isTerminated()) {
/* 389 */           this.termination.signalAll();
/*     */         }
/*     */       } finally {
/* 392 */         this.lock.unlock();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ListeningExecutorService newDirectExecutorService()
/*     */   {
/* 430 */     return new DirectExecutorService(null);
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
/*     */   public static Executor directExecutor()
/*     */   {
/* 450 */     return DirectExecutor.INSTANCE;
/*     */   }
/*     */   
/*     */   private static enum DirectExecutor implements Executor
/*     */   {
/* 455 */     INSTANCE;
/*     */     private DirectExecutor() {}
/* 457 */     public void execute(Runnable command) { command.run(); }
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
/*     */   public static ListeningExecutorService listeningDecorator(ExecutorService delegate)
/*     */   {
/* 481 */     return (delegate instanceof ScheduledExecutorService) ? new ScheduledListeningDecorator((ScheduledExecutorService)delegate) : (delegate instanceof ListeningExecutorService) ? (ListeningExecutorService)delegate : new ListeningDecorator(delegate);
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
/* 509 */     return (delegate instanceof ListeningScheduledExecutorService) ? (ListeningScheduledExecutorService)delegate : new ScheduledListeningDecorator(delegate);
/*     */   }
/*     */   
/*     */   private static class ListeningDecorator
/*     */     extends AbstractListeningExecutorService
/*     */   {
/*     */     private final ExecutorService delegate;
/*     */     
/*     */     ListeningDecorator(ExecutorService delegate)
/*     */     {
/* 519 */       this.delegate = ((ExecutorService)Preconditions.checkNotNull(delegate));
/*     */     }
/*     */     
/*     */     public boolean awaitTermination(long timeout, TimeUnit unit)
/*     */       throws InterruptedException
/*     */     {
/* 525 */       return this.delegate.awaitTermination(timeout, unit);
/*     */     }
/*     */     
/*     */     public boolean isShutdown()
/*     */     {
/* 530 */       return this.delegate.isShutdown();
/*     */     }
/*     */     
/*     */     public boolean isTerminated()
/*     */     {
/* 535 */       return this.delegate.isTerminated();
/*     */     }
/*     */     
/*     */     public void shutdown()
/*     */     {
/* 540 */       this.delegate.shutdown();
/*     */     }
/*     */     
/*     */     public List<Runnable> shutdownNow()
/*     */     {
/* 545 */       return this.delegate.shutdownNow();
/*     */     }
/*     */     
/*     */     public void execute(Runnable command)
/*     */     {
/* 550 */       this.delegate.execute(command);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ScheduledListeningDecorator extends ListeningDecorator implements ListeningScheduledExecutorService
/*     */   {
/*     */     final ScheduledExecutorService delegate;
/*     */     
/*     */     ScheduledListeningDecorator(ScheduledExecutorService delegate)
/*     */     {
/* 560 */       super();
/* 561 */       this.delegate = ((ScheduledExecutorService)Preconditions.checkNotNull(delegate));
/*     */     }
/*     */     
/*     */ 
/*     */     public ListenableScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit)
/*     */     {
/* 567 */       ListenableFutureTask<Void> task = ListenableFutureTask.create(command, null);
/*     */       
/* 569 */       ScheduledFuture<?> scheduled = this.delegate.schedule(task, delay, unit);
/* 570 */       return new ListenableScheduledTask(task, scheduled);
/*     */     }
/*     */     
/*     */ 
/*     */     public <V> ListenableScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit)
/*     */     {
/* 576 */       ListenableFutureTask<V> task = ListenableFutureTask.create(callable);
/* 577 */       ScheduledFuture<?> scheduled = this.delegate.schedule(task, delay, unit);
/* 578 */       return new ListenableScheduledTask(task, scheduled);
/*     */     }
/*     */     
/*     */ 
/*     */     public ListenableScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
/*     */     {
/* 584 */       NeverSuccessfulListenableFutureTask task = new NeverSuccessfulListenableFutureTask(command);
/*     */       
/* 586 */       ScheduledFuture<?> scheduled = this.delegate.scheduleAtFixedRate(task, initialDelay, period, unit);
/*     */       
/* 588 */       return new ListenableScheduledTask(task, scheduled);
/*     */     }
/*     */     
/*     */ 
/*     */     public ListenableScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)
/*     */     {
/* 594 */       NeverSuccessfulListenableFutureTask task = new NeverSuccessfulListenableFutureTask(command);
/*     */       
/* 596 */       ScheduledFuture<?> scheduled = this.delegate.scheduleWithFixedDelay(task, initialDelay, delay, unit);
/*     */       
/* 598 */       return new ListenableScheduledTask(task, scheduled);
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
/* 610 */         super();
/* 611 */         this.scheduledDelegate = scheduledDelegate;
/*     */       }
/*     */       
/*     */       public boolean cancel(boolean mayInterruptIfRunning)
/*     */       {
/* 616 */         boolean cancelled = super.cancel(mayInterruptIfRunning);
/* 617 */         if (cancelled)
/*     */         {
/* 619 */           this.scheduledDelegate.cancel(mayInterruptIfRunning);
/*     */         }
/*     */         
/*     */ 
/* 623 */         return cancelled;
/*     */       }
/*     */       
/*     */       public long getDelay(TimeUnit unit)
/*     */       {
/* 628 */         return this.scheduledDelegate.getDelay(unit);
/*     */       }
/*     */       
/*     */       public int compareTo(Delayed other)
/*     */       {
/* 633 */         return this.scheduledDelegate.compareTo(other);
/*     */       }
/*     */     }
/*     */     
/*     */     private static final class NeverSuccessfulListenableFutureTask extends AbstractFuture<Void> implements Runnable
/*     */     {
/*     */       private final Runnable delegate;
/*     */       
/*     */       public NeverSuccessfulListenableFutureTask(Runnable delegate)
/*     */       {
/* 643 */         this.delegate = ((Runnable)Preconditions.checkNotNull(delegate));
/*     */       }
/*     */       
/*     */       public void run() {
/*     */         try {
/* 648 */           this.delegate.run();
/*     */         } catch (Throwable t) {
/* 650 */           setException(t);
/* 651 */           throw Throwables.propagate(t);
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
/* 675 */     Preconditions.checkNotNull(executorService);
/* 676 */     int ntasks = tasks.size();
/* 677 */     Preconditions.checkArgument(ntasks > 0);
/* 678 */     List<Future<T>> futures = Lists.newArrayListWithCapacity(ntasks);
/* 679 */     BlockingQueue<Future<T>> futureQueue = Queues.newLinkedBlockingQueue();
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
/* 690 */       ExecutionException ee = null;
/* 691 */       long lastTime = timed ? System.nanoTime() : 0L;
/* 692 */       Iterator<? extends Callable<T>> it = tasks.iterator();
/*     */       
/* 694 */       futures.add(submitAndAddQueueListener(executorService, (Callable)it.next(), futureQueue));
/* 695 */       ntasks--;
/* 696 */       int active = 1;
/*     */       for (;;)
/*     */       {
/* 699 */         Future<T> f = (Future)futureQueue.poll();
/* 700 */         if (f == null)
/* 701 */           if (ntasks > 0) {
/* 702 */             ntasks--;
/* 703 */             futures.add(submitAndAddQueueListener(executorService, (Callable)it.next(), futureQueue));
/* 704 */             active++;
/* 705 */           } else { if (active == 0)
/*     */               break;
/* 707 */             if (timed) {
/* 708 */               f = (Future)futureQueue.poll(nanos, TimeUnit.NANOSECONDS);
/* 709 */               if (f == null) {
/* 710 */                 throw new TimeoutException();
/*     */               }
/* 712 */               long now = System.nanoTime();
/* 713 */               nanos -= now - lastTime;
/* 714 */               lastTime = now;
/*     */             } else {
/* 716 */               f = (Future)futureQueue.take();
/*     */             }
/*     */           }
/* 719 */         if (f != null) {
/* 720 */           active--;
/*     */           try { Iterator i$;
/* 722 */             Future<T> f; return (T)f.get();
/*     */           } catch (ExecutionException eex) {
/* 724 */             ee = eex;
/*     */           } catch (RuntimeException rex) {
/* 726 */             ee = new ExecutionException(rex);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 731 */       if (ee == null) {
/* 732 */         ee = new ExecutionException(null);
/*     */       }
/* 734 */       throw ee;
/*     */     } finally {
/* 736 */       for (Future<T> f : futures) {
/* 737 */         f.cancel(true);
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
/* 748 */     final ListenableFuture<T> future = executorService.submit(task);
/* 749 */     future.addListener(new Runnable()
/*     */     {
/* 751 */       public void run() { this.val$queue.add(future); } }, directExecutor());
/*     */     
/*     */ 
/* 754 */     return future;
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
/* 767 */     if (!isAppEngine()) {
/* 768 */       return Executors.defaultThreadFactory();
/*     */     }
/*     */     try {
/* 771 */       return (ThreadFactory)Class.forName("com.google.appengine.api.ThreadManager").getMethod("currentRequestThreadFactory", new Class[0]).invoke(null, new Object[0]);
/*     */     }
/*     */     catch (IllegalAccessException e)
/*     */     {
/* 775 */       throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", e);
/*     */     } catch (ClassNotFoundException e) {
/* 777 */       throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", e);
/*     */     } catch (NoSuchMethodException e) {
/* 779 */       throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", e);
/*     */     } catch (InvocationTargetException e) {
/* 781 */       throw Throwables.propagate(e.getCause());
/*     */     }
/*     */   }
/*     */   
/*     */   private static boolean isAppEngine() {
/* 786 */     if (System.getProperty("com.google.appengine.runtime.environment") == null) {
/* 787 */       return false;
/*     */     }
/*     */     try
/*     */     {
/* 791 */       return Class.forName("com.google.apphosting.api.ApiProxy").getMethod("getCurrentEnvironment", new Class[0]).invoke(null, new Object[0]) != null;
/*     */ 
/*     */     }
/*     */     catch (ClassNotFoundException e)
/*     */     {
/* 796 */       return false;
/*     */     }
/*     */     catch (InvocationTargetException e) {
/* 799 */       return false;
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 802 */       return false;
/*     */     }
/*     */     catch (NoSuchMethodException e) {}
/* 805 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   static Thread newThread(String name, Runnable runnable)
/*     */   {
/* 814 */     Preconditions.checkNotNull(name);
/* 815 */     Preconditions.checkNotNull(runnable);
/* 816 */     Thread result = platformThreadFactory().newThread(runnable);
/*     */     try {
/* 818 */       result.setName(name);
/*     */     }
/*     */     catch (SecurityException e) {}
/*     */     
/* 822 */     return result;
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
/*     */   static Executor renamingDecorator(Executor executor, final Supplier<String> nameSupplier)
/*     */   {
/* 841 */     Preconditions.checkNotNull(executor);
/* 842 */     Preconditions.checkNotNull(nameSupplier);
/* 843 */     if (isAppEngine())
/*     */     {
/* 845 */       return executor;
/*     */     }
/* 847 */     new Executor() {
/*     */       public void execute(Runnable command) {
/* 849 */         this.val$executor.execute(Callables.threadRenaming(command, nameSupplier));
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
/*     */ 
/*     */   static ExecutorService renamingDecorator(ExecutorService service, final Supplier<String> nameSupplier)
/*     */   {
/* 868 */     Preconditions.checkNotNull(service);
/* 869 */     Preconditions.checkNotNull(nameSupplier);
/* 870 */     if (isAppEngine())
/*     */     {
/* 872 */       return service;
/*     */     }
/* 874 */     new WrappingExecutorService(service) {
/*     */       protected <T> Callable<T> wrapTask(Callable<T> callable) {
/* 876 */         return Callables.threadRenaming(callable, nameSupplier);
/*     */       }
/*     */       
/* 879 */       protected Runnable wrapTask(Runnable command) { return Callables.threadRenaming(command, nameSupplier); }
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
/*     */ 
/*     */   static ScheduledExecutorService renamingDecorator(ScheduledExecutorService service, final Supplier<String> nameSupplier)
/*     */   {
/* 898 */     Preconditions.checkNotNull(service);
/* 899 */     Preconditions.checkNotNull(nameSupplier);
/* 900 */     if (isAppEngine())
/*     */     {
/* 902 */       return service;
/*     */     }
/* 904 */     new WrappingScheduledExecutorService(service) {
/*     */       protected <T> Callable<T> wrapTask(Callable<T> callable) {
/* 906 */         return Callables.threadRenaming(callable, nameSupplier);
/*     */       }
/*     */       
/* 909 */       protected Runnable wrapTask(Runnable command) { return Callables.threadRenaming(command, nameSupplier); }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */   public static boolean shutdownAndAwaitTermination(ExecutorService service, long timeout, TimeUnit unit)
/*     */   {
/* 942 */     Preconditions.checkNotNull(unit);
/*     */     
/* 944 */     service.shutdown();
/*     */     try {
/* 946 */       long halfTimeoutNanos = TimeUnit.NANOSECONDS.convert(timeout, unit) / 2L;
/*     */       
/* 948 */       if (!service.awaitTermination(halfTimeoutNanos, TimeUnit.NANOSECONDS))
/*     */       {
/* 950 */         service.shutdownNow();
/*     */         
/* 952 */         service.awaitTermination(halfTimeoutNanos, TimeUnit.NANOSECONDS);
/*     */       }
/*     */     }
/*     */     catch (InterruptedException ie) {
/* 956 */       Thread.currentThread().interrupt();
/*     */       
/* 958 */       service.shutdownNow();
/*     */     }
/* 960 */     return service.isTerminated();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\util\concurrent\MoreExecutors.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */