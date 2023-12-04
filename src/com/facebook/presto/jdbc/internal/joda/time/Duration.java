/*     */ package com.facebook.presto.jdbc.internal.joda.time;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.base.BaseDuration;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.FieldUtils;
/*     */ import java.io.Serializable;
/*     */ import org.joda.convert.FromString;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Duration
/*     */   extends BaseDuration
/*     */   implements ReadableDuration, Serializable
/*     */ {
/*  44 */   public static final Duration ZERO = new Duration(0L);
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final long serialVersionUID = 2471658376918L;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @FromString
/*     */   public static Duration parse(String paramString)
/*     */   {
/*  60 */     return new Duration(paramString);
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
/*     */   public static Duration standardDays(long paramLong)
/*     */   {
/*  82 */     if (paramLong == 0L) {
/*  83 */       return ZERO;
/*     */     }
/*  85 */     return new Duration(FieldUtils.safeMultiply(paramLong, 86400000));
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
/*     */   public static Duration standardHours(long paramLong)
/*     */   {
/* 105 */     if (paramLong == 0L) {
/* 106 */       return ZERO;
/*     */     }
/* 108 */     return new Duration(FieldUtils.safeMultiply(paramLong, 3600000));
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
/*     */   public static Duration standardMinutes(long paramLong)
/*     */   {
/* 128 */     if (paramLong == 0L) {
/* 129 */       return ZERO;
/*     */     }
/* 131 */     return new Duration(FieldUtils.safeMultiply(paramLong, 60000));
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
/*     */   public static Duration standardSeconds(long paramLong)
/*     */   {
/* 150 */     if (paramLong == 0L) {
/* 151 */       return ZERO;
/*     */     }
/* 153 */     return new Duration(FieldUtils.safeMultiply(paramLong, 1000));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Duration millis(long paramLong)
/*     */   {
/* 164 */     if (paramLong == 0L) {
/* 165 */       return ZERO;
/*     */     }
/* 167 */     return new Duration(paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Duration(long paramLong)
/*     */   {
/* 177 */     super(paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Duration(long paramLong1, long paramLong2)
/*     */   {
/* 188 */     super(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Duration(ReadableInstant paramReadableInstant1, ReadableInstant paramReadableInstant2)
/*     */   {
/* 199 */     super(paramReadableInstant1, paramReadableInstant2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Duration(Object paramObject)
/*     */   {
/* 210 */     super(paramObject);
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
/*     */   public long getStandardDays()
/*     */   {
/* 230 */     return getMillis() / 86400000L;
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
/*     */   public long getStandardHours()
/*     */   {
/* 248 */     return getMillis() / 3600000L;
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
/*     */   public long getStandardMinutes()
/*     */   {
/* 266 */     return getMillis() / 60000L;
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
/*     */   public long getStandardSeconds()
/*     */   {
/* 283 */     return getMillis() / 1000L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Duration toDuration()
/*     */   {
/* 294 */     return this;
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
/*     */   public Days toStandardDays()
/*     */   {
/* 311 */     long l = getStandardDays();
/* 312 */     return Days.days(FieldUtils.safeToInt(l));
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
/*     */   public Hours toStandardHours()
/*     */   {
/* 328 */     long l = getStandardHours();
/* 329 */     return Hours.hours(FieldUtils.safeToInt(l));
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
/*     */   public Minutes toStandardMinutes()
/*     */   {
/* 345 */     long l = getStandardMinutes();
/* 346 */     return Minutes.minutes(FieldUtils.safeToInt(l));
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
/*     */   public Seconds toStandardSeconds()
/*     */   {
/* 361 */     long l = getStandardSeconds();
/* 362 */     return Seconds.seconds(FieldUtils.safeToInt(l));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Duration withMillis(long paramLong)
/*     */   {
/* 373 */     if (paramLong == getMillis()) {
/* 374 */       return this;
/*     */     }
/* 376 */     return new Duration(paramLong);
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
/*     */   public Duration withDurationAdded(long paramLong, int paramInt)
/*     */   {
/* 390 */     if ((paramLong == 0L) || (paramInt == 0)) {
/* 391 */       return this;
/*     */     }
/* 393 */     long l1 = FieldUtils.safeMultiply(paramLong, paramInt);
/* 394 */     long l2 = FieldUtils.safeAdd(getMillis(), l1);
/* 395 */     return new Duration(l2);
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
/*     */   public Duration withDurationAdded(ReadableDuration paramReadableDuration, int paramInt)
/*     */   {
/* 409 */     if ((paramReadableDuration == null) || (paramInt == 0)) {
/* 410 */       return this;
/*     */     }
/* 412 */     return withDurationAdded(paramReadableDuration.getMillis(), paramInt);
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
/*     */   public Duration plus(long paramLong)
/*     */   {
/* 426 */     return withDurationAdded(paramLong, 1);
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
/*     */   public Duration plus(ReadableDuration paramReadableDuration)
/*     */   {
/* 439 */     if (paramReadableDuration == null) {
/* 440 */       return this;
/*     */     }
/* 442 */     return withDurationAdded(paramReadableDuration.getMillis(), 1);
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
/*     */   public Duration minus(long paramLong)
/*     */   {
/* 455 */     return withDurationAdded(paramLong, -1);
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
/*     */   public Duration minus(ReadableDuration paramReadableDuration)
/*     */   {
/* 468 */     if (paramReadableDuration == null) {
/* 469 */       return this;
/*     */     }
/* 471 */     return withDurationAdded(paramReadableDuration.getMillis(), -1);
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
/*     */   public Duration multipliedBy(long paramLong)
/*     */   {
/* 485 */     if (paramLong == 1L) {
/* 486 */       return this;
/*     */     }
/* 488 */     return new Duration(FieldUtils.safeMultiply(getMillis(), paramLong));
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
/*     */   public Duration dividedBy(long paramLong)
/*     */   {
/* 502 */     if (paramLong == 1L) {
/* 503 */       return this;
/*     */     }
/* 505 */     return new Duration(FieldUtils.safeDivide(getMillis(), paramLong));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Duration negated()
/*     */   {
/* 515 */     if (getMillis() == Long.MIN_VALUE) {
/* 516 */       throw new ArithmeticException("Negation of this duration would overflow");
/*     */     }
/* 518 */     return new Duration(-getMillis());
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\Duration.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */