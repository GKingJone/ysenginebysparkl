package com.facebook.presto.jdbc.internal.airlift.http.client;

import com.facebook.presto.jdbc.internal.guava.annotations.Beta;

@Beta
public abstract interface ResponseHandler<T, E extends Exception>
{
  public abstract T handleException(Request paramRequest, Exception paramException)
    throws Exception;
  
  public abstract T handle(Request paramRequest, Response paramResponse)
    throws Exception;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\ResponseHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */