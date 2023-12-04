/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Objects.ToStringHelper;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Predicates;
/*     */ import com.google.common.base.Stopwatch;
/*     */ import com.google.common.base.Supplier;
/*     */ import com.google.common.collect.Collections2;
/*     */ import com.google.common.collect.ImmutableCollection;
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import com.google.common.collect.ImmutableMap;
/*     */ import com.google.common.collect.ImmutableMap.Builder;
/*     */ import com.google.common.collect.ImmutableMultimap;
/*     */ import com.google.common.collect.ImmutableSet;
/*     */ import com.google.common.collect.ImmutableSetMultimap;
/*     */ import com.google.common.collect.ImmutableSetMultimap.Builder;
/*     */ import com.google.common.collect.Lists;
/*     */ import com.google.common.collect.Maps;
/*     */ import com.google.common.collect.Multimaps;
/*     */ import com.google.common.collect.Multiset;
/*     */ import com.google.common.collect.Ordering;
/*     */ import com.google.common.collect.SetMultimap;
/*     */ import com.google.common.collect.Sets;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public final class ServiceManager
/*     */ {
/* 124 */   private static final Logger logger = Logger.getLogger(ServiceManager.class.getName());
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final ServiceManagerState state;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final ImmutableList<Service> services;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ServiceManager(Iterable<? extends Service> services)
/*     */   {
/* 180 */     ImmutableList<Service> copy = ImmutableList.copyOf(services);
/* 181 */     if (copy.isEmpty())
/*     */     {
/*     */ 
/* 184 */       logger.log(Level.WARNING, "ServiceManager configured with no services.  Is your application configured properly?", new EmptyServiceManagerWarning(null));
/*     */       
/*     */ 
/* 187 */       copy = ImmutableList.of(new NoOpService(null));
/*     */     }
/* 189 */     this.state = new ServiceManagerState(copy);
/* 190 */     this.services = copy;
/* 191 */     WeakReference<ServiceManagerState> stateReference = new WeakReference(this.state);
/*     */     
/* 193 */     for (Service service : copy)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 202 */       service.addListener(new ServiceListener(service, stateReference), new SynchronizedExecutor(null));
/*     */       
/*     */ 
/* 205 */       Preconditions.checkArgument(service.state() == Service.State.NEW, "Can only manage NEW services, %s", new Object[] { service });
/*     */     }
/*     */     
/*     */ 
/* 209 */     this.state.markReady();
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
/*     */   public void addListener(Listener listener, Executor executor)
/*     */   {
/* 233 */     this.state.addListener(listener, executor);
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
/*     */   public void addListener(Listener listener)
/*     */   {
/* 249 */     this.state.addListener(listener, MoreExecutors.sameThreadExecutor());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ServiceManager startAsync()
/*     */   {
/* 261 */     for (Service service : this.services) {
/* 262 */       Service.State state = service.state();
/* 263 */       Preconditions.checkState(state == Service.State.NEW, "Service %s is %s, cannot start it.", new Object[] { service, state });
/*     */     }
/* 265 */     for (Service service : this.services) {
/*     */       try {
/* 267 */         service.startAsync();
/*     */ 
/*     */       }
/*     */       catch (IllegalStateException e)
/*     */       {
/*     */ 
/* 273 */         logger.log(Level.WARNING, "Unable to start Service " + service, e);
/*     */       }
/*     */     }
/* 276 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void awaitHealthy()
/*     */   {
/* 288 */     this.state.awaitHealthy();
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
/*     */   public void awaitHealthy(long timeout, TimeUnit unit)
/*     */     throws TimeoutException
/*     */   {
/* 303 */     this.state.awaitHealthy(timeout, unit);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ServiceManager stopAsync()
/*     */   {
/* 313 */     for (Service service : this.services) {
/* 314 */       service.stopAsync();
/*     */     }
/* 316 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void awaitStopped()
/*     */   {
/* 325 */     this.state.awaitStopped();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void awaitStopped(long timeout, TimeUnit unit)
/*     */     throws TimeoutException
/*     */   {
/* 338 */     this.state.awaitStopped(timeout, unit);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isHealthy()
/*     */   {
/* 348 */     for (Service service : this.services) {
/* 349 */       if (!service.isRunning()) {
/* 350 */         return false;
/*     */       }
/*     */     }
/* 353 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ImmutableMultimap<Service.State, Service> servicesByState()
/*     */   {
/* 363 */     return this.state.servicesByState();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ImmutableMap<Service, Long> startupTimes()
/*     */   {
/* 374 */     return this.state.startupTimes();
/*     */   }
/*     */   
/*     */ 
/* 378 */   public String toString() { return Objects.toStringHelper(ServiceManager.class).add("services", Collections2.filter(this.services, Predicates.not(Predicates.instanceOf(NoOpService.class)))).toString(); }
/*     */   
/*     */   @Beta
/*     */   public static abstract class Listener { public void healthy() {}
/*     */     
/*     */     public void stopped() {}
/*     */     
/*     */     public void failure(Service service) {}
/*     */   }
/*     */   
/* 388 */   private static final class ServiceManagerState { final Monitor monitor = new Monitor();
/*     */     @GuardedBy("monitor")
/* 390 */     final SetMultimap<Service.State, Service> servicesByState = Multimaps.newSetMultimap(new EnumMap(Service.State.class), new Supplier()
/*     */     {
/*     */ 
/*     */       public Set<Service> get()
/*     */       {
/* 395 */         return Sets.newLinkedHashSet();
/*     */       }
/* 390 */     });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     @GuardedBy("monitor")
/* 399 */     final Multiset<Service.State> states = this.servicesByState.keys();
/*     */     
/*     */     @GuardedBy("monitor")
/* 402 */     final Map<Service, Stopwatch> startupTimers = Maps.newIdentityHashMap();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     @GuardedBy("monitor")
/*     */     boolean ready;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     @GuardedBy("monitor")
/*     */     boolean transitioned;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     final int numberOfServices;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 428 */     final Monitor.Guard awaitHealthGuard = new Monitor.Guard(this.monitor)
/*     */     {
/*     */       public boolean isSatisfied() {
/* 431 */         return (ServiceManagerState.this.states.count(Service.State.RUNNING) == ServiceManagerState.this.numberOfServices) || (ServiceManagerState.this.states.contains(Service.State.STOPPING)) || (ServiceManagerState.this.states.contains(Service.State.TERMINATED)) || (ServiceManagerState.this.states.contains(Service.State.FAILED));
/*     */       }
/*     */     };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 441 */     final Monitor.Guard stoppedGuard = new Monitor.Guard(this.monitor) {
/*     */       public boolean isSatisfied() {
/* 443 */         return ServiceManagerState.this.states.count(Service.State.TERMINATED) + ServiceManagerState.this.states.count(Service.State.FAILED) == ServiceManagerState.this.numberOfServices;
/*     */       }
/*     */     };
/*     */     
/*     */     @GuardedBy("monitor")
/* 448 */     final List<ListenerExecutorPair> listeners = Lists.newArrayList();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     @GuardedBy("monitor")
/* 459 */     final ExecutionQueue queuedListeners = new ExecutionQueue();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     ServiceManagerState(ImmutableCollection<Service> services)
/*     */     {
/* 469 */       this.numberOfServices = services.size();
/* 470 */       this.servicesByState.putAll(Service.State.NEW, services);
/* 471 */       for (Service service : services) {
/* 472 */         this.startupTimers.put(service, Stopwatch.createUnstarted());
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     void markReady()
/*     */     {
/* 481 */       this.monitor.enter();
/*     */       try {
/* 483 */         if (!this.transitioned)
/*     */         {
/* 485 */           this.ready = true;
/*     */         }
/*     */         else {
/* 488 */           List<Service> servicesInBadStates = Lists.newArrayList();
/* 489 */           for (Service service : servicesByState().values()) {
/* 490 */             if (service.state() != Service.State.NEW) {
/* 491 */               servicesInBadStates.add(service);
/*     */             }
/*     */           }
/* 494 */           throw new IllegalArgumentException("Services started transitioning asynchronously before the ServiceManager was constructed: " + servicesInBadStates);
/*     */         }
/*     */       }
/*     */       finally {
/* 498 */         this.monitor.leave();
/*     */       }
/*     */     }
/*     */     
/*     */     void addListener(Listener listener, Executor executor) {
/* 503 */       Preconditions.checkNotNull(listener, "listener");
/* 504 */       Preconditions.checkNotNull(executor, "executor");
/* 505 */       this.monitor.enter();
/*     */       try
/*     */       {
/* 508 */         if (!this.stoppedGuard.isSatisfied()) {
/* 509 */           this.listeners.add(new ListenerExecutorPair(listener, executor));
/*     */         }
/*     */       } finally {
/* 512 */         this.monitor.leave();
/*     */       }
/*     */     }
/*     */     
/*     */     void awaitHealthy() {
/* 517 */       this.monitor.enterWhenUninterruptibly(this.awaitHealthGuard);
/*     */       try {
/* 519 */         checkHealthy();
/*     */       } finally {
/* 521 */         this.monitor.leave();
/*     */       }
/*     */     }
/*     */     
/*     */     void awaitHealthy(long timeout, TimeUnit unit) throws TimeoutException {
/* 526 */       this.monitor.enter();
/*     */       try {
/* 528 */         if (!this.monitor.waitForUninterruptibly(this.awaitHealthGuard, timeout, unit)) {
/* 529 */           throw new TimeoutException("Timeout waiting for the services to become healthy. The following services have not started: " + Multimaps.filterKeys(this.servicesByState, Predicates.in(ImmutableSet.of(Service.State.NEW, Service.State.STARTING))));
/*     */         }
/*     */         
/*     */ 
/* 533 */         checkHealthy();
/*     */       } finally {
/* 535 */         this.monitor.leave();
/*     */       }
/*     */     }
/*     */     
/*     */     void awaitStopped() {
/* 540 */       this.monitor.enterWhenUninterruptibly(this.stoppedGuard);
/* 541 */       this.monitor.leave();
/*     */     }
/*     */     
/*     */     void awaitStopped(long timeout, TimeUnit unit) throws TimeoutException {
/* 545 */       this.monitor.enter();
/*     */       try {
/* 547 */         if (!this.monitor.waitForUninterruptibly(this.stoppedGuard, timeout, unit)) {
/* 548 */           throw new TimeoutException("Timeout waiting for the services to stop. The following services have not stopped: " + Multimaps.filterKeys(this.servicesByState, Predicates.not(Predicates.in(ImmutableSet.of(Service.State.TERMINATED, Service.State.FAILED)))));
/*     */         }
/*     */         
/*     */       }
/*     */       finally
/*     */       {
/* 554 */         this.monitor.leave();
/*     */       }
/*     */     }
/*     */     
/*     */     ImmutableMultimap<Service.State, Service> servicesByState() {
/* 559 */       ImmutableSetMultimap.Builder<Service.State, Service> builder = ImmutableSetMultimap.builder();
/* 560 */       this.monitor.enter();
/*     */       try {
/* 562 */         for (Entry<Service.State, Service> entry : this.servicesByState.entries()) {
/* 563 */           if (!(entry.getValue() instanceof NoOpService)) {
/* 564 */             builder.put(entry.getKey(), entry.getValue());
/*     */           }
/*     */         }
/*     */       } finally {
/* 568 */         this.monitor.leave();
/*     */       }
/* 570 */       return builder.build();
/*     */     }
/*     */     
/*     */     ImmutableMap<Service, Long> startupTimes()
/*     */     {
/* 575 */       this.monitor.enter();
/*     */       List<Entry<Service, Long>> loadTimes;
/* 577 */       try { loadTimes = Lists.newArrayListWithCapacity(this.states.size() - this.states.count(Service.State.NEW) + this.states.count(Service.State.STARTING));
/*     */         
/* 579 */         for (Entry<Service, Stopwatch> entry : this.startupTimers.entrySet()) {
/* 580 */           Service service = (Service)entry.getKey();
/* 581 */           Stopwatch stopWatch = (Stopwatch)entry.getValue();
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 586 */           if ((!stopWatch.isRunning()) && (!this.servicesByState.containsEntry(Service.State.NEW, service)) && (!(service instanceof NoOpService)))
/*     */           {
/* 588 */             loadTimes.add(Maps.immutableEntry(service, Long.valueOf(stopWatch.elapsed(TimeUnit.MILLISECONDS))));
/*     */           }
/*     */         }
/*     */       } finally {
/* 592 */         this.monitor.leave();
/*     */       }
/* 594 */       Collections.sort(loadTimes, Ordering.natural().onResultOf(new Function()
/*     */       {
/*     */         public Long apply(Entry<Service, Long> input) {
/* 597 */           return (Long)input.getValue();
/*     */         }
/* 599 */       }));
/* 600 */       ImmutableMap.Builder<Service, Long> builder = ImmutableMap.builder();
/* 601 */       for (Entry<Service, Long> entry : loadTimes) {
/* 602 */         builder.put(entry);
/*     */       }
/* 604 */       return builder.build();
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
/*     */ 
/*     */ 
/*     */     void transitionService(Service service, Service.State from, Service.State to)
/*     */     {
/* 619 */       Preconditions.checkNotNull(service);
/* 620 */       Preconditions.checkArgument(from != to);
/* 621 */       this.monitor.enter();
/*     */       try {
/* 623 */         this.transitioned = true;
/* 624 */         if (!this.ready) {
/*     */           return;
/*     */         }
/*     */         
/* 628 */         Preconditions.checkState(this.servicesByState.remove(from, service), "Service %s not at the expected location in the state map %s", new Object[] { service, from });
/*     */         
/* 630 */         Preconditions.checkState(this.servicesByState.put(to, service), "Service %s in the state map unexpectedly at %s", new Object[] { service, to });
/*     */         
/*     */ 
/* 633 */         Stopwatch stopwatch = (Stopwatch)this.startupTimers.get(service);
/* 634 */         if (from == Service.State.NEW) {
/* 635 */           stopwatch.start();
/*     */         }
/* 637 */         if ((to.compareTo(Service.State.RUNNING) >= 0) && (stopwatch.isRunning()))
/*     */         {
/* 639 */           stopwatch.stop();
/* 640 */           if (!(service instanceof NoOpService)) {
/* 641 */             ServiceManager.logger.log(Level.FINE, "Started {0} in {1}.", new Object[] { service, stopwatch });
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 648 */         if (to == Service.State.FAILED) {
/* 649 */           fireFailedListeners(service);
/*     */         }
/*     */         
/* 652 */         if (this.states.count(Service.State.RUNNING) == this.numberOfServices)
/*     */         {
/*     */ 
/* 655 */           fireHealthyListeners();
/* 656 */         } else if (this.states.count(Service.State.TERMINATED) + this.states.count(Service.State.FAILED) == this.numberOfServices) {
/* 657 */           fireStoppedListeners();
/*     */           
/* 659 */           this.listeners.clear();
/*     */         }
/*     */       } finally {
/* 662 */         this.monitor.leave();
/*     */         
/* 664 */         executeListeners();
/*     */       }
/*     */     }
/*     */     
/*     */     @GuardedBy("monitor")
/*     */     void fireStoppedListeners() {
/* 670 */       for (final ListenerExecutorPair pair : this.listeners) {
/* 671 */         this.queuedListeners.add(new Runnable()
/*     */         {
/* 673 */           public void run() { pair.listener.stopped(); } }, pair.executor);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     @GuardedBy("monitor")
/*     */     void fireHealthyListeners()
/*     */     {
/* 681 */       for (final ListenerExecutorPair pair : this.listeners) {
/* 682 */         this.queuedListeners.add(new Runnable()
/*     */         {
/* 684 */           public void run() { pair.listener.healthy(); } }, pair.executor);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     @GuardedBy("monitor")
/*     */     void fireFailedListeners(final Service service)
/*     */     {
/* 692 */       for (final ListenerExecutorPair pair : this.listeners) {
/* 693 */         this.queuedListeners.add(new Runnable()
/*     */         {
/* 695 */           public void run() { pair.listener.failure(service); } }, pair.executor);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     void executeListeners()
/*     */     {
/* 703 */       Preconditions.checkState(!this.monitor.isOccupiedByCurrentThread(), "It is incorrect to execute listeners with the monitor held.");
/*     */       
/* 705 */       this.queuedListeners.execute();
/*     */     }
/*     */     
/*     */     @GuardedBy("monitor")
/*     */     void checkHealthy() {
/* 710 */       if (this.states.count(Service.State.RUNNING) != this.numberOfServices) {
/* 711 */         throw new IllegalStateException("Expected to be healthy after starting. The following services are not running: " + Multimaps.filterKeys(this.servicesByState, Predicates.not(Predicates.equalTo(Service.State.RUNNING))));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static final class ServiceListener
/*     */     extends Service.Listener
/*     */   {
/*     */     final Service service;
/*     */     
/*     */ 
/*     */     final WeakReference<ServiceManagerState> state;
/*     */     
/*     */ 
/*     */ 
/*     */     ServiceListener(Service service, WeakReference<ServiceManagerState> state)
/*     */     {
/* 730 */       this.service = service;
/* 731 */       this.state = state;
/*     */     }
/*     */     
/*     */     public void starting() {
/* 735 */       ServiceManagerState state = (ServiceManagerState)this.state.get();
/* 736 */       if (state != null) {
/* 737 */         state.transitionService(this.service, Service.State.NEW, Service.State.STARTING);
/* 738 */         if (!(this.service instanceof NoOpService)) {
/* 739 */           ServiceManager.logger.log(Level.FINE, "Starting {0}.", this.service);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     public void running() {
/* 745 */       ServiceManagerState state = (ServiceManagerState)this.state.get();
/* 746 */       if (state != null) {
/* 747 */         state.transitionService(this.service, Service.State.STARTING, Service.State.RUNNING);
/*     */       }
/*     */     }
/*     */     
/*     */     public void stopping(Service.State from) {
/* 752 */       ServiceManagerState state = (ServiceManagerState)this.state.get();
/* 753 */       if (state != null) {
/* 754 */         state.transitionService(this.service, from, Service.State.STOPPING);
/*     */       }
/*     */     }
/*     */     
/*     */     public void terminated(Service.State from) {
/* 759 */       ServiceManagerState state = (ServiceManagerState)this.state.get();
/* 760 */       if (state != null) {
/* 761 */         if (!(this.service instanceof NoOpService)) {
/* 762 */           ServiceManager.logger.log(Level.FINE, "Service {0} has terminated. Previous state was: {1}", new Object[] { this.service, from });
/*     */         }
/*     */         
/* 765 */         state.transitionService(this.service, from, Service.State.TERMINATED);
/*     */       }
/*     */     }
/*     */     
/*     */     public void failed(Service.State from, Throwable failure) {
/* 770 */       ServiceManagerState state = (ServiceManagerState)this.state.get();
/* 771 */       if (state != null)
/*     */       {
/*     */ 
/* 774 */         if (!(this.service instanceof NoOpService)) {
/* 775 */           ServiceManager.logger.log(Level.SEVERE, "Service " + this.service + " has failed in the " + from + " state.", failure);
/*     */         }
/*     */         
/* 778 */         state.transitionService(this.service, from, Service.State.FAILED);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   @Immutable
/*     */   private static final class ListenerExecutorPair {
/*     */     final Listener listener;
/*     */     final Executor executor;
/*     */     
/*     */     ListenerExecutorPair(Listener listener, Executor executor) {
/* 789 */       this.listener = listener;
/* 790 */       this.executor = executor;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final class NoOpService
/*     */     extends AbstractService
/*     */   {
/* 803 */     protected void doStart() { notifyStarted(); }
/* 804 */     protected void doStop() { notifyStopped(); }
/*     */   }
/*     */   
/*     */ 
/*     */   private static final class EmptyServiceManagerWarning
/*     */     extends Throwable
/*     */   {}
/*     */   
/*     */ 
/*     */   private static final class SynchronizedExecutor
/*     */     implements Executor
/*     */   {
/*     */     public synchronized void execute(Runnable command)
/*     */     {
/* 818 */       command.run();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\util\concurrent\ServiceManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */