/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.ser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.std.StdSerializer;
/*    */ 
/*    */ abstract class JodaSerializerBase<T> extends StdSerializer<T>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected JodaSerializerBase(Class<T> cls)
/*    */   {
/* 15 */     super(cls);
/*    */   }
/*    */   
/*    */   public void serializeWithType(T value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws java.io.IOException, JsonProcessingException {
/* 19 */     typeSer.writeTypePrefixForScalar(value, jgen);
/* 20 */     serialize(value, jgen, provider);
/* 21 */     typeSer.writeTypeSuffixForScalar(value, jgen);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\ser\JodaSerializerBase.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */