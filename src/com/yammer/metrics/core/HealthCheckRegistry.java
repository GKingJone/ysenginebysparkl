/*    */ package com.yammer.metrics.core;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.SortedMap;
/*    */ import java.util.TreeMap;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ import java.util.concurrent.ConcurrentMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HealthCheckRegistry
/*    */ {
/* 16 */   private final ConcurrentMap<String, HealthCheck> healthChecks = new ConcurrentHashMap();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void register(HealthCheck healthCheck)
/*    */   {
/* 24 */     this.healthChecks.putIfAbsent(healthCheck.getName(), healthCheck);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void unregister(String name)
/*    */   {
/* 33 */     this.healthChecks.remove(name);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void unregister(HealthCheck healthCheck)
/*    */   {
/* 42 */     unregister(healthCheck.getName());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public SortedMap<String, HealthCheck.Result> runHealthChecks()
/*    */   {
/* 51 */     SortedMap<String, HealthCheck.Result> results = new TreeMap();
/* 52 */     for (Map.Entry<String, HealthCheck> entry : this.healthChecks.entrySet()) {
/* 53 */       HealthCheck.Result result = ((HealthCheck)entry.getValue()).execute();
/* 54 */       results.put(entry.getKey(), result);
/*    */     }
/* 56 */     return Collections.unmodifiableSortedMap(results);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\core\HealthCheckRegistry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */