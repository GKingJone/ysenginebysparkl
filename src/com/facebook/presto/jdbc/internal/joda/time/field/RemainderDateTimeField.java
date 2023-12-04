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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RemainderDateTimeField
/*     */   extends DecoratedDateTimeField
/*     */ {
/*     */   private static final long serialVersionUID = 5708241235177666790L;
/*     */   final int iDivisor;
/*     */   final DurationField iDurationField;
/*     */   final DurationField iRangeField;
/*     */   
/*     */   public RemainderDateTimeField(DateTimeField paramDateTimeField, DateTimeFieldType paramDateTimeFieldType, int paramInt)
/*     */   {
/*  54 */     super(paramDateTimeField, paramDateTimeFieldType);
/*     */     
/*  56 */     if (paramInt < 2) {
/*  57 */       throw new IllegalArgumentException("The divisor must be at least 2");
/*     */     }
/*     */     
/*  60 */     DurationField localDurationField = paramDateTimeField.getDurationField();
/*  61 */     if (localDurationField == null) {
/*  62 */       this.iRangeField = null;
/*     */     } else {
/*  64 */       this.iRangeField = new ScaledDurationField(localDurationField, paramDateTimeFieldType.getRangeDurationType(), paramInt);
/*     */     }
/*     */     
/*  67 */     this.iDurationField = paramDateTimeField.getDurationField();
/*  68 */     this.iDivisor = paramInt;
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
/*     */   public RemainderDateTimeField(DateTimeField paramDateTimeField, DurationField paramDurationField, DateTimeFieldType paramDateTimeFieldType, int paramInt)
/*     */   {
/*  82 */     super(paramDateTimeField, paramDateTimeFieldType);
/*  83 */     if (paramInt < 2) {
/*  84 */       throw new IllegalArgumentException("The divisor must be at least 2");
/*     */     }
/*  86 */     this.iRangeField = paramDurationField;
/*  87 */     this.iDurationField = paramDateTimeField.getDurationField();
/*  88 */     this.iDivisor = paramInt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public RemainderDateTimeField(DividedDateTimeField paramDividedDateTimeField)
/*     */   {
/*  98 */     this(paramDividedDateTimeField, paramDividedDateTimeField.getType());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public RemainderDateTimeField(DividedDateTimeField paramDividedDateTimeField, DateTimeFieldType paramDateTimeFieldType)
/*     */   {
/* 109 */     this(paramDividedDateTimeField, paramDividedDateTimeField.getWrappedField().getDurationField(), paramDateTimeFieldType);
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
/*     */   public RemainderDateTimeField(DividedDateTimeField paramDividedDateTimeField, DurationField paramDurationField, DateTimeFieldType paramDateTimeFieldType)
/*     */   {
/* 122 */     super(paramDividedDateTimeField.getWrappedField(), paramDateTimeFieldType);
/* 123 */     this.iDivisor = paramDividedDateTimeField.iDivisor;
/* 124 */     this.iDurationField = paramDurationField;
/* 125 */     this.iRangeField = paramDividedDateTimeField.iDurationField;
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
/* 136 */     int i = getWrappedField().get(paramLong);
/* 137 */     if (i >= 0) {
/* 138 */       return i % this.iDivisor;
/*     */     }
/* 140 */     return this.iDivisor - 1 + (i + 1) % this.iDivisor;
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
/*     */   public long addWrapField(long paramLong, int paramInt)
/*     */   {
/* 154 */     return set(paramLong, FieldUtils.getWrappedValue(get(paramLong), paramInt, 0, this.iDivisor - 1));
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
/* 166 */     FieldUtils.verifyValueBounds(this, paramInt, 0, this.iDivisor - 1);
/* 167 */     int i = getDivided(getWrappedField().get(paramLong));
/* 168 */     return getWrappedField().set(paramLong, i * this.iDivisor + paramInt);
/*     */   }
/*     */   
/*     */   public DurationField getDurationField()
/*     */   {
/* 173 */     return this.iDurationField;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DurationField getRangeDurationField()
/*     */   {
/* 180 */     return this.iRangeField;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMinimumValue()
/*     */   {
/* 189 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaximumValue()
/*     */   {
/* 199 */     return this.iDivisor - 1;
/*     */   }
/*     */   
/*     */   public long roundFloor(long paramLong) {
/* 203 */     return getWrappedField().roundFloor(paramLong);
/*     */   }
/*     */   
/*     */   public long roundCeiling(long paramLong) {
/* 207 */     return getWrappedField().roundCeiling(paramLong);
/*     */   }
/*     */   
/*     */   public long roundHalfFloor(long paramLong) {
/* 211 */     return getWrappedField().roundHalfFloor(paramLong);
/*     */   }
/*     */   
/*     */   public long roundHalfCeiling(long paramLong) {
/* 215 */     return getWrappedField().roundHalfCeiling(paramLong);
/*     */   }
/*     */   
/*     */   public long roundHalfEven(long paramLong) {
/* 219 */     return getWrappedField().roundHalfEven(paramLong);
/*     */   }
/*     */   
/*     */   public long remainder(long paramLong) {
/* 223 */     return getWrappedField().remainder(paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getDivisor()
/*     */   {
/* 232 */     return this.iDivisor;
/*     */   }
/*     */   
/*     */   private int getDivided(int paramInt) {
/* 236 */     if (paramInt >= 0) {
/* 237 */       return paramInt / this.iDivisor;
/*     */     }
/* 239 */     return (paramInt + 1) / this.iDivisor - 1;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\field\RemainderDateTimeField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */