/*     */ package com.facebook.presto.jdbc.internal.joda.time.base;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadableDateTime;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormat;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*     */ import java.util.Calendar;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.Locale;
/*     */ import org.joda.convert.ToString;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractDateTime
/*     */   extends AbstractInstant
/*     */   implements ReadableDateTime
/*     */ {
/*     */   public int get(DateTimeFieldType paramDateTimeFieldType)
/*     */   {
/*  67 */     if (paramDateTimeFieldType == null) {
/*  68 */       throw new IllegalArgumentException("The DateTimeFieldType must not be null");
/*     */     }
/*  70 */     return paramDateTimeFieldType.getField(getChronology()).get(getMillis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getEra()
/*     */   {
/*  80 */     return getChronology().era().get(getMillis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getCenturyOfEra()
/*     */   {
/*  89 */     return getChronology().centuryOfEra().get(getMillis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getYearOfEra()
/*     */   {
/*  98 */     return getChronology().yearOfEra().get(getMillis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getYearOfCentury()
/*     */   {
/* 107 */     return getChronology().yearOfCentury().get(getMillis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getYear()
/*     */   {
/* 116 */     return getChronology().year().get(getMillis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getWeekyear()
/*     */   {
/* 131 */     return getChronology().weekyear().get(getMillis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMonthOfYear()
/*     */   {
/* 140 */     return getChronology().monthOfYear().get(getMillis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getWeekOfWeekyear()
/*     */   {
/* 154 */     return getChronology().weekOfWeekyear().get(getMillis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getDayOfYear()
/*     */   {
/* 163 */     return getChronology().dayOfYear().get(getMillis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getDayOfMonth()
/*     */   {
/* 174 */     return getChronology().dayOfMonth().get(getMillis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getDayOfWeek()
/*     */   {
/* 185 */     return getChronology().dayOfWeek().get(getMillis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getHourOfDay()
/*     */   {
/* 195 */     return getChronology().hourOfDay().get(getMillis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMinuteOfDay()
/*     */   {
/* 204 */     return getChronology().minuteOfDay().get(getMillis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMinuteOfHour()
/*     */   {
/* 213 */     return getChronology().minuteOfHour().get(getMillis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getSecondOfDay()
/*     */   {
/* 222 */     return getChronology().secondOfDay().get(getMillis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getSecondOfMinute()
/*     */   {
/* 231 */     return getChronology().secondOfMinute().get(getMillis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMillisOfDay()
/*     */   {
/* 240 */     return getChronology().millisOfDay().get(getMillis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMillisOfSecond()
/*     */   {
/* 249 */     return getChronology().millisOfSecond().get(getMillis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Calendar toCalendar(Locale paramLocale)
/*     */   {
/* 271 */     if (paramLocale == null) {
/* 272 */       paramLocale = Locale.getDefault();
/*     */     }
/* 274 */     DateTimeZone localDateTimeZone = getZone();
/* 275 */     Calendar localCalendar = Calendar.getInstance(localDateTimeZone.toTimeZone(), paramLocale);
/* 276 */     localCalendar.setTime(toDate());
/* 277 */     return localCalendar;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public GregorianCalendar toGregorianCalendar()
/*     */   {
/* 295 */     DateTimeZone localDateTimeZone = getZone();
/* 296 */     GregorianCalendar localGregorianCalendar = new GregorianCalendar(localDateTimeZone.toTimeZone());
/* 297 */     localGregorianCalendar.setTime(toDate());
/* 298 */     return localGregorianCalendar;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @ToString
/*     */   public String toString()
/*     */   {
/* 314 */     return super.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString(String paramString)
/*     */   {
/* 325 */     if (paramString == null) {
/* 326 */       return toString();
/*     */     }
/* 328 */     return DateTimeFormat.forPattern(paramString).print(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString(String paramString, Locale paramLocale)
/*     */     throws IllegalArgumentException
/*     */   {
/* 340 */     if (paramString == null) {
/* 341 */       return toString();
/*     */     }
/* 343 */     return DateTimeFormat.forPattern(paramString).withLocale(paramLocale).print(this);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\base\AbstractDateTime.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */