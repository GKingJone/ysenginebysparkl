package com.facebook.presto.jdbc.internal.airlift.http.client;

public abstract interface HttpRequestFilter
{
  public abstract Request filterRequest(Request paramRequest);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\airlift\http\client\HttpRequestFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */