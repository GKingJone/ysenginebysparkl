package com.mysql.jdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public abstract interface BalanceStrategy
  extends Extension
{
  public abstract ConnectionImpl pickConnection(LoadBalancedConnectionProxy paramLoadBalancedConnectionProxy, List<String> paramList, Map<String, ConnectionImpl> paramMap, long[] paramArrayOfLong, int paramInt)
    throws SQLException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\BalanceStrategy.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */