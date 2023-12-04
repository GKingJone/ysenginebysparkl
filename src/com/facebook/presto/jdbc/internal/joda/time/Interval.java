/*     */ package com.facebook.presto.jdbc.internal.joda.time;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.base.BaseInterval;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.chrono.ISOChronology;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Interval
/*     */   extends BaseInterval
/*     */   implements ReadableInterval, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 4922451897541386752L;
/*     */   
/*     */   public static Interval parse(String paramString)
/*     */   {
/*  69 */     return new Interval(paramString);
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
/*     */   public Interval(long paramLong1, long paramLong2)
/*     */   {
/*  82 */     super(paramLong1, paramLong2, null);
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
/*     */   public Interval(long paramLong1, long paramLong2, DateTimeZone paramDateTimeZone)
/*     */   {
/*  96 */     super(paramLong1, paramLong2, ISOChronology.getInstance(paramDateTimeZone));
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
/*     */   public Interval(long paramLong1, long paramLong2, Chronology paramChronology)
/*     */   {
/* 109 */     super(paramLong1, paramLong2, paramChronology);
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
/*     */   public Interval(ReadableInstant paramReadableInstant1, ReadableInstant paramReadableInstant2)
/*     */   {
/* 122 */     super(paramReadableInstant1, paramReadableInstant2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Interval(ReadableInstant paramReadableInstant, ReadableDuration paramReadableDuration)
/*     */   {
/* 134 */     super(paramReadableInstant, paramReadableDuration);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Interval(ReadableDuration paramReadableDuration, ReadableInstant paramReadableInstant)
/*     */   {
/* 146 */     super(paramReadableDuration, paramReadableInstant);
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
/*     */   public Interval(ReadableInstant paramReadableInstant, ReadablePeriod paramReadablePeriod)
/*     */   {
/* 161 */     super(paramReadableInstant, paramReadablePeriod);
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
/*     */   public Interval(ReadablePeriod paramReadablePeriod, ReadableInstant paramReadableInstant)
/*     */   {
/* 176 */     super(paramReadablePeriod, paramReadableInstant);
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
/*     */   public Interval(Object paramObject)
/*     */   {
/* 193 */     super(paramObject, null);
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
/*     */   public Interval(Object paramObject, Chronology paramChronology)
/*     */   {
/* 212 */     super(paramObject, paramChronology);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Interval toInterval()
/*     */   {
/* 223 */     return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Interval overlap(ReadableInterval paramReadableInterval)
/*     */   {
/* 251 */     paramReadableInterval = DateTimeUtils.getReadableInterval(paramReadableInterval);
/* 252 */     if (!overlaps(paramReadableInterval)) {
/* 253 */       return null;
/*     */     }
/* 255 */     long l1 = Math.max(getStartMillis(), paramReadableInterval.getStartMillis());
/* 256 */     long l2 = Math.min(getEndMillis(), paramReadableInterval.getEndMillis());
/* 257 */     return new Interval(l1, l2, getChronology());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Interval gap(ReadableInterval paramReadableInterval)
/*     */   {
/* 286 */     paramReadableInterval = DateTimeUtils.getReadableInterval(paramReadableInterval);
/* 287 */     long l1 = paramReadableInterval.getStartMillis();
/* 288 */     long l2 = paramReadableInterval.getEndMillis();
/* 289 */     long l3 = getStartMillis();
/* 290 */     long l4 = getEndMillis();
/* 291 */     if (l3 > l2)
/* 292 */       return new Interval(l2, l3, getChronology());
/* 293 */     if (l1 > l4) {
/* 294 */       return new Interval(l4, l1, getChronology());
/*     */     }
/* 296 */     return null;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean abuts(ReadableInterval paramReadableInterval)
/*     */   {
/* 338 */     if (paramReadableInterval == null) {
/* 339 */       long l = DateTimeUtils.currentTimeMillis();
/* 340 */       return (getStartMillis() == l) || (getEndMillis() == l);
/*     */     }
/* 342 */     return (paramReadableInterval.getEndMillis() == getStartMillis()) || (getEndMillis() == paramReadableInterval.getStartMillis());
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
/*     */   public Interval withChronology(Chronology paramChronology)
/*     */   {
/* 355 */     if (getChronology() == paramChronology) {
/* 356 */       return this;
/*     */     }
/* 358 */     return new Interval(getStartMillis(), getEndMillis(), paramChronology);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Interval withStartMillis(long paramLong)
/*     */   {
/* 369 */     if (paramLong == getStartMillis()) {
/* 370 */       return this;
/*     */     }
/* 372 */     return new Interval(paramLong, getEndMillis(), getChronology());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Interval withStart(ReadableInstant paramReadableInstant)
/*     */   {
/* 383 */     long l = DateTimeUtils.getInstantMillis(paramReadableInstant);
/* 384 */     return withStartMillis(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Interval withEndMillis(long paramLong)
/*     */   {
/* 395 */     if (paramLong == getEndMillis()) {
/* 396 */       return this;
/*     */     }
/* 398 */     return new Interval(getStartMillis(), paramLong, getChronology());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Interval withEnd(ReadableInstant paramReadableInstant)
/*     */   {
/* 409 */     long l = DateTimeUtils.getInstantMillis(paramReadableInstant);
/* 410 */     return withEndMillis(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Interval withDurationAfterStart(ReadableDuration paramReadableDuration)
/*     */   {
/* 422 */     long l1 = DateTimeUtils.getDurationMillis(paramReadableDuration);
/* 423 */     if (l1 == toDurationMillis()) {
/* 424 */       return this;
/*     */     }
/* 426 */     Chronology localChronology = getChronology();
/* 427 */     long l2 = getStartMillis();
/* 428 */     long l3 = localChronology.add(l2, l1, 1);
/* 429 */     return new Interval(l2, l3, localChronology);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Interval withDurationBeforeEnd(ReadableDuration paramReadableDuration)
/*     */   {
/* 440 */     long l1 = DateTimeUtils.getDurationMillis(paramReadableDuration);
/* 441 */     if (l1 == toDurationMillis()) {
/* 442 */       return this;
/*     */     }
/* 444 */     Chronology localChronology = getChronology();
/* 445 */     long l2 = getEndMillis();
/* 446 */     long l3 = localChronology.add(l2, l1, -1);
/* 447 */     return new Interval(l3, l2, localChronology);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Interval withPeriodAfterStart(ReadablePeriod paramReadablePeriod)
/*     */   {
/* 459 */     if (paramReadablePeriod == null) {
/* 460 */       return withDurationAfterStart(null);
/*     */     }
/* 462 */     Chronology localChronology = getChronology();
/* 463 */     long l1 = getStartMillis();
/* 464 */     long l2 = localChronology.add(paramReadablePeriod, l1, 1);
/* 465 */     return new Interval(l1, l2, localChronology);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Interval withPeriodBeforeEnd(ReadablePeriod paramReadablePeriod)
/*     */   {
/* 476 */     if (paramReadablePeriod == null) {
/* 477 */       return withDurationBeforeEnd(null);
/*     */     }
/* 479 */     Chronology localChronology = getChronology();
/* 480 */     long l1 = getEndMillis();
/* 481 */     long l2 = localChronology.add(paramReadablePeriod, l1, -1);
/* 482 */     return new Interval(l2, l1, localChronology);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\Interval.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */