package com.facebook.presto.jdbc.internal.guava.base;

import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;

@GwtCompatible
public abstract interface Supplier<T>
{
  public abstract T get();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\base\Supplier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */