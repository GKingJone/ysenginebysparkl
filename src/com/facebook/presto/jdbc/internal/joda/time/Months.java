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
/*     */ public final class Months
/*     */   extends BaseSingleFieldPeriod
/*     */ {
/*  45 */   public static final Months ZERO = new Months(0);
/*     */   
/*  47 */   public static final Months ONE = new Months(1);
/*     */   
/*  49 */   public static final Months TWO = new Months(2);
/*     */   
/*  51 */   public static final Months THREE = new Months(3);
/*     */   
/*  53 */   public static final Months FOUR = new Months(4);
/*     */   
/*  55 */   public static final Months FIVE = new Months(5);
/*     */   
/*  57 */   public static final Months SIX = new Months(6);
/*     */   
/*  59 */   public static final Months SEVEN = new Months(7);
/*     */   
/*  61 */   public static final Months EIGHT = new Months(8);
/*     */   
/*  63 */   public static final Months NINE = new Months(9);
/*     */   
/*  65 */   public static final Months TEN = new Months(10);
/*     */   
/*  67 */   public static final Months ELEVEN = new Months(11);
/*     */   
/*  69 */   public static final Months TWELVE = new Months(12);
/*     */   
/*  71 */   public static final Months MAX_VALUE = new Months(Integer.MAX_VALUE);
/*     */   
/*  73 */   public static final Months MIN_VALUE = new Months(Integer.MIN_VALUE);
/*     */   
/*     */ 
/*  76 */   private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.months());
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final long serialVersionUID = 87525275727380867L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Months months(int paramInt)
/*     */   {
/*  90 */     switch (paramInt) {
/*     */     case 0: 
/*  92 */       return ZERO;
/*     */     case 1: 
/*  94 */       return ONE;
/*     */     case 2: 
/*  96 */       return TWO;
/*     */     case 3: 
/*  98 */       return THREE;
/*     */     case 4: 
/* 100 */       return FOUR;
/*     */     case 5: 
/* 102 */       return FIVE;
/*     */     case 6: 
/* 104 */       return SIX;
/*     */     case 7: 
/* 106 */       return SEVEN;
/*     */     case 8: 
/* 108 */       return EIGHT;
/*     */     case 9: 
/* 110 */       return NINE;
/*     */     case 10: 
/* 112 */       return TEN;
/*     */     case 11: 
/* 114 */       return ELEVEN;
/*     */     case 12: 
/* 116 */       return TWELVE;
/*     */     case 2147483647: 
/* 118 */       return MAX_VALUE;
/*     */     case -2147483648: 
/* 120 */       return MIN_VALUE;
/*     */     }
/* 122 */     return new Months(paramInt);
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
/*     */   public static Months monthsBetween(ReadableInstant paramReadableInstant1, ReadableInstant paramReadableInstant2)
/*     */   {
/* 138 */     int i = BaseSingleFieldPeriod.between(paramReadableInstant1, paramReadableInstant2, DurationFieldType.months());
/* 139 */     return months(i);
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
/*     */   public static Months monthsBetween(ReadablePartial paramReadablePartial1, ReadablePartial paramReadablePartial2)
/*     */   {
/* 155 */     if (((paramReadablePartial1 instanceof LocalDate)) && ((paramReadablePartial2 instanceof LocalDate))) {
/* 156 */       Chronology localChronology = DateTimeUtils.getChronology(paramReadablePartial1.getChronology());
/* 157 */       int j = localChronology.months().getDifference(((LocalDate)paramReadablePartial2).getLocalMillis(), ((LocalDate)paramReadablePartial1).getLocalMillis());
/*     */       
/* 159 */       return months(j);
/*     */     }
/* 161 */     int i = BaseSingleFieldPeriod.between(paramReadablePartial1, paramReadablePartial2, ZERO);
/* 162 */     return months(i);
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
/*     */   public static Months monthsIn(ReadableInterval paramReadableInterval)
/*     */   {
/* 175 */     if (paramReadableInterval == null) {
/* 176 */       return ZERO;
/*     */     }
/* 178 */     int i = BaseSingleFieldPeriod.between(paramReadableInterval.getStart(), paramReadableInterval.getEnd(), DurationFieldType.months());
/* 179 */     return months(i);
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
/*     */   public static Months parseMonths(String paramString)
/*     */   {
/* 195 */     if (paramString == null) {
/* 196 */       return ZERO;
/*     */     }
/* 198 */     Period localPeriod = PARSER.parsePeriod(paramString);
/* 199 */     return months(localPeriod.getMonths());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Months(int paramInt)
/*     */   {
/* 211 */     super(paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Object readResolve()
/*     */   {
/* 220 */     return months(getValue());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationFieldType getFieldType()
/*     */   {
/* 230 */     return DurationFieldType.months();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PeriodType getPeriodType()
/*     */   {
/* 239 */     return PeriodType.months();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMonths()
/*     */   {
/* 249 */     return getValue();
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
/*     */   public Months plus(int paramInt)
/*     */   {
/* 263 */     if (paramInt == 0) {
/* 264 */       return this;
/*     */     }
/* 266 */     return months(FieldUtils.safeAdd(getValue(), paramInt));
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
/*     */   public Months plus(Months paramMonths)
/*     */   {
/* 279 */     if (paramMonths == null) {
/* 280 */       return this;
/*     */     }
/* 282 */     return plus(paramMonths.getValue());
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
/*     */   public Months minus(int paramInt)
/*     */   {
/* 296 */     return plus(FieldUtils.safeNegate(paramInt));
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
/*     */   public Months minus(Months paramMonths)
/*     */   {
/* 309 */     if (paramMonths == null) {
/* 310 */       return this;
/*     */     }
/* 312 */     return minus(paramMonths.getValue());
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
/*     */   public Months multipliedBy(int paramInt)
/*     */   {
/* 326 */     return months(FieldUtils.safeMultiply(getValue(), paramInt));
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
/*     */   public Months dividedBy(int paramInt)
/*     */   {
/* 340 */     if (paramInt == 1) {
/* 341 */       return this;
/*     */     }
/* 343 */     return months(getValue() / paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Months negated()
/*     */   {
/* 354 */     return months(FieldUtils.safeNegate(getValue()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isGreaterThan(Months paramMonths)
/*     */   {
/* 365 */     if (paramMonths == null) {
/* 366 */       return getValue() > 0;
/*     */     }
/* 368 */     return getValue() > paramMonths.getValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isLessThan(Months paramMonths)
/*     */   {
/* 378 */     if (paramMonths == null) {
/* 379 */       return getValue() < 0;
/*     */     }
/* 381 */     return getValue() < paramMonths.getValue();
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
/* 394 */     return "P" + String.valueOf(getValue()) + "M";
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\Months.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */