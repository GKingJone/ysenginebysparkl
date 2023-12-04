package com.facebook.presto.jdbc.internal.guava.collect;

import java.util.SortedSet;

abstract interface SortedMultisetBridge<E>
  extends Multiset<E>
{
  public abstract SortedSet<E> elementSet();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\SortedMultisetBridge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */