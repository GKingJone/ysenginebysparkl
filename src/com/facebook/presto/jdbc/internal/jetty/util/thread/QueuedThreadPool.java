/*     */ package com.facebook.presto.jdbc.internal.jetty.util.thread;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.BlockingArrayQueue;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.ConcurrentHashSet;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedAttribute;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedObject;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.ManagedOperation;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.annotation.Name;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.AbstractLifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.ContainerLifeCycle;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.component.Dumpable;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*     */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.RejectedExecutionException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ManagedObject("A thread pool with no max bound by default")
/*     */ public class QueuedThreadPool
/*     */   extends AbstractLifeCycle
/*     */   implements ThreadPool.SizedThreadPool, Dumpable
/*     */ {
/*  49 */   private static final Logger LOG = Log.getLogger(QueuedThreadPool.class);
/*     */   
/*  51 */   private final AtomicInteger _threadsStarted = new AtomicInteger();
/*  52 */   private final AtomicInteger _threadsIdle = new AtomicInteger();
/*  53 */   private final AtomicLong _lastShrink = new AtomicLong();
/*  54 */   private final ConcurrentHashSet<Thread> _threads = new ConcurrentHashSet();
/*  55 */   private final Object _joinLock = new Object();
/*     */   private final BlockingQueue<Runnable> _jobs;
/*     */   private final ThreadGroup _threadGroup;
/*  58 */   private String _name = "qtp" + hashCode();
/*     */   private int _idleTimeout;
/*     */   private int _maxThreads;
/*     */   private int _minThreads;
/*  62 */   private int _priority = 5;
/*  63 */   private boolean _daemon = false;
/*  64 */   private boolean _detailedDump = false;
/*     */   
/*     */   public QueuedThreadPool()
/*     */   {
/*  68 */     this(200);
/*     */   }
/*     */   
/*     */   public QueuedThreadPool(@Name("maxThreads") int maxThreads)
/*     */   {
/*  73 */     this(maxThreads, 8);
/*     */   }
/*     */   
/*     */   public QueuedThreadPool(@Name("maxThreads") int maxThreads, @Name("minThreads") int minThreads)
/*     */   {
/*  78 */     this(maxThreads, minThreads, 60000);
/*     */   }
/*     */   
/*     */   public QueuedThreadPool(@Name("maxThreads") int maxThreads, @Name("minThreads") int minThreads, @Name("idleTimeout") int idleTimeout)
/*     */   {
/*  83 */     this(maxThreads, minThreads, idleTimeout, null);
/*     */   }
/*     */   
/*     */   public QueuedThreadPool(@Name("maxThreads") int maxThreads, @Name("minThreads") int minThreads, @Name("idleTimeout") int idleTimeout, @Name("queue") BlockingQueue<Runnable> queue)
/*     */   {
/*  88 */     this(maxThreads, minThreads, idleTimeout, queue, null);
/*     */   }
/*     */   
/*     */   public QueuedThreadPool(@Name("maxThreads") int maxThreads, @Name("minThreads") int minThreads, @Name("idleTimeout") int idleTimeout, @Name("queue") BlockingQueue<Runnable> queue, @Name("threadGroup") ThreadGroup threadGroup)
/*     */   {
/*  93 */     setMinThreads(minThreads);
/*  94 */     setMaxThreads(maxThreads);
/*  95 */     setIdleTimeout(idleTimeout);
/*  96 */     setStopTimeout(5000L);
/*     */     
/*  98 */     if (queue == null)
/*     */     {
/* 100 */       int capacity = Math.max(this._minThreads, 8);
/* 101 */       queue = new BlockingArrayQueue(capacity, capacity);
/*     */     }
/* 103 */     this._jobs = queue;
/* 104 */     this._threadGroup = threadGroup;
/*     */   }
/*     */   
/*     */   protected void doStart()
/*     */     throws Exception
/*     */   {
/* 110 */     super.doStart();
/* 111 */     this._threadsStarted.set(0);
/*     */     
/* 113 */     startThreads(this._minThreads);
/*     */   }
/*     */   
/*     */   protected void doStop()
/*     */     throws Exception
/*     */   {
/* 119 */     super.doStop();
/*     */     
/* 121 */     long timeout = getStopTimeout();
/* 122 */     BlockingQueue<Runnable> jobs = getQueue();
/*     */     
/*     */ 
/* 125 */     if (timeout <= 0L) {
/* 126 */       jobs.clear();
/*     */     }
/*     */     
/* 129 */     Runnable noop = new Runnable()
/*     */     {
/*     */       public void run() {}
/*     */     };
/*     */     
/*     */ 
/*     */ 
/* 136 */     for (int i = this._threadsStarted.get(); i-- > 0;) {
/* 137 */       jobs.offer(noop);
/*     */     }
/*     */     
/* 140 */     long stopby = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeout) / 2L;
/* 141 */     for (Thread thread : this._threads)
/*     */     {
/* 143 */       long canwait = TimeUnit.NANOSECONDS.toMillis(stopby - System.nanoTime());
/* 144 */       if (canwait > 0L) {
/* 145 */         thread.join(canwait);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 151 */     if (this._threadsStarted.get() > 0) {
/* 152 */       for (Thread thread : this._threads) {
/* 153 */         thread.interrupt();
/*     */       }
/*     */     }
/* 156 */     stopby = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeout) / 2L;
/* 157 */     for (??? = this._threads.iterator(); ???.hasNext();) { thread = (Thread)???.next();
/*     */       
/* 159 */       long canwait = TimeUnit.NANOSECONDS.toMillis(stopby - System.nanoTime());
/* 160 */       if (canwait > 0L)
/* 161 */         thread.join(canwait);
/*     */     }
/*     */     Thread thread;
/* 164 */     Thread.yield();
/* 165 */     int size = this._threads.size();
/* 166 */     if (size > 0)
/*     */     {
/* 168 */       Thread.yield();
/*     */       
/* 170 */       if (LOG.isDebugEnabled())
/*     */       {
/* 172 */         for (Thread unstopped : this._threads)
/*     */         {
/* 174 */           StringBuilder dmp = new StringBuilder();
/* 175 */           for (StackTraceElement element : unstopped.getStackTrace())
/*     */           {
/* 177 */             dmp.append(System.lineSeparator()).append("\tat ").append(element);
/*     */           }
/* 179 */           LOG.warn("Couldn't stop {}{}", new Object[] { unstopped, dmp.toString() });
/*     */         }
/*     */         
/*     */       }
/*     */       else {
/* 184 */         for (Thread unstopped : this._threads) {
/* 185 */           LOG.warn("{} Couldn't stop {}", new Object[] { this, unstopped });
/*     */         }
/*     */       }
/*     */     }
/* 189 */     synchronized (this._joinLock)
/*     */     {
/* 191 */       this._joinLock.notifyAll();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDaemon(boolean daemon)
/*     */   {
/* 203 */     this._daemon = daemon;
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
/*     */   public void setIdleTimeout(int idleTimeout)
/*     */   {
/* 217 */     this._idleTimeout = idleTimeout;
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
/*     */   public void setMaxThreads(int maxThreads)
/*     */   {
/* 230 */     this._maxThreads = maxThreads;
/* 231 */     if (this._minThreads > this._maxThreads) {
/* 232 */       this._minThreads = this._maxThreads;
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
/*     */   public void setMinThreads(int minThreads)
/*     */   {
/* 245 */     this._minThreads = minThreads;
/*     */     
/* 247 */     if (this._minThreads > this._maxThreads) {
/* 248 */       this._maxThreads = this._minThreads;
/*     */     }
/* 250 */     int threads = this._threadsStarted.get();
/* 251 */     if ((isStarted()) && (threads < this._minThreads)) {
/* 252 */       startThreads(this._minThreads - threads);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setName(String name)
/*     */   {
/* 260 */     if (isRunning())
/* 261 */       throw new IllegalStateException("started");
/* 262 */     this._name = name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setThreadsPriority(int priority)
/*     */   {
/* 272 */     this._priority = priority;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @ManagedAttribute("maximum time a thread may be idle in ms")
/*     */   public int getIdleTimeout()
/*     */   {
/* 285 */     return this._idleTimeout;
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
/*     */   @ManagedAttribute("maximum number of threads in the pool")
/*     */   public int getMaxThreads()
/*     */   {
/* 299 */     return this._maxThreads;
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
/*     */   @ManagedAttribute("minimum number of threads in the pool")
/*     */   public int getMinThreads()
/*     */   {
/* 313 */     return this._minThreads;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @ManagedAttribute("name of the thread pool")
/*     */   public String getName()
/*     */   {
/* 322 */     return this._name;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @ManagedAttribute("priority of threads in the pool")
/*     */   public int getThreadsPriority()
/*     */   {
/* 333 */     return this._priority;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @ManagedAttribute("Size of the job queue")
/*     */   public int getQueueSize()
/*     */   {
/* 344 */     return this._jobs.size();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @ManagedAttribute("thead pool using a daemon thread")
/*     */   public boolean isDaemon()
/*     */   {
/* 356 */     return this._daemon;
/*     */   }
/*     */   
/*     */   public boolean isDetailedDump()
/*     */   {
/* 361 */     return this._detailedDump;
/*     */   }
/*     */   
/*     */   public void setDetailedDump(boolean detailedDump)
/*     */   {
/* 366 */     this._detailedDump = detailedDump;
/*     */   }
/*     */   
/*     */ 
/*     */   public void execute(Runnable job)
/*     */   {
/* 372 */     if (LOG.isDebugEnabled())
/* 373 */       LOG.debug("queue {}", new Object[] { job });
/* 374 */     if ((!isRunning()) || (!this._jobs.offer(job)))
/*     */     {
/* 376 */       LOG.warn("{} rejected {}", new Object[] { this, job });
/* 377 */       throw new RejectedExecutionException(job.toString());
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 382 */     if (getThreads() == 0) {
/* 383 */       startThreads(1);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void join()
/*     */     throws InterruptedException
/*     */   {
/* 393 */     synchronized (this._joinLock)
/*     */     {
/* 395 */       while (isRunning()) {
/* 396 */         this._joinLock.wait();
/*     */       }
/*     */     }
/* 399 */     while (isStopping()) {
/* 400 */       Thread.sleep(1L);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @ManagedAttribute("total number of threads currently in the pool")
/*     */   public int getThreads()
/*     */   {
/* 410 */     return this._threadsStarted.get();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @ManagedAttribute("total number of idle threads in the pool")
/*     */   public int getIdleThreads()
/*     */   {
/* 420 */     return this._threadsIdle.get();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @ManagedAttribute("total number of busy threads in the pool")
/*     */   public int getBusyThreads()
/*     */   {
/* 429 */     return getThreads() - getIdleThreads();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @ManagedAttribute("True if the pools is at maxThreads and there are not idle threads than queued jobs")
/*     */   public boolean isLowOnThreads()
/*     */   {
/* 439 */     return (this._threadsStarted.get() == this._maxThreads) && (this._jobs.size() >= this._threadsIdle.get());
/*     */   }
/*     */   
/*     */   private boolean startThreads(int threadsToStart)
/*     */   {
/* 444 */     while ((threadsToStart > 0) && (isRunning()))
/*     */     {
/* 446 */       int threads = this._threadsStarted.get();
/* 447 */       if (threads >= this._maxThreads) {
/* 448 */         return false;
/*     */       }
/* 450 */       if (this._threadsStarted.compareAndSet(threads, threads + 1))
/*     */       {
/*     */ 
/* 453 */         boolean started = false;
/*     */         try
/*     */         {
/* 456 */           Thread thread = newThread(this._runnable);
/* 457 */           thread.setDaemon(isDaemon());
/* 458 */           thread.setPriority(getThreadsPriority());
/* 459 */           thread.setName(this._name + "-" + thread.getId());
/* 460 */           this._threads.add(thread);
/*     */           
/* 462 */           thread.start();
/* 463 */           started = true;
/* 464 */           threadsToStart--;
/*     */         }
/*     */         finally
/*     */         {
/* 468 */           if (!started)
/* 469 */             this._threadsStarted.decrementAndGet();
/*     */         }
/*     */       } }
/* 472 */     return true;
/*     */   }
/*     */   
/*     */   protected Thread newThread(Runnable runnable)
/*     */   {
/* 477 */     return new Thread(this._threadGroup, runnable);
/*     */   }
/*     */   
/*     */ 
/*     */   @ManagedOperation("dump thread state")
/*     */   public String dump()
/*     */   {
/* 484 */     return ContainerLifeCycle.dump(this);
/*     */   }
/*     */   
/*     */   public void dump(Appendable out, String indent)
/*     */     throws IOException
/*     */   {
/* 490 */     List<Object> dump = new ArrayList(getMaxThreads());
/* 491 */     for (final Thread thread : this._threads)
/*     */     {
/* 493 */       final StackTraceElement[] trace = thread.getStackTrace();
/* 494 */       boolean inIdleJobPoll = false;
/* 495 */       for (StackTraceElement t : trace)
/*     */       {
/* 497 */         if ("idleJobPoll".equals(t.getMethodName()))
/*     */         {
/* 499 */           inIdleJobPoll = true;
/* 500 */           break;
/*     */         }
/*     */       }
/* 503 */       final boolean idle = inIdleJobPoll;
/*     */       
/* 505 */       if (isDetailedDump())
/*     */       {
/* 507 */         dump.add(new Dumpable()
/*     */         {
/*     */           public void dump(Appendable out, String indent)
/*     */             throws IOException
/*     */           {
/* 512 */             out.append(String.valueOf(thread.getId())).append(' ').append(thread.getName()).append(' ').append(thread.getState().toString()).append(idle ? " IDLE" : "");
/* 513 */             if (thread.getPriority() != 5)
/* 514 */               out.append(" prio=").append(String.valueOf(thread.getPriority()));
/* 515 */             out.append(System.lineSeparator());
/* 516 */             if (!idle) {
/* 517 */               ContainerLifeCycle.dump(out, indent, new Collection[] { Arrays.asList(trace) });
/*     */             }
/*     */           }
/*     */           
/*     */           public String dump()
/*     */           {
/* 523 */             return null;
/*     */           }
/*     */         });
/*     */       }
/*     */       else
/*     */       {
/* 529 */         int p = thread.getPriority();
/* 530 */         dump.add(thread.getId() + " " + thread.getName() + " " + thread.getState() + " @ " + (trace.length > 0 ? trace[0] : "???") + (idle ? " IDLE" : "") + (p == 5 ? "" : new StringBuilder().append(" prio=").append(p).toString()));
/*     */       }
/*     */     }
/*     */     
/* 534 */     ContainerLifeCycle.dumpObject(out, this);
/* 535 */     ContainerLifeCycle.dump(out, indent, new Collection[] { dump });
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 541 */     return String.format("%s{%s,%d<=%d<=%d,i=%d,q=%d}", new Object[] { this._name, getState(), Integer.valueOf(getMinThreads()), Integer.valueOf(getThreads()), Integer.valueOf(getMaxThreads()), Integer.valueOf(getIdleThreads()), Integer.valueOf(this._jobs == null ? -1 : this._jobs.size()) });
/*     */   }
/*     */   
/*     */   private Runnable idleJobPoll() throws InterruptedException
/*     */   {
/* 546 */     return (Runnable)this._jobs.poll(this._idleTimeout, TimeUnit.MILLISECONDS);
/*     */   }
/*     */   
/* 549 */   private Runnable _runnable = new Runnable()
/*     */   {
/*     */     /* Error */
/*     */     public void run()
/*     */     {
/*     */       // Byte code:
/*     */       //   0: iconst_0
/*     */       //   1: istore_1
/*     */       //   2: iconst_0
/*     */       //   3: istore_2
/*     */       //   4: aload_0
/*     */       //   5: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   8: invokestatic 29	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$000	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)Ljava/util/concurrent/BlockingQueue;
/*     */       //   11: invokeinterface 35 1 0
/*     */       //   16: checkcast 6	java/lang/Runnable
/*     */       //   19: astore_3
/*     */       //   20: aload_3
/*     */       //   21: ifnull +25 -> 46
/*     */       //   24: aload_0
/*     */       //   25: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   28: invokestatic 39	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$100	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)Ljava/util/concurrent/atomic/AtomicInteger;
/*     */       //   31: invokevirtual 45	java/util/concurrent/atomic/AtomicInteger:get	()I
/*     */       //   34: ifne +12 -> 46
/*     */       //   37: aload_0
/*     */       //   38: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   41: iconst_1
/*     */       //   42: invokestatic 49	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$200	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;I)Z
/*     */       //   45: pop
/*     */       //   46: aload_0
/*     */       //   47: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   50: invokevirtual 53	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:isRunning	()Z
/*     */       //   53: ifeq +367 -> 420
/*     */       //   56: aload_3
/*     */       //   57: ifnull +109 -> 166
/*     */       //   60: aload_0
/*     */       //   61: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   64: invokevirtual 53	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:isRunning	()Z
/*     */       //   67: ifeq +99 -> 166
/*     */       //   70: invokestatic 57	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$300	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   73: invokeinterface 62 1 0
/*     */       //   78: ifeq +21 -> 99
/*     */       //   81: invokestatic 57	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$300	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   84: ldc 64
/*     */       //   86: iconst_1
/*     */       //   87: anewarray 4	java/lang/Object
/*     */       //   90: dup
/*     */       //   91: iconst_0
/*     */       //   92: aload_3
/*     */       //   93: aastore
/*     */       //   94: invokeinterface 68 3 0
/*     */       //   99: aload_0
/*     */       //   100: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   103: aload_3
/*     */       //   104: invokevirtual 72	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:runJob	(Ljava/lang/Runnable;)V
/*     */       //   107: invokestatic 57	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$300	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   110: invokeinterface 62 1 0
/*     */       //   115: ifeq +21 -> 136
/*     */       //   118: invokestatic 57	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$300	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   121: ldc 74
/*     */       //   123: iconst_1
/*     */       //   124: anewarray 4	java/lang/Object
/*     */       //   127: dup
/*     */       //   128: iconst_0
/*     */       //   129: aload_3
/*     */       //   130: aastore
/*     */       //   131: invokeinterface 68 3 0
/*     */       //   136: invokestatic 79	java/lang/Thread:interrupted	()Z
/*     */       //   139: ifeq +8 -> 147
/*     */       //   142: iconst_1
/*     */       //   143: istore_2
/*     */       //   144: goto +276 -> 420
/*     */       //   147: aload_0
/*     */       //   148: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   151: invokestatic 29	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$000	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)Ljava/util/concurrent/BlockingQueue;
/*     */       //   154: invokeinterface 35 1 0
/*     */       //   159: checkcast 6	java/lang/Runnable
/*     */       //   162: astore_3
/*     */       //   163: goto -107 -> 56
/*     */       //   166: aload_0
/*     */       //   167: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   170: invokestatic 39	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$100	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)Ljava/util/concurrent/atomic/AtomicInteger;
/*     */       //   173: invokevirtual 82	java/util/concurrent/atomic/AtomicInteger:incrementAndGet	()I
/*     */       //   176: pop
/*     */       //   177: aload_0
/*     */       //   178: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   181: invokevirtual 53	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:isRunning	()Z
/*     */       //   184: ifeq +181 -> 365
/*     */       //   187: aload_3
/*     */       //   188: ifnonnull +177 -> 365
/*     */       //   191: aload_0
/*     */       //   192: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   195: invokestatic 86	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$400	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)I
/*     */       //   198: ifgt +22 -> 220
/*     */       //   201: aload_0
/*     */       //   202: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   205: invokestatic 29	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$000	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)Ljava/util/concurrent/BlockingQueue;
/*     */       //   208: invokeinterface 89 1 0
/*     */       //   213: checkcast 6	java/lang/Runnable
/*     */       //   216: astore_3
/*     */       //   217: goto -40 -> 177
/*     */       //   220: aload_0
/*     */       //   221: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   224: invokestatic 92	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$500	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)Ljava/util/concurrent/atomic/AtomicInteger;
/*     */       //   227: invokevirtual 45	java/util/concurrent/atomic/AtomicInteger:get	()I
/*     */       //   230: istore 4
/*     */       //   232: iload 4
/*     */       //   234: aload_0
/*     */       //   235: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   238: invokestatic 95	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$600	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)I
/*     */       //   241: if_icmple +113 -> 354
/*     */       //   244: aload_0
/*     */       //   245: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   248: invokestatic 99	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$700	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)Ljava/util/concurrent/atomic/AtomicLong;
/*     */       //   251: invokevirtual 104	java/util/concurrent/atomic/AtomicLong:get	()J
/*     */       //   254: lstore 5
/*     */       //   256: invokestatic 109	java/lang/System:nanoTime	()J
/*     */       //   259: lstore 7
/*     */       //   261: lload 5
/*     */       //   263: lconst_0
/*     */       //   264: lcmp
/*     */       //   265: ifeq +26 -> 291
/*     */       //   268: lload 7
/*     */       //   270: lload 5
/*     */       //   272: lsub
/*     */       //   273: getstatic 115	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
/*     */       //   276: aload_0
/*     */       //   277: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   280: invokestatic 86	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$400	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)I
/*     */       //   283: i2l
/*     */       //   284: invokevirtual 119	java/util/concurrent/TimeUnit:toNanos	(J)J
/*     */       //   287: lcmp
/*     */       //   288: ifle +66 -> 354
/*     */       //   291: aload_0
/*     */       //   292: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   295: invokestatic 99	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$700	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)Ljava/util/concurrent/atomic/AtomicLong;
/*     */       //   298: lload 5
/*     */       //   300: lload 7
/*     */       //   302: invokevirtual 123	java/util/concurrent/atomic/AtomicLong:compareAndSet	(JJ)Z
/*     */       //   305: ifeq +49 -> 354
/*     */       //   308: aload_0
/*     */       //   309: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   312: invokestatic 92	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$500	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)Ljava/util/concurrent/atomic/AtomicInteger;
/*     */       //   315: iload 4
/*     */       //   317: iload 4
/*     */       //   319: iconst_1
/*     */       //   320: isub
/*     */       //   321: invokevirtual 126	java/util/concurrent/atomic/AtomicInteger:compareAndSet	(II)Z
/*     */       //   324: ifeq +30 -> 354
/*     */       //   327: iconst_1
/*     */       //   328: istore_1
/*     */       //   329: aload_0
/*     */       //   330: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   333: invokestatic 39	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$100	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)Ljava/util/concurrent/atomic/AtomicInteger;
/*     */       //   336: invokevirtual 129	java/util/concurrent/atomic/AtomicInteger:decrementAndGet	()I
/*     */       //   339: ifne +81 -> 420
/*     */       //   342: aload_0
/*     */       //   343: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   346: iconst_1
/*     */       //   347: invokestatic 49	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$200	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;I)Z
/*     */       //   350: pop
/*     */       //   351: goto +69 -> 420
/*     */       //   354: aload_0
/*     */       //   355: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   358: invokestatic 133	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$800	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)Ljava/lang/Runnable;
/*     */       //   361: astore_3
/*     */       //   362: goto -185 -> 177
/*     */       //   365: aload_0
/*     */       //   366: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   369: invokestatic 39	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$100	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)Ljava/util/concurrent/atomic/AtomicInteger;
/*     */       //   372: invokevirtual 129	java/util/concurrent/atomic/AtomicInteger:decrementAndGet	()I
/*     */       //   375: ifne +42 -> 417
/*     */       //   378: aload_0
/*     */       //   379: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   382: iconst_1
/*     */       //   383: invokestatic 49	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$200	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;I)Z
/*     */       //   386: pop
/*     */       //   387: goto +30 -> 417
/*     */       //   390: astore 9
/*     */       //   392: aload_0
/*     */       //   393: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   396: invokestatic 39	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$100	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)Ljava/util/concurrent/atomic/AtomicInteger;
/*     */       //   399: invokevirtual 129	java/util/concurrent/atomic/AtomicInteger:decrementAndGet	()I
/*     */       //   402: ifne +12 -> 414
/*     */       //   405: aload_0
/*     */       //   406: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   409: iconst_1
/*     */       //   410: invokestatic 49	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$200	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;I)Z
/*     */       //   413: pop
/*     */       //   414: aload 9
/*     */       //   416: athrow
/*     */       //   417: goto -371 -> 46
/*     */       //   420: iload_1
/*     */       //   421: ifne +71 -> 492
/*     */       //   424: aload_0
/*     */       //   425: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   428: invokevirtual 53	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:isRunning	()Z
/*     */       //   431: ifeq +61 -> 492
/*     */       //   434: iload_2
/*     */       //   435: ifne +28 -> 463
/*     */       //   438: invokestatic 57	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$300	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   441: ldc -121
/*     */       //   443: iconst_2
/*     */       //   444: anewarray 4	java/lang/Object
/*     */       //   447: dup
/*     */       //   448: iconst_0
/*     */       //   449: aload_0
/*     */       //   450: aastore
/*     */       //   451: dup
/*     */       //   452: iconst_1
/*     */       //   453: aload_0
/*     */       //   454: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   457: aastore
/*     */       //   458: invokeinterface 138 3 0
/*     */       //   463: aload_0
/*     */       //   464: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   467: invokestatic 92	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$500	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)Ljava/util/concurrent/atomic/AtomicInteger;
/*     */       //   470: invokevirtual 129	java/util/concurrent/atomic/AtomicInteger:decrementAndGet	()I
/*     */       //   473: aload_0
/*     */       //   474: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   477: invokevirtual 141	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:getMaxThreads	()I
/*     */       //   480: if_icmpge +12 -> 492
/*     */       //   483: aload_0
/*     */       //   484: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   487: iconst_1
/*     */       //   488: invokestatic 49	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$200	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;I)Z
/*     */       //   491: pop
/*     */       //   492: aload_0
/*     */       //   493: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   496: invokestatic 145	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$900	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)Lcom/facebook/presto/jdbc/internal/jetty/util/ConcurrentHashSet;
/*     */       //   499: invokestatic 149	java/lang/Thread:currentThread	()Ljava/lang/Thread;
/*     */       //   502: invokevirtual 155	com/facebook/presto/jdbc/internal/jetty/util/ConcurrentHashSet:remove	(Ljava/lang/Object;)Z
/*     */       //   505: pop
/*     */       //   506: goto +294 -> 800
/*     */       //   509: astore_3
/*     */       //   510: iconst_1
/*     */       //   511: istore_2
/*     */       //   512: invokestatic 57	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$300	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   515: aload_3
/*     */       //   516: invokeinterface 159 2 0
/*     */       //   521: iload_1
/*     */       //   522: ifne +71 -> 593
/*     */       //   525: aload_0
/*     */       //   526: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   529: invokevirtual 53	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:isRunning	()Z
/*     */       //   532: ifeq +61 -> 593
/*     */       //   535: iload_2
/*     */       //   536: ifne +28 -> 564
/*     */       //   539: invokestatic 57	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$300	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   542: ldc -121
/*     */       //   544: iconst_2
/*     */       //   545: anewarray 4	java/lang/Object
/*     */       //   548: dup
/*     */       //   549: iconst_0
/*     */       //   550: aload_0
/*     */       //   551: aastore
/*     */       //   552: dup
/*     */       //   553: iconst_1
/*     */       //   554: aload_0
/*     */       //   555: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   558: aastore
/*     */       //   559: invokeinterface 138 3 0
/*     */       //   564: aload_0
/*     */       //   565: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   568: invokestatic 92	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$500	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)Ljava/util/concurrent/atomic/AtomicInteger;
/*     */       //   571: invokevirtual 129	java/util/concurrent/atomic/AtomicInteger:decrementAndGet	()I
/*     */       //   574: aload_0
/*     */       //   575: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   578: invokevirtual 141	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:getMaxThreads	()I
/*     */       //   581: if_icmpge +12 -> 593
/*     */       //   584: aload_0
/*     */       //   585: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   588: iconst_1
/*     */       //   589: invokestatic 49	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$200	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;I)Z
/*     */       //   592: pop
/*     */       //   593: aload_0
/*     */       //   594: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   597: invokestatic 145	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$900	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)Lcom/facebook/presto/jdbc/internal/jetty/util/ConcurrentHashSet;
/*     */       //   600: invokestatic 149	java/lang/Thread:currentThread	()Ljava/lang/Thread;
/*     */       //   603: invokevirtual 155	com/facebook/presto/jdbc/internal/jetty/util/ConcurrentHashSet:remove	(Ljava/lang/Object;)Z
/*     */       //   606: pop
/*     */       //   607: goto +193 -> 800
/*     */       //   610: astore_3
/*     */       //   611: invokestatic 57	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$300	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   614: aload_3
/*     */       //   615: invokeinterface 161 2 0
/*     */       //   620: iload_1
/*     */       //   621: ifne +71 -> 692
/*     */       //   624: aload_0
/*     */       //   625: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   628: invokevirtual 53	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:isRunning	()Z
/*     */       //   631: ifeq +61 -> 692
/*     */       //   634: iload_2
/*     */       //   635: ifne +28 -> 663
/*     */       //   638: invokestatic 57	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$300	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   641: ldc -121
/*     */       //   643: iconst_2
/*     */       //   644: anewarray 4	java/lang/Object
/*     */       //   647: dup
/*     */       //   648: iconst_0
/*     */       //   649: aload_0
/*     */       //   650: aastore
/*     */       //   651: dup
/*     */       //   652: iconst_1
/*     */       //   653: aload_0
/*     */       //   654: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   657: aastore
/*     */       //   658: invokeinterface 138 3 0
/*     */       //   663: aload_0
/*     */       //   664: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   667: invokestatic 92	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$500	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)Ljava/util/concurrent/atomic/AtomicInteger;
/*     */       //   670: invokevirtual 129	java/util/concurrent/atomic/AtomicInteger:decrementAndGet	()I
/*     */       //   673: aload_0
/*     */       //   674: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   677: invokevirtual 141	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:getMaxThreads	()I
/*     */       //   680: if_icmpge +12 -> 692
/*     */       //   683: aload_0
/*     */       //   684: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   687: iconst_1
/*     */       //   688: invokestatic 49	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$200	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;I)Z
/*     */       //   691: pop
/*     */       //   692: aload_0
/*     */       //   693: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   696: invokestatic 145	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$900	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)Lcom/facebook/presto/jdbc/internal/jetty/util/ConcurrentHashSet;
/*     */       //   699: invokestatic 149	java/lang/Thread:currentThread	()Ljava/lang/Thread;
/*     */       //   702: invokevirtual 155	com/facebook/presto/jdbc/internal/jetty/util/ConcurrentHashSet:remove	(Ljava/lang/Object;)Z
/*     */       //   705: pop
/*     */       //   706: goto +94 -> 800
/*     */       //   709: astore 10
/*     */       //   711: iload_1
/*     */       //   712: ifne +71 -> 783
/*     */       //   715: aload_0
/*     */       //   716: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   719: invokevirtual 53	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:isRunning	()Z
/*     */       //   722: ifeq +61 -> 783
/*     */       //   725: iload_2
/*     */       //   726: ifne +28 -> 754
/*     */       //   729: invokestatic 57	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$300	()Lcom/facebook/presto/jdbc/internal/jetty/util/log/Logger;
/*     */       //   732: ldc -121
/*     */       //   734: iconst_2
/*     */       //   735: anewarray 4	java/lang/Object
/*     */       //   738: dup
/*     */       //   739: iconst_0
/*     */       //   740: aload_0
/*     */       //   741: aastore
/*     */       //   742: dup
/*     */       //   743: iconst_1
/*     */       //   744: aload_0
/*     */       //   745: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   748: aastore
/*     */       //   749: invokeinterface 138 3 0
/*     */       //   754: aload_0
/*     */       //   755: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   758: invokestatic 92	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$500	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)Ljava/util/concurrent/atomic/AtomicInteger;
/*     */       //   761: invokevirtual 129	java/util/concurrent/atomic/AtomicInteger:decrementAndGet	()I
/*     */       //   764: aload_0
/*     */       //   765: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   768: invokevirtual 141	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:getMaxThreads	()I
/*     */       //   771: if_icmpge +12 -> 783
/*     */       //   774: aload_0
/*     */       //   775: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   778: iconst_1
/*     */       //   779: invokestatic 49	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$200	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;I)Z
/*     */       //   782: pop
/*     */       //   783: aload_0
/*     */       //   784: getfield 15	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool$3:this$0	Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;
/*     */       //   787: invokestatic 145	com/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool:access$900	(Lcom/facebook/presto/jdbc/internal/jetty/util/thread/QueuedThreadPool;)Lcom/facebook/presto/jdbc/internal/jetty/util/ConcurrentHashSet;
/*     */       //   790: invokestatic 149	java/lang/Thread:currentThread	()Ljava/lang/Thread;
/*     */       //   793: invokevirtual 155	com/facebook/presto/jdbc/internal/jetty/util/ConcurrentHashSet:remove	(Ljava/lang/Object;)Z
/*     */       //   796: pop
/*     */       //   797: aload 10
/*     */       //   799: athrow
/*     */       //   800: return
/*     */       // Line number table:
/*     */       //   Java source line #554	-> byte code offset #0
/*     */       //   Java source line #555	-> byte code offset #2
/*     */       //   Java source line #558	-> byte code offset #4
/*     */       //   Java source line #560	-> byte code offset #20
/*     */       //   Java source line #562	-> byte code offset #37
/*     */       //   Java source line #565	-> byte code offset #46
/*     */       //   Java source line #568	-> byte code offset #56
/*     */       //   Java source line #570	-> byte code offset #70
/*     */       //   Java source line #571	-> byte code offset #81
/*     */       //   Java source line #572	-> byte code offset #99
/*     */       //   Java source line #573	-> byte code offset #107
/*     */       //   Java source line #574	-> byte code offset #118
/*     */       //   Java source line #575	-> byte code offset #136
/*     */       //   Java source line #577	-> byte code offset #142
/*     */       //   Java source line #578	-> byte code offset #144
/*     */       //   Java source line #580	-> byte code offset #147
/*     */       //   Java source line #586	-> byte code offset #166
/*     */       //   Java source line #588	-> byte code offset #177
/*     */       //   Java source line #590	-> byte code offset #191
/*     */       //   Java source line #591	-> byte code offset #201
/*     */       //   Java source line #595	-> byte code offset #220
/*     */       //   Java source line #596	-> byte code offset #232
/*     */       //   Java source line #598	-> byte code offset #244
/*     */       //   Java source line #599	-> byte code offset #256
/*     */       //   Java source line #600	-> byte code offset #261
/*     */       //   Java source line #602	-> byte code offset #291
/*     */       //   Java source line #604	-> byte code offset #327
/*     */       //   Java source line #615	-> byte code offset #329
/*     */       //   Java source line #617	-> byte code offset #342
/*     */       //   Java source line #609	-> byte code offset #354
/*     */       //   Java source line #610	-> byte code offset #362
/*     */       //   Java source line #615	-> byte code offset #365
/*     */       //   Java source line #617	-> byte code offset #378
/*     */       //   Java source line #615	-> byte code offset #390
/*     */       //   Java source line #617	-> byte code offset #405
/*     */       //   Java source line #633	-> byte code offset #420
/*     */       //   Java source line #635	-> byte code offset #434
/*     */       //   Java source line #636	-> byte code offset #438
/*     */       //   Java source line #638	-> byte code offset #463
/*     */       //   Java source line #639	-> byte code offset #483
/*     */       //   Java source line #641	-> byte code offset #492
/*     */       //   Java source line #642	-> byte code offset #506
/*     */       //   Java source line #622	-> byte code offset #509
/*     */       //   Java source line #624	-> byte code offset #510
/*     */       //   Java source line #625	-> byte code offset #512
/*     */       //   Java source line #633	-> byte code offset #521
/*     */       //   Java source line #635	-> byte code offset #535
/*     */       //   Java source line #636	-> byte code offset #539
/*     */       //   Java source line #638	-> byte code offset #564
/*     */       //   Java source line #639	-> byte code offset #584
/*     */       //   Java source line #641	-> byte code offset #593
/*     */       //   Java source line #642	-> byte code offset #607
/*     */       //   Java source line #627	-> byte code offset #610
/*     */       //   Java source line #629	-> byte code offset #611
/*     */       //   Java source line #633	-> byte code offset #620
/*     */       //   Java source line #635	-> byte code offset #634
/*     */       //   Java source line #636	-> byte code offset #638
/*     */       //   Java source line #638	-> byte code offset #663
/*     */       //   Java source line #639	-> byte code offset #683
/*     */       //   Java source line #641	-> byte code offset #692
/*     */       //   Java source line #642	-> byte code offset #706
/*     */       //   Java source line #633	-> byte code offset #709
/*     */       //   Java source line #635	-> byte code offset #725
/*     */       //   Java source line #636	-> byte code offset #729
/*     */       //   Java source line #638	-> byte code offset #754
/*     */       //   Java source line #639	-> byte code offset #774
/*     */       //   Java source line #641	-> byte code offset #783
/*     */       //   Java source line #643	-> byte code offset #800
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	801	0	this	3
/*     */       //   1	711	1	shrink	boolean
/*     */       //   3	723	2	ignore	boolean
/*     */       //   19	343	3	job	Runnable
/*     */       //   509	7	3	e	InterruptedException
/*     */       //   610	5	3	e	Throwable
/*     */       //   230	88	4	size	int
/*     */       //   254	45	5	last	long
/*     */       //   259	42	7	now	long
/*     */       //   390	25	9	localObject1	Object
/*     */       //   709	89	10	localObject2	Object
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   166	329	390	finally
/*     */       //   354	365	390	finally
/*     */       //   390	392	390	finally
/*     */       //   4	420	509	java/lang/InterruptedException
/*     */       //   4	420	610	java/lang/Throwable
/*     */       //   4	420	709	finally
/*     */       //   509	521	709	finally
/*     */       //   610	620	709	finally
/*     */       //   709	711	709	finally
/*     */     }
/*     */   };
/*     */   
/*     */   protected void runJob(Runnable job)
/*     */   {
/* 654 */     job.run();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected BlockingQueue<Runnable> getQueue()
/*     */   {
/* 662 */     return this._jobs;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setQueue(BlockingQueue<Runnable> queue)
/*     */   {
/* 670 */     throw new UnsupportedOperationException("Use constructor injection");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @ManagedOperation("interrupt a pool thread")
/*     */   public boolean interruptThread(@Name("id") long id)
/*     */   {
/* 680 */     for (Thread thread : this._threads)
/*     */     {
/* 682 */       if (thread.getId() == id)
/*     */       {
/* 684 */         thread.interrupt();
/* 685 */         return true;
/*     */       }
/*     */     }
/* 688 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @ManagedOperation("dump a pool thread stack")
/*     */   public String dumpThread(@Name("id") long id)
/*     */   {
/* 698 */     for (Thread thread : this._threads)
/*     */     {
/* 700 */       if (thread.getId() == id)
/*     */       {
/* 702 */         StringBuilder buf = new StringBuilder();
/* 703 */         buf.append(thread.getId()).append(" ").append(thread.getName()).append(" ");
/* 704 */         buf.append(thread.getState()).append(":").append(System.lineSeparator());
/* 705 */         for (StackTraceElement element : thread.getStackTrace())
/* 706 */           buf.append("  at ").append(element.toString()).append(System.lineSeparator());
/* 707 */         return buf.toString();
/*     */       }
/*     */     }
/* 710 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\thread\QueuedThreadPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */