package com.facebook.presto.jdbc.internal.jackson.annotation;

public abstract interface ObjectIdResolver
{
  public abstract void bindItem(ObjectIdGenerator.IdKey paramIdKey, Object paramObject);
  
  public abstract Object resolveId(ObjectIdGenerator.IdKey paramIdKey);
  
  public abstract ObjectIdResolver newForDeserialization(Object paramObject);
  
  public abstract boolean canUseFor(ObjectIdResolver paramObjectIdResolver);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\annotation\ObjectIdResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */