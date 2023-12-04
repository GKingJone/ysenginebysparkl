/*     */ package com.facebook.presto.jdbc.internal.airlift.stats;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects.ToStringHelper;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
/*     */ import org.weakref.jmx.Managed;
/*     */ import org.weakref.jmx.Nested;
/*     */ 
/*     */ 
/*     */ public class DistributionStat
/*     */ {
/*     */   private final Distribution oneMinute;
/*     */   private final Distribution fiveMinutes;
/*     */   private final Distribution fifteenMinutes;
/*     */   private final Distribution allTime;
/*     */   
/*     */   public DistributionStat()
/*     */   {
/*  20 */     this.oneMinute = new Distribution(ExponentialDecay.oneMinute());
/*  21 */     this.fiveMinutes = new Distribution(ExponentialDecay.fiveMinutes());
/*  22 */     this.fifteenMinutes = new Distribution(ExponentialDecay.fifteenMinutes());
/*  23 */     this.allTime = new Distribution();
/*     */   }
/*     */   
/*     */   public void add(long value)
/*     */   {
/*  28 */     this.oneMinute.add(value);
/*  29 */     this.fiveMinutes.add(value);
/*  30 */     this.fifteenMinutes.add(value);
/*  31 */     this.allTime.add(value);
/*     */   }
/*     */   
/*     */   @Managed
/*     */   @Nested
/*     */   public Distribution getOneMinute()
/*     */   {
/*  38 */     return this.oneMinute;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   @Nested
/*     */   public Distribution getFiveMinutes()
/*     */   {
/*  45 */     return this.fiveMinutes;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   @Nested
/*     */   public Distribution getFifteenMinutes()
/*     */   {
/*  52 */     return this.fifteenMinutes;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   @Nested
/*     */   public Distribution getAllTime()
/*     */   {
/*  59 */     return this.allTime;
/*     */   }
/*     */   
/*     */   public DistributionStatSnapshot snapshot()
/*     */   {
/*  64 */     return new DistributionStatSnapshot(
/*  65 */       getOneMinute().snapshot(), 
/*  66 */       getFiveMinutes().snapshot(), 
/*  67 */       getFifteenMinutes().snapshot(), 
/*  68 */       getAllTime().snapshot());
/*     */   }
/*     */   
/*     */ 
/*     */   public static class DistributionStatSnapshot
/*     */   {
/*     */     private final Distribution.DistributionSnapshot oneMinute;
/*     */     
/*     */     private final Distribution.DistributionSnapshot fiveMinute;
/*     */     
/*     */     private final Distribution.DistributionSnapshot fifteenMinute;
/*     */     
/*     */     private final Distribution.DistributionSnapshot allTime;
/*     */     
/*     */     @JsonCreator
/*     */     public DistributionStatSnapshot(@JsonProperty("oneMinute") Distribution.DistributionSnapshot oneMinute, @JsonProperty("fiveMinute") Distribution.DistributionSnapshot fiveMinute, @JsonProperty("fifteenMinute") Distribution.DistributionSnapshot fifteenMinute, @JsonProperty("allTime") Distribution.DistributionSnapshot allTime)
/*     */     {
/*  85 */       this.oneMinute = oneMinute;
/*  86 */       this.fiveMinute = fiveMinute;
/*  87 */       this.fifteenMinute = fifteenMinute;
/*  88 */       this.allTime = allTime;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public Distribution.DistributionSnapshot getOneMinute()
/*     */     {
/*  94 */       return this.oneMinute;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public Distribution.DistributionSnapshot getFiveMinutes()
/*     */     {
/* 100 */       return this.fiveMinute;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public Distribution.DistributionSnapshot getFifteenMinutes()
/*     */     {
/* 106 */       return this.fifteenMinute;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public Distribution.DistributionSnapshot getAllTime()
/*     */     {
/* 112 */       return this.allTime;
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 118 */       return 
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 123 */         MoreObjects.toStringHelper(this).add("oneMinute", this.oneMinute).add("fiveMinute", this.fiveMinute).add("fifteenMinute", this.fifteenMinute).add("allTime", this.allTime).toString();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\stats\DistributionStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */