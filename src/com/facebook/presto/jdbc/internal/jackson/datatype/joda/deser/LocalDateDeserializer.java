/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.FormatConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.LocalDate;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ public class LocalDateDeserializer
/*    */   extends JodaDateDeserializerBase<LocalDate>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public LocalDateDeserializer()
/*    */   {
/* 21 */     this(FormatConfig.DEFAULT_LOCAL_DATEONLY_FORMAT);
/*    */   }
/*    */   
/*    */   public LocalDateDeserializer(JacksonJodaDateFormat format) {
/* 25 */     super(LocalDate.class, format);
/*    */   }
/*    */   
/*    */   public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format)
/*    */   {
/* 30 */     return new LocalDateDeserializer(format);
/*    */   }
/*    */   
/*    */   public LocalDate deserialize(JsonParser p, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/* 36 */     switch (p.getCurrentTokenId()) {
/*    */     case 6: 
/* 38 */       String str = p.getText().trim();
/* 39 */       return str.length() == 0 ? null : this._format.createParser(ctxt).parseLocalDate(str);
/*    */     
/*    */ 
/*    */     case 7: 
/* 43 */       DateTimeZone tz = this._format.isTimezoneExplicit() ? this._format.getTimeZone() : DateTimeZone.forTimeZone(ctxt.getTimeZone());
/* 44 */       return new LocalDate(p.getLongValue(), tz);
/*    */     
/*    */ 
/*    */     case 3: 
/* 48 */       int year = p.nextIntValue(-1);
/* 49 */       if (year == -1) {
/* 50 */         year = _parseIntPrimitive(p, ctxt);
/*    */       }
/* 52 */       int month = p.nextIntValue(-1);
/* 53 */       if (month == -1) {
/* 54 */         month = _parseIntPrimitive(p, ctxt);
/*    */       }
/* 56 */       int day = p.nextIntValue(-1);
/* 57 */       if (day == -1) {
/* 58 */         day = _parseIntPrimitive(p, ctxt);
/*    */       }
/* 60 */       if (p.nextToken() != JsonToken.END_ARRAY) {
/* 61 */         throw ctxt.wrongTokenException(p, JsonToken.END_ARRAY, "after LocalDate ints");
/*    */       }
/* 63 */       return new LocalDate(year, month, day);
/*    */     }
/* 65 */     throw ctxt.wrongTokenException(p, JsonToken.START_ARRAY, "expected String, Number or JSON Array");
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\deser\LocalDateDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */