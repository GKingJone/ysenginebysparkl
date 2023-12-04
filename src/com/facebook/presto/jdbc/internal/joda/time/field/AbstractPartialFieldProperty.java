/*     */ package com.facebook.presto.jdbc.internal.joda.time.field;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeFieldType;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DurationField;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadableInstant;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePartial;
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
/*     */ public abstract class AbstractPartialFieldProperty
/*     */ {
/*     */   public abstract DateTimeField getField();
/*     */   
/*     */   public DateTimeFieldType getFieldType()
/*     */   {
/*  60 */     return getField().getType();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/*  69 */     return getField().getName();
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
/*     */   protected abstract ReadablePartial getReadablePartial();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public abstract int get();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getAsString()
/*     */   {
/* 106 */     return Integer.toString(get());
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
/*     */   public String getAsText()
/*     */   {
/* 121 */     return getAsText(null);
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
/*     */   public String getAsText(Locale paramLocale)
/*     */   {
/* 137 */     return getField().getAsText(getReadablePartial(), get(), paramLocale);
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
/*     */   public String getAsShortText()
/*     */   {
/* 152 */     return getAsShortText(null);
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
/*     */   public String getAsShortText(Locale paramLocale)
/*     */   {
/* 168 */     return getField().getAsShortText(getReadablePartial(), get(), paramLocale);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationField getDurationField()
/*     */   {
/* 179 */     return getField().getDurationField();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public DurationField getRangeDurationField()
/*     */   {
/* 189 */     return getField().getRangeDurationField();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMinimumValueOverall()
/*     */   {
/* 200 */     return getField().getMinimumValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMinimumValue()
/*     */   {
/* 210 */     return getField().getMinimumValue(getReadablePartial());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaximumValueOverall()
/*     */   {
/* 220 */     return getField().getMaximumValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaximumValue()
/*     */   {
/* 230 */     return getField().getMaximumValue(getReadablePartial());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaximumTextLength(Locale paramLocale)
/*     */   {
/* 242 */     return getField().getMaximumTextLength(paramLocale);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getMaximumShortTextLength(Locale paramLocale)
/*     */   {
/* 253 */     return getField().getMaximumShortTextLength(paramLocale);
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
/*     */   public int compareTo(ReadableInstant paramReadableInstant)
/*     */   {
/* 271 */     if (paramReadableInstant == null) {
/* 272 */       throw new IllegalArgumentException("The instant must not be null");
/*     */     }
/* 274 */     int i = get();
/* 275 */     int j = paramReadableInstant.get(getFieldType());
/* 276 */     if (i < j)
/* 277 */       return -1;
/* 278 */     if (i > j) {
/* 279 */       return 1;
/*     */     }
/* 281 */     return 0;
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
/*     */   public int compareTo(ReadablePartial paramReadablePartial)
/*     */   {
/* 300 */     if (paramReadablePartial == null) {
/* 301 */       throw new IllegalArgumentException("The instant must not be null");
/*     */     }
/* 303 */     int i = get();
/* 304 */     int j = paramReadablePartial.get(getFieldType());
/* 305 */     if (i < j)
/* 306 */       return -1;
/* 307 */     if (i > j) {
/* 308 */       return 1;
/*     */     }
/* 310 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/* 322 */     if (this == paramObject) {
/* 323 */       return true;
/*     */     }
/* 325 */     if (!(paramObject instanceof AbstractPartialFieldProperty)) {
/* 326 */       return false;
/*     */     }
/* 328 */     AbstractPartialFieldProperty localAbstractPartialFieldProperty = (AbstractPartialFieldProperty)paramObject;
/* 329 */     return (get() == localAbstractPartialFieldProperty.get()) && (getFieldType() == localAbstractPartialFieldProperty.getFieldType()) && (FieldUtils.equals(getReadablePartial().getChronology(), localAbstractPartialFieldProperty.getReadablePartial().getChronology()));
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
/*     */   public int hashCode()
/*     */   {
/* 343 */     int i = 19;
/* 344 */     i = 13 * i + get();
/* 345 */     i = 13 * i + getFieldType().hashCode();
/* 346 */     i = 13 * i + getReadablePartial().getChronology().hashCode();
/* 347 */     return i;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 357 */     return "Property[" + getName() + "]";
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\field\AbstractPartialFieldProperty.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */