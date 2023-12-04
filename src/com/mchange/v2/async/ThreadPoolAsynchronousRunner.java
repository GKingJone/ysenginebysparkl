/*     */ package com.mchange.v2.async;
/*     */ 
/*     */ import com.mchange.v2.io.IndentedWriter;
/*     */ import com.mchange.v2.log.MLevel;
/*     */ import com.mchange.v2.log.MLog;
/*     */ import com.mchange.v2.log.MLogger;
/*     */ import com.mchange.v2.util.ResourceClosedException;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.Timer;
/*     */ import java.util.TimerTask;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ThreadPoolAsynchronousRunner
/*     */   implements AsynchronousRunner
/*     */ {
/*  38 */   static final MLogger logger = MLog.getLogger(ThreadPoolAsynchronousRunner.class);
/*     */   
/*     */   static final int POLL_FOR_STOP_INTERVAL = 5000;
/*     */   
/*     */   static final int DFLT_DEADLOCK_DETECTOR_INTERVAL = 10000;
/*     */   
/*     */   static final int DFLT_INTERRUPT_DELAY_AFTER_APPARENT_DEADLOCK = 60000;
/*     */   
/*     */   static final int DFLT_MAX_INDIVIDUAL_TASK_TIME = 0;
/*     */   
/*     */   static final int DFLT_MAX_EMERGENCY_THREADS = 10;
/*     */   
/*     */   int deadlock_detector_interval;
/*     */   
/*     */   int interrupt_delay_after_apparent_deadlock;
/*     */   int max_individual_task_time;
/*     */   int num_threads;
/*     */   boolean daemon;
/*     */   HashSet managed;
/*     */   HashSet available;
/*     */   LinkedList pendingTasks;
/*     */   Timer myTimer;
/*     */   boolean should_cancel_timer;
/*  61 */   TimerTask deadlockDetector = new DeadlockDetector();
/*  62 */   TimerTask replacedThreadInterruptor = null;
/*     */   
/*  64 */   Map stoppedThreadsToStopDates = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private ThreadPoolAsynchronousRunner(int num_threads, boolean daemon, int max_individual_task_time, int deadlock_detector_interval, int interrupt_delay_after_apparent_deadlock, Timer myTimer, boolean should_cancel_timer)
/*     */   {
/*  74 */     this.num_threads = num_threads;
/*  75 */     this.daemon = daemon;
/*  76 */     this.max_individual_task_time = max_individual_task_time;
/*  77 */     this.deadlock_detector_interval = deadlock_detector_interval;
/*  78 */     this.interrupt_delay_after_apparent_deadlock = interrupt_delay_after_apparent_deadlock;
/*  79 */     this.myTimer = myTimer;
/*  80 */     this.should_cancel_timer = should_cancel_timer;
/*     */     
/*  82 */     recreateThreadsAndTasks();
/*     */     
/*  84 */     myTimer.schedule(this.deadlockDetector, deadlock_detector_interval, deadlock_detector_interval);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ThreadPoolAsynchronousRunner(int num_threads, boolean daemon, int max_individual_task_time, int deadlock_detector_interval, int interrupt_delay_after_apparent_deadlock, Timer myTimer)
/*     */   {
/*  96 */     this(num_threads, daemon, max_individual_task_time, deadlock_detector_interval, interrupt_delay_after_apparent_deadlock, myTimer, false);
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
/*     */   public ThreadPoolAsynchronousRunner(int num_threads, boolean daemon, int max_individual_task_time, int deadlock_detector_interval, int interrupt_delay_after_apparent_deadlock)
/*     */   {
/* 111 */     this(num_threads, daemon, max_individual_task_time, deadlock_detector_interval, interrupt_delay_after_apparent_deadlock, new Timer(true), true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ThreadPoolAsynchronousRunner(int num_threads, boolean daemon, Timer sharedTimer)
/*     */   {
/* 122 */     this(num_threads, daemon, 0, 10000, 60000, sharedTimer, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ThreadPoolAsynchronousRunner(int num_threads, boolean daemon)
/*     */   {
/* 133 */     this(num_threads, daemon, 0, 10000, 60000, new Timer(true), true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized void postRunnable(Runnable r)
/*     */   {
/*     */     try
/*     */     {
/* 145 */       this.pendingTasks.add(r);
/* 146 */       notifyAll();
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (NullPointerException e)
/*     */     {
/*     */ 
/* 153 */       if (logger.isLoggable(MLevel.FINE)) {
/* 154 */         logger.log(MLevel.FINE, "NullPointerException while posting Runnable -- Probably we're closed.", e);
/*     */       }
/* 156 */       throw new ResourceClosedException("Attempted to use a ThreadPoolAsynchronousRunner in a closed or broken state.");
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized int getThreadCount() {
/* 161 */     return this.managed.size();
/*     */   }
/*     */   
/*     */   public void close(boolean skip_remaining_tasks) {
/* 165 */     synchronized (this)
/*     */     {
/* 167 */       if (this.managed == null) return;
/* 168 */       this.deadlockDetector.cancel();
/*     */       
/* 170 */       if (this.should_cancel_timer)
/* 171 */         this.myTimer.cancel();
/* 172 */       this.myTimer = null;
/* 173 */       for (Iterator ii = this.managed.iterator(); ii.hasNext();)
/*     */       {
/* 175 */         PoolThread stopMe = (PoolThread)ii.next();
/* 176 */         stopMe.gentleStop();
/* 177 */         if (skip_remaining_tasks)
/* 178 */           stopMe.interrupt();
/*     */       }
/* 180 */       this.managed = null;
/*     */       Iterator ii;
/* 182 */       if (!skip_remaining_tasks)
/*     */       {
/* 184 */         for (ii = this.pendingTasks.iterator(); ii.hasNext();)
/*     */         {
/* 186 */           Runnable r = (Runnable)ii.next();
/* 187 */           new Thread(r).start();
/* 188 */           ii.remove();
/*     */         }
/*     */       }
/* 191 */       this.available = null;
/* 192 */       this.pendingTasks = null;
/*     */     }
/*     */   }
/*     */   
/*     */   public void close() {
/* 197 */     close(true);
/*     */   }
/*     */   
/* 200 */   public synchronized int getActiveCount() { return this.managed.size() - this.available.size(); }
/*     */   
/*     */   public synchronized int getIdleCount() {
/* 203 */     return this.available.size();
/*     */   }
/*     */   
/* 206 */   public synchronized int getPendingTaskCount() { return this.pendingTasks.size(); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public synchronized String getStatus()
/*     */   {
/* 218 */     return getMultiLineStatusString();
/*     */   }
/*     */   
/*     */   public synchronized String getStackTraces()
/*     */   {
/* 223 */     return getStackTraces(0);
/*     */   }
/*     */   
/*     */ 
/*     */   private String getStackTraces(int initial_indent)
/*     */   {
/* 229 */     if (this.managed == null) {
/* 230 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 234 */       Method m = Thread.class.getMethod("getStackTrace", null);
/*     */       
/* 236 */       StringWriter sw = new StringWriter(2048);
/* 237 */       IndentedWriter iw = new IndentedWriter(sw);
/* 238 */       for (int i = 0; i < initial_indent; i++)
/* 239 */         iw.upIndent();
/* 240 */       for (Iterator ii = this.managed.iterator(); ii.hasNext();)
/*     */       {
/* 242 */         Object poolThread = ii.next();
/* 243 */         Object[] stackTraces = (Object[])m.invoke(poolThread, null);
/* 244 */         iw.println(poolThread);
/* 245 */         iw.upIndent();
/* 246 */         int i = 0; for (int len = stackTraces.length; i < len; i++)
/* 247 */           iw.println(stackTraces[i]);
/* 248 */         iw.downIndent();
/*     */       }
/* 250 */       for (int i = 0; i < initial_indent; i++)
/* 251 */         iw.downIndent();
/* 252 */       iw.flush();
/* 253 */       String out = sw.toString();
/* 254 */       iw.close();
/* 255 */       return out;
/*     */     }
/*     */     catch (NoSuchMethodException e)
/*     */     {
/* 259 */       if (logger.isLoggable(MLevel.FINE))
/* 260 */         logger.fine(this + ": strack traces unavailable because this is a pre-Java 1.5 VM.");
/* 261 */       return null;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 265 */       if (logger.isLoggable(MLevel.FINE))
/* 266 */         logger.log(MLevel.FINE, this + ": An Exception occurred while trying to extract PoolThread stack traces.", e); }
/* 267 */     return null;
/*     */   }
/*     */   
/*     */   public synchronized String getMultiLineStatusString()
/*     */   {
/* 272 */     return getMultiLineStatusString(0);
/*     */   }
/*     */   
/*     */ 
/*     */   private String getMultiLineStatusString(int initial_indent)
/*     */   {
/*     */     try
/*     */     {
/* 280 */       StringWriter sw = new StringWriter(2048);
/* 281 */       IndentedWriter iw = new IndentedWriter(sw);
/*     */       
/* 283 */       for (int i = 0; i < initial_indent; i++) {
/* 284 */         iw.upIndent();
/*     */       }
/* 286 */       if (this.managed == null)
/*     */       {
/* 288 */         iw.print("[");
/* 289 */         iw.print(this);
/* 290 */         iw.println(" closed.]");
/*     */       }
/*     */       else
/*     */       {
/* 294 */         HashSet active = (HashSet)this.managed.clone();
/* 295 */         active.removeAll(this.available);
/*     */         
/* 297 */         iw.print("Managed Threads: ");
/* 298 */         iw.println(this.managed.size());
/* 299 */         iw.print("Active Threads: ");
/* 300 */         iw.println(active.size());
/* 301 */         iw.println("Active Tasks: ");
/* 302 */         iw.upIndent();
/* 303 */         for (Iterator ii = active.iterator(); ii.hasNext();)
/*     */         {
/* 305 */           PoolThread pt = (PoolThread)ii.next();
/* 306 */           iw.print(pt.getCurrentTask());
/* 307 */           iw.print(" (");
/* 308 */           iw.print(pt.getName());
/* 309 */           iw.println(')');
/*     */         }
/* 311 */         iw.downIndent();
/* 312 */         iw.println("Pending Tasks: ");
/* 313 */         iw.upIndent();
/* 314 */         int i = 0; for (int len = this.pendingTasks.size(); i < len; i++)
/* 315 */           iw.println(this.pendingTasks.get(i));
/* 316 */         iw.downIndent();
/*     */       }
/*     */       
/* 319 */       for (int i = 0; i < initial_indent; i++)
/* 320 */         iw.downIndent();
/* 321 */       iw.flush();
/* 322 */       String out = sw.toString();
/* 323 */       iw.close();
/* 324 */       return out;
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 328 */       if (logger.isLoggable(MLevel.WARNING))
/* 329 */         logger.log(MLevel.WARNING, "Huh? An IOException when working with a StringWriter?!?", e);
/* 330 */       throw new RuntimeException("Huh? An IOException when working with a StringWriter?!? " + e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void appendStatusString(StringBuffer sb)
/*     */   {
/* 338 */     if (this.managed == null) {
/* 339 */       sb.append("[closed]");
/*     */     }
/*     */     else {
/* 342 */       HashSet active = (HashSet)this.managed.clone();
/* 343 */       active.removeAll(this.available);
/* 344 */       sb.append("[num_managed_threads: ");
/* 345 */       sb.append(this.managed.size());
/* 346 */       sb.append(", num_active: ");
/* 347 */       sb.append(active.size());
/* 348 */       sb.append("; activeTasks: ");
/* 349 */       boolean first = true;
/* 350 */       for (Iterator ii = active.iterator(); ii.hasNext();)
/*     */       {
/* 352 */         if (first) {
/* 353 */           first = false;
/*     */         } else
/* 355 */           sb.append(", ");
/* 356 */         PoolThread pt = (PoolThread)ii.next();
/* 357 */         sb.append(pt.getCurrentTask());
/* 358 */         sb.append(" (");
/* 359 */         sb.append(pt.getName());
/* 360 */         sb.append(')');
/*     */       }
/* 362 */       sb.append("; pendingTasks: ");
/* 363 */       int i = 0; for (int len = this.pendingTasks.size(); i < len; i++)
/*     */       {
/* 365 */         if (i != 0) sb.append(", ");
/* 366 */         sb.append(this.pendingTasks.get(i));
/*     */       }
/* 368 */       sb.append(']');
/*     */     }
/*     */   }
/*     */   
/*     */   private void recreateThreadsAndTasks()
/*     */   {
/*     */     Date aboutNow;
/*     */     Iterator ii;
/* 376 */     if (this.managed != null)
/*     */     {
/* 378 */       aboutNow = new Date();
/* 379 */       for (ii = this.managed.iterator(); ii.hasNext();)
/*     */       {
/* 381 */         PoolThread pt = (PoolThread)ii.next();
/* 382 */         pt.gentleStop();
/* 383 */         this.stoppedThreadsToStopDates.put(pt, aboutNow);
/* 384 */         ensureReplacedThreadsProcessing();
/*     */       }
/*     */     }
/*     */     
/* 388 */     this.managed = new HashSet();
/* 389 */     this.available = new HashSet();
/* 390 */     this.pendingTasks = new LinkedList();
/* 391 */     for (int i = 0; i < this.num_threads; i++)
/*     */     {
/* 393 */       Thread t = new PoolThread(i, this.daemon);
/* 394 */       this.managed.add(t);
/* 395 */       this.available.add(t);
/* 396 */       t.start();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void processReplacedThreads()
/*     */   {
/* 404 */     long about_now = System.currentTimeMillis();
/* 405 */     for (Iterator ii = this.stoppedThreadsToStopDates.keySet().iterator(); ii.hasNext();)
/*     */     {
/* 407 */       PoolThread pt = (PoolThread)ii.next();
/* 408 */       if (!pt.isAlive()) {
/* 409 */         ii.remove();
/*     */       }
/*     */       else {
/* 412 */         Date d = (Date)this.stoppedThreadsToStopDates.get(pt);
/* 413 */         if (about_now - d.getTime() > this.interrupt_delay_after_apparent_deadlock)
/*     */         {
/* 415 */           if (logger.isLoggable(MLevel.WARNING)) {
/* 416 */             logger.log(MLevel.WARNING, "Task " + pt.getCurrentTask() + " (in deadlocked PoolThread) failed to complete in maximum time " + this.interrupt_delay_after_apparent_deadlock + "ms. Trying interrupt().");
/*     */           }
/*     */           
/* 419 */           pt.interrupt();
/* 420 */           ii.remove();
/*     */         }
/*     */       }
/*     */       
/* 424 */       if (this.stoppedThreadsToStopDates.isEmpty()) {
/* 425 */         stopReplacedThreadsProcessing();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void ensureReplacedThreadsProcessing()
/*     */   {
/* 433 */     if (this.replacedThreadInterruptor == null)
/*     */     {
/* 435 */       if (logger.isLoggable(MLevel.FINE)) {
/* 436 */         logger.fine("Apparently some threads have been replaced. Replacement thread processing enabled.");
/*     */       }
/* 438 */       this.replacedThreadInterruptor = new ReplacedThreadInterruptor();
/* 439 */       int replacedThreadProcessDelay = this.interrupt_delay_after_apparent_deadlock / 4;
/* 440 */       this.myTimer.schedule(this.replacedThreadInterruptor, replacedThreadProcessDelay, replacedThreadProcessDelay);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void stopReplacedThreadsProcessing()
/*     */   {
/* 448 */     if (this.replacedThreadInterruptor != null)
/*     */     {
/* 450 */       this.replacedThreadInterruptor.cancel();
/* 451 */       this.replacedThreadInterruptor = null;
/*     */       
/* 453 */       if (logger.isLoggable(MLevel.FINE)) {
/* 454 */         logger.fine("Apparently all replaced threads have either completed their tasks or been interrupted(). Replacement thread processing cancelled.");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void shuttingDown(PoolThread pt)
/*     */   {
/* 463 */     if ((this.managed != null) && (this.managed.contains(pt)))
/*     */     {
/* 465 */       this.managed.remove(pt);
/* 466 */       this.available.remove(pt);
/* 467 */       PoolThread replacement = new PoolThread(pt.getIndex(), this.daemon);
/* 468 */       this.managed.add(replacement);
/* 469 */       this.available.add(replacement);
/* 470 */       replacement.start();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   class PoolThread
/*     */     extends Thread
/*     */   {
/*     */     Runnable currentTask;
/*     */     
/*     */ 
/*     */     boolean should_stop;
/*     */     
/*     */ 
/*     */     int index;
/*     */     
/* 487 */     TimerTask maxIndividualTaskTimeEnforcer = null;
/*     */     
/*     */     PoolThread(int index, boolean daemon)
/*     */     {
/* 491 */       setName(getClass().getName() + "-#" + index);
/* 492 */       setDaemon(daemon);
/* 493 */       this.index = index;
/*     */     }
/*     */     
/*     */     public int getIndex() {
/* 497 */       return this.index;
/*     */     }
/*     */     
/*     */     void gentleStop()
/*     */     {
/* 502 */       this.should_stop = true;
/*     */     }
/*     */     
/*     */     Runnable getCurrentTask()
/*     */     {
/* 507 */       return this.currentTask;
/*     */     }
/*     */     
/*     */     private void setMaxIndividualTaskTimeEnforcer()
/*     */     {
/* 512 */       this.maxIndividualTaskTimeEnforcer = new MaxIndividualTaskTimeEnforcer(ThreadPoolAsynchronousRunner.this, this);
/* 513 */       ThreadPoolAsynchronousRunner.this.myTimer.schedule(this.maxIndividualTaskTimeEnforcer, ThreadPoolAsynchronousRunner.this.max_individual_task_time);
/*     */     }
/*     */     
/*     */ 
/*     */     private void cancelMaxIndividualTaskTimeEnforcer()
/*     */     {
/* 519 */       this.maxIndividualTaskTimeEnforcer.cancel();
/* 520 */       this.maxIndividualTaskTimeEnforcer = null;
/*     */     }
/*     */     
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       try
/*     */       {
/*     */         for (;;)
/*     */         {
/*     */           Runnable myTask;
/* 531 */           synchronized (ThreadPoolAsynchronousRunner.this)
/*     */           {
/* 533 */             if ((!this.should_stop) && (ThreadPoolAsynchronousRunner.this.pendingTasks.size() == 0)) {
/* 534 */               ThreadPoolAsynchronousRunner.this.wait(5000L); continue; }
/* 535 */             if (this.should_stop) {
/*     */               break;
/*     */             }
/* 538 */             if (!ThreadPoolAsynchronousRunner.this.available.remove(this))
/* 539 */               throw new InternalError("An unavailable PoolThread tried to check itself out!!!");
/* 540 */             myTask = (Runnable)ThreadPoolAsynchronousRunner.this.pendingTasks.remove(0);
/* 541 */             this.currentTask = myTask;
/*     */           }
/*     */           try
/*     */           {
/* 545 */             if (ThreadPoolAsynchronousRunner.this.max_individual_task_time > 0)
/* 546 */               setMaxIndividualTaskTimeEnforcer();
/* 547 */             myTask.run();
/*     */           }
/*     */           catch (RuntimeException e)
/*     */           {
/* 551 */             if (ThreadPoolAsynchronousRunner.logger.isLoggable(MLevel.WARNING)) {
/* 552 */               ThreadPoolAsynchronousRunner.logger.log(MLevel.WARNING, this + " -- caught unexpected Exception while executing posted task.", e);
/*     */             }
/*     */           }
/*     */           finally
/*     */           {
/* 557 */             if (this.maxIndividualTaskTimeEnforcer != null) {
/* 558 */               cancelMaxIndividualTaskTimeEnforcer();
/*     */             }
/* 560 */             synchronized (ThreadPoolAsynchronousRunner.this)
/*     */             {
/* 562 */               if (this.should_stop) {
/*     */                 break;
/*     */               }
/* 565 */               if ((ThreadPoolAsynchronousRunner.this.available != null) && (!ThreadPoolAsynchronousRunner.this.available.add(this)))
/* 566 */                 throw new InternalError("An apparently available PoolThread tried to check itself in!!!");
/* 567 */               this.currentTask = null;
/*     */             }
/*     */             
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */       }
/*     */       catch (InterruptedException exc)
/*     */       {
/* 577 */         if (ThreadPoolAsynchronousRunner.logger.isLoggable(MLevel.FINE)) {
/* 578 */           ThreadPoolAsynchronousRunner.logger.fine(this + " interrupted. Shutting down.");
/*     */         }
/*     */       }
/* 581 */       synchronized (ThreadPoolAsynchronousRunner.this) {
/* 582 */         ThreadPoolAsynchronousRunner.this.shuttingDown(this);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   class DeadlockDetector extends TimerTask {
/* 588 */     LinkedList last = null;
/* 589 */     LinkedList current = null;
/*     */     
/*     */     DeadlockDetector() {}
/*     */     
/* 593 */     public void run() { boolean run_stray_tasks = false;
/* 594 */       synchronized (ThreadPoolAsynchronousRunner.this)
/*     */       {
/* 596 */         if (ThreadPoolAsynchronousRunner.this.pendingTasks.size() == 0)
/*     */         {
/* 598 */           this.last = null;
/* 599 */           return;
/*     */         }
/*     */         
/* 602 */         this.current = ((LinkedList)ThreadPoolAsynchronousRunner.this.pendingTasks.clone());
/* 603 */         if (this.current.equals(this.last))
/*     */         {
/*     */ 
/* 606 */           if (ThreadPoolAsynchronousRunner.logger.isLoggable(MLevel.WARNING))
/*     */           {
/* 608 */             ThreadPoolAsynchronousRunner.logger.warning(this + " -- APPARENT DEADLOCK!!! Creating emergency threads for unassigned pending tasks!");
/* 609 */             StringWriter sw = new StringWriter(4096);
/* 610 */             PrintWriter pw = new PrintWriter(sw);
/*     */             
/*     */ 
/*     */ 
/* 614 */             pw.print(this);
/* 615 */             pw.println(" -- APPARENT DEADLOCK!!! Complete Status: ");
/* 616 */             pw.print(ThreadPoolAsynchronousRunner.this.getMultiLineStatusString(1));
/* 617 */             pw.println("Pool thread stack traces:");
/* 618 */             String stackTraces = ThreadPoolAsynchronousRunner.this.getStackTraces(1);
/* 619 */             if (stackTraces == null) {
/* 620 */               pw.println("\t[Stack traces of deadlocked task threads not available.]");
/*     */             } else
/* 622 */               pw.println(stackTraces);
/* 623 */             pw.flush();
/* 624 */             ThreadPoolAsynchronousRunner.logger.warning(sw.toString());
/* 625 */             pw.close();
/*     */           }
/* 627 */           ThreadPoolAsynchronousRunner.this.recreateThreadsAndTasks();
/* 628 */           run_stray_tasks = true;
/*     */         }
/*     */       }
/* 631 */       if (run_stray_tasks)
/*     */       {
/* 633 */         AsynchronousRunner ar = new ThreadPerTaskAsynchronousRunner(10, ThreadPoolAsynchronousRunner.this.max_individual_task_time);
/* 634 */         for (Iterator ii = this.current.iterator(); ii.hasNext();)
/* 635 */           ar.postRunnable((Runnable)ii.next());
/* 636 */         ar.close(false);
/* 637 */         this.last = null;
/*     */       }
/*     */       else {
/* 640 */         this.last = this.current;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 646 */       this.current = null;
/*     */     }
/*     */   }
/*     */   
/*     */   class MaxIndividualTaskTimeEnforcer extends TimerTask
/*     */   {
/*     */     PoolThread pt;
/*     */     Thread interruptMe;
/*     */     String threadStr;
/*     */     String fixedTaskStr;
/*     */     
/*     */     MaxIndividualTaskTimeEnforcer(PoolThread pt)
/*     */     {
/* 659 */       this.pt = pt;
/* 660 */       this.interruptMe = pt;
/* 661 */       this.threadStr = pt.toString();
/* 662 */       this.fixedTaskStr = null;
/*     */     }
/*     */     
/*     */     MaxIndividualTaskTimeEnforcer(Thread interruptMe, String threadStr, String fixedTaskStr)
/*     */     {
/* 667 */       this.pt = null;
/* 668 */       this.interruptMe = interruptMe;
/* 669 */       this.threadStr = threadStr;
/* 670 */       this.fixedTaskStr = fixedTaskStr;
/*     */     }
/*     */     
/*     */     public void run()
/*     */     {
/*     */       String taskStr;
/*     */       String taskStr;
/* 677 */       if (this.fixedTaskStr != null) {
/* 678 */         taskStr = this.fixedTaskStr;
/* 679 */       } else if (this.pt != null) {
/*     */         String taskStr;
/* 681 */         synchronized (ThreadPoolAsynchronousRunner.this) {
/* 682 */           taskStr = String.valueOf(this.pt.getCurrentTask());
/*     */         }
/*     */       } else {
/* 685 */         taskStr = "Unknown task?!";
/*     */       }
/* 687 */       if (ThreadPoolAsynchronousRunner.logger.isLoggable(MLevel.WARNING)) {
/* 688 */         ThreadPoolAsynchronousRunner.logger.warning("A task has exceeded the maximum allowable task time. Will interrupt() thread [" + this.threadStr + "], with current task: " + taskStr);
/*     */       }
/*     */       
/* 691 */       this.interruptMe.interrupt();
/*     */       
/* 693 */       if (ThreadPoolAsynchronousRunner.logger.isLoggable(MLevel.WARNING)) {
/* 694 */         ThreadPoolAsynchronousRunner.logger.warning("Thread [" + this.threadStr + "] interrupted.");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void runInEmergencyThread(Runnable r)
/*     */   {
/* 701 */     Thread t = new Thread(r);
/* 702 */     t.start();
/* 703 */     if (this.max_individual_task_time > 0)
/*     */     {
/* 705 */       TimerTask maxIndividualTaskTimeEnforcer = new MaxIndividualTaskTimeEnforcer(t, t + " [One-off emergency thread!!!]", r.toString());
/* 706 */       this.myTimer.schedule(maxIndividualTaskTimeEnforcer, this.max_individual_task_time);
/*     */     }
/*     */   }
/*     */   
/*     */   class ReplacedThreadInterruptor extends TimerTask {
/*     */     ReplacedThreadInterruptor() {}
/*     */     
/*     */     public void run() {
/* 714 */       synchronized (ThreadPoolAsynchronousRunner.this) {
/* 715 */         ThreadPoolAsynchronousRunner.this.processReplacedThreads();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\async\ThreadPoolAsynchronousRunner.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */