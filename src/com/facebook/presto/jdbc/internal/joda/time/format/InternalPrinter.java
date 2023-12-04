package com.facebook.presto.jdbc.internal.joda.time.format;

import com.facebook.presto.jdbc.internal.joda.time.Chronology;
import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
import com.facebook.presto.jdbc.internal.joda.time.ReadablePartial;
import java.io.IOException;
import java.util.Locale;

abstract interface InternalPrinter
{
  public abstract int estimatePrintedLength();
  
  public abstract void printTo(Appendable paramAppendable, long paramLong, Chronology paramChronology, int paramInt, DateTimeZone paramDateTimeZone, Locale paramLocale)
    throws IOException;
  
  public abstract void printTo(Appendable paramAppendable, ReadablePartial paramReadablePartial, Locale paramLocale)
    throws IOException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\format\InternalPrinter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */