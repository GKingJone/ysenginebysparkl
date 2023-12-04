package com.facebook.presto.jdbc.internal.guava.util.concurrent;

import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
public abstract interface CheckedFuture<V, X extends Exception>
  extends ListenableFuture<V>
{
  public abstract V checkedGet()
    throws Exception;
  
  public abstract V checkedGet(long paramLong, TimeUnit paramTimeUnit)
    throws TimeoutException, Exception;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\util\concurrent\CheckedFuture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */