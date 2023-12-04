/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import java.io.IOException;
/*    */ import java.time.DateTimeException;
/*    */ import java.time.MonthDay;
/*    */ import java.time.format.DateTimeFormatter;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MonthDayDeserializer
/*    */   extends JSR310DateTimeDeserializerBase<MonthDay>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 20 */   public static final MonthDayDeserializer INSTANCE = new MonthDayDeserializer(null);
/*    */   
/*    */   public MonthDayDeserializer(DateTimeFormatter formatter) {
/* 23 */     super(MonthDay.class, formatter);
/*    */   }
/*    */   
/*    */   protected JsonDeserializer<MonthDay> withDateFormat(DateTimeFormatter dtf)
/*    */   {
/* 28 */     return new MonthDayDeserializer(dtf);
/*    */   }
/*    */   
/*    */   public MonthDay deserialize(JsonParser parser, DeserializationContext context)
/*    */     throws IOException
/*    */   {
/* 34 */     if (parser.hasToken(JsonToken.VALUE_STRING)) {
/* 35 */       String string = parser.getValueAsString().trim();
/*    */       try {
/* 37 */         if (this._formatter == null) {
/* 38 */           return MonthDay.parse(string);
/*    */         }
/* 40 */         return MonthDay.parse(string, this._formatter);
/*    */       } catch (DateTimeException e) {
/* 42 */         _rethrowDateTimeException(parser, context, e, string);
/*    */       }
/*    */     }
/* 45 */     if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
/* 46 */       return (MonthDay)parser.getEmbeddedObject();
/*    */     }
/* 48 */     throw context.mappingException("Unexpected token (%s), expected VALUE_STRING or VALUE_NUMBER_INT", new Object[] {parser
/* 49 */       .getCurrentToken() });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\deser\MonthDayDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */