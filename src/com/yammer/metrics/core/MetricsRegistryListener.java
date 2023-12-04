package com.yammer.metrics.core;

import java.util.EventListener;

public abstract interface MetricsRegistryListener
  extends EventListener
{
  public abstract void onMetricAdded(MetricName paramMetricName, Metric paramMetric);
  
  public abstract void onMetricRemoved(MetricName paramMetricName);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\yammer\metrics\core\MetricsRegistryListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */