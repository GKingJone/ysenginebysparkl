/*      */ package com.facebook.presto.jdbc.internal.joda.time;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.joda.time.base.BaseLocal;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.chrono.ISOChronology;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.convert.ConverterManager;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.convert.PartialConverter;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.field.AbstractReadableInstantFieldProperty;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormat;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.format.ISODateTimeFormat;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.HashSet;
/*      */ import java.util.Locale;
/*      */ import java.util.Set;
/*      */ import org.joda.convert.FromString;
/*      */ import org.joda.convert.ToString;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class LocalTime
/*      */   extends BaseLocal
/*      */   implements ReadablePartial, Serializable
/*      */ {
/*      */   private static final long serialVersionUID = -12873158713873L;
/*   84 */   public static final LocalTime MIDNIGHT = new LocalTime(0, 0, 0, 0);
/*      */   
/*      */ 
/*      */   private static final int HOUR_OF_DAY = 0;
/*      */   
/*      */   private static final int MINUTE_OF_HOUR = 1;
/*      */   
/*      */   private static final int SECOND_OF_MINUTE = 2;
/*      */   
/*      */   private static final int MILLIS_OF_SECOND = 3;
/*      */   
/*   95 */   private static final Set<DurationFieldType> TIME_DURATION_TYPES = new HashSet();
/*      */   
/*   97 */   static { TIME_DURATION_TYPES.add(DurationFieldType.millis());
/*   98 */     TIME_DURATION_TYPES.add(DurationFieldType.seconds());
/*   99 */     TIME_DURATION_TYPES.add(DurationFieldType.minutes());
/*  100 */     TIME_DURATION_TYPES.add(DurationFieldType.hours());
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
/*      */   public static LocalTime now()
/*      */   {
/*  118 */     return new LocalTime();
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
/*      */   public static LocalTime now(DateTimeZone paramDateTimeZone)
/*      */   {
/*  131 */     if (paramDateTimeZone == null) {
/*  132 */       throw new NullPointerException("Zone must not be null");
/*      */     }
/*  134 */     return new LocalTime(paramDateTimeZone);
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
/*      */   public static LocalTime now(Chronology paramChronology)
/*      */   {
/*  147 */     if (paramChronology == null) {
/*  148 */       throw new NullPointerException("Chronology must not be null");
/*      */     }
/*  150 */     return new LocalTime(paramChronology);
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
/*      */   public static LocalTime parse(String paramString)
/*      */   {
/*  164 */     return parse(paramString, ISODateTimeFormat.localTimeParser());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static LocalTime parse(String paramString, DateTimeFormatter paramDateTimeFormatter)
/*      */   {
/*  175 */     return paramDateTimeFormatter.parseLocalTime(paramString);
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
/*      */   public static LocalTime fromMillisOfDay(long paramLong)
/*      */   {
/*  190 */     return fromMillisOfDay(paramLong, null);
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
/*      */   public static LocalTime fromMillisOfDay(long paramLong, Chronology paramChronology)
/*      */   {
/*  205 */     paramChronology = DateTimeUtils.getChronology(paramChronology).withUTC();
/*  206 */     return new LocalTime(paramLong, paramChronology);
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
/*      */   private final long iLocalMillis;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static LocalTime fromCalendarFields(Calendar paramCalendar)
/*      */   {
/*  234 */     if (paramCalendar == null) {
/*  235 */       throw new IllegalArgumentException("The calendar must not be null");
/*      */     }
/*  237 */     return new LocalTime(paramCalendar.get(11), paramCalendar.get(12), paramCalendar.get(13), paramCalendar.get(14));
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
/*      */   private final Chronology iChronology;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static LocalTime fromDateFields(Date paramDate)
/*      */   {
/*  267 */     if (paramDate == null) {
/*  268 */       throw new IllegalArgumentException("The date must not be null");
/*      */     }
/*  270 */     return new LocalTime(paramDate.getHours(), paramDate.getMinutes(), paramDate.getSeconds(), ((int)(paramDate.getTime() % 1000L) + 1000) % 1000);
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
/*      */   public LocalTime()
/*      */   {
/*  288 */     this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance());
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
/*      */   public LocalTime(DateTimeZone paramDateTimeZone)
/*      */   {
/*  302 */     this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance(paramDateTimeZone));
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
/*      */   public LocalTime(Chronology paramChronology)
/*      */   {
/*  316 */     this(DateTimeUtils.currentTimeMillis(), paramChronology);
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
/*      */   public LocalTime(long paramLong)
/*      */   {
/*  329 */     this(paramLong, ISOChronology.getInstance());
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
/*      */   public LocalTime(long paramLong, DateTimeZone paramDateTimeZone)
/*      */   {
/*  343 */     this(paramLong, ISOChronology.getInstance(paramDateTimeZone));
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
/*      */   public LocalTime(long paramLong, Chronology paramChronology)
/*      */   {
/*  357 */     paramChronology = DateTimeUtils.getChronology(paramChronology);
/*      */     
/*  359 */     long l = paramChronology.getZone().getMillisKeepLocal(DateTimeZone.UTC, paramLong);
/*  360 */     paramChronology = paramChronology.withUTC();
/*  361 */     this.iLocalMillis = paramChronology.millisOfDay().get(l);
/*  362 */     this.iChronology = paramChronology;
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
/*      */   public LocalTime(Object paramObject)
/*      */   {
/*  383 */     this(paramObject, (Chronology)null);
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
/*      */   public LocalTime(Object paramObject, DateTimeZone paramDateTimeZone)
/*      */   {
/*  405 */     PartialConverter localPartialConverter = ConverterManager.getInstance().getPartialConverter(paramObject);
/*  406 */     Chronology localChronology = localPartialConverter.getChronology(paramObject, paramDateTimeZone);
/*  407 */     localChronology = DateTimeUtils.getChronology(localChronology);
/*  408 */     this.iChronology = localChronology.withUTC();
/*  409 */     int[] arrayOfInt = localPartialConverter.getPartialValues(this, paramObject, localChronology, ISODateTimeFormat.localTimeParser());
/*  410 */     this.iLocalMillis = this.iChronology.getDateTimeMillis(0L, arrayOfInt[0], arrayOfInt[1], arrayOfInt[2], arrayOfInt[3]);
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
/*      */   public LocalTime(Object paramObject, Chronology paramChronology)
/*      */   {
/*  431 */     PartialConverter localPartialConverter = ConverterManager.getInstance().getPartialConverter(paramObject);
/*  432 */     paramChronology = localPartialConverter.getChronology(paramObject, paramChronology);
/*  433 */     paramChronology = DateTimeUtils.getChronology(paramChronology);
/*  434 */     this.iChronology = paramChronology.withUTC();
/*  435 */     int[] arrayOfInt = localPartialConverter.getPartialValues(this, paramObject, paramChronology, ISODateTimeFormat.localTimeParser());
/*  436 */     this.iLocalMillis = this.iChronology.getDateTimeMillis(0L, arrayOfInt[0], arrayOfInt[1], arrayOfInt[2], arrayOfInt[3]);
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
/*      */   public LocalTime(int paramInt1, int paramInt2)
/*      */   {
/*  450 */     this(paramInt1, paramInt2, 0, 0, ISOChronology.getInstanceUTC());
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
/*      */   public LocalTime(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/*  465 */     this(paramInt1, paramInt2, paramInt3, 0, ISOChronology.getInstanceUTC());
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
/*      */   public LocalTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  482 */     this(paramInt1, paramInt2, paramInt3, paramInt4, ISOChronology.getInstanceUTC());
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
/*      */   public LocalTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Chronology paramChronology)
/*      */   {
/*  505 */     paramChronology = DateTimeUtils.getChronology(paramChronology).withUTC();
/*  506 */     long l = paramChronology.getDateTimeMillis(0L, paramInt1, paramInt2, paramInt3, paramInt4);
/*      */     
/*  508 */     this.iChronology = paramChronology;
/*  509 */     this.iLocalMillis = l;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private Object readResolve()
/*      */   {
/*  517 */     if (this.iChronology == null) {
/*  518 */       return new LocalTime(this.iLocalMillis, ISOChronology.getInstanceUTC());
/*      */     }
/*  520 */     if (!DateTimeZone.UTC.equals(this.iChronology.getZone())) {
/*  521 */       return new LocalTime(this.iLocalMillis, this.iChronology.withUTC());
/*      */     }
/*  523 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int size()
/*      */   {
/*  535 */     return 4;
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
/*      */   protected DateTimeField getField(int paramInt, Chronology paramChronology)
/*      */   {
/*  548 */     switch (paramInt) {
/*      */     case 0: 
/*  550 */       return paramChronology.hourOfDay();
/*      */     case 1: 
/*  552 */       return paramChronology.minuteOfHour();
/*      */     case 2: 
/*  554 */       return paramChronology.secondOfMinute();
/*      */     case 3: 
/*  556 */       return paramChronology.millisOfSecond();
/*      */     }
/*  558 */     throw new IndexOutOfBoundsException("Invalid index: " + paramInt);
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
/*      */   public int getValue(int paramInt)
/*      */   {
/*  574 */     switch (paramInt) {
/*      */     case 0: 
/*  576 */       return getChronology().hourOfDay().get(getLocalMillis());
/*      */     case 1: 
/*  578 */       return getChronology().minuteOfHour().get(getLocalMillis());
/*      */     case 2: 
/*  580 */       return getChronology().secondOfMinute().get(getLocalMillis());
/*      */     case 3: 
/*  582 */       return getChronology().millisOfSecond().get(getLocalMillis());
/*      */     }
/*  584 */     throw new IndexOutOfBoundsException("Invalid index: " + paramInt);
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
/*      */   public int get(DateTimeFieldType paramDateTimeFieldType)
/*      */   {
/*  604 */     if (paramDateTimeFieldType == null) {
/*  605 */       throw new IllegalArgumentException("The DateTimeFieldType must not be null");
/*      */     }
/*  607 */     if (!isSupported(paramDateTimeFieldType)) {
/*  608 */       throw new IllegalArgumentException("Field '" + paramDateTimeFieldType + "' is not supported");
/*      */     }
/*  610 */     return paramDateTimeFieldType.getField(getChronology()).get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isSupported(DateTimeFieldType paramDateTimeFieldType)
/*      */   {
/*  622 */     if (paramDateTimeFieldType == null) {
/*  623 */       return false;
/*      */     }
/*  625 */     if (!isSupported(paramDateTimeFieldType.getDurationType())) {
/*  626 */       return false;
/*      */     }
/*  628 */     DurationFieldType localDurationFieldType = paramDateTimeFieldType.getRangeDurationType();
/*  629 */     return (isSupported(localDurationFieldType)) || (localDurationFieldType == DurationFieldType.days());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public boolean isSupported(DurationFieldType paramDurationFieldType)
/*      */   {
/*  640 */     if (paramDurationFieldType == null) {
/*  641 */       return false;
/*      */     }
/*  643 */     DurationField localDurationField = paramDurationFieldType.getField(getChronology());
/*  644 */     if ((TIME_DURATION_TYPES.contains(paramDurationFieldType)) || (localDurationField.getUnitMillis() < getChronology().days().getUnitMillis()))
/*      */     {
/*  646 */       return localDurationField.isSupported();
/*      */     }
/*  648 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected long getLocalMillis()
/*      */   {
/*  660 */     return this.iLocalMillis;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Chronology getChronology()
/*      */   {
/*  669 */     return this.iChronology;
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
/*      */   public boolean equals(Object paramObject)
/*      */   {
/*  682 */     if (this == paramObject) {
/*  683 */       return true;
/*      */     }
/*  685 */     if ((paramObject instanceof LocalTime)) {
/*  686 */       LocalTime localLocalTime = (LocalTime)paramObject;
/*  687 */       if (this.iChronology.equals(localLocalTime.iChronology)) {
/*  688 */         return this.iLocalMillis == localLocalTime.iLocalMillis;
/*      */       }
/*      */     }
/*  691 */     return super.equals(paramObject);
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
/*      */   public int compareTo(ReadablePartial paramReadablePartial)
/*      */   {
/*  712 */     if (this == paramReadablePartial) {
/*  713 */       return 0;
/*      */     }
/*  715 */     if ((paramReadablePartial instanceof LocalTime)) {
/*  716 */       LocalTime localLocalTime = (LocalTime)paramReadablePartial;
/*  717 */       if (this.iChronology.equals(localLocalTime.iChronology)) {
/*  718 */         return this.iLocalMillis == localLocalTime.iLocalMillis ? 0 : this.iLocalMillis < localLocalTime.iLocalMillis ? -1 : 1;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  723 */     return super.compareTo(paramReadablePartial);
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
/*      */   LocalTime withLocalMillis(long paramLong)
/*      */   {
/*  738 */     return paramLong == getLocalMillis() ? this : new LocalTime(paramLong, getChronology());
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
/*      */   public LocalTime withFields(ReadablePartial paramReadablePartial)
/*      */   {
/*  756 */     if (paramReadablePartial == null) {
/*  757 */       return this;
/*      */     }
/*  759 */     return withLocalMillis(getChronology().set(paramReadablePartial, getLocalMillis()));
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
/*      */   public LocalTime withField(DateTimeFieldType paramDateTimeFieldType, int paramInt)
/*      */   {
/*  782 */     if (paramDateTimeFieldType == null) {
/*  783 */       throw new IllegalArgumentException("Field must not be null");
/*      */     }
/*  785 */     if (!isSupported(paramDateTimeFieldType)) {
/*  786 */       throw new IllegalArgumentException("Field '" + paramDateTimeFieldType + "' is not supported");
/*      */     }
/*  788 */     long l = paramDateTimeFieldType.getField(getChronology()).set(getLocalMillis(), paramInt);
/*  789 */     return withLocalMillis(l);
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
/*      */   public LocalTime withFieldAdded(DurationFieldType paramDurationFieldType, int paramInt)
/*      */   {
/*  815 */     if (paramDurationFieldType == null) {
/*  816 */       throw new IllegalArgumentException("Field must not be null");
/*      */     }
/*  818 */     if (!isSupported(paramDurationFieldType)) {
/*  819 */       throw new IllegalArgumentException("Field '" + paramDurationFieldType + "' is not supported");
/*      */     }
/*  821 */     if (paramInt == 0) {
/*  822 */       return this;
/*      */     }
/*  824 */     long l = paramDurationFieldType.getField(getChronology()).add(getLocalMillis(), paramInt);
/*  825 */     return withLocalMillis(l);
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
/*      */   public LocalTime withPeriodAdded(ReadablePeriod paramReadablePeriod, int paramInt)
/*      */   {
/*  845 */     if ((paramReadablePeriod == null) || (paramInt == 0)) {
/*  846 */       return this;
/*      */     }
/*  848 */     long l = getChronology().add(paramReadablePeriod, getLocalMillis(), paramInt);
/*  849 */     return withLocalMillis(l);
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
/*      */   public LocalTime plus(ReadablePeriod paramReadablePeriod)
/*      */   {
/*  867 */     return withPeriodAdded(paramReadablePeriod, 1);
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
/*      */   public LocalTime plusHours(int paramInt)
/*      */   {
/*  887 */     if (paramInt == 0) {
/*  888 */       return this;
/*      */     }
/*  890 */     long l = getChronology().hours().add(getLocalMillis(), paramInt);
/*  891 */     return withLocalMillis(l);
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
/*      */   public LocalTime plusMinutes(int paramInt)
/*      */   {
/*  910 */     if (paramInt == 0) {
/*  911 */       return this;
/*      */     }
/*  913 */     long l = getChronology().minutes().add(getLocalMillis(), paramInt);
/*  914 */     return withLocalMillis(l);
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
/*      */   public LocalTime plusSeconds(int paramInt)
/*      */   {
/*  933 */     if (paramInt == 0) {
/*  934 */       return this;
/*      */     }
/*  936 */     long l = getChronology().seconds().add(getLocalMillis(), paramInt);
/*  937 */     return withLocalMillis(l);
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
/*      */   public LocalTime plusMillis(int paramInt)
/*      */   {
/*  956 */     if (paramInt == 0) {
/*  957 */       return this;
/*      */     }
/*  959 */     long l = getChronology().millis().add(getLocalMillis(), paramInt);
/*  960 */     return withLocalMillis(l);
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
/*      */   public LocalTime minus(ReadablePeriod paramReadablePeriod)
/*      */   {
/*  978 */     return withPeriodAdded(paramReadablePeriod, -1);
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
/*      */   public LocalTime minusHours(int paramInt)
/*      */   {
/*  998 */     if (paramInt == 0) {
/*  999 */       return this;
/*      */     }
/* 1001 */     long l = getChronology().hours().subtract(getLocalMillis(), paramInt);
/* 1002 */     return withLocalMillis(l);
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
/*      */   public LocalTime minusMinutes(int paramInt)
/*      */   {
/* 1021 */     if (paramInt == 0) {
/* 1022 */       return this;
/*      */     }
/* 1024 */     long l = getChronology().minutes().subtract(getLocalMillis(), paramInt);
/* 1025 */     return withLocalMillis(l);
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
/*      */   public LocalTime minusSeconds(int paramInt)
/*      */   {
/* 1044 */     if (paramInt == 0) {
/* 1045 */       return this;
/*      */     }
/* 1047 */     long l = getChronology().seconds().subtract(getLocalMillis(), paramInt);
/* 1048 */     return withLocalMillis(l);
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
/*      */   public LocalTime minusMillis(int paramInt)
/*      */   {
/* 1067 */     if (paramInt == 0) {
/* 1068 */       return this;
/*      */     }
/* 1070 */     long l = getChronology().millis().subtract(getLocalMillis(), paramInt);
/* 1071 */     return withLocalMillis(l);
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
/* 1084 */     if (paramDateTimeFieldType == null) {
/* 1085 */       throw new IllegalArgumentException("The DateTimeFieldType must not be null");
/*      */     }
/* 1087 */     if (!isSupported(paramDateTimeFieldType)) {
/* 1088 */       throw new IllegalArgumentException("Field '" + paramDateTimeFieldType + "' is not supported");
/*      */     }
/* 1090 */     return new Property(this, paramDateTimeFieldType.getField(getChronology()));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getHourOfDay()
/*      */   {
/* 1100 */     return getChronology().hourOfDay().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMinuteOfHour()
/*      */   {
/* 1109 */     return getChronology().minuteOfHour().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getSecondOfMinute()
/*      */   {
/* 1118 */     return getChronology().secondOfMinute().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMillisOfSecond()
/*      */   {
/* 1127 */     return getChronology().millisOfSecond().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMillisOfDay()
/*      */   {
/* 1136 */     return getChronology().millisOfDay().get(getLocalMillis());
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
/*      */   public LocalTime withHourOfDay(int paramInt)
/*      */   {
/* 1152 */     return withLocalMillis(getChronology().hourOfDay().set(getLocalMillis(), paramInt));
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
/*      */   public LocalTime withMinuteOfHour(int paramInt)
/*      */   {
/* 1167 */     return withLocalMillis(getChronology().minuteOfHour().set(getLocalMillis(), paramInt));
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
/*      */   public LocalTime withSecondOfMinute(int paramInt)
/*      */   {
/* 1182 */     return withLocalMillis(getChronology().secondOfMinute().set(getLocalMillis(), paramInt));
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
/*      */   public LocalTime withMillisOfSecond(int paramInt)
/*      */   {
/* 1197 */     return withLocalMillis(getChronology().millisOfSecond().set(getLocalMillis(), paramInt));
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
/*      */   public LocalTime withMillisOfDay(int paramInt)
/*      */   {
/* 1212 */     return withLocalMillis(getChronology().millisOfDay().set(getLocalMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property hourOfDay()
/*      */   {
/* 1222 */     return new Property(this, getChronology().hourOfDay());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property minuteOfHour()
/*      */   {
/* 1231 */     return new Property(this, getChronology().minuteOfHour());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property secondOfMinute()
/*      */   {
/* 1240 */     return new Property(this, getChronology().secondOfMinute());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property millisOfSecond()
/*      */   {
/* 1249 */     return new Property(this, getChronology().millisOfSecond());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property millisOfDay()
/*      */   {
/* 1258 */     return new Property(this, getChronology().millisOfDay());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime toDateTimeToday()
/*      */   {
/* 1270 */     return toDateTimeToday(null);
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
/*      */   public DateTime toDateTimeToday(DateTimeZone paramDateTimeZone)
/*      */   {
/* 1285 */     Chronology localChronology = getChronology().withZone(paramDateTimeZone);
/* 1286 */     long l1 = DateTimeUtils.currentTimeMillis();
/* 1287 */     long l2 = localChronology.set(this, l1);
/* 1288 */     return new DateTime(l2, localChronology);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @ToString
/*      */   public String toString()
/*      */   {
/* 1299 */     return ISODateTimeFormat.time().print(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString(String paramString)
/*      */   {
/* 1309 */     if (paramString == null) {
/* 1310 */       return toString();
/*      */     }
/* 1312 */     return DateTimeFormat.forPattern(paramString).print(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString(String paramString, Locale paramLocale)
/*      */     throws IllegalArgumentException
/*      */   {
/* 1323 */     if (paramString == null) {
/* 1324 */       return toString();
/*      */     }
/* 1326 */     return DateTimeFormat.forPattern(paramString).withLocale(paramLocale).print(this);
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
/*      */   public static final class Property
/*      */     extends AbstractReadableInstantFieldProperty
/*      */   {
/*      */     private static final long serialVersionUID = -325842547277223L;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private transient LocalTime iInstant;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private transient DateTimeField iField;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     Property(LocalTime paramLocalTime, DateTimeField paramDateTimeField)
/*      */     {
/* 1374 */       this.iInstant = paramLocalTime;
/* 1375 */       this.iField = paramDateTimeField;
/*      */     }
/*      */     
/*      */ 
/*      */     private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*      */       throws IOException
/*      */     {
/* 1382 */       paramObjectOutputStream.writeObject(this.iInstant);
/* 1383 */       paramObjectOutputStream.writeObject(this.iField.getType());
/*      */     }
/*      */     
/*      */ 
/*      */     private void readObject(ObjectInputStream paramObjectInputStream)
/*      */       throws IOException, ClassNotFoundException
/*      */     {
/* 1390 */       this.iInstant = ((LocalTime)paramObjectInputStream.readObject());
/* 1391 */       DateTimeFieldType localDateTimeFieldType = (DateTimeFieldType)paramObjectInputStream.readObject();
/* 1392 */       this.iField = localDateTimeFieldType.getField(this.iInstant.getChronology());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public DateTimeField getField()
/*      */     {
/* 1402 */       return this.iField;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected long getMillis()
/*      */     {
/* 1411 */       return this.iInstant.getLocalMillis();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected Chronology getChronology()
/*      */     {
/* 1421 */       return this.iInstant.getChronology();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public LocalTime getLocalTime()
/*      */     {
/* 1430 */       return this.iInstant;
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
/*      */     public LocalTime addCopy(int paramInt)
/*      */     {
/* 1443 */       return this.iInstant.withLocalMillis(this.iField.add(this.iInstant.getLocalMillis(), paramInt));
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
/*      */     public LocalTime addCopy(long paramLong)
/*      */     {
/* 1457 */       return this.iInstant.withLocalMillis(this.iField.add(this.iInstant.getLocalMillis(), paramLong));
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
/*      */     public LocalTime addNoWrapToCopy(int paramInt)
/*      */     {
/* 1473 */       long l1 = this.iField.add(this.iInstant.getLocalMillis(), paramInt);
/* 1474 */       long l2 = this.iInstant.getChronology().millisOfDay().get(l1);
/* 1475 */       if (l2 != l1) {
/* 1476 */         throw new IllegalArgumentException("The addition exceeded the boundaries of LocalTime");
/*      */       }
/* 1478 */       return this.iInstant.withLocalMillis(l1);
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
/*      */     public LocalTime addWrapFieldToCopy(int paramInt)
/*      */     {
/* 1493 */       return this.iInstant.withLocalMillis(this.iField.addWrapField(this.iInstant.getLocalMillis(), paramInt));
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
/*      */     public LocalTime setCopy(int paramInt)
/*      */     {
/* 1507 */       return this.iInstant.withLocalMillis(this.iField.set(this.iInstant.getLocalMillis(), paramInt));
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
/*      */     public LocalTime setCopy(String paramString, Locale paramLocale)
/*      */     {
/* 1521 */       return this.iInstant.withLocalMillis(this.iField.set(this.iInstant.getLocalMillis(), paramString, paramLocale));
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
/*      */     public LocalTime setCopy(String paramString)
/*      */     {
/* 1534 */       return setCopy(paramString, null);
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
/*      */     public LocalTime withMaximumValue()
/*      */     {
/* 1547 */       return setCopy(getMaximumValue());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public LocalTime withMinimumValue()
/*      */     {
/* 1559 */       return setCopy(getMinimumValue());
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
/*      */     public LocalTime roundFloorCopy()
/*      */     {
/* 1574 */       return this.iInstant.withLocalMillis(this.iField.roundFloor(this.iInstant.getLocalMillis()));
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
/*      */     public LocalTime roundCeilingCopy()
/*      */     {
/* 1588 */       return this.iInstant.withLocalMillis(this.iField.roundCeiling(this.iInstant.getLocalMillis()));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public LocalTime roundHalfFloorCopy()
/*      */     {
/* 1598 */       return this.iInstant.withLocalMillis(this.iField.roundHalfFloor(this.iInstant.getLocalMillis()));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public LocalTime roundHalfCeilingCopy()
/*      */     {
/* 1608 */       return this.iInstant.withLocalMillis(this.iField.roundHalfCeiling(this.iInstant.getLocalMillis()));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public LocalTime roundHalfEvenCopy()
/*      */     {
/* 1619 */       return this.iInstant.withLocalMillis(this.iField.roundHalfEven(this.iInstant.getLocalMillis()));
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\LocalTime.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */