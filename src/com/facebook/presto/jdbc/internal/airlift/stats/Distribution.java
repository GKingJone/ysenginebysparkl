/*     */ package com.facebook.presto.jdbc.internal.airlift.stats;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects.ToStringHelper;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableList;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.annotation.concurrent.GuardedBy;
/*     */ import javax.annotation.concurrent.ThreadSafe;
/*     */ import org.weakref.jmx.Managed;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public class Distribution
/*     */ {
/*     */   private static final double MAX_ERROR = 0.01D;
/*     */   @GuardedBy("this")
/*     */   private final QuantileDigest digest;
/*     */   private final DecayCounter total;
/*     */   
/*     */   public Distribution()
/*     */   {
/*  30 */     this.digest = new QuantileDigest(0.01D);
/*  31 */     this.total = new DecayCounter(0.0D);
/*     */   }
/*     */   
/*     */   public Distribution(double alpha)
/*     */   {
/*  36 */     this.digest = new QuantileDigest(0.01D, alpha);
/*  37 */     this.total = new DecayCounter(alpha);
/*     */   }
/*     */   
/*     */   public Distribution(Distribution distribution)
/*     */   {
/*  42 */     this.digest = new QuantileDigest(distribution.digest);
/*  43 */     this.total = new DecayCounter(distribution.digest.getAlpha());
/*  44 */     this.total.merge(distribution.total);
/*     */   }
/*     */   
/*     */   public synchronized void add(long value)
/*     */   {
/*  49 */     this.digest.add(value);
/*  50 */     this.total.add(value);
/*     */   }
/*     */   
/*     */   public synchronized void add(long value, long count)
/*     */   {
/*  55 */     this.digest.add(value, count);
/*  56 */     this.total.add(value * count);
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized double getMaxError()
/*     */   {
/*  62 */     return this.digest.getConfidenceFactor();
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized double getCount()
/*     */   {
/*  68 */     return this.digest.getCount();
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized double getTotal()
/*     */   {
/*  74 */     return this.total.getCount();
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized long getP01()
/*     */   {
/*  80 */     return this.digest.getQuantile(0.01D);
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized long getP05()
/*     */   {
/*  86 */     return this.digest.getQuantile(0.05D);
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized long getP10()
/*     */   {
/*  92 */     return this.digest.getQuantile(0.1D);
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized long getP25()
/*     */   {
/*  98 */     return this.digest.getQuantile(0.25D);
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized long getP50()
/*     */   {
/* 104 */     return this.digest.getQuantile(0.5D);
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized long getP75()
/*     */   {
/* 110 */     return this.digest.getQuantile(0.75D);
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized long getP90()
/*     */   {
/* 116 */     return this.digest.getQuantile(0.9D);
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized long getP95()
/*     */   {
/* 122 */     return this.digest.getQuantile(0.95D);
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized long getP99()
/*     */   {
/* 128 */     return this.digest.getQuantile(0.99D);
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized long getMin()
/*     */   {
/* 134 */     return this.digest.getMin();
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized long getMax()
/*     */   {
/* 140 */     return this.digest.getMax();
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public Map<Double, Long> getPercentiles()
/*     */   {
/* 146 */     List<Double> percentiles = new ArrayList(100);
/* 147 */     for (int i = 0; i < 100; i++) {
/* 148 */       percentiles.add(Double.valueOf(i / 100.0D));
/*     */     }
/*     */     
/*     */     List<Long> values;
/* 152 */     synchronized (this) {
/* 153 */       values = this.digest.getQuantiles(percentiles);
/*     */     }
/*     */     List<Long> values;
/* 156 */     Map<Double, Long> result = new LinkedHashMap(values.size());
/* 157 */     for (int i = 0; i < percentiles.size(); i++) {
/* 158 */       result.put(percentiles.get(i), values.get(i));
/*     */     }
/*     */     
/* 161 */     return result;
/*     */   }
/*     */   
/*     */   public synchronized List<Long> getPercentiles(List<Double> percentiles)
/*     */   {
/* 166 */     return this.digest.getQuantiles(percentiles);
/*     */   }
/*     */   
/*     */   public synchronized DistributionSnapshot snapshot()
/*     */   {
/* 171 */     List<Long> quantiles = this.digest.getQuantiles(ImmutableList.of(Double.valueOf(0.01D), Double.valueOf(0.05D), Double.valueOf(0.1D), Double.valueOf(0.25D), Double.valueOf(0.5D), Double.valueOf(0.75D), Double.valueOf(0.9D), Double.valueOf(0.95D), Double.valueOf(0.99D)));
/* 172 */     return new DistributionSnapshot(
/* 173 */       getMaxError(), 
/* 174 */       getCount(), 
/* 175 */       getTotal(), 
/* 176 */       ((Long)quantiles.get(0)).longValue(), 
/* 177 */       ((Long)quantiles.get(1)).longValue(), 
/* 178 */       ((Long)quantiles.get(2)).longValue(), 
/* 179 */       ((Long)quantiles.get(3)).longValue(), 
/* 180 */       ((Long)quantiles.get(4)).longValue(), 
/* 181 */       ((Long)quantiles.get(5)).longValue(), 
/* 182 */       ((Long)quantiles.get(6)).longValue(), 
/* 183 */       ((Long)quantiles.get(7)).longValue(), 
/* 184 */       ((Long)quantiles.get(8)).longValue(), 
/* 185 */       getMin(), 
/* 186 */       getMax());
/*     */   }
/*     */   
/*     */ 
/*     */   public static class DistributionSnapshot
/*     */   {
/*     */     private final double maxError;
/*     */     
/*     */     private final double count;
/*     */     
/*     */     private final double total;
/*     */     
/*     */     private final long p01;
/*     */     
/*     */     private final long p05;
/*     */     
/*     */     private final long p10;
/*     */     
/*     */     private final long p25;
/*     */     
/*     */     private final long p50;
/*     */     
/*     */     private final long p75;
/*     */     
/*     */     private final long p90;
/*     */     
/*     */     private final long p95;
/*     */     
/*     */     private final long p99;
/*     */     
/*     */     private final long min;
/*     */     
/*     */     private final long max;
/*     */     
/*     */     @JsonCreator
/*     */     public DistributionSnapshot(@JsonProperty("maxError") double maxError, @JsonProperty("count") double count, @JsonProperty("total") double total, @JsonProperty("p01") long p01, @JsonProperty("p05") long p05, @JsonProperty("p10") long p10, @JsonProperty("p25") long p25, @JsonProperty("p50") long p50, @JsonProperty("p75") long p75, @JsonProperty("p90") long p90, @JsonProperty("p95") long p95, @JsonProperty("p99") long p99, @JsonProperty("min") long min, @JsonProperty("max") long max)
/*     */     {
/* 223 */       this.maxError = maxError;
/* 224 */       this.count = count;
/* 225 */       this.total = total;
/* 226 */       this.p01 = p01;
/* 227 */       this.p05 = p05;
/* 228 */       this.p10 = p10;
/* 229 */       this.p25 = p25;
/* 230 */       this.p50 = p50;
/* 231 */       this.p75 = p75;
/* 232 */       this.p90 = p90;
/* 233 */       this.p95 = p95;
/* 234 */       this.p99 = p99;
/* 235 */       this.min = min;
/* 236 */       this.max = max;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public double getMaxError()
/*     */     {
/* 242 */       return this.maxError;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public double getCount()
/*     */     {
/* 248 */       return this.count;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public double getTotal()
/*     */     {
/* 254 */       return this.total;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public long getP01()
/*     */     {
/* 260 */       return this.p01;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public long getP05()
/*     */     {
/* 266 */       return this.p05;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public long getP10()
/*     */     {
/* 272 */       return this.p10;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public long getP25()
/*     */     {
/* 278 */       return this.p25;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public long getP50()
/*     */     {
/* 284 */       return this.p50;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public long getP75()
/*     */     {
/* 290 */       return this.p75;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public long getP90()
/*     */     {
/* 296 */       return this.p90;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public long getP95()
/*     */     {
/* 302 */       return this.p95;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public long getP99()
/*     */     {
/* 308 */       return this.p99;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public long getMin()
/*     */     {
/* 314 */       return this.min;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public long getMax()
/*     */     {
/* 320 */       return this.max;
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 326 */       return 
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 341 */         MoreObjects.toStringHelper(this).add("maxError", this.maxError).add("count", this.count).add("total", this.total).add("p01", this.p01).add("p05", this.p05).add("p10", this.p10).add("p25", this.p25).add("p50", this.p50).add("p75", this.p75).add("p90", this.p90).add("p95", this.p95).add("p99", this.p99).add("min", this.min).add("max", this.max).toString();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\stats\Distribution.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */