/*      */ package com.facebook.presto.jdbc.internal.jackson.databind.introspect;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonBackReference;
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator;
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonIdentityInfo;
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonIgnoreProperties.Value;
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonInclude;
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonInclude.Include;
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonManagedReference;
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty;
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonPropertyOrder;
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonRootName;
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo;
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonUnwrapped;
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonView;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyMetadata;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonAppend;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonAppend.Attr;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonAppend.Prop;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonDeserialize;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonSerialize;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.MapperConfig;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.ext.Java7Support;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeResolverBuilder;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.BeanPropertyWriter;
/*      */ import java.lang.annotation.Annotation;
/*      */ import java.lang.reflect.Field;
/*      */ import java.util.List;
/*      */ 
/*      */ public class JacksonAnnotationIntrospector extends com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector implements java.io.Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 1L;
/*   35 */   private static final Class<? extends Annotation>[] ANNOTATIONS_TO_INFER_SER = (Class[])new Class[] { JsonSerialize.class, JsonView.class, com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.class, JsonTypeInfo.class, com.facebook.presto.jdbc.internal.jackson.annotation.JsonRawValue.class, JsonUnwrapped.class, JsonBackReference.class, JsonManagedReference.class };
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
/*   48 */   private static final Class<? extends Annotation>[] ANNOTATIONS_TO_INFER_DESER = (Class[])new Class[] { JsonDeserialize.class, JsonView.class, com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.class, JsonTypeInfo.class, JsonUnwrapped.class, JsonBackReference.class, JsonManagedReference.class };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final Java7Support _java7Helper;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   static
/*      */   {
/*   63 */     Java7Support x = null;
/*      */     try {
/*   65 */       x = Java7Support.instance();
/*      */     } catch (Throwable t) {}
/*   67 */     _java7Helper = x;
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
/*   79 */   protected transient com.facebook.presto.jdbc.internal.jackson.databind.util.LRUMap<Class<?>, Boolean> _annotationsInside = new com.facebook.presto.jdbc.internal.jackson.databind.util.LRUMap(48, 48);
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
/*   95 */   protected boolean _cfgConstructorPropertiesImpliesCreator = true;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public com.facebook.presto.jdbc.internal.jackson.core.Version version()
/*      */   {
/*  107 */     return com.facebook.presto.jdbc.internal.jackson.databind.cfg.PackageVersion.VERSION;
/*      */   }
/*      */   
/*      */   protected Object readResolve() {
/*  111 */     if (this._annotationsInside == null) {
/*  112 */       this._annotationsInside = new com.facebook.presto.jdbc.internal.jackson.databind.util.LRUMap(48, 48);
/*      */     }
/*  114 */     return this;
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
/*      */   public JacksonAnnotationIntrospector setConstructorPropertiesImpliesCreator(boolean b)
/*      */   {
/*  135 */     this._cfgConstructorPropertiesImpliesCreator = b;
/*  136 */     return this;
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
/*      */   public boolean isAnnotationBundle(Annotation ann)
/*      */   {
/*  155 */     Class<?> type = ann.annotationType();
/*  156 */     Boolean b = (Boolean)this._annotationsInside.get(type);
/*  157 */     if (b == null) {
/*  158 */       b = Boolean.valueOf(type.getAnnotation(com.facebook.presto.jdbc.internal.jackson.annotation.JacksonAnnotationsInside.class) != null);
/*  159 */       this._annotationsInside.putIfAbsent(type, b);
/*      */     }
/*  161 */     return b.booleanValue();
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
/*      */   @Deprecated
/*      */   public String findEnumValue(Enum<?> value)
/*      */   {
/*      */     try
/*      */     {
/*  183 */       Field f = value.getClass().getField(value.name());
/*  184 */       if (f != null) {
/*  185 */         JsonProperty prop = (JsonProperty)f.getAnnotation(JsonProperty.class);
/*  186 */         if (prop != null) {
/*  187 */           String n = prop.value();
/*  188 */           if ((n != null) && (!n.isEmpty())) {
/*  189 */             return n;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (SecurityException e) {}catch (NoSuchFieldException e) {}
/*      */     
/*      */ 
/*      */ 
/*  198 */     return value.name();
/*      */   }
/*      */   
/*      */   public String[] findEnumValues(Class<?> enumType, Enum<?>[] enumValues, String[] names)
/*      */   {
/*  203 */     java.util.HashMap<String, String> expl = null;
/*  204 */     for (Field f : com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil.getDeclaredFields(enumType))
/*  205 */       if (f.isEnumConstant())
/*      */       {
/*      */ 
/*  208 */         JsonProperty prop = (JsonProperty)f.getAnnotation(JsonProperty.class);
/*  209 */         if (prop != null)
/*      */         {
/*      */ 
/*  212 */           String n = prop.value();
/*  213 */           if (!n.isEmpty())
/*      */           {
/*      */ 
/*  216 */             if (expl == null) {
/*  217 */               expl = new java.util.HashMap();
/*      */             }
/*  219 */             expl.put(f.getName(), n);
/*      */           }
/*      */         } }
/*  222 */     if (expl != null) {
/*  223 */       int i = 0; for (int end = enumValues.length; i < end; i++) {
/*  224 */         String defName = enumValues[i].name();
/*  225 */         String explValue = (String)expl.get(defName);
/*  226 */         if (explValue != null) {
/*  227 */           names[i] = explValue;
/*      */         }
/*      */       }
/*      */     }
/*  231 */     return names;
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
/*      */   public Enum<?> findDefaultEnumValue(Class<Enum<?>> enumCls)
/*      */   {
/*  245 */     return com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil.findFirstAnnotatedEnumValue(enumCls, com.facebook.presto.jdbc.internal.jackson.annotation.JsonEnumDefaultValue.class);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PropertyName findRootName(AnnotatedClass ac)
/*      */   {
/*  257 */     JsonRootName ann = (JsonRootName)_findAnnotation(ac, JsonRootName.class);
/*  258 */     if (ann == null) {
/*  259 */       return null;
/*      */     }
/*  261 */     String ns = ann.namespace();
/*  262 */     if ((ns != null) && (ns.length() == 0)) {
/*  263 */       ns = null;
/*      */     }
/*  265 */     return PropertyName.construct(ann.value(), ns);
/*      */   }
/*      */   
/*      */ 
/*      */   public JsonIgnoreProperties.Value findPropertyIgnorals(Annotated a)
/*      */   {
/*  271 */     com.facebook.presto.jdbc.internal.jackson.annotation.JsonIgnoreProperties v = (com.facebook.presto.jdbc.internal.jackson.annotation.JsonIgnoreProperties)_findAnnotation(a, com.facebook.presto.jdbc.internal.jackson.annotation.JsonIgnoreProperties.class);
/*  272 */     if (v == null)
/*      */     {
/*  274 */       return null;
/*      */     }
/*  276 */     return JsonIgnoreProperties.Value.from(v);
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public String[] findPropertiesToIgnore(Annotated a, boolean forSerialization)
/*      */   {
/*  282 */     JsonIgnoreProperties.Value v = findPropertyIgnorals(a);
/*  283 */     if (v == null) {
/*  284 */       return null;
/*      */     }
/*      */     
/*  287 */     if (forSerialization) {
/*  288 */       if (v.getAllowGetters()) {
/*  289 */         return null;
/*      */       }
/*      */     }
/*  292 */     else if (v.getAllowSetters()) {
/*  293 */       return null;
/*      */     }
/*      */     
/*  296 */     java.util.Set<String> ignored = v.getIgnored();
/*  297 */     return (String[])ignored.toArray(new String[ignored.size()]);
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public Boolean findIgnoreUnknownProperties(AnnotatedClass a)
/*      */   {
/*  303 */     JsonIgnoreProperties.Value v = findPropertyIgnorals(a);
/*  304 */     return v == null ? null : Boolean.valueOf(v.getIgnoreUnknown());
/*      */   }
/*      */   
/*      */   public Boolean isIgnorableType(AnnotatedClass ac)
/*      */   {
/*  309 */     com.facebook.presto.jdbc.internal.jackson.annotation.JsonIgnoreType ignore = (com.facebook.presto.jdbc.internal.jackson.annotation.JsonIgnoreType)_findAnnotation(ac, com.facebook.presto.jdbc.internal.jackson.annotation.JsonIgnoreType.class);
/*  310 */     return ignore == null ? null : Boolean.valueOf(ignore.value());
/*      */   }
/*      */   
/*      */   public Object findFilterId(Annotated a)
/*      */   {
/*  315 */     com.facebook.presto.jdbc.internal.jackson.annotation.JsonFilter ann = (com.facebook.presto.jdbc.internal.jackson.annotation.JsonFilter)_findAnnotation(a, com.facebook.presto.jdbc.internal.jackson.annotation.JsonFilter.class);
/*  316 */     if (ann != null) {
/*  317 */       String id = ann.value();
/*      */       
/*  319 */       if (id.length() > 0) {
/*  320 */         return id;
/*      */       }
/*      */     }
/*  323 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public Object findNamingStrategy(AnnotatedClass ac)
/*      */   {
/*  329 */     com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonNaming ann = (com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonNaming)_findAnnotation(ac, com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonNaming.class);
/*  330 */     return ann == null ? null : ann.value();
/*      */   }
/*      */   
/*      */   public String findClassDescription(AnnotatedClass ac)
/*      */   {
/*  335 */     com.facebook.presto.jdbc.internal.jackson.annotation.JsonClassDescription ann = (com.facebook.presto.jdbc.internal.jackson.annotation.JsonClassDescription)_findAnnotation(ac, com.facebook.presto.jdbc.internal.jackson.annotation.JsonClassDescription.class);
/*  336 */     return ann == null ? null : ann.value();
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
/*      */   public VisibilityChecker<?> findAutoDetectVisibility(AnnotatedClass ac, VisibilityChecker<?> checker)
/*      */   {
/*  349 */     com.facebook.presto.jdbc.internal.jackson.annotation.JsonAutoDetect ann = (com.facebook.presto.jdbc.internal.jackson.annotation.JsonAutoDetect)_findAnnotation(ac, com.facebook.presto.jdbc.internal.jackson.annotation.JsonAutoDetect.class);
/*  350 */     return ann == null ? checker : checker.with(ann);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String findImplicitPropertyName(AnnotatedMember m)
/*      */   {
/*  361 */     PropertyName n = _findConstructorName(m);
/*  362 */     return n == null ? null : n.getSimpleName();
/*      */   }
/*      */   
/*      */   public boolean hasIgnoreMarker(AnnotatedMember m)
/*      */   {
/*  367 */     return _isIgnorable(m);
/*      */   }
/*      */   
/*      */ 
/*      */   public Boolean hasRequiredMarker(AnnotatedMember m)
/*      */   {
/*  373 */     JsonProperty ann = (JsonProperty)_findAnnotation(m, JsonProperty.class);
/*  374 */     if (ann != null) {
/*  375 */       return Boolean.valueOf(ann.required());
/*      */     }
/*  377 */     return null;
/*      */   }
/*      */   
/*      */   public com.facebook.presto.jdbc.internal.jackson.annotation.JsonProperty.Access findPropertyAccess(Annotated m)
/*      */   {
/*  382 */     JsonProperty ann = (JsonProperty)_findAnnotation(m, JsonProperty.class);
/*  383 */     if (ann != null) {
/*  384 */       return ann.access();
/*      */     }
/*  386 */     return null;
/*      */   }
/*      */   
/*      */   public String findPropertyDescription(Annotated ann)
/*      */   {
/*  391 */     com.facebook.presto.jdbc.internal.jackson.annotation.JsonPropertyDescription desc = (com.facebook.presto.jdbc.internal.jackson.annotation.JsonPropertyDescription)_findAnnotation(ann, com.facebook.presto.jdbc.internal.jackson.annotation.JsonPropertyDescription.class);
/*  392 */     return desc == null ? null : desc.value();
/*      */   }
/*      */   
/*      */   public Integer findPropertyIndex(Annotated ann)
/*      */   {
/*  397 */     JsonProperty prop = (JsonProperty)_findAnnotation(ann, JsonProperty.class);
/*  398 */     if (prop != null) {
/*  399 */       int ix = prop.index();
/*  400 */       if (ix != -1) {
/*  401 */         return Integer.valueOf(ix);
/*      */       }
/*      */     }
/*  404 */     return null;
/*      */   }
/*      */   
/*      */   public String findPropertyDefaultValue(Annotated ann)
/*      */   {
/*  409 */     JsonProperty prop = (JsonProperty)_findAnnotation(ann, JsonProperty.class);
/*  410 */     if (prop == null) {
/*  411 */       return null;
/*      */     }
/*  413 */     String str = prop.defaultValue();
/*      */     
/*  415 */     return str.isEmpty() ? null : str;
/*      */   }
/*      */   
/*      */   public com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Value findFormat(Annotated ann)
/*      */   {
/*  420 */     com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat f = (com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat)_findAnnotation(ann, com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.class);
/*  421 */     return f == null ? null : new com.facebook.presto.jdbc.internal.jackson.annotation.JsonFormat.Value(f);
/*      */   }
/*      */   
/*      */ 
/*      */   public com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector.ReferenceProperty findReferenceType(AnnotatedMember member)
/*      */   {
/*  427 */     JsonManagedReference ref1 = (JsonManagedReference)_findAnnotation(member, JsonManagedReference.class);
/*  428 */     if (ref1 != null) {
/*  429 */       return com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector.ReferenceProperty.managed(ref1.value());
/*      */     }
/*  431 */     JsonBackReference ref2 = (JsonBackReference)_findAnnotation(member, JsonBackReference.class);
/*  432 */     if (ref2 != null) {
/*  433 */       return com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector.ReferenceProperty.back(ref2.value());
/*      */     }
/*  435 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public com.facebook.presto.jdbc.internal.jackson.databind.util.NameTransformer findUnwrappingNameTransformer(AnnotatedMember member)
/*      */   {
/*  441 */     JsonUnwrapped ann = (JsonUnwrapped)_findAnnotation(member, JsonUnwrapped.class);
/*      */     
/*      */ 
/*  444 */     if ((ann == null) || (!ann.enabled())) {
/*  445 */       return null;
/*      */     }
/*  447 */     String prefix = ann.prefix();
/*  448 */     String suffix = ann.suffix();
/*  449 */     return com.facebook.presto.jdbc.internal.jackson.databind.util.NameTransformer.simpleTransformer(prefix, suffix);
/*      */   }
/*      */   
/*      */ 
/*      */   public Object findInjectableValueId(AnnotatedMember m)
/*      */   {
/*  455 */     com.facebook.presto.jdbc.internal.jackson.annotation.JacksonInject ann = (com.facebook.presto.jdbc.internal.jackson.annotation.JacksonInject)_findAnnotation(m, com.facebook.presto.jdbc.internal.jackson.annotation.JacksonInject.class);
/*  456 */     if (ann == null) {
/*  457 */       return null;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  462 */     String id = ann.value();
/*  463 */     if (id.length() == 0)
/*      */     {
/*  465 */       if (!(m instanceof AnnotatedMethod)) {
/*  466 */         return m.getRawType().getName();
/*      */       }
/*  468 */       AnnotatedMethod am = (AnnotatedMethod)m;
/*  469 */       if (am.getParameterCount() == 0) {
/*  470 */         return m.getRawType().getName();
/*      */       }
/*  472 */       return am.getRawParameterType(0).getName();
/*      */     }
/*  474 */     return id;
/*      */   }
/*      */   
/*      */ 
/*      */   public Class<?>[] findViews(Annotated a)
/*      */   {
/*  480 */     JsonView ann = (JsonView)_findAnnotation(a, JsonView.class);
/*  481 */     return ann == null ? null : ann.value();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public AnnotatedMethod resolveSetterConflict(MapperConfig<?> config, AnnotatedMethod setter1, AnnotatedMethod setter2)
/*      */   {
/*  488 */     Class<?> cls1 = setter1.getRawParameterType(0);
/*  489 */     Class<?> cls2 = setter2.getRawParameterType(0);
/*      */     
/*      */ 
/*      */ 
/*  493 */     if (cls1.isPrimitive()) {
/*  494 */       if (!cls2.isPrimitive()) {
/*  495 */         return setter1;
/*      */       }
/*  497 */     } else if (cls2.isPrimitive()) {
/*  498 */       return setter2;
/*      */     }
/*      */     
/*  501 */     if (cls1 == String.class) {
/*  502 */       if (cls2 != String.class) {
/*  503 */         return setter1;
/*      */       }
/*  505 */     } else if (cls2 == String.class) {
/*  506 */       return setter2;
/*      */     }
/*      */     
/*  509 */     return null;
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
/*      */   public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> config, AnnotatedClass ac, JavaType baseType)
/*      */   {
/*  522 */     return _findTypeResolver(config, ac, baseType);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public TypeResolverBuilder<?> findPropertyTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType baseType)
/*      */   {
/*  533 */     if ((baseType.isContainerType()) || (baseType.isReferenceType())) {
/*  534 */       return null;
/*      */     }
/*      */     
/*  537 */     return _findTypeResolver(config, am, baseType);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public TypeResolverBuilder<?> findPropertyContentTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType containerType)
/*      */   {
/*  547 */     if (containerType.getContentType() == null) {
/*  548 */       throw new IllegalArgumentException("Must call method with a container or reference type (got " + containerType + ")");
/*      */     }
/*  550 */     return _findTypeResolver(config, am, containerType);
/*      */   }
/*      */   
/*      */ 
/*      */   public List<com.facebook.presto.jdbc.internal.jackson.databind.jsontype.NamedType> findSubtypes(Annotated a)
/*      */   {
/*  556 */     com.facebook.presto.jdbc.internal.jackson.annotation.JsonSubTypes t = (com.facebook.presto.jdbc.internal.jackson.annotation.JsonSubTypes)_findAnnotation(a, com.facebook.presto.jdbc.internal.jackson.annotation.JsonSubTypes.class);
/*  557 */     if (t == null) return null;
/*  558 */     com.facebook.presto.jdbc.internal.jackson.annotation.JsonSubTypes.Type[] types = t.value();
/*  559 */     java.util.ArrayList<com.facebook.presto.jdbc.internal.jackson.databind.jsontype.NamedType> result = new java.util.ArrayList(types.length);
/*  560 */     for (com.facebook.presto.jdbc.internal.jackson.annotation.JsonSubTypes.Type type : types) {
/*  561 */       result.add(new com.facebook.presto.jdbc.internal.jackson.databind.jsontype.NamedType(type.value(), type.name()));
/*      */     }
/*  563 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   public String findTypeName(AnnotatedClass ac)
/*      */   {
/*  569 */     com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeName tn = (com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeName)_findAnnotation(ac, com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeName.class);
/*  570 */     return tn == null ? null : tn.value();
/*      */   }
/*      */   
/*      */   public Boolean isTypeId(AnnotatedMember member)
/*      */   {
/*  575 */     return Boolean.valueOf(_hasAnnotation(member, com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeId.class));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectIdInfo findObjectIdInfo(Annotated ann)
/*      */   {
/*  586 */     JsonIdentityInfo info = (JsonIdentityInfo)_findAnnotation(ann, JsonIdentityInfo.class);
/*  587 */     if ((info == null) || (info.generator() == com.facebook.presto.jdbc.internal.jackson.annotation.ObjectIdGenerators.None.class)) {
/*  588 */       return null;
/*      */     }
/*      */     
/*  591 */     PropertyName name = PropertyName.construct(info.property());
/*  592 */     return new ObjectIdInfo(name, info.scope(), info.generator(), info.resolver());
/*      */   }
/*      */   
/*      */   public ObjectIdInfo findObjectReferenceInfo(Annotated ann, ObjectIdInfo objectIdInfo)
/*      */   {
/*  597 */     com.facebook.presto.jdbc.internal.jackson.annotation.JsonIdentityReference ref = (com.facebook.presto.jdbc.internal.jackson.annotation.JsonIdentityReference)_findAnnotation(ann, com.facebook.presto.jdbc.internal.jackson.annotation.JsonIdentityReference.class);
/*  598 */     if (ref != null) {
/*  599 */       objectIdInfo = objectIdInfo.withAlwaysAsId(ref.alwaysAsId());
/*      */     }
/*  601 */     return objectIdInfo;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object findSerializer(Annotated a)
/*      */   {
/*  613 */     JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
/*  614 */     if (ann != null)
/*      */     {
/*  616 */       Class<? extends com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer> serClass = ann.using();
/*  617 */       if (serClass != com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer.None.class) {
/*  618 */         return serClass;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  626 */     com.facebook.presto.jdbc.internal.jackson.annotation.JsonRawValue annRaw = (com.facebook.presto.jdbc.internal.jackson.annotation.JsonRawValue)_findAnnotation(a, com.facebook.presto.jdbc.internal.jackson.annotation.JsonRawValue.class);
/*  627 */     if ((annRaw != null) && (annRaw.value()))
/*      */     {
/*  629 */       Class<?> cls = a.getRawType();
/*  630 */       return new com.facebook.presto.jdbc.internal.jackson.databind.ser.std.RawSerializer(cls);
/*      */     }
/*  632 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public Object findKeySerializer(Annotated a)
/*      */   {
/*  638 */     JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
/*  639 */     if (ann != null)
/*      */     {
/*  641 */       Class<? extends com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer> serClass = ann.keyUsing();
/*  642 */       if (serClass != com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer.None.class) {
/*  643 */         return serClass;
/*      */       }
/*      */     }
/*  646 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public Object findContentSerializer(Annotated a)
/*      */   {
/*  652 */     JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
/*  653 */     if (ann != null)
/*      */     {
/*  655 */       Class<? extends com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer> serClass = ann.contentUsing();
/*  656 */       if (serClass != com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer.None.class) {
/*  657 */         return serClass;
/*      */       }
/*      */     }
/*  660 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public Object findNullSerializer(Annotated a)
/*      */   {
/*  666 */     JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
/*  667 */     if (ann != null)
/*      */     {
/*  669 */       Class<? extends com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer> serClass = ann.nullsUsing();
/*  670 */       if (serClass != com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer.None.class) {
/*  671 */         return serClass;
/*      */       }
/*      */     }
/*  674 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public JsonInclude.Include findSerializationInclusion(Annotated a, JsonInclude.Include defValue)
/*      */   {
/*  681 */     JsonInclude inc = (JsonInclude)_findAnnotation(a, JsonInclude.class);
/*  682 */     if (inc != null) {
/*  683 */       JsonInclude.Include v = inc.value();
/*  684 */       if (v != JsonInclude.Include.USE_DEFAULTS) {
/*  685 */         return v;
/*      */       }
/*      */     }
/*  688 */     JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
/*  689 */     if (ann != null) {
/*  690 */       JsonSerialize.Inclusion i2 = ann.include();
/*  691 */       switch (i2) {
/*      */       case ALWAYS: 
/*  693 */         return JsonInclude.Include.ALWAYS;
/*      */       case NON_NULL: 
/*  695 */         return JsonInclude.Include.NON_NULL;
/*      */       case NON_DEFAULT: 
/*  697 */         return JsonInclude.Include.NON_DEFAULT;
/*      */       case NON_EMPTY: 
/*  699 */         return JsonInclude.Include.NON_EMPTY;
/*      */       }
/*      */       
/*      */     }
/*      */     
/*  704 */     return defValue;
/*      */   }
/*      */   
/*      */ 
/*      */   @Deprecated
/*      */   public JsonInclude.Include findSerializationInclusionForContent(Annotated a, JsonInclude.Include defValue)
/*      */   {
/*  711 */     JsonInclude inc = (JsonInclude)_findAnnotation(a, JsonInclude.class);
/*  712 */     if (inc != null) {
/*  713 */       JsonInclude.Include incl = inc.content();
/*  714 */       if (incl != JsonInclude.Include.USE_DEFAULTS) {
/*  715 */         return incl;
/*      */       }
/*      */     }
/*  718 */     return defValue;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public com.facebook.presto.jdbc.internal.jackson.annotation.JsonInclude.Value findPropertyInclusion(Annotated a)
/*      */   {
/*  725 */     JsonInclude inc = (JsonInclude)_findAnnotation(a, JsonInclude.class);
/*  726 */     JsonInclude.Include valueIncl = inc == null ? JsonInclude.Include.USE_DEFAULTS : inc.value();
/*  727 */     if (valueIncl == JsonInclude.Include.USE_DEFAULTS) {
/*  728 */       JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
/*  729 */       if (ann != null) {
/*  730 */         JsonSerialize.Inclusion i2 = ann.include();
/*  731 */         switch (i2) {
/*      */         case ALWAYS: 
/*  733 */           valueIncl = JsonInclude.Include.ALWAYS;
/*  734 */           break;
/*      */         case NON_NULL: 
/*  736 */           valueIncl = JsonInclude.Include.NON_NULL;
/*  737 */           break;
/*      */         case NON_DEFAULT: 
/*  739 */           valueIncl = JsonInclude.Include.NON_DEFAULT;
/*  740 */           break;
/*      */         case NON_EMPTY: 
/*  742 */           valueIncl = JsonInclude.Include.NON_EMPTY;
/*  743 */           break;
/*      */         }
/*      */         
/*      */       }
/*      */     }
/*      */     
/*  749 */     JsonInclude.Include contentIncl = inc == null ? JsonInclude.Include.USE_DEFAULTS : inc.content();
/*  750 */     return com.facebook.presto.jdbc.internal.jackson.annotation.JsonInclude.Value.construct(valueIncl, contentIncl);
/*      */   }
/*      */   
/*      */ 
/*      */   @Deprecated
/*      */   public Class<?> findSerializationType(Annotated am)
/*      */   {
/*  757 */     JsonSerialize ann = (JsonSerialize)_findAnnotation(am, JsonSerialize.class);
/*  758 */     return ann == null ? null : _classIfExplicit(ann.as());
/*      */   }
/*      */   
/*      */ 
/*      */   @Deprecated
/*      */   public Class<?> findSerializationKeyType(Annotated am, JavaType baseType)
/*      */   {
/*  765 */     JsonSerialize ann = (JsonSerialize)_findAnnotation(am, JsonSerialize.class);
/*  766 */     return ann == null ? null : _classIfExplicit(ann.keyAs());
/*      */   }
/*      */   
/*      */ 
/*      */   @Deprecated
/*      */   public Class<?> findSerializationContentType(Annotated am, JavaType baseType)
/*      */   {
/*  773 */     JsonSerialize ann = (JsonSerialize)_findAnnotation(am, JsonSerialize.class);
/*  774 */     return ann == null ? null : _classIfExplicit(ann.contentAs());
/*      */   }
/*      */   
/*      */ 
/*      */   public JsonSerialize.Typing findSerializationTyping(Annotated a)
/*      */   {
/*  780 */     JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
/*  781 */     return ann == null ? null : ann.typing();
/*      */   }
/*      */   
/*      */   public Object findSerializationConverter(Annotated a)
/*      */   {
/*  786 */     JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
/*  787 */     return ann == null ? null : _classIfExplicit(ann.converter(), com.facebook.presto.jdbc.internal.jackson.databind.util.Converter.None.class);
/*      */   }
/*      */   
/*      */   public Object findSerializationContentConverter(AnnotatedMember a)
/*      */   {
/*  792 */     JsonSerialize ann = (JsonSerialize)_findAnnotation(a, JsonSerialize.class);
/*  793 */     return ann == null ? null : _classIfExplicit(ann.contentConverter(), com.facebook.presto.jdbc.internal.jackson.databind.util.Converter.None.class);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String[] findSerializationPropertyOrder(AnnotatedClass ac)
/*      */   {
/*  804 */     JsonPropertyOrder order = (JsonPropertyOrder)_findAnnotation(ac, JsonPropertyOrder.class);
/*  805 */     return order == null ? null : order.value();
/*      */   }
/*      */   
/*      */   public Boolean findSerializationSortAlphabetically(Annotated ann)
/*      */   {
/*  810 */     return _findSortAlpha(ann);
/*      */   }
/*      */   
/*      */   private final Boolean _findSortAlpha(Annotated ann) {
/*  814 */     JsonPropertyOrder order = (JsonPropertyOrder)_findAnnotation(ann, JsonPropertyOrder.class);
/*      */     
/*      */ 
/*      */ 
/*  818 */     if ((order != null) && (order.alphabetic())) {
/*  819 */       return Boolean.TRUE;
/*      */     }
/*  821 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public void findAndAddVirtualProperties(MapperConfig<?> config, AnnotatedClass ac, List<BeanPropertyWriter> properties)
/*      */   {
/*  827 */     JsonAppend ann = (JsonAppend)_findAnnotation(ac, JsonAppend.class);
/*  828 */     if (ann == null) {
/*  829 */       return;
/*      */     }
/*  831 */     boolean prepend = ann.prepend();
/*  832 */     JavaType propType = null;
/*      */     
/*      */ 
/*  835 */     Attr[] attrs = ann.attrs();
/*  836 */     int i = 0; for (int len = attrs.length; i < len; i++) {
/*  837 */       if (propType == null) {
/*  838 */         propType = config.constructType(Object.class);
/*      */       }
/*  840 */       BeanPropertyWriter bpw = _constructVirtualProperty(attrs[i], config, ac, propType);
/*      */       
/*  842 */       if (prepend) {
/*  843 */         properties.add(i, bpw);
/*      */       } else {
/*  845 */         properties.add(bpw);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  850 */     Prop[] props = ann.props();
/*  851 */     int i = 0; for (int len = props.length; i < len; i++) {
/*  852 */       BeanPropertyWriter bpw = _constructVirtualProperty(props[i], config, ac);
/*      */       
/*  854 */       if (prepend) {
/*  855 */         properties.add(i, bpw);
/*      */       } else {
/*  857 */         properties.add(bpw);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected BeanPropertyWriter _constructVirtualProperty(Attr attr, MapperConfig<?> config, AnnotatedClass ac, JavaType type)
/*      */   {
/*  865 */     PropertyMetadata metadata = attr.required() ? PropertyMetadata.STD_REQUIRED : PropertyMetadata.STD_OPTIONAL;
/*      */     
/*      */ 
/*  868 */     String attrName = attr.value();
/*      */     
/*      */ 
/*  871 */     PropertyName propName = _propertyName(attr.propName(), attr.propNamespace());
/*  872 */     if (!propName.hasSimpleName()) {
/*  873 */       propName = PropertyName.construct(attrName);
/*      */     }
/*      */     
/*  876 */     AnnotatedMember member = new VirtualAnnotatedMember(ac, ac.getRawType(), attrName, type);
/*      */     
/*      */ 
/*  879 */     com.facebook.presto.jdbc.internal.jackson.databind.util.SimpleBeanPropertyDefinition propDef = com.facebook.presto.jdbc.internal.jackson.databind.util.SimpleBeanPropertyDefinition.construct(config, member, propName, metadata, attr.include());
/*      */     
/*      */ 
/*  882 */     return com.facebook.presto.jdbc.internal.jackson.databind.ser.impl.AttributePropertyWriter.construct(attrName, propDef, ac.getAnnotations(), type);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected BeanPropertyWriter _constructVirtualProperty(Prop prop, MapperConfig<?> config, AnnotatedClass ac)
/*      */   {
/*  889 */     PropertyMetadata metadata = prop.required() ? PropertyMetadata.STD_REQUIRED : PropertyMetadata.STD_OPTIONAL;
/*      */     
/*  891 */     PropertyName propName = _propertyName(prop.name(), prop.namespace());
/*  892 */     JavaType type = config.constructType(prop.type());
/*      */     
/*  894 */     AnnotatedMember member = new VirtualAnnotatedMember(ac, ac.getRawType(), propName.getSimpleName(), type);
/*      */     
/*      */ 
/*  897 */     com.facebook.presto.jdbc.internal.jackson.databind.util.SimpleBeanPropertyDefinition propDef = com.facebook.presto.jdbc.internal.jackson.databind.util.SimpleBeanPropertyDefinition.construct(config, member, propName, metadata, prop.include());
/*      */     
/*      */ 
/*  900 */     Class<?> implClass = prop.value();
/*      */     
/*  902 */     com.facebook.presto.jdbc.internal.jackson.databind.cfg.HandlerInstantiator hi = config.getHandlerInstantiator();
/*  903 */     com.facebook.presto.jdbc.internal.jackson.databind.ser.VirtualBeanPropertyWriter bpw = hi == null ? null : hi.virtualPropertyWriterInstance(config, implClass);
/*      */     
/*  905 */     if (bpw == null) {
/*  906 */       bpw = (com.facebook.presto.jdbc.internal.jackson.databind.ser.VirtualBeanPropertyWriter)com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil.createInstance(implClass, config.canOverrideAccessModifiers());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  911 */     return bpw.withConfig(config, ac, propDef, type);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public PropertyName findNameForSerialization(Annotated a)
/*      */   {
/*  923 */     com.facebook.presto.jdbc.internal.jackson.annotation.JsonGetter jg = (com.facebook.presto.jdbc.internal.jackson.annotation.JsonGetter)_findAnnotation(a, com.facebook.presto.jdbc.internal.jackson.annotation.JsonGetter.class);
/*  924 */     if (jg != null) {
/*  925 */       return PropertyName.construct(jg.value());
/*      */     }
/*  927 */     JsonProperty pann = (JsonProperty)_findAnnotation(a, JsonProperty.class);
/*  928 */     if (pann != null) {
/*  929 */       return PropertyName.construct(pann.value());
/*      */     }
/*  931 */     if (_hasOneOf(a, ANNOTATIONS_TO_INFER_SER)) {
/*  932 */       return PropertyName.USE_DEFAULT;
/*      */     }
/*  934 */     return null;
/*      */   }
/*      */   
/*      */   public boolean hasAsValueAnnotation(AnnotatedMethod am)
/*      */   {
/*  939 */     com.facebook.presto.jdbc.internal.jackson.annotation.JsonValue ann = (com.facebook.presto.jdbc.internal.jackson.annotation.JsonValue)_findAnnotation(am, com.facebook.presto.jdbc.internal.jackson.annotation.JsonValue.class);
/*      */     
/*  941 */     return (ann != null) && (ann.value());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object findDeserializer(Annotated a)
/*      */   {
/*  953 */     JsonDeserialize ann = (JsonDeserialize)_findAnnotation(a, JsonDeserialize.class);
/*  954 */     if (ann != null)
/*      */     {
/*  956 */       Class<? extends com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer> deserClass = ann.using();
/*  957 */       if (deserClass != com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer.None.class) {
/*  958 */         return deserClass;
/*      */       }
/*      */     }
/*  961 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public Object findKeyDeserializer(Annotated a)
/*      */   {
/*  967 */     JsonDeserialize ann = (JsonDeserialize)_findAnnotation(a, JsonDeserialize.class);
/*  968 */     if (ann != null) {
/*  969 */       Class<? extends com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer> deserClass = ann.keyUsing();
/*  970 */       if (deserClass != com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer.None.class) {
/*  971 */         return deserClass;
/*      */       }
/*      */     }
/*  974 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public Object findContentDeserializer(Annotated a)
/*      */   {
/*  980 */     JsonDeserialize ann = (JsonDeserialize)_findAnnotation(a, JsonDeserialize.class);
/*  981 */     if (ann != null)
/*      */     {
/*  983 */       Class<? extends com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer> deserClass = ann.contentUsing();
/*  984 */       if (deserClass != com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer.None.class) {
/*  985 */         return deserClass;
/*      */       }
/*      */     }
/*  988 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public Object findDeserializationConverter(Annotated a)
/*      */   {
/*  994 */     JsonDeserialize ann = (JsonDeserialize)_findAnnotation(a, JsonDeserialize.class);
/*  995 */     return ann == null ? null : _classIfExplicit(ann.converter(), com.facebook.presto.jdbc.internal.jackson.databind.util.Converter.None.class);
/*      */   }
/*      */   
/*      */ 
/*      */   public Object findDeserializationContentConverter(AnnotatedMember a)
/*      */   {
/* 1001 */     JsonDeserialize ann = (JsonDeserialize)_findAnnotation(a, JsonDeserialize.class);
/* 1002 */     return ann == null ? null : _classIfExplicit(ann.contentConverter(), com.facebook.presto.jdbc.internal.jackson.databind.util.Converter.None.class);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public Class<?> findDeserializationContentType(Annotated am, JavaType baseContentType)
/*      */   {
/* 1015 */     JsonDeserialize ann = (JsonDeserialize)_findAnnotation(am, JsonDeserialize.class);
/* 1016 */     return ann == null ? null : _classIfExplicit(ann.contentAs());
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public Class<?> findDeserializationType(Annotated am, JavaType baseType)
/*      */   {
/* 1022 */     JsonDeserialize ann = (JsonDeserialize)_findAnnotation(am, JsonDeserialize.class);
/* 1023 */     return ann == null ? null : _classIfExplicit(ann.as());
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public Class<?> findDeserializationKeyType(Annotated am, JavaType baseKeyType)
/*      */   {
/* 1029 */     JsonDeserialize ann = (JsonDeserialize)_findAnnotation(am, JsonDeserialize.class);
/* 1030 */     return ann == null ? null : _classIfExplicit(ann.keyAs());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object findValueInstantiator(AnnotatedClass ac)
/*      */   {
/* 1042 */     com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonValueInstantiator ann = (com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonValueInstantiator)_findAnnotation(ac, com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonValueInstantiator.class);
/*      */     
/* 1044 */     return ann == null ? null : ann.value();
/*      */   }
/*      */   
/*      */ 
/*      */   public Class<?> findPOJOBuilder(AnnotatedClass ac)
/*      */   {
/* 1050 */     JsonDeserialize ann = (JsonDeserialize)_findAnnotation(ac, JsonDeserialize.class);
/* 1051 */     return ann == null ? null : _classIfExplicit(ann.builder());
/*      */   }
/*      */   
/*      */ 
/*      */   public com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonPOJOBuilder.Value findPOJOBuilderConfig(AnnotatedClass ac)
/*      */   {
/* 1057 */     com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonPOJOBuilder ann = (com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonPOJOBuilder)_findAnnotation(ac, com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonPOJOBuilder.class);
/* 1058 */     return ann == null ? null : new com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonPOJOBuilder.Value(ann);
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
/*      */   public PropertyName findNameForDeserialization(Annotated a)
/*      */   {
/* 1072 */     com.facebook.presto.jdbc.internal.jackson.annotation.JsonSetter js = (com.facebook.presto.jdbc.internal.jackson.annotation.JsonSetter)_findAnnotation(a, com.facebook.presto.jdbc.internal.jackson.annotation.JsonSetter.class);
/* 1073 */     if (js != null) {
/* 1074 */       return PropertyName.construct(js.value());
/*      */     }
/* 1076 */     JsonProperty pann = (JsonProperty)_findAnnotation(a, JsonProperty.class);
/* 1077 */     if (pann != null) {
/* 1078 */       return PropertyName.construct(pann.value());
/*      */     }
/* 1080 */     if (_hasOneOf(a, ANNOTATIONS_TO_INFER_DESER)) {
/* 1081 */       return PropertyName.USE_DEFAULT;
/*      */     }
/* 1083 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean hasAnySetterAnnotation(AnnotatedMethod am)
/*      */   {
/* 1093 */     return _hasAnnotation(am, com.facebook.presto.jdbc.internal.jackson.annotation.JsonAnySetter.class);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean hasAnyGetterAnnotation(AnnotatedMethod am)
/*      */   {
/* 1102 */     return _hasAnnotation(am, com.facebook.presto.jdbc.internal.jackson.annotation.JsonAnyGetter.class);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean hasCreatorAnnotation(Annotated a)
/*      */   {
/* 1111 */     JsonCreator ann = (JsonCreator)_findAnnotation(a, JsonCreator.class);
/* 1112 */     if (ann != null) {
/* 1113 */       return ann.mode() != com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator.Mode.DISABLED;
/*      */     }
/*      */     
/*      */ 
/* 1117 */     if ((this._cfgConstructorPropertiesImpliesCreator) && 
/* 1118 */       ((a instanceof AnnotatedConstructor)) && 
/* 1119 */       (_java7Helper != null)) {
/* 1120 */       Boolean b = _java7Helper.hasCreatorAnnotation(a);
/* 1121 */       if (b != null) {
/* 1122 */         return b.booleanValue();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/* 1127 */     return false;
/*      */   }
/*      */   
/*      */   public com.facebook.presto.jdbc.internal.jackson.annotation.JsonCreator.Mode findCreatorBinding(Annotated a)
/*      */   {
/* 1132 */     JsonCreator ann = (JsonCreator)_findAnnotation(a, JsonCreator.class);
/* 1133 */     return ann == null ? null : ann.mode();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean _isIgnorable(Annotated a)
/*      */   {
/* 1144 */     com.facebook.presto.jdbc.internal.jackson.annotation.JsonIgnore ann = (com.facebook.presto.jdbc.internal.jackson.annotation.JsonIgnore)_findAnnotation(a, com.facebook.presto.jdbc.internal.jackson.annotation.JsonIgnore.class);
/* 1145 */     if (ann != null) {
/* 1146 */       return ann.value();
/*      */     }
/* 1148 */     if (_java7Helper != null) {
/* 1149 */       Boolean b = _java7Helper.findTransient(a);
/* 1150 */       if (b != null) {
/* 1151 */         return b.booleanValue();
/*      */       }
/*      */     }
/* 1154 */     return false;
/*      */   }
/*      */   
/*      */   protected Class<?> _classIfExplicit(Class<?> cls) {
/* 1158 */     if ((cls == null) || (com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil.isBogusClass(cls))) {
/* 1159 */       return null;
/*      */     }
/* 1161 */     return cls;
/*      */   }
/*      */   
/*      */   protected Class<?> _classIfExplicit(Class<?> cls, Class<?> implicit) {
/* 1165 */     cls = _classIfExplicit(cls);
/* 1166 */     return (cls == null) || (cls == implicit) ? null : cls;
/*      */   }
/*      */   
/*      */   protected PropertyName _propertyName(String localName, String namespace) {
/* 1170 */     if (localName.isEmpty()) {
/* 1171 */       return PropertyName.USE_DEFAULT;
/*      */     }
/* 1173 */     if ((namespace == null) || (namespace.isEmpty())) {
/* 1174 */       return PropertyName.construct(localName);
/*      */     }
/* 1176 */     return PropertyName.construct(localName, namespace);
/*      */   }
/*      */   
/*      */   protected PropertyName _findConstructorName(Annotated a)
/*      */   {
/* 1181 */     if ((a instanceof AnnotatedParameter)) {
/* 1182 */       AnnotatedParameter p = (AnnotatedParameter)a;
/* 1183 */       AnnotatedWithParams ctor = p.getOwner();
/*      */       
/* 1185 */       if ((ctor != null) && 
/* 1186 */         (_java7Helper != null)) {
/* 1187 */         PropertyName name = _java7Helper.findConstructorName(p);
/* 1188 */         if (name != null) {
/* 1189 */           return name;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1194 */     return null;
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
/*      */   protected TypeResolverBuilder<?> _findTypeResolver(MapperConfig<?> config, Annotated ann, JavaType baseType)
/*      */   {
/* 1207 */     JsonTypeInfo info = (JsonTypeInfo)_findAnnotation(ann, JsonTypeInfo.class);
/* 1208 */     com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonTypeResolver resAnn = (com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonTypeResolver)_findAnnotation(ann, com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonTypeResolver.class);
/*      */     TypeResolverBuilder<?> b;
/* 1210 */     if (resAnn != null) {
/* 1211 */       if (info == null) {
/* 1212 */         return null;
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1218 */       b = config.typeResolverBuilderInstance(ann, resAnn.value());
/*      */     } else {
/* 1220 */       if (info == null) {
/* 1221 */         return null;
/*      */       }
/*      */       
/* 1224 */       if (info.use() == com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo.Id.NONE) {
/* 1225 */         return _constructNoTypeResolverBuilder();
/*      */       }
/* 1227 */       b = _constructStdTypeResolverBuilder();
/*      */     }
/*      */     
/* 1230 */     com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonTypeIdResolver idResInfo = (com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonTypeIdResolver)_findAnnotation(ann, com.facebook.presto.jdbc.internal.jackson.databind.annotation.JsonTypeIdResolver.class);
/* 1231 */     com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeIdResolver idRes = idResInfo == null ? null : config.typeIdResolverInstance(ann, idResInfo.value());
/*      */     
/* 1233 */     if (idRes != null) {
/* 1234 */       idRes.init(baseType);
/*      */     }
/* 1236 */     TypeResolverBuilder<?> b = b.init(info.use(), idRes);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1241 */     com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo.As inclusion = info.include();
/* 1242 */     if ((inclusion == com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo.As.EXTERNAL_PROPERTY) && ((ann instanceof AnnotatedClass))) {
/* 1243 */       inclusion = com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo.As.PROPERTY;
/*      */     }
/* 1245 */     b = b.inclusion(inclusion);
/* 1246 */     b = b.typeProperty(info.property());
/* 1247 */     Class<?> defaultImpl = info.defaultImpl();
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1253 */     if ((defaultImpl != com.facebook.presto.jdbc.internal.jackson.annotation.JsonTypeInfo.None.class) && (!defaultImpl.isAnnotation())) {
/* 1254 */       b = b.defaultImpl(defaultImpl);
/*      */     }
/* 1256 */     b = b.typeIdVisibility(info.visible());
/* 1257 */     return b;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected com.facebook.presto.jdbc.internal.jackson.databind.jsontype.impl.StdTypeResolverBuilder _constructStdTypeResolverBuilder()
/*      */   {
/* 1265 */     return new com.facebook.presto.jdbc.internal.jackson.databind.jsontype.impl.StdTypeResolverBuilder();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected com.facebook.presto.jdbc.internal.jackson.databind.jsontype.impl.StdTypeResolverBuilder _constructNoTypeResolverBuilder()
/*      */   {
/* 1273 */     return com.facebook.presto.jdbc.internal.jackson.databind.jsontype.impl.StdTypeResolverBuilder.noTypeInfoBuilder();
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\introspect\JacksonAnnotationIntrospector.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */