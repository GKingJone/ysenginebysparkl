package com.facebook.presto.jdbc.internal.spi.connector;

import com.facebook.presto.jdbc.internal.spi.BucketFunction;
import com.facebook.presto.jdbc.internal.spi.ConnectorSession;
import com.facebook.presto.jdbc.internal.spi.ConnectorSplit;
import com.facebook.presto.jdbc.internal.spi.Node;
import com.facebook.presto.jdbc.internal.spi.type.Type;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

public abstract interface ConnectorNodePartitioningProvider
{
  public abstract Map<Integer, Node> getBucketToNode(ConnectorTransactionHandle paramConnectorTransactionHandle, ConnectorSession paramConnectorSession, ConnectorPartitioningHandle paramConnectorPartitioningHandle);
  
  public abstract ToIntFunction<ConnectorSplit> getSplitBucketFunction(ConnectorTransactionHandle paramConnectorTransactionHandle, ConnectorSession paramConnectorSession, ConnectorPartitioningHandle paramConnectorPartitioningHandle);
  
  public abstract BucketFunction getBucketFunction(ConnectorTransactionHandle paramConnectorTransactionHandle, ConnectorSession paramConnectorSession, ConnectorPartitioningHandle paramConnectorPartitioningHandle, List<Type> paramList, int paramInt);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\connector\ConnectorNodePartitioningProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */