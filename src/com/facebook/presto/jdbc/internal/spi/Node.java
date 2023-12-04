package com.facebook.presto.jdbc.internal.spi;

import java.net.URI;

public abstract interface Node
{
  public abstract HostAddress getHostAndPort();
  
  public abstract URI getHttpUri();
  
  public abstract String getNodeIdentifier();
  
  public abstract String getVersion();
  
  public abstract boolean isCoordinator();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\Node.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */