package com.facebook.presto.jdbc.internal.guava.util.concurrent;

import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Beta
public abstract interface ListeningScheduledExecutorService
  extends ScheduledExecutorService, ListeningExecutorService
{
  public abstract ListenableScheduledFuture<?> schedule(Runnable paramRunnable, long paramLong, TimeUnit paramTimeUnit);
  
  public abstract <V> ListenableScheduledFuture<V> schedule(Callable<V> paramCallable, long paramLong, TimeUnit paramTimeUnit);
  
  public abstract ListenableScheduledFuture<?> scheduleAtFixedRate(Runnable paramRunnable, long paramLong1, long paramLong2, TimeUnit paramTimeUnit);
  
  public abstract ListenableScheduledFuture<?> scheduleWithFixedDelay(Runnable paramRunnable, long paramLong1, long paramLong2, TimeUnit paramTimeUnit);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\util\concurrent\ListeningScheduledExecutorService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */