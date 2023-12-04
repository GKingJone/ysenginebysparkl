package com.mchange.v2.c3p0.management;

import java.sql.SQLException;
import java.util.Collection;

public abstract interface PooledDataSourceManagerMBean
{
  public abstract String getIdentityToken();
  
  public abstract String getDataSourceName();
  
  public abstract void setDataSourceName(String paramString);
  
  public abstract int getNumConnectionsDefaultUser()
    throws SQLException;
  
  public abstract int getNumIdleConnectionsDefaultUser()
    throws SQLException;
  
  public abstract int getNumBusyConnectionsDefaultUser()
    throws SQLException;
  
  public abstract int getNumUnclosedOrphanedConnectionsDefaultUser()
    throws SQLException;
  
  public abstract float getEffectivePropertyCycleDefaultUser()
    throws SQLException;
  
  public abstract void softResetDefaultUser()
    throws SQLException;
  
  public abstract int getNumConnections(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract int getNumIdleConnections(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract int getNumBusyConnections(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract int getNumUnclosedOrphanedConnections(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract float getEffectivePropertyCycle(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract void softReset(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract int getNumBusyConnectionsAllUsers()
    throws SQLException;
  
  public abstract int getNumIdleConnectionsAllUsers()
    throws SQLException;
  
  public abstract int getNumConnectionsAllUsers()
    throws SQLException;
  
  public abstract int getNumUnclosedOrphanedConnectionsAllUsers()
    throws SQLException;
  
  public abstract int getThreadPoolSize()
    throws SQLException;
  
  public abstract int getThreadPoolNumActiveThreads()
    throws SQLException;
  
  public abstract int getThreadPoolNumIdleThreads()
    throws SQLException;
  
  public abstract int getThreadPoolNumTasksPending()
    throws SQLException;
  
  public abstract String sampleThreadPoolStackTraces()
    throws SQLException;
  
  public abstract String sampleThreadPoolStatus()
    throws SQLException;
  
  public abstract void softResetAllUsers()
    throws SQLException;
  
  public abstract int getNumUserPools()
    throws SQLException;
  
  public abstract Collection getAllUsers()
    throws SQLException;
  
  public abstract void hardReset()
    throws SQLException;
  
  public abstract void close()
    throws SQLException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\management\PooledDataSourceManagerMBean.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */