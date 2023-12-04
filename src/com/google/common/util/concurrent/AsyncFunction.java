package com.google.common.util.concurrent;

public abstract interface AsyncFunction<I, O>
{
  public abstract ListenableFuture<O> apply(I paramI)
    throws Exception;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\google\common\util\concurrent\AsyncFunction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */