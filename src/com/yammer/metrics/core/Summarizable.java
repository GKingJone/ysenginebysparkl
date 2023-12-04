package com.yammer.metrics.core;

public abstract interface Summarizable
{
  public abstract double max();
  
  public abstract double min();
  
  public abstract double mean();
  
  public abstract double stdDev();
  
  public abstract double sum();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\core\Summarizable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */