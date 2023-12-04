/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import java.io.IOException;
/*    */ import java.time.DateTimeException;
/*    */ import java.time.ZonedDateTime;
/*    */ import java.time.format.DateTimeFormatter;
/*    */ 
/*    */ public class ZonedDateTimeKeyDeserializer
/*    */   extends Jsr310KeyDeserializer
/*    */ {
/* 12 */   public static final ZonedDateTimeKeyDeserializer INSTANCE = new ZonedDateTimeKeyDeserializer();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   protected ZonedDateTime deserialize(String key, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 22 */       return ZonedDateTime.parse(key, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
/*    */     } catch (DateTimeException e) {
/* 24 */       return (ZonedDateTime)_rethrowDateTimeException(ctxt, ZonedDateTime.class, e, key);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\deser\key\ZonedDateTimeKeyDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */