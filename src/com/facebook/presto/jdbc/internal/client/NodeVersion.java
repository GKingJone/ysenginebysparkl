/*    */ package com.facebook.presto.jdbc.internal.client;
/*    */ 
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
/*    */ @Immutable
/*    */ public class NodeVersion
/*    */ {
/* 28 */   public static final NodeVersion UNKNOWN = new NodeVersion("<unknown>");
/*    */   
/*    */   private final String version;
/*    */   
/*    */   @JsonCreator
/*    */   public NodeVersion(@JsonProperty("version") String version)
/*    */   {
/* 35 */     this.version = ((String)Objects.requireNonNull(version, "version is null"));
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public String getVersion()
/*    */   {
/* 41 */     return this.version;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 47 */     if (this == o) {
/* 48 */       return true;
/*    */     }
/* 50 */     if ((o == null) || (getClass() != o.getClass())) {
/* 51 */       return false;
/*    */     }
/*    */     
/* 54 */     NodeVersion that = (NodeVersion)o;
/* 55 */     return Objects.equals(this.version, that.version);
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 61 */     return Objects.hash(new Object[] { this.version });
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 67 */     return this.version;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\client\NodeVersion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */