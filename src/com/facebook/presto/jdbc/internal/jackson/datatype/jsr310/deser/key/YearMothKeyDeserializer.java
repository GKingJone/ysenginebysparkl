/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import java.io.IOException;
/*    */ import java.time.DateTimeException;
/*    */ import java.time.YearMonth;
/*    */ import java.time.format.DateTimeFormatter;
/*    */ import java.time.format.DateTimeFormatterBuilder;
/*    */ import java.time.format.SignStyle;
/*    */ import java.time.temporal.ChronoField;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class YearMothKeyDeserializer
/*    */   extends Jsr310KeyDeserializer
/*    */ {
/* 17 */   public static final YearMothKeyDeserializer INSTANCE = new YearMothKeyDeserializer();
/*    */   
/*    */ 
/* 20 */   private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
/* 21 */     .appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
/* 22 */     .appendLiteral('-')
/* 23 */     .appendValue(ChronoField.MONTH_OF_YEAR, 2)
/* 24 */     .toFormatter();
/*    */   
/*    */ 
/*    */ 
/*    */   protected YearMonth deserialize(String key, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 33 */       return YearMonth.parse(key, FORMATTER);
/*    */     } catch (DateTimeException e) {
/* 35 */       return (YearMonth)_rethrowDateTimeException(ctxt, YearMonth.class, e, key);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\deser\key\YearMothKeyDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */