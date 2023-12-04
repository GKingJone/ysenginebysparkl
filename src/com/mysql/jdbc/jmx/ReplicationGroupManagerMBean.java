package com.mysql.jdbc.jmx;

import java.sql.SQLException;

public abstract interface ReplicationGroupManagerMBean
{
  public abstract void addSlaveHost(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract void removeSlaveHost(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract void promoteSlaveToMaster(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract void removeMasterHost(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract String getMasterHostsList(String paramString);
  
  public abstract String getSlaveHostsList(String paramString);
  
  public abstract String getRegisteredConnectionGroups();
  
  public abstract int getActiveMasterHostCount(String paramString);
  
  public abstract int getActiveSlaveHostCount(String paramString);
  
  public abstract int getSlavePromotionCount(String paramString);
  
  public abstract long getTotalLogicalConnectionCount(String paramString);
  
  public abstract long getActiveLogicalConnectionCount(String paramString);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mysql\jdbc\jmx\ReplicationGroupManagerMBean.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */