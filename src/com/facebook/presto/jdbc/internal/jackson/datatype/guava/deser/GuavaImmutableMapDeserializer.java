/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableMap;
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableMap.Builder;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.KeyDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.MapType;
/*    */ import java.io.IOException;
/*    */ 
/*    */ abstract class GuavaImmutableMapDeserializer<T extends ImmutableMap<Object, Object>>
/*    */   extends GuavaMapDeserializer<T>
/*    */ {
/*    */   GuavaImmutableMapDeserializer(MapType type, KeyDeserializer keyDeser, TypeDeserializer typeDeser, JsonDeserializer<?> deser)
/*    */   {
/* 20 */     super(type, keyDeser, typeDeser, deser);
/*    */   }
/*    */   
/*    */ 
/*    */   protected abstract Builder<Object, Object> createBuilder();
/*    */   
/*    */   protected T _deserializeEntries(JsonParser p, DeserializationContext ctxt)
/*    */     throws IOException, JsonProcessingException
/*    */   {
/* 29 */     KeyDeserializer keyDes = this._keyDeserializer;
/* 30 */     JsonDeserializer<?> valueDes = this._valueDeserializer;
/* 31 */     TypeDeserializer typeDeser = this._typeDeserializerForValue;
/*    */     
/* 33 */     Builder<Object, Object> builder = createBuilder();
/* 34 */     for (; p.getCurrentToken() == JsonToken.FIELD_NAME; p.nextToken())
/*    */     {
/* 36 */       String fieldName = p.getCurrentName();
/* 37 */       Object key = keyDes == null ? fieldName : keyDes.deserializeKey(fieldName, ctxt);
/*    */       
/* 39 */       JsonToken t = p.nextToken();
/*    */       
/*    */ 
/* 42 */       if (t == JsonToken.VALUE_NULL) {
/* 43 */         _handleNull(ctxt, key, this._valueDeserializer, builder);
/*    */       } else { Object value;
/*    */         Object value;
/* 46 */         if (typeDeser == null) {
/* 47 */           value = valueDes.deserialize(p, ctxt);
/*    */         } else {
/* 49 */           value = valueDes.deserializeWithType(p, ctxt, typeDeser);
/*    */         }
/* 51 */         builder.put(key, value);
/*    */       }
/*    */     }
/*    */     
/*    */ 
/* 56 */     T map = builder.build();
/* 57 */     return map;
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
/*    */   protected void _handleNull(DeserializationContext ctxt, Object key, JsonDeserializer<?> valueDeser, Builder<Object, Object> builder)
/*    */     throws IOException
/*    */   {
/* 74 */     Object nvl = valueDeser.getNullValue(ctxt);
/* 75 */     if (nvl != null) {
/* 76 */       builder.put(key, nvl);
/*    */     }
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\deser\GuavaImmutableMapDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */