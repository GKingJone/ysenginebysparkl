/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import java.io.IOException;
/*    */ import java.time.DateTimeException;
/*    */ import java.time.LocalDateTime;
/*    */ import java.time.format.DateTimeFormatter;
/*    */ 
/*    */ public class LocalDateTimeKeyDeserializer
/*    */   extends Jsr310KeyDeserializer
/*    */ {
/* 12 */   public static final LocalDateTimeKeyDeserializer INSTANCE = new LocalDateTimeKeyDeserializer();
/*    */   
/*    */ 
/*    */ 
/*    */   protected LocalDateTime deserialize(String key, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 21 */       return LocalDateTime.parse(key, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
/*    */     } catch (DateTimeException e) {
/* 23 */       return (LocalDateTime)_rethrowDateTimeException(ctxt, LocalDateTime.class, e, key);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\deser\key\LocalDateTimeKeyDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */