/*    */ package com.facebook.presto.jdbc.internal.spi.type;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NamedTypeSignature
/*    */ {
/*    */   private final String name;
/*    */   private final TypeSignature typeSignature;
/*    */   
/*    */   @JsonCreator
/*    */   public NamedTypeSignature(@JsonProperty("name") String name, @JsonProperty("typeSignature") TypeSignature typeSignature)
/*    */   {
/* 33 */     this.name = name;
/* 34 */     this.typeSignature = typeSignature;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public String getName()
/*    */   {
/* 40 */     return this.name;
/*    */   }
/*    */   
/*    */   @JsonProperty
/*    */   public TypeSignature getTypeSignature()
/*    */   {
/* 46 */     return this.typeSignature;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 52 */     if (this == o) {
/* 53 */       return true;
/*    */     }
/* 55 */     if ((o == null) || (getClass() != o.getClass())) {
/* 56 */       return false;
/*    */     }
/*    */     
/* 59 */     NamedTypeSignature other = (NamedTypeSignature)o;
/*    */     
/* 61 */     return (Objects.equals(this.name, other.name)) && 
/* 62 */       (Objects.equals(this.typeSignature, other.typeSignature));
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 68 */     return String.format("%s %s", new Object[] { this.name, this.typeSignature });
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 74 */     return Objects.hash(new Object[] { this.name, this.typeSignature });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\NamedTypeSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */