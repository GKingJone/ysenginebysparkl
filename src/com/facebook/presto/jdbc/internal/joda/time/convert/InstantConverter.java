package com.facebook.presto.jdbc.internal.joda.time.convert;

import com.facebook.presto.jdbc.internal.joda.time.Chronology;
import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;

public abstract interface InstantConverter
  extends Converter
{
  public abstract Chronology getChronology(Object paramObject, DateTimeZone paramDateTimeZone);
  
  public abstract Chronology getChronology(Object paramObject, Chronology paramChronology);
  
  public abstract long getInstantMillis(Object paramObject, Chronology paramChronology);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\convert\InstantConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */