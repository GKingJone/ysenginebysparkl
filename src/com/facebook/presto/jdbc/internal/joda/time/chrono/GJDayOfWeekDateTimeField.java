/*     */ package com.facebook.presto.jdbc.internal.joda.time.chrono;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.field.PreciseDurationDateTimeField;
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
/*     */ final class GJDayOfWeekDateTimeField
/*     */   extends PreciseDurationDateTimeField
/*     */ {
/*     */   private static final long serialVersionUID = -3857947176719041436L;
/*     */   private final BasicChronology iChronology;
/*     */   
/*     */   GJDayOfWeekDateTimeField(BasicChronology paramBasicChronology, DurationField paramDurationField)
/*     */   {
/*  46 */     super(DateTimeFieldType.dayOfWeek(), paramDurationField);
/*  47 */     this.iChronology = paramBasicChronology;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int get(long paramLong)
/*     */   {
/*  57 */     return this.iChronology.getDayOfWeek(paramLong);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAsText(int paramInt, Locale paramLocale)
/*     */   {
/*  68 */     return GJLocaleSymbols.forLocale(paramLocale).dayOfWeekValueToText(paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAsShortText(int paramInt, Locale paramLocale)
/*     */   {
/*  79 */     return GJLocaleSymbols.forLocale(paramLocale).dayOfWeekValueToShortText(paramInt);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int convertText(String paramString, Locale paramLocale)
/*     */   {
/*  91 */     return GJLocaleSymbols.forLocale(paramLocale).dayOfWeekTextToValue(paramString);
/*     */   }
/*     */   
/*     */   public DurationField getRangeDurationField() {
/*  95 */     return this.iChronology.weeks();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMinimumValue()
/*     */   {
/* 104 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaximumValue()
/*     */   {
/* 113 */     return 7;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaximumTextLength(Locale paramLocale)
/*     */   {
/* 123 */     return GJLocaleSymbols.forLocale(paramLocale).getDayOfWeekMaxTextLength();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaximumShortTextLength(Locale paramLocale)
/*     */   {
/* 133 */     return GJLocaleSymbols.forLocale(paramLocale).getDayOfWeekMaxShortTextLength();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private Object readResolve()
/*     */   {
/* 140 */     return this.iChronology.dayOfWeek();
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\chrono\GJDayOfWeekDateTimeField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */