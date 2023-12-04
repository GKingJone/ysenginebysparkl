/*      */ package com.google.common.util.concurrent;
/*      */ 
/*      */ import com.google.common.annotations.Beta;
/*      */ import com.google.common.base.Preconditions;
/*      */ import com.google.common.base.Throwables;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.locks.Condition;
/*      */ import java.util.concurrent.locks.ReentrantLock;
/*      */ import javax.annotation.concurrent.GuardedBy;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ @Beta
/*      */ public final class Monitor
/*      */ {
/*      */   private final boolean fair;
/*      */   private final ReentrantLock lock;
/*      */   
/*      */   @Beta
/*      */   public static abstract class Guard
/*      */   {
/*      */     final Monitor monitor;
/*      */     final Condition condition;
/*      */     @GuardedBy("monitor.lock")
/*  296 */     int waiterCount = 0;
/*      */     
/*      */     @GuardedBy("monitor.lock")
/*      */     Guard next;
/*      */     
/*      */ 
/*      */     protected Guard(Monitor monitor)
/*      */     {
/*  304 */       this.monitor = ((Monitor)Preconditions.checkNotNull(monitor, "monitor"));
/*  305 */       this.condition = monitor.lock.newCondition();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public abstract boolean isSatisfied();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @GuardedBy("lock")
/*  331 */   private Guard activeGuards = null;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Monitor()
/*      */   {
/*  339 */     this(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Monitor(boolean fair)
/*      */   {
/*  349 */     this.fair = fair;
/*  350 */     this.lock = new ReentrantLock(fair);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void enter()
/*      */   {
/*  357 */     this.lock.lock();
/*      */   }
/*      */   
/*      */ 
/*      */   public void enterInterruptibly()
/*      */     throws InterruptedException
/*      */   {
/*  364 */     this.lock.lockInterruptibly();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean enter(long time, TimeUnit unit)
/*      */   {
/*  373 */     long timeoutNanos = unit.toNanos(time);
/*  374 */     ReentrantLock lock = this.lock;
/*  375 */     if ((!this.fair) && (lock.tryLock())) {
/*  376 */       return true;
/*      */     }
/*  378 */     long deadline = System.nanoTime() + timeoutNanos;
/*  379 */     boolean interrupted = Thread.interrupted();
/*      */     
/*      */     try
/*      */     {
/*  383 */       return lock.tryLock(timeoutNanos, TimeUnit.NANOSECONDS);
/*      */     } catch (InterruptedException interrupt) {
/*  385 */       for (;;) { interrupted = true;
/*  386 */         timeoutNanos = deadline - System.nanoTime();
/*      */       }
/*      */     }
/*      */     finally {
/*  390 */       if (interrupted) {
/*  391 */         Thread.currentThread().interrupt();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean enterInterruptibly(long time, TimeUnit unit)
/*      */     throws InterruptedException
/*      */   {
/*  402 */     return this.lock.tryLock(time, unit);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean tryEnter()
/*      */   {
/*  413 */     return this.lock.tryLock();
/*      */   }
/*      */   
/*      */ 
/*      */   public void enterWhen(Guard guard)
/*      */     throws InterruptedException
/*      */   {
/*  420 */     if (guard.monitor != this) {
/*  421 */       throw new IllegalMonitorStateException();
/*      */     }
/*  423 */     ReentrantLock lock = this.lock;
/*  424 */     boolean signalBeforeWaiting = lock.isHeldByCurrentThread();
/*  425 */     lock.lockInterruptibly();
/*      */     
/*  427 */     boolean satisfied = false;
/*      */     try {
/*  429 */       if (!guard.isSatisfied()) {
/*  430 */         await(guard, signalBeforeWaiting);
/*      */       }
/*  432 */       satisfied = true;
/*      */     } finally {
/*  434 */       if (!satisfied) {
/*  435 */         leave();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void enterWhenUninterruptibly(Guard guard)
/*      */   {
/*  444 */     if (guard.monitor != this) {
/*  445 */       throw new IllegalMonitorStateException();
/*      */     }
/*  447 */     ReentrantLock lock = this.lock;
/*  448 */     boolean signalBeforeWaiting = lock.isHeldByCurrentThread();
/*  449 */     lock.lock();
/*      */     
/*  451 */     boolean satisfied = false;
/*      */     try {
/*  453 */       if (!guard.isSatisfied()) {
/*  454 */         awaitUninterruptibly(guard, signalBeforeWaiting);
/*      */       }
/*  456 */       satisfied = true;
/*      */     } finally {
/*  458 */       if (!satisfied) {
/*  459 */         leave();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean enterWhen(Guard guard, long time, TimeUnit unit)
/*      */     throws InterruptedException
/*      */   {
/*  472 */     long timeoutNanos = unit.toNanos(time);
/*  473 */     if (guard.monitor != this) {
/*  474 */       throw new IllegalMonitorStateException();
/*      */     }
/*  476 */     ReentrantLock lock = this.lock;
/*  477 */     boolean reentrant = lock.isHeldByCurrentThread();
/*  478 */     if ((this.fair) || (!lock.tryLock())) {
/*  479 */       long deadline = System.nanoTime() + timeoutNanos;
/*  480 */       if (!lock.tryLock(time, unit)) {
/*  481 */         return false;
/*      */       }
/*  483 */       timeoutNanos = deadline - System.nanoTime();
/*      */     }
/*      */     
/*  486 */     boolean satisfied = false;
/*  487 */     boolean threw = true;
/*      */     try {
/*  489 */       satisfied = (guard.isSatisfied()) || (awaitNanos(guard, timeoutNanos, reentrant));
/*  490 */       threw = false;
/*  491 */       return satisfied;
/*      */     } finally {
/*  493 */       if (!satisfied) {
/*      */         try
/*      */         {
/*  496 */           if ((threw) && (!reentrant)) {
/*  497 */             signalNextWaiter();
/*      */           }
/*      */         } finally {
/*  500 */           lock.unlock();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   /* Error */
/*      */   public boolean enterWhenUninterruptibly(Guard guard, long time, TimeUnit unit)
/*      */   {
/*      */     // Byte code:
/*      */     //   0: aload 4
/*      */     //   2: lload_2
/*      */     //   3: invokevirtual 10	java/util/concurrent/TimeUnit:toNanos	(J)J
/*      */     //   6: lstore 5
/*      */     //   8: aload_1
/*      */     //   9: getfield 19	com/google/common/util/concurrent/Monitor$Guard:monitor	Lcom/google/common/util/concurrent/Monitor;
/*      */     //   12: aload_0
/*      */     //   13: if_acmpeq +11 -> 24
/*      */     //   16: new 20	java/lang/IllegalMonitorStateException
/*      */     //   19: dup
/*      */     //   20: invokespecial 21	java/lang/IllegalMonitorStateException:<init>	()V
/*      */     //   23: athrow
/*      */     //   24: aload_0
/*      */     //   25: getfield 1	com/google/common/util/concurrent/Monitor:lock	Ljava/util/concurrent/locks/ReentrantLock;
/*      */     //   28: astore 7
/*      */     //   30: invokestatic 12	java/lang/System:nanoTime	()J
/*      */     //   33: lload 5
/*      */     //   35: ladd
/*      */     //   36: lstore 8
/*      */     //   38: aload 7
/*      */     //   40: invokevirtual 22	java/util/concurrent/locks/ReentrantLock:isHeldByCurrentThread	()Z
/*      */     //   43: istore 10
/*      */     //   45: invokestatic 13	java/lang/Thread:interrupted	()Z
/*      */     //   48: istore 11
/*      */     //   50: aload_0
/*      */     //   51: getfield 5	com/google/common/util/concurrent/Monitor:fair	Z
/*      */     //   54: ifne +11 -> 65
/*      */     //   57: aload 7
/*      */     //   59: invokevirtual 11	java/util/concurrent/locks/ReentrantLock:tryLock	()Z
/*      */     //   62: ifne +61 -> 123
/*      */     //   65: iconst_0
/*      */     //   66: istore 12
/*      */     //   68: aload 7
/*      */     //   70: lload 5
/*      */     //   72: getstatic 14	java/util/concurrent/TimeUnit:NANOSECONDS	Ljava/util/concurrent/TimeUnit;
/*      */     //   75: invokevirtual 15	java/util/concurrent/locks/ReentrantLock:tryLock	(JLjava/util/concurrent/TimeUnit;)Z
/*      */     //   78: istore 12
/*      */     //   80: iload 12
/*      */     //   82: ifne +20 -> 102
/*      */     //   85: iconst_0
/*      */     //   86: istore 13
/*      */     //   88: iload 11
/*      */     //   90: ifeq +9 -> 99
/*      */     //   93: invokestatic 16	java/lang/Thread:currentThread	()Ljava/lang/Thread;
/*      */     //   96: invokevirtual 17	java/lang/Thread:interrupt	()V
/*      */     //   99: iload 13
/*      */     //   101: ireturn
/*      */     //   102: goto +8 -> 110
/*      */     //   105: astore 13
/*      */     //   107: iconst_1
/*      */     //   108: istore 11
/*      */     //   110: lload 8
/*      */     //   112: invokestatic 12	java/lang/System:nanoTime	()J
/*      */     //   115: lsub
/*      */     //   116: lstore 5
/*      */     //   118: iload 12
/*      */     //   120: ifeq -52 -> 68
/*      */     //   123: iconst_0
/*      */     //   124: istore 12
/*      */     //   126: aload_1
/*      */     //   127: invokevirtual 23	com/google/common/util/concurrent/Monitor$Guard:isSatisfied	()Z
/*      */     //   130: ifne +15 -> 145
/*      */     //   133: aload_0
/*      */     //   134: aload_1
/*      */     //   135: lload 5
/*      */     //   137: iload 10
/*      */     //   139: invokespecial 27	com/google/common/util/concurrent/Monitor:awaitNanos	(Lcom/google/common/util/concurrent/Monitor$Guard;JZ)Z
/*      */     //   142: ifeq +7 -> 149
/*      */     //   145: iconst_1
/*      */     //   146: goto +4 -> 150
/*      */     //   149: iconst_0
/*      */     //   150: dup
/*      */     //   151: istore 12
/*      */     //   153: istore 13
/*      */     //   155: iload 12
/*      */     //   157: ifne +8 -> 165
/*      */     //   160: aload 7
/*      */     //   162: invokevirtual 29	java/util/concurrent/locks/ReentrantLock:unlock	()V
/*      */     //   165: iload 11
/*      */     //   167: ifeq +9 -> 176
/*      */     //   170: invokestatic 16	java/lang/Thread:currentThread	()Ljava/lang/Thread;
/*      */     //   173: invokevirtual 17	java/lang/Thread:interrupt	()V
/*      */     //   176: iload 13
/*      */     //   178: ireturn
/*      */     //   179: astore 13
/*      */     //   181: iconst_1
/*      */     //   182: istore 11
/*      */     //   184: iconst_0
/*      */     //   185: istore 10
/*      */     //   187: lload 8
/*      */     //   189: invokestatic 12	java/lang/System:nanoTime	()J
/*      */     //   192: lsub
/*      */     //   193: lstore 5
/*      */     //   195: goto -69 -> 126
/*      */     //   198: astore 14
/*      */     //   200: iload 12
/*      */     //   202: ifne +8 -> 210
/*      */     //   205: aload 7
/*      */     //   207: invokevirtual 29	java/util/concurrent/locks/ReentrantLock:unlock	()V
/*      */     //   210: aload 14
/*      */     //   212: athrow
/*      */     //   213: astore 15
/*      */     //   215: iload 11
/*      */     //   217: ifeq +9 -> 226
/*      */     //   220: invokestatic 16	java/lang/Thread:currentThread	()Ljava/lang/Thread;
/*      */     //   223: invokevirtual 17	java/lang/Thread:interrupt	()V
/*      */     //   226: aload 15
/*      */     //   228: athrow
/*      */     // Line number table:
/*      */     //   Java source line #513	-> byte code offset #0
/*      */     //   Java source line #514	-> byte code offset #8
/*      */     //   Java source line #515	-> byte code offset #16
/*      */     //   Java source line #517	-> byte code offset #24
/*      */     //   Java source line #518	-> byte code offset #30
/*      */     //   Java source line #519	-> byte code offset #38
/*      */     //   Java source line #520	-> byte code offset #45
/*      */     //   Java source line #522	-> byte code offset #50
/*      */     //   Java source line #523	-> byte code offset #65
/*      */     //   Java source line #526	-> byte code offset #68
/*      */     //   Java source line #527	-> byte code offset #80
/*      */     //   Java source line #528	-> byte code offset #85
/*      */     //   Java source line #555	-> byte code offset #88
/*      */     //   Java source line #556	-> byte code offset #93
/*      */     //   Java source line #532	-> byte code offset #102
/*      */     //   Java source line #530	-> byte code offset #105
/*      */     //   Java source line #531	-> byte code offset #107
/*      */     //   Java source line #533	-> byte code offset #110
/*      */     //   Java source line #534	-> byte code offset #118
/*      */     //   Java source line #537	-> byte code offset #123
/*      */     //   Java source line #541	-> byte code offset #126
/*      */     //   Java source line #550	-> byte code offset #155
/*      */     //   Java source line #551	-> byte code offset #160
/*      */     //   Java source line #555	-> byte code offset #165
/*      */     //   Java source line #556	-> byte code offset #170
/*      */     //   Java source line #543	-> byte code offset #179
/*      */     //   Java source line #544	-> byte code offset #181
/*      */     //   Java source line #545	-> byte code offset #184
/*      */     //   Java source line #546	-> byte code offset #187
/*      */     //   Java source line #547	-> byte code offset #195
/*      */     //   Java source line #550	-> byte code offset #198
/*      */     //   Java source line #551	-> byte code offset #205
/*      */     //   Java source line #555	-> byte code offset #213
/*      */     //   Java source line #556	-> byte code offset #220
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	signature
/*      */     //   0	229	0	this	Monitor
/*      */     //   0	229	1	guard	Guard
/*      */     //   0	229	2	time	long
/*      */     //   0	229	4	unit	TimeUnit
/*      */     //   6	188	5	timeoutNanos	long
/*      */     //   28	178	7	lock	ReentrantLock
/*      */     //   36	152	8	deadline	long
/*      */     //   43	143	10	signalBeforeWaiting	boolean
/*      */     //   48	168	11	interrupted	boolean
/*      */     //   66	53	12	locked	boolean
/*      */     //   124	77	12	satisfied	boolean
/*      */     //   86	14	13	bool1	boolean
/*      */     //   105	72	13	interrupt	InterruptedException
/*      */     //   153	24	13	bool2	boolean
/*      */     //   179	3	13	interrupt	InterruptedException
/*      */     //   198	13	14	localObject1	Object
/*      */     //   213	14	15	localObject2	Object
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   68	88	105	java/lang/InterruptedException
/*      */     //   126	155	179	java/lang/InterruptedException
/*      */     //   126	155	198	finally
/*      */     //   179	200	198	finally
/*      */     //   50	88	213	finally
/*      */     //   102	165	213	finally
/*      */     //   179	215	213	finally
/*      */   }
/*      */   
/*      */   public boolean enterIf(Guard guard)
/*      */   {
/*  568 */     if (guard.monitor != this) {
/*  569 */       throw new IllegalMonitorStateException();
/*      */     }
/*  571 */     ReentrantLock lock = this.lock;
/*  572 */     lock.lock();
/*      */     
/*  574 */     boolean satisfied = false;
/*      */     try {
/*  576 */       return satisfied = guard.isSatisfied();
/*      */     } finally {
/*  578 */       if (!satisfied) {
/*  579 */         lock.unlock();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean enterIfInterruptibly(Guard guard)
/*      */     throws InterruptedException
/*      */   {
/*  591 */     if (guard.monitor != this) {
/*  592 */       throw new IllegalMonitorStateException();
/*      */     }
/*  594 */     ReentrantLock lock = this.lock;
/*  595 */     lock.lockInterruptibly();
/*      */     
/*  597 */     boolean satisfied = false;
/*      */     try {
/*  599 */       return satisfied = guard.isSatisfied();
/*      */     } finally {
/*  601 */       if (!satisfied) {
/*  602 */         lock.unlock();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean enterIf(Guard guard, long time, TimeUnit unit)
/*      */   {
/*  614 */     if (guard.monitor != this) {
/*  615 */       throw new IllegalMonitorStateException();
/*      */     }
/*  617 */     if (!enter(time, unit)) {
/*  618 */       return false;
/*      */     }
/*      */     
/*  621 */     boolean satisfied = false;
/*      */     try {
/*  623 */       return satisfied = guard.isSatisfied();
/*      */     } finally {
/*  625 */       if (!satisfied) {
/*  626 */         this.lock.unlock();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean enterIfInterruptibly(Guard guard, long time, TimeUnit unit)
/*      */     throws InterruptedException
/*      */   {
/*  639 */     if (guard.monitor != this) {
/*  640 */       throw new IllegalMonitorStateException();
/*      */     }
/*  642 */     ReentrantLock lock = this.lock;
/*  643 */     if (!lock.tryLock(time, unit)) {
/*  644 */       return false;
/*      */     }
/*      */     
/*  647 */     boolean satisfied = false;
/*      */     try {
/*  649 */       return satisfied = guard.isSatisfied();
/*      */     } finally {
/*  651 */       if (!satisfied) {
/*  652 */         lock.unlock();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean tryEnterIf(Guard guard)
/*      */   {
/*  666 */     if (guard.monitor != this) {
/*  667 */       throw new IllegalMonitorStateException();
/*      */     }
/*  669 */     ReentrantLock lock = this.lock;
/*  670 */     if (!lock.tryLock()) {
/*  671 */       return false;
/*      */     }
/*      */     
/*  674 */     boolean satisfied = false;
/*      */     try {
/*  676 */       return satisfied = guard.isSatisfied();
/*      */     } finally {
/*  678 */       if (!satisfied) {
/*  679 */         lock.unlock();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void waitFor(Guard guard)
/*      */     throws InterruptedException
/*      */   {
/*  689 */     if (!(guard.monitor == this & this.lock.isHeldByCurrentThread())) {
/*  690 */       throw new IllegalMonitorStateException();
/*      */     }
/*  692 */     if (!guard.isSatisfied()) {
/*  693 */       await(guard, true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void waitForUninterruptibly(Guard guard)
/*      */   {
/*  702 */     if (!(guard.monitor == this & this.lock.isHeldByCurrentThread())) {
/*  703 */       throw new IllegalMonitorStateException();
/*      */     }
/*  705 */     if (!guard.isSatisfied()) {
/*  706 */       awaitUninterruptibly(guard, true);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean waitFor(Guard guard, long time, TimeUnit unit)
/*      */     throws InterruptedException
/*      */   {
/*  717 */     long timeoutNanos = unit.toNanos(time);
/*  718 */     if (!(guard.monitor == this & this.lock.isHeldByCurrentThread())) {
/*  719 */       throw new IllegalMonitorStateException();
/*      */     }
/*  721 */     return (guard.isSatisfied()) || (awaitNanos(guard, timeoutNanos, true));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean waitForUninterruptibly(Guard guard, long time, TimeUnit unit)
/*      */   {
/*  731 */     long timeoutNanos = unit.toNanos(time);
/*  732 */     if (!(guard.monitor == this & this.lock.isHeldByCurrentThread())) {
/*  733 */       throw new IllegalMonitorStateException();
/*      */     }
/*  735 */     if (guard.isSatisfied()) {
/*  736 */       return true;
/*      */     }
/*  738 */     boolean signalBeforeWaiting = true;
/*  739 */     long deadline = System.nanoTime() + timeoutNanos;
/*  740 */     boolean interrupted = Thread.interrupted();
/*      */     
/*      */     try
/*      */     {
/*  744 */       return awaitNanos(guard, timeoutNanos, signalBeforeWaiting);
/*      */     } catch (InterruptedException interrupt) {
/*  746 */       for (;;) { interrupted = true;
/*  747 */         if (guard.isSatisfied()) {
/*  748 */           return true;
/*      */         }
/*  750 */         signalBeforeWaiting = false;
/*  751 */         timeoutNanos = deadline - System.nanoTime();
/*      */       }
/*      */     }
/*      */     finally {
/*  755 */       if (interrupted) {
/*  756 */         Thread.currentThread().interrupt();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void leave()
/*      */   {
/*  765 */     ReentrantLock lock = this.lock;
/*      */     try
/*      */     {
/*  768 */       if (lock.getHoldCount() == 1) {
/*  769 */         signalNextWaiter();
/*      */       }
/*      */     } finally {
/*  772 */       lock.unlock();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public boolean isFair()
/*      */   {
/*  780 */     return this.fair;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isOccupied()
/*      */   {
/*  788 */     return this.lock.isLocked();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isOccupiedByCurrentThread()
/*      */   {
/*  796 */     return this.lock.isHeldByCurrentThread();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getOccupiedDepth()
/*      */   {
/*  804 */     return this.lock.getHoldCount();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getQueueLength()
/*      */   {
/*  814 */     return this.lock.getQueueLength();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean hasQueuedThreads()
/*      */   {
/*  824 */     return this.lock.hasQueuedThreads();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean hasQueuedThread(Thread thread)
/*      */   {
/*  834 */     return this.lock.hasQueuedThread(thread);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean hasWaiters(Guard guard)
/*      */   {
/*  844 */     return getWaitQueueLength(guard) > 0;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getWaitQueueLength(Guard guard)
/*      */   {
/*  854 */     if (guard.monitor != this) {
/*  855 */       throw new IllegalMonitorStateException();
/*      */     }
/*  857 */     this.lock.lock();
/*      */     try {
/*  859 */       return guard.waiterCount;
/*      */     } finally {
/*  861 */       this.lock.unlock();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @GuardedBy("lock")
/*      */   private void signalNextWaiter()
/*      */   {
/*  891 */     for (Guard guard = this.activeGuards; guard != null; guard = guard.next) {
/*  892 */       if (isSatisfied(guard)) {
/*  893 */         guard.condition.signal();
/*  894 */         break;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @GuardedBy("lock")
/*      */   private boolean isSatisfied(Guard guard)
/*      */   {
/*      */     try
/*      */     {
/*  924 */       return guard.isSatisfied();
/*      */     } catch (Throwable throwable) {
/*  926 */       signalAllWaiters();
/*  927 */       throw Throwables.propagate(throwable);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @GuardedBy("lock")
/*      */   private void signalAllWaiters()
/*      */   {
/*  936 */     for (Guard guard = this.activeGuards; guard != null; guard = guard.next) {
/*  937 */       guard.condition.signalAll();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @GuardedBy("lock")
/*      */   private void beginWaitingFor(Guard guard)
/*      */   {
/*  946 */     int waiters = guard.waiterCount++;
/*  947 */     if (waiters == 0)
/*      */     {
/*  949 */       guard.next = this.activeGuards;
/*  950 */       this.activeGuards = guard;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @GuardedBy("lock")
/*      */   private void endWaitingFor(Guard guard)
/*      */   {
/*  959 */     int waiters = --guard.waiterCount;
/*  960 */     if (waiters == 0)
/*      */     {
/*  962 */       Guard p = this.activeGuards; for (Guard pred = null;; p = p.next) {
/*  963 */         if (p == guard) {
/*  964 */           if (pred == null) {
/*  965 */             this.activeGuards = p.next;
/*      */           } else {
/*  967 */             pred.next = p.next;
/*      */           }
/*  969 */           p.next = null;
/*  970 */           break;
/*      */         }
/*  962 */         pred = p;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @GuardedBy("lock")
/*      */   private void await(Guard guard, boolean signalBeforeWaiting)
/*      */     throws InterruptedException
/*      */   {
/*  985 */     if (signalBeforeWaiting) {
/*  986 */       signalNextWaiter();
/*      */     }
/*  988 */     beginWaitingFor(guard);
/*      */     try {
/*      */       do {
/*  991 */         guard.condition.await();
/*  992 */       } while (!guard.isSatisfied());
/*      */     } finally {
/*  994 */       endWaitingFor(guard);
/*      */     }
/*      */   }
/*      */   
/*      */   @GuardedBy("lock")
/*      */   private void awaitUninterruptibly(Guard guard, boolean signalBeforeWaiting) {
/* 1000 */     if (signalBeforeWaiting) {
/* 1001 */       signalNextWaiter();
/*      */     }
/* 1003 */     beginWaitingFor(guard);
/*      */     try {
/*      */       do {
/* 1006 */         guard.condition.awaitUninterruptibly();
/* 1007 */       } while (!guard.isSatisfied());
/*      */     } finally {
/* 1009 */       endWaitingFor(guard);
/*      */     }
/*      */   }
/*      */   
/*      */   @GuardedBy("lock")
/*      */   private boolean awaitNanos(Guard guard, long nanos, boolean signalBeforeWaiting) throws InterruptedException
/*      */   {
/* 1016 */     if (signalBeforeWaiting) {
/* 1017 */       signalNextWaiter();
/*      */     }
/* 1019 */     beginWaitingFor(guard);
/*      */     try {
/*      */       boolean bool;
/* 1022 */       do { if (nanos < 0L) {
/* 1023 */           return false;
/*      */         }
/* 1025 */         nanos = guard.condition.awaitNanos(nanos);
/* 1026 */       } while (!guard.isSatisfied());
/* 1027 */       return true;
/*      */     } finally {
/* 1029 */       endWaitingFor(guard);
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\util\concurrent\Monitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */