/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.node;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser.NumberType;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.ObjectCodec;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonNode;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializable;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.SerializerProvider;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeSerializer;
/*    */ import java.io.IOException;
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
/*    */ public abstract class BaseJsonNode
/*    */   extends JsonNode
/*    */   implements JsonSerializable
/*    */ {
/*    */   public final JsonNode findPath(String fieldName)
/*    */   {
/* 33 */     JsonNode value = findValue(fieldName);
/* 34 */     if (value == null) {
/* 35 */       return MissingNode.getInstance();
/*    */     }
/* 37 */     return value;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public abstract int hashCode();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public JsonParser traverse()
/*    */   {
/* 51 */     return new TreeTraversingParser(this);
/*    */   }
/*    */   
/*    */   public JsonParser traverse(ObjectCodec codec)
/*    */   {
/* 56 */     return new TreeTraversingParser(this, codec);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public abstract JsonToken asToken();
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public NumberType numberType()
/*    */   {
/* 76 */     return null;
/*    */   }
/*    */   
/*    */   public abstract void serialize(JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider)
/*    */     throws IOException, JsonProcessingException;
/*    */   
/*    */   public abstract void serializeWithType(JsonGenerator paramJsonGenerator, SerializerProvider paramSerializerProvider, TypeSerializer paramTypeSerializer)
/*    */     throws IOException, JsonProcessingException;
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\node\BaseJsonNode.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */