/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.DateTimeDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateTime;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Duration;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.LocalDate;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.LocalDateTime;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.LocalTime;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.MonthDay;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Period;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.ReadableDateTime;
/*    */ 
/*    */ public class JodaModule extends com.facebook.presto.jdbc.internal.jackson.databind.module.SimpleModule
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public JodaModule()
/*    */   {
/* 20 */     super(PackageVersion.VERSION);
/*    */     
/* 22 */     addDeserializer(DateTime.class, DateTimeDeserializer.forType(DateTime.class));
/* 23 */     addDeserializer(DateTimeZone.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.DateTimeZoneDeserializer());
/*    */     
/* 25 */     addDeserializer(Duration.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.DurationDeserializer());
/* 26 */     addDeserializer(com.facebook.presto.jdbc.internal.joda.time.Instant.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.InstantDeserializer());
/* 27 */     addDeserializer(LocalDateTime.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.LocalDateTimeDeserializer());
/* 28 */     addDeserializer(LocalDate.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.LocalDateDeserializer());
/* 29 */     addDeserializer(LocalTime.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.LocalTimeDeserializer());
/* 30 */     com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer<?> deser = new com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.PeriodDeserializer(true);
/* 31 */     addDeserializer(Period.class, deser);
/* 32 */     addDeserializer(com.facebook.presto.jdbc.internal.joda.time.ReadablePeriod.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.PeriodDeserializer(false));
/* 33 */     addDeserializer(ReadableDateTime.class, DateTimeDeserializer.forType(ReadableDateTime.class));
/* 34 */     addDeserializer(com.facebook.presto.jdbc.internal.joda.time.ReadableInstant.class, DateTimeDeserializer.forType(com.facebook.presto.jdbc.internal.joda.time.ReadableInstant.class));
/* 35 */     addDeserializer(com.facebook.presto.jdbc.internal.joda.time.Interval.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.IntervalDeserializer());
/* 36 */     addDeserializer(MonthDay.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.MonthDayDeserializer());
/* 37 */     addDeserializer(com.facebook.presto.jdbc.internal.joda.time.YearMonth.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.YearMonthDeserializer());
/*    */     
/*    */ 
/* 40 */     com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer<Object> stringSer = com.facebook.presto.jdbc.internal.jackson.databind.ser.std.ToStringSerializer.instance;
/* 41 */     addSerializer(DateTime.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.ser.DateTimeSerializer());
/* 42 */     addSerializer(DateTimeZone.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.ser.DateTimeZoneSerializer());
/* 43 */     addSerializer(Duration.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.ser.DurationSerializer());
/* 44 */     addSerializer(com.facebook.presto.jdbc.internal.joda.time.Instant.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.ser.InstantSerializer());
/* 45 */     addSerializer(LocalDateTime.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.ser.LocalDateTimeSerializer());
/* 46 */     addSerializer(LocalDate.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.ser.LocalDateSerializer());
/* 47 */     addSerializer(LocalTime.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.ser.LocalTimeSerializer());
/* 48 */     addSerializer(Period.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.ser.PeriodSerializer());
/* 49 */     addSerializer(com.facebook.presto.jdbc.internal.joda.time.Interval.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.ser.IntervalSerializer());
/* 50 */     addSerializer(MonthDay.class, stringSer);
/* 51 */     addSerializer(com.facebook.presto.jdbc.internal.joda.time.YearMonth.class, stringSer);
/*    */     
/*    */ 
/* 54 */     addKeyDeserializer(DateTime.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.key.DateTimeKeyDeserializer());
/* 55 */     addKeyDeserializer(LocalTime.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.key.LocalTimeKeyDeserializer());
/* 56 */     addKeyDeserializer(LocalDate.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.key.LocalDateKeyDeserializer());
/* 57 */     addKeyDeserializer(LocalDateTime.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.key.LocalDateTimeKeyDeserializer());
/* 58 */     addKeyDeserializer(Duration.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.key.DurationKeyDeserializer());
/* 59 */     addKeyDeserializer(Period.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.key.PeriodKeyDeserializer());
/*    */     
/*    */ 
/*    */ 
/*    */ 
/* 64 */     addDeserializer(com.facebook.presto.jdbc.internal.joda.time.DateMidnight.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.DateMidnightDeserializer());
/* 65 */     addSerializer(com.facebook.presto.jdbc.internal.joda.time.DateMidnight.class, new com.facebook.presto.jdbc.internal.jackson.datatype.joda.ser.DateMidnightSerializer());
/*    */   }
/*    */   
/*    */ 
/*    */   public String getModuleName()
/*    */   {
/* 71 */     return getClass().getSimpleName();
/*    */   }
/*    */   
/*    */   public int hashCode()
/*    */   {
/* 76 */     return getClass().hashCode();
/*    */   }
/*    */   
/*    */   public boolean equals(Object o)
/*    */   {
/* 81 */     return this == o;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\JodaModule.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */