package com.facebook.presto.jdbc.internal.guava.util.concurrent;

import com.facebook.presto.jdbc.internal.guava.annotations.Beta;

@Beta
public abstract interface FutureFallback<V>
{
  public abstract ListenableFuture<V> create(Throwable paramThrowable)
    throws Exception;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\util\concurrent\FutureFallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */