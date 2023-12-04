/*     */ package com.facebook.presto.jdbc.internal.client;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects.ToStringHelper;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
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
/*     */ @Immutable
/*     */ public class QueryError
/*     */ {
/*     */   private final String message;
/*     */   private final String sqlState;
/*     */   private final int errorCode;
/*     */   private final String errorName;
/*     */   private final String errorType;
/*     */   private final ErrorLocation errorLocation;
/*     */   private final FailureInfo failureInfo;
/*     */   
/*     */   @JsonCreator
/*     */   public QueryError(@JsonProperty("message") String message, @JsonProperty("sqlState") String sqlState, @JsonProperty("errorCode") int errorCode, @JsonProperty("errorName") String errorName, @JsonProperty("errorType") String errorType, @JsonProperty("errorLocation") ErrorLocation errorLocation, @JsonProperty("failureInfo") FailureInfo failureInfo)
/*     */   {
/*  46 */     this.message = message;
/*  47 */     this.sqlState = sqlState;
/*  48 */     this.errorCode = errorCode;
/*  49 */     this.errorName = errorName;
/*  50 */     this.errorType = errorType;
/*  51 */     this.errorLocation = errorLocation;
/*  52 */     this.failureInfo = failureInfo;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   @JsonProperty
/*     */   public String getMessage()
/*     */   {
/*  59 */     return this.message;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   @JsonProperty
/*     */   public String getSqlState()
/*     */   {
/*  66 */     return this.sqlState;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public int getErrorCode()
/*     */   {
/*  72 */     return this.errorCode;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   @JsonProperty
/*     */   public String getErrorName()
/*     */   {
/*  79 */     return this.errorName;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   @JsonProperty
/*     */   public String getErrorType()
/*     */   {
/*  86 */     return this.errorType;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   @JsonProperty
/*     */   public ErrorLocation getErrorLocation()
/*     */   {
/*  93 */     return this.errorLocation;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   @JsonProperty
/*     */   public FailureInfo getFailureInfo()
/*     */   {
/* 100 */     return this.failureInfo;
/*     */   }
/*     */   
/*     */ 
/*     */   public String toString()
/*     */   {
/* 106 */     return 
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 114 */       MoreObjects.toStringHelper(this).add("message", this.message).add("sqlState", this.sqlState).add("errorCode", this.errorCode).add("errorName", this.errorName).add("errorType", this.errorType).add("errorLocation", this.errorLocation).add("failureInfo", this.failureInfo).toString();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\client\QueryError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */