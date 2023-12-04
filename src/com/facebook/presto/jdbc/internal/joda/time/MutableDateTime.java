/*      */ package com.facebook.presto.jdbc.internal.joda.time;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.joda.time.base.BaseDateTime;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.chrono.ISOChronology;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.field.AbstractReadableInstantFieldProperty;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.field.FieldUtils;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class MutableDateTime
/*      */   extends BaseDateTime
/*      */   implements ReadWritableDateTime, Cloneable, Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 2852608688135209575L;
/*      */   public static final int ROUND_NONE = 0;
/*      */   public static final int ROUND_FLOOR = 1;
/*      */   public static final int ROUND_CEILING = 2;
/*      */   public static final int ROUND_HALF_FLOOR = 3;
/*      */   public static final int ROUND_HALF_CEILING = 4;
/*      */   public static final int ROUND_HALF_EVEN = 5;
/*      */   private DateTimeField iRoundingField;
/*      */   private int iRoundingMode;
/*      */   
/*      */   public static MutableDateTime now()
/*      */   {
/*  105 */     return new MutableDateTime();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static MutableDateTime now(DateTimeZone paramDateTimeZone)
/*      */   {
/*  117 */     if (paramDateTimeZone == null) {
/*  118 */       throw new NullPointerException("Zone must not be null");
/*      */     }
/*  120 */     return new MutableDateTime(paramDateTimeZone);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static MutableDateTime now(Chronology paramChronology)
/*      */   {
/*  132 */     if (paramChronology == null) {
/*  133 */       throw new NullPointerException("Chronology must not be null");
/*      */     }
/*  135 */     return new MutableDateTime(paramChronology);
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
/*      */   @FromString
/*      */   public static MutableDateTime parse(String paramString)
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
/*      */   public static MutableDateTime parse(String paramString, DateTimeFormatter paramDateTimeFormatter)
/*      */   {
/*  160 */     return paramDateTimeFormatter.parseDateTime(paramString).toMutableDateTime();
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
/*      */   public MutableDateTime() {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public MutableDateTime(DateTimeZone paramDateTimeZone)
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
/*      */   public MutableDateTime(Chronology paramChronology)
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
/*      */   public MutableDateTime(long paramLong)
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
/*      */   public MutableDateTime(long paramLong, DateTimeZone paramDateTimeZone)
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
/*      */   public MutableDateTime(long paramLong, Chronology paramChronology)
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
/*      */   public MutableDateTime(Object paramObject)
/*      */   {
/*  256 */     super(paramObject, (Chronology)null);
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
/*      */   public MutableDateTime(Object paramObject, DateTimeZone paramDateTimeZone)
/*      */   {
/*  279 */     super(paramObject, paramDateTimeZone);
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
/*      */   public MutableDateTime(Object paramObject, Chronology paramChronology)
/*      */   {
/*  299 */     super(paramObject, DateTimeUtils.getChronology(paramChronology));
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
/*      */   public MutableDateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
/*      */   {
/*  323 */     super(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
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
/*      */   public MutableDateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, DateTimeZone paramDateTimeZone)
/*      */   {
/*  350 */     super(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramDateTimeZone);
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
/*      */   public MutableDateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, Chronology paramChronology)
/*      */   {
/*  379 */     super(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramChronology);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeField getRoundingField()
/*      */   {
/*  391 */     return this.iRoundingField;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getRoundingMode()
/*      */   {
/*  401 */     return this.iRoundingMode;
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
/*      */   public void setRounding(DateTimeField paramDateTimeField)
/*      */   {
/*  417 */     setRounding(paramDateTimeField, 1);
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
/*      */   public void setRounding(DateTimeField paramDateTimeField, int paramInt)
/*      */   {
/*  435 */     if ((paramDateTimeField != null) && ((paramInt < 0) || (paramInt > 5))) {
/*  436 */       throw new IllegalArgumentException("Illegal rounding mode: " + paramInt);
/*      */     }
/*  438 */     this.iRoundingField = (paramInt == 0 ? null : paramDateTimeField);
/*  439 */     this.iRoundingMode = (paramDateTimeField == null ? 0 : paramInt);
/*  440 */     setMillis(getMillis());
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
/*      */   public void setMillis(long paramLong)
/*      */   {
/*  453 */     switch (this.iRoundingMode) {
/*      */     case 0: 
/*      */       break;
/*      */     case 1: 
/*  457 */       paramLong = this.iRoundingField.roundFloor(paramLong);
/*  458 */       break;
/*      */     case 2: 
/*  460 */       paramLong = this.iRoundingField.roundCeiling(paramLong);
/*  461 */       break;
/*      */     case 3: 
/*  463 */       paramLong = this.iRoundingField.roundHalfFloor(paramLong);
/*  464 */       break;
/*      */     case 4: 
/*  466 */       paramLong = this.iRoundingField.roundHalfCeiling(paramLong);
/*  467 */       break;
/*      */     case 5: 
/*  469 */       paramLong = this.iRoundingField.roundHalfEven(paramLong);
/*      */     }
/*      */     
/*  472 */     super.setMillis(paramLong);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setMillis(ReadableInstant paramReadableInstant)
/*      */   {
/*  484 */     long l = DateTimeUtils.getInstantMillis(paramReadableInstant);
/*  485 */     setMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void add(long paramLong)
/*      */   {
/*  496 */     setMillis(FieldUtils.safeAdd(getMillis(), paramLong));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void add(ReadableDuration paramReadableDuration)
/*      */   {
/*  508 */     add(paramReadableDuration, 1);
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
/*      */   public void add(ReadableDuration paramReadableDuration, int paramInt)
/*      */   {
/*  521 */     if (paramReadableDuration != null) {
/*  522 */       add(FieldUtils.safeMultiply(paramReadableDuration.getMillis(), paramInt));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void add(ReadablePeriod paramReadablePeriod)
/*      */   {
/*  535 */     add(paramReadablePeriod, 1);
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
/*      */   public void add(ReadablePeriod paramReadablePeriod, int paramInt)
/*      */   {
/*  548 */     if (paramReadablePeriod != null) {
/*  549 */       setMillis(getChronology().add(paramReadablePeriod, getMillis(), paramInt));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setChronology(Chronology paramChronology)
/*      */   {
/*  562 */     super.setChronology(paramChronology);
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
/*      */   public void setZone(DateTimeZone paramDateTimeZone)
/*      */   {
/*  581 */     paramDateTimeZone = DateTimeUtils.getZone(paramDateTimeZone);
/*  582 */     Chronology localChronology = getChronology();
/*  583 */     if (localChronology.getZone() != paramDateTimeZone) {
/*  584 */       setChronology(localChronology.withZone(paramDateTimeZone));
/*      */     }
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
/*      */   public void setZoneRetainFields(DateTimeZone paramDateTimeZone)
/*      */   {
/*  600 */     paramDateTimeZone = DateTimeUtils.getZone(paramDateTimeZone);
/*  601 */     DateTimeZone localDateTimeZone = DateTimeUtils.getZone(getZone());
/*  602 */     if (paramDateTimeZone == localDateTimeZone) {
/*  603 */       return;
/*      */     }
/*      */     
/*  606 */     long l = localDateTimeZone.getMillisKeepLocal(paramDateTimeZone, getMillis());
/*  607 */     setChronology(getChronology().withZone(paramDateTimeZone));
/*  608 */     setMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void set(DateTimeFieldType paramDateTimeFieldType, int paramInt)
/*      */   {
/*  620 */     if (paramDateTimeFieldType == null) {
/*  621 */       throw new IllegalArgumentException("Field must not be null");
/*      */     }
/*  623 */     setMillis(paramDateTimeFieldType.getField(getChronology()).set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void add(DurationFieldType paramDurationFieldType, int paramInt)
/*      */   {
/*  635 */     if (paramDurationFieldType == null) {
/*  636 */       throw new IllegalArgumentException("Field must not be null");
/*      */     }
/*  638 */     if (paramInt != 0) {
/*  639 */       setMillis(paramDurationFieldType.getField(getChronology()).add(getMillis(), paramInt));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setYear(int paramInt)
/*      */   {
/*  651 */     setMillis(getChronology().year().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addYears(int paramInt)
/*      */   {
/*  661 */     if (paramInt != 0) {
/*  662 */       setMillis(getChronology().years().add(getMillis(), paramInt));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setWeekyear(int paramInt)
/*      */   {
/*  674 */     setMillis(getChronology().weekyear().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addWeekyears(int paramInt)
/*      */   {
/*  684 */     if (paramInt != 0) {
/*  685 */       setMillis(getChronology().weekyears().add(getMillis(), paramInt));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setMonthOfYear(int paramInt)
/*      */   {
/*  697 */     setMillis(getChronology().monthOfYear().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addMonths(int paramInt)
/*      */   {
/*  707 */     if (paramInt != 0) {
/*  708 */       setMillis(getChronology().months().add(getMillis(), paramInt));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setWeekOfWeekyear(int paramInt)
/*      */   {
/*  720 */     setMillis(getChronology().weekOfWeekyear().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addWeeks(int paramInt)
/*      */   {
/*  730 */     if (paramInt != 0) {
/*  731 */       setMillis(getChronology().weeks().add(getMillis(), paramInt));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setDayOfYear(int paramInt)
/*      */   {
/*  743 */     setMillis(getChronology().dayOfYear().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setDayOfMonth(int paramInt)
/*      */   {
/*  753 */     setMillis(getChronology().dayOfMonth().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setDayOfWeek(int paramInt)
/*      */   {
/*  763 */     setMillis(getChronology().dayOfWeek().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addDays(int paramInt)
/*      */   {
/*  773 */     if (paramInt != 0) {
/*  774 */       setMillis(getChronology().days().add(getMillis(), paramInt));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setHourOfDay(int paramInt)
/*      */   {
/*  786 */     setMillis(getChronology().hourOfDay().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addHours(int paramInt)
/*      */   {
/*  796 */     if (paramInt != 0) {
/*  797 */       setMillis(getChronology().hours().add(getMillis(), paramInt));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setMinuteOfDay(int paramInt)
/*      */   {
/*  809 */     setMillis(getChronology().minuteOfDay().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setMinuteOfHour(int paramInt)
/*      */   {
/*  819 */     setMillis(getChronology().minuteOfHour().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addMinutes(int paramInt)
/*      */   {
/*  829 */     if (paramInt != 0) {
/*  830 */       setMillis(getChronology().minutes().add(getMillis(), paramInt));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setSecondOfDay(int paramInt)
/*      */   {
/*  842 */     setMillis(getChronology().secondOfDay().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setSecondOfMinute(int paramInt)
/*      */   {
/*  852 */     setMillis(getChronology().secondOfMinute().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addSeconds(int paramInt)
/*      */   {
/*  862 */     if (paramInt != 0) {
/*  863 */       setMillis(getChronology().seconds().add(getMillis(), paramInt));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setMillisOfDay(int paramInt)
/*      */   {
/*  875 */     setMillis(getChronology().millisOfDay().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setMillisOfSecond(int paramInt)
/*      */   {
/*  885 */     setMillis(getChronology().millisOfSecond().set(getMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addMillis(int paramInt)
/*      */   {
/*  897 */     if (paramInt != 0) {
/*  898 */       setMillis(getChronology().millis().add(getMillis(), paramInt));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setDate(long paramLong)
/*      */   {
/*  911 */     setMillis(getChronology().millisOfDay().set(paramLong, getMillisOfDay()));
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
/*      */   public void setDate(ReadableInstant paramReadableInstant)
/*      */   {
/*  925 */     long l = DateTimeUtils.getInstantMillis(paramReadableInstant);
/*  926 */     if ((paramReadableInstant instanceof ReadableDateTime)) {
/*  927 */       ReadableDateTime localReadableDateTime = (ReadableDateTime)paramReadableInstant;
/*  928 */       Chronology localChronology = DateTimeUtils.getChronology(localReadableDateTime.getChronology());
/*  929 */       DateTimeZone localDateTimeZone = localChronology.getZone();
/*  930 */       if (localDateTimeZone != null) {
/*  931 */         l = localDateTimeZone.getMillisKeepLocal(getZone(), l);
/*      */       }
/*      */     }
/*  934 */     setDate(l);
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
/*      */   public void setDate(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/*  950 */     Chronology localChronology = getChronology();
/*  951 */     long l = localChronology.getDateTimeMillis(paramInt1, paramInt2, paramInt3, 0);
/*  952 */     setDate(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTime(long paramLong)
/*      */   {
/*  964 */     int i = ISOChronology.getInstanceUTC().millisOfDay().get(paramLong);
/*  965 */     setMillis(getChronology().millisOfDay().set(getMillis(), i));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setTime(ReadableInstant paramReadableInstant)
/*      */   {
/*  976 */     long l = DateTimeUtils.getInstantMillis(paramReadableInstant);
/*  977 */     Chronology localChronology = DateTimeUtils.getInstantChronology(paramReadableInstant);
/*  978 */     DateTimeZone localDateTimeZone = localChronology.getZone();
/*  979 */     if (localDateTimeZone != null) {
/*  980 */       l = localDateTimeZone.getMillisKeepLocal(DateTimeZone.UTC, l);
/*      */     }
/*  982 */     setTime(l);
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
/*      */   public void setTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/* 1000 */     long l = getChronology().getDateTimeMillis(getMillis(), paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     
/* 1002 */     setMillis(l);
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
/*      */   public void setDateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
/*      */   {
/* 1025 */     long l = getChronology().getDateTimeMillis(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
/*      */     
/* 1027 */     setMillis(l);
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
/*      */   public Property property(DateTimeFieldType paramDateTimeFieldType)
/*      */   {
/* 1040 */     if (paramDateTimeFieldType == null) {
/* 1041 */       throw new IllegalArgumentException("The DateTimeFieldType must not be null");
/*      */     }
/* 1043 */     DateTimeField localDateTimeField = paramDateTimeFieldType.getField(getChronology());
/* 1044 */     if (!localDateTimeField.isSupported()) {
/* 1045 */       throw new IllegalArgumentException("Field '" + paramDateTimeFieldType + "' is not supported");
/*      */     }
/* 1047 */     return new Property(this, localDateTimeField);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property era()
/*      */   {
/* 1056 */     return new Property(this, getChronology().era());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property centuryOfEra()
/*      */   {
/* 1065 */     return new Property(this, getChronology().centuryOfEra());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property yearOfCentury()
/*      */   {
/* 1074 */     return new Property(this, getChronology().yearOfCentury());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property yearOfEra()
/*      */   {
/* 1083 */     return new Property(this, getChronology().yearOfEra());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property year()
/*      */   {
/* 1092 */     return new Property(this, getChronology().year());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property weekyear()
/*      */   {
/* 1101 */     return new Property(this, getChronology().weekyear());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property monthOfYear()
/*      */   {
/* 1110 */     return new Property(this, getChronology().monthOfYear());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property weekOfWeekyear()
/*      */   {
/* 1119 */     return new Property(this, getChronology().weekOfWeekyear());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property dayOfYear()
/*      */   {
/* 1128 */     return new Property(this, getChronology().dayOfYear());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property dayOfMonth()
/*      */   {
/* 1139 */     return new Property(this, getChronology().dayOfMonth());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property dayOfWeek()
/*      */   {
/* 1150 */     return new Property(this, getChronology().dayOfWeek());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property hourOfDay()
/*      */   {
/* 1160 */     return new Property(this, getChronology().hourOfDay());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property minuteOfDay()
/*      */   {
/* 1169 */     return new Property(this, getChronology().minuteOfDay());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property minuteOfHour()
/*      */   {
/* 1178 */     return new Property(this, getChronology().minuteOfHour());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property secondOfDay()
/*      */   {
/* 1187 */     return new Property(this, getChronology().secondOfDay());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property secondOfMinute()
/*      */   {
/* 1196 */     return new Property(this, getChronology().secondOfMinute());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property millisOfDay()
/*      */   {
/* 1205 */     return new Property(this, getChronology().millisOfDay());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property millisOfSecond()
/*      */   {
/* 1214 */     return new Property(this, getChronology().millisOfSecond());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public MutableDateTime copy()
/*      */   {
/* 1224 */     return (MutableDateTime)clone();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Object clone()
/*      */   {
/*      */     try
/*      */     {
/* 1235 */       return super.clone();
/*      */     } catch (CloneNotSupportedException localCloneNotSupportedException) {
/* 1237 */       throw new InternalError("Clone error");
/*      */     }
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
/*      */   public static final class Property
/*      */     extends AbstractReadableInstantFieldProperty
/*      */   {
/*      */     private static final long serialVersionUID = -4481126543819298617L;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private MutableDateTime iInstant;
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
/*      */     Property(MutableDateTime paramMutableDateTime, DateTimeField paramDateTimeField)
/*      */     {
/* 1279 */       this.iInstant = paramMutableDateTime;
/* 1280 */       this.iField = paramDateTimeField;
/*      */     }
/*      */     
/*      */ 
/*      */     private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*      */       throws IOException
/*      */     {
/* 1287 */       paramObjectOutputStream.writeObject(this.iInstant);
/* 1288 */       paramObjectOutputStream.writeObject(this.iField.getType());
/*      */     }
/*      */     
/*      */ 
/*      */     private void readObject(ObjectInputStream paramObjectInputStream)
/*      */       throws IOException, ClassNotFoundException
/*      */     {
/* 1295 */       this.iInstant = ((MutableDateTime)paramObjectInputStream.readObject());
/* 1296 */       DateTimeFieldType localDateTimeFieldType = (DateTimeFieldType)paramObjectInputStream.readObject();
/* 1297 */       this.iField = localDateTimeFieldType.getField(this.iInstant.getChronology());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public DateTimeField getField()
/*      */     {
/* 1307 */       return this.iField;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected long getMillis()
/*      */     {
/* 1316 */       return this.iInstant.getMillis();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected Chronology getChronology()
/*      */     {
/* 1326 */       return this.iInstant.getChronology();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public MutableDateTime getMutableDateTime()
/*      */     {
/* 1335 */       return this.iInstant;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public MutableDateTime add(int paramInt)
/*      */     {
/* 1347 */       this.iInstant.setMillis(getField().add(this.iInstant.getMillis(), paramInt));
/* 1348 */       return this.iInstant;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public MutableDateTime add(long paramLong)
/*      */     {
/* 1359 */       this.iInstant.setMillis(getField().add(this.iInstant.getMillis(), paramLong));
/* 1360 */       return this.iInstant;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public MutableDateTime addWrapField(int paramInt)
/*      */     {
/* 1371 */       this.iInstant.setMillis(getField().addWrapField(this.iInstant.getMillis(), paramInt));
/* 1372 */       return this.iInstant;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public MutableDateTime set(int paramInt)
/*      */     {
/* 1384 */       this.iInstant.setMillis(getField().set(this.iInstant.getMillis(), paramInt));
/* 1385 */       return this.iInstant;
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
/*      */     public MutableDateTime set(String paramString, Locale paramLocale)
/*      */     {
/* 1398 */       this.iInstant.setMillis(getField().set(this.iInstant.getMillis(), paramString, paramLocale));
/* 1399 */       return this.iInstant;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public MutableDateTime set(String paramString)
/*      */     {
/* 1411 */       set(paramString, null);
/* 1412 */       return this.iInstant;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public MutableDateTime roundFloor()
/*      */     {
/* 1423 */       this.iInstant.setMillis(getField().roundFloor(this.iInstant.getMillis()));
/* 1424 */       return this.iInstant;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public MutableDateTime roundCeiling()
/*      */     {
/* 1434 */       this.iInstant.setMillis(getField().roundCeiling(this.iInstant.getMillis()));
/* 1435 */       return this.iInstant;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public MutableDateTime roundHalfFloor()
/*      */     {
/* 1446 */       this.iInstant.setMillis(getField().roundHalfFloor(this.iInstant.getMillis()));
/* 1447 */       return this.iInstant;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public MutableDateTime roundHalfCeiling()
/*      */     {
/* 1458 */       this.iInstant.setMillis(getField().roundHalfCeiling(this.iInstant.getMillis()));
/* 1459 */       return this.iInstant;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public MutableDateTime roundHalfEven()
/*      */     {
/* 1470 */       this.iInstant.setMillis(getField().roundHalfEven(this.iInstant.getMillis()));
/* 1471 */       return this.iInstant;
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\MutableDateTime.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */