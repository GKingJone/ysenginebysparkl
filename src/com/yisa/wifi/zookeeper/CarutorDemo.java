/*    */ package com.yisa.wifi.zookeeper;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import org.apache.curator.framework.CuratorFramework;
/*    */ import org.apache.curator.framework.CuratorFrameworkFactory;
/*    */ import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
/*    */ import org.apache.curator.framework.listen.ListenerContainer;
/*    */ import org.apache.curator.framework.recipes.cache.ChildData;
/*    */ import org.apache.curator.framework.recipes.cache.NodeCache;
/*    */ import org.apache.curator.framework.recipes.cache.NodeCacheListener;
/*    */ import org.apache.curator.retry.ExponentialBackoffRetry;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CarutorDemo
/*    */ {
/*    */   public static void main(String[] args)
/*    */     throws Exception
/*    */   {
/* 22 */     CuratorFramework client = CuratorFrameworkFactory.builder().connectString("cdh2:2181").sessionTimeoutMs(5000).connectionTimeoutMs(3000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
/* 23 */     client.start();
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
/* 37 */     NodeCache nodeCache = new NodeCache(client, "/test/key1", false);
/* 38 */     nodeCache.start(true);
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
/* 50 */     nodeCache.getListenable().addListener(new NodeCacheListener()
/*    */     {
/*    */       public void nodeChanged() throws Exception {
/* 53 */         System.out.println("Node data is changed, new data: " + new String(this.val$nodeCache
/* 54 */           .getCurrentData().getData()));
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
/*    */       }
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
/* 88 */     });
/* 89 */     Thread.sleep(Long.MAX_VALUE);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yisa\wifi\zookeeper\CarutorDemo.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */