/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import java.io.IOException;
/*    */ import java.time.DateTimeException;
/*    */ import java.time.MonthDay;
/*    */ import java.time.format.DateTimeFormatter;
/*    */ import java.time.format.DateTimeFormatterBuilder;
/*    */ import java.time.temporal.ChronoField;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MonthDayKeyDeserializer
/*    */   extends Jsr310KeyDeserializer
/*    */ {
/* 16 */   public static final MonthDayKeyDeserializer INSTANCE = new MonthDayKeyDeserializer();
/*    */   
/*    */ 
/* 19 */   private static final DateTimeFormatter PARSER = new DateTimeFormatterBuilder()
/* 20 */     .appendLiteral("--")
/* 21 */     .appendValue(ChronoField.MONTH_OF_YEAR, 2)
/* 22 */     .appendLiteral('-')
/* 23 */     .appendValue(ChronoField.DAY_OF_MONTH, 2)
/* 24 */     .toFormatter();
/*    */   
/*    */ 
/*    */ 
/*    */   protected MonthDay deserialize(String key, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 33 */       return MonthDay.parse(key, PARSER);
/*    */     } catch (DateTimeException e) {
/* 35 */       return (MonthDay)_rethrowDateTimeException(ctxt, MonthDay.class, e, key);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\deser\key\MonthDayKeyDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */