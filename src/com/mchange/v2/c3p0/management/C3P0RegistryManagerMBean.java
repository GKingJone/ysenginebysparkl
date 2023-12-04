package com.mchange.v2.c3p0.management;

import java.sql.SQLException;
import java.util.Set;

public abstract interface C3P0RegistryManagerMBean
{
  public abstract String[] getAllIdentityTokens();
  
  public abstract Set getAllIdentityTokenized();
  
  public abstract Set getAllPooledDataSources();
  
  public abstract int getAllIdentityTokenCount();
  
  public abstract int getAllIdentityTokenizedCount();
  
  public abstract int getAllPooledDataSourcesCount();
  
  public abstract String[] getAllIdentityTokenizedStringified();
  
  public abstract String[] getAllPooledDataSourcesStringified();
  
  public abstract int getNumPooledDataSources()
    throws SQLException;
  
  public abstract int getNumPoolsAllDataSources()
    throws SQLException;
  
  public abstract String getC3p0Version();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\mchange\v2\c3p0\management\C3P0RegistryManagerMBean.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */