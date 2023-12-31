package com.facebook.presto.jdbc.internal.joda.time;

public abstract class Chronology
{
  public abstract DateTimeZone getZone();
  
  public abstract Chronology withUTC();
  
  public abstract Chronology withZone(DateTimeZone paramDateTimeZone);
  
  public abstract long getDateTimeMillis(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract long getDateTimeMillis(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);
  
  public abstract long getDateTimeMillis(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void validate(ReadablePartial paramReadablePartial, int[] paramArrayOfInt);
  
  public abstract int[] get(ReadablePartial paramReadablePartial, long paramLong);
  
  public abstract long set(ReadablePartial paramReadablePartial, long paramLong);
  
  public abstract int[] get(ReadablePeriod paramReadablePeriod, long paramLong1, long paramLong2);
  
  public abstract int[] get(ReadablePeriod paramReadablePeriod, long paramLong);
  
  public abstract long add(ReadablePeriod paramReadablePeriod, long paramLong, int paramInt);
  
  public abstract long add(long paramLong1, long paramLong2, int paramInt);
  
  public abstract DurationField millis();
  
  public abstract DateTimeField millisOfSecond();
  
  public abstract DateTimeField millisOfDay();
  
  public abstract DurationField seconds();
  
  public abstract DateTimeField secondOfMinute();
  
  public abstract DateTimeField secondOfDay();
  
  public abstract DurationField minutes();
  
  public abstract DateTimeField minuteOfHour();
  
  public abstract DateTimeField minuteOfDay();
  
  public abstract DurationField hours();
  
  public abstract DateTimeField hourOfDay();
  
  public abstract DateTimeField clockhourOfDay();
  
  public abstract DurationField halfdays();
  
  public abstract DateTimeField hourOfHalfday();
  
  public abstract DateTimeField clockhourOfHalfday();
  
  public abstract DateTimeField halfdayOfDay();
  
  public abstract DurationField days();
  
  public abstract DateTimeField dayOfWeek();
  
  public abstract DateTimeField dayOfMonth();
  
  public abstract DateTimeField dayOfYear();
  
  public abstract DurationField weeks();
  
  public abstract DateTimeField weekOfWeekyear();
  
  public abstract DurationField weekyears();
  
  public abstract DateTimeField weekyear();
  
  public abstract DateTimeField weekyearOfCentury();
  
  public abstract DurationField months();
  
  public abstract DateTimeField monthOfYear();
  
  public abstract DurationField years();
  
  public abstract DateTimeField year();
  
  public abstract DateTimeField yearOfEra();
  
  public abstract DateTimeField yearOfCentury();
  
  public abstract DurationField centuries();
  
  public abstract DateTimeField centuryOfEra();
  
  public abstract DurationField eras();
  
  public abstract DateTimeField era();
  
  public abstract String toString();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\Chronology.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */