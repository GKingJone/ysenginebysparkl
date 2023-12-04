package com.yammer.metrics.core;

public abstract interface Metric
{
  public abstract <T> void processWith(MetricProcessor<T> paramMetricProcessor, MetricName paramMetricName, T paramT)
    throws Exception;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\core\Metric.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */