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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PreciseDateTimeField
/*     */   extends PreciseDurationDateTimeField
/*     */ {
/*     */   private static final long serialVersionUID = -5586801265774496376L;
/*     */   private final int iRange;
/*     */   private final DurationField iRangeField;
/*     */   
/*     */   public PreciseDateTimeField(DateTimeFieldType paramDateTimeFieldType, DurationField paramDurationField1, DurationField paramDurationField2)
/*     */   {
/*  58 */     super(paramDateTimeFieldType, paramDurationField1);
/*     */     
/*  60 */     if (!paramDurationField2.isPrecise()) {
/*  61 */       throw new IllegalArgumentException("Range duration field must be precise");
/*     */     }
/*     */     
/*  64 */     long l = paramDurationField2.getUnitMillis();
/*  65 */     this.iRange = ((int)(l / getUnitMillis()));
/*  66 */     if (this.iRange < 2) {
/*  67 */       throw new IllegalArgumentException("The effective range must be at least 2");
/*     */     }
/*     */     
/*  70 */     this.iRangeField = paramDurationField2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int get(long paramLong)
/*     */   {
/*  80 */     if (paramLong >= 0L) {
/*  81 */       return (int)(paramLong / getUnitMillis() % this.iRange);
/*     */     }
/*  83 */     return this.iRange - 1 + (int)((paramLong + 1L) / getUnitMillis() % this.iRange);
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
/*     */   public long addWrapField(long paramLong, int paramInt)
/*     */   {
/*  96 */     int i = get(paramLong);
/*  97 */     int j = FieldUtils.getWrappedValue(i, paramInt, getMinimumValue(), getMaximumValue());
/*     */     
/*     */ 
/* 100 */     return paramLong + (j - i) * getUnitMillis();
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
/* 112 */     FieldUtils.verifyValueBounds(this, paramInt, getMinimumValue(), getMaximumValue());
/* 113 */     return paramLong + (paramInt - get(paramLong)) * this.iUnitMillis;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationField getRangeDurationField()
/*     */   {
/* 123 */     return this.iRangeField;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaximumValue()
/*     */   {
/* 132 */     return this.iRange - 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getRange()
/*     */   {
/* 144 */     return this.iRange;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\field\PreciseDateTimeField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */