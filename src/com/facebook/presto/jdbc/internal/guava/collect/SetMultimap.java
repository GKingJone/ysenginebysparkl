package com.facebook.presto.jdbc.internal.guava.collect;

import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public abstract interface SetMultimap<K, V>
  extends Multimap<K, V>
{
  public abstract Set<V> get(@Nullable K paramK);
  
  public abstract Set<V> removeAll(@Nullable Object paramObject);
  
  public abstract Set<V> replaceValues(K paramK, Iterable<? extends V> paramIterable);
  
  public abstract Set<Entry<K, V>> entries();
  
  public abstract Map<K, Collection<V>> asMap();
  
  public abstract boolean equals(@Nullable Object paramObject);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\SetMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */