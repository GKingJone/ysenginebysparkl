/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import java.io.IOException;
/*    */ import java.time.DateTimeException;
/*    */ import java.time.OffsetDateTime;
/*    */ import java.time.format.DateTimeFormatter;
/*    */ 
/*    */ public class OffsetDateTimeKeyDeserializer
/*    */   extends Jsr310KeyDeserializer
/*    */ {
/* 12 */   public static final OffsetDateTimeKeyDeserializer INSTANCE = new OffsetDateTimeKeyDeserializer();
/*    */   
/*    */ 
/*    */ 
/*    */   protected OffsetDateTime deserialize(String key, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 21 */       return OffsetDateTime.parse(key, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
/*    */     } catch (DateTimeException e) {
/* 23 */       return (OffsetDateTime)_rethrowDateTimeException(ctxt, OffsetDateTime.class, e, key);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\deser\key\OffsetDateTimeKeyDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */