/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.Lists;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.ExecutionException;
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
/*     */ @Beta
/*     */ public abstract class AbstractService
/*     */   implements Service
/*     */ {
/*  55 */   private final Monitor monitor = new Monitor();
/*     */   
/*  57 */   private final Transition startup = new Transition(null);
/*  58 */   private final Transition shutdown = new Transition(null);
/*     */   
/*  60 */   private final Monitor.Guard isStartable = new Monitor.Guard(this.monitor) {
/*     */     public boolean isSatisfied() {
/*  62 */       return AbstractService.this.state() == State.NEW;
/*     */     }
/*     */   };
/*     */   
/*  66 */   private final Monitor.Guard isStoppable = new Monitor.Guard(this.monitor) {
/*     */     public boolean isSatisfied() {
/*  68 */       return AbstractService.this.state().compareTo(State.RUNNING) <= 0;
/*     */     }
/*     */   };
/*     */   
/*  72 */   private final Monitor.Guard hasReachedRunning = new Monitor.Guard(this.monitor) {
/*     */     public boolean isSatisfied() {
/*  74 */       return AbstractService.this.state().compareTo(State.RUNNING) >= 0;
/*     */     }
/*     */   };
/*     */   
/*  78 */   private final Monitor.Guard isStopped = new Monitor.Guard(this.monitor) {
/*     */     public boolean isSatisfied() {
/*  80 */       return AbstractService.this.state().isTerminal();
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */   @GuardedBy("monitor")
/*  87 */   private final List<ListenerExecutorPair> listeners = Lists.newArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  96 */   private final ExecutionQueue queuedListeners = new ExecutionQueue();
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
/* 107 */   private volatile StateSnapshot snapshot = new StateSnapshot(State.NEW);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected AbstractService()
/*     */   {
/* 114 */     addListener(new Listener()
/*     */     {
/*     */       public void running() {
/* 117 */         AbstractService.this.startup.set(State.RUNNING);
/*     */       }
/*     */       
/*     */       public void stopping(State from) {
/* 121 */         if (from == State.STARTING) {
/* 122 */           AbstractService.this.startup.set(State.STOPPING);
/*     */         }
/*     */       }
/*     */       
/*     */       public void terminated(State from) {
/* 127 */         if (from == State.NEW) {
/* 128 */           AbstractService.this.startup.set(State.TERMINATED);
/*     */         }
/* 130 */         AbstractService.this.shutdown.set(State.TERMINATED);
/*     */       }
/*     */       
/*     */       public void failed(State from, Throwable failure) {
/* 134 */         switch (AbstractService.11.$SwitchMap$com$google$common$util$concurrent$Service$State[from.ordinal()]) {
/*     */         case 1: 
/* 136 */           AbstractService.this.startup.setException(failure);
/* 137 */           AbstractService.this.shutdown.setException(new Exception("Service failed to start.", failure));
/* 138 */           break;
/*     */         case 2: 
/* 140 */           AbstractService.this.shutdown.setException(new Exception("Service failed while running", failure));
/* 141 */           break;
/*     */         case 3: 
/* 143 */           AbstractService.this.shutdown.setException(failure);
/* 144 */           break;
/*     */         case 4: 
/*     */         case 5: 
/*     */         case 6: 
/*     */         default: 
/* 149 */           throw new AssertionError("Unexpected from state: " + from); } } }, MoreExecutors.sameThreadExecutor());
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
/* 181 */     if (this.monitor.enterIf(this.isStartable)) {
/*     */       try {
/* 183 */         this.snapshot = new StateSnapshot(State.STARTING);
/* 184 */         starting();
/* 185 */         doStart();
/*     */       }
/*     */       catch (Throwable startupFailure) {
/* 188 */         notifyFailed(startupFailure);
/*     */       } finally {
/* 190 */         this.monitor.leave();
/* 191 */         executeListeners();
/*     */       }
/*     */     } else {
/* 194 */       throw new IllegalStateException("Service " + this + " has already been started");
/*     */     }
/* 196 */     return this;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public final ListenableFuture<State> start()
/*     */   {
/* 202 */     if (this.monitor.enterIf(this.isStartable)) {
/*     */       try {
/* 204 */         this.snapshot = new StateSnapshot(State.STARTING);
/* 205 */         starting();
/* 206 */         doStart();
/*     */       } catch (Throwable startupFailure) {
/* 208 */         notifyFailed(startupFailure);
/*     */       } finally {
/* 210 */         this.monitor.leave();
/* 211 */         executeListeners();
/*     */       }
/*     */     }
/* 214 */     return this.startup;
/*     */   }
/*     */   
/*     */   public final Service stopAsync() {
/* 218 */     stop();
/* 219 */     return this;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public final ListenableFuture<State> stop()
/*     */   {
/* 225 */     if (this.monitor.enterIf(this.isStoppable)) {
/*     */       try {
/* 227 */         State previous = state();
/* 228 */         switch (previous) {
/*     */         case NEW: 
/* 230 */           this.snapshot = new StateSnapshot(State.TERMINATED);
/* 231 */           terminated(State.NEW);
/* 232 */           break;
/*     */         case STARTING: 
/* 234 */           this.snapshot = new StateSnapshot(State.STARTING, true, null);
/* 235 */           stopping(State.STARTING);
/* 236 */           break;
/*     */         case RUNNING: 
/* 238 */           this.snapshot = new StateSnapshot(State.STOPPING);
/* 239 */           stopping(State.RUNNING);
/* 240 */           doStop();
/* 241 */           break;
/*     */         
/*     */         case STOPPING: 
/*     */         case TERMINATED: 
/*     */         case FAILED: 
/* 246 */           throw new AssertionError("isStoppable is incorrectly implemented, saw: " + previous);
/*     */         default: 
/* 248 */           throw new AssertionError("Unexpected state: " + previous);
/*     */         }
/*     */       }
/*     */       catch (Throwable shutdownFailure)
/*     */       {
/* 253 */         notifyFailed(shutdownFailure);
/*     */       } finally {
/* 255 */         this.monitor.leave();
/* 256 */         executeListeners();
/*     */       }
/*     */     }
/* 259 */     return this.shutdown;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public State startAndWait()
/*     */   {
/* 265 */     return (State)Futures.getUnchecked(start());
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public State stopAndWait()
/*     */   {
/* 271 */     return (State)Futures.getUnchecked(stop());
/*     */   }
/*     */   
/*     */   public final void awaitRunning() {
/* 275 */     this.monitor.enterWhenUninterruptibly(this.hasReachedRunning);
/*     */     try {
/* 277 */       checkCurrentState(State.RUNNING);
/*     */     } finally {
/* 279 */       this.monitor.leave();
/*     */     }
/*     */   }
/*     */   
/*     */   public final void awaitRunning(long timeout, TimeUnit unit) throws TimeoutException {
/* 284 */     if (this.monitor.enterWhenUninterruptibly(this.hasReachedRunning, timeout, unit)) {
/*     */       try {
/* 286 */         checkCurrentState(State.RUNNING);
/*     */       } finally {
/* 288 */         this.monitor.leave();
/*     */ 
/*     */       }
/*     */       
/*     */     }
/*     */     else
/*     */     {
/* 295 */       throw new TimeoutException("Timed out waiting for " + this + " to reach the RUNNING state. " + "Current state: " + state());
/*     */     }
/*     */   }
/*     */   
/*     */   public final void awaitTerminated()
/*     */   {
/* 301 */     this.monitor.enterWhenUninterruptibly(this.isStopped);
/*     */     try {
/* 303 */       checkCurrentState(State.TERMINATED);
/*     */     } finally {
/* 305 */       this.monitor.leave();
/*     */     }
/*     */   }
/*     */   
/*     */   public final void awaitTerminated(long timeout, TimeUnit unit) throws TimeoutException {
/* 310 */     if (this.monitor.enterWhenUninterruptibly(this.isStopped, timeout, unit)) {
/*     */       try {
/* 312 */         State state = state();
/* 313 */         checkCurrentState(State.TERMINATED);
/*     */       } finally {
/* 315 */         this.monitor.leave();
/*     */ 
/*     */       }
/*     */       
/*     */     }
/*     */     else
/*     */     {
/* 322 */       throw new TimeoutException("Timed out waiting for " + this + " to reach a terminal state. " + "Current state: " + state());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @GuardedBy("monitor")
/*     */   private void checkCurrentState(State expected)
/*     */   {
/* 330 */     State actual = state();
/* 331 */     if (actual != expected) {
/* 332 */       if (actual == State.FAILED)
/*     */       {
/* 334 */         throw new IllegalStateException("Expected the service to be " + expected + ", but the service has FAILED", failureCause());
/*     */       }
/*     */       
/* 337 */       throw new IllegalStateException("Expected the service to be " + expected + ", but was " + actual);
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
/* 349 */     this.monitor.enter();
/*     */     
/*     */     try
/*     */     {
/* 353 */       if (this.snapshot.state != State.STARTING) {
/* 354 */         IllegalStateException failure = new IllegalStateException("Cannot notifyStarted() when the service is " + this.snapshot.state);
/*     */         
/* 356 */         notifyFailed(failure);
/* 357 */         throw failure;
/*     */       }
/*     */       
/* 360 */       if (this.snapshot.shutdownWhenStartupFinishes) {
/* 361 */         this.snapshot = new StateSnapshot(State.STOPPING);
/*     */         
/*     */ 
/* 364 */         doStop();
/*     */       } else {
/* 366 */         this.snapshot = new StateSnapshot(State.RUNNING);
/* 367 */         running();
/*     */       }
/*     */     } finally {
/* 370 */       this.monitor.leave();
/* 371 */       executeListeners();
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
/* 383 */     this.monitor.enter();
/*     */     
/*     */     try
/*     */     {
/* 387 */       State previous = this.snapshot.state;
/* 388 */       if ((previous != State.STOPPING) && (previous != State.RUNNING)) {
/* 389 */         IllegalStateException failure = new IllegalStateException("Cannot notifyStopped() when the service is " + previous);
/*     */         
/* 391 */         notifyFailed(failure);
/* 392 */         throw failure;
/*     */       }
/* 394 */       this.snapshot = new StateSnapshot(State.TERMINATED);
/* 395 */       terminated(previous);
/*     */     } finally {
/* 397 */       this.monitor.leave();
/* 398 */       executeListeners();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final void notifyFailed(Throwable cause)
/*     */   {
/* 408 */     Preconditions.checkNotNull(cause);
/*     */     
/* 410 */     this.monitor.enter();
/*     */     try {
/* 412 */       State previous = state();
/* 413 */       switch (previous) {
/*     */       case TERMINATED: 
/*     */       case NEW: 
/* 416 */         throw new IllegalStateException("Failed while in state:" + previous, cause);
/*     */       case STARTING: 
/*     */       case RUNNING: 
/*     */       case STOPPING: 
/* 420 */         this.snapshot = new StateSnapshot(State.FAILED, false, cause);
/* 421 */         failed(previous, cause);
/* 422 */         break;
/*     */       case FAILED: 
/*     */         break;
/*     */       
/*     */       default: 
/* 427 */         throw new AssertionError("Unexpected state: " + previous);
/*     */       }
/*     */     } finally {
/* 430 */       this.monitor.leave();
/* 431 */       executeListeners();
/*     */     }
/*     */   }
/*     */   
/*     */   public final boolean isRunning()
/*     */   {
/* 437 */     return state() == State.RUNNING;
/*     */   }
/*     */   
/*     */   public final State state()
/*     */   {
/* 442 */     return this.snapshot.externalState();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final Throwable failureCause()
/*     */   {
/* 450 */     return this.snapshot.failureCause();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void addListener(Listener listener, Executor executor)
/*     */   {
/* 458 */     Preconditions.checkNotNull(listener, "listener");
/* 459 */     Preconditions.checkNotNull(executor, "executor");
/* 460 */     this.monitor.enter();
/*     */     try {
/* 462 */       State currentState = state();
/* 463 */       if ((currentState != State.TERMINATED) && (currentState != State.FAILED)) {
/* 464 */         this.listeners.add(new ListenerExecutorPair(listener, executor));
/*     */       }
/*     */     } finally {
/* 467 */       this.monitor.leave();
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString() {
/* 472 */     return getClass().getSimpleName() + " [" + state() + "]";
/*     */   }
/*     */   
/*     */   private class Transition extends AbstractFuture<State>
/*     */   {
/*     */     private Transition() {}
/*     */     
/*     */     public State get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException
/*     */     {
/*     */       try
/*     */       {
/* 483 */         return (State)super.get(timeout, unit);
/*     */       } catch (TimeoutException e) {
/* 485 */         throw new TimeoutException(AbstractService.this.toString());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void executeListeners()
/*     */   {
/* 495 */     if (!this.monitor.isOccupiedByCurrentThread()) {
/* 496 */       this.queuedListeners.execute();
/*     */     }
/*     */   }
/*     */   
/*     */   @GuardedBy("monitor")
/*     */   private void starting() {
/* 502 */     for (final ListenerExecutorPair pair : this.listeners) {
/* 503 */       this.queuedListeners.add(new Runnable()
/*     */       {
/* 505 */         public void run() { pair.listener.starting(); } }, pair.executor);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @GuardedBy("monitor")
/*     */   private void running()
/*     */   {
/* 513 */     for (final ListenerExecutorPair pair : this.listeners) {
/* 514 */       this.queuedListeners.add(new Runnable()
/*     */       {
/* 516 */         public void run() { pair.listener.running(); } }, pair.executor);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @GuardedBy("monitor")
/*     */   private void stopping(final State from)
/*     */   {
/* 524 */     for (final ListenerExecutorPair pair : this.listeners) {
/* 525 */       this.queuedListeners.add(new Runnable()
/*     */       {
/* 527 */         public void run() { pair.listener.stopping(from); } }, pair.executor);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @GuardedBy("monitor")
/*     */   private void terminated(final State from)
/*     */   {
/* 535 */     for (final ListenerExecutorPair pair : this.listeners) {
/* 536 */       this.queuedListeners.add(new Runnable()
/*     */       {
/* 538 */         public void run() { pair.listener.terminated(from); } }, pair.executor);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 543 */     this.listeners.clear();
/*     */   }
/*     */   
/*     */   @GuardedBy("monitor")
/*     */   private void failed(final State from, final Throwable cause) {
/* 548 */     for (final ListenerExecutorPair pair : this.listeners) {
/* 549 */       this.queuedListeners.add(new Runnable()
/*     */       {
/* 551 */         public void run() { pair.listener.failed(from, cause); } }, pair.executor);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 556 */     this.listeners.clear();
/*     */   }
/*     */   
/*     */   private static class ListenerExecutorPair
/*     */   {
/*     */     final Listener listener;
/*     */     final Executor executor;
/*     */     
/*     */     ListenerExecutorPair(Listener listener, Executor executor) {
/* 565 */       this.listener = listener;
/* 566 */       this.executor = executor;
/*     */     }
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
/*     */     final boolean shutdownWhenStartupFinishes;
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
/* 597 */       this(internalState, false, null);
/*     */     }
/*     */     
/*     */     StateSnapshot(State internalState, boolean shutdownWhenStartupFinishes, @Nullable Throwable failure)
/*     */     {
/* 602 */       Preconditions.checkArgument((!shutdownWhenStartupFinishes) || (internalState == State.STARTING), "shudownWhenStartupFinishes can only be set if state is STARTING. Got %s instead.", new Object[] { internalState });
/*     */       
/*     */ 
/* 605 */       Preconditions.checkArgument(((failure != null ? 1 : 0) ^ (internalState == State.FAILED ? 1 : 0)) == 0, "A failure cause should be set if and only if the state is failed.  Got %s and %s instead.", new Object[] { internalState, failure });
/*     */       
/*     */ 
/* 608 */       this.state = internalState;
/* 609 */       this.shutdownWhenStartupFinishes = shutdownWhenStartupFinishes;
/* 610 */       this.failure = failure;
/*     */     }
/*     */     
/*     */     State externalState()
/*     */     {
/* 615 */       if ((this.shutdownWhenStartupFinishes) && (this.state == State.STARTING)) {
/* 616 */         return State.STOPPING;
/*     */       }
/* 618 */       return this.state;
/*     */     }
/*     */     
/*     */ 
/*     */     Throwable failureCause()
/*     */     {
/* 624 */       Preconditions.checkState(this.state == State.FAILED, "failureCause() is only valid if the service has failed, service is %s", new Object[] { this.state });
/*     */       
/* 626 */       return this.failure;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\util\concurrent\AbstractService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */