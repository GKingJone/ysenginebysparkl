/*    */ package com.facebook.presto.jdbc.internal.spi.eventlistener;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
/*    */ import java.util.Objects;
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
/*    */ public class QueryOutputMetadata
/*    */ {
/*    */   private final String connectorId;
/*    */   private final String schema;
/*    */   private final String table;
/*    */   
/*    */   public QueryOutputMetadata(String connectorId, String schema, String table)
/*    */   {
/* 29 */     this.connectorId = ((String)Objects.requireNonNull(connectorId, "connectorId is null"));
/* 30 */     this.schema = ((String)Objects.requireNonNull(schema, "schema is null"));
/* 31 */     this.table = ((String)Objects.requireNonNull(table, "table is null"));
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public String getConnectorId()
/*    */   {
/* 37 */     return this.connectorId;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public String getSchema()
/*    */   {
/* 43 */     return this.schema;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public String getTable()
/*    */   {
/* 49 */     return this.table;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\eventlistener\QueryOutputMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */