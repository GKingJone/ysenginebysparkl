/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @GwtCompatible
/*    */ public abstract class ForwardingListMultimap<K, V>
/*    */   extends ForwardingMultimap<K, V>
/*    */   implements ListMultimap<K, V>
/*    */ {
/*    */   protected abstract ListMultimap<K, V> delegate();
/*    */   
/*    */   public List<V> get(@Nullable K key)
/*    */   {
/* 44 */     return delegate().get(key);
/*    */   }
/*    */   
/*    */   public List<V> removeAll(@Nullable Object key) {
/* 48 */     return delegate().removeAll(key);
/*    */   }
/*    */   
/*    */   public List<V> replaceValues(K key, Iterable<? extends V> values) {
/* 52 */     return delegate().replaceValues(key, values);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\ForwardingListMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */