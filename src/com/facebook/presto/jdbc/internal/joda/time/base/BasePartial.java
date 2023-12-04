/*     */ package com.facebook.presto.jdbc.internal.joda.time.base;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeUtils;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePartial;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.convert.ConverterManager;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.convert.PartialConverter;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormat;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class BasePartial
/*     */   extends AbstractPartial
/*     */   implements ReadablePartial, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 2353678632973660L;
/*     */   private final Chronology iChronology;
/*     */   private final int[] iValues;
/*     */   
/*     */   protected BasePartial()
/*     */   {
/*  65 */     this(DateTimeUtils.currentTimeMillis(), null);
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
/*     */   protected BasePartial(Chronology paramChronology)
/*     */   {
/*  79 */     this(DateTimeUtils.currentTimeMillis(), paramChronology);
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
/*     */   protected BasePartial(long paramLong)
/*     */   {
/*  93 */     this(paramLong, null);
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
/*     */   protected BasePartial(long paramLong, Chronology paramChronology)
/*     */   {
/* 109 */     paramChronology = DateTimeUtils.getChronology(paramChronology);
/* 110 */     this.iChronology = paramChronology.withUTC();
/* 111 */     this.iValues = paramChronology.get(this, paramLong);
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
/*     */   protected BasePartial(Object paramObject, Chronology paramChronology)
/*     */   {
/* 132 */     PartialConverter localPartialConverter = ConverterManager.getInstance().getPartialConverter(paramObject);
/* 133 */     paramChronology = localPartialConverter.getChronology(paramObject, paramChronology);
/* 134 */     paramChronology = DateTimeUtils.getChronology(paramChronology);
/* 135 */     this.iChronology = paramChronology.withUTC();
/* 136 */     this.iValues = localPartialConverter.getPartialValues(this, paramObject, paramChronology);
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
/*     */   protected BasePartial(Object paramObject, Chronology paramChronology, DateTimeFormatter paramDateTimeFormatter)
/*     */   {
/* 159 */     PartialConverter localPartialConverter = ConverterManager.getInstance().getPartialConverter(paramObject);
/* 160 */     paramChronology = localPartialConverter.getChronology(paramObject, paramChronology);
/* 161 */     paramChronology = DateTimeUtils.getChronology(paramChronology);
/* 162 */     this.iChronology = paramChronology.withUTC();
/* 163 */     this.iValues = localPartialConverter.getPartialValues(this, paramObject, paramChronology, paramDateTimeFormatter);
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
/*     */   protected BasePartial(int[] paramArrayOfInt, Chronology paramChronology)
/*     */   {
/* 181 */     paramChronology = DateTimeUtils.getChronology(paramChronology);
/* 182 */     this.iChronology = paramChronology.withUTC();
/* 183 */     paramChronology.validate(this, paramArrayOfInt);
/* 184 */     this.iValues = paramArrayOfInt;
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
/*     */   protected BasePartial(BasePartial paramBasePartial, int[] paramArrayOfInt)
/*     */   {
/* 197 */     this.iChronology = paramBasePartial.iChronology;
/* 198 */     this.iValues = paramArrayOfInt;
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
/*     */   protected BasePartial(BasePartial paramBasePartial, Chronology paramChronology)
/*     */   {
/* 212 */     this.iChronology = paramChronology.withUTC();
/* 213 */     this.iValues = paramBasePartial.iValues;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getValue(int paramInt)
/*     */   {
/* 225 */     return this.iValues[paramInt];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int[] getValues()
/*     */   {
/* 237 */     return (int[])this.iValues.clone();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Chronology getChronology()
/*     */   {
/* 249 */     return this.iChronology;
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
/*     */   protected void setValue(int paramInt1, int paramInt2)
/*     */   {
/* 265 */     DateTimeField localDateTimeField = getField(paramInt1);
/* 266 */     int[] arrayOfInt = localDateTimeField.set(this, paramInt1, this.iValues, paramInt2);
/* 267 */     System.arraycopy(arrayOfInt, 0, this.iValues, 0, this.iValues.length);
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
/* 280 */     getChronology().validate(this, paramArrayOfInt);
/* 281 */     System.arraycopy(paramArrayOfInt, 0, this.iValues, 0, this.iValues.length);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString(String paramString)
/*     */   {
/* 292 */     if (paramString == null) {
/* 293 */       return toString();
/*     */     }
/* 295 */     return DateTimeFormat.forPattern(paramString).print(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString(String paramString, Locale paramLocale)
/*     */     throws IllegalArgumentException
/*     */   {
/* 306 */     if (paramString == null) {
/* 307 */       return toString();
/*     */     }
/* 309 */     return DateTimeFormat.forPattern(paramString).withLocale(paramLocale).print(this);
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\base\BasePartial.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */