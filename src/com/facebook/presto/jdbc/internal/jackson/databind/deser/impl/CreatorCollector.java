/*     */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.impl;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.BeanDescription;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.MapperFeature;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.cfg.MapperConfig;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.CreatorProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.SettableBeanProperty;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.ValueInstantiator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StdValueInstantiator;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.Annotated;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedMember;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedParameter;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotatedWithParams;
/*     */ import com.facebook.presto.jdbc.internal.jackson.databind.introspect.AnnotationMap;
/*     */ import java.lang.reflect.Member;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ 
/*     */ public class CreatorCollector
/*     */ {
/*     */   protected static final int C_DEFAULT = 0;
/*     */   protected static final int C_STRING = 1;
/*     */   protected static final int C_INT = 2;
/*     */   protected static final int C_LONG = 3;
/*     */   protected static final int C_DOUBLE = 4;
/*     */   protected static final int C_BOOLEAN = 5;
/*     */   protected static final int C_DELEGATE = 6;
/*     */   protected static final int C_PROPS = 7;
/*     */   protected static final int C_ARRAY_DELEGATE = 8;
/*  34 */   protected static final String[] TYPE_DESCS = { "default", "from-String", "from-int", "from-long", "from-double", "from-boolean", "delegate", "property-based" };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final BeanDescription _beanDesc;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final boolean _canFixAccess;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final boolean _forceAccess;
/*     */   
/*     */ 
/*     */ 
/*  53 */   protected final AnnotatedWithParams[] _creators = new AnnotatedWithParams[9];
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  62 */   protected int _explicitCreators = 0;
/*     */   
/*  64 */   protected boolean _hasNonDefaultCreator = false;
/*     */   
/*     */ 
/*     */   protected SettableBeanProperty[] _delegateArgs;
/*     */   
/*     */ 
/*     */   protected SettableBeanProperty[] _arrayDelegateArgs;
/*     */   
/*     */ 
/*     */   protected SettableBeanProperty[] _propertyBasedArgs;
/*     */   
/*     */ 
/*     */   protected AnnotatedParameter _incompleteParameter;
/*     */   
/*     */ 
/*     */   public CreatorCollector(BeanDescription beanDesc, MapperConfig<?> config)
/*     */   {
/*  81 */     this._beanDesc = beanDesc;
/*  82 */     this._canFixAccess = config.canOverrideAccessModifiers();
/*  83 */     this._forceAccess = config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS);
/*     */   }
/*     */   
/*     */ 
/*     */   public ValueInstantiator constructValueInstantiator(DeserializationConfig config)
/*     */   {
/*  89 */     JavaType delegateType = _computeDelegateType(this._creators[6], this._delegateArgs);
/*     */     
/*  91 */     JavaType arrayDelegateType = _computeDelegateType(this._creators[8], this._arrayDelegateArgs);
/*     */     
/*  93 */     JavaType type = this._beanDesc.getType();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  98 */     AnnotatedWithParams defaultCtor = StdTypeConstructor.tryToOptimize(this._creators[0]);
/*     */     
/*     */ 
/* 101 */     StdValueInstantiator inst = new StdValueInstantiator(config, type);
/* 102 */     inst.configureFromObjectSettings(defaultCtor, this._creators[6], delegateType, this._delegateArgs, this._creators[7], this._propertyBasedArgs);
/*     */     
/*     */ 
/* 105 */     inst.configureFromArraySettings(this._creators[8], arrayDelegateType, this._arrayDelegateArgs);
/*     */     
/* 107 */     inst.configureFromStringCreator(this._creators[1]);
/* 108 */     inst.configureFromIntCreator(this._creators[2]);
/* 109 */     inst.configureFromLongCreator(this._creators[3]);
/* 110 */     inst.configureFromDoubleCreator(this._creators[4]);
/* 111 */     inst.configureFromBooleanCreator(this._creators[5]);
/* 112 */     inst.configureIncompleteParameter(this._incompleteParameter);
/* 113 */     return inst;
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
/*     */   public void setDefaultCreator(AnnotatedWithParams creator)
/*     */   {
/* 132 */     this._creators[0] = ((AnnotatedWithParams)_fixAccess(creator));
/*     */   }
/*     */   
/*     */   public void addStringCreator(AnnotatedWithParams creator, boolean explicit)
/*     */   {
/* 137 */     verifyNonDup(creator, 1, explicit);
/*     */   }
/*     */   
/*     */   public void addIntCreator(AnnotatedWithParams creator, boolean explicit) {
/* 141 */     verifyNonDup(creator, 2, explicit);
/*     */   }
/*     */   
/*     */   public void addLongCreator(AnnotatedWithParams creator, boolean explicit) {
/* 145 */     verifyNonDup(creator, 3, explicit);
/*     */   }
/*     */   
/*     */   public void addDoubleCreator(AnnotatedWithParams creator, boolean explicit)
/*     */   {
/* 150 */     verifyNonDup(creator, 4, explicit);
/*     */   }
/*     */   
/*     */   public void addBooleanCreator(AnnotatedWithParams creator, boolean explicit)
/*     */   {
/* 155 */     verifyNonDup(creator, 5, explicit);
/*     */   }
/*     */   
/*     */   public void addDelegatingCreator(AnnotatedWithParams creator, boolean explicit, SettableBeanProperty[] injectables)
/*     */   {
/* 160 */     if (creator.getParameterType(0).isCollectionLikeType()) {
/* 161 */       verifyNonDup(creator, 8, explicit);
/* 162 */       this._arrayDelegateArgs = injectables;
/*     */     } else {
/* 164 */       verifyNonDup(creator, 6, explicit);
/* 165 */       this._delegateArgs = injectables;
/*     */     }
/*     */   }
/*     */   
/*     */   public void addPropertyCreator(AnnotatedWithParams creator, boolean explicit, SettableBeanProperty[] properties)
/*     */   {
/* 171 */     verifyNonDup(creator, 7, explicit);
/*     */     
/* 173 */     if (properties.length > 1) {
/* 174 */       HashMap<String, Integer> names = new HashMap();
/* 175 */       int i = 0; for (int len = properties.length; i < len; i++) {
/* 176 */         String name = properties[i].getName();
/*     */         
/*     */ 
/* 179 */         if ((name.length() != 0) || (properties[i].getInjectableValueId() == null))
/*     */         {
/*     */ 
/*     */ 
/* 183 */           Integer old = (Integer)names.put(name, Integer.valueOf(i));
/* 184 */           if (old != null) {
/* 185 */             throw new IllegalArgumentException(String.format("Duplicate creator property \"%s\" (index %s vs %d)", new Object[] { name, old, Integer.valueOf(i) }));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 191 */     this._propertyBasedArgs = properties;
/*     */   }
/*     */   
/*     */   public void addIncompeteParameter(AnnotatedParameter parameter) {
/* 195 */     if (this._incompleteParameter == null) {
/* 196 */       this._incompleteParameter = parameter;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @Deprecated
/*     */   public void addStringCreator(AnnotatedWithParams creator)
/*     */   {
/* 204 */     addStringCreator(creator, false);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void addIntCreator(AnnotatedWithParams creator) {
/* 209 */     addBooleanCreator(creator, false);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void addLongCreator(AnnotatedWithParams creator) {
/* 214 */     addBooleanCreator(creator, false);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void addDoubleCreator(AnnotatedWithParams creator) {
/* 219 */     addBooleanCreator(creator, false);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void addBooleanCreator(AnnotatedWithParams creator) {
/* 224 */     addBooleanCreator(creator, false);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void addDelegatingCreator(AnnotatedWithParams creator, CreatorProperty[] injectables)
/*     */   {
/* 230 */     addDelegatingCreator(creator, false, injectables);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void addPropertyCreator(AnnotatedWithParams creator, CreatorProperty[] properties)
/*     */   {
/* 236 */     addPropertyCreator(creator, false, properties);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasDefaultCreator()
/*     */   {
/* 248 */     return this._creators[0] != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean hasDelegatingCreator()
/*     */   {
/* 255 */     return this._creators[6] != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean hasPropertyBasedCreator()
/*     */   {
/* 262 */     return this._creators[7] != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private JavaType _computeDelegateType(AnnotatedWithParams creator, SettableBeanProperty[] delegateArgs)
/*     */   {
/* 272 */     if ((!this._hasNonDefaultCreator) || (creator == null)) {
/* 273 */       return null;
/*     */     }
/*     */     
/* 276 */     int ix = 0;
/* 277 */     if (delegateArgs != null) {
/* 278 */       int i = 0; for (int len = delegateArgs.length; i < len; i++) {
/* 279 */         if (delegateArgs[i] == null) {
/* 280 */           ix = i;
/* 281 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 285 */     return creator.getParameterType(ix);
/*     */   }
/*     */   
/*     */   private <T extends AnnotatedMember> T _fixAccess(T member) {
/* 289 */     if ((member != null) && (this._canFixAccess)) {
/* 290 */       com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil.checkAndFixAccess((Member)member.getAnnotated(), this._forceAccess);
/*     */     }
/*     */     
/* 293 */     return member;
/*     */   }
/*     */   
/*     */   protected void verifyNonDup(AnnotatedWithParams newOne, int typeIndex, boolean explicit)
/*     */   {
/* 298 */     int mask = 1 << typeIndex;
/* 299 */     this._hasNonDefaultCreator = true;
/* 300 */     AnnotatedWithParams oldOne = this._creators[typeIndex];
/*     */     
/* 302 */     if (oldOne != null) {
/*     */       boolean verify;
/*     */       boolean verify;
/* 305 */       if ((this._explicitCreators & mask) != 0)
/*     */       {
/*     */ 
/* 308 */         if (!explicit) {
/* 309 */           return;
/*     */         }
/*     */         
/* 312 */         verify = true;
/*     */       }
/*     */       else {
/* 315 */         verify = !explicit;
/*     */       }
/*     */       
/*     */ 
/* 319 */       if ((verify) && (oldOne.getClass() == newOne.getClass()))
/*     */       {
/* 321 */         Class<?> oldType = oldOne.getRawParameterType(0);
/* 322 */         Class<?> newType = newOne.getRawParameterType(0);
/*     */         
/* 324 */         if (oldType == newType)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 330 */           if (_isEnumValueOf(newOne)) {
/* 331 */             return;
/*     */           }
/* 333 */           if (!_isEnumValueOf(oldOne))
/*     */           {
/*     */ 
/* 336 */             throw new IllegalArgumentException(String.format("Conflicting %s creators: already had %s creator %s, encountered another: %s", new Object[] { TYPE_DESCS[typeIndex], explicit ? "explicitly marked" : "implicitly discovered", oldOne, newOne }));
/*     */ 
/*     */ 
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */         }
/* 345 */         else if (newType.isAssignableFrom(oldType))
/*     */         {
/* 347 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 352 */     if (explicit) {
/* 353 */       this._explicitCreators |= mask;
/*     */     }
/* 355 */     this._creators[typeIndex] = ((AnnotatedWithParams)_fixAccess(newOne));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean _isEnumValueOf(AnnotatedWithParams creator)
/*     */   {
/* 364 */     return (creator.getDeclaringClass().isEnum()) && ("valueOf".equals(creator.getName()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static final class StdTypeConstructor
/*     */     extends AnnotatedWithParams
/*     */     implements java.io.Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */ 
/*     */     public static final int TYPE_ARRAY_LIST = 1;
/*     */     
/*     */ 
/*     */     public static final int TYPE_HASH_MAP = 2;
/*     */     
/*     */ 
/*     */     public static final int TYPE_LINKED_HASH_MAP = 3;
/*     */     
/*     */ 
/*     */     private final AnnotatedWithParams _base;
/*     */     
/*     */ 
/*     */     private final int _type;
/*     */     
/*     */ 
/*     */ 
/*     */     public StdTypeConstructor(AnnotatedWithParams base, int t)
/*     */     {
/* 395 */       super(null);
/* 396 */       this._base = base;
/* 397 */       this._type = t;
/*     */     }
/*     */     
/*     */     public static AnnotatedWithParams tryToOptimize(AnnotatedWithParams src)
/*     */     {
/* 402 */       if (src != null) {
/* 403 */         Class<?> rawType = src.getDeclaringClass();
/* 404 */         if ((rawType == java.util.List.class) || (rawType == ArrayList.class)) {
/* 405 */           return new StdTypeConstructor(src, 1);
/*     */         }
/* 407 */         if (rawType == LinkedHashMap.class) {
/* 408 */           return new StdTypeConstructor(src, 3);
/*     */         }
/* 410 */         if (rawType == HashMap.class) {
/* 411 */           return new StdTypeConstructor(src, 2);
/*     */         }
/*     */       }
/* 414 */       return src;
/*     */     }
/*     */     
/*     */     protected final Object _construct() {
/* 418 */       switch (this._type) {
/*     */       case 1: 
/* 420 */         return new ArrayList();
/*     */       case 3: 
/* 422 */         return new LinkedHashMap();
/*     */       case 2: 
/* 424 */         return new HashMap();
/*     */       }
/* 426 */       throw new IllegalStateException("Unknown type " + this._type);
/*     */     }
/*     */     
/*     */     public int getParameterCount()
/*     */     {
/* 431 */       return this._base.getParameterCount();
/*     */     }
/*     */     
/*     */     public Class<?> getRawParameterType(int index)
/*     */     {
/* 436 */       return this._base.getRawParameterType(index);
/*     */     }
/*     */     
/*     */     public JavaType getParameterType(int index)
/*     */     {
/* 441 */       return this._base.getParameterType(index);
/*     */     }
/*     */     
/*     */     @Deprecated
/*     */     public Type getGenericParameterType(int index)
/*     */     {
/* 447 */       return this._base.getGenericParameterType(index);
/*     */     }
/*     */     
/*     */     public Object call() throws Exception
/*     */     {
/* 452 */       return _construct();
/*     */     }
/*     */     
/*     */     public Object call(Object[] args) throws Exception
/*     */     {
/* 457 */       return _construct();
/*     */     }
/*     */     
/*     */     public Object call1(Object arg) throws Exception
/*     */     {
/* 462 */       return _construct();
/*     */     }
/*     */     
/*     */     public Class<?> getDeclaringClass()
/*     */     {
/* 467 */       return this._base.getDeclaringClass();
/*     */     }
/*     */     
/*     */     public Member getMember()
/*     */     {
/* 472 */       return this._base.getMember();
/*     */     }
/*     */     
/*     */     public void setValue(Object pojo, Object value)
/*     */       throws UnsupportedOperationException, IllegalArgumentException
/*     */     {
/* 478 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */     public Object getValue(Object pojo)
/*     */       throws UnsupportedOperationException, IllegalArgumentException
/*     */     {
/* 484 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */     public Annotated withAnnotations(AnnotationMap fallback)
/*     */     {
/* 489 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */     public java.lang.reflect.AnnotatedElement getAnnotated()
/*     */     {
/* 494 */       return this._base.getAnnotated();
/*     */     }
/*     */     
/*     */     protected int getModifiers()
/*     */     {
/* 499 */       return this._base.getMember().getModifiers();
/*     */     }
/*     */     
/*     */     public String getName()
/*     */     {
/* 504 */       return this._base.getName();
/*     */     }
/*     */     
/*     */     public JavaType getType()
/*     */     {
/* 509 */       return this._base.getType();
/*     */     }
/*     */     
/*     */     public Class<?> getRawType()
/*     */     {
/* 514 */       return this._base.getRawType();
/*     */     }
/*     */     
/*     */     public boolean equals(Object o)
/*     */     {
/* 519 */       return o == this;
/*     */     }
/*     */     
/*     */     public int hashCode()
/*     */     {
/* 524 */       return this._base.hashCode();
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 529 */       return this._base.toString();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\impl\CreatorCollector.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */