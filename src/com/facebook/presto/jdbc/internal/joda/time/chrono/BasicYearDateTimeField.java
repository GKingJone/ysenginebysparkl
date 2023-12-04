/*     */ package com.facebook.presto.jdbc.internal.joda.time.chrono;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.FieldUtils;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.ImpreciseDateTimeField;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class BasicYearDateTimeField
/*     */   extends ImpreciseDateTimeField
/*     */ {
/*     */   private static final long serialVersionUID = -98628754872287L;
/*     */   protected final BasicChronology iChronology;
/*     */   
/*     */   BasicYearDateTimeField(BasicChronology paramBasicChronology)
/*     */   {
/*  46 */     super(DateTimeFieldType.year(), paramBasicChronology.getAverageMillisPerYear());
/*  47 */     this.iChronology = paramBasicChronology;
/*     */   }
/*     */   
/*     */   public boolean isLenient() {
/*  51 */     return false;
/*     */   }
/*     */   
/*     */   public int get(long paramLong) {
/*  55 */     return this.iChronology.getYear(paramLong);
/*     */   }
/*     */   
/*     */   public long add(long paramLong, int paramInt) {
/*  59 */     if (paramInt == 0) {
/*  60 */       return paramLong;
/*     */     }
/*  62 */     int i = get(paramLong);
/*  63 */     int j = FieldUtils.safeAdd(i, paramInt);
/*  64 */     return set(paramLong, j);
/*     */   }
/*     */   
/*     */   public long add(long paramLong1, long paramLong2) {
/*  68 */     return add(paramLong1, FieldUtils.safeToInt(paramLong2));
/*     */   }
/*     */   
/*     */   public long addWrapField(long paramLong, int paramInt) {
/*  72 */     if (paramInt == 0) {
/*  73 */       return paramLong;
/*     */     }
/*     */     
/*  76 */     int i = this.iChronology.getYear(paramLong);
/*  77 */     int j = FieldUtils.getWrappedValue(i, paramInt, this.iChronology.getMinYear(), this.iChronology.getMaxYear());
/*     */     
/*  79 */     return set(paramLong, j);
/*     */   }
/*     */   
/*     */   public long set(long paramLong, int paramInt) {
/*  83 */     FieldUtils.verifyValueBounds(this, paramInt, this.iChronology.getMinYear(), this.iChronology.getMaxYear());
/*     */     
/*  85 */     return this.iChronology.setYear(paramLong, paramInt);
/*     */   }
/*     */   
/*     */   public long getDifferenceAsLong(long paramLong1, long paramLong2) {
/*  89 */     if (paramLong1 < paramLong2) {
/*  90 */       return -this.iChronology.getYearDifference(paramLong2, paramLong1);
/*     */     }
/*  92 */     return this.iChronology.getYearDifference(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */   public DurationField getRangeDurationField() {
/*  96 */     return null;
/*     */   }
/*     */   
/*     */   public boolean isLeap(long paramLong) {
/* 100 */     return this.iChronology.isLeapYear(get(paramLong));
/*     */   }
/*     */   
/*     */   public int getLeapAmount(long paramLong) {
/* 104 */     if (this.iChronology.isLeapYear(get(paramLong))) {
/* 105 */       return 1;
/*     */     }
/* 107 */     return 0;
/*     */   }
/*     */   
/*     */   public DurationField getLeapDurationField()
/*     */   {
/* 112 */     return this.iChronology.days();
/*     */   }
/*     */   
/*     */   public int getMinimumValue() {
/* 116 */     return this.iChronology.getMinYear();
/*     */   }
/*     */   
/*     */   public int getMaximumValue() {
/* 120 */     return this.iChronology.getMaxYear();
/*     */   }
/*     */   
/*     */   public long roundFloor(long paramLong) {
/* 124 */     return this.iChronology.getYearMillis(get(paramLong));
/*     */   }
/*     */   
/*     */   public long roundCeiling(long paramLong) {
/* 128 */     int i = get(paramLong);
/* 129 */     long l = this.iChronology.getYearMillis(i);
/* 130 */     if (paramLong != l)
/*     */     {
/* 132 */       paramLong = this.iChronology.getYearMillis(i + 1);
/*     */     }
/* 134 */     return paramLong;
/*     */   }
/*     */   
/*     */   public long remainder(long paramLong) {
/* 138 */     return paramLong - roundFloor(paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Object readResolve()
/*     */   {
/* 145 */     return this.iChronology.year();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\BasicYearDateTimeField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */