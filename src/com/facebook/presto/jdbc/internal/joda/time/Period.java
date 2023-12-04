/*      */ package com.facebook.presto.jdbc.internal.joda.time;
/*      */ 
/*      */ import com.facebook.presto.jdbc.internal.joda.time.base.BasePeriod;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.chrono.ISOChronology;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.field.FieldUtils;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.format.ISOPeriodFormat;
/*      */ import com.facebook.presto.jdbc.internal.joda.time.format.PeriodFormatter;
/*      */ import java.io.Serializable;
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
/*      */ public final class Period
/*      */   extends BasePeriod
/*      */   implements ReadablePeriod, Serializable
/*      */ {
/*   65 */   public static final Period ZERO = new Period();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static final long serialVersionUID = 741052353876488155L;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   @FromString
/*      */   public static Period parse(String paramString)
/*      */   {
/*   81 */     return parse(paramString, ISOPeriodFormat.standard());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Period parse(String paramString, PeriodFormatter paramPeriodFormatter)
/*      */   {
/*   92 */     return paramPeriodFormatter.parsePeriod(paramString);
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
/*      */   public static Period years(int paramInt)
/*      */   {
/*  110 */     return new Period(new int[] { paramInt, 0, 0, 0, 0, 0, 0, 0, 0 }, PeriodType.standard());
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
/*      */   public static Period months(int paramInt)
/*      */   {
/*  127 */     return new Period(new int[] { 0, paramInt, 0, 0, 0, 0, 0, 0 }, PeriodType.standard());
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
/*      */   public static Period weeks(int paramInt)
/*      */   {
/*  144 */     return new Period(new int[] { 0, 0, paramInt, 0, 0, 0, 0, 0 }, PeriodType.standard());
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
/*      */   public static Period days(int paramInt)
/*      */   {
/*  161 */     return new Period(new int[] { 0, 0, 0, paramInt, 0, 0, 0, 0 }, PeriodType.standard());
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
/*      */   public static Period hours(int paramInt)
/*      */   {
/*  178 */     return new Period(new int[] { 0, 0, 0, 0, paramInt, 0, 0, 0 }, PeriodType.standard());
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
/*      */   public static Period minutes(int paramInt)
/*      */   {
/*  195 */     return new Period(new int[] { 0, 0, 0, 0, 0, paramInt, 0, 0 }, PeriodType.standard());
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
/*      */   public static Period seconds(int paramInt)
/*      */   {
/*  212 */     return new Period(new int[] { 0, 0, 0, 0, 0, 0, paramInt, 0 }, PeriodType.standard());
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
/*      */   public static Period millis(int paramInt)
/*      */   {
/*  226 */     return new Period(new int[] { 0, 0, 0, 0, 0, 0, 0, paramInt }, PeriodType.standard());
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
/*      */   public static Period fieldDifference(ReadablePartial paramReadablePartial1, ReadablePartial paramReadablePartial2)
/*      */   {
/*  257 */     if ((paramReadablePartial1 == null) || (paramReadablePartial2 == null)) {
/*  258 */       throw new IllegalArgumentException("ReadablePartial objects must not be null");
/*      */     }
/*  260 */     if (paramReadablePartial1.size() != paramReadablePartial2.size()) {
/*  261 */       throw new IllegalArgumentException("ReadablePartial objects must have the same set of fields");
/*      */     }
/*  263 */     DurationFieldType[] arrayOfDurationFieldType = new DurationFieldType[paramReadablePartial1.size()];
/*  264 */     int[] arrayOfInt = new int[paramReadablePartial1.size()];
/*  265 */     int i = 0; for (int j = paramReadablePartial1.size(); i < j; i++) {
/*  266 */       if (paramReadablePartial1.getFieldType(i) != paramReadablePartial2.getFieldType(i)) {
/*  267 */         throw new IllegalArgumentException("ReadablePartial objects must have the same set of fields");
/*      */       }
/*  269 */       arrayOfDurationFieldType[i] = paramReadablePartial1.getFieldType(i).getDurationType();
/*  270 */       if ((i > 0) && (arrayOfDurationFieldType[(i - 1)] == arrayOfDurationFieldType[i])) {
/*  271 */         throw new IllegalArgumentException("ReadablePartial objects must not have overlapping fields");
/*      */       }
/*  273 */       arrayOfInt[i] = (paramReadablePartial2.getValue(i) - paramReadablePartial1.getValue(i));
/*      */     }
/*  275 */     return new Period(arrayOfInt, PeriodType.forFields(arrayOfDurationFieldType));
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
/*      */   public Period()
/*      */   {
/*  298 */     super(0L, null, null);
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
/*      */   public Period(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  312 */     super(0, 0, 0, 0, paramInt1, paramInt2, paramInt3, paramInt4, PeriodType.standard());
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
/*      */   public Period(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8)
/*      */   {
/*  329 */     super(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, PeriodType.standard());
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
/*      */   public Period(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, PeriodType paramPeriodType)
/*      */   {
/*  352 */     super(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramPeriodType);
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
/*      */   public Period(long paramLong)
/*      */   {
/*  382 */     super(paramLong);
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
/*      */   public Period(long paramLong, PeriodType paramPeriodType)
/*      */   {
/*  402 */     super(paramLong, paramPeriodType, null);
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
/*      */   public Period(long paramLong, Chronology paramChronology)
/*      */   {
/*  423 */     super(paramLong, null, paramChronology);
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
/*      */   public Period(long paramLong, PeriodType paramPeriodType, Chronology paramChronology)
/*      */   {
/*  444 */     super(paramLong, paramPeriodType, paramChronology);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Period(long paramLong1, long paramLong2)
/*      */   {
/*  455 */     super(paramLong1, paramLong2, null, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Period(long paramLong1, long paramLong2, PeriodType paramPeriodType)
/*      */   {
/*  466 */     super(paramLong1, paramLong2, paramPeriodType, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Period(long paramLong1, long paramLong2, Chronology paramChronology)
/*      */   {
/*  478 */     super(paramLong1, paramLong2, null, paramChronology);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Period(long paramLong1, long paramLong2, PeriodType paramPeriodType, Chronology paramChronology)
/*      */   {
/*  490 */     super(paramLong1, paramLong2, paramPeriodType, paramChronology);
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
/*      */   public Period(ReadableInstant paramReadableInstant1, ReadableInstant paramReadableInstant2)
/*      */   {
/*  519 */     super(paramReadableInstant1, paramReadableInstant2, null);
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
/*      */   public Period(ReadableInstant paramReadableInstant1, ReadableInstant paramReadableInstant2, PeriodType paramPeriodType)
/*      */   {
/*  548 */     super(paramReadableInstant1, paramReadableInstant2, paramPeriodType);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   public Period(ReadablePartial paramReadablePartial1, ReadablePartial paramReadablePartial2)
/*      */   {
/*  582 */     super(paramReadablePartial1, paramReadablePartial2, null);
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
/*      */ 
/*      */ 
/*      */ 
/*      */   public Period(ReadablePartial paramReadablePartial1, ReadablePartial paramReadablePartial2, PeriodType paramPeriodType)
/*      */   {
/*  616 */     super(paramReadablePartial1, paramReadablePartial2, paramPeriodType);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Period(ReadableInstant paramReadableInstant, ReadableDuration paramReadableDuration)
/*      */   {
/*  626 */     super(paramReadableInstant, paramReadableDuration, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Period(ReadableInstant paramReadableInstant, ReadableDuration paramReadableDuration, PeriodType paramPeriodType)
/*      */   {
/*  637 */     super(paramReadableInstant, paramReadableDuration, paramPeriodType);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Period(ReadableDuration paramReadableDuration, ReadableInstant paramReadableInstant)
/*      */   {
/*  647 */     super(paramReadableDuration, paramReadableInstant, null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Period(ReadableDuration paramReadableDuration, ReadableInstant paramReadableInstant, PeriodType paramPeriodType)
/*      */   {
/*  658 */     super(paramReadableDuration, paramReadableInstant, paramPeriodType);
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
/*      */   public Period(Object paramObject)
/*      */   {
/*  674 */     super(paramObject, null, null);
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
/*      */   public Period(Object paramObject, PeriodType paramPeriodType)
/*      */   {
/*  691 */     super(paramObject, paramPeriodType, null);
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
/*      */   public Period(Object paramObject, Chronology paramChronology)
/*      */   {
/*  708 */     super(paramObject, null, paramChronology);
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
/*      */   public Period(Object paramObject, PeriodType paramPeriodType, Chronology paramChronology)
/*      */   {
/*  726 */     super(paramObject, paramPeriodType, paramChronology);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Period(int[] paramArrayOfInt, PeriodType paramPeriodType)
/*      */   {
/*  736 */     super(paramArrayOfInt, paramPeriodType);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Period toPeriod()
/*      */   {
/*  747 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getYears()
/*      */   {
/*  757 */     return getPeriodType().getIndexedField(this, PeriodType.YEAR_INDEX);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMonths()
/*      */   {
/*  766 */     return getPeriodType().getIndexedField(this, PeriodType.MONTH_INDEX);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getWeeks()
/*      */   {
/*  775 */     return getPeriodType().getIndexedField(this, PeriodType.WEEK_INDEX);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getDays()
/*      */   {
/*  784 */     return getPeriodType().getIndexedField(this, PeriodType.DAY_INDEX);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getHours()
/*      */   {
/*  794 */     return getPeriodType().getIndexedField(this, PeriodType.HOUR_INDEX);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMinutes()
/*      */   {
/*  803 */     return getPeriodType().getIndexedField(this, PeriodType.MINUTE_INDEX);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getSeconds()
/*      */   {
/*  812 */     return getPeriodType().getIndexedField(this, PeriodType.SECOND_INDEX);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMillis()
/*      */   {
/*  821 */     return getPeriodType().getIndexedField(this, PeriodType.MILLI_INDEX);
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
/*      */   public Period withPeriodType(PeriodType paramPeriodType)
/*      */   {
/*  836 */     paramPeriodType = DateTimeUtils.getPeriodType(paramPeriodType);
/*  837 */     if (paramPeriodType.equals(getPeriodType())) {
/*  838 */       return this;
/*      */     }
/*  840 */     return new Period(this, paramPeriodType);
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
/*      */   public Period withFields(ReadablePeriod paramReadablePeriod)
/*      */   {
/*  854 */     if (paramReadablePeriod == null) {
/*  855 */       return this;
/*      */     }
/*  857 */     int[] arrayOfInt = getValues();
/*  858 */     arrayOfInt = super.mergePeriodInto(arrayOfInt, paramReadablePeriod);
/*  859 */     return new Period(arrayOfInt, getPeriodType());
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
/*      */   public Period withField(DurationFieldType paramDurationFieldType, int paramInt)
/*      */   {
/*  874 */     if (paramDurationFieldType == null) {
/*  875 */       throw new IllegalArgumentException("Field must not be null");
/*      */     }
/*  877 */     int[] arrayOfInt = getValues();
/*  878 */     super.setFieldInto(arrayOfInt, paramDurationFieldType, paramInt);
/*  879 */     return new Period(arrayOfInt, getPeriodType());
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
/*      */   public Period withFieldAdded(DurationFieldType paramDurationFieldType, int paramInt)
/*      */   {
/*  893 */     if (paramDurationFieldType == null) {
/*  894 */       throw new IllegalArgumentException("Field must not be null");
/*      */     }
/*  896 */     if (paramInt == 0) {
/*  897 */       return this;
/*      */     }
/*  899 */     int[] arrayOfInt = getValues();
/*  900 */     super.addFieldInto(arrayOfInt, paramDurationFieldType, paramInt);
/*  901 */     return new Period(arrayOfInt, getPeriodType());
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
/*      */   public Period withYears(int paramInt)
/*      */   {
/*  915 */     int[] arrayOfInt = getValues();
/*  916 */     getPeriodType().setIndexedField(this, PeriodType.YEAR_INDEX, arrayOfInt, paramInt);
/*  917 */     return new Period(arrayOfInt, getPeriodType());
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
/*      */   public Period withMonths(int paramInt)
/*      */   {
/*  930 */     int[] arrayOfInt = getValues();
/*  931 */     getPeriodType().setIndexedField(this, PeriodType.MONTH_INDEX, arrayOfInt, paramInt);
/*  932 */     return new Period(arrayOfInt, getPeriodType());
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
/*      */   public Period withWeeks(int paramInt)
/*      */   {
/*  945 */     int[] arrayOfInt = getValues();
/*  946 */     getPeriodType().setIndexedField(this, PeriodType.WEEK_INDEX, arrayOfInt, paramInt);
/*  947 */     return new Period(arrayOfInt, getPeriodType());
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
/*      */   public Period withDays(int paramInt)
/*      */   {
/*  960 */     int[] arrayOfInt = getValues();
/*  961 */     getPeriodType().setIndexedField(this, PeriodType.DAY_INDEX, arrayOfInt, paramInt);
/*  962 */     return new Period(arrayOfInt, getPeriodType());
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
/*      */   public Period withHours(int paramInt)
/*      */   {
/*  975 */     int[] arrayOfInt = getValues();
/*  976 */     getPeriodType().setIndexedField(this, PeriodType.HOUR_INDEX, arrayOfInt, paramInt);
/*  977 */     return new Period(arrayOfInt, getPeriodType());
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
/*      */   public Period withMinutes(int paramInt)
/*      */   {
/*  990 */     int[] arrayOfInt = getValues();
/*  991 */     getPeriodType().setIndexedField(this, PeriodType.MINUTE_INDEX, arrayOfInt, paramInt);
/*  992 */     return new Period(arrayOfInt, getPeriodType());
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
/*      */   public Period withSeconds(int paramInt)
/*      */   {
/* 1005 */     int[] arrayOfInt = getValues();
/* 1006 */     getPeriodType().setIndexedField(this, PeriodType.SECOND_INDEX, arrayOfInt, paramInt);
/* 1007 */     return new Period(arrayOfInt, getPeriodType());
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
/*      */   public Period withMillis(int paramInt)
/*      */   {
/* 1020 */     int[] arrayOfInt = getValues();
/* 1021 */     getPeriodType().setIndexedField(this, PeriodType.MILLI_INDEX, arrayOfInt, paramInt);
/* 1022 */     return new Period(arrayOfInt, getPeriodType());
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
/*      */   public Period plus(ReadablePeriod paramReadablePeriod)
/*      */   {
/* 1044 */     if (paramReadablePeriod == null) {
/* 1045 */       return this;
/*      */     }
/* 1047 */     int[] arrayOfInt = getValues();
/* 1048 */     getPeriodType().addIndexedField(this, PeriodType.YEAR_INDEX, arrayOfInt, paramReadablePeriod.get(DurationFieldType.YEARS_TYPE));
/* 1049 */     getPeriodType().addIndexedField(this, PeriodType.MONTH_INDEX, arrayOfInt, paramReadablePeriod.get(DurationFieldType.MONTHS_TYPE));
/* 1050 */     getPeriodType().addIndexedField(this, PeriodType.WEEK_INDEX, arrayOfInt, paramReadablePeriod.get(DurationFieldType.WEEKS_TYPE));
/* 1051 */     getPeriodType().addIndexedField(this, PeriodType.DAY_INDEX, arrayOfInt, paramReadablePeriod.get(DurationFieldType.DAYS_TYPE));
/* 1052 */     getPeriodType().addIndexedField(this, PeriodType.HOUR_INDEX, arrayOfInt, paramReadablePeriod.get(DurationFieldType.HOURS_TYPE));
/* 1053 */     getPeriodType().addIndexedField(this, PeriodType.MINUTE_INDEX, arrayOfInt, paramReadablePeriod.get(DurationFieldType.MINUTES_TYPE));
/* 1054 */     getPeriodType().addIndexedField(this, PeriodType.SECOND_INDEX, arrayOfInt, paramReadablePeriod.get(DurationFieldType.SECONDS_TYPE));
/* 1055 */     getPeriodType().addIndexedField(this, PeriodType.MILLI_INDEX, arrayOfInt, paramReadablePeriod.get(DurationFieldType.MILLIS_TYPE));
/* 1056 */     return new Period(arrayOfInt, getPeriodType());
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
/*      */   public Period plusYears(int paramInt)
/*      */   {
/* 1070 */     if (paramInt == 0) {
/* 1071 */       return this;
/*      */     }
/* 1073 */     int[] arrayOfInt = getValues();
/* 1074 */     getPeriodType().addIndexedField(this, PeriodType.YEAR_INDEX, arrayOfInt, paramInt);
/* 1075 */     return new Period(arrayOfInt, getPeriodType());
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
/*      */   public Period plusMonths(int paramInt)
/*      */   {
/* 1088 */     if (paramInt == 0) {
/* 1089 */       return this;
/*      */     }
/* 1091 */     int[] arrayOfInt = getValues();
/* 1092 */     getPeriodType().addIndexedField(this, PeriodType.MONTH_INDEX, arrayOfInt, paramInt);
/* 1093 */     return new Period(arrayOfInt, getPeriodType());
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
/*      */   public Period plusWeeks(int paramInt)
/*      */   {
/* 1106 */     if (paramInt == 0) {
/* 1107 */       return this;
/*      */     }
/* 1109 */     int[] arrayOfInt = getValues();
/* 1110 */     getPeriodType().addIndexedField(this, PeriodType.WEEK_INDEX, arrayOfInt, paramInt);
/* 1111 */     return new Period(arrayOfInt, getPeriodType());
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
/*      */   public Period plusDays(int paramInt)
/*      */   {
/* 1124 */     if (paramInt == 0) {
/* 1125 */       return this;
/*      */     }
/* 1127 */     int[] arrayOfInt = getValues();
/* 1128 */     getPeriodType().addIndexedField(this, PeriodType.DAY_INDEX, arrayOfInt, paramInt);
/* 1129 */     return new Period(arrayOfInt, getPeriodType());
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
/*      */   public Period plusHours(int paramInt)
/*      */   {
/* 1142 */     if (paramInt == 0) {
/* 1143 */       return this;
/*      */     }
/* 1145 */     int[] arrayOfInt = getValues();
/* 1146 */     getPeriodType().addIndexedField(this, PeriodType.HOUR_INDEX, arrayOfInt, paramInt);
/* 1147 */     return new Period(arrayOfInt, getPeriodType());
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
/*      */   public Period plusMinutes(int paramInt)
/*      */   {
/* 1160 */     if (paramInt == 0) {
/* 1161 */       return this;
/*      */     }
/* 1163 */     int[] arrayOfInt = getValues();
/* 1164 */     getPeriodType().addIndexedField(this, PeriodType.MINUTE_INDEX, arrayOfInt, paramInt);
/* 1165 */     return new Period(arrayOfInt, getPeriodType());
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
/*      */   public Period plusSeconds(int paramInt)
/*      */   {
/* 1178 */     if (paramInt == 0) {
/* 1179 */       return this;
/*      */     }
/* 1181 */     int[] arrayOfInt = getValues();
/* 1182 */     getPeriodType().addIndexedField(this, PeriodType.SECOND_INDEX, arrayOfInt, paramInt);
/* 1183 */     return new Period(arrayOfInt, getPeriodType());
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
/*      */   public Period plusMillis(int paramInt)
/*      */   {
/* 1196 */     if (paramInt == 0) {
/* 1197 */       return this;
/*      */     }
/* 1199 */     int[] arrayOfInt = getValues();
/* 1200 */     getPeriodType().addIndexedField(this, PeriodType.MILLI_INDEX, arrayOfInt, paramInt);
/* 1201 */     return new Period(arrayOfInt, getPeriodType());
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
/*      */   public Period minus(ReadablePeriod paramReadablePeriod)
/*      */   {
/* 1223 */     if (paramReadablePeriod == null) {
/* 1224 */       return this;
/*      */     }
/* 1226 */     int[] arrayOfInt = getValues();
/* 1227 */     getPeriodType().addIndexedField(this, PeriodType.YEAR_INDEX, arrayOfInt, -paramReadablePeriod.get(DurationFieldType.YEARS_TYPE));
/* 1228 */     getPeriodType().addIndexedField(this, PeriodType.MONTH_INDEX, arrayOfInt, -paramReadablePeriod.get(DurationFieldType.MONTHS_TYPE));
/* 1229 */     getPeriodType().addIndexedField(this, PeriodType.WEEK_INDEX, arrayOfInt, -paramReadablePeriod.get(DurationFieldType.WEEKS_TYPE));
/* 1230 */     getPeriodType().addIndexedField(this, PeriodType.DAY_INDEX, arrayOfInt, -paramReadablePeriod.get(DurationFieldType.DAYS_TYPE));
/* 1231 */     getPeriodType().addIndexedField(this, PeriodType.HOUR_INDEX, arrayOfInt, -paramReadablePeriod.get(DurationFieldType.HOURS_TYPE));
/* 1232 */     getPeriodType().addIndexedField(this, PeriodType.MINUTE_INDEX, arrayOfInt, -paramReadablePeriod.get(DurationFieldType.MINUTES_TYPE));
/* 1233 */     getPeriodType().addIndexedField(this, PeriodType.SECOND_INDEX, arrayOfInt, -paramReadablePeriod.get(DurationFieldType.SECONDS_TYPE));
/* 1234 */     getPeriodType().addIndexedField(this, PeriodType.MILLI_INDEX, arrayOfInt, -paramReadablePeriod.get(DurationFieldType.MILLIS_TYPE));
/* 1235 */     return new Period(arrayOfInt, getPeriodType());
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
/*      */   public Period minusYears(int paramInt)
/*      */   {
/* 1249 */     return plusYears(-paramInt);
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
/*      */   public Period minusMonths(int paramInt)
/*      */   {
/* 1262 */     return plusMonths(-paramInt);
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
/*      */   public Period minusWeeks(int paramInt)
/*      */   {
/* 1275 */     return plusWeeks(-paramInt);
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
/*      */   public Period minusDays(int paramInt)
/*      */   {
/* 1288 */     return plusDays(-paramInt);
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
/*      */   public Period minusHours(int paramInt)
/*      */   {
/* 1301 */     return plusHours(-paramInt);
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
/*      */   public Period minusMinutes(int paramInt)
/*      */   {
/* 1314 */     return plusMinutes(-paramInt);
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
/*      */   public Period minusSeconds(int paramInt)
/*      */   {
/* 1327 */     return plusSeconds(-paramInt);
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
/*      */   public Period minusMillis(int paramInt)
/*      */   {
/* 1340 */     return plusMillis(-paramInt);
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
/*      */   public Period multipliedBy(int paramInt)
/*      */   {
/* 1354 */     if ((this == ZERO) || (paramInt == 1)) {
/* 1355 */       return this;
/*      */     }
/* 1357 */     int[] arrayOfInt = getValues();
/* 1358 */     for (int i = 0; i < arrayOfInt.length; i++) {
/* 1359 */       arrayOfInt[i] = FieldUtils.safeMultiply(arrayOfInt[i], paramInt);
/*      */     }
/* 1361 */     return new Period(arrayOfInt, getPeriodType());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Period negated()
/*      */   {
/* 1372 */     return multipliedBy(-1);
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
/*      */   public Weeks toStandardWeeks()
/*      */   {
/* 1396 */     checkYearsAndMonths("Weeks");
/* 1397 */     long l1 = getMillis();
/* 1398 */     l1 += getSeconds() * 1000L;
/* 1399 */     l1 += getMinutes() * 60000L;
/* 1400 */     l1 += getHours() * 3600000L;
/* 1401 */     l1 += getDays() * 86400000L;
/* 1402 */     long l2 = getWeeks() + l1 / 604800000L;
/* 1403 */     return Weeks.weeks(FieldUtils.safeToInt(l2));
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
/*      */   public Days toStandardDays()
/*      */   {
/* 1426 */     checkYearsAndMonths("Days");
/* 1427 */     long l1 = getMillis();
/* 1428 */     l1 += getSeconds() * 1000L;
/* 1429 */     l1 += getMinutes() * 60000L;
/* 1430 */     l1 += getHours() * 3600000L;
/* 1431 */     long l2 = l1 / 86400000L;
/* 1432 */     l2 = FieldUtils.safeAdd(l2, getDays());
/* 1433 */     l2 = FieldUtils.safeAdd(l2, getWeeks() * 7L);
/* 1434 */     return Days.days(FieldUtils.safeToInt(l2));
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
/*      */   public Hours toStandardHours()
/*      */   {
/* 1457 */     checkYearsAndMonths("Hours");
/* 1458 */     long l1 = getMillis();
/* 1459 */     l1 += getSeconds() * 1000L;
/* 1460 */     l1 += getMinutes() * 60000L;
/* 1461 */     long l2 = l1 / 3600000L;
/* 1462 */     l2 = FieldUtils.safeAdd(l2, getHours());
/* 1463 */     l2 = FieldUtils.safeAdd(l2, getDays() * 24L);
/* 1464 */     l2 = FieldUtils.safeAdd(l2, getWeeks() * 168L);
/* 1465 */     return Hours.hours(FieldUtils.safeToInt(l2));
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
/*      */   public Minutes toStandardMinutes()
/*      */   {
/* 1488 */     checkYearsAndMonths("Minutes");
/* 1489 */     long l1 = getMillis();
/* 1490 */     l1 += getSeconds() * 1000L;
/* 1491 */     long l2 = l1 / 60000L;
/* 1492 */     l2 = FieldUtils.safeAdd(l2, getMinutes());
/* 1493 */     l2 = FieldUtils.safeAdd(l2, getHours() * 60L);
/* 1494 */     l2 = FieldUtils.safeAdd(l2, getDays() * 1440L);
/* 1495 */     l2 = FieldUtils.safeAdd(l2, getWeeks() * 10080L);
/* 1496 */     return Minutes.minutes(FieldUtils.safeToInt(l2));
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
/*      */   public Seconds toStandardSeconds()
/*      */   {
/* 1519 */     checkYearsAndMonths("Seconds");
/* 1520 */     long l = getMillis() / 1000;
/* 1521 */     l = FieldUtils.safeAdd(l, getSeconds());
/* 1522 */     l = FieldUtils.safeAdd(l, getMinutes() * 60L);
/* 1523 */     l = FieldUtils.safeAdd(l, getHours() * 3600L);
/* 1524 */     l = FieldUtils.safeAdd(l, getDays() * 86400L);
/* 1525 */     l = FieldUtils.safeAdd(l, getWeeks() * 604800L);
/* 1526 */     return Seconds.seconds(FieldUtils.safeToInt(l));
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
/*      */   public Duration toStandardDuration()
/*      */   {
/* 1549 */     checkYearsAndMonths("Duration");
/* 1550 */     long l = getMillis();
/* 1551 */     l += getSeconds() * 1000L;
/* 1552 */     l += getMinutes() * 60000L;
/* 1553 */     l += getHours() * 3600000L;
/* 1554 */     l += getDays() * 86400000L;
/* 1555 */     l += getWeeks() * 604800000L;
/* 1556 */     return new Duration(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void checkYearsAndMonths(String paramString)
/*      */   {
/* 1566 */     if (getMonths() != 0) {
/* 1567 */       throw new UnsupportedOperationException("Cannot convert to " + paramString + " as this period contains months and months vary in length");
/*      */     }
/* 1569 */     if (getYears() != 0) {
/* 1570 */       throw new UnsupportedOperationException("Cannot convert to " + paramString + " as this period contains years and years vary in length");
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Period normalizedStandard()
/*      */   {
/* 1601 */     return normalizedStandard(PeriodType.standard());
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Period normalizedStandard(PeriodType paramPeriodType)
/*      */   {
/* 1637 */     paramPeriodType = DateTimeUtils.getPeriodType(paramPeriodType);
/* 1638 */     long l1 = getMillis();
/* 1639 */     l1 += getSeconds() * 1000L;
/* 1640 */     l1 += getMinutes() * 60000L;
/* 1641 */     l1 += getHours() * 3600000L;
/* 1642 */     l1 += getDays() * 86400000L;
/* 1643 */     l1 += getWeeks() * 604800000L;
/* 1644 */     Period localPeriod = new Period(l1, paramPeriodType, ISOChronology.getInstanceUTC());
/* 1645 */     int i = getYears();
/* 1646 */     int j = getMonths();
/* 1647 */     if ((i != 0) || (j != 0)) {
/* 1648 */       long l2 = i * 12L + j;
/* 1649 */       int k; if (paramPeriodType.isSupported(DurationFieldType.YEARS_TYPE)) {
/* 1650 */         k = FieldUtils.safeToInt(l2 / 12L);
/* 1651 */         localPeriod = localPeriod.withYears(k);
/* 1652 */         l2 -= k * 12;
/*      */       }
/* 1654 */       if (paramPeriodType.isSupported(DurationFieldType.MONTHS_TYPE)) {
/* 1655 */         k = FieldUtils.safeToInt(l2);
/* 1656 */         localPeriod = localPeriod.withMonths(k);
/* 1657 */         l2 -= k;
/*      */       }
/* 1659 */       if (l2 != 0L) {
/* 1660 */         throw new UnsupportedOperationException("Unable to normalize as PeriodType is missing either years or months but period has a month/year amount: " + toString());
/*      */       }
/*      */     }
/* 1663 */     return localPeriod;
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\Period.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */