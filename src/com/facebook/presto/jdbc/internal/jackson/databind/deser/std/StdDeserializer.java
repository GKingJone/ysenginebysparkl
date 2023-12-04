/*      */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Feature;
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Value;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser.NumberType;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.io.NumberInput;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.util.Converter;
/*      */ import java.io.IOException;
/*      */ import java.io.Serializable;
/*      */ import java.util.Date;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class StdDeserializer<T>
/*      */   extends JsonDeserializer<T>
/*      */   implements Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 1L;
/*   35 */   protected static final int F_MASK_INT_COERCIONS = DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.getMask() | DeserializationFeature.USE_LONG_FOR_INTS.getMask();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final Class<?> _valueClass;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected StdDeserializer(Class<?> vc)
/*      */   {
/*   48 */     this._valueClass = vc;
/*      */   }
/*      */   
/*      */   protected StdDeserializer(JavaType valueType) {
/*   52 */     this._valueClass = (valueType == null ? null : valueType.getRawClass());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected StdDeserializer(StdDeserializer<?> src)
/*      */   {
/*   62 */     this._valueClass = src._valueClass;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Class<?> handledType()
/*      */   {
/*   72 */     return this._valueClass;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public final Class<?> getValueClass()
/*      */   {
/*   84 */     return this._valueClass;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public JavaType getValueType()
/*      */   {
/*   91 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean isDefaultDeserializer(JsonDeserializer<?> deserializer)
/*      */   {
/*  100 */     return ClassUtil.isJacksonStdImpl(deserializer);
/*      */   }
/*      */   
/*      */   protected boolean isDefaultKeyDeserializer(KeyDeserializer keyDeser) {
/*  104 */     return ClassUtil.isJacksonStdImpl(keyDeser);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
/*      */     throws IOException
/*      */   {
/*  121 */     return typeDeserializer.deserializeTypedFromAny(p, ctxt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final boolean _parseBooleanPrimitive(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/*  134 */     JsonToken t = p.getCurrentToken();
/*  135 */     if (t == JsonToken.VALUE_TRUE) return true;
/*  136 */     if (t == JsonToken.VALUE_FALSE) return false;
/*  137 */     if (t == JsonToken.VALUE_NULL) { return false;
/*      */     }
/*      */     
/*  140 */     if (t == JsonToken.VALUE_NUMBER_INT)
/*      */     {
/*  142 */       if (p.getNumberType() == NumberType.INT) {
/*  143 */         return p.getIntValue() != 0;
/*      */       }
/*  145 */       return _parseBooleanFromOther(p, ctxt);
/*      */     }
/*      */     
/*  148 */     if (t == JsonToken.VALUE_STRING) {
/*  149 */       String text = p.getText().trim();
/*      */       
/*  151 */       if (("true".equals(text)) || ("True".equals(text))) {
/*  152 */         return true;
/*      */       }
/*  154 */       if (("false".equals(text)) || ("False".equals(text)) || (text.length() == 0)) {
/*  155 */         return false;
/*      */       }
/*  157 */       if (_hasTextualNull(text)) {
/*  158 */         return false;
/*      */       }
/*  160 */       Boolean b = (Boolean)ctxt.handleWeirdStringValue(this._valueClass, text, "only \"true\" or \"false\" recognized", new Object[0]);
/*      */       
/*  162 */       return b == null ? false : b.booleanValue();
/*      */     }
/*      */     
/*  165 */     if ((t == JsonToken.START_ARRAY) && (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS))) {
/*  166 */       p.nextToken();
/*  167 */       boolean parsed = _parseBooleanPrimitive(p, ctxt);
/*  168 */       t = p.nextToken();
/*  169 */       if (t != JsonToken.END_ARRAY) {
/*  170 */         handleMissingEndArrayForSingle(p, ctxt);
/*      */       }
/*  172 */       return parsed;
/*      */     }
/*      */     
/*  175 */     return ((Boolean)ctxt.handleUnexpectedToken(this._valueClass, p)).booleanValue();
/*      */   }
/*      */   
/*      */   protected final Boolean _parseBoolean(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/*  181 */     JsonToken t = p.getCurrentToken();
/*  182 */     if (t == JsonToken.VALUE_TRUE) {
/*  183 */       return Boolean.TRUE;
/*      */     }
/*  185 */     if (t == JsonToken.VALUE_FALSE) {
/*  186 */       return Boolean.FALSE;
/*      */     }
/*      */     
/*  189 */     if (t == JsonToken.VALUE_NUMBER_INT)
/*      */     {
/*  191 */       if (p.getNumberType() == NumberType.INT) {
/*  192 */         return p.getIntValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
/*      */       }
/*  194 */       return Boolean.valueOf(_parseBooleanFromOther(p, ctxt));
/*      */     }
/*  196 */     if (t == JsonToken.VALUE_NULL) {
/*  197 */       return (Boolean)getNullValue(ctxt);
/*      */     }
/*      */     
/*  200 */     if (t == JsonToken.VALUE_STRING) {
/*  201 */       String text = p.getText().trim();
/*      */       
/*  203 */       if (("true".equals(text)) || ("True".equals(text))) {
/*  204 */         return Boolean.TRUE;
/*      */       }
/*  206 */       if (("false".equals(text)) || ("False".equals(text))) {
/*  207 */         return Boolean.FALSE;
/*      */       }
/*  209 */       if (text.length() == 0) {
/*  210 */         return (Boolean)getEmptyValue(ctxt);
/*      */       }
/*  212 */       if (_hasTextualNull(text)) {
/*  213 */         return (Boolean)getNullValue(ctxt);
/*      */       }
/*  215 */       return (Boolean)ctxt.handleWeirdStringValue(this._valueClass, text, "only \"true\" or \"false\" recognized", new Object[0]);
/*      */     }
/*      */     
/*      */ 
/*  219 */     if ((t == JsonToken.START_ARRAY) && (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS))) {
/*  220 */       p.nextToken();
/*  221 */       Boolean parsed = _parseBoolean(p, ctxt);
/*  222 */       t = p.nextToken();
/*  223 */       if (t != JsonToken.END_ARRAY) {
/*  224 */         handleMissingEndArrayForSingle(p, ctxt);
/*      */       }
/*  226 */       return parsed;
/*      */     }
/*      */     
/*  229 */     return (Boolean)ctxt.handleUnexpectedToken(this._valueClass, p);
/*      */   }
/*      */   
/*      */   protected final boolean _parseBooleanFromOther(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/*  235 */     if (p.getNumberType() == NumberType.LONG) {
/*  236 */       return (p.getLongValue() == 0L ? Boolean.FALSE : Boolean.TRUE).booleanValue();
/*      */     }
/*      */     
/*  239 */     String str = p.getText();
/*  240 */     if (("0.0".equals(str)) || ("0".equals(str))) {
/*  241 */       return Boolean.FALSE.booleanValue();
/*      */     }
/*  243 */     return Boolean.TRUE.booleanValue();
/*      */   }
/*      */   
/*      */   protected Byte _parseByte(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/*  249 */     JsonToken t = p.getCurrentToken();
/*  250 */     if (t == JsonToken.VALUE_NUMBER_INT) {
/*  251 */       return Byte.valueOf(p.getByteValue());
/*      */     }
/*  253 */     if (t == JsonToken.VALUE_STRING) {
/*  254 */       String text = p.getText().trim();
/*  255 */       if (_hasTextualNull(text)) {
/*  256 */         return (Byte)getNullValue(ctxt);
/*      */       }
/*      */       int value;
/*      */       try {
/*  260 */         int len = text.length();
/*  261 */         if (len == 0) {
/*  262 */           return (Byte)getEmptyValue(ctxt);
/*      */         }
/*  264 */         value = NumberInput.parseInt(text);
/*      */       } catch (IllegalArgumentException iae) {
/*  266 */         return (Byte)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Byte value", new Object[0]);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  271 */       if ((value < -128) || (value > 255)) {
/*  272 */         return (Byte)ctxt.handleWeirdStringValue(this._valueClass, text, "overflow, value can not be represented as 8-bit value", new Object[0]);
/*      */       }
/*      */       
/*      */ 
/*  276 */       return Byte.valueOf((byte)value);
/*      */     }
/*  278 */     if (t == JsonToken.VALUE_NUMBER_FLOAT) {
/*  279 */       if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
/*  280 */         _failDoubleToIntCoercion(p, ctxt, "Byte");
/*      */       }
/*  282 */       return Byte.valueOf(p.getByteValue());
/*      */     }
/*  284 */     if (t == JsonToken.VALUE_NULL) {
/*  285 */       return (Byte)getNullValue(ctxt);
/*      */     }
/*      */     
/*  288 */     if ((t == JsonToken.START_ARRAY) && (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS))) {
/*  289 */       p.nextToken();
/*  290 */       Byte parsed = _parseByte(p, ctxt);
/*  291 */       t = p.nextToken();
/*  292 */       if (t != JsonToken.END_ARRAY) {
/*  293 */         handleMissingEndArrayForSingle(p, ctxt);
/*      */       }
/*  295 */       return parsed;
/*      */     }
/*  297 */     return (Byte)ctxt.handleUnexpectedToken(this._valueClass, p);
/*      */   }
/*      */   
/*      */   protected Short _parseShort(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/*  303 */     JsonToken t = p.getCurrentToken();
/*  304 */     if (t == JsonToken.VALUE_NUMBER_INT) {
/*  305 */       return Short.valueOf(p.getShortValue());
/*      */     }
/*  307 */     if (t == JsonToken.VALUE_STRING) {
/*  308 */       String text = p.getText().trim();
/*      */       int value;
/*      */       try {
/*  311 */         int len = text.length();
/*  312 */         if (len == 0) {
/*  313 */           return (Short)getEmptyValue(ctxt);
/*      */         }
/*  315 */         if (_hasTextualNull(text)) {
/*  316 */           return (Short)getNullValue(ctxt);
/*      */         }
/*  318 */         value = NumberInput.parseInt(text);
/*      */       } catch (IllegalArgumentException iae) {
/*  320 */         return (Short)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Short value", new Object[0]);
/*      */       }
/*      */       
/*      */ 
/*  324 */       if ((value < 32768) || (value > 32767)) {
/*  325 */         return (Short)ctxt.handleWeirdStringValue(this._valueClass, text, "overflow, value can not be represented as 16-bit value", new Object[0]);
/*      */       }
/*      */       
/*  328 */       return Short.valueOf((short)value);
/*      */     }
/*  330 */     if (t == JsonToken.VALUE_NUMBER_FLOAT) {
/*  331 */       if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
/*  332 */         _failDoubleToIntCoercion(p, ctxt, "Short");
/*      */       }
/*  334 */       return Short.valueOf(p.getShortValue());
/*      */     }
/*  336 */     if (t == JsonToken.VALUE_NULL) {
/*  337 */       return (Short)getNullValue(ctxt);
/*      */     }
/*      */     
/*  340 */     if ((t == JsonToken.START_ARRAY) && (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS))) {
/*  341 */       p.nextToken();
/*  342 */       Short parsed = _parseShort(p, ctxt);
/*  343 */       t = p.nextToken();
/*  344 */       if (t != JsonToken.END_ARRAY) {
/*  345 */         handleMissingEndArrayForSingle(p, ctxt);
/*      */       }
/*  347 */       return parsed;
/*      */     }
/*  349 */     return (Short)ctxt.handleUnexpectedToken(this._valueClass, p);
/*      */   }
/*      */   
/*      */   protected final short _parseShortPrimitive(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/*  355 */     int value = _parseIntPrimitive(p, ctxt);
/*      */     
/*  357 */     if ((value < 32768) || (value > 32767)) {
/*  358 */       Number v = (Number)ctxt.handleWeirdStringValue(this._valueClass, String.valueOf(value), "overflow, value can not be represented as 16-bit value", new Object[0]);
/*      */       
/*  360 */       return v == null ? 0 : v.shortValue();
/*      */     }
/*  362 */     return (short)value;
/*      */   }
/*      */   
/*      */   protected final int _parseIntPrimitive(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/*  368 */     if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
/*  369 */       return p.getIntValue();
/*      */     }
/*  371 */     JsonToken t = p.getCurrentToken();
/*  372 */     if (t == JsonToken.VALUE_STRING) {
/*  373 */       String text = p.getText().trim();
/*  374 */       if (_hasTextualNull(text)) {
/*  375 */         return 0;
/*      */       }
/*      */       try {
/*  378 */         int len = text.length();
/*  379 */         if (len > 9) {
/*  380 */           long l = Long.parseLong(text);
/*  381 */           if ((l < -2147483648L) || (l > 2147483647L)) {
/*  382 */             Number v = (Number)ctxt.handleWeirdStringValue(this._valueClass, text, "Overflow: numeric value (%s) out of range of int (%d -%d)", new Object[] { text, Integer.valueOf(Integer.MIN_VALUE), Integer.valueOf(Integer.MAX_VALUE) });
/*      */             
/*      */ 
/*  385 */             return v == null ? 0 : v.intValue();
/*      */           }
/*  387 */           return (int)l;
/*      */         }
/*  389 */         if (len == 0) {
/*  390 */           return 0;
/*      */         }
/*  392 */         return NumberInput.parseInt(text);
/*      */       } catch (IllegalArgumentException iae) {
/*  394 */         Number v = (Number)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid int value", new Object[0]);
/*      */         
/*  396 */         return v == null ? 0 : v.intValue();
/*      */       }
/*      */     }
/*  399 */     if (t == JsonToken.VALUE_NUMBER_FLOAT) {
/*  400 */       if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
/*  401 */         _failDoubleToIntCoercion(p, ctxt, "int");
/*      */       }
/*  403 */       return p.getValueAsInt();
/*      */     }
/*  405 */     if (t == JsonToken.VALUE_NULL) {
/*  406 */       return 0;
/*      */     }
/*  408 */     if ((t == JsonToken.START_ARRAY) && (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS))) {
/*  409 */       p.nextToken();
/*  410 */       int parsed = _parseIntPrimitive(p, ctxt);
/*  411 */       t = p.nextToken();
/*  412 */       if (t != JsonToken.END_ARRAY) {
/*  413 */         handleMissingEndArrayForSingle(p, ctxt);
/*      */       }
/*  415 */       return parsed;
/*      */     }
/*      */     
/*  418 */     return ((Number)ctxt.handleUnexpectedToken(this._valueClass, p)).intValue();
/*      */   }
/*      */   
/*      */   protected final Integer _parseInteger(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/*  424 */     switch (p.getCurrentTokenId())
/*      */     {
/*      */     case 7: 
/*  427 */       return Integer.valueOf(p.getIntValue());
/*      */     case 8: 
/*  429 */       if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
/*  430 */         _failDoubleToIntCoercion(p, ctxt, "Integer");
/*      */       }
/*  432 */       return Integer.valueOf(p.getValueAsInt());
/*      */     case 6: 
/*  434 */       String text = p.getText().trim();
/*      */       try {
/*  436 */         int len = text.length();
/*  437 */         if (_hasTextualNull(text)) {
/*  438 */           return (Integer)getNullValue(ctxt);
/*      */         }
/*  440 */         if (len > 9) {
/*  441 */           long l = Long.parseLong(text);
/*  442 */           if ((l < -2147483648L) || (l > 2147483647L)) {
/*  443 */             return (Integer)ctxt.handleWeirdStringValue(this._valueClass, text, "Overflow: numeric value (" + text + ") out of range of Integer (" + Integer.MIN_VALUE + " - " + Integer.MAX_VALUE + ")", new Object[0]);
/*      */           }
/*      */           
/*      */ 
/*  447 */           return Integer.valueOf((int)l);
/*      */         }
/*  449 */         if (len == 0) {
/*  450 */           return (Integer)getEmptyValue(ctxt);
/*      */         }
/*  452 */         return Integer.valueOf(NumberInput.parseInt(text));
/*      */       } catch (IllegalArgumentException iae) {
/*  454 */         return (Integer)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Integer value", new Object[0]);
/*      */       }
/*      */     
/*      */ 
/*      */     case 11: 
/*  459 */       return (Integer)getNullValue(ctxt);
/*      */     case 3: 
/*  461 */       if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/*  462 */         p.nextToken();
/*  463 */         Integer parsed = _parseInteger(p, ctxt);
/*  464 */         if (p.nextToken() != JsonToken.END_ARRAY) {
/*  465 */           handleMissingEndArrayForSingle(p, ctxt);
/*      */         }
/*  467 */         return parsed;
/*      */       }
/*      */       break;
/*      */     }
/*      */     
/*  472 */     return (Integer)ctxt.handleUnexpectedToken(this._valueClass, p);
/*      */   }
/*      */   
/*      */   protected final Long _parseLong(JsonParser p, DeserializationContext ctxt) throws IOException
/*      */   {
/*  477 */     switch (p.getCurrentTokenId())
/*      */     {
/*      */     case 7: 
/*  480 */       return Long.valueOf(p.getLongValue());
/*      */     case 8: 
/*  482 */       if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
/*  483 */         _failDoubleToIntCoercion(p, ctxt, "Long");
/*      */       }
/*  485 */       return Long.valueOf(p.getValueAsLong());
/*      */     
/*      */ 
/*      */     case 6: 
/*  489 */       String text = p.getText().trim();
/*  490 */       if (text.length() == 0) {
/*  491 */         return (Long)getEmptyValue(ctxt);
/*      */       }
/*  493 */       if (_hasTextualNull(text)) {
/*  494 */         return (Long)getNullValue(ctxt);
/*      */       }
/*      */       try {
/*  497 */         return Long.valueOf(NumberInput.parseLong(text));
/*      */       } catch (IllegalArgumentException iae) {
/*  499 */         return (Long)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Long value", new Object[0]);
/*      */       }
/*      */     
/*      */     case 11: 
/*  503 */       return (Long)getNullValue(ctxt);
/*      */     case 3: 
/*  505 */       if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/*  506 */         p.nextToken();
/*  507 */         Long parsed = _parseLong(p, ctxt);
/*  508 */         JsonToken t = p.nextToken();
/*  509 */         if (t != JsonToken.END_ARRAY) {
/*  510 */           handleMissingEndArrayForSingle(p, ctxt);
/*      */         }
/*  512 */         return parsed;
/*      */       }
/*      */       break;
/*      */     }
/*      */     
/*  517 */     return (Long)ctxt.handleUnexpectedToken(this._valueClass, p);
/*      */   }
/*      */   
/*      */   protected final long _parseLongPrimitive(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/*  523 */     switch (p.getCurrentTokenId()) {
/*      */     case 7: 
/*  525 */       return p.getLongValue();
/*      */     case 8: 
/*  527 */       if (!ctxt.isEnabled(DeserializationFeature.ACCEPT_FLOAT_AS_INT)) {
/*  528 */         _failDoubleToIntCoercion(p, ctxt, "long");
/*      */       }
/*  530 */       return p.getValueAsLong();
/*      */     case 6: 
/*  532 */       String text = p.getText().trim();
/*  533 */       if ((text.length() == 0) || (_hasTextualNull(text))) {
/*  534 */         return 0L;
/*      */       }
/*      */       try {
/*  537 */         return NumberInput.parseLong(text);
/*      */       }
/*      */       catch (IllegalArgumentException iae) {
/*  540 */         Number v = (Number)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid long value", new Object[0]);
/*      */         
/*  542 */         return v == null ? 0L : v.longValue();
/*      */       }
/*      */     case 11: 
/*  545 */       return 0L;
/*      */     case 3: 
/*  547 */       if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/*  548 */         p.nextToken();
/*  549 */         long parsed = _parseLongPrimitive(p, ctxt);
/*  550 */         JsonToken t = p.nextToken();
/*  551 */         if (t != JsonToken.END_ARRAY) {
/*  552 */           handleMissingEndArrayForSingle(p, ctxt);
/*      */         }
/*  554 */         return parsed;
/*      */       }
/*      */       break;
/*      */     }
/*  558 */     return ((Number)ctxt.handleUnexpectedToken(this._valueClass, p)).longValue();
/*      */   }
/*      */   
/*      */ 
/*      */   protected final Float _parseFloat(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/*  565 */     JsonToken t = p.getCurrentToken();
/*      */     
/*  567 */     if ((t == JsonToken.VALUE_NUMBER_INT) || (t == JsonToken.VALUE_NUMBER_FLOAT)) {
/*  568 */       return Float.valueOf(p.getFloatValue());
/*      */     }
/*      */     
/*  571 */     if (t == JsonToken.VALUE_STRING) {
/*  572 */       String text = p.getText().trim();
/*  573 */       if (text.length() == 0) {
/*  574 */         return (Float)getEmptyValue(ctxt);
/*      */       }
/*  576 */       if (_hasTextualNull(text)) {
/*  577 */         return (Float)getNullValue(ctxt);
/*      */       }
/*  579 */       switch (text.charAt(0)) {
/*      */       case 'I': 
/*  581 */         if (_isPosInf(text)) {
/*  582 */           return Float.valueOf(Float.POSITIVE_INFINITY);
/*      */         }
/*      */         break;
/*      */       case 'N': 
/*  586 */         if (_isNaN(text)) {
/*  587 */           return Float.valueOf(NaN.0F);
/*      */         }
/*      */         break;
/*      */       case '-': 
/*  591 */         if (_isNegInf(text)) {
/*  592 */           return Float.valueOf(Float.NEGATIVE_INFINITY);
/*      */         }
/*      */         break;
/*      */       }
/*      */       try {
/*  597 */         return Float.valueOf(Float.parseFloat(text));
/*      */       } catch (IllegalArgumentException iae) {
/*  599 */         return (Float)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Float value", new Object[0]);
/*      */       }
/*      */     }
/*  602 */     if (t == JsonToken.VALUE_NULL) {
/*  603 */       return (Float)getNullValue(ctxt);
/*      */     }
/*  605 */     if ((t == JsonToken.START_ARRAY) && (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS))) {
/*  606 */       p.nextToken();
/*  607 */       Float parsed = _parseFloat(p, ctxt);
/*  608 */       t = p.nextToken();
/*  609 */       if (t != JsonToken.END_ARRAY) {
/*  610 */         handleMissingEndArrayForSingle(p, ctxt);
/*      */       }
/*  612 */       return parsed;
/*      */     }
/*      */     
/*  615 */     return (Float)ctxt.handleUnexpectedToken(this._valueClass, p);
/*      */   }
/*      */   
/*      */   protected final float _parseFloatPrimitive(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/*  621 */     JsonToken t = p.getCurrentToken();
/*      */     
/*  623 */     if ((t == JsonToken.VALUE_NUMBER_INT) || (t == JsonToken.VALUE_NUMBER_FLOAT)) {
/*  624 */       return p.getFloatValue();
/*      */     }
/*  626 */     if (t == JsonToken.VALUE_STRING) {
/*  627 */       String text = p.getText().trim();
/*  628 */       if ((text.length() == 0) || (_hasTextualNull(text))) {
/*  629 */         return 0.0F;
/*      */       }
/*  631 */       switch (text.charAt(0)) {
/*      */       case 'I': 
/*  633 */         if (_isPosInf(text)) {
/*  634 */           return Float.POSITIVE_INFINITY;
/*      */         }
/*      */         break;
/*      */       case 'N': 
/*  638 */         if (_isNaN(text)) return NaN.0F;
/*      */         break;
/*      */       case '-': 
/*  641 */         if (_isNegInf(text)) {
/*  642 */           return Float.NEGATIVE_INFINITY;
/*      */         }
/*      */         break;
/*      */       }
/*      */       try {
/*  647 */         return Float.parseFloat(text);
/*      */       } catch (IllegalArgumentException iae) {
/*  649 */         Number v = (Number)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid float value", new Object[0]);
/*      */         
/*  651 */         return v == null ? 0.0F : v.floatValue();
/*      */       } }
/*  653 */     if (t == JsonToken.VALUE_NULL) {
/*  654 */       return 0.0F;
/*      */     }
/*  656 */     if ((t == JsonToken.START_ARRAY) && (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS))) {
/*  657 */       p.nextToken();
/*  658 */       float parsed = _parseFloatPrimitive(p, ctxt);
/*  659 */       t = p.nextToken();
/*  660 */       if (t != JsonToken.END_ARRAY) {
/*  661 */         handleMissingEndArrayForSingle(p, ctxt);
/*      */       }
/*  663 */       return parsed;
/*      */     }
/*      */     
/*  666 */     return ((Number)ctxt.handleUnexpectedToken(this._valueClass, p)).floatValue();
/*      */   }
/*      */   
/*      */   protected final Double _parseDouble(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/*  672 */     JsonToken t = p.getCurrentToken();
/*      */     
/*  674 */     if ((t == JsonToken.VALUE_NUMBER_INT) || (t == JsonToken.VALUE_NUMBER_FLOAT)) {
/*  675 */       return Double.valueOf(p.getDoubleValue());
/*      */     }
/*  677 */     if (t == JsonToken.VALUE_STRING) {
/*  678 */       String text = p.getText().trim();
/*  679 */       if (text.length() == 0) {
/*  680 */         return (Double)getEmptyValue(ctxt);
/*      */       }
/*  682 */       if (_hasTextualNull(text)) {
/*  683 */         return (Double)getNullValue(ctxt);
/*      */       }
/*  685 */       switch (text.charAt(0)) {
/*      */       case 'I': 
/*  687 */         if (_isPosInf(text)) {
/*  688 */           return Double.valueOf(Double.POSITIVE_INFINITY);
/*      */         }
/*      */         break;
/*      */       case 'N': 
/*  692 */         if (_isNaN(text)) {
/*  693 */           return Double.valueOf(NaN.0D);
/*      */         }
/*      */         break;
/*      */       case '-': 
/*  697 */         if (_isNegInf(text)) {
/*  698 */           return Double.valueOf(Double.NEGATIVE_INFINITY);
/*      */         }
/*      */         break;
/*      */       }
/*      */       try {
/*  703 */         return Double.valueOf(parseDouble(text));
/*      */       } catch (IllegalArgumentException iae) {
/*  705 */         return (Double)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid Double value", new Object[0]);
/*      */       }
/*      */     }
/*  708 */     if (t == JsonToken.VALUE_NULL) {
/*  709 */       return (Double)getNullValue(ctxt);
/*      */     }
/*  711 */     if ((t == JsonToken.START_ARRAY) && (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS))) {
/*  712 */       p.nextToken();
/*  713 */       Double parsed = _parseDouble(p, ctxt);
/*  714 */       t = p.nextToken();
/*  715 */       if (t != JsonToken.END_ARRAY) {
/*  716 */         handleMissingEndArrayForSingle(p, ctxt);
/*      */       }
/*  718 */       return parsed;
/*      */     }
/*      */     
/*  721 */     return (Double)ctxt.handleUnexpectedToken(this._valueClass, p);
/*      */   }
/*      */   
/*      */ 
/*      */   protected final double _parseDoublePrimitive(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/*  728 */     JsonToken t = p.getCurrentToken();
/*      */     
/*  730 */     if ((t == JsonToken.VALUE_NUMBER_INT) || (t == JsonToken.VALUE_NUMBER_FLOAT)) {
/*  731 */       return p.getDoubleValue();
/*      */     }
/*      */     
/*  734 */     if (t == JsonToken.VALUE_STRING) {
/*  735 */       String text = p.getText().trim();
/*  736 */       if ((text.length() == 0) || (_hasTextualNull(text))) {
/*  737 */         return 0.0D;
/*      */       }
/*  739 */       switch (text.charAt(0)) {
/*      */       case 'I': 
/*  741 */         if (_isPosInf(text)) {
/*  742 */           return Double.POSITIVE_INFINITY;
/*      */         }
/*      */         break;
/*      */       case 'N': 
/*  746 */         if (_isNaN(text)) {
/*  747 */           return NaN.0D;
/*      */         }
/*      */         break;
/*      */       case '-': 
/*  751 */         if (_isNegInf(text)) {
/*  752 */           return Double.NEGATIVE_INFINITY;
/*      */         }
/*      */         break;
/*      */       }
/*      */       try {
/*  757 */         return parseDouble(text);
/*      */       } catch (IllegalArgumentException iae) {
/*  759 */         Number v = (Number)ctxt.handleWeirdStringValue(this._valueClass, text, "not a valid double value", new Object[0]);
/*      */         
/*  761 */         return v == null ? 0.0D : v.doubleValue();
/*      */       } }
/*  763 */     if (t == JsonToken.VALUE_NULL) {
/*  764 */       return 0.0D;
/*      */     }
/*      */     
/*  767 */     if ((t == JsonToken.START_ARRAY) && (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS))) {
/*  768 */       p.nextToken();
/*  769 */       double parsed = _parseDoublePrimitive(p, ctxt);
/*  770 */       t = p.nextToken();
/*  771 */       if (t != JsonToken.END_ARRAY) {
/*  772 */         handleMissingEndArrayForSingle(p, ctxt);
/*      */       }
/*  774 */       return parsed;
/*      */     }
/*      */     
/*  777 */     return ((Number)ctxt.handleUnexpectedToken(this._valueClass, p)).doubleValue();
/*      */   }
/*      */   
/*      */   protected Date _parseDate(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/*  783 */     JsonToken t = p.getCurrentToken();
/*  784 */     if (t == JsonToken.VALUE_NUMBER_INT) {
/*  785 */       return new Date(p.getLongValue());
/*      */     }
/*  787 */     if (t == JsonToken.VALUE_NULL) {
/*  788 */       return (Date)getNullValue(ctxt);
/*      */     }
/*  790 */     if (t == JsonToken.VALUE_STRING) {
/*  791 */       return _parseDate(p.getText().trim(), ctxt);
/*      */     }
/*      */     
/*  794 */     if ((t == JsonToken.START_ARRAY) && (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS))) {
/*  795 */       p.nextToken();
/*  796 */       Date parsed = _parseDate(p, ctxt);
/*  797 */       t = p.nextToken();
/*  798 */       if (t != JsonToken.END_ARRAY) {
/*  799 */         handleMissingEndArrayForSingle(p, ctxt);
/*      */       }
/*  801 */       return parsed;
/*      */     }
/*  803 */     return (Date)ctxt.handleUnexpectedToken(this._valueClass, p);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Date _parseDate(String value, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/*      */     try
/*      */     {
/*  814 */       if (value.length() == 0) {
/*  815 */         return (Date)getEmptyValue(ctxt);
/*      */       }
/*  817 */       if (_hasTextualNull(value)) {
/*  818 */         return (Date)getNullValue(ctxt);
/*      */       }
/*  820 */       return ctxt.parseDate(value);
/*      */     } catch (IllegalArgumentException iae) {}
/*  822 */     return (Date)ctxt.handleWeirdStringValue(this._valueClass, value, "not a valid representation (error: %s)", tmp53_50);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static final double parseDouble(String numStr)
/*      */     throws NumberFormatException
/*      */   {
/*  834 */     if ("2.2250738585072012e-308".equals(numStr)) {
/*  835 */       return 2.2250738585072014E-308D;
/*      */     }
/*  837 */     return Double.parseDouble(numStr);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final String _parseString(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/*  848 */     JsonToken t = p.getCurrentToken();
/*  849 */     if (t == JsonToken.VALUE_STRING) {
/*  850 */       return p.getText();
/*      */     }
/*      */     
/*  853 */     if ((t == JsonToken.START_ARRAY) && (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS))) {
/*  854 */       p.nextToken();
/*  855 */       String parsed = _parseString(p, ctxt);
/*  856 */       if (p.nextToken() != JsonToken.END_ARRAY) {
/*  857 */         handleMissingEndArrayForSingle(p, ctxt);
/*      */       }
/*  859 */       return parsed;
/*      */     }
/*  861 */     String value = p.getValueAsString();
/*  862 */     if (value != null) {
/*  863 */       return value;
/*      */     }
/*  865 */     return (String)ctxt.handleUnexpectedToken(String.class, p);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected T _deserializeFromEmpty(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/*  878 */     JsonToken t = p.getCurrentToken();
/*  879 */     if (t == JsonToken.START_ARRAY) {
/*  880 */       if (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
/*  881 */         t = p.nextToken();
/*  882 */         if (t == JsonToken.END_ARRAY) {
/*  883 */           return null;
/*      */         }
/*  885 */         return (T)ctxt.handleUnexpectedToken(handledType(), p);
/*      */       }
/*  887 */     } else if ((t == JsonToken.VALUE_STRING) && 
/*  888 */       (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT))) {
/*  889 */       String str = p.getText().trim();
/*  890 */       if (str.isEmpty()) {
/*  891 */         return null;
/*      */       }
/*      */     }
/*      */     
/*  895 */     return (T)ctxt.handleUnexpectedToken(handledType(), p);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean _hasTextualNull(String value)
/*      */   {
/*  906 */     return "null".equals(value);
/*      */   }
/*      */   
/*      */   protected final boolean _isNegInf(String text) {
/*  910 */     return ("-Infinity".equals(text)) || ("-INF".equals(text));
/*      */   }
/*      */   
/*      */   protected final boolean _isPosInf(String text) {
/*  914 */     return ("Infinity".equals(text)) || ("INF".equals(text));
/*      */   }
/*      */   
/*  917 */   protected final boolean _isNaN(String text) { return "NaN".equals(text); }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Object _coerceIntegral(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/*  938 */     int feats = ctxt.getDeserializationFeatures();
/*  939 */     if (DeserializationFeature.USE_BIG_INTEGER_FOR_INTS.enabledIn(feats)) {
/*  940 */       return p.getBigIntegerValue();
/*      */     }
/*  942 */     if (DeserializationFeature.USE_LONG_FOR_INTS.enabledIn(feats)) {
/*  943 */       return Long.valueOf(p.getLongValue());
/*      */     }
/*  945 */     return p.getBigIntegerValue();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonDeserializer<Object> findDeserializer(DeserializationContext ctxt, JavaType type, BeanProperty property)
/*      */     throws JsonMappingException
/*      */   {
/*  967 */     return ctxt.findContextualValueDeserializer(type, property);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final boolean _isIntNumber(String text)
/*      */   {
/*  976 */     int len = text.length();
/*  977 */     if (len > 0) {
/*  978 */       char c = text.charAt(0);
/*      */       
/*  980 */       for (int i = (c == '-') || (c == '+') ? 1 : 0; 
/*  981 */           i < len; i++) {
/*  982 */         int ch = text.charAt(i);
/*  983 */         if ((ch > 57) || (ch < 48)) {
/*  984 */           return false;
/*      */         }
/*      */       }
/*  987 */       return true;
/*      */     }
/*  989 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonDeserializer<?> findConvertingContentDeserializer(DeserializationContext ctxt, BeanProperty prop, JsonDeserializer<?> existingDeserializer)
/*      */     throws JsonMappingException
/*      */   {
/* 1012 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 1013 */     if ((intr != null) && (prop != null)) {
/* 1014 */       AnnotatedMember member = prop.getMember();
/* 1015 */       if (member != null) {
/* 1016 */         Object convDef = intr.findDeserializationContentConverter(member);
/* 1017 */         if (convDef != null) {
/* 1018 */           Converter<Object, Object> conv = ctxt.converterInstance(prop.getMember(), convDef);
/* 1019 */           JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
/* 1020 */           if (existingDeserializer == null) {
/* 1021 */             existingDeserializer = ctxt.findContextualValueDeserializer(delegateType, prop);
/*      */           }
/* 1023 */           return new StdDelegatingDeserializer(conv, delegateType, existingDeserializer);
/*      */         }
/*      */       }
/*      */     }
/* 1027 */     return existingDeserializer;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonFormat.Value findFormatOverrides(DeserializationContext ctxt, BeanProperty prop, Class<?> typeForDefaults)
/*      */   {
/* 1042 */     if (prop != null) {
/* 1043 */       return prop.findPropertyFormat(ctxt.getConfig(), typeForDefaults);
/*      */     }
/*      */     
/* 1046 */     return ctxt.getDefaultPropertyFormat(typeForDefaults);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Boolean findFormatFeature(DeserializationContext ctxt, BeanProperty prop, Class<?> typeForDefaults, JsonFormat.Feature feat)
/*      */   {
/* 1062 */     JsonFormat.Value format = findFormatOverrides(ctxt, prop, typeForDefaults);
/* 1063 */     if (format != null) {
/* 1064 */       return format.getFeature(feat);
/*      */     }
/* 1066 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void handleUnknownProperty(JsonParser p, DeserializationContext ctxt, Object instanceOrClass, String propName)
/*      */     throws IOException
/*      */   {
/* 1092 */     if (instanceOrClass == null) {
/* 1093 */       instanceOrClass = handledType();
/*      */     }
/*      */     
/* 1096 */     if (ctxt.handleUnknownProperty(p, this, instanceOrClass, propName)) {
/* 1097 */       return;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1102 */     p.skipChildren();
/*      */   }
/*      */   
/*      */   protected void handleMissingEndArrayForSingle(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/* 1108 */     ctxt.reportWrongTokenException(p, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single '%s' value but there was more than a single value in the array", new Object[] { handledType().getName() });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _failDoubleToIntCoercion(JsonParser p, DeserializationContext ctxt, String type)
/*      */     throws IOException
/*      */   {
/* 1118 */     ctxt.reportMappingException("Can not coerce a floating-point value ('%s') into %s; enable `DeserializationFeature.ACCEPT_FLOAT_AS_INT` to allow", new Object[] { p.getValueAsString(), type });
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\StdDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */