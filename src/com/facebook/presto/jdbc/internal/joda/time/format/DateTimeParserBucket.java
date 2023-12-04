/*     */ package com.facebook.presto.jdbc.internal.joda.time.format;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeUtils;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.IllegalFieldValueException;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.IllegalInstantException;
/*     */ import java.util.Arrays;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DateTimeParserBucket
/*     */ {
/*     */   private final Chronology iChrono;
/*     */   private final long iMillis;
/*     */   private final Locale iLocale;
/*     */   private final int iDefaultYear;
/*     */   private final DateTimeZone iDefaultZone;
/*     */   private final Integer iDefaultPivotYear;
/*     */   private DateTimeZone iZone;
/*     */   private Integer iOffset;
/*     */   private Integer iPivotYear;
/*     */   private SavedField[] iSavedFields;
/*     */   private int iSavedFieldsCount;
/*     */   private boolean iSavedFieldsShared;
/*     */   private Object iSavedState;
/*     */   
/*     */   @Deprecated
/*     */   public DateTimeParserBucket(long paramLong, Chronology paramChronology, Locale paramLocale)
/*     */   {
/*  94 */     this(paramLong, paramChronology, paramLocale, null, 2000);
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
/*     */   @Deprecated
/*     */   public DateTimeParserBucket(long paramLong, Chronology paramChronology, Locale paramLocale, Integer paramInteger)
/*     */   {
/* 110 */     this(paramLong, paramChronology, paramLocale, paramInteger, 2000);
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
/*     */   public DateTimeParserBucket(long paramLong, Chronology paramChronology, Locale paramLocale, Integer paramInteger, int paramInt)
/*     */   {
/* 127 */     paramChronology = DateTimeUtils.getChronology(paramChronology);
/* 128 */     this.iMillis = paramLong;
/* 129 */     this.iDefaultZone = paramChronology.getZone();
/* 130 */     this.iChrono = paramChronology.withUTC();
/* 131 */     this.iLocale = (paramLocale == null ? Locale.getDefault() : paramLocale);
/* 132 */     this.iDefaultYear = paramInt;
/* 133 */     this.iDefaultPivotYear = paramInteger;
/*     */     
/* 135 */     this.iZone = this.iDefaultZone;
/* 136 */     this.iPivotYear = this.iDefaultPivotYear;
/* 137 */     this.iSavedFields = new SavedField[8];
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
/*     */   public void reset()
/*     */   {
/* 150 */     this.iZone = this.iDefaultZone;
/* 151 */     this.iOffset = null;
/* 152 */     this.iPivotYear = this.iDefaultPivotYear;
/* 153 */     this.iSavedFieldsCount = 0;
/* 154 */     this.iSavedFieldsShared = false;
/* 155 */     this.iSavedState = null;
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
/*     */   public long parseMillis(DateTimeParser paramDateTimeParser, CharSequence paramCharSequence)
/*     */   {
/* 174 */     reset();
/* 175 */     return doParseMillis(DateTimeParserInternalParser.of(paramDateTimeParser), paramCharSequence);
/*     */   }
/*     */   
/*     */   long doParseMillis(InternalParser paramInternalParser, CharSequence paramCharSequence) {
/* 179 */     int i = paramInternalParser.parseInto(this, paramCharSequence, 0);
/* 180 */     if (i >= 0) {
/* 181 */       if (i >= paramCharSequence.length()) {
/* 182 */         return computeMillis(true, paramCharSequence);
/*     */       }
/*     */     } else {
/* 185 */       i ^= 0xFFFFFFFF;
/*     */     }
/* 187 */     throw new IllegalArgumentException(FormatUtils.createErrorMessage(paramCharSequence.toString(), i));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Chronology getChronology()
/*     */   {
/* 195 */     return this.iChrono;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Locale getLocale()
/*     */   {
/* 205 */     return this.iLocale;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public DateTimeZone getZone()
/*     */   {
/* 213 */     return this.iZone;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setZone(DateTimeZone paramDateTimeZone)
/*     */   {
/* 220 */     this.iSavedState = null;
/* 221 */     this.iZone = paramDateTimeZone;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public int getOffset()
/*     */   {
/* 231 */     return this.iOffset != null ? this.iOffset.intValue() : 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Integer getOffsetInteger()
/*     */   {
/* 238 */     return this.iOffset;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   @Deprecated
/*     */   public void setOffset(int paramInt)
/*     */   {
/* 247 */     this.iSavedState = null;
/* 248 */     this.iOffset = Integer.valueOf(paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setOffset(Integer paramInteger)
/*     */   {
/* 255 */     this.iSavedState = null;
/* 256 */     this.iOffset = paramInteger;
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
/*     */   public Integer getPivotYear()
/*     */   {
/* 273 */     return this.iPivotYear;
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
/*     */   @Deprecated
/*     */   public void setPivotYear(Integer paramInteger)
/*     */   {
/* 288 */     this.iPivotYear = paramInteger;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void saveField(DateTimeField paramDateTimeField, int paramInt)
/*     */   {
/* 299 */     obtainSaveField().init(paramDateTimeField, paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void saveField(DateTimeFieldType paramDateTimeFieldType, int paramInt)
/*     */   {
/* 309 */     obtainSaveField().init(paramDateTimeFieldType.getField(this.iChrono), paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void saveField(DateTimeFieldType paramDateTimeFieldType, String paramString, Locale paramLocale)
/*     */   {
/* 320 */     obtainSaveField().init(paramDateTimeFieldType.getField(this.iChrono), paramString, paramLocale);
/*     */   }
/*     */   
/*     */   private SavedField obtainSaveField() {
/* 324 */     Object localObject1 = this.iSavedFields;
/* 325 */     int i = this.iSavedFieldsCount;
/*     */     
/* 327 */     if ((i == localObject1.length) || (this.iSavedFieldsShared))
/*     */     {
/* 329 */       localObject2 = new SavedField[i == localObject1.length ? i * 2 : localObject1.length];
/*     */       
/* 331 */       System.arraycopy(localObject1, 0, localObject2, 0, i);
/* 332 */       this.iSavedFields = (localObject1 = localObject2);
/* 333 */       this.iSavedFieldsShared = false;
/*     */     }
/*     */     
/* 336 */     this.iSavedState = null;
/* 337 */     Object localObject2 = localObject1[i];
/* 338 */     if (localObject2 == null) {
/* 339 */       localObject2 = localObject1[i] = new SavedField();
/*     */     }
/* 341 */     this.iSavedFieldsCount = (i + 1);
/* 342 */     return (SavedField)localObject2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object saveState()
/*     */   {
/* 353 */     if (this.iSavedState == null) {
/* 354 */       this.iSavedState = new SavedState();
/*     */     }
/* 356 */     return this.iSavedState;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean restoreState(Object paramObject)
/*     */   {
/* 368 */     if (((paramObject instanceof SavedState)) && 
/* 369 */       (((SavedState)paramObject).restoreState(this))) {
/* 370 */       this.iSavedState = paramObject;
/* 371 */       return true;
/*     */     }
/*     */     
/* 374 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long computeMillis()
/*     */   {
/* 385 */     return computeMillis(false, (CharSequence)null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long computeMillis(boolean paramBoolean)
/*     */   {
/* 397 */     return computeMillis(paramBoolean, (CharSequence)null);
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
/*     */   public long computeMillis(boolean paramBoolean, String paramString)
/*     */   {
/* 411 */     return computeMillis(paramBoolean, paramString);
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
/*     */   public long computeMillis(boolean paramBoolean, CharSequence paramCharSequence)
/*     */   {
/* 425 */     SavedField[] arrayOfSavedField = this.iSavedFields;
/* 426 */     int i = this.iSavedFieldsCount;
/* 427 */     if (this.iSavedFieldsShared)
/*     */     {
/* 429 */       this.iSavedFields = (arrayOfSavedField = (SavedField[])this.iSavedFields.clone());
/* 430 */       this.iSavedFieldsShared = false;
/*     */     }
/* 432 */     sort(arrayOfSavedField, i);
/* 433 */     if (i > 0)
/*     */     {
/* 435 */       DurationField localDurationField1 = DurationFieldType.months().getField(this.iChrono);
/* 436 */       DurationField localDurationField2 = DurationFieldType.days().getField(this.iChrono);
/* 437 */       DurationField localDurationField3 = arrayOfSavedField[0].iField.getDurationField();
/* 438 */       if ((compareReverse(localDurationField3, localDurationField1) >= 0) && (compareReverse(localDurationField3, localDurationField2) <= 0)) {
/* 439 */         saveField(DateTimeFieldType.year(), this.iDefaultYear);
/* 440 */         return computeMillis(paramBoolean, paramCharSequence);
/*     */       }
/*     */     }
/*     */     
/* 444 */     long l = this.iMillis;
/*     */     try {
/* 446 */       for (int j = 0; j < i; j++) {
/* 447 */         l = arrayOfSavedField[j].set(l, paramBoolean);
/*     */       }
/* 449 */       if (paramBoolean) {
/* 450 */         for (j = 0; j < i; j++) {
/* 451 */           l = arrayOfSavedField[j].set(l, j == i - 1);
/*     */         }
/*     */       }
/*     */     } catch (IllegalFieldValueException localIllegalFieldValueException) {
/* 455 */       if (paramCharSequence != null) {
/* 456 */         localIllegalFieldValueException.prependMessage("Cannot parse \"" + paramCharSequence + '"');
/*     */       }
/* 458 */       throw localIllegalFieldValueException;
/*     */     }
/*     */     
/* 461 */     if (this.iOffset != null) {
/* 462 */       l -= this.iOffset.intValue();
/* 463 */     } else if (this.iZone != null) {
/* 464 */       int k = this.iZone.getOffsetFromLocal(l);
/* 465 */       l -= k;
/* 466 */       if (k != this.iZone.getOffset(l)) {
/* 467 */         String str = "Illegal instant due to time zone offset transition (" + this.iZone + ')';
/* 468 */         if (paramCharSequence != null) {
/* 469 */           str = "Cannot parse \"" + paramCharSequence + "\": " + str;
/*     */         }
/* 471 */         throw new IllegalInstantException(str);
/*     */       }
/*     */     }
/*     */     
/* 475 */     return l;
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
/*     */   private static void sort(SavedField[] paramArrayOfSavedField, int paramInt)
/*     */   {
/* 497 */     if (paramInt > 10) {
/* 498 */       Arrays.sort(paramArrayOfSavedField, 0, paramInt);
/*     */     } else {
/* 500 */       for (int i = 0; i < paramInt; i++) {
/* 501 */         for (int j = i; (j > 0) && (paramArrayOfSavedField[(j - 1)].compareTo(paramArrayOfSavedField[j]) > 0); j--) {
/* 502 */           SavedField localSavedField = paramArrayOfSavedField[j];
/* 503 */           paramArrayOfSavedField[j] = paramArrayOfSavedField[(j - 1)];
/* 504 */           paramArrayOfSavedField[(j - 1)] = localSavedField;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   class SavedState {
/*     */     final DateTimeZone iZone;
/*     */     final Integer iOffset;
/*     */     final SavedField[] iSavedFields;
/*     */     final int iSavedFieldsCount;
/*     */     
/*     */     SavedState() {
/* 517 */       this.iZone = DateTimeParserBucket.this.iZone;
/* 518 */       this.iOffset = DateTimeParserBucket.this.iOffset;
/* 519 */       this.iSavedFields = DateTimeParserBucket.this.iSavedFields;
/* 520 */       this.iSavedFieldsCount = DateTimeParserBucket.this.iSavedFieldsCount;
/*     */     }
/*     */     
/*     */     boolean restoreState(DateTimeParserBucket paramDateTimeParserBucket) {
/* 524 */       if (paramDateTimeParserBucket != DateTimeParserBucket.this)
/*     */       {
/* 526 */         return false;
/*     */       }
/* 528 */       paramDateTimeParserBucket.iZone = this.iZone;
/* 529 */       paramDateTimeParserBucket.iOffset = this.iOffset;
/* 530 */       paramDateTimeParserBucket.iSavedFields = this.iSavedFields;
/* 531 */       if (this.iSavedFieldsCount < paramDateTimeParserBucket.iSavedFieldsCount)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 536 */         paramDateTimeParserBucket.iSavedFieldsShared = true;
/*     */       }
/* 538 */       paramDateTimeParserBucket.iSavedFieldsCount = this.iSavedFieldsCount;
/* 539 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */   static class SavedField
/*     */     implements Comparable<SavedField>
/*     */   {
/*     */     DateTimeField iField;
/*     */     int iValue;
/*     */     String iText;
/*     */     Locale iLocale;
/*     */     
/*     */     void init(DateTimeField paramDateTimeField, int paramInt)
/*     */     {
/* 553 */       this.iField = paramDateTimeField;
/* 554 */       this.iValue = paramInt;
/* 555 */       this.iText = null;
/* 556 */       this.iLocale = null;
/*     */     }
/*     */     
/*     */     void init(DateTimeField paramDateTimeField, String paramString, Locale paramLocale) {
/* 560 */       this.iField = paramDateTimeField;
/* 561 */       this.iValue = 0;
/* 562 */       this.iText = paramString;
/* 563 */       this.iLocale = paramLocale;
/*     */     }
/*     */     
/*     */     long set(long paramLong, boolean paramBoolean) {
/* 567 */       if (this.iText == null) {
/* 568 */         paramLong = this.iField.set(paramLong, this.iValue);
/*     */       } else {
/* 570 */         paramLong = this.iField.set(paramLong, this.iText, this.iLocale);
/*     */       }
/* 572 */       if (paramBoolean) {
/* 573 */         paramLong = this.iField.roundFloor(paramLong);
/*     */       }
/* 575 */       return paramLong;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public int compareTo(SavedField paramSavedField)
/*     */     {
/* 584 */       DateTimeField localDateTimeField = paramSavedField.iField;
/* 585 */       int i = DateTimeParserBucket.compareReverse(this.iField.getRangeDurationField(), localDateTimeField.getRangeDurationField());
/*     */       
/* 587 */       if (i != 0) {
/* 588 */         return i;
/*     */       }
/* 590 */       return DateTimeParserBucket.compareReverse(this.iField.getDurationField(), localDateTimeField.getDurationField());
/*     */     }
/*     */   }
/*     */   
/*     */   static int compareReverse(DurationField paramDurationField1, DurationField paramDurationField2)
/*     */   {
/* 596 */     if ((paramDurationField1 == null) || (!paramDurationField1.isSupported())) {
/* 597 */       if ((paramDurationField2 == null) || (!paramDurationField2.isSupported())) {
/* 598 */         return 0;
/*     */       }
/* 600 */       return -1;
/*     */     }
/* 602 */     if ((paramDurationField2 == null) || (!paramDurationField2.isSupported())) {
/* 603 */       return 1;
/*     */     }
/* 605 */     return -paramDurationField1.compareTo(paramDurationField2);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\format\DateTimeParserBucket.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */