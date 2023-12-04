/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import java.io.IOException;
/*    */ import java.time.DateTimeException;
/*    */ import java.time.Year;
/*    */ import java.time.format.DateTimeFormatter;
/*    */ import java.time.format.DateTimeFormatterBuilder;
/*    */ import java.time.format.SignStyle;
/*    */ import java.time.temporal.ChronoField;
/*    */ 
/*    */ 
/*    */ public class YearKeyDeserializer
/*    */   extends Jsr310KeyDeserializer
/*    */ {
/* 16 */   public static final YearKeyDeserializer INSTANCE = new YearKeyDeserializer();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/* 21 */   private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
/* 22 */     .appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
/* 23 */     .toFormatter();
/*    */   
/*    */ 
/*    */ 
/*    */   protected Year deserialize(String key, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 32 */       return Year.parse(key, FORMATTER);
/*    */     } catch (DateTimeException e) {
/* 34 */       return (Year)_rethrowDateTimeException(ctxt, Year.class, e, key);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\deser\key\YearKeyDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */