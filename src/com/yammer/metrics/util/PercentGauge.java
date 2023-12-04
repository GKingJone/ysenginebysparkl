/*    */ package com.yammer.metrics.util;
/*    */ 
/*    */ 
/*    */ public abstract class PercentGauge
/*    */   extends RatioGauge
/*    */ {
/*    */   private static final int ONE_HUNDRED = 100;
/*    */   
/*    */   public Double value()
/*    */   {
/* 11 */     return Double.valueOf(super.value().doubleValue() * 100.0D);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\util\PercentGauge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */