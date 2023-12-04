package com.mysql.jdbc;

import java.sql.SQLException;
import java.util.Properties;

public abstract interface CacheAdapterFactory<K, V>
{
  public abstract CacheAdapter<K, V> getInstance(Connection paramConnection, String paramString, int paramInt1, int paramInt2, Properties paramProperties)
    throws SQLException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\CacheAdapterFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */