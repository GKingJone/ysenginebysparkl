/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.FormatConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateTime;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.ReadableDateTime;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.ReadableInstant;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DateTimeDeserializer
/*    */   extends JodaDateDeserializerBase<ReadableInstant>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public DateTimeDeserializer(Class<?> cls, JacksonJodaDateFormat format)
/*    */   {
/* 28 */     super(cls, format);
/*    */   }
/*    */   
/*    */ 
/*    */   public static <T extends ReadableInstant> JsonDeserializer<T> forType(Class<T> cls)
/*    */   {
/* 34 */     return new DateTimeDeserializer(cls, FormatConfig.DEFAULT_DATETIME_PARSER);
/*    */   }
/*    */   
/*    */ 
/*    */   public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format)
/*    */   {
/* 40 */     return new DateTimeDeserializer(this._valueClass, format);
/*    */   }
/*    */   
/*    */ 
/*    */   public ReadableDateTime deserialize(JsonParser p, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/* 47 */     JsonToken t = p.getCurrentToken();
/*    */     
/* 49 */     if (t == JsonToken.VALUE_NUMBER_INT) {
/* 50 */       DateTimeZone tz = this._format.isTimezoneExplicit() ? this._format.getTimeZone() : DateTimeZone.forTimeZone(ctxt.getTimeZone());
/* 51 */       return new DateTime(p.getLongValue(), tz);
/*    */     }
/* 53 */     if (t == JsonToken.VALUE_STRING) {
/* 54 */       String str = p.getText().trim();
/* 55 */       if (str.length() == 0) {
/* 56 */         return null;
/*    */       }
/*    */       
/*    */ 
/* 60 */       int ix = str.indexOf('[');
/* 61 */       if (ix > 0) {
/* 62 */         int ix2 = str.lastIndexOf(']');
/* 63 */         String tzId = ix2 < ix ? str.substring(ix + 1) : str.substring(ix + 1, ix2);
/*    */         
/*    */         DateTimeZone tz;
/*    */         try
/*    */         {
/* 68 */           tz = DateTimeZone.forID(tzId);
/*    */         } catch (IllegalArgumentException e) {
/* 70 */           throw ctxt.mappingException(String.format("Unknown DateTimeZone id '%s'", new Object[] { tzId }));
/*    */         }
/* 72 */         str = str.substring(0, ix);
/*    */         
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 84 */         return this._format.createParser(ctxt).parseDateTime(str).withZone(tz);
/*    */       }
/*    */       
/*    */ 
/*    */ 
/*    */ 
/* 90 */       return this._format.createParser(ctxt).parseDateTime(str);
/*    */     }
/* 92 */     return (ReadableDateTime)ctxt.handleUnexpectedToken(handledType(), p);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\deser\DateTimeDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */