/*     */ package com.facebook.presto.jdbc.internal.guava.cache;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.annotations.GwtIncompatible;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Function;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Preconditions;
/*     */ import com.facebook.presto.jdbc.internal.guava.base.Supplier;
/*     */ import com.facebook.presto.jdbc.internal.guava.util.concurrent.Futures;
/*     */ import com.facebook.presto.jdbc.internal.guava.util.concurrent.ListenableFuture;
/*     */ import com.facebook.presto.jdbc.internal.guava.util.concurrent.ListenableFutureTask;
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.Executor;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ public abstract class CacheLoader<K, V>
/*     */ {
/*     */   public abstract V load(K paramK)
/*     */     throws Exception;
/*     */   
/*     */   @GwtIncompatible("Futures")
/*     */   public ListenableFuture<V> reload(K key, V oldValue)
/*     */     throws Exception
/*     */   {
/*  95 */     Preconditions.checkNotNull(key);
/*  96 */     Preconditions.checkNotNull(oldValue);
/*  97 */     return Futures.immediateFuture(load(key));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Map<K, V> loadAll(Iterable<? extends K> keys)
/*     */     throws Exception
/*     */   {
/* 125 */     throw new UnsupportedLoadingOperationException();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Beta
/*     */   public static <K, V> CacheLoader<K, V> from(Function<K, V> function)
/*     */   {
/* 138 */     return new FunctionToCacheLoader(function);
/*     */   }
/*     */   
/*     */   private static final class FunctionToCacheLoader<K, V> extends CacheLoader<K, V> implements Serializable {
/*     */     private final Function<K, V> computingFunction;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     public FunctionToCacheLoader(Function<K, V> computingFunction) {
/* 146 */       this.computingFunction = ((Function)Preconditions.checkNotNull(computingFunction));
/*     */     }
/*     */     
/*     */     public V load(K key)
/*     */     {
/* 151 */       return (V)this.computingFunction.apply(Preconditions.checkNotNull(key));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Beta
/*     */   public static <V> CacheLoader<Object, V> from(Supplier<V> supplier)
/*     */   {
/* 168 */     return new SupplierToCacheLoader(supplier);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Beta
/*     */   @GwtIncompatible("Executor + Futures")
/*     */   public static <K, V> CacheLoader<K, V> asyncReloading(CacheLoader<K, V> loader, final Executor executor)
/*     */   {
/* 184 */     Preconditions.checkNotNull(loader);
/* 185 */     Preconditions.checkNotNull(executor);
/* 186 */     new CacheLoader()
/*     */     {
/*     */       public V load(K key) throws Exception {
/* 189 */         return (V)this.val$loader.load(key);
/*     */       }
/*     */       
/*     */       public ListenableFuture<V> reload(final K key, final V oldValue) throws Exception
/*     */       {
/* 194 */         ListenableFutureTask<V> task = ListenableFutureTask.create(new Callable()
/*     */         {
/*     */           public V call() throws Exception {
/* 197 */             return (V)CacheLoader.1.this.val$loader.reload(key, oldValue).get();
/*     */           }
/* 199 */         });
/* 200 */         executor.execute(task);
/* 201 */         return task;
/*     */       }
/*     */       
/*     */       public Map<K, V> loadAll(Iterable<? extends K> keys) throws Exception
/*     */       {
/* 206 */         return this.val$loader.loadAll(keys);
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */   private static final class SupplierToCacheLoader<V> extends CacheLoader<Object, V> implements Serializable {
/*     */     private final Supplier<V> computingSupplier;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     public SupplierToCacheLoader(Supplier<V> computingSupplier) {
/* 216 */       this.computingSupplier = ((Supplier)Preconditions.checkNotNull(computingSupplier));
/*     */     }
/*     */     
/*     */     public V load(Object key)
/*     */     {
/* 221 */       Preconditions.checkNotNull(key);
/* 222 */       return (V)this.computingSupplier.get();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   static final class UnsupportedLoadingOperationException
/*     */     extends UnsupportedOperationException
/*     */   {}
/*     */   
/*     */ 
/*     */   public static final class InvalidCacheLoadException
/*     */     extends RuntimeException
/*     */   {
/*     */     public InvalidCacheLoadException(String message)
/*     */     {
/* 237 */       super();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\cache\CacheLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */