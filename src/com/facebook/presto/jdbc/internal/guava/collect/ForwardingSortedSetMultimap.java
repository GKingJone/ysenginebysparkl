/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import java.util.Comparator;
/*    */ import java.util.SortedSet;
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
/*    */ 
/*    */ 
/*    */ @GwtCompatible
/*    */ public abstract class ForwardingSortedSetMultimap<K, V>
/*    */   extends ForwardingSetMultimap<K, V>
/*    */   implements SortedSetMultimap<K, V>
/*    */ {
/*    */   protected abstract SortedSetMultimap<K, V> delegate();
/*    */   
/*    */   public SortedSet<V> get(@Nullable K key)
/*    */   {
/* 45 */     return delegate().get(key);
/*    */   }
/*    */   
/*    */   public SortedSet<V> removeAll(@Nullable Object key) {
/* 49 */     return delegate().removeAll(key);
/*    */   }
/*    */   
/*    */   public SortedSet<V> replaceValues(K key, Iterable<? extends V> values)
/*    */   {
/* 54 */     return delegate().replaceValues(key, values);
/*    */   }
/*    */   
/*    */   public Comparator<? super V> valueComparator() {
/* 58 */     return delegate().valueComparator();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\ForwardingSortedSetMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */