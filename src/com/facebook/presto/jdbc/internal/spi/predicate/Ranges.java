package com.facebook.presto.jdbc.internal.spi.predicate;

import java.util.List;

public abstract interface Ranges
{
  public abstract int getRangeCount();
  
  public abstract List<Range> getOrderedRanges();
  
  public abstract Range getSpan();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\predicate\Ranges.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */