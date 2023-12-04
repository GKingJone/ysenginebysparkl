/*    */ package com.facebook.presto.jdbc.internal.spi;
/*    */ 
/*    */ import java.util.Objects;
/*    */ import java.util.Optional;
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
/*    */ public class ConnectorViewDefinition
/*    */ {
/*    */   private final SchemaTableName name;
/*    */   private final Optional<String> owner;
/*    */   private final String viewData;
/*    */   
/*    */   public ConnectorViewDefinition(SchemaTableName name, Optional<String> owner, String viewData)
/*    */   {
/* 28 */     this.name = ((SchemaTableName)Objects.requireNonNull(name, "name is null"));
/* 29 */     this.owner = ((Optional)Objects.requireNonNull(owner, "owner is null"));
/* 30 */     this.viewData = ((String)Objects.requireNonNull(viewData, "viewData is null"));
/*    */   }
/*    */   
/*    */   public SchemaTableName getName()
/*    */   {
/* 35 */     return this.name;
/*    */   }
/*    */   
/*    */   public Optional<String> getOwner()
/*    */   {
/* 40 */     return this.owner;
/*    */   }
/*    */   
/*    */   public String getViewData()
/*    */   {
/* 45 */     return this.viewData;
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 51 */     StringBuilder sb = new StringBuilder("ConnectorViewDefinition{");
/* 52 */     sb.append("name=").append(this.name);
/* 53 */     sb.append(", owner=").append(this.owner);
/* 54 */     sb.append('}');
/* 55 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\ConnectorViewDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */