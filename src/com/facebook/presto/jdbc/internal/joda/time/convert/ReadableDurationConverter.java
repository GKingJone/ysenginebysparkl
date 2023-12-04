/*    */ package com.facebook.presto.jdbc.internal.joda.time.convert;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeUtils;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.ReadWritablePeriod;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.ReadableDuration;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class ReadableDurationConverter
/*    */   extends AbstractConverter
/*    */   implements DurationConverter, PeriodConverter
/*    */ {
/* 36 */   static final ReadableDurationConverter INSTANCE = new ReadableDurationConverter();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public long getDurationMillis(Object paramObject)
/*    */   {
/* 56 */     return ((ReadableDuration)paramObject).getMillis();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void setInto(ReadWritablePeriod paramReadWritablePeriod, Object paramObject, Chronology paramChronology)
/*    */   {
/* 72 */     ReadableDuration localReadableDuration = (ReadableDuration)paramObject;
/* 73 */     paramChronology = DateTimeUtils.getChronology(paramChronology);
/* 74 */     long l = localReadableDuration.getMillis();
/* 75 */     int[] arrayOfInt = paramChronology.get(paramReadWritablePeriod, l);
/* 76 */     for (int i = 0; i < arrayOfInt.length; i++) {
/* 77 */       paramReadWritablePeriod.setValue(i, arrayOfInt[i]);
/*    */     }
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Class<?> getSupportedType()
/*    */   {
/* 88 */     return ReadableDuration.class;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\convert\ReadableDurationConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */