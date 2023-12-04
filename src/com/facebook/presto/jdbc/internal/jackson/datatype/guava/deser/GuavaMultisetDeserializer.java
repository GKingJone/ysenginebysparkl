/*    */ package com.facebook.presto.jdbc.internal.jackson.datatype.guava.deser;
/*    */ 
/*    */ import com.facebook.presto.jdbc.internal.guava.collect.Multiset;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonParser;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonProcessingException;
/*    */ import com.facebook.presto.jdbc.internal.jackson.core.JsonToken;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.DeserializationContext;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.JsonDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.jsontype.TypeDeserializer;
/*    */ import com.facebook.presto.jdbc.internal.jackson.databind.type.CollectionType;
/*    */ import java.io.IOException;
/*    */ 
/*    */ abstract class GuavaMultisetDeserializer<T extends Multiset<Object>>
/*    */   extends GuavaCollectionDeserializer<T>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   GuavaMultisetDeserializer(CollectionType type, TypeDeserializer typeDeser, JsonDeserializer<?> deser)
/*    */   {
/* 20 */     super(type, typeDeser, deser);
/*    */   }
/*    */   
/*    */   protected abstract T createMultiset();
/*    */   
/*    */   protected T _deserializeContents(JsonParser jp, DeserializationContext ctxt)
/*    */     throws IOException, JsonProcessingException
/*    */   {
/* 28 */     JsonDeserializer<?> valueDes = this._valueDeserializer;
/*    */     
/* 30 */     TypeDeserializer typeDeser = this._typeDeserializerForValue;
/* 31 */     T set = createMultiset();
/*    */     JsonToken t;
/* 33 */     while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
/*    */       Object value;
/*    */       Object value;
/* 36 */       if (t == JsonToken.VALUE_NULL) {
/* 37 */         value = null; } else { Object value;
/* 38 */         if (typeDeser == null) {
/* 39 */           value = valueDes.deserialize(jp, ctxt);
/*    */         } else
/* 41 */           value = valueDes.deserializeWithType(jp, ctxt, typeDeser);
/*    */       }
/* 43 */       set.add(value);
/*    */     }
/* 45 */     return set;
/*    */   }
/*    */   
/*    */ 
/*    */   protected T _deserializeFromSingleValue(JsonParser jp, DeserializationContext ctxt)
/*    */     throws IOException, JsonProcessingException
/*    */   {
/* 52 */     JsonDeserializer<?> valueDes = this._valueDeserializer;
/* 53 */     TypeDeserializer typeDeser = this._typeDeserializerForValue;
/* 54 */     JsonToken t = jp.getCurrentToken();
/*    */     
/*    */     Object value;
/*    */     Object value;
/* 58 */     if (t == JsonToken.VALUE_NULL) {
/* 59 */       value = null; } else { Object value;
/* 60 */       if (typeDeser == null) {
/* 61 */         value = valueDes.deserialize(jp, ctxt);
/*    */       } else
/* 63 */         value = valueDes.deserializeWithType(jp, ctxt, typeDeser);
/*    */     }
/* 65 */     T result = createMultiset();
/* 66 */     result.add(value);
/* 67 */     return result;
/*    */   }
/*    */ }


/* Location:              E:\BaiduYunDownload\伊萨时期的一些代码\SparkEngine终极版\jars\YISAEngineBySpark2-4.0.6-SNAPSHOT-jar-with-dependencies.jar!\com\facebook\presto\jdbc\internal\jackson\datatype\guava\deser\GuavaMultisetDeserializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */