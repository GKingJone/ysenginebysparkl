package com.mchange.v2.c3p0.management;

import com.mchange.v2.c3p0.PooledDataSource;

public class NullManagementCoordinator
  implements ManagementCoordinator
{
  public void attemptManageC3P0Registry() {}
  
  public void attemptUnmanageC3P0Registry() {}
  
  public void attemptManagePooledDataSource(PooledDataSource pds) {}
  
  public void attemptUnmanagePooledDataSource(PooledDataSource pds) {}
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\management\NullManagementCoordinator.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */