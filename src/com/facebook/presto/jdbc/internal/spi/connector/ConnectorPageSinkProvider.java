package com.facebook.presto.jdbc.internal.spi.connector;

import com.facebook.presto.jdbc.internal.spi.ConnectorInsertTableHandle;
import com.facebook.presto.jdbc.internal.spi.ConnectorOutputTableHandle;
import com.facebook.presto.jdbc.internal.spi.ConnectorPageSink;
import com.facebook.presto.jdbc.internal.spi.ConnectorSession;

public abstract interface ConnectorPageSinkProvider
{
  public abstract ConnectorPageSink createPageSink(ConnectorTransactionHandle paramConnectorTransactionHandle, ConnectorSession paramConnectorSession, ConnectorOutputTableHandle paramConnectorOutputTableHandle);
  
  public abstract ConnectorPageSink createPageSink(ConnectorTransactionHandle paramConnectorTransactionHandle, ConnectorSession paramConnectorSession, ConnectorInsertTableHandle paramConnectorInsertTableHandle);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\connector\ConnectorPageSinkProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */