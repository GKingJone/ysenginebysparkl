package com.facebook.presto.jdbc.internal.joda.time;

public abstract interface ReadWritableInterval
  extends ReadableInterval
{
  public abstract void setInterval(long paramLong1, long paramLong2);
  
  public abstract void setInterval(ReadableInterval paramReadableInterval);
  
  public abstract void setInterval(ReadableInstant paramReadableInstant1, ReadableInstant paramReadableInstant2);
  
  public abstract void setChronology(Chronology paramChronology);
  
  public abstract void setStartMillis(long paramLong);
  
  public abstract void setStart(ReadableInstant paramReadableInstant);
  
  public abstract void setEndMillis(long paramLong);
  
  public abstract void setEnd(ReadableInstant paramReadableInstant);
  
  public abstract void setDurationAfterStart(ReadableDuration paramReadableDuration);
  
  public abstract void setDurationBeforeEnd(ReadableDuration paramReadableDuration);
  
  public abstract void setPeriodAfterStart(ReadablePeriod paramReadablePeriod);
  
  public abstract void setPeriodBeforeEnd(ReadablePeriod paramReadablePeriod);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\ReadWritableInterval.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */