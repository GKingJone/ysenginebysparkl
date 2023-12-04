/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.FormatConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.LocalTime;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.format.DateTimeFormatter;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class LocalTimeDeserializer
/*    */   extends JodaDateDeserializerBase<LocalTime>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public LocalTimeDeserializer()
/*    */   {
/* 19 */     this(FormatConfig.DEFAULT_LOCAL_TIMEONLY_PARSER);
/*    */   }
/*    */   
/*    */   public LocalTimeDeserializer(JacksonJodaDateFormat format) {
/* 23 */     super(LocalTime.class, format);
/*    */   }
/*    */   
/*    */   public JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format)
/*    */   {
/* 28 */     return new LocalTimeDeserializer(format);
/*    */   }
/*    */   
/*    */ 
/*    */   public LocalTime deserialize(JsonParser p, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/* 35 */     switch (p.getCurrentToken())
/*    */     {
/*    */     case START_ARRAY: 
/* 38 */       if (p.isExpectedStartArrayToken()) {
/* 39 */         p.nextToken();
/* 40 */         int hour = p.getIntValue();
/* 41 */         p.nextToken();
/* 42 */         int minute = p.getIntValue();
/* 43 */         p.nextToken();
/* 44 */         int second = p.getIntValue();
/* 45 */         p.nextToken();
/* 46 */         int millis = 0;
/* 47 */         if (p.getCurrentToken() != JsonToken.END_ARRAY) {
/* 48 */           millis = p.getIntValue();
/* 49 */           p.nextToken();
/*    */         }
/* 51 */         if (p.getCurrentToken() != JsonToken.END_ARRAY) {
/* 52 */           throw ctxt.wrongTokenException(p, JsonToken.END_ARRAY, "after LocalTime ints");
/*    */         }
/* 54 */         return new LocalTime(hour, minute, second, millis);
/*    */       }
/*    */       break;
/*    */     case VALUE_NUMBER_INT: 
/* 58 */       return new LocalTime(p.getLongValue());
/*    */     case VALUE_STRING: 
/* 60 */       String str = p.getText().trim();
/* 61 */       return str.length() == 0 ? null : this._format.createParser(ctxt).parseLocalTime(str);
/*    */     }
/*    */     
/*    */     
/* 65 */     throw ctxt.wrongTokenException(p, JsonToken.START_ARRAY, "expected JSON Array, String or Number");
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\deser\LocalTimeDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */