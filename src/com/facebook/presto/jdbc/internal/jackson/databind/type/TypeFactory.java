/*      */ package com.facebook.presto.jdbc.internal.jackson.databind.type;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jackson.core.type.TypeReference;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ArrayBuilders;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.util.ClassUtil;
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.util.LRUMap;
/*      */ import java.io.Serializable;
/*      */ import java.lang.reflect.GenericArrayType;
/*      */ import java.lang.reflect.ParameterizedType;
/*      */ import java.lang.reflect.Type;
/*      */ import java.lang.reflect.TypeVariable;
/*      */ import java.lang.reflect.WildcardType;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.EnumMap;
/*      */ import java.util.EnumSet;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.TreeMap;
/*      */ import java.util.TreeSet;
/*      */ import java.util.concurrent.atomic.AtomicReference;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class TypeFactory
/*      */   implements Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 1L;
/*   39 */   private static final JavaType[] NO_TYPES = new JavaType[0];
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   46 */   protected static final TypeFactory instance = new TypeFactory();
/*      */   
/*   48 */   protected static final TypeBindings EMPTY_BINDINGS = TypeBindings.emptyBindings();
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
/*   60 */   private static final Class<?> CLS_STRING = String.class;
/*   61 */   private static final Class<?> CLS_OBJECT = Object.class;
/*      */   
/*   63 */   private static final Class<?> CLS_COMPARABLE = Comparable.class;
/*   64 */   private static final Class<?> CLS_CLASS = Class.class;
/*   65 */   private static final Class<?> CLS_ENUM = Enum.class;
/*      */   
/*   67 */   private static final Class<?> CLS_BOOL = Boolean.TYPE;
/*   68 */   private static final Class<?> CLS_INT = Integer.TYPE;
/*   69 */   private static final Class<?> CLS_LONG = Long.TYPE;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   78 */   protected static final SimpleType CORE_TYPE_BOOL = new SimpleType(CLS_BOOL);
/*   79 */   protected static final SimpleType CORE_TYPE_INT = new SimpleType(CLS_INT);
/*   80 */   protected static final SimpleType CORE_TYPE_LONG = new SimpleType(CLS_LONG);
/*      */   
/*      */ 
/*   83 */   protected static final SimpleType CORE_TYPE_STRING = new SimpleType(CLS_STRING);
/*      */   
/*      */ 
/*   86 */   protected static final SimpleType CORE_TYPE_OBJECT = new SimpleType(CLS_OBJECT);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*   94 */   protected static final SimpleType CORE_TYPE_COMPARABLE = new SimpleType(CLS_COMPARABLE);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  102 */   protected static final SimpleType CORE_TYPE_ENUM = new SimpleType(CLS_ENUM);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  110 */   protected static final SimpleType CORE_TYPE_CLASS = new SimpleType(CLS_CLASS);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final LRUMap<Object, JavaType> _typeCache;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final TypeModifier[] _modifiers;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final TypeParser _parser;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected final ClassLoader _classLoader;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private TypeFactory()
/*      */   {
/*  145 */     this(null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected TypeFactory(LRUMap<Object, JavaType> typeCache)
/*      */   {
/*  152 */     if (typeCache == null) {
/*  153 */       typeCache = new LRUMap(16, 200);
/*      */     }
/*  155 */     this._typeCache = typeCache;
/*  156 */     this._parser = new TypeParser(this);
/*  157 */     this._modifiers = null;
/*  158 */     this._classLoader = null;
/*      */   }
/*      */   
/*      */ 
/*      */   protected TypeFactory(LRUMap<Object, JavaType> typeCache, TypeParser p, TypeModifier[] mods, ClassLoader classLoader)
/*      */   {
/*  164 */     if (typeCache == null) {
/*  165 */       typeCache = new LRUMap(16, 200);
/*      */     }
/*  167 */     this._typeCache = typeCache;
/*      */     
/*  169 */     this._parser = p.withFactory(this);
/*  170 */     this._modifiers = mods;
/*  171 */     this._classLoader = classLoader;
/*      */   }
/*      */   
/*      */   public TypeFactory withModifier(TypeModifier mod)
/*      */   {
/*  176 */     LRUMap<Object, JavaType> typeCache = this._typeCache;
/*      */     TypeModifier[] mods;
/*  178 */     if (mod == null) {
/*  179 */       TypeModifier[] mods = null;
/*      */       
/*      */ 
/*  182 */       typeCache = null; } else { TypeModifier[] mods;
/*  183 */       if (this._modifiers == null) {
/*  184 */         mods = new TypeModifier[] { mod };
/*      */       } else
/*  186 */         mods = (TypeModifier[])ArrayBuilders.insertInListNoDup(this._modifiers, mod);
/*      */     }
/*  188 */     return new TypeFactory(typeCache, this._parser, mods, this._classLoader);
/*      */   }
/*      */   
/*      */   public TypeFactory withClassLoader(ClassLoader classLoader) {
/*  192 */     return new TypeFactory(this._typeCache, this._parser, this._modifiers, classLoader);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public TypeFactory withCache(LRUMap<Object, JavaType> cache)
/*      */   {
/*  203 */     return new TypeFactory(cache, this._parser, this._modifiers, this._classLoader);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static TypeFactory defaultInstance()
/*      */   {
/*  211 */     return instance;
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
/*      */   public void clearCache()
/*      */   {
/*  224 */     this._typeCache.clear();
/*      */   }
/*      */   
/*      */   public ClassLoader getClassLoader() {
/*  228 */     return this._classLoader;
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
/*      */   public static JavaType unknownType()
/*      */   {
/*  243 */     return defaultInstance()._unknownType();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Class<?> rawClass(Type t)
/*      */   {
/*  253 */     if ((t instanceof Class)) {
/*  254 */       return (Class)t;
/*      */     }
/*      */     
/*  257 */     return defaultInstance().constructType(t).getRawClass();
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
/*      */   public Class<?> findClass(String className)
/*      */     throws ClassNotFoundException
/*      */   {
/*  274 */     if (className.indexOf('.') < 0) {
/*  275 */       Class<?> prim = _findPrimitive(className);
/*  276 */       if (prim != null) {
/*  277 */         return prim;
/*      */       }
/*      */     }
/*      */     
/*  281 */     Throwable prob = null;
/*  282 */     ClassLoader loader = getClassLoader();
/*  283 */     if (loader == null) {
/*  284 */       loader = Thread.currentThread().getContextClassLoader();
/*      */     }
/*  286 */     if (loader != null) {
/*      */       try {
/*  288 */         return classForName(className, true, loader);
/*      */       } catch (Exception e) {
/*  290 */         prob = ClassUtil.getRootCause(e);
/*      */       }
/*      */     }
/*      */     try {
/*  294 */       return classForName(className);
/*      */     } catch (Exception e) {
/*  296 */       if (prob == null) {
/*  297 */         prob = ClassUtil.getRootCause(e);
/*      */       }
/*      */       
/*  300 */       if ((prob instanceof RuntimeException)) {
/*  301 */         throw ((RuntimeException)prob);
/*      */       }
/*  303 */       throw new ClassNotFoundException(prob.getMessage(), prob);
/*      */     }
/*      */   }
/*      */   
/*      */   protected Class<?> classForName(String name, boolean initialize, ClassLoader loader) throws ClassNotFoundException {
/*  308 */     return Class.forName(name, true, loader);
/*      */   }
/*      */   
/*      */   protected Class<?> classForName(String name) throws ClassNotFoundException {
/*  312 */     return Class.forName(name);
/*      */   }
/*      */   
/*      */   protected Class<?> _findPrimitive(String className)
/*      */   {
/*  317 */     if ("int".equals(className)) return Integer.TYPE;
/*  318 */     if ("long".equals(className)) return Long.TYPE;
/*  319 */     if ("float".equals(className)) return Float.TYPE;
/*  320 */     if ("double".equals(className)) return Double.TYPE;
/*  321 */     if ("boolean".equals(className)) return Boolean.TYPE;
/*  322 */     if ("byte".equals(className)) return Byte.TYPE;
/*  323 */     if ("char".equals(className)) return Character.TYPE;
/*  324 */     if ("short".equals(className)) return Short.TYPE;
/*  325 */     if ("void".equals(className)) return Void.TYPE;
/*  326 */     return null;
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
/*      */   public JavaType constructSpecializedType(JavaType baseType, Class<?> subclass)
/*      */   {
/*  345 */     Class<?> rawBase = baseType.getRawClass();
/*  346 */     if (rawBase == subclass) {
/*  347 */       return baseType;
/*      */     }
/*      */     
/*      */     JavaType newType;
/*      */     
/*      */     JavaType newType;
/*      */     
/*  354 */     if (rawBase == Object.class) {
/*  355 */       newType = _fromClass(null, subclass, TypeBindings.emptyBindings());
/*      */     }
/*      */     else {
/*  358 */       if (!rawBase.isAssignableFrom(subclass)) {
/*  359 */         throw new IllegalArgumentException(String.format("Class %s not subtype of %s", new Object[] { subclass.getName(), baseType }));
/*      */       }
/*      */       
/*      */ 
/*      */       JavaType newType;
/*      */       
/*  365 */       if (baseType.getBindings().isEmpty()) {
/*  366 */         newType = _fromClass(null, subclass, TypeBindings.emptyBindings());
/*      */       }
/*      */       else
/*      */       {
/*  370 */         if (baseType.isContainerType()) {
/*  371 */           if (baseType.isMapLikeType()) {
/*  372 */             if ((subclass == HashMap.class) || (subclass == LinkedHashMap.class) || (subclass == EnumMap.class) || (subclass == TreeMap.class))
/*      */             {
/*      */ 
/*      */ 
/*  376 */               JavaType newType = _fromClass(null, subclass, TypeBindings.create(subclass, baseType.getKeyType(), baseType.getContentType()));
/*      */               break label313;
/*      */             }
/*      */           }
/*  380 */           else if (baseType.isCollectionLikeType()) {
/*  381 */             if ((subclass == ArrayList.class) || (subclass == LinkedList.class) || (subclass == HashSet.class) || (subclass == TreeSet.class))
/*      */             {
/*      */ 
/*      */ 
/*  385 */               JavaType newType = _fromClass(null, subclass, TypeBindings.create(subclass, baseType.getContentType()));
/*      */               
/*      */ 
/*      */               break label313;
/*      */             }
/*      */             
/*  391 */             if (rawBase == EnumSet.class) {
/*  392 */               return baseType;
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  397 */         int typeParamCount = subclass.getTypeParameters().length;
/*  398 */         JavaType newType; if (typeParamCount == 0) {
/*  399 */           newType = _fromClass(null, subclass, TypeBindings.emptyBindings());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  414 */           TypeBindings tb = _bindingsForSubtype(baseType, typeParamCount, subclass);
/*  415 */           JavaType newType; if (baseType.isInterface()) {
/*  416 */             newType = baseType.refine(subclass, tb, null, new JavaType[] { baseType });
/*      */           } else {
/*  418 */             newType = baseType.refine(subclass, tb, baseType, NO_TYPES);
/*      */           }
/*      */           
/*  421 */           if (newType == null) {
/*  422 */             newType = _fromClass(null, subclass, tb);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     label313:
/*  428 */     return newType;
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
/*      */   private TypeBindings _bindingsForSubtype(JavaType baseType, int typeParamCount, Class<?> subclass)
/*      */   {
/*  480 */     int baseCount = baseType.containedTypeCount();
/*  481 */     if (baseCount == typeParamCount) {
/*  482 */       if (typeParamCount == 1) {
/*  483 */         return TypeBindings.create(subclass, baseType.containedType(0));
/*      */       }
/*  485 */       if (typeParamCount == 2) {
/*  486 */         return TypeBindings.create(subclass, baseType.containedType(0), baseType.containedType(1));
/*      */       }
/*      */       
/*  489 */       List<JavaType> types = new ArrayList(baseCount);
/*  490 */       for (int i = 0; i < baseCount; i++) {
/*  491 */         types.add(baseType.containedType(i));
/*      */       }
/*  493 */       return TypeBindings.create(subclass, types);
/*      */     }
/*      */     
/*  496 */     return TypeBindings.emptyBindings();
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
/*      */   public JavaType constructGeneralizedType(JavaType baseType, Class<?> superClass)
/*      */   {
/*  512 */     Class<?> rawBase = baseType.getRawClass();
/*  513 */     if (rawBase == superClass) {
/*  514 */       return baseType;
/*      */     }
/*  516 */     JavaType superType = baseType.findSuperType(superClass);
/*  517 */     if (superType == null)
/*      */     {
/*  519 */       if (!superClass.isAssignableFrom(rawBase)) {
/*  520 */         throw new IllegalArgumentException(String.format("Class %s not a super-type of %s", new Object[] { superClass.getName(), baseType }));
/*      */       }
/*      */       
/*      */ 
/*  524 */       throw new IllegalArgumentException(String.format("Internal error: class %s not included as super-type for %s", new Object[] { superClass.getName(), baseType }));
/*      */     }
/*      */     
/*      */ 
/*  528 */     return superType;
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
/*      */   public JavaType constructFromCanonical(String canonical)
/*      */     throws IllegalArgumentException
/*      */   {
/*  543 */     return this._parser.parse(canonical);
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
/*      */   public JavaType[] findTypeParameters(JavaType type, Class<?> expType)
/*      */   {
/*  557 */     JavaType match = type.findSuperType(expType);
/*  558 */     if (match == null) {
/*  559 */       return NO_TYPES;
/*      */     }
/*  561 */     return match.getBindings().typeParameterArray();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public JavaType[] findTypeParameters(Class<?> clz, Class<?> expType, TypeBindings bindings)
/*      */   {
/*  569 */     return findTypeParameters(constructType(clz, bindings), expType);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public JavaType[] findTypeParameters(Class<?> clz, Class<?> expType)
/*      */   {
/*  577 */     return findTypeParameters(constructType(clz), expType);
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
/*      */   public JavaType moreSpecificType(JavaType type1, JavaType type2)
/*      */   {
/*  592 */     if (type1 == null) {
/*  593 */       return type2;
/*      */     }
/*  595 */     if (type2 == null) {
/*  596 */       return type1;
/*      */     }
/*  598 */     Class<?> raw1 = type1.getRawClass();
/*  599 */     Class<?> raw2 = type2.getRawClass();
/*  600 */     if (raw1 == raw2) {
/*  601 */       return type1;
/*      */     }
/*      */     
/*  604 */     if (raw1.isAssignableFrom(raw2)) {
/*  605 */       return type2;
/*      */     }
/*  607 */     return type1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JavaType constructType(Type type)
/*      */   {
/*  617 */     return _fromAny(null, type, EMPTY_BINDINGS);
/*      */   }
/*      */   
/*      */   public JavaType constructType(Type type, TypeBindings bindings) {
/*  621 */     return _fromAny(null, type, bindings);
/*      */   }
/*      */   
/*      */ 
/*      */   public JavaType constructType(TypeReference<?> typeRef)
/*      */   {
/*  627 */     return _fromAny(null, typeRef.getType(), EMPTY_BINDINGS);
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
/*      */ 
/*      */   @Deprecated
/*      */   public JavaType constructType(Type type, Class<?> contextClass)
/*      */   {
/*  653 */     TypeBindings bindings = contextClass == null ? TypeBindings.emptyBindings() : constructType(contextClass).getBindings();
/*      */     
/*  655 */     return _fromAny(null, type, bindings);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public JavaType constructType(Type type, JavaType contextType)
/*      */   {
/*  663 */     TypeBindings bindings = contextType == null ? TypeBindings.emptyBindings() : contextType.getBindings();
/*      */     
/*  665 */     return _fromAny(null, type, bindings);
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
/*      */   public ArrayType constructArrayType(Class<?> elementType)
/*      */   {
/*  681 */     return ArrayType.construct(_fromAny(null, elementType, null), null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public ArrayType constructArrayType(JavaType elementType)
/*      */   {
/*  691 */     return ArrayType.construct(elementType, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public CollectionType constructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass)
/*      */   {
/*  701 */     return constructCollectionType(collectionClass, _fromClass(null, elementClass, EMPTY_BINDINGS));
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
/*      */   public CollectionType constructCollectionType(Class<? extends Collection> collectionClass, JavaType elementType)
/*      */   {
/*  714 */     return (CollectionType)_fromClass(null, collectionClass, TypeBindings.create(collectionClass, elementType));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public CollectionLikeType constructCollectionLikeType(Class<?> collectionClass, Class<?> elementClass)
/*      */   {
/*  725 */     return constructCollectionLikeType(collectionClass, _fromClass(null, elementClass, EMPTY_BINDINGS));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public CollectionLikeType constructCollectionLikeType(Class<?> collectionClass, JavaType elementType)
/*      */   {
/*  736 */     JavaType type = _fromClass(null, collectionClass, TypeBindings.createIfNeeded(collectionClass, elementType));
/*      */     
/*  738 */     if ((type instanceof CollectionLikeType)) {
/*  739 */       return (CollectionLikeType)type;
/*      */     }
/*  741 */     return CollectionLikeType.upgradeFrom(type, elementType);
/*      */   }
/*      */   
/*      */ 
/*      */   public MapType constructMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass)
/*      */   {
/*      */     JavaType kt;
/*      */     
/*      */     JavaType kt;
/*      */     
/*      */     JavaType vt;
/*  752 */     if (mapClass == Properties.class) { JavaType vt;
/*  753 */       kt = vt = CORE_TYPE_STRING;
/*      */     } else {
/*  755 */       kt = _fromClass(null, keyClass, EMPTY_BINDINGS);
/*  756 */       vt = _fromClass(null, valueClass, EMPTY_BINDINGS);
/*      */     }
/*  758 */     return constructMapType(mapClass, kt, vt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public MapType constructMapType(Class<? extends Map> mapClass, JavaType keyType, JavaType valueType)
/*      */   {
/*  768 */     return (MapType)_fromClass(null, mapClass, TypeBindings.create(mapClass, new JavaType[] { keyType, valueType }));
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
/*      */   public MapLikeType constructMapLikeType(Class<?> mapClass, Class<?> keyClass, Class<?> valueClass)
/*      */   {
/*  781 */     return constructMapLikeType(mapClass, _fromClass(null, keyClass, EMPTY_BINDINGS), _fromClass(null, valueClass, EMPTY_BINDINGS));
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
/*      */   public MapLikeType constructMapLikeType(Class<?> mapClass, JavaType keyType, JavaType valueType)
/*      */   {
/*  795 */     JavaType type = _fromClass(null, mapClass, TypeBindings.createIfNeeded(mapClass, new JavaType[] { keyType, valueType }));
/*      */     
/*  797 */     if ((type instanceof MapLikeType)) {
/*  798 */       return (MapLikeType)type;
/*      */     }
/*  800 */     return MapLikeType.upgradeFrom(type, keyType, valueType);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JavaType constructSimpleType(Class<?> rawType, JavaType[] parameterTypes)
/*      */   {
/*  809 */     return _fromClass(null, rawType, TypeBindings.create(rawType, parameterTypes));
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
/*      */   @Deprecated
/*      */   public JavaType constructSimpleType(Class<?> rawType, Class<?> parameterTarget, JavaType[] parameterTypes)
/*      */   {
/*  823 */     return constructSimpleType(rawType, parameterTypes);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public JavaType constructReferenceType(Class<?> rawType, JavaType referredType)
/*      */   {
/*  831 */     return ReferenceType.construct(rawType, null, null, null, referredType);
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
/*      */   public JavaType uncheckedSimpleType(Class<?> cls)
/*      */   {
/*  850 */     return _constructSimple(cls, EMPTY_BINDINGS, null, null);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JavaType constructParametricType(Class<?> parametrized, Class<?>... parameterClasses)
/*      */   {
/*  881 */     int len = parameterClasses.length;
/*  882 */     JavaType[] pt = new JavaType[len];
/*  883 */     for (int i = 0; i < len; i++) {
/*  884 */       pt[i] = _fromClass(null, parameterClasses[i], null);
/*      */     }
/*  886 */     return constructParametricType(parametrized, pt);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JavaType constructParametricType(Class<?> rawType, JavaType... parameterTypes)
/*      */   {
/*  918 */     return _fromClass(null, rawType, TypeBindings.create(rawType, parameterTypes));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JavaType constructParametrizedType(Class<?> parametrized, Class<?> parametersFor, JavaType... parameterTypes)
/*      */   {
/*  927 */     return constructParametricType(parametrized, parameterTypes);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public JavaType constructParametrizedType(Class<?> parametrized, Class<?> parametersFor, Class<?>... parameterClasses)
/*      */   {
/*  936 */     return constructParametricType(parametrized, parameterClasses);
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
/*      */   public CollectionType constructRawCollectionType(Class<? extends Collection> collectionClass)
/*      */   {
/*  958 */     return constructCollectionType(collectionClass, unknownType());
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
/*      */   public CollectionLikeType constructRawCollectionLikeType(Class<?> collectionClass)
/*      */   {
/*  973 */     return constructCollectionLikeType(collectionClass, unknownType());
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
/*      */   public MapType constructRawMapType(Class<? extends Map> mapClass)
/*      */   {
/*  988 */     return constructMapType(mapClass, unknownType(), unknownType());
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
/*      */   public MapLikeType constructRawMapLikeType(Class<?> mapClass)
/*      */   {
/* 1003 */     return constructMapLikeType(mapClass, unknownType(), unknownType());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private JavaType _mapType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces)
/*      */   {
/*      */     JavaType kt;
/*      */     
/*      */ 
/*      */     JavaType vt;
/*      */     
/*      */ 
/*      */     JavaType kt;
/*      */     
/* 1018 */     if (rawClass == Properties.class) { JavaType vt;
/* 1019 */       kt = vt = CORE_TYPE_STRING;
/*      */     } else {
/* 1021 */       List<JavaType> typeParams = bindings.getTypeParameters();
/*      */       
/* 1023 */       switch (typeParams.size()) {
/*      */       case 0: 
/* 1025 */         kt = vt = _unknownType();
/* 1026 */         break;
/*      */       case 2: 
/* 1028 */         kt = (JavaType)typeParams.get(0);
/* 1029 */         vt = (JavaType)typeParams.get(1);
/* 1030 */         break;
/*      */       default: 
/* 1032 */         throw new IllegalArgumentException("Strange Map type " + rawClass.getName() + ": can not determine type parameters");
/*      */       }
/*      */     }
/* 1035 */     return MapType.construct(rawClass, bindings, superClass, superInterfaces, kt, vt);
/*      */   }
/*      */   
/*      */ 
/*      */   private JavaType _collectionType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces)
/*      */   {
/* 1041 */     List<JavaType> typeParams = bindings.getTypeParameters();
/*      */     
/*      */     JavaType ct;
/* 1044 */     if (typeParams.isEmpty()) {
/* 1045 */       ct = _unknownType(); } else { JavaType ct;
/* 1046 */       if (typeParams.size() == 1) {
/* 1047 */         ct = (JavaType)typeParams.get(0);
/*      */       } else
/* 1049 */         throw new IllegalArgumentException("Strange Collection type " + rawClass.getName() + ": can not determine type parameters"); }
/*      */     JavaType ct;
/* 1051 */     return CollectionType.construct(rawClass, bindings, superClass, superInterfaces, ct);
/*      */   }
/*      */   
/*      */ 
/*      */   private JavaType _referenceType(Class<?> rawClass, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces)
/*      */   {
/* 1057 */     List<JavaType> typeParams = bindings.getTypeParameters();
/*      */     
/*      */     JavaType ct;
/* 1060 */     if (typeParams.isEmpty()) {
/* 1061 */       ct = _unknownType(); } else { JavaType ct;
/* 1062 */       if (typeParams.size() == 1) {
/* 1063 */         ct = (JavaType)typeParams.get(0);
/*      */       } else
/* 1065 */         throw new IllegalArgumentException("Strange Reference type " + rawClass.getName() + ": can not determine type parameters"); }
/*      */     JavaType ct;
/* 1067 */     return ReferenceType.construct(rawClass, bindings, superClass, superInterfaces, ct);
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
/*      */   protected JavaType _constructSimple(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces)
/*      */   {
/* 1081 */     if (bindings.isEmpty()) {
/* 1082 */       JavaType result = _findWellKnownSimple(raw);
/* 1083 */       if (result != null) {
/* 1084 */         return result;
/*      */       }
/*      */     }
/* 1087 */     return _newSimpleType(raw, bindings, superClass, superInterfaces);
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
/*      */   protected JavaType _newSimpleType(Class<?> raw, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces)
/*      */   {
/* 1100 */     return new SimpleType(raw, bindings, superClass, superInterfaces);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JavaType _unknownType()
/*      */   {
/* 1109 */     return CORE_TYPE_OBJECT;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JavaType _findWellKnownSimple(Class<?> clz)
/*      */   {
/* 1120 */     if (clz.isPrimitive()) {
/* 1121 */       if (clz == CLS_BOOL) return CORE_TYPE_BOOL;
/* 1122 */       if (clz == CLS_INT) return CORE_TYPE_INT;
/* 1123 */       if (clz == CLS_LONG) return CORE_TYPE_LONG;
/*      */     } else {
/* 1125 */       if (clz == CLS_STRING) return CORE_TYPE_STRING;
/* 1126 */       if (clz == CLS_OBJECT) return CORE_TYPE_OBJECT;
/*      */     }
/* 1128 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JavaType _fromAny(ClassStack context, Type type, TypeBindings bindings)
/*      */   {
/*      */     JavaType resultType;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1147 */     if ((type instanceof Class))
/*      */     {
/* 1149 */       resultType = _fromClass(context, (Class)type, EMPTY_BINDINGS);
/*      */     } else {
/*      */       JavaType resultType;
/* 1152 */       if ((type instanceof ParameterizedType)) {
/* 1153 */         resultType = _fromParamType(context, (ParameterizedType)type, bindings);
/*      */       } else {
/* 1155 */         if ((type instanceof JavaType))
/*      */         {
/* 1157 */           return (JavaType)type; }
/*      */         JavaType resultType;
/* 1159 */         if ((type instanceof GenericArrayType)) {
/* 1160 */           resultType = _fromArrayType(context, (GenericArrayType)type, bindings);
/*      */         } else { JavaType resultType;
/* 1162 */           if ((type instanceof TypeVariable)) {
/* 1163 */             resultType = _fromVariable(context, (TypeVariable)type, bindings);
/*      */           } else { JavaType resultType;
/* 1165 */             if ((type instanceof WildcardType)) {
/* 1166 */               resultType = _fromWildcard(context, (WildcardType)type, bindings);
/*      */             }
/*      */             else
/* 1169 */               throw new IllegalArgumentException("Unrecognized Type: " + (type == null ? "[null]" : type.toString()));
/*      */           }
/*      */         }
/*      */       } }
/*      */     JavaType resultType;
/* 1174 */     if (this._modifiers != null) {
/* 1175 */       TypeBindings b = resultType.getBindings();
/* 1176 */       if (b == null) {
/* 1177 */         b = EMPTY_BINDINGS;
/*      */       }
/* 1179 */       for (TypeModifier mod : this._modifiers) {
/* 1180 */         JavaType t = mod.modifyType(resultType, type, b, this);
/* 1181 */         if (t == null) {
/* 1182 */           throw new IllegalStateException(String.format("TypeModifier %s (of type %s) return null for type %s", new Object[] { mod, mod.getClass().getName(), resultType }));
/*      */         }
/*      */         
/*      */ 
/* 1186 */         resultType = t;
/*      */       }
/*      */     }
/* 1189 */     return resultType;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JavaType _fromClass(ClassStack context, Class<?> rawType, TypeBindings bindings)
/*      */   {
/* 1199 */     JavaType result = _findWellKnownSimple(rawType);
/* 1200 */     if (result != null) {
/* 1201 */       return result;
/*      */     }
/*      */     Object key;
/*      */     Object key;
/* 1205 */     if ((bindings == null) || (bindings.isEmpty())) {
/* 1206 */       key = rawType;
/*      */     } else {
/* 1208 */       key = bindings.asKey(rawType);
/*      */     }
/* 1210 */     result = (JavaType)this._typeCache.get(key);
/* 1211 */     if (result != null) {
/* 1212 */       return result;
/*      */     }
/*      */     
/*      */ 
/* 1216 */     if (context == null) {
/* 1217 */       context = new ClassStack(rawType);
/*      */     } else {
/* 1219 */       ClassStack prev = context.find(rawType);
/* 1220 */       if (prev != null)
/*      */       {
/* 1222 */         ResolvedRecursiveType selfRef = new ResolvedRecursiveType(rawType, EMPTY_BINDINGS);
/* 1223 */         prev.addSelfReference(selfRef);
/* 1224 */         return selfRef;
/*      */       }
/*      */       
/* 1227 */       context = context.child(rawType);
/*      */     }
/*      */     
/*      */ 
/* 1231 */     if (rawType.isArray()) {
/* 1232 */       result = ArrayType.construct(_fromAny(context, rawType.getComponentType(), bindings), bindings);
/*      */     }
/*      */     else
/*      */     {
/*      */       JavaType[] superInterfaces;
/*      */       
/*      */       JavaType superClass;
/*      */       JavaType[] superInterfaces;
/* 1240 */       if (rawType.isInterface()) {
/* 1241 */         JavaType superClass = null;
/* 1242 */         superInterfaces = _resolveSuperInterfaces(context, rawType, bindings);
/*      */       }
/*      */       else {
/* 1245 */         superClass = _resolveSuperClass(context, rawType, bindings);
/* 1246 */         superInterfaces = _resolveSuperInterfaces(context, rawType, bindings);
/*      */       }
/*      */       
/*      */ 
/* 1250 */       if (rawType == Properties.class) {
/* 1251 */         result = MapType.construct(rawType, bindings, superClass, superInterfaces, CORE_TYPE_STRING, CORE_TYPE_STRING);
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/* 1256 */       else if (superClass != null) {
/* 1257 */         result = superClass.refine(rawType, bindings, superClass, superInterfaces);
/*      */       }
/*      */       
/* 1260 */       if (result == null) {
/* 1261 */         result = _fromWellKnownClass(context, rawType, bindings, superClass, superInterfaces);
/* 1262 */         if (result == null) {
/* 1263 */           result = _fromWellKnownInterface(context, rawType, bindings, superClass, superInterfaces);
/* 1264 */           if (result == null)
/*      */           {
/* 1266 */             result = _newSimpleType(rawType, bindings, superClass, superInterfaces);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1271 */     context.resolveSelfReferences(result);
/*      */     
/*      */ 
/* 1274 */     if (!result.hasHandlers()) {
/* 1275 */       this._typeCache.putIfAbsent(key, result);
/*      */     }
/* 1277 */     return result;
/*      */   }
/*      */   
/*      */   protected JavaType _resolveSuperClass(ClassStack context, Class<?> rawType, TypeBindings parentBindings)
/*      */   {
/* 1282 */     Type parent = ClassUtil.getGenericSuperclass(rawType);
/* 1283 */     if (parent == null) {
/* 1284 */       return null;
/*      */     }
/* 1286 */     return _fromAny(context, parent, parentBindings);
/*      */   }
/*      */   
/*      */   protected JavaType[] _resolveSuperInterfaces(ClassStack context, Class<?> rawType, TypeBindings parentBindings)
/*      */   {
/* 1291 */     Type[] types = ClassUtil.getGenericInterfaces(rawType);
/* 1292 */     if ((types == null) || (types.length == 0)) {
/* 1293 */       return NO_TYPES;
/*      */     }
/* 1295 */     int len = types.length;
/* 1296 */     JavaType[] resolved = new JavaType[len];
/* 1297 */     for (int i = 0; i < len; i++) {
/* 1298 */       Type type = types[i];
/* 1299 */       resolved[i] = _fromAny(context, type, parentBindings);
/*      */     }
/* 1301 */     return resolved;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JavaType _fromWellKnownClass(ClassStack context, Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces)
/*      */   {
/* 1312 */     if (bindings == null) {
/* 1313 */       bindings = TypeBindings.emptyBindings();
/*      */     }
/*      */     
/*      */ 
/* 1317 */     if (rawType == Map.class) {
/* 1318 */       return _mapType(rawType, bindings, superClass, superInterfaces);
/*      */     }
/* 1320 */     if (rawType == Collection.class) {
/* 1321 */       return _collectionType(rawType, bindings, superClass, superInterfaces);
/*      */     }
/*      */     
/* 1324 */     if (rawType == AtomicReference.class) {
/* 1325 */       return _referenceType(rawType, bindings, superClass, superInterfaces);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1331 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JavaType _fromWellKnownInterface(ClassStack context, Class<?> rawType, TypeBindings bindings, JavaType superClass, JavaType[] superInterfaces)
/*      */   {
/* 1339 */     int intCount = superInterfaces.length;
/*      */     
/* 1341 */     for (int i = 0; i < intCount; i++) {
/* 1342 */       JavaType result = superInterfaces[i].refine(rawType, bindings, superClass, superInterfaces);
/* 1343 */       if (result != null) {
/* 1344 */         return result;
/*      */       }
/*      */     }
/* 1347 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JavaType _fromParamType(ClassStack context, ParameterizedType ptype, TypeBindings parentBindings)
/*      */   {
/* 1358 */     Class<?> rawType = (Class)ptype.getRawType();
/*      */     
/*      */ 
/*      */ 
/* 1362 */     if (rawType == CLS_ENUM) {
/* 1363 */       return CORE_TYPE_ENUM;
/*      */     }
/* 1365 */     if (rawType == CLS_COMPARABLE) {
/* 1366 */       return CORE_TYPE_COMPARABLE;
/*      */     }
/* 1368 */     if (rawType == CLS_CLASS) {
/* 1369 */       return CORE_TYPE_CLASS;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1375 */     Type[] args = ptype.getActualTypeArguments();
/* 1376 */     int paramCount = args == null ? 0 : args.length;
/*      */     
/*      */     TypeBindings newBindings;
/*      */     TypeBindings newBindings;
/* 1380 */     if (paramCount == 0) {
/* 1381 */       newBindings = EMPTY_BINDINGS;
/*      */     } else {
/* 1383 */       JavaType[] pt = new JavaType[paramCount];
/* 1384 */       for (int i = 0; i < paramCount; i++) {
/* 1385 */         pt[i] = _fromAny(context, args[i], parentBindings);
/*      */       }
/* 1387 */       newBindings = TypeBindings.create(rawType, pt);
/*      */     }
/* 1389 */     return _fromClass(context, rawType, newBindings);
/*      */   }
/*      */   
/*      */   protected JavaType _fromArrayType(ClassStack context, GenericArrayType type, TypeBindings bindings)
/*      */   {
/* 1394 */     JavaType elementType = _fromAny(context, type.getGenericComponentType(), bindings);
/* 1395 */     return ArrayType.construct(elementType, bindings);
/*      */   }
/*      */   
/*      */ 
/*      */   protected JavaType _fromVariable(ClassStack context, TypeVariable<?> var, TypeBindings bindings)
/*      */   {
/* 1401 */     String name = var.getName();
/* 1402 */     JavaType type = bindings.findBoundType(name);
/* 1403 */     if (type != null) {
/* 1404 */       return type;
/*      */     }
/*      */     
/*      */ 
/* 1408 */     if (bindings.hasUnbound(name)) {
/* 1409 */       return CORE_TYPE_OBJECT;
/*      */     }
/* 1411 */     bindings = bindings.withUnboundVariable(name);
/*      */     
/* 1413 */     Type[] bounds = var.getBounds();
/* 1414 */     return _fromAny(context, bounds[0], bindings);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected JavaType _fromWildcard(ClassStack context, WildcardType type, TypeBindings bindings)
/*      */   {
/* 1424 */     return _fromAny(context, type.getUpperBounds()[0], bindings);
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\type\TypeFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */