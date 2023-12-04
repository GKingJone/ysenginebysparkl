/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import java.io.IOException;
/*    */ import java.time.DateTimeException;
/*    */ import java.time.OffsetTime;
/*    */ import java.time.format.DateTimeFormatter;
/*    */ 
/*    */ public class OffsetTimeKeyDeserializer
/*    */   extends Jsr310KeyDeserializer
/*    */ {
/* 12 */   public static final OffsetTimeKeyDeserializer INSTANCE = new OffsetTimeKeyDeserializer();
/*    */   
/*    */ 
/*    */ 
/*    */   protected OffsetTime deserialize(String key, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 21 */       return OffsetTime.parse(key, DateTimeFormatter.ISO_OFFSET_TIME);
/*    */     } catch (DateTimeException e) {
/* 23 */       return (OffsetTime)_rethrowDateTimeException(ctxt, OffsetTime.class, e, key);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\deser\key\OffsetTimeKeyDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */