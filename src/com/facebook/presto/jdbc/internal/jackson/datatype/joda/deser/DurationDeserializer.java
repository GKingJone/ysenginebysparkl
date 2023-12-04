/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StdScalarDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.Duration;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DurationDeserializer
/*    */   extends StdScalarDeserializer<Duration>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public DurationDeserializer()
/*    */   {
/* 21 */     super(Duration.class);
/*    */   }
/*    */   
/*    */   public Duration deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
/*    */     throws IOException, JsonProcessingException
/*    */   {
/* 27 */     switch (jsonParser.getCurrentToken()) {
/*    */     case VALUE_NUMBER_INT: 
/* 29 */       return new Duration(jsonParser.getLongValue());
/*    */     case VALUE_STRING: 
/* 31 */       return new Duration(jsonParser.getText());
/*    */     }
/*    */     
/* 34 */     throw deserializationContext.mappingException("expected JSON Number or String");
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\deser\DurationDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */