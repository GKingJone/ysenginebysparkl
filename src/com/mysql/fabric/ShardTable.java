/*    */ package com.mysql.fabric;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ShardTable
/*    */ {
/*    */   private String database;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private String table;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private String column;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ShardTable(String database, String table, String column)
/*    */   {
/* 35 */     this.database = database;
/* 36 */     this.table = table;
/* 37 */     this.column = column;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public String getDatabase()
/*    */   {
/* 44 */     return this.database;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public String getTable()
/*    */   {
/* 51 */     return this.table;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public String getColumn()
/*    */   {
/* 58 */     return this.column;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\ShardTable.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */