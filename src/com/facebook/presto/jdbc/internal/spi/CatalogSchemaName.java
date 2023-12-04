/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
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
/*    */ public final class CatalogSchemaName
/*    */ {
/*    */   private final String catalogName;
/*    */   private final String schemaName;
/*    */   
/*    */   public CatalogSchemaName(String catalogName, String schemaName)
/*    */   {
/* 29 */     this.catalogName = ((String)Objects.requireNonNull(catalogName, "catalogName is null")).toLowerCase(Locale.ENGLISH);
/* 30 */     this.schemaName = ((String)Objects.requireNonNull(schemaName, "schemaName is null")).toLowerCase(Locale.ENGLISH);
/*    */   }
/*    */   
/*    */   public String getCatalogName()
/*    */   {
/* 35 */     return this.catalogName;
/*    */   }
/*    */   
/*    */   public String getSchemaName()
/*    */   {
/* 40 */     return this.schemaName;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 46 */     if (this == obj) {
/* 47 */       return true;
/*    */     }
/* 49 */     if ((obj == null) || (getClass() != obj.getClass())) {
/* 50 */       return false;
/*    */     }
/* 52 */     CatalogSchemaName that = (CatalogSchemaName)obj;
/* 53 */     return (Objects.equals(this.catalogName, that.catalogName)) && 
/* 54 */       (Objects.equals(this.schemaName, that.schemaName));
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 60 */     return Objects.hash(new Object[] { this.catalogName, this.schemaName });
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 66 */     return this.catalogName + '.' + this.schemaName;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\CatalogSchemaName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */