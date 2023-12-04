/*     */ package com.facebook.presto.jdbc.internal.joda.time.field;
/*     */ 
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
/*     */ public abstract class PreciseDurationDateTimeField
/*     */   extends BaseDateTimeField
/*     */ {
/*     */   private static final long serialVersionUID = 5004523158306266035L;
/*     */   final long iUnitMillis;
/*     */   private final DurationField iUnitField;
/*     */   
/*     */   public PreciseDurationDateTimeField(DateTimeFieldType paramDateTimeFieldType, DurationField paramDurationField)
/*     */   {
/*  49 */     super(paramDateTimeFieldType);
/*     */     
/*  51 */     if (!paramDurationField.isPrecise()) {
/*  52 */       throw new IllegalArgumentException("Unit duration field must be precise");
/*     */     }
/*     */     
/*  55 */     this.iUnitMillis = paramDurationField.getUnitMillis();
/*  56 */     if (this.iUnitMillis < 1L) {
/*  57 */       throw new IllegalArgumentException("The unit milliseconds must be at least 1");
/*     */     }
/*     */     
/*  60 */     this.iUnitField = paramDurationField;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isLenient()
/*     */   {
/*  67 */     return false;
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
/*  79 */     FieldUtils.verifyValueBounds(this, paramInt, getMinimumValue(), getMaximumValueForSet(paramLong, paramInt));
/*     */     
/*  81 */     return paramLong + (paramInt - get(paramLong)) * this.iUnitMillis;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long roundFloor(long paramLong)
/*     */   {
/*  93 */     if (paramLong >= 0L) {
/*  94 */       return paramLong - paramLong % this.iUnitMillis;
/*     */     }
/*  96 */     paramLong += 1L;
/*  97 */     return paramLong - paramLong % this.iUnitMillis - this.iUnitMillis;
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
/*     */   public long roundCeiling(long paramLong)
/*     */   {
/* 110 */     if (paramLong > 0L) {
/* 111 */       paramLong -= 1L;
/* 112 */       return paramLong - paramLong % this.iUnitMillis + this.iUnitMillis;
/*     */     }
/* 114 */     return paramLong - paramLong % this.iUnitMillis;
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
/*     */   public long remainder(long paramLong)
/*     */   {
/* 127 */     if (paramLong >= 0L) {
/* 128 */       return paramLong % this.iUnitMillis;
/*     */     }
/* 130 */     return (paramLong + 1L) % this.iUnitMillis + this.iUnitMillis - 1L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationField getDurationField()
/*     */   {
/* 142 */     return this.iUnitField;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMinimumValue()
/*     */   {
/* 151 */     return 0;
/*     */   }
/*     */   
/*     */   public final long getUnitMillis() {
/* 155 */     return this.iUnitMillis;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int getMaximumValueForSet(long paramLong, int paramInt)
/*     */   {
/* 164 */     return getMaximumValue(paramLong);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\field\PreciseDurationDateTimeField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */