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
/*     */ 
/*     */ final class BasicWeekOfWeekyearDateTimeField
/*     */   extends PreciseDurationDateTimeField
/*     */ {
/*     */   private static final long serialVersionUID = -1587436826395135328L;
/*     */   private final BasicChronology iChronology;
/*     */   
/*     */   BasicWeekOfWeekyearDateTimeField(BasicChronology paramBasicChronology, DurationField paramDurationField)
/*     */   {
/*  43 */     super(DateTimeFieldType.weekOfWeekyear(), paramDurationField);
/*  44 */     this.iChronology = paramBasicChronology;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int get(long paramLong)
/*     */   {
/*  55 */     return this.iChronology.getWeekOfWeekyear(paramLong);
/*     */   }
/*     */   
/*     */   public DurationField getRangeDurationField() {
/*  59 */     return this.iChronology.weekyears();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long roundFloor(long paramLong)
/*     */   {
/*  66 */     return super.roundFloor(paramLong + 259200000L) - 259200000L;
/*     */   }
/*     */   
/*     */   public long roundCeiling(long paramLong)
/*     */   {
/*  71 */     return super.roundCeiling(paramLong + 259200000L) - 259200000L;
/*     */   }
/*     */   
/*     */   public long remainder(long paramLong)
/*     */   {
/*  76 */     return super.remainder(paramLong + 259200000L);
/*     */   }
/*     */   
/*     */   public int getMinimumValue() {
/*  80 */     return 1;
/*     */   }
/*     */   
/*     */   public int getMaximumValue() {
/*  84 */     return 53;
/*     */   }
/*     */   
/*     */   public int getMaximumValue(long paramLong) {
/*  88 */     int i = this.iChronology.getWeekyear(paramLong);
/*  89 */     return this.iChronology.getWeeksInYear(i);
/*     */   }
/*     */   
/*     */   public int getMaximumValue(ReadablePartial paramReadablePartial) {
/*  93 */     if (paramReadablePartial.isSupported(DateTimeFieldType.weekyear())) {
/*  94 */       int i = paramReadablePartial.get(DateTimeFieldType.weekyear());
/*  95 */       return this.iChronology.getWeeksInYear(i);
/*     */     }
/*  97 */     return 53;
/*     */   }
/*     */   
/*     */   public int getMaximumValue(ReadablePartial paramReadablePartial, int[] paramArrayOfInt) {
/* 101 */     int i = paramReadablePartial.size();
/* 102 */     for (int j = 0; j < i; j++) {
/* 103 */       if (paramReadablePartial.getFieldType(j) == DateTimeFieldType.weekyear()) {
/* 104 */         int k = paramArrayOfInt[j];
/* 105 */         return this.iChronology.getWeeksInYear(k);
/*     */       }
/*     */     }
/* 108 */     return 53;
/*     */   }
/*     */   
/*     */   protected int getMaximumValueForSet(long paramLong, int paramInt) {
/* 112 */     return paramInt > 52 ? getMaximumValue(paramLong) : 52;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Object readResolve()
/*     */   {
/* 119 */     return this.iChronology.weekOfWeekyear();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\BasicWeekOfWeekyearDateTimeField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */