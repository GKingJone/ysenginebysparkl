/*      */ package com.facebook.presto.jdbc.internal.jackson.databind.introspect;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jackson.annotation.JsonAnySetter;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.MapperFeature;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyName;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.PropertyNamingStrategy;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.HandlerInstantiator;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.MapperConfig;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.util.BeanUtil;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.TreeMap;
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
/*      */ public class POJOPropertiesCollector
/*      */ {
/*      */   protected final MapperConfig<?> _config;
/*      */   protected final boolean _forSerialization;
/*      */   protected final boolean _stdBeanNaming;
/*      */   protected final JavaType _type;
/*      */   protected final AnnotatedClass _classDef;
/*      */   protected final VisibilityChecker<?> _visibilityChecker;
/*      */   protected final AnnotationIntrospector _annotationIntrospector;
/*      */   protected final String _mutatorPrefix;
/*      */   protected boolean _collected;
/*      */   protected LinkedHashMap<String, POJOPropertyBuilder> _properties;
/*      */   protected LinkedList<POJOPropertyBuilder> _creatorProperties;
/*      */   protected LinkedList<AnnotatedMember> _anyGetters;
/*      */   protected LinkedList<AnnotatedMethod> _anySetters;
/*      */   protected LinkedList<AnnotatedMember> _anySetterField;
/*      */   protected LinkedList<AnnotatedMethod> _jsonValueGetters;
/*      */   protected HashSet<String> _ignoredPropertyNames;
/*      */   protected LinkedHashMap<Object, AnnotatedMember> _injectables;
/*      */   
/*      */   protected POJOPropertiesCollector(MapperConfig<?> config, boolean forSerialization, JavaType type, AnnotatedClass classDef, String mutatorPrefix)
/*      */   {
/*  118 */     this._config = config;
/*  119 */     this._stdBeanNaming = config.isEnabled(MapperFeature.USE_STD_BEAN_NAMING);
/*  120 */     this._forSerialization = forSerialization;
/*  121 */     this._type = type;
/*  122 */     this._classDef = classDef;
/*  123 */     this._mutatorPrefix = (mutatorPrefix == null ? "set" : mutatorPrefix);
/*  124 */     this._annotationIntrospector = (config.isAnnotationProcessingEnabled() ? this._config.getAnnotationIntrospector() : null);
/*      */     
/*  126 */     if (this._annotationIntrospector == null) {
/*  127 */       this._visibilityChecker = this._config.getDefaultVisibilityChecker();
/*      */     } else {
/*  129 */       this._visibilityChecker = this._annotationIntrospector.findAutoDetectVisibility(classDef, this._config.getDefaultVisibilityChecker());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public MapperConfig<?> getConfig()
/*      */   {
/*  141 */     return this._config;
/*      */   }
/*      */   
/*      */   public JavaType getType() {
/*  145 */     return this._type;
/*      */   }
/*      */   
/*      */   public AnnotatedClass getClassDef() {
/*  149 */     return this._classDef;
/*      */   }
/*      */   
/*      */   public AnnotationIntrospector getAnnotationIntrospector() {
/*  153 */     return this._annotationIntrospector;
/*      */   }
/*      */   
/*      */   public List<BeanPropertyDefinition> getProperties()
/*      */   {
/*  158 */     Map<String, POJOPropertyBuilder> props = getPropertyMap();
/*  159 */     return new ArrayList(props.values());
/*      */   }
/*      */   
/*      */   public Map<Object, AnnotatedMember> getInjectables() {
/*  163 */     if (!this._collected) {
/*  164 */       collectAll();
/*      */     }
/*  166 */     return this._injectables;
/*      */   }
/*      */   
/*      */   public AnnotatedMethod getJsonValueMethod()
/*      */   {
/*  171 */     if (!this._collected) {
/*  172 */       collectAll();
/*      */     }
/*      */     
/*  175 */     if (this._jsonValueGetters != null) {
/*  176 */       if (this._jsonValueGetters.size() > 1) {
/*  177 */         reportProblem("Multiple value properties defined (" + this._jsonValueGetters.get(0) + " vs " + this._jsonValueGetters.get(1) + ")");
/*      */       }
/*      */       
/*      */ 
/*  181 */       return (AnnotatedMethod)this._jsonValueGetters.get(0);
/*      */     }
/*  183 */     return null;
/*      */   }
/*      */   
/*      */   public AnnotatedMember getAnyGetter()
/*      */   {
/*  188 */     if (!this._collected) {
/*  189 */       collectAll();
/*      */     }
/*  191 */     if (this._anyGetters != null) {
/*  192 */       if (this._anyGetters.size() > 1) {
/*  193 */         reportProblem("Multiple 'any-getters' defined (" + this._anyGetters.get(0) + " vs " + this._anyGetters.get(1) + ")");
/*      */       }
/*      */       
/*  196 */       return (AnnotatedMember)this._anyGetters.getFirst();
/*      */     }
/*  198 */     return null;
/*      */   }
/*      */   
/*      */   public AnnotatedMember getAnySetterField()
/*      */   {
/*  203 */     if (!this._collected) {
/*  204 */       collectAll();
/*      */     }
/*  206 */     if (this._anySetterField != null) {
/*  207 */       if (this._anySetterField.size() > 1) {
/*  208 */         reportProblem("Multiple 'any-Setters' defined (" + this._anySetters.get(0) + " vs " + this._anySetterField.get(1) + ")");
/*      */       }
/*      */       
/*  211 */       return (AnnotatedMember)this._anySetterField.getFirst();
/*      */     }
/*  213 */     return null;
/*      */   }
/*      */   
/*      */   public AnnotatedMethod getAnySetterMethod()
/*      */   {
/*  218 */     if (!this._collected) {
/*  219 */       collectAll();
/*      */     }
/*  221 */     if (this._anySetters != null) {
/*  222 */       if (this._anySetters.size() > 1) {
/*  223 */         reportProblem("Multiple 'any-setters' defined (" + this._anySetters.get(0) + " vs " + this._anySetters.get(1) + ")");
/*      */       }
/*      */       
/*  226 */       return (AnnotatedMethod)this._anySetters.getFirst();
/*      */     }
/*  228 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Set<String> getIgnoredPropertyNames()
/*      */   {
/*  236 */     return this._ignoredPropertyNames;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ObjectIdInfo getObjectIdInfo()
/*      */   {
/*  245 */     if (this._annotationIntrospector == null) {
/*  246 */       return null;
/*      */     }
/*  248 */     ObjectIdInfo info = this._annotationIntrospector.findObjectIdInfo(this._classDef);
/*  249 */     if (info != null) {
/*  250 */       info = this._annotationIntrospector.findObjectReferenceInfo(this._classDef, info);
/*      */     }
/*  252 */     return info;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public Class<?> findPOJOBuilderClass()
/*      */   {
/*  260 */     return this._annotationIntrospector.findPOJOBuilder(this._classDef);
/*      */   }
/*      */   
/*      */   protected Map<String, POJOPropertyBuilder> getPropertyMap()
/*      */   {
/*  265 */     if (!this._collected) {
/*  266 */       collectAll();
/*      */     }
/*  268 */     return this._properties;
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
/*      */   public POJOPropertiesCollector collect()
/*      */   {
/*  288 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void collectAll()
/*      */   {
/*  298 */     LinkedHashMap<String, POJOPropertyBuilder> props = new LinkedHashMap();
/*      */     
/*      */ 
/*  301 */     _addFields(props);
/*  302 */     _addMethods(props);
/*  303 */     _addCreators(props);
/*  304 */     _addInjectables(props);
/*      */     
/*      */ 
/*      */ 
/*  308 */     _removeUnwantedProperties(props);
/*      */     
/*      */ 
/*  311 */     for (POJOPropertyBuilder property : props.values()) {
/*  312 */       property.mergeAnnotations(this._forSerialization);
/*      */     }
/*      */     
/*      */ 
/*  316 */     _removeUnwantedAccessor(props);
/*      */     
/*      */ 
/*  319 */     _renameProperties(props);
/*      */     
/*      */ 
/*  322 */     PropertyNamingStrategy naming = _findNamingStrategy();
/*  323 */     if (naming != null) {
/*  324 */       _renameUsing(props, naming);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  331 */     for (POJOPropertyBuilder property : props.values()) {
/*  332 */       property.trimByVisibility();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  338 */     if (this._config.isEnabled(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME)) {
/*  339 */       _renameWithWrappers(props);
/*      */     }
/*      */     
/*      */ 
/*  343 */     _sortProperties(props);
/*  344 */     this._properties = props;
/*      */     
/*  346 */     this._collected = true;
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
/*      */   protected void _addFields(Map<String, POJOPropertyBuilder> props)
/*      */   {
/*  360 */     AnnotationIntrospector ai = this._annotationIntrospector;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  365 */     boolean pruneFinalFields = (!this._forSerialization) && (!this._config.isEnabled(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS));
/*  366 */     boolean transientAsIgnoral = this._config.isEnabled(MapperFeature.PROPAGATE_TRANSIENT_MARKER);
/*      */     
/*  368 */     for (AnnotatedField f : this._classDef.fields()) {
/*  369 */       String implName = ai == null ? null : ai.findImplicitPropertyName(f);
/*  370 */       if (implName == null) {
/*  371 */         implName = f.getName();
/*      */       }
/*      */       
/*      */       PropertyName pn;
/*      */       PropertyName pn;
/*  376 */       if (ai == null) {
/*  377 */         pn = null; } else { PropertyName pn;
/*  378 */         if (this._forSerialization)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  384 */           pn = ai.findNameForSerialization(f);
/*      */         } else
/*  386 */           pn = ai.findNameForDeserialization(f);
/*      */       }
/*  388 */       boolean hasName = pn != null;
/*  389 */       boolean nameExplicit = hasName;
/*      */       
/*  391 */       if ((nameExplicit) && (pn.isEmpty())) {
/*  392 */         pn = _propNameFromSimple(implName);
/*  393 */         nameExplicit = false;
/*      */       }
/*      */       
/*  396 */       boolean visible = pn != null;
/*  397 */       if (!visible) {
/*  398 */         visible = this._visibilityChecker.isFieldVisible(f);
/*      */       }
/*      */       
/*  401 */       boolean ignored = (ai != null) && (ai.hasIgnoreMarker(f));
/*      */       
/*      */ 
/*  404 */       if (f.isTransient())
/*      */       {
/*      */ 
/*  407 */         if (!hasName) {
/*  408 */           visible = false;
/*  409 */           if (transientAsIgnoral) {
/*  410 */             ignored = true;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  419 */       if ((!pruneFinalFields) || (pn != null) || (ignored) || (!Modifier.isFinal(f.getModifiers())))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  424 */         if (f.hasAnnotation(JsonAnySetter.class)) {
/*  425 */           if (this._anySetterField == null) {
/*  426 */             this._anySetterField = new LinkedList();
/*      */           }
/*  428 */           this._anySetterField.add(f);
/*      */         }
/*      */         
/*  431 */         _property(props, implName).addField(f, pn, nameExplicit, visible, ignored);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _addCreators(Map<String, POJOPropertyBuilder> props)
/*      */   {
/*  441 */     if (this._annotationIntrospector == null) {
/*  442 */       return;
/*      */     }
/*  444 */     for (AnnotatedConstructor ctor : this._classDef.getConstructors()) {
/*  445 */       if (this._creatorProperties == null) {
/*  446 */         this._creatorProperties = new LinkedList();
/*      */       }
/*  448 */       int i = 0; for (int len = ctor.getParameterCount(); i < len; i++) {
/*  449 */         _addCreatorParam(props, ctor.getParameter(i));
/*      */       }
/*      */     }
/*  452 */     for (AnnotatedMethod factory : this._classDef.getStaticMethods()) {
/*  453 */       if (this._creatorProperties == null) {
/*  454 */         this._creatorProperties = new LinkedList();
/*      */       }
/*  456 */       int i = 0; for (int len = factory.getParameterCount(); i < len; i++) {
/*  457 */         _addCreatorParam(props, factory.getParameter(i));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _addCreatorParam(Map<String, POJOPropertyBuilder> props, AnnotatedParameter param)
/*      */   {
/*  469 */     String impl = this._annotationIntrospector.findImplicitPropertyName(param);
/*  470 */     if (impl == null) {
/*  471 */       impl = "";
/*      */     }
/*  473 */     PropertyName pn = this._annotationIntrospector.findNameForDeserialization(param);
/*  474 */     boolean expl = (pn != null) && (!pn.isEmpty());
/*  475 */     if (!expl) {
/*  476 */       if (impl.isEmpty())
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  481 */         return;
/*      */       }
/*      */       
/*  484 */       if (!this._annotationIntrospector.hasCreatorAnnotation(param.getOwner())) {
/*  485 */         return;
/*      */       }
/*  487 */       pn = PropertyName.construct(impl);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  497 */     POJOPropertyBuilder prop = (expl) && (impl.isEmpty()) ? _property(props, pn) : _property(props, impl);
/*      */     
/*  499 */     prop.addCtor(param, pn, expl, true, false);
/*  500 */     this._creatorProperties.add(prop);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _addMethods(Map<String, POJOPropertyBuilder> props)
/*      */   {
/*  508 */     AnnotationIntrospector ai = this._annotationIntrospector;
/*      */     
/*  510 */     for (AnnotatedMethod m : this._classDef.memberMethods())
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  516 */       int argCount = m.getParameterCount();
/*  517 */       if (argCount == 0) {
/*  518 */         _addGetterMethod(props, m, ai);
/*  519 */       } else if (argCount == 1) {
/*  520 */         _addSetterMethod(props, m, ai);
/*  521 */       } else if ((argCount == 2) && 
/*  522 */         (ai != null) && (ai.hasAnySetterAnnotation(m))) {
/*  523 */         if (this._anySetters == null) {
/*  524 */           this._anySetters = new LinkedList();
/*      */         }
/*  526 */         this._anySetters.add(m);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _addGetterMethod(Map<String, POJOPropertyBuilder> props, AnnotatedMethod m, AnnotationIntrospector ai)
/*      */   {
/*  536 */     if (!m.hasReturnType()) {
/*  537 */       return;
/*      */     }
/*      */     
/*      */ 
/*  541 */     if (ai != null) {
/*  542 */       if (ai.hasAnyGetterAnnotation(m)) {
/*  543 */         if (this._anyGetters == null) {
/*  544 */           this._anyGetters = new LinkedList();
/*      */         }
/*  546 */         this._anyGetters.add(m);
/*  547 */         return;
/*      */       }
/*      */       
/*  550 */       if (ai.hasAsValueAnnotation(m)) {
/*  551 */         if (this._jsonValueGetters == null) {
/*  552 */           this._jsonValueGetters = new LinkedList();
/*      */         }
/*  554 */         this._jsonValueGetters.add(m);
/*  555 */         return;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  561 */     PropertyName pn = ai == null ? null : ai.findNameForSerialization(m);
/*  562 */     boolean nameExplicit = pn != null;
/*      */     boolean visible;
/*  564 */     String implName; boolean visible; if (!nameExplicit) {
/*  565 */       String implName = ai == null ? null : ai.findImplicitPropertyName(m);
/*  566 */       if (implName == null)
/*  567 */         implName = BeanUtil.okNameForRegularGetter(m, m.getName(), this._stdBeanNaming);
/*      */       boolean visible;
/*  569 */       if (implName == null) {
/*  570 */         implName = BeanUtil.okNameForIsGetter(m, m.getName(), this._stdBeanNaming);
/*  571 */         if (implName == null) {
/*  572 */           return;
/*      */         }
/*  574 */         visible = this._visibilityChecker.isIsGetterVisible(m);
/*      */       } else {
/*  576 */         visible = this._visibilityChecker.isGetterVisible(m);
/*      */       }
/*      */     }
/*      */     else {
/*  580 */       implName = ai == null ? null : ai.findImplicitPropertyName(m);
/*  581 */       if (implName == null) {
/*  582 */         implName = BeanUtil.okNameForGetter(m, this._stdBeanNaming);
/*      */       }
/*      */       
/*  585 */       if (implName == null) {
/*  586 */         implName = m.getName();
/*      */       }
/*  588 */       if (pn.isEmpty())
/*      */       {
/*  590 */         pn = _propNameFromSimple(implName);
/*  591 */         nameExplicit = false;
/*      */       }
/*  593 */       visible = true;
/*      */     }
/*  595 */     boolean ignore = ai == null ? false : ai.hasIgnoreMarker(m);
/*  596 */     _property(props, implName).addGetter(m, pn, nameExplicit, visible, ignore);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _addSetterMethod(Map<String, POJOPropertyBuilder> props, AnnotatedMethod m, AnnotationIntrospector ai)
/*      */   {
/*  604 */     PropertyName pn = ai == null ? null : ai.findNameForDeserialization(m);
/*  605 */     boolean nameExplicit = pn != null;
/*  606 */     boolean visible; String implName; boolean visible; if (!nameExplicit) {
/*  607 */       String implName = ai == null ? null : ai.findImplicitPropertyName(m);
/*  608 */       if (implName == null) {
/*  609 */         implName = BeanUtil.okNameForMutator(m, this._mutatorPrefix, this._stdBeanNaming);
/*      */       }
/*  611 */       if (implName == null) {
/*  612 */         return;
/*      */       }
/*  614 */       visible = this._visibilityChecker.isSetterVisible(m);
/*      */     }
/*      */     else {
/*  617 */       implName = ai == null ? null : ai.findImplicitPropertyName(m);
/*  618 */       if (implName == null) {
/*  619 */         implName = BeanUtil.okNameForMutator(m, this._mutatorPrefix, this._stdBeanNaming);
/*      */       }
/*      */       
/*  622 */       if (implName == null) {
/*  623 */         implName = m.getName();
/*      */       }
/*  625 */       if (pn.isEmpty())
/*      */       {
/*  627 */         pn = _propNameFromSimple(implName);
/*  628 */         nameExplicit = false;
/*      */       }
/*  630 */       visible = true;
/*      */     }
/*  632 */     boolean ignore = ai == null ? false : ai.hasIgnoreMarker(m);
/*  633 */     _property(props, implName).addSetter(m, pn, nameExplicit, visible, ignore);
/*      */   }
/*      */   
/*      */   protected void _addInjectables(Map<String, POJOPropertyBuilder> props)
/*      */   {
/*  638 */     AnnotationIntrospector ai = this._annotationIntrospector;
/*  639 */     if (ai == null) {
/*  640 */       return;
/*      */     }
/*      */     
/*      */ 
/*  644 */     for (AnnotatedField f : this._classDef.fields()) {
/*  645 */       _doAddInjectable(ai.findInjectableValueId(f), f);
/*      */     }
/*      */     
/*  648 */     for (AnnotatedMethod m : this._classDef.memberMethods())
/*      */     {
/*      */ 
/*      */ 
/*  652 */       if (m.getParameterCount() == 1)
/*      */       {
/*      */ 
/*  655 */         _doAddInjectable(ai.findInjectableValueId(m), m);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void _doAddInjectable(Object id, AnnotatedMember m) {
/*  661 */     if (id == null) {
/*  662 */       return;
/*      */     }
/*  664 */     if (this._injectables == null) {
/*  665 */       this._injectables = new LinkedHashMap();
/*      */     }
/*  667 */     AnnotatedMember prev = (AnnotatedMember)this._injectables.put(id, m);
/*  668 */     if (prev != null) {
/*  669 */       String type = id.getClass().getName();
/*  670 */       throw new IllegalArgumentException("Duplicate injectable value with id '" + String.valueOf(id) + "' (of type " + type + ")");
/*      */     }
/*      */   }
/*      */   
/*      */   private PropertyName _propNameFromSimple(String simpleName)
/*      */   {
/*  676 */     return PropertyName.construct(simpleName, null);
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
/*      */   protected void _removeUnwantedProperties(Map<String, POJOPropertyBuilder> props)
/*      */   {
/*  691 */     Iterator<POJOPropertyBuilder> it = props.values().iterator();
/*      */     
/*  693 */     while (it.hasNext()) {
/*  694 */       POJOPropertyBuilder prop = (POJOPropertyBuilder)it.next();
/*      */       
/*      */ 
/*  697 */       if (!prop.anyVisible()) {
/*  698 */         it.remove();
/*      */ 
/*      */ 
/*      */       }
/*  702 */       else if (prop.anyIgnorals())
/*      */       {
/*  704 */         if (!prop.isExplicitlyIncluded()) {
/*  705 */           it.remove();
/*  706 */           _collectIgnorals(prop.getName());
/*      */         }
/*      */         else
/*      */         {
/*  710 */           prop.removeIgnored();
/*  711 */           if ((!this._forSerialization) && (!prop.couldDeserialize())) {
/*  712 */             _collectIgnorals(prop.getName());
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _removeUnwantedAccessor(Map<String, POJOPropertyBuilder> props)
/*      */   {
/*  725 */     boolean inferMutators = this._config.isEnabled(MapperFeature.INFER_PROPERTY_MUTATORS);
/*  726 */     Iterator<POJOPropertyBuilder> it = props.values().iterator();
/*      */     
/*  728 */     while (it.hasNext()) {
/*  729 */       POJOPropertyBuilder prop = (POJOPropertyBuilder)it.next();
/*  730 */       prop.removeNonVisible(inferMutators);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void _collectIgnorals(String name)
/*      */   {
/*  741 */     if (!this._forSerialization) {
/*  742 */       if (this._ignoredPropertyNames == null) {
/*  743 */         this._ignoredPropertyNames = new HashSet();
/*      */       }
/*  745 */       this._ignoredPropertyNames.add(name);
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
/*      */   protected void _renameProperties(Map<String, POJOPropertyBuilder> props)
/*      */   {
/*  758 */     Iterator<Entry<String, POJOPropertyBuilder>> it = props.entrySet().iterator();
/*  759 */     LinkedList<POJOPropertyBuilder> renamed = null;
/*  760 */     while (it.hasNext()) {
/*  761 */       Entry<String, POJOPropertyBuilder> entry = (Entry)it.next();
/*  762 */       POJOPropertyBuilder prop = (POJOPropertyBuilder)entry.getValue();
/*      */       
/*  764 */       Collection<PropertyName> l = prop.findExplicitNames();
/*      */       
/*      */ 
/*  767 */       if (!l.isEmpty())
/*      */       {
/*      */ 
/*  770 */         it.remove();
/*  771 */         if (renamed == null) {
/*  772 */           renamed = new LinkedList();
/*      */         }
/*      */         
/*  775 */         if (l.size() == 1) {
/*  776 */           PropertyName n = (PropertyName)l.iterator().next();
/*  777 */           renamed.add(prop.withName(n));
/*      */         }
/*      */         else
/*      */         {
/*  781 */           renamed.addAll(prop.explode(l));
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
/*      */ 
/*      */ 
/*  797 */     if (renamed != null) {
/*  798 */       for (POJOPropertyBuilder prop : renamed) {
/*  799 */         String name = prop.getName();
/*  800 */         POJOPropertyBuilder old = (POJOPropertyBuilder)props.get(name);
/*  801 */         if (old == null) {
/*  802 */           props.put(name, prop);
/*      */         } else {
/*  804 */           old.addAll(prop);
/*      */         }
/*      */         
/*  807 */         _updateCreatorProperty(prop, this._creatorProperties);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void _renameUsing(Map<String, POJOPropertyBuilder> propMap, PropertyNamingStrategy naming)
/*      */   {
/*  815 */     POJOPropertyBuilder[] props = (POJOPropertyBuilder[])propMap.values().toArray(new POJOPropertyBuilder[propMap.size()]);
/*  816 */     propMap.clear();
/*  817 */     for (POJOPropertyBuilder prop : props) {
/*  818 */       PropertyName fullName = prop.getFullName();
/*  819 */       String rename = null;
/*      */       
/*      */ 
/*  822 */       if ((!prop.isExplicitlyNamed()) || (this._config.isEnabled(MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING))) {
/*  823 */         if (this._forSerialization) {
/*  824 */           if (prop.hasGetter()) {
/*  825 */             rename = naming.nameForGetterMethod(this._config, prop.getGetter(), fullName.getSimpleName());
/*  826 */           } else if (prop.hasField()) {
/*  827 */             rename = naming.nameForField(this._config, prop.getField(), fullName.getSimpleName());
/*      */           }
/*      */         }
/*  830 */         else if (prop.hasSetter()) {
/*  831 */           rename = naming.nameForSetterMethod(this._config, prop.getSetter(), fullName.getSimpleName());
/*  832 */         } else if (prop.hasConstructorParameter()) {
/*  833 */           rename = naming.nameForConstructorParameter(this._config, prop.getConstructorParameter(), fullName.getSimpleName());
/*  834 */         } else if (prop.hasField()) {
/*  835 */           rename = naming.nameForField(this._config, prop.getField(), fullName.getSimpleName());
/*  836 */         } else if (prop.hasGetter())
/*      */         {
/*      */ 
/*      */ 
/*  840 */           rename = naming.nameForGetterMethod(this._config, prop.getGetter(), fullName.getSimpleName());
/*      */         }
/*      */       }
/*      */       String simpleName;
/*      */       String simpleName;
/*  845 */       if ((rename != null) && (!fullName.hasSimpleName(rename))) {
/*  846 */         prop = prop.withSimpleName(rename);
/*  847 */         simpleName = rename;
/*      */       } else {
/*  849 */         simpleName = fullName.getSimpleName();
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*  854 */       POJOPropertyBuilder old = (POJOPropertyBuilder)propMap.get(simpleName);
/*  855 */       if (old == null) {
/*  856 */         propMap.put(simpleName, prop);
/*      */       } else {
/*  858 */         old.addAll(prop);
/*      */       }
/*      */       
/*  861 */       _updateCreatorProperty(prop, this._creatorProperties);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _renameWithWrappers(Map<String, POJOPropertyBuilder> props)
/*      */   {
/*  870 */     Iterator<Entry<String, POJOPropertyBuilder>> it = props.entrySet().iterator();
/*  871 */     LinkedList<POJOPropertyBuilder> renamed = null;
/*  872 */     while (it.hasNext()) {
/*  873 */       Entry<String, POJOPropertyBuilder> entry = (Entry)it.next();
/*  874 */       POJOPropertyBuilder prop = (POJOPropertyBuilder)entry.getValue();
/*  875 */       AnnotatedMember member = prop.getPrimaryMember();
/*  876 */       if (member != null)
/*      */       {
/*      */ 
/*  879 */         PropertyName wrapperName = this._annotationIntrospector.findWrapperName(member);
/*      */         
/*      */ 
/*      */ 
/*  883 */         if ((wrapperName != null) && (wrapperName.hasSimpleName()))
/*      */         {
/*      */ 
/*  886 */           if (!wrapperName.equals(prop.getFullName())) {
/*  887 */             if (renamed == null) {
/*  888 */               renamed = new LinkedList();
/*      */             }
/*  890 */             prop = prop.withName(wrapperName);
/*  891 */             renamed.add(prop);
/*  892 */             it.remove();
/*      */           } }
/*      */       }
/*      */     }
/*  896 */     if (renamed != null) {
/*  897 */       for (POJOPropertyBuilder prop : renamed) {
/*  898 */         String name = prop.getName();
/*  899 */         POJOPropertyBuilder old = (POJOPropertyBuilder)props.get(name);
/*  900 */         if (old == null) {
/*  901 */           props.put(name, prop);
/*      */         } else {
/*  903 */           old.addAll(prop);
/*      */         }
/*      */       }
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
/*      */ 
/*      */ 
/*      */   protected void _sortProperties(Map<String, POJOPropertyBuilder> props)
/*      */   {
/*  921 */     AnnotationIntrospector intr = this._annotationIntrospector;
/*      */     
/*  923 */     Boolean alpha = intr == null ? null : intr.findSerializationSortAlphabetically(this._classDef);
/*      */     boolean sort;
/*  925 */     boolean sort; if (alpha == null) {
/*  926 */       sort = this._config.shouldSortPropertiesAlphabetically();
/*      */     } else {
/*  928 */       sort = alpha.booleanValue();
/*      */     }
/*  930 */     String[] propertyOrder = intr == null ? null : intr.findSerializationPropertyOrder(this._classDef);
/*      */     
/*      */ 
/*  933 */     if ((!sort) && (this._creatorProperties == null) && (propertyOrder == null)) {
/*  934 */       return;
/*      */     }
/*  936 */     int size = props.size();
/*      */     Map<String, POJOPropertyBuilder> all;
/*      */     Map<String, POJOPropertyBuilder> all;
/*  939 */     if (sort) {
/*  940 */       all = new TreeMap();
/*      */     } else {
/*  942 */       all = new LinkedHashMap(size + size);
/*      */     }
/*      */     
/*  945 */     for (POJOPropertyBuilder prop : props.values()) {
/*  946 */       all.put(prop.getName(), prop);
/*      */     }
/*  948 */     Map<String, POJOPropertyBuilder> ordered = new LinkedHashMap(size + size);
/*      */     
/*  950 */     if (propertyOrder != null) {
/*  951 */       for (String name : propertyOrder) {
/*  952 */         POJOPropertyBuilder w = (POJOPropertyBuilder)all.get(name);
/*  953 */         if (w == null) {
/*  954 */           for (POJOPropertyBuilder prop : props.values()) {
/*  955 */             if (name.equals(prop.getInternalName())) {
/*  956 */               w = prop;
/*      */               
/*  958 */               name = prop.getName();
/*  959 */               break;
/*      */             }
/*      */           }
/*      */         }
/*  963 */         if (w != null) {
/*  964 */           ordered.put(name, w);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  969 */     if (this._creatorProperties != null)
/*      */     {
/*      */       Collection<POJOPropertyBuilder> cr;
/*      */       
/*      */ 
/*      */       Collection<POJOPropertyBuilder> cr;
/*      */       
/*  976 */       if (sort) {
/*  977 */         TreeMap<String, POJOPropertyBuilder> sorted = new TreeMap();
/*      */         
/*  979 */         for (POJOPropertyBuilder prop : this._creatorProperties) {
/*  980 */           sorted.put(prop.getName(), prop);
/*      */         }
/*  982 */         cr = sorted.values();
/*      */       } else {
/*  984 */         cr = this._creatorProperties;
/*      */       }
/*  986 */       for (POJOPropertyBuilder prop : cr) {
/*  987 */         ordered.put(prop.getName(), prop);
/*      */       }
/*      */     }
/*      */     
/*  991 */     ordered.putAll(all);
/*      */     
/*  993 */     props.clear();
/*  994 */     props.putAll(ordered);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void reportProblem(String msg)
/*      */   {
/* 1004 */     throw new IllegalArgumentException("Problem with definition of " + this._classDef + ": " + msg);
/*      */   }
/*      */   
/*      */   protected POJOPropertyBuilder _property(Map<String, POJOPropertyBuilder> props, PropertyName name)
/*      */   {
/* 1009 */     return _property(props, name.getSimpleName());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected POJOPropertyBuilder _property(Map<String, POJOPropertyBuilder> props, String implName)
/*      */   {
/* 1016 */     POJOPropertyBuilder prop = (POJOPropertyBuilder)props.get(implName);
/* 1017 */     if (prop == null) {
/* 1018 */       prop = new POJOPropertyBuilder(this._config, this._annotationIntrospector, this._forSerialization, PropertyName.construct(implName));
/*      */       
/* 1020 */       props.put(implName, prop);
/*      */     }
/* 1022 */     return prop;
/*      */   }
/*      */   
/*      */   private PropertyNamingStrategy _findNamingStrategy()
/*      */   {
/* 1027 */     Object namingDef = this._annotationIntrospector == null ? null : this._annotationIntrospector.findNamingStrategy(this._classDef);
/*      */     
/* 1029 */     if (namingDef == null) {
/* 1030 */       return this._config.getPropertyNamingStrategy();
/*      */     }
/* 1032 */     if ((namingDef instanceof PropertyNamingStrategy)) {
/* 1033 */       return (PropertyNamingStrategy)namingDef;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/* 1038 */     if (!(namingDef instanceof Class)) {
/* 1039 */       throw new IllegalStateException("AnnotationIntrospector returned PropertyNamingStrategy definition of type " + namingDef.getClass().getName() + "; expected type PropertyNamingStrategy or Class<PropertyNamingStrategy> instead");
/*      */     }
/*      */     
/* 1042 */     Class<?> namingClass = (Class)namingDef;
/*      */     
/* 1044 */     if (namingClass == PropertyNamingStrategy.class) {
/* 1045 */       return null;
/*      */     }
/*      */     
/* 1048 */     if (!PropertyNamingStrategy.class.isAssignableFrom(namingClass)) {
/* 1049 */       throw new IllegalStateException("AnnotationIntrospector returned Class " + namingClass.getName() + "; expected Class<PropertyNamingStrategy>");
/*      */     }
/*      */     
/* 1052 */     HandlerInstantiator hi = this._config.getHandlerInstantiator();
/* 1053 */     if (hi != null) {
/* 1054 */       PropertyNamingStrategy pns = hi.namingStrategyInstance(this._config, this._classDef, namingClass);
/* 1055 */       if (pns != null) {
/* 1056 */         return pns;
/*      */       }
/*      */     }
/* 1059 */     return (PropertyNamingStrategy)ClassUtil.createInstance(namingClass, this._config.canOverrideAccessModifiers());
/*      */   }
/*      */   
/*      */   protected void _updateCreatorProperty(POJOPropertyBuilder prop, List<POJOPropertyBuilder> creatorProperties)
/*      */   {
/* 1064 */     if (creatorProperties != null) {
/* 1065 */       int i = 0; for (int len = creatorProperties.size(); i < len; i++) {
/* 1066 */         if (((POJOPropertyBuilder)creatorProperties.get(i)).getInternalName().equals(prop.getInternalName())) {
/* 1067 */           creatorProperties.set(i, prop);
/* 1068 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\introspect\POJOPropertiesCollector.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */