/*     */ package com.facebook.presto.jdbc.internal.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects.ToStringHelper;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
/*     */ import java.util.Objects;
/*     */ import javax.annotation.Nullable;
/*     */ import javax.annotation.concurrent.Immutable;
/*     */ import javax.validation.constraints.NotNull;
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
/*     */ @Immutable
/*     */ public class StatementStats
/*     */ {
/*     */   private final String state;
/*     */   private final boolean queued;
/*     */   private final boolean scheduled;
/*     */   private final int nodes;
/*     */   private final int totalSplits;
/*     */   private final int queuedSplits;
/*     */   private final int runningSplits;
/*     */   private final int completedSplits;
/*     */   private final long userTimeMillis;
/*     */   private final long cpuTimeMillis;
/*     */   private final long wallTimeMillis;
/*     */   private final long processedRows;
/*     */   private final long processedBytes;
/*     */   private final StageStats rootStage;
/*     */   
/*     */   @JsonCreator
/*     */   public StatementStats(@JsonProperty("state") String state, @JsonProperty("queued") boolean queued, @JsonProperty("scheduled") boolean scheduled, @JsonProperty("nodes") int nodes, @JsonProperty("totalSplits") int totalSplits, @JsonProperty("queuedSplits") int queuedSplits, @JsonProperty("runningSplits") int runningSplits, @JsonProperty("completedSplits") int completedSplits, @JsonProperty("userTimeMillis") long userTimeMillis, @JsonProperty("cpuTimeMillis") long cpuTimeMillis, @JsonProperty("wallTimeMillis") long wallTimeMillis, @JsonProperty("processedRows") long processedRows, @JsonProperty("processedBytes") long processedBytes, @JsonProperty("rootStage") StageStats rootStage)
/*     */   {
/*  61 */     this.state = ((String)Objects.requireNonNull(state, "state is null"));
/*  62 */     this.queued = queued;
/*  63 */     this.scheduled = scheduled;
/*  64 */     this.nodes = nodes;
/*  65 */     this.totalSplits = totalSplits;
/*  66 */     this.queuedSplits = queuedSplits;
/*  67 */     this.runningSplits = runningSplits;
/*  68 */     this.completedSplits = completedSplits;
/*  69 */     this.userTimeMillis = userTimeMillis;
/*  70 */     this.cpuTimeMillis = cpuTimeMillis;
/*  71 */     this.wallTimeMillis = wallTimeMillis;
/*  72 */     this.processedRows = processedRows;
/*  73 */     this.processedBytes = processedBytes;
/*  74 */     this.rootStage = rootStage;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   @JsonProperty
/*     */   public String getState()
/*     */   {
/*  81 */     return this.state;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public boolean isQueued()
/*     */   {
/*  87 */     return this.queued;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public boolean isScheduled()
/*     */   {
/*  93 */     return this.scheduled;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public int getNodes()
/*     */   {
/*  99 */     return this.nodes;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public int getTotalSplits()
/*     */   {
/* 105 */     return this.totalSplits;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public int getQueuedSplits()
/*     */   {
/* 111 */     return this.queuedSplits;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public int getRunningSplits()
/*     */   {
/* 117 */     return this.runningSplits;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public int getCompletedSplits()
/*     */   {
/* 123 */     return this.completedSplits;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public long getUserTimeMillis()
/*     */   {
/* 129 */     return this.userTimeMillis;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public long getCpuTimeMillis()
/*     */   {
/* 135 */     return this.cpuTimeMillis;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public long getWallTimeMillis()
/*     */   {
/* 141 */     return this.wallTimeMillis;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public long getProcessedRows()
/*     */   {
/* 147 */     return this.processedRows;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public long getProcessedBytes()
/*     */   {
/* 153 */     return this.processedBytes;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   @JsonProperty
/*     */   public StageStats getRootStage()
/*     */   {
/* 160 */     return this.rootStage;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 166 */     return 
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
/* 181 */       MoreObjects.toStringHelper(this).add("state", this.state).add("queued", this.queued).add("scheduled", this.scheduled).add("nodes", this.nodes).add("totalSplits", this.totalSplits).add("queuedSplits", this.queuedSplits).add("runningSplits", this.runningSplits).add("completedSplits", this.completedSplits).add("userTimeMillis", this.userTimeMillis).add("cpuTimeMillis", this.cpuTimeMillis).add("wallTimeMillis", this.wallTimeMillis).add("processedRows", this.processedRows).add("processedBytes", this.processedBytes).add("rootStage", this.rootStage).toString();
/*     */   }
/*     */   
/*     */   public static Builder builder()
/*     */   {
/* 186 */     return new Builder(null);
/*     */   }
/*     */   
/*     */ 
/*     */   public static class Builder
/*     */   {
/*     */     private String state;
/*     */     
/*     */     private boolean queued;
/*     */     private boolean scheduled;
/*     */     private int nodes;
/*     */     private int totalSplits;
/*     */     private int queuedSplits;
/*     */     private int runningSplits;
/*     */     private int completedSplits;
/*     */     private long userTimeMillis;
/*     */     private long cpuTimeMillis;
/*     */     private long wallTimeMillis;
/*     */     private long processedRows;
/*     */     private long processedBytes;
/*     */     private StageStats rootStage;
/*     */     
/*     */     public Builder setState(String state)
/*     */     {
/* 210 */       this.state = ((String)Objects.requireNonNull(state, "state is null"));
/* 211 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setNodes(int nodes)
/*     */     {
/* 216 */       this.nodes = nodes;
/* 217 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setQueued(boolean queued)
/*     */     {
/* 222 */       this.queued = queued;
/* 223 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setScheduled(boolean scheduled)
/*     */     {
/* 228 */       this.scheduled = scheduled;
/* 229 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setTotalSplits(int totalSplits)
/*     */     {
/* 234 */       this.totalSplits = totalSplits;
/* 235 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setQueuedSplits(int queuedSplits)
/*     */     {
/* 240 */       this.queuedSplits = queuedSplits;
/* 241 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setRunningSplits(int runningSplits)
/*     */     {
/* 246 */       this.runningSplits = runningSplits;
/* 247 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setCompletedSplits(int completedSplits)
/*     */     {
/* 252 */       this.completedSplits = completedSplits;
/* 253 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setUserTimeMillis(long userTimeMillis)
/*     */     {
/* 258 */       this.userTimeMillis = userTimeMillis;
/* 259 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setCpuTimeMillis(long cpuTimeMillis)
/*     */     {
/* 264 */       this.cpuTimeMillis = cpuTimeMillis;
/* 265 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setWallTimeMillis(long wallTimeMillis)
/*     */     {
/* 270 */       this.wallTimeMillis = wallTimeMillis;
/* 271 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setProcessedRows(long processedRows)
/*     */     {
/* 276 */       this.processedRows = processedRows;
/* 277 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setProcessedBytes(long processedBytes)
/*     */     {
/* 282 */       this.processedBytes = processedBytes;
/* 283 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setRootStage(StageStats rootStage)
/*     */     {
/* 288 */       this.rootStage = rootStage;
/* 289 */       return this;
/*     */     }
/*     */     
/*     */     public StatementStats build()
/*     */     {
/* 294 */       return new StatementStats(this.state, this.queued, this.scheduled, this.nodes, this.totalSplits, this.queuedSplits, this.runningSplits, this.completedSplits, this.userTimeMillis, this.cpuTimeMillis, this.wallTimeMillis, this.processedRows, this.processedBytes, this.rootStage);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\client\StatementStats.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */