package com.facebook.presto.jdbc.internal.joda.time.convert;

import com.facebook.presto.jdbc.internal.joda.time.Chronology;
import com.facebook.presto.jdbc.internal.joda.time.PeriodType;
import com.facebook.presto.jdbc.internal.joda.time.ReadWritablePeriod;

public abstract interface PeriodConverter
  extends Converter
{
  public abstract void setInto(ReadWritablePeriod paramReadWritablePeriod, Object paramObject, Chronology paramChronology);
  
  public abstract PeriodType getPeriodType(Object paramObject);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\convert\PeriodConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */