/*    */ package com.facebook.presto.jdbc.internal.jetty.util;
/*    */ 
/*    */ import java.util.concurrent.atomic.AtomicInteger;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CountingCallback
/*    */   extends Callback.Nested
/*    */ {
/*    */   private final AtomicInteger count;
/*    */   
/*    */   public CountingCallback(Callback callback, int count)
/*    */   {
/* 47 */     super(callback);
/* 48 */     this.count = new AtomicInteger(count);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void succeeded()
/*    */   {
/*    */     for (;;)
/*    */     {
/* 57 */       int current = this.count.get();
/*    */       
/*    */ 
/* 60 */       if (current == 0) {
/* 61 */         return;
/*    */       }
/* 63 */       if (this.count.compareAndSet(current, current - 1))
/*    */       {
/* 65 */         if (current == 1)
/* 66 */           super.succeeded();
/* 67 */         return;
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void failed(Throwable failure)
/*    */   {
/*    */     for (;;)
/*    */     {
/* 78 */       int current = this.count.get();
/*    */       
/*    */ 
/* 81 */       if (current == 0) {
/* 82 */         return;
/*    */       }
/* 84 */       if (this.count.compareAndSet(current, 0))
/*    */       {
/* 86 */         super.failed(failure);
/* 87 */         return;
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 95 */     return String.format("%s@%x", new Object[] { getClass().getSimpleName(), Integer.valueOf(hashCode()) });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\CountingCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */