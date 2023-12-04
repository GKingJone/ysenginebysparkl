/*    */ package com.yammer.metrics.core;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract interface MetricPredicate
/*    */ {
/* 11 */   public static final MetricPredicate ALL = new MetricPredicate()
/*    */   {
/*    */     public boolean matches(MetricName name, Metric metric) {
/* 14 */       return true;
/*    */     }
/*    */   };
/*    */   
/*    */   public abstract boolean matches(MetricName paramMetricName, Metric paramMetric);
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\core\MetricPredicate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */