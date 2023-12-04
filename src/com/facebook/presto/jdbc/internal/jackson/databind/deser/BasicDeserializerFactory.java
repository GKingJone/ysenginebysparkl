/*      */ package com.facebook.presto.jdbc.internal.jackson.databind.deser;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.DeserializerFactoryConfig;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.impl.CreatorCollector;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.Annotated;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedConstructor;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMethod;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedParameter;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedWithParams;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.BeanPropertyDefinition;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.VisibilityChecker;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeResolverBuilder;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ArrayType;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.type.CollectionLikeType;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.type.CollectionType;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapLikeType;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapType;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.type.ReferenceType;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ 
/*      */ public abstract class BasicDeserializerFactory extends DeserializerFactory implements java.io.Serializable
/*      */ {
/*   39 */   private static final Class<?> CLASS_OBJECT = Object.class;
/*   40 */   private static final Class<?> CLASS_STRING = String.class;
/*   41 */   private static final Class<?> CLASS_CHAR_BUFFER = CharSequence.class;
/*   42 */   private static final Class<?> CLASS_ITERABLE = Iterable.class;
/*   43 */   private static final Class<?> CLASS_MAP_ENTRY = Map.Entry.class;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   49 */   protected static final PropertyName UNWRAPPED_CREATOR_PARAM_NAME = new PropertyName("@JsonUnwrapped");
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   56 */   static final HashMap<String, Class<? extends Map>> _mapFallbacks = new HashMap();
/*      */   static final HashMap<String, Class<? extends Collection>> _collectionFallbacks;
/*      */   
/*   59 */   static { _mapFallbacks.put(Map.class.getName(), java.util.LinkedHashMap.class);
/*   60 */     _mapFallbacks.put(java.util.concurrent.ConcurrentMap.class.getName(), java.util.concurrent.ConcurrentHashMap.class);
/*   61 */     _mapFallbacks.put(java.util.SortedMap.class.getName(), java.util.TreeMap.class);
/*      */     
/*   63 */     _mapFallbacks.put(java.util.NavigableMap.class.getName(), java.util.TreeMap.class);
/*   64 */     _mapFallbacks.put(java.util.concurrent.ConcurrentNavigableMap.class.getName(), java.util.concurrent.ConcurrentSkipListMap.class);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   73 */     _collectionFallbacks = new HashMap();
/*      */     
/*      */ 
/*   76 */     _collectionFallbacks.put(Collection.class.getName(), java.util.ArrayList.class);
/*   77 */     _collectionFallbacks.put(List.class.getName(), java.util.ArrayList.class);
/*   78 */     _collectionFallbacks.put(java.util.Set.class.getName(), java.util.HashSet.class);
/*   79 */     _collectionFallbacks.put(java.util.SortedSet.class.getName(), java.util.TreeSet.class);
/*   80 */     _collectionFallbacks.put(java.util.Queue.class.getName(), java.util.LinkedList.class);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   87 */     _collectionFallbacks.put("java.util.Deque", java.util.LinkedList.class);
/*   88 */     _collectionFallbacks.put("java.util.NavigableSet", java.util.TreeSet.class);
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
/*      */   protected BasicDeserializerFactory(DeserializerFactoryConfig config)
/*      */   {
/*  110 */     this._factoryConfig = config;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DeserializerFactoryConfig getFactoryConfig()
/*      */   {
/*  121 */     return this._factoryConfig;
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
/*      */   public final DeserializerFactory withAdditionalDeserializers(Deserializers additional)
/*      */   {
/*  138 */     return withConfig(this._factoryConfig.withAdditionalDeserializers(additional));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final DeserializerFactory withAdditionalKeyDeserializers(KeyDeserializers additional)
/*      */   {
/*  147 */     return withConfig(this._factoryConfig.withAdditionalKeyDeserializers(additional));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final DeserializerFactory withDeserializerModifier(BeanDeserializerModifier modifier)
/*      */   {
/*  156 */     return withConfig(this._factoryConfig.withDeserializerModifier(modifier));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final DeserializerFactory withAbstractTypeResolver(com.facebook.presto.jdbc.internal.jackson.databind.AbstractTypeResolver resolver)
/*      */   {
/*  165 */     return withConfig(this._factoryConfig.withAbstractTypeResolver(resolver));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public final DeserializerFactory withValueInstantiators(ValueInstantiators instantiators)
/*      */   {
/*  174 */     return withConfig(this._factoryConfig.withValueInstantiators(instantiators));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JavaType mapAbstractType(DeserializationConfig config, JavaType type)
/*      */     throws JsonMappingException
/*      */   {
/*      */     for (;;)
/*      */     {
/*  188 */       JavaType next = _mapAbstractType2(config, type);
/*  189 */       if (next == null) {
/*  190 */         return type;
/*      */       }
/*      */       
/*      */ 
/*  194 */       Class<?> prevCls = type.getRawClass();
/*  195 */       Class<?> nextCls = next.getRawClass();
/*  196 */       if ((prevCls == nextCls) || (!prevCls.isAssignableFrom(nextCls))) {
/*  197 */         throw new IllegalArgumentException("Invalid abstract type resolution from " + type + " to " + next + ": latter is not a subtype of former");
/*      */       }
/*  199 */       type = next;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private JavaType _mapAbstractType2(DeserializationConfig config, JavaType type)
/*      */     throws JsonMappingException
/*      */   {
/*  210 */     Class<?> currClass = type.getRawClass();
/*  211 */     if (this._factoryConfig.hasAbstractTypeResolvers()) {
/*  212 */       for (com.facebook.presto.jdbc.internal.jackson.databind.AbstractTypeResolver resolver : this._factoryConfig.abstractTypeResolvers()) {
/*  213 */         JavaType concrete = resolver.findTypeMapping(config, type);
/*  214 */         if ((concrete != null) && (concrete.getRawClass() != currClass)) {
/*  215 */           return concrete;
/*      */         }
/*      */       }
/*      */     }
/*  219 */     return null;
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
/*      */   public ValueInstantiator findValueInstantiator(DeserializationContext ctxt, BeanDescription beanDesc)
/*      */     throws JsonMappingException
/*      */   {
/*  238 */     DeserializationConfig config = ctxt.getConfig();
/*      */     
/*  240 */     ValueInstantiator instantiator = null;
/*      */     
/*  242 */     com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedClass ac = beanDesc.getClassInfo();
/*  243 */     Object instDef = ctxt.getAnnotationIntrospector().findValueInstantiator(ac);
/*  244 */     if (instDef != null) {
/*  245 */       instantiator = _valueInstantiatorInstance(config, ac, instDef);
/*      */     }
/*  247 */     if (instantiator == null)
/*      */     {
/*      */ 
/*      */ 
/*  251 */       instantiator = _findStdValueInstantiator(config, beanDesc);
/*  252 */       if (instantiator == null) {
/*  253 */         instantiator = _constructDefaultValueInstantiator(ctxt, beanDesc);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  258 */     if (this._factoryConfig.hasValueInstantiators()) {
/*  259 */       for (ValueInstantiators insts : this._factoryConfig.valueInstantiators()) {
/*  260 */         instantiator = insts.findValueInstantiator(config, beanDesc, instantiator);
/*      */         
/*  262 */         if (instantiator == null) {
/*  263 */           ctxt.reportMappingException("Broken registered ValueInstantiators (of type %s): returned null ValueInstantiator", new Object[] { insts.getClass().getName() });
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  271 */     if (instantiator.getIncompleteParameter() != null) {
/*  272 */       AnnotatedParameter nonAnnotatedParam = instantiator.getIncompleteParameter();
/*  273 */       AnnotatedWithParams ctor = nonAnnotatedParam.getOwner();
/*  274 */       throw new IllegalArgumentException("Argument #" + nonAnnotatedParam.getIndex() + " of constructor " + ctor + " has no property name annotation; must have name when multiple-parameter constructor annotated as Creator");
/*      */     }
/*      */     
/*  277 */     return instantiator;
/*      */   }
/*      */   
/*      */ 
/*      */   private ValueInstantiator _findStdValueInstantiator(DeserializationConfig config, BeanDescription beanDesc)
/*      */     throws JsonMappingException
/*      */   {
/*  284 */     if (beanDesc.getBeanClass() == com.facebook.presto.jdbc.internal.jackson.core.JsonLocation.class) {
/*  285 */       return new com.facebook.presto.jdbc.internal.jackson.databind.deser.std.JsonLocationInstantiator();
/*      */     }
/*  287 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected ValueInstantiator _constructDefaultValueInstantiator(DeserializationContext ctxt, BeanDescription beanDesc)
/*      */     throws JsonMappingException
/*      */   {
/*  298 */     CreatorCollector creators = new CreatorCollector(beanDesc, ctxt.getConfig());
/*  299 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*      */     
/*      */ 
/*  302 */     DeserializationConfig config = ctxt.getConfig();
/*  303 */     VisibilityChecker<?> vchecker = config.getDefaultVisibilityChecker();
/*  304 */     vchecker = intr.findAutoDetectVisibility(beanDesc.getClassInfo(), vchecker);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  314 */     Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorDefs = _findCreatorsFromProperties(ctxt, beanDesc);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  320 */     _addDeserializerFactoryMethods(ctxt, beanDesc, vchecker, intr, creators, creatorDefs);
/*      */     
/*  322 */     if (beanDesc.getType().isConcrete()) {
/*  323 */       _addDeserializerConstructors(ctxt, beanDesc, vchecker, intr, creators, creatorDefs);
/*      */     }
/*  325 */     return creators.constructValueInstantiator(config);
/*      */   }
/*      */   
/*      */   protected Map<AnnotatedWithParams, BeanPropertyDefinition[]> _findCreatorsFromProperties(DeserializationContext ctxt, BeanDescription beanDesc)
/*      */     throws JsonMappingException
/*      */   {
/*  331 */     Map<AnnotatedWithParams, BeanPropertyDefinition[]> result = java.util.Collections.emptyMap();
/*  332 */     for (BeanPropertyDefinition propDef : beanDesc.findProperties()) {
/*  333 */       java.util.Iterator<AnnotatedParameter> it = propDef.getConstructorParameters();
/*  334 */       while (it.hasNext()) {
/*  335 */         AnnotatedParameter param = (AnnotatedParameter)it.next();
/*  336 */         AnnotatedWithParams owner = param.getOwner();
/*  337 */         BeanPropertyDefinition[] defs = (BeanPropertyDefinition[])result.get(owner);
/*  338 */         int index = param.getIndex();
/*      */         
/*  340 */         if (defs == null) {
/*  341 */           if (result.isEmpty()) {
/*  342 */             result = new java.util.LinkedHashMap();
/*      */           }
/*  344 */           defs = new BeanPropertyDefinition[owner.getParameterCount()];
/*  345 */           result.put(owner, defs);
/*      */         }
/*  347 */         else if (defs[index] != null) {
/*  348 */           throw new IllegalStateException("Conflict: parameter #" + index + " of " + owner + " bound to more than one property; " + defs[index] + " vs " + propDef);
/*      */         }
/*      */         
/*      */ 
/*  352 */         defs[index] = propDef;
/*      */       }
/*      */     }
/*  355 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   public ValueInstantiator _valueInstantiatorInstance(DeserializationConfig config, Annotated annotated, Object instDef)
/*      */     throws JsonMappingException
/*      */   {
/*  362 */     if (instDef == null) {
/*  363 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  368 */     if ((instDef instanceof ValueInstantiator)) {
/*  369 */       return (ValueInstantiator)instDef;
/*      */     }
/*  371 */     if (!(instDef instanceof Class)) {
/*  372 */       throw new IllegalStateException("AnnotationIntrospector returned key deserializer definition of type " + instDef.getClass().getName() + "; expected type KeyDeserializer or Class<KeyDeserializer> instead");
/*      */     }
/*      */     
/*      */ 
/*  376 */     Class<?> instClass = (Class)instDef;
/*  377 */     if (ClassUtil.isBogusClass(instClass)) {
/*  378 */       return null;
/*      */     }
/*  380 */     if (!ValueInstantiator.class.isAssignableFrom(instClass)) {
/*  381 */       throw new IllegalStateException("AnnotationIntrospector returned Class " + instClass.getName() + "; expected Class<ValueInstantiator>");
/*      */     }
/*      */     
/*  384 */     com.facebook.presto.jdbc.internal.jackson.databind.cfg.HandlerInstantiator hi = config.getHandlerInstantiator();
/*  385 */     if (hi != null) {
/*  386 */       ValueInstantiator inst = hi.valueInstantiatorInstance(config, annotated, instClass);
/*  387 */       if (inst != null) {
/*  388 */         return inst;
/*      */       }
/*      */     }
/*  391 */     return (ValueInstantiator)ClassUtil.createInstance(instClass, config.canOverrideAccessModifiers());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _addDeserializerConstructors(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams)
/*      */     throws JsonMappingException
/*      */   {
/*  404 */     AnnotatedConstructor defaultCtor = beanDesc.findDefaultConstructor();
/*  405 */     if ((defaultCtor != null) && (
/*  406 */       (!creators.hasDefaultCreator()) || (intr.hasCreatorAnnotation(defaultCtor)))) {
/*  407 */       creators.setDefaultCreator(defaultCtor);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  412 */     List<AnnotatedConstructor> implicitCtors = null;
/*  413 */     for (AnnotatedConstructor ctor : beanDesc.getConstructors()) {
/*  414 */       boolean isCreator = intr.hasCreatorAnnotation(ctor);
/*  415 */       BeanPropertyDefinition[] propDefs = (BeanPropertyDefinition[])creatorParams.get(ctor);
/*  416 */       int argCount = ctor.getParameterCount();
/*      */       
/*      */ 
/*  419 */       if (argCount == 1) {
/*  420 */         BeanPropertyDefinition argDef = propDefs == null ? null : propDefs[0];
/*  421 */         boolean useProps = _checkIfCreatorPropertyBased(intr, ctor, argDef);
/*      */         
/*  423 */         if (useProps) {
/*  424 */           SettableBeanProperty[] properties = new SettableBeanProperty[1];
/*  425 */           PropertyName name = argDef == null ? null : argDef.getFullName();
/*  426 */           AnnotatedParameter arg = ctor.getParameter(0);
/*  427 */           properties[0] = constructCreatorProperty(ctxt, beanDesc, name, 0, arg, intr.findInjectableValueId(arg));
/*      */           
/*  429 */           creators.addPropertyCreator(ctor, isCreator, properties);
/*      */         } else {
/*  431 */           _handleSingleArgumentConstructor(ctxt, beanDesc, vchecker, intr, creators, ctor, isCreator, vchecker.isCreatorVisible(ctor));
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  436 */           if (argDef != null) {
/*  437 */             ((com.facebook.presto.jdbc.internal.jackson.databind.introspect.POJOPropertyBuilder)argDef).removeConstructors();
/*      */ 
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*      */ 
/*  448 */         AnnotatedParameter nonAnnotatedParam = null;
/*  449 */         SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
/*  450 */         int explicitNameCount = 0;
/*  451 */         int implicitWithCreatorCount = 0;
/*  452 */         int injectCount = 0;
/*      */         
/*  454 */         for (int i = 0; i < argCount; i++) {
/*  455 */           AnnotatedParameter param = ctor.getParameter(i);
/*  456 */           BeanPropertyDefinition propDef = propDefs == null ? null : propDefs[i];
/*  457 */           Object injectId = intr.findInjectableValueId(param);
/*  458 */           PropertyName name = propDef == null ? null : propDef.getFullName();
/*      */           
/*  460 */           if ((propDef != null) && (propDef.isExplicitlyNamed())) {
/*  461 */             explicitNameCount++;
/*  462 */             properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/*      */ 
/*      */           }
/*  465 */           else if (injectId != null) {
/*  466 */             injectCount++;
/*  467 */             properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/*      */           }
/*      */           else {
/*  470 */             com.facebook.presto.jdbc.internal.jackson.databind.util.NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param);
/*  471 */             if (unwrapper != null) {
/*  472 */               properties[i] = constructCreatorProperty(ctxt, beanDesc, UNWRAPPED_CREATOR_PARAM_NAME, i, param, null);
/*  473 */               explicitNameCount++;
/*      */ 
/*      */ 
/*      */             }
/*  477 */             else if ((isCreator) && (name != null) && (!name.isEmpty())) {
/*  478 */               implicitWithCreatorCount++;
/*  479 */               properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/*      */ 
/*      */             }
/*  482 */             else if (nonAnnotatedParam == null) {
/*  483 */               nonAnnotatedParam = param;
/*      */             }
/*      */           }
/*      */         }
/*  487 */         int namedCount = explicitNameCount + implicitWithCreatorCount;
/*      */         
/*  489 */         if ((isCreator) || (explicitNameCount > 0) || (injectCount > 0))
/*      */         {
/*  491 */           if (namedCount + injectCount == argCount) {
/*  492 */             creators.addPropertyCreator(ctor, isCreator, properties);
/*  493 */             continue;
/*      */           }
/*  495 */           if ((explicitNameCount == 0) && (injectCount + 1 == argCount))
/*      */           {
/*  497 */             creators.addDelegatingCreator(ctor, isCreator, properties);
/*  498 */             continue;
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*  503 */           PropertyName impl = _findImplicitParamName(nonAnnotatedParam, intr);
/*  504 */           if ((impl == null) || (impl.isEmpty()))
/*      */           {
/*  506 */             int ix = nonAnnotatedParam.getIndex();
/*  507 */             if ((ix == 0) && (ClassUtil.isNonStaticInnerClass(ctor.getDeclaringClass()))) {
/*  508 */               throw new IllegalArgumentException("Non-static inner classes like " + ctor.getDeclaringClass().getName() + " can not use @JsonCreator for constructors");
/*      */             }
/*      */             
/*  511 */             throw new IllegalArgumentException("Argument #" + ix + " of constructor " + ctor + " has no property name annotation; must have name when multiple-parameter constructor annotated as Creator");
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*  516 */         if (!creators.hasDefaultCreator()) {
/*  517 */           if (implicitCtors == null) {
/*  518 */             implicitCtors = new java.util.LinkedList();
/*      */           }
/*  520 */           implicitCtors.add(ctor);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  525 */     if ((implicitCtors != null) && (!creators.hasDelegatingCreator()) && (!creators.hasPropertyBasedCreator()))
/*      */     {
/*  527 */       _checkImplicitlyNamedConstructors(ctxt, beanDesc, vchecker, intr, creators, implicitCtors);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _checkImplicitlyNamedConstructors(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, List<AnnotatedConstructor> implicitCtors)
/*      */     throws JsonMappingException
/*      */   {
/*  537 */     AnnotatedConstructor found = null;
/*  538 */     SettableBeanProperty[] foundProps = null;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  545 */     for (AnnotatedConstructor ctor : implicitCtors)
/*  546 */       if (vchecker.isCreatorVisible(ctor))
/*      */       {
/*      */ 
/*      */ 
/*  550 */         int argCount = ctor.getParameterCount();
/*  551 */         SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
/*  552 */         for (int i = 0;; i++) { if (i >= argCount) break label137;
/*  553 */           AnnotatedParameter param = ctor.getParameter(i);
/*  554 */           PropertyName name = _findParamName(param, intr);
/*      */           
/*      */ 
/*  557 */           if ((name == null) || (name.isEmpty())) {
/*      */             break;
/*      */           }
/*  560 */           properties[i] = constructCreatorProperty(ctxt, beanDesc, name, param.getIndex(), param, null);
/*      */         }
/*      */         
/*  563 */         if (found != null) {
/*  564 */           found = null;
/*  565 */           break;
/*      */         }
/*  567 */         found = ctor;
/*  568 */         foundProps = properties;
/*      */       }
/*      */     label137:
/*  571 */     if (found != null) {
/*  572 */       creators.addPropertyCreator(found, false, foundProps);
/*  573 */       com.facebook.presto.jdbc.internal.jackson.databind.introspect.BasicBeanDescription bbd = (com.facebook.presto.jdbc.internal.jackson.databind.introspect.BasicBeanDescription)beanDesc;
/*      */       
/*  575 */       for (SettableBeanProperty prop : foundProps) {
/*  576 */         PropertyName pn = prop.getFullName();
/*  577 */         if (!bbd.hasProperty(pn)) {
/*  578 */           BeanPropertyDefinition newDef = com.facebook.presto.jdbc.internal.jackson.databind.util.SimpleBeanPropertyDefinition.construct(ctxt.getConfig(), prop.getMember(), pn);
/*      */           
/*  580 */           bbd.addProperty(newDef);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected boolean _checkIfCreatorPropertyBased(AnnotationIntrospector intr, AnnotatedWithParams creator, BeanPropertyDefinition propDef)
/*      */   {
/*  589 */     com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator.Mode mode = intr.findCreatorBinding(creator);
/*      */     
/*  591 */     if (mode == com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator.Mode.PROPERTIES) {
/*  592 */       return true;
/*      */     }
/*  594 */     if (mode == com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator.Mode.DELEGATING) {
/*  595 */       return false;
/*      */     }
/*      */     
/*  598 */     if (((propDef != null) && (propDef.isExplicitlyNamed())) || (intr.findInjectableValueId(creator.getParameter(0)) != null))
/*      */     {
/*  600 */       return true;
/*      */     }
/*  602 */     if (propDef != null)
/*      */     {
/*      */ 
/*  605 */       String implName = propDef.getName();
/*  606 */       if ((implName != null) && (!implName.isEmpty()) && 
/*  607 */         (propDef.couldSerialize())) {
/*  608 */         return true;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  613 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean _handleSingleArgumentConstructor(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, AnnotatedConstructor ctor, boolean isCreator, boolean isVisible)
/*      */     throws JsonMappingException
/*      */   {
/*  623 */     Class<?> type = ctor.getRawParameterType(0);
/*  624 */     if ((type == String.class) || (type == CharSequence.class)) {
/*  625 */       if ((isCreator) || (isVisible)) {
/*  626 */         creators.addStringCreator(ctor, isCreator);
/*      */       }
/*  628 */       return true;
/*      */     }
/*  630 */     if ((type == Integer.TYPE) || (type == Integer.class)) {
/*  631 */       if ((isCreator) || (isVisible)) {
/*  632 */         creators.addIntCreator(ctor, isCreator);
/*      */       }
/*  634 */       return true;
/*      */     }
/*  636 */     if ((type == Long.TYPE) || (type == Long.class)) {
/*  637 */       if ((isCreator) || (isVisible)) {
/*  638 */         creators.addLongCreator(ctor, isCreator);
/*      */       }
/*  640 */       return true;
/*      */     }
/*  642 */     if ((type == Double.TYPE) || (type == Double.class)) {
/*  643 */       if ((isCreator) || (isVisible)) {
/*  644 */         creators.addDoubleCreator(ctor, isCreator);
/*      */       }
/*  646 */       return true;
/*      */     }
/*  648 */     if ((type == Boolean.TYPE) || (type == Boolean.class)) {
/*  649 */       if ((isCreator) || (isVisible)) {
/*  650 */         creators.addBooleanCreator(ctor, isCreator);
/*      */       }
/*  652 */       return true;
/*      */     }
/*      */     
/*  655 */     if (isCreator) {
/*  656 */       creators.addDelegatingCreator(ctor, isCreator, null);
/*  657 */       return true;
/*      */     }
/*  659 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _addDeserializerFactoryMethods(DeserializationContext ctxt, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, Map<AnnotatedWithParams, BeanPropertyDefinition[]> creatorParams)
/*      */     throws JsonMappingException
/*      */   {
/*  668 */     DeserializationConfig config = ctxt.getConfig();
/*  669 */     for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
/*  670 */       boolean isCreator = intr.hasCreatorAnnotation(factory);
/*  671 */       int argCount = factory.getParameterCount();
/*      */       
/*  673 */       if (argCount == 0) {
/*  674 */         if (isCreator) {
/*  675 */           creators.setDefaultCreator(factory);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  680 */         BeanPropertyDefinition[] propDefs = (BeanPropertyDefinition[])creatorParams.get(factory);
/*      */         
/*  682 */         if (argCount == 1) {
/*  683 */           BeanPropertyDefinition argDef = propDefs == null ? null : propDefs[0];
/*  684 */           boolean useProps = _checkIfCreatorPropertyBased(intr, factory, argDef);
/*  685 */           if (!useProps) {
/*  686 */             _handleSingleArgumentFactory(config, beanDesc, vchecker, intr, creators, factory, isCreator);
/*      */             
/*      */ 
/*  689 */             continue;
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  694 */           if (!isCreator) {
/*      */             continue;
/*      */           }
/*      */         }
/*      */         
/*  699 */         AnnotatedParameter nonAnnotatedParam = null;
/*  700 */         SettableBeanProperty[] properties = new SettableBeanProperty[argCount];
/*  701 */         int implicitNameCount = 0;
/*  702 */         int explicitNameCount = 0;
/*  703 */         int injectCount = 0;
/*      */         
/*  705 */         for (int i = 0; i < argCount; i++) {
/*  706 */           AnnotatedParameter param = factory.getParameter(i);
/*  707 */           BeanPropertyDefinition propDef = propDefs == null ? null : propDefs[i];
/*  708 */           Object injectId = intr.findInjectableValueId(param);
/*  709 */           PropertyName name = propDef == null ? null : propDef.getFullName();
/*      */           
/*  711 */           if ((propDef != null) && (propDef.isExplicitlyNamed())) {
/*  712 */             explicitNameCount++;
/*  713 */             properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/*      */ 
/*      */           }
/*  716 */           else if (injectId != null) {
/*  717 */             injectCount++;
/*  718 */             properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
/*      */           }
/*      */           else {
/*  721 */             com.facebook.presto.jdbc.internal.jackson.databind.util.NameTransformer unwrapper = intr.findUnwrappingNameTransformer(param);
/*  722 */             if (unwrapper != null) {
/*  723 */               properties[i] = constructCreatorProperty(ctxt, beanDesc, UNWRAPPED_CREATOR_PARAM_NAME, i, param, null);
/*  724 */               implicitNameCount++;
/*      */ 
/*      */ 
/*      */             }
/*  728 */             else if ((isCreator) && 
/*  729 */               (name != null) && (!name.isEmpty())) {
/*  730 */               implicitNameCount++;
/*  731 */               properties[i] = constructCreatorProperty(ctxt, beanDesc, name, i, param, injectId);
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
/*      */             }
/*  747 */             else if (nonAnnotatedParam == null) {
/*  748 */               nonAnnotatedParam = param;
/*      */             }
/*      */           } }
/*  751 */         int namedCount = explicitNameCount + implicitNameCount;
/*      */         
/*      */ 
/*  754 */         if ((isCreator) || (explicitNameCount > 0) || (injectCount > 0))
/*      */         {
/*  756 */           if (namedCount + injectCount == argCount) {
/*  757 */             creators.addPropertyCreator(factory, isCreator, properties);
/*  758 */           } else if ((explicitNameCount == 0) && (injectCount + 1 == argCount))
/*      */           {
/*  760 */             creators.addDelegatingCreator(factory, isCreator, properties);
/*      */           } else {
/*  762 */             throw new IllegalArgumentException("Argument #" + nonAnnotatedParam.getIndex() + " of factory method " + factory + " has no property name annotation; must have name when multiple-parameter constructor annotated as Creator");
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean _handleSingleArgumentFactory(DeserializationConfig config, BeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, AnnotatedMethod factory, boolean isCreator)
/*      */     throws JsonMappingException
/*      */   {
/*  775 */     Class<?> type = factory.getRawParameterType(0);
/*      */     
/*  777 */     if ((type == String.class) || (type == CharSequence.class)) {
/*  778 */       if ((isCreator) || (vchecker.isCreatorVisible(factory))) {
/*  779 */         creators.addStringCreator(factory, isCreator);
/*      */       }
/*  781 */       return true;
/*      */     }
/*  783 */     if ((type == Integer.TYPE) || (type == Integer.class)) {
/*  784 */       if ((isCreator) || (vchecker.isCreatorVisible(factory))) {
/*  785 */         creators.addIntCreator(factory, isCreator);
/*      */       }
/*  787 */       return true;
/*      */     }
/*  789 */     if ((type == Long.TYPE) || (type == Long.class)) {
/*  790 */       if ((isCreator) || (vchecker.isCreatorVisible(factory))) {
/*  791 */         creators.addLongCreator(factory, isCreator);
/*      */       }
/*  793 */       return true;
/*      */     }
/*  795 */     if ((type == Double.TYPE) || (type == Double.class)) {
/*  796 */       if ((isCreator) || (vchecker.isCreatorVisible(factory))) {
/*  797 */         creators.addDoubleCreator(factory, isCreator);
/*      */       }
/*  799 */       return true;
/*      */     }
/*  801 */     if ((type == Boolean.TYPE) || (type == Boolean.class)) {
/*  802 */       if ((isCreator) || (vchecker.isCreatorVisible(factory))) {
/*  803 */         creators.addBooleanCreator(factory, isCreator);
/*      */       }
/*  805 */       return true;
/*      */     }
/*  807 */     if (isCreator) {
/*  808 */       creators.addDelegatingCreator(factory, isCreator, null);
/*  809 */       return true;
/*      */     }
/*  811 */     return false;
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
/*      */   protected SettableBeanProperty constructCreatorProperty(DeserializationContext ctxt, BeanDescription beanDesc, PropertyName name, int index, AnnotatedParameter param, Object injectableValueId)
/*      */     throws JsonMappingException
/*      */   {
/*  825 */     DeserializationConfig config = ctxt.getConfig();
/*  826 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/*      */     com.facebook.presto.jdbc.internal.jackson.databind.PropertyMetadata metadata;
/*      */     com.facebook.presto.jdbc.internal.jackson.databind.PropertyMetadata metadata;
/*  829 */     if (intr == null) {
/*  830 */       metadata = com.facebook.presto.jdbc.internal.jackson.databind.PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
/*      */     } else {
/*  832 */       Boolean b = intr.hasRequiredMarker(param);
/*  833 */       boolean req = (b != null) && (b.booleanValue());
/*  834 */       String desc = intr.findPropertyDescription(param);
/*  835 */       Integer idx = intr.findPropertyIndex(param);
/*  836 */       String def = intr.findPropertyDefaultValue(param);
/*  837 */       metadata = com.facebook.presto.jdbc.internal.jackson.databind.PropertyMetadata.construct(req, desc, idx, def);
/*      */     }
/*      */     
/*  840 */     JavaType type = resolveMemberAndTypeAnnotations(ctxt, param, param.getType());
/*  841 */     com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty.Std property = new com.facebook.presto.jdbc.internal.jackson.databind.BeanProperty.Std(name, type, intr.findWrapperName(param), beanDesc.getClassAnnotations(), param, metadata);
/*      */     
/*      */ 
/*      */ 
/*  845 */     TypeDeserializer typeDeser = (TypeDeserializer)type.getTypeHandler();
/*      */     
/*  847 */     if (typeDeser == null) {
/*  848 */       typeDeser = findTypeDeserializer(config, type);
/*      */     }
/*      */     
/*      */ 
/*  852 */     SettableBeanProperty prop = new CreatorProperty(name, type, property.getWrapperName(), typeDeser, beanDesc.getClassAnnotations(), param, index, injectableValueId, metadata);
/*      */     
/*      */ 
/*  855 */     JsonDeserializer<?> deser = findDeserializerFromAnnotation(ctxt, param);
/*  856 */     if (deser == null) {
/*  857 */       deser = (JsonDeserializer)type.getValueHandler();
/*      */     }
/*  859 */     if (deser != null)
/*      */     {
/*  861 */       deser = ctxt.handlePrimaryContextualization(deser, prop, type);
/*  862 */       prop = prop.withValueDeserializer(deser);
/*      */     }
/*  864 */     return prop;
/*      */   }
/*      */   
/*      */   protected PropertyName _findParamName(AnnotatedParameter param, AnnotationIntrospector intr)
/*      */   {
/*  869 */     if ((param != null) && (intr != null)) {
/*  870 */       PropertyName name = intr.findNameForDeserialization(param);
/*  871 */       if (name != null) {
/*  872 */         return name;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  877 */       String str = intr.findImplicitPropertyName(param);
/*  878 */       if ((str != null) && (!str.isEmpty())) {
/*  879 */         return PropertyName.construct(str);
/*      */       }
/*      */     }
/*  882 */     return null;
/*      */   }
/*      */   
/*      */   protected PropertyName _findImplicitParamName(AnnotatedParameter param, AnnotationIntrospector intr)
/*      */   {
/*  887 */     String str = intr.findImplicitPropertyName(param);
/*  888 */     if ((str != null) && (!str.isEmpty())) {
/*  889 */       return PropertyName.construct(str);
/*      */     }
/*  891 */     return null;
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   protected PropertyName _findExplicitParamName(AnnotatedParameter param, AnnotationIntrospector intr)
/*      */   {
/*  897 */     if ((param != null) && (intr != null)) {
/*  898 */       return intr.findNameForDeserialization(param);
/*      */     }
/*  900 */     return null;
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   protected boolean _hasExplicitParamName(AnnotatedParameter param, AnnotationIntrospector intr)
/*      */   {
/*  906 */     if ((param != null) && (intr != null)) {
/*  907 */       PropertyName n = intr.findNameForDeserialization(param);
/*  908 */       return (n != null) && (n.hasSimpleName());
/*      */     }
/*  910 */     return false;
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
/*      */   public JsonDeserializer<?> createArrayDeserializer(DeserializationContext ctxt, ArrayType type, BeanDescription beanDesc)
/*      */     throws JsonMappingException
/*      */   {
/*  924 */     DeserializationConfig config = ctxt.getConfig();
/*  925 */     JavaType elemType = type.getContentType();
/*      */     
/*      */ 
/*  928 */     JsonDeserializer<Object> contentDeser = (JsonDeserializer)elemType.getValueHandler();
/*      */     
/*  930 */     TypeDeserializer elemTypeDeser = (TypeDeserializer)elemType.getTypeHandler();
/*      */     
/*  932 */     if (elemTypeDeser == null) {
/*  933 */       elemTypeDeser = findTypeDeserializer(config, elemType);
/*      */     }
/*      */     
/*  936 */     JsonDeserializer<?> deser = _findCustomArrayDeserializer(type, config, beanDesc, elemTypeDeser, contentDeser);
/*      */     
/*  938 */     if (deser == null) {
/*  939 */       if (contentDeser == null) {
/*  940 */         Class<?> raw = elemType.getRawClass();
/*  941 */         if (elemType.isPrimitive())
/*  942 */           return com.facebook.presto.jdbc.internal.jackson.databind.deser.std.PrimitiveArrayDeserializers.forType(raw);
/*  943 */         if (raw == String.class) {
/*  944 */           return com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StringArrayDeserializer.instance;
/*      */         }
/*      */       }
/*  947 */       deser = new com.facebook.presto.jdbc.internal.jackson.databind.deser.std.ObjectArrayDeserializer(type, contentDeser, elemTypeDeser);
/*      */     }
/*      */     
/*  950 */     if (this._factoryConfig.hasDeserializerModifiers()) {
/*  951 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/*  952 */         deser = mod.modifyArrayDeserializer(config, type, beanDesc, deser);
/*      */       }
/*      */     }
/*  955 */     return deser;
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
/*      */   public JsonDeserializer<?> createCollectionDeserializer(DeserializationContext ctxt, CollectionType type, BeanDescription beanDesc)
/*      */     throws JsonMappingException
/*      */   {
/*  969 */     JavaType contentType = type.getContentType();
/*      */     
/*  971 */     JsonDeserializer<Object> contentDeser = (JsonDeserializer)contentType.getValueHandler();
/*  972 */     DeserializationConfig config = ctxt.getConfig();
/*      */     
/*      */ 
/*  975 */     TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
/*      */     
/*  977 */     if (contentTypeDeser == null) {
/*  978 */       contentTypeDeser = findTypeDeserializer(config, contentType);
/*      */     }
/*      */     
/*  981 */     JsonDeserializer<?> deser = _findCustomCollectionDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
/*      */     
/*  983 */     if (deser == null) {
/*  984 */       Class<?> collectionClass = type.getRawClass();
/*  985 */       if (contentDeser == null)
/*      */       {
/*  987 */         if (java.util.EnumSet.class.isAssignableFrom(collectionClass)) {
/*  988 */           deser = new com.facebook.presto.jdbc.internal.jackson.databind.deser.std.EnumSetDeserializer(contentType, null);
/*      */         }
/*      */       }
/*      */     }
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
/* 1002 */     if (deser == null) {
/* 1003 */       if ((type.isInterface()) || (type.isAbstract())) {
/* 1004 */         CollectionType implType = _mapAbstractCollectionType(type, config);
/* 1005 */         if (implType == null)
/*      */         {
/* 1007 */           if (type.getTypeHandler() == null) {
/* 1008 */             throw new IllegalArgumentException("Can not find a deserializer for non-concrete Collection type " + type);
/*      */           }
/* 1010 */           deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
/*      */         } else {
/* 1012 */           type = implType;
/*      */           
/* 1014 */           beanDesc = config.introspectForCreation(type);
/*      */         }
/*      */       }
/* 1017 */       if (deser == null) {
/* 1018 */         ValueInstantiator inst = findValueInstantiator(ctxt, beanDesc);
/* 1019 */         if (!inst.canCreateUsingDefault())
/*      */         {
/* 1021 */           if (type.getRawClass() == java.util.concurrent.ArrayBlockingQueue.class) {
/* 1022 */             return new com.facebook.presto.jdbc.internal.jackson.databind.deser.std.ArrayBlockingQueueDeserializer(type, contentDeser, contentTypeDeser, inst);
/*      */           }
/*      */         }
/*      */         
/* 1026 */         if (contentType.getRawClass() == String.class)
/*      */         {
/* 1028 */           deser = new com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StringCollectionDeserializer(type, contentDeser, inst);
/*      */         } else {
/* 1030 */           deser = new com.facebook.presto.jdbc.internal.jackson.databind.deser.std.CollectionDeserializer(type, contentDeser, contentTypeDeser, inst);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1035 */     if (this._factoryConfig.hasDeserializerModifiers()) {
/* 1036 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 1037 */         deser = mod.modifyCollectionDeserializer(config, type, beanDesc, deser);
/*      */       }
/*      */     }
/* 1040 */     return deser;
/*      */   }
/*      */   
/*      */   protected CollectionType _mapAbstractCollectionType(JavaType type, DeserializationConfig config)
/*      */   {
/* 1045 */     Class<?> collectionClass = type.getRawClass();
/* 1046 */     collectionClass = (Class)_collectionFallbacks.get(collectionClass.getName());
/* 1047 */     if (collectionClass == null) {
/* 1048 */       return null;
/*      */     }
/* 1050 */     return (CollectionType)config.constructSpecializedType(type, collectionClass);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonDeserializer<?> createCollectionLikeDeserializer(DeserializationContext ctxt, CollectionLikeType type, BeanDescription beanDesc)
/*      */     throws JsonMappingException
/*      */   {
/* 1059 */     JavaType contentType = type.getContentType();
/*      */     
/* 1061 */     JsonDeserializer<Object> contentDeser = (JsonDeserializer)contentType.getValueHandler();
/* 1062 */     DeserializationConfig config = ctxt.getConfig();
/*      */     
/*      */ 
/* 1065 */     TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
/*      */     
/* 1067 */     if (contentTypeDeser == null) {
/* 1068 */       contentTypeDeser = findTypeDeserializer(config, contentType);
/*      */     }
/* 1070 */     JsonDeserializer<?> deser = _findCustomCollectionLikeDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
/*      */     
/* 1072 */     if (deser != null)
/*      */     {
/* 1074 */       if (this._factoryConfig.hasDeserializerModifiers()) {
/* 1075 */         for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 1076 */           deser = mod.modifyCollectionLikeDeserializer(config, type, beanDesc, deser);
/*      */         }
/*      */       }
/*      */     }
/* 1080 */     return deser;
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
/*      */   public JsonDeserializer<?> createMapDeserializer(DeserializationContext ctxt, MapType type, BeanDescription beanDesc)
/*      */     throws JsonMappingException
/*      */   {
/* 1094 */     DeserializationConfig config = ctxt.getConfig();
/* 1095 */     JavaType keyType = type.getKeyType();
/* 1096 */     JavaType contentType = type.getContentType();
/*      */     
/*      */ 
/*      */ 
/* 1100 */     JsonDeserializer<Object> contentDeser = (JsonDeserializer)contentType.getValueHandler();
/*      */     
/*      */ 
/* 1103 */     KeyDeserializer keyDes = (KeyDeserializer)keyType.getValueHandler();
/*      */     
/* 1105 */     TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
/*      */     
/* 1107 */     if (contentTypeDeser == null) {
/* 1108 */       contentTypeDeser = findTypeDeserializer(config, contentType);
/*      */     }
/*      */     
/*      */ 
/* 1112 */     JsonDeserializer<?> deser = _findCustomMapDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
/*      */     
/*      */ 
/* 1115 */     if (deser == null)
/*      */     {
/* 1117 */       Class<?> mapClass = type.getRawClass();
/* 1118 */       if (java.util.EnumMap.class.isAssignableFrom(mapClass)) {
/* 1119 */         Class<?> kt = keyType.getRawClass();
/* 1120 */         if ((kt == null) || (!kt.isEnum())) {
/* 1121 */           throw new IllegalArgumentException("Can not construct EnumMap; generic (key) type not available");
/*      */         }
/* 1123 */         deser = new com.facebook.presto.jdbc.internal.jackson.databind.deser.std.EnumMapDeserializer(type, null, contentDeser, contentTypeDeser);
/*      */       }
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
/* 1137 */       if (deser == null) {
/* 1138 */         if ((type.isInterface()) || (type.isAbstract()))
/*      */         {
/* 1140 */           Class<? extends Map> fallback = (Class)_mapFallbacks.get(mapClass.getName());
/* 1141 */           if (fallback != null) {
/* 1142 */             mapClass = fallback;
/* 1143 */             type = (MapType)config.constructSpecializedType(type, mapClass);
/*      */             
/* 1145 */             beanDesc = config.introspectForCreation(type);
/*      */           }
/*      */           else {
/* 1148 */             if (type.getTypeHandler() == null) {
/* 1149 */               throw new IllegalArgumentException("Can not find a deserializer for non-concrete Map type " + type);
/*      */             }
/* 1151 */             deser = AbstractDeserializer.constructForNonPOJO(beanDesc);
/*      */           }
/*      */         }
/* 1154 */         if (deser == null) {
/* 1155 */           ValueInstantiator inst = findValueInstantiator(ctxt, beanDesc);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/* 1160 */           com.facebook.presto.jdbc.internal.jackson.databind.deser.std.MapDeserializer md = new com.facebook.presto.jdbc.internal.jackson.databind.deser.std.MapDeserializer(type, inst, keyDes, contentDeser, contentTypeDeser);
/* 1161 */           com.facebook.presto.jdbc.internal.jackson.annotation.JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Map.class, beanDesc.getClassInfo());
/*      */           
/* 1163 */           java.util.Set<String> ignored = ignorals == null ? null : ignorals.findIgnoredForDeserialization();
/*      */           
/* 1165 */           md.setIgnorableProperties(ignored);
/* 1166 */           deser = md;
/*      */         }
/*      */       }
/*      */     }
/* 1170 */     if (this._factoryConfig.hasDeserializerModifiers()) {
/* 1171 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 1172 */         deser = mod.modifyMapDeserializer(config, type, beanDesc, deser);
/*      */       }
/*      */     }
/* 1175 */     return deser;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonDeserializer<?> createMapLikeDeserializer(DeserializationContext ctxt, MapLikeType type, BeanDescription beanDesc)
/*      */     throws JsonMappingException
/*      */   {
/* 1184 */     JavaType keyType = type.getKeyType();
/* 1185 */     JavaType contentType = type.getContentType();
/* 1186 */     DeserializationConfig config = ctxt.getConfig();
/*      */     
/*      */ 
/*      */ 
/* 1190 */     JsonDeserializer<Object> contentDeser = (JsonDeserializer)contentType.getValueHandler();
/*      */     
/*      */ 
/* 1193 */     KeyDeserializer keyDes = (KeyDeserializer)keyType.getValueHandler();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1200 */     TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
/*      */     
/* 1202 */     if (contentTypeDeser == null) {
/* 1203 */       contentTypeDeser = findTypeDeserializer(config, contentType);
/*      */     }
/* 1205 */     JsonDeserializer<?> deser = _findCustomMapLikeDeserializer(type, config, beanDesc, keyDes, contentTypeDeser, contentDeser);
/*      */     
/* 1207 */     if (deser != null)
/*      */     {
/* 1209 */       if (this._factoryConfig.hasDeserializerModifiers()) {
/* 1210 */         for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 1211 */           deser = mod.modifyMapLikeDeserializer(config, type, beanDesc, deser);
/*      */         }
/*      */       }
/*      */     }
/* 1215 */     return deser;
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
/*      */   public JsonDeserializer<?> createEnumDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc)
/*      */     throws JsonMappingException
/*      */   {
/* 1232 */     DeserializationConfig config = ctxt.getConfig();
/* 1233 */     Class<?> enumClass = type.getRawClass();
/*      */     
/* 1235 */     JsonDeserializer<?> deser = _findCustomEnumDeserializer(enumClass, config, beanDesc);
/*      */     
/* 1237 */     if (deser == null) {
/* 1238 */       ValueInstantiator valueInstantiator = _constructDefaultValueInstantiator(ctxt, beanDesc);
/* 1239 */       SettableBeanProperty[] creatorProps = valueInstantiator == null ? null : valueInstantiator.getFromObjectArguments(ctxt.getConfig());
/*      */       
/*      */ 
/* 1242 */       for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
/* 1243 */         if (ctxt.getAnnotationIntrospector().hasCreatorAnnotation(factory)) {
/* 1244 */           int argCount = factory.getParameterCount();
/* 1245 */           if (argCount == 1) {
/* 1246 */             Class<?> returnType = factory.getRawReturnType();
/*      */             
/* 1248 */             if (returnType.isAssignableFrom(enumClass)) {
/* 1249 */               deser = com.facebook.presto.jdbc.internal.jackson.databind.deser.std.EnumDeserializer.deserializerForCreator(config, enumClass, factory, valueInstantiator, creatorProps);
/* 1250 */               break;
/*      */             }
/* 1252 */           } else if (argCount == 0) {
/* 1253 */             deser = com.facebook.presto.jdbc.internal.jackson.databind.deser.std.EnumDeserializer.deserializerForNoArgsCreator(config, enumClass, factory);
/* 1254 */             break;
/*      */           }
/* 1256 */           throw new IllegalArgumentException("Unsuitable method (" + factory + ") decorated with @JsonCreator (for Enum type " + enumClass.getName() + ")");
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1262 */       if (deser == null) {
/* 1263 */         deser = new com.facebook.presto.jdbc.internal.jackson.databind.deser.std.EnumDeserializer(constructEnumResolver(enumClass, config, beanDesc.findJsonValueMethod()));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1269 */     if (this._factoryConfig.hasDeserializerModifiers()) {
/* 1270 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 1271 */         deser = mod.modifyEnumDeserializer(config, type, beanDesc, deser);
/*      */       }
/*      */     }
/* 1274 */     return deser;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public JsonDeserializer<?> createTreeDeserializer(DeserializationConfig config, JavaType nodeType, BeanDescription beanDesc)
/*      */     throws JsonMappingException
/*      */   {
/* 1283 */     Class<? extends com.facebook.presto.jdbc.internal.jackson.databind.JsonNode> nodeClass = nodeType.getRawClass();
/*      */     
/* 1285 */     JsonDeserializer<?> custom = _findCustomTreeNodeDeserializer(nodeClass, config, beanDesc);
/*      */     
/* 1287 */     if (custom != null) {
/* 1288 */       return custom;
/*      */     }
/* 1290 */     return com.facebook.presto.jdbc.internal.jackson.databind.deser.std.JsonNodeDeserializer.getDeserializer(nodeClass);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public JsonDeserializer<?> createReferenceDeserializer(DeserializationContext ctxt, ReferenceType type, BeanDescription beanDesc)
/*      */     throws JsonMappingException
/*      */   {
/* 1298 */     JavaType contentType = type.getContentType();
/*      */     
/* 1300 */     JsonDeserializer<Object> contentDeser = (JsonDeserializer)contentType.getValueHandler();
/* 1301 */     DeserializationConfig config = ctxt.getConfig();
/*      */     
/* 1303 */     TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
/* 1304 */     if (contentTypeDeser == null) {
/* 1305 */       contentTypeDeser = findTypeDeserializer(config, contentType);
/*      */     }
/* 1307 */     JsonDeserializer<?> deser = _findCustomReferenceDeserializer(type, config, beanDesc, contentTypeDeser, contentDeser);
/*      */     
/*      */ 
/* 1310 */     if (deser == null)
/*      */     {
/* 1312 */       if (java.util.concurrent.atomic.AtomicReference.class.isAssignableFrom(type.getRawClass())) {
/* 1313 */         return new com.facebook.presto.jdbc.internal.jackson.databind.deser.std.AtomicReferenceDeserializer(type, contentTypeDeser, contentDeser);
/*      */       }
/*      */     }
/* 1316 */     if (deser != null)
/*      */     {
/* 1318 */       if (this._factoryConfig.hasDeserializerModifiers()) {
/* 1319 */         for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 1320 */           deser = mod.modifyReferenceDeserializer(config, type, beanDesc, deser);
/*      */         }
/*      */       }
/*      */     }
/* 1324 */     return deser;
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
/*      */   public TypeDeserializer findTypeDeserializer(DeserializationConfig config, JavaType baseType)
/*      */     throws JsonMappingException
/*      */   {
/* 1338 */     BeanDescription bean = config.introspectClassAnnotations(baseType.getRawClass());
/* 1339 */     com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedClass ac = bean.getClassInfo();
/* 1340 */     AnnotationIntrospector ai = config.getAnnotationIntrospector();
/* 1341 */     TypeResolverBuilder<?> b = ai.findTypeResolver(config, ac, baseType);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1346 */     Collection<com.facebook.presto.jdbc.internal.jackson.databind.jsontype.NamedType> subtypes = null;
/* 1347 */     if (b == null) {
/* 1348 */       b = config.getDefaultTyper(baseType);
/* 1349 */       if (b == null) {
/* 1350 */         return null;
/*      */       }
/*      */     } else {
/* 1353 */       subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, ac);
/*      */     }
/*      */     
/*      */ 
/* 1357 */     if ((b.getDefaultImpl() == null) && (baseType.isAbstract())) {
/* 1358 */       JavaType defaultType = mapAbstractType(config, baseType);
/* 1359 */       if ((defaultType != null) && (defaultType.getRawClass() != baseType.getRawClass())) {
/* 1360 */         b = b.defaultImpl(defaultType.getRawClass());
/*      */       }
/*      */     }
/* 1363 */     return b.buildTypeDeserializer(config, baseType, subtypes);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonDeserializer<?> findOptionalStdDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc)
/*      */     throws JsonMappingException
/*      */   {
/* 1375 */     return com.facebook.presto.jdbc.internal.jackson.databind.ext.OptionalHandlerFactory.instance.findDeserializer(type, ctxt.getConfig(), beanDesc);
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
/*      */   public KeyDeserializer createKeyDeserializer(DeserializationContext ctxt, JavaType type)
/*      */     throws JsonMappingException
/*      */   {
/* 1389 */     DeserializationConfig config = ctxt.getConfig();
/* 1390 */     KeyDeserializer deser = null;
/* 1391 */     BeanDescription beanDesc; if (this._factoryConfig.hasKeyDeserializers()) {
/* 1392 */       beanDesc = config.introspectClassAnnotations(type.getRawClass());
/* 1393 */       for (KeyDeserializers d : this._factoryConfig.keyDeserializers()) {
/* 1394 */         deser = d.findKeyDeserializer(type, config, beanDesc);
/* 1395 */         if (deser != null) {
/*      */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1401 */     if (deser == null) {
/* 1402 */       if (type.isEnumType()) {
/* 1403 */         return _createEnumKeyDeserializer(ctxt, type);
/*      */       }
/* 1405 */       deser = com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StdKeyDeserializers.findStringBasedKeyDeserializer(config, type);
/*      */     }
/*      */     
/*      */ 
/* 1409 */     if ((deser != null) && 
/* 1410 */       (this._factoryConfig.hasDeserializerModifiers())) {
/* 1411 */       for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 1412 */         deser = mod.modifyKeyDeserializer(config, type, deser);
/*      */       }
/*      */     }
/*      */     
/* 1416 */     return deser;
/*      */   }
/*      */   
/*      */ 
/*      */   private KeyDeserializer _createEnumKeyDeserializer(DeserializationContext ctxt, JavaType type)
/*      */     throws JsonMappingException
/*      */   {
/* 1423 */     DeserializationConfig config = ctxt.getConfig();
/* 1424 */     Class<?> enumClass = type.getRawClass();
/*      */     
/* 1426 */     BeanDescription beanDesc = config.introspect(type);
/*      */     
/* 1428 */     KeyDeserializer des = findKeyDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
/* 1429 */     if (des != null) {
/* 1430 */       return des;
/*      */     }
/*      */     
/* 1433 */     JsonDeserializer<?> custom = _findCustomEnumDeserializer(enumClass, config, beanDesc);
/* 1434 */     if (custom != null) {
/* 1435 */       return com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, custom);
/*      */     }
/* 1437 */     JsonDeserializer<?> valueDesForKey = findDeserializerFromAnnotation(ctxt, beanDesc.getClassInfo());
/* 1438 */     if (valueDesForKey != null) {
/* 1439 */       return com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StdKeyDeserializers.constructDelegatingKeyDeserializer(config, type, valueDesForKey);
/*      */     }
/*      */     
/* 1442 */     com.facebook.presto.jdbc.internal.jackson.databind.util.EnumResolver enumRes = constructEnumResolver(enumClass, config, beanDesc.findJsonValueMethod());
/*      */     
/* 1444 */     AnnotationIntrospector ai = config.getAnnotationIntrospector();
/* 1445 */     for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
/* 1446 */       if (ai.hasCreatorAnnotation(factory)) {
/* 1447 */         int argCount = factory.getParameterCount();
/* 1448 */         if (argCount == 1) {
/* 1449 */           Class<?> returnType = factory.getRawReturnType();
/*      */           
/* 1451 */           if (returnType.isAssignableFrom(enumClass))
/*      */           {
/* 1453 */             if (factory.getRawParameterType(0) != String.class) {
/* 1454 */               throw new IllegalArgumentException("Parameter #0 type for factory method (" + factory + ") not suitable, must be java.lang.String");
/*      */             }
/* 1456 */             if (config.canOverrideAccessModifiers()) {
/* 1457 */               ClassUtil.checkAndFixAccess(factory.getMember(), ctxt.isEnabled(com.facebook.presto.jdbc.internal.jackson.databind.MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*      */             }
/*      */             
/* 1460 */             return com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StdKeyDeserializers.constructEnumKeyDeserializer(enumRes, factory);
/*      */           }
/*      */         }
/* 1463 */         throw new IllegalArgumentException("Unsuitable method (" + factory + ") decorated with @JsonCreator (for Enum type " + enumClass.getName() + ")");
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1468 */     return com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StdKeyDeserializers.constructEnumKeyDeserializer(enumRes);
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
/*      */   protected final DeserializerFactoryConfig _factoryConfig;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public TypeDeserializer findPropertyTypeDeserializer(DeserializationConfig config, JavaType baseType, AnnotatedMember annotated)
/*      */     throws JsonMappingException
/*      */   {
/* 1494 */     AnnotationIntrospector ai = config.getAnnotationIntrospector();
/* 1495 */     TypeResolverBuilder<?> b = ai.findPropertyTypeResolver(config, annotated, baseType);
/*      */     
/* 1497 */     if (b == null) {
/* 1498 */       return findTypeDeserializer(config, baseType);
/*      */     }
/*      */     
/* 1501 */     Collection<com.facebook.presto.jdbc.internal.jackson.databind.jsontype.NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, annotated, baseType);
/*      */     
/* 1503 */     return b.buildTypeDeserializer(config, baseType, subtypes);
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
/*      */   public TypeDeserializer findPropertyContentTypeDeserializer(DeserializationConfig config, JavaType containerType, AnnotatedMember propertyEntity)
/*      */     throws JsonMappingException
/*      */   {
/* 1521 */     AnnotationIntrospector ai = config.getAnnotationIntrospector();
/* 1522 */     TypeResolverBuilder<?> b = ai.findPropertyContentTypeResolver(config, propertyEntity, containerType);
/* 1523 */     JavaType contentType = containerType.getContentType();
/*      */     
/* 1525 */     if (b == null) {
/* 1526 */       return findTypeDeserializer(config, contentType);
/*      */     }
/*      */     
/* 1529 */     Collection<com.facebook.presto.jdbc.internal.jackson.databind.jsontype.NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByTypeId(config, propertyEntity, contentType);
/*      */     
/* 1531 */     return b.buildTypeDeserializer(config, contentType, subtypes);
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
/*      */   public JsonDeserializer<?> findDefaultDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc)
/*      */     throws JsonMappingException
/*      */   {
/* 1545 */     Class<?> rawType = type.getRawClass();
/*      */     
/* 1547 */     if (rawType == CLASS_OBJECT)
/*      */     {
/* 1549 */       DeserializationConfig config = ctxt.getConfig();
/*      */       JavaType mt;
/*      */       JavaType mt;
/* 1552 */       JavaType lt; if (this._factoryConfig.hasAbstractTypeResolvers()) {
/* 1553 */         JavaType lt = _findRemappedType(config, List.class);
/* 1554 */         mt = _findRemappedType(config, Map.class);
/*      */       } else {
/* 1556 */         lt = mt = null;
/*      */       }
/* 1558 */       return new com.facebook.presto.jdbc.internal.jackson.databind.deser.std.UntypedObjectDeserializer(lt, mt);
/*      */     }
/* 1560 */     if ((rawType == CLASS_STRING) || (rawType == CLASS_CHAR_BUFFER)) {
/* 1561 */       return com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StringDeserializer.instance;
/*      */     }
/* 1563 */     if (rawType == CLASS_ITERABLE)
/*      */     {
/* 1565 */       TypeFactory tf = ctxt.getTypeFactory();
/* 1566 */       JavaType[] tps = tf.findTypeParameters(type, CLASS_ITERABLE);
/* 1567 */       JavaType elemType = (tps == null) || (tps.length != 1) ? TypeFactory.unknownType() : tps[0];
/* 1568 */       CollectionType ct = tf.constructCollectionType(Collection.class, elemType);
/*      */       
/* 1570 */       return createCollectionDeserializer(ctxt, ct, beanDesc);
/*      */     }
/* 1572 */     if (rawType == CLASS_MAP_ENTRY)
/*      */     {
/* 1574 */       JavaType kt = type.containedType(0);
/* 1575 */       if (kt == null) {
/* 1576 */         kt = TypeFactory.unknownType();
/*      */       }
/* 1578 */       JavaType vt = type.containedType(1);
/* 1579 */       if (vt == null) {
/* 1580 */         vt = TypeFactory.unknownType();
/*      */       }
/* 1582 */       TypeDeserializer vts = (TypeDeserializer)vt.getTypeHandler();
/* 1583 */       if (vts == null) {
/* 1584 */         vts = findTypeDeserializer(ctxt.getConfig(), vt);
/*      */       }
/* 1586 */       JsonDeserializer<Object> valueDeser = (JsonDeserializer)vt.getValueHandler();
/* 1587 */       KeyDeserializer keyDes = (KeyDeserializer)kt.getValueHandler();
/* 1588 */       return new com.facebook.presto.jdbc.internal.jackson.databind.deser.std.MapEntryDeserializer(type, keyDes, valueDeser, vts);
/*      */     }
/* 1590 */     String clsName = rawType.getName();
/* 1591 */     if ((rawType.isPrimitive()) || (clsName.startsWith("java.")))
/*      */     {
/* 1593 */       JsonDeserializer<?> deser = com.facebook.presto.jdbc.internal.jackson.databind.deser.std.NumberDeserializers.find(rawType, clsName);
/* 1594 */       if (deser == null) {
/* 1595 */         deser = com.facebook.presto.jdbc.internal.jackson.databind.deser.std.DateDeserializers.find(rawType, clsName);
/*      */       }
/* 1597 */       if (deser != null) {
/* 1598 */         return deser;
/*      */       }
/*      */     }
/*      */     
/* 1602 */     if (rawType == com.facebook.presto.jdbc.internal.jackson.databind.util.TokenBuffer.class) {
/* 1603 */       return new com.facebook.presto.jdbc.internal.jackson.databind.deser.std.TokenBufferDeserializer();
/*      */     }
/* 1605 */     JsonDeserializer<?> deser = findOptionalStdDeserializer(ctxt, type, beanDesc);
/* 1606 */     if (deser != null) {
/* 1607 */       return deser;
/*      */     }
/* 1609 */     return com.facebook.presto.jdbc.internal.jackson.databind.deser.std.JdkDeserializers.find(rawType, clsName);
/*      */   }
/*      */   
/*      */   protected JavaType _findRemappedType(DeserializationConfig config, Class<?> rawType) throws JsonMappingException {
/* 1613 */     JavaType type = mapAbstractType(config, config.constructType(rawType));
/* 1614 */     return (type == null) || (type.hasRawClass(rawType)) ? null : type;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonDeserializer<?> _findCustomTreeNodeDeserializer(Class<? extends com.facebook.presto.jdbc.internal.jackson.databind.JsonNode> type, DeserializationConfig config, BeanDescription beanDesc)
/*      */     throws JsonMappingException
/*      */   {
/* 1627 */     for (Deserializers d : this._factoryConfig.deserializers()) {
/* 1628 */       JsonDeserializer<?> deser = d.findTreeNodeDeserializer(type, config, beanDesc);
/* 1629 */       if (deser != null) {
/* 1630 */         return deser;
/*      */       }
/*      */     }
/* 1633 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected JsonDeserializer<?> _findCustomReferenceDeserializer(ReferenceType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer)
/*      */     throws JsonMappingException
/*      */   {
/* 1641 */     for (Deserializers d : this._factoryConfig.deserializers()) {
/* 1642 */       JsonDeserializer<?> deser = d.findReferenceDeserializer(type, config, beanDesc, contentTypeDeserializer, contentDeserializer);
/*      */       
/* 1644 */       if (deser != null) {
/* 1645 */         return deser;
/*      */       }
/*      */     }
/* 1648 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected JsonDeserializer<Object> _findCustomBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc)
/*      */     throws JsonMappingException
/*      */   {
/* 1656 */     for (Deserializers d : this._factoryConfig.deserializers()) {
/* 1657 */       JsonDeserializer<?> deser = d.findBeanDeserializer(type, config, beanDesc);
/* 1658 */       if (deser != null) {
/* 1659 */         return deser;
/*      */       }
/*      */     }
/* 1662 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected JsonDeserializer<?> _findCustomArrayDeserializer(ArrayType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
/*      */     throws JsonMappingException
/*      */   {
/* 1670 */     for (Deserializers d : this._factoryConfig.deserializers()) {
/* 1671 */       JsonDeserializer<?> deser = d.findArrayDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
/*      */       
/* 1673 */       if (deser != null) {
/* 1674 */         return deser;
/*      */       }
/*      */     }
/* 1677 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected JsonDeserializer<?> _findCustomCollectionDeserializer(CollectionType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
/*      */     throws JsonMappingException
/*      */   {
/* 1685 */     for (Deserializers d : this._factoryConfig.deserializers()) {
/* 1686 */       JsonDeserializer<?> deser = d.findCollectionDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
/*      */       
/* 1688 */       if (deser != null) {
/* 1689 */         return deser;
/*      */       }
/*      */     }
/* 1692 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected JsonDeserializer<?> _findCustomCollectionLikeDeserializer(CollectionLikeType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
/*      */     throws JsonMappingException
/*      */   {
/* 1700 */     for (Deserializers d : this._factoryConfig.deserializers()) {
/* 1701 */       JsonDeserializer<?> deser = d.findCollectionLikeDeserializer(type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
/*      */       
/* 1703 */       if (deser != null) {
/* 1704 */         return deser;
/*      */       }
/*      */     }
/* 1707 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   protected JsonDeserializer<?> _findCustomEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc)
/*      */     throws JsonMappingException
/*      */   {
/* 1714 */     for (Deserializers d : this._factoryConfig.deserializers()) {
/* 1715 */       JsonDeserializer<?> deser = d.findEnumDeserializer(type, config, beanDesc);
/* 1716 */       if (deser != null) {
/* 1717 */         return deser;
/*      */       }
/*      */     }
/* 1720 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonDeserializer<?> _findCustomMapDeserializer(MapType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
/*      */     throws JsonMappingException
/*      */   {
/* 1729 */     for (Deserializers d : this._factoryConfig.deserializers()) {
/* 1730 */       JsonDeserializer<?> deser = d.findMapDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*      */       
/* 1732 */       if (deser != null) {
/* 1733 */         return deser;
/*      */       }
/*      */     }
/* 1736 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JsonDeserializer<?> _findCustomMapLikeDeserializer(MapLikeType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer)
/*      */     throws JsonMappingException
/*      */   {
/* 1745 */     for (Deserializers d : this._factoryConfig.deserializers()) {
/* 1746 */       JsonDeserializer<?> deser = d.findMapLikeDeserializer(type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
/*      */       
/* 1748 */       if (deser != null) {
/* 1749 */         return deser;
/*      */       }
/*      */     }
/* 1752 */     return null;
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
/*      */   protected JsonDeserializer<Object> findDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann)
/*      */     throws JsonMappingException
/*      */   {
/* 1773 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 1774 */     if (intr != null) {
/* 1775 */       Object deserDef = intr.findDeserializer(ann);
/* 1776 */       if (deserDef != null) {
/* 1777 */         return ctxt.deserializerInstance(ann, deserDef);
/*      */       }
/*      */     }
/* 1780 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected KeyDeserializer findKeyDeserializerFromAnnotation(DeserializationContext ctxt, Annotated ann)
/*      */     throws JsonMappingException
/*      */   {
/* 1792 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 1793 */     if (intr != null) {
/* 1794 */       Object deserDef = intr.findKeyDeserializer(ann);
/* 1795 */       if (deserDef != null) {
/* 1796 */         return ctxt.keyDeserializerInstance(ann, deserDef);
/*      */       }
/*      */     }
/* 1799 */     return null;
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
/*      */   protected JavaType resolveMemberAndTypeAnnotations(DeserializationContext ctxt, AnnotatedMember member, JavaType type)
/*      */     throws JsonMappingException
/*      */   {
/* 1815 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 1816 */     if (intr == null) {
/* 1817 */       return type;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1823 */     if (type.isMapLikeType()) {
/* 1824 */       JavaType keyType = type.getKeyType();
/* 1825 */       if (keyType != null) {
/* 1826 */         Object kdDef = intr.findKeyDeserializer(member);
/* 1827 */         KeyDeserializer kd = ctxt.keyDeserializerInstance(member, kdDef);
/* 1828 */         if (kd != null) {
/* 1829 */           type = ((MapLikeType)type).withKeyValueHandler(kd);
/* 1830 */           keyType = type.getKeyType();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1835 */     if (type.hasContentType()) {
/* 1836 */       Object cdDef = intr.findContentDeserializer(member);
/* 1837 */       JsonDeserializer<?> cd = ctxt.deserializerInstance(member, cdDef);
/* 1838 */       if (cd != null) {
/* 1839 */         type = type.withContentValueHandler(cd);
/*      */       }
/* 1841 */       TypeDeserializer contentTypeDeser = findPropertyContentTypeDeserializer(ctxt.getConfig(), type, member);
/*      */       
/* 1843 */       if (contentTypeDeser != null) {
/* 1844 */         type = type.withContentTypeHandler(contentTypeDeser);
/*      */       }
/*      */     }
/* 1847 */     TypeDeserializer valueTypeDeser = findPropertyTypeDeserializer(ctxt.getConfig(), type, member);
/*      */     
/* 1849 */     if (valueTypeDeser != null) {
/* 1850 */       type = type.withTypeHandler(valueTypeDeser);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1858 */     type = intr.refineDeserializationType(ctxt.getConfig(), member, type);
/* 1859 */     return type;
/*      */   }
/*      */   
/*      */ 
/*      */   protected com.facebook.presto.jdbc.internal.jackson.databind.util.EnumResolver constructEnumResolver(Class<?> enumClass, DeserializationConfig config, AnnotatedMethod jsonValueMethod)
/*      */   {
/* 1865 */     if (jsonValueMethod != null) {
/* 1866 */       java.lang.reflect.Method accessor = jsonValueMethod.getAnnotated();
/* 1867 */       if (config.canOverrideAccessModifiers()) {
/* 1868 */         ClassUtil.checkAndFixAccess(accessor, config.isEnabled(com.facebook.presto.jdbc.internal.jackson.databind.MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
/*      */       }
/* 1870 */       return com.facebook.presto.jdbc.internal.jackson.databind.util.EnumResolver.constructUnsafeUsingMethod(enumClass, accessor, config.getAnnotationIntrospector());
/*      */     }
/*      */     
/*      */ 
/* 1874 */     return com.facebook.presto.jdbc.internal.jackson.databind.util.EnumResolver.constructUnsafe(enumClass, config.getAnnotationIntrospector());
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
/*      */   @Deprecated
/*      */   protected JavaType modifyTypeByAnnotation(DeserializationContext ctxt, Annotated a, JavaType type)
/*      */     throws JsonMappingException
/*      */   {
/* 1894 */     AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 1895 */     if (intr == null) {
/* 1896 */       return type;
/*      */     }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1928 */     return intr.refineDeserializationType(ctxt.getConfig(), a, type);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   protected JavaType resolveType(DeserializationContext ctxt, BeanDescription beanDesc, JavaType type, AnnotatedMember member)
/*      */     throws JsonMappingException
/*      */   {
/* 1939 */     return resolveMemberAndTypeAnnotations(ctxt, member, type);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   protected AnnotatedMethod _findJsonValueFor(DeserializationConfig config, JavaType enumType)
/*      */   {
/* 1948 */     if (enumType == null) {
/* 1949 */       return null;
/*      */     }
/* 1951 */     BeanDescription beanDesc = config.introspect(enumType);
/* 1952 */     return beanDesc.findJsonValueMethod();
/*      */   }
/*      */   
/*      */   protected abstract DeserializerFactory withConfig(DeserializerFactoryConfig paramDeserializerFactoryConfig);
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\BasicDeserializerFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */