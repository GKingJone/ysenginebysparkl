/*    */ package com.facebook.presto.jdbc.internal.jetty.util;
/*    */ 
/*    */ import java.util.concurrent.atomic.AtomicInteger;
/*    */ import java.util.concurrent.atomic.AtomicLong;
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
/*    */ public class Atomics
/*    */ {
/*    */   public static boolean updateMin(AtomicLong currentMin, long newValue)
/*    */   {
/* 32 */     long oldValue = currentMin.get();
/* 33 */     while (newValue < oldValue)
/*    */     {
/* 35 */       if (currentMin.compareAndSet(oldValue, newValue))
/* 36 */         return true;
/* 37 */       oldValue = currentMin.get();
/*    */     }
/* 39 */     return false;
/*    */   }
/*    */   
/*    */   public static boolean updateMax(AtomicLong currentMax, long newValue)
/*    */   {
/* 44 */     long oldValue = currentMax.get();
/* 45 */     while (newValue > oldValue)
/*    */     {
/* 47 */       if (currentMax.compareAndSet(oldValue, newValue))
/* 48 */         return true;
/* 49 */       oldValue = currentMax.get();
/*    */     }
/* 51 */     return false;
/*    */   }
/*    */   
/*    */   public static boolean updateMin(AtomicInteger currentMin, int newValue)
/*    */   {
/* 56 */     int oldValue = currentMin.get();
/* 57 */     while (newValue < oldValue)
/*    */     {
/* 59 */       if (currentMin.compareAndSet(oldValue, newValue))
/* 60 */         return true;
/* 61 */       oldValue = currentMin.get();
/*    */     }
/* 63 */     return false;
/*    */   }
/*    */   
/*    */   public static boolean updateMax(AtomicInteger currentMax, int newValue)
/*    */   {
/* 68 */     int oldValue = currentMax.get();
/* 69 */     while (newValue > oldValue)
/*    */     {
/* 71 */       if (currentMax.compareAndSet(oldValue, newValue))
/* 72 */         return true;
/* 73 */       oldValue = currentMax.get();
/*    */     }
/* 75 */     return false;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\Atomics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */