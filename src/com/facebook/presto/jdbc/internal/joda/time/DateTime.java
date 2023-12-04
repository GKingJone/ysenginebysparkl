/*      */ package com.facebook.presto.jdbc.internal.joda.time;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.joda.time.base.BaseDateTime;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.chrono.ISOChronology;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.field.AbstractReadableInstantFieldProperty;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.format.ISODateTimeFormat;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.util.Locale;
/*      */ import org.joda.convert.FromString;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class DateTime
/*      */   extends BaseDateTime
/*      */   implements ReadableDateTime, Serializable
/*      */ {
/*      */   private static final long serialVersionUID = -5171125899451703815L;
/*      */   
/*      */   public static DateTime now()
/*      */   {
/*   89 */     return new DateTime();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DateTime now(DateTimeZone paramDateTimeZone)
/*      */   {
/*  101 */     if (paramDateTimeZone == null) {
/*  102 */       throw new NullPointerException("Zone must not be null");
/*      */     }
/*  104 */     return new DateTime(paramDateTimeZone);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DateTime now(Chronology paramChronology)
/*      */   {
/*  116 */     if (paramChronology == null) {
/*  117 */       throw new NullPointerException("Chronology must not be null");
/*      */     }
/*  119 */     return new DateTime(paramChronology);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @FromString
/*      */   public static DateTime parse(String paramString)
/*      */   {
/*  149 */     return parse(paramString, ISODateTimeFormat.dateTimeParser().withOffsetParsed());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static DateTime parse(String paramString, DateTimeFormatter paramDateTimeFormatter)
/*      */   {
/*  160 */     return paramDateTimeFormatter.parseDateTime(paramString);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime() {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime(DateTimeZone paramDateTimeZone)
/*      */   {
/*  184 */     super(paramDateTimeZone);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime(Chronology paramChronology)
/*      */   {
/*  198 */     super(paramChronology);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime(long paramLong)
/*      */   {
/*  209 */     super(paramLong);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime(long paramLong, DateTimeZone paramDateTimeZone)
/*      */   {
/*  222 */     super(paramLong, paramDateTimeZone);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime(long paramLong, Chronology paramChronology)
/*      */   {
/*  236 */     super(paramLong, paramChronology);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime(Object paramObject)
/*      */   {
/*  257 */     super(paramObject, (Chronology)null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime(Object paramObject, DateTimeZone paramDateTimeZone)
/*      */   {
/*  281 */     super(paramObject, paramDateTimeZone);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime(Object paramObject, Chronology paramChronology)
/*      */   {
/*  302 */     super(paramObject, DateTimeUtils.getChronology(paramChronology));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/*  323 */     super(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, 0, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, DateTimeZone paramDateTimeZone)
/*      */   {
/*  347 */     super(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, 0, 0, paramDateTimeZone);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Chronology paramChronology)
/*      */   {
/*  373 */     super(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, 0, 0, paramChronology);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*      */   {
/*  397 */     super(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, DateTimeZone paramDateTimeZone)
/*      */   {
/*  423 */     super(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 0, paramDateTimeZone);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Chronology paramChronology)
/*      */   {
/*  451 */     super(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 0, paramChronology);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
/*      */   {
/*  476 */     super(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, DateTimeZone paramDateTimeZone)
/*      */   {
/*  503 */     super(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramDateTimeZone);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, Chronology paramChronology)
/*      */   {
/*  532 */     super(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramChronology);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime toDateTime()
/*      */   {
/*  543 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime toDateTimeISO()
/*      */   {
/*  553 */     if (getChronology() == ISOChronology.getInstance()) {
/*  554 */       return this;
/*      */     }
/*  556 */     return super.toDateTimeISO();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime toDateTime(DateTimeZone paramDateTimeZone)
/*      */   {
/*  566 */     paramDateTimeZone = DateTimeUtils.getZone(paramDateTimeZone);
/*  567 */     if (getZone() == paramDateTimeZone) {
/*  568 */       return this;
/*      */     }
/*  570 */     return super.toDateTime(paramDateTimeZone);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime toDateTime(Chronology paramChronology)
/*      */   {
/*  580 */     paramChronology = DateTimeUtils.getChronology(paramChronology);
/*  581 */     if (getChronology() == paramChronology) {
/*  582 */       return this;
/*      */     }
/*  584 */     return super.toDateTime(paramChronology);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withMillis(long paramLong)
/*      */   {
/*  598 */     return paramLong == getMillis() ? this : new DateTime(paramLong, getChronology());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withChronology(Chronology paramChronology)
/*      */   {
/*  611 */     paramChronology = DateTimeUtils.getChronology(paramChronology);
/*  612 */     return paramChronology == getChronology() ? this : new DateTime(getMillis(), paramChronology);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withZone(DateTimeZone paramDateTimeZone)
/*      */   {
/*  634 */     return withChronology(getChronology().withZone(paramDateTimeZone));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withZoneRetainFields(DateTimeZone paramDateTimeZone)
/*      */   {
/*  655 */     paramDateTimeZone = DateTimeUtils.getZone(paramDateTimeZone);
/*  656 */     DateTimeZone localDateTimeZone = DateTimeUtils.getZone(getZone());
/*  657 */     if (paramDateTimeZone == localDateTimeZone) {
/*  658 */       return this;
/*      */     }
/*      */     
/*  661 */     long l = localDateTimeZone.getMillisKeepLocal(paramDateTimeZone, getMillis());
/*  662 */     return new DateTime(l, getChronology().withZone(paramDateTimeZone));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withEarlierOffsetAtOverlap()
/*      */   {
/*  681 */     long l = getZone().adjustOffset(getMillis(), false);
/*  682 */     return withMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withLaterOffsetAtOverlap()
/*      */   {
/*  701 */     long l = getZone().adjustOffset(getMillis(), true);
/*  702 */     return withMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withDate(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/*  727 */     Chronology localChronology = getChronology();
/*  728 */     long l = localChronology.withUTC().getDateTimeMillis(paramInt1, paramInt2, paramInt3, getMillisOfDay());
/*  729 */     return withMillis(localChronology.getZone().convertLocalToUTC(l, false, getMillis()));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withDate(LocalDate paramLocalDate)
/*      */   {
/*  745 */     return withDate(paramLocalDate.getYear(), paramLocalDate.getMonthOfYear(), paramLocalDate.getDayOfMonth());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  771 */     Chronology localChronology = getChronology();
/*  772 */     long l = localChronology.withUTC().getDateTimeMillis(getYear(), getMonthOfYear(), getDayOfMonth(), paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     
/*  774 */     return withMillis(localChronology.getZone().convertLocalToUTC(l, false, getMillis()));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withTime(LocalTime paramLocalTime)
/*      */   {
/*  790 */     return withTime(paramLocalTime.getHourOfDay(), paramLocalTime.getMinuteOfHour(), paramLocalTime.getSecondOfMinute(), paramLocalTime.getMillisOfSecond());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withTimeAtStartOfDay()
/*      */   {
/*  807 */     return toLocalDate().toDateTimeAtStartOfDay(getZone());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withFields(ReadablePartial paramReadablePartial)
/*      */   {
/*  824 */     if (paramReadablePartial == null) {
/*  825 */       return this;
/*      */     }
/*  827 */     return withMillis(getChronology().set(paramReadablePartial, getMillis()));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withField(DateTimeFieldType paramDateTimeFieldType, int paramInt)
/*      */   {
/*  850 */     if (paramDateTimeFieldType == null) {
/*  851 */       throw new IllegalArgumentException("Field must not be null");
/*      */     }
/*  853 */     long l = paramDateTimeFieldType.getField(getChronology()).set(getMillis(), paramInt);
/*  854 */     return withMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withFieldAdded(DurationFieldType paramDurationFieldType, int paramInt)
/*      */   {
/*  876 */     if (paramDurationFieldType == null) {
/*  877 */       throw new IllegalArgumentException("Field must not be null");
/*      */     }
/*  879 */     if (paramInt == 0) {
/*  880 */       return this;
/*      */     }
/*  882 */     long l = paramDurationFieldType.getField(getChronology()).add(getMillis(), paramInt);
/*  883 */     return withMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withDurationAdded(long paramLong, int paramInt)
/*      */   {
/*  898 */     if ((paramLong == 0L) || (paramInt == 0)) {
/*  899 */       return this;
/*      */     }
/*  901 */     long l = getChronology().add(getMillis(), paramLong, paramInt);
/*  902 */     return withMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withDurationAdded(ReadableDuration paramReadableDuration, int paramInt)
/*      */   {
/*  916 */     if ((paramReadableDuration == null) || (paramInt == 0)) {
/*  917 */       return this;
/*      */     }
/*  919 */     return withDurationAdded(paramReadableDuration.getMillis(), paramInt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withPeriodAdded(ReadablePeriod paramReadablePeriod, int paramInt)
/*      */   {
/*  938 */     if ((paramReadablePeriod == null) || (paramInt == 0)) {
/*  939 */       return this;
/*      */     }
/*  941 */     long l = getChronology().add(paramReadablePeriod, getMillis(), paramInt);
/*  942 */     return withMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime plus(long paramLong)
/*      */   {
/*  957 */     return withDurationAdded(paramLong, 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime plus(ReadableDuration paramReadableDuration)
/*      */   {
/*  971 */     return withDurationAdded(paramReadableDuration, 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime plus(ReadablePeriod paramReadablePeriod)
/*      */   {
/*  997 */     return withPeriodAdded(paramReadablePeriod, 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime plusYears(int paramInt)
/*      */   {
/* 1024 */     if (paramInt == 0) {
/* 1025 */       return this;
/*      */     }
/* 1027 */     long l = getChronology().years().add(getMillis(), paramInt);
/* 1028 */     return withMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime plusMonths(int paramInt)
/*      */   {
/* 1054 */     if (paramInt == 0) {
/* 1055 */       return this;
/*      */     }
/* 1057 */     long l = getChronology().months().add(getMillis(), paramInt);
/* 1058 */     return withMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime plusWeeks(int paramInt)
/*      */   {
/* 1080 */     if (paramInt == 0) {
/* 1081 */       return this;
/*      */     }
/* 1083 */     long l = getChronology().weeks().add(getMillis(), paramInt);
/* 1084 */     return withMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime plusDays(int paramInt)
/*      */   {
/* 1115 */     if (paramInt == 0) {
/* 1116 */       return this;
/*      */     }
/* 1118 */     long l = getChronology().days().add(getMillis(), paramInt);
/* 1119 */     return withMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime plusHours(int paramInt)
/*      */   {
/* 1146 */     if (paramInt == 0) {
/* 1147 */       return this;
/*      */     }
/* 1149 */     long l = getChronology().hours().add(getMillis(), paramInt);
/* 1150 */     return withMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime plusMinutes(int paramInt)
/*      */   {
/* 1173 */     if (paramInt == 0) {
/* 1174 */       return this;
/*      */     }
/* 1176 */     long l = getChronology().minutes().add(getMillis(), paramInt);
/* 1177 */     return withMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime plusSeconds(int paramInt)
/*      */   {
/* 1200 */     if (paramInt == 0) {
/* 1201 */       return this;
/*      */     }
/* 1203 */     long l = getChronology().seconds().add(getMillis(), paramInt);
/* 1204 */     return withMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime plusMillis(int paramInt)
/*      */   {
/* 1226 */     if (paramInt == 0) {
/* 1227 */       return this;
/*      */     }
/* 1229 */     long l = getChronology().millis().add(getMillis(), paramInt);
/* 1230 */     return withMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime minus(long paramLong)
/*      */   {
/* 1245 */     return withDurationAdded(paramLong, -1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime minus(ReadableDuration paramReadableDuration)
/*      */   {
/* 1259 */     return withDurationAdded(paramReadableDuration, -1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime minus(ReadablePeriod paramReadablePeriod)
/*      */   {
/* 1286 */     return withPeriodAdded(paramReadablePeriod, -1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime minusYears(int paramInt)
/*      */   {
/* 1313 */     if (paramInt == 0) {
/* 1314 */       return this;
/*      */     }
/* 1316 */     long l = getChronology().years().subtract(getMillis(), paramInt);
/* 1317 */     return withMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime minusMonths(int paramInt)
/*      */   {
/* 1343 */     if (paramInt == 0) {
/* 1344 */       return this;
/*      */     }
/* 1346 */     long l = getChronology().months().subtract(getMillis(), paramInt);
/* 1347 */     return withMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime minusWeeks(int paramInt)
/*      */   {
/* 1369 */     if (paramInt == 0) {
/* 1370 */       return this;
/*      */     }
/* 1372 */     long l = getChronology().weeks().subtract(getMillis(), paramInt);
/* 1373 */     return withMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime minusDays(int paramInt)
/*      */   {
/* 1404 */     if (paramInt == 0) {
/* 1405 */       return this;
/*      */     }
/* 1407 */     long l = getChronology().days().subtract(getMillis(), paramInt);
/* 1408 */     return withMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime minusHours(int paramInt)
/*      */   {
/* 1436 */     if (paramInt == 0) {
/* 1437 */       return this;
/*      */     }
/* 1439 */     long l = getChronology().hours().subtract(getMillis(), paramInt);
/* 1440 */     return withMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime minusMinutes(int paramInt)
/*      */   {
/* 1463 */     if (paramInt == 0) {
/* 1464 */       return this;
/*      */     }
/* 1466 */     long l = getChronology().minutes().subtract(getMillis(), paramInt);
/* 1467 */     return withMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime minusSeconds(int paramInt)
/*      */   {
/* 1490 */     if (paramInt == 0) {
/* 1491 */       return this;
/*      */     }
/* 1493 */     long l = getChronology().seconds().subtract(getMillis(), paramInt);
/* 1494 */     return withMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime minusMillis(int paramInt)
/*      */   {
/* 1517 */     if (paramInt == 0) {
/* 1518 */       return this;
/*      */     }
/* 1520 */     long l = getChronology().millis().subtract(getMillis(), paramInt);
/* 1521 */     return withMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property property(DateTimeFieldType paramDateTimeFieldType)
/*      */   {
/* 1533 */     if (paramDateTimeFieldType == null) {
/* 1534 */       throw new IllegalArgumentException("The DateTimeFieldType must not be null");
/*      */     }
/* 1536 */     DateTimeField localDateTimeField = paramDateTimeFieldType.getField(getChronology());
/* 1537 */     if (!localDateTimeField.isSupported()) {
/* 1538 */       throw new IllegalArgumentException("Field '" + paramDateTimeFieldType + "' is not supported");
/*      */     }
/* 1540 */     return new Property(this, localDateTimeField);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public DateMidnight toDateMidnight()
/*      */   {
/* 1553 */     return new DateMidnight(getMillis(), getChronology());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public YearMonthDay toYearMonthDay()
/*      */   {
/* 1565 */     return new YearMonthDay(getMillis(), getChronology());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public TimeOfDay toTimeOfDay()
/*      */   {
/* 1577 */     return new TimeOfDay(getMillis(), getChronology());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime toLocalDateTime()
/*      */   {
/* 1588 */     return new LocalDateTime(getMillis(), getChronology());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDate toLocalDate()
/*      */   {
/* 1599 */     return new LocalDate(getMillis(), getChronology());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalTime toLocalTime()
/*      */   {
/* 1610 */     return new LocalTime(getMillis(), getChronology());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withEra(int paramInt)
/*      */   {
/* 1627 */     return withMillis(getChronology().era().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withCenturyOfEra(int paramInt)
/*      */   {
/* 1643 */     return withMillis(getChronology().centuryOfEra().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withYearOfEra(int paramInt)
/*      */   {
/* 1659 */     return withMillis(getChronology().yearOfEra().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withYearOfCentury(int paramInt)
/*      */   {
/* 1675 */     return withMillis(getChronology().yearOfCentury().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withYear(int paramInt)
/*      */   {
/* 1691 */     return withMillis(getChronology().year().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withWeekyear(int paramInt)
/*      */   {
/* 1713 */     return withMillis(getChronology().weekyear().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withMonthOfYear(int paramInt)
/*      */   {
/* 1729 */     return withMillis(getChronology().monthOfYear().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withWeekOfWeekyear(int paramInt)
/*      */   {
/* 1750 */     return withMillis(getChronology().weekOfWeekyear().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withDayOfYear(int paramInt)
/*      */   {
/* 1766 */     return withMillis(getChronology().dayOfYear().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withDayOfMonth(int paramInt)
/*      */   {
/* 1782 */     return withMillis(getChronology().dayOfMonth().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withDayOfWeek(int paramInt)
/*      */   {
/* 1798 */     return withMillis(getChronology().dayOfWeek().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withHourOfDay(int paramInt)
/*      */   {
/* 1815 */     return withMillis(getChronology().hourOfDay().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withMinuteOfHour(int paramInt)
/*      */   {
/* 1831 */     return withMillis(getChronology().minuteOfHour().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withSecondOfMinute(int paramInt)
/*      */   {
/* 1847 */     return withMillis(getChronology().secondOfMinute().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withMillisOfSecond(int paramInt)
/*      */   {
/* 1863 */     return withMillis(getChronology().millisOfSecond().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime withMillisOfDay(int paramInt)
/*      */   {
/* 1879 */     return withMillis(getChronology().millisOfDay().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property era()
/*      */   {
/* 1890 */     return new Property(this, getChronology().era());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property centuryOfEra()
/*      */   {
/* 1899 */     return new Property(this, getChronology().centuryOfEra());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property yearOfCentury()
/*      */   {
/* 1908 */     return new Property(this, getChronology().yearOfCentury());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property yearOfEra()
/*      */   {
/* 1917 */     return new Property(this, getChronology().yearOfEra());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property year()
/*      */   {
/* 1926 */     return new Property(this, getChronology().year());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property weekyear()
/*      */   {
/* 1935 */     return new Property(this, getChronology().weekyear());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property monthOfYear()
/*      */   {
/* 1944 */     return new Property(this, getChronology().monthOfYear());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property weekOfWeekyear()
/*      */   {
/* 1953 */     return new Property(this, getChronology().weekOfWeekyear());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property dayOfYear()
/*      */   {
/* 1962 */     return new Property(this, getChronology().dayOfYear());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property dayOfMonth()
/*      */   {
/* 1971 */     return new Property(this, getChronology().dayOfMonth());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property dayOfWeek()
/*      */   {
/* 1980 */     return new Property(this, getChronology().dayOfWeek());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property hourOfDay()
/*      */   {
/* 1991 */     return new Property(this, getChronology().hourOfDay());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property minuteOfDay()
/*      */   {
/* 2000 */     return new Property(this, getChronology().minuteOfDay());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property minuteOfHour()
/*      */   {
/* 2009 */     return new Property(this, getChronology().minuteOfHour());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property secondOfDay()
/*      */   {
/* 2018 */     return new Property(this, getChronology().secondOfDay());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property secondOfMinute()
/*      */   {
/* 2027 */     return new Property(this, getChronology().secondOfMinute());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property millisOfDay()
/*      */   {
/* 2036 */     return new Property(this, getChronology().millisOfDay());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property millisOfSecond()
/*      */   {
/* 2045 */     return new Property(this, getChronology().millisOfSecond());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final class Property
/*      */     extends AbstractReadableInstantFieldProperty
/*      */   {
/*      */     private static final long serialVersionUID = -6983323811635733510L;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private DateTime iInstant;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private DateTimeField iField;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     Property(DateTime paramDateTime, DateTimeField paramDateTimeField)
/*      */     {
/* 2096 */       this.iInstant = paramDateTime;
/* 2097 */       this.iField = paramDateTimeField;
/*      */     }
/*      */     
/*      */ 
/*      */     private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*      */       throws IOException
/*      */     {
/* 2104 */       paramObjectOutputStream.writeObject(this.iInstant);
/* 2105 */       paramObjectOutputStream.writeObject(this.iField.getType());
/*      */     }
/*      */     
/*      */ 
/*      */     private void readObject(ObjectInputStream paramObjectInputStream)
/*      */       throws IOException, ClassNotFoundException
/*      */     {
/* 2112 */       this.iInstant = ((DateTime)paramObjectInputStream.readObject());
/* 2113 */       DateTimeFieldType localDateTimeFieldType = (DateTimeFieldType)paramObjectInputStream.readObject();
/* 2114 */       this.iField = localDateTimeFieldType.getField(this.iInstant.getChronology());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public DateTimeField getField()
/*      */     {
/* 2124 */       return this.iField;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected long getMillis()
/*      */     {
/* 2133 */       return this.iInstant.getMillis();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected Chronology getChronology()
/*      */     {
/* 2143 */       return this.iInstant.getChronology();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public DateTime getDateTime()
/*      */     {
/* 2152 */       return this.iInstant;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public DateTime addToCopy(int paramInt)
/*      */     {
/* 2169 */       return this.iInstant.withMillis(this.iField.add(this.iInstant.getMillis(), paramInt));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public DateTime addToCopy(long paramLong)
/*      */     {
/* 2185 */       return this.iInstant.withMillis(this.iField.add(this.iInstant.getMillis(), paramLong));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public DateTime addWrapFieldToCopy(int paramInt)
/*      */     {
/* 2203 */       return this.iInstant.withMillis(this.iField.addWrapField(this.iInstant.getMillis(), paramInt));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public DateTime setCopy(int paramInt)
/*      */     {
/* 2220 */       return this.iInstant.withMillis(this.iField.set(this.iInstant.getMillis(), paramInt));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public DateTime setCopy(String paramString, Locale paramLocale)
/*      */     {
/* 2237 */       return this.iInstant.withMillis(this.iField.set(this.iInstant.getMillis(), paramString, paramLocale));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public DateTime setCopy(String paramString)
/*      */     {
/* 2253 */       return setCopy(paramString, null);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public DateTime withMaximumValue()
/*      */     {
/*      */       try
/*      */       {
/* 2281 */         return setCopy(getMaximumValue());
/*      */       } catch (RuntimeException localRuntimeException) {
/* 2283 */         if (IllegalInstantException.isIllegalInstant(localRuntimeException))
/*      */         {
/* 2285 */           long l = getChronology().getZone().previousTransition(getMillis() + 86400000L);
/* 2286 */           return new DateTime(l, getChronology());
/*      */         }
/* 2288 */         throw localRuntimeException;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public DateTime withMinimumValue()
/*      */     {
/*      */       try
/*      */       {
/* 2310 */         return setCopy(getMinimumValue());
/*      */       } catch (RuntimeException localRuntimeException) {
/* 2312 */         if (IllegalInstantException.isIllegalInstant(localRuntimeException))
/*      */         {
/* 2314 */           long l = getChronology().getZone().nextTransition(getMillis() - 86400000L);
/* 2315 */           return new DateTime(l, getChronology());
/*      */         }
/* 2317 */         throw localRuntimeException;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public DateTime roundFloorCopy()
/*      */     {
/* 2328 */       return this.iInstant.withMillis(this.iField.roundFloor(this.iInstant.getMillis()));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public DateTime roundCeilingCopy()
/*      */     {
/* 2337 */       return this.iInstant.withMillis(this.iField.roundCeiling(this.iInstant.getMillis()));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public DateTime roundHalfFloorCopy()
/*      */     {
/* 2347 */       return this.iInstant.withMillis(this.iField.roundHalfFloor(this.iInstant.getMillis()));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public DateTime roundHalfCeilingCopy()
/*      */     {
/* 2357 */       return this.iInstant.withMillis(this.iField.roundHalfCeiling(this.iInstant.getMillis()));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public DateTime roundHalfEvenCopy()
/*      */     {
/* 2368 */       return this.iInstant.withMillis(this.iField.roundHalfEven(this.iInstant.getMillis()));
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\DateTime.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */