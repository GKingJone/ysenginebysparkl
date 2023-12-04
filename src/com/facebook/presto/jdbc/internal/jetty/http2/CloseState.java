package com.facebook.presto.jdbc.internal.jetty.http2;

public enum CloseState
{
  NOT_CLOSED,  LOCALLY_CLOSED,  REMOTELY_CLOSED,  CLOSED;
  
  private CloseState() {}
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\CloseState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */