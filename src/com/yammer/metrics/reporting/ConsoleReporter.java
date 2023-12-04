/*     */ package com.yammer.metrics.reporting;
/*     */ 
/*     */ import com.yammer.metrics.core.Clock;
/*     */ import com.yammer.metrics.core.Gauge;
/*     */ import com.yammer.metrics.core.Histogram;
/*     */ import com.yammer.metrics.core.Metered;
/*     */ import com.yammer.metrics.core.Metric;
/*     */ import com.yammer.metrics.core.MetricName;
/*     */ import com.yammer.metrics.core.MetricPredicate;
/*     */ import com.yammer.metrics.core.MetricsRegistry;
/*     */ import com.yammer.metrics.core.Timer;
/*     */ import com.yammer.metrics.stats.Snapshot;
/*     */ import java.io.PrintStream;
/*     */ import java.text.DateFormat;
/*     */ import java.util.Locale;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.SortedMap;
/*     */ import java.util.TimeZone;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public class ConsoleReporter extends AbstractPollingReporter implements com.yammer.metrics.core.MetricProcessor<PrintStream>
/*     */ {
/*     */   private static final int CONSOLE_WIDTH = 80;
/*     */   private final PrintStream out;
/*     */   private final MetricPredicate predicate;
/*     */   private final Clock clock;
/*     */   private final TimeZone timeZone;
/*     */   private final Locale locale;
/*     */   
/*     */   public static void enable(long period, TimeUnit unit)
/*     */   {
/*  32 */     enable(com.yammer.metrics.Metrics.defaultRegistry(), period, unit);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void enable(MetricsRegistry metricsRegistry, long period, TimeUnit unit)
/*     */   {
/*  44 */     ConsoleReporter reporter = new ConsoleReporter(metricsRegistry, System.out, MetricPredicate.ALL);
/*     */     
/*     */ 
/*  47 */     reporter.start(period, unit);
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
/*     */   public ConsoleReporter(PrintStream out)
/*     */   {
/*  63 */     this(com.yammer.metrics.Metrics.defaultRegistry(), out, MetricPredicate.ALL);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConsoleReporter(MetricsRegistry metricsRegistry, PrintStream out, MetricPredicate predicate)
/*     */   {
/*  75 */     this(metricsRegistry, out, predicate, Clock.defaultClock(), TimeZone.getDefault());
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
/*     */   public ConsoleReporter(MetricsRegistry metricsRegistry, PrintStream out, MetricPredicate predicate, Clock clock, TimeZone timeZone)
/*     */   {
/*  93 */     this(metricsRegistry, out, predicate, clock, timeZone, Locale.getDefault());
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
/*     */   public ConsoleReporter(MetricsRegistry metricsRegistry, PrintStream out, MetricPredicate predicate, Clock clock, TimeZone timeZone, Locale locale)
/*     */   {
/* 112 */     super(metricsRegistry, "console-reporter");
/* 113 */     this.out = out;
/* 114 */     this.predicate = predicate;
/* 115 */     this.clock = clock;
/* 116 */     this.timeZone = timeZone;
/* 117 */     this.locale = locale;
/*     */   }
/*     */   
/*     */   public void run()
/*     */   {
/*     */     try {
/* 123 */       DateFormat format = DateFormat.getDateTimeInstance(3, 2, this.locale);
/*     */       
/*     */ 
/* 126 */       format.setTimeZone(this.timeZone);
/* 127 */       String dateTime = format.format(new java.util.Date(this.clock.time()));
/* 128 */       this.out.print(dateTime);
/* 129 */       this.out.print(' ');
/* 130 */       for (int i = 0; i < 80 - dateTime.length() - 1; i++) {
/* 131 */         this.out.print('=');
/*     */       }
/* 133 */       this.out.println();
/* 134 */       for (Map.Entry<String, SortedMap<MetricName, Metric>> entry : getMetricsRegistry().groupedMetrics(this.predicate).entrySet())
/*     */       {
/* 136 */         this.out.print((String)entry.getKey());
/* 137 */         this.out.println(':');
/* 138 */         for (Map.Entry<MetricName, Metric> subEntry : ((SortedMap)entry.getValue()).entrySet()) {
/* 139 */           this.out.print("  ");
/* 140 */           this.out.print(((MetricName)subEntry.getKey()).getName());
/* 141 */           this.out.println(':');
/* 142 */           ((Metric)subEntry.getValue()).processWith(this, (MetricName)subEntry.getKey(), this.out);
/* 143 */           this.out.println();
/*     */         }
/* 145 */         this.out.println();
/*     */       }
/* 147 */       this.out.println();
/* 148 */       this.out.flush();
/*     */     } catch (Exception e) {
/* 150 */       e.printStackTrace(this.out);
/*     */     }
/*     */   }
/*     */   
/*     */   public void processGauge(MetricName name, Gauge<?> gauge, PrintStream stream)
/*     */   {
/* 156 */     stream.printf(this.locale, "    value = %s\n", new Object[] { gauge.value() });
/*     */   }
/*     */   
/*     */   public void processCounter(MetricName name, com.yammer.metrics.core.Counter counter, PrintStream stream)
/*     */   {
/* 161 */     stream.printf(this.locale, "    count = %d\n", new Object[] { Long.valueOf(counter.count()) });
/*     */   }
/*     */   
/*     */   public void processMeter(MetricName name, Metered meter, PrintStream stream)
/*     */   {
/* 166 */     String unit = abbrev(meter.rateUnit());
/* 167 */     stream.printf(this.locale, "             count = %d\n", new Object[] { Long.valueOf(meter.count()) });
/* 168 */     stream.printf(this.locale, "         mean rate = %2.2f %s/%s\n", new Object[] { Double.valueOf(meter.meanRate()), meter.eventType(), unit });
/*     */     
/*     */ 
/*     */ 
/* 172 */     stream.printf(this.locale, "     1-minute rate = %2.2f %s/%s\n", new Object[] { Double.valueOf(meter.oneMinuteRate()), meter.eventType(), unit });
/*     */     
/*     */ 
/*     */ 
/* 176 */     stream.printf(this.locale, "     5-minute rate = %2.2f %s/%s\n", new Object[] { Double.valueOf(meter.fiveMinuteRate()), meter.eventType(), unit });
/*     */     
/*     */ 
/*     */ 
/* 180 */     stream.printf(this.locale, "    15-minute rate = %2.2f %s/%s\n", new Object[] { Double.valueOf(meter.fifteenMinuteRate()), meter.eventType(), unit });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void processHistogram(MetricName name, Histogram histogram, PrintStream stream)
/*     */   {
/* 188 */     Snapshot snapshot = histogram.getSnapshot();
/* 189 */     stream.printf(this.locale, "               min = %2.2f\n", new Object[] { Double.valueOf(histogram.min()) });
/* 190 */     stream.printf(this.locale, "               max = %2.2f\n", new Object[] { Double.valueOf(histogram.max()) });
/* 191 */     stream.printf(this.locale, "              mean = %2.2f\n", new Object[] { Double.valueOf(histogram.mean()) });
/* 192 */     stream.printf(this.locale, "            stddev = %2.2f\n", new Object[] { Double.valueOf(histogram.stdDev()) });
/* 193 */     stream.printf(this.locale, "            median = %2.2f\n", new Object[] { Double.valueOf(snapshot.getMedian()) });
/* 194 */     stream.printf(this.locale, "              75%% <= %2.2f\n", new Object[] { Double.valueOf(snapshot.get75thPercentile()) });
/* 195 */     stream.printf(this.locale, "              95%% <= %2.2f\n", new Object[] { Double.valueOf(snapshot.get95thPercentile()) });
/* 196 */     stream.printf(this.locale, "              98%% <= %2.2f\n", new Object[] { Double.valueOf(snapshot.get98thPercentile()) });
/* 197 */     stream.printf(this.locale, "              99%% <= %2.2f\n", new Object[] { Double.valueOf(snapshot.get99thPercentile()) });
/* 198 */     stream.printf(this.locale, "            99.9%% <= %2.2f\n", new Object[] { Double.valueOf(snapshot.get999thPercentile()) });
/*     */   }
/*     */   
/*     */   public void processTimer(MetricName name, Timer timer, PrintStream stream)
/*     */   {
/* 203 */     processMeter(name, timer, stream);
/* 204 */     String durationUnit = abbrev(timer.durationUnit());
/* 205 */     Snapshot snapshot = timer.getSnapshot();
/* 206 */     stream.printf(this.locale, "               min = %2.2f%s\n", new Object[] { Double.valueOf(timer.min()), durationUnit });
/* 207 */     stream.printf(this.locale, "               max = %2.2f%s\n", new Object[] { Double.valueOf(timer.max()), durationUnit });
/* 208 */     stream.printf(this.locale, "              mean = %2.2f%s\n", new Object[] { Double.valueOf(timer.mean()), durationUnit });
/* 209 */     stream.printf(this.locale, "            stddev = %2.2f%s\n", new Object[] { Double.valueOf(timer.stdDev()), durationUnit });
/* 210 */     stream.printf(this.locale, "            median = %2.2f%s\n", new Object[] { Double.valueOf(snapshot.getMedian()), durationUnit });
/* 211 */     stream.printf(this.locale, "              75%% <= %2.2f%s\n", new Object[] { Double.valueOf(snapshot.get75thPercentile()), durationUnit });
/* 212 */     stream.printf(this.locale, "              95%% <= %2.2f%s\n", new Object[] { Double.valueOf(snapshot.get95thPercentile()), durationUnit });
/* 213 */     stream.printf(this.locale, "              98%% <= %2.2f%s\n", new Object[] { Double.valueOf(snapshot.get98thPercentile()), durationUnit });
/* 214 */     stream.printf(this.locale, "              99%% <= %2.2f%s\n", new Object[] { Double.valueOf(snapshot.get99thPercentile()), durationUnit });
/* 215 */     stream.printf(this.locale, "            99.9%% <= %2.2f%s\n", new Object[] { Double.valueOf(snapshot.get999thPercentile()), durationUnit });
/*     */   }
/*     */   
/*     */   private String abbrev(TimeUnit unit) {
/* 219 */     switch (unit) {
/*     */     case NANOSECONDS: 
/* 221 */       return "ns";
/*     */     case MICROSECONDS: 
/* 223 */       return "us";
/*     */     case MILLISECONDS: 
/* 225 */       return "ms";
/*     */     case SECONDS: 
/* 227 */       return "s";
/*     */     case MINUTES: 
/* 229 */       return "m";
/*     */     case HOURS: 
/* 231 */       return "h";
/*     */     case DAYS: 
/* 233 */       return "d";
/*     */     }
/* 235 */     throw new IllegalArgumentException("Unrecognized TimeUnit: " + unit);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\reporting\ConsoleReporter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */