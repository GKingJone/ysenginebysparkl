package com.facebook.presto.jdbc.internal.guava.eventbus;

public abstract interface SubscriberExceptionHandler
{
  public abstract void handleException(Throwable paramThrowable, SubscriberExceptionContext paramSubscriberExceptionContext);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\eventbus\SubscriberExceptionHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */