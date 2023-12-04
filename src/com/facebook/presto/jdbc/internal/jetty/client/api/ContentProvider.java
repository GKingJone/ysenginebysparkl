package com.facebook.presto.jdbc.internal.jetty.client.api;

import java.nio.ByteBuffer;

public abstract interface ContentProvider
  extends Iterable<ByteBuffer>
{
  public abstract long getLength();
  
  public static abstract interface Typed
    extends ContentProvider
  {
    public abstract String getContentType();
  }
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jetty\client\api\ContentProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */