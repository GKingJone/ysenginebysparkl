/*     */ package com.facebook.presto.jdbc.internal.guava.base;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtIncompatible;
/*     */ import java.io.Serializable;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.EnumSet;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ import javax.annotation.Nullable;
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
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ @Beta
/*     */ public final class Enums
/*     */ {
/*     */   @GwtIncompatible("reflection")
/*     */   public static Field getField(Enum<?> enumValue)
/*     */   {
/*  57 */     Class<?> clazz = enumValue.getDeclaringClass();
/*     */     try {
/*  59 */       return clazz.getDeclaredField(enumValue.name());
/*     */     } catch (NoSuchFieldException impossible) {
/*  61 */       throw new AssertionError(impossible);
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
/*     */   public static <T extends Enum<T>> Optional<T> getIfPresent(Class<T> enumClass, String value)
/*     */   {
/*  75 */     Preconditions.checkNotNull(enumClass);
/*  76 */     Preconditions.checkNotNull(value);
/*  77 */     return Platform.getEnumIfPresent(enumClass, value);
/*     */   }
/*     */   
/*     */ 
/*     */   @GwtIncompatible("java.lang.ref.WeakReference")
/*  82 */   private static final Map<Class<? extends Enum<?>>, Map<String, WeakReference<? extends Enum<?>>>> enumConstantCache = new WeakHashMap();
/*     */   
/*     */ 
/*     */   @GwtIncompatible("java.lang.ref.WeakReference")
/*     */   private static <T extends Enum<T>> Map<String, WeakReference<? extends Enum<?>>> populateCache(Class<T> enumClass)
/*     */   {
/*  88 */     Map<String, WeakReference<? extends Enum<?>>> result = new HashMap();
/*     */     
/*  90 */     for (T enumInstance : EnumSet.allOf(enumClass)) {
/*  91 */       result.put(enumInstance.name(), new WeakReference(enumInstance));
/*     */     }
/*  93 */     enumConstantCache.put(enumClass, result);
/*  94 */     return result;
/*     */   }
/*     */   
/*     */   @GwtIncompatible("java.lang.ref.WeakReference")
/*     */   static <T extends Enum<T>> Map<String, WeakReference<? extends Enum<?>>> getEnumConstants(Class<T> enumClass)
/*     */   {
/* 100 */     synchronized (enumConstantCache) {
/* 101 */       Map<String, WeakReference<? extends Enum<?>>> constants = (Map)enumConstantCache.get(enumClass);
/*     */       
/* 103 */       if (constants == null) {
/* 104 */         constants = populateCache(enumClass);
/*     */       }
/* 106 */       return constants;
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
/*     */   public static <T extends Enum<T>> Converter<String, T> stringConverter(Class<T> enumClass)
/*     */   {
/* 119 */     return new StringConverter(enumClass);
/*     */   }
/*     */   
/*     */   private static final class StringConverter<T extends Enum<T>> extends Converter<String, T> implements Serializable
/*     */   {
/*     */     private final Class<T> enumClass;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     StringConverter(Class<T> enumClass) {
/* 128 */       this.enumClass = ((Class)Preconditions.checkNotNull(enumClass));
/*     */     }
/*     */     
/*     */     protected T doForward(String value)
/*     */     {
/* 133 */       return Enum.valueOf(this.enumClass, value);
/*     */     }
/*     */     
/*     */     protected String doBackward(T enumValue)
/*     */     {
/* 138 */       return enumValue.name();
/*     */     }
/*     */     
/*     */     public boolean equals(@Nullable Object object)
/*     */     {
/* 143 */       if ((object instanceof StringConverter)) {
/* 144 */         StringConverter<?> that = (StringConverter)object;
/* 145 */         return this.enumClass.equals(that.enumClass);
/*     */       }
/* 147 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode()
/*     */     {
/* 152 */       return this.enumClass.hashCode();
/*     */     }
/*     */     
/*     */     public String toString()
/*     */     {
/* 157 */       String str = String.valueOf(String.valueOf(this.enumClass.getName()));return 29 + str.length() + "Enums.stringConverter(" + str + ".class)";
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\base\Enums.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */