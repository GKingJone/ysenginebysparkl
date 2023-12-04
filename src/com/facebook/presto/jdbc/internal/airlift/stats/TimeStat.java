/*     */ package com.facebook.presto.jdbc.internal.airlift.stats;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.units.Duration;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects.ToStringHelper;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Ticker;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.weakref.jmx.Managed;
/*     */ import org.weakref.jmx.Nested;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TimeStat
/*     */ {
/*     */   private final TimeDistribution oneMinute;
/*     */   private final TimeDistribution fiveMinutes;
/*     */   private final TimeDistribution fifteenMinutes;
/*     */   private final TimeDistribution allTime;
/*     */   private final Ticker ticker;
/*     */   
/*     */   public TimeStat()
/*     */   {
/*  41 */     this(Ticker.systemTicker(), TimeUnit.SECONDS);
/*     */   }
/*     */   
/*     */   public TimeStat(Ticker ticker)
/*     */   {
/*  46 */     this(ticker, TimeUnit.SECONDS);
/*     */   }
/*     */   
/*     */   public TimeStat(TimeUnit unit)
/*     */   {
/*  51 */     this(Ticker.systemTicker(), unit);
/*     */   }
/*     */   
/*     */   public TimeStat(Ticker ticker, TimeUnit unit)
/*     */   {
/*  56 */     this.ticker = ticker;
/*  57 */     this.oneMinute = new TimeDistribution(ExponentialDecay.oneMinute(), unit);
/*  58 */     this.fiveMinutes = new TimeDistribution(ExponentialDecay.fiveMinutes(), unit);
/*  59 */     this.fifteenMinutes = new TimeDistribution(ExponentialDecay.fifteenMinutes(), unit);
/*  60 */     this.allTime = new TimeDistribution(unit);
/*     */   }
/*     */   
/*     */   public void add(double value, TimeUnit timeUnit)
/*     */   {
/*  65 */     add(new Duration(value, timeUnit));
/*     */   }
/*     */   
/*     */   public void add(Duration duration)
/*     */   {
/*  70 */     add(duration.getValue(TimeUnit.NANOSECONDS));
/*     */   }
/*     */   
/*     */   private void add(long value)
/*     */   {
/*  75 */     this.oneMinute.add(value);
/*  76 */     this.fiveMinutes.add(value);
/*  77 */     this.fifteenMinutes.add(value);
/*  78 */     this.allTime.add(value);
/*     */   }
/*     */   
/*     */   public <T> T time(Callable<T> callable)
/*     */     throws Exception
/*     */   {
/*  84 */     long start = this.ticker.read();
/*  85 */     T result = callable.call();
/*  86 */     add(this.ticker.read() - start);
/*  87 */     return result;
/*     */   }
/*     */   
/*     */   public BlockTimer time()
/*     */   {
/*  92 */     return new BlockTimer();
/*     */   }
/*     */   
/*     */   public class BlockTimer
/*     */     implements AutoCloseable
/*     */   {
/*  98 */     private final long start = TimeStat.this.ticker.read();
/*     */     
/*     */     public BlockTimer() {}
/*     */     
/*     */     public void close() {
/* 103 */       TimeStat.this.add(TimeStat.this.ticker.read() - this.start);
/*     */     }
/*     */   }
/*     */   
/*     */   @Managed
/*     */   @Nested
/*     */   public TimeDistribution getOneMinute()
/*     */   {
/* 111 */     return this.oneMinute;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   @Nested
/*     */   public TimeDistribution getFiveMinutes()
/*     */   {
/* 118 */     return this.fiveMinutes;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   @Nested
/*     */   public TimeDistribution getFifteenMinutes()
/*     */   {
/* 125 */     return this.fifteenMinutes;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   @Nested
/*     */   public TimeDistribution getAllTime()
/*     */   {
/* 132 */     return this.allTime;
/*     */   }
/*     */   
/*     */   public TimeDistributionStatSnapshot snapshot()
/*     */   {
/* 137 */     return new TimeDistributionStatSnapshot(
/* 138 */       getOneMinute().snapshot(), 
/* 139 */       getFiveMinutes().snapshot(), 
/* 140 */       getFifteenMinutes().snapshot(), 
/* 141 */       getAllTime().snapshot());
/*     */   }
/*     */   
/*     */ 
/*     */   public static class TimeDistributionStatSnapshot
/*     */   {
/*     */     private final TimeDistribution.TimeDistributionSnapshot oneMinute;
/*     */     
/*     */     private final TimeDistribution.TimeDistributionSnapshot fiveMinute;
/*     */     
/*     */     private final TimeDistribution.TimeDistributionSnapshot fifteenMinute;
/*     */     
/*     */     private final TimeDistribution.TimeDistributionSnapshot allTime;
/*     */     
/*     */     @JsonCreator
/*     */     public TimeDistributionStatSnapshot(@JsonProperty("oneMinute") TimeDistribution.TimeDistributionSnapshot oneMinute, @JsonProperty("fiveMinute") TimeDistribution.TimeDistributionSnapshot fiveMinute, @JsonProperty("fifteenMinute") TimeDistribution.TimeDistributionSnapshot fifteenMinute, @JsonProperty("allTime") TimeDistribution.TimeDistributionSnapshot allTime)
/*     */     {
/* 158 */       this.oneMinute = oneMinute;
/* 159 */       this.fiveMinute = fiveMinute;
/* 160 */       this.fifteenMinute = fifteenMinute;
/* 161 */       this.allTime = allTime;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public TimeDistribution.TimeDistributionSnapshot getOneMinute()
/*     */     {
/* 167 */       return this.oneMinute;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public TimeDistribution.TimeDistributionSnapshot getFiveMinutes()
/*     */     {
/* 173 */       return this.fiveMinute;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public TimeDistribution.TimeDistributionSnapshot getFifteenMinutes()
/*     */     {
/* 179 */       return this.fifteenMinute;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public TimeDistribution.TimeDistributionSnapshot getAllTime()
/*     */     {
/* 185 */       return this.allTime;
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 191 */       return 
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 196 */         MoreObjects.toStringHelper(this).add("oneMinute", this.oneMinute).add("fiveMinute", this.fiveMinute).add("fifteenMinute", this.fifteenMinute).add("allTime", this.allTime).toString();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\stats\TimeStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */