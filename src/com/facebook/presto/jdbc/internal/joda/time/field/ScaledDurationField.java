/*     */ package com.facebook.presto.jdbc.internal.joda.time.field;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationFieldType;
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
/*     */ public class ScaledDurationField
/*     */   extends DecoratedDurationField
/*     */ {
/*     */   private static final long serialVersionUID = -3205227092378684157L;
/*     */   private final int iScalar;
/*     */   
/*     */   public ScaledDurationField(DurationField paramDurationField, DurationFieldType paramDurationFieldType, int paramInt)
/*     */   {
/*  47 */     super(paramDurationField, paramDurationFieldType);
/*  48 */     if ((paramInt == 0) || (paramInt == 1)) {
/*  49 */       throw new IllegalArgumentException("The scalar must not be 0 or 1");
/*     */     }
/*  51 */     this.iScalar = paramInt;
/*     */   }
/*     */   
/*     */   public int getValue(long paramLong) {
/*  55 */     return getWrappedField().getValue(paramLong) / this.iScalar;
/*     */   }
/*     */   
/*     */   public long getValueAsLong(long paramLong) {
/*  59 */     return getWrappedField().getValueAsLong(paramLong) / this.iScalar;
/*     */   }
/*     */   
/*     */   public int getValue(long paramLong1, long paramLong2) {
/*  63 */     return getWrappedField().getValue(paramLong1, paramLong2) / this.iScalar;
/*     */   }
/*     */   
/*     */   public long getValueAsLong(long paramLong1, long paramLong2) {
/*  67 */     return getWrappedField().getValueAsLong(paramLong1, paramLong2) / this.iScalar;
/*     */   }
/*     */   
/*     */   public long getMillis(int paramInt) {
/*  71 */     long l = paramInt * this.iScalar;
/*  72 */     return getWrappedField().getMillis(l);
/*     */   }
/*     */   
/*     */   public long getMillis(long paramLong) {
/*  76 */     long l = FieldUtils.safeMultiply(paramLong, this.iScalar);
/*  77 */     return getWrappedField().getMillis(l);
/*     */   }
/*     */   
/*     */   public long getMillis(int paramInt, long paramLong) {
/*  81 */     long l = paramInt * this.iScalar;
/*  82 */     return getWrappedField().getMillis(l, paramLong);
/*     */   }
/*     */   
/*     */   public long getMillis(long paramLong1, long paramLong2) {
/*  86 */     long l = FieldUtils.safeMultiply(paramLong1, this.iScalar);
/*  87 */     return getWrappedField().getMillis(l, paramLong2);
/*     */   }
/*     */   
/*     */   public long add(long paramLong, int paramInt) {
/*  91 */     long l = paramInt * this.iScalar;
/*  92 */     return getWrappedField().add(paramLong, l);
/*     */   }
/*     */   
/*     */   public long add(long paramLong1, long paramLong2) {
/*  96 */     long l = FieldUtils.safeMultiply(paramLong2, this.iScalar);
/*  97 */     return getWrappedField().add(paramLong1, l);
/*     */   }
/*     */   
/*     */   public int getDifference(long paramLong1, long paramLong2) {
/* 101 */     return getWrappedField().getDifference(paramLong1, paramLong2) / this.iScalar;
/*     */   }
/*     */   
/*     */   public long getDifferenceAsLong(long paramLong1, long paramLong2) {
/* 105 */     return getWrappedField().getDifferenceAsLong(paramLong1, paramLong2) / this.iScalar;
/*     */   }
/*     */   
/*     */   public long getUnitMillis() {
/* 109 */     return getWrappedField().getUnitMillis() * this.iScalar;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getScalar()
/*     */   {
/* 119 */     return this.iScalar;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 130 */     if (this == paramObject)
/* 131 */       return true;
/* 132 */     if ((paramObject instanceof ScaledDurationField)) {
/* 133 */       ScaledDurationField localScaledDurationField = (ScaledDurationField)paramObject;
/* 134 */       return (getWrappedField().equals(localScaledDurationField.getWrappedField())) && (getType() == localScaledDurationField.getType()) && (this.iScalar == localScaledDurationField.iScalar);
/*     */     }
/*     */     
/*     */ 
/* 138 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 147 */     long l = this.iScalar;
/* 148 */     int i = (int)(l ^ l >>> 32);
/* 149 */     i += getType().hashCode();
/* 150 */     i += getWrappedField().hashCode();
/* 151 */     return i;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\field\ScaledDurationField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */