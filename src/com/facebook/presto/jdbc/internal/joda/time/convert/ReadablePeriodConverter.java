/*    */ package com.facebook.presto.jdbc.internal.joda.time.convert;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.PeriodType;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.ReadWritablePeriod;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.ReadablePeriod;
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
/*    */ class ReadablePeriodConverter
/*    */   extends AbstractConverter
/*    */   implements PeriodConverter
/*    */ {
/* 36 */   static final ReadablePeriodConverter INSTANCE = new ReadablePeriodConverter();
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
/*    */   public void setInto(ReadWritablePeriod paramReadWritablePeriod, Object paramObject, Chronology paramChronology)
/*    */   {
/* 58 */     paramReadWritablePeriod.setPeriod((ReadablePeriod)paramObject);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public PeriodType getPeriodType(Object paramObject)
/*    */   {
/* 70 */     ReadablePeriod localReadablePeriod = (ReadablePeriod)paramObject;
/* 71 */     return localReadablePeriod.getPeriodType();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Class<?> getSupportedType()
/*    */   {
/* 81 */     return ReadablePeriod.class;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\convert\ReadablePeriodConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */