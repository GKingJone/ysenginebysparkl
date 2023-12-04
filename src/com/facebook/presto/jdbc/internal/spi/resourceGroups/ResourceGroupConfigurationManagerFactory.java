package com.facebook.presto.jdbc.internal.spi.resourceGroups;

import java.util.Map;

public abstract interface ResourceGroupConfigurationManagerFactory
{
  public abstract String getName();
  
  public abstract ResourceGroupConfigurationManager create(Map<String, String> paramMap, ResourceGroupConfigurationManagerContext paramResourceGroupConfigurationManagerContext);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\resourceGroups\ResourceGroupConfigurationManagerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */