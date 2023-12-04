/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.key;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
/*    */ import java.io.IOException;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ abstract class JodaKeyDeserializer extends KeyDeserializer implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public final Object deserializeKey(String key, DeserializationContext ctxt) throws IOException
/*    */   {
/* 14 */     if (key.length() == 0) {
/* 15 */       return null;
/*    */     }
/* 17 */     return deserialize(key, ctxt);
/*    */   }
/*    */   
/*    */   protected abstract Object deserialize(String paramString, DeserializationContext paramDeserializationContext)
/*    */     throws IOException;
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\deser\key\JodaKeyDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */