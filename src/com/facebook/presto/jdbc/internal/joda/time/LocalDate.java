/*      */ package com.facebook.presto.jdbc.internal.joda.time;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.joda.time.base.BaseLocal;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.chrono.ISOChronology;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.convert.ConverterManager;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.convert.PartialConverter;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.field.AbstractReadableInstantFieldProperty;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.field.FieldUtils;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormat;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.format.ISODateTimeFormat;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.HashSet;
/*      */ import java.util.Locale;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class LocalDate
/*      */   extends BaseLocal
/*      */   implements ReadablePartial, Serializable
/*      */ {
/*      */   private static final long serialVersionUID = -8775358157899L;
/*      */   private static final int YEAR = 0;
/*      */   private static final int MONTH_OF_YEAR = 1;
/*      */   private static final int DAY_OF_MONTH = 2;
/*   96 */   private static final Set<DurationFieldType> DATE_DURATION_TYPES = new HashSet();
/*      */   
/*   98 */   static { DATE_DURATION_TYPES.add(DurationFieldType.days());
/*   99 */     DATE_DURATION_TYPES.add(DurationFieldType.weeks());
/*  100 */     DATE_DURATION_TYPES.add(DurationFieldType.months());
/*  101 */     DATE_DURATION_TYPES.add(DurationFieldType.weekyears());
/*  102 */     DATE_DURATION_TYPES.add(DurationFieldType.years());
/*  103 */     DATE_DURATION_TYPES.add(DurationFieldType.centuries());
/*      */     
/*  105 */     DATE_DURATION_TYPES.add(DurationFieldType.eras());
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
/*      */   public static LocalDate now()
/*      */   {
/*  124 */     return new LocalDate();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static LocalDate now(DateTimeZone paramDateTimeZone)
/*      */   {
/*  136 */     if (paramDateTimeZone == null) {
/*  137 */       throw new NullPointerException("Zone must not be null");
/*      */     }
/*  139 */     return new LocalDate(paramDateTimeZone);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static LocalDate now(Chronology paramChronology)
/*      */   {
/*  151 */     if (paramChronology == null) {
/*  152 */       throw new NullPointerException("Chronology must not be null");
/*      */     }
/*  154 */     return new LocalDate(paramChronology);
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
/*      */   public static LocalDate parse(String paramString)
/*      */   {
/*  168 */     return parse(paramString, ISODateTimeFormat.localDateParser());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static LocalDate parse(String paramString, DateTimeFormatter paramDateTimeFormatter)
/*      */   {
/*  179 */     return paramDateTimeFormatter.parseLocalDate(paramString);
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
/*      */   public static LocalDate fromCalendarFields(Calendar paramCalendar)
/*      */   {
/*  207 */     if (paramCalendar == null) {
/*  208 */       throw new IllegalArgumentException("The calendar must not be null");
/*      */     }
/*  210 */     int i = paramCalendar.get(0);
/*  211 */     int j = paramCalendar.get(1);
/*  212 */     return new LocalDate(i == 1 ? j : 1 - j, paramCalendar.get(2) + 1, paramCalendar.get(5));
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
/*      */   private final Chronology iChronology;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private transient int iHash;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static LocalDate fromDateFields(Date paramDate)
/*      */   {
/*  241 */     if (paramDate == null) {
/*  242 */       throw new IllegalArgumentException("The date must not be null");
/*      */     }
/*  244 */     if (paramDate.getTime() < 0L)
/*      */     {
/*  246 */       GregorianCalendar localGregorianCalendar = new GregorianCalendar();
/*  247 */       localGregorianCalendar.setTime(paramDate);
/*  248 */       return fromCalendarFields(localGregorianCalendar);
/*      */     }
/*  250 */     return new LocalDate(paramDate.getYear() + 1900, paramDate.getMonth() + 1, paramDate.getDate());
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
/*      */   public LocalDate()
/*      */   {
/*  267 */     this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance());
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
/*      */   public LocalDate(DateTimeZone paramDateTimeZone)
/*      */   {
/*  281 */     this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance(paramDateTimeZone));
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
/*      */   public LocalDate(Chronology paramChronology)
/*      */   {
/*  295 */     this(DateTimeUtils.currentTimeMillis(), paramChronology);
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
/*      */   public LocalDate(long paramLong)
/*      */   {
/*  308 */     this(paramLong, ISOChronology.getInstance());
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
/*      */   public LocalDate(long paramLong, DateTimeZone paramDateTimeZone)
/*      */   {
/*  322 */     this(paramLong, ISOChronology.getInstance(paramDateTimeZone));
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
/*      */   public LocalDate(long paramLong, Chronology paramChronology)
/*      */   {
/*  336 */     paramChronology = DateTimeUtils.getChronology(paramChronology);
/*      */     
/*  338 */     long l = paramChronology.getZone().getMillisKeepLocal(DateTimeZone.UTC, paramLong);
/*  339 */     paramChronology = paramChronology.withUTC();
/*  340 */     this.iLocalMillis = paramChronology.dayOfMonth().roundFloor(l);
/*  341 */     this.iChronology = paramChronology;
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
/*      */   public LocalDate(Object paramObject)
/*      */   {
/*  363 */     this(paramObject, (Chronology)null);
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
/*      */   public LocalDate(Object paramObject, DateTimeZone paramDateTimeZone)
/*      */   {
/*  385 */     PartialConverter localPartialConverter = ConverterManager.getInstance().getPartialConverter(paramObject);
/*  386 */     Chronology localChronology = localPartialConverter.getChronology(paramObject, paramDateTimeZone);
/*  387 */     localChronology = DateTimeUtils.getChronology(localChronology);
/*  388 */     this.iChronology = localChronology.withUTC();
/*  389 */     int[] arrayOfInt = localPartialConverter.getPartialValues(this, paramObject, localChronology, ISODateTimeFormat.localDateParser());
/*  390 */     this.iLocalMillis = this.iChronology.getDateTimeMillis(arrayOfInt[0], arrayOfInt[1], arrayOfInt[2], 0);
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
/*      */   public LocalDate(Object paramObject, Chronology paramChronology)
/*      */   {
/*  415 */     PartialConverter localPartialConverter = ConverterManager.getInstance().getPartialConverter(paramObject);
/*  416 */     paramChronology = localPartialConverter.getChronology(paramObject, paramChronology);
/*  417 */     paramChronology = DateTimeUtils.getChronology(paramChronology);
/*  418 */     this.iChronology = paramChronology.withUTC();
/*  419 */     int[] arrayOfInt = localPartialConverter.getPartialValues(this, paramObject, paramChronology, ISODateTimeFormat.localDateParser());
/*  420 */     this.iLocalMillis = this.iChronology.getDateTimeMillis(arrayOfInt[0], arrayOfInt[1], arrayOfInt[2], 0);
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
/*      */   public LocalDate(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/*  436 */     this(paramInt1, paramInt2, paramInt3, ISOChronology.getInstanceUTC());
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
/*      */   public LocalDate(int paramInt1, int paramInt2, int paramInt3, Chronology paramChronology)
/*      */   {
/*  456 */     paramChronology = DateTimeUtils.getChronology(paramChronology).withUTC();
/*  457 */     long l = paramChronology.getDateTimeMillis(paramInt1, paramInt2, paramInt3, 0);
/*  458 */     this.iChronology = paramChronology;
/*  459 */     this.iLocalMillis = l;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private Object readResolve()
/*      */   {
/*  467 */     if (this.iChronology == null) {
/*  468 */       return new LocalDate(this.iLocalMillis, ISOChronology.getInstanceUTC());
/*      */     }
/*  470 */     if (!DateTimeZone.UTC.equals(this.iChronology.getZone())) {
/*  471 */       return new LocalDate(this.iLocalMillis, this.iChronology.withUTC());
/*      */     }
/*  473 */     return this;
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
/*      */   public int size()
/*      */   {
/*  486 */     return 3;
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
/*  499 */     switch (paramInt) {
/*      */     case 0: 
/*  501 */       return paramChronology.year();
/*      */     case 1: 
/*  503 */       return paramChronology.monthOfYear();
/*      */     case 2: 
/*  505 */       return paramChronology.dayOfMonth();
/*      */     }
/*  507 */     throw new IndexOutOfBoundsException("Invalid index: " + paramInt);
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
/*      */   public int getValue(int paramInt)
/*      */   {
/*  524 */     switch (paramInt) {
/*      */     case 0: 
/*  526 */       return getChronology().year().get(getLocalMillis());
/*      */     case 1: 
/*  528 */       return getChronology().monthOfYear().get(getLocalMillis());
/*      */     case 2: 
/*  530 */       return getChronology().dayOfMonth().get(getLocalMillis());
/*      */     }
/*  532 */     throw new IndexOutOfBoundsException("Invalid index: " + paramInt);
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
/*  552 */     if (paramDateTimeFieldType == null) {
/*  553 */       throw new IllegalArgumentException("The DateTimeFieldType must not be null");
/*      */     }
/*  555 */     if (!isSupported(paramDateTimeFieldType)) {
/*  556 */       throw new IllegalArgumentException("Field '" + paramDateTimeFieldType + "' is not supported");
/*      */     }
/*  558 */     return paramDateTimeFieldType.getField(getChronology()).get(getLocalMillis());
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
/*  570 */     if (paramDateTimeFieldType == null) {
/*  571 */       return false;
/*      */     }
/*  573 */     DurationFieldType localDurationFieldType = paramDateTimeFieldType.getDurationType();
/*  574 */     if ((DATE_DURATION_TYPES.contains(localDurationFieldType)) || (localDurationFieldType.getField(getChronology()).getUnitMillis() >= getChronology().days().getUnitMillis()))
/*      */     {
/*      */ 
/*  577 */       return paramDateTimeFieldType.getField(getChronology()).isSupported();
/*      */     }
/*  579 */     return false;
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
/*  590 */     if (paramDurationFieldType == null) {
/*  591 */       return false;
/*      */     }
/*  593 */     DurationField localDurationField = paramDurationFieldType.getField(getChronology());
/*  594 */     if ((DATE_DURATION_TYPES.contains(paramDurationFieldType)) || (localDurationField.getUnitMillis() >= getChronology().days().getUnitMillis()))
/*      */     {
/*  596 */       return localDurationField.isSupported();
/*      */     }
/*  598 */     return false;
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
/*  610 */     return this.iLocalMillis;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Chronology getChronology()
/*      */   {
/*  619 */     return this.iChronology;
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
/*  632 */     if (this == paramObject) {
/*  633 */       return true;
/*      */     }
/*  635 */     if ((paramObject instanceof LocalDate)) {
/*  636 */       LocalDate localLocalDate = (LocalDate)paramObject;
/*  637 */       if (this.iChronology.equals(localLocalDate.iChronology)) {
/*  638 */         return this.iLocalMillis == localLocalDate.iLocalMillis;
/*      */       }
/*      */     }
/*  641 */     return super.equals(paramObject);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int hashCode()
/*      */   {
/*  651 */     int i = this.iHash;
/*  652 */     if (i == 0) {
/*  653 */       i = this.iHash = super.hashCode();
/*      */     }
/*  655 */     return i;
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
/*  676 */     if (this == paramReadablePartial) {
/*  677 */       return 0;
/*      */     }
/*  679 */     if ((paramReadablePartial instanceof LocalDate)) {
/*  680 */       LocalDate localLocalDate = (LocalDate)paramReadablePartial;
/*  681 */       if (this.iChronology.equals(localLocalDate.iChronology)) {
/*  682 */         return this.iLocalMillis == localLocalDate.iLocalMillis ? 0 : this.iLocalMillis < localLocalDate.iLocalMillis ? -1 : 1;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  687 */     return super.compareTo(paramReadablePartial);
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
/*      */   public DateTime toDateTimeAtStartOfDay()
/*      */   {
/*  706 */     return toDateTimeAtStartOfDay(null);
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
/*      */   public DateTime toDateTimeAtStartOfDay(DateTimeZone paramDateTimeZone)
/*      */   {
/*  728 */     paramDateTimeZone = DateTimeUtils.getZone(paramDateTimeZone);
/*  729 */     Chronology localChronology = getChronology().withZone(paramDateTimeZone);
/*  730 */     long l1 = getLocalMillis() + 21600000L;
/*  731 */     long l2 = paramDateTimeZone.convertLocalToUTC(l1, false);
/*  732 */     l2 = localChronology.dayOfMonth().roundFloor(l2);
/*  733 */     return new DateTime(l2, localChronology);
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
/*      */   @Deprecated
/*      */   public DateTime toDateTimeAtMidnight()
/*      */   {
/*  753 */     return toDateTimeAtMidnight(null);
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
/*      */   @Deprecated
/*      */   public DateTime toDateTimeAtMidnight(DateTimeZone paramDateTimeZone)
/*      */   {
/*  776 */     paramDateTimeZone = DateTimeUtils.getZone(paramDateTimeZone);
/*  777 */     Chronology localChronology = getChronology().withZone(paramDateTimeZone);
/*  778 */     return new DateTime(getYear(), getMonthOfYear(), getDayOfMonth(), 0, 0, 0, 0, localChronology);
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
/*      */   public DateTime toDateTimeAtCurrentTime()
/*      */   {
/*  795 */     return toDateTimeAtCurrentTime(null);
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
/*      */   public DateTime toDateTimeAtCurrentTime(DateTimeZone paramDateTimeZone)
/*      */   {
/*  815 */     paramDateTimeZone = DateTimeUtils.getZone(paramDateTimeZone);
/*  816 */     Chronology localChronology = getChronology().withZone(paramDateTimeZone);
/*  817 */     long l1 = DateTimeUtils.currentTimeMillis();
/*  818 */     long l2 = localChronology.set(this, l1);
/*  819 */     return new DateTime(l2, localChronology);
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
/*      */   @Deprecated
/*      */   public DateMidnight toDateMidnight()
/*      */   {
/*  842 */     return toDateMidnight(null);
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
/*      */   @Deprecated
/*      */   public DateMidnight toDateMidnight(DateTimeZone paramDateTimeZone)
/*      */   {
/*  865 */     paramDateTimeZone = DateTimeUtils.getZone(paramDateTimeZone);
/*  866 */     Chronology localChronology = getChronology().withZone(paramDateTimeZone);
/*  867 */     return new DateMidnight(getYear(), getMonthOfYear(), getDayOfMonth(), localChronology);
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
/*      */   public LocalDateTime toLocalDateTime(LocalTime paramLocalTime)
/*      */   {
/*  888 */     if (paramLocalTime == null) {
/*  889 */       throw new IllegalArgumentException("The time must not be null");
/*      */     }
/*  891 */     if (getChronology() != paramLocalTime.getChronology()) {
/*  892 */       throw new IllegalArgumentException("The chronology of the time does not match");
/*      */     }
/*  894 */     long l = getLocalMillis() + paramLocalTime.getLocalMillis();
/*  895 */     return new LocalDateTime(l, getChronology());
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
/*      */   public DateTime toDateTime(LocalTime paramLocalTime)
/*      */   {
/*  923 */     return toDateTime(paramLocalTime, null);
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
/*      */   public DateTime toDateTime(LocalTime paramLocalTime, DateTimeZone paramDateTimeZone)
/*      */   {
/*  951 */     if (paramLocalTime == null) {
/*  952 */       return toDateTimeAtCurrentTime(paramDateTimeZone);
/*      */     }
/*  954 */     if (getChronology() != paramLocalTime.getChronology()) {
/*  955 */       throw new IllegalArgumentException("The chronology of the time does not match");
/*      */     }
/*  957 */     Chronology localChronology = getChronology().withZone(paramDateTimeZone);
/*  958 */     return new DateTime(getYear(), getMonthOfYear(), getDayOfMonth(), paramLocalTime.getHourOfDay(), paramLocalTime.getMinuteOfHour(), paramLocalTime.getSecondOfMinute(), paramLocalTime.getMillisOfSecond(), localChronology);
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
/*      */   public Interval toInterval()
/*      */   {
/*  977 */     return toInterval(null);
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
/*      */   public Interval toInterval(DateTimeZone paramDateTimeZone)
/*      */   {
/*  992 */     paramDateTimeZone = DateTimeUtils.getZone(paramDateTimeZone);
/*  993 */     DateTime localDateTime1 = toDateTimeAtStartOfDay(paramDateTimeZone);
/*  994 */     DateTime localDateTime2 = plusDays(1).toDateTimeAtStartOfDay(paramDateTimeZone);
/*  995 */     return new Interval(localDateTime1, localDateTime2);
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
/*      */   public Date toDate()
/*      */   {
/* 1016 */     int i = getDayOfMonth();
/* 1017 */     Object localObject = new Date(getYear() - 1900, getMonthOfYear() - 1, i);
/* 1018 */     LocalDate localLocalDate = fromDateFields((Date)localObject);
/* 1019 */     if (localLocalDate.isBefore(this))
/*      */     {
/*      */ 
/* 1022 */       while (!localLocalDate.equals(this)) {
/* 1023 */         ((Date)localObject).setTime(((Date)localObject).getTime() + 3600000L);
/* 1024 */         localLocalDate = fromDateFields((Date)localObject);
/*      */       }
/*      */       
/* 1027 */       while (((Date)localObject).getDate() == i) {
/* 1028 */         ((Date)localObject).setTime(((Date)localObject).getTime() - 1000L);
/*      */       }
/*      */       
/* 1031 */       ((Date)localObject).setTime(((Date)localObject).getTime() + 1000L);
/* 1032 */     } else if (localLocalDate.equals(this))
/*      */     {
/* 1034 */       Date localDate = new Date(((Date)localObject).getTime() - TimeZone.getDefault().getDSTSavings());
/* 1035 */       if (localDate.getDate() == i) {
/* 1036 */         localObject = localDate;
/*      */       }
/*      */     }
/* 1039 */     return (Date)localObject;
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
/*      */   LocalDate withLocalMillis(long paramLong)
/*      */   {
/* 1054 */     paramLong = this.iChronology.dayOfMonth().roundFloor(paramLong);
/* 1055 */     return paramLong == getLocalMillis() ? this : new LocalDate(paramLong, getChronology());
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
/*      */   public LocalDate withFields(ReadablePartial paramReadablePartial)
/*      */   {
/* 1073 */     if (paramReadablePartial == null) {
/* 1074 */       return this;
/*      */     }
/* 1076 */     return withLocalMillis(getChronology().set(paramReadablePartial, getLocalMillis()));
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
/*      */   public LocalDate withField(DateTimeFieldType paramDateTimeFieldType, int paramInt)
/*      */   {
/* 1098 */     if (paramDateTimeFieldType == null) {
/* 1099 */       throw new IllegalArgumentException("Field must not be null");
/*      */     }
/* 1101 */     if (!isSupported(paramDateTimeFieldType)) {
/* 1102 */       throw new IllegalArgumentException("Field '" + paramDateTimeFieldType + "' is not supported");
/*      */     }
/* 1104 */     long l = paramDateTimeFieldType.getField(getChronology()).set(getLocalMillis(), paramInt);
/* 1105 */     return withLocalMillis(l);
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
/*      */   public LocalDate withFieldAdded(DurationFieldType paramDurationFieldType, int paramInt)
/*      */   {
/* 1127 */     if (paramDurationFieldType == null) {
/* 1128 */       throw new IllegalArgumentException("Field must not be null");
/*      */     }
/* 1130 */     if (!isSupported(paramDurationFieldType)) {
/* 1131 */       throw new IllegalArgumentException("Field '" + paramDurationFieldType + "' is not supported");
/*      */     }
/* 1133 */     if (paramInt == 0) {
/* 1134 */       return this;
/*      */     }
/* 1136 */     long l = paramDurationFieldType.getField(getChronology()).add(getLocalMillis(), paramInt);
/* 1137 */     return withLocalMillis(l);
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
/*      */   public LocalDate withPeriodAdded(ReadablePeriod paramReadablePeriod, int paramInt)
/*      */   {
/* 1160 */     if ((paramReadablePeriod == null) || (paramInt == 0)) {
/* 1161 */       return this;
/*      */     }
/* 1163 */     long l1 = getLocalMillis();
/* 1164 */     Chronology localChronology = getChronology();
/* 1165 */     for (int i = 0; i < paramReadablePeriod.size(); i++) {
/* 1166 */       long l2 = FieldUtils.safeMultiply(paramReadablePeriod.getValue(i), paramInt);
/* 1167 */       DurationFieldType localDurationFieldType = paramReadablePeriod.getFieldType(i);
/* 1168 */       if (isSupported(localDurationFieldType)) {
/* 1169 */         l1 = localDurationFieldType.getField(localChronology).add(l1, l2);
/*      */       }
/*      */     }
/* 1172 */     return withLocalMillis(l1);
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
/*      */   public LocalDate plus(ReadablePeriod paramReadablePeriod)
/*      */   {
/* 1193 */     return withPeriodAdded(paramReadablePeriod, 1);
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
/*      */   public LocalDate plusYears(int paramInt)
/*      */   {
/* 1215 */     if (paramInt == 0) {
/* 1216 */       return this;
/*      */     }
/* 1218 */     long l = getChronology().years().add(getLocalMillis(), paramInt);
/* 1219 */     return withLocalMillis(l);
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
/*      */   public LocalDate plusMonths(int paramInt)
/*      */   {
/* 1241 */     if (paramInt == 0) {
/* 1242 */       return this;
/*      */     }
/* 1244 */     long l = getChronology().months().add(getLocalMillis(), paramInt);
/* 1245 */     return withLocalMillis(l);
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
/*      */   public LocalDate plusWeeks(int paramInt)
/*      */   {
/* 1264 */     if (paramInt == 0) {
/* 1265 */       return this;
/*      */     }
/* 1267 */     long l = getChronology().weeks().add(getLocalMillis(), paramInt);
/* 1268 */     return withLocalMillis(l);
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
/*      */   public LocalDate plusDays(int paramInt)
/*      */   {
/* 1287 */     if (paramInt == 0) {
/* 1288 */       return this;
/*      */     }
/* 1290 */     long l = getChronology().days().add(getLocalMillis(), paramInt);
/* 1291 */     return withLocalMillis(l);
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
/*      */   public LocalDate minus(ReadablePeriod paramReadablePeriod)
/*      */   {
/* 1312 */     return withPeriodAdded(paramReadablePeriod, -1);
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
/*      */   public LocalDate minusYears(int paramInt)
/*      */   {
/* 1334 */     if (paramInt == 0) {
/* 1335 */       return this;
/*      */     }
/* 1337 */     long l = getChronology().years().subtract(getLocalMillis(), paramInt);
/* 1338 */     return withLocalMillis(l);
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
/*      */   public LocalDate minusMonths(int paramInt)
/*      */   {
/* 1360 */     if (paramInt == 0) {
/* 1361 */       return this;
/*      */     }
/* 1363 */     long l = getChronology().months().subtract(getLocalMillis(), paramInt);
/* 1364 */     return withLocalMillis(l);
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
/*      */   public LocalDate minusWeeks(int paramInt)
/*      */   {
/* 1383 */     if (paramInt == 0) {
/* 1384 */       return this;
/*      */     }
/* 1386 */     long l = getChronology().weeks().subtract(getLocalMillis(), paramInt);
/* 1387 */     return withLocalMillis(l);
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
/*      */   public LocalDate minusDays(int paramInt)
/*      */   {
/* 1406 */     if (paramInt == 0) {
/* 1407 */       return this;
/*      */     }
/* 1409 */     long l = getChronology().days().subtract(getLocalMillis(), paramInt);
/* 1410 */     return withLocalMillis(l);
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
/* 1423 */     if (paramDateTimeFieldType == null) {
/* 1424 */       throw new IllegalArgumentException("The DateTimeFieldType must not be null");
/*      */     }
/* 1426 */     if (!isSupported(paramDateTimeFieldType)) {
/* 1427 */       throw new IllegalArgumentException("Field '" + paramDateTimeFieldType + "' is not supported");
/*      */     }
/* 1429 */     return new Property(this, paramDateTimeFieldType.getField(getChronology()));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getEra()
/*      */   {
/* 1439 */     return getChronology().era().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getCenturyOfEra()
/*      */   {
/* 1448 */     return getChronology().centuryOfEra().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getYearOfEra()
/*      */   {
/* 1457 */     return getChronology().yearOfEra().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getYearOfCentury()
/*      */   {
/* 1466 */     return getChronology().yearOfCentury().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getYear()
/*      */   {
/* 1475 */     return getChronology().year().get(getLocalMillis());
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
/*      */   public int getWeekyear()
/*      */   {
/* 1490 */     return getChronology().weekyear().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMonthOfYear()
/*      */   {
/* 1499 */     return getChronology().monthOfYear().get(getLocalMillis());
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
/*      */   public int getWeekOfWeekyear()
/*      */   {
/* 1513 */     return getChronology().weekOfWeekyear().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getDayOfYear()
/*      */   {
/* 1522 */     return getChronology().dayOfYear().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getDayOfMonth()
/*      */   {
/* 1533 */     return getChronology().dayOfMonth().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getDayOfWeek()
/*      */   {
/* 1544 */     return getChronology().dayOfWeek().get(getLocalMillis());
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
/*      */   public LocalDate withEra(int paramInt)
/*      */   {
/* 1560 */     return withLocalMillis(getChronology().era().set(getLocalMillis(), paramInt));
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
/*      */   public LocalDate withCenturyOfEra(int paramInt)
/*      */   {
/* 1575 */     return withLocalMillis(getChronology().centuryOfEra().set(getLocalMillis(), paramInt));
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
/*      */   public LocalDate withYearOfEra(int paramInt)
/*      */   {
/* 1590 */     return withLocalMillis(getChronology().yearOfEra().set(getLocalMillis(), paramInt));
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
/*      */   public LocalDate withYearOfCentury(int paramInt)
/*      */   {
/* 1605 */     return withLocalMillis(getChronology().yearOfCentury().set(getLocalMillis(), paramInt));
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
/*      */   public LocalDate withYear(int paramInt)
/*      */   {
/* 1620 */     return withLocalMillis(getChronology().year().set(getLocalMillis(), paramInt));
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
/*      */   public LocalDate withWeekyear(int paramInt)
/*      */   {
/* 1641 */     return withLocalMillis(getChronology().weekyear().set(getLocalMillis(), paramInt));
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
/*      */   public LocalDate withMonthOfYear(int paramInt)
/*      */   {
/* 1656 */     return withLocalMillis(getChronology().monthOfYear().set(getLocalMillis(), paramInt));
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
/*      */   public LocalDate withWeekOfWeekyear(int paramInt)
/*      */   {
/* 1676 */     return withLocalMillis(getChronology().weekOfWeekyear().set(getLocalMillis(), paramInt));
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
/*      */   public LocalDate withDayOfYear(int paramInt)
/*      */   {
/* 1691 */     return withLocalMillis(getChronology().dayOfYear().set(getLocalMillis(), paramInt));
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
/*      */   public LocalDate withDayOfMonth(int paramInt)
/*      */   {
/* 1706 */     return withLocalMillis(getChronology().dayOfMonth().set(getLocalMillis(), paramInt));
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
/*      */   public LocalDate withDayOfWeek(int paramInt)
/*      */   {
/* 1721 */     return withLocalMillis(getChronology().dayOfWeek().set(getLocalMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property era()
/*      */   {
/* 1731 */     return new Property(this, getChronology().era());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property centuryOfEra()
/*      */   {
/* 1740 */     return new Property(this, getChronology().centuryOfEra());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property yearOfCentury()
/*      */   {
/* 1749 */     return new Property(this, getChronology().yearOfCentury());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property yearOfEra()
/*      */   {
/* 1758 */     return new Property(this, getChronology().yearOfEra());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property year()
/*      */   {
/* 1767 */     return new Property(this, getChronology().year());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property weekyear()
/*      */   {
/* 1776 */     return new Property(this, getChronology().weekyear());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property monthOfYear()
/*      */   {
/* 1785 */     return new Property(this, getChronology().monthOfYear());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property weekOfWeekyear()
/*      */   {
/* 1794 */     return new Property(this, getChronology().weekOfWeekyear());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property dayOfYear()
/*      */   {
/* 1803 */     return new Property(this, getChronology().dayOfYear());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property dayOfMonth()
/*      */   {
/* 1812 */     return new Property(this, getChronology().dayOfMonth());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property dayOfWeek()
/*      */   {
/* 1821 */     return new Property(this, getChronology().dayOfWeek());
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
/* 1832 */     return ISODateTimeFormat.date().print(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString(String paramString)
/*      */   {
/* 1842 */     if (paramString == null) {
/* 1843 */       return toString();
/*      */     }
/* 1845 */     return DateTimeFormat.forPattern(paramString).print(this);
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
/* 1856 */     if (paramString == null) {
/* 1857 */       return toString();
/*      */     }
/* 1859 */     return DateTimeFormat.forPattern(paramString).withLocale(paramLocale).print(this);
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
/*      */     private static final long serialVersionUID = -3193829732634L;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private transient LocalDate iInstant;
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
/*      */     Property(LocalDate paramLocalDate, DateTimeField paramDateTimeField)
/*      */     {
/* 1908 */       this.iInstant = paramLocalDate;
/* 1909 */       this.iField = paramDateTimeField;
/*      */     }
/*      */     
/*      */ 
/*      */     private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*      */       throws IOException
/*      */     {
/* 1916 */       paramObjectOutputStream.writeObject(this.iInstant);
/* 1917 */       paramObjectOutputStream.writeObject(this.iField.getType());
/*      */     }
/*      */     
/*      */ 
/*      */     private void readObject(ObjectInputStream paramObjectInputStream)
/*      */       throws IOException, ClassNotFoundException
/*      */     {
/* 1924 */       this.iInstant = ((LocalDate)paramObjectInputStream.readObject());
/* 1925 */       DateTimeFieldType localDateTimeFieldType = (DateTimeFieldType)paramObjectInputStream.readObject();
/* 1926 */       this.iField = localDateTimeFieldType.getField(this.iInstant.getChronology());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public DateTimeField getField()
/*      */     {
/* 1936 */       return this.iField;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected long getMillis()
/*      */     {
/* 1945 */       return this.iInstant.getLocalMillis();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected Chronology getChronology()
/*      */     {
/* 1955 */       return this.iInstant.getChronology();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public LocalDate getLocalDate()
/*      */     {
/* 1964 */       return this.iInstant;
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
/*      */     public LocalDate addToCopy(int paramInt)
/*      */     {
/* 1978 */       return this.iInstant.withLocalMillis(this.iField.add(this.iInstant.getLocalMillis(), paramInt));
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
/*      */     public LocalDate addWrapFieldToCopy(int paramInt)
/*      */     {
/* 1993 */       return this.iInstant.withLocalMillis(this.iField.addWrapField(this.iInstant.getLocalMillis(), paramInt));
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
/*      */     public LocalDate setCopy(int paramInt)
/*      */     {
/* 2007 */       return this.iInstant.withLocalMillis(this.iField.set(this.iInstant.getLocalMillis(), paramInt));
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
/*      */     public LocalDate setCopy(String paramString, Locale paramLocale)
/*      */     {
/* 2021 */       return this.iInstant.withLocalMillis(this.iField.set(this.iInstant.getLocalMillis(), paramString, paramLocale));
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
/*      */     public LocalDate setCopy(String paramString)
/*      */     {
/* 2034 */       return setCopy(paramString, null);
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
/*      */     public LocalDate withMaximumValue()
/*      */     {
/* 2053 */       return setCopy(getMaximumValue());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public LocalDate withMinimumValue()
/*      */     {
/* 2065 */       return setCopy(getMinimumValue());
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
/*      */     public LocalDate roundFloorCopy()
/*      */     {
/* 2080 */       return this.iInstant.withLocalMillis(this.iField.roundFloor(this.iInstant.getLocalMillis()));
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
/*      */     public LocalDate roundCeilingCopy()
/*      */     {
/* 2094 */       return this.iInstant.withLocalMillis(this.iField.roundCeiling(this.iInstant.getLocalMillis()));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public LocalDate roundHalfFloorCopy()
/*      */     {
/* 2104 */       return this.iInstant.withLocalMillis(this.iField.roundHalfFloor(this.iInstant.getLocalMillis()));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public LocalDate roundHalfCeilingCopy()
/*      */     {
/* 2114 */       return this.iInstant.withLocalMillis(this.iField.roundHalfCeiling(this.iInstant.getLocalMillis()));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public LocalDate roundHalfEvenCopy()
/*      */     {
/* 2125 */       return this.iInstant.withLocalMillis(this.iField.roundHalfEven(this.iInstant.getLocalMillis()));
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\LocalDate.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */