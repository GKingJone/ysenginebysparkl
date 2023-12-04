/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.FormatConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateMidnight;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.LocalDate;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*    */ import java.io.IOException;
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
/*    */ @Deprecated
/*    */ public class DateMidnightDeserializer
/*    */   extends JodaDateDeserializerBase<DateMidnight>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public DateMidnightDeserializer()
/*    */   {
/* 31 */     this(FormatConfig.DEFAULT_DATEONLY_FORMAT);
/*    */   }
/*    */   
/*    */   public DateMidnightDeserializer(JacksonJodaDateFormat format) {
/* 35 */     super(DateMidnight.class, format);
/*    */   }
/*    */   
/*    */   public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format)
/*    */   {
/* 40 */     return new DateMidnightDeserializer(format);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public DateMidnight deserialize(JsonParser p, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/* 49 */     if (p.isExpectedStartArrayToken()) {
/* 50 */       p.nextToken();
/* 51 */       int year = p.getIntValue();
/* 52 */       p.nextToken();
/* 53 */       int month = p.getIntValue();
/* 54 */       p.nextToken();
/* 55 */       int day = p.getIntValue();
/* 56 */       if (p.nextToken() != JsonToken.END_ARRAY) {
/* 57 */         throw ctxt.wrongTokenException(p, JsonToken.END_ARRAY, "after DateMidnight ints");
/*    */       }
/*    */       
/* 60 */       DateTimeZone tz = this._format.isTimezoneExplicit() ? this._format.getTimeZone() : DateTimeZone.forTimeZone(ctxt.getTimeZone());
/*    */       
/* 62 */       return new DateMidnight(year, month, day, tz);
/*    */     }
/* 64 */     switch (p.getCurrentToken()) {
/*    */     case VALUE_NUMBER_INT: 
/* 66 */       return new DateMidnight(p.getLongValue());
/*    */     case VALUE_STRING: 
/* 68 */       String str = p.getText().trim();
/* 69 */       if (str.length() == 0) {
/* 70 */         return null;
/*    */       }
/* 72 */       LocalDate local = this._format.createParser(ctxt).parseLocalDate(str);
/* 73 */       if (local == null) {
/* 74 */         return null;
/*    */       }
/* 76 */       return local.toDateMidnight();
/*    */     }
/*    */     
/* 79 */     throw ctxt.wrongTokenException(p, JsonToken.START_ARRAY, "expected JSON Array, Number or String");
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\deser\DateMidnightDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */