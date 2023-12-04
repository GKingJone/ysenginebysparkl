/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonIgnoreProperties.Value;
/*     */ import com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.AbstractTypeResolver;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector.ReferenceProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty.Std;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.MapperFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonPOJOBuilder.Value;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.ConfigOverride;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.DeserializerFactoryConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedField;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMethod;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.BeanPropertyDefinition;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.ObjectIdInfo;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.util.SimpleBeanPropertyDefinition;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class BeanDeserializerFactory extends BasicDeserializerFactory implements java.io.Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  38 */   private static final Class<?>[] INIT_CAUSE_PARAMS = { Throwable.class };
/*     */   
/*  40 */   private static final Class<?>[] NO_VIEWS = new Class[0];
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
/*  52 */   public static final BeanDeserializerFactory instance = new BeanDeserializerFactory(new DeserializerFactoryConfig());
/*     */   
/*     */   public BeanDeserializerFactory(DeserializerFactoryConfig config)
/*     */   {
/*  56 */     super(config);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DeserializerFactory withConfig(DeserializerFactoryConfig config)
/*     */   {
/*  67 */     if (this._factoryConfig == config) {
/*  68 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  76 */     if (getClass() != BeanDeserializerFactory.class) {
/*  77 */       throw new IllegalStateException("Subtype of BeanDeserializerFactory (" + getClass().getName() + ") has not properly overridden method 'withAdditionalDeserializers': can not instantiate subtype with " + "additional deserializer definitions");
/*     */     }
/*     */     
/*     */ 
/*  81 */     return new BeanDeserializerFactory(config);
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
/*     */   public JsonDeserializer<Object> createBeanDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc)
/*     */     throws JsonMappingException
/*     */   {
/* 100 */     DeserializationConfig config = ctxt.getConfig();
/*     */     
/* 102 */     JsonDeserializer<Object> custom = _findCustomBeanDeserializer(type, config, beanDesc);
/* 103 */     if (custom != null) {
/* 104 */       return custom;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 110 */     if (type.isThrowable()) {
/* 111 */       return buildThrowableDeserializer(ctxt, type, beanDesc);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 118 */     if ((type.isAbstract()) && (!type.isPrimitive()) && (!type.isEnumType()))
/*     */     {
/* 120 */       JavaType concreteType = materializeAbstractType(ctxt, type, beanDesc);
/* 121 */       if (concreteType != null)
/*     */       {
/*     */ 
/*     */ 
/* 125 */         beanDesc = config.introspect(concreteType);
/* 126 */         return buildBeanDeserializer(ctxt, concreteType, beanDesc);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 131 */     JsonDeserializer<Object> deser = findStdDeserializer(ctxt, type, beanDesc);
/* 132 */     if (deser != null) {
/* 133 */       return deser;
/*     */     }
/*     */     
/*     */ 
/* 137 */     if (!isPotentialBeanType(type.getRawClass())) {
/* 138 */       return null;
/*     */     }
/*     */     
/* 141 */     return buildBeanDeserializer(ctxt, type, beanDesc);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonDeserializer<Object> createBuilderBasedDeserializer(DeserializationContext ctxt, JavaType valueType, BeanDescription beanDesc, Class<?> builderClass)
/*     */     throws JsonMappingException
/*     */   {
/* 151 */     JavaType builderType = ctxt.constructType(builderClass);
/* 152 */     BeanDescription builderDesc = ctxt.getConfig().introspectForBuilder(builderType);
/* 153 */     return buildBuilderBasedDeserializer(ctxt, valueType, builderDesc);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected JsonDeserializer<?> findStdDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc)
/*     */     throws JsonMappingException
/*     */   {
/* 166 */     JsonDeserializer<?> deser = findDefaultDeserializer(ctxt, type, beanDesc);
/*     */     
/* 168 */     if ((deser != null) && 
/* 169 */       (this._factoryConfig.hasDeserializerModifiers())) {
/* 170 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 171 */         deser = mod.modifyDeserializer(ctxt.getConfig(), beanDesc, deser);
/*     */       }
/*     */     }
/*     */     
/* 175 */     return deser;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected JavaType materializeAbstractType(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc)
/*     */     throws JsonMappingException
/*     */   {
/* 183 */     for (AbstractTypeResolver r : this._factoryConfig.abstractTypeResolvers()) {
/* 184 */       JavaType concrete = r.resolveAbstractType(ctxt.getConfig(), beanDesc);
/* 185 */       if (concrete != null) {
/* 186 */         return concrete;
/*     */       }
/*     */     }
/* 189 */     return null;
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
/*     */   public JsonDeserializer<Object> buildBeanDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc)
/*     */     throws JsonMappingException
/*     */   {
/*     */     ValueInstantiator valueInstantiator;
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
/*     */     try
/*     */     {
/* 219 */       valueInstantiator = findValueInstantiator(ctxt, beanDesc);
/*     */     } catch (NoClassDefFoundError error) {
/* 221 */       return new com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.NoClassDefFoundDeserializer(error);
/*     */     }
/* 223 */     BeanDeserializerBuilder builder = constructBeanDeserializerBuilder(ctxt, beanDesc);
/* 224 */     builder.setValueInstantiator(valueInstantiator);
/*     */     
/* 226 */     addBeanProps(ctxt, beanDesc, builder);
/* 227 */     addObjectIdReader(ctxt, beanDesc, builder);
/*     */     
/*     */ 
/* 230 */     addReferenceProperties(ctxt, beanDesc, builder);
/* 231 */     addInjectables(ctxt, beanDesc, builder);
/*     */     
/* 233 */     DeserializationConfig config = ctxt.getConfig();
/*     */     
/* 235 */     if (this._factoryConfig.hasDeserializerModifiers()) {
/* 236 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 237 */         builder = mod.updateBuilder(config, beanDesc, builder);
/*     */       }
/*     */     }
/*     */     
/*     */     JsonDeserializer<?> deserializer;
/*     */     
/*     */     JsonDeserializer<?> deserializer;
/*     */     
/* 245 */     if ((type.isAbstract()) && (!valueInstantiator.canInstantiate())) {
/* 246 */       deserializer = builder.buildAbstract();
/*     */     } else {
/* 248 */       deserializer = builder.build();
/*     */     }
/*     */     
/*     */ 
/* 252 */     if (this._factoryConfig.hasDeserializerModifiers()) {
/* 253 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 254 */         deserializer = mod.modifyDeserializer(config, beanDesc, deserializer);
/*     */       }
/*     */     }
/* 257 */     return deserializer;
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
/*     */   protected JsonDeserializer<Object> buildBuilderBasedDeserializer(DeserializationContext ctxt, JavaType valueType, BeanDescription builderDesc)
/*     */     throws JsonMappingException
/*     */   {
/* 273 */     ValueInstantiator valueInstantiator = findValueInstantiator(ctxt, builderDesc);
/* 274 */     DeserializationConfig config = ctxt.getConfig();
/* 275 */     BeanDeserializerBuilder builder = constructBeanDeserializerBuilder(ctxt, builderDesc);
/* 276 */     builder.setValueInstantiator(valueInstantiator);
/*     */     
/* 278 */     addBeanProps(ctxt, builderDesc, builder);
/* 279 */     addObjectIdReader(ctxt, builderDesc, builder);
/*     */     
/*     */ 
/* 282 */     addReferenceProperties(ctxt, builderDesc, builder);
/* 283 */     addInjectables(ctxt, builderDesc, builder);
/*     */     
/* 285 */     JsonPOJOBuilder.Value builderConfig = builderDesc.findPOJOBuilderConfig();
/* 286 */     String buildMethodName = builderConfig == null ? "build" : builderConfig.buildMethodName;
/*     */     
/*     */ 
/*     */ 
/* 290 */     AnnotatedMethod buildMethod = builderDesc.findMethod(buildMethodName, null);
/* 291 */     if ((buildMethod != null) && 
/* 292 */       (config.canOverrideAccessModifiers())) {
/* 293 */       ClassUtil.checkAndFixAccess(buildMethod.getMember(), config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*     */     }
/*     */     
/* 296 */     builder.setPOJOBuilder(buildMethod, builderConfig);
/*     */     
/* 298 */     if (this._factoryConfig.hasDeserializerModifiers()) {
/* 299 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 300 */         builder = mod.updateBuilder(config, builderDesc, builder);
/*     */       }
/*     */     }
/* 303 */     JsonDeserializer<?> deserializer = builder.buildBuilderBased(valueType, buildMethodName);
/*     */     
/*     */ 
/*     */ 
/* 307 */     if (this._factoryConfig.hasDeserializerModifiers()) {
/* 308 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 309 */         deserializer = mod.modifyDeserializer(config, builderDesc, deserializer);
/*     */       }
/*     */     }
/* 312 */     return deserializer;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void addObjectIdReader(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder)
/*     */     throws JsonMappingException
/*     */   {
/* 319 */     ObjectIdInfo objectIdInfo = beanDesc.getObjectIdInfo();
/* 320 */     if (objectIdInfo == null) {
/* 321 */       return;
/*     */     }
/* 323 */     Class<?> implClass = objectIdInfo.getGeneratorType();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 328 */     com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdResolver resolver = ctxt.objectIdResolverInstance(beanDesc.getClassInfo(), objectIdInfo);
/*     */     ObjectIdGenerator<?> gen;
/*     */     JavaType idType;
/* 331 */     SettableBeanProperty idProp; ObjectIdGenerator<?> gen; if (implClass == com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerators.PropertyGenerator.class) {
/* 332 */       PropertyName propName = objectIdInfo.getPropertyName();
/* 333 */       SettableBeanProperty idProp = builder.findProperty(propName);
/* 334 */       if (idProp == null) {
/* 335 */         throw new IllegalArgumentException("Invalid Object Id definition for " + beanDesc.getBeanClass().getName() + ": can not find property with name '" + propName + "'");
/*     */       }
/*     */       
/* 338 */       JavaType idType = idProp.getType();
/* 339 */       gen = new com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator(objectIdInfo.getScope());
/*     */     } else {
/* 341 */       JavaType type = ctxt.constructType(implClass);
/* 342 */       idType = ctxt.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
/* 343 */       idProp = null;
/* 344 */       gen = ctxt.objectIdGeneratorInstance(beanDesc.getClassInfo(), objectIdInfo);
/*     */     }
/*     */     
/* 347 */     JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(idType);
/* 348 */     builder.setObjectIdReader(com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.ObjectIdReader.construct(idType, objectIdInfo.getPropertyName(), gen, deser, idProp, resolver));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public JsonDeserializer<Object> buildThrowableDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc)
/*     */     throws JsonMappingException
/*     */   {
/* 357 */     DeserializationConfig config = ctxt.getConfig();
/*     */     
/* 359 */     BeanDeserializerBuilder builder = constructBeanDeserializerBuilder(ctxt, beanDesc);
/* 360 */     builder.setValueInstantiator(findValueInstantiator(ctxt, beanDesc));
/*     */     
/* 362 */     addBeanProps(ctxt, beanDesc, builder);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 369 */     AnnotatedMethod am = beanDesc.findMethod("initCause", INIT_CAUSE_PARAMS);
/* 370 */     if (am != null) {
/* 371 */       SimpleBeanPropertyDefinition propDef = SimpleBeanPropertyDefinition.construct(ctxt.getConfig(), am, new PropertyName("cause"));
/*     */       
/* 373 */       SettableBeanProperty prop = constructSettableProperty(ctxt, beanDesc, propDef, am.getParameterType(0));
/*     */       
/* 375 */       if (prop != null)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 380 */         builder.addOrReplaceProperty(prop, true);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 385 */     builder.addIgnorable("localizedMessage");
/*     */     
/* 387 */     builder.addIgnorable("suppressed");
/*     */     
/*     */ 
/*     */ 
/* 391 */     builder.addIgnorable("message");
/*     */     
/*     */ 
/* 394 */     if (this._factoryConfig.hasDeserializerModifiers()) {
/* 395 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 396 */         builder = mod.updateBuilder(config, beanDesc, builder);
/*     */       }
/*     */     }
/* 399 */     JsonDeserializer<?> deserializer = builder.build();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 404 */     if ((deserializer instanceof BeanDeserializer)) {
/* 405 */       deserializer = new com.facebook.presto.jdbc.internal.jackson.databind.deser.std.ThrowableDeserializer((BeanDeserializer)deserializer);
/*     */     }
/*     */     
/*     */ 
/* 409 */     if (this._factoryConfig.hasDeserializerModifiers()) {
/* 410 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 411 */         deserializer = mod.modifyDeserializer(config, beanDesc, deserializer);
/*     */       }
/*     */     }
/* 414 */     return deserializer;
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
/*     */   protected BeanDeserializerBuilder constructBeanDeserializerBuilder(DeserializationContext ctxt, BeanDescription beanDesc)
/*     */   {
/* 431 */     return new BeanDeserializerBuilder(beanDesc, ctxt.getConfig());
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
/*     */   protected void addBeanProps(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder)
/*     */     throws JsonMappingException
/*     */   {
/* 445 */     SettableBeanProperty[] creatorProps = builder.getValueInstantiator().getFromObjectArguments(ctxt.getConfig());
/*     */     
/* 447 */     boolean isConcrete = !beanDesc.getType().isAbstract();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 453 */     JsonIgnoreProperties.Value ignorals = ctxt.getConfig().getDefaultPropertyIgnorals(beanDesc.getBeanClass(), beanDesc.getClassInfo());
/*     */     
/*     */ 
/*     */     Set<String> ignored;
/*     */     
/* 458 */     if (ignorals != null) {
/* 459 */       boolean ignoreAny = ignorals.getIgnoreUnknown();
/* 460 */       builder.setIgnoreUnknownProperties(ignoreAny);
/*     */       
/* 462 */       Set<String> ignored = ignorals.getIgnored();
/* 463 */       for (String propName : ignored) {
/* 464 */         builder.addIgnorable(propName);
/*     */       }
/*     */     } else {
/* 467 */       ignored = java.util.Collections.emptySet();
/*     */     }
/*     */     
/*     */ 
/* 471 */     AnnotatedMethod anySetterMethod = beanDesc.findAnySetter();
/* 472 */     AnnotatedMember anySetterField = null;
/* 473 */     if (anySetterMethod != null) {
/* 474 */       builder.setAnySetter(constructAnySetter(ctxt, beanDesc, anySetterMethod));
/*     */     }
/*     */     else {
/* 477 */       anySetterField = beanDesc.findAnySetterField();
/* 478 */       if (anySetterField != null) {
/* 479 */         builder.setAnySetter(constructAnySetter(ctxt, beanDesc, anySetterField));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 484 */     if ((anySetterMethod == null) && (anySetterField == null)) {
/* 485 */       Collection<String> ignored2 = beanDesc.getIgnoredPropertyNames();
/* 486 */       if (ignored2 != null) {
/* 487 */         for (String propName : ignored2)
/*     */         {
/*     */ 
/* 490 */           builder.addIgnorable(propName);
/*     */         }
/*     */       }
/*     */     }
/* 494 */     boolean useGettersAsSetters = (ctxt.isEnabled(MapperFeature.USE_GETTERS_AS_SETTERS)) && (ctxt.isEnabled(MapperFeature.AUTO_DETECT_GETTERS));
/*     */     
/*     */ 
/*     */ 
/* 498 */     List<BeanPropertyDefinition> propDefs = filterBeanProps(ctxt, beanDesc, builder, beanDesc.findProperties(), ignored);
/*     */     
/*     */ 
/*     */ 
/* 502 */     if (this._factoryConfig.hasDeserializerModifiers()) {
/* 503 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 504 */         propDefs = mod.updateProperties(ctxt.getConfig(), beanDesc, propDefs);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 509 */     for (BeanPropertyDefinition propDef : propDefs) {
/* 510 */       SettableBeanProperty prop = null;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 515 */       if (propDef.hasSetter()) {
/* 516 */         JavaType propertyType = propDef.getSetter().getParameterType(0);
/* 517 */         prop = constructSettableProperty(ctxt, beanDesc, propDef, propertyType);
/* 518 */       } else if (propDef.hasField()) {
/* 519 */         JavaType propertyType = propDef.getField().getType();
/* 520 */         prop = constructSettableProperty(ctxt, beanDesc, propDef, propertyType);
/* 521 */       } else if ((useGettersAsSetters) && (propDef.hasGetter()))
/*     */       {
/*     */ 
/*     */ 
/* 525 */         AnnotatedMethod getter = propDef.getGetter();
/*     */         
/* 527 */         Class<?> rawPropertyType = getter.getRawType();
/* 528 */         if ((Collection.class.isAssignableFrom(rawPropertyType)) || (Map.class.isAssignableFrom(rawPropertyType)))
/*     */         {
/* 530 */           prop = constructSetterlessProperty(ctxt, beanDesc, propDef);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 535 */       if ((isConcrete) && (propDef.hasConstructorParameter()))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 541 */         String name = propDef.getName();
/* 542 */         CreatorProperty cprop = null;
/* 543 */         if (creatorProps != null) {
/* 544 */           for (SettableBeanProperty cp : creatorProps) {
/* 545 */             if ((name.equals(cp.getName())) && ((cp instanceof CreatorProperty))) {
/* 546 */               cprop = (CreatorProperty)cp;
/* 547 */               break;
/*     */             }
/*     */           }
/*     */         }
/* 551 */         if (cprop == null) {
/* 552 */           ctxt.reportMappingException("Could not find creator property with name '%s' (in class %s)", new Object[] { name, beanDesc.getBeanClass().getName() });
/*     */         }
/*     */         else
/*     */         {
/* 556 */           if (prop != null) {
/* 557 */             cprop.setFallbackSetter(prop);
/*     */           }
/* 559 */           prop = cprop;
/* 560 */           builder.addCreatorProperty(cprop);
/*     */         }
/*     */         
/*     */       }
/* 564 */       else if (prop != null) {
/* 565 */         Class<?>[] views = propDef.findViews();
/* 566 */         if (views == null)
/*     */         {
/* 568 */           if (!ctxt.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION)) {
/* 569 */             views = NO_VIEWS;
/*     */           }
/*     */         }
/*     */         
/* 573 */         prop.setViews(views);
/* 574 */         builder.addProperty(prop);
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
/*     */ 
/*     */   protected List<BeanPropertyDefinition> filterBeanProps(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder, List<BeanPropertyDefinition> propDefsIn, Set<String> ignored)
/*     */     throws JsonMappingException
/*     */   {
/* 591 */     ArrayList<BeanPropertyDefinition> result = new ArrayList(Math.max(4, propDefsIn.size()));
/*     */     
/* 593 */     HashMap<Class<?>, Boolean> ignoredTypes = new HashMap();
/*     */     
/* 595 */     for (BeanPropertyDefinition property : propDefsIn) {
/* 596 */       String name = property.getName();
/* 597 */       if (!ignored.contains(name))
/*     */       {
/*     */ 
/* 600 */         if (!property.hasConstructorParameter()) {
/* 601 */           Class<?> rawPropertyType = null;
/* 602 */           if (property.hasSetter()) {
/* 603 */             rawPropertyType = property.getSetter().getRawParameterType(0);
/* 604 */           } else if (property.hasField()) {
/* 605 */             rawPropertyType = property.getField().getRawType();
/*     */           }
/*     */           
/*     */ 
/* 609 */           if ((rawPropertyType != null) && (isIgnorableType(ctxt.getConfig(), beanDesc, rawPropertyType, ignoredTypes)))
/*     */           {
/*     */ 
/* 612 */             builder.addIgnorable(name);
/* 613 */             continue;
/*     */           }
/*     */         }
/* 616 */         result.add(property);
/*     */       } }
/* 618 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void addReferenceProperties(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder)
/*     */     throws JsonMappingException
/*     */   {
/* 630 */     Map<String, AnnotatedMember> refs = beanDesc.findBackReferenceProperties();
/* 631 */     if (refs != null) {
/* 632 */       for (Entry<String, AnnotatedMember> en : refs.entrySet()) {
/* 633 */         String name = (String)en.getKey();
/* 634 */         AnnotatedMember m = (AnnotatedMember)en.getValue();
/*     */         JavaType type;
/* 636 */         JavaType type; if ((m instanceof AnnotatedMethod)) {
/* 637 */           type = ((AnnotatedMethod)m).getParameterType(0);
/*     */         } else {
/* 639 */           type = m.getType();
/*     */         }
/* 641 */         SimpleBeanPropertyDefinition propDef = SimpleBeanPropertyDefinition.construct(ctxt.getConfig(), m);
/*     */         
/* 643 */         builder.addBackReferenceProperty(name, constructSettableProperty(ctxt, beanDesc, propDef, type));
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
/*     */   protected void addInjectables(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder)
/*     */     throws JsonMappingException
/*     */   {
/* 657 */     Map<Object, AnnotatedMember> raw = beanDesc.findInjectables();
/* 658 */     boolean fixAccess; boolean forceAccess; if (raw != null) {
/* 659 */       fixAccess = ctxt.canOverrideAccessModifiers();
/* 660 */       forceAccess = (fixAccess) && (ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/* 661 */       for (Entry<Object, AnnotatedMember> entry : raw.entrySet()) {
/* 662 */         AnnotatedMember m = (AnnotatedMember)entry.getValue();
/* 663 */         if (fixAccess) {
/* 664 */           m.fixAccess(forceAccess);
/*     */         }
/* 666 */         builder.addInjectable(PropertyName.construct(m.getName()), m.getType(), beanDesc.getClassAnnotations(), m, entry.getKey());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected SettableAnyProperty constructAnySetter(DeserializationContext ctxt, BeanDescription beanDesc, AnnotatedMember mutator)
/*     */     throws JsonMappingException
/*     */   {
/* 686 */     if (ctxt.canOverrideAccessModifiers()) {
/* 687 */       mutator.fixAccess(ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*     */     }
/*     */     
/*     */ 
/* 691 */     JavaType type = null;
/* 692 */     if ((mutator instanceof AnnotatedMethod))
/*     */     {
/* 694 */       type = ((AnnotatedMethod)mutator).getParameterType(1);
/* 695 */     } else if ((mutator instanceof AnnotatedField))
/*     */     {
/* 697 */       type = ((AnnotatedField)mutator).getType().getContentType();
/*     */     }
/*     */     
/*     */ 
/* 701 */     type = resolveMemberAndTypeAnnotations(ctxt, mutator, type);
/* 702 */     BeanProperty.Std prop = new BeanProperty.Std(PropertyName.construct(mutator.getName()), type, null, beanDesc.getClassAnnotations(), mutator, com.facebook.presto.jdbc.internal.jackson.databind.PropertyMetadata.STD_OPTIONAL);
/*     */     
/*     */ 
/*     */ 
/* 706 */     JsonDeserializer<Object> deser = findDeserializerFromAnnotation(ctxt, mutator);
/* 707 */     if (deser == null) {
/* 708 */       deser = (JsonDeserializer)type.getValueHandler();
/*     */     }
/* 710 */     if (deser != null)
/*     */     {
/* 712 */       deser = ctxt.handlePrimaryContextualization(deser, prop, type);
/*     */     }
/* 714 */     TypeDeserializer typeDeser = (TypeDeserializer)type.getTypeHandler();
/* 715 */     return new SettableAnyProperty(prop, mutator, type, deser, typeDeser);
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
/*     */   protected SettableBeanProperty constructSettableProperty(DeserializationContext ctxt, BeanDescription beanDesc, BeanPropertyDefinition propDef, JavaType propType0)
/*     */     throws JsonMappingException
/*     */   {
/* 731 */     AnnotatedMember mutator = propDef.getNonConstructorMutator();
/* 732 */     if (ctxt.canOverrideAccessModifiers()) {
/* 733 */       mutator.fixAccess(ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*     */     }
/* 735 */     JavaType type = resolveMemberAndTypeAnnotations(ctxt, mutator, propType0);
/*     */     
/* 737 */     TypeDeserializer typeDeser = (TypeDeserializer)type.getTypeHandler();
/*     */     SettableBeanProperty prop;
/* 739 */     SettableBeanProperty prop; if ((mutator instanceof AnnotatedMethod)) {
/* 740 */       prop = new com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.MethodProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), (AnnotatedMethod)mutator);
/*     */     }
/*     */     else {
/* 743 */       prop = new com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.FieldProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), (AnnotatedField)mutator);
/*     */     }
/*     */     
/* 746 */     JsonDeserializer<?> deser = findDeserializerFromAnnotation(ctxt, mutator);
/* 747 */     if (deser == null) {
/* 748 */       deser = (JsonDeserializer)type.getValueHandler();
/*     */     }
/* 750 */     if (deser != null) {
/* 751 */       deser = ctxt.handlePrimaryContextualization(deser, prop, type);
/* 752 */       prop = prop.withValueDeserializer(deser);
/*     */     }
/*     */     
/* 755 */     AnnotationIntrospector.ReferenceProperty ref = propDef.findReferenceType();
/* 756 */     if ((ref != null) && (ref.isManagedReference())) {
/* 757 */       prop.setManagedReferenceName(ref.getName());
/*     */     }
/* 759 */     ObjectIdInfo objectIdInfo = propDef.findObjectIdInfo();
/* 760 */     if (objectIdInfo != null) {
/* 761 */       prop.setObjectIdInfo(objectIdInfo);
/*     */     }
/* 763 */     return prop;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected SettableBeanProperty constructSetterlessProperty(DeserializationContext ctxt, BeanDescription beanDesc, BeanPropertyDefinition propDef)
/*     */     throws JsonMappingException
/*     */   {
/* 774 */     AnnotatedMethod getter = propDef.getGetter();
/*     */     
/* 776 */     if (ctxt.canOverrideAccessModifiers()) {
/* 777 */       getter.fixAccess(ctxt.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*     */     }
/* 779 */     JavaType type = resolveMemberAndTypeAnnotations(ctxt, getter, getter.getType());
/* 780 */     TypeDeserializer typeDeser = (TypeDeserializer)type.getTypeHandler();
/* 781 */     SettableBeanProperty prop = new com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.SetterlessProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), getter);
/*     */     
/* 783 */     JsonDeserializer<?> deser = findDeserializerFromAnnotation(ctxt, getter);
/* 784 */     if (deser == null) {
/* 785 */       deser = (JsonDeserializer)type.getValueHandler();
/*     */     }
/* 787 */     if (deser != null) {
/* 788 */       deser = ctxt.handlePrimaryContextualization(deser, prop, type);
/* 789 */       prop = prop.withValueDeserializer(deser);
/*     */     }
/* 791 */     return prop;
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
/* 810 */     String typeStr = ClassUtil.canBeABeanType(type);
/* 811 */     if (typeStr != null) {
/* 812 */       throw new IllegalArgumentException("Can not deserialize Class " + type.getName() + " (of type " + typeStr + ") as a Bean");
/*     */     }
/* 814 */     if (ClassUtil.isProxyType(type)) {
/* 815 */       throw new IllegalArgumentException("Can not deserialize Proxy class " + type.getName() + " as a Bean");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 820 */     typeStr = ClassUtil.isLocalType(type, true);
/* 821 */     if (typeStr != null) {
/* 822 */       throw new IllegalArgumentException("Can not deserialize Class " + type.getName() + " (of type " + typeStr + ") as a Bean");
/*     */     }
/* 824 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean isIgnorableType(DeserializationConfig config, BeanDescription beanDesc, Class<?> type, Map<Class<?>, Boolean> ignoredTypes)
/*     */   {
/* 834 */     Boolean status = (Boolean)ignoredTypes.get(type);
/* 835 */     if (status != null) {
/* 836 */       return status.booleanValue();
/*     */     }
/*     */     
/* 839 */     ConfigOverride override = config.findConfigOverride(type);
/* 840 */     if (override != null) {
/* 841 */       status = override.getIsIgnoredType();
/*     */     }
/* 843 */     if (status == null) {
/* 844 */       BeanDescription desc = config.introspectClassAnnotations(type);
/* 845 */       status = config.getAnnotationIntrospector().isIgnorableType(desc.getClassInfo());
/*     */       
/* 847 */       if (status == null) {
/* 848 */         status = Boolean.FALSE;
/*     */       }
/*     */     }
/* 851 */     ignoredTypes.put(type, status);
/* 852 */     return status.booleanValue();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\BeanDeserializerFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */