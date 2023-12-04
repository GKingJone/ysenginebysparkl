/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Function;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Predicate;
/*    */ import java.lang.reflect.Array;
/*    */ import java.util.Collections;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.NavigableMap;
/*    */ import java.util.NavigableSet;
/*    */ import java.util.Set;
/*    */ import java.util.SortedMap;
/*    */ import java.util.SortedSet;
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
/*    */ 
/*    */ @GwtCompatible(emulated=true)
/*    */ final class Platform
/*    */ {
/*    */   static <T> T[] newArray(T[] reference, int length)
/*    */   {
/* 48 */     Class<?> type = reference.getClass().getComponentType();
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 53 */     T[] result = (Object[])Array.newInstance(type, length);
/* 54 */     return result;
/*    */   }
/*    */   
/*    */   static <E> Set<E> newSetFromMap(Map<E, Boolean> map) {
/* 58 */     return Collections.newSetFromMap(map);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   static MapMaker tryWeakKeys(MapMaker mapMaker)
/*    */   {
/* 68 */     return mapMaker.weakKeys();
/*    */   }
/*    */   
/*    */ 
/*    */   static <K, V1, V2> SortedMap<K, V2> mapsTransformEntriesSortedMap(SortedMap<K, V1> fromMap, Maps.EntryTransformer<? super K, ? super V1, V2> transformer)
/*    */   {
/* 74 */     return (fromMap instanceof NavigableMap) ? Maps.transformEntries((NavigableMap)fromMap, transformer) : Maps.transformEntriesIgnoreNavigable(fromMap, transformer);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   static <K, V> SortedMap<K, V> mapsAsMapSortedSet(SortedSet<K> set, Function<? super K, V> function)
/*    */   {
/* 81 */     return (set instanceof NavigableSet) ? Maps.asMap((NavigableSet)set, function) : Maps.asMapSortedIgnoreNavigable(set, function);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   static <E> SortedSet<E> setsFilterSortedSet(SortedSet<E> set, Predicate<? super E> predicate)
/*    */   {
/* 88 */     return (set instanceof NavigableSet) ? Sets.filter((NavigableSet)set, predicate) : Sets.filterSortedIgnoreNavigable(set, predicate);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   static <K, V> SortedMap<K, V> mapsFilterSortedMap(SortedMap<K, V> map, Predicate<? super Entry<K, V>> predicate)
/*    */   {
/* 95 */     return (map instanceof NavigableMap) ? Maps.filterEntries((NavigableMap)map, predicate) : Maps.filterSortedIgnoreNavigable(map, predicate);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\Platform.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */