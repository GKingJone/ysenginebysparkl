/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.format.ISODateTimeFormat;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.format.ISOPeriodFormat;
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
/*    */ public class FormatConfig
/*    */ {
/* 19 */   private static final DateTimeZone DEFAULT_TZ = ;
/*    */   
/*    */ 
/*    */ 
/* 23 */   public static final JacksonJodaDateFormat DEFAULT_DATEONLY_FORMAT = createUTC(ISODateTimeFormat.date());
/*    */   
/*    */ 
/* 26 */   public static final JacksonJodaDateFormat DEFAULT_TIMEONLY_FORMAT = createUTC(ISODateTimeFormat.time());
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 33 */   public static final JacksonJodaDateFormat DEFAULT_DATETIME_PARSER = createUTC(ISODateTimeFormat.dateTimeParser());
/*    */   
/*    */ 
/* 36 */   public static final JacksonJodaDateFormat DEFAULT_DATETIME_PRINTER = createUTC(ISODateTimeFormat.dateTime());
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   @Deprecated
/* 43 */   public static final JacksonJodaDateFormat DEFAULT_DATETIME_FORMAT = DEFAULT_DATETIME_PRINTER;
/*    */   
/*    */ 
/*    */ 
/* 47 */   public static final JacksonJodaDateFormat DEFAULT_LOCAL_DATEONLY_FORMAT = createDefaultTZ(ISODateTimeFormat.date());
/*    */   
/*    */ 
/* 50 */   public static final JacksonJodaDateFormat DEFAULT_LOCAL_TIMEONLY_PRINTER = createDefaultTZ(ISODateTimeFormat.time());
/*    */   
/*    */ 
/* 53 */   public static final JacksonJodaDateFormat DEFAULT_LOCAL_TIMEONLY_PARSER = createDefaultTZ(ISODateTimeFormat.localTimeParser());
/*    */   
/*    */ 
/* 56 */   public static final JacksonJodaDateFormat DEFAULT_LOCAL_DATETIME_PRINTER = createDefaultTZ(ISODateTimeFormat.dateTime());
/*    */   
/*    */ 
/* 59 */   public static final JacksonJodaDateFormat DEFAULT_LOCAL_DATETIME_PARSER = createDefaultTZ(ISODateTimeFormat.localDateOptionalTimeParser());
/*    */   
/*    */ 
/* 62 */   public static final JacksonJodaPeriodFormat DEFAULT_PERIOD_FORMAT = new JacksonJodaPeriodFormat(ISOPeriodFormat.standard());
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private static final JacksonJodaDateFormat createUTC(DateTimeFormatter f)
/*    */   {
/* 71 */     f = f.withZoneUTC();
/* 72 */     return new JacksonJodaDateFormat(f);
/*    */   }
/*    */   
/*    */   private static final JacksonJodaDateFormat createDefaultTZ(DateTimeFormatter f)
/*    */   {
/* 77 */     f = f.withZone(DEFAULT_TZ);
/* 78 */     return new JacksonJodaDateFormat(f);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\cfg\FormatConfig.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */