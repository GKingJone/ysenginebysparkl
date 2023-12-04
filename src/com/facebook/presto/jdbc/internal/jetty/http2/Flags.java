package com.facebook.presto.jdbc.internal.jetty.http2;

public abstract interface Flags
{
  public static final int NONE = 0;
  public static final int END_STREAM = 1;
  public static final int ACK = 1;
  public static final int END_HEADERS = 4;
  public static final int PADDING = 8;
  public static final int PRIORITY = 32;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\http2\Flags.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */