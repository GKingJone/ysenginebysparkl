/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*     */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.BeanAsArrayDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.BeanPropertyMap;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ExternalTypeHandler;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ObjectIdReader;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.PropertyBasedCreator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.PropertyValueBuffer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ReadableObjectId;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ReadableObjectId.Referring;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.UnwrappedPropertyHandler;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.NameTransformer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.TokenBuffer;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
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
/*     */ public class BeanDeserializer
/*     */   extends BeanDeserializerBase
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   protected transient Exception _nullFromCreator;
/*     */   
/*     */   public BeanDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, HashSet<String> ignorableProps, boolean ignoreAllUnknown, boolean hasViews)
/*     */   {
/*  56 */     super(builder, beanDesc, properties, backRefs, ignorableProps, ignoreAllUnknown, hasViews);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected BeanDeserializer(BeanDeserializerBase src)
/*     */   {
/*  65 */     super(src, src._ignoreAllUnknown);
/*     */   }
/*     */   
/*     */   protected BeanDeserializer(BeanDeserializerBase src, boolean ignoreAllUnknown) {
/*  69 */     super(src, ignoreAllUnknown);
/*     */   }
/*     */   
/*     */   protected BeanDeserializer(BeanDeserializerBase src, NameTransformer unwrapper) {
/*  73 */     super(src, unwrapper);
/*     */   }
/*     */   
/*     */   public BeanDeserializer(BeanDeserializerBase src, ObjectIdReader oir) {
/*  77 */     super(src, oir);
/*     */   }
/*     */   
/*     */   public BeanDeserializer(BeanDeserializerBase src, Set<String> ignorableProps) {
/*  81 */     super(src, ignorableProps);
/*     */   }
/*     */   
/*     */   public BeanDeserializer(BeanDeserializerBase src, BeanPropertyMap props) {
/*  85 */     super(src, props);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper)
/*     */   {
/*  94 */     if (getClass() != BeanDeserializer.class) {
/*  95 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 101 */     return new BeanDeserializer(this, unwrapper);
/*     */   }
/*     */   
/*     */   public BeanDeserializer withObjectIdReader(ObjectIdReader oir)
/*     */   {
/* 106 */     return new BeanDeserializer(this, oir);
/*     */   }
/*     */   
/*     */   public BeanDeserializer withIgnorableProperties(Set<String> ignorableProps)
/*     */   {
/* 111 */     return new BeanDeserializer(this, ignorableProps);
/*     */   }
/*     */   
/*     */   public BeanDeserializerBase withBeanProperties(BeanPropertyMap props)
/*     */   {
/* 116 */     return new BeanDeserializer(this, props);
/*     */   }
/*     */   
/*     */   protected BeanDeserializerBase asArrayDeserializer()
/*     */   {
/* 121 */     SettableBeanProperty[] props = this._beanProperties.getPropertiesInInsertionOrder();
/* 122 */     return new BeanAsArrayDeserializer(this, props);
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
/*     */   public Object deserialize(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 138 */     if (p.isExpectedStartObjectToken()) {
/* 139 */       if (this._vanillaProcessing) {
/* 140 */         return vanillaDeserialize(p, ctxt, p.nextToken());
/*     */       }
/*     */       
/*     */ 
/* 144 */       p.nextToken();
/* 145 */       if (this._objectIdReader != null) {
/* 146 */         return deserializeWithObjectId(p, ctxt);
/*     */       }
/* 148 */       return deserializeFromObject(p, ctxt);
/*     */     }
/* 150 */     return _deserializeOther(p, ctxt, p.getCurrentToken());
/*     */   }
/*     */   
/*     */ 
/*     */   protected final Object _deserializeOther(JsonParser p, DeserializationContext ctxt, JsonToken t)
/*     */     throws IOException
/*     */   {
/* 157 */     switch (t) {
/*     */     case VALUE_STRING: 
/* 159 */       return deserializeFromString(p, ctxt);
/*     */     case VALUE_NUMBER_INT: 
/* 161 */       return deserializeFromNumber(p, ctxt);
/*     */     case VALUE_NUMBER_FLOAT: 
/* 163 */       return deserializeFromDouble(p, ctxt);
/*     */     case VALUE_EMBEDDED_OBJECT: 
/* 165 */       return deserializeFromEmbedded(p, ctxt);
/*     */     case VALUE_TRUE: 
/*     */     case VALUE_FALSE: 
/* 168 */       return deserializeFromBoolean(p, ctxt);
/*     */     
/*     */     case VALUE_NULL: 
/* 171 */       return deserializeFromNull(p, ctxt);
/*     */     
/*     */     case START_ARRAY: 
/* 174 */       return deserializeFromArray(p, ctxt);
/*     */     case FIELD_NAME: 
/*     */     case END_OBJECT: 
/* 177 */       if (this._vanillaProcessing) {
/* 178 */         return vanillaDeserialize(p, ctxt, t);
/*     */       }
/* 180 */       if (this._objectIdReader != null) {
/* 181 */         return deserializeWithObjectId(p, ctxt);
/*     */       }
/* 183 */       return deserializeFromObject(p, ctxt);
/*     */     }
/*     */     
/* 186 */     return ctxt.handleUnexpectedToken(handledType(), p);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   protected Object _missingToken(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 191 */     throw ctxt.endOfInputException(handledType());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object deserialize(JsonParser p, DeserializationContext ctxt, Object bean)
/*     */     throws IOException
/*     */   {
/* 203 */     p.setCurrentValue(bean);
/* 204 */     if (this._injectables != null) {
/* 205 */       injectValues(ctxt, bean);
/*     */     }
/* 207 */     if (this._unwrappedPropertyHandler != null) {
/* 208 */       return deserializeWithUnwrapped(p, ctxt, bean);
/*     */     }
/* 210 */     if (this._externalTypeIdHandler != null) {
/* 211 */       return deserializeWithExternalTypeId(p, ctxt, bean);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 216 */     if (p.isExpectedStartObjectToken()) {
/* 217 */       String propName = p.nextFieldName();
/* 218 */       if (propName == null)
/* 219 */         return bean;
/*     */     } else {
/*     */       String propName;
/* 222 */       if (p.hasTokenId(5)) {
/* 223 */         propName = p.getCurrentName();
/*     */       } else
/* 225 */         return bean;
/*     */     }
/*     */     String propName;
/* 228 */     if (this._needViewProcesing) {
/* 229 */       Class<?> view = ctxt.getActiveView();
/* 230 */       if (view != null) {
/* 231 */         return deserializeWithView(p, ctxt, bean, view);
/*     */       }
/*     */     }
/*     */     do {
/* 235 */       p.nextToken();
/* 236 */       SettableBeanProperty prop = this._beanProperties.find(propName);
/*     */       
/* 238 */       if (prop != null) {
/*     */         try {
/* 240 */           prop.deserializeAndSet(p, ctxt, bean);
/*     */         } catch (Exception e) {
/* 242 */           wrapAndThrow(e, bean, propName, ctxt);
/*     */         }
/*     */         
/*     */       } else
/* 246 */         handleUnknownVanilla(p, ctxt, bean, propName);
/* 247 */     } while ((propName = p.nextFieldName()) != null);
/* 248 */     return bean;
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
/*     */   private final Object vanillaDeserialize(JsonParser p, DeserializationContext ctxt, JsonToken t)
/*     */     throws IOException
/*     */   {
/* 265 */     Object bean = this._valueInstantiator.createUsingDefault(ctxt);
/*     */     
/* 267 */     p.setCurrentValue(bean);
/* 268 */     if (p.hasTokenId(5)) {
/* 269 */       String propName = p.getCurrentName();
/*     */       do {
/* 271 */         p.nextToken();
/* 272 */         SettableBeanProperty prop = this._beanProperties.find(propName);
/*     */         
/* 274 */         if (prop != null) {
/*     */           try {
/* 276 */             prop.deserializeAndSet(p, ctxt, bean);
/*     */           } catch (Exception e) {
/* 278 */             wrapAndThrow(e, bean, propName, ctxt);
/*     */           }
/*     */           
/*     */         } else
/* 282 */           handleUnknownVanilla(p, ctxt, bean, propName);
/* 283 */       } while ((propName = p.nextFieldName()) != null);
/*     */     }
/* 285 */     return bean;
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
/*     */   public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 301 */     if ((this._objectIdReader != null) && (this._objectIdReader.maySerializeAsObject()) && 
/* 302 */       (p.hasTokenId(5)) && (this._objectIdReader.isValidReferencePropertyName(p.getCurrentName(), p)))
/*     */     {
/* 304 */       return deserializeFromObjectId(p, ctxt);
/*     */     }
/*     */     
/* 307 */     if (this._nonStandardCreation) {
/* 308 */       if (this._unwrappedPropertyHandler != null) {
/* 309 */         return deserializeWithUnwrapped(p, ctxt);
/*     */       }
/* 311 */       if (this._externalTypeIdHandler != null) {
/* 312 */         return deserializeWithExternalTypeId(p, ctxt);
/*     */       }
/* 314 */       Object bean = deserializeFromObjectUsingNonDefault(p, ctxt);
/* 315 */       if (this._injectables != null) {
/* 316 */         injectValues(ctxt, bean);
/*     */       }
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
/* 330 */       return bean;
/*     */     }
/* 332 */     Object bean = this._valueInstantiator.createUsingDefault(ctxt);
/*     */     
/* 334 */     p.setCurrentValue(bean);
/* 335 */     if (p.canReadObjectId()) {
/* 336 */       Object id = p.getObjectId();
/* 337 */       if (id != null) {
/* 338 */         _handleTypedObjectId(p, ctxt, bean, id);
/*     */       }
/*     */     }
/* 341 */     if (this._injectables != null) {
/* 342 */       injectValues(ctxt, bean);
/*     */     }
/* 344 */     if (this._needViewProcesing) {
/* 345 */       Class<?> view = ctxt.getActiveView();
/* 346 */       if (view != null) {
/* 347 */         return deserializeWithView(p, ctxt, bean, view);
/*     */       }
/*     */     }
/* 350 */     if (p.hasTokenId(5)) {
/* 351 */       String propName = p.getCurrentName();
/*     */       do {
/* 353 */         p.nextToken();
/* 354 */         SettableBeanProperty prop = this._beanProperties.find(propName);
/* 355 */         if (prop != null) {
/*     */           try {
/* 357 */             prop.deserializeAndSet(p, ctxt, bean);
/*     */           } catch (Exception e) {
/* 359 */             wrapAndThrow(e, bean, propName, ctxt);
/*     */           }
/*     */           
/*     */         } else
/* 363 */           handleUnknownVanilla(p, ctxt, bean, propName);
/* 364 */       } while ((propName = p.nextFieldName()) != null);
/*     */     }
/* 366 */     return bean;
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
/*     */   protected Object _deserializeUsingPropertyBased(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 382 */     PropertyBasedCreator creator = this._propertyBasedCreator;
/* 383 */     PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
/*     */     
/* 385 */     TokenBuffer unknown = null;
/*     */     
/* 387 */     JsonToken t = p.getCurrentToken();
/* 388 */     List<BeanReferring> referrings = null;
/* 389 */     for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 390 */       String propName = p.getCurrentName();
/* 391 */       p.nextToken();
/*     */       
/* 393 */       SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
/* 394 */       if (creatorProp != null)
/*     */       {
/* 396 */         if (buffer.assignParameter(creatorProp, _deserializeWithErrorWrapping(p, ctxt, creatorProp)))
/*     */         {
/* 398 */           p.nextToken();
/*     */           Object bean;
/*     */           try {
/* 401 */             bean = creator.build(ctxt, buffer);
/*     */           } catch (Exception e) {
/* 403 */             bean = wrapInstantiationProblem(e, ctxt);
/*     */           }
/* 405 */           if (bean == null) {
/* 406 */             return ctxt.handleInstantiationProblem(handledType(), null, _creatorReturnedNullException());
/*     */           }
/*     */           
/*     */ 
/* 410 */           p.setCurrentValue(bean);
/*     */           
/*     */ 
/* 413 */           if (bean.getClass() != this._beanType.getRawClass()) {
/* 414 */             return handlePolymorphic(p, ctxt, bean, unknown);
/*     */           }
/* 416 */           if (unknown != null) {
/* 417 */             bean = handleUnknownProperties(ctxt, bean, unknown);
/*     */           }
/*     */           
/* 420 */           return deserialize(p, ctxt, bean);
/*     */         }
/*     */         
/*     */ 
/*     */       }
/* 425 */       else if (!buffer.readIdProperty(propName))
/*     */       {
/*     */ 
/*     */ 
/* 429 */         SettableBeanProperty prop = this._beanProperties.find(propName);
/* 430 */         if (prop != null) {
/*     */           try {
/* 432 */             buffer.bufferProperty(prop, _deserializeWithErrorWrapping(p, ctxt, prop));
/*     */ 
/*     */           }
/*     */           catch (UnresolvedForwardReference reference)
/*     */           {
/* 437 */             BeanReferring referring = handleUnresolvedReference(ctxt, prop, buffer, reference);
/*     */             
/* 439 */             if (referrings == null) {
/* 440 */               referrings = new ArrayList();
/*     */             }
/* 442 */             referrings.add(referring);
/*     */           }
/*     */           
/*     */ 
/*     */         }
/* 447 */         else if ((this._ignorableProps != null) && (this._ignorableProps.contains(propName))) {
/* 448 */           handleIgnoredProperty(p, ctxt, handledType(), propName);
/*     */ 
/*     */ 
/*     */         }
/* 452 */         else if (this._anySetter != null) {
/*     */           try {
/* 454 */             buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
/*     */           } catch (Exception e) {
/* 456 */             wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 461 */           if (unknown == null) {
/* 462 */             unknown = new TokenBuffer(p, ctxt);
/*     */           }
/* 464 */           unknown.writeFieldName(propName);
/* 465 */           unknown.copyCurrentStructure(p);
/*     */         }
/*     */       }
/*     */     }
/*     */     Object bean;
/*     */     try {
/* 471 */       bean = creator.build(ctxt, buffer);
/*     */     } catch (Exception e) {
/* 473 */       wrapInstantiationProblem(e, ctxt);
/* 474 */       bean = null;
/*     */     }
/* 476 */     if (referrings != null) {
/* 477 */       for (BeanReferring referring : referrings) {
/* 478 */         referring.setBean(bean);
/*     */       }
/*     */     }
/* 481 */     if (unknown != null)
/*     */     {
/* 483 */       if (bean.getClass() != this._beanType.getRawClass()) {
/* 484 */         return handlePolymorphic(null, ctxt, bean, unknown);
/*     */       }
/*     */       
/* 487 */       return handleUnknownProperties(ctxt, bean, unknown);
/*     */     }
/* 489 */     return bean;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private BeanReferring handleUnresolvedReference(DeserializationContext ctxt, SettableBeanProperty prop, PropertyValueBuffer buffer, UnresolvedForwardReference reference)
/*     */     throws JsonMappingException
/*     */   {
/* 500 */     BeanReferring referring = new BeanReferring(ctxt, reference, prop.getType(), buffer, prop);
/*     */     
/* 502 */     reference.getRoid().appendReferring(referring);
/* 503 */     return referring;
/*     */   }
/*     */   
/*     */   protected final Object _deserializeWithErrorWrapping(JsonParser p, DeserializationContext ctxt, SettableBeanProperty prop)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 511 */       return prop.deserialize(p, ctxt);
/*     */     } catch (Exception e) {
/* 513 */       wrapAndThrow(e, this._beanType.getRawClass(), prop.getName(), ctxt);
/*     */     }
/* 515 */     return null;
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
/*     */   protected Object deserializeFromNull(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 533 */     if (p.requiresCustomCodec())
/*     */     {
/* 535 */       TokenBuffer tb = new TokenBuffer(p, ctxt);
/* 536 */       tb.writeEndObject();
/* 537 */       JsonParser p2 = tb.asParser(p);
/* 538 */       p2.nextToken();
/*     */       
/* 540 */       Object ob = this._vanillaProcessing ? vanillaDeserialize(p2, ctxt, JsonToken.END_OBJECT) : deserializeFromObject(p2, ctxt);
/*     */       
/* 542 */       p2.close();
/* 543 */       return ob;
/*     */     }
/* 545 */     return ctxt.handleUnexpectedToken(handledType(), p);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final Object deserializeWithView(JsonParser p, DeserializationContext ctxt, Object bean, Class<?> activeView)
/*     */     throws IOException
/*     */   {
/* 558 */     if (p.hasTokenId(5)) {
/* 559 */       String propName = p.getCurrentName();
/*     */       do {
/* 561 */         p.nextToken();
/*     */         
/* 563 */         SettableBeanProperty prop = this._beanProperties.find(propName);
/* 564 */         if (prop != null) {
/* 565 */           if (!prop.visibleInView(activeView)) {
/* 566 */             p.skipChildren();
/*     */           } else {
/*     */             try
/*     */             {
/* 570 */               prop.deserializeAndSet(p, ctxt, bean);
/*     */             } catch (Exception e) {
/* 572 */               wrapAndThrow(e, bean, propName, ctxt);
/*     */             }
/*     */           }
/*     */         } else
/* 576 */           handleUnknownVanilla(p, ctxt, bean, propName);
/* 577 */       } while ((propName = p.nextFieldName()) != null);
/*     */     }
/* 579 */     return bean;
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
/*     */   protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 596 */     if (this._delegateDeserializer != null) {
/* 597 */       return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
/*     */     }
/* 599 */     if (this._propertyBasedCreator != null) {
/* 600 */       return deserializeUsingPropertyBasedWithUnwrapped(p, ctxt);
/*     */     }
/* 602 */     TokenBuffer tokens = new TokenBuffer(p, ctxt);
/* 603 */     tokens.writeStartObject();
/* 604 */     Object bean = this._valueInstantiator.createUsingDefault(ctxt);
/*     */     
/*     */ 
/* 607 */     p.setCurrentValue(bean);
/*     */     
/* 609 */     if (this._injectables != null) {
/* 610 */       injectValues(ctxt, bean);
/*     */     }
/* 612 */     Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/* 613 */     for (String propName = p.hasTokenId(5) ? p.getCurrentName() : null; 
/*     */         
/* 615 */         propName != null; propName = p.nextFieldName()) {
/* 616 */       p.nextToken();
/* 617 */       SettableBeanProperty prop = this._beanProperties.find(propName);
/* 618 */       if (prop != null) {
/* 619 */         if ((activeView != null) && (!prop.visibleInView(activeView))) {
/* 620 */           p.skipChildren();
/*     */         } else {
/*     */           try
/*     */           {
/* 624 */             prop.deserializeAndSet(p, ctxt, bean);
/*     */           } catch (Exception e) {
/* 626 */             wrapAndThrow(e, bean, propName, ctxt);
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 631 */       else if ((this._ignorableProps != null) && (this._ignorableProps.contains(propName))) {
/* 632 */         handleIgnoredProperty(p, ctxt, bean, propName);
/*     */       }
/*     */       else
/*     */       {
/* 636 */         tokens.writeFieldName(propName);
/* 637 */         tokens.copyCurrentStructure(p);
/*     */         
/* 639 */         if (this._anySetter != null) {
/*     */           try {
/* 641 */             this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
/*     */           } catch (Exception e) {
/* 643 */             wrapAndThrow(e, bean, propName, ctxt);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 648 */     tokens.writeEndObject();
/* 649 */     this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
/* 650 */     return bean;
/*     */   }
/*     */   
/*     */ 
/*     */   protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt, Object bean)
/*     */     throws IOException
/*     */   {
/* 657 */     JsonToken t = p.getCurrentToken();
/* 658 */     if (t == JsonToken.START_OBJECT) {
/* 659 */       t = p.nextToken();
/*     */     }
/* 661 */     TokenBuffer tokens = new TokenBuffer(p, ctxt);
/* 662 */     tokens.writeStartObject();
/* 663 */     Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/* 664 */     for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 665 */       String propName = p.getCurrentName();
/* 666 */       SettableBeanProperty prop = this._beanProperties.find(propName);
/* 667 */       p.nextToken();
/* 668 */       if (prop != null) {
/* 669 */         if ((activeView != null) && (!prop.visibleInView(activeView))) {
/* 670 */           p.skipChildren();
/*     */         } else {
/*     */           try
/*     */           {
/* 674 */             prop.deserializeAndSet(p, ctxt, bean);
/*     */           } catch (Exception e) {
/* 676 */             wrapAndThrow(e, bean, propName, ctxt);
/*     */           }
/*     */         }
/*     */       }
/* 680 */       else if ((this._ignorableProps != null) && (this._ignorableProps.contains(propName))) {
/* 681 */         handleIgnoredProperty(p, ctxt, bean, propName);
/*     */       }
/*     */       else
/*     */       {
/* 685 */         tokens.writeFieldName(propName);
/* 686 */         tokens.copyCurrentStructure(p);
/*     */         
/* 688 */         if (this._anySetter != null)
/* 689 */           this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
/*     */       }
/*     */     }
/* 692 */     tokens.writeEndObject();
/* 693 */     this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
/* 694 */     return bean;
/*     */   }
/*     */   
/*     */ 
/*     */   protected Object deserializeUsingPropertyBasedWithUnwrapped(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 701 */     PropertyBasedCreator creator = this._propertyBasedCreator;
/* 702 */     PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
/*     */     
/* 704 */     TokenBuffer tokens = new TokenBuffer(p, ctxt);
/* 705 */     tokens.writeStartObject();
/*     */     
/* 707 */     for (JsonToken t = p.getCurrentToken(); 
/* 708 */         t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 709 */       String propName = p.getCurrentName();
/* 710 */       p.nextToken();
/*     */       
/* 712 */       SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
/* 713 */       if (creatorProp != null)
/*     */       {
/* 715 */         if (buffer.assignParameter(creatorProp, _deserializeWithErrorWrapping(p, ctxt, creatorProp))) {
/* 716 */           t = p.nextToken();
/*     */           Object bean;
/*     */           try {
/* 719 */             bean = creator.build(ctxt, buffer);
/*     */           } catch (Exception e) {
/* 721 */             bean = wrapInstantiationProblem(e, ctxt);
/*     */           }
/*     */           
/* 724 */           p.setCurrentValue(bean);
/*     */           
/* 726 */           while (t == JsonToken.FIELD_NAME) {
/* 727 */             p.nextToken();
/* 728 */             tokens.copyCurrentStructure(p);
/* 729 */             t = p.nextToken();
/*     */           }
/* 731 */           tokens.writeEndObject();
/* 732 */           if (bean.getClass() != this._beanType.getRawClass())
/*     */           {
/*     */ 
/* 735 */             tokens.close();
/* 736 */             ctxt.reportMappingException("Can not create polymorphic instances with unwrapped values", new Object[0]);
/* 737 */             return null;
/*     */           }
/* 739 */           return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
/*     */         }
/*     */         
/*     */ 
/*     */       }
/* 744 */       else if (!buffer.readIdProperty(propName))
/*     */       {
/*     */ 
/*     */ 
/* 748 */         SettableBeanProperty prop = this._beanProperties.find(propName);
/* 749 */         if (prop != null) {
/* 750 */           buffer.bufferProperty(prop, _deserializeWithErrorWrapping(p, ctxt, prop));
/*     */ 
/*     */ 
/*     */         }
/* 754 */         else if ((this._ignorableProps != null) && (this._ignorableProps.contains(propName))) {
/* 755 */           handleIgnoredProperty(p, ctxt, handledType(), propName);
/*     */         }
/*     */         else {
/* 758 */           tokens.writeFieldName(propName);
/* 759 */           tokens.copyCurrentStructure(p);
/*     */           
/* 761 */           if (this._anySetter != null) {
/*     */             try {
/* 763 */               buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
/*     */             } catch (Exception e) {
/* 765 */               wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     Object bean;
/*     */     try {
/* 773 */       bean = creator.build(ctxt, buffer);
/*     */     } catch (Exception e) {
/* 775 */       wrapInstantiationProblem(e, ctxt);
/* 776 */       return null;
/*     */     }
/* 778 */     return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 791 */     if (this._propertyBasedCreator != null) {
/* 792 */       return deserializeUsingPropertyBasedWithExternalTypeId(p, ctxt);
/*     */     }
/* 794 */     if (this._delegateDeserializer != null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 800 */       return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
/*     */     }
/*     */     
/*     */ 
/* 804 */     return deserializeWithExternalTypeId(p, ctxt, this._valueInstantiator.createUsingDefault(ctxt));
/*     */   }
/*     */   
/*     */ 
/*     */   protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt, Object bean)
/*     */     throws IOException
/*     */   {
/* 811 */     Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/* 812 */     ExternalTypeHandler ext = this._externalTypeIdHandler.start();
/*     */     
/* 814 */     for (JsonToken t = p.getCurrentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 815 */       String propName = p.getCurrentName();
/* 816 */       t = p.nextToken();
/* 817 */       SettableBeanProperty prop = this._beanProperties.find(propName);
/* 818 */       if (prop != null)
/*     */       {
/* 820 */         if (t.isScalarValue()) {
/* 821 */           ext.handleTypePropertyValue(p, ctxt, propName, bean);
/*     */         }
/* 823 */         if ((activeView != null) && (!prop.visibleInView(activeView))) {
/* 824 */           p.skipChildren();
/*     */         } else {
/*     */           try
/*     */           {
/* 828 */             prop.deserializeAndSet(p, ctxt, bean);
/*     */           } catch (Exception e) {
/* 830 */             wrapAndThrow(e, bean, propName, ctxt);
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 835 */       else if ((this._ignorableProps != null) && (this._ignorableProps.contains(propName))) {
/* 836 */         handleIgnoredProperty(p, ctxt, bean, propName);
/*     */ 
/*     */ 
/*     */       }
/* 840 */       else if (!ext.handlePropertyValue(p, ctxt, propName, bean))
/*     */       {
/*     */ 
/*     */ 
/* 844 */         if (this._anySetter != null) {
/*     */           try {
/* 846 */             this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
/*     */           } catch (Exception e) {
/* 848 */             wrapAndThrow(e, bean, propName, ctxt);
/*     */           }
/*     */           
/*     */         }
/*     */         else
/* 853 */           handleUnknownProperty(p, ctxt, bean, propName);
/*     */       }
/*     */     }
/* 856 */     return ext.complete(p, ctxt, bean);
/*     */   }
/*     */   
/*     */ 
/*     */   protected Object deserializeUsingPropertyBasedWithExternalTypeId(JsonParser p, DeserializationContext ctxt)
/*     */     throws IOException
/*     */   {
/* 863 */     ExternalTypeHandler ext = this._externalTypeIdHandler.start();
/* 864 */     PropertyBasedCreator creator = this._propertyBasedCreator;
/* 865 */     PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
/*     */     
/* 867 */     TokenBuffer tokens = new TokenBuffer(p, ctxt);
/* 868 */     tokens.writeStartObject();
/*     */     
/* 870 */     for (JsonToken t = p.getCurrentToken(); 
/* 871 */         t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 872 */       String propName = p.getCurrentName();
/* 873 */       p.nextToken();
/*     */       
/* 875 */       SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
/* 876 */       if (creatorProp != null)
/*     */       {
/*     */ 
/*     */ 
/* 880 */         if (!ext.handlePropertyValue(p, ctxt, propName, null))
/*     */         {
/*     */ 
/*     */ 
/* 884 */           if (buffer.assignParameter(creatorProp, _deserializeWithErrorWrapping(p, ctxt, creatorProp))) {
/* 885 */             t = p.nextToken();
/*     */             Object bean;
/*     */             try {
/* 888 */               bean = creator.build(ctxt, buffer);
/*     */             } catch (Exception e) {
/* 890 */               wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
/* 891 */               continue;
/*     */             }
/*     */             
/* 894 */             while (t == JsonToken.FIELD_NAME) {
/* 895 */               p.nextToken();
/* 896 */               tokens.copyCurrentStructure(p);
/* 897 */               t = p.nextToken();
/*     */             }
/* 899 */             if (bean.getClass() != this._beanType.getRawClass())
/*     */             {
/*     */ 
/* 902 */               ctxt.reportMappingException("Can not create polymorphic instances with external type ids", new Object[0]);
/* 903 */               return null;
/*     */             }
/* 905 */             return ext.complete(p, ctxt, bean);
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */       }
/* 911 */       else if (!buffer.readIdProperty(propName))
/*     */       {
/*     */ 
/*     */ 
/* 915 */         SettableBeanProperty prop = this._beanProperties.find(propName);
/* 916 */         if (prop != null) {
/* 917 */           buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
/*     */ 
/*     */ 
/*     */         }
/* 921 */         else if (!ext.handlePropertyValue(p, ctxt, propName, null))
/*     */         {
/*     */ 
/*     */ 
/* 925 */           if ((this._ignorableProps != null) && (this._ignorableProps.contains(propName))) {
/* 926 */             handleIgnoredProperty(p, ctxt, handledType(), propName);
/*     */ 
/*     */ 
/*     */           }
/* 930 */           else if (this._anySetter != null) {
/* 931 */             buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     try {
/* 937 */       return ext.complete(p, ctxt, buffer, creator);
/*     */     } catch (Exception e) {
/* 939 */       return wrapInstantiationProblem(e, ctxt);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Exception _creatorReturnedNullException()
/*     */   {
/* 950 */     if (this._nullFromCreator == null) {
/* 951 */       this._nullFromCreator = new NullPointerException("JSON Creator returned null");
/*     */     }
/* 953 */     return this._nullFromCreator;
/*     */   }
/*     */   
/*     */ 
/*     */   static class BeanReferring
/*     */     extends Referring
/*     */   {
/*     */     private final DeserializationContext _context;
/*     */     
/*     */     private final SettableBeanProperty _prop;
/*     */     
/*     */     private Object _bean;
/*     */     
/*     */     BeanReferring(DeserializationContext ctxt, UnresolvedForwardReference ref, JavaType valueType, PropertyValueBuffer buffer, SettableBeanProperty prop)
/*     */     {
/* 968 */       super(valueType);
/* 969 */       this._context = ctxt;
/* 970 */       this._prop = prop;
/*     */     }
/*     */     
/*     */     public void setBean(Object bean) {
/* 974 */       this._bean = bean;
/*     */     }
/*     */     
/*     */     public void handleResolvedForwardReference(Object id, Object value)
/*     */       throws IOException
/*     */     {
/* 980 */       if (this._bean == null) {
/* 981 */         this._context.reportMappingException("Can not resolve ObjectId forward reference using property '%s' (of type %s): Bean not yet resolved", new Object[] { this._prop.getName(), this._prop.getDeclaringClass().getName() });
/*     */       }
/*     */       
/*     */ 
/* 985 */       this._prop.set(this._bean, value);
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\BeanDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */