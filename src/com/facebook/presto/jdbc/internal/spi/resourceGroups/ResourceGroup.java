package com.facebook.presto.jdbc.internal.spi.resourceGroups;

import com.facebook.presto.jdbc.internal.airlift.units.DataSize;
import com.facebook.presto.jdbc.internal.airlift.units.Duration;

public abstract interface ResourceGroup
{
  public abstract ResourceGroupId getId();
  
  public abstract DataSize getSoftMemoryLimit();
  
  public abstract void setSoftMemoryLimit(DataSize paramDataSize);
  
  public abstract Duration getSoftCpuLimit();
  
  public abstract void setSoftCpuLimit(Duration paramDuration);
  
  public abstract Duration getHardCpuLimit();
  
  public abstract void setHardCpuLimit(Duration paramDuration);
  
  public abstract long getCpuQuotaGenerationMillisPerSecond();
  
  public abstract void setCpuQuotaGenerationMillisPerSecond(long paramLong);
  
  public abstract int getMaxRunningQueries();
  
  public abstract void setMaxRunningQueries(int paramInt);
  
  public abstract int getMaxQueuedQueries();
  
  public abstract void setMaxQueuedQueries(int paramInt);
  
  public abstract int getSchedulingWeight();
  
  public abstract void setSchedulingWeight(int paramInt);
  
  public abstract SchedulingPolicy getSchedulingPolicy();
  
  public abstract void setSchedulingPolicy(SchedulingPolicy paramSchedulingPolicy);
  
  public abstract boolean getJmxExport();
  
  public abstract void setJmxExport(boolean paramBoolean);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\spi\resourceGroups\ResourceGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */