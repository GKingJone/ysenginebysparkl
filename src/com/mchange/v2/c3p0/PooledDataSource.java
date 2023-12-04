package com.mchange.v2.c3p0;

import java.sql.SQLException;
import java.util.Collection;
import javax.sql.DataSource;

public abstract interface PooledDataSource
  extends DataSource
{
  public abstract String getIdentityToken();
  
  public abstract String getDataSourceName();
  
  public abstract void setDataSourceName(String paramString);
  
  /**
   * @deprecated
   */
  public abstract int getNumConnections()
    throws SQLException;
  
  /**
   * @deprecated
   */
  public abstract int getNumIdleConnections()
    throws SQLException;
  
  /**
   * @deprecated
   */
  public abstract int getNumBusyConnections()
    throws SQLException;
  
  /**
   * @deprecated
   */
  public abstract int getNumUnclosedOrphanedConnections()
    throws SQLException;
  
  public abstract int getNumConnectionsDefaultUser()
    throws SQLException;
  
  public abstract int getNumIdleConnectionsDefaultUser()
    throws SQLException;
  
  public abstract int getNumBusyConnectionsDefaultUser()
    throws SQLException;
  
  public abstract int getNumUnclosedOrphanedConnectionsDefaultUser()
    throws SQLException;
  
  public abstract int getStatementCacheNumStatementsDefaultUser()
    throws SQLException;
  
  public abstract int getStatementCacheNumCheckedOutDefaultUser()
    throws SQLException;
  
  public abstract int getStatementCacheNumConnectionsWithCachedStatementsDefaultUser()
    throws SQLException;
  
  public abstract long getStartTimeMillisDefaultUser()
    throws SQLException;
  
  public abstract long getUpTimeMillisDefaultUser()
    throws SQLException;
  
  public abstract long getNumFailedCheckinsDefaultUser()
    throws SQLException;
  
  public abstract long getNumFailedCheckoutsDefaultUser()
    throws SQLException;
  
  public abstract long getNumFailedIdleTestsDefaultUser()
    throws SQLException;
  
  public abstract float getEffectivePropertyCycleDefaultUser()
    throws SQLException;
  
  public abstract int getNumThreadsAwaitingCheckoutDefaultUser()
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
  
  public abstract int getStatementCacheNumStatements(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract int getStatementCacheNumCheckedOut(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract int getStatementCacheNumConnectionsWithCachedStatements(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract float getEffectivePropertyCycle(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract int getNumThreadsAwaitingCheckout(String paramString1, String paramString2)
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
  
  public abstract int getStatementCacheNumStatementsAllUsers()
    throws SQLException;
  
  public abstract int getStatementCacheNumCheckedOutStatementsAllUsers()
    throws SQLException;
  
  public abstract int getStatementCacheNumConnectionsWithCachedStatementsAllUsers()
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
  
  public abstract String sampleStatementCacheStatusDefaultUser()
    throws SQLException;
  
  public abstract String sampleStatementCacheStatus(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract Throwable getLastAcquisitionFailureDefaultUser()
    throws SQLException;
  
  public abstract Throwable getLastCheckinFailureDefaultUser()
    throws SQLException;
  
  public abstract Throwable getLastCheckoutFailureDefaultUser()
    throws SQLException;
  
  public abstract Throwable getLastIdleTestFailureDefaultUser()
    throws SQLException;
  
  public abstract Throwable getLastConnectionTestFailureDefaultUser()
    throws SQLException;
  
  public abstract Throwable getLastAcquisitionFailure(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract Throwable getLastCheckinFailure(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract Throwable getLastCheckoutFailure(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract Throwable getLastIdleTestFailure(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract Throwable getLastConnectionTestFailure(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract String sampleLastAcquisitionFailureStackTraceDefaultUser()
    throws SQLException;
  
  public abstract String sampleLastCheckinFailureStackTraceDefaultUser()
    throws SQLException;
  
  public abstract String sampleLastCheckoutFailureStackTraceDefaultUser()
    throws SQLException;
  
  public abstract String sampleLastIdleTestFailureStackTraceDefaultUser()
    throws SQLException;
  
  public abstract String sampleLastConnectionTestFailureStackTraceDefaultUser()
    throws SQLException;
  
  public abstract String sampleLastAcquisitionFailureStackTrace(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract String sampleLastCheckinFailureStackTrace(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract String sampleLastCheckoutFailureStackTrace(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract String sampleLastIdleTestFailureStackTrace(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract String sampleLastConnectionTestFailureStackTrace(String paramString1, String paramString2)
    throws SQLException;
  
  public abstract void softResetAllUsers()
    throws SQLException;
  
  public abstract int getNumUserPools()
    throws SQLException;
  
  public abstract int getNumHelperThreads()
    throws SQLException;
  
  public abstract Collection getAllUsers()
    throws SQLException;
  
  public abstract void hardReset()
    throws SQLException;
  
  public abstract void close()
    throws SQLException;
  
  /**
   * @deprecated
   */
  public abstract void close(boolean paramBoolean)
    throws SQLException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\PooledDataSource.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */