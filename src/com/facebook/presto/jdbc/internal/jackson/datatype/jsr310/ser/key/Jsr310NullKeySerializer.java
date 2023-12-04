/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.jsr310.ser.key;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
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
/*    */ public class Jsr310NullKeySerializer
/*    */   extends JsonSerializer<Object>
/*    */ {
/*    */   public static final String NULL_KEY = "";
/*    */   
/*    */   public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers)
/*    */     throws IOException
/*    */   {
/* 26 */     if (value != null) {
/* 27 */       throw JsonMappingException.from(gen, "Jsr310NullKeySerializer is only for serializing null values.");
/*    */     }
/*    */     
/* 30 */     gen.writeFieldName("");
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\jsr310\ser\key\Jsr310NullKeySerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */