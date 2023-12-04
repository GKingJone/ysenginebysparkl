/*     */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser.util;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.BoundType;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Lists;
/*     */ import com.facebook.presto.jdbc.internal.guava.collect.Range;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.concurrent.Callable;
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
/*     */ public class RangeFactory
/*     */ {
/*     */   private static final String LEGACY_RANGES_CLASS_NAME = "com.facebook.presto.jdbc.internal.guava.collect.Ranges";
/*     */   private static final String LEGACY_RANGE_METHOD_NAME = "range";
/*     */   private static final String LEGACY_DOWN_TO_METHOD_NAME = "downTo";
/*     */   private static final String LEGACY_UP_TO_METHOD_NAME = "upTo";
/*     */   private static final String LEGACY_ALL_METHOD_NAME = "all";
/*     */   private static Method legacyRangeMethod;
/*     */   private static Method legacyDownToMethod;
/*     */   private static Method legacyUpToMethod;
/*     */   private static Method legacyAllMethod;
/*     */   
/*     */   private static void initLegacyRangeFactoryMethods()
/*     */   {
/*     */     try
/*     */     {
/*  40 */       Class<?> rangesClass = Class.forName("com.facebook.presto.jdbc.internal.guava.collect.Ranges");
/*  41 */       legacyRangeMethod = findMethod(rangesClass, "range", new Class[] { Comparable.class, BoundType.class, Comparable.class, BoundType.class });
/*     */       
/*  43 */       legacyDownToMethod = findMethod(rangesClass, "downTo", new Class[] { Comparable.class, BoundType.class });
/*  44 */       legacyUpToMethod = findMethod(rangesClass, "upTo", new Class[] { Comparable.class, BoundType.class });
/*  45 */       legacyAllMethod = findMethod(rangesClass, "all", new Class[0]);
/*     */     }
/*     */     catch (ClassNotFoundException e) {}
/*     */   }
/*     */   
/*     */ 
/*     */   private static Method findMethod(Class<?> clazz, String methodName, Class<?>... paramTypes)
/*     */   {
/*     */     try
/*     */     {
/*  55 */       return clazz.getMethod(methodName, paramTypes);
/*     */     } catch (NoSuchMethodException e) {}
/*  57 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> open(C lowerEndpoint, C upperEndpoint)
/*     */   {
/*  63 */     return range(lowerEndpoint, BoundType.OPEN, upperEndpoint, BoundType.OPEN);
/*     */   }
/*     */   
/*     */   public static <C extends Comparable<?>> Range<C> openClosed(C lowerEndpoint, C upperEndpoint)
/*     */   {
/*  68 */     return range(lowerEndpoint, BoundType.OPEN, upperEndpoint, BoundType.CLOSED);
/*     */   }
/*     */   
/*     */   public static <C extends Comparable<?>> Range<C> closedOpen(C lowerEndpoint, C upperEndpoint)
/*     */   {
/*  73 */     return range(lowerEndpoint, BoundType.CLOSED, upperEndpoint, BoundType.OPEN);
/*     */   }
/*     */   
/*     */   public static <C extends Comparable<?>> Range<C> closed(C lowerEndpoint, C upperEndpoint)
/*     */   {
/*  78 */     return range(lowerEndpoint, BoundType.CLOSED, upperEndpoint, BoundType.CLOSED);
/*     */   }
/*     */   
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> range(C lowerEndpoint, final BoundType lowerBoundType, final C upperEndpoint, final BoundType upperBoundType)
/*     */   {
/*  84 */     createRange(new Callable()
/*     */     {
/*     */ 
/*  87 */       public Range<C> call() throws Exception { return Range.range(this.val$lowerEndpoint, lowerBoundType, upperEndpoint, upperBoundType); } }, legacyRangeMethod, new Object[] { lowerEndpoint, lowerBoundType, upperEndpoint, upperBoundType });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> greaterThan(C lowerEndpoint)
/*     */   {
/*  94 */     return downTo(lowerEndpoint, BoundType.OPEN);
/*     */   }
/*     */   
/*     */   public static <C extends Comparable<?>> Range<C> atLeast(C lowerEndpoint)
/*     */   {
/*  99 */     return downTo(lowerEndpoint, BoundType.CLOSED);
/*     */   }
/*     */   
/*     */   public static <C extends Comparable<?>> Range<C> downTo(C lowerEndpoint, final BoundType lowerBoundType)
/*     */   {
/* 104 */     createRange(new Callable()
/*     */     {
/*     */ 
/* 107 */       public Range<C> call() throws Exception { return Range.downTo(this.val$lowerEndpoint, lowerBoundType); } }, legacyDownToMethod, new Object[] { lowerEndpoint, lowerBoundType });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> lessThan(C upperEndpoint)
/*     */   {
/* 114 */     return upTo(upperEndpoint, BoundType.OPEN);
/*     */   }
/*     */   
/*     */   public static <C extends Comparable<?>> Range<C> atMost(C upperEndpoint)
/*     */   {
/* 119 */     return upTo(upperEndpoint, BoundType.CLOSED);
/*     */   }
/*     */   
/*     */   public static <C extends Comparable<?>> Range<C> upTo(C upperEndpoint, final BoundType upperBoundType)
/*     */   {
/* 124 */     createRange(new Callable()
/*     */     {
/*     */ 
/* 127 */       public Range<C> call() throws Exception { return Range.upTo(this.val$upperEndpoint, upperBoundType); } }, legacyUpToMethod, new Object[] { upperEndpoint, upperBoundType });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> all()
/*     */   {
/* 134 */     createRange(new Callable()
/*     */     {
/*     */ 
/* 137 */       public Range<C> call() throws Exception { return Range.all(); } }, legacyAllMethod, new Object[0]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> singleton(C value)
/*     */   {
/* 144 */     createRange(new Callable()
/*     */     {
/*     */ 
/* 147 */       public Range<C> call() throws Exception { return Range.singleton(this.val$value); } }, legacyRangeMethod, new Object[] { value, BoundType.CLOSED, value, BoundType.CLOSED });
/*     */   }
/*     */   
/*     */ 
/*     */   private static <C extends Comparable<?>> Range<C> createRange(Callable<Range<C>> rangeCallable, Method legacyRangeFactoryMethod, Object... params)
/*     */   {
/*     */     try
/*     */     {
/* 155 */       return (Range)rangeCallable.call();
/*     */     } catch (NoSuchMethodError noSuchMethodError) {
/* 157 */       if (legacyRangeFactoryMethod != null) {
/* 158 */         return invokeLegacyRangeFactoryMethod(legacyRangeFactoryMethod, params);
/*     */       }
/* 160 */       throw noSuchMethodError;
/*     */     }
/*     */     catch (Exception e) {
/* 163 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static <C extends Comparable<?>> Range<C> invokeLegacyRangeFactoryMethod(Method method, Object... params)
/*     */   {
/*     */     try
/*     */     {
/* 172 */       return (Range)method.invoke(null, params);
/*     */     } catch (Exception e) {
/* 174 */       throw new RuntimeException("Failed to invoke legacy Range factory method [" + method.getName() + "] with params " + Lists.newArrayList(params) + ".", e);
/*     */     }
/*     */   }
/*     */   
/*     */   static {}
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\deser\util\RangeFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */