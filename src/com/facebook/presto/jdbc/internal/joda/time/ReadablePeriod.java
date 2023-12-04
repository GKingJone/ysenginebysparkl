package com.facebook.presto.jdbc.internal.joda.time;

public abstract interface ReadablePeriod
{
  public abstract PeriodType getPeriodType();
  
  public abstract int size();
  
  public abstract DurationFieldType getFieldType(int paramInt);
  
  public abstract int getValue(int paramInt);
  
  public abstract int get(DurationFieldType paramDurationFieldType);
  
  public abstract boolean isSupported(DurationFieldType paramDurationFieldType);
  
  public abstract Period toPeriod();
  
  public abstract MutablePeriod toMutablePeriod();
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\ReadablePeriod.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */