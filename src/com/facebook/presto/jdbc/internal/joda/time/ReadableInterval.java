package com.facebook.presto.jdbc.internal.joda.time;

public abstract interface ReadableInterval
{
  public abstract Chronology getChronology();
  
  public abstract long getStartMillis();
  
  public abstract DateTime getStart();
  
  public abstract long getEndMillis();
  
  public abstract DateTime getEnd();
  
  public abstract boolean contains(ReadableInstant paramReadableInstant);
  
  public abstract boolean contains(ReadableInterval paramReadableInterval);
  
  public abstract boolean overlaps(ReadableInterval paramReadableInterval);
  
  public abstract boolean isAfter(ReadableInstant paramReadableInstant);
  
  public abstract boolean isAfter(ReadableInterval paramReadableInterval);
  
  public abstract boolean isBefore(ReadableInstant paramReadableInstant);
  
  public abstract boolean isBefore(ReadableInterval paramReadableInterval);
  
  public abstract Interval toInterval();
  
  public abstract MutableInterval toMutableInterval();
  
  public abstract Duration toDuration();
  
  public abstract long toDurationMillis();
  
  public abstract Period toPeriod();
  
  public abstract Period toPeriod(PeriodType paramPeriodType);
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\ReadableInterval.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */