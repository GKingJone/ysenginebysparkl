package com.facebook.presto.jdbc.internal.guava.collect;

import com.facebook.presto.jdbc.internal.guava.annotations.Beta;

@Beta
public abstract interface Interner<E>
{
  public abstract E intern(E paramE);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\collect\Interner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */