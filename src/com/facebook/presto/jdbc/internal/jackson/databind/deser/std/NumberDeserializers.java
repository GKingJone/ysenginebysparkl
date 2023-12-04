/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import java.io.IOException;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.HashSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NumberDeserializers
/*     */ {
/*  22 */   private static final HashSet<String> _classNames = new HashSet();
/*     */   
/*     */   static {
/*  25 */     Class<?>[] numberTypes = { Boolean.class, Byte.class, Short.class, Character.class, Integer.class, Long.class, Float.class, Double.class, Number.class, BigDecimal.class, BigInteger.class };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  37 */     for (Class<?> cls : numberTypes) {
/*  38 */       _classNames.add(cls.getName());
/*     */     }
/*     */   }
/*     */   
/*     */   public static JsonDeserializer<?> find(Class<?> rawType, String clsName) {
/*  43 */     if (rawType.isPrimitive()) {
/*  44 */       if (rawType == Integer.TYPE) {
/*  45 */         return IntegerDeserializer.primitiveInstance;
/*     */       }
/*  47 */       if (rawType == Boolean.TYPE) {
/*  48 */         return BooleanDeserializer.primitiveInstance;
/*     */       }
/*  50 */       if (rawType == Long.TYPE) {
/*  51 */         return LongDeserializer.primitiveInstance;
/*     */       }
/*  53 */       if (rawType == Double.TYPE) {
/*  54 */         return DoubleDeserializer.primitiveInstance;
/*     */       }
/*  56 */       if (rawType == Character.TYPE) {
/*  57 */         return CharacterDeserializer.primitiveInstance;
/*     */       }
/*  59 */       if (rawType == Byte.TYPE) {
/*  60 */         return ByteDeserializer.primitiveInstance;
/*     */       }
/*  62 */       if (rawType == Short.TYPE) {
/*  63 */         return ShortDeserializer.primitiveInstance;
/*     */       }
/*  65 */       if (rawType == Float.TYPE) {
/*  66 */         return FloatDeserializer.primitiveInstance;
/*     */       }
/*  68 */     } else if (_classNames.contains(clsName))
/*     */     {
/*  70 */       if (rawType == Integer.class) {
/*  71 */         return IntegerDeserializer.wrapperInstance;
/*     */       }
/*  73 */       if (rawType == Boolean.class) {
/*  74 */         return BooleanDeserializer.wrapperInstance;
/*     */       }
/*  76 */       if (rawType == Long.class) {
/*  77 */         return LongDeserializer.wrapperInstance;
/*     */       }
/*  79 */       if (rawType == Double.class) {
/*  80 */         return DoubleDeserializer.wrapperInstance;
/*     */       }
/*  82 */       if (rawType == Character.class) {
/*  83 */         return CharacterDeserializer.wrapperInstance;
/*     */       }
/*  85 */       if (rawType == Byte.class) {
/*  86 */         return ByteDeserializer.wrapperInstance;
/*     */       }
/*  88 */       if (rawType == Short.class) {
/*  89 */         return ShortDeserializer.wrapperInstance;
/*     */       }
/*  91 */       if (rawType == Float.class) {
/*  92 */         return FloatDeserializer.wrapperInstance;
/*     */       }
/*  94 */       if (rawType == Number.class) {
/*  95 */         return NumberDeserializer.instance;
/*     */       }
/*  97 */       if (rawType == BigDecimal.class) {
/*  98 */         return BigDecimalDeserializer.instance;
/*     */       }
/* 100 */       if (rawType == BigInteger.class) {
/* 101 */         return BigIntegerDeserializer.instance;
/*     */       }
/*     */     } else {
/* 104 */       return null;
/*     */     }
/*     */     
/* 107 */     throw new IllegalArgumentException("Internal error: can't find deserializer for " + rawType.getName());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static abstract class PrimitiveOrWrapperDeserializer<T>
/*     */     extends StdScalarDeserializer<T>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */ 
/*     */     protected final T _nullValue;
/*     */     
/*     */ 
/*     */     protected final boolean _primitive;
/*     */     
/*     */ 
/*     */     protected PrimitiveOrWrapperDeserializer(Class<T> vc, T nvl)
/*     */     {
/* 126 */       super();
/* 127 */       this._nullValue = nvl;
/* 128 */       this._primitive = vc.isPrimitive();
/*     */     }
/*     */     
/*     */     public final T getNullValue(DeserializationContext ctxt)
/*     */       throws JsonMappingException
/*     */     {
/* 134 */       if ((this._primitive) && (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES))) {
/* 135 */         ctxt.reportMappingException("Can not map JSON null into type %s (set DeserializationConfig.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES to 'false' to allow)", new Object[] { handledType().toString() });
/*     */       }
/*     */       
/*     */ 
/* 139 */       return (T)this._nullValue;
/*     */     }
/*     */     
/*     */ 
/*     */     public T getEmptyValue(DeserializationContext ctxt)
/*     */       throws JsonMappingException
/*     */     {
/* 146 */       if ((this._primitive) && (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES))) {
/* 147 */         ctxt.reportMappingException("Can not map Empty String as null into type %s (set DeserializationConfig.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES to 'false' to allow)", new Object[] { handledType().toString() });
/*     */       }
/*     */       
/*     */ 
/* 151 */       return (T)this._nullValue;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @JacksonStdImpl
/*     */   public static final class BooleanDeserializer
/*     */     extends PrimitiveOrWrapperDeserializer<Boolean>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */ 
/*     */ 
/* 167 */     static final BooleanDeserializer primitiveInstance = new BooleanDeserializer(Boolean.TYPE, Boolean.FALSE);
/* 168 */     static final BooleanDeserializer wrapperInstance = new BooleanDeserializer(Boolean.class, null);
/*     */     
/*     */     public BooleanDeserializer(Class<Boolean> cls, Boolean nvl)
/*     */     {
/* 172 */       super(nvl);
/*     */     }
/*     */     
/*     */     public Boolean deserialize(JsonParser j, DeserializationContext ctxt)
/*     */       throws IOException
/*     */     {
/* 178 */       return _parseBoolean(j, ctxt);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Boolean deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
/*     */       throws IOException
/*     */     {
/* 188 */       return _parseBoolean(p, ctxt);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @JacksonStdImpl
/*     */   public static class ByteDeserializer
/*     */     extends PrimitiveOrWrapperDeserializer<Byte>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/* 198 */     static final ByteDeserializer primitiveInstance = new ByteDeserializer(Byte.TYPE, Byte.valueOf((byte)0));
/* 199 */     static final ByteDeserializer wrapperInstance = new ByteDeserializer(Byte.class, null);
/*     */     
/*     */     public ByteDeserializer(Class<Byte> cls, Byte nvl)
/*     */     {
/* 203 */       super(nvl);
/*     */     }
/*     */     
/*     */     public Byte deserialize(JsonParser p, DeserializationContext ctxt)
/*     */       throws IOException
/*     */     {
/* 209 */       return _parseByte(p, ctxt);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @JacksonStdImpl
/*     */   public static class ShortDeserializer
/*     */     extends PrimitiveOrWrapperDeserializer<Short>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/* 219 */     static final ShortDeserializer primitiveInstance = new ShortDeserializer(Short.TYPE, Short.valueOf((short)0));
/* 220 */     static final ShortDeserializer wrapperInstance = new ShortDeserializer(Short.class, null);
/*     */     
/*     */     public ShortDeserializer(Class<Short> cls, Short nvl)
/*     */     {
/* 224 */       super(nvl);
/*     */     }
/*     */     
/*     */ 
/*     */     public Short deserialize(JsonParser jp, DeserializationContext ctxt)
/*     */       throws IOException
/*     */     {
/* 231 */       return _parseShort(jp, ctxt);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @JacksonStdImpl
/*     */   public static class CharacterDeserializer
/*     */     extends PrimitiveOrWrapperDeserializer<Character>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/* 241 */     static final CharacterDeserializer primitiveInstance = new CharacterDeserializer(Character.TYPE, Character.valueOf('\000'));
/* 242 */     static final CharacterDeserializer wrapperInstance = new CharacterDeserializer(Character.class, null);
/*     */     
/*     */     public CharacterDeserializer(Class<Character> cls, Character nvl)
/*     */     {
/* 246 */       super(nvl);
/*     */     }
/*     */     
/*     */ 
/*     */     public Character deserialize(JsonParser p, DeserializationContext ctxt)
/*     */       throws IOException
/*     */     {
/* 253 */       switch (p.getCurrentTokenId()) {
/*     */       case 7: 
/* 255 */         int value = p.getIntValue();
/* 256 */         if ((value >= 0) && (value <= 65535)) {
/* 257 */           return Character.valueOf((char)value);
/*     */         }
/*     */         
/*     */         break;
/*     */       case 6: 
/* 262 */         String text = p.getText();
/* 263 */         if (text.length() == 1) {
/* 264 */           return Character.valueOf(text.charAt(0));
/*     */         }
/*     */         
/* 267 */         if (text.length() == 0) {
/* 268 */           return (Character)getEmptyValue(ctxt);
/*     */         }
/*     */         break;
/*     */       case 3: 
/* 272 */         if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 273 */           p.nextToken();
/* 274 */           Character C = deserialize(p, ctxt);
/* 275 */           if (p.nextToken() != JsonToken.END_ARRAY) {
/* 276 */             handleMissingEndArrayForSingle(p, ctxt);
/*     */           }
/* 278 */           return C;
/*     */         }
/*     */         break;
/*     */       }
/* 282 */       return (Character)ctxt.handleUnexpectedToken(this._valueClass, p);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @JacksonStdImpl
/*     */   public static final class IntegerDeserializer
/*     */     extends PrimitiveOrWrapperDeserializer<Integer>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/* 292 */     static final IntegerDeserializer primitiveInstance = new IntegerDeserializer(Integer.TYPE, Integer.valueOf(0));
/* 293 */     static final IntegerDeserializer wrapperInstance = new IntegerDeserializer(Integer.class, null);
/*     */     
/*     */     public IntegerDeserializer(Class<Integer> cls, Integer nvl) {
/* 296 */       super(nvl);
/*     */     }
/*     */     
/*     */     public boolean isCachable()
/*     */     {
/* 301 */       return true;
/*     */     }
/*     */     
/*     */     public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 305 */       if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
/* 306 */         return Integer.valueOf(p.getIntValue());
/*     */       }
/* 308 */       return _parseInteger(p, ctxt);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public Integer deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
/*     */       throws IOException
/*     */     {
/* 317 */       if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
/* 318 */         return Integer.valueOf(p.getIntValue());
/*     */       }
/* 320 */       return _parseInteger(p, ctxt);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @JacksonStdImpl
/*     */   public static final class LongDeserializer
/*     */     extends PrimitiveOrWrapperDeserializer<Long>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/* 330 */     static final LongDeserializer primitiveInstance = new LongDeserializer(Long.TYPE, Long.valueOf(0L));
/* 331 */     static final LongDeserializer wrapperInstance = new LongDeserializer(Long.class, null);
/*     */     
/*     */     public LongDeserializer(Class<Long> cls, Long nvl) {
/* 334 */       super(nvl);
/*     */     }
/*     */     
/*     */     public boolean isCachable()
/*     */     {
/* 339 */       return true;
/*     */     }
/*     */     
/*     */     public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 343 */       if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
/* 344 */         return Long.valueOf(p.getLongValue());
/*     */       }
/* 346 */       return _parseLong(p, ctxt);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @JacksonStdImpl
/*     */   public static class FloatDeserializer
/*     */     extends PrimitiveOrWrapperDeserializer<Float>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/* 356 */     static final FloatDeserializer primitiveInstance = new FloatDeserializer(Float.TYPE, Float.valueOf(0.0F));
/* 357 */     static final FloatDeserializer wrapperInstance = new FloatDeserializer(Float.class, null);
/*     */     
/*     */     public FloatDeserializer(Class<Float> cls, Float nvl) {
/* 360 */       super(nvl);
/*     */     }
/*     */     
/*     */     public Float deserialize(JsonParser p, DeserializationContext ctxt)
/*     */       throws IOException
/*     */     {
/* 366 */       return _parseFloat(p, ctxt);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @JacksonStdImpl
/*     */   public static class DoubleDeserializer
/*     */     extends PrimitiveOrWrapperDeserializer<Double>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/* 376 */     static final DoubleDeserializer primitiveInstance = new DoubleDeserializer(Double.TYPE, Double.valueOf(0.0D));
/* 377 */     static final DoubleDeserializer wrapperInstance = new DoubleDeserializer(Double.class, null);
/*     */     
/*     */     public DoubleDeserializer(Class<Double> cls, Double nvl) {
/* 380 */       super(nvl);
/*     */     }
/*     */     
/*     */     public Double deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
/*     */     {
/* 385 */       return _parseDouble(jp, ctxt);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public Double deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
/*     */       throws IOException
/*     */     {
/* 394 */       return _parseDouble(jp, ctxt);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @JacksonStdImpl
/*     */   public static class NumberDeserializer
/*     */     extends StdScalarDeserializer<Object>
/*     */   {
/* 413 */     public static final NumberDeserializer instance = new NumberDeserializer();
/*     */     
/*     */     public NumberDeserializer() {
/* 416 */       super();
/*     */     }
/*     */     
/*     */     public Object deserialize(JsonParser p, DeserializationContext ctxt)
/*     */       throws IOException
/*     */     {
/* 422 */       switch (p.getCurrentTokenId()) {
/*     */       case 7: 
/* 424 */         if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
/* 425 */           return _coerceIntegral(p, ctxt);
/*     */         }
/* 427 */         return p.getNumberValue();
/*     */       
/*     */       case 8: 
/* 430 */         if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
/* 431 */           return p.getDecimalValue();
/*     */         }
/* 433 */         return Double.valueOf(p.getDoubleValue());
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       case 6: 
/* 439 */         String text = p.getText().trim();
/* 440 */         if (text.length() == 0) {
/* 441 */           return getEmptyValue(ctxt);
/*     */         }
/* 443 */         if (_hasTextualNull(text)) {
/* 444 */           return getNullValue(ctxt);
/*     */         }
/* 446 */         if (_isPosInf(text)) {
/* 447 */           return Double.valueOf(Double.POSITIVE_INFINITY);
/*     */         }
/* 449 */         if (_isNegInf(text)) {
/* 450 */           return Double.valueOf(Double.NEGATIVE_INFINITY);
/*     */         }
/* 452 */         if (_isNaN(text)) {
/* 453 */           return Double.valueOf(NaN.0D);
/*     */         }
/*     */         try {
/* 456 */           if (!_isIntNumber(text)) {
/* 457 */             if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
/* 458 */               return new BigDecimal(text);
/*     */             }
/* 460 */             return new Double(text);
/*     */           }
/* 462 */           if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
/* 463 */             return new BigInteger(text);
/*     */           }
/* 465 */           long value = Long.parseLong(text);
/* 466 */           if ((!ctxt.isEnabled(DeserializationFeature.USE_LONG_FOR_INTS)) && 
/* 467 */             (value <= 2147483647L) && (value >= -2147483648L)) {
/* 468 */             return Integer.valueOf((int)value);
/*     */           }
/*     */           
/* 471 */           return Long.valueOf(value);
/*     */         } catch (IllegalArgumentException iae) {
/* 473 */           return ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid number", new Object[0]);
/*     */         }
/*     */       
/*     */       case 3: 
/* 477 */         if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 478 */           p.nextToken();
/* 479 */           Object value = deserialize(p, ctxt);
/* 480 */           if (p.nextToken() != JsonToken.END_ARRAY) {
/* 481 */             handleMissingEndArrayForSingle(p, ctxt);
/*     */           }
/* 483 */           return value;
/*     */         }
/*     */         break;
/*     */       }
/*     */       
/* 488 */       return ctxt.handleUnexpectedToken(this._valueClass, p);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
/*     */       throws IOException
/*     */     {
/* 502 */       switch (jp.getCurrentTokenId())
/*     */       {
/*     */       case 6: 
/*     */       case 7: 
/*     */       case 8: 
/* 507 */         return deserialize(jp, ctxt);
/*     */       }
/* 509 */       return typeDeserializer.deserializeTypedFromScalar(jp, ctxt);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @JacksonStdImpl
/*     */   public static class BigIntegerDeserializer
/*     */     extends StdScalarDeserializer<BigInteger>
/*     */   {
/* 529 */     public static final BigIntegerDeserializer instance = new BigIntegerDeserializer();
/*     */     
/* 531 */     public BigIntegerDeserializer() { super(); }
/*     */     
/*     */ 
/*     */     public BigInteger deserialize(JsonParser p, DeserializationContext ctxt)
/*     */       throws IOException
/*     */     {
/* 537 */       switch (p.getCurrentTokenId()) {
/*     */       case 7: 
/* 539 */         switch (NumberDeserializers.1.$SwitchMap$com$fasterxml$jackson$core$JsonParser$NumberType[p.getNumberType().ordinal()]) {
/*     */         case 1: 
/*     */         case 2: 
/*     */         case 3: 
/* 543 */           return p.getBigIntegerValue();
/*     */         }
/* 545 */         break;
/*     */       case 8: 
/* 547 */         if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
/* 548 */           _failDoubleToIntCoercion(p, ctxt, "java.math.BigInteger");
/*     */         }
/* 550 */         return p.getDecimalValue().toBigInteger();
/*     */       case 3: 
/* 552 */         if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 553 */           p.nextToken();
/* 554 */           BigInteger value = deserialize(p, ctxt);
/* 555 */           if (p.nextToken() != JsonToken.END_ARRAY) {
/* 556 */             handleMissingEndArrayForSingle(p, ctxt);
/*     */           }
/* 558 */           return value;
/*     */         }
/*     */         break;
/*     */       case 6: 
/* 562 */         String text = p.getText().trim();
/* 563 */         if (text.length() == 0) {
/* 564 */           return null;
/*     */         }
/*     */         try {
/* 567 */           return new BigInteger(text);
/*     */         } catch (IllegalArgumentException iae) {
/* 569 */           return (BigInteger)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid representation", new Object[0]);
/*     */         }
/*     */       }
/*     */       
/*     */       
/* 574 */       return (BigInteger)ctxt.handleUnexpectedToken(this._valueClass, p);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @JacksonStdImpl
/*     */   public static class BigDecimalDeserializer
/*     */     extends StdScalarDeserializer<BigDecimal>
/*     */   {
/* 583 */     public static final BigDecimalDeserializer instance = new BigDecimalDeserializer();
/*     */     
/* 585 */     public BigDecimalDeserializer() { super(); }
/*     */     
/*     */ 
/*     */     public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt)
/*     */       throws IOException
/*     */     {
/* 591 */       switch (p.getCurrentTokenId()) {
/*     */       case 7: 
/*     */       case 8: 
/* 594 */         return p.getDecimalValue();
/*     */       case 6: 
/* 596 */         String text = p.getText().trim();
/* 597 */         if (text.length() == 0) {
/* 598 */           return null;
/*     */         }
/*     */         try {
/* 601 */           return new BigDecimal(text);
/*     */         } catch (IllegalArgumentException iae) {
/* 603 */           return (BigDecimal)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid representation", new Object[0]);
/*     */         }
/*     */       
/*     */       case 3: 
/* 607 */         if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 608 */           p.nextToken();
/* 609 */           BigDecimal value = deserialize(p, ctxt);
/* 610 */           if (p.nextToken() != JsonToken.END_ARRAY) {
/* 611 */             handleMissingEndArrayForSingle(p, ctxt);
/*     */           }
/* 613 */           return value;
/*     */         }
/*     */         break;
/*     */       }
/*     */       
/* 618 */       return (BigDecimal)ctxt.handleUnexpectedToken(this._valueClass, p);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\NumberDeserializers.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */