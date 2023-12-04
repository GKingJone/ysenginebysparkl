/*     */ package com.facebook.presto.jdbc.internal.joda.time;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.FieldUtils;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class PeriodType
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 2274324892792009998L;
/*  56 */   private static final Map<PeriodType, Object> cTypes = new HashMap(32);
/*     */   
/*  58 */   static int YEAR_INDEX = 0;
/*  59 */   static int MONTH_INDEX = 1;
/*  60 */   static int WEEK_INDEX = 2;
/*  61 */   static int DAY_INDEX = 3;
/*  62 */   static int HOUR_INDEX = 4;
/*  63 */   static int MINUTE_INDEX = 5;
/*  64 */   static int SECOND_INDEX = 6;
/*  65 */   static int MILLI_INDEX = 7;
/*     */   
/*     */   private static PeriodType cStandard;
/*     */   
/*     */   private static PeriodType cYMDTime;
/*     */   
/*     */   private static PeriodType cYMD;
/*     */   
/*     */   private static PeriodType cYWDTime;
/*     */   
/*     */   private static PeriodType cYWD;
/*     */   
/*     */   private static PeriodType cYDTime;
/*     */   
/*     */   private static PeriodType cYD;
/*     */   
/*     */   private static PeriodType cDTime;
/*     */   
/*     */   private static PeriodType cTime;
/*     */   
/*     */   private static PeriodType cYears;
/*     */   
/*     */   private static PeriodType cMonths;
/*     */   
/*     */   private static PeriodType cWeeks;
/*     */   
/*     */   private static PeriodType cDays;
/*     */   private static PeriodType cHours;
/*     */   private static PeriodType cMinutes;
/*     */   private static PeriodType cSeconds;
/*     */   private static PeriodType cMillis;
/*     */   private final String iName;
/*     */   private final DurationFieldType[] iTypes;
/*     */   private final int[] iIndices;
/*     */   
/*     */   public static PeriodType standard()
/*     */   {
/* 102 */     PeriodType localPeriodType = cStandard;
/* 103 */     if (localPeriodType == null) {
/* 104 */       localPeriodType = new PeriodType("Standard", new DurationFieldType[] { DurationFieldType.years(), DurationFieldType.months(), DurationFieldType.weeks(), DurationFieldType.days(), DurationFieldType.hours(), DurationFieldType.minutes(), DurationFieldType.seconds(), DurationFieldType.millis() }, new int[] { 0, 1, 2, 3, 4, 5, 6, 7 });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 114 */       cStandard = localPeriodType;
/*     */     }
/* 116 */     return localPeriodType;
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
/*     */   public static PeriodType yearMonthDayTime()
/*     */   {
/* 134 */     PeriodType localPeriodType = cYMDTime;
/* 135 */     if (localPeriodType == null) {
/* 136 */       localPeriodType = new PeriodType("YearMonthDayTime", new DurationFieldType[] { DurationFieldType.years(), DurationFieldType.months(), DurationFieldType.days(), DurationFieldType.hours(), DurationFieldType.minutes(), DurationFieldType.seconds(), DurationFieldType.millis() }, new int[] { 0, 1, -1, 2, 3, 4, 5, 6 });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 146 */       cYMDTime = localPeriodType;
/*     */     }
/* 148 */     return localPeriodType;
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
/*     */   public static PeriodType yearMonthDay()
/*     */   {
/* 163 */     PeriodType localPeriodType = cYMD;
/* 164 */     if (localPeriodType == null) {
/* 165 */       localPeriodType = new PeriodType("YearMonthDay", new DurationFieldType[] { DurationFieldType.years(), DurationFieldType.months(), DurationFieldType.days() }, new int[] { 0, 1, -1, 2, -1, -1, -1, -1 });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 173 */       cYMD = localPeriodType;
/*     */     }
/* 175 */     return localPeriodType;
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
/*     */   public static PeriodType yearWeekDayTime()
/*     */   {
/* 193 */     PeriodType localPeriodType = cYWDTime;
/* 194 */     if (localPeriodType == null) {
/* 195 */       localPeriodType = new PeriodType("YearWeekDayTime", new DurationFieldType[] { DurationFieldType.years(), DurationFieldType.weeks(), DurationFieldType.days(), DurationFieldType.hours(), DurationFieldType.minutes(), DurationFieldType.seconds(), DurationFieldType.millis() }, new int[] { 0, -1, 1, 2, 3, 4, 5, 6 });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 205 */       cYWDTime = localPeriodType;
/*     */     }
/* 207 */     return localPeriodType;
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
/*     */   public static PeriodType yearWeekDay()
/*     */   {
/* 222 */     PeriodType localPeriodType = cYWD;
/* 223 */     if (localPeriodType == null) {
/* 224 */       localPeriodType = new PeriodType("YearWeekDay", new DurationFieldType[] { DurationFieldType.years(), DurationFieldType.weeks(), DurationFieldType.days() }, new int[] { 0, -1, 1, 2, -1, -1, -1, -1 });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 232 */       cYWD = localPeriodType;
/*     */     }
/* 234 */     return localPeriodType;
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
/*     */   public static PeriodType yearDayTime()
/*     */   {
/* 251 */     PeriodType localPeriodType = cYDTime;
/* 252 */     if (localPeriodType == null) {
/* 253 */       localPeriodType = new PeriodType("YearDayTime", new DurationFieldType[] { DurationFieldType.years(), DurationFieldType.days(), DurationFieldType.hours(), DurationFieldType.minutes(), DurationFieldType.seconds(), DurationFieldType.millis() }, new int[] { 0, -1, -1, 1, 2, 3, 4, 5 });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 262 */       cYDTime = localPeriodType;
/*     */     }
/* 264 */     return localPeriodType;
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
/*     */   public static PeriodType yearDay()
/*     */   {
/* 278 */     PeriodType localPeriodType = cYD;
/* 279 */     if (localPeriodType == null) {
/* 280 */       localPeriodType = new PeriodType("YearDay", new DurationFieldType[] { DurationFieldType.years(), DurationFieldType.days() }, new int[] { 0, -1, -1, 1, -1, -1, -1, -1 });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 287 */       cYD = localPeriodType;
/*     */     }
/* 289 */     return localPeriodType;
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
/*     */   public static PeriodType dayTime()
/*     */   {
/* 305 */     PeriodType localPeriodType = cDTime;
/* 306 */     if (localPeriodType == null) {
/* 307 */       localPeriodType = new PeriodType("DayTime", new DurationFieldType[] { DurationFieldType.days(), DurationFieldType.hours(), DurationFieldType.minutes(), DurationFieldType.seconds(), DurationFieldType.millis() }, new int[] { -1, -1, -1, 0, 1, 2, 3, 4 });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 316 */       cDTime = localPeriodType;
/*     */     }
/* 318 */     return localPeriodType;
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
/*     */   public static PeriodType time()
/*     */   {
/* 333 */     PeriodType localPeriodType = cTime;
/* 334 */     if (localPeriodType == null) {
/* 335 */       localPeriodType = new PeriodType("Time", new DurationFieldType[] { DurationFieldType.hours(), DurationFieldType.minutes(), DurationFieldType.seconds(), DurationFieldType.millis() }, new int[] { -1, -1, -1, -1, 0, 1, 2, 3 });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 343 */       cTime = localPeriodType;
/*     */     }
/* 345 */     return localPeriodType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PeriodType years()
/*     */   {
/* 354 */     PeriodType localPeriodType = cYears;
/* 355 */     if (localPeriodType == null) {
/* 356 */       localPeriodType = new PeriodType("Years", new DurationFieldType[] { DurationFieldType.years() }, new int[] { 0, -1, -1, -1, -1, -1, -1, -1 });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 361 */       cYears = localPeriodType;
/*     */     }
/* 363 */     return localPeriodType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PeriodType months()
/*     */   {
/* 372 */     PeriodType localPeriodType = cMonths;
/* 373 */     if (localPeriodType == null) {
/* 374 */       localPeriodType = new PeriodType("Months", new DurationFieldType[] { DurationFieldType.months() }, new int[] { -1, 0, -1, -1, -1, -1, -1, -1 });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 379 */       cMonths = localPeriodType;
/*     */     }
/* 381 */     return localPeriodType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PeriodType weeks()
/*     */   {
/* 390 */     PeriodType localPeriodType = cWeeks;
/* 391 */     if (localPeriodType == null) {
/* 392 */       localPeriodType = new PeriodType("Weeks", new DurationFieldType[] { DurationFieldType.weeks() }, new int[] { -1, -1, 0, -1, -1, -1, -1, -1 });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 397 */       cWeeks = localPeriodType;
/*     */     }
/* 399 */     return localPeriodType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PeriodType days()
/*     */   {
/* 408 */     PeriodType localPeriodType = cDays;
/* 409 */     if (localPeriodType == null) {
/* 410 */       localPeriodType = new PeriodType("Days", new DurationFieldType[] { DurationFieldType.days() }, new int[] { -1, -1, -1, 0, -1, -1, -1, -1 });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 415 */       cDays = localPeriodType;
/*     */     }
/* 417 */     return localPeriodType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PeriodType hours()
/*     */   {
/* 426 */     PeriodType localPeriodType = cHours;
/* 427 */     if (localPeriodType == null) {
/* 428 */       localPeriodType = new PeriodType("Hours", new DurationFieldType[] { DurationFieldType.hours() }, new int[] { -1, -1, -1, -1, 0, -1, -1, -1 });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 433 */       cHours = localPeriodType;
/*     */     }
/* 435 */     return localPeriodType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PeriodType minutes()
/*     */   {
/* 444 */     PeriodType localPeriodType = cMinutes;
/* 445 */     if (localPeriodType == null) {
/* 446 */       localPeriodType = new PeriodType("Minutes", new DurationFieldType[] { DurationFieldType.minutes() }, new int[] { -1, -1, -1, -1, -1, 0, -1, -1 });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 451 */       cMinutes = localPeriodType;
/*     */     }
/* 453 */     return localPeriodType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PeriodType seconds()
/*     */   {
/* 462 */     PeriodType localPeriodType = cSeconds;
/* 463 */     if (localPeriodType == null) {
/* 464 */       localPeriodType = new PeriodType("Seconds", new DurationFieldType[] { DurationFieldType.seconds() }, new int[] { -1, -1, -1, -1, -1, -1, 0, -1 });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 469 */       cSeconds = localPeriodType;
/*     */     }
/* 471 */     return localPeriodType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PeriodType millis()
/*     */   {
/* 480 */     PeriodType localPeriodType = cMillis;
/* 481 */     if (localPeriodType == null) {
/* 482 */       localPeriodType = new PeriodType("Millis", new DurationFieldType[] { DurationFieldType.millis() }, new int[] { -1, -1, -1, -1, -1, -1, -1, 0 });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 487 */       cMillis = localPeriodType;
/*     */     }
/* 489 */     return localPeriodType;
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
/*     */   public static synchronized PeriodType forFields(DurationFieldType[] paramArrayOfDurationFieldType)
/*     */   {
/* 502 */     if ((paramArrayOfDurationFieldType == null) || (paramArrayOfDurationFieldType.length == 0)) {
/* 503 */       throw new IllegalArgumentException("Types array must not be null or empty");
/*     */     }
/* 505 */     for (int i = 0; i < paramArrayOfDurationFieldType.length; i++) {
/* 506 */       if (paramArrayOfDurationFieldType[i] == null) {
/* 507 */         throw new IllegalArgumentException("Types array must not contain null");
/*     */       }
/*     */     }
/* 510 */     Map localMap = cTypes;
/* 511 */     if (localMap.isEmpty()) {
/* 512 */       localMap.put(standard(), standard());
/* 513 */       localMap.put(yearMonthDayTime(), yearMonthDayTime());
/* 514 */       localMap.put(yearMonthDay(), yearMonthDay());
/* 515 */       localMap.put(yearWeekDayTime(), yearWeekDayTime());
/* 516 */       localMap.put(yearWeekDay(), yearWeekDay());
/* 517 */       localMap.put(yearDayTime(), yearDayTime());
/* 518 */       localMap.put(yearDay(), yearDay());
/* 519 */       localMap.put(dayTime(), dayTime());
/* 520 */       localMap.put(time(), time());
/* 521 */       localMap.put(years(), years());
/* 522 */       localMap.put(months(), months());
/* 523 */       localMap.put(weeks(), weeks());
/* 524 */       localMap.put(days(), days());
/* 525 */       localMap.put(hours(), hours());
/* 526 */       localMap.put(minutes(), minutes());
/* 527 */       localMap.put(seconds(), seconds());
/* 528 */       localMap.put(millis(), millis());
/*     */     }
/* 530 */     PeriodType localPeriodType1 = new PeriodType(null, paramArrayOfDurationFieldType, null);
/* 531 */     Object localObject = localMap.get(localPeriodType1);
/* 532 */     if ((localObject instanceof PeriodType)) {
/* 533 */       return (PeriodType)localObject;
/*     */     }
/* 535 */     if (localObject != null) {
/* 536 */       throw new IllegalArgumentException("PeriodType does not support fields: " + localObject);
/*     */     }
/* 538 */     PeriodType localPeriodType2 = standard();
/* 539 */     ArrayList localArrayList = new ArrayList(Arrays.asList(paramArrayOfDurationFieldType));
/* 540 */     if (!localArrayList.remove(DurationFieldType.years())) {
/* 541 */       localPeriodType2 = localPeriodType2.withYearsRemoved();
/*     */     }
/* 543 */     if (!localArrayList.remove(DurationFieldType.months())) {
/* 544 */       localPeriodType2 = localPeriodType2.withMonthsRemoved();
/*     */     }
/* 546 */     if (!localArrayList.remove(DurationFieldType.weeks())) {
/* 547 */       localPeriodType2 = localPeriodType2.withWeeksRemoved();
/*     */     }
/* 549 */     if (!localArrayList.remove(DurationFieldType.days())) {
/* 550 */       localPeriodType2 = localPeriodType2.withDaysRemoved();
/*     */     }
/* 552 */     if (!localArrayList.remove(DurationFieldType.hours())) {
/* 553 */       localPeriodType2 = localPeriodType2.withHoursRemoved();
/*     */     }
/* 555 */     if (!localArrayList.remove(DurationFieldType.minutes())) {
/* 556 */       localPeriodType2 = localPeriodType2.withMinutesRemoved();
/*     */     }
/* 558 */     if (!localArrayList.remove(DurationFieldType.seconds())) {
/* 559 */       localPeriodType2 = localPeriodType2.withSecondsRemoved();
/*     */     }
/* 561 */     if (!localArrayList.remove(DurationFieldType.millis())) {
/* 562 */       localPeriodType2 = localPeriodType2.withMillisRemoved();
/*     */     }
/* 564 */     if (localArrayList.size() > 0) {
/* 565 */       localMap.put(localPeriodType1, localArrayList);
/* 566 */       throw new IllegalArgumentException("PeriodType does not support fields: " + localArrayList);
/*     */     }
/*     */     
/* 569 */     PeriodType localPeriodType3 = new PeriodType(null, localPeriodType2.iTypes, null);
/* 570 */     PeriodType localPeriodType4 = (PeriodType)localMap.get(localPeriodType3);
/* 571 */     if (localPeriodType4 != null) {
/* 572 */       localMap.put(localPeriodType3, localPeriodType4);
/* 573 */       return localPeriodType4;
/*     */     }
/* 575 */     localMap.put(localPeriodType3, localPeriodType2);
/* 576 */     return localPeriodType2;
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
/*     */   protected PeriodType(String paramString, DurationFieldType[] paramArrayOfDurationFieldType, int[] paramArrayOfInt)
/*     */   {
/* 596 */     this.iName = paramString;
/* 597 */     this.iTypes = paramArrayOfDurationFieldType;
/* 598 */     this.iIndices = paramArrayOfInt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 608 */     return this.iName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int size()
/*     */   {
/* 617 */     return this.iTypes.length;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationFieldType getFieldType(int paramInt)
/*     */   {
/* 628 */     return this.iTypes[paramInt];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isSupported(DurationFieldType paramDurationFieldType)
/*     */   {
/* 638 */     return indexOf(paramDurationFieldType) >= 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int indexOf(DurationFieldType paramDurationFieldType)
/*     */   {
/* 648 */     int i = 0; for (int j = size(); i < j; i++) {
/* 649 */       if (this.iTypes[i] == paramDurationFieldType) {
/* 650 */         return i;
/*     */       }
/*     */     }
/* 653 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 662 */     return "PeriodType[" + getName() + "]";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   int getIndexedField(ReadablePeriod paramReadablePeriod, int paramInt)
/*     */   {
/* 674 */     int i = this.iIndices[paramInt];
/* 675 */     return i == -1 ? 0 : paramReadablePeriod.getValue(i);
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
/*     */   boolean setIndexedField(ReadablePeriod paramReadablePeriod, int paramInt1, int[] paramArrayOfInt, int paramInt2)
/*     */   {
/* 688 */     int i = this.iIndices[paramInt1];
/* 689 */     if (i == -1) {
/* 690 */       throw new UnsupportedOperationException("Field is not supported");
/*     */     }
/* 692 */     paramArrayOfInt[i] = paramInt2;
/* 693 */     return true;
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
/*     */   boolean addIndexedField(ReadablePeriod paramReadablePeriod, int paramInt1, int[] paramArrayOfInt, int paramInt2)
/*     */   {
/* 707 */     if (paramInt2 == 0) {
/* 708 */       return false;
/*     */     }
/* 710 */     int i = this.iIndices[paramInt1];
/* 711 */     if (i == -1) {
/* 712 */       throw new UnsupportedOperationException("Field is not supported");
/*     */     }
/* 714 */     paramArrayOfInt[i] = FieldUtils.safeAdd(paramArrayOfInt[i], paramInt2);
/* 715 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PeriodType withYearsRemoved()
/*     */   {
/* 725 */     return withFieldRemoved(0, "NoYears");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PeriodType withMonthsRemoved()
/*     */   {
/* 734 */     return withFieldRemoved(1, "NoMonths");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PeriodType withWeeksRemoved()
/*     */   {
/* 743 */     return withFieldRemoved(2, "NoWeeks");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PeriodType withDaysRemoved()
/*     */   {
/* 752 */     return withFieldRemoved(3, "NoDays");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PeriodType withHoursRemoved()
/*     */   {
/* 761 */     return withFieldRemoved(4, "NoHours");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PeriodType withMinutesRemoved()
/*     */   {
/* 770 */     return withFieldRemoved(5, "NoMinutes");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PeriodType withSecondsRemoved()
/*     */   {
/* 779 */     return withFieldRemoved(6, "NoSeconds");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PeriodType withMillisRemoved()
/*     */   {
/* 788 */     return withFieldRemoved(7, "NoMillis");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private PeriodType withFieldRemoved(int paramInt, String paramString)
/*     */   {
/* 799 */     int i = this.iIndices[paramInt];
/* 800 */     if (i == -1) {
/* 801 */       return this;
/*     */     }
/*     */     
/* 804 */     DurationFieldType[] arrayOfDurationFieldType = new DurationFieldType[size() - 1];
/* 805 */     for (int j = 0; j < this.iTypes.length; j++) {
/* 806 */       if (j < i) {
/* 807 */         arrayOfDurationFieldType[j] = this.iTypes[j];
/* 808 */       } else if (j > i) {
/* 809 */         arrayOfDurationFieldType[(j - 1)] = this.iTypes[j];
/*     */       }
/*     */     }
/*     */     
/* 813 */     int[] arrayOfInt = new int[8];
/* 814 */     for (int k = 0; k < arrayOfInt.length; k++) {
/* 815 */       if (k < paramInt) {
/* 816 */         arrayOfInt[k] = this.iIndices[k];
/* 817 */       } else if (k > paramInt) {
/* 818 */         arrayOfInt[k] = (this.iIndices[k] == -1 ? -1 : this.iIndices[k] - 1);
/*     */       } else {
/* 820 */         arrayOfInt[k] = -1;
/*     */       }
/*     */     }
/* 823 */     return new PeriodType(getName() + paramString, arrayOfDurationFieldType, arrayOfInt);
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
/* 835 */     if (this == paramObject) {
/* 836 */       return true;
/*     */     }
/* 838 */     if (!(paramObject instanceof PeriodType)) {
/* 839 */       return false;
/*     */     }
/* 841 */     PeriodType localPeriodType = (PeriodType)paramObject;
/* 842 */     return Arrays.equals(this.iTypes, localPeriodType.iTypes);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 851 */     int i = 0;
/* 852 */     for (int j = 0; j < this.iTypes.length; j++) {
/* 853 */       i += this.iTypes[j].hashCode();
/*     */     }
/* 855 */     return i;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\PeriodType.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */