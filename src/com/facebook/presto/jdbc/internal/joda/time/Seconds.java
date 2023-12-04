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
/*     */ public final class Seconds
/*     */   extends BaseSingleFieldPeriod
/*     */ {
/*  45 */   public static final Seconds ZERO = new Seconds(0);
/*     */   
/*  47 */   public static final Seconds ONE = new Seconds(1);
/*     */   
/*  49 */   public static final Seconds TWO = new Seconds(2);
/*     */   
/*  51 */   public static final Seconds THREE = new Seconds(3);
/*     */   
/*  53 */   public static final Seconds MAX_VALUE = new Seconds(Integer.MAX_VALUE);
/*     */   
/*  55 */   public static final Seconds MIN_VALUE = new Seconds(Integer.MIN_VALUE);
/*     */   
/*     */ 
/*  58 */   private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.seconds());
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final long serialVersionUID = 87525275727380862L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Seconds seconds(int paramInt)
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
/*  86 */     return new Seconds(paramInt);
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
/*     */   public static Seconds secondsBetween(ReadableInstant paramReadableInstant1, ReadableInstant paramReadableInstant2)
/*     */   {
/* 101 */     int i = BaseSingleFieldPeriod.between(paramReadableInstant1, paramReadableInstant2, DurationFieldType.seconds());
/* 102 */     return seconds(i);
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
/*     */   public static Seconds secondsBetween(ReadablePartial paramReadablePartial1, ReadablePartial paramReadablePartial2)
/*     */   {
/* 118 */     if (((paramReadablePartial1 instanceof LocalTime)) && ((paramReadablePartial2 instanceof LocalTime))) {
/* 119 */       Chronology localChronology = DateTimeUtils.getChronology(paramReadablePartial1.getChronology());
/* 120 */       int j = localChronology.seconds().getDifference(((LocalTime)paramReadablePartial2).getLocalMillis(), ((LocalTime)paramReadablePartial1).getLocalMillis());
/*     */       
/* 122 */       return seconds(j);
/*     */     }
/* 124 */     int i = BaseSingleFieldPeriod.between(paramReadablePartial1, paramReadablePartial2, ZERO);
/* 125 */     return seconds(i);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Seconds secondsIn(ReadableInterval paramReadableInterval)
/*     */   {
/* 137 */     if (paramReadableInterval == null) {
/* 138 */       return ZERO;
/*     */     }
/* 140 */     int i = BaseSingleFieldPeriod.between(paramReadableInterval.getStart(), paramReadableInterval.getEnd(), DurationFieldType.seconds());
/* 141 */     return seconds(i);
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
/*     */   public static Seconds standardSecondsIn(ReadablePeriod paramReadablePeriod)
/*     */   {
/* 165 */     int i = BaseSingleFieldPeriod.standardPeriodIn(paramReadablePeriod, 1000L);
/* 166 */     return seconds(i);
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
/*     */   public static Seconds parseSeconds(String paramString)
/*     */   {
/* 182 */     if (paramString == null) {
/* 183 */       return ZERO;
/*     */     }
/* 185 */     Period localPeriod = PARSER.parsePeriod(paramString);
/* 186 */     return seconds(localPeriod.getSeconds());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Seconds(int paramInt)
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
/* 207 */     return seconds(getValue());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationFieldType getFieldType()
/*     */   {
/* 217 */     return DurationFieldType.seconds();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PeriodType getPeriodType()
/*     */   {
/* 226 */     return PeriodType.seconds();
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
/*     */   public Weeks toStandardWeeks()
/*     */   {
/* 245 */     return Weeks.weeks(getValue() / 604800);
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
/* 262 */     return Days.days(getValue() / 86400);
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
/* 278 */     return Hours.hours(getValue() / 3600);
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
/*     */   public Minutes toStandardMinutes()
/*     */   {
/* 294 */     return Minutes.minutes(getValue() / 60);
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
/* 313 */     return new Duration(l * 1000L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getSeconds()
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
/*     */   public Seconds plus(int paramInt)
/*     */   {
/* 337 */     if (paramInt == 0) {
/* 338 */       return this;
/*     */     }
/* 340 */     return seconds(FieldUtils.safeAdd(getValue(), paramInt));
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
/*     */   public Seconds plus(Seconds paramSeconds)
/*     */   {
/* 353 */     if (paramSeconds == null) {
/* 354 */       return this;
/*     */     }
/* 356 */     return plus(paramSeconds.getValue());
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
/*     */   public Seconds minus(int paramInt)
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
/*     */   public Seconds minus(Seconds paramSeconds)
/*     */   {
/* 383 */     if (paramSeconds == null) {
/* 384 */       return this;
/*     */     }
/* 386 */     return minus(paramSeconds.getValue());
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
/*     */   public Seconds multipliedBy(int paramInt)
/*     */   {
/* 400 */     return seconds(FieldUtils.safeMultiply(getValue(), paramInt));
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
/*     */   public Seconds dividedBy(int paramInt)
/*     */   {
/* 414 */     if (paramInt == 1) {
/* 415 */       return this;
/*     */     }
/* 417 */     return seconds(getValue() / paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Seconds negated()
/*     */   {
/* 428 */     return seconds(FieldUtils.safeNegate(getValue()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isGreaterThan(Seconds paramSeconds)
/*     */   {
/* 439 */     if (paramSeconds == null) {
/* 440 */       return getValue() > 0;
/*     */     }
/* 442 */     return getValue() > paramSeconds.getValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isLessThan(Seconds paramSeconds)
/*     */   {
/* 452 */     if (paramSeconds == null) {
/* 453 */       return getValue() < 0;
/*     */     }
/* 455 */     return getValue() < paramSeconds.getValue();
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
/* 468 */     return "PT" + String.valueOf(getValue()) + "S";
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\Seconds.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */