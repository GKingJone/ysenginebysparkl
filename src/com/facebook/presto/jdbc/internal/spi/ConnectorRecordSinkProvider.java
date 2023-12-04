package com.facebook.presto.jdbc.internal.spi;

@Deprecated
public abstract interface ConnectorRecordSinkProvider
{
  public abstract RecordSink getRecordSink(ConnectorSession paramConnectorSession, ConnectorOutputTableHandle paramConnectorOutputTableHandle);
  
  public abstract RecordSink getRecordSink(ConnectorSession paramConnectorSession, ConnectorInsertTableHandle paramConnectorInsertTableHandle);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\ConnectorRecordSinkProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */