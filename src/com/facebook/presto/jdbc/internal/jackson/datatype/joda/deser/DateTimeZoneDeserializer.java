/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DateTimeZoneDeserializer
/*    */   extends JodaDeserializerBase<DateTimeZone>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public DateTimeZoneDeserializer()
/*    */   {
/* 18 */     super(DateTimeZone.class);
/*    */   }
/*    */   
/*    */   public DateTimeZone deserialize(JsonParser p, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/* 24 */     JsonToken t = p.getCurrentToken();
/* 25 */     if (t == JsonToken.VALUE_NUMBER_INT)
/*    */     {
/* 27 */       return DateTimeZone.forOffsetHours(p.getIntValue());
/*    */     }
/* 29 */     if (t == JsonToken.VALUE_STRING) {
/* 30 */       return DateTimeZone.forID(p.getText().trim());
/*    */     }
/* 32 */     return (DateTimeZone)ctxt.handleUnexpectedToken(handledType(), p);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\deser\DateTimeZoneDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */