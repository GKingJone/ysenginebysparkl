/*     */ package com.yammer.metrics.reporting;
/*     */ 
/*     */ import com.yammer.metrics.core.Clock;
/*     */ import com.yammer.metrics.core.Counter;
/*     */ import com.yammer.metrics.core.Gauge;
/*     */ import com.yammer.metrics.core.Histogram;
/*     */ import com.yammer.metrics.core.Metered;
/*     */ import com.yammer.metrics.core.Metric;
/*     */ import com.yammer.metrics.core.MetricName;
/*     */ import com.yammer.metrics.core.MetricPredicate;
/*     */ import com.yammer.metrics.core.MetricsRegistry;
/*     */ import com.yammer.metrics.core.Timer;
/*     */ import com.yammer.metrics.stats.Snapshot;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public class CsvReporter extends AbstractPollingReporter implements com.yammer.metrics.core.MetricProcessor<Context>
/*     */ {
/*     */   private final MetricPredicate predicate;
/*     */   private final File outputDir;
/*     */   private final Map<MetricName, PrintStream> streamMap;
/*     */   private final Clock clock;
/*     */   private long startTime;
/*     */   
/*     */   public static void enable(File outputDir, long period, TimeUnit unit)
/*     */   {
/*  34 */     enable(com.yammer.metrics.Metrics.defaultRegistry(), outputDir, period, unit);
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
/*     */   public static void enable(MetricsRegistry metricsRegistry, File outputDir, long period, TimeUnit unit)
/*     */   {
/*  47 */     CsvReporter reporter = new CsvReporter(metricsRegistry, outputDir);
/*  48 */     reporter.start(period, unit);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CsvReporter(MetricsRegistry metricsRegistry, File outputDir)
/*     */   {
/*  81 */     this(metricsRegistry, MetricPredicate.ALL, outputDir);
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
/*     */   public CsvReporter(MetricsRegistry metricsRegistry, MetricPredicate predicate, File outputDir)
/*     */   {
/*  98 */     this(metricsRegistry, predicate, outputDir, Clock.defaultClock());
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
/*     */   public CsvReporter(MetricsRegistry metricsRegistry, MetricPredicate predicate, File outputDir, Clock clock)
/*     */   {
/* 117 */     super(metricsRegistry, "csv-reporter");
/* 118 */     if ((outputDir.exists()) && (!outputDir.isDirectory())) {
/* 119 */       throw new IllegalArgumentException(outputDir + " is not a directory");
/*     */     }
/* 121 */     this.outputDir = outputDir;
/* 122 */     this.predicate = predicate;
/* 123 */     this.streamMap = new HashMap();
/* 124 */     this.startTime = 0L;
/* 125 */     this.clock = clock;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected PrintStream createStreamForMetric(MetricName metricName)
/*     */     throws IOException
/*     */   {
/* 137 */     File newFile = new File(this.outputDir, metricName.getName() + ".csv");
/* 138 */     if (newFile.createNewFile()) {
/* 139 */       return new PrintStream(new FileOutputStream(newFile));
/*     */     }
/* 141 */     throw new IOException("Unable to create " + newFile);
/*     */   }
/*     */   
/*     */   public void run()
/*     */   {
/* 146 */     final long time = TimeUnit.MILLISECONDS.toSeconds(this.clock.time() - this.startTime);
/* 147 */     Set<Entry<MetricName, Metric>> metrics = getMetricsRegistry().allMetrics().entrySet();
/*     */     try {
/* 149 */       for (Entry<MetricName, Metric> entry : metrics) {
/* 150 */         final MetricName metricName = (MetricName)entry.getKey();
/* 151 */         Metric metric = (Metric)entry.getValue();
/* 152 */         if (this.predicate.matches(metricName, metric)) {
/* 153 */           Context context = new Context()
/*     */           {
/*     */             public PrintStream getStream(String header) throws IOException {
/* 156 */               PrintStream stream = CsvReporter.this.getPrintStream(metricName, header);
/* 157 */               stream.print(time);
/* 158 */               stream.print(',');
/* 159 */               return stream;
/*     */             }
/*     */             
/* 162 */           };
/* 163 */           metric.processWith(this, (MetricName)entry.getKey(), context);
/*     */         }
/*     */       }
/*     */     } catch (Exception e) {
/* 167 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public void processMeter(MetricName name, Metered meter, Context context) throws IOException
/*     */   {
/* 173 */     PrintStream stream = context.getStream("# time,count,1 min rate,mean rate,5 min rate,15 min rate");
/*     */     
/* 175 */     stream.append(meter.count() + ',' + meter.oneMinuteRate() + ',' + meter.meanRate() + ',' + meter.fiveMinuteRate() + ',' + meter.fifteenMinuteRate()).println();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 182 */     stream.flush();
/*     */   }
/*     */   
/*     */   public void processCounter(MetricName name, Counter counter, Context context) throws IOException
/*     */   {
/* 187 */     PrintStream stream = context.getStream("# time,count");
/* 188 */     stream.println(counter.count());
/* 189 */     stream.flush();
/*     */   }
/*     */   
/*     */   public void processHistogram(MetricName name, Histogram histogram, Context context) throws IOException
/*     */   {
/* 194 */     PrintStream stream = context.getStream("# time,min,max,mean,median,stddev,95%,99%,99.9%");
/* 195 */     Snapshot snapshot = histogram.getSnapshot();
/* 196 */     stream.append(histogram.min() + ',' + histogram.max() + ',' + histogram.mean() + ',' + snapshot.getMedian() + ',' + histogram.stdDev() + ',' + snapshot.get95thPercentile() + ',' + snapshot.get99thPercentile() + ',' + snapshot.get999thPercentile()).println();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 206 */     stream.println();
/* 207 */     stream.flush();
/*     */   }
/*     */   
/*     */   public void processTimer(MetricName name, Timer timer, Context context) throws IOException
/*     */   {
/* 212 */     PrintStream stream = context.getStream("# time,min,max,mean,median,stddev,95%,99%,99.9%");
/* 213 */     Snapshot snapshot = timer.getSnapshot();
/* 214 */     stream.append(timer.min() + ',' + timer.max() + ',' + timer.mean() + ',' + snapshot.getMedian() + ',' + timer.stdDev() + ',' + snapshot.get95thPercentile() + ',' + snapshot.get99thPercentile() + ',' + snapshot.get999thPercentile()).println();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 224 */     stream.flush();
/*     */   }
/*     */   
/*     */   public void processGauge(MetricName name, Gauge<?> gauge, Context context) throws IOException
/*     */   {
/* 229 */     PrintStream stream = context.getStream("# time,value");
/* 230 */     stream.println(gauge.value());
/* 231 */     stream.flush();
/*     */   }
/*     */   
/*     */   public void start(long period, TimeUnit unit)
/*     */   {
/* 236 */     this.startTime = this.clock.time();
/* 237 */     super.start(period, unit);
/*     */   }
/*     */   
/*     */   public void shutdown()
/*     */   {
/*     */     try {
/* 243 */       super.shutdown(); } finally { java.util.Iterator i$;
/*     */       PrintStream out;
/* 245 */       for (PrintStream out : this.streamMap.values()) {
/* 246 */         out.close();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private PrintStream getPrintStream(MetricName metricName, String header) throws IOException
/*     */   {
/*     */     PrintStream stream;
/* 254 */     synchronized (this.streamMap) {
/* 255 */       stream = (PrintStream)this.streamMap.get(metricName);
/* 256 */       if (stream == null) {
/* 257 */         stream = createStreamForMetric(metricName);
/* 258 */         this.streamMap.put(metricName, stream);
/* 259 */         stream.println(header);
/*     */       }
/*     */     }
/* 262 */     return stream;
/*     */   }
/*     */   
/*     */   public static abstract interface Context
/*     */   {
/*     */     public abstract PrintStream getStream(String paramString)
/*     */       throws IOException;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\reporting\CsvReporter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */