package com.facebook.presto.jdbc.internal.spi;

import com.facebook.presto.jdbc.internal.spi.block.SortOrder;
import com.facebook.presto.jdbc.internal.spi.type.Type;
import java.util.List;

@Deprecated
public abstract interface PageSorter
{
  @Deprecated
  public abstract long[] sort(List<Type> paramList, List<Page> paramList1, List<Integer> paramList2, List<SortOrder> paramList3, int paramInt);
  
  public abstract int decodePageIndex(long paramLong);
  
  public abstract int decodePositionIndex(long paramLong);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\PageSorter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */