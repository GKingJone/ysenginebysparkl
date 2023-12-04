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
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.util.TokenBuffer;
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
/*    */ public class TokenBufferSerializer
/*    */   extends StdSerializer<TokenBuffer>
/*    */ {
/*    */   public TokenBufferSerializer()
/*    */   {
/* 27 */     super(TokenBuffer.class);
/*    */   }
/*    */   
/*    */   public void serialize(TokenBuffer value, JsonGenerator jgen, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 33 */     value.serialize(jgen);
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
/*    */   public final void serializeWithType(TokenBuffer value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
/*    */     throws IOException
/*    */   {
/* 50 */     typeSer.writeTypePrefixForScalar(value, jgen);
/* 51 */     serialize(value, jgen, provider);
/* 52 */     typeSer.writeTypeSuffixForScalar(value, jgen);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public JsonNode getSchema(SerializerProvider provider, Type typeHint)
/*    */   {
/* 60 */     return createSchemaNode("any", true);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*    */     throws JsonMappingException
/*    */   {
/* 69 */     visitor.expectAnyFormat(typeHint);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\TokenBufferSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */