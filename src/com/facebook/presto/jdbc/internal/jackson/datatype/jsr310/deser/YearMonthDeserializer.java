/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import java.io.IOException;
/*    */ import java.time.DateTimeException;
/*    */ import java.time.YearMonth;
/*    */ import java.time.format.DateTimeFormatter;
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
/*    */ 
/*    */ 
/*    */ public class YearMonthDeserializer
/*    */   extends JSR310DateTimeDeserializerBase<YearMonth>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 39 */   public static final YearMonthDeserializer INSTANCE = new YearMonthDeserializer();
/*    */   
/*    */   private YearMonthDeserializer()
/*    */   {
/* 43 */     this(DateTimeFormatter.ofPattern("uuuu-MM"));
/*    */   }
/*    */   
/*    */   public YearMonthDeserializer(DateTimeFormatter formatter)
/*    */   {
/* 48 */     super(YearMonth.class, formatter);
/*    */   }
/*    */   
/*    */ 
/*    */   protected JsonDeserializer<YearMonth> withDateFormat(DateTimeFormatter dtf)
/*    */   {
/* 54 */     return new YearMonthDeserializer(dtf);
/*    */   }
/*    */   
/*    */   public YearMonth deserialize(JsonParser parser, DeserializationContext context)
/*    */     throws IOException
/*    */   {
/* 60 */     if (parser.hasToken(JsonToken.VALUE_STRING)) {
/* 61 */       String string = parser.getText().trim();
/* 62 */       if (string.length() == 0) {
/* 63 */         return null;
/*    */       }
/*    */       try {
/* 66 */         return YearMonth.parse(string, this._formatter);
/*    */       } catch (DateTimeException e) {
/* 68 */         _rethrowDateTimeException(parser, context, e, string);
/*    */       }
/*    */     }
/* 71 */     if (parser.isExpectedStartArrayToken()) {
/* 72 */       int year = parser.nextIntValue(-1);
/* 73 */       if (year == -1) {
/* 74 */         if (parser.hasToken(JsonToken.END_ARRAY)) {
/* 75 */           return null;
/*    */         }
/* 77 */         if (!parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
/* 78 */           _reportWrongToken(parser, context, JsonToken.VALUE_NUMBER_INT, "years");
/*    */         }
/* 80 */         year = parser.getIntValue();
/*    */       }
/* 82 */       int month = parser.nextIntValue(-1);
/* 83 */       if (month == -1) {
/* 84 */         if (!parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
/* 85 */           _reportWrongToken(parser, context, JsonToken.VALUE_NUMBER_INT, "months");
/*    */         }
/* 87 */         month = parser.getIntValue();
/*    */       }
/* 89 */       if (parser.nextToken() != JsonToken.END_ARRAY) {
/* 90 */         throw context.wrongTokenException(parser, JsonToken.END_ARRAY, "Expected array to end.");
/*    */       }
/*    */       
/* 93 */       return YearMonth.of(year, month);
/*    */     }
/* 95 */     if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
/* 96 */       return (YearMonth)parser.getEmbeddedObject();
/*    */     }
/* 98 */     return (YearMonth)_reportWrongToken(parser, context, new JsonToken[] { JsonToken.VALUE_STRING, JsonToken.START_ARRAY });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\deser\YearMonthDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */