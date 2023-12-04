package com.facebook.presto.jdbc.internal.jetty.client.api;

import java.io.Closeable;

public abstract interface Connection
  extends Closeable
{
  public abstract void send(Request paramRequest, Response.CompleteListener paramCompleteListener);
  
  public abstract void close();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\api\Connection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */