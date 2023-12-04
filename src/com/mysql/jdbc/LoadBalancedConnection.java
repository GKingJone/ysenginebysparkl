package com.mysql.jdbc;

import java.sql.SQLException;

public abstract interface LoadBalancedConnection
  extends MySQLConnection
{
  public abstract boolean addHost(String paramString)
    throws SQLException;
  
  public abstract void removeHost(String paramString)
    throws SQLException;
  
  public abstract void removeHostWhenNotInUse(String paramString)
    throws SQLException;
  
  public abstract void ping(boolean paramBoolean)
    throws SQLException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\LoadBalancedConnection.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */