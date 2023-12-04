/*     */ package com.facebook.presto.jdbc.internal.joda.time.base;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeUtils;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Duration;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.MutablePeriod;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.PeriodType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadWritablePeriod;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadableDuration;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadableInstant;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePartial;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePeriod;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.chrono.ISOChronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.convert.ConverterManager;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.convert.PeriodConverter;
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
/*     */ public abstract class BasePeriod
/*     */   extends AbstractPeriod
/*     */   implements ReadablePeriod, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -2110953284060001145L;
/*  57 */   private static final ReadablePeriod DUMMY_PERIOD = new AbstractPeriod() {
/*     */     public int getValue(int paramAnonymousInt) {
/*  59 */       return 0;
/*     */     }
/*     */     
/*  62 */     public PeriodType getPeriodType() { return PeriodType.time(); }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final PeriodType iType;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final int[] iValues;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected BasePeriod(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, PeriodType paramPeriodType)
/*     */   {
/*  91 */     paramPeriodType = checkPeriodType(paramPeriodType);
/*  92 */     this.iType = paramPeriodType;
/*  93 */     this.iValues = setPeriodInternal(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8);
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
/*     */   protected BasePeriod(long paramLong1, long paramLong2, PeriodType paramPeriodType, Chronology paramChronology)
/*     */   {
/* 107 */     paramPeriodType = checkPeriodType(paramPeriodType);
/* 108 */     paramChronology = DateTimeUtils.getChronology(paramChronology);
/* 109 */     this.iType = paramPeriodType;
/* 110 */     this.iValues = paramChronology.get(this, paramLong1, paramLong2);
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
/*     */   protected BasePeriod(ReadableInstant paramReadableInstant1, ReadableInstant paramReadableInstant2, PeriodType paramPeriodType)
/*     */   {
/* 123 */     paramPeriodType = checkPeriodType(paramPeriodType);
/* 124 */     if ((paramReadableInstant1 == null) && (paramReadableInstant2 == null)) {
/* 125 */       this.iType = paramPeriodType;
/* 126 */       this.iValues = new int[size()];
/*     */     } else {
/* 128 */       long l1 = DateTimeUtils.getInstantMillis(paramReadableInstant1);
/* 129 */       long l2 = DateTimeUtils.getInstantMillis(paramReadableInstant2);
/* 130 */       Chronology localChronology = DateTimeUtils.getIntervalChronology(paramReadableInstant1, paramReadableInstant2);
/* 131 */       this.iType = paramPeriodType;
/* 132 */       this.iValues = localChronology.get(this, l1, l2);
/*     */     }
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
/*     */   protected BasePeriod(ReadablePartial paramReadablePartial1, ReadablePartial paramReadablePartial2, PeriodType paramPeriodType)
/*     */   {
/* 156 */     if ((paramReadablePartial1 == null) || (paramReadablePartial2 == null)) {
/* 157 */       throw new IllegalArgumentException("ReadablePartial objects must not be null");
/*     */     }
/* 159 */     if (((paramReadablePartial1 instanceof BaseLocal)) && ((paramReadablePartial2 instanceof BaseLocal)) && (paramReadablePartial1.getClass() == paramReadablePartial2.getClass()))
/*     */     {
/* 161 */       paramPeriodType = checkPeriodType(paramPeriodType);
/* 162 */       long l1 = ((BaseLocal)paramReadablePartial1).getLocalMillis();
/* 163 */       long l2 = ((BaseLocal)paramReadablePartial2).getLocalMillis();
/* 164 */       Chronology localChronology1 = paramReadablePartial1.getChronology();
/* 165 */       localChronology1 = DateTimeUtils.getChronology(localChronology1);
/* 166 */       this.iType = paramPeriodType;
/* 167 */       this.iValues = localChronology1.get(this, l1, l2);
/*     */     } else {
/* 169 */       if (paramReadablePartial1.size() != paramReadablePartial2.size()) {
/* 170 */         throw new IllegalArgumentException("ReadablePartial objects must have the same set of fields");
/*     */       }
/* 172 */       int i = 0; for (int j = paramReadablePartial1.size(); i < j; i++) {
/* 173 */         if (paramReadablePartial1.getFieldType(i) != paramReadablePartial2.getFieldType(i)) {
/* 174 */           throw new IllegalArgumentException("ReadablePartial objects must have the same set of fields");
/*     */         }
/*     */       }
/* 177 */       if (!DateTimeUtils.isContiguous(paramReadablePartial1)) {
/* 178 */         throw new IllegalArgumentException("ReadablePartial objects must be contiguous");
/*     */       }
/* 180 */       this.iType = checkPeriodType(paramPeriodType);
/* 181 */       Chronology localChronology2 = DateTimeUtils.getChronology(paramReadablePartial1.getChronology()).withUTC();
/* 182 */       this.iValues = localChronology2.get(this, localChronology2.set(paramReadablePartial1, 0L), localChronology2.set(paramReadablePartial2, 0L));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected BasePeriod(ReadableInstant paramReadableInstant, ReadableDuration paramReadableDuration, PeriodType paramPeriodType)
/*     */   {
/* 195 */     paramPeriodType = checkPeriodType(paramPeriodType);
/* 196 */     long l1 = DateTimeUtils.getInstantMillis(paramReadableInstant);
/* 197 */     long l2 = DateTimeUtils.getDurationMillis(paramReadableDuration);
/* 198 */     long l3 = FieldUtils.safeAdd(l1, l2);
/* 199 */     Chronology localChronology = DateTimeUtils.getInstantChronology(paramReadableInstant);
/* 200 */     this.iType = paramPeriodType;
/* 201 */     this.iValues = localChronology.get(this, l1, l3);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected BasePeriod(ReadableDuration paramReadableDuration, ReadableInstant paramReadableInstant, PeriodType paramPeriodType)
/*     */   {
/* 213 */     paramPeriodType = checkPeriodType(paramPeriodType);
/* 214 */     long l1 = DateTimeUtils.getDurationMillis(paramReadableDuration);
/* 215 */     long l2 = DateTimeUtils.getInstantMillis(paramReadableInstant);
/* 216 */     long l3 = FieldUtils.safeSubtract(l2, l1);
/* 217 */     Chronology localChronology = DateTimeUtils.getInstantChronology(paramReadableInstant);
/* 218 */     this.iType = paramPeriodType;
/* 219 */     this.iValues = localChronology.get(this, l3, l2);
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
/*     */   protected BasePeriod(long paramLong)
/*     */   {
/* 235 */     this.iType = PeriodType.standard();
/* 236 */     int[] arrayOfInt = ISOChronology.getInstanceUTC().get(DUMMY_PERIOD, paramLong);
/* 237 */     this.iValues = new int[8];
/* 238 */     System.arraycopy(arrayOfInt, 0, this.iValues, 4, 4);
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
/*     */   protected BasePeriod(long paramLong, PeriodType paramPeriodType, Chronology paramChronology)
/*     */   {
/* 255 */     paramPeriodType = checkPeriodType(paramPeriodType);
/* 256 */     paramChronology = DateTimeUtils.getChronology(paramChronology);
/* 257 */     this.iType = paramPeriodType;
/* 258 */     this.iValues = paramChronology.get(this, paramLong);
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
/*     */   protected BasePeriod(Object paramObject, PeriodType paramPeriodType, Chronology paramChronology)
/*     */   {
/* 272 */     PeriodConverter localPeriodConverter = ConverterManager.getInstance().getPeriodConverter(paramObject);
/* 273 */     paramPeriodType = paramPeriodType == null ? localPeriodConverter.getPeriodType(paramObject) : paramPeriodType;
/* 274 */     paramPeriodType = checkPeriodType(paramPeriodType);
/* 275 */     this.iType = paramPeriodType;
/* 276 */     if ((this instanceof ReadWritablePeriod)) {
/* 277 */       this.iValues = new int[size()];
/* 278 */       paramChronology = DateTimeUtils.getChronology(paramChronology);
/* 279 */       localPeriodConverter.setInto((ReadWritablePeriod)this, paramObject, paramChronology);
/*     */     } else {
/* 281 */       this.iValues = new MutablePeriod(paramObject, paramPeriodType, paramChronology).getValues();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected BasePeriod(int[] paramArrayOfInt, PeriodType paramPeriodType)
/*     */   {
/* 294 */     this.iType = paramPeriodType;
/* 295 */     this.iValues = paramArrayOfInt;
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
/*     */   protected PeriodType checkPeriodType(PeriodType paramPeriodType)
/*     */   {
/* 308 */     return DateTimeUtils.getPeriodType(paramPeriodType);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PeriodType getPeriodType()
/*     */   {
/* 318 */     return this.iType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getValue(int paramInt)
/*     */   {
/* 329 */     return this.iValues[paramInt];
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
/*     */   public Duration toDurationFrom(ReadableInstant paramReadableInstant)
/*     */   {
/* 350 */     long l1 = DateTimeUtils.getInstantMillis(paramReadableInstant);
/* 351 */     Chronology localChronology = DateTimeUtils.getInstantChronology(paramReadableInstant);
/* 352 */     long l2 = localChronology.add(this, l1, 1);
/* 353 */     return new Duration(l1, l2);
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
/*     */   public Duration toDurationTo(ReadableInstant paramReadableInstant)
/*     */   {
/* 374 */     long l1 = DateTimeUtils.getInstantMillis(paramReadableInstant);
/* 375 */     Chronology localChronology = DateTimeUtils.getInstantChronology(paramReadableInstant);
/* 376 */     long l2 = localChronology.add(this, l1, -1);
/* 377 */     return new Duration(l2, l1);
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
/*     */   private void checkAndUpdate(DurationFieldType paramDurationFieldType, int[] paramArrayOfInt, int paramInt)
/*     */   {
/* 390 */     int i = indexOf(paramDurationFieldType);
/* 391 */     if (i == -1) {
/* 392 */       if (paramInt != 0) {
/* 393 */         throw new IllegalArgumentException("Period does not support field '" + paramDurationFieldType.getName() + "'");
/*     */       }
/*     */     }
/*     */     else {
/* 397 */       paramArrayOfInt[i] = paramInt;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setPeriod(ReadablePeriod paramReadablePeriod)
/*     */   {
/* 409 */     if (paramReadablePeriod == null) {
/* 410 */       setValues(new int[size()]);
/*     */     } else {
/* 412 */       setPeriodInternal(paramReadablePeriod);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void setPeriodInternal(ReadablePeriod paramReadablePeriod)
/*     */   {
/* 420 */     int[] arrayOfInt = new int[size()];
/* 421 */     int i = 0; for (int j = paramReadablePeriod.size(); i < j; i++) {
/* 422 */       DurationFieldType localDurationFieldType = paramReadablePeriod.getFieldType(i);
/* 423 */       int k = paramReadablePeriod.getValue(i);
/* 424 */       checkAndUpdate(localDurationFieldType, arrayOfInt, k);
/*     */     }
/* 426 */     setValues(arrayOfInt);
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
/*     */   protected void setPeriod(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8)
/*     */   {
/* 444 */     int[] arrayOfInt = setPeriodInternal(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8);
/* 445 */     setValues(arrayOfInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private int[] setPeriodInternal(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8)
/*     */   {
/* 453 */     int[] arrayOfInt = new int[size()];
/* 454 */     checkAndUpdate(DurationFieldType.years(), arrayOfInt, paramInt1);
/* 455 */     checkAndUpdate(DurationFieldType.months(), arrayOfInt, paramInt2);
/* 456 */     checkAndUpdate(DurationFieldType.weeks(), arrayOfInt, paramInt3);
/* 457 */     checkAndUpdate(DurationFieldType.days(), arrayOfInt, paramInt4);
/* 458 */     checkAndUpdate(DurationFieldType.hours(), arrayOfInt, paramInt5);
/* 459 */     checkAndUpdate(DurationFieldType.minutes(), arrayOfInt, paramInt6);
/* 460 */     checkAndUpdate(DurationFieldType.seconds(), arrayOfInt, paramInt7);
/* 461 */     checkAndUpdate(DurationFieldType.millis(), arrayOfInt, paramInt8);
/* 462 */     return arrayOfInt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setField(DurationFieldType paramDurationFieldType, int paramInt)
/*     */   {
/* 474 */     setFieldInto(this.iValues, paramDurationFieldType, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setFieldInto(int[] paramArrayOfInt, DurationFieldType paramDurationFieldType, int paramInt)
/*     */   {
/* 486 */     int i = indexOf(paramDurationFieldType);
/* 487 */     if (i == -1) {
/* 488 */       if ((paramInt != 0) || (paramDurationFieldType == null)) {
/* 489 */         throw new IllegalArgumentException("Period does not support field '" + paramDurationFieldType + "'");
/*     */       }
/*     */     }
/*     */     else {
/* 493 */       paramArrayOfInt[i] = paramInt;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void addField(DurationFieldType paramDurationFieldType, int paramInt)
/*     */   {
/* 505 */     addFieldInto(this.iValues, paramDurationFieldType, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void addFieldInto(int[] paramArrayOfInt, DurationFieldType paramDurationFieldType, int paramInt)
/*     */   {
/* 517 */     int i = indexOf(paramDurationFieldType);
/* 518 */     if (i == -1) {
/* 519 */       if ((paramInt != 0) || (paramDurationFieldType == null)) {
/* 520 */         throw new IllegalArgumentException("Period does not support field '" + paramDurationFieldType + "'");
/*     */       }
/*     */     }
/*     */     else {
/* 524 */       paramArrayOfInt[i] = FieldUtils.safeAdd(paramArrayOfInt[i], paramInt);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void mergePeriod(ReadablePeriod paramReadablePeriod)
/*     */   {
/* 535 */     if (paramReadablePeriod != null) {
/* 536 */       setValues(mergePeriodInto(getValues(), paramReadablePeriod));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int[] mergePeriodInto(int[] paramArrayOfInt, ReadablePeriod paramReadablePeriod)
/*     */   {
/* 549 */     int i = 0; for (int j = paramReadablePeriod.size(); i < j; i++) {
/* 550 */       DurationFieldType localDurationFieldType = paramReadablePeriod.getFieldType(i);
/* 551 */       int k = paramReadablePeriod.getValue(i);
/* 552 */       checkAndUpdate(localDurationFieldType, paramArrayOfInt, k);
/*     */     }
/* 554 */     return paramArrayOfInt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void addPeriod(ReadablePeriod paramReadablePeriod)
/*     */   {
/* 564 */     if (paramReadablePeriod != null) {
/* 565 */       setValues(addPeriodInto(getValues(), paramReadablePeriod));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int[] addPeriodInto(int[] paramArrayOfInt, ReadablePeriod paramReadablePeriod)
/*     */   {
/* 578 */     int i = 0; for (int j = paramReadablePeriod.size(); i < j; i++) {
/* 579 */       DurationFieldType localDurationFieldType = paramReadablePeriod.getFieldType(i);
/* 580 */       int k = paramReadablePeriod.getValue(i);
/* 581 */       if (k != 0) {
/* 582 */         int m = indexOf(localDurationFieldType);
/* 583 */         if (m == -1) {
/* 584 */           throw new IllegalArgumentException("Period does not support field '" + localDurationFieldType.getName() + "'");
/*     */         }
/*     */         
/* 587 */         paramArrayOfInt[m] = FieldUtils.safeAdd(getValue(m), k);
/*     */       }
/*     */     }
/*     */     
/* 591 */     return paramArrayOfInt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setValue(int paramInt1, int paramInt2)
/*     */   {
/* 603 */     this.iValues[paramInt1] = paramInt2;
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
/*     */   protected void setValues(int[] paramArrayOfInt)
/*     */   {
/* 616 */     System.arraycopy(paramArrayOfInt, 0, this.iValues, 0, this.iValues.length);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\base\BasePeriod.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */