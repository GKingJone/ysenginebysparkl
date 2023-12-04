/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JacksonStdImpl;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ @JacksonStdImpl
/*    */ public class ToStringSerializer
/*    */   extends StdSerializer<Object>
/*    */ {
/* 28 */   public static final ToStringSerializer instance = new ToStringSerializer();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ToStringSerializer()
/*    */   {
/* 38 */     super(Object.class);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public ToStringSerializer(Class<?> handledType)
/*    */   {
/* 46 */     super(handledType, false);
/*    */   }
/*    */   
/*    */   public boolean isEmpty(SerializerProvider prov, Object value)
/*    */   {
/* 51 */     if (value == null) {
/* 52 */       return true;
/*    */     }
/* 54 */     String str = value.toString();
/* 55 */     return str.isEmpty();
/*    */   }
/*    */   
/*    */ 
/*    */   public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 62 */     gen.writeString(value.toString());
/*    */   }
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
/*    */   public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer)
/*    */     throws IOException
/*    */   {
/* 81 */     typeSer.writeTypePrefixForScalar(value, gen);
/* 82 */     serialize(value, gen, provider);
/* 83 */     typeSer.writeTypeSuffixForScalar(value, gen);
/*    */   }
/*    */   
/*    */   public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException
/*    */   {
/* 88 */     return createSchemaNode("string", true);
/*    */   }
/*    */   
/*    */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*    */     throws JsonMappingException
/*    */   {
/* 94 */     visitStringFormat(visitor, typeHint);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\ToStringSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */