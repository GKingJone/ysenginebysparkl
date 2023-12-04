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
/*     */ public final class Hours
/*     */   extends BaseSingleFieldPeriod
/*     */ {
/*  45 */   public static final Hours ZERO = new Hours(0);
/*     */   
/*  47 */   public static final Hours ONE = new Hours(1);
/*     */   
/*  49 */   public static final Hours TWO = new Hours(2);
/*     */   
/*  51 */   public static final Hours THREE = new Hours(3);
/*     */   
/*  53 */   public static final Hours FOUR = new Hours(4);
/*     */   
/*  55 */   public static final Hours FIVE = new Hours(5);
/*     */   
/*  57 */   public static final Hours SIX = new Hours(6);
/*     */   
/*  59 */   public static final Hours SEVEN = new Hours(7);
/*     */   
/*  61 */   public static final Hours EIGHT = new Hours(8);
/*     */   
/*  63 */   public static final Hours MAX_VALUE = new Hours(Integer.MAX_VALUE);
/*     */   
/*  65 */   public static final Hours MIN_VALUE = new Hours(Integer.MIN_VALUE);
/*     */   
/*     */ 
/*  68 */   private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.hours());
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final long serialVersionUID = 87525275727380864L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Hours hours(int paramInt)
/*     */   {
/*  82 */     switch (paramInt) {
/*     */     case 0: 
/*  84 */       return ZERO;
/*     */     case 1: 
/*  86 */       return ONE;
/*     */     case 2: 
/*  88 */       return TWO;
/*     */     case 3: 
/*  90 */       return THREE;
/*     */     case 4: 
/*  92 */       return FOUR;
/*     */     case 5: 
/*  94 */       return FIVE;
/*     */     case 6: 
/*  96 */       return SIX;
/*     */     case 7: 
/*  98 */       return SEVEN;
/*     */     case 8: 
/* 100 */       return EIGHT;
/*     */     case 2147483647: 
/* 102 */       return MAX_VALUE;
/*     */     case -2147483648: 
/* 104 */       return MIN_VALUE;
/*     */     }
/* 106 */     return new Hours(paramInt);
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
/*     */   public static Hours hoursBetween(ReadableInstant paramReadableInstant1, ReadableInstant paramReadableInstant2)
/*     */   {
/* 121 */     int i = BaseSingleFieldPeriod.between(paramReadableInstant1, paramReadableInstant2, DurationFieldType.hours());
/* 122 */     return hours(i);
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
/*     */   public static Hours hoursBetween(ReadablePartial paramReadablePartial1, ReadablePartial paramReadablePartial2)
/*     */   {
/* 138 */     if (((paramReadablePartial1 instanceof LocalTime)) && ((paramReadablePartial2 instanceof LocalTime))) {
/* 139 */       Chronology localChronology = DateTimeUtils.getChronology(paramReadablePartial1.getChronology());
/* 140 */       int j = localChronology.hours().getDifference(((LocalTime)paramReadablePartial2).getLocalMillis(), ((LocalTime)paramReadablePartial1).getLocalMillis());
/*     */       
/* 142 */       return hours(j);
/*     */     }
/* 144 */     int i = BaseSingleFieldPeriod.between(paramReadablePartial1, paramReadablePartial2, ZERO);
/* 145 */     return hours(i);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Hours hoursIn(ReadableInterval paramReadableInterval)
/*     */   {
/* 157 */     if (paramReadableInterval == null) {
/* 158 */       return ZERO;
/*     */     }
/* 160 */     int i = BaseSingleFieldPeriod.between(paramReadableInterval.getStart(), paramReadableInterval.getEnd(), DurationFieldType.hours());
/* 161 */     return hours(i);
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
/*     */   public static Hours standardHoursIn(ReadablePeriod paramReadablePeriod)
/*     */   {
/* 185 */     int i = BaseSingleFieldPeriod.standardPeriodIn(paramReadablePeriod, 3600000L);
/* 186 */     return hours(i);
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
/*     */   public static Hours parseHours(String paramString)
/*     */   {
/* 202 */     if (paramString == null) {
/* 203 */       return ZERO;
/*     */     }
/* 205 */     Period localPeriod = PARSER.parsePeriod(paramString);
/* 206 */     return hours(localPeriod.getHours());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Hours(int paramInt)
/*     */   {
/* 218 */     super(paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Object readResolve()
/*     */   {
/* 227 */     return hours(getValue());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationFieldType getFieldType()
/*     */   {
/* 237 */     return DurationFieldType.hours();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PeriodType getPeriodType()
/*     */   {
/* 246 */     return PeriodType.hours();
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
/* 264 */     return Weeks.weeks(getValue() / 168);
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
/*     */   public Days toStandardDays()
/*     */   {
/* 280 */     return Days.days(getValue() / 24);
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
/* 296 */     return Minutes.minutes(FieldUtils.safeMultiply(getValue(), 60));
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
/* 313 */     return Seconds.seconds(FieldUtils.safeMultiply(getValue(), 3600));
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
/* 331 */     long l = getValue();
/* 332 */     return new Duration(l * 3600000L);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getHours()
/*     */   {
/* 342 */     return getValue();
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
/*     */   public Hours plus(int paramInt)
/*     */   {
/* 356 */     if (paramInt == 0) {
/* 357 */       return this;
/*     */     }
/* 359 */     return hours(FieldUtils.safeAdd(getValue(), paramInt));
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
/*     */   public Hours plus(Hours paramHours)
/*     */   {
/* 372 */     if (paramHours == null) {
/* 373 */       return this;
/*     */     }
/* 375 */     return plus(paramHours.getValue());
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
/*     */   public Hours minus(int paramInt)
/*     */   {
/* 389 */     return plus(FieldUtils.safeNegate(paramInt));
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
/*     */   public Hours minus(Hours paramHours)
/*     */   {
/* 402 */     if (paramHours == null) {
/* 403 */       return this;
/*     */     }
/* 405 */     return minus(paramHours.getValue());
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
/*     */   public Hours multipliedBy(int paramInt)
/*     */   {
/* 419 */     return hours(FieldUtils.safeMultiply(getValue(), paramInt));
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
/*     */   public Hours dividedBy(int paramInt)
/*     */   {
/* 433 */     if (paramInt == 1) {
/* 434 */       return this;
/*     */     }
/* 436 */     return hours(getValue() / paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Hours negated()
/*     */   {
/* 447 */     return hours(FieldUtils.safeNegate(getValue()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isGreaterThan(Hours paramHours)
/*     */   {
/* 458 */     if (paramHours == null) {
/* 459 */       return getValue() > 0;
/*     */     }
/* 461 */     return getValue() > paramHours.getValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isLessThan(Hours paramHours)
/*     */   {
/* 471 */     if (paramHours == null) {
/* 472 */       return getValue() < 0;
/*     */     }
/* 474 */     return getValue() < paramHours.getValue();
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
/* 487 */     return "PT" + String.valueOf(getValue()) + "H";
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\Hours.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */