/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import java.io.IOException;
/*    */ import java.time.DateTimeException;
/*    */ import java.time.Duration;
/*    */ 
/*    */ public class DurationKeyDeserializer
/*    */   extends Jsr310KeyDeserializer
/*    */ {
/* 11 */   public static final DurationKeyDeserializer INSTANCE = new DurationKeyDeserializer();
/*    */   
/*    */ 
/*    */ 
/*    */   protected Duration deserialize(String key, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 20 */       return Duration.parse(key);
/*    */     } catch (DateTimeException e) {
/* 22 */       return (Duration)_rethrowDateTimeException(ctxt, Duration.class, e, key);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\deser\key\DurationKeyDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */