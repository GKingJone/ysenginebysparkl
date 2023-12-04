package com.mchange.v2.c3p0.mbean;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import javax.naming.NamingException;

/**
 * @deprecated
 */
public abstract interface C3P0PooledDataSourceMBean
{
  public abstract void setJndiName(String paramString)
    throws NamingException;
  
  public abstract String getJndiName();
  
  public abstract String getDescription();
  
  public abstract void setDescription(String paramString)
    throws NamingException;
  
  public abstract String getDriverClass();
  
  public abstract void setDriverClass(String paramString)
    throws PropertyVetoException, NamingException;
  
  public abstract String getJdbcUrl();
  
  public abstract void setJdbcUrl(String paramString)
    throws NamingException;
  
  public abstract String getUser();
  
  public abstract void setUser(String paramString)
    throws NamingException;
  
  public abstract String getPassword();
  
  public abstract void setPassword(String paramString)
    throws NamingException;
  
  public abstract int getCheckoutTimeout();
  
  public abstract void setCheckoutTimeout(int paramInt)
    throws NamingException;
  
  public abstract int getAcquireIncrement();
  
  public abstract void setAcquireIncrement(int paramInt)
    throws NamingException;
  
  public abstract int getAcquireRetryAttempts();
  
  public abstract void setAcquireRetryAttempts(int paramInt)
    throws NamingException;
  
  public abstract int getAcquireRetryDelay();
  
  public abstract void setAcquireRetryDelay(int paramInt)
    throws NamingException;
  
  public abstract boolean isAutoCommitOnClose();
  
  public abstract void setAutoCommitOnClose(boolean paramBoolean)
    throws NamingException;
  
  public abstract String getConnectionTesterClassName();
  
  public abstract void setConnectionTesterClassName(String paramString)
    throws PropertyVetoException, NamingException;
  
  public abstract String getAutomaticTestTable();
  
  public abstract void setAutomaticTestTable(String paramString)
    throws NamingException;
  
  public abstract boolean isForceIgnoreUnresolvedTransactions();
  
  public abstract void setForceIgnoreUnresolvedTransactions(boolean paramBoolean)
    throws NamingException;
  
  public abstract int getIdleConnectionTestPeriod();
  
  public abstract void setIdleConnectionTestPeriod(int paramInt)
    throws NamingException;
  
  public abstract int getInitialPoolSize();
  
  public abstract void setInitialPoolSize(int paramInt)
    throws NamingException;
  
  public abstract int getMaxIdleTime();
  
  public abstract void setMaxIdleTime(int paramInt)
    throws NamingException;
  
  public abstract int getMaxPoolSize();
  
  public abstract void setMaxPoolSize(int paramInt)
    throws NamingException;
  
  public abstract int getMaxStatements();
  
  public abstract void setMaxStatements(int paramInt)
    throws NamingException;
  
  public abstract int getMaxStatementsPerConnection();
  
  public abstract void setMaxStatementsPerConnection(int paramInt)
    throws NamingException;
  
  public abstract int getMinPoolSize();
  
  public abstract void setMinPoolSize(int paramInt)
    throws NamingException;
  
  public abstract int getPropertyCycle();
  
  public abstract void setPropertyCycle(int paramInt)
    throws NamingException;
  
  public abstract boolean isBreakAfterAcquireFailure();
  
  public abstract void setBreakAfterAcquireFailure(boolean paramBoolean)
    throws NamingException;
  
  public abstract boolean isTestConnectionOnCheckout();
  
  public abstract void setTestConnectionOnCheckout(boolean paramBoolean)
    throws NamingException;
  
  public abstract boolean isTestConnectionOnCheckin();
  
  public abstract void setTestConnectionOnCheckin(boolean paramBoolean)
    throws NamingException;
  
  public abstract boolean isUsesTraditionalReflectiveProxies();
  
  public abstract void setUsesTraditionalReflectiveProxies(boolean paramBoolean)
    throws NamingException;
  
  public abstract String getPreferredTestQuery();
  
  public abstract void setPreferredTestQuery(String paramString)
    throws NamingException;
  
  public abstract int getNumHelperThreads();
  
  public abstract void setNumHelperThreads(int paramInt)
    throws NamingException;
  
  public abstract String getFactoryClassLocation();
  
  public abstract void setFactoryClassLocation(String paramString)
    throws NamingException;
  
  public abstract int getNumUserPools()
    throws SQLException;
  
  public abstract int getNumConnectionsDefaultUser()
    throws SQLException;
  
  public abstract int getNumIdleConnectionsDefaultUser()
    throws SQLException;
  
  public abstract int getNumBusyConnectionsDefaultUser()
    throws SQLException;
  
  public abstract int getNumUnclosedOrphanedConnectionsDefaultUser()
    throws SQLException;
  
  public abstract int getNumConnections(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract int getNumIdleConnections(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract int getNumBusyConnections(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract int getNumUnclosedOrphanedConnections(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract int getNumBusyConnectionsAllUsers()
    throws SQLException;
  
  public abstract int getNumIdleConnectionsAllUsers()
    throws SQLException;
  
  public abstract int getNumConnectionsAllUsers()
    throws SQLException;
  
  public abstract int getNumUnclosedOrphanedConnectionsAllUsers()
    throws SQLException;
  
  public abstract void softResetDefaultUser()
    throws SQLException;
  
  public abstract void softReset(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract void softResetAllUsers()
    throws SQLException;
  
  public abstract void hardReset()
    throws SQLException;
  
  public abstract void close()
    throws SQLException;
  
  public abstract void create()
    throws Exception;
  
  public abstract void start()
    throws Exception;
  
  public abstract void stop();
  
  public abstract void destroy();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\mbean\C3P0PooledDataSourceMBean.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */