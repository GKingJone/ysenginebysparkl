package com.facebook.presto.jdbc.internal.joda.time.tz;

import java.util.Locale;

public abstract interface NameProvider
{
  public abstract String getShortName(Locale paramLocale, String paramString1, String paramString2);
  
  public abstract String getName(Locale paramLocale, String paramString1, String paramString2);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\tz\NameProvider.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */