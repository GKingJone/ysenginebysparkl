/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.std;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JavaType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonMappingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializationConfig;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.annotation.JacksonStdImpl;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.node.ObjectNode;
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
/*    */ @JacksonStdImpl
/*    */ public class ByteArraySerializer
/*    */   extends StdSerializer<byte[]>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public ByteArraySerializer()
/*    */   {
/* 34 */     super(byte[].class);
/*    */   }
/*    */   
/*    */   public boolean isEmpty(SerializerProvider prov, byte[] value)
/*    */   {
/* 39 */     return (value == null) || (value.length == 0);
/*    */   }
/*    */   
/*    */ 
/*    */   public void serialize(byte[] value, JsonGenerator g, SerializerProvider provider)
/*    */     throws IOException
/*    */   {
/* 46 */     g.writeBinary(provider.getConfig().getBase64Variant(), value, 0, value.length);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void serializeWithType(byte[] value, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer)
/*    */     throws IOException
/*    */   {
/* 55 */     typeSer.writeTypePrefixForScalar(value, g);
/* 56 */     g.writeBinary(provider.getConfig().getBase64Variant(), value, 0, value.length);
/*    */     
/* 58 */     typeSer.writeTypeSuffixForScalar(value, g);
/*    */   }
/*    */   
/*    */ 
/*    */   public JsonNode getSchema(SerializerProvider provider, Type typeHint)
/*    */   {
/* 64 */     ObjectNode o = createSchemaNode("array", true);
/* 65 */     ObjectNode itemSchema = createSchemaNode("byte");
/* 66 */     return o.set("items", itemSchema);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
/*    */     throws JsonMappingException
/*    */   {
/* 79 */     if (visitor != null) {
/* 80 */       JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
/* 81 */       if (v2 != null) {
/* 82 */         v2.itemsFormat(JsonFormatTypes.INTEGER);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\std\ByteArraySerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */