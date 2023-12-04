package com.facebook.presto.jdbc.internal.joda.time;

public abstract interface ReadableInstant
  extends Comparable<ReadableInstant>
{
  public abstract long getMillis();
  
  public abstract Chronology getChronology();
  
  public abstract DateTimeZone getZone();
  
  public abstract int get(DateTimeFieldType paramDateTimeFieldType);
  
  public abstract boolean isSupported(DateTimeFieldType paramDateTimeFieldType);
  
  public abstract Instant toInstant();
  
  public abstract boolean isEqual(ReadableInstant paramReadableInstant);
  
  public abstract boolean isAfter(ReadableInstant paramReadableInstant);
  
  public abstract boolean isBefore(ReadableInstant paramReadableInstant);
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\ReadableInstant.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */