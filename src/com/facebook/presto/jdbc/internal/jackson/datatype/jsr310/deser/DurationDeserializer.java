/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationFeature;
/*    */ import com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.DecimalUtils;
/*    */ import java.io.IOException;
/*    */ import java.math.BigDecimal;
/*    */ import java.time.DateTimeException;
/*    */ import java.time.Duration;
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
/*    */ 
/*    */ public class DurationDeserializer
/*    */   extends JSR310DeserializerBase<Duration>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 40 */   public static final DurationDeserializer INSTANCE = new DurationDeserializer();
/*    */   
/*    */   private DurationDeserializer()
/*    */   {
/* 44 */     super(Duration.class);
/*    */   }
/*    */   
/*    */   public Duration deserialize(JsonParser parser, DeserializationContext context)
/*    */     throws IOException
/*    */   {
/* 50 */     switch (parser.getCurrentTokenId())
/*    */     {
/*    */     case 8: 
/* 53 */       BigDecimal value = parser.getDecimalValue();
/* 54 */       long seconds = value.longValue();
/* 55 */       int nanoseconds = DecimalUtils.extractNanosecondDecimal(value, seconds);
/* 56 */       return Duration.ofSeconds(seconds, nanoseconds);
/*    */     
/*    */     case 7: 
/* 59 */       if (context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
/* 60 */         return Duration.ofSeconds(parser.getLongValue());
/*    */       }
/* 62 */       return Duration.ofMillis(parser.getLongValue());
/*    */     
/*    */     case 6: 
/* 65 */       String string = parser.getText().trim();
/* 66 */       if (string.length() == 0) {
/* 67 */         return null;
/*    */       }
/*    */       try {
/* 70 */         return Duration.parse(string);
/*    */       } catch (DateTimeException e) {
/* 72 */         return (Duration)_rethrowDateTimeException(parser, context, e, string);
/*    */       }
/*    */     
/*    */ 
/*    */     case 12: 
/* 77 */       return (Duration)parser.getEmbeddedObject();
/*    */     }
/* 79 */     throw context.mappingException("Expected type float, integer, or string.");
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\deser\DurationDeserializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */