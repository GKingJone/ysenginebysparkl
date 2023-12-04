/*    */ package com.mysql.fabric;
/*    */ 
/*    */ import java.util.Comparator;
/*    */ import java.util.Set;
/*    */ import java.util.TreeSet;
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
/*    */ 
/*    */ public class RangeShardMapping
/*    */   extends ShardMapping
/*    */ {
/*    */   private static class RangeShardIndexSorter
/*    */     implements Comparator<ShardIndex>
/*    */   {
/*    */     public int compare(ShardIndex i1, ShardIndex i2)
/*    */     {
/* 41 */       Integer bound1 = Integer.valueOf(Integer.parseInt(i1.getBound()));
/* 42 */       Integer bound2 = Integer.valueOf(Integer.parseInt(i2.getBound()));
/* 43 */       return bound2.compareTo(bound1);
/*    */     }
/*    */     
/*    */ 
/* 47 */     public static final RangeShardIndexSorter instance = new RangeShardIndexSorter();
/*    */   }
/*    */   
/*    */   public RangeShardMapping(int mappingId, ShardingType shardingType, String globalGroupName, Set<ShardTable> shardTables, Set<ShardIndex> shardIndices)
/*    */   {
/* 52 */     super(mappingId, shardingType, globalGroupName, shardTables, new TreeSet(RangeShardIndexSorter.instance));
/* 53 */     this.shardIndices.addAll(shardIndices);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected ShardIndex getShardIndexForKey(String stringKey)
/*    */   {
/* 63 */     Integer key = Integer.valueOf(-1);
/* 64 */     key = Integer.valueOf(Integer.parseInt(stringKey));
/* 65 */     for (ShardIndex i : this.shardIndices) {
/* 66 */       Integer lowerBound = Integer.valueOf(i.getBound());
/* 67 */       if (key.intValue() >= lowerBound.intValue()) {
/* 68 */         return i;
/*    */       }
/*    */     }
/* 71 */     return null;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\RangeShardMapping.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */