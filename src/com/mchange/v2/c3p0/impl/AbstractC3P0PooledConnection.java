package com.mchange.v2.c3p0.impl;

import com.mchange.v1.util.ClosableResource;
import com.mchange.v2.c3p0.stmt.GooGooStatementCache;
import java.sql.Connection;
import javax.sql.PooledConnection;

abstract class AbstractC3P0PooledConnection
  implements PooledConnection, ClosableResource
{
  abstract Connection getPhysicalConnection();
  
  abstract void initStatementCache(GooGooStatementCache paramGooGooStatementCache);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\impl\AbstractC3P0PooledConnection.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */