package com.facebook.presto.jdbc.internal.guava.collect;

import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import javax.annotation.Nullable;

@GwtCompatible
public abstract interface SortedSetMultimap<K, V>
  extends SetMultimap<K, V>
{
  public abstract SortedSet<V> get(@Nullable K paramK);
  
  public abstract SortedSet<V> removeAll(@Nullable Object paramObject);
  
  public abstract SortedSet<V> replaceValues(K paramK, Iterable<? extends V> paramIterable);
  
  public abstract Map<K, Collection<V>> asMap();
  
  public abstract Comparator<? super V> valueComparator();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\SortedSetMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */