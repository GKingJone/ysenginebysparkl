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
/*     */ public final class Years
/*     */   extends BaseSingleFieldPeriod
/*     */ {
/*  45 */   public static final Years ZERO = new Years(0);
/*     */   
/*  47 */   public static final Years ONE = new Years(1);
/*     */   
/*  49 */   public static final Years TWO = new Years(2);
/*     */   
/*  51 */   public static final Years THREE = new Years(3);
/*     */   
/*  53 */   public static final Years MAX_VALUE = new Years(Integer.MAX_VALUE);
/*     */   
/*  55 */   public static final Years MIN_VALUE = new Years(Integer.MIN_VALUE);
/*     */   
/*     */ 
/*  58 */   private static final PeriodFormatter PARSER = ISOPeriodFormat.standard().withParseType(PeriodType.years());
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final long serialVersionUID = 87525275727380868L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Years years(int paramInt)
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
/*  86 */     return new Years(paramInt);
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
/*     */   public static Years yearsBetween(ReadableInstant paramReadableInstant1, ReadableInstant paramReadableInstant2)
/*     */   {
/* 102 */     int i = BaseSingleFieldPeriod.between(paramReadableInstant1, paramReadableInstant2, DurationFieldType.years());
/* 103 */     return years(i);
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
/*     */   public static Years yearsBetween(ReadablePartial paramReadablePartial1, ReadablePartial paramReadablePartial2)
/*     */   {
/* 119 */     if (((paramReadablePartial1 instanceof LocalDate)) && ((paramReadablePartial2 instanceof LocalDate))) {
/* 120 */       Chronology localChronology = DateTimeUtils.getChronology(paramReadablePartial1.getChronology());
/* 121 */       int j = localChronology.years().getDifference(((LocalDate)paramReadablePartial2).getLocalMillis(), ((LocalDate)paramReadablePartial1).getLocalMillis());
/*     */       
/* 123 */       return years(j);
/*     */     }
/* 125 */     int i = BaseSingleFieldPeriod.between(paramReadablePartial1, paramReadablePartial2, ZERO);
/* 126 */     return years(i);
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
/*     */   public static Years yearsIn(ReadableInterval paramReadableInterval)
/*     */   {
/* 139 */     if (paramReadableInterval == null) {
/* 140 */       return ZERO;
/*     */     }
/* 142 */     int i = BaseSingleFieldPeriod.between(paramReadableInterval.getStart(), paramReadableInterval.getEnd(), DurationFieldType.years());
/* 143 */     return years(i);
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
/*     */   public static Years parseYears(String paramString)
/*     */   {
/* 159 */     if (paramString == null) {
/* 160 */       return ZERO;
/*     */     }
/* 162 */     Period localPeriod = PARSER.parsePeriod(paramString);
/* 163 */     return years(localPeriod.getYears());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Years(int paramInt)
/*     */   {
/* 175 */     super(paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Object readResolve()
/*     */   {
/* 184 */     return years(getValue());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationFieldType getFieldType()
/*     */   {
/* 194 */     return DurationFieldType.years();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PeriodType getPeriodType()
/*     */   {
/* 203 */     return PeriodType.years();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getYears()
/*     */   {
/* 213 */     return getValue();
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
/*     */   public Years plus(int paramInt)
/*     */   {
/* 227 */     if (paramInt == 0) {
/* 228 */       return this;
/*     */     }
/* 230 */     return years(FieldUtils.safeAdd(getValue(), paramInt));
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
/*     */   public Years plus(Years paramYears)
/*     */   {
/* 243 */     if (paramYears == null) {
/* 244 */       return this;
/*     */     }
/* 246 */     return plus(paramYears.getValue());
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
/*     */   public Years minus(int paramInt)
/*     */   {
/* 260 */     return plus(FieldUtils.safeNegate(paramInt));
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
/*     */   public Years minus(Years paramYears)
/*     */   {
/* 273 */     if (paramYears == null) {
/* 274 */       return this;
/*     */     }
/* 276 */     return minus(paramYears.getValue());
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
/*     */   public Years multipliedBy(int paramInt)
/*     */   {
/* 290 */     return years(FieldUtils.safeMultiply(getValue(), paramInt));
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
/*     */   public Years dividedBy(int paramInt)
/*     */   {
/* 304 */     if (paramInt == 1) {
/* 305 */       return this;
/*     */     }
/* 307 */     return years(getValue() / paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Years negated()
/*     */   {
/* 318 */     return years(FieldUtils.safeNegate(getValue()));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isGreaterThan(Years paramYears)
/*     */   {
/* 329 */     if (paramYears == null) {
/* 330 */       return getValue() > 0;
/*     */     }
/* 332 */     return getValue() > paramYears.getValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isLessThan(Years paramYears)
/*     */   {
/* 342 */     if (paramYears == null) {
/* 343 */       return getValue() < 0;
/*     */     }
/* 345 */     return getValue() < paramYears.getValue();
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
/* 358 */     return "P" + String.valueOf(getValue()) + "Y";
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\Years.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */