/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.node;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser.NumberType;
/*    */ import java.math.BigDecimal;
/*    */ import java.math.BigInteger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class NumericNode
/*    */   extends ValueNode
/*    */ {
/*    */   public final JsonNodeType getNodeType()
/*    */   {
/* 19 */     return JsonNodeType.NUMBER;
/*    */   }
/*    */   
/*    */ 
/*    */   public abstract JsonParser.NumberType numberType();
/*    */   
/*    */ 
/*    */   public abstract Number numberValue();
/*    */   
/*    */ 
/*    */   public abstract int intValue();
/*    */   
/*    */ 
/*    */   public abstract long longValue();
/*    */   
/*    */   public abstract double doubleValue();
/*    */   
/*    */   public abstract BigDecimal decimalValue();
/*    */   
/*    */   public abstract BigInteger bigIntegerValue();
/*    */   
/*    */   public abstract boolean canConvertToInt();
/*    */   
/*    */   public abstract boolean canConvertToLong();
/*    */   
/*    */   public abstract String asText();
/*    */   
/*    */   public final int asInt()
/*    */   {
/* 48 */     return intValue();
/*    */   }
/*    */   
/*    */   public final int asInt(int defaultValue) {
/* 52 */     return intValue();
/*    */   }
/*    */   
/*    */   public final long asLong()
/*    */   {
/* 57 */     return longValue();
/*    */   }
/*    */   
/*    */   public final long asLong(long defaultValue) {
/* 61 */     return longValue();
/*    */   }
/*    */   
/*    */   public final double asDouble()
/*    */   {
/* 66 */     return doubleValue();
/*    */   }
/*    */   
/*    */   public final double asDouble(double defaultValue) {
/* 70 */     return doubleValue();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\node\NumericNode.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */