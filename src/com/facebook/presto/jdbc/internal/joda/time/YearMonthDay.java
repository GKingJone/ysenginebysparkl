/*      */ package com.facebook.presto.jdbc.internal.joda.time;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.joda.time.base.BasePartial;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.chrono.ISOChronology;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.field.AbstractPartialFieldProperty;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.field.FieldUtils;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.format.ISODateTimeFormat;
/*      */ import java.io.Serializable;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.Locale;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ @Deprecated
/*      */ public final class YearMonthDay
/*      */   extends BasePartial
/*      */   implements ReadablePartial, Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 797544782896179L;
/*   72 */   private static final DateTimeFieldType[] FIELD_TYPES = { DateTimeFieldType.year(), DateTimeFieldType.monthOfYear(), DateTimeFieldType.dayOfMonth() };
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int YEAR = 0;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int MONTH_OF_YEAR = 1;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static final int DAY_OF_MONTH = 2;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static YearMonthDay fromCalendarFields(Calendar paramCalendar)
/*      */   {
/*  106 */     if (paramCalendar == null) {
/*  107 */       throw new IllegalArgumentException("The calendar must not be null");
/*      */     }
/*  109 */     return new YearMonthDay(paramCalendar.get(1), paramCalendar.get(2) + 1, paramCalendar.get(5));
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
/*      */   public static YearMonthDay fromDateFields(Date paramDate)
/*      */   {
/*  133 */     if (paramDate == null) {
/*  134 */       throw new IllegalArgumentException("The date must not be null");
/*      */     }
/*  136 */     return new YearMonthDay(paramDate.getYear() + 1900, paramDate.getMonth() + 1, paramDate.getDate());
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
/*      */   public YearMonthDay() {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public YearMonthDay(DateTimeZone paramDateTimeZone)
/*      */   {
/*  168 */     super(ISOChronology.getInstance(paramDateTimeZone));
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
/*      */   public YearMonthDay(Chronology paramChronology)
/*      */   {
/*  182 */     super(paramChronology);
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
/*      */   public YearMonthDay(long paramLong)
/*      */   {
/*  196 */     super(paramLong);
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
/*      */   public YearMonthDay(long paramLong, Chronology paramChronology)
/*      */   {
/*  211 */     super(paramLong, paramChronology);
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
/*      */   public YearMonthDay(Object paramObject)
/*      */   {
/*  231 */     super(paramObject, null, ISODateTimeFormat.dateOptionalTimeParser());
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
/*      */   public YearMonthDay(Object paramObject, Chronology paramChronology)
/*      */   {
/*  256 */     super(paramObject, DateTimeUtils.getChronology(paramChronology), ISODateTimeFormat.dateOptionalTimeParser());
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
/*      */   public YearMonthDay(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/*  272 */     this(paramInt1, paramInt2, paramInt3, null);
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
/*      */   public YearMonthDay(int paramInt1, int paramInt2, int paramInt3, Chronology paramChronology)
/*      */   {
/*  288 */     super(new int[] { paramInt1, paramInt2, paramInt3 }, paramChronology);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   YearMonthDay(YearMonthDay paramYearMonthDay, int[] paramArrayOfInt)
/*      */   {
/*  298 */     super(paramYearMonthDay, paramArrayOfInt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   YearMonthDay(YearMonthDay paramYearMonthDay, Chronology paramChronology)
/*      */   {
/*  308 */     super(paramYearMonthDay, paramChronology);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int size()
/*      */   {
/*  318 */     return 3;
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
/*  331 */     switch (paramInt) {
/*      */     case 0: 
/*  333 */       return paramChronology.year();
/*      */     case 1: 
/*  335 */       return paramChronology.monthOfYear();
/*      */     case 2: 
/*  337 */       return paramChronology.dayOfMonth();
/*      */     }
/*  339 */     throw new IndexOutOfBoundsException("Invalid index: " + paramInt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFieldType getFieldType(int paramInt)
/*      */   {
/*  351 */     return FIELD_TYPES[paramInt];
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTimeFieldType[] getFieldTypes()
/*      */   {
/*  362 */     return (DateTimeFieldType[])FIELD_TYPES.clone();
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
/*      */   public YearMonthDay withChronologyRetainFields(Chronology paramChronology)
/*      */   {
/*  381 */     paramChronology = DateTimeUtils.getChronology(paramChronology);
/*  382 */     paramChronology = paramChronology.withUTC();
/*  383 */     if (paramChronology == getChronology()) {
/*  384 */       return this;
/*      */     }
/*  386 */     YearMonthDay localYearMonthDay = new YearMonthDay(this, paramChronology);
/*  387 */     paramChronology.validate(localYearMonthDay, getValues());
/*  388 */     return localYearMonthDay;
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
/*      */   public YearMonthDay withField(DateTimeFieldType paramDateTimeFieldType, int paramInt)
/*      */   {
/*  411 */     int i = indexOfSupported(paramDateTimeFieldType);
/*  412 */     if (paramInt == getValue(i)) {
/*  413 */       return this;
/*      */     }
/*  415 */     int[] arrayOfInt = getValues();
/*  416 */     arrayOfInt = getField(i).set(this, i, arrayOfInt, paramInt);
/*  417 */     return new YearMonthDay(this, arrayOfInt);
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
/*      */   public YearMonthDay withFieldAdded(DurationFieldType paramDurationFieldType, int paramInt)
/*      */   {
/*  439 */     int i = indexOfSupported(paramDurationFieldType);
/*  440 */     if (paramInt == 0) {
/*  441 */       return this;
/*      */     }
/*  443 */     int[] arrayOfInt = getValues();
/*  444 */     arrayOfInt = getField(i).add(this, i, arrayOfInt, paramInt);
/*  445 */     return new YearMonthDay(this, arrayOfInt);
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
/*      */   public YearMonthDay withPeriodAdded(ReadablePeriod paramReadablePeriod, int paramInt)
/*      */   {
/*  465 */     if ((paramReadablePeriod == null) || (paramInt == 0)) {
/*  466 */       return this;
/*      */     }
/*  468 */     int[] arrayOfInt = getValues();
/*  469 */     for (int i = 0; i < paramReadablePeriod.size(); i++) {
/*  470 */       DurationFieldType localDurationFieldType = paramReadablePeriod.getFieldType(i);
/*  471 */       int j = indexOf(localDurationFieldType);
/*  472 */       if (j >= 0) {
/*  473 */         arrayOfInt = getField(j).add(this, j, arrayOfInt, FieldUtils.safeMultiply(paramReadablePeriod.getValue(i), paramInt));
/*      */       }
/*      */     }
/*      */     
/*  477 */     return new YearMonthDay(this, arrayOfInt);
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
/*      */   public YearMonthDay plus(ReadablePeriod paramReadablePeriod)
/*      */   {
/*  495 */     return withPeriodAdded(paramReadablePeriod, 1);
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
/*      */   public YearMonthDay plusYears(int paramInt)
/*      */   {
/*  516 */     return withFieldAdded(DurationFieldType.years(), paramInt);
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
/*      */   public YearMonthDay plusMonths(int paramInt)
/*      */   {
/*  536 */     return withFieldAdded(DurationFieldType.months(), paramInt);
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
/*      */   public YearMonthDay plusDays(int paramInt)
/*      */   {
/*  556 */     return withFieldAdded(DurationFieldType.days(), paramInt);
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
/*      */   public YearMonthDay minus(ReadablePeriod paramReadablePeriod)
/*      */   {
/*  574 */     return withPeriodAdded(paramReadablePeriod, -1);
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
/*      */   public YearMonthDay minusYears(int paramInt)
/*      */   {
/*  595 */     return withFieldAdded(DurationFieldType.years(), FieldUtils.safeNegate(paramInt));
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
/*      */   public YearMonthDay minusMonths(int paramInt)
/*      */   {
/*  615 */     return withFieldAdded(DurationFieldType.months(), FieldUtils.safeNegate(paramInt));
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
/*      */   public YearMonthDay minusDays(int paramInt)
/*      */   {
/*  635 */     return withFieldAdded(DurationFieldType.days(), FieldUtils.safeNegate(paramInt));
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
/*  648 */     return new Property(this, indexOfSupported(paramDateTimeFieldType));
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
/*  659 */     return new LocalDate(getYear(), getMonthOfYear(), getDayOfMonth(), getChronology());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime toDateTimeAtMidnight()
/*      */   {
/*  670 */     return toDateTimeAtMidnight(null);
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
/*      */   public DateTime toDateTimeAtMidnight(DateTimeZone paramDateTimeZone)
/*      */   {
/*  684 */     Chronology localChronology = getChronology().withZone(paramDateTimeZone);
/*  685 */     return new DateTime(getYear(), getMonthOfYear(), getDayOfMonth(), 0, 0, 0, 0, localChronology);
/*      */   }
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
/*  697 */     return toDateTimeAtCurrentTime(null);
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
/*      */   public DateTime toDateTimeAtCurrentTime(DateTimeZone paramDateTimeZone)
/*      */   {
/*  712 */     Chronology localChronology = getChronology().withZone(paramDateTimeZone);
/*  713 */     long l1 = DateTimeUtils.currentTimeMillis();
/*  714 */     long l2 = localChronology.set(this, l1);
/*  715 */     return new DateTime(l2, localChronology);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateMidnight toDateMidnight()
/*      */   {
/*  725 */     return toDateMidnight(null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateMidnight toDateMidnight(DateTimeZone paramDateTimeZone)
/*      */   {
/*  735 */     Chronology localChronology = getChronology().withZone(paramDateTimeZone);
/*  736 */     return new DateMidnight(getYear(), getMonthOfYear(), getDayOfMonth(), localChronology);
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
/*      */   public DateTime toDateTime(TimeOfDay paramTimeOfDay)
/*      */   {
/*  753 */     return toDateTime(paramTimeOfDay, null);
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
/*      */   public DateTime toDateTime(TimeOfDay paramTimeOfDay, DateTimeZone paramDateTimeZone)
/*      */   {
/*  770 */     Chronology localChronology = getChronology().withZone(paramDateTimeZone);
/*  771 */     long l = DateTimeUtils.currentTimeMillis();
/*  772 */     l = localChronology.set(this, l);
/*  773 */     if (paramTimeOfDay != null) {
/*  774 */       l = localChronology.set(paramTimeOfDay, l);
/*      */     }
/*  776 */     return new DateTime(l, localChronology);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Interval toInterval()
/*      */   {
/*  787 */     return toInterval(null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Interval toInterval(DateTimeZone paramDateTimeZone)
/*      */   {
/*  797 */     paramDateTimeZone = DateTimeUtils.getZone(paramDateTimeZone);
/*  798 */     return toDateMidnight(paramDateTimeZone).toInterval();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getYear()
/*      */   {
/*  808 */     return getValue(0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMonthOfYear()
/*      */   {
/*  817 */     return getValue(1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getDayOfMonth()
/*      */   {
/*  826 */     return getValue(2);
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
/*      */   public YearMonthDay withYear(int paramInt)
/*      */   {
/*  843 */     int[] arrayOfInt = getValues();
/*  844 */     arrayOfInt = getChronology().year().set(this, 0, arrayOfInt, paramInt);
/*  845 */     return new YearMonthDay(this, arrayOfInt);
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
/*      */   public YearMonthDay withMonthOfYear(int paramInt)
/*      */   {
/*  861 */     int[] arrayOfInt = getValues();
/*  862 */     arrayOfInt = getChronology().monthOfYear().set(this, 1, arrayOfInt, paramInt);
/*  863 */     return new YearMonthDay(this, arrayOfInt);
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
/*      */   public YearMonthDay withDayOfMonth(int paramInt)
/*      */   {
/*  879 */     int[] arrayOfInt = getValues();
/*  880 */     arrayOfInt = getChronology().dayOfMonth().set(this, 2, arrayOfInt, paramInt);
/*  881 */     return new YearMonthDay(this, arrayOfInt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property year()
/*      */   {
/*  891 */     return new Property(this, 0);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property monthOfYear()
/*      */   {
/*  900 */     return new Property(this, 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property dayOfMonth()
/*      */   {
/*  909 */     return new Property(this, 2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString()
/*      */   {
/*  919 */     return ISODateTimeFormat.yearMonthDay().print(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @Deprecated
/*      */   public static class Property
/*      */     extends AbstractPartialFieldProperty
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 5727734012190224363L;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private final YearMonthDay iYearMonthDay;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private final int iFieldIndex;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     Property(YearMonthDay paramYearMonthDay, int paramInt)
/*      */     {
/*  951 */       this.iYearMonthDay = paramYearMonthDay;
/*  952 */       this.iFieldIndex = paramInt;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public DateTimeField getField()
/*      */     {
/*  961 */       return this.iYearMonthDay.getField(this.iFieldIndex);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected ReadablePartial getReadablePartial()
/*      */     {
/*  970 */       return this.iYearMonthDay;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public YearMonthDay getYearMonthDay()
/*      */     {
/*  979 */       return this.iYearMonthDay;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public int get()
/*      */     {
/*  988 */       return this.iYearMonthDay.getValue(this.iFieldIndex);
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
/*      */     public YearMonthDay addToCopy(int paramInt)
/*      */     {
/* 1010 */       int[] arrayOfInt = this.iYearMonthDay.getValues();
/* 1011 */       arrayOfInt = getField().add(this.iYearMonthDay, this.iFieldIndex, arrayOfInt, paramInt);
/* 1012 */       return new YearMonthDay(this.iYearMonthDay, arrayOfInt);
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
/*      */     public YearMonthDay addWrapFieldToCopy(int paramInt)
/*      */     {
/* 1034 */       int[] arrayOfInt = this.iYearMonthDay.getValues();
/* 1035 */       arrayOfInt = getField().addWrapField(this.iYearMonthDay, this.iFieldIndex, arrayOfInt, paramInt);
/* 1036 */       return new YearMonthDay(this.iYearMonthDay, arrayOfInt);
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
/*      */     public YearMonthDay setCopy(int paramInt)
/*      */     {
/* 1051 */       int[] arrayOfInt = this.iYearMonthDay.getValues();
/* 1052 */       arrayOfInt = getField().set(this.iYearMonthDay, this.iFieldIndex, arrayOfInt, paramInt);
/* 1053 */       return new YearMonthDay(this.iYearMonthDay, arrayOfInt);
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
/*      */     public YearMonthDay setCopy(String paramString, Locale paramLocale)
/*      */     {
/* 1068 */       int[] arrayOfInt = this.iYearMonthDay.getValues();
/* 1069 */       arrayOfInt = getField().set(this.iYearMonthDay, this.iFieldIndex, arrayOfInt, paramString, paramLocale);
/* 1070 */       return new YearMonthDay(this.iYearMonthDay, arrayOfInt);
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
/*      */     public YearMonthDay setCopy(String paramString)
/*      */     {
/* 1084 */       return setCopy(paramString, null);
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
/*      */     public YearMonthDay withMaximumValue()
/*      */     {
/* 1104 */       return setCopy(getMaximumValue());
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
/*      */     public YearMonthDay withMinimumValue()
/*      */     {
/* 1117 */       return setCopy(getMinimumValue());
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\YearMonthDay.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */