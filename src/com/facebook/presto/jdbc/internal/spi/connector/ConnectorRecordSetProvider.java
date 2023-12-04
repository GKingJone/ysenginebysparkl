package com.facebook.presto.jdbc.internal.spi.connector;

import com.facebook.presto.jdbc.internal.spi.ColumnHandle;
import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
import com.facebook.presto.jdbc.internal.spi.ConnectorSplit;
import com.facebook.presto.jdbc.internal.spi.RecordSet;
import java.util.List;

public abstract interface ConnectorRecordSetProvider
{
  public abstract RecordSet getRecordSet(ConnectorTransactionHandle paramConnectorTransactionHandle, ConnectorSession paramConnectorSession, ConnectorSplit paramConnectorSplit, List<? extends ColumnHandle> paramList);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\connector\ConnectorRecordSetProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */