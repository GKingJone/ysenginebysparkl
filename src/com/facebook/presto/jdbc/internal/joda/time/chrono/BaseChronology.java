/*     */ package com.facebook.presto.jdbc.internal.joda.time.chrono;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.IllegalFieldValueException;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePartial;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePeriod;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.FieldUtils;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.UnsupportedDateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.UnsupportedDurationField;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class BaseChronology
/*     */   extends Chronology
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -7310865996721419676L;
/*     */   
/*     */   public abstract DateTimeZone getZone();
/*     */   
/*     */   public abstract Chronology withUTC();
/*     */   
/*     */   public abstract Chronology withZone(DateTimeZone paramDateTimeZone);
/*     */   
/*     */   public long getDateTimeMillis(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     throws IllegalArgumentException
/*     */   {
/* 102 */     long l = year().set(0L, paramInt1);
/* 103 */     l = monthOfYear().set(l, paramInt2);
/* 104 */     l = dayOfMonth().set(l, paramInt3);
/* 105 */     return millisOfDay().set(l, paramInt4);
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
/*     */   public long getDateTimeMillis(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7)
/*     */     throws IllegalArgumentException
/*     */   {
/* 132 */     long l = year().set(0L, paramInt1);
/* 133 */     l = monthOfYear().set(l, paramInt2);
/* 134 */     l = dayOfMonth().set(l, paramInt3);
/* 135 */     l = hourOfDay().set(l, paramInt4);
/* 136 */     l = minuteOfHour().set(l, paramInt5);
/* 137 */     l = secondOfMinute().set(l, paramInt6);
/* 138 */     return millisOfSecond().set(l, paramInt7);
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
/*     */   public long getDateTimeMillis(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
/*     */     throws IllegalArgumentException
/*     */   {
/* 163 */     paramLong = hourOfDay().set(paramLong, paramInt1);
/* 164 */     paramLong = minuteOfHour().set(paramLong, paramInt2);
/* 165 */     paramLong = secondOfMinute().set(paramLong, paramInt3);
/* 166 */     return millisOfSecond().set(paramLong, paramInt4);
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
/*     */   public void validate(ReadablePartial paramReadablePartial, int[] paramArrayOfInt)
/*     */   {
/* 183 */     int i = paramReadablePartial.size();
/* 184 */     int k; DateTimeField localDateTimeField; for (int j = 0; j < i; j++) {
/* 185 */       k = paramArrayOfInt[j];
/* 186 */       localDateTimeField = paramReadablePartial.getField(j);
/* 187 */       if (k < localDateTimeField.getMinimumValue()) {
/* 188 */         throw new IllegalFieldValueException(localDateTimeField.getType(), Integer.valueOf(k), Integer.valueOf(localDateTimeField.getMinimumValue()), null);
/*     */       }
/*     */       
/*     */ 
/* 192 */       if (k > localDateTimeField.getMaximumValue()) {
/* 193 */         throw new IllegalFieldValueException(localDateTimeField.getType(), Integer.valueOf(k), null, Integer.valueOf(localDateTimeField.getMaximumValue()));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 199 */     for (j = 0; j < i; j++) {
/* 200 */       k = paramArrayOfInt[j];
/* 201 */       localDateTimeField = paramReadablePartial.getField(j);
/* 202 */       if (k < localDateTimeField.getMinimumValue(paramReadablePartial, paramArrayOfInt)) {
/* 203 */         throw new IllegalFieldValueException(localDateTimeField.getType(), Integer.valueOf(k), Integer.valueOf(localDateTimeField.getMinimumValue(paramReadablePartial, paramArrayOfInt)), null);
/*     */       }
/*     */       
/*     */ 
/* 207 */       if (k > localDateTimeField.getMaximumValue(paramReadablePartial, paramArrayOfInt)) {
/* 208 */         throw new IllegalFieldValueException(localDateTimeField.getType(), Integer.valueOf(k), null, Integer.valueOf(localDateTimeField.getMaximumValue(paramReadablePartial, paramArrayOfInt)));
/*     */       }
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
/*     */   public int[] get(ReadablePartial paramReadablePartial, long paramLong)
/*     */   {
/* 223 */     int i = paramReadablePartial.size();
/* 224 */     int[] arrayOfInt = new int[i];
/* 225 */     for (int j = 0; j < i; j++) {
/* 226 */       arrayOfInt[j] = paramReadablePartial.getFieldType(j).getField(this).get(paramLong);
/*     */     }
/* 228 */     return arrayOfInt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long set(ReadablePartial paramReadablePartial, long paramLong)
/*     */   {
/* 239 */     int i = 0; for (int j = paramReadablePartial.size(); i < j; i++) {
/* 240 */       paramLong = paramReadablePartial.getFieldType(i).getField(this).set(paramLong, paramReadablePartial.getValue(i));
/*     */     }
/* 242 */     return paramLong;
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
/*     */   public int[] get(ReadablePeriod paramReadablePeriod, long paramLong1, long paramLong2)
/*     */   {
/* 255 */     int i = paramReadablePeriod.size();
/* 256 */     int[] arrayOfInt = new int[i];
/* 257 */     if (paramLong1 != paramLong2) {
/* 258 */       for (int j = 0; j < i; j++) {
/* 259 */         DurationField localDurationField = paramReadablePeriod.getFieldType(j).getField(this);
/* 260 */         int k = localDurationField.getDifference(paramLong2, paramLong1);
/* 261 */         if (k != 0) {
/* 262 */           paramLong1 = localDurationField.add(paramLong1, k);
/*     */         }
/* 264 */         arrayOfInt[j] = k;
/*     */       }
/*     */     }
/* 267 */     return arrayOfInt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int[] get(ReadablePeriod paramReadablePeriod, long paramLong)
/*     */   {
/* 278 */     int i = paramReadablePeriod.size();
/* 279 */     int[] arrayOfInt = new int[i];
/* 280 */     if (paramLong != 0L) {
/* 281 */       long l = 0L;
/* 282 */       for (int j = 0; j < i; j++) {
/* 283 */         DurationField localDurationField = paramReadablePeriod.getFieldType(j).getField(this);
/* 284 */         if (localDurationField.isPrecise()) {
/* 285 */           int k = localDurationField.getDifference(paramLong, l);
/* 286 */           l = localDurationField.add(l, k);
/* 287 */           arrayOfInt[j] = k;
/*     */         }
/*     */       }
/*     */     }
/* 291 */     return arrayOfInt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long add(ReadablePeriod paramReadablePeriod, long paramLong, int paramInt)
/*     */   {
/* 303 */     if ((paramInt != 0) && (paramReadablePeriod != null)) {
/* 304 */       int i = 0; for (int j = paramReadablePeriod.size(); i < j; i++) {
/* 305 */         long l = paramReadablePeriod.getValue(i);
/* 306 */         if (l != 0L) {
/* 307 */           paramLong = paramReadablePeriod.getFieldType(i).getField(this).add(paramLong, l * paramInt);
/*     */         }
/*     */       }
/*     */     }
/* 311 */     return paramLong;
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
/*     */   public long add(long paramLong1, long paramLong2, int paramInt)
/*     */   {
/* 324 */     if ((paramLong2 == 0L) || (paramInt == 0)) {
/* 325 */       return paramLong1;
/*     */     }
/* 327 */     long l = FieldUtils.safeMultiply(paramLong2, paramInt);
/* 328 */     return FieldUtils.safeAdd(paramLong1, l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationField millis()
/*     */   {
/* 339 */     return UnsupportedDurationField.getInstance(DurationFieldType.millis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField millisOfSecond()
/*     */   {
/* 348 */     return UnsupportedDateTimeField.getInstance(DateTimeFieldType.millisOfSecond(), millis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField millisOfDay()
/*     */   {
/* 357 */     return UnsupportedDateTimeField.getInstance(DateTimeFieldType.millisOfDay(), millis());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationField seconds()
/*     */   {
/* 368 */     return UnsupportedDurationField.getInstance(DurationFieldType.seconds());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField secondOfMinute()
/*     */   {
/* 377 */     return UnsupportedDateTimeField.getInstance(DateTimeFieldType.secondOfMinute(), seconds());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField secondOfDay()
/*     */   {
/* 386 */     return UnsupportedDateTimeField.getInstance(DateTimeFieldType.secondOfDay(), seconds());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationField minutes()
/*     */   {
/* 397 */     return UnsupportedDurationField.getInstance(DurationFieldType.minutes());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField minuteOfHour()
/*     */   {
/* 406 */     return UnsupportedDateTimeField.getInstance(DateTimeFieldType.minuteOfHour(), minutes());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField minuteOfDay()
/*     */   {
/* 415 */     return UnsupportedDateTimeField.getInstance(DateTimeFieldType.minuteOfDay(), minutes());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationField hours()
/*     */   {
/* 426 */     return UnsupportedDurationField.getInstance(DurationFieldType.hours());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField hourOfDay()
/*     */   {
/* 435 */     return UnsupportedDateTimeField.getInstance(DateTimeFieldType.hourOfDay(), hours());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField clockhourOfDay()
/*     */   {
/* 444 */     return UnsupportedDateTimeField.getInstance(DateTimeFieldType.clockhourOfDay(), hours());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationField halfdays()
/*     */   {
/* 455 */     return UnsupportedDurationField.getInstance(DurationFieldType.halfdays());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField hourOfHalfday()
/*     */   {
/* 464 */     return UnsupportedDateTimeField.getInstance(DateTimeFieldType.hourOfHalfday(), hours());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField clockhourOfHalfday()
/*     */   {
/* 473 */     return UnsupportedDateTimeField.getInstance(DateTimeFieldType.clockhourOfHalfday(), hours());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField halfdayOfDay()
/*     */   {
/* 482 */     return UnsupportedDateTimeField.getInstance(DateTimeFieldType.halfdayOfDay(), halfdays());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationField days()
/*     */   {
/* 493 */     return UnsupportedDurationField.getInstance(DurationFieldType.days());
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
/*     */   public DateTimeField dayOfWeek()
/*     */   {
/* 506 */     return UnsupportedDateTimeField.getInstance(DateTimeFieldType.dayOfWeek(), days());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField dayOfMonth()
/*     */   {
/* 515 */     return UnsupportedDateTimeField.getInstance(DateTimeFieldType.dayOfMonth(), days());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField dayOfYear()
/*     */   {
/* 524 */     return UnsupportedDateTimeField.getInstance(DateTimeFieldType.dayOfYear(), days());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationField weeks()
/*     */   {
/* 535 */     return UnsupportedDurationField.getInstance(DurationFieldType.weeks());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField weekOfWeekyear()
/*     */   {
/* 544 */     return UnsupportedDateTimeField.getInstance(DateTimeFieldType.weekOfWeekyear(), weeks());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationField weekyears()
/*     */   {
/* 555 */     return UnsupportedDurationField.getInstance(DurationFieldType.weekyears());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField weekyear()
/*     */   {
/* 564 */     return UnsupportedDateTimeField.getInstance(DateTimeFieldType.weekyear(), weekyears());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField weekyearOfCentury()
/*     */   {
/* 573 */     return UnsupportedDateTimeField.getInstance(DateTimeFieldType.weekyearOfCentury(), weekyears());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationField months()
/*     */   {
/* 584 */     return UnsupportedDurationField.getInstance(DurationFieldType.months());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField monthOfYear()
/*     */   {
/* 593 */     return UnsupportedDateTimeField.getInstance(DateTimeFieldType.monthOfYear(), months());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationField years()
/*     */   {
/* 604 */     return UnsupportedDurationField.getInstance(DurationFieldType.years());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField year()
/*     */   {
/* 613 */     return UnsupportedDateTimeField.getInstance(DateTimeFieldType.year(), years());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField yearOfEra()
/*     */   {
/* 622 */     return UnsupportedDateTimeField.getInstance(DateTimeFieldType.yearOfEra(), years());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField yearOfCentury()
/*     */   {
/* 631 */     return UnsupportedDateTimeField.getInstance(DateTimeFieldType.yearOfCentury(), years());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationField centuries()
/*     */   {
/* 642 */     return UnsupportedDurationField.getInstance(DurationFieldType.centuries());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField centuryOfEra()
/*     */   {
/* 651 */     return UnsupportedDateTimeField.getInstance(DateTimeFieldType.centuryOfEra(), centuries());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationField eras()
/*     */   {
/* 662 */     return UnsupportedDurationField.getInstance(DurationFieldType.eras());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeField era()
/*     */   {
/* 671 */     return UnsupportedDateTimeField.getInstance(DateTimeFieldType.era(), eras());
/*     */   }
/*     */   
/*     */   public abstract String toString();
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\BaseChronology.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */