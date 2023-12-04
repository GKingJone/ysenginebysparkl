/*    */ package com.yammer.metrics.util;
/*    */ 
/*    */ import com.yammer.metrics.core.Counter;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
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
/*    */ public class DeathRattleExceptionHandler
/*    */   implements Thread.UncaughtExceptionHandler
/*    */ {
/* 36 */   private static final Logger LOGGER = LoggerFactory.getLogger(DeathRattleExceptionHandler.class);
/*    */   
/*    */ 
/*    */ 
/*    */   private final Counter counter;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public DeathRattleExceptionHandler(Counter counter)
/*    */   {
/* 47 */     this.counter = counter;
/*    */   }
/*    */   
/*    */   public void uncaughtException(Thread t, Throwable e)
/*    */   {
/* 52 */     this.counter.inc();
/* 53 */     LOGGER.error("Uncaught exception on thread {}", t, e);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\util\DeathRattleExceptionHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */