/*     */ package com.facebook.presto.jdbc.internal.joda.time.chrono;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTime;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*     */ import java.io.Serializable;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class IslamicChronology
/*     */   extends BasicChronology
/*     */ {
/*     */   private static final long serialVersionUID = -3663823829888L;
/*     */   public static final int AH = 1;
/*  77 */   private static final DateTimeField ERA_FIELD = new BasicSingleEraDateTimeField("AH");
/*     */   
/*     */ 
/*  80 */   public static final LeapYearPatternType LEAP_YEAR_15_BASED = new LeapYearPatternType(0, 623158436);
/*     */   
/*  82 */   public static final LeapYearPatternType LEAP_YEAR_16_BASED = new LeapYearPatternType(1, 623191204);
/*     */   
/*  84 */   public static final LeapYearPatternType LEAP_YEAR_INDIAN = new LeapYearPatternType(2, 690562340);
/*     */   
/*  86 */   public static final LeapYearPatternType LEAP_YEAR_HABASH_AL_HASIB = new LeapYearPatternType(3, 153692453);
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int MIN_YEAR = -292269337;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int MAX_YEAR = 292271022;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int MONTH_PAIR_LENGTH = 59;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int LONG_MONTH_LENGTH = 30;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final int SHORT_MONTH_LENGTH = 29;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final long MILLIS_PER_MONTH_PAIR = 5097600000L;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final long MILLIS_PER_MONTH = 2551440384L;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final long MILLIS_PER_LONG_MONTH = 2592000000L;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final long MILLIS_PER_YEAR = 30617280288L;
/*     */   
/*     */ 
/*     */ 
/*     */   private static final long MILLIS_PER_SHORT_YEAR = 30585600000L;
/*     */   
/*     */ 
/*     */   private static final long MILLIS_PER_LONG_YEAR = 30672000000L;
/*     */   
/*     */ 
/*     */   private static final long MILLIS_YEAR_1 = -42521587200000L;
/*     */   
/*     */ 
/*     */   private static final int CYCLE = 30;
/*     */   
/*     */ 
/*     */   private static final long MILLIS_PER_CYCLE = 918518400000L;
/*     */   
/*     */ 
/* 141 */   private static final ConcurrentHashMap<DateTimeZone, IslamicChronology[]> cCache = new ConcurrentHashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 147 */   private static final IslamicChronology INSTANCE_UTC = getInstance(DateTimeZone.UTC);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final LeapYearPatternType iLeapYears;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static IslamicChronology getInstanceUTC()
/*     */   {
/* 161 */     return INSTANCE_UTC;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static IslamicChronology getInstance()
/*     */   {
/* 170 */     return getInstance(DateTimeZone.getDefault(), LEAP_YEAR_16_BASED);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static IslamicChronology getInstance(DateTimeZone paramDateTimeZone)
/*     */   {
/* 180 */     return getInstance(paramDateTimeZone, LEAP_YEAR_16_BASED);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static IslamicChronology getInstance(DateTimeZone paramDateTimeZone, LeapYearPatternType paramLeapYearPatternType)
/*     */   {
/* 191 */     if (paramDateTimeZone == null) {
/* 192 */       paramDateTimeZone = DateTimeZone.getDefault();
/*     */     }
/*     */     
/* 195 */     Object localObject1 = (IslamicChronology[])cCache.get(paramDateTimeZone);
/* 196 */     if (localObject1 == null) {
/* 197 */       localObject1 = new IslamicChronology[4];
/* 198 */       IslamicChronology[] arrayOfIslamicChronology = (IslamicChronology[])cCache.putIfAbsent(paramDateTimeZone, localObject1);
/* 199 */       if (arrayOfIslamicChronology != null) {
/* 200 */         localObject1 = arrayOfIslamicChronology;
/*     */       }
/*     */     }
/* 203 */     IslamicChronology localIslamicChronology = localObject1[paramLeapYearPatternType.index];
/* 204 */     if (localIslamicChronology == null) {
/* 205 */       synchronized (localObject1) {
/* 206 */         localIslamicChronology = localObject1[paramLeapYearPatternType.index];
/* 207 */         if (localIslamicChronology == null) {
/* 208 */           if (paramDateTimeZone == DateTimeZone.UTC)
/*     */           {
/* 210 */             localIslamicChronology = new IslamicChronology(null, null, paramLeapYearPatternType);
/*     */             
/* 212 */             DateTime localDateTime = new DateTime(1, 1, 1, 0, 0, 0, 0, localIslamicChronology);
/* 213 */             localIslamicChronology = new IslamicChronology(LimitChronology.getInstance(localIslamicChronology, localDateTime, null), null, paramLeapYearPatternType);
/*     */           }
/*     */           else
/*     */           {
/* 217 */             localIslamicChronology = getInstance(DateTimeZone.UTC, paramLeapYearPatternType);
/* 218 */             localIslamicChronology = new IslamicChronology(ZonedChronology.getInstance(localIslamicChronology, paramDateTimeZone), null, paramLeapYearPatternType);
/*     */           }
/*     */           
/* 221 */           localObject1[paramLeapYearPatternType.index] = localIslamicChronology;
/*     */         }
/*     */       }
/*     */     }
/* 225 */     return localIslamicChronology;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   IslamicChronology(Chronology paramChronology, Object paramObject, LeapYearPatternType paramLeapYearPatternType)
/*     */   {
/* 234 */     super(paramChronology, paramObject, 4);
/* 235 */     this.iLeapYears = paramLeapYearPatternType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Object readResolve()
/*     */   {
/* 242 */     Chronology localChronology = getBase();
/* 243 */     return localChronology == null ? getInstanceUTC() : getInstance(localChronology.getZone());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public LeapYearPatternType getLeapYearPatternType()
/*     */   {
/* 253 */     return this.iLeapYears;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Chronology withUTC()
/*     */   {
/* 264 */     return INSTANCE_UTC;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Chronology withZone(DateTimeZone paramDateTimeZone)
/*     */   {
/* 274 */     if (paramDateTimeZone == null) {
/* 275 */       paramDateTimeZone = DateTimeZone.getDefault();
/*     */     }
/* 277 */     if (paramDateTimeZone == getZone()) {
/* 278 */       return this;
/*     */     }
/* 280 */     return getInstance(paramDateTimeZone);
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
/* 292 */     if (this == paramObject) {
/* 293 */       return true;
/*     */     }
/* 295 */     if ((paramObject instanceof IslamicChronology)) {
/* 296 */       IslamicChronology localIslamicChronology = (IslamicChronology)paramObject;
/* 297 */       return (getLeapYearPatternType().index == localIslamicChronology.getLeapYearPatternType().index) && (super.equals(paramObject));
/*     */     }
/*     */     
/* 300 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 310 */     return super.hashCode() * 13 + getLeapYearPatternType().hashCode();
/*     */   }
/*     */   
/*     */   int getYear(long paramLong)
/*     */   {
/* 315 */     long l1 = paramLong - -42521587200000L;
/* 316 */     long l2 = l1 / 918518400000L;
/* 317 */     long l3 = l1 % 918518400000L;
/*     */     
/* 319 */     int i = (int)(l2 * 30L + 1L);
/* 320 */     long l4 = isLeapYear(i) ? 30672000000L : 30585600000L;
/* 321 */     while (l3 >= l4) {
/* 322 */       l3 -= l4;
/* 323 */       l4 = isLeapYear(++i) ? 30672000000L : 30585600000L;
/*     */     }
/* 325 */     return i;
/*     */   }
/*     */   
/*     */   long setYear(long paramLong, int paramInt)
/*     */   {
/* 330 */     int i = getYear(paramLong);
/* 331 */     int j = getDayOfYear(paramLong, i);
/* 332 */     int k = getMillisOfDay(paramLong);
/*     */     
/* 334 */     if (j > 354)
/*     */     {
/* 336 */       if (!isLeapYear(paramInt))
/*     */       {
/* 338 */         j--;
/*     */       }
/*     */     }
/*     */     
/* 342 */     paramLong = getYearMonthDayMillis(paramInt, 1, j);
/* 343 */     paramLong += k;
/* 344 */     return paramLong;
/*     */   }
/*     */   
/*     */ 
/*     */   long getYearDifference(long paramLong1, long paramLong2)
/*     */   {
/* 350 */     int i = getYear(paramLong1);
/* 351 */     int j = getYear(paramLong2);
/*     */     
/*     */ 
/* 354 */     long l1 = paramLong1 - getYearMillis(i);
/* 355 */     long l2 = paramLong2 - getYearMillis(j);
/*     */     
/* 357 */     int k = i - j;
/* 358 */     if (l1 < l2) {
/* 359 */       k--;
/*     */     }
/* 361 */     return k;
/*     */   }
/*     */   
/*     */   long getTotalMillisByYearMonth(int paramInt1, int paramInt2) {
/*     */     
/* 366 */     if (paramInt2 % 2 == 1) {
/* 367 */       paramInt2 /= 2;
/* 368 */       return paramInt2 * 5097600000L + 2592000000L;
/*     */     }
/* 370 */     paramInt2 /= 2;
/* 371 */     return paramInt2 * 5097600000L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   int getDayOfMonth(long paramLong)
/*     */   {
/* 378 */     int i = getDayOfYear(paramLong) - 1;
/* 379 */     if (i == 354) {
/* 380 */       return 30;
/*     */     }
/* 382 */     return i % 59 % 30 + 1;
/*     */   }
/*     */   
/*     */   boolean isLeapYear(int paramInt)
/*     */   {
/* 387 */     return this.iLeapYears.isLeapYear(paramInt);
/*     */   }
/*     */   
/*     */   int getDaysInYearMax()
/*     */   {
/* 392 */     return 355;
/*     */   }
/*     */   
/*     */   int getDaysInYear(int paramInt)
/*     */   {
/* 397 */     return isLeapYear(paramInt) ? 355 : 354;
/*     */   }
/*     */   
/*     */   int getDaysInYearMonth(int paramInt1, int paramInt2)
/*     */   {
/* 402 */     if ((paramInt2 == 12) && (isLeapYear(paramInt1))) {
/* 403 */       return 30;
/*     */     }
/* 405 */     paramInt2--;return paramInt2 % 2 == 0 ? 30 : 29;
/*     */   }
/*     */   
/*     */   int getDaysInMonthMax()
/*     */   {
/* 410 */     return 30;
/*     */   }
/*     */   
/*     */   int getDaysInMonthMax(int paramInt)
/*     */   {
/* 415 */     if (paramInt == 12) {
/* 416 */       return 30;
/*     */     }
/* 418 */     paramInt--;return paramInt % 2 == 0 ? 30 : 29;
/*     */   }
/*     */   
/*     */   int getMonthOfYear(long paramLong, int paramInt)
/*     */   {
/* 423 */     int i = (int)((paramLong - getYearMillis(paramInt)) / 86400000L);
/* 424 */     if (i == 354) {
/* 425 */       return 12;
/*     */     }
/* 427 */     return i * 2 / 59 + 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   long getAverageMillisPerYear()
/*     */   {
/* 437 */     return 30617280288L;
/*     */   }
/*     */   
/*     */   long getAverageMillisPerYearDividedByTwo()
/*     */   {
/* 442 */     return 15308640144L;
/*     */   }
/*     */   
/*     */   long getAverageMillisPerMonth()
/*     */   {
/* 447 */     return 2551440384L;
/*     */   }
/*     */   
/*     */   long calculateFirstDayOfYearMillis(int paramInt)
/*     */   {
/* 452 */     if (paramInt > 292271022) {
/* 453 */       throw new ArithmeticException("Year is too large: " + paramInt + " > " + 292271022);
/*     */     }
/* 455 */     if (paramInt < -292269337) {
/* 456 */       throw new ArithmeticException("Year is too small: " + paramInt + " < " + -292269337);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 463 */     paramInt--;
/* 464 */     long l1 = paramInt / 30;
/* 465 */     long l2 = -42521587200000L + l1 * 918518400000L;
/* 466 */     int i = paramInt % 30 + 1;
/*     */     
/* 468 */     for (int j = 1; j < i; j++) {
/* 469 */       l2 += (isLeapYear(j) ? 30672000000L : 30585600000L);
/*     */     }
/*     */     
/* 472 */     return l2;
/*     */   }
/*     */   
/*     */   int getMinYear()
/*     */   {
/* 477 */     return 1;
/*     */   }
/*     */   
/*     */   int getMaxYear()
/*     */   {
/* 482 */     return 292271022;
/*     */   }
/*     */   
/*     */ 
/*     */   long getApproxMillisAtEpochDividedByTwo()
/*     */   {
/* 488 */     return 21260793600000L;
/*     */   }
/*     */   
/*     */   protected void assemble(AssembledChronology.Fields paramFields)
/*     */   {
/* 493 */     if (getBase() == null) {
/* 494 */       super.assemble(paramFields);
/*     */       
/* 496 */       paramFields.era = ERA_FIELD;
/* 497 */       paramFields.monthOfYear = new BasicMonthOfYearDateTimeField(this, 12);
/* 498 */       paramFields.months = paramFields.monthOfYear.getDurationField();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static class LeapYearPatternType
/*     */     implements Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 26581275372698L;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     final byte index;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     final int pattern;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     LeapYearPatternType(int paramInt1, int paramInt2)
/*     */     {
/* 535 */       this.index = ((byte)paramInt1);
/* 536 */       this.pattern = paramInt2;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     boolean isLeapYear(int paramInt)
/*     */     {
/* 545 */       int i = 1 << paramInt % 30;
/* 546 */       return (this.pattern & i) > 0;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private Object readResolve()
/*     */     {
/* 554 */       switch (this.index) {
/*     */       case 0: 
/* 556 */         return IslamicChronology.LEAP_YEAR_15_BASED;
/*     */       case 1: 
/* 558 */         return IslamicChronology.LEAP_YEAR_16_BASED;
/*     */       case 2: 
/* 560 */         return IslamicChronology.LEAP_YEAR_INDIAN;
/*     */       case 3: 
/* 562 */         return IslamicChronology.LEAP_YEAR_HABASH_AL_HASIB;
/*     */       }
/* 564 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean equals(Object paramObject)
/*     */     {
/* 570 */       if ((paramObject instanceof LeapYearPatternType)) {
/* 571 */         return this.index == ((LeapYearPatternType)paramObject).index;
/*     */       }
/* 573 */       return false;
/*     */     }
/*     */     
/*     */     public int hashCode()
/*     */     {
/* 578 */       return this.index;
/*     */     }
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\IslamicChronology.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */