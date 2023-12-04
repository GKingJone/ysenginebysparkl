package com.facebook.presto.jdbc.internal.spi.function;

public abstract interface GroupedAccumulatorState
  extends AccumulatorState
{
  public abstract void setGroupId(long paramLong);
  
  public abstract void ensureCapacity(long paramLong);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\function\GroupedAccumulatorState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */