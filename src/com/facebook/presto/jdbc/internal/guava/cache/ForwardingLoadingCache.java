/*    */ package com.facebook.presto.jdbc.internal.guava.cache;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*    */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableMap;
/*    */ import java.util.concurrent.ExecutionException;
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
/*    */ 
/*    */ 
/*    */ @Beta
/*    */ public abstract class ForwardingLoadingCache<K, V>
/*    */   extends ForwardingCache<K, V>
/*    */   implements LoadingCache<K, V>
/*    */ {
/*    */   protected abstract LoadingCache<K, V> delegate();
/*    */   
/*    */   public V get(K key)
/*    */     throws ExecutionException
/*    */   {
/* 48 */     return (V)delegate().get(key);
/*    */   }
/*    */   
/*    */   public V getUnchecked(K key)
/*    */   {
/* 53 */     return (V)delegate().getUnchecked(key);
/*    */   }
/*    */   
/*    */   public ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException
/*    */   {
/* 58 */     return delegate().getAll(keys);
/*    */   }
/*    */   
/*    */   public V apply(K key)
/*    */   {
/* 63 */     return (V)delegate().apply(key);
/*    */   }
/*    */   
/*    */   public void refresh(K key)
/*    */   {
/* 68 */     delegate().refresh(key);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   @Beta
/*    */   public static abstract class SimpleForwardingLoadingCache<K, V>
/*    */     extends ForwardingLoadingCache<K, V>
/*    */   {
/*    */     private final LoadingCache<K, V> delegate;
/*    */     
/*    */ 
/*    */ 
/*    */     protected SimpleForwardingLoadingCache(LoadingCache<K, V> delegate)
/*    */     {
/* 83 */       this.delegate = ((LoadingCache)Preconditions.checkNotNull(delegate));
/*    */     }
/*    */     
/*    */     protected final LoadingCache<K, V> delegate()
/*    */     {
/* 88 */       return this.delegate;
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\cache\ForwardingLoadingCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */