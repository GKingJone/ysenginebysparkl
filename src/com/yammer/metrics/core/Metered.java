package com.yammer.metrics.core;

import java.util.concurrent.TimeUnit;

public abstract interface Metered
  extends Metric
{
  public abstract TimeUnit rateUnit();
  
  public abstract String eventType();
  
  public abstract long count();
  
  public abstract double fifteenMinuteRate();
  
  public abstract double fiveMinuteRate();
  
  public abstract double meanRate();
  
  public abstract double oneMinuteRate();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\core\Metered.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */