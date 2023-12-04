/*     */ package com.yammer.metrics;
/*     */ 
/*     */ import com.yammer.metrics.core.Gauge;
/*     */ import com.yammer.metrics.core.Histogram;
/*     */ import com.yammer.metrics.core.MetricName;
/*     */ import com.yammer.metrics.core.MetricsRegistry;
/*     */ import com.yammer.metrics.core.Timer;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public class Metrics
/*     */ {
/*  12 */   private static final MetricsRegistry DEFAULT_REGISTRY = new MetricsRegistry();
/*  13 */   private static final Thread SHUTDOWN_HOOK = new Thread()
/*     */   {
/*     */     public void run() {}
/*     */   };
/*     */   
/*     */   static
/*     */   {
/*  20 */     com.yammer.metrics.reporting.JmxReporter.startDefault(DEFAULT_REGISTRY);
/*  21 */     Runtime.getRuntime().addShutdownHook(SHUTDOWN_HOOK);
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
/*     */   public static <T> Gauge<T> newGauge(Class<?> klass, String name, Gauge<T> metric)
/*     */   {
/*  39 */     return DEFAULT_REGISTRY.newGauge(klass, name, metric);
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
/*     */   public static <T> Gauge<T> newGauge(Class<?> klass, String name, String scope, Gauge<T> metric)
/*     */   {
/*  57 */     return DEFAULT_REGISTRY.newGauge(klass, name, scope, metric);
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
/*     */   public static <T> Gauge<T> newGauge(MetricName metricName, Gauge<T> metric)
/*     */   {
/*  70 */     return DEFAULT_REGISTRY.newGauge(metricName, metric);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static com.yammer.metrics.core.Counter newCounter(Class<?> klass, String name)
/*     */   {
/*  82 */     return DEFAULT_REGISTRY.newCounter(klass, name);
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
/*     */   public static com.yammer.metrics.core.Counter newCounter(Class<?> klass, String name, String scope)
/*     */   {
/*  97 */     return DEFAULT_REGISTRY.newCounter(klass, name, scope);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static com.yammer.metrics.core.Counter newCounter(MetricName metricName)
/*     */   {
/* 108 */     return DEFAULT_REGISTRY.newCounter(metricName);
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
/*     */   public static Histogram newHistogram(Class<?> klass, String name, boolean biased)
/*     */   {
/* 123 */     return DEFAULT_REGISTRY.newHistogram(klass, name, biased);
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
/*     */   public static Histogram newHistogram(Class<?> klass, String name, String scope, boolean biased)
/*     */   {
/* 140 */     return DEFAULT_REGISTRY.newHistogram(klass, name, scope, biased);
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
/*     */   public static Histogram newHistogram(MetricName metricName, boolean biased)
/*     */   {
/* 153 */     return DEFAULT_REGISTRY.newHistogram(metricName, biased);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Histogram newHistogram(Class<?> klass, String name)
/*     */   {
/* 165 */     return DEFAULT_REGISTRY.newHistogram(klass, name);
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
/*     */   public static Histogram newHistogram(Class<?> klass, String name, String scope)
/*     */   {
/* 180 */     return DEFAULT_REGISTRY.newHistogram(klass, name, scope);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Histogram newHistogram(MetricName metricName)
/*     */   {
/* 191 */     return newHistogram(metricName, false);
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
/*     */   public static com.yammer.metrics.core.Meter newMeter(Class<?> klass, String name, String eventType, TimeUnit unit)
/*     */   {
/* 209 */     return DEFAULT_REGISTRY.newMeter(klass, name, eventType, unit);
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
/*     */   public static com.yammer.metrics.core.Meter newMeter(Class<?> klass, String name, String scope, String eventType, TimeUnit unit)
/*     */   {
/* 229 */     return DEFAULT_REGISTRY.newMeter(klass, name, scope, eventType, unit);
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
/*     */   public static com.yammer.metrics.core.Meter newMeter(MetricName metricName, String eventType, TimeUnit unit)
/*     */   {
/* 245 */     return DEFAULT_REGISTRY.newMeter(metricName, eventType, unit);
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
/*     */   public static Timer newTimer(Class<?> klass, String name, TimeUnit durationUnit, TimeUnit rateUnit)
/*     */   {
/* 262 */     return DEFAULT_REGISTRY.newTimer(klass, name, durationUnit, rateUnit);
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
/*     */   public static Timer newTimer(Class<?> klass, String name)
/*     */   {
/* 275 */     return DEFAULT_REGISTRY.newTimer(klass, name);
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
/*     */   public static Timer newTimer(Class<?> klass, String name, String scope, TimeUnit durationUnit, TimeUnit rateUnit)
/*     */   {
/* 294 */     return DEFAULT_REGISTRY.newTimer(klass, name, scope, durationUnit, rateUnit);
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
/*     */   public static Timer newTimer(Class<?> klass, String name, String scope)
/*     */   {
/* 309 */     return DEFAULT_REGISTRY.newTimer(klass, name, scope);
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
/*     */   public static Timer newTimer(MetricName metricName, TimeUnit durationUnit, TimeUnit rateUnit)
/*     */   {
/* 324 */     return DEFAULT_REGISTRY.newTimer(metricName, durationUnit, rateUnit);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static MetricsRegistry defaultRegistry()
/*     */   {
/* 333 */     return DEFAULT_REGISTRY;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void shutdown()
/*     */   {
/* 340 */     DEFAULT_REGISTRY.shutdown();
/* 341 */     com.yammer.metrics.reporting.JmxReporter.shutdownDefault();
/* 342 */     Runtime.getRuntime().removeShutdownHook(SHUTDOWN_HOOK);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\Metrics.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */