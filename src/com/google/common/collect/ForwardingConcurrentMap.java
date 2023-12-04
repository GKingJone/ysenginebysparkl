/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.util.concurrent.ConcurrentMap;
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
/*    */ public abstract class ForwardingConcurrentMap<K, V>
/*    */   extends ForwardingMap<K, V>
/*    */   implements ConcurrentMap<K, V>
/*    */ {
/*    */   protected abstract ConcurrentMap<K, V> delegate();
/*    */   
/*    */   public V putIfAbsent(K key, V value)
/*    */   {
/* 43 */     return (V)delegate().putIfAbsent(key, value);
/*    */   }
/*    */   
/*    */   public boolean remove(Object key, Object value)
/*    */   {
/* 48 */     return delegate().remove(key, value);
/*    */   }
/*    */   
/*    */   public V replace(K key, V value)
/*    */   {
/* 53 */     return (V)delegate().replace(key, value);
/*    */   }
/*    */   
/*    */   public boolean replace(K key, V oldValue, V newValue)
/*    */   {
/* 58 */     return delegate().replace(key, oldValue, newValue);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\ForwardingConcurrentMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */