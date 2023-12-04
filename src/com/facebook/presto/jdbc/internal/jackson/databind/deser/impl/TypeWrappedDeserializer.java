/*    */ package com.facebook.presto.jdbc.internal.jackson.databind.deser.impl;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*    */ import java.io.IOException;
/*    */ import java.io.Serializable;
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
/*    */ public final class TypeWrappedDeserializer
/*    */   extends JsonDeserializer<Object>
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected final TypeDeserializer _typeDeserializer;
/*    */   protected final JsonDeserializer<Object> _deserializer;
/*    */   
/*    */   public TypeWrappedDeserializer(TypeDeserializer typeDeser, JsonDeserializer<?> deser)
/*    */   {
/* 30 */     this._typeDeserializer = typeDeser;
/* 31 */     this._deserializer = deser;
/*    */   }
/*    */   
/*    */   public Class<?> handledType()
/*    */   {
/* 36 */     return this._deserializer.handledType();
/*    */   }
/*    */   
/*    */   public Object deserialize(JsonParser jp, DeserializationContext ctxt)
/*    */     throws IOException
/*    */   {
/* 42 */     return this._deserializer.deserializeWithType(jp, ctxt, this._typeDeserializer);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
/*    */     throws IOException
/*    */   {
/* 50 */     throw new IllegalStateException("Type-wrapped deserializer's deserializeWithType should never get called");
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public Object deserialize(JsonParser jp, DeserializationContext ctxt, Object intoValue)
/*    */     throws IOException
/*    */   {
/* 60 */     return this._deserializer.deserialize(jp, ctxt, intoValue);
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\databind\deser\impl\TypeWrappedDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */