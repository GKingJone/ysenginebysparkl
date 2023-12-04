/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*    */ import java.io.IOException;
/*    */ import java.util.TimeZone;
/*    */ 
/*    */ public class TimeZoneSerializer extends StdScalarSerializer<TimeZone>
/*    */ {
/*    */   public TimeZoneSerializer()
/*    */   {
/* 13 */     super(TimeZone.class);
/*    */   }
/*    */   
/*    */   public void serialize(TimeZone value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
/* 17 */     jgen.writeString(value.getID());
/*    */   }
/*    */   
/*    */   public void serializeWithType(TimeZone value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
/*    */     throws IOException
/*    */   {
/* 23 */     typeSer.writeTypePrefixForScalar(value, jgen, TimeZone.class);
/* 24 */     serialize(value, jgen, provider);
/* 25 */     typeSer.writeTypeSuffixForScalar(value, jgen);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\TimeZoneSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */