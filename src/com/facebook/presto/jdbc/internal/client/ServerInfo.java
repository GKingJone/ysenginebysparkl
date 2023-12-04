/*    */ package com.facebook.presto.jdbc.internal.client;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.MoreObjects.ToStringHelper;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
/*    */ import java.util.Objects;
/*    */ import javax.annotation.concurrent.Immutable;
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
/*    */ @Immutable
/*    */ public class ServerInfo
/*    */ {
/*    */   private final NodeVersion nodeVersion;
/*    */   private final String environment;
/*    */   private final boolean coordinator;
/*    */   
/*    */   @JsonCreator
/*    */   public ServerInfo(@JsonProperty("nodeVersion") NodeVersion nodeVersion, @JsonProperty("environment") String environment, @JsonProperty("coordinator") boolean coordinator)
/*    */   {
/* 39 */     this.nodeVersion = ((NodeVersion)Objects.requireNonNull(nodeVersion, "nodeVersion is null"));
/* 40 */     this.environment = ((String)Objects.requireNonNull(environment, "environment is null"));
/* 41 */     this.coordinator = ((Boolean)Objects.requireNonNull(Boolean.valueOf(coordinator), "coordinator is null")).booleanValue();
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public NodeVersion getNodeVersion()
/*    */   {
/* 47 */     return this.nodeVersion;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public String getEnvironment()
/*    */   {
/* 53 */     return this.environment;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public boolean isCoordinator()
/*    */   {
/* 59 */     return this.coordinator;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 65 */     if (this == o) {
/* 66 */       return true;
/*    */     }
/* 68 */     if ((o == null) || (getClass() != o.getClass())) {
/* 69 */       return false;
/*    */     }
/*    */     
/* 72 */     ServerInfo that = (ServerInfo)o;
/* 73 */     return (Objects.equals(this.nodeVersion, that.nodeVersion)) && 
/* 74 */       (Objects.equals(this.environment, that.environment));
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 80 */     return Objects.hash(new Object[] { this.nodeVersion, this.environment });
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 86 */     return 
/*    */     
/*    */ 
/*    */ 
/* 90 */       MoreObjects.toStringHelper(this).add("nodeVersion", this.nodeVersion).add("environment", this.environment).add("coordinator", this.coordinator).toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\client\ServerInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */