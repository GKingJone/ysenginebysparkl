/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.FormatConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.LocalDateTime;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class LocalDateTimeDeserializer
/*    */   extends JodaDateDeserializerBase<LocalDateTime>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public LocalDateTimeDeserializer()
/*    */   {
/* 20 */     this(FormatConfig.DEFAULT_LOCAL_DATETIME_PARSER);
/*    */   }
/*    */   
/*    */   public LocalDateTimeDeserializer(JacksonJodaDateFormat format) {
/* 24 */     super(LocalDateTime.class, format);
/*    */   }
/*    */   
/*    */   public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format)
/*    */   {
/* 29 */     return new LocalDateTimeDeserializer(format);
/*    */   }
/*    */   
/*    */ 
/*    */   public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/* 36 */     switch (p.getCurrentTokenId())
/*    */     {
/*    */     case 6: 
/* 39 */       String str = p.getText().trim();
/* 40 */       return str.length() == 0 ? null : this._format.createParser(ctxt).parseLocalDateTime(str);
/*    */     
/*    */ 
/*    */ 
/*    */     case 7: 
/* 45 */       DateTimeZone tz = this._format.isTimezoneExplicit() ? this._format.getTimeZone() : DateTimeZone.forTimeZone(ctxt.getTimeZone());
/* 46 */       return new LocalDateTime(p.getLongValue(), tz);
/*    */     
/*    */ 
/*    */     case 3: 
/* 50 */       JsonToken t = p.nextToken();
/* 51 */       LocalDateTime dt = null;
/*    */       
/* 53 */       if (t.isNumeric()) {
/* 54 */         int year = p.getIntValue();
/* 55 */         t = p.nextToken();
/* 56 */         if (t.isNumeric()) {
/* 57 */           int month = p.getIntValue();
/* 58 */           t = p.nextToken();
/* 59 */           if (t.isNumeric()) {
/* 60 */             int day = p.getIntValue();
/* 61 */             t = p.nextToken();
/* 62 */             if (t.isNumeric()) {
/* 63 */               int hour = p.getIntValue();
/* 64 */               t = p.nextToken();
/* 65 */               if (t.isNumeric()) {
/* 66 */                 int minute = p.getIntValue();
/* 67 */                 t = p.nextToken();
/* 68 */                 if (t.isNumeric()) {
/* 69 */                   int second = p.getIntValue();
/* 70 */                   t = p.nextToken();
/*    */                   
/* 72 */                   int millisecond = 0;
/* 73 */                   if (t.isNumeric()) {
/* 74 */                     millisecond = p.getIntValue();
/* 75 */                     t = p.nextToken();
/*    */                   }
/* 77 */                   dt = new LocalDateTime(year, month, day, hour, minute, second, millisecond);
/*    */                 } } } } } }
/* 79 */       if (t == JsonToken.END_ARRAY) {
/* 80 */         return dt;
/*    */       }
/* 82 */       throw ctxt.wrongTokenException(p, JsonToken.END_ARRAY, "after LocalDateTime ints");
/*    */     }
/*    */     
/* 85 */     throw ctxt.wrongTokenException(p, JsonToken.START_ARRAY, "expected String, Number or JSON Array");
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\deser\LocalDateTimeDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */