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
/*    */ 
/*    */ @Immutable
/*    */ public class Column
/*    */ {
/*    */   private final String name;
/*    */   private final String type;
/*    */   private final ClientTypeSignature typeSignature;
/*    */   
/*    */   @JsonCreator
/*    */   public Column(@JsonProperty("name") String name, @JsonProperty("type") String type, @JsonProperty("typeSignature") ClientTypeSignature typeSignature)
/*    */   {
/* 36 */     this.name = ((String)Objects.requireNonNull(name, "name is null"));
/* 37 */     this.type = ((String)Objects.requireNonNull(type, "type is null"));
/* 38 */     this.typeSignature = typeSignature;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public String getName()
/*    */   {
/* 44 */     return this.name;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public String getType()
/*    */   {
/* 50 */     return this.type;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public ClientTypeSignature getTypeSignature()
/*    */   {
/* 56 */     return this.typeSignature;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\client\Column.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */