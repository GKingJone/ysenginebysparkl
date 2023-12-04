package com.facebook.presto.jdbc.internal.spi.connector;

import com.facebook.presto.jdbc.internal.spi.ColumnHandle;
import com.facebook.presto.jdbc.internal.spi.ConnectorPageSource;
import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
import com.facebook.presto.jdbc.internal.spi.ConnectorSplit;
import java.util.List;

public abstract interface ConnectorPageSourceProvider
{
  public abstract ConnectorPageSource createPageSource(ConnectorTransactionHandle paramConnectorTransactionHandle, ConnectorSession paramConnectorSession, ConnectorSplit paramConnectorSplit, List<ColumnHandle> paramList);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\connector\ConnectorPageSourceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */