/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ResolvableDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ObjectBuffer;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @JacksonStdImpl
/*     */ public class UntypedObjectDeserializer
/*     */   extends StdDeserializer<Object>
/*     */   implements ResolvableDeserializer, ContextualDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  38 */   protected static final Object[] NO_OBJECTS = new Object[0];
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*  44 */   public static final UntypedObjectDeserializer instance = new UntypedObjectDeserializer(null, null);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected JsonDeserializer<Object> _mapDeserializer;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected JsonDeserializer<Object> _listDeserializer;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected JsonDeserializer<Object> _stringDeserializer;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected JsonDeserializer<Object> _numberDeserializer;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected JavaType _listType;
/*     */   
/*     */ 
/*     */ 
/*     */   protected JavaType _mapType;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public UntypedObjectDeserializer()
/*     */   {
/*  81 */     this(null, null);
/*     */   }
/*     */   
/*     */   public UntypedObjectDeserializer(JavaType listType, JavaType mapType) {
/*  85 */     super(Object.class);
/*  86 */     this._listType = listType;
/*  87 */     this._mapType = mapType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public UntypedObjectDeserializer(UntypedObjectDeserializer base, JsonDeserializer<?> mapDeser, JsonDeserializer<?> listDeser, JsonDeserializer<?> stringDeser, JsonDeserializer<?> numberDeser)
/*     */   {
/*  95 */     super(Object.class);
/*  96 */     this._mapDeserializer = mapDeser;
/*  97 */     this._listDeserializer = listDeser;
/*  98 */     this._stringDeserializer = stringDeser;
/*  99 */     this._numberDeserializer = numberDeser;
/* 100 */     this._listType = base._listType;
/* 101 */     this._mapType = base._mapType;
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
/*     */   public void resolve(DeserializationContext ctxt)
/*     */     throws JsonMappingException
/*     */   {
/* 119 */     JavaType obType = ctxt.constructType(Object.class);
/* 120 */     JavaType stringType = ctxt.constructType(String.class);
/* 121 */     TypeFactory tf = ctxt.getTypeFactory();
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
/* 133 */     if (this._listType == null) {
/* 134 */       this._listDeserializer = _clearIfStdImpl(_findCustomDeser(ctxt, tf.constructCollectionType(List.class, obType)));
/*     */     }
/*     */     else {
/* 137 */       this._listDeserializer = _findCustomDeser(ctxt, this._listType);
/*     */     }
/* 139 */     if (this._mapType == null) {
/* 140 */       this._mapDeserializer = _clearIfStdImpl(_findCustomDeser(ctxt, tf.constructMapType(Map.class, stringType, obType)));
/*     */     }
/*     */     else {
/* 143 */       this._mapDeserializer = _findCustomDeser(ctxt, this._mapType);
/*     */     }
/* 145 */     this._stringDeserializer = _clearIfStdImpl(_findCustomDeser(ctxt, stringType));
/* 146 */     this._numberDeserializer = _clearIfStdImpl(_findCustomDeser(ctxt, tf.constructType(Number.class)));
/*     */     
/*     */ 
/*     */ 
/* 150 */     JavaType unknown = TypeFactory.unknownType();
/* 151 */     this._mapDeserializer = ctxt.handleSecondaryContextualization(this._mapDeserializer, null, unknown);
/* 152 */     this._listDeserializer = ctxt.handleSecondaryContextualization(this._listDeserializer, null, unknown);
/* 153 */     this._stringDeserializer = ctxt.handleSecondaryContextualization(this._stringDeserializer, null, unknown);
/* 154 */     this._numberDeserializer = ctxt.handleSecondaryContextualization(this._numberDeserializer, null, unknown);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected JsonDeserializer<Object> _findCustomDeser(DeserializationContext ctxt, JavaType type)
/*     */     throws JsonMappingException
/*     */   {
/* 162 */     return ctxt.findNonContextualValueDeserializer(type);
/*     */   }
/*     */   
/*     */   protected JsonDeserializer<Object> _clearIfStdImpl(JsonDeserializer<Object> deser) {
/* 166 */     return ClassUtil.isJacksonStdImpl(deser) ? null : deser;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/* 179 */     if ((this._stringDeserializer == null) && (this._numberDeserializer == null) && (this._mapDeserializer == null) && (this._listDeserializer == null) && (getClass() == UntypedObjectDeserializer.class))
/*     */     {
/*     */ 
/* 182 */       return Vanilla.std;
/*     */     }
/* 184 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   protected JsonDeserializer<?> _withResolved(JsonDeserializer<?> mapDeser, JsonDeserializer<?> listDeser, JsonDeserializer<?> stringDeser, JsonDeserializer<?> numberDeser)
/*     */   {
/* 190 */     return new UntypedObjectDeserializer(this, mapDeser, listDeser, stringDeser, numberDeser);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isCachable()
/*     */   {
/* 210 */     return true;
/*     */   }
/*     */   
/*     */   public Object deserialize(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 216 */     switch (p.getCurrentTokenId())
/*     */     {
/*     */ 
/*     */     case 1: 
/*     */     case 2: 
/*     */     case 5: 
/* 222 */       if (this._mapDeserializer != null) {
/* 223 */         return this._mapDeserializer.deserialize(p, ctxt);
/*     */       }
/* 225 */       return mapObject(p, ctxt);
/*     */     case 3: 
/* 227 */       if (ctxt.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)) {
/* 228 */         return mapArrayToArray(p, ctxt);
/*     */       }
/* 230 */       if (this._listDeserializer != null) {
/* 231 */         return this._listDeserializer.deserialize(p, ctxt);
/*     */       }
/* 233 */       return mapArray(p, ctxt);
/*     */     case 12: 
/* 235 */       return p.getEmbeddedObject();
/*     */     case 6: 
/* 237 */       if (this._stringDeserializer != null) {
/* 238 */         return this._stringDeserializer.deserialize(p, ctxt);
/*     */       }
/* 240 */       return p.getText();
/*     */     
/*     */     case 7: 
/* 243 */       if (this._numberDeserializer != null) {
/* 244 */         return this._numberDeserializer.deserialize(p, ctxt);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 249 */       if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
/* 250 */         return _coerceIntegral(p, ctxt);
/*     */       }
/* 252 */       return p.getNumberValue();
/*     */     
/*     */     case 8: 
/* 255 */       if (this._numberDeserializer != null) {
/* 256 */         return this._numberDeserializer.deserialize(p, ctxt);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 261 */       if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
/* 262 */         return p.getDecimalValue();
/*     */       }
/* 264 */       return Double.valueOf(p.getDoubleValue());
/*     */     
/*     */     case 9: 
/* 267 */       return Boolean.TRUE;
/*     */     case 10: 
/* 269 */       return Boolean.FALSE;
/*     */     
/*     */     case 11: 
/* 272 */       return null;
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 277 */     return ctxt.handleUnexpectedToken(Object.class, p);
/*     */   }
/*     */   
/*     */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
/*     */     throws IOException
/*     */   {
/* 283 */     switch (p.getCurrentTokenId())
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */     case 1: 
/*     */     case 3: 
/*     */     case 5: 
/* 291 */       return typeDeserializer.deserializeTypedFromAny(p, ctxt);
/*     */     
/*     */     case 12: 
/* 294 */       return p.getEmbeddedObject();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     case 6: 
/* 300 */       if (this._stringDeserializer != null) {
/* 301 */         return this._stringDeserializer.deserialize(p, ctxt);
/*     */       }
/* 303 */       return p.getText();
/*     */     
/*     */     case 7: 
/* 306 */       if (this._numberDeserializer != null) {
/* 307 */         return this._numberDeserializer.deserialize(p, ctxt);
/*     */       }
/*     */       
/* 310 */       if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
/* 311 */         return _coerceIntegral(p, ctxt);
/*     */       }
/* 313 */       return p.getNumberValue();
/*     */     
/*     */     case 8: 
/* 316 */       if (this._numberDeserializer != null) {
/* 317 */         return this._numberDeserializer.deserialize(p, ctxt);
/*     */       }
/* 319 */       if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
/* 320 */         return p.getDecimalValue();
/*     */       }
/* 322 */       return Double.valueOf(p.getDoubleValue());
/*     */     
/*     */     case 9: 
/* 325 */       return Boolean.TRUE;
/*     */     case 10: 
/* 327 */       return Boolean.FALSE;
/*     */     
/*     */     case 11: 
/* 330 */       return null;
/*     */     }
/*     */     
/* 333 */     return ctxt.handleUnexpectedToken(Object.class, p);
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
/*     */   protected Object mapArray(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 348 */     if (p.nextToken() == JsonToken.END_ARRAY) {
/* 349 */       return new ArrayList(2);
/*     */     }
/* 351 */     Object value = deserialize(p, ctxt);
/* 352 */     if (p.nextToken() == JsonToken.END_ARRAY) {
/* 353 */       ArrayList<Object> l = new ArrayList(2);
/* 354 */       l.add(value);
/* 355 */       return l;
/*     */     }
/* 357 */     Object value2 = deserialize(p, ctxt);
/* 358 */     if (p.nextToken() == JsonToken.END_ARRAY) {
/* 359 */       ArrayList<Object> l = new ArrayList(2);
/* 360 */       l.add(value);
/* 361 */       l.add(value2);
/* 362 */       return l;
/*     */     }
/* 364 */     ObjectBuffer buffer = ctxt.leaseObjectBuffer();
/* 365 */     Object[] values = buffer.resetAndStart();
/* 366 */     int ptr = 0;
/* 367 */     values[(ptr++)] = value;
/* 368 */     values[(ptr++)] = value2;
/* 369 */     int totalSize = ptr;
/*     */     do {
/* 371 */       value = deserialize(p, ctxt);
/* 372 */       totalSize++;
/* 373 */       if (ptr >= values.length) {
/* 374 */         values = buffer.appendCompletedChunk(values);
/* 375 */         ptr = 0;
/*     */       }
/* 377 */       values[(ptr++)] = value;
/* 378 */     } while (p.nextToken() != JsonToken.END_ARRAY);
/*     */     
/* 380 */     ArrayList<Object> result = new ArrayList(totalSize);
/* 381 */     buffer.completeAndClearBuffer(values, ptr, result);
/* 382 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Object mapObject(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 392 */     JsonToken t = p.getCurrentToken();
/*     */     String key1;
/* 394 */     String key1; if (t == JsonToken.START_OBJECT) {
/* 395 */       key1 = p.nextFieldName(); } else { String key1;
/* 396 */       if (t == JsonToken.FIELD_NAME) {
/* 397 */         key1 = p.getCurrentName();
/*     */       } else {
/* 399 */         if (t != JsonToken.END_OBJECT) {
/* 400 */           return ctxt.handleUnexpectedToken(handledType(), p);
/*     */         }
/* 402 */         key1 = null;
/*     */       } }
/* 404 */     if (key1 == null)
/*     */     {
/* 406 */       return new LinkedHashMap(2);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 411 */     p.nextToken();
/* 412 */     Object value1 = deserialize(p, ctxt);
/*     */     
/* 414 */     String key2 = p.nextFieldName();
/* 415 */     if (key2 == null)
/*     */     {
/* 417 */       LinkedHashMap<String, Object> result = new LinkedHashMap(2);
/* 418 */       result.put(key1, value1);
/* 419 */       return result;
/*     */     }
/* 421 */     p.nextToken();
/* 422 */     Object value2 = deserialize(p, ctxt);
/*     */     
/* 424 */     String key = p.nextFieldName();
/*     */     
/* 426 */     if (key == null) {
/* 427 */       LinkedHashMap<String, Object> result = new LinkedHashMap(4);
/* 428 */       result.put(key1, value1);
/* 429 */       result.put(key2, value2);
/* 430 */       return result;
/*     */     }
/*     */     
/* 433 */     LinkedHashMap<String, Object> result = new LinkedHashMap();
/* 434 */     result.put(key1, value1);
/* 435 */     result.put(key2, value2);
/*     */     do
/*     */     {
/* 438 */       p.nextToken();
/* 439 */       result.put(key, deserialize(p, ctxt));
/* 440 */     } while ((key = p.nextFieldName()) != null);
/* 441 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Object[] mapArrayToArray(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 450 */     if (p.nextToken() == JsonToken.END_ARRAY) {
/* 451 */       return NO_OBJECTS;
/*     */     }
/* 453 */     ObjectBuffer buffer = ctxt.leaseObjectBuffer();
/* 454 */     Object[] values = buffer.resetAndStart();
/* 455 */     int ptr = 0;
/*     */     do {
/* 457 */       Object value = deserialize(p, ctxt);
/* 458 */       if (ptr >= values.length) {
/* 459 */         values = buffer.appendCompletedChunk(values);
/* 460 */         ptr = 0;
/*     */       }
/* 462 */       values[(ptr++)] = value;
/* 463 */     } while (p.nextToken() != JsonToken.END_ARRAY);
/* 464 */     return buffer.completeAndClearBuffer(values, ptr);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @JacksonStdImpl
/*     */   public static class Vanilla
/*     */     extends StdDeserializer<Object>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 480 */     public static final Vanilla std = new Vanilla();
/*     */     
/* 482 */     public Vanilla() { super(); }
/*     */     
/*     */     public Object deserialize(JsonParser p, DeserializationContext ctxt)
/*     */       throws IOException
/*     */     {
/* 487 */       switch (p.getCurrentTokenId())
/*     */       {
/*     */       case 1: 
/* 490 */         JsonToken t = p.nextToken();
/* 491 */         if (t == JsonToken.END_OBJECT) {
/* 492 */           return new LinkedHashMap(2);
/*     */         }
/*     */       
/*     */       case 5: 
/* 496 */         return mapObject(p, ctxt);
/*     */       
/*     */       case 3: 
/* 499 */         JsonToken t = p.nextToken();
/* 500 */         if (t == JsonToken.END_ARRAY) {
/* 501 */           if (ctxt.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)) {
/* 502 */             return UntypedObjectDeserializer.NO_OBJECTS;
/*     */           }
/* 504 */           return new ArrayList(2);
/*     */         }
/*     */         
/* 507 */         if (ctxt.isEnabled(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY)) {
/* 508 */           return mapArrayToArray(p, ctxt);
/*     */         }
/* 510 */         return mapArray(p, ctxt);
/*     */       case 12: 
/* 512 */         return p.getEmbeddedObject();
/*     */       case 6: 
/* 514 */         return p.getText();
/*     */       
/*     */       case 7: 
/* 517 */         if (ctxt.hasSomeOfFeatures(F_MASK_INT_COERCIONS)) {
/* 518 */           return _coerceIntegral(p, ctxt);
/*     */         }
/* 520 */         return p.getNumberValue();
/*     */       
/*     */       case 8: 
/* 523 */         if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
/* 524 */           return p.getDecimalValue();
/*     */         }
/* 526 */         return Double.valueOf(p.getDoubleValue());
/*     */       
/*     */       case 9: 
/* 529 */         return Boolean.TRUE;
/*     */       case 10: 
/* 531 */         return Boolean.FALSE;
/*     */       
/*     */       case 11: 
/* 534 */         return null;
/*     */       
/*     */ 
/*     */ 
/*     */       case 2: 
/* 539 */         return new LinkedHashMap(2);
/*     */       }
/*     */       
/*     */       
/*     */ 
/* 544 */       return ctxt.handleUnexpectedToken(Object.class, p);
/*     */     }
/*     */     
/*     */     public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
/*     */       throws IOException
/*     */     {
/* 550 */       switch (p.getCurrentTokenId()) {
/*     */       case 1: 
/*     */       case 3: 
/*     */       case 5: 
/* 554 */         return typeDeserializer.deserializeTypedFromAny(p, ctxt);
/*     */       
/*     */       case 6: 
/* 557 */         return p.getText();
/*     */       
/*     */       case 7: 
/* 560 */         if (ctxt.isEnabled(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)) {
/* 561 */           return p.getBigIntegerValue();
/*     */         }
/* 563 */         return p.getNumberValue();
/*     */       
/*     */       case 8: 
/* 566 */         if (ctxt.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)) {
/* 567 */           return p.getDecimalValue();
/*     */         }
/* 569 */         return Double.valueOf(p.getDoubleValue());
/*     */       
/*     */       case 9: 
/* 572 */         return Boolean.TRUE;
/*     */       case 10: 
/* 574 */         return Boolean.FALSE;
/*     */       case 12: 
/* 576 */         return p.getEmbeddedObject();
/*     */       
/*     */       case 11: 
/* 579 */         return null;
/*     */       }
/*     */       
/* 582 */       return ctxt.handleUnexpectedToken(Object.class, p);
/*     */     }
/*     */     
/*     */     protected Object mapArray(JsonParser p, DeserializationContext ctxt) throws IOException
/*     */     {
/* 587 */       Object value = deserialize(p, ctxt);
/* 588 */       if (p.nextToken() == JsonToken.END_ARRAY) {
/* 589 */         ArrayList<Object> l = new ArrayList(2);
/* 590 */         l.add(value);
/* 591 */         return l;
/*     */       }
/* 593 */       Object value2 = deserialize(p, ctxt);
/* 594 */       if (p.nextToken() == JsonToken.END_ARRAY) {
/* 595 */         ArrayList<Object> l = new ArrayList(2);
/* 596 */         l.add(value);
/* 597 */         l.add(value2);
/* 598 */         return l;
/*     */       }
/* 600 */       ObjectBuffer buffer = ctxt.leaseObjectBuffer();
/* 601 */       Object[] values = buffer.resetAndStart();
/* 602 */       int ptr = 0;
/* 603 */       values[(ptr++)] = value;
/* 604 */       values[(ptr++)] = value2;
/* 605 */       int totalSize = ptr;
/*     */       do {
/* 607 */         value = deserialize(p, ctxt);
/* 608 */         totalSize++;
/* 609 */         if (ptr >= values.length) {
/* 610 */           values = buffer.appendCompletedChunk(values);
/* 611 */           ptr = 0;
/*     */         }
/* 613 */         values[(ptr++)] = value;
/* 614 */       } while (p.nextToken() != JsonToken.END_ARRAY);
/*     */       
/* 616 */       ArrayList<Object> result = new ArrayList(totalSize);
/* 617 */       buffer.completeAndClearBuffer(values, ptr, result);
/* 618 */       return result;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     protected Object mapObject(JsonParser p, DeserializationContext ctxt)
/*     */       throws IOException
/*     */     {
/* 627 */       String key1 = p.getText();
/* 628 */       p.nextToken();
/* 629 */       Object value1 = deserialize(p, ctxt);
/*     */       
/* 631 */       String key2 = p.nextFieldName();
/* 632 */       if (key2 == null) {
/* 633 */         LinkedHashMap<String, Object> result = new LinkedHashMap(2);
/* 634 */         result.put(key1, value1);
/* 635 */         return result;
/*     */       }
/* 637 */       p.nextToken();
/* 638 */       Object value2 = deserialize(p, ctxt);
/*     */       
/* 640 */       String key = p.nextFieldName();
/* 641 */       if (key == null) {
/* 642 */         LinkedHashMap<String, Object> result = new LinkedHashMap(4);
/* 643 */         result.put(key1, value1);
/* 644 */         result.put(key2, value2);
/* 645 */         return result;
/*     */       }
/*     */       
/* 648 */       LinkedHashMap<String, Object> result = new LinkedHashMap();
/* 649 */       result.put(key1, value1);
/* 650 */       result.put(key2, value2);
/*     */       do {
/* 652 */         p.nextToken();
/* 653 */         result.put(key, deserialize(p, ctxt));
/* 654 */       } while ((key = p.nextFieldName()) != null);
/* 655 */       return result;
/*     */     }
/*     */     
/*     */ 
/*     */     protected Object[] mapArrayToArray(JsonParser p, DeserializationContext ctxt)
/*     */       throws IOException
/*     */     {
/* 662 */       ObjectBuffer buffer = ctxt.leaseObjectBuffer();
/* 663 */       Object[] values = buffer.resetAndStart();
/* 664 */       int ptr = 0;
/*     */       do {
/* 666 */         Object value = deserialize(p, ctxt);
/* 667 */         if (ptr >= values.length) {
/* 668 */           values = buffer.appendCompletedChunk(values);
/* 669 */           ptr = 0;
/*     */         }
/* 671 */         values[(ptr++)] = value;
/* 672 */       } while (p.nextToken() != JsonToken.END_ARRAY);
/* 673 */       return buffer.completeAndClearBuffer(values, ptr);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\UntypedObjectDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */