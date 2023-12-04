/*     */ package com.facebook.presto.jdbc.internal.joda.time.chrono;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePartial;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.PreciseDurationDateTimeField;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class BasicDayOfMonthDateTimeField
/*     */   extends PreciseDurationDateTimeField
/*     */ {
/*     */   private static final long serialVersionUID = -4677223814028011723L;
/*     */   private final BasicChronology iChronology;
/*     */   
/*     */   BasicDayOfMonthDateTimeField(BasicChronology paramBasicChronology, DurationField paramDurationField)
/*     */   {
/*  42 */     super(DateTimeFieldType.dayOfMonth(), paramDurationField);
/*  43 */     this.iChronology = paramBasicChronology;
/*     */   }
/*     */   
/*     */   public int get(long paramLong)
/*     */   {
/*  48 */     return this.iChronology.getDayOfMonth(paramLong);
/*     */   }
/*     */   
/*     */   public DurationField getRangeDurationField() {
/*  52 */     return this.iChronology.months();
/*     */   }
/*     */   
/*     */   public int getMinimumValue() {
/*  56 */     return 1;
/*     */   }
/*     */   
/*     */   public int getMaximumValue() {
/*  60 */     return this.iChronology.getDaysInMonthMax();
/*     */   }
/*     */   
/*     */   public int getMaximumValue(long paramLong) {
/*  64 */     return this.iChronology.getDaysInMonthMax(paramLong);
/*     */   }
/*     */   
/*     */   public int getMaximumValue(ReadablePartial paramReadablePartial) {
/*  68 */     if (paramReadablePartial.isSupported(DateTimeFieldType.monthOfYear())) {
/*  69 */       int i = paramReadablePartial.get(DateTimeFieldType.monthOfYear());
/*  70 */       if (paramReadablePartial.isSupported(DateTimeFieldType.year())) {
/*  71 */         int j = paramReadablePartial.get(DateTimeFieldType.year());
/*  72 */         return this.iChronology.getDaysInYearMonth(j, i);
/*     */       }
/*  74 */       return this.iChronology.getDaysInMonthMax(i);
/*     */     }
/*  76 */     return getMaximumValue();
/*     */   }
/*     */   
/*     */   public int getMaximumValue(ReadablePartial paramReadablePartial, int[] paramArrayOfInt) {
/*  80 */     int i = paramReadablePartial.size();
/*  81 */     for (int j = 0; j < i; j++) {
/*  82 */       if (paramReadablePartial.getFieldType(j) == DateTimeFieldType.monthOfYear()) {
/*  83 */         int k = paramArrayOfInt[j];
/*  84 */         for (int m = 0; m < i; m++) {
/*  85 */           if (paramReadablePartial.getFieldType(m) == DateTimeFieldType.year()) {
/*  86 */             int n = paramArrayOfInt[m];
/*  87 */             return this.iChronology.getDaysInYearMonth(n, k);
/*     */           }
/*     */         }
/*  90 */         return this.iChronology.getDaysInMonthMax(k);
/*     */       }
/*     */     }
/*  93 */     return getMaximumValue();
/*     */   }
/*     */   
/*     */   protected int getMaximumValueForSet(long paramLong, int paramInt) {
/*  97 */     return this.iChronology.getDaysInMonthMaxForSet(paramLong, paramInt);
/*     */   }
/*     */   
/*     */   public boolean isLeap(long paramLong)
/*     */   {
/* 102 */     return this.iChronology.isLeapDay(paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Object readResolve()
/*     */   {
/* 109 */     return this.iChronology.dayOfMonth();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\BasicDayOfMonthDateTimeField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */