/*    */ package com.yammer.metrics.reporting;
/*    */ 
/*    */ import com.yammer.metrics.core.MetricsRegistry;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class AbstractReporter
/*    */ {
/*    */   private final MetricsRegistry metricsRegistry;
/*    */   
/*    */   protected AbstractReporter(MetricsRegistry registry)
/*    */   {
/* 18 */     this.metricsRegistry = registry;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void shutdown() {}
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected MetricsRegistry getMetricsRegistry()
/*    */   {
/* 34 */     return this.metricsRegistry;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\reporting\AbstractReporter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */