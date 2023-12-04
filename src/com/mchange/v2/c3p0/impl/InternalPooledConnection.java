package com.mchange.v2.c3p0.impl;

import com.mchange.v2.c3p0.stmt.GooGooStatementCache;
import javax.sql.PooledConnection;

abstract interface InternalPooledConnection
  extends PooledConnection
{
  public abstract void initStatementCache(GooGooStatementCache paramGooGooStatementCache);
  
  public abstract GooGooStatementCache getStatementCache();
  
  public abstract int getConnectionStatus();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\InternalPooledConnection.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */