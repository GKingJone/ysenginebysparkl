/*     */ package com.facebook.presto.jdbc.internal.joda.time.field;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class OffsetDateTimeField
/*     */   extends DecoratedDateTimeField
/*     */ {
/*     */   private static final long serialVersionUID = 3145790132623583142L;
/*     */   private final int iOffset;
/*     */   private final int iMin;
/*     */   private final int iMax;
/*     */   
/*     */   public OffsetDateTimeField(DateTimeField paramDateTimeField, int paramInt)
/*     */   {
/*  47 */     this(paramDateTimeField, paramDateTimeField == null ? null : paramDateTimeField.getType(), paramInt, Integer.MIN_VALUE, Integer.MAX_VALUE);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public OffsetDateTimeField(DateTimeField paramDateTimeField, DateTimeFieldType paramDateTimeFieldType, int paramInt)
/*     */   {
/*  59 */     this(paramDateTimeField, paramDateTimeFieldType, paramInt, Integer.MIN_VALUE, Integer.MAX_VALUE);
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
/*     */   public OffsetDateTimeField(DateTimeField paramDateTimeField, DateTimeFieldType paramDateTimeFieldType, int paramInt1, int paramInt2, int paramInt3)
/*     */   {
/*  74 */     super(paramDateTimeField, paramDateTimeFieldType);
/*     */     
/*  76 */     if (paramInt1 == 0) {
/*  77 */       throw new IllegalArgumentException("The offset cannot be zero");
/*     */     }
/*     */     
/*  80 */     this.iOffset = paramInt1;
/*     */     
/*  82 */     if (paramInt2 < paramDateTimeField.getMinimumValue() + paramInt1) {
/*  83 */       this.iMin = (paramDateTimeField.getMinimumValue() + paramInt1);
/*     */     } else {
/*  85 */       this.iMin = paramInt2;
/*     */     }
/*  87 */     if (paramInt3 > paramDateTimeField.getMaximumValue() + paramInt1) {
/*  88 */       this.iMax = (paramDateTimeField.getMaximumValue() + paramInt1);
/*     */     } else {
/*  90 */       this.iMax = paramInt3;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int get(long paramLong)
/*     */   {
/* 101 */     return super.get(paramLong) + this.iOffset;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long add(long paramLong, int paramInt)
/*     */   {
/* 113 */     paramLong = super.add(paramLong, paramInt);
/* 114 */     FieldUtils.verifyValueBounds(this, get(paramLong), this.iMin, this.iMax);
/* 115 */     return paramLong;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long add(long paramLong1, long paramLong2)
/*     */   {
/* 127 */     paramLong1 = super.add(paramLong1, paramLong2);
/* 128 */     FieldUtils.verifyValueBounds(this, get(paramLong1), this.iMin, this.iMax);
/* 129 */     return paramLong1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long addWrapField(long paramLong, int paramInt)
/*     */   {
/* 141 */     return set(paramLong, FieldUtils.getWrappedValue(get(paramLong), paramInt, this.iMin, this.iMax));
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
/* 153 */     FieldUtils.verifyValueBounds(this, paramInt, this.iMin, this.iMax);
/* 154 */     return super.set(paramLong, paramInt - this.iOffset);
/*     */   }
/*     */   
/*     */   public boolean isLeap(long paramLong) {
/* 158 */     return getWrappedField().isLeap(paramLong);
/*     */   }
/*     */   
/*     */   public int getLeapAmount(long paramLong) {
/* 162 */     return getWrappedField().getLeapAmount(paramLong);
/*     */   }
/*     */   
/*     */   public DurationField getLeapDurationField() {
/* 166 */     return getWrappedField().getLeapDurationField();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMinimumValue()
/*     */   {
/* 175 */     return this.iMin;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaximumValue()
/*     */   {
/* 184 */     return this.iMax;
/*     */   }
/*     */   
/*     */   public long roundFloor(long paramLong) {
/* 188 */     return getWrappedField().roundFloor(paramLong);
/*     */   }
/*     */   
/*     */   public long roundCeiling(long paramLong) {
/* 192 */     return getWrappedField().roundCeiling(paramLong);
/*     */   }
/*     */   
/*     */   public long roundHalfFloor(long paramLong) {
/* 196 */     return getWrappedField().roundHalfFloor(paramLong);
/*     */   }
/*     */   
/*     */   public long roundHalfCeiling(long paramLong) {
/* 200 */     return getWrappedField().roundHalfCeiling(paramLong);
/*     */   }
/*     */   
/*     */   public long roundHalfEven(long paramLong) {
/* 204 */     return getWrappedField().roundHalfEven(paramLong);
/*     */   }
/*     */   
/*     */   public long remainder(long paramLong) {
/* 208 */     return getWrappedField().remainder(paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getOffset()
/*     */   {
/* 217 */     return this.iOffset;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\field\OffsetDateTimeField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */