package com.facebook.presto.jdbc.internal.spi;

import com.facebook.presto.jdbc.internal.spi.type.Type;
import java.util.List;

public abstract interface RecordSet
{
  public abstract List<Type> getColumnTypes();
  
  public abstract RecordCursor cursor();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\RecordSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */