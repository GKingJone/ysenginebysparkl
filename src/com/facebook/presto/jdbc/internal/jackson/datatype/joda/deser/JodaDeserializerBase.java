/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.joda.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.deser.std.StdScalarDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*    */ import java.io.IOException;
/*    */ 
/*    */ abstract class JodaDeserializerBase<T> extends StdScalarDeserializer<T>
/*    */ {
/*    */   protected JodaDeserializerBase(Class<?> cls)
/*    */   {
/* 14 */     super(cls);
/*    */   }
/*    */   
/*    */   protected JodaDeserializerBase(JodaDeserializerBase<?> src) {
/* 18 */     super(src);
/*    */   }
/*    */   
/*    */   public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException
/*    */   {
/* 23 */     return typeDeserializer.deserializeTypedFromAny(p, ctxt);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\joda\deser\JodaDeserializerBase.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */