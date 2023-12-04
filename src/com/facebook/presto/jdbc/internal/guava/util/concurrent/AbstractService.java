/*     */ package com.facebook.presto.jdbc.internal.guava.util.concurrent;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import javax.annotation.Nullable;
/*     */ import javax.annotation.concurrent.GuardedBy;
/*     */ import javax.annotation.concurrent.Immutable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public abstract class AbstractService
/*     */   implements Service
/*     */ {
/*  57 */   private static final ListenerCallQueue.Callback<Listener> STARTING_CALLBACK = new ListenerCallQueue.Callback("starting()")
/*     */   {
/*     */     void call(Listener listener) {
/*  60 */       listener.starting();
/*     */     }
/*     */   };
/*  63 */   private static final ListenerCallQueue.Callback<Listener> RUNNING_CALLBACK = new ListenerCallQueue.Callback("running()")
/*     */   {
/*     */     void call(Listener listener) {
/*  66 */       listener.running();
/*     */     }
/*     */   };
/*  69 */   private static final ListenerCallQueue.Callback<Listener> STOPPING_FROM_STARTING_CALLBACK = stoppingCallback(State.STARTING);
/*     */   
/*  71 */   private static final ListenerCallQueue.Callback<Listener> STOPPING_FROM_RUNNING_CALLBACK = stoppingCallback(State.RUNNING);
/*     */   
/*     */ 
/*  74 */   private static final ListenerCallQueue.Callback<Listener> TERMINATED_FROM_NEW_CALLBACK = terminatedCallback(State.NEW);
/*     */   
/*  76 */   private static final ListenerCallQueue.Callback<Listener> TERMINATED_FROM_RUNNING_CALLBACK = terminatedCallback(State.RUNNING);
/*     */   
/*  78 */   private static final ListenerCallQueue.Callback<Listener> TERMINATED_FROM_STOPPING_CALLBACK = terminatedCallback(State.STOPPING);
/*     */   
/*     */   private static ListenerCallQueue.Callback<Listener> terminatedCallback(final State from)
/*     */   {
/*  82 */     String str = String.valueOf(String.valueOf(from));new ListenerCallQueue.Callback(21 + str.length() + "terminated({from = " + str + "})") {
/*     */       void call(Listener listener) {
/*  84 */         listener.terminated(from);
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   private static ListenerCallQueue.Callback<Listener> stoppingCallback(final State from) {
/*  90 */     String str = String.valueOf(String.valueOf(from));new ListenerCallQueue.Callback(19 + str.length() + "stopping({from = " + str + "})") {
/*     */       void call(Listener listener) {
/*  92 */         listener.stopping(from);
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*  97 */   private final Monitor monitor = new Monitor();
/*     */   
/*  99 */   private final Monitor.Guard isStartable = new Monitor.Guard(this.monitor) {
/*     */     public boolean isSatisfied() {
/* 101 */       return AbstractService.this.state() == State.NEW;
/*     */     }
/*     */   };
/*     */   
/* 105 */   private final Monitor.Guard isStoppable = new Monitor.Guard(this.monitor) {
/*     */     public boolean isSatisfied() {
/* 107 */       return AbstractService.this.state().compareTo(State.RUNNING) <= 0;
/*     */     }
/*     */   };
/*     */   
/* 111 */   private final Monitor.Guard hasReachedRunning = new Monitor.Guard(this.monitor) {
/*     */     public boolean isSatisfied() {
/* 113 */       return AbstractService.this.state().compareTo(State.RUNNING) >= 0;
/*     */     }
/*     */   };
/*     */   
/* 117 */   private final Monitor.Guard isStopped = new Monitor.Guard(this.monitor) {
/*     */     public boolean isSatisfied() {
/* 119 */       return AbstractService.this.state().isTerminal();
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */   @GuardedBy("monitor")
/* 126 */   private final List<ListenerCallQueue<Listener>> listeners = Collections.synchronizedList(new ArrayList());
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @GuardedBy("monitor")
/* 139 */   private volatile StateSnapshot snapshot = new StateSnapshot(State.NEW);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract void doStart();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract void doStop();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final Service startAsync()
/*     */   {
/* 170 */     if (this.monitor.enterIf(this.isStartable)) {
/*     */       try {
/* 172 */         this.snapshot = new StateSnapshot(State.STARTING);
/* 173 */         starting();
/* 174 */         doStart();
/*     */       }
/*     */       catch (Throwable startupFailure) {
/* 177 */         notifyFailed(startupFailure);
/*     */       } finally {
/* 179 */         this.monitor.leave();
/* 180 */         executeListeners();
/*     */       }
/*     */     } else {
/* 183 */       startupFailure = String.valueOf(String.valueOf(this));throw new IllegalStateException(33 + startupFailure.length() + "Service " + startupFailure + " has already been started");
/*     */     }
/* 185 */     return this;
/*     */   }
/*     */   
/*     */   public final Service stopAsync() {
/* 189 */     if (this.monitor.enterIf(this.isStoppable)) {
/*     */       try {
/* 191 */         State previous = state();
/* 192 */         switch (previous) {
/*     */         case NEW: 
/* 194 */           this.snapshot = new StateSnapshot(State.TERMINATED);
/* 195 */           terminated(State.NEW);
/* 196 */           break;
/*     */         case STARTING: 
/* 198 */           this.snapshot = new StateSnapshot(State.STARTING, true, null);
/* 199 */           stopping(State.STARTING);
/* 200 */           break;
/*     */         case RUNNING: 
/* 202 */           this.snapshot = new StateSnapshot(State.STOPPING);
/* 203 */           stopping(State.RUNNING);
/* 204 */           doStop();
/* 205 */           break;
/*     */         
/*     */         case STOPPING: 
/*     */         case TERMINATED: 
/*     */         case FAILED: 
/* 210 */           String str1 = String.valueOf(String.valueOf(previous));throw new AssertionError(45 + str1.length() + "isStoppable is incorrectly implemented, saw: " + str1);
/*     */         default: 
/* 212 */           String str2 = String.valueOf(String.valueOf(previous));throw new AssertionError(18 + str2.length() + "Unexpected state: " + str2);
/*     */         }
/*     */       }
/*     */       catch (Throwable shutdownFailure)
/*     */       {
/* 217 */         notifyFailed(shutdownFailure);
/*     */       } finally {
/* 219 */         this.monitor.leave();
/* 220 */         executeListeners();
/*     */       }
/*     */     }
/* 223 */     return this;
/*     */   }
/*     */   
/*     */   public final void awaitRunning() {
/* 227 */     this.monitor.enterWhenUninterruptibly(this.hasReachedRunning);
/*     */     try {
/* 229 */       checkCurrentState(State.RUNNING);
/*     */     } finally {
/* 231 */       this.monitor.leave();
/*     */     }
/*     */   }
/*     */   
/*     */   public final void awaitRunning(long timeout, TimeUnit unit) throws TimeoutException {
/* 236 */     if (this.monitor.enterWhenUninterruptibly(this.hasReachedRunning, timeout, unit)) {
/*     */       try {
/* 238 */         checkCurrentState(State.RUNNING);
/*     */       } finally {
/* 240 */         this.monitor.leave();
/*     */       }
/*     */       
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 247 */       String str1 = String.valueOf(String.valueOf(this));String str2 = String.valueOf(String.valueOf(state()));throw new TimeoutException(66 + str1.length() + str2.length() + "Timed out waiting for " + str1 + " to reach the RUNNING state. " + "Current state: " + str2);
/*     */     }
/*     */   }
/*     */   
/*     */   public final void awaitTerminated()
/*     */   {
/* 253 */     this.monitor.enterWhenUninterruptibly(this.isStopped);
/*     */     try {
/* 255 */       checkCurrentState(State.TERMINATED);
/*     */     } finally {
/* 257 */       this.monitor.leave();
/*     */     }
/*     */   }
/*     */   
/*     */   public final void awaitTerminated(long timeout, TimeUnit unit) throws TimeoutException {
/* 262 */     if (this.monitor.enterWhenUninterruptibly(this.isStopped, timeout, unit)) {
/*     */       try {
/* 264 */         checkCurrentState(State.TERMINATED);
/*     */       } finally {
/* 266 */         this.monitor.leave();
/*     */       }
/*     */       
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 273 */       String str1 = String.valueOf(String.valueOf(this));String str2 = String.valueOf(String.valueOf(state()));throw new TimeoutException(65 + str1.length() + str2.length() + "Timed out waiting for " + str1 + " to reach a terminal state. " + "Current state: " + str2);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @GuardedBy("monitor")
/*     */   private void checkCurrentState(State expected)
/*     */   {
/* 281 */     State actual = state();
/* 282 */     if (actual != expected) {
/* 283 */       if (actual == State.FAILED)
/*     */       {
/* 285 */         str1 = String.valueOf(String.valueOf(expected));throw new IllegalStateException(55 + str1.length() + "Expected the service to be " + str1 + ", but the service has FAILED", failureCause());
/*     */       }
/*     */       
/* 288 */       String str1 = String.valueOf(String.valueOf(expected));String str2 = String.valueOf(String.valueOf(actual));throw new IllegalStateException(37 + str1.length() + str2.length() + "Expected the service to be " + str1 + ", but was " + str2);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final void notifyStarted()
/*     */   {
/* 300 */     this.monitor.enter();
/*     */     
/*     */     try
/*     */     {
/* 304 */       if (this.snapshot.state != State.STARTING) {
/* 305 */         String str = String.valueOf(String.valueOf(this.snapshot.state));IllegalStateException failure = new IllegalStateException(43 + str.length() + "Cannot notifyStarted() when the service is " + str);
/*     */         
/* 307 */         notifyFailed(failure);
/* 308 */         throw failure;
/*     */       }
/*     */       
/* 311 */       if (this.snapshot.shutdownWhenStartupFinishes) {
/* 312 */         this.snapshot = new StateSnapshot(State.STOPPING);
/*     */         
/*     */ 
/* 315 */         doStop();
/*     */       } else {
/* 317 */         this.snapshot = new StateSnapshot(State.RUNNING);
/* 318 */         running();
/*     */       }
/*     */     } finally {
/* 321 */       this.monitor.leave();
/* 322 */       executeListeners();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final void notifyStopped()
/*     */   {
/* 334 */     this.monitor.enter();
/*     */     
/*     */     try
/*     */     {
/* 338 */       State previous = this.snapshot.state;
/* 339 */       if ((previous != State.STOPPING) && (previous != State.RUNNING)) {
/* 340 */         String str = String.valueOf(String.valueOf(previous));IllegalStateException failure = new IllegalStateException(43 + str.length() + "Cannot notifyStopped() when the service is " + str);
/*     */         
/* 342 */         notifyFailed(failure);
/* 343 */         throw failure;
/*     */       }
/* 345 */       this.snapshot = new StateSnapshot(State.TERMINATED);
/* 346 */       terminated(previous);
/*     */     } finally {
/* 348 */       this.monitor.leave();
/* 349 */       executeListeners();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final void notifyFailed(Throwable cause)
/*     */   {
/* 359 */     Preconditions.checkNotNull(cause);
/*     */     
/* 361 */     this.monitor.enter();
/*     */     try {
/* 363 */       State previous = state();
/* 364 */       switch (previous) {
/*     */       case NEW: 
/*     */       case TERMINATED: 
/* 367 */         String str1 = String.valueOf(String.valueOf(previous));throw new IllegalStateException(22 + str1.length() + "Failed while in state:" + str1, cause);
/*     */       case STARTING: 
/*     */       case RUNNING: 
/*     */       case STOPPING: 
/* 371 */         this.snapshot = new StateSnapshot(State.FAILED, false, cause);
/* 372 */         failed(previous, cause);
/* 373 */         break;
/*     */       case FAILED: 
/*     */         break;
/*     */       
/*     */       default: 
/* 378 */         String str2 = String.valueOf(String.valueOf(previous));throw new AssertionError(18 + str2.length() + "Unexpected state: " + str2);
/*     */       }
/*     */     } finally {
/* 381 */       this.monitor.leave();
/* 382 */       executeListeners();
/*     */     }
/*     */   }
/*     */   
/*     */   public final boolean isRunning()
/*     */   {
/* 388 */     return state() == State.RUNNING;
/*     */   }
/*     */   
/*     */   public final State state()
/*     */   {
/* 393 */     return this.snapshot.externalState();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final Throwable failureCause()
/*     */   {
/* 401 */     return this.snapshot.failureCause();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void addListener(Listener listener, Executor executor)
/*     */   {
/* 409 */     Preconditions.checkNotNull(listener, "listener");
/* 410 */     Preconditions.checkNotNull(executor, "executor");
/* 411 */     this.monitor.enter();
/*     */     try {
/* 413 */       if (!state().isTerminal()) {
/* 414 */         this.listeners.add(new ListenerCallQueue(listener, executor));
/*     */       }
/*     */     } finally {
/* 417 */       this.monitor.leave();
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString() {
/* 422 */     String str1 = String.valueOf(String.valueOf(getClass().getSimpleName()));String str2 = String.valueOf(String.valueOf(state()));return 3 + str1.length() + str2.length() + str1 + " [" + str2 + "]";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void executeListeners()
/*     */   {
/* 430 */     if (!this.monitor.isOccupiedByCurrentThread())
/*     */     {
/* 432 */       for (int i = 0; i < this.listeners.size(); i++) {
/* 433 */         ((ListenerCallQueue)this.listeners.get(i)).execute();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   @GuardedBy("monitor")
/*     */   private void starting() {
/* 440 */     STARTING_CALLBACK.enqueueOn(this.listeners);
/*     */   }
/*     */   
/*     */   @GuardedBy("monitor")
/*     */   private void running() {
/* 445 */     RUNNING_CALLBACK.enqueueOn(this.listeners);
/*     */   }
/*     */   
/*     */   @GuardedBy("monitor")
/*     */   private void stopping(State from) {
/* 450 */     if (from == State.STARTING) {
/* 451 */       STOPPING_FROM_STARTING_CALLBACK.enqueueOn(this.listeners);
/* 452 */     } else if (from == State.RUNNING) {
/* 453 */       STOPPING_FROM_RUNNING_CALLBACK.enqueueOn(this.listeners);
/*     */     } else {
/* 455 */       throw new AssertionError();
/*     */     }
/*     */   }
/*     */   
/*     */   @GuardedBy("monitor")
/*     */   private void terminated(State from) {
/* 461 */     switch (from) {
/*     */     case NEW: 
/* 463 */       TERMINATED_FROM_NEW_CALLBACK.enqueueOn(this.listeners);
/* 464 */       break;
/*     */     case RUNNING: 
/* 466 */       TERMINATED_FROM_RUNNING_CALLBACK.enqueueOn(this.listeners);
/* 467 */       break;
/*     */     case STOPPING: 
/* 469 */       TERMINATED_FROM_STOPPING_CALLBACK.enqueueOn(this.listeners);
/* 470 */       break;
/*     */     case STARTING: 
/*     */     case TERMINATED: 
/*     */     case FAILED: 
/*     */     default: 
/* 475 */       throw new AssertionError();
/*     */     }
/*     */   }
/*     */   
/*     */   @GuardedBy("monitor")
/*     */   private void failed(final State from, final Throwable cause)
/*     */   {
/* 482 */     String str1 = String.valueOf(String.valueOf(from));String str2 = String.valueOf(String.valueOf(cause));new ListenerCallQueue.Callback(27 + str1.length() + str2.length() + "failed({from = " + str1 + ", cause = " + str2 + "})")
/*     */     {
/* 484 */       void call(Listener listener) { listener.failed(from, cause); } }.enqueueOn(this.listeners);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Immutable
/*     */   private static final class StateSnapshot
/*     */   {
/*     */     final State state;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     final boolean shutdownWhenStartupFinishes;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     @Nullable
/*     */     final Throwable failure;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     StateSnapshot(State internalState)
/*     */     {
/* 516 */       this(internalState, false, null);
/*     */     }
/*     */     
/*     */     StateSnapshot(State internalState, boolean shutdownWhenStartupFinishes, @Nullable Throwable failure)
/*     */     {
/* 521 */       Preconditions.checkArgument((!shutdownWhenStartupFinishes) || (internalState == State.STARTING), "shudownWhenStartupFinishes can only be set if state is STARTING. Got %s instead.", new Object[] { internalState });
/*     */       
/*     */ 
/* 524 */       Preconditions.checkArgument(((failure != null ? 1 : 0) ^ (internalState == State.FAILED ? 1 : 0)) == 0, "A failure cause should be set if and only if the state is failed.  Got %s and %s instead.", new Object[] { internalState, failure });
/*     */       
/*     */ 
/* 527 */       this.state = internalState;
/* 528 */       this.shutdownWhenStartupFinishes = shutdownWhenStartupFinishes;
/* 529 */       this.failure = failure;
/*     */     }
/*     */     
/*     */     State externalState()
/*     */     {
/* 534 */       if ((this.shutdownWhenStartupFinishes) && (this.state == State.STARTING)) {
/* 535 */         return State.STOPPING;
/*     */       }
/* 537 */       return this.state;
/*     */     }
/*     */     
/*     */ 
/*     */     Throwable failureCause()
/*     */     {
/* 543 */       Preconditions.checkState(this.state == State.FAILED, "failureCause() is only valid if the service has failed, service is %s", new Object[] { this.state });
/*     */       
/* 545 */       return this.failure;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\util\concurrent\AbstractService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */