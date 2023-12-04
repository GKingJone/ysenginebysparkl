/*     */ package com.facebook.presto.jdbc.internal.guava.util.concurrent;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Function;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects.ToStringHelper;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Predicates;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Stopwatch;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Supplier;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Collections2;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableCollection;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableList;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableMap;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableMap.Builder;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableMultimap;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableSet;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableSetMultimap;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableSetMultimap.Builder;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Lists;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Maps;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Multimaps;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Multiset;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Ordering;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.SetMultimap;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Sets;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumMap;
/*     */ import java.util.Iterator;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/* 127 */   private static final Logger logger = Logger.getLogger(ServiceManager.class.getName());
/* 128 */   private static final ListenerCallQueue.Callback<Listener> HEALTHY_CALLBACK = new ListenerCallQueue.Callback("healthy()") {
/*     */     void call(Listener listener) {
/* 130 */       listener.healthy();
/*     */     }
/*     */   };
/* 133 */   private static final ListenerCallQueue.Callback<Listener> STOPPED_CALLBACK = new ListenerCallQueue.Callback("stopped()") {
/*     */     void call(Listener listener) {
/* 135 */       listener.stopped();
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/* 193 */     ImmutableList<Service> copy = ImmutableList.copyOf(services);
/* 194 */     if (copy.isEmpty())
/*     */     {
/*     */ 
/* 197 */       logger.log(Level.WARNING, "ServiceManager configured with no services.  Is your application configured properly?", new EmptyServiceManagerWarning(null));
/*     */       
/*     */ 
/* 200 */       copy = ImmutableList.of(new NoOpService(null));
/*     */     }
/* 202 */     this.state = new ServiceManagerState(copy);
/* 203 */     this.services = copy;
/* 204 */     WeakReference<ServiceManagerState> stateReference = new WeakReference(this.state);
/*     */     
/* 206 */     for (Service service : copy) {
/* 207 */       service.addListener(new ServiceListener(service, stateReference), MoreExecutors.directExecutor());
/*     */       
/*     */ 
/* 210 */       Preconditions.checkArgument(service.state() == Service.State.NEW, "Can only manage NEW services, %s", new Object[] { service });
/*     */     }
/*     */     
/*     */ 
/* 214 */     this.state.markReady();
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
/*     */   public void addListener(Listener listener, Executor executor)
/*     */   {
/* 241 */     this.state.addListener(listener, executor);
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
/*     */   public void addListener(Listener listener)
/*     */   {
/* 261 */     this.state.addListener(listener, MoreExecutors.directExecutor());
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
/* 273 */     for (Service service : this.services) {
/* 274 */       Service.State state = service.state();
/* 275 */       Preconditions.checkState(state == Service.State.NEW, "Service %s is %s, cannot start it.", new Object[] { service, state });
/*     */     }
/* 277 */     for (Service service : this.services) {
/*     */       try {
/* 279 */         this.state.tryStartTiming(service);
/* 280 */         service.startAsync();
/*     */ 
/*     */       }
/*     */       catch (IllegalStateException e)
/*     */       {
/*     */ 
/* 286 */         String str = String.valueOf(String.valueOf(service));logger.log(Level.WARNING, 24 + str.length() + "Unable to start Service " + str, e);
/*     */       }
/*     */     }
/* 289 */     return this;
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
/* 301 */     this.state.awaitHealthy();
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
/* 316 */     this.state.awaitHealthy(timeout, unit);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ServiceManager stopAsync()
/*     */   {
/* 326 */     for (Service service : this.services) {
/* 327 */       service.stopAsync();
/*     */     }
/* 329 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void awaitStopped()
/*     */   {
/* 338 */     this.state.awaitStopped();
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
/* 351 */     this.state.awaitStopped(timeout, unit);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isHealthy()
/*     */   {
/* 361 */     for (Service service : this.services) {
/* 362 */       if (!service.isRunning()) {
/* 363 */         return false;
/*     */       }
/*     */     }
/* 366 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ImmutableMultimap<Service.State, Service> servicesByState()
/*     */   {
/* 376 */     return this.state.servicesByState();
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
/* 387 */     return this.state.startupTimes();
/*     */   }
/*     */   
/*     */ 
/* 391 */   public String toString() { return MoreObjects.toStringHelper(ServiceManager.class).add("services", Collections2.filter(this.services, Predicates.not(Predicates.instanceOf(NoOpService.class)))).toString(); }
/*     */   
/*     */   @Beta
/*     */   public static abstract class Listener { public void healthy() {}
/*     */     
/*     */     public void stopped() {}
/*     */     
/*     */     public void failure(Service service) {}
/*     */   }
/*     */   
/* 401 */   private static final class ServiceManagerState { final Monitor monitor = new Monitor();
/*     */     @GuardedBy("monitor")
/* 403 */     final SetMultimap<Service.State, Service> servicesByState = Multimaps.newSetMultimap(new EnumMap(Service.State.class), new Supplier()
/*     */     {
/*     */ 
/*     */       public Set<Service> get()
/*     */       {
/* 408 */         return Sets.newLinkedHashSet();
/*     */       }
/* 403 */     });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     @GuardedBy("monitor")
/* 412 */     final Multiset<Service.State> states = this.servicesByState.keys();
/*     */     
/*     */     @GuardedBy("monitor")
/* 415 */     final Map<Service, Stopwatch> startupTimers = Maps.newIdentityHashMap();
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
/* 441 */     final Monitor.Guard awaitHealthGuard = new Monitor.Guard(this.monitor)
/*     */     {
/*     */       public boolean isSatisfied() {
/* 444 */         return (ServiceManagerState.this.states.count(Service.State.RUNNING) == ServiceManagerState.this.numberOfServices) || (ServiceManagerState.this.states.contains(Service.State.STOPPING)) || (ServiceManagerState.this.states.contains(Service.State.TERMINATED)) || (ServiceManagerState.this.states.contains(Service.State.FAILED));
/*     */       }
/*     */     };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 454 */     final Monitor.Guard stoppedGuard = new Monitor.Guard(this.monitor) {
/*     */       public boolean isSatisfied() {
/* 456 */         return ServiceManagerState.this.states.count(Service.State.TERMINATED) + ServiceManagerState.this.states.count(Service.State.FAILED) == ServiceManagerState.this.numberOfServices;
/*     */       }
/*     */     };
/*     */     
/*     */     @GuardedBy("monitor")
/* 461 */     final List<ListenerCallQueue<Listener>> listeners = Collections.synchronizedList(new ArrayList());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     ServiceManagerState(ImmutableCollection<Service> services)
/*     */     {
/* 472 */       this.numberOfServices = services.size();
/* 473 */       this.servicesByState.putAll(Service.State.NEW, services);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     void tryStartTiming(Service service)
/*     */     {
/* 481 */       this.monitor.enter();
/*     */       try {
/* 483 */         Stopwatch stopwatch = (Stopwatch)this.startupTimers.get(service);
/* 484 */         if (stopwatch == null) {
/* 485 */           this.startupTimers.put(service, Stopwatch.createStarted());
/*     */         }
/*     */       } finally {
/* 488 */         this.monitor.leave();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     void markReady()
/*     */     {
/* 497 */       this.monitor.enter();
/*     */       try {
/* 499 */         if (!this.transitioned)
/*     */         {
/* 501 */           this.ready = true;
/*     */         }
/*     */         else {
/* 504 */           List<Service> servicesInBadStates = Lists.newArrayList();
/* 505 */           for (Iterator i$ = servicesByState().values().iterator(); i$.hasNext();) { service = (Service)i$.next();
/* 506 */             if (service.state() != Service.State.NEW) {
/* 507 */               servicesInBadStates.add(service);
/*     */             }
/*     */           }
/* 510 */           i$ = String.valueOf(String.valueOf("Services started transitioning asynchronously before the ServiceManager was constructed: "));Service service = String.valueOf(String.valueOf(servicesInBadStates));throw new IllegalArgumentException(0 + i$.length() + service.length() + i$ + service);
/*     */         }
/*     */       }
/*     */       finally {
/* 514 */         this.monitor.leave();
/*     */       }
/*     */     }
/*     */     
/*     */     void addListener(Listener listener, Executor executor) {
/* 519 */       Preconditions.checkNotNull(listener, "listener");
/* 520 */       Preconditions.checkNotNull(executor, "executor");
/* 521 */       this.monitor.enter();
/*     */       try
/*     */       {
/* 524 */         if (!this.stoppedGuard.isSatisfied()) {
/* 525 */           this.listeners.add(new ListenerCallQueue(listener, executor));
/*     */         }
/*     */       } finally {
/* 528 */         this.monitor.leave();
/*     */       }
/*     */     }
/*     */     
/*     */     void awaitHealthy() {
/* 533 */       this.monitor.enterWhenUninterruptibly(this.awaitHealthGuard);
/*     */       try {
/* 535 */         checkHealthy();
/*     */       } finally {
/* 537 */         this.monitor.leave();
/*     */       }
/*     */     }
/*     */     
/*     */     void awaitHealthy(long timeout, TimeUnit unit) throws TimeoutException {
/* 542 */       this.monitor.enter();
/*     */       try {
/* 544 */         if (!this.monitor.waitForUninterruptibly(this.awaitHealthGuard, timeout, unit)) {
/* 545 */           String str1 = String.valueOf(String.valueOf("Timeout waiting for the services to become healthy. The following services have not started: "));String str2 = String.valueOf(String.valueOf(Multimaps.filterKeys(this.servicesByState, Predicates.in(ImmutableSet.of(Service.State.NEW, Service.State.STARTING)))));throw new TimeoutException(0 + str1.length() + str2.length() + str1 + str2);
/*     */         }
/*     */         
/*     */ 
/* 549 */         checkHealthy();
/*     */       } finally {
/* 551 */         this.monitor.leave();
/*     */       }
/*     */     }
/*     */     
/*     */     void awaitStopped() {
/* 556 */       this.monitor.enterWhenUninterruptibly(this.stoppedGuard);
/* 557 */       this.monitor.leave();
/*     */     }
/*     */     
/*     */     void awaitStopped(long timeout, TimeUnit unit) throws TimeoutException {
/* 561 */       this.monitor.enter();
/*     */       try {
/* 563 */         if (!this.monitor.waitForUninterruptibly(this.stoppedGuard, timeout, unit)) {
/* 564 */           String str1 = String.valueOf(String.valueOf("Timeout waiting for the services to stop. The following services have not stopped: "));String str2 = String.valueOf(String.valueOf(Multimaps.filterKeys(this.servicesByState, Predicates.not(Predicates.in(ImmutableSet.of(Service.State.TERMINATED, Service.State.FAILED))))));throw new TimeoutException(0 + str1.length() + str2.length() + str1 + str2);
/*     */         }
/*     */         
/*     */       }
/*     */       finally
/*     */       {
/* 570 */         this.monitor.leave();
/*     */       }
/*     */     }
/*     */     
/*     */     ImmutableMultimap<Service.State, Service> servicesByState() {
/* 575 */       ImmutableSetMultimap.Builder<Service.State, Service> builder = ImmutableSetMultimap.builder();
/* 576 */       this.monitor.enter();
/*     */       try {
/* 578 */         for (Entry<Service.State, Service> entry : this.servicesByState.entries()) {
/* 579 */           if (!(entry.getValue() instanceof NoOpService)) {
/* 580 */             builder.put(entry.getKey(), entry.getValue());
/*     */           }
/*     */         }
/*     */       } finally {
/* 584 */         this.monitor.leave();
/*     */       }
/* 586 */       return builder.build();
/*     */     }
/*     */     
/*     */     ImmutableMap<Service, Long> startupTimes()
/*     */     {
/* 591 */       this.monitor.enter();
/*     */       List<Entry<Service, Long>> loadTimes;
/* 593 */       try { loadTimes = Lists.newArrayListWithCapacity(this.startupTimers.size());
/*     */         
/* 595 */         for (Entry<Service, Stopwatch> entry : this.startupTimers.entrySet()) {
/* 596 */           Service service = (Service)entry.getKey();
/* 597 */           Stopwatch stopWatch = (Stopwatch)entry.getValue();
/* 598 */           if ((!stopWatch.isRunning()) && (!(service instanceof NoOpService))) {
/* 599 */             loadTimes.add(Maps.immutableEntry(service, Long.valueOf(stopWatch.elapsed(TimeUnit.MILLISECONDS))));
/*     */           }
/*     */         }
/*     */       } finally {
/* 603 */         this.monitor.leave();
/*     */       }
/* 605 */       Collections.sort(loadTimes, Ordering.natural().onResultOf(new Function()
/*     */       {
/*     */         public Long apply(Entry<Service, Long> input) {
/* 608 */           return (Long)input.getValue();
/*     */         }
/* 610 */       }));
/* 611 */       ImmutableMap.Builder<Service, Long> builder = ImmutableMap.builder();
/* 612 */       for (Entry<Service, Long> entry : loadTimes) {
/* 613 */         builder.put(entry);
/*     */       }
/* 615 */       return builder.build();
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
/* 630 */       Preconditions.checkNotNull(service);
/* 631 */       Preconditions.checkArgument(from != to);
/* 632 */       this.monitor.enter();
/*     */       try {
/* 634 */         this.transitioned = true;
/* 635 */         if (!this.ready) {
/*     */           return;
/*     */         }
/*     */         
/* 639 */         Preconditions.checkState(this.servicesByState.remove(from, service), "Service %s not at the expected location in the state map %s", new Object[] { service, from });
/*     */         
/* 641 */         Preconditions.checkState(this.servicesByState.put(to, service), "Service %s in the state map unexpectedly at %s", new Object[] { service, to });
/*     */         
/*     */ 
/* 644 */         Stopwatch stopwatch = (Stopwatch)this.startupTimers.get(service);
/* 645 */         if (stopwatch == null)
/*     */         {
/* 647 */           stopwatch = Stopwatch.createStarted();
/* 648 */           this.startupTimers.put(service, stopwatch);
/*     */         }
/* 650 */         if ((to.compareTo(Service.State.RUNNING) >= 0) && (stopwatch.isRunning()))
/*     */         {
/* 652 */           stopwatch.stop();
/* 653 */           if (!(service instanceof NoOpService)) {
/* 654 */             ServiceManager.logger.log(Level.FINE, "Started {0} in {1}.", new Object[] { service, stopwatch });
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 660 */         if (to == Service.State.FAILED) {
/* 661 */           fireFailedListeners(service);
/*     */         }
/*     */         
/* 664 */         if (this.states.count(Service.State.RUNNING) == this.numberOfServices)
/*     */         {
/*     */ 
/* 667 */           fireHealthyListeners();
/* 668 */         } else if (this.states.count(Service.State.TERMINATED) + this.states.count(Service.State.FAILED) == this.numberOfServices) {
/* 669 */           fireStoppedListeners();
/*     */         }
/*     */       } finally {
/* 672 */         this.monitor.leave();
/*     */         
/* 674 */         executeListeners();
/*     */       }
/*     */     }
/*     */     
/*     */     @GuardedBy("monitor")
/*     */     void fireStoppedListeners() {
/* 680 */       ServiceManager.STOPPED_CALLBACK.enqueueOn(this.listeners);
/*     */     }
/*     */     
/*     */     @GuardedBy("monitor")
/*     */     void fireHealthyListeners() {
/* 685 */       ServiceManager.HEALTHY_CALLBACK.enqueueOn(this.listeners);
/*     */     }
/*     */     
/*     */     @GuardedBy("monitor")
/*     */     void fireFailedListeners(final Service service) {
/* 690 */       String str = String.valueOf(String.valueOf(service));new ListenerCallQueue.Callback(18 + str.length() + "failed({service=" + str + "})")
/*     */       {
/* 692 */         void call(Listener listener) { listener.failure(service); } }.enqueueOn(this.listeners);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     void executeListeners()
/*     */     {
/* 699 */       Preconditions.checkState(!this.monitor.isOccupiedByCurrentThread(), "It is incorrect to execute listeners with the monitor held.");
/*     */       
/*     */ 
/* 702 */       for (int i = 0; i < this.listeners.size(); i++) {
/* 703 */         ((ListenerCallQueue)this.listeners.get(i)).execute();
/*     */       }
/*     */     }
/*     */     
/*     */     @GuardedBy("monitor")
/*     */     void checkHealthy() {
/* 709 */       if (this.states.count(Service.State.RUNNING) != this.numberOfServices) {
/* 710 */         String str = String.valueOf(String.valueOf(Multimaps.filterKeys(this.servicesByState, Predicates.not(Predicates.equalTo(Service.State.RUNNING)))));IllegalStateException exception = new IllegalStateException(79 + str.length() + "Expected to be healthy after starting. The following services are not running: " + str);
/*     */         
/*     */ 
/* 713 */         throw exception;
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
/*     */     final WeakReference<ServiceManagerState> state;
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
/* 775 */           String str1 = String.valueOf(String.valueOf(this.service));String str2 = String.valueOf(String.valueOf(from));ServiceManager.logger.log(Level.SEVERE, 34 + str1.length() + str2.length() + "Service " + str1 + " has failed in the " + str2 + " state.", failure);
/*     */         }
/*     */         
/* 778 */         state.transitionService(this.service, from, Service.State.FAILED);
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
/*     */   private static final class NoOpService
/*     */     extends AbstractService
/*     */   {
/* 792 */     protected void doStart() { notifyStarted(); }
/* 793 */     protected void doStop() { notifyStopped(); }
/*     */   }
/*     */   
/*     */   private static final class EmptyServiceManagerWarning
/*     */     extends Throwable
/*     */   {}
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\util\concurrent\ServiceManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */