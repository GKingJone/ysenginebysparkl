package com.facebook.presto.jdbc.internal.jetty.http;

public abstract interface HttpFieldPreEncoder
{
  public abstract HttpVersion getHttpVersion();
  
  public abstract byte[] getEncodedField(HttpHeader paramHttpHeader, String paramString1, String paramString2);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http\HttpFieldPreEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */