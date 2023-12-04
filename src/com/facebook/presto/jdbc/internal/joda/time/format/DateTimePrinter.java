package com.facebook.presto.jdbc.internal.joda.time.format;

import com.facebook.presto.jdbc.internal.joda.time.Chronology;
import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
import com.facebook.presto.jdbc.internal.joda.time.ReadablePartial;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

public abstract interface DateTimePrinter
{
  public abstract int estimatePrintedLength();
  
  public abstract void printTo(StringBuffer paramStringBuffer, long paramLong, Chronology paramChronology, int paramInt, DateTimeZone paramDateTimeZone, Locale paramLocale);
  
  public abstract void printTo(Writer paramWriter, long paramLong, Chronology paramChronology, int paramInt, DateTimeZone paramDateTimeZone, Locale paramLocale)
    throws IOException;
  
  public abstract void printTo(StringBuffer paramStringBuffer, ReadablePartial paramReadablePartial, Locale paramLocale);
  
  public abstract void printTo(Writer paramWriter, ReadablePartial paramReadablePartial, Locale paramLocale)
    throws IOException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\format\DateTimePrinter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */