package com.yammer.metrics.core;

public abstract interface MetricProcessor<T>
{
  public abstract void processMeter(MetricName paramMetricName, Metered paramMetered, T paramT)
    throws Exception;
  
  public abstract void processCounter(MetricName paramMetricName, Counter paramCounter, T paramT)
    throws Exception;
  
  public abstract void processHistogram(MetricName paramMetricName, Histogram paramHistogram, T paramT)
    throws Exception;
  
  public abstract void processTimer(MetricName paramMetricName, Timer paramTimer, T paramT)
    throws Exception;
  
  public abstract void processGauge(MetricName paramMetricName, Gauge<?> paramGauge, T paramT)
    throws Exception;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\core\MetricProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */