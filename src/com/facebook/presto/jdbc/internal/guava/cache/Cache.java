package com.facebook.presto.jdbc.internal.guava.cache;

import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
import com.facebook.presto.jdbc.internal.guava.collect.ImmutableMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
public abstract interface Cache<K, V>
{
  @Nullable
  public abstract V getIfPresent(Object paramObject);
  
  public abstract V get(K paramK, Callable<? extends V> paramCallable)
    throws ExecutionException;
  
  public abstract ImmutableMap<K, V> getAllPresent(Iterable<?> paramIterable);
  
  public abstract void put(K paramK, V paramV);
  
  public abstract void putAll(Map<? extends K, ? extends V> paramMap);
  
  public abstract void invalidate(Object paramObject);
  
  public abstract void invalidateAll(Iterable<?> paramIterable);
  
  public abstract void invalidateAll();
  
  public abstract long size();
  
  public abstract CacheStats stats();
  
  public abstract ConcurrentMap<K, V> asMap();
  
  public abstract void cleanUp();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\cache\Cache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */