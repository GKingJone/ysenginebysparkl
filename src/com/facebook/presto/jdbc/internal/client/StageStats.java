/*     */ package com.facebook.presto.jdbc.internal.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects.ToStringHelper;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableList;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
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
/*     */ public class StageStats
/*     */ {
/*     */   private final String stageId;
/*     */   private final String state;
/*     */   private final boolean done;
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
/*     */   private final List<StageStats> subStages;
/*     */   
/*     */   @JsonCreator
/*     */   public StageStats(@JsonProperty("stageId") String stageId, @JsonProperty("state") String state, @JsonProperty("done") boolean done, @JsonProperty("nodes") int nodes, @JsonProperty("totalSplits") int totalSplits, @JsonProperty("queuedSplits") int queuedSplits, @JsonProperty("runningSplits") int runningSplits, @JsonProperty("completedSplits") int completedSplits, @JsonProperty("userTimeMillis") long userTimeMillis, @JsonProperty("cpuTimeMillis") long cpuTimeMillis, @JsonProperty("wallTimeMillis") long wallTimeMillis, @JsonProperty("processedRows") long processedRows, @JsonProperty("processedBytes") long processedBytes, @JsonProperty("subStages") List<StageStats> subStages)
/*     */   {
/*  63 */     this.stageId = stageId;
/*  64 */     this.state = ((String)Objects.requireNonNull(state, "state is null"));
/*  65 */     this.done = done;
/*  66 */     this.nodes = nodes;
/*  67 */     this.totalSplits = totalSplits;
/*  68 */     this.queuedSplits = queuedSplits;
/*  69 */     this.runningSplits = runningSplits;
/*  70 */     this.completedSplits = completedSplits;
/*  71 */     this.userTimeMillis = userTimeMillis;
/*  72 */     this.cpuTimeMillis = cpuTimeMillis;
/*  73 */     this.wallTimeMillis = wallTimeMillis;
/*  74 */     this.processedRows = processedRows;
/*  75 */     this.processedBytes = processedBytes;
/*  76 */     this.subStages = ImmutableList.copyOf((Collection)Objects.requireNonNull(subStages, "subStages is null"));
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public String getStageId()
/*     */   {
/*  82 */     return this.stageId;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   @JsonProperty
/*     */   public String getState()
/*     */   {
/*  89 */     return this.state;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public boolean isDone()
/*     */   {
/*  95 */     return this.done;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public int getNodes()
/*     */   {
/* 101 */     return this.nodes;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public int getTotalSplits()
/*     */   {
/* 107 */     return this.totalSplits;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public int getQueuedSplits()
/*     */   {
/* 113 */     return this.queuedSplits;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public int getRunningSplits()
/*     */   {
/* 119 */     return this.runningSplits;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public int getCompletedSplits()
/*     */   {
/* 125 */     return this.completedSplits;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public long getUserTimeMillis()
/*     */   {
/* 131 */     return this.userTimeMillis;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public long getCpuTimeMillis()
/*     */   {
/* 137 */     return this.cpuTimeMillis;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public long getWallTimeMillis()
/*     */   {
/* 143 */     return this.wallTimeMillis;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public long getProcessedRows()
/*     */   {
/* 149 */     return this.processedRows;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public long getProcessedBytes()
/*     */   {
/* 155 */     return this.processedBytes;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   @JsonProperty
/*     */   public List<StageStats> getSubStages()
/*     */   {
/* 162 */     return this.subStages;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 168 */     return 
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
/* 182 */       MoreObjects.toStringHelper(this).add("state", this.state).add("done", this.done).add("nodes", this.nodes).add("totalSplits", this.totalSplits).add("queuedSplits", this.queuedSplits).add("runningSplits", this.runningSplits).add("completedSplits", this.completedSplits).add("userTimeMillis", this.userTimeMillis).add("cpuTimeMillis", this.cpuTimeMillis).add("wallTimeMillis", this.wallTimeMillis).add("processedRows", this.processedRows).add("processedBytes", this.processedBytes).add("subStages", this.subStages).toString();
/*     */   }
/*     */   
/*     */   public static Builder builder()
/*     */   {
/* 187 */     return new Builder(null);
/*     */   }
/*     */   
/*     */ 
/*     */   public static class Builder
/*     */   {
/*     */     private String stageId;
/*     */     
/*     */     private String state;
/*     */     private boolean done;
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
/*     */     private List<StageStats> subStages;
/*     */     
/*     */     public Builder setStageId(String stageId)
/*     */     {
/* 211 */       this.stageId = ((String)Objects.requireNonNull(stageId, "stageId is null"));
/* 212 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setState(String state)
/*     */     {
/* 217 */       this.state = ((String)Objects.requireNonNull(state, "state is null"));
/* 218 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setDone(boolean done)
/*     */     {
/* 223 */       this.done = done;
/* 224 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setNodes(int nodes)
/*     */     {
/* 229 */       this.nodes = nodes;
/* 230 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setTotalSplits(int totalSplits)
/*     */     {
/* 235 */       this.totalSplits = totalSplits;
/* 236 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setQueuedSplits(int queuedSplits)
/*     */     {
/* 241 */       this.queuedSplits = queuedSplits;
/* 242 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setRunningSplits(int runningSplits)
/*     */     {
/* 247 */       this.runningSplits = runningSplits;
/* 248 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setCompletedSplits(int completedSplits)
/*     */     {
/* 253 */       this.completedSplits = completedSplits;
/* 254 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setUserTimeMillis(long userTimeMillis)
/*     */     {
/* 259 */       this.userTimeMillis = userTimeMillis;
/* 260 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setCpuTimeMillis(long cpuTimeMillis)
/*     */     {
/* 265 */       this.cpuTimeMillis = cpuTimeMillis;
/* 266 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setWallTimeMillis(long wallTimeMillis)
/*     */     {
/* 271 */       this.wallTimeMillis = wallTimeMillis;
/* 272 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setProcessedRows(long processedRows)
/*     */     {
/* 277 */       this.processedRows = processedRows;
/* 278 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setProcessedBytes(long processedBytes)
/*     */     {
/* 283 */       this.processedBytes = processedBytes;
/* 284 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setSubStages(List<StageStats> subStages)
/*     */     {
/* 289 */       this.subStages = ImmutableList.copyOf((Collection)Objects.requireNonNull(subStages, "subStages is null"));
/* 290 */       return this;
/*     */     }
/*     */     
/*     */     public StageStats build()
/*     */     {
/* 295 */       return new StageStats(this.stageId, this.state, this.done, this.nodes, this.totalSplits, this.queuedSplits, this.runningSplits, this.completedSplits, this.userTimeMillis, this.cpuTimeMillis, this.wallTimeMillis, this.processedRows, this.processedBytes, this.subStages);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\client\StageStats.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */