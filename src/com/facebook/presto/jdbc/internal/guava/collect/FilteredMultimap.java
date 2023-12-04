package com.facebook.presto.jdbc.internal.guava.collect;

import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
import com.facebook.presto.jdbc.internal.guava.base.Predicate;
import java.util.Map.Entry;

@GwtCompatible
abstract interface FilteredMultimap<K, V>
  extends Multimap<K, V>
{
  public abstract Multimap<K, V> unfiltered();
  
  public abstract Predicate<? super Map.Entry<K, V>> entryPredicate();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\FilteredMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */