/*    */ package com.facebook.presto.jdbc.internal.guava.base;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class Defaults
/*    */ {
/*    */   private static final Map<Class<?>, Object> DEFAULTS;
/*    */   
/*    */   static
/*    */   {
/* 40 */     Map<Class<?>, Object> map = new HashMap();
/* 41 */     put(map, Boolean.TYPE, Boolean.valueOf(false));
/* 42 */     put(map, Character.TYPE, Character.valueOf('\000'));
/* 43 */     put(map, Byte.TYPE, Byte.valueOf((byte)0));
/* 44 */     put(map, Short.TYPE, Short.valueOf((short)0));
/* 45 */     put(map, Integer.TYPE, Integer.valueOf(0));
/* 46 */     put(map, Long.TYPE, Long.valueOf(0L));
/* 47 */     put(map, Float.TYPE, Float.valueOf(0.0F));
/* 48 */     put(map, Double.TYPE, Double.valueOf(0.0D));
/* 49 */     DEFAULTS = Collections.unmodifiableMap(map);
/*    */   }
/*    */   
/*    */   private static <T> void put(Map<Class<?>, Object> map, Class<T> type, T value) {
/* 53 */     map.put(type, value);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   @Nullable
/*    */   public static <T> T defaultValue(Class<T> type)
/*    */   {
/* 65 */     T t = DEFAULTS.get(Preconditions.checkNotNull(type));
/* 66 */     return t;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\base\Defaults.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */