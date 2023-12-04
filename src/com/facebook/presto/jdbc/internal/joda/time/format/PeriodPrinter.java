package com.facebook.presto.jdbc.internal.joda.time.format;

import com.facebook.presto.jdbc.internal.joda.time.ReadablePeriod;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

public abstract interface PeriodPrinter
{
  public abstract int calculatePrintedLength(ReadablePeriod paramReadablePeriod, Locale paramLocale);
  
  public abstract int countFieldsToPrint(ReadablePeriod paramReadablePeriod, int paramInt, Locale paramLocale);
  
  public abstract void printTo(StringBuffer paramStringBuffer, ReadablePeriod paramReadablePeriod, Locale paramLocale);
  
  public abstract void printTo(Writer paramWriter, ReadablePeriod paramReadablePeriod, Locale paramLocale)
    throws IOException;
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\format\PeriodPrinter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */