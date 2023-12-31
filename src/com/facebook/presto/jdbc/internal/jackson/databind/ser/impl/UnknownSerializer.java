/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.impl;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationFeature;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.ser.std.StdSerializer;
/*    */ import java.lang.reflect.Type;
/*    */ 
/*    */ public class UnknownSerializer extends StdSerializer<Object>
/*    */ {
/*    */   public UnknownSerializer()
/*    */   {
/* 17 */     super(Object.class);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public UnknownSerializer(Class<?> cls)
/*    */   {
/* 24 */     super(cls, false);
/*    */   }
/*    */   
/*    */ 
/*    */   public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
/*    */     throws java.io.IOException
/*    */   {
/* 31 */     if (provider.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS)) {
/* 32 */       failForEmpty(provider, value);
/*    */     }
/*    */     
/* 35 */     gen.writeStartObject();
/* 36 */     gen.writeEndObject();
/*    */   }
/*    */   
/*    */ 
/*    */   public final void serializeWithType(Object value, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer)
/*    */     throws java.io.IOException
/*    */   {
/* 43 */     if (provider.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS)) {
/* 44 */       failForEmpty(provider, value);
/*    */     }
/* 46 */     typeSer.writeTypePrefixForObject(value, gen);
/* 47 */     typeSer.writeTypeSuffixForObject(value, gen);
/*    */   }
/*    */   
/*    */   public boolean isEmpty(SerializerProvider provider, Object value)
/*    */   {
/* 52 */     return true;
/*    */   }
/*    */   
/*    */   public com.facebook.presto.jdbc.internal.jackson.databind.JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException
/*    */   {
/* 57 */     return null;
/*    */   }
/*    */   
/*    */ 
/*    */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*    */     throws JsonMappingException
/*    */   {
/* 64 */     visitor.expectAnyFormat(typeHint);
/*    */   }
/*    */   
/*    */   protected void failForEmpty(SerializerProvider prov, Object value) throws JsonMappingException
/*    */   {
/* 69 */     prov.reportMappingProblem("No serializer found for class %s and no properties discovered to create BeanSerializer (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS)", new Object[] { value.getClass().getName() });
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\impl\UnknownSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */