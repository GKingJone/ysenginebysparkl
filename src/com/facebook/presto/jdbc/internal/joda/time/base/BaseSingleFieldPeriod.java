/*     */ package com.facebook.presto.jdbc.internal.joda.time.base;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeUtils;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.MutablePeriod;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Period;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.PeriodType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadableInstant;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePartial;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePeriod;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.chrono.ISOChronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.FieldUtils;
/*     */ import java.io.Serializable;
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
/*     */ public abstract class BaseSingleFieldPeriod
/*     */   implements ReadablePeriod, Comparable<BaseSingleFieldPeriod>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 9386874258972L;
/*     */   private static final long START_1972 = 63072000000L;
/*     */   private volatile int iPeriod;
/*     */   
/*     */   protected static int between(ReadableInstant paramReadableInstant1, ReadableInstant paramReadableInstant2, DurationFieldType paramDurationFieldType)
/*     */   {
/*  68 */     if ((paramReadableInstant1 == null) || (paramReadableInstant2 == null)) {
/*  69 */       throw new IllegalArgumentException("ReadableInstant objects must not be null");
/*     */     }
/*  71 */     Chronology localChronology = DateTimeUtils.getInstantChronology(paramReadableInstant1);
/*  72 */     int i = paramDurationFieldType.getField(localChronology).getDifference(paramReadableInstant2.getMillis(), paramReadableInstant1.getMillis());
/*  73 */     return i;
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
/*     */ 
/*     */ 
/*     */   protected static int between(ReadablePartial paramReadablePartial1, ReadablePartial paramReadablePartial2, ReadablePeriod paramReadablePeriod)
/*     */   {
/*  90 */     if ((paramReadablePartial1 == null) || (paramReadablePartial2 == null)) {
/*  91 */       throw new IllegalArgumentException("ReadablePartial objects must not be null");
/*     */     }
/*  93 */     if (paramReadablePartial1.size() != paramReadablePartial2.size()) {
/*  94 */       throw new IllegalArgumentException("ReadablePartial objects must have the same set of fields");
/*     */     }
/*  96 */     int i = 0; for (int j = paramReadablePartial1.size(); i < j; i++) {
/*  97 */       if (paramReadablePartial1.getFieldType(i) != paramReadablePartial2.getFieldType(i)) {
/*  98 */         throw new IllegalArgumentException("ReadablePartial objects must have the same set of fields");
/*     */       }
/*     */     }
/* 101 */     if (!DateTimeUtils.isContiguous(paramReadablePartial1)) {
/* 102 */       throw new IllegalArgumentException("ReadablePartial objects must be contiguous");
/*     */     }
/* 104 */     Chronology localChronology = DateTimeUtils.getChronology(paramReadablePartial1.getChronology()).withUTC();
/* 105 */     int[] arrayOfInt = localChronology.get(paramReadablePeriod, localChronology.set(paramReadablePartial1, 63072000000L), localChronology.set(paramReadablePartial2, 63072000000L));
/* 106 */     return arrayOfInt[0];
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static int standardPeriodIn(ReadablePeriod paramReadablePeriod, long paramLong)
/*     */   {
/* 130 */     if (paramReadablePeriod == null) {
/* 131 */       return 0;
/*     */     }
/* 133 */     ISOChronology localISOChronology = ISOChronology.getInstanceUTC();
/* 134 */     long l = 0L;
/* 135 */     for (int i = 0; i < paramReadablePeriod.size(); i++) {
/* 136 */       int j = paramReadablePeriod.getValue(i);
/* 137 */       if (j != 0) {
/* 138 */         DurationField localDurationField = paramReadablePeriod.getFieldType(i).getField(localISOChronology);
/* 139 */         if (!localDurationField.isPrecise()) {
/* 140 */           throw new IllegalArgumentException("Cannot convert period to duration as " + localDurationField.getName() + " is not precise in the period " + paramReadablePeriod);
/*     */         }
/*     */         
/*     */ 
/* 144 */         l = FieldUtils.safeAdd(l, FieldUtils.safeMultiply(localDurationField.getUnitMillis(), j));
/*     */       }
/*     */     }
/* 147 */     return FieldUtils.safeToInt(l / paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected BaseSingleFieldPeriod(int paramInt)
/*     */   {
/* 158 */     this.iPeriod = paramInt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int getValue()
/*     */   {
/* 168 */     return this.iPeriod;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setValue(int paramInt)
/*     */   {
/* 178 */     this.iPeriod = paramInt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract DurationFieldType getFieldType();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract PeriodType getPeriodType();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int size()
/*     */   {
/* 203 */     return 1;
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
/*     */   public DurationFieldType getFieldType(int paramInt)
/*     */   {
/* 217 */     if (paramInt != 0) {
/* 218 */       throw new IndexOutOfBoundsException(String.valueOf(paramInt));
/*     */     }
/* 220 */     return getFieldType();
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
/*     */   public int getValue(int paramInt)
/*     */   {
/* 233 */     if (paramInt != 0) {
/* 234 */       throw new IndexOutOfBoundsException(String.valueOf(paramInt));
/*     */     }
/* 236 */     return getValue();
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
/*     */   public int get(DurationFieldType paramDurationFieldType)
/*     */   {
/* 249 */     if (paramDurationFieldType == getFieldType()) {
/* 250 */       return getValue();
/*     */     }
/* 252 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isSupported(DurationFieldType paramDurationFieldType)
/*     */   {
/* 262 */     return paramDurationFieldType == getFieldType();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Period toPeriod()
/*     */   {
/* 273 */     return Period.ZERO.withFields(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public MutablePeriod toMutablePeriod()
/*     */   {
/* 285 */     MutablePeriod localMutablePeriod = new MutablePeriod();
/* 286 */     localMutablePeriod.add(this);
/* 287 */     return localMutablePeriod;
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
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 301 */     if (this == paramObject) {
/* 302 */       return true;
/*     */     }
/* 304 */     if (!(paramObject instanceof ReadablePeriod)) {
/* 305 */       return false;
/*     */     }
/* 307 */     ReadablePeriod localReadablePeriod = (ReadablePeriod)paramObject;
/* 308 */     return (localReadablePeriod.getPeriodType() == getPeriodType()) && (localReadablePeriod.getValue(0) == getValue());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 317 */     int i = 17;
/* 318 */     i = 27 * i + getValue();
/* 319 */     i = 27 * i + getFieldType().hashCode();
/* 320 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int compareTo(BaseSingleFieldPeriod paramBaseSingleFieldPeriod)
/*     */   {
/* 332 */     if (paramBaseSingleFieldPeriod.getClass() != getClass()) {
/* 333 */       throw new ClassCastException(getClass() + " cannot be compared to " + paramBaseSingleFieldPeriod.getClass());
/*     */     }
/* 335 */     int i = paramBaseSingleFieldPeriod.getValue();
/* 336 */     int j = getValue();
/* 337 */     if (j > i) {
/* 338 */       return 1;
/*     */     }
/* 340 */     if (j < i) {
/* 341 */       return -1;
/*     */     }
/* 343 */     return 0;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\base\BaseSingleFieldPeriod.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */