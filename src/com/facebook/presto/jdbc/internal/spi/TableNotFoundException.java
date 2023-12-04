/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TableNotFoundException
/*    */   extends NotFoundException
/*    */ {
/*    */   private final SchemaTableName tableName;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public TableNotFoundException(SchemaTableName tableName)
/*    */   {
/* 25 */     this(tableName, String.format("Table '%s' not found", new Object[] { tableName }));
/*    */   }
/*    */   
/*    */   public TableNotFoundException(SchemaTableName tableName, String message)
/*    */   {
/* 30 */     super(message);
/* 31 */     if (tableName == null) {
/* 32 */       throw new NullPointerException("tableName is null");
/*    */     }
/* 34 */     this.tableName = tableName;
/*    */   }
/*    */   
/*    */   public TableNotFoundException(SchemaTableName tableName, Throwable cause)
/*    */   {
/* 39 */     this(tableName, String.format("Table '%s' not found", new Object[] { tableName }), cause);
/*    */   }
/*    */   
/*    */   public TableNotFoundException(SchemaTableName tableName, String message, Throwable cause)
/*    */   {
/* 44 */     super(message, cause);
/* 45 */     if (tableName == null) {
/* 46 */       throw new NullPointerException("tableName is null");
/*    */     }
/* 48 */     this.tableName = tableName;
/*    */   }
/*    */   
/*    */   public SchemaTableName getTableName()
/*    */   {
/* 53 */     return this.tableName;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\TableNotFoundException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */