/*    */ package com.facebook.presto.jdbc.internal.joda.time.convert;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Chronology;
/*    */ import java.util.Date;
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
/*    */ final class DateConverter
/*    */   extends AbstractConverter
/*    */   implements InstantConverter, PartialConverter
/*    */ {
/* 35 */   static final DateConverter INSTANCE = new DateConverter();
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
/* 55 */     Date localDate = (Date)paramObject;
/* 56 */     return localDate.getTime();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Class<?> getSupportedType()
/*    */   {
/* 66 */     return Date.class;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\joda\time\convert\DateConverter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */