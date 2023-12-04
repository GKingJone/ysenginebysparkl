package com.facebook.presto.jdbc.internal.joda.time.format;

abstract interface InternalParser
{
  public abstract int estimateParsedLength();
  
  public abstract int parseInto(DateTimeParserBucket paramDateTimeParserBucket, CharSequence paramCharSequence, int paramInt);
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\format\InternalParser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */