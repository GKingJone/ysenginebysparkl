/*    */ package com.facebook.presto.jdbc.internal.guava.base;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import java.lang.ref.WeakReference;
/*    */ import java.util.Map;
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
/*    */ @GwtCompatible(emulated=true)
/*    */ final class Platform
/*    */ {
/*    */   static long systemNanoTime()
/*    */   {
/* 34 */     return System.nanoTime();
/*    */   }
/*    */   
/*    */   static CharMatcher precomputeCharMatcher(CharMatcher matcher) {
/* 38 */     return matcher.precomputedInternal();
/*    */   }
/*    */   
/*    */   static <T extends Enum<T>> Optional<T> getEnumIfPresent(Class<T> enumClass, String value) {
/* 42 */     WeakReference<? extends Enum<?>> ref = (WeakReference)Enums.getEnumConstants(enumClass).get(value);
/* 43 */     return ref == null ? Optional.absent() : Optional.of(enumClass.cast(ref.get()));
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\base\Platform.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */