package com.mysql.jdbc.jmx;

import java.sql.SQLException;

public abstract interface LoadBalanceConnectionGroupManagerMBean
{
  public abstract int getActiveHostCount(String paramString);
  
  public abstract int getTotalHostCount(String paramString);
  
  public abstract long getTotalLogicalConnectionCount(String paramString);
  
  public abstract long getActiveLogicalConnectionCount(String paramString);
  
  public abstract long getActivePhysicalConnectionCount(String paramString);
  
  public abstract long getTotalPhysicalConnectionCount(String paramString);
  
  public abstract long getTotalTransactionCount(String paramString);
  
  public abstract void removeHost(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract void stopNewConnectionsToHost(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract void addHost(String paramString1, String paramString2, boolean paramBoolean);
  
  public abstract String getActiveHostsList(String paramString);
  
  public abstract String getRegisteredConnectionGroups();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\jmx\LoadBalanceConnectionGroupManagerMBean.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */