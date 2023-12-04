package com.facebook.presto.jdbc.internal.spi;

import com.facebook.presto.jdbc.internal.spi.type.Type;
import java.util.List;

@Deprecated
public abstract interface PageIndexerFactory
{
  public abstract PageIndexer createPageIndexer(List<? extends Type> paramList);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\PageIndexerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */