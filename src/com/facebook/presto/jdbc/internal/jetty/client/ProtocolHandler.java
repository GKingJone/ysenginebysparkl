package com.facebook.presto.jdbc.internal.jetty.client;

import com.facebook.presto.jdbc.internal.jetty.client.api.Request;
import com.facebook.presto.jdbc.internal.jetty.client.api.Response;
import com.facebook.presto.jdbc.internal.jetty.client.api.Response.Listener;

public abstract interface ProtocolHandler
{
  public abstract String getName();
  
  public abstract boolean accept(Request paramRequest, Response paramResponse);
  
  public abstract Listener getResponseListener();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\ProtocolHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */