/*     */ package com.google.common.cache;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Supplier;
/*     */ import com.google.common.util.concurrent.Futures;
/*     */ import com.google.common.util.concurrent.ListenableFuture;
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
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
/*  92 */     Preconditions.checkNotNull(key);
/*  93 */     Preconditions.checkNotNull(oldValue);
/*  94 */     return Futures.immediateFuture(load(key));
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
/* 122 */     throw new UnsupportedLoadingOperationException();
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
/* 135 */     return new FunctionToCacheLoader(function);
/*     */   }
/*     */   
/*     */   private static final class FunctionToCacheLoader<K, V> extends CacheLoader<K, V> implements Serializable {
/*     */     private final Function<K, V> computingFunction;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     public FunctionToCacheLoader(Function<K, V> computingFunction) {
/* 143 */       this.computingFunction = ((Function)Preconditions.checkNotNull(computingFunction));
/*     */     }
/*     */     
/*     */     public V load(K key)
/*     */     {
/* 148 */       return (V)this.computingFunction.apply(Preconditions.checkNotNull(key));
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
/* 165 */     return new SupplierToCacheLoader(supplier);
/*     */   }
/*     */   
/*     */   private static final class SupplierToCacheLoader<V> extends CacheLoader<Object, V> implements Serializable {
/*     */     private final Supplier<V> computingSupplier;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     public SupplierToCacheLoader(Supplier<V> computingSupplier) {
/* 173 */       this.computingSupplier = ((Supplier)Preconditions.checkNotNull(computingSupplier));
/*     */     }
/*     */     
/*     */     public V load(Object key)
/*     */     {
/* 178 */       Preconditions.checkNotNull(key);
/* 179 */       return (V)this.computingSupplier.get();
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
/* 194 */       super();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\cache\CacheLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */