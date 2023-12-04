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
/*     */ public final class Weeks
/*     */   extends BaseSingleFieldPeriod
/*     */ {
/*  45 */   public static final Weeks ZERO = new Weeks(0);
/*     */   
/*  47 */   public static final Weeks ONE = new Weeks(1);
/*     */   
/*  49 */   public static final Weeks TWO = new Weeks(2);
/*     */   
/*  51 */   public static final Weeks THREE = new Weeks(3);
/*     */   
/*  53 */   public static final Weeks MAX_VALUE = new Weeks(Integer.MAX_VALUE);
/*     */   
/*  55 */   public static final Weeks MIN_VALUE = new Weeks(Integer.MIN_VALUE);
/*     */   
/*     */ 
/*  58 */   private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.weeks());
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final long serialVersionUID = 87525275727380866L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Weeks weeks(int paramInt)
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
/*  86 */     return new Weeks(paramInt);
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
/*     */   public static Weeks weeksBetween(ReadableInstant paramReadableInstant1, ReadableInstant paramReadableInstant2)
/*     */   {
/* 101 */     int i = BaseSingleFieldPeriod.between(paramReadableInstant1, paramReadableInstant2, DurationFieldType.weeks());
/* 102 */     return weeks(i);
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
/*     */   public static Weeks weeksBetween(ReadablePartial paramReadablePartial1, ReadablePartial paramReadablePartial2)
/*     */   {
/* 118 */     if (((paramReadablePartial1 instanceof LocalDate)) && ((paramReadablePartial2 instanceof LocalDate))) {
/* 119 */       Chronology localChronology = DateTimeUtils.getChronology(paramReadablePartial1.getChronology());
/* 120 */       int j = localChronology.weeks().getDifference(((LocalDate)paramReadablePartial2).getLocalMillis(), ((LocalDate)paramReadablePartial1).getLocalMillis());
/*     */       
/* 122 */       return weeks(j);
/*     */     }
/* 124 */     int i = BaseSingleFieldPeriod.between(paramReadablePartial1, paramReadablePartial2, ZERO);
/* 125 */     return weeks(i);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Weeks weeksIn(ReadableInterval paramReadableInterval)
/*     */   {
/* 137 */     if (paramReadableInterval == null) {
/* 138 */       return ZERO;
/*     */     }
/* 140 */     int i = BaseSingleFieldPeriod.between(paramReadableInterval.getStart(), paramReadableInterval.getEnd(), DurationFieldType.weeks());
/* 141 */     return weeks(i);
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
/*     */   public static Weeks standardWeeksIn(ReadablePeriod paramReadablePeriod)
/*     */   {
/* 165 */     int i = BaseSingleFieldPeriod.standardPeriodIn(paramReadablePeriod, 604800000L);
/* 166 */     return weeks(i);
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
/*     */   public static Weeks parseWeeks(String paramString)
/*     */   {
/* 182 */     if (paramString == null) {
/* 183 */       return ZERO;
/*     */     }
/* 185 */     Period localPeriod = PARSER.parsePeriod(paramString);
/* 186 */     return weeks(localPeriod.getWeeks());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Weeks(int paramInt)
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
/* 207 */     return weeks(getValue());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationFieldType getFieldType()
/*     */   {
/* 217 */     return DurationFieldType.weeks();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PeriodType getPeriodType()
/*     */   {
/* 226 */     return PeriodType.weeks();
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
/*     */   public Days toStandardDays()
/*     */   {
/* 244 */     return Days.days(FieldUtils.safeMultiply(getValue(), 7));
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
/*     */   public Hours toStandardHours()
/*     */   {
/* 262 */     return Hours.hours(FieldUtils.safeMultiply(getValue(), 168));
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
/* 280 */     return Minutes.minutes(FieldUtils.safeMultiply(getValue(), 10080));
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
/*     */   public Seconds toStandardSeconds()
/*     */   {
/* 299 */     return Seconds.seconds(FieldUtils.safeMultiply(getValue(), 604800));
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
/*     */   public Duration toStandardDuration()
/*     */   {
/* 318 */     long l = getValue();
/* 319 */     return new Duration(l * 604800000L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getWeeks()
/*     */   {
/* 329 */     return getValue();
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
/*     */   public Weeks plus(int paramInt)
/*     */   {
/* 343 */     if (paramInt == 0) {
/* 344 */       return this;
/*     */     }
/* 346 */     return weeks(FieldUtils.safeAdd(getValue(), paramInt));
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
/*     */   public Weeks plus(Weeks paramWeeks)
/*     */   {
/* 359 */     if (paramWeeks == null) {
/* 360 */       return this;
/*     */     }
/* 362 */     return plus(paramWeeks.getValue());
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
/*     */   public Weeks minus(int paramInt)
/*     */   {
/* 376 */     return plus(FieldUtils.safeNegate(paramInt));
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
/*     */   public Weeks minus(Weeks paramWeeks)
/*     */   {
/* 389 */     if (paramWeeks == null) {
/* 390 */       return this;
/*     */     }
/* 392 */     return minus(paramWeeks.getValue());
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
/*     */   public Weeks multipliedBy(int paramInt)
/*     */   {
/* 406 */     return weeks(FieldUtils.safeMultiply(getValue(), paramInt));
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
/*     */   public Weeks dividedBy(int paramInt)
/*     */   {
/* 420 */     if (paramInt == 1) {
/* 421 */       return this;
/*     */     }
/* 423 */     return weeks(getValue() / paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Weeks negated()
/*     */   {
/* 434 */     return weeks(FieldUtils.safeNegate(getValue()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isGreaterThan(Weeks paramWeeks)
/*     */   {
/* 445 */     if (paramWeeks == null) {
/* 446 */       return getValue() > 0;
/*     */     }
/* 448 */     return getValue() > paramWeeks.getValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isLessThan(Weeks paramWeeks)
/*     */   {
/* 458 */     if (paramWeeks == null) {
/* 459 */       return getValue() < 0;
/*     */     }
/* 461 */     return getValue() < paramWeeks.getValue();
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
/* 474 */     return "P" + String.valueOf(getValue()) + "W";
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\Weeks.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */