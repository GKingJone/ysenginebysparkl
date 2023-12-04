/*     */ package com.facebook.presto.jdbc.internal.joda.time.chrono;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.BaseDateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.FieldUtils;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.UnsupportedDurationField;
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
/*     */ final class GJEraDateTimeField
/*     */   extends BaseDateTimeField
/*     */ {
/*     */   private static final long serialVersionUID = 4240986525305515528L;
/*     */   private final BasicChronology iChronology;
/*     */   
/*     */   GJEraDateTimeField(BasicChronology paramBasicChronology)
/*     */   {
/*  47 */     super(DateTimeFieldType.era());
/*  48 */     this.iChronology = paramBasicChronology;
/*     */   }
/*     */   
/*     */   public boolean isLenient() {
/*  52 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int get(long paramLong)
/*     */   {
/*  61 */     if (this.iChronology.getYear(paramLong) <= 0) {
/*  62 */       return 0;
/*     */     }
/*  64 */     return 1;
/*     */   }
/*     */   
/*     */   public String getAsText(int paramInt, Locale paramLocale)
/*     */   {
/*  69 */     return GJLocaleSymbols.forLocale(paramLocale).eraValueToText(paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long set(long paramLong, int paramInt)
/*     */   {
/*  81 */     FieldUtils.verifyValueBounds(this, paramInt, 0, 1);
/*     */     
/*  83 */     int i = get(paramLong);
/*  84 */     if (i != paramInt) {
/*  85 */       int j = this.iChronology.getYear(paramLong);
/*  86 */       return this.iChronology.setYear(paramLong, -j);
/*     */     }
/*  88 */     return paramLong;
/*     */   }
/*     */   
/*     */   public long set(long paramLong, String paramString, Locale paramLocale)
/*     */   {
/*  93 */     return set(paramLong, GJLocaleSymbols.forLocale(paramLocale).eraTextToValue(paramString));
/*     */   }
/*     */   
/*     */   public long roundFloor(long paramLong) {
/*  97 */     if (get(paramLong) == 1) {
/*  98 */       return this.iChronology.setYear(0L, 1);
/*     */     }
/* 100 */     return Long.MIN_VALUE;
/*     */   }
/*     */   
/*     */   public long roundCeiling(long paramLong)
/*     */   {
/* 105 */     if (get(paramLong) == 0) {
/* 106 */       return this.iChronology.setYear(0L, 1);
/*     */     }
/* 108 */     return Long.MAX_VALUE;
/*     */   }
/*     */   
/*     */ 
/*     */   public long roundHalfFloor(long paramLong)
/*     */   {
/* 114 */     return roundFloor(paramLong);
/*     */   }
/*     */   
/*     */   public long roundHalfCeiling(long paramLong)
/*     */   {
/* 119 */     return roundFloor(paramLong);
/*     */   }
/*     */   
/*     */   public long roundHalfEven(long paramLong)
/*     */   {
/* 124 */     return roundFloor(paramLong);
/*     */   }
/*     */   
/*     */   public DurationField getDurationField() {
/* 128 */     return UnsupportedDurationField.getInstance(DurationFieldType.eras());
/*     */   }
/*     */   
/*     */   public DurationField getRangeDurationField() {
/* 132 */     return null;
/*     */   }
/*     */   
/*     */   public int getMinimumValue() {
/* 136 */     return 0;
/*     */   }
/*     */   
/*     */   public int getMaximumValue() {
/* 140 */     return 1;
/*     */   }
/*     */   
/*     */   public int getMaximumTextLength(Locale paramLocale) {
/* 144 */     return GJLocaleSymbols.forLocale(paramLocale).getEraMaxTextLength();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Object readResolve()
/*     */   {
/* 151 */     return this.iChronology.era();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\GJEraDateTimeField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */