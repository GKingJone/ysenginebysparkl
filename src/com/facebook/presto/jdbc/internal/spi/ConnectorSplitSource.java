package com.facebook.presto.jdbc.internal.spi;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract interface ConnectorSplitSource
  extends Closeable
{
  public abstract CompletableFuture<List<ConnectorSplit>> getNextBatch(int paramInt);
  
  public abstract void close();
  
  public abstract boolean isFinished();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\ConnectorSplitSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */