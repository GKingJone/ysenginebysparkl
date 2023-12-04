/*    */ package com.mysql.fabric;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ShardIndex
/*    */ {
/*    */   private String bound;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private Integer shardId;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private String groupName;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ShardIndex(String bound, Integer shardId, String groupName)
/*    */   {
/* 36 */     this.bound = bound;
/* 37 */     this.shardId = shardId;
/* 38 */     this.groupName = groupName;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getBound()
/*    */   {
/* 46 */     return this.bound;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public Integer getShardId()
/*    */   {
/* 53 */     return this.shardId;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public String getGroupName()
/*    */   {
/* 60 */     return this.groupName;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\ShardIndex.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */