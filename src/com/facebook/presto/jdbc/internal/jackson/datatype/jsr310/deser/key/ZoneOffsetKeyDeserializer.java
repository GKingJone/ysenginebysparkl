/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import java.io.IOException;
/*    */ import java.time.DateTimeException;
/*    */ import java.time.ZoneOffset;
/*    */ 
/*    */ public class ZoneOffsetKeyDeserializer
/*    */   extends Jsr310KeyDeserializer
/*    */ {
/* 11 */   public static final ZoneOffsetKeyDeserializer INSTANCE = new ZoneOffsetKeyDeserializer();
/*    */   
/*    */ 
/*    */ 
/*    */   protected ZoneOffset deserialize(String key, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 20 */       return ZoneOffset.of(key);
/*    */     } catch (DateTimeException e) {
/* 22 */       return (ZoneOffset)_rethrowDateTimeException(ctxt, ZoneOffset.class, e, key);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\deser\key\ZoneOffsetKeyDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */