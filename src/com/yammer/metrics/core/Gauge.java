/*    */ package com.yammer.metrics.core;
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
/*    */ public abstract class Gauge<T>
/*    */   implements Metric
/*    */ {
/*    */   public abstract T value();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public <U> void processWith(MetricProcessor<U> processor, MetricName name, U context)
/*    */     throws Exception
/*    */   {
/* 28 */     processor.processGauge(name, this, context);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\core\Gauge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */