/*    */ package com.yammer.metrics.util;
/*    */ 
/*    */ import com.yammer.metrics.core.HealthCheck;
/*    */ import com.yammer.metrics.core.HealthCheck.Result;
/*    */ import com.yammer.metrics.core.VirtualMachineMetrics;
/*    */ import java.util.Set;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DeadlockHealthCheck
/*    */   extends HealthCheck
/*    */ {
/*    */   private final VirtualMachineMetrics vm;
/*    */   
/*    */   public DeadlockHealthCheck(VirtualMachineMetrics vm)
/*    */   {
/* 21 */     super("deadlocks");
/* 22 */     this.vm = vm;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public DeadlockHealthCheck()
/*    */   {
/* 30 */     this(VirtualMachineMetrics.getInstance());
/*    */   }
/*    */   
/*    */   protected Result check() throws Exception
/*    */   {
/* 35 */     Set<String> threads = this.vm.deadlockedThreads();
/* 36 */     if (threads.isEmpty()) {
/* 37 */       return Result.healthy();
/*    */     }
/*    */     
/* 40 */     StringBuilder builder = new StringBuilder("Deadlocked threads detected:\n");
/* 41 */     for (String thread : threads) {
/* 42 */       builder.append(thread).append('\n');
/*    */     }
/* 44 */     return Result.unhealthy(builder.toString());
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\util\DeadlockHealthCheck.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */