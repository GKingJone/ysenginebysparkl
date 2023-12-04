/*     */ package com.facebook.presto.jdbc.internal.airlift.stats;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects.ToStringHelper;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.annotation.concurrent.GuardedBy;
/*     */ import org.weakref.jmx.Managed;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TimeDistribution
/*     */ {
/*     */   private static final double MAX_ERROR = 0.01D;
/*     */   @GuardedBy("this")
/*     */   private final QuantileDigest digest;
/*     */   private final TimeUnit unit;
/*     */   
/*     */   public TimeDistribution()
/*     */   {
/*  29 */     this(TimeUnit.SECONDS);
/*     */   }
/*     */   
/*     */   public TimeDistribution(TimeUnit unit)
/*     */   {
/*  34 */     Preconditions.checkNotNull(unit, "unit is null");
/*     */     
/*  36 */     this.digest = new QuantileDigest(0.01D);
/*  37 */     this.unit = unit;
/*     */   }
/*     */   
/*     */   public TimeDistribution(double alpha)
/*     */   {
/*  42 */     this(alpha, TimeUnit.SECONDS);
/*     */   }
/*     */   
/*     */   public TimeDistribution(double alpha, TimeUnit unit)
/*     */   {
/*  47 */     Preconditions.checkNotNull(unit, "unit is null");
/*     */     
/*  49 */     this.digest = new QuantileDigest(0.01D, alpha);
/*  50 */     this.unit = unit;
/*     */   }
/*     */   
/*     */   public synchronized void add(long value)
/*     */   {
/*  55 */     this.digest.add(value);
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized double getMaxError()
/*     */   {
/*  61 */     return this.digest.getConfidenceFactor();
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized double getCount()
/*     */   {
/*  67 */     return this.digest.getCount();
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized double getP50()
/*     */   {
/*  73 */     return convertToUnit(this.digest.getQuantile(0.5D));
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized double getP75()
/*     */   {
/*  79 */     return convertToUnit(this.digest.getQuantile(0.75D));
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized double getP90()
/*     */   {
/*  85 */     return convertToUnit(this.digest.getQuantile(0.9D));
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized double getP95()
/*     */   {
/*  91 */     return convertToUnit(this.digest.getQuantile(0.95D));
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized double getP99()
/*     */   {
/*  97 */     return convertToUnit(this.digest.getQuantile(0.99D));
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized double getMin()
/*     */   {
/* 103 */     return convertToUnit(this.digest.getMin());
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized double getMax()
/*     */   {
/* 109 */     return convertToUnit(this.digest.getMax());
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public TimeUnit getUnit()
/*     */   {
/* 115 */     return this.unit;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public Map<Double, Double> getPercentiles()
/*     */   {
/* 121 */     List<Double> percentiles = new ArrayList(100);
/* 122 */     for (int i = 0; i < 100; i++) {
/* 123 */       percentiles.add(Double.valueOf(i / 100.0D));
/*     */     }
/*     */     
/*     */     List<Long> values;
/* 127 */     synchronized (this) {
/* 128 */       values = this.digest.getQuantiles(percentiles);
/*     */     }
/*     */     List<Long> values;
/* 131 */     Map<Double, Double> result = new LinkedHashMap(values.size());
/* 132 */     for (int i = 0; i < percentiles.size(); i++) {
/* 133 */       result.put(percentiles.get(i), Double.valueOf(convertToUnit(((Long)values.get(i)).longValue())));
/*     */     }
/*     */     
/* 136 */     return result;
/*     */   }
/*     */   
/*     */   private double convertToUnit(long nanos)
/*     */   {
/* 141 */     if ((nanos == Long.MAX_VALUE) || (nanos == Long.MIN_VALUE)) {
/* 142 */       return NaN.0D;
/*     */     }
/* 144 */     return nanos * 1.0D / this.unit.toNanos(1L);
/*     */   }
/*     */   
/*     */   public TimeDistributionSnapshot snapshot()
/*     */   {
/* 149 */     return new TimeDistributionSnapshot(
/* 150 */       getMaxError(), 
/* 151 */       getCount(), 
/* 152 */       getP50(), 
/* 153 */       getP75(), 
/* 154 */       getP90(), 
/* 155 */       getP95(), 
/* 156 */       getP99(), 
/* 157 */       getMin(), 
/* 158 */       getMax(), 
/* 159 */       getUnit());
/*     */   }
/*     */   
/*     */ 
/*     */   public static class TimeDistributionSnapshot
/*     */   {
/*     */     private final double maxError;
/*     */     
/*     */     private final double count;
/*     */     
/*     */     private final double p50;
/*     */     
/*     */     private final double p75;
/*     */     
/*     */     private final double p90;
/*     */     
/*     */     private final double p95;
/*     */     
/*     */     private final double p99;
/*     */     
/*     */     private final double min;
/*     */     
/*     */     private final double max;
/*     */     
/*     */     private final TimeUnit unit;
/*     */     
/*     */     @JsonCreator
/*     */     public TimeDistributionSnapshot(@JsonProperty("maxError") double maxError, @JsonProperty("count") double count, @JsonProperty("p50") double p50, @JsonProperty("p75") double p75, @JsonProperty("p90") double p90, @JsonProperty("p95") double p95, @JsonProperty("p99") double p99, @JsonProperty("min") double min, @JsonProperty("max") double max, @JsonProperty("unit") TimeUnit unit)
/*     */     {
/* 188 */       this.maxError = maxError;
/* 189 */       this.count = count;
/* 190 */       this.p50 = p50;
/* 191 */       this.p75 = p75;
/* 192 */       this.p90 = p90;
/* 193 */       this.p95 = p95;
/* 194 */       this.p99 = p99;
/* 195 */       this.min = min;
/* 196 */       this.max = max;
/* 197 */       this.unit = unit;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public double getMaxError()
/*     */     {
/* 203 */       return this.maxError;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public double getCount()
/*     */     {
/* 209 */       return this.count;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public double getP50()
/*     */     {
/* 215 */       return this.p50;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public double getP75()
/*     */     {
/* 221 */       return this.p75;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public double getP90()
/*     */     {
/* 227 */       return this.p90;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public double getP95()
/*     */     {
/* 233 */       return this.p95;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public double getP99()
/*     */     {
/* 239 */       return this.p99;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public double getMin()
/*     */     {
/* 245 */       return this.min;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public double getMax()
/*     */     {
/* 251 */       return this.max;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public TimeUnit unit()
/*     */     {
/* 257 */       return this.unit;
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 263 */       return 
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 274 */         MoreObjects.toStringHelper(this).add("maxError", this.maxError).add("count", this.count).add("p50", this.p50).add("p75", this.p75).add("p90", this.p90).add("p95", this.p95).add("p99", this.p99).add("min", this.min).add("max", this.max).add("unit", this.unit).toString();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\stats\TimeDistribution.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */