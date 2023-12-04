/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import java.io.IOException;
/*    */ import java.time.DateTimeException;
/*    */ import java.time.Year;
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
/*    */ public class YearDeserializer
/*    */   extends JSR310DeserializerBase<Year>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 38 */   public static final YearDeserializer INSTANCE = new YearDeserializer();
/*    */   
/*    */   private final DateTimeFormatter _formatter;
/*    */   
/*    */   private YearDeserializer()
/*    */   {
/* 44 */     this(null);
/*    */   }
/*    */   
/*    */   public YearDeserializer(DateTimeFormatter formatter) {
/* 48 */     super(Year.class);
/* 49 */     this._formatter = formatter;
/*    */   }
/*    */   
/*    */   public Year deserialize(JsonParser parser, DeserializationContext context)
/*    */     throws IOException
/*    */   {
/* 55 */     JsonToken t = parser.getCurrentToken();
/* 56 */     if (t == JsonToken.VALUE_STRING) {
/* 57 */       String string = parser.getValueAsString().trim();
/*    */       try {
/* 59 */         if (this._formatter == null) {
/* 60 */           return Year.parse(string);
/*    */         }
/* 62 */         return Year.parse(string, this._formatter);
/*    */       } catch (DateTimeException e) {
/* 64 */         _rethrowDateTimeException(parser, context, e, string);
/*    */       }
/*    */     }
/* 67 */     if (t == JsonToken.VALUE_NUMBER_INT) {
/* 68 */       return Year.of(parser.getIntValue());
/*    */     }
/* 70 */     if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
/* 71 */       return (Year)parser.getEmbeddedObject();
/*    */     }
/* 73 */     throw context.mappingException("Unexpected token (%s), expected VALUE_STRING or VALUE_NUMBER_INT", new Object[] {parser
/* 74 */       .getCurrentToken() });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\deser\YearDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */