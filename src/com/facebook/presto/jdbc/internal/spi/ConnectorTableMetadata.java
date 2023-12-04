/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
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
/*    */ public class ConnectorTableMetadata
/*    */ {
/*    */   private final SchemaTableName table;
/*    */   private final List<ColumnMetadata> columns;
/*    */   private final Map<String, Object> properties;
/*    */   private final boolean sampled;
/*    */   
/*    */   public ConnectorTableMetadata(SchemaTableName table, List<ColumnMetadata> columns)
/*    */   {
/* 33 */     this(table, columns, Collections.emptyMap());
/*    */   }
/*    */   
/*    */   public ConnectorTableMetadata(SchemaTableName table, List<ColumnMetadata> columns, Map<String, Object> properties)
/*    */   {
/* 38 */     this(table, columns, properties, false);
/*    */   }
/*    */   
/*    */   public ConnectorTableMetadata(SchemaTableName table, List<ColumnMetadata> columns, Map<String, Object> properties, boolean sampled)
/*    */   {
/* 43 */     if (table == null) {
/* 44 */       throw new NullPointerException("table is null or empty");
/*    */     }
/* 46 */     if (columns == null) {
/* 47 */       throw new NullPointerException("columns is null");
/*    */     }
/*    */     
/* 50 */     this.table = table;
/* 51 */     this.columns = Collections.unmodifiableList(new ArrayList(columns));
/* 52 */     this.properties = Collections.unmodifiableMap(new LinkedHashMap(properties));
/* 53 */     this.sampled = sampled;
/*    */   }
/*    */   
/*    */   public boolean isSampled()
/*    */   {
/* 58 */     return this.sampled;
/*    */   }
/*    */   
/*    */   public SchemaTableName getTable()
/*    */   {
/* 63 */     return this.table;
/*    */   }
/*    */   
/*    */   public List<ColumnMetadata> getColumns()
/*    */   {
/* 68 */     return this.columns;
/*    */   }
/*    */   
/*    */   public Map<String, Object> getProperties()
/*    */   {
/* 73 */     return this.properties;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 79 */     StringBuilder sb = new StringBuilder("ConnectorTableMetadata{");
/* 80 */     sb.append("table=").append(this.table);
/* 81 */     sb.append(", columns=").append(this.columns);
/* 82 */     sb.append(", properties=").append(this.properties);
/* 83 */     sb.append('}');
/* 84 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\ConnectorTableMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */