package com.facebook.presto.jdbc.internal.spi.security;

import java.util.Map;

public abstract interface SystemAccessControlFactory
{
  public abstract String getName();
  
  public abstract SystemAccessControl create(Map<String, String> paramMap);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\security\SystemAccessControlFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */