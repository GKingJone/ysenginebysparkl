/*     */ package com.mchange.v2.resourcepool;
/*     */ 
/*     */ import com.mchange.v2.async.AsynchronousRunner;
/*     */ import com.mchange.v2.async.CarefulRunnableQueue;
/*     */ import com.mchange.v2.async.Queuable;
/*     */ import com.mchange.v2.async.RunnableQueue;
/*     */ import com.mchange.v2.async.ThreadPoolAsynchronousRunner;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.Timer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BasicResourcePoolFactory
/*     */   extends ResourcePoolFactory
/*     */ {
/*     */   public static BasicResourcePoolFactory createNoEventSupportInstance(int num_task_threads)
/*     */   {
/*  32 */     return createNoEventSupportInstance(null, null, num_task_threads);
/*     */   }
/*     */   
/*     */   public static BasicResourcePoolFactory createNoEventSupportInstance(AsynchronousRunner taskRunner, Timer timer) {
/*  36 */     return createNoEventSupportInstance(taskRunner, timer, 3);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static BasicResourcePoolFactory createNoEventSupportInstance(AsynchronousRunner taskRunner, Timer timer, int default_num_task_threads)
/*     */   {
/*  43 */     return new BasicResourcePoolFactory(taskRunner, timer, default_num_task_threads, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  49 */   int start = -1;
/*  50 */   int min = 1;
/*  51 */   int max = 12;
/*  52 */   int inc = 3;
/*  53 */   int retry_attempts = -1;
/*  54 */   int retry_delay = 1000;
/*  55 */   long idle_resource_test_period = -1L;
/*  56 */   long max_age = -1L;
/*  57 */   long max_idle_time = -1L;
/*  58 */   long excess_max_idle_time = -1L;
/*  59 */   long destroy_overdue_resc_time = -1L;
/*  60 */   long expiration_enforcement_delay = -1L;
/*     */   
/*  62 */   boolean break_on_acquisition_failure = true;
/*  63 */   boolean debug_store_checkout_stacktrace = false;
/*     */   
/*     */ 
/*     */   AsynchronousRunner taskRunner;
/*     */   
/*     */ 
/*     */   boolean taskRunner_is_external;
/*     */   
/*     */ 
/*     */   RunnableQueue asyncEventQueue;
/*     */   
/*     */ 
/*     */   boolean asyncEventQueue_is_external;
/*     */   
/*     */ 
/*     */   Timer timer;
/*     */   
/*     */   boolean timer_is_external;
/*     */   
/*     */   int default_num_task_threads;
/*     */   
/*     */   Set liveChildren;
/*     */   
/*     */ 
/*     */   BasicResourcePoolFactory()
/*     */   {
/*  89 */     this(null, null, null);
/*     */   }
/*     */   
/*     */   BasicResourcePoolFactory(AsynchronousRunner taskRunner, RunnableQueue asyncEventQueue, Timer timer)
/*     */   {
/*  94 */     this(taskRunner, asyncEventQueue, timer, 3);
/*     */   }
/*     */   
/*  97 */   BasicResourcePoolFactory(int num_task_threads) { this(null, null, null, num_task_threads); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   BasicResourcePoolFactory(AsynchronousRunner taskRunner, Timer timer, int default_num_task_threads, boolean no_event_support)
/*     */   {
/* 104 */     this(taskRunner, null, timer, default_num_task_threads);
/* 105 */     if (no_event_support) {
/* 106 */       this.asyncEventQueue_is_external = true;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   BasicResourcePoolFactory(AsynchronousRunner taskRunner, RunnableQueue asyncEventQueue, Timer timer, int default_num_task_threads)
/*     */   {
/* 114 */     this.taskRunner = taskRunner;
/* 115 */     this.taskRunner_is_external = (taskRunner != null);
/*     */     
/* 117 */     this.asyncEventQueue = asyncEventQueue;
/* 118 */     this.asyncEventQueue_is_external = (asyncEventQueue != null);
/*     */     
/* 120 */     this.timer = timer;
/* 121 */     this.timer_is_external = (timer != null);
/*     */     
/* 123 */     this.default_num_task_threads = default_num_task_threads;
/*     */   }
/*     */   
/*     */   private void createThreadResources()
/*     */   {
/* 128 */     if (!this.taskRunner_is_external)
/*     */     {
/*     */ 
/* 131 */       this.taskRunner = new ThreadPoolAsynchronousRunner(this.default_num_task_threads, true);
/* 132 */       if (!this.asyncEventQueue_is_external)
/* 133 */         this.asyncEventQueue = ((Queuable)this.taskRunner).asRunnableQueue();
/*     */     }
/* 135 */     if (!this.asyncEventQueue_is_external)
/* 136 */       this.asyncEventQueue = new CarefulRunnableQueue(true, false);
/* 137 */     if (!this.timer_is_external) {
/* 138 */       this.timer = new Timer(true);
/*     */     }
/* 140 */     this.liveChildren = new HashSet();
/*     */   }
/*     */   
/*     */   private void destroyThreadResources()
/*     */   {
/* 145 */     if (!this.taskRunner_is_external)
/*     */     {
/* 147 */       this.taskRunner.close();
/* 148 */       this.taskRunner = null;
/*     */     }
/* 150 */     if (!this.asyncEventQueue_is_external)
/*     */     {
/* 152 */       this.asyncEventQueue.close();
/* 153 */       this.asyncEventQueue = null;
/*     */     }
/* 155 */     if (!this.timer_is_external)
/*     */     {
/* 157 */       this.timer.cancel();
/* 158 */       this.timer = null;
/*     */     }
/*     */     
/* 161 */     this.liveChildren = null;
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
/*     */   synchronized void markBroken(BasicResourcePool pool)
/*     */   {
/* 189 */     if (this.liveChildren != null)
/*     */     {
/* 191 */       this.liveChildren.remove(pool);
/* 192 */       if (this.liveChildren.isEmpty()) {
/* 193 */         destroyThreadResources();
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
/*     */   public synchronized void setStart(int start)
/*     */     throws ResourcePoolException
/*     */   {
/* 219 */     this.start = start;
/*     */   }
/*     */   
/*     */   public synchronized int getStart() throws ResourcePoolException {
/* 223 */     return this.start;
/*     */   }
/*     */   
/*     */   public synchronized void setMin(int min) throws ResourcePoolException {
/* 227 */     this.min = min;
/*     */   }
/*     */   
/*     */   public synchronized int getMin() throws ResourcePoolException {
/* 231 */     return this.min;
/*     */   }
/*     */   
/*     */   public synchronized void setMax(int max) throws ResourcePoolException {
/* 235 */     this.max = max;
/*     */   }
/*     */   
/*     */   public synchronized int getMax() throws ResourcePoolException {
/* 239 */     return this.max;
/*     */   }
/*     */   
/*     */   public synchronized void setIncrement(int inc) throws ResourcePoolException {
/* 243 */     this.inc = inc;
/*     */   }
/*     */   
/*     */   public synchronized int getIncrement() throws ResourcePoolException {
/* 247 */     return this.inc;
/*     */   }
/*     */   
/*     */   public synchronized void setAcquisitionRetryAttempts(int retry_attempts) throws ResourcePoolException {
/* 251 */     this.retry_attempts = retry_attempts;
/*     */   }
/*     */   
/*     */   public synchronized int getAcquisitionRetryAttempts() throws ResourcePoolException {
/* 255 */     return this.retry_attempts;
/*     */   }
/*     */   
/*     */   public synchronized void setAcquisitionRetryDelay(int retry_delay) throws ResourcePoolException {
/* 259 */     this.retry_delay = retry_delay;
/*     */   }
/*     */   
/*     */   public synchronized int getAcquisitionRetryDelay() throws ResourcePoolException {
/* 263 */     return this.retry_delay;
/*     */   }
/*     */   
/* 266 */   public synchronized void setIdleResourceTestPeriod(long test_period) { this.idle_resource_test_period = test_period; }
/*     */   
/*     */   public synchronized long getIdleResourceTestPeriod() {
/* 269 */     return this.idle_resource_test_period;
/*     */   }
/*     */   
/*     */   public synchronized void setResourceMaxAge(long max_age) throws ResourcePoolException {
/* 273 */     this.max_age = max_age;
/*     */   }
/*     */   
/*     */   public synchronized long getResourceMaxAge() throws ResourcePoolException {
/* 277 */     return this.max_age;
/*     */   }
/*     */   
/*     */   public synchronized void setResourceMaxIdleTime(long millis) throws ResourcePoolException {
/* 281 */     this.max_idle_time = millis;
/*     */   }
/*     */   
/*     */   public synchronized long getResourceMaxIdleTime() throws ResourcePoolException {
/* 285 */     return this.max_idle_time;
/*     */   }
/*     */   
/*     */   public synchronized void setExcessResourceMaxIdleTime(long millis) throws ResourcePoolException {
/* 289 */     this.excess_max_idle_time = millis;
/*     */   }
/*     */   
/*     */   public synchronized long getExcessResourceMaxIdleTime() throws ResourcePoolException {
/* 293 */     return this.excess_max_idle_time;
/*     */   }
/*     */   
/*     */   public synchronized long getDestroyOverdueResourceTime() throws ResourcePoolException {
/* 297 */     return this.destroy_overdue_resc_time;
/*     */   }
/*     */   
/*     */   public synchronized void setDestroyOverdueResourceTime(long millis) throws ResourcePoolException {
/* 301 */     this.destroy_overdue_resc_time = millis;
/*     */   }
/*     */   
/*     */   public synchronized void setExpirationEnforcementDelay(long expiration_enforcement_delay) throws ResourcePoolException {
/* 305 */     this.expiration_enforcement_delay = expiration_enforcement_delay;
/*     */   }
/*     */   
/*     */   public synchronized long getExpirationEnforcementDelay() throws ResourcePoolException {
/* 309 */     return this.expiration_enforcement_delay;
/*     */   }
/*     */   
/*     */   public synchronized void setBreakOnAcquisitionFailure(boolean break_on_acquisition_failure) throws ResourcePoolException {
/* 313 */     this.break_on_acquisition_failure = break_on_acquisition_failure;
/*     */   }
/*     */   
/*     */   public synchronized boolean getBreakOnAcquisitionFailure() throws ResourcePoolException {
/* 317 */     return this.break_on_acquisition_failure;
/*     */   }
/*     */   
/*     */   public synchronized void setDebugStoreCheckoutStackTrace(boolean debug_store_checkout_stacktrace) throws ResourcePoolException {
/* 321 */     this.debug_store_checkout_stacktrace = debug_store_checkout_stacktrace;
/*     */   }
/*     */   
/*     */   public synchronized boolean getDebugStoreCheckoutStackTrace() throws ResourcePoolException {
/* 325 */     return this.debug_store_checkout_stacktrace;
/*     */   }
/*     */   
/*     */   public synchronized ResourcePool createPool(ResourcePool.Manager mgr) throws ResourcePoolException
/*     */   {
/* 330 */     if (this.liveChildren == null) {
/* 331 */       createThreadResources();
/*     */     }
/* 333 */     ResourcePool child = new BasicResourcePool(mgr, this.start, this.min, this.max, this.inc, this.retry_attempts, this.retry_delay, this.idle_resource_test_period, this.max_age, this.max_idle_time, this.excess_max_idle_time, this.destroy_overdue_resc_time, this.expiration_enforcement_delay, this.break_on_acquisition_failure, this.debug_store_checkout_stacktrace, this.taskRunner, this.asyncEventQueue, this.timer, this);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 352 */     this.liveChildren.add(child);
/* 353 */     return child;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\resourcepool\BasicResourcePoolFactory.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */