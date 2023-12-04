package com.facebook.presto.jdbc.internal.jetty.io;

import java.io.IOException;
import java.util.Map;

public abstract interface ClientConnectionFactory
{
  public abstract Connection newConnection(EndPoint paramEndPoint, Map<String, Object> paramMap)
    throws IOException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\io\ClientConnectionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */