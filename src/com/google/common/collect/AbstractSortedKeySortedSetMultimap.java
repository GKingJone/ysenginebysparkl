/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.util.Collection;
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
/*    */ @GwtCompatible
/*    */ abstract class AbstractSortedKeySortedSetMultimap<K, V>
/*    */   extends AbstractSortedSetMultimap<K, V>
/*    */ {
/*    */   AbstractSortedKeySortedSetMultimap(SortedMap<K, Collection<V>> map)
/*    */   {
/* 38 */     super(map);
/*    */   }
/*    */   
/*    */   public SortedMap<K, Collection<V>> asMap()
/*    */   {
/* 43 */     return (SortedMap)super.asMap();
/*    */   }
/*    */   
/*    */   SortedMap<K, Collection<V>> backingMap()
/*    */   {
/* 48 */     return (SortedMap)super.backingMap();
/*    */   }
/*    */   
/*    */   public SortedSet<K> keySet()
/*    */   {
/* 53 */     return (SortedSet)super.keySet();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\AbstractSortedKeySortedSetMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */