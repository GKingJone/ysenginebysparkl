/*    */ package com.yammer.metrics.util;
/*    */ 
/*    */ import com.yammer.metrics.core.Gauge;
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
/*    */ public abstract class RatioGauge
/*    */   extends Gauge<Double>
/*    */ {
/*    */   protected abstract double getNumerator();
/*    */   
/*    */   protected abstract double getDenominator();
/*    */   
/*    */   public Double value()
/*    */   {
/* 32 */     double d = getDenominator();
/* 33 */     if ((Double.isNaN(d)) || (Double.isInfinite(d)) || (d == 0.0D)) {
/* 34 */       return Double.valueOf(NaN.0D);
/*    */     }
/* 36 */     return Double.valueOf(getNumerator() / d);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\util\RatioGauge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */