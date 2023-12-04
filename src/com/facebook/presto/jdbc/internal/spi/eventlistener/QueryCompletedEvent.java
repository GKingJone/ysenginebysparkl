/*    */ package com.facebook.presto.jdbc.internal.spi.eventlistener;
/*    */ 
/*    */ import java.time.Instant;
/*    */ import java.util.Objects;
/*    */ import java.util.Optional;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class QueryCompletedEvent
/*    */ {
/*    */   private final QueryMetadata metadata;
/*    */   private final QueryStatistics statistics;
/*    */   private final QueryContext context;
/*    */   private final QueryIOMetadata ioMetadata;
/*    */   private final Optional<QueryFailureInfo> failureInfo;
/*    */   private final Instant createTime;
/*    */   private final Instant executionStartTime;
/*    */   private final Instant endTime;
/*    */   
/*    */   public QueryCompletedEvent(QueryMetadata metadata, QueryStatistics statistics, QueryContext context, QueryIOMetadata ioMetadata, Optional<QueryFailureInfo> failureInfo, Instant createTime, Instant executionStartTime, Instant endTime)
/*    */   {
/* 44 */     this.metadata = ((QueryMetadata)Objects.requireNonNull(metadata, "metadata is null"));
/* 45 */     this.statistics = ((QueryStatistics)Objects.requireNonNull(statistics, "statistics is null"));
/* 46 */     this.context = ((QueryContext)Objects.requireNonNull(context, "context is null"));
/* 47 */     this.ioMetadata = ((QueryIOMetadata)Objects.requireNonNull(ioMetadata, "ioMetadata is null"));
/* 48 */     this.failureInfo = ((Optional)Objects.requireNonNull(failureInfo, "failureInfo is null"));
/* 49 */     this.createTime = ((Instant)Objects.requireNonNull(createTime, "createTime is null"));
/* 50 */     this.executionStartTime = ((Instant)Objects.requireNonNull(executionStartTime, "executionStartTime is null"));
/* 51 */     this.endTime = ((Instant)Objects.requireNonNull(endTime, "endTime is null"));
/*    */   }
/*    */   
/*    */   public QueryMetadata getMetadata()
/*    */   {
/* 56 */     return this.metadata;
/*    */   }
/*    */   
/*    */   public QueryStatistics getStatistics()
/*    */   {
/* 61 */     return this.statistics;
/*    */   }
/*    */   
/*    */   public QueryContext getContext()
/*    */   {
/* 66 */     return this.context;
/*    */   }
/*    */   
/*    */   public QueryIOMetadata getIoMetadata()
/*    */   {
/* 71 */     return this.ioMetadata;
/*    */   }
/*    */   
/*    */   public Optional<QueryFailureInfo> getFailureInfo()
/*    */   {
/* 76 */     return this.failureInfo;
/*    */   }
/*    */   
/*    */   public Instant getCreateTime()
/*    */   {
/* 81 */     return this.createTime;
/*    */   }
/*    */   
/*    */   public Instant getExecutionStartTime()
/*    */   {
/* 86 */     return this.executionStartTime;
/*    */   }
/*    */   
/*    */   public Instant getEndTime()
/*    */   {
/* 91 */     return this.endTime;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\eventlistener\QueryCompletedEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */