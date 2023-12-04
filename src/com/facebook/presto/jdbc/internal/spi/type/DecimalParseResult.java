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
/*    */ public class DecimalParseResult
/*    */ {
/*    */   private final Object object;
/*    */   private final DecimalType type;
/*    */   
/*    */   public DecimalParseResult(Object object, DecimalType type)
/*    */   {
/* 25 */     this.object = object;
/* 26 */     this.type = type;
/*    */   }
/*    */   
/*    */   public Object getObject()
/*    */   {
/* 31 */     return this.object;
/*    */   }
/*    */   
/*    */   public DecimalType getType()
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
/* 48 */     DecimalParseResult that = (DecimalParseResult)o;
/* 49 */     return (Objects.equals(this.object, that.object)) && 
/* 50 */       (Objects.equals(this.type, that.type));
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 56 */     return Objects.hash(new Object[] { this.object, this.type });
/*    */   }
/*    */   
/*    */ 
/*    */   public String toString()
/*    */   {
/* 62 */     return "ParseResult{object=" + this.object + ", type=" + this.type + '}';
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\DecimalParseResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */