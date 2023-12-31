package com.facebook.presto.jdbc.internal.joda.time;

import java.util.Locale;

public abstract class DateTimeField
{
  public abstract DateTimeFieldType getType();
  
  public abstract String getName();
  
  public abstract boolean isSupported();
  
  public abstract boolean isLenient();
  
  public abstract int get(long paramLong);
  
  public abstract String getAsText(long paramLong, Locale paramLocale);
  
  public abstract String getAsText(long paramLong);
  
  public abstract String getAsText(ReadablePartial paramReadablePartial, int paramInt, Locale paramLocale);
  
  public abstract String getAsText(ReadablePartial paramReadablePartial, Locale paramLocale);
  
  public abstract String getAsText(int paramInt, Locale paramLocale);
  
  public abstract String getAsShortText(long paramLong, Locale paramLocale);
  
  public abstract String getAsShortText(long paramLong);
  
  public abstract String getAsShortText(ReadablePartial paramReadablePartial, int paramInt, Locale paramLocale);
  
  public abstract String getAsShortText(ReadablePartial paramReadablePartial, Locale paramLocale);
  
  public abstract String getAsShortText(int paramInt, Locale paramLocale);
  
  public abstract long add(long paramLong, int paramInt);
  
  public abstract long add(long paramLong1, long paramLong2);
  
  public abstract int[] add(ReadablePartial paramReadablePartial, int paramInt1, int[] paramArrayOfInt, int paramInt2);
  
  public abstract int[] addWrapPartial(ReadablePartial paramReadablePartial, int paramInt1, int[] paramArrayOfInt, int paramInt2);
  
  public abstract long addWrapField(long paramLong, int paramInt);
  
  public abstract int[] addWrapField(ReadablePartial paramReadablePartial, int paramInt1, int[] paramArrayOfInt, int paramInt2);
  
  public abstract int getDifference(long paramLong1, long paramLong2);
  
  public abstract long getDifferenceAsLong(long paramLong1, long paramLong2);
  
  public abstract long set(long paramLong, int paramInt);
  
  public abstract int[] set(ReadablePartial paramReadablePartial, int paramInt1, int[] paramArrayOfInt, int paramInt2);
  
  public abstract long set(long paramLong, String paramString, Locale paramLocale);
  
  public abstract long set(long paramLong, String paramString);
  
  public abstract int[] set(ReadablePartial paramReadablePartial, int paramInt, int[] paramArrayOfInt, String paramString, Locale paramLocale);
  
  public abstract DurationField getDurationField();
  
  public abstract DurationField getRangeDurationField();
  
  public abstract boolean isLeap(long paramLong);
  
  public abstract int getLeapAmount(long paramLong);
  
  public abstract DurationField getLeapDurationField();
  
  public abstract int getMinimumValue();
  
  public abstract int getMinimumValue(long paramLong);
  
  public abstract int getMinimumValue(ReadablePartial paramReadablePartial);
  
  public abstract int getMinimumValue(ReadablePartial paramReadablePartial, int[] paramArrayOfInt);
  
  public abstract int getMaximumValue();
  
  public abstract int getMaximumValue(long paramLong);
  
  public abstract int getMaximumValue(ReadablePartial paramReadablePartial);
  
  public abstract int getMaximumValue(ReadablePartial paramReadablePartial, int[] paramArrayOfInt);
  
  public abstract int getMaximumTextLength(Locale paramLocale);
  
  public abstract int getMaximumShortTextLength(Locale paramLocale);
  
  public abstract long roundFloor(long paramLong);
  
  public abstract long roundCeiling(long paramLong);
  
  public abstract long roundHalfFloor(long paramLong);
  
  public abstract long roundHalfCeiling(long paramLong);
  
  public abstract long roundHalfEven(long paramLong);
  
  public abstract long remainder(long paramLong);
  
  public abstract String toString();
}


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\DateTimeField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */