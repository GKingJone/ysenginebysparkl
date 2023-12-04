/*    */ package com.facebook.presto.jdbc.internal.airlift.stats;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ExponentialDecay
/*    */ {
/*    */   public static double oneMinute()
/*    */   {
/* 16 */     return 1.0D / TimeUnit.MINUTES.toSeconds(1L);
/*    */   }
/*    */   
/*    */ 
/*    */   public static double fiveMinutes()
/*    */   {
/* 22 */     return 1.0D / TimeUnit.MINUTES.toSeconds(5L);
/*    */   }
/*    */   
/*    */ 
/*    */   public static double fifteenMinutes()
/*    */   {
/* 28 */     return 1.0D / TimeUnit.MINUTES.toSeconds(15L);
/*    */   }
/*    */   
/*    */ 
/*    */   public static double seconds(int seconds)
/*    */   {
/* 34 */     return 1.0D / seconds;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public static double computeAlpha(double targetWeight, long targetAgeInSeconds)
/*    */   {
/* 42 */     Preconditions.checkArgument(targetAgeInSeconds > 0L, "targetAgeInSeconds must be > 0");
/* 43 */     Preconditions.checkArgument((targetWeight > 0.0D) && (targetWeight < 1.0D), "targetWeight must be in range (0, 1)");
/*    */     
/* 45 */     return -Math.log(targetWeight) / targetAgeInSeconds;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\stats\ExponentialDecay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */