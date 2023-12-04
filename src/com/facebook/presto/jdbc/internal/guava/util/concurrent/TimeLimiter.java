package com.facebook.presto.jdbc.internal.guava.util.concurrent;

import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Beta
public abstract interface TimeLimiter
{
  public abstract <T> T newProxy(T paramT, Class<T> paramClass, long paramLong, TimeUnit paramTimeUnit);
  
  public abstract <T> T callWithTimeout(Callable<T> paramCallable, long paramLong, TimeUnit paramTimeUnit, boolean paramBoolean)
    throws Exception;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\util\concurrent\TimeLimiter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */