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
/*     */ public final class Days
/*     */   extends BaseSingleFieldPeriod
/*     */ {
/*  45 */   public static final Days ZERO = new Days(0);
/*     */   
/*  47 */   public static final Days ONE = new Days(1);
/*     */   
/*  49 */   public static final Days TWO = new Days(2);
/*     */   
/*  51 */   public static final Days THREE = new Days(3);
/*     */   
/*  53 */   public static final Days FOUR = new Days(4);
/*     */   
/*  55 */   public static final Days FIVE = new Days(5);
/*     */   
/*  57 */   public static final Days SIX = new Days(6);
/*     */   
/*  59 */   public static final Days SEVEN = new Days(7);
/*     */   
/*  61 */   public static final Days MAX_VALUE = new Days(Integer.MAX_VALUE);
/*     */   
/*  63 */   public static final Days MIN_VALUE = new Days(Integer.MIN_VALUE);
/*     */   
/*     */ 
/*  66 */   private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.days());
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final long serialVersionUID = 87525275727380865L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Days days(int paramInt)
/*     */   {
/*  80 */     switch (paramInt) {
/*     */     case 0: 
/*  82 */       return ZERO;
/*     */     case 1: 
/*  84 */       return ONE;
/*     */     case 2: 
/*  86 */       return TWO;
/*     */     case 3: 
/*  88 */       return THREE;
/*     */     case 4: 
/*  90 */       return FOUR;
/*     */     case 5: 
/*  92 */       return FIVE;
/*     */     case 6: 
/*  94 */       return SIX;
/*     */     case 7: 
/*  96 */       return SEVEN;
/*     */     case 2147483647: 
/*  98 */       return MAX_VALUE;
/*     */     case -2147483648: 
/* 100 */       return MIN_VALUE;
/*     */     }
/* 102 */     return new Days(paramInt);
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
/*     */   public static Days daysBetween(ReadableInstant paramReadableInstant1, ReadableInstant paramReadableInstant2)
/*     */   {
/* 118 */     int i = BaseSingleFieldPeriod.between(paramReadableInstant1, paramReadableInstant2, DurationFieldType.days());
/* 119 */     return days(i);
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
/*     */   public static Days daysBetween(ReadablePartial paramReadablePartial1, ReadablePartial paramReadablePartial2)
/*     */   {
/* 135 */     if (((paramReadablePartial1 instanceof LocalDate)) && ((paramReadablePartial2 instanceof LocalDate))) {
/* 136 */       Chronology localChronology = DateTimeUtils.getChronology(paramReadablePartial1.getChronology());
/* 137 */       int j = localChronology.days().getDifference(((LocalDate)paramReadablePartial2).getLocalMillis(), ((LocalDate)paramReadablePartial1).getLocalMillis());
/*     */       
/* 139 */       return days(j);
/*     */     }
/* 141 */     int i = BaseSingleFieldPeriod.between(paramReadablePartial1, paramReadablePartial2, ZERO);
/* 142 */     return days(i);
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
/*     */   public static Days daysIn(ReadableInterval paramReadableInterval)
/*     */   {
/* 155 */     if (paramReadableInterval == null) {
/* 156 */       return ZERO;
/*     */     }
/* 158 */     int i = BaseSingleFieldPeriod.between(paramReadableInterval.getStart(), paramReadableInterval.getEnd(), DurationFieldType.days());
/* 159 */     return days(i);
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
/*     */   public static Days standardDaysIn(ReadablePeriod paramReadablePeriod)
/*     */   {
/* 183 */     int i = BaseSingleFieldPeriod.standardPeriodIn(paramReadablePeriod, 86400000L);
/* 184 */     return days(i);
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
/*     */   public static Days parseDays(String paramString)
/*     */   {
/* 200 */     if (paramString == null) {
/* 201 */       return ZERO;
/*     */     }
/* 203 */     Period localPeriod = PARSER.parsePeriod(paramString);
/* 204 */     return days(localPeriod.getDays());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Days(int paramInt)
/*     */   {
/* 216 */     super(paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Object readResolve()
/*     */   {
/* 225 */     return days(getValue());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationFieldType getFieldType()
/*     */   {
/* 235 */     return DurationFieldType.days();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PeriodType getPeriodType()
/*     */   {
/* 244 */     return PeriodType.days();
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
/*     */   public Weeks toStandardWeeks()
/*     */   {
/* 261 */     return Weeks.weeks(getValue() / 7);
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
/*     */   public Hours toStandardHours()
/*     */   {
/* 278 */     return Hours.hours(FieldUtils.safeMultiply(getValue(), 24));
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
/*     */   public Minutes toStandardMinutes()
/*     */   {
/* 296 */     return Minutes.minutes(FieldUtils.safeMultiply(getValue(), 1440));
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
/*     */   public Seconds toStandardSeconds()
/*     */   {
/* 314 */     return Seconds.seconds(FieldUtils.safeMultiply(getValue(), 86400));
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
/* 332 */     long l = getValue();
/* 333 */     return new Duration(l * 86400000L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getDays()
/*     */   {
/* 343 */     return getValue();
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
/*     */   public Days plus(int paramInt)
/*     */   {
/* 357 */     if (paramInt == 0) {
/* 358 */       return this;
/*     */     }
/* 360 */     return days(FieldUtils.safeAdd(getValue(), paramInt));
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
/*     */   public Days plus(Days paramDays)
/*     */   {
/* 373 */     if (paramDays == null) {
/* 374 */       return this;
/*     */     }
/* 376 */     return plus(paramDays.getValue());
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
/*     */   public Days minus(int paramInt)
/*     */   {
/* 390 */     return plus(FieldUtils.safeNegate(paramInt));
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
/*     */   public Days minus(Days paramDays)
/*     */   {
/* 403 */     if (paramDays == null) {
/* 404 */       return this;
/*     */     }
/* 406 */     return minus(paramDays.getValue());
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
/*     */   public Days multipliedBy(int paramInt)
/*     */   {
/* 420 */     return days(FieldUtils.safeMultiply(getValue(), paramInt));
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
/*     */   public Days dividedBy(int paramInt)
/*     */   {
/* 434 */     if (paramInt == 1) {
/* 435 */       return this;
/*     */     }
/* 437 */     return days(getValue() / paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Days negated()
/*     */   {
/* 448 */     return days(FieldUtils.safeNegate(getValue()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isGreaterThan(Days paramDays)
/*     */   {
/* 459 */     if (paramDays == null) {
/* 460 */       return getValue() > 0;
/*     */     }
/* 462 */     return getValue() > paramDays.getValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isLessThan(Days paramDays)
/*     */   {
/* 472 */     if (paramDays == null) {
/* 473 */       return getValue() < 0;
/*     */     }
/* 475 */     return getValue() < paramDays.getValue();
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
/* 488 */     return "P" + String.valueOf(getValue()) + "D";
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\Days.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */