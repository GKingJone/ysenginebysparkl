/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import java.io.IOException;
/*    */ import java.time.DateTimeException;
/*    */ import java.time.ZoneId;
/*    */ 
/*    */ public class ZoneIdKeyDeserializer
/*    */   extends Jsr310KeyDeserializer
/*    */ {
/* 11 */   public static final ZoneIdKeyDeserializer INSTANCE = new ZoneIdKeyDeserializer();
/*    */   
/*    */ 
/*    */ 
/*    */   protected Object deserialize(String key, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 20 */       return ZoneId.of(key);
/*    */     } catch (DateTimeException e) {
/* 22 */       return _rethrowDateTimeException(ctxt, ZoneId.class, e, key);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\deser\key\ZoneIdKeyDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */