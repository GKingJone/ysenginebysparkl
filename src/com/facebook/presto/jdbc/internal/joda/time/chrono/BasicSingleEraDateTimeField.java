/*     */ package com.facebook.presto.jdbc.internal.joda.time.chrono;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.IllegalFieldValueException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ final class BasicSingleEraDateTimeField
/*     */   extends BaseDateTimeField
/*     */ {
/*     */   private static final int ERA_VALUE = 1;
/*     */   private final String iEraText;
/*     */   
/*     */   BasicSingleEraDateTimeField(String paramString)
/*     */   {
/*  51 */     super(DateTimeFieldType.era());
/*  52 */     this.iEraText = paramString;
/*     */   }
/*     */   
/*     */   public boolean isLenient()
/*     */   {
/*  57 */     return false;
/*     */   }
/*     */   
/*     */   public int get(long paramLong)
/*     */   {
/*  62 */     return 1;
/*     */   }
/*     */   
/*     */   public long set(long paramLong, int paramInt)
/*     */   {
/*  67 */     FieldUtils.verifyValueBounds(this, paramInt, 1, 1);
/*  68 */     return paramLong;
/*     */   }
/*     */   
/*     */   public long set(long paramLong, String paramString, Locale paramLocale)
/*     */   {
/*  73 */     if ((!this.iEraText.equals(paramString)) && (!"1".equals(paramString))) {
/*  74 */       throw new IllegalFieldValueException(DateTimeFieldType.era(), paramString);
/*     */     }
/*  76 */     return paramLong;
/*     */   }
/*     */   
/*     */   public long roundFloor(long paramLong)
/*     */   {
/*  81 */     return Long.MIN_VALUE;
/*     */   }
/*     */   
/*     */   public long roundCeiling(long paramLong)
/*     */   {
/*  86 */     return Long.MAX_VALUE;
/*     */   }
/*     */   
/*     */   public long roundHalfFloor(long paramLong)
/*     */   {
/*  91 */     return Long.MIN_VALUE;
/*     */   }
/*     */   
/*     */   public long roundHalfCeiling(long paramLong)
/*     */   {
/*  96 */     return Long.MIN_VALUE;
/*     */   }
/*     */   
/*     */   public long roundHalfEven(long paramLong)
/*     */   {
/* 101 */     return Long.MIN_VALUE;
/*     */   }
/*     */   
/*     */   public DurationField getDurationField()
/*     */   {
/* 106 */     return UnsupportedDurationField.getInstance(DurationFieldType.eras());
/*     */   }
/*     */   
/*     */   public DurationField getRangeDurationField()
/*     */   {
/* 111 */     return null;
/*     */   }
/*     */   
/*     */   public int getMinimumValue()
/*     */   {
/* 116 */     return 1;
/*     */   }
/*     */   
/*     */   public int getMaximumValue()
/*     */   {
/* 121 */     return 1;
/*     */   }
/*     */   
/*     */   public String getAsText(int paramInt, Locale paramLocale)
/*     */   {
/* 126 */     return this.iEraText;
/*     */   }
/*     */   
/*     */   public int getMaximumTextLength(Locale paramLocale)
/*     */   {
/* 131 */     return this.iEraText.length();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\BasicSingleEraDateTimeField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */