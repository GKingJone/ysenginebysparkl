/*    */ package com.facebook.presto.jdbc.internal.spi.eventlistener;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
/*    */ import java.util.List;
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
/*    */ public class QueryInputMetadata
/*    */ {
/*    */   private final String connectorId;
/*    */   private final String schema;
/*    */   private final String table;
/*    */   private final List<String> columns;
/*    */   private final Optional<Object> connectorInfo;
/*    */   
/*    */   public QueryInputMetadata(String connectorId, String schema, String table, List<String> columns, Optional<Object> connectorInfo)
/*    */   {
/* 34 */     this.connectorId = ((String)Objects.requireNonNull(connectorId, "connectorId is null"));
/* 35 */     this.schema = ((String)Objects.requireNonNull(schema, "schema is null"));
/* 36 */     this.table = ((String)Objects.requireNonNull(table, "table is null"));
/* 37 */     this.columns = ((List)Objects.requireNonNull(columns, "columns is null"));
/* 38 */     this.connectorInfo = ((Optional)Objects.requireNonNull(connectorInfo, "connectorInfo is null"));
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public String getConnectorId()
/*    */   {
/* 44 */     return this.connectorId;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public String getSchema()
/*    */   {
/* 50 */     return this.schema;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public String getTable()
/*    */   {
/* 56 */     return this.table;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public List<String> getColumns()
/*    */   {
/* 62 */     return this.columns;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public Optional<Object> getConnectorInfo()
/*    */   {
/* 68 */     return this.connectorInfo;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\eventlistener\QueryInputMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */