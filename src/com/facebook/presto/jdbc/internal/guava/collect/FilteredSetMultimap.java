package com.facebook.presto.jdbc.internal.guava.collect;

import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;

@GwtCompatible
abstract interface FilteredSetMultimap<K, V>
  extends FilteredMultimap<K, V>, SetMultimap<K, V>
{
  public abstract SetMultimap<K, V> unfiltered();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\FilteredSetMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */