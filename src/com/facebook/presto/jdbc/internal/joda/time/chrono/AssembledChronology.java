/*     */ package com.facebook.presto.jdbc.internal.joda.time.chrono;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
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
/*     */ public abstract class AssembledChronology
/*     */   extends BaseChronology
/*     */ {
/*     */   private static final long serialVersionUID = -6728465968995518215L;
/*     */   private final Chronology iBase;
/*     */   private final Object iParam;
/*     */   private transient DurationField iMillis;
/*     */   private transient DurationField iSeconds;
/*     */   private transient DurationField iMinutes;
/*     */   private transient DurationField iHours;
/*     */   private transient DurationField iHalfdays;
/*     */   private transient DurationField iDays;
/*     */   private transient DurationField iWeeks;
/*     */   private transient DurationField iWeekyears;
/*     */   private transient DurationField iMonths;
/*     */   private transient DurationField iYears;
/*     */   private transient DurationField iCenturies;
/*     */   private transient DurationField iEras;
/*     */   private transient DateTimeField iMillisOfSecond;
/*     */   private transient DateTimeField iMillisOfDay;
/*     */   private transient DateTimeField iSecondOfMinute;
/*     */   private transient DateTimeField iSecondOfDay;
/*     */   private transient DateTimeField iMinuteOfHour;
/*     */   private transient DateTimeField iMinuteOfDay;
/*     */   private transient DateTimeField iHourOfDay;
/*     */   private transient DateTimeField iClockhourOfDay;
/*     */   private transient DateTimeField iHourOfHalfday;
/*     */   private transient DateTimeField iClockhourOfHalfday;
/*     */   private transient DateTimeField iHalfdayOfDay;
/*     */   private transient DateTimeField iDayOfWeek;
/*     */   private transient DateTimeField iDayOfMonth;
/*     */   private transient DateTimeField iDayOfYear;
/*     */   private transient DateTimeField iWeekOfWeekyear;
/*     */   private transient DateTimeField iWeekyear;
/*     */   private transient DateTimeField iWeekyearOfCentury;
/*     */   private transient DateTimeField iMonthOfYear;
/*     */   private transient DateTimeField iYear;
/*     */   private transient DateTimeField iYearOfEra;
/*     */   private transient DateTimeField iYearOfCentury;
/*     */   private transient DateTimeField iCenturyOfEra;
/*     */   private transient DateTimeField iEra;
/*     */   private transient int iBaseFlags;
/*     */   
/*     */   protected AssembledChronology(Chronology paramChronology, Object paramObject)
/*     */   {
/* 100 */     this.iBase = paramChronology;
/* 101 */     this.iParam = paramObject;
/* 102 */     setFields();
/*     */   }
/*     */   
/*     */   public DateTimeZone getZone() {
/*     */     Chronology localChronology;
/* 107 */     if ((localChronology = this.iBase) != null) {
/* 108 */       return localChronology.getZone();
/*     */     }
/* 110 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDateTimeMillis(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     throws IllegalArgumentException
/*     */   {
/*     */     Chronology localChronology;
/* 118 */     if (((localChronology = this.iBase) != null) && ((this.iBaseFlags & 0x6) == 6))
/*     */     {
/* 120 */       return localChronology.getDateTimeMillis(paramInt1, paramInt2, paramInt3, paramInt4);
/*     */     }
/* 122 */     return super.getDateTimeMillis(paramInt1, paramInt2, paramInt3, paramInt4);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getDateTimeMillis(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
/*     */     throws IllegalArgumentException
/*     */   {
/*     */     Chronology localChronology;
/*     */     
/* 131 */     if (((localChronology = this.iBase) != null) && ((this.iBaseFlags & 0x5) == 5))
/*     */     {
/* 133 */       return localChronology.getDateTimeMillis(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
/*     */     }
/*     */     
/* 136 */     return super.getDateTimeMillis(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long getDateTimeMillis(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     throws IllegalArgumentException
/*     */   {
/*     */     Chronology localChronology;
/*     */     
/* 146 */     if (((localChronology = this.iBase) != null) && ((this.iBaseFlags & 0x1) == 1))
/*     */     {
/* 148 */       return localChronology.getDateTimeMillis(paramLong, paramInt1, paramInt2, paramInt3, paramInt4);
/*     */     }
/*     */     
/* 151 */     return super.getDateTimeMillis(paramLong, paramInt1, paramInt2, paramInt3, paramInt4);
/*     */   }
/*     */   
/*     */   public final DurationField millis()
/*     */   {
/* 156 */     return this.iMillis;
/*     */   }
/*     */   
/*     */   public final DateTimeField millisOfSecond() {
/* 160 */     return this.iMillisOfSecond;
/*     */   }
/*     */   
/*     */   public final DateTimeField millisOfDay() {
/* 164 */     return this.iMillisOfDay;
/*     */   }
/*     */   
/*     */   public final DurationField seconds() {
/* 168 */     return this.iSeconds;
/*     */   }
/*     */   
/*     */   public final DateTimeField secondOfMinute() {
/* 172 */     return this.iSecondOfMinute;
/*     */   }
/*     */   
/*     */   public final DateTimeField secondOfDay() {
/* 176 */     return this.iSecondOfDay;
/*     */   }
/*     */   
/*     */   public final DurationField minutes() {
/* 180 */     return this.iMinutes;
/*     */   }
/*     */   
/*     */   public final DateTimeField minuteOfHour() {
/* 184 */     return this.iMinuteOfHour;
/*     */   }
/*     */   
/*     */   public final DateTimeField minuteOfDay() {
/* 188 */     return this.iMinuteOfDay;
/*     */   }
/*     */   
/*     */   public final DurationField hours() {
/* 192 */     return this.iHours;
/*     */   }
/*     */   
/*     */   public final DateTimeField hourOfDay() {
/* 196 */     return this.iHourOfDay;
/*     */   }
/*     */   
/*     */   public final DateTimeField clockhourOfDay() {
/* 200 */     return this.iClockhourOfDay;
/*     */   }
/*     */   
/*     */   public final DurationField halfdays() {
/* 204 */     return this.iHalfdays;
/*     */   }
/*     */   
/*     */   public final DateTimeField hourOfHalfday() {
/* 208 */     return this.iHourOfHalfday;
/*     */   }
/*     */   
/*     */   public final DateTimeField clockhourOfHalfday() {
/* 212 */     return this.iClockhourOfHalfday;
/*     */   }
/*     */   
/*     */   public final DateTimeField halfdayOfDay() {
/* 216 */     return this.iHalfdayOfDay;
/*     */   }
/*     */   
/*     */   public final DurationField days() {
/* 220 */     return this.iDays;
/*     */   }
/*     */   
/*     */   public final DateTimeField dayOfWeek() {
/* 224 */     return this.iDayOfWeek;
/*     */   }
/*     */   
/*     */   public final DateTimeField dayOfMonth() {
/* 228 */     return this.iDayOfMonth;
/*     */   }
/*     */   
/*     */   public final DateTimeField dayOfYear() {
/* 232 */     return this.iDayOfYear;
/*     */   }
/*     */   
/*     */   public final DurationField weeks() {
/* 236 */     return this.iWeeks;
/*     */   }
/*     */   
/*     */   public final DateTimeField weekOfWeekyear() {
/* 240 */     return this.iWeekOfWeekyear;
/*     */   }
/*     */   
/*     */   public final DurationField weekyears() {
/* 244 */     return this.iWeekyears;
/*     */   }
/*     */   
/*     */   public final DateTimeField weekyear() {
/* 248 */     return this.iWeekyear;
/*     */   }
/*     */   
/*     */   public final DateTimeField weekyearOfCentury() {
/* 252 */     return this.iWeekyearOfCentury;
/*     */   }
/*     */   
/*     */   public final DurationField months() {
/* 256 */     return this.iMonths;
/*     */   }
/*     */   
/*     */   public final DateTimeField monthOfYear() {
/* 260 */     return this.iMonthOfYear;
/*     */   }
/*     */   
/*     */   public final DurationField years() {
/* 264 */     return this.iYears;
/*     */   }
/*     */   
/*     */   public final DateTimeField year() {
/* 268 */     return this.iYear;
/*     */   }
/*     */   
/*     */   public final DateTimeField yearOfEra() {
/* 272 */     return this.iYearOfEra;
/*     */   }
/*     */   
/*     */   public final DateTimeField yearOfCentury() {
/* 276 */     return this.iYearOfCentury;
/*     */   }
/*     */   
/*     */   public final DurationField centuries() {
/* 280 */     return this.iCenturies;
/*     */   }
/*     */   
/*     */   public final DateTimeField centuryOfEra() {
/* 284 */     return this.iCenturyOfEra;
/*     */   }
/*     */   
/*     */   public final DurationField eras() {
/* 288 */     return this.iEras;
/*     */   }
/*     */   
/*     */   public final DateTimeField era() {
/* 292 */     return this.iEra;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected abstract void assemble(Fields paramFields);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected final Chronology getBase()
/*     */   {
/* 308 */     return this.iBase;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected final Object getParam()
/*     */   {
/* 315 */     return this.iParam;
/*     */   }
/*     */   
/*     */   private void setFields() {
/* 319 */     Fields localFields = new Fields();
/* 320 */     if (this.iBase != null) {
/* 321 */       localFields.copyFieldsFrom(this.iBase);
/*     */     }
/* 323 */     assemble(localFields);
/*     */     
/*     */     Object localObject;
/*     */     
/* 327 */     this.iMillis = ((localObject = localFields.millis) != null ? localObject : super.millis());
/* 328 */     this.iSeconds = ((localObject = localFields.seconds) != null ? localObject : super.seconds());
/* 329 */     this.iMinutes = ((localObject = localFields.minutes) != null ? localObject : super.minutes());
/* 330 */     this.iHours = ((localObject = localFields.hours) != null ? localObject : super.hours());
/* 331 */     this.iHalfdays = ((localObject = localFields.halfdays) != null ? localObject : super.halfdays());
/* 332 */     this.iDays = ((localObject = localFields.days) != null ? localObject : super.days());
/* 333 */     this.iWeeks = ((localObject = localFields.weeks) != null ? localObject : super.weeks());
/* 334 */     this.iWeekyears = ((localObject = localFields.weekyears) != null ? localObject : super.weekyears());
/* 335 */     this.iMonths = ((localObject = localFields.months) != null ? localObject : super.months());
/* 336 */     this.iYears = ((localObject = localFields.years) != null ? localObject : super.years());
/* 337 */     this.iCenturies = ((localObject = localFields.centuries) != null ? localObject : super.centuries());
/* 338 */     this.iEras = ((localObject = localFields.eras) != null ? localObject : super.eras());
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 343 */     this.iMillisOfSecond = ((localObject = localFields.millisOfSecond) != null ? localObject : super.millisOfSecond());
/* 344 */     this.iMillisOfDay = ((localObject = localFields.millisOfDay) != null ? localObject : super.millisOfDay());
/* 345 */     this.iSecondOfMinute = ((localObject = localFields.secondOfMinute) != null ? localObject : super.secondOfMinute());
/* 346 */     this.iSecondOfDay = ((localObject = localFields.secondOfDay) != null ? localObject : super.secondOfDay());
/* 347 */     this.iMinuteOfHour = ((localObject = localFields.minuteOfHour) != null ? localObject : super.minuteOfHour());
/* 348 */     this.iMinuteOfDay = ((localObject = localFields.minuteOfDay) != null ? localObject : super.minuteOfDay());
/* 349 */     this.iHourOfDay = ((localObject = localFields.hourOfDay) != null ? localObject : super.hourOfDay());
/* 350 */     this.iClockhourOfDay = ((localObject = localFields.clockhourOfDay) != null ? localObject : super.clockhourOfDay());
/* 351 */     this.iHourOfHalfday = ((localObject = localFields.hourOfHalfday) != null ? localObject : super.hourOfHalfday());
/* 352 */     this.iClockhourOfHalfday = ((localObject = localFields.clockhourOfHalfday) != null ? localObject : super.clockhourOfHalfday());
/* 353 */     this.iHalfdayOfDay = ((localObject = localFields.halfdayOfDay) != null ? localObject : super.halfdayOfDay());
/* 354 */     this.iDayOfWeek = ((localObject = localFields.dayOfWeek) != null ? localObject : super.dayOfWeek());
/* 355 */     this.iDayOfMonth = ((localObject = localFields.dayOfMonth) != null ? localObject : super.dayOfMonth());
/* 356 */     this.iDayOfYear = ((localObject = localFields.dayOfYear) != null ? localObject : super.dayOfYear());
/* 357 */     this.iWeekOfWeekyear = ((localObject = localFields.weekOfWeekyear) != null ? localObject : super.weekOfWeekyear());
/* 358 */     this.iWeekyear = ((localObject = localFields.weekyear) != null ? localObject : super.weekyear());
/* 359 */     this.iWeekyearOfCentury = ((localObject = localFields.weekyearOfCentury) != null ? localObject : super.weekyearOfCentury());
/* 360 */     this.iMonthOfYear = ((localObject = localFields.monthOfYear) != null ? localObject : super.monthOfYear());
/* 361 */     this.iYear = ((localObject = localFields.year) != null ? localObject : super.year());
/* 362 */     this.iYearOfEra = ((localObject = localFields.yearOfEra) != null ? localObject : super.yearOfEra());
/* 363 */     this.iYearOfCentury = ((localObject = localFields.yearOfCentury) != null ? localObject : super.yearOfCentury());
/* 364 */     this.iCenturyOfEra = ((localObject = localFields.centuryOfEra) != null ? localObject : super.centuryOfEra());
/* 365 */     this.iEra = ((localObject = localFields.era) != null ? localObject : super.era());
/*     */     
/*     */     int i;
/*     */     
/* 369 */     if (this.iBase == null) {
/* 370 */       i = 0;
/*     */     } else {
/* 372 */       i = ((this.iHourOfDay == this.iBase.hourOfDay()) && (this.iMinuteOfHour == this.iBase.minuteOfHour()) && (this.iSecondOfMinute == this.iBase.secondOfMinute()) && (this.iMillisOfSecond == this.iBase.millisOfSecond()) ? 1 : 0) | (this.iMillisOfDay == this.iBase.millisOfDay() ? 2 : 0) | ((this.iYear == this.iBase.year()) && (this.iMonthOfYear == this.iBase.monthOfYear()) && (this.iDayOfMonth == this.iBase.dayOfMonth()) ? 4 : 0);
/*     */     }
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
/* 385 */     this.iBaseFlags = i;
/*     */   }
/*     */   
/*     */   private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
/* 389 */     paramObjectInputStream.defaultReadObject();
/* 390 */     setFields();
/*     */   }
/*     */   
/*     */ 
/*     */   public static final class Fields
/*     */   {
/*     */     public DurationField millis;
/*     */     
/*     */     public DurationField seconds;
/*     */     
/*     */     public DurationField minutes;
/*     */     
/*     */     public DurationField hours;
/*     */     
/*     */     public DurationField halfdays;
/*     */     
/*     */     public DurationField days;
/*     */     
/*     */     public DurationField weeks;
/*     */     
/*     */     public DurationField weekyears;
/*     */     
/*     */     public DurationField months;
/*     */     
/*     */     public DurationField years;
/*     */     
/*     */     public DurationField centuries;
/*     */     public DurationField eras;
/*     */     public DateTimeField millisOfSecond;
/*     */     public DateTimeField millisOfDay;
/*     */     public DateTimeField secondOfMinute;
/*     */     public DateTimeField secondOfDay;
/*     */     public DateTimeField minuteOfHour;
/*     */     public DateTimeField minuteOfDay;
/*     */     public DateTimeField hourOfDay;
/*     */     public DateTimeField clockhourOfDay;
/*     */     public DateTimeField hourOfHalfday;
/*     */     public DateTimeField clockhourOfHalfday;
/*     */     public DateTimeField halfdayOfDay;
/*     */     public DateTimeField dayOfWeek;
/*     */     public DateTimeField dayOfMonth;
/*     */     public DateTimeField dayOfYear;
/*     */     public DateTimeField weekOfWeekyear;
/*     */     public DateTimeField weekyear;
/*     */     public DateTimeField weekyearOfCentury;
/*     */     public DateTimeField monthOfYear;
/*     */     public DateTimeField year;
/*     */     public DateTimeField yearOfEra;
/*     */     public DateTimeField yearOfCentury;
/*     */     public DateTimeField centuryOfEra;
/*     */     public DateTimeField era;
/*     */     
/*     */     public void copyFieldsFrom(Chronology paramChronology)
/*     */     {
/*     */       Object localObject;
/* 445 */       if (isSupported(localObject = paramChronology.millis())) {
/* 446 */         this.millis = ((DurationField)localObject);
/*     */       }
/* 448 */       if (isSupported(localObject = paramChronology.seconds())) {
/* 449 */         this.seconds = ((DurationField)localObject);
/*     */       }
/* 451 */       if (isSupported(localObject = paramChronology.minutes())) {
/* 452 */         this.minutes = ((DurationField)localObject);
/*     */       }
/* 454 */       if (isSupported(localObject = paramChronology.hours())) {
/* 455 */         this.hours = ((DurationField)localObject);
/*     */       }
/* 457 */       if (isSupported(localObject = paramChronology.halfdays())) {
/* 458 */         this.halfdays = ((DurationField)localObject);
/*     */       }
/* 460 */       if (isSupported(localObject = paramChronology.days())) {
/* 461 */         this.days = ((DurationField)localObject);
/*     */       }
/* 463 */       if (isSupported(localObject = paramChronology.weeks())) {
/* 464 */         this.weeks = ((DurationField)localObject);
/*     */       }
/* 466 */       if (isSupported(localObject = paramChronology.weekyears())) {
/* 467 */         this.weekyears = ((DurationField)localObject);
/*     */       }
/* 469 */       if (isSupported(localObject = paramChronology.months())) {
/* 470 */         this.months = ((DurationField)localObject);
/*     */       }
/* 472 */       if (isSupported(localObject = paramChronology.years())) {
/* 473 */         this.years = ((DurationField)localObject);
/*     */       }
/* 475 */       if (isSupported(localObject = paramChronology.centuries())) {
/* 476 */         this.centuries = ((DurationField)localObject);
/*     */       }
/* 478 */       if (isSupported(localObject = paramChronology.eras())) {
/* 479 */         this.eras = ((DurationField)localObject);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 485 */       if (isSupported(localObject = paramChronology.millisOfSecond())) {
/* 486 */         this.millisOfSecond = ((DateTimeField)localObject);
/*     */       }
/* 488 */       if (isSupported(localObject = paramChronology.millisOfDay())) {
/* 489 */         this.millisOfDay = ((DateTimeField)localObject);
/*     */       }
/* 491 */       if (isSupported(localObject = paramChronology.secondOfMinute())) {
/* 492 */         this.secondOfMinute = ((DateTimeField)localObject);
/*     */       }
/* 494 */       if (isSupported(localObject = paramChronology.secondOfDay())) {
/* 495 */         this.secondOfDay = ((DateTimeField)localObject);
/*     */       }
/* 497 */       if (isSupported(localObject = paramChronology.minuteOfHour())) {
/* 498 */         this.minuteOfHour = ((DateTimeField)localObject);
/*     */       }
/* 500 */       if (isSupported(localObject = paramChronology.minuteOfDay())) {
/* 501 */         this.minuteOfDay = ((DateTimeField)localObject);
/*     */       }
/* 503 */       if (isSupported(localObject = paramChronology.hourOfDay())) {
/* 504 */         this.hourOfDay = ((DateTimeField)localObject);
/*     */       }
/* 506 */       if (isSupported(localObject = paramChronology.clockhourOfDay())) {
/* 507 */         this.clockhourOfDay = ((DateTimeField)localObject);
/*     */       }
/* 509 */       if (isSupported(localObject = paramChronology.hourOfHalfday())) {
/* 510 */         this.hourOfHalfday = ((DateTimeField)localObject);
/*     */       }
/* 512 */       if (isSupported(localObject = paramChronology.clockhourOfHalfday())) {
/* 513 */         this.clockhourOfHalfday = ((DateTimeField)localObject);
/*     */       }
/* 515 */       if (isSupported(localObject = paramChronology.halfdayOfDay())) {
/* 516 */         this.halfdayOfDay = ((DateTimeField)localObject);
/*     */       }
/* 518 */       if (isSupported(localObject = paramChronology.dayOfWeek())) {
/* 519 */         this.dayOfWeek = ((DateTimeField)localObject);
/*     */       }
/* 521 */       if (isSupported(localObject = paramChronology.dayOfMonth())) {
/* 522 */         this.dayOfMonth = ((DateTimeField)localObject);
/*     */       }
/* 524 */       if (isSupported(localObject = paramChronology.dayOfYear())) {
/* 525 */         this.dayOfYear = ((DateTimeField)localObject);
/*     */       }
/* 527 */       if (isSupported(localObject = paramChronology.weekOfWeekyear())) {
/* 528 */         this.weekOfWeekyear = ((DateTimeField)localObject);
/*     */       }
/* 530 */       if (isSupported(localObject = paramChronology.weekyear())) {
/* 531 */         this.weekyear = ((DateTimeField)localObject);
/*     */       }
/* 533 */       if (isSupported(localObject = paramChronology.weekyearOfCentury())) {
/* 534 */         this.weekyearOfCentury = ((DateTimeField)localObject);
/*     */       }
/* 536 */       if (isSupported(localObject = paramChronology.monthOfYear())) {
/* 537 */         this.monthOfYear = ((DateTimeField)localObject);
/*     */       }
/* 539 */       if (isSupported(localObject = paramChronology.year())) {
/* 540 */         this.year = ((DateTimeField)localObject);
/*     */       }
/* 542 */       if (isSupported(localObject = paramChronology.yearOfEra())) {
/* 543 */         this.yearOfEra = ((DateTimeField)localObject);
/*     */       }
/* 545 */       if (isSupported(localObject = paramChronology.yearOfCentury())) {
/* 546 */         this.yearOfCentury = ((DateTimeField)localObject);
/*     */       }
/* 548 */       if (isSupported(localObject = paramChronology.centuryOfEra())) {
/* 549 */         this.centuryOfEra = ((DateTimeField)localObject);
/*     */       }
/* 551 */       if (isSupported(localObject = paramChronology.era())) {
/* 552 */         this.era = ((DateTimeField)localObject);
/*     */       }
/*     */     }
/*     */     
/*     */     private static boolean isSupported(DurationField paramDurationField)
/*     */     {
/* 558 */       return paramDurationField == null ? false : paramDurationField.isSupported();
/*     */     }
/*     */     
/*     */     private static boolean isSupported(DateTimeField paramDateTimeField) {
/* 562 */       return paramDateTimeField == null ? false : paramDateTimeField.isSupported();
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\AssembledChronology.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */