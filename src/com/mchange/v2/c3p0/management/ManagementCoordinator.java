package com.mchange.v2.c3p0.management;

import com.mchange.v2.c3p0.PooledDataSource;

public abstract interface ManagementCoordinator
{
  public abstract void attemptManageC3P0Registry();
  
  public abstract void attemptUnmanageC3P0Registry();
  
  public abstract void attemptManagePooledDataSource(PooledDataSource paramPooledDataSource);
  
  public abstract void attemptUnmanagePooledDataSource(PooledDataSource paramPooledDataSource);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\management\ManagementCoordinator.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */