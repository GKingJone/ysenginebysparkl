package com.facebook.presto.jdbc.internal.guava.cache;

import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;

@GwtCompatible
abstract interface LongAddable
{
  public abstract void increment();
  
  public abstract void add(long paramLong);
  
  public abstract long sum();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\cache\LongAddable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */