/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SchemaNotFoundException
/*    */   extends NotFoundException
/*    */ {
/*    */   private final String schemaName;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public SchemaNotFoundException(String schemaName)
/*    */   {
/* 23 */     this(schemaName, "Schema " + schemaName + " not found");
/*    */   }
/*    */   
/*    */   public SchemaNotFoundException(String schemaName, String message)
/*    */   {
/* 28 */     super(message);
/* 29 */     if (schemaName == null) {
/* 30 */       throw new NullPointerException("schemaName is null");
/*    */     }
/* 32 */     this.schemaName = schemaName;
/*    */   }
/*    */   
/*    */   public SchemaNotFoundException(String schemaName, Throwable cause)
/*    */   {
/* 37 */     this(schemaName, "Schema " + schemaName + " not found", cause);
/*    */   }
/*    */   
/*    */   public SchemaNotFoundException(String schemaName, String message, Throwable cause)
/*    */   {
/* 42 */     super(message, cause);
/* 43 */     if (schemaName == null) {
/* 44 */       throw new NullPointerException("schemaName is null");
/*    */     }
/* 46 */     this.schemaName = schemaName;
/*    */   }
/*    */   
/*    */   public String getSchemaName()
/*    */   {
/* 51 */     return this.schemaName;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\SchemaNotFoundException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */