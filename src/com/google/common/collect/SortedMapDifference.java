package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.SortedMap;

@GwtCompatible
public abstract interface SortedMapDifference<K, V>
  extends MapDifference<K, V>
{
  public abstract SortedMap<K, V> entriesOnlyOnLeft();
  
  public abstract SortedMap<K, V> entriesOnlyOnRight();
  
  public abstract SortedMap<K, V> entriesInCommon();
  
  public abstract SortedMap<K, ValueDifference<V>> entriesDiffering();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\collect\SortedMapDifference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */