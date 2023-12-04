package com.facebook.presto.jdbc.internal.joda.time;

public abstract interface ReadableDuration
  extends Comparable<ReadableDuration>
{
  public abstract long getMillis();
  
  public abstract Duration toDuration();
  
  public abstract Period toPeriod();
  
  public abstract boolean isEqual(ReadableDuration paramReadableDuration);
  
  public abstract boolean isLongerThan(ReadableDuration paramReadableDuration);
  
  public abstract boolean isShorterThan(ReadableDuration paramReadableDuration);
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\ReadableDuration.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */