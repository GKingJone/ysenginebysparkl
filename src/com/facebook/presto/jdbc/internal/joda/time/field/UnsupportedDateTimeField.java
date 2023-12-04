/*     */ package com.facebook.presto.jdbc.internal.joda.time.field;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePartial;
/*     */ import java.io.Serializable;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class UnsupportedDateTimeField
/*     */   extends DateTimeField
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -1934618396111902255L;
/*     */   private static HashMap<DateTimeFieldType, UnsupportedDateTimeField> cCache;
/*     */   private final DateTimeFieldType iType;
/*     */   private final DurationField iDurationField;
/*     */   
/*     */   public static synchronized UnsupportedDateTimeField getInstance(DateTimeFieldType paramDateTimeFieldType, DurationField paramDurationField)
/*     */   {
/*     */     UnsupportedDateTimeField localUnsupportedDateTimeField;
/*  55 */     if (cCache == null) {
/*  56 */       cCache = new HashMap(7);
/*  57 */       localUnsupportedDateTimeField = null;
/*     */     } else {
/*  59 */       localUnsupportedDateTimeField = (UnsupportedDateTimeField)cCache.get(paramDateTimeFieldType);
/*  60 */       if ((localUnsupportedDateTimeField != null) && (localUnsupportedDateTimeField.getDurationField() != paramDurationField)) {
/*  61 */         localUnsupportedDateTimeField = null;
/*     */       }
/*     */     }
/*  64 */     if (localUnsupportedDateTimeField == null) {
/*  65 */       localUnsupportedDateTimeField = new UnsupportedDateTimeField(paramDateTimeFieldType, paramDurationField);
/*  66 */       cCache.put(paramDateTimeFieldType, localUnsupportedDateTimeField);
/*     */     }
/*  68 */     return localUnsupportedDateTimeField;
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
/*     */   private UnsupportedDateTimeField(DateTimeFieldType paramDateTimeFieldType, DurationField paramDurationField)
/*     */   {
/*  83 */     if ((paramDateTimeFieldType == null) || (paramDurationField == null)) {
/*  84 */       throw new IllegalArgumentException();
/*     */     }
/*  86 */     this.iType = paramDateTimeFieldType;
/*  87 */     this.iDurationField = paramDurationField;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeFieldType getType()
/*     */   {
/*  95 */     return this.iType;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  99 */     return this.iType.getName();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isSupported()
/*     */   {
/* 108 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isLenient()
/*     */   {
/* 117 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int get(long paramLong)
/*     */   {
/* 126 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAsText(long paramLong, Locale paramLocale)
/*     */   {
/* 135 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAsText(long paramLong)
/*     */   {
/* 144 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAsText(ReadablePartial paramReadablePartial, int paramInt, Locale paramLocale)
/*     */   {
/* 153 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAsText(ReadablePartial paramReadablePartial, Locale paramLocale)
/*     */   {
/* 162 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAsText(int paramInt, Locale paramLocale)
/*     */   {
/* 171 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAsShortText(long paramLong, Locale paramLocale)
/*     */   {
/* 180 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAsShortText(long paramLong)
/*     */   {
/* 189 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAsShortText(ReadablePartial paramReadablePartial, int paramInt, Locale paramLocale)
/*     */   {
/* 198 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAsShortText(ReadablePartial paramReadablePartial, Locale paramLocale)
/*     */   {
/* 207 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAsShortText(int paramInt, Locale paramLocale)
/*     */   {
/* 216 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long add(long paramLong, int paramInt)
/*     */   {
/* 225 */     return getDurationField().add(paramLong, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long add(long paramLong1, long paramLong2)
/*     */   {
/* 234 */     return getDurationField().add(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int[] add(ReadablePartial paramReadablePartial, int paramInt1, int[] paramArrayOfInt, int paramInt2)
/*     */   {
/* 243 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int[] addWrapPartial(ReadablePartial paramReadablePartial, int paramInt1, int[] paramArrayOfInt, int paramInt2)
/*     */   {
/* 252 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long addWrapField(long paramLong, int paramInt)
/*     */   {
/* 261 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int[] addWrapField(ReadablePartial paramReadablePartial, int paramInt1, int[] paramArrayOfInt, int paramInt2)
/*     */   {
/* 270 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getDifference(long paramLong1, long paramLong2)
/*     */   {
/* 279 */     return getDurationField().getDifference(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getDifferenceAsLong(long paramLong1, long paramLong2)
/*     */   {
/* 288 */     return getDurationField().getDifferenceAsLong(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long set(long paramLong, int paramInt)
/*     */   {
/* 297 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int[] set(ReadablePartial paramReadablePartial, int paramInt1, int[] paramArrayOfInt, int paramInt2)
/*     */   {
/* 306 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long set(long paramLong, String paramString, Locale paramLocale)
/*     */   {
/* 315 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long set(long paramLong, String paramString)
/*     */   {
/* 324 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int[] set(ReadablePartial paramReadablePartial, int paramInt, int[] paramArrayOfInt, String paramString, Locale paramLocale)
/*     */   {
/* 333 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationField getDurationField()
/*     */   {
/* 343 */     return this.iDurationField;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationField getRangeDurationField()
/*     */   {
/* 352 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isLeap(long paramLong)
/*     */   {
/* 361 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getLeapAmount(long paramLong)
/*     */   {
/* 370 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationField getLeapDurationField()
/*     */   {
/* 379 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMinimumValue()
/*     */   {
/* 388 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMinimumValue(long paramLong)
/*     */   {
/* 397 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMinimumValue(ReadablePartial paramReadablePartial)
/*     */   {
/* 406 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMinimumValue(ReadablePartial paramReadablePartial, int[] paramArrayOfInt)
/*     */   {
/* 415 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaximumValue()
/*     */   {
/* 424 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaximumValue(long paramLong)
/*     */   {
/* 433 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaximumValue(ReadablePartial paramReadablePartial)
/*     */   {
/* 442 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaximumValue(ReadablePartial paramReadablePartial, int[] paramArrayOfInt)
/*     */   {
/* 451 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaximumTextLength(Locale paramLocale)
/*     */   {
/* 460 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaximumShortTextLength(Locale paramLocale)
/*     */   {
/* 469 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long roundFloor(long paramLong)
/*     */   {
/* 478 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long roundCeiling(long paramLong)
/*     */   {
/* 487 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long roundHalfFloor(long paramLong)
/*     */   {
/* 496 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long roundHalfCeiling(long paramLong)
/*     */   {
/* 505 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long roundHalfEven(long paramLong)
/*     */   {
/* 514 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long remainder(long paramLong)
/*     */   {
/* 523 */     throw unsupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 533 */     return "UnsupportedDateTimeField";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Object readResolve()
/*     */   {
/* 540 */     return getInstance(this.iType, this.iDurationField);
/*     */   }
/*     */   
/*     */   private UnsupportedOperationException unsupported() {
/* 544 */     return new UnsupportedOperationException(this.iType + " field is unsupported");
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\field\UnsupportedDateTimeField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */