package com.mchange.v2.c3p0.stmt;

import com.mchange.v1.util.ClosableResource;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

public abstract interface StatementCache
  extends ClosableResource
{
  public abstract Object checkoutStatement(Connection paramConnection, Method paramMethod, Object[] paramArrayOfObject)
    throws SQLException;
  
  public abstract void checkinStatement(Object paramObject)
    throws SQLException;
  
  public abstract void checkinAll(Connection paramConnection)
    throws SQLException;
  
  public abstract void closeAll(Connection paramConnection)
    throws SQLException;
  
  public abstract void close()
    throws SQLException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\stmt\StatementCache.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */