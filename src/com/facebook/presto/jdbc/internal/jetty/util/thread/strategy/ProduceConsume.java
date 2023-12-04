/*    */ package com.facebook.presto.jdbc.internal.jetty.util.thread.strategy;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.thread.ExecutionStrategy;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.thread.ExecutionStrategy.Producer;
/*    */ import java.util.concurrent.Executor;
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
/*    */ public class ProduceConsume
/*    */   implements ExecutionStrategy, Runnable
/*    */ {
/*    */   private final ExecutionStrategy.Producer _producer;
/*    */   private final Executor _executor;
/*    */   
/*    */   public ProduceConsume(ExecutionStrategy.Producer producer, Executor executor)
/*    */   {
/* 36 */     this._producer = producer;
/* 37 */     this._executor = executor;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void execute()
/*    */   {
/*    */     for (;;)
/*    */     {
/* 47 */       Runnable task = this._producer.produce();
/*    */       
/* 49 */       if (task == null) {
/*    */         break;
/*    */       }
/*    */       
/* 53 */       task.run();
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public void dispatch()
/*    */   {
/* 60 */     this._executor.execute(this);
/*    */   }
/*    */   
/*    */ 
/*    */   public void run()
/*    */   {
/* 66 */     execute();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\thread\strategy\ProduceConsume.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */