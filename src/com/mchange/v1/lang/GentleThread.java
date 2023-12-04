/*    */ package com.mchange.v1.lang;
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
/*    */ public abstract class GentleThread
/*    */   extends Thread
/*    */ {
/* 34 */   boolean should_stop = false;
/* 35 */   boolean should_suspend = false;
/*    */   
/*    */   public GentleThread() {}
/*    */   
/*    */   public GentleThread(String name)
/*    */   {
/* 41 */     super(name);
/*    */   }
/*    */   
/*    */ 
/*    */   public abstract void run();
/*    */   
/*    */   public synchronized void gentleStop()
/*    */   {
/* 49 */     this.should_stop = true;
/*    */   }
/*    */   
/*    */ 
/*    */   public synchronized void gentleSuspend()
/*    */   {
/* 55 */     this.should_suspend = true;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public synchronized void gentleResume()
/*    */   {
/* 62 */     this.should_suspend = false;
/* 63 */     notifyAll();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected synchronized boolean shouldStop()
/*    */   {
/* 73 */     return this.should_stop;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected synchronized boolean shouldSuspend()
/*    */   {
/* 84 */     return this.should_suspend;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected synchronized void allowSuspend()
/*    */     throws InterruptedException
/*    */   {
/* 97 */     while (this.should_suspend) wait();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v1\lang\GentleThread.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */