/*      */ package com.facebook.presto.jdbc.internal.jackson.databind.deser;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Value;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.BeanPropertyMap;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ExternalTypeHandler.Builder;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ObjectIdReader;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.PropertyBasedCreator;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ReadableObjectId;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.UnwrappedPropertyHandler;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ValueInjector;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StdDeserializer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.ObjectIdInfo;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.util.NameTransformer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.util.TokenBuffer;
/*      */ import java.io.IOException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ 
/*      */ public abstract class BeanDeserializerBase extends StdDeserializer<Object> implements ContextualDeserializer, ResolvableDeserializer, java.io.Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 1L;
/*   36 */   protected static final PropertyName TEMP_PROPERTY_NAME = new PropertyName("#temporary-name");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private final transient com.facebook.presto.jdbc.internal.jackson.databind.util.Annotations _classAnnotations;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final JavaType _beanType;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Shape _serializationShape;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final ValueInstantiator _valueInstantiator;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonDeserializer<Object> _delegateDeserializer;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonDeserializer<Object> _arrayDelegateDeserializer;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected PropertyBasedCreator _propertyBasedCreator;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean _nonStandardCreation;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean _vanillaProcessing;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final BeanPropertyMap _beanProperties;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final ValueInjector[] _injectables;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SettableAnyProperty _anySetter;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final Set<String> _ignorableProps;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final boolean _ignoreAllUnknown;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final boolean _needViewProcesing;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final Map<String, SettableBeanProperty> _backRefs;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected transient HashMap<com.facebook.presto.jdbc.internal.jackson.databind.type.ClassKey, JsonDeserializer<Object>> _subDeserializers;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected UnwrappedPropertyHandler _unwrappedPropertyHandler;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ExternalTypeHandler _externalTypeIdHandler;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final ObjectIdReader _objectIdReader;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected BeanDeserializerBase(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, Set<String> ignorableProps, boolean ignoreAllUnknown, boolean hasViews)
/*      */   {
/*  213 */     super(beanDesc.getType());
/*      */     
/*  215 */     com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedClass ac = beanDesc.getClassInfo();
/*  216 */     this._classAnnotations = ac.getAnnotations();
/*  217 */     this._beanType = beanDesc.getType();
/*  218 */     this._valueInstantiator = builder.getValueInstantiator();
/*      */     
/*  220 */     this._beanProperties = properties;
/*  221 */     this._backRefs = backRefs;
/*  222 */     this._ignorableProps = ignorableProps;
/*  223 */     this._ignoreAllUnknown = ignoreAllUnknown;
/*      */     
/*  225 */     this._anySetter = builder.getAnySetter();
/*  226 */     List<ValueInjector> injectables = builder.getInjectables();
/*  227 */     this._injectables = ((injectables == null) || (injectables.isEmpty()) ? null : (ValueInjector[])injectables.toArray(new ValueInjector[injectables.size()]));
/*      */     
/*  229 */     this._objectIdReader = builder.getObjectIdReader();
/*  230 */     this._nonStandardCreation = ((this._unwrappedPropertyHandler != null) || (this._valueInstantiator.canCreateUsingDelegate()) || (this._valueInstantiator.canCreateFromObjectWith()) || (!this._valueInstantiator.canCreateUsingDefault()));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  237 */     JsonFormat.Value format = beanDesc.findExpectedFormat(null);
/*  238 */     this._serializationShape = (format == null ? null : format.getShape());
/*      */     
/*  240 */     this._needViewProcesing = hasViews;
/*  241 */     this._vanillaProcessing = ((!this._nonStandardCreation) && (this._injectables == null) && (!this._needViewProcesing) && (this._objectIdReader == null));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected BeanDeserializerBase(BeanDeserializerBase src)
/*      */   {
/*  250 */     this(src, src._ignoreAllUnknown);
/*      */   }
/*      */   
/*      */   protected BeanDeserializerBase(BeanDeserializerBase src, boolean ignoreAllUnknown)
/*      */   {
/*  255 */     super(src._beanType);
/*      */     
/*  257 */     this._classAnnotations = src._classAnnotations;
/*  258 */     this._beanType = src._beanType;
/*      */     
/*  260 */     this._valueInstantiator = src._valueInstantiator;
/*  261 */     this._delegateDeserializer = src._delegateDeserializer;
/*  262 */     this._propertyBasedCreator = src._propertyBasedCreator;
/*      */     
/*  264 */     this._beanProperties = src._beanProperties;
/*  265 */     this._backRefs = src._backRefs;
/*  266 */     this._ignorableProps = src._ignorableProps;
/*  267 */     this._ignoreAllUnknown = ignoreAllUnknown;
/*  268 */     this._anySetter = src._anySetter;
/*  269 */     this._injectables = src._injectables;
/*  270 */     this._objectIdReader = src._objectIdReader;
/*      */     
/*  272 */     this._nonStandardCreation = src._nonStandardCreation;
/*  273 */     this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
/*  274 */     this._needViewProcesing = src._needViewProcesing;
/*  275 */     this._serializationShape = src._serializationShape;
/*      */     
/*  277 */     this._vanillaProcessing = src._vanillaProcessing;
/*      */   }
/*      */   
/*      */   protected BeanDeserializerBase(BeanDeserializerBase src, NameTransformer unwrapper)
/*      */   {
/*  282 */     super(src._beanType);
/*      */     
/*  284 */     this._classAnnotations = src._classAnnotations;
/*  285 */     this._beanType = src._beanType;
/*      */     
/*  287 */     this._valueInstantiator = src._valueInstantiator;
/*  288 */     this._delegateDeserializer = src._delegateDeserializer;
/*  289 */     this._propertyBasedCreator = src._propertyBasedCreator;
/*      */     
/*  291 */     this._backRefs = src._backRefs;
/*  292 */     this._ignorableProps = src._ignorableProps;
/*  293 */     this._ignoreAllUnknown = ((unwrapper != null) || (src._ignoreAllUnknown));
/*  294 */     this._anySetter = src._anySetter;
/*  295 */     this._injectables = src._injectables;
/*  296 */     this._objectIdReader = src._objectIdReader;
/*      */     
/*  298 */     this._nonStandardCreation = src._nonStandardCreation;
/*  299 */     UnwrappedPropertyHandler uph = src._unwrappedPropertyHandler;
/*      */     
/*  301 */     if (unwrapper != null)
/*      */     {
/*  303 */       if (uph != null) {
/*  304 */         uph = uph.renameAll(unwrapper);
/*      */       }
/*      */       
/*  307 */       this._beanProperties = src._beanProperties.renameAll(unwrapper);
/*      */     } else {
/*  309 */       this._beanProperties = src._beanProperties;
/*      */     }
/*  311 */     this._unwrappedPropertyHandler = uph;
/*  312 */     this._needViewProcesing = src._needViewProcesing;
/*  313 */     this._serializationShape = src._serializationShape;
/*      */     
/*      */ 
/*  316 */     this._vanillaProcessing = false;
/*      */   }
/*      */   
/*      */   public BeanDeserializerBase(BeanDeserializerBase src, ObjectIdReader oir)
/*      */   {
/*  321 */     super(src._beanType);
/*      */     
/*  323 */     this._classAnnotations = src._classAnnotations;
/*  324 */     this._beanType = src._beanType;
/*      */     
/*  326 */     this._valueInstantiator = src._valueInstantiator;
/*  327 */     this._delegateDeserializer = src._delegateDeserializer;
/*  328 */     this._propertyBasedCreator = src._propertyBasedCreator;
/*      */     
/*  330 */     this._backRefs = src._backRefs;
/*  331 */     this._ignorableProps = src._ignorableProps;
/*  332 */     this._ignoreAllUnknown = src._ignoreAllUnknown;
/*  333 */     this._anySetter = src._anySetter;
/*  334 */     this._injectables = src._injectables;
/*      */     
/*  336 */     this._nonStandardCreation = src._nonStandardCreation;
/*  337 */     this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
/*  338 */     this._needViewProcesing = src._needViewProcesing;
/*  339 */     this._serializationShape = src._serializationShape;
/*      */     
/*      */ 
/*  342 */     this._objectIdReader = oir;
/*      */     
/*  344 */     if (oir == null) {
/*  345 */       this._beanProperties = src._beanProperties;
/*  346 */       this._vanillaProcessing = src._vanillaProcessing;
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*  352 */       com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ObjectIdValueProperty idProp = new com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ObjectIdValueProperty(oir, com.facebook.presto.jdbc.internal.jackson.databind.PropertyMetadata.STD_REQUIRED);
/*  353 */       this._beanProperties = src._beanProperties.withProperty(idProp);
/*  354 */       this._vanillaProcessing = false;
/*      */     }
/*      */   }
/*      */   
/*      */   public BeanDeserializerBase(BeanDeserializerBase src, Set<String> ignorableProps)
/*      */   {
/*  360 */     super(src._beanType);
/*      */     
/*  362 */     this._classAnnotations = src._classAnnotations;
/*  363 */     this._beanType = src._beanType;
/*      */     
/*  365 */     this._valueInstantiator = src._valueInstantiator;
/*  366 */     this._delegateDeserializer = src._delegateDeserializer;
/*  367 */     this._propertyBasedCreator = src._propertyBasedCreator;
/*      */     
/*  369 */     this._backRefs = src._backRefs;
/*  370 */     this._ignorableProps = ignorableProps;
/*  371 */     this._ignoreAllUnknown = src._ignoreAllUnknown;
/*  372 */     this._anySetter = src._anySetter;
/*  373 */     this._injectables = src._injectables;
/*      */     
/*  375 */     this._nonStandardCreation = src._nonStandardCreation;
/*  376 */     this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
/*  377 */     this._needViewProcesing = src._needViewProcesing;
/*  378 */     this._serializationShape = src._serializationShape;
/*      */     
/*  380 */     this._vanillaProcessing = src._vanillaProcessing;
/*  381 */     this._objectIdReader = src._objectIdReader;
/*      */     
/*      */ 
/*      */ 
/*  385 */     this._beanProperties = src._beanProperties.withoutProperties(ignorableProps);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected BeanDeserializerBase(BeanDeserializerBase src, BeanPropertyMap beanProps)
/*      */   {
/*  393 */     super(src._beanType);
/*      */     
/*  395 */     this._classAnnotations = src._classAnnotations;
/*  396 */     this._beanType = src._beanType;
/*      */     
/*  398 */     this._valueInstantiator = src._valueInstantiator;
/*  399 */     this._delegateDeserializer = src._delegateDeserializer;
/*  400 */     this._propertyBasedCreator = src._propertyBasedCreator;
/*      */     
/*  402 */     this._beanProperties = beanProps;
/*  403 */     this._backRefs = src._backRefs;
/*  404 */     this._ignorableProps = src._ignorableProps;
/*  405 */     this._ignoreAllUnknown = src._ignoreAllUnknown;
/*  406 */     this._anySetter = src._anySetter;
/*  407 */     this._injectables = src._injectables;
/*  408 */     this._objectIdReader = src._objectIdReader;
/*      */     
/*  410 */     this._nonStandardCreation = src._nonStandardCreation;
/*  411 */     this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
/*  412 */     this._needViewProcesing = src._needViewProcesing;
/*  413 */     this._serializationShape = src._serializationShape;
/*      */     
/*  415 */     this._vanillaProcessing = src._vanillaProcessing;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public abstract JsonDeserializer<Object> unwrappingDeserializer(NameTransformer paramNameTransformer);
/*      */   
/*      */ 
/*      */ 
/*      */   public abstract BeanDeserializerBase withObjectIdReader(ObjectIdReader paramObjectIdReader);
/*      */   
/*      */ 
/*      */   public abstract BeanDeserializerBase withIgnorableProperties(Set<String> paramSet);
/*      */   
/*      */ 
/*      */   public BeanDeserializerBase withBeanProperties(BeanPropertyMap props)
/*      */   {
/*  432 */     throw new UnsupportedOperationException("Class " + getClass().getName() + " does not override `withBeanProperties()`, needs to");
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
/*      */   protected abstract BeanDeserializerBase asArrayDeserializer();
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
/*      */   public void resolve(DeserializationContext ctxt)
/*      */     throws JsonMappingException
/*      */   {
/*  460 */     ExternalTypeHandler.Builder extTypes = null;
/*      */     
/*      */     SettableBeanProperty[] creatorProps;
/*      */     SettableBeanProperty[] creatorProps;
/*  464 */     if (this._valueInstantiator.canCreateFromObjectWith()) {
/*  465 */       creatorProps = this._valueInstantiator.getFromObjectArguments(ctxt.getConfig());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  487 */       creatorProps = null;
/*      */     }
/*      */     
/*  490 */     UnwrappedPropertyHandler unwrapped = null;
/*      */     
/*  492 */     for (SettableBeanProperty origProp : this._beanProperties) {
/*  493 */       SettableBeanProperty prop = origProp;
/*      */       
/*      */ 
/*  496 */       if (!prop.hasValueDeserializer())
/*      */       {
/*  498 */         JsonDeserializer<?> deser = findConvertingDeserializer(ctxt, prop);
/*  499 */         if (deser == null) {
/*  500 */           deser = findDeserializer(ctxt, prop.getType(), prop);
/*      */         }
/*  502 */         prop = prop.withValueDeserializer(deser);
/*      */       } else {
/*  504 */         JsonDeserializer<Object> deser = prop.getValueDeserializer();
/*      */         
/*      */ 
/*      */ 
/*  508 */         JsonDeserializer<?> cd = ctxt.handlePrimaryContextualization(deser, prop, prop.getType());
/*      */         
/*  510 */         if (cd != deser) {
/*  511 */           prop = prop.withValueDeserializer(cd);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*  516 */       prop = _resolveManagedReferenceProperty(ctxt, prop);
/*      */       
/*      */ 
/*  519 */       if (!(prop instanceof com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ManagedReferenceProperty)) {
/*  520 */         prop = _resolvedObjectIdProperty(ctxt, prop);
/*      */       }
/*      */       
/*  523 */       SettableBeanProperty u = _resolveUnwrappedProperty(ctxt, prop);
/*  524 */       if (u != null) {
/*  525 */         prop = u;
/*  526 */         if (unwrapped == null) {
/*  527 */           unwrapped = new UnwrappedPropertyHandler();
/*      */         }
/*  529 */         unwrapped.addProperty(prop);
/*      */         
/*      */ 
/*      */ 
/*  533 */         this._beanProperties.remove(prop);
/*      */       }
/*      */       else
/*      */       {
/*  537 */         prop = _resolveInnerClassValuedProperty(ctxt, prop);
/*  538 */         if (prop != origProp) {
/*  539 */           this._beanProperties.replace(prop);
/*      */           
/*  541 */           if (creatorProps != null)
/*      */           {
/*      */ 
/*  544 */             int i = 0; for (int len = creatorProps.length; i < len; i++) {
/*  545 */               if (creatorProps[i] == origProp) {
/*  546 */                 creatorProps[i] = prop;
/*  547 */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
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
/*  562 */         if (prop.hasValueTypeDeserializer()) {
/*  563 */           TypeDeserializer typeDeser = prop.getValueTypeDeserializer();
/*  564 */           if (typeDeser.getTypeInclusion() == com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo.As.EXTERNAL_PROPERTY) {
/*  565 */             if (extTypes == null) {
/*  566 */               extTypes = new ExternalTypeHandler.Builder();
/*      */             }
/*  568 */             extTypes.addExternal(prop, typeDeser);
/*      */             
/*  570 */             this._beanProperties.remove(prop);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  576 */     if ((this._anySetter != null) && (!this._anySetter.hasValueDeserializer())) {
/*  577 */       this._anySetter = this._anySetter.withValueDeserializer(findDeserializer(ctxt, this._anySetter.getType(), this._anySetter.getProperty()));
/*      */     }
/*      */     
/*      */ 
/*  581 */     if (this._valueInstantiator.canCreateUsingDelegate()) {
/*  582 */       JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
/*  583 */       if (delegateType == null) {
/*  584 */         throw new IllegalArgumentException("Invalid delegate-creator definition for " + this._beanType + ": value instantiator (" + this._valueInstantiator.getClass().getName() + ") returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'");
/*      */       }
/*      */       
/*      */ 
/*  588 */       this._delegateDeserializer = _findDelegateDeserializer(ctxt, delegateType, this._valueInstantiator.getDelegateCreator());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  593 */     if (this._valueInstantiator.canCreateUsingArrayDelegate()) {
/*  594 */       JavaType delegateType = this._valueInstantiator.getArrayDelegateType(ctxt.getConfig());
/*  595 */       if (delegateType == null) {
/*  596 */         throw new IllegalArgumentException("Invalid array-delegate-creator definition for " + this._beanType + ": value instantiator (" + this._valueInstantiator.getClass().getName() + ") returned true for 'canCreateUsingArrayDelegate()', but null for 'getArrayDelegateType()'");
/*      */       }
/*      */       
/*      */ 
/*  600 */       this._arrayDelegateDeserializer = _findDelegateDeserializer(ctxt, delegateType, this._valueInstantiator.getArrayDelegateCreator());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  605 */     if (creatorProps != null) {
/*  606 */       this._propertyBasedCreator = PropertyBasedCreator.construct(ctxt, this._valueInstantiator, creatorProps);
/*      */     }
/*      */     
/*  609 */     if (extTypes != null)
/*      */     {
/*      */ 
/*  612 */       this._externalTypeIdHandler = extTypes.build(this._beanProperties);
/*      */       
/*  614 */       this._nonStandardCreation = true;
/*      */     }
/*      */     
/*  617 */     this._unwrappedPropertyHandler = unwrapped;
/*  618 */     if (unwrapped != null) {
/*  619 */       this._nonStandardCreation = true;
/*      */     }
/*      */     
/*      */ 
/*  623 */     this._vanillaProcessing = ((this._vanillaProcessing) && (!this._nonStandardCreation));
/*      */   }
/*      */   
/*      */   private JsonDeserializer<Object> _findDelegateDeserializer(DeserializationContext ctxt, JavaType delegateType, com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedWithParams delegateCreator)
/*      */     throws JsonMappingException
/*      */   {
/*  629 */     com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty.Std property = new com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty.Std(TEMP_PROPERTY_NAME, delegateType, null, this._classAnnotations, delegateCreator, com.facebook.presto.jdbc.internal.jackson.databind.PropertyMetadata.STD_OPTIONAL);
/*      */     
/*      */ 
/*      */ 
/*  633 */     TypeDeserializer td = (TypeDeserializer)delegateType.getTypeHandler();
/*  634 */     if (td == null) {
/*  635 */       td = ctxt.getConfig().findTypeDeserializer(delegateType);
/*      */     }
/*  637 */     JsonDeserializer<Object> dd = findDeserializer(ctxt, delegateType, property);
/*  638 */     if (td != null) {
/*  639 */       td = td.forProperty(property);
/*  640 */       return new com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.TypeWrappedDeserializer(td, dd);
/*      */     }
/*  642 */     return dd;
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
/*      */   protected JsonDeserializer<Object> findConvertingDeserializer(DeserializationContext ctxt, SettableBeanProperty prop)
/*      */     throws JsonMappingException
/*      */   {
/*  657 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*  658 */     if (intr != null) {
/*  659 */       Object convDef = intr.findDeserializationConverter(prop.getMember());
/*  660 */       if (convDef != null) {
/*  661 */         com.facebook.presto.jdbc.internal.jackson.databind.util.Converter<Object, Object> conv = ctxt.converterInstance(prop.getMember(), convDef);
/*  662 */         JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
/*  663 */         JsonDeserializer<?> ser = ctxt.findContextualValueDeserializer(delegateType, prop);
/*  664 */         return new com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StdDelegatingDeserializer(conv, delegateType, ser);
/*      */       }
/*      */     }
/*  667 */     return null;
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
/*      */   public JsonDeserializer<?> createContextual(DeserializationContext ctxt, com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty property)
/*      */     throws JsonMappingException
/*      */   {
/*  681 */     ObjectIdReader oir = this._objectIdReader;
/*      */     
/*      */ 
/*  684 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*  685 */     com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember accessor = (property == null) || (intr == null) ? null : property.getMember();
/*      */     
/*  687 */     if ((accessor != null) && (intr != null)) {
/*  688 */       ObjectIdInfo objectIdInfo = intr.findObjectIdInfo(accessor);
/*  689 */       if (objectIdInfo != null)
/*      */       {
/*  691 */         objectIdInfo = intr.findObjectReferenceInfo(accessor, objectIdInfo);
/*      */         
/*  693 */         Class<?> implClass = objectIdInfo.getGeneratorType();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  698 */         com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdResolver resolver = ctxt.objectIdResolverInstance(accessor, objectIdInfo);
/*  699 */         com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerator<?> idGen; JavaType idType; SettableBeanProperty idProp; com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerator<?> idGen; if (implClass == com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerators.PropertyGenerator.class) {
/*  700 */           PropertyName propName = objectIdInfo.getPropertyName();
/*  701 */           SettableBeanProperty idProp = findProperty(propName);
/*  702 */           if (idProp == null) {
/*  703 */             throw new IllegalArgumentException("Invalid Object Id definition for " + handledType().getName() + ": can not find property with name '" + propName + "'");
/*      */           }
/*      */           
/*  706 */           JavaType idType = idProp.getType();
/*  707 */           idGen = new com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator(objectIdInfo.getScope());
/*      */         } else {
/*  709 */           JavaType type = ctxt.constructType(implClass);
/*  710 */           idType = ctxt.getTypeFactory().findTypeParameters(type, com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerator.class)[0];
/*  711 */           idProp = null;
/*  712 */           idGen = ctxt.objectIdGeneratorInstance(accessor, objectIdInfo);
/*      */         }
/*  714 */         JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(idType);
/*  715 */         oir = ObjectIdReader.construct(idType, objectIdInfo.getPropertyName(), idGen, deser, idProp, resolver);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  720 */     BeanDeserializerBase contextual = this;
/*  721 */     if ((oir != null) && (oir != this._objectIdReader)) {
/*  722 */       contextual = contextual.withObjectIdReader(oir);
/*      */     }
/*      */     
/*  725 */     if (accessor != null) {
/*  726 */       com.facebook.presto.jdbc.internal.jackson.annotation.JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnorals(accessor);
/*  727 */       if (ignorals != null) {
/*  728 */         Set<String> ignored = ignorals.findIgnoredForDeserialization();
/*  729 */         if (!ignored.isEmpty()) {
/*  730 */           Set<String> prev = contextual._ignorableProps;
/*  731 */           if ((prev != null) && (!prev.isEmpty())) {
/*  732 */             ignored = new java.util.HashSet(ignored);
/*  733 */             ignored.addAll(prev);
/*      */           }
/*  735 */           contextual = contextual.withIgnorableProperties(ignored);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  741 */     JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());
/*  742 */     com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Shape shape = null;
/*  743 */     if (format != null) {
/*  744 */       if (format.hasShape()) {
/*  745 */         shape = format.getShape();
/*      */       }
/*      */       
/*  748 */       Boolean B = format.getFeature(com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
/*  749 */       if (B != null)
/*      */       {
/*  751 */         BeanPropertyMap propsOrig = this._beanProperties;
/*  752 */         BeanPropertyMap props = propsOrig.withCaseInsensitivity(B.booleanValue());
/*  753 */         if (props != propsOrig) {
/*  754 */           contextual = contextual.withBeanProperties(props);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  759 */     if (shape == null) {
/*  760 */       shape = this._serializationShape;
/*      */     }
/*  762 */     if (shape == com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Shape.ARRAY) {
/*  763 */       contextual = contextual.asArrayDeserializer();
/*      */     }
/*  765 */     return contextual;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SettableBeanProperty _resolveManagedReferenceProperty(DeserializationContext ctxt, SettableBeanProperty prop)
/*      */   {
/*  775 */     String refName = prop.getManagedReferenceName();
/*  776 */     if (refName == null) {
/*  777 */       return prop;
/*      */     }
/*  779 */     JsonDeserializer<?> valueDeser = prop.getValueDeserializer();
/*  780 */     SettableBeanProperty backProp = valueDeser.findBackReference(refName);
/*  781 */     if (backProp == null) {
/*  782 */       throw new IllegalArgumentException("Can not handle managed/back reference '" + refName + "': no back reference property found from type " + prop.getType());
/*      */     }
/*      */     
/*      */ 
/*  786 */     JavaType referredType = this._beanType;
/*  787 */     JavaType backRefType = backProp.getType();
/*  788 */     boolean isContainer = prop.getType().isContainerType();
/*  789 */     if (!backRefType.getRawClass().isAssignableFrom(referredType.getRawClass())) {
/*  790 */       throw new IllegalArgumentException("Can not handle managed/back reference '" + refName + "': back reference type (" + backRefType.getRawClass().getName() + ") not compatible with managed type (" + referredType.getRawClass().getName() + ")");
/*      */     }
/*      */     
/*      */ 
/*  794 */     return new com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ManagedReferenceProperty(prop, refName, backProp, this._classAnnotations, isContainer);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SettableBeanProperty _resolvedObjectIdProperty(DeserializationContext ctxt, SettableBeanProperty prop)
/*      */     throws JsonMappingException
/*      */   {
/*  805 */     ObjectIdInfo objectIdInfo = prop.getObjectIdInfo();
/*  806 */     JsonDeserializer<Object> valueDeser = prop.getValueDeserializer();
/*  807 */     ObjectIdReader objectIdReader = valueDeser.getObjectIdReader();
/*  808 */     if ((objectIdInfo == null) && (objectIdReader == null)) {
/*  809 */       return prop;
/*      */     }
/*  811 */     return new com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ObjectIdReferenceProperty(prop, objectIdInfo);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected SettableBeanProperty _resolveUnwrappedProperty(DeserializationContext ctxt, SettableBeanProperty prop)
/*      */   {
/*  821 */     com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember am = prop.getMember();
/*  822 */     if (am != null) {
/*  823 */       NameTransformer unwrapper = ctxt.getAnnotationIntrospector().findUnwrappingNameTransformer(am);
/*  824 */       if (unwrapper != null) {
/*  825 */         JsonDeserializer<Object> orig = prop.getValueDeserializer();
/*  826 */         JsonDeserializer<Object> unwrapping = orig.unwrappingDeserializer(unwrapper);
/*  827 */         if ((unwrapping != orig) && (unwrapping != null))
/*      */         {
/*  829 */           return prop.withValueDeserializer(unwrapping);
/*      */         }
/*      */       }
/*      */     }
/*  833 */     return null;
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
/*      */   protected SettableBeanProperty _resolveInnerClassValuedProperty(DeserializationContext ctxt, SettableBeanProperty prop)
/*      */   {
/*  846 */     JsonDeserializer<Object> deser = prop.getValueDeserializer();
/*      */     
/*  848 */     if ((deser instanceof BeanDeserializerBase)) {
/*  849 */       BeanDeserializerBase bd = (BeanDeserializerBase)deser;
/*  850 */       ValueInstantiator vi = bd.getValueInstantiator();
/*  851 */       if (!vi.canCreateUsingDefault()) {
/*  852 */         Class<?> valueClass = prop.getType().getRawClass();
/*  853 */         Class<?> enclosing = com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil.getOuterClass(valueClass);
/*      */         
/*  855 */         if ((enclosing != null) && (enclosing == this._beanType.getRawClass())) {
/*  856 */           for (java.lang.reflect.Constructor<?> ctor : valueClass.getConstructors()) {
/*  857 */             Class<?>[] paramTypes = ctor.getParameterTypes();
/*  858 */             if ((paramTypes.length == 1) && (paramTypes[0] == enclosing)) {
/*  859 */               if (ctxt.canOverrideAccessModifiers()) {
/*  860 */                 com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil.checkAndFixAccess(ctor, ctxt.isEnabled(com.facebook.presto.jdbc.internal.jackson.databind.MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*      */               }
/*  862 */               return new com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.InnerClassProperty(prop, ctor);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  868 */     return prop;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isCachable()
/*      */   {
/*  878 */     return true;
/*      */   }
/*      */   
/*      */   public Class<?> handledType() {
/*  882 */     return this._beanType.getRawClass();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectIdReader getObjectIdReader()
/*      */   {
/*  892 */     return this._objectIdReader;
/*      */   }
/*      */   
/*      */   public boolean hasProperty(String propertyName) {
/*  896 */     return this._beanProperties.find(propertyName) != null;
/*      */   }
/*      */   
/*      */   public boolean hasViews() {
/*  900 */     return this._needViewProcesing;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public int getPropertyCount()
/*      */   {
/*  907 */     return this._beanProperties.size();
/*      */   }
/*      */   
/*      */   public java.util.Collection<Object> getKnownPropertyNames()
/*      */   {
/*  912 */     ArrayList<Object> names = new ArrayList();
/*  913 */     for (SettableBeanProperty prop : this._beanProperties) {
/*  914 */       names.add(prop.getName());
/*      */     }
/*  916 */     return names;
/*      */   }
/*      */   
/*      */ 
/*      */   @Deprecated
/*      */   public final Class<?> getBeanClass()
/*      */   {
/*  923 */     return this._beanType.getRawClass();
/*      */   }
/*      */   
/*  926 */   public JavaType getValueType() { return this._beanType; }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public java.util.Iterator<SettableBeanProperty> properties()
/*      */   {
/*  937 */     if (this._beanProperties == null) {
/*  938 */       throw new IllegalStateException("Can only call after BeanDeserializer has been resolved");
/*      */     }
/*  940 */     return this._beanProperties.iterator();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public java.util.Iterator<SettableBeanProperty> creatorProperties()
/*      */   {
/*  952 */     if (this._propertyBasedCreator == null) {
/*  953 */       return java.util.Collections.emptyList().iterator();
/*      */     }
/*  955 */     return this._propertyBasedCreator.properties().iterator();
/*      */   }
/*      */   
/*      */ 
/*      */   public SettableBeanProperty findProperty(PropertyName propertyName)
/*      */   {
/*  961 */     return findProperty(propertyName.getSimpleName());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SettableBeanProperty findProperty(String propertyName)
/*      */   {
/*  973 */     SettableBeanProperty prop = this._beanProperties == null ? null : this._beanProperties.find(propertyName);
/*      */     
/*  975 */     if ((prop == null) && (this._propertyBasedCreator != null)) {
/*  976 */       prop = this._propertyBasedCreator.findCreatorProperty(propertyName);
/*      */     }
/*  978 */     return prop;
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
/*      */   public SettableBeanProperty findProperty(int propertyIndex)
/*      */   {
/*  993 */     SettableBeanProperty prop = this._beanProperties == null ? null : this._beanProperties.find(propertyIndex);
/*      */     
/*  995 */     if ((prop == null) && (this._propertyBasedCreator != null)) {
/*  996 */       prop = this._propertyBasedCreator.findCreatorProperty(propertyIndex);
/*      */     }
/*  998 */     return prop;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public SettableBeanProperty findBackReference(String logicalName)
/*      */   {
/* 1008 */     if (this._backRefs == null) {
/* 1009 */       return null;
/*      */     }
/* 1011 */     return (SettableBeanProperty)this._backRefs.get(logicalName);
/*      */   }
/*      */   
/*      */   public ValueInstantiator getValueInstantiator() {
/* 1015 */     return this._valueInstantiator;
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
/*      */   public void replaceProperty(SettableBeanProperty original, SettableBeanProperty replacement)
/*      */   {
/* 1039 */     this._beanProperties.replace(replacement);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public abstract Object deserializeFromObject(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
/*      */     throws IOException;
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
/* 1061 */     if (this._objectIdReader != null)
/*      */     {
/* 1063 */       if (p.canReadObjectId()) {
/* 1064 */         Object id = p.getObjectId();
/* 1065 */         if (id != null) {
/* 1066 */           Object ob = typeDeserializer.deserializeTypedFromObject(p, ctxt);
/* 1067 */           return _handleTypedObjectId(p, ctxt, ob, id);
/*      */         }
/*      */       }
/*      */       
/* 1071 */       JsonToken t = p.getCurrentToken();
/* 1072 */       if (t != null)
/*      */       {
/* 1074 */         if (t.isScalarValue()) {
/* 1075 */           return deserializeFromObjectId(p, ctxt);
/*      */         }
/*      */         
/* 1078 */         if (t == JsonToken.START_OBJECT) {
/* 1079 */           t = p.nextToken();
/*      */         }
/* 1081 */         if ((t == JsonToken.FIELD_NAME) && (this._objectIdReader.maySerializeAsObject()) && (this._objectIdReader.isValidReferencePropertyName(p.getCurrentName(), p)))
/*      */         {
/* 1083 */           return deserializeFromObjectId(p, ctxt);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1088 */     return typeDeserializer.deserializeTypedFromObject(p, ctxt);
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
/*      */   protected Object _handleTypedObjectId(JsonParser p, DeserializationContext ctxt, Object pojo, Object rawId)
/*      */     throws IOException
/*      */   {
/* 1103 */     JsonDeserializer<Object> idDeser = this._objectIdReader.getDeserializer();
/*      */     
/*      */     Object id;
/*      */     Object id;
/* 1107 */     if (idDeser.handledType() == rawId.getClass())
/*      */     {
/* 1109 */       id = rawId;
/*      */     } else {
/* 1111 */       id = _convertObjectId(p, ctxt, rawId, idDeser);
/*      */     }
/*      */     
/* 1114 */     ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
/* 1115 */     roid.bindItem(pojo);
/*      */     
/* 1117 */     SettableBeanProperty idProp = this._objectIdReader.idProperty;
/* 1118 */     if (idProp != null) {
/* 1119 */       return idProp.setAndReturn(pojo, id);
/*      */     }
/* 1121 */     return pojo;
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
/*      */   protected Object _convertObjectId(JsonParser p, DeserializationContext ctxt, Object rawId, JsonDeserializer<Object> idDeser)
/*      */     throws IOException
/*      */   {
/* 1137 */     TokenBuffer buf = new TokenBuffer(p, ctxt);
/* 1138 */     if ((rawId instanceof String)) {
/* 1139 */       buf.writeString((String)rawId);
/* 1140 */     } else if ((rawId instanceof Long)) {
/* 1141 */       buf.writeNumber(((Long)rawId).longValue());
/* 1142 */     } else if ((rawId instanceof Integer)) {
/* 1143 */       buf.writeNumber(((Integer)rawId).intValue());
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1148 */       buf.writeObject(rawId);
/*      */     }
/* 1150 */     JsonParser bufParser = buf.asParser();
/* 1151 */     bufParser.nextToken();
/* 1152 */     return idDeser.deserialize(bufParser, ctxt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Object deserializeWithObjectId(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/* 1165 */     return deserializeFromObject(p, ctxt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Object deserializeFromObjectId(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/* 1174 */     Object id = this._objectIdReader.readObjectReference(p, ctxt);
/* 1175 */     ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
/*      */     
/* 1177 */     Object pojo = roid.resolve();
/* 1178 */     if (pojo == null) {
/* 1179 */       throw new UnresolvedForwardReference(p, "Could not resolve Object Id [" + id + "] (for " + this._beanType + ").", p.getCurrentLocation(), roid);
/*      */     }
/*      */     
/*      */ 
/* 1183 */     return pojo;
/*      */   }
/*      */   
/*      */   protected Object deserializeFromObjectUsingNonDefault(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/* 1189 */     if (this._delegateDeserializer != null) {
/* 1190 */       return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
/*      */     }
/*      */     
/* 1193 */     if (this._propertyBasedCreator != null) {
/* 1194 */       return _deserializeUsingPropertyBased(p, ctxt);
/*      */     }
/*      */     
/* 1197 */     if (this._beanType.isAbstract()) {
/* 1198 */       return ctxt.handleMissingInstantiator(handledType(), p, "abstract type (need to add/enable type information?)", new Object[0]);
/*      */     }
/*      */     
/* 1201 */     return ctxt.handleMissingInstantiator(this._beanType.getRawClass(), p, "no suitable constructor found, can not deserialize from Object value (missing default constructor or creator, or perhaps need to add/enable type information?)", new Object[0]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected abstract Object _deserializeUsingPropertyBased(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
/*      */     throws IOException, com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*      */   
/*      */ 
/*      */   public Object deserializeFromNumber(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/* 1213 */     if (this._objectIdReader != null) {
/* 1214 */       return deserializeFromObjectId(p, ctxt);
/*      */     }
/*      */     
/* 1217 */     switch (p.getNumberType()) {
/*      */     case INT: 
/* 1219 */       if ((this._delegateDeserializer != null) && 
/* 1220 */         (!this._valueInstantiator.canCreateFromInt())) {
/* 1221 */         Object bean = this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
/*      */         
/* 1223 */         if (this._injectables != null) {
/* 1224 */           injectValues(ctxt, bean);
/*      */         }
/* 1226 */         return bean;
/*      */       }
/*      */       
/* 1229 */       return this._valueInstantiator.createFromInt(ctxt, p.getIntValue());
/*      */     case LONG: 
/* 1231 */       if ((this._delegateDeserializer != null) && 
/* 1232 */         (!this._valueInstantiator.canCreateFromInt())) {
/* 1233 */         Object bean = this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
/*      */         
/* 1235 */         if (this._injectables != null) {
/* 1236 */           injectValues(ctxt, bean);
/*      */         }
/* 1238 */         return bean;
/*      */       }
/*      */       
/* 1241 */       return this._valueInstantiator.createFromLong(ctxt, p.getLongValue());
/*      */     }
/*      */     
/* 1244 */     if (this._delegateDeserializer != null) {
/* 1245 */       Object bean = this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
/*      */       
/* 1247 */       if (this._injectables != null) {
/* 1248 */         injectValues(ctxt, bean);
/*      */       }
/* 1250 */       return bean;
/*      */     }
/* 1252 */     return ctxt.handleMissingInstantiator(handledType(), p, "no suitable creator method found to deserialize from Number value (%s)", new Object[] { p.getNumberValue() });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Object deserializeFromString(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/* 1260 */     if (this._objectIdReader != null) {
/* 1261 */       return deserializeFromObjectId(p, ctxt);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1267 */     if ((this._delegateDeserializer != null) && 
/* 1268 */       (!this._valueInstantiator.canCreateFromString())) {
/* 1269 */       Object bean = this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
/* 1270 */       if (this._injectables != null) {
/* 1271 */         injectValues(ctxt, bean);
/*      */       }
/* 1273 */       return bean;
/*      */     }
/*      */     
/* 1276 */     return this._valueInstantiator.createFromString(ctxt, p.getText());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object deserializeFromDouble(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/* 1285 */     JsonParser.NumberType t = p.getNumberType();
/*      */     
/* 1287 */     if ((t == JsonParser.NumberType.DOUBLE) || (t == JsonParser.NumberType.FLOAT)) {
/* 1288 */       if ((this._delegateDeserializer != null) && 
/* 1289 */         (!this._valueInstantiator.canCreateFromDouble())) {
/* 1290 */         Object bean = this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
/* 1291 */         if (this._injectables != null) {
/* 1292 */           injectValues(ctxt, bean);
/*      */         }
/* 1294 */         return bean;
/*      */       }
/*      */       
/* 1297 */       return this._valueInstantiator.createFromDouble(ctxt, p.getDoubleValue());
/*      */     }
/*      */     
/* 1300 */     if (this._delegateDeserializer != null) {
/* 1301 */       return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
/*      */     }
/* 1303 */     return ctxt.handleMissingInstantiator(handledType(), p, "no suitable creator method found to deserialize from Number value (%s)", new Object[] { p.getNumberValue() });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object deserializeFromBoolean(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/* 1313 */     if ((this._delegateDeserializer != null) && 
/* 1314 */       (!this._valueInstantiator.canCreateFromBoolean())) {
/* 1315 */       Object bean = this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
/* 1316 */       if (this._injectables != null) {
/* 1317 */         injectValues(ctxt, bean);
/*      */       }
/* 1319 */       return bean;
/*      */     }
/*      */     
/* 1322 */     boolean value = p.getCurrentToken() == JsonToken.VALUE_TRUE;
/* 1323 */     return this._valueInstantiator.createFromBoolean(ctxt, value);
/*      */   }
/*      */   
/*      */   public Object deserializeFromArray(JsonParser p, DeserializationContext ctxt) throws IOException
/*      */   {
/* 1328 */     if (this._arrayDelegateDeserializer != null) {
/*      */       try {
/* 1330 */         Object bean = this._valueInstantiator.createUsingArrayDelegate(ctxt, this._arrayDelegateDeserializer.deserialize(p, ctxt));
/* 1331 */         if (this._injectables != null) {
/* 1332 */           injectValues(ctxt, bean);
/*      */         }
/* 1334 */         return bean;
/*      */       } catch (Exception e) {
/* 1336 */         return wrapInstantiationProblem(e, ctxt);
/*      */       }
/*      */     }
/*      */     
/* 1340 */     if (this._delegateDeserializer != null) {
/*      */       try {
/* 1342 */         Object bean = this._valueInstantiator.createUsingArrayDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
/* 1343 */         if (this._injectables != null) {
/* 1344 */           injectValues(ctxt, bean);
/*      */         }
/* 1346 */         return bean;
/*      */       } catch (Exception e) {
/* 1348 */         wrapInstantiationProblem(e, ctxt);
/* 1349 */         return null;
/*      */       }
/*      */     }
/* 1352 */     if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/* 1353 */       JsonToken t = p.nextToken();
/* 1354 */       if ((t == JsonToken.END_ARRAY) && (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT))) {
/* 1355 */         return null;
/*      */       }
/* 1357 */       Object value = deserialize(p, ctxt);
/* 1358 */       if (p.nextToken() != JsonToken.END_ARRAY) {
/* 1359 */         handleMissingEndArrayForSingle(p, ctxt);
/*      */       }
/* 1361 */       return value;
/*      */     }
/* 1363 */     if (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
/* 1364 */       JsonToken t = p.nextToken();
/* 1365 */       if (t == JsonToken.END_ARRAY) {
/* 1366 */         return null;
/*      */       }
/* 1368 */       return ctxt.handleUnexpectedToken(handledType(), JsonToken.START_ARRAY, p, null, new Object[0]);
/*      */     }
/*      */     
/* 1371 */     return ctxt.handleUnexpectedToken(handledType(), p);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public Object deserializeFromEmbedded(JsonParser p, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/* 1379 */     if (this._objectIdReader != null) {
/* 1380 */       return deserializeFromObjectId(p, ctxt);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1385 */     return p.getEmbeddedObject();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void injectValues(DeserializationContext ctxt, Object bean)
/*      */     throws IOException
/*      */   {
/* 1397 */     for (ValueInjector injector : this._injectables) {
/* 1398 */       injector.inject(ctxt, bean);
/*      */     }
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
/*      */   protected Object handleUnknownProperties(DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens)
/*      */     throws IOException
/*      */   {
/* 1413 */     unknownTokens.writeEndObject();
/*      */     
/*      */ 
/* 1416 */     JsonParser bufferParser = unknownTokens.asParser();
/* 1417 */     while (bufferParser.nextToken() != JsonToken.END_OBJECT) {
/* 1418 */       String propName = bufferParser.getCurrentName();
/*      */       
/* 1420 */       bufferParser.nextToken();
/* 1421 */       handleUnknownProperty(bufferParser, ctxt, bean, propName);
/*      */     }
/* 1423 */     return bean;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void handleUnknownVanilla(JsonParser p, DeserializationContext ctxt, Object bean, String propName)
/*      */     throws IOException
/*      */   {
/* 1434 */     if ((this._ignorableProps != null) && (this._ignorableProps.contains(propName))) {
/* 1435 */       handleIgnoredProperty(p, ctxt, bean, propName);
/* 1436 */     } else if (this._anySetter != null) {
/*      */       try
/*      */       {
/* 1439 */         this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
/*      */       } catch (Exception e) {
/* 1441 */         wrapAndThrow(e, bean, propName, ctxt);
/*      */       }
/*      */       
/*      */     } else {
/* 1445 */       handleUnknownProperty(p, ctxt, bean, propName);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void handleUnknownProperty(JsonParser p, DeserializationContext ctxt, Object beanOrClass, String propName)
/*      */     throws IOException
/*      */   {
/* 1458 */     if (this._ignoreAllUnknown) {
/* 1459 */       p.skipChildren();
/* 1460 */       return;
/*      */     }
/* 1462 */     if ((this._ignorableProps != null) && (this._ignorableProps.contains(propName))) {
/* 1463 */       handleIgnoredProperty(p, ctxt, beanOrClass, propName);
/*      */     }
/*      */     
/*      */ 
/* 1467 */     super.handleUnknownProperty(p, ctxt, beanOrClass, propName);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void handleIgnoredProperty(JsonParser p, DeserializationContext ctxt, Object beanOrClass, String propName)
/*      */     throws IOException
/*      */   {
/* 1480 */     if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)) {
/* 1481 */       throw com.facebook.presto.jdbc.internal.jackson.databind.exc.IgnoredPropertyException.from(p, beanOrClass, propName, getKnownPropertyNames());
/*      */     }
/* 1483 */     p.skipChildren();
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
/*      */   protected Object handlePolymorphic(JsonParser p, DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens)
/*      */     throws IOException
/*      */   {
/* 1503 */     JsonDeserializer<Object> subDeser = _findSubclassDeserializer(ctxt, bean, unknownTokens);
/* 1504 */     if (subDeser != null) {
/* 1505 */       if (unknownTokens != null)
/*      */       {
/* 1507 */         unknownTokens.writeEndObject();
/* 1508 */         JsonParser p2 = unknownTokens.asParser();
/* 1509 */         p2.nextToken();
/* 1510 */         bean = subDeser.deserialize(p2, ctxt, bean);
/*      */       }
/*      */       
/* 1513 */       if (p != null) {
/* 1514 */         bean = subDeser.deserialize(p, ctxt, bean);
/*      */       }
/* 1516 */       return bean;
/*      */     }
/*      */     
/* 1519 */     if (unknownTokens != null) {
/* 1520 */       bean = handleUnknownProperties(ctxt, bean, unknownTokens);
/*      */     }
/*      */     
/* 1523 */     if (p != null) {
/* 1524 */       bean = deserialize(p, ctxt, bean);
/*      */     }
/* 1526 */     return bean;
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
/*      */   protected JsonDeserializer<Object> _findSubclassDeserializer(DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens)
/*      */     throws IOException
/*      */   {
/* 1540 */     synchronized (this) {
/* 1541 */       subDeser = this._subDeserializers == null ? null : (JsonDeserializer)this._subDeserializers.get(new com.facebook.presto.jdbc.internal.jackson.databind.type.ClassKey(bean.getClass()));
/*      */     }
/* 1543 */     if (subDeser != null) {
/* 1544 */       return subDeser;
/*      */     }
/*      */     
/* 1547 */     JavaType type = ctxt.constructType(bean.getClass());
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1554 */     JsonDeserializer<Object> subDeser = ctxt.findRootValueDeserializer(type);
/*      */     
/* 1556 */     if (subDeser != null) {
/* 1557 */       synchronized (this) {
/* 1558 */         if (this._subDeserializers == null) {
/* 1559 */           this._subDeserializers = new HashMap();
/*      */         }
/* 1561 */         this._subDeserializers.put(new com.facebook.presto.jdbc.internal.jackson.databind.type.ClassKey(bean.getClass()), subDeser);
/*      */       }
/*      */     }
/* 1564 */     return subDeser;
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
/*      */   public void wrapAndThrow(Throwable t, Object bean, String fieldName, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/* 1589 */     throw JsonMappingException.wrapWithPath(throwOrReturnThrowable(t, ctxt), bean, fieldName);
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public void wrapAndThrow(Throwable t, Object bean, int index, DeserializationContext ctxt) throws IOException
/*      */   {
/* 1595 */     throw JsonMappingException.wrapWithPath(throwOrReturnThrowable(t, ctxt), bean, index);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Throwable throwOrReturnThrowable(Throwable t, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/* 1605 */     while (((t instanceof java.lang.reflect.InvocationTargetException)) && (t.getCause() != null)) {
/* 1606 */       t = t.getCause();
/*      */     }
/*      */     
/* 1609 */     if ((t instanceof Error)) {
/* 1610 */       throw ((Error)t);
/*      */     }
/* 1612 */     boolean wrap = (ctxt == null) || (ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS));
/*      */     
/* 1614 */     if ((t instanceof IOException)) {
/* 1615 */       if ((!wrap) || (!(t instanceof com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException))) {
/* 1616 */         throw ((IOException)t);
/*      */       }
/* 1618 */     } else if ((!wrap) && 
/* 1619 */       ((t instanceof RuntimeException))) {
/* 1620 */       throw ((RuntimeException)t);
/*      */     }
/*      */     
/* 1623 */     return t;
/*      */   }
/*      */   
/*      */   protected Object wrapInstantiationProblem(Throwable t, DeserializationContext ctxt)
/*      */     throws IOException
/*      */   {
/* 1629 */     while (((t instanceof java.lang.reflect.InvocationTargetException)) && (t.getCause() != null)) {
/* 1630 */       t = t.getCause();
/*      */     }
/*      */     
/* 1633 */     if ((t instanceof Error)) {
/* 1634 */       throw ((Error)t);
/*      */     }
/* 1636 */     boolean wrap = (ctxt == null) || (ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS));
/* 1637 */     if ((t instanceof IOException))
/*      */     {
/* 1639 */       throw ((IOException)t); }
/* 1640 */     if ((!wrap) && 
/* 1641 */       ((t instanceof RuntimeException))) {
/* 1642 */       throw ((RuntimeException)t);
/*      */     }
/*      */     
/* 1645 */     return ctxt.handleInstantiationProblem(this._beanType.getRawClass(), null, t);
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\BeanDeserializerBase.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */