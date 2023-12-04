/*     */ package com.facebook.presto.jdbc.internal.spi.eventlistener;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class QueryContext
/*     */ {
/*     */   private final String user;
/*     */   private final Optional<String> principal;
/*     */   private final Optional<String> remoteClientAddress;
/*     */   private final Optional<String> userAgent;
/*     */   private final Optional<String> source;
/*     */   private final Optional<String> catalog;
/*     */   private final Optional<String> schema;
/*     */   private final Map<String, String> sessionProperties;
/*     */   private final String serverAddress;
/*     */   private final String serverVersion;
/*     */   private final String environment;
/*     */   
/*     */   public QueryContext(String user, Optional<String> principal, Optional<String> remoteClientAddress, Optional<String> userAgent, Optional<String> source, Optional<String> catalog, Optional<String> schema, Map<String, String> sessionProperties, String serverAddress, String serverVersion, String environment)
/*     */   {
/*  54 */     this.user = ((String)Objects.requireNonNull(user, "user is null"));
/*  55 */     this.principal = ((Optional)Objects.requireNonNull(principal, "principal is null"));
/*  56 */     this.remoteClientAddress = ((Optional)Objects.requireNonNull(remoteClientAddress, "remoteClientAddress is null"));
/*  57 */     this.userAgent = ((Optional)Objects.requireNonNull(userAgent, "userAgent is null"));
/*  58 */     this.source = ((Optional)Objects.requireNonNull(source, "source is null"));
/*  59 */     this.catalog = ((Optional)Objects.requireNonNull(catalog, "catalog is null"));
/*  60 */     this.schema = ((Optional)Objects.requireNonNull(schema, "schema is null"));
/*  61 */     this.sessionProperties = ((Map)Objects.requireNonNull(sessionProperties, "sessionProperties is null"));
/*  62 */     this.serverAddress = ((String)Objects.requireNonNull(serverAddress, "serverAddress is null"));
/*  63 */     this.serverVersion = ((String)Objects.requireNonNull(serverVersion, "serverVersion is null"));
/*  64 */     this.environment = ((String)Objects.requireNonNull(environment, "environment is null"));
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public String getUser()
/*     */   {
/*  70 */     return this.user;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public Optional<String> getPrincipal()
/*     */   {
/*  76 */     return this.principal;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public Optional<String> getRemoteClientAddress()
/*     */   {
/*  82 */     return this.remoteClientAddress;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public Optional<String> getUserAgent()
/*     */   {
/*  88 */     return this.userAgent;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public Optional<String> getSource()
/*     */   {
/*  94 */     return this.source;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public Optional<String> getCatalog()
/*     */   {
/* 100 */     return this.catalog;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public Optional<String> getSchema()
/*     */   {
/* 106 */     return this.schema;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public Map<String, String> getSessionProperties()
/*     */   {
/* 112 */     return this.sessionProperties;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public String getServerAddress()
/*     */   {
/* 118 */     return this.serverAddress;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public String getServerVersion()
/*     */   {
/* 124 */     return this.serverVersion;
/*     */   }
/*     */   
/*     */   @JsonProperty
/*     */   public String getEnvironment()
/*     */   {
/* 130 */     return this.environment;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\eventlistener\QueryContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */