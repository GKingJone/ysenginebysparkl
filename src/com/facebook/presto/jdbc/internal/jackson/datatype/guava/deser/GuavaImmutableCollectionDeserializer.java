/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableCollection;
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.ImmutableCollection.Builder;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.CollectionType;
/*    */ import java.io.IOException;
/*    */ 
/*    */ abstract class GuavaImmutableCollectionDeserializer<T extends ImmutableCollection<Object>>
/*    */   extends GuavaCollectionDeserializer<T>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   GuavaImmutableCollectionDeserializer(CollectionType type, TypeDeserializer typeDeser, JsonDeserializer<?> deser)
/*    */   {
/* 21 */     super(type, typeDeser, deser);
/*    */   }
/*    */   
/*    */   protected abstract Builder<Object> createBuilder();
/*    */   
/*    */   protected T _deserializeContents(JsonParser jp, DeserializationContext ctxt)
/*    */     throws IOException, JsonProcessingException
/*    */   {
/* 29 */     JsonDeserializer<?> valueDes = this._valueDeserializer;
/*    */     
/* 31 */     TypeDeserializer typeDeser = this._typeDeserializerForValue;
/*    */     
/*    */ 
/* 34 */     Builder<Object> builder = createBuilder();
/*    */     JsonToken t;
/* 36 */     while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
/*    */       Object value;
/*    */       Object value;
/* 39 */       if (t == JsonToken.VALUE_NULL) {
/* 40 */         value = null; } else { Object value;
/* 41 */         if (typeDeser == null) {
/* 42 */           value = valueDes.deserialize(jp, ctxt);
/*    */         } else
/* 44 */           value = valueDes.deserializeWithType(jp, ctxt, typeDeser);
/*    */       }
/* 46 */       builder.add(value);
/*    */     }
/*    */     
/*    */ 
/*    */ 
/* 51 */     T collection = builder.build();
/* 52 */     return collection;
/*    */   }
/*    */   
/*    */ 
/*    */   protected T _deserializeFromSingleValue(JsonParser jp, DeserializationContext ctxt)
/*    */     throws IOException, JsonProcessingException
/*    */   {
/* 59 */     JsonDeserializer<?> valueDes = this._valueDeserializer;
/* 60 */     TypeDeserializer typeDeser = this._typeDeserializerForValue;
/* 61 */     JsonToken t = jp.getCurrentToken();
/*    */     
/*    */     Object value;
/*    */     Object value;
/* 65 */     if (t == JsonToken.VALUE_NULL) {
/* 66 */       value = null; } else { Object value;
/* 67 */       if (typeDeser == null) {
/* 68 */         value = valueDes.deserialize(jp, ctxt);
/*    */       } else {
/* 70 */         value = valueDes.deserializeWithType(jp, ctxt, typeDeser);
/*    */       }
/*    */     }
/* 73 */     T result = createBuilder().add(value).build();
/* 74 */     return result;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\deser\GuavaImmutableCollectionDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */