/*    */ package com.facebook.presto.jdbc.internal.jetty.client;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.Request;
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.Response.CompleteListener;
/*    */ import com.facebook.presto.jdbc.internal.jetty.client.api.Result;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Log;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.log.Logger;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Scheduler;
/*    */ import com.facebook.presto.jdbc.internal.jetty.util.thread.Scheduler.Task;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import java.util.concurrent.TimeoutException;
/*    */ import java.util.concurrent.atomic.AtomicReference;
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
/*    */ public class TimeoutCompleteListener
/*    */   implements Response.CompleteListener, Runnable
/*    */ {
/* 34 */   private static final Logger LOG = Log.getLogger(TimeoutCompleteListener.class);
/*    */   
/* 36 */   private final AtomicReference<Task> task = new AtomicReference();
/*    */   private final Request request;
/*    */   
/*    */   public TimeoutCompleteListener(Request request)
/*    */   {
/* 41 */     this.request = request;
/*    */   }
/*    */   
/*    */ 
/*    */   public void onComplete(Result result)
/*    */   {
/* 47 */     cancel();
/*    */   }
/*    */   
/*    */   public boolean schedule(Scheduler scheduler)
/*    */   {
/* 52 */     long timeout = this.request.getTimeout();
/* 53 */     Task task = scheduler.schedule(this, timeout, TimeUnit.MILLISECONDS);
/* 54 */     Task existing = (Task)this.task.getAndSet(task);
/* 55 */     if (existing != null)
/*    */     {
/* 57 */       existing.cancel();
/* 58 */       cancel();
/* 59 */       throw new IllegalStateException();
/*    */     }
/* 61 */     if (LOG.isDebugEnabled())
/* 62 */       LOG.debug("Scheduled timeout task {} in {} ms for {}", new Object[] { task, Long.valueOf(timeout), this.request });
/* 63 */     return true;
/*    */   }
/*    */   
/*    */ 
/*    */   public void run()
/*    */   {
/* 69 */     if (LOG.isDebugEnabled())
/* 70 */       LOG.debug("Executing timeout task {} for {}", new Object[] { this.task, this.request });
/* 71 */     this.request.abort(new TimeoutException("Total timeout " + this.request.getTimeout() + " ms elapsed"));
/*    */   }
/*    */   
/*    */   public void cancel()
/*    */   {
/* 76 */     Task task = (Task)this.task.getAndSet(null);
/* 77 */     if (task != null)
/*    */     {
/* 79 */       boolean cancelled = task.cancel();
/* 80 */       if (LOG.isDebugEnabled()) {
/* 81 */         LOG.debug("Cancelled (successfully: {}) timeout task {}", new Object[] { Boolean.valueOf(cancelled), task });
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\TimeoutCompleteListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */