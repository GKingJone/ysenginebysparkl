package com.facebook.presto.jdbc.internal.joda.time.tz;

import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
import java.util.Set;

public abstract interface Provider
{
  public abstract DateTimeZone getZone(String paramString);
  
  public abstract Set<String> getAvailableIDs();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\tz\Provider.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */