package com.facebook.presto.jdbc.internal.joda.time.format;

import com.facebook.presto.jdbc.internal.joda.time.ReadWritablePeriod;
import java.util.Locale;

public abstract interface PeriodParser
{
  public abstract int parseInto(ReadWritablePeriod paramReadWritablePeriod, String paramString, int paramInt, Locale paramLocale);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\format\PeriodParser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */