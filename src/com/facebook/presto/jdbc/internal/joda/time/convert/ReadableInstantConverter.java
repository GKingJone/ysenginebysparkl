/*     */ package com.facebook.presto.jdbc.internal.joda.time.convert;
/*     */ 
/*     */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeUtils;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.ReadableInstant;
/*     */ import com.facebook.presto.jdbc.internal.joda.time.chrono.ISOChronology;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ReadableInstantConverter
/*     */   extends AbstractConverter
/*     */   implements InstantConverter, PartialConverter
/*     */ {
/*  36 */   static final ReadableInstantConverter INSTANCE = new ReadableInstantConverter();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*  58 */     Chronology localChronology = ((ReadableInstant)paramObject).getChronology();
/*  59 */     if (localChronology == null) {
/*  60 */       return ISOChronology.getInstance(paramDateTimeZone);
/*     */     }
/*  62 */     DateTimeZone localDateTimeZone = localChronology.getZone();
/*  63 */     if (localDateTimeZone != paramDateTimeZone) {
/*  64 */       localChronology = localChronology.withZone(paramDateTimeZone);
/*  65 */       if (localChronology == null) {
/*  66 */         return ISOChronology.getInstance(paramDateTimeZone);
/*     */       }
/*     */     }
/*  69 */     return localChronology;
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
/*  83 */     if (paramChronology == null) {
/*  84 */       paramChronology = ((ReadableInstant)paramObject).getChronology();
/*  85 */       paramChronology = DateTimeUtils.getChronology(paramChronology);
/*     */     }
/*  87 */     return paramChronology;
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
/*     */   public long getInstantMillis(Object paramObject, Chronology paramChronology)
/*     */   {
/* 100 */     return ((ReadableInstant)paramObject).getMillis();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Class<?> getSupportedType()
/*     */   {
/* 110 */     return ReadableInstant.class;
/*     */   }
/*     */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\convert\ReadableInstantConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */