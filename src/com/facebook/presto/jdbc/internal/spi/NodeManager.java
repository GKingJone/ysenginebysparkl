/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import java.util.Set;
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
/*    */ public abstract interface NodeManager
/*    */ {
/*    */   public abstract Set<Node> getAllNodes();
/*    */   
/*    */   public abstract Set<Node> getWorkerNodes();
/*    */   
/*    */   public abstract Node getCurrentNode();
/*    */   
/*    */   public abstract String getEnvironment();
/*    */   
/*    */   public Set<Node> getRequiredWorkerNodes()
/*    */   {
/* 32 */     Set<Node> nodes = getWorkerNodes();
/* 33 */     if (nodes.isEmpty()) {
/* 34 */       throw new PrestoException(StandardErrorCode.NO_NODES_AVAILABLE, "No nodes available to run query");
/*    */     }
/* 36 */     return nodes;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\NodeManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */