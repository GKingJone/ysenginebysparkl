/*    */ package com.facebook.presto.jdbc.internal.jetty.util.thread.strategy;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.thread.ExecutionStrategy;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.thread.ExecutionStrategy.Producer;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.thread.ExecutionStrategy.Rejectable;
/*    */ import java.util.concurrent.Executor;
/*    */ import java.util.concurrent.RejectedExecutionException;
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
/*    */ public class ProduceExecuteConsume
/*    */   implements ExecutionStrategy
/*    */ {
/* 34 */   private static final Logger LOG = Log.getLogger(ExecutionStrategy.class);
/*    */   private final ExecutionStrategy.Producer _producer;
/*    */   private final Executor _executor;
/*    */   
/*    */   public ProduceExecuteConsume(ExecutionStrategy.Producer producer, Executor executor)
/*    */   {
/* 40 */     this._producer = producer;
/* 41 */     this._executor = executor;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void execute()
/*    */   {
/*    */     for (;;)
/*    */     {
/* 51 */       Runnable task = this._producer.produce();
/* 52 */       if (LOG.isDebugEnabled()) {
/* 53 */         LOG.debug("{} PER produced {}", new Object[] { this._producer, task });
/*    */       }
/* 55 */       if (task == null) {
/*    */         break;
/*    */       }
/*    */       
/*    */       try
/*    */       {
/* 61 */         this._executor.execute(task);
/*    */ 
/*    */       }
/*    */       catch (RejectedExecutionException e)
/*    */       {
/* 66 */         if ((task instanceof ExecutionStrategy.Rejectable))
/*    */         {
/*    */           try
/*    */           {
/* 70 */             ((ExecutionStrategy.Rejectable)task).reject();
/*    */           }
/*    */           catch (Throwable x)
/*    */           {
/* 74 */             e.addSuppressed(x);
/* 75 */             LOG.warn(e);
/*    */           }
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */   public void dispatch()
/*    */   {
/* 85 */     execute();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\thread\strategy\ProduceExecuteConsume.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */