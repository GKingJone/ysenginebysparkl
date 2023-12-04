/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.ser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonIgnoreProperties.Value;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo.As;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerators.PropertyGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector.ReferenceProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty.Std;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.MapperFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyMetadata;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.ConfigOverride;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.SerializerFactoryConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedClass;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedField;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMethod;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.BeanPropertyDefinition;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.ObjectIdInfo;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.NamedType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.SubtypeResolver;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeResolverBuilder;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.FilteredBeanPropertyWriter;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.ObjectIdWriter;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.PropertyBasedObjectIdGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.std.AtomicReferenceSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.std.MapSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.std.StdDelegatingSerializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ReferenceType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.Converter;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.atomic.AtomicReference;
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
/*     */ public class BeanSerializerFactory
/*     */   extends BasicSerializerFactory
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  67 */   public static final BeanSerializerFactory instance = new BeanSerializerFactory(null);
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
/*     */   protected BeanSerializerFactory(SerializerFactoryConfig config)
/*     */   {
/*  80 */     super(config);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public SerializerFactory withConfig(SerializerFactoryConfig config)
/*     */   {
/*  92 */     if (this._factoryConfig == config) {
/*  93 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 101 */     if (getClass() != BeanSerializerFactory.class) {
/* 102 */       throw new IllegalStateException("Subtype of BeanSerializerFactory (" + getClass().getName() + ") has not properly overridden method 'withAdditionalSerializers': can not instantiate subtype with " + "additional serializer definitions");
/*     */     }
/*     */     
/*     */ 
/* 106 */     return new BeanSerializerFactory(config);
/*     */   }
/*     */   
/*     */   protected Iterable<Serializers> customSerializers()
/*     */   {
/* 111 */     return this._factoryConfig.serializers();
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
/*     */   public JsonSerializer<Object> createSerializer(SerializerProvider prov, JavaType origType)
/*     */     throws JsonMappingException
/*     */   {
/* 137 */     SerializationConfig config = prov.getConfig();
/* 138 */     BeanDescription beanDesc = config.introspect(origType);
/* 139 */     JsonSerializer<?> ser = findSerializerFromAnnotation(prov, beanDesc.getClassInfo());
/* 140 */     if (ser != null) {
/* 141 */       return ser;
/*     */     }
/*     */     
/*     */ 
/* 145 */     AnnotationIntrospector intr = config.getAnnotationIntrospector();
/* 146 */     JavaType type = intr == null ? origType : intr.refineSerializationType(config, beanDesc.getClassInfo(), origType);
/*     */     boolean staticTyping;
/* 148 */     boolean staticTyping; if (type == origType) {
/* 149 */       staticTyping = false;
/*     */     } else {
/* 151 */       staticTyping = true;
/* 152 */       if (!type.hasRawClass(origType.getRawClass())) {
/* 153 */         beanDesc = config.introspect(type);
/*     */       }
/*     */     }
/*     */     
/* 157 */     Converter<Object, Object> conv = beanDesc.findSerializationConverter();
/* 158 */     if (conv == null) {
/* 159 */       return _createSerializer2(prov, type, beanDesc, staticTyping);
/*     */     }
/* 161 */     JavaType delegateType = conv.getOutputType(prov.getTypeFactory());
/*     */     
/*     */ 
/* 164 */     if (!delegateType.hasRawClass(type.getRawClass())) {
/* 165 */       beanDesc = config.introspect(delegateType);
/*     */       
/* 167 */       ser = findSerializerFromAnnotation(prov, beanDesc.getClassInfo());
/*     */     }
/*     */     
/* 170 */     if ((ser == null) && (!delegateType.isJavaLangObject())) {
/* 171 */       ser = _createSerializer2(prov, delegateType, beanDesc, true);
/*     */     }
/* 173 */     return new StdDelegatingSerializer(conv, delegateType, ser);
/*     */   }
/*     */   
/*     */ 
/*     */   protected JsonSerializer<?> _createSerializer2(SerializerProvider prov, JavaType type, BeanDescription beanDesc, boolean staticTyping)
/*     */     throws JsonMappingException
/*     */   {
/* 180 */     JsonSerializer<?> ser = null;
/* 181 */     SerializationConfig config = prov.getConfig();
/*     */     
/*     */ 
/*     */ 
/* 185 */     if (type.isContainerType()) {
/* 186 */       if (!staticTyping) {
/* 187 */         staticTyping = usesStaticTyping(config, beanDesc, null);
/*     */       }
/*     */       
/* 190 */       ser = buildContainerSerializer(prov, type, beanDesc, staticTyping);
/*     */       
/* 192 */       if (ser != null) {
/* 193 */         return ser;
/*     */       }
/*     */     } else {
/* 196 */       if (type.isReferenceType()) {
/* 197 */         ser = findReferenceSerializer(prov, (ReferenceType)type, beanDesc, staticTyping);
/*     */       }
/*     */       else {
/* 200 */         for (Serializers serializers : customSerializers()) {
/* 201 */           ser = serializers.findSerializer(config, type, beanDesc);
/* 202 */           if (ser != null) {
/*     */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 209 */       if (ser == null) {
/* 210 */         ser = findSerializerByAnnotations(prov, type, beanDesc);
/*     */       }
/*     */     }
/*     */     
/* 214 */     if (ser == null)
/*     */     {
/*     */ 
/*     */ 
/* 218 */       ser = findSerializerByLookup(type, config, beanDesc, staticTyping);
/* 219 */       if (ser == null) {
/* 220 */         ser = findSerializerByPrimaryType(prov, type, beanDesc, staticTyping);
/* 221 */         if (ser == null)
/*     */         {
/*     */ 
/*     */ 
/* 225 */           ser = findBeanSerializer(prov, type, beanDesc);
/*     */           
/* 227 */           if (ser == null) {
/* 228 */             ser = findSerializerByAddonType(config, type, beanDesc, staticTyping);
/*     */             
/*     */ 
/*     */ 
/* 232 */             if (ser == null) {
/* 233 */               ser = prov.getUnknownTypeSerializer(beanDesc.getBeanClass());
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 239 */     if (ser != null)
/*     */     {
/* 241 */       if (this._factoryConfig.hasSerializerModifiers()) {
/* 242 */         for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
/* 243 */           ser = mod.modifySerializer(config, beanDesc, ser);
/*     */         }
/*     */       }
/*     */     }
/* 247 */     return ser;
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
/*     */   public JsonSerializer<Object> findBeanSerializer(SerializerProvider prov, JavaType type, BeanDescription beanDesc)
/*     */     throws JsonMappingException
/*     */   {
/* 266 */     if (!isPotentialBeanType(type.getRawClass()))
/*     */     {
/*     */ 
/* 269 */       if (!type.isEnumType()) {
/* 270 */         return null;
/*     */       }
/*     */     }
/* 273 */     return constructBeanSerializer(prov, beanDesc);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonSerializer<?> findReferenceSerializer(SerializerProvider prov, ReferenceType refType, BeanDescription beanDesc, boolean staticTyping)
/*     */     throws JsonMappingException
/*     */   {
/* 283 */     JavaType contentType = refType.getContentType();
/* 284 */     TypeSerializer contentTypeSerializer = (TypeSerializer)contentType.getTypeHandler();
/* 285 */     SerializationConfig config = prov.getConfig();
/* 286 */     if (contentTypeSerializer == null) {
/* 287 */       contentTypeSerializer = createTypeSerializer(config, contentType);
/*     */     }
/* 289 */     JsonSerializer<Object> contentSerializer = (JsonSerializer)contentType.getValueHandler();
/* 290 */     for (Serializers serializers : customSerializers()) {
/* 291 */       JsonSerializer<?> ser = serializers.findReferenceSerializer(config, refType, beanDesc, contentTypeSerializer, contentSerializer);
/*     */       
/* 293 */       if (ser != null) {
/* 294 */         return ser;
/*     */       }
/*     */     }
/* 297 */     if (refType.isTypeOrSubTypeOf(AtomicReference.class)) {
/* 298 */       return new AtomicReferenceSerializer(refType, staticTyping, contentTypeSerializer, contentSerializer);
/*     */     }
/*     */     
/* 301 */     return null;
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
/*     */   public TypeSerializer findPropertyTypeSerializer(JavaType baseType, SerializationConfig config, AnnotatedMember accessor)
/*     */     throws JsonMappingException
/*     */   {
/* 318 */     AnnotationIntrospector ai = config.getAnnotationIntrospector();
/* 319 */     TypeResolverBuilder<?> b = ai.findPropertyTypeResolver(config, accessor, baseType);
/*     */     
/*     */     TypeSerializer typeSer;
/*     */     TypeSerializer typeSer;
/* 323 */     if (b == null) {
/* 324 */       typeSer = createTypeSerializer(config, baseType);
/*     */     } else {
/* 326 */       Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByClass(config, accessor, baseType);
/*     */       
/* 328 */       typeSer = b.buildTypeSerializer(config, baseType, subtypes);
/*     */     }
/* 330 */     return typeSer;
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
/*     */   public TypeSerializer findPropertyContentTypeSerializer(JavaType containerType, SerializationConfig config, AnnotatedMember accessor)
/*     */     throws JsonMappingException
/*     */   {
/* 347 */     JavaType contentType = containerType.getContentType();
/* 348 */     AnnotationIntrospector ai = config.getAnnotationIntrospector();
/* 349 */     TypeResolverBuilder<?> b = ai.findPropertyContentTypeResolver(config, accessor, containerType);
/*     */     
/*     */     TypeSerializer typeSer;
/*     */     TypeSerializer typeSer;
/* 353 */     if (b == null) {
/* 354 */       typeSer = createTypeSerializer(config, contentType);
/*     */     } else {
/* 356 */       Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByClass(config, accessor, contentType);
/*     */       
/* 358 */       typeSer = b.buildTypeSerializer(config, contentType, subtypes);
/*     */     }
/* 360 */     return typeSer;
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
/*     */   protected JsonSerializer<Object> constructBeanSerializer(SerializerProvider prov, BeanDescription beanDesc)
/*     */     throws JsonMappingException
/*     */   {
/* 381 */     if (beanDesc.getBeanClass() == Object.class) {
/* 382 */       return prov.getUnknownTypeSerializer(Object.class);
/*     */     }
/*     */     
/* 385 */     SerializationConfig config = prov.getConfig();
/* 386 */     BeanSerializerBuilder builder = constructBeanSerializerBuilder(beanDesc);
/* 387 */     builder.setConfig(config);
/*     */     
/*     */ 
/* 390 */     List<BeanPropertyWriter> props = findBeanProperties(prov, beanDesc, builder);
/* 391 */     if (props == null) {
/* 392 */       props = new ArrayList();
/*     */     } else {
/* 394 */       props = removeOverlappingTypeIds(prov, beanDesc, builder, props);
/*     */     }
/*     */     
/*     */ 
/* 398 */     prov.getAnnotationIntrospector().findAndAddVirtualProperties(config, beanDesc.getClassInfo(), props);
/*     */     
/*     */ 
/* 401 */     if (this._factoryConfig.hasSerializerModifiers()) {
/* 402 */       for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
/* 403 */         props = mod.changeProperties(config, beanDesc, props);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 408 */     props = filterBeanProperties(config, beanDesc, props);
/*     */     
/*     */ 
/* 411 */     if (this._factoryConfig.hasSerializerModifiers()) {
/* 412 */       for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
/* 413 */         props = mod.orderProperties(config, beanDesc, props);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 421 */     builder.setObjectIdWriter(constructObjectIdHandler(prov, beanDesc, props));
/*     */     
/* 423 */     builder.setProperties(props);
/* 424 */     builder.setFilterId(findFilterId(config, beanDesc));
/*     */     
/* 426 */     AnnotatedMember anyGetter = beanDesc.findAnyGetter();
/* 427 */     if (anyGetter != null) {
/* 428 */       if (config.canOverrideAccessModifiers()) {
/* 429 */         anyGetter.fixAccess(config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*     */       }
/* 431 */       JavaType type = anyGetter.getType();
/*     */       
/* 433 */       boolean staticTyping = config.isEnabled(MapperFeature.USE_STATIC_TYPING);
/* 434 */       JavaType valueType = type.getContentType();
/* 435 */       TypeSerializer typeSer = createTypeSerializer(config, valueType);
/*     */       
/*     */ 
/* 438 */       JsonSerializer<?> anySer = findSerializerFromAnnotation(prov, anyGetter);
/* 439 */       if (anySer == null)
/*     */       {
/* 441 */         anySer = MapSerializer.construct((Set)null, type, staticTyping, typeSer, null, null, null);
/*     */       }
/*     */       
/*     */ 
/* 445 */       PropertyName name = PropertyName.construct(anyGetter.getName());
/* 446 */       BeanProperty.Std anyProp = new BeanProperty.Std(name, valueType, null, beanDesc.getClassAnnotations(), anyGetter, PropertyMetadata.STD_OPTIONAL);
/*     */       
/* 448 */       builder.setAnyGetter(new AnyGetterWriter(anyProp, anyGetter, anySer));
/*     */     }
/*     */     
/* 451 */     processViews(config, builder);
/*     */     
/*     */ 
/* 454 */     if (this._factoryConfig.hasSerializerModifiers()) {
/* 455 */       for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
/* 456 */         builder = mod.updateBuilder(config, beanDesc, builder);
/*     */       }
/*     */     }
/*     */     
/* 460 */     JsonSerializer<Object> ser = builder.build();
/* 461 */     if (ser == null)
/*     */     {
/*     */ 
/*     */ 
/* 465 */       if (beanDesc.hasKnownClassAnnotations()) {
/* 466 */         return builder.createDummy();
/*     */       }
/*     */     }
/* 469 */     return ser;
/*     */   }
/*     */   
/*     */ 
/*     */   protected ObjectIdWriter constructObjectIdHandler(SerializerProvider prov, BeanDescription beanDesc, List<BeanPropertyWriter> props)
/*     */     throws JsonMappingException
/*     */   {
/* 476 */     ObjectIdInfo objectIdInfo = beanDesc.getObjectIdInfo();
/* 477 */     if (objectIdInfo == null) {
/* 478 */       return null;
/*     */     }
/*     */     
/* 481 */     Class<?> implClass = objectIdInfo.getGeneratorType();
/*     */     
/*     */ 
/* 484 */     if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
/* 485 */       String propName = objectIdInfo.getPropertyName().getSimpleName();
/* 486 */       BeanPropertyWriter idProp = null;
/*     */       
/* 488 */       int i = 0; for (int len = props.size();; i++) {
/* 489 */         if (i == len) {
/* 490 */           throw new IllegalArgumentException("Invalid Object Id definition for " + beanDesc.getBeanClass().getName() + ": can not find property with name '" + propName + "'");
/*     */         }
/*     */         
/* 493 */         BeanPropertyWriter prop = (BeanPropertyWriter)props.get(i);
/* 494 */         if (propName.equals(prop.getName())) {
/* 495 */           idProp = prop;
/*     */           
/*     */ 
/*     */ 
/* 499 */           if (i <= 0) break;
/* 500 */           props.remove(i);
/* 501 */           props.add(0, idProp); break;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 506 */       JavaType idType = idProp.getType();
/* 507 */       ObjectIdGenerator<?> gen = new PropertyBasedObjectIdGenerator(objectIdInfo, idProp);
/*     */       
/* 509 */       return ObjectIdWriter.construct(idType, (PropertyName)null, gen, objectIdInfo.getAlwaysAsId());
/*     */     }
/*     */     
/*     */ 
/* 513 */     JavaType type = prov.constructType(implClass);
/*     */     
/* 515 */     JavaType idType = prov.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
/* 516 */     ObjectIdGenerator<?> gen = prov.objectIdGeneratorInstance(beanDesc.getClassInfo(), objectIdInfo);
/* 517 */     return ObjectIdWriter.construct(idType, objectIdInfo.getPropertyName(), gen, objectIdInfo.getAlwaysAsId());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected BeanPropertyWriter constructFilteredBeanWriter(BeanPropertyWriter writer, Class<?>[] inViews)
/*     */   {
/* 529 */     return FilteredBeanPropertyWriter.constructViewBased(writer, inViews);
/*     */   }
/*     */   
/*     */ 
/*     */   protected PropertyBuilder constructPropertyBuilder(SerializationConfig config, BeanDescription beanDesc)
/*     */   {
/* 535 */     return new PropertyBuilder(config, beanDesc);
/*     */   }
/*     */   
/*     */   protected BeanSerializerBuilder constructBeanSerializerBuilder(BeanDescription beanDesc) {
/* 539 */     return new BeanSerializerBuilder(beanDesc);
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
/*     */   protected boolean isPotentialBeanType(Class<?> type)
/*     */   {
/* 558 */     return (ClassUtil.canBeABeanType(type) == null) && (!ClassUtil.isProxyType(type));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected List<BeanPropertyWriter> findBeanProperties(SerializerProvider prov, BeanDescription beanDesc, BeanSerializerBuilder builder)
/*     */     throws JsonMappingException
/*     */   {
/* 569 */     List<BeanPropertyDefinition> properties = beanDesc.findProperties();
/* 570 */     SerializationConfig config = prov.getConfig();
/*     */     
/*     */ 
/* 573 */     removeIgnorableTypes(config, beanDesc, properties);
/*     */     
/*     */ 
/* 576 */     if (config.isEnabled(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS)) {
/* 577 */       removeSetterlessGetters(config, beanDesc, properties);
/*     */     }
/*     */     
/*     */ 
/* 581 */     if (properties.isEmpty()) {
/* 582 */       return null;
/*     */     }
/*     */     
/* 585 */     boolean staticTyping = usesStaticTyping(config, beanDesc, null);
/* 586 */     PropertyBuilder pb = constructPropertyBuilder(config, beanDesc);
/*     */     
/* 588 */     ArrayList<BeanPropertyWriter> result = new ArrayList(properties.size());
/* 589 */     boolean fixAccess = config.canOverrideAccessModifiers();
/* 590 */     boolean forceAccess = (fixAccess) && (config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/* 591 */     for (BeanPropertyDefinition property : properties) {
/* 592 */       AnnotatedMember accessor = property.getAccessor();
/*     */       
/* 594 */       if (property.isTypeId()) {
/* 595 */         if (accessor != null) {
/* 596 */           if (fixAccess) {
/* 597 */             accessor.fixAccess(forceAccess);
/*     */           }
/* 599 */           builder.setTypeId(accessor);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 604 */         AnnotationIntrospector.ReferenceProperty refType = property.findReferenceType();
/* 605 */         if ((refType == null) || (!refType.isBackReference()))
/*     */         {
/*     */ 
/* 608 */           if ((accessor instanceof AnnotatedMethod)) {
/* 609 */             result.add(_constructWriter(prov, property, pb, staticTyping, (AnnotatedMethod)accessor));
/*     */           } else
/* 611 */             result.add(_constructWriter(prov, property, pb, staticTyping, (AnnotatedField)accessor)); }
/*     */       }
/*     */     }
/* 614 */     return result;
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
/*     */   protected List<BeanPropertyWriter> filterBeanProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> props)
/*     */   {
/* 634 */     JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(beanDesc.getBeanClass(), beanDesc.getClassInfo());
/*     */     
/* 636 */     if (ignorals != null) {
/* 637 */       Set<String> ignored = ignorals.findIgnoredForSerialization();
/* 638 */       if (!ignored.isEmpty()) {
/* 639 */         Iterator<BeanPropertyWriter> it = props.iterator();
/* 640 */         while (it.hasNext()) {
/* 641 */           if (ignored.contains(((BeanPropertyWriter)it.next()).getName())) {
/* 642 */             it.remove();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 647 */     return props;
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
/*     */   protected void processViews(SerializationConfig config, BeanSerializerBuilder builder)
/*     */   {
/* 662 */     List<BeanPropertyWriter> props = builder.getProperties();
/* 663 */     boolean includeByDefault = config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
/* 664 */     int propCount = props.size();
/* 665 */     int viewsFound = 0;
/* 666 */     BeanPropertyWriter[] filtered = new BeanPropertyWriter[propCount];
/*     */     
/* 668 */     for (int i = 0; i < propCount; i++) {
/* 669 */       BeanPropertyWriter bpw = (BeanPropertyWriter)props.get(i);
/* 670 */       Class<?>[] views = bpw.getViews();
/* 671 */       if (views == null) {
/* 672 */         if (includeByDefault) {
/* 673 */           filtered[i] = bpw;
/*     */         }
/*     */       } else {
/* 676 */         viewsFound++;
/* 677 */         filtered[i] = constructFilteredBeanWriter(bpw, views);
/*     */       }
/*     */     }
/*     */     
/* 681 */     if ((includeByDefault) && (viewsFound == 0)) {
/* 682 */       return;
/*     */     }
/* 684 */     builder.setFilteredProperties(filtered);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void removeIgnorableTypes(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyDefinition> properties)
/*     */   {
/* 695 */     AnnotationIntrospector intr = config.getAnnotationIntrospector();
/* 696 */     HashMap<Class<?>, Boolean> ignores = new HashMap();
/* 697 */     Iterator<BeanPropertyDefinition> it = properties.iterator();
/* 698 */     while (it.hasNext()) {
/* 699 */       BeanPropertyDefinition property = (BeanPropertyDefinition)it.next();
/* 700 */       AnnotatedMember accessor = property.getAccessor();
/* 701 */       if (accessor == null) {
/* 702 */         it.remove();
/*     */       }
/*     */       else {
/* 705 */         Class<?> type = accessor.getRawType();
/* 706 */         Boolean result = (Boolean)ignores.get(type);
/* 707 */         if (result == null)
/*     */         {
/* 709 */           ConfigOverride override = config.findConfigOverride(type);
/* 710 */           if (override != null) {
/* 711 */             result = override.getIsIgnoredType();
/*     */           }
/* 713 */           if (result == null) {
/* 714 */             BeanDescription desc = config.introspectClassAnnotations(type);
/* 715 */             AnnotatedClass ac = desc.getClassInfo();
/* 716 */             result = intr.isIgnorableType(ac);
/*     */             
/* 718 */             if (result == null) {
/* 719 */               result = Boolean.FALSE;
/*     */             }
/*     */           }
/* 722 */           ignores.put(type, result);
/*     */         }
/*     */         
/* 725 */         if (result.booleanValue()) {
/* 726 */           it.remove();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void removeSetterlessGetters(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyDefinition> properties)
/*     */   {
/* 737 */     Iterator<BeanPropertyDefinition> it = properties.iterator();
/* 738 */     while (it.hasNext()) {
/* 739 */       BeanPropertyDefinition property = (BeanPropertyDefinition)it.next();
/*     */       
/*     */ 
/* 742 */       if ((!property.couldDeserialize()) && (!property.isExplicitlyIncluded())) {
/* 743 */         it.remove();
/*     */       }
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
/*     */   protected List<BeanPropertyWriter> removeOverlappingTypeIds(SerializerProvider prov, BeanDescription beanDesc, BeanSerializerBuilder builder, List<BeanPropertyWriter> props)
/*     */   {
/* 758 */     int i = 0; BeanPropertyWriter bpw; PropertyName typePropName; for (int end = props.size(); i < end; i++) {
/* 759 */       bpw = (BeanPropertyWriter)props.get(i);
/* 760 */       TypeSerializer td = bpw.getTypeSerializer();
/* 761 */       if ((td != null) && (td.getTypeInclusion() == JsonTypeInfo.As.EXTERNAL_PROPERTY))
/*     */       {
/*     */ 
/* 764 */         String n = td.getPropertyName();
/* 765 */         typePropName = PropertyName.construct(n);
/*     */         
/* 767 */         for (BeanPropertyWriter w2 : props)
/* 768 */           if ((w2 != bpw) && (w2.wouldConflictWithName(typePropName))) {
/* 769 */             bpw.assignTypeSerializer(null);
/* 770 */             break;
/*     */           }
/*     */       }
/*     */     }
/* 774 */     return props;
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
/*     */   protected BeanPropertyWriter _constructWriter(SerializerProvider prov, BeanPropertyDefinition propDef, PropertyBuilder pb, boolean staticTyping, AnnotatedMember accessor)
/*     */     throws JsonMappingException
/*     */   {
/* 792 */     PropertyName name = propDef.getFullName();
/* 793 */     if (prov.canOverrideAccessModifiers()) {
/* 794 */       accessor.fixAccess(prov.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*     */     }
/* 796 */     JavaType type = accessor.getType();
/* 797 */     BeanProperty.Std property = new BeanProperty.Std(name, type, propDef.getWrapperName(), pb.getClassAnnotations(), accessor, propDef.getMetadata());
/*     */     
/*     */ 
/*     */ 
/* 801 */     JsonSerializer<?> annotatedSerializer = findSerializerFromAnnotation(prov, accessor);
/*     */     
/*     */ 
/*     */ 
/* 805 */     if ((annotatedSerializer instanceof ResolvableSerializer)) {
/* 806 */       ((ResolvableSerializer)annotatedSerializer).resolve(prov);
/*     */     }
/*     */     
/* 809 */     annotatedSerializer = prov.handlePrimaryContextualization(annotatedSerializer, property);
/*     */     
/* 811 */     TypeSerializer contentTypeSer = null;
/*     */     
/* 813 */     if ((type.isContainerType()) || (type.isReferenceType())) {
/* 814 */       contentTypeSer = findPropertyContentTypeSerializer(type, prov.getConfig(), accessor);
/*     */     }
/*     */     
/* 817 */     TypeSerializer typeSer = findPropertyTypeSerializer(type, prov.getConfig(), accessor);
/* 818 */     return pb.buildWriter(prov, propDef, type, annotatedSerializer, typeSer, contentTypeSer, accessor, staticTyping);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\BeanSerializerFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */