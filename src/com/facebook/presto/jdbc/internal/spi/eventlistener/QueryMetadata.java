/*    */ package com.facebook.presto.jdbc.internal.spi.eventlistener;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
/*    */ import java.net.URI;
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
/*    */ 
/*    */ public class QueryMetadata
/*    */ {
/*    */   private final String queryId;
/*    */   private final Optional<String> transactionId;
/*    */   private final String query;
/*    */   private final String queryState;
/*    */   private final URI uri;
/*    */   private final Optional<String> payload;
/*    */   
/*    */   public QueryMetadata(String queryId, Optional<String> transactionId, String query, String queryState, URI uri, Optional<String> payload)
/*    */   {
/* 44 */     this.queryId = ((String)Objects.requireNonNull(queryId, "queryId is null"));
/* 45 */     this.transactionId = ((Optional)Objects.requireNonNull(transactionId, "transactionId is null"));
/* 46 */     this.query = ((String)Objects.requireNonNull(query, "query is null"));
/* 47 */     this.queryState = ((String)Objects.requireNonNull(queryState, "queryState is null"));
/* 48 */     this.uri = ((URI)Objects.requireNonNull(uri, "uri is null"));
/* 49 */     this.payload = ((Optional)Objects.requireNonNull(payload, "payload is null"));
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public String getQueryId()
/*    */   {
/* 55 */     return this.queryId;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public Optional<String> getTransactionId()
/*    */   {
/* 61 */     return this.transactionId;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public String getQuery()
/*    */   {
/* 67 */     return this.query;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public String getQueryState()
/*    */   {
/* 73 */     return this.queryState;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public URI getUri()
/*    */   {
/* 79 */     return this.uri;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public Optional<String> getPayload()
/*    */   {
/* 85 */     return this.payload;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\eventlistener\QueryMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */