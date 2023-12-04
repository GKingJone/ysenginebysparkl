/*     */ package com.facebook.presto.jdbc.internal.joda.time.chrono;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePartial;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.DecoratedDateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.FieldUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class GJYearOfEraDateTimeField
/*     */   extends DecoratedDateTimeField
/*     */ {
/*     */   private static final long serialVersionUID = -5961050944769862059L;
/*     */   private final BasicChronology iChronology;
/*     */   
/*     */   GJYearOfEraDateTimeField(DateTimeField paramDateTimeField, BasicChronology paramBasicChronology)
/*     */   {
/*  42 */     super(paramDateTimeField, DateTimeFieldType.yearOfEra());
/*  43 */     this.iChronology = paramBasicChronology;
/*     */   }
/*     */   
/*     */   public DurationField getRangeDurationField()
/*     */   {
/*  48 */     return this.iChronology.eras();
/*     */   }
/*     */   
/*     */   public int get(long paramLong) {
/*  52 */     int i = getWrappedField().get(paramLong);
/*  53 */     if (i <= 0) {
/*  54 */       i = 1 - i;
/*     */     }
/*  56 */     return i;
/*     */   }
/*     */   
/*     */   public long add(long paramLong, int paramInt) {
/*  60 */     return getWrappedField().add(paramLong, paramInt);
/*     */   }
/*     */   
/*     */   public long add(long paramLong1, long paramLong2) {
/*  64 */     return getWrappedField().add(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */   public long addWrapField(long paramLong, int paramInt) {
/*  68 */     return getWrappedField().addWrapField(paramLong, paramInt);
/*     */   }
/*     */   
/*     */   public int[] addWrapField(ReadablePartial paramReadablePartial, int paramInt1, int[] paramArrayOfInt, int paramInt2) {
/*  72 */     return getWrappedField().addWrapField(paramReadablePartial, paramInt1, paramArrayOfInt, paramInt2);
/*     */   }
/*     */   
/*     */   public int getDifference(long paramLong1, long paramLong2) {
/*  76 */     return getWrappedField().getDifference(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */   public long getDifferenceAsLong(long paramLong1, long paramLong2) {
/*  80 */     return getWrappedField().getDifferenceAsLong(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long set(long paramLong, int paramInt)
/*     */   {
/*  92 */     FieldUtils.verifyValueBounds(this, paramInt, 1, getMaximumValue());
/*  93 */     if (this.iChronology.getYear(paramLong) <= 0) {
/*  94 */       paramInt = 1 - paramInt;
/*     */     }
/*  96 */     return super.set(paramLong, paramInt);
/*     */   }
/*     */   
/*     */   public int getMinimumValue() {
/* 100 */     return 1;
/*     */   }
/*     */   
/*     */   public int getMaximumValue() {
/* 104 */     return getWrappedField().getMaximumValue();
/*     */   }
/*     */   
/*     */   public long roundFloor(long paramLong) {
/* 108 */     return getWrappedField().roundFloor(paramLong);
/*     */   }
/*     */   
/*     */   public long roundCeiling(long paramLong) {
/* 112 */     return getWrappedField().roundCeiling(paramLong);
/*     */   }
/*     */   
/*     */   public long remainder(long paramLong) {
/* 116 */     return getWrappedField().remainder(paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Object readResolve()
/*     */   {
/* 123 */     return this.iChronology.yearOfEra();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\GJYearOfEraDateTimeField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */