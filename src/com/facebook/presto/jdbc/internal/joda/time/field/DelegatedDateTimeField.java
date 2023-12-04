/*     */ package com.facebook.presto.jdbc.internal.joda.time.field;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePartial;
/*     */ import java.io.Serializable;
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
/*     */ public class DelegatedDateTimeField
/*     */   extends DateTimeField
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -4730164440214502503L;
/*     */   private final DateTimeField iField;
/*     */   private final DurationField iRangeDurationField;
/*     */   private final DateTimeFieldType iType;
/*     */   
/*     */   public DelegatedDateTimeField(DateTimeField paramDateTimeField)
/*     */   {
/*  54 */     this(paramDateTimeField, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DelegatedDateTimeField(DateTimeField paramDateTimeField, DateTimeFieldType paramDateTimeFieldType)
/*     */   {
/*  64 */     this(paramDateTimeField, null, paramDateTimeFieldType);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DelegatedDateTimeField(DateTimeField paramDateTimeField, DurationField paramDurationField, DateTimeFieldType paramDateTimeFieldType)
/*     */   {
/*  76 */     if (paramDateTimeField == null) {
/*  77 */       throw new IllegalArgumentException("The field must not be null");
/*     */     }
/*  79 */     this.iField = paramDateTimeField;
/*  80 */     this.iRangeDurationField = paramDurationField;
/*  81 */     this.iType = (paramDateTimeFieldType == null ? paramDateTimeField.getType() : paramDateTimeFieldType);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final DateTimeField getWrappedField()
/*     */   {
/*  90 */     return this.iField;
/*     */   }
/*     */   
/*     */   public DateTimeFieldType getType() {
/*  94 */     return this.iType;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  98 */     return this.iType.getName();
/*     */   }
/*     */   
/*     */   public boolean isSupported() {
/* 102 */     return this.iField.isSupported();
/*     */   }
/*     */   
/*     */   public boolean isLenient() {
/* 106 */     return this.iField.isLenient();
/*     */   }
/*     */   
/*     */   public int get(long paramLong) {
/* 110 */     return this.iField.get(paramLong);
/*     */   }
/*     */   
/*     */   public String getAsText(long paramLong, Locale paramLocale) {
/* 114 */     return this.iField.getAsText(paramLong, paramLocale);
/*     */   }
/*     */   
/*     */   public String getAsText(long paramLong) {
/* 118 */     return this.iField.getAsText(paramLong);
/*     */   }
/*     */   
/*     */   public String getAsText(ReadablePartial paramReadablePartial, int paramInt, Locale paramLocale) {
/* 122 */     return this.iField.getAsText(paramReadablePartial, paramInt, paramLocale);
/*     */   }
/*     */   
/*     */   public String getAsText(ReadablePartial paramReadablePartial, Locale paramLocale) {
/* 126 */     return this.iField.getAsText(paramReadablePartial, paramLocale);
/*     */   }
/*     */   
/*     */   public String getAsText(int paramInt, Locale paramLocale) {
/* 130 */     return this.iField.getAsText(paramInt, paramLocale);
/*     */   }
/*     */   
/*     */   public String getAsShortText(long paramLong, Locale paramLocale) {
/* 134 */     return this.iField.getAsShortText(paramLong, paramLocale);
/*     */   }
/*     */   
/*     */   public String getAsShortText(long paramLong) {
/* 138 */     return this.iField.getAsShortText(paramLong);
/*     */   }
/*     */   
/*     */   public String getAsShortText(ReadablePartial paramReadablePartial, int paramInt, Locale paramLocale) {
/* 142 */     return this.iField.getAsShortText(paramReadablePartial, paramInt, paramLocale);
/*     */   }
/*     */   
/*     */   public String getAsShortText(ReadablePartial paramReadablePartial, Locale paramLocale) {
/* 146 */     return this.iField.getAsShortText(paramReadablePartial, paramLocale);
/*     */   }
/*     */   
/*     */   public String getAsShortText(int paramInt, Locale paramLocale) {
/* 150 */     return this.iField.getAsShortText(paramInt, paramLocale);
/*     */   }
/*     */   
/*     */   public long add(long paramLong, int paramInt) {
/* 154 */     return this.iField.add(paramLong, paramInt);
/*     */   }
/*     */   
/*     */   public long add(long paramLong1, long paramLong2) {
/* 158 */     return this.iField.add(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */   public int[] add(ReadablePartial paramReadablePartial, int paramInt1, int[] paramArrayOfInt, int paramInt2) {
/* 162 */     return this.iField.add(paramReadablePartial, paramInt1, paramArrayOfInt, paramInt2);
/*     */   }
/*     */   
/*     */   public int[] addWrapPartial(ReadablePartial paramReadablePartial, int paramInt1, int[] paramArrayOfInt, int paramInt2) {
/* 166 */     return this.iField.addWrapPartial(paramReadablePartial, paramInt1, paramArrayOfInt, paramInt2);
/*     */   }
/*     */   
/*     */   public long addWrapField(long paramLong, int paramInt) {
/* 170 */     return this.iField.addWrapField(paramLong, paramInt);
/*     */   }
/*     */   
/*     */   public int[] addWrapField(ReadablePartial paramReadablePartial, int paramInt1, int[] paramArrayOfInt, int paramInt2) {
/* 174 */     return this.iField.addWrapField(paramReadablePartial, paramInt1, paramArrayOfInt, paramInt2);
/*     */   }
/*     */   
/*     */   public int getDifference(long paramLong1, long paramLong2) {
/* 178 */     return this.iField.getDifference(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */   public long getDifferenceAsLong(long paramLong1, long paramLong2) {
/* 182 */     return this.iField.getDifferenceAsLong(paramLong1, paramLong2);
/*     */   }
/*     */   
/*     */   public long set(long paramLong, int paramInt) {
/* 186 */     return this.iField.set(paramLong, paramInt);
/*     */   }
/*     */   
/*     */   public long set(long paramLong, String paramString, Locale paramLocale) {
/* 190 */     return this.iField.set(paramLong, paramString, paramLocale);
/*     */   }
/*     */   
/*     */   public long set(long paramLong, String paramString) {
/* 194 */     return this.iField.set(paramLong, paramString);
/*     */   }
/*     */   
/*     */   public int[] set(ReadablePartial paramReadablePartial, int paramInt1, int[] paramArrayOfInt, int paramInt2) {
/* 198 */     return this.iField.set(paramReadablePartial, paramInt1, paramArrayOfInt, paramInt2);
/*     */   }
/*     */   
/*     */   public int[] set(ReadablePartial paramReadablePartial, int paramInt, int[] paramArrayOfInt, String paramString, Locale paramLocale) {
/* 202 */     return this.iField.set(paramReadablePartial, paramInt, paramArrayOfInt, paramString, paramLocale);
/*     */   }
/*     */   
/*     */   public DurationField getDurationField() {
/* 206 */     return this.iField.getDurationField();
/*     */   }
/*     */   
/*     */   public DurationField getRangeDurationField() {
/* 210 */     if (this.iRangeDurationField != null) {
/* 211 */       return this.iRangeDurationField;
/*     */     }
/* 213 */     return this.iField.getRangeDurationField();
/*     */   }
/*     */   
/*     */   public boolean isLeap(long paramLong) {
/* 217 */     return this.iField.isLeap(paramLong);
/*     */   }
/*     */   
/*     */   public int getLeapAmount(long paramLong) {
/* 221 */     return this.iField.getLeapAmount(paramLong);
/*     */   }
/*     */   
/*     */   public DurationField getLeapDurationField() {
/* 225 */     return this.iField.getLeapDurationField();
/*     */   }
/*     */   
/*     */   public int getMinimumValue() {
/* 229 */     return this.iField.getMinimumValue();
/*     */   }
/*     */   
/*     */   public int getMinimumValue(long paramLong) {
/* 233 */     return this.iField.getMinimumValue(paramLong);
/*     */   }
/*     */   
/*     */   public int getMinimumValue(ReadablePartial paramReadablePartial) {
/* 237 */     return this.iField.getMinimumValue(paramReadablePartial);
/*     */   }
/*     */   
/*     */   public int getMinimumValue(ReadablePartial paramReadablePartial, int[] paramArrayOfInt) {
/* 241 */     return this.iField.getMinimumValue(paramReadablePartial, paramArrayOfInt);
/*     */   }
/*     */   
/*     */   public int getMaximumValue() {
/* 245 */     return this.iField.getMaximumValue();
/*     */   }
/*     */   
/*     */   public int getMaximumValue(long paramLong) {
/* 249 */     return this.iField.getMaximumValue(paramLong);
/*     */   }
/*     */   
/*     */   public int getMaximumValue(ReadablePartial paramReadablePartial) {
/* 253 */     return this.iField.getMaximumValue(paramReadablePartial);
/*     */   }
/*     */   
/*     */   public int getMaximumValue(ReadablePartial paramReadablePartial, int[] paramArrayOfInt) {
/* 257 */     return this.iField.getMaximumValue(paramReadablePartial, paramArrayOfInt);
/*     */   }
/*     */   
/*     */   public int getMaximumTextLength(Locale paramLocale) {
/* 261 */     return this.iField.getMaximumTextLength(paramLocale);
/*     */   }
/*     */   
/*     */   public int getMaximumShortTextLength(Locale paramLocale) {
/* 265 */     return this.iField.getMaximumShortTextLength(paramLocale);
/*     */   }
/*     */   
/*     */   public long roundFloor(long paramLong) {
/* 269 */     return this.iField.roundFloor(paramLong);
/*     */   }
/*     */   
/*     */   public long roundCeiling(long paramLong) {
/* 273 */     return this.iField.roundCeiling(paramLong);
/*     */   }
/*     */   
/*     */   public long roundHalfFloor(long paramLong) {
/* 277 */     return this.iField.roundHalfFloor(paramLong);
/*     */   }
/*     */   
/*     */   public long roundHalfCeiling(long paramLong) {
/* 281 */     return this.iField.roundHalfCeiling(paramLong);
/*     */   }
/*     */   
/*     */   public long roundHalfEven(long paramLong) {
/* 285 */     return this.iField.roundHalfEven(paramLong);
/*     */   }
/*     */   
/*     */   public long remainder(long paramLong) {
/* 289 */     return this.iField.remainder(paramLong);
/*     */   }
/*     */   
/*     */   public String toString() {
/* 293 */     return "DateTimeField[" + getName() + ']';
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\field\DelegatedDateTimeField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */