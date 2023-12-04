/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import java.io.IOException;
/*    */ import java.time.DateTimeException;
/*    */ import java.time.LocalDate;
/*    */ import java.time.format.DateTimeFormatter;
/*    */ 
/*    */ public class LocalDateKeyDeserializer
/*    */   extends Jsr310KeyDeserializer
/*    */ {
/* 12 */   public static final LocalDateKeyDeserializer INSTANCE = new LocalDateKeyDeserializer();
/*    */   
/*    */ 
/*    */ 
/*    */   protected LocalDate deserialize(String key, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 21 */       return LocalDate.parse(key, DateTimeFormatter.ISO_LOCAL_DATE);
/*    */     } catch (DateTimeException e) {
/* 23 */       return (LocalDate)_rethrowDateTimeException(ctxt, LocalDate.class, e, key);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\deser\key\LocalDateKeyDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */