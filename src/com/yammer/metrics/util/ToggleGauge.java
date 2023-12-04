/*    */ package com.yammer.metrics.util;
/*    */ 
/*    */ import com.yammer.metrics.core.Gauge;
/*    */ import java.util.concurrent.atomic.AtomicInteger;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ToggleGauge
/*    */   extends Gauge<Integer>
/*    */ {
/* 11 */   private final AtomicInteger value = new AtomicInteger(1);
/*    */   
/*    */   public Integer value()
/*    */   {
/*    */     try {
/* 16 */       return Integer.valueOf(this.value.get());
/*    */     } finally {
/* 18 */       this.value.set(0);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\util\ToggleGauge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */