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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ISOYearOfEraDateTimeField
/*     */   extends DecoratedDateTimeField
/*     */ {
/*     */   private static final long serialVersionUID = 7037524068969447317L;
/*  42 */   static final DateTimeField INSTANCE = new ISOYearOfEraDateTimeField();
/*     */   
/*     */ 
/*     */ 
/*     */   private ISOYearOfEraDateTimeField()
/*     */   {
/*  48 */     super(GregorianChronology.getInstanceUTC().year(), DateTimeFieldType.yearOfEra());
/*     */   }
/*     */   
/*     */   public DurationField getRangeDurationField()
/*     */   {
/*  53 */     return GregorianChronology.getInstanceUTC().eras();
/*     */   }
/*     */   
/*     */   public int get(long paramLong) {
/*  57 */     int i = getWrappedField().get(paramLong);
/*  58 */     return i < 0 ? -i : i;
/*     */   }
/*     */   
/*     */   public long add(long paramLong, int paramInt) {
/*  62 */     return getWrappedField().add(paramLong, paramInt);
/*     */   }
/*     */   
/*     */   public long add(long paramLong1, long paramLong2) {
/*  66 */     return getWrappedField().add(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */   public long addWrapField(long paramLong, int paramInt) {
/*  70 */     return getWrappedField().addWrapField(paramLong, paramInt);
/*     */   }
/*     */   
/*     */   public int[] addWrapField(ReadablePartial paramReadablePartial, int paramInt1, int[] paramArrayOfInt, int paramInt2) {
/*  74 */     return getWrappedField().addWrapField(paramReadablePartial, paramInt1, paramArrayOfInt, paramInt2);
/*     */   }
/*     */   
/*     */   public int getDifference(long paramLong1, long paramLong2) {
/*  78 */     return getWrappedField().getDifference(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */   public long getDifferenceAsLong(long paramLong1, long paramLong2) {
/*  82 */     return getWrappedField().getDifferenceAsLong(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */   public long set(long paramLong, int paramInt) {
/*  86 */     FieldUtils.verifyValueBounds(this, paramInt, 0, getMaximumValue());
/*  87 */     if (getWrappedField().get(paramLong) < 0) {
/*  88 */       paramInt = -paramInt;
/*     */     }
/*  90 */     return super.set(paramLong, paramInt);
/*     */   }
/*     */   
/*     */   public int getMinimumValue() {
/*  94 */     return 0;
/*     */   }
/*     */   
/*     */   public int getMaximumValue() {
/*  98 */     return getWrappedField().getMaximumValue();
/*     */   }
/*     */   
/*     */   public long roundFloor(long paramLong) {
/* 102 */     return getWrappedField().roundFloor(paramLong);
/*     */   }
/*     */   
/*     */   public long roundCeiling(long paramLong) {
/* 106 */     return getWrappedField().roundCeiling(paramLong);
/*     */   }
/*     */   
/*     */   public long remainder(long paramLong) {
/* 110 */     return getWrappedField().remainder(paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Object readResolve()
/*     */   {
/* 117 */     return INSTANCE;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\ISOYearOfEraDateTimeField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */