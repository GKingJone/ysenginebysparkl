/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ColumnNotFoundException
/*    */   extends NotFoundException
/*    */ {
/*    */   private final SchemaTableName tableName;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   private final String columnName;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ColumnNotFoundException(SchemaTableName tableName, String columnName)
/*    */   {
/* 24 */     this(tableName, columnName, "Column " + columnName + " not found in table " + tableName);
/*    */   }
/*    */   
/*    */   public ColumnNotFoundException(SchemaTableName tableName, String columnName, String message)
/*    */   {
/* 29 */     super(message);
/* 30 */     if (tableName == null) {
/* 31 */       throw new NullPointerException("tableName is null");
/*    */     }
/* 33 */     if (columnName == null) {
/* 34 */       throw new NullPointerException("columnName is null");
/*    */     }
/* 36 */     this.tableName = tableName;
/* 37 */     this.columnName = columnName;
/*    */   }
/*    */   
/*    */   public ColumnNotFoundException(SchemaTableName tableName, String columnName, Throwable cause)
/*    */   {
/* 42 */     this(tableName, columnName, "Table " + tableName + " not found", cause);
/*    */   }
/*    */   
/*    */   public ColumnNotFoundException(SchemaTableName tableName, String columnName, String message, Throwable cause)
/*    */   {
/* 47 */     super(message, cause);
/* 48 */     if (tableName == null) {
/* 49 */       throw new NullPointerException("tableName is null");
/*    */     }
/* 51 */     if (columnName == null) {
/* 52 */       throw new NullPointerException("columnName is null");
/*    */     }
/* 54 */     this.tableName = tableName;
/* 55 */     this.columnName = columnName;
/*    */   }
/*    */   
/*    */   public SchemaTableName getTableName()
/*    */   {
/* 60 */     return this.tableName;
/*    */   }
/*    */   
/*    */   public String getColumnName()
/*    */   {
/* 65 */     return this.columnName;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\ColumnNotFoundException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */