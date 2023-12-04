/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.node;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser.NumberType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.io.NumberOutput;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*     */ import java.io.IOException;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ShortNode
/*     */   extends NumericNode
/*     */ {
/*     */   protected final short _value;
/*     */   
/*  26 */   public ShortNode(short v) { this._value = v; }
/*     */   
/*  28 */   public static ShortNode valueOf(short l) { return new ShortNode(l); }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonToken asToken()
/*     */   {
/*  36 */     return JsonToken.VALUE_NUMBER_INT;
/*     */   }
/*     */   
/*  39 */   public JsonParser.NumberType numberType() { return JsonParser.NumberType.INT; }
/*     */   
/*     */   public boolean isIntegralNumber()
/*     */   {
/*  43 */     return true;
/*     */   }
/*     */   
/*  46 */   public boolean isShort() { return true; }
/*     */   
/*  48 */   public boolean canConvertToInt() { return true; }
/*  49 */   public boolean canConvertToLong() { return true; }
/*     */   
/*     */   public Number numberValue()
/*     */   {
/*  53 */     return Short.valueOf(this._value);
/*     */   }
/*     */   
/*     */   public short shortValue() {
/*  57 */     return this._value;
/*     */   }
/*     */   
/*  60 */   public int intValue() { return this._value; }
/*     */   
/*     */   public long longValue() {
/*  63 */     return this._value;
/*     */   }
/*     */   
/*  66 */   public float floatValue() { return this._value; }
/*     */   
/*     */   public double doubleValue() {
/*  69 */     return this._value;
/*     */   }
/*     */   
/*  72 */   public BigDecimal decimalValue() { return BigDecimal.valueOf(this._value); }
/*     */   
/*     */   public BigInteger bigIntegerValue() {
/*  75 */     return BigInteger.valueOf(this._value);
/*     */   }
/*     */   
/*     */   public String asText() {
/*  79 */     return NumberOutput.toString(this._value);
/*     */   }
/*     */   
/*     */   public boolean asBoolean(boolean defaultValue)
/*     */   {
/*  84 */     return this._value != 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public final void serialize(JsonGenerator jg, SerializerProvider provider)
/*     */     throws IOException, JsonProcessingException
/*     */   {
/*  91 */     jg.writeNumber(this._value);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/*  97 */     if (o == this) return true;
/*  98 */     if (o == null) return false;
/*  99 */     if ((o instanceof ShortNode)) {
/* 100 */       return ((ShortNode)o)._value == this._value;
/*     */     }
/* 102 */     return false;
/*     */   }
/*     */   
/*     */   public int hashCode()
/*     */   {
/* 107 */     return this._value;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\node\ShortNode.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */