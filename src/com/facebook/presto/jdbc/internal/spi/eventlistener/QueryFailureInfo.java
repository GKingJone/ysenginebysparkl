/*    */ package com.facebook.presto.jdbc.internal.spi.eventlistener;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.spi.ErrorCode;
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
/*    */ public class QueryFailureInfo
/*    */ {
/*    */   private final ErrorCode errorCode;
/*    */   private final Optional<String> failureType;
/*    */   private final Optional<String> failureMessage;
/*    */   private final Optional<String> failureTask;
/*    */   private final Optional<String> failureHost;
/*    */   private final String failuresJson;
/*    */   
/*    */   public QueryFailureInfo(ErrorCode errorCode, Optional<String> failureType, Optional<String> failureMessage, Optional<String> failureTask, Optional<String> failureHost, String failuresJson)
/*    */   {
/* 40 */     this.errorCode = ((ErrorCode)Objects.requireNonNull(errorCode, "errorCode is null"));
/* 41 */     this.failureType = ((Optional)Objects.requireNonNull(failureType, "failureType is null"));
/* 42 */     this.failureMessage = ((Optional)Objects.requireNonNull(failureMessage, "failureMessage is null"));
/* 43 */     this.failureTask = ((Optional)Objects.requireNonNull(failureTask, "failureTask is null"));
/* 44 */     this.failureHost = ((Optional)Objects.requireNonNull(failureHost, "failureHost is null"));
/* 45 */     this.failuresJson = ((String)Objects.requireNonNull(failuresJson, "failuresJson is null"));
/*    */   }
/*    */   
/*    */   public ErrorCode getErrorCode()
/*    */   {
/* 50 */     return this.errorCode;
/*    */   }
/*    */   
/*    */   public Optional<String> getFailureType()
/*    */   {
/* 55 */     return this.failureType;
/*    */   }
/*    */   
/*    */   public Optional<String> getFailureMessage()
/*    */   {
/* 60 */     return this.failureMessage;
/*    */   }
/*    */   
/*    */   public Optional<String> getFailureTask()
/*    */   {
/* 65 */     return this.failureTask;
/*    */   }
/*    */   
/*    */   public Optional<String> getFailureHost()
/*    */   {
/* 70 */     return this.failureHost;
/*    */   }
/*    */   
/*    */   public String getFailuresJson()
/*    */   {
/* 75 */     return this.failuresJson;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\eventlistener\QueryFailureInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */