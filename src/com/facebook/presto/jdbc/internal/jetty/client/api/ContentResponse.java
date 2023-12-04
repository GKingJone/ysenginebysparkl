package com.facebook.presto.jdbc.internal.jetty.client.api;

public abstract interface ContentResponse
  extends Response
{
  public abstract String getMediaType();
  
  public abstract String getEncoding();
  
  public abstract byte[] getContent();
  
  public abstract String getContentAsString();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\api\ContentResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */