/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*    */ import java.io.IOException;
/*    */ import java.lang.reflect.Type;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RawSerializer<T>
/*    */   extends StdSerializer<T>
/*    */ {
/*    */   public RawSerializer(Class<?> cls)
/*    */   {
/* 25 */     super(cls, false);
/*    */   }
/*    */   
/*    */   public void serialize(T value, JsonGenerator jgen, SerializerProvider provider) throws IOException
/*    */   {
/* 30 */     jgen.writeRawValue(value.toString());
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void serializeWithType(T value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
/*    */     throws IOException
/*    */   {
/* 38 */     typeSer.writeTypePrefixForScalar(value, jgen);
/* 39 */     serialize(value, jgen, provider);
/* 40 */     typeSer.writeTypeSuffixForScalar(value, jgen);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public JsonNode getSchema(SerializerProvider provider, Type typeHint)
/*    */   {
/* 47 */     return createSchemaNode("string", true);
/*    */   }
/*    */   
/*    */ 
/*    */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*    */     throws JsonMappingException
/*    */   {
/* 54 */     visitStringFormat(visitor, typeHint);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\RawSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */