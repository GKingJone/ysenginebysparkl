/*    */ package com.yammer.metrics.reporting;
/*    */ 
/*    */ import com.yammer.metrics.core.MetricsRegistry;
/*    */ import java.util.concurrent.ScheduledExecutorService;
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
/*    */ public abstract class AbstractPollingReporter
/*    */   extends AbstractReporter
/*    */   implements Runnable
/*    */ {
/*    */   private final ScheduledExecutorService executor;
/*    */   
/*    */   protected AbstractPollingReporter(MetricsRegistry registry, String name)
/*    */   {
/* 24 */     super(registry);
/* 25 */     this.executor = registry.newScheduledThreadPool(1, name);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void start(long period, TimeUnit unit)
/*    */   {
/* 35 */     this.executor.scheduleWithFixedDelay(this, period, period, unit);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void shutdown(long timeout, TimeUnit unit)
/*    */     throws InterruptedException
/*    */   {
/* 47 */     this.executor.shutdown();
/* 48 */     this.executor.awaitTermination(timeout, unit);
/*    */   }
/*    */   
/*    */   public void shutdown()
/*    */   {
/* 53 */     this.executor.shutdown();
/* 54 */     super.shutdown();
/*    */   }
/*    */   
/*    */   public abstract void run();
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\reporting\AbstractPollingReporter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */