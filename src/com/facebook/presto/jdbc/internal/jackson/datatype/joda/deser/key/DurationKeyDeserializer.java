/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.key;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Duration;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class DurationKeyDeserializer extends JodaKeyDeserializer
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected Duration deserialize(String key, DeserializationContext ctxt) throws IOException
/*    */   {
/* 13 */     return Duration.parse(key);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\deser\key\DurationKeyDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */