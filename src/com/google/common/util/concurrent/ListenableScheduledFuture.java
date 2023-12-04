package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import java.util.concurrent.ScheduledFuture;

@Beta
public abstract interface ListenableScheduledFuture<V>
  extends ScheduledFuture<V>, ListenableFuture<V>
{}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\util\concurrent\ListenableScheduledFuture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */