package com.facebook.presto.jdbc.internal.joda.time.convert;

import com.facebook.presto.jdbc.internal.joda.time.Chronology;
import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
import com.facebook.presto.jdbc.internal.joda.time.ReadablePartial;
import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;

public abstract interface PartialConverter
  extends Converter
{
  public abstract Chronology getChronology(Object paramObject, DateTimeZone paramDateTimeZone);
  
  public abstract Chronology getChronology(Object paramObject, Chronology paramChronology);
  
  public abstract int[] getPartialValues(ReadablePartial paramReadablePartial, Object paramObject, Chronology paramChronology);
  
  public abstract int[] getPartialValues(ReadablePartial paramReadablePartial, Object paramObject, Chronology paramChronology, DateTimeFormatter paramDateTimeFormatter);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\convert\PartialConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */