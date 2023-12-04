/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.key;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.LocalDateTime;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.format.ISODateTimeFormat;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class LocalDateTimeKeyDeserializer extends JodaKeyDeserializer
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 12 */   private static final DateTimeFormatter parser = ;
/*    */   
/*    */   protected LocalDateTime deserialize(String key, DeserializationContext ctxt) throws IOException
/*    */   {
/* 16 */     return parser.parseLocalDateTime(key);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\deser\key\LocalDateTimeKeyDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */