package com.facebook.presto.jdbc.internal.guava.cache;

import com.facebook.presto.jdbc.internal.guava.annotations.Beta;
import com.facebook.presto.jdbc.internal.guava.annotations.GwtCompatible;

@Beta
@GwtCompatible
public enum RemovalCause
{
  EXPLICIT,  REPLACED,  COLLECTED,  EXPIRED,  SIZE;
  
  private RemovalCause() {}
  
  abstract boolean wasEvicted();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\guava\cache\RemovalCause.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */