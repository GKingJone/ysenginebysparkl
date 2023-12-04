package com.mysql.jdbc;

import java.util.Set;

public abstract interface CacheAdapter<K, V>
{
  public abstract V get(K paramK);
  
  public abstract void put(K paramK, V paramV);
  
  public abstract void invalidate(K paramK);
  
  public abstract void invalidateAll(Set<K> paramSet);
  
  public abstract void invalidateAll();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\CacheAdapter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */