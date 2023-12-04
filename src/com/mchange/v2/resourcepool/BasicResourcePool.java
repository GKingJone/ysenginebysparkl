/*      */ package com.mchange.v2.resourcepool;
/*      */ 
/*      */ import com.mchange.v2.log.MLevel;
/*      */ import com.mchange.v2.log.MLogger;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ 
/*      */ class BasicResourcePool implements ResourcePool
/*      */ {
/*      */   private static final MLogger logger;
/*      */   static final int AUTO_CULL_FREQUENCY_DIVISOR = 4;
/*      */   static final int AUTO_MAX_CULL_FREQUENCY = 900000;
/*      */   static final int AUTO_MIN_CULL_FREQUENCY = 1000;
/*      */   static final String USE_SCATTERED_ACQUIRE_TASK_KEY = "com.mchange.v2.resourcepool.experimental.useScatteredAcquireTask";
/*      */   static final boolean USE_SCATTERED_ACQUIRE_TASK;
/*      */   final Manager mgr;
/*      */   final int start;
/*      */   final int min;
/*      */   final int max;
/*      */   final int inc;
/*      */   final int num_acq_attempts;
/*      */   final int acq_attempt_delay;
/*      */   final long check_idle_resources_delay;
/*      */   final long max_resource_age;
/*      */   final long max_idle_time;
/*      */   final long excess_max_idle_time;
/*      */   final long destroy_unreturned_resc_time;
/*      */   final long expiration_enforcement_delay;
/*      */   final boolean break_on_acquisition_failure;
/*      */   final boolean debug_store_checkout_exceptions;
/*      */   
/*      */   static
/*      */   {
/*   34 */     logger = com.mchange.v2.log.MLog.getLogger(BasicResourcePool.class);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   48 */     String checkScattered = com.mchange.v2.cfg.MultiPropertiesConfig.readVmConfig().getProperty("com.mchange.v2.resourcepool.experimental.useScatteredAcquireTask");
/*   49 */     if ((checkScattered != null) && (checkScattered.trim().toLowerCase().equals("true")))
/*      */     {
/*   51 */       USE_SCATTERED_ACQUIRE_TASK = true;
/*   52 */       if (logger.isLoggable(MLevel.INFO)) {
/*   53 */         logger.info(BasicResourcePool.class.getName() + " using experimental ScatteredAcquireTask.");
/*      */       }
/*      */     } else {
/*   56 */       USE_SCATTERED_ACQUIRE_TASK = false;
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
/*   81 */   final long pool_start_time = System.currentTimeMillis();
/*      */   
/*      */   final BasicResourcePoolFactory factory;
/*      */   
/*      */   final com.mchange.v2.async.AsynchronousRunner taskRunner;
/*      */   
/*      */   final com.mchange.v2.async.RunnableQueue asyncEventQueue;
/*      */   
/*      */   final ResourcePoolEventSupport rpes;
/*      */   java.util.Timer cullAndIdleRefurbishTimer;
/*      */   java.util.TimerTask cullTask;
/*      */   java.util.TimerTask idleRefurbishTask;
/*   93 */   HashSet acquireWaiters = new HashSet();
/*   94 */   HashSet otherWaiters = new HashSet();
/*      */   
/*      */   int pending_acquires;
/*      */   
/*      */   int pending_removes;
/*      */   
/*      */   int target_pool_size;
/*      */   
/*  102 */   HashMap managed = new HashMap();
/*      */   
/*      */ 
/*  105 */   java.util.LinkedList unused = new java.util.LinkedList();
/*      */   
/*      */ 
/*      */ 
/*  109 */   HashSet excluded = new HashSet();
/*      */   
/*  111 */   java.util.Map formerResources = new java.util.WeakHashMap();
/*      */   
/*  113 */   java.util.Set idleCheckResources = new HashSet();
/*      */   
/*  115 */   boolean force_kill_acquires = false;
/*      */   
/*  117 */   boolean broken = false;
/*      */   
/*      */ 
/*      */ 
/*  121 */   long failed_checkins = 0L;
/*  122 */   long failed_checkouts = 0L;
/*  123 */   long failed_idle_tests = 0L;
/*      */   
/*  125 */   Throwable lastCheckinFailure = null;
/*  126 */   Throwable lastCheckoutFailure = null;
/*  127 */   Throwable lastIdleTestFailure = null;
/*  128 */   Throwable lastResourceTestFailure = null;
/*      */   
/*  130 */   Throwable lastAcquisitionFailiure = null;
/*      */   
/*      */   Object exampleResource;
/*      */   
/*      */   public long getStartTime()
/*      */   {
/*  136 */     return this.pool_start_time;
/*      */   }
/*      */   
/*  139 */   public long getUpTime() { return System.currentTimeMillis() - this.pool_start_time; }
/*      */   
/*      */   public synchronized long getNumFailedCheckins() {
/*  142 */     return this.failed_checkins;
/*      */   }
/*      */   
/*  145 */   public synchronized long getNumFailedCheckouts() { return this.failed_checkouts; }
/*      */   
/*      */   public synchronized long getNumFailedIdleTests() {
/*  148 */     return this.failed_idle_tests;
/*      */   }
/*      */   
/*  151 */   public synchronized Throwable getLastCheckinFailure() { return this.lastCheckinFailure; }
/*      */   
/*      */ 
/*      */   private void setLastCheckinFailure(Throwable t)
/*      */   {
/*  156 */     assert (Thread.holdsLock(this));
/*      */     
/*  158 */     this.lastCheckinFailure = t;
/*  159 */     this.lastResourceTestFailure = t;
/*      */   }
/*      */   
/*      */   public synchronized Throwable getLastCheckoutFailure() {
/*  163 */     return this.lastCheckoutFailure;
/*      */   }
/*      */   
/*      */   private void setLastCheckoutFailure(Throwable t)
/*      */   {
/*  168 */     assert (Thread.holdsLock(this));
/*      */     
/*  170 */     this.lastCheckoutFailure = t;
/*  171 */     this.lastResourceTestFailure = t;
/*      */   }
/*      */   
/*      */   public synchronized Throwable getLastIdleCheckFailure() {
/*  175 */     return this.lastIdleTestFailure;
/*      */   }
/*      */   
/*      */   private void setLastIdleCheckFailure(Throwable t)
/*      */   {
/*  180 */     assert (Thread.holdsLock(this));
/*      */     
/*  182 */     this.lastIdleTestFailure = t;
/*  183 */     this.lastResourceTestFailure = t;
/*      */   }
/*      */   
/*      */   public synchronized Throwable getLastResourceTestFailure() {
/*  187 */     return this.lastResourceTestFailure;
/*      */   }
/*      */   
/*  190 */   public synchronized Throwable getLastAcquisitionFailure() { return this.lastAcquisitionFailiure; }
/*      */   
/*      */   private synchronized void setLastAcquisitionFailure(Throwable t)
/*      */   {
/*  194 */     this.lastAcquisitionFailiure = t;
/*      */   }
/*      */   
/*  197 */   public synchronized int getNumCheckoutWaiters() { return this.acquireWaiters.size(); }
/*      */   
/*      */   private void addToFormerResources(Object resc) {
/*  200 */     this.formerResources.put(resc, null);
/*      */   }
/*      */   
/*  203 */   private boolean isFormerResource(Object resc) { return this.formerResources.keySet().contains(resc); }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public BasicResourcePool(Manager mgr, int start, int min, int max, int inc, int num_acq_attempts, int acq_attempt_delay, long check_idle_resources_delay, long max_resource_age, long max_idle_time, long excess_max_idle_time, long destroy_unreturned_resc_time, long expiration_enforcement_delay, boolean break_on_acquisition_failure, boolean debug_store_checkout_exceptions, com.mchange.v2.async.AsynchronousRunner taskRunner, com.mchange.v2.async.RunnableQueue asyncEventQueue, java.util.Timer cullAndIdleRefurbishTimer, BasicResourcePoolFactory factory)
/*      */     throws ResourcePoolException
/*      */   {
/*      */     try
/*      */     {
/*  231 */       this.mgr = mgr;
/*  232 */       this.start = start;
/*  233 */       this.min = min;
/*  234 */       this.max = max;
/*  235 */       this.inc = inc;
/*  236 */       this.num_acq_attempts = num_acq_attempts;
/*  237 */       this.acq_attempt_delay = acq_attempt_delay;
/*  238 */       this.check_idle_resources_delay = check_idle_resources_delay;
/*  239 */       this.max_resource_age = max_resource_age;
/*  240 */       this.max_idle_time = max_idle_time;
/*  241 */       this.excess_max_idle_time = excess_max_idle_time;
/*  242 */       this.destroy_unreturned_resc_time = destroy_unreturned_resc_time;
/*      */       
/*  244 */       this.break_on_acquisition_failure = break_on_acquisition_failure;
/*  245 */       this.debug_store_checkout_exceptions = ((debug_store_checkout_exceptions) && (destroy_unreturned_resc_time > 0L));
/*  246 */       this.taskRunner = taskRunner;
/*  247 */       this.asyncEventQueue = asyncEventQueue;
/*  248 */       this.cullAndIdleRefurbishTimer = cullAndIdleRefurbishTimer;
/*  249 */       this.factory = factory;
/*      */       
/*  251 */       this.pending_acquires = 0;
/*  252 */       this.pending_removes = 0;
/*      */       
/*  254 */       this.target_pool_size = Math.max(start, min);
/*      */       
/*  256 */       if (asyncEventQueue != null) {
/*  257 */         this.rpes = new ResourcePoolEventSupport(this);
/*      */       } else {
/*  259 */         this.rpes = null;
/*      */       }
/*      */       
/*  262 */       ensureStartResources();
/*      */       
/*  264 */       if (mustEnforceExpiration())
/*      */       {
/*  266 */         if (expiration_enforcement_delay <= 0L) {
/*  267 */           this.expiration_enforcement_delay = automaticExpirationEnforcementDelay();
/*      */         } else {
/*  269 */           this.expiration_enforcement_delay = expiration_enforcement_delay;
/*      */         }
/*  271 */         this.cullTask = new CullTask();
/*      */         
/*      */ 
/*  274 */         cullAndIdleRefurbishTimer.schedule(this.cullTask, minExpirationTime(), this.expiration_enforcement_delay);
/*      */       }
/*      */       else {
/*  277 */         this.expiration_enforcement_delay = expiration_enforcement_delay;
/*      */       }
/*      */       
/*  280 */       if (check_idle_resources_delay > 0L)
/*      */       {
/*  282 */         this.idleRefurbishTask = new CheckIdleResourcesTask();
/*  283 */         cullAndIdleRefurbishTimer.schedule(this.idleRefurbishTask, check_idle_resources_delay, check_idle_resources_delay);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  288 */       if (logger.isLoggable(MLevel.FINER)) {
/*  289 */         logger.finer(this + " config: [start -> " + this.start + "; min -> " + this.min + "; max -> " + this.max + "; inc -> " + this.inc + "; num_acq_attempts -> " + this.num_acq_attempts + "; acq_attempt_delay -> " + this.acq_attempt_delay + "; check_idle_resources_delay -> " + this.check_idle_resources_delay + "; mox_resource_age -> " + this.max_resource_age + "; max_idle_time -> " + this.max_idle_time + "; excess_max_idle_time -> " + this.excess_max_idle_time + "; destroy_unreturned_resc_time -> " + this.destroy_unreturned_resc_time + "; expiration_enforcement_delay -> " + this.expiration_enforcement_delay + "; break_on_acquisition_failure -> " + this.break_on_acquisition_failure + "; debug_store_checkout_exceptions -> " + this.debug_store_checkout_exceptions + "]");
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*  305 */       throw ResourcePoolUtils.convertThrowable(e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private boolean mustTestIdleResources()
/*      */   {
/*  314 */     return this.check_idle_resources_delay > 0L;
/*      */   }
/*      */   
/*      */   private boolean mustEnforceExpiration()
/*      */   {
/*  319 */     return (this.max_resource_age > 0L) || (this.max_idle_time > 0L) || (this.excess_max_idle_time > 0L) || (this.destroy_unreturned_resc_time > 0L);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private long minExpirationTime()
/*      */   {
/*  329 */     long out = Long.MAX_VALUE;
/*  330 */     if (this.max_resource_age > 0L)
/*  331 */       out = Math.min(out, this.max_resource_age);
/*  332 */     if (this.max_idle_time > 0L)
/*  333 */       out = Math.min(out, this.max_idle_time);
/*  334 */     if (this.excess_max_idle_time > 0L)
/*  335 */       out = Math.min(out, this.excess_max_idle_time);
/*  336 */     if (this.destroy_unreturned_resc_time > 0L)
/*  337 */       out = Math.min(out, this.destroy_unreturned_resc_time);
/*  338 */     return out;
/*      */   }
/*      */   
/*      */   private long automaticExpirationEnforcementDelay()
/*      */   {
/*  343 */     long out = minExpirationTime();
/*  344 */     out /= 4L;
/*  345 */     out = Math.min(out, 900000L);
/*  346 */     out = Math.max(out, 1000L);
/*  347 */     return out;
/*      */   }
/*      */   
/*      */   public long getEffectiveExpirationEnforcementDelay() {
/*  351 */     return this.expiration_enforcement_delay;
/*      */   }
/*      */   
/*  354 */   private synchronized boolean isBroken() { return this.broken; }
/*      */   
/*      */   private boolean supportsEvents()
/*      */   {
/*  358 */     return this.asyncEventQueue != null;
/*      */   }
/*      */   
/*      */   public Object checkoutResource() throws ResourcePoolException, InterruptedException {
/*      */     try {
/*  363 */       return checkoutResource(0L);
/*      */ 
/*      */     }
/*      */     catch (TimeoutException e)
/*      */     {
/*  368 */       if (logger.isLoggable(MLevel.WARNING)) {
/*  369 */         logger.log(MLevel.WARNING, "Huh??? TimeoutException with no timeout set!!!", e);
/*      */       }
/*  371 */       throw new ResourcePoolException("Huh??? TimeoutException with no timeout set!!!", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void _recheckResizePool()
/*      */   {
/*  378 */     assert (Thread.holdsLock(this));
/*      */     
/*  380 */     if (!this.broken)
/*      */     {
/*  382 */       int msz = this.managed.size();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       int shrink_count;
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  392 */       if ((shrink_count = msz - this.pending_removes - this.target_pool_size) > 0) {
/*  393 */         shrinkPool(shrink_count); } else { int expand_count;
/*  394 */         if ((expand_count = this.target_pool_size - (msz + this.pending_acquires)) > 0)
/*  395 */           expandPool(expand_count);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private synchronized void incrementPendingAcquires() {
/*  401 */     this.pending_acquires += 1;
/*      */     
/*  403 */     if (logger.isLoggable(MLevel.FINEST)) {
/*  404 */       logger.finest("incremented pending_acquires: " + this.pending_acquires);
/*      */     }
/*      */   }
/*      */   
/*      */   private synchronized void incrementPendingRemoves()
/*      */   {
/*  410 */     this.pending_removes += 1;
/*      */     
/*  412 */     if (logger.isLoggable(MLevel.FINEST)) {
/*  413 */       logger.finest("incremented pending_removes: " + this.pending_removes);
/*      */     }
/*      */   }
/*      */   
/*      */   private synchronized void decrementPendingAcquires()
/*      */   {
/*  419 */     this.pending_acquires -= 1;
/*      */     
/*  421 */     if (logger.isLoggable(MLevel.FINEST)) {
/*  422 */       logger.finest("decremented pending_acquires: " + this.pending_acquires);
/*      */     }
/*      */   }
/*      */   
/*      */   private synchronized void decrementPendingRemoves()
/*      */   {
/*  428 */     this.pending_removes -= 1;
/*      */     
/*  430 */     if (logger.isLoggable(MLevel.FINEST)) {
/*  431 */       logger.finest("decremented pending_removes: " + this.pending_removes);
/*      */     }
/*      */   }
/*      */   
/*      */   private synchronized void recheckResizePool()
/*      */   {
/*  437 */     _recheckResizePool();
/*      */   }
/*      */   
/*      */   private void expandPool(int count)
/*      */   {
/*  442 */     assert (Thread.holdsLock(this));
/*      */     
/*      */ 
/*      */ 
/*  446 */     if (USE_SCATTERED_ACQUIRE_TASK)
/*      */     {
/*  448 */       for (int i = 0; i < count; i++) {
/*  449 */         this.taskRunner.postRunnable(new ScatteredAcquireTask());
/*      */       }
/*      */       
/*      */     } else {
/*  453 */       for (int i = 0; i < count; i++) {
/*  454 */         this.taskRunner.postRunnable(new AcquireTask());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void shrinkPool(int count)
/*      */   {
/*  461 */     assert (Thread.holdsLock(this));
/*      */     
/*  463 */     for (int i = 0; i < count; i++) {
/*  464 */       this.taskRunner.postRunnable(new RemoveTask());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object checkoutResource(long timeout)
/*      */     throws TimeoutException, ResourcePoolException, InterruptedException
/*      */   {
/*  477 */     Object resc = prelimCheckoutResource(timeout);
/*      */     
/*  479 */     boolean refurb = attemptRefurbishResourceOnCheckout(resc);
/*      */     
/*  481 */     synchronized (this)
/*      */     {
/*  483 */       if (!refurb)
/*      */       {
/*  485 */         removeResource(resc);
/*  486 */         ensureMinResources();
/*  487 */         resc = null;
/*      */       }
/*      */       else
/*      */       {
/*  491 */         asyncFireResourceCheckedOut(resc, this.managed.size(), this.unused.size(), this.excluded.size());
/*  492 */         trace();
/*      */         
/*  494 */         PunchCard card = (PunchCard)this.managed.get(resc);
/*  495 */         if (card == null)
/*      */         {
/*  497 */           if (logger.isLoggable(MLevel.FINE)) {
/*  498 */             logger.fine("Resource " + resc + " was removed from the pool while it was being checked out " + " or refurbished for checkout.");
/*      */           }
/*  500 */           resc = null;
/*      */         }
/*      */         else
/*      */         {
/*  504 */           card.checkout_time = System.currentTimeMillis();
/*  505 */           if (this.debug_store_checkout_exceptions) {
/*  506 */             card.checkoutStackTraceException = new Exception("DEBUG ONLY: Overdue resource check-out stack trace.");
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  513 */     if (resc == null) {
/*  514 */       return checkoutResource(timeout);
/*      */     }
/*  516 */     return resc;
/*      */   }
/*      */   
/*      */   private synchronized Object prelimCheckoutResource(long timeout)
/*      */     throws TimeoutException, ResourcePoolException, InterruptedException
/*      */   {
/*      */     try
/*      */     {
/*  524 */       ensureNotBroken();
/*      */       
/*  526 */       int available = this.unused.size();
/*  527 */       if (available == 0)
/*      */       {
/*  529 */         int msz = this.managed.size();
/*      */         
/*  531 */         if (msz < this.max)
/*      */         {
/*      */ 
/*      */ 
/*  535 */           int desired_target = msz + this.acquireWaiters.size() + 1;
/*      */           
/*  537 */           if (logger.isLoggable(MLevel.FINER)) {
/*  538 */             logger.log(MLevel.FINER, "acquire test -- pool size: " + msz + "; target_pool_size: " + this.target_pool_size + "; desired target? " + desired_target);
/*      */           }
/*  540 */           if (desired_target >= this.target_pool_size)
/*      */           {
/*      */ 
/*  543 */             desired_target = Math.max(desired_target, this.target_pool_size + this.inc);
/*      */             
/*      */ 
/*  546 */             this.target_pool_size = Math.max(Math.min(this.max, desired_target), this.min);
/*      */             
/*  548 */             _recheckResizePool();
/*      */           }
/*      */           
/*      */ 
/*      */         }
/*  553 */         else if (logger.isLoggable(MLevel.FINER)) {
/*  554 */           logger.log(MLevel.FINER, "acquire test -- pool is already maxed out. [managed: " + msz + "; max: " + this.max + "]");
/*      */         }
/*      */         
/*  557 */         awaitAvailable(timeout);
/*      */       }
/*      */       
/*  560 */       Object resc = this.unused.get(0);
/*      */       
/*      */ 
/*      */ 
/*  564 */       if (this.idleCheckResources.contains(resc))
/*      */       {
/*  566 */         if (logger.isLoggable(MLevel.FINER)) {
/*  567 */           logger.log(MLevel.FINER, "Resource we want to check out is in idleCheck! (waiting until idle-check completes.) [" + this + "]");
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  575 */         Thread t = Thread.currentThread();
/*      */         try
/*      */         {
/*  578 */           this.otherWaiters.add(t);
/*  579 */           wait(timeout);
/*  580 */           ensureNotBroken();
/*      */         }
/*      */         finally {
/*  583 */           this.otherWaiters.remove(t); }
/*  584 */         return prelimCheckoutResource(timeout);
/*      */       }
/*  586 */       if (shouldExpire(resc))
/*      */       {
/*  588 */         removeResource(resc);
/*  589 */         ensureMinResources();
/*  590 */         return prelimCheckoutResource(timeout);
/*      */       }
/*      */       
/*      */ 
/*  594 */       this.unused.remove(0);
/*  595 */       return resc;
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (com.mchange.v2.util.ResourceClosedException e)
/*      */     {
/*      */ 
/*  602 */       if (logger.isLoggable(MLevel.SEVERE)) {
/*  603 */         logger.log(MLevel.SEVERE, this + " -- the pool was found to be closed or broken during an attempt to check out a resource.", e);
/*      */       }
/*  605 */       unexpectedBreak();
/*  606 */       throw e;
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (InterruptedException e)
/*      */     {
/*      */ 
/*  613 */       if (this.broken)
/*      */       {
/*  615 */         if (logger.isLoggable(MLevel.FINER)) {
/*  616 */           logger.log(MLevel.FINER, this + " -- an attempt to checkout a resource was interrupted, because the pool is now closed. " + "[Thread: " + Thread.currentThread().getName() + ']', e);
/*      */ 
/*      */ 
/*      */         }
/*  620 */         else if (logger.isLoggable(MLevel.INFO)) {
/*  621 */           logger.log(MLevel.INFO, this + " -- an attempt to checkout a resource was interrupted, because the pool is now closed. " + "[Thread: " + Thread.currentThread().getName() + ']');
/*      */ 
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*  627 */       else if (logger.isLoggable(MLevel.WARNING))
/*      */       {
/*  629 */         logger.log(MLevel.WARNING, this + " -- an attempt to checkout a resource was interrupted, and the pool is still live: some other thread " + "must have either interrupted the Thread attempting checkout!", e);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  635 */       throw e;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public synchronized void checkinResource(Object resc)
/*      */     throws ResourcePoolException
/*      */   {
/*      */     try
/*      */     {
/*  646 */       if (this.managed.keySet().contains(resc)) {
/*  647 */         doCheckinManaged(resc);
/*  648 */       } else if (this.excluded.contains(resc)) {
/*  649 */         doCheckinExcluded(resc);
/*  650 */       } else if (isFormerResource(resc))
/*      */       {
/*  652 */         if (logger.isLoggable(MLevel.FINER)) {
/*  653 */           logger.finer("Resource " + resc + " checked-in after having been checked-in already, or checked-in after " + " having being destroyed for being checked-out too long.");
/*      */         }
/*      */       }
/*      */       else
/*  657 */         throw new ResourcePoolException("ResourcePool" + (this.broken ? " [BROKEN!]" : "") + ": Tried to check-in a foreign resource!");
/*  658 */       trace();
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (com.mchange.v2.util.ResourceClosedException e)
/*      */     {
/*      */ 
/*      */ 
/*  666 */       if (logger.isLoggable(MLevel.SEVERE)) {
/*  667 */         logger.log(MLevel.SEVERE, this + " - checkinResource( ... ) -- even broken pools should allow checkins without exception. probable resource pool bug.", e);
/*      */       }
/*      */       
/*      */ 
/*  671 */       unexpectedBreak();
/*  672 */       throw e;
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized void checkinAll()
/*      */     throws ResourcePoolException
/*      */   {
/*      */     try
/*      */     {
/*  681 */       java.util.Set checkedOutNotExcluded = new HashSet(this.managed.keySet());
/*  682 */       checkedOutNotExcluded.removeAll(this.unused);
/*  683 */       for (java.util.Iterator ii = checkedOutNotExcluded.iterator(); ii.hasNext();)
/*  684 */         doCheckinManaged(ii.next());
/*  685 */       for (ii = this.excluded.iterator(); ii.hasNext();) {
/*  686 */         doCheckinExcluded(ii.next());
/*      */       }
/*      */     }
/*      */     catch (com.mchange.v2.util.ResourceClosedException e)
/*      */     {
/*      */       java.util.Iterator ii;
/*      */       
/*      */ 
/*  694 */       if (logger.isLoggable(MLevel.SEVERE)) {
/*  695 */         logger.log(MLevel.SEVERE, this + " - checkinAll() -- even broken pools should allow checkins without exception. probable resource pool bug.", e);
/*      */       }
/*      */       
/*      */ 
/*  699 */       unexpectedBreak();
/*  700 */       throw e;
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized int statusInPool(Object resc)
/*      */     throws ResourcePoolException
/*      */   {
/*      */     try
/*      */     {
/*  709 */       if (this.unused.contains(resc))
/*  710 */         return 0;
/*  711 */       if ((this.managed.keySet().contains(resc)) || (this.excluded.contains(resc))) {
/*  712 */         return 1;
/*      */       }
/*  714 */       return -1;
/*      */ 
/*      */     }
/*      */     catch (com.mchange.v2.util.ResourceClosedException e)
/*      */     {
/*  719 */       if (logger.isLoggable(MLevel.SEVERE))
/*  720 */         logger.log(MLevel.SEVERE, "Apparent pool break.", e);
/*  721 */       unexpectedBreak();
/*  722 */       throw e;
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized void markBroken(Object resc)
/*      */   {
/*      */     try
/*      */     {
/*  730 */       if (logger.isLoggable(MLevel.FINER)) {
/*  731 */         logger.log(MLevel.FINER, "Resource " + resc + " marked broken by pool (" + this + ").");
/*      */       }
/*  733 */       _markBroken(resc);
/*  734 */       ensureMinResources();
/*      */ 
/*      */     }
/*      */     catch (com.mchange.v2.util.ResourceClosedException e)
/*      */     {
/*  739 */       if (logger.isLoggable(MLevel.SEVERE))
/*  740 */         logger.log(MLevel.SEVERE, "Apparent pool break.", e);
/*  741 */       unexpectedBreak();
/*      */     }
/*      */   }
/*      */   
/*      */   public int getMinPoolSize()
/*      */   {
/*  747 */     return this.min;
/*      */   }
/*      */   
/*      */   public int getMaxPoolSize() {
/*  751 */     return this.max;
/*      */   }
/*      */   
/*      */   public synchronized int getPoolSize() throws ResourcePoolException {
/*  755 */     return this.managed.size();
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
/*      */   public synchronized int getAvailableCount()
/*      */   {
/*  772 */     return this.unused.size();
/*      */   }
/*      */   
/*  775 */   public synchronized int getExcludedCount() { return this.excluded.size(); }
/*      */   
/*      */   public synchronized int getAwaitingCheckinCount() {
/*  778 */     return this.managed.size() - this.unused.size() + this.excluded.size();
/*      */   }
/*      */   
/*      */   public synchronized void resetPool()
/*      */   {
/*      */     try {
/*  784 */       for (java.util.Iterator ii = cloneOfManaged().keySet().iterator(); ii.hasNext();)
/*  785 */         markBrokenNoEnsureMinResources(ii.next());
/*  786 */       ensureMinResources();
/*      */ 
/*      */     }
/*      */     catch (com.mchange.v2.util.ResourceClosedException e)
/*      */     {
/*  791 */       if (logger.isLoggable(MLevel.SEVERE))
/*  792 */         logger.log(MLevel.SEVERE, "Apparent pool break.", e);
/*  793 */       unexpectedBreak();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public synchronized void close()
/*      */     throws ResourcePoolException
/*      */   {
/*  803 */     close(true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void finalize()
/*      */     throws Throwable
/*      */   {
/*  812 */     if (!this.broken) {
/*  813 */       close();
/*      */     }
/*      */   }
/*      */   
/*      */   public void addResourcePoolListener(ResourcePoolListener rpl)
/*      */   {
/*  819 */     if (!supportsEvents()) {
/*  820 */       throw new RuntimeException(this + " does not support ResourcePoolEvents. " + "Probably it was constructed by a BasicResourceFactory configured not to support such events.");
/*      */     }
/*      */     
/*  823 */     this.rpes.addResourcePoolListener(rpl);
/*      */   }
/*      */   
/*      */ 
/*      */   public void removeResourcePoolListener(ResourcePoolListener rpl)
/*      */   {
/*  829 */     if (!supportsEvents()) {
/*  830 */       throw new RuntimeException(this + " does not support ResourcePoolEvents. " + "Probably it was constructed by a BasicResourceFactory configured not to support such events.");
/*      */     }
/*      */     
/*  833 */     this.rpes.removeResourcePoolListener(rpl);
/*      */   }
/*      */   
/*      */   private synchronized boolean isForceKillAcquiresPending() {
/*  837 */     return this.force_kill_acquires;
/*      */   }
/*      */   
/*      */ 
/*      */   private synchronized void forceKillAcquires()
/*      */     throws InterruptedException
/*      */   {
/*  844 */     Thread t = Thread.currentThread();
/*      */     
/*      */     try
/*      */     {
/*  848 */       this.force_kill_acquires = true;
/*  849 */       notifyAll();
/*  850 */       while (this.acquireWaiters.size() > 0)
/*      */       {
/*  852 */         this.otherWaiters.add(t);
/*  853 */         wait();
/*      */       }
/*  855 */       this.force_kill_acquires = false;
/*      */     }
/*      */     finally {
/*  858 */       this.otherWaiters.remove(t);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private synchronized void unexpectedBreak()
/*      */   {
/*  865 */     if (logger.isLoggable(MLevel.SEVERE))
/*  866 */       logger.log(MLevel.SEVERE, this + " -- Unexpectedly broken!!!", new ResourcePoolException("Unexpected Break Stack Trace!"));
/*  867 */     close(false);
/*      */   }
/*      */   
/*      */   private boolean canFireEvents()
/*      */   {
/*  872 */     return (this.asyncEventQueue != null) && (!isBroken());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void asyncFireResourceAcquired(final Object resc, final int pool_size, final int available_size, final int removed_but_unreturned_size)
/*      */   {
/*  880 */     if (canFireEvents())
/*      */     {
/*  882 */       Runnable r = new Runnable()
/*      */       {
/*      */ 
/*  885 */         public void run() { BasicResourcePool.this.rpes.fireResourceAcquired(resc, pool_size, available_size, removed_but_unreturned_size); }
/*  886 */       };
/*  887 */       this.asyncEventQueue.postRunnable(r);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void asyncFireResourceCheckedIn(final Object resc, final int pool_size, final int available_size, final int removed_but_unreturned_size)
/*      */   {
/*  897 */     if (canFireEvents())
/*      */     {
/*  899 */       Runnable r = new Runnable()
/*      */       {
/*      */ 
/*  902 */         public void run() { BasicResourcePool.this.rpes.fireResourceCheckedIn(resc, pool_size, available_size, removed_but_unreturned_size); }
/*  903 */       };
/*  904 */       this.asyncEventQueue.postRunnable(r);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void asyncFireResourceCheckedOut(final Object resc, final int pool_size, final int available_size, final int removed_but_unreturned_size)
/*      */   {
/*  914 */     if (canFireEvents())
/*      */     {
/*  916 */       Runnable r = new Runnable()
/*      */       {
/*      */ 
/*  919 */         public void run() { BasicResourcePool.this.rpes.fireResourceCheckedOut(resc, pool_size, available_size, removed_but_unreturned_size); }
/*  920 */       };
/*  921 */       this.asyncEventQueue.postRunnable(r);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void asyncFireResourceRemoved(final Object resc, final boolean checked_out_resource, final int pool_size, final int available_size, final int removed_but_unreturned_size)
/*      */   {
/*  932 */     if (canFireEvents())
/*      */     {
/*      */ 
/*      */ 
/*  936 */       Runnable r = new Runnable()
/*      */       {
/*      */         public void run()
/*      */         {
/*  940 */           BasicResourcePool.this.rpes.fireResourceRemoved(resc, checked_out_resource, pool_size, available_size, removed_but_unreturned_size);
/*      */         }
/*      */         
/*  943 */       };
/*  944 */       this.asyncEventQueue.postRunnable(r);
/*      */     }
/*      */   }
/*      */   
/*      */   private void destroyResource(Object resc)
/*      */   {
/*  950 */     destroyResource(resc, false);
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
/*      */ 
/*      */ 
/*      */   private void destroyResource(final Object resc, boolean synchronous)
/*      */   {
/*  980 */     Runnable r = new Runnable()
/*      */     {
/*      */       public void run()
/*      */       {
/*      */         try
/*      */         {
/*  961 */           if (BasicResourcePool.logger.isLoggable(MLevel.FINER)) {
/*  962 */             BasicResourcePool.logger.log(MLevel.FINER, "Preparing to destroy resource: " + resc);
/*      */           }
/*  964 */           BasicResourcePool.this.mgr.destroyResource(resc);
/*      */           
/*  966 */           if (BasicResourcePool.logger.isLoggable(MLevel.FINER)) {
/*  967 */             BasicResourcePool.logger.log(MLevel.FINER, "Successfully destroyed resource: " + resc);
/*      */           }
/*      */         }
/*      */         catch (Exception e) {
/*  971 */           if (BasicResourcePool.logger.isLoggable(MLevel.WARNING)) {
/*  972 */             BasicResourcePool.logger.log(MLevel.WARNING, "Failed to destroy resource: " + resc, e);
/*      */           }
/*      */         }
/*      */       }
/*      */     };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  981 */     if ((synchronous) || (this.broken))
/*      */     {
/*  983 */       if ((logger.isLoggable(MLevel.FINEST)) && (!this.broken) && (Boolean.TRUE.equals(com.mchange.v2.lang.ThreadUtils.reflectiveHoldsLock(this)))) {
/*  984 */         logger.log(MLevel.FINEST, this + ": Destroyiong a resource on an active pool, synchronousy while holding pool's lock! " + "(not a bug, but a potential bottleneck... is there a good reason for this?)", new Exception("DEBUG STACK TRACE"));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  989 */       r.run();
/*      */     }
/*      */     else {
/*      */       try {
/*  993 */         this.taskRunner.postRunnable(r);
/*      */       }
/*      */       catch (Exception e) {
/*  996 */         if (logger.isLoggable(MLevel.FINER)) {
/*  997 */           logger.log(MLevel.FINER, "AsynchronousRunner refused to accept task to destroy resource. It is probably shared, and has probably been closed underneath us. Reverting to synchronous destruction. This is not usually a problem.", e);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1002 */         destroyResource(resc, true);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void doAcquire()
/*      */     throws Exception
/*      */   {
/* 1012 */     assert (!Thread.holdsLock(this));
/*      */     
/* 1014 */     Object resc = this.mgr.acquireResource();
/*      */     
/* 1016 */     boolean destroy = false;
/*      */     
/*      */ 
/* 1019 */     synchronized (this)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1026 */       int msz = this.managed.size();
/* 1027 */       if (msz < this.target_pool_size) {
/* 1028 */         assimilateResource(resc);
/*      */       } else {
/* 1030 */         destroy = true;
/*      */       }
/*      */     }
/* 1033 */     if (destroy)
/*      */     {
/* 1035 */       this.mgr.destroyResource(resc);
/* 1036 */       if (logger.isLoggable(MLevel.FINER)) {
/* 1037 */         logger.log(MLevel.FINER, "destroying overacquired resource: " + resc);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized void setPoolSize(int sz) throws ResourcePoolException
/*      */   {
/*      */     try
/*      */     {
/* 1046 */       setTargetPoolSize(sz);
/* 1047 */       while (this.managed.size() != sz) {
/* 1048 */         wait();
/*      */       }
/*      */     }
/*      */     catch (Exception e) {
/* 1052 */       String msg = "An exception occurred while trying to set the pool size!";
/* 1053 */       if (logger.isLoggable(MLevel.FINER))
/* 1054 */         logger.log(MLevel.FINER, msg, e);
/* 1055 */       throw ResourcePoolUtils.convertThrowable(msg, e);
/*      */     }
/*      */   }
/*      */   
/*      */   public synchronized void setTargetPoolSize(int sz)
/*      */   {
/* 1061 */     if (sz > this.max)
/*      */     {
/* 1063 */       throw new IllegalArgumentException("Requested size [" + sz + "] is greater than max [" + this.max + "].");
/*      */     }
/*      */     
/*      */ 
/* 1067 */     if (sz < this.min)
/*      */     {
/* 1069 */       throw new IllegalArgumentException("Requested size [" + sz + "] is less than min [" + this.min + "].");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1074 */     this.target_pool_size = sz;
/*      */     
/* 1076 */     _recheckResizePool();
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void markBrokenNoEnsureMinResources(Object resc)
/*      */   {
/* 1110 */     assert (Thread.holdsLock(this));
/*      */     
/*      */     try
/*      */     {
/* 1114 */       _markBroken(resc);
/*      */ 
/*      */     }
/*      */     catch (com.mchange.v2.util.ResourceClosedException e)
/*      */     {
/* 1119 */       if (logger.isLoggable(MLevel.SEVERE))
/* 1120 */         logger.log(MLevel.SEVERE, "Apparent pool break.", e);
/* 1121 */       unexpectedBreak();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   private void _markBroken(Object resc)
/*      */   {
/* 1128 */     assert (Thread.holdsLock(this));
/*      */     
/* 1130 */     if (this.unused.contains(resc)) {
/* 1131 */       removeResource(resc);
/*      */     } else {
/* 1133 */       excludeResource(resc);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public synchronized void close(boolean close_checked_out_resources)
/*      */   {
/* 1141 */     if (!this.broken)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1147 */       this.broken = true;
/* 1148 */       final java.util.Collection cleanupResources = close_checked_out_resources ? cloneOfManaged().keySet() : cloneOfUnused();
/* 1149 */       if (this.cullTask != null)
/* 1150 */         this.cullTask.cancel();
/* 1151 */       if (this.idleRefurbishTask != null) {
/* 1152 */         this.idleRefurbishTask.cancel();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1161 */       this.managed.keySet().removeAll(cleanupResources);
/* 1162 */       this.unused.removeAll(cleanupResources);
/* 1163 */       Thread resourceDestroyer = new Thread("Resource Destroyer in BasicResourcePool.close()")
/*      */       {
/*      */         public void run()
/*      */         {
/* 1167 */           for (java.util.Iterator ii = cleanupResources.iterator(); ii.hasNext();)
/*      */           {
/*      */             try
/*      */             {
/* 1171 */               Object resc = ii.next();
/*      */               
/*      */ 
/* 1174 */               BasicResourcePool.this.destroyResource(resc, true);
/*      */ 
/*      */ 
/*      */             }
/*      */             catch (Exception e)
/*      */             {
/*      */ 
/* 1181 */               if (BasicResourcePool.logger.isLoggable(MLevel.FINE)) {
/* 1182 */                 BasicResourcePool.logger.log(MLevel.FINE, "BasicResourcePool -- A resource couldn't be cleaned up on close()", e);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 1187 */       };
/* 1188 */       resourceDestroyer.start();
/*      */       
/* 1190 */       for (java.util.Iterator ii = this.acquireWaiters.iterator(); ii.hasNext();)
/* 1191 */         ((Thread)ii.next()).interrupt();
/* 1192 */       for (java.util.Iterator ii = this.otherWaiters.iterator(); ii.hasNext();)
/* 1193 */         ((Thread)ii.next()).interrupt();
/* 1194 */       if (this.factory != null) {
/* 1195 */         this.factory.markBroken(this);
/*      */ 
/*      */       }
/*      */       
/*      */ 
/*      */     }
/* 1201 */     else if (logger.isLoggable(MLevel.WARNING)) {
/* 1202 */       logger.warning(this + " -- close() called multiple times.");
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void doCheckinManaged(final Object resc)
/*      */     throws ResourcePoolException
/*      */   {
/* 1213 */     assert (Thread.holdsLock(this));
/*      */     
/* 1215 */     if (this.unused.contains(resc))
/*      */     {
/*      */ 
/* 1218 */       throw new ResourcePoolException("Tried to check-in an already checked-in resource: " + resc);
/*      */     }
/* 1220 */     if (this.broken) {
/* 1221 */       removeResource(resc, true);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1258 */       Runnable doMe = new Runnable()
/*      */       {
/*      */         public void run()
/*      */         {
/* 1228 */           boolean resc_okay = BasicResourcePool.this.attemptRefurbishResourceOnCheckin(resc);
/* 1229 */           synchronized (BasicResourcePool.this)
/*      */           {
/* 1231 */             PunchCard card = (PunchCard)BasicResourcePool.this.managed.get(resc);
/*      */             
/* 1233 */             if ((resc_okay) && (card != null))
/*      */             {
/* 1235 */               BasicResourcePool.this.unused.add(0, resc);
/*      */               
/* 1237 */               card.last_checkin_time = System.currentTimeMillis();
/* 1238 */               card.checkout_time = -1L;
/*      */             }
/*      */             else
/*      */             {
/* 1242 */               if (card != null) {
/* 1243 */                 card.checkout_time = -1L;
/*      */               }
/* 1245 */               BasicResourcePool.this.removeResource(resc);
/* 1246 */               BasicResourcePool.this.ensureMinResources();
/*      */               
/* 1248 */               if ((card == null) && (BasicResourcePool.logger.isLoggable(MLevel.FINE))) {
/* 1249 */                 BasicResourcePool.logger.fine("Resource " + resc + " was removed from the pool during its refurbishment for checkin.");
/*      */               }
/*      */             }
/* 1252 */             BasicResourcePool.this.asyncFireResourceCheckedIn(resc, BasicResourcePool.this.managed.size(), BasicResourcePool.this.unused.size(), BasicResourcePool.this.excluded.size());
/* 1253 */             BasicResourcePool.this.notifyAll();
/*      */           }
/*      */           
/*      */         }
/*      */         
/* 1258 */       };
/* 1259 */       this.taskRunner.postRunnable(doMe);
/*      */     }
/*      */   }
/*      */   
/*      */   private void doCheckinExcluded(Object resc)
/*      */   {
/* 1265 */     assert (Thread.holdsLock(this));
/*      */     
/* 1267 */     this.excluded.remove(resc);
/* 1268 */     destroyResource(resc);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void awaitAvailable(long timeout)
/*      */     throws InterruptedException, TimeoutException, ResourcePoolException
/*      */   {
/* 1276 */     assert (Thread.holdsLock(this));
/*      */     
/* 1278 */     if (this.force_kill_acquires) {
/* 1279 */       throw new ResourcePoolException("A ResourcePool cannot acquire a new resource -- the factory or source appears to be down.");
/*      */     }
/* 1281 */     Thread t = Thread.currentThread();
/*      */     try
/*      */     {
/* 1284 */       this.acquireWaiters.add(t);
/*      */       
/*      */ 
/* 1287 */       long start = timeout > 0L ? System.currentTimeMillis() : -1L;
/*      */       
/*      */ 
/* 1290 */       if (logger.isLoggable(MLevel.FINE)) {
/* 1291 */         logger.fine("awaitAvailable(): " + (this.exampleResource != null ? this.exampleResource : "[unknown]"));
/*      */       }
/*      */       
/*      */ 
/* 1295 */       trace();
/*      */       int avail;
/* 1297 */       while ((avail = this.unused.size()) == 0)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1312 */         if ((this.pending_acquires == 0) && (this.managed.size() < this.max)) {
/* 1313 */           _recheckResizePool();
/*      */         }
/* 1315 */         wait(timeout);
/* 1316 */         if ((timeout > 0L) && (System.currentTimeMillis() - start > timeout))
/* 1317 */           throw new TimeoutException("A client timed out while waiting to acquire a resource from " + this + " -- timeout at awaitAvailable()");
/* 1318 */         if (this.force_kill_acquires)
/* 1319 */           throw new CannotAcquireResourceException("A ResourcePool could not acquire a resource from its primary factory or source.");
/* 1320 */         ensureNotBroken();
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1325 */       this.acquireWaiters.remove(t);
/* 1326 */       if (this.acquireWaiters.size() == 0) {
/* 1327 */         notifyAll();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void assimilateResource(Object resc) throws Exception {
/* 1333 */     assert (Thread.holdsLock(this));
/*      */     
/* 1335 */     this.managed.put(resc, new PunchCard());
/* 1336 */     this.unused.add(0, resc);
/*      */     
/* 1338 */     asyncFireResourceAcquired(resc, this.managed.size(), this.unused.size(), this.excluded.size());
/* 1339 */     notifyAll();
/* 1340 */     trace();
/* 1341 */     if (this.exampleResource == null) {
/* 1342 */       this.exampleResource = resc;
/*      */     }
/*      */   }
/*      */   
/*      */   private void synchronousRemoveArbitraryResource()
/*      */   {
/* 1348 */     assert (!Thread.holdsLock(this));
/*      */     
/* 1350 */     Object removeMe = null;
/*      */     
/* 1352 */     synchronized (this)
/*      */     {
/* 1354 */       if (this.unused.size() > 0)
/*      */       {
/* 1356 */         removeMe = this.unused.get(0);
/* 1357 */         this.managed.remove(removeMe);
/* 1358 */         this.unused.remove(removeMe);
/*      */       }
/*      */       else
/*      */       {
/* 1362 */         java.util.Set checkedOut = cloneOfManaged().keySet();
/* 1363 */         if (checkedOut.isEmpty())
/*      */         {
/* 1365 */           unexpectedBreak();
/* 1366 */           logger.severe("A pool from which a resource is requested to be removed appears to have no managed resources?!");
/*      */         }
/*      */         else {
/* 1369 */           excludeResource(checkedOut.iterator().next());
/*      */         }
/*      */       }
/*      */     }
/* 1373 */     if (removeMe != null)
/* 1374 */       destroyResource(removeMe, true);
/*      */   }
/*      */   
/*      */   private void removeResource(Object resc) {
/* 1378 */     removeResource(resc, false);
/*      */   }
/*      */   
/*      */   private void removeResource(Object resc, boolean synchronous) {
/* 1382 */     assert (Thread.holdsLock(this));
/*      */     
/* 1384 */     PunchCard pc = (PunchCard)this.managed.remove(resc);
/*      */     
/* 1386 */     if (pc != null)
/*      */     {
/* 1388 */       if ((pc.checkout_time > 0L) && (!this.broken))
/*      */       {
/* 1390 */         if (logger.isLoggable(MLevel.INFO))
/*      */         {
/* 1392 */           logger.info("A checked-out resource is overdue, and will be destroyed: " + resc);
/* 1393 */           if (pc.checkoutStackTraceException != null)
/*      */           {
/* 1395 */             logger.log(MLevel.INFO, "Logging the stack trace by which the overdue resource was checked-out.", pc.checkoutStackTraceException);
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */       }
/*      */     }
/* 1402 */     else if (logger.isLoggable(MLevel.FINE)) {
/* 1403 */       logger.fine("Resource " + resc + " was removed twice. (Lotsa reasons a resource can be removed, sometimes simultaneously. It's okay)");
/*      */     }
/* 1405 */     this.unused.remove(resc);
/* 1406 */     destroyResource(resc, synchronous);
/* 1407 */     addToFormerResources(resc);
/* 1408 */     asyncFireResourceRemoved(resc, false, this.managed.size(), this.unused.size(), this.excluded.size());
/*      */     
/* 1410 */     trace();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void excludeResource(Object resc)
/*      */   {
/* 1418 */     assert (Thread.holdsLock(this));
/*      */     
/* 1420 */     this.managed.remove(resc);
/* 1421 */     this.excluded.add(resc);
/* 1422 */     if (this.unused.contains(resc))
/* 1423 */       throw new InternalError("We should only \"exclude\" checked-out resources!");
/* 1424 */     asyncFireResourceRemoved(resc, true, this.managed.size(), this.unused.size(), this.excluded.size());
/*      */   }
/*      */   
/*      */   private void removeTowards(int new_sz)
/*      */   {
/* 1429 */     assert (Thread.holdsLock(this));
/*      */     
/* 1431 */     int num_to_remove = this.managed.size() - new_sz;
/* 1432 */     int count = 0;
/* 1433 */     java.util.Iterator ii = cloneOfUnused().iterator();
/* 1434 */     for (; (ii.hasNext()) && (count < num_to_remove); 
/* 1435 */         count++)
/*      */     {
/* 1437 */       Object resc = ii.next();
/* 1438 */       removeResource(resc);
/*      */     }
/*      */   }
/*      */   
/*      */   private void cullExpired()
/*      */   {
/* 1444 */     assert (Thread.holdsLock(this));
/*      */     
/* 1446 */     if (logger.isLoggable(MLevel.FINER)) {
/* 1447 */       logger.log(MLevel.FINER, "BEGIN check for expired resources.  [" + this + "]");
/*      */     }
/*      */     
/* 1450 */     java.util.Collection checkMe = this.destroy_unreturned_resc_time > 0L ? cloneOfManaged().keySet() : cloneOfUnused();
/*      */     
/* 1452 */     for (java.util.Iterator ii = checkMe.iterator(); ii.hasNext();)
/*      */     {
/* 1454 */       Object resc = ii.next();
/* 1455 */       if (shouldExpire(resc))
/*      */       {
/* 1457 */         if (logger.isLoggable(MLevel.FINER)) {
/* 1458 */           logger.log(MLevel.FINER, "Removing expired resource: " + resc + " [" + this + "]");
/*      */         }
/* 1460 */         this.target_pool_size = Math.max(this.min, this.target_pool_size - 1);
/*      */         
/* 1462 */         removeResource(resc);
/*      */         
/* 1464 */         trace();
/*      */       }
/*      */     }
/* 1467 */     if (logger.isLoggable(MLevel.FINER))
/* 1468 */       logger.log(MLevel.FINER, "FINISHED check for expired resources.  [" + this + "]");
/* 1469 */     ensureMinResources();
/*      */   }
/*      */   
/*      */   private void checkIdleResources()
/*      */   {
/* 1474 */     assert (Thread.holdsLock(this));
/*      */     
/* 1476 */     java.util.List u = cloneOfUnused();
/* 1477 */     for (java.util.Iterator ii = u.iterator(); ii.hasNext();)
/*      */     {
/* 1479 */       Object resc = ii.next();
/* 1480 */       if (this.idleCheckResources.add(resc)) {
/* 1481 */         this.taskRunner.postRunnable(new AsyncTestIdleResourceTask(resc));
/*      */       }
/*      */     }
/* 1484 */     trace();
/*      */   }
/*      */   
/*      */   private boolean shouldExpire(Object resc)
/*      */   {
/* 1489 */     assert (Thread.holdsLock(this));
/*      */     
/* 1491 */     boolean expired = false;
/*      */     
/* 1493 */     PunchCard pc = (PunchCard)this.managed.get(resc);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1500 */     if (pc == null)
/*      */     {
/* 1502 */       if (logger.isLoggable(MLevel.FINE))
/* 1503 */         logger.fine("Resource " + resc + " was being tested for expiration, but has already been removed from the pool.");
/* 1504 */       return true;
/*      */     }
/*      */     
/* 1507 */     long now = System.currentTimeMillis();
/*      */     
/* 1509 */     if (pc.checkout_time < 0L)
/*      */     {
/* 1511 */       long idle_age = now - pc.last_checkin_time;
/* 1512 */       if (this.excess_max_idle_time > 0L)
/*      */       {
/* 1514 */         int msz = this.managed.size();
/* 1515 */         expired = (msz > this.min) && (idle_age > this.excess_max_idle_time);
/* 1516 */         if ((expired) && (logger.isLoggable(MLevel.FINER))) {
/* 1517 */           logger.log(MLevel.FINER, "EXPIRED excess idle resource: " + resc + " ---> idle_time: " + idle_age + "; excess_max_idle_time: " + this.excess_max_idle_time + "; pool_size: " + msz + "; min_pool_size: " + this.min + " [" + this + "]");
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1525 */       if ((!expired) && (this.max_idle_time > 0L))
/*      */       {
/* 1527 */         expired = idle_age > this.max_idle_time;
/* 1528 */         if ((expired) && (logger.isLoggable(MLevel.FINER))) {
/* 1529 */           logger.log(MLevel.FINER, "EXPIRED idle resource: " + resc + " ---> idle_time: " + idle_age + "; max_idle_time: " + this.max_idle_time + " [" + this + "]");
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1535 */       if ((!expired) && (this.max_resource_age > 0L))
/*      */       {
/* 1537 */         long abs_age = now - pc.acquisition_time;
/* 1538 */         expired = abs_age > this.max_resource_age;
/*      */         
/* 1540 */         if ((expired) && (logger.isLoggable(MLevel.FINER))) {
/* 1541 */           logger.log(MLevel.FINER, "EXPIRED old resource: " + resc + " ---> absolute_age: " + abs_age + "; max_absolute_age: " + this.max_resource_age + " [" + this + "]");
/*      */         }
/*      */         
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1550 */       long checkout_age = now - pc.checkout_time;
/* 1551 */       expired = checkout_age > this.destroy_unreturned_resc_time;
/*      */     }
/*      */     
/* 1554 */     return expired;
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
/*      */   private void ensureStartResources()
/*      */   {
/* 1567 */     recheckResizePool();
/*      */   }
/*      */   
/*      */   private void ensureMinResources() {
/* 1571 */     recheckResizePool();
/*      */   }
/*      */   
/*      */   private boolean attemptRefurbishResourceOnCheckout(Object resc) {
/* 1575 */     assert (!Thread.holdsLock(this));
/*      */     
/*      */     try
/*      */     {
/* 1579 */       this.mgr.refurbishResourceOnCheckout(resc);
/* 1580 */       return true;
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*      */ 
/*      */ 
/* 1588 */       if (logger.isLoggable(MLevel.FINE)) {
/* 1589 */         logger.log(MLevel.FINE, "A resource could not be refurbished for checkout. [" + resc + ']', e);
/*      */       }
/* 1591 */       synchronized (this)
/*      */       {
/* 1593 */         this.failed_checkouts += 1L;
/* 1594 */         setLastCheckoutFailure(e);
/*      */       } }
/* 1596 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   private boolean attemptRefurbishResourceOnCheckin(Object resc)
/*      */   {
/* 1602 */     assert (!Thread.holdsLock(this));
/*      */     
/*      */     try
/*      */     {
/* 1606 */       this.mgr.refurbishResourceOnCheckin(resc);
/* 1607 */       return true;
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*      */ 
/*      */ 
/* 1615 */       if (logger.isLoggable(MLevel.FINE)) {
/* 1616 */         logger.log(MLevel.FINE, "A resource could not be refurbished on checkin. [" + resc + ']', e);
/*      */       }
/* 1618 */       synchronized (this)
/*      */       {
/* 1620 */         this.failed_checkins += 1L;
/* 1621 */         setLastCheckinFailure(e);
/*      */       } }
/* 1623 */     return false;
/*      */   }
/*      */   
/*      */   private void ensureNotBroken()
/*      */     throws ResourcePoolException
/*      */   {
/* 1629 */     assert (Thread.holdsLock(this));
/*      */     
/* 1631 */     if (this.broken) {
/* 1632 */       throw new ResourcePoolException("Attempted to use a closed or broken resource pool");
/*      */     }
/*      */   }
/*      */   
/*      */   private void trace() {
/* 1637 */     assert (Thread.holdsLock(this));
/*      */     
/* 1639 */     if (logger.isLoggable(MLevel.FINEST))
/*      */     {
/* 1641 */       String exampleResStr = " (e.g. " + this.exampleResource + ")";
/*      */       
/*      */ 
/* 1644 */       logger.finest("trace " + this + " [managed: " + this.managed.size() + ", " + "unused: " + this.unused.size() + ", excluded: " + this.excluded.size() + ']' + exampleResStr);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private final HashMap cloneOfManaged()
/*      */   {
/* 1652 */     assert (Thread.holdsLock(this));
/*      */     
/* 1654 */     return (HashMap)this.managed.clone();
/*      */   }
/*      */   
/*      */   private final java.util.LinkedList cloneOfUnused()
/*      */   {
/* 1659 */     assert (Thread.holdsLock(this));
/*      */     
/* 1661 */     return (java.util.LinkedList)this.unused.clone();
/*      */   }
/*      */   
/*      */   private final HashSet cloneOfExcluded()
/*      */   {
/* 1666 */     assert (Thread.holdsLock(this));
/*      */     
/* 1668 */     return (HashSet)this.excluded.clone();
/*      */   }
/*      */   
/* 1671 */   class ScatteredAcquireTask implements Runnable { ScatteredAcquireTask(int x1, boolean x2, BasicResourcePool.1 x3) { this(x1, x2); }
/*      */     
/*      */ 
/*      */     ScatteredAcquireTask()
/*      */     {
/* 1676 */       this(BasicResourcePool.this.num_acq_attempts >= 0 ? BasicResourcePool.this.num_acq_attempts : -1, true);
/*      */     }
/*      */     
/*      */     private ScatteredAcquireTask(int attempts_remaining, boolean first_attempt) {
/* 1680 */       this.attempts_remaining = attempts_remaining;
/* 1681 */       if (first_attempt)
/*      */       {
/* 1683 */         BasicResourcePool.this.incrementPendingAcquires();
/* 1684 */         if (BasicResourcePool.logger.isLoggable(MLevel.FINEST)) {
/* 1685 */           BasicResourcePool.logger.finest("Starting acquisition series. Incremented pending_acquires [" + BasicResourcePool.this.pending_acquires + "], " + " attempts_remaining: " + attempts_remaining);
/*      */         }
/*      */         
/*      */ 
/*      */       }
/* 1690 */       else if (BasicResourcePool.logger.isLoggable(MLevel.FINEST)) {
/* 1691 */         BasicResourcePool.logger.finest("Continuing acquisition series. pending_acquires [" + BasicResourcePool.this.pending_acquires + "], " + " attempts_remaining: " + attempts_remaining);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     public void run()
/*      */     {
/*      */       try
/*      */       {
/* 1700 */         boolean fkap = BasicResourcePool.this.isForceKillAcquiresPending();
/* 1701 */         if (!fkap)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/* 1706 */           BasicResourcePool.this.doAcquire();
/*      */         }
/* 1708 */         BasicResourcePool.this.decrementPendingAcquires();
/* 1709 */         if (BasicResourcePool.logger.isLoggable(MLevel.FINEST)) {
/* 1710 */           BasicResourcePool.logger.finest("Acquisition series terminated " + (fkap ? "because force-kill-acquires is pending" : "successfully") + ". Decremented pending_acquires [" + BasicResourcePool.this.pending_acquires + "], " + " attempts_remaining: " + this.attempts_remaining);
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/* 1717 */         BasicResourcePool.this.setLastAcquisitionFailure(e);
/*      */         
/* 1719 */         if (this.attempts_remaining == 0)
/*      */         {
/* 1721 */           BasicResourcePool.this.decrementPendingAcquires();
/* 1722 */           if (BasicResourcePool.logger.isLoggable(MLevel.WARNING))
/*      */           {
/* 1724 */             BasicResourcePool.logger.log(MLevel.WARNING, this + " -- Acquisition Attempt Failed!!! Clearing pending acquires. " + "While trying to acquire a needed new resource, we failed " + "to succeed more than the maximum number of allowed " + "acquisition attempts (" + BasicResourcePool.this.num_acq_attempts + "). " + "Last acquisition attempt exception: ", e);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1732 */           if (BasicResourcePool.this.break_on_acquisition_failure)
/*      */           {
/*      */ 
/* 1735 */             if (BasicResourcePool.logger.isLoggable(MLevel.SEVERE)) {
/* 1736 */               BasicResourcePool.logger.severe("A RESOURCE POOL IS PERMANENTLY BROKEN! [" + this + "] " + "(because a series of " + BasicResourcePool.this.num_acq_attempts + " acquisition attempts " + "failed.)");
/*      */             }
/*      */             
/* 1739 */             BasicResourcePool.this.unexpectedBreak();
/*      */           }
/*      */           else {
/*      */             try {
/* 1743 */               BasicResourcePool.this.forceKillAcquires();
/*      */             }
/*      */             catch (InterruptedException ie) {
/* 1746 */               if (BasicResourcePool.logger.isLoggable(MLevel.WARNING)) {
/* 1747 */                 BasicResourcePool.logger.log(MLevel.WARNING, "Failed to force-kill pending acquisition attempts after acquisition failue,  due to an InterruptedException!", ie);
/*      */               }
/*      */               
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1754 */               BasicResourcePool.this.recheckResizePool();
/*      */             }
/*      */           }
/* 1757 */           if (BasicResourcePool.logger.isLoggable(MLevel.FINEST)) {
/* 1758 */             BasicResourcePool.logger.finest("Acquisition series terminated unsuccessfully. Decremented pending_acquires [" + BasicResourcePool.this.pending_acquires + "], " + " attempts_remaining: " + this.attempts_remaining);
/*      */ 
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/* 1768 */           MLevel logLevel = this.attempts_remaining > 0 ? MLevel.FINE : MLevel.INFO;
/* 1769 */           if (BasicResourcePool.logger.isLoggable(logLevel)) {
/* 1770 */             BasicResourcePool.logger.log(logLevel, "An exception occurred while acquiring a poolable resource. Will retry.", e);
/*      */           }
/* 1772 */           java.util.TimerTask doNextAcquire = new java.util.TimerTask()
/*      */           {
/*      */ 
/* 1775 */             public void run() { BasicResourcePool.this.taskRunner.postRunnable(new ScatteredAcquireTask(BasicResourcePool.this, ScatteredAcquireTask.this.attempts_remaining - 1, false, null)); }
/* 1776 */           };
/* 1777 */           BasicResourcePool.this.cullAndIdleRefurbishTimer.schedule(doNextAcquire, BasicResourcePool.this.acq_attempt_delay);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     int attempts_remaining;
/*      */   }
/*      */   
/*      */ 
/*      */   class AcquireTask
/*      */     implements Runnable
/*      */   {
/* 1790 */     boolean success = false;
/*      */     
/*      */     public AcquireTask() {
/* 1793 */       BasicResourcePool.this.incrementPendingAcquires();
/*      */     }
/*      */     
/*      */     public void run()
/*      */     {
/*      */       try {
/* 1799 */         Exception lastException = null;
/* 1800 */         for (int i = 0; shouldTry(i); i++)
/*      */         {
/*      */           try
/*      */           {
/* 1804 */             if (i > 0) {
/* 1805 */               Thread.sleep(BasicResourcePool.this.acq_attempt_delay);
/*      */             }
/*      */             
/*      */ 
/*      */ 
/* 1810 */             BasicResourcePool.this.doAcquire();
/*      */             
/* 1812 */             this.success = true;
/*      */ 
/*      */           }
/*      */           catch (InterruptedException e)
/*      */           {
/*      */ 
/* 1818 */             throw e;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           }
/*      */           catch (Exception e)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/* 1829 */             MLevel logLevel = BasicResourcePool.this.num_acq_attempts > 0 ? MLevel.FINE : MLevel.INFO;
/* 1830 */             if (BasicResourcePool.logger.isLoggable(logLevel)) {
/* 1831 */               BasicResourcePool.logger.log(logLevel, "An exception occurred while acquiring a poolable resource. Will retry.", e);
/*      */             }
/* 1833 */             lastException = e;
/* 1834 */             BasicResourcePool.this.setLastAcquisitionFailure(e);
/*      */           }
/*      */         }
/* 1837 */         if (!this.success)
/*      */         {
/* 1839 */           if (BasicResourcePool.logger.isLoggable(MLevel.WARNING))
/*      */           {
/* 1841 */             BasicResourcePool.logger.log(MLevel.WARNING, this + " -- Acquisition Attempt Failed!!! Clearing pending acquires. " + "While trying to acquire a needed new resource, we failed " + "to succeed more than the maximum number of allowed " + "acquisition attempts (" + BasicResourcePool.this.num_acq_attempts + "). " + (lastException == null ? "" : "Last acquisition attempt exception: "), lastException);
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1849 */           if (BasicResourcePool.this.break_on_acquisition_failure)
/*      */           {
/*      */ 
/* 1852 */             if (BasicResourcePool.logger.isLoggable(MLevel.SEVERE))
/* 1853 */               BasicResourcePool.logger.severe("A RESOURCE POOL IS PERMANENTLY BROKEN! [" + this + "]");
/* 1854 */             BasicResourcePool.this.unexpectedBreak();
/*      */           }
/*      */           else {
/* 1857 */             BasicResourcePool.this.forceKillAcquires();
/*      */           }
/*      */         } else {
/* 1860 */           BasicResourcePool.this.recheckResizePool();
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       catch (com.mchange.v2.util.ResourceClosedException e)
/*      */       {
/* 1867 */         if (BasicResourcePool.logger.isLoggable(MLevel.FINE)) {
/* 1868 */           BasicResourcePool.logger.log(MLevel.FINE, "a resource pool async thread died.", e);
/*      */         }
/* 1870 */         BasicResourcePool.this.unexpectedBreak();
/*      */       }
/*      */       catch (InterruptedException e)
/*      */       {
/* 1874 */         if (BasicResourcePool.logger.isLoggable(MLevel.WARNING))
/*      */         {
/* 1876 */           BasicResourcePool.logger.log(MLevel.WARNING, BasicResourcePool.this + " -- Thread unexpectedly interrupted while performing an acquisition attempt.", e);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1884 */         BasicResourcePool.this.recheckResizePool();
/*      */       }
/*      */       finally {
/* 1887 */         BasicResourcePool.this.decrementPendingAcquires();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private boolean shouldTry(int attempt_num)
/*      */     {
/* 1896 */       return (!this.success) && (!BasicResourcePool.this.isForceKillAcquiresPending()) && ((BasicResourcePool.this.num_acq_attempts <= 0) || (attempt_num < BasicResourcePool.this.num_acq_attempts));
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
/*      */   class RemoveTask
/*      */     implements Runnable
/*      */   {
/*      */     public RemoveTask()
/*      */     {
/* 1914 */       BasicResourcePool.this.incrementPendingRemoves();
/*      */     }
/*      */     
/*      */     public void run()
/*      */     {
/*      */       try {
/* 1920 */         BasicResourcePool.this.synchronousRemoveArbitraryResource();
/* 1921 */         BasicResourcePool.this.recheckResizePool();
/*      */       }
/*      */       finally {
/* 1924 */         BasicResourcePool.this.decrementPendingRemoves();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   class CullTask extends java.util.TimerTask {
/*      */     CullTask() {}
/*      */     
/*      */     public void run() {
/*      */       try {
/* 1934 */         if (BasicResourcePool.logger.isLoggable(MLevel.FINER))
/* 1935 */           BasicResourcePool.logger.log(MLevel.FINER, "Checking for expired resources - " + new java.util.Date() + " [" + BasicResourcePool.this + "]");
/* 1936 */         synchronized (BasicResourcePool.this) {
/* 1937 */           BasicResourcePool.this.cullExpired();
/*      */         }
/*      */         
/*      */       }
/*      */       catch (com.mchange.v2.util.ResourceClosedException e)
/*      */       {
/* 1943 */         if (BasicResourcePool.logger.isLoggable(MLevel.FINE)) {
/* 1944 */           BasicResourcePool.logger.log(MLevel.FINE, "a resource pool async thread died.", e);
/*      */         }
/* 1946 */         BasicResourcePool.this.unexpectedBreak();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   class CheckIdleResourcesTask
/*      */     extends java.util.TimerTask
/*      */   {
/*      */     CheckIdleResourcesTask() {}
/*      */     
/*      */     public void run()
/*      */     {
/*      */       try
/*      */       {
/* 1961 */         if (BasicResourcePool.logger.isLoggable(MLevel.FINER))
/* 1962 */           BasicResourcePool.logger.log(MLevel.FINER, "Refurbishing idle resources - " + new java.util.Date() + " [" + BasicResourcePool.this + "]");
/* 1963 */         synchronized (BasicResourcePool.this) {
/* 1964 */           BasicResourcePool.this.checkIdleResources();
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       catch (com.mchange.v2.util.ResourceClosedException e)
/*      */       {
/* 1971 */         if (BasicResourcePool.logger.isLoggable(MLevel.FINE)) {
/* 1972 */           BasicResourcePool.logger.log(MLevel.FINE, "a resource pool async thread died.", e);
/*      */         }
/* 1974 */         BasicResourcePool.this.unexpectedBreak();
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   class AsyncTestIdleResourceTask
/*      */     implements Runnable
/*      */   {
/*      */     Object resc;
/*      */     
/* 1985 */     boolean pending = true;
/*      */     boolean failed;
/*      */     
/*      */     AsyncTestIdleResourceTask(Object resc) {
/* 1989 */       this.resc = resc;
/*      */     }
/*      */     
/*      */     public void run() {
/* 1993 */       assert (!Thread.holdsLock(BasicResourcePool.this));
/*      */       
/*      */       try
/*      */       {
/*      */         try
/*      */         {
/* 1999 */           BasicResourcePool.this.mgr.refurbishIdleResource(this.resc);
/*      */         }
/*      */         catch (Exception e)
/*      */         {
/* 2003 */           if (BasicResourcePool.logger.isLoggable(MLevel.FINE)) {
/* 2004 */             BasicResourcePool.logger.log(MLevel.FINE, "BasicResourcePool: An idle resource is broken and will be purged. [" + this.resc + ']', e);
/*      */           }
/* 2006 */           synchronized (BasicResourcePool.this)
/*      */           {
/* 2008 */             if (BasicResourcePool.this.managed.keySet().contains(this.resc))
/*      */             {
/* 2010 */               BasicResourcePool.this.removeResource(this.resc);
/* 2011 */               BasicResourcePool.this.ensureMinResources();
/*      */             }
/*      */             
/* 2014 */             BasicResourcePool.this.failed_idle_tests += 1L;
/* 2015 */             BasicResourcePool.this.setLastIdleCheckFailure(e);
/*      */           }
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/* 2021 */         synchronized (BasicResourcePool.this)
/*      */         {
/* 2023 */           BasicResourcePool.this.idleCheckResources.remove(this.resc);
/* 2024 */           BasicResourcePool.this.notifyAll();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   static final class PunchCard
/*      */   {
/*      */     long acquisition_time;
/*      */     long last_checkin_time;
/*      */     long checkout_time;
/*      */     Exception checkoutStackTraceException;
/*      */     
/*      */     PunchCard()
/*      */     {
/* 2039 */       this.acquisition_time = System.currentTimeMillis();
/* 2040 */       this.last_checkin_time = this.acquisition_time;
/* 2041 */       this.checkout_time = -1L;
/* 2042 */       this.checkoutStackTraceException = null;
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\resourcepool\BasicResourcePool.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */