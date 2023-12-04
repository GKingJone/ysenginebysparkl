package com.facebook.presto.jdbc.internal.joda.time.format;

public abstract interface DateTimeParser
{
  public abstract int estimateParsedLength();
  
  public abstract int parseInto(DateTimeParserBucket paramDateTimeParserBucket, String paramString, int paramInt);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\format\DateTimeParser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */