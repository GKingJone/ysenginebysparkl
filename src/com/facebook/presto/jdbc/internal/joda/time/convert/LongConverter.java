/*    */ package com.facebook.presto.jdbc.internal.joda.time.convert;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
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
/*    */ class LongConverter
/*    */   extends AbstractConverter
/*    */   implements InstantConverter, PartialConverter, DurationConverter
/*    */ {
/* 34 */   static final LongConverter INSTANCE = new LongConverter();
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
/*    */   public long getInstantMillis(Object paramObject, Chronology paramChronology)
/*    */   {
/* 54 */     return ((Long)paramObject).longValue();
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
/*    */   public long getDurationMillis(Object paramObject)
/*    */   {
/* 67 */     return ((Long)paramObject).longValue();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Class<?> getSupportedType()
/*    */   {
/* 77 */     return Long.class;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\convert\LongConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */