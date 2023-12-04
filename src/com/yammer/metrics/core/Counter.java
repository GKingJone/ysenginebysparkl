/*    */ package com.yammer.metrics.core;
/*    */ 
/*    */ import java.util.concurrent.atomic.AtomicLong;
/*    */ 
/*    */ public class Counter
/*    */   implements Metric
/*    */ {
/*    */   private final AtomicLong count;
/*    */   
/*    */   Counter()
/*    */   {
/* 12 */     this.count = new AtomicLong(0L);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void inc()
/*    */   {
/* 19 */     inc(1L);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void inc(long n)
/*    */   {
/* 28 */     this.count.addAndGet(n);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void dec()
/*    */   {
/* 35 */     dec(1L);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void dec(long n)
/*    */   {
/* 44 */     this.count.addAndGet(0L - n);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public long count()
/*    */   {
/* 53 */     return this.count.get();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void clear()
/*    */   {
/* 60 */     this.count.set(0L);
/*    */   }
/*    */   
/*    */   public <T> void processWith(MetricProcessor<T> processor, MetricName name, T context) throws Exception
/*    */   {
/* 65 */     processor.processCounter(name, this, context);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\core\Counter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */