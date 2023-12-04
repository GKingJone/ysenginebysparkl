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
/*    */ public abstract class StdScalarSerializer<T>
/*    */   extends StdSerializer<T>
/*    */ {
/*    */   protected StdScalarSerializer(Class<T> t)
/*    */   {
/* 19 */     super(t);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected StdScalarSerializer(Class<?> t, boolean dummy)
/*    */   {
/* 28 */     super(t);
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
/*    */   public void serializeWithType(T value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer)
/*    */     throws IOException
/*    */   {
/* 42 */     typeSer.writeTypePrefixForScalar(value, g);
/* 43 */     serialize(value, g, provider);
/* 44 */     typeSer.writeTypeSuffixForScalar(value, g);
/*    */   }
/*    */   
/*    */ 
/*    */   public JsonNode getSchema(SerializerProvider provider, Type typeHint)
/*    */     throws JsonMappingException
/*    */   {
/* 51 */     return createSchemaNode("string", true);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*    */     throws JsonMappingException
/*    */   {
/* 59 */     visitStringFormat(visitor, typeHint);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\StdScalarSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */