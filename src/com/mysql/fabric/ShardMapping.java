/*    */ package com.mysql.fabric;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.Set;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class ShardMapping
/*    */ {
/*    */   private int mappingId;
/*    */   private ShardingType shardingType;
/*    */   private String globalGroupName;
/*    */   protected Set<ShardTable> shardTables;
/*    */   protected Set<ShardIndex> shardIndices;
/*    */   
/*    */   public ShardMapping(int mappingId, ShardingType shardingType, String globalGroupName, Set<ShardTable> shardTables, Set<ShardIndex> shardIndices)
/*    */   {
/* 40 */     this.mappingId = mappingId;
/* 41 */     this.shardingType = shardingType;
/* 42 */     this.globalGroupName = globalGroupName;
/* 43 */     this.shardTables = shardTables;
/* 44 */     this.shardIndices = shardIndices;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public String getGroupNameForKey(String key)
/*    */   {
/* 51 */     return getShardIndexForKey(key).getGroupName();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected abstract ShardIndex getShardIndexForKey(String paramString);
/*    */   
/*    */ 
/*    */ 
/*    */   public int getMappingId()
/*    */   {
/* 63 */     return this.mappingId;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public ShardingType getShardingType()
/*    */   {
/* 70 */     return this.shardingType;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public String getGlobalGroupName()
/*    */   {
/* 77 */     return this.globalGroupName;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public Set<ShardTable> getShardTables()
/*    */   {
/* 84 */     return Collections.unmodifiableSet(this.shardTables);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public Set<ShardIndex> getShardIndices()
/*    */   {
/* 91 */     return Collections.unmodifiableSet(this.shardIndices);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\ShardMapping.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */