/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser.key;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateTime;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*    */ import java.io.IOException;
/*    */ import java.util.TimeZone;
/*    */ 
/*    */ public class DateTimeKeyDeserializer
/*    */   extends JodaKeyDeserializer
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected DateTime deserialize(String key, DeserializationContext ctxt) throws IOException
/*    */   {
/* 17 */     if (ctxt.isEnabled(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)) {
/* 18 */       TimeZone tz = ctxt.getTimeZone();
/* 19 */       DateTimeZone dtz = tz == null ? DateTimeZone.UTC : DateTimeZone.forTimeZone(tz);
/* 20 */       return new DateTime(key, dtz);
/*    */     }
/* 22 */     return DateTime.parse(key);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\deser\key\DateTimeKeyDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */