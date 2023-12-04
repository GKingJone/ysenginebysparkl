/*    */ package com.mysql.fabric;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import java.security.MessageDigest;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ import java.util.Comparator;
/*    */ import java.util.Iterator;
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
/*    */ public class HashShardMapping
/*    */   extends ShardMapping
/*    */ {
/*    */   private static final MessageDigest md5Hasher;
/*    */   
/*    */   private static class ReverseShardIndexSorter
/*    */     implements Comparator<ShardIndex>
/*    */   {
/*    */     public int compare(ShardIndex i1, ShardIndex i2)
/*    */     {
/* 39 */       return i2.getBound().compareTo(i1.getBound());
/*    */     }
/*    */     
/*    */ 
/* 43 */     public static final ReverseShardIndexSorter instance = new ReverseShardIndexSorter();
/*    */   }
/*    */   
/*    */   static
/*    */   {
/*    */     try
/*    */     {
/* 50 */       md5Hasher = MessageDigest.getInstance("MD5");
/*    */     } catch (NoSuchAlgorithmException ex) {
/* 52 */       throw new ExceptionInInitializerError(ex);
/*    */     }
/*    */   }
/*    */   
/*    */   public HashShardMapping(int mappingId, ShardingType shardingType, String globalGroupName, Set<ShardTable> shardTables, Set<ShardIndex> shardIndices) {
/* 57 */     super(mappingId, shardingType, globalGroupName, shardTables, new TreeSet(ReverseShardIndexSorter.instance));
/* 58 */     this.shardIndices.addAll(shardIndices);
/*    */   }
/*    */   
/*    */   protected ShardIndex getShardIndexForKey(String stringKey)
/*    */   {
/* 63 */     String hashedKey = new BigInteger(1, md5Hasher.digest(stringKey.getBytes())).toString(16).toUpperCase();
/*    */     
/*    */ 
/* 66 */     for (int i = 0; i < 32 - hashedKey.length(); i++) {
/* 67 */       hashedKey = "0" + hashedKey;
/*    */     }
/*    */     
/* 70 */     for (ShardIndex i : this.shardIndices) {
/* 71 */       if (i.getBound().compareTo(hashedKey) <= 0) {
/* 72 */         return i;
/*    */       }
/*    */     }
/*    */     
/*    */ 
/*    */ 
/* 78 */     return (ShardIndex)this.shardIndices.iterator().next();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\fabric\HashShardMapping.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */