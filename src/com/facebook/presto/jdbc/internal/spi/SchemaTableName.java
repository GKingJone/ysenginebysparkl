/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonValue;
/*    */ import java.util.Locale;
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
/*    */ public class SchemaTableName
/*    */ {
/*    */   private final String schemaName;
/*    */   private final String tableName;
/*    */   
/*    */   @JsonCreator
/*    */   public static SchemaTableName valueOf(String schemaTableName)
/*    */   {
/* 32 */     SchemaUtil.checkNotEmpty(schemaTableName, "schemaTableName");
/* 33 */     String[] parts = schemaTableName.split("\\.");
/* 34 */     if (parts.length != 2) {
/* 35 */       throw new IllegalArgumentException("Invalid schemaTableName " + schemaTableName);
/*    */     }
/* 37 */     return new SchemaTableName(parts[0], parts[1]);
/*    */   }
/*    */   
/*    */   public SchemaTableName(String schemaName, String tableName)
/*    */   {
/* 42 */     this.schemaName = SchemaUtil.checkNotEmpty(schemaName, "schemaName").toLowerCase(Locale.ENGLISH);
/* 43 */     this.tableName = SchemaUtil.checkNotEmpty(tableName, "tableName").toLowerCase(Locale.ENGLISH);
/*    */   }
/*    */   
/*    */   public String getSchemaName()
/*    */   {
/* 48 */     return this.schemaName;
/*    */   }
/*    */   
/*    */   public String getTableName()
/*    */   {
/* 53 */     return this.tableName;
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 59 */     return Objects.hash(new Object[] { this.schemaName, this.tableName });
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 65 */     if (this == obj) {
/* 66 */       return true;
/*    */     }
/* 68 */     if ((obj == null) || (getClass() != obj.getClass())) {
/* 69 */       return false;
/*    */     }
/* 71 */     SchemaTableName other = (SchemaTableName)obj;
/* 72 */     return (Objects.equals(this.schemaName, other.schemaName)) && 
/* 73 */       (Objects.equals(this.tableName, other.tableName));
/*    */   }
/*    */   
/*    */ 
/*    */   @JsonValue
/*    */   public String toString()
/*    */   {
/* 80 */     return this.schemaName + '.' + this.tableName;
/*    */   }
/*    */   
/*    */   public SchemaTablePrefix toSchemaTablePrefix()
/*    */   {
/* 85 */     return new SchemaTablePrefix(this.schemaName, this.tableName);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\SchemaTableName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */