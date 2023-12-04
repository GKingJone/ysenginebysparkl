/*     */ package com.facebook.presto.jdbc.internal.joda.time;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.base.BaseSingleFieldPeriod;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.FieldUtils;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.format.ISOPeriodFormat;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.format.PeriodFormatter;
/*     */ import org.joda.convert.FromString;
/*     */ import org.joda.convert.ToString;
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
/*     */ public final class Minutes
/*     */   extends BaseSingleFieldPeriod
/*     */ {
/*  45 */   public static final Minutes ZERO = new Minutes(0);
/*     */   
/*  47 */   public static final Minutes ONE = new Minutes(1);
/*     */   
/*  49 */   public static final Minutes TWO = new Minutes(2);
/*     */   
/*  51 */   public static final Minutes THREE = new Minutes(3);
/*     */   
/*  53 */   public static final Minutes MAX_VALUE = new Minutes(Integer.MAX_VALUE);
/*     */   
/*  55 */   public static final Minutes MIN_VALUE = new Minutes(Integer.MIN_VALUE);
/*     */   
/*     */ 
/*  58 */   private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.minutes());
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final long serialVersionUID = 87525275727380863L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Minutes minutes(int paramInt)
/*     */   {
/*  72 */     switch (paramInt) {
/*     */     case 0: 
/*  74 */       return ZERO;
/*     */     case 1: 
/*  76 */       return ONE;
/*     */     case 2: 
/*  78 */       return TWO;
/*     */     case 3: 
/*  80 */       return THREE;
/*     */     case 2147483647: 
/*  82 */       return MAX_VALUE;
/*     */     case -2147483648: 
/*  84 */       return MIN_VALUE;
/*     */     }
/*  86 */     return new Minutes(paramInt);
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
/*     */   public static Minutes minutesBetween(ReadableInstant paramReadableInstant1, ReadableInstant paramReadableInstant2)
/*     */   {
/* 101 */     int i = BaseSingleFieldPeriod.between(paramReadableInstant1, paramReadableInstant2, DurationFieldType.minutes());
/* 102 */     return minutes(i);
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
/*     */   public static Minutes minutesBetween(ReadablePartial paramReadablePartial1, ReadablePartial paramReadablePartial2)
/*     */   {
/* 118 */     if (((paramReadablePartial1 instanceof LocalTime)) && ((paramReadablePartial2 instanceof LocalTime))) {
/* 119 */       Chronology localChronology = DateTimeUtils.getChronology(paramReadablePartial1.getChronology());
/* 120 */       int j = localChronology.minutes().getDifference(((LocalTime)paramReadablePartial2).getLocalMillis(), ((LocalTime)paramReadablePartial1).getLocalMillis());
/*     */       
/* 122 */       return minutes(j);
/*     */     }
/* 124 */     int i = BaseSingleFieldPeriod.between(paramReadablePartial1, paramReadablePartial2, ZERO);
/* 125 */     return minutes(i);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Minutes minutesIn(ReadableInterval paramReadableInterval)
/*     */   {
/* 137 */     if (paramReadableInterval == null) {
/* 138 */       return ZERO;
/*     */     }
/* 140 */     int i = BaseSingleFieldPeriod.between(paramReadableInterval.getStart(), paramReadableInterval.getEnd(), DurationFieldType.minutes());
/* 141 */     return minutes(i);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Minutes standardMinutesIn(ReadablePeriod paramReadablePeriod)
/*     */   {
/* 165 */     int i = BaseSingleFieldPeriod.standardPeriodIn(paramReadablePeriod, 60000L);
/* 166 */     return minutes(i);
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
/*     */   @FromString
/*     */   public static Minutes parseMinutes(String paramString)
/*     */   {
/* 182 */     if (paramString == null) {
/* 183 */       return ZERO;
/*     */     }
/* 185 */     Period localPeriod = PARSER.parsePeriod(paramString);
/* 186 */     return minutes(localPeriod.getMinutes());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Minutes(int paramInt)
/*     */   {
/* 198 */     super(paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Object readResolve()
/*     */   {
/* 207 */     return minutes(getValue());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationFieldType getFieldType()
/*     */   {
/* 217 */     return DurationFieldType.minutes();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PeriodType getPeriodType()
/*     */   {
/* 226 */     return PeriodType.minutes();
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
/*     */   public Weeks toStandardWeeks()
/*     */   {
/* 244 */     return Weeks.weeks(getValue() / 10080);
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
/*     */   public Days toStandardDays()
/*     */   {
/* 261 */     return Days.days(getValue() / 1440);
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
/*     */   public Hours toStandardHours()
/*     */   {
/* 277 */     return Hours.hours(getValue() / 60);
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
/*     */   public Seconds toStandardSeconds()
/*     */   {
/* 294 */     return Seconds.seconds(FieldUtils.safeMultiply(getValue(), 60));
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
/*     */   public Duration toStandardDuration()
/*     */   {
/* 312 */     long l = getValue();
/* 313 */     return new Duration(l * 60000L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMinutes()
/*     */   {
/* 323 */     return getValue();
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
/*     */   public Minutes plus(int paramInt)
/*     */   {
/* 337 */     if (paramInt == 0) {
/* 338 */       return this;
/*     */     }
/* 340 */     return minutes(FieldUtils.safeAdd(getValue(), paramInt));
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
/*     */   public Minutes plus(Minutes paramMinutes)
/*     */   {
/* 353 */     if (paramMinutes == null) {
/* 354 */       return this;
/*     */     }
/* 356 */     return plus(paramMinutes.getValue());
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
/*     */   public Minutes minus(int paramInt)
/*     */   {
/* 370 */     return plus(FieldUtils.safeNegate(paramInt));
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
/*     */   public Minutes minus(Minutes paramMinutes)
/*     */   {
/* 383 */     if (paramMinutes == null) {
/* 384 */       return this;
/*     */     }
/* 386 */     return minus(paramMinutes.getValue());
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
/*     */   public Minutes multipliedBy(int paramInt)
/*     */   {
/* 400 */     return minutes(FieldUtils.safeMultiply(getValue(), paramInt));
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
/*     */   public Minutes dividedBy(int paramInt)
/*     */   {
/* 414 */     if (paramInt == 1) {
/* 415 */       return this;
/*     */     }
/* 417 */     return minutes(getValue() / paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Minutes negated()
/*     */   {
/* 428 */     return minutes(FieldUtils.safeNegate(getValue()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isGreaterThan(Minutes paramMinutes)
/*     */   {
/* 439 */     if (paramMinutes == null) {
/* 440 */       return getValue() > 0;
/*     */     }
/* 442 */     return getValue() > paramMinutes.getValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isLessThan(Minutes paramMinutes)
/*     */   {
/* 452 */     if (paramMinutes == null) {
/* 453 */       return getValue() < 0;
/*     */     }
/* 455 */     return getValue() < paramMinutes.getValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @ToString
/*     */   public String toString()
/*     */   {
/* 468 */     return "PT" + String.valueOf(getValue()) + "M";
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\Minutes.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */