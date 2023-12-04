package com.facebook.presto.jdbc.internal.jetty.client.api;

import com.facebook.presto.jdbc.internal.jetty.util.Promise;

public abstract interface Destination
{
  public abstract String getScheme();
  
  public abstract String getHost();
  
  public abstract int getPort();
  
  public abstract void newConnection(Promise<Connection> paramPromise);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\api\Destination.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */