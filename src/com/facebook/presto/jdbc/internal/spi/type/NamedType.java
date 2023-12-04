/*    */ package com.facebook.presto.jdbc.internal.spi.type;
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
/*    */ public class NamedType
/*    */ {
/*    */   private final String name;
/*    */   private final Type type;
/*    */   
/*    */   public NamedType(String name, Type type)
/*    */   {
/* 25 */     this.name = name;
/* 26 */     this.type = type;
/*    */   }
/*    */   
/*    */   public String getName()
/*    */   {
/* 31 */     return this.name;
/*    */   }
/*    */   
/*    */   public Type getType()
/*    */   {
/* 36 */     return this.type;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 42 */     if (this == o) {
/* 43 */       return true;
/*    */     }
/* 45 */     if ((o == null) || (getClass() != o.getClass())) {
/* 46 */       return false;
/*    */     }
/*    */     
/* 49 */     NamedType other = (NamedType)o;
/*    */     
/* 51 */     return (Objects.equals(this.name, other.name)) && 
/* 52 */       (Objects.equals(this.type, other.type));
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 58 */     return Objects.hash(new Object[] { this.name, this.type });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\NamedType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */