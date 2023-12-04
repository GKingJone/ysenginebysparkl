/*     */ package com.facebook.presto.jdbc.internal.joda.time.field;
/*     */ 
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
/*     */ public class PreciseDurationField
/*     */   extends BaseDurationField
/*     */ {
/*     */   private static final long serialVersionUID = -8346152187724495365L;
/*     */   private final long iUnitMillis;
/*     */   
/*     */   public PreciseDurationField(DurationFieldType paramDurationFieldType, long paramLong)
/*     */   {
/*  43 */     super(paramDurationFieldType);
/*  44 */     this.iUnitMillis = paramLong;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final boolean isPrecise()
/*     */   {
/*  54 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final long getUnitMillis()
/*     */   {
/*  63 */     return this.iUnitMillis;
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
/*     */   public long getValueAsLong(long paramLong1, long paramLong2)
/*     */   {
/*  76 */     return paramLong1 / this.iUnitMillis;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getMillis(int paramInt, long paramLong)
/*     */   {
/*  88 */     return paramInt * this.iUnitMillis;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getMillis(long paramLong1, long paramLong2)
/*     */   {
/* 100 */     return FieldUtils.safeMultiply(paramLong1, this.iUnitMillis);
/*     */   }
/*     */   
/*     */   public long add(long paramLong, int paramInt) {
/* 104 */     long l = paramInt * this.iUnitMillis;
/* 105 */     return FieldUtils.safeAdd(paramLong, l);
/*     */   }
/*     */   
/*     */   public long add(long paramLong1, long paramLong2) {
/* 109 */     long l = FieldUtils.safeMultiply(paramLong2, this.iUnitMillis);
/* 110 */     return FieldUtils.safeAdd(paramLong1, l);
/*     */   }
/*     */   
/*     */   public long getDifferenceAsLong(long paramLong1, long paramLong2) {
/* 114 */     long l = FieldUtils.safeSubtract(paramLong1, paramLong2);
/* 115 */     return l / this.iUnitMillis;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 127 */     if (this == paramObject)
/* 128 */       return true;
/* 129 */     if ((paramObject instanceof PreciseDurationField)) {
/* 130 */       PreciseDurationField localPreciseDurationField = (PreciseDurationField)paramObject;
/* 131 */       return (getType() == localPreciseDurationField.getType()) && (this.iUnitMillis == localPreciseDurationField.iUnitMillis);
/*     */     }
/* 133 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 142 */     long l = this.iUnitMillis;
/* 143 */     int i = (int)(l ^ l >>> 32);
/* 144 */     i += getType().hashCode();
/* 145 */     return i;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\field\PreciseDurationField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */