/*    */ package com.yammer.metrics.core;
/*    */ 
/*    */ import java.lang.management.ManagementFactory;
/*    */ import java.lang.management.ThreadMXBean;
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
/*    */ public abstract class Clock
/*    */ {
/*    */   public abstract long tick();
/*    */   
/*    */   public long time()
/*    */   {
/* 23 */     return System.currentTimeMillis();
/*    */   }
/*    */   
/* 26 */   private static final Clock DEFAULT = new UserTimeClock();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static Clock defaultClock()
/*    */   {
/* 36 */     return DEFAULT;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public static class UserTimeClock
/*    */     extends Clock
/*    */   {
/*    */     public long tick()
/*    */     {
/* 46 */       return System.nanoTime();
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public static class CpuTimeClock
/*    */     extends Clock
/*    */   {
/* 54 */     private static final ThreadMXBean THREAD_MX_BEAN = ;
/*    */     
/*    */     public long tick()
/*    */     {
/* 58 */       return THREAD_MX_BEAN.getCurrentThreadCpuTime();
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\core\Clock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */