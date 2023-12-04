/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.lang.ref.Reference;
/*    */ import java.lang.ref.ReferenceQueue;
/*    */ import java.util.concurrent.ConcurrentHashMap;
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
/*    */ public class AbandonedConnectionCleanupThread
/*    */   extends Thread
/*    */ {
/* 31 */   private static boolean running = true;
/* 32 */   private static Thread threadRef = null;
/*    */   
/*    */   public AbandonedConnectionCleanupThread() {
/* 35 */     super("Abandoned connection cleanup thread");
/*    */   }
/*    */   
/*    */   public void run()
/*    */   {
/* 40 */     threadRef = this;
/* 41 */     while (running) {
/*    */       try {
/* 43 */         Reference<? extends ConnectionImpl> ref = NonRegisteringDriver.refQueue.remove(100L);
/* 44 */         if (ref != null) {
/*    */           try {
/* 46 */             ((NonRegisteringDriver.ConnectionPhantomReference)ref).cleanup();
/*    */           } finally {
/* 48 */             NonRegisteringDriver.connectionPhantomRefs.remove(ref);
/*    */           }
/*    */         }
/*    */       }
/*    */       catch (Exception ex) {}
/*    */     }
/*    */   }
/*    */   
/*    */   public static void shutdown()
/*    */     throws InterruptedException
/*    */   {
/* 59 */     running = false;
/* 60 */     if (threadRef != null) {
/* 61 */       threadRef.interrupt();
/* 62 */       threadRef.join();
/* 63 */       threadRef = null;
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\AbandonedConnectionCleanupThread.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */