/*    */ package com.facebook.presto.jdbc.internal.joda.time.convert;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeUtils;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Period;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.ReadWritableInterval;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.ReadWritablePeriod;
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
/*    */ 
/*    */ 
/*    */ class NullConverter
/*    */   extends AbstractConverter
/*    */   implements InstantConverter, PartialConverter, DurationConverter, PeriodConverter, IntervalConverter
/*    */ {
/* 39 */   static final NullConverter INSTANCE = new NullConverter();
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
/* 56 */     return 0L;
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
/*    */   public void setInto(ReadWritablePeriod paramReadWritablePeriod, Object paramObject, Chronology paramChronology)
/*    */   {
/* 69 */     paramReadWritablePeriod.setPeriod((Period)null);
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
/*    */   public void setInto(ReadWritableInterval paramReadWritableInterval, Object paramObject, Chronology paramChronology)
/*    */   {
/* 83 */     paramReadWritableInterval.setChronology(paramChronology);
/* 84 */     long l = DateTimeUtils.currentTimeMillis();
/* 85 */     paramReadWritableInterval.setInterval(l, l);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Class<?> getSupportedType()
/*    */   {
/* 95 */     return null;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\convert\NullConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */