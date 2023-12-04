/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.ser.impl;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonGenerator;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonSerializer;
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
/*    */ public final class TypeWrappedSerializer
/*    */   extends JsonSerializer<Object>
/*    */ {
/*    */   protected final TypeSerializer _typeSerializer;
/*    */   protected final JsonSerializer<Object> _serializer;
/*    */   
/*    */   public TypeWrappedSerializer(TypeSerializer typeSer, JsonSerializer<?> ser)
/*    */   {
/* 26 */     this._typeSerializer = typeSer;
/* 27 */     this._serializer = ser;
/*    */   }
/*    */   
/*    */   public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException
/*    */   {
/* 32 */     this._serializer.serializeWithType(value, jgen, provider, this._typeSerializer);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void serializeWithType(Object value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer)
/*    */     throws IOException
/*    */   {
/* 42 */     this._serializer.serializeWithType(value, jgen, provider, typeSer);
/*    */   }
/*    */   
/*    */   public Class<Object> handledType() {
/* 46 */     return Object.class;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public JsonSerializer<Object> valueSerializer()
/*    */   {
/* 55 */     return this._serializer;
/*    */   }
/*    */   
/*    */   public TypeSerializer typeSerializer() {
/* 59 */     return this._typeSerializer;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\ser\impl\TypeWrappedSerializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */