/*    */ package com.yammer.metrics;
/*    */ 
/*    */ import com.yammer.metrics.core.HealthCheck;
/*    */ import com.yammer.metrics.core.HealthCheck.Result;
/*    */ import com.yammer.metrics.core.HealthCheckRegistry;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HealthChecks
/*    */ {
/* 13 */   private static final HealthCheckRegistry DEFAULT_REGISTRY = new HealthCheckRegistry();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static void register(HealthCheck healthCheck)
/*    */   {
/* 23 */     DEFAULT_REGISTRY.register(healthCheck);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static Map<String, Result> runHealthChecks()
/*    */   {
/* 32 */     return DEFAULT_REGISTRY.runHealthChecks();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static HealthCheckRegistry defaultRegistry()
/*    */   {
/* 41 */     return DEFAULT_REGISTRY;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\HealthChecks.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */