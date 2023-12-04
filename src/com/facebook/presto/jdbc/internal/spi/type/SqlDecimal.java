/*    */ package com.facebook.presto.jdbc.internal.spi.type;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonValue;
/*    */ import java.math.BigDecimal;
/*    */ import java.math.BigInteger;
/*    */ import java.math.MathContext;
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
/*    */ public final class SqlDecimal
/*    */ {
/*    */   private final BigInteger unscaledValue;
/*    */   private final int precision;
/*    */   private final int scale;
/*    */   
/*    */   public SqlDecimal(BigInteger unscaledValue, int precision, int scale)
/*    */   {
/* 32 */     this.unscaledValue = unscaledValue;
/* 33 */     this.precision = precision;
/* 34 */     this.scale = scale;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 40 */     if (this == o) {
/* 41 */       return true;
/*    */     }
/* 43 */     if ((o == null) || (getClass() != o.getClass())) {
/* 44 */       return false;
/*    */     }
/* 46 */     SqlDecimal that = (SqlDecimal)o;
/* 47 */     return Objects.equals(this.unscaledValue, that.unscaledValue);
/*    */   }
/*    */   
/*    */   public int getPrecision()
/*    */   {
/* 52 */     return this.precision;
/*    */   }
/*    */   
/*    */   public int getScale()
/*    */   {
/* 57 */     return this.scale;
/*    */   }
/*    */   
/*    */   public static SqlDecimal of(String decimalValue)
/*    */   {
/* 62 */     BigDecimal bigDecimal = new BigDecimal(decimalValue);
/* 63 */     return new SqlDecimal(bigDecimal.unscaledValue(), bigDecimal.precision(), bigDecimal.scale());
/*    */   }
/*    */   
/*    */   public static SqlDecimal of(String unscaledValue, int precision, int scale)
/*    */   {
/* 68 */     return new SqlDecimal(new BigInteger(unscaledValue), precision, scale);
/*    */   }
/*    */   
/*    */   public static SqlDecimal of(long unscaledValue, int precision, int scale)
/*    */   {
/* 73 */     return new SqlDecimal(BigInteger.valueOf(unscaledValue), precision, scale);
/*    */   }
/*    */   
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 79 */     return Objects.hash(new Object[] { this.unscaledValue });
/*    */   }
/*    */   
/*    */ 
/*    */   @JsonValue
/*    */   public String toString()
/*    */   {
/* 86 */     return Decimals.toString(this.unscaledValue, this.scale);
/*    */   }
/*    */   
/*    */   public BigDecimal toBigDecimal()
/*    */   {
/* 91 */     return new BigDecimal(this.unscaledValue, this.scale, new MathContext(this.precision));
/*    */   }
/*    */   
/*    */   public BigInteger getUnscaledValue()
/*    */   {
/* 96 */     return this.unscaledValue;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\type\SqlDecimal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */