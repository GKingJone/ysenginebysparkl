/*    */ package com.facebook.presto.jdbc.internal.jol.util;
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
/*    */ public class MathUtil
/*    */ {
/*    */   public static int log2p(int x)
/*    */   {
/* 30 */     int r = 0;
/* 31 */     while (x >>= 1 != 0)
/* 32 */       r++;
/* 33 */     return r;
/*    */   }
/*    */   
/*    */   public static int minDiff(int... offs) {
/* 37 */     int min = Integer.MAX_VALUE;
/* 38 */     for (int o1 : offs) {
/* 39 */       for (int o2 : offs) {
/* 40 */         if (o1 != o2) {
/* 41 */           min = Math.min(min, Math.abs(o1 - o2));
/*    */         }
/*    */       }
/*    */     }
/* 45 */     return min;
/*    */   }
/*    */   
/*    */   public static long gcd(long a, long b) {
/* 49 */     while (b > 0L) {
/* 50 */       long temp = b;
/* 51 */       b = a % b;
/* 52 */       a = temp;
/*    */     }
/* 54 */     return a;
/*    */   }
/*    */   
/*    */   public static int pow2(int power) {
/* 58 */     int p = 1;
/* 59 */     for (int i = 0; i < power; i++) {
/* 60 */       p *= 2;
/*    */     }
/* 62 */     return p;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jol\util\MathUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */