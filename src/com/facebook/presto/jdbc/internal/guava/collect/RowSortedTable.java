package com.facebook.presto.jdbc.internal.guava.collect;

import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

@GwtCompatible
@Beta
public abstract interface RowSortedTable<R, C, V>
  extends Table<R, C, V>
{
  public abstract SortedSet<R> rowKeySet();
  
  public abstract SortedMap<R, Map<C, V>> rowMap();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\RowSortedTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */