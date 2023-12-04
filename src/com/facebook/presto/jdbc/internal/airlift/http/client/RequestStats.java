/*     */ package com.facebook.presto.jdbc.internal.airlift.http.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.airlift.stats.CounterStat;
/*     */ import com.facebook.presto.jdbc.internal.airlift.stats.DistributionStat;
/*     */ import com.facebook.presto.jdbc.internal.airlift.stats.TimeStat;
/*     */ import com.facebook.presto.jdbc.internal.airlift.units.Duration;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.inject.Inject;
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
/*     */ public class RequestStats
/*     */ {
/*  33 */   private final CounterStat allResponse = new CounterStat();
/*  34 */   private final CounterStat informationalResponse = new CounterStat();
/*  35 */   private final CounterStat successfulResponse = new CounterStat();
/*  36 */   private final CounterStat redirectionResponse = new CounterStat();
/*  37 */   private final CounterStat clientErrorResponse = new CounterStat();
/*  38 */   private final CounterStat serverErrorResponse = new CounterStat();
/*     */   
/*  40 */   private final CounterStat requestFailed = new CounterStat();
/*  41 */   private final CounterStat requestCanceled = new CounterStat();
/*     */   
/*  43 */   private final TimeStat requestTime = new TimeStat();
/*  44 */   private final TimeStat responseTime = new TimeStat();
/*  45 */   private final DistributionStat readBytes = new DistributionStat();
/*  46 */   private final DistributionStat writtenBytes = new DistributionStat();
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
/*     */   public void recordResponseReceived(String method, int responseCode, long requestSizeInBytes, long responseSizeInBytes, Duration requestProcessingTime, Duration responseProcessingTime)
/*     */   {
/*  60 */     this.requestTime.add(requestProcessingTime);
/*  61 */     this.responseTime.add(responseProcessingTime);
/*  62 */     this.readBytes.add(responseSizeInBytes);
/*  63 */     this.writtenBytes.add(requestSizeInBytes);
/*     */     
/*  65 */     this.allResponse.update(1L);
/*  66 */     switch (HttpStatus.familyForStatusCode(responseCode)) {
/*     */     case INFORMATIONAL: 
/*  68 */       this.informationalResponse.update(1L);
/*  69 */       break;
/*     */     case SUCCESSFUL: 
/*  71 */       this.successfulResponse.update(1L);
/*  72 */       break;
/*     */     case REDIRECTION: 
/*  74 */       this.redirectionResponse.update(1L);
/*  75 */       break;
/*     */     case CLIENT_ERROR: 
/*  77 */       this.clientErrorResponse.update(1L);
/*  78 */       break;
/*     */     case SERVER_ERROR: 
/*  80 */       this.serverErrorResponse.update(1L);
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */   public void recordRequestFailed()
/*     */   {
/*  87 */     this.requestFailed.update(1L);
/*     */   }
/*     */   
/*     */   public void recordRequestCanceled()
/*     */   {
/*  92 */     this.requestCanceled.update(1L);
/*     */   }
/*     */   
/*     */   @Managed
/*     */   @Nested
/*     */   public CounterStat getAllResponse()
/*     */   {
/*  99 */     return this.allResponse;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   @Nested
/*     */   public CounterStat get1xxResponse()
/*     */   {
/* 106 */     return this.informationalResponse;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   @Nested
/*     */   public CounterStat get2xxResponse()
/*     */   {
/* 113 */     return this.successfulResponse;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   @Nested
/*     */   public CounterStat get3xxResponse()
/*     */   {
/* 120 */     return this.redirectionResponse;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   @Nested
/*     */   public CounterStat get4xxResponse()
/*     */   {
/* 127 */     return this.clientErrorResponse;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   @Nested
/*     */   public CounterStat get5xxResponse()
/*     */   {
/* 134 */     return this.serverErrorResponse;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   @Nested
/*     */   public CounterStat getRequestFailed()
/*     */   {
/* 141 */     return this.requestFailed;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   @Nested
/*     */   public CounterStat getRequestCanceled()
/*     */   {
/* 148 */     return this.requestCanceled;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   @Nested
/*     */   public TimeStat getRequestTime()
/*     */   {
/* 155 */     return this.requestTime;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   @Nested
/*     */   public TimeStat getResponseTime()
/*     */   {
/* 162 */     return this.responseTime;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   @Nested
/*     */   public DistributionStat getReadBytes()
/*     */   {
/* 169 */     return this.readBytes;
/*     */   }
/*     */   
/*     */   @Managed
/*     */   @Nested
/*     */   public DistributionStat getWrittenBytes()
/*     */   {
/* 176 */     return this.writtenBytes;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\RequestStats.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */