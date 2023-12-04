/*    */ package com.facebook.presto.jdbc.internal.guava.cache;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableMap;
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.Maps;
/*    */ import com.facebook.presto.jdbc.internal.guava.util.concurrent.UncheckedExecutionException;
/*    */ import java.util.Map;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Beta
/*    */ public abstract class AbstractLoadingCache<K, V>
/*    */   extends AbstractCache<K, V>
/*    */   implements LoadingCache<K, V>
/*    */ {
/*    */   public V getUnchecked(K key)
/*    */   {
/*    */     try
/*    */     {
/* 53 */       return (V)get(key);
/*    */     } catch (ExecutionException e) {
/* 55 */       throw new UncheckedExecutionException(e.getCause());
/*    */     }
/*    */   }
/*    */   
/*    */   public ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException
/*    */   {
/* 61 */     Map<K, V> result = Maps.newLinkedHashMap();
/* 62 */     for (K key : keys) {
/* 63 */       if (!result.containsKey(key)) {
/* 64 */         result.put(key, get(key));
/*    */       }
/*    */     }
/* 67 */     return ImmutableMap.copyOf(result);
/*    */   }
/*    */   
/*    */   public final V apply(K key)
/*    */   {
/* 72 */     return (V)getUnchecked(key);
/*    */   }
/*    */   
/*    */   public void refresh(K key)
/*    */   {
/* 77 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\cache\AbstractLoadingCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */