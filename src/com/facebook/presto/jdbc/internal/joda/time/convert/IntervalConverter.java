package com.facebook.presto.jdbc.internal.joda.time.convert;

import com.facebook.presto.jdbc.internal.joda.time.Chronology;
import com.facebook.presto.jdbc.internal.joda.time.ReadWritableInterval;

public abstract interface IntervalConverter
  extends Converter
{
  public abstract boolean isReadableInterval(Object paramObject, Chronology paramChronology);
  
  public abstract void setInto(ReadWritableInterval paramReadWritableInterval, Object paramObject, Chronology paramChronology);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\convert\IntervalConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */