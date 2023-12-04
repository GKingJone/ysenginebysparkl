package com.facebook.presto.jdbc.internal.spi;

import java.io.Closeable;
import java.io.IOException;

public abstract interface ConnectorPageSource
  extends Closeable
{
  public abstract long getTotalBytes();
  
  public abstract long getCompletedBytes();
  
  public abstract long getReadTimeNanos();
  
  public abstract boolean isFinished();
  
  public abstract Page getNextPage();
  
  public abstract long getSystemMemoryUsage();
  
  public abstract void close()
    throws IOException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\ConnectorPageSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */