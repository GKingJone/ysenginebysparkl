/*     */ package com.facebook.presto.jdbc.internal.spi.eventlistener;
/*     */ 
/*     */ import java.time.Instant;
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
/*     */ public class SplitCompletedEvent
/*     */ {
/*     */   private final String queryId;
/*     */   private final String stageId;
/*     */   private final String taskId;
/*     */   private final Instant createTime;
/*     */   private final Optional<Instant> startTime;
/*     */   private final Optional<Instant> endTime;
/*     */   private final SplitStatistics statistics;
/*     */   private final Optional<SplitFailureInfo> failureInfo;
/*     */   private final String payload;
/*     */   
/*     */   public SplitCompletedEvent(String queryId, String stageId, String taskId, Instant createTime, Optional<Instant> startTime, Optional<Instant> endTime, SplitStatistics statistics, Optional<SplitFailureInfo> failureInfo, String payload)
/*     */   {
/*  48 */     this.queryId = ((String)Objects.requireNonNull(queryId, "queryId is null"));
/*  49 */     this.stageId = ((String)Objects.requireNonNull(stageId, "stageId is null"));
/*  50 */     this.taskId = ((String)Objects.requireNonNull(taskId, "taskId is null"));
/*  51 */     this.createTime = ((Instant)Objects.requireNonNull(createTime, "createTime is null"));
/*  52 */     this.startTime = ((Optional)Objects.requireNonNull(startTime, "startTime is null"));
/*  53 */     this.endTime = ((Optional)Objects.requireNonNull(endTime, "endTime is null"));
/*  54 */     this.statistics = ((SplitStatistics)Objects.requireNonNull(statistics, "statistics is null"));
/*  55 */     this.failureInfo = ((Optional)Objects.requireNonNull(failureInfo, "failureInfo is null"));
/*  56 */     this.payload = ((String)Objects.requireNonNull(payload, "payload is null"));
/*     */   }
/*     */   
/*     */   public String getQueryId()
/*     */   {
/*  61 */     return this.queryId;
/*     */   }
/*     */   
/*     */   public String getStageId()
/*     */   {
/*  66 */     return this.stageId;
/*     */   }
/*     */   
/*     */   public String getTaskId()
/*     */   {
/*  71 */     return this.taskId;
/*     */   }
/*     */   
/*     */   public Instant getCreateTime()
/*     */   {
/*  76 */     return this.createTime;
/*     */   }
/*     */   
/*     */   public Optional<Instant> getStartTime()
/*     */   {
/*  81 */     return this.startTime;
/*     */   }
/*     */   
/*     */   public Optional<Instant> getEndTime()
/*     */   {
/*  86 */     return this.endTime;
/*     */   }
/*     */   
/*     */   public SplitStatistics getStatistics()
/*     */   {
/*  91 */     return this.statistics;
/*     */   }
/*     */   
/*     */   public Optional<SplitFailureInfo> getFailureInfo()
/*     */   {
/*  96 */     return this.failureInfo;
/*     */   }
/*     */   
/*     */   public String getPayload()
/*     */   {
/* 101 */     return this.payload;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\eventlistener\SplitCompletedEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */