package com.facebook.presto.jdbc.internal.joda.time.convert;

public abstract interface DurationConverter
  extends Converter
{
  public abstract long getDurationMillis(Object paramObject);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\convert\DurationConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */