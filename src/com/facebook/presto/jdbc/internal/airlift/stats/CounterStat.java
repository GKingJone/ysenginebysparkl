/*     */ package com.facebook.presto.jdbc.internal.airlift.stats;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
/*     */ import java.util.concurrent.atomic.AtomicLong;
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
/*     */ 
/*     */ @Beta
/*     */ public class CounterStat
/*     */ {
/*  32 */   private final AtomicLong count = new AtomicLong(0L);
/*  33 */   private final DecayCounter oneMinute = new DecayCounter(ExponentialDecay.oneMinute());
/*  34 */   private final DecayCounter fiveMinute = new DecayCounter(ExponentialDecay.fiveMinutes());
/*  35 */   private final DecayCounter fifteenMinute = new DecayCounter(ExponentialDecay.fifteenMinutes());
/*     */   
/*     */   public void update(long count)
/*     */   {
/*  39 */     this.oneMinute.add(count);
/*  40 */     this.fiveMinute.add(count);
/*  41 */     this.fifteenMinute.add(count);
/*  42 */     this.count.addAndGet(count);
/*     */   }
/*     */   
/*     */   public void merge(CounterStat counterStat)
/*     */   {
/*  47 */     Preconditions.checkNotNull(counterStat, "counterStat is null");
/*  48 */     this.oneMinute.merge(counterStat.getOneMinute());
/*  49 */     this.fiveMinute.merge(counterStat.getFiveMinute());
/*  50 */     this.fifteenMinute.merge(counterStat.getFifteenMinute());
/*  51 */     this.count.addAndGet(counterStat.getTotalCount());
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public void reset()
/*     */   {
/*  57 */     this.oneMinute.reset();
/*  58 */     this.fiveMinute.reset();
/*  59 */     this.fifteenMinute.reset();
/*  60 */     this.count.set(0L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public void resetTo(CounterStat counterStat)
/*     */   {
/*  69 */     this.oneMinute.resetTo(counterStat.getOneMinute());
/*  70 */     this.fiveMinute.resetTo(counterStat.getFiveMinute());
/*  71 */     this.fifteenMinute.resetTo(counterStat.getFifteenMinute());
/*  72 */     this.count.set(counterStat.getTotalCount());
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public long getTotalCount()
/*     */   {
/*  78 */     return this.count.get();
/*     */   }
/*     */   
/*     */   @Managed
/*     */   @Nested
/*     */   public DecayCounter getOneMinute()
/*     */   {
/*  85 */     return this.oneMinute;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   @Nested
/*     */   public DecayCounter getFiveMinute()
/*     */   {
/*  92 */     return this.fiveMinute;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   @Nested
/*     */   public DecayCounter getFifteenMinute()
/*     */   {
/*  99 */     return this.fifteenMinute;
/*     */   }
/*     */   
/*     */   public CounterStatSnapshot snapshot()
/*     */   {
/* 104 */     return new CounterStatSnapshot(getTotalCount(), getOneMinute().snapshot(), getFiveMinute().snapshot(), getFifteenMinute().snapshot());
/*     */   }
/*     */   
/*     */ 
/*     */   public static class CounterStatSnapshot
/*     */   {
/*     */     private final long totalCount;
/*     */     
/*     */     private final DecayCounter.DecayCounterSnapshot oneMinute;
/*     */     
/*     */     private final DecayCounter.DecayCounterSnapshot fiveMinute;
/*     */     private final DecayCounter.DecayCounterSnapshot fifteenMinute;
/*     */     
/*     */     @JsonCreator
/*     */     public CounterStatSnapshot(@JsonProperty("totalCount") long totalCount, @JsonProperty("oneMinute") DecayCounter.DecayCounterSnapshot oneMinute, @JsonProperty("fiveMinute") DecayCounter.DecayCounterSnapshot fiveMinute, @JsonProperty("fifteenMinute") DecayCounter.DecayCounterSnapshot fifteenMinute)
/*     */     {
/* 120 */       this.totalCount = totalCount;
/* 121 */       this.oneMinute = oneMinute;
/* 122 */       this.fiveMinute = fiveMinute;
/* 123 */       this.fifteenMinute = fifteenMinute;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public long getTotalCount()
/*     */     {
/* 129 */       return this.totalCount;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public DecayCounter.DecayCounterSnapshot getOneMinute()
/*     */     {
/* 135 */       return this.oneMinute;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public DecayCounter.DecayCounterSnapshot getFiveMinute()
/*     */     {
/* 141 */       return this.fiveMinute;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public DecayCounter.DecayCounterSnapshot getFifteenMinute()
/*     */     {
/* 147 */       return this.fifteenMinute;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\stats\CounterStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */