/*    */ package com.facebook.presto.jdbc.internal.spi.eventlistener;
/*    */ 
/*    */ import java.time.Duration;
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
/*    */ public class SplitStatistics
/*    */ {
/*    */   private final Duration cpuTime;
/*    */   private final Duration wallTime;
/*    */   private final Duration queuedTime;
/*    */   private final Duration userTime;
/*    */   private final Duration completedReadTime;
/*    */   private final long completedPositions;
/*    */   private final long completedDataSizeBytes;
/*    */   private final Optional<Duration> timeToFirstByte;
/*    */   private final Optional<Duration> timeToLastByte;
/*    */   
/*    */   public SplitStatistics(Duration cpuTime, Duration wallTime, Duration queuedTime, Duration userTime, Duration completedReadTime, long completedPositions, long completedDataSizeBytes, Optional<Duration> timeToFirstByte, Optional<Duration> timeToLastByte)
/*    */   {
/* 38 */     this.cpuTime = ((Duration)Objects.requireNonNull(cpuTime, "cpuTime is null"));
/* 39 */     this.wallTime = ((Duration)Objects.requireNonNull(wallTime, "wallTime is null"));
/* 40 */     this.queuedTime = ((Duration)Objects.requireNonNull(queuedTime, "queuedTime is null"));
/* 41 */     this.userTime = ((Duration)Objects.requireNonNull(userTime, "userTime is null"));
/* 42 */     this.completedReadTime = ((Duration)Objects.requireNonNull(completedReadTime, "completedReadTime is null"));
/* 43 */     this.completedPositions = completedPositions;
/* 44 */     this.completedDataSizeBytes = completedDataSizeBytes;
/* 45 */     this.timeToFirstByte = ((Optional)Objects.requireNonNull(timeToFirstByte, "timeToFirstByte is null"));
/* 46 */     this.timeToLastByte = ((Optional)Objects.requireNonNull(timeToLastByte, "timeToLastByte is null"));
/*    */   }
/*    */   
/*    */   public Duration getCpuTime()
/*    */   {
/* 51 */     return this.cpuTime;
/*    */   }
/*    */   
/*    */   public Duration getWallTime()
/*    */   {
/* 56 */     return this.wallTime;
/*    */   }
/*    */   
/*    */   public Duration getQueuedTime()
/*    */   {
/* 61 */     return this.queuedTime;
/*    */   }
/*    */   
/*    */   public Duration getUserTime()
/*    */   {
/* 66 */     return this.userTime;
/*    */   }
/*    */   
/*    */   public Duration getCompletedReadTime()
/*    */   {
/* 71 */     return this.completedReadTime;
/*    */   }
/*    */   
/*    */   public long getCompletedPositions()
/*    */   {
/* 76 */     return this.completedPositions;
/*    */   }
/*    */   
/*    */   public long getCompletedDataSizeBytes()
/*    */   {
/* 81 */     return this.completedDataSizeBytes;
/*    */   }
/*    */   
/*    */   public Optional<Duration> getTimeToFirstByte()
/*    */   {
/* 86 */     return this.timeToFirstByte;
/*    */   }
/*    */   
/*    */   public Optional<Duration> getTimeToLastByte()
/*    */   {
/* 91 */     return this.timeToLastByte;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\eventlistener\SplitStatistics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */