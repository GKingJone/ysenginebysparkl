/*    */ package com.mysql.fabric;
/*    */ 
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
/*    */ 
/*    */ public class ShardMappingFactory
/*    */ {
/*    */   public ShardMapping createShardMapping(int mappingId, ShardingType shardingType, String globalGroupName, Set<ShardTable> shardTables, Set<ShardIndex> shardIndices)
/*    */   {
/* 34 */     ShardMapping sm = null;
/* 35 */     switch (shardingType) {
/*    */     case RANGE: 
/* 37 */       sm = new RangeShardMapping(mappingId, shardingType, globalGroupName, shardTables, shardIndices);
/* 38 */       break;
/*    */     case HASH: 
/* 40 */       sm = new HashShardMapping(mappingId, shardingType, globalGroupName, shardTables, shardIndices);
/* 41 */       break;
/*    */     default: 
/* 43 */       throw new IllegalArgumentException("Invalid ShardingType");
/*    */     }
/* 45 */     return sm;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\ShardMappingFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */