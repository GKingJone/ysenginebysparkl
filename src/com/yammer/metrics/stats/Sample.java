package com.yammer.metrics.stats;

public abstract interface Sample
{
  public abstract void clear();
  
  public abstract int size();
  
  public abstract void update(long paramLong);
  
  public abstract Snapshot getSnapshot();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\stats\Sample.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */