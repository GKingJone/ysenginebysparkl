/*      */ package com.facebook.presto.jdbc.internal.jackson.databind.util;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*      */ import java.io.IOException;
/*      */ import java.lang.annotation.Annotation;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.Member;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.Collection;
/*      */ import java.util.List;
/*      */ 
/*      */ public final class ClassUtil
/*      */ {
/*   15 */   private static final Class<?> CLS_OBJECT = Object.class;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final class EmptyIterator<T>
/*      */     implements java.util.Iterator<T>
/*      */   {
/*   28 */     public boolean hasNext() { return false; }
/*   29 */     public T next() { throw new java.util.NoSuchElementException(); }
/*   30 */     public void remove() { throw new UnsupportedOperationException(); }
/*      */   }
/*      */   
/*   33 */   private static final EmptyIterator<?> EMPTY_ITERATOR = new EmptyIterator(null);
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static <T> java.util.Iterator<T> emptyIterator()
/*      */   {
/*   48 */     return EMPTY_ITERATOR;
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
/*      */   public static List<JavaType> findSuperTypes(JavaType type, Class<?> endBefore, boolean addClassItself)
/*      */   {
/*   73 */     if ((type == null) || (type.hasRawClass(endBefore)) || (type.hasRawClass(Object.class))) {
/*   74 */       return java.util.Collections.emptyList();
/*      */     }
/*   76 */     List<JavaType> result = new java.util.ArrayList(8);
/*   77 */     _addSuperTypes(type, endBefore, result, addClassItself);
/*   78 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static List<Class<?>> findRawSuperTypes(Class<?> cls, Class<?> endBefore, boolean addClassItself)
/*      */   {
/*   85 */     if ((cls == null) || (cls == endBefore) || (cls == Object.class)) {
/*   86 */       return java.util.Collections.emptyList();
/*      */     }
/*   88 */     List<Class<?>> result = new java.util.ArrayList(8);
/*   89 */     _addRawSuperTypes(cls, endBefore, result, addClassItself);
/*   90 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static List<Class<?>> findSuperClasses(Class<?> cls, Class<?> endBefore, boolean addClassItself)
/*      */   {
/*  102 */     List<Class<?>> result = new java.util.LinkedList();
/*  103 */     if ((cls != null) && (cls != endBefore)) {
/*  104 */       if (addClassItself) {
/*  105 */         result.add(cls);
/*      */       }
/*  107 */       while (((cls = cls.getSuperclass()) != null) && 
/*  108 */         (cls != endBefore))
/*      */       {
/*      */ 
/*  111 */         result.add(cls);
/*      */       }
/*      */     }
/*  114 */     return result;
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public static List<Class<?>> findSuperTypes(Class<?> cls, Class<?> endBefore) {
/*  119 */     return findSuperTypes(cls, endBefore, new java.util.ArrayList(8));
/*      */   }
/*      */   
/*      */   @Deprecated
/*      */   public static List<Class<?>> findSuperTypes(Class<?> cls, Class<?> endBefore, List<Class<?>> result) {
/*  124 */     _addRawSuperTypes(cls, endBefore, result, false);
/*  125 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */   private static void _addSuperTypes(JavaType type, Class<?> endBefore, Collection<JavaType> result, boolean addClassItself)
/*      */   {
/*  131 */     if (type == null) {
/*  132 */       return;
/*      */     }
/*  134 */     Class<?> cls = type.getRawClass();
/*  135 */     if ((cls == endBefore) || (cls == Object.class)) return;
/*  136 */     if (addClassItself) {
/*  137 */       if (result.contains(type)) {
/*  138 */         return;
/*      */       }
/*  140 */       result.add(type);
/*      */     }
/*  142 */     for (JavaType intCls : type.getInterfaces()) {
/*  143 */       _addSuperTypes(intCls, endBefore, result, true);
/*      */     }
/*  145 */     _addSuperTypes(type.getSuperClass(), endBefore, result, true);
/*      */   }
/*      */   
/*      */   private static void _addRawSuperTypes(Class<?> cls, Class<?> endBefore, Collection<Class<?>> result, boolean addClassItself) {
/*  149 */     if ((cls == endBefore) || (cls == null) || (cls == Object.class)) return;
/*  150 */     if (addClassItself) {
/*  151 */       if (result.contains(cls)) {
/*  152 */         return;
/*      */       }
/*  154 */       result.add(cls);
/*      */     }
/*  156 */     for (Class<?> intCls : _interfaces(cls)) {
/*  157 */       _addRawSuperTypes(intCls, endBefore, result, true);
/*      */     }
/*  159 */     _addRawSuperTypes(cls.getSuperclass(), endBefore, result, true);
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
/*      */   public static String canBeABeanType(Class<?> type)
/*      */   {
/*  175 */     if (type.isAnnotation()) {
/*  176 */       return "annotation";
/*      */     }
/*  178 */     if (type.isArray()) {
/*  179 */       return "array";
/*      */     }
/*  181 */     if (type.isEnum()) {
/*  182 */       return "enum";
/*      */     }
/*  184 */     if (type.isPrimitive()) {
/*  185 */       return "primitive";
/*      */     }
/*      */     
/*      */ 
/*  189 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static String isLocalType(Class<?> type, boolean allowNonStatic)
/*      */   {
/*      */     try
/*      */     {
/*  200 */       if (hasEnclosingMethod(type)) {
/*  201 */         return "local/anonymous";
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  208 */       if ((!allowNonStatic) && 
/*  209 */         (!java.lang.reflect.Modifier.isStatic(type.getModifiers())) && 
/*  210 */         (getEnclosingClass(type) != null)) {
/*  211 */         return "non-static member class";
/*      */       }
/*      */     }
/*      */     catch (SecurityException e) {}catch (NullPointerException e) {}
/*      */     
/*      */ 
/*      */ 
/*  218 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Class<?> getOuterClass(Class<?> type)
/*      */   {
/*      */     try
/*      */     {
/*  229 */       if (hasEnclosingMethod(type)) {
/*  230 */         return null;
/*      */       }
/*  232 */       if (!java.lang.reflect.Modifier.isStatic(type.getModifiers())) {
/*  233 */         return getEnclosingClass(type);
/*      */       }
/*      */     } catch (SecurityException e) {}
/*  236 */     return null;
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
/*      */   public static boolean isProxyType(Class<?> type)
/*      */   {
/*  254 */     String name = type.getName();
/*      */     
/*  256 */     if ((name.startsWith("net.sf.cglib.proxy.")) || (name.startsWith("org.hibernate.proxy.")))
/*      */     {
/*  258 */       return true;
/*      */     }
/*      */     
/*  261 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isConcrete(Class<?> type)
/*      */   {
/*  270 */     int mod = type.getModifiers();
/*  271 */     return (mod & 0x600) == 0;
/*      */   }
/*      */   
/*      */   public static boolean isConcrete(Member member)
/*      */   {
/*  276 */     int mod = member.getModifiers();
/*  277 */     return (mod & 0x600) == 0;
/*      */   }
/*      */   
/*      */   public static boolean isCollectionMapOrArray(Class<?> type)
/*      */   {
/*  282 */     if (type.isArray()) return true;
/*  283 */     if (Collection.class.isAssignableFrom(type)) return true;
/*  284 */     if (java.util.Map.class.isAssignableFrom(type)) return true;
/*  285 */     return false;
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
/*      */   public static String getClassDescription(Object classOrInstance)
/*      */   {
/*  301 */     if (classOrInstance == null) {
/*  302 */       return "unknown";
/*      */     }
/*  304 */     Class<?> cls = (classOrInstance instanceof Class) ? (Class)classOrInstance : classOrInstance.getClass();
/*      */     
/*  306 */     return cls.getName();
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
/*      */   @Deprecated
/*      */   public static Class<?> findClass(String className)
/*      */     throws ClassNotFoundException
/*      */   {
/*  322 */     if (className.indexOf('.') < 0) {
/*  323 */       if ("int".equals(className)) return Integer.TYPE;
/*  324 */       if ("long".equals(className)) return Long.TYPE;
/*  325 */       if ("float".equals(className)) return Float.TYPE;
/*  326 */       if ("double".equals(className)) return Double.TYPE;
/*  327 */       if ("boolean".equals(className)) return Boolean.TYPE;
/*  328 */       if ("byte".equals(className)) return Byte.TYPE;
/*  329 */       if ("char".equals(className)) return Character.TYPE;
/*  330 */       if ("short".equals(className)) return Short.TYPE;
/*  331 */       if ("void".equals(className)) { return Void.TYPE;
/*      */       }
/*      */     }
/*  334 */     Throwable prob = null;
/*  335 */     ClassLoader loader = Thread.currentThread().getContextClassLoader();
/*      */     
/*  337 */     if (loader != null) {
/*      */       try {
/*  339 */         return Class.forName(className, true, loader);
/*      */       } catch (Exception e) {
/*  341 */         prob = getRootCause(e);
/*      */       }
/*      */     }
/*      */     try {
/*  345 */       return Class.forName(className);
/*      */     } catch (Exception e) {
/*  347 */       if (prob == null) {
/*  348 */         prob = getRootCause(e);
/*      */       }
/*      */       
/*  351 */       if ((prob instanceof RuntimeException)) {
/*  352 */         throw ((RuntimeException)prob);
/*      */       }
/*  354 */       throw new ClassNotFoundException(prob.getMessage(), prob);
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
/*  369 */   private static final LRUMap<Class<?>, ClassMetadata> sCached = new LRUMap(48, 48);
/*      */   
/*      */ 
/*      */ 
/*      */   public static String getPackageName(Class<?> cls)
/*      */   {
/*  375 */     return _getMetadata(cls).getPackageName();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static boolean hasEnclosingMethod(Class<?> cls)
/*      */   {
/*  382 */     return _getMetadata(cls).hasEnclosingMethod();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static Field[] getDeclaredFields(Class<?> cls)
/*      */   {
/*  389 */     return _getMetadata(cls).getDeclaredFields();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static Method[] getDeclaredMethods(Class<?> cls)
/*      */   {
/*  396 */     return _getMetadata(cls).getDeclaredMethods();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static Annotation[] findClassAnnotations(Class<?> cls)
/*      */   {
/*  403 */     return _getMetadata(cls).getDeclaredAnnotations();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static Ctor[] getConstructors(Class<?> cls)
/*      */   {
/*  410 */     return _getMetadata(cls).getConstructors();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Class<?> getDeclaringClass(Class<?> cls)
/*      */   {
/*  421 */     return isObjectOrPrimitive(cls) ? null : cls.getDeclaringClass();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static java.lang.reflect.Type getGenericSuperclass(Class<?> cls)
/*      */   {
/*  428 */     return cls.getGenericSuperclass();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static java.lang.reflect.Type[] getGenericInterfaces(Class<?> cls)
/*      */   {
/*  435 */     return _getMetadata(cls).getGenericInterfaces();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Class<?> getEnclosingClass(Class<?> cls)
/*      */   {
/*  443 */     return isObjectOrPrimitive(cls) ? null : cls.getEnclosingClass();
/*      */   }
/*      */   
/*      */   private static Class<?>[] _interfaces(Class<?> cls)
/*      */   {
/*  448 */     return _getMetadata(cls).getInterfaces();
/*      */   }
/*      */   
/*      */   private static ClassMetadata _getMetadata(Class<?> cls)
/*      */   {
/*  453 */     ClassMetadata md = (ClassMetadata)sCached.get(cls);
/*  454 */     if (md == null) {
/*  455 */       md = new ClassMetadata(cls);
/*      */       
/*      */ 
/*  458 */       ClassMetadata old = (ClassMetadata)sCached.putIfAbsent(cls, md);
/*  459 */       if (old != null) {
/*  460 */         md = old;
/*      */       }
/*      */     }
/*  463 */     return md;
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
/*      */   @Deprecated
/*      */   public static boolean hasGetterSignature(Method m)
/*      */   {
/*  479 */     if (java.lang.reflect.Modifier.isStatic(m.getModifiers())) {
/*  480 */       return false;
/*      */     }
/*      */     
/*  483 */     Class<?>[] pts = m.getParameterTypes();
/*  484 */     if ((pts != null) && (pts.length != 0)) {
/*  485 */       return false;
/*      */     }
/*      */     
/*  488 */     if (Void.TYPE == m.getReturnType()) {
/*  489 */       return false;
/*      */     }
/*      */     
/*  492 */     return true;
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
/*      */   public static Throwable getRootCause(Throwable t)
/*      */   {
/*  507 */     while (t.getCause() != null) {
/*  508 */       t = t.getCause();
/*      */     }
/*  510 */     return t;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void throwRootCause(Throwable t)
/*      */     throws Exception
/*      */   {
/*  521 */     t = getRootCause(t);
/*  522 */     if ((t instanceof Exception)) {
/*  523 */       throw ((Exception)t);
/*      */     }
/*  525 */     throw ((Error)t);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Throwable throwRootCauseIfIOE(Throwable t)
/*      */     throws IOException
/*      */   {
/*  536 */     t = getRootCause(t);
/*  537 */     if ((t instanceof IOException)) {
/*  538 */       throw ((IOException)t);
/*      */     }
/*  540 */     return t;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void throwAsIAE(Throwable t)
/*      */   {
/*  549 */     throwAsIAE(t, t.getMessage());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void throwAsIAE(Throwable t, String msg)
/*      */   {
/*  559 */     if ((t instanceof RuntimeException)) {
/*  560 */       throw ((RuntimeException)t);
/*      */     }
/*  562 */     if ((t instanceof Error)) {
/*  563 */       throw ((Error)t);
/*      */     }
/*  565 */     throw new IllegalArgumentException(msg, t);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void unwrapAndThrowAsIAE(Throwable t)
/*      */   {
/*  575 */     throwAsIAE(getRootCause(t));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static void unwrapAndThrowAsIAE(Throwable t, String msg)
/*      */   {
/*  585 */     throwAsIAE(getRootCause(t), msg);
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
/*      */   public static void closeOnFailAndThrowAsIAE(com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator g, Exception fail)
/*      */     throws IOException
/*      */   {
/*  603 */     g.disable(com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
/*      */     try {
/*  605 */       g.close();
/*      */     } catch (Exception e) {
/*  607 */       fail.addSuppressed(e);
/*      */     }
/*  609 */     if ((fail instanceof IOException)) {
/*  610 */       throw ((IOException)fail);
/*      */     }
/*  612 */     if ((fail instanceof RuntimeException)) {
/*  613 */       throw ((RuntimeException)fail);
/*      */     }
/*  615 */     throw new RuntimeException(fail);
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
/*      */   public static void closeOnFailAndThrowAsIAE(com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator g, java.io.Closeable toClose, Exception fail)
/*      */     throws IOException
/*      */   {
/*  631 */     if (g != null) {
/*  632 */       g.disable(com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
/*      */       try {
/*  634 */         g.close();
/*      */       } catch (Exception e) {
/*  636 */         fail.addSuppressed(e);
/*      */       }
/*      */     }
/*  639 */     if (toClose != null) {
/*      */       try {
/*  641 */         toClose.close();
/*      */       } catch (Exception e) {
/*  643 */         fail.addSuppressed(e);
/*      */       }
/*      */     }
/*  646 */     if ((fail instanceof IOException)) {
/*  647 */       throw ((IOException)fail);
/*      */     }
/*  649 */     if ((fail instanceof RuntimeException)) {
/*  650 */       throw ((RuntimeException)fail);
/*      */     }
/*  652 */     throw new RuntimeException(fail);
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
/*      */   public static <T> T createInstance(Class<T> cls, boolean canFixAccess)
/*      */     throws IllegalArgumentException
/*      */   {
/*  677 */     Constructor<T> ctor = findConstructor(cls, canFixAccess);
/*  678 */     if (ctor == null) {
/*  679 */       throw new IllegalArgumentException("Class " + cls.getName() + " has no default (no arg) constructor");
/*      */     }
/*      */     try {
/*  682 */       return (T)ctor.newInstance(new Object[0]);
/*      */     } catch (Exception e) {
/*  684 */       unwrapAndThrowAsIAE(e, "Failed to instantiate class " + cls.getName() + ", problem: " + e.getMessage()); }
/*  685 */     return null;
/*      */   }
/*      */   
/*      */   public static <T> Constructor<T> findConstructor(Class<T> cls, boolean canFixAccess)
/*      */     throws IllegalArgumentException
/*      */   {
/*      */     try
/*      */     {
/*  693 */       Constructor<T> ctor = cls.getDeclaredConstructor(new Class[0]);
/*  694 */       if (canFixAccess) {
/*  695 */         checkAndFixAccess(ctor);
/*      */ 
/*      */       }
/*  698 */       else if (!java.lang.reflect.Modifier.isPublic(ctor.getModifiers())) {
/*  699 */         throw new IllegalArgumentException("Default constructor for " + cls.getName() + " is not accessible (non-public?): not allowed to try modify access via Reflection: can not instantiate type");
/*      */       }
/*      */       
/*  702 */       return ctor;
/*      */     }
/*      */     catch (NoSuchMethodException e) {}catch (Exception e)
/*      */     {
/*  706 */       unwrapAndThrowAsIAE(e, "Failed to find default constructor of class " + cls.getName() + ", problem: " + e.getMessage());
/*      */     }
/*  708 */     return null;
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
/*      */   public static Object defaultValue(Class<?> cls)
/*      */   {
/*  723 */     if (cls == Integer.TYPE) {
/*  724 */       return Integer.valueOf(0);
/*      */     }
/*  726 */     if (cls == Long.TYPE) {
/*  727 */       return Long.valueOf(0L);
/*      */     }
/*  729 */     if (cls == Boolean.TYPE) {
/*  730 */       return Boolean.FALSE;
/*      */     }
/*  732 */     if (cls == Double.TYPE) {
/*  733 */       return Double.valueOf(0.0D);
/*      */     }
/*  735 */     if (cls == Float.TYPE) {
/*  736 */       return Float.valueOf(0.0F);
/*      */     }
/*  738 */     if (cls == Byte.TYPE) {
/*  739 */       return Byte.valueOf((byte)0);
/*      */     }
/*  741 */     if (cls == Short.TYPE) {
/*  742 */       return Short.valueOf((short)0);
/*      */     }
/*  744 */     if (cls == Character.TYPE) {
/*  745 */       return Character.valueOf('\000');
/*      */     }
/*  747 */     throw new IllegalArgumentException("Class " + cls.getName() + " is not a primitive type");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Class<?> wrapperType(Class<?> primitiveType)
/*      */   {
/*  756 */     if (primitiveType == Integer.TYPE) {
/*  757 */       return Integer.class;
/*      */     }
/*  759 */     if (primitiveType == Long.TYPE) {
/*  760 */       return Long.class;
/*      */     }
/*  762 */     if (primitiveType == Boolean.TYPE) {
/*  763 */       return Boolean.class;
/*      */     }
/*  765 */     if (primitiveType == Double.TYPE) {
/*  766 */       return Double.class;
/*      */     }
/*  768 */     if (primitiveType == Float.TYPE) {
/*  769 */       return Float.class;
/*      */     }
/*  771 */     if (primitiveType == Byte.TYPE) {
/*  772 */       return Byte.class;
/*      */     }
/*  774 */     if (primitiveType == Short.TYPE) {
/*  775 */       return Short.class;
/*      */     }
/*  777 */     if (primitiveType == Character.TYPE) {
/*  778 */       return Character.class;
/*      */     }
/*  780 */     throw new IllegalArgumentException("Class " + primitiveType.getName() + " is not a primitive type");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Class<?> primitiveType(Class<?> type)
/*      */   {
/*  791 */     if (type.isPrimitive()) {
/*  792 */       return type;
/*      */     }
/*      */     
/*  795 */     if (type == Integer.class) {
/*  796 */       return Integer.TYPE;
/*      */     }
/*  798 */     if (type == Long.class) {
/*  799 */       return Long.TYPE;
/*      */     }
/*  801 */     if (type == Boolean.class) {
/*  802 */       return Boolean.TYPE;
/*      */     }
/*  804 */     if (type == Double.class) {
/*  805 */       return Double.TYPE;
/*      */     }
/*  807 */     if (type == Float.class) {
/*  808 */       return Float.TYPE;
/*      */     }
/*  810 */     if (type == Byte.class) {
/*  811 */       return Byte.TYPE;
/*      */     }
/*  813 */     if (type == Short.class) {
/*  814 */       return Short.TYPE;
/*      */     }
/*  816 */     if (type == Character.class) {
/*  817 */       return Character.TYPE;
/*      */     }
/*  819 */     return null;
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
/*      */   public static void checkAndFixAccess(Member member)
/*      */   {
/*  838 */     checkAndFixAccess(member, false);
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
/*      */   public static void checkAndFixAccess(Member member, boolean force)
/*      */   {
/*  855 */     java.lang.reflect.AccessibleObject ao = (java.lang.reflect.AccessibleObject)member;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     try
/*      */     {
/*  863 */       if ((force) || (!java.lang.reflect.Modifier.isPublic(member.getModifiers())) || (!java.lang.reflect.Modifier.isPublic(member.getDeclaringClass().getModifiers())))
/*      */       {
/*      */ 
/*  866 */         ao.setAccessible(true);
/*      */       }
/*      */       
/*      */ 
/*      */     }
/*      */     catch (SecurityException se)
/*      */     {
/*  873 */       if (!ao.isAccessible()) {
/*  874 */         Class<?> declClass = member.getDeclaringClass();
/*  875 */         throw new IllegalArgumentException("Can not access " + member + " (from class " + declClass.getName() + "; failed to set access: " + se.getMessage());
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
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Class<? extends Enum<?>> findEnumType(java.util.EnumSet<?> s)
/*      */   {
/*  896 */     if (!s.isEmpty()) {
/*  897 */       return findEnumType((Enum)s.iterator().next());
/*      */     }
/*      */     
/*  900 */     return EnumTypeLocator.instance.enumTypeFor(s);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Class<? extends Enum<?>> findEnumType(java.util.EnumMap<?, ?> m)
/*      */   {
/*  911 */     if (!m.isEmpty()) {
/*  912 */       return findEnumType((Enum)m.keySet().iterator().next());
/*      */     }
/*      */     
/*  915 */     return EnumTypeLocator.instance.enumTypeFor(m);
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
/*      */   public static Class<? extends Enum<?>> findEnumType(Enum<?> en)
/*      */   {
/*  928 */     Class<?> ec = en.getClass();
/*  929 */     if (ec.getSuperclass() != Enum.class) {
/*  930 */       ec = ec.getSuperclass();
/*      */     }
/*  932 */     return ec;
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
/*      */   public static Class<? extends Enum<?>> findEnumType(Class<?> cls)
/*      */   {
/*  945 */     if (cls.getSuperclass() != Enum.class) {
/*  946 */       cls = cls.getSuperclass();
/*      */     }
/*  948 */     return cls;
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
/*      */   public static <T extends Annotation> Enum<?> findFirstAnnotatedEnumValue(Class<Enum<?>> enumClass, Class<T> annotationClass)
/*      */   {
/*  964 */     Field[] fields = getDeclaredFields(enumClass);
/*  965 */     for (Field field : fields) {
/*  966 */       if (field.isEnumConstant()) {
/*  967 */         Annotation defaultValueAnnotation = field.getAnnotation(annotationClass);
/*  968 */         if (defaultValueAnnotation != null) {
/*  969 */           String name = field.getName();
/*  970 */           for (Enum<?> enumValue : (Enum[])enumClass.getEnumConstants()) {
/*  971 */             if (name.equals(enumValue.name())) {
/*  972 */               return enumValue;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  978 */     return null;
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
/*      */   public static boolean isJacksonStdImpl(Object impl)
/*      */   {
/*  995 */     return (impl != null) && (isJacksonStdImpl(impl.getClass()));
/*      */   }
/*      */   
/*      */   public static boolean isJacksonStdImpl(Class<?> implClass) {
/*  999 */     return implClass.getAnnotation(com.facebook.presto.jdbc.internal.jackson.databind.annotation.JacksonStdImpl.class) != null;
/*      */   }
/*      */   
/*      */   public static boolean isBogusClass(Class<?> cls) {
/* 1003 */     return (cls == Void.class) || (cls == Void.TYPE) || (cls == com.facebook.presto.jdbc.internal.jackson.databind.annotation.NoClass.class);
/*      */   }
/*      */   
/*      */   public static boolean isNonStaticInnerClass(Class<?> cls)
/*      */   {
/* 1008 */     return (!java.lang.reflect.Modifier.isStatic(cls.getModifiers())) && (getEnclosingClass(cls) != null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public static boolean isObjectOrPrimitive(Class<?> cls)
/*      */   {
/* 1016 */     return (cls == CLS_OBJECT) || (cls.isPrimitive());
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
/*      */   private static class EnumTypeLocator
/*      */   {
/* 1031 */     static final EnumTypeLocator instance = new EnumTypeLocator();
/*      */     
/*      */     private final Field enumSetTypeField;
/*      */     
/*      */     private final Field enumMapTypeField;
/*      */     
/*      */     private EnumTypeLocator()
/*      */     {
/* 1039 */       this.enumSetTypeField = locateField(java.util.EnumSet.class, "elementType", Class.class);
/* 1040 */       this.enumMapTypeField = locateField(java.util.EnumMap.class, "elementType", Class.class);
/*      */     }
/*      */     
/*      */ 
/*      */     public Class<? extends Enum<?>> enumTypeFor(java.util.EnumSet<?> set)
/*      */     {
/* 1046 */       if (this.enumSetTypeField != null) {
/* 1047 */         return (Class)get(set, this.enumSetTypeField);
/*      */       }
/* 1049 */       throw new IllegalStateException("Can not figure out type for EnumSet (odd JDK platform?)");
/*      */     }
/*      */     
/*      */ 
/*      */     public Class<? extends Enum<?>> enumTypeFor(java.util.EnumMap<?, ?> set)
/*      */     {
/* 1055 */       if (this.enumMapTypeField != null) {
/* 1056 */         return (Class)get(set, this.enumMapTypeField);
/*      */       }
/* 1058 */       throw new IllegalStateException("Can not figure out type for EnumMap (odd JDK platform?)");
/*      */     }
/*      */     
/*      */     private Object get(Object bean, Field field)
/*      */     {
/*      */       try {
/* 1064 */         return field.get(bean);
/*      */       } catch (Exception e) {
/* 1066 */         throw new IllegalArgumentException(e);
/*      */       }
/*      */     }
/*      */     
/*      */     private static Field locateField(Class<?> fromClass, String expectedName, Class<?> type)
/*      */     {
/* 1072 */       Field found = null;
/*      */       
/* 1074 */       Field[] fields = ClassUtil.getDeclaredFields(fromClass);
/* 1075 */       for (Field f : fields) {
/* 1076 */         if ((expectedName.equals(f.getName())) && (f.getType() == type)) {
/* 1077 */           found = f;
/* 1078 */           break;
/*      */         }
/*      */       }
/*      */       
/* 1082 */       if (found == null) {
/* 1083 */         for (Field f : fields) {
/* 1084 */           if (f.getType() == type)
/*      */           {
/* 1086 */             if (found != null) return null;
/* 1087 */             found = f;
/*      */           }
/*      */         }
/*      */       }
/* 1091 */       if (found != null) {
/*      */         try {
/* 1093 */           found.setAccessible(true);
/*      */         } catch (Throwable t) {}
/*      */       }
/* 1096 */       return found;
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
/*      */   private static final class ClassMetadata
/*      */   {
/* 1111 */     private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];
/* 1112 */     private static final Ctor[] NO_CTORS = new Ctor[0];
/*      */     
/*      */     private final Class<?> _forClass;
/*      */     
/*      */     private String _packageName;
/*      */     
/*      */     private Boolean _hasEnclosingMethod;
/*      */     private Class<?>[] _interfaces;
/*      */     private java.lang.reflect.Type[] _genericInterfaces;
/*      */     private Annotation[] _annotations;
/*      */     private Ctor[] _constructors;
/*      */     private Field[] _fields;
/*      */     private Method[] _methods;
/*      */     
/*      */     public ClassMetadata(Class<?> forClass)
/*      */     {
/* 1128 */       this._forClass = forClass;
/*      */     }
/*      */     
/*      */     public String getPackageName() {
/* 1132 */       String name = this._packageName;
/* 1133 */       if (name == null) {
/* 1134 */         Package pkg = this._forClass.getPackage();
/* 1135 */         name = pkg == null ? null : pkg.getName();
/* 1136 */         if (name == null) {
/* 1137 */           name = "";
/*      */         }
/* 1139 */         this._packageName = name;
/*      */       }
/* 1141 */       return name == "" ? null : name;
/*      */     }
/*      */     
/*      */ 
/*      */     public Class<?>[] getInterfaces()
/*      */     {
/* 1147 */       Class<?>[] result = this._interfaces;
/* 1148 */       if (result == null) {
/* 1149 */         result = this._forClass.getInterfaces();
/* 1150 */         this._interfaces = result;
/*      */       }
/* 1152 */       return result;
/*      */     }
/*      */     
/*      */     public java.lang.reflect.Type[] getGenericInterfaces()
/*      */     {
/* 1157 */       java.lang.reflect.Type[] result = this._genericInterfaces;
/* 1158 */       if (result == null) {
/* 1159 */         result = this._forClass.getGenericInterfaces();
/* 1160 */         this._genericInterfaces = result;
/*      */       }
/* 1162 */       return result;
/*      */     }
/*      */     
/*      */ 
/*      */     public Annotation[] getDeclaredAnnotations()
/*      */     {
/* 1168 */       Annotation[] result = this._annotations;
/* 1169 */       if (result == null) {
/* 1170 */         result = isObjectOrPrimitive() ? NO_ANNOTATIONS : this._forClass.getDeclaredAnnotations();
/* 1171 */         this._annotations = result;
/*      */       }
/* 1173 */       return result;
/*      */     }
/*      */     
/*      */ 
/*      */     public Ctor[] getConstructors()
/*      */     {
/* 1179 */       Ctor[] result = this._constructors;
/* 1180 */       if (result == null)
/*      */       {
/*      */ 
/* 1183 */         if ((this._forClass.isInterface()) || (isObjectOrPrimitive())) {
/* 1184 */           result = NO_CTORS;
/*      */         } else {
/* 1186 */           Constructor<?>[] rawCtors = this._forClass.getDeclaredConstructors();
/* 1187 */           int len = rawCtors.length;
/* 1188 */           result = new Ctor[len];
/* 1189 */           for (int i = 0; i < len; i++) {
/* 1190 */             result[i] = new Ctor(rawCtors[i]);
/*      */           }
/*      */         }
/* 1193 */         this._constructors = result;
/*      */       }
/* 1195 */       return result;
/*      */     }
/*      */     
/*      */     public Field[] getDeclaredFields()
/*      */     {
/* 1200 */       Field[] fields = this._fields;
/* 1201 */       if (fields == null) {
/* 1202 */         fields = this._forClass.getDeclaredFields();
/* 1203 */         this._fields = fields;
/*      */       }
/* 1205 */       return fields;
/*      */     }
/*      */     
/*      */     public Method[] getDeclaredMethods()
/*      */     {
/* 1210 */       Method[] methods = this._methods;
/* 1211 */       if (methods == null) {
/* 1212 */         methods = this._forClass.getDeclaredMethods();
/* 1213 */         this._methods = methods;
/*      */       }
/* 1215 */       return methods;
/*      */     }
/*      */     
/*      */ 
/*      */     public boolean hasEnclosingMethod()
/*      */     {
/* 1221 */       Boolean b = this._hasEnclosingMethod;
/* 1222 */       if (b == null) {
/* 1223 */         b = isObjectOrPrimitive() ? Boolean.FALSE : Boolean.valueOf(this._forClass.getEnclosingMethod() != null);
/* 1224 */         this._hasEnclosingMethod = b;
/*      */       }
/* 1226 */       return b.booleanValue();
/*      */     }
/*      */     
/*      */     private boolean isObjectOrPrimitive() {
/* 1230 */       return (this._forClass == ClassUtil.CLS_OBJECT) || (this._forClass.isPrimitive());
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final class Ctor
/*      */   {
/*      */     public final Constructor<?> _ctor;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private Annotation[] _annotations;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private Annotation[][] _paramAnnotations;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1258 */     private int _paramCount = -1;
/*      */     
/*      */     public Ctor(Constructor<?> ctor) {
/* 1261 */       this._ctor = ctor;
/*      */     }
/*      */     
/*      */     public Constructor<?> getConstructor() {
/* 1265 */       return this._ctor;
/*      */     }
/*      */     
/*      */     public int getParamCount() {
/* 1269 */       int c = this._paramCount;
/* 1270 */       if (c < 0) {
/* 1271 */         c = this._ctor.getParameterTypes().length;
/* 1272 */         this._paramCount = c;
/*      */       }
/* 1274 */       return c;
/*      */     }
/*      */     
/*      */     public Class<?> getDeclaringClass() {
/* 1278 */       return this._ctor.getDeclaringClass();
/*      */     }
/*      */     
/*      */     public Annotation[] getDeclaredAnnotations()
/*      */     {
/* 1283 */       Annotation[] result = this._annotations;
/* 1284 */       if (result == null) {
/* 1285 */         result = this._ctor.getDeclaredAnnotations();
/* 1286 */         this._annotations = result;
/*      */       }
/* 1288 */       return result;
/*      */     }
/*      */     
/*      */     public Annotation[][] getParameterAnnotations()
/*      */     {
/* 1293 */       Annotation[][] result = this._paramAnnotations;
/* 1294 */       if (result == null) {
/* 1295 */         result = this._ctor.getParameterAnnotations();
/* 1296 */         this._paramAnnotations = result;
/*      */       }
/* 1298 */       return result;
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\util\ClassUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */