/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonValue;
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
/*    */ 
/*    */ 
/*    */ public class CatalogSchemaTableName
/*    */ {
/*    */   private final String catalogName;
/*    */   private final SchemaTableName schemaTableName;
/*    */   
/*    */   @JsonCreator
/*    */   public static CatalogSchemaTableName valueOf(String catalogSchemaTableName)
/*    */   {
/* 32 */     SchemaUtil.checkNotEmpty(catalogSchemaTableName, "catalogSchemaTableName");
/* 33 */     String[] parts = catalogSchemaTableName.split("\\.");
/* 34 */     if (parts.length != 3) {
/* 35 */       throw new IllegalArgumentException("Invalid catalogSchemaTableName " + catalogSchemaTableName);
/*    */     }
/* 37 */     return new CatalogSchemaTableName(parts[0], parts[1], parts[2]);
/*    */   }
/*    */   
/*    */   public CatalogSchemaTableName(String catalogName, SchemaTableName schemaTableName)
/*    */   {
/* 42 */     this.catalogName = catalogName;
/* 43 */     this.schemaTableName = schemaTableName;
/*    */   }
/*    */   
/*    */   public CatalogSchemaTableName(String catalogName, String schemaName, String tableName)
/*    */   {
/* 48 */     this.catalogName = catalogName;
/* 49 */     this.schemaTableName = new SchemaTableName(schemaName, tableName);
/*    */   }
/*    */   
/*    */   public String getCatalogName()
/*    */   {
/* 54 */     return this.catalogName;
/*    */   }
/*    */   
/*    */   public SchemaTableName getSchemaTableName()
/*    */   {
/* 59 */     return this.schemaTableName;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 65 */     if (this == o) {
/* 66 */       return true;
/*    */     }
/* 68 */     if ((o == null) || (getClass() != o.getClass())) {
/* 69 */       return false;
/*    */     }
/* 71 */     CatalogSchemaTableName that = (CatalogSchemaTableName)o;
/* 72 */     return (Objects.equals(this.catalogName, that.catalogName)) && 
/* 73 */       (Objects.equals(this.schemaTableName, that.schemaTableName));
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 79 */     return Objects.hash(new Object[] { this.catalogName, this.schemaTableName });
/*    */   }
/*    */   
/*    */ 
/*    */   @JsonValue
/*    */   public String toString()
/*    */   {
/* 86 */     return this.catalogName + '.' + this.schemaTableName.toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\CatalogSchemaTableName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */