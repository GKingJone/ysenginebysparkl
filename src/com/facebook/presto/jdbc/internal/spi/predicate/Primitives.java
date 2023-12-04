/*     */ package com.facebook.presto.jdbc.internal.spi.predicate;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
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
/*     */ final class Primitives
/*     */ {
/*     */   private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER_TYPE;
/*     */   private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE_TYPE;
/*     */   
/*     */   static
/*     */   {
/*  50 */     Map<Class<?>, Class<?>> primToWrap = new HashMap(16);
/*  51 */     Map<Class<?>, Class<?>> wrapToPrim = new HashMap(16);
/*     */     
/*  53 */     add(primToWrap, wrapToPrim, Boolean.TYPE, Boolean.class);
/*  54 */     add(primToWrap, wrapToPrim, Byte.TYPE, Byte.class);
/*  55 */     add(primToWrap, wrapToPrim, Character.TYPE, Character.class);
/*  56 */     add(primToWrap, wrapToPrim, Double.TYPE, Double.class);
/*  57 */     add(primToWrap, wrapToPrim, Float.TYPE, Float.class);
/*  58 */     add(primToWrap, wrapToPrim, Integer.TYPE, Integer.class);
/*  59 */     add(primToWrap, wrapToPrim, Long.TYPE, Long.class);
/*  60 */     add(primToWrap, wrapToPrim, Short.TYPE, Short.class);
/*  61 */     add(primToWrap, wrapToPrim, Void.TYPE, Void.class);
/*     */     
/*  63 */     PRIMITIVE_TO_WRAPPER_TYPE = Collections.unmodifiableMap(primToWrap);
/*  64 */     WRAPPER_TO_PRIMITIVE_TYPE = Collections.unmodifiableMap(wrapToPrim);
/*     */   }
/*     */   
/*     */ 
/*     */   private static void add(Map<Class<?>, Class<?>> forward, Map<Class<?>, Class<?>> backward, Class<?> key, Class<?> value)
/*     */   {
/*  70 */     forward.put(key, value);
/*  71 */     backward.put(value, key);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Set<Class<?>> allPrimitiveTypes()
/*     */   {
/*  83 */     return PRIMITIVE_TO_WRAPPER_TYPE.keySet();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Set<Class<?>> allWrapperTypes()
/*     */   {
/*  94 */     return WRAPPER_TO_PRIMITIVE_TYPE.keySet();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isWrapperType(Class<?> type)
/*     */   {
/* 105 */     return WRAPPER_TO_PRIMITIVE_TYPE.containsKey(Objects.requireNonNull(type));
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
/*     */   public static <T> Class<T> wrap(Class<T> type)
/*     */   {
/* 119 */     Objects.requireNonNull(type);
/*     */     
/*     */ 
/*     */ 
/* 123 */     Class<T> wrapped = (Class)PRIMITIVE_TO_WRAPPER_TYPE.get(type);
/* 124 */     return wrapped == null ? type : wrapped;
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
/*     */   public static <T> Class<T> unwrap(Class<T> type)
/*     */   {
/* 138 */     Objects.requireNonNull(type);
/*     */     
/*     */ 
/*     */ 
/* 142 */     Class<T> unwrapped = (Class)WRAPPER_TO_PRIMITIVE_TYPE.get(type);
/* 143 */     return unwrapped == null ? type : unwrapped;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\predicate\Primitives.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */