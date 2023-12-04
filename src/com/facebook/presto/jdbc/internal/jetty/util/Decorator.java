package com.facebook.presto.jdbc.internal.jetty.util;

public abstract interface Decorator
{
  public abstract <T> T decorate(T paramT);
  
  public abstract void destroy(Object paramObject);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\util\Decorator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */