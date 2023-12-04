/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Predicate;
/*    */ import java.util.List;
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
/*    */ @GwtCompatible
/*    */ final class FilteredKeyListMultimap<K, V>
/*    */   extends FilteredKeyMultimap<K, V>
/*    */   implements ListMultimap<K, V>
/*    */ {
/*    */   FilteredKeyListMultimap(ListMultimap<K, V> unfiltered, Predicate<? super K> keyPredicate)
/*    */   {
/* 35 */     super(unfiltered, keyPredicate);
/*    */   }
/*    */   
/*    */   public ListMultimap<K, V> unfiltered()
/*    */   {
/* 40 */     return (ListMultimap)super.unfiltered();
/*    */   }
/*    */   
/*    */   public List<V> get(K key)
/*    */   {
/* 45 */     return (List)super.get(key);
/*    */   }
/*    */   
/*    */   public List<V> removeAll(@Nullable Object key)
/*    */   {
/* 50 */     return (List)super.removeAll(key);
/*    */   }
/*    */   
/*    */   public List<V> replaceValues(K key, Iterable<? extends V> values)
/*    */   {
/* 55 */     return (List)super.replaceValues(key, values);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\FilteredKeyListMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */