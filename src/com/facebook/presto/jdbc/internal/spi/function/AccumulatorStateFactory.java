package com.facebook.presto.jdbc.internal.spi.function;

public abstract interface AccumulatorStateFactory<T>
{
  public abstract T createSingleState();
  
  public abstract Class<? extends T> getSingleStateClass();
  
  public abstract T createGroupedState();
  
  public abstract Class<? extends T> getGroupedStateClass();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\function\AccumulatorStateFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */