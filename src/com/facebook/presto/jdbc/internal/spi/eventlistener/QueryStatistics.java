/*     */ package com.facebook.presto.jdbc.internal.spi.eventlistener;
/*     */ 
/*     */ import java.time.Duration;
/*     */ import java.util.Objects;
/*     */ import java.util.Optional;
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
/*     */ public class QueryStatistics
/*     */ {
/*     */   private final Duration cpuTime;
/*     */   private final Duration wallTime;
/*     */   private final Duration queuedTime;
/*     */   private final Optional<Duration> analysisTime;
/*     */   private final Optional<Duration> distributedPlanningTime;
/*     */   private final long peakMemoryBytes;
/*     */   private final long totalBytes;
/*     */   private final long totalRows;
/*     */   private final int completedSplits;
/*     */   private final boolean complete;
/*     */   
/*     */   public QueryStatistics(Duration cpuTime, Duration wallTime, Duration queuedTime, Optional<Duration> analysisTime, Optional<Duration> distributedPlanningTime, long peakMemoryBytes, long totalBytes, long totalRows, int completedSplits, boolean complete)
/*     */   {
/*  49 */     this.cpuTime = ((Duration)Objects.requireNonNull(cpuTime, "cpuTime is null"));
/*  50 */     this.wallTime = ((Duration)Objects.requireNonNull(wallTime, "wallTime is null"));
/*  51 */     this.queuedTime = ((Duration)Objects.requireNonNull(queuedTime, "queuedTime is null"));
/*  52 */     this.analysisTime = ((Optional)Objects.requireNonNull(analysisTime, "analysisTime is null"));
/*  53 */     this.distributedPlanningTime = ((Optional)Objects.requireNonNull(distributedPlanningTime, "distributedPlanningTime is null"));
/*  54 */     this.peakMemoryBytes = ((Long)Objects.requireNonNull(Long.valueOf(peakMemoryBytes), "peakMemoryBytes is null")).longValue();
/*  55 */     this.totalBytes = ((Long)Objects.requireNonNull(Long.valueOf(totalBytes), "totalBytes is null")).longValue();
/*  56 */     this.totalRows = ((Long)Objects.requireNonNull(Long.valueOf(totalRows), "totalRows is null")).longValue();
/*  57 */     this.completedSplits = ((Integer)Objects.requireNonNull(Integer.valueOf(completedSplits), "completedSplits is null")).intValue();
/*  58 */     this.complete = complete;
/*     */   }
/*     */   
/*     */   public Duration getCpuTime()
/*     */   {
/*  63 */     return this.cpuTime;
/*     */   }
/*     */   
/*     */   public Duration getWallTime()
/*     */   {
/*  68 */     return this.wallTime;
/*     */   }
/*     */   
/*     */   public Duration getQueuedTime()
/*     */   {
/*  73 */     return this.queuedTime;
/*     */   }
/*     */   
/*     */   public Optional<Duration> getAnalysisTime()
/*     */   {
/*  78 */     return this.analysisTime;
/*     */   }
/*     */   
/*     */   public Optional<Duration> getDistributedPlanningTime()
/*     */   {
/*  83 */     return this.distributedPlanningTime;
/*     */   }
/*     */   
/*     */   public long getPeakMemoryBytes()
/*     */   {
/*  88 */     return this.peakMemoryBytes;
/*     */   }
/*     */   
/*     */   public long getTotalBytes()
/*     */   {
/*  93 */     return this.totalBytes;
/*     */   }
/*     */   
/*     */   public long getTotalRows()
/*     */   {
/*  98 */     return this.totalRows;
/*     */   }
/*     */   
/*     */   public int getCompletedSplits()
/*     */   {
/* 103 */     return this.completedSplits;
/*     */   }
/*     */   
/*     */   public boolean isComplete()
/*     */   {
/* 108 */     return this.complete;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\eventlistener\QueryStatistics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */