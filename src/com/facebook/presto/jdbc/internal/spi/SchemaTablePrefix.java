/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
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
/*    */ public class SchemaTablePrefix
/*    */ {
/*    */   private final String schemaName;
/*    */   private final String tableName;
/*    */   
/*    */   public SchemaTablePrefix()
/*    */   {
/* 29 */     this.schemaName = null;
/* 30 */     this.tableName = null;
/*    */   }
/*    */   
/*    */   public SchemaTablePrefix(String schemaName)
/*    */   {
/* 35 */     this.schemaName = SchemaUtil.checkNotEmpty(schemaName, "schemaName");
/* 36 */     this.tableName = null;
/*    */   }
/*    */   
/*    */   public SchemaTablePrefix(String schemaName, String tableName)
/*    */   {
/* 41 */     this.schemaName = SchemaUtil.checkNotEmpty(schemaName, "schemaName");
/* 42 */     this.tableName = SchemaUtil.checkNotEmpty(tableName, "tableName");
/*    */   }
/*    */   
/*    */   public String getSchemaName()
/*    */   {
/* 47 */     return this.schemaName;
/*    */   }
/*    */   
/*    */   public String getTableName()
/*    */   {
/* 52 */     return this.tableName;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean matches(SchemaTableName schemaTableName)
/*    */   {
/* 58 */     if (this.schemaName == null) {
/* 59 */       return true;
/*    */     }
/*    */     
/* 62 */     if (!this.schemaName.equals(schemaTableName.getSchemaName())) {
/* 63 */       return false;
/*    */     }
/*    */     
/* 66 */     return (this.tableName == null) || (this.tableName.equals(schemaTableName.getTableName()));
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 72 */     return Objects.hash(new Object[] { this.schemaName, this.tableName });
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 78 */     if (this == obj) {
/* 79 */       return true;
/*    */     }
/* 81 */     if ((obj == null) || (getClass() != obj.getClass())) {
/* 82 */       return false;
/*    */     }
/* 84 */     SchemaTablePrefix other = (SchemaTablePrefix)obj;
/* 85 */     return (Objects.equals(this.schemaName, other.schemaName)) && 
/* 86 */       (Objects.equals(this.tableName, other.tableName));
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 92 */     return (this.schemaName == null ? "*" : this.schemaName) + '.' + (this.tableName == null ? "*" : this.tableName);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\SchemaTablePrefix.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */