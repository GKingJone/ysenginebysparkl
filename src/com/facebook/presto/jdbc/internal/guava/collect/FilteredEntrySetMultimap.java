/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Predicate;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.Set;
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
/*    */ final class FilteredEntrySetMultimap<K, V>
/*    */   extends FilteredEntryMultimap<K, V>
/*    */   implements FilteredSetMultimap<K, V>
/*    */ {
/*    */   FilteredEntrySetMultimap(SetMultimap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> predicate)
/*    */   {
/* 35 */     super(unfiltered, predicate);
/*    */   }
/*    */   
/*    */   public SetMultimap<K, V> unfiltered()
/*    */   {
/* 40 */     return (SetMultimap)this.unfiltered;
/*    */   }
/*    */   
/*    */   public Set<V> get(K key)
/*    */   {
/* 45 */     return (Set)super.get(key);
/*    */   }
/*    */   
/*    */   public Set<V> removeAll(Object key)
/*    */   {
/* 50 */     return (Set)super.removeAll(key);
/*    */   }
/*    */   
/*    */   public Set<V> replaceValues(K key, Iterable<? extends V> values)
/*    */   {
/* 55 */     return (Set)super.replaceValues(key, values);
/*    */   }
/*    */   
/*    */   Set<Map.Entry<K, V>> createEntries()
/*    */   {
/* 60 */     return Sets.filter(unfiltered().entries(), entryPredicate());
/*    */   }
/*    */   
/*    */   public Set<Map.Entry<K, V>> entries()
/*    */   {
/* 65 */     return (Set)super.entries();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\FilteredEntrySetMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */