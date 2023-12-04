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
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.Locale;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class LocalDateTime
/*      */   extends BaseLocal
/*      */   implements ReadablePartial, Serializable
/*      */ {
/*      */   private static final long serialVersionUID = -268716875315837168L;
/*      */   private static final int YEAR = 0;
/*      */   private static final int MONTH_OF_YEAR = 1;
/*      */   private static final int DAY_OF_MONTH = 2;
/*      */   private static final int MILLIS_OF_DAY = 3;
/*      */   private final long iLocalMillis;
/*      */   private final Chronology iChronology;
/*      */   
/*      */   public static LocalDateTime now()
/*      */   {
/*  111 */     return new LocalDateTime();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static LocalDateTime now(DateTimeZone paramDateTimeZone)
/*      */   {
/*  124 */     if (paramDateTimeZone == null) {
/*  125 */       throw new NullPointerException("Zone must not be null");
/*      */     }
/*  127 */     return new LocalDateTime(paramDateTimeZone);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static LocalDateTime now(Chronology paramChronology)
/*      */   {
/*  140 */     if (paramChronology == null) {
/*  141 */       throw new NullPointerException("Chronology must not be null");
/*      */     }
/*  143 */     return new LocalDateTime(paramChronology);
/*      */   }
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
/*      */   public static LocalDateTime parse(String paramString)
/*      */   {
/*  157 */     return parse(paramString, ISODateTimeFormat.localDateOptionalTimeParser());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static LocalDateTime parse(String paramString, DateTimeFormatter paramDateTimeFormatter)
/*      */   {
/*  168 */     return paramDateTimeFormatter.parseLocalDateTime(paramString);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static LocalDateTime fromCalendarFields(Calendar paramCalendar)
/*      */   {
/*  196 */     if (paramCalendar == null) {
/*  197 */       throw new IllegalArgumentException("The calendar must not be null");
/*      */     }
/*  199 */     int i = paramCalendar.get(0);
/*  200 */     int j = paramCalendar.get(1);
/*  201 */     return new LocalDateTime(i == 1 ? j : 1 - j, paramCalendar.get(2) + 1, paramCalendar.get(5), paramCalendar.get(11), paramCalendar.get(12), paramCalendar.get(13), paramCalendar.get(14));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static LocalDateTime fromDateFields(Date paramDate)
/*      */   {
/*  234 */     if (paramDate == null) {
/*  235 */       throw new IllegalArgumentException("The date must not be null");
/*      */     }
/*  237 */     if (paramDate.getTime() < 0L)
/*      */     {
/*  239 */       GregorianCalendar localGregorianCalendar = new GregorianCalendar();
/*  240 */       localGregorianCalendar.setTime(paramDate);
/*  241 */       return fromCalendarFields(localGregorianCalendar);
/*      */     }
/*  243 */     return new LocalDateTime(paramDate.getYear() + 1900, paramDate.getMonth() + 1, paramDate.getDate(), paramDate.getHours(), paramDate.getMinutes(), paramDate.getSeconds(), ((int)(paramDate.getTime() % 1000L) + 1000) % 1000);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime()
/*      */   {
/*  264 */     this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime(DateTimeZone paramDateTimeZone)
/*      */   {
/*  278 */     this(DateTimeUtils.currentTimeMillis(), ISOChronology.getInstance(paramDateTimeZone));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime(Chronology paramChronology)
/*      */   {
/*  292 */     this(DateTimeUtils.currentTimeMillis(), paramChronology);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime(long paramLong)
/*      */   {
/*  305 */     this(paramLong, ISOChronology.getInstance());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime(long paramLong, DateTimeZone paramDateTimeZone)
/*      */   {
/*  319 */     this(paramLong, ISOChronology.getInstance(paramDateTimeZone));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime(long paramLong, Chronology paramChronology)
/*      */   {
/*  333 */     paramChronology = DateTimeUtils.getChronology(paramChronology);
/*      */     
/*  335 */     long l = paramChronology.getZone().getMillisKeepLocal(DateTimeZone.UTC, paramLong);
/*  336 */     this.iLocalMillis = l;
/*  337 */     this.iChronology = paramChronology.withUTC();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime(Object paramObject)
/*      */   {
/*  358 */     this(paramObject, (Chronology)null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime(Object paramObject, DateTimeZone paramDateTimeZone)
/*      */   {
/*  380 */     PartialConverter localPartialConverter = ConverterManager.getInstance().getPartialConverter(paramObject);
/*  381 */     Chronology localChronology = localPartialConverter.getChronology(paramObject, paramDateTimeZone);
/*  382 */     localChronology = DateTimeUtils.getChronology(localChronology);
/*  383 */     this.iChronology = localChronology.withUTC();
/*  384 */     int[] arrayOfInt = localPartialConverter.getPartialValues(this, paramObject, localChronology, ISODateTimeFormat.localDateOptionalTimeParser());
/*  385 */     this.iLocalMillis = this.iChronology.getDateTimeMillis(arrayOfInt[0], arrayOfInt[1], arrayOfInt[2], arrayOfInt[3]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime(Object paramObject, Chronology paramChronology)
/*      */   {
/*  410 */     PartialConverter localPartialConverter = ConverterManager.getInstance().getPartialConverter(paramObject);
/*  411 */     paramChronology = localPartialConverter.getChronology(paramObject, paramChronology);
/*  412 */     paramChronology = DateTimeUtils.getChronology(paramChronology);
/*  413 */     this.iChronology = paramChronology.withUTC();
/*  414 */     int[] arrayOfInt = localPartialConverter.getPartialValues(this, paramObject, paramChronology, ISODateTimeFormat.localDateOptionalTimeParser());
/*  415 */     this.iLocalMillis = this.iChronology.getDateTimeMillis(arrayOfInt[0], arrayOfInt[1], arrayOfInt[2], arrayOfInt[3]);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
/*      */   {
/*  435 */     this(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, 0, 0, ISOChronology.getInstanceUTC());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
/*      */   {
/*  457 */     this(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 0, ISOChronology.getInstanceUTC());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
/*      */   {
/*  481 */     this(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, ISOChronology.getInstanceUTC());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, Chronology paramChronology)
/*      */   {
/*  510 */     paramChronology = DateTimeUtils.getChronology(paramChronology).withUTC();
/*  511 */     long l = paramChronology.getDateTimeMillis(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
/*      */     
/*  513 */     this.iChronology = paramChronology;
/*  514 */     this.iLocalMillis = l;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private Object readResolve()
/*      */   {
/*  522 */     if (this.iChronology == null) {
/*  523 */       return new LocalDateTime(this.iLocalMillis, ISOChronology.getInstanceUTC());
/*      */     }
/*  525 */     if (!DateTimeZone.UTC.equals(this.iChronology.getZone())) {
/*  526 */       return new LocalDateTime(this.iLocalMillis, this.iChronology.withUTC());
/*      */     }
/*  528 */     return this;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int size()
/*      */   {
/*  539 */     return 4;
/*      */   }
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
/*  552 */     switch (paramInt) {
/*      */     case 0: 
/*  554 */       return paramChronology.year();
/*      */     case 1: 
/*  556 */       return paramChronology.monthOfYear();
/*      */     case 2: 
/*  558 */       return paramChronology.dayOfMonth();
/*      */     case 3: 
/*  560 */       return paramChronology.millisOfDay();
/*      */     }
/*  562 */     throw new IndexOutOfBoundsException("Invalid index: " + paramInt);
/*      */   }
/*      */   
/*      */ 
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
/*  577 */     switch (paramInt) {
/*      */     case 0: 
/*  579 */       return getChronology().year().get(getLocalMillis());
/*      */     case 1: 
/*  581 */       return getChronology().monthOfYear().get(getLocalMillis());
/*      */     case 2: 
/*  583 */       return getChronology().dayOfMonth().get(getLocalMillis());
/*      */     case 3: 
/*  585 */       return getChronology().millisOfDay().get(getLocalMillis());
/*      */     }
/*  587 */     throw new IndexOutOfBoundsException("Invalid index: " + paramInt);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*  607 */     if (paramDateTimeFieldType == null) {
/*  608 */       throw new IllegalArgumentException("The DateTimeFieldType must not be null");
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
/*  625 */     return paramDateTimeFieldType.getField(getChronology()).isSupported();
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
/*  636 */     if (paramDurationFieldType == null) {
/*  637 */       return false;
/*      */     }
/*  639 */     return paramDurationFieldType.getField(getChronology()).isSupported();
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
/*  651 */     return this.iLocalMillis;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Chronology getChronology()
/*      */   {
/*  660 */     return this.iChronology;
/*      */   }
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
/*  673 */     if (this == paramObject) {
/*  674 */       return true;
/*      */     }
/*  676 */     if ((paramObject instanceof LocalDateTime)) {
/*  677 */       LocalDateTime localLocalDateTime = (LocalDateTime)paramObject;
/*  678 */       if (this.iChronology.equals(localLocalDateTime.iChronology)) {
/*  679 */         return this.iLocalMillis == localLocalDateTime.iLocalMillis;
/*      */       }
/*      */     }
/*  682 */     return super.equals(paramObject);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*  703 */     if (this == paramReadablePartial) {
/*  704 */       return 0;
/*      */     }
/*  706 */     if ((paramReadablePartial instanceof LocalDateTime)) {
/*  707 */       LocalDateTime localLocalDateTime = (LocalDateTime)paramReadablePartial;
/*  708 */       if (this.iChronology.equals(localLocalDateTime.iChronology)) {
/*  709 */         return this.iLocalMillis == localLocalDateTime.iLocalMillis ? 0 : this.iLocalMillis < localLocalDateTime.iLocalMillis ? -1 : 1;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  714 */     return super.compareTo(paramReadablePartial);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime toDateTime()
/*      */   {
/*  731 */     return toDateTime((DateTimeZone)null);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public DateTime toDateTime(DateTimeZone paramDateTimeZone)
/*      */   {
/*  748 */     paramDateTimeZone = DateTimeUtils.getZone(paramDateTimeZone);
/*  749 */     Chronology localChronology = this.iChronology.withZone(paramDateTimeZone);
/*  750 */     return new DateTime(getYear(), getMonthOfYear(), getDayOfMonth(), getHourOfDay(), getMinuteOfHour(), getSecondOfMinute(), getMillisOfSecond(), localChronology);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDate toLocalDate()
/*      */   {
/*  763 */     return new LocalDate(getLocalMillis(), getChronology());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalTime toLocalTime()
/*      */   {
/*  772 */     return new LocalTime(getLocalMillis(), getChronology());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
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
/*  795 */     int i = getDayOfMonth();
/*  796 */     Date localDate = new Date(getYear() - 1900, getMonthOfYear() - 1, i, getHourOfDay(), getMinuteOfHour(), getSecondOfMinute());
/*      */     
/*  798 */     localDate.setTime(localDate.getTime() + getMillisOfSecond());
/*  799 */     return correctDstTransition(localDate, TimeZone.getDefault());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Date toDate(TimeZone paramTimeZone)
/*      */   {
/*  824 */     Calendar localCalendar = Calendar.getInstance(paramTimeZone);
/*  825 */     localCalendar.clear();
/*  826 */     localCalendar.set(getYear(), getMonthOfYear() - 1, getDayOfMonth(), getHourOfDay(), getMinuteOfHour(), getSecondOfMinute());
/*      */     
/*  828 */     Date localDate = localCalendar.getTime();
/*  829 */     localDate.setTime(localDate.getTime() + getMillisOfSecond());
/*  830 */     return correctDstTransition(localDate, paramTimeZone);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Date correctDstTransition(Date paramDate, TimeZone paramTimeZone)
/*      */   {
/*  849 */     Object localObject = Calendar.getInstance(paramTimeZone);
/*  850 */     ((Calendar)localObject).setTime(paramDate);
/*  851 */     LocalDateTime localLocalDateTime = fromCalendarFields((Calendar)localObject);
/*  852 */     if (localLocalDateTime.isBefore(this))
/*      */     {
/*      */ 
/*  855 */       while (localLocalDateTime.isBefore(this)) {
/*  856 */         ((Calendar)localObject).setTimeInMillis(((Calendar)localObject).getTimeInMillis() + 60000L);
/*  857 */         localLocalDateTime = fromCalendarFields((Calendar)localObject);
/*      */       }
/*      */       
/*  860 */       while (!localLocalDateTime.isBefore(this)) {
/*  861 */         ((Calendar)localObject).setTimeInMillis(((Calendar)localObject).getTimeInMillis() - 1000L);
/*  862 */         localLocalDateTime = fromCalendarFields((Calendar)localObject);
/*      */       }
/*  864 */       ((Calendar)localObject).setTimeInMillis(((Calendar)localObject).getTimeInMillis() + 1000L);
/*  865 */     } else if (localLocalDateTime.equals(this))
/*      */     {
/*  867 */       Calendar localCalendar = Calendar.getInstance(paramTimeZone);
/*  868 */       localCalendar.setTimeInMillis(((Calendar)localObject).getTimeInMillis() - paramTimeZone.getDSTSavings());
/*  869 */       localLocalDateTime = fromCalendarFields(localCalendar);
/*  870 */       if (localLocalDateTime.equals(this)) {
/*  871 */         localObject = localCalendar;
/*      */       }
/*      */     }
/*  874 */     return ((Calendar)localObject).getTime();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   LocalDateTime withLocalMillis(long paramLong)
/*      */   {
/*  889 */     return paramLong == getLocalMillis() ? this : new LocalDateTime(paramLong, getChronology());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime withDate(int paramInt1, int paramInt2, int paramInt3)
/*      */   {
/*  911 */     Chronology localChronology = getChronology();
/*  912 */     long l = getLocalMillis();
/*  913 */     l = localChronology.year().set(l, paramInt1);
/*  914 */     l = localChronology.monthOfYear().set(l, paramInt2);
/*  915 */     l = localChronology.dayOfMonth().set(l, paramInt3);
/*  916 */     return withLocalMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime withTime(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*      */   {
/*  938 */     Chronology localChronology = getChronology();
/*  939 */     long l = getLocalMillis();
/*  940 */     l = localChronology.hourOfDay().set(l, paramInt1);
/*  941 */     l = localChronology.minuteOfHour().set(l, paramInt2);
/*  942 */     l = localChronology.secondOfMinute().set(l, paramInt3);
/*  943 */     l = localChronology.millisOfSecond().set(l, paramInt4);
/*  944 */     return withLocalMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime withFields(ReadablePartial paramReadablePartial)
/*      */   {
/*  961 */     if (paramReadablePartial == null) {
/*  962 */       return this;
/*      */     }
/*  964 */     return withLocalMillis(getChronology().set(paramReadablePartial, getLocalMillis()));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime withField(DateTimeFieldType paramDateTimeFieldType, int paramInt)
/*      */   {
/*  987 */     if (paramDateTimeFieldType == null) {
/*  988 */       throw new IllegalArgumentException("Field must not be null");
/*      */     }
/*  990 */     long l = paramDateTimeFieldType.getField(getChronology()).set(getLocalMillis(), paramInt);
/*  991 */     return withLocalMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime withFieldAdded(DurationFieldType paramDurationFieldType, int paramInt)
/*      */   {
/* 1014 */     if (paramDurationFieldType == null) {
/* 1015 */       throw new IllegalArgumentException("Field must not be null");
/*      */     }
/* 1017 */     if (paramInt == 0) {
/* 1018 */       return this;
/*      */     }
/* 1020 */     long l = paramDurationFieldType.getField(getChronology()).add(getLocalMillis(), paramInt);
/* 1021 */     return withLocalMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime withDurationAdded(ReadableDuration paramReadableDuration, int paramInt)
/*      */   {
/* 1036 */     if ((paramReadableDuration == null) || (paramInt == 0)) {
/* 1037 */       return this;
/*      */     }
/* 1039 */     long l = getChronology().add(getLocalMillis(), paramReadableDuration.getMillis(), paramInt);
/* 1040 */     return withLocalMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime withPeriodAdded(ReadablePeriod paramReadablePeriod, int paramInt)
/*      */   {
/* 1059 */     if ((paramReadablePeriod == null) || (paramInt == 0)) {
/* 1060 */       return this;
/*      */     }
/* 1062 */     long l = getChronology().add(paramReadablePeriod, getLocalMillis(), paramInt);
/* 1063 */     return withLocalMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime plus(ReadableDuration paramReadableDuration)
/*      */   {
/* 1077 */     return withDurationAdded(paramReadableDuration, 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime plus(ReadablePeriod paramReadablePeriod)
/*      */   {
/* 1094 */     return withPeriodAdded(paramReadablePeriod, 1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime plusYears(int paramInt)
/*      */   {
/* 1114 */     if (paramInt == 0) {
/* 1115 */       return this;
/*      */     }
/* 1117 */     long l = getChronology().years().add(getLocalMillis(), paramInt);
/* 1118 */     return withLocalMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime plusMonths(int paramInt)
/*      */   {
/* 1137 */     if (paramInt == 0) {
/* 1138 */       return this;
/*      */     }
/* 1140 */     long l = getChronology().months().add(getLocalMillis(), paramInt);
/* 1141 */     return withLocalMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime plusWeeks(int paramInt)
/*      */   {
/* 1160 */     if (paramInt == 0) {
/* 1161 */       return this;
/*      */     }
/* 1163 */     long l = getChronology().weeks().add(getLocalMillis(), paramInt);
/* 1164 */     return withLocalMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime plusDays(int paramInt)
/*      */   {
/* 1183 */     if (paramInt == 0) {
/* 1184 */       return this;
/*      */     }
/* 1186 */     long l = getChronology().days().add(getLocalMillis(), paramInt);
/* 1187 */     return withLocalMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime plusHours(int paramInt)
/*      */   {
/* 1207 */     if (paramInt == 0) {
/* 1208 */       return this;
/*      */     }
/* 1210 */     long l = getChronology().hours().add(getLocalMillis(), paramInt);
/* 1211 */     return withLocalMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime plusMinutes(int paramInt)
/*      */   {
/* 1230 */     if (paramInt == 0) {
/* 1231 */       return this;
/*      */     }
/* 1233 */     long l = getChronology().minutes().add(getLocalMillis(), paramInt);
/* 1234 */     return withLocalMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime plusSeconds(int paramInt)
/*      */   {
/* 1253 */     if (paramInt == 0) {
/* 1254 */       return this;
/*      */     }
/* 1256 */     long l = getChronology().seconds().add(getLocalMillis(), paramInt);
/* 1257 */     return withLocalMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime plusMillis(int paramInt)
/*      */   {
/* 1276 */     if (paramInt == 0) {
/* 1277 */       return this;
/*      */     }
/* 1279 */     long l = getChronology().millis().add(getLocalMillis(), paramInt);
/* 1280 */     return withLocalMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime minus(ReadableDuration paramReadableDuration)
/*      */   {
/* 1294 */     return withDurationAdded(paramReadableDuration, -1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime minus(ReadablePeriod paramReadablePeriod)
/*      */   {
/* 1311 */     return withPeriodAdded(paramReadablePeriod, -1);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime minusYears(int paramInt)
/*      */   {
/* 1331 */     if (paramInt == 0) {
/* 1332 */       return this;
/*      */     }
/* 1334 */     long l = getChronology().years().subtract(getLocalMillis(), paramInt);
/* 1335 */     return withLocalMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime minusMonths(int paramInt)
/*      */   {
/* 1354 */     if (paramInt == 0) {
/* 1355 */       return this;
/*      */     }
/* 1357 */     long l = getChronology().months().subtract(getLocalMillis(), paramInt);
/* 1358 */     return withLocalMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime minusWeeks(int paramInt)
/*      */   {
/* 1377 */     if (paramInt == 0) {
/* 1378 */       return this;
/*      */     }
/* 1380 */     long l = getChronology().weeks().subtract(getLocalMillis(), paramInt);
/* 1381 */     return withLocalMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime minusDays(int paramInt)
/*      */   {
/* 1400 */     if (paramInt == 0) {
/* 1401 */       return this;
/*      */     }
/* 1403 */     long l = getChronology().days().subtract(getLocalMillis(), paramInt);
/* 1404 */     return withLocalMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime minusHours(int paramInt)
/*      */   {
/* 1424 */     if (paramInt == 0) {
/* 1425 */       return this;
/*      */     }
/* 1427 */     long l = getChronology().hours().subtract(getLocalMillis(), paramInt);
/* 1428 */     return withLocalMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime minusMinutes(int paramInt)
/*      */   {
/* 1447 */     if (paramInt == 0) {
/* 1448 */       return this;
/*      */     }
/* 1450 */     long l = getChronology().minutes().subtract(getLocalMillis(), paramInt);
/* 1451 */     return withLocalMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime minusSeconds(int paramInt)
/*      */   {
/* 1470 */     if (paramInt == 0) {
/* 1471 */       return this;
/*      */     }
/* 1473 */     long l = getChronology().seconds().subtract(getLocalMillis(), paramInt);
/* 1474 */     return withLocalMillis(l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime minusMillis(int paramInt)
/*      */   {
/* 1493 */     if (paramInt == 0) {
/* 1494 */       return this;
/*      */     }
/* 1496 */     long l = getChronology().millis().subtract(getLocalMillis(), paramInt);
/* 1497 */     return withLocalMillis(l);
/*      */   }
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
/* 1510 */     if (paramDateTimeFieldType == null) {
/* 1511 */       throw new IllegalArgumentException("The DateTimeFieldType must not be null");
/*      */     }
/* 1513 */     if (!isSupported(paramDateTimeFieldType)) {
/* 1514 */       throw new IllegalArgumentException("Field '" + paramDateTimeFieldType + "' is not supported");
/*      */     }
/* 1516 */     return new Property(this, paramDateTimeFieldType.getField(getChronology()));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getEra()
/*      */   {
/* 1526 */     return getChronology().era().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getCenturyOfEra()
/*      */   {
/* 1535 */     return getChronology().centuryOfEra().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getYearOfEra()
/*      */   {
/* 1544 */     return getChronology().yearOfEra().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getYearOfCentury()
/*      */   {
/* 1553 */     return getChronology().yearOfCentury().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getYear()
/*      */   {
/* 1562 */     return getChronology().year().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
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
/* 1577 */     return getChronology().weekyear().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMonthOfYear()
/*      */   {
/* 1586 */     return getChronology().monthOfYear().get(getLocalMillis());
/*      */   }
/*      */   
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
/* 1600 */     return getChronology().weekOfWeekyear().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getDayOfYear()
/*      */   {
/* 1609 */     return getChronology().dayOfYear().get(getLocalMillis());
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
/* 1620 */     return getChronology().dayOfMonth().get(getLocalMillis());
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
/* 1631 */     return getChronology().dayOfWeek().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getHourOfDay()
/*      */   {
/* 1641 */     return getChronology().hourOfDay().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMinuteOfHour()
/*      */   {
/* 1650 */     return getChronology().minuteOfHour().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getSecondOfMinute()
/*      */   {
/* 1659 */     return getChronology().secondOfMinute().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMillisOfSecond()
/*      */   {
/* 1668 */     return getChronology().millisOfSecond().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getMillisOfDay()
/*      */   {
/* 1677 */     return getChronology().millisOfDay().get(getLocalMillis());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime withEra(int paramInt)
/*      */   {
/* 1693 */     return withLocalMillis(getChronology().era().set(getLocalMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime withCenturyOfEra(int paramInt)
/*      */   {
/* 1708 */     return withLocalMillis(getChronology().centuryOfEra().set(getLocalMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime withYearOfEra(int paramInt)
/*      */   {
/* 1723 */     return withLocalMillis(getChronology().yearOfEra().set(getLocalMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime withYearOfCentury(int paramInt)
/*      */   {
/* 1738 */     return withLocalMillis(getChronology().yearOfCentury().set(getLocalMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime withYear(int paramInt)
/*      */   {
/* 1753 */     return withLocalMillis(getChronology().year().set(getLocalMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime withWeekyear(int paramInt)
/*      */   {
/* 1774 */     return withLocalMillis(getChronology().weekyear().set(getLocalMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime withMonthOfYear(int paramInt)
/*      */   {
/* 1789 */     return withLocalMillis(getChronology().monthOfYear().set(getLocalMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime withWeekOfWeekyear(int paramInt)
/*      */   {
/* 1809 */     return withLocalMillis(getChronology().weekOfWeekyear().set(getLocalMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime withDayOfYear(int paramInt)
/*      */   {
/* 1824 */     return withLocalMillis(getChronology().dayOfYear().set(getLocalMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime withDayOfMonth(int paramInt)
/*      */   {
/* 1839 */     return withLocalMillis(getChronology().dayOfMonth().set(getLocalMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime withDayOfWeek(int paramInt)
/*      */   {
/* 1854 */     return withLocalMillis(getChronology().dayOfWeek().set(getLocalMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime withHourOfDay(int paramInt)
/*      */   {
/* 1870 */     return withLocalMillis(getChronology().hourOfDay().set(getLocalMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime withMinuteOfHour(int paramInt)
/*      */   {
/* 1885 */     return withLocalMillis(getChronology().minuteOfHour().set(getLocalMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime withSecondOfMinute(int paramInt)
/*      */   {
/* 1900 */     return withLocalMillis(getChronology().secondOfMinute().set(getLocalMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime withMillisOfSecond(int paramInt)
/*      */   {
/* 1915 */     return withLocalMillis(getChronology().millisOfSecond().set(getLocalMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public LocalDateTime withMillisOfDay(int paramInt)
/*      */   {
/* 1930 */     return withLocalMillis(getChronology().millisOfDay().set(getLocalMillis(), paramInt));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property era()
/*      */   {
/* 1940 */     return new Property(this, getChronology().era());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property centuryOfEra()
/*      */   {
/* 1949 */     return new Property(this, getChronology().centuryOfEra());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property yearOfCentury()
/*      */   {
/* 1958 */     return new Property(this, getChronology().yearOfCentury());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property yearOfEra()
/*      */   {
/* 1967 */     return new Property(this, getChronology().yearOfEra());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property year()
/*      */   {
/* 1976 */     return new Property(this, getChronology().year());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property weekyear()
/*      */   {
/* 1985 */     return new Property(this, getChronology().weekyear());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property monthOfYear()
/*      */   {
/* 1994 */     return new Property(this, getChronology().monthOfYear());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property weekOfWeekyear()
/*      */   {
/* 2003 */     return new Property(this, getChronology().weekOfWeekyear());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property dayOfYear()
/*      */   {
/* 2012 */     return new Property(this, getChronology().dayOfYear());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property dayOfMonth()
/*      */   {
/* 2021 */     return new Property(this, getChronology().dayOfMonth());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property dayOfWeek()
/*      */   {
/* 2030 */     return new Property(this, getChronology().dayOfWeek());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property hourOfDay()
/*      */   {
/* 2040 */     return new Property(this, getChronology().hourOfDay());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property minuteOfHour()
/*      */   {
/* 2049 */     return new Property(this, getChronology().minuteOfHour());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property secondOfMinute()
/*      */   {
/* 2058 */     return new Property(this, getChronology().secondOfMinute());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property millisOfSecond()
/*      */   {
/* 2067 */     return new Property(this, getChronology().millisOfSecond());
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Property millisOfDay()
/*      */   {
/* 2076 */     return new Property(this, getChronology().millisOfDay());
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
/* 2087 */     return ISODateTimeFormat.dateTime().print(this);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public String toString(String paramString)
/*      */   {
/* 2097 */     if (paramString == null) {
/* 2098 */       return toString();
/*      */     }
/* 2100 */     return DateTimeFormat.forPattern(paramString).print(this);
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
/* 2111 */     if (paramString == null) {
/* 2112 */       return toString();
/*      */     }
/* 2114 */     return DateTimeFormat.forPattern(paramString).withLocale(paramLocale).print(this);
/*      */   }
/*      */   
/*      */ 
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
/*      */     private static final long serialVersionUID = -358138762846288L;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private transient LocalDateTime iInstant;
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
/*      */     Property(LocalDateTime paramLocalDateTime, DateTimeField paramDateTimeField)
/*      */     {
/* 2164 */       this.iInstant = paramLocalDateTime;
/* 2165 */       this.iField = paramDateTimeField;
/*      */     }
/*      */     
/*      */ 
/*      */     private void writeObject(ObjectOutputStream paramObjectOutputStream)
/*      */       throws IOException
/*      */     {
/* 2172 */       paramObjectOutputStream.writeObject(this.iInstant);
/* 2173 */       paramObjectOutputStream.writeObject(this.iField.getType());
/*      */     }
/*      */     
/*      */ 
/*      */     private void readObject(ObjectInputStream paramObjectInputStream)
/*      */       throws IOException, ClassNotFoundException
/*      */     {
/* 2180 */       this.iInstant = ((LocalDateTime)paramObjectInputStream.readObject());
/* 2181 */       DateTimeFieldType localDateTimeFieldType = (DateTimeFieldType)paramObjectInputStream.readObject();
/* 2182 */       this.iField = localDateTimeFieldType.getField(this.iInstant.getChronology());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public DateTimeField getField()
/*      */     {
/* 2192 */       return this.iField;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected long getMillis()
/*      */     {
/* 2201 */       return this.iInstant.getLocalMillis();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     protected Chronology getChronology()
/*      */     {
/* 2211 */       return this.iInstant.getChronology();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public LocalDateTime getLocalDateTime()
/*      */     {
/* 2220 */       return this.iInstant;
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
/*      */     public LocalDateTime addToCopy(int paramInt)
/*      */     {
/* 2234 */       return this.iInstant.withLocalMillis(this.iField.add(this.iInstant.getLocalMillis(), paramInt));
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
/*      */     public LocalDateTime addToCopy(long paramLong)
/*      */     {
/* 2247 */       return this.iInstant.withLocalMillis(this.iField.add(this.iInstant.getLocalMillis(), paramLong));
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
/*      */     public LocalDateTime addWrapFieldToCopy(int paramInt)
/*      */     {
/* 2262 */       return this.iInstant.withLocalMillis(this.iField.addWrapField(this.iInstant.getLocalMillis(), paramInt));
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
/*      */     public LocalDateTime setCopy(int paramInt)
/*      */     {
/* 2276 */       return this.iInstant.withLocalMillis(this.iField.set(this.iInstant.getLocalMillis(), paramInt));
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
/*      */     public LocalDateTime setCopy(String paramString, Locale paramLocale)
/*      */     {
/* 2290 */       return this.iInstant.withLocalMillis(this.iField.set(this.iInstant.getLocalMillis(), paramString, paramLocale));
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
/*      */     public LocalDateTime setCopy(String paramString)
/*      */     {
/* 2303 */       return setCopy(paramString, null);
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
/*      */     public LocalDateTime withMaximumValue()
/*      */     {
/* 2322 */       return setCopy(getMaximumValue());
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public LocalDateTime withMinimumValue()
/*      */     {
/* 2334 */       return setCopy(getMinimumValue());
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
/*      */     public LocalDateTime roundFloorCopy()
/*      */     {
/* 2349 */       return this.iInstant.withLocalMillis(this.iField.roundFloor(this.iInstant.getLocalMillis()));
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
/*      */     public LocalDateTime roundCeilingCopy()
/*      */     {
/* 2363 */       return this.iInstant.withLocalMillis(this.iField.roundCeiling(this.iInstant.getLocalMillis()));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public LocalDateTime roundHalfFloorCopy()
/*      */     {
/* 2373 */       return this.iInstant.withLocalMillis(this.iField.roundHalfFloor(this.iInstant.getLocalMillis()));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public LocalDateTime roundHalfCeilingCopy()
/*      */     {
/* 2383 */       return this.iInstant.withLocalMillis(this.iField.roundHalfCeiling(this.iInstant.getLocalMillis()));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public LocalDateTime roundHalfEvenCopy()
/*      */     {
/* 2394 */       return this.iInstant.withLocalMillis(this.iField.roundHalfEven(this.iInstant.getLocalMillis()));
/*      */     }
/*      */   }
/*      */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\LocalDateTime.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */