package com.facebook.presto.jdbc.internal.guava.eventbus;

import com.facebook.presto.jdbc.internal.guava.collect.Multimap;

abstract interface SubscriberFindingStrategy
{
  public abstract Multimap<Class<?>, EventSubscriber> findAllSubscribers(Object paramObject);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\eventbus\SubscriberFindingStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */