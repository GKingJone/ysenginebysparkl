package com.facebook.presto.jdbc.internal.guava.util.concurrent;

import javax.annotation.Nullable;

public abstract interface FutureCallback<V>
{
  public abstract void onSuccess(@Nullable V paramV);
  
  public abstract void onFailure(Throwable paramThrowable);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\util\concurrent\FutureCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */