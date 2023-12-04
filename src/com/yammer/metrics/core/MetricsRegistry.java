/*     */ package com.yammer.metrics.core;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.SortedMap;
/*     */ import java.util.TreeMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public class MetricsRegistry
/*     */ {
/*     */   private static final int EXPECTED_METRIC_COUNT = 1024;
/*     */   private final Clock clock;
/*     */   private final ConcurrentMap<MetricName, Metric> metrics;
/*     */   private final ThreadPools threadPools;
/*     */   private final List<MetricsRegistryListener> listeners;
/*     */   
/*     */   public MetricsRegistry()
/*     */   {
/*  22 */     this(Clock.defaultClock());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public MetricsRegistry(Clock clock)
/*     */   {
/*  31 */     this.clock = clock;
/*  32 */     this.metrics = newMetricsMap();
/*  33 */     this.threadPools = new ThreadPools();
/*  34 */     this.listeners = new java.util.concurrent.CopyOnWriteArrayList();
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
/*     */   public <T> Gauge<T> newGauge(Class<?> klass, String name, Gauge<T> metric)
/*     */   {
/*  49 */     return newGauge(klass, name, null, metric);
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
/*     */   public <T> Gauge<T> newGauge(Class<?> klass, String name, String scope, Gauge<T> metric)
/*     */   {
/*  66 */     return newGauge(createName(klass, name, scope), metric);
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
/*     */   public <T> Gauge<T> newGauge(MetricName metricName, Gauge<T> metric)
/*     */   {
/*  79 */     return (Gauge)getOrAdd(metricName, metric);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Counter newCounter(Class<?> klass, String name)
/*     */   {
/*  91 */     return newCounter(klass, name, null);
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
/*     */   public Counter newCounter(Class<?> klass, String name, String scope)
/*     */   {
/* 105 */     return newCounter(createName(klass, name, scope));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Counter newCounter(MetricName metricName)
/*     */   {
/* 115 */     return (Counter)getOrAdd(metricName, new Counter());
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
/*     */   public Histogram newHistogram(Class<?> klass, String name, boolean biased)
/*     */   {
/* 129 */     return newHistogram(klass, name, null, biased);
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
/*     */   public Histogram newHistogram(Class<?> klass, String name, String scope, boolean biased)
/*     */   {
/* 145 */     return newHistogram(createName(klass, name, scope), biased);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Histogram newHistogram(Class<?> klass, String name)
/*     */   {
/* 157 */     return newHistogram(klass, name, false);
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
/*     */   public Histogram newHistogram(Class<?> klass, String name, String scope)
/*     */   {
/* 172 */     return newHistogram(klass, name, scope, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Histogram newHistogram(MetricName metricName, boolean biased)
/*     */   {
/* 184 */     return (Histogram)getOrAdd(metricName, new Histogram(biased ? Histogram.SampleType.BIASED : Histogram.SampleType.UNIFORM));
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
/*     */   public Meter newMeter(Class<?> klass, String name, String eventType, TimeUnit unit)
/*     */   {
/* 202 */     return newMeter(klass, name, null, eventType, unit);
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
/*     */   public Meter newMeter(Class<?> klass, String name, String scope, String eventType, TimeUnit unit)
/*     */   {
/* 221 */     return newMeter(createName(klass, name, scope), eventType, unit);
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
/*     */   public Meter newMeter(MetricName metricName, String eventType, TimeUnit unit)
/*     */   {
/* 236 */     Metric existingMetric = (Metric)this.metrics.get(metricName);
/* 237 */     if (existingMetric != null) {
/* 238 */       return (Meter)existingMetric;
/*     */     }
/* 240 */     return (Meter)getOrAdd(metricName, new Meter(newMeterTickThreadPool(), eventType, unit, this.clock));
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
/*     */   public Timer newTimer(Class<?> klass, String name)
/*     */   {
/* 253 */     return newTimer(klass, name, null, TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
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
/*     */   public Timer newTimer(Class<?> klass, String name, TimeUnit durationUnit, TimeUnit rateUnit)
/*     */   {
/* 269 */     return newTimer(klass, name, null, durationUnit, rateUnit);
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
/*     */   public Timer newTimer(Class<?> klass, String name, String scope)
/*     */   {
/* 284 */     return newTimer(klass, name, scope, TimeUnit.MILLISECONDS, TimeUnit.SECONDS);
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
/*     */   public Timer newTimer(Class<?> klass, String name, String scope, TimeUnit durationUnit, TimeUnit rateUnit)
/*     */   {
/* 302 */     return newTimer(createName(klass, name, scope), durationUnit, rateUnit);
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
/*     */   public Timer newTimer(MetricName metricName, TimeUnit durationUnit, TimeUnit rateUnit)
/*     */   {
/* 316 */     Metric existingMetric = (Metric)this.metrics.get(metricName);
/* 317 */     if (existingMetric != null) {
/* 318 */       return (Timer)existingMetric;
/*     */     }
/* 320 */     return (Timer)getOrAdd(metricName, new Timer(newMeterTickThreadPool(), durationUnit, rateUnit, this.clock));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public java.util.Map<MetricName, Metric> allMetrics()
/*     */   {
/* 330 */     return Collections.unmodifiableMap(this.metrics);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SortedMap<String, SortedMap<MetricName, Metric>> groupedMetrics()
/*     */   {
/* 339 */     return groupedMetrics(MetricPredicate.ALL);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SortedMap<String, SortedMap<MetricName, Metric>> groupedMetrics(MetricPredicate predicate)
/*     */   {
/* 350 */     SortedMap<String, SortedMap<MetricName, Metric>> groups = new TreeMap();
/*     */     
/* 352 */     for (Map.Entry<MetricName, Metric> entry : this.metrics.entrySet()) {
/* 353 */       String qualifiedTypeName = ((MetricName)entry.getKey()).getGroup() + "." + ((MetricName)entry.getKey()).getType();
/*     */       
/* 355 */       if (predicate.matches((MetricName)entry.getKey(), (Metric)entry.getValue())) { String scopedName;
/*     */         String scopedName;
/* 357 */         if (((MetricName)entry.getKey()).hasScope()) {
/* 358 */           scopedName = qualifiedTypeName + "." + ((MetricName)entry.getKey()).getScope();
/*     */         } else {
/* 360 */           scopedName = qualifiedTypeName;
/*     */         }
/* 362 */         SortedMap<MetricName, Metric> group = (SortedMap)groups.get(scopedName);
/* 363 */         if (group == null) {
/* 364 */           group = new TreeMap();
/* 365 */           groups.put(scopedName, group);
/*     */         }
/* 367 */         group.put(entry.getKey(), entry.getValue());
/*     */       }
/*     */     }
/* 370 */     return Collections.unmodifiableSortedMap(groups);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void shutdown()
/*     */   {
/* 377 */     this.threadPools.shutdown();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ScheduledExecutorService newScheduledThreadPool(int poolSize, String name)
/*     */   {
/* 389 */     return this.threadPools.newScheduledThreadPool(poolSize, name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeMetric(Class<?> klass, String name)
/*     */   {
/* 400 */     removeMetric(klass, name, null);
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
/*     */   public void removeMetric(Class<?> klass, String name, String scope)
/*     */   {
/* 413 */     removeMetric(createName(klass, name, scope));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeMetric(MetricName name)
/*     */   {
/* 422 */     Metric metric = (Metric)this.metrics.remove(name);
/* 423 */     if (metric != null) {
/* 424 */       if ((metric instanceof Stoppable)) {
/* 425 */         ((Stoppable)metric).stop();
/*     */       }
/* 427 */       notifyMetricRemoved(name);
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
/*     */   public void addListener(MetricsRegistryListener listener)
/*     */   {
/* 440 */     this.listeners.add(listener);
/* 441 */     for (Map.Entry<MetricName, Metric> entry : this.metrics.entrySet()) {
/* 442 */       listener.onMetricAdded((MetricName)entry.getKey(), (Metric)entry.getValue());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeListener(MetricsRegistryListener listener)
/*     */   {
/* 452 */     this.listeners.remove(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected MetricName createName(Class<?> klass, String name, String scope)
/*     */   {
/* 464 */     return new MetricName(klass, name, scope);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ConcurrentMap<MetricName, Metric> newMetricsMap()
/*     */   {
/* 474 */     return new java.util.concurrent.ConcurrentHashMap(1024);
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
/*     */   protected final <T extends Metric> T getOrAdd(MetricName name, T metric)
/*     */   {
/* 487 */     Metric existingMetric = (Metric)this.metrics.get(name);
/* 488 */     if (existingMetric == null) {
/* 489 */       Metric justAddedMetric = (Metric)this.metrics.putIfAbsent(name, metric);
/* 490 */       if (justAddedMetric == null) {
/* 491 */         notifyMetricAdded(name, metric);
/* 492 */         return metric;
/*     */       }
/*     */       
/* 495 */       if ((metric instanceof Stoppable)) {
/* 496 */         ((Stoppable)metric).stop();
/*     */       }
/*     */       
/* 499 */       return justAddedMetric;
/*     */     }
/* 501 */     return existingMetric;
/*     */   }
/*     */   
/*     */   private ScheduledExecutorService newMeterTickThreadPool() {
/* 505 */     return this.threadPools.newScheduledThreadPool(2, "meter-tick");
/*     */   }
/*     */   
/*     */   private void notifyMetricRemoved(MetricName name) {
/* 509 */     for (MetricsRegistryListener listener : this.listeners) {
/* 510 */       listener.onMetricRemoved(name);
/*     */     }
/*     */   }
/*     */   
/*     */   private void notifyMetricAdded(MetricName name, Metric metric) {
/* 515 */     for (MetricsRegistryListener listener : this.listeners) {
/* 516 */       listener.onMetricAdded(name, metric);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\core\MetricsRegistry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */