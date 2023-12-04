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
/*    */ @JacksonStdImpl
/*    */ public class NullSerializer
/*    */   extends StdSerializer<Object>
/*    */ {
/* 24 */   public static final NullSerializer instance = new NullSerializer();
/*    */   
/* 26 */   private NullSerializer() { super(Object.class); }
/*    */   
/*    */   public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException
/*    */   {
/* 30 */     gen.writeNull();
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
/*    */   public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer)
/*    */     throws IOException
/*    */   {
/* 44 */     gen.writeNull();
/*    */   }
/*    */   
/*    */   public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException
/*    */   {
/* 49 */     return createSchemaNode("null");
/*    */   }
/*    */   
/*    */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException
/*    */   {
/* 54 */     visitor.expectNullFormat(typeHint);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\NullSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */