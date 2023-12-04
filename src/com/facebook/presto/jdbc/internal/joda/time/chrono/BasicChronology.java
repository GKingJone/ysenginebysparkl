/*     */ package com.facebook.presto.jdbc.internal.joda.time.chrono;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.DividedDateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.FieldUtils;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.MillisDurationField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.OffsetDateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.PreciseDateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.PreciseDurationField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.RemainderDateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.ZeroIsMaxDateTimeField;
/*     */ import java.util.Locale;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class BasicChronology
/*     */   extends AssembledChronology
/*     */ {
/*     */   private static final long serialVersionUID = 8283225332206808863L;
/*  76 */   private static final DurationField cMillisField = MillisDurationField.INSTANCE;
/*  77 */   private static final DurationField cSecondsField = new PreciseDurationField(DurationFieldType.seconds(), 1000L);
/*     */   
/*  79 */   private static final DurationField cMinutesField = new PreciseDurationField(DurationFieldType.minutes(), 60000L);
/*     */   
/*  81 */   private static final DurationField cHoursField = new PreciseDurationField(DurationFieldType.hours(), 3600000L);
/*     */   
/*  83 */   private static final DurationField cHalfdaysField = new PreciseDurationField(DurationFieldType.halfdays(), 43200000L);
/*     */   
/*  85 */   private static final DurationField cDaysField = new PreciseDurationField(DurationFieldType.days(), 86400000L);
/*     */   
/*  87 */   private static final DurationField cWeeksField = new PreciseDurationField(DurationFieldType.weeks(), 604800000L);
/*     */   
/*     */ 
/*  90 */   private static final DateTimeField cMillisOfSecondField = new PreciseDateTimeField(DateTimeFieldType.millisOfSecond(), cMillisField, cSecondsField);
/*     */   
/*     */ 
/*  93 */   private static final DateTimeField cMillisOfDayField = new PreciseDateTimeField(DateTimeFieldType.millisOfDay(), cMillisField, cDaysField);
/*     */   
/*     */ 
/*  96 */   private static final DateTimeField cSecondOfMinuteField = new PreciseDateTimeField(DateTimeFieldType.secondOfMinute(), cSecondsField, cMinutesField);
/*     */   
/*     */ 
/*  99 */   private static final DateTimeField cSecondOfDayField = new PreciseDateTimeField(DateTimeFieldType.secondOfDay(), cSecondsField, cDaysField);
/*     */   
/*     */ 
/* 102 */   private static final DateTimeField cMinuteOfHourField = new PreciseDateTimeField(DateTimeFieldType.minuteOfHour(), cMinutesField, cHoursField);
/*     */   
/*     */ 
/* 105 */   private static final DateTimeField cMinuteOfDayField = new PreciseDateTimeField(DateTimeFieldType.minuteOfDay(), cMinutesField, cDaysField);
/*     */   
/*     */ 
/* 108 */   private static final DateTimeField cHourOfDayField = new PreciseDateTimeField(DateTimeFieldType.hourOfDay(), cHoursField, cDaysField);
/*     */   
/*     */ 
/* 111 */   private static final DateTimeField cHourOfHalfdayField = new PreciseDateTimeField(DateTimeFieldType.hourOfHalfday(), cHoursField, cHalfdaysField);
/*     */   
/*     */ 
/* 114 */   private static final DateTimeField cClockhourOfDayField = new ZeroIsMaxDateTimeField(cHourOfDayField, DateTimeFieldType.clockhourOfDay());
/*     */   
/*     */ 
/* 117 */   private static final DateTimeField cClockhourOfHalfdayField = new ZeroIsMaxDateTimeField(cHourOfHalfdayField, DateTimeFieldType.clockhourOfHalfday());
/*     */   
/*     */ 
/* 120 */   private static final DateTimeField cHalfdayOfDayField = new HalfdayField();
/*     */   
/*     */   private static final int CACHE_SIZE = 1024;
/*     */   
/*     */   private static final int CACHE_MASK = 1023;
/*     */   
/* 126 */   private final transient YearInfo[] iYearInfoCache = new YearInfo['Ѐ'];
/*     */   private final int iMinDaysInFirstWeek;
/*     */   
/*     */   BasicChronology(Chronology paramChronology, Object paramObject, int paramInt)
/*     */   {
/* 131 */     super(paramChronology, paramObject);
/*     */     
/* 133 */     if ((paramInt < 1) || (paramInt > 7)) {
/* 134 */       throw new IllegalArgumentException("Invalid min days in first week: " + paramInt);
/*     */     }
/*     */     
/*     */ 
/* 138 */     this.iMinDaysInFirstWeek = paramInt;
/*     */   }
/*     */   
/*     */   public DateTimeZone getZone() {
/*     */     Chronology localChronology;
/* 143 */     if ((localChronology = getBase()) != null) {
/* 144 */       return localChronology.getZone();
/*     */     }
/* 146 */     return DateTimeZone.UTC;
/*     */   }
/*     */   
/*     */   public long getDateTimeMillis(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     throws IllegalArgumentException
/*     */   {
/*     */     Chronology localChronology;
/* 153 */     if ((localChronology = getBase()) != null) {
/* 154 */       return localChronology.getDateTimeMillis(paramInt1, paramInt2, paramInt3, paramInt4);
/*     */     }
/*     */     
/* 157 */     FieldUtils.verifyValueBounds(DateTimeFieldType.millisOfDay(), paramInt4, 0, 86399999);
/*     */     
/* 159 */     return getDateMidnightMillis(paramInt1, paramInt2, paramInt3) + paramInt4;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDateTimeMillis(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
/*     */     throws IllegalArgumentException
/*     */   {
/*     */     Chronology localChronology;
/* 167 */     if ((localChronology = getBase()) != null) {
/* 168 */       return localChronology.getDateTimeMillis(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
/*     */     }
/*     */     
/*     */ 
/* 172 */     FieldUtils.verifyValueBounds(DateTimeFieldType.hourOfDay(), paramInt4, 0, 23);
/* 173 */     FieldUtils.verifyValueBounds(DateTimeFieldType.minuteOfHour(), paramInt5, 0, 59);
/* 174 */     FieldUtils.verifyValueBounds(DateTimeFieldType.secondOfMinute(), paramInt6, 0, 59);
/* 175 */     FieldUtils.verifyValueBounds(DateTimeFieldType.millisOfSecond(), paramInt7, 0, 999);
/*     */     
/* 177 */     return getDateMidnightMillis(paramInt1, paramInt2, paramInt3) + paramInt4 * 3600000 + paramInt5 * 60000 + paramInt6 * 1000 + paramInt7;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMinimumDaysInFirstWeek()
/*     */   {
/* 185 */     return this.iMinDaysInFirstWeek;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 197 */     if (this == paramObject) {
/* 198 */       return true;
/*     */     }
/* 200 */     if ((paramObject != null) && (getClass() == paramObject.getClass())) {
/* 201 */       BasicChronology localBasicChronology = (BasicChronology)paramObject;
/* 202 */       return (getMinimumDaysInFirstWeek() == localBasicChronology.getMinimumDaysInFirstWeek()) && (getZone().equals(localBasicChronology.getZone()));
/*     */     }
/*     */     
/* 205 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 215 */     return getClass().getName().hashCode() * 11 + getZone().hashCode() + getMinimumDaysInFirstWeek();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 226 */     StringBuilder localStringBuilder = new StringBuilder(60);
/* 227 */     String str = getClass().getName();
/* 228 */     int i = str.lastIndexOf('.');
/* 229 */     if (i >= 0) {
/* 230 */       str = str.substring(i + 1);
/*     */     }
/* 232 */     localStringBuilder.append(str);
/* 233 */     localStringBuilder.append('[');
/* 234 */     DateTimeZone localDateTimeZone = getZone();
/* 235 */     if (localDateTimeZone != null) {
/* 236 */       localStringBuilder.append(localDateTimeZone.getID());
/*     */     }
/* 238 */     if (getMinimumDaysInFirstWeek() != 4) {
/* 239 */       localStringBuilder.append(",mdfw=");
/* 240 */       localStringBuilder.append(getMinimumDaysInFirstWeek());
/*     */     }
/* 242 */     localStringBuilder.append(']');
/* 243 */     return localStringBuilder.toString();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void assemble(AssembledChronology.Fields paramFields)
/*     */   {
/* 250 */     paramFields.millis = cMillisField;
/* 251 */     paramFields.seconds = cSecondsField;
/* 252 */     paramFields.minutes = cMinutesField;
/* 253 */     paramFields.hours = cHoursField;
/* 254 */     paramFields.halfdays = cHalfdaysField;
/* 255 */     paramFields.days = cDaysField;
/* 256 */     paramFields.weeks = cWeeksField;
/*     */     
/* 258 */     paramFields.millisOfSecond = cMillisOfSecondField;
/* 259 */     paramFields.millisOfDay = cMillisOfDayField;
/* 260 */     paramFields.secondOfMinute = cSecondOfMinuteField;
/* 261 */     paramFields.secondOfDay = cSecondOfDayField;
/* 262 */     paramFields.minuteOfHour = cMinuteOfHourField;
/* 263 */     paramFields.minuteOfDay = cMinuteOfDayField;
/* 264 */     paramFields.hourOfDay = cHourOfDayField;
/* 265 */     paramFields.hourOfHalfday = cHourOfHalfdayField;
/* 266 */     paramFields.clockhourOfDay = cClockhourOfDayField;
/* 267 */     paramFields.clockhourOfHalfday = cClockhourOfHalfdayField;
/* 268 */     paramFields.halfdayOfDay = cHalfdayOfDayField;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 273 */     paramFields.year = new BasicYearDateTimeField(this);
/* 274 */     paramFields.yearOfEra = new GJYearOfEraDateTimeField(paramFields.year, this);
/*     */     
/*     */ 
/* 277 */     Object localObject = new OffsetDateTimeField(paramFields.yearOfEra, 99);
/*     */     
/* 279 */     paramFields.centuryOfEra = new DividedDateTimeField((DateTimeField)localObject, DateTimeFieldType.centuryOfEra(), 100);
/*     */     
/* 281 */     paramFields.centuries = paramFields.centuryOfEra.getDurationField();
/*     */     
/* 283 */     localObject = new RemainderDateTimeField((DividedDateTimeField)paramFields.centuryOfEra);
/*     */     
/* 285 */     paramFields.yearOfCentury = new OffsetDateTimeField((DateTimeField)localObject, DateTimeFieldType.yearOfCentury(), 1);
/*     */     
/*     */ 
/* 288 */     paramFields.era = new GJEraDateTimeField(this);
/* 289 */     paramFields.dayOfWeek = new GJDayOfWeekDateTimeField(this, paramFields.days);
/* 290 */     paramFields.dayOfMonth = new BasicDayOfMonthDateTimeField(this, paramFields.days);
/* 291 */     paramFields.dayOfYear = new BasicDayOfYearDateTimeField(this, paramFields.days);
/* 292 */     paramFields.monthOfYear = new GJMonthOfYearDateTimeField(this);
/* 293 */     paramFields.weekyear = new BasicWeekyearDateTimeField(this);
/* 294 */     paramFields.weekOfWeekyear = new BasicWeekOfWeekyearDateTimeField(this, paramFields.weeks);
/*     */     
/* 296 */     localObject = new RemainderDateTimeField(paramFields.weekyear, paramFields.centuries, DateTimeFieldType.weekyearOfCentury(), 100);
/*     */     
/* 298 */     paramFields.weekyearOfCentury = new OffsetDateTimeField((DateTimeField)localObject, DateTimeFieldType.weekyearOfCentury(), 1);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 303 */     paramFields.years = paramFields.year.getDurationField();
/* 304 */     paramFields.months = paramFields.monthOfYear.getDurationField();
/* 305 */     paramFields.weekyears = paramFields.weekyear.getDurationField();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   int getDaysInYearMax()
/*     */   {
/* 315 */     return 366;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   int getDaysInYear(int paramInt)
/*     */   {
/* 325 */     return isLeapYear(paramInt) ? 366 : 365;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   int getWeeksInYear(int paramInt)
/*     */   {
/* 335 */     long l1 = getFirstWeekOfYearMillis(paramInt);
/* 336 */     long l2 = getFirstWeekOfYearMillis(paramInt + 1);
/* 337 */     return (int)((l2 - l1) / 604800000L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   long getFirstWeekOfYearMillis(int paramInt)
/*     */   {
/* 347 */     long l = getYearMillis(paramInt);
/* 348 */     int i = getDayOfWeek(l);
/*     */     
/* 350 */     if (i > 8 - this.iMinDaysInFirstWeek)
/*     */     {
/* 352 */       return l + (8 - i) * 86400000L;
/*     */     }
/*     */     
/*     */ 
/* 356 */     return l - (i - 1) * 86400000L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   long getYearMillis(int paramInt)
/*     */   {
/* 368 */     return getYearInfo(paramInt).iFirstDayMillis;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   long getYearMonthMillis(int paramInt1, int paramInt2)
/*     */   {
/* 379 */     long l = getYearMillis(paramInt1);
/* 380 */     l += getTotalMillisByYearMonth(paramInt1, paramInt2);
/* 381 */     return l;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   long getYearMonthDayMillis(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 393 */     long l = getYearMillis(paramInt1);
/* 394 */     l += getTotalMillisByYearMonth(paramInt1, paramInt2);
/* 395 */     return l + (paramInt3 - 1) * 86400000L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   int getYear(long paramLong)
/*     */   {
/* 407 */     long l1 = getAverageMillisPerYearDividedByTwo();
/* 408 */     long l2 = (paramLong >> 1) + getApproxMillisAtEpochDividedByTwo();
/* 409 */     if (l2 < 0L) {
/* 410 */       l2 = l2 - l1 + 1L;
/*     */     }
/* 412 */     int i = (int)(l2 / l1);
/*     */     
/* 414 */     long l3 = getYearMillis(i);
/* 415 */     long l4 = paramLong - l3;
/*     */     
/* 417 */     if (l4 < 0L) {
/* 418 */       i--;
/* 419 */     } else if (l4 >= 31536000000L)
/*     */     {
/*     */       long l5;
/* 422 */       if (isLeapYear(i)) {
/* 423 */         l5 = 31622400000L;
/*     */       } else {
/* 425 */         l5 = 31536000000L;
/*     */       }
/*     */       
/* 428 */       l3 += l5;
/*     */       
/* 430 */       if (l3 <= paramLong)
/*     */       {
/* 432 */         i++;
/*     */       }
/*     */     }
/*     */     
/* 436 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   int getMonthOfYear(long paramLong)
/*     */   {
/* 443 */     return getMonthOfYear(paramLong, getYear(paramLong));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract int getMonthOfYear(long paramLong, int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   int getDayOfMonth(long paramLong)
/*     */   {
/* 456 */     int i = getYear(paramLong);
/* 457 */     int j = getMonthOfYear(paramLong, i);
/* 458 */     return getDayOfMonth(paramLong, i, j);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   int getDayOfMonth(long paramLong, int paramInt)
/*     */   {
/* 466 */     int i = getMonthOfYear(paramLong, paramInt);
/* 467 */     return getDayOfMonth(paramLong, paramInt, i);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   int getDayOfMonth(long paramLong, int paramInt1, int paramInt2)
/*     */   {
/* 476 */     long l = getYearMillis(paramInt1);
/* 477 */     l += getTotalMillisByYearMonth(paramInt1, paramInt2);
/* 478 */     return (int)((paramLong - l) / 86400000L) + 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   int getDayOfYear(long paramLong)
/*     */   {
/* 485 */     return getDayOfYear(paramLong, getYear(paramLong));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   int getDayOfYear(long paramLong, int paramInt)
/*     */   {
/* 493 */     long l = getYearMillis(paramInt);
/* 494 */     return (int)((paramLong - l) / 86400000L) + 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   int getWeekyear(long paramLong)
/*     */   {
/* 501 */     int i = getYear(paramLong);
/* 502 */     int j = getWeekOfWeekyear(paramLong, i);
/* 503 */     if (j == 1)
/* 504 */       return getYear(paramLong + 604800000L);
/* 505 */     if (j > 51) {
/* 506 */       return getYear(paramLong - 1209600000L);
/*     */     }
/* 508 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   int getWeekOfWeekyear(long paramLong)
/*     */   {
/* 516 */     return getWeekOfWeekyear(paramLong, getYear(paramLong));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   int getWeekOfWeekyear(long paramLong, int paramInt)
/*     */   {
/* 524 */     long l1 = getFirstWeekOfYearMillis(paramInt);
/* 525 */     if (paramLong < l1) {
/* 526 */       return getWeeksInYear(paramInt - 1);
/*     */     }
/* 528 */     long l2 = getFirstWeekOfYearMillis(paramInt + 1);
/* 529 */     if (paramLong >= l2) {
/* 530 */       return 1;
/*     */     }
/* 532 */     return (int)((paramLong - l1) / 604800000L) + 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   int getDayOfWeek(long paramLong)
/*     */   {
/*     */     long l;
/*     */     
/*     */ 
/* 542 */     if (paramLong >= 0L) {
/* 543 */       l = paramLong / 86400000L;
/*     */     } else {
/* 545 */       l = (paramLong - 86399999L) / 86400000L;
/*     */       
/* 547 */       if (l < -3L) {
/* 548 */         return 7 + (int)((l + 4L) % 7L);
/*     */       }
/*     */     }
/*     */     
/* 552 */     return 1 + (int)((l + 3L) % 7L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   int getMillisOfDay(long paramLong)
/*     */   {
/* 559 */     if (paramLong >= 0L) {
/* 560 */       return (int)(paramLong % 86400000L);
/*     */     }
/* 562 */     return 86399999 + (int)((paramLong + 1L) % 86400000L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   int getDaysInMonthMax()
/*     */   {
/* 573 */     return 31;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   int getDaysInMonthMax(long paramLong)
/*     */   {
/* 583 */     int i = getYear(paramLong);
/* 584 */     int j = getMonthOfYear(paramLong, i);
/* 585 */     return getDaysInYearMonth(i, j);
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
/*     */   int getDaysInMonthMaxForSet(long paramLong, int paramInt)
/*     */   {
/* 598 */     return getDaysInMonthMax(paramLong);
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
/*     */   long getDateMidnightMillis(int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/* 611 */     FieldUtils.verifyValueBounds(DateTimeFieldType.year(), paramInt1, getMinYear(), getMaxYear());
/* 612 */     FieldUtils.verifyValueBounds(DateTimeFieldType.monthOfYear(), paramInt2, 1, getMaxMonth(paramInt1));
/* 613 */     FieldUtils.verifyValueBounds(DateTimeFieldType.dayOfMonth(), paramInt3, 1, getDaysInYearMonth(paramInt1, paramInt2));
/* 614 */     return getYearMonthDayMillis(paramInt1, paramInt2, paramInt3);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract long getYearDifference(long paramLong1, long paramLong2);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract boolean isLeapYear(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   boolean isLeapDay(long paramLong)
/*     */   {
/* 641 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract int getDaysInYearMonth(int paramInt1, int paramInt2);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract int getDaysInMonthMax(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract long getTotalMillisByYearMonth(int paramInt1, int paramInt2);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract long calculateFirstDayOfYearMillis(int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract int getMinYear();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract int getMaxYear();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   int getMaxMonth(int paramInt)
/*     */   {
/* 700 */     return getMaxMonth();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   int getMaxMonth()
/*     */   {
/* 709 */     return 12;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract long getAverageMillisPerYear();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract long getAverageMillisPerYearDividedByTwo();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract long getAverageMillisPerMonth();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract long getApproxMillisAtEpochDividedByTwo();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   abstract long setYear(long paramLong, int paramInt);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private YearInfo getYearInfo(int paramInt)
/*     */   {
/* 756 */     YearInfo localYearInfo = this.iYearInfoCache[(paramInt & 0x3FF)];
/* 757 */     if ((localYearInfo == null) || (localYearInfo.iYear != paramInt)) {
/* 758 */       localYearInfo = new YearInfo(paramInt, calculateFirstDayOfYearMillis(paramInt));
/* 759 */       this.iYearInfoCache[(paramInt & 0x3FF)] = localYearInfo;
/*     */     }
/* 761 */     return localYearInfo;
/*     */   }
/*     */   
/*     */   private static class HalfdayField extends PreciseDateTimeField
/*     */   {
/*     */     private static final long serialVersionUID = 581601443656929254L;
/*     */     
/*     */     HalfdayField() {
/* 769 */       super(BasicChronology.cHalfdaysField, BasicChronology.cDaysField);
/*     */     }
/*     */     
/*     */     public String getAsText(int paramInt, Locale paramLocale) {
/* 773 */       return GJLocaleSymbols.forLocale(paramLocale).halfdayValueToText(paramInt);
/*     */     }
/*     */     
/*     */     public long set(long paramLong, String paramString, Locale paramLocale) {
/* 777 */       return set(paramLong, GJLocaleSymbols.forLocale(paramLocale).halfdayTextToValue(paramString));
/*     */     }
/*     */     
/*     */     public int getMaximumTextLength(Locale paramLocale) {
/* 781 */       return GJLocaleSymbols.forLocale(paramLocale).getHalfdayMaxTextLength();
/*     */     }
/*     */   }
/*     */   
/*     */   private static class YearInfo {
/*     */     public final int iYear;
/*     */     public final long iFirstDayMillis;
/*     */     
/*     */     YearInfo(int paramInt, long paramLong) {
/* 790 */       this.iYear = paramInt;
/* 791 */       this.iFirstDayMillis = paramLong;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\BasicChronology.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */