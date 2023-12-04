/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser.key;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
/*    */ import java.io.IOException;
/*    */ import java.time.DateTimeException;
/*    */ import java.time.format.DateTimeParseException;
/*    */ 
/*    */ 
/*    */ 
/*    */ abstract class Jsr310KeyDeserializer
/*    */   extends KeyDeserializer
/*    */ {
/*    */   public final Object deserializeKey(String key, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/* 18 */     if ("".equals(key))
/*    */     {
/* 20 */       return null;
/*    */     }
/* 22 */     return deserialize(key, ctxt);
/*    */   }
/*    */   
/*    */   protected abstract Object deserialize(String paramString, DeserializationContext paramDeserializationContext)
/*    */     throws IOException;
/*    */   
/*    */   protected <T> T _rethrowDateTimeException(DeserializationContext ctxt, Class<?> type, DateTimeException e0, String value)
/*    */     throws IOException
/*    */   {
/*    */     JsonMappingException e;
/* 32 */     if ((e0 instanceof DateTimeParseException)) {
/* 33 */       JsonMappingException e = ctxt.weirdStringException(value, type, e0.getMessage());
/* 34 */       e.initCause(e0);
/*    */     } else {
/* 36 */       e = JsonMappingException.from(ctxt, 
/* 37 */         String.format("Failed to deserialize %s: (%s) %s", new Object[] {type
/* 38 */         .getName(), e0.getClass().getName(), e0.getMessage() }), e0);
/*    */     }
/* 40 */     throw e;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\deser\key\Jsr310KeyDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */