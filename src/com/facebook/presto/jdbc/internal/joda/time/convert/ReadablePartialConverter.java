/*     */ package com.facebook.presto.jdbc.internal.joda.time.convert;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeUtils;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePartial;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ReadablePartialConverter
/*     */   extends AbstractConverter
/*     */   implements PartialConverter
/*     */ {
/*  35 */   static final ReadablePartialConverter INSTANCE = new ReadablePartialConverter();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Chronology getChronology(Object paramObject, DateTimeZone paramDateTimeZone)
/*     */   {
/*  53 */     return getChronology(paramObject, (Chronology)null).withZone(paramDateTimeZone);
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
/*     */   public Chronology getChronology(Object paramObject, Chronology paramChronology)
/*     */   {
/*  67 */     if (paramChronology == null) {
/*  68 */       paramChronology = ((ReadablePartial)paramObject).getChronology();
/*  69 */       paramChronology = DateTimeUtils.getChronology(paramChronology);
/*     */     }
/*  71 */     return paramChronology;
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
/*     */   public int[] getPartialValues(ReadablePartial paramReadablePartial, Object paramObject, Chronology paramChronology)
/*     */   {
/*  87 */     ReadablePartial localReadablePartial = (ReadablePartial)paramObject;
/*  88 */     int i = paramReadablePartial.size();
/*  89 */     int[] arrayOfInt = new int[i];
/*  90 */     for (int j = 0; j < i; j++) {
/*  91 */       arrayOfInt[j] = localReadablePartial.get(paramReadablePartial.getFieldType(j));
/*     */     }
/*  93 */     paramChronology.validate(paramReadablePartial, arrayOfInt);
/*  94 */     return arrayOfInt;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Class<?> getSupportedType()
/*     */   {
/* 104 */     return ReadablePartial.class;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\convert\ReadablePartialConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */