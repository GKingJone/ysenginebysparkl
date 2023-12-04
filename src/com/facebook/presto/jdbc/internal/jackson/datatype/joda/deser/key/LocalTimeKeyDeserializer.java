/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.key;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.LocalTime;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.format.ISODateTimeFormat;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class LocalTimeKeyDeserializer
/*    */   extends JodaKeyDeserializer
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 13 */   private static final DateTimeFormatter parser = ;
/*    */   
/*    */   protected LocalTime deserialize(String key, DeserializationContext ctxt) throws IOException
/*    */   {
/* 17 */     return parser.parseLocalTime(key);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\deser\key\LocalTimeKeyDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */