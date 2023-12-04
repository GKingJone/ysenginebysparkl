package com.facebook.presto.jdbc.internal.spi;

import com.facebook.presto.jdbc.internal.spi.connector.ConnectorContext;
import java.util.Map;

@Deprecated
public abstract interface ConnectorFactory
{
  public abstract String getName();
  
  public abstract ConnectorHandleResolver getHandleResolver();
  
  public abstract Connector create(String paramString, Map<String, String> paramMap, ConnectorContext paramConnectorContext);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\ConnectorFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */