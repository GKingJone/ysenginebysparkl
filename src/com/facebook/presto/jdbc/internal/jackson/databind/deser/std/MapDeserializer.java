/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.std;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonIgnoreProperties.Value;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JacksonStdImpl;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ContextualDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ContextualKeyDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ResolvableDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.UnresolvedForwardReference;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ValueInstantiator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.PropertyBasedCreator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.PropertyValueBuffer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ReadableObjectId;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ReadableObjectId.Referring;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ArrayBuilders;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ @JacksonStdImpl
/*     */ public class MapDeserializer
/*     */   extends ContainerDeserializerBase<Map<Object, Object>>
/*     */   implements ContextualDeserializer, ResolvableDeserializer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected final JavaType _mapType;
/*     */   protected final KeyDeserializer _keyDeserializer;
/*     */   protected boolean _standardStringKey;
/*     */   protected final JsonDeserializer<Object> _valueDeserializer;
/*     */   protected final TypeDeserializer _valueTypeDeserializer;
/*     */   protected final ValueInstantiator _valueInstantiator;
/*     */   protected final boolean _hasDefaultCreator;
/*     */   protected JsonDeserializer<Object> _delegateDeserializer;
/*     */   protected PropertyBasedCreator _propertyBasedCreator;
/*     */   protected Set<String> _ignorableProperties;
/*     */   
/*     */   public MapDeserializer(JavaType mapType, ValueInstantiator valueInstantiator, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser)
/*     */   {
/*  99 */     super(mapType);
/* 100 */     this._mapType = mapType;
/* 101 */     this._keyDeserializer = keyDeser;
/* 102 */     this._valueDeserializer = valueDeser;
/* 103 */     this._valueTypeDeserializer = valueTypeDeser;
/* 104 */     this._valueInstantiator = valueInstantiator;
/* 105 */     this._hasDefaultCreator = valueInstantiator.canCreateUsingDefault();
/* 106 */     this._delegateDeserializer = null;
/* 107 */     this._propertyBasedCreator = null;
/* 108 */     this._standardStringKey = _isStdKeyDeser(mapType, keyDeser);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected MapDeserializer(MapDeserializer src)
/*     */   {
/* 117 */     super(src._mapType);
/* 118 */     this._mapType = src._mapType;
/* 119 */     this._keyDeserializer = src._keyDeserializer;
/* 120 */     this._valueDeserializer = src._valueDeserializer;
/* 121 */     this._valueTypeDeserializer = src._valueTypeDeserializer;
/* 122 */     this._valueInstantiator = src._valueInstantiator;
/* 123 */     this._propertyBasedCreator = src._propertyBasedCreator;
/* 124 */     this._delegateDeserializer = src._delegateDeserializer;
/* 125 */     this._hasDefaultCreator = src._hasDefaultCreator;
/*     */     
/* 127 */     this._ignorableProperties = src._ignorableProperties;
/*     */     
/* 129 */     this._standardStringKey = src._standardStringKey;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected MapDeserializer(MapDeserializer src, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, Set<String> ignorable)
/*     */   {
/* 137 */     super(src._mapType);
/* 138 */     this._mapType = src._mapType;
/* 139 */     this._keyDeserializer = keyDeser;
/* 140 */     this._valueDeserializer = valueDeser;
/* 141 */     this._valueTypeDeserializer = valueTypeDeser;
/* 142 */     this._valueInstantiator = src._valueInstantiator;
/* 143 */     this._propertyBasedCreator = src._propertyBasedCreator;
/* 144 */     this._delegateDeserializer = src._delegateDeserializer;
/* 145 */     this._hasDefaultCreator = src._hasDefaultCreator;
/* 146 */     this._ignorableProperties = ignorable;
/*     */     
/* 148 */     this._standardStringKey = _isStdKeyDeser(this._mapType, keyDeser);
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
/*     */   protected MapDeserializer withResolved(KeyDeserializer keyDeser, TypeDeserializer valueTypeDeser, JsonDeserializer<?> valueDeser, Set<String> ignorable)
/*     */   {
/* 161 */     if ((this._keyDeserializer == keyDeser) && (this._valueDeserializer == valueDeser) && (this._valueTypeDeserializer == valueTypeDeser) && (this._ignorableProperties == ignorable))
/*     */     {
/* 163 */       return this;
/*     */     }
/* 165 */     return new MapDeserializer(this, keyDeser, valueDeser, valueTypeDeser, ignorable);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final boolean _isStdKeyDeser(JavaType mapType, KeyDeserializer keyDeser)
/*     */   {
/* 175 */     if (keyDeser == null) {
/* 176 */       return true;
/*     */     }
/* 178 */     JavaType keyType = mapType.getKeyType();
/* 179 */     if (keyType == null) {
/* 180 */       return true;
/*     */     }
/* 182 */     Class<?> rawKeyType = keyType.getRawClass();
/* 183 */     return ((rawKeyType == String.class) || (rawKeyType == Object.class)) && (isDefaultKeyDeserializer(keyDeser));
/*     */   }
/*     */   
/*     */   public void setIgnorableProperties(String[] ignorable)
/*     */   {
/* 188 */     this._ignorableProperties = ((ignorable == null) || (ignorable.length == 0) ? null : ArrayBuilders.arrayToSet(ignorable));
/*     */   }
/*     */   
/*     */   public void setIgnorableProperties(Set<String> ignorable)
/*     */   {
/* 193 */     this._ignorableProperties = ((ignorable == null) || (ignorable.size() == 0) ? null : ignorable);
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
/*     */   public void resolve(DeserializationContext ctxt)
/*     */     throws JsonMappingException
/*     */   {
/* 207 */     if (this._valueInstantiator.canCreateUsingDelegate()) {
/* 208 */       JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
/* 209 */       if (delegateType == null) {
/* 210 */         throw new IllegalArgumentException("Invalid delegate-creator definition for " + this._mapType + ": value instantiator (" + this._valueInstantiator.getClass().getName() + ") returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'");
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 218 */       this._delegateDeserializer = findDeserializer(ctxt, delegateType, null);
/*     */     }
/* 220 */     if (this._valueInstantiator.canCreateFromObjectWith()) {
/* 221 */       SettableBeanProperty[] creatorProps = this._valueInstantiator.getFromObjectArguments(ctxt.getConfig());
/* 222 */       this._propertyBasedCreator = PropertyBasedCreator.construct(ctxt, this._valueInstantiator, creatorProps);
/*     */     }
/* 224 */     this._standardStringKey = _isStdKeyDeser(this._mapType, this._keyDeserializer);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
/*     */     throws JsonMappingException
/*     */   {
/* 235 */     KeyDeserializer kd = this._keyDeserializer;
/* 236 */     if (kd == null) {
/* 237 */       kd = ctxt.findKeyDeserializer(this._mapType.getKeyType(), property);
/*     */     }
/* 239 */     else if ((kd instanceof ContextualKeyDeserializer)) {
/* 240 */       kd = ((ContextualKeyDeserializer)kd).createContextual(ctxt, property);
/*     */     }
/*     */     
/*     */ 
/* 244 */     JsonDeserializer<?> vd = this._valueDeserializer;
/*     */     
/* 246 */     if (property != null) {
/* 247 */       vd = findConvertingContentDeserializer(ctxt, property, vd);
/*     */     }
/* 249 */     JavaType vt = this._mapType.getContentType();
/* 250 */     if (vd == null) {
/* 251 */       vd = ctxt.findContextualValueDeserializer(vt, property);
/*     */     } else {
/* 253 */       vd = ctxt.handleSecondaryContextualization(vd, property, vt);
/*     */     }
/* 255 */     TypeDeserializer vtd = this._valueTypeDeserializer;
/* 256 */     if (vtd != null) {
/* 257 */       vtd = vtd.forProperty(property);
/*     */     }
/* 259 */     Set<String> ignored = this._ignorableProperties;
/* 260 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 261 */     if ((intr != null) && (property != null)) {
/* 262 */       AnnotatedMember member = property.getMember();
/* 263 */       if (member != null) {
/* 264 */         JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnorals(member);
/* 265 */         if (ignorals != null) {
/* 266 */           Set<String> ignoresToAdd = ignorals.findIgnoredForDeserialization();
/* 267 */           if (!ignoresToAdd.isEmpty()) {
/* 268 */             ignored = ignored == null ? new HashSet() : new HashSet(ignored);
/* 269 */             for (String str : ignoresToAdd) {
/* 270 */               ignored.add(str);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 276 */     return withResolved(kd, vtd, vd, ignored);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JavaType getContentType()
/*     */   {
/* 287 */     return this._mapType.getContentType();
/*     */   }
/*     */   
/*     */   public JsonDeserializer<Object> getContentDeserializer()
/*     */   {
/* 292 */     return this._valueDeserializer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isCachable()
/*     */   {
/* 319 */     return (this._valueDeserializer == null) && (this._keyDeserializer == null) && (this._valueTypeDeserializer == null) && (this._ignorableProperties == null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<Object, Object> deserialize(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 329 */     if (this._propertyBasedCreator != null) {
/* 330 */       return _deserializeUsingCreator(p, ctxt);
/*     */     }
/* 332 */     if (this._delegateDeserializer != null) {
/* 333 */       return (Map)this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
/*     */     }
/*     */     
/* 336 */     if (!this._hasDefaultCreator) {
/* 337 */       return (Map)ctxt.handleMissingInstantiator(getMapClass(), p, "no default constructor found", new Object[0]);
/*     */     }
/*     */     
/*     */ 
/* 341 */     JsonToken t = p.getCurrentToken();
/* 342 */     if ((t != JsonToken.START_OBJECT) && (t != JsonToken.FIELD_NAME) && (t != JsonToken.END_OBJECT))
/*     */     {
/* 344 */       if (t == JsonToken.VALUE_STRING) {
/* 345 */         return (Map)this._valueInstantiator.createFromString(ctxt, p.getText());
/*     */       }
/*     */       
/* 348 */       return (Map)_deserializeFromEmpty(p, ctxt);
/*     */     }
/* 350 */     Map<Object, Object> result = (Map)this._valueInstantiator.createUsingDefault(ctxt);
/* 351 */     if (this._standardStringKey) {
/* 352 */       _readAndBindStringKeyMap(p, ctxt, result);
/* 353 */       return result;
/*     */     }
/* 355 */     _readAndBind(p, ctxt, result);
/* 356 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<Object, Object> deserialize(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result)
/*     */     throws IOException
/*     */   {
/* 366 */     p.setCurrentValue(result);
/*     */     
/*     */ 
/* 369 */     JsonToken t = p.getCurrentToken();
/* 370 */     if ((t != JsonToken.START_OBJECT) && (t != JsonToken.FIELD_NAME)) {
/* 371 */       return (Map)ctxt.handleUnexpectedToken(getMapClass(), p);
/*     */     }
/* 373 */     if (this._standardStringKey) {
/* 374 */       _readAndBindStringKeyMap(p, ctxt, result);
/* 375 */       return result;
/*     */     }
/* 377 */     _readAndBind(p, ctxt, result);
/* 378 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
/*     */     throws IOException, JsonProcessingException
/*     */   {
/* 387 */     return typeDeserializer.deserializeTypedFromObject(jp, ctxt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 397 */   public final Class<?> getMapClass() { return this._mapType.getRawClass(); }
/*     */   
/* 399 */   public JavaType getValueType() { return this._mapType; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final void _readAndBind(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result)
/*     */     throws IOException
/*     */   {
/* 410 */     KeyDeserializer keyDes = this._keyDeserializer;
/* 411 */     JsonDeserializer<Object> valueDes = this._valueDeserializer;
/* 412 */     TypeDeserializer typeDeser = this._valueTypeDeserializer;
/*     */     
/* 414 */     MapReferringAccumulator referringAccumulator = null;
/* 415 */     boolean useObjectId = valueDes.getObjectIdReader() != null;
/* 416 */     if (useObjectId) {
/* 417 */       referringAccumulator = new MapReferringAccumulator(this._mapType.getContentType().getRawClass(), result);
/*     */     }
/*     */     
/*     */     String keyStr;
/* 421 */     if (p.isExpectedStartObjectToken()) {
/* 422 */       keyStr = p.nextFieldName();
/*     */     } else {
/* 424 */       JsonToken t = p.getCurrentToken();
/* 425 */       if (t == JsonToken.END_OBJECT) {
/* 426 */         return;
/*     */       }
/* 428 */       if (t != JsonToken.FIELD_NAME)
/* 429 */         ctxt.reportWrongTokenException(p, JsonToken.FIELD_NAME, null, new Object[0]);
/*     */     }
/* 431 */     for (String keyStr = p.getCurrentName(); 
/*     */         
/*     */ 
/* 434 */         keyStr != null; keyStr = p.nextFieldName()) {
/* 435 */       Object key = keyDes.deserializeKey(keyStr, ctxt);
/*     */       
/* 437 */       JsonToken t = p.nextToken();
/* 438 */       if ((this._ignorableProperties != null) && (this._ignorableProperties.contains(keyStr))) {
/* 439 */         p.skipChildren();
/*     */       } else {
/*     */         try
/*     */         {
/*     */           Object value;
/*     */           Object value;
/* 445 */           if (t == JsonToken.VALUE_NULL) {
/* 446 */             value = valueDes.getNullValue(ctxt); } else { Object value;
/* 447 */             if (typeDeser == null) {
/* 448 */               value = valueDes.deserialize(p, ctxt);
/*     */             } else
/* 450 */               value = valueDes.deserializeWithType(p, ctxt, typeDeser);
/*     */           }
/* 452 */           if (useObjectId) {
/* 453 */             referringAccumulator.put(key, value);
/*     */           } else {
/* 455 */             result.put(key, value);
/*     */           }
/*     */         } catch (UnresolvedForwardReference reference) {
/* 458 */           handleUnresolvedReference(p, referringAccumulator, key, reference);
/*     */         } catch (Exception e) {
/* 460 */           wrapAndThrow(e, result, keyStr);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final void _readAndBindStringKeyMap(JsonParser p, DeserializationContext ctxt, Map<Object, Object> result)
/*     */     throws IOException
/*     */   {
/* 473 */     JsonDeserializer<Object> valueDes = this._valueDeserializer;
/* 474 */     TypeDeserializer typeDeser = this._valueTypeDeserializer;
/* 475 */     MapReferringAccumulator referringAccumulator = null;
/* 476 */     boolean useObjectId = valueDes.getObjectIdReader() != null;
/* 477 */     if (useObjectId) {
/* 478 */       referringAccumulator = new MapReferringAccumulator(this._mapType.getContentType().getRawClass(), result);
/*     */     }
/*     */     
/*     */     String key;
/* 482 */     if (p.isExpectedStartObjectToken()) {
/* 483 */       key = p.nextFieldName();
/*     */     } else {
/* 485 */       JsonToken t = p.getCurrentToken();
/* 486 */       if (t == JsonToken.END_OBJECT) {
/* 487 */         return;
/*     */       }
/* 489 */       if (t != JsonToken.FIELD_NAME)
/* 490 */         ctxt.reportWrongTokenException(p, JsonToken.FIELD_NAME, null, new Object[0]);
/*     */     }
/* 492 */     for (String key = p.getCurrentName(); 
/*     */         
/*     */ 
/* 495 */         key != null; key = p.nextFieldName()) {
/* 496 */       JsonToken t = p.nextToken();
/* 497 */       if ((this._ignorableProperties != null) && (this._ignorableProperties.contains(key))) {
/* 498 */         p.skipChildren();
/*     */       } else {
/*     */         try
/*     */         {
/*     */           Object value;
/*     */           Object value;
/* 504 */           if (t == JsonToken.VALUE_NULL) {
/* 505 */             value = valueDes.getNullValue(ctxt); } else { Object value;
/* 506 */             if (typeDeser == null) {
/* 507 */               value = valueDes.deserialize(p, ctxt);
/*     */             } else
/* 509 */               value = valueDes.deserializeWithType(p, ctxt, typeDeser);
/*     */           }
/* 511 */           if (useObjectId) {
/* 512 */             referringAccumulator.put(key, value);
/*     */           } else {
/* 514 */             result.put(key, value);
/*     */           }
/*     */         } catch (UnresolvedForwardReference reference) {
/* 517 */           handleUnresolvedReference(p, referringAccumulator, key, reference);
/*     */         } catch (Exception e) {
/* 519 */           wrapAndThrow(e, result, key);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public Map<Object, Object> _deserializeUsingCreator(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 528 */     PropertyBasedCreator creator = this._propertyBasedCreator;
/*     */     
/* 530 */     PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, null);
/*     */     
/* 532 */     JsonDeserializer<Object> valueDes = this._valueDeserializer;
/* 533 */     TypeDeserializer typeDeser = this._valueTypeDeserializer;
/*     */     
/*     */     String key;
/* 536 */     if (p.isExpectedStartObjectToken()) {
/* 537 */       key = p.nextFieldName(); } else { String key;
/* 538 */       if (p.hasToken(JsonToken.FIELD_NAME))
/* 539 */         key = p.getCurrentName();
/*     */     }
/* 541 */     for (String key = null; 
/*     */         
/*     */ 
/* 544 */         key != null; key = p.nextFieldName()) {
/* 545 */       JsonToken t = p.nextToken();
/* 546 */       if ((this._ignorableProperties != null) && (this._ignorableProperties.contains(key))) {
/* 547 */         p.skipChildren();
/*     */       }
/*     */       else
/*     */       {
/* 551 */         SettableBeanProperty prop = creator.findCreatorProperty(key);
/* 552 */         if (prop != null)
/*     */         {
/* 554 */           if (buffer.assignParameter(prop, prop.deserialize(p, ctxt))) {
/* 555 */             p.nextToken();
/*     */             Map<Object, Object> result;
/*     */             try {
/* 558 */               result = (Map)creator.build(ctxt, buffer);
/*     */             } catch (Exception e) {
/* 560 */               wrapAndThrow(e, this._mapType.getRawClass(), key);
/* 561 */               return null;
/*     */             }
/* 563 */             _readAndBind(p, ctxt, result);
/* 564 */             return result;
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 569 */           Object actualKey = this._keyDeserializer.deserializeKey(key, ctxt);
/*     */           Object value;
/*     */           try {
/*     */             Object value;
/* 573 */             if (t == JsonToken.VALUE_NULL) {
/* 574 */               value = valueDes.getNullValue(ctxt); } else { Object value;
/* 575 */               if (typeDeser == null) {
/* 576 */                 value = valueDes.deserialize(p, ctxt);
/*     */               } else
/* 578 */                 value = valueDes.deserializeWithType(p, ctxt, typeDeser);
/*     */             }
/*     */           } catch (Exception e) {
/* 581 */             wrapAndThrow(e, this._mapType.getRawClass(), key);
/* 582 */             return null;
/*     */           }
/* 584 */           buffer.bufferMapProperty(actualKey, value);
/*     */         }
/*     */       }
/*     */     }
/*     */     try {
/* 589 */       return (Map)creator.build(ctxt, buffer);
/*     */     } catch (Exception e) {
/* 591 */       wrapAndThrow(e, this._mapType.getRawClass(), key); }
/* 592 */     return null;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   protected void wrapAndThrow(Throwable t, Object ref) throws IOException
/*     */   {
/* 598 */     wrapAndThrow(t, ref, null);
/*     */   }
/*     */   
/*     */ 
/*     */   private void handleUnresolvedReference(JsonParser jp, MapReferringAccumulator accumulator, Object key, UnresolvedForwardReference reference)
/*     */     throws JsonMappingException
/*     */   {
/* 605 */     if (accumulator == null) {
/* 606 */       throw JsonMappingException.from(jp, "Unresolved forward reference but no identity info.", reference);
/*     */     }
/* 608 */     ReadableObjectId.Referring referring = accumulator.handleUnresolvedReference(reference, key);
/* 609 */     reference.getRoid().appendReferring(referring);
/*     */   }
/*     */   
/*     */ 
/*     */   private static final class MapReferringAccumulator
/*     */   {
/*     */     private final Class<?> _valueType;
/*     */     
/*     */     private Map<Object, Object> _result;
/* 618 */     private List<MapReferring> _accumulator = new ArrayList();
/*     */     
/*     */     public MapReferringAccumulator(Class<?> valueType, Map<Object, Object> result) {
/* 621 */       this._valueType = valueType;
/* 622 */       this._result = result;
/*     */     }
/*     */     
/*     */     public void put(Object key, Object value)
/*     */     {
/* 627 */       if (this._accumulator.isEmpty()) {
/* 628 */         this._result.put(key, value);
/*     */       } else {
/* 630 */         MapReferring ref = (MapReferring)this._accumulator.get(this._accumulator.size() - 1);
/* 631 */         ref.next.put(key, value);
/*     */       }
/*     */     }
/*     */     
/*     */     public ReadableObjectId.Referring handleUnresolvedReference(UnresolvedForwardReference reference, Object key)
/*     */     {
/* 637 */       MapReferring id = new MapReferring(this, reference, this._valueType, key);
/* 638 */       this._accumulator.add(id);
/* 639 */       return id;
/*     */     }
/*     */     
/*     */     public void resolveForwardReference(Object id, Object value) throws IOException
/*     */     {
/* 644 */       Iterator<MapReferring> iterator = this._accumulator.iterator();
/*     */       
/*     */ 
/*     */ 
/* 648 */       Map<Object, Object> previous = this._result;
/* 649 */       while (iterator.hasNext()) {
/* 650 */         MapReferring ref = (MapReferring)iterator.next();
/* 651 */         if (ref.hasId(id)) {
/* 652 */           iterator.remove();
/* 653 */           previous.put(ref.key, value);
/* 654 */           previous.putAll(ref.next);
/* 655 */           return;
/*     */         }
/* 657 */         previous = ref.next;
/*     */       }
/*     */       
/* 660 */       throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id + "] that wasn't previously seen as unresolved.");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   static class MapReferring
/*     */     extends ReadableObjectId.Referring
/*     */   {
/*     */     private final MapReferringAccumulator _parent;
/*     */     
/*     */ 
/* 673 */     public final Map<Object, Object> next = new LinkedHashMap();
/*     */     
/*     */     public final Object key;
/*     */     
/*     */     MapReferring(MapReferringAccumulator parent, UnresolvedForwardReference ref, Class<?> valueType, Object key)
/*     */     {
/* 679 */       super(valueType);
/* 680 */       this._parent = parent;
/* 681 */       this.key = key;
/*     */     }
/*     */     
/*     */     public void handleResolvedForwardReference(Object id, Object value) throws IOException
/*     */     {
/* 686 */       this._parent.resolveForwardReference(id, value);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\std\MapDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */