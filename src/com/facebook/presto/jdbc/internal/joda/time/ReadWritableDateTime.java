package com.facebook.presto.jdbc.internal.joda.time;

public abstract interface ReadWritableDateTime
  extends ReadableDateTime, ReadWritableInstant
{
  public abstract void setYear(int paramInt);
  
  public abstract void addYears(int paramInt);
  
  public abstract void setWeekyear(int paramInt);
  
  public abstract void addWeekyears(int paramInt);
  
  public abstract void setMonthOfYear(int paramInt);
  
  public abstract void addMonths(int paramInt);
  
  public abstract void setWeekOfWeekyear(int paramInt);
  
  public abstract void addWeeks(int paramInt);
  
  public abstract void setDayOfYear(int paramInt);
  
  public abstract void setDayOfMonth(int paramInt);
  
  public abstract void setDayOfWeek(int paramInt);
  
  public abstract void addDays(int paramInt);
  
  public abstract void setHourOfDay(int paramInt);
  
  public abstract void addHours(int paramInt);
  
  public abstract void setMinuteOfDay(int paramInt);
  
  public abstract void setMinuteOfHour(int paramInt);
  
  public abstract void addMinutes(int paramInt);
  
  public abstract void setSecondOfDay(int paramInt);
  
  public abstract void setSecondOfMinute(int paramInt);
  
  public abstract void addSeconds(int paramInt);
  
  public abstract void setMillisOfDay(int paramInt);
  
  public abstract void setMillisOfSecond(int paramInt);
  
  public abstract void addMillis(int paramInt);
  
  public abstract void setDate(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract void setTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract void setDateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\ReadWritableDateTime.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */