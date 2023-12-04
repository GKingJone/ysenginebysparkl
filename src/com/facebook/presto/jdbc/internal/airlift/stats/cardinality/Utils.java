/*    */ package com.facebook.presto.jdbc.internal.airlift.stats.cardinality;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
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
/*    */ final class Utils
/*    */ {
/*    */   public static double alpha(int indexBitLength)
/*    */   {
/* 26 */     switch (indexBitLength) {
/*    */     case 4: 
/* 28 */       return 0.673D;
/*    */     case 5: 
/* 30 */       return 0.697D;
/*    */     case 6: 
/* 32 */       return 0.709D;
/*    */     }
/* 34 */     return 0.7213D / (1.0D + 1.079D / numberOfBuckets(indexBitLength));
/*    */   }
/*    */   
/*    */ 
/*    */   public static boolean isPowerOf2(long value)
/*    */   {
/* 40 */     Preconditions.checkArgument(value > 0L, "value must be positive");
/* 41 */     return (value & value - 1L) == 0L;
/*    */   }
/*    */   
/*    */   public static int indexBitLength(int numberOfBuckets)
/*    */   {
/* 46 */     Preconditions.checkArgument(isPowerOf2(numberOfBuckets), "numberOfBuckets must be a power of 2, actual: %s", new Object[] { Integer.valueOf(numberOfBuckets) });
/* 47 */     return (int)(Math.log(numberOfBuckets) / Math.log(2.0D));
/*    */   }
/*    */   
/*    */   public static int numberOfBuckets(int indexBitLength)
/*    */   {
/* 52 */     return 1 << indexBitLength;
/*    */   }
/*    */   
/*    */   public static int computeIndex(long hash, int indexBitLength)
/*    */   {
/* 57 */     return (int)(hash >>> 64 - indexBitLength);
/*    */   }
/*    */   
/*    */   public static int numberOfLeadingZeros(long hash, int indexBitLength)
/*    */   {
/* 62 */     long value = hash << indexBitLength | 1L << indexBitLength - 1;
/* 63 */     return Long.numberOfLeadingZeros(value);
/*    */   }
/*    */   
/*    */   public static int computeValue(long hash, int indexBitLength)
/*    */   {
/* 68 */     return numberOfLeadingZeros(hash, indexBitLength) + 1;
/*    */   }
/*    */   
/*    */   public static double linearCounting(int zeroBuckets, int totalBuckets)
/*    */   {
/* 73 */     return totalBuckets * Math.log(totalBuckets * 1.0D / zeroBuckets);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\stats\cardinality\Utils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */