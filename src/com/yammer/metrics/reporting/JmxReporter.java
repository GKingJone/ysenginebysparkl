/*     */ package com.yammer.metrics.reporting;
/*     */ 
/*     */ import com.yammer.metrics.core.Counter;
/*     */ import com.yammer.metrics.core.Gauge;
/*     */ import com.yammer.metrics.core.Histogram;
/*     */ import com.yammer.metrics.core.Metered;
/*     */ import com.yammer.metrics.core.MetricName;
/*     */ import com.yammer.metrics.core.MetricsRegistry;
/*     */ import com.yammer.metrics.core.Timer;
/*     */ import com.yammer.metrics.stats.Snapshot;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.ObjectName;
/*     */ import org.slf4j.Logger;
/*     */ 
/*     */ public class JmxReporter extends AbstractReporter implements com.yammer.metrics.core.MetricsRegistryListener, com.yammer.metrics.core.MetricProcessor<Context>
/*     */ {
/*  19 */   private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(JmxReporter.class);
/*     */   private final Map<MetricName, ObjectName> registeredBeans;
/*     */   private final MBeanServer server;
/*     */   private static JmxReporter INSTANCE;
/*     */   
/*     */   public static abstract interface MetricMBean
/*     */   {
/*     */     public abstract ObjectName objectName();
/*     */   }
/*     */   
/*     */   private static abstract class AbstractBean
/*     */     implements MetricMBean
/*     */   {
/*     */     private final ObjectName objectName;
/*     */     
/*     */     protected AbstractBean(ObjectName objectName)
/*     */     {
/*  36 */       this.objectName = objectName;
/*     */     }
/*     */     
/*     */     public ObjectName objectName()
/*     */     {
/*  41 */       return this.objectName;
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface GaugeMBean
/*     */     extends MetricMBean
/*     */   {
/*     */     public abstract Object getValue();
/*     */   }
/*     */   
/*     */   private static class Gauge extends AbstractBean implements GaugeMBean
/*     */   {
/*     */     private final Gauge<?> metric;
/*     */     
/*     */     private Gauge(Gauge<?> metric, ObjectName objectName)
/*     */     {
/*  57 */       super();
/*  58 */       this.metric = metric;
/*     */     }
/*     */     
/*     */     public Object getValue()
/*     */     {
/*  63 */       return this.metric.value();
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface CounterMBean
/*     */     extends MetricMBean
/*     */   {
/*     */     public abstract long getCount();
/*     */   }
/*     */   
/*     */   private static class Counter extends AbstractBean implements CounterMBean
/*     */   {
/*     */     private final Counter metric;
/*     */     
/*     */     private Counter(Counter metric, ObjectName objectName)
/*     */     {
/*  79 */       super();
/*  80 */       this.metric = metric;
/*     */     }
/*     */     
/*     */     public long getCount()
/*     */     {
/*  85 */       return this.metric.count();
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface MeterMBean extends MetricMBean
/*     */   {
/*     */     public abstract long getCount();
/*     */     
/*     */     public abstract String getEventType();
/*     */     
/*     */     public abstract TimeUnit getRateUnit();
/*     */     
/*     */     public abstract double getMeanRate();
/*     */     
/*     */     public abstract double getOneMinuteRate();
/*     */     
/*     */     public abstract double getFiveMinuteRate();
/*     */     
/*     */     public abstract double getFifteenMinuteRate();
/*     */   }
/*     */   
/*     */   private static class Meter extends AbstractBean implements MeterMBean
/*     */   {
/*     */     private final Metered metric;
/*     */     
/*     */     private Meter(Metered metric, ObjectName objectName)
/*     */     {
/* 112 */       super();
/* 113 */       this.metric = metric;
/*     */     }
/*     */     
/*     */     public long getCount()
/*     */     {
/* 118 */       return this.metric.count();
/*     */     }
/*     */     
/*     */     public String getEventType()
/*     */     {
/* 123 */       return this.metric.eventType();
/*     */     }
/*     */     
/*     */     public TimeUnit getRateUnit()
/*     */     {
/* 128 */       return this.metric.rateUnit();
/*     */     }
/*     */     
/*     */     public double getMeanRate()
/*     */     {
/* 133 */       return this.metric.meanRate();
/*     */     }
/*     */     
/*     */     public double getOneMinuteRate()
/*     */     {
/* 138 */       return this.metric.oneMinuteRate();
/*     */     }
/*     */     
/*     */     public double getFiveMinuteRate()
/*     */     {
/* 143 */       return this.metric.fiveMinuteRate();
/*     */     }
/*     */     
/*     */     public double getFifteenMinuteRate()
/*     */     {
/* 148 */       return this.metric.fifteenMinuteRate();
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface HistogramMBean extends MetricMBean
/*     */   {
/*     */     public abstract long getCount();
/*     */     
/*     */     public abstract double getMin();
/*     */     
/*     */     public abstract double getMax();
/*     */     
/*     */     public abstract double getMean();
/*     */     
/*     */     public abstract double getStdDev();
/*     */     
/*     */     public abstract double get50thPercentile();
/*     */     
/*     */     public abstract double get75thPercentile();
/*     */     
/*     */     public abstract double get95thPercentile();
/*     */     
/*     */     public abstract double get98thPercentile();
/*     */     
/*     */     public abstract double get99thPercentile();
/*     */     
/*     */     public abstract double get999thPercentile();
/*     */     
/*     */     public abstract double[] values();
/*     */   }
/*     */   
/*     */   private static class Histogram implements HistogramMBean
/*     */   {
/*     */     private final ObjectName objectName;
/*     */     private final Histogram metric;
/*     */     
/*     */     private Histogram(Histogram metric, ObjectName objectName)
/*     */     {
/* 186 */       this.metric = metric;
/* 187 */       this.objectName = objectName;
/*     */     }
/*     */     
/*     */     public ObjectName objectName()
/*     */     {
/* 192 */       return this.objectName;
/*     */     }
/*     */     
/*     */     public double get50thPercentile()
/*     */     {
/* 197 */       return this.metric.getSnapshot().getMedian();
/*     */     }
/*     */     
/*     */     public long getCount()
/*     */     {
/* 202 */       return this.metric.count();
/*     */     }
/*     */     
/*     */     public double getMin()
/*     */     {
/* 207 */       return this.metric.min();
/*     */     }
/*     */     
/*     */     public double getMax()
/*     */     {
/* 212 */       return this.metric.max();
/*     */     }
/*     */     
/*     */     public double getMean()
/*     */     {
/* 217 */       return this.metric.mean();
/*     */     }
/*     */     
/*     */     public double getStdDev()
/*     */     {
/* 222 */       return this.metric.stdDev();
/*     */     }
/*     */     
/*     */     public double get75thPercentile()
/*     */     {
/* 227 */       return this.metric.getSnapshot().get75thPercentile();
/*     */     }
/*     */     
/*     */     public double get95thPercentile()
/*     */     {
/* 232 */       return this.metric.getSnapshot().get95thPercentile();
/*     */     }
/*     */     
/*     */     public double get98thPercentile()
/*     */     {
/* 237 */       return this.metric.getSnapshot().get98thPercentile();
/*     */     }
/*     */     
/*     */     public double get99thPercentile()
/*     */     {
/* 242 */       return this.metric.getSnapshot().get99thPercentile();
/*     */     }
/*     */     
/*     */     public double get999thPercentile()
/*     */     {
/* 247 */       return this.metric.getSnapshot().get999thPercentile();
/*     */     }
/*     */     
/*     */     public double[] values()
/*     */     {
/* 252 */       return this.metric.getSnapshot().getValues();
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface TimerMBean extends MeterMBean, HistogramMBean
/*     */   {
/*     */     public abstract TimeUnit getLatencyUnit();
/*     */   }
/*     */   
/*     */   static class Timer extends Meter implements TimerMBean
/*     */   {
/*     */     private final Timer metric;
/*     */     
/*     */     private Timer(Timer metric, ObjectName objectName)
/*     */     {
/* 267 */       super(objectName, null);
/* 268 */       this.metric = metric;
/*     */     }
/*     */     
/*     */     public double get50thPercentile()
/*     */     {
/* 273 */       return this.metric.getSnapshot().getMedian();
/*     */     }
/*     */     
/*     */     public TimeUnit getLatencyUnit()
/*     */     {
/* 278 */       return this.metric.durationUnit();
/*     */     }
/*     */     
/*     */     public double getMin()
/*     */     {
/* 283 */       return this.metric.min();
/*     */     }
/*     */     
/*     */     public double getMax()
/*     */     {
/* 288 */       return this.metric.max();
/*     */     }
/*     */     
/*     */     public double getMean()
/*     */     {
/* 293 */       return this.metric.mean();
/*     */     }
/*     */     
/*     */     public double getStdDev()
/*     */     {
/* 298 */       return this.metric.stdDev();
/*     */     }
/*     */     
/*     */     public double get75thPercentile()
/*     */     {
/* 303 */       return this.metric.getSnapshot().get75thPercentile();
/*     */     }
/*     */     
/*     */     public double get95thPercentile()
/*     */     {
/* 308 */       return this.metric.getSnapshot().get95thPercentile();
/*     */     }
/*     */     
/*     */     public double get98thPercentile()
/*     */     {
/* 313 */       return this.metric.getSnapshot().get98thPercentile();
/*     */     }
/*     */     
/*     */     public double get99thPercentile()
/*     */     {
/* 318 */       return this.metric.getSnapshot().get99thPercentile();
/*     */     }
/*     */     
/*     */     public double get999thPercentile()
/*     */     {
/* 323 */       return this.metric.getSnapshot().get999thPercentile();
/*     */     }
/*     */     
/*     */     public double[] values()
/*     */     {
/* 328 */       return this.metric.getSnapshot().getValues();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void startDefault(MetricsRegistry registry)
/*     */   {
/* 340 */     INSTANCE = new JmxReporter(registry);
/* 341 */     INSTANCE.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static JmxReporter getDefault()
/*     */   {
/* 350 */     return INSTANCE;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void shutdownDefault()
/*     */   {
/* 357 */     if (INSTANCE != null) {
/* 358 */       INSTANCE.shutdown();
/*     */     }
/*     */   }
/*     */   
/*     */   static final class Context {
/*     */     private final MetricName metricName;
/*     */     private final ObjectName objectName;
/*     */     
/*     */     public Context(MetricName metricName, ObjectName objectName) {
/* 367 */       this.metricName = metricName;
/* 368 */       this.objectName = objectName;
/*     */     }
/*     */     
/*     */     MetricName getMetricName() {
/* 372 */       return this.metricName;
/*     */     }
/*     */     
/*     */     ObjectName getObjectName() {
/* 376 */       return this.objectName;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JmxReporter(MetricsRegistry registry)
/*     */   {
/* 386 */     super(registry);
/* 387 */     this.registeredBeans = new java.util.concurrent.ConcurrentHashMap(100);
/* 388 */     this.server = java.lang.management.ManagementFactory.getPlatformMBeanServer();
/*     */   }
/*     */   
/*     */   public void onMetricAdded(MetricName name, com.yammer.metrics.core.Metric metric)
/*     */   {
/* 393 */     if (metric != null) {
/*     */       try {
/* 395 */         metric.processWith(this, name, new Context(name, new ObjectName(name.getMBeanName())));
/*     */       } catch (Exception e) {
/* 397 */         LOGGER.warn("Error processing {}", name, e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void onMetricRemoved(MetricName name)
/*     */   {
/* 404 */     ObjectName objectName = (ObjectName)this.registeredBeans.remove(name);
/* 405 */     if (objectName != null) {
/* 406 */       unregisterBean(objectName);
/*     */     }
/*     */   }
/*     */   
/*     */   public void processMeter(MetricName name, Metered meter, Context context) throws Exception
/*     */   {
/* 412 */     registerBean(context.getMetricName(), new Meter(meter, context.getObjectName(), null), context.getObjectName());
/*     */   }
/*     */   
/*     */   public void processCounter(MetricName name, Counter counter, Context context)
/*     */     throws Exception
/*     */   {
/* 418 */     registerBean(context.getMetricName(), new Counter(counter, context.getObjectName(), null), context.getObjectName());
/*     */   }
/*     */   
/*     */ 
/*     */   public void processHistogram(MetricName name, Histogram histogram, Context context)
/*     */     throws Exception
/*     */   {
/* 425 */     registerBean(context.getMetricName(), new Histogram(histogram, context.getObjectName(), null), context.getObjectName());
/*     */   }
/*     */   
/*     */ 
/*     */   public void processTimer(MetricName name, Timer timer, Context context)
/*     */     throws Exception
/*     */   {
/* 432 */     registerBean(context.getMetricName(), new Timer(timer, context.getObjectName(), null), context.getObjectName());
/*     */   }
/*     */   
/*     */   public void processGauge(MetricName name, Gauge<?> gauge, Context context)
/*     */     throws Exception
/*     */   {
/* 438 */     registerBean(context.getMetricName(), new Gauge(gauge, context.getObjectName(), null), context.getObjectName());
/*     */   }
/*     */   
/*     */ 
/*     */   public void shutdown()
/*     */   {
/* 444 */     getMetricsRegistry().removeListener(this);
/* 445 */     for (ObjectName name : this.registeredBeans.values()) {
/* 446 */       unregisterBean(name);
/*     */     }
/* 448 */     this.registeredBeans.clear();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final void start()
/*     */   {
/* 455 */     getMetricsRegistry().addListener(this);
/*     */   }
/*     */   
/*     */   private void registerBean(MetricName name, MetricMBean bean, ObjectName objectName)
/*     */     throws javax.management.MBeanRegistrationException, javax.management.OperationsException
/*     */   {
/* 461 */     if (this.server.isRegistered(objectName)) {
/* 462 */       this.server.unregisterMBean(objectName);
/*     */     }
/* 464 */     this.server.registerMBean(bean, objectName);
/* 465 */     this.registeredBeans.put(name, objectName);
/*     */   }
/*     */   
/*     */   private void unregisterBean(ObjectName name) {
/*     */     try {
/* 470 */       this.server.unregisterMBean(name);
/*     */ 
/*     */     }
/*     */     catch (javax.management.InstanceNotFoundException e)
/*     */     {
/* 475 */       LOGGER.trace("Error unregistering {}", name, e);
/*     */     } catch (javax.management.MBeanRegistrationException e) {
/* 477 */       LOGGER.debug("Error unregistering {}", name, e);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\reporting\JmxReporter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */