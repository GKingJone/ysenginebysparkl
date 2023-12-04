package com.facebook.presto.jdbc.internal.spi.connector;

import com.facebook.presto.jdbc.internal.spi.ColumnHandle;
import com.facebook.presto.jdbc.internal.spi.ConnectorIndex;
import com.facebook.presto.jdbc.internal.spi.ConnectorIndexHandle;
import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
import java.util.List;

public abstract interface ConnectorIndexProvider
{
  public abstract ConnectorIndex getIndex(ConnectorTransactionHandle paramConnectorTransactionHandle, ConnectorSession paramConnectorSession, ConnectorIndexHandle paramConnectorIndexHandle, List<ColumnHandle> paramList1, List<ColumnHandle> paramList2);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\connector\ConnectorIndexProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */