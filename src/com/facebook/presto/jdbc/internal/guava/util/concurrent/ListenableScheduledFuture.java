package com.facebook.presto.jdbc.internal.guava.util.concurrent;

import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
import java.util.concurrent.ScheduledFuture;

@Beta
public abstract interface ListenableScheduledFuture<V>
  extends ScheduledFuture<V>, ListenableFuture<V>
{}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\util\concurrent\ListenableScheduledFuture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */