/*    */ package com.yammer.metrics.core;
/*    */ 
/*    */ import java.util.concurrent.TimeUnit;
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
/*    */ public class TimerContext
/*    */ {
/*    */   private final Timer timer;
/*    */   private final Clock clock;
/*    */   private final long startTime;
/*    */   
/*    */   TimerContext(Timer timer, Clock clock)
/*    */   {
/* 22 */     this.timer = timer;
/* 23 */     this.clock = clock;
/* 24 */     this.startTime = clock.tick();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void stop()
/*    */   {
/* 31 */     this.timer.update(this.clock.tick() - this.startTime, TimeUnit.NANOSECONDS);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\core\TimerContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */