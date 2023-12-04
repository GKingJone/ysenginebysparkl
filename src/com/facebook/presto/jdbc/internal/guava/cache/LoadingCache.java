package com.facebook.presto.jdbc.internal.guava.cache;

import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
import com.facebook.presto.jdbc.internal.guava.base.Function;
import com.facebook.presto.jdbc.internal.guava.collect.ImmutableMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

@Beta
@GwtCompatible
public abstract interface LoadingCache<K, V>
  extends Cache<K, V>, Function<K, V>
{
  public abstract V get(K paramK)
    throws ExecutionException;
  
  public abstract V getUnchecked(K paramK);
  
  public abstract ImmutableMap<K, V> getAll(Iterable<? extends K> paramIterable)
    throws ExecutionException;
  
  @Deprecated
  public abstract V apply(K paramK);
  
  public abstract void refresh(K paramK);
  
  public abstract ConcurrentMap<K, V> asMap();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\cache\LoadingCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */