/*      */ package com.facebook.presto.jdbc.internal.jackson.databind.introspect;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.AnnotationIntrospector;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.MapperConfig;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeBindings;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.type.TypeFactory;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil.Ctor;
/*      */ import java.lang.annotation.Annotation;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ 
/*      */ public final class AnnotatedClass extends Annotated implements TypeResolutionContext
/*      */ {
/*   22 */   private static final AnnotationMap[] NO_ANNOTATION_MAPS = new AnnotationMap[0];
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final JavaType _type;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final Class<?> _class;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final TypeBindings _bindings;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final List<JavaType> _superTypes;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final AnnotationIntrospector _annotationIntrospector;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final TypeFactory _typeFactory;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final ClassIntrospector.MixInResolver _mixInResolver;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final Class<?> _primaryMixIn;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected AnnotationMap _classAnnotations;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   97 */   protected boolean _creatorsResolved = false;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected AnnotatedConstructor _defaultConstructor;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected List<AnnotatedConstructor> _constructors;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected List<AnnotatedMethod> _creatorMethods;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected AnnotatedMethodMap _memberMethods;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected List<AnnotatedField> _fields;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private AnnotatedClass(JavaType type, Class<?> rawType, TypeBindings bindings, List<JavaType> superTypes, AnnotationIntrospector aintr, ClassIntrospector.MixInResolver mir, TypeFactory tf, AnnotationMap classAnnotations)
/*      */   {
/*  142 */     this._type = type;
/*  143 */     this._class = rawType;
/*  144 */     this._bindings = bindings;
/*  145 */     this._superTypes = superTypes;
/*  146 */     this._annotationIntrospector = aintr;
/*  147 */     this._typeFactory = tf;
/*  148 */     this._mixInResolver = mir;
/*  149 */     this._primaryMixIn = (this._mixInResolver == null ? null : this._mixInResolver.findMixInClassFor(this._class));
/*      */     
/*  151 */     this._classAnnotations = classAnnotations;
/*      */   }
/*      */   
/*      */   public AnnotatedClass withAnnotations(AnnotationMap ann)
/*      */   {
/*  156 */     return new AnnotatedClass(this._type, this._class, this._bindings, this._superTypes, this._annotationIntrospector, this._mixInResolver, this._typeFactory, ann);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static AnnotatedClass construct(JavaType type, MapperConfig<?> config)
/*      */   {
/*  168 */     AnnotationIntrospector intr = config.isAnnotationProcessingEnabled() ? config.getAnnotationIntrospector() : null;
/*      */     
/*  170 */     Class<?> raw = type.getRawClass();
/*  171 */     return new AnnotatedClass(type, raw, type.getBindings(), ClassUtil.findSuperTypes(type, null, false), intr, config, config.getTypeFactory(), null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static AnnotatedClass construct(JavaType type, MapperConfig<?> config, ClassIntrospector.MixInResolver mir)
/*      */   {
/*  182 */     AnnotationIntrospector intr = config.isAnnotationProcessingEnabled() ? config.getAnnotationIntrospector() : null;
/*      */     
/*  184 */     Class<?> raw = type.getRawClass();
/*  185 */     return new AnnotatedClass(type, raw, type.getBindings(), ClassUtil.findSuperTypes(type, null, false), intr, mir, config.getTypeFactory(), null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static AnnotatedClass constructWithoutSuperTypes(Class<?> cls, MapperConfig<?> config)
/*      */   {
/*  197 */     if (config == null) {
/*  198 */       return new AnnotatedClass(null, cls, TypeBindings.emptyBindings(), Collections.emptyList(), null, null, null, null);
/*      */     }
/*      */     
/*  201 */     AnnotationIntrospector intr = config.isAnnotationProcessingEnabled() ? config.getAnnotationIntrospector() : null;
/*      */     
/*  203 */     return new AnnotatedClass(null, cls, TypeBindings.emptyBindings(), Collections.emptyList(), intr, config, config.getTypeFactory(), null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static AnnotatedClass constructWithoutSuperTypes(Class<?> cls, MapperConfig<?> config, ClassIntrospector.MixInResolver mir)
/*      */   {
/*  210 */     if (config == null) {
/*  211 */       return new AnnotatedClass(null, cls, TypeBindings.emptyBindings(), Collections.emptyList(), null, null, null, null);
/*      */     }
/*      */     
/*  214 */     AnnotationIntrospector intr = config.isAnnotationProcessingEnabled() ? config.getAnnotationIntrospector() : null;
/*      */     
/*  216 */     return new AnnotatedClass(null, cls, TypeBindings.emptyBindings(), Collections.emptyList(), intr, mir, config.getTypeFactory(), null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JavaType resolveType(java.lang.reflect.Type type)
/*      */   {
/*  228 */     return this._typeFactory.constructType(type, this._bindings);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Class<?> getAnnotated()
/*      */   {
/*  238 */     return this._class;
/*      */   }
/*      */   
/*  241 */   public int getModifiers() { return this._class.getModifiers(); }
/*      */   
/*      */   public String getName() {
/*  244 */     return this._class.getName();
/*      */   }
/*      */   
/*      */   public <A extends Annotation> A getAnnotation(Class<A> acls) {
/*  248 */     return _classAnnotations().get(acls);
/*      */   }
/*      */   
/*      */   public boolean hasAnnotation(Class<?> acls)
/*      */   {
/*  253 */     return _classAnnotations().has(acls);
/*      */   }
/*      */   
/*      */   public boolean hasOneOf(Class<? extends Annotation>[] annoClasses)
/*      */   {
/*  258 */     return _classAnnotations().hasOneOf(annoClasses);
/*      */   }
/*      */   
/*      */   public Class<?> getRawType()
/*      */   {
/*  263 */     return this._class;
/*      */   }
/*      */   
/*      */   public Iterable<Annotation> annotations()
/*      */   {
/*  268 */     return _classAnnotations().annotations();
/*      */   }
/*      */   
/*      */   protected AnnotationMap getAllAnnotations()
/*      */   {
/*  273 */     return _classAnnotations();
/*      */   }
/*      */   
/*      */   public JavaType getType()
/*      */   {
/*  278 */     return this._type;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public com.facebook.presto.jdbc.internal.jackson.databind.util.Annotations getAnnotations()
/*      */   {
/*  288 */     return _classAnnotations();
/*      */   }
/*      */   
/*      */   public boolean hasAnnotations() {
/*  292 */     return _classAnnotations().size() > 0;
/*      */   }
/*      */   
/*      */   public AnnotatedConstructor getDefaultConstructor()
/*      */   {
/*  297 */     if (!this._creatorsResolved) {
/*  298 */       resolveCreators();
/*      */     }
/*  300 */     return this._defaultConstructor;
/*      */   }
/*      */   
/*      */   public List<AnnotatedConstructor> getConstructors()
/*      */   {
/*  305 */     if (!this._creatorsResolved) {
/*  306 */       resolveCreators();
/*      */     }
/*  308 */     return this._constructors;
/*      */   }
/*      */   
/*      */   public List<AnnotatedMethod> getStaticMethods()
/*      */   {
/*  313 */     if (!this._creatorsResolved) {
/*  314 */       resolveCreators();
/*      */     }
/*  316 */     return this._creatorMethods;
/*      */   }
/*      */   
/*      */   public Iterable<AnnotatedMethod> memberMethods()
/*      */   {
/*  321 */     if (this._memberMethods == null) {
/*  322 */       resolveMemberMethods();
/*      */     }
/*  324 */     return this._memberMethods;
/*      */   }
/*      */   
/*      */   public int getMemberMethodCount()
/*      */   {
/*  329 */     if (this._memberMethods == null) {
/*  330 */       resolveMemberMethods();
/*      */     }
/*  332 */     return this._memberMethods.size();
/*      */   }
/*      */   
/*      */   public AnnotatedMethod findMethod(String name, Class<?>[] paramTypes)
/*      */   {
/*  337 */     if (this._memberMethods == null) {
/*  338 */       resolveMemberMethods();
/*      */     }
/*  340 */     return this._memberMethods.find(name, paramTypes);
/*      */   }
/*      */   
/*      */   public int getFieldCount() {
/*  344 */     if (this._fields == null) {
/*  345 */       resolveFields();
/*      */     }
/*  347 */     return this._fields.size();
/*      */   }
/*      */   
/*      */   public Iterable<AnnotatedField> fields()
/*      */   {
/*  352 */     if (this._fields == null) {
/*  353 */       resolveFields();
/*      */     }
/*  355 */     return this._fields;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private AnnotationMap _classAnnotations()
/*      */   {
/*  365 */     AnnotationMap anns = this._classAnnotations;
/*  366 */     if (anns == null)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  373 */       synchronized (this) {
/*  374 */         anns = this._classAnnotations;
/*  375 */         if (anns == null) {
/*  376 */           anns = _resolveClassAnnotations();
/*  377 */           this._classAnnotations = anns;
/*      */         }
/*      */       }
/*      */     }
/*  381 */     return anns;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private AnnotationMap _resolveClassAnnotations()
/*      */   {
/*  391 */     AnnotationMap ca = new AnnotationMap();
/*      */     
/*  393 */     if (this._annotationIntrospector != null)
/*      */     {
/*  395 */       if (this._primaryMixIn != null) {
/*  396 */         _addClassMixIns(ca, this._class, this._primaryMixIn);
/*      */       }
/*      */       
/*  399 */       _addAnnotationsIfNotPresent(ca, ClassUtil.findClassAnnotations(this._class));
/*      */       
/*      */ 
/*      */ 
/*  403 */       for (JavaType type : this._superTypes)
/*      */       {
/*  405 */         _addClassMixIns(ca, type);
/*  406 */         _addAnnotationsIfNotPresent(ca, ClassUtil.findClassAnnotations(type.getRawClass()));
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  416 */       _addClassMixIns(ca, Object.class);
/*      */     }
/*  418 */     return ca;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void resolveCreators()
/*      */   {
/*  428 */     TypeResolutionContext typeContext = this;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  435 */     List<AnnotatedConstructor> constructors = null;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  442 */     if (!this._type.isEnumType()) {
/*  443 */       Ctor[] declaredCtors = ClassUtil.getConstructors(this._class);
/*  444 */       for (Ctor ctor : declaredCtors) {
/*  445 */         if (_isIncludableConstructor(ctor.getConstructor())) {
/*  446 */           if (ctor.getParamCount() == 0) {
/*  447 */             this._defaultConstructor = _constructDefaultConstructor(ctor, typeContext);
/*      */           } else {
/*  449 */             if (constructors == null) {
/*  450 */               constructors = new ArrayList(Math.max(10, declaredCtors.length));
/*      */             }
/*  452 */             constructors.add(_constructNonDefaultConstructor(ctor, typeContext));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  457 */     if (constructors == null) {
/*  458 */       this._constructors = Collections.emptyList();
/*      */     } else {
/*  460 */       this._constructors = constructors;
/*      */     }
/*      */     
/*  463 */     if ((this._primaryMixIn != null) && (
/*  464 */       (this._defaultConstructor != null) || (!this._constructors.isEmpty()))) {
/*  465 */       _addConstructorMixIns(this._primaryMixIn);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  473 */     if (this._annotationIntrospector != null) {
/*  474 */       if ((this._defaultConstructor != null) && 
/*  475 */         (this._annotationIntrospector.hasIgnoreMarker(this._defaultConstructor))) {
/*  476 */         this._defaultConstructor = null;
/*      */       }
/*      */       
/*  479 */       if (this._constructors != null)
/*      */       {
/*  481 */         int i = this._constructors.size(); for (;;) { i--; if (i < 0) break;
/*  482 */           if (this._annotationIntrospector.hasIgnoreMarker((AnnotatedMember)this._constructors.get(i))) {
/*  483 */             this._constructors.remove(i);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  488 */     List<AnnotatedMethod> creatorMethods = null;
/*      */     
/*      */ 
/*  491 */     for (Method m : _findClassMethods(this._class))
/*  492 */       if (Modifier.isStatic(m.getModifiers()))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*  497 */         if (creatorMethods == null) {
/*  498 */           creatorMethods = new ArrayList(8);
/*      */         }
/*  500 */         creatorMethods.add(_constructCreatorMethod(m, typeContext));
/*      */       }
/*  502 */     if (creatorMethods == null) {
/*  503 */       this._creatorMethods = Collections.emptyList();
/*      */     } else {
/*  505 */       this._creatorMethods = creatorMethods;
/*      */       
/*  507 */       if (this._primaryMixIn != null) {
/*  508 */         _addFactoryMixIns(this._primaryMixIn);
/*      */       }
/*      */       
/*  511 */       if (this._annotationIntrospector != null)
/*      */       {
/*  513 */         int i = this._creatorMethods.size(); for (;;) { i--; if (i < 0) break;
/*  514 */           if (this._annotationIntrospector.hasIgnoreMarker((AnnotatedMember)this._creatorMethods.get(i))) {
/*  515 */             this._creatorMethods.remove(i);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  520 */     this._creatorsResolved = true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void resolveMemberMethods()
/*      */   {
/*  531 */     this._memberMethods = new AnnotatedMethodMap();
/*  532 */     AnnotatedMethodMap mixins = new AnnotatedMethodMap();
/*      */     
/*  534 */     _addMemberMethods(this._class, this, this._memberMethods, this._primaryMixIn, mixins);
/*      */     
/*      */ 
/*  537 */     for (JavaType type : this._superTypes) {
/*  538 */       Class<?> mixin = this._mixInResolver == null ? null : this._mixInResolver.findMixInClassFor(type.getRawClass());
/*  539 */       _addMemberMethods(type.getRawClass(), new TypeResolutionContext.Basic(this._typeFactory, type.getBindings()), this._memberMethods, mixin, mixins);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  544 */     if (this._mixInResolver != null) {
/*  545 */       Class<?> mixin = this._mixInResolver.findMixInClassFor(Object.class);
/*  546 */       if (mixin != null) {
/*  547 */         _addMethodMixIns(this._class, this._memberMethods, mixin, mixins);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  557 */     if ((this._annotationIntrospector != null) && 
/*  558 */       (!mixins.isEmpty())) {
/*  559 */       java.util.Iterator<AnnotatedMethod> it = mixins.iterator();
/*  560 */       while (it.hasNext()) {
/*  561 */         AnnotatedMethod mixIn = (AnnotatedMethod)it.next();
/*      */         try {
/*  563 */           Method m = Object.class.getDeclaredMethod(mixIn.getName(), mixIn.getRawParameterTypes());
/*  564 */           if (m != null)
/*      */           {
/*  566 */             AnnotatedMethod am = _constructMethod(m, this);
/*  567 */             _addMixOvers(mixIn.getAnnotated(), am, false);
/*  568 */             this._memberMethods.add(am);
/*      */           }
/*      */         }
/*      */         catch (Exception e) {}
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void resolveFields()
/*      */   {
/*  583 */     Map<String, AnnotatedField> foundFields = _findFields(this._type, this, null);
/*  584 */     if ((foundFields == null) || (foundFields.size() == 0)) {
/*  585 */       this._fields = Collections.emptyList();
/*      */     } else {
/*  587 */       this._fields = new ArrayList(foundFields.size());
/*  588 */       this._fields.addAll(foundFields.values());
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
/*      */ 
/*      */ 
/*      */   protected void _addClassMixIns(AnnotationMap annotations, JavaType target)
/*      */   {
/*  606 */     if (this._mixInResolver != null) {
/*  607 */       Class<?> toMask = target.getRawClass();
/*  608 */       _addClassMixIns(annotations, toMask, this._mixInResolver.findMixInClassFor(toMask));
/*      */     }
/*      */   }
/*      */   
/*      */   protected void _addClassMixIns(AnnotationMap annotations, Class<?> target)
/*      */   {
/*  614 */     if (this._mixInResolver != null) {
/*  615 */       _addClassMixIns(annotations, target, this._mixInResolver.findMixInClassFor(target));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void _addClassMixIns(AnnotationMap annotations, Class<?> toMask, Class<?> mixin)
/*      */   {
/*  622 */     if (mixin == null) {
/*  623 */       return;
/*      */     }
/*      */     
/*  626 */     _addAnnotationsIfNotPresent(annotations, ClassUtil.findClassAnnotations(mixin));
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  635 */     for (Class<?> parent : ClassUtil.findSuperClasses(mixin, toMask, false)) {
/*  636 */       _addAnnotationsIfNotPresent(annotations, ClassUtil.findClassAnnotations(parent));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _addConstructorMixIns(Class<?> mixin)
/*      */   {
/*  648 */     MemberKey[] ctorKeys = null;
/*  649 */     int ctorCount = this._constructors == null ? 0 : this._constructors.size();
/*  650 */     for (Ctor ctor0 : ClassUtil.getConstructors(mixin)) {
/*  651 */       Constructor<?> ctor = ctor0.getConstructor();
/*  652 */       if (ctor.getParameterTypes().length == 0) {
/*  653 */         if (this._defaultConstructor != null) {
/*  654 */           _addMixOvers(ctor, this._defaultConstructor, false);
/*      */         }
/*      */       } else {
/*  657 */         if (ctorKeys == null) {
/*  658 */           ctorKeys = new MemberKey[ctorCount];
/*  659 */           for (int i = 0; i < ctorCount; i++) {
/*  660 */             ctorKeys[i] = new MemberKey(((AnnotatedConstructor)this._constructors.get(i)).getAnnotated());
/*      */           }
/*      */         }
/*  663 */         MemberKey key = new MemberKey(ctor);
/*      */         
/*  665 */         for (int i = 0; i < ctorCount; i++) {
/*  666 */           if (key.equals(ctorKeys[i]))
/*      */           {
/*      */ 
/*  669 */             _addMixOvers(ctor, (AnnotatedConstructor)this._constructors.get(i), true);
/*  670 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   protected void _addFactoryMixIns(Class<?> mixin) {
/*  678 */     MemberKey[] methodKeys = null;
/*  679 */     int methodCount = this._creatorMethods.size();
/*      */     
/*  681 */     for (Method m : ClassUtil.getDeclaredMethods(mixin)) {
/*  682 */       if (Modifier.isStatic(m.getModifiers()))
/*      */       {
/*      */ 
/*  685 */         if (m.getParameterTypes().length != 0)
/*      */         {
/*      */ 
/*  688 */           if (methodKeys == null) {
/*  689 */             methodKeys = new MemberKey[methodCount];
/*  690 */             for (int i = 0; i < methodCount; i++) {
/*  691 */               methodKeys[i] = new MemberKey(((AnnotatedMethod)this._creatorMethods.get(i)).getAnnotated());
/*      */             }
/*      */           }
/*  694 */           MemberKey key = new MemberKey(m);
/*  695 */           for (int i = 0; i < methodCount; i++) {
/*  696 */             if (key.equals(methodKeys[i]))
/*      */             {
/*      */ 
/*  699 */               _addMixOvers(m, (AnnotatedMethod)this._creatorMethods.get(i), true);
/*  700 */               break;
/*      */             }
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
/*      */ 
/*      */ 
/*      */   protected void _addMemberMethods(Class<?> cls, TypeResolutionContext typeContext, AnnotatedMethodMap methods, Class<?> mixInCls, AnnotatedMethodMap mixIns)
/*      */   {
/*  716 */     if (mixInCls != null) {
/*  717 */       _addMethodMixIns(cls, methods, mixInCls, mixIns);
/*      */     }
/*  719 */     if (cls == null) {
/*  720 */       return;
/*      */     }
/*      */     
/*  723 */     for (Method m : _findClassMethods(cls)) {
/*  724 */       if (_isIncludableMemberMethod(m))
/*      */       {
/*      */ 
/*  727 */         AnnotatedMethod old = methods.find(m);
/*  728 */         if (old == null) {
/*  729 */           AnnotatedMethod newM = _constructMethod(m, typeContext);
/*  730 */           methods.add(newM);
/*      */           
/*  732 */           old = mixIns.remove(m);
/*  733 */           if (old != null) {
/*  734 */             _addMixOvers(old.getAnnotated(), newM, false);
/*      */           }
/*      */           
/*      */         }
/*      */         else
/*      */         {
/*  740 */           _addMixUnders(m, old);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  749 */           if ((old.getDeclaringClass().isInterface()) && (!m.getDeclaringClass().isInterface())) {
/*  750 */             methods.add(old.withMethod(m));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void _addMethodMixIns(Class<?> targetClass, AnnotatedMethodMap methods, Class<?> mixInCls, AnnotatedMethodMap mixIns)
/*      */   {
/*  761 */     List<Class<?>> parents = ClassUtil.findRawSuperTypes(mixInCls, targetClass, true);
/*  762 */     for (Class<?> mixin : parents) {
/*  763 */       for (Method m : ClassUtil.getDeclaredMethods(mixin)) {
/*  764 */         if (_isIncludableMemberMethod(m))
/*      */         {
/*      */ 
/*  767 */           AnnotatedMethod am = methods.find(m);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  772 */           if (am != null) {
/*  773 */             _addMixUnders(m, am);
/*      */ 
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/*  780 */             am = mixIns.find(m);
/*  781 */             if (am != null) {
/*  782 */               _addMixUnders(m, am);
/*      */             }
/*      */             else
/*      */             {
/*  786 */               mixIns.add(_constructMethod(m, this));
/*      */             }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Map<String, AnnotatedField> _findFields(JavaType type, TypeResolutionContext typeContext, Map<String, AnnotatedField> fields)
/*      */   {
/*  807 */     JavaType parent = type.getSuperClass();
/*  808 */     if (parent != null) {
/*  809 */       Class<?> cls = type.getRawClass();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  814 */       fields = _findFields(parent, new TypeResolutionContext.Basic(this._typeFactory, parent.getBindings()), fields);
/*      */       
/*      */ 
/*  817 */       for (Field f : ClassUtil.getDeclaredFields(cls))
/*      */       {
/*  819 */         if (_isIncludableField(f))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  827 */           if (fields == null) {
/*  828 */             fields = new java.util.LinkedHashMap();
/*      */           }
/*  830 */           fields.put(f.getName(), _constructField(f, typeContext));
/*      */         }
/*      */       }
/*  833 */       if (this._mixInResolver != null) {
/*  834 */         Class<?> mixin = this._mixInResolver.findMixInClassFor(cls);
/*  835 */         if (mixin != null) {
/*  836 */           _addFieldMixIns(mixin, cls, fields);
/*      */         }
/*      */       }
/*      */     }
/*  840 */     return fields;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _addFieldMixIns(Class<?> mixInCls, Class<?> targetClass, Map<String, AnnotatedField> fields)
/*      */   {
/*  851 */     List<Class<?>> parents = ClassUtil.findSuperClasses(mixInCls, targetClass, true);
/*  852 */     for (Class<?> mixin : parents) {
/*  853 */       for (Field mixinField : ClassUtil.getDeclaredFields(mixin))
/*      */       {
/*  855 */         if (_isIncludableField(mixinField))
/*      */         {
/*      */ 
/*  858 */           String name = mixinField.getName();
/*      */           
/*  860 */           AnnotatedField maskedField = (AnnotatedField)fields.get(name);
/*  861 */           if (maskedField != null) {
/*  862 */             _addOrOverrideAnnotations(maskedField, mixinField.getDeclaredAnnotations());
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected AnnotatedMethod _constructMethod(Method m, TypeResolutionContext typeContext)
/*      */   {
/*  880 */     if (this._annotationIntrospector == null) {
/*  881 */       return new AnnotatedMethod(typeContext, m, _emptyAnnotationMap(), null);
/*      */     }
/*  883 */     return new AnnotatedMethod(typeContext, m, _collectRelevantAnnotations(m.getDeclaredAnnotations()), null);
/*      */   }
/*      */   
/*      */ 
/*      */   protected AnnotatedConstructor _constructDefaultConstructor(Ctor ctor, TypeResolutionContext typeContext)
/*      */   {
/*  889 */     if (this._annotationIntrospector == null) {
/*  890 */       return new AnnotatedConstructor(typeContext, ctor.getConstructor(), _emptyAnnotationMap(), NO_ANNOTATION_MAPS);
/*      */     }
/*  892 */     return new AnnotatedConstructor(typeContext, ctor.getConstructor(), _collectRelevantAnnotations(ctor.getDeclaredAnnotations()), NO_ANNOTATION_MAPS);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected AnnotatedConstructor _constructNonDefaultConstructor(Ctor ctor, TypeResolutionContext typeContext)
/*      */   {
/*  899 */     int paramCount = ctor.getParamCount();
/*  900 */     if (this._annotationIntrospector == null) {
/*  901 */       return new AnnotatedConstructor(typeContext, ctor.getConstructor(), _emptyAnnotationMap(), _emptyAnnotationMaps(paramCount));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  909 */     if (paramCount == 0) {
/*  910 */       return new AnnotatedConstructor(typeContext, ctor.getConstructor(), _collectRelevantAnnotations(ctor.getDeclaredAnnotations()), NO_ANNOTATION_MAPS);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  915 */     Annotation[][] paramAnns = ctor.getParameterAnnotations();
/*  916 */     AnnotationMap[] resolvedAnnotations; if (paramCount != paramAnns.length)
/*      */     {
/*      */ 
/*      */ 
/*  920 */       AnnotationMap[] resolvedAnnotations = null;
/*  921 */       Class<?> dc = ctor.getDeclaringClass();
/*      */       
/*  923 */       if ((dc.isEnum()) && (paramCount == paramAnns.length + 2)) {
/*  924 */         Annotation[][] old = paramAnns;
/*  925 */         paramAnns = new Annotation[old.length + 2][];
/*  926 */         System.arraycopy(old, 0, paramAnns, 2, old.length);
/*  927 */         resolvedAnnotations = _collectRelevantAnnotations(paramAnns);
/*  928 */       } else if (dc.isMemberClass())
/*      */       {
/*  930 */         if (paramCount == paramAnns.length + 1)
/*      */         {
/*  932 */           Annotation[][] old = paramAnns;
/*  933 */           paramAnns = new Annotation[old.length + 1][];
/*  934 */           System.arraycopy(old, 0, paramAnns, 1, old.length);
/*  935 */           resolvedAnnotations = _collectRelevantAnnotations(paramAnns);
/*      */         }
/*      */       }
/*  938 */       if (resolvedAnnotations == null) {
/*  939 */         throw new IllegalStateException("Internal error: constructor for " + ctor.getDeclaringClass().getName() + " has mismatch: " + paramCount + " parameters; " + paramAnns.length + " sets of annotations");
/*      */       }
/*      */     }
/*      */     else {
/*  943 */       resolvedAnnotations = _collectRelevantAnnotations(paramAnns);
/*      */     }
/*  945 */     return new AnnotatedConstructor(typeContext, ctor.getConstructor(), _collectRelevantAnnotations(ctor.getDeclaredAnnotations()), resolvedAnnotations);
/*      */   }
/*      */   
/*      */ 
/*      */   protected AnnotatedMethod _constructCreatorMethod(Method m, TypeResolutionContext typeContext)
/*      */   {
/*  951 */     int paramCount = m.getParameterTypes().length;
/*  952 */     if (this._annotationIntrospector == null) {
/*  953 */       return new AnnotatedMethod(typeContext, m, _emptyAnnotationMap(), _emptyAnnotationMaps(paramCount));
/*      */     }
/*  955 */     if (paramCount == 0) {
/*  956 */       return new AnnotatedMethod(typeContext, m, _collectRelevantAnnotations(m.getDeclaredAnnotations()), NO_ANNOTATION_MAPS);
/*      */     }
/*      */     
/*  959 */     return new AnnotatedMethod(typeContext, m, _collectRelevantAnnotations(m.getDeclaredAnnotations()), _collectRelevantAnnotations(m.getParameterAnnotations()));
/*      */   }
/*      */   
/*      */ 
/*      */   protected AnnotatedField _constructField(Field f, TypeResolutionContext typeContext)
/*      */   {
/*  965 */     if (this._annotationIntrospector == null) {
/*  966 */       return new AnnotatedField(typeContext, f, _emptyAnnotationMap());
/*      */     }
/*  968 */     return new AnnotatedField(typeContext, f, _collectRelevantAnnotations(f.getDeclaredAnnotations()));
/*      */   }
/*      */   
/*      */   private AnnotationMap _emptyAnnotationMap() {
/*  972 */     return new AnnotationMap();
/*      */   }
/*      */   
/*      */   private AnnotationMap[] _emptyAnnotationMaps(int count) {
/*  976 */     if (count == 0) {
/*  977 */       return NO_ANNOTATION_MAPS;
/*      */     }
/*  979 */     AnnotationMap[] maps = new AnnotationMap[count];
/*  980 */     for (int i = 0; i < count; i++) {
/*  981 */       maps[i] = _emptyAnnotationMap();
/*      */     }
/*  983 */     return maps;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean _isIncludableMemberMethod(Method m)
/*      */   {
/*  994 */     if (Modifier.isStatic(m.getModifiers())) {
/*  995 */       return false;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1001 */     if ((m.isSynthetic()) || (m.isBridge())) {
/* 1002 */       return false;
/*      */     }
/*      */     
/* 1005 */     int pcount = m.getParameterTypes().length;
/* 1006 */     return pcount <= 2;
/*      */   }
/*      */   
/*      */ 
/*      */   private boolean _isIncludableField(Field f)
/*      */   {
/* 1012 */     if (f.isSynthetic()) {
/* 1013 */       return false;
/*      */     }
/*      */     
/*      */ 
/* 1017 */     int mods = f.getModifiers();
/* 1018 */     if (Modifier.isStatic(mods)) {
/* 1019 */       return false;
/*      */     }
/* 1021 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */   private boolean _isIncludableConstructor(Constructor<?> c)
/*      */   {
/* 1027 */     return !c.isSynthetic();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected AnnotationMap[] _collectRelevantAnnotations(Annotation[][] anns)
/*      */   {
/* 1038 */     int len = anns.length;
/* 1039 */     AnnotationMap[] result = new AnnotationMap[len];
/* 1040 */     for (int i = 0; i < len; i++) {
/* 1041 */       result[i] = _collectRelevantAnnotations(anns[i]);
/*      */     }
/* 1043 */     return result;
/*      */   }
/*      */   
/*      */   protected AnnotationMap _collectRelevantAnnotations(Annotation[] anns)
/*      */   {
/* 1048 */     return _addAnnotationsIfNotPresent(new AnnotationMap(), anns);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private AnnotationMap _addAnnotationsIfNotPresent(AnnotationMap result, Annotation[] anns)
/*      */   {
/* 1057 */     if (anns != null) {
/* 1058 */       List<Annotation> fromBundles = null;
/* 1059 */       for (Annotation ann : anns)
/*      */       {
/* 1061 */         boolean wasNotPresent = result.addIfNotPresent(ann);
/* 1062 */         if ((wasNotPresent) && (_isAnnotationBundle(ann))) {
/* 1063 */           fromBundles = _addFromBundle(ann, fromBundles);
/*      */         }
/*      */       }
/* 1066 */       if (fromBundles != null) {
/* 1067 */         _addAnnotationsIfNotPresent(result, (Annotation[])fromBundles.toArray(new Annotation[fromBundles.size()]));
/*      */       }
/*      */     }
/* 1070 */     return result;
/*      */   }
/*      */   
/*      */   private List<Annotation> _addFromBundle(Annotation bundle, List<Annotation> result)
/*      */   {
/* 1075 */     for (Annotation a : ClassUtil.findClassAnnotations(bundle.annotationType()))
/*      */     {
/* 1077 */       if ((!(a instanceof java.lang.annotation.Target)) && (!(a instanceof java.lang.annotation.Retention)))
/*      */       {
/*      */ 
/* 1080 */         if (result == null) {
/* 1081 */           result = new ArrayList();
/*      */         }
/* 1083 */         result.add(a);
/*      */       } }
/* 1085 */     return result;
/*      */   }
/*      */   
/*      */   private void _addAnnotationsIfNotPresent(AnnotatedMember target, Annotation[] anns)
/*      */   {
/* 1090 */     if (anns != null) {
/* 1091 */       List<Annotation> fromBundles = null;
/* 1092 */       for (Annotation ann : anns) {
/* 1093 */         boolean wasNotPresent = target.addIfNotPresent(ann);
/* 1094 */         if ((wasNotPresent) && (_isAnnotationBundle(ann))) {
/* 1095 */           fromBundles = _addFromBundle(ann, fromBundles);
/*      */         }
/*      */       }
/* 1098 */       if (fromBundles != null) {
/* 1099 */         _addAnnotationsIfNotPresent(target, (Annotation[])fromBundles.toArray(new Annotation[fromBundles.size()]));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void _addOrOverrideAnnotations(AnnotatedMember target, Annotation[] anns)
/*      */   {
/* 1106 */     if (anns != null) {
/* 1107 */       List<Annotation> fromBundles = null;
/* 1108 */       for (Annotation ann : anns) {
/* 1109 */         boolean wasModified = target.addOrOverride(ann);
/* 1110 */         if ((wasModified) && (_isAnnotationBundle(ann))) {
/* 1111 */           fromBundles = _addFromBundle(ann, fromBundles);
/*      */         }
/*      */       }
/* 1114 */       if (fromBundles != null) {
/* 1115 */         _addOrOverrideAnnotations(target, (Annotation[])fromBundles.toArray(new Annotation[fromBundles.size()]));
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _addMixOvers(Constructor<?> mixin, AnnotatedConstructor target, boolean addParamAnnotations)
/*      */   {
/* 1127 */     _addOrOverrideAnnotations(target, mixin.getDeclaredAnnotations());
/* 1128 */     if (addParamAnnotations) {
/* 1129 */       Annotation[][] pa = mixin.getParameterAnnotations();
/* 1130 */       int i = 0; for (int len = pa.length; i < len; i++) {
/* 1131 */         for (Annotation a : pa[i]) {
/* 1132 */           target.addOrOverrideParam(i, a);
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
/*      */   protected void _addMixOvers(Method mixin, AnnotatedMethod target, boolean addParamAnnotations)
/*      */   {
/* 1145 */     _addOrOverrideAnnotations(target, mixin.getDeclaredAnnotations());
/* 1146 */     if (addParamAnnotations) {
/* 1147 */       Annotation[][] pa = mixin.getParameterAnnotations();
/* 1148 */       int i = 0; for (int len = pa.length; i < len; i++) {
/* 1149 */         for (Annotation a : pa[i]) {
/* 1150 */           target.addOrOverrideParam(i, a);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void _addMixUnders(Method src, AnnotatedMethod target)
/*      */   {
/* 1161 */     _addAnnotationsIfNotPresent(target, src.getDeclaredAnnotations());
/*      */   }
/*      */   
/*      */   private final boolean _isAnnotationBundle(Annotation ann) {
/* 1165 */     return (this._annotationIntrospector != null) && (this._annotationIntrospector.isAnnotationBundle(ann));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Method[] _findClassMethods(Class<?> cls)
/*      */   {
/*      */     try
/*      */     {
/* 1178 */       return ClassUtil.getDeclaredMethods(cls);
/*      */     }
/*      */     catch (NoClassDefFoundError ex)
/*      */     {
/* 1182 */       ClassLoader loader = Thread.currentThread().getContextClassLoader();
/* 1183 */       if (loader == null)
/*      */       {
/* 1185 */         throw ex;
/*      */       }
/*      */       Class<?> contextClass;
/*      */       try {
/* 1189 */         contextClass = loader.loadClass(cls.getName());
/*      */       }
/*      */       catch (ClassNotFoundException e)
/*      */       {
/* 1193 */         throw ex;
/*      */       }
/* 1195 */       return contextClass.getDeclaredMethods();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1207 */     return "[AnnotedClass " + this._class.getName() + "]";
/*      */   }
/*      */   
/*      */   public int hashCode()
/*      */   {
/* 1212 */     return this._class.getName().hashCode();
/*      */   }
/*      */   
/*      */   public boolean equals(Object o)
/*      */   {
/* 1217 */     if (o == this) return true;
/* 1218 */     if ((o == null) || (o.getClass() != getClass())) return false;
/* 1219 */     return ((AnnotatedClass)o)._class == this._class;
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\introspect\AnnotatedClass.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */