/*    */ package com.facebook.presto.jdbc.internal.guava.collect;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.Set;
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
/*    */ @GwtCompatible
/*    */ public abstract class ForwardingSetMultimap<K, V>
/*    */   extends ForwardingMultimap<K, V>
/*    */   implements SetMultimap<K, V>
/*    */ {
/*    */   protected abstract SetMultimap<K, V> delegate();
/*    */   
/*    */   public Set<Map.Entry<K, V>> entries()
/*    */   {
/* 42 */     return delegate().entries();
/*    */   }
/*    */   
/*    */   public Set<V> get(@Nullable K key) {
/* 46 */     return delegate().get(key);
/*    */   }
/*    */   
/*    */   public Set<V> removeAll(@Nullable Object key) {
/* 50 */     return delegate().removeAll(key);
/*    */   }
/*    */   
/*    */   public Set<V> replaceValues(K key, Iterable<? extends V> values) {
/* 54 */     return delegate().replaceValues(key, values);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\ForwardingSetMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */