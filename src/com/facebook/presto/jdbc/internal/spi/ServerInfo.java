/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import java.util.Objects;
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
/*    */ public final class ServerInfo
/*    */ {
/*    */   private final String nodeId;
/*    */   private final String environment;
/*    */   private final String version;
/*    */   
/*    */   public ServerInfo(String nodeId, String environment, String version)
/*    */   {
/* 26 */     this.nodeId = ((String)Objects.requireNonNull(nodeId, "nodeId is null"));
/* 27 */     this.environment = ((String)Objects.requireNonNull(environment, "environment is null"));
/* 28 */     this.version = ((String)Objects.requireNonNull(version, "version is null"));
/*    */   }
/*    */   
/*    */   public String getNodeId()
/*    */   {
/* 33 */     return this.nodeId;
/*    */   }
/*    */   
/*    */   public String getEnvironment()
/*    */   {
/* 38 */     return this.environment;
/*    */   }
/*    */   
/*    */   public String getVersion()
/*    */   {
/* 43 */     return this.version;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 49 */     StringBuilder sb = new StringBuilder("ServerInfo{");
/* 50 */     sb.append("nodeId=").append(this.nodeId);
/* 51 */     sb.append(", environment=").append(this.environment);
/* 52 */     sb.append(", version=").append(this.version);
/* 53 */     sb.append('}');
/* 54 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\ServerInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */