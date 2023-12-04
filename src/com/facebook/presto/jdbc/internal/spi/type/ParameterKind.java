/*    */ package com.facebook.presto.jdbc.internal.spi.type;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonValue;
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
/*    */ 
/*    */ public enum ParameterKind
/*    */ {
/* 24 */   TYPE(Optional.of("TYPE_SIGNATURE")), 
/* 25 */   NAMED_TYPE(Optional.of("NAMED_TYPE_SIGNATURE")), 
/* 26 */   LONG(Optional.of("LONG_LITERAL")), 
/* 27 */   VARIABLE(Optional.empty());
/*    */   
/*    */ 
/*    */ 
/*    */   private final Optional<String> oldName;
/*    */   
/*    */ 
/*    */   private ParameterKind(Optional<String> oldName)
/*    */   {
/* 36 */     this.oldName = oldName;
/*    */   }
/*    */   
/*    */   @JsonValue
/*    */   public String jsonName()
/*    */   {
/* 42 */     return (String)this.oldName.orElse(name());
/*    */   }
/*    */   
/*    */   @JsonCreator
/*    */   public static ParameterKind fromJsonValue(String value)
/*    */   {
/* 48 */     for (ParameterKind kind : ) {
/* 49 */       if ((kind.oldName.isPresent()) && (((String)kind.oldName.get()).equals(value))) {
/* 50 */         return kind;
/*    */       }
/* 52 */       if (kind.name().equals(value)) {
/* 53 */         return kind;
/*    */       }
/*    */     }
/* 56 */     throw new IllegalArgumentException("Invalid serialized ParameterKind value: " + value);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\ParameterKind.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */