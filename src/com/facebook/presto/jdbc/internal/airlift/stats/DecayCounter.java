/*     */ package com.facebook.presto.jdbc.internal.airlift.stats;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects.ToStringHelper;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Ticker;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.weakref.jmx.Managed;
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
/*     */ public class DecayCounter
/*     */ {
/*     */   static final long RESCALE_THRESHOLD_SECONDS = 50L;
/*     */   private final double alpha;
/*     */   private final Ticker ticker;
/*     */   private long landmarkInSeconds;
/*     */   private double count;
/*     */   
/*     */   public DecayCounter(double alpha)
/*     */   {
/*  35 */     this(alpha, Ticker.systemTicker());
/*     */   }
/*     */   
/*     */   public DecayCounter(double alpha, Ticker ticker)
/*     */   {
/*  40 */     this.alpha = alpha;
/*  41 */     this.ticker = ticker;
/*  42 */     this.landmarkInSeconds = getTickInSeconds();
/*     */   }
/*     */   
/*     */   public synchronized void add(long value)
/*     */   {
/*  47 */     long nowInSeconds = getTickInSeconds();
/*     */     
/*  49 */     if (nowInSeconds - this.landmarkInSeconds >= 50L) {
/*  50 */       rescaleToNewLandmark(nowInSeconds);
/*     */     }
/*  52 */     this.count += value * weight(nowInSeconds, this.landmarkInSeconds);
/*     */   }
/*     */   
/*     */   public synchronized void merge(DecayCounter decayCounter)
/*     */   {
/*  57 */     Preconditions.checkNotNull(decayCounter, "decayCounter is null");
/*  58 */     Preconditions.checkArgument(decayCounter.alpha == this.alpha, "Expected decayCounter to have alpha %s, but was %s", new Object[] { Double.valueOf(this.alpha), Double.valueOf(decayCounter.alpha) });
/*     */     
/*  60 */     synchronized (decayCounter)
/*     */     {
/*  62 */       if (this.landmarkInSeconds < decayCounter.landmarkInSeconds)
/*     */       {
/*  64 */         rescaleToNewLandmark(decayCounter.landmarkInSeconds);
/*  65 */         this.count += decayCounter.count;
/*     */       }
/*     */       else
/*     */       {
/*  69 */         double otherRescaledCount = decayCounter.count / weight(this.landmarkInSeconds, decayCounter.landmarkInSeconds);
/*  70 */         this.count += otherRescaledCount;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void rescaleToNewLandmark(long newLandMarkInSeconds)
/*     */   {
/*  78 */     this.count /= weight(newLandMarkInSeconds, this.landmarkInSeconds);
/*  79 */     this.landmarkInSeconds = newLandMarkInSeconds;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized void reset()
/*     */   {
/*  85 */     this.landmarkInSeconds = getTickInSeconds();
/*  86 */     this.count = 0.0D;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public synchronized void resetTo(DecayCounter counter)
/*     */   {
/*  95 */     synchronized (counter) {
/*  96 */       this.landmarkInSeconds = counter.landmarkInSeconds;
/*  97 */       this.count = counter.count;
/*     */     }
/*     */   }
/*     */   
/*     */   @Managed
/*     */   public synchronized double getCount()
/*     */   {
/* 104 */     long nowInSeconds = getTickInSeconds();
/* 105 */     return this.count / weight(nowInSeconds, this.landmarkInSeconds);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @Managed
/*     */   public synchronized double getRate()
/*     */   {
/* 113 */     return getCount() * this.alpha;
/*     */   }
/*     */   
/*     */ 
/*     */   private double weight(long timestampInSeconds, long landmarkInSeconds)
/*     */   {
/* 119 */     return Math.exp(this.alpha * (timestampInSeconds - landmarkInSeconds));
/*     */   }
/*     */   
/*     */   private long getTickInSeconds()
/*     */   {
/* 124 */     return TimeUnit.NANOSECONDS.toSeconds(this.ticker.read());
/*     */   }
/*     */   
/*     */   public DecayCounterSnapshot snapshot()
/*     */   {
/* 129 */     return new DecayCounterSnapshot(getCount(), getRate());
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 135 */     return 
/*     */     
/*     */ 
/* 138 */       MoreObjects.toStringHelper(this).add("count", getCount()).add("rate", getRate()).toString();
/*     */   }
/*     */   
/*     */   public static class DecayCounterSnapshot
/*     */   {
/*     */     private final double count;
/*     */     private final double rate;
/*     */     
/*     */     @JsonCreator
/*     */     public DecayCounterSnapshot(@JsonProperty("count") double count, @JsonProperty("rate") double rate)
/*     */     {
/* 149 */       this.count = count;
/* 150 */       this.rate = rate;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public double getCount()
/*     */     {
/* 156 */       return this.count;
/*     */     }
/*     */     
/*     */     @JsonProperty
/*     */     public double getRate()
/*     */     {
/* 162 */       return this.rate;
/*     */     }
/*     */     
/*     */ 
/*     */     public String toString()
/*     */     {
/* 168 */       return 
/*     */       
/*     */ 
/* 171 */         MoreObjects.toStringHelper(this).add("count", this.count).add("rate", this.rate).toString();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\stats\DecayCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */