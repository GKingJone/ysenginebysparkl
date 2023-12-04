/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser.key;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import java.io.IOException;
/*    */ import java.time.ZonedDateTime;
/*    */ import java.time.format.DateTimeFormatter;
/*    */ 
/*    */ public class ZonedDateTimeKeySerializer
/*    */   extends JsonSerializer<ZonedDateTime>
/*    */ {
/* 14 */   public static final ZonedDateTimeKeySerializer INSTANCE = new ZonedDateTimeKeySerializer();
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
/*    */   public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider serializers)
/*    */     throws IOException, JsonProcessingException
/*    */   {
/* 28 */     gen.writeFieldName(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value));
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\ser\key\ZonedDateTimeKeySerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */