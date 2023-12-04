/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.ser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*    */ import com.facebook.presto.jdbc.internal.joda.time.DateTimeZone;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class DateTimeZoneSerializer extends JodaSerializerBase<DateTimeZone>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public DateTimeZoneSerializer()
/*    */   {
/* 16 */     super(DateTimeZone.class);
/*    */   }
/*    */   
/*    */   public void serialize(DateTimeZone value, JsonGenerator gen, SerializerProvider provider) throws IOException
/*    */   {
/* 21 */     gen.writeString(value.getID());
/*    */   }
/*    */   
/*    */   public void serializeWithType(DateTimeZone value, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer)
/*    */     throws IOException, JsonProcessingException
/*    */   {
/* 27 */     typeSer.writeTypePrefixForScalar(value, gen, DateTimeZone.class);
/* 28 */     serialize(value, gen, provider);
/* 29 */     typeSer.writeTypeSuffixForScalar(value, gen);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\ser\DateTimeZoneSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */